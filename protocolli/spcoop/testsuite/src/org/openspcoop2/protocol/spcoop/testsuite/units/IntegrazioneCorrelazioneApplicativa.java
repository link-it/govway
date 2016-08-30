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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Properties;
import java.util.Vector;

import javax.xml.soap.Name;

import org.apache.axis.AxisFault;
import org.apache.axis.Message;
import org.apache.axis.message.MessageElement;
import org.apache.axis.message.PrefixedQName;
import org.apache.axis.message.SOAPHeaderElement;
import org.openspcoop2.testsuite.axis14.Axis14SoapUtils;
import org.openspcoop2.testsuite.clients.ClientHttpGenerico;
import org.openspcoop2.testsuite.core.TestSuiteException;
import org.openspcoop2.testsuite.core.Repository;
import org.openspcoop2.testsuite.core.TestSuiteProperties;
import org.openspcoop2.testsuite.db.DatabaseComponent;
import org.openspcoop2.testsuite.db.DatabaseMsgDiagnosticiComponent;
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
import org.openspcoop2.protocol.spcoop.testsuite.core.FileSystemUtilities;
import org.openspcoop2.protocol.spcoop.testsuite.core.Utilities;
import org.openspcoop2.utils.date.DateManager;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.AfterGroups;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.w3c.dom.Document;

