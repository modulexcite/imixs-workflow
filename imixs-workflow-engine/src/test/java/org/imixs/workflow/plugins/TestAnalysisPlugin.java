package org.imixs.workflow.plugins;

import java.util.logging.Logger;

import javax.script.ScriptException;

import junit.framework.Assert;

import org.imixs.workflow.ItemCollection;
import org.imixs.workflow.Plugin;
import org.imixs.workflow.exceptions.PluginException;
import org.imixs.workflow.plugins.ResultPlugin;
import org.imixs.workflow.plugins.RulePlugin;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for AnalysisPlugin
 * 
 * @author rsoika
 */
public class TestAnalysisPlugin {
	AnalysisPlugin analysisPlugin = null;
	private static Logger logger = Logger.getLogger(TestAnalysisPlugin.class.getName());

	@Before
	public void setup() throws PluginException {
		analysisPlugin = new AnalysisPlugin();
		analysisPlugin.init(null);
	}


	
	
	
	
	/**
	 * Verify the start mechanism
	 * @throws PluginException
	 */
	@Test
	public void testBasicTest() throws PluginException {

		ItemCollection adocumentContext = new ItemCollection();
		adocumentContext.replaceItemValue("txtName", "Anna");
		
		// Activity Entity Dummy
		ItemCollection adocumentActivity = new ItemCollection();

		String sResult = "<item name='measurepoint' type='start'>M1</item>";
		logger.info("txtActivityResult=" + sResult);
		adocumentActivity.replaceItemValue("txtActivityResult", sResult);

		int result = analysisPlugin.run(adocumentContext, adocumentActivity);

		Assert.assertTrue(result == Plugin.PLUGIN_OK);

		Assert.assertTrue(adocumentContext.hasItem("datMeasurePointStart_M1"));
		
		logger.info("datMeasurePointStart_M1= " + adocumentContext.getItemValueDate("datMeasurePointStart_M1"));
		

	}




	/**
	 * Verify the start mechanism
	 * @throws PluginException
	 */
	@Test
	public void testWrongStartTest() throws PluginException {

		ItemCollection adocumentContext = new ItemCollection();
		adocumentContext.replaceItemValue("txtName", "Anna");
		
		// Activity Entity Dummy
		ItemCollection adocumentActivity = new ItemCollection();

		String sResult = "<item name='measurepoint' type='start'>M1</item>";
		logger.info("txtActivityResult=" + sResult);
		adocumentActivity.replaceItemValue("txtActivityResult", sResult);

		int result = analysisPlugin.run(adocumentContext, adocumentActivity);

		Assert.assertTrue(result == Plugin.PLUGIN_OK);

		Assert.assertTrue(adocumentContext.hasItem("datMeasurePointStart_M1"));
		
		logger.info("datMeasurePointStart_M1= " + adocumentContext.getItemValueDate("datMeasurePointStart_M1"));
		

		
		
		sResult = "<item name='measurepoint' type='start'>M1</item>";
		logger.info("txtActivityResult=" + sResult);
		adocumentActivity.replaceItemValue("txtActivityResult", sResult);

		 result = analysisPlugin.run(adocumentContext, adocumentActivity);

		Assert.assertTrue(result == Plugin.PLUGIN_OK);

		Assert.assertTrue(adocumentContext.hasItem("datMeasurePointStart_M1"));
	}


	
	
	
	


	/**
	 * Verify the start  numMeasurePoint_
	 * 
	 * @throws PluginException
	 */
	@Test
	public void testTotalTime() throws PluginException {

		ItemCollection adocumentContext = new ItemCollection();
		adocumentContext.replaceItemValue("txtName", "Anna");
		
		// Activity Entity Dummy
		ItemCollection adocumentActivity = new ItemCollection();

		String sResult = "<item name='measurepoint' type='start'>M1</item>";
		logger.info("txtActivityResult=" + sResult);
		adocumentActivity.replaceItemValue("txtActivityResult", sResult);

		int result = analysisPlugin.run(adocumentContext, adocumentActivity);

		Assert.assertTrue(result == Plugin.PLUGIN_OK);

		Assert.assertTrue(adocumentContext.hasItem("datMeasurePointStart_M1"));
		
		logger.info("datMeasurePointStart_M1= " + adocumentContext.getItemValueDate("datMeasurePointStart_M1"));
		

		
		
		try {
		    Thread.sleep(1000);                 //1000 milliseconds is one second.
		} catch(InterruptedException ex) {
		    Thread.currentThread().interrupt();
		}
		
		
		sResult = "<item name='measurepoint' type='stop'>M1</item>";
		logger.info("txtActivityResult=" + sResult);
		adocumentActivity.replaceItemValue("txtActivityResult", sResult);

		 result = analysisPlugin.run(adocumentContext, adocumentActivity);

		Assert.assertTrue(result == Plugin.PLUGIN_OK);

		Assert.assertTrue(adocumentContext.hasItem("datMeasurePointStart_M1"));
		
		
		int time=adocumentContext.getItemValueInteger("numMeasurePoint_M1");
		
		System.out.println("Time=" + time);
		Assert.assertTrue(time>0);
	}


	
	


}
