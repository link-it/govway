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
import java.util.Hashtable;
import java.util.Vector;

import javax.xml.soap.SOAPException;

import org.apache.axis.AxisFault;
import org.apache.axis.Message;
import org.openspcoop2.message.SOAPVersion;
import org.openspcoop2.pdd.core.CostantiPdD;
import org.openspcoop2.protocol.sdk.constants.CodiceErroreCooperazione;
import org.openspcoop2.protocol.sdk.constants.CodiceErroreIntegrazione;
import org.openspcoop2.protocol.sdk.constants.Inoltro;
import org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione;
import org.openspcoop2.protocol.spcoop.constants.SPCoopCostanti;
import org.openspcoop2.protocol.spcoop.testsuite.core.CooperazioneSPCoopBase;
import org.openspcoop2.protocol.spcoop.testsuite.core.CostantiTestSuite;
import org.openspcoop2.protocol.spcoop.testsuite.core.DatabaseProperties;
import org.openspcoop2.protocol.spcoop.testsuite.core.FileSystemUtilities;
import org.openspcoop2.protocol.spcoop.testsuite.core.SPCoopTestsuiteLogger;
import org.openspcoop2.protocol.spcoop.testsuite.core.Utilities;
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
 * Test sulla connessione HTTPS
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class HTTPS {

	/** Identificativo del gruppo */
	public static final String ID_GRUPPO = "HTTPS";
	
	/** Gestore della Collaborazione di Base */
	private CooperazioneBaseInformazioni info = CooperazioneSPCoopBase.getCooperazioneBaseInformazioni(CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE,
			CostantiTestSuite.SPCOOP_SOGGETTO_EROGATORE,
			false,SPCoopCostanti.PROFILO_TRASMISSIONE_CON_DUPLICATI,Inoltro.CON_DUPLICATI);	
	private CooperazioneBase collaborazioneSPCoopBase = 
		new CooperazioneBase(false,SOAPVersion.SOAP11,  this.info, 
				org.openspcoop2.protocol.spcoop.testsuite.core.TestSuiteProperties.getInstance(), 
				DatabaseProperties.getInstance(), SPCoopTestsuiteLogger.getInstance());



	private static boolean addIDUnivoco = true;


	
	
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
	
	

	
	/** Messaggi */
	private static final String MESSAGGIO_SPCOOP_RICEVUTO = "Ricevuto messaggio di cooperazione con identificativo [@IDEGOV@] inviata dalla parte mittente [@MITTENTE@] ( SSL Subject: [CN=@CN@, OU=test, O=openspcoop.org, L=Pisa, ST=Italy, C=IT, EMAILADDRESS=apoli@link.it] ) ";
	private static final String AUTORIZZAZIONE_PREFIX = "Controllo Autorizzazione[spcoop] messaggio con identificativo [@IDEGOV@] FR[@MITTENTE@]->ER[@DESTINATARIO@_@SERVIZIO@_@AZIONE@] inviato da una PdD mittente [CN=@CN@, OU=test, O=openspcoop.org, L=Pisa, ST=Italy, C=IT, EMAILADDRESS=apoli@link.it]";
	private static final String AUTORIZZAZIONE_IN_CORSO = AUTORIZZAZIONE_PREFIX+" ...";
	private static final String AUTORIZZAZIONE_EFFETTUATA = AUTORIZZAZIONE_PREFIX+": autorizzato";
	
	private static final String AUTORIZZAZIONE_BUSTE_FALLITA = "Verifica autorizzazione [registro] messaggio con identificativo [@IDEGOV@] FR[@MITTENTE@]->ER[@DESTINATARIO@--@SERVIZIO@:1--@AZIONE@]@PDD@ fallita (codice: EGOV_IT_201) Il soggetto @MITTENTE@ non e' autorizzato ad invocare il servizio @SERVIZIO@_@AZIONE@ erogato da @DESTINATARIO@";
	private static final String SOAP_FAULT_AUTORIZZAZIONE_SPCOOP_FALLITA = "@DESTINATARIO@ ha rilevato le seguenti eccezioni: Il soggetto @MITTENTE@ non e' autorizzato ad invocare il servizio @SERVIZIO@_@AZIONE@ erogato da @DESTINATARIO@";
	
	private static final String PDD_NON_DISPONIBILE = "Porta di Dominio del soggetto @DESTINATARIO@ non disponibile";
	private static final String BAD_CERTIFICATE = "bad_certificate";
	private static final String BAD_CERTIFICATE_2 = "Remote host closed connection during handshake";
	private static final String CA_NON_PRESENTE = "unable to find valid certification path to requested target";
	private static final String HOST_VERIFY = "No subject alternative names present";
	private static final String HOST_VERIFY_2 = "HTTPS hostname wrong:  should be <127.0.0.1>";
	
	private static final String CREDENZIALI_NON_FORNITE = "Autenticazione del servizio applicativo non riuscita: Credenziali non fornite";
	//private static final String CREDENZIALI_NON_CORRETTE = "Autenticazione del servizio applicativo non riuscita ( SSL Subject: CN=@SIL@, OU=test, O=openspcoop.org, L=Pisa, ST=Italy, C=IT, EMAILADDRESS=apoli@link.it ) : Credenziali fornite non corrette: subject[CN=@SIL@, OU=test, O=openspcoop.org, L=Pisa, ST=Italy, C=IT, EMAILADDRESS=apoli@link.it]";
	private static final String CREDENZIALI_NON_CORRETTE = "Autenticazione del servizio applicativo non riuscita: Credenziali fornite non corrette";
	//private static final String CREDENZIALI_NON_CORRETTE = "Autenticazione del servizio applicativo non riuscita ( SSL Subject: [CN=@SIL@, OU=test, O=openspcoop.org, L=Pisa, ST=Italy, C=IT, EMAILADDRESS=apoli@link.it] ) : Credenziali fornite non corrette";
	private static final String AUTORIZZAZIONE_FALLITA = "Il servizio applicativo @SILNAME@ non risulta autorizzato a fruire del servizio richiesto";
	//private static final String AUTORIZZAZIONE_FALLITA = "Autorizzazione non concessa al servizio applicativo [@SILNAME@] di utilizzare la porta delegata [@PD@]: Servizio non invocabile dal servizio applicativo @SILNAME@";
	
	private static final String SIL_RICONOSCIUTO = "Ricevuta richiesta di servizio dal Servizio Applicativo ( SSL Subject: [CN=@SIL@, OU=test, O=openspcoop.org, L=Pisa, ST=Italy, C=IT, EMAILADDRESS=apoli@link.it] ) @SILNAME@ verso la porta delegata @PD@";


	/***
	 * Test https con autenticazione client
	 */
	Repository repositoryHTTPSWithClientAuth=new Repository();
	@Test(groups={HTTPS.ID_GRUPPO,HTTPS.ID_GRUPPO+".WITH_CLIENT_AUTH"},description="Test di tipo sincrono, Viene controllato se i body sono uguali e se gli attachment sono uguali")
	public void httpsWithClientAuth() throws TestSuiteException, IOException, SOAPException{
		this.collaborazioneSPCoopBase.sincrono(this.repositoryHTTPSWithClientAuth,CostantiTestSuite.PORTA_DELEGATA_HTTPS_WITH_CLIENT_AUTH,addIDUnivoco);
	}
	@DataProvider (name="httpsWithClientAuth")
	public Object[][]testHttpsWithClientAuth()throws Exception{
		String id=this.repositoryHTTPSWithClientAuth.getNext();
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),null,id,false},	
				{DatabaseProperties.getDatabaseComponentErogatore(),DatabaseProperties.getDatabaseComponentDiagnosticaErogatore(),id,true}	
		};
	}
	@Test(groups={HTTPS.ID_GRUPPO,HTTPS.ID_GRUPPO+".WITH_CLIENT_AUTH"},dataProvider="httpsWithClientAuth",dependsOnMethods={"httpsWithClientAuth"})
	public void testHttpsWithClientAuth(DatabaseComponent data,DatabaseMsgDiagnosticiComponent dataMsg,String id,boolean checkServizioApplicativo) throws Exception{
		try{
			this.collaborazioneSPCoopBase.testSincrono(data, id, CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_HTTPS_WITH_CLIENT_AUTH, 
					checkServizioApplicativo,null);
			
			// Check msgDiag
			if(dataMsg!=null){
				String msg1 = MESSAGGIO_SPCOOP_RICEVUTO.replace("@IDEGOV@", id);
				msg1 = msg1.replace("@MITTENTE@", this.collaborazioneSPCoopBase.getMittente().getTipo()+"/"+this.collaborazioneSPCoopBase.getMittente().getNome());
				msg1 = msg1.replace("@CN@", "Soggetto1");
				Reporter.log("Controllo messaggio (id:"+id+") msg["+msg1+"]");
				Assert.assertTrue(dataMsg.isTracedMessaggio(id, msg1));
				
				String msg2 = AUTORIZZAZIONE_IN_CORSO.replace("@IDEGOV@", id);
				msg2 = msg2.replace("@MITTENTE@", this.collaborazioneSPCoopBase.getMittente().getTipo()+"/"+this.collaborazioneSPCoopBase.getMittente().getNome());
				msg2 = msg2.replace("@DESTINATARIO@", this.collaborazioneSPCoopBase.getDestinatario().getTipo()+"/"+this.collaborazioneSPCoopBase.getDestinatario().getNome());
				msg2 = msg2.replace("@SERVIZIO@", CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO+"/"+CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO);
				msg2 = msg2.replace("@AZIONE@", CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_HTTPS_WITH_CLIENT_AUTH);
				msg2 = msg2.replace("@CN@", "Soggetto1");
				Reporter.log("Controllo messaggio (id:"+id+") msg["+msg1+"]");
				Assert.assertTrue(dataMsg.isTracedMessaggio(id, msg1));			
				msg2 = AUTORIZZAZIONE_EFFETTUATA.replace("@IDEGOV@", id);
				msg2 = msg2.replace("@MITTENTE@", this.collaborazioneSPCoopBase.getMittente().getTipo()+"/"+this.collaborazioneSPCoopBase.getMittente().getNome());
				msg2 = msg2.replace("@DESTINATARIO@", this.collaborazioneSPCoopBase.getDestinatario().getTipo()+"/"+this.collaborazioneSPCoopBase.getDestinatario().getNome());
				msg2 = msg2.replace("@SERVIZIO@", CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO+"/"+CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO);
				msg2 = msg2.replace("@AZIONE@", CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_HTTPS_WITH_CLIENT_AUTH);
				msg2 = msg2.replace("@CN@", "Soggetto1");
				Reporter.log("Controllo messaggio (id:"+id+") msg["+msg1+"]");
				Assert.assertTrue(dataMsg.isTracedMessaggio(id, msg1));
			}
			
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
			if(dataMsg!=null){
				dataMsg.close();
			}
		}
	}


	
	
	
	
	
	
	
	
	
	
	
	
	/***
	 * Test https con autenticazione client (Identita2)
	 */
	Repository repositoryHTTPSWithClientAuthIdentita2=new Repository();
	@Test(groups={HTTPS.ID_GRUPPO,HTTPS.ID_GRUPPO+".WITH_CLIENT_AUTH_IDENTITA2"},description="Test di tipo sincrono, Viene controllato se i body sono uguali e se gli attachment sono uguali")
	public void httpsWithClientAuthIdentita2() throws TestSuiteException, IOException, SOAPException{
		this.collaborazioneSPCoopBase.sincrono(this.repositoryHTTPSWithClientAuthIdentita2,CostantiTestSuite.PORTA_DELEGATA_HTTPS_WITH_CLIENT_AUTH_IDENTITA2,addIDUnivoco);
	}
	@DataProvider (name="httpsWithClientAuthIdentita2")
	public Object[][]testHttpsWithClientAuthIdentita2()throws Exception{
		String id=this.repositoryHTTPSWithClientAuthIdentita2.getNext();
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),null,id,false},	
				{DatabaseProperties.getDatabaseComponentErogatore(),DatabaseProperties.getDatabaseComponentDiagnosticaErogatore(),id,true}	
		};
	}
	@Test(groups={HTTPS.ID_GRUPPO,HTTPS.ID_GRUPPO+".WITH_CLIENT_AUTH_IDENTITA2"},dataProvider="httpsWithClientAuthIdentita2",dependsOnMethods={"httpsWithClientAuthIdentita2"})
	public void testHttpsWithClientAuthIdentita2(DatabaseComponent data,DatabaseMsgDiagnosticiComponent dataMsg,String id,boolean checkServizioApplicativo) throws Exception{
		try{
			this.collaborazioneSPCoopBase.testSincrono(data, id, CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_HTTPS_WITH_CLIENT_AUTH_IDENTITA2, 
					checkServizioApplicativo,null);
			
			// Check msgDiag
			if(dataMsg!=null){
				String msg1 = MESSAGGIO_SPCOOP_RICEVUTO.replace("@IDEGOV@", id);
				msg1 = msg1.replace("@MITTENTE@", this.collaborazioneSPCoopBase.getMittente().getTipo()+"/"+this.collaborazioneSPCoopBase.getMittente().getNome());
				msg1 = msg1.replace("@CN@", "Soggetto2");
				Reporter.log("Controllo messaggio (id:"+id+") msg["+msg1+"]");
				Assert.assertTrue(dataMsg.isTracedMessaggio(id, msg1));
				
				String msg2 = AUTORIZZAZIONE_IN_CORSO.replace("@IDEGOV@", id);
				msg2 = msg2.replace("@MITTENTE@", this.collaborazioneSPCoopBase.getMittente().getTipo()+"/"+this.collaborazioneSPCoopBase.getMittente().getNome());
				msg2 = msg2.replace("@DESTINATARIO@", this.collaborazioneSPCoopBase.getDestinatario().getTipo()+"/"+this.collaborazioneSPCoopBase.getDestinatario().getNome());
				msg2 = msg2.replace("@SERVIZIO@", CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO+"/"+CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO);
				msg2 = msg2.replace("@AZIONE@", CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_HTTPS_WITH_CLIENT_AUTH_IDENTITA2);
				msg2 = msg2.replace("@CN@", "Soggetto2");
				Reporter.log("Controllo messaggio (id:"+id+") msg["+msg1+"]");
				Assert.assertTrue(dataMsg.isTracedMessaggio(id, msg1));			
				msg2 = AUTORIZZAZIONE_EFFETTUATA.replace("@IDEGOV@", id);
				msg2 = msg2.replace("@MITTENTE@", this.collaborazioneSPCoopBase.getMittente().getTipo()+"/"+this.collaborazioneSPCoopBase.getMittente().getNome());
				msg2 = msg2.replace("@DESTINATARIO@", this.collaborazioneSPCoopBase.getDestinatario().getTipo()+"/"+this.collaborazioneSPCoopBase.getDestinatario().getNome());
				msg2 = msg2.replace("@SERVIZIO@", CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO+"/"+CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO);
				msg2 = msg2.replace("@AZIONE@", CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_HTTPS_WITH_CLIENT_AUTH_IDENTITA2);
				msg2 = msg2.replace("@CN@", "Soggetto2");
				Reporter.log("Controllo messaggio (id:"+id+") msg["+msg1+"]");
				Assert.assertTrue(dataMsg.isTracedMessaggio(id, msg1));
			}
			
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
			if(dataMsg!=null){
				dataMsg.close();
			}
		}
	}

	
	
	
	
	
	
	
	
	
	
	
	
	/***
	 * Test https con autenticazione client non riuscita
	 */
	Repository repositoryHTTPSWithClientAuthError=new Repository();
	@Test(groups={HTTPS.ID_GRUPPO,HTTPS.ID_GRUPPO+".WITH_CLIENT_AUTH_ERROR"},description="Test di tipo sincrono, Viene controllato se i body sono uguali e se gli attachment sono uguali")
	public void httpsWithClientAuthError() throws TestSuiteException, IOException, Exception{
		
		Date dataInizioTest = DateManager.getDate();
		
		java.io.FileInputStream fin = null;
		DatabaseComponent dbComponentFruitore = null;
		try{
			fin = new java.io.FileInputStream(new File(Utilities.testSuiteProperties.getSoap11FileName()));

			Message msg=new Message(fin);
			msg.getSOAPPartAsBytes();
			
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryHTTPSWithClientAuthError);
			client.setSoapAction("\"TEST\"");
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_HTTPS_WITH_CLIENT_AUTH_ERROR);
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
				throw new Exception("Atteso errore");
			} catch (AxisFault error) {
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				Reporter.log("Controllo actor code [OpenSPCoop]");
				Assert.assertTrue(CostantiPdD.OPENSPCOOP2.equals(error.getFaultActor()));
				Reporter.log("Controllo fault code ["+Utilities.toString(CodiceErroreIntegrazione.CODICE_516_CONNETTORE_UTILIZZO_CON_ERRORE)+"]");
				Assert.assertTrue(Utilities.toString(CodiceErroreIntegrazione.CODICE_516_CONNETTORE_UTILIZZO_CON_ERRORE).equals(error.getFaultCode().getLocalPart()));
				String msg2 = PDD_NON_DISPONIBILE.replace("@DESTINATARIO@", this.collaborazioneSPCoopBase.getDestinatario().getTipo()+this.collaborazioneSPCoopBase.getDestinatario().getNome());
				Reporter.log("Controllo fault string ["+msg2+"]");
				Assert.assertTrue(msg2.equals(error.getFaultString()));
				Reporter.log("IDEGOV["+client.getIdMessaggio()+"]");
				this.repositoryHTTPSWithClientAuthError.add(client.getIdMessaggio());
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
		err.setMsgErrore("Errore avvenuto durante la consegna HTTP: Received fatal alert: bad_certificate");
		this.erroriAttesiOpenSPCoopCore.add(err);
		
		
		ErroreAttesoOpenSPCoopLogCore err2 = new ErroreAttesoOpenSPCoopLogCore();
		err2.setIntervalloInferiore(dataInizioTest);
		err2.setIntervalloSuperiore(dataFineTest);
		err2.setMsgErrore("Errore avvenuto durante la consegna HTTP: Remote host closed connection during handshake");
		this.erroriAttesiOpenSPCoopCore.add(err2);
	}
	@DataProvider (name="httpsWithClientAuthError")
	public Object[][]testHttpsWithClientAuthError()throws Exception{
		String id=this.repositoryHTTPSWithClientAuthError.getNext();
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),DatabaseProperties.getDatabaseComponentDiagnosticaFruitore(),id,false}
		};
	}
	@Test(groups={HTTPS.ID_GRUPPO,HTTPS.ID_GRUPPO+".WITH_CLIENT_AUTH_ERROR"},dataProvider="httpsWithClientAuthError",dependsOnMethods={"httpsWithClientAuthError"})
	public void testHttpsWithClientAuthError(DatabaseComponent data,DatabaseMsgDiagnosticiComponent dataMsg,String id,boolean checkServizioApplicativo) throws Exception{
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
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedAzione(id, CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_HTTPS_WITH_CLIENT_AUTH_ERROR));
			Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloDiCollaborazione(id, SPCoopCostanti.PROFILO_COLLABORAZIONE_SINCRONO, ProfiloDiCollaborazione.SINCRONO));
			Reporter.log("Numero messaggi arrivati al servizio applicativo: "+data.getVerificatoreTracciaRichiesta().isArrivedCount(id));
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isArrivedCount(id)==0);
			
			String version_jbossas = Utilities.readApplicationServerVersion();
			if(!version_jbossas.startsWith("tomcat")){
				Reporter.log("Controllo lista trasmissione con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedTrasmissione(id, CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE, null, CostantiTestSuite.SPCOOP_SOGGETTO_EROGATORE, null));
			}
						
			// Check msgDiag
			if(dataMsg!=null){
				boolean badCertificate = dataMsg.isTracedMessaggioWithLike(id,BAD_CERTIFICATE);
				boolean badCertificate2 = dataMsg.isTracedMessaggioWithLike(id,BAD_CERTIFICATE_2);
				Assert.assertTrue(badCertificate || badCertificate2);
			}
			
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
			if(dataMsg!=null){
				dataMsg.close();
			}
		}
	}
	
	
	
	
	
	
	
	
	
	/***
	 * Test https senza autenticazione client
	 */
	Repository repositoryHTTPSWithoutClientAuth=new Repository();
	@Test(groups={HTTPS.ID_GRUPPO,HTTPS.ID_GRUPPO+".WITHOUT_CLIENT_AUTH"},description="Test di tipo sincrono, Viene controllato se i body sono uguali e se gli attachment sono uguali")
	public void httpsWithoutClientAuth() throws TestSuiteException, IOException, SOAPException{
		this.collaborazioneSPCoopBase.sincrono(this.repositoryHTTPSWithoutClientAuth,CostantiTestSuite.PORTA_DELEGATA_HTTPS_WITHOUT_CLIENT_AUTH,addIDUnivoco);
	}
	@DataProvider (name="httpsWithoutClientAuth")
	public Object[][]testHttpsWithoutClientAuth()throws Exception{
		String id=this.repositoryHTTPSWithoutClientAuth.getNext();
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),null,id,false},	
				{DatabaseProperties.getDatabaseComponentErogatore(),DatabaseProperties.getDatabaseComponentDiagnosticaErogatore(),id,true}	
		};
	}
	@Test(groups={HTTPS.ID_GRUPPO,HTTPS.ID_GRUPPO+".WITHOUT_CLIENT_AUTH"},dataProvider="httpsWithoutClientAuth",dependsOnMethods={"httpsWithoutClientAuth"})
	public void testHttpsWithoutClientAuth(DatabaseComponent data,DatabaseMsgDiagnosticiComponent dataMsg,String id,boolean checkServizioApplicativo) throws Exception{
		try{
			this.collaborazioneSPCoopBase.testSincrono(data, id, CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_HTTPS_WITHOUT_CLIENT_AUTH, 
					checkServizioApplicativo,null);
			
			// Check msgDiag
			if(dataMsg!=null){
				String msg1 = MESSAGGIO_SPCOOP_RICEVUTO.replace("@IDEGOV@", id);
				msg1 = msg1.replace("@MITTENTE@", this.collaborazioneSPCoopBase.getMittente().getTipo()+"/"+this.collaborazioneSPCoopBase.getMittente());
				msg1 = msg1.replace("@CN@", "Soggetto1");
				Assert.assertTrue(dataMsg.isTracedMessaggio(id, msg1)==false);
				
				String msg2 = AUTORIZZAZIONE_IN_CORSO.replace("@IDEGOV@", id);
				msg2 = msg2.replace("@MITTENTE@", this.collaborazioneSPCoopBase.getMittente().getTipo()+"/"+this.collaborazioneSPCoopBase.getMittente());
				msg2 = msg2.replace("@DESTINATARIO@", this.collaborazioneSPCoopBase.getDestinatario().getTipo()+"/"+this.collaborazioneSPCoopBase.getDestinatario());
				msg2 = msg2.replace("@SERVIZIO@", CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO+"/"+CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO);
				msg2 = msg2.replace("@AZIONE@", CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_HTTPS_WITH_CLIENT_AUTH);
				msg2 = msg2.replace("@CN@", "Soggetto1");
				Assert.assertTrue(dataMsg.isTracedMessaggio(id, msg1)==false);			
				msg2 = AUTORIZZAZIONE_EFFETTUATA.replace("@IDEGOV@", id);
				msg2 = msg2.replace("@MITTENTE@", this.collaborazioneSPCoopBase.getMittente().getTipo()+"/"+this.collaborazioneSPCoopBase.getMittente());
				msg2 = msg2.replace("@DESTINATARIO@", this.collaborazioneSPCoopBase.getDestinatario().getTipo()+"/"+this.collaborazioneSPCoopBase.getDestinatario());
				msg2 = msg2.replace("@SERVIZIO@", CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO+"/"+CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO);
				msg2 = msg2.replace("@AZIONE@", CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_HTTPS_WITH_CLIENT_AUTH);
				msg2 = msg2.replace("@CN@", "Soggetto1");
				Assert.assertTrue(dataMsg.isTracedMessaggio(id, msg1)==false);
			}
			
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
			if(dataMsg!=null){
				dataMsg.close();
			}
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	/***
	 * Test ca non presente nel client
	 */
	Repository repositoryHTTPSCANonPresente=new Repository();
	@Test(groups={HTTPS.ID_GRUPPO,HTTPS.ID_GRUPPO+".CA_NON_PRESENTE"},description="Test di tipo sincrono, Viene controllato se i body sono uguali e se gli attachment sono uguali")
	public void httpsCANonPresente() throws TestSuiteException, IOException, Exception{
		
		Date dataInizioTest = DateManager.getDate();
		
		java.io.FileInputStream fin = null;
		DatabaseComponent dbComponentFruitore = null;
		try{
			fin = new java.io.FileInputStream(new File(Utilities.testSuiteProperties.getSoap11FileName()));

			Message msg=new Message(fin);
			msg.getSOAPPartAsBytes();
			
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryHTTPSCANonPresente);
			client.setSoapAction("\"TEST\"");
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_HTTPS_CA_NON_PRESENTE);
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
				throw new Exception("Atteso errore");
			} catch (AxisFault error) {
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				Reporter.log("Controllo actor code [OpenSPCoop]");
				Assert.assertTrue(CostantiPdD.OPENSPCOOP2.equals(error.getFaultActor()));
				Reporter.log("Controllo fault code ["+Utilities.toString(CodiceErroreIntegrazione.CODICE_516_CONNETTORE_UTILIZZO_CON_ERRORE)+"]");
				Assert.assertTrue(Utilities.toString(CodiceErroreIntegrazione.CODICE_516_CONNETTORE_UTILIZZO_CON_ERRORE).equals(error.getFaultCode().getLocalPart()));
				String msg2 = PDD_NON_DISPONIBILE.replace("@DESTINATARIO@", this.collaborazioneSPCoopBase.getDestinatario().getTipo()+this.collaborazioneSPCoopBase.getDestinatario().getNome());
				Reporter.log("Controllo fault string ["+msg2+"]");
				Assert.assertTrue(msg2.equals(error.getFaultString()));
				Reporter.log("IDEGOV["+client.getIdMessaggio()+"]");
				this.repositoryHTTPSCANonPresente.add(client.getIdMessaggio());
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
		err.setMsgErrore("Errore avvenuto durante la consegna HTTP: sun.security.validator.ValidatorException: PKIX path building failed: sun.security.provider.certpath.SunCertPathBuilderException: unable to find valid certification path to requested target");
		this.erroriAttesiOpenSPCoopCore.add(err);
	}
	@DataProvider (name="httpsCANonPresente")
	public Object[][]testHttpsCANonPresente()throws Exception{
		String id=this.repositoryHTTPSCANonPresente.getNext();
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),DatabaseProperties.getDatabaseComponentDiagnosticaFruitore(),id,false}
		};
	}
	@Test(groups={HTTPS.ID_GRUPPO,HTTPS.ID_GRUPPO+".CA_NON_PRESENTE"},dataProvider="httpsCANonPresente",dependsOnMethods={"httpsCANonPresente"})
	public void testHttpsCANonPresente(DatabaseComponent data,DatabaseMsgDiagnosticiComponent dataMsg,String id,boolean checkServizioApplicativo) throws Exception{
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
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedAzione(id, CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_HTTPS_CA_NON_PRESENTE));
			Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloDiCollaborazione(id, SPCoopCostanti.PROFILO_COLLABORAZIONE_SINCRONO, ProfiloDiCollaborazione.SINCRONO));
			Reporter.log("Numero messaggi arrivati al servizio applicativo: "+data.getVerificatoreTracciaRichiesta().isArrivedCount(id));
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isArrivedCount(id)==0);
			Reporter.log("Controllo lista trasmissione con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedTrasmissione(id, CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE, null, CostantiTestSuite.SPCOOP_SOGGETTO_EROGATORE, null));
			
			// Check msgDiag
			if(dataMsg!=null){
				Assert.assertTrue(dataMsg.isTracedMessaggioWithLike(id,CA_NON_PRESENTE));
			}
			
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
			if(dataMsg!=null){
				dataMsg.close();
			}
		}
	}
	
	
	
	
	
	
	
	
	
	/***
	 * Test host verify
	 */
	Repository repositoryHTTPSHostnameVerifier=new Repository();
	@Test(groups={HTTPS.ID_GRUPPO,HTTPS.ID_GRUPPO+".HOSTNAME_VERIFIER"},description="Test di tipo sincrono, Viene controllato se i body sono uguali e se gli attachment sono uguali")
	public void httpsHostnameVerifier() throws TestSuiteException, IOException, Exception{
		
		Date dataInizioTest = DateManager.getDate();
		
		java.io.FileInputStream fin = null;
		DatabaseComponent dbComponentFruitore = null;
		try{
			fin = new java.io.FileInputStream(new File(Utilities.testSuiteProperties.getSoap11FileName()));

			Message msg=new Message(fin);
			msg.getSOAPPartAsBytes();
			
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryHTTPSHostnameVerifier);
			client.setSoapAction("\"TEST\"");
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_HTTPS_HOSTNAME_VERIFY);
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
				throw new Exception("Atteso errore di hostname verifier");
			} catch (AxisFault error) {
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				Reporter.log("Controllo actor code [OpenSPCoop]");
				Assert.assertTrue(CostantiPdD.OPENSPCOOP2.equals(error.getFaultActor()));
				Reporter.log("Controllo fault code ["+Utilities.toString(CodiceErroreIntegrazione.CODICE_516_CONNETTORE_UTILIZZO_CON_ERRORE)+"]");
				Assert.assertTrue(Utilities.toString(CodiceErroreIntegrazione.CODICE_516_CONNETTORE_UTILIZZO_CON_ERRORE).equals(error.getFaultCode().getLocalPart()));
				String msg2 = PDD_NON_DISPONIBILE.replace("@DESTINATARIO@", this.collaborazioneSPCoopBase.getDestinatario().getTipo()+this.collaborazioneSPCoopBase.getDestinatario().getNome());
				Reporter.log("Controllo fault string ["+msg2+"]");
				Assert.assertTrue(msg2.equals(error.getFaultString()));
				Reporter.log("IDEGOV["+client.getIdMessaggio()+"]");
				this.repositoryHTTPSHostnameVerifier.add(client.getIdMessaggio());
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
		err.setMsgErrore("Errore avvenuto durante la consegna HTTP: java.security.cert.CertificateException: No subject alternative names present");
		this.erroriAttesiOpenSPCoopCore.add(err);

	}
	@DataProvider (name="httpsHostnameVerifier")
	public Object[][]testHttpsHostnameVerifier()throws Exception{
		String id=this.repositoryHTTPSHostnameVerifier.getNext();
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),DatabaseProperties.getDatabaseComponentDiagnosticaFruitore(),id,false}
		};
	}
	@Test(groups={HTTPS.ID_GRUPPO,HTTPS.ID_GRUPPO+".HOSTNAME_VERIFIER"},dataProvider="httpsHostnameVerifier",dependsOnMethods={"httpsHostnameVerifier"})
	public void testHttpsHostnameVerifier(DatabaseComponent data,DatabaseMsgDiagnosticiComponent dataMsg,String id,boolean checkServizioApplicativo) throws Exception{
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
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedAzione(id, CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_HTTPS_HOSTNAME_VERIFY));
			Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloDiCollaborazione(id, SPCoopCostanti.PROFILO_COLLABORAZIONE_SINCRONO, ProfiloDiCollaborazione.SINCRONO));
			Reporter.log("Numero messaggi arrivati al servizio applicativo: "+data.getVerificatoreTracciaRichiesta().isArrivedCount(id));
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isArrivedCount(id)==0);
			Reporter.log("Controllo lista trasmissione con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedTrasmissione(id, CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE, null, CostantiTestSuite.SPCOOP_SOGGETTO_EROGATORE, null));
			
			// Check msgDiag
			if(dataMsg!=null){
				Assert.assertTrue( dataMsg.isTracedMessaggioWithLike(id,HOST_VERIFY) || dataMsg.isTracedMessaggioWithLike(id,HOST_VERIFY_2) );
			}
			
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
			if(dataMsg!=null){
				dataMsg.close();
			}
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * Test di invocazione di una porta delegata fornendo autenticazioni sbagliate
	 */
	@DataProvider(name="credenzialiScorrette")
	public Object[][] credenzialiScorrette(){
		return new Object[][]{
				{null,null,null},// nessuna credenziale
				{"/etc/openspcoop2/keys/sil3.jks","openspcoopjks","openspcoop"}, // crendeziali non corrette
		};
	}
	Repository repositoryHTTPSautenticazioneSSL=new Repository();
	@Test(groups={HTTPS.ID_GRUPPO,HTTPS.ID_GRUPPO+".CREDENZIALI_NON_CORRETTE"},dataProvider="credenzialiScorrette")
	public void TestAutenticazioneCredenzialiScorrette(String location,String passwordKeystore,String passwordKey) throws Exception{
		java.io.FileInputStream fin = null;
		DatabaseComponent dbComponentFruitore = null;
		try{
			fin = new java.io.FileInputStream(new File(Utilities.testSuiteProperties.getSoap11FileName()));

			Message msg=new Message(fin);
			msg.getSOAPPartAsBytes();
			
			// Contesto SSL
			java.util.Hashtable<String, String> sslContext = null;
			if(location!=null){
				sslContext = new Hashtable<String, String>();
				if(location!=null){
					sslContext.put("trustStoreLocation", location);
					sslContext.put("keyStoreLocation", location);
				}
				if(passwordKeystore!=null){
					sslContext.put("trustStorePassword", passwordKeystore);
					sslContext.put("keyStorePassword", passwordKeystore);
				}
				if(passwordKey!=null){
					sslContext.put("keyPassword", passwordKey);
				}
				sslContext.put("hostnameVerifier", "false");
			}

			
			
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryHTTPSautenticazioneSSL,sslContext);
			client.setSoapAction("\"TEST\"");
			if(location!=null){
				//System.out.println("Location ["+Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore_httpsConAutenticazioneClient()+"]");
				client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore_httpsConAutenticazioneClient());
			}else{
				//System.out.println("NoLocation ["+Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore()+"]");
				client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			}
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_HTTPS_AUTENTICAZIONE_SSL);
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
				throw new Exception("Atteso errore");
			} catch (AxisFault error) {
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				Reporter.log("Controllo actor code [OpenSPCoop]");
				Assert.assertTrue(CostantiPdD.OPENSPCOOP2.equals(error.getFaultActor()));
				Reporter.log("Controllo fault code ["+Utilities.toString(CodiceErroreIntegrazione.CODICE_402_AUTENTICAZIONE_FALLITA)+"]");
				Assert.assertTrue(Utilities.toString(CodiceErroreIntegrazione.CODICE_402_AUTENTICAZIONE_FALLITA).equals(error.getFaultCode().getLocalPart()));
				
				if(location!=null){
					String msg2 = CREDENZIALI_NON_CORRETTE.replace("@SIL@", "sil3");
					msg2 = msg2.replace("@SIL@", "sil3");
					Reporter.log("Controllo fault string ["+msg2+"]");
					Assert.assertTrue(msg2.equals(error.getFaultString()));
				}else{
					String msg2 =  CREDENZIALI_NON_FORNITE;
					Assert.assertTrue(msg2.equals(error.getFaultString()));
				}
				Reporter.log("IDEGOV["+client.getIdMessaggio()+"]");
				this.repositoryHTTPSautenticazioneSSL.add(client.getIdMessaggio());
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
	 * Test https con autenticazione client
	 */
	Repository repositoryHTTPSAutenticazioneSIL=new Repository();
	Date dataTestAutenticazioneSIL = null;
	@Test(groups={HTTPS.ID_GRUPPO,HTTPS.ID_GRUPPO+".AUTENTICAZIONE_SIL"},description="Test di tipo sincrono, Viene controllato se i body sono uguali e se gli attachment sono uguali")
	public void httpsAutenticazioneSIL() throws TestSuiteException, IOException, Exception{
		
		this.dataTestAutenticazioneSIL = new Date();
		
		java.io.FileInputStream fin = null;
		DatabaseComponent dbComponentFruitore = null;
		try{
			fin = new java.io.FileInputStream(new File(Utilities.testSuiteProperties.getSoap11FileName()));

			Message msg=new Message(fin);
			msg.getSOAPPartAsBytes();
			
			// Contesto SSL
			java.util.Hashtable<String, String> sslContext = null;
			sslContext = new Hashtable<String, String>();
			sslContext.put("trustStoreLocation", "/etc/openspcoop2/keys/sil1.jks");
			sslContext.put("keyStoreLocation", "/etc/openspcoop2/keys/sil1.jks");
			sslContext.put("trustStorePassword", "openspcoopjks");
			sslContext.put("keyStorePassword", "openspcoopjks");
			sslContext.put("keyPassword", "openspcoop");
			sslContext.put("hostnameVerifier", "false");
						
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryHTTPSAutenticazioneSIL,sslContext);
			client.setSoapAction("\"TEST\"");
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore_httpsConAutenticazioneClient());
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_HTTPS_AUTENTICAZIONE_SSL);
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
	@DataProvider (name="httpsAutenticazioneSIL")
	public Object[][]testHttpsAutenticazioneSIL()throws Exception{
		String id=this.repositoryHTTPSAutenticazioneSIL.getNext();
		
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),DatabaseProperties.getDatabaseComponentDiagnosticaFruitore(),id,false},	
				{DatabaseProperties.getDatabaseComponentErogatore(),null,id,true}	
		};
	}
	@Test(groups={HTTPS.ID_GRUPPO,HTTPS.ID_GRUPPO+".AUTENTICAZIONE_SIL"},dataProvider="httpsAutenticazioneSIL",dependsOnMethods={"httpsAutenticazioneSIL"})
	public void testHttpsAutenticazioneSIL(DatabaseComponent data,DatabaseMsgDiagnosticiComponent dataMsg,String id,boolean checkServizioApplicativo) throws Exception{
		try{
			this.collaborazioneSPCoopBase.testSincrono(data, id, CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,null, 
					checkServizioApplicativo,null);
			
			// Check msgDiag
			if(dataMsg!=null){
				String msg1 = SIL_RICONOSCIUTO.replace("@SIL@", "sil1");
				msg1 = msg1.replace("@SILNAME@", "sil1HTTPS");
				msg1 = msg1.replace("@PD@", CostantiTestSuite.PORTA_DELEGATA_HTTPS_AUTENTICAZIONE_SSL);
				Assert.assertTrue(dataMsg.isTracedMessaggioWithLike(this.dataTestAutenticazioneSIL , msg1));
			}
			
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
			if(dataMsg!=null){
				dataMsg.close();
			}
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	/***
	 * Test https con autenticazione client
	 */
	Date dataTestAutenticazioneSIL2 = null;
	Repository repositoryHTTPSAutenticazioneSIL_Test2=new Repository();
	private CooperazioneBaseInformazioni infoTestSil2 = CooperazioneSPCoopBase.getCooperazioneBaseInformazioni(CostantiTestSuite.SPCOOP_SOGGETTO_1,
			CostantiTestSuite.SPCOOP_SOGGETTO_EROGATORE,
			false,SPCoopCostanti.PROFILO_TRASMISSIONE_CON_DUPLICATI,Inoltro.CON_DUPLICATI);	
	private CooperazioneBase collaborazioneSPCoopBaseTestSil2 = 
		new CooperazioneBase(false,SOAPVersion.SOAP11,  this.infoTestSil2, 
				org.openspcoop2.protocol.spcoop.testsuite.core.TestSuiteProperties.getInstance(), 
				DatabaseProperties.getInstance(), SPCoopTestsuiteLogger.getInstance());
		
	@Test(groups={HTTPS.ID_GRUPPO,HTTPS.ID_GRUPPO+".AUTENTICAZIONE_SIL_2"},description="Test di tipo sincrono, Viene controllato se i body sono uguali e se gli attachment sono uguali")
	public void httpsAutenticazioneSIL_2() throws TestSuiteException, IOException, Exception{
		
		this.dataTestAutenticazioneSIL2 = new Date();
		
		java.io.FileInputStream fin = null;
		DatabaseComponent dbComponentFruitore = null;
		try{
			fin = new java.io.FileInputStream(new File(Utilities.testSuiteProperties.getSoap11FileName()));

			Message msg=new Message(fin);
			msg.getSOAPPartAsBytes();
			
			// Contesto SSL
			java.util.Hashtable<String, String> sslContext = null;
			sslContext = new Hashtable<String, String>();
			sslContext.put("trustStoreLocation", "/etc/openspcoop2/keys/sil1.jks");
			sslContext.put("keyStoreLocation", "/etc/openspcoop2/keys/sil1.jks");
			sslContext.put("trustStorePassword", "openspcoopjks");
			sslContext.put("keyStorePassword", "openspcoopjks");
			sslContext.put("keyPassword", "openspcoop");
			sslContext.put("hostnameVerifier", "false");
						
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryHTTPSAutenticazioneSIL_Test2,sslContext);
			client.setSoapAction("\"TEST\"");
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore_httpsConAutenticazioneClient());
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_HTTPS_AUTENTICAZIONE_SSL_2);
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
	@DataProvider (name="httpsAutenticazioneSIL_2")
	public Object[][]testHttpsAutenticazioneSIL_2()throws Exception{
		String id=this.repositoryHTTPSAutenticazioneSIL_Test2.getNext();
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),DatabaseProperties.getDatabaseComponentDiagnosticaFruitore(),id,false},	
				{DatabaseProperties.getDatabaseComponentErogatore(),null,id,true}	
		};
	}
	@Test(groups={HTTPS.ID_GRUPPO,HTTPS.ID_GRUPPO+".AUTENTICAZIONE_SIL_2"},dataProvider="httpsAutenticazioneSIL_2",dependsOnMethods={"httpsAutenticazioneSIL_2"})
	public void testHttpsAutenticazioneSIL_2(DatabaseComponent data,DatabaseMsgDiagnosticiComponent dataMsg,String id,boolean checkServizioApplicativo) throws Exception{
		try{
			this.collaborazioneSPCoopBaseTestSil2.testSincrono(data, id, CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,null, 
					checkServizioApplicativo,null);
			
			// Check msgDiag
			if(dataMsg!=null){
				String msg1 = SIL_RICONOSCIUTO.replace("@SIL@", "sil1");
				msg1 = msg1.replace("@SILNAME@", "sil1HTTPS_test2");
				msg1 = msg1.replace("@PD@", CostantiTestSuite.PORTA_DELEGATA_HTTPS_AUTENTICAZIONE_SSL_2);
				Reporter.log("Verifico msg ["+msg1+"]");
				Assert.assertTrue(dataMsg.isTracedMessaggioWithLike(this.dataTestAutenticazioneSIL2, msg1));
			}
			
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
			if(dataMsg!=null){
				dataMsg.close();
			}
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * Test di invocazione di una porta delegata fornendo autenticazioni sbagliate
	 */
	@DataProvider(name="autorizzazioneFallita")
	public Object[][] autorizzazioneFallita(){
		return new Object[][]{
				{"/etc/openspcoop2/keys/sil2.jks","openspcoopjks","openspcoop"}, 
		};
	}
	Repository repositoryHTTPSautorizzazioneFallita=new Repository();
	@Test(groups={HTTPS.ID_GRUPPO,HTTPS.ID_GRUPPO+".AUTORIZZAZIONE_FALLITA"},dataProvider="autorizzazioneFallita")
	public void testAutorizzazioneFallita(String location,String passwordKeystore,String passwordKey) throws Exception{
		java.io.FileInputStream fin = null;
		DatabaseComponent dbComponentFruitore = null;
		try{
			fin = new java.io.FileInputStream(new File(Utilities.testSuiteProperties.getSoap11FileName()));

			Message msg=new Message(fin);
			msg.getSOAPPartAsBytes();
			
			// Contesto SSL
			java.util.Hashtable<String, String> sslContext = null;
			if(location!=null){
				sslContext = new Hashtable<String, String>();
				if(location!=null){
					sslContext.put("trustStoreLocation", location);
					sslContext.put("keyStoreLocation", location);
				}
				if(passwordKeystore!=null){
					sslContext.put("trustStorePassword", passwordKeystore);
					sslContext.put("keyStorePassword", passwordKeystore);
				}
				if(passwordKey!=null){
					sslContext.put("keyPassword", passwordKey);
				}
				sslContext.put("hostnameVerifier", "false");
			}

			
			
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryHTTPSautorizzazioneFallita,sslContext);
			client.setSoapAction("\"TEST\"");
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore_httpsConAutenticazioneClient());
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_HTTPS_AUTENTICAZIONE_SSL);
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
				throw new Exception("Atteso errore");
			} catch (AxisFault error) {
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				Reporter.log("Controllo actor code [OpenSPCoop]");
				Assert.assertTrue(CostantiPdD.OPENSPCOOP2.equals(error.getFaultActor()));
				Reporter.log("Controllo fault code ["+Utilities.toString(CodiceErroreIntegrazione.CODICE_404_AUTORIZZAZIONE_FALLITA)+"]");
				Assert.assertTrue(Utilities.toString(CodiceErroreIntegrazione.CODICE_404_AUTORIZZAZIONE_FALLITA).equals(error.getFaultCode().getLocalPart()));
				
				String msg2 = AUTORIZZAZIONE_FALLITA.replace("@SILNAME@", "sil2HTTPS");
				msg2 = msg2.replace("@SILNAME@", "sil2HTTPS");
				msg2 = msg2.replace("@PD@", CostantiTestSuite.PORTA_DELEGATA_HTTPS_AUTENTICAZIONE_SSL);
				Reporter.log("Controllo fault string ["+msg2+"]");
				Assert.assertTrue(msg2.equals(error.getFaultString()));

				Reporter.log("IDEGOV["+client.getIdMessaggio()+"]");
				this.repositoryHTTPSautorizzazioneFallita.add(client.getIdMessaggio());
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
	 * Test https con autorizzazione spcoop ok
	 */
	private CooperazioneBaseInformazioni infoFruitoreSoggetto1 = CooperazioneSPCoopBase.getCooperazioneBaseInformazioni(CostantiTestSuite.SPCOOP_SOGGETTO_1,
			CostantiTestSuite.SPCOOP_SOGGETTO_EROGATORE,
			false,SPCoopCostanti.PROFILO_TRASMISSIONE_CON_DUPLICATI,Inoltro.CON_DUPLICATI);	
	private CooperazioneBase collaborazioneSPCoopFruitoreSoggetto1 = 
		new CooperazioneBase(false,SOAPVersion.SOAP11,  this.infoFruitoreSoggetto1, 
				org.openspcoop2.protocol.spcoop.testsuite.core.TestSuiteProperties.getInstance(), 
				DatabaseProperties.getInstance(), SPCoopTestsuiteLogger.getInstance());
	
	Repository repositoryHTTPSAutorizzazioneSPCoopOkSoggetto1=new Repository();
	@Test(groups={HTTPS.ID_GRUPPO,HTTPS.ID_GRUPPO+".AUTORIZZAZIONE_SPCOOP"},description="Test di tipo sincrono, Viene controllato se i body sono uguali e se gli attachment sono uguali")
	public void httpsAutorizzazioneSPCoopOkSoggetto1() throws TestSuiteException, IOException, SOAPException{
		this.collaborazioneSPCoopFruitoreSoggetto1.sincrono(this.repositoryHTTPSAutorizzazioneSPCoopOkSoggetto1,CostantiTestSuite.PORTA_DELEGATA_AUTORIZZAZIONE_SPCOOP_FUNZIONANTE,addIDUnivoco);
	}
	@DataProvider (name="httpsAutorizzazioneSPCoopOkSoggetto1")
	public Object[][]testHttpsAutorizzazioneSPCoopOkSoggetto1()throws Exception{
		String id=this.repositoryHTTPSAutorizzazioneSPCoopOkSoggetto1.getNext();
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),null,id,false},	
				{DatabaseProperties.getDatabaseComponentErogatore(),DatabaseProperties.getDatabaseComponentDiagnosticaErogatore(),id,true}	
		};
	}
	@Test(groups={HTTPS.ID_GRUPPO,HTTPS.ID_GRUPPO+".AUTORIZZAZIONE_SPCOOP"},dataProvider="httpsAutorizzazioneSPCoopOkSoggetto1",dependsOnMethods={"httpsAutorizzazioneSPCoopOkSoggetto1"})
	public void testHttpsAutorizzazioneSPCoopOkSoggetto1(DatabaseComponent data,DatabaseMsgDiagnosticiComponent dataMsg,String id,boolean checkServizioApplicativo) throws Exception{
		try{
			this.collaborazioneSPCoopFruitoreSoggetto1.testSincrono(data, id, CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,null, 
					checkServizioApplicativo,null);
			
			// Check msgDiag
			if(dataMsg!=null){
				String msg1 = MESSAGGIO_SPCOOP_RICEVUTO.replace("@IDEGOV@", id);
				msg1 = msg1.replace("@MITTENTE@", this.collaborazioneSPCoopFruitoreSoggetto1.getMittente().getTipo()+"/"+this.collaborazioneSPCoopFruitoreSoggetto1.getMittente().getNome());
				msg1 = msg1.replace("@CN@", "Soggetto1");
				Reporter.log("Controllo messaggio (id:"+id+") msg["+msg1+"]");
				Assert.assertTrue(dataMsg.isTracedMessaggio(id, msg1));
				
				String msg2 = AUTORIZZAZIONE_IN_CORSO.replace("@IDEGOV@", id);
				msg2 = msg2.replace("@MITTENTE@", this.collaborazioneSPCoopFruitoreSoggetto1.getMittente().getTipo()+"/"+this.collaborazioneSPCoopFruitoreSoggetto1.getMittente().getNome());
				msg2 = msg2.replace("@DESTINATARIO@", this.collaborazioneSPCoopFruitoreSoggetto1.getDestinatario().getTipo()+"/"+this.collaborazioneSPCoopFruitoreSoggetto1.getDestinatario().getNome());
				msg2 = msg2.replace("@SERVIZIO@", CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO+"/"+CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO);
				msg2 = msg2.replace("_@AZIONE@", "");
				msg2 = msg2.replace("@CN@", "Soggetto1");
				Reporter.log("Controllo messaggio (id:"+id+") msg["+msg1+"]");
				Assert.assertTrue(dataMsg.isTracedMessaggio(id, msg1));			
				msg2 = AUTORIZZAZIONE_EFFETTUATA.replace("@IDEGOV@", id);
				msg2 = msg2.replace("@MITTENTE@", this.collaborazioneSPCoopFruitoreSoggetto1.getMittente().getTipo()+"/"+this.collaborazioneSPCoopFruitoreSoggetto1.getMittente().getNome());
				msg2 = msg2.replace("@DESTINATARIO@", this.collaborazioneSPCoopFruitoreSoggetto1.getDestinatario().getTipo()+"/"+this.collaborazioneSPCoopFruitoreSoggetto1.getDestinatario().getNome());
				msg2 = msg2.replace("@SERVIZIO@", CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO+"/"+CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO);
				msg2 = msg2.replace("_@AZIONE@", "");
				msg2 = msg2.replace("@CN@", "Soggetto1");
				Reporter.log("Controllo messaggio (id:"+id+") msg["+msg1+"]");
				Assert.assertTrue(dataMsg.isTracedMessaggio(id, msg1));
			}
			
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
			if(dataMsg!=null){
				dataMsg.close();
			}
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	/***
	 * Test fruitore non assegnato al servizio
	 */
	Repository repositoryFruitoreNonPresente=new Repository();
	@Test(groups={HTTPS.ID_GRUPPO,HTTPS.ID_GRUPPO+".FRUITORE_NON_PRESENTE"},description="Test di tipo sincrono, Viene controllato se i body sono uguali e se gli attachment sono uguali")
	public void FruitoreNonPresente() throws TestSuiteException, IOException, Exception{
		java.io.FileInputStream fin = null;
		DatabaseComponent dbComponentFruitore = null;
		try{
			fin = new java.io.FileInputStream(new File(Utilities.testSuiteProperties.getSoap11FileName()));

			Message msg=new Message(fin);
			msg.getSOAPPartAsBytes();
			
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryFruitoreNonPresente);
			client.setSoapAction("\"TEST\"");
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_AUTORIZZAZIONE_SPCOOP_FRUITORE_NON_PRESENTE);
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
				if(Utilities.testSuiteProperties.isNewConnectionForResponse()==false)
					throw new Exception("Atteso errore");
			} catch (AxisFault error) {
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: ["+error.getFaultString()+"]");
				if(Utilities.testSuiteProperties.isNewConnectionForResponse()==false){
					Reporter.log("Controllo actor code [OpenSPCoop]");
					Assert.assertTrue(CostantiPdD.OPENSPCOOP2.equals(error.getFaultActor()));
					Reporter.log("Controllo fault code [EGOV_IT_201]");
					Assert.assertTrue("EGOV_IT_201".equals(error.getFaultCode().getLocalPart()));
					
					String msg2 = SOAP_FAULT_AUTORIZZAZIONE_SPCOOP_FALLITA.replace("@MITTENTE@", this.collaborazioneSPCoopFruitoreSoggetto1.getMittente().getTipo()+"/"+this.collaborazioneSPCoopFruitoreSoggetto1.getMittente().getNome());
					msg2 = msg2.replace("@DESTINATARIO@", this.collaborazioneSPCoopFruitoreSoggetto1.getDestinatario().getTipo()+"/"+this.collaborazioneSPCoopFruitoreSoggetto1.getDestinatario().getNome());
					msg2 = msg2.replace("@SERVIZIO@", CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY+"/"+CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY);
					msg2 = msg2.replace("_@AZIONE@", "");
									
					String test = error.getFaultString();
					Reporter.log("Controllo fault string ["+msg2+"]["+test+"]");
					Reporter.log("length ["+msg2.length()+"] ["+test.length()+"]");
					
					for(int i=0; i<msg2.length(); i++){
						if(msg2.charAt(i)!=test.charAt(i)){
							if(msg2.charAt(i)!=' '){
								Reporter.log("DIFFF["+msg2.charAt(i)+"]["+test.charAt(i)+"]["+(byte)msg2.charAt(i)+"]("+i+")");
								Assert.assertTrue(false);
							}
						}
					}
					Reporter.log("IDEGOV["+client.getIdMessaggio()+"]");
					this.repositoryFruitoreNonPresente.add(client.getIdMessaggio());
				}else{
					throw error;
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
	@DataProvider (name="FruitoreNonPresente")
	public Object[][]testFruitoreNonPresente()throws Exception{
		String id=this.repositoryFruitoreNonPresente.getNext();
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),null,id,false},	
				{DatabaseProperties.getDatabaseComponentErogatore(),DatabaseProperties.getDatabaseComponentDiagnosticaErogatore(),id,true}
		};
	}
	@Test(groups={HTTPS.ID_GRUPPO,HTTPS.ID_GRUPPO+".FRUITORE_NON_PRESENTE"},dataProvider="FruitoreNonPresente",dependsOnMethods={"FruitoreNonPresente"})
	public void testFruitoreNonPresente(DatabaseComponent data,DatabaseMsgDiagnosticiComponent dataMsg,String id,boolean checkServizioApplicativo) throws Exception{
		try{
			Reporter.log("Controllo tracciamento richiesta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
			Reporter.log("Controllo valore Mittente Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id,CostantiTestSuite.SPCOOP_SOGGETTO_1, null));
			Reporter.log("Controllo valore Destinatario Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedDestinatario(id,CostantiTestSuite.SPCOOP_SOGGETTO_EROGATORE, null));
			Reporter.log("Controllo valore Servizio Busta con id: " +id);
			DatiServizio datiServizio = new DatiServizio(CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY, CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY, CostantiTestSuite.SPCOOP_VERSIONE_SERVIZIO_DEFAULT);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedServizio(id, datiServizio));
			Reporter.log("Controllo valore Azione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedAzione(id, null));
			Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloDiCollaborazione(id, SPCoopCostanti.PROFILO_COLLABORAZIONE_ONEWAY, ProfiloDiCollaborazione.ONEWAY));
			Reporter.log("Numero messaggi arrivati al servizio applicativo: "+data.getVerificatoreTracciaRichiesta().isArrivedCount(id));
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isArrivedCount(id)==0);
			Reporter.log("Controllo lista trasmissione con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedTrasmissione(id, CostantiTestSuite.SPCOOP_SOGGETTO_1, null, CostantiTestSuite.SPCOOP_SOGGETTO_EROGATORE, null));
			Reporter.log("Controllo che la busta abbia generato eccezioni, id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().existsListaEccezioni(id));
			Reporter.log("Controllo che la busta abbia generato l'eccezione " + CodiceErroreCooperazione.SICUREZZA_AUTORIZZAZIONE_FALLITA);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedEccezione(id, CodiceErroreCooperazione.SICUREZZA_AUTORIZZAZIONE_FALLITA));
			Reporter.log("Controllo lista trasmissione con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedTrasmissione(id, CostantiTestSuite.SPCOOP_SOGGETTO_1, null, CostantiTestSuite.SPCOOP_SOGGETTO_EROGATORE, null));
			if(checkServizioApplicativo){
				Reporter.log("Numero messaggi arrivati al servizio applicativo: "+data.getVerificatoreTracciaRichiesta().isArrivedCount(id));
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isArrivedCount(id)==0);
			}
			Reporter.log("----------------------------------------------------------");

			Reporter.log("Controllo tracciamento risposta con riferimento id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTraced(id));
			Reporter.log("Controllo valore Mittente Busta della risposta con riferimento id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedMittente(id, CostantiTestSuite.SPCOOP_SOGGETTO_EROGATORE, null));
			Reporter.log("Controllo valore Destinatario Busta della risposta con riferimento id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedDestinatario(id, CostantiTestSuite.SPCOOP_SOGGETTO_1, null));
			Reporter.log("Controllo valore Servizio Busta della risposta con riferimento id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedServizio(id, datiServizio));
			Reporter.log("Controllo valore Azione Busta della risposta con riferimento id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedAzione(id, null));               
			Reporter.log("Controllo valore Profilo di Collaborazione Busta della risposta con riferimento id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedProfiloDiCollaborazione(id, SPCoopCostanti.PROFILO_COLLABORAZIONE_ONEWAY,ProfiloDiCollaborazione.ONEWAY));
			Reporter.log("Controllo che la busta abbia generato eccezioni, id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().existsListaEccezioni(id));
			Reporter.log("Controllo lista trasmissione, riferimento messaggio: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedTrasmissione(id, CostantiTestSuite.SPCOOP_SOGGETTO_EROGATORE, null,CostantiTestSuite.SPCOOP_SOGGETTO_1, null));

			
			
			// Check msgDiag
			if(dataMsg!=null){
				
				String msg2 = AUTORIZZAZIONE_BUSTE_FALLITA.replace("@IDEGOV@", id);
				msg2 = msg2.replace("@IDEGOV@", msg2);
				msg2 = msg2.replace("@MITTENTE@", this.collaborazioneSPCoopFruitoreSoggetto1.getMittente().getTipo()+"/"+this.collaborazioneSPCoopFruitoreSoggetto1.getMittente().getNome());
				msg2 = msg2.replace("@MITTENTE@", this.collaborazioneSPCoopFruitoreSoggetto1.getMittente().getTipo()+"/"+this.collaborazioneSPCoopFruitoreSoggetto1.getMittente().getNome());
				msg2 = msg2.replace("@DESTINATARIO@", this.collaborazioneSPCoopFruitoreSoggetto1.getDestinatario().getTipo()+"/"+this.collaborazioneSPCoopFruitoreSoggetto1.getDestinatario().getNome());
				msg2 = msg2.replace("@DESTINATARIO@", this.collaborazioneSPCoopFruitoreSoggetto1.getDestinatario().getTipo()+"/"+this.collaborazioneSPCoopFruitoreSoggetto1.getDestinatario().getNome());
				msg2 = msg2.replace("@SERVIZIO@", CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY+"/"+CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY);
				msg2 = msg2.replace("@SERVIZIO@", CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY+"/"+CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY);
				msg2 = msg2.replace("--@AZIONE@", "");
				msg2 = msg2.replace("_@AZIONE@", "");
				msg2 = msg2.replace("@PDD@", "");
				Reporter.log("Controllo Messaggio (id:"+id+") msg["+msg2+"]");
				Assert.assertTrue(dataMsg.isTracedMessaggio(id, msg2));
				
			}
			
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
			if(dataMsg!=null){
				dataMsg.close();
			}
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	/***
	 * Test spoofing rilevato
	 */
	private CooperazioneBaseInformazioni infoFruitoreSoggetto2 = CooperazioneSPCoopBase.getCooperazioneBaseInformazioni(CostantiTestSuite.SPCOOP_SOGGETTO_2,
			CostantiTestSuite.SPCOOP_SOGGETTO_EROGATORE,
			false,SPCoopCostanti.PROFILO_TRASMISSIONE_CON_DUPLICATI,Inoltro.CON_DUPLICATI);	
	private CooperazioneBase collaborazioneSPCoopFruitoreSoggetto2 = 
		new CooperazioneBase(false,SOAPVersion.SOAP11,  this.infoFruitoreSoggetto2, 
				org.openspcoop2.protocol.spcoop.testsuite.core.TestSuiteProperties.getInstance(), 
				DatabaseProperties.getInstance(), SPCoopTestsuiteLogger.getInstance());
	Repository repositorySpoofingRilevato=new Repository();
	@Test(groups={HTTPS.ID_GRUPPO,HTTPS.ID_GRUPPO+".SPOOFING_RILEVATO"},description="Test di tipo sincrono, Viene controllato se i body sono uguali e se gli attachment sono uguali")
	public void SpoofingRilevato() throws TestSuiteException, IOException, Exception{
		
		Date dataInizioTest = DateManager.getDate();
		
		java.io.FileInputStream fin = null;
		DatabaseComponent dbComponentFruitore = null;
		try{
			fin = new java.io.FileInputStream(new File(Utilities.testSuiteProperties.getSoap11FileName()));

			Message msg=new Message(fin);
			msg.getSOAPPartAsBytes();
			
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositorySpoofingRilevato);
			client.setSoapAction("\"TEST\"");
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_AUTORIZZAZIONE_SPCOOP_SPOOFING_RILEVATO);
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
				throw new Exception("Atteso errore");
			} catch (AxisFault error) {
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: ["+error.getFaultString()+"]");
				Reporter.log("Controllo actor code [OpenSPCoop]");
				Assert.assertTrue(CostantiPdD.OPENSPCOOP2.equals(error.getFaultActor()));
				Reporter.log("Controllo fault code [EGOV_IT_201]");
				Assert.assertTrue("EGOV_IT_201".equals(error.getFaultCode().getLocalPart()));
				
				String msg2 = SOAP_FAULT_AUTORIZZAZIONE_SPCOOP_FALLITA.replace("@MITTENTE@", this.collaborazioneSPCoopFruitoreSoggetto2.getMittente().getTipo()+"/"+this.collaborazioneSPCoopFruitoreSoggetto2.getMittente().getNome());
				msg2 = msg2.replace("@DESTINATARIO@", this.collaborazioneSPCoopFruitoreSoggetto2.getDestinatario().getTipo()+"/"+this.collaborazioneSPCoopFruitoreSoggetto2.getDestinatario().getNome());
				msg2 = msg2.replace("@SERVIZIO@", CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO+"/"+CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO);
				msg2 = msg2.replace("_@AZIONE@", "");
								
				String test = error.getFaultString();
				Reporter.log("Controllo fault string ["+msg2+"]["+test+"]");
				Reporter.log("length ["+msg2.length()+"] ["+test.length()+"]");
				
				for(int i=0; i<msg2.length(); i++){
					if(msg2.charAt(i)!=test.charAt(i)){
						if(msg2.charAt(i)!=' '){
							Reporter.log("DIFFF["+msg2.charAt(i)+"]["+test.charAt(i)+"]["+(byte)msg2.charAt(i)+"]("+i+")");
							Assert.assertTrue(false);
						}
					}
				}
				Reporter.log("IDEGOV["+client.getIdMessaggio()+"]");
				this.repositorySpoofingRilevato.add(client.getIdMessaggio());
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
		err.setMsgErrore("subject estratto dal certificato client [CN=Soggetto1, OU=test, O=openspcoop.org, L=Pisa, ST=Italy, C=IT, EMAILADDRESS=apoli@link.it] diverso da quello registrato per la porta di dominio PdDSoggetto2 del mittente [CN=Soggetto2, OU=test, O=openspcoop.org, L=Pisa, ST=Italy, C=IT, EMAILADDRESS=apoli@link.it]");
		this.erroriAttesiOpenSPCoopCore.add(err);
	}
	@DataProvider (name="SpoofingRilevato")
	public Object[][]testSpoofingRilevato()throws Exception{
		String id=this.repositorySpoofingRilevato.getNext();
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),null,id,false},	
				{DatabaseProperties.getDatabaseComponentErogatore(),DatabaseProperties.getDatabaseComponentDiagnosticaErogatore(),id,true}
		};
	}
	@Test(groups={HTTPS.ID_GRUPPO,HTTPS.ID_GRUPPO+".SPOOFING_RILEVATO"},dataProvider="SpoofingRilevato",dependsOnMethods={"SpoofingRilevato"})
	public void testSpoofingRilevato(DatabaseComponent data,DatabaseMsgDiagnosticiComponent dataMsg,String id,boolean checkServizioApplicativo) throws Exception{
		try{
			Reporter.log("Controllo tracciamento richiesta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
			Reporter.log("Controllo valore Mittente Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id,CostantiTestSuite.SPCOOP_SOGGETTO_2, null));
			Reporter.log("Controllo valore Destinatario Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedDestinatario(id,CostantiTestSuite.SPCOOP_SOGGETTO_EROGATORE, null));
			Reporter.log("Controllo valore Servizio Busta con id: " +id);
			DatiServizio datiServizio = new DatiServizio(CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO, CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO, CostantiTestSuite.SPCOOP_VERSIONE_SERVIZIO_DEFAULT);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedServizio(id, datiServizio));
			Reporter.log("Controllo valore Azione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedAzione(id, null));
			Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloDiCollaborazione(id, SPCoopCostanti.PROFILO_COLLABORAZIONE_SINCRONO, ProfiloDiCollaborazione.SINCRONO));
			Reporter.log("Numero messaggi arrivati al servizio applicativo: "+data.getVerificatoreTracciaRichiesta().isArrivedCount(id));
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isArrivedCount(id)==0);
			Reporter.log("Controllo lista trasmissione con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedTrasmissione(id, CostantiTestSuite.SPCOOP_SOGGETTO_2, null, CostantiTestSuite.SPCOOP_SOGGETTO_EROGATORE, null));
			Reporter.log("Controllo che la busta abbia generato eccezioni, id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().existsListaEccezioni(id));
			Reporter.log("Controllo che la busta abbia generato l'eccezione " + CodiceErroreCooperazione.SICUREZZA_AUTORIZZAZIONE_FALLITA);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedEccezione(id, CodiceErroreCooperazione.SICUREZZA_AUTORIZZAZIONE_FALLITA));
			Reporter.log("Controllo lista trasmissione con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedTrasmissione(id, CostantiTestSuite.SPCOOP_SOGGETTO_2, null, CostantiTestSuite.SPCOOP_SOGGETTO_EROGATORE, null));
			if(checkServizioApplicativo){
				Reporter.log("Numero messaggi arrivati al servizio applicativo: "+data.getVerificatoreTracciaRichiesta().isArrivedCount(id));
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isArrivedCount(id)==0);
			}
			Reporter.log("----------------------------------------------------------");

			Reporter.log("Controllo tracciamento risposta con riferimento id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTraced(id));
			Reporter.log("Controllo valore Mittente Busta della risposta con riferimento id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedMittente(id, CostantiTestSuite.SPCOOP_SOGGETTO_EROGATORE, null));
			Reporter.log("Controllo valore Destinatario Busta della risposta con riferimento id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedDestinatario(id, CostantiTestSuite.SPCOOP_SOGGETTO_2, null));
			Reporter.log("Controllo valore Servizio Busta della risposta con riferimento id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedServizio(id, datiServizio));
			Reporter.log("Controllo valore Azione Busta della risposta con riferimento id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedAzione(id, null));               
			Reporter.log("Controllo valore Profilo di Collaborazione Busta della risposta con riferimento id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedProfiloDiCollaborazione(id, SPCoopCostanti.PROFILO_COLLABORAZIONE_SINCRONO, ProfiloDiCollaborazione.SINCRONO));
			Reporter.log("Controllo che la busta abbia generato eccezioni, id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().existsListaEccezioni(id));
			Reporter.log("Controllo lista trasmissione, riferimento messaggio: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedTrasmissione(id, CostantiTestSuite.SPCOOP_SOGGETTO_EROGATORE, null,CostantiTestSuite.SPCOOP_SOGGETTO_2, null));

			
			
			// Check msgDiag
			if(dataMsg!=null){
				
				String msg2 = AUTORIZZAZIONE_BUSTE_FALLITA.replace("@IDEGOV@", id);
				msg2 = msg2.replace("@IDEGOV@", msg2);
				msg2 = msg2.replace("@MITTENTE@", this.collaborazioneSPCoopFruitoreSoggetto2.getMittente().getTipo()+"/"+this.collaborazioneSPCoopFruitoreSoggetto2.getMittente().getNome());
				msg2 = msg2.replace("@MITTENTE@", this.collaborazioneSPCoopFruitoreSoggetto2.getMittente().getTipo()+"/"+this.collaborazioneSPCoopFruitoreSoggetto2.getMittente().getNome());
				msg2 = msg2.replace("@DESTINATARIO@", this.collaborazioneSPCoopFruitoreSoggetto2.getDestinatario().getTipo()+"/"+this.collaborazioneSPCoopFruitoreSoggetto2.getDestinatario().getNome());
				msg2 = msg2.replace("@DESTINATARIO@", this.collaborazioneSPCoopFruitoreSoggetto2.getDestinatario().getTipo()+"/"+this.collaborazioneSPCoopFruitoreSoggetto2.getDestinatario().getNome());
				msg2 = msg2.replace("@SERVIZIO@", CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO+"/"+CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO);
				msg2 = msg2.replace("@SERVIZIO@", CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO+"/"+CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO);
				msg2 = msg2.replace("--@AZIONE@", "");
				msg2 = msg2.replace("_@AZIONE@", "");
				msg2 = msg2.replace("@PDD@", " inviato da un mittente [( SSL Subject: [CN=Soggetto1, OU=test, O=openspcoop.org, L=Pisa, ST=Italy, C=IT, EMAILADDRESS=apoli@link.it] ) ]");
				String XML = msg2 + " (subject estratto dal certificato client [CN=Soggetto1, OU=test, O=openspcoop.org, L=Pisa, ST=Italy, C=IT, EMAILADDRESS=apoli@link.it] diverso da quello registrato per la porta di dominio PdDSoggetto2 del mittente [CN=Soggetto2, OU=test, O=openspcoop.org, L=Pisa, ST=Italy, C=IT, EMAILADDRESS=apoli@link.it])";
				String DB = msg2 + " (subject estratto dal certificato client [CN=Soggetto1, OU=test, O=openspcoop.org, L=Pisa, ST=Italy, C=IT, EMAILADDRESS=apoli@link.it] diverso da quello registrato per la porta di dominio PdDSoggetto2 del mittente [/l=Pisa/st=Italy/ou=test/emailaddress=apoli@link.it/o=openspcoop.org/c=IT/cn=Soggetto2/])";
				Reporter.log("Controllo Messaggio (id:"+id+") msgXML["+XML+"] msgDB["+DB+"]");
				Assert.assertTrue( dataMsg.isTracedMessaggio(id, XML) || dataMsg.isTracedMessaggio(id, DB)  );
				
			}
			
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
			if(dataMsg!=null){
				dataMsg.close();
			}
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/***
	 * Test spoofing rilevato
	 */
	private CooperazioneBaseInformazioni infoFruitoreSoggettoNonAutenticato = CooperazioneSPCoopBase.getCooperazioneBaseInformazioni(CostantiTestSuite.SPCOOP_SOGGETTO_NON_AUTENTICATO,
			CostantiTestSuite.SPCOOP_SOGGETTO_EROGATORE,
			false,SPCoopCostanti.PROFILO_TRASMISSIONE_CON_DUPLICATI,Inoltro.CON_DUPLICATI);	
	private CooperazioneBase collaborazioneSPCoopFruitoreSoggettoNonAutenticato = 
		new CooperazioneBase(false,SOAPVersion.SOAP11,  this.infoFruitoreSoggettoNonAutenticato, 
				org.openspcoop2.protocol.spcoop.testsuite.core.TestSuiteProperties.getInstance(), 
				DatabaseProperties.getInstance(), SPCoopTestsuiteLogger.getInstance());

	Repository repositorySpoofingRilevatoTramiteFruitore=new Repository();
	@Test(groups={HTTPS.ID_GRUPPO,HTTPS.ID_GRUPPO+".SPOOFING_RILEVATO_TRAMITE_FRUIZIONE"},description="Test di tipo sincrono, Viene controllato se i body sono uguali e se gli attachment sono uguali")
	public void SpoofingRilevatoTramiteFruitore() throws TestSuiteException, IOException, Exception{
		
		Date dataInizioTest = DateManager.getDate();
		
		java.io.FileInputStream fin = null;
		DatabaseComponent dbComponentFruitore = null;
		try{
			fin = new java.io.FileInputStream(new File(Utilities.testSuiteProperties.getSoap11FileName()));

			Message msg=new Message(fin);
			msg.getSOAPPartAsBytes();
			
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositorySpoofingRilevatoTramiteFruitore);
			client.setSoapAction("\"TEST\"");
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_AUTORIZZAZIONE_SPCOOP_SPOOFING_RILEVATO_TRAMITE_FRUIZIONE);
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
				if(Utilities.testSuiteProperties.isNewConnectionForResponse()==false)
					throw new Exception("Atteso errore");
			} catch (AxisFault error) {
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: ["+error.getFaultString()+"]");
				if(Utilities.testSuiteProperties.isNewConnectionForResponse()==false){
					Reporter.log("Controllo actor code [OpenSPCoop]");
					Assert.assertTrue(CostantiPdD.OPENSPCOOP2.equals(error.getFaultActor()));
					Reporter.log("Controllo fault code [EGOV_IT_201]");
					Assert.assertTrue("EGOV_IT_201".equals(error.getFaultCode().getLocalPart()));
					
					String msg2 = SOAP_FAULT_AUTORIZZAZIONE_SPCOOP_FALLITA.replace("@MITTENTE@", this.collaborazioneSPCoopFruitoreSoggettoNonAutenticato.getMittente().getTipo()+"/"+this.collaborazioneSPCoopFruitoreSoggettoNonAutenticato.getMittente().getNome());
					msg2 = msg2.replace("@DESTINATARIO@", this.collaborazioneSPCoopFruitoreSoggettoNonAutenticato.getDestinatario().getTipo()+"/"+this.collaborazioneSPCoopFruitoreSoggettoNonAutenticato.getDestinatario().getNome());
					msg2 = msg2.replace("@SERVIZIO@", CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY+"/"+CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY);
					msg2 = msg2.replace("_@AZIONE@", "");
									
					String test = error.getFaultString();
					Reporter.log("Controllo fault string ["+msg2+"]["+test+"]");
					Reporter.log("length ["+msg2.length()+"] ["+test.length()+"]");
					
					for(int i=0; i<msg2.length(); i++){
						if(msg2.charAt(i)!=test.charAt(i)){
							if(msg2.charAt(i)!=' '){
								Reporter.log("DIFFF["+msg2.charAt(i)+"]["+test.charAt(i)+"]["+(byte)msg2.charAt(i)+"]("+i+")");
								Assert.assertTrue(false);
							}
						}
					}
					Reporter.log("IDEGOV["+client.getIdMessaggio()+"]");
					this.repositorySpoofingRilevatoTramiteFruitore.add(client.getIdMessaggio());
				}else{
					throw error;
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
		
		Date dataFineTest = DateManager.getDate();
		ErroreAttesoOpenSPCoopLogCore err = new ErroreAttesoOpenSPCoopLogCore();
		err.setIntervalloInferiore(dataInizioTest);
		err.setIntervalloSuperiore(dataFineTest);
		err.setMsgErrore("subject estratto dal certificato client [CN=Soggetto1, OU=test, O=openspcoop.org, L=Pisa, ST=Italy, C=IT, EMAILADDRESS=apoli@link.it] diverso da quello registrato per la porta di dominio PdDSoggettoNonAutenticato del mittente [CN=sil1,OU=test,O=openspcoop.org,L=Pisa,ST=Italy,C=IT,emailaddress=apoli@link.it]");
		this.erroriAttesiOpenSPCoopCore.add(err);
	}
	@DataProvider (name="SpoofingRilevatoTramiteFruitore")
	public Object[][]testSpoofingRilevatoTramiteFruitore()throws Exception{
		String id=this.repositorySpoofingRilevatoTramiteFruitore.getNext();
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),null,id,false},	
				{DatabaseProperties.getDatabaseComponentErogatore(),DatabaseProperties.getDatabaseComponentDiagnosticaErogatore(),id,true}
		};
	}
	@Test(groups={HTTPS.ID_GRUPPO,HTTPS.ID_GRUPPO+".SPOOFING_RILEVATO_TRAMITE_FRUIZIONE"},dataProvider="SpoofingRilevatoTramiteFruitore",dependsOnMethods={"SpoofingRilevatoTramiteFruitore"})
	public void testSpoofingRilevatoTramiteFruitore(DatabaseComponent data,DatabaseMsgDiagnosticiComponent dataMsg,String id,boolean checkServizioApplicativo) throws Exception{
		try{
			Reporter.log("Controllo tracciamento richiesta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
			Reporter.log("Controllo valore Mittente Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id,CostantiTestSuite.SPCOOP_SOGGETTO_NON_AUTENTICATO, null));
			Reporter.log("Controllo valore Destinatario Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedDestinatario(id,CostantiTestSuite.SPCOOP_SOGGETTO_EROGATORE, null));
			Reporter.log("Controllo valore Servizio Busta con id: " +id);
			DatiServizio datiServizio = new DatiServizio(CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY, CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY, CostantiTestSuite.SPCOOP_VERSIONE_SERVIZIO_DEFAULT);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedServizio(id, datiServizio));
			Reporter.log("Controllo valore Azione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedAzione(id, null));
			Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloDiCollaborazione(id, SPCoopCostanti.PROFILO_COLLABORAZIONE_ONEWAY,ProfiloDiCollaborazione.ONEWAY));
			Reporter.log("Numero messaggi arrivati al servizio applicativo: "+data.getVerificatoreTracciaRichiesta().isArrivedCount(id));
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isArrivedCount(id)==0);
			Reporter.log("Controllo lista trasmissione con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedTrasmissione(id, CostantiTestSuite.SPCOOP_SOGGETTO_NON_AUTENTICATO, null, CostantiTestSuite.SPCOOP_SOGGETTO_EROGATORE, null));
			Reporter.log("Controllo che la busta abbia generato eccezioni, id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().existsListaEccezioni(id));
			Reporter.log("Controllo che la busta abbia generato l'eccezione " + CodiceErroreCooperazione.SICUREZZA_FALSIFICAZIONE_MITTENTE);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedEccezione(id, CodiceErroreCooperazione.SICUREZZA_FALSIFICAZIONE_MITTENTE));
			Reporter.log("Controllo lista trasmissione con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedTrasmissione(id, CostantiTestSuite.SPCOOP_SOGGETTO_NON_AUTENTICATO, null, CostantiTestSuite.SPCOOP_SOGGETTO_EROGATORE, null));
			if(checkServizioApplicativo){
				Reporter.log("Numero messaggi arrivati al servizio applicativo: "+data.getVerificatoreTracciaRichiesta().isArrivedCount(id));
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isArrivedCount(id)==0);
			}
			Reporter.log("----------------------------------------------------------");

			Reporter.log("Controllo tracciamento risposta con riferimento id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTraced(id));
			Reporter.log("Controllo valore Mittente Busta della risposta con riferimento id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedMittente(id, CostantiTestSuite.SPCOOP_SOGGETTO_EROGATORE, null));
			Reporter.log("Controllo valore Destinatario Busta della risposta con riferimento id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedDestinatario(id, CostantiTestSuite.SPCOOP_SOGGETTO_NON_AUTENTICATO, null));
			Reporter.log("Controllo valore Servizio Busta della risposta con riferimento id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedServizio(id, datiServizio));
			Reporter.log("Controllo valore Azione Busta della risposta con riferimento id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedAzione(id, null));               
			Reporter.log("Controllo valore Profilo di Collaborazione Busta della risposta con riferimento id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedProfiloDiCollaborazione(id, SPCoopCostanti.PROFILO_COLLABORAZIONE_ONEWAY,ProfiloDiCollaborazione.ONEWAY));
			Reporter.log("Controllo che la busta abbia generato eccezioni, id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().existsListaEccezioni(id));
			Reporter.log("Controllo lista trasmissione, riferimento messaggio: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedTrasmissione(id, CostantiTestSuite.SPCOOP_SOGGETTO_EROGATORE, null,
					CostantiTestSuite.SPCOOP_SOGGETTO_NON_AUTENTICATO, null));

			
			
			// Check msgDiag
			if(dataMsg!=null){
				String msg2 = AUTORIZZAZIONE_BUSTE_FALLITA.replace("@IDEGOV@", id);
				msg2 = msg2.replace("@IDEGOV@", msg2);
				msg2 = msg2.replace("@MITTENTE@", this.collaborazioneSPCoopFruitoreSoggettoNonAutenticato.getMittente().getTipo()+"/"+this.collaborazioneSPCoopFruitoreSoggettoNonAutenticato.getMittente().getNome());
				msg2 = msg2.replace("@MITTENTE@", this.collaborazioneSPCoopFruitoreSoggettoNonAutenticato.getMittente().getTipo()+"/"+this.collaborazioneSPCoopFruitoreSoggettoNonAutenticato.getMittente().getNome());
				msg2 = msg2.replace("@DESTINATARIO@", this.collaborazioneSPCoopFruitoreSoggettoNonAutenticato.getDestinatario().getTipo()+"/"+this.collaborazioneSPCoopFruitoreSoggettoNonAutenticato.getDestinatario().getNome());
				msg2 = msg2.replace("@DESTINATARIO@", this.collaborazioneSPCoopFruitoreSoggettoNonAutenticato.getDestinatario().getTipo()+"/"+this.collaborazioneSPCoopFruitoreSoggettoNonAutenticato.getDestinatario().getNome());
				msg2 = msg2.replace("@SERVIZIO@", CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY+"/"+CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY);
				msg2 = msg2.replace("@SERVIZIO@", CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY+"/"+CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY);
				msg2 = msg2.replace("--@AZIONE@", "");
				msg2 = msg2.replace("_@AZIONE@", "");
				msg2 = msg2.replace("@PDD@", " inviato da un mittente [( SSL Subject: [CN=Soggetto1, OU=test, O=openspcoop.org, L=Pisa, ST=Italy, C=IT, EMAILADDRESS=apoli@link.it] ) ]");
				// Versione XML
				String XML = msg2 + " (subject estratto dal certificato client [CN=Soggetto1, OU=test, O=openspcoop.org, L=Pisa, ST=Italy, C=IT, EMAILADDRESS=apoli@link.it] diverso da quello registrato per la porta di dominio PdDSoggettoNonAutenticato del mittente [CN=sil1,OU=test,O=openspcoop.org,L=Pisa,ST=Italy,C=IT,emailaddress=apoli@link.it])";
				// Versione DB
				String DB = msg2 + " (subject estratto dal certificato client [CN=Soggetto1, OU=test, O=openspcoop.org, L=Pisa, ST=Italy, C=IT, EMAILADDRESS=apoli@link.it] diverso da quello registrato per la porta di dominio PdDSoggettoNonAutenticato del mittente [/l=Pisa/st=Italy/ou=test/emailaddress=apoli@link.it/o=openspcoop.org/c=IT/cn=sil1/])";
				Reporter.log("Controllo Messaggio (id:"+id+") XML["+XML+"] DB["+DB+"]");
				Assert.assertTrue( dataMsg.isTracedMessaggio(id, XML) || dataMsg.isTracedMessaggio(id, DB) );
				
			}
			
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
			if(dataMsg!=null){
				dataMsg.close();
			}
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/***
	 * Test autorizzazione SPCoop disabilitato
	 */
	Repository repositoryHTTPSAutorizzazioneSPCoopDisabilitata=new Repository();
	@Test(groups={HTTPS.ID_GRUPPO,HTTPS.ID_GRUPPO+".AUTORIZZAZIONE_SPCOOP_DISABILITATA"},description="Test di tipo sincrono, Viene controllato se i body sono uguali e se gli attachment sono uguali")
	public void httpsAutorizzazioneSPCoopDisabilitata() throws TestSuiteException, IOException, SOAPException{
		this.collaborazioneSPCoopFruitoreSoggettoNonAutenticato.sincrono(this.repositoryHTTPSAutorizzazioneSPCoopDisabilitata,
				CostantiTestSuite.PORTA_DELEGATA_AUTORIZZAZIONE_SPCOOP_DISABILITATA,addIDUnivoco);
	}
	@DataProvider (name="httpsAutorizzazioneSPCoopDisabilitata")
	public Object[][]testHttpsAutorizzazioneSPCoopDisabilitata()throws Exception{
		String id=this.repositoryHTTPSAutorizzazioneSPCoopDisabilitata.getNext();
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),null,id,false},	
				{DatabaseProperties.getDatabaseComponentErogatore(),DatabaseProperties.getDatabaseComponentDiagnosticaErogatore(),id,true}	
		};
	}
	@Test(groups={HTTPS.ID_GRUPPO,HTTPS.ID_GRUPPO+".AUTORIZZAZIONE_SPCOOP_DISABILITATA"},dataProvider="httpsAutorizzazioneSPCoopDisabilitata",dependsOnMethods={"httpsAutorizzazioneSPCoopDisabilitata"})
	public void testHttpsAutorizzazioneSPCoopDisabilitata(DatabaseComponent data,DatabaseMsgDiagnosticiComponent dataMsg,String id,boolean checkServizioApplicativo) throws Exception{
		try{
			this.collaborazioneSPCoopFruitoreSoggettoNonAutenticato.testSincrono(data, id, CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,null, 
					checkServizioApplicativo,null);
			
			// Check msgDiag
			if(dataMsg!=null){
				String msg1 = MESSAGGIO_SPCOOP_RICEVUTO.replace("@IDEGOV@", id);
				msg1 = msg1.replace("@MITTENTE@", this.collaborazioneSPCoopFruitoreSoggettoNonAutenticato.getMittente().getTipo()+"/"+this.collaborazioneSPCoopFruitoreSoggettoNonAutenticato.getMittente().getNome());
				msg1 = msg1.replace("@CN@", "Soggetto1");
				Reporter.log("Controllo messaggio (id:"+id+") msg["+msg1+"]");
				Assert.assertTrue(dataMsg.isTracedMessaggio(id, msg1));
				
				String msg2 = AUTORIZZAZIONE_IN_CORSO.replace("@IDEGOV@", id);
				msg2 = msg2.replace("@MITTENTE@", this.collaborazioneSPCoopFruitoreSoggettoNonAutenticato.getMittente().getTipo()+"/"+this.collaborazioneSPCoopFruitoreSoggettoNonAutenticato.getMittente().getNome());
				msg2 = msg2.replace("@DESTINATARIO@", this.collaborazioneSPCoopFruitoreSoggettoNonAutenticato.getDestinatario().getTipo()+"/"+this.collaborazioneSPCoopFruitoreSoggettoNonAutenticato.getDestinatario().getNome());
				msg2 = msg2.replace("@SERVIZIO@", CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO+"/"+CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO);
				msg2 = msg2.replace("_@AZIONE@", "");
				msg2 = msg2.replace("@CN@", "Soggetto1");
				Reporter.log("Controllo messaggio (id:"+id+") msg["+msg1+"]");
				Assert.assertTrue(dataMsg.isTracedMessaggio(id, msg1));			
				msg2 = AUTORIZZAZIONE_EFFETTUATA.replace("@IDEGOV@", id);
				msg2 = msg2.replace("@MITTENTE@", this.collaborazioneSPCoopFruitoreSoggettoNonAutenticato.getMittente().getTipo()+"/"+this.collaborazioneSPCoopFruitoreSoggettoNonAutenticato.getMittente().getNome());
				msg2 = msg2.replace("@DESTINATARIO@", this.collaborazioneSPCoopFruitoreSoggettoNonAutenticato.getDestinatario().getTipo()+"/"+this.collaborazioneSPCoopFruitoreSoggettoNonAutenticato.getDestinatario().getNome());
				msg2 = msg2.replace("@SERVIZIO@", CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO+"/"+CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO);
				msg2 = msg2.replace("_@AZIONE@", "");
				msg2 = msg2.replace("@CN@", "Soggetto1");
				msg2 = msg2 + " (client-auth disabilitato nella porta di dominio PdDSoggettoNonAutenticato)";
				Reporter.log("Controllo messaggio (id:"+id+") msg["+msg1+"]");
				Assert.assertTrue(dataMsg.isTracedMessaggio(id, msg1));
			}
			
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
			if(dataMsg!=null){
				dataMsg.close();
			}
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/***
	 * Test autorizzazione SPCoop disabilitato nel fruitore disabilitato
	 */
	Repository repositoryHTTPSAutorizzazioneSPCoopDisabilitataTramiteFruizione=new Repository();
	@Test(groups={HTTPS.ID_GRUPPO,HTTPS.ID_GRUPPO+".AUTORIZZAZIONE_SPCOOP_DISABILITATA_TRAMITE_FRUZIONE"},description="Test di tipo sincrono, Viene controllato se i body sono uguali e se gli attachment sono uguali")
	public void httpsAutorizzazioneSPCoopDisabilitataTramiteFruizione() throws TestSuiteException, IOException, SOAPException{
		this.collaborazioneSPCoopFruitoreSoggetto2.sincrono(this.repositoryHTTPSAutorizzazioneSPCoopDisabilitataTramiteFruizione,
				CostantiTestSuite.PORTA_DELEGATA_AUTORIZZAZIONE_SPCOOP_DISABILITATA_TRAMITE_FRUIZIONE,addIDUnivoco,true);
	}
	@DataProvider (name="httpsAutorizzazioneSPCoopDisabilitataTramiteFruizione")
	public Object[][]testHttpsAutorizzazioneSPCoopDisabilitataTramiteFruizione()throws Exception{
		String id=this.repositoryHTTPSAutorizzazioneSPCoopDisabilitataTramiteFruizione.getNext();
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),null,id,false},	
				{DatabaseProperties.getDatabaseComponentErogatore(),DatabaseProperties.getDatabaseComponentDiagnosticaErogatore(),id,true}	
		};
	}
	@Test(groups={HTTPS.ID_GRUPPO,HTTPS.ID_GRUPPO+".AUTORIZZAZIONE_SPCOOP_DISABILITATA_TRAMITE_FRUZIONE"},dataProvider="httpsAutorizzazioneSPCoopDisabilitataTramiteFruizione",dependsOnMethods={"httpsAutorizzazioneSPCoopDisabilitataTramiteFruizione"})
	public void testHttpsAutorizzazioneSPCoopDisabilitataTramiteFruizione(DatabaseComponent data,DatabaseMsgDiagnosticiComponent dataMsg,String id,boolean checkServizioApplicativo) throws Exception{
		try{
			this.collaborazioneSPCoopFruitoreSoggetto2.testOneWay(data, id, CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY,null, 
					checkServizioApplicativo,null);
			
			// Check msgDiag
			if(dataMsg!=null){
				String msg1 = MESSAGGIO_SPCOOP_RICEVUTO.replace("@IDEGOV@", id);
				msg1 = msg1.replace("@MITTENTE@", this.collaborazioneSPCoopFruitoreSoggetto2.getMittente().getTipo()+"/"+this.collaborazioneSPCoopFruitoreSoggetto2.getMittente().getNome());
				msg1 = msg1.replace("@CN@", "Soggetto1");
				Reporter.log("Controllo messaggio (id:"+id+") msg["+msg1+"]");
				Assert.assertTrue(dataMsg.isTracedMessaggio(id, msg1));
				
				String msg2 = AUTORIZZAZIONE_IN_CORSO.replace("@IDEGOV@", id);
				msg2 = msg2.replace("@MITTENTE@", this.collaborazioneSPCoopFruitoreSoggetto2.getMittente().getTipo()+"/"+this.collaborazioneSPCoopFruitoreSoggetto2.getMittente().getNome());
				msg2 = msg2.replace("@DESTINATARIO@", this.collaborazioneSPCoopFruitoreSoggetto2.getDestinatario().getTipo()+"/"+this.collaborazioneSPCoopFruitoreSoggetto2.getDestinatario().getNome());
				msg2 = msg2.replace("@SERVIZIO@", CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY+"/"+CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY);
				msg2 = msg2.replace("_@AZIONE@", "");
				msg2 = msg2.replace("@CN@", "Soggetto1");
				Reporter.log("Controllo messaggio (id:"+id+") msg["+msg1+"]");
				Assert.assertTrue(dataMsg.isTracedMessaggio(id, msg1));			
				msg2 = AUTORIZZAZIONE_EFFETTUATA.replace("@IDEGOV@", id);
				msg2 = msg2.replace("@MITTENTE@", this.collaborazioneSPCoopFruitoreSoggetto2.getMittente().getTipo()+"/"+this.collaborazioneSPCoopFruitoreSoggetto2.getMittente().getNome());
				msg2 = msg2.replace("@DESTINATARIO@", this.collaborazioneSPCoopFruitoreSoggetto2.getDestinatario().getTipo()+"/"+this.collaborazioneSPCoopFruitoreSoggetto2.getDestinatario().getNome());
				msg2 = msg2.replace("@SERVIZIO@", CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY+"/"+CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY);
				msg2 = msg2.replace("_@AZIONE@", "");
				msg2 = msg2.replace("@CN@", "Soggetto1");
				msg2 = msg2 + " (client-auth disabilitato nella fruizione)";
				Reporter.log("Controllo messaggio (id:"+id+") msg["+msg1+"]");
				Assert.assertTrue(dataMsg.isTracedMessaggio(id, msg1));
			}
			
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
			if(dataMsg!=null){
				dataMsg.close();
			}
		}
	}
	
	
	
	
	
	
	
	
	
	/***
	 * Test per il profilo di collaborazione Sincrono con consegna HTTPS
	 */
	Repository repositorySincronoConsegnaHTTPS=new Repository();
	@Test(groups={HTTPS.ID_GRUPPO,HTTPS.ID_GRUPPO+".CONSEGNA_SIL_HTTPS"},description="Test di tipo sincrono, Viene controllato se i body sono uguali e se gli attachment sono uguali")
	public void sincronoConsegnaHTTPS() throws TestSuiteException, IOException, SOAPException{
		this.collaborazioneSPCoopBase.sincrono(this.repositorySincronoConsegnaHTTPS,CostantiTestSuite.PORTA_DELEGATA_HTTPS_SIL_CONSEGNA,addIDUnivoco);
	}
	@DataProvider (name="SincronoConsegnaHTTPS")
	public Object[][]testSincronoConsegnaHTTPS()throws Exception{
		String id=this.repositorySincronoConsegnaHTTPS.getNext();
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),null,id,false},	
				{DatabaseProperties.getDatabaseComponentErogatore(),DatabaseProperties.getDatabaseComponentDiagnosticaErogatore(),id,true}	
		};
	}
	@Test(groups={HTTPS.ID_GRUPPO,HTTPS.ID_GRUPPO+".CONSEGNA_SIL_HTTPS"},dataProvider="SincronoConsegnaHTTPS",dependsOnMethods={"sincronoConsegnaHTTPS"})
	public void testSincronoConsegnaHTTPS(DatabaseComponent data,DatabaseMsgDiagnosticiComponent dataMsg,String id,boolean checkServizioApplicativo) throws Exception{
		try{
			this.collaborazioneSPCoopBase.testSincrono(data, id, CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_HTTPS_SIL_CONSEGNA, checkServizioApplicativo,
					null);
			
			// Check msgDiag
			if(dataMsg!=null){
				Assert.assertTrue(dataMsg.isTracedMessaggioWithLike(id, "https"));
			}
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
			if(dataMsg!=null){
				dataMsg.close();
			}
		}
	}
}
