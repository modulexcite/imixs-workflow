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
package org.imixs.workflow.xml;

import java.util.List;
import java.util.Vector;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * An EntityTabe represents a list of XMLItemCollections to be used by JAXB api.
 * Each XMLItemCollection in the list represents the same properties. So the
 * EntityTable can be used to generate a table repseentation of
 * XMLItemCollections
 * 
 * @author rsoika
 * @version 0.0.1
 */
@XmlRootElement(name = "collection")
public class EntityTable implements java.io.Serializable {
	private XMLItemCollection[] entity;
	private List<String> attributeList;

	public EntityTable() {
		setEntity(new XMLItemCollection[] {});
		setAttributeList(new Vector<String>());
	}

	public XMLItemCollection[] getEntity() {
		return entity;
	}

	public void setEntity(XMLItemCollection[] entity) {
		this.entity = entity;
	}

	public List<String> getAttributeList() {
		return attributeList;
	}

	public void setAttributeList(List<String> attributeList) {
		this.attributeList = attributeList;
	}
	
	

}