/**
 * Test sulle informazioni di integrazione
 * 
 * @author Andi Rexha (rexha@openspcoop.org)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class IntegrazioneCorrelazioneApplicativa {

	/** Identificativo del gruppo */
	public static final String ID_GRUPPO = "IntegrazioneCorrelazioneApplicativa";
	
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
			FileSystemUtilities.verificaOpenspcoopCore(this.dataAvvioGruppoTest,
					this.erroriAttesiOpenSPCoopCore.toArray(new ErroreAttesoOpenSPCoopLogCore[1]));
		}else{
			FileSystemUtilities.verificaOpenspcoopCore(this.dataAvvioGruppoTest);
		}
	} 
	
	
	
	
	
	private static final String MSG_INIZIO_CORRELAZIONE = "Il contenuto applicativo della richiesta, con identificativo applicativo [@IDAPPLICATIVO@], è stato correlato ad una busta con identificativo @IDEGOV@";
	private static final String MSG_CORRELAZIONE = "Il contenuto applicativo della richiesta, con identificativo applicativo [@IDAPPLICATIVO@], è già stato precedentemente correlato ad una busta con identificativo @IDEGOV@";
	
	private void checkHttpRisposta(Properties risposta,String tipoServizio,String servizio,String azione,String idEGov)throws Exception{
		Integrazione.checkHttpRisposta(this.collaborazioneSPCoopBase,risposta, tipoServizio, servizio, azione, idEGov);
	}
	
	private void checkMessaggioRisposta(Message risposta,String tipoServizio,String servizio,String azione,String idEGov)throws Exception{
		Integrazione.checkMessaggioRisposta(this.collaborazioneSPCoopBase,risposta, tipoServizio, servizio, azione, idEGov);
	}
	
	private void checkMessaggioRispostaWSAddressing(Message risposta,String tipoServizio,String nomeServizio,String azione,String idEGov)throws Exception{
		Integrazione.checkMessaggioRispostaWSAddressing(this.collaborazioneSPCoopBase,risposta, tipoServizio, nomeServizio, azione, idEGov);
	}
	
	
	
	
	
	

	
	/***
	 * Test Correlazione Applicativa URL Based
	 */
		
	private String runClientCorrelazioneApplicativaUrlBased(String portaDelegata,String idUnivoco,Repository repository) throws Exception{
		java.io.FileInputStream fin = null;
		DatabaseComponent dbComponentFruitore = null;
		try{
			fin = new java.io.FileInputStream(new File(Utilities.testSuiteProperties.getSoap11FileName()));

			Message msg=new Message(fin);
			msg.getSOAPPartAsBytes();

			
			ClientHttpGenerico client=new ClientHttpGenerico(repository);
			client.setSoapAction("\"TEST\"");
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(portaDelegata+"?correlazioneApplicativa="+idUnivoco);
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
			} catch (AxisFault error) {
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				throw error;
			}finally{
				dbComponentFruitore.close();
			}
			
			checkHttpRisposta(client.getPropertiesTrasportoRisposta(),CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_INTEGRAZIONE,client.getIdMessaggio());
			
			checkMessaggioRisposta(client.getResponseMessage(),CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_INTEGRAZIONE,client.getIdMessaggio());
			
			return client.getIdMessaggio();
			
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
	
	Repository repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaUrlBased=new Repository();
	Repository repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaUrlBasedIDUnivoco=new Repository();
	@Test(groups={IntegrazioneCorrelazioneApplicativa.ID_GRUPPO,IntegrazioneCorrelazioneApplicativa.ID_GRUPPO+".CORRELAZIONE_APPLICATIVA_URL_BASED"},description="Test di tipo sincrono, Viene controllato se i body sono uguali e se gli attachment sono uguali")
	public void sincronoCorrelazioneApplicativaUrlBased() throws TestSuiteException, IOException, Exception{
		
		String idUnivoco = "XXXXXXXIDXXXXXXXX_"+System.currentTimeMillis();
		this.repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaUrlBasedIDUnivoco.add(idUnivoco);
		
		String id1 = runClientCorrelazioneApplicativaUrlBased(CostantiTestSuite.PORTA_DELEGATA_PROFILO_SINCRONO_INTEGRAZIONE_CORRELAZIONE_APPLICATIVA_URL_BASED,idUnivoco,
				this.repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaUrlBased);
		
		Reporter.log("Sleep 4 secondi...");
		try{
			Thread.sleep(4000);
		}catch(Exception e){}
		
		String id2 = runClientCorrelazioneApplicativaUrlBased(CostantiTestSuite.PORTA_DELEGATA_PROFILO_SINCRONO_INTEGRAZIONE_CORRELAZIONE_APPLICATIVA_URL_BASED,idUnivoco,
				this.repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaUrlBased);
		
		Reporter.log("ID 1st invocazione["+id1+"]");
		Reporter.log("ID 2nd invocazione["+id2+"]");
		Assert.assertTrue(id1.equals(id2));
	}
	@DataProvider (name="SincronoCorrelazioneApplicativaUrlBased")
	public Object[][]testSincronoCorrelazioneApplicativaUrlBased()throws Exception{
		String id=this.repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaUrlBased.getNext();
		String idUnivoco=this.repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaUrlBasedIDUnivoco.getNext();
		return new Object[][]{
				{idUnivoco,DatabaseProperties.getDatabaseComponentDiagnosticaFruitore(),DatabaseProperties.getDatabaseComponentFruitore(),id,false},	
				{idUnivoco,null,DatabaseProperties.getDatabaseComponentErogatore(),id,false}	
		};
	}
	@Test(groups={IntegrazioneCorrelazioneApplicativa.ID_GRUPPO,IntegrazioneCorrelazioneApplicativa.ID_GRUPPO+".CORRELAZIONE_APPLICATIVA_URL_BASED"},dataProvider="SincronoCorrelazioneApplicativaUrlBased",dependsOnMethods={"sincronoCorrelazioneApplicativaUrlBased"})
	public void testSincronoCorrelazioneApplicativaUrlBased(String idUnivoco,DatabaseMsgDiagnosticiComponent dataMsgDiag,DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
		try{
			this.collaborazioneSPCoopBase.testSincrono(data, id, CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_INTEGRAZIONE, checkServizioApplicativo,
					null);
			Reporter.log("Numero messaggi arrivati al servizio applicativo: "+data.getVerificatoreTracciaRichiesta().isArrivedCount(id));
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isArrivedCount(id)==2);
			
			if(dataMsgDiag!=null){
				//fruitore
				
				String msgInizioCorrelazione = MSG_INIZIO_CORRELAZIONE.replace("@IDAPPLICATIVO@", idUnivoco);
				msgInizioCorrelazione = msgInizioCorrelazione.replace("@IDEGOV@", id);
				Reporter.log("Check inizio correlazione ["+msgInizioCorrelazione+"]");
				Assert.assertTrue(dataMsgDiag.isTracedMessaggio(msgInizioCorrelazione));
				
				String msgCorrelazioneAvvenuta = MSG_CORRELAZIONE.replace("@IDAPPLICATIVO@", idUnivoco);
				msgCorrelazioneAvvenuta = msgCorrelazioneAvvenuta.replace("@IDEGOV@", id);
				Reporter.log("Check correlazione ["+msgCorrelazioneAvvenuta+"]");
				Assert.assertTrue(dataMsgDiag.isTracedMessaggio(msgCorrelazioneAvvenuta));
				
				// check correlazione 
				Reporter.log("Check correlazione ["+idUnivoco+"]");
				Assert.assertTrue(dataMsgDiag.isTracedCorrelazioneApplicativaIntoMsgDiagnosticiCorrelazione(idUnivoco));
			}
			
			// Check correlazione in tracciamento
			Reporter.log("Check correlazione ["+idUnivoco+"] in tracciamento nella richiesta");
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedCorrelazioneApplicativaRichiesta(id, this.collaborazioneSPCoopBase.getMittente(), idUnivoco));
			Reporter.log("Check correlazione ["+idUnivoco+"] in tracciamento nella risposta");
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedCorrelazioneApplicativaRichiesta(id, this.collaborazioneSPCoopBase.getMittente(), idUnivoco));
			
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
			if(dataMsgDiag!=null)
				dataMsgDiag.close();
		}
	}
	
	
	Repository repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaUrlBasedSenzaRiusoIDEGov=new Repository();
	Repository repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaUrlBasedSenzaRiusoIDEGovIDUnivoco=new Repository();
	@Test(groups={IntegrazioneCorrelazioneApplicativa.ID_GRUPPO,IntegrazioneCorrelazioneApplicativa.ID_GRUPPO+".CORRELAZIONE_APPLICATIVA_URL_BASED"},
			description="Test di tipo sincrono, Viene controllato se i body sono uguali e se gli attachment sono uguali",
			dependsOnMethods={"testSincronoCorrelazioneApplicativaUrlBased"})
	public void sincronoCorrelazioneApplicativaUrlBasedSenzaRiusoIDEGov() throws TestSuiteException, IOException, Exception{
		
		String idUnivoco = "XXXXXXXIDXXXXXXXX_"+System.currentTimeMillis();
		this.repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaUrlBasedSenzaRiusoIDEGovIDUnivoco.add(idUnivoco);
		
		String id1 = runClientCorrelazioneApplicativaUrlBased(CostantiTestSuite.PORTA_DELEGATA_PROFILO_SINCRONO_INTEGRAZIONE_CORRELAZIONE_APPLICATIVA_URL_BASED_SENZA_RIUSO_IDEGOV,
				idUnivoco,
				this.repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaUrlBasedSenzaRiusoIDEGov);
		
		Reporter.log("Sleep 4 secondi...");
		try{
			Thread.sleep(4000);
		}catch(Exception e){}
		
		String id2 = runClientCorrelazioneApplicativaUrlBased(CostantiTestSuite.PORTA_DELEGATA_PROFILO_SINCRONO_INTEGRAZIONE_CORRELAZIONE_APPLICATIVA_URL_BASED_SENZA_RIUSO_IDEGOV,
				idUnivoco,
				this.repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaUrlBasedSenzaRiusoIDEGov);
		
		Reporter.log("ID 1st invocazione["+id1+"]");
		Reporter.log("ID 2nd invocazione["+id2+"]");
		Assert.assertTrue(id1.equals(id2)==false);
	}
	@DataProvider (name="SincronoCorrelazioneApplicativaUrlBasedSenzaRiusoIDEGov")
	public Object[][]testSincronoCorrelazioneApplicativaUrlBasedSenzaRiusoIDEGov()throws Exception{
		String id=this.repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaUrlBasedSenzaRiusoIDEGov.getNext();
		String idUnivoco=this.repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaUrlBasedSenzaRiusoIDEGovIDUnivoco.getNext();
		return new Object[][]{
				{idUnivoco,DatabaseProperties.getDatabaseComponentDiagnosticaFruitore(),DatabaseProperties.getDatabaseComponentFruitore(),id,false},	
				{idUnivoco,null,DatabaseProperties.getDatabaseComponentErogatore(),id,false}	
		};
	}
	@Test(groups={IntegrazioneCorrelazioneApplicativa.ID_GRUPPO,IntegrazioneCorrelazioneApplicativa.ID_GRUPPO+".CORRELAZIONE_APPLICATIVA_URL_BASED"},
			dataProvider="SincronoCorrelazioneApplicativaUrlBasedSenzaRiusoIDEGov",dependsOnMethods={"sincronoCorrelazioneApplicativaUrlBasedSenzaRiusoIDEGov"})
	public void testSincronoCorrelazioneApplicativaUrlBasedSenzaRiusoIDEGov(String idUnivoco,DatabaseMsgDiagnosticiComponent dataMsgDiag,DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
		try{
			this.collaborazioneSPCoopBase.testSincrono(data, id, CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_INTEGRAZIONE, checkServizioApplicativo,
					null);
			Reporter.log("Numero messaggi arrivati al servizio applicativo: "+data.getVerificatoreTracciaRichiesta().isArrivedCount(id));
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isArrivedCount(id)==1);
			
			if(dataMsgDiag!=null){
				//fruitore
				
				String msgInizioCorrelazione = MSG_INIZIO_CORRELAZIONE.replace("@IDAPPLICATIVO@", idUnivoco);
				msgInizioCorrelazione = msgInizioCorrelazione.replace("@IDEGOV@", id);
				Reporter.log("Check inizio correlazione ["+msgInizioCorrelazione+"] non esistente");
				Assert.assertTrue(dataMsgDiag.isTracedMessaggio(msgInizioCorrelazione)==false);
				
				String msgCorrelazioneAvvenuta = MSG_CORRELAZIONE.replace("@IDAPPLICATIVO@", idUnivoco);
				msgCorrelazioneAvvenuta = msgCorrelazioneAvvenuta.replace("@IDEGOV@", id);
				Reporter.log("Check correlazione ["+msgCorrelazioneAvvenuta+"] non esistente");
				Assert.assertTrue(dataMsgDiag.isTracedMessaggio(msgCorrelazioneAvvenuta)==false);
				
				// check correlazione 
				Reporter.log("Check correlazione ["+idUnivoco+"]");
				Assert.assertTrue(dataMsgDiag.isTracedCorrelazioneApplicativaIntoMsgDiagnosticiCorrelazione(idUnivoco));
			}
			
			// Check correlazione in tracciamento
			Reporter.log("Check correlazione ["+idUnivoco+"] in tracciamento nella richiesta");
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedCorrelazioneApplicativaRichiesta(id, this.collaborazioneSPCoopBase.getMittente(), idUnivoco));
			Reporter.log("Check correlazione ["+idUnivoco+"] in tracciamento nella risposta");
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedCorrelazioneApplicativaRichiesta(id, this.collaborazioneSPCoopBase.getMittente(), idUnivoco));
			
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
			if(dataMsgDiag!=null)
				dataMsgDiag.close();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/***
	 * Test Correlazione Applicativa Url Based, input mancanti
	 */
	Repository repositorySincronoCorrelazioneApplicativaUrlBasedDatiMancanti=new Repository();
	@Test(groups={IntegrazioneCorrelazioneApplicativa.ID_GRUPPO,IntegrazioneCorrelazioneApplicativa.ID_GRUPPO+".CORRELAZIONE_APPLICATIVA_URL_BASED_DATI_MANCANTI"},description="Test di tipo sincrono, Viene controllato se i body sono uguali e se gli attachment sono uguali")
	public void sincronoCorrelazioneApplicativaUrlBasedDatiMancanti() throws TestSuiteException, IOException, Exception{
		
		Date dataInizioTest = DateManager.getDate();
		
		java.io.FileInputStream fin = null;
		DatabaseComponent dbComponentFruitore = null;
		try{
			fin = new java.io.FileInputStream(new File(Utilities.testSuiteProperties.getSoap11FileName()));

			Message msg=new Message(fin);
			msg.getSOAPPartAsBytes();
			
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositorySincronoCorrelazioneApplicativaUrlBasedDatiMancanti);
			client.setSoapAction("\"TEST\"");
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_PROFILO_SINCRONO_INTEGRAZIONE_CORRELAZIONE_APPLICATIVA_URL_BASED);
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
				throw new Exception("Errore atteso [OPENSPCOOP_ORG_416] non si e' verificato...");
			} catch (AxisFault error) {
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				Reporter.log("Controllo fault code [OPENSPCOOP_ORG_416]");
				Assert.assertTrue(Utilities.toString(CodiceErroreIntegrazione.CODICE_416_CORRELAZIONE_APPLICATIVA_RICHIESTA_ERRORE).equals(error.getFaultCode().getLocalPart()));
				String msgVerifica="identificativo di correlazione applicativa non identificato nell'elemento [*] con modalita' di acquisizione urlBased (Pattern:.+correlazioneApplicativa=([^&]*).*): nessun match trovato";
				Reporter.log("Controllo fault string ["+msgVerifica+"]");
				Assert.assertTrue(error.getFaultString().contains(msgVerifica));
				Reporter.log("Controllo fault actor [OpenSPCoop]");
				Assert.assertTrue(CostantiPdD.OPENSPCOOP2.equals(error.getFaultActor()));
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
		
		Date dataFineTest = DateManager.getDate();
		ErroreAttesoOpenSPCoopLogCore err = new ErroreAttesoOpenSPCoopLogCore();
		err.setIntervalloInferiore(dataInizioTest);
		err.setIntervalloSuperiore(dataFineTest);
		err.setMsgErrore("identificativo di correlazione applicativa non identificato nell'elemento [*] con modalita' di acquisizione urlBased (Pattern:.+correlazioneApplicativa=([^&]*).*): nessun match trovato");
		this.erroriAttesiOpenSPCoopCore.add(err);
	}
	@DataProvider (name="SincronoCorrelazioneApplicativaUrlBasedDatiMancanti")
	public Object[][]testSincronoCorrelazioneApplicativaUrlBasedDatiMancanti()throws Exception{
		String id=this.repositorySincronoCorrelazioneApplicativaUrlBasedDatiMancanti.getNext();
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),id,false},	
				{DatabaseProperties.getDatabaseComponentErogatore(),id,true}	
		};
	}
	@Test(groups={IntegrazioneCorrelazioneApplicativa.ID_GRUPPO,IntegrazioneCorrelazioneApplicativa.ID_GRUPPO+".CORRELAZIONE_APPLICATIVA_URL_BASED_DATI_MANCANTI"},dataProvider="SincronoCorrelazioneApplicativaUrlBasedDatiMancanti",dependsOnMethods={"sincronoCorrelazioneApplicativaUrlBasedDatiMancanti"})
	public void testSincronoCorrelazioneApplicativaUrlBasedDatiMancanti(DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
		try{
			Reporter.log("Controllo tracciamento richiesta non effettuato con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id)==false);
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/***
	 * Test Correlazione Applicativa URL Based, con correlazione non riuscita, ma da accettere comunque.
	 * Verranno utilizzati due id egov differenti
	 */
		
	private String runClientCorrelazioneApplicativaUrlBased_erroreAccettato(String idUnivoco) throws Exception{
		java.io.FileInputStream fin = null;
		DatabaseComponent dbComponentFruitore = null;
		try{
			fin = new java.io.FileInputStream(new File(Utilities.testSuiteProperties.getSoap11FileName()));

			Message msg=new Message(fin);
			msg.getSOAPPartAsBytes();

			
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaUrlBased_erroreAccettato);
			client.setSoapAction("\"TEST\"");
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_PROFILO_SINCRONO_INTEGRAZIONE_CORRELAZIONE_APPLICATIVA_URL_BASED_ACCETTA_CON_IDENTIFICAZIONE_NON_RIUSCITA+"?correlazioneApplicativaERRATO="+idUnivoco);
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
			} catch (AxisFault error) {
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				throw error;
			}finally{
				dbComponentFruitore.close();
			}
			
			checkHttpRisposta(client.getPropertiesTrasportoRisposta(),CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_INTEGRAZIONE,client.getIdMessaggio());
			
			checkMessaggioRisposta(client.getResponseMessage(),CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_INTEGRAZIONE,client.getIdMessaggio());
			
			return client.getIdMessaggio();
			
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
	
	Repository repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaUrlBased_erroreAccettato=new Repository();
	Repository repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaUrlBasedIDUnivoco_erroreAccettato=new Repository();
	@Test(groups={IntegrazioneCorrelazioneApplicativa.ID_GRUPPO,IntegrazioneCorrelazioneApplicativa.ID_GRUPPO+".CORRELAZIONE_APPLICATIVA_URL_BASED_ACCETTA_IDENTIFICAZIONE_NON_RIUSCITA"},description="Test di tipo sincrono, Viene controllato se i body sono uguali e se gli attachment sono uguali")
	public void sincronoCorrelazioneApplicativaUrlBased_erroreAccettato() throws TestSuiteException, IOException, Exception{
		
		String idUnivoco = "XXXXXXXIDXXXXXXXX_"+System.currentTimeMillis();
		this.repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaUrlBasedIDUnivoco_erroreAccettato.add(idUnivoco);
		
		String id1 = runClientCorrelazioneApplicativaUrlBased_erroreAccettato(idUnivoco);
		
		Reporter.log("Sleep 4 secondi...");
		try{
			Thread.sleep(4000);
		}catch(Exception e){}
		
		String id2 = runClientCorrelazioneApplicativaUrlBased_erroreAccettato(idUnivoco);
		
		Reporter.log("ID 1st invocazione["+id1+"]");
		Reporter.log("ID 2nd invocazione["+id2+"]");
		Assert.assertTrue(id1.equals(id2)==false);
	}
	@DataProvider (name="SincronoCorrelazioneApplicativaUrlBased_erroreAccettato")
	public Object[][]testSincronoCorrelazioneApplicativaUrlBased_erroreAccettato()throws Exception{
		String id=this.repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaUrlBased_erroreAccettato.getNext();
		String idSecondaInvocazione=this.repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaUrlBased_erroreAccettato.getNext();
		String idUnivoco = this.repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaUrlBasedIDUnivoco_erroreAccettato.getNext();
		return new Object[][]{
				{idUnivoco,idSecondaInvocazione,DatabaseProperties.getDatabaseComponentDiagnosticaFruitore(),DatabaseProperties.getDatabaseComponentFruitore(),id,false},	
				{idUnivoco,idSecondaInvocazione,null,DatabaseProperties.getDatabaseComponentErogatore(),id,false}	
		};
	}
	@Test(groups={IntegrazioneCorrelazioneApplicativa.ID_GRUPPO,IntegrazioneCorrelazioneApplicativa.ID_GRUPPO+".CORRELAZIONE_APPLICATIVA_URL_BASED_ACCETTA_IDENTIFICAZIONE_NON_RIUSCITA"},
			dataProvider="SincronoCorrelazioneApplicativaUrlBased_erroreAccettato",dependsOnMethods={"sincronoCorrelazioneApplicativaUrlBased_erroreAccettato"})
	public void testSincronoCorrelazioneApplicativaUrlBased_erroreAccettato(String idUnivoco,String idSecondaInvocazione,DatabaseMsgDiagnosticiComponent dataMsgDiag,DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
		try{
			this.collaborazioneSPCoopBase.testSincrono(data, id, CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_INTEGRAZIONE, checkServizioApplicativo,
					null);
			
			Reporter.log("Numero messaggi arrivati al servizio applicativo: "+data.getVerificatoreTracciaRichiesta().isArrivedCount(id));
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isArrivedCount(id)==1);
			
			this.collaborazioneSPCoopBase.testSincrono(data, idSecondaInvocazione, CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_INTEGRAZIONE, checkServizioApplicativo,
					null);
			
			Reporter.log("Numero messaggi arrivati al servizio applicativo: "+data.getVerificatoreTracciaRichiesta().isArrivedCount(id));
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isArrivedCount(idSecondaInvocazione)==1);

			if(dataMsgDiag!=null){
				//fruitore
				
				String msgInizioCorrelazione = MSG_INIZIO_CORRELAZIONE.replace("@IDEGOV@", id);
				String [] msgRicerca = msgInizioCorrelazione.split("@IDAPPLICATIVO@");
				Reporter.log("Check inizio correlazione ["+msgInizioCorrelazione+"]");
				Assert.assertTrue(dataMsgDiag.isTracedMessaggio(msgRicerca)==false);
				
				String msgCorrelazioneAvvenuta = MSG_CORRELAZIONE.replace("@IDEGOV@", id);
				msgRicerca = msgCorrelazioneAvvenuta.split("@IDAPPLICATIVO@");
				Reporter.log("Check inizio correlazione ["+msgCorrelazioneAvvenuta+"]");
				Assert.assertTrue(dataMsgDiag.isTracedMessaggio(msgRicerca)==false);
				
				msgInizioCorrelazione = MSG_INIZIO_CORRELAZIONE.replace("@IDEGOV@", idSecondaInvocazione);
				msgRicerca = msgInizioCorrelazione.split("@IDAPPLICATIVO@");
				Reporter.log("Check inizio correlazione ["+msgInizioCorrelazione+"]");
				Assert.assertTrue(dataMsgDiag.isTracedMessaggio(msgRicerca)==false);
				
				msgCorrelazioneAvvenuta = MSG_CORRELAZIONE.replace("@IDEGOV@", idSecondaInvocazione);
				msgRicerca = msgCorrelazioneAvvenuta.split("@IDAPPLICATIVO@");
				Reporter.log("Check inizio correlazione ["+msgCorrelazioneAvvenuta+"]");
				Assert.assertTrue(dataMsgDiag.isTracedMessaggio(msgRicerca)==false);

				// check correlazione 
				Reporter.log("Check correlazione ["+idUnivoco+"] non esistente");
				Assert.assertTrue(dataMsgDiag.isTracedCorrelazioneApplicativaIntoMsgDiagnosticiCorrelazione(idUnivoco)==false);
			}
			
			// Check correlazione in tracciamento
			Reporter.log("Check correlazione ["+idUnivoco+"] in tracciamento nella richiesta non esistente");
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedCorrelazioneApplicativaRichiesta(id, this.collaborazioneSPCoopBase.getMittente(),idUnivoco)==false);
			Reporter.log("Check correlazione ["+idUnivoco+"] in tracciamento nella risposta non esistente");
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedCorrelazioneApplicativaRichiesta(id, this.collaborazioneSPCoopBase.getMittente(),idUnivoco)==false);
			
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
			if(dataMsgDiag!=null)
				dataMsgDiag.close();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/***
	 * Test Correlazione Applicativa Content Based
	 */
		
	private String runClientCorrelazioneApplicativaContentBased(String portaDelegata,String idUnivoco, Repository repository) throws Exception{
		java.io.FileInputStream fin = null;
		DatabaseComponent dbComponentFruitore = null;
		try{
			fin = new java.io.FileInputStream(new File(Utilities.testSuiteProperties.getSoap11FileName()));

			Message msg=new Message(fin);
			msg.getSOAPPartAsBytes();

			MessageElement elem = (MessageElement) msg.getSOAPBody().addChildElement("idApplicativo", "test", "http://www.openspcoop.org");
			elem.setValue(idUnivoco);
			Reporter.log("Messaggio: "+msg.getSOAPPartAsString());
			msg.getSOAPPartAsBytes();
			
			ClientHttpGenerico client=new ClientHttpGenerico(repository);
			client.setSoapAction("\"TEST\"");
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
			} catch (AxisFault error) {
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				throw error;
			}finally{
				dbComponentFruitore.close();
			}
			
			checkHttpRisposta(client.getPropertiesTrasportoRisposta(),CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_INTEGRAZIONE,client.getIdMessaggio());
			
			checkMessaggioRisposta(client.getResponseMessage(),CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_INTEGRAZIONE,client.getIdMessaggio());
			
			return client.getIdMessaggio();
			
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
	
	Repository repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaContentBased=new Repository();
	Repository repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaContentBasedIDUnivoco=new Repository();
	@Test(groups={IntegrazioneCorrelazioneApplicativa.ID_GRUPPO,IntegrazioneCorrelazioneApplicativa.ID_GRUPPO+".CORRELAZIONE_APPLICATIVA_CONTENT_BASED"},description="Test di tipo sincrono, Viene controllato se i body sono uguali e se gli attachment sono uguali")
	public void sincronoCorrelazioneApplicativaContentBased() throws TestSuiteException, IOException, Exception{
		
		String idUnivoco = "XXXXXXXIDXXXXXXXX_"+System.currentTimeMillis();
		this.repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaContentBasedIDUnivoco.add(idUnivoco);
		
		String id1 = runClientCorrelazioneApplicativaContentBased(CostantiTestSuite.PORTA_DELEGATA_PROFILO_SINCRONO_INTEGRAZIONE_CORRELAZIONE_APPLICATIVA_CONTENT_BASED,
				idUnivoco,
				this.repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaContentBased);
		
		Reporter.log("Sleep 4 secondi...");
		try{
			Thread.sleep(4000);
		}catch(Exception e){}
		
		String id2 = runClientCorrelazioneApplicativaContentBased(CostantiTestSuite.PORTA_DELEGATA_PROFILO_SINCRONO_INTEGRAZIONE_CORRELAZIONE_APPLICATIVA_CONTENT_BASED,
				idUnivoco,
				this.repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaContentBased);
		
		Reporter.log("ID 1st invocazione["+id1+"]");
		Reporter.log("ID 2nd invocazione["+id2+"]");
		Assert.assertTrue(id1.equals(id2));
	}
	@DataProvider (name="SincronoCorrelazioneApplicativaContentBased")
	public Object[][]testSincronoCorrelazioneApplicativaContentBased()throws Exception{
		String id=this.repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaContentBased.getNext();
		String idUnivoco=this.repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaContentBasedIDUnivoco.getNext();
		return new Object[][]{
				{idUnivoco,DatabaseProperties.getDatabaseComponentDiagnosticaFruitore(),DatabaseProperties.getDatabaseComponentFruitore(),id,false},	
				{idUnivoco,null,DatabaseProperties.getDatabaseComponentErogatore(),id,false}	
		};
	}
	@Test(groups={IntegrazioneCorrelazioneApplicativa.ID_GRUPPO,IntegrazioneCorrelazioneApplicativa.ID_GRUPPO+".CORRELAZIONE_APPLICATIVA_CONTENT_BASED"},dataProvider="SincronoCorrelazioneApplicativaContentBased",dependsOnMethods={"sincronoCorrelazioneApplicativaContentBased"})
	public void testSincronoCorrelazioneApplicativaContentBased(String idUnivoco,DatabaseMsgDiagnosticiComponent dataMsgDiag,DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
		try{
			this.collaborazioneSPCoopBase.testSincrono(data, id, CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_INTEGRAZIONE, checkServizioApplicativo,
					null);
			Reporter.log("Numero messaggi arrivati al servizio applicativo: "+data.getVerificatoreTracciaRichiesta().isArrivedCount(id));
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isArrivedCount(id)==2);
			
			if(dataMsgDiag!=null){
				//fruitore
				
				String msgInizioCorrelazione = MSG_INIZIO_CORRELAZIONE.replace("@IDAPPLICATIVO@", idUnivoco);
				msgInizioCorrelazione = msgInizioCorrelazione.replace("@IDEGOV@", id);
				Reporter.log("Check inizio correlazione ["+msgInizioCorrelazione+"]");
				Assert.assertTrue(dataMsgDiag.isTracedMessaggio(msgInizioCorrelazione));
				
				String msgCorrelazioneAvvenuta = MSG_CORRELAZIONE.replace("@IDAPPLICATIVO@", idUnivoco);
				msgCorrelazioneAvvenuta = msgCorrelazioneAvvenuta.replace("@IDEGOV@", id);
				Reporter.log("Check correlazione ["+msgCorrelazioneAvvenuta+"]");
				Assert.assertTrue(dataMsgDiag.isTracedMessaggio(msgCorrelazioneAvvenuta));
				
				// check correlazione 
				Reporter.log("Check correlazione ["+idUnivoco+"]");
				Assert.assertTrue(dataMsgDiag.isTracedCorrelazioneApplicativaIntoMsgDiagnosticiCorrelazione(idUnivoco));
			}
			
			// Check correlazione in tracciamento
			Reporter.log("Check correlazione ["+idUnivoco+"] in tracciamento nella richiesta");
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedCorrelazioneApplicativaRichiesta(id, this.collaborazioneSPCoopBase.getMittente(), idUnivoco));
			Reporter.log("Check correlazione ["+idUnivoco+"] in tracciamento nella risposta");
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedCorrelazioneApplicativaRichiesta(id, this.collaborazioneSPCoopBase.getMittente(), idUnivoco));
			
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
			if(dataMsgDiag!=null)
				dataMsgDiag.close();
		}
	}
	
	Repository repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaContentBasedSenzaRiusoIDEgov=new Repository();
	Repository repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaContentBasedSenzaRiusoIDEgovIDUnivoco=new Repository();
	@Test(groups={IntegrazioneCorrelazioneApplicativa.ID_GRUPPO,IntegrazioneCorrelazioneApplicativa.ID_GRUPPO+".CORRELAZIONE_APPLICATIVA_CONTENT_BASED"},
			description="Test di tipo sincrono, Viene controllato se i body sono uguali e se gli attachment sono uguali",
			dependsOnMethods={"testSincronoCorrelazioneApplicativaContentBased"})
	public void sincronoCorrelazioneApplicativaContentBasedSenzaRiusoIDEgov() throws TestSuiteException, IOException, Exception{
		
		String idUnivoco = "XXXXXXXIDXXXXXXXX_"+System.currentTimeMillis();
		this.repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaContentBasedSenzaRiusoIDEgovIDUnivoco.add(idUnivoco);
		
		String id1 = runClientCorrelazioneApplicativaContentBased(CostantiTestSuite.PORTA_DELEGATA_PROFILO_SINCRONO_INTEGRAZIONE_CORRELAZIONE_APPLICATIVA_CONTENT_BASED_SENZA_RIUSO_IDEGOV,
				idUnivoco,
				this.repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaContentBasedSenzaRiusoIDEgov);
		
		Reporter.log("Sleep 4 secondi...");
		try{
			Thread.sleep(4000);
		}catch(Exception e){}
		
		String id2 = runClientCorrelazioneApplicativaContentBased(CostantiTestSuite.PORTA_DELEGATA_PROFILO_SINCRONO_INTEGRAZIONE_CORRELAZIONE_APPLICATIVA_CONTENT_BASED_SENZA_RIUSO_IDEGOV,
				idUnivoco,
				this.repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaContentBasedSenzaRiusoIDEgov);
		
		Reporter.log("ID 1st invocazione["+id1+"]");
		Reporter.log("ID 2nd invocazione["+id2+"]");
		Assert.assertTrue(id1.equals(id2)==false);
	}
	@DataProvider (name="SincronoCorrelazioneApplicativaContentBasedSenzaRiusoIDEgov")
	public Object[][]testSincronoCorrelazioneApplicativaContentBasedSenzaRiusoIDEgov()throws Exception{
		String id=this.repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaContentBasedSenzaRiusoIDEgov.getNext();
		String idUnivoco=this.repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaContentBasedSenzaRiusoIDEgovIDUnivoco.getNext();
		return new Object[][]{
				{idUnivoco,DatabaseProperties.getDatabaseComponentDiagnosticaFruitore(),DatabaseProperties.getDatabaseComponentFruitore(),id,false},	
				{idUnivoco,null,DatabaseProperties.getDatabaseComponentErogatore(),id,false}	
		};
	}
	@Test(groups={IntegrazioneCorrelazioneApplicativa.ID_GRUPPO,IntegrazioneCorrelazioneApplicativa.ID_GRUPPO+".CORRELAZIONE_APPLICATIVA_CONTENT_BASED"},
			dataProvider="SincronoCorrelazioneApplicativaContentBasedSenzaRiusoIDEgov",dependsOnMethods={"sincronoCorrelazioneApplicativaContentBasedSenzaRiusoIDEgov"})
	public void testSincronoCorrelazioneApplicativaContentBasedSenzaRiusoIDEgov(String idUnivoco,DatabaseMsgDiagnosticiComponent dataMsgDiag,DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
		try{
			this.collaborazioneSPCoopBase.testSincrono(data, id, CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_INTEGRAZIONE, checkServizioApplicativo,
					null);
			Reporter.log("Numero messaggi arrivati al servizio applicativo: "+data.getVerificatoreTracciaRichiesta().isArrivedCount(id));
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isArrivedCount(id)==1);
			
			if(dataMsgDiag!=null){
				//fruitore
				
				String msgInizioCorrelazione = MSG_INIZIO_CORRELAZIONE.replace("@IDAPPLICATIVO@", idUnivoco);
				msgInizioCorrelazione = msgInizioCorrelazione.replace("@IDEGOV@", id);
				Reporter.log("Check inizio correlazione ["+msgInizioCorrelazione+"] non esistente");
				Assert.assertTrue(dataMsgDiag.isTracedMessaggio(msgInizioCorrelazione)==false);
				
				String msgCorrelazioneAvvenuta = MSG_CORRELAZIONE.replace("@IDAPPLICATIVO@", idUnivoco);
				msgCorrelazioneAvvenuta = msgCorrelazioneAvvenuta.replace("@IDEGOV@", id);
				Reporter.log("Check correlazione ["+msgCorrelazioneAvvenuta+"] non esistente");
				Assert.assertTrue(dataMsgDiag.isTracedMessaggio(msgCorrelazioneAvvenuta)==false);
				
				// check correlazione 
				Reporter.log("Check correlazione ["+idUnivoco+"]");
				Assert.assertTrue(dataMsgDiag.isTracedCorrelazioneApplicativaIntoMsgDiagnosticiCorrelazione(idUnivoco));
			}
			
			// Check correlazione in tracciamento
			Reporter.log("Check correlazione ["+idUnivoco+"] in tracciamento nella richiesta");
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedCorrelazioneApplicativaRichiesta(id, this.collaborazioneSPCoopBase.getMittente(), idUnivoco));
			Reporter.log("Check correlazione ["+idUnivoco+"] in tracciamento nella risposta");
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedCorrelazioneApplicativaRichiesta(id, this.collaborazioneSPCoopBase.getMittente(), idUnivoco));
			
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
			if(dataMsgDiag!=null)
				dataMsgDiag.close();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/***
	 * Test Correlazione Applicativa Content Based, input mancanti
	 */
	Repository repositorySincronoCorrelazioneApplicativaContentBasedDatiMancanti=new Repository();
	@Test(groups={IntegrazioneCorrelazioneApplicativa.ID_GRUPPO,IntegrazioneCorrelazioneApplicativa.ID_GRUPPO+".CORRELAZIONE_APPLICATIVA_CONTENT_BASED_DATI_MANCANTI"},description="Test di tipo sincrono, Viene controllato se i body sono uguali e se gli attachment sono uguali")
	public void sincronoCorrelazioneApplicativaContentBasedDatiMancanti() throws TestSuiteException, IOException, Exception{
		
		Date dataInizioTest = DateManager.getDate();
		
		java.io.FileInputStream fin = null;
		DatabaseComponent dbComponentFruitore = null;
		try{
			fin = new java.io.FileInputStream(new File(Utilities.testSuiteProperties.getSoap11FileName()));

			Message msg=new Message(fin);
			msg.getSOAPPartAsBytes();
			
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositorySincronoCorrelazioneApplicativaContentBasedDatiMancanti);
			client.setSoapAction("\"TEST\"");
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_PROFILO_SINCRONO_INTEGRAZIONE_CORRELAZIONE_APPLICATIVA_CONTENT_BASED);
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
				throw new Exception("Errore atteso [OPENSPCOOP_ORG_416] non si e' verificato...");
			} catch (AxisFault error) {
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				Reporter.log("Controllo fault code [OPENSPCOOP_ORG_416]");
				Assert.assertTrue(Utilities.toString(CodiceErroreIntegrazione.CODICE_416_CORRELAZIONE_APPLICATIVA_RICHIESTA_ERRORE).equals(error.getFaultCode().getLocalPart()));
								   
				String msgCheck = "La gestione della funzionalità di correlazione applicativa, per il messaggio di richiesta, ha generato un errore: identificativo di correlazione applicativa non identificato nell'elemento [*] con modalita' di acquisizione contentBased (Pattern://test:idApplicativo/text()): Espressione XPATH non applicabile al messaggio: javax.xml.transform.TransformerException: Prefix must resolve to a namespace: test";
				String msgCheck2 = "La gestione della funzionalità di correlazione applicativa, per il messaggio di richiesta, ha generato un errore: identificativo di correlazione applicativa non identificato nell'elemento [*] con modalita' di acquisizione contentBased (Pattern://test:idApplicativo/text()): Espressione XPATH non applicabile al messaggio: javax.xml.xpath.XPathExpressionException: org.apache.xpath.domapi.XPathStylesheetDOM3Exception: Prefix must resolve to a namespace: test";
				String msgCheck3 = "La gestione della funzionalità di correlazione applicativa, per il messaggio di richiesta, ha generato un errore: identificativo di correlazione applicativa non identificato nell'elemento [*] con modalita' di acquisizione contentBased (Pattern://test:idApplicativo/text()): Espressione XPATH non applicabile al messaggio: org.apache.xpath.domapi.XPathStylesheetDOM3Exception: Prefix must resolve to a namespace: test"; 
				String msgCheck4 = "La gestione della funzionalità di correlazione applicativa, per il messaggio di richiesta, ha generato un errore: identificativo di correlazione applicativa non identificato nell'elemento [*] con modalita' di acquisizione contentBased (Pattern://test:idApplicativo/text()): Espressione XPATH non applicabile al messaggio: com.sun.org.apache.xpath.internal.domapi.XPathStylesheetDOM3Exception: Prefix must resolve to a namespace: test";
				
				Reporter.log("Controllo fault string 1["+msgCheck+"]");
				Reporter.log("Controllo fault string 2["+msgCheck2+"]");
				Reporter.log("Controllo fault string 3["+msgCheck3+"]");
				Reporter.log("Controllo fault string 4["+msgCheck4+"]");
				Assert.assertTrue(error.getFaultString().contains(msgCheck) || 
						error.getFaultString().contains(msgCheck2) || 
						error.getFaultString().contains(msgCheck3) ||
						error.getFaultString().contains(msgCheck4));
				Reporter.log("Controllo fault actor [OpenSPCoop]");
				Assert.assertTrue(CostantiPdD.OPENSPCOOP2.equals(error.getFaultActor()));
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
		
		Date dataFineTest = DateManager.getDate();
		
		ErroreAttesoOpenSPCoopLogCore err = new ErroreAttesoOpenSPCoopLogCore();
		err.setIntervalloInferiore(dataInizioTest);
		err.setIntervalloSuperiore(dataFineTest);
		err.setMsgErrore("identificativo di correlazione applicativa non identificato nell'elemento [*] con modalita' di acquisizione contentBased (Pattern://test:idApplicativo/text()): Espressione XPATH non applicabile al messaggio: javax.xml.transform.TransformerException: Prefix must resolve to a namespace: test");
		this.erroriAttesiOpenSPCoopCore.add(err);
		
		ErroreAttesoOpenSPCoopLogCore err2 = new ErroreAttesoOpenSPCoopLogCore();
		err2.setIntervalloInferiore(dataInizioTest);
		err2.setIntervalloSuperiore(dataFineTest);
		err2.setMsgErrore("identificativo di correlazione applicativa non identificato nell'elemento [*] con modalita' di acquisizione contentBased (Pattern://test:idApplicativo/text()): Espressione XPATH non applicabile al messaggio: javax.xml.xpath.XPathExpressionException: org.apache.xpath.domapi.XPathStylesheetDOM3Exception: Prefix must resolve to a namespace: test");
		this.erroriAttesiOpenSPCoopCore.add(err2);
		
		ErroreAttesoOpenSPCoopLogCore err3 = new ErroreAttesoOpenSPCoopLogCore();
		err3.setIntervalloInferiore(dataInizioTest);
		err3.setIntervalloSuperiore(dataFineTest);
		err3.setMsgErrore("identificativo di correlazione applicativa non identificato nell'elemento [*] con modalita' di acquisizione contentBased (Pattern://test:idApplicativo/text()): Espressione XPATH non applicabile al messaggio: org.apache.xpath.domapi.XPathStylesheetDOM3Exception: Prefix must resolve to a namespace: test");
		this.erroriAttesiOpenSPCoopCore.add(err3);
		
		ErroreAttesoOpenSPCoopLogCore err4 = new ErroreAttesoOpenSPCoopLogCore();
		err4.setIntervalloInferiore(dataInizioTest);
		err4.setIntervalloSuperiore(dataFineTest);
		err4.setMsgErrore("identificativo di correlazione applicativa non identificato nell'elemento [*] con modalita' di acquisizione contentBased (Pattern://test:idApplicativo/text()): Espressione XPATH non applicabile al messaggio: com.sun.org.apache.xpath.internal.domapi.XPathStylesheetDOM3Exception: Prefix must resolve to a namespace: test");
		this.erroriAttesiOpenSPCoopCore.add(err4);

	}
	@DataProvider (name="SincronoCorrelazioneApplicativaContentBasedDatiMancanti")
	public Object[][]testSincronoCorrelazioneApplicativaContentBasedDatiMancanti()throws Exception{
		String id=this.repositorySincronoCorrelazioneApplicativaContentBasedDatiMancanti.getNext();
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),id,false},	
				{DatabaseProperties.getDatabaseComponentErogatore(),id,true}	
		};
	}
	@Test(groups={IntegrazioneCorrelazioneApplicativa.ID_GRUPPO,IntegrazioneCorrelazioneApplicativa.ID_GRUPPO+".CORRELAZIONE_APPLICATIVA_CONTENT_BASED_DATI_MANCANTI"},dataProvider="SincronoCorrelazioneApplicativaContentBasedDatiMancanti",dependsOnMethods={"sincronoCorrelazioneApplicativaContentBasedDatiMancanti"})
	public void testSincronoCorrelazioneApplicativaContentBasedDatiMancanti(DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
		try{
			Reporter.log("Controllo tracciamento richiesta non effettuato con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id)==false);
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/***
	 * Test Correlazione Applicativa Content Based
	 */
		
	private String runClientCorrelazioneApplicativaContentBased_erroreAccettato(String idUnivoco) throws Exception{
		java.io.FileInputStream fin = null;
		DatabaseComponent dbComponentFruitore = null;
		try{
			fin = new java.io.FileInputStream(new File(Utilities.testSuiteProperties.getSoap11FileName()));

			Message msg=new Message(fin);
			msg.getSOAPPartAsBytes();

			MessageElement elem = (MessageElement) msg.getSOAPBody().addChildElement("idApplicativoERRORE", "testERRORE", "http://www.openspcoop.org");
			elem.setValue(idUnivoco);
			Reporter.log("Messaggio: "+msg.getSOAPPartAsString());
			msg.getSOAPPartAsBytes();
			
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaContentBased_erroreAccettato);
			client.setSoapAction("\"TEST\"");
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_PROFILO_SINCRONO_INTEGRAZIONE_CORRELAZIONE_APPLICATIVA_CONTENT_BASED_ACCETTA_CON_IDENTIFICAZIONE_NON_RIUSCITA);
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
			} catch (AxisFault error) {
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				throw error;
			}finally{
				dbComponentFruitore.close();
			}
			
			checkHttpRisposta(client.getPropertiesTrasportoRisposta(),CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_INTEGRAZIONE,client.getIdMessaggio());
			
			checkMessaggioRisposta(client.getResponseMessage(),CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_INTEGRAZIONE,client.getIdMessaggio());
			
			return client.getIdMessaggio();
			
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
	
	Repository repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaContentBased_erroreAccettato=new Repository();
	Repository repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaContentBasedIDUnivoco_erroreAccettato=new Repository();
	@Test(groups={IntegrazioneCorrelazioneApplicativa.ID_GRUPPO,IntegrazioneCorrelazioneApplicativa.ID_GRUPPO+".CORRELAZIONE_APPLICATIVA_CONTENT_BASED_ACCETTA_IDENTIFICAZIONE_NON_RIUSCITA"},description="Test di tipo sincrono, Viene controllato se i body sono uguali e se gli attachment sono uguali")
	public void sincronoCorrelazioneApplicativaContentBased_erroreAccettato() throws TestSuiteException, IOException, Exception{
		
		String idUnivoco = "XXXXXXXIDXXXXXXXX_"+System.currentTimeMillis();
		this.repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaContentBasedIDUnivoco_erroreAccettato.add(idUnivoco);
		
		String id1 = runClientCorrelazioneApplicativaContentBased_erroreAccettato(idUnivoco);
		
		Reporter.log("Sleep 4 secondi...");
		try{
			Thread.sleep(4000);
		}catch(Exception e){}
		
		String id2 = runClientCorrelazioneApplicativaContentBased_erroreAccettato(idUnivoco);
		
		Reporter.log("ID 1st invocazione["+id1+"]");
		Reporter.log("ID 2nd invocazione["+id2+"]");
		Assert.assertTrue(id1.equals(id2)==false);
	}
	@DataProvider (name="SincronoCorrelazioneApplicativaContentBased_erroreAccettato")
	public Object[][]testSincronoCorrelazioneApplicativaContentBased_erroreAccettato()throws Exception{
		String id=this.repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaContentBased_erroreAccettato.getNext();
		String idSecondaInvocazione=this.repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaContentBased_erroreAccettato.getNext();
		String idUnivoco = this.repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaContentBasedIDUnivoco_erroreAccettato.getNext();
		return new Object[][]{
				{idUnivoco,idSecondaInvocazione,DatabaseProperties.getDatabaseComponentDiagnosticaFruitore(),DatabaseProperties.getDatabaseComponentFruitore(),id,false},	
				{idUnivoco,idSecondaInvocazione,null,DatabaseProperties.getDatabaseComponentErogatore(),id,false}	
		};
	}
	@Test(groups={IntegrazioneCorrelazioneApplicativa.ID_GRUPPO,IntegrazioneCorrelazioneApplicativa.ID_GRUPPO+".CORRELAZIONE_APPLICATIVA_CONTENT_BASED_ACCETTA_IDENTIFICAZIONE_NON_RIUSCITA"},
			dataProvider="SincronoCorrelazioneApplicativaContentBased_erroreAccettato",dependsOnMethods={"sincronoCorrelazioneApplicativaContentBased_erroreAccettato"})
	public void testSincronoCorrelazioneApplicativaContentBased_erroreAccettato(String idUnivoco,String idSecondaInvocazione,DatabaseMsgDiagnosticiComponent dataMsgDiag,DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
		try{
			this.collaborazioneSPCoopBase.testSincrono(data, id, CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_INTEGRAZIONE, checkServizioApplicativo,
					null);
			
			Reporter.log("Numero messaggi arrivati al servizio applicativo: "+data.getVerificatoreTracciaRichiesta().isArrivedCount(id));
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isArrivedCount(id)==1);
			
			this.collaborazioneSPCoopBase.testSincrono(data, idSecondaInvocazione, CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_INTEGRAZIONE, checkServizioApplicativo,
					null);
			
			Reporter.log("Numero messaggi arrivati al servizio applicativo: "+data.getVerificatoreTracciaRichiesta().isArrivedCount(id));
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isArrivedCount(idSecondaInvocazione)==1);
			
			if(dataMsgDiag!=null){
				//fruitore
				
				String msgInizioCorrelazione = MSG_INIZIO_CORRELAZIONE.replace("@IDEGOV@", id);
				String [] msgRicerca = msgInizioCorrelazione.split("@IDAPPLICATIVO@");
				Reporter.log("Check inizio correlazione ["+msgInizioCorrelazione+"]");
				Assert.assertTrue(dataMsgDiag.isTracedMessaggio(msgRicerca)==false);
				
				String msgCorrelazioneAvvenuta = MSG_CORRELAZIONE.replace("@IDEGOV@", id);
				msgRicerca = msgCorrelazioneAvvenuta.split("@IDAPPLICATIVO@");
				Reporter.log("Check inizio correlazione ["+msgCorrelazioneAvvenuta+"]");
				Assert.assertTrue(dataMsgDiag.isTracedMessaggio(msgRicerca)==false);
				
				msgInizioCorrelazione = MSG_INIZIO_CORRELAZIONE.replace("@IDEGOV@", idSecondaInvocazione);
				msgRicerca = msgInizioCorrelazione.split("@IDAPPLICATIVO@");
				Reporter.log("Check inizio correlazione ["+msgInizioCorrelazione+"]");
				Assert.assertTrue(dataMsgDiag.isTracedMessaggio(msgRicerca)==false);
				
				msgCorrelazioneAvvenuta = MSG_CORRELAZIONE.replace("@IDEGOV@", idSecondaInvocazione);
				msgRicerca = msgCorrelazioneAvvenuta.split("@IDAPPLICATIVO@");
				Reporter.log("Check inizio correlazione ["+msgCorrelazioneAvvenuta+"]");
				Assert.assertTrue(dataMsgDiag.isTracedMessaggio(msgRicerca)==false);
				
				// check correlazione 
				Reporter.log("Check correlazione ["+idUnivoco+"] non esistente");
				Assert.assertTrue(dataMsgDiag.isTracedCorrelazioneApplicativaIntoMsgDiagnosticiCorrelazione(idUnivoco)==false);
			}
			
			// Check correlazione in tracciamento
			Reporter.log("Check correlazione ["+idUnivoco+"] in tracciamento nella richiesta non esistente");
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedCorrelazioneApplicativaRichiesta(id, this.collaborazioneSPCoopBase.getMittente(), idUnivoco)==false);
			Reporter.log("Check correlazione ["+idUnivoco+"] in tracciamento nella risposta non esistente");
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedCorrelazioneApplicativaRichiesta(id, this.collaborazioneSPCoopBase.getMittente(), idUnivoco)==false);
			
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
			if(dataMsgDiag!=null)
				dataMsgDiag.close();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/***
	 * Test Correlazione Applicativa Content Based con concat
	 */
		
	private String runClientCorrelazioneApplicativaContentBasedConcat(String portaDelegata,String idUnivoco,Repository repository) throws Exception{
		java.io.FileInputStream fin = null;
		DatabaseComponent dbComponentFruitore = null;
		try{
			fin = new java.io.FileInputStream(new File(Utilities.testSuiteProperties.getSoap11FileName()));

			Message msg=new Message(fin);
			msg.getSOAPPartAsBytes();

			MessageElement elem = (MessageElement) msg.getSOAPBody().addChildElement("idApplicativo", "test", "http://www.openspcoop.org");
			elem.setValue(idUnivoco);
			Reporter.log("Messaggio: "+msg.getSOAPPartAsString());
			msg.getSOAPPartAsBytes();
			
			ClientHttpGenerico client=new ClientHttpGenerico(repository);
			client.setSoapAction("\"TEST\"");
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
			} catch (AxisFault error) {
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				throw error;
			}finally{
				dbComponentFruitore.close();
			}
			
			checkHttpRisposta(client.getPropertiesTrasportoRisposta(),CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_INTEGRAZIONE,client.getIdMessaggio());
			
			checkMessaggioRisposta(client.getResponseMessage(),CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_INTEGRAZIONE,client.getIdMessaggio());
			
			return client.getIdMessaggio();
			
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
	
	Repository repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaContentBasedConcat=new Repository();
	Repository repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaContentBasedIDUnivocoConcat=new Repository();
	@Test(groups={IntegrazioneCorrelazioneApplicativa.ID_GRUPPO,IntegrazioneCorrelazioneApplicativa.ID_GRUPPO+".CORRELAZIONE_APPLICATIVA_CONTENT_BASED_CONCAT"},description="Test di tipo sincrono, Viene controllato se i body sono uguali e se gli attachment sono uguali")
	public void sincronoCorrelazioneApplicativaContentBasedConcat() throws TestSuiteException, IOException, Exception{
		
		String idUnivoco = "XXXXXXXIDXXXXXXXX_"+System.currentTimeMillis();
		this.repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaContentBasedIDUnivocoConcat.add(idUnivoco);
		
		String id1 = runClientCorrelazioneApplicativaContentBasedConcat(CostantiTestSuite.PORTA_DELEGATA_PROFILO_SINCRONO_INTEGRAZIONE_CORRELAZIONE_APPLICATIVA_CONTENT_BASED_CONCAT,
				idUnivoco,this.repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaContentBasedConcat);
		
		Reporter.log("Sleep 4 secondi...");
		try{
			Thread.sleep(4000);
		}catch(Exception e){}
		
		String id2 = runClientCorrelazioneApplicativaContentBasedConcat(CostantiTestSuite.PORTA_DELEGATA_PROFILO_SINCRONO_INTEGRAZIONE_CORRELAZIONE_APPLICATIVA_CONTENT_BASED_CONCAT,
				idUnivoco,this.repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaContentBasedConcat);
		
		Reporter.log("ID 1st invocazione["+id1+"]");
		Reporter.log("ID 2nd invocazione["+id2+"]");
		Assert.assertTrue(id1.equals(id2));
	}
	@DataProvider (name="SincronoCorrelazioneApplicativaContentBasedConcat")
	public Object[][]testSincronoCorrelazioneApplicativaContentBasedConcat()throws Exception{
		String id=this.repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaContentBasedConcat.getNext();
		String idUnivoco=this.repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaContentBasedIDUnivocoConcat.getNext();
		return new Object[][]{
				{idUnivoco,DatabaseProperties.getDatabaseComponentDiagnosticaFruitore(),DatabaseProperties.getDatabaseComponentFruitore(),id,false},	
				{idUnivoco,null,DatabaseProperties.getDatabaseComponentErogatore(),id,false}	
		};
	}
	@Test(groups={IntegrazioneCorrelazioneApplicativa.ID_GRUPPO,IntegrazioneCorrelazioneApplicativa.ID_GRUPPO+".CORRELAZIONE_APPLICATIVA_CONTENT_BASED_CONCAT"},
			dataProvider="SincronoCorrelazioneApplicativaContentBasedConcat",dependsOnMethods={"sincronoCorrelazioneApplicativaContentBasedConcat"})
	public void testSincronoCorrelazioneApplicativaContentBasedConcat(String idUnivoco,DatabaseMsgDiagnosticiComponent dataMsgDiag,DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
		try{
			this.collaborazioneSPCoopBase.testSincrono(data, id, CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_INTEGRAZIONE, checkServizioApplicativo,
					null);
			Reporter.log("Numero messaggi arrivati al servizio applicativo: "+data.getVerificatoreTracciaRichiesta().isArrivedCount(id));
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isArrivedCount(id)==2);
			
			if(dataMsgDiag!=null){
				//fruitore
				
				String msgInizioCorrelazione = MSG_INIZIO_CORRELAZIONE.replace("@IDAPPLICATIVO@", "BEGIN-ID_"+idUnivoco+"_END-ID");
				msgInizioCorrelazione = msgInizioCorrelazione.replace("@IDEGOV@", id);
				Reporter.log("Check inizio correlazione ["+msgInizioCorrelazione+"]");
				Assert.assertTrue(dataMsgDiag.isTracedMessaggio(msgInizioCorrelazione));
				
				String msgCorrelazioneAvvenuta = MSG_CORRELAZIONE.replace("@IDAPPLICATIVO@", "BEGIN-ID_"+idUnivoco+"_END-ID");
				msgCorrelazioneAvvenuta = msgCorrelazioneAvvenuta.replace("@IDEGOV@", id);
				Reporter.log("Check correlazione ["+msgCorrelazioneAvvenuta+"]");
				Assert.assertTrue(dataMsgDiag.isTracedMessaggio(msgCorrelazioneAvvenuta));
				
				// check correlazione 
				Reporter.log("Check correlazione ["+"BEGIN-ID_"+idUnivoco+"_END-ID"+"]");
				Assert.assertTrue(dataMsgDiag.isTracedCorrelazioneApplicativaIntoMsgDiagnosticiCorrelazione("BEGIN-ID_"+idUnivoco+"_END-ID"));
			}
			
			// Check correlazione in tracciamento
			Reporter.log("Check correlazione ["+"BEGIN-ID_"+idUnivoco+"_END-ID"+"] in tracciamento nella richiesta");
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedCorrelazioneApplicativaRichiesta(id, this.collaborazioneSPCoopBase.getMittente(), "BEGIN-ID_"+idUnivoco+"_END-ID"));
			Reporter.log("Check correlazione ["+"BEGIN-ID_"+idUnivoco+"_END-ID"+"] in tracciamento nella risposta");
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedCorrelazioneApplicativaRichiesta(id, this.collaborazioneSPCoopBase.getMittente(), "BEGIN-ID_"+idUnivoco+"_END-ID"));
			
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
			if(dataMsgDiag!=null)
				dataMsgDiag.close();
		}
	}
	
	
	Repository repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaContentBasedConcatSenzaRiusoIDEgov=new Repository();
	Repository repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaContentBasedSenzaRiusoIDEgovIDUnivocoConcat=new Repository();
	@Test(groups={IntegrazioneCorrelazioneApplicativa.ID_GRUPPO,IntegrazioneCorrelazioneApplicativa.ID_GRUPPO+".CORRELAZIONE_APPLICATIVA_CONTENT_BASED_CONCAT"},
			description="Test di tipo sincrono, Viene controllato se i body sono uguali e se gli attachment sono uguali",
			dependsOnMethods={"testSincronoCorrelazioneApplicativaContentBasedConcat"})
	public void sincronoCorrelazioneApplicativaContentBasedConcatSenzaRiusoIDEgov() throws TestSuiteException, IOException, Exception{
		
		String idUnivoco = "XXXXXXXIDXXXXXXXX_"+System.currentTimeMillis();
		this.repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaContentBasedSenzaRiusoIDEgovIDUnivocoConcat.add(idUnivoco);
		
		String id1 = runClientCorrelazioneApplicativaContentBasedConcat(CostantiTestSuite.PORTA_DELEGATA_PROFILO_SINCRONO_INTEGRAZIONE_CORRELAZIONE_APPLICATIVA_CONTENT_BASED_CONCAT_SENZA_RIUSO_IDEGOV,
				idUnivoco,this.repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaContentBasedConcatSenzaRiusoIDEgov);
		
		Reporter.log("Sleep 4 secondi...");
		try{
			Thread.sleep(4000);
		}catch(Exception e){}
		
		String id2 = runClientCorrelazioneApplicativaContentBasedConcat(CostantiTestSuite.PORTA_DELEGATA_PROFILO_SINCRONO_INTEGRAZIONE_CORRELAZIONE_APPLICATIVA_CONTENT_BASED_CONCAT_SENZA_RIUSO_IDEGOV,
				idUnivoco,this.repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaContentBasedConcatSenzaRiusoIDEgov);
		
		Reporter.log("ID 1st invocazione["+id1+"]");
		Reporter.log("ID 2nd invocazione["+id2+"]");
		Assert.assertTrue(id1.equals(id2)==false);
	}
	@DataProvider (name="SincronoCorrelazioneApplicativaContentBasedConcatSenzaRiusoIDEgov")
	public Object[][]testSincronoCorrelazioneApplicativaContentBasedConcatSenzaRiusoIDEgov()throws Exception{
		String id=this.repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaContentBasedConcatSenzaRiusoIDEgov.getNext();
		String idUnivoco=this.repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaContentBasedSenzaRiusoIDEgovIDUnivocoConcat.getNext();
		return new Object[][]{
				{idUnivoco,DatabaseProperties.getDatabaseComponentDiagnosticaFruitore(),DatabaseProperties.getDatabaseComponentFruitore(),id,false},	
				{idUnivoco,null,DatabaseProperties.getDatabaseComponentErogatore(),id,false}	
		};
	}
	@Test(groups={IntegrazioneCorrelazioneApplicativa.ID_GRUPPO,IntegrazioneCorrelazioneApplicativa.ID_GRUPPO+".CORRELAZIONE_APPLICATIVA_CONTENT_BASED_CONCAT"},
			dataProvider="SincronoCorrelazioneApplicativaContentBasedConcatSenzaRiusoIDEgov",dependsOnMethods={"sincronoCorrelazioneApplicativaContentBasedConcatSenzaRiusoIDEgov"})
	public void testSincronoCorrelazioneApplicativaContentBasedConcatSenzaRiusoIDEgov(String idUnivoco,DatabaseMsgDiagnosticiComponent dataMsgDiag,DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
		try{
			this.collaborazioneSPCoopBase.testSincrono(data, id, CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_INTEGRAZIONE, checkServizioApplicativo,
					null);
			Reporter.log("Numero messaggi arrivati al servizio applicativo: "+data.getVerificatoreTracciaRichiesta().isArrivedCount(id));
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isArrivedCount(id)==1);
			
			if(dataMsgDiag!=null){
				//fruitore
				
				String msgInizioCorrelazione = MSG_INIZIO_CORRELAZIONE.replace("@IDAPPLICATIVO@", "BEGIN-ID_"+idUnivoco+"_END-ID");
				msgInizioCorrelazione = msgInizioCorrelazione.replace("@IDEGOV@", id);
				Reporter.log("Check inizio correlazione ["+msgInizioCorrelazione+"] non esistente");
				Assert.assertTrue(dataMsgDiag.isTracedMessaggio(msgInizioCorrelazione) == false);
				
				String msgCorrelazioneAvvenuta = MSG_CORRELAZIONE.replace("@IDAPPLICATIVO@", "BEGIN-ID_"+idUnivoco+"_END-ID");
				msgCorrelazioneAvvenuta = msgCorrelazioneAvvenuta.replace("@IDEGOV@", id);
				Reporter.log("Check correlazione ["+msgCorrelazioneAvvenuta+"] non esistente");
				Assert.assertTrue(dataMsgDiag.isTracedMessaggio(msgCorrelazioneAvvenuta) == false);
				
				// check correlazione 
				Reporter.log("Check correlazione ["+"BEGIN-ID_"+idUnivoco+"_END-ID"+"]");
				Assert.assertTrue(dataMsgDiag.isTracedCorrelazioneApplicativaIntoMsgDiagnosticiCorrelazione("BEGIN-ID_"+idUnivoco+"_END-ID"));
			}
			
			// Check correlazione in tracciamento
			Reporter.log("Check correlazione ["+"BEGIN-ID_"+idUnivoco+"_END-ID"+"] in tracciamento nella richiesta");
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedCorrelazioneApplicativaRichiesta(id, this.collaborazioneSPCoopBase.getMittente(), "BEGIN-ID_"+idUnivoco+"_END-ID"));
			Reporter.log("Check correlazione ["+"BEGIN-ID_"+idUnivoco+"_END-ID"+"] in tracciamento nella risposta");
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedCorrelazioneApplicativaRichiesta(id, this.collaborazioneSPCoopBase.getMittente(), "BEGIN-ID_"+idUnivoco+"_END-ID"));
			
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
			if(dataMsgDiag!=null)
				dataMsgDiag.close();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/***
	 * Test Correlazione Applicativa Content Based con concat, fornendo ID errato. Il concat non ritorna errore, quindi l'ID applicativo di correlazione
	 * tiene solo le costanti.
	 */
		
	private String runClientCorrelazioneApplicativaContentBasedConcat_erroreIdentificazione(String idUnivoco) throws Exception{
		java.io.FileInputStream fin = null;
		DatabaseComponent dbComponentFruitore = null;
		try{
			fin = new java.io.FileInputStream(new File(Utilities.testSuiteProperties.getSoap11FileName()));

			Message msg=new Message(fin);
			msg.getSOAPPartAsBytes();

			MessageElement elem = (MessageElement) msg.getSOAPBody().addChildElement("idApplicativ", "test", "http://www.openspcoop.org");
			elem.setValue(idUnivoco);
			Reporter.log("Messaggio: "+msg.getSOAPPartAsString());
			msg.getSOAPPartAsBytes();
			
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaContentBasedConcat_erroreIdentificazione);
			client.setSoapAction("\"TEST\"");
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_PROFILO_SINCRONO_INTEGRAZIONE_CORRELAZIONE_APPLICATIVA_CONTENT_BASED_CONCAT);
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
			} catch (AxisFault error) {
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				throw error;
			}finally{
				dbComponentFruitore.close();
			}
			
			checkHttpRisposta(client.getPropertiesTrasportoRisposta(),CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_INTEGRAZIONE,client.getIdMessaggio());
			
			checkMessaggioRisposta(client.getResponseMessage(),CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_INTEGRAZIONE,client.getIdMessaggio());
			
			return client.getIdMessaggio();
			
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
	
	Repository repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaContentBasedConcat_erroreIdentificazione=new Repository();
	Repository repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaContentBasedIDUnivocoConcat_erroreIdentificazione=new Repository();
	@Test(groups={IntegrazioneCorrelazioneApplicativa.ID_GRUPPO,IntegrazioneCorrelazioneApplicativa.ID_GRUPPO+".CORRELAZIONE_APPLICATIVA_CONTENT_BASED_CONCAT_IDENTIFICAZIONE_ERRATA"},description="Test di tipo sincrono, Viene controllato se i body sono uguali e se gli attachment sono uguali")
	public void sincronoCorrelazioneApplicativaContentBasedConcat_erroreIdentificazione() throws TestSuiteException, IOException, Exception{
		
		String idUnivoco = "XXXXXXXIDXXXXXXXX_"+System.currentTimeMillis();
		this.repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaContentBasedIDUnivocoConcat_erroreIdentificazione.add(idUnivoco);
		
		// Siccome la funzione concat non si arrabbia per i casi in cui non viene identificato la correlazione,
		// rimane la correlazione attiva nel database openspcoop.
		// Questo fa si che successivi riavvi della testsuite falliscono.
		// Viene per questo effettuata l'eliminazione della correlazione, prima di avviare il test.
		DatabaseComponent dbComponentFruitore = null;
		try{
			dbComponentFruitore= DatabaseProperties.getDatabaseComponentFruitore();
			dbComponentFruitore.getVerificatoreMessaggi().deleteCorrelazioneApplicativa("BEGIN-ID_"+"_END-ID");
		}catch(Exception e){
			throw e;
		}finally{
			try{
				dbComponentFruitore.close();
			}catch(Exception e){}
		}
		
		String id1 = runClientCorrelazioneApplicativaContentBasedConcat_erroreIdentificazione(idUnivoco);
		
		Reporter.log("Sleep 4 secondi...");
		try{
			Thread.sleep(4000);
		}catch(Exception e){}
		
		String id2 = runClientCorrelazioneApplicativaContentBasedConcat_erroreIdentificazione(idUnivoco);
		
		Reporter.log("ID 1st invocazione["+id1+"]");
		Reporter.log("ID 2nd invocazione["+id2+"]");
		Assert.assertTrue(id1.equals(id2));
	}
	@DataProvider (name="SincronoCorrelazioneApplicativaContentBasedConcat_erroreIdentificazione")
	public Object[][]testSincronoCorrelazioneApplicativaContentBasedConcat_erroreIdentificazione()throws Exception{
		String id=this.repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaContentBasedConcat_erroreIdentificazione.getNext();
		String idUnivoco=this.repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaContentBasedIDUnivocoConcat_erroreIdentificazione.getNext();
		return new Object[][]{
				{idUnivoco,DatabaseProperties.getDatabaseComponentDiagnosticaFruitore(),DatabaseProperties.getDatabaseComponentFruitore(),id,false},	
				{idUnivoco,null,DatabaseProperties.getDatabaseComponentErogatore(),id,false}	
		};
	}
	@Test(groups={IntegrazioneCorrelazioneApplicativa.ID_GRUPPO,IntegrazioneCorrelazioneApplicativa.ID_GRUPPO+".CORRELAZIONE_APPLICATIVA_CONTENT_BASED_CONCAT_IDENTIFICAZIONE_ERRATA"},
			dataProvider="SincronoCorrelazioneApplicativaContentBasedConcat_erroreIdentificazione",dependsOnMethods={"sincronoCorrelazioneApplicativaContentBasedConcat_erroreIdentificazione"})
	public void testSincronoCorrelazioneApplicativaContentBasedConcat_erroreIdentificazione(String idUnivoco,DatabaseMsgDiagnosticiComponent dataMsgDiag,DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
		try{
			this.collaborazioneSPCoopBase.testSincrono(data, id, CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_INTEGRAZIONE, checkServizioApplicativo,
					null);
			Reporter.log("Numero messaggi arrivati al servizio applicativo: "+data.getVerificatoreTracciaRichiesta().isArrivedCount(id));
			Assert.assertTrue( data.getVerificatoreTracciaRichiesta().isArrivedCount(id)==2 );
			
			if(dataMsgDiag!=null){
				//fruitore
				
				String msgInizioCorrelazione = MSG_INIZIO_CORRELAZIONE.replace("@IDAPPLICATIVO@", "BEGIN-ID_"+"_END-ID");
				msgInizioCorrelazione = msgInizioCorrelazione.replace("@IDEGOV@", id);
				Reporter.log("Check inizio correlazione ["+msgInizioCorrelazione+"]");
				Assert.assertTrue(dataMsgDiag.isTracedMessaggio(msgInizioCorrelazione));
				
				String msgCorrelazioneAvvenuta = MSG_CORRELAZIONE.replace("@IDAPPLICATIVO@", "BEGIN-ID_"+"_END-ID");
				msgCorrelazioneAvvenuta = msgCorrelazioneAvvenuta.replace("@IDEGOV@", id);
				Reporter.log("Check correlazione ["+msgCorrelazioneAvvenuta+"]");
				Assert.assertTrue(dataMsgDiag.isTracedMessaggio(msgCorrelazioneAvvenuta));
				
				// check correlazione 
				Reporter.log("Check correlazione ["+"BEGIN-ID_"+"_END-ID"+"]");
				Assert.assertTrue(dataMsgDiag.isTracedCorrelazioneApplicativaIntoMsgDiagnosticiCorrelazione("BEGIN-ID_"+"_END-ID"));
			}
			
			// Check correlazione in tracciamento
			Reporter.log("Check correlazione ["+"BEGIN-ID_"+"_END-ID"+"] in tracciamento nella richiesta");
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedCorrelazioneApplicativaRichiesta(id, this.collaborazioneSPCoopBase.getMittente(), "BEGIN-ID_"+"_END-ID"));
			Reporter.log("Check correlazione ["+"BEGIN-ID_"+"_END-ID"+"] in tracciamento nella risposta");
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedCorrelazioneApplicativaRichiesta(id, this.collaborazioneSPCoopBase.getMittente(), "BEGIN-ID_"+"_END-ID"));
			
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
			if(dataMsgDiag!=null)
				dataMsgDiag.close();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/***
	 * Test Correlazione Applicativa Content Based con concat_openspcoop
	 */
		
	private String runClientCorrelazioneApplicativaContentBasedConcatOpenSPCoop(String portaDelegata,String idUnivoco,Repository repository) throws Exception{
		java.io.FileInputStream fin = null;
		DatabaseComponent dbComponentFruitore = null;
		try{
			fin = new java.io.FileInputStream(new File(Utilities.testSuiteProperties.getSoap11FileName()));

			Message msg=new Message(fin);
			msg.getSOAPPartAsBytes();

			MessageElement elem = (MessageElement) msg.getSOAPBody().addChildElement("idApplicativo", "test", "http://www.openspcoop.org");
			elem.setValue(idUnivoco);
			Reporter.log("Messaggio: "+msg.getSOAPPartAsString());
			msg.getSOAPPartAsBytes();
			
			ClientHttpGenerico client=new ClientHttpGenerico(repository);
			client.setSoapAction("\"TEST\"");
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
			} catch (AxisFault error) {
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				throw error;
			}finally{
				dbComponentFruitore.close();
			}
			
			checkHttpRisposta(client.getPropertiesTrasportoRisposta(),CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_INTEGRAZIONE,client.getIdMessaggio());
			
			checkMessaggioRisposta(client.getResponseMessage(),CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_INTEGRAZIONE,client.getIdMessaggio());
			
			return client.getIdMessaggio();
			
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
	
	Repository repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaContentBasedConcatOpenSPCoop=new Repository();
	Repository repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaContentBasedIDUnivocoConcatOpenSPCoop=new Repository();
	@Test(groups={IntegrazioneCorrelazioneApplicativa.ID_GRUPPO,IntegrazioneCorrelazioneApplicativa.ID_GRUPPO+".CORRELAZIONE_APPLICATIVA_CONTENT_BASED_CONCAT_OPENSPCOOP"},description="Test di tipo sincrono, Viene controllato se i body sono uguali e se gli attachment sono uguali")
	public void sincronoCorrelazioneApplicativaContentBasedConcatOpenSPCoop() throws TestSuiteException, IOException, Exception{
		
		String idUnivoco = "XXXXXXXIDXXXXXXXX_"+System.currentTimeMillis();
		this.repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaContentBasedIDUnivocoConcatOpenSPCoop.add(idUnivoco);
		
		String id1 = runClientCorrelazioneApplicativaContentBasedConcatOpenSPCoop(CostantiTestSuite.PORTA_DELEGATA_PROFILO_SINCRONO_INTEGRAZIONE_CORRELAZIONE_APPLICATIVA_CONTENT_BASED_CONCAT_OPENSPCOOP,
				idUnivoco,this.repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaContentBasedConcatOpenSPCoop);
		
		Reporter.log("Sleep 4 secondi...");
		try{
			Thread.sleep(4000);
		}catch(Exception e){}
		
		String id2 = runClientCorrelazioneApplicativaContentBasedConcatOpenSPCoop(CostantiTestSuite.PORTA_DELEGATA_PROFILO_SINCRONO_INTEGRAZIONE_CORRELAZIONE_APPLICATIVA_CONTENT_BASED_CONCAT_OPENSPCOOP,
				idUnivoco,this.repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaContentBasedConcatOpenSPCoop);
		
		Reporter.log("ID 1st invocazione["+id1+"]");
		Reporter.log("ID 2nd invocazione["+id2+"]");
		Assert.assertTrue(id1.equals(id2));
	}
	@DataProvider (name="SincronoCorrelazioneApplicativaContentBasedConcatOpenSPCoop")
	public Object[][]testSincronoCorrelazioneApplicativaContentBasedConcatOpenSPCoop()throws Exception{
		String id=this.repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaContentBasedConcatOpenSPCoop.getNext();
		String idUnivoco=this.repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaContentBasedIDUnivocoConcatOpenSPCoop.getNext();
		return new Object[][]{
				{idUnivoco,DatabaseProperties.getDatabaseComponentDiagnosticaFruitore(),DatabaseProperties.getDatabaseComponentFruitore(),id,false},	
				{idUnivoco,null,DatabaseProperties.getDatabaseComponentErogatore(),id,false}	
		};
	}
	@Test(groups={IntegrazioneCorrelazioneApplicativa.ID_GRUPPO,IntegrazioneCorrelazioneApplicativa.ID_GRUPPO+".CORRELAZIONE_APPLICATIVA_CONTENT_BASED_CONCAT_OPENSPCOOP"},
			dataProvider="SincronoCorrelazioneApplicativaContentBasedConcatOpenSPCoop",dependsOnMethods={"sincronoCorrelazioneApplicativaContentBasedConcatOpenSPCoop"})
	public void testSincronoCorrelazioneApplicativaContentBasedConcatOpenSPCoop(String idUnivoco,DatabaseMsgDiagnosticiComponent dataMsgDiag,DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
		try{
			this.collaborazioneSPCoopBase.testSincrono(data, id, CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_INTEGRAZIONE, checkServizioApplicativo,
					null);
			Reporter.log("Numero messaggi arrivati al servizio applicativo: "+data.getVerificatoreTracciaRichiesta().isArrivedCount(id));
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isArrivedCount(id)==2);
			
			if(dataMsgDiag!=null){
				//fruitore
				
				String msgInizioCorrelazione = MSG_INIZIO_CORRELAZIONE.replace("@IDAPPLICATIVO@", "BEGIN-ID_"+idUnivoco+"_END-ID");
				msgInizioCorrelazione = msgInizioCorrelazione.replace("@IDEGOV@", id);
				Reporter.log("Check inizio correlazione ["+msgInizioCorrelazione+"]");
				Assert.assertTrue(dataMsgDiag.isTracedMessaggio(msgInizioCorrelazione));
				
				String msgCorrelazioneAvvenuta = MSG_CORRELAZIONE.replace("@IDAPPLICATIVO@", "BEGIN-ID_"+idUnivoco+"_END-ID");
				msgCorrelazioneAvvenuta = msgCorrelazioneAvvenuta.replace("@IDEGOV@", id);
				Reporter.log("Check correlazione ["+msgCorrelazioneAvvenuta+"]");
				Assert.assertTrue(dataMsgDiag.isTracedMessaggio(msgCorrelazioneAvvenuta));
				
				// check correlazione 
				Reporter.log("Check correlazione ["+"BEGIN-ID_"+idUnivoco+"_END-ID"+"]");
				Assert.assertTrue(dataMsgDiag.isTracedCorrelazioneApplicativaIntoMsgDiagnosticiCorrelazione("BEGIN-ID_"+idUnivoco+"_END-ID"));
			}
			
			// Check correlazione in tracciamento
			Reporter.log("Check correlazione ["+"BEGIN-ID_"+idUnivoco+"_END-ID"+"] in tracciamento nella richiesta");
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedCorrelazioneApplicativaRichiesta(id, this.collaborazioneSPCoopBase.getMittente(), "BEGIN-ID_"+idUnivoco+"_END-ID"));
			Reporter.log("Check correlazione ["+"BEGIN-ID_"+idUnivoco+"_END-ID"+"] in tracciamento nella risposta");
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedCorrelazioneApplicativaRichiesta(id, this.collaborazioneSPCoopBase.getMittente(), "BEGIN-ID_"+idUnivoco+"_END-ID"));
			
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
			if(dataMsgDiag!=null)
				dataMsgDiag.close();
		}
	}
	
	
	Repository repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaContentBasedConcatOpenSPCoopSenzaRiusoIDEgov=new Repository();
	Repository repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaContentBasedIDUnivocoConcatOpenSPCoopSenzaRiusoIDEgov=new Repository();
	@Test(groups={IntegrazioneCorrelazioneApplicativa.ID_GRUPPO,IntegrazioneCorrelazioneApplicativa.ID_GRUPPO+".CORRELAZIONE_APPLICATIVA_CONTENT_BASED_CONCAT_OPENSPCOOP"},
			description="Test di tipo sincrono, Viene controllato se i body sono uguali e se gli attachment sono uguali",
			dependsOnMethods={"testSincronoCorrelazioneApplicativaContentBasedConcatOpenSPCoop"})
	public void sincronoCorrelazioneApplicativaContentBasedConcatOpenSPCoopSenzaRiusoIDEgov() throws TestSuiteException, IOException, Exception{
		
		String idUnivoco = "XXXXXXXIDXXXXXXXX_"+System.currentTimeMillis();
		this.repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaContentBasedIDUnivocoConcatOpenSPCoopSenzaRiusoIDEgov.add(idUnivoco);
		
		String id1 = runClientCorrelazioneApplicativaContentBasedConcatOpenSPCoop(CostantiTestSuite.PORTA_DELEGATA_PROFILO_SINCRONO_INTEGRAZIONE_CORRELAZIONE_APPLICATIVA_CONTENT_BASED_CONCAT_OPENSPCOOP_SENZA_RIUSO_IDEGOV,
				idUnivoco,this.repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaContentBasedConcatOpenSPCoopSenzaRiusoIDEgov);
		
		Reporter.log("Sleep 4 secondi...");
		try{
			Thread.sleep(4000);
		}catch(Exception e){}
		
		String id2 = runClientCorrelazioneApplicativaContentBasedConcatOpenSPCoop(CostantiTestSuite.PORTA_DELEGATA_PROFILO_SINCRONO_INTEGRAZIONE_CORRELAZIONE_APPLICATIVA_CONTENT_BASED_CONCAT_OPENSPCOOP_SENZA_RIUSO_IDEGOV,
				idUnivoco,this.repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaContentBasedConcatOpenSPCoopSenzaRiusoIDEgov);
		
		Reporter.log("ID 1st invocazione["+id1+"]");
		Reporter.log("ID 2nd invocazione["+id2+"]");
		Assert.assertTrue(id1.equals(id2)==false);
	}
	@DataProvider (name="SincronoCorrelazioneApplicativaContentBasedConcatOpenSPCoopSenzaRiusoIDEgov")
	public Object[][]testSincronoCorrelazioneApplicativaContentBasedConcatOpenSPCoopSenzaRiusoIDEgov()throws Exception{
		String id=this.repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaContentBasedConcatOpenSPCoopSenzaRiusoIDEgov.getNext();
		String idUnivoco=this.repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaContentBasedIDUnivocoConcatOpenSPCoopSenzaRiusoIDEgov.getNext();
		return new Object[][]{
				{idUnivoco,DatabaseProperties.getDatabaseComponentDiagnosticaFruitore(),DatabaseProperties.getDatabaseComponentFruitore(),id,false},	
				{idUnivoco,null,DatabaseProperties.getDatabaseComponentErogatore(),id,false}	
		};
	}
	@Test(groups={IntegrazioneCorrelazioneApplicativa.ID_GRUPPO,IntegrazioneCorrelazioneApplicativa.ID_GRUPPO+".CORRELAZIONE_APPLICATIVA_CONTENT_BASED_CONCAT_OPENSPCOOP"},
			dataProvider="SincronoCorrelazioneApplicativaContentBasedConcatOpenSPCoopSenzaRiusoIDEgov",
			dependsOnMethods={"sincronoCorrelazioneApplicativaContentBasedConcatOpenSPCoopSenzaRiusoIDEgov"})
	public void testSincronoCorrelazioneApplicativaContentBasedConcatOpenSPCoopSenzaRiusoIDEgov(String idUnivoco,DatabaseMsgDiagnosticiComponent dataMsgDiag,DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
		try{
			this.collaborazioneSPCoopBase.testSincrono(data, id, CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_INTEGRAZIONE, checkServizioApplicativo,
					null);
			Reporter.log("Numero messaggi arrivati al servizio applicativo: "+data.getVerificatoreTracciaRichiesta().isArrivedCount(id));
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isArrivedCount(id)==1);
			
			if(dataMsgDiag!=null){
				//fruitore
				
				String msgInizioCorrelazione = MSG_INIZIO_CORRELAZIONE.replace("@IDAPPLICATIVO@", "BEGIN-ID_"+idUnivoco+"_END-ID");
				msgInizioCorrelazione = msgInizioCorrelazione.replace("@IDEGOV@", id);
				Reporter.log("Check inizio correlazione ["+msgInizioCorrelazione+"] non esistente");
				Assert.assertTrue(dataMsgDiag.isTracedMessaggio(msgInizioCorrelazione)==false);
				
				String msgCorrelazioneAvvenuta = MSG_CORRELAZIONE.replace("@IDAPPLICATIVO@", "BEGIN-ID_"+idUnivoco+"_END-ID");
				msgCorrelazioneAvvenuta = msgCorrelazioneAvvenuta.replace("@IDEGOV@", id);
				Reporter.log("Check correlazione ["+msgCorrelazioneAvvenuta+"] non esistente");
				Assert.assertTrue(dataMsgDiag.isTracedMessaggio(msgCorrelazioneAvvenuta)==false);
				
				// check correlazione 
				Reporter.log("Check correlazione ["+"BEGIN-ID_"+idUnivoco+"_END-ID"+"]");
				Assert.assertTrue(dataMsgDiag.isTracedCorrelazioneApplicativaIntoMsgDiagnosticiCorrelazione("BEGIN-ID_"+idUnivoco+"_END-ID"));
			}
			
			// Check correlazione in tracciamento
			Reporter.log("Check correlazione ["+"BEGIN-ID_"+idUnivoco+"_END-ID"+"] in tracciamento nella richiesta");
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedCorrelazioneApplicativaRichiesta(id, this.collaborazioneSPCoopBase.getMittente(), "BEGIN-ID_"+idUnivoco+"_END-ID"));
			Reporter.log("Check correlazione ["+"BEGIN-ID_"+idUnivoco+"_END-ID"+"] in tracciamento nella risposta");
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedCorrelazioneApplicativaRichiesta(id, this.collaborazioneSPCoopBase.getMittente(), "BEGIN-ID_"+idUnivoco+"_END-ID"));
			
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
			if(dataMsgDiag!=null)
				dataMsgDiag.close();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/***
	 * Test Correlazione Applicativa Content Based con concat_openspcoop
	 */
		
	private String runClientCorrelazioneApplicativaContentBasedConcatOpenSPCoop_erroreIdentificazione(String idUnivoco) throws Exception{
		java.io.FileInputStream fin = null;
		DatabaseComponent dbComponentFruitore = null;
		try{
			fin = new java.io.FileInputStream(new File(Utilities.testSuiteProperties.getSoap11FileName()));

			Message msg=new Message(fin);
			msg.getSOAPPartAsBytes();

			MessageElement elem = (MessageElement) msg.getSOAPBody().addChildElement("idApplicativ", "test", "http://www.openspcoop.org");
			elem.setValue(idUnivoco);
			Reporter.log("Messaggio: "+msg.getSOAPPartAsString());
			msg.getSOAPPartAsBytes();
			
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaContentBasedConcatOpenSPCoop_erroreIdentificazione);
			client.setSoapAction("\"TEST\"");
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_PROFILO_SINCRONO_INTEGRAZIONE_CORRELAZIONE_APPLICATIVA_CONTENT_BASED_CONCAT_OPENSPCOOP);
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
			} catch (AxisFault error) {
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				Reporter.log("Controllo fault code [OPENSPCOOP_ORG_416]");
				Assert.assertTrue(Utilities.toString(CodiceErroreIntegrazione.CODICE_416_CORRELAZIONE_APPLICATIVA_RICHIESTA_ERRORE).equals(error.getFaultCode().getLocalPart()));
				String msgErrore = "La gestione della funzionalità di correlazione applicativa, per il messaggio di richiesta, ha generato un errore: identificativo di correlazione applicativa non identificato nell'elemento [*] con modalita' di acquisizione contentBased (Pattern:concat_openspcoop(\"BEGIN-ID_\",//test:idApplicativo/text(),\"_END-ID\")): nessun match trovato per l'espressione xpath contenuta in concat_openspcoop (//test:idApplicativo/text())";
				Reporter.log("Controllo fault string ["+msgErrore+"]");
				Assert.assertTrue(error.getFaultString().contains(msgErrore));
				Reporter.log("Controllo fault actor [OpenSPCoop]");
				Assert.assertTrue(CostantiPdD.OPENSPCOOP2.equals(error.getFaultActor()));
			}finally{
				dbComponentFruitore.close();
			}
			
			return client.getIdMessaggio();
			
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
	
	Repository repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaContentBasedConcatOpenSPCoop_erroreIdentificazione=new Repository();
	Repository repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaContentBasedIDUnivocoConcatOpenSPCoop_erroreIdentificazione=new Repository();
	@Test(groups={IntegrazioneCorrelazioneApplicativa.ID_GRUPPO,IntegrazioneCorrelazioneApplicativa.ID_GRUPPO+".CORRELAZIONE_APPLICATIVA_CONTENT_BASED_CONCAT_OPENSPCOOP_ERRORE_IDENTIFICAZIONE"},
			description="Test di tipo sincrono, Viene controllato se i body sono uguali e se gli attachment sono uguali")
	public void sincronoCorrelazioneApplicativaContentBasedConcatOpenSPCoop_erroreIdentificazione() throws TestSuiteException, IOException, Exception{
		
		Date dataInizioTest = DateManager.getDate();
		
		String idUnivoco = "XXXXXXXIDXXXXXXXX_"+System.currentTimeMillis();
		this.repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaContentBasedIDUnivocoConcatOpenSPCoop_erroreIdentificazione.add(idUnivoco);
		
		runClientCorrelazioneApplicativaContentBasedConcatOpenSPCoop_erroreIdentificazione(idUnivoco);
		
		Date dataFineTest = DateManager.getDate();
		ErroreAttesoOpenSPCoopLogCore err = new ErroreAttesoOpenSPCoopLogCore();
		err.setIntervalloInferiore(dataInizioTest);
		err.setIntervalloSuperiore(dataFineTest);
		err.setMsgErrore("La gestione della funzionalità di correlazione applicativa, per il messaggio di richiesta, ha generato un errore: identificativo di correlazione applicativa non identificato nell'elemento [*] con modalita' di acquisizione contentBased (Pattern:concat_openspcoop(\"BEGIN-ID_\",//test:idApplicativo/text(),\"_END-ID\")): nessun match trovato per l'espressione xpath contenuta in concat_openspcoop (//test:idApplicativo/text())");
		this.erroriAttesiOpenSPCoopCore.add(err);		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/***
	 * Test Correlazione Applicativa Input Based tramite input
	 */
		
	private String runClientCorrelazioneApplicativaInputBasedUrl(String portaDelegata,String idUnivoco,Repository repository) throws Exception{
		java.io.FileInputStream fin = null;
		DatabaseComponent dbComponentFruitore = null;
		try{
			fin = new java.io.FileInputStream(new File(Utilities.testSuiteProperties.getSoap11FileName()));

			Message msg=new Message(fin);
			msg.getSOAPPartAsBytes();

			TestSuiteProperties testsuiteProperties = TestSuiteProperties.getInstance();
			
			ClientHttpGenerico client=new ClientHttpGenerico(repository);
			client.setSoapAction("\"TEST\"");
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(portaDelegata+"?"+testsuiteProperties.getIDApplicativoUrlBased()+"="+idUnivoco);
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
			} catch (AxisFault error) {
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				throw error;
			}finally{
				dbComponentFruitore.close();
			}
			
			checkHttpRisposta(client.getPropertiesTrasportoRisposta(),CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_INTEGRAZIONE,client.getIdMessaggio());
			
			checkMessaggioRisposta(client.getResponseMessage(),CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_INTEGRAZIONE,client.getIdMessaggio());
			
			return client.getIdMessaggio();
			
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
	
	Repository repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaInputBasedUrl=new Repository();
	Repository repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaInputBasedUrlIDUnivoco=new Repository();
	@Test(groups={IntegrazioneCorrelazioneApplicativa.ID_GRUPPO,IntegrazioneCorrelazioneApplicativa.ID_GRUPPO+".CORRELAZIONE_APPLICATIVA_INPUT_BASED_URL"},description="Test di tipo sincrono, Viene controllato se i body sono uguali e se gli attachment sono uguali")
	public void sincronoCorrelazioneApplicativaInputBasedUrl() throws TestSuiteException, IOException, Exception{
		
		String idUnivoco = "XXXXXXXIDXXXXXXXX_"+System.currentTimeMillis();
		this.repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaInputBasedUrlIDUnivoco.add(idUnivoco);
		
		String id1 = runClientCorrelazioneApplicativaInputBasedUrl(CostantiTestSuite.PORTA_DELEGATA_PROFILO_SINCRONO_INTEGRAZIONE_CORRELAZIONE_APPLICATIVA_INPUT_BASED,
				idUnivoco,this.repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaInputBasedUrl);
		
		Reporter.log("Sleep 4 secondi...");
		try{
			Thread.sleep(4000);
		}catch(Exception e){}
		
		String id2 = runClientCorrelazioneApplicativaInputBasedUrl(CostantiTestSuite.PORTA_DELEGATA_PROFILO_SINCRONO_INTEGRAZIONE_CORRELAZIONE_APPLICATIVA_INPUT_BASED,
				idUnivoco,this.repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaInputBasedUrl);
		
		Reporter.log("ID 1st invocazione["+id1+"]");
		Reporter.log("ID 2nd invocazione["+id2+"]");
		Assert.assertTrue(id1.equals(id2));
	}
	@DataProvider (name="SincronoCorrelazioneApplicativaInputBasedUrl")
	public Object[][]testSincronoCorrelazioneApplicativaInputBasedUrl()throws Exception{
		String id=this.repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaInputBasedUrl.getNext();
		String idUnivoco=this.repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaInputBasedUrlIDUnivoco.getNext();
		return new Object[][]{
				{idUnivoco,DatabaseProperties.getDatabaseComponentDiagnosticaFruitore(),DatabaseProperties.getDatabaseComponentFruitore(),id,false},	
				{idUnivoco,null,DatabaseProperties.getDatabaseComponentErogatore(),id,false}	
		};
	}
	@Test(groups={IntegrazioneCorrelazioneApplicativa.ID_GRUPPO,IntegrazioneCorrelazioneApplicativa.ID_GRUPPO+".CORRELAZIONE_APPLICATIVA_INPUT_BASED_URL"},dataProvider="SincronoCorrelazioneApplicativaInputBasedUrl",dependsOnMethods={"sincronoCorrelazioneApplicativaInputBasedUrl"})
	public void testSincronoCorrelazioneApplicativaInputBasedUrl(String idUnivoco,DatabaseMsgDiagnosticiComponent dataMsgDiag,DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
		try{
			this.collaborazioneSPCoopBase.testSincrono(data, id, CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_INTEGRAZIONE, checkServizioApplicativo,
					null);
			Reporter.log("Numero messaggi arrivati al servizio applicativo: "+data.getVerificatoreTracciaRichiesta().isArrivedCount(id));
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isArrivedCount(id)==2);
			
			if(dataMsgDiag!=null){
				//fruitore
				
				String msgInizioCorrelazione = MSG_INIZIO_CORRELAZIONE.replace("@IDAPPLICATIVO@", idUnivoco);
				msgInizioCorrelazione = msgInizioCorrelazione.replace("@IDEGOV@", id);
				Reporter.log("Check inizio correlazione ["+msgInizioCorrelazione+"]");
				Assert.assertTrue(dataMsgDiag.isTracedMessaggio(msgInizioCorrelazione));
				
				String msgCorrelazioneAvvenuta = MSG_CORRELAZIONE.replace("@IDAPPLICATIVO@", idUnivoco);
				msgCorrelazioneAvvenuta = msgCorrelazioneAvvenuta.replace("@IDEGOV@", id);
				Reporter.log("Check correlazione ["+msgCorrelazioneAvvenuta+"]");
				Assert.assertTrue(dataMsgDiag.isTracedMessaggio(msgCorrelazioneAvvenuta));
				
				// check correlazione 
				Reporter.log("Check correlazione ["+idUnivoco+"]");
				Assert.assertTrue(dataMsgDiag.isTracedCorrelazioneApplicativaIntoMsgDiagnosticiCorrelazione(idUnivoco));
			}
			
			// Check correlazione in tracciamento
			Reporter.log("Check correlazione ["+idUnivoco+"] in tracciamento nella richiesta");
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedCorrelazioneApplicativaRichiesta(id, this.collaborazioneSPCoopBase.getMittente(), idUnivoco));
			Reporter.log("Check correlazione ["+idUnivoco+"] in tracciamento nella risposta");
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedCorrelazioneApplicativaRichiesta(id, this.collaborazioneSPCoopBase.getMittente(), idUnivoco));
			
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
			if(dataMsgDiag!=null)
				dataMsgDiag.close();
		}
	}
	
	
	Repository repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaInputBasedUrlSenzaRiusoIDEGov=new Repository();
	Repository repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaInputBasedUrlSenzaRiusoIDEGovIDUnivoco=new Repository();
	@Test(groups={IntegrazioneCorrelazioneApplicativa.ID_GRUPPO,IntegrazioneCorrelazioneApplicativa.ID_GRUPPO+".CORRELAZIONE_APPLICATIVA_INPUT_BASED_URL"},
			description="Test di tipo sincrono, Viene controllato se i body sono uguali e se gli attachment sono uguali",
			dependsOnMethods={"testSincronoCorrelazioneApplicativaInputBasedUrl"})
	public void sincronoCorrelazioneApplicativaInputBasedUrlSenzaRiusoIDEGov() throws TestSuiteException, IOException, Exception{
		
		String idUnivoco = "XXXXXXXIDXXXXXXXX_"+System.currentTimeMillis();
		this.repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaInputBasedUrlSenzaRiusoIDEGovIDUnivoco.add(idUnivoco);
		
		String id1 = runClientCorrelazioneApplicativaInputBasedUrl(CostantiTestSuite.PORTA_DELEGATA_PROFILO_SINCRONO_INTEGRAZIONE_CORRELAZIONE_APPLICATIVA_INPUT_BASED_SENZA_RIUSO_IDEGOV,
				idUnivoco,this.repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaInputBasedUrlSenzaRiusoIDEGov);
		
		Reporter.log("Sleep 4 secondi...");
		try{
			Thread.sleep(4000);
		}catch(Exception e){}
		
		String id2 = runClientCorrelazioneApplicativaInputBasedUrl(CostantiTestSuite.PORTA_DELEGATA_PROFILO_SINCRONO_INTEGRAZIONE_CORRELAZIONE_APPLICATIVA_INPUT_BASED_SENZA_RIUSO_IDEGOV,
				idUnivoco,this.repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaInputBasedUrlSenzaRiusoIDEGov);
		
		Reporter.log("ID 1st invocazione["+id1+"]");
		Reporter.log("ID 2nd invocazione["+id2+"]");
		Assert.assertTrue(id1.equals(id2)==false);
	}
	@DataProvider (name="SincronoCorrelazioneApplicativaInputBasedUrlSenzaRiusoIDEGov")
	public Object[][]testSincronoCorrelazioneApplicativaInputBasedUrlSenzaRiusoIDEGov()throws Exception{
		String id=this.repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaInputBasedUrlSenzaRiusoIDEGov.getNext();
		String idUnivoco=this.repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaInputBasedUrlSenzaRiusoIDEGovIDUnivoco.getNext();
		return new Object[][]{
				{idUnivoco,DatabaseProperties.getDatabaseComponentDiagnosticaFruitore(),DatabaseProperties.getDatabaseComponentFruitore(),id,false},	
				{idUnivoco,null,DatabaseProperties.getDatabaseComponentErogatore(),id,false}	
		};
	}
	@Test(groups={IntegrazioneCorrelazioneApplicativa.ID_GRUPPO,IntegrazioneCorrelazioneApplicativa.ID_GRUPPO+".CORRELAZIONE_APPLICATIVA_INPUT_BASED_URL"},
			dataProvider="SincronoCorrelazioneApplicativaInputBasedUrlSenzaRiusoIDEGov",dependsOnMethods={"sincronoCorrelazioneApplicativaInputBasedUrlSenzaRiusoIDEGov"})
	public void testSincronoCorrelazioneApplicativaInputBasedUrlSenzaRiusoIDEGov(String idUnivoco,DatabaseMsgDiagnosticiComponent dataMsgDiag,DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
		try{
			this.collaborazioneSPCoopBase.testSincrono(data, id, CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_INTEGRAZIONE, checkServizioApplicativo,
					null);
			Reporter.log("Numero messaggi arrivati al servizio applicativo: "+data.getVerificatoreTracciaRichiesta().isArrivedCount(id));
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isArrivedCount(id)==1);
			
			if(dataMsgDiag!=null){
				//fruitore
				
				String msgInizioCorrelazione = MSG_INIZIO_CORRELAZIONE.replace("@IDAPPLICATIVO@", idUnivoco);
				msgInizioCorrelazione = msgInizioCorrelazione.replace("@IDEGOV@", id);
				Reporter.log("Check inizio correlazione ["+msgInizioCorrelazione+"] non esistente");
				Assert.assertTrue(dataMsgDiag.isTracedMessaggio(msgInizioCorrelazione)==false);
				
				String msgCorrelazioneAvvenuta = MSG_CORRELAZIONE.replace("@IDAPPLICATIVO@", idUnivoco);
				msgCorrelazioneAvvenuta = msgCorrelazioneAvvenuta.replace("@IDEGOV@", id);
				Reporter.log("Check correlazione ["+msgCorrelazioneAvvenuta+"] non esistente");
				Assert.assertTrue(dataMsgDiag.isTracedMessaggio(msgCorrelazioneAvvenuta)==false);
				
				// check correlazione 
				Reporter.log("Check correlazione ["+idUnivoco+"]");
				Assert.assertTrue(dataMsgDiag.isTracedCorrelazioneApplicativaIntoMsgDiagnosticiCorrelazione(idUnivoco));
			}
			
			// Check correlazione in tracciamento
			Reporter.log("Check correlazione ["+idUnivoco+"] in tracciamento nella richiesta");
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedCorrelazioneApplicativaRichiesta(id, this.collaborazioneSPCoopBase.getMittente(), idUnivoco));
			Reporter.log("Check correlazione ["+idUnivoco+"] in tracciamento nella risposta");
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedCorrelazioneApplicativaRichiesta(id, this.collaborazioneSPCoopBase.getMittente(), idUnivoco));
			
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
			if(dataMsgDiag!=null)
				dataMsgDiag.close();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	/***
	 * Test Correlazione Applicativa Input Based tramite input
	 */
		
	private String runClientCorrelazioneApplicativaInputBasedUrl_erroreAccettato(String idUnivoco) throws Exception{
		java.io.FileInputStream fin = null;
		DatabaseComponent dbComponentFruitore = null;
		try{
			fin = new java.io.FileInputStream(new File(Utilities.testSuiteProperties.getSoap11FileName()));

			Message msg=new Message(fin);
			msg.getSOAPPartAsBytes();

			TestSuiteProperties testsuiteProperties = TestSuiteProperties.getInstance();
			
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaInputBasedUrl_erroreAccettato);
			client.setSoapAction("\"TEST\"");
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_PROFILO_SINCRONO_INTEGRAZIONE_CORRELAZIONE_APPLICATIVA_INPUT_BASED_ACCETTA_CON_IDENTIFICAZIONE_NON_RIUSCITA+"?"+testsuiteProperties.getIDApplicativoUrlBased()+"ERRATO="+idUnivoco);
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
			} catch (AxisFault error) {
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				throw error;
			}finally{
				dbComponentFruitore.close();
			}
			
			checkHttpRisposta(client.getPropertiesTrasportoRisposta(),CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_INTEGRAZIONE,client.getIdMessaggio());
			
			checkMessaggioRisposta(client.getResponseMessage(),CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_INTEGRAZIONE,client.getIdMessaggio());
			
			return client.getIdMessaggio();
			
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
	
	Repository repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaInputBasedUrl_erroreAccettato=new Repository();
	Repository repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaInputBasedUrlIDUnivoco_erroreAccettato=new Repository();
	@Test(groups={IntegrazioneCorrelazioneApplicativa.ID_GRUPPO,IntegrazioneCorrelazioneApplicativa.ID_GRUPPO+".CORRELAZIONE_APPLICATIVA_INPUT_BASED_URL_IDENTIFICAZIONE_NON_RIUSCITA"},description="Test di tipo sincrono, Viene controllato se i body sono uguali e se gli attachment sono uguali")
	public void sincronoCorrelazioneApplicativaInputBasedUrl_erroreAccettato() throws TestSuiteException, IOException, Exception{
		
		String idUnivoco = "XXXXXXXIDXXXXXXXX_"+System.currentTimeMillis();
		this.repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaInputBasedUrlIDUnivoco_erroreAccettato.add(idUnivoco);
		
		String id1 = runClientCorrelazioneApplicativaInputBasedUrl_erroreAccettato(idUnivoco);
		
		Reporter.log("Sleep 4 secondi...");
		try{
			Thread.sleep(4000);
		}catch(Exception e){}
		
		String id2 = runClientCorrelazioneApplicativaInputBasedUrl_erroreAccettato(idUnivoco);
		
		Reporter.log("ID 1st invocazione["+id1+"]");
		Reporter.log("ID 2nd invocazione["+id2+"]");
		Assert.assertTrue(id1.equals(id2)==false);
	}
	@DataProvider (name="SincronoCorrelazioneApplicativaInputBasedUrl_erroreAccettato")
	public Object[][]testSincronoCorrelazioneApplicativaInputBasedUrl_erroreAccettato()throws Exception{
		String id=this.repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaInputBasedUrl_erroreAccettato.getNext();
		String idSecondaInvocazione = this.repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaInputBasedUrl_erroreAccettato.getNext();
		String idUnivoco=this.repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaInputBasedUrlIDUnivoco_erroreAccettato.getNext();
		return new Object[][]{
				{idUnivoco,idSecondaInvocazione,DatabaseProperties.getDatabaseComponentDiagnosticaFruitore(),DatabaseProperties.getDatabaseComponentFruitore(),id,false},	
				{idUnivoco,idSecondaInvocazione,null,DatabaseProperties.getDatabaseComponentErogatore(),id,false}	
		};
	}
	@Test(groups={IntegrazioneCorrelazioneApplicativa.ID_GRUPPO,IntegrazioneCorrelazioneApplicativa.ID_GRUPPO+".CORRELAZIONE_APPLICATIVA_INPUT_BASED_URL_IDENTIFICAZIONE_NON_RIUSCITA"},
			dataProvider="SincronoCorrelazioneApplicativaInputBasedUrl_erroreAccettato",
			dependsOnMethods={"sincronoCorrelazioneApplicativaInputBasedUrl_erroreAccettato"})
	public void testSincronoCorrelazioneApplicativaInputBasedUrl_erroreAccettato(String idUnivoco,String idSecondaInvocazione,DatabaseMsgDiagnosticiComponent dataMsgDiag,DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
		try{
			this.collaborazioneSPCoopBase.testSincrono(data, id, CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_INTEGRAZIONE, checkServizioApplicativo,
					null);
			Reporter.log("Numero messaggi arrivati al servizio applicativo: "+data.getVerificatoreTracciaRichiesta().isArrivedCount(id));
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isArrivedCount(id)==1);
			
			this.collaborazioneSPCoopBase.testSincrono(data, idSecondaInvocazione, CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_INTEGRAZIONE, checkServizioApplicativo,
					null);
			Reporter.log("Numero messaggi arrivati al servizio applicativo: "+data.getVerificatoreTracciaRichiesta().isArrivedCount(id));
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isArrivedCount(idSecondaInvocazione)==1);
			
			if(dataMsgDiag!=null){
				//fruitore
				
				String msgInizioCorrelazione = MSG_INIZIO_CORRELAZIONE.replace("@IDEGOV@", id);
				String [] msgRicerca = msgInizioCorrelazione.split("@IDAPPLICATIVO@");
				Reporter.log("Check inizio correlazione ["+msgInizioCorrelazione+"]");
				Assert.assertTrue(dataMsgDiag.isTracedMessaggio(msgRicerca)==false);
				
				String msgCorrelazioneAvvenuta = MSG_CORRELAZIONE.replace("@IDEGOV@", id);
				msgRicerca = msgCorrelazioneAvvenuta.split("@IDAPPLICATIVO@");
				Reporter.log("Check inizio correlazione ["+msgCorrelazioneAvvenuta+"]");
				Assert.assertTrue(dataMsgDiag.isTracedMessaggio(msgRicerca)==false);
				
				msgInizioCorrelazione = MSG_INIZIO_CORRELAZIONE.replace("@IDEGOV@", idSecondaInvocazione);
				msgRicerca = msgInizioCorrelazione.split("@IDAPPLICATIVO@");
				Reporter.log("Check inizio correlazione ["+msgInizioCorrelazione+"]");
				Assert.assertTrue(dataMsgDiag.isTracedMessaggio(msgRicerca)==false);
				
				msgCorrelazioneAvvenuta = MSG_CORRELAZIONE.replace("@IDEGOV@", idSecondaInvocazione);
				msgRicerca = msgCorrelazioneAvvenuta.split("@IDAPPLICATIVO@");
				Reporter.log("Check inizio correlazione ["+msgCorrelazioneAvvenuta+"]");
				Assert.assertTrue(dataMsgDiag.isTracedMessaggio(msgRicerca)==false);
				
				// check correlazione 
				Reporter.log("Check correlazione ["+idUnivoco+"] non esistente");
				Assert.assertTrue(dataMsgDiag.isTracedCorrelazioneApplicativaIntoMsgDiagnosticiCorrelazione(idUnivoco)==false);
			}
			
			// Check correlazione in tracciamento
			Reporter.log("Check correlazione ["+idUnivoco+"] in tracciamento nella richiesta non esistente");
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedCorrelazioneApplicativaRichiesta(id, this.collaborazioneSPCoopBase.getMittente(), idUnivoco)==false);
			Reporter.log("Check correlazione ["+idUnivoco+"] in tracciamento nella risposta non esistente");
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedCorrelazioneApplicativaRichiesta(id, this.collaborazioneSPCoopBase.getMittente(), idUnivoco)==false);
			
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
			if(dataMsgDiag!=null)
				dataMsgDiag.close();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/***
	 * Test Correlazione Applicativa Input Based tramite trasporto
	 */
		
	private String runClientCorrelazioneApplicativaInputBasedTrasporto(String portaDelegata,String idUnivoco,Repository repository) throws Exception{
		java.io.FileInputStream fin = null;
		DatabaseComponent dbComponentFruitore = null;
		try{
			fin = new java.io.FileInputStream(new File(Utilities.testSuiteProperties.getSoap11FileName()));

			Message msg=new Message(fin);
			msg.getSOAPPartAsBytes();

			TestSuiteProperties testsuiteProperties = TestSuiteProperties.getInstance();
			
			ClientHttpGenerico client=new ClientHttpGenerico(repository);
			client.setSoapAction("\"TEST\"");
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(portaDelegata);
			client.connectToSoapEngine();
			client.setProperty(testsuiteProperties.getIDApplicativoTrasporto(), idUnivoco);
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
			} catch (AxisFault error) {
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				throw error;
			}finally{
				dbComponentFruitore.close();
			}
			
			checkHttpRisposta(client.getPropertiesTrasportoRisposta(),CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_INTEGRAZIONE,client.getIdMessaggio());
			
			checkMessaggioRisposta(client.getResponseMessage(),CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_INTEGRAZIONE,client.getIdMessaggio());
			
			return client.getIdMessaggio();
			
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
	
	Repository repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaInputBasedTrasporto=new Repository();
	Repository repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaInputBasedTrasportoIDUnivoco=new Repository();
	@Test(groups={IntegrazioneCorrelazioneApplicativa.ID_GRUPPO,IntegrazioneCorrelazioneApplicativa.ID_GRUPPO+".CORRELAZIONE_APPLICATIVA_INPUT_BASED_TRASPORTO"},description="Test di tipo sincrono, Viene controllato se i body sono uguali e se gli attachment sono uguali")
	public void sincronoCorrelazioneApplicativaInputBasedTrasporto() throws TestSuiteException, IOException, Exception{
		
		String idUnivoco = "XXXXXXXIDXXXXXXXX_"+System.currentTimeMillis();
		this.repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaInputBasedTrasportoIDUnivoco.add(idUnivoco);
		
		String id1 = runClientCorrelazioneApplicativaInputBasedTrasporto(CostantiTestSuite.PORTA_DELEGATA_PROFILO_SINCRONO_INTEGRAZIONE_CORRELAZIONE_APPLICATIVA_INPUT_BASED,
				idUnivoco,this.repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaInputBasedTrasporto);
		
		Reporter.log("Sleep 4 secondi...");
		try{
			Thread.sleep(4000);
		}catch(Exception e){}
		
		String id2 = runClientCorrelazioneApplicativaInputBasedTrasporto(CostantiTestSuite.PORTA_DELEGATA_PROFILO_SINCRONO_INTEGRAZIONE_CORRELAZIONE_APPLICATIVA_INPUT_BASED,
				idUnivoco,this.repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaInputBasedTrasporto);
		
		Reporter.log("ID 1st invocazione["+id1+"]");
		Reporter.log("ID 2nd invocazione["+id2+"]");
		Assert.assertTrue(id1.equals(id2));
	}
	@DataProvider (name="SincronoCorrelazioneApplicativaInputBasedTrasporto")
	public Object[][]testSincronoCorrelazioneApplicativaInputBasedTrasporto()throws Exception{
		String id=this.repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaInputBasedTrasporto.getNext();
		String idUnivoco=this.repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaInputBasedTrasportoIDUnivoco.getNext();
		return new Object[][]{
				{idUnivoco,DatabaseProperties.getDatabaseComponentDiagnosticaFruitore(),DatabaseProperties.getDatabaseComponentFruitore(),id,false},	
				{idUnivoco,null,DatabaseProperties.getDatabaseComponentErogatore(),id,false}	
		};
	}
	@Test(groups={IntegrazioneCorrelazioneApplicativa.ID_GRUPPO,IntegrazioneCorrelazioneApplicativa.ID_GRUPPO+".CORRELAZIONE_APPLICATIVA_INPUT_BASED_TRASPORTO"},dataProvider="SincronoCorrelazioneApplicativaInputBasedTrasporto",dependsOnMethods={"sincronoCorrelazioneApplicativaInputBasedTrasporto"})
	public void testSincronoCorrelazioneApplicativaInputBasedTrasporto(String idUnivoco,DatabaseMsgDiagnosticiComponent dataMsgDiag,DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
		try{
			this.collaborazioneSPCoopBase.testSincrono(data, id, CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_INTEGRAZIONE, checkServizioApplicativo,
					null);
			Reporter.log("Numero messaggi arrivati al servizio applicativo: "+data.getVerificatoreTracciaRichiesta().isArrivedCount(id));
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isArrivedCount(id)==2);
			
			if(dataMsgDiag!=null){
				//fruitore
				
				String msgInizioCorrelazione = MSG_INIZIO_CORRELAZIONE.replace("@IDAPPLICATIVO@", idUnivoco);
				msgInizioCorrelazione = msgInizioCorrelazione.replace("@IDEGOV@", id);
				Reporter.log("Check inizio correlazione ["+msgInizioCorrelazione+"]");
				Assert.assertTrue(dataMsgDiag.isTracedMessaggio(msgInizioCorrelazione));
				
				String msgCorrelazioneAvvenuta = MSG_CORRELAZIONE.replace("@IDAPPLICATIVO@", idUnivoco);
				msgCorrelazioneAvvenuta = msgCorrelazioneAvvenuta.replace("@IDEGOV@", id);
				Reporter.log("Check correlazione ["+msgCorrelazioneAvvenuta+"]");
				Assert.assertTrue(dataMsgDiag.isTracedMessaggio(msgCorrelazioneAvvenuta));
				
				// check correlazione 
				Reporter.log("Check correlazione ["+idUnivoco+"]");
				Assert.assertTrue(dataMsgDiag.isTracedCorrelazioneApplicativaIntoMsgDiagnosticiCorrelazione(idUnivoco));
			}
			
			// Check correlazione in tracciamento
			Reporter.log("Check correlazione ["+idUnivoco+"] in tracciamento nella richiesta");
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedCorrelazioneApplicativaRichiesta(id, this.collaborazioneSPCoopBase.getMittente(), idUnivoco));
			Reporter.log("Check correlazione ["+idUnivoco+"] in tracciamento nella risposta");
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedCorrelazioneApplicativaRichiesta(id, this.collaborazioneSPCoopBase.getMittente(), idUnivoco));
			
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
			if(dataMsgDiag!=null)
				dataMsgDiag.close();
		}
	}
	
	Repository repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaInputBasedTrasportoSenzaRiusoIDEGov=new Repository();
	Repository repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaInputBasedTrasportoSenzaRiusoIDEGovIDUnivoco=new Repository();
	@Test(groups={IntegrazioneCorrelazioneApplicativa.ID_GRUPPO,IntegrazioneCorrelazioneApplicativa.ID_GRUPPO+".CORRELAZIONE_APPLICATIVA_INPUT_BASED_TRASPORTO"},
			description="Test di tipo sincrono, Viene controllato se i body sono uguali e se gli attachment sono uguali",
			dependsOnMethods={"testSincronoCorrelazioneApplicativaInputBasedTrasporto"})
	public void sincronoCorrelazioneApplicativaInputBasedTrasportoSenzaRiusoIDEGov() throws TestSuiteException, IOException, Exception{
		
		String idUnivoco = "XXXXXXXIDXXXXXXXX_"+System.currentTimeMillis();
		this.repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaInputBasedTrasportoSenzaRiusoIDEGovIDUnivoco.add(idUnivoco);
		
		String id1 = runClientCorrelazioneApplicativaInputBasedTrasporto(CostantiTestSuite.PORTA_DELEGATA_PROFILO_SINCRONO_INTEGRAZIONE_CORRELAZIONE_APPLICATIVA_INPUT_BASED_SENZA_RIUSO_IDEGOV,
				idUnivoco,this.repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaInputBasedTrasportoSenzaRiusoIDEGov);
		
		Reporter.log("Sleep 4 secondi...");
		try{
			Thread.sleep(4000);
		}catch(Exception e){}
		
		String id2 = runClientCorrelazioneApplicativaInputBasedTrasporto(CostantiTestSuite.PORTA_DELEGATA_PROFILO_SINCRONO_INTEGRAZIONE_CORRELAZIONE_APPLICATIVA_INPUT_BASED_SENZA_RIUSO_IDEGOV,
				idUnivoco,this.repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaInputBasedTrasportoSenzaRiusoIDEGov);
		
		Reporter.log("ID 1st invocazione["+id1+"]");
		Reporter.log("ID 2nd invocazione["+id2+"]");
		Assert.assertTrue(id1.equals(id2)==false);
	}
	@DataProvider (name="SincronoCorrelazioneApplicativaInputBasedTrasportoSenzaRiusoIDEGov")
	public Object[][]testSincronoCorrelazioneApplicativaInputBasedTrasportoSenzaRiusoIDEGov()throws Exception{
		String id=this.repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaInputBasedTrasportoSenzaRiusoIDEGov.getNext();
		String idUnivoco=this.repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaInputBasedTrasportoSenzaRiusoIDEGovIDUnivoco.getNext();
		return new Object[][]{
				{idUnivoco,DatabaseProperties.getDatabaseComponentDiagnosticaFruitore(),DatabaseProperties.getDatabaseComponentFruitore(),id,false},	
				{idUnivoco,null,DatabaseProperties.getDatabaseComponentErogatore(),id,false}	
		};
	}
	@Test(groups={IntegrazioneCorrelazioneApplicativa.ID_GRUPPO,IntegrazioneCorrelazioneApplicativa.ID_GRUPPO+".CORRELAZIONE_APPLICATIVA_INPUT_BASED_TRASPORTO"},
			dataProvider="SincronoCorrelazioneApplicativaInputBasedTrasportoSenzaRiusoIDEGov",dependsOnMethods={"sincronoCorrelazioneApplicativaInputBasedTrasportoSenzaRiusoIDEGov"})
	public void testSincronoCorrelazioneApplicativaInputBasedTrasportoSenzaRiusoIDEGov(String idUnivoco,DatabaseMsgDiagnosticiComponent dataMsgDiag,DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
		try{
			this.collaborazioneSPCoopBase.testSincrono(data, id, CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_INTEGRAZIONE, checkServizioApplicativo,
					null);
			Reporter.log("Numero messaggi arrivati al servizio applicativo: "+data.getVerificatoreTracciaRichiesta().isArrivedCount(id));
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isArrivedCount(id)==1);
			
			if(dataMsgDiag!=null){
				//fruitore
				
				String msgInizioCorrelazione = MSG_INIZIO_CORRELAZIONE.replace("@IDAPPLICATIVO@", idUnivoco);
				msgInizioCorrelazione = msgInizioCorrelazione.replace("@IDEGOV@", id);
				Reporter.log("Check inizio correlazione ["+msgInizioCorrelazione+"] non esistente");
				Assert.assertTrue(dataMsgDiag.isTracedMessaggio(msgInizioCorrelazione) == false);
				
				String msgCorrelazioneAvvenuta = MSG_CORRELAZIONE.replace("@IDAPPLICATIVO@", idUnivoco);
				msgCorrelazioneAvvenuta = msgCorrelazioneAvvenuta.replace("@IDEGOV@", id);
				Reporter.log("Check correlazione ["+msgCorrelazioneAvvenuta+"] non esistente");
				Assert.assertTrue(dataMsgDiag.isTracedMessaggio(msgCorrelazioneAvvenuta)==false);
				
				// check correlazione 
				Reporter.log("Check correlazione ["+idUnivoco+"]");
				Assert.assertTrue(dataMsgDiag.isTracedCorrelazioneApplicativaIntoMsgDiagnosticiCorrelazione(idUnivoco));
			}
			
			// Check correlazione in tracciamento
			Reporter.log("Check correlazione ["+idUnivoco+"] in tracciamento nella richiesta");
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedCorrelazioneApplicativaRichiesta(id, this.collaborazioneSPCoopBase.getMittente(), idUnivoco));
			Reporter.log("Check correlazione ["+idUnivoco+"] in tracciamento nella risposta");
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedCorrelazioneApplicativaRichiesta(id, this.collaborazioneSPCoopBase.getMittente(), idUnivoco));
			
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
			if(dataMsgDiag!=null)
				dataMsgDiag.close();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/***
	 * Test Correlazione Applicativa Input Based tramite soap header
	 */
		
	private String runClientCorrelazioneApplicativaInputBasedSoapHeader(String portaDelegata,String idUnivoco,Repository repository) throws Exception{
		java.io.FileInputStream fin = null;
		DatabaseComponent dbComponentFruitore = null;
		try{
			fin = new java.io.FileInputStream(new File(Utilities.testSuiteProperties.getSoap11FileName()));

			TestSuiteProperties testsuiteProperties = TestSuiteProperties.getInstance();
			
			Message msg=new Message(fin);
			msg.getSOAPPartAsBytes();

			Name name = new PrefixedQName("http://www.openspcoop2.org/core/integrazione","integrazione","openspcoop");
			org.apache.axis.message.SOAPHeaderElement header = 
				new org.apache.axis.message.SOAPHeaderElement(name);
			header.setActor("http://www.openspcoop2.org/core/integrazione");
			header.setMustUnderstand(false);
			header.addNamespaceDeclaration("SOAP_ENV","http://schemas.xmlsoap.org/soap/envelope/");

			header.setAttribute(testsuiteProperties.getIDApplicativoSoap(), idUnivoco);
			
			if(msg.getSOAPHeader()==null)
				msg.getSOAPEnvelope().addHeader();
			msg.getSOAPHeader().addChildElement(header);
			
			ClientHttpGenerico client=new ClientHttpGenerico(repository);
			client.setSoapAction("\"TEST\"");
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
			} catch (AxisFault error) {
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				throw error;
			}finally{
				dbComponentFruitore.close();
			}
			
			checkHttpRisposta(client.getPropertiesTrasportoRisposta(),CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_INTEGRAZIONE,client.getIdMessaggio());
			
			checkMessaggioRisposta(client.getResponseMessage(),CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_INTEGRAZIONE,client.getIdMessaggio());
			
			return client.getIdMessaggio();
			
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
	
	Repository repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaInputBasedSoapHeader=new Repository();
	Repository repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaInputBasedSoapHeaderIDUnivoco=new Repository();
	@Test(groups={IntegrazioneCorrelazioneApplicativa.ID_GRUPPO,IntegrazioneCorrelazioneApplicativa.ID_GRUPPO+".CORRELAZIONE_APPLICATIVA_INPUT_BASED_SOAP_HEADER"},description="Test di tipo sincrono, Viene controllato se i body sono uguali e se gli attachment sono uguali")
	public void sincronoCorrelazioneApplicativaInputBasedSoapHeader() throws TestSuiteException, IOException, Exception{
		
		String idUnivoco = "XXXXXXXIDXXXXXXXX_"+System.currentTimeMillis();
		this.repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaInputBasedSoapHeaderIDUnivoco.add(idUnivoco);
		
		String id1 = runClientCorrelazioneApplicativaInputBasedSoapHeader(CostantiTestSuite.PORTA_DELEGATA_PROFILO_SINCRONO_INTEGRAZIONE_CORRELAZIONE_APPLICATIVA_INPUT_BASED,
				idUnivoco,this.repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaInputBasedSoapHeader);
		
		Reporter.log("Sleep 4 secondi...");
		try{
			Thread.sleep(4000);
		}catch(Exception e){}
		
		String id2 = runClientCorrelazioneApplicativaInputBasedSoapHeader(CostantiTestSuite.PORTA_DELEGATA_PROFILO_SINCRONO_INTEGRAZIONE_CORRELAZIONE_APPLICATIVA_INPUT_BASED,
				idUnivoco,this.repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaInputBasedSoapHeader);
		
		Reporter.log("ID 1st invocazione["+id1+"]");
		Reporter.log("ID 2nd invocazione["+id2+"]");
		Assert.assertTrue(id1.equals(id2));
	}
	@DataProvider (name="SincronoCorrelazioneApplicativaInputBasedSoapHeader")
	public Object[][]testSincronoCorrelazioneApplicativaInputBasedSoapHeader()throws Exception{
		String id=this.repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaInputBasedSoapHeader.getNext();
		String idUnivoco=this.repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaInputBasedSoapHeaderIDUnivoco.getNext();
		return new Object[][]{
				{idUnivoco,DatabaseProperties.getDatabaseComponentDiagnosticaFruitore(),DatabaseProperties.getDatabaseComponentFruitore(),id,false},	
				{idUnivoco,null,DatabaseProperties.getDatabaseComponentErogatore(),id,false}	
		};
	}
	@Test(groups={IntegrazioneCorrelazioneApplicativa.ID_GRUPPO,IntegrazioneCorrelazioneApplicativa.ID_GRUPPO+".CORRELAZIONE_APPLICATIVA_INPUT_BASED_SOAP_HEADER"},dataProvider="SincronoCorrelazioneApplicativaInputBasedSoapHeader",dependsOnMethods={"sincronoCorrelazioneApplicativaInputBasedSoapHeader"})
	public void testSincronoCorrelazioneApplicativaInputBasedSoapHeader(String idUnivoco,DatabaseMsgDiagnosticiComponent dataMsgDiag,DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
		try{
			this.collaborazioneSPCoopBase.testSincrono(data, id, CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_INTEGRAZIONE, checkServizioApplicativo,
					null);
			Reporter.log("Numero messaggi arrivati al servizio applicativo: "+data.getVerificatoreTracciaRichiesta().isArrivedCount(id));
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isArrivedCount(id)==2);
			
			if(dataMsgDiag!=null){
				//fruitore
				
				String msgInizioCorrelazione = MSG_INIZIO_CORRELAZIONE.replace("@IDAPPLICATIVO@", idUnivoco);
				msgInizioCorrelazione = msgInizioCorrelazione.replace("@IDEGOV@", id);
				Reporter.log("Check inizio correlazione ["+msgInizioCorrelazione+"]");
				Assert.assertTrue(dataMsgDiag.isTracedMessaggio(msgInizioCorrelazione));
				
				String msgCorrelazioneAvvenuta = MSG_CORRELAZIONE.replace("@IDAPPLICATIVO@", idUnivoco);
				msgCorrelazioneAvvenuta = msgCorrelazioneAvvenuta.replace("@IDEGOV@", id);
				Reporter.log("Check correlazione ["+msgCorrelazioneAvvenuta+"]");
				Assert.assertTrue(dataMsgDiag.isTracedMessaggio(msgCorrelazioneAvvenuta));
				
				// check correlazione 
				Reporter.log("Check correlazione ["+idUnivoco+"]");
				Assert.assertTrue(dataMsgDiag.isTracedCorrelazioneApplicativaIntoMsgDiagnosticiCorrelazione(idUnivoco));
			}
			
			// Check correlazione in tracciamento
			Reporter.log("Check correlazione ["+idUnivoco+"] in tracciamento nella richiesta");
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedCorrelazioneApplicativaRichiesta(id, this.collaborazioneSPCoopBase.getMittente(), idUnivoco));
			Reporter.log("Check correlazione ["+idUnivoco+"] in tracciamento nella risposta");
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedCorrelazioneApplicativaRichiesta(id, this.collaborazioneSPCoopBase.getMittente(), idUnivoco));
			
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
			if(dataMsgDiag!=null)
				dataMsgDiag.close();
		}
	}
	
	Repository repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaInputBasedSoapHeaderSenzaRiusoIDEGov=new Repository();
	Repository repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaInputBasedSoapHeaderSenzaRiusoIDEGovIDUnivoco=new Repository();
	@Test(groups={IntegrazioneCorrelazioneApplicativa.ID_GRUPPO,IntegrazioneCorrelazioneApplicativa.ID_GRUPPO+".CORRELAZIONE_APPLICATIVA_INPUT_BASED_SOAP_HEADER"},
			description="Test di tipo sincrono, Viene controllato se i body sono uguali e se gli attachment sono uguali",
			dependsOnMethods={"testSincronoCorrelazioneApplicativaInputBasedSoapHeader"})
	public void sincronoCorrelazioneApplicativaInputBasedSoapHeaderSenzaRiusoIDEGov() throws TestSuiteException, IOException, Exception{
		
		String idUnivoco = "XXXXXXXIDXXXXXXXX_"+System.currentTimeMillis();
		this.repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaInputBasedSoapHeaderSenzaRiusoIDEGovIDUnivoco.add(idUnivoco);
		
		String id1 = runClientCorrelazioneApplicativaInputBasedSoapHeader(CostantiTestSuite.PORTA_DELEGATA_PROFILO_SINCRONO_INTEGRAZIONE_CORRELAZIONE_APPLICATIVA_INPUT_BASED_SENZA_RIUSO_IDEGOV,
				idUnivoco,this.repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaInputBasedSoapHeaderSenzaRiusoIDEGov);
		
		Reporter.log("Sleep 4 secondi...");
		try{
			Thread.sleep(4000);
		}catch(Exception e){}
		
		String id2 = runClientCorrelazioneApplicativaInputBasedSoapHeader(CostantiTestSuite.PORTA_DELEGATA_PROFILO_SINCRONO_INTEGRAZIONE_CORRELAZIONE_APPLICATIVA_INPUT_BASED_SENZA_RIUSO_IDEGOV,
				idUnivoco,this.repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaInputBasedSoapHeaderSenzaRiusoIDEGov);
		
		Reporter.log("ID 1st invocazione["+id1+"]");
		Reporter.log("ID 2nd invocazione["+id2+"]");
		Assert.assertTrue(id1.equals(id2)==false);
	}
	@DataProvider (name="SincronoCorrelazioneApplicativaInputBasedSoapHeaderSenzaRiusoIDEGov")
	public Object[][]testSincronoCorrelazioneApplicativaInputBasedSoapHeaderSenzaRiusoIDEGov()throws Exception{
		String id=this.repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaInputBasedSoapHeaderSenzaRiusoIDEGov.getNext();
		String idUnivoco=this.repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaInputBasedSoapHeaderSenzaRiusoIDEGovIDUnivoco.getNext();
		return new Object[][]{
				{idUnivoco,DatabaseProperties.getDatabaseComponentDiagnosticaFruitore(),DatabaseProperties.getDatabaseComponentFruitore(),id,false},	
				{idUnivoco,null,DatabaseProperties.getDatabaseComponentErogatore(),id,false}	
		};
	}
	@Test(groups={IntegrazioneCorrelazioneApplicativa.ID_GRUPPO,IntegrazioneCorrelazioneApplicativa.ID_GRUPPO+".CORRELAZIONE_APPLICATIVA_INPUT_BASED_SOAP_HEADER"},
			dataProvider="SincronoCorrelazioneApplicativaInputBasedSoapHeaderSenzaRiusoIDEGov",dependsOnMethods={"sincronoCorrelazioneApplicativaInputBasedSoapHeaderSenzaRiusoIDEGov"})
	public void testSincronoCorrelazioneApplicativaInputBasedSoapHeaderSenzaRiusoIDEGov(String idUnivoco,DatabaseMsgDiagnosticiComponent dataMsgDiag,DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
		try{
			this.collaborazioneSPCoopBase.testSincrono(data, id, CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_INTEGRAZIONE, checkServizioApplicativo,
					null);
			Reporter.log("Numero messaggi arrivati al servizio applicativo: "+data.getVerificatoreTracciaRichiesta().isArrivedCount(id));
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isArrivedCount(id)==1);
			
			if(dataMsgDiag!=null){
				//fruitore
				
				String msgInizioCorrelazione = MSG_INIZIO_CORRELAZIONE.replace("@IDAPPLICATIVO@", idUnivoco);
				msgInizioCorrelazione = msgInizioCorrelazione.replace("@IDEGOV@", id);
				Reporter.log("Check inizio correlazione ["+msgInizioCorrelazione+"] non esistente");
				Assert.assertTrue(dataMsgDiag.isTracedMessaggio(msgInizioCorrelazione)==false);
				
				String msgCorrelazioneAvvenuta = MSG_CORRELAZIONE.replace("@IDAPPLICATIVO@", idUnivoco);
				msgCorrelazioneAvvenuta = msgCorrelazioneAvvenuta.replace("@IDEGOV@", id);
				Reporter.log("Check correlazione ["+msgCorrelazioneAvvenuta+"] non esistente");
				Assert.assertTrue(dataMsgDiag.isTracedMessaggio(msgCorrelazioneAvvenuta)==false);
				
				// check correlazione 
				Reporter.log("Check correlazione ["+idUnivoco+"]");
				Assert.assertTrue(dataMsgDiag.isTracedCorrelazioneApplicativaIntoMsgDiagnosticiCorrelazione(idUnivoco));
			}
			
			// Check correlazione in tracciamento
			Reporter.log("Check correlazione ["+idUnivoco+"] in tracciamento nella richiesta");
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedCorrelazioneApplicativaRichiesta(id, this.collaborazioneSPCoopBase.getMittente(), idUnivoco));
			Reporter.log("Check correlazione ["+idUnivoco+"] in tracciamento nella risposta");
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedCorrelazioneApplicativaRichiesta(id, this.collaborazioneSPCoopBase.getMittente(), idUnivoco));
			
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
			if(dataMsgDiag!=null)
				dataMsgDiag.close();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/***
	 * Test Correlazione Applicativa Input Based tramite soap header
	 */
		
	private String runClientCorrelazioneApplicativaInputBasedWSAddressing(String portaDelegata,String idUnivoco,Repository repository) throws Exception{
		java.io.FileInputStream fin = null;
		DatabaseComponent dbComponentFruitore = null;
		try{
			fin = new java.io.FileInputStream(new File(Utilities.testSuiteProperties.getSoap11FileName()));

			Message msg=new Message(fin);
			msg.getSOAPPartAsBytes();

			
			
			Name name = new PrefixedQName("http://www.w3.org/2005/08/addressing","MessageID","wsa");
			org.apache.axis.message.SOAPHeaderElement header = 
				new org.apache.axis.message.SOAPHeaderElement(name);
			header.setActor("http://www.openspcoop2.org/core/integrazione/wsa");
			header.setMustUnderstand(false);
			header.addNamespaceDeclaration("SOAP_ENV","http://schemas.xmlsoap.org/soap/envelope/");
			header.setValue("uuid:"+idUnivoco);
			
			if(msg.getSOAPHeader()==null)
				msg.getSOAPEnvelope().addHeader();
			
			byte [] headerByte = Axis14SoapUtils.msgElementoToByte(header);
			ByteArrayInputStream input = new ByteArrayInputStream(headerByte);
			Document document = org.apache.axis.utils.XMLUtils.newDocument(input);
			SOAPHeaderElement elementSenzaXSITypes = new SOAPHeaderElement(document.getDocumentElement());
			msg.getSOAPHeader().addChildElement(elementSenzaXSITypes);
			
			
			ClientHttpGenerico client=new ClientHttpGenerico(repository);
			client.setSoapAction("\"TEST\"");
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
			} catch (AxisFault error) {
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				throw error;
			}finally{
				dbComponentFruitore.close();
			}
			
			checkHttpRisposta(client.getPropertiesTrasportoRisposta(),CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_INTEGRAZIONE,client.getIdMessaggio());
			
			// Controllo header proprietario OpenSPCoop
			checkMessaggioRisposta(client.getResponseMessage(),CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_INTEGRAZIONE,client.getIdMessaggio());
			
			// Controllo header WSAddressing
			checkMessaggioRispostaWSAddressing(client.getResponseMessage(),
					CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO, CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_INTEGRAZIONE,client.getIdMessaggio());
			
			return client.getIdMessaggio();
			
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
	
	Repository repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaInputBasedWSAddressing=new Repository();
	Repository repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaInputBasedWSAddressingIDUnivoco=new Repository();
	@Test(groups={IntegrazioneCorrelazioneApplicativa.ID_GRUPPO,IntegrazioneCorrelazioneApplicativa.ID_GRUPPO+".CORRELAZIONE_APPLICATIVA_INPUT_BASED_WSADDRESSING"},description="Test di tipo sincrono, Viene controllato se i body sono uguali e se gli attachment sono uguali")
	public void sincronoCorrelazioneApplicativaInputBasedWSAddressing() throws TestSuiteException, IOException, Exception{
		
		String idUnivoco = "XXXXXXXIDXXXXXXXX_"+System.currentTimeMillis();
		this.repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaInputBasedWSAddressingIDUnivoco.add(idUnivoco);
		
		String id1 = runClientCorrelazioneApplicativaInputBasedWSAddressing(CostantiTestSuite.PORTA_DELEGATA_PROFILO_SINCRONO_INTEGRAZIONE_CORRELAZIONE_APPLICATIVA_INPUT_BASED,
				idUnivoco,this.repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaInputBasedWSAddressing);
		
		Reporter.log("Sleep 4 secondi...");
		try{
			Thread.sleep(4000);
		}catch(Exception e){}
		
		String id2 = runClientCorrelazioneApplicativaInputBasedWSAddressing(CostantiTestSuite.PORTA_DELEGATA_PROFILO_SINCRONO_INTEGRAZIONE_CORRELAZIONE_APPLICATIVA_INPUT_BASED,
				idUnivoco,this.repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaInputBasedWSAddressing);
		
		Reporter.log("ID 1st invocazione["+id1+"]");
		Reporter.log("ID 2nd invocazione["+id2+"]");
		Assert.assertTrue(id1.equals(id2));
	}
	@DataProvider (name="SincronoCorrelazioneApplicativaInputBasedWSAddressing")
	public Object[][]testSincronoCorrelazioneApplicativaInputBasedWSAddressing()throws Exception{
		String id=this.repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaInputBasedWSAddressing.getNext();
		String idUnivoco=this.repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaInputBasedWSAddressingIDUnivoco.getNext();
		return new Object[][]{
				{idUnivoco,DatabaseProperties.getDatabaseComponentDiagnosticaFruitore(),DatabaseProperties.getDatabaseComponentFruitore(),id,false},	
				{idUnivoco,null,DatabaseProperties.getDatabaseComponentErogatore(),id,false}	
		};
	}
	@Test(groups={IntegrazioneCorrelazioneApplicativa.ID_GRUPPO,IntegrazioneCorrelazioneApplicativa.ID_GRUPPO+".CORRELAZIONE_APPLICATIVA_INPUT_BASED_WSADDRESSING"},
			dataProvider="SincronoCorrelazioneApplicativaInputBasedWSAddressing",dependsOnMethods={"sincronoCorrelazioneApplicativaInputBasedWSAddressing"})
	public void testSincronoCorrelazioneApplicativaInputBasedWSAddressing(String idUnivoco,DatabaseMsgDiagnosticiComponent dataMsgDiag,DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
		try{
			this.collaborazioneSPCoopBase.testSincrono(data, id, CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_INTEGRAZIONE, checkServizioApplicativo,
					null);
			Reporter.log("Numero messaggi arrivati al servizio applicativo: "+data.getVerificatoreTracciaRichiesta().isArrivedCount(id));
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isArrivedCount(id)==2);
			
			if(dataMsgDiag!=null){
				//fruitore
				
				String msgInizioCorrelazione = MSG_INIZIO_CORRELAZIONE.replace("@IDAPPLICATIVO@", idUnivoco);
				msgInizioCorrelazione = msgInizioCorrelazione.replace("@IDEGOV@", id);
				Reporter.log("Check inizio correlazione ["+msgInizioCorrelazione+"]");
				Assert.assertTrue(dataMsgDiag.isTracedMessaggio(msgInizioCorrelazione));
				
				String msgCorrelazioneAvvenuta = MSG_CORRELAZIONE.replace("@IDAPPLICATIVO@", idUnivoco);
				msgCorrelazioneAvvenuta = msgCorrelazioneAvvenuta.replace("@IDEGOV@", id);
				Reporter.log("Check correlazione ["+msgCorrelazioneAvvenuta+"]");
				Assert.assertTrue(dataMsgDiag.isTracedMessaggio(msgCorrelazioneAvvenuta));
				
				// check correlazione 
				Reporter.log("Check correlazione ["+idUnivoco+"]");
				Assert.assertTrue(dataMsgDiag.isTracedCorrelazioneApplicativaIntoMsgDiagnosticiCorrelazione(idUnivoco));
			}
			
			// Check correlazione in tracciamento
			Reporter.log("Check correlazione ["+idUnivoco+"] in tracciamento nella richiesta");
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedCorrelazioneApplicativaRichiesta(id, this.collaborazioneSPCoopBase.getMittente(), idUnivoco));
			Reporter.log("Check correlazione ["+idUnivoco+"] in tracciamento nella risposta");
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedCorrelazioneApplicativaRichiesta(id, this.collaborazioneSPCoopBase.getMittente(), idUnivoco));
			
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
			if(dataMsgDiag!=null)
				dataMsgDiag.close();
		}
	}
	
	Repository repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaInputBasedWSAddressingSenzaRiusoIDEGov=new Repository();
	Repository repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaInputBasedWSAddressingSenzaRiusoIDEGovIDUnivoco=new Repository();
	@Test(groups={IntegrazioneCorrelazioneApplicativa.ID_GRUPPO,IntegrazioneCorrelazioneApplicativa.ID_GRUPPO+".CORRELAZIONE_APPLICATIVA_INPUT_BASED_WSADDRESSING"},
			description="Test di tipo sincrono, Viene controllato se i body sono uguali e se gli attachment sono uguali",
			dependsOnMethods={"testSincronoCorrelazioneApplicativaInputBasedWSAddressing"})
	public void sincronoCorrelazioneApplicativaInputBasedWSAddressingSenzaRiusoIDEGov() throws TestSuiteException, IOException, Exception{
		
		String idUnivoco = "XXXXXXXIDXXXXXXXX_"+System.currentTimeMillis();
		this.repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaInputBasedWSAddressingSenzaRiusoIDEGovIDUnivoco.add(idUnivoco);
		
		String id1 = runClientCorrelazioneApplicativaInputBasedWSAddressing(CostantiTestSuite.PORTA_DELEGATA_PROFILO_SINCRONO_INTEGRAZIONE_CORRELAZIONE_APPLICATIVA_INPUT_BASED_SENZA_RIUSO_IDEGOV,
				idUnivoco,this.repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaInputBasedWSAddressingSenzaRiusoIDEGov);
		
		Reporter.log("Sleep 4 secondi...");
		try{
			Thread.sleep(4000);
		}catch(Exception e){}
		
		String id2 = runClientCorrelazioneApplicativaInputBasedWSAddressing(CostantiTestSuite.PORTA_DELEGATA_PROFILO_SINCRONO_INTEGRAZIONE_CORRELAZIONE_APPLICATIVA_INPUT_BASED_SENZA_RIUSO_IDEGOV,
				idUnivoco,this.repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaInputBasedWSAddressingSenzaRiusoIDEGov);
		
		Reporter.log("ID 1st invocazione["+id1+"]");
		Reporter.log("ID 2nd invocazione["+id2+"]");
		Assert.assertTrue(id1.equals(id2)==false);
	}
	@DataProvider (name="SincronoCorrelazioneApplicativaInputBasedWSAddressingSenzaRiusoIDEGov")
	public Object[][]testSincronoCorrelazioneApplicativaInputBasedWSAddressingSenzaRiusoIDEGov()throws Exception{
		String id=this.repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaInputBasedWSAddressingSenzaRiusoIDEGov.getNext();
		String idUnivoco=this.repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaInputBasedWSAddressingSenzaRiusoIDEGovIDUnivoco.getNext();
		return new Object[][]{
				{idUnivoco,DatabaseProperties.getDatabaseComponentDiagnosticaFruitore(),DatabaseProperties.getDatabaseComponentFruitore(),id,false},	
				{idUnivoco,null,DatabaseProperties.getDatabaseComponentErogatore(),id,false}	
		};
	}
	@Test(groups={IntegrazioneCorrelazioneApplicativa.ID_GRUPPO,IntegrazioneCorrelazioneApplicativa.ID_GRUPPO+".CORRELAZIONE_APPLICATIVA_INPUT_BASED_WSADDRESSING"},
			dataProvider="SincronoCorrelazioneApplicativaInputBasedWSAddressingSenzaRiusoIDEGov",dependsOnMethods={"sincronoCorrelazioneApplicativaInputBasedWSAddressingSenzaRiusoIDEGov"})
	public void testSincronoCorrelazioneApplicativaInputBasedWSAddressingSenzaRiusoIDEGov(String idUnivoco,DatabaseMsgDiagnosticiComponent dataMsgDiag,DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
		try{
			this.collaborazioneSPCoopBase.testSincrono(data, id, CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_INTEGRAZIONE, checkServizioApplicativo,
					null);
			Reporter.log("Numero messaggi arrivati al servizio applicativo: "+data.getVerificatoreTracciaRichiesta().isArrivedCount(id));
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isArrivedCount(id)==1);
			
			if(dataMsgDiag!=null){
				//fruitore
				
				String msgInizioCorrelazione = MSG_INIZIO_CORRELAZIONE.replace("@IDAPPLICATIVO@", idUnivoco);
				msgInizioCorrelazione = msgInizioCorrelazione.replace("@IDEGOV@", id);
				Reporter.log("Check inizio correlazione ["+msgInizioCorrelazione+"] non esistente");
				Assert.assertTrue(dataMsgDiag.isTracedMessaggio(msgInizioCorrelazione)==false);
				
				String msgCorrelazioneAvvenuta = MSG_CORRELAZIONE.replace("@IDAPPLICATIVO@", idUnivoco);
				msgCorrelazioneAvvenuta = msgCorrelazioneAvvenuta.replace("@IDEGOV@", id);
				Reporter.log("Check correlazione ["+msgCorrelazioneAvvenuta+"] non esistente");
				Assert.assertTrue(dataMsgDiag.isTracedMessaggio(msgCorrelazioneAvvenuta)==false);
				
				// check correlazione 
				Reporter.log("Check correlazione ["+idUnivoco+"]");
				Assert.assertTrue(dataMsgDiag.isTracedCorrelazioneApplicativaIntoMsgDiagnosticiCorrelazione(idUnivoco));
			}
			
			// Check correlazione in tracciamento
			Reporter.log("Check correlazione ["+idUnivoco+"] in tracciamento nella richiesta");
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedCorrelazioneApplicativaRichiesta(id, this.collaborazioneSPCoopBase.getMittente(), idUnivoco));
			Reporter.log("Check correlazione ["+idUnivoco+"] in tracciamento nella risposta");
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedCorrelazioneApplicativaRichiesta(id, this.collaborazioneSPCoopBase.getMittente(), idUnivoco));
			
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
			if(dataMsgDiag!=null)
				dataMsgDiag.close();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/***
	 * Test Correlazione Applicativa Input Based, input mancanti
	 */
	Repository repositorySincronoCorrelazioneApplicativaInputBasedDatiMancanti=new Repository();
	@Test(groups={IntegrazioneCorrelazioneApplicativa.ID_GRUPPO,IntegrazioneCorrelazioneApplicativa.ID_GRUPPO+".CORRELAZIONE_APPLICATIVA_INPUT_BASED_DATI_MANCANTI"},description="Test di tipo sincrono, Viene controllato se i body sono uguali e se gli attachment sono uguali")
	public void sincronoCorrelazioneApplicativaInputBasedDatiMancanti() throws TestSuiteException, IOException, Exception{
		
		Date dataInizioTest = DateManager.getDate();
		
		java.io.FileInputStream fin = null;
		DatabaseComponent dbComponentFruitore = null;
		try{
			fin = new java.io.FileInputStream(new File(Utilities.testSuiteProperties.getSoap11FileName()));

			Message msg=new Message(fin);
			msg.getSOAPPartAsBytes();
			
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositorySincronoCorrelazioneApplicativaInputBasedDatiMancanti);
			client.setSoapAction("\"TEST\"");
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_PROFILO_SINCRONO_INTEGRAZIONE_CORRELAZIONE_APPLICATIVA_INPUT_BASED);
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
				throw new Exception("Errore atteso [OPENSPCOOP_ORG_416] non si e' verificato...");
			} catch (AxisFault error) {
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				Reporter.log("Controllo fault code [OPENSPCOOP_ORG_416]");
				Assert.assertTrue(Utilities.toString(CodiceErroreIntegrazione.CODICE_416_CORRELAZIONE_APPLICATIVA_RICHIESTA_ERRORE).equals(error.getFaultCode().getLocalPart()));
				String msgErrore = "La gestione della funzionalità di correlazione applicativa, per il messaggio di richiesta, ha generato un errore: identificativo di correlazione applicativa per l'elemento [*] con modalita' di acquisizione inputBased non presente tra le informazioni di integrazione";
				Reporter.log("Controllo fault string ["+msgErrore+"]");
				Assert.assertTrue(error.getFaultString().contains(msgErrore));
				Reporter.log("Controllo fault actor [OpenSPCoop]");
				Assert.assertTrue(CostantiPdD.OPENSPCOOP2.equals(error.getFaultActor()));
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
		
		Date dataFineTest = DateManager.getDate();
		ErroreAttesoOpenSPCoopLogCore err = new ErroreAttesoOpenSPCoopLogCore();
		err.setIntervalloInferiore(dataInizioTest);
		err.setIntervalloSuperiore(dataFineTest);
		err.setMsgErrore("identificativo di correlazione applicativa per l'elemento [*] con modalita' di acquisizione inputBased non presente tra le informazioni di integrazione");
		this.erroriAttesiOpenSPCoopCore.add(err);		
	}
	@DataProvider (name="SincronoCorrelazioneApplicativaInputBasedDatiMancanti")
	public Object[][]testSincronoCorrelazioneApplicativaInputBasedDatiMancanti()throws Exception{
		String id=this.repositorySincronoCorrelazioneApplicativaInputBasedDatiMancanti.getNext();
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),id,false},	
				{DatabaseProperties.getDatabaseComponentErogatore(),id,true}	
		};
	}
	@Test(groups={IntegrazioneCorrelazioneApplicativa.ID_GRUPPO,IntegrazioneCorrelazioneApplicativa.ID_GRUPPO+".CORRELAZIONE_APPLICATIVA_INPUT_BASED_DATI_MANCANTI"},dataProvider="SincronoCorrelazioneApplicativaInputBasedDatiMancanti",dependsOnMethods={"sincronoCorrelazioneApplicativaInputBasedDatiMancanti"})
	public void testSincronoCorrelazioneApplicativaInputBasedDatiMancanti(DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
		try{
			Reporter.log("Controllo tracciamento richiesta non effettuato con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id)==false);
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/***
	 * Test Correlazione Applicativa disabilitata
	 */
		
	private String runClientCorrelazioneApplicativaDisabilitata(String idUnivoco) throws Exception{
		java.io.FileInputStream fin = null;
		DatabaseComponent dbComponentFruitore = null;
		try{
			fin = new java.io.FileInputStream(new File(Utilities.testSuiteProperties.getSoap11FileName()));

			Message msg=new Message(fin);
			msg.getSOAPPartAsBytes();

			
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaDisabilitata);
			client.setSoapAction("\"TEST\"");
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_PROFILO_SINCRONO_INTEGRAZIONE_CORRELAZIONE_APPLICATIVA_DISABILITATA);
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
			} catch (AxisFault error) {
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				throw error;
			}finally{
				dbComponentFruitore.close();
			}
			
			checkHttpRisposta(client.getPropertiesTrasportoRisposta(),CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_INTEGRAZIONE,client.getIdMessaggio());
			
			checkMessaggioRisposta(client.getResponseMessage(),CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_INTEGRAZIONE,client.getIdMessaggio());
			
			return client.getIdMessaggio();
			
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
	
	Repository repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaDisabilitata=new Repository();
	Repository repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaDisabilitataIDUnivoco=new Repository();
	@Test(groups={IntegrazioneCorrelazioneApplicativa.ID_GRUPPO,IntegrazioneCorrelazioneApplicativa.ID_GRUPPO+".CORRELAZIONE_APPLICATIVA_DISABILITATA"},description="Test di tipo sincrono, Viene controllato se i body sono uguali e se gli attachment sono uguali")
	public void sincronoCorrelazioneApplicativaDisabilitata() throws TestSuiteException, IOException, Exception{
		
		String idUnivoco = "XXXXXXXIDXXXXXXXX_"+System.currentTimeMillis();
		this.repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaDisabilitataIDUnivoco.add(idUnivoco);
		
		String id1 = runClientCorrelazioneApplicativaDisabilitata(idUnivoco);
		
		Reporter.log("Sleep 4 secondi...");
		try{
			Thread.sleep(4000);
		}catch(Exception e){}
		
		String id2 = runClientCorrelazioneApplicativaDisabilitata(idUnivoco);
		
		Reporter.log("ID 1st invocazione["+id1+"]");
		Reporter.log("ID 2nd invocazione["+id2+"]");
		Assert.assertTrue(id1.equals(id2)==false);
	}
	@DataProvider (name="SincronoCorrelazioneApplicativaDisabilitata")
	public Object[][]testSincronoCorrelazioneApplicativaDisabilitata()throws Exception{
		String id=this.repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaDisabilitata.getNext();
		String idSecondaInvocazione = this.repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaDisabilitata.getNext();
		String idUnivoco=this.repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaDisabilitataIDUnivoco.getNext();
		return new Object[][]{
				{idUnivoco,idSecondaInvocazione,DatabaseProperties.getDatabaseComponentDiagnosticaFruitore(),DatabaseProperties.getDatabaseComponentFruitore(),id,false},	
				{idUnivoco,idSecondaInvocazione,null,DatabaseProperties.getDatabaseComponentErogatore(),id,false}	
		};
	}
	@Test(groups={IntegrazioneCorrelazioneApplicativa.ID_GRUPPO,IntegrazioneCorrelazioneApplicativa.ID_GRUPPO+".CORRELAZIONE_APPLICATIVA_DISABILITATA"},
			dataProvider="SincronoCorrelazioneApplicativaDisabilitata",
			dependsOnMethods={"sincronoCorrelazioneApplicativaDisabilitata"})
	public void testSincronoCorrelazioneApplicativaDisabilitata(String idUnivoco,String idSecondaInvocazione,DatabaseMsgDiagnosticiComponent dataMsgDiag,DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
		try{
			this.collaborazioneSPCoopBase.testSincrono(data, id, CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_INTEGRAZIONE, checkServizioApplicativo,
					null);
			Reporter.log("Numero messaggi arrivati al servizio applicativo: "+data.getVerificatoreTracciaRichiesta().isArrivedCount(id));
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isArrivedCount(id)==1);
			
			this.collaborazioneSPCoopBase.testSincrono(data, idSecondaInvocazione, CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_INTEGRAZIONE, checkServizioApplicativo,
					null);
			Reporter.log("Numero messaggi arrivati al servizio applicativo: "+data.getVerificatoreTracciaRichiesta().isArrivedCount(id));
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isArrivedCount(idSecondaInvocazione)==1);
			
			if(dataMsgDiag!=null){
				//fruitore
				
				String msgInizioCorrelazione = MSG_INIZIO_CORRELAZIONE.replace("@IDAPPLICATIVO@", idUnivoco);
				msgInizioCorrelazione = msgInizioCorrelazione.replace("@IDEGOV@", id);
				Reporter.log("Check inizio correlazione ["+msgInizioCorrelazione+"]");
				Assert.assertTrue(dataMsgDiag.isTracedMessaggio(msgInizioCorrelazione)==false);
				
				String msgCorrelazioneAvvenuta = MSG_CORRELAZIONE.replace("@IDAPPLICATIVO@", idUnivoco);
				msgCorrelazioneAvvenuta = msgCorrelazioneAvvenuta.replace("@IDEGOV@", id);
				Reporter.log("Check correlazione ["+msgCorrelazioneAvvenuta+"]");
				Assert.assertTrue(dataMsgDiag.isTracedMessaggio(msgCorrelazioneAvvenuta)==false);
				
				msgInizioCorrelazione = MSG_INIZIO_CORRELAZIONE.replace("@IDAPPLICATIVO@", idUnivoco);
				msgInizioCorrelazione = msgInizioCorrelazione.replace("@IDEGOV@", idSecondaInvocazione);
				Reporter.log("Check inizio correlazione ["+msgInizioCorrelazione+"]");
				Assert.assertTrue(dataMsgDiag.isTracedMessaggio(msgInizioCorrelazione)==false);
				
				msgCorrelazioneAvvenuta = MSG_CORRELAZIONE.replace("@IDAPPLICATIVO@", idUnivoco);
				msgCorrelazioneAvvenuta = msgCorrelazioneAvvenuta.replace("@IDEGOV@", idSecondaInvocazione);
				Reporter.log("Check correlazione ["+msgCorrelazioneAvvenuta+"]");
				Assert.assertTrue(dataMsgDiag.isTracedMessaggio(msgCorrelazioneAvvenuta)==false);
				
				// check correlazione 
				Reporter.log("Check correlazione ["+idUnivoco+"] non esistente");
				Assert.assertTrue(dataMsgDiag.isTracedCorrelazioneApplicativaIntoMsgDiagnosticiCorrelazione(idUnivoco)==false);
			}
			
			// Check correlazione in tracciamento
			Reporter.log("Check correlazione ["+idUnivoco+"] in tracciamento nella richiesta non esistente");
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedCorrelazioneApplicativaRichiesta(id, this.collaborazioneSPCoopBase.getMittente(), idUnivoco)==false);
			Reporter.log("Check correlazione ["+idUnivoco+"] in tracciamento nella risposta non esistente");
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedCorrelazioneApplicativaRichiesta(id, this.collaborazioneSPCoopBase.getMittente(), idUnivoco)==false);
			
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
			if(dataMsgDiag!=null)
				dataMsgDiag.close();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	// CORRELAZIONE CON SCELTE MULTIPLE
	// 1. <elemento nome="testNome1" identificazione="contentBased" 
	//          pattern="concat_openspcoop(&#34;NOME1_&#34;,//test:idApplicativo/text(),&#34;_NOME1&#34;)" identificazione-fallita="blocca" riuso-id-egov="abilitato" />
    // 2.  <elemento nome="testNome2" identificazione="contentBased" 
	// 			pattern="concat_openspcoop(&#34;NOME2_&#34;,//test:idApplicativo/text(),&#34;_NOME2&#34;)" identificazione-fallita="accetta" riuso-id-egov="abilitato" />
    // 3.<elemento nome="testNome3" identificazione="disabilitato" />
    // 4. <elemento nome="name(//testNome4)"  identificazione="contentBased" 
	//			pattern="concat_openspcoop(&#34;NOMEXPATH1_&#34;,//test:idApplicativo/text(),&#34;_NOMEXPATH1&#34;)" identificazione-fallita="blocca" riuso-id-egov="abilitato" />
	// 5. <elemento nome="testNome5" identificazione="contentBased" 
	//          pattern="concat_openspcoop(&#34;NOME5_&#34;,//test:idApplicativo/text(),&#34;_NOME5&#34;)" identificazione-fallita="blocca" />
	// Il 5 serve per verificare che non venga riusato idegov
    // 6. <elemento identificazione="contentBased" 
	// 			pattern="concat_openspcoop(&#34;ALL_&#34;,//test:idApplicativo/text(),&#34;_ALL&#34;)" identificazione-fallita="blocca" riuso-id-egov="abilitato"/>
	
	/***
	 * Test Correlazione Applicativa con scelte multiple
	 */
		
	
	
	// CASO 1.
	// 1. <elemento nome="testNome1" identificazione="contentBased" 
	//          pattern="concat_openspcoop(&#34;NOME1_&#34;,//test:idApplicativo/text(),&#34;_NOME1&#34;)" identificazione-fallita="blocca" />
	private String runClientCorrelazioneApplicativaScelteMultipleCaso1(String idUnivoco) throws Exception{
		java.io.FileInputStream fin = null;
		DatabaseComponent dbComponentFruitore = null;
		try{
			fin = new java.io.FileInputStream(new File(Utilities.testSuiteProperties.getSoap11FileName()));

			Message msg=new Message(fin);
			msg.getSOAPPartAsBytes();

			MessageElement elemTest = (MessageElement) msg.getSOAPBody().addChildElement("testNome1", "test", "http://www.openspcoop.org");
			MessageElement elem = (MessageElement) elemTest.addChildElement("idApplicativo", "test", "http://www.openspcoop.org");
			elem.setValue(idUnivoco);
			Reporter.log("Messaggio: "+msg.getSOAPPartAsString());
			msg.getSOAPPartAsBytes();
			
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaScelteMultipleCaso1);
			client.setSoapAction("\"TEST\"");
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_PROFILO_SINCRONO_INTEGRAZIONE_CORRELAZIONE_APPLICATIVA_LISTA_CORRELAZIONI);
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
			} catch (AxisFault error) {
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				throw error;
			}finally{
				dbComponentFruitore.close();
			}
			
			checkHttpRisposta(client.getPropertiesTrasportoRisposta(),CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_INTEGRAZIONE,client.getIdMessaggio());
			
			checkMessaggioRisposta(client.getResponseMessage(),CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_INTEGRAZIONE,client.getIdMessaggio());
			
			return client.getIdMessaggio();
			
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
	
	Repository repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaScelteMultipleCaso1=new Repository();
	Repository repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaScelteMultipleCaso1IDUnivoco=new Repository();
	@Test(groups={IntegrazioneCorrelazioneApplicativa.ID_GRUPPO,IntegrazioneCorrelazioneApplicativa.ID_GRUPPO+".CORRELAZIONE_APPLICATIVA_SCELTE_MULTIPLE_CASO_1"},description="Test di tipo sincrono, Viene controllato se i body sono uguali e se gli attachment sono uguali")
	public void sincronoCorrelazioneApplicativaScelteMultipleCaso1() throws TestSuiteException, IOException, Exception{
		
		String idUnivoco = "XXXXXXXIDXXXXXXXX_"+System.currentTimeMillis();
		this.repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaScelteMultipleCaso1IDUnivoco.add(idUnivoco);
		
		String id1 = runClientCorrelazioneApplicativaScelteMultipleCaso1(idUnivoco);
		
		Reporter.log("Sleep 4 secondi...");
		try{
			Thread.sleep(4000);
		}catch(Exception e){}
		
		String id2 = runClientCorrelazioneApplicativaScelteMultipleCaso1(idUnivoco);
		
		Reporter.log("ID 1st invocazione["+id1+"]");
		Reporter.log("ID 2nd invocazione["+id2+"]");
		Assert.assertTrue(id1.equals(id2));
	}
	@DataProvider (name="SincronoCorrelazioneApplicativaScelteMultipleCaso1")
	public Object[][]testSincronoCorrelazioneApplicativaScelteMultipleCaso1()throws Exception{
		String id=this.repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaScelteMultipleCaso1.getNext();
		String idUnivoco=this.repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaScelteMultipleCaso1IDUnivoco.getNext();
		return new Object[][]{
				{idUnivoco,DatabaseProperties.getDatabaseComponentDiagnosticaFruitore(),DatabaseProperties.getDatabaseComponentFruitore(),id,false},	
				{idUnivoco,null,DatabaseProperties.getDatabaseComponentErogatore(),id,false}	
		};
	}
	@Test(groups={IntegrazioneCorrelazioneApplicativa.ID_GRUPPO,IntegrazioneCorrelazioneApplicativa.ID_GRUPPO+".CORRELAZIONE_APPLICATIVA_SCELTE_MULTIPLE_CASO_1"},
			dataProvider="SincronoCorrelazioneApplicativaScelteMultipleCaso1",dependsOnMethods={"sincronoCorrelazioneApplicativaScelteMultipleCaso1"})
	public void testSincronoCorrelazioneApplicativaScelteMultipleCaso1(String idUnivoco,DatabaseMsgDiagnosticiComponent dataMsgDiag,DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
		try{
			this.collaborazioneSPCoopBase.testSincrono(data, id, CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_INTEGRAZIONE, checkServizioApplicativo,
					null);
			Reporter.log("Numero messaggi arrivati al servizio applicativo: "+data.getVerificatoreTracciaRichiesta().isArrivedCount(id));
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isArrivedCount(id)==2);
			
			if(dataMsgDiag!=null){
				//fruitore
				
				String msgInizioCorrelazione = MSG_INIZIO_CORRELAZIONE.replace("@IDAPPLICATIVO@", "NOME1_"+idUnivoco+"_NOME1");
				msgInizioCorrelazione = msgInizioCorrelazione.replace("@IDEGOV@", id);
				Reporter.log("Check inizio correlazione ["+msgInizioCorrelazione+"]");
				Assert.assertTrue(dataMsgDiag.isTracedMessaggio(msgInizioCorrelazione));
				
				String msgCorrelazioneAvvenuta = MSG_CORRELAZIONE.replace("@IDAPPLICATIVO@", "NOME1_"+idUnivoco+"_NOME1");
				msgCorrelazioneAvvenuta = msgCorrelazioneAvvenuta.replace("@IDEGOV@", id);
				Reporter.log("Check correlazione ["+msgCorrelazioneAvvenuta+"]");
				Assert.assertTrue(dataMsgDiag.isTracedMessaggio(msgCorrelazioneAvvenuta));
				
				// check correlazione 
				Reporter.log("Check correlazione ["+"NOME1_"+idUnivoco+"_NOME1"+"]");
				Assert.assertTrue(dataMsgDiag.isTracedCorrelazioneApplicativaIntoMsgDiagnosticiCorrelazione("NOME1_"+idUnivoco+"_NOME1"));
			}
			
			// Check correlazione in tracciamento
			Reporter.log("Check correlazione ["+"NOME1_"+idUnivoco+"_NOME1"+"] in tracciamento nella richiesta");
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedCorrelazioneApplicativaRichiesta(id, this.collaborazioneSPCoopBase.getMittente(), "NOME1_"+idUnivoco+"_NOME1"));
			Reporter.log("Check correlazione ["+"NOME1_"+idUnivoco+"_NOME1"+"] in tracciamento nella risposta");
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedCorrelazioneApplicativaRichiesta(id, this.collaborazioneSPCoopBase.getMittente(), "NOME1_"+idUnivoco+"_NOME1"));
			
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
			if(dataMsgDiag!=null)
				dataMsgDiag.close();
		}
	}
	
	/***
	 * Test Correlazione Applicativa Content Based, input mancanti
	 */
	Repository repositorySincronoCorrelazioneApplicativaScelteMultipleCaso1DatiMancanti=new Repository();
	@Test(groups={IntegrazioneCorrelazioneApplicativa.ID_GRUPPO,IntegrazioneCorrelazioneApplicativa.ID_GRUPPO+".CORRELAZIONE_APPLICATIVA_SCELTE_MULTIPLE_CASO_1"},
			description="Test di tipo sincrono, Viene controllato se i body sono uguali e se gli attachment sono uguali",
			dependsOnMethods={"testSincronoCorrelazioneApplicativaScelteMultipleCaso1"})
	public void sincronoCorrelazioneApplicativaScelteMultipleCaso1DatiMancanti() throws TestSuiteException, IOException, Exception{
		
		Date dataInizioTest = DateManager.getDate();
		
		java.io.FileInputStream fin = null;
		DatabaseComponent dbComponentFruitore = null;
		try{
			fin = new java.io.FileInputStream(new File(Utilities.testSuiteProperties.getSoap11FileName()));

			Message msg=new Message(fin);
			msg.getSOAPPartAsBytes();

			msg.getSOAPBody().addChildElement("testNome1", "test", "http://www.openspcoop.org");
			Reporter.log("Messaggio: "+msg.getSOAPPartAsString());
			msg.getSOAPPartAsBytes();
			
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositorySincronoCorrelazioneApplicativaScelteMultipleCaso1DatiMancanti);
			client.setSoapAction("\"TEST\"");
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_PROFILO_SINCRONO_INTEGRAZIONE_CORRELAZIONE_APPLICATIVA_LISTA_CORRELAZIONI);
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
				throw new Exception("Errore atteso [OPENSPCOOP_ORG_416] non si e' verificato...");
			} catch (AxisFault error) {
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				Reporter.log("Controllo fault code [OPENSPCOOP_ORG_416]");
				Assert.assertTrue(Utilities.toString(CodiceErroreIntegrazione.CODICE_416_CORRELAZIONE_APPLICATIVA_RICHIESTA_ERRORE).equals(error.getFaultCode().getLocalPart()));
				String msgErrore = "identificativo di correlazione applicativa non identificato nell'elemento [testNome1] con modalita' di acquisizione contentBased (Pattern:concat_openspcoop(\"NOME1_\",//test:idApplicativo/text(),\"_NOME1\")): nessun match trovato per l'espressione xpath contenuta in concat_openspcoop (//test:idApplicativo/text())";
				Reporter.log("Controllo fault string ["+msgErrore+"]");
				Assert.assertTrue(error.getFaultString().contains(msgErrore));
				Reporter.log("Controllo fault actor [OpenSPCoop]");
				Assert.assertTrue(CostantiPdD.OPENSPCOOP2.equals(error.getFaultActor()));
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
		
		Date dataFineTest = DateManager.getDate();
		ErroreAttesoOpenSPCoopLogCore err = new ErroreAttesoOpenSPCoopLogCore();
		err.setIntervalloInferiore(dataInizioTest);
		err.setIntervalloSuperiore(dataFineTest);
		err.setMsgErrore("identificativo di correlazione applicativa non identificato nell'elemento [testNome1] con modalita' di acquisizione contentBased (Pattern:concat_openspcoop(\"NOME1_\",//test:idApplicativo/text(),\"_NOME1\")): nessun match trovato per l'espressione xpath contenuta in concat_openspcoop (//test:idApplicativo/text())");
		this.erroriAttesiOpenSPCoopCore.add(err);		
	}
	@DataProvider (name="SincronoCorrelazioneApplicativaScelteMultipleCaso1DatiMancanti")
	public Object[][]testSincronoCorrelazioneApplicativaScelteMultipleCaso1DatiMancanti()throws Exception{
		String id=this.repositorySincronoCorrelazioneApplicativaScelteMultipleCaso1DatiMancanti.getNext();
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),id,false},	
				{DatabaseProperties.getDatabaseComponentErogatore(),id,true}	
		};
	}
	@Test(groups={IntegrazioneCorrelazioneApplicativa.ID_GRUPPO,IntegrazioneCorrelazioneApplicativa.ID_GRUPPO+".CORRELAZIONE_APPLICATIVA_SCELTE_MULTIPLE_CASO_1"},
			dataProvider="SincronoCorrelazioneApplicativaScelteMultipleCaso1DatiMancanti",dependsOnMethods={"sincronoCorrelazioneApplicativaScelteMultipleCaso1DatiMancanti"})
	public void testSincronoCorrelazioneApplicativaScelteMultipleCaso1DatiMancanti(DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
		try{
			Reporter.log("Controllo tracciamento richiesta non effettuato con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id)==false);
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	
	
	
	// CASO 2.
	// 2.  <elemento nome="testNome2" identificazione="contentBased" 
	// 			pattern="concat_openspcoop(&#34;NOME2_&#34;,//test:idApplicativo/text(),&#34;_NOME2&#34;)" identificazione-fallita="accetta" />
	
	private String runClientCorrelazioneApplicativaScelteMultipleCaso2(String idUnivoco) throws Exception{
		java.io.FileInputStream fin = null;
		DatabaseComponent dbComponentFruitore = null;
		try{
			fin = new java.io.FileInputStream(new File(Utilities.testSuiteProperties.getSoap11FileName()));

			Message msg=new Message(fin);
			msg.getSOAPPartAsBytes();

			MessageElement elemTest = (MessageElement) msg.getSOAPBody().addChildElement("testNome2", "test", "http://www.openspcoop.org");
			MessageElement elem = (MessageElement) elemTest.addChildElement("idApplicativo", "test", "http://www.openspcoop.org");
			elem.setValue(idUnivoco);
			Reporter.log("Messaggio: "+msg.getSOAPPartAsString());
			msg.getSOAPPartAsBytes();
			
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaScelteMultipleCaso2);
			client.setSoapAction("\"TEST\"");
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_PROFILO_SINCRONO_INTEGRAZIONE_CORRELAZIONE_APPLICATIVA_LISTA_CORRELAZIONI);
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
			} catch (AxisFault error) {
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				throw error;
			}finally{
				dbComponentFruitore.close();
			}
			
			checkHttpRisposta(client.getPropertiesTrasportoRisposta(),CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_INTEGRAZIONE,client.getIdMessaggio());
			
			checkMessaggioRisposta(client.getResponseMessage(),CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_INTEGRAZIONE,client.getIdMessaggio());
			
			return client.getIdMessaggio();
			
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
	
	Repository repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaScelteMultipleCaso2=new Repository();
	Repository repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaScelteMultipleCaso2IDUnivoco=new Repository();
	@Test(groups={IntegrazioneCorrelazioneApplicativa.ID_GRUPPO,IntegrazioneCorrelazioneApplicativa.ID_GRUPPO+".CORRELAZIONE_APPLICATIVA_SCELTE_MULTIPLE_CASO_2"},description="Test di tipo sincrono, Viene controllato se i body sono uguali e se gli attachment sono uguali")
	public void sincronoCorrelazioneApplicativaScelteMultipleCaso2() throws TestSuiteException, IOException, Exception{
		
		String idUnivoco = "XXXXXXXIDXXXXXXXX_"+System.currentTimeMillis();
		this.repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaScelteMultipleCaso2IDUnivoco.add(idUnivoco);
		
		String id1 = runClientCorrelazioneApplicativaScelteMultipleCaso2(idUnivoco);
		
		Reporter.log("Sleep 4 secondi...");
		try{
			Thread.sleep(4000);
		}catch(Exception e){}
		
		String id2 = runClientCorrelazioneApplicativaScelteMultipleCaso2(idUnivoco);
		
		Reporter.log("ID 1st invocazione["+id1+"]");
		Reporter.log("ID 2nd invocazione["+id2+"]");
		Assert.assertTrue(id1.equals(id2));
	}
	@DataProvider (name="SincronoCorrelazioneApplicativaScelteMultipleCaso2")
	public Object[][]testSincronoCorrelazioneApplicativaScelteMultipleCaso2()throws Exception{
		String id=this.repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaScelteMultipleCaso2.getNext();
		String idUnivoco=this.repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaScelteMultipleCaso2IDUnivoco.getNext();
		return new Object[][]{
				{idUnivoco,DatabaseProperties.getDatabaseComponentDiagnosticaFruitore(),DatabaseProperties.getDatabaseComponentFruitore(),id,false},	
				{idUnivoco,null,DatabaseProperties.getDatabaseComponentErogatore(),id,false}	
		};
	}
	@Test(groups={IntegrazioneCorrelazioneApplicativa.ID_GRUPPO,IntegrazioneCorrelazioneApplicativa.ID_GRUPPO+".CORRELAZIONE_APPLICATIVA_SCELTE_MULTIPLE_CASO_2"},
			dataProvider="SincronoCorrelazioneApplicativaScelteMultipleCaso2",dependsOnMethods={"sincronoCorrelazioneApplicativaScelteMultipleCaso2"})
	public void testSincronoCorrelazioneApplicativaScelteMultipleCaso2(String idUnivoco,DatabaseMsgDiagnosticiComponent dataMsgDiag,DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
		try{
			this.collaborazioneSPCoopBase.testSincrono(data, id, CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_INTEGRAZIONE, checkServizioApplicativo,
					null);
			Reporter.log("Numero messaggi arrivati al servizio applicativo: "+data.getVerificatoreTracciaRichiesta().isArrivedCount(id));
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isArrivedCount(id)==2);
			
			if(dataMsgDiag!=null){
				//fruitore
				
				String msgInizioCorrelazione = MSG_INIZIO_CORRELAZIONE.replace("@IDAPPLICATIVO@", "NOME2_"+idUnivoco+"_NOME2");
				msgInizioCorrelazione = msgInizioCorrelazione.replace("@IDEGOV@", id);
				Reporter.log("Check inizio correlazione ["+msgInizioCorrelazione+"]");
				Assert.assertTrue(dataMsgDiag.isTracedMessaggio(msgInizioCorrelazione));
				
				String msgCorrelazioneAvvenuta = MSG_CORRELAZIONE.replace("@IDAPPLICATIVO@", "NOME2_"+idUnivoco+"_NOME2");
				msgCorrelazioneAvvenuta = msgCorrelazioneAvvenuta.replace("@IDEGOV@", id);
				Reporter.log("Check correlazione ["+msgCorrelazioneAvvenuta+"]");
				Assert.assertTrue(dataMsgDiag.isTracedMessaggio(msgCorrelazioneAvvenuta));
				
				// check correlazione 
				Reporter.log("Check correlazione ["+"NOME2_"+idUnivoco+"_NOME2"+"]");
				Assert.assertTrue(dataMsgDiag.isTracedCorrelazioneApplicativaIntoMsgDiagnosticiCorrelazione("NOME2_"+idUnivoco+"_NOME2"));
			}
			
			// Check correlazione in tracciamento
			Reporter.log("Check correlazione ["+"NOME2_"+idUnivoco+"_NOME2"+"] in tracciamento nella richiesta");
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedCorrelazioneApplicativaRichiesta(id, this.collaborazioneSPCoopBase.getMittente(), "NOME2_"+idUnivoco+"_NOME2"));
			Reporter.log("Check correlazione ["+"NOME2_"+idUnivoco+"_NOME2"+"] in tracciamento nella risposta");
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedCorrelazioneApplicativaRichiesta(id, this.collaborazioneSPCoopBase.getMittente(), "NOME2_"+idUnivoco+"_NOME2"));
			
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
			if(dataMsgDiag!=null)
				dataMsgDiag.close();
		}
	}
	
	/***
	 * Test Correlazione Applicativa Content Based
	 */
		
	private String runClientCorrelazioneApplicativaScelteMultipleCaso2_erroreAccettato(String idUnivoco) throws Exception{
		java.io.FileInputStream fin = null;
		DatabaseComponent dbComponentFruitore = null;
		try{
			fin = new java.io.FileInputStream(new File(Utilities.testSuiteProperties.getSoap11FileName()));

			Message msg=new Message(fin);
			msg.getSOAPPartAsBytes();

			msg.getSOAPBody().addChildElement("testNome2", "test", "http://www.openspcoop.org");
			Reporter.log("Messaggio: "+msg.getSOAPPartAsString());
			msg.getSOAPPartAsBytes();
			
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaScelteMultipleCaso2_erroreAccettato);
			client.setSoapAction("\"TEST\"");
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_PROFILO_SINCRONO_INTEGRAZIONE_CORRELAZIONE_APPLICATIVA_LISTA_CORRELAZIONI);
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
			} catch (AxisFault error) {
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				throw error;
			}finally{
				dbComponentFruitore.close();
			}
			
			checkHttpRisposta(client.getPropertiesTrasportoRisposta(),CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_INTEGRAZIONE,client.getIdMessaggio());
			
			checkMessaggioRisposta(client.getResponseMessage(),CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_INTEGRAZIONE,client.getIdMessaggio());
			
			return client.getIdMessaggio();
			
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
	
	Repository repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaScelteMultipleCaso2_erroreAccettato=new Repository();
	Repository repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaScelteMultipleCaso2IDUnivoco_erroreAccettato=new Repository();
	@Test(groups={IntegrazioneCorrelazioneApplicativa.ID_GRUPPO,IntegrazioneCorrelazioneApplicativa.ID_GRUPPO+".CORRELAZIONE_APPLICATIVA_SCELTE_MULTIPLE_CASO_2"},
			description="Test di tipo sincrono, Viene controllato se i body sono uguali e se gli attachment sono uguali",
			dependsOnMethods={"testSincronoCorrelazioneApplicativaScelteMultipleCaso2"})
	public void sincronoCorrelazioneApplicativaScelteMultipleCaso2_erroreAccettato() throws TestSuiteException, IOException, Exception{
		
		String idUnivoco = "XXXXXXXIDXXXXXXXX_"+System.currentTimeMillis();
		this.repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaScelteMultipleCaso2IDUnivoco_erroreAccettato.add(idUnivoco);
		
		String id1 = runClientCorrelazioneApplicativaScelteMultipleCaso2_erroreAccettato(idUnivoco);
		
		Reporter.log("Sleep 4 secondi...");
		try{
			Thread.sleep(4000);
		}catch(Exception e){}
		
		String id2 = runClientCorrelazioneApplicativaScelteMultipleCaso2_erroreAccettato(idUnivoco);
		
		Reporter.log("ID 1st invocazione["+id1+"]");
		Reporter.log("ID 2nd invocazione["+id2+"]");
		Assert.assertTrue(id1.equals(id2)==false);
	}
	@DataProvider (name="SincronoCorrelazioneApplicativaScelteMultipleCaso2_erroreAccettato")
	public Object[][]testSincronoCorrelazioneApplicativaScelteMultipleCaso2_erroreAccettato()throws Exception{
		String id=this.repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaScelteMultipleCaso2_erroreAccettato.getNext();
		String idSecondaInvocazione=this.repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaScelteMultipleCaso2_erroreAccettato.getNext();
		String idUnivoco = this.repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaScelteMultipleCaso2IDUnivoco_erroreAccettato.getNext();
		return new Object[][]{
				{idUnivoco,idSecondaInvocazione,DatabaseProperties.getDatabaseComponentDiagnosticaFruitore(),DatabaseProperties.getDatabaseComponentFruitore(),id,false},	
				{idUnivoco,idSecondaInvocazione,null,DatabaseProperties.getDatabaseComponentErogatore(),id,false}	
		};
	}
	@Test(groups={IntegrazioneCorrelazioneApplicativa.ID_GRUPPO,IntegrazioneCorrelazioneApplicativa.ID_GRUPPO+".CORRELAZIONE_APPLICATIVA_SCELTE_MULTIPLE_CASO_2"},
			dataProvider="SincronoCorrelazioneApplicativaScelteMultipleCaso2_erroreAccettato",dependsOnMethods={"sincronoCorrelazioneApplicativaScelteMultipleCaso2_erroreAccettato"})
	public void testSincronoCorrelazioneApplicativaScelteMultipleCaso2_erroreAccettato(String idUnivoco,String idSecondaInvocazione,DatabaseMsgDiagnosticiComponent dataMsgDiag,DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
		try{
			this.collaborazioneSPCoopBase.testSincrono(data, id, CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_INTEGRAZIONE, checkServizioApplicativo,
					null);
			
			Reporter.log("Numero messaggi arrivati al servizio applicativo: "+data.getVerificatoreTracciaRichiesta().isArrivedCount(id));
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isArrivedCount(id)==1);
			
			this.collaborazioneSPCoopBase.testSincrono(data, idSecondaInvocazione, CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_INTEGRAZIONE, checkServizioApplicativo,
					null);
			
			Reporter.log("Numero messaggi arrivati al servizio applicativo: "+data.getVerificatoreTracciaRichiesta().isArrivedCount(id));
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isArrivedCount(idSecondaInvocazione)==1);
			
			if(dataMsgDiag!=null){
				//fruitore
				
				String msgInizioCorrelazione = MSG_INIZIO_CORRELAZIONE.replace("@IDEGOV@", id);
				String [] msgRicerca = msgInizioCorrelazione.split("@IDAPPLICATIVO@");
				Reporter.log("Check inizio correlazione ["+msgInizioCorrelazione+"]");
				Assert.assertTrue(dataMsgDiag.isTracedMessaggio(msgRicerca)==false);
				
				String msgCorrelazioneAvvenuta = MSG_CORRELAZIONE.replace("@IDEGOV@", id);
				msgRicerca = msgCorrelazioneAvvenuta.split("@IDAPPLICATIVO@");
				Reporter.log("Check inizio correlazione ["+msgCorrelazioneAvvenuta+"]");
				Assert.assertTrue(dataMsgDiag.isTracedMessaggio(msgRicerca)==false);
				
				msgInizioCorrelazione = MSG_INIZIO_CORRELAZIONE.replace("@IDEGOV@", idSecondaInvocazione);
				msgRicerca = msgInizioCorrelazione.split("@IDAPPLICATIVO@");
				Reporter.log("Check inizio correlazione ["+msgInizioCorrelazione+"]");
				Assert.assertTrue(dataMsgDiag.isTracedMessaggio(msgRicerca)==false);
				
				msgCorrelazioneAvvenuta = MSG_CORRELAZIONE.replace("@IDEGOV@", idSecondaInvocazione);
				msgRicerca = msgCorrelazioneAvvenuta.split("@IDAPPLICATIVO@");
				Reporter.log("Check inizio correlazione ["+msgCorrelazioneAvvenuta+"]");
				Assert.assertTrue(dataMsgDiag.isTracedMessaggio(msgRicerca)==false);
				
				// check correlazione 
				Reporter.log("Check correlazione ["+idUnivoco+"] non esistente");
				Assert.assertTrue(dataMsgDiag.isTracedCorrelazioneApplicativaIntoMsgDiagnosticiCorrelazione(idUnivoco)==false);
			}
			
			// Check correlazione in tracciamento
			Reporter.log("Check correlazione ["+idUnivoco+"] in tracciamento nella richiesta non esistente");
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedCorrelazioneApplicativaRichiesta(id, this.collaborazioneSPCoopBase.getMittente(), idUnivoco)==false);
			Reporter.log("Check correlazione ["+idUnivoco+"] in tracciamento nella risposta non esistente");
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedCorrelazioneApplicativaRichiesta(id, this.collaborazioneSPCoopBase.getMittente(), idUnivoco)==false);
			
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
			if(dataMsgDiag!=null)
				dataMsgDiag.close();
		}
	}
	

	// CASO 3
	// 3.<elemento nome="testNome3" identificazione="disabilitato" />
	private String runClientCorrelazioneApplicativaMultipleCaso3(String idUnivoco) throws Exception{
		java.io.FileInputStream fin = null;
		DatabaseComponent dbComponentFruitore = null;
		try{
			fin = new java.io.FileInputStream(new File(Utilities.testSuiteProperties.getSoap11FileName()));

			Message msg=new Message(fin);
			msg.getSOAPPartAsBytes();

			msg.getSOAPBody().addChildElement("testNome3", "test", "http://www.openspcoop.org");
			Reporter.log("Messaggio: "+msg.getSOAPPartAsString());
			msg.getSOAPPartAsBytes();

			
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaMultipleCaso3);
			client.setSoapAction("\"TEST\"");
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_PROFILO_SINCRONO_INTEGRAZIONE_CORRELAZIONE_APPLICATIVA_LISTA_CORRELAZIONI);
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
			} catch (AxisFault error) {
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				throw error;
			}finally{
				dbComponentFruitore.close();
			}
			
			checkHttpRisposta(client.getPropertiesTrasportoRisposta(),CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_INTEGRAZIONE,client.getIdMessaggio());
			
			checkMessaggioRisposta(client.getResponseMessage(),CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_INTEGRAZIONE,client.getIdMessaggio());
			
			return client.getIdMessaggio();
			
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
	
	Repository repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaMultipleCaso3=new Repository();
	Repository repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaMultipleCaso3IDUnivoco=new Repository();
	@Test(groups={IntegrazioneCorrelazioneApplicativa.ID_GRUPPO,IntegrazioneCorrelazioneApplicativa.ID_GRUPPO+".CORRELAZIONE_APPLICATIVA_SCELTE_MULTIPLE_CASO_3"},description="Test di tipo sincrono, Viene controllato se i body sono uguali e se gli attachment sono uguali")
	public void sincronoCorrelazioneApplicativaMultipleCaso3() throws TestSuiteException, IOException, Exception{
		
		String idUnivoco = "XXXXXXXIDXXXXXXXX_"+System.currentTimeMillis();
		this.repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaMultipleCaso3IDUnivoco.add(idUnivoco);
		
		String id1 = runClientCorrelazioneApplicativaMultipleCaso3(idUnivoco);
		
		Reporter.log("Sleep 4 secondi...");
		try{
			Thread.sleep(4000);
		}catch(Exception e){}
		
		String id2 = runClientCorrelazioneApplicativaMultipleCaso3(idUnivoco);
		
		Reporter.log("ID 1st invocazione["+id1+"]");
		Reporter.log("ID 2nd invocazione["+id2+"]");
		Assert.assertTrue(id1.equals(id2)==false);
	}
	@DataProvider (name="SincronoCorrelazioneApplicativaMultipleCaso3")
	public Object[][]testSincronoCorrelazioneApplicativaMultipleCaso3()throws Exception{
		String id=this.repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaMultipleCaso3.getNext();
		String idSecondaInvocazione = this.repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaMultipleCaso3.getNext();
		String idUnivoco=this.repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaMultipleCaso3IDUnivoco.getNext();
		return new Object[][]{
				{idUnivoco,idSecondaInvocazione,DatabaseProperties.getDatabaseComponentDiagnosticaFruitore(),DatabaseProperties.getDatabaseComponentFruitore(),id,false},	
				{idUnivoco,idSecondaInvocazione,null,DatabaseProperties.getDatabaseComponentErogatore(),id,false}	
		};
	}
	@Test(groups={IntegrazioneCorrelazioneApplicativa.ID_GRUPPO,IntegrazioneCorrelazioneApplicativa.ID_GRUPPO+".CORRELAZIONE_APPLICATIVA_SCELTE_MULTIPLE_CASO_3"},
			dataProvider="SincronoCorrelazioneApplicativaMultipleCaso3",
			dependsOnMethods={"sincronoCorrelazioneApplicativaMultipleCaso3"})
	public void testSincronoCorrelazioneApplicativaMultipleCaso3(String idUnivoco,String idSecondaInvocazione,DatabaseMsgDiagnosticiComponent dataMsgDiag,DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
		try{
			this.collaborazioneSPCoopBase.testSincrono(data, id, CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_INTEGRAZIONE, checkServizioApplicativo,
					null);
			Reporter.log("Numero messaggi arrivati al servizio applicativo: "+data.getVerificatoreTracciaRichiesta().isArrivedCount(id));
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isArrivedCount(id)==1);
			
			this.collaborazioneSPCoopBase.testSincrono(data, idSecondaInvocazione, CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_INTEGRAZIONE, checkServizioApplicativo,
					null);
			Reporter.log("Numero messaggi arrivati al servizio applicativo: "+data.getVerificatoreTracciaRichiesta().isArrivedCount(id));
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isArrivedCount(idSecondaInvocazione)==1);
			
			if(dataMsgDiag!=null){
				//fruitore
				
				String msgInizioCorrelazione = MSG_INIZIO_CORRELAZIONE.replace("@IDAPPLICATIVO@", idUnivoco);
				msgInizioCorrelazione = msgInizioCorrelazione.replace("@IDEGOV@", id);
				Reporter.log("Check inizio correlazione ["+msgInizioCorrelazione+"]");
				Assert.assertTrue(dataMsgDiag.isTracedMessaggio(msgInizioCorrelazione)==false);
				
				String msgCorrelazioneAvvenuta = MSG_CORRELAZIONE.replace("@IDAPPLICATIVO@", idUnivoco);
				msgCorrelazioneAvvenuta = msgCorrelazioneAvvenuta.replace("@IDEGOV@", id);
				Reporter.log("Check correlazione ["+msgCorrelazioneAvvenuta+"]");
				Assert.assertTrue(dataMsgDiag.isTracedMessaggio(msgCorrelazioneAvvenuta)==false);
				
				msgInizioCorrelazione = MSG_INIZIO_CORRELAZIONE.replace("@IDAPPLICATIVO@", idUnivoco);
				msgInizioCorrelazione = msgInizioCorrelazione.replace("@IDEGOV@", idSecondaInvocazione);
				Reporter.log("Check inizio correlazione ["+msgInizioCorrelazione+"]");
				Assert.assertTrue(dataMsgDiag.isTracedMessaggio(msgInizioCorrelazione)==false);
				
				msgCorrelazioneAvvenuta = MSG_CORRELAZIONE.replace("@IDAPPLICATIVO@", idUnivoco);
				msgCorrelazioneAvvenuta = msgCorrelazioneAvvenuta.replace("@IDEGOV@", idSecondaInvocazione);
				Reporter.log("Check correlazione ["+msgCorrelazioneAvvenuta+"]");
				Assert.assertTrue(dataMsgDiag.isTracedMessaggio(msgCorrelazioneAvvenuta)==false);
				
				// check correlazione 
				Reporter.log("Check correlazione ["+idUnivoco+"] non esistente");
				Assert.assertTrue(dataMsgDiag.isTracedCorrelazioneApplicativaIntoMsgDiagnosticiCorrelazione(idUnivoco)==false);
			}
			
			// Check correlazione in tracciamento
			Reporter.log("Check correlazione ["+idUnivoco+"] in tracciamento nella richiesta non esistente");
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedCorrelazioneApplicativaRichiesta(id, this.collaborazioneSPCoopBase.getMittente(), idUnivoco)==false);
			Reporter.log("Check correlazione ["+idUnivoco+"] in tracciamento nella risposta non esistente");
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedCorrelazioneApplicativaRichiesta(id, this.collaborazioneSPCoopBase.getMittente(), idUnivoco)==false);
			
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
			if(dataMsgDiag!=null)
				dataMsgDiag.close();
		}
	}
	
	
	
	
	
	// 4. <elemento nome="name(//test:testNome4)"  identificazione="contentBased" 
	//			pattern="concat_openspcoop(&#34;NOMEXPATH1_&#34;,//test:idApplicativo/text(),&#34;_NOMEXPATH1&#34;)" identificazione-fallita="blocca" />
	
	private String runClientCorrelazioneApplicativaScelteMultipleCaso4(String idUnivoco) throws Exception{
		java.io.FileInputStream fin = null;
		DatabaseComponent dbComponentFruitore = null;
		try{
			fin = new java.io.FileInputStream(new File(Utilities.testSuiteProperties.getSoap11FileName()));

			Message msg=new Message(fin);
			msg.getSOAPPartAsBytes();

			MessageElement elemTest = (MessageElement) msg.getSOAPBody().addChildElement("testNome4","test","http://www.openspcoop.org");
			MessageElement elem = (MessageElement) elemTest.addChildElement("idApplicativo", "test", "http://www.openspcoop.org");
			elem.setValue(idUnivoco);
			Reporter.log("Messaggio: "+msg.getSOAPPartAsString());
			msg.getSOAPPartAsBytes();
			
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaScelteMultipleCaso4);
			client.setSoapAction("\"TEST\"");
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_PROFILO_SINCRONO_INTEGRAZIONE_CORRELAZIONE_APPLICATIVA_LISTA_CORRELAZIONI);
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
			} catch (AxisFault error) {
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				throw error;
			}finally{
				dbComponentFruitore.close();
			}
			
			checkHttpRisposta(client.getPropertiesTrasportoRisposta(),CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_INTEGRAZIONE,client.getIdMessaggio());
			
			checkMessaggioRisposta(client.getResponseMessage(),CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_INTEGRAZIONE,client.getIdMessaggio());
			
			return client.getIdMessaggio();
			
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
	
	Repository repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaScelteMultipleCaso4=new Repository();
	Repository repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaScelteMultipleCaso4IDUnivoco=new Repository();
	@Test(groups={IntegrazioneCorrelazioneApplicativa.ID_GRUPPO,IntegrazioneCorrelazioneApplicativa.ID_GRUPPO+".CORRELAZIONE_APPLICATIVA_SCELTE_MULTIPLE_CASO_4"},description="Test di tipo sincrono, Viene controllato se i body sono uguali e se gli attachment sono uguali")
	public void sincronoCorrelazioneApplicativaScelteMultipleCaso4() throws TestSuiteException, IOException, Exception{
		
		String idUnivoco = "XXXXXXXIDXXXXXXXX_"+System.currentTimeMillis();
		this.repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaScelteMultipleCaso4IDUnivoco.add(idUnivoco);
		
		String id1 = runClientCorrelazioneApplicativaScelteMultipleCaso4(idUnivoco);
		
		Reporter.log("Sleep 4 secondi...");
		try{
			Thread.sleep(4000);
		}catch(Exception e){}
		
		String id2 = runClientCorrelazioneApplicativaScelteMultipleCaso4(idUnivoco);
		
		Reporter.log("ID 1st invocazione["+id1+"]");
		Reporter.log("ID 2nd invocazione["+id2+"]");
		Assert.assertTrue(id1.equals(id2));
	}
	@DataProvider (name="SincronoCorrelazioneApplicativaScelteMultipleCaso4")
	public Object[][]testSincronoCorrelazioneApplicativaScelteMultipleCaso4()throws Exception{
		String id=this.repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaScelteMultipleCaso4.getNext();
		String idUnivoco=this.repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaScelteMultipleCaso4IDUnivoco.getNext();
		return new Object[][]{
				{idUnivoco,DatabaseProperties.getDatabaseComponentDiagnosticaFruitore(),DatabaseProperties.getDatabaseComponentFruitore(),id,false},	
				{idUnivoco,null,DatabaseProperties.getDatabaseComponentErogatore(),id,false}	
		};
	}
	@Test(groups={IntegrazioneCorrelazioneApplicativa.ID_GRUPPO,IntegrazioneCorrelazioneApplicativa.ID_GRUPPO+".CORRELAZIONE_APPLICATIVA_SCELTE_MULTIPLE_CASO_4"},
			dataProvider="SincronoCorrelazioneApplicativaScelteMultipleCaso4",dependsOnMethods={"sincronoCorrelazioneApplicativaScelteMultipleCaso4"})
	public void testSincronoCorrelazioneApplicativaScelteMultipleCaso4(String idUnivoco,DatabaseMsgDiagnosticiComponent dataMsgDiag,DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
		try{
			this.collaborazioneSPCoopBase.testSincrono(data, id, CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_INTEGRAZIONE, checkServizioApplicativo,
					null);
			Reporter.log("Numero messaggi arrivati al servizio applicativo: "+data.getVerificatoreTracciaRichiesta().isArrivedCount(id));
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isArrivedCount(id)==2);
			
			if(dataMsgDiag!=null){
				//fruitore
				
				String msgInizioCorrelazione = MSG_INIZIO_CORRELAZIONE.replace("@IDAPPLICATIVO@", "NOMEXPATH1_"+idUnivoco+"_NOMEXPATH1");
				msgInizioCorrelazione = msgInizioCorrelazione.replace("@IDEGOV@", id);
				Reporter.log("Check inizio correlazione ["+msgInizioCorrelazione+"]");
				Assert.assertTrue(dataMsgDiag.isTracedMessaggio(msgInizioCorrelazione));
				
				String msgCorrelazioneAvvenuta = MSG_CORRELAZIONE.replace("@IDAPPLICATIVO@", "NOMEXPATH1_"+idUnivoco+"_NOMEXPATH1");
				msgCorrelazioneAvvenuta = msgCorrelazioneAvvenuta.replace("@IDEGOV@", id);
				Reporter.log("Check correlazione ["+msgCorrelazioneAvvenuta+"]");
				Assert.assertTrue(dataMsgDiag.isTracedMessaggio(msgCorrelazioneAvvenuta));
				
				// check correlazione 
				Reporter.log("Check correlazione ["+"NOMEXPATH1_"+idUnivoco+"_NOMEXPATH1"+"]");
				Assert.assertTrue(dataMsgDiag.isTracedCorrelazioneApplicativaIntoMsgDiagnosticiCorrelazione("NOMEXPATH1_"+idUnivoco+"_NOMEXPATH1"));
			}
			
			// Check correlazione in tracciamento
			Reporter.log("Check correlazione ["+"NOMEXPATH1_"+idUnivoco+"_NOMEXPATH1"+"] in tracciamento nella richiesta");
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedCorrelazioneApplicativaRichiesta(id, this.collaborazioneSPCoopBase.getMittente(), "NOMEXPATH1_"+idUnivoco+"_NOMEXPATH1"));
			Reporter.log("Check correlazione ["+"NOMEXPATH1_"+idUnivoco+"_NOMEXPATH1"+"] in tracciamento nella risposta");
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedCorrelazioneApplicativaRichiesta(id, this.collaborazioneSPCoopBase.getMittente(), "NOMEXPATH1_"+idUnivoco+"_NOMEXPATH1"));
			
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
			if(dataMsgDiag!=null)
				dataMsgDiag.close();
		}
	}
	
	/***
	 * Test Correlazione Applicativa Content Based, input mancanti
	 */
	Repository repositorySincronoCorrelazioneApplicativaScelteMultipleCaso4DatiMancanti=new Repository();
	@Test(groups={IntegrazioneCorrelazioneApplicativa.ID_GRUPPO,IntegrazioneCorrelazioneApplicativa.ID_GRUPPO+".CORRELAZIONE_APPLICATIVA_SCELTE_MULTIPLE_CASO_4"},
			description="Test di tipo sincrono, Viene controllato se i body sono uguali e se gli attachment sono uguali",
			dependsOnMethods={"testSincronoCorrelazioneApplicativaScelteMultipleCaso4"})
	public void sincronoCorrelazioneApplicativaScelteMultipleCaso4DatiMancanti() throws TestSuiteException, IOException, Exception{
		
		Date dataInizioTest = DateManager.getDate();
		
		java.io.FileInputStream fin = null;
		DatabaseComponent dbComponentFruitore = null;
		try{
			fin = new java.io.FileInputStream(new File(Utilities.testSuiteProperties.getSoap11FileName()));

			Message msg=new Message(fin);
			msg.getSOAPPartAsBytes();

			msg.getSOAPBody().addChildElement("testNome4","test","http://www.openspcoop.org");
			Reporter.log("Messaggio: "+msg.getSOAPPartAsString());
			msg.getSOAPPartAsBytes();
			
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositorySincronoCorrelazioneApplicativaScelteMultipleCaso4DatiMancanti);
			client.setSoapAction("\"TEST\"");
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_PROFILO_SINCRONO_INTEGRAZIONE_CORRELAZIONE_APPLICATIVA_LISTA_CORRELAZIONI);
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
				throw new Exception("Errore atteso [OPENSPCOOP_ORG_416] non si e' verificato...");
			} catch (AxisFault error) {
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				Reporter.log("Controllo fault code [OPENSPCOOP_ORG_416]");
				Assert.assertTrue(Utilities.toString(CodiceErroreIntegrazione.CODICE_416_CORRELAZIONE_APPLICATIVA_RICHIESTA_ERRORE).equals(error.getFaultCode().getLocalPart()));
				String msgErrore = "identificativo di correlazione applicativa non identificato nell'elemento [test:testNome4] con modalita' di acquisizione contentBased (Pattern:concat_openspcoop(\"NOMEXPATH1_\",//test:idApplicativo/text(),\"_NOMEXPATH1\")): nessun match trovato per l'espressione xpath contenuta in concat_openspcoop (//test:idApplicativo/text())";
				Reporter.log("Controllo fault string ["+msgErrore+"]");
				Assert.assertTrue(error.getFaultString().contains(msgErrore));
				Reporter.log("Controllo fault actor [OpenSPCoop]");
				Assert.assertTrue(CostantiPdD.OPENSPCOOP2.equals(error.getFaultActor()));
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
		
		Date dataFineTest = DateManager.getDate();
		ErroreAttesoOpenSPCoopLogCore err = new ErroreAttesoOpenSPCoopLogCore();
		err.setIntervalloInferiore(dataInizioTest);
		err.setIntervalloSuperiore(dataFineTest);
		err.setMsgErrore("identificativo di correlazione applicativa non identificato nell'elemento [test:testNome4] con modalita' di acquisizione contentBased (Pattern:concat_openspcoop(\"NOMEXPATH1_\",//test:idApplicativo/text(),\"_NOMEXPATH1\")): nessun match trovato per l'espressione xpath contenuta in concat_openspcoop (//test:idApplicativo/text())");
		this.erroriAttesiOpenSPCoopCore.add(err);	
	}
	@DataProvider (name="SincronoCorrelazioneApplicativaScelteMultipleCaso4DatiMancanti")
	public Object[][]testSincronoCorrelazioneApplicativaScelteMultipleCaso4DatiMancanti()throws Exception{
		String id=this.repositorySincronoCorrelazioneApplicativaScelteMultipleCaso4DatiMancanti.getNext();
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),id,false},	
				{DatabaseProperties.getDatabaseComponentErogatore(),id,true}	
		};
	}
	@Test(groups={IntegrazioneCorrelazioneApplicativa.ID_GRUPPO,IntegrazioneCorrelazioneApplicativa.ID_GRUPPO+".CORRELAZIONE_APPLICATIVA_SCELTE_MULTIPLE_CASO_4"},
			dataProvider="SincronoCorrelazioneApplicativaScelteMultipleCaso4DatiMancanti",dependsOnMethods={"sincronoCorrelazioneApplicativaScelteMultipleCaso4DatiMancanti"})
	public void testSincronoCorrelazioneApplicativaScelteMultipleCaso4DatiMancanti(DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
		try{
			Reporter.log("Controllo tracciamento richiesta non effettuato con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id)==false);
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	
	
	
	
	
	
	// 5. <elemento nome="testNome5" identificazione="contentBased" 
	//          pattern="concat_openspcoop(&#34;NOME5_&#34;,//test:idApplicativo/text(),&#34;_NOME5&#34;)" identificazione-fallita="blocca" />
	// Il 5 serve per verificare che non venga riusato idegov
	private String runClientCorrelazioneApplicativaScelteMultipleCaso5(String idUnivoco) throws Exception{
		java.io.FileInputStream fin = null;
		DatabaseComponent dbComponentFruitore = null;
		try{
			fin = new java.io.FileInputStream(new File(Utilities.testSuiteProperties.getSoap11FileName()));

			Message msg=new Message(fin);
			msg.getSOAPPartAsBytes();

			MessageElement elemTest = (MessageElement) msg.getSOAPBody().addChildElement("testNome5", "test", "http://www.openspcoop.org");
			MessageElement elem = (MessageElement) elemTest.addChildElement("idApplicativo", "test", "http://www.openspcoop.org");
			elem.setValue(idUnivoco);
			Reporter.log("Messaggio: "+msg.getSOAPPartAsString());
			msg.getSOAPPartAsBytes();
			
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaScelteMultipleCaso5);
			client.setSoapAction("\"TEST\"");
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_PROFILO_SINCRONO_INTEGRAZIONE_CORRELAZIONE_APPLICATIVA_LISTA_CORRELAZIONI);
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
			} catch (AxisFault error) {
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				throw error;
			}finally{
				dbComponentFruitore.close();
			}
			
			checkHttpRisposta(client.getPropertiesTrasportoRisposta(),CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_INTEGRAZIONE,client.getIdMessaggio());
			
			checkMessaggioRisposta(client.getResponseMessage(),CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_INTEGRAZIONE,client.getIdMessaggio());
			
			return client.getIdMessaggio();
			
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
	
	Repository repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaScelteMultipleCaso5=new Repository();
	Repository repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaScelteMultipleCaso5IDUnivoco=new Repository();
	@Test(groups={IntegrazioneCorrelazioneApplicativa.ID_GRUPPO,IntegrazioneCorrelazioneApplicativa.ID_GRUPPO+".CORRELAZIONE_APPLICATIVA_SCELTE_MULTIPLE_CASO_5"},description="Test di tipo sincrono, Viene controllato se i body sono uguali e se gli attachment sono uguali")
	public void sincronoCorrelazioneApplicativaScelteMultipleCaso5() throws TestSuiteException, IOException, Exception{
		
		String idUnivoco = "XXXXXXXIDXXXXXXXX_"+System.currentTimeMillis();
		this.repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaScelteMultipleCaso5IDUnivoco.add(idUnivoco);
		
		String id1 = runClientCorrelazioneApplicativaScelteMultipleCaso5(idUnivoco);
		
		Reporter.log("Sleep 4 secondi...");
		try{
			Thread.sleep(4000);
		}catch(Exception e){}
		
		String id2 = runClientCorrelazioneApplicativaScelteMultipleCaso5(idUnivoco);
		
		Reporter.log("ID 1st invocazione["+id1+"]");
		Reporter.log("ID 2nd invocazione["+id2+"]");
		Assert.assertTrue(id1.equals(id2)==false);
	}
	@DataProvider (name="SincronoCorrelazioneApplicativaScelteMultipleCaso5")
	public Object[][]testSincronoCorrelazioneApplicativaScelteMultipleCaso5()throws Exception{
		String id=this.repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaScelteMultipleCaso5.getNext();
		String idUnivoco=this.repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaScelteMultipleCaso5IDUnivoco.getNext();
		return new Object[][]{
				{idUnivoco,DatabaseProperties.getDatabaseComponentDiagnosticaFruitore(),DatabaseProperties.getDatabaseComponentFruitore(),id,false},	
				{idUnivoco,null,DatabaseProperties.getDatabaseComponentErogatore(),id,false}	
		};
	}
	@Test(groups={IntegrazioneCorrelazioneApplicativa.ID_GRUPPO,IntegrazioneCorrelazioneApplicativa.ID_GRUPPO+".CORRELAZIONE_APPLICATIVA_SCELTE_MULTIPLE_CASO_5"},
			dataProvider="SincronoCorrelazioneApplicativaScelteMultipleCaso5",dependsOnMethods={"sincronoCorrelazioneApplicativaScelteMultipleCaso5"})
	public void testSincronoCorrelazioneApplicativaScelteMultipleCaso5(String idUnivoco,DatabaseMsgDiagnosticiComponent dataMsgDiag,DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
		try{
			this.collaborazioneSPCoopBase.testSincrono(data, id, CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_INTEGRAZIONE, checkServizioApplicativo,
					null);
			Reporter.log("Numero messaggi arrivati al servizio applicativo: "+data.getVerificatoreTracciaRichiesta().isArrivedCount(id));
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isArrivedCount(id)==1);
			
			if(dataMsgDiag!=null){
				//fruitore
				
				String msgInizioCorrelazione = MSG_INIZIO_CORRELAZIONE.replace("@IDAPPLICATIVO@", "NOME5_"+idUnivoco+"_NOME5");
				msgInizioCorrelazione = msgInizioCorrelazione.replace("@IDEGOV@", id);
				Reporter.log("Check inizio correlazione ["+msgInizioCorrelazione+"] non esiste");
				Assert.assertTrue(dataMsgDiag.isTracedMessaggio(msgInizioCorrelazione)==false);
				
				String msgCorrelazioneAvvenuta = MSG_CORRELAZIONE.replace("@IDAPPLICATIVO@", "NOME5_"+idUnivoco+"_NOME5");
				msgCorrelazioneAvvenuta = msgCorrelazioneAvvenuta.replace("@IDEGOV@", id);
				Reporter.log("Check correlazione ["+msgCorrelazioneAvvenuta+"] non esiste");
				Assert.assertTrue(dataMsgDiag.isTracedMessaggio(msgCorrelazioneAvvenuta)==false);
				
				// check correlazione 
				Reporter.log("Check correlazione ["+"NOME5_"+idUnivoco+"_NOME5"+"]");
				Assert.assertTrue(dataMsgDiag.isTracedCorrelazioneApplicativaIntoMsgDiagnosticiCorrelazione("NOME5_"+idUnivoco+"_NOME5"));
			}
			
			// Check correlazione in tracciamento
			Reporter.log("Check correlazione ["+"NOME5_"+idUnivoco+"_NOME5"+"] in tracciamento nella richiesta");
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedCorrelazioneApplicativaRichiesta(id, this.collaborazioneSPCoopBase.getMittente(), "NOME5_"+idUnivoco+"_NOME5"));
			Reporter.log("Check correlazione ["+"NOME5_"+idUnivoco+"_NOME5"+"] in tracciamento nella risposta");
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedCorrelazioneApplicativaRichiesta(id, this.collaborazioneSPCoopBase.getMittente(), "NOME5_"+idUnivoco+"_NOME5"));
			
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
			if(dataMsgDiag!=null)
				dataMsgDiag.close();
		}
	}
	
	/***
	 * Test Correlazione Applicativa Content Based, input mancanti
	 */
	Repository repositorySincronoCorrelazioneApplicativaScelteMultipleCaso5DatiMancanti=new Repository();
	@Test(groups={IntegrazioneCorrelazioneApplicativa.ID_GRUPPO,IntegrazioneCorrelazioneApplicativa.ID_GRUPPO+".CORRELAZIONE_APPLICATIVA_SCELTE_MULTIPLE_CASO_5"},
			description="Test di tipo sincrono, Viene controllato se i body sono uguali e se gli attachment sono uguali",
			dependsOnMethods={"testSincronoCorrelazioneApplicativaScelteMultipleCaso5"})
	public void sincronoCorrelazioneApplicativaScelteMultipleCaso5DatiMancanti() throws TestSuiteException, IOException, Exception{
	
		Date dataInizioTest = DateManager.getDate();
		
		java.io.FileInputStream fin = null;
		DatabaseComponent dbComponentFruitore = null;
		try{
			fin = new java.io.FileInputStream(new File(Utilities.testSuiteProperties.getSoap11FileName()));

			Message msg=new Message(fin);
			msg.getSOAPPartAsBytes();

			msg.getSOAPBody().addChildElement("testNome5", "test", "http://www.openspcoop.org");
			Reporter.log("Messaggio: "+msg.getSOAPPartAsString());
			msg.getSOAPPartAsBytes();
			
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositorySincronoCorrelazioneApplicativaScelteMultipleCaso5DatiMancanti);
			client.setSoapAction("\"TEST\"");
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_PROFILO_SINCRONO_INTEGRAZIONE_CORRELAZIONE_APPLICATIVA_LISTA_CORRELAZIONI);
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
				throw new Exception("Errore atteso [OPENSPCOOP_ORG_416] non si e' verificato...");
			} catch (AxisFault error) {
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				Reporter.log("Controllo fault code [OPENSPCOOP_ORG_416]");
				Assert.assertTrue(Utilities.toString(CodiceErroreIntegrazione.CODICE_416_CORRELAZIONE_APPLICATIVA_RICHIESTA_ERRORE).equals(error.getFaultCode().getLocalPart()));
				String msgErrore = "identificativo di correlazione applicativa non identificato nell'elemento [testNome5] con modalita' di acquisizione contentBased (Pattern:concat_openspcoop(\"NOME5_\",//test:idApplicativo/text(),\"_NOME5\")): nessun match trovato per l'espressione xpath contenuta in concat_openspcoop (//test:idApplicativo/text())";
				Reporter.log("Controllo fault string ["+msgErrore+"]");
				Assert.assertTrue(error.getFaultString().contains(msgErrore));
				Reporter.log("Controllo fault actor [OpenSPCoop]");
				Assert.assertTrue(CostantiPdD.OPENSPCOOP2.equals(error.getFaultActor()));
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
		
		Date dataFineTest = DateManager.getDate();
		ErroreAttesoOpenSPCoopLogCore err = new ErroreAttesoOpenSPCoopLogCore();
		err.setIntervalloInferiore(dataInizioTest);
		err.setIntervalloSuperiore(dataFineTest);
		err.setMsgErrore("identificativo di correlazione applicativa non identificato nell'elemento [testNome5] con modalita' di acquisizione contentBased (Pattern:concat_openspcoop(\"NOME5_\",//test:idApplicativo/text(),\"_NOME5\")): nessun match trovato per l'espressione xpath contenuta in concat_openspcoop (//test:idApplicativo/text())");
		this.erroriAttesiOpenSPCoopCore.add(err);
	}
	@DataProvider (name="SincronoCorrelazioneApplicativaScelteMultipleCaso5DatiMancanti")
	public Object[][]testSincronoCorrelazioneApplicativaScelteMultipleCaso5DatiMancanti()throws Exception{
		String id=this.repositorySincronoCorrelazioneApplicativaScelteMultipleCaso5DatiMancanti.getNext();
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),id,false},	
				{DatabaseProperties.getDatabaseComponentErogatore(),id,true}	
		};
	}
	@Test(groups={IntegrazioneCorrelazioneApplicativa.ID_GRUPPO,IntegrazioneCorrelazioneApplicativa.ID_GRUPPO+".CORRELAZIONE_APPLICATIVA_SCELTE_MULTIPLE_CASO_5"},
			dataProvider="SincronoCorrelazioneApplicativaScelteMultipleCaso5DatiMancanti",dependsOnMethods={"sincronoCorrelazioneApplicativaScelteMultipleCaso5DatiMancanti"})
	public void testSincronoCorrelazioneApplicativaScelteMultipleCaso5DatiMancanti(DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
		try{
			Reporter.log("Controllo tracciamento richiesta non effettuato con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id)==false);
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	
	
	
	
	
	
	// 6. <elemento identificazione="contentBased" 
	// 			pattern="concat_openspcoop(&#34;ALL_&#34;,//test:idApplicativo/text(),&#34;_ALL&#34;)" identificazione-fallita="blocca" />
	
	private String runClientCorrelazioneApplicativaScelteMultipleCaso6(String idUnivoco) throws Exception{
		java.io.FileInputStream fin = null;
		DatabaseComponent dbComponentFruitore = null;
		try{
			fin = new java.io.FileInputStream(new File(Utilities.testSuiteProperties.getSoap11FileName()));

			Message msg=new Message(fin);
			msg.getSOAPPartAsBytes();

			MessageElement elemTest = (MessageElement) msg.getSOAPBody().addChildElement("testNomeALTROXXXXXXXXXXXXX","test","http://www.openspcoop.org");
			MessageElement elem = (MessageElement) elemTest.addChildElement("idApplicativo", "test", "http://www.openspcoop.org");
			elem.setValue(idUnivoco);
			Reporter.log("Messaggio: "+msg.getSOAPPartAsString());
			msg.getSOAPPartAsBytes();
			
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaScelteMultipleCaso6);
			client.setSoapAction("\"TEST\"");
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_PROFILO_SINCRONO_INTEGRAZIONE_CORRELAZIONE_APPLICATIVA_LISTA_CORRELAZIONI);
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
			} catch (AxisFault error) {
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				throw error;
			}finally{
				dbComponentFruitore.close();
			}
			
			checkHttpRisposta(client.getPropertiesTrasportoRisposta(),CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_INTEGRAZIONE,client.getIdMessaggio());
			
			checkMessaggioRisposta(client.getResponseMessage(),CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_INTEGRAZIONE,client.getIdMessaggio());
			
			return client.getIdMessaggio();
			
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
	
	Repository repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaScelteMultipleCaso6=new Repository();
	Repository repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaScelteMultipleCaso6IDUnivoco=new Repository();
	@Test(groups={IntegrazioneCorrelazioneApplicativa.ID_GRUPPO,IntegrazioneCorrelazioneApplicativa.ID_GRUPPO+".CORRELAZIONE_APPLICATIVA_SCELTE_MULTIPLE_CASO_6"},
			description="Test di tipo sincrono, Viene controllato se i body sono uguali e se gli attachment sono uguali")
	public void sincronoCorrelazioneApplicativaScelteMultipleCaso6() throws TestSuiteException, IOException, Exception{
		
		String idUnivoco = "XXXXXXXIDXXXXXXXX_"+System.currentTimeMillis();
		this.repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaScelteMultipleCaso6IDUnivoco.add(idUnivoco);
		
		String id1 = runClientCorrelazioneApplicativaScelteMultipleCaso6(idUnivoco);
		
		Reporter.log("Sleep 4 secondi...");
		try{
			Thread.sleep(4000);
		}catch(Exception e){}
		
		String id2 = runClientCorrelazioneApplicativaScelteMultipleCaso6(idUnivoco);
		
		Reporter.log("ID 1st invocazione["+id1+"]");
		Reporter.log("ID 2nd invocazione["+id2+"]");
		Assert.assertTrue(id1.equals(id2));
	}
	@DataProvider (name="SincronoCorrelazioneApplicativaScelteMultipleCaso6")
	public Object[][]testSincronoCorrelazioneApplicativaScelteMultipleCaso6()throws Exception{
		String id=this.repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaScelteMultipleCaso6.getNext();
		String idUnivoco=this.repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaScelteMultipleCaso6IDUnivoco.getNext();
		return new Object[][]{
				{idUnivoco,DatabaseProperties.getDatabaseComponentDiagnosticaFruitore(),DatabaseProperties.getDatabaseComponentFruitore(),id,false},	
				{idUnivoco,null,DatabaseProperties.getDatabaseComponentErogatore(),id,false}	
		};
	}
	@Test(groups={IntegrazioneCorrelazioneApplicativa.ID_GRUPPO,IntegrazioneCorrelazioneApplicativa.ID_GRUPPO+".CORRELAZIONE_APPLICATIVA_SCELTE_MULTIPLE_CASO_6"},
			dataProvider="SincronoCorrelazioneApplicativaScelteMultipleCaso6",dependsOnMethods={"sincronoCorrelazioneApplicativaScelteMultipleCaso6"})
	public void testSincronoCorrelazioneApplicativaScelteMultipleCaso6(String idUnivoco,DatabaseMsgDiagnosticiComponent dataMsgDiag,DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
		try{
			this.collaborazioneSPCoopBase.testSincrono(data, id, CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_INTEGRAZIONE, checkServizioApplicativo,
					null);
			Reporter.log("Numero messaggi arrivati al servizio applicativo: "+data.getVerificatoreTracciaRichiesta().isArrivedCount(id));
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isArrivedCount(id)==2);
			
			if(dataMsgDiag!=null){
				//fruitore
				
				String msgInizioCorrelazione = MSG_INIZIO_CORRELAZIONE.replace("@IDAPPLICATIVO@", "ALL_"+idUnivoco+"_ALL");
				msgInizioCorrelazione = msgInizioCorrelazione.replace("@IDEGOV@", id);
				Reporter.log("Check inizio correlazione ["+msgInizioCorrelazione+"]");
				Assert.assertTrue(dataMsgDiag.isTracedMessaggio(msgInizioCorrelazione));
				
				String msgCorrelazioneAvvenuta = MSG_CORRELAZIONE.replace("@IDAPPLICATIVO@", "ALL_"+idUnivoco+"_ALL");
				msgCorrelazioneAvvenuta = msgCorrelazioneAvvenuta.replace("@IDEGOV@", id);
				Reporter.log("Check correlazione ["+msgCorrelazioneAvvenuta+"]");
				Assert.assertTrue(dataMsgDiag.isTracedMessaggio(msgCorrelazioneAvvenuta));
				
				// check correlazione 
				Reporter.log("Check correlazione ["+"ALL_"+idUnivoco+"_ALL"+"]");
				Assert.assertTrue(dataMsgDiag.isTracedCorrelazioneApplicativaIntoMsgDiagnosticiCorrelazione("ALL_"+idUnivoco+"_ALL"));
			}
			
			// Check correlazione in tracciamento
			Reporter.log("Check correlazione ["+"ALL_"+idUnivoco+"_ALL"+"] in tracciamento nella richiesta");
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedCorrelazioneApplicativaRichiesta(id, this.collaborazioneSPCoopBase.getMittente(), "ALL_"+idUnivoco+"_ALL"));
			Reporter.log("Check correlazione ["+"ALL_"+idUnivoco+"_ALL"+"] in tracciamento nella risposta");
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedCorrelazioneApplicativaRichiesta(id, this.collaborazioneSPCoopBase.getMittente(), "ALL_"+idUnivoco+"_ALL"));
			
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
			if(dataMsgDiag!=null)
				dataMsgDiag.close();
		}
	}
	
	/***
	 * Test Correlazione Applicativa Content Based, input mancanti
	 */
	Repository repositorySincronoCorrelazioneApplicativaScelteMultipleCaso6DatiMancanti=new Repository();
	@Test(groups={IntegrazioneCorrelazioneApplicativa.ID_GRUPPO,IntegrazioneCorrelazioneApplicativa.ID_GRUPPO+".CORRELAZIONE_APPLICATIVA_SCELTE_MULTIPLE_CASO_6"},
			description="Test di tipo sincrono, Viene controllato se i body sono uguali e se gli attachment sono uguali",
			dependsOnMethods={"testSincronoCorrelazioneApplicativaScelteMultipleCaso6"})
	public void sincronoCorrelazioneApplicativaScelteMultipleCaso6DatiMancanti() throws TestSuiteException, IOException, Exception{
		
		Date dataInizioTest = DateManager.getDate();
		
		java.io.FileInputStream fin = null;
		DatabaseComponent dbComponentFruitore = null;
		try{
			fin = new java.io.FileInputStream(new File(Utilities.testSuiteProperties.getSoap11FileName()));
			
			Message msg=new Message(fin);
			msg.getSOAPPartAsBytes();

			msg.getSOAPBody().addChildElement("testNomeALTROXXXXXXXXXXXXX","test","http://www.openspcoop.org");
			Reporter.log("Messaggio: "+msg.getSOAPPartAsString());
			msg.getSOAPPartAsBytes();
			
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositorySincronoCorrelazioneApplicativaScelteMultipleCaso6DatiMancanti);
			client.setSoapAction("\"TEST\"");
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_PROFILO_SINCRONO_INTEGRAZIONE_CORRELAZIONE_APPLICATIVA_LISTA_CORRELAZIONI);
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
				throw new Exception("Errore atteso [OPENSPCOOP_ORG_416] non si e' verificato...");
			} catch (AxisFault error) {
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				Reporter.log("Controllo fault code [OPENSPCOOP_ORG_416]");
				Assert.assertTrue(Utilities.toString(CodiceErroreIntegrazione.CODICE_416_CORRELAZIONE_APPLICATIVA_RICHIESTA_ERRORE).equals(error.getFaultCode().getLocalPart()));
				String msgErrore = "identificativo di correlazione applicativa non identificato nell'elemento [*] con modalita' di acquisizione contentBased (Pattern:concat_openspcoop(\"ALL_\",//test:idApplicativo/text(),\"_ALL\")): nessun match trovato per l'espressione xpath contenuta in concat_openspcoop (//test:idApplicativo/text())";
				Reporter.log("Controllo fault string ["+msgErrore+"]");
				Assert.assertTrue(error.getFaultString().contains(msgErrore));
				Reporter.log("Controllo fault actor [OpenSPCoop]");
				Assert.assertTrue(CostantiPdD.OPENSPCOOP2.equals(error.getFaultActor()));
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
		
		Date dataFineTest = DateManager.getDate();
		ErroreAttesoOpenSPCoopLogCore err = new ErroreAttesoOpenSPCoopLogCore();
		err.setIntervalloInferiore(dataInizioTest);
		err.setIntervalloSuperiore(dataFineTest);
		err.setMsgErrore("identificativo di correlazione applicativa non identificato nell'elemento [*] con modalita' di acquisizione contentBased (Pattern:concat_openspcoop(\"ALL_\",//test:idApplicativo/text(),\"_ALL\")): nessun match trovato per l'espressione xpath contenuta in concat_openspcoop (//test:idApplicativo/text())");
		this.erroriAttesiOpenSPCoopCore.add(err);
	}
	@DataProvider (name="SincronoCorrelazioneApplicativaScelteMultipleCaso6DatiMancanti")
	public Object[][]testSincronoCorrelazioneApplicativaScelteMultipleCaso6DatiMancanti()throws Exception{
		String id=this.repositorySincronoCorrelazioneApplicativaScelteMultipleCaso6DatiMancanti.getNext();
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),id,false},	
				{DatabaseProperties.getDatabaseComponentErogatore(),id,true}	
		};
	}
	@Test(groups={IntegrazioneCorrelazioneApplicativa.ID_GRUPPO,IntegrazioneCorrelazioneApplicativa.ID_GRUPPO+".CORRELAZIONE_APPLICATIVA_SCELTE_MULTIPLE_CASO_6"},
			dataProvider="SincronoCorrelazioneApplicativaScelteMultipleCaso6DatiMancanti",dependsOnMethods={"sincronoCorrelazioneApplicativaScelteMultipleCaso6DatiMancanti"})
	public void testSincronoCorrelazioneApplicativaScelteMultipleCaso6DatiMancanti(DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
		try{
			Reporter.log("Controllo tracciamento richiesta non effettuato con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id)==false);
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

	
	/***
	 * Test Correlazione Applicativa Content Based anche lato PortaApplicativa
	 */
		
	private String runClientCorrelazioneApplicativaContentBasedLatoPortaApplicativa(String portaDelegata,
			String nomeElementoPD,String idUnivocoPD,String nomeElementoPA,String idUnivocoPA, Repository repository) throws Exception{
		java.io.FileInputStream fin = null;
		DatabaseComponent dbComponentFruitore = null;
		try{
			fin = new java.io.FileInputStream(new File(Utilities.testSuiteProperties.getSoap11FileName()));

			Message msg=new Message(fin);
			msg.getSOAPPartAsBytes();

			MessageElement elem = (MessageElement) msg.getSOAPBody().addChildElement("testRadice", "test", "http://www.openspcoop.org");
			
			MessageElement elemPD = (MessageElement) elem.addChildElement(nomeElementoPD, "test", "http://www.openspcoop.org");
			elemPD.setValue(idUnivocoPD);
			MessageElement elemPA = (MessageElement) elem.addChildElement(nomeElementoPA, "test", "http://www.openspcoop.org");
			elemPA.setValue(idUnivocoPA);
			Reporter.log("Messaggio: "+msg.getSOAPPartAsString());
			msg.getSOAPPartAsBytes();
			
			ClientHttpGenerico client=new ClientHttpGenerico(repository);
			client.setSoapAction("\"TEST\"");
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
			} catch (AxisFault error) {
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				throw error;
			}finally{
				dbComponentFruitore.close();
			}
			
			checkHttpRisposta(client.getPropertiesTrasportoRisposta(),CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_INTEGRAZIONE_LATO_PA,client.getIdMessaggio());
						
			checkMessaggioRisposta(client.getResponseMessage(),CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_INTEGRAZIONE_LATO_PA,client.getIdMessaggio());
			
			return client.getIdMessaggio();
			
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
	
	Repository repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaContentBasedLatoPortaApplicativa=new Repository();
	Repository repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaContentBasedLatoPortaApplicativaIDUnivocoPD=new Repository();
	Repository repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaContentBasedLatoPortaApplicativaIDUnivocoPA=new Repository();
	@Test(groups={IntegrazioneCorrelazioneApplicativa.ID_GRUPPO,IntegrazioneCorrelazioneApplicativa.ID_GRUPPO+".CORRELAZIONE_APPLICATIVA_CONTENT_BASED_PA"},
			description="Test di tipo sincrono, Viene controllato se i body sono uguali e se gli attachment sono uguali")
	public void sincronoCorrelazioneApplicativaContentBasedLatoPortaApplicativa() throws TestSuiteException, IOException, Exception{
		
		String idUnivocoPD = "XXXXXXXIDPORTADELEGATAXXXXXXXX_"+System.currentTimeMillis();
		this.repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaContentBasedLatoPortaApplicativaIDUnivocoPD.add(idUnivocoPD);
		
		String idUnivocoPA = "XXXXXXXIDPORTAAPPLICATIVAXXXXXXXX_"+System.currentTimeMillis();
		this.repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaContentBasedLatoPortaApplicativaIDUnivocoPA.add(idUnivocoPA);
		
		String id1 = runClientCorrelazioneApplicativaContentBasedLatoPortaApplicativa(CostantiTestSuite.PORTA_DELEGATA_PROFILO_SINCRONO_INTEGRAZIONE_CORRELAZIONE_APPLICATIVA_CONTENT_BASED_LATO_PORTA_APPLICATIVA,
				"idApplicativo",idUnivocoPD,"idApplicativoLatoPA",idUnivocoPA,
				this.repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaContentBasedLatoPortaApplicativa);
		
		Reporter.log("Sleep 4 secondi...");
		try{
			Thread.sleep(4000);
		}catch(Exception e){}
		
		String id2 = runClientCorrelazioneApplicativaContentBasedLatoPortaApplicativa(CostantiTestSuite.PORTA_DELEGATA_PROFILO_SINCRONO_INTEGRAZIONE_CORRELAZIONE_APPLICATIVA_CONTENT_BASED_LATO_PORTA_APPLICATIVA,
				"idApplicativo",idUnivocoPD,"idApplicativoLatoPA",idUnivocoPA,
				this.repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaContentBasedLatoPortaApplicativa);
		
		Reporter.log("ID 1st invocazione["+id1+"]");
		Reporter.log("ID 2nd invocazione["+id2+"]");
		Assert.assertTrue(id1.equals(id2));
	}
	@DataProvider (name="SincronoCorrelazioneApplicativaContentBasedLatoPortaApplicativa")
	public Object[][]testSincronoCorrelazioneApplicativaContentBasedLatoPortaApplicativa()throws Exception{
		String id=this.repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaContentBasedLatoPortaApplicativa.getNext();
		String idUnivocoPD=this.repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaContentBasedLatoPortaApplicativaIDUnivocoPD.getNext();
		String idUnivocoPA=this.repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaContentBasedLatoPortaApplicativaIDUnivocoPA.getNext();
		return new Object[][]{
				{idUnivocoPD,idUnivocoPA,DatabaseProperties.getDatabaseComponentDiagnosticaFruitore(),DatabaseProperties.getDatabaseComponentFruitore(),id,false},	
				{idUnivocoPD,idUnivocoPA,null,DatabaseProperties.getDatabaseComponentErogatore(),id,false}	
		};
	}
	@Test(groups={IntegrazioneCorrelazioneApplicativa.ID_GRUPPO,IntegrazioneCorrelazioneApplicativa.ID_GRUPPO+".CORRELAZIONE_APPLICATIVA_CONTENT_BASED_PA"},
			dataProvider="SincronoCorrelazioneApplicativaContentBasedLatoPortaApplicativa",dependsOnMethods={"sincronoCorrelazioneApplicativaContentBasedLatoPortaApplicativa"})
	public void testSincronoCorrelazioneApplicativaContentBased(String idUnivocoPD,String idUnivocoPA,DatabaseMsgDiagnosticiComponent dataMsgDiag,DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
		
		DatabaseMsgDiagnosticiComponent dataMsgDiagErogatore = null;
		try{
			this.collaborazioneSPCoopBase.testSincrono(data, id, CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_INTEGRAZIONE_LATO_PA, checkServizioApplicativo,
					null);
			Reporter.log("Numero messaggi arrivati al servizio applicativo: "+data.getVerificatoreTracciaRichiesta().isArrivedCount(id));
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isArrivedCount(id)==2);
			
			if(dataMsgDiag!=null){
				//fruitore
				
				// Check correlazione  msg diagnostici id richiesta
				
				String msgInizioCorrelazione = MSG_INIZIO_CORRELAZIONE.replace("@IDAPPLICATIVO@", idUnivocoPD);
				msgInizioCorrelazione = msgInizioCorrelazione.replace("@IDEGOV@", id);
				Reporter.log("Check inizio correlazione ["+msgInizioCorrelazione+"]");
				Assert.assertTrue(dataMsgDiag.isTracedMessaggio(msgInizioCorrelazione));
				
				String msgCorrelazioneAvvenuta = MSG_CORRELAZIONE.replace("@IDAPPLICATIVO@", idUnivocoPD);
				msgCorrelazioneAvvenuta = msgCorrelazioneAvvenuta.replace("@IDEGOV@", id);
				Reporter.log("Check correlazione ["+msgCorrelazioneAvvenuta+"]");
				Assert.assertTrue(dataMsgDiag.isTracedMessaggio(msgCorrelazioneAvvenuta));
				
				
				// check correlazione PD
				Reporter.log("Check correlazione PD ["+idUnivocoPD+"]");
				Assert.assertTrue(dataMsgDiag.isTracedCorrelazioneApplicativaIntoMsgDiagnosticiCorrelazione(idUnivocoPD));
				
			}else{
				dataMsgDiagErogatore = DatabaseProperties.getDatabaseComponentDiagnosticaErogatore();
				
				// check correlazione PA
				Reporter.log("Check correlazione PA ["+idUnivocoPA+"]");
				Assert.assertTrue(dataMsgDiagErogatore.isTracedCorrelazioneApplicativaIntoMsgDiagnosticiCorrelazione(idUnivocoPA));
			}
			
			// Check correlazione in tracciamento
			Reporter.log("Check correlazione ["+idUnivocoPD+"] in tracciamento nella richiesta (PD)");
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedCorrelazioneApplicativaRichiesta(id, this.collaborazioneSPCoopBase.getMittente(), idUnivocoPD));
			Reporter.log("Check correlazione ["+idUnivocoPD+"] in tracciamento nella risposta (PD)");
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedCorrelazioneApplicativaRichiesta(id, this.collaborazioneSPCoopBase.getMittente(), idUnivocoPD));
			
			// Check correlazione in tracciamento
			Reporter.log("Check correlazione ["+idUnivocoPA+"] in tracciamento nella richiesta (PA)");
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedCorrelazioneApplicativaRichiesta(id, this.collaborazioneSPCoopBase.getDestinatario(),idUnivocoPA));
			Reporter.log("Check correlazione ["+idUnivocoPA+"] in tracciamento nella risposta (PA)");
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedCorrelazioneApplicativaRichiesta(id, this.collaborazioneSPCoopBase.getDestinatario(), idUnivocoPA));
			
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
			if(dataMsgDiag!=null)
				dataMsgDiag.close();
			if(dataMsgDiagErogatore!=null)
				dataMsgDiagErogatore.close();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/***
	 * Test Correlazione Applicativa Content Based, input mancanti lato PortaApplicativa
	 */
	Repository repositorySincronoCorrelazioneApplicativaContentBasedDatiMancantiLatoPortaApplicativa=new Repository();
	@Test(groups={IntegrazioneCorrelazioneApplicativa.ID_GRUPPO,IntegrazioneCorrelazioneApplicativa.ID_GRUPPO+".CORRELAZIONE_APPLICATIVA_CONTENT_BASED_DATI_MANCANTI_PA"},
			description="Test di tipo sincrono, Viene controllato se i body sono uguali e se gli attachment sono uguali")
	public void sincronoCorrelazioneApplicativaContentBasedDatiMancantiLatoPortaApplicativa() throws TestSuiteException, IOException, Exception{
		
		Date dataInizioTest = DateManager.getDate();
		
		java.io.FileInputStream fin = null;
		DatabaseComponent dbComponentFruitore = null;
		try{
			fin = new java.io.FileInputStream(new File(Utilities.testSuiteProperties.getSoap11FileName()));

			Message msg=new Message(fin);
			msg.getSOAPPartAsBytes();

			String idUnivocoPD = "XXXXXXXIDPORTADELEGATAXXXXXXXX_"+System.currentTimeMillis();
					
			MessageElement elem = (MessageElement) msg.getSOAPBody().addChildElement("testRadice", "test", "http://www.openspcoop.org");
			
			MessageElement elemPD = (MessageElement) elem.addChildElement("idApplicativo", "test", "http://www.openspcoop.org");
			elemPD.setValue(idUnivocoPD);
			MessageElement elemPA = (MessageElement) elem.addChildElement("idApplicativoLatoPAERRATO", "test", "http://www.openspcoop.org");
			elemPA.setValue("ERRATOILNOME");
			Reporter.log("Messaggio: "+msg.getSOAPPartAsString());
			msg.getSOAPPartAsBytes();
			
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositorySincronoCorrelazioneApplicativaContentBasedDatiMancantiLatoPortaApplicativa);
			client.setSoapAction("\"TEST\"");
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_PROFILO_SINCRONO_INTEGRAZIONE_CORRELAZIONE_APPLICATIVA_CONTENT_BASED_LATO_PORTA_APPLICATIVA);
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
				throw new Exception("Errore atteso [] non si e' verificato...");
			} catch (AxisFault error) {
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				Reporter.log("Controllo fault code [EGOV_IT_300]");
				Assert.assertTrue("EGOV_IT_300".equals(error.getFaultCode().getLocalPart()));
				Reporter.log("Controllo fault actor [OpenSPCoop]");
				Assert.assertTrue(CostantiPdD.OPENSPCOOP2.equals(error.getFaultActor()));
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
		
		Date dataFineTest = DateManager.getDate();
		
		ErroreAttesoOpenSPCoopLogCore err = new ErroreAttesoOpenSPCoopLogCore();
		err.setIntervalloInferiore(dataInizioTest);
		err.setIntervalloSuperiore(dataFineTest);
		err.setMsgErrore("Riscontrato errore durante la correlazione applicativa");
		this.erroriAttesiOpenSPCoopCore.add(err);
		
		ErroreAttesoOpenSPCoopLogCore err2 = new ErroreAttesoOpenSPCoopLogCore();
		err2.setIntervalloInferiore(dataInizioTest);
		err2.setIntervalloSuperiore(dataFineTest);
		err2.setMsgErrore("identificativo di correlazione applicativa non identificato nell'elemento [*] con modalita' di acquisizione contentBased (Pattern://test:idApplicativoLatoPA/text()): nessun match trovato per l'espressione xpath [//test:idApplicativoLatoPA/text()]");
		this.erroriAttesiOpenSPCoopCore.add(err2);
	}
	@DataProvider (name="SincronoCorrelazioneApplicativaContentBasedDatiMancantiLatoPortaApplicativa")
	public Object[][]testSincronoCorrelazioneApplicativaContentBasedDatiMancantiLatoPortaApplicativa()throws Exception{
		String id=this.repositorySincronoCorrelazioneApplicativaContentBasedDatiMancantiLatoPortaApplicativa.getNext();
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),id,false},	
				{DatabaseProperties.getDatabaseComponentErogatore(),id,true}	
		};
	}
	@Test(groups={IntegrazioneCorrelazioneApplicativa.ID_GRUPPO,IntegrazioneCorrelazioneApplicativa.ID_GRUPPO+".CORRELAZIONE_APPLICATIVA_CONTENT_BASED_DATI_MANCANTI_PA"},
			dataProvider="SincronoCorrelazioneApplicativaContentBasedDatiMancantiLatoPortaApplicativa",
			dependsOnMethods={"sincronoCorrelazioneApplicativaContentBasedDatiMancantiLatoPortaApplicativa"})
	public void testSincronoCorrelazioneApplicativaContentBasedDatiMancantiLatoPortaApplicativa(DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
		try{
			Reporter.log("Controllo tracciamento richiesta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
			Reporter.log("Controllo valore Mittente Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id,CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE, null));
			Reporter.log("Controllo valore Destinatario Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedDestinatario(id,CostantiTestSuite.SPCOOP_SOGGETTO_EROGATORE, null));
			Reporter.log("Controllo valore Servizio Busta con id: " +id);
			DatiServizio datiServizio = new DatiServizio(CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO, CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO, CostantiTestSuite.SPCOOP_VERSIONE_SERVIZIO_DEFAULT);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedServizio(id, datiServizio));
			Reporter.log("Controllo valore Azione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedAzione(id, CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_INTEGRAZIONE_LATO_PA));
			Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloDiCollaborazione(id, SPCoopCostanti.PROFILO_COLLABORAZIONE_SINCRONO, ProfiloDiCollaborazione.SINCRONO));
			Reporter.log("Numero messaggi arrivati al servizio applicativo: "+data.getVerificatoreTracciaRichiesta().isArrivedCount(id));
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isArrivedCount(id)==0);
			Reporter.log("Controllo che la busta abbia non generato eccezioni, id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().existsListaEccezioni(id)==false);
			//Reporter.log("Controllo che la busta abbia generato l'eccezione " + Costanti.ECCEZIONE_PROCESSAMENTO_MESSAGGIO + " rappresentante una Busta e-Gov scaduta");
			//Assert.assertTrue(data.isTracedEccezione(id, Costanti.ECCEZIONE_PROCESSAMENTO_MESSAGGIO));
			Reporter.log("Controllo lista trasmissione con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedTrasmissione(id, CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE, null, 
					CostantiTestSuite.SPCOOP_SOGGETTO_EROGATORE, null));
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/***
	 * Test Correlazione Applicativa Content Based
	 */
		
	private String runClientCorrelazioneApplicativaContentBased_erroreAccettatoLatoPortaApplicativa(String idUnivoco) throws Exception{
		java.io.FileInputStream fin = null;
		DatabaseComponent dbComponentFruitore = null;
		try{
			fin = new java.io.FileInputStream(new File(Utilities.testSuiteProperties.getSoap11FileName()));

			Message msg=Axis14SoapUtils.build_Soap_Empty();
			
			MessageElement elem = (MessageElement) msg.getSOAPBody().addChildElement("testRadice", "test", "http://www.openspcoop.org");
			
			MessageElement elemPD = (MessageElement) elem.addChildElement("idApplicativo", "test", "http://www.openspcoop.org");
			elemPD.setValue(idUnivoco);
			MessageElement elemPA = (MessageElement) elem.addChildElement("accettaSenzaIdentificazione", "test", "http://www.openspcoop.org");
			elemPA.setValue("ERRATOILNOME");
			Reporter.log("Messaggio: "+msg.getSOAPPartAsString());
			msg.getSOAPPartAsBytes();
			
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaContentBased_erroreAccettatoLatoPortaApplicativa);
			client.setSoapAction("\"TEST\"");
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_PROFILO_SINCRONO_INTEGRAZIONE_CORRELAZIONE_APPLICATIVA_CONTENT_BASED_LATO_PORTA_APPLICATIVA);
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
			} catch (AxisFault error) {
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				throw error;
			}finally{
				dbComponentFruitore.close();
			}
			
			checkHttpRisposta(client.getPropertiesTrasportoRisposta(),CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_INTEGRAZIONE_LATO_PA,client.getIdMessaggio());
			
			checkMessaggioRisposta(client.getResponseMessage(),CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_INTEGRAZIONE_LATO_PA,client.getIdMessaggio());
			
			return client.getIdMessaggio();
			
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
	
	Repository repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaContentBased_erroreAccettatoLatoPortaApplicativa=new Repository();
	Repository repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaContentBasedIDUnivoco_erroreAccettatoLatoPortaApplicativa=new Repository();
	@Test(groups={IntegrazioneCorrelazioneApplicativa.ID_GRUPPO,IntegrazioneCorrelazioneApplicativa.ID_GRUPPO+".CORRELAZIONE_APPLICATIVA_CONTENT_BASED_ACCETTA_IDENTIFICAZIONE_NON_RIUSCITA_PA"},
			description="Test di tipo sincrono, Viene controllato se i body sono uguali e se gli attachment sono uguali")
	public void sincronoCorrelazioneApplicativaContentBased_erroreAccettatoLatoPortaApplicativa() throws TestSuiteException, IOException, Exception{
		
		String idUnivoco = "XXXXXXXIDXXXXXXXX_"+System.currentTimeMillis();
		this.repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaContentBasedIDUnivoco_erroreAccettatoLatoPortaApplicativa.add(idUnivoco);
		
		String id1 = runClientCorrelazioneApplicativaContentBased_erroreAccettatoLatoPortaApplicativa(idUnivoco);
		
		Reporter.log("Sleep 4 secondi...");
		try{
			Thread.sleep(4000);
		}catch(Exception e){}
		
		String id2 = runClientCorrelazioneApplicativaContentBased_erroreAccettatoLatoPortaApplicativa(idUnivoco);
		
		Reporter.log("ID 1st invocazione["+id1+"]");
		Reporter.log("ID 2nd invocazione["+id2+"]");
		Assert.assertTrue(id1.equals(id2));
	}
	@DataProvider (name="SincronoCorrelazioneApplicativaContentBased_erroreAccettatoLatoPortaApplicativa")
	public Object[][]testSincronoCorrelazioneApplicativaContentBased_erroreAccettatoLatoPortaApplicativa()throws Exception{
		String id=this.repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaContentBased_erroreAccettatoLatoPortaApplicativa.getNext();
		String idUnivoco = this.repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaContentBasedIDUnivoco_erroreAccettatoLatoPortaApplicativa.getNext();
		return new Object[][]{
				{idUnivoco,DatabaseProperties.getDatabaseComponentDiagnosticaFruitore(),DatabaseProperties.getDatabaseComponentFruitore(),id,false,true},	
				{idUnivoco,null,DatabaseProperties.getDatabaseComponentErogatore(),id,false,false}	
		};
	}
	@Test(groups={IntegrazioneCorrelazioneApplicativa.ID_GRUPPO,IntegrazioneCorrelazioneApplicativa.ID_GRUPPO+".CORRELAZIONE_APPLICATIVA_CONTENT_BASED_ACCETTA_IDENTIFICAZIONE_NON_RIUSCITA_PA"},
			dataProvider="SincronoCorrelazioneApplicativaContentBased_erroreAccettatoLatoPortaApplicativa",
			dependsOnMethods={"sincronoCorrelazioneApplicativaContentBased_erroreAccettatoLatoPortaApplicativa"})
	public void testSincronoCorrelazioneApplicativaContentBased_erroreAccettatoLatoPortaApplicativa(String idUnivoco,DatabaseMsgDiagnosticiComponent dataMsgDiag,DatabaseComponent data,String id,boolean checkServizioApplicativo,boolean fruitore) throws Exception{
		try{
			this.collaborazioneSPCoopBase.testSincrono(data, id, CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_INTEGRAZIONE_LATO_PA, checkServizioApplicativo,
					null);
			
			if(!fruitore){
				Reporter.log("Numero messaggi arrivati al servizio applicativo: "+data.getVerificatoreTracciaRichiesta().isArrivedCount(id));
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isArrivedCount(id)==2);
			}
			
			if(fruitore){
				String msgInizioCorrelazione = MSG_INIZIO_CORRELAZIONE.replace("@IDEGOV@", id);
				String [] msgRicerca = msgInizioCorrelazione.split("@IDAPPLICATIVO@");
				Reporter.log("Check inizio correlazione ["+msgInizioCorrelazione+"]");
				Assert.assertTrue(dataMsgDiag.isTracedMessaggio(msgRicerca));
				
				String msgCorrelazioneAvvenuta = MSG_CORRELAZIONE.replace("@IDEGOV@", id);
				msgRicerca = msgCorrelazioneAvvenuta.split("@IDAPPLICATIVO@");
				Reporter.log("Check inizio correlazione ["+msgCorrelazioneAvvenuta+"]");
				Assert.assertTrue(dataMsgDiag.isTracedMessaggio(msgRicerca));

				// check correlazione
				Reporter.log("Check correlazione ["+idUnivoco+"]");
				Assert.assertTrue(dataMsgDiag.isTracedCorrelazioneApplicativaIntoMsgDiagnosticiCorrelazione(idUnivoco));
			}
			
			// Check correlazione in tracciamento
			if(fruitore){
				Reporter.log("Check correlazione ["+idUnivoco+"] in tracciamento nella richiesta");
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedCorrelazioneApplicativaRichiesta(id, this.collaborazioneSPCoopBase.getMittente(), idUnivoco));
				Reporter.log("Check correlazione ["+idUnivoco+"] in tracciamento nella risposta");
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedCorrelazioneApplicativaRichiesta(id, this.collaborazioneSPCoopBase.getMittente(), idUnivoco));
			}
					
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
			if(dataMsgDiag!=null)
				dataMsgDiag.close();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/* ********************* CORRELAZIONE APPLICATIVA RISPOSTA *************************** */
	
	
	
	
	/***
	 * Test Correlazione Applicativa Risposta Content Based con header SOAP e gestione stateless
	 */
		
	private String runClientCorrelazioneApplicativaContentBasedRisposta_SOAP_STATELESS(String portaDelegata,String idUnivoco, Repository repository) throws Exception{
		java.io.FileInputStream fin = null;
		DatabaseComponent dbComponentFruitore = null;
		try{
			fin = new java.io.FileInputStream(new File(Utilities.testSuiteProperties.getSoap11FileName()));

			Message msg=new Message(fin);
			msg.getSOAPPartAsBytes();

			MessageElement elem = (MessageElement) msg.getSOAPBody().addChildElement("idApplicativo", "test", "http://www.openspcoop.org");
			elem.setValue(idUnivoco);
			Reporter.log("Messaggio: "+msg.getSOAPPartAsString());
			msg.getSOAPPartAsBytes();
			
			ClientHttpGenerico client=new ClientHttpGenerico(repository);
			client.setSoapAction("\"TEST\"");
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
			} catch (AxisFault error) {
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				throw error;
			}finally{
				dbComponentFruitore.close();
			}
			
			checkHttpRisposta(client.getPropertiesTrasportoRisposta(),CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_TEST_CORRELAZIONE_APPLICATIVA_RISPOSTA_CONTENT_BASED_SOAP_STATELESS,client.getIdMessaggio());
			
			checkMessaggioRisposta(client.getResponseMessage(),CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_TEST_CORRELAZIONE_APPLICATIVA_RISPOSTA_CONTENT_BASED_SOAP_STATELESS,client.getIdMessaggio());
			
			return client.getIdMessaggio();
			
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
	
	Repository repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaContentBasedRisposta_SOAP_STATELESS=new Repository();
	Repository repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaContentBasedRisposta_SOAP_STATELESSIDUnivoco=new Repository();
	@Test(groups={IntegrazioneCorrelazioneApplicativa.ID_GRUPPO,IntegrazioneCorrelazioneApplicativa.ID_GRUPPO+".CORRELAZIONE_APPLICATIVA_RISPOSTA_CONTENT_BASED_SOAP_STATELESS"},description="Test di tipo sincrono, Viene controllato se i body sono uguali e se gli attachment sono uguali")
	public void sincronoCorrelazioneApplicativaContentBasedRisposta_SOAP_STATELESS() throws TestSuiteException, IOException, Exception{
		
		String idUnivoco = "XXXXXXXIDXXXXXXXX_"+System.currentTimeMillis();
		this.repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaContentBasedRisposta_SOAP_STATELESSIDUnivoco.add(idUnivoco);
		
		String id1 = runClientCorrelazioneApplicativaContentBasedRisposta_SOAP_STATELESS(CostantiTestSuite.PORTA_DELEGATA_PROFILO_SINCRONO_INTEGRAZIONE_CORRELAZIONE_APPLICATIVA_RISPOSTA_CONTENT_BASED_SOAP_STATELESS,
				idUnivoco,
				this.repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaContentBasedRisposta_SOAP_STATELESS);
		
		Reporter.log("ID invocazione["+id1+"]");
	}
	@DataProvider (name="SincronoCorrelazioneApplicativaContentBasedRisposta_SOAP_STATELESS")
	public Object[][]testSincronoCorrelazioneApplicativaContentBasedRisposta_SOAP_STATELESS()throws Exception{
		String id=this.repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaContentBasedRisposta_SOAP_STATELESS.getNext();
		String idUnivoco=this.repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaContentBasedRisposta_SOAP_STATELESSIDUnivoco.getNext();
		try{
			Thread.sleep(3000);
		}catch(Exception e){}
		return new Object[][]{
				{idUnivoco,DatabaseProperties.getDatabaseComponentDiagnosticaFruitore(),DatabaseProperties.getDatabaseComponentFruitore(),id,false},	
				{idUnivoco,DatabaseProperties.getDatabaseComponentDiagnosticaErogatore(),DatabaseProperties.getDatabaseComponentErogatore(),id,false}	
		};
	}
	@Test(groups={IntegrazioneCorrelazioneApplicativa.ID_GRUPPO,IntegrazioneCorrelazioneApplicativa.ID_GRUPPO+".CORRELAZIONE_APPLICATIVA_RISPOSTA_CONTENT_BASED_SOAP_STATELESS"},dataProvider="SincronoCorrelazioneApplicativaContentBasedRisposta_SOAP_STATELESS",dependsOnMethods={"sincronoCorrelazioneApplicativaContentBasedRisposta_SOAP_STATELESS"})
	public void testSincronoCorrelazioneApplicativaContentBasedRisposta_SOAP_STATELESS(String idUnivoco,DatabaseMsgDiagnosticiComponent dataMsgDiag,DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
		try{
			this.collaborazioneSPCoopBase.testSincrono(data, id, CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_TEST_CORRELAZIONE_APPLICATIVA_RISPOSTA_CONTENT_BASED_SOAP_STATELESS, checkServizioApplicativo,
					null);
			Reporter.log("Numero messaggi arrivati al servizio applicativo: "+data.getVerificatoreTracciaRichiesta().isArrivedCount(id));
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isArrivedCount(id)==1);
			
			// check correlazione richiesta
			Reporter.log("Check correlazione ["+idUnivoco+"]");
			Assert.assertTrue(dataMsgDiag.isTracedCorrelazioneApplicativaIntoMsgDiagnosticiCorrelazione(idUnivoco));
			// check correlazione risposta
			Reporter.log("Check correlazione risposta ["+idUnivoco+"]");
			Assert.assertTrue(dataMsgDiag.isTracedCorrelazioneApplicativaRispostaIntoMsgDiagnosticiCorrelazione(idUnivoco));
									
			// Check correlazione richiesta in tutte le tracce
			Reporter.log("Check correlazione ["+idUnivoco+"] in tracciamento nella richiesta");
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedCorrelazioneApplicativaRichiesta(id, this.collaborazioneSPCoopBase.getMittente(), idUnivoco));
			Reporter.log("Check correlazione ["+idUnivoco+"] in tracciamento nella risposta");
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedCorrelazioneApplicativaRichiesta(id, this.collaborazioneSPCoopBase.getMittente(), idUnivoco));
			
			// Check correlazione risposta solo nelle tracce della risposta
			Reporter.log("Check correlazione risposta == null ["+idUnivoco+"] in tracciamento nella richiesta");
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedCorrelazioneApplicativaRisposta(id, this.collaborazioneSPCoopBase.getMittente(), null));
			Reporter.log("Check correlazione risposta ["+idUnivoco+"] in tracciamento nella risposta");
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedCorrelazioneApplicativaRisposta(id, this.collaborazioneSPCoopBase.getMittente(), idUnivoco));
			
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
			if(dataMsgDiag!=null)
				dataMsgDiag.close();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	/***
	 * Test Correlazione Applicativa Risposta Content Based con header SOAP e gestione stateful
	 */
		
	private String runClientCorrelazioneApplicativaContentBasedRisposta_SOAP_STATEFUL(String portaDelegata,String idUnivoco, Repository repository) throws Exception{
		java.io.FileInputStream fin = null;
		DatabaseComponent dbComponentFruitore = null;
		try{
			fin = new java.io.FileInputStream(new File(Utilities.testSuiteProperties.getSoap11FileName()));

			Message msg=new Message(fin);
			msg.getSOAPPartAsBytes();

			MessageElement elem = (MessageElement) msg.getSOAPBody().addChildElement("idApplicativo", "test", "http://www.openspcoop.org");
			elem.setValue(idUnivoco);
			Reporter.log("Messaggio: "+msg.getSOAPPartAsString());
			msg.getSOAPPartAsBytes();
			
			ClientHttpGenerico client=new ClientHttpGenerico(repository);
			client.setSoapAction("\"TEST\"");
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
			} catch (AxisFault error) {
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				throw error;
			}finally{
				dbComponentFruitore.close();
			}
			
			checkHttpRisposta(client.getPropertiesTrasportoRisposta(),CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_TEST_CORRELAZIONE_APPLICATIVA_RISPOSTA_CONTENT_BASED_SOAP_STATEFUL,client.getIdMessaggio());
			
			checkMessaggioRisposta(client.getResponseMessage(),CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_TEST_CORRELAZIONE_APPLICATIVA_RISPOSTA_CONTENT_BASED_SOAP_STATEFUL,client.getIdMessaggio());
			
			return client.getIdMessaggio();
			
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
	
	Repository repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaContentBasedRisposta_SOAP_STATEFUL=new Repository();
	Repository repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaContentBasedRisposta_SOAP_STATEFULIDUnivoco=new Repository();
	@Test(groups={IntegrazioneCorrelazioneApplicativa.ID_GRUPPO,IntegrazioneCorrelazioneApplicativa.ID_GRUPPO+".CORRELAZIONE_APPLICATIVA_RISPOSTA_CONTENT_BASED_SOAP_STATEFUL"},description="Test di tipo sincrono, Viene controllato se i body sono uguali e se gli attachment sono uguali")
	public void sincronoCorrelazioneApplicativaContentBasedRisposta_SOAP_STATEFUL() throws TestSuiteException, IOException, Exception{
		
		String idUnivoco = "XXXXXXXIDXXXXXXXX_"+System.currentTimeMillis();
		this.repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaContentBasedRisposta_SOAP_STATEFULIDUnivoco.add(idUnivoco);
		
		String id1 = runClientCorrelazioneApplicativaContentBasedRisposta_SOAP_STATEFUL(CostantiTestSuite.PORTA_DELEGATA_PROFILO_SINCRONO_INTEGRAZIONE_CORRELAZIONE_APPLICATIVA_RISPOSTA_CONTENT_BASED_SOAP_STATEFUL,
				idUnivoco,
				this.repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaContentBasedRisposta_SOAP_STATEFUL);
		
		Reporter.log("ID invocazione["+id1+"]");
	}
	@DataProvider (name="SincronoCorrelazioneApplicativaContentBasedRisposta_SOAP_STATEFUL")
	public Object[][]testSincronoCorrelazioneApplicativaContentBasedRisposta_SOAP_STATEFUL()throws Exception{
		String id=this.repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaContentBasedRisposta_SOAP_STATEFUL.getNext();
		String idUnivoco=this.repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaContentBasedRisposta_SOAP_STATEFULIDUnivoco.getNext();
		return new Object[][]{
				{idUnivoco,DatabaseProperties.getDatabaseComponentDiagnosticaFruitore(),DatabaseProperties.getDatabaseComponentFruitore(),id,false},	
				{idUnivoco,DatabaseProperties.getDatabaseComponentDiagnosticaErogatore(),DatabaseProperties.getDatabaseComponentErogatore(),id,false}	
		};
	}
	@Test(groups={IntegrazioneCorrelazioneApplicativa.ID_GRUPPO,IntegrazioneCorrelazioneApplicativa.ID_GRUPPO+".CORRELAZIONE_APPLICATIVA_RISPOSTA_CONTENT_BASED_SOAP_STATEFUL"},dataProvider="SincronoCorrelazioneApplicativaContentBasedRisposta_SOAP_STATEFUL",dependsOnMethods={"sincronoCorrelazioneApplicativaContentBasedRisposta_SOAP_STATEFUL"})
	public void testSincronoCorrelazioneApplicativaContentBasedRisposta_SOAP_STATEFUL(String idUnivoco,DatabaseMsgDiagnosticiComponent dataMsgDiag,DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
		try{
			this.collaborazioneSPCoopBase.testSincrono(data, id, CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_TEST_CORRELAZIONE_APPLICATIVA_RISPOSTA_CONTENT_BASED_SOAP_STATEFUL, checkServizioApplicativo,
					null);
			Reporter.log("Numero messaggi arrivati al servizio applicativo: "+data.getVerificatoreTracciaRichiesta().isArrivedCount(id));
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isArrivedCount(id)==1);
			
			// check correlazione richiesta
			Reporter.log("Check correlazione ["+idUnivoco+"]");
			Assert.assertTrue(dataMsgDiag.isTracedCorrelazioneApplicativaIntoMsgDiagnosticiCorrelazione(idUnivoco));
			// check correlazione risposta
			Reporter.log("Check correlazione risposta ["+idUnivoco+"]");
			Assert.assertTrue(dataMsgDiag.isTracedCorrelazioneApplicativaRispostaIntoMsgDiagnosticiCorrelazione(idUnivoco));
									
			// Check correlazione richiesta in tutte le tracce
			Reporter.log("Check correlazione ["+idUnivoco+"] in tracciamento nella richiesta");
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedCorrelazioneApplicativaRichiesta(id, this.collaborazioneSPCoopBase.getMittente(), idUnivoco));
			Reporter.log("Check correlazione ["+idUnivoco+"] in tracciamento nella risposta");
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedCorrelazioneApplicativaRichiesta(id, this.collaborazioneSPCoopBase.getMittente(), idUnivoco));
			
			// Check correlazione risposta solo nelle tracce della risposta
			Reporter.log("Check correlazione risposta == null ["+idUnivoco+"] in tracciamento nella richiesta");
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedCorrelazioneApplicativaRisposta(id, this.collaborazioneSPCoopBase.getMittente(), null));
			Reporter.log("Check correlazione risposta ["+idUnivoco+"] in tracciamento nella risposta");
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedCorrelazioneApplicativaRisposta(id, this.collaborazioneSPCoopBase.getMittente(), idUnivoco));
			
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
			if(dataMsgDiag!=null)
				dataMsgDiag.close();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	/***
	 * Test Correlazione Applicativa Risposta Input Based con header TRASPORTO e gestione stateless
	 */
		
	private String runClientCorrelazioneApplicativaInputBasedRisposta_TRASPORTO_STATELESS(String portaDelegata,String idUnivoco, Repository repository) throws Exception{
		java.io.FileInputStream fin = null;
		DatabaseComponent dbComponentFruitore = null;
		try{
			fin = new java.io.FileInputStream(new File(Utilities.testSuiteProperties.getSoap11FileName()));
			Message msg=new Message(fin);
			msg.getSOAPPartAsBytes();

			ClientHttpGenerico client=new ClientHttpGenerico(repository);
			client.setSoapAction("\"TEST\"");
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(portaDelegata);
			client.connectToSoapEngine();
			TestSuiteProperties testsuiteProperties = TestSuiteProperties.getInstance();
			client.setProperty(testsuiteProperties.getIDApplicativoTrasporto(), idUnivoco);
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
			} catch (AxisFault error) {
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				throw error;
			}finally{
				dbComponentFruitore.close();
			}
			
			checkHttpRisposta(client.getPropertiesTrasportoRisposta(),CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_TEST_CORRELAZIONE_APPLICATIVA_RISPOSTA_INPUT_BASED_TRASPORTO,client.getIdMessaggio());
			
			return client.getIdMessaggio();
			
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
	
	Repository repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaInputBasedRisposta_TRASPORTO_STATELESS=new Repository();
	Repository repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaInputBasedRisposta_TRASPORTO_STATELESSIDUnivoco=new Repository();
	@Test(groups={IntegrazioneCorrelazioneApplicativa.ID_GRUPPO,IntegrazioneCorrelazioneApplicativa.ID_GRUPPO+".CORRELAZIONE_APPLICATIVA_RISPOSTA_INPUT_BASED_TRASPORTO"},description="Test di tipo sincrono, Viene controllato se i body sono uguali e se gli attachment sono uguali")
	public void sincronoCorrelazioneApplicativaInputBasedRisposta_TRASPORTO_STATELESS() throws TestSuiteException, IOException, Exception{
		
		String idUnivoco = "XXXXXXXIDXXXXXXXX_"+System.currentTimeMillis();
		this.repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaInputBasedRisposta_TRASPORTO_STATELESSIDUnivoco.add(idUnivoco);
		
		String id1 = runClientCorrelazioneApplicativaInputBasedRisposta_TRASPORTO_STATELESS(CostantiTestSuite.PORTA_DELEGATA_PROFILO_SINCRONO_INTEGRAZIONE_CORRELAZIONE_APPLICATIVA_RISPOSTA_INPUT_BASED_TRASPORTO,
				idUnivoco,
				this.repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaInputBasedRisposta_TRASPORTO_STATELESS);
		
		Reporter.log("ID invocazione["+id1+"]");
	}
	@DataProvider (name="SincronoCorrelazioneApplicativaInputBasedRisposta_TRASPORTO_STATELESS")
	public Object[][]testSincronoCorrelazioneApplicativaInputBasedRisposta_TRASPORTO_STATELESS()throws Exception{
		String id=this.repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaInputBasedRisposta_TRASPORTO_STATELESS.getNext();
		String idUnivoco=this.repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaInputBasedRisposta_TRASPORTO_STATELESSIDUnivoco.getNext();
		return new Object[][]{
				{idUnivoco,DatabaseProperties.getDatabaseComponentDiagnosticaFruitore(),DatabaseProperties.getDatabaseComponentFruitore(),id,false},	
				{idUnivoco,DatabaseProperties.getDatabaseComponentDiagnosticaErogatore(),DatabaseProperties.getDatabaseComponentErogatore(),id,false}	
		};
	}
	@Test(groups={IntegrazioneCorrelazioneApplicativa.ID_GRUPPO,IntegrazioneCorrelazioneApplicativa.ID_GRUPPO+".CORRELAZIONE_APPLICATIVA_RISPOSTA_INPUT_BASED_TRASPORTO"},dataProvider="SincronoCorrelazioneApplicativaInputBasedRisposta_TRASPORTO_STATELESS",dependsOnMethods={"sincronoCorrelazioneApplicativaInputBasedRisposta_TRASPORTO_STATELESS"})
	public void testSincronoCorrelazioneApplicativaInputBasedRisposta_TRASPORTO_STATELESS(String idUnivoco,DatabaseMsgDiagnosticiComponent dataMsgDiag,DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
		try{
			this.collaborazioneSPCoopBase.testSincrono(data, id, CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_TEST_CORRELAZIONE_APPLICATIVA_RISPOSTA_INPUT_BASED_TRASPORTO, checkServizioApplicativo,
					null);
			Reporter.log("Numero messaggi arrivati al servizio applicativo: "+data.getVerificatoreTracciaRichiesta().isArrivedCount(id));
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isArrivedCount(id)==1);
			
			// check correlazione richiesta
			Reporter.log("Check correlazione ["+idUnivoco+"]");
			Assert.assertTrue(dataMsgDiag.isTracedCorrelazioneApplicativaIntoMsgDiagnosticiCorrelazione(idUnivoco));
			if(checkServizioApplicativo){
				// porta applicativa
				// check correlazione risposta
				boolean v = dataMsgDiag.existsCorrelazioneApplicativaRispostaIntoMsgDiagnosticiCorrelazione(id, false);
				Reporter.log("Check correlazione risposta presente (atteso:true trovato:"+v+")");
				Assert.assertTrue(v);
			}else{
				// porta delegata
				// check correlazione risposta non presente
				boolean v = dataMsgDiag.existsCorrelazioneApplicativaRispostaIntoMsgDiagnosticiCorrelazione(id, true);
				Reporter.log("Check correlazione risposta presente (atteso:false trovato:"+v+")");
				Assert.assertTrue(v==false);
			}
									
			// Check correlazione richiesta in tutte le tracce
			Reporter.log("Check correlazione ["+idUnivoco+"] in tracciamento nella richiesta");
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedCorrelazioneApplicativaRichiesta(id, this.collaborazioneSPCoopBase.getMittente(), idUnivoco));
			Reporter.log("Check correlazione ["+idUnivoco+"] in tracciamento nella risposta");
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedCorrelazioneApplicativaRichiesta(id, this.collaborazioneSPCoopBase.getMittente(), idUnivoco));
			
			// Check correlazione risposta solo nelle tracce della risposta
			Reporter.log("Check correlazione risposta == null ["+idUnivoco+"] in tracciamento nella richiesta");
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedCorrelazioneApplicativaRisposta(id, this.collaborazioneSPCoopBase.getMittente(), null));
			if(checkServizioApplicativo){
				// porta applicativa
				// check correlazione risposta
				boolean v = data.getVerificatoreTracciaRisposta().existsTracedCorrelazioneApplicativaRisposta(id, this.collaborazioneSPCoopBase.getMittente());
				Reporter.log("Check correlazione risposta in tracciamento nella risposta  (atteso:true trovato:"+v+")");
				Assert.assertTrue(v);
			}else{
				// porta delegata
				// check correlazione risposta non presente
				boolean v = data.getVerificatoreTracciaRisposta().existsTracedCorrelazioneApplicativaRisposta(id, this.collaborazioneSPCoopBase.getMittente());
				Reporter.log("Check correlazione risposta in tracciamento nella risposta  (atteso:false trovato:"+v+")");
				Assert.assertTrue(v==false);
			}
			
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
			if(dataMsgDiag!=null)
				dataMsgDiag.close();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/***
	 * Test Correlazione Applicativa Risposta Input Based con header SOAP e gestione stateless
	 */
		
	private String runClientCorrelazioneApplicativaInputBasedRisposta_SOAP_STATELESS(String portaDelegata,String idUnivoco, Repository repository) throws Exception{
		java.io.FileInputStream fin = null;
		DatabaseComponent dbComponentFruitore = null;
		try{
			fin = new java.io.FileInputStream(new File(Utilities.testSuiteProperties.getSoap11FileName()));
			Message msg=new Message(fin);
			msg.getSOAPPartAsBytes();

			Name name = new PrefixedQName("http://www.openspcoop2.org/core/integrazione","integrazione","openspcoop");
			org.apache.axis.message.SOAPHeaderElement header = 
				new org.apache.axis.message.SOAPHeaderElement(name);
			header.setActor("http://www.openspcoop2.org/core/integrazione");
			header.setMustUnderstand(false);
			header.addNamespaceDeclaration("SOAP_ENV","http://schemas.xmlsoap.org/soap/envelope/");

			TestSuiteProperties testsuiteProperties = TestSuiteProperties.getInstance();
			
			header.setAttribute(testsuiteProperties.getIDApplicativoSoap(), idUnivoco);
			
			if(msg.getSOAPHeader()==null)
				msg.getSOAPEnvelope().addHeader();
			msg.getSOAPHeader().addChildElement(header);
			
			ClientHttpGenerico client=new ClientHttpGenerico(repository);
			client.setSoapAction("\"TEST\"");
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
			} catch (AxisFault error) {
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				throw error;
			}finally{
				dbComponentFruitore.close();
			}
			
			checkHttpRisposta(client.getPropertiesTrasportoRisposta(),CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_TEST_CORRELAZIONE_APPLICATIVA_RISPOSTA_INPUT_BASED_SOAP,client.getIdMessaggio());
			
			checkMessaggioRisposta(client.getResponseMessage(),CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_TEST_CORRELAZIONE_APPLICATIVA_RISPOSTA_INPUT_BASED_SOAP,client.getIdMessaggio());
			
			return client.getIdMessaggio();
			
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
	
	Repository repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaInputBasedRisposta_SOAP_STATELESS=new Repository();
	Repository repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaInputBasedRisposta_SOAP_STATELESSIDUnivoco=new Repository();
	@Test(groups={IntegrazioneCorrelazioneApplicativa.ID_GRUPPO,IntegrazioneCorrelazioneApplicativa.ID_GRUPPO+".CORRELAZIONE_APPLICATIVA_RISPOSTA_INPUT_BASED_SOAP"},description="Test di tipo sincrono, Viene controllato se i body sono uguali e se gli attachment sono uguali")
	public void sincronoCorrelazioneApplicativaInputBasedRisposta_SOAP_STATELESS() throws TestSuiteException, IOException, Exception{
		
		String idUnivoco = "XXXXXXXIDXXXXXXXX_"+System.currentTimeMillis();
		this.repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaInputBasedRisposta_SOAP_STATELESSIDUnivoco.add(idUnivoco);
		
		String id1 = runClientCorrelazioneApplicativaInputBasedRisposta_SOAP_STATELESS(CostantiTestSuite.PORTA_DELEGATA_PROFILO_SINCRONO_INTEGRAZIONE_CORRELAZIONE_APPLICATIVA_RISPOSTA_INPUT_BASED_SOAP,
				idUnivoco,
				this.repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaInputBasedRisposta_SOAP_STATELESS);
		
		Reporter.log("ID invocazione["+id1+"]");
	}
	@DataProvider (name="SincronoCorrelazioneApplicativaInputBasedRisposta_SOAP_STATELESS")
	public Object[][]testSincronoCorrelazioneApplicativaInputBasedRisposta_SOAP_STATELESS()throws Exception{
		String id=this.repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaInputBasedRisposta_SOAP_STATELESS.getNext();
		String idUnivoco=this.repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaInputBasedRisposta_SOAP_STATELESSIDUnivoco.getNext();
		return new Object[][]{
				{idUnivoco,DatabaseProperties.getDatabaseComponentDiagnosticaFruitore(),DatabaseProperties.getDatabaseComponentFruitore(),id,false},	
				{idUnivoco,DatabaseProperties.getDatabaseComponentDiagnosticaErogatore(),DatabaseProperties.getDatabaseComponentErogatore(),id,false}	
		};
	}
	@Test(groups={IntegrazioneCorrelazioneApplicativa.ID_GRUPPO,IntegrazioneCorrelazioneApplicativa.ID_GRUPPO+".CORRELAZIONE_APPLICATIVA_RISPOSTA_INPUT_BASED_SOAP"},dataProvider="SincronoCorrelazioneApplicativaInputBasedRisposta_SOAP_STATELESS",dependsOnMethods={"sincronoCorrelazioneApplicativaInputBasedRisposta_SOAP_STATELESS"})
	public void testSincronoCorrelazioneApplicativaInputBasedRisposta_SOAP_STATELESS(String idUnivoco,DatabaseMsgDiagnosticiComponent dataMsgDiag,DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
		try{
			this.collaborazioneSPCoopBase.testSincrono(data, id, CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_TEST_CORRELAZIONE_APPLICATIVA_RISPOSTA_INPUT_BASED_SOAP, checkServizioApplicativo,
					null);
			Reporter.log("Numero messaggi arrivati al servizio applicativo: "+data.getVerificatoreTracciaRichiesta().isArrivedCount(id));
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isArrivedCount(id)==1);
			
			// check correlazione richiesta
			Reporter.log("Check correlazione ["+idUnivoco+"]");
			Assert.assertTrue(dataMsgDiag.isTracedCorrelazioneApplicativaIntoMsgDiagnosticiCorrelazione(idUnivoco));
			if(checkServizioApplicativo){
				// porta applicativa
				// check correlazione risposta
				boolean v = dataMsgDiag.existsCorrelazioneApplicativaRispostaIntoMsgDiagnosticiCorrelazione(id, false);
				Reporter.log("Check correlazione risposta presente (atteso:true trovato:"+v+")");
				Assert.assertTrue(v);
			}else{
				// porta delegata
				// check correlazione risposta non presente
				boolean v = dataMsgDiag.existsCorrelazioneApplicativaRispostaIntoMsgDiagnosticiCorrelazione(id, true);
				Reporter.log("Check correlazione risposta presente (atteso:false trovato:"+v+")");
				Assert.assertTrue(v==false);
			}
									
			// Check correlazione richiesta in tutte le tracce
			Reporter.log("Check correlazione ["+idUnivoco+"] in tracciamento nella richiesta");
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedCorrelazioneApplicativaRichiesta(id, this.collaborazioneSPCoopBase.getMittente(), idUnivoco));
			Reporter.log("Check correlazione ["+idUnivoco+"] in tracciamento nella risposta");
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedCorrelazioneApplicativaRichiesta(id, this.collaborazioneSPCoopBase.getMittente(), idUnivoco));
			
			// Check correlazione risposta solo nelle tracce della risposta
			Reporter.log("Check correlazione risposta == null ["+idUnivoco+"] in tracciamento nella richiesta");
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedCorrelazioneApplicativaRisposta(id, this.collaborazioneSPCoopBase.getMittente(), null));
			if(checkServizioApplicativo){
				// porta applicativa
				// check correlazione risposta
				boolean v = data.getVerificatoreTracciaRisposta().existsTracedCorrelazioneApplicativaRisposta(id, this.collaborazioneSPCoopBase.getMittente());
				Reporter.log("Check correlazione risposta in tracciamento nella risposta  (atteso:true trovato:"+v+")");
				Assert.assertTrue(v);
			}else{
				// porta delegata
				// check correlazione risposta non presente
				boolean v = data.getVerificatoreTracciaRisposta().existsTracedCorrelazioneApplicativaRisposta(id, this.collaborazioneSPCoopBase.getMittente());
				Reporter.log("Check correlazione risposta in tracciamento nella risposta  (atteso:false trovato:"+v+")");
				Assert.assertTrue(v==false);
			}
			
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
			if(dataMsgDiag!=null)
				dataMsgDiag.close();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	/***
	 * Test Correlazione Applicativa Risposta Input Based con header WSA e gestione stateless
	 */
		
	private String runClientCorrelazioneApplicativaInputBasedRisposta_WSA_STATELESS(String portaDelegata,String idUnivoco, Repository repository) throws Exception{
		java.io.FileInputStream fin = null;
		DatabaseComponent dbComponentFruitore = null;
		try{
			fin = new java.io.FileInputStream(new File(Utilities.testSuiteProperties.getSoap11FileName()));

			Message msg=new Message(fin);
			msg.getSOAPPartAsBytes();

			Name name = new PrefixedQName("http://www.w3.org/2005/08/addressing","MessageID","wsa");
			org.apache.axis.message.SOAPHeaderElement header = 
				new org.apache.axis.message.SOAPHeaderElement(name);
			header.setActor("http://www.openspcoop2.org/core/integrazione/wsa");
			header.setMustUnderstand(false);
			header.addNamespaceDeclaration("SOAP_ENV","http://schemas.xmlsoap.org/soap/envelope/");
			header.setValue("uuid:"+idUnivoco);
			
			if(msg.getSOAPHeader()==null)
				msg.getSOAPEnvelope().addHeader();
			
			byte [] headerByte = Axis14SoapUtils.msgElementoToByte(header);
			ByteArrayInputStream input = new ByteArrayInputStream(headerByte);
			Document document = org.apache.axis.utils.XMLUtils.newDocument(input);
			SOAPHeaderElement elementSenzaXSITypes = new SOAPHeaderElement(document.getDocumentElement());
			msg.getSOAPHeader().addChildElement(elementSenzaXSITypes);
			
			ClientHttpGenerico client=new ClientHttpGenerico(repository);
			client.setSoapAction("\"TEST\"");
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
			} catch (AxisFault error) {
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				throw error;
			}finally{
				dbComponentFruitore.close();
			}
			
			checkHttpRisposta(client.getPropertiesTrasportoRisposta(),CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_TEST_CORRELAZIONE_APPLICATIVA_RISPOSTA_INPUT_BASED_WSA,client.getIdMessaggio());
			
			checkMessaggioRispostaWSAddressing(client.getResponseMessage(),
					CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO, CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_TEST_CORRELAZIONE_APPLICATIVA_RISPOSTA_INPUT_BASED_WSA,client.getIdMessaggio());
			
			return client.getIdMessaggio();
			
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
	
	Repository repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaInputBasedRisposta_WSA_STATELESS=new Repository();
	Repository repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaInputBasedRisposta_WSA_STATELESSIDUnivoco=new Repository();
	@Test(groups={IntegrazioneCorrelazioneApplicativa.ID_GRUPPO,IntegrazioneCorrelazioneApplicativa.ID_GRUPPO+".CORRELAZIONE_APPLICATIVA_RISPOSTA_INPUT_BASED_WSA"},description="Test di tipo sincrono, Viene controllato se i body sono uguali e se gli attachment sono uguali")
	public void sincronoCorrelazioneApplicativaInputBasedRisposta_WSA_STATELESS() throws TestSuiteException, IOException, Exception{
		
		String idUnivoco = "XXXXXXXIDXXXXXXXX_"+System.currentTimeMillis();
		this.repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaInputBasedRisposta_WSA_STATELESSIDUnivoco.add(idUnivoco);
		
		String id1 = runClientCorrelazioneApplicativaInputBasedRisposta_WSA_STATELESS(CostantiTestSuite.PORTA_DELEGATA_PROFILO_SINCRONO_INTEGRAZIONE_CORRELAZIONE_APPLICATIVA_RISPOSTA_INPUT_BASED_WSA,
				idUnivoco,
				this.repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaInputBasedRisposta_WSA_STATELESS);
		
		Reporter.log("ID invocazione["+id1+"]");
	}
	@DataProvider (name="SincronoCorrelazioneApplicativaInputBasedRisposta_WSA_STATELESS")
	public Object[][]testSincronoCorrelazioneApplicativaInputBasedRisposta_WSA_STATELESS()throws Exception{
		String id=this.repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaInputBasedRisposta_WSA_STATELESS.getNext();
		String idUnivoco=this.repositorySincronoCorrelazioneApplicativaCorrelazioneApplicativaInputBasedRisposta_WSA_STATELESSIDUnivoco.getNext();
		return new Object[][]{
				{idUnivoco,DatabaseProperties.getDatabaseComponentDiagnosticaFruitore(),DatabaseProperties.getDatabaseComponentFruitore(),id,false},	
				{idUnivoco,DatabaseProperties.getDatabaseComponentDiagnosticaErogatore(),DatabaseProperties.getDatabaseComponentErogatore(),id,false}	
		};
	}
	@Test(groups={IntegrazioneCorrelazioneApplicativa.ID_GRUPPO,IntegrazioneCorrelazioneApplicativa.ID_GRUPPO+".CORRELAZIONE_APPLICATIVA_RISPOSTA_INPUT_BASED_WSA"},dataProvider="SincronoCorrelazioneApplicativaInputBasedRisposta_WSA_STATELESS",dependsOnMethods={"sincronoCorrelazioneApplicativaInputBasedRisposta_WSA_STATELESS"})
	public void testSincronoCorrelazioneApplicativaInputBasedRisposta_WSA_STATELESS(String idUnivoco,DatabaseMsgDiagnosticiComponent dataMsgDiag,DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
		try{
			this.collaborazioneSPCoopBase.testSincrono(data, id, CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_TEST_CORRELAZIONE_APPLICATIVA_RISPOSTA_INPUT_BASED_WSA, checkServizioApplicativo,
					null);
			Reporter.log("Numero messaggi arrivati al servizio applicativo: "+data.getVerificatoreTracciaRichiesta().isArrivedCount(id));
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isArrivedCount(id)==1);
			
			// check correlazione richiesta
			Reporter.log("Check correlazione ["+idUnivoco+"]");
			Assert.assertTrue(dataMsgDiag.isTracedCorrelazioneApplicativaIntoMsgDiagnosticiCorrelazione(idUnivoco));
			if(checkServizioApplicativo){
				// porta applicativa
				// check correlazione risposta
				boolean v = dataMsgDiag.existsCorrelazioneApplicativaRispostaIntoMsgDiagnosticiCorrelazione(id, false);
				Reporter.log("Check correlazione risposta presente (atteso:true trovato:"+v+")");
				Assert.assertTrue(v);
			}else{
				// porta delegata
				// check correlazione risposta non presente
				boolean v = dataMsgDiag.existsCorrelazioneApplicativaRispostaIntoMsgDiagnosticiCorrelazione(id, true);
				Reporter.log("Check correlazione risposta presente (atteso:false trovato:"+v+")");
				Assert.assertTrue(v==false);
			}
									
			// Check correlazione richiesta in tutte le tracce
			Reporter.log("Check correlazione ["+idUnivoco+"] in tracciamento nella richiesta");
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedCorrelazioneApplicativaRichiesta(id, this.collaborazioneSPCoopBase.getMittente(), idUnivoco));
			Reporter.log("Check correlazione ["+idUnivoco+"] in tracciamento nella risposta");
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedCorrelazioneApplicativaRichiesta(id, this.collaborazioneSPCoopBase.getMittente(), idUnivoco));
			
			// Check correlazione risposta solo nelle tracce della risposta
			Reporter.log("Check correlazione risposta == null ["+idUnivoco+"] in tracciamento nella richiesta");
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedCorrelazioneApplicativaRisposta(id, this.collaborazioneSPCoopBase.getMittente(), null));
			if(checkServizioApplicativo){
				// porta applicativa
				// check correlazione risposta
				boolean v = data.getVerificatoreTracciaRisposta().existsTracedCorrelazioneApplicativaRisposta(id, this.collaborazioneSPCoopBase.getMittente());
				Reporter.log("Check correlazione risposta in tracciamento nella risposta  (atteso:true trovato:"+v+")");
				Assert.assertTrue(v);
			}else{
				// porta delegata
				// check correlazione risposta non presente
				boolean v = data.getVerificatoreTracciaRisposta().existsTracedCorrelazioneApplicativaRisposta(id, this.collaborazioneSPCoopBase.getMittente());
				Reporter.log("Check correlazione risposta in tracciamento nella risposta  (atteso:false trovato:"+v+")");
				Assert.assertTrue(v==false);
			}
			
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
			if(dataMsgDiag!=null)
				dataMsgDiag.close();
		}
	}
}
