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

import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.Azione;
import org.openspcoop2.core.registry.Documento;
import org.openspcoop2.core.registry.Operation;
import org.openspcoop2.core.registry.PortType;
import org.openspcoop2.core.registry.driver.BeanUtilities;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.testsuite.core.CostantiTestSuite;
import org.openspcoop2.testsuite.gui.commons.SeleniumClient;
import org.openspcoop2.testsuite.gui.commons.TestUtil;
import org.testng.Reporter;

import com.thoughtworks.selenium.Selenium;
import com.thoughtworks.selenium.SeleniumException;

/**
 *
 * Libreria per Accordo di Servizio
 * @author Stefano Corallo <corallo@link.it>
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class Accordo {

	private static int totDeleted;
	
	public static void create(SeleniumClient seleniumClient,AccordoServizioParteComune accordo,boolean validate,boolean terminologiaSICA) throws AssertionError,Exception{
		Selenium selenium_browser = seleniumClient.getSeleniumBrowser();
		
		assertNotNull(accordo.getNome());
		
		String descr = accordo.getDescrizione()!=null ? accordo.getDescrizione() : "";
		String webAppContext = seleniumClient.getCtrlstatContext();
		String profiloCollaborazione = accordo.getProfiloCollaborazione()!=null 
										? accordo.getProfiloCollaborazione().toString()
										: "oneway";
		boolean filtroDuplicati= accordo.getFiltroDuplicati()!=null && CostantiConfigurazione.ABILITATO.toString().equals(accordo.getFiltroDuplicati()) 
								? true
								: false;
		boolean confermaRicezione = accordo.getConfermaRicezione()!=null && CostantiConfigurazione.ABILITATO.toString().equals(accordo.getConfermaRicezione()) 
									? true
									: false;
		boolean idCollaborazione = accordo.getIdCollaborazione()!=null && CostantiConfigurazione.ABILITATO.toString().equals(accordo.getIdCollaborazione()) 
									? true
									: false;
		boolean consegnaOrdine = accordo.getConsegnaInOrdine()!=null && CostantiConfigurazione.ABILITATO.toString().equals(accordo.getConsegnaInOrdine()) 
								? true
								: false;
		
		String scadenza  = accordo.getScadenza()!=null ? accordo.getScadenza() : "";
		
		boolean servizioComposto = (accordo.getServizioComposto()!=null);
		selenium_browser.open(TestUtil.urlAccordoServizio(webAppContext, "accordiAdd.do", terminologiaSICA, servizioComposto));
		selenium_browser.type("nome", accordo.getNome());
		selenium_browser.type("descr", descr);
		
		if(IDAccordoFactory.getInstance().versioneNonDefinita(accordo.getVersione())==false){
			selenium_browser.type("versione", accordo.getVersione());
		}else{
			selenium_browser.type("versione", "0");
		}
		
		if(BeanUtilities.getSoggettoReferenteID(accordo.getSoggettoReferente())!=null){
			selenium_browser.select("referente", "label="+BeanUtilities.getSoggettoReferenteID(accordo.getSoggettoReferente()).toString());
		}
		
		if(!terminologiaSICA && servizioComposto){
			// seleziono servizio composto
			selenium_browser.click("isServizioComposto");
			selenium_browser.waitForPageToLoad(TestUtil.getWaitPageToLoadTime());
		}
		if(servizioComposto){
			// imposto accordo di cooperazione
			selenium_browser.select("accordoCooperazione", "label="+accordo.getServizioComposto().getAccordoCooperazione());
		}
		
		//if(accordo.getStatoPackage()!=null)
		//	selenium_browser.select("stato", "label="+accordo.getStatoPackage());
		// ADD: e' solo un text
		
		Vector<File> files = TestUtil.CRUD_WSDL(seleniumClient, validate, webAppContext, CostantiDB.CREATE, accordo,false);
		
		selenium_browser.select("profcoll", "label="+profiloCollaborazione);
		
		if(filtroDuplicati)
			selenium_browser.check("filtrodup");
		else 
			selenium_browser.uncheck("filtrodup");
		
		if(confermaRicezione)
			selenium_browser.check("confric");
		else 
			selenium_browser.uncheck("confric");
		
		if(idCollaborazione)
			selenium_browser.check("idcoll");
		else 
			selenium_browser.uncheck("idcoll");
		if(consegnaOrdine)
			selenium_browser.check("consord");
		else
			selenium_browser.uncheck("consord");
		
		selenium_browser.type("scadenza", scadenza);
		
		selenium_browser.click("//input[@value='"+CostantiTestSuite.BOTTONE_INVIA+"']");
		selenium_browser.waitForPageToLoad(TestUtil.getWaitPageToLoadTime());

		assertEquals(IDAccordoFactory.getInstance().getUriFromAccordo(accordo), selenium_browser.getText("link="+IDAccordoFactory.getInstance().getUriFromAccordo(accordo)));
		
		int idAccordo=getIdAccordo(selenium_browser,webAppContext,IDAccordoFactory.getInstance().getUriFromAccordo(accordo),terminologiaSICA,servizioComposto);
		
		// elimino wsdl temporanei
		TestUtil.eliminaFileTemporanei(files);
		
		// azioni
		//deleteAllAzione(seleniumClient, idAccordo, validate);
		if(accordo.sizeAzioneList()>0)
		{
			for (int i = 0; i < accordo.sizeAzioneList(); i++) {
				Azione azione = accordo.getAzione(i);
				CRUDAzione(seleniumClient, azione, idAccordo, accordo.sizeAzioneList(),validate, CostantiDB.CREATE,terminologiaSICA,servizioComposto);
			}
		}
		
		// port type
		if(accordo.sizePortTypeList()>0)
		{
			for (int i = 0; i < accordo.sizePortTypeList(); i++) {
				PortType pt = accordo.getPortType(i);
				CRUDPortType(seleniumClient, pt, idAccordo, accordo.sizePortTypeList(),validate, CostantiDB.CREATE,terminologiaSICA,servizioComposto);
				// Se il port type ha delle azioni, creo anche quelle
				if(pt.sizeAzioneList()>0)
				{
					int idPT=getIdPortType(selenium_browser,webAppContext,idAccordo,pt.getNome(),terminologiaSICA,servizioComposto);
					for (int j = 0; j < pt.sizeAzioneList(); j++) {
						Operation azione = pt.getAzione(j);
						CRUDAzionePT(seleniumClient, azione, idAccordo, accordo.getNome(), idPT, pt.getNome(), pt.sizeAzioneList(),validate, CostantiDB.CREATE,terminologiaSICA,servizioComposto);
					}
				}
			}
		}
		
		// servizi componenti
		if(servizioComposto){
			TestUtil.CRUDElencoServiziComponenti(seleniumClient, validate, CostantiDB.CREATE, webAppContext, accordo.getServizioComposto().getServizioComponenteList(), idAccordo, terminologiaSICA);
		}
		
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
		if(servizioComposto){
			for (int i = 0; i < accordo.getServizioComposto().sizeSpecificaCoordinamentoList(); i++) {
				Documento doc = accordo.getServizioComposto().getSpecificaCoordinamento(i);
				documenti.add(doc);
			}
		}
		TestUtil.CRUDDocumenti(seleniumClient, validate, CostantiDB.CREATE, webAppContext, "accordiAllegatiList.do", documenti, idAccordo, terminologiaSICA, servizioComposto);
	}
	
	private static int getIdAccordo(Selenium selenium,String webAppContext,String uriAccordo,boolean terminologiaSICA,boolean servizioComposto) throws AssertionError{
		selenium.open(TestUtil.urlAccordoServizio(webAppContext, "accordiList.do", terminologiaSICA, servizioComposto));
		String url = selenium.getAttribute("link="+uriAccordo+"@href");
		String idA = url.substring("accordiChange.do?id=".length(),url.indexOf("&"));
		try{
			return Integer.parseInt(idA);
		}catch (Exception e) {
			Reporter.log("Errore durante il recuperare dell'id Accordo di Servizio dalla URL: "+url);
			throw new AssertionError("Errore durante il recuperare dell'id Accordo di Servizio dalla URL: "+url);
		}
	}
	
	private static int getIdPortType(Selenium selenium,String webAppContext,int idAccordo,String nomePT,boolean terminologiaSICA,boolean servizioComposto) throws AssertionError{
		selenium.open(TestUtil.urlAccordoServizio(webAppContext, "accordiPorttypeList.do?id="+idAccordo, terminologiaSICA, servizioComposto));
		String url = selenium.getAttribute("link="+nomePT+"@href");
		String idPT = url.substring("accordiPorttypeChange.do?id=".length(),url.indexOf("&"));
		try{
			return Integer.parseInt(idPT);
		}catch (Exception e) {
			Reporter.log("Errore durante il recuperare dell'id Port type dalla URL: "+url);
			throw new AssertionError("Errore durante il recuperare dell'id Port type dalla URL: "+url);
		}
	}
	
	public static void update(SeleniumClient seleniumClient,AccordoServizioParteComune accordo,AccordoServizioParteComune oldAccordo,boolean validate,boolean terminologiaSICA) throws AssertionError,Exception{
		Selenium selenium_browser = seleniumClient.getSeleniumBrowser();
		String descr = accordo.getDescrizione()!=null ? accordo.getDescrizione() : "";
		String webAppContext = seleniumClient.getCtrlstatContext();
	
		String profiloCollaborazione = accordo.getProfiloCollaborazione()!=null 
										? accordo.getProfiloCollaborazione().toString()
										: "oneway";
		boolean filtroDuplicati= accordo.getFiltroDuplicati()!=null && CostantiConfigurazione.ABILITATO.toString().equals(accordo.getFiltroDuplicati()) 
								? true
								: false;
		boolean confermaRicezione = accordo.getConfermaRicezione()!=null && CostantiConfigurazione.ABILITATO.toString().equals(accordo.getConfermaRicezione()) 
									? true
									: false;
		boolean idCollaborazione = accordo.getIdCollaborazione()!=null && CostantiConfigurazione.ABILITATO.toString().equals(accordo.getIdCollaborazione()) 
									? true
									: false;
		boolean consegnaOrdine = accordo.getConsegnaInOrdine()!=null && CostantiConfigurazione.ABILITATO.toString().equals(accordo.getConsegnaInOrdine()) 
								? true
								: false;
		
		String scadenza  = accordo.getScadenza()!=null ? accordo.getScadenza() : "";

		boolean servizioCompostoOLD = (oldAccordo.getServizioComposto()!=null);
		boolean servizioComposto = (accordo.getServizioComposto()!=null);
		selenium_browser.open(TestUtil.urlAccordoServizio(webAppContext, "accordiList.do", terminologiaSICA, servizioCompostoOLD));
		selenium_browser.click("link="+IDAccordoFactory.getInstance().getUriFromAccordo(oldAccordo));
		selenium_browser.waitForPageToLoad(TestUtil.getWaitPageToLoadTime());
		
		selenium_browser.type("nome", accordo.getNome());
		selenium_browser.type("descr", descr);
		
		if(IDAccordoFactory.getInstance().versioneNonDefinita(accordo.getVersione())==false){
			selenium_browser.type("versione", accordo.getVersione());
		}else{
			selenium_browser.type("versione", "0");
		}
		
		if(BeanUtilities.getSoggettoReferenteID(accordo.getSoggettoReferente())!=null){
			selenium_browser.select("referente", "label="+BeanUtilities.getSoggettoReferenteID(accordo.getSoggettoReferente()).toString());
		}else{
			selenium_browser.select("referente", "label=-");
		}
		
		if(!terminologiaSICA){
			// seleziono servizio composto
			Reporter.log("OLD_s_c["+servizioCompostoOLD+"] s_c_["+servizioComposto+"]");
			if(servizioCompostoOLD && !servizioComposto){
				Reporter.log("CLICK per eliminare il servizio composto");
				selenium_browser.click("isServizioComposto");// prima era un servizio composto, ora non lo e' piu'
				selenium_browser.waitForPageToLoad(TestUtil.getWaitPageToLoadTime());
			}
			if(!servizioCompostoOLD && servizioComposto){
				Reporter.log("CLICK per impostare il servizio composto");
				selenium_browser.click("isServizioComposto");// prima era un servizio composto, ora non lo e' piu'
				selenium_browser.waitForPageToLoad(TestUtil.getWaitPageToLoadTime());
			}
		}
		if(servizioComposto){
			// imposto accordo di cooperazione
			selenium_browser.select("accordoCooperazione", "label="+accordo.getServizioComposto().getAccordoCooperazione());
		}
		
		if(accordo.getStatoPackage()!=null)
			selenium_browser.select("stato", "label="+accordo.getStatoPackage());
		
		selenium_browser.select("profcoll", "label="+profiloCollaborazione);
		
		if(filtroDuplicati)
			selenium_browser.check("filtrodup");
		else 
			selenium_browser.uncheck("filtrodup");
		
		if(confermaRicezione)
			selenium_browser.check("confric");
		else 
			selenium_browser.uncheck("confric");
		
		if(idCollaborazione)
			selenium_browser.check("idcoll");
		else 
			selenium_browser.uncheck("idcoll");
		
		if(consegnaOrdine)
			selenium_browser.check("consord");
		else
			selenium_browser.uncheck("consord");
		
		selenium_browser.type("scadenza", scadenza);
		selenium_browser.click("//input[@value='"+CostantiTestSuite.BOTTONE_INVIA+"']");
		selenium_browser.waitForPageToLoad(TestUtil.getWaitPageToLoadTime());
		
		assertEquals(IDAccordoFactory.getInstance().getUriFromAccordo(accordo), selenium_browser.getText("link="+IDAccordoFactory.getInstance().getUriFromAccordo(accordo)));
		
		int idAccordo=getIdAccordo(selenium_browser,webAppContext,IDAccordoFactory.getInstance().getUriFromAccordo(accordo),terminologiaSICA,servizioComposto);
			
		//wsdl
		selenium_browser.open(TestUtil.urlAccordoServizio(webAppContext, "accordiList.do", terminologiaSICA, servizioComposto));
		selenium_browser.click("link="+IDAccordoFactory.getInstance().getUriFromAccordo(accordo));
		selenium_browser.waitForPageToLoad(TestUtil.getWaitPageToLoadTime());
		Vector<File> files = TestUtil.CRUD_WSDL(seleniumClient, validate, webAppContext, CostantiDB.UPDATE, accordo,false);	
		TestUtil.eliminaFileTemporanei(files);
		
		// azioni
		//cancello puntualmente le vecchie azioni
		//cancello prima le azioni correlate
		for(int i=0;i<oldAccordo.sizeAzioneList();i++){
			Azione oldAzione = oldAccordo.getAzione(i);
			//se l azione ha una correlata allora la cancello
			if(oldAzione.getCorrelata()!=null){
				CRUDAzione(seleniumClient, oldAzione, idAccordo, accordo.sizeAzioneList(), validate, CostantiDB.DELETE,terminologiaSICA,servizioComposto);
			}
		}
		//cancello le non correllate
		for(int i=0;i<oldAccordo.sizeAzioneList();i++){
			Azione oldAzione = oldAccordo.getAzione(i);
			//se l azione ha una correlata allora la cancello
			if(oldAzione.getCorrelata()==null){
				CRUDAzione(seleniumClient, oldAzione, idAccordo, accordo.sizeAzioneList(), validate, CostantiDB.DELETE,terminologiaSICA,servizioComposto);
			}
		}
		//deleteAllAzione(seleniumClient, idAccordo, validate);
		if(accordo.sizeAzioneList()>0)
		{
			for (int i = 0; i < accordo.sizeAzioneList(); i++) {
				Azione azione = accordo.getAzione(i);
				CRUDAzione(seleniumClient, azione, idAccordo, accordo.sizeAzioneList(), validate, CostantiDB.CREATE,terminologiaSICA,servizioComposto);
			}
		}
		
		// port types
		//cancello puntualmente i vecchi porttype
		//cancello prima le azioni correlate dei porttype
		for(int i=0;i<oldAccordo.sizePortTypeList();i++){
			PortType oldPT = oldAccordo.getPortType(i);
			if(oldPT.sizeAzioneList()>0)
			{
				int idPT=getIdPortType(selenium_browser,webAppContext,idAccordo,oldPT.getNome(),terminologiaSICA,servizioComposto);
				for (int j = 0; j < oldPT.sizeAzioneList(); j++) {
					Operation oldAzione = oldPT.getAzione(j);
					if(oldAzione.getCorrelata()!=null){
						CRUDAzionePT(seleniumClient, oldAzione, idAccordo, accordo.getNome(), idPT, oldPT.getNome(), oldPT.sizeAzioneList(),validate, CostantiDB.DELETE,terminologiaSICA,servizioComposto);
					}
				}
			}
		}
		//cancello le azioni non correlate dei porttype
		for(int i=0;i<oldAccordo.sizePortTypeList();i++){
			PortType oldPT = oldAccordo.getPortType(i);
			if(oldPT.sizeAzioneList()>0)
			{
				int idPT=getIdPortType(selenium_browser,webAppContext,idAccordo,oldPT.getNome(),terminologiaSICA,servizioComposto);
				for (int j = 0; j < oldPT.sizeAzioneList(); j++) {
					Operation oldAzione = oldPT.getAzione(j);
					if(oldAzione.getCorrelata()==null){
						CRUDAzionePT(seleniumClient, oldAzione, idAccordo, accordo.getNome(), idPT, oldPT.getNome(), oldPT.sizeAzioneList(),validate, CostantiDB.DELETE,terminologiaSICA,servizioComposto);
					}
				}
			}
		}
		// cancello i vecchi porttype
		for(int i=0;i<oldAccordo.sizePortTypeList();i++){
			PortType oldPT = oldAccordo.getPortType(i);
			CRUDPortType(seleniumClient, oldPT, idAccordo, accordo.sizePortTypeList(), validate, CostantiDB.DELETE,terminologiaSICA,servizioComposto);
		}
		// inserisco i nuovi porttype
		if(accordo.sizePortTypeList()>0)
		{
			for (int i = 0; i < accordo.sizePortTypeList(); i++) {
				PortType pt = accordo.getPortType(i);
				CRUDPortType(seleniumClient, pt, idAccordo, accordo.sizePortTypeList(), validate, CostantiDB.CREATE,terminologiaSICA,servizioComposto);
				// Se il port type ha delle azioni, creo anche quelle
				if(pt.sizeAzioneList()>0)
				{
					int idPT=getIdPortType(selenium_browser,webAppContext,idAccordo,pt.getNome(),terminologiaSICA,servizioComposto);
					for (int j = 0; j < pt.sizeAzioneList(); j++) {
						Operation azione = pt.getAzione(j);
						CRUDAzionePT(seleniumClient, azione, idAccordo, accordo.getNome(), idPT, pt.getNome(), pt.sizeAzioneList(),validate, CostantiDB.CREATE,terminologiaSICA,servizioComposto);
					}
				}
			}
		}
		
		// servizi componenti
		if(servizioComposto){
			TestUtil.CRUDElencoServiziComponenti(seleniumClient, validate, CostantiDB.UPDATE, webAppContext, accordo.getServizioComposto().getServizioComponenteList(), idAccordo, terminologiaSICA);
		}
		
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
		if(servizioComposto){
			for (int i = 0; i < accordo.getServizioComposto().sizeSpecificaCoordinamentoList(); i++) {
				Documento doc = accordo.getServizioComposto().getSpecificaCoordinamento(i);
				documenti.add(doc);
			}
		}
		TestUtil.CRUDDocumenti(seleniumClient, validate, CostantiDB.UPDATE, webAppContext, "accordiAllegatiList.do", documenti, idAccordo, terminologiaSICA, servizioComposto);
	}
	public static void delete(SeleniumClient seleniumClient,AccordoServizioParteComune accordo,int totAccordi,boolean validate,boolean terminologiaSICA) throws AssertionError{
		Selenium selenium_browser = seleniumClient.getSeleniumBrowser();
		String webAppContext = seleniumClient.getCtrlstatContext();
		
		selenium_browser.open(TestUtil.urlAccordoServizio(webAppContext, "accordiList.do", terminologiaSICA, (accordo.getServizioComposto()!=null)));
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
		TestUtil.deleteAll(seleniumClient, webAppContext, "accordiList", validate);
	}
	
	public static void list(SeleniumClient seleniumClient,AccordoServizioParteComune accordo, boolean validate,boolean terminologiaSICA) throws AssertionError{
		Selenium selenium_browser = seleniumClient.getSeleniumBrowser();
		String webAppContext = seleniumClient.getCtrlstatContext();
		
		selenium_browser.open(TestUtil.urlAccordoServizio(webAppContext, "accordiList.do", terminologiaSICA, (accordo.getServizioComposto()!=null)));
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
		
	protected static void deleteAllAzione(SeleniumClient seleniumClient,int idAccordo,boolean validate,boolean terminologiaSICA,boolean servizioComposto) throws AssertionError{
		
		String webAppContext = seleniumClient.getCtrlstatContext();
		assertNotNull(seleniumClient,"Il client seleniun e' null.");
		
		//Note: si effettua la cancellazione dell'intera lista.
		Selenium selenium = seleniumClient.getSeleniumBrowser();
		selenium.open(TestUtil.urlAccordoServizio(webAppContext, "accordiAzioniList.do?id="+idAccordo, terminologiaSICA, servizioComposto));
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
	
	private static Selenium CRUDAzione(SeleniumClient seleniumClient,Azione azione,int idAccordo,int totAzioni,boolean validate,int operationType,boolean terminologiaSICA,boolean servizioComposto) throws AssertionError{
		Selenium selenium = seleniumClient.getSeleniumBrowser();
		String webAppContext = seleniumClient.getCtrlstatContext();
		
		String profiloAzione=azione.getProfAzione()!=null ? azione.getProfAzione() : "usa profilo accordo";
		
		if("ridefinito".equals(profiloAzione))profiloAzione="ridefinisci";
		if("default".equals(profiloAzione))profiloAzione="usa profilo accordo";
		
		selenium.open(TestUtil.urlAccordoServizio(webAppContext, "accordiList.do", terminologiaSICA, servizioComposto));
		selenium.click("//a[contains(@href, 'accordiAzioniList.do?id="+idAccordo+"')]");
		selenium.waitForPageToLoad(TestUtil.getWaitPageToLoadTime());
		
		switch (operationType) {
		case CostantiDB.CREATE:
			selenium.click("//input[@value='"+CostantiTestSuite.BOTTONE_ADD+"']");
			selenium.waitForPageToLoad(TestUtil.getWaitPageToLoadTime());
			selenium.type("nomeaz", azione.getNome());
			break;

		case CostantiDB.UPDATE:
			selenium.click("link="+azione.getNome());
			selenium.waitForPageToLoad(TestUtil.getWaitPageToLoadTime());
			break;
			
		case CostantiDB.DELETE:
			Reporter.log("Cancello Azione "+azione.getNome());
			selenium.check(azione.getNome());
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
			
			//dopo la cancellazione esco
			return selenium;
		}
		
		
		selenium.select("prof", "label="+profiloAzione);
		try{
			selenium.waitForPageToLoad(TestUtil.getWaitPageToLoadTime());
		}catch (SeleniumException e) {
			// ignore
		}
		
		if("ridefinisci".equals(profiloAzione)){
			boolean filtroDuplicati= 
				azione.getFiltroDuplicati()!=null && CostantiConfigurazione.ABILITATO.toString().equals(azione.getFiltroDuplicati()) 
				? true  : false;
			
			boolean confermaRicezione = 
				azione.getConfermaRicezione()!=null && CostantiConfigurazione.ABILITATO.toString().equals(azione.getConfermaRicezione()) 
				? true	: false;
			boolean idCollaborazione = 
				azione.getIdCollaborazione()!=null && CostantiConfigurazione.ABILITATO.toString().equals(azione.getIdCollaborazione()) 
				? true  : false;
			boolean consegnaOrdine = 
				azione.getConsegnaInOrdine()!=null && CostantiConfigurazione.ABILITATO.toString().equals(azione.getConsegnaInOrdine()) 
				? true	: false;
			
			String scadenza  = azione.getScadenza()!=null ? azione.getScadenza() : "";
			
			if(filtroDuplicati)
				selenium.check("filtrodupaz");
			else 
				selenium.uncheck("filtrodupaz");
			
			if(confermaRicezione)
				selenium.check("confricaz");
			else 
				selenium.uncheck("confricaz");
			
			if(idCollaborazione)
				selenium.check("idcollaz");
			else 
				selenium.uncheck("idcollaz");
			
			if(consegnaOrdine)
				selenium.check("consordaz");
			else
				selenium.uncheck("consordaz");
			
			selenium.type("scadenzaaz", scadenza);
		}
		
		selenium.click("//input[@value='"+CostantiTestSuite.BOTTONE_INVIA+"']");
		selenium.waitForPageToLoad(TestUtil.getWaitPageToLoadTime());
		
		assertEquals(azione.getNome(), selenium.getText("link="+azione.getNome()));
		
		return selenium;
	}
	
	private static Selenium CRUDPortType(SeleniumClient seleniumClient,PortType pt,int idAccordo,int totPT,boolean validate,int operationType,boolean terminologiaSICA,boolean servizioComposto) throws AssertionError{
		Selenium selenium = seleniumClient.getSeleniumBrowser();
		String webAppContext = seleniumClient.getCtrlstatContext();
		
		String profiloPT=pt.getProfiloPT()!=null ? pt.getProfiloPT() : "usa profilo accordo";
		
		if("ridefinito".equals(profiloPT))profiloPT="ridefinisci";
		if("default".equals(profiloPT))profiloPT="usa profilo accordo";
		
		selenium.open(TestUtil.urlAccordoServizio(webAppContext, "accordiList.do", terminologiaSICA, servizioComposto));
		selenium.click("//a[contains(@href, 'accordiPorttypeList.do?id="+idAccordo+"')]");
		selenium.waitForPageToLoad(TestUtil.getWaitPageToLoadTime());
		
		switch (operationType) {
		case CostantiDB.CREATE:
			selenium.click("//input[@value='"+CostantiTestSuite.BOTTONE_ADD+"']");
			selenium.waitForPageToLoad(TestUtil.getWaitPageToLoadTime());
			selenium.type("nomept", pt.getNome());
			break;

		case CostantiDB.UPDATE:
			selenium.click("link="+pt.getNome());
			selenium.waitForPageToLoad(TestUtil.getWaitPageToLoadTime());
			break;
			
		case CostantiDB.DELETE:
			Reporter.log("Cancello Port type "+pt.getNome());
			selenium.check(pt.getNome());
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
			
			//dopo la cancellazione esco
			return selenium;
		}
		
		
		selenium.select("prof", "label="+profiloPT);
		try{
			selenium.waitForPageToLoad(TestUtil.getWaitPageToLoadTime());
		}catch (SeleniumException e) {
			// ignore
		}
		selenium.type("descr", pt.getDescrizione() != null ?
				pt.getDescrizione() : "");
		try{
			selenium.waitForPageToLoad(TestUtil.getWaitPageToLoadTime());
		}catch (SeleniumException e) {
			// ignore
		}
		
		if("ridefinisci".equals(profiloPT)){
			boolean filtroDuplicati= 
				pt.getFiltroDuplicati()!=null && CostantiConfigurazione.ABILITATO.toString().equals(pt.getFiltroDuplicati()) 
				? true  : false;
			
			boolean confermaRicezione = 
				pt.getConfermaRicezione()!=null && CostantiConfigurazione.ABILITATO.toString().equals(pt.getConfermaRicezione()) 
				? true	: false;
			boolean idCollaborazione = 
				pt.getIdCollaborazione()!=null && CostantiConfigurazione.ABILITATO.toString().equals(pt.getIdCollaborazione()) 
				? true  : false;
			boolean consegnaOrdine = 
				pt.getConsegnaInOrdine()!=null && CostantiConfigurazione.ABILITATO.toString().equals(pt.getConsegnaInOrdine()) 
				? true	: false;
			
			String profColl  = pt.getProfiloCollaborazione()!=null ? pt.getProfiloCollaborazione().toString() : "";
			String scadenza  = pt.getScadenza()!=null ? pt.getScadenza() : "";
			
			selenium.select("profcollpt", "label="+profColl);
			try{
				selenium.waitForPageToLoad(TestUtil.getWaitPageToLoadTime());
			}catch (SeleniumException e) {
				// ignore
			}
			
			if(filtroDuplicati)
				selenium.check("filtroduppt");
			else 
				selenium.uncheck("filtroduppt");
			
			if(confermaRicezione)
				selenium.check("confricpt");
			else 
				selenium.uncheck("confricpt");
			
			if(idCollaborazione)
				selenium.check("idcollpt");
			else 
				selenium.uncheck("idcollpt");
			
			if(consegnaOrdine)
				selenium.check("consordpt");
			else
				selenium.uncheck("consordpt");
			
			selenium.type("scadenzapt", scadenza);
		}
		
		selenium.click("//input[@value='"+CostantiTestSuite.BOTTONE_INVIA+"']");
		selenium.waitForPageToLoad(TestUtil.getWaitPageToLoadTime());
		
		assertEquals(pt.getNome(), selenium.getText("link="+pt.getNome()));
		
		return selenium;
	}
	
	private static Selenium CRUDAzionePT(SeleniumClient seleniumClient,Operation azione,int idAccordo,String nomeAcc,int idPT,String nomePT,int totAzioni,boolean validate,int operationType,boolean terminologiaSICA,boolean servizioComposto) throws AssertionError{
		Selenium selenium = seleniumClient.getSeleniumBrowser();
		String webAppContext = seleniumClient.getCtrlstatContext();
		
		String profiloAzione=azione.getProfAzione()!=null ? azione.getProfAzione() : "usa profilo servizio";
		
		if("ridefinito".equals(profiloAzione))profiloAzione="ridefinisci";
		if("default".equals(profiloAzione))profiloAzione="usa profilo servizio";
		
		selenium.open(TestUtil.urlAccordoServizio(webAppContext, "accordiPorttypeList.do?id="+idAccordo+"&nome="+nomeAcc, terminologiaSICA, servizioComposto));
		selenium.click("//a[contains(@href, 'accordiPorttypeOperationsList.do?id="+idPT+"&nomept="+nomePT+"')]");
		selenium.waitForPageToLoad(TestUtil.getWaitPageToLoadTime());
		
		switch (operationType) {
		case CostantiDB.CREATE:
			selenium.click("//input[@value='"+CostantiTestSuite.BOTTONE_ADD+"']");
			selenium.waitForPageToLoad(TestUtil.getWaitPageToLoadTime());
			selenium.type("nomeop", azione.getNome());
			break;

		case CostantiDB.UPDATE:
			selenium.click("link="+azione.getNome());
			selenium.waitForPageToLoad(TestUtil.getWaitPageToLoadTime());
			break;
			
		case CostantiDB.DELETE:
			Reporter.log("Cancello Azione "+azione.getNome());
			selenium.check(azione.getNome());
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
			
			//dopo la cancellazione esco
			return selenium;
		}
		
		
		selenium.select("prof", "label="+profiloAzione);
		try{
			selenium.waitForPageToLoad(TestUtil.getWaitPageToLoadTime());
		}catch (SeleniumException e) {
			// ignore
		}
		
		if("ridefinisci".equals(profiloAzione)){
			boolean filtroDuplicati= 
				azione.getFiltroDuplicati()!=null && CostantiConfigurazione.ABILITATO.toString().equals(azione.getFiltroDuplicati()) 
				? true  : false;
			
			boolean confermaRicezione = 
				azione.getConfermaRicezione()!=null && CostantiConfigurazione.ABILITATO.toString().equals(azione.getConfermaRicezione()) 
				? true	: false;
			boolean idCollaborazione = 
				azione.getIdCollaborazione()!=null && CostantiConfigurazione.ABILITATO.toString().equals(azione.getIdCollaborazione()) 
				? true  : false;
			boolean consegnaOrdine = 
				azione.getConsegnaInOrdine()!=null && CostantiConfigurazione.ABILITATO.toString().equals(azione.getConsegnaInOrdine()) 
				? true	: false;
			
			String profColl  = azione.getProfiloCollaborazione()!=null ? azione.getProfiloCollaborazione().toString() : "";
			String scadenza  = azione.getScadenza()!=null ? azione.getScadenza() : "";
			
			selenium.select("profcollop", "label="+profColl);
			try{
				selenium.waitForPageToLoad(TestUtil.getWaitPageToLoadTime());
			}catch (SeleniumException e) {
				// ignore
			}
			
			if(filtroDuplicati)
				selenium.check("filtrodupop");
			else 
				selenium.uncheck("filtrodupop");
			
			if(confermaRicezione)
				selenium.check("confricop");
			else 
				selenium.uncheck("confricop");
			
			if(idCollaborazione)
				selenium.check("idcollop");
			else 
				selenium.uncheck("idcollop");
			
			if(consegnaOrdine)
				selenium.check("consordop");
			else
				selenium.uncheck("consordop");
			
			selenium.type("scadenzaop", scadenza);
		}
		
		// correlazione asincrona
		if(azione.getCorrelataServizio()!=null && !nomePT.equals(azione.getCorrelataServizio())){
			selenium.select("servcorr", "label="+azione.getCorrelataServizio());
			try{
				selenium.waitForPageToLoad(TestUtil.getWaitPageToLoadTime());
			}catch (SeleniumException e) {
				// ignore
			}
		}
		if(azione.getCorrelata()!=null){
			selenium.select("azicorr", "label="+azione.getCorrelata());
			try{
				selenium.waitForPageToLoad(TestUtil.getWaitPageToLoadTime());
			}catch (SeleniumException e) {
				// ignore
			}
		}
		
		
		selenium.click("//input[@value='"+CostantiTestSuite.BOTTONE_INVIA+"']");
		selenium.waitForPageToLoad(TestUtil.getWaitPageToLoadTime());
		
		assertEquals(azione.getNome(), selenium.getText("link="+azione.getNome()));
		
		return selenium;
	}
}


