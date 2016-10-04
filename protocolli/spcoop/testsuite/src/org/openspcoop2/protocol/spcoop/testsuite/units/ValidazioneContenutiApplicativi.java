/*
 * OpenSPCoop - Customizable API Gateway 
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



package org.openspcoop2.protocol.spcoop.testsuite.units;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
import java.util.Vector;

import javax.xml.rpc.Stub;
import javax.xml.soap.MimeHeader;
import javax.xml.soap.MimeHeaders;

import org.apache.axis.AxisFault;
import org.apache.axis.Message;
import org.apache.axis.client.Call;
import org.openspcoop2.ValidazioneContenutiWS.Service.AggiornamentoAsincronoWrappedDocumentLiteral;
import org.openspcoop2.ValidazioneContenutiWS.Service.AggiornamentoAsincronoWrappedDocumentLiteralServiceLocator;
import org.openspcoop2.ValidazioneContenutiWS.Service.AggiornamentoUtentiWrappedDocumentLiteral;
import org.openspcoop2.ValidazioneContenutiWS.Service.AggiornamentoUtentiWrappedDocumentLiteralServiceLocator;
import org.openspcoop2.ValidazioneContenutiWS.Service.EsitoAggiornamentoAsincronoWrappedDocumentLiteralServiceLocator;
import org.openspcoop2.ValidazioneContenutiWS.Service.GestioneUtentiDocumentLiteral;
import org.openspcoop2.ValidazioneContenutiWS.Service.GestioneUtentiDocumentLiteralServiceLocator;
import org.openspcoop2.ValidazioneContenutiWS.Service.GestioneUtentiOverloadedOperations;
import org.openspcoop2.ValidazioneContenutiWS.Service.GestioneUtentiOverloadedOperationsServiceLocator;
import org.openspcoop2.ValidazioneContenutiWS.Service.GestioneUtentiRPCEncoded;
import org.openspcoop2.ValidazioneContenutiWS.Service.GestioneUtentiRPCEncodedServiceLocator;
import org.openspcoop2.ValidazioneContenutiWS.Service.GestioneUtentiRPCLiteral;
import org.openspcoop2.ValidazioneContenutiWS.Service.GestioneUtentiRPCLiteralServiceLocator;
import org.openspcoop2.ValidazioneContenutiWS.Service.GestioneUtentiStileIbrido;
import org.openspcoop2.ValidazioneContenutiWS.Service.GestioneUtentiStileIbridoServiceLocator;
import org.openspcoop2.ValidazioneContenutiWS.Service.GestioneUtentiWrappedDocumentLiteral;
import org.openspcoop2.ValidazioneContenutiWS.Service.GestioneUtentiWrappedDocumentLiteralServiceLocator;
import org.openspcoop2.ValidazioneContenutiWS.Service.types.AggiornamentoNominativo;
import org.openspcoop2.ValidazioneContenutiWS.Service.types.AggiornamentoUtenteWDLRequest;
import org.openspcoop2.ValidazioneContenutiWS.Service.types.AggiornamentoUtenteWDLResponse;
import org.openspcoop2.ValidazioneContenutiWS.Service.types.NotificaAggiornamentoUtenteWDLRequest;
import org.openspcoop2.ValidazioneContenutiWS.Service.types.RichiestaAggiornamentoUtenteAsincronoAsimmetricoWDLRequest;
import org.openspcoop2.ValidazioneContenutiWS.Service.types.RichiestaAggiornamentoUtenteAsincronoAsimmetricoWDLResponse;
import org.openspcoop2.ValidazioneContenutiWS.Service.types.RichiestaAggiornamentoUtenteAsincronoSimmetricoWDLRequest;
import org.openspcoop2.ValidazioneContenutiWS.Service.types.RichiestaAggiornamentoUtenteAsincronoSimmetricoWDLResponse;
import org.openspcoop2.ValidazioneContenutiWS.Service.types.esempio1.EliminazioneUtenteWDLRequestType;
import org.openspcoop2.ValidazioneContenutiWS.Service.types.esempio1.EliminazioneUtenteWDLResponseType;
import org.openspcoop2.ValidazioneContenutiWS.Service.types.esempio1.RegistrazioneUtenteWDLRequestType;
import org.openspcoop2.ValidazioneContenutiWS.Service.types.esempio1.RegistrazioneUtenteWDLResponseType;
import org.openspcoop2.testsuite.clients.ClientHttpGenerico;
import org.openspcoop2.testsuite.core.TestSuiteException;
import org.openspcoop2.testsuite.core.Repository;
import org.openspcoop2.testsuite.core.TestSuiteProperties;
import org.openspcoop2.testsuite.db.DatabaseComponent;
import org.openspcoop2.testsuite.db.DatiServizio;
import org.openspcoop2.testsuite.units.CooperazioneBase;
import org.openspcoop2.testsuite.units.CooperazioneBaseInformazioni;
import org.openspcoop2.message.SOAPVersion;
import org.openspcoop2.pdd.core.CostantiPdD;
import org.openspcoop2.protocol.sdk.constants.CodiceErroreIntegrazione;
import org.openspcoop2.protocol.sdk.constants.Inoltro;
import org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione;
import org.openspcoop2.protocol.spcoop.constants.SPCoopCostanti;
import org.openspcoop2.protocol.spcoop.testsuite.core.CooperazioneSPCoopBase;
import org.openspcoop2.protocol.spcoop.testsuite.core.CostantiTestSuite;
import org.openspcoop2.protocol.spcoop.testsuite.core.DatabaseProperties;
import org.openspcoop2.protocol.spcoop.testsuite.core.SPCoopTestsuiteLogger;
import org.openspcoop2.testsuite.core.ErroreAttesoOpenSPCoopLogCore;
import org.openspcoop2.protocol.spcoop.testsuite.core.TestSuiteTransformer;
import org.openspcoop2.protocol.spcoop.testsuite.core.Utilities;
import org.openspcoop2.utils.date.DateManager;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.AfterGroups;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Test sulla validazione dei contenuti applicativi
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ValidazioneContenutiApplicativi {

	/** Identificativo del gruppo */
	public static final String ID_GRUPPO = "ValidazioneContenutiApplicativi";
	
	/** Gestore della Collaborazione di Base */
	private CooperazioneBaseInformazioni info = CooperazioneSPCoopBase.getCooperazioneBaseInformazioni(CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE,
			CostantiTestSuite.SPCOOP_SOGGETTO_EROGATORE,
			false,SPCoopCostanti.PROFILO_TRASMISSIONE_CON_DUPLICATI,Inoltro.CON_DUPLICATI);	
	private CooperazioneBase collaborazioneSPCoopBase = 
		new CooperazioneBase(false,SOAPVersion.SOAP11,  this.info, 
				org.openspcoop2.protocol.spcoop.testsuite.core.TestSuiteProperties.getInstance(), 
				DatabaseProperties.getInstance(), SPCoopTestsuiteLogger.getInstance());



	
	
	private Date dataAvvioGruppoTest = null;
	@BeforeGroups (alwaysRun=true , groups=ID_GRUPPO)
	public void testOpenspcoopCoreLog_raccoltaTempoAvvioTest() throws Exception{
		this.dataAvvioGruppoTest = DateManager.getDate();
	} 	
	private Vector<ErroreAttesoOpenSPCoopLogCore> erroriAttesiOpenSPCoopCore = new Vector<ErroreAttesoOpenSPCoopLogCore>();
	@AfterGroups (alwaysRun=true , groups=ID_GRUPPO)
	public void testOpenspcoopCoreLog() throws Exception{
		if(this.erroriAttesiOpenSPCoopCore.size()>0){
			org.openspcoop2.protocol.spcoop.testsuite.core.FileSystemUtilities.verificaOpenspcoopCore(this.dataAvvioGruppoTest,
					this.erroriAttesiOpenSPCoopCore.toArray(new ErroreAttesoOpenSPCoopLogCore[1]));
		}else{
			org.openspcoop2.protocol.spcoop.testsuite.core.FileSystemUtilities.verificaOpenspcoopCore(this.dataAvvioGruppoTest);
		}
	} 
	
	


		
	private String getIDEgov(Call call) throws Exception{
		MimeHeaders mh = call.getResponseMessage().getMimeHeaders();
		Iterator<?> it = mh.getAllHeaders();
		while(it.hasNext()){
			MimeHeader m = (MimeHeader) it.next();
			TestSuiteProperties testSuiteProperties = TestSuiteProperties.getInstance(); 
			if(testSuiteProperties.getIdMessaggioTrasporto().equalsIgnoreCase(m.getName()))
				return m.getValue();
		}
		return null;
	}

	private void invocaServizioContenutoApplicativo(String fileTest,Repository repository,String portaDelegata,
			String soapAction) throws Exception{
		invocaServizioContenutoApplicativoEngine(fileTest,repository,portaDelegata,null,null,null,soapAction,null,true,false);
	}
	private void invocaServizioContenutoApplicativoRispostaAsincronaSimmetrica(String fileTest,Repository repository,String portaDelegata,
			String soapAction,String riferimentoMessaggio) throws Exception{
		invocaServizioContenutoApplicativoEngine(fileTest,repository,portaDelegata,null,null,null,soapAction,riferimentoMessaggio,true,true);
	}
	private void invocaServizioContenutoApplicativoRispostaAsincronaAsimmetrica(String fileTest,Repository repository,String portaDelegata,
			String soapAction,String riferimentoMessaggio) throws Exception{
		invocaServizioContenutoApplicativoEngine(fileTest,repository,portaDelegata,null,null,null,soapAction,riferimentoMessaggio,true,false);
	}
	private void invocaServizioContenutoApplicativoErrato(String fileTest,Repository repository,String portaDelegata,
			String actorClientAtteso,String faultCodeAtteso,String faultString,String soapAction) throws Exception{
		invocaServizioContenutoApplicativoEngine(fileTest,repository,portaDelegata,actorClientAtteso,faultCodeAtteso,faultString,
				soapAction,null,false,false);
	}
	private void invocaServizioContenutoApplicativoEngine(String fileTest,Repository repository,String portaDelegata,
			String actorClientAtteso,String faultCodeAtteso,String faultString,String soapAction,String riferimentoMessaggio,
			boolean invocazioneOK,boolean rispostaAsincronaSimmetrica) throws Exception{
		java.io.FileInputStream fin = null;
		DatabaseComponent dbComponentFruitore = null;
		try{
			fin = new java.io.FileInputStream(new File(Utilities.testSuiteProperties.getPathTestValidazioneContenutiApplicativi()+
					File.separator+fileTest));

			Message msg=new Message(fin);
			msg.getSOAPPartAsBytes();
			
			ClientHttpGenerico client=new ClientHttpGenerico(repository);
			String soapActionTmp = soapAction;
			if(soapActionTmp.startsWith("\"")==false){
				soapActionTmp = "\""+soapActionTmp;
			}
			if(soapActionTmp.endsWith("\"")==false){
				soapActionTmp = soapActionTmp+"\"";
			}
			client.setSoapAction(soapActionTmp);
			client.setRiferimentoMessaggio(riferimentoMessaggio);
			if(rispostaAsincronaSimmetrica)
				client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiErogatore());
			else
				client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(portaDelegata);
			client.connectToSoapEngine();
			client.setMessage(msg);
			client.setRispostaDaGestire(true);
			// AttesaTerminazioneMessaggi
			if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()){
				dbComponentFruitore= DatabaseProperties.getDatabaseComponentFruitore();

				client.setAttesaTerminazioneMessaggi(true);
				client.setDbAttesaTerminazioneMessaggiFruitore(dbComponentFruitore);
			}
			try {
				client.run();
				if(invocazioneOK==false){
					Reporter.log("Invocazione PD non ha causato errori.");
					throw new TestSuiteException("Invocazione PD non ha causato errori.");
				}
			} catch (AxisFault error) {
				if(invocazioneOK){
					throw error;
				}else{
					Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
					Reporter.log("Controllo fault actor ["+actorClientAtteso+"]");
					if(actorClientAtteso!=null)
						Assert.assertTrue(actorClientAtteso.equals(error.getFaultActor()));
					else
						Assert.assertTrue(error.getFaultActor()==null);
					Reporter.log("Controllo fault code ["+faultCodeAtteso+"]");
					if(faultCodeAtteso!=null)
						Assert.assertTrue(faultCodeAtteso.equals(error.getFaultCode().getLocalPart().trim()));
					else
						Assert.assertTrue(error.getFaultCode()==null);
					Reporter.log("Controllo fault string ["+faultString+"] con ["+error.getFaultString()+"]");
					Assert.assertTrue(error.getFaultString().indexOf(faultString)>=0);
				}
			}finally{
				dbComponentFruitore.close();
			}
		}catch(Exception e){
			throw e;
		}finally{
			try{
				fin.close();
			}catch(Exception e){}
			try{
				dbComponentFruitore.close();
			}catch(Exception e){}
		}
	}
	
	
	
	
	
	



	/***
	 * Test 1 Invocazione servizio GestioneUtentiWrappedDocumentLiteral
	 * Scopo del test: corretta validazione del contenuto applicativo 
	 *                 realizzato in stile wrapped document literal
	 *                 
	 *  Invocazioni realizzate:
	 *  - Test contenuto corretto per Validazione WSDL (operazione registrazione)
	 *  - Test contenuto corretto per Validazione OpenSPCoop (operazione registrazione)
	 *  - Test contenuto corretto per Validazione WSDL (operazione eliminazione)
	 *  - Test contenuto corretto per Validazione OpenSPCoop (operazione eliminazione)
	 *  - Test  identificazione contenuto applicativo non corretto secondo il wsdl definitorio (xsd) per Validazione WSDL
	 *  - Test  identificazione contenuto applicativo non corretto secondo il wsdl definitorio (xsd) per Validazione OpenSPCoop
	 *  - Test contenuto corretto ma invocazione della porta delegata, con azione 'eliminazione' 
	 *              mentre contenuto applicativo dell'operation 'registrazione'. 
	 *              Viene riconosciuto che il contenuto e' di un'altra operation del port type  
	 *  - Test contenuto corretto ma invocazione della porta delegata, con azione 'eliminazione'  per Validazione WSDL
	 *              mentre contenuto applicativo dell'operation 'registrazione'. 
	 *              Viene riconosciuto che il contenuto e' di un'altra operation del port type  per Validazione OpenSPCoop
	 *  - Test contenuto corretto ma invocazione con una SOAPAction non conforme a quanto indicato nel wsdl per Validazione WSDL
	 *  - Test contenuto corretto ma invocazione con una SOAPAction non conforme a quanto indicato nel wsdl per Validazione OpenSPCoop           
	 */
	
	@SuppressWarnings({ "deprecation" })
	private String getIDEgov(GestioneUtentiWrappedDocumentLiteralServiceLocator locator)throws Exception{
		return this.getIDEgov(locator.getCall());
	}
	
	private void invocaServizioGestioneUtentiWrappedDocumentLiteral(String portaDelegata,String servizio,String azione,String test,boolean registra) throws Exception{
		Reporter.log("["+test+"] Invocazione GestioneUtenti WrappedDocumentLiteral  ["+Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore()+
				portaDelegata+"/"+
				servizio+"/"+
				azione+"]");
		
		GestioneUtentiWrappedDocumentLiteralServiceLocator locator = new GestioneUtentiWrappedDocumentLiteralServiceLocator();
		locator.setGestioneUtentiWrappedDocumentLiteralEndpointAddress(
				Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore()+
				portaDelegata+"/"+
				servizio+"/"+
				azione);
		GestioneUtentiWrappedDocumentLiteral port = locator.getGestioneUtentiWrappedDocumentLiteral();
		
		try{
			if(registra){
				RegistrazioneUtenteWDLRequestType wrappedRequest = new RegistrazioneUtenteWDLRequestType();
				wrappedRequest.setNominativo("Mario Rossi");
				wrappedRequest.setIndirizzo("viale Roma 26");
				wrappedRequest.setOraRegistrazione(new java.util.Date());
					
				RegistrazioneUtenteWDLResponseType esito = port.registrazioneUtenteWDL(wrappedRequest);
				Reporter.log("["+test+"] Invocazione GestioneUtenti WrappedDocumentLiteral (azione registrazioneUtenteWDL) esito invocazione: "+esito.getEsito());
				Assert.assertTrue("OK".equals(esito.getEsito()));
			}else{
				EliminazioneUtenteWDLRequestType wrappedRequest = new EliminazioneUtenteWDLRequestType();
				wrappedRequest.setNominativo("Mario Rossi");
				
				EliminazioneUtenteWDLResponseType esito = port.eliminazioneUtenteWDL(wrappedRequest);
				Reporter.log("["+test+"] Invocazione GestioneUtenti WrappedDocumentLiteral (azione eliminazioneUtenteWDL) esito invocazione: "+esito.getEsito());
				Assert.assertTrue("OK".equals(esito.getEsito()));
			}
		}catch(Exception e){
			throw e;
		}finally{
			String idEGov = this.getIDEgov(locator);
			Reporter.log("["+test+"] Invocazione GestioneUtenti WrappedDocumentLiteral idEgov: "+idEGov);
			Assert.assertTrue(idEGov!=null);
			this.repositoryGestioneUtentiWrappedDocumentLiteral.add(idEGov);
		}
	}
	
	
	Repository repositoryGestioneUtentiWrappedDocumentLiteral=new Repository();
	@Test(groups={ValidazioneContenutiApplicativi.ID_GRUPPO,
			ValidazioneContenutiApplicativi.ID_GRUPPO+".GestioneUtentiWrappedDocumentLiteral"},description="Test di tipo sincrono, Viene controllato se i body sono uguali e se gli attachment sono uguali")
	public void gestioneUtentiWrappedDocumentLiteral() throws TestSuiteException, IOException, Exception{
		
		TestSuiteTransformer.sequentialForced = true;
		
		// Test contenuto corretto per Validazione WSDL (operazione registrazione)
		invocaServizioGestioneUtentiWrappedDocumentLiteral(CostantiTestSuite.PORTA_DELEGATA_VALIDAZIONE_WSDL_GENERICA,
				CostantiTestSuite.SPCOOP_NOME_SERVIZIO_VAL_WSDL_GESTIONE_UTENTI_WDL,
				CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_VALIDAZIONE_WSDL_OPENSPCOOP_AZIONE_REGISTRAZIONE_UTENTE_WDL,
				"ValidazioneWSDL_OperationRegistrazione",true);
		
		// Test contenuto corretto per Validazione OpenSPCoop (operazione registrazione)
		invocaServizioGestioneUtentiWrappedDocumentLiteral(CostantiTestSuite.PORTA_DELEGATA_VALIDAZIONE_OPENSPCOOP_GENERICA,
				CostantiTestSuite.SPCOOP_NOME_SERVIZIO_VAL_OPENSPCOOP_GESTIONE_UTENTI_WDL,
				CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_VALIDAZIONE_WSDL_OPENSPCOOP_AZIONE_REGISTRAZIONE_UTENTE_WDL,
				"ValidazioneOpenSPCoop_OperationRegistrazione",true);
		
		// Test contenuto corretto per Validazione WSDL (operazione eliminazione)
		invocaServizioGestioneUtentiWrappedDocumentLiteral(CostantiTestSuite.PORTA_DELEGATA_VALIDAZIONE_WSDL_GENERICA,
				CostantiTestSuite.SPCOOP_NOME_SERVIZIO_VAL_WSDL_GESTIONE_UTENTI_WDL,
				CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_VALIDAZIONE_WSDL_OPENSPCOOP_AZIONE_ELIMINAZIONE_UTENTE_WDL,
				"ValidazioneWSDL_OperationEliminazione",false);
		
		// Test contenuto corretto per Validazione OpenSPCoop (operazione eliminazione)
		invocaServizioGestioneUtentiWrappedDocumentLiteral(CostantiTestSuite.PORTA_DELEGATA_VALIDAZIONE_OPENSPCOOP_GENERICA,
				CostantiTestSuite.SPCOOP_NOME_SERVIZIO_VAL_OPENSPCOOP_GESTIONE_UTENTI_WDL,
				CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_VALIDAZIONE_WSDL_OPENSPCOOP_AZIONE_ELIMINAZIONE_UTENTE_WDL,
				"ValidazioneOpenSPCoop_OperationEliminazione",false);
		
		// Test  identificazione contenuto applicativo non corretto secondo il wsdl definitorio (xsd) per Validazione WSDL
		Date dataInizioTestA = DateManager.getDate();
		invocaServizioContenutoApplicativoErrato("operazioneRegistrazioneUtenteWDLNonCorrettamenteFormato.xml",
				this.repositoryGestioneUtentiWrappedDocumentLiteral,
				CostantiTestSuite.PORTA_DELEGATA_VALIDAZIONE_WSDL_GENERICA+"/"+
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_VAL_WSDL_GESTIONE_UTENTI_WDL+"/"+
					CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_VALIDAZIONE_WSDL_OPENSPCOOP_AZIONE_REGISTRAZIONE_UTENTE_WDL,
				CostantiPdD.OPENSPCOOP2,Utilities.toString(CodiceErroreIntegrazione.CODICE_418_VALIDAZIONE_WSDL_RICHIESTA_FALLITA),
				"Il contenuto applicativo del messaggio di richiesta non rispetta l'accordo di servizio (Wsdl erogatore) definito nel Registro dei Servizi",
				"registrazioneUtenteWDL");
		
		Date dataFineTestA = DateManager.getDate();
		ErroreAttesoOpenSPCoopLogCore errA = new ErroreAttesoOpenSPCoopLogCore();
		errA.setIntervalloInferiore(dataInizioTestA);
		errA.setIntervalloSuperiore(dataFineTestA);
		errA.setMsgErrore("Validazione WSDL (true) fallita: Messaggio con elementi non conformi alla definizione wsdl dell'Operation [registrazioneUtenteWDL] del port-type [GestioneUtentiWrappedDocumentLiteral] (AccordoServizio:ASMultiPortTypeValidazioneWSDL style:document use:literal)");
		this.erroriAttesiOpenSPCoopCore.add(errA);
		
		ErroreAttesoOpenSPCoopLogCore errA2 = new ErroreAttesoOpenSPCoopLogCore();
		errA2.setIntervalloInferiore(dataInizioTestA);
		errA2.setIntervalloSuperiore(dataFineTestA);
		errA2.setMsgErrore("Riscontrata non conformità rispetto all'interfaccia WSDL; Messaggio con elementi non conformi alla definizione wsdl dell'Operation [registrazioneUtenteWDL] del port-type [GestioneUtentiWrappedDocumentLiteral] (AccordoServizio:ASMultiPortTypeValidazioneWSDL style:document use:literal)");
		this.erroriAttesiOpenSPCoopCore.add(errA2);
				
		// Test  identificazione contenuto applicativo non corretto secondo il wsdl definitorio (xsd) per Validazione OpenSPCoop
		Date dataInizioTestB = DateManager.getDate();
		invocaServizioContenutoApplicativoErrato("operazioneRegistrazioneUtenteWDLNonCorrettamenteFormato.xml",
				this.repositoryGestioneUtentiWrappedDocumentLiteral,
				CostantiTestSuite.PORTA_DELEGATA_VALIDAZIONE_OPENSPCOOP_GENERICA+"/"+
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_VAL_OPENSPCOOP_GESTIONE_UTENTI_WDL+"/"+
					CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_VALIDAZIONE_WSDL_OPENSPCOOP_AZIONE_REGISTRAZIONE_UTENTE_WDL,
				CostantiPdD.OPENSPCOOP2,Utilities.toString(CodiceErroreIntegrazione.CODICE_418_VALIDAZIONE_WSDL_RICHIESTA_FALLITA),
				"Il contenuto applicativo del messaggio di richiesta non rispetta l'accordo di servizio (Wsdl erogatore) definito nel Registro dei Servizi",
				"registrazioneUtenteWDL");
		Date dataFineTestB = DateManager.getDate();
		ErroreAttesoOpenSPCoopLogCore errB = new ErroreAttesoOpenSPCoopLogCore();
		errB.setIntervalloInferiore(dataInizioTestB);
		errB.setIntervalloSuperiore(dataFineTestB);
		errB.setMsgErrore("Validazione WSDL (true) fallita: Messaggio con elementi non conformi alla definizione wsdl dell'Azione [registrazioneUtenteWDL] del Servizio [GestioneUtentiWrappedDocumentLiteral] (AccordoServizio:ASMultiPortTypeValidazioneOpenSPCoop style:document use:literal)");
		this.erroriAttesiOpenSPCoopCore.add(errB);
		
		ErroreAttesoOpenSPCoopLogCore errB2 = new ErroreAttesoOpenSPCoopLogCore();
		errB2.setIntervalloInferiore(dataInizioTestB);
		errB2.setIntervalloSuperiore(dataFineTestB);
		errB2.setMsgErrore("Riscontrata non conformità rispetto all'interfaccia WSDL; Messaggio con elementi non conformi alla definizione wsdl dell'Azione [registrazioneUtenteWDL] del Servizio [GestioneUtentiWrappedDocumentLiteral] (AccordoServizio:ASMultiPortTypeValidazioneOpenSPCoop style:document use:literal)");
		this.erroriAttesiOpenSPCoopCore.add(errB2);
		
		// Test contenuto corretto ma invocazione della porta delegata, con azione 'eliminazione' 
		//              mentre contenuto applicativo dell'operation 'registrazione'. 
		//              Viene riconosciuto che il contenuto e' di un'altra operation del port type
		Date dataInizioTest1 = DateManager.getDate();
		try{
			invocaServizioGestioneUtentiWrappedDocumentLiteral(CostantiTestSuite.PORTA_DELEGATA_VALIDAZIONE_WSDL_GENERICA,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_VAL_WSDL_GESTIONE_UTENTI_WDL,
					CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_VALIDAZIONE_WSDL_OPENSPCOOP_AZIONE_REGISTRAZIONE_UTENTE_WDL,
					"ValidazioneWSDL_OperationRegistrazione",false);	
			throw new Exception("Invocazione azione con xml di una operation relativa ad un altra azione non ha dato errore");
		} catch (AxisFault error) {
			Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
			Reporter.log("Controllo fault actor [OpenSPCoop]");
			Assert.assertTrue(CostantiPdD.OPENSPCOOP2.equals(error.getFaultActor()));
			Reporter.log("Controllo fault code [OPENSPCOOP_ORG_418]");
			Assert.assertTrue(Utilities.toString(CodiceErroreIntegrazione.CODICE_418_VALIDAZIONE_WSDL_RICHIESTA_FALLITA).equals(error.getFaultCode().getLocalPart().trim()));
			Reporter.log("Controllo fault string [Il contenuto applicativo del messaggio di richiesta non rispetta l'accordo di servizio (Wsdl erogatore) definito nel Registro dei Servizi] con ["+error.getFaultString()+"]");
			Assert.assertTrue(error.getFaultString().indexOf("Il contenuto applicativo del messaggio di richiesta non rispetta l'accordo di servizio (Wsdl erogatore) definito nel Registro dei Servizi")>=0);
		}
		Date dataFineTest1 = DateManager.getDate();
		ErroreAttesoOpenSPCoopLogCore err = new ErroreAttesoOpenSPCoopLogCore();
		err.setIntervalloInferiore(dataInizioTest1);
		err.setIntervalloSuperiore(dataFineTest1);
		err.setMsgErrore("Validazione WSDL (true) fallita: Messaggio con elementi non conformi alla definizione wsdl dell'Operation [registrazioneUtenteWDL] del port-type [GestioneUtentiWrappedDocumentLiteral] (AccordoServizio:ASMultiPortTypeValidazioneWSDL style:document use:literal)");
		this.erroriAttesiOpenSPCoopCore.add(err);
		
		ErroreAttesoOpenSPCoopLogCore err2 = new ErroreAttesoOpenSPCoopLogCore();
		err2.setIntervalloInferiore(dataInizioTest1);
		err2.setIntervalloSuperiore(dataFineTest1);
		err2.setMsgErrore("Riscontrata non conformità rispetto all'interfaccia WSDL; Messaggio con elementi non conformi alla definizione wsdl dell'Operation [registrazioneUtenteWDL] del port-type [GestioneUtentiWrappedDocumentLiteral] (AccordoServizio:ASMultiPortTypeValidazioneWSDL style:document use:literal):");
		this.erroriAttesiOpenSPCoopCore.add(err2);
		
		// Test contenuto corretto ma invocazione della porta delegata, con azione 'eliminazione' 
		//              mentre contenuto applicativo dell'operation 'registrazione'. 
		//              Viene riconosciuto che il contenuto e' di un'altra operation del port type
		Date dataInizioTest2 = DateManager.getDate();
		try{
			invocaServizioGestioneUtentiWrappedDocumentLiteral(CostantiTestSuite.PORTA_DELEGATA_VALIDAZIONE_OPENSPCOOP_GENERICA,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_VAL_OPENSPCOOP_GESTIONE_UTENTI_WDL,
					CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_VALIDAZIONE_WSDL_OPENSPCOOP_AZIONE_REGISTRAZIONE_UTENTE_WDL,
					"ValidazioneWSDL_OperationRegistrazione",false);
			throw new Exception("Invocazione azione con xml di una operation relativa ad un altra azione non ha dato errore");
		} catch (AxisFault error) {
			Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
			Reporter.log("Controllo fault actor [OpenSPCoop]");
			Assert.assertTrue(CostantiPdD.OPENSPCOOP2.equals(error.getFaultActor()));
			Reporter.log("Controllo fault code [OPENSPCOOP_ORG_418]");
			Assert.assertTrue(Utilities.toString(CodiceErroreIntegrazione.CODICE_418_VALIDAZIONE_WSDL_RICHIESTA_FALLITA).equals(error.getFaultCode().getLocalPart().trim()));
			Reporter.log("Controllo fault string [Il contenuto applicativo del messaggio di richiesta non rispetta l'accordo di servizio (Wsdl erogatore) definito nel Registro dei Servizi] con ["+error.getFaultString()+"]");
			Assert.assertTrue(error.getFaultString().indexOf("Il contenuto applicativo del messaggio di richiesta non rispetta l'accordo di servizio (Wsdl erogatore) definito nel Registro dei Servizi")>=0);
		}
		Date dataFineTest2 = DateManager.getDate();
		ErroreAttesoOpenSPCoopLogCore errW = new ErroreAttesoOpenSPCoopLogCore();
		errW.setIntervalloInferiore(dataInizioTest2);
		errW.setIntervalloSuperiore(dataFineTest2);
		errW.setMsgErrore("Validazione WSDL (true) fallita: Messaggio con elementi non conformi alla definizione wsdl dell'Azione [registrazioneUtenteWDL] del Servizio [GestioneUtentiWrappedDocumentLiteral] (AccordoServizio:ASMultiPortTypeValidazioneOpenSPCoop style:document use:literal)");
		this.erroriAttesiOpenSPCoopCore.add(errW);
		
		ErroreAttesoOpenSPCoopLogCore errW2 = new ErroreAttesoOpenSPCoopLogCore();
		errW2.setIntervalloInferiore(dataInizioTest2);
		errW2.setIntervalloSuperiore(dataFineTest2);
		errW2.setMsgErrore("Riscontrata non conformità rispetto all'interfaccia WSDL; Messaggio con elementi non conformi alla definizione wsdl dell'Azione [registrazioneUtenteWDL] del Servizio [GestioneUtentiWrappedDocumentLiteral] (AccordoServizio:ASMultiPortTypeValidazioneOpenSPCoop style:document use:literal):");
		this.erroriAttesiOpenSPCoopCore.add(errW2);
		
		// Test contenuto corretto ma invocazione con una SOAPAction non conforme a quanto indicato nel wsdl per Validazione WSDL
		Date dataInizioTest3 = DateManager.getDate();
		invocaServizioContenutoApplicativoErrato("operazioneRegistrazioneUtenteWDL.xml",
				this.repositoryGestioneUtentiWrappedDocumentLiteral,
				CostantiTestSuite.PORTA_DELEGATA_VALIDAZIONE_WSDL_GENERICA+"/"+
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_VAL_WSDL_GESTIONE_UTENTI_WDL+"/"+
					CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_VALIDAZIONE_WSDL_OPENSPCOOP_AZIONE_REGISTRAZIONE_UTENTE_WDL,
				CostantiPdD.OPENSPCOOP2,Utilities.toString(CodiceErroreIntegrazione.CODICE_418_VALIDAZIONE_WSDL_RICHIESTA_FALLITA),
				"Il contenuto applicativo del messaggio di richiesta non rispetta l'accordo di servizio (Wsdl erogatore) definito nel Registro dei Servizi",
				"SOAP_ACTION_ERRATA");
		Date dataFineTest3 = DateManager.getDate();
		ErroreAttesoOpenSPCoopLogCore err3 = new ErroreAttesoOpenSPCoopLogCore();
		err3.setIntervalloInferiore(dataInizioTest3);
		err3.setIntervalloSuperiore(dataFineTest3);
		err3.setMsgErrore("Validazione WSDL (true) fallita: Operazione [registrazioneUtenteWDL] del port-type [GestioneUtentiWrappedDocumentLiteral] con soap action [SOAP_ACTION_ERRATA] che non rispetta quella indicata nel wsdl: registrazioneUtenteWDL");
		this.erroriAttesiOpenSPCoopCore.add(err3);
		
		ErroreAttesoOpenSPCoopLogCore err3a = new ErroreAttesoOpenSPCoopLogCore();
		err3a.setIntervalloInferiore(dataInizioTest3);
		err3a.setIntervalloSuperiore(dataFineTest3);
		err3a.setMsgErrore("Riscontrata non conformità rispetto all'interfaccia WSDL; Operazione [registrazioneUtenteWDL] del port-type [GestioneUtentiWrappedDocumentLiteral] con soap action [SOAP_ACTION_ERRATA] che non rispetta quella indicata nel wsdl: registrazioneUtenteWDL");
		this.erroriAttesiOpenSPCoopCore.add(err3a);
		
		// Test contenuto corretto ma invocazione con una SOAPAction non conforme a quanto indicato nel wsdl per Validazione OpenSPCoop
		Date dataInizioTest4 = DateManager.getDate();
		invocaServizioContenutoApplicativoErrato("operazioneRegistrazioneUtenteWDL.xml",
				this.repositoryGestioneUtentiWrappedDocumentLiteral,
				CostantiTestSuite.PORTA_DELEGATA_VALIDAZIONE_OPENSPCOOP_GENERICA+"/"+
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_VAL_OPENSPCOOP_GESTIONE_UTENTI_WDL+"/"+
					CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_VALIDAZIONE_WSDL_OPENSPCOOP_AZIONE_REGISTRAZIONE_UTENTE_WDL,
				CostantiPdD.OPENSPCOOP2,Utilities.toString(CodiceErroreIntegrazione.CODICE_418_VALIDAZIONE_WSDL_RICHIESTA_FALLITA),
				"Il contenuto applicativo del messaggio di richiesta non rispetta l'accordo di servizio (Wsdl erogatore) definito nel Registro dei Servizi",
				"SOAP_ACTION_ERRATA");
		Date dataFineTest4 = DateManager.getDate();
		ErroreAttesoOpenSPCoopLogCore err4 = new ErroreAttesoOpenSPCoopLogCore();
		err4.setIntervalloInferiore(dataInizioTest4);
		err4.setIntervalloSuperiore(dataFineTest4);
		err4.setMsgErrore("Validazione WSDL (true) fallita: Operazione [registrazioneUtenteWDL] del port-type [GestioneUtentiWrappedDocumentLiteral] con soap action [SOAP_ACTION_ERRATA] che non rispetta quella indicata nel wsdl: registrazioneUtenteWDL");
		this.erroriAttesiOpenSPCoopCore.add(err4);
		
		ErroreAttesoOpenSPCoopLogCore err4a = new ErroreAttesoOpenSPCoopLogCore();
		err4a.setIntervalloInferiore(dataInizioTest4);
		err4a.setIntervalloSuperiore(dataFineTest4);
		err4a.setMsgErrore("Riscontrata non conformità rispetto all'interfaccia WSDL; Operazione [registrazioneUtenteWDL] del port-type [GestioneUtentiWrappedDocumentLiteral] con soap action [SOAP_ACTION_ERRATA] che non rispetta quella indicata nel wsdl: registrazioneUtenteWDL");
		this.erroriAttesiOpenSPCoopCore.add(err4a);
	}
	@DataProvider (name="gestioneUtentiWrappedDocumentLiteral")
	public Object[][]testGestioneUtentiWrappedDocumentLiteral()throws Exception{
		String [] ids = new String[1];
		Vector<String> idsV = new Vector<String>();
		String id = null;
		while( (id=this.repositoryGestioneUtentiWrappedDocumentLiteral.getNext()) != null){
			idsV.add(id);
		}
		ids = idsV.toArray(ids);
		
		try{
			Thread.sleep(3000);
		}catch(Exception e){}
		
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),ids,false},	
				{DatabaseProperties.getDatabaseComponentErogatore(),ids,true}	
		};
	}
	@Test(groups={ValidazioneContenutiApplicativi.ID_GRUPPO,
			ValidazioneContenutiApplicativi.ID_GRUPPO+".GestioneUtentiWrappedDocumentLiteral"},
			dataProvider="gestioneUtentiWrappedDocumentLiteral",
			dependsOnMethods={"gestioneUtentiWrappedDocumentLiteral"})
	public void testGestioneUtentiWrappedDocumentLiteral(DatabaseComponent data,String [] ids,boolean checkServizioApplicativo) throws Exception{
		try{
			for(int i=0; i<ids.length; i++){
				
				String tipoServizio = CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_VALIDAZIONE_WSDL_OPENSPCOOP;
				String servizio = null;
				String azione = null;
				boolean eccezione = false;
				boolean isTraced = true;
				
				if(i==0){
					// Test contenuto corretto per Validazione WSDL (operazione registrazione)
					servizio = CostantiTestSuite.SPCOOP_NOME_SERVIZIO_VAL_WSDL_GESTIONE_UTENTI_WDL;
					azione = CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_VALIDAZIONE_WSDL_OPENSPCOOP_AZIONE_REGISTRAZIONE_UTENTE_WDL;
				}
				else if(i==1){
					// Test contenuto corretto per Validazione OpenSPCoop (operazione registrazione)
					servizio = CostantiTestSuite.SPCOOP_NOME_SERVIZIO_VAL_OPENSPCOOP_GESTIONE_UTENTI_WDL;
					azione = CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_VALIDAZIONE_WSDL_OPENSPCOOP_AZIONE_REGISTRAZIONE_UTENTE_WDL;
				}
				else if(i==2){
					// Test contenuto corretto per Validazione WSDL (operazione eliminazione)
					servizio = CostantiTestSuite.SPCOOP_NOME_SERVIZIO_VAL_WSDL_GESTIONE_UTENTI_WDL;
					azione = CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_VALIDAZIONE_WSDL_OPENSPCOOP_AZIONE_ELIMINAZIONE_UTENTE_WDL;
				}
				else if(i==3){
					// Test contenuto corretto per Validazione OpenSPCoop (operazione eliminazione)
					servizio = CostantiTestSuite.SPCOOP_NOME_SERVIZIO_VAL_OPENSPCOOP_GESTIONE_UTENTI_WDL;
					azione = CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_VALIDAZIONE_WSDL_OPENSPCOOP_AZIONE_ELIMINAZIONE_UTENTE_WDL;
				}
				else if(i==4){
					// Test  identificazione contenuto applicativo non corretto secondo il wsdl definitorio (xsd) per Validazione WSDL
					isTraced = false;
				}
				else if(i==5){
					// Test  identificazione contenuto applicativo non corretto secondo il wsdl definitorio (xsd) per Validazione OpenSPCoop
					isTraced = false;
				}
				else if(i==6){
					// Test contenuto corretto ma invocazione della porta delegata, con azione 'eliminazione' 
					//              mentre contenuto applicativo dell'operation 'registrazione'. 
					//              Viene riconosciuto che il contenuto e' di un'altra operation del port type
					isTraced = false;
				}
				else if(i==7){
					// Test contenuto corretto ma invocazione della porta delegata, con azione 'eliminazione' 
					//              mentre contenuto applicativo dell'operation 'registrazione'. 
					//              Viene riconosciuto che il contenuto e' di un'altra operation del port type
					isTraced = false;
				}
				else if(i==8){
					// Test contenuto corretto ma invocazione con una SOAPAction non conforme a quanto indicato nel wsdl per Validazione WSDL
					isTraced = false;
				}
				else if(i==9){
					// Test contenuto corretto ma invocazione con una SOAPAction non conforme a quanto indicato nel wsdl per Validazione OpenSPCoop 
					isTraced = false;
				}
				
				
				String id = ids[i];
				
				if(isTraced){
					
					// Busta di Richiesta
					Reporter.log("Controllo tracciamento richiesta con id: " +id);
					Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
					Reporter.log("Controllo valore Mittente Busta con id: " +id);
					Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id, this.collaborazioneSPCoopBase.getMittente(), null));
					Reporter.log("Controllo valore Destinatario Busta con id: " +id);
					Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedDestinatario(id, this.collaborazioneSPCoopBase.getDestinatario(), null));
					Reporter.log("Controllo valore Servizio Busta con id: " +id);
					DatiServizio datiServizio = new DatiServizio(tipoServizio, servizio, CostantiTestSuite.SPCOOP_VERSIONE_SERVIZIO_DEFAULT);
					Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedServizio(id, datiServizio));
					if(azione!=null){
						Reporter.log("Controllo valore Azione Busta con id: " +id);
						Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedAzione(id, azione));
					}
					Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
					Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloDiCollaborazione(id, SPCoopCostanti.PROFILO_COLLABORAZIONE_SINCRONO,ProfiloDiCollaborazione.SINCRONO));
					Reporter.log("Controllo valore Collaborazione Busta con id: " +id);
					Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedCollaborazione(id, null));
					Reporter.log("Controllo valore Profilo di Trasmissione Busta con id: " +id);
					Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloTrasmissione(id, this.collaborazioneSPCoopBase.isConfermaRicezione(),this.collaborazioneSPCoopBase.getInoltro(),this.collaborazioneSPCoopBase.getInoltroSdk()));
					Reporter.log("Controllo che la busta abbia/non generato eccezioni, id: " +id);
					Assert.assertTrue(data.getVerificatoreTracciaRisposta().existsListaEccezioni(id)==eccezione);
					Reporter.log("Controllo lista trasmissione con id: " +id);
					Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedTrasmissione(id, this.collaborazioneSPCoopBase.getMittente(), 
							null, 
							this.collaborazioneSPCoopBase.getDestinatario(), 
							null));
					if(checkServizioApplicativo){
						Reporter.log("Numero messaggi arrivati al servizio applicativo: "+data.getVerificatoreTracciaRichiesta().isArrivedCount(id));
						Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isArrivedCount(id)==1);
					}
					Reporter.log("----------------------------------------------------------");
	
					if(eccezione == false){
						Reporter.log("Controllo tracciamento risposta con riferimento id: " +id);
						Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTraced(id));
						Reporter.log("Controllo valore Mittente Busta della risposta con riferimento id: " +id);
						Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedMittente(id, this.collaborazioneSPCoopBase.getDestinatario(), null));
						Reporter.log("Controllo valore Destinatario Busta della risposta con riferimento id: " +id);
						Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedDestinatario(id, this.collaborazioneSPCoopBase.getMittente(), null));
						Reporter.log("Controllo valore Servizio Busta della risposta con riferimento id: " +id);
						Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedServizio(id, datiServizio));
						if(azione!=null){
							Reporter.log("Controllo valore Azione Busta della risposta con riferimento id: " +id);
							Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedAzione(id, azione));
						}
						Reporter.log("Controllo valore Profilo di Collaborazione Busta della risposta con riferimento id: " +id);
						Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedProfiloDiCollaborazione(id, SPCoopCostanti.PROFILO_COLLABORAZIONE_SINCRONO, ProfiloDiCollaborazione.SINCRONO));
						Reporter.log("Controllo valore Collaborazione Busta con id: " +id);
						Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedCollaborazione(id, null));
						Reporter.log("Controllo valore Profilo di Trasmissione Busta con id: " +id);
						Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedProfiloTrasmissione(id, this.collaborazioneSPCoopBase.isConfermaRicezione(),this.collaborazioneSPCoopBase.getInoltro(),this.collaborazioneSPCoopBase.getInoltroSdk()));
						Reporter.log("Controllo che la busta non abbia generato eccezioni, riferimento messaggio: " +id);
						Assert.assertTrue(data.getVerificatoreTracciaRisposta().existsListaEccezioni(id)==false);
						Reporter.log("Controllo lista trasmissione, riferimento messaggio: " +id);
						Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedTrasmissione(id, this.collaborazioneSPCoopBase.getDestinatario(), 
								null,
								this.collaborazioneSPCoopBase.getMittente(), 
								null));
					}
					else{
						// TODO se serve...
					}
				}else{
					Reporter.log("Controllo tracciamento richiesta non effettuata con id: " +id);
					Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id)==false);
				}
			}

		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}







	
	
	
	
	
	
	
	
	/***
	 * Test 2 Invocazione servizio AggiornamentoUtentiWrappedDocumentLiteral
	 * Scopo del test: corretta validazione del contenuto applicativo 
	 *                 realizzato in stile wrapped document literal
	 *                 Il portType ridefinisce il profilo collaborazione    
	 */
	
	@SuppressWarnings({ "deprecation" })
	private String getIDEgov(AggiornamentoUtentiWrappedDocumentLiteralServiceLocator locator)throws Exception{
		return this.getIDEgov(locator.getCall());
	}
	
	private void invocaServizioAggiornamentoUtentiWrappedDocumentLiteral(String portaDelegata,String servizio,String azione,String test,boolean notifica) throws Exception{
		Reporter.log("["+test+"] Invocazione GestioneUtenti WrappedDocumentLiteral ["+Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore()+
				portaDelegata+"/"+
				servizio+"/"+
				azione+"]");
		
		AggiornamentoUtentiWrappedDocumentLiteralServiceLocator locator = new AggiornamentoUtentiWrappedDocumentLiteralServiceLocator();
		locator.setAggiornamentoUtentiWrappedDocumentLiteralEndpointAddress(
				Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore()+
				portaDelegata+"/"+
				servizio+"/"+
				azione);
		AggiornamentoUtentiWrappedDocumentLiteral port = locator.getAggiornamentoUtentiWrappedDocumentLiteral();
		
		AggiornamentoNominativo nominativo = new AggiornamentoNominativo();
		nominativo.setNomePrecedente("Mario Rossi");
		nominativo.set_value("Mario Verdi");
		
		try{
			if(notifica){
				NotificaAggiornamentoUtenteWDLRequest wrappedRequest = new NotificaAggiornamentoUtenteWDLRequest();
				wrappedRequest.setAggiornamentoNominativo(nominativo);
				wrappedRequest.setIndirizzo("viale Roma 26");
				
				port.notificaAggiornamentoUtenteWDL(wrappedRequest);
				Reporter.log("["+test+"] Invocazione AggiurnamentoUtenti WrappedDocumentLiteral (azione notificaAggiornamentoUtenteWDL) ");
			}else{
				AggiornamentoUtenteWDLRequest wrappedRequest = new AggiornamentoUtenteWDLRequest();
				wrappedRequest.setAggiornamentoNominativo(nominativo);
				wrappedRequest.setIndirizzo("viale Roma 26");
				
				AggiornamentoUtenteWDLResponse esito = port.aggiornamentoUtenteWDL(wrappedRequest);
				String data = "";
				if(esito.getOraRegistrazione()!=null)
					data=" ["+esito.getOraRegistrazione().toString()+"] ";
				Reporter.log("["+test+"] Invocazione AggiurnamentoUtenti WrappedDocumentLiteral (azione aggiornamentoUtenteWDL) (oraRegistazione:"+data+") esito invocazione: "+esito.getEsito());
				Assert.assertTrue("OK".equals(esito.getEsito()));
			}
		}catch(Exception e){
			throw e;
		}finally{
			String idEGov = this.getIDEgov(locator);
			Reporter.log("["+test+"] Invocazione AggiurnamentoUtenti WrappedDocumentLiteral idEgov: "+idEGov);
			Assert.assertTrue(idEGov!=null);
			this.repositoryAggiornamentoUtentiWrappedDocumentLiteral.add(idEGov);
		}
	}
	
	
	Repository repositoryAggiornamentoUtentiWrappedDocumentLiteral=new Repository();
	@Test(groups={ValidazioneContenutiApplicativi.ID_GRUPPO,
			ValidazioneContenutiApplicativi.ID_GRUPPO+".AggiornamentoUtentiWrappedDocumentLiteral"},description="Test di tipo sincrono, Viene controllato se i body sono uguali e se gli attachment sono uguali")
	public void aggiornamentoUtentiWrappedDocumentLiteral() throws TestSuiteException, IOException, Exception{
		
		TestSuiteTransformer.sequentialForced = true;
		
		// Test contenuto corretto per Validazione WSDL (operazione notifica)
		invocaServizioContenutoApplicativo("notificaAggiornamentoUtenteWDL.xml",
				this.repositoryAggiornamentoUtentiWrappedDocumentLiteral,
				CostantiTestSuite.PORTA_DELEGATA_VALIDAZIONE_WSDL_GENERICA+"/"+
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_VAL_WSDL_AGGIORNAMENTO_UTENTI_WDL+"/"+
					CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_VALIDAZIONE_WSDL_OPENSPCOOP_AZIONE_NOTIFICA_AGGIORNAMENTO_UTENTE_WDL,
					"notificaAggiornamentoUtenteWDL");
		
		// Test contenuto corretto per Validazione OpenSPCoop (operazione notifica)
		invocaServizioContenutoApplicativo("notificaAggiornamentoUtenteWDL.xml",
				this.repositoryAggiornamentoUtentiWrappedDocumentLiteral,
				CostantiTestSuite.PORTA_DELEGATA_VALIDAZIONE_OPENSPCOOP_GENERICA+"/"+
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_VAL_OPENSPCOOP_AGGIORNAMENTO_UTENTI_WDL+"/"+
					CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_VALIDAZIONE_WSDL_OPENSPCOOP_AZIONE_NOTIFICA_AGGIORNAMENTO_UTENTE_WDL,
					"notificaAggiornamentoUtenteWDL");
		
		// Test contenuto corretto per Validazione WSDL (operazione aggiornamento)
		invocaServizioAggiornamentoUtentiWrappedDocumentLiteral(CostantiTestSuite.PORTA_DELEGATA_VALIDAZIONE_WSDL_GENERICA,
				CostantiTestSuite.SPCOOP_NOME_SERVIZIO_VAL_WSDL_AGGIORNAMENTO_UTENTI_WDL,
				CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_VALIDAZIONE_WSDL_OPENSPCOOP_AZIONE_AGGIORNAMENTO_UTENTE_WDL,
				"ValidazioneWSDL_OperationAggiornamento",false);
		
		// Test contenuto corretto per Validazione OpenSPCoop (operazione aggiornamento)
		invocaServizioAggiornamentoUtentiWrappedDocumentLiteral(CostantiTestSuite.PORTA_DELEGATA_VALIDAZIONE_OPENSPCOOP_GENERICA,
				CostantiTestSuite.SPCOOP_NOME_SERVIZIO_VAL_OPENSPCOOP_AGGIORNAMENTO_UTENTI_WDL,
				CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_VALIDAZIONE_WSDL_OPENSPCOOP_AZIONE_AGGIORNAMENTO_UTENTE_WDL,
				"ValidazioneOpenSPCoop_OperationAggiornamento",false);
				
	}
	@DataProvider (name="aggiornamentoUtentiWrappedDocumentLiteral")
	public Object[][]testAggiornamentoUtentiWrappedDocumentLiteral()throws Exception{
		String [] ids = new String[1];
		Vector<String> idsV = new Vector<String>();
		String id = null;
		while( (id=this.repositoryAggiornamentoUtentiWrappedDocumentLiteral.getNext()) != null){
			idsV.add(id);
		}
		ids = idsV.toArray(ids);
		
		try{
			Thread.sleep(3000);
		}catch(Exception e){}
		
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),ids,false},	
				{DatabaseProperties.getDatabaseComponentErogatore(),ids,true}	
		};
	}
	@Test(groups={ValidazioneContenutiApplicativi.ID_GRUPPO,
			ValidazioneContenutiApplicativi.ID_GRUPPO+".AggiornamentoUtentiWrappedDocumentLiteral"},
			dataProvider="aggiornamentoUtentiWrappedDocumentLiteral",
			dependsOnMethods={"aggiornamentoUtentiWrappedDocumentLiteral"})
	public void testAggiornamentoUtentiWrappedDocumentLiteral(DatabaseComponent data,String [] ids,boolean checkServizioApplicativo) throws Exception{
		try{
			for(int i=0; i<ids.length; i++){
				String id = ids[i];
				String tipoServizio = CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_VALIDAZIONE_WSDL_OPENSPCOOP;
				String servizio = null;
				String azione = null;
			
				if(i==0){
					// Test contenuto corretto per Validazione WSDL (operazione notifica)
					servizio = CostantiTestSuite.SPCOOP_NOME_SERVIZIO_VAL_WSDL_AGGIORNAMENTO_UTENTI_WDL;
					azione = CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_VALIDAZIONE_WSDL_OPENSPCOOP_AZIONE_NOTIFICA_AGGIORNAMENTO_UTENTE_WDL;
				}
				else if(i==1){
					// Test contenuto corretto per Validazione OpenSPCoop (operazione notifica)
					servizio = CostantiTestSuite.SPCOOP_NOME_SERVIZIO_VAL_OPENSPCOOP_AGGIORNAMENTO_UTENTI_WDL;
					azione = CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_VALIDAZIONE_WSDL_OPENSPCOOP_AZIONE_NOTIFICA_AGGIORNAMENTO_UTENTE_WDL;
				}
				else if(i==2){
					// Test contenuto corretto per Validazione WSDL (operazione aggiornamento)
					servizio = CostantiTestSuite.SPCOOP_NOME_SERVIZIO_VAL_WSDL_AGGIORNAMENTO_UTENTI_WDL;
					azione = CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_VALIDAZIONE_WSDL_OPENSPCOOP_AZIONE_AGGIORNAMENTO_UTENTE_WDL;
				}
				else if(i==3){
					// Test contenuto corretto per Validazione OpenSPCoop (operazione aggiornamento)
					servizio = CostantiTestSuite.SPCOOP_NOME_SERVIZIO_VAL_OPENSPCOOP_AGGIORNAMENTO_UTENTI_WDL;
					azione = CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_VALIDAZIONE_WSDL_OPENSPCOOP_AZIONE_AGGIORNAMENTO_UTENTE_WDL;
				}
				
				
				if(i==0 || i==1){
					// Test contenuto corretto per Validazione (operazione notifica)
					this.collaborazioneSPCoopBase.testOneWay(data,id, tipoServizio,servizio,azione, checkServizioApplicativo,null);
				}
				else{
					// Test contenuto corretto per Validazione (operazione aggiornamento)
					this.collaborazioneSPCoopBase.testSincrono(data, id, tipoServizio,servizio,azione, checkServizioApplicativo,null);
				}
			}
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	
	
	
	
	
	
	
	
	
	// Utility per Test 3 e 4
	@SuppressWarnings({ "deprecation" })
	private String getIDEgov(AggiornamentoAsincronoWrappedDocumentLiteralServiceLocator locator)throws Exception{
		return this.getIDEgov(locator.getCall());
	}
	@SuppressWarnings({ "unused", "deprecation" })
	private String getIDEgov(EsitoAggiornamentoAsincronoWrappedDocumentLiteralServiceLocator locator)throws Exception{
		return this.getIDEgov(locator.getCall());
	}
	
	private void invocaServizioAsincronoWrappedDocumentLiteral(String portaDelegata,
			String test,String username,String password,boolean asincronoSimmetrico,Repository repository) throws Exception{
		
		AggiornamentoAsincronoWrappedDocumentLiteralServiceLocator locator =  null;
		try{
			Reporter.log("["+test+"] Invocazione AggiornamentoAsincronoWrappedDocumentLiteral ["+Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore()+
					portaDelegata+"]");
			
			locator = 
				new AggiornamentoAsincronoWrappedDocumentLiteralServiceLocator();
			locator.setAggiornamentoAsincronoWrappedDocumentLiteralEndpointAddress(
					Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore()+
					portaDelegata);
			AggiornamentoAsincronoWrappedDocumentLiteral port = locator.getAggiornamentoAsincronoWrappedDocumentLiteral();
	
			if(username !=null && password!=null){
				// to use Basic HTTP Authentication: 
				Reporter.log("["+test+"] SET AUTENTICAZIONE ["+username+"] ["+password+"]");
				((Stub) port)._setProperty(javax.xml.rpc.Call.USERNAME_PROPERTY, username);
				((Stub) port)._setProperty(javax.xml.rpc.Call.PASSWORD_PROPERTY, password);
			}
			
			if(asincronoSimmetrico){
				RichiestaAggiornamentoUtenteAsincronoSimmetricoWDLRequest wrappedRequest = new RichiestaAggiornamentoUtenteAsincronoSimmetricoWDLRequest();
				wrappedRequest.setNominativo("Mario Rossi");
				wrappedRequest.setIndirizzo("viale Roma 26");
				wrappedRequest.setOraRegistrazione(new java.util.Date());
				RichiestaAggiornamentoUtenteAsincronoSimmetricoWDLResponse esito = port.richiestaAggiornamentoUtenteAsincronoSimmetricoWDL(wrappedRequest);
				Reporter.log("["+test+"] Registrazione di AggiornamentoAsincronoSimmetricoWrappedDocumentLiteral inviata con esito-ack: "+esito.getAckRichiestaAsincrona());
				Assert.assertTrue("OK".equals(esito.getAckRichiestaAsincrona()));
			}else{
				RichiestaAggiornamentoUtenteAsincronoAsimmetricoWDLRequest wrappedRequest = new RichiestaAggiornamentoUtenteAsincronoAsimmetricoWDLRequest();
				wrappedRequest.setNominativo("Mario Rossi");
				wrappedRequest.setIndirizzo("viale Roma 26");
				wrappedRequest.setOraRegistrazione(new java.util.Date());
				RichiestaAggiornamentoUtenteAsincronoAsimmetricoWDLResponse esito = port.richiestaAggiornamentoUtenteAsincronoAsimmetricoWDL(wrappedRequest);
				Reporter.log("["+test+"] Registrazione di AggiornamentoAsincronoSimmetricoWrappedDocumentLiteral inviata con esito-ack: "+esito.getAckRichiestaAsincrona());
				Assert.assertTrue("OK".equals(esito.getAckRichiestaAsincrona()));
			}
			
		}catch(Exception e){
			throw e;
		}finally{
			String idEGov = this.getIDEgov(locator);
			Reporter.log("["+test+"] Invocazione AggiurnamentoUtentiAsincrono WrappedDocumentLiteral idEgov: "+idEGov);
			Assert.assertTrue(idEGov!=null);
			repository.add(idEGov);
		}
	}
	
	
	
	
	
	
	
	/***
	 * Test 3 Invocazione servizio AggiornamentoAsincronoWrappedDocumentLiteral e EsitoAggiornamentoAsincronoWrappedDocumentLiteral
	 * Scopo del test: corretta validazione del contenuto applicativo 
	 *                 realizzato in stile wrapped document literal
	 *                 E' stato implementato il profilo Asincrono Simmetrico
	 *                 operation: richiestaAsincronaSimmetrica  
	 *                 operation: rispostaAsincronaSimmetrica
	 */
	Repository repositoryAsincronoSimmetricoWrappedDocumentLiteral=new Repository();
	@Test(groups={ValidazioneContenutiApplicativi.ID_GRUPPO,
			ValidazioneContenutiApplicativi.ID_GRUPPO+".AsincronoSimmetricoWrappedDocumentLiteral"},description="Test di tipo sincrono, Viene controllato se i body sono uguali e se gli attachment sono uguali")
	public void asincronoSimmetricoRichiestaWrappedDocumentLiteral() throws TestSuiteException, IOException, Exception{
		
		TestSuiteTransformer.sequentialForced = true;
		
		// Test contenuto corretto per Validazione WSDL (operazione richiesta)
		invocaServizioAsincronoWrappedDocumentLiteral(CostantiTestSuite.PORTA_DELEGATA_VALIDAZIONE_WSDL_RICHIESTA_ASINCRONA_SIMMETRICA,
				"testAsincronoSimmetricoRichiesta",
				"AggiornamentoAsincronoSimmetricoWrappedDocumentLiteral","123456",
				true,this.repositoryAsincronoSimmetricoWrappedDocumentLiteral);

		String idEGovRichiesta = this.repositoryAsincronoSimmetricoWrappedDocumentLiteral.getNext();
		Assert.assertTrue(idEGovRichiesta!=null);
		
		try{
			Thread.sleep(5000);
		}catch(Exception e){}
		
		// Risposta Asincrona Simmetrica per Validazione WSDL
		invocaServizioContenutoApplicativoRispostaAsincronaSimmetrica("esitoAggiornamentoUtenteAsincronoSimmetricoWDL.xml", 
				this.repositoryAsincronoSimmetricoWrappedDocumentLiteral, 
				CostantiTestSuite.PORTA_DELEGATA_VALIDAZIONE_WSDL_RISPOSTA_ASINCRONA_SIMMETRICA, 
				"esitoAggiornamentoUtenteAsincronoSimmetricoWDL", idEGovRichiesta);
		
		// Test contenuto corretto per Validazione OpenSPCoop (operazione richiesta)
		invocaServizioAsincronoWrappedDocumentLiteral(CostantiTestSuite.PORTA_DELEGATA_VALIDAZIONE_OPENSPCOOP_RICHIESTA_ASINCRONA_SIMMETRICA,
				"testAsincronoSimmetricoRichiesta",
				"AggiornamentoAsincronoSimmetricoWrappedDocumentLiteral","123456",
				true,this.repositoryAsincronoSimmetricoWrappedDocumentLiteral);

		this.repositoryAsincronoSimmetricoWrappedDocumentLiteral.getNext(); // Brucio id risposta asincrona precedente
		idEGovRichiesta = this.repositoryAsincronoSimmetricoWrappedDocumentLiteral.getNext();
		Assert.assertTrue(idEGovRichiesta!=null);
		
		try{
			Thread.sleep(5000);
		}catch(Exception e){}
		
		// Risposta Asincrona Simmetrica per Validazione OpenSPCoop
		invocaServizioContenutoApplicativoRispostaAsincronaSimmetrica("esitoAggiornamentoUtenteAsincronoSimmetricoWDL.xml", 
				this.repositoryAsincronoSimmetricoWrappedDocumentLiteral, 
				CostantiTestSuite.PORTA_DELEGATA_VALIDAZIONE_OPENSPCOOP_RISPOSTA_ASINCRONA_SIMMETRICA, 
				"esitoAggiornamentoUtenteAsincronoSimmetricoWDL", idEGovRichiesta);
		
	}
	
	@DataProvider (name="AsincronoSimmetricoWrappedDocumentLiteral")
	public Object[][]testAsincronoSimmetricoWrappedDocumentLiteral()throws Exception{
		String [] ids = new String[1];
		Vector<String> idsV = new Vector<String>();
		String id = null;
		// Ripristino index
		this.repositoryAsincronoSimmetricoWrappedDocumentLiteral.setIndex(0);
	
		while( (id=this.repositoryAsincronoSimmetricoWrappedDocumentLiteral.getNext()) != null){
			idsV.add(id);
		}
		ids = idsV.toArray(ids);
		
		try{
			Thread.sleep(3000);
		}catch(Exception e){}
		
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),ids,false},	
				{DatabaseProperties.getDatabaseComponentErogatore(),ids,true}	
		};
	}
	@Test(groups={ValidazioneContenutiApplicativi.ID_GRUPPO,
			ValidazioneContenutiApplicativi.ID_GRUPPO+".AsincronoSimmetricoWrappedDocumentLiteral"},
			dataProvider="AsincronoSimmetricoWrappedDocumentLiteral",
			dependsOnMethods={"asincronoSimmetricoRichiestaWrappedDocumentLiteral"})
	public void testAsincronoSimmetricoWrappedDocumentLiteral(DatabaseComponent data,String [] ids,boolean checkServizioApplicativo) throws Exception{
		
		TestSuiteTransformer.sequentialForced = true;
		
		try{
					
			String idAsincrono = null;
			for(int i=0; i<ids.length; i++){
				String id = ids[i];
				String tipoServizio = CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_VALIDAZIONE_WSDL_OPENSPCOOP;
				String servizio = null;
				String azione = null;
				String tipoServizioCorrelato = CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_VALIDAZIONE_WSDL_OPENSPCOOP;
				String servizioCorrelato = null;
				
				if(i==0){
					// Test contenuto corretto per Validazione WSDL (operazione richiesta asincrona simmetrica)
					servizio = CostantiTestSuite.SPCOOP_NOME_SERVIZIO_VAL_WSDL_AGGIORNAMENTO_ASINCRONO_SIMMETRICO_WDL;
					azione = CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_VALIDAZIONE_WSDL_OPENSPCOOP_AZIONE_RICHIESTA_AGGIORNAMENTO_UTENTE_ASINCRONO_SIMMETRICO_WDL;
					servizioCorrelato = CostantiTestSuite.SPCOOP_NOME_SERVIZIO_VAL_WSDL_ESITO_AGGIORNAMENTO_ASINCRONO_SIMMETRICO_WDL;
					idAsincrono = id;
				}
				else if(i==1){
					// Test contenuto corretto per Validazione WSDL (operazione risposta asincrona simmetrica)
					servizio = CostantiTestSuite.SPCOOP_NOME_SERVIZIO_VAL_WSDL_ESITO_AGGIORNAMENTO_ASINCRONO_SIMMETRICO_WDL;
					azione = CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_VALIDAZIONE_WSDL_OPENSPCOOP_AZIONE_ESITO_AGGIORNAMENTO_ASINCRONO_SIMMETRICO_WDL;
				}
				else if(i==2){
					// Test contenuto corretto per Validazione OpenSPCoop (operazione richiesta asincrona simmetrica)
					servizio = CostantiTestSuite.SPCOOP_NOME_SERVIZIO_VAL_OPENSPCOOP_AGGIORNAMENTO_ASINCRONO_SIMMETRICO_WDL;
					azione = CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_VALIDAZIONE_WSDL_OPENSPCOOP_AZIONE_RICHIESTA_AGGIORNAMENTO_UTENTE_ASINCRONO_SIMMETRICO_WDL;
					servizioCorrelato = CostantiTestSuite.SPCOOP_NOME_SERVIZIO_VAL_OPENSPCOOP_ESITO_AGGIORNAMENTO_ASINCRONO_SIMMETRICO_WDL;
					idAsincrono = id;
				}
				else if(i==3){
					// Test contenuto corretto per Validazione OpenSPCoop (operazione risposta asincrona simmetrica)
					servizio = CostantiTestSuite.SPCOOP_NOME_SERVIZIO_VAL_OPENSPCOOP_ESITO_AGGIORNAMENTO_ASINCRONO_SIMMETRICO_WDL;
					azione = CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_VALIDAZIONE_WSDL_OPENSPCOOP_AZIONE_ESITO_AGGIORNAMENTO_ASINCRONO_SIMMETRICO_WDL;
				}
				
				
				if(i==0 || i==2){
					// Test contenuto corretto per Validazione (operazione richiesta asincrona simmetrica)
					this.collaborazioneSPCoopBase.testAsincronoSimmetrico_ModalitaSincrona(data, id, tipoServizio, servizio, azione, 
							checkServizioApplicativo, tipoServizioCorrelato, servizioCorrelato, null);
				}
				else{
					// Test contenuto corretto per Validazione (operazione risposta asincrona simmetrica)
					this.collaborazioneSPCoopBase.testRispostaAsincronoSimmetrico_ModalitaSincrona(data, id, 
							idAsincrono, tipoServizio, servizio, azione, checkServizioApplicativo, null);
				}
			}
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	/***
	 * Test 4 Invocazione servizio AggiornamentoAsincronoWrappedDocumentLiteral e EsitoAggiornamentoAsincronoWrappedDocumentLiteral
	 * Scopo del test: corretta validazione del contenuto applicativo 
	 *                 realizzato in stile wrapped document literal
	 *                 E' stato implementato il profilo Asincrono Asimmetrico
	 *                 operation: richiestaAsincronaAsimmetrica  
	 *                 operation: rispostaAsincronaAsimmetrica
	 */
	Repository repositoryAsincronoAsimmetricoWrappedDocumentLiteral=new Repository();
	@Test(groups={ValidazioneContenutiApplicativi.ID_GRUPPO,
			ValidazioneContenutiApplicativi.ID_GRUPPO+".AsincronoAsimmetricoWrappedDocumentLiteral"},description="Test di tipo sincrono, Viene controllato se i body sono uguali e se gli attachment sono uguali")
	public void asincronoAsimmetricoRichiestaWrappedDocumentLiteral() throws TestSuiteException, IOException, Exception{
		
		TestSuiteTransformer.sequentialForced = true;
		
		// Test contenuto corretto per Validazione WSDL (operazione richiesta)
		invocaServizioAsincronoWrappedDocumentLiteral(CostantiTestSuite.PORTA_DELEGATA_VALIDAZIONE_WSDL_RICHIESTA_ASINCRONA_ASIMMETRICA,
				"testAsincronoAsimmetricoRichiesta",
				null,null,
				false,this.repositoryAsincronoAsimmetricoWrappedDocumentLiteral);

		String idEGovRichiesta = this.repositoryAsincronoAsimmetricoWrappedDocumentLiteral.getNext();
		Assert.assertTrue(idEGovRichiesta!=null);
		
		try{
			Thread.sleep(5000);
		}catch(Exception e){}
		
		// Risposta Asincrona Asimmetrica per Validazione WSDL
		invocaServizioContenutoApplicativoRispostaAsincronaAsimmetrica("esitoAggiornamentoUtenteAsincronoAsimmetricoWDL.xml", 
				this.repositoryAsincronoAsimmetricoWrappedDocumentLiteral, 
				CostantiTestSuite.PORTA_DELEGATA_VALIDAZIONE_WSDL_RICHIESTA_STATO_ASINCRONA_ASIMMETRICA, 
				"esitoAggiornamentoUtenteAsincronoAsimmetricoWDL", idEGovRichiesta);
		
		// Test contenuto corretto per Validazione OpenSPCoop (operazione richiesta)
		invocaServizioAsincronoWrappedDocumentLiteral(CostantiTestSuite.PORTA_DELEGATA_VALIDAZIONE_OPENSPCOOP_RICHIESTA_ASINCRONA_ASIMMETRICA,
				"testAsincronoAsimmetricoRichiesta",
				null,null,
				false,this.repositoryAsincronoAsimmetricoWrappedDocumentLiteral);

		this.repositoryAsincronoAsimmetricoWrappedDocumentLiteral.getNext(); // Brucio id risposta asincrona precedente
		idEGovRichiesta = this.repositoryAsincronoAsimmetricoWrappedDocumentLiteral.getNext();
		Assert.assertTrue(idEGovRichiesta!=null);
		
		try{
			Thread.sleep(5000);
		}catch(Exception e){}
		
		// Risposta Asincrona Asimmetrica per Validazione OpenSPCoop
		invocaServizioContenutoApplicativoRispostaAsincronaAsimmetrica("esitoAggiornamentoUtenteAsincronoAsimmetricoWDL.xml", 
				this.repositoryAsincronoAsimmetricoWrappedDocumentLiteral, 
				CostantiTestSuite.PORTA_DELEGATA_VALIDAZIONE_OPENSPCOOP_RICHIESTA_STATO_ASINCRONA_ASIMMETRICA, 
				"esitoAggiornamentoUtenteAsincronoAsimmetricoWDL", idEGovRichiesta);
		
	}
	
	@DataProvider (name="AsincronoAsimmetricoWrappedDocumentLiteral")
	public Object[][]testAsincronoAsimmetricoWrappedDocumentLiteral()throws Exception{
		String [] ids = new String[1];
		Vector<String> idsV = new Vector<String>();
		String id = null;
		// Ripristino index
		this.repositoryAsincronoAsimmetricoWrappedDocumentLiteral.setIndex(0);
	
		while( (id=this.repositoryAsincronoAsimmetricoWrappedDocumentLiteral.getNext()) != null){
			idsV.add(id);
		}
		ids = idsV.toArray(ids);
		
		try{
			Thread.sleep(3000);
		}catch(Exception e){}
		
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),ids,false},	
				{DatabaseProperties.getDatabaseComponentErogatore(),ids,true}	
		};
	}
	@Test(groups={ValidazioneContenutiApplicativi.ID_GRUPPO,
			ValidazioneContenutiApplicativi.ID_GRUPPO+".AsincronoAsimmetricoWrappedDocumentLiteral"},
			dataProvider="AsincronoAsimmetricoWrappedDocumentLiteral",
			dependsOnMethods={"asincronoAsimmetricoRichiestaWrappedDocumentLiteral"})
	public void testAsincronoAsimmetricoWrappedDocumentLiteral(DatabaseComponent data,String [] ids,boolean checkServizioApplicativo) throws Exception{
		
		TestSuiteTransformer.sequentialForced = true;
		
		try{

			String idAsincrono = null;
			for(int i=0; i<ids.length; i++){
				String id = ids[i];
				String tipoServizio = CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_VALIDAZIONE_WSDL_OPENSPCOOP;
				String servizio = null;
				String azione = null;
				String tipoServizioCorrelato = CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_VALIDAZIONE_WSDL_OPENSPCOOP;
				String servizioCorrelato = null;
				
				if(i==0){
					// Test contenuto corretto per Validazione WSDL (operazione richiesta asincrona asimmetrica)
					servizio = CostantiTestSuite.SPCOOP_NOME_SERVIZIO_VAL_WSDL_AGGIORNAMENTO_ASINCRONO_ASIMMETRICO_WDL;
					azione = CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_VALIDAZIONE_WSDL_OPENSPCOOP_AZIONE_RICHIESTA_AGGIORNAMENTO_UTENTE_ASINCRONO_ASIMMETRICO_WDL;
					servizioCorrelato = CostantiTestSuite.SPCOOP_NOME_SERVIZIO_VAL_WSDL_ESITO_AGGIORNAMENTO_ASINCRONO_ASIMMETRICO_WDL;
					idAsincrono = id;
				}
				else if(i==1){
					// Test contenuto corretto per Validazione WSDL (operazione risposta asincrona asimmetrica)
					servizio = CostantiTestSuite.SPCOOP_NOME_SERVIZIO_VAL_WSDL_ESITO_AGGIORNAMENTO_ASINCRONO_ASIMMETRICO_WDL;
					azione = CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_VALIDAZIONE_WSDL_OPENSPCOOP_AZIONE_ESITO_AGGIORNAMENTO_ASINCRONO_ASIMMETRICO_WDL;
				}
				else if(i==2){
					// Test contenuto corretto per Validazione OpenSPCoop (operazione richiesta asincrona asimmetrica)
					servizio = CostantiTestSuite.SPCOOP_NOME_SERVIZIO_VAL_OPENSPCOOP_AGGIORNAMENTO_ASINCRONO_ASIMMETRICO_WDL;
					azione = CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_VALIDAZIONE_WSDL_OPENSPCOOP_AZIONE_RICHIESTA_AGGIORNAMENTO_UTENTE_ASINCRONO_ASIMMETRICO_WDL;
					servizioCorrelato = CostantiTestSuite.SPCOOP_NOME_SERVIZIO_VAL_OPENSPCOOP_ESITO_AGGIORNAMENTO_ASINCRONO_ASIMMETRICO_WDL;
					idAsincrono = id;
				}
				else if(i==3){
					// Test contenuto corretto per Validazione OpenSPCoop (operazione risposta asincrona asimmetrica)
					servizio = CostantiTestSuite.SPCOOP_NOME_SERVIZIO_VAL_OPENSPCOOP_ESITO_AGGIORNAMENTO_ASINCRONO_ASIMMETRICO_WDL;
					azione = CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_VALIDAZIONE_WSDL_OPENSPCOOP_AZIONE_ESITO_AGGIORNAMENTO_ASINCRONO_ASIMMETRICO_WDL;
				}
				
				
				if(i==0 || i==2){
					// Test contenuto corretto per Validazione (operazione richiesta asincrona asimmetrica)
					this.collaborazioneSPCoopBase.testAsincronoAsimmetrico_modalitaSincrona(data, id, tipoServizio, servizio, azione, 
							checkServizioApplicativo, tipoServizioCorrelato, servizioCorrelato, null);
				}
				else{
					// Test contenuto corretto per Validazione (operazione risposta asincrona asimmetrica)
					this.collaborazioneSPCoopBase.testRispostaAsincronoAsimmetrico_modalitaSincrona(data, id, 
							idAsincrono, tipoServizio, servizio, azione, checkServizioApplicativo, null);
				}
			}
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/***
	 *  Test 8 Invocazione servizio GestioneUtentiDocumentLiteral
	 * Scopo del test: corretta validazione del contenuto applicativo 
	 *                 realizzato in stile document literal (no wrapped)
	 *                 Il body e' formato da tre root-element:
	 *                 - nominativo
	 *                 - indirizzo
	 *                 - ora-registrazione          
	 */
	
	@SuppressWarnings({ "deprecation" })
	private String getIDEgov(GestioneUtentiDocumentLiteralServiceLocator locator)throws Exception{
		return this.getIDEgov(locator.getCall());
	}
	
	private void invocaServizioGestioneUtentiDocumentLiteral(String portaDelegata,String servizio,String azione,String test,boolean registra) throws Exception{
		Reporter.log("["+test+"] Invocazione GestioneUtenti DocumentLiteral  ["+Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore()+
				portaDelegata+"/"+
				servizio+"/"+
				azione+"]");
		
		GestioneUtentiDocumentLiteralServiceLocator locator = new GestioneUtentiDocumentLiteralServiceLocator();
		locator.setGestioneUtentiDocumentLiteralEndpointAddress(
				Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore()+
				portaDelegata+"/"+
				servizio+"/"+
				azione);
		GestioneUtentiDocumentLiteral port = locator.getGestioneUtentiDocumentLiteral();
		
		try{
			if(registra){
				String esito = port.registrazioneUtenteDL("Mario Rossi", "viale Roma 26", new java.util.Date());
				Reporter.log("["+test+"] Invocazione GestioneUtenti DocumentLiteral (azione registrazioneUtenteWDL) esito invocazione: "+esito);
				Assert.assertTrue("OK".equals(esito));
			}else{
				String esito = port.eliminazioneUtenteDL("Mario Rossi");
				Reporter.log("["+test+"] Invocazione GestioneUtenti WrappedDocumentLiteral (azione eliminazioneUtenteWDL) esito invocazione: "+esito);
				Assert.assertTrue("OK".equals(esito));
			}
		}catch(Exception e){
			throw e;
		}finally{
			String idEGov = this.getIDEgov(locator);
			Reporter.log("["+test+"] Invocazione GestioneUtenti WrappedDocumentLiteral idEgov: "+idEGov);
			Assert.assertTrue(idEGov!=null);
			this.repositoryGestioneUtentiDocumentLiteral.add(idEGov);
		}
	}
	
	
	Repository repositoryGestioneUtentiDocumentLiteral=new Repository();
	@Test(groups={ValidazioneContenutiApplicativi.ID_GRUPPO,
			ValidazioneContenutiApplicativi.ID_GRUPPO+".GestioneUtentiDocumentLiteral"},description="Test di tipo sincrono, Viene controllato se i body sono uguali e se gli attachment sono uguali")
	public void gestioneUtentiDocumentLiteral() throws TestSuiteException, IOException, Exception{
		
		TestSuiteTransformer.sequentialForced = true;
		
		// Test contenuto corretto per Validazione WSDL (operazione registrazione)
		invocaServizioGestioneUtentiDocumentLiteral(CostantiTestSuite.PORTA_DELEGATA_VALIDAZIONE_WSDL_GENERICA,
				CostantiTestSuite.SPCOOP_NOME_SERVIZIO_VAL_WSDL_GESTIONE_UTENTI_DL,
				CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_VALIDAZIONE_WSDL_OPENSPCOOP_AZIONE_REGISTRAZIONE_UTENTE_DL,
				"ValidazioneWSDL_OperationRegistrazione",true);
		
		// Test contenuto corretto per Validazione OpenSPCoop (operazione registrazione)
		invocaServizioGestioneUtentiDocumentLiteral(CostantiTestSuite.PORTA_DELEGATA_VALIDAZIONE_OPENSPCOOP_GENERICA,
				CostantiTestSuite.SPCOOP_NOME_SERVIZIO_VAL_OPENSPCOOP_GESTIONE_UTENTI_DL,
				CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_VALIDAZIONE_WSDL_OPENSPCOOP_AZIONE_REGISTRAZIONE_UTENTE_DL,
				"ValidazioneOpenSPCoop_OperationRegistrazione",true);
		
		// Test contenuto corretto per Validazione WSDL (operazione eliminazione)
		invocaServizioGestioneUtentiDocumentLiteral(CostantiTestSuite.PORTA_DELEGATA_VALIDAZIONE_WSDL_GENERICA,
				CostantiTestSuite.SPCOOP_NOME_SERVIZIO_VAL_WSDL_GESTIONE_UTENTI_DL,
				CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_VALIDAZIONE_WSDL_OPENSPCOOP_AZIONE_ELIMINAZIONE_UTENTE_DL,
				"ValidazioneWSDL_OperationEliminazione",false);
		
		// Test contenuto corretto per Validazione OpenSPCoop (operazione eliminazione)
		invocaServizioGestioneUtentiDocumentLiteral(CostantiTestSuite.PORTA_DELEGATA_VALIDAZIONE_OPENSPCOOP_GENERICA,
				CostantiTestSuite.SPCOOP_NOME_SERVIZIO_VAL_OPENSPCOOP_GESTIONE_UTENTI_DL,
				CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_VALIDAZIONE_WSDL_OPENSPCOOP_AZIONE_ELIMINAZIONE_UTENTE_DL,
				"ValidazioneOpenSPCoop_OperationEliminazione",false);
	}
	@DataProvider (name="gestioneUtentiDocumentLiteral")
	public Object[][]testGestioneUtentiDocumentLiteral()throws Exception{
		String [] ids = new String[1];
		Vector<String> idsV = new Vector<String>();
		String id = null;
		while( (id=this.repositoryGestioneUtentiDocumentLiteral.getNext()) != null){
			idsV.add(id);
		}
		ids = idsV.toArray(ids);
		
		try{
			Thread.sleep(3000);
		}catch(Exception e){}
		
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),ids,false},	
				{DatabaseProperties.getDatabaseComponentErogatore(),ids,true}	
		};
	}
	@Test(groups={ValidazioneContenutiApplicativi.ID_GRUPPO,
			ValidazioneContenutiApplicativi.ID_GRUPPO+".GestioneUtentiDocumentLiteral"},
			dataProvider="gestioneUtentiDocumentLiteral",
			dependsOnMethods={"gestioneUtentiDocumentLiteral"})
	public void testGestioneUtentiDocumentLiteral(DatabaseComponent data,String [] ids,boolean checkServizioApplicativo) throws Exception{
		try{
			for(int i=0; i<ids.length; i++){
				
				String tipoServizio = CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_VALIDAZIONE_WSDL_OPENSPCOOP;
				String servizio = null;
				String azione = null;
				
				if(i==0){
					// Test contenuto corretto per Validazione WSDL (operazione registrazione)
					servizio = CostantiTestSuite.SPCOOP_NOME_SERVIZIO_VAL_WSDL_GESTIONE_UTENTI_DL;
					azione = CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_VALIDAZIONE_WSDL_OPENSPCOOP_AZIONE_REGISTRAZIONE_UTENTE_DL;
				}
				else if(i==1){
					// Test contenuto corretto per Validazione OpenSPCoop (operazione registrazione)
					servizio = CostantiTestSuite.SPCOOP_NOME_SERVIZIO_VAL_OPENSPCOOP_GESTIONE_UTENTI_DL;
					azione = CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_VALIDAZIONE_WSDL_OPENSPCOOP_AZIONE_REGISTRAZIONE_UTENTE_DL;
				}
				else if(i==2){
					// Test contenuto corretto per Validazione WSDL (operazione eliminazione)
					servizio = CostantiTestSuite.SPCOOP_NOME_SERVIZIO_VAL_WSDL_GESTIONE_UTENTI_DL;
					azione = CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_VALIDAZIONE_WSDL_OPENSPCOOP_AZIONE_ELIMINAZIONE_UTENTE_DL;
				}
				else if(i==3){
					// Test contenuto corretto per Validazione OpenSPCoop (operazione eliminazione)
					servizio = CostantiTestSuite.SPCOOP_NOME_SERVIZIO_VAL_OPENSPCOOP_GESTIONE_UTENTI_DL;
					azione = CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_VALIDAZIONE_WSDL_OPENSPCOOP_AZIONE_ELIMINAZIONE_UTENTE_DL;
				}
								
				
				String id = ids[i];
				
				this.collaborazioneSPCoopBase.testSincrono(data, id, tipoServizio, servizio, azione, checkServizioApplicativo, null);
			}

		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/***
	 *  Test Invocazione servizio GestioneUtentiDocumentLiteral
	 * 
	 * 1. Scopo del test: corretta validazione del contenuto applicativo 
	 *                 realizzato in stile document literal (no wrapped)
	 *                 Il  body e' pero formato da piu' root-element di quanti
	 *                 sono quelli attesi:
	 *                 - nominativo
	 *                 - indirizzo
	 *                 - ora-registrazione
	 *                 - nominativo (!in PIU'!)
	 *                 
	 * 2. Scopo del test: corretta validazione del contenuto applicativo 
	 *                 realizzato in stile document literal (no wrapped)
	 *                 Il  body e' pero formato da meno root-element di quanti
	 *                 sono quelli attesi:
	 *                 - nominativo
	 *                 - indirizzo
	 *                 - ora-registrazione (!ASSENTE!)
	 *                 
	 * 3. Scopo del test: corretta validazione del contenuto applicativo 
	 *                 realizzato in stile document literal (no wrapped)
	 *                 Il  body e' pero formato da root-element di cui 
	 *                 uno non e' quello atteso:
	 *                 - aggiorna-nominativo (!Atteso era nominativo!)
	 *                 - indirizzo
	 *                 - ora-registrazione
	 */
	Repository repositoryGestioneUtentiDocumentLiteralContenutiErrato=new Repository();
	@Test(groups={ValidazioneContenutiApplicativi.ID_GRUPPO,
			ValidazioneContenutiApplicativi.ID_GRUPPO+".GestioneUtentiDocumentLiteralContenutoErrato"},description="Test di tipo sincrono, Viene controllato se i body sono uguali e se gli attachment sono uguali")
	public void gestioneUtentiDocumentLiteralContenutoErrato() throws TestSuiteException, IOException, Exception{
		
		TestSuiteTransformer.sequentialForced = true;
		
		// Test N.1 con Validazione WSDL
		Date dataInizioTest = DateManager.getDate();
		invocaServizioContenutoApplicativoErrato("registrazioneDocumentLiteralConTroppiParametriInput.xml",
				this.repositoryGestioneUtentiDocumentLiteralContenutiErrato,
				CostantiTestSuite.PORTA_DELEGATA_VALIDAZIONE_WSDL_GENERICA+"/"+
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_VAL_WSDL_GESTIONE_UTENTI_DL+"/"+
					CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_VALIDAZIONE_WSDL_OPENSPCOOP_AZIONE_REGISTRAZIONE_UTENTE_DL,
				CostantiPdD.OPENSPCOOP2,Utilities.toString(CodiceErroreIntegrazione.CODICE_418_VALIDAZIONE_WSDL_RICHIESTA_FALLITA),
				"Il contenuto applicativo del messaggio di richiesta non rispetta l'accordo di servizio (Wsdl erogatore) definito nel Registro dei Servizi",
				"registrazioneUtenteDL");
		Date dataFineTest = DateManager.getDate();
		ErroreAttesoOpenSPCoopLogCore err = new ErroreAttesoOpenSPCoopLogCore();
		err.setIntervalloInferiore(dataInizioTest);
		err.setIntervalloSuperiore(dataFineTest);
		err.setMsgErrore("Validazione WSDL (true) fallita: Messaggio con elementi non conformi alla definizione wsdl dell'Operation [registrazioneUtenteDL] del port-type [GestioneUtentiDocumentLiteral] (AccordoServizio:ASMultiPortTypeValidazioneWSDL style:document use:literal)");
		this.erroriAttesiOpenSPCoopCore.add(err);
		
		ErroreAttesoOpenSPCoopLogCore errA = new ErroreAttesoOpenSPCoopLogCore();
		errA.setIntervalloInferiore(dataInizioTest);
		errA.setIntervalloSuperiore(dataFineTest);
		errA.setMsgErrore("Riscontrata non conformità rispetto all'interfaccia WSDL; Messaggio con elementi non conformi alla definizione wsdl dell'Operation [registrazioneUtenteDL] del port-type [GestioneUtentiDocumentLiteral] (AccordoServizio:ASMultiPortTypeValidazioneWSDL style:document use:literal)");
		this.erroriAttesiOpenSPCoopCore.add(errA);
		
		// Test N.1 con Validazione OpenSPCoop
		dataInizioTest = DateManager.getDate();
		invocaServizioContenutoApplicativoErrato("registrazioneDocumentLiteralConTroppiParametriInput.xml",
				this.repositoryGestioneUtentiDocumentLiteralContenutiErrato,
				CostantiTestSuite.PORTA_DELEGATA_VALIDAZIONE_OPENSPCOOP_GENERICA+"/"+
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_VAL_OPENSPCOOP_GESTIONE_UTENTI_DL+"/"+
					CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_VALIDAZIONE_WSDL_OPENSPCOOP_AZIONE_REGISTRAZIONE_UTENTE_DL,
				CostantiPdD.OPENSPCOOP2,Utilities.toString(CodiceErroreIntegrazione.CODICE_418_VALIDAZIONE_WSDL_RICHIESTA_FALLITA),
				"Il contenuto applicativo del messaggio di richiesta non rispetta l'accordo di servizio (Wsdl erogatore) definito nel Registro dei Servizi",
				"registrazioneUtenteDL");
		dataFineTest = DateManager.getDate();
		err = new ErroreAttesoOpenSPCoopLogCore();
		err.setIntervalloInferiore(dataInizioTest);
		err.setIntervalloSuperiore(dataFineTest);
		err.setMsgErrore("Validazione WSDL (true) fallita: Messaggio con elementi non conformi alla definizione wsdl dell'Azione [registrazioneUtenteDL] del Servizio [GestioneUtentiDocumentLiteral] (AccordoServizio:ASMultiPortTypeValidazioneOpenSPCoop style:document use:literal)");
		this.erroriAttesiOpenSPCoopCore.add(err);
		
		errA = new ErroreAttesoOpenSPCoopLogCore();
		errA.setIntervalloInferiore(dataInizioTest);
		errA.setIntervalloSuperiore(dataFineTest);
		errA.setMsgErrore("Riscontrata non conformità rispetto all'interfaccia WSDL; Messaggio con elementi non conformi alla definizione wsdl dell'Azione [registrazioneUtenteDL] del Servizio [GestioneUtentiDocumentLiteral] (AccordoServizio:ASMultiPortTypeValidazioneOpenSPCoop style:document use:literal)");
		this.erroriAttesiOpenSPCoopCore.add(errA);
		
		// Test N.2 con Validazione WSDL
		dataInizioTest = DateManager.getDate();
		invocaServizioContenutoApplicativoErrato("registrazioneDocumentLiteralConPochiParametriInput.xml",
				this.repositoryGestioneUtentiDocumentLiteralContenutiErrato,
				CostantiTestSuite.PORTA_DELEGATA_VALIDAZIONE_WSDL_GENERICA+"/"+
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_VAL_WSDL_GESTIONE_UTENTI_DL+"/"+
					CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_VALIDAZIONE_WSDL_OPENSPCOOP_AZIONE_REGISTRAZIONE_UTENTE_DL,
				CostantiPdD.OPENSPCOOP2,Utilities.toString(CodiceErroreIntegrazione.CODICE_418_VALIDAZIONE_WSDL_RICHIESTA_FALLITA),
				"Il contenuto applicativo del messaggio di richiesta non rispetta l'accordo di servizio (Wsdl erogatore) definito nel Registro dei Servizi",
				"registrazioneUtenteDL");
		dataFineTest = DateManager.getDate();
		err = new ErroreAttesoOpenSPCoopLogCore();
		err.setIntervalloInferiore(dataInizioTest);
		err.setIntervalloSuperiore(dataFineTest);
		err.setMsgErrore("Validazione WSDL (true) fallita: Messaggio con elementi non conformi alla definizione wsdl dell'Operation [registrazioneUtenteDL] del port-type [GestioneUtentiDocumentLiteral] (AccordoServizio:ASMultiPortTypeValidazioneWSDL style:document use:literal)");
		this.erroriAttesiOpenSPCoopCore.add(err);
		
		errA = new ErroreAttesoOpenSPCoopLogCore();
		errA.setIntervalloInferiore(dataInizioTest);
		errA.setIntervalloSuperiore(dataFineTest);
		errA.setMsgErrore("Riscontrata non conformità rispetto all'interfaccia WSDL; Messaggio con elementi non conformi alla definizione wsdl dell'Operation [registrazioneUtenteDL] del port-type [GestioneUtentiDocumentLiteral] (AccordoServizio:ASMultiPortTypeValidazioneWSDL style:document use:literal)");
		this.erroriAttesiOpenSPCoopCore.add(errA);
		
		// Test N.2 con Validazione OpenSPCoop
		dataInizioTest = DateManager.getDate();
		invocaServizioContenutoApplicativoErrato("registrazioneDocumentLiteralConPochiParametriInput.xml",
				this.repositoryGestioneUtentiDocumentLiteralContenutiErrato,
				CostantiTestSuite.PORTA_DELEGATA_VALIDAZIONE_OPENSPCOOP_GENERICA+"/"+
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_VAL_OPENSPCOOP_GESTIONE_UTENTI_DL+"/"+
					CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_VALIDAZIONE_WSDL_OPENSPCOOP_AZIONE_REGISTRAZIONE_UTENTE_DL,
				CostantiPdD.OPENSPCOOP2,Utilities.toString(CodiceErroreIntegrazione.CODICE_418_VALIDAZIONE_WSDL_RICHIESTA_FALLITA),
				"Il contenuto applicativo del messaggio di richiesta non rispetta l'accordo di servizio (Wsdl erogatore) definito nel Registro dei Servizi",
				"registrazioneUtenteDL");
		dataFineTest = DateManager.getDate();
		err = new ErroreAttesoOpenSPCoopLogCore();
		err.setIntervalloInferiore(dataInizioTest);
		err.setIntervalloSuperiore(dataFineTest);
		err.setMsgErrore("Validazione WSDL (true) fallita: Messaggio con elementi non conformi alla definizione wsdl dell'Azione [registrazioneUtenteDL] del Servizio [GestioneUtentiDocumentLiteral] (AccordoServizio:ASMultiPortTypeValidazioneOpenSPCoop style:document use:literal)");
		this.erroriAttesiOpenSPCoopCore.add(err);
		
		errA = new ErroreAttesoOpenSPCoopLogCore();
		errA.setIntervalloInferiore(dataInizioTest);
		errA.setIntervalloSuperiore(dataFineTest);
		errA.setMsgErrore("Riscontrata non conformità rispetto all'interfaccia WSDL; Messaggio con elementi non conformi alla definizione wsdl dell'Azione [registrazioneUtenteDL] del Servizio [GestioneUtentiDocumentLiteral] (AccordoServizio:ASMultiPortTypeValidazioneOpenSPCoop style:document use:literal)");
		this.erroriAttesiOpenSPCoopCore.add(errA);
		
		// Test N.3 con Validazione WSDL
		dataInizioTest = DateManager.getDate();
		invocaServizioContenutoApplicativoErrato("registrazioneDocumentLiteralConAlcuniParametriInputNonAttesi.xml",
				this.repositoryGestioneUtentiDocumentLiteralContenutiErrato,
				CostantiTestSuite.PORTA_DELEGATA_VALIDAZIONE_WSDL_GENERICA+"/"+
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_VAL_WSDL_GESTIONE_UTENTI_DL+"/"+
					CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_VALIDAZIONE_WSDL_OPENSPCOOP_AZIONE_REGISTRAZIONE_UTENTE_DL,
				CostantiPdD.OPENSPCOOP2,Utilities.toString(CodiceErroreIntegrazione.CODICE_418_VALIDAZIONE_WSDL_RICHIESTA_FALLITA),
				"Il contenuto applicativo del messaggio di richiesta non rispetta l'accordo di servizio (Wsdl erogatore) definito nel Registro dei Servizi",
				"registrazioneUtenteDL");
		dataFineTest = DateManager.getDate();
		err = new ErroreAttesoOpenSPCoopLogCore();
		err.setIntervalloInferiore(dataInizioTest);
		err.setIntervalloSuperiore(dataFineTest);
		err.setMsgErrore("Validazione WSDL (true) fallita: Messaggio con elementi non conformi alla definizione wsdl dell'Operation [registrazioneUtenteDL] del port-type [GestioneUtentiDocumentLiteral] (AccordoServizio:ASMultiPortTypeValidazioneWSDL style:document use:literal)");
		this.erroriAttesiOpenSPCoopCore.add(err);
		
		errA = new ErroreAttesoOpenSPCoopLogCore();
		errA.setIntervalloInferiore(dataInizioTest);
		errA.setIntervalloSuperiore(dataFineTest);
		errA.setMsgErrore("Riscontrata non conformità rispetto all'interfaccia WSDL; Messaggio con elementi non conformi alla definizione wsdl dell'Operation [registrazioneUtenteDL] del port-type [GestioneUtentiDocumentLiteral] (AccordoServizio:ASMultiPortTypeValidazioneWSDL style:document use:literal):");
		this.erroriAttesiOpenSPCoopCore.add(errA);
		
		// Test N.3 con Validazione OpenSPCoop
		dataInizioTest = DateManager.getDate();
		invocaServizioContenutoApplicativoErrato("registrazioneDocumentLiteralConAlcuniParametriInputNonAttesi.xml",
				this.repositoryGestioneUtentiDocumentLiteralContenutiErrato,
				CostantiTestSuite.PORTA_DELEGATA_VALIDAZIONE_OPENSPCOOP_GENERICA+"/"+
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_VAL_OPENSPCOOP_GESTIONE_UTENTI_DL+"/"+
					CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_VALIDAZIONE_WSDL_OPENSPCOOP_AZIONE_REGISTRAZIONE_UTENTE_DL,
				CostantiPdD.OPENSPCOOP2,Utilities.toString(CodiceErroreIntegrazione.CODICE_418_VALIDAZIONE_WSDL_RICHIESTA_FALLITA),
				"Il contenuto applicativo del messaggio di richiesta non rispetta l'accordo di servizio (Wsdl erogatore) definito nel Registro dei Servizi",
				"registrazioneUtenteDL");
		dataFineTest = DateManager.getDate();
		err = new ErroreAttesoOpenSPCoopLogCore();
		err.setIntervalloInferiore(dataInizioTest);
		err.setIntervalloSuperiore(dataFineTest);
		err.setMsgErrore("Validazione WSDL (true) fallita: Messaggio con elementi non conformi alla definizione wsdl dell'Azione [registrazioneUtenteDL] del Servizio [GestioneUtentiDocumentLiteral] (AccordoServizio:ASMultiPortTypeValidazioneOpenSPCoop style:document use:literal)");
		this.erroriAttesiOpenSPCoopCore.add(err);
		
		errA = new ErroreAttesoOpenSPCoopLogCore();
		errA.setIntervalloInferiore(dataInizioTest);
		errA.setIntervalloSuperiore(dataFineTest);
		errA.setMsgErrore("Riscontrata non conformità rispetto all'interfaccia WSDL; Messaggio con elementi non conformi alla definizione wsdl dell'Azione [registrazioneUtenteDL] del Servizio [GestioneUtentiDocumentLiteral] (AccordoServizio:ASMultiPortTypeValidazioneOpenSPCoop style:document use:literal):");
		this.erroriAttesiOpenSPCoopCore.add(errA);
	}
	@DataProvider (name="gestioneUtentiDocumentLiteralContenutiErrato")
	public Object[][]testGestioneUtentiDocumentLiteralContenutiErrato()throws Exception{
		String [] ids = new String[1];
		Vector<String> idsV = new Vector<String>();
		String id = null;
		while( (id=this.repositoryGestioneUtentiDocumentLiteralContenutiErrato.getNext()) != null){
			idsV.add(id);
		}
		ids = idsV.toArray(ids);
		
		try{
			Thread.sleep(3000);
		}catch(Exception e){}
		
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),ids,false},	
				{DatabaseProperties.getDatabaseComponentErogatore(),ids,true}	
		};
	}
	@Test(groups={ValidazioneContenutiApplicativi.ID_GRUPPO,
			ValidazioneContenutiApplicativi.ID_GRUPPO+".GestioneUtentiDocumentLiteralContenutiErrato"},
			dataProvider="gestioneUtentiDocumentLiteralContenutiErrato",
			dependsOnMethods={"gestioneUtentiDocumentLiteralContenutoErrato"})
	public void testGestioneUtentiDocumentLiteralContenutiErrato(DatabaseComponent data,String [] ids,boolean checkServizioApplicativo) throws Exception{
		try{
			for(int i=0; i<ids.length; i++){
				
				String id = ids[i];
				
				Reporter.log("Controllo tracciamento richiesta non effettuata con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id)==false);
			}

		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/***
	 * Test 12 Invocazione servizio GestioneUtentiRPCLiteral
	 * Scopo del test: corretta validazione del contenuto applicativo 
	 *                 realizzato in stile rpc literal          
	 */
	
	@SuppressWarnings({ "deprecation" })
	private String getIDEgov(GestioneUtentiRPCLiteralServiceLocator locator)throws Exception{
		return this.getIDEgov(locator.getCall());
	}
	
	private void invocaServizioGestioneUtentiRPCLiteral(String portaDelegata,String servizio,String azione,String test,boolean registra) throws Exception{
		Reporter.log("["+test+"] Invocazione GestioneUtenti RPCLiteral  ["+Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore()+
				portaDelegata+"/"+
				servizio+"/"+
				azione+"]");
		
		GestioneUtentiRPCLiteralServiceLocator locator = new GestioneUtentiRPCLiteralServiceLocator();
		locator.setGestioneUtentiRPCLiteralEndpointAddress(
				Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore()+
				portaDelegata+"/"+
				servizio+"/"+
				azione);
		GestioneUtentiRPCLiteral port = locator.getGestioneUtentiRPCLiteral();
		
		try{
			if(registra){
				String esito = port.registrazioneUtenteRPCL("Mario Rossi", "viale Roma 26", new java.util.Date());
				Reporter.log("["+test+"] Invocazione GestioneUtenti DocumentLiteral (azione registrazioneUtenteWDL) esito invocazione: "+esito);
				Assert.assertTrue("OK".equals(esito));
			}else{
				String esito = port.eliminazioneUtenteRPCL("Mario Rossi");
				Reporter.log("["+test+"] Invocazione GestioneUtenti WrappedDocumentLiteral (azione eliminazioneUtenteWDL) esito invocazione: "+esito);
				Assert.assertTrue("OK".equals(esito));
			}
		}catch(Exception e){
			throw e;
		}finally{
			String idEGov = this.getIDEgov(locator);
			Reporter.log("["+test+"] Invocazione GestioneUtenti WrappedDocumentLiteral idEgov: "+idEGov);
			Assert.assertTrue(idEGov!=null);
			this.repositoryGestioneUtentiRPCLiteral.add(idEGov);
		}
	}
	
	
	Repository repositoryGestioneUtentiRPCLiteral=new Repository();
	@Test(groups={ValidazioneContenutiApplicativi.ID_GRUPPO,
			ValidazioneContenutiApplicativi.ID_GRUPPO+".GestioneUtentiRPCLiteral"},description="Test di tipo sincrono, Viene controllato se i body sono uguali e se gli attachment sono uguali")
	public void gestioneUtentiRPCLiteral() throws TestSuiteException, IOException, Exception{
		
		TestSuiteTransformer.sequentialForced = true;
		
		// Test contenuto corretto per Validazione WSDL (operazione registrazione)
		invocaServizioGestioneUtentiRPCLiteral(CostantiTestSuite.PORTA_DELEGATA_VALIDAZIONE_WSDL_GENERICA,
				CostantiTestSuite.SPCOOP_NOME_SERVIZIO_VAL_WSDL_GESTIONE_UTENTI_RPCL,
				CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_VALIDAZIONE_WSDL_OPENSPCOOP_AZIONE_REGISTRAZIONE_UTENTE_RPCL,
				"ValidazioneWSDL_OperationRegistrazione",true);
		
		// Test contenuto corretto per Validazione OpenSPCoop (operazione registrazione)
		invocaServizioGestioneUtentiRPCLiteral(CostantiTestSuite.PORTA_DELEGATA_VALIDAZIONE_OPENSPCOOP_GENERICA,
				CostantiTestSuite.SPCOOP_NOME_SERVIZIO_VAL_OPENSPCOOP_GESTIONE_UTENTI_RPCL,
				CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_VALIDAZIONE_WSDL_OPENSPCOOP_AZIONE_REGISTRAZIONE_UTENTE_RPCL,
				"ValidazioneOpenSPCoop_OperationRegistrazione",true);
		
		// Test contenuto corretto per Validazione WSDL (operazione eliminazione)
		invocaServizioGestioneUtentiRPCLiteral(CostantiTestSuite.PORTA_DELEGATA_VALIDAZIONE_WSDL_GENERICA,
				CostantiTestSuite.SPCOOP_NOME_SERVIZIO_VAL_WSDL_GESTIONE_UTENTI_RPCL,
				CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_VALIDAZIONE_WSDL_OPENSPCOOP_AZIONE_ELIMINAZIONE_UTENTE_RPCL,
				"ValidazioneWSDL_OperationEliminazione",false);
		
		// Test contenuto corretto per Validazione OpenSPCoop (operazione eliminazione)
		invocaServizioGestioneUtentiRPCLiteral(CostantiTestSuite.PORTA_DELEGATA_VALIDAZIONE_OPENSPCOOP_GENERICA,
				CostantiTestSuite.SPCOOP_NOME_SERVIZIO_VAL_OPENSPCOOP_GESTIONE_UTENTI_RPCL,
				CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_VALIDAZIONE_WSDL_OPENSPCOOP_AZIONE_ELIMINAZIONE_UTENTE_RPCL,
				"ValidazioneOpenSPCoop_OperationEliminazione",false);
	}
	@DataProvider (name="gestioneUtentiRPCLiteral")
	public Object[][]testGestioneUtentiRPCLiteral()throws Exception{
		String [] ids = new String[1];
		Vector<String> idsV = new Vector<String>();
		String id = null;
		while( (id=this.repositoryGestioneUtentiRPCLiteral.getNext()) != null){
			idsV.add(id);
		}
		ids = idsV.toArray(ids);
		
		try{
			Thread.sleep(3000);
		}catch(Exception e){}
		
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),ids,false},	
				{DatabaseProperties.getDatabaseComponentErogatore(),ids,true}	
		};
	}
	@Test(groups={ValidazioneContenutiApplicativi.ID_GRUPPO,
			ValidazioneContenutiApplicativi.ID_GRUPPO+".GestioneUtentiRPCLiteral"},
			dataProvider="gestioneUtentiRPCLiteral",
			dependsOnMethods={"gestioneUtentiRPCLiteral"})
	public void testGestioneUtentiRPCLiteral(DatabaseComponent data,String [] ids,boolean checkServizioApplicativo) throws Exception{
		try{
			for(int i=0; i<ids.length; i++){
				
				String tipoServizio = CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_VALIDAZIONE_WSDL_OPENSPCOOP;
				String servizio = null;
				String azione = null;
				
				if(i==0){
					// Test contenuto corretto per Validazione WSDL (operazione registrazione)
					servizio = CostantiTestSuite.SPCOOP_NOME_SERVIZIO_VAL_WSDL_GESTIONE_UTENTI_RPCL;
					azione = CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_VALIDAZIONE_WSDL_OPENSPCOOP_AZIONE_REGISTRAZIONE_UTENTE_RPCL;
				}
				else if(i==1){
					// Test contenuto corretto per Validazione OpenSPCoop (operazione registrazione)
					servizio = CostantiTestSuite.SPCOOP_NOME_SERVIZIO_VAL_OPENSPCOOP_GESTIONE_UTENTI_RPCL;
					azione = CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_VALIDAZIONE_WSDL_OPENSPCOOP_AZIONE_REGISTRAZIONE_UTENTE_RPCL;
				}
				else if(i==2){
					// Test contenuto corretto per Validazione WSDL (operazione eliminazione)
					servizio = CostantiTestSuite.SPCOOP_NOME_SERVIZIO_VAL_WSDL_GESTIONE_UTENTI_RPCL;
					azione = CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_VALIDAZIONE_WSDL_OPENSPCOOP_AZIONE_ELIMINAZIONE_UTENTE_RPCL;
				}
				else if(i==3){
					// Test contenuto corretto per Validazione OpenSPCoop (operazione eliminazione)
					servizio = CostantiTestSuite.SPCOOP_NOME_SERVIZIO_VAL_OPENSPCOOP_GESTIONE_UTENTI_RPCL;
					azione = CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_VALIDAZIONE_WSDL_OPENSPCOOP_AZIONE_ELIMINAZIONE_UTENTE_RPCL;
				}
								
				
				String id = ids[i];
				
				this.collaborazioneSPCoopBase.testSincrono(data, id, tipoServizio, servizio, azione, checkServizioApplicativo, null);
			}

		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/***
	 * Test 12 Invocazione servizio GestioneUtentiRPCEncoded
	 * Scopo del test: corretta validazione del contenuto applicativo 
	 *                 realizzato in stile rpc Encoded          
	 */
	
	@SuppressWarnings({ "deprecation" })
	private String getIDEgov(GestioneUtentiRPCEncodedServiceLocator locator)throws Exception{
		return this.getIDEgov(locator.getCall());
	}
	
	private void invocaServizioGestioneUtentiRPCEncoded(String portaDelegata,String servizio,String azione,String test,boolean registra) throws Exception{
		Reporter.log("["+test+"] Invocazione GestioneUtenti RPCEncoded  ["+Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore()+
				portaDelegata+"/"+
				servizio+"/"+
				azione+"]");
		
		GestioneUtentiRPCEncodedServiceLocator locator = new GestioneUtentiRPCEncodedServiceLocator();
		locator.setGestioneUtentiRPCEncodedEndpointAddress(
				Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore()+
				portaDelegata+"/"+
				servizio+"/"+
				azione);
		GestioneUtentiRPCEncoded port = locator.getGestioneUtentiRPCEncoded();
		
		try{
			if(registra){
				String esito = port.registrazioneUtenteRPCE("Mario Rossi", "viale Roma 26", new java.util.Date());
				Reporter.log("["+test+"] Invocazione GestioneUtenti DocumentEncoded (azione registrazioneUtenteWDL) esito invocazione: "+esito);
				Assert.assertTrue("OK".equals(esito));
			}else{
				String esito = port.eliminazioneUtenteRPCE("Mario Rossi");
				Reporter.log("["+test+"] Invocazione GestioneUtenti WrappedDocumentEncoded (azione eliminazioneUtenteWDL) esito invocazione: "+esito);
				Assert.assertTrue("OK".equals(esito));
			}
		}catch(Exception e){
			throw e;
		}finally{
			String idEGov = this.getIDEgov(locator);
			Reporter.log("["+test+"] Invocazione GestioneUtenti WrappedDocumentEncoded idEgov: "+idEGov);
			Assert.assertTrue(idEGov!=null);
			this.repositoryGestioneUtentiRPCEncoded.add(idEGov);
		}
	}
	
	
	Repository repositoryGestioneUtentiRPCEncoded=new Repository();
	@Test(groups={ValidazioneContenutiApplicativi.ID_GRUPPO,
			ValidazioneContenutiApplicativi.ID_GRUPPO+".GestioneUtentiRPCEncoded"},description="Test di tipo sincrono, Viene controllato se i body sono uguali e se gli attachment sono uguali")
	public void gestioneUtentiRPCEncoded() throws TestSuiteException, IOException, Exception{
		
		TestSuiteTransformer.sequentialForced = true;
		
		// Test contenuto corretto per Validazione WSDL (operazione registrazione)
		invocaServizioGestioneUtentiRPCEncoded(CostantiTestSuite.PORTA_DELEGATA_VALIDAZIONE_WSDL_GENERICA,
				CostantiTestSuite.SPCOOP_NOME_SERVIZIO_VAL_WSDL_GESTIONE_UTENTI_RPCE,
				CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_VALIDAZIONE_WSDL_OPENSPCOOP_AZIONE_REGISTRAZIONE_UTENTE_RPCE,
				"ValidazioneWSDL_OperationRegistrazione",true);
		
		// Test contenuto corretto per Validazione OpenSPCoop (operazione registrazione)
		invocaServizioGestioneUtentiRPCEncoded(CostantiTestSuite.PORTA_DELEGATA_VALIDAZIONE_OPENSPCOOP_GENERICA,
				CostantiTestSuite.SPCOOP_NOME_SERVIZIO_VAL_OPENSPCOOP_GESTIONE_UTENTI_RPCE,
				CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_VALIDAZIONE_WSDL_OPENSPCOOP_AZIONE_REGISTRAZIONE_UTENTE_RPCE,
				"ValidazioneOpenSPCoop_OperationRegistrazione",true);
		
		// Test contenuto corretto per Validazione WSDL (operazione eliminazione)
		invocaServizioGestioneUtentiRPCEncoded(CostantiTestSuite.PORTA_DELEGATA_VALIDAZIONE_WSDL_GENERICA,
				CostantiTestSuite.SPCOOP_NOME_SERVIZIO_VAL_WSDL_GESTIONE_UTENTI_RPCE,
				CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_VALIDAZIONE_WSDL_OPENSPCOOP_AZIONE_ELIMINAZIONE_UTENTE_RPCE,
				"ValidazioneWSDL_OperationEliminazione",false);
		
		// Test contenuto corretto per Validazione OpenSPCoop (operazione eliminazione)
		invocaServizioGestioneUtentiRPCEncoded(CostantiTestSuite.PORTA_DELEGATA_VALIDAZIONE_OPENSPCOOP_GENERICA,
				CostantiTestSuite.SPCOOP_NOME_SERVIZIO_VAL_OPENSPCOOP_GESTIONE_UTENTI_RPCE,
				CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_VALIDAZIONE_WSDL_OPENSPCOOP_AZIONE_ELIMINAZIONE_UTENTE_RPCE,
				"ValidazioneOpenSPCoop_OperationEliminazione",false);
	}
	@DataProvider (name="gestioneUtentiRPCEncoded")
	public Object[][]testGestioneUtentiRPCEncoded()throws Exception{
		String [] ids = new String[1];
		Vector<String> idsV = new Vector<String>();
		String id = null;
		while( (id=this.repositoryGestioneUtentiRPCEncoded.getNext()) != null){
			idsV.add(id);
		}
		ids = idsV.toArray(ids);
		
		try{
			Thread.sleep(3000);
		}catch(Exception e){}
		
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),ids,false},	
				{DatabaseProperties.getDatabaseComponentErogatore(),ids,true}	
		};
	}
	@Test(groups={ValidazioneContenutiApplicativi.ID_GRUPPO,
			ValidazioneContenutiApplicativi.ID_GRUPPO+".GestioneUtentiRPCEncoded"},
			dataProvider="gestioneUtentiRPCEncoded",
			dependsOnMethods={"gestioneUtentiRPCEncoded"})
	public void testGestioneUtentiRPCEncoded(DatabaseComponent data,String [] ids,boolean checkServizioApplicativo) throws Exception{
		try{
			for(int i=0; i<ids.length; i++){
				
				String tipoServizio = CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_VALIDAZIONE_WSDL_OPENSPCOOP;
				String servizio = null;
				String azione = null;
				
				if(i==0){
					// Test contenuto corretto per Validazione WSDL (operazione registrazione)
					servizio = CostantiTestSuite.SPCOOP_NOME_SERVIZIO_VAL_WSDL_GESTIONE_UTENTI_RPCE;
					azione = CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_VALIDAZIONE_WSDL_OPENSPCOOP_AZIONE_REGISTRAZIONE_UTENTE_RPCE;
				}
				else if(i==1){
					// Test contenuto corretto per Validazione OpenSPCoop (operazione registrazione)
					servizio = CostantiTestSuite.SPCOOP_NOME_SERVIZIO_VAL_OPENSPCOOP_GESTIONE_UTENTI_RPCE;
					azione = CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_VALIDAZIONE_WSDL_OPENSPCOOP_AZIONE_REGISTRAZIONE_UTENTE_RPCE;
				}
				else if(i==2){
					// Test contenuto corretto per Validazione WSDL (operazione eliminazione)
					servizio = CostantiTestSuite.SPCOOP_NOME_SERVIZIO_VAL_WSDL_GESTIONE_UTENTI_RPCE;
					azione = CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_VALIDAZIONE_WSDL_OPENSPCOOP_AZIONE_ELIMINAZIONE_UTENTE_RPCE;
				}
				else if(i==3){
					// Test contenuto corretto per Validazione OpenSPCoop (operazione eliminazione)
					servizio = CostantiTestSuite.SPCOOP_NOME_SERVIZIO_VAL_OPENSPCOOP_GESTIONE_UTENTI_RPCE;
					azione = CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_VALIDAZIONE_WSDL_OPENSPCOOP_AZIONE_ELIMINAZIONE_UTENTE_RPCE;
				}
								
				
				String id = ids[i];
				
				this.collaborazioneSPCoopBase.testSincrono(data, id, tipoServizio, servizio, azione, checkServizioApplicativo, null);
			}

		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	/***
	 * Test 14 Invocazione servizio GestioneUtentiOverloadedOperations
	 * Scopo del test: corretta validazione del contenuto applicativo 
	 *                 realizzato in stile rpc encoded
	 *                 Le operation comprendono tre operazioni con lo stesso nome
	 *                 e argomento diverso      
	 */
	
	@SuppressWarnings({ "deprecation" })
	private String getIDEgov(GestioneUtentiOverloadedOperationsServiceLocator locator)throws Exception{
		return this.getIDEgov(locator.getCall());
	}
	
	private void invocaServizioGestioneUtentiOverloadedOperations(String portaDelegata,String servizio,String azione,String test,int tipoTest) throws Exception{
		Reporter.log("["+test+"] Invocazione GestioneUtentiOverloadedOperations  ["+Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore()+
				portaDelegata+"/"+
				servizio+"/"+
				azione+"]");
		
		GestioneUtentiOverloadedOperationsServiceLocator locator = new GestioneUtentiOverloadedOperationsServiceLocator();
		locator.setGestioneUtentiOverloadedOperationsEndpointAddress(
				Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore()+
				portaDelegata+"/"+
				servizio+"/"+
				azione);
		GestioneUtentiOverloadedOperations port = locator.getGestioneUtentiOverloadedOperations();
		
		try{
			if(tipoTest==1){
				String esito = port.registrazioneUtenteOverloadedOperations("Mario Rossi", "viale Roma 26", new java.util.Date());
				Reporter.log("["+test+"] Invocazione GestioneUtenti OverloadedOperations Signature1 esito invocazione: "+esito);
				Assert.assertTrue("OK".equals(esito));
			}
			else if(tipoTest==2){
				String esito = port.registrazioneUtenteOverloadedOperations("Mario Rossi", "viale Roma 26");
				Reporter.log("["+test+"] Invocazione GestioneUtenti OverloadedOperations Signature1 esito invocazione: "+esito);
				Assert.assertTrue("OK".equals(esito));
			}
			else if(tipoTest==3){
				String esito = port.registrazioneUtenteOverloadedOperations("Mario Rossi",new java.util.Date());
				Reporter.log("["+test+"] Invocazione GestioneUtenti OverloadedOperations Signature1 esito invocazione: "+esito);
				Assert.assertTrue("OK".equals(esito));
			}
		}catch(Exception e){
			throw e;
		}finally{
			String idEGov = this.getIDEgov(locator);
			Reporter.log("["+test+"] Invocazione GestioneUtentiOverloadedOperations idEgov: "+idEGov);
			Assert.assertTrue(idEGov!=null);
			this.repositoryGestioneUtentiOverloadedOperations.add(idEGov);
		}
	}
	
	
	Repository repositoryGestioneUtentiOverloadedOperations=new Repository();
	@Test(groups={ValidazioneContenutiApplicativi.ID_GRUPPO,
			ValidazioneContenutiApplicativi.ID_GRUPPO+".GestioneUtentiOverloadedOperations"},description="Test di tipo sincrono, Viene controllato se i body sono uguali e se gli attachment sono uguali")
	public void gestioneUtentiOverloadedOperations() throws TestSuiteException, IOException, Exception{
		
		TestSuiteTransformer.sequentialForced = true;
		
		// Test contenuto corretto per Validazione WSDL Signature 1
		invocaServizioGestioneUtentiOverloadedOperations(CostantiTestSuite.PORTA_DELEGATA_VALIDAZIONE_WSDL_GENERICA,
				CostantiTestSuite.SPCOOP_NOME_SERVIZIO_VAL_WSDL_GESTIONE_UTENTI_OVERLOADED_OPERATIONS,
				CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_VALIDAZIONE_WSDL_OPENSPCOOP_AZIONE_REGISTRAZIONE_UTENTE_OVERLOADED,
				"ValidazioneWSDL_OperationRegistrazione",1);
		
		// Test contenuto corretto per Validazione OpenSPCoop Signature 1
		invocaServizioGestioneUtentiOverloadedOperations(CostantiTestSuite.PORTA_DELEGATA_VALIDAZIONE_OPENSPCOOP_GENERICA,
				CostantiTestSuite.SPCOOP_NOME_SERVIZIO_VAL_OPENSPCOOP_GESTIONE_UTENTI_OVERLOADED_OPERATIONS,
				CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_VALIDAZIONE_WSDL_OPENSPCOOP_AZIONE_REGISTRAZIONE_UTENTE_OVERLOADED,
				"ValidazioneWSDL_OperationRegistrazione",1);
		
		// Test contenuto corretto per Validazione WSDL Signature 2
		invocaServizioGestioneUtentiOverloadedOperations(CostantiTestSuite.PORTA_DELEGATA_VALIDAZIONE_WSDL_GENERICA,
				CostantiTestSuite.SPCOOP_NOME_SERVIZIO_VAL_WSDL_GESTIONE_UTENTI_OVERLOADED_OPERATIONS,
				CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_VALIDAZIONE_WSDL_OPENSPCOOP_AZIONE_REGISTRAZIONE_UTENTE_OVERLOADED,
				"ValidazioneWSDL_OperationRegistrazione",2);
		
		// Test contenuto corretto per Validazione OpenSPCoop Signature 2
		/*
		 * OVERLOADE IN QUESTA MODALITA NON E' PERMESSA
		invocaServizioGestioneUtentiOverloadedOperations(CostantiTestSuite.PORTA_DELEGATA_VALIDAZIONE_OPENSPCOOP_GENERICA,
				CostantiTestSuite.SPCOOP_NOME_SERVIZIO_VAL_OPENSPCOOP_GESTIONE_UTENTI_OVERLOADED_OPERATIONS,
				CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_VALIDAZIONE_WSDL_OPENSPCOOP_AZIONE_REGISTRAZIONE_UTENTE_OVERLOADED,
				"ValidazioneWSDL_OperationRegistrazione",2);
		*/
		
		// Test contenuto corretto per Validazione WSDL Signature 3
		invocaServizioGestioneUtentiOverloadedOperations(CostantiTestSuite.PORTA_DELEGATA_VALIDAZIONE_WSDL_GENERICA,
				CostantiTestSuite.SPCOOP_NOME_SERVIZIO_VAL_WSDL_GESTIONE_UTENTI_OVERLOADED_OPERATIONS,
				CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_VALIDAZIONE_WSDL_OPENSPCOOP_AZIONE_REGISTRAZIONE_UTENTE_OVERLOADED,
				"ValidazioneWSDL_OperationRegistrazione",3);
		
		// Test contenuto corretto per Validazione OpenSPCoop Signature 3
		/*
		 * OVERLOADE IN QUESTA MODALITA NON E' PERMESSA
		invocaServizioGestioneUtentiOverloadedOperations(CostantiTestSuite.PORTA_DELEGATA_VALIDAZIONE_OPENSPCOOP_GENERICA,
				CostantiTestSuite.SPCOOP_NOME_SERVIZIO_VAL_OPENSPCOOP_GESTIONE_UTENTI_OVERLOADED_OPERATIONS,
				CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_VALIDAZIONE_WSDL_OPENSPCOOP_AZIONE_REGISTRAZIONE_UTENTE_OVERLOADED,
				"ValidazioneWSDL_OperationRegistrazione",3);
				*/
	}
	@DataProvider (name="gestioneUtentiOverloadedOperations")
	public Object[][]testGestioneUtentiOverloadedOperations()throws Exception{
		String [] ids = new String[1];
		Vector<String> idsV = new Vector<String>();
		String id = null;
		while( (id=this.repositoryGestioneUtentiOverloadedOperations.getNext()) != null){
			idsV.add(id);
		}
		ids = idsV.toArray(ids);
		
		try{
			Thread.sleep(3000);
		}catch(Exception e){}
		
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),ids,false},	
				{DatabaseProperties.getDatabaseComponentErogatore(),ids,true}	
		};
	}
	@Test(groups={ValidazioneContenutiApplicativi.ID_GRUPPO,
			ValidazioneContenutiApplicativi.ID_GRUPPO+".GestioneUtentiOverloadedOperations"},
			dataProvider="gestioneUtentiOverloadedOperations",
			dependsOnMethods={"gestioneUtentiOverloadedOperations"})
	public void testGestioneUtentiOverloadedOperations(DatabaseComponent data,String [] ids,boolean checkServizioApplicativo) throws Exception{
		try{
			for(int i=0; i<ids.length; i++){
				
				String tipoServizio = CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_VALIDAZIONE_WSDL_OPENSPCOOP;
				String servizio = null;
				String azione = null;
				
				if(i==0 || i==2 || i==3){
					// Test contenuto corretto per Validazione WSDL (operazione registrazione)
					servizio = CostantiTestSuite.SPCOOP_NOME_SERVIZIO_VAL_WSDL_GESTIONE_UTENTI_OVERLOADED_OPERATIONS;
					azione = CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_VALIDAZIONE_WSDL_OPENSPCOOP_AZIONE_REGISTRAZIONE_UTENTE_OVERLOADED;
				}
				else {
					// Test contenuto corretto per Validazione OpenSPCoop (operazione registrazione)
					servizio = CostantiTestSuite.SPCOOP_NOME_SERVIZIO_VAL_OPENSPCOOP_GESTIONE_UTENTI_OVERLOADED_OPERATIONS;
					azione = CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_VALIDAZIONE_WSDL_OPENSPCOOP_AZIONE_REGISTRAZIONE_UTENTE_OVERLOADED;
				}
					
				
				String id = ids[i];
				
				this.collaborazioneSPCoopBase.testSincrono(data, id, tipoServizio, servizio, azione, checkServizioApplicativo, null);
			}

		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	
	
	
	
	
	
	
	
	
	
	/***
	 * Test 15 Invocazione servizio GestioneUtentiStileIbrido
	 * Scopo del test: Ridefinizione dello style nelle operation:
	 *                 - registrazioneUtenteWDL realizzato in stile wrapped document literal
	 *                 - registrazioneUtenteRPCL realizzato in stile rpc literal
	 *                 - registrazioneUtenteRPCE realizzato in stile rpc encoded
	 */
	
	@SuppressWarnings({ "deprecation" })
	private String getIDEgov(GestioneUtentiStileIbridoServiceLocator locator)throws Exception{
		return this.getIDEgov(locator.getCall());
	}
	
	private void invocaServizioGestioneUtentiStileIbrido(String portaDelegata,String servizio,String azione,String test,String tipoTest) throws Exception{
		Reporter.log("["+test+"] Invocazione GestioneUtentiOverloadedOperations  ["+Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore()+
				portaDelegata+"/"+
				servizio+"/"+
				azione+"]");
		
		GestioneUtentiStileIbridoServiceLocator locator = new GestioneUtentiStileIbridoServiceLocator();
		locator.setGestioneUtentiStileIbridoEndpointAddress(
				Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore()+
				portaDelegata+"/"+
				servizio+"/"+
				azione);
		GestioneUtentiStileIbrido port = locator.getGestioneUtentiStileIbrido();
		
		try{
			if(tipoTest.equals("WDL")){
				RegistrazioneUtenteWDLRequestType wrappedRequest = new RegistrazioneUtenteWDLRequestType();
				wrappedRequest.setNominativo("Mario Rossi");
				wrappedRequest.setIndirizzo("viale Roma 26");
				wrappedRequest.setOraRegistrazione(new java.util.Date());
				
				RegistrazioneUtenteWDLResponseType esito = port.registrazioneUtenteWDL(wrappedRequest);
				Reporter.log("["+test+"] Invocazione GestioneUtenti GestioneUtentiStileIbrido WDL esito invocazione: "+esito.getEsito());
				Assert.assertTrue("OK".equals(esito.getEsito()));
			}
			else if(tipoTest.equals("RPCL")){
				String esito = port.registrazioneUtenteRPCL("Mario Rossi", "viale Roma 26", new java.util.Date());
				Reporter.log("["+test+"] Invocazione GestioneUtenti GestioneUtentiStileIbrido RPCL esito invocazione: "+esito);
				Assert.assertTrue("OK".equals(esito));
			}
			else if(tipoTest.equals("RPCE")){
				String esito = port.registrazioneUtenteRPCE("Mario Rossi", "viale Roma 26", new java.util.Date());
				Reporter.log("["+test+"] Invocazione GestioneUtenti GestioneUtentiStileIbrido RPCE esito invocazione: "+esito);
				Assert.assertTrue("OK".equals(esito));
			}
		}catch (AxisFault error) {
			if(tipoTest.equals("WDL")){
				throw error;
			}else{
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				Reporter.log("Controllo fault code [Server.userException]");
				Assert.assertTrue("Server.userException".equals(error.getFaultCode().getLocalPart().trim()));
				Reporter.log("Controllo fault string [SimpleDeserializer encountered a child element, which is NOT expected, in something it was trying to deserialize] con ["+error.getFaultString()+"]");
				Assert.assertTrue(error.getFaultString().indexOf("SimpleDeserializer encountered a child element, which is NOT expected, in something it was trying to deserialize")>=0);
			}
		}catch(Exception e){
			throw e;
		}finally{
			String idEGov = this.getIDEgov(locator);
			Reporter.log("["+test+"] Invocazione GestioneUtentiOverloadedOperations idEgov: "+idEGov);
			Assert.assertTrue(idEGov!=null);
			this.repositoryGestioneUtentiStileIbrido.add(idEGov);
		}
	}
	
	
	Repository repositoryGestioneUtentiStileIbrido=new Repository();
	@Test(groups={ValidazioneContenutiApplicativi.ID_GRUPPO,
			ValidazioneContenutiApplicativi.ID_GRUPPO+".GestioneUtentiStileIbrido"},description="Test di tipo sincrono, Viene controllato se i body sono uguali e se gli attachment sono uguali")
	public void gestioneUtentiStileIbrido() throws TestSuiteException, IOException, Exception{
		
		TestSuiteTransformer.sequentialForced = true;
		
		// Test contenuto corretto per Validazione WSDL WDL
		invocaServizioGestioneUtentiStileIbrido(CostantiTestSuite.PORTA_DELEGATA_VALIDAZIONE_WSDL_GENERICA,
				CostantiTestSuite.SPCOOP_NOME_SERVIZIO_VAL_WSDL_GESTIONE_UTENTI_STILE_IBRIDO,
				CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_VALIDAZIONE_WSDL_OPENSPCOOP_AZIONE_REGISTRAZIONE_UTENTE_WDL,
				"ValidazioneWSDL_OperationRegistrazione","WDL");
		
		// Test contenuto corretto per Validazione OpenSPCoop WDL
		invocaServizioGestioneUtentiStileIbrido(CostantiTestSuite.PORTA_DELEGATA_VALIDAZIONE_OPENSPCOOP_GENERICA,
				CostantiTestSuite.SPCOOP_NOME_SERVIZIO_VAL_OPENSPCOOP_GESTIONE_UTENTI_STILE_IBRIDO,
				CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_VALIDAZIONE_WSDL_OPENSPCOOP_AZIONE_REGISTRAZIONE_UTENTE_WDL,
				"ValidazioneWSDL_OperationRegistrazione","WDL");
		
		/* Manda in crash il server, e dopodiche saltano gli id egov raccolti dall'applicazione finale tramite handler axis.
		// Test contenuto corretto per Validazione WSDL RPCL
		invocaServizioGestioneUtentiStileIbrido(CostantiTestSuite.PORTA_DELEGATA_VALIDAZIONE_WSDL_GENERICA,
				CostantiTestSuite.SPCOOP_NOME_SERVIZIO_VAL_WSDL_GESTIONE_UTENTI_STILE_IBRIDO,
				CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_VALIDAZIONE_WSDL_OPENSPCOOP_AZIONE_REGISTRAZIONE_UTENTE_RPCL,
				"ValidazioneWSDL_OperationRegistrazione","RPCL");
		
		// Test contenuto corretto per Validazione OpenSPCoop RPCL
		invocaServizioGestioneUtentiStileIbrido(CostantiTestSuite.PORTA_DELEGATA_VALIDAZIONE_OPENSPCOOP_GENERICA,
				CostantiTestSuite.SPCOOP_NOME_SERVIZIO_VAL_OPENSPCOOP_GESTIONE_UTENTI_STILE_IBRIDO,
				CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_VALIDAZIONE_WSDL_OPENSPCOOP_AZIONE_REGISTRAZIONE_UTENTE_RPCL,
				"ValidazioneWSDL_OperationRegistrazione","RPCL");
		
		// Test contenuto corretto per Validazione WSDL RPCE
		invocaServizioGestioneUtentiStileIbrido(CostantiTestSuite.PORTA_DELEGATA_VALIDAZIONE_WSDL_GENERICA,
				CostantiTestSuite.SPCOOP_NOME_SERVIZIO_VAL_WSDL_GESTIONE_UTENTI_STILE_IBRIDO,
				CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_VALIDAZIONE_WSDL_OPENSPCOOP_AZIONE_REGISTRAZIONE_UTENTE_RPCE,
				"ValidazioneWSDL_OperationRegistrazione","RPCE");
		
		// Test contenuto corretto per Validazione OpenSPCoop RPCE
		invocaServizioGestioneUtentiStileIbrido(CostantiTestSuite.PORTA_DELEGATA_VALIDAZIONE_OPENSPCOOP_GENERICA,
				CostantiTestSuite.SPCOOP_NOME_SERVIZIO_VAL_OPENSPCOOP_GESTIONE_UTENTI_STILE_IBRIDO,
				CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_VALIDAZIONE_WSDL_OPENSPCOOP_AZIONE_REGISTRAZIONE_UTENTE_RPCE,
				"ValidazioneWSDL_OperationRegistrazione","RPCE");
				*/
	}
	@DataProvider (name="gestioneUtentiStileIbrido")
	public Object[][]testGestioneUtentiStileIbrido()throws Exception{
		String [] ids = new String[1];
		Vector<String> idsV = new Vector<String>();
		String id = null;
		while( (id=this.repositoryGestioneUtentiStileIbrido.getNext()) != null){
			idsV.add(id);
		}
		ids = idsV.toArray(ids);
		
		try{
			Thread.sleep(3000);
		}catch(Exception e){}
		
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),ids,false},	
				{DatabaseProperties.getDatabaseComponentErogatore(),ids,true}	
		};
	}
	@Test(groups={ValidazioneContenutiApplicativi.ID_GRUPPO,
			ValidazioneContenutiApplicativi.ID_GRUPPO+".GestioneUtentiStileIbrido"},
			dataProvider="gestioneUtentiStileIbrido",
			dependsOnMethods={"gestioneUtentiStileIbrido"})
	public void testGestioneUtentiStileIbrido(DatabaseComponent data,String [] ids,boolean checkServizioApplicativo) throws Exception{
		try{
			for(int i=0; i<ids.length; i++){
				
				String tipoServizio = CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_VALIDAZIONE_WSDL_OPENSPCOOP;
				String servizio = null;
				String azione = null;
				boolean checkServiziApplicativi = checkServizioApplicativo;
				
				if(i==0 || i==2 || i==4){
					// Test contenuto corretto per Validazione WSDL (operazione registrazione)
					servizio = CostantiTestSuite.SPCOOP_NOME_SERVIZIO_VAL_WSDL_GESTIONE_UTENTI_STILE_IBRIDO;
				}
				else {
					// Test contenuto corretto per Validazione OpenSPCoop (operazione registrazione)
					servizio = CostantiTestSuite.SPCOOP_NOME_SERVIZIO_VAL_OPENSPCOOP_GESTIONE_UTENTI_STILE_IBRIDO;
				}
				
				if(i==0 || i==1 ){
					azione = CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_VALIDAZIONE_WSDL_OPENSPCOOP_AZIONE_REGISTRAZIONE_UTENTE_WDL;
				}
				else if(i==2 || i==3 ){
					azione = CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_VALIDAZIONE_WSDL_OPENSPCOOP_AZIONE_REGISTRAZIONE_UTENTE_RPCL;
				}
				else{
					azione = CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_VALIDAZIONE_WSDL_OPENSPCOOP_AZIONE_REGISTRAZIONE_UTENTE_RPCE;
				}				
				
				if(checkServizioApplicativo){
					if(i==0 || i==1 ){
						checkServiziApplicativi = true;
					}else{
						checkServiziApplicativi = false;
					}
				}
				
				String id = ids[i];
				
				this.collaborazioneSPCoopBase.testSincrono(data, id, tipoServizio, servizio, azione, checkServiziApplicativi, null);
			}

		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
}
