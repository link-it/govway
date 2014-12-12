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
import org.testng.Assert;

import com.thoughtworks.selenium.Selenium;
import com.thoughtworks.selenium.SeleniumException;

/**
 * Libreria per Porta di Dominio
 *
 * @author Sandra Giangrandi <sandra@link.it>
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class PortaDominioSinglePdD {

	private static int totDeleted;

	public static void create(SeleniumClient seleniumClient,org.openspcoop2.core.registry.PortaDominio portadom, boolean validate) throws AssertionError{
		Selenium selenium_browser = seleniumClient.getSeleniumBrowser();
		String webAppContext = seleniumClient.getCtrlstatContext();

		String nome = portadom.getNome();
		String descrizione = portadom.getDescrizione()!=null ? portadom.getDescrizione() : "";
		String subject = portadom.getSubject()!=null ? portadom.getSubject() : "";
		String implementazione = portadom.getImplementazione()!=null ? portadom.getImplementazione() : "";
		String clientAuth = portadom.getClientAuth()!=null ? portadom.getClientAuth().toString() : "";

		selenium_browser.open("/"+webAppContext+"/pddAddSinglePdD.do");
		selenium_browser.type("nome", nome);
		selenium_browser.type("descrizione", descrizione);
		selenium_browser.select("client_auth", "label="+clientAuth);
		try{
			selenium_browser.waitForPageToLoad(TestUtil.getWaitPageToLoadTime()); // Aspetto che carica subject
		}catch(Exception e){}
		if("abilitato".equals(clientAuth)){
			selenium_browser.type("subject", subject);
		}
		selenium_browser.type("implementazione", implementazione);
		selenium_browser.click("//input[@value='"+CostantiTestSuite.BOTTONE_INVIA+"']");
		selenium_browser.waitForPageToLoad(TestUtil.getWaitPageToLoadTime());
		assertEquals(selenium_browser.getText("link="+(nome)), (nome));
	}

	public static void update(SeleniumClient seleniumClient,org.openspcoop2.core.registry.PortaDominio portadom,boolean validate) throws AssertionError{
		Selenium selenium_browser = seleniumClient.getSeleniumBrowser();
		String webAppContext = seleniumClient.getCtrlstatContext();
		String nome = portadom.getNome();
		String descrizione = portadom.getDescrizione()!=null ? portadom.getDescrizione() : "";
		String subject = portadom.getSubject()!=null ? portadom.getSubject() : "";
		String implementazione = portadom.getImplementazione()!=null ? portadom.getImplementazione() : "";
		String clientAuth = portadom.getClientAuth()!=null ? portadom.getClientAuth().toString() : "";

		selenium_browser.open("/"+webAppContext+"/pddListSinglePdD.do");
		selenium_browser.click("link="+(nome));
		selenium_browser.waitForPageToLoad(TestUtil.getWaitPageToLoadTime());
		selenium_browser.type("descrizione", descrizione);
		selenium_browser.select("client_auth", "label="+clientAuth);
		try{
			selenium_browser.waitForPageToLoad(TestUtil.getWaitPageToLoadTime()); // Aspetto che carica subject
		}catch(Exception e){}
		if("abilitato".equals(clientAuth)){
			selenium_browser.type("subject", subject);
		}
		selenium_browser.type("implementazione", implementazione);
		selenium_browser.click("//input[@value='"+CostantiTestSuite.BOTTONE_INVIA+"']");
		selenium_browser.waitForPageToLoad(TestUtil.getWaitPageToLoadTime());
		assertEquals(selenium_browser.getText("link="+(nome)), (nome));
	}

	public static void delete(SeleniumClient seleniumClient,org.openspcoop2.core.registry.PortaDominio portadom,int totPorte,boolean validate) throws AssertionError{
		Selenium selenium_browser = seleniumClient.getSeleniumBrowser();
		String webAppContext = seleniumClient.getCtrlstatContext();
		selenium_browser.open("/"+webAppContext+"/pddListSinglePdD.do");		
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

	public static void deleteAll(SeleniumClient seleniumClient,boolean validate, boolean operativo) throws AssertionError
	{
		assertNotNull(seleniumClient,"Il client seleniun e' null.");
		//Note: si effettua la cancellazione dell'intera lista.
		String webAppContext = seleniumClient.getCtrlstatContext();
		
		Assert.assertNotNull(seleniumClient,"Il client seleniun e' null.");
			
		//Note: si effettua la cancellazione dell'intera lista.
		Selenium selenium = seleniumClient.getSeleniumBrowser();
		selenium.open("/"+webAppContext+"/pddListSinglePdD.do");
		selenium.click("//input[@value='"+CostantiTestSuite.BOTTONE_SELECT_ALL+"']");
		selenium.click("//input[@value='"+CostantiTestSuite.BOTTONE_REMOVE_SELECTED+"']");
		try{
			selenium.waitForPageToLoad(500+"");
		}catch(Exception e){}
		try{
			selenium.click("//button[1]"); // il confirm non e' presente se non ci sono elementi nella lista
		}catch(Exception e){}
		try{
			selenium.waitForPageToLoad(TestUtil.getWaitPageToLoadTime());
		}catch (SeleniumException e) {
			// ignore
		}
		//recuper stringa lista elenco
		String toParse = selenium.getText("xpath=//span[@class='history']");
		
		int totLista = TestUtil.getTotEntryLista(toParse); 
		
		if(operativo){
			// Una PdD Operativa non puo' essere eliminata
			Assert.assertEquals(totLista, 1);
		}
		else{
			Assert.assertEquals(totLista, 0);
		}
		
	}

	public static void list(SeleniumClient seleniumClient,org.openspcoop2.core.registry.PortaDominio portadom,boolean validate) throws AssertionError{
		Selenium selenium_browser = seleniumClient.getSeleniumBrowser();
		String webAppContext = seleniumClient.getCtrlstatContext();
		String nome = portadom.getNome();

		selenium_browser.open("/"+webAppContext+"/pddListSinglePdD.do");
		assertEquals(selenium_browser.getText("link="+(nome)), (nome));
	}

	static synchronized void incrDeleted () {
		totDeleted++;
	}

	static synchronized int getTotDeleted(){
		return totDeleted;
	}
}


