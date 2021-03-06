/*******************************************************************************
 *  Imixs Workflow 
 *  Copyright (C) 2001, 2011 Imixs Software Solutions GmbH,  
 *  http://www.imixs.com
 *  
 *  This program is free software; you can redistribute it and/or 
 *  modify it under the terms of the GNU General Public License 
 *  as published by the Free Software Foundation; either version 2 
 *  of the License, or (at your option) any later version.
 *  
 *  This program is distributed in the hope that it will be useful, 
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of 
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU 
 *  General Public License for more details.
 *  
 *  You can receive a copy of the GNU General Public
 *  License at http://www.gnu.org/licenses/gpl.html
 *  
 *  Project: 
 *  	http://www.imixs.org
 *  	http://java.net/projects/imixs-workflow
 *  
 *  Contributors:  
 *  	Imixs Software Solutions GmbH - initial API and implementation
 *  	Ralph Soika - Software Developer
 *******************************************************************************/

package org.imixs.workflow.plugins.jee;

import java.util.Collection;

import javax.ejb.EJBTransactionRolledbackException;

import org.imixs.workflow.ItemCollection;
import org.imixs.workflow.Plugin;
import org.imixs.workflow.WorkflowContext;
import org.imixs.workflow.exceptions.AccessDeniedException;
import org.imixs.workflow.exceptions.PluginException;
import org.imixs.workflow.exceptions.ProcessingErrorException;
import org.imixs.workflow.jee.ejb.EntityService;
import org.imixs.workflow.jee.ejb.WorkflowService;

/**
 * This plugin handles the creation and management of versions from an existing
 * workitem. inside the Imix JEE Workflow. The creation or modificatin of a
 * version is defined by the workflowmodel. The plugin can generate new versions
 * (e.g. creating a version of a master version) and also converting a existing
 * version into a master version.
 * <p>
 * The Plugin depends on the org.imixs.workflow.jee.ejb.WorkflowManager. So the
 * Plugin can not be used in other implementations.
 * <p>
 * The creation and management of a new version is defined by the workflow model
 * (See Imixs Modeler) There are currently two modes supported (provided by the
 * activity property 'keyVersion')
 * <p>
 * mode=1 indicates that the plugin should create a new version of the current
 * workitem. The two workitems are identically except the attributes $unqiueid
 * and $workitemidRef. The attribute workitemidRef points to the $uniqueid form
 * the source workitem. So the availability of this property indicates that the
 * workitem is a version of source workitem with this $uniqueid. The source
 * workitem has typically no $workitemidRef attribute. The Source Workitem is
 * also named Master Version. After the new version is created the plugin
 * processes the version with the activity provided by the model
 * (numVersionActivityID) if provided by the model.
 * <p>
 * 2=indicates that the plugin should convert a existing version into a Master
 * Version. This means that the $workitemIDRef will be nulled. An existing
 * Master Version will be processed by the activity provided by the model
 * (numVersionActivityID). Also the $workitemidRef will be set to the current
 * $workitemID.
 * <p>
 * If an error occured during the workflow process this plugin will throw a new
 * ejbexception in the close() method to cancel the current transaction. So no
 * changes will be saved by the ejb container
 * 
 * @see org.imixs.workflow.jee.ejb.WorkflowManager
 * @author Ralph Soika
 * @version 1.0
 */

public class VersionPlugin extends AbstractPlugin {

	public  static final String INVALID_CONTEXT = "INVALID_CONTEXT";
	public static final String INVALID_WORKITEM = "INVALID_WORKITEM";
	private EntityService entityService = null;
	private WorkflowService workflowService = null;
	private String versionMode = "";
	private int versionActivityID = -1;
	private ItemCollection version = null;

	/**
	 * the init method throws an exception if the plugin is not run in a
	 * instance of org.imixs.workflow.jee.ejb.WorkflowManager. This is necessary
	 * as the plugin needs an instance of the EntityService.
	 */
	public void init(WorkflowContext actx) throws PluginException {
		super.init(actx);

		// check for an instance of WorkflowService
		if (actx instanceof WorkflowService) {
			// yes we are running in a WorkflowService EJB
			workflowService = (WorkflowService) actx;
			// get latest model version....
			entityService = workflowService.getEntityService();
		}

		if (workflowService == null)
			throw new PluginException(VersionPlugin.class.getSimpleName(),INVALID_CONTEXT,
					"VersionPlugin unable to access WorkflowSerive");

	}

	public EntityService getEntityService() {
		return entityService;
	}

	public WorkflowService getWorkflowService() {
		return workflowService;
	}

	public ItemCollection getVersion() {
		return version;
	}

	public void setVersion(ItemCollection version) {
		this.version = version;
	}

