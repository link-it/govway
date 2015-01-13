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

import org.openspcoop2.core.config.Connettore;
import org.openspcoop2.core.config.Credenziali;
import org.openspcoop2.core.config.InvocazionePorta;
import org.openspcoop2.core.config.InvocazionePortaGestioneErrore;
import org.openspcoop2.core.config.InvocazioneServizio;
import org.openspcoop2.core.config.RispostaAsincrona;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.testsuite.core.CostantiTestSuite;
import org.openspcoop2.testsuite.gui.commons.SeleniumClient;
import org.openspcoop2.testsuite.gui.commons.TestUtil;
import org.testng.Assert;
import org.testng.Reporter;

import com.thoughtworks.selenium.Selenium;
import com.thoughtworks.selenium.SeleniumException;

/**
 * Lib test Servizio Applicativo
 *
 * @author Stefano Corallo <corallo@link.it>
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ServizioApplicativo {

	public static void create(SeleniumClient seleniumClient,org.openspcoop2.core.config.ServizioApplicativo sa,boolean validate) throws AssertionError 
	{
		Assert.assertNotNull(seleniumClient,"Il client seleniun e' null.");
		Assert.assertNotNull(sa,"L' oggetto Servizio Applicativo e' null");
		
		Selenium selenium = ServizioApplicativo.CRUDOperation(seleniumClient, sa, validate, CostantiDB.CREATE);
		String webAppContext = seleniumClient.getCtrlstatContext();
		selenium.open("/"+webAppContext+"/servizioApplicativoList.do");
		Assert.assertEquals(selenium.getText("link="+sa.getNome()), sa.getNome());
	}
	
	public static void update(SeleniumClient seleniumClient,org.openspcoop2.core.config.ServizioApplicativo sa,boolean validate) throws AssertionError
	{
		Assert.assertNotNull(seleniumClient,"Il client seleniun e' null.");
		Assert.assertNotNull(sa,"L' oggetto Servizio Applicativo e' null");
		
		Selenium selenium = ServizioApplicativo.CRUDOperation(seleniumClient, sa, validate, CostantiDB.UPDATE);
		
		String webAppContext = seleniumClient.getCtrlstatContext();
		selenium.open("/"+webAppContext+"/servizioApplicativoList.do");
		Assert.assertEquals(selenium.getText("link="+sa.getNome()), sa.getNome());
		
	}
	
	public static void list(SeleniumClient seleniumClient,org.openspcoop2.core.config.ServizioApplicativo sa,boolean validate) throws AssertionError
	{
		Assert.assertNotNull(seleniumClient,"Il client seleniun e' null.");
		Assert.assertNotNull(sa,"L' oggetto Servizio Applicativo e' null");
		
		String webAppContext = seleniumClient.getCtrlstatContext();
		Selenium selenium = seleniumClient.getSeleniumBrowser();
		selenium.open("/"+webAppContext+"/servizioApplicativoList.do");
		Assert.assertEquals(selenium.getText("link="+sa.getNome()), sa.getNome());
	}
	
	public static void deleteAll(SeleniumClient seleniumClient,boolean validate) throws AssertionError
	{
		Assert.assertNotNull(seleniumClient,"Il client seleniun e' null.");
		//Note: si effettua la cancellazione dell'intera lista.
		String webAppContext = seleniumClient.getCtrlstatContext();		
		TestUtil.deleteAll(seleniumClient, webAppContext, "servizioApplicativoList", validate);
	}
	
	private static Selenium CRUDOperation(SeleniumClient seleniumClient,org.openspcoop2.core.config.ServizioApplicativo sa,boolean validate,int operationType) throws AssertionError
	{
		Selenium selenium = seleniumClient.getSeleniumBrowser();
		String webAppContext = seleniumClient.getCtrlstatContext();
		
		String nome = sa.getNome();
		
		Assert.assertNotNull(nome,"Il nome non e' valido.");
		
		String provider = sa.getTipoSoggettoProprietario()+"/"+sa.getNomeSoggettoProprietario();
		
		InvocazionePorta invocazionePorta = sa.getInvocazionePorta();
									 
		Credenziali cred = null;
		if(invocazionePorta!=null && invocazionePorta.sizeCredenzialiList()>0){
			cred = invocazionePorta.getCredenziali(0);
			
		}
		
		//prefix fault code
		//String prefixfault = "";
		String fault = "soap";
		//String genericFault = "";
		if(invocazionePorta!=null && invocazionePorta.getGestioneErrore()!=null){
			InvocazionePortaGestioneErrore ge = invocazionePorta.getGestioneErrore();
			//prefixfault = ge.getPrefixFaultCode()!=null ? ge.getPrefixFaultCode() : "";
			fault = ge.getFault()!=null && !"".equals(ge.getFault()) ? ge.getFault().toString() : "soap";
			//genericFault = ge.getGenericFaultCode()!=null && !"".equals(ge.getGenericFaultCode()) ? ge.getGenericFaultCode() : CostantiConfigurazione.DISABILITATO.toString();
		}
									 
									 
		switch (operationType) {
		case CostantiDB.CREATE:
			selenium.open("/"+webAppContext+"/servizioApplicativoAdd.do");
			selenium.type("nome", nome);
			selenium.select("provider", "label="+provider);
			break;

		case CostantiDB.UPDATE:
			
			int idSa = ServizioApplicativo.getIDServizioApplicativo(selenium, webAppContext, sa.getNome());
			
			selenium.open("/"+webAppContext+"/servizioApplicativoChange.do?id="+idSa);
			
			break;
		}
		
		selenium=TestUtil.CRUDCredenziali(selenium, cred, operationType,true);
		
		selenium.select("fault", "label="+fault);
		try{
			selenium.waitForPageToLoad(TestUtil.getWaitPageToLoadTime());
		}catch (SeleniumException e) {
			// ignore
		}
		selenium.click("//input[@value='"+CostantiTestSuite.BOTTONE_INVIA+"']");
		
		try{
			selenium.waitForPageToLoad(TestUtil.getWaitPageToLoadTime());
		}catch (SeleniumException e) {
			// ignore
		}
		
		/**
		 * Invocazione Servizio
		 */
		int idSA = ServizioApplicativo.getIDServizioApplicativo(selenium, webAppContext, sa.getNome());
		ServizioApplicativo.CRUDInvocazioneServizio(seleniumClient, sa, validate, operationType, idSA);
		
		/**
		 * Risposta Asinc
		 */
		ServizioApplicativo.CRUDRispostaAsincrona(seleniumClient, sa, validate, operationType, idSA);
		return selenium;
	}
	
	private static int getIDServizioApplicativo(Selenium selenium,String webAppContext,String nomeSA) throws AssertionError{
		selenium.open("/"+webAppContext+"/servizioApplicativoList.do");
		String url = selenium.getAttribute("link="+nomeSA+"@href");
		String idSA = url.substring("servizioApplicativoChange.do?id=".length());
		try{
			return Integer.parseInt(idSA);
		}catch (Exception e) {
			Reporter.log("Errore durante il recuper dell'id Servizio Applicativo dalla URL: "+url);
			throw new AssertionError("Errore durante il recuper dell'id Servizio Applicativo dalla URL: "+url);
		}
	}
	
	private static Selenium CRUDInvocazioneServizio(SeleniumClient seleniumClient,org.openspcoop2.core.config.ServizioApplicativo sa,boolean validate,int operationType,int idSA) throws AssertionError
	{
		
		Selenium selenium = seleniumClient.getSeleniumBrowser();
		String webAppContext = seleniumClient.getCtrlstatContext();
		
		InvocazioneServizio invServizio = sa.getInvocazioneServizio();
		
		if(invServizio!=null){
			//nella gui il tipoAutenticazione coincide con credenziali.tipo
			//String tipoAutenticazione = invServizio!=null && invServizio.getAutenticazione() !=null ? invServizio.getAutenticazione() : "none";
			Credenziali credenziali = invServizio!=null ? invServizio.getCredenziali() : null;
			
			Connettore connettore = invServizio!=null ? invServizio.getConnettore() : null;
			String getMessage = invServizio!=null && invServizio.getGetMessage()!=null? invServizio.getGetMessage().toString() : CostantiConfigurazione.DISABILITATO.toString();
			boolean isSbustamentoSoap = invServizio!=null && invServizio.getSbustamentoSoap()!=null? CostantiConfigurazione.ABILITATO.toString().equals(invServizio.getSbustamentoSoap()) : false;
			
			switch (operationType) {
			case CostantiDB.CREATE:
			case CostantiDB.UPDATE:
				//http://10.114.87.206:8080/pddConsole/servizioApplicativoEndPoint.do?idsil=133&idprovidersa=69&nomeservizioApplicativo=ServizioApplicativoEsempio
			        selenium.open("/"+webAppContext+"/servizioApplicativoEndPoint.do?idsil="+idSA+"&idprovidersa="+sa.getIdSoggetto()+"&nomeservizioApplicativo="+sa.getNome());
				
				if(isSbustamentoSoap)selenium.check("sbustamento");
				else selenium.uncheck("sbustamento");
				
				selenium.select("getmsg", "label="+getMessage);
				//crud connettore
				selenium=TestUtil.CRUDConnettore(selenium,connettore,operationType,sa,webAppContext,true);
								
				//crud credenziali
				selenium=TestUtil.CRUDCredenziali(selenium, credenziali, operationType,true);
				
				selenium.click("//input[@value='"+CostantiTestSuite.BOTTONE_INVIA+"']");
				selenium.waitForPageToLoad(TestUtil.getWaitPageToLoadTime());
				
				break;
			
			}
		}//end if
		return selenium;
	}
	
	private static Selenium CRUDRispostaAsincrona(SeleniumClient seleniumClient,org.openspcoop2.core.config.ServizioApplicativo sa,boolean validate,int operationType,int idSA) throws AssertionError
	{
		
		Selenium selenium = seleniumClient.getSeleniumBrowser();
		String webAppContext = seleniumClient.getCtrlstatContext();
		
		RispostaAsincrona rispostaAsincrona = sa.getRispostaAsincrona();
		
		if(rispostaAsincrona!=null){
			//nella gui il tipoAutenticazione coincide con credenziali.tipo
			//String tipoAutenticazione = invServizio!=null && invServizio.getAutenticazione() !=null ? invServizio.getAutenticazione() : "none";
			Credenziali credenziali = rispostaAsincrona!=null ? rispostaAsincrona.getCredenziali() : null;
			
			Connettore connettore = rispostaAsincrona!=null ? rispostaAsincrona.getConnettore() : null;
			String getMessage = rispostaAsincrona!=null && rispostaAsincrona.getGetMessage()!=null ? rispostaAsincrona.getGetMessage().toString() : CostantiConfigurazione.DISABILITATO.toString();
			boolean isSbustamentoSoap = rispostaAsincrona!=null && rispostaAsincrona.getSbustamentoSoap()!=null? CostantiConfigurazione.ABILITATO.toString().equals(rispostaAsincrona.getSbustamentoSoap()) : false;
						
			switch (operationType) {
			case CostantiDB.CREATE:
			case CostantiDB.UPDATE:
				//http://10.114.87.206:8080/pddConsole/servizioApplicativoEndPoint.do?idsil=133&idprovidersa=69&nomeservizioApplicativo=ServizioApplicativoEsempio
				selenium.open("/"+webAppContext+"/servizioApplicativoEndPointRisposta.do?idsil="+idSA+"&idprovidersa="+sa.getIdSoggetto()+"&nomeservizioApplicativo="+sa.getNome());
				
				if(isSbustamentoSoap)selenium.check("sbustamento");
				else selenium.uncheck("sbustamento");
				
				selenium.select("getmsg", "label="+getMessage);
				//crud connettore
				selenium=TestUtil.CRUDConnettore(selenium,connettore,operationType,sa,webAppContext,true);
								
				//crud credenziali
				selenium=TestUtil.CRUDCredenziali(selenium, credenziali, operationType,true);
								
				selenium.click("//input[@value='"+CostantiTestSuite.BOTTONE_INVIA+"']");
				selenium.waitForPageToLoad(TestUtil.getWaitPageToLoadTime());
				break;
			
			}
		}//end if
		return selenium;
	}
}


