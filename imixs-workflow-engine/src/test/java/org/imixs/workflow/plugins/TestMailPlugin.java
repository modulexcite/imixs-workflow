package org.imixs.workflow.plugins;

import java.util.logging.Logger;

import junit.framework.Assert;

import org.imixs.workflow.ItemCollection;
import org.imixs.workflow.plugins.jee.MailPlugin;
import org.junit.Before;
import org.junit.Test;

/**
 * Test the MailPlugin getter methods
 * 
 * @author rsoika
 * 
 */
public class TestMailPlugin  {

	MailPlugin mailPlugin=null;
	private final static Logger logger = Logger.getLogger(TestMailPlugin.class
			.getName());

	@Before
	public void setup()  {
		mailPlugin=new MailPlugin();
	}

	/**
	 * Test getBody, getSubject
	 * replace dynamic values
	 * 
	 */
	@Test
	public void testItemCollection() {
		logger.info("[TestMailPlugin] getBody...");

		// prepare data
		ItemCollection documentContext = new ItemCollection();
	
		ItemCollection documentActivity = new ItemCollection();
		
		
		
		documentActivity.replaceItemValue("rtfMailBody","Hello <itemValue>namcreator</itemValue>!");
		documentContext.replaceItemValue("namcreator","Anna");

		String sBody=mailPlugin.getBody(documentContext, documentActivity);
		
		Assert.assertEquals("Hello Anna!", sBody);
	}

}
