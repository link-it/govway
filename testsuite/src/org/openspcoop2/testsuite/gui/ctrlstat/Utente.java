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
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;


import org.openspcoop2.testsuite.core.CostantiTestSuite;
import org.openspcoop2.testsuite.gui.commons.SeleniumClient;
import org.openspcoop2.testsuite.gui.commons.TestUtil;
import org.openspcoop2.web.lib.users.dao.PermessiUtente;
import org.openspcoop2.web.lib.users.dao.User;

import com.thoughtworks.selenium.Selenium;

/**
 * Libreria per User
 *
 * @author Sandra Giangrandi <sandra@link.it>
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class Utente {

	public static void create(SeleniumClient seleniumClient,User utente,boolean validate) throws AssertionError{
		Selenium selenium_browser = seleniumClient.getSeleniumBrowser();
		String webAppContext = seleniumClient.getCtrlstatContext();
		
		String nomesu = utente.getLogin();
		String pwsu = utente.getPassword();
		PermessiUtente pu = utente.getPermessi();
		
		selenium_browser.open("/"+webAppContext+"/suAdd.do");
		selenium_browser.type("nomesu", nomesu);
		selenium_browser.type("pwsu", pwsu);
		selenium_browser.type("confpwsu", pwsu);
		if (pu.isServizi())
			selenium_browser.check("isServizi");
		else
			selenium_browser.uncheck("isServizi");
		if (pu.isSistema())
			selenium_browser.check("isSistema");
		else
			selenium_browser.uncheck("isSistema");
		if (pu.isUtenti())
			selenium_browser.check("isUtenti");
		else
			selenium_browser.uncheck("isUtenti");
		if (pu.isAuditing())
			selenium_browser.check("isAuditing");
		else
			selenium_browser.uncheck("isAuditing");
		if (pu.isCodeMessaggi())
			selenium_browser.check("isMessaggi");
		else
			selenium_browser.uncheck("isMessaggi");
		selenium_browser.click("//input[@value='"+CostantiTestSuite.BOTTONE_INVIA+"']");
		selenium_browser.waitForPageToLoad(TestUtil.getWaitPageToLoadTime());
		assertEquals(selenium_browser.getText("link="+(nomesu)), (nomesu));
	}
	
	public static void update(SeleniumClient seleniumClient,User utente,boolean validate) throws AssertionError{
		Selenium selenium_browser = seleniumClient.getSeleniumBrowser();
		String webAppContext = seleniumClient.getCtrlstatContext();
		
		String nomesu = utente.getLogin();
		String pwsu = utente.getPassword();
		PermessiUtente pu = utente.getPermessi();
		
		selenium_browser.open("/"+webAppContext+"/suList.do");
		selenium_browser.click("link="+(nomesu));
		selenium_browser.waitForPageToLoad(TestUtil.getWaitPageToLoadTime());
		selenium_browser.click("changepwd");
		selenium_browser.waitForPageToLoad(TestUtil.getWaitPageToLoadTime());
		selenium_browser.type("pwsu", pwsu);
		selenium_browser.type("confpwsu", pwsu);
		boolean oldIsServizi = selenium_browser.isChecked("isServizi");
		if (pu.isServizi())
			selenium_browser.check("isServizi");
		else
			selenium_browser.uncheck("isServizi");
		if (pu.isSistema())
			selenium_browser.check("isSistema");
		else
			selenium_browser.uncheck("isSistema");
		if (pu.isUtenti())
			selenium_browser.check("isUtenti");
		else
			selenium_browser.uncheck("isUtenti");
		if (pu.isAuditing())
			selenium_browser.check("isAuditing");
		else
			selenium_browser.uncheck("isAuditing");
		if (pu.isCodeMessaggi())
			selenium_browser.check("isMessaggi");
		else
			selenium_browser.uncheck("isMessaggi");
		selenium_browser.click("//input[@value='"+CostantiTestSuite.BOTTONE_INVIA+"']");
		selenium_browser.waitForPageToLoad(TestUtil.getWaitPageToLoadTime());
		if (oldIsServizi && !pu.isServizi()) {
			//viene la pagina che chiede di scegliere un utente a cui associare
			//gli oggetti di quello che stiamo cancellando
			selenium_browser.click("//input[@value='"+CostantiTestSuite.BOTTONE_INVIA+"']");
			selenium_browser.waitForPageToLoad(TestUtil.getWaitPageToLoadTime());
		}
		assertEquals(selenium_browser.getText("link="+(nomesu)), (nomesu));
	}
	
	public static void delete(SeleniumClient seleniumClient,User utente,int totUtenti,boolean validate) throws AssertionError{
		Selenium selenium_browser = seleniumClient.getSeleniumBrowser();
		String webAppContext = seleniumClient.getCtrlstatContext();
		
		PermessiUtente pu = utente.getPermessi();
		
		selenium_browser.open("/"+webAppContext+"/suList.do");		
		//selenium_browser.click("selectcheckbox");
		selenium_browser.click(""+utente.getId());
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
				
		if (pu.isServizi()) {
			//viene la pagina che chiede di scegliere un utente a cui associare
			//gli oggetti di quello che stiamo cancellando
			selenium_browser.click("//input[@value='"+CostantiTestSuite.BOTTONE_INVIA+"']");
			selenium_browser.waitForPageToLoad(TestUtil.getWaitPageToLoadTime());
		}
				
		//recuper stringa lista elenco
		String toParse = selenium_browser.getText("xpath=//span[@class='history']");
		
		int totLista = TestUtil.getTotEntryLista(toParse); //il totale degli utenti rimanenti che mi indica l'interfaccia
		
		//il totUtenti - 1 deve essere = a totLista ovvero gli utenti rimanenti
		assertEquals( (totUtenti-1)==totLista, true);
	}
	
	public static void deleteAll(SeleniumClient seleniumClient,boolean validate) throws AssertionError
	{
		assertNotNull(seleniumClient,"Il client seleniun e' null.");
		//Note: si effettua la cancellazione dell'intera lista.
		String webAppContext = seleniumClient.getCtrlstatContext();
		TestUtil.deleteAll(seleniumClient, webAppContext, "suList", validate);
	}
	
	public static void list(SeleniumClient seleniumClient,User utente,boolean validate) throws AssertionError{
		Selenium selenium_browser = seleniumClient.getSeleniumBrowser();
		String webAppContext = seleniumClient.getCtrlstatContext();
		
		String nomesu = utente.getLogin();
		
		selenium_browser.open("/"+webAppContext+"/suList.do");
		assertEquals(selenium_browser.getText("link="+(nomesu)), (nomesu));
	}
	
	public static void existsUser(SeleniumClient seleniumClient,User utente,boolean checkEsistenza,boolean validate) throws AssertionError{
		Selenium selenium_browser = seleniumClient.getSeleniumBrowser();
		String webAppContext = seleniumClient.getCtrlstatContext();
		
		String nomesu = utente.getLogin();
		PermessiUtente pu = utente.getPermessi();
		
		selenium_browser.open("/"+webAppContext+"/suList.do");
		
		if (checkEsistenza) {
			assertEquals(selenium_browser.getText("link="+(nomesu)), (nomesu));
			selenium_browser.open("/"+webAppContext+"/suChange.do?nomesu="+nomesu);
			if (pu.isServizi())
				assertTrue(selenium_browser.isChecked("isServizi"));
			else
				assertFalse(selenium_browser.isChecked("isServizi"));
			if (pu.isSistema())
				assertTrue(selenium_browser.isChecked("isSistema"));
			else
				assertFalse(selenium_browser.isChecked("isSistema"));
			if (pu.isUtenti())
				assertTrue(selenium_browser.isChecked("isUtenti"));
			else
				assertFalse(selenium_browser.isChecked("isUtenti"));
			if (pu.isAuditing())
				assertTrue(selenium_browser.isChecked("isAuditing"));
			else
				assertFalse(selenium_browser.isChecked("isAuditing"));
			if (pu.isCodeMessaggi())
				assertTrue(selenium_browser.isChecked("isMessaggi"));
			else
				assertFalse(selenium_browser.isChecked("isMessaggi"));
		} else
			assertFalse(selenium_browser.isTextPresent(nomesu));
	}
	
	public static void testUserAuth(SeleniumClient seleniumClient,User utente,boolean validate,boolean singlePdD) throws AssertionError{
		//Selenium selenium_browser = seleniumClient.getSeleniumBrowser();
		//String webAppContext = seleniumClient.getCtrlstatContext();
		
		PermessiUtente pu = utente.getPermessi();
		if (pu.isServizi()) {
			// Servlet autorizzate
			if(singlePdD){
				testServletAuth(seleniumClient, "pddListSinglePdD.do", true);
				testServletAuth(seleniumClient, "pddAddSinglePdD.do", true);
			}else{
				testServletAuth(seleniumClient, "pddList.do", true);
				testServletAuth(seleniumClient, "pddAdd.do", true);
			}
			testServletAuth(seleniumClient, "accordiList.do", true);
			testServletAuth(seleniumClient, "accordiAdd.do", true);
			testServletAuth(seleniumClient, "soggettiList.do", true);
			testServletAuth(seleniumClient, "soggettiAdd.do", true);
			testServletAuth(seleniumClient, "serviziList.do", true);
			testServletAuth(seleniumClient, "serviziAdd.do", true);
			testServletAuth(seleniumClient, "servizioApplicativoList.do", true);
			testServletAuth(seleniumClient, "servizioApplicativoAdd.do", true);
		} else {
			// Servlet non autorizzate
			if(singlePdD){
				testServletAuth(seleniumClient, "pddListSinglePdD.do", false);
				testServletAuth(seleniumClient, "pddAddSinglePdD.do", false);
			}else{
				testServletAuth(seleniumClient, "pddList.do", false);
				testServletAuth(seleniumClient, "pddAdd.do", false);
			}
			testServletAuth(seleniumClient, "accordiList.do", false);
			testServletAuth(seleniumClient, "accordiAdd.do", false);
			testServletAuth(seleniumClient, "soggettiList.do", false);
			testServletAuth(seleniumClient, "soggettiAdd.do", false);
			testServletAuth(seleniumClient, "serviziList.do", false);
			testServletAuth(seleniumClient, "serviziAdd.do", false);
			testServletAuth(seleniumClient, "servizioApplicativoList.do", false);
			testServletAuth(seleniumClient, "servizioApplicativoAdd.do", false);
		}
		if (pu.isSistema()) {
			// Servlet autorizzate
			testServletAuth(seleniumClient, "auditing.do", true);
		} else {
			// Servlet non autorizzate
			testServletAuth(seleniumClient, "auditing.do", false);
		}
		if (pu.isAuditing()) {
			// Servlet autorizzate
			testServletAuth(seleniumClient, "auditReport.do", true);
		} else {
			// Servlet non autorizzate
			testServletAuth(seleniumClient, "auditReport.do", false);
		}
		if (pu.isUtenti()) {
			// Servlet autorizzate
			testServletAuth(seleniumClient, "suList.do", true);
			testServletAuth(seleniumClient, "suAdd.do", true);
			// Servlet non autorizzate
			testServletAuth(seleniumClient, "changePw.do", false);
		} else {
			// Servlet autorizzate
			testServletAuth(seleniumClient, "changePw.do", true);
			// Servlet non autorizzate
			testServletAuth(seleniumClient, "suList.do", false);
			testServletAuth(seleniumClient, "suAdd.do", false);
		}
		if (pu.isCodeMessaggi()) {
			// Servlet autorizzate
			if(singlePdD)
				testServletAuth(seleniumClient, "monitorSinglePdD.do", true);
			else
				testServletAuth(seleniumClient, "monitor.do", true);
		} else {
			// Servlet non autorizzate
			if(singlePdD)
				testServletAuth(seleniumClient, "monitorSinglePdD.do", false);
			else
				testServletAuth(seleniumClient, "monitor.do", false);
		}
		if(singlePdD){
			if (pu.isDiagnostica()) {
				// Servlet autorizzate
				testServletAuth(seleniumClient, "diagnostica.do", true);
				testServletAuth(seleniumClient, "tracciamento.do", true);
			} else {
				// Servlet non autorizzate
				testServletAuth(seleniumClient, "diagnostica.do", false);
				testServletAuth(seleniumClient, "tracciamento.do", false);
			}
		}
	}
	
	static void testServletAuth(SeleniumClient seleniumClient,
			String servlet, boolean isAuthorized) throws AssertionError{
		Selenium selenium_browser = seleniumClient.getSeleniumBrowser();
		String webAppContext = seleniumClient.getCtrlstatContext();
		
		selenium_browser.open("/"+webAppContext+"/"+servlet);
		if (isAuthorized)
			assertFalse(selenium_browser.isTextPresent("Autorizzazione negata"));
		else
			assertTrue(selenium_browser.isTextPresent("Autorizzazione negata"));
	}
}


