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

import org.openspcoop2.core.config.CorrelazioneApplicativa;
import org.openspcoop2.core.config.CorrelazioneApplicativaElemento;
import org.openspcoop2.core.config.PortaApplicativaServizio;
import org.openspcoop2.core.config.ProprietaProtocollo;
import org.openspcoop2.core.config.constants.CorrelazioneApplicativaRichiestaIdentificazione;
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
 * Lib di test Porta Applicativa
 *
 * @author Stefano Corallo <corallo@link.it>
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class PortaApplicativa {

	public static void create(SeleniumClient seleniumClient,org.openspcoop2.core.config.PortaApplicativa pa,boolean validate, Long idSogg) throws AssertionError 
	{
		assertNotNull(seleniumClient,"Il client seleniun e' null.");
		assertNotNull(pa,"L' oggetto Porta Applicativa e' null");
		
		Selenium selenium = CRUDOperation(seleniumClient, pa, validate, CostantiDB.CREATE, idSogg);
		String webAppContext = seleniumClient.getCtrlstatContext();
		selenium.open("/"+webAppContext+"/porteAppList.do?idsogg="+idSogg+"&tipoprov="+pa.getTipoSoggettoProprietario()+"&nomeprov="+pa.getNomeSoggettoProprietario());
		assertEquals(selenium.getText("link="+pa.getNome()), pa.getNome());
		
	}
	
	public static void update(SeleniumClient seleniumClient,org.openspcoop2.core.config.PortaApplicativa pa,boolean validate, Long idSogg) throws AssertionError
	{
		assertNotNull(seleniumClient,"Il client seleniun e' null.");
		assertNotNull(pa,"L' oggetto Porta Applicativa e' null");
		
		Selenium selenium = CRUDOperation(seleniumClient, pa, validate, CostantiDB.UPDATE, idSogg);
		String webAppContext = seleniumClient.getCtrlstatContext();
		selenium.open("/"+webAppContext+"/porteAppList.do?idsogg="+idSogg+"&tipoprov="+pa.getTipoSoggettoProprietario()+"&nomeprov="+pa.getNomeSoggettoProprietario());
		assertEquals(selenium.getText("link="+pa.getNome()), pa.getNome());
		if(pa.getDescrizione()!=null && !"".equals(pa.getDescrizione())){
			assertEquals(selenium.isTextPresent(pa.getDescrizione()),true);
		}
	}
	
	public static void list(SeleniumClient seleniumClient,org.openspcoop2.core.config.PortaApplicativa pa,boolean validate) throws AssertionError
	{
		assertNotNull(seleniumClient,"Il client seleniun e' null.");
		assertNotNull(pa,"L' oggetto Porta Applicativa e' null");
		
		String webAppContext = seleniumClient.getCtrlstatContext();
		Selenium selenium = seleniumClient.getSeleniumBrowser();
		selenium.open("/"+webAppContext+"/porteAppList.do?idsogg="+pa.getIdSoggetto()+"&tipoprov="+pa.getTipoSoggettoProprietario()+"&nomeprov="+pa.getNomeSoggettoProprietario());
		assertEquals(selenium.getText("link="+pa.getNome()), pa.getNome());
		
	}
	
	public static void deleteAll(SeleniumClient seleniumClient,String tipoSogg,String nomeSogg,Long idSogg,boolean validate) throws AssertionError
	{
		assertNotNull(seleniumClient,"Il client seleniun e' null.");
		//Note: si effettua la cancellazione dell'intera lista.
		String webAppContext = seleniumClient.getCtrlstatContext();		
		TestUtil.deleteAllSublist(seleniumClient, webAppContext, "porteAppList.do?idsogg="+idSogg+"&tipoprov="+tipoSogg+"&nomeprov="+nomeSogg, validate);
		
	}
	
	private static int getIdPortaApplicativa(Selenium selenium,String webAppContext,String tipoSogg,String nomeSogg,Long idSogg,String nomePorta) throws AssertionError{
		selenium.open("/"+webAppContext+"/porteAppList.do?idsogg="+idSogg+"&tipoprov="+tipoSogg+"&nomeprov="+nomeSogg);
		String url = selenium.getAttribute("link="+nomePorta+"@href");
		String idPA = url.substring(("porteAppChange.do?idsogg="+idSogg+"&id="+nomePorta+"&idPorta=").length(),url.length());
		try{
			return Integer.parseInt(idPA);
		}catch (Exception e) {
			Reporter.log("Errore durante il recuper dell'id Porta Applicativa dalla URL: "+url);
			throw new AssertionError("Errore durante il recuper dell'id Porta Applicativa dalla URL: "+url);
		}
	}
	
	private static Selenium CRUDOperation(SeleniumClient seleniumClient,org.openspcoop2.core.config.PortaApplicativa pa,boolean validate,int operationType, Long idSogg) throws AssertionError{
		
		Selenium selenium = seleniumClient.getSeleniumBrowser();
		String webAppContext = seleniumClient.getCtrlstatContext();
		
		String nome = pa.getNome();
		
		assertNotNull(nome,"Il nome non e' valido.");
		
		String descr = pa.getDescrizione()!=null ? pa.getDescrizione() : "";
		String ricevutaAsimmet = pa.getRicevutaAsincronaAsimmetrica()!=null ? pa.getRicevutaAsincronaAsimmetrica().toString() : CostantiConfigurazione.DISABILITATO.toString();
		String ricevutaSimmet = pa.getRicevutaAsincronaSimmetrica()!=null ? pa.getRicevutaAsincronaSimmetrica().toString() : CostantiConfigurazione.DISABILITATO.toString();
		String validazioneXSD = (pa.getValidazioneContenutiApplicativi() != null && pa.getValidazioneContenutiApplicativi().getStato()!=null) ? pa.getValidazioneContenutiApplicativi().getStato().toString() : CostantiConfigurazione.DISABILITATO.toString();
		String tipoValidazioneXSD = (pa.getValidazioneContenutiApplicativi() != null && pa.getValidazioneContenutiApplicativi().getTipo()!=null) ? pa.getValidazioneContenutiApplicativi().getTipo().toString() : "xsd";
		//String proprietario = pa.getTipoSoggettoProprietario()+"/"+pa.getNomeSoggettoProprietario();
		
		PortaApplicativaServizio paServizio = pa.getServizio();
		String tipoServizio = paServizio!=null && paServizio.getTipo()!=null ? paServizio.getTipo() : "";
		String nomeServizio = paServizio!=null && paServizio.getNome()!=null? paServizio.getNome() : "";
		
		//PortaApplicativaAzione paAzione = pa.getAzione();
		//String nomeazione = paAzione != null ? paAzione.getNome() : "";
		
		switch (operationType) {
		case CostantiDB.CREATE:
			selenium.open("/"+webAppContext+"/porteAppAdd.do?idsogg="+idSogg);
			selenium.type("id", nome);
			break;

		case CostantiDB.UPDATE:
			int idPA = getIdPortaApplicativa(selenium, webAppContext, pa.getTipoSoggettoProprietario(), pa.getNomeSoggettoProprietario(), pa.getIdSoggetto(), nome);
			selenium.open("/"+webAppContext+"/porteAppChange.do?id="+nome+"&idsogg="+idSogg+"&idPorta="+idPA);
			String integrazione = pa.getIntegrazione()!=null ? pa.getIntegrazione() : "";
			selenium.type("integrazione", integrazione);
			break;
		}
		
		
		selenium.type("descr", descr);
		selenium.select("soggvirt", "-");
		selenium.select("ricsim", "label="+ricevutaSimmet);
		selenium.select("ricasim", "label="+ricevutaAsimmet);
		if(pa.getAutorizzazioneContenuto()!=null){
			selenium.type("autorizzazioneContenuti", pa.getAutorizzazioneContenuto());
		}
		selenium.select("xsd", "label="+validazioneXSD);
		
		// Aspetto che venga caricata la pagina in seguito al cambio di tipo di validazione
		try{
			selenium.waitForPageToLoad(TestUtil.getWaitPageToLoadTime());
		}catch(Exception e){}
		
		if(CostantiConfigurazione.ABILITATO.toString().equals(validazioneXSD) || CostantiConfigurazione.STATO_CON_WARNING_WARNING_ONLY.toString().equals(validazioneXSD)){
			selenium.select("tipo_validazione", "label="+tipoValidazioneXSD);
		}
		
		// Gestione body
		if(CostantiConfigurazione.ABILITATO.toString().equals(pa.getAllegaBody()))
			selenium.select("gestBody", "label=allega");
		else if(CostantiConfigurazione.ABILITATO.toString().equals(pa.getScartaBody()))
			selenium.select("gestBody", "label=scarta");
		else
			selenium.select("gestBody", "label=none");
		
		// Manifest
		if(CostantiConfigurazione.ABILITATO.toString().equals(pa.getGestioneManifest())){
			selenium.select("gestManifest", "label=abilitato");
		}else if(CostantiConfigurazione.DISABILITATO.toString().equals(pa.getGestioneManifest())){
			selenium.select("gestManifest", "label=disabilitato");
		}
		

		selenium.select("servizio", "label="+tipoServizio+"/"+nomeServizio);
		selenium.click("//input[@value='"+CostantiTestSuite.BOTTONE_INVIA+"']");
		selenium.waitForPageToLoad(TestUtil.getWaitPageToLoadTime());
		
		
		/**
		 * Servizi Applicativi
		 */
		int idPA = getIdPortaApplicativa(selenium, webAppContext, pa.getTipoSoggettoProprietario(), pa.getNomeSoggettoProprietario(), pa.getIdSoggetto(), pa.getNome());
		TestUtil.CRUDServiziApplicativi(seleniumClient, false, CostantiDB.DELETE, idPA, pa.getNome(), pa.getServizioApplicativoList(), pa.getIdSoggetto(), false);
		TestUtil.CRUDServiziApplicativi(seleniumClient, false, operationType, idPA, pa.getNome(), pa.getServizioApplicativoList(), pa.getIdSoggetto(), false);
		
		/**
		 * Correlazione Applicativa
		 */
		//cancello
		PortaApplicativa.CRUDCorrelazioneApplicativa(seleniumClient, pa, false, CostantiDB.DELETE);
		//creo/aggiorno
		PortaApplicativa.CRUDCorrelazioneApplicativa(seleniumClient, pa, false, operationType);
		
		/**
		 * MessageSecurity
		 */
		TestUtil.CRUDMessageSecurity(seleniumClient, false, operationType, idPA, pa.getNome(), pa.getStatoMessageSecurity(), pa.getMessageSecurity(), pa.getIdSoggetto(), false);
		
		/**
		 * Properties
		 */
		CRUDProtocolProperties(seleniumClient, pa, idPA, false, CostantiDB.DELETE, idSogg);
		CRUDProtocolProperties(seleniumClient, pa, idPA, false, operationType, idSogg);
		
		
		return selenium;
	}
	
	private static Selenium CRUDProtocolProperties(SeleniumClient seleniumClient,org.openspcoop2.core.config.PortaApplicativa pa,int idPA,boolean validate,int operationType, Long idSogg) throws AssertionError{
		Selenium selenium = seleniumClient.getSeleniumBrowser();
		String webAppContext = seleniumClient.getCtrlstatContext();
		
		//String nomePorta = pa.getNome();
		
		switch (operationType) {
		case CostantiDB.CREATE:
		case CostantiDB.UPDATE:
			
			if(pa.sizeProprietaProtocolloList()>0){
				selenium.open("/"+webAppContext+"/porteAppPropList.do?id="+idPA+"&idsogg="+idSogg);
				for (int i = 0; i < pa.sizeProprietaProtocolloList(); i++) {
					ProprietaProtocollo prop = pa.getProprietaProtocollo(i);
					String nome = prop.getNome();
					String valore = prop.getValore().toString();
					selenium.click("//input[@value='"+CostantiTestSuite.BOTTONE_ADD+"']");
					selenium.waitForPageToLoad(TestUtil.getWaitPageToLoadTime());
					selenium.type("nome", nome);
					//selenium.type("valore", valore);
					selenium.select("valore", "label="+valore);
					selenium.click("//input[@value='"+CostantiTestSuite.BOTTONE_INVIA+"']");
					selenium.waitForPageToLoad(TestUtil.getWaitPageToLoadTime());
					assertEquals(selenium.getText("link="+nome), nome);
				}
			}
			
			
			break;

		case CostantiDB.DELETE:
			selenium.open("/"+webAppContext+"/porteAppPropList.do?id="+idPA+"&idsogg="+idSogg);
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
			
			assertEquals(totLista, 0);
			break;
		}
		
		return selenium;
	}
	
    private static Selenium CRUDCorrelazioneApplicativa(SeleniumClient seleniumClient,org.openspcoop2.core.config.PortaApplicativa pa,boolean validate,int operationType) throws AssertionError{
		Selenium selenium = seleniumClient.getSeleniumBrowser();
		String webAppContext = seleniumClient.getCtrlstatContext();
		String nomePorta = pa.getNome();
		
		int idPA = getIdPortaApplicativa(selenium, webAppContext, pa.getTipoSoggettoProprietario(), pa.getNomeSoggettoProprietario(), pa.getIdSoggetto(), pa.getNome());
		
		switch (operationType) {
		case CostantiDB.CREATE:
		case CostantiDB.UPDATE:
			if(pa.getCorrelazioneApplicativa()!=null){
				selenium.open("/"+webAppContext+"/porteApplicativeCorrAppList.do?idsogg="+pa.getIdSoggetto()+"&id="+idPA+"&nome="+nomePorta);
				
				CorrelazioneApplicativa ca = pa.getCorrelazioneApplicativa();
				if(ca.sizeElementoList()>0){
					for (int i = 0; i < ca.sizeElementoList(); i++) {
						CorrelazioneApplicativaElemento elemento = ca.getElemento(i);
						String nomeElemento=elemento.getNome();
						CorrelazioneApplicativaRichiestaIdentificazione identificazione=elemento.getIdentificazione();
						String pattern=elemento.getPattern();
						
						selenium.click("//input[@value='"+CostantiTestSuite.BOTTONE_ADD+"']");
						selenium.waitForPageToLoad(TestUtil.getWaitPageToLoadTime());
						
						if(nomeElemento!=null)
							selenium.type("elemxml", nomeElemento);
						
						if(CorrelazioneApplicativaRichiestaIdentificazione.DISABILITATO.equals(identificazione)){
							selenium.select("mode", "label="+CostantiConfigurazione.DISABILITATO);
							
							try{
								selenium.waitForPageToLoad(TestUtil.getWaitPageToLoadTime());
							}catch (SeleniumException e) {
								// ingore
							}
							
							//non c'e' nessun valore
						}
						else{
							switch (identificazione) {
							case CONTENT_BASED:
								selenium.select("mode", "label="+identificazione.toString());
								
								try{
									selenium.waitForPageToLoad(TestUtil.getWaitPageToLoadTime());
								}catch (SeleniumException e) {
									// ingore
								}
								
								selenium.type("pattern", pattern);
								break;
							/*case _inputBased:
								selenium.select("mode", "label="+Identificazione._inputBased.toString());
								
								try{
									selenium.waitForPageToLoad(TestUtil.getWaitPageToLoadTime());
								}catch (SeleniumException e) {
									// ingore
								}
								
								//non c'e' pattern
								break;
							case _urlBased:
								selenium.select("mode", "label="+Identificazione._urlBased.toString());
								
								try{
									selenium.waitForPageToLoad(TestUtil.getWaitPageToLoadTime());
								}catch (SeleniumException e) {
									// ingore
								}
								
								selenium.type("pattern", pattern);
								break;*/
							default:
								break;
							}
						}
						selenium.click("//input[@value='"+CostantiTestSuite.BOTTONE_INVIA+"']");
						selenium.waitForPageToLoad(TestUtil.getWaitPageToLoadTime());
						
						if(nomeElemento!=null){
							Assert.assertEquals(selenium.getText("link="+nomeElemento), nomeElemento);
						}else{
							Reporter.log("Check opzione 'all' della correlazione applicativa: "+selenium.getText("link=exact:*"));
							Assert.assertEquals(selenium.getText("link=exact:*"), "*");
						}
					}
					
					//setto scadenza correlazione applicativa
					//la scadenza correlazione puo essere settata solo se esistono correlazioni applicative
					//altrimenti il campo scadcorr non e' disponibile.
					selenium.open("/"+webAppContext+"/porteAppChange.do?id="+pa.getNome()+"&idsogg="+pa.getIdSoggetto()+"&idPorta="+idPA);
					selenium.type("scadcorr", pa.getCorrelazioneApplicativa().getScadenza()!=null ? pa.getCorrelazioneApplicativa().getScadenza() : "");
					
					selenium.click("//input[@value='"+CostantiTestSuite.BOTTONE_INVIA+"']");
					selenium.waitForPageToLoad(TestUtil.getWaitPageToLoadTime());
					
					Assert.assertEquals(selenium.getText("link="+pa.getNome()), pa.getNome());
				}
				
								
			}		
			
			break;
			
		case CostantiDB.DELETE:
			//cancello tutti i servizi applicativi associati a questa porta applicativa
			selenium.open("/"+webAppContext+"/porteApplicativeCorrAppList.do?idsogg="+pa.getIdSoggetto()+"&id="+idPA+"&nome="+nomePorta);
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
}


