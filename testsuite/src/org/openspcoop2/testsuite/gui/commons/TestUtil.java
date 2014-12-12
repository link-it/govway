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


package org.openspcoop2.testsuite.gui.commons;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.openspcoop2.core.config.Connettore;
import org.openspcoop2.core.config.Credenziali;
import org.openspcoop2.core.config.OpenspcoopAppender;
import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.config.MessageSecurity;
import org.openspcoop2.core.config.MessageSecurityFlow;
import org.openspcoop2.core.config.MessageSecurityFlowParameter;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.constants.TipiConnettore;
import org.openspcoop2.core.registry.IdSoggetto;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.AccordoServizioParteComuneServizioCompostoServizioComponente;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.Documento;
import org.openspcoop2.core.registry.Fruitore;
import org.openspcoop2.core.registry.Property;
import org.openspcoop2.core.registry.Servizio;
import org.openspcoop2.core.registry.Soggetto;
import org.openspcoop2.core.registry.constants.RuoliDocumento;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.testsuite.core.CostantiTestSuite;
import org.openspcoop2.testsuite.gui.constant.TestProperties;
import org.openspcoop2.utils.resources.FileSystemUtilities;
import org.testng.Assert;
import org.testng.Reporter;

import com.thoughtworks.selenium.Selenium;
import com.thoughtworks.selenium.SeleniumException;

