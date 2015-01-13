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

import org.openspcoop2.core.config.CorrelazioneApplicativa;
import org.openspcoop2.core.config.CorrelazioneApplicativaElemento;
import org.openspcoop2.core.config.PortaDelegataAzione;
import org.openspcoop2.core.config.PortaDelegataServizio;
import org.openspcoop2.core.config.PortaDelegataSoggettoErogatore;
import org.openspcoop2.core.config.constants.CorrelazioneApplicativaRichiestaIdentificazione;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.core.config.constants.PortaDelegataAzioneIdentificazione;
import org.openspcoop2.core.config.constants.PortaDelegataServizioIdentificazione;
import org.openspcoop2.core.config.constants.PortaDelegataSoggettoErogatoreIdentificazione;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.testsuite.core.CostantiTestSuite;
import org.openspcoop2.testsuite.gui.commons.SeleniumClient;
import org.openspcoop2.testsuite.gui.commons.TestUtil;
import org.testng.Assert;
import org.testng.Reporter;

import com.thoughtworks.selenium.Selenium;
import com.thoughtworks.selenium.SeleniumException;

/**
 *
 *
 * @author Stefano Corallo <corallo@link.it>
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class PortaDelegata {

	public static void create(SeleniumClient seleniumClient,org.openspcoop2.core.config.PortaDelegata pd,boolean validate, Long idSogg) throws AssertionError 
	{
		
		Assert.assertNotNull(seleniumClient,"Il client seleniun e' null.");
		Assert.assertNotNull(pd,"L' oggetto Porta Delegata e' null");
		
		Selenium selenium=PortaDelegata.CRUDOperation(seleniumClient, pd, validate, CostantiDB.CREATE, idSogg);
		String webAppContext = seleniumClient.getCtrlstatContext();
		selenium.open("/"+webAppContext+"/porteDelegateList.do?idsogg="+idSogg+"&tipoprov="+pd.getTipoSoggettoProprietario()+"&nomeprov="+pd.getNomeSoggettoProprietario());
		Assert.assertEquals(selenium.getText("link="+pd.getNome()), pd.getNome());
		
	}
	
	public static void update(SeleniumClient seleniumClient,org.openspcoop2.core.config.PortaDelegata pd,boolean validate, Long idSogg) throws AssertionError
	{
		Assert.assertNotNull(seleniumClient,"Il client seleniun e' null.");
		Assert.assertNotNull(pd,"L' oggetto Porta Delegata e' null");
		
		Selenium selenium = PortaDelegata.CRUDOperation(seleniumClient, pd, validate, CostantiDB.UPDATE, idSogg);
		
		String webAppContext = seleniumClient.getCtrlstatContext();
		selenium.open("/"+webAppContext+"/porteDelegateList.do?idsogg="+idSogg+"&tipoprov="+pd.getTipoSoggettoProprietario()+"&nomeprov="+pd.getNomeSoggettoProprietario());
		Assert.assertEquals(selenium.getText("link="+pd.getNome()), pd.getNome());
		if(pd.getDescrizione()!=null && !"".equals(pd.getDescrizione())){
			Assert.assertEquals(selenium.isTextPresent(pd.getDescrizione()),true);
		}
		
	}
	
	public static void list(SeleniumClient seleniumClient,org.openspcoop2.core.config.PortaDelegata pd,boolean validate) throws AssertionError
	{
		Assert.assertNotNull(seleniumClient,"Il client seleniun e' null.");
		Assert.assertNotNull(pd,"L' oggetto Porta Delegata e' null");
		
		String webAppContext = seleniumClient.getCtrlstatContext();
		Selenium selenium = seleniumClient.getSeleniumBrowser();
		selenium.open("/"+webAppContext+"/porteDelegateList.do?idsogg="+pd.getIdSoggetto()+"&tipoprov="+pd.getTipoSoggettoProprietario()+"&nomeprov="+pd.getNomeSoggettoProprietario());
		Assert.assertEquals(selenium.getText("link="+pd.getNome()), pd.getNome());
	}
	
	public static void deleteAll(SeleniumClient seleniumClient,String tipoSogg,String nomeSogg,Long idSogg,boolean validate) throws AssertionError
	{
		Assert.assertNotNull(seleniumClient,"Il client seleniun e' null.");
		//Note: si effettua la cancellazione dell'intera lista.
		String webAppContext = seleniumClient.getCtrlstatContext();
		TestUtil.deleteAllSublist(seleniumClient, webAppContext, "porteDelegateList.do?idsogg="+idSogg+"&tipoprov="+tipoSogg+"&nomeprov="+nomeSogg, validate);
		
	}

	private static int getIdPortaDelegata(Selenium selenium,String webAppContext,String tipoSogg,String nomeSogg,Long idSogg,String nomePorta) throws AssertionError{
		selenium.open("/"+webAppContext+"/porteDelegateList.do?idsogg="+idSogg+"&tipoprov="+tipoSogg+"&nomeprov="+nomeSogg);
		String url = selenium.getAttribute("link="+nomePorta+"@href");
		String idPD = url.substring(("porteDelegateChange.do?idsogg="+idSogg+"&id="+nomePorta+"&idPorta=").length(),url.length());
		try{
			return Integer.parseInt(idPD);
		}catch (Exception e) {
			Reporter.log("Errore durante il recuper dell'id Porta Delegata dalla URL: "+url);
			throw new AssertionError("Errore durante il recuper dell'id Porta Delegata dalla URL: "+url);
		}
	}
	
	private static Selenium CRUDOperation(SeleniumClient seleniumClient,org.openspcoop2.core.config.PortaDelegata pd,boolean validate,int operationType, Long idSogg) throws AssertionError{
		String webAppContext = seleniumClient.getCtrlstatContext();
		
		String nome = pd.getNome();
		String descrizione = pd.getDescrizione()!=null ? pd.getDescrizione() : "";
		String urlInvocazione = pd.getLocation()!=null ? pd.getLocation(): "";
		String autenticazione = pd.getAutenticazione() !=null ? pd.getAutenticazione() : "";
		String autorizzazione = pd.getAutorizzazione() !=null ? pd.getAutorizzazione() : "";
		String ricevutaSimmetrica = pd.getRicevutaAsincronaSimmetrica()!=null ? pd.getRicevutaAsincronaSimmetrica().toString() : CostantiConfigurazione.DISABILITATO.toString();
		String ricevutaAsimmetrica = pd.getRicevutaAsincronaAsimmetrica()!=null ? pd.getRicevutaAsincronaAsimmetrica().toString() : CostantiConfigurazione.DISABILITATO.toString();
		String validazioneXSD = (pd.getValidazioneContenutiApplicativi() != null && pd.getValidazioneContenutiApplicativi().getStato()!=null) ? pd.getValidazioneContenutiApplicativi().getStato().toString() : CostantiConfigurazione.DISABILITATO.toString();
		String tipoValidazioneXSD = (pd.getValidazioneContenutiApplicativi() != null && pd.getValidazioneContenutiApplicativi().getTipo()!=null) ? pd.getValidazioneContenutiApplicativi().getTipo().toString() : "xsd";
		//String proprietario = pd.getTipoSoggettoProprietario()+"/"+pd.getNomeSoggettoProprietario();
		
		Selenium selenium = seleniumClient.getSeleniumBrowser();
		
		switch (operationType) {
		case CostantiDB.CREATE:
			selenium.open("/"+webAppContext+"/porteDelegateAdd.do?idsogg="+idSogg);
			selenium.type("id", nome);
			//proprietario = pd.getTipoSoggettoProprietario()+"/"+pd.getNomeSoggettoProprietario();
			
			break;

		case CostantiDB.UPDATE:
			int idPd = PortaDelegata.getIdPortaDelegata(selenium,webAppContext, pd.getTipoSoggettoProprietario(), pd.getNomeSoggettoProprietario(), pd.getIdSoggetto(), nome);
			selenium.open("/"+webAppContext+"/porteDelegateChange.do?id="+nome+"&idsogg="+idSogg+"&idPorta="+idPd);
			String integrazione = pd.getIntegrazione()!=null ? pd.getIntegrazione() : "";
			
			selenium.type("integrazione", integrazione);
			//String integrazione = pd.getIntegrazione()!=null ? pd.getIntegrazione() : "";
			
//			String oldNomeProp = pd.getOldNomeSoggettoProprietarioForUpdate()!=null ? pd.getOldNomeSoggettoProprietarioForUpdate() : pd.getNomeSoggettoProprietario();
//			String oldTipoProp = pd.getOldTipoSoggettoProprietarioForUpdate()!=null ? pd.getOldTipoSoggettoProprietarioForUpdate() : pd.getTipoSoggettoProprietario();
//			proprietario = oldTipoProp+"/"+oldNomeProp;
//			Reporter.log("Update proprietario: "+proprietario);
			break;
		}
		
		selenium.type("descr", descrizione);
		selenium.type("urlinv", urlInvocazione);
		//autenticazione
		selenium.select("autenticazione", "label="+autenticazione);
		try{
			selenium.waitForPageToLoad(TestUtil.getWaitPageToLoadTime());
		}catch(Exception e){}
		//autorizzazione
		selenium.select("autorizzazione", "label="+autorizzazione);
		try{
			selenium.waitForPageToLoad(TestUtil.getWaitPageToLoadTime());
		}catch(Exception e){}
		
		selenium.select("ricsim", "label="+ricevutaSimmetrica);
		selenium.select("ricasim", "label="+ricevutaAsimmetrica);
		if(pd.getAutorizzazioneContenuto()!=null){
			selenium.type("autorizzazioneContenuti", pd.getAutorizzazioneContenuto());
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
		if(CostantiConfigurazione.ABILITATO.toString().equals(pd.getAllegaBody()))
			selenium.select("gestBody", "label=allega");
		else if(CostantiConfigurazione.ABILITATO.toString().equals(pd.getScartaBody()))
			selenium.select("gestBody", "label=scarta");
		else
			selenium.select("gestBody", "label=none");
		
		// Manifest
		if(CostantiConfigurazione.ABILITATO.toString().equals(pd.getGestioneManifest())){
			selenium.select("gestManifest", "label=abilitato");
		}else if(CostantiConfigurazione.DISABILITATO.toString().equals(pd.getGestioneManifest())){
			selenium.select("gestManifest", "label=disabilitato");
		}
		
		//soggetto erogatore
		PortaDelegataSoggettoErogatore erogatore = pd.getSoggettoErogatore();
		PortaDelegataSoggettoErogatoreIdentificazione modeSoggettoErogatore = (erogatore != null ? erogatore.getIdentificazione() : null);
		String tipoErog = erogatore!=null && erogatore.getTipo()!=null ? erogatore.getTipo() : "";
		String nomeErog = erogatore!=null && erogatore.getNome()!=null ? erogatore.getNome() : "";
		String patternErog = erogatore!=null && erogatore.getPattern()!=null ? erogatore.getPattern() : "";
		if(modeSoggettoErogatore==null || modeSoggettoErogatore.equals("")) 
			modeSoggettoErogatore=PortaDelegataSoggettoErogatoreIdentificazione.STATIC;
		switch (modeSoggettoErogatore) {
		case CONTENT_BASED:
			selenium.select("modesp", "label=content-based");
			try{
				selenium.waitForPageToLoad(TestUtil.getWaitPageToLoadTime());
			}catch(SeleniumException e){
				//ignore
			}
			selenium.type("tiposp", tipoErog);
			selenium.type("patternsp", patternErog);
			
			break;
		case URL_BASED:
			selenium.select("modesp", "label=url-based");
			try{
				selenium.waitForPageToLoad(TestUtil.getWaitPageToLoadTime());
			}catch(SeleniumException e){
				//ignore
			}
			selenium.type("tiposp", tipoErog);
			selenium.type("patternsp", patternErog);
			break;
		case INPUT_BASED:
			selenium.select("modesp", "label=input-based");
			try{
				selenium.waitForPageToLoad(TestUtil.getWaitPageToLoadTime());
			}catch(SeleniumException e){
				//ignore
			}
			selenium.type("tiposp", tipoErog);
			break;
		case STATIC:
			selenium.select("modesp", "label=user-input");
			try{
				selenium.waitForPageToLoad(TestUtil.getWaitPageToLoadTime());
			}catch(SeleniumException e){
				//ignore
			}
			selenium.type("tiposp", tipoErog);
			selenium.type("sp", nomeErog);
			break;
		default:
			break;
		}
		
		//servizio
		PortaDelegataServizio servizio = pd.getServizio();
		Assert.assertNotNull(servizio,"L'oggetto PortaDelegataServizio e' null");
		PortaDelegataServizioIdentificazione modeServizio = (servizio != null ? servizio.getIdentificazione() : null);
		if(modeServizio==null || modeServizio.equals("")) 
			modeServizio=PortaDelegataServizioIdentificazione.STATIC;
		String tipoServizio = servizio!=null && servizio.getTipo() != null ? servizio.getTipo() : "";
		String nomeServizio = servizio!=null && servizio.getNome()!= null ? servizio.getNome() : "";
		String patternServizio = servizio!=null && servizio.getPattern()!= null ? servizio.getPattern() : "";
		//campi obbligatori
		switch (modeServizio) {
		case CONTENT_BASED:
			selenium.select("modeservizio", "label=content-based");
			try{
				selenium.waitForPageToLoad(TestUtil.getWaitPageToLoadTime());
			}catch(SeleniumException e){
				//ignore
			}
		
            selenium.type("tiposervizio", tipoServizio);
            selenium.type("patternserv", patternServizio);
			break;
		case URL_BASED:
			selenium.select("modeservizio", "label=url-based");
			try{
				selenium.waitForPageToLoad(TestUtil.getWaitPageToLoadTime());
			}catch(SeleniumException e){
				//ignore
			}
            selenium.type("tiposervizio", tipoServizio);
            selenium.type("patternserv", patternServizio);
			break;
		case INPUT_BASED:
			selenium.select("modeservizio", "label=input-based");
			try{
				selenium.waitForPageToLoad(TestUtil.getWaitPageToLoadTime());
			}catch(SeleniumException e){
				//ignore
			}
			selenium.type("tiposervizio", tipoServizio);
            
			break;
		case STATIC:
			selenium.select("modeservizio", "label=user-input");
			try{
				selenium.waitForPageToLoad(TestUtil.getWaitPageToLoadTime());
			}catch(SeleniumException e){
				//ignore
			}
			selenium.type("tiposervizio", tipoServizio);
            selenium.type("servizio", nomeServizio);
			break;
		default:
			break;
		}        
        		
		//Azione
		PortaDelegataAzione azione = pd.getAzione();
		//Se il bean Azione nn e' presente allora non controllo nulla
		if(azione!=null){
			String nomeAzione = azione.getNome()!=null ? azione.getNome() : "";
			String patternAzione = azione.getPattern()!=null ? azione.getPattern() : "";
			PortaDelegataAzioneIdentificazione modeAzione = azione.getIdentificazione()!=null? azione.getIdentificazione() : null;
			if(modeAzione==null || modeAzione.equals("")) 
				modeAzione = PortaDelegataAzioneIdentificazione.STATIC;
			switch (modeAzione) {
			case CONTENT_BASED:
				selenium.select("modeaz", "label=content-based");
				try{
					selenium.waitForPageToLoad(TestUtil.getWaitPageToLoadTime());
				}catch(SeleniumException e){
					//ignore
				}
				selenium.type("patternaz", patternAzione);
				break;
			case URL_BASED:
				selenium.select("modeaz", "label=url-based");
				try{
					selenium.waitForPageToLoad(TestUtil.getWaitPageToLoadTime());
				}catch(SeleniumException e){
					//ignore
				}
				selenium.type("patternaz", patternAzione);
				break;
			case INPUT_BASED:
				selenium.select("modeaz", "label=input-based");
				try{
					selenium.waitForPageToLoad(TestUtil.getWaitPageToLoadTime());
				}catch(SeleniumException e){
					//ignore
				}
				break;
			case STATIC:
				selenium.select("modeaz", "label=user-input");
				try{
					selenium.waitForPageToLoad(TestUtil.getWaitPageToLoadTime());
				}catch(SeleniumException e){
					//ignore
				}
				selenium.type("azione", nomeAzione);
				break;
			default:
				break;
			}
		}
		
		selenium.click("//input[@value='"+CostantiTestSuite.BOTTONE_INVIA+"']");
		selenium.waitForPageToLoad(TestUtil.getWaitPageToLoadTime());
		
		
		int idPD = PortaDelegata.getIdPortaDelegata(selenium, webAppContext, pd.getTipoSoggettoProprietario(), pd.getNomeSoggettoProprietario(), pd.getIdSoggetto(), pd.getNome());
		/**
		 * Servizi applicativi
		 */
		//prima cancello i servizi applicativi (in caso fossi in modifica
		TestUtil.CRUDServiziApplicativi(seleniumClient,false, CostantiDB.DELETE,idPD,pd.getNome(),pd.getServizioApplicativoList(), pd.getIdSoggetto(),true);
		//creo/aggiorno servizi applicativi associati alla porta delegata
		TestUtil.CRUDServiziApplicativi(seleniumClient,false, operationType,idPD,pd.getNome(),pd.getServizioApplicativoList(), pd.getIdSoggetto(),true);
		
		/**
		 * Correlazione Applicativa
		 */
		//cancello
		PortaDelegata.CRUDCorrelazioneApplicativa(seleniumClient, pd, false, CostantiDB.DELETE);
		//creo/aggiorno
		PortaDelegata.CRUDCorrelazioneApplicativa(seleniumClient, pd, false, operationType);
		
		/**
		 * MessageSecurity
		 */
		TestUtil.CRUDMessageSecurity(seleniumClient, false, operationType,idPD,pd.getNome(),pd.getStatoMessageSecurity(),pd.getMessageSecurity(), pd.getIdSoggetto(),true);
		
		return selenium;
	}
			
	private static Selenium CRUDCorrelazioneApplicativa(SeleniumClient seleniumClient,org.openspcoop2.core.config.PortaDelegata pd,boolean validate,int operationType) throws AssertionError{
		Selenium selenium = seleniumClient.getSeleniumBrowser();
		String webAppContext = seleniumClient.getCtrlstatContext();
		String nomePorta = pd.getNome();
		
		int idPD = PortaDelegata.getIdPortaDelegata(selenium, webAppContext, pd.getTipoSoggettoProprietario(), pd.getNomeSoggettoProprietario(), pd.getIdSoggetto(), nomePorta);
		
		switch (operationType) {
		case CostantiDB.CREATE:
		case CostantiDB.UPDATE:
			if(pd.getCorrelazioneApplicativa()!=null){
				selenium.open("/"+webAppContext+"/porteDelegateCorrAppList.do?idsogg="+pd.getIdSoggetto()+"&id="+idPD+"&nome="+nomePorta);
				
				CorrelazioneApplicativa ca = pd.getCorrelazioneApplicativa();
				if(ca.sizeElementoList()>0){
					for (int i = 0; i < ca.sizeElementoList(); i++) {
						CorrelazioneApplicativaElemento elemento = ca.getElemento(i);
						String nomeElemento=elemento.getNome();
						CorrelazioneApplicativaRichiestaIdentificazione identificazione=elemento.getIdentificazione();
						String pattern=elemento.getPattern();
						StatoFunzionalita riusoIdMessaggio = elemento.getRiusoIdentificativo();
						
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
								if(riusoIdMessaggio!=null)
									selenium.type("riuso", riusoIdMessaggio.toString());
								break;
							case INPUT_BASED:
								selenium.select("mode", "label="+identificazione.toString());
								
								try{
									selenium.waitForPageToLoad(TestUtil.getWaitPageToLoadTime());
								}catch (SeleniumException e) {
									// ingore
								}
								
								//non c'e' pattern
								if(riusoIdMessaggio!=null)
									selenium.type("riuso", riusoIdMessaggio.toString());
								break;
							case URL_BASED:
								selenium.select("mode", "label="+identificazione.toString());
								
								try{
									selenium.waitForPageToLoad(TestUtil.getWaitPageToLoadTime());
								}catch (SeleniumException e) {
									// ingore
								}
								
								selenium.type("pattern", pattern);
								if(riusoIdMessaggio!=null)
									selenium.type("riuso", riusoIdMessaggio.toString());
								break;
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
					selenium.open("/"+webAppContext+"/porteDelegateChange.do?id="+pd.getNome()+"&idsogg="+pd.getIdSoggetto()+"&idPorta="+idPD);
					selenium.type("scadcorr", pd.getCorrelazioneApplicativa().getScadenza()!=null ? pd.getCorrelazioneApplicativa().getScadenza() : "");
					
					selenium.click("//input[@value='"+CostantiTestSuite.BOTTONE_INVIA+"']");
					selenium.waitForPageToLoad(TestUtil.getWaitPageToLoadTime());
					
					Assert.assertEquals(selenium.getText("link="+pd.getNome()), pd.getNome());
				}
				
								
			}		
			
			break;
			
		case CostantiDB.DELETE:
			//cancello tutti i servizi applicativi associati a questa porta delegata
			selenium.open("/"+webAppContext+"/porteDelegateCorrAppList.do?idsogg="+pd.getIdSoggetto()+"&id="+idPD+"&nome="+nomePorta);
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


