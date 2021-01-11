/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it). 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3, as published by
 * the Free Software Foundation.
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



package org.openspcoop2.protocol.spcoop.testsuite.units.integrazione;

import javax.xml.soap.SOAPException;

import org.openspcoop2.testsuite.core.TestSuiteException;
import org.testng.annotations.AfterGroups;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Test su richieste applicative malformate indirizzate alla Porta di Dominio
 * 
 * @author Andi Rexha (rexha@openspcoop.org)
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class RichiesteApplicativeScorretteGenericCode extends RichiesteApplicativeScorrette {

	public RichiesteApplicativeScorretteGenericCode() {
		super(true);
	}
		



	
	@BeforeGroups (alwaysRun=true , groups=ID_GRUPPO)
	public void testOpenspcoopCoreLog_raccoltaTempoAvvioTest() throws Exception{
		super._testOpenspcoopCoreLog_raccoltaTempoAvvioTest();
	} 	
	@AfterGroups (alwaysRun=true , groups=ID_GRUPPO)
	public void testOpenspcoopCoreLog() throws Exception{
		super._testOpenspcoopCoreLog();
	} 

	
	
	
	/**
	 * Porta delegata non esistente
	 * "401";
	 */
	@DataProvider (name="porteDelegateInesistenti")
	public Object[][] porteDelegateInesistenti(){
		return super._porteDelegateInesistenti();
	}
	@Test(groups={CostantiIntegrazione.ID_GRUPPO_INTEGRAZIONE,RichiesteApplicativeScorrette.ID_GRUPPO,RichiesteApplicativeScorrette.ID_GRUPPO+".401"},dataProvider="porteDelegateInesistenti")
	public void testPorteDelegateInesistenti(String portaDelegata) throws Exception{
		super._testPorteDelegateInesistenti(portaDelegata);
	}

	
	
	
	/**
	 * Autenticazione fallita
	 * "402";
	 * 
	 * messaggio: credenziali non fornite
	 */
	@DataProvider(name="credenzialiNonFornite")
	public Object[][] credenzialiNonFornite(){
		return super._credenzialiNonFornite();
	}
	@Test(groups={CostantiIntegrazione.ID_GRUPPO_INTEGRAZIONE,RichiesteApplicativeScorrette.ID_GRUPPO,RichiesteApplicativeScorrette.ID_GRUPPO+".402"},dataProvider="credenzialiNonFornite")
	public void testAutenticazioneCredenzialiNonFornite(String username,String password) throws Exception{
		super._testAutenticazioneCredenzialiNonFornite(username, password);
	}
	
	/**
	 * Autenticazione fallita
	 * "402";
	 * 
	 * messaggio: credenziali fornite non corrette
	 */
	@DataProvider(name="credenzialiScorrette")
	public Object[][] credenzialiScorrette(){
		return super._credenzialiScorrette();
	}
	@Test(groups={CostantiIntegrazione.ID_GRUPPO_INTEGRAZIONE,RichiesteApplicativeScorrette.ID_GRUPPO,RichiesteApplicativeScorrette.ID_GRUPPO+".402"},dataProvider="credenzialiScorrette",
			dependsOnMethods="testAutenticazioneCredenzialiNonFornite")
	public void testAutenticazioneCredenzialiScorrette(String username,String password) throws Exception{
		super._testAutenticazioneCredenzialiScorrette(username, password);
	}
	
	/**
	 * Autenticazione fallita
	 * "402";
	 * 
	 * messaggio: identità del servizio applicativo fornita non esiste nella configurazione
	 */
	@Test(groups={CostantiIntegrazione.ID_GRUPPO_INTEGRAZIONE,RichiesteApplicativeScorrette.ID_GRUPPO,RichiesteApplicativeScorrette.ID_GRUPPO+".402"},
			dependsOnMethods="testAutenticazioneCredenzialiScorrette")
	public void testAutenticazioneSANonEsistente() throws Exception{
		super._testAutenticazioneSANonEsistente();
	}

	
	
	
	
	/**
	 * Pattern Ricerca Porta Delegata Non Validi
	 * "403";
	 */
	
	// CONTENT-BASED
	@Test(groups={CostantiIntegrazione.ID_GRUPPO_INTEGRAZIONE,RichiesteApplicativeScorrette.ID_GRUPPO,RichiesteApplicativeScorrette.ID_GRUPPO+".403"})
	public void testParametriIdentificazionePortaDelegataNonValidi_contentBased() throws Exception{
		super._testParametriIdentificazionePortaDelegataNonValidi_contentBased();
	}
	
	// URL-BASED
	@Test(groups={CostantiIntegrazione.ID_GRUPPO_INTEGRAZIONE,RichiesteApplicativeScorrette.ID_GRUPPO,RichiesteApplicativeScorrette.ID_GRUPPO+".403"},
			dependsOnMethods="testParametriIdentificazionePortaDelegataNonValidi_contentBased")
	public void testParametriIdentificazionePortaDelegataNonValidi_urlBased() throws Exception{
		super._testParametriIdentificazionePortaDelegataNonValidi_urlBased();
	}
	
	// URL-FORM-BASED
	@Test(groups={CostantiIntegrazione.ID_GRUPPO_INTEGRAZIONE,RichiesteApplicativeScorrette.ID_GRUPPO,RichiesteApplicativeScorrette.ID_GRUPPO+".403"},
			dependsOnMethods="testParametriIdentificazionePortaDelegataNonValidi_urlBased")
	public void testParametriIdentificazionePortaDelegataNonValidi_urlFormBased() throws Exception{
		super._testParametriIdentificazionePortaDelegataNonValidi_urlFormBased();
	}
	
	// INPUT-BASED
	@Test(groups={CostantiIntegrazione.ID_GRUPPO_INTEGRAZIONE,RichiesteApplicativeScorrette.ID_GRUPPO,RichiesteApplicativeScorrette.ID_GRUPPO+".403"},
			dependsOnMethods="testParametriIdentificazionePortaDelegataNonValidi_urlFormBased")
	public void testParametriIdentificazionePortaDelegataNonValidi_inputBased() throws Exception{
		super._testParametriIdentificazionePortaDelegataNonValidi_inputBased();
	}

	
	
	
	/**
	 * Autorizzazione Fallita
	 * "404";
	 */
	@Test(groups={CostantiIntegrazione.ID_GRUPPO_INTEGRAZIONE,RichiesteApplicativeScorrette.ID_GRUPPO,RichiesteApplicativeScorrette.ID_GRUPPO+".404"})
	public void testAutorizzazioneFallita() throws Exception{
		super._testAutorizzazioneFallita();
	}

	
	
	
	/**
	 * Servizio SPCoop abbinato alla Porta Delegata Inesistente
	 * "405";
	 */
	@Test(groups={CostantiIntegrazione.ID_GRUPPO_INTEGRAZIONE,RichiesteApplicativeScorrette.ID_GRUPPO,RichiesteApplicativeScorrette.ID_GRUPPO+".405"})
	public void testServizioSPCoopNonEsistente() throws Exception{
		super._testServizioSPCoopNonEsistente();
	}

	
	
	
	/**
	 * Nessun Messaggio disponibile per il Servizio Applicativo (Integration Manager)
	 * "406";
	 */
	@Test(groups={CostantiIntegrazione.ID_GRUPPO_INTEGRAZIONE,RichiesteApplicativeScorrette.ID_GRUPPO,RichiesteApplicativeScorrette.ID_GRUPPO+".406"})
	public void testIM_messaggiNonDisponibili() throws Exception{
		super._testIM_messaggiNonDisponibili();
	}

	
	
	
	/**
	 * Messaggio Richiesto Inesistente (Integration Manager)
	 * "407";
	 */
	@Test(groups={CostantiIntegrazione.ID_GRUPPO_INTEGRAZIONE,RichiesteApplicativeScorrette.ID_GRUPPO,RichiesteApplicativeScorrette.ID_GRUPPO+".407"})
	public void testIM_messaggioNonEsistente() throws Exception{
		super._testIM_messaggioNonEsistente();
	}

	
	
	
	/**
	 * Servizio Correlato associato ad un Servizio Asincrono non esistente
	 * "408";
	 */
	@Test(groups={CostantiIntegrazione.ID_GRUPPO_INTEGRAZIONE,RichiesteApplicativeScorrette.ID_GRUPPO,RichiesteApplicativeScorrette.ID_GRUPPO+".408"})
	public void testServizioCorrelatoNonEsistenteAS_wrap() throws Exception{
		super._testServizioCorrelatoNonEsistenteAS(false);
	}
	
	@Test(groups={CostantiIntegrazione.ID_GRUPPO_INTEGRAZIONE,RichiesteApplicativeScorrette.ID_GRUPPO,RichiesteApplicativeScorrette.ID_GRUPPO+".408"})
	public void testServizioCorrelatoNonEsistenteAS_unwrap() throws Exception{
		super._testServizioCorrelatoNonEsistenteAS(true);
	}
	
	@Test(groups={CostantiIntegrazione.ID_GRUPPO_INTEGRAZIONE,RichiesteApplicativeScorrette.ID_GRUPPO,RichiesteApplicativeScorrette.ID_GRUPPO+".408"}, 
			dependsOnMethods="testServizioCorrelatoNonEsistenteAS_wrap")
	public void testServizioCorrelatoNonEsistenteAA_wrap() throws Exception{
		super._testServizioCorrelatoNonEsistenteAA(false);
	}
	
	@Test(groups={CostantiIntegrazione.ID_GRUPPO_INTEGRAZIONE,RichiesteApplicativeScorrette.ID_GRUPPO,RichiesteApplicativeScorrette.ID_GRUPPO+".408"}, 
			dependsOnMethods="testServizioCorrelatoNonEsistenteAS_unwrap")
	public void testServizioCorrelatoNonEsistenteAA_unwrap() throws Exception{
		super._testServizioCorrelatoNonEsistenteAA(true);
	}

	
	
	
	/**
	 * Risposta/RichiestaStato asincrona non correlata ad una precedente richiesta
	 * "409";
	 */
	@Test(groups={CostantiIntegrazione.ID_GRUPPO_INTEGRAZIONE,RichiesteApplicativeScorrette.ID_GRUPPO,RichiesteApplicativeScorrette.ID_GRUPPO+".409"})
	public void testRispostaAsincronaSimmetricaNonGenerabile() throws Exception{
		
		super._testRispostaAsincronaSimmetricaNonGenerabile();
	}
	
	@Test(groups={CostantiIntegrazione.ID_GRUPPO_INTEGRAZIONE,RichiesteApplicativeScorrette.ID_GRUPPO,RichiesteApplicativeScorrette.ID_GRUPPO+".409"},
			dependsOnMethods="testRispostaAsincronaSimmetricaNonGenerabile")
	public void testRispostaAsincronaAsimmetricaNonGenerabile() throws Exception{
		
		super._testRispostaAsincronaAsimmetricaNonGenerabile();
	}

	
	
	
	/**
	 * Autenticazione richiesta per l'invocazione della Porta Delegata
	 * "410";
	 */
	@Test(groups={CostantiIntegrazione.ID_GRUPPO_INTEGRAZIONE,RichiesteApplicativeScorrette.ID_GRUPPO,RichiesteApplicativeScorrette.ID_GRUPPO+".410"})
	public void testAutenticazioneRichiestaServizioApplicativoAsincronoSimmetrico() throws Exception{
		super._testAutenticazioneRichiestaServizioApplicativoAsincronoSimmetrico();
	}

	
	
	
	/**
	 * Elemento Risposta Asincrona richiesto per l'invocazione della Porta Delegata
	 * "411";
	 */
	@Test(groups={CostantiIntegrazione.ID_GRUPPO_INTEGRAZIONE,RichiesteApplicativeScorrette.ID_GRUPPO,RichiesteApplicativeScorrette.ID_GRUPPO+".411"})
	public void testElementoRispostaAsincronaNonPresente_wrap() throws Exception{
		super._testElementoRispostaAsincronaNonPresente(false);
	}
	
	@Test(groups={CostantiIntegrazione.ID_GRUPPO_INTEGRAZIONE,RichiesteApplicativeScorrette.ID_GRUPPO,RichiesteApplicativeScorrette.ID_GRUPPO+".411"})
	public void testElementoRispostaAsincronaNonPresente_unwrap() throws Exception{
		super._testElementoRispostaAsincronaNonPresente(true);
	}

	
	
	
	/**
	 * Porta Delegata invocabile dal servizio applicativo solo per riferimento
	 * "412";
	 */
	@Test(groups={CostantiIntegrazione.ID_GRUPPO_INTEGRAZIONE,RichiesteApplicativeScorrette.ID_GRUPPO,RichiesteApplicativeScorrette.ID_GRUPPO+".412"})
	public void testInvioPerRiferimento() throws Exception{
		super._testInvioPerRiferimento();
	}

	
	
	
	/**
	 * Porta Delegata invocabile dal servizio applicativo solo senza riferimento
	 * "413";
	 */
	@Test(groups={CostantiIntegrazione.ID_GRUPPO_INTEGRAZIONE,RichiesteApplicativeScorrette.ID_GRUPPO,RichiesteApplicativeScorrette.ID_GRUPPO+".413"})
	public void testInvioPerRiferimentoNonAutorizzato() throws Exception{
		super._testInvioPerRiferimentoNonAutorizzato();
	}

	
	
	
	/**
	 * Consegna in ordine utilizzabile sono con profilo Oneway
	 * "414";
	 */
	@Test(groups={CostantiIntegrazione.ID_GRUPPO_INTEGRAZIONE,RichiesteApplicativeScorrette.ID_GRUPPO,RichiesteApplicativeScorrette.ID_GRUPPO+".414"})
	public void testConsegnaOrdineProfiloSincrono() throws Exception{
		super._testConsegnaOrdineProfiloSincrono();
	}
	
	
	
	/**
	 * Consegna in ordine non utilizzabile per mancanza di dati necessari
	 * "415";
	 */
	@Test(groups={CostantiIntegrazione.ID_GRUPPO_INTEGRAZIONE,RichiesteApplicativeScorrette.ID_GRUPPO,RichiesteApplicativeScorrette.ID_GRUPPO+".415"})
	public void testConsegnaOrdineConfigurazioneErrata_confermaRicezioneMancante() throws Exception{
		super._testConsegnaOrdineConfigurazioneErrata_confermaRicezioneMancante();
	}
	
	@Test(groups={CostantiIntegrazione.ID_GRUPPO_INTEGRAZIONE,RichiesteApplicativeScorrette.ID_GRUPPO,RichiesteApplicativeScorrette.ID_GRUPPO+".415"},
			dependsOnMethods="testConsegnaOrdineConfigurazioneErrata_confermaRicezioneMancante")
	public void testConsegnaOrdineConfigurazioneErrata_filtroDuplicatiMancante() throws Exception{
		super._testConsegnaOrdineConfigurazioneErrata_filtroDuplicatiMancante();
	}
	
	@Test(groups={CostantiIntegrazione.ID_GRUPPO_INTEGRAZIONE,RichiesteApplicativeScorrette.ID_GRUPPO,RichiesteApplicativeScorrette.ID_GRUPPO+".415"},
			dependsOnMethods="testConsegnaOrdineConfigurazioneErrata_filtroDuplicatiMancante")
	public void testConsegnaOrdineConfigurazioneErrata_idCollaborazioneMancante() throws Exception{
		super._testConsegnaOrdineConfigurazioneErrata_idCollaborazioneMancante();
	}

	
	
	
	/**
	 * Correlazione Applicativa non riuscita
	 * "416";
	 */
	@Test(groups={CostantiIntegrazione.ID_GRUPPO_INTEGRAZIONE,RichiesteApplicativeScorrette.ID_GRUPPO,RichiesteApplicativeScorrette.ID_GRUPPO+".416"})
	public void testCorrelazioneApplicativaErrata() throws Exception{
		super._testCorrelazioneApplicativaErrata();
	}

	
	
	
	/**
	 * Impossibile istanziare un validatore: XSD non valido o mancante
	 * "417";
	 */
	@Test(groups={CostantiIntegrazione.ID_GRUPPO_INTEGRAZIONE,RichiesteApplicativeScorrette.ID_GRUPPO,RichiesteApplicativeScorrette.ID_GRUPPO+".417"})
	public void testValidazioneApplicativaSenzaXsd_tipoXSD_wrap() throws Exception{
		super._testValidazioneApplicativaSenzaXsd_tipoXSD(false);
	}
	
	@Test(groups={CostantiIntegrazione.ID_GRUPPO_INTEGRAZIONE,RichiesteApplicativeScorrette.ID_GRUPPO,RichiesteApplicativeScorrette.ID_GRUPPO+".417"})
	public void testValidazioneApplicativaSenzaXsd_tipoXSD_unwrap() throws Exception{
		super._testValidazioneApplicativaSenzaXsd_tipoXSD(true);
	}
	
	@Test(groups={CostantiIntegrazione.ID_GRUPPO_INTEGRAZIONE,RichiesteApplicativeScorrette.ID_GRUPPO,RichiesteApplicativeScorrette.ID_GRUPPO+".417"},
			dependsOnMethods="testValidazioneApplicativaSenzaXsd_tipoXSD_wrap")
	public void testValidazioneApplicativaSenzaXsd_tipoWSDL_wrap() throws Exception{
		super._testValidazioneApplicativaSenzaXsd_tipoWSDL(false);
	}
	
	@Test(groups={CostantiIntegrazione.ID_GRUPPO_INTEGRAZIONE,RichiesteApplicativeScorrette.ID_GRUPPO,RichiesteApplicativeScorrette.ID_GRUPPO+".417"},
			dependsOnMethods="testValidazioneApplicativaSenzaXsd_tipoXSD_unwrap")
	public void testValidazioneApplicativaSenzaXsd_tipoWSDL_unwrap() throws Exception{
		super._testValidazioneApplicativaSenzaXsd_tipoWSDL(true);
	}
	
	@Test(groups={CostantiIntegrazione.ID_GRUPPO_INTEGRAZIONE,RichiesteApplicativeScorrette.ID_GRUPPO,RichiesteApplicativeScorrette.ID_GRUPPO+".417"},
			dependsOnMethods="testValidazioneApplicativaSenzaXsd_tipoWSDL_wrap")
	public void testValidazioneApplicativaSenzaXsd_tipoOPENSPCOOP_wrap() throws Exception{
		super._testValidazioneApplicativaSenzaXsd_tipoOPENSPCOOP(false);
	}
	
	@Test(groups={CostantiIntegrazione.ID_GRUPPO_INTEGRAZIONE,RichiesteApplicativeScorrette.ID_GRUPPO,RichiesteApplicativeScorrette.ID_GRUPPO+".417"},
			dependsOnMethods="testValidazioneApplicativaSenzaXsd_tipoWSDL_unwrap")
	public void testValidazioneApplicativaSenzaXsd_tipoOPENSPCOOP_unwrap() throws Exception{
		super._testValidazioneApplicativaSenzaXsd_tipoOPENSPCOOP(true);
	}

	
	
	
	/**
	 * Validazione del messaggio di richiesta fallita
	 * "418";
	 */
	@Test(groups={CostantiIntegrazione.ID_GRUPPO_INTEGRAZIONE,RichiesteApplicativeScorrette.ID_GRUPPO,RichiesteApplicativeScorrette.ID_GRUPPO+".418"})
	public void testValidazioneApplicativaRichiestaFallita() throws Exception{
		super._testValidazioneApplicativaRichiestaFallita();
		
	}
	
	
	
	
	/**
	 * Validazione del messaggio di risposta fallita
	 * "419";
	 */
	@Test(groups={CostantiIntegrazione.ID_GRUPPO_INTEGRAZIONE,RichiesteApplicativeScorrette.ID_GRUPPO,RichiesteApplicativeScorrette.ID_GRUPPO+".419"})
	public void testValidazioneApplicativaRispostaFallita_wrap() throws Exception{
		super._testValidazioneApplicativaRispostaFallita(false);
	}
	
	@Test(groups={CostantiIntegrazione.ID_GRUPPO_INTEGRAZIONE,RichiesteApplicativeScorrette.ID_GRUPPO,RichiesteApplicativeScorrette.ID_GRUPPO+".419"})
	public void testValidazioneApplicativaRispostaFallita_unwrap() throws Exception{
		super._testValidazioneApplicativaRispostaFallita(true);
	}

	
	
	
	/**
	 * Busta E-Gov presente nel messaggio di richiesta
	 * "420";
	 */
	@Test(groups={CostantiIntegrazione.ID_GRUPPO_INTEGRAZIONE,RichiesteApplicativeScorrette.ID_GRUPPO,RichiesteApplicativeScorrette.ID_GRUPPO+".420"})
	public void TestBustaEGovInviataVersoPortaDelegata() throws Exception{
		super._testBustaEGovInviataVersoPortaDelegata();
	}

	
	
	
	/**
	 * Il messaggio di richiesta utilizzato con IM per invocare la Porta Delegata non rispetta il formato SOAP
	 * "421";
	 */
	@Test(groups={CostantiIntegrazione.ID_GRUPPO_INTEGRAZIONE,RichiesteApplicativeScorrette.ID_GRUPPO,RichiesteApplicativeScorrette.ID_GRUPPO+".421"})
	public void testInvioMessaggioNonSOAPConXML() throws Exception{
		super._testInvioMessaggioNonSOAPConXML();
	}

	
	
	
	/**
	 * Il messaggio di richiesta utilizzato con il tunnel SOAP e  con IM per invocare la Porta Delegata non e' imbustabile
	 * "422";
	 */
	@Test(groups={CostantiIntegrazione.ID_GRUPPO_INTEGRAZIONE,RichiesteApplicativeScorrette.ID_GRUPPO,RichiesteApplicativeScorrette.ID_GRUPPO+".422"})
	public void testInvioMessaggioTramiteTunnelSOAP_nonImbustabileInSOAP() throws Exception{
		super._testInvioMessaggioTramiteTunnelSOAP_nonImbustabileInSOAP();
	}
	
	@Test(groups={CostantiIntegrazione.ID_GRUPPO_INTEGRAZIONE,RichiesteApplicativeScorrette.ID_GRUPPO,RichiesteApplicativeScorrette.ID_GRUPPO+".422"},
			dependsOnMethods="testInvioMessaggioTramiteTunnelSOAP_nonImbustabileInSOAP")
	public void testInvioMessaggioTramiteIM_nonImbustabileInSOAP() throws Exception{
		super._testInvioMessaggioTramiteIM_nonImbustabileInSOAP();
	}

	
	
	
	/**
	 * Servizio SPCoop invocato con azione non corretta
	 * "423";
	 */
	@Test(groups={CostantiIntegrazione.ID_GRUPPO_INTEGRAZIONE,RichiesteApplicativeScorrette.ID_GRUPPO,RichiesteApplicativeScorrette.ID_GRUPPO+".423"})
	public void testInvocazioneServizioSenzaAzione() throws Exception{
		super._testInvocazioneServizioSenzaAzione();
	}

	
	
	
	
	/**
	 * Funzione "Allega Body" non riuscita sul messaggio di richiesta
	 * "424";
	 */
	@Test(groups={CostantiIntegrazione.ID_GRUPPO_INTEGRAZIONE,RichiesteApplicativeScorrette.ID_GRUPPO,RichiesteApplicativeScorrette.ID_GRUPPO+".424"})
	public void testAllegaBodyNonRiuscito() throws Exception{
		super._testAllegaBodyNonRiuscito();
	}

	
	
	
	/**
	 * Funzione "Scarta Body" non riuscita sul messaggio di richiesta
	 * "425";
	 */
	@Test(groups={CostantiIntegrazione.ID_GRUPPO_INTEGRAZIONE,RichiesteApplicativeScorrette.ID_GRUPPO,RichiesteApplicativeScorrette.ID_GRUPPO+".425"})
	public void testScartaBodyNonRiuscito() throws Exception{
		super._testScartaBodyNonRiuscito();
	}

	
	
	
	/**
	 * Errore di processamento SOAP del messaggio di richiesta 
	 * "426";
	 */
	@Test(groups={CostantiIntegrazione.ID_GRUPPO_INTEGRAZIONE,RichiesteApplicativeScorrette.ID_GRUPPO,RichiesteApplicativeScorrette.ID_GRUPPO+".426"})
	public void testSoapEngineFallito_errore_processamento() throws Exception{
		super._testSoapEngineFallito_errore_processamento();
	}

	
	
	
	/**
	 * Impossibile processare header SOAP in messaggio con opzione mustUnderstand
	 * "427";
	 */
	@Test(groups={CostantiIntegrazione.ID_GRUPPO_INTEGRAZIONE,RichiesteApplicativeScorrette.ID_GRUPPO,RichiesteApplicativeScorrette.ID_GRUPPO+".427"})
	public void testMustUnderstad() throws Exception{
		super._testMustUnderstad();
	}

	
	
	
	/**
	 * Autorizzazione basata sul contenuto fallita
	 * "428";
	 */
	@Test(groups={CostantiIntegrazione.ID_GRUPPO_INTEGRAZIONE,RichiesteApplicativeScorrette.ID_GRUPPO,RichiesteApplicativeScorrette.ID_GRUPPO+".428"})
	public void testAutorizzazioneContenutoKO() throws Exception{
		super._testAutorizzazioneContenutoKO();
	}

	
	
	
	/**
	 * L'header HTTP riporta un Content-Type non previsto in SOAP 1.1
	 * "429";
	 */
	@Test(groups={CostantiIntegrazione.ID_GRUPPO_INTEGRAZIONE,RichiesteApplicativeScorrette.ID_GRUPPO,RichiesteApplicativeScorrette.ID_GRUPPO+".429"})
	public void testContentTypeErrato() throws Exception{
		super._testContentTypeErrato();
	}

	
	
	
	/**
	 * Envelope con Namespace non previsto in SOAP 1.1
	 * "430";
	 */
	@Test(groups={CostantiIntegrazione.ID_GRUPPO_INTEGRAZIONE,RichiesteApplicativeScorrette.ID_GRUPPO,RichiesteApplicativeScorrette.ID_GRUPPO+".430"})
	public void testNamespaceEnvelopeErrato() throws Exception{
		super._testNamespaceEnvelopeErrato();
	}

	
	
	
	/**
	 * Test Errore Configurazione del GEstoreCredenziali
	 * "431";
	 **/
	@Test(groups={CostantiIntegrazione.ID_GRUPPO_INTEGRAZIONE,RichiesteApplicativeScorrette.ID_GRUPPO,RichiesteApplicativeScorrette.ID_GRUPPO+".431"})
	public void testLetturaCredenzialeERRORE_CONFIGURAZIONE_PD() throws TestSuiteException, Exception{
		super._testLetturaCredenzialeERRORE_CONFIGURAZIONE_PD();
	}

	
	
	
	/**
	 * Errore di processamento SOAP del messaggio di richiesta 
	 * "432";
	 */
	@Test(groups={CostantiIntegrazione.ID_GRUPPO_INTEGRAZIONE,RichiesteApplicativeScorrette.ID_GRUPPO,RichiesteApplicativeScorrette.ID_GRUPPO+".432"})
	public void testSoapEngineFallito_ricostruzioneMessaggioNonRiuscito() throws Exception{
		super._testSoapEngineFallito_ricostruzioneMessaggioNonRiuscito();
	}

	
	
	
	/**
	 * Errore di processamento content type non presente
	 * "433";
	 */
	// LA LIBRERIA CLIENT HTTP GENERICO NON PERMETTE UNA INVOCAZIONE SENZA CONTENT TYPE
//	@Test(groups={CostantiIntegrazione.ID_GRUPPO_INTEGRAZIONE,RichiesteApplicativeScorrette.ID_GRUPPO,RichiesteApplicativeScorrette.ID_GRUPPO+".433"})
//	public void testContentTypeNonPresente() throws Exception{
//		super._testContentTypeNonPresente();
//	}

	
	
	
	/**
	 * Errore di processamento correlazione applicativa risposta
	 * "434";
	 */
	@Test(groups={CostantiIntegrazione.ID_GRUPPO_INTEGRAZIONE,RichiesteApplicativeScorrette.ID_GRUPPO,RichiesteApplicativeScorrette.ID_GRUPPO+".434"})
	public void testCorrelazioneApplicativaRispostaErrata_wrap() throws Exception{
		super._testCorrelazioneApplicativaRispostaErrata(false);
	}
	
	@Test(groups={CostantiIntegrazione.ID_GRUPPO_INTEGRAZIONE,RichiesteApplicativeScorrette.ID_GRUPPO,RichiesteApplicativeScorrette.ID_GRUPPO+".434"})
	public void testCorrelazioneApplicativaRispostaErrata_unwrap() throws Exception{
		super._testCorrelazioneApplicativaRispostaErrata(true);
	}

	
	
	
	/***
	 * LocalForward configErrata
	 * "435";
	 */
	@Test(groups={CostantiIntegrazione.ID_GRUPPO_INTEGRAZIONE,RichiesteApplicativeScorrette.ID_GRUPPO,RichiesteApplicativeScorrette.ID_GRUPPO+".435"})
	public void localForward_invokePD_ASINCRONI_wrap() throws TestSuiteException, Exception{
		super._localForward_invokePD_ASINCRONI(false);
	}
	
	@Test(groups={CostantiIntegrazione.ID_GRUPPO_INTEGRAZIONE,RichiesteApplicativeScorrette.ID_GRUPPO,RichiesteApplicativeScorrette.ID_GRUPPO+".435"})
	public void localForward_invokePD_ASINCRONI_nowrap() throws TestSuiteException, Exception{
		super._localForward_invokePD_ASINCRONI(true);
	}
	
	
	
	/*
	
	436: Tipo del Soggetto Fruitore non supportato dal Protocollo

	437: Tipo del Soggetto Erogatore non supportato dal Protocollo

	438: Tipo di Servizio non supportato dal Protocollo

	439: Funzionalità non supportato dal Protocollo (es. profiloAsincrono sul protocollo trasparente
	
	NON VERIFICABILI
	
	*/
	
	
	/***
	 * PARSING_EXCEPTION_RISPOSTA
	 * "440";
	 */
	@Test(groups={CostantiIntegrazione.ID_GRUPPO_INTEGRAZIONE,RichiesteApplicativeScorrette.ID_GRUPPO,RichiesteApplicativeScorrette.ID_GRUPPO+".440"})
	public void strutturaXMLRispostaErrata_wrap()throws TestSuiteException,SOAPException, Exception{
		super._strutturaXMLRispostaErrata(false);
	}
	
	@Test(groups={CostantiIntegrazione.ID_GRUPPO_INTEGRAZIONE,RichiesteApplicativeScorrette.ID_GRUPPO,RichiesteApplicativeScorrette.ID_GRUPPO+".440"})
	public void strutturaXMLRispostaErrata_unwrap()throws TestSuiteException,SOAPException, Exception{
		super._strutturaXMLRispostaErrata(true);
	}
}