/**
 * Classe di utilita per i test
 * @author Stefano Corallo <corallo@link.it>
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class TestUtil {

	private static Logger log;
	private static String waitTime;
	
	/**
	 * Legge le proprieta dal file indicato
	 * @param fileName
	 * @return le proprieta, null altrimenti
	 */
	public static Properties loadConfigProperties(String fileName){
		
		if(fileName==null || "".equals(fileName)) return null;
		
		try{
			InputStream ios = TestUtil.class.getResourceAsStream(fileName);
			Properties prop = new Properties();
			if(ios == null) return null;
			prop.load(ios);
			
			return prop;
		}catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		
	}
	
	public static Logger getLog() throws AssertionError{
		
		if(TestUtil.log==null){
			Properties prop=TestUtil.loadConfigProperties(TestProperties._config_file_name.toString());
			
			Assert.assertNotNull(prop);
			
			//logger
			
			String fileName=prop.getProperty(TestProperties.logger_file_name.toString());
			String logName=prop.getProperty(TestProperties.logger_name.toString());
				
			if(fileName==null || "".equals(fileName)) fileName=TestProperties._default_logger_file_name.toString();
			if(logName==null || "".equals(logName)) logName=TestProperties._default_logger_name.toString();
			
			PropertyConfigurator.configure(TestUtil.class.getResource(fileName));
							
			Logger log= Logger.getLogger(logName);
			
			Assert.assertNotNull(log);
			
			Reporter.log("Informazioni su Logger caricate dal file ["+fileName+"] LOGGER_NAME="+logName);
		}
		
		return TestUtil.log;
	}
	
	public static String getWaitPageToLoadTime(){
		
		if(waitTime==null){
			Properties prop=TestUtil.loadConfigProperties(TestProperties._config_file_name.toString());
			assertNotNull(prop,"Proprieta' di configurazione non caricate.");
			waitTime=prop.getProperty(TestProperties.wait_time.toString());
			if(waitTime==null || "".equals(waitTime))waitTime=TestProperties._WAIT_PAGE_TO_LOAD.toString();
		}
		return waitTime;

	}
		
	public static int getTotEntryLista(String toParse){
		
		//Soggetti > Elenco [1-5] su 5 
		if(toParse==null || "".equals(toParse)) return -1;
		
		//if(!toParse.matches("(.*)> Elenco \\[(.*)-(.*)\\] su (.*)")){
		if(!toParse.matches(".*\\[(.*)-(.*)\\] su (.*)")){
			Reporter.log("La Stringa ["+toParse+"] non e' stringa di Elenco valida");
			return -1;
		}
		
		try{
			String tmp=toParse.substring(toParse.indexOf("su")+2);
			return Integer.parseInt(tmp.trim());
		}catch (Exception e) {
			TestUtil.getLog().error("Errore durante il parsing della Stringa ["+toParse+"] per il recuper del numero di entry nella lista.");
			return -1;
		}
	}
	
	public static void deleteAll(SeleniumClient seleniumClient,String webAppContext,String pageList,boolean validate) throws AssertionError
	{
		Assert.assertNotNull(seleniumClient,"Il client seleniun e' null.");
		
		//Note: si effettua la cancellazione dell'intera lista.
		Selenium selenium = seleniumClient.getSeleniumBrowser();
		selenium.open("/"+webAppContext+"/"+pageList+".do");
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
		
		Assert.assertEquals(totLista, 0);
		
	}
	
	public static void deleteAllSublist(SeleniumClient seleniumClient,String webAppContext,String pageList,boolean validate) throws AssertionError
	{
		Assert.assertNotNull(seleniumClient,"Il client seleniun e' null.");
		
		//Note: si effettua la cancellazione dell'intera lista.
		Selenium selenium = seleniumClient.getSeleniumBrowser();
		selenium.open("/"+webAppContext+"/"+pageList);
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
		
		Assert.assertEquals(totLista, 0);
		
	}
	
	public static Selenium CRUDServiziApplicativi(SeleniumClient seleniumClient,
		boolean validate,
		int operationType,
		int idPorta,
		String nomePorta,
		List<ServizioApplicativo> serviziApplicativi,
		boolean isPortaDelegataSIL) throws AssertionError{
		
		Selenium selenium = seleniumClient.getSeleniumBrowser();
		String webAppContext = seleniumClient.getPddContext();
		String pageName = isPortaDelegataSIL ? "porteDelegateSilList.do" : "porteAppSilList.do";
		
		switch (operationType) {
		case CostantiDB.CREATE:
		case CostantiDB.UPDATE:
			
			if(serviziApplicativi!=null && serviziApplicativi.size()>0)
			{
				selenium.open("/"+webAppContext+"/"+pageName+"?id="+idPorta+"&nome="+nomePorta);
				for (int i = 0; i < serviziApplicativi.size(); i++) {
					ServizioApplicativo sa = serviziApplicativi.get(i);
					selenium.click("//input[@value='"+CostantiTestSuite.BOTTONE_ADD+"']");
					selenium.waitForPageToLoad(TestUtil.getWaitPageToLoadTime());
					selenium.select("sil", "label="+sa.getNome());
					selenium.click("//input[@value='"+CostantiTestSuite.BOTTONE_INVIA+"']");
					selenium.waitForPageToLoad(TestUtil.getWaitPageToLoadTime());
					
					Assert.assertEquals(selenium.isTextPresent(sa.getNome()), true);				
				}
			}
			
			break;
			
		case CostantiDB.DELETE:
			//cancello tutti i servizi applicativi associati a questa porta delegata
			selenium.open("/"+webAppContext+"/"+pageName+"?id="+idPorta+"&nome="+nomePorta);
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
			}catch(SeleniumException e){
				//ignore
			}
			
			String toParse = selenium.getText("xpath=//span[@class='history']");
			int totLista = TestUtil.getTotEntryLista(toParse); 
			Assert.assertEquals(totLista, 0);
			
			break;
			
		}
		
		return selenium;
		
	}
	
	public static Selenium CRUDServiziApplicativi(SeleniumClient seleniumClient,
		boolean validate,
		int operationType,
		int idPorta,
		String nomePorta,
		List<ServizioApplicativo> serviziApplicativi,
		Long idSogg,
		boolean isPortaDelegataSIL) throws AssertionError{
		
		Selenium selenium = seleniumClient.getSeleniumBrowser();
		String webAppContext = seleniumClient.getCtrlstatContext();
		String pageName = isPortaDelegataSIL ? "porteDelegateServizioApplicativoList.do" : "porteAppServizioApplicativoList.do";
		
		switch (operationType) {
		case CostantiDB.CREATE:
		case CostantiDB.UPDATE:
			
			if(serviziApplicativi!=null && serviziApplicativi.size()>0)
			{
				selenium.open("/"+webAppContext+"/"+pageName+"?idsogg="+idSogg+"&id="+idPorta);
				for (int i = 0; i < serviziApplicativi.size(); i++) {
					ServizioApplicativo sa = serviziApplicativi.get(i);
					selenium.click("//input[@value='"+CostantiTestSuite.BOTTONE_ADD+"']");
					selenium.waitForPageToLoad(TestUtil.getWaitPageToLoadTime());
					selenium.select("servizioApplicativo", "label="+sa.getNome());
					selenium.click("//input[@value='"+CostantiTestSuite.BOTTONE_INVIA+"']");
					selenium.waitForPageToLoad(TestUtil.getWaitPageToLoadTime());
					
					Assert.assertEquals(selenium.isTextPresent(sa.getNome()), true);				
				}
			}
			
			break;
			
		case CostantiDB.DELETE:
			//cancello tutti i servizi applicativi associati a questa porta delegata
			selenium.open("/"+webAppContext+"/"+pageName+"?idsogg="+idSogg+"&id="+idPorta);
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
			}catch(SeleniumException e){
				//ignore
			}
			
			String toParse = selenium.getText("xpath=//span[@class='history']");
			int totLista = TestUtil.getTotEntryLista(toParse); 
			Assert.assertEquals(totLista, 0);
			
			break;
			
		}
		
		return selenium;
		
	}
		
	public static Selenium CRUDMessageSecurity(SeleniumClient seleniumClient,
			boolean validate,
			int operationType,
			int idPorta,
			String nomePorta,
			String statoMessageSecurity,
			MessageSecurity messageSecurity,
			boolean isPortaDelegataMessageSecurity) throws AssertionError{
		
		Selenium selenium = seleniumClient.getSeleniumBrowser();
		String webAppContext = seleniumClient.getPddContext();
		String pageName = isPortaDelegataMessageSecurity ? "porteDelegateWS.do" : "porteAppWS.do";
		
		selenium.open("/"+webAppContext+"/"+pageName+"?id="+idPorta+"&nome="+nomePorta);
		
		if(CostantiConfigurazione.ABILITATO.toString().equals(statoMessageSecurity)){
			
			selenium.select("messageSecurity", "label=abilitato");
			selenium.click("//input[@value='"+CostantiTestSuite.BOTTONE_INVIA+"']");
			selenium.waitForPageToLoad(TestUtil.getWaitPageToLoadTime());
			
			//request flow
			if(messageSecurity.getRequestFlow()!=null){
				MessageSecurityFlow wsFlow = messageSecurity.getRequestFlow();
				if(wsFlow.sizeParameterList()>0){
					selenium.open("/"+webAppContext+"/"+pageName+"?id="+idPorta+"&nome="+nomePorta);
					selenium.click("link=Request Flow");
					selenium.waitForPageToLoad(TestUtil.getWaitPageToLoadTime());
					TestUtil.CRUDWSRequestFlow(selenium, wsFlow.getParameterList(), CostantiDB.DELETE);
					TestUtil.CRUDWSRequestFlow(selenium, wsFlow.getParameterList(), operationType);
				}
			}
			//response flow
			if(messageSecurity.getResponseFlow()!=null){
				MessageSecurityFlow wsFlow = messageSecurity.getResponseFlow();
				if(wsFlow.sizeParameterList()>0){
					selenium.open("/"+webAppContext+"/"+pageName+"?id="+idPorta+"&nome="+nomePorta);
					selenium.click("link=Response Flow");
					selenium.waitForPageToLoad(TestUtil.getWaitPageToLoadTime());
					TestUtil.CRUDWSResponseFlow(selenium, wsFlow.getParameterList(), CostantiDB.DELETE);
					TestUtil.CRUDWSResponseFlow(selenium, wsFlow.getParameterList(), operationType);
				}
			}
		}else{
			selenium.select("messageSecurity", "label=disabilitato");
			selenium.click("//input[@value='"+CostantiTestSuite.BOTTONE_INVIA+"']");
			selenium.waitForPageToLoad(TestUtil.getWaitPageToLoadTime());
		}
		
		return selenium;
	}
	
	public static Selenium CRUDMessageSecurity(SeleniumClient seleniumClient,
			boolean validate,
			int operationType,
			int idPorta,
			String nomePorta,
			String statoMessageSecurity,
			MessageSecurity messageSecurity,
			Long idSogg,
			boolean isPortaDelegataMessageSecurity) throws AssertionError{
		
		Selenium selenium = seleniumClient.getSeleniumBrowser();
		String webAppContext = seleniumClient.getCtrlstatContext();
		String pageName = isPortaDelegataMessageSecurity ? "porteDelegateWS.do" : "porteAppWS.do";
		
		selenium.open("/"+webAppContext+"/"+pageName+"?idsogg="+idSogg+"&id="+idPorta);
		
		if(CostantiConfigurazione.ABILITATO.toString().equals(statoMessageSecurity)){
			
			selenium.select("messageSecurity", "label=abilitato");
			selenium.click("//input[@value='"+CostantiTestSuite.BOTTONE_INVIA+"']");
			selenium.waitForPageToLoad(TestUtil.getWaitPageToLoadTime());
			
			//request flow
			if(messageSecurity.getRequestFlow()!=null){
				MessageSecurityFlow messageSecurityFlow = messageSecurity.getRequestFlow();
				if(messageSecurityFlow.sizeParameterList()>0){
					String pageNameWS = isPortaDelegataMessageSecurity ? "porteDelegateWSRequestList.do" : "porteAppWSRequestList.do";
					selenium.open("/"+webAppContext+"/"+pageNameWS+"?id="+idPorta+"&idsogg="+idSogg);
					//selenium.click("link=Request Flow");
					selenium.waitForPageToLoad(TestUtil.getWaitPageToLoadTime());
					TestUtil.CRUDWSRequestFlow(selenium, messageSecurityFlow.getParameterList(), CostantiDB.DELETE);
					TestUtil.CRUDWSRequestFlow(selenium, messageSecurityFlow.getParameterList(), operationType);
				}
			}
			//response flow
			if(messageSecurity.getResponseFlow()!=null){
				MessageSecurityFlow messageSecurityFlow = messageSecurity.getResponseFlow();
				if(messageSecurityFlow.sizeParameterList()>0){
					String pageNameWS = isPortaDelegataMessageSecurity ? "porteDelegateWSResponseList.do" : "porteAppWSResponseList.do";
					selenium.open("/"+webAppContext+"/"+pageNameWS+"?id="+idPorta+"&idsogg="+idSogg);
					//selenium.click("link=Response Flow");
					selenium.waitForPageToLoad(TestUtil.getWaitPageToLoadTime());
					TestUtil.CRUDWSResponseFlow(selenium, messageSecurityFlow.getParameterList(), CostantiDB.DELETE);
					TestUtil.CRUDWSResponseFlow(selenium, messageSecurityFlow.getParameterList(), operationType);
				}
			}
		}else{
			selenium.select("messageSecurity", "label=disabilitato");
			selenium.click("//input[@value='"+CostantiTestSuite.BOTTONE_INVIA+"']");
			selenium.waitForPageToLoad(TestUtil.getWaitPageToLoadTime());
		}
		
		return selenium;
	}
	
	private static void CRUDWSRequestFlow(Selenium selenium,List<MessageSecurityFlowParameter> requestFlows,int operationType) throws AssertionError{
		
		switch (operationType) {
		case CostantiDB.CREATE:
		case CostantiDB.UPDATE:
			for (int i = 0; i < requestFlows.size(); i++) {
				MessageSecurityFlowParameter param = requestFlows.get(i);
				String nome = param.getNome();
				String valore = param.getValore();
				selenium.click("//input[@value='"+CostantiTestSuite.BOTTONE_ADD+"']");
				selenium.waitForPageToLoad(TestUtil.getWaitPageToLoadTime());
				selenium.type("nome", nome);
				selenium.type("valore", valore);
				selenium.click("//input[@value='"+CostantiTestSuite.BOTTONE_INVIA+"']");
				selenium.waitForPageToLoad(TestUtil.getWaitPageToLoadTime());
				Assert.assertEquals(selenium.getText("link="+nome), nome);
			}
			
			break;

		case CostantiDB.DELETE:
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
			}catch(SeleniumException e){
				//ignore
			}
			String toParse = selenium.getText("xpath=//span[@class='history']");
			
			int totLista = TestUtil.getTotEntryLista(toParse); 
			
			Assert.assertEquals(totLista, 0);
			break;
		}
		
	}
	
	private static void CRUDWSResponseFlow(Selenium selenium,List<MessageSecurityFlowParameter> responseFlow,int operationType) throws AssertionError{
		
		switch (operationType) {
		case CostantiDB.CREATE:
		case CostantiDB.UPDATE:
			for (int i = 0; i < responseFlow.size(); i++) {
				MessageSecurityFlowParameter param = responseFlow.get(i);
				String nome = param.getNome();
				String valore = param.getValore();
				selenium.click("//input[@value='"+CostantiTestSuite.BOTTONE_ADD+"']");
				selenium.waitForPageToLoad(TestUtil.getWaitPageToLoadTime());
				selenium.type("nome", nome);
				selenium.type("valore", valore);
				selenium.click("//input[@value='"+CostantiTestSuite.BOTTONE_INVIA+"']");
				selenium.waitForPageToLoad(TestUtil.getWaitPageToLoadTime());
				Assert.assertEquals(selenium.getText("link="+nome), nome);
			}
			
			break;

		case CostantiDB.DELETE:
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
			}catch(SeleniumException e){
				//ignore
			}
			String toParse = selenium.getText("xpath=//span[@class='history']");
			
			int totLista = TestUtil.getTotEntryLista(toParse); 
			
			Assert.assertEquals(totLista, 0);
			break;
		}
		
	}
	
	public static <T> Selenium CRUDConnettore(Selenium selenium_browser,T connettore,int operationType,Object oggetto,String webAppContext,boolean ctrlstat) throws AssertionError{
		
		Connettore con = new Connettore();
		boolean isRegistro = false;
		org.openspcoop2.core.registry.Connettore conR = new org.openspcoop2.core.registry.Connettore();
		if(connettore instanceof Connettore){
			con = (Connettore) connettore;
		}
		if(connettore instanceof org.openspcoop2.core.registry.Connettore){
			isRegistro = true;
			conR = (org.openspcoop2.core.registry.Connettore) connettore;
		}
		
		String tipo = null;
		if(isRegistro){
			tipo = conR!=null ? conR.getTipo() : null;
		}else{
			tipo = con!=null ? con.getTipo() : null;
		}
		
		boolean custom = false;
		if(isRegistro){
			custom = conR!=null ? conR.isCustom() : false;
		}else{
			custom = con!=null ? con.isCustom() : false;
		}
		
		Reporter.log("Check connettore custom["+custom+"] di tipo ["+tipo+"]");
		if (custom && !tipo.equals(TipiConnettore.HTTPS.toString())) {
					
			Reporter.log("Gestione connettore custom["+custom+"] di tipo ["+tipo+"]");
			
			//	Map<String, String> propConnettore =
			//	isRegistro ? conR.getProperties() : con.getProperties();
			selenium_browser.select("endpointtype", "label=custom");
			try{
				selenium_browser.waitForPageToLoad(TestUtil.getWaitPageToLoadTime());
			}catch(SeleniumException e){
				//ignore
			}
			selenium_browser.type("tipoconn", isRegistro ? conR.getTipo() : con.getTipo());
			
			Reporter.log("Gestione connettore custom["+custom+"] di tipo ["+tipo+"], invia...");
			selenium_browser.click("//input[@value='"+CostantiTestSuite.BOTTONE_INVIA+"']");
			try{
				selenium_browser.waitForPageToLoad(TestUtil.getWaitPageToLoadTime());
			}catch(SeleniumException e){
				//ignore
			}
			
			// Riedito pagina connettore
			Reporter.log("Gestione connettore custom["+custom+"] di tipo ["+tipo+"], riedito pagina...");
			if(oggetto instanceof Soggetto){
				Soggetto s = (Soggetto) oggetto;
				String urlConnettoreChange=null;
				if(ctrlstat){
					String urlProvidersChange = selenium_browser.getAttribute("link="+(s.getTipo()+"/"+s.getNome())+"@href");
					String idSoggetto = urlProvidersChange.substring("soggettiChange.do?id=".length());
					urlConnettoreChange= "soggettiEndPoint.do?id="+idSoggetto;
				}else{
					String urlProvidersChange = selenium_browser.getAttribute("link="+(s.getTipo()+"/"+s.getNome())+"@href");
					String idSoggetto = urlProvidersChange.substring("providersChange.do?id=".length());
					urlConnettoreChange= "providersEndPoint.do?id="+idSoggetto;		
				}
				String url = "/"+webAppContext+"/"+urlConnettoreChange;
				Reporter.log("Gestione connettore custom["+custom+"] di tipo ["+tipo+"], riedito pagina Soggetto ["+url+"]...");
				selenium_browser.open(url);
			}
			else if( (oggetto instanceof AccordoServizioParteSpecifica) && CostantiDB.UPDATE==operationType){
				AccordoServizioParteSpecifica asps = (AccordoServizioParteSpecifica) oggetto;
				Servizio s = asps.getServizio();
				String provider = s.getTipoSoggettoErogatore()+"/"+s.getNomeSoggettoErogatore();
				String accordoServParteSpecifica = asps.getNome();
				String url = "link="+(provider+":"+accordoServParteSpecifica);
				Reporter.log("Gestione connettore custom["+custom+"] di tipo ["+tipo+"], riedito pagina servizio ["+url+"]...");
				selenium_browser.click(url);
			}
			else if( (oggetto instanceof Fruitore)){
				Fruitore f = (Fruitore) oggetto;
				String url = "link="+f.getTipo()+"/"+f.getNome();
				Reporter.log("Gestione connettore custom["+custom+"] di tipo ["+tipo+"], riedito pagina fruitore ["+url+"]...");
				selenium_browser.click(url);
			}
			else if( (oggetto instanceof ServizioApplicativo) ){
				ServizioApplicativo sa = (ServizioApplicativo) oggetto;
				String url = null;
				if(sa.getInvocazioneServizio()!=null && sa.getInvocazioneServizio().getConnettore()!=null && 
						sa.getInvocazioneServizio().getConnettore().getId()==con.getId()){
					if(ctrlstat){
						url = "/"+webAppContext+"/servizioApplicativoEndPoint.do?idsil="+sa.getId()+"&idprovidersa="+sa.getIdSoggetto()+"&nomeservizioApplicativo="+sa.getNome();
						
					}else{
						url = "/"+webAppContext+"/silEndPoint.do?idsil="+sa.getId();
					}
					Reporter.log("Gestione connettore custom["+custom+"] di tipo ["+tipo+"], riedito pagina sa invocazione servizio ["+url+"]...");
					selenium_browser.open(url);
				}
				else{
					if(ctrlstat){
						url = "/"+webAppContext+"/servizioApplicativoEndPointRisposta.do?idsil="+sa.getId()+"&idprovidersa="+sa.getIdSoggetto()+"&nomeservizioApplicativo="+sa.getNome();
					}else{
						url = "/"+webAppContext+"/silEndPointRisposta.do?idsil="+sa.getId();
					}
					Reporter.log("Gestione connettore custom["+custom+"] di tipo ["+tipo+"], riedito pagina sa risposta asincrona ["+url+"]...");
					selenium_browser.open(url);
				}
			}
			try{
				selenium_browser.waitForPageToLoad(TestUtil.getWaitPageToLoadTime());
			}catch(SeleniumException e){
				//ignore
			}
						
			// ITERARE SULLE PROPRIETA'
			Reporter.log("Gestione connettore custom["+custom+"] di tipo ["+tipo+"], proprieta...");
			int sizeProprieta = 0;
			if(isRegistro){
				sizeProprieta = conR.sizePropertyList();
			}else{
				sizeProprieta = con.sizePropertyList();
			}
			
			if(sizeProprieta>0){
				if(ctrlstat)
					selenium_browser.click("link=Proprietà(0)");
				else
					selenium_browser.click("link=Proprietà");
				try{
					selenium_browser.waitForPageToLoad(TestUtil.getWaitPageToLoadTime());
				}catch(SeleniumException e){
					//ignore
				}
			}
			
			for(int i=0; i<sizeProprieta;i++){
				
				Reporter.log("Gestione connettore custom["+custom+"] di tipo ["+tipo+"], proprieta["+i+"] ...");
				
				selenium_browser.click("//input[@value='"+CostantiTestSuite.BOTTONE_ADD+"']");
				try{
					selenium_browser.waitForPageToLoad(TestUtil.getWaitPageToLoadTime());
				}catch(SeleniumException e){
					//ignore
				}
				
				if(isRegistro){
					Property cp =  conR.getProperty(i);
					selenium_browser.type("nome", cp.getNome());
					selenium_browser.type("valore", cp.getValore());
				}
				else{
					org.openspcoop2.core.config.Property cp = con.getProperty(i);
					selenium_browser.type("nome", cp.getNome());
					selenium_browser.type("valore", cp.getValore());
				}
				
				selenium_browser.click("//input[@value='"+CostantiTestSuite.BOTTONE_INVIA+"']");
				try{
					selenium_browser.waitForPageToLoad(TestUtil.getWaitPageToLoadTime());
				}catch(SeleniumException e){
					//ignore
				}
			}
			
			
			if(oggetto instanceof Soggetto){
				Soggetto s = (Soggetto) oggetto;
				// ritornare indietro
				//selenium_browser.click("link=Connettore di SPC/MinisteroFruitore");
				String link = "link=Connettore del soggetto "+s.getTipo()+"/"+s.getNome();
				selenium_browser.click(link);
				Reporter.log("Gestione connettore custom["+custom+"] di tipo ["+tipo+"], soggetto link["+link+"] ...");
				try{
					selenium_browser.waitForPageToLoad(TestUtil.getWaitPageToLoadTime());
				}catch(SeleniumException e){
					//ignore
				}
			}
			else if( (oggetto instanceof AccordoServizioParteSpecifica) && CostantiDB.UPDATE==operationType){
				AccordoServizioParteSpecifica asps = (AccordoServizioParteSpecifica) oggetto;
				Servizio s = asps.getServizio();
				// ritornare indietro
				//selenium_browser.click("link=Connettore di SPC/MinisteroFruitore");
				String link = "link=Connettore del servizio "+s.getTipo()+"/"+s.getNome()+" erogato dal soggetto "+s.getTipoSoggettoErogatore()+"/"+s.getNomeSoggettoErogatore();
				selenium_browser.click(link);
				Reporter.log("Gestione connettore custom["+custom+"] di tipo ["+tipo+"], servizio link["+link+"] ...");
				try{
					selenium_browser.waitForPageToLoad(TestUtil.getWaitPageToLoadTime());
				}catch(SeleniumException e){
					//ignore
				}
			}
			else if( (oggetto instanceof Fruitore)){
				Fruitore f = (Fruitore) oggetto;
				// ritornare indietro
				//selenium_browser.click("link=Connettore di SPC/MinisteroFruitore");
				String link = "link=Connettore del fruitore "+f.getTipo()+"/"+f.getNome();
				selenium_browser.click(link);
				Reporter.log("Gestione connettore custom["+custom+"] di tipo ["+tipo+"], fruitore link["+link+"] ...");
				try{
					selenium_browser.waitForPageToLoad(TestUtil.getWaitPageToLoadTime());
				}catch(SeleniumException e){
					//ignore
				}
			}
			else if( (oggetto instanceof ServizioApplicativo) ){
				ServizioApplicativo sa = (ServizioApplicativo) oggetto;
				if(sa.getInvocazioneServizio()!=null && sa.getInvocazioneServizio().getConnettore()!=null && 
						sa.getInvocazioneServizio().getConnettore().getId()==con.getId()){
					// ritornare indietro
					//selenium_browser.click("link=Connettore di SPC/MinisteroFruitore");
					String link = "link=Connettore del servizio applicativo (InvocazioneServizio) "+sa.getNome()+" del soggetto "+sa.getTipoSoggettoProprietario()+"/"+sa.getNomeSoggettoProprietario();
					selenium_browser.click(link);
					Reporter.log("Gestione connettore custom["+custom+"] di tipo ["+tipo+"], sa invocazione servizio link["+link+"] ...");
					try{
						selenium_browser.waitForPageToLoad(TestUtil.getWaitPageToLoadTime());
					}catch(SeleniumException e){
						//ignore
					}
				}
				else{
					// ritornare indietro
					//selenium_browser.click("link=Connettore di SPC/MinisteroFruitore");
					String link = "link=Connettore del servizio applicativo (RispostaAsincrona) "+sa.getNome()+" del soggetto "+sa.getTipoSoggettoProprietario()+"/"+sa.getNomeSoggettoProprietario();
					selenium_browser.click(link);
					Reporter.log("Gestione connettore custom["+custom+"] di tipo ["+tipo+"], sa risposta asincrona link["+link+"] ...");
					try{
						selenium_browser.waitForPageToLoad(TestUtil.getWaitPageToLoadTime());
					}catch(SeleniumException e){
						//ignore
					}
				}
			}
						
		} else {
			
			Reporter.log("Gestione connettore di tipo ["+tipo+"]");
			
			TipiConnettore tipoConnettore = null;
			if(tipo==null){
				tipoConnettore = TipiConnettore.DISABILITATO;
			}else if(tipo.equals(TipiConnettore.DISABILITATO.toString())){
				tipoConnettore = TipiConnettore.DISABILITATO;
			}else if(tipo.equals(TipiConnettore.HTTP.toString())){
				tipoConnettore = TipiConnettore.HTTP;
			}else if(tipo.equals(TipiConnettore.JMS.toString())){
				tipoConnettore = TipiConnettore.JMS;
			}else if(tipo.equals(TipiConnettore.NULL.toString())){
				tipoConnettore = TipiConnettore.NULL;
			}else if(tipo.equals(TipiConnettore.NULLECHO.toString())){
				tipoConnettore = TipiConnettore.NULLECHO;
			}else if(tipo.equals(TipiConnettore.HTTPS.toString())){
				tipoConnettore = TipiConnettore.HTTPS;
			}else{
				throw new AssertionError("Tipo connettore ["+tipo+"] non gestito");
			}
			Map<String, String> propConnettore = null;
			
			switch (tipoConnettore) {
			case HTTP:
				propConnettore=isRegistro ? conR.getProperties() : con.getProperties();
				selenium_browser.select("endpointtype", "label="+tipoConnettore.getNome());
				try{
					selenium_browser.waitForPageToLoad(TestUtil.getWaitPageToLoadTime());
				}catch(SeleniumException e){
					//ignore
				}
				selenium_browser.type("url", propConnettore.get(CostantiDB.CONNETTORE_HTTP_LOCATION));
				break;
				
			case JMS:
				propConnettore=isRegistro ? conR.getProperties() : con.getProperties();
				selenium_browser.select("endpointtype", "label="+tipoConnettore.getNome());
				try{
					selenium_browser.waitForPageToLoad(TestUtil.getWaitPageToLoadTime());
				}catch(SeleniumException e){
					//ignore
				}
				selenium_browser.type("nome", propConnettore.get(CostantiDB.CONNETTORE_JMS_NOME)!=null ? propConnettore.get(CostantiDB.CONNETTORE_JMS_NOME) : "");
				selenium_browser.select("tipo", "label="+propConnettore.get(CostantiDB.CONNETTORE_JMS_TIPO));
				//selenium_browser.type("user", propConnettore.get(CostantiDB.CONNETTORE_JMS_USER)!=null?propConnettore.get(CostantiDB.CONNETTORE_JMS_USER):"");
				//selenium_browser.type("password", propConnettore.get(CostantiDB.CONNETTORE_JMS_PWD)!=null? propConnettore.get(CostantiDB.CONNETTORE_JMS_PWD) : "");
				selenium_browser.type("initcont", propConnettore.get(CostantiDB.CONNETTORE_JMS_CONTEXT_JAVA_NAMING_FACTORY_INITIAL)!=null ? propConnettore.get(CostantiDB.CONNETTORE_JMS_CONTEXT_JAVA_NAMING_FACTORY_INITIAL) :"");
				selenium_browser.type("urlpgk", propConnettore.get(CostantiDB.CONNETTORE_JMS_CONTEXT_JAVA_NAMING_FACTORY_URL_PKG)!=null?propConnettore.get(CostantiDB.CONNETTORE_JMS_CONTEXT_JAVA_NAMING_FACTORY_URL_PKG):"");
				selenium_browser.type("provurl", propConnettore.get(CostantiDB.CONNETTORE_JMS_CONTEXT_JAVA_NAMING_PROVIDER_URL)!=null?propConnettore.get(CostantiDB.CONNETTORE_JMS_CONTEXT_JAVA_NAMING_PROVIDER_URL):"");
				selenium_browser.type("connfact", propConnettore.get(CostantiDB.CONNETTORE_JMS_CONNECTION_FACTORY)!=null?propConnettore.get(CostantiDB.CONNETTORE_JMS_CONNECTION_FACTORY):"");
				selenium_browser.select("sendas", "label="+propConnettore.get(CostantiDB.CONNETTORE_JMS_SEND_AS)!=null?propConnettore.get(CostantiDB.CONNETTORE_JMS_SEND_AS):"");
				try{
					selenium_browser.waitForPageToLoad(TestUtil.getWaitPageToLoadTime());
				}catch(SeleniumException e){
					//ignore
				}
				
				break;
			
			case NULL:
				selenium_browser.select("endpointtype", "label="+tipoConnettore.getNome());
				try{
					selenium_browser.waitForPageToLoad(TestUtil.getWaitPageToLoadTime());
				}catch(SeleniumException e){
					//ignore
				}
				break;
				
			case NULLECHO:
				selenium_browser.select("endpointtype", "label="+tipoConnettore.getNome());
				try{
					selenium_browser.waitForPageToLoad(TestUtil.getWaitPageToLoadTime());
				}catch(SeleniumException e){
					//ignore
				}
				break;
				
			case DISABILITATO:
				selenium_browser.select("endpointtype", "label="+tipoConnettore.getNome());
				try{
					selenium_browser.waitForPageToLoad(TestUtil.getWaitPageToLoadTime());
				}catch(SeleniumException e){
					//ignore
				}
				break;
				
			case HTTPS:
				propConnettore=isRegistro ? conR.getProperties() : con.getProperties();
				selenium_browser.select("endpointtype", "label="+tipoConnettore.getNome());
				try{
					selenium_browser.waitForPageToLoad(TestUtil.getWaitPageToLoadTime());
				}catch(SeleniumException e){
					//ignore
				}
				selenium_browser.type("httpsurl", propConnettore.get(CostantiDB.CONNETTORE_HTTPS_LOCATION));
				selenium_browser.select("httpstipologia", "label="+propConnettore.get(CostantiDB.CONNETTORE_HTTPS_SSL_TYPE));
				if (Boolean.valueOf(CostantiDB.CONNETTORE_HTTPS_HOSTNAME_VERIFIER))
					selenium_browser.check("httpshostverify");
				else
					selenium_browser.uncheck("httpshostverify");
				selenium_browser.type("httpspath", propConnettore.get(CostantiDB.CONNETTORE_HTTPS_TRUST_STORE_LOCATION));
				selenium_browser.select("httpstipo", "label="+propConnettore.get(CostantiDB.CONNETTORE_HTTPS_TRUST_STORE_TYPE));
				selenium_browser.type("httpspwd", propConnettore.get(CostantiDB.CONNETTORE_HTTPS_TRUST_STORE_PASSWORD));
				selenium_browser.type("httpsalgoritmo", propConnettore.get(CostantiDB.CONNETTORE_HTTPS_TRUST_MANAGEMENT_ALGORITM));
				if (propConnettore.get(CostantiDB.CONNETTORE_HTTPS_KEY_STORE_LOCATION) == null) {
					selenium_browser.uncheck("httpsstato");
				} else {
					//selenium_browser.check("httpsstato");
					selenium_browser.uncheck("httpsstato");
					selenium_browser.click("httpsstato");
					try{
						selenium_browser.waitForPageToLoad(TestUtil.getWaitPageToLoadTime());
					}catch(SeleniumException e){
						//ignore
					}
					String httpspath = propConnettore.get(CostantiDB.CONNETTORE_HTTPS_TRUST_STORE_LOCATION);
					String httpstipo = propConnettore.get(CostantiDB.CONNETTORE_HTTPS_TRUST_STORE_TYPE);
					String httpspwd = propConnettore.get(CostantiDB.CONNETTORE_HTTPS_TRUST_STORE_PASSWORD);
					String httpspathkey = propConnettore.get(CostantiDB.CONNETTORE_HTTPS_KEY_STORE_LOCATION);
					String httpstipokey = propConnettore.get(CostantiDB.CONNETTORE_HTTPS_KEY_STORE_TYPE);
					String httpspwdkey = propConnettore.get(CostantiDB.CONNETTORE_HTTPS_KEY_STORE_PASSWORD);
					if (httpspathkey.equals(httpspath) &&
							httpstipokey.equals(httpstipo) &&
							httpspwdkey.equals(httpspwd)) {
						selenium_browser.select("httpskeystore", "label=Usa valori del TrustStore");
						try{
							selenium_browser.waitForPageToLoad(TestUtil.getWaitPageToLoadTime());
						}catch(SeleniumException e){
							//ignore
						}
						selenium_browser.type("httpspwdprivatekeytrust", propConnettore.get(CostantiDB.CONNETTORE_HTTPS_KEY_PASSWORD));
					} else {
						selenium_browser.select("httpskeystore", "label=Ridefinisci");
						try{
							selenium_browser.waitForPageToLoad(TestUtil.getWaitPageToLoadTime());
						}catch(SeleniumException e){
							//ignore
						}
						selenium_browser.type("httpspathkey", propConnettore.get(CostantiDB.CONNETTORE_HTTPS_KEY_STORE_LOCATION));
						selenium_browser.select("httpstipokey", "label="+propConnettore.get(CostantiDB.CONNETTORE_HTTPS_KEY_STORE_TYPE));
						selenium_browser.type("httpspwdkey", propConnettore.get(CostantiDB.CONNETTORE_HTTPS_KEY_STORE_PASSWORD));
						selenium_browser.type("httpspwdprivatekey", propConnettore.get(CostantiDB.CONNETTORE_HTTPS_KEY_PASSWORD));
					}
					selenium_browser.type("httpsalgoritmokey", propConnettore.get(CostantiDB.CONNETTORE_HTTPS_KEY_MANAGEMENT_ALGORITM));
				}
				break;
				
			default:
				break;
				
			}
		}
		return selenium_browser;
	}
	
	public static Selenium CRUDCredenziali(Selenium selenium_browser,Credenziali credenziali,int operationType,boolean ctrlstat) throws AssertionError{
		
		String tipoCredenziale = credenziali!=null ? credenziali.getTipo().toString() : "none";
		
		if("basic".equals(tipoCredenziale)){
			String user = credenziali.getUser()!=null ? credenziali.getUser() :"";
			String pwd = credenziali.getPassword()!=null ? credenziali.getPassword():"";
			selenium_browser.select("tipoauth", "label="+tipoCredenziale);
			
			try{
				selenium_browser.waitForPageToLoad(TestUtil.getWaitPageToLoadTime());
			}catch(SeleniumException e){
				//ignore
			}
			
			selenium_browser.type("utente", user);
			selenium_browser.type("password", pwd);
			selenium_browser.type("confpw", pwd);
			
		}else if("ssl".equals(tipoCredenziale)){
			String sbj = credenziali.getSubject()!=null ? credenziali.getSubject() : "";
			selenium_browser.select("tipoauth", "label="+tipoCredenziale);
			try{
				selenium_browser.waitForPageToLoad(TestUtil.getWaitPageToLoadTime());
			}catch(SeleniumException e){
				//ignore
			}
			selenium_browser.type("subject", sbj);
		}else{
			//none
			if(ctrlstat)
				selenium_browser.select("tipoauth", "label="+"nessuna");
			else
				selenium_browser.select("tipoauth", "label="+tipoCredenziale);
			try{
				selenium_browser.waitForPageToLoad(TestUtil.getWaitPageToLoadTime());
			}catch(SeleniumException e){
				//ignore
			}
		}
		return selenium_browser;
		
	}
	
	public static Selenium CRUDDocumenti(SeleniumClient seleniumClient,
			boolean validate,
			int operationType,
			String webAppContext,
			String servlet,
			Vector<Documento> documenti,
			long idOggetto,
			boolean terminologiaSICA,
			boolean servizioComposto) throws Exception{
		Selenium selenium = seleniumClient.getSeleniumBrowser();
		
		if(operationType==CostantiDB.UPDATE || operationType==CostantiDB.DELETE){
			// Elimino quelli presenti
			deleteAllAllegati(seleniumClient, webAppContext, servlet, idOggetto, validate, terminologiaSICA, servizioComposto);
		}
		
		
		// Riaggiungo allegati
		if(documenti!=null && documenti.size()>0){
			if("accordiAllegatiList.do".equals(servlet)){
				selenium.open(TestUtil.urlAccordoServizio(webAppContext, servlet+"?id="+idOggetto, terminologiaSICA, servizioComposto));
			}else if("serviziAllegatiList.do".equals(servlet)){
				if(seleniumClient.getRegservContext().equals(webAppContext)){
					selenium.open("/"+webAppContext+"/"+servlet+"?idServizio="+idOggetto);
				}else{
					selenium.open("/"+webAppContext+"/"+servlet+"?id="+idOggetto);
				}
			}else{
				selenium.open("/"+webAppContext+"/"+servlet+"?id="+idOggetto);
			}
			try{
				selenium.waitForPageToLoad(TestUtil.getWaitPageToLoadTime());
			}catch(Exception e){}
			
			switch (operationType) {
			case CostantiDB.CREATE:
			case CostantiDB.UPDATE:
				for(int i=0; i<documenti.size(); i++){
					Documento doc = documenti.get(i);
					selenium.click("//input[@value='"+CostantiTestSuite.BOTTONE_ADD+"']");
					selenium.waitForPageToLoad(TestUtil.getWaitPageToLoadTime());
					selenium.select("ruolo", "label="+doc.getRuolo());
					try{
						selenium.waitForPageToLoad(TestUtil.getWaitPageToLoadTime());
					}catch(Exception e){}
					if(doc.getRuolo().equals(RuoliDocumento.allegato.toString())==false){
						selenium.select("tipoFile", "label="+doc.getTipo());
						try{
							selenium.waitForPageToLoad(TestUtil.getWaitPageToLoadTime());
						}catch(Exception e){}
					}
					File tmp = null;
					if(doc.getRuolo().equals(RuoliDocumento.allegato.toString())==false){
						tmp = File.createTempFile("allegatoGeneratoTESTSUITE", ".zip");
					}else{
						tmp = File.createTempFile("allegatoGeneratoTESTSUITE", "."+doc.getTipo());
					}
					doc.setFile(tmp.getName());
					FileSystemUtilities.writeFile(tmp, doc.getByteContenuto());
					selenium.type("theFile", tmp.getAbsolutePath());
					selenium.click("//input[@value='"+CostantiTestSuite.BOTTONE_INVIA+"']");
					selenium.waitForPageToLoad(TestUtil.getWaitPageToLoadTime());
					
					tmp.delete();
				}
				break;
				
			case CostantiDB.DELETE:
				//dopo la cancellazione esco
				return selenium;
			}
		}
		
		return selenium;
	}
		
	protected static void deleteAllAllegati(SeleniumClient seleniumClient,
			String webAppContext,String servlet,long idOggetto,boolean validate,boolean terminologiaSICA,boolean servizioComposto) throws AssertionError{
		
		assertNotNull(seleniumClient,"Il client seleniun e' null.");
		
		//Note: si effettua la cancellazione dell'intera lista.
		Selenium selenium = seleniumClient.getSeleniumBrowser();
		if("accordiAllegatiList.do".equals(servlet)){
			selenium.open(TestUtil.urlAccordoServizio(webAppContext, servlet+"?id="+idOggetto, terminologiaSICA, servizioComposto));
		}else if("serviziAllegatiList.do".equals(servlet)){
			if(seleniumClient.getRegservContext().equals(webAppContext)){
				selenium.open("/"+webAppContext+"/"+servlet+"?idServizio="+idOggetto);
			}else{
				selenium.open("/"+webAppContext+"/"+servlet+"?id="+idOggetto);
			}
		}else{
			selenium.open("/"+webAppContext+"/"+servlet+"?id="+idOggetto);
		}
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
	
	public static Selenium CRUDElencoPartecipanti(SeleniumClient seleniumClient,
			boolean validate,
			int operationType,
			String webAppContext,
			List<IdSoggetto> partecipanti,
			long idOggetto) throws Exception{
		
		Selenium selenium = seleniumClient.getSeleniumBrowser();
		
		if(operationType==CostantiDB.UPDATE || operationType==CostantiDB.DELETE){
			// Elimino quelli presenti
			deleteAllElencoPartecipanti(seleniumClient, webAppContext, idOggetto, validate);
		}
		
		
		// Riaggiungo partecipanti
		if(partecipanti!=null && partecipanti.size()>0){
			selenium.open("/"+webAppContext+"/accordiCoopPartecipantiList.do?id="+idOggetto);
			try{
				selenium.waitForPageToLoad(TestUtil.getWaitPageToLoadTime());
			}catch(Exception e){}
			
			switch (operationType) {
			case CostantiDB.CREATE:
			case CostantiDB.UPDATE:
				for(int i=0; i<partecipanti.size(); i++){
					IdSoggetto partecipante = partecipanti.get(i);
					selenium.click("//input[@value='"+CostantiTestSuite.BOTTONE_ADD+"']");
					selenium.waitForPageToLoad(TestUtil.getWaitPageToLoadTime());
					selenium.select("partecipante", "label="+partecipante.getTipo()+"/"+partecipante.getNome());
					selenium.click("//input[@value='"+CostantiTestSuite.BOTTONE_INVIA+"']");
					selenium.waitForPageToLoad(TestUtil.getWaitPageToLoadTime());
				}
				break;
				
			case CostantiDB.DELETE:
				//dopo la cancellazione esco
				return selenium;
			}
		}
		
		return selenium;
	}
	
	protected static void deleteAllElencoPartecipanti(SeleniumClient seleniumClient,
			String webAppContext,long idOggetto,boolean validate) throws AssertionError{
		
		assertNotNull(seleniumClient,"Il client seleniun e' null.");
		
		//Note: si effettua la cancellazione dell'intera lista.
		Selenium selenium = seleniumClient.getSeleniumBrowser();
		selenium.open("/"+webAppContext+"/accordiCoopPartecipantiList.do?id="+idOggetto);
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
	
	public static Selenium CRUDElencoServiziComponenti(SeleniumClient seleniumClient,
			boolean validate,
			int operationType,
			String webAppContext,
			List<AccordoServizioParteComuneServizioCompostoServizioComponente> componenti,
			long idOggetto,
			boolean terminologiaSICA) throws Exception{
		
		Selenium selenium = seleniumClient.getSeleniumBrowser();
		
		if(operationType==CostantiDB.UPDATE || operationType==CostantiDB.DELETE){
			// Elimino quelli presenti
			deleteAllElencoServiziComponenti(seleniumClient, webAppContext, idOggetto, validate, terminologiaSICA);
		}
		
		
		// Riaggiungo componenti
		if(componenti!=null && componenti.size()>0){
			selenium.open(urlAccordoServizio(webAppContext, "accordiComponentiList.do?id="+idOggetto, terminologiaSICA, true));
			try{
				selenium.waitForPageToLoad(TestUtil.getWaitPageToLoadTime());
			}catch(Exception e){}
			
			switch (operationType) {
			case CostantiDB.CREATE:
			case CostantiDB.UPDATE:
				for(int i=0; i<componenti.size(); i++){
					AccordoServizioParteComuneServizioCompostoServizioComponente componente = componenti.get(i);
					selenium.click("//input[@value='"+CostantiTestSuite.BOTTONE_ADD+"']");
					selenium.waitForPageToLoad(TestUtil.getWaitPageToLoadTime());
					selenium.select("componente", "label="+componente.getTipoSoggetto()+"/"+componente.getNomeSoggetto()+"_"+componente.getTipo()+"/"+componente.getNome());
					selenium.click("//input[@value='"+CostantiTestSuite.BOTTONE_INVIA+"']");
					selenium.waitForPageToLoad(TestUtil.getWaitPageToLoadTime());
				}
				break;
				
			case CostantiDB.DELETE:
				//dopo la cancellazione esco
				return selenium;
			}
		}
		
		return selenium;
	}
	
	
	protected static void deleteAllElencoServiziComponenti(SeleniumClient seleniumClient,
			String webAppContext,long idOggetto,boolean validate,boolean terminologiaSICA) throws AssertionError{
		
		assertNotNull(seleniumClient,"Il client seleniun e' null.");
		
		//Note: si effettua la cancellazione dell'intera lista.
		Selenium selenium = seleniumClient.getSeleniumBrowser();
		selenium.open(urlAccordoServizio(webAppContext, "accordiComponentiList.do?id="+idOggetto, terminologiaSICA, true));
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
	
	public static String urlAccordoServizio(String webAppContext,String servlet,boolean terminologiaSICA,boolean servizioComposto){
		if(terminologiaSICA){
			if(!servizioComposto){
				if(servlet.endsWith(".do")){
					return "/"+webAppContext+"/"+servlet+"?tipoSICA=apc";
				}else{
					return "/"+webAppContext+"/"+servlet+"&tipoSICA=apc";
				}
			}
			else{
				if(servlet.endsWith(".do")){
					return "/"+webAppContext+"/"+servlet+"?tipoSICA=asc";
				}else{
					return "/"+webAppContext+"/"+servlet+"&tipoSICA=asc";
				}
			}
		}else{
			return "/"+webAppContext+"/"+servlet;
		}
	}
	
	public static Vector<File> CRUD_WSDL(SeleniumClient seleniumClient,
			boolean validate,
			String webAppContext,int operationType,AccordoServizioParteComune as,boolean returnBack) throws Exception{
		
		Selenium selenium = seleniumClient.getSeleniumBrowser();
		Vector<File> files = new Vector<File>();
		
		String labelBack = null;
		if(returnBack){
			labelBack = IDAccordoFactory.getInstance().getUriFromAccordo(as);
		}
		
		switch (operationType) {
		case CostantiDB.CREATE:
			// specifica interfaccia
			if(as.getByteWsdlDefinitorio()!=null){
				File tmp = File.createTempFile("wsdlDefinitorio", ".wsdl");
				FileSystemUtilities.writeFile(tmp, as.getByteWsdlDefinitorio());
				selenium.type("wsdldef", tmp.getAbsolutePath());
				files.add(tmp);
			}
			if(as.getByteWsdlConcettuale()!=null){
				File tmp = File.createTempFile("wsdlConcettuale", ".wsdl");
				FileSystemUtilities.writeFile(tmp, as.getByteWsdlConcettuale());
				selenium.type("wsdlconc", tmp.getAbsolutePath());
				files.add(tmp);
			}
			if(as.getByteWsdlLogicoErogatore()!=null){
				File tmp = File.createTempFile("wsdlLogicoErogatore", ".wsdl");
				FileSystemUtilities.writeFile(tmp, as.getByteWsdlLogicoErogatore());
				selenium.type("wsdlserv", tmp.getAbsolutePath());
				files.add(tmp);
			}
			if(as.getByteWsdlLogicoFruitore()!=null){
				File tmp = File.createTempFile("wsdlLogicoFruitore", ".wsdl");
				FileSystemUtilities.writeFile(tmp, as.getByteWsdlLogicoFruitore());
				selenium.type("wsdlservcorr", tmp.getAbsolutePath());
				files.add(tmp);
			}
			// specifica conversazioni
			/*if(as.getByteWsblConcettuale()!=null){
				File tmp = File.createTempFile("wsblConcettuale", ".wsbl");
				FileSystemUtilities.writeFile(tmp, as.getByteWsblConcettuale());
				selenium.type("wsblconc", tmp.getAbsolutePath());
				files.add(tmp);
			}
			if(as.getByteWsblLogicoErogatore()!=null){
				File tmp = File.createTempFile("wsblLogicoErogatore", ".wsbl");
				FileSystemUtilities.writeFile(tmp, as.getByteWsblLogicoErogatore());
				selenium.type("wsblserv", tmp.getAbsolutePath());
				files.add(tmp);
			}
			if(as.getByteWsblLogicoFruitore()!=null){
				File tmp = File.createTempFile("wsblLogicoFruitore", ".wsbl");
				FileSystemUtilities.writeFile(tmp, as.getByteWsblLogicoFruitore());
				selenium.type("wsblservcorr", tmp.getAbsolutePath());
				files.add(tmp);
			}*/
			
			break;
		case CostantiDB.UPDATE:
			
			updateDocumento(files,selenium,"WSDL Definitorio","wsdlDefinitorio",".wsdl",as.getByteWsdlDefinitorio(),labelBack);
			updateDocumento(files,selenium,"WSDL Concettuale","wsdlConcettuale",".wsdl",as.getByteWsdlConcettuale(),labelBack);
			updateDocumento(files,selenium,"WSDL Logico Erogatore","wsdlLogicoErogatore",".wsdl",as.getByteWsdlLogicoErogatore(),labelBack);
			updateDocumento(files,selenium,"WSDL Logico Fruitore","wsdlLogicoFruitore",".wsdl",as.getByteWsdlLogicoFruitore(),labelBack);
			
			//updateDocumento(files,selenium,"WSBL Concettuale","wsblConcettuale",".wsbl",as.getByteWsblConcettuale(),labelBack);
			//updateDocumento(files,selenium,"WSBL Logico Erogatore","wsblLogicoErogatore",".wsbl",as.getByteWsblLogicoErogatore(),labelBack);
			//updateDocumento(files,selenium,"WSBL Logico Fruitore","wsblLogicoFruitore",".wsbl",as.getByteWsblLogicoFruitore(),labelBack);
			
			break;
		case CostantiDB.DELETE:
			
			updateDocumento(files,selenium,"WSDL Definitorio","wsdlDefinitorio",".wsdl",null,labelBack);
			updateDocumento(files,selenium,"WSDL Concettuale","wsdlConcettuale",".wsdl",null,labelBack);
			updateDocumento(files,selenium,"WSDL Logico Erogatore","wsdlLogicoErogatore",".wsdl",null,labelBack);
			updateDocumento(files,selenium,"WSDL Logico Fruitore","wsdlLogicoFruitore",".wsdl",null,labelBack);
			
			//updateDocumento(files,selenium,"WSBL Concettuale","wsblConcettuale",".wsbl",null,labelBack);
			//updateDocumento(files,selenium,"WSBL Logico Erogatore","wsblLogicoErogatore",".wsbl",null,labelBack);
			//updateDocumento(files,selenium,"WSBL Logico Fruitore","wsblLogicoFruitore",".wsbl",null,labelBack);
			
			break;
		}	
		
		return files;
	}
	
	public static Vector<File> CRUD_WSDL(SeleniumClient seleniumClient,
			boolean validate,
			String webAppContext,int operationType,AccordoServizioParteSpecifica asps,boolean returnBack) throws Exception{
		
		Servizio servizio = asps.getServizio();
		Selenium selenium = seleniumClient.getSeleniumBrowser();
		Vector<File> files = new Vector<File>();
		
		String labelBack = null;
		if(returnBack){
			String provider = servizio.getTipoSoggettoErogatore()+"/"+servizio.getNomeSoggettoErogatore();
			String accordoServParteSpecifica = asps.getNome();
			labelBack = provider+":"+accordoServParteSpecifica;
		}
		
		switch (operationType) {
		case CostantiDB.CREATE:
			
			// specifica interfaccia
			if(asps.getByteWsdlImplementativoErogatore()!=null){
				File tmp = File.createTempFile("wsdlImplementativoErogatore", ".wsdl");
				FileSystemUtilities.writeFile(tmp, asps.getByteWsdlImplementativoErogatore());
				selenium.type("wsdlimpler", tmp.getAbsolutePath());
				files.add(tmp);
			}
			if(asps.getByteWsdlImplementativoFruitore()!=null){
				File tmp = File.createTempFile("wsdlImplementativoFruitore", ".wsdl");
				FileSystemUtilities.writeFile(tmp, asps.getByteWsdlImplementativoFruitore());
				selenium.type("wsdlimplfru", tmp.getAbsolutePath());
				files.add(tmp);
			}
			
			break;
		case CostantiDB.UPDATE:
			
			updateDocumento(files,selenium,"WSDL Implementativo Erogatore","wsdlImplementativoErogatore",".wsdl",asps.getByteWsdlImplementativoErogatore(),labelBack);
			updateDocumento(files,selenium,"WSDL Implementativo Fruitore","wsdlImplementativoFruitore",".wsdl",asps.getByteWsdlImplementativoFruitore(),labelBack);
			
			break;
		case CostantiDB.DELETE:
			
			updateDocumento(files,selenium,"WSDL Implementativo Erogatore","wsdlImplementativoErogatore",".wsdl",null,labelBack);
			updateDocumento(files,selenium,"WSDL Implementativo Fruitore","wsdlImplementativoFruitore",".wsdl",null,labelBack);
			
			break;
		}	
		
		return files;
	}
	
	public static Vector<File> CRUD_WSDL(SeleniumClient seleniumClient,
			boolean validate,
			String webAppContext,int operationType,Fruitore fruitore,boolean returnBack) throws Exception{
		
		Selenium selenium = seleniumClient.getSeleniumBrowser();
		Vector<File> files = new Vector<File>();
		
		String labelBack = null;
		if(returnBack){
			labelBack = fruitore.getTipo()+"/"+fruitore.getNome();
		}
		
		switch (operationType) {
		case CostantiDB.CREATE:
			
			// specifica interfaccia
			if(fruitore.getByteWsdlImplementativoErogatore()!=null){
				File tmp = File.createTempFile("wsdlImplementativoErogatore", ".wsdl");
				FileSystemUtilities.writeFile(tmp, fruitore.getByteWsdlImplementativoErogatore());
				selenium.type("wsdlimpler", tmp.getAbsolutePath());
				files.add(tmp);
			}
			if(fruitore.getByteWsdlImplementativoFruitore()!=null){
				File tmp = File.createTempFile("wsdlImplementativoFruitore", ".wsdl");
				FileSystemUtilities.writeFile(tmp, fruitore.getByteWsdlImplementativoFruitore());
				selenium.type("wsdlimplfru", tmp.getAbsolutePath());
				files.add(tmp);
			}
			
			break;
		case CostantiDB.UPDATE:
			
			updateDocumento(files,selenium,"WSDL Implementativo Erogatore","wsdlImplementativoErogatore",".wsdl",fruitore.getByteWsdlImplementativoErogatore(),labelBack);
			updateDocumento(files,selenium,"WSDL Implementativo Fruitore","wsdlImplementativoFruitore",".wsdl",fruitore.getByteWsdlImplementativoFruitore(),labelBack);
			
			break;
		case CostantiDB.DELETE:
			
			updateDocumento(files,selenium,"WSDL Implementativo Erogatore","wsdlImplementativoErogatore",".wsdl",null,labelBack);
			updateDocumento(files,selenium,"WSDL Implementativo Fruitore","wsdlImplementativoFruitore",".wsdl",null,labelBack);
			
			break;
		}	
		
		return files;
	}
	
	private static void updateDocumento(Vector<File> files,Selenium selenium,String label,String fileName,String ext,byte[] contenuto,String servletBack) throws Exception{
		selenium.click("link="+label);
		selenium.waitForPageToLoad(TestUtil.getWaitPageToLoadTime());
		if(contenuto!=null){
			File tmp = File.createTempFile(fileName, ext);
			FileSystemUtilities.writeFile(tmp, contenuto);
			selenium.type("wsdl", tmp.getAbsolutePath());
			files.add(tmp);
		}else{
			selenium.type("wsdl", "");
		}
		selenium.click("//input[@value='"+CostantiTestSuite.BOTTONE_INVIA+"']");
		selenium.waitForPageToLoad(TestUtil.getWaitPageToLoadTime());
		if(servletBack!=null){
			selenium.click("link="+servletBack);
			selenium.waitForPageToLoad(TestUtil.getWaitPageToLoadTime());
		}
	}
	
	public static void eliminaFileTemporanei(Vector<File> files){
		for(int i=0; i<files.size(); i++){
			files.get(i).delete();
		}
	}
	
	
	public static Selenium CRUDMsgDiagnosticiOpenSPCoopAppender(SeleniumClient seleniumClient,
			boolean validate,
			int operationType,
			String webAppContext,
			List<OpenspcoopAppender> appender) throws Exception{
		return CRUDOpenSPCoopAppender(seleniumClient, validate, operationType, webAppContext, true, appender);
	}
	public static Selenium CRUDTracciamentoOpenSPCoopAppender(SeleniumClient seleniumClient,
			boolean validate,
			int operationType,
			String webAppContext,
			List<OpenspcoopAppender> appender) throws Exception{
		return CRUDOpenSPCoopAppender(seleniumClient, validate, operationType, webAppContext, false, appender);
	}
	private static Selenium CRUDOpenSPCoopAppender(SeleniumClient seleniumClient,
			boolean validate,
			int operationType,
			String webAppContext,
			boolean diagnostica,
			List<OpenspcoopAppender> appender) throws Exception{
		
		Selenium selenium = seleniumClient.getSeleniumBrowser();
		
		Reporter.log("Tipo: "+operationType);
		
		if(operationType==CostantiDB.UPDATE || operationType==CostantiDB.DELETE){
			// Elimino quelli presenti
			Reporter.log("Elimino appender presenti");
			deleteAllOpenSPCoopAppender(seleniumClient, webAppContext, diagnostica, validate);
		}
		
		
		// Riaggiungo componenti
		if(appender!=null && appender.size()>0){
						
			switch (operationType) {
			case CostantiDB.CREATE:
			case CostantiDB.UPDATE:
				for(int i=0; i<appender.size(); i++){
					
					Reporter.log("Appender "+(i+1)+"/"+appender.size()+"...");
					
					if(diagnostica){
						selenium.open("/"+webAppContext+"/diagnosticaAppenderList.do");
					}else{
						selenium.open("/"+webAppContext+"/tracciamentoAppenderList.do");
					}
					try{
						selenium.waitForPageToLoad(TestUtil.getWaitPageToLoadTime());
					}catch(Exception e){}
					
					OpenspcoopAppender app = appender.get(i);
					selenium.click("//input[@value='"+CostantiTestSuite.BOTTONE_ADD+"']");
					selenium.waitForPageToLoad(TestUtil.getWaitPageToLoadTime());
					selenium.type("tipo", app.getTipo());
					selenium.click("//input[@value='"+CostantiTestSuite.BOTTONE_INVIA+"']");
					selenium.waitForPageToLoad(TestUtil.getWaitPageToLoadTime());
					
					if(app.sizePropertyList()>0){
						String tipoAggiunto = selenium.getAttribute("link="+app.getTipo()+"@href");
						String idS = null;
						try{
							String [] tmp = tipoAggiunto.split("\\?");
							Reporter.log("Split riuscito in ["+tmp.length+"] ["+tmp[0]+"] ["+tmp[1]+"]");
							tmp = tmp[1].trim().split("=");
							Reporter.log("Secondo split riuscito in ["+tmp.length+"] ["+tmp[0]+"] ["+tmp[1]+"]");
							idS = tmp[1].trim();
						}catch(Exception e){
							throw new Exception("Raccolta id per openspcoopAppender aggiunto di tipo ["+app.getTipo()+"] non riuscita: "+e.getMessage(),e);
						}
						if(diagnostica){
							selenium.open("/"+webAppContext+"/diagnosticaAppenderPropList.do?id="+idS);
						}else{
							selenium.open("/"+webAppContext+"/tracciamentoAppenderPropList.do?id="+idS);
						}
	
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
						Reporter.log("Eliminate prioprieta' presenti ");
						int totLista = TestUtil.getTotEntryLista(toParse); 
						assertEquals(totLista, 0);
						
						for(int j=0; j<app.sizePropertyList(); j++){
							selenium.click("//input[@value='"+CostantiTestSuite.BOTTONE_ADD+"']");
							selenium.waitForPageToLoad(TestUtil.getWaitPageToLoadTime());
							selenium.type("nome", app.getProperty(j).getNome());
							selenium.type("valore", app.getProperty(j).getValore());
							selenium.click("//input[@value='"+CostantiTestSuite.BOTTONE_INVIA+"']");
							selenium.waitForPageToLoad(TestUtil.getWaitPageToLoadTime());
						}
					}
					
					Reporter.log("Appender "+(i+1)+"/"+appender.size()+" effettuato");
				}
				break;
				
			case CostantiDB.DELETE:
				//dopo la cancellazione esco
				return selenium;
			}
		}
		
		return selenium;
	}
	
	public static void deleteAllMsgDiagnosticiOpenSPCoopAppender(SeleniumClient seleniumClient,
			String webAppContext,boolean validate) throws AssertionError{
		deleteAllOpenSPCoopAppender(seleniumClient, webAppContext, true, validate);
	}
	public static void deleteAllTracciamentoOpenSPCoopAppender(SeleniumClient seleniumClient,
			String webAppContext,boolean validate) throws AssertionError{
		deleteAllOpenSPCoopAppender(seleniumClient, webAppContext, false, validate);
	}
	private static void deleteAllOpenSPCoopAppender(SeleniumClient seleniumClient,
			String webAppContext,boolean diagnostica,boolean validate) throws AssertionError{
		
		assertNotNull(seleniumClient,"Il client seleniun e' null.");
		
		//Note: si effettua la cancellazione dell'intera lista.
		Selenium selenium = seleniumClient.getSeleniumBrowser();
		if(diagnostica){
			selenium.open("/"+webAppContext+"/diagnosticaAppenderList.do");
		}else{
			selenium.open("/"+webAppContext+"/tracciamentoAppenderList.do");
		}
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
		
		Reporter.log("Eliminati appender presenti ");
		
		int totLista = TestUtil.getTotEntryLista(toParse); 
		
		assertEquals(totLista, 0);
	}
}


