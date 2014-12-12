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


import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.testsuite.core.CostantiTestSuite;
import org.openspcoop2.testsuite.gui.commons.SeleniumClient;
import org.openspcoop2.testsuite.gui.commons.TestUtil;

import com.thoughtworks.selenium.Selenium;

/**
 * Libreria per Soggetto
 *
 * @author Stefano Corallo <corallo@link.it>
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class Soggetto {

	private static int totDeleted;
	
	public static void create(SeleniumClient seleniumClient,org.openspcoop2.core.config.Soggetto soggetto, String portaDom, boolean validate) throws AssertionError{
		org.openspcoop2.core.registry.Soggetto ss = new org.openspcoop2.core.registry.Soggetto();
	    ss.setSuperUser(soggetto.getSuperUser());
	    ss.setTipo(soggetto.getTipo());
	    ss.setNome(soggetto.getNome());
	    ss.setIdentificativoPorta(soggetto.getIdentificativoPorta());
	    ss.setDescrizione(soggetto.getDescrizione());
	    ss.setPortaDominio(portaDom);
	    create(seleniumClient, ss, validate);
	}

	public static void create(SeleniumClient seleniumClient,org.openspcoop2.core.registry.Soggetto soggetto,boolean validate) throws AssertionError{
		Selenium selenium_browser = seleniumClient.getSeleniumBrowser();
		String webAppContext = seleniumClient.getCtrlstatContext();
		
		String tipo = soggetto.getTipo();
		String nome = soggetto.getNome();
		String descrizione = soggetto.getDescrizione()!=null ? soggetto.getDescrizione() : "";
		String portaDominio = soggetto.getIdentificativoPorta()!=null ? soggetto.getIdentificativoPorta() : "";
		String pdd = soggetto.getPortaDominio()!=null ? soggetto.getPortaDominio() : "-";
		String codiceIPA = soggetto.getCodiceIpa()!=null ? soggetto.getCodiceIpa() : "";
		
		selenium_browser.open("/"+webAppContext+"/soggettiAdd.do");
		selenium_browser.select("tipoprov", "label="+tipo);
		selenium_browser.type("nomeprov", nome);
		selenium_browser.type("descr", descrizione);
		selenium_browser.type("portadom", portaDominio );
		selenium_browser.select("pdd", "label="+pdd);
		if(soggetto.getVersioneProtocollo()!=null)
			selenium_browser.select("profilo", "label="+soggetto.getVersioneProtocollo());
		if(codiceIPA!=null)
			selenium_browser.type("codice_ipa", codiceIPA );
		selenium_browser.click("//input[@value='"+CostantiTestSuite.BOTTONE_INVIA+"']");
		selenium_browser.waitForPageToLoad(TestUtil.getWaitPageToLoadTime());
		assertEquals(selenium_browser.getText("link="+(tipo+"/"+nome)), (tipo+"/"+nome));
				
		//connettore
		
		
		//recuper id del soggetto
		String urlProvidersChange = selenium_browser.getAttribute("link="+(tipo+"/"+nome)+"@href");
		String idSoggetto = urlProvidersChange.substring("soggettiChange.do?id=".length());
		String urlConnettoreChange= "soggettiEndPoint.do?id="+idSoggetto;
				
		selenium_browser.open("/"+webAppContext+"/"+urlConnettoreChange);
		
		TestUtil.CRUDConnettore(selenium_browser, soggetto.getConnettore(), CostantiDB.CREATE, soggetto,webAppContext,true);
		
//		TipiConnettore tipoConnettore = soggetto.getConnettore() != null 
//		?TipiConnettore.valueOf(soggetto.getConnettore().getTipo().toUpperCase()) 
//		: TipiConnettore.DISABILITATO;
//		Map<String, String> propConnettore = null;
//		
//		switch (tipoConnettore) {
//		case HTTP:
//			propConnettore=soggetto.getConnettore().getProperties();
//			selenium_browser.select("endpointtype", "label="+tipoConnettore.getNome());
//			selenium_browser.waitForPageToLoad(TestUtil.getWaitPageToLoadTime());
//			selenium_browser.type("url", propConnettore.get(CostantiDB.CONNETTORE_HTTP_LOCATION));
//			break;
//			
//		case JMS:
//			propConnettore=soggetto.getConnettore().getProperties();
//			selenium_browser.select("endpointtype", "label="+tipoConnettore.getNome());
//			selenium_browser.waitForPageToLoad(TestUtil.getWaitPageToLoadTime());
//			selenium_browser.type("nome", propConnettore.get(CostantiDB.CONNETTORE_JMS_NOME));
//			selenium_browser.select("tipo", "label="+propConnettore.get(CostantiDB.CONNETTORE_JMS_TIPO));
//			selenium_browser.type("user", propConnettore.get(CostantiDB.CONNETTORE_JMS_USER));
//			selenium_browser.type("password", propConnettore.get(CostantiDB.CONNETTORE_JMS_PWD));
//			selenium_browser.type("initcont", propConnettore.get(CostantiDB.CONNETTORE_JMS_CONTEXT_JAVA_NAMING_FACTORY_INITIAL));
//			selenium_browser.type("urlpgk", propConnettore.get(CostantiDB.CONNETTORE_JMS_CONTEXT_JAVA_NAMING_FACTORY_URL_PKG));
//			selenium_browser.type("provurl", propConnettore.get(CostantiDB.CONNETTORE_JMS_CONTEXT_JAVA_NAMING_PROVIDER_URL));
//			selenium_browser.type("connfact", propConnettore.get(CostantiDB.CONNETTORE_JMS_CONNECTION_FACTORY));
//			selenium_browser.select("sendas", "label="+propConnettore.get(CostantiDB.CONNETTORE_JMS_SEND_AS));
//			selenium_browser.waitForPageToLoad(TestUtil.getWaitPageToLoadTime());
//			break;
//			
//		case NULL:
//			selenium_browser.select("endpointtype", "label="+tipoConnettore.getNome());
//			selenium_browser.waitForPageToLoad(TestUtil.getWaitPageToLoadTime());
//			break;
//			
//		case NULLECHO:
//			selenium_browser.select("endpointtype", "label="+tipoConnettore.getNome());
//			selenium_browser.waitForPageToLoad(TestUtil.getWaitPageToLoadTime());
//			break;
//			
//		case DISABILITATO:
//			//disabilitato e' l'opzioni di default selezionata
//			//non faccio la wait
//			selenium_browser.select("endpointtype", "label="+tipoConnettore.getNome());
//			break;
//		}
				
		selenium_browser.click("//input[@value='"+CostantiTestSuite.BOTTONE_INVIA+"']");
		selenium_browser.waitForPageToLoad(TestUtil.getWaitPageToLoadTime());
	}
	
	public static void update(SeleniumClient seleniumClient,org.openspcoop2.core.config.Soggetto soggetto, String portaDom, boolean validate) throws AssertionError{
		org.openspcoop2.core.registry.Soggetto ss = new org.openspcoop2.core.registry.Soggetto();
	    ss.setSuperUser(soggetto.getSuperUser());
	    ss.setOldNomeForUpdate(soggetto.getOldNomeForUpdate());
	    ss.setOldTipoForUpdate(soggetto.getOldTipoForUpdate());
	    ss.setTipo(soggetto.getTipo());
	    ss.setNome(soggetto.getNome());
	    ss.setIdentificativoPorta(soggetto.getIdentificativoPorta());
	    ss.setDescrizione(soggetto.getDescrizione());
	    ss.setPortaDominio(portaDom);
	    update(seleniumClient, ss, validate);
	}

	public static void update(SeleniumClient seleniumClient,org.openspcoop2.core.registry.Soggetto soggetto,boolean validate) throws AssertionError{
		Selenium selenium_browser = seleniumClient.getSeleniumBrowser();
		String webAppContext = seleniumClient.getCtrlstatContext();
		String tipo = soggetto.getTipo();
		String nome = soggetto.getNome();
		String descrizione = soggetto.getDescrizione()!=null ? soggetto.getDescrizione() : "";
		String identificativoPorta = soggetto.getIdentificativoPorta()!=null ? soggetto.getIdentificativoPorta() : "";
		String pdd = soggetto.getPortaDominio()!=null ? soggetto.getPortaDominio() : "-";
		String oldTipo = soggetto.getOldTipoForUpdate()!=null ? soggetto.getOldTipoForUpdate() : soggetto.getTipo();
		String oldNome = soggetto.getOldNomeForUpdate()!=null ? soggetto.getOldNomeForUpdate() : soggetto.getNome();
		String codiceIPA = soggetto.getCodiceIpa()!=null ? soggetto.getCodiceIpa() : "";
		
		selenium_browser.open("/"+webAppContext+"/soggettiList.do");
		selenium_browser.click("link="+(oldTipo+"/"+oldNome));
		selenium_browser.waitForPageToLoad(TestUtil.getWaitPageToLoadTime());
		selenium_browser.select("tipoprov", "label="+tipo);
		selenium_browser.type("nomeprov", nome);
		selenium_browser.type("descr", descrizione);
		selenium_browser.type("portadom", identificativoPorta );
		selenium_browser.type("pdd", pdd);
		if(soggetto.getVersioneProtocollo()!=null)
			selenium_browser.select("profilo", "label="+soggetto.getVersioneProtocollo());
		if(codiceIPA!=null)
			selenium_browser.type("codice_ipa", codiceIPA );
		selenium_browser.click("//input[@value='"+CostantiTestSuite.BOTTONE_INVIA+"']");
		selenium_browser.waitForPageToLoad(TestUtil.getWaitPageToLoadTime());
		assertEquals(selenium_browser.isTextPresent(pdd),true);
		
		//connettore
		
		
		//recuper id del soggetto
		String urlProvidersChange = selenium_browser.getAttribute("link="+(tipo+"/"+nome)+"@href");
		String idSoggetto = urlProvidersChange.substring("soggettiChange.do?id=".length());
		String urlConnettoreChange= "soggettiEndPoint.do?id="+idSoggetto;
				
		selenium_browser.open("/"+webAppContext+"/"+urlConnettoreChange);
		
		TestUtil.CRUDConnettore(selenium_browser, soggetto.getConnettore(), CostantiDB.UPDATE, soggetto,webAppContext,true);
		selenium_browser.click("//input[@value='"+CostantiTestSuite.BOTTONE_INVIA+"']");
		selenium_browser.waitForPageToLoad(TestUtil.getWaitPageToLoadTime());
	}
	
	public static void delete(SeleniumClient seleniumClient,org.openspcoop2.core.config.Soggetto soggetto,int totSoggetti,boolean validate) throws AssertionError{
		org.openspcoop2.core.registry.Soggetto ss = new org.openspcoop2.core.registry.Soggetto();
	    ss.setSuperUser(soggetto.getSuperUser());
	    ss.setTipo(soggetto.getTipo());
	    ss.setNome(soggetto.getNome());
	    delete(seleniumClient, ss, totSoggetti, validate);
	}

	public static void delete(SeleniumClient seleniumClient,org.openspcoop2.core.registry.Soggetto soggetto,int totSoggetti,boolean validate) throws AssertionError{
		Selenium selenium_browser = seleniumClient.getSeleniumBrowser();
		String webAppContext = seleniumClient.getCtrlstatContext();
		selenium_browser.open("/"+webAppContext+"/soggettiList.do");		
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
		
		int totLista = TestUtil.getTotEntryLista(toParse); //il totale dei soggetti rimanenti che mi indica l'interfaccia
		
		incrDeleted();//ho cancellato una entry
		
		//il totSoggetti - getTotDeleted deve essere = a totLista ovvero i soggetti rimanenti
		assertEquals( (totSoggetti-getTotDeleted())==totLista, true);
	}
	
	public static void deleteAll(SeleniumClient seleniumClient,boolean validate) throws AssertionError
	{
		assertNotNull(seleniumClient,"Il client seleniun e' null.");
		//Note: si effettua la cancellazione dell'intera lista.
		String webAppContext = seleniumClient.getCtrlstatContext();
		TestUtil.deleteAll(seleniumClient, webAppContext, "soggettiList", validate);
	}
	
	public static void list(SeleniumClient seleniumClient,org.openspcoop2.core.registry.Soggetto soggetto,boolean validate) throws AssertionError{
		Selenium selenium_browser = seleniumClient.getSeleniumBrowser();
		String webAppContext = seleniumClient.getCtrlstatContext();
		String tipo = soggetto.getTipo();
		String nome = soggetto.getNome();
		
		selenium_browser.open("/"+webAppContext+"/soggettiList.do");
		assertEquals(selenium_browser.getText("link="+(tipo+"/"+nome)), (tipo+"/"+nome));
	}
	
	static synchronized void incrDeleted () {
		totDeleted++;
	}
	
	static synchronized int getTotDeleted(){
		return totDeleted;
	}
}


