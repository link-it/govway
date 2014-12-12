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

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertEquals;

import org.openspcoop2.core.config.Attachments;
import org.openspcoop2.core.config.IndirizzoRisposta;
import org.openspcoop2.core.config.InoltroBusteNonRiscontrate;
import org.openspcoop2.core.config.IntegrationManager;
import org.openspcoop2.core.config.MessaggiDiagnostici;
import org.openspcoop2.core.config.Risposte;
import org.openspcoop2.core.config.Tracciamento;
import org.openspcoop2.core.config.ValidazioneBuste;
import org.openspcoop2.core.config.ValidazioneContenutiApplicativi;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.pdd.logger.LogLevels;
import org.openspcoop2.testsuite.core.CostantiTestSuite;
import org.openspcoop2.testsuite.gui.commons.SeleniumClient;
import org.openspcoop2.testsuite.gui.commons.TestUtil;

import com.thoughtworks.selenium.Selenium;

/**
 * Lib Configurazione Generale
 *
 * @author Stefano Corallo <corallo@link.it>
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ConfigurazioneGenerale {

	public static void create(SeleniumClient seleniumClient,org.openspcoop2.core.config.Configurazione configurazione,boolean validate) throws AssertionError,Exception 
	{
		assertNotNull(seleniumClient,"Il client seleniun e' null.");
		assertNotNull(configurazione,"L' oggetto Configurazione e' null");
		
		Selenium selenium = CRUDOperation(seleniumClient, configurazione, validate, CostantiDB.CREATE);
		
		assertEquals(selenium.isTextPresent("Inoltro buste dev'essere numerico"),false);
		
	}
	
	public static void update(SeleniumClient seleniumClient,org.openspcoop2.core.config.Configurazione configurazione,boolean validate) throws AssertionError,Exception
	{
		assertNotNull(seleniumClient,"Il client seleniun e' null.");
		assertNotNull(configurazione,"L' oggetto Configurazione e' null");
		
		Selenium selenium = CRUDOperation(seleniumClient, configurazione, validate, CostantiDB.UPDATE);
		
		assertEquals(selenium.isTextPresent("Inoltro buste dev'essere numerico"),false);
	}
	
	
	private static Selenium CRUDOperation(SeleniumClient seleniumClient,org.openspcoop2.core.config.Configurazione configurazione,boolean validate,int operationType) throws AssertionError,Exception
	{
		Selenium selenium = seleniumClient.getSeleniumBrowser();
		String webAppContext = seleniumClient.getCtrlstatContext();
					
		selenium.open("/"+webAppContext+"/configurazione.do");
		
		//inoltro buste nn riscontrate
		InoltroBusteNonRiscontrate inoltroBuste = configurazione.getInoltroBusteNonRiscontrate();
		String cadenzaInoltro = inoltroBuste!=null && inoltroBuste.getCadenza()!=null && !"".equals(inoltroBuste.getCadenza()) ? inoltroBuste.getCadenza() : "60";
		selenium.type("inoltromin", cadenzaInoltro);
		
		//Validazione buste messaggio
		ValidazioneBuste validazione = configurazione.getValidazioneBuste();
		String statoValidazione = validazione!=null && validazione.getStato()!=null ? validazione.getStato().toString() : CostantiConfigurazione.DISABILITATO.toString();
		String profcoll = validazione.getProfiloCollaborazione()!=null ? validazione.getProfiloCollaborazione().toString() : CostantiConfigurazione.DISABILITATO.toString();
		String controllo = validazione.getControllo() != null ? validazione.getControllo().toString() : "normale";
		String manifest = validazione.getManifestAttachments() != null  ? validazione.getManifestAttachments().toString() : CostantiConfigurazione.ABILITATO.toString() ;
		selenium.select("stato", "label="+statoValidazione);
		selenium.select("controllo", "label="+controllo);
		selenium.select("profcoll", "label="+profcoll);
		selenium.select("validman", "label="+manifest);
		
		//Messaggi diagnostici
		MessaggiDiagnostici messaggi = configurazione.getMessaggiDiagnostici();
		String severitaLog4J = messaggi.getSeveritaLog4j() !=null ? messaggi.getSeveritaLog4j().toString() : LogLevels.LIVELLO_INFO_INTEGRATION;
		String severita = messaggi.getSeverita()!=null ? messaggi.getSeverita().toString() : LogLevels.LIVELLO_FATAL;
		selenium.select("severita", "label="+severita);
		selenium.select("severitaLog4J", "label="+severitaLog4J);
		
		//tracciamento
		Tracciamento tracciamento =  configurazione.getTracciamento();
		String tracce = tracciamento!=null && tracciamento.getBuste()!=null ? tracciamento.getBuste().toString() : CostantiConfigurazione.ABILITATO.toString();
		String traccDump = tracciamento!=null && tracciamento.getDump()!=null ? tracciamento.getDump().toString() : CostantiConfigurazione.DISABILITATO.toString();
		selenium.select("busta", "label="+tracce);
		selenium.select("dump", "label="+traccDump);
		
		//Integration Manager 
		IntegrationManager integrationM = configurazione.getIntegrationManager();
		String autenticazione = integrationM!=null ? integrationM.getAutenticazione() : CostantiConfigurazione.INVOCAZIONE_SERVIZIO_AUTENTICAZIONE_SSL.toString();
		selenium.select("integman", "label="+autenticazione);
		
		// Validazione contenuti applicativi
		ValidazioneContenutiApplicativi valContenutiApplicativi = configurazione.getValidazioneContenutiApplicativi();
		String statoValidazioneContenuti = valContenutiApplicativi!=null && valContenutiApplicativi.getStato()!=null ? valContenutiApplicativi.getStato().toString() : CostantiConfigurazione.DISABILITATO.toString();
		String tipoValidazioneContenuti = valContenutiApplicativi!=null && valContenutiApplicativi.getTipo()!=null ? valContenutiApplicativi.getTipo().toString() : "xsd";
		selenium.select("xsd", "label="+statoValidazioneContenuti);
		try{
			selenium.waitForPageToLoad(TestUtil.getWaitPageToLoadTime());
		}catch(Exception e){}
		selenium.select("tipo_validazione", "label="+tipoValidazioneContenuti);
		
		//Risposte  
		Risposte risposte = configurazione.getRisposte();
		String connessione = risposte!=null && risposte.getConnessione() !=null ? risposte.getConnessione().toString() : "reply";
		selenium.select("connessione", "label="+connessione);
		
		//Indirizzo telematico 
		IndirizzoRisposta indirizzoRisposta = configurazione.getIndirizzoRisposta();
		String utilizzo = indirizzoRisposta!=null && indirizzoRisposta.getUtilizzo()!=null ? indirizzoRisposta.getUtilizzo().toString() : CostantiConfigurazione.DISABILITATO.toString();
		selenium.select("utilizzo", "label="+utilizzo);
		
		//Manifest attachments
		Attachments att = configurazione.getAttachments();
		String gestione = att!=null && att.getGestioneManifest()!=null ? att.getGestioneManifest().toString() : CostantiConfigurazione.DISABILITATO.toString();
		selenium.select("gestman", "label="+gestione);
		
		selenium.click("//input[@value='"+CostantiTestSuite.BOTTONE_INVIA+"']");
		selenium.waitForPageToLoad(TestUtil.getWaitPageToLoadTime());
		
		
		// Msg Diagnostici Appender
		if(messaggi.sizeOpenspcoopAppenderList()>0){
			TestUtil.CRUDMsgDiagnosticiOpenSPCoopAppender(seleniumClient, validate, operationType, webAppContext, messaggi.getOpenspcoopAppenderList());
		}
		
		// Tracciamento Appender
		if(tracciamento.sizeOpenspcoopAppenderList()>0){
			TestUtil.CRUDTracciamentoOpenSPCoopAppender(seleniumClient, validate, operationType, webAppContext, tracciamento.getOpenspcoopAppenderList());
		}
		
		return selenium;
		
	}
}


