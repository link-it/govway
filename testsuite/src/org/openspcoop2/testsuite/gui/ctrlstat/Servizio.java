/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2016 Link.it srl (http://link.it). 
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

import java.io.File;
import java.util.Vector;


import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.Documento;
import org.openspcoop2.core.registry.Fruitore;
import org.openspcoop2.core.registry.constants.TipologiaServizio;
import org.openspcoop2.testsuite.core.CostantiTestSuite;
import org.openspcoop2.testsuite.gui.commons.SeleniumClient;
import org.openspcoop2.testsuite.gui.commons.TestUtil;
import org.testng.Reporter;

import com.thoughtworks.selenium.Selenium;
import com.thoughtworks.selenium.SeleniumException;

/**
 * Libreria per Servizio
 *
 * @author Stefano Corallo <corallo@link.it>
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class Servizio {

	private static int totDeleted;
	
	public static void create(SeleniumClient seleniumClient,AccordoServizioParteSpecifica asps,boolean validate) throws AssertionError,Exception{
		org.openspcoop2.core.registry.Servizio servizio = asps.getServizio();
		Selenium selenium_browser = seleniumClient.getSeleniumBrowser();
		String webAppContext = seleniumClient.getCtrlstatContext();
		
		assertNotNull(servizio);
		
		String tipo = servizio.getTipo();
		String nome = servizio.getNome();
		String provider = servizio.getTipoSoggettoErogatore()+"/"+servizio.getNomeSoggettoErogatore();
		String accordo = asps.getAccordoServizioParteComune();
		String accordoServParteSpecifica = asps.getNome();
		
		selenium_browser.open("/"+webAppContext+"/serviziAdd.do");
		selenium_browser.select("provider", "label="+provider);
		selenium_browser.select("accordo", "label="+accordo);
		try{
			selenium_browser.waitForPageToLoad(TestUtil.getWaitPageToLoadTime());
		}catch(Exception e){}
		if(asps.getPortType()!=null){
			selenium_browser.select("port_type", "label="+asps.getPortType());
			try{
				selenium_browser.waitForPageToLoad(TestUtil.getWaitPageToLoadTime());
			}catch(Exception e){}
		}
		// Li setto dopo aver settato il port_type, perchè il
		// settaggio del port_type fa cambiare il nomeservizio
		selenium_browser.select("tiposervizio", "label="+tipo);
		selenium_browser.type("nomeservizio", nome);
		if(asps.getPortType()==null){
			if(TipologiaServizio.CORRELATO.equals(servizio.getTipologiaServizio()))
				selenium_browser.check("servcorr");
			else
				selenium_browser.uncheck("servcorr");
		}
		if(asps.getDescrizione()!=null)
			selenium_browser.type("descrizione", asps.getDescrizione());
		
		if(asps.getStatoPackage()!=null)
			selenium_browser.select("stato", "label="+asps.getStatoPackage());
		
		if(asps.getVersioneProtocollo()!=null)
			selenium_browser.select("profilo", "label="+asps.getVersioneProtocollo());

		selenium_browser.type("nome_aps", accordoServParteSpecifica);
		
		//connettore
		TestUtil.CRUDConnettore(selenium_browser, servizio.getConnettore(), CostantiDB.CREATE,servizio,null,true);
		
		Vector<File> files = TestUtil.CRUD_WSDL(seleniumClient, validate, webAppContext, CostantiDB.CREATE, asps,false);
		
		selenium_browser.click("//input[@value='"+CostantiTestSuite.BOTTONE_INVIA+"']");
		selenium_browser.waitForPageToLoad(TestUtil.getWaitPageToLoadTime());
		assertEquals((provider+":"+accordoServParteSpecifica), selenium_browser.getText("link="+(provider+":"+accordoServParteSpecifica)));
		
		// elimino wsdl temporanei
		TestUtil.eliminaFileTemporanei(files);
		
		//fruitori
		int idServizio = getIdServizio(selenium_browser, webAppContext, provider, accordoServParteSpecifica);
		int idSoggEr = getIdSoggEr(selenium_browser, webAppContext, provider, accordoServParteSpecifica);
		deleteAllFruitori(seleniumClient, idServizio, idSoggEr, nome, tipo, validate);
		if(asps.sizeFruitoreList()>0){
			for (int i = 0; i < asps.sizeFruitoreList(); i++) {
				Fruitore fruitore = asps.getFruitore(i);
				CRUDFruitori(seleniumClient, fruitore, idServizio, validate, CostantiDB.CREATE);
			}
		}
		
		// Allegati
		Vector<Documento> documenti = new Vector<Documento>();
		for (int i = 0; i < asps.sizeAllegatoList(); i++) {
			Documento doc = asps.getAllegato(i);
			documenti.add(doc);
		}
		for (int i = 0; i < asps.sizeSpecificaSemiformaleList(); i++) {
			Documento doc = asps.getSpecificaSemiformale(i);
			documenti.add(doc);
		}
		for (int i = 0; i < asps.sizeSpecificaLivelloServizioList(); i++) {
			Documento doc = asps.getSpecificaLivelloServizio(i);
			documenti.add(doc);
		}
		for (int i = 0; i < asps.sizeSpecificaSicurezzaList(); i++) {
			Documento doc = asps.getSpecificaSicurezza(i);
			documenti.add(doc);
		}
		TestUtil.CRUDDocumenti(seleniumClient, validate, CostantiDB.CREATE, webAppContext, "serviziAllegatiList.do", documenti, idServizio, false, false);
	}
	
	public static void update(SeleniumClient seleniumClient,AccordoServizioParteSpecifica asps,boolean validate) throws AssertionError,Exception{
		org.openspcoop2.core.registry.Servizio servizio = asps.getServizio();
		Selenium selenium_browser = seleniumClient.getSeleniumBrowser();
		String webAppContext = seleniumClient.getCtrlstatContext();
		String tipo = servizio.getTipo();
		String nome = servizio.getNome();
		String oldTipo = servizio.getOldTipoSoggettoErogatoreForUpdate()!=null ? servizio.getOldTipoSoggettoErogatoreForUpdate() : servizio.getTipoSoggettoErogatore();
		String oldNome = servizio.getOldNomeSoggettoErogatoreForUpdate()!=null ? servizio.getOldNomeSoggettoErogatoreForUpdate() : servizio.getNomeSoggettoErogatore();
		
		String provider = servizio.getTipoSoggettoErogatore()+"/"+servizio.getNomeSoggettoErogatore();
		String oldProvider = oldTipo+"/"+oldNome;
		String accordo = asps.getAccordoServizioParteComune();
		String accordoServParteSpecifica = asps.getNome();
		
		selenium_browser.open("/"+webAppContext+"/serviziList.do");
		selenium_browser.click("link="+(oldProvider+":"+accordoServParteSpecifica));
		selenium_browser.waitForPageToLoad(TestUtil.getWaitPageToLoadTime());
		selenium_browser.type("nomeservizio", nome);
		selenium_browser.select("tiposervizio", "label="+tipo);
		selenium_browser.select("accordo", "label="+accordo);
		try{
			selenium_browser.waitForPageToLoad(TestUtil.getWaitPageToLoadTime());
		}catch(Exception e){}
		if(asps.getPortType()!=null){
			selenium_browser.select("port_type", "label="+asps.getPortType());
			try{
				selenium_browser.waitForPageToLoad(TestUtil.getWaitPageToLoadTime());
			}catch(Exception e){}
		}
		if(asps.getPortType()==null){
			if(TipologiaServizio.CORRELATO.equals(servizio.getTipologiaServizio()))
				selenium_browser.check("servcorr");
			else
				selenium_browser.uncheck("servcorr");
		}
		if(asps.getDescrizione()!=null)
			selenium_browser.type("descrizione", asps.getDescrizione());
		
		if(asps.getStatoPackage()!=null)
			selenium_browser.select("stato", "label="+asps.getStatoPackage());
		
		if(asps.getVersioneProtocollo()!=null)
			selenium_browser.select("profilo", "label="+asps.getVersioneProtocollo());
		
		//connettore
		TestUtil.CRUDConnettore(selenium_browser, servizio.getConnettore(), CostantiDB.UPDATE,servizio,null,true);
		
		selenium_browser.click("//input[@value='"+CostantiTestSuite.BOTTONE_INVIA+"']");
		selenium_browser.waitForPageToLoad(TestUtil.getWaitPageToLoadTime());
		assertEquals(selenium_browser.getText("link="+(provider+":"+accordoServParteSpecifica)),(provider+":"+accordoServParteSpecifica));
		
		// WSDL
		selenium_browser.open("/"+webAppContext+"/serviziList.do");
		selenium_browser.click("link="+(provider+":"+accordoServParteSpecifica));
		selenium_browser.waitForPageToLoad(TestUtil.getWaitPageToLoadTime());
		Vector<File> files = TestUtil.CRUD_WSDL(seleniumClient, validate, webAppContext, CostantiDB.UPDATE, asps,false);	
		TestUtil.eliminaFileTemporanei(files);
		
		//fruitori
		int idServizio = getIdServizio(selenium_browser, webAppContext, provider, accordoServParteSpecifica);
		int idSoggEr = getIdSoggEr(selenium_browser, webAppContext, provider, accordoServParteSpecifica);
		deleteAllFruitori(seleniumClient, idServizio, idSoggEr, nome, tipo, validate);
		if(asps.sizeFruitoreList()>0){
			for (int i = 0; i < asps.sizeFruitoreList(); i++) {
				Fruitore fruitore = asps.getFruitore(i);
				CRUDFruitori(seleniumClient, fruitore, idServizio, validate, CostantiDB.CREATE);
			}
		}

		// Allegati
		Vector<Documento> documenti = new Vector<Documento>();
		for (int i = 0; i < asps.sizeAllegatoList(); i++) {
			Documento doc = asps.getAllegato(i);
			documenti.add(doc);
		}
		for (int i = 0; i < asps.sizeSpecificaSemiformaleList(); i++) {
			Documento doc = asps.getSpecificaSemiformale(i);
			documenti.add(doc);
		}
		for (int i = 0; i < asps.sizeSpecificaLivelloServizioList(); i++) {
			Documento doc = asps.getSpecificaLivelloServizio(i);
			documenti.add(doc);
		}
		for (int i = 0; i < asps.sizeSpecificaSicurezzaList(); i++) {
			Documento doc = asps.getSpecificaSicurezza(i);
			documenti.add(doc);
		}
		TestUtil.CRUDDocumenti(seleniumClient, validate, CostantiDB.UPDATE, webAppContext, "serviziAllegatiList.do", documenti, idServizio, false, false);
	}
	
	
	public static void delete(SeleniumClient seleniumClient,AccordoServizioParteSpecifica asps,int totServizi,boolean validate) throws AssertionError{
		Selenium selenium_browser = seleniumClient.getSeleniumBrowser();
		String webAppContext = seleniumClient.getCtrlstatContext();
		selenium_browser.open("/"+webAppContext+"/serviziList.do");
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
		
		int totLista = TestUtil.getTotEntryLista(toParse); //il totale dei servizi rimanenti che mi indica l'interfaccia
		
		incrDeleted();//ho cancellato una entry
		
		//il totServizi - getTotDeleted deve essere = a totLista ovvero i servizi rimanenti
		assertEquals( (totServizi - getTotDeleted())==totLista, true);
	}
	
	public static void list(SeleniumClient seleniumClient,AccordoServizioParteSpecifica asps,boolean validate) throws AssertionError{
		org.openspcoop2.core.registry.Servizio servizio = asps.getServizio();
		Selenium selenium_browser = seleniumClient.getSeleniumBrowser();
		String webAppContext = seleniumClient.getCtrlstatContext();
		String tipo = servizio.getTipo();
		String nome = servizio.getNome();
		
		selenium_browser.open("/"+webAppContext+"/serviziList.do");
		assertEquals(selenium_browser.getText("link="+(tipo+"/"+nome)), (tipo+"/"+nome));
	}
	
	static synchronized void incrDeleted () {
		totDeleted++;
	}
	
	static synchronized int getTotDeleted(){
		return totDeleted;
	}

	public static void deleteAll(SeleniumClient seleniumClient, boolean validate) throws AssertionError{
		assertNotNull(seleniumClient,"Il client seleniun e' null.");
		//Note: si effettua la cancellazione dell'intera lista.
		String webAppContext = seleniumClient.getCtrlstatContext();
		TestUtil.deleteAll(seleniumClient, webAppContext, "serviziList", validate);
	}
	
	public static void deleteAllFruitori(SeleniumClient seleniumClient,AccordoServizioParteSpecifica asps, boolean validate) throws AssertionError{
		org.openspcoop2.core.registry.Servizio servizio = asps.getServizio();
		Selenium selenium_browser = seleniumClient.getSeleniumBrowser();
		String webAppContext = seleniumClient.getCtrlstatContext();
		String tipo = servizio.getTipo();
		String nome = servizio.getNome();
		String provider = servizio.getTipoSoggettoErogatore()+"/"+servizio.getNomeSoggettoErogatore();
		String accordoServParteSpecifica = asps.getNome();
		
		//fruitori
		int idServizio = getIdServizio(selenium_browser, webAppContext, provider, accordoServParteSpecifica);
		int idSoggEr = getIdSoggEr(selenium_browser, webAppContext, provider, accordoServParteSpecifica);
		deleteAllFruitori(seleniumClient, idServizio, idSoggEr, nome, tipo, validate);
	}
	
        private static void deleteAllFruitori(SeleniumClient seleniumClient,int idServizio , int idSoggEr, String nomeServ, String tipoServ, boolean validat) throws AssertionError{
		String webAppContext = seleniumClient.getCtrlstatContext();
		assertNotNull(seleniumClient,"Il client seleniun e' null.");
		
		//Note: si effettua la cancellazione dell'intera lista.
		Selenium selenium = seleniumClient.getSeleniumBrowser();
		selenium.open("/"+webAppContext+"/serviziFruitoriList.do?id="+idServizio+"&idSoggErogatore="+idSoggEr+"&nomeservizio="+nomeServ+"&tiposervizio="+tipoServ);
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
		
		assertEquals(totLista, 0);
	}
	
	private static Selenium CRUDFruitori(SeleniumClient seleniumClient,Fruitore fruitore,int idServizio,boolean validate,int operationType) throws AssertionError,Exception{
		Selenium selenium = seleniumClient.getSeleniumBrowser();
		String webAppContext = seleniumClient.getCtrlstatContext();
		
		selenium.open("/"+webAppContext+"/serviziFruitoriList.do?id="+idServizio);
		
		switch (operationType) {
		case CostantiDB.CREATE:
			
			selenium.click("//input[@value='"+CostantiTestSuite.BOTTONE_ADD+"']");
			selenium.waitForPageToLoad(TestUtil.getWaitPageToLoadTime());
			selenium.select("provider", "label="+fruitore.getTipo()+"/"+fruitore.getNome());
			break;

		case CostantiDB.UPDATE:
			selenium.click("link="+fruitore.getTipo()+"/"+fruitore.getNome());
			selenium.waitForPageToLoad(TestUtil.getWaitPageToLoadTime());
			break;
		}
		
		if(fruitore.getStatoPackage()!=null){
			Reporter.log("Setto per fruitore ["+(fruitore.getTipo()+"/"+fruitore.getNome())+"] lo stato: "+fruitore.getStatoPackage());
			selenium.select("stato", "label="+fruitore.getStatoPackage());
		}
		else{
			Reporter.log("Non setto per fruitore ["+(fruitore.getTipo()+"/"+fruitore.getNome())+"] lo stato: "+fruitore.getStatoPackage());
		}
		
		if(fruitore.getVersioneProtocollo()!=null){
			Reporter.log("Setto per fruitore ["+(fruitore.getTipo()+"/"+fruitore.getNome())+"] il profilo: "+fruitore.getVersioneProtocollo());
			selenium.select("profilo", "label="+fruitore.getVersioneProtocollo());
		}
		else{
			Reporter.log("Non setto per fruitore ["+(fruitore.getTipo()+"/"+fruitore.getNome())+"] il profilo: "+fruitore.getVersioneProtocollo());
		}
		
		if(fruitore.getClientAuth()!=null){
			Reporter.log("Setto per fruitore ["+(fruitore.getTipo()+"/"+fruitore.getNome())+"] il client-auth: "+fruitore.getClientAuth());
			selenium.select("clientAuth", "label="+fruitore.getClientAuth());
		}
		else{
			Reporter.log("Non setto per fruitore ["+(fruitore.getTipo()+"/"+fruitore.getNome())+"] il client-auth: "+fruitore.getClientAuth());
		}
		
		//connettore
		TestUtil.CRUDConnettore(selenium, fruitore.getConnettore(), operationType,fruitore,null,true);

		Vector<File> files = null;
		if(CostantiDB.CREATE == operationType){
			//wsdl
			files = TestUtil.CRUD_WSDL(seleniumClient, validate, webAppContext, CostantiDB.CREATE, fruitore,false);
		}
		
		selenium.click("//input[@value='"+CostantiTestSuite.BOTTONE_INVIA+"']");
		selenium.waitForPageToLoad(TestUtil.getWaitPageToLoadTime());
		
		assertEquals(selenium.getText("link="+(fruitore.getTipo()+"/"+fruitore.getNome())),(fruitore.getTipo()+"/"+fruitore.getNome()));
		
		if(CostantiDB.UPDATE == operationType){
			// WSDL
			selenium.open("/"+webAppContext+"/serviziFruitoriList.do?id="+idServizio);
			selenium.click("link="+fruitore.getTipo()+"/"+fruitore.getNome());
			selenium.waitForPageToLoad(TestUtil.getWaitPageToLoadTime());
			files = TestUtil.CRUD_WSDL(seleniumClient, validate, webAppContext, CostantiDB.UPDATE, fruitore,true);
		}
		
		// wsdl temporanei
		TestUtil.eliminaFileTemporanei(files);
		
		return selenium;
	}
	
	private static int getIdServizio(Selenium selenium,String webAppContext,String provider,String accordoServParteSpecifica) throws AssertionError{
		selenium.open("/"+webAppContext+"/serviziList.do");
		String url = selenium.getAttribute("link="+(provider+":"+accordoServParteSpecifica)+"@href");
		String idS = url.substring("serviziChange.do?id=".length(), url.indexOf("&"));
		try{
			return Integer.parseInt(idS);
		}catch (Exception e) {
			Reporter.log("Errore durante il recuperare dell'id Servizio dalla URL: "+url);
			throw new AssertionError("Errore durante il recuperare dell'id Servizio dalla URL: "+url);
		}
	}
	
	private static int getIdSoggEr(Selenium selenium,String webAppContext,String provider,String accordoServParteSpecifica) throws AssertionError{
		selenium.open("/"+webAppContext+"/serviziList.do");
		String url = selenium.getAttribute("link="+(provider+":"+accordoServParteSpecifica)+"@href");
		String idS = url.substring(url.indexOf("&idSoggErogatore")+17, url.length());
		try{
			return Integer.parseInt(idS);
		}catch (Exception e) {
			Reporter.log("Errore durante il recuperare dell'id Soggetto Erogatore dalla URL: "+url);
			throw new AssertionError("Errore durante il recuperare dell'id Soggetto Erogatore dalla URL: "+url);
		}
	}
}


