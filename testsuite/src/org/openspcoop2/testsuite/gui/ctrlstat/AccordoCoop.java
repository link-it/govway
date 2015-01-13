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


package org.openspcoop2.testsuite.gui.ctrlstat;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.util.List;
import java.util.Vector;

import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.registry.AccordoCooperazione;
import org.openspcoop2.core.registry.AccordoCooperazionePartecipanti;
import org.openspcoop2.core.registry.IdSoggetto;
import org.openspcoop2.core.registry.Documento;
import org.openspcoop2.core.registry.driver.BeanUtilities;
import org.openspcoop2.core.registry.driver.IDAccordoCooperazioneFactory;
import org.openspcoop2.testsuite.core.CostantiTestSuite;
import org.openspcoop2.testsuite.gui.commons.SeleniumClient;
import org.openspcoop2.testsuite.gui.commons.TestUtil;
import org.testng.Reporter;

import com.thoughtworks.selenium.Selenium;

/**
 *
 * Libreria per Accordo Cooperazione
 * @author Sandra Giangrandi <sandra@link.it>
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class AccordoCoop {

	private static int totDeleted;
	
	public static void create(SeleniumClient seleniumClient,AccordoCooperazione accordo,boolean validate) throws AssertionError,Exception{
		Selenium selenium_browser = seleniumClient.getSeleniumBrowser();
		
		assertNotNull(accordo.getNome());
		
		String descr = accordo.getDescrizione()!=null ? accordo.getDescrizione() : "";
		String webAppContext = seleniumClient.getCtrlstatContext();
		
		selenium_browser.open("/"+webAppContext+"/accordiCooperazioneAdd.do");
		selenium_browser.type("nome", accordo.getNome());
		selenium_browser.type("descr", descr);
		
		if(IDAccordoCooperazioneFactory.getInstance().versioneNonDefinita(accordo.getVersione())==false){
			selenium_browser.type("versione", accordo.getVersione());
		}else{
			selenium_browser.type("versione", "0");
		}
		
		if(BeanUtilities.getSoggettoReferenteID(accordo.getSoggettoReferente())!=null){
			selenium_browser.select("referente", "label="+BeanUtilities.getSoggettoReferenteID(accordo.getSoggettoReferente()).toString());
		}
		
		//if(accordo.getStatoPackage()!=null)
		//	selenium_browser.select("stato", "label="+accordo.getStatoPackage());
		// ADD: e' solo un text
		
		selenium_browser.click("//input[@value='"+CostantiTestSuite.BOTTONE_INVIA+"']");
		selenium_browser.waitForPageToLoad(TestUtil.getWaitPageToLoadTime());

		assertEquals(IDAccordoCooperazioneFactory.getInstance().getUriFromAccordo(accordo), selenium_browser.getText("link="+IDAccordoCooperazioneFactory.getInstance().getUriFromAccordo(accordo)));
		
		int idAccordo=getIdAccordo(selenium_browser,webAppContext,IDAccordoCooperazioneFactory.getInstance().getUriFromAccordo(accordo));
		
		// ElencoPartecipanti
		AccordoCooperazionePartecipanti partecipanti = accordo.getElencoPartecipanti();
		List<IdSoggetto> listaPartecipanti = null;
		if(partecipanti!=null){
			listaPartecipanti = partecipanti.getSoggettoPartecipanteList();
		}
		//partecipanti.getSoggettoList();
		TestUtil.CRUDElencoPartecipanti(seleniumClient, validate, CostantiDB.CREATE, webAppContext, listaPartecipanti, idAccordo);
		
		// Allegati
		Vector<Documento> documenti = new Vector<Documento>();
		for (int i = 0; i < accordo.sizeAllegatoList(); i++) {
			Documento doc = accordo.getAllegato(i);
			documenti.add(doc);
		}
		for (int i = 0; i < accordo.sizeSpecificaSemiformaleList(); i++) {
			Documento doc = accordo.getSpecificaSemiformale(i);
			documenti.add(doc);
		}
		TestUtil.CRUDDocumenti(seleniumClient, validate, CostantiDB.CREATE, webAppContext, "accordiCoopAllegatiList.do", documenti, idAccordo, false, false);
	}
	
	private static int getIdAccordo(Selenium selenium,String webAppContext,String uriAccordo) throws AssertionError{
		selenium.open("/"+webAppContext+"/accordiCooperazioneList.do");
		String url = selenium.getAttribute("link="+uriAccordo+"@href");
		String idA = url.substring("accordiCooperazioneChange.do?id=".length(),url.indexOf("&"));
		try{
			return Integer.parseInt(idA);
		}catch (Exception e) {
			Reporter.log("Errore durante il recuperare dell'id Accordo Cooperazione dalla URL: "+url);
			throw new AssertionError("Errore durante il recuperare dell'id Accordo Cooperazione dalla URL: "+url);
		}
	}
	
	public static void update(SeleniumClient seleniumClient,AccordoCooperazione accordo,AccordoCooperazione oldAccordo,boolean validate) throws AssertionError,Exception{
		Selenium selenium_browser = seleniumClient.getSeleniumBrowser();
		String descr = accordo.getDescrizione()!=null ? accordo.getDescrizione() : "";
		String stato = accordo.getStatoPackage()!=null ? accordo.getStatoPackage() : "bozza";
		String webAppContext = seleniumClient.getCtrlstatContext();
	
		selenium_browser.open("/"+webAppContext+"/accordiCooperazioneList.do");
		selenium_browser.click("link="+IDAccordoCooperazioneFactory.getInstance().getUriFromAccordo(oldAccordo));
		selenium_browser.waitForPageToLoad(TestUtil.getWaitPageToLoadTime());
		
		selenium_browser.type("nome", accordo.getNome());
		selenium_browser.type("descr", descr);
		
		if(IDAccordoCooperazioneFactory.getInstance().versioneNonDefinita(accordo.getVersione())==false){
			selenium_browser.type("versione", accordo.getVersione());
		}else{
			selenium_browser.type("versione", "0");
		}
		
		if(BeanUtilities.getSoggettoReferenteID(accordo.getSoggettoReferente())!=null){
			selenium_browser.select("referente", "label="+BeanUtilities.getSoggettoReferenteID(accordo.getSoggettoReferente()).toString());
		}else{
			selenium_browser.select("referente", "label=-");
		}
		
		
		selenium_browser.select("stato", "label="+stato);
		
		selenium_browser.click("//input[@value='"+CostantiTestSuite.BOTTONE_INVIA+"']");
		selenium_browser.waitForPageToLoad(TestUtil.getWaitPageToLoadTime());
		
		assertEquals(IDAccordoCooperazioneFactory.getInstance().getUriFromAccordo(accordo), selenium_browser.getText("link="+IDAccordoCooperazioneFactory.getInstance().getUriFromAccordo(accordo)));
		
		int idAccordo=getIdAccordo(selenium_browser,webAppContext,IDAccordoCooperazioneFactory.getInstance().getUriFromAccordo(accordo));
		
		// ElencoPartecipanti
		AccordoCooperazionePartecipanti partecipanti = accordo.getElencoPartecipanti();
		List<IdSoggetto> listaPartecipanti = null;
		if(partecipanti!=null){
			listaPartecipanti = partecipanti.getSoggettoPartecipanteList();
		}
		TestUtil.CRUDElencoPartecipanti(seleniumClient, validate, CostantiDB.UPDATE, webAppContext, listaPartecipanti, idAccordo);
		
		// Allegati
		Vector<Documento> documenti = new Vector<Documento>();
		for (int i = 0; i < accordo.sizeAllegatoList(); i++) {
			Documento doc = accordo.getAllegato(i);
			documenti.add(doc);
		}
		for (int i = 0; i < accordo.sizeSpecificaSemiformaleList(); i++) {
			Documento doc = accordo.getSpecificaSemiformale(i);
			documenti.add(doc);
		}
		TestUtil.CRUDDocumenti(seleniumClient, validate, CostantiDB.UPDATE, webAppContext, "accordiCoopAllegatiList.do", documenti, idAccordo, false, false);
		
	}

	public static void delete(SeleniumClient seleniumClient,AccordoCooperazione accordo,int totAccordi,boolean validate) throws AssertionError{
		Selenium selenium_browser = seleniumClient.getSeleniumBrowser();
		String webAppContext = seleniumClient.getCtrlstatContext();
		
		selenium_browser.open("/"+webAppContext+"/accordiCooperazioneList.do");
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
		
		int totLista = TestUtil.getTotEntryLista(toParse); //il totale dei accordi rimanenti che mi indica l'interfaccia
		
		incrDeleted();//ho cancellato una entry
		
		//il totAccordi - getTotDeleted deve essere = a totLista ovvero gli accordi rimanenti
		assertEquals( (totAccordi-getTotDeleted())==totLista, true);
	}
	
	public static void deleteAll(SeleniumClient seleniumClient,boolean validate) throws AssertionError{
		assertNotNull(seleniumClient,"Il client seleniun e' null.");
		//Note: si effettua la cancellazione dell'intera lista.
		String webAppContext = seleniumClient.getCtrlstatContext();
		TestUtil.deleteAll(seleniumClient, webAppContext, "accordiCooperazioneList", validate);
	}
	
	public static void list(SeleniumClient seleniumClient,AccordoCooperazione accordo, boolean validate) throws AssertionError{
		Selenium selenium_browser = seleniumClient.getSeleniumBrowser();
		String webAppContext = seleniumClient.getCtrlstatContext();
		
		selenium_browser.open("/"+webAppContext+"/accordiCooperazioneList.do");
		selenium_browser.click("link=Elenco");
		selenium_browser.waitForPageToLoad(TestUtil.getWaitPageToLoadTime());
		assertEquals(selenium_browser.isTextPresent(accordo.getNome()),true);
	}
	
	static synchronized void incrDeleted () {
		totDeleted++;
	}
	
	static synchronized int getTotDeleted(){
		return totDeleted;
	}
}
