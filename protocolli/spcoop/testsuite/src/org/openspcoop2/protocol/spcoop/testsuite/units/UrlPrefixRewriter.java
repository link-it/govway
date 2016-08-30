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



package org.openspcoop2.protocol.spcoop.testsuite.units;

import java.io.IOException;
import java.util.Date;
import java.util.Vector;

import javax.xml.soap.SOAPException;

import org.apache.axis.AxisFault;
import org.apache.axis.Message;
import org.openspcoop2.message.SOAPVersion;
import org.openspcoop2.pdd.mdb.InoltroBuste;
import org.openspcoop2.protocol.sdk.constants.CodiceErroreIntegrazione;
import org.openspcoop2.protocol.sdk.constants.CostantiProtocollo;
import org.openspcoop2.protocol.sdk.constants.Inoltro;
import org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione;
import org.openspcoop2.protocol.spcoop.constants.SPCoopCostanti;
import org.openspcoop2.protocol.spcoop.testsuite.core.CooperazioneSPCoopBase;
import org.openspcoop2.protocol.spcoop.testsuite.core.CostantiErroriIntegrazione;
import org.openspcoop2.protocol.spcoop.testsuite.core.CostantiTestSuite;
import org.openspcoop2.protocol.spcoop.testsuite.core.DatabaseProperties;
import org.openspcoop2.protocol.spcoop.testsuite.core.SPCoopTestsuiteLogger;
import org.openspcoop2.protocol.spcoop.testsuite.core.Utilities;
import org.openspcoop2.protocol.spcoop.testsuite.core.UtilitiesEGov;
import org.openspcoop2.testsuite.clients.ClientHttpGenerico;
import org.openspcoop2.testsuite.core.ErroreAttesoOpenSPCoopLogCore;
import org.openspcoop2.testsuite.core.TestSuiteException;
import org.openspcoop2.testsuite.core.Repository;
import org.openspcoop2.testsuite.db.DatabaseComponent;
import org.openspcoop2.testsuite.db.DatabaseMsgDiagnosticiComponent;
import org.openspcoop2.testsuite.db.DatiServizio;
import org.openspcoop2.testsuite.units.CooperazioneBase;
import org.openspcoop2.testsuite.units.CooperazioneBaseInformazioni;
import org.openspcoop2.utils.date.DateManager;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.AfterGroups;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Test sui profili di collaborazione implementati nella Porta di Dominio
 * 
 * @author Andi Rexha (rexha@openspcoop.org)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class UrlPrefixRewriter {

	/** Identificativo del gruppo */
	public static final String ID_GRUPPO = "UrlPrefixRewriter";

	
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
	
	
	

	

	
	/* ************************ TEST 1  http://urlPDNonEsistente e  http://urlPANonEsistente" **************************


	/***
	 * Test per pd-url-prefix-rewriter="http://urlPDNonEsistente"
	 */
	Date testPD1StartTime = null;
	Repository repository_testPD1=new Repository();
	@Test(groups={UrlPrefixRewriter.ID_GRUPPO,UrlPrefixRewriter.ID_GRUPPO+".PD_1"})
	public void testPD1() throws Exception{
		
		Date dataInizioTest = DateManager.getDate();
		
		DatabaseComponent dbComponentFruitore = null;
		DatabaseComponent dbComponentErogatore = null;

		this.testPD1StartTime = new Date();
		
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(new Repository());
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_TEST_URL_PREFIX_REWRITER_1);
			client.connectToSoapEngine();
			client.setMessageFromFile(Utilities.testSuiteProperties.getSoap11FileName(), false);
			client.setRispostaDaGestire(true);
			// AttesaTerminazioneMessaggi
			if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()){
				dbComponentFruitore = DatabaseProperties.getDatabaseComponentFruitore();
				dbComponentErogatore = DatabaseProperties.getDatabaseComponentErogatore();

				client.setAttesaTerminazioneMessaggi(true);
				client.setDbAttesaTerminazioneMessaggiFruitore(dbComponentFruitore);
				client.setDbAttesaTerminazioneMessaggiErogatore(dbComponentErogatore);
			}
			try {
				client.run();

				Reporter.log("Invocazione porta delegata con identificazione dinamica dei dati non ha causato errori.");
				throw new TestSuiteException("Invocazione porta delegata con identificazione dinamica dei dati non ha causato errori.");

			} catch (AxisFault error) {
				
				Assert.assertTrue(client.getCodiceStatoHTTP()==500);
				
				Utilities.verificaFaultIntegrazione(error, 
						CostantiTestSuite.SPCOOP_NOME_SOGGETTO_TEST_URL_PREFIX_REWRITER_1+"SPCoopIT",InoltroBuste.ID_MODULO, 
						Utilities.toString(CodiceErroreIntegrazione.CODICE_516_CONNETTORE_UTILIZZO_CON_ERRORE), 
						CostantiErroriIntegrazione.MSG_516_PDD_NON_DISPONIBILE.replace(CostantiProtocollo.KEYWORDPDD_NON_DISPONIBILE, CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_EROGATORE+CostantiTestSuite.SPCOOP_NOME_SOGGETTO_EROGATORE), 
						Utilities.CONTROLLO_DESCRIZIONE_TRAMITE_METODO_EQUALS);	
			}finally{
				dbComponentFruitore.close();
				dbComponentErogatore.close();
			}
		}catch(Exception e){
			throw e;
		}finally{
			dbComponentFruitore.close();
			dbComponentErogatore.close();
		}
		
		Date dataFineTest = DateManager.getDate();
		
		ErroreAttesoOpenSPCoopLogCore err = new ErroreAttesoOpenSPCoopLogCore();
		err.setIntervalloInferiore(dataInizioTest);
		err.setIntervalloSuperiore(dataFineTest);
		err.setMsgErrore("Errore avvenuto durante la consegna HTTP: unknown host 'urlPDNonEsistente");
		this.erroriAttesiOpenSPCoopCore.add(err);
	}
	@DataProvider (name="testPD1Provider")
	public Object[][]testPD1Provider()throws Exception{
		String id=this.repository_testPD1.getNext();
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),DatabaseProperties.getDatabaseComponentDiagnosticaFruitore(),id}	
		};
	}
	@Test(groups={UrlPrefixRewriter.ID_GRUPPO,UrlPrefixRewriter.ID_GRUPPO+".PD_1"},dataProvider="testPD1Provider",dependsOnMethods={"testPD1"})
	public void testPD1VerificaTracce(DatabaseComponent data,DatabaseMsgDiagnosticiComponent msgDiagComponent,String id) throws Exception{
		try{
			
			Reporter.log("Controllo tracciamento richiesta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id)==false);
			
			Reporter.log("Controllo esistenza diagnostico [http://urlPDNonEsistente]");
			Assert.assertTrue(msgDiagComponent.isTracedMessaggioWithLike(this.testPD1StartTime,"(location: http://urlPDNonEsistente/openspcoop2/spcoop/PA)"));
			Assert.assertTrue(msgDiagComponent.isTracedMessaggioWithLike(this.testPD1StartTime,"Errore avvenuto durante la consegna HTTP: unknown host 'urlPDNonEsistente"));
			
		}catch(Exception e){
			throw e;
		}finally{
			try{
				data.close();
			}catch(Exception e){}
			try{
				msgDiagComponent.close();
			}catch(Exception e){}
		}
	}
	
	

	
	
	
	
	
	/***
	 * Test per pa-url-prefix-rewriter="http://urlPANonEsistente"
	 */
	Date testPA1StartTime = null;
	Repository repository_testPA1=new Repository();
	@Test(groups={UrlPrefixRewriter.ID_GRUPPO,UrlPrefixRewriter.ID_GRUPPO+".PA_1"})
	public void testPA1()throws TestSuiteException,SOAPException, Exception{
		
		Date dataInizioTest = DateManager.getDate();
		
		this.testPA1StartTime = new Date();
		
		String egov=UtilitiesEGov.getIDEGov(CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE,
				CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE+CostantiTestSuite.SPCOOP_PORTA_DOMINIO);
		this.repository_testPA1.add(egov);
		String bustaSOAP=UtilitiesEGov.getBustaEGov(CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_FRUITORE,
				CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE,
				CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_TEST_URL_PREFIX_REWRITER_1,
				CostantiTestSuite.SPCOOP_NOME_SOGGETTO_TEST_URL_PREFIX_REWRITER_1,
				SPCoopCostanti.PROFILO_COLLABORAZIONE_SINCRONO,
				CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
				CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
				egov,
				false,
				SPCoopCostanti.PROFILO_TRASMISSIONE_SENZA_DUPLICATI,
				false,
				null);

		// costruzione busta
		java.io.ByteArrayInputStream bin =
			new java.io.ByteArrayInputStream(bustaSOAP.getBytes());
		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();

		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repository_testPA1);
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneBusteErogatore());
			client.connectToSoapEngine();
			client.setMessage(msg);
			client.setRispostaDaGestire(true);
			// AttesaTerminazioneMessaggi
			if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()){
				dbComponentErogatore = DatabaseProperties.getDatabaseComponentErogatore();

				client.setAttesaTerminazioneMessaggi(true);
				client.setDbAttesaTerminazioneMessaggiErogatore(dbComponentErogatore);
			}
			try {
				client.run();
				if(Utilities.testSuiteProperties.isNewConnectionForResponse()==false){
					Reporter.log("Invocazione PA.");
					throw new TestSuiteException("Invocazione PA, non ha causato errori.");
				}
			} catch (AxisFault error) {
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				Reporter.log("Controllo fault code [Server]");
				Assert.assertTrue("Server".equals(error.getFaultCode().getLocalPart()));
				Reporter.log("Controllo fault string ["+SPCoopCostanti.FAULT_STRING_PROCESSAMENTO_SPCOOP+"]");
				Assert.assertTrue(SPCoopCostanti.FAULT_STRING_PROCESSAMENTO_SPCOOP.equals(error.getFaultString()));
			}finally{
				dbComponentErogatore.close();
			}
		}catch(Exception e){
			throw e;
		}finally{
			dbComponentErogatore.close();
		}
		
		Date dataFineTest = DateManager.getDate();
		
		ErroreAttesoOpenSPCoopLogCore err = new ErroreAttesoOpenSPCoopLogCore();
		err.setIntervalloInferiore(dataInizioTest);
		err.setIntervalloSuperiore(dataFineTest);
		err.setMsgErrore("Errore avvenuto durante la consegna HTTP: unknown host 'urlPANonEsistente");
		this.erroriAttesiOpenSPCoopCore.add(err);
	}
	@DataProvider (name="testPA1Provider")
	public Object[][] testPA1Provider()throws Exception{
		String id=this.repository_testPA1.getNext();
		if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()==false){
			try {
				Thread.sleep(Utilities.testSuiteProperties.timeToSleep_verificaDatabase());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		// Attendo tempo perso per la nuova connessione
		//if(Utilities.testSuiteProperties.isNewConnectionForResponse()){
		try{
			Thread.sleep(3000);
		}catch(InterruptedException e){}
		//}	
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentErogatore(),DatabaseProperties.getDatabaseComponentDiagnosticaErogatore(),id}
		};
	}
	@Test(groups={UrlPrefixRewriter.ID_GRUPPO,UrlPrefixRewriter.ID_GRUPPO+".PA_1"},
			dataProvider="testPA1Provider",
			dependsOnMethods="testPA1")
			public void testPA1VerificaTracce(DatabaseComponent data,DatabaseMsgDiagnosticiComponent msgDiagComponent,String id) throws Exception{
		try{
			Reporter.log("Controllo tracciamento richiesta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
			Reporter.log("Controllo valore Mittente Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id, CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE , null));
			Reporter.log("Controllo valore Destinatario Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedDestinatario(id, CostantiTestSuite.SPCOOP_SOGGETTO_TEST_URL_PREFIX_REWRITER_1, null));
			Reporter.log("Controllo valore OraRegistrazione con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedOraRegistrazione(id));

			Reporter.log("Controllo valore Servizio Busta con id: " +id);
			DatiServizio datiServizio = new DatiServizio(CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO, CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO, CostantiTestSuite.SPCOOP_VERSIONE_SERVIZIO_DEFAULT);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedServizio(id, datiServizio));
			Reporter.log("Controllo valore Azione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedAzione(id, null));
			Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloDiCollaborazione(id, SPCoopCostanti.PROFILO_COLLABORAZIONE_SINCRONO,ProfiloDiCollaborazione.SINCRONO));
			Reporter.log("Controllo valore Profilo di Trasmissione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloTrasmissione(id, false,SPCoopCostanti.PROFILO_TRASMISSIONE_SENZA_DUPLICATI,Inoltro.SENZA_DUPLICATI));
			//Reporter.log("Controllo che la busta abbia generato eccezioni, id: " +id);
			//Assert.assertTrue(data.getVerificatoreTracciaRisposta().existsListaEccezioni(id)==true);
			Reporter.log("Controllo lista trasmissione con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedTrasmissione(id, 
					CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE, null ,
					CostantiTestSuite.SPCOOP_SOGGETTO_TEST_URL_PREFIX_REWRITER_1, null,
					true));
			Reporter.log("Numero messaggi arrivati al servizio applicativo: "+data.getVerificatoreTracciaRichiesta().isArrivedCount(id));
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isArrivedCount(id)==0);

			Reporter.log("----------------------------------------------------------");

			Reporter.log("Controllo tracciamento risposta con riferimento id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTraced(id));
			Reporter.log("Controllo valore Mittente Busta della risposta con riferimento id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedMittente(id, CostantiTestSuite.SPCOOP_SOGGETTO_TEST_URL_PREFIX_REWRITER_1, null));
			Reporter.log("Controllo valore Destinatario Busta della risposta con riferimento id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedDestinatario(id, CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE , null));
			Reporter.log("Controllo valore OraRegistrazione con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedOraRegistrazione(id));
	
			Reporter.log("Controllo valore Servizio Busta della risposta con riferimento id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedServizio(id, datiServizio));
			Reporter.log("Controllo valore Azione Busta della risposta con riferimento id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedAzione(id, null));
			Reporter.log("Controllo valore Profilo di Collaborazione Busta della risposta con riferimento id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedProfiloDiCollaborazione(id, SPCoopCostanti.PROFILO_COLLABORAZIONE_SINCRONO,ProfiloDiCollaborazione.SINCRONO));
			Reporter.log("Controllo valore Profilo di Trasmissione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedProfiloTrasmissione(id,false,SPCoopCostanti.PROFILO_TRASMISSIONE_SENZA_DUPLICATI,Inoltro.SENZA_DUPLICATI));
			//Reporter.log("Controllo che la busta abbia generato eccezioni, riferimento messaggio: " +id);
			//Assert.assertTrue(data.getVerificatoreTracciaRisposta().existsListaEccezioni(id)==true);
			Reporter.log("Controllo lista trasmissione, riferimento messaggio: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedTrasmissione(id, 
					CostantiTestSuite.SPCOOP_SOGGETTO_TEST_URL_PREFIX_REWRITER_1, null,
					CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE, null,
					true));
		
			//Reporter.log("Controllo che la busta abbia generato l'eccezione " + Costanti.ECCEZIONE_PROCESSAMENTO_MESSAGGIO + " rappresentante un servizio applicativo non disponibile");
			//Assert.assertTrue(data.isTracedEccezione(id, Costanti.ECCEZIONE_PROCESSAMENTO_MESSAGGIO));
			
			Reporter.log("Controllo esistenza diagnostico [http://urlPANonEsistente]");
			Assert.assertTrue(msgDiagComponent.isTracedMessaggioWithLike(id,"(location: http://urlPANonEsistente/OpenSPCoop2TestSuite/server)"));
			Assert.assertTrue(msgDiagComponent.isTracedMessaggioWithLike(id,"Errore avvenuto durante la consegna HTTP: unknown host 'urlPANonEsistente"));
			
		}catch(Exception e){
			throw e;
		}finally{
			try{
				data.close();
			}catch(Exception e){}
			try{
				msgDiagComponent.close();
			}catch(Exception e){}
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	/* ************************ TEST 2 http://urlPDNonEsistente/ e http://urlPANonEsistente/ **************************


	/***
	 * Test per pd-url-prefix-rewriter="http://urlPDNonEsistente/"
	 */
	Date testPD2StartTime = null;
	Repository repository_testPD2=new Repository();
	@Test(groups={UrlPrefixRewriter.ID_GRUPPO,UrlPrefixRewriter.ID_GRUPPO+".PD_2"})
	public void testPD2() throws Exception{
		
		Date dataInizioTest = DateManager.getDate();
		
		DatabaseComponent dbComponentFruitore = null;
		DatabaseComponent dbComponentErogatore = null;
		this.testPD2StartTime = new Date();

		try{
			ClientHttpGenerico client=new ClientHttpGenerico(new Repository());
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_TEST_URL_PREFIX_REWRITER_2);
			client.connectToSoapEngine();
			client.setMessageFromFile(Utilities.testSuiteProperties.getSoap11FileName(), false);
			client.setRispostaDaGestire(true);
			// AttesaTerminazioneMessaggi
			if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()){
				dbComponentFruitore = DatabaseProperties.getDatabaseComponentFruitore();
				dbComponentErogatore = DatabaseProperties.getDatabaseComponentErogatore();

				client.setAttesaTerminazioneMessaggi(true);
				client.setDbAttesaTerminazioneMessaggiFruitore(dbComponentFruitore);
				client.setDbAttesaTerminazioneMessaggiErogatore(dbComponentErogatore);
			}
			try {
				client.run();

				Reporter.log("Invocazione porta delegata con identificazione dinamica dei dati non ha causato errori.");
				throw new TestSuiteException("Invocazione porta delegata con identificazione dinamica dei dati non ha causato errori.");

			} catch (AxisFault error) {
				
				Assert.assertTrue(client.getCodiceStatoHTTP()==500);
				
				Utilities.verificaFaultIntegrazione(error, 
						CostantiTestSuite.SPCOOP_NOME_SOGGETTO_TEST_URL_PREFIX_REWRITER_2+"SPCoopIT",InoltroBuste.ID_MODULO, 
						Utilities.toString(CodiceErroreIntegrazione.CODICE_516_CONNETTORE_UTILIZZO_CON_ERRORE), 
						CostantiErroriIntegrazione.MSG_516_PDD_NON_DISPONIBILE.replace(CostantiProtocollo.KEYWORDPDD_NON_DISPONIBILE, CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_EROGATORE+CostantiTestSuite.SPCOOP_NOME_SOGGETTO_EROGATORE), 
						Utilities.CONTROLLO_DESCRIZIONE_TRAMITE_METODO_EQUALS);	
			}finally{
				dbComponentFruitore.close();
				dbComponentErogatore.close();
			}
		}catch(Exception e){
			throw e;
		}finally{
			dbComponentFruitore.close();
			dbComponentErogatore.close();
		}
		
		Date dataFineTest = DateManager.getDate();
		
		ErroreAttesoOpenSPCoopLogCore err = new ErroreAttesoOpenSPCoopLogCore();
		err.setIntervalloInferiore(dataInizioTest);
		err.setIntervalloSuperiore(dataFineTest);
		err.setMsgErrore("Errore avvenuto durante la consegna HTTP: unknown host 'urlPDNonEsistente");
		this.erroriAttesiOpenSPCoopCore.add(err);
	}
	@DataProvider (name="testPD2Provider")
	public Object[][]testPD2Provider()throws Exception{
		String id=this.repository_testPD2.getNext();
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),DatabaseProperties.getDatabaseComponentDiagnosticaFruitore(),id}	
		};
	}
	@Test(groups={UrlPrefixRewriter.ID_GRUPPO,UrlPrefixRewriter.ID_GRUPPO+".PD_2"},dataProvider="testPD2Provider",dependsOnMethods={"testPD2"})
	public void testPD2VerificaTracce(DatabaseComponent data,DatabaseMsgDiagnosticiComponent msgDiagComponent,String id) throws Exception{
		try{
			
			Reporter.log("Controllo tracciamento richiesta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id)==false);
			
			Reporter.log("Controllo esistenza diagnostico [http://urlPDNonEsistente/]");
			Assert.assertTrue(msgDiagComponent.isTracedMessaggioWithLike(this.testPD2StartTime,"(location: http://urlPDNonEsistente/openspcoop2/spcoop/PA)"));
			Assert.assertTrue(msgDiagComponent.isTracedMessaggioWithLike(this.testPD2StartTime,"Errore avvenuto durante la consegna HTTP: unknown host 'urlPDNonEsistente"));
			
		}catch(Exception e){
			throw e;
		}finally{
			try{
				data.close();
			}catch(Exception e){}
			try{
				msgDiagComponent.close();
			}catch(Exception e){}
		}
	}
	
	

	
	
	
	
	
	/***
	 * Test per pa-url-prefix-rewriter="http://urlPANonEsistente/"
	 */
	Date testPA2StartTime = null;
	Repository repository_testPA2=new Repository();
	@Test(groups={UrlPrefixRewriter.ID_GRUPPO,UrlPrefixRewriter.ID_GRUPPO+".PA_2"})
	public void testPA2()throws TestSuiteException,SOAPException, Exception{
		
		Date dataInizioTest = DateManager.getDate();
		
		this.testPA2StartTime = new Date();
		
		String egov=UtilitiesEGov.getIDEGov(CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE,
				CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE+CostantiTestSuite.SPCOOP_PORTA_DOMINIO);
		this.repository_testPA2.add(egov);
		String bustaSOAP=UtilitiesEGov.getBustaEGov(CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_FRUITORE,
				CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE,
				CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_TEST_URL_PREFIX_REWRITER_2,
				CostantiTestSuite.SPCOOP_NOME_SOGGETTO_TEST_URL_PREFIX_REWRITER_2,
				SPCoopCostanti.PROFILO_COLLABORAZIONE_SINCRONO,
				CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
				CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
				egov,
				false,
				SPCoopCostanti.PROFILO_TRASMISSIONE_SENZA_DUPLICATI,
				false,
				null);

		// costruzione busta
		java.io.ByteArrayInputStream bin =
			new java.io.ByteArrayInputStream(bustaSOAP.getBytes());
		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();

		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repository_testPA2);
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneBusteErogatore());
			client.connectToSoapEngine();
			client.setMessage(msg);
			client.setRispostaDaGestire(true);
			// AttesaTerminazioneMessaggi
			if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()){
				dbComponentErogatore = DatabaseProperties.getDatabaseComponentErogatore();

				client.setAttesaTerminazioneMessaggi(true);
				client.setDbAttesaTerminazioneMessaggiErogatore(dbComponentErogatore);
			}
			try {
				client.run();
				if(Utilities.testSuiteProperties.isNewConnectionForResponse()==false){
					Reporter.log("Invocazione PA.");
					throw new TestSuiteException("Invocazione PA, non ha causato errori.");
				}
			} catch (AxisFault error) {
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				Reporter.log("Controllo fault code [Server]");
				Assert.assertTrue("Server".equals(error.getFaultCode().getLocalPart()));
				Reporter.log("Controllo fault string ["+SPCoopCostanti.FAULT_STRING_PROCESSAMENTO_SPCOOP+"]");
				Assert.assertTrue(SPCoopCostanti.FAULT_STRING_PROCESSAMENTO_SPCOOP.equals(error.getFaultString()));
			}finally{
				dbComponentErogatore.close();
			}
		}catch(Exception e){
			throw e;
		}finally{
			dbComponentErogatore.close();
		}
		
		Date dataFineTest = DateManager.getDate();
		
		ErroreAttesoOpenSPCoopLogCore err = new ErroreAttesoOpenSPCoopLogCore();
		err.setIntervalloInferiore(dataInizioTest);
		err.setIntervalloSuperiore(dataFineTest);
		err.setMsgErrore("Errore avvenuto durante la consegna HTTP: unknown host 'urlPANonEsistente");
		this.erroriAttesiOpenSPCoopCore.add(err);
	}
	@DataProvider (name="testPA2Provider")
	public Object[][] testPA2Provider()throws Exception{
		String id=this.repository_testPA2.getNext();
		if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()==false){
			try {
				Thread.sleep(Utilities.testSuiteProperties.timeToSleep_verificaDatabase());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		// Attendo tempo perso per la nuova connessione
		//if(Utilities.testSuiteProperties.isNewConnectionForResponse()){
		try{
			Thread.sleep(3000);
		}catch(InterruptedException e){}
		//}	
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentErogatore(),DatabaseProperties.getDatabaseComponentDiagnosticaErogatore(),id}
		};
	}
	@Test(groups={UrlPrefixRewriter.ID_GRUPPO,UrlPrefixRewriter.ID_GRUPPO+".PA_2"},
			dataProvider="testPA2Provider",
			dependsOnMethods="testPA2")
			public void testPA2VerificaTracce(DatabaseComponent data,DatabaseMsgDiagnosticiComponent msgDiagComponent,String id) throws Exception{
		try{
			Reporter.log("Controllo tracciamento richiesta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
			Reporter.log("Controllo valore Mittente Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id, CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE , null));
			Reporter.log("Controllo valore Destinatario Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedDestinatario(id, CostantiTestSuite.SPCOOP_SOGGETTO_TEST_URL_PREFIX_REWRITER_2, null));
			Reporter.log("Controllo valore OraRegistrazione con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedOraRegistrazione(id));

			Reporter.log("Controllo valore Servizio Busta con id: " +id);
			DatiServizio datiServizio = new DatiServizio(CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO, CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO, CostantiTestSuite.SPCOOP_VERSIONE_SERVIZIO_DEFAULT);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedServizio(id, datiServizio));
			Reporter.log("Controllo valore Azione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedAzione(id, null));
			Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloDiCollaborazione(id, SPCoopCostanti.PROFILO_COLLABORAZIONE_SINCRONO, ProfiloDiCollaborazione.SINCRONO));
			Reporter.log("Controllo valore Profilo di Trasmissione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloTrasmissione(id, false,SPCoopCostanti.PROFILO_TRASMISSIONE_SENZA_DUPLICATI, Inoltro.SENZA_DUPLICATI));
			//Reporter.log("Controllo che la busta abbia generato eccezioni, id: " +id);
			//Assert.assertTrue(data.getVerificatoreTracciaRisposta().existsListaEccezioni(id)==true);
			Reporter.log("Controllo lista trasmissione con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedTrasmissione(id, 
					CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE, null ,
					CostantiTestSuite.SPCOOP_SOGGETTO_TEST_URL_PREFIX_REWRITER_2, null,
					true));
			Reporter.log("Numero messaggi arrivati al servizio applicativo: "+data.getVerificatoreTracciaRichiesta().isArrivedCount(id));
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isArrivedCount(id)==0);

			Reporter.log("----------------------------------------------------------");

			Reporter.log("Controllo tracciamento risposta con riferimento id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTraced(id));
			Reporter.log("Controllo valore Mittente Busta della risposta con riferimento id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedMittente(id, CostantiTestSuite.SPCOOP_SOGGETTO_TEST_URL_PREFIX_REWRITER_2, null));
			Reporter.log("Controllo valore Destinatario Busta della risposta con riferimento id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedDestinatario(id, CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE , null));
			Reporter.log("Controllo valore OraRegistrazione con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedOraRegistrazione(id));
	
			Reporter.log("Controllo valore Servizio Busta della risposta con riferimento id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedServizio(id, datiServizio));
			Reporter.log("Controllo valore Azione Busta della risposta con riferimento id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedAzione(id, null));
			Reporter.log("Controllo valore Profilo di Collaborazione Busta della risposta con riferimento id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedProfiloDiCollaborazione(id, SPCoopCostanti.PROFILO_COLLABORAZIONE_SINCRONO, ProfiloDiCollaborazione.SINCRONO));
			Reporter.log("Controllo valore Profilo di Trasmissione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedProfiloTrasmissione(id,false,SPCoopCostanti.PROFILO_TRASMISSIONE_SENZA_DUPLICATI, Inoltro.SENZA_DUPLICATI));
			//Reporter.log("Controllo che la busta abbia generato eccezioni, riferimento messaggio: " +id);
			//Assert.assertTrue(data.getVerificatoreTracciaRisposta().existsListaEccezioni(id)==true);
			Reporter.log("Controllo lista trasmissione, riferimento messaggio: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedTrasmissione(id, 
					CostantiTestSuite.SPCOOP_SOGGETTO_TEST_URL_PREFIX_REWRITER_2, null,
					CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE, null,
					true));
		
			//Reporter.log("Controllo che la busta abbia generato l'eccezione " + Costanti.ECCEZIONE_PROCESSAMENTO_MESSAGGIO + " rappresentante un servizio applicativo non disponibile");
			//Assert.assertTrue(data.isTracedEccezione(id, Costanti.ECCEZIONE_PROCESSAMENTO_MESSAGGIO));
			
			Reporter.log("Controllo esistenza diagnostico [http://urlPANonEsistente/]");
			Assert.assertTrue(msgDiagComponent.isTracedMessaggioWithLike(id,"(location: http://urlPANonEsistente/OpenSPCoop2TestSuite/server)"));
			Assert.assertTrue(msgDiagComponent.isTracedMessaggioWithLike(id,"Errore avvenuto durante la consegna HTTP: unknown host 'urlPANonEsistente"));
			
		}catch(Exception e){
			throw e;
		}finally{
			try{
				data.close();
			}catch(Exception e){}
			try{
				msgDiagComponent.close();
			}catch(Exception e){}
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	/* ************************ TEST 3 http://localhost:8080/openspcoop/testPD e http://localhost:8080/openspcoop/testPA **************************


	/***
	 * Test per pd-url-prefix-rewriter="http://localhost:8080/openspcoop/testPD"
	 */
	Date testPD3StartTime = null;
	Repository repository_testPD3=new Repository();
	@Test(groups={UrlPrefixRewriter.ID_GRUPPO,UrlPrefixRewriter.ID_GRUPPO+".PD_3"})
	public void testPD3() throws Exception{
		DatabaseComponent dbComponentFruitore = null;
		DatabaseComponent dbComponentErogatore = null;
		
		this.testPD3StartTime = new Date();

		try{
			ClientHttpGenerico client=new ClientHttpGenerico(new Repository());
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_TEST_URL_PREFIX_REWRITER_3);
			client.connectToSoapEngine();
			client.setMessageFromFile(Utilities.testSuiteProperties.getSoap11FileName(), false);
			client.setRispostaDaGestire(true);
			// AttesaTerminazioneMessaggi
			if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()){
				dbComponentFruitore = DatabaseProperties.getDatabaseComponentFruitore();
				dbComponentErogatore = DatabaseProperties.getDatabaseComponentErogatore();

				client.setAttesaTerminazioneMessaggi(true);
				client.setDbAttesaTerminazioneMessaggiFruitore(dbComponentFruitore);
				client.setDbAttesaTerminazioneMessaggiErogatore(dbComponentErogatore);
			}
			try {
				client.run();

				Reporter.log("Invocazione porta delegata con identificazione dinamica dei dati non ha causato errori.");
				throw new TestSuiteException("Invocazione porta delegata con identificazione dinamica dei dati non ha causato errori.");

			} catch (AxisFault error) {
				
				Assert.assertTrue(client.getCodiceStatoHTTP()==500);
				
				Utilities.verificaFaultIntegrazione(error, 
						CostantiTestSuite.SPCOOP_NOME_SOGGETTO_TEST_URL_PREFIX_REWRITER_3+"SPCoopIT",InoltroBuste.ID_MODULO, 
						Utilities.toString(CodiceErroreIntegrazione.CODICE_516_CONNETTORE_UTILIZZO_CON_ERRORE), 
						CostantiErroriIntegrazione.MSG_516_PDD_NON_DISPONIBILE.replace(CostantiProtocollo.KEYWORDPDD_NON_DISPONIBILE, CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_EROGATORE+CostantiTestSuite.SPCOOP_NOME_SOGGETTO_EROGATORE), 
						Utilities.CONTROLLO_DESCRIZIONE_TRAMITE_METODO_EQUALS);	

			}finally{
				dbComponentFruitore.close();
				dbComponentErogatore.close();
			}
		}catch(Exception e){
			throw e;
		}finally{
			dbComponentFruitore.close();
			dbComponentErogatore.close();
		}
	}
	@DataProvider (name="testPD3Provider")
	public Object[][]testPD3Provider()throws Exception{
		String id=this.repository_testPD3.getNext();
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),DatabaseProperties.getDatabaseComponentDiagnosticaFruitore(),id}	
		};
	}
	@Test(groups={UrlPrefixRewriter.ID_GRUPPO,UrlPrefixRewriter.ID_GRUPPO+".PD_3"},dataProvider="testPD3Provider",dependsOnMethods={"testPD3"})
	public void testPD3VerificaTracce(DatabaseComponent data,DatabaseMsgDiagnosticiComponent msgDiagComponent,String id) throws Exception{
		try{
			
			Reporter.log("Controllo tracciamento richiesta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id)==false);
			
			Reporter.log("Controllo esistenza diagnostico [http://localhost:8080/openspcoop/testPD]");
			Assert.assertTrue(msgDiagComponent.isTracedMessaggioWithLike(this.testPD3StartTime,"(location: http://localhost:8080/openspcoop/testPD/openspcoop2/spcoop/PA)"));
			Assert.assertTrue(msgDiagComponent.isTracedMessaggioWithLike(this.testPD3StartTime,"(404)"));
			
		}catch(Exception e){
			throw e;
		}finally{
			try{
				data.close();
			}catch(Exception e){}
			try{
				msgDiagComponent.close();
			}catch(Exception e){}
		}
	}
	
	

	
	
	
	
	
	/***
	 * Test per pa-url-prefix-rewriter="http://localhost:8080/openspcoop/testPA"
	 */
	Date testPA3StartTime = null;
	Repository repository_testPA3=new Repository();
	@Test(groups={UrlPrefixRewriter.ID_GRUPPO,UrlPrefixRewriter.ID_GRUPPO+".PA_3"})
	public void testPA3()throws TestSuiteException,SOAPException, Exception{
		
		this.testPA3StartTime = new Date();
		
		String egov=UtilitiesEGov.getIDEGov(CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE,
				CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE+CostantiTestSuite.SPCOOP_PORTA_DOMINIO);
		this.repository_testPA3.add(egov);
		String bustaSOAP=UtilitiesEGov.getBustaEGov(CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_FRUITORE,
				CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE,
				CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_TEST_URL_PREFIX_REWRITER_3,
				CostantiTestSuite.SPCOOP_NOME_SOGGETTO_TEST_URL_PREFIX_REWRITER_3,
				SPCoopCostanti.PROFILO_COLLABORAZIONE_SINCRONO,
				CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
				CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
				egov,
				false,
				SPCoopCostanti.PROFILO_TRASMISSIONE_SENZA_DUPLICATI,
				false,
				null);

		// costruzione busta
		java.io.ByteArrayInputStream bin =
			new java.io.ByteArrayInputStream(bustaSOAP.getBytes());
		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();

		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repository_testPA3);
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneBusteErogatore());
			client.connectToSoapEngine();
			client.setMessage(msg);
			client.setRispostaDaGestire(true);
			// AttesaTerminazioneMessaggi
			if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()){
				dbComponentErogatore = DatabaseProperties.getDatabaseComponentErogatore();

				client.setAttesaTerminazioneMessaggi(true);
				client.setDbAttesaTerminazioneMessaggiErogatore(dbComponentErogatore);
			}
			try {
				client.run();
				if(Utilities.testSuiteProperties.isNewConnectionForResponse()==false){
					Reporter.log("Invocazione PA.");
					throw new TestSuiteException("Invocazione PA, non ha causato errori.");
				}
			} catch (AxisFault error) {
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				Reporter.log("Controllo actor null");
				Assert.assertTrue(error.getFaultActor()==null);
				Reporter.log("Controllo fault code [Server]");
				Assert.assertTrue("Server".equals(error.getFaultCode().getLocalPart()));
				Reporter.log("Controllo fault string ["+SPCoopCostanti.FAULT_STRING_PROCESSAMENTO_SPCOOP+"]");
				Assert.assertTrue(SPCoopCostanti.FAULT_STRING_PROCESSAMENTO_SPCOOP.equals(error.getFaultString()));
			}finally{
				dbComponentErogatore.close();
			}
		}catch(Exception e){
			throw e;
		}finally{
			dbComponentErogatore.close();
		}
	}
	@DataProvider (name="testPA3Provider")
	public Object[][] testPA3Provider()throws Exception{
		String id=this.repository_testPA3.getNext();
		if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()==false){
			try {
				Thread.sleep(Utilities.testSuiteProperties.timeToSleep_verificaDatabase());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		// Attendo tempo perso per la nuova connessione
		//if(Utilities.testSuiteProperties.isNewConnectionForResponse()){
		try{
			Thread.sleep(3000);
		}catch(InterruptedException e){}
		//}	
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentErogatore(),DatabaseProperties.getDatabaseComponentDiagnosticaErogatore(),id}
		};
	}
	@Test(groups={UrlPrefixRewriter.ID_GRUPPO,UrlPrefixRewriter.ID_GRUPPO+".PA_3"},
			dataProvider="testPA3Provider",
			dependsOnMethods="testPA3")
			public void testPA3VerificaTracce(DatabaseComponent data,DatabaseMsgDiagnosticiComponent msgDiagComponent,String id) throws Exception{
		try{
			Reporter.log("Controllo tracciamento richiesta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
			Reporter.log("Controllo valore Mittente Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id, CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE , null));
			Reporter.log("Controllo valore Destinatario Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedDestinatario(id, CostantiTestSuite.SPCOOP_SOGGETTO_TEST_URL_PREFIX_REWRITER_3, null));
			Reporter.log("Controllo valore OraRegistrazione con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedOraRegistrazione(id));

			Reporter.log("Controllo valore Servizio Busta con id: " +id);
			DatiServizio datiServizio = new DatiServizio(CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO, CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO, CostantiTestSuite.SPCOOP_VERSIONE_SERVIZIO_DEFAULT);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedServizio(id, datiServizio));
			Reporter.log("Controllo valore Azione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedAzione(id, null));
			Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloDiCollaborazione(id, SPCoopCostanti.PROFILO_COLLABORAZIONE_SINCRONO,ProfiloDiCollaborazione.SINCRONO));
			Reporter.log("Controllo valore Profilo di Trasmissione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloTrasmissione(id, false,SPCoopCostanti.PROFILO_TRASMISSIONE_SENZA_DUPLICATI,Inoltro.SENZA_DUPLICATI));
			Reporter.log("Controllo che la busta non abbia generato eccezioni, id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().existsListaEccezioni(id)==false);
			Reporter.log("Controllo lista trasmissione con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedTrasmissione(id, 
					CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE, null ,
					CostantiTestSuite.SPCOOP_SOGGETTO_TEST_URL_PREFIX_REWRITER_3, null,
					true));
			Reporter.log("Numero messaggi arrivati al servizio applicativo: "+data.getVerificatoreTracciaRichiesta().isArrivedCount(id));
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isArrivedCount(id)==0);

			Reporter.log("----------------------------------------------------------");

			Reporter.log("Controllo tracciamento risposta con riferimento id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTraced(id));
			Reporter.log("Controllo valore Mittente Busta della risposta con riferimento id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedMittente(id, CostantiTestSuite.SPCOOP_SOGGETTO_TEST_URL_PREFIX_REWRITER_3, null));
			Reporter.log("Controllo valore Destinatario Busta della risposta con riferimento id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedDestinatario(id, CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE , null));
			Reporter.log("Controllo valore OraRegistrazione con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedOraRegistrazione(id));
	
			Reporter.log("Controllo valore Servizio Busta della risposta con riferimento id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedServizio(id, datiServizio));
			Reporter.log("Controllo valore Azione Busta della risposta con riferimento id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedAzione(id, null));
			Reporter.log("Controllo valore Profilo di Collaborazione Busta della risposta con riferimento id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedProfiloDiCollaborazione(id, SPCoopCostanti.PROFILO_COLLABORAZIONE_SINCRONO,ProfiloDiCollaborazione.SINCRONO));
			Reporter.log("Controllo valore Profilo di Trasmissione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedProfiloTrasmissione(id,false,SPCoopCostanti.PROFILO_TRASMISSIONE_SENZA_DUPLICATI,Inoltro.SENZA_DUPLICATI));
			Reporter.log("Controllo che la busta non abbia generato eccezioni, riferimento messaggio: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().existsListaEccezioni(id)==false);
			Reporter.log("Controllo lista trasmissione, riferimento messaggio: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedTrasmissione(id, 
					CostantiTestSuite.SPCOOP_SOGGETTO_TEST_URL_PREFIX_REWRITER_3, null,
					CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE, null,
					true));
		
			//Reporter.log("Controllo che la busta abbia generato l'eccezione " + Costanti.ECCEZIONE_PROCESSAMENTO_MESSAGGIO + " rappresentante un servizio applicativo non disponibile");
			//Assert.assertTrue(data.isTracedEccezione(id, Costanti.ECCEZIONE_PROCESSAMENTO_MESSAGGIO));
			
			Reporter.log("Controllo esistenza diagnostico [http://localhost:8080/openspcoop/testPA]");
			Assert.assertTrue(msgDiagComponent.isTracedMessaggioWithLike(id,"(location: http://localhost:8080/openspcoop/testPA/OpenSPCoop2TestSuite/server)"));
			Assert.assertTrue(msgDiagComponent.isTracedMessaggioWithLike(id,"(404)"));
			
		}catch(Exception e){
			throw e;
		}finally{
			try{
				data.close();
			}catch(Exception e){}
			try{
				msgDiagComponent.close();
			}catch(Exception e){}
		}
	}
	
	
	
	
	
	
	
	
	/* ************************ TEST 4, prefissi corretti http://localhost:8080 e http://localhost:8080/ ************************** */
	
	/***
	 * Test per pd-url-prefix-rewriter="http://localhost:8080"
	 */
	private CooperazioneBaseInformazioni info_testPD4 = CooperazioneSPCoopBase.getCooperazioneBaseInformazioni(CostantiTestSuite.SPCOOP_SOGGETTO_TEST_URL_PREFIX_REWRITER_4,
			CostantiTestSuite.SPCOOP_SOGGETTO_EROGATORE,
			false,SPCoopCostanti.PROFILO_TRASMISSIONE_CON_DUPLICATI,Inoltro.CON_DUPLICATI);	
	private CooperazioneBase collaborazioneSPCoopBase_testPD4 = 
		new CooperazioneBase(false, SOAPVersion.SOAP11, this.info_testPD4, 
				org.openspcoop2.protocol.spcoop.testsuite.core.TestSuiteProperties.getInstance(), 
				DatabaseProperties.getInstance(), SPCoopTestsuiteLogger.getInstance());

	Repository repository_testPD4=new Repository();
	@Test(groups={UrlPrefixRewriter.ID_GRUPPO,UrlPrefixRewriter.ID_GRUPPO+".PD_4"})
	public void testPD4() throws TestSuiteException, IOException, SOAPException{
		this.collaborazioneSPCoopBase_testPD4.sincrono(this.repository_testPD4,CostantiTestSuite.PORTA_DELEGATA_TEST_URL_PREFIX_REWRITER_4,true);
	}
	@DataProvider (name="testPD4Provider")
	public Object[][]testPD4Provider()throws Exception{
		String id=this.repository_testPD4.getNext();
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),id,false},	
				{DatabaseProperties.getDatabaseComponentErogatore(),id,true}	
		};
	}
	@Test(groups={UrlPrefixRewriter.ID_GRUPPO,UrlPrefixRewriter.ID_GRUPPO+".PD_4"},dataProvider="testPD4Provider",dependsOnMethods={"testPD4"})
	public void testPD4VerificaTracce(DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
		try{
			this.collaborazioneSPCoopBase_testPD4.testSincrono(data, id, CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,null, checkServizioApplicativo,
					null);
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	
	
	

	
	/***
	 * Test per pa-url-prefix-rewriter="http://localhost:8080/"
	 */
	Repository repository_testPA4=new Repository();
	@Test(groups={UrlPrefixRewriter.ID_GRUPPO,UrlPrefixRewriter.ID_GRUPPO+".PA_4"})
	public void testPA4()throws TestSuiteException,SOAPException, Exception{
		String egov=UtilitiesEGov.getIDEGov(CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE,
				CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE+CostantiTestSuite.SPCOOP_PORTA_DOMINIO);
		this.repository_testPA4.add(egov);
		String bustaSOAP=UtilitiesEGov.getBustaEGov(CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_FRUITORE,
				CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE,
				CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_TEST_URL_PREFIX_REWRITER_4,
				CostantiTestSuite.SPCOOP_NOME_SOGGETTO_TEST_URL_PREFIX_REWRITER_4,
				SPCoopCostanti.PROFILO_COLLABORAZIONE_SINCRONO,
				CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
				CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
				egov,
				false,
				SPCoopCostanti.PROFILO_TRASMISSIONE_SENZA_DUPLICATI,
				false,
				null);

		// costruzione busta
		java.io.ByteArrayInputStream bin =
			new java.io.ByteArrayInputStream(bustaSOAP.getBytes());
		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();

		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repository_testPA4);
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneBusteErogatore());
			client.connectToSoapEngine();
			client.setMessage(msg);
			client.setRispostaDaGestire(true);
			// AttesaTerminazioneMessaggi
			if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()){
				dbComponentErogatore = DatabaseProperties.getDatabaseComponentErogatore();

				client.setAttesaTerminazioneMessaggi(true);
				client.setDbAttesaTerminazioneMessaggiErogatore(dbComponentErogatore);
			}
			try {
				client.run();
			} catch (AxisFault error) {
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				throw error;
			}finally{
				dbComponentErogatore.close();
			}
		}catch(Exception e){
			throw e;
		}finally{
			dbComponentErogatore.close();
		}
	}
	@DataProvider (name="testPA4Provider")
	public Object[][] testPA4Provider()throws Exception{
		String id=this.repository_testPA4.getNext();
		if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()==false){
			try {
				Thread.sleep(Utilities.testSuiteProperties.timeToSleep_verificaDatabase());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		// Attendo tempo perso per la nuova connessione
		//if(Utilities.testSuiteProperties.isNewConnectionForResponse()){
		try{
			Thread.sleep(3000);
		}catch(InterruptedException e){}
		//}	
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentErogatore(),id}
		};
	}
	@Test(groups={UrlPrefixRewriter.ID_GRUPPO,UrlPrefixRewriter.ID_GRUPPO+".PA_4"},
			dataProvider="testPA4Provider",
			dependsOnMethods="testPA4")
			public void testPA4VerificaTracce(DatabaseComponent data,String id) throws Exception{
		try{
			Reporter.log("Controllo tracciamento richiesta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
			Reporter.log("Controllo valore Mittente Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id, CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE , null));
			Reporter.log("Controllo valore Destinatario Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedDestinatario(id, CostantiTestSuite.SPCOOP_SOGGETTO_TEST_URL_PREFIX_REWRITER_4, null));
			Reporter.log("Controllo valore OraRegistrazione con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedOraRegistrazione(id));

			Reporter.log("Controllo valore Servizio Busta con id: " +id);
			DatiServizio datiServizio = new DatiServizio(CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO, CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO, CostantiTestSuite.SPCOOP_VERSIONE_SERVIZIO_DEFAULT);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedServizio(id, datiServizio));
			Reporter.log("Controllo valore Azione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedAzione(id, null));
			Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloDiCollaborazione(id, SPCoopCostanti.PROFILO_COLLABORAZIONE_SINCRONO,ProfiloDiCollaborazione.SINCRONO));
			Reporter.log("Controllo valore Profilo di Trasmissione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloTrasmissione(id, false,SPCoopCostanti.PROFILO_TRASMISSIONE_SENZA_DUPLICATI,Inoltro.SENZA_DUPLICATI));
			Reporter.log("Controllo che la busta non abbia generato eccezioni, id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().existsListaEccezioni(id)==false);
			Reporter.log("Controllo lista trasmissione con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedTrasmissione(id, 
					CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE, null ,
					CostantiTestSuite.SPCOOP_SOGGETTO_TEST_URL_PREFIX_REWRITER_4, null,
					true));
			Reporter.log("Numero messaggi arrivati al servizio applicativo: "+data.getVerificatoreTracciaRichiesta().isArrivedCount(id));
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isArrivedCount(id)==1);

			Reporter.log("----------------------------------------------------------");

			Reporter.log("Controllo tracciamento risposta con riferimento id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTraced(id));
			Reporter.log("Controllo valore Mittente Busta della risposta con riferimento id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedMittente(id, CostantiTestSuite.SPCOOP_SOGGETTO_TEST_URL_PREFIX_REWRITER_4, null));
			Reporter.log("Controllo valore Destinatario Busta della risposta con riferimento id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedDestinatario(id, CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE , null));
			Reporter.log("Controllo valore OraRegistrazione con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedOraRegistrazione(id));
	
			Reporter.log("Controllo valore Servizio Busta della risposta con riferimento id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedServizio(id, datiServizio));
			Reporter.log("Controllo valore Azione Busta della risposta con riferimento id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedAzione(id, null));
			Reporter.log("Controllo valore Profilo di Collaborazione Busta della risposta con riferimento id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedProfiloDiCollaborazione(id, SPCoopCostanti.PROFILO_COLLABORAZIONE_SINCRONO,ProfiloDiCollaborazione.SINCRONO));
			Reporter.log("Controllo valore Profilo di Trasmissione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedProfiloTrasmissione(id,false,SPCoopCostanti.PROFILO_TRASMISSIONE_SENZA_DUPLICATI,Inoltro.SENZA_DUPLICATI));
			Reporter.log("Controllo che la busta non abbia generato eccezioni, riferimento messaggio: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().existsListaEccezioni(id)==false);
			Reporter.log("Controllo lista trasmissione, riferimento messaggio: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedTrasmissione(id, 
					CostantiTestSuite.SPCOOP_SOGGETTO_TEST_URL_PREFIX_REWRITER_4, null,
					CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE, null,
					true));
						
		}catch(Exception e){
			throw e;
		}finally{
			try{
				data.close();
			}catch(Exception e){}
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/* ************************ TEST 5  http://localhostPD:6789 e http://localhostPA:6789 **************************


	/***
	 * Test per pd-url-prefix-rewriter="http://localhostPD:6789"
	 */
	Date testPD5StartTime = null;
	Repository repository_testPD5=new Repository();
	@Test(groups={UrlPrefixRewriter.ID_GRUPPO,UrlPrefixRewriter.ID_GRUPPO+".PD_5"})
	public void testPD5() throws Exception{
		
		Date dataInizioTest = DateManager.getDate();
		
		DatabaseComponent dbComponentFruitore = null;
		DatabaseComponent dbComponentErogatore = null;

		this.testPD5StartTime = new Date();
		
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(new Repository());
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_TEST_URL_PREFIX_REWRITER_5);
			client.connectToSoapEngine();
			client.setMessageFromFile(Utilities.testSuiteProperties.getSoap11FileName(), false);
			client.setRispostaDaGestire(true);
			// AttesaTerminazioneMessaggi
			if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()){
				dbComponentFruitore = DatabaseProperties.getDatabaseComponentFruitore();
				dbComponentErogatore = DatabaseProperties.getDatabaseComponentErogatore();

				client.setAttesaTerminazioneMessaggi(true);
				client.setDbAttesaTerminazioneMessaggiFruitore(dbComponentFruitore);
				client.setDbAttesaTerminazioneMessaggiErogatore(dbComponentErogatore);
			}
			try {
				client.run();

				Reporter.log("Invocazione porta delegata con identificazione dinamica dei dati non ha causato errori.");
				throw new TestSuiteException("Invocazione porta delegata con identificazione dinamica dei dati non ha causato errori.");

			} catch (AxisFault error) {
				
				Assert.assertTrue(client.getCodiceStatoHTTP()==500);
				
				Utilities.verificaFaultIntegrazione(error, 
						CostantiTestSuite.SPCOOP_NOME_SOGGETTO_TEST_URL_PREFIX_REWRITER_5+"SPCoopIT",InoltroBuste.ID_MODULO, 
						Utilities.toString(CodiceErroreIntegrazione.CODICE_516_CONNETTORE_UTILIZZO_CON_ERRORE), 
						CostantiErroriIntegrazione.MSG_516_PDD_NON_DISPONIBILE.replace(CostantiProtocollo.KEYWORDPDD_NON_DISPONIBILE, CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_EROGATORE+CostantiTestSuite.SPCOOP_NOME_SOGGETTO_EROGATORE), 
						Utilities.CONTROLLO_DESCRIZIONE_TRAMITE_METODO_EQUALS);	
			}finally{
				dbComponentFruitore.close();
				dbComponentErogatore.close();
			}
		}catch(Exception e){
			throw e;
		}finally{
			dbComponentFruitore.close();
			dbComponentErogatore.close();
		}
		
		Date dataFineTest = DateManager.getDate();
		
		ErroreAttesoOpenSPCoopLogCore err = new ErroreAttesoOpenSPCoopLogCore();
		err.setIntervalloInferiore(dataInizioTest);
		err.setIntervalloSuperiore(dataFineTest);
		err.setMsgErrore("Errore avvenuto durante la consegna HTTP: unknown host 'localhostPD");
		this.erroriAttesiOpenSPCoopCore.add(err);
	}
	@DataProvider (name="testPD5Provider")
	public Object[][]testPD5Provider()throws Exception{
		String id=this.repository_testPD5.getNext();
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),DatabaseProperties.getDatabaseComponentDiagnosticaFruitore(),id}	
		};
	}
	@Test(groups={UrlPrefixRewriter.ID_GRUPPO,UrlPrefixRewriter.ID_GRUPPO+".PD_5"},dataProvider="testPD5Provider",dependsOnMethods={"testPD5"})
	public void testPD5VerificaTracce(DatabaseComponent data,DatabaseMsgDiagnosticiComponent msgDiagComponent,String id) throws Exception{
		try{
			
			Reporter.log("Controllo tracciamento richiesta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id)==false);
			
			Reporter.log("Controllo esistenza diagnostico [http://localhostPD:6789]");
			Assert.assertTrue(msgDiagComponent.isTracedMessaggioWithLike(this.testPD5StartTime,"(location: http://localhostPD:6789/openspcoop2/spcoop/PA)"));
			Assert.assertTrue(msgDiagComponent.isTracedMessaggioWithLike(this.testPD5StartTime,"Errore avvenuto durante la consegna HTTP: unknown host 'localhostPD"));
			
		}catch(Exception e){
			throw e;
		}finally{
			try{
				data.close();
			}catch(Exception e){}
			try{
				msgDiagComponent.close();
			}catch(Exception e){}
		}
	}
	
	

	
	
	
	
	
	/***
	 * Test per pa-url-prefix-rewriter="http://localhostPA:6789"
	 */
	Date testPA5StartTime = null;
	Repository repository_testPA5=new Repository();
	@Test(groups={UrlPrefixRewriter.ID_GRUPPO,UrlPrefixRewriter.ID_GRUPPO+".PA_5"})
	public void testPA5()throws TestSuiteException,SOAPException, Exception{
		
		Date dataInizioTest = DateManager.getDate();
		
		this.testPA5StartTime = new Date();
		
		String egov=UtilitiesEGov.getIDEGov(CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE,
				CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE+CostantiTestSuite.SPCOOP_PORTA_DOMINIO);
		this.repository_testPA5.add(egov);
		String bustaSOAP=UtilitiesEGov.getBustaEGov(CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_FRUITORE,
				CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE,
				CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_TEST_URL_PREFIX_REWRITER_5,
				CostantiTestSuite.SPCOOP_NOME_SOGGETTO_TEST_URL_PREFIX_REWRITER_5,
				SPCoopCostanti.PROFILO_COLLABORAZIONE_SINCRONO,
				CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
				CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
				egov,
				false,
				SPCoopCostanti.PROFILO_TRASMISSIONE_SENZA_DUPLICATI,
				false,
				null);

		// costruzione busta
		java.io.ByteArrayInputStream bin =
			new java.io.ByteArrayInputStream(bustaSOAP.getBytes());
		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();

		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repository_testPA5);
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneBusteErogatore());
			client.connectToSoapEngine();
			client.setMessage(msg);
			client.setRispostaDaGestire(true);
			// AttesaTerminazioneMessaggi
			if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()){
				dbComponentErogatore = DatabaseProperties.getDatabaseComponentErogatore();

				client.setAttesaTerminazioneMessaggi(true);
				client.setDbAttesaTerminazioneMessaggiErogatore(dbComponentErogatore);
			}
			try {
				client.run();
				if(Utilities.testSuiteProperties.isNewConnectionForResponse()==false){
					Reporter.log("Invocazione PA.");
					throw new TestSuiteException("Invocazione PA, non ha causato errori.");
				}
			} catch (AxisFault error) {
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				Reporter.log("Controllo fault code [Server]");
				Assert.assertTrue("Server".equals(error.getFaultCode().getLocalPart()));
				Reporter.log("Controllo fault string ["+SPCoopCostanti.FAULT_STRING_PROCESSAMENTO_SPCOOP+"]");
				Assert.assertTrue(SPCoopCostanti.FAULT_STRING_PROCESSAMENTO_SPCOOP.equals(error.getFaultString()));
			}finally{
				dbComponentErogatore.close();
			}
		}catch(Exception e){
			throw e;
		}finally{
			dbComponentErogatore.close();
		}
		
		Date dataFineTest = DateManager.getDate();
		
		ErroreAttesoOpenSPCoopLogCore err = new ErroreAttesoOpenSPCoopLogCore();
		err.setIntervalloInferiore(dataInizioTest);
		err.setIntervalloSuperiore(dataFineTest);
		err.setMsgErrore("Errore avvenuto durante la consegna HTTP: unknown host 'localhostPA");
		this.erroriAttesiOpenSPCoopCore.add(err);
	}
	@DataProvider (name="testPA5Provider")
	public Object[][] testPA5Provider()throws Exception{
		String id=this.repository_testPA5.getNext();
		if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()==false){
			try {
				Thread.sleep(Utilities.testSuiteProperties.timeToSleep_verificaDatabase());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		// Attendo tempo perso per la nuova connessione
		//if(Utilities.testSuiteProperties.isNewConnectionForResponse()){
		try{
			Thread.sleep(3000);
		}catch(InterruptedException e){}
		//}	
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentErogatore(),DatabaseProperties.getDatabaseComponentDiagnosticaErogatore(),id}
		};
	}
	@Test(groups={UrlPrefixRewriter.ID_GRUPPO,UrlPrefixRewriter.ID_GRUPPO+".PA_5"},
			dataProvider="testPA5Provider",
			dependsOnMethods="testPA5")
			public void testPA5VerificaTracce(DatabaseComponent data,DatabaseMsgDiagnosticiComponent msgDiagComponent,String id) throws Exception{
		try{
			Reporter.log("Controllo tracciamento richiesta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
			Reporter.log("Controllo valore Mittente Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id, CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE , null));
			Reporter.log("Controllo valore Destinatario Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedDestinatario(id, CostantiTestSuite.SPCOOP_SOGGETTO_TEST_URL_PREFIX_REWRITER_5, null));
			Reporter.log("Controllo valore OraRegistrazione con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedOraRegistrazione(id));

			Reporter.log("Controllo valore Servizio Busta con id: " +id);
			DatiServizio datiServizio = new DatiServizio(CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO, CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO, CostantiTestSuite.SPCOOP_VERSIONE_SERVIZIO_DEFAULT);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedServizio(id, datiServizio));
			Reporter.log("Controllo valore Azione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedAzione(id, null));
			Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloDiCollaborazione(id, SPCoopCostanti.PROFILO_COLLABORAZIONE_SINCRONO,ProfiloDiCollaborazione.SINCRONO));
			Reporter.log("Controllo valore Profilo di Trasmissione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloTrasmissione(id, false,SPCoopCostanti.PROFILO_TRASMISSIONE_SENZA_DUPLICATI,Inoltro.SENZA_DUPLICATI));
			//Reporter.log("Controllo che la busta abbia generato eccezioni, id: " +id);
			//Assert.assertTrue(data.getVerificatoreTracciaRisposta().existsListaEccezioni(id)==true);
			Reporter.log("Controllo lista trasmissione con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedTrasmissione(id, 
					CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE, null ,
					CostantiTestSuite.SPCOOP_SOGGETTO_TEST_URL_PREFIX_REWRITER_5, null,
					true));
			Reporter.log("Numero messaggi arrivati al servizio applicativo: "+data.getVerificatoreTracciaRichiesta().isArrivedCount(id));
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isArrivedCount(id)==0);

			Reporter.log("----------------------------------------------------------");

			Reporter.log("Controllo tracciamento risposta con riferimento id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTraced(id));
			Reporter.log("Controllo valore Mittente Busta della risposta con riferimento id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedMittente(id, CostantiTestSuite.SPCOOP_SOGGETTO_TEST_URL_PREFIX_REWRITER_5, null));
			Reporter.log("Controllo valore Destinatario Busta della risposta con riferimento id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedDestinatario(id, CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE , null));
			Reporter.log("Controllo valore OraRegistrazione con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedOraRegistrazione(id));
	
			Reporter.log("Controllo valore Servizio Busta della risposta con riferimento id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedServizio(id, datiServizio));
			Reporter.log("Controllo valore Azione Busta della risposta con riferimento id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedAzione(id, null));
			Reporter.log("Controllo valore Profilo di Collaborazione Busta della risposta con riferimento id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedProfiloDiCollaborazione(id, SPCoopCostanti.PROFILO_COLLABORAZIONE_SINCRONO,ProfiloDiCollaborazione.SINCRONO));
			Reporter.log("Controllo valore Profilo di Trasmissione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedProfiloTrasmissione(id,false,SPCoopCostanti.PROFILO_TRASMISSIONE_SENZA_DUPLICATI,Inoltro.SENZA_DUPLICATI));
			//Reporter.log("Controllo che la busta abbia generato eccezioni, riferimento messaggio: " +id);
			//Assert.assertTrue(data.getVerificatoreTracciaRisposta().existsListaEccezioni(id)==true);
			Reporter.log("Controllo lista trasmissione, riferimento messaggio: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedTrasmissione(id, 
					CostantiTestSuite.SPCOOP_SOGGETTO_TEST_URL_PREFIX_REWRITER_5, null,
					CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE, null,
					true));
		
			//Reporter.log("Controllo che la busta abbia generato l'eccezione " + Costanti.ECCEZIONE_PROCESSAMENTO_MESSAGGIO + " rappresentante un servizio applicativo non disponibile");
			//Assert.assertTrue(data.isTracedEccezione(id, Costanti.ECCEZIONE_PROCESSAMENTO_MESSAGGIO));
			
			Reporter.log("Controllo esistenza diagnostico [ http://localhostPA:6789/OpenSPCoop2TestSuite/server]");
			Assert.assertTrue(msgDiagComponent.isTracedMessaggioWithLike(id,"(location: http://localhostPA:6789/OpenSPCoop2TestSuite/server)"));
			Assert.assertTrue(msgDiagComponent.isTracedMessaggioWithLike(id,"Errore avvenuto durante la consegna HTTP: unknown host 'localhostPA"));
			
		}catch(Exception e){
			throw e;
		}finally{
			try{
				data.close();
			}catch(Exception e){}
			try{
				msgDiagComponent.close();
			}catch(Exception e){}
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	/* ************************ TEST 6   https://verificaSSLTestPD e https://verificaSSLTestPD **************************


	/***
	 * Test per pd-url-prefix-rewriter="https://verificaSSLTestPD"
	 */
	Date testPD6StartTime = null;
	Repository repository_testPD6=new Repository();
	@Test(groups={UrlPrefixRewriter.ID_GRUPPO,UrlPrefixRewriter.ID_GRUPPO+".PD_6"})
	public void testPD6() throws Exception{
		
		Date dataInizioTest = DateManager.getDate();
		
		DatabaseComponent dbComponentFruitore = null;
		DatabaseComponent dbComponentErogatore = null;

		this.testPD6StartTime = new Date();
		
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(new Repository());
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_TEST_URL_PREFIX_REWRITER_6);
			client.connectToSoapEngine();
			client.setMessageFromFile(Utilities.testSuiteProperties.getSoap11FileName(), false);
			client.setRispostaDaGestire(true);
			// AttesaTerminazioneMessaggi
			if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()){
				dbComponentFruitore = DatabaseProperties.getDatabaseComponentFruitore();
				dbComponentErogatore = DatabaseProperties.getDatabaseComponentErogatore();

				client.setAttesaTerminazioneMessaggi(true);
				client.setDbAttesaTerminazioneMessaggiFruitore(dbComponentFruitore);
				client.setDbAttesaTerminazioneMessaggiErogatore(dbComponentErogatore);
			}
			try {
				client.run();

				Reporter.log("Invocazione porta delegata con identificazione dinamica dei dati non ha causato errori.");
				throw new TestSuiteException("Invocazione porta delegata con identificazione dinamica dei dati non ha causato errori.");

			} catch (AxisFault error) {
				
				Assert.assertTrue(client.getCodiceStatoHTTP()==500);
				
				Utilities.verificaFaultIntegrazione(error, 
						CostantiTestSuite.SPCOOP_NOME_SOGGETTO_TEST_URL_PREFIX_REWRITER_6+"SPCoopIT",InoltroBuste.ID_MODULO, 
						Utilities.toString(CodiceErroreIntegrazione.CODICE_516_CONNETTORE_UTILIZZO_CON_ERRORE), 
						CostantiErroriIntegrazione.MSG_516_PDD_NON_DISPONIBILE.replace(CostantiProtocollo.KEYWORDPDD_NON_DISPONIBILE, CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_EROGATORE+CostantiTestSuite.SPCOOP_NOME_SOGGETTO_EROGATORE), 
						Utilities.CONTROLLO_DESCRIZIONE_TRAMITE_METODO_EQUALS);	
			}finally{
				dbComponentFruitore.close();
				dbComponentErogatore.close();
			}
		}catch(Exception e){
			throw e;
		}finally{
			dbComponentFruitore.close();
			dbComponentErogatore.close();
		}
		
		Date dataFineTest = DateManager.getDate();
		
		ErroreAttesoOpenSPCoopLogCore err = new ErroreAttesoOpenSPCoopLogCore();
		err.setIntervalloInferiore(dataInizioTest);
		err.setIntervalloSuperiore(dataFineTest);
		err.setMsgErrore("Errore avvenuto durante la consegna HTTP: unknown host 'verificaSSLTestPD");
		this.erroriAttesiOpenSPCoopCore.add(err);
	}
	@DataProvider (name="testPD6Provider")
	public Object[][]testPD6Provider()throws Exception{
		String id=this.repository_testPD6.getNext();
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),DatabaseProperties.getDatabaseComponentDiagnosticaFruitore(),id}	
		};
	}
	@Test(groups={UrlPrefixRewriter.ID_GRUPPO,UrlPrefixRewriter.ID_GRUPPO+".PD_6"},dataProvider="testPD6Provider",dependsOnMethods={"testPD6"})
	public void testPD6VerificaTracce(DatabaseComponent data,DatabaseMsgDiagnosticiComponent msgDiagComponent,String id) throws Exception{
		try{
			
			Reporter.log("Controllo tracciamento richiesta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id)==false);
			
			Reporter.log("Controllo esistenza diagnostico [https://verificaSSLTestPD]");
			Assert.assertTrue(msgDiagComponent.isTracedMessaggioWithLike(this.testPD6StartTime,"(location: https://verificaSSLTestPD/openspcoop2/spcoop/PA)"));
			Assert.assertTrue(msgDiagComponent.isTracedMessaggioWithLike(this.testPD6StartTime,"Errore avvenuto durante la consegna HTTP: unknown host 'verificaSSLTestPD"));
			
		}catch(Exception e){
			throw e;
		}finally{
			try{
				data.close();
			}catch(Exception e){}
			try{
				msgDiagComponent.close();
			}catch(Exception e){}
		}
	}
	
	

	
	
	
	
	
	/***
	 * Test per pa-url-prefix-rewriter="https://verificaSSLTestPA"
	 */
	Date testPA6StartTime = null;
	Repository repository_testPA6=new Repository();
	@Test(groups={UrlPrefixRewriter.ID_GRUPPO,UrlPrefixRewriter.ID_GRUPPO+".PA_6"})
	public void testPA6()throws TestSuiteException,SOAPException, Exception{
		
		Date dataInizioTest = DateManager.getDate();
		
		this.testPA6StartTime = new Date();
		
		String egov=UtilitiesEGov.getIDEGov(CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE,
				CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE+CostantiTestSuite.SPCOOP_PORTA_DOMINIO);
		this.repository_testPA6.add(egov);
		String bustaSOAP=UtilitiesEGov.getBustaEGov(CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_FRUITORE,
				CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE,
				CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_TEST_URL_PREFIX_REWRITER_6,
				CostantiTestSuite.SPCOOP_NOME_SOGGETTO_TEST_URL_PREFIX_REWRITER_6,
				SPCoopCostanti.PROFILO_COLLABORAZIONE_SINCRONO,
				CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
				CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
				egov,
				false,
				SPCoopCostanti.PROFILO_TRASMISSIONE_SENZA_DUPLICATI,
				false,
				null);

		// costruzione busta
		java.io.ByteArrayInputStream bin =
			new java.io.ByteArrayInputStream(bustaSOAP.getBytes());
		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();

		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repository_testPA6);
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneBusteErogatore());
			client.connectToSoapEngine();
			client.setMessage(msg);
			client.setRispostaDaGestire(true);
			// AttesaTerminazioneMessaggi
			if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()){
				dbComponentErogatore = DatabaseProperties.getDatabaseComponentErogatore();

				client.setAttesaTerminazioneMessaggi(true);
				client.setDbAttesaTerminazioneMessaggiErogatore(dbComponentErogatore);
			}
			try {
				client.run();
				if(Utilities.testSuiteProperties.isNewConnectionForResponse()==false){
					Reporter.log("Invocazione PA.");
					throw new TestSuiteException("Invocazione PA, non ha causato errori.");
				}
			} catch (AxisFault error) {
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				Reporter.log("Controllo fault code [Server]");
				Assert.assertTrue("Server".equals(error.getFaultCode().getLocalPart()));
				Reporter.log("Controllo fault string ["+SPCoopCostanti.FAULT_STRING_PROCESSAMENTO_SPCOOP+"]");
				Assert.assertTrue(SPCoopCostanti.FAULT_STRING_PROCESSAMENTO_SPCOOP.equals(error.getFaultString()));
			}finally{
				dbComponentErogatore.close();
			}
		}catch(Exception e){
			throw e;
		}finally{
			dbComponentErogatore.close();
		}
		
		Date dataFineTest = DateManager.getDate();
		
		ErroreAttesoOpenSPCoopLogCore err = new ErroreAttesoOpenSPCoopLogCore();
		err.setIntervalloInferiore(dataInizioTest);
		err.setIntervalloSuperiore(dataFineTest);
		err.setMsgErrore("Errore avvenuto durante la consegna HTTP: unknown host 'verificaSSLTestPA");
		this.erroriAttesiOpenSPCoopCore.add(err);
	}
	@DataProvider (name="testPA6Provider")
	public Object[][] testPA6Provider()throws Exception{
		String id=this.repository_testPA6.getNext();
		if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()==false){
			try {
				Thread.sleep(Utilities.testSuiteProperties.timeToSleep_verificaDatabase());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		// Attendo tempo perso per la nuova connessione
		//if(Utilities.testSuiteProperties.isNewConnectionForResponse()){
		try{
			Thread.sleep(3000);
		}catch(InterruptedException e){}
		//}	
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentErogatore(),DatabaseProperties.getDatabaseComponentDiagnosticaErogatore(),id}
		};
	}
	@Test(groups={UrlPrefixRewriter.ID_GRUPPO,UrlPrefixRewriter.ID_GRUPPO+".PA_6"},
			dataProvider="testPA6Provider",
			dependsOnMethods="testPA6")
			public void testPA6VerificaTracce(DatabaseComponent data,DatabaseMsgDiagnosticiComponent msgDiagComponent,String id) throws Exception{
		try{
			Reporter.log("Controllo tracciamento richiesta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
			Reporter.log("Controllo valore Mittente Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id, CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE , null));
			Reporter.log("Controllo valore Destinatario Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedDestinatario(id, CostantiTestSuite.SPCOOP_SOGGETTO_TEST_URL_PREFIX_REWRITER_6, null));
			Reporter.log("Controllo valore OraRegistrazione con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedOraRegistrazione(id));

			Reporter.log("Controllo valore Servizio Busta con id: " +id);
			DatiServizio datiServizio = new DatiServizio(CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO, CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO, CostantiTestSuite.SPCOOP_VERSIONE_SERVIZIO_DEFAULT);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedServizio(id, datiServizio));
			Reporter.log("Controllo valore Azione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedAzione(id, null));
			Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloDiCollaborazione(id, SPCoopCostanti.PROFILO_COLLABORAZIONE_SINCRONO,ProfiloDiCollaborazione.SINCRONO));
			Reporter.log("Controllo valore Profilo di Trasmissione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloTrasmissione(id, false,SPCoopCostanti.PROFILO_TRASMISSIONE_SENZA_DUPLICATI,Inoltro.SENZA_DUPLICATI));
			//Reporter.log("Controllo che la busta abbia generato eccezioni, id: " +id);
			//Assert.assertTrue(data.getVerificatoreTracciaRisposta().existsListaEccezioni(id)==true);
			Reporter.log("Controllo lista trasmissione con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedTrasmissione(id, 
					CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE, null ,
					CostantiTestSuite.SPCOOP_SOGGETTO_TEST_URL_PREFIX_REWRITER_6, null,
					true));
			Reporter.log("Numero messaggi arrivati al servizio applicativo: "+data.getVerificatoreTracciaRichiesta().isArrivedCount(id));
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isArrivedCount(id)==0);

			Reporter.log("----------------------------------------------------------");

			Reporter.log("Controllo tracciamento risposta con riferimento id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTraced(id));
			Reporter.log("Controllo valore Mittente Busta della risposta con riferimento id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedMittente(id, CostantiTestSuite.SPCOOP_SOGGETTO_TEST_URL_PREFIX_REWRITER_6, null));
			Reporter.log("Controllo valore Destinatario Busta della risposta con riferimento id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedDestinatario(id, CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE , null));
			Reporter.log("Controllo valore OraRegistrazione con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedOraRegistrazione(id));
	
			Reporter.log("Controllo valore Servizio Busta della risposta con riferimento id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedServizio(id, datiServizio));
			Reporter.log("Controllo valore Azione Busta della risposta con riferimento id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedAzione(id, null));
			Reporter.log("Controllo valore Profilo di Collaborazione Busta della risposta con riferimento id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedProfiloDiCollaborazione(id, SPCoopCostanti.PROFILO_COLLABORAZIONE_SINCRONO,ProfiloDiCollaborazione.SINCRONO));
			Reporter.log("Controllo valore Profilo di Trasmissione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedProfiloTrasmissione(id,false,SPCoopCostanti.PROFILO_TRASMISSIONE_SENZA_DUPLICATI,Inoltro.SENZA_DUPLICATI));
			//Reporter.log("Controllo che la busta abbia generato eccezioni, riferimento messaggio: " +id);
			//Assert.assertTrue(data.getVerificatoreTracciaRisposta().existsListaEccezioni(id)==true);
			Reporter.log("Controllo lista trasmissione, riferimento messaggio: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedTrasmissione(id, 
					CostantiTestSuite.SPCOOP_SOGGETTO_TEST_URL_PREFIX_REWRITER_6, null,
					CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE, null,
					true));
		
			//Reporter.log("Controllo che la busta abbia generato l'eccezione " + Costanti.ECCEZIONE_PROCESSAMENTO_MESSAGGIO + " rappresentante un servizio applicativo non disponibile");
			//Assert.assertTrue(data.isTracedEccezione(id, Costanti.ECCEZIONE_PROCESSAMENTO_MESSAGGIO));
			
			Reporter.log("Controllo esistenza diagnostico [ https://verificaSSLTestPA]");
			Assert.assertTrue(msgDiagComponent.isTracedMessaggioWithLike(id,"(location: https://verificaSSLTestPA/OpenSPCoop2TestSuite/server)"));
			Assert.assertTrue(msgDiagComponent.isTracedMessaggioWithLike(id,"Errore avvenuto durante la consegna HTTP: unknown host 'verificaSSLTestPA"));
			
		}catch(Exception e){
			throw e;
		}finally{
			try{
				data.close();
			}catch(Exception e){}
			try{
				msgDiagComponent.close();
			}catch(Exception e){}
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/* ************************ TEST 7, prefissi corretti http://127.0.0.1:8080 e http://127.0.0.1:8080/ ************************** */
	
	/***
	 * Test per pd-url-prefix-rewriter="http://127.0.0.1:8080"
	 */
	private CooperazioneBaseInformazioni info_testPD7 = CooperazioneSPCoopBase.getCooperazioneBaseInformazioni(CostantiTestSuite.SPCOOP_SOGGETTO_TEST_URL_PREFIX_REWRITER_7,
			CostantiTestSuite.SPCOOP_SOGGETTO_EROGATORE,
			false,SPCoopCostanti.PROFILO_TRASMISSIONE_CON_DUPLICATI,Inoltro.CON_DUPLICATI);	
	private CooperazioneBase collaborazioneSPCoopBase_testPD7 = 
		new CooperazioneBase(false, SOAPVersion.SOAP11, this.info_testPD7, 
				org.openspcoop2.protocol.spcoop.testsuite.core.TestSuiteProperties.getInstance(), 
				DatabaseProperties.getInstance(), SPCoopTestsuiteLogger.getInstance());

	Repository repository_testPD7=new Repository();
	@Test(groups={UrlPrefixRewriter.ID_GRUPPO,UrlPrefixRewriter.ID_GRUPPO+".PD_7"})
	public void testPD7() throws TestSuiteException, IOException, SOAPException{
		this.collaborazioneSPCoopBase_testPD7.sincrono(this.repository_testPD7,CostantiTestSuite.PORTA_DELEGATA_TEST_URL_PREFIX_REWRITER_7,true);
	}
	@DataProvider (name="testPD7Provider")
	public Object[][]testPD7Provider()throws Exception{
		String id=this.repository_testPD7.getNext();
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),DatabaseProperties.getDatabaseComponentDiagnosticaFruitore(),id,false},	
				{DatabaseProperties.getDatabaseComponentErogatore(),DatabaseProperties.getDatabaseComponentDiagnosticaErogatore(),id,true}	
		};
	}
	@Test(groups={UrlPrefixRewriter.ID_GRUPPO,UrlPrefixRewriter.ID_GRUPPO+".PD_7"},dataProvider="testPD7Provider",dependsOnMethods={"testPD7"})
	public void testPD7VerificaTracce(DatabaseComponent data,DatabaseMsgDiagnosticiComponent msgDiagComponent,String id,boolean checkServizioApplicativo) throws Exception{
		try{
			this.collaborazioneSPCoopBase_testPD7.testSincrono(data, id, CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,null, checkServizioApplicativo,
					null);
			
			if(checkServizioApplicativo == false){
				// fruitore
				Reporter.log("Controllo esistenza diagnostico [http://127.0.0.1:8080/openspcoop2/spcoop/PA]");
				Assert.assertTrue(msgDiagComponent.isTracedMessaggioWithLike(id,"(location: http://127.0.0.1:8080/openspcoop2/spcoop/PA)"));
			}
			
		}catch(Exception e){
			throw e;
		}finally{
			try{
				data.close();
			}catch(Exception e){}
			try{
				msgDiagComponent.close();
			}catch(Exception e){}
		}
	}
	
	
	

	
	/***
	 * Test per pa-url-prefix-rewriter="http://127.0.0.1:8080/"
	 */
	Repository repository_testPA7=new Repository();
	@Test(groups={UrlPrefixRewriter.ID_GRUPPO,UrlPrefixRewriter.ID_GRUPPO+".PA_7"})
	public void testPA7()throws TestSuiteException,SOAPException, Exception{
		String egov=UtilitiesEGov.getIDEGov(CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE,
				CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE+CostantiTestSuite.SPCOOP_PORTA_DOMINIO);
		this.repository_testPA7.add(egov);
		String bustaSOAP=UtilitiesEGov.getBustaEGov(CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_FRUITORE,
				CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE,
				CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_TEST_URL_PREFIX_REWRITER_7,
				CostantiTestSuite.SPCOOP_NOME_SOGGETTO_TEST_URL_PREFIX_REWRITER_7,
				SPCoopCostanti.PROFILO_COLLABORAZIONE_SINCRONO,
				CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
				CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
				egov,
				false,
				SPCoopCostanti.PROFILO_TRASMISSIONE_SENZA_DUPLICATI,
				false,
				null);

		// costruzione busta
		java.io.ByteArrayInputStream bin =
			new java.io.ByteArrayInputStream(bustaSOAP.getBytes());
		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();

		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repository_testPA7);
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneBusteErogatore());
			client.connectToSoapEngine();
			client.setMessage(msg);
			client.setRispostaDaGestire(true);
			// AttesaTerminazioneMessaggi
			if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()){
				dbComponentErogatore = DatabaseProperties.getDatabaseComponentErogatore();

				client.setAttesaTerminazioneMessaggi(true);
				client.setDbAttesaTerminazioneMessaggiErogatore(dbComponentErogatore);
			}
			try {
				client.run();
			} catch (AxisFault error) {
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				throw error;
			}finally{
				dbComponentErogatore.close();
			}
		}catch(Exception e){
			throw e;
		}finally{
			dbComponentErogatore.close();
		}
	}
	@DataProvider (name="testPA7Provider")
	public Object[][] testPA7Provider()throws Exception{
		String id=this.repository_testPA7.getNext();
		if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()==false){
			try {
				Thread.sleep(Utilities.testSuiteProperties.timeToSleep_verificaDatabase());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		// Attendo tempo perso per la nuova connessione
		//if(Utilities.testSuiteProperties.isNewConnectionForResponse()){
		try{
			Thread.sleep(3000);
		}catch(InterruptedException e){}
		//}	
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentErogatore(),DatabaseProperties.getDatabaseComponentDiagnosticaErogatore(),id}
		};
	}
	@Test(groups={UrlPrefixRewriter.ID_GRUPPO,UrlPrefixRewriter.ID_GRUPPO+".PA_7"},
			dataProvider="testPA7Provider",
			dependsOnMethods="testPA7")
			public void testPA7VerificaTracce(DatabaseComponent data,DatabaseMsgDiagnosticiComponent msgDiagComponent,String id) throws Exception{
		try{
			Reporter.log("Controllo tracciamento richiesta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
			Reporter.log("Controllo valore Mittente Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id, CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE , null));
			Reporter.log("Controllo valore Destinatario Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedDestinatario(id, CostantiTestSuite.SPCOOP_SOGGETTO_TEST_URL_PREFIX_REWRITER_7, null));
			Reporter.log("Controllo valore OraRegistrazione con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedOraRegistrazione(id));

			Reporter.log("Controllo valore Servizio Busta con id: " +id);
			DatiServizio datiServizio = new DatiServizio(CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO, CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO, CostantiTestSuite.SPCOOP_VERSIONE_SERVIZIO_DEFAULT);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedServizio(id, datiServizio));
			Reporter.log("Controllo valore Azione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedAzione(id, null));
			Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloDiCollaborazione(id, SPCoopCostanti.PROFILO_COLLABORAZIONE_SINCRONO,ProfiloDiCollaborazione.SINCRONO));
			Reporter.log("Controllo valore Profilo di Trasmissione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloTrasmissione(id, false,SPCoopCostanti.PROFILO_TRASMISSIONE_SENZA_DUPLICATI,Inoltro.SENZA_DUPLICATI));
			Reporter.log("Controllo che la busta non abbia generato eccezioni, id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().existsListaEccezioni(id)==false);
			Reporter.log("Controllo lista trasmissione con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedTrasmissione(id, 
					CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE, null ,
					CostantiTestSuite.SPCOOP_SOGGETTO_TEST_URL_PREFIX_REWRITER_7, null,
					true));
			Reporter.log("Numero messaggi arrivati al servizio applicativo: "+data.getVerificatoreTracciaRichiesta().isArrivedCount(id));
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isArrivedCount(id)==1);

			Reporter.log("----------------------------------------------------------");

			Reporter.log("Controllo tracciamento risposta con riferimento id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTraced(id));
			Reporter.log("Controllo valore Mittente Busta della risposta con riferimento id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedMittente(id, CostantiTestSuite.SPCOOP_SOGGETTO_TEST_URL_PREFIX_REWRITER_7, null));
			Reporter.log("Controllo valore Destinatario Busta della risposta con riferimento id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedDestinatario(id, CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE , null));
			Reporter.log("Controllo valore OraRegistrazione con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedOraRegistrazione(id));
	
			Reporter.log("Controllo valore Servizio Busta della risposta con riferimento id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedServizio(id, datiServizio));
			Reporter.log("Controllo valore Azione Busta della risposta con riferimento id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedAzione(id, null));
			Reporter.log("Controllo valore Profilo di Collaborazione Busta della risposta con riferimento id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedProfiloDiCollaborazione(id, SPCoopCostanti.PROFILO_COLLABORAZIONE_SINCRONO,ProfiloDiCollaborazione.SINCRONO));
			Reporter.log("Controllo valore Profilo di Trasmissione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedProfiloTrasmissione(id,false,SPCoopCostanti.PROFILO_TRASMISSIONE_SENZA_DUPLICATI,Inoltro.SENZA_DUPLICATI));
			Reporter.log("Controllo che la busta non abbia generato eccezioni, riferimento messaggio: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().existsListaEccezioni(id)==false);
			Reporter.log("Controllo lista trasmissione, riferimento messaggio: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedTrasmissione(id, 
					CostantiTestSuite.SPCOOP_SOGGETTO_TEST_URL_PREFIX_REWRITER_7, null,
					CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE, null,
					true));
				
			Reporter.log("Controllo esistenza diagnostico [http://127.0.0.1:8080/OpenSPCoop2TestSuite/server]");
			Assert.assertTrue(msgDiagComponent.isTracedMessaggioWithLike(id,"(location: http://127.0.0.1:8080/OpenSPCoop2TestSuite/server)"));
			
		}catch(Exception e){
			throw e;
		}finally{
			try{
				data.close();
			}catch(Exception e){}
			try{
				msgDiagComponent.close();
			}catch(Exception e){}
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/* ************************ TEST 8   http://127.0.0.3 e  http://127.0.0.3 **************************


	/***
	 * Test per pd-url-prefix-rewriter="http://127.0.0.3"
	 */
	Date testPD8StartTime = null;
	Repository repository_testPD8=new Repository();
	@Test(groups={UrlPrefixRewriter.ID_GRUPPO,UrlPrefixRewriter.ID_GRUPPO+".PD_8"})
	public void testPD8() throws Exception{
		
		Date dataInizioTest = DateManager.getDate();
		
		DatabaseComponent dbComponentFruitore = null;
		DatabaseComponent dbComponentErogatore = null;

		this.testPD8StartTime = new Date();
		
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(new Repository());
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_TEST_URL_PREFIX_REWRITER_8);
			client.connectToSoapEngine();
			client.setMessageFromFile(Utilities.testSuiteProperties.getSoap11FileName(), false);
			client.setRispostaDaGestire(true);
			// AttesaTerminazioneMessaggi
			if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()){
				dbComponentFruitore = DatabaseProperties.getDatabaseComponentFruitore();
				dbComponentErogatore = DatabaseProperties.getDatabaseComponentErogatore();

				client.setAttesaTerminazioneMessaggi(true);
				client.setDbAttesaTerminazioneMessaggiFruitore(dbComponentFruitore);
				client.setDbAttesaTerminazioneMessaggiErogatore(dbComponentErogatore);
			}
			try {
				client.run();

				Reporter.log("Invocazione porta delegata con identificazione dinamica dei dati non ha causato errori.");
				throw new TestSuiteException("Invocazione porta delegata con identificazione dinamica dei dati non ha causato errori.");

			} catch (AxisFault error) {
				
				Assert.assertTrue(client.getCodiceStatoHTTP()==500);
				
				Utilities.verificaFaultIntegrazione(error, 
						CostantiTestSuite.SPCOOP_NOME_SOGGETTO_TEST_URL_PREFIX_REWRITER_8+"SPCoopIT",InoltroBuste.ID_MODULO, 
						Utilities.toString(CodiceErroreIntegrazione.CODICE_516_CONNETTORE_UTILIZZO_CON_ERRORE), 
						CostantiErroriIntegrazione.MSG_516_PDD_NON_DISPONIBILE.replace(CostantiProtocollo.KEYWORDPDD_NON_DISPONIBILE, CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_EROGATORE+CostantiTestSuite.SPCOOP_NOME_SOGGETTO_EROGATORE), 
						Utilities.CONTROLLO_DESCRIZIONE_TRAMITE_METODO_EQUALS);	
			}finally{
				dbComponentFruitore.close();
				dbComponentErogatore.close();
			}
		}catch(Exception e){
			throw e;
		}finally{
			dbComponentFruitore.close();
			dbComponentErogatore.close();
		}
		
		Date dataFineTest = DateManager.getDate();
		
		ErroreAttesoOpenSPCoopLogCore err = new ErroreAttesoOpenSPCoopLogCore();
		err.setIntervalloInferiore(dataInizioTest);
		err.setIntervalloSuperiore(dataFineTest);
		err.setMsgErrore("Errore avvenuto durante la consegna HTTP: Connection refused");
		this.erroriAttesiOpenSPCoopCore.add(err);
	}
	@DataProvider (name="testPD8Provider")
	public Object[][]testPD8Provider()throws Exception{
		String id=this.repository_testPD8.getNext();
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),DatabaseProperties.getDatabaseComponentDiagnosticaFruitore(),id}	
		};
	}
	@Test(groups={UrlPrefixRewriter.ID_GRUPPO,UrlPrefixRewriter.ID_GRUPPO+".PD_8"},dataProvider="testPD8Provider",dependsOnMethods={"testPD8"})
	public void testPD8VerificaTracce(DatabaseComponent data,DatabaseMsgDiagnosticiComponent msgDiagComponent,String id) throws Exception{
		try{
			
			Reporter.log("Controllo tracciamento richiesta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id)==false);
			
			Reporter.log("Controllo esistenza diagnostico [https://verificaSSLTestPD]");
			Assert.assertTrue(msgDiagComponent.isTracedMessaggioWithLike(this.testPD8StartTime,"(location: http://127.0.0.3/openspcoop2/spcoop/PA)"));
			Assert.assertTrue(msgDiagComponent.isTracedMessaggioWithLike(this.testPD8StartTime,"Errore avvenuto durante la consegna HTTP: Connection refused"));
			
		}catch(Exception e){
			throw e;
		}finally{
			try{
				data.close();
			}catch(Exception e){}
			try{
				msgDiagComponent.close();
			}catch(Exception e){}
		}
	}
	
	

	
	
	
	
	
	/***
	 * Test per pa-url-prefix-rewriter="http://127.0.0.3"
	 */
	Date testPA8StartTime = null;
	Repository repository_testPA8=new Repository();
	@Test(groups={UrlPrefixRewriter.ID_GRUPPO,UrlPrefixRewriter.ID_GRUPPO+".PA_8"})
	public void testPA8()throws TestSuiteException,SOAPException, Exception{
		
		Date dataInizioTest = DateManager.getDate();
		
		this.testPA8StartTime = new Date();
		
		String egov=UtilitiesEGov.getIDEGov(CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE,
				CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE+CostantiTestSuite.SPCOOP_PORTA_DOMINIO);
		this.repository_testPA8.add(egov);
		String bustaSOAP=UtilitiesEGov.getBustaEGov(CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_FRUITORE,
				CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE,
				CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_TEST_URL_PREFIX_REWRITER_8,
				CostantiTestSuite.SPCOOP_NOME_SOGGETTO_TEST_URL_PREFIX_REWRITER_8,
				SPCoopCostanti.PROFILO_COLLABORAZIONE_SINCRONO,
				CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
				CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
				egov,
				false,
				SPCoopCostanti.PROFILO_TRASMISSIONE_SENZA_DUPLICATI,
				false,
				null);

		// costruzione busta
		java.io.ByteArrayInputStream bin =
			new java.io.ByteArrayInputStream(bustaSOAP.getBytes());
		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();

		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repository_testPA8);
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneBusteErogatore());
			client.connectToSoapEngine();
			client.setMessage(msg);
			client.setRispostaDaGestire(true);
			// AttesaTerminazioneMessaggi
			if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()){
				dbComponentErogatore = DatabaseProperties.getDatabaseComponentErogatore();

				client.setAttesaTerminazioneMessaggi(true);
				client.setDbAttesaTerminazioneMessaggiErogatore(dbComponentErogatore);
			}
			try {
				client.run();
				if(Utilities.testSuiteProperties.isNewConnectionForResponse()==false){
					Reporter.log("Invocazione PA.");
					throw new TestSuiteException("Invocazione PA, non ha causato errori.");
				}
			} catch (AxisFault error) {
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				Reporter.log("Controllo fault code [Server]");
				Assert.assertTrue("Server".equals(error.getFaultCode().getLocalPart()));
				Reporter.log("Controllo fault string ["+SPCoopCostanti.FAULT_STRING_PROCESSAMENTO_SPCOOP+"]");
				Assert.assertTrue(SPCoopCostanti.FAULT_STRING_PROCESSAMENTO_SPCOOP.equals(error.getFaultString()));
			}finally{
				dbComponentErogatore.close();
			}
		}catch(Exception e){
			throw e;
		}finally{
			dbComponentErogatore.close();
		}
		
		Date dataFineTest = DateManager.getDate();
		
		ErroreAttesoOpenSPCoopLogCore err = new ErroreAttesoOpenSPCoopLogCore();
		err.setIntervalloInferiore(dataInizioTest);
		err.setIntervalloSuperiore(dataFineTest);
		err.setMsgErrore("Errore avvenuto durante la consegna HTTP: Connection refused");
		this.erroriAttesiOpenSPCoopCore.add(err);
	}
	@DataProvider (name="testPA8Provider")
	public Object[][] testPA8Provider()throws Exception{
		String id=this.repository_testPA8.getNext();
		if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()==false){
			try {
				Thread.sleep(Utilities.testSuiteProperties.timeToSleep_verificaDatabase());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		// Attendo tempo perso per la nuova connessione
		//if(Utilities.testSuiteProperties.isNewConnectionForResponse()){
		try{
			Thread.sleep(3000);
		}catch(InterruptedException e){}
		//}	
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentErogatore(),DatabaseProperties.getDatabaseComponentDiagnosticaErogatore(),id}
		};
	}
	@Test(groups={UrlPrefixRewriter.ID_GRUPPO,UrlPrefixRewriter.ID_GRUPPO+".PA_8"},
			dataProvider="testPA8Provider",
			dependsOnMethods="testPA8")
			public void testPA8VerificaTracce(DatabaseComponent data,DatabaseMsgDiagnosticiComponent msgDiagComponent,String id) throws Exception{
		try{
			Reporter.log("Controllo tracciamento richiesta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
			Reporter.log("Controllo valore Mittente Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id, CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE , null));
			Reporter.log("Controllo valore Destinatario Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedDestinatario(id, CostantiTestSuite.SPCOOP_SOGGETTO_TEST_URL_PREFIX_REWRITER_8, null));
			Reporter.log("Controllo valore OraRegistrazione con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedOraRegistrazione(id));

			Reporter.log("Controllo valore Servizio Busta con id: " +id);
			DatiServizio datiServizio = new DatiServizio(CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO, CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO, CostantiTestSuite.SPCOOP_VERSIONE_SERVIZIO_DEFAULT);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedServizio(id, datiServizio));
			Reporter.log("Controllo valore Azione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedAzione(id, null));
			Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloDiCollaborazione(id, SPCoopCostanti.PROFILO_COLLABORAZIONE_SINCRONO,ProfiloDiCollaborazione.SINCRONO));
			Reporter.log("Controllo valore Profilo di Trasmissione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloTrasmissione(id, false,SPCoopCostanti.PROFILO_TRASMISSIONE_SENZA_DUPLICATI,Inoltro.SENZA_DUPLICATI));
			//Reporter.log("Controllo che la busta abbia generato eccezioni, id: " +id);
			//Assert.assertTrue(data.getVerificatoreTracciaRisposta().existsListaEccezioni(id)==true);
			Reporter.log("Controllo lista trasmissione con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedTrasmissione(id, 
					CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE, null ,
					CostantiTestSuite.SPCOOP_SOGGETTO_TEST_URL_PREFIX_REWRITER_8, null,
					true));
			Reporter.log("Numero messaggi arrivati al servizio applicativo: "+data.getVerificatoreTracciaRichiesta().isArrivedCount(id));
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isArrivedCount(id)==0);

			Reporter.log("----------------------------------------------------------");

			Reporter.log("Controllo tracciamento risposta con riferimento id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTraced(id));
			Reporter.log("Controllo valore Mittente Busta della risposta con riferimento id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedMittente(id, CostantiTestSuite.SPCOOP_SOGGETTO_TEST_URL_PREFIX_REWRITER_8, null));
			Reporter.log("Controllo valore Destinatario Busta della risposta con riferimento id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedDestinatario(id, CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE , null));
			Reporter.log("Controllo valore OraRegistrazione con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedOraRegistrazione(id));
	
			Reporter.log("Controllo valore Servizio Busta della risposta con riferimento id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedServizio(id, datiServizio));
			Reporter.log("Controllo valore Azione Busta della risposta con riferimento id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedAzione(id, null));
			Reporter.log("Controllo valore Profilo di Collaborazione Busta della risposta con riferimento id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedProfiloDiCollaborazione(id, SPCoopCostanti.PROFILO_COLLABORAZIONE_SINCRONO,ProfiloDiCollaborazione.SINCRONO));
			Reporter.log("Controllo valore Profilo di Trasmissione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedProfiloTrasmissione(id,false,SPCoopCostanti.PROFILO_TRASMISSIONE_SENZA_DUPLICATI,Inoltro.SENZA_DUPLICATI));
			//Reporter.log("Controllo che la busta abbia generato eccezioni, riferimento messaggio: " +id);
			//Assert.assertTrue(data.getVerificatoreTracciaRisposta().existsListaEccezioni(id)==true);
			Reporter.log("Controllo lista trasmissione, riferimento messaggio: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedTrasmissione(id, 
					CostantiTestSuite.SPCOOP_SOGGETTO_TEST_URL_PREFIX_REWRITER_8, null,
					CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE, null,
					true));
		
			//Reporter.log("Controllo che la busta abbia generato l'eccezione " + Costanti.ECCEZIONE_PROCESSAMENTO_MESSAGGIO + " rappresentante un servizio applicativo non disponibile");
			//Assert.assertTrue(data.isTracedEccezione(id, Costanti.ECCEZIONE_PROCESSAMENTO_MESSAGGIO));
			
			Reporter.log("Controllo esistenza diagnostico [ https://verificaSSLTestPA]");
			Assert.assertTrue(msgDiagComponent.isTracedMessaggioWithLike(id,"(location: http://127.0.0.3/OpenSPCoop2TestSuite/server)"));
			Assert.assertTrue(msgDiagComponent.isTracedMessaggioWithLike(id,"Errore avvenuto durante la consegna HTTP: Connection refused"));
			
		}catch(Exception e){
			throw e;
		}finally{
			try{
				data.close();
			}catch(Exception e){}
			try{
				msgDiagComponent.close();
			}catch(Exception e){}
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/* ************************ TEST 9   http://127.0.0.3:567 e  http://127.0.0.3:567 **************************


	/***
	 * Test per pd-url-prefix-rewriter="http://127.0.0.3:567"
	 */
	Date testPD9StartTime = null;
	Repository repository_testPD9=new Repository();
	@Test(groups={UrlPrefixRewriter.ID_GRUPPO,UrlPrefixRewriter.ID_GRUPPO+".PD_9"})
	public void testPD9() throws Exception{
		
		Date dataInizioTest = DateManager.getDate();
		
		DatabaseComponent dbComponentFruitore = null;
		DatabaseComponent dbComponentErogatore = null;

		this.testPD9StartTime = new Date();
		
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(new Repository());
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_TEST_URL_PREFIX_REWRITER_9);
			client.connectToSoapEngine();
			client.setMessageFromFile(Utilities.testSuiteProperties.getSoap11FileName(), false);
			client.setRispostaDaGestire(true);
			// AttesaTerminazioneMessaggi
			if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()){
				dbComponentFruitore = DatabaseProperties.getDatabaseComponentFruitore();
				dbComponentErogatore = DatabaseProperties.getDatabaseComponentErogatore();

				client.setAttesaTerminazioneMessaggi(true);
				client.setDbAttesaTerminazioneMessaggiFruitore(dbComponentFruitore);
				client.setDbAttesaTerminazioneMessaggiErogatore(dbComponentErogatore);
			}
			try {
				client.run();

				Reporter.log("Invocazione porta delegata con identificazione dinamica dei dati non ha causato errori.");
				throw new TestSuiteException("Invocazione porta delegata con identificazione dinamica dei dati non ha causato errori.");

			} catch (AxisFault error) {
				
				Assert.assertTrue(client.getCodiceStatoHTTP()==500);
				
				Utilities.verificaFaultIntegrazione(error, 
						CostantiTestSuite.SPCOOP_NOME_SOGGETTO_TEST_URL_PREFIX_REWRITER_9+"SPCoopIT",InoltroBuste.ID_MODULO, 
						Utilities.toString(CodiceErroreIntegrazione.CODICE_516_CONNETTORE_UTILIZZO_CON_ERRORE), 
						CostantiErroriIntegrazione.MSG_516_PDD_NON_DISPONIBILE.replace(CostantiProtocollo.KEYWORDPDD_NON_DISPONIBILE, CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_EROGATORE+CostantiTestSuite.SPCOOP_NOME_SOGGETTO_EROGATORE), 
						Utilities.CONTROLLO_DESCRIZIONE_TRAMITE_METODO_EQUALS);	
			}finally{
				dbComponentFruitore.close();
				dbComponentErogatore.close();
			}
		}catch(Exception e){
			throw e;
		}finally{
			dbComponentFruitore.close();
			dbComponentErogatore.close();
		}
		
		Date dataFineTest = DateManager.getDate();
		
		ErroreAttesoOpenSPCoopLogCore err = new ErroreAttesoOpenSPCoopLogCore();
		err.setIntervalloInferiore(dataInizioTest);
		err.setIntervalloSuperiore(dataFineTest);
		err.setMsgErrore("Errore avvenuto durante la consegna HTTP: Connection refused");
		this.erroriAttesiOpenSPCoopCore.add(err);
	}
	@DataProvider (name="testPD9Provider")
	public Object[][]testPD9Provider()throws Exception{
		String id=this.repository_testPD9.getNext();
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),DatabaseProperties.getDatabaseComponentDiagnosticaFruitore(),id}	
		};
	}
	@Test(groups={UrlPrefixRewriter.ID_GRUPPO,UrlPrefixRewriter.ID_GRUPPO+".PD_9"},dataProvider="testPD9Provider",dependsOnMethods={"testPD9"})
	public void testPD9VerificaTracce(DatabaseComponent data,DatabaseMsgDiagnosticiComponent msgDiagComponent,String id) throws Exception{
		try{
			
			Reporter.log("Controllo tracciamento richiesta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id)==false);
			
			Reporter.log("Controllo esistenza diagnostico [https://verificaSSLTestPD]");
			Assert.assertTrue(msgDiagComponent.isTracedMessaggioWithLike(this.testPD9StartTime,"(location: http://127.0.0.3:567/openspcoop2/spcoop/PA)"));
			Assert.assertTrue(msgDiagComponent.isTracedMessaggioWithLike(this.testPD9StartTime,"Errore avvenuto durante la consegna HTTP: Connection refused"));
			
		}catch(Exception e){
			throw e;
		}finally{
			try{
				data.close();
			}catch(Exception e){}
			try{
				msgDiagComponent.close();
			}catch(Exception e){}
		}
	}
	
	

	
	
	
	
	
	/***
	 * Test per pa-url-prefix-rewriter="http://127.0.0.3:567"
	 */
	Date testPA9StartTime = null;
	Repository repository_testPA9=new Repository();
	@Test(groups={UrlPrefixRewriter.ID_GRUPPO,UrlPrefixRewriter.ID_GRUPPO+".PA_9"})
	public void testPA9()throws TestSuiteException,SOAPException, Exception{
		
		Date dataInizioTest = DateManager.getDate();
		
		this.testPA9StartTime = new Date();
		
		String egov=UtilitiesEGov.getIDEGov(CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE,
				CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE+CostantiTestSuite.SPCOOP_PORTA_DOMINIO);
		this.repository_testPA9.add(egov);
		String bustaSOAP=UtilitiesEGov.getBustaEGov(CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_FRUITORE,
				CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE,
				CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_TEST_URL_PREFIX_REWRITER_9,
				CostantiTestSuite.SPCOOP_NOME_SOGGETTO_TEST_URL_PREFIX_REWRITER_9,
				SPCoopCostanti.PROFILO_COLLABORAZIONE_SINCRONO,
				CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
				CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
				egov,
				false,
				SPCoopCostanti.PROFILO_TRASMISSIONE_SENZA_DUPLICATI,
				false,
				null);

		// costruzione busta
		java.io.ByteArrayInputStream bin =
			new java.io.ByteArrayInputStream(bustaSOAP.getBytes());
		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();

		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repository_testPA9);
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneBusteErogatore());
			client.connectToSoapEngine();
			client.setMessage(msg);
			client.setRispostaDaGestire(true);
			// AttesaTerminazioneMessaggi
			if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()){
				dbComponentErogatore = DatabaseProperties.getDatabaseComponentErogatore();

				client.setAttesaTerminazioneMessaggi(true);
				client.setDbAttesaTerminazioneMessaggiErogatore(dbComponentErogatore);
			}
			try {
				client.run();
				if(Utilities.testSuiteProperties.isNewConnectionForResponse()==false){
					Reporter.log("Invocazione PA.");
					throw new TestSuiteException("Invocazione PA, non ha causato errori.");
				}
			} catch (AxisFault error) {
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				Reporter.log("Controllo fault code [Server]");
				Assert.assertTrue("Server".equals(error.getFaultCode().getLocalPart()));
				Reporter.log("Controllo fault string ["+SPCoopCostanti.FAULT_STRING_PROCESSAMENTO_SPCOOP+"]");
				Assert.assertTrue(SPCoopCostanti.FAULT_STRING_PROCESSAMENTO_SPCOOP.equals(error.getFaultString()));
			}finally{
				dbComponentErogatore.close();
			}
		}catch(Exception e){
			throw e;
		}finally{
			dbComponentErogatore.close();
		}
		
		Date dataFineTest = DateManager.getDate();
		
		ErroreAttesoOpenSPCoopLogCore err = new ErroreAttesoOpenSPCoopLogCore();
		err.setIntervalloInferiore(dataInizioTest);
		err.setIntervalloSuperiore(dataFineTest);
		err.setMsgErrore("Errore avvenuto durante la consegna HTTP: Connection refused");
		this.erroriAttesiOpenSPCoopCore.add(err);
	}
	@DataProvider (name="testPA9Provider")
	public Object[][] testPA9Provider()throws Exception{
		String id=this.repository_testPA9.getNext();
		if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()==false){
			try {
				Thread.sleep(Utilities.testSuiteProperties.timeToSleep_verificaDatabase());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		// Attendo tempo perso per la nuova connessione
		//if(Utilities.testSuiteProperties.isNewConnectionForResponse()){
		try{
			Thread.sleep(3000);
		}catch(InterruptedException e){}
		//}	
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentErogatore(),DatabaseProperties.getDatabaseComponentDiagnosticaErogatore(),id}
		};
	}
	@Test(groups={UrlPrefixRewriter.ID_GRUPPO,UrlPrefixRewriter.ID_GRUPPO+".PA_9"},
			dataProvider="testPA9Provider",
			dependsOnMethods="testPA9")
			public void testPA9VerificaTracce(DatabaseComponent data,DatabaseMsgDiagnosticiComponent msgDiagComponent,String id) throws Exception{
		try{
			Reporter.log("Controllo tracciamento richiesta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
			Reporter.log("Controllo valore Mittente Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id, CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE , null));
			Reporter.log("Controllo valore Destinatario Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedDestinatario(id, CostantiTestSuite.SPCOOP_SOGGETTO_TEST_URL_PREFIX_REWRITER_9, null));
			Reporter.log("Controllo valore OraRegistrazione con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedOraRegistrazione(id));

			Reporter.log("Controllo valore Servizio Busta con id: " +id);
			DatiServizio datiServizio = new DatiServizio(CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO, CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO, CostantiTestSuite.SPCOOP_VERSIONE_SERVIZIO_DEFAULT);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedServizio(id, datiServizio));
			Reporter.log("Controllo valore Azione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedAzione(id, null));
			Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloDiCollaborazione(id, SPCoopCostanti.PROFILO_COLLABORAZIONE_SINCRONO,ProfiloDiCollaborazione.SINCRONO));
			Reporter.log("Controllo valore Profilo di Trasmissione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloTrasmissione(id, false,SPCoopCostanti.PROFILO_TRASMISSIONE_SENZA_DUPLICATI,Inoltro.SENZA_DUPLICATI));
			//Reporter.log("Controllo che la busta abbia generato eccezioni, id: " +id);
			//Assert.assertTrue(data.getVerificatoreTracciaRisposta().existsListaEccezioni(id)==true);
			Reporter.log("Controllo lista trasmissione con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedTrasmissione(id, 
					CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE, null ,
					CostantiTestSuite.SPCOOP_SOGGETTO_TEST_URL_PREFIX_REWRITER_9, null,
					true));
			Reporter.log("Numero messaggi arrivati al servizio applicativo: "+data.getVerificatoreTracciaRichiesta().isArrivedCount(id));
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isArrivedCount(id)==0);

			Reporter.log("----------------------------------------------------------");

			Reporter.log("Controllo tracciamento risposta con riferimento id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTraced(id));
			Reporter.log("Controllo valore Mittente Busta della risposta con riferimento id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedMittente(id, CostantiTestSuite.SPCOOP_SOGGETTO_TEST_URL_PREFIX_REWRITER_9, null));
			Reporter.log("Controllo valore Destinatario Busta della risposta con riferimento id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedDestinatario(id, CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE , null));
			Reporter.log("Controllo valore OraRegistrazione con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedOraRegistrazione(id));
	
			Reporter.log("Controllo valore Servizio Busta della risposta con riferimento id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedServizio(id, datiServizio));
			Reporter.log("Controllo valore Azione Busta della risposta con riferimento id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedAzione(id, null));
			Reporter.log("Controllo valore Profilo di Collaborazione Busta della risposta con riferimento id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedProfiloDiCollaborazione(id, SPCoopCostanti.PROFILO_COLLABORAZIONE_SINCRONO,ProfiloDiCollaborazione.SINCRONO));
			Reporter.log("Controllo valore Profilo di Trasmissione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedProfiloTrasmissione(id,false,SPCoopCostanti.PROFILO_TRASMISSIONE_SENZA_DUPLICATI,Inoltro.SENZA_DUPLICATI));
			//Reporter.log("Controllo che la busta abbia generato eccezioni, riferimento messaggio: " +id);
			//Assert.assertTrue(data.getVerificatoreTracciaRisposta().existsListaEccezioni(id)==true);
			Reporter.log("Controllo lista trasmissione, riferimento messaggio: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedTrasmissione(id, 
					CostantiTestSuite.SPCOOP_SOGGETTO_TEST_URL_PREFIX_REWRITER_9, null,
					CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE, null,
					true));
		
			//Reporter.log("Controllo che la busta abbia generato l'eccezione " + Costanti.ECCEZIONE_PROCESSAMENTO_MESSAGGIO + " rappresentante un servizio applicativo non disponibile");
			//Assert.assertTrue(data.isTracedEccezione(id, Costanti.ECCEZIONE_PROCESSAMENTO_MESSAGGIO));
			
			Reporter.log("Controllo esistenza diagnostico [ https://verificaSSLTestPA]");
			Assert.assertTrue(msgDiagComponent.isTracedMessaggioWithLike(id,"(location: http://127.0.0.3:567/OpenSPCoop2TestSuite/server)"));
			Assert.assertTrue(msgDiagComponent.isTracedMessaggioWithLike(id,"Errore avvenuto durante la consegna HTTP: Connection refused"));
			
		}catch(Exception e){
			throw e;
		}finally{
			try{
				data.close();
			}catch(Exception e){}
			try{
				msgDiagComponent.close();
			}catch(Exception e){}
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/* ************************ TEST 10   otherProtocoll://hostnamePD e  otherProtocoll://hostnamePA**************************


	/***
	 * Test per pd-url-prefix-rewriter="otherProtocoll://hostnamePD"
	 */
	Date testPD10StartTime = null;
	Repository repository_testPD10=new Repository();
	@Test(groups={UrlPrefixRewriter.ID_GRUPPO,UrlPrefixRewriter.ID_GRUPPO+".PD_10"})
	public void testPD10() throws Exception{
		
		Date dataInizioTest = DateManager.getDate();
		
		DatabaseComponent dbComponentFruitore = null;
		DatabaseComponent dbComponentErogatore = null;

		this.testPD10StartTime = new Date();
		
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(new Repository());
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_TEST_URL_PREFIX_REWRITER_10);
			client.connectToSoapEngine();
			client.setMessageFromFile(Utilities.testSuiteProperties.getSoap11FileName(), false);
			client.setRispostaDaGestire(true);
			// AttesaTerminazioneMessaggi
			if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()){
				dbComponentFruitore = DatabaseProperties.getDatabaseComponentFruitore();
				dbComponentErogatore = DatabaseProperties.getDatabaseComponentErogatore();

				client.setAttesaTerminazioneMessaggi(true);
				client.setDbAttesaTerminazioneMessaggiFruitore(dbComponentFruitore);
				client.setDbAttesaTerminazioneMessaggiErogatore(dbComponentErogatore);
			}
			try {
				client.run();

				Reporter.log("Invocazione porta delegata con identificazione dinamica dei dati non ha causato errori.");
				throw new TestSuiteException("Invocazione porta delegata con identificazione dinamica dei dati non ha causato errori.");

			} catch (AxisFault error) {
				
				Assert.assertTrue(client.getCodiceStatoHTTP()==500);
				Utilities.verificaFaultIntegrazione(error, 
						CostantiTestSuite.SPCOOP_NOME_SOGGETTO_TEST_URL_PREFIX_REWRITER_10+"SPCoopIT",InoltroBuste.ID_MODULO, 
						Utilities.toString(CodiceErroreIntegrazione.CODICE_516_CONNETTORE_UTILIZZO_CON_ERRORE), 
						CostantiErroriIntegrazione.MSG_516_PDD_NON_DISPONIBILE.replace(CostantiProtocollo.KEYWORDPDD_NON_DISPONIBILE, CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_EROGATORE+CostantiTestSuite.SPCOOP_NOME_SOGGETTO_EROGATORE), 
						Utilities.CONTROLLO_DESCRIZIONE_TRAMITE_METODO_EQUALS);	
			}finally{
				dbComponentFruitore.close();
				dbComponentErogatore.close();
			}
		}catch(Exception e){
			throw e;
		}finally{
			dbComponentFruitore.close();
			dbComponentErogatore.close();
		}
		
		Date dataFineTest = DateManager.getDate();
		
		ErroreAttesoOpenSPCoopLogCore err = new ErroreAttesoOpenSPCoopLogCore();
		err.setIntervalloInferiore(dataInizioTest);
		err.setIntervalloSuperiore(dataFineTest);
		err.setMsgErrore("Errore avvenuto durante la consegna HTTP: unknown protocol: otherprotocoll");
		this.erroriAttesiOpenSPCoopCore.add(err);
	}
	@DataProvider (name="testPD10Provider")
	public Object[][]testPD10Provider()throws Exception{
		String id=this.repository_testPD10.getNext();
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),DatabaseProperties.getDatabaseComponentDiagnosticaFruitore(),id}	
		};
	}
	@Test(groups={UrlPrefixRewriter.ID_GRUPPO,UrlPrefixRewriter.ID_GRUPPO+".PD_10"},dataProvider="testPD10Provider",dependsOnMethods={"testPD10"})
	public void testPD10VerificaTracce(DatabaseComponent data,DatabaseMsgDiagnosticiComponent msgDiagComponent,String id) throws Exception{
		try{
			
			Reporter.log("Controllo tracciamento richiesta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id)==false);
			
			Reporter.log("Controllo esistenza diagnostico [https://verificaSSLTestPD]");
			Assert.assertTrue(msgDiagComponent.isTracedMessaggioWithLike(this.testPD10StartTime,"(location: otherProtocoll://hostnamePD/openspcoop2/spcoop/PA)"));
			Assert.assertTrue(msgDiagComponent.isTracedMessaggioWithLike(this.testPD10StartTime,"Errore avvenuto durante la consegna HTTP: unknown protocol: otherprotocoll"));
			
		}catch(Exception e){
			throw e;
		}finally{
			try{
				data.close();
			}catch(Exception e){}
			try{
				msgDiagComponent.close();
			}catch(Exception e){}
		}
	}
	
	

	
	
	
	
	
	/***
	 * Test per pa-url-prefix-rewriter="otherProtocoll://hostnamePA"
	 */
	Date testPA10StartTime = null;
	Repository repository_testPA10=new Repository();
	@Test(groups={UrlPrefixRewriter.ID_GRUPPO,UrlPrefixRewriter.ID_GRUPPO+".PA_10"})
	public void testPA10()throws TestSuiteException,SOAPException, Exception{
		
		Date dataInizioTest = DateManager.getDate();
		
		this.testPA10StartTime = new Date();
		
		String egov=UtilitiesEGov.getIDEGov(CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE,
				CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE+CostantiTestSuite.SPCOOP_PORTA_DOMINIO);
		this.repository_testPA10.add(egov);
		String bustaSOAP=UtilitiesEGov.getBustaEGov(CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_FRUITORE,
				CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE,
				CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_TEST_URL_PREFIX_REWRITER_10,
				CostantiTestSuite.SPCOOP_NOME_SOGGETTO_TEST_URL_PREFIX_REWRITER_10,
				SPCoopCostanti.PROFILO_COLLABORAZIONE_SINCRONO,
				CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
				CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
				egov,
				false,
				SPCoopCostanti.PROFILO_TRASMISSIONE_SENZA_DUPLICATI,
				false,
				null);

		// costruzione busta
		java.io.ByteArrayInputStream bin =
			new java.io.ByteArrayInputStream(bustaSOAP.getBytes());
		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();

		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repository_testPA10);
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneBusteErogatore());
			client.connectToSoapEngine();
			client.setMessage(msg);
			client.setRispostaDaGestire(true);
			// AttesaTerminazioneMessaggi
			if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()){
				dbComponentErogatore = DatabaseProperties.getDatabaseComponentErogatore();

				client.setAttesaTerminazioneMessaggi(true);
				client.setDbAttesaTerminazioneMessaggiErogatore(dbComponentErogatore);
			}
			try {
				client.run();
				if(Utilities.testSuiteProperties.isNewConnectionForResponse()==false){
					Reporter.log("Invocazione PA.");
					throw new TestSuiteException("Invocazione PA, non ha causato errori.");
				}
			} catch (AxisFault error) {
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				Reporter.log("Controllo fault code [Server]");
				Assert.assertTrue("Server".equals(error.getFaultCode().getLocalPart()));
				Reporter.log("Controllo fault string ["+SPCoopCostanti.FAULT_STRING_PROCESSAMENTO_SPCOOP+"]");
				Assert.assertTrue(SPCoopCostanti.FAULT_STRING_PROCESSAMENTO_SPCOOP.equals(error.getFaultString()));
			}finally{
				dbComponentErogatore.close();
			}
		}catch(Exception e){
			throw e;
		}finally{
			dbComponentErogatore.close();
		}
		
		Date dataFineTest = DateManager.getDate();
		
		ErroreAttesoOpenSPCoopLogCore err = new ErroreAttesoOpenSPCoopLogCore();
		err.setIntervalloInferiore(dataInizioTest);
		err.setIntervalloSuperiore(dataFineTest);
		err.setMsgErrore("Errore avvenuto durante la consegna HTTP: unknown protocol: otherprotocoll");
		this.erroriAttesiOpenSPCoopCore.add(err);
	}
	@DataProvider (name="testPA10Provider")
	public Object[][] testPA10Provider()throws Exception{
		String id=this.repository_testPA10.getNext();
		if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()==false){
			try {
				Thread.sleep(Utilities.testSuiteProperties.timeToSleep_verificaDatabase());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		// Attendo tempo perso per la nuova connessione
		//if(Utilities.testSuiteProperties.isNewConnectionForResponse()){
		try{
			Thread.sleep(3000);
		}catch(InterruptedException e){}
		//}	
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentErogatore(),DatabaseProperties.getDatabaseComponentDiagnosticaErogatore(),id}
		};
	}
	@Test(groups={UrlPrefixRewriter.ID_GRUPPO,UrlPrefixRewriter.ID_GRUPPO+".PA_10"},
			dataProvider="testPA10Provider",
			dependsOnMethods="testPA10")
			public void testPA10VerificaTracce(DatabaseComponent data,DatabaseMsgDiagnosticiComponent msgDiagComponent,String id) throws Exception{
		try{
			Reporter.log("Controllo tracciamento richiesta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
			Reporter.log("Controllo valore Mittente Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id, CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE , null));
			Reporter.log("Controllo valore Destinatario Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedDestinatario(id, CostantiTestSuite.SPCOOP_SOGGETTO_TEST_URL_PREFIX_REWRITER_10, null));
			Reporter.log("Controllo valore OraRegistrazione con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedOraRegistrazione(id));

			Reporter.log("Controllo valore Servizio Busta con id: " +id);
			DatiServizio datiServizio = new DatiServizio(CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO, CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO, CostantiTestSuite.SPCOOP_VERSIONE_SERVIZIO_DEFAULT);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedServizio(id, datiServizio));
			Reporter.log("Controllo valore Azione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedAzione(id, null));
			Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloDiCollaborazione(id, SPCoopCostanti.PROFILO_COLLABORAZIONE_SINCRONO,ProfiloDiCollaborazione.SINCRONO));
			Reporter.log("Controllo valore Profilo di Trasmissione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloTrasmissione(id, false,SPCoopCostanti.PROFILO_TRASMISSIONE_SENZA_DUPLICATI,Inoltro.SENZA_DUPLICATI));
			Reporter.log("Controllo che la busta non abbia generato eccezioni, id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().existsListaEccezioni(id)==false);
			Reporter.log("Controllo lista trasmissione con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedTrasmissione(id, 
					CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE, null ,
					CostantiTestSuite.SPCOOP_SOGGETTO_TEST_URL_PREFIX_REWRITER_10, null,
					true));
			Reporter.log("Numero messaggi arrivati al servizio applicativo: "+data.getVerificatoreTracciaRichiesta().isArrivedCount(id));
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isArrivedCount(id)==0);

			Reporter.log("----------------------------------------------------------");

			Reporter.log("Controllo tracciamento risposta con riferimento id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTraced(id));
			Reporter.log("Controllo valore Mittente Busta della risposta con riferimento id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedMittente(id, CostantiTestSuite.SPCOOP_SOGGETTO_TEST_URL_PREFIX_REWRITER_10, null));
			Reporter.log("Controllo valore Destinatario Busta della risposta con riferimento id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedDestinatario(id, CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE , null));
			Reporter.log("Controllo valore OraRegistrazione con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedOraRegistrazione(id));
	
			Reporter.log("Controllo valore Servizio Busta della risposta con riferimento id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedServizio(id, datiServizio));
			Reporter.log("Controllo valore Azione Busta della risposta con riferimento id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedAzione(id, null));
			Reporter.log("Controllo valore Profilo di Collaborazione Busta della risposta con riferimento id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedProfiloDiCollaborazione(id, SPCoopCostanti.PROFILO_COLLABORAZIONE_SINCRONO,ProfiloDiCollaborazione.SINCRONO));
			Reporter.log("Controllo valore Profilo di Trasmissione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedProfiloTrasmissione(id,false,SPCoopCostanti.PROFILO_TRASMISSIONE_SENZA_DUPLICATI,Inoltro.SENZA_DUPLICATI));
			//Reporter.log("Controllo che la busta abbia generato eccezioni, riferimento messaggio: " +id);
			//Assert.assertTrue(data.getVerificatoreTracciaRisposta().existsListaEccezioni(id)==true);
			Reporter.log("Controllo lista trasmissione, riferimento messaggio: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedTrasmissione(id, 
					CostantiTestSuite.SPCOOP_SOGGETTO_TEST_URL_PREFIX_REWRITER_10, null,
					CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE, null,
					true));
		
			//Reporter.log("Controllo che la busta abbia generato l'eccezione " + Costanti.ECCEZIONE_PROCESSAMENTO_MESSAGGIO + " rappresentante un servizio applicativo non disponibile");
			//Assert.assertTrue(data.isTracedEccezione(id, Costanti.ECCEZIONE_PROCESSAMENTO_MESSAGGIO));
			
			Reporter.log("Controllo esistenza diagnostico [ https://verificaSSLTestPA]");
			Assert.assertTrue(msgDiagComponent.isTracedMessaggioWithLike(id,"(location: otherProtocoll://hostnamePA/OpenSPCoop2TestSuite/server)"));
			Assert.assertTrue(msgDiagComponent.isTracedMessaggioWithLike(id,"Errore avvenuto durante la consegna HTTP: unknown protocol: otherprotocoll"));
			
		}catch(Exception e){
			throw e;
		}finally{
			try{
				data.close();
			}catch(Exception e){}
			try{
				msgDiagComponent.close();
			}catch(Exception e){}
		}
	}
}
