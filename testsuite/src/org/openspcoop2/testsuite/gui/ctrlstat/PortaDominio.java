/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2014 Link.it srl (http://link.it). All rights reserved. 
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


package org.openspcoop2.testsuite.gui.ctrlstat;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import org.openspcoop2.testsuite.core.CostantiTestSuite;
import org.openspcoop2.testsuite.gui.commons.SeleniumClient;
import org.openspcoop2.testsuite.gui.commons.TestUtil;

import com.thoughtworks.selenium.Selenium;

/**
 * Libreria per Porta di Dominio
 *
 * @author Sandra Giangrandi <sandra@link.it>
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class PortaDominio {

	private static int totDeleted;
	
	public static void create(SeleniumClient seleniumClient,org.openspcoop2.core.registry.PortaDominio portadom, String tipo, String ip, String protocollo, int porta, String ipGestione, int portaGestione, boolean validate) throws AssertionError{
		Selenium selenium_browser = seleniumClient.getSeleniumBrowser();
		String webAppContext = seleniumClient.getCtrlstatContext();
		
		String nome = portadom.getNome();
		String descrizione = portadom.getDescrizione()!=null ? portadom.getDescrizione() : "";
		String subject = portadom.getSubject()!=null ? portadom.getSubject() : "";
		String implementazione = portadom.getImplementazione()!=null ? portadom.getImplementazione() : "";
		String clientAuth = portadom.getClientAuth()!=null ? portadom.getClientAuth().toString() : "";
		
		selenium_browser.open("/"+webAppContext+"/pddAdd.do");
		selenium_browser.type("nome", nome);
		selenium_browser.type("descrizione", descrizione);
		selenium_browser.select("tipo", "label="+tipo);
		selenium_browser.select("client_auth", "label="+clientAuth);
		try{
			selenium_browser.waitForPageToLoad(TestUtil.getWaitPageToLoadTime()); // Aspetto che carica subject
		}catch(Exception e){}
		if("abilitato".equals(clientAuth)){
			selenium_browser.type("subject", subject);
		}
		selenium_browser.type("implementazione", implementazione);
		selenium_browser.type("ip", ip);
		try{
			selenium_browser.waitForPageToLoad(TestUtil.getWaitPageToLoadTime()); // Aspetto che carica subject
		}catch(Exception e){}
		selenium_browser.select("protocollo", "label="+protocollo);
		selenium_browser.type("porta", ""+porta);
		selenium_browser.type("ip_gestione", ipGestione);
		try{
			selenium_browser.waitForPageToLoad(TestUtil.getWaitPageToLoadTime()); // Aspetto che carica subject
		}catch(Exception e){}
		selenium_browser.type("porta_gestione", ""+portaGestione);
		selenium_browser.click("//input[@value='"+CostantiTestSuite.BOTTONE_INVIA+"']");
		selenium_browser.waitForPageToLoad(TestUtil.getWaitPageToLoadTime());
		assertEquals(selenium_browser.getText("link="+(nome)), (nome));
	}
	
	public static void update(SeleniumClient seleniumClient,org.openspcoop2.core.registry.PortaDominio portadom, String tipo, String ip, String protocollo, int porta, String ipGestione, int portaGestione,boolean validate) throws AssertionError{
		Selenium selenium_browser = seleniumClient.getSeleniumBrowser();
		String webAppContext = seleniumClient.getCtrlstatContext();
		String nome = portadom.getNome();
		String descrizione = portadom.getDescrizione()!=null ? portadom.getDescrizione() : "";
		String subject = portadom.getSubject()!=null ? portadom.getSubject() : "";
		String implementazione = portadom.getImplementazione()!=null ? portadom.getImplementazione() : "";
		String clientAuth = portadom.getClientAuth()!=null ? portadom.getClientAuth().toString() : "";
		
		selenium_browser.open("/"+webAppContext+"/pddList.do");
		selenium_browser.click("link="+(nome));
		selenium_browser.waitForPageToLoad(TestUtil.getWaitPageToLoadTime());
		selenium_browser.type("descrizione", descrizione);
		selenium_browser.select("tipo", "label="+tipo);
		selenium_browser.select("client_auth", "label="+clientAuth);
		try{
			selenium_browser.waitForPageToLoad(TestUtil.getWaitPageToLoadTime()); // Aspetto che carica subject
		}catch(Exception e){}
		if("abilitato".equals(clientAuth)){
			selenium_browser.type("subject", subject);
		}
		selenium_browser.type("implementazione", implementazione);
		selenium_browser.type("ip", ip);
		try{
			selenium_browser.waitForPageToLoad(TestUtil.getWaitPageToLoadTime()); // Aspetto che carica subject
		}catch(Exception e){}
		selenium_browser.select("protocollo", "label="+protocollo);
		selenium_browser.type("porta", ""+porta);
		selenium_browser.type("ip_gestione", ipGestione);
		try{
			selenium_browser.waitForPageToLoad(TestUtil.getWaitPageToLoadTime()); // Aspetto che carica subject
		}catch(Exception e){}
		selenium_browser.type("porta_gestione", ""+portaGestione);
		selenium_browser.click("//input[@value='"+CostantiTestSuite.BOTTONE_INVIA+"']");
		selenium_browser.waitForPageToLoad(TestUtil.getWaitPageToLoadTime());
		assertEquals(selenium_browser.isTextPresent(tipo),true);
	}
	
	public static void delete(SeleniumClient seleniumClient,org.openspcoop2.core.registry.PortaDominio portadom,int totPorte,boolean validate) throws AssertionError{
		Selenium selenium_browser = seleniumClient.getSeleniumBrowser();
		String webAppContext = seleniumClient.getCtrlstatContext();
		selenium_browser.open("/"+webAppContext+"/pddList.do");		
		selenium_browser.click("selectcheckbox");
		selenium_browser.click("//input[@value='"+CostantiTestSuite.BOTTONE_REMOVE_SELECTED+"']");
		try{
			selenium_browser.waitForPageToLoad(500+"");
		}catch(Exception e){}
		try{
			selenium_browser.click("//button[1]"); // il confirm non e' presente se non ci sono elementi nella lista
		}catch(Exception e){}
		try{
			selenium_browser.waitForPageToLoad(TestUtil.getWaitPageToLoadTime());
		}catch(Exception e){}
				
		//recuper stringa lista elenco
		String toParse = selenium_browser.getText("xpath=//span[@class='history']");
		
		int totLista = TestUtil.getTotEntryLista(toParse); //il totale delle porte rimanenti che mi indica l'interfaccia
		
		incrDeleted();//ho cancellato una entry
		
		//il totPorte - getTotDeleted deve essere = a totLista ovvero i soggetti rimanenti
		assertEquals( (totPorte-getTotDeleted())==totLista, true);
	}
	
	public static void deleteAll(SeleniumClient seleniumClient,boolean validate) throws AssertionError
	{
		assertNotNull(seleniumClient,"Il client seleniun e' null.");
		//Note: si effettua la cancellazione dell'intera lista.
		String webAppContext = seleniumClient.getCtrlstatContext();
		TestUtil.deleteAll(seleniumClient, webAppContext, "pddList", validate);
	}
	
	public static void list(SeleniumClient seleniumClient,org.openspcoop2.core.registry.PortaDominio portadom,boolean validate) throws AssertionError{
		Selenium selenium_browser = seleniumClient.getSeleniumBrowser();
		String webAppContext = seleniumClient.getCtrlstatContext();
		String nome = portadom.getNome();
		
		selenium_browser.open("/"+webAppContext+"/pddList.do");
		assertEquals(selenium_browser.getText("link="+(nome)), (nome));
	}
	
	static synchronized void incrDeleted () {
		totDeleted++;
	}
	
	static synchronized int getTotDeleted(){
		return totDeleted;
	}
}


