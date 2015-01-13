/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2015 Link.it srl (http://link.it). 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */


package org.openspcoop2.testsuite.gui.commons;

import org.openspcoop2.testsuite.gui.constant.TestProperties;

import java.util.Enumeration;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.Reporter;

import com.thoughtworks.selenium.DefaultSelenium;
import com.thoughtworks.selenium.Selenium;

/**
 * Oggetto che si occupa dell interazione con il server Selenium
 * 
 * @author Stefano Corallo <corallo@link.it>
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SeleniumClient {

	private Logger log;
	private String xmlPathRegistro;
	private String xmlPathConfig;
	private Selenium selenium_browser;
	private String regservContext;
	private String pddContext;
	private String ctrlstatContext;
	private String geContext;
	
	public SeleniumClient() {
		init();
	}
	
	private  void init(){
		
		Properties prop=TestUtil.loadConfigProperties(TestProperties._config_file_name.toString());
		
		Assert.assertNotNull(prop,"Proprieta' di configurazione non caricate.");
		
		this.log=TestUtil.getLog();
		
		//file di dati
		this.xmlPathRegistro = prop.getProperty(TestProperties.xml_registro.toString());
		
		Assert.assertNotNull(this.xmlPathRegistro,"File dati XML del Registro non valido.");
		
		this.xmlPathConfig = prop.getProperty(TestProperties.xml_config.toString());
		
		Assert.assertNotNull(this.xmlPathConfig,"File dati XML Porta di Dominio non valido");
		
		Reporter.log("File di Dati:");
		Reporter.log("XML Registro:"+this.xmlPathRegistro);
		Reporter.log("XML Config:"+this.xmlPathConfig);
		
		/**
		 * WebApp context
		 */
		this.regservContext = prop.getProperty(TestProperties.regserv_context.toString());
		Assert.assertNotNull(this.regservContext,"WebApp context per GUI Registro Servizi non valida.");
		this.pddContext = prop.getProperty(TestProperties.pdd_context.toString());
		Assert.assertNotNull(this.pddContext,"WebApp context per GUI Porta di Dominio non valida.");
		this.ctrlstatContext = prop.getProperty(TestProperties.ctrlstat_context.toString());
		Assert.assertNotNull(this.ctrlstatContext,"WebApp context per GUI PddConsole non valida.");
		this.geContext = prop.getProperty(TestProperties.ge_context.toString());
		Assert.assertNotNull(this.geContext,"WebApp context per GUI Gestore Eventi non valida.");
		
		Reporter.log("GUI Registro WebApp Context:"+this.regservContext);
		Reporter.log("GUI Pdd WebApp Context:"+this.pddContext);
		Reporter.log("GUI PddConsole WebApp Context:"+this.ctrlstatContext);
		Reporter.log("GUI Gestore Eventi WebApp Context:"+this.geContext);
		
		Enumeration<Object> keys= prop.keys();
		Reporter.log("Lette ("+prop.size()+") Properties:");
		while (keys.hasMoreElements()) {
			String key = (String) keys.nextElement();
			Reporter.log(key+"="+prop.getProperty(key));
		}
		
		//remote host
		String remote_core_host = prop.getProperty(TestProperties.remote_core_host.toString());
		Assert.assertNotNull(remote_core_host,"Host remoto non corretto.");
		if(remote_core_host.startsWith("http://"))remote_core_host=remote_core_host.substring("http://".length());
		
		//remote port
		int porta = 0;
		try{
			porta = Integer.parseInt(prop.getProperty(TestProperties.remote_core_port.toString()));					
		}catch (NumberFormatException e) {
			Reporter.log("Selenium Remote Server Port non impostata correttamente");
			Reporter.log("Setto Porta di default ["+TestProperties._default_remote_core_port.toString()+"]");
			porta = Integer.parseInt(TestProperties._default_remote_core_port.toString());
		}
		
		//browser
		String browser = prop.getProperty(TestProperties.remote_core_browser.toString());
		if(browser==null) browser=TestProperties._default_browser.toString();
		
		//openspcoop
		String openspcoop_http_endpoint = prop.getProperty(TestProperties.openspcoop_http_endpoint.toString());
		
		Assert.assertNotNull(openspcoop_http_endpoint,"OpenSPCoop HTTP EndPoint non valido.");
		
		int openspcoop_http_port = 80;
		if(prop.getProperty(TestProperties.openspcoop_http_port.toString())!=null){
			try{
				openspcoop_http_port = Integer.parseInt(TestProperties.openspcoop_http_port.toString());
			}catch (NumberFormatException e) {
				Reporter.log(TestProperties.openspcoop_http_port.name()+" non valida setto porta di default "+TestProperties._default_openspcoop_http_port.toString());
				openspcoop_http_port=Integer.parseInt(TestProperties._default_openspcoop_http_port.toString());
			}
		}
				
		//Inizializzazione Selenium
		Reporter.log("Inizializzazione Selenium Remote Server ["+remote_core_host+":"+porta+"]");
		
        this.selenium_browser = new DefaultSelenium(remote_core_host,porta,browser,(openspcoop_http_endpoint+":"+openspcoop_http_port));
        this.selenium_browser.start();
	}
	
	public Selenium getSeleniumBrowser(){
		return this.selenium_browser;
	}

	public Logger getLog() {
		return this.log;
	}

	public String getXmlPathRegistro() {
		return this.xmlPathRegistro;
	}

	public void setXmlPathRegistro(String xmlPathRegistro) {
		this.xmlPathRegistro = xmlPathRegistro;
	}

	public String getXmlPathConfig() {
		return this.xmlPathConfig;
	}

	public void setXmlPathConfig(String xmlPathConfig) {
		this.xmlPathConfig = xmlPathConfig;
	}

	public String getRegservContext() {
		return this.regservContext;
	}

	public void setRegservContext(String regservContext) {
		this.regservContext = regservContext;
	}

	public String getPddContext() {
		return this.pddContext;
	}

	public void setPddContext(String pddContext) {
		this.pddContext = pddContext;
	}
	
	public String getCtrlstatContext() {
		return this.ctrlstatContext;
	}

	public void setCtrlstatContext(String ctrlstatContext) {
		this.ctrlstatContext = ctrlstatContext;
	}
	
	public String getGEContext() {
		return this.geContext;
	}

	public void setGEContext(String geContext) {
		this.geContext = geContext;
	}
}