	/**
	 * creates an version depending to the version mode and the version activity
	 * ID provided by the workflow model.
	 * 
	 * @throws InvalidItemValueException
	 * 
	 */
	public int run(ItemCollection adocumentContext,
			ItemCollection adocumentActivity) throws PluginException {

		// determine mode and ActivityID to manage version
		versionMode = adocumentActivity.getItemValueString("keyVersion");
		versionActivityID = adocumentActivity
				.getItemValueInteger("numVersionActivityID");
		try {
			// lookup ejbs ?
			if ("1".equals(versionMode) || "2".equals(versionMode)) {

				// handle different version modes
				// 1 = create a new Version from current workitem
				if ("1".equals(versionMode)) {

					// copy workitem

					version = createVersion(adocumentContext);

					if (this.ctx.getLogLevel() > 0)
						System.out
								.println("[VersionPlugin] new version created");
					// check if workitem should be processed
					if (versionActivityID > 0) {
						version.replaceItemValue("$ActivityID",
								versionActivityID);
						version = workflowService.processWorkItem(version);
					} else {
						// no processing - simply save workitem
						version = entityService.save(version);
					}
					return Plugin.PLUGIN_OK;

				}

				// convert to Master Version
				if ("2".equals(adocumentActivity
						.getItemValueString("keyVersion"))) {

					/*
					 * this code iterates over all existing workitems with the
					 * same $workitemid and fixes lost parent workitemRefIDs.
					 */
					String sworkitemID = adocumentContext
							.getItemValueString("$WorkItemID");

					// get current master version
					String query = " SELECT workitem FROM Entity AS workitem "
							+ " JOIN workitem.textItems AS t1"
							+ " WHERE t1.itemName = '$workitemid'"
							+ " AND t1.itemValue ='" + sworkitemID + "'";

					Collection<ItemCollection> col = entityService
							.findAllEntities(query, 0, -1);
					// now search master version...
					for (ItemCollection aVersion : col) {
						String sWorkitemRef = aVersion
								.getItemValueString("$workitemIDRef");
						if ("".equals(sWorkitemRef)) {
							// Master version found!
							// convert now this workitem into a Version from the
							// current workitem
							String id = adocumentContext
									.getItemValueString("$uniqueID");
							aVersion.replaceItemValue("$WorkItemIDRef", id);
							// process version?
							// check if worktiem should be processed
							if (versionActivityID > 0) {
								aVersion.replaceItemValue("$ActivityID",
										versionActivityID);
								aVersion = workflowService
										.processWorkItem(aVersion);
							} else {
								// no processing - simply save workitem
								aVersion = entityService.save(aVersion);
							}
							version = aVersion;
						}
					}
					// now remove workitemIDRef from current version
					adocumentContext.removeItem("$WorkItemIDRef");
				}
			}

		} catch (AccessDeniedException e) {
			throw new PluginException(e.getErrorContext(),e.getErrorCode(), e.getMessage(), e);
		} catch (ProcessingErrorException e) {
			throw new PluginException(e.getErrorContext(),e.getErrorCode(),e.getMessage(), e);
		}
		return Plugin.PLUGIN_OK;
	}

	public void close(int status) throws PluginException {
		// if an error has occurred during processing take back new created
		// versions
		if (status == Plugin.PLUGIN_ERROR) {
			// throw a ejb exception to cancel a running transaction
			// this will avoid changes back into the database
			throw new EJBTransactionRolledbackException();
		}
	}

	/**
	 * This method creates a new instance of a exiting workitem. The method did
	 * not save the workitem!. The method can be subclassed to modify the new
	 * created version.
	 * 
	 * The new property $WorkitemIDRef will be added which points to the
	 * $uniqueID of the sourceWorkitem.
	 * 
	 * @param sourceItemCollection
	 *            the ItemCollection which should be versioned
	 * @return new version of the source ItemCollection
	 * 
	 * @throws PluginException
	 * @throws Exception
	 */
	public ItemCollection createVersion(ItemCollection sourceItemCollection)
			throws PluginException {
		ItemCollection itemColNewVersion = new ItemCollection();

		itemColNewVersion.replaceAllItems(sourceItemCollection.getAllItems());

		String id = sourceItemCollection.getItemValueString("$uniqueid");
		if ("".equals(id))
			throw new PluginException(VersionPlugin.class.getSimpleName(),INVALID_WORKITEM,
					"Error - unable to create a version from a new workitem!");
		// remove $Uniqueid to force the generation of a new Entity Instance.
		itemColNewVersion.getAllItems().remove("$uniqueid");

		// update $WorkItemIDRef to current worktiemID
		itemColNewVersion.replaceItemValue("$WorkItemIDRef", id);
		
		return itemColNewVersion;

	}

}