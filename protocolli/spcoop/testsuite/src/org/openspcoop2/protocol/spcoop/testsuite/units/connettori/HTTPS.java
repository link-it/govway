/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it). 
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



package org.openspcoop2.protocol.spcoop.testsuite.units.connettori;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import javax.xml.soap.SOAPException;

import org.apache.axis.AxisFault;
import org.apache.axis.Message;
import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.protocol.sdk.constants.CodiceErroreCooperazione;
import org.openspcoop2.protocol.sdk.constants.CodiceErroreIntegrazione;
import org.openspcoop2.protocol.sdk.constants.Inoltro;
import org.openspcoop2.protocol.sdk.constants.IntegrationFunctionError;
import org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione;
import org.openspcoop2.protocol.spcoop.constants.SPCoopCostanti;
import org.openspcoop2.protocol.spcoop.testsuite.core.CooperazioneSPCoopBase;
import org.openspcoop2.protocol.spcoop.testsuite.core.CostantiTestSuite;
import org.openspcoop2.protocol.spcoop.testsuite.core.DatabaseProperties;
import org.openspcoop2.protocol.spcoop.testsuite.core.FileSystemUtilities;
import org.openspcoop2.protocol.spcoop.testsuite.core.SPCoopTestsuiteLogger;
import org.openspcoop2.protocol.spcoop.testsuite.core.Utilities;
import org.openspcoop2.protocol.utils.ErroriProperties;
import org.openspcoop2.testsuite.clients.ClientHttpGenerico;
import org.openspcoop2.testsuite.core.ErroreAttesoOpenSPCoopLogCore;
import org.openspcoop2.testsuite.core.Repository;
import org.openspcoop2.testsuite.core.TestSuiteException;
import org.openspcoop2.testsuite.db.DatabaseComponent;
import org.openspcoop2.testsuite.db.DatabaseMsgDiagnosticiComponent;
import org.openspcoop2.testsuite.db.DatiServizio;
import org.openspcoop2.testsuite.units.CooperazioneBase;
import org.openspcoop2.testsuite.units.CooperazioneBaseInformazioni;
import org.openspcoop2.testsuite.units.GestioneViaJmx;
import org.openspcoop2.utils.date.DateManager;
import org.slf4j.Logger;
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
public class HTTPS extends GestioneViaJmx {

	/** Identificativo del gruppo */
	public static final String ID_GRUPPO = "HTTPS";
	
	
	private Logger log = SPCoopTestsuiteLogger.getInstance();
	
	protected HTTPS() {
		super(org.openspcoop2.protocol.spcoop.testsuite.core.TestSuiteProperties.getInstance());
	}
	
	
	
	
	/** Gestore della Collaborazione di Base */
	private CooperazioneBaseInformazioni info = CooperazioneSPCoopBase.getCooperazioneBaseInformazioni(CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE,
			CostantiTestSuite.SPCOOP_SOGGETTO_EROGATORE,
			false,SPCoopCostanti.PROFILO_TRASMISSIONE_CON_DUPLICATI,Inoltro.CON_DUPLICATI);	
	private CooperazioneBase collaborazioneSPCoopBase = 
		new CooperazioneBase(false,MessageType.SOAP_11,  this.info, 
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
	private static final String MESSAGGIO_SPCOOP_RICEVUTO = "Ricevuto messaggio di cooperazione con identificativo [@IDEGOV@] inviato dalla parte mittente [@MITTENTE@] ( SSL-Subject 'CN=@CN@, OU=test, O=openspcoop.org, L=Pisa, ST=Italy, C=IT, EMAILADDRESS=apoli@link.it' ) ";
	private static final String AUTORIZZAZIONE_PREFIX_SENZA_AUTENTICAZIONE = "Verifica autorizzazione [authenticated] messaggio con identificativo [@IDEGOV@] fruitore [@MITTENTE@] -> servizio [@DESTINATARIO@:@SERVIZIO@:1:@AZIONE@] credenzialiMittente ( SSL-Subject 'CN=@CN@, OU=test, O=openspcoop.org, L=Pisa, ST=Italy, C=IT, EMAILADDRESS=apoli@link.it' )";
	private static final String AUTORIZZAZIONE_IN_CORSO_SENZA_AUTENTICAZIONE = AUTORIZZAZIONE_PREFIX_SENZA_AUTENTICAZIONE+" in corso ...";
	private static final String AUTORIZZAZIONE_EFFETTUATA_SENZA_AUTENTICAZIONE = AUTORIZZAZIONE_PREFIX_SENZA_AUTENTICAZIONE+" completata con successo";
	
	private static final String MESSAGGIO_SPCOOP_RICEVUTO_AUTENTICAZIONE_HTTPS_IN_CORSO = "Autenticazione [ssl] in corso ( SSL-Subject 'CN=@CN@, OU=test, O=openspcoop.org, L=Pisa, ST=Italy, C=IT, EMAILADDRESS=apoli@link.it' ) ...";
	private static final String MESSAGGIO_SPCOOP_RICEVUTO_AUTENTICAZIONE_HTTPS_SUCCESS_PREFIX = "Autenticazione [ssl] effettuata con successo";
	private static final String MESSAGGIO_SPCOOP_RICEVUTO_AUTENTICAZIONE_HTTPS = "Ricevuto messaggio di cooperazione con identificativo [@IDEGOV@] inviato dalla parte mittente [@MITTENTE@]";
	private static final String AUTORIZZAZIONE_PREFIX_AUTENTICAZIONE_HTTPS = "Verifica autorizzazione [authenticated] messaggio con identificativo [@IDEGOV@] fruitore [@MITTENTE@] -> servizio [@DESTINATARIO@:@SERVIZIO@:1:@AZIONE@]";
	private static final String AUTORIZZAZIONE_IN_CORSO_AUTENTICAZIONE_HTTPS = AUTORIZZAZIONE_PREFIX_AUTENTICAZIONE_HTTPS+" in corso ...";
	private static final String AUTORIZZAZIONE_EFFETTUATA_AUTENTICAZIONE_HTTPS= AUTORIZZAZIONE_PREFIX_AUTENTICAZIONE_HTTPS+" completata con successo";
	
	
	private static final String AUTORIZZAZIONE_BUSTE_FALLITA = "Verifica autorizzazione [authenticated] messaggio con identificativo [@IDEGOV@] fruitore [@MITTENTE@] -> servizio [@DESTINATARIO@:@SERVIZIO@:1:@AZIONE@]@PDD@ fallita (codice: EGOV_IT_201) Il soggetto @MITTENTE@ non è autorizzato ad invocare il servizio @SERVIZIO@_@AZIONE@ (versione:1) erogato da @DESTINATARIO@";
	private static final String NON_AUTORIZZATO = "Il soggetto @MITTENTE@ non è autorizzato ad invocare il servizio @SERVIZIO@_@AZIONE@ (versione:1) erogato da @DESTINATARIO@";
	private static final String _SOAP_FAULT_AUTORIZZAZIONE_SPCOOP_FALLITA = "@DESTINATARIO@ ha rilevato le seguenti eccezioni: "+NON_AUTORIZZATO;
	private static final String SOAP_FAULT_AUTORIZZAZIONE_SPCOOP_FALLITA_SUBJECT_NON_PRESENTE = _SOAP_FAULT_AUTORIZZAZIONE_SPCOOP_FALLITA;
			//+" (subject della porta di dominio che ha inviato la busta non presente (https attivo?, client-auth attivo?))";
	
	private static final String AUTENTICAZIONE_FALLITA_MITTENTE = "@DESTINATARIO@ ha rilevato le seguenti eccezioni: Mittente/IdentificativoParte";
	private static final String AUTENTICAZIONE_FALLITA_CREDENZIALI_NON_FORNITE = "@DESTINATARIO@ ha rilevato le seguenti eccezioni: Autenticazione fallita, credenziali non fornite";
	
	private static final String AUTENTICAZIONE_FALLITA_SPOOFING_PREFIX = "Autenticazione [ssl] fallita ( SSL-Subject '@SUBJECT@' ) : [RicezioneBuste] processo di autenticazione [ssl] fallito,";
	
	private static final String PDD_NON_DISPONIBILE = "Servizio erogato dal Soggetto @DESTINATARIO@ non disponibile";
	private static final String BAD_CERTIFICATE = "bad_certificate";
	private static final String BAD_CERTIFICATE_2 = "Remote host closed connection during handshake";
	private static final String BAD_CERTIFICATE_3 = "Connection reset by peer";
	private static final String BAD_CERTIFICATE_4 = "readHandshakeRecord";
	private static final String BAD_CERTIFICATE_5 = "Broken pipe (Write failed)";
	private static final String CA_NON_PRESENTE = "unable to find valid certification path to requested target";
	private static final String HOST_VERIFY = "No subject alternative names present";
	private static final String HOST_VERIFY_2 = "HTTPS hostname wrong:  should be <127.0.0.1>";
	
	private static final String CREDENZIALI_NON_FORNITE = "Autenticazione fallita, credenziali non fornite";
	//private static final String CREDENZIALI_NON_CORRETTE = "Autenticazione del servizio applicativo non riuscita ( SSL Subject: CN=@SIL@, OU=test, O=openspcoop.org, L=Pisa, ST=Italy, C=IT, EMAILADDRESS=apoli@link.it ) : Credenziali fornite non corrette: subject[CN=@SIL@, OU=test, O=openspcoop.org, L=Pisa, ST=Italy, C=IT, EMAILADDRESS=apoli@link.it]";
	@SuppressWarnings("unused")
	private static final String CREDENZIALI_NON_CORRETTE = "Autenticazione fallita, credenziali fornite non corrette";
	//private static final String CREDENZIALI_NON_CORRETTE = "Autenticazione del servizio applicativo non riuscita ( SSL Subject: [CN=@SIL@, OU=test, O=openspcoop.org, L=Pisa, ST=Italy, C=IT, EMAILADDRESS=apoli@link.it] ) : Credenziali fornite non corrette";
	private static final String AUTORIZZAZIONE_FALLITA = "Il servizio applicativo @SILNAME@ non risulta autorizzato a fruire del servizio richiesto";
	//private static final String AUTORIZZAZIONE_FALLITA = "Autorizzazione non concessa al servizio applicativo [@SILNAME@] di utilizzare la porta delegata [@PD@]: Servizio non invocabile dal servizio applicativo @SILNAME@";
	

	private static final String AUTH_SSL_IN_CORSO = "Autenticazione [ssl] in corso ( SSL-Subject 'CN=@SIL@, OU=test, O=openspcoop.org, L=Pisa, ST=Italy, C=IT, EMAILADDRESS=apoli@link.it' ) ...";
	private static final String AUTH_SSL_EFFETTUATA = "Autenticazione [ssl] effettuata con successo"; 
	private static final String SA_RICONOSCIUTO = "Ricevuta richiesta di servizio dal Servizio Applicativo @SILNAME@ verso la porta delegata @PD@";
	

	/***
	 * Test https con autenticazione client
	 */
	Repository repositoryHTTPSWithClientAuth=new Repository();
	@Test(groups={CostantiConnettori.ID_GRUPPO_CONNETTORI,HTTPS.ID_GRUPPO,HTTPS.ID_GRUPPO+".WITH_CLIENT_AUTH"},description="Test di tipo sincrono, Viene controllato se i body sono uguali e se gli attachment sono uguali")
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
	@Test(groups={CostantiConnettori.ID_GRUPPO_CONNETTORI,HTTPS.ID_GRUPPO,HTTPS.ID_GRUPPO+".WITH_CLIENT_AUTH"},dataProvider="httpsWithClientAuth",dependsOnMethods={"httpsWithClientAuth"})
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
				
				String msg2 = AUTORIZZAZIONE_IN_CORSO_SENZA_AUTENTICAZIONE.replace("@IDEGOV@", id);
				msg2 = msg2.replace("@MITTENTE@", this.collaborazioneSPCoopBase.getMittente().getTipo()+"/"+this.collaborazioneSPCoopBase.getMittente().getNome());
				msg2 = msg2.replace("@DESTINATARIO@", this.collaborazioneSPCoopBase.getDestinatario().getTipo()+"/"+this.collaborazioneSPCoopBase.getDestinatario().getNome());
				msg2 = msg2.replace("@SERVIZIO@", CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO+"/"+CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO);
				msg2 = msg2.replace("@AZIONE@", CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_HTTPS_WITH_CLIENT_AUTH);
				msg2 = msg2.replace("@CN@", "Soggetto1");
				Reporter.log("Controllo messaggio (id:"+id+") msg["+msg2+"]");
				Assert.assertTrue(dataMsg.isTracedMessaggioWithLike(id, msg2));			
				msg2 = AUTORIZZAZIONE_EFFETTUATA_SENZA_AUTENTICAZIONE.replace("@IDEGOV@", id);
				msg2 = msg2.replace("@MITTENTE@", this.collaborazioneSPCoopBase.getMittente().getTipo()+"/"+this.collaborazioneSPCoopBase.getMittente().getNome());
				msg2 = msg2.replace("@DESTINATARIO@", this.collaborazioneSPCoopBase.getDestinatario().getTipo()+"/"+this.collaborazioneSPCoopBase.getDestinatario().getNome());
				msg2 = msg2.replace("@SERVIZIO@", CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO+"/"+CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO);
				msg2 = msg2.replace("@AZIONE@", CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_HTTPS_WITH_CLIENT_AUTH);
				msg2 = msg2.replace("@CN@", "Soggetto1");
				Reporter.log("Controllo messaggio (id:"+id+") msg["+msg2+"]");
				Assert.assertTrue(dataMsg.isTracedMessaggioWithLike(id, msg2));
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
	@Test(groups={CostantiConnettori.ID_GRUPPO_CONNETTORI,HTTPS.ID_GRUPPO,HTTPS.ID_GRUPPO+".WITH_CLIENT_AUTH_IDENTITA2"},description="Test di tipo sincrono, Viene controllato se i body sono uguali e se gli attachment sono uguali")
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
	@Test(groups={CostantiConnettori.ID_GRUPPO_CONNETTORI,HTTPS.ID_GRUPPO,HTTPS.ID_GRUPPO+".WITH_CLIENT_AUTH_IDENTITA2"},dataProvider="httpsWithClientAuthIdentita2",dependsOnMethods={"httpsWithClientAuthIdentita2"})
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
				
				String msg2 = AUTORIZZAZIONE_IN_CORSO_SENZA_AUTENTICAZIONE.replace("@IDEGOV@", id);
				msg2 = msg2.replace("@MITTENTE@", this.collaborazioneSPCoopBase.getMittente().getTipo()+"/"+this.collaborazioneSPCoopBase.getMittente().getNome());
				msg2 = msg2.replace("@DESTINATARIO@", this.collaborazioneSPCoopBase.getDestinatario().getTipo()+"/"+this.collaborazioneSPCoopBase.getDestinatario().getNome());
				msg2 = msg2.replace("@SERVIZIO@", CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO+"/"+CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO);
				msg2 = msg2.replace("@AZIONE@", CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_HTTPS_WITH_CLIENT_AUTH_IDENTITA2);
				msg2 = msg2.replace("@CN@", "Soggetto2");
				Reporter.log("Controllo messaggio (id:"+id+") msg["+msg2+"]");
				Assert.assertTrue(dataMsg.isTracedMessaggioWithLike(id, msg2));			
				msg2 = AUTORIZZAZIONE_EFFETTUATA_SENZA_AUTENTICAZIONE.replace("@IDEGOV@", id);
				msg2 = msg2.replace("@MITTENTE@", this.collaborazioneSPCoopBase.getMittente().getTipo()+"/"+this.collaborazioneSPCoopBase.getMittente().getNome());
				msg2 = msg2.replace("@DESTINATARIO@", this.collaborazioneSPCoopBase.getDestinatario().getTipo()+"/"+this.collaborazioneSPCoopBase.getDestinatario().getNome());
				msg2 = msg2.replace("@SERVIZIO@", CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO+"/"+CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO);
				msg2 = msg2.replace("@AZIONE@", CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_HTTPS_WITH_CLIENT_AUTH_IDENTITA2);
				msg2 = msg2.replace("@CN@", "Soggetto2");
				Reporter.log("Controllo messaggio (id:"+id+") msg["+msg2+"]");
				Assert.assertTrue(dataMsg.isTracedMessaggioWithLike(id, msg2));
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
	@Test(groups={CostantiConnettori.ID_GRUPPO_CONNETTORI,HTTPS.ID_GRUPPO,HTTPS.ID_GRUPPO+".WITH_CLIENT_AUTH_ERROR"},description="Test di tipo sincrono, Viene controllato se i body sono uguali e se gli attachment sono uguali")
	public void httpsWithClientAuthError_genericCode_wrap() throws TestSuiteException, IOException, Exception{
		boolean genericCode = true;
		boolean unwrap = false;
		try {
			super.lockForCode(genericCode, unwrap);
			
			_httpsWithClientAuthError(genericCode, unwrap);
		}finally {
			super.unlockForCode(genericCode);
		}
	}
	@Test(groups={CostantiConnettori.ID_GRUPPO_CONNETTORI,HTTPS.ID_GRUPPO,HTTPS.ID_GRUPPO+".WITH_CLIENT_AUTH_ERROR"},description="Test di tipo sincrono, Viene controllato se i body sono uguali e se gli attachment sono uguali")
	public void httpsWithClientAuthError_genericCode_unwrap() throws TestSuiteException, IOException, Exception{
		boolean genericCode = true;
		boolean unwrap = true;
		try {
			super.lockForCode(genericCode, unwrap);
			
			_httpsWithClientAuthError(genericCode, unwrap);
		}finally {
			super.unlockForCode(genericCode);
		}
	}
	@Test(groups={CostantiConnettori.ID_GRUPPO_CONNETTORI,HTTPS.ID_GRUPPO,HTTPS.ID_GRUPPO+".WITH_CLIENT_AUTH_ERROR"},description="Test di tipo sincrono, Viene controllato se i body sono uguali e se gli attachment sono uguali")
	public void httpsWithClientAuthError_specificCode() throws TestSuiteException, IOException, Exception{
		boolean genericCode = false;
		boolean unwrap = false;
		try {
			super.lockForCode(genericCode, unwrap);
			
			_httpsWithClientAuthError(genericCode, unwrap);
		}finally {
			super.unlockForCode(genericCode);
		}
	}
	
	private void _httpsWithClientAuthError(boolean genericCode, boolean unwrap) throws TestSuiteException, IOException, Exception{
		
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
				
				String codiceErrore = Utilities.toString(CodiceErroreIntegrazione.CODICE_516_CONNETTORE_UTILIZZO_CON_ERRORE);
				String msg2 = PDD_NON_DISPONIBILE.replace("@DESTINATARIO@", this.collaborazioneSPCoopBase.getDestinatario().getTipo()+"-"+this.collaborazioneSPCoopBase.getDestinatario().getNome());
				
				if(genericCode) {
					IntegrationFunctionError integrationFunctionError = IntegrationFunctionError.SERVICE_UNAVAILABLE;
					ErroriProperties erroriProperties = ErroriProperties.getInstance(this.log);
					codiceErrore = org.openspcoop2.message.constants.Costanti.SOAP11_FAULT_CODE_SERVER +
							org.openspcoop2.message.constants.Costanti.SOAP11_FAULT_CODE_SEPARATOR+erroriProperties.getErrorType_noWrap(integrationFunctionError);
					if(erroriProperties.isForceGenericDetails_noWrap(integrationFunctionError)) {
						msg2 = erroriProperties.getGenericDetails_noWrap(integrationFunctionError);
					}
				}
				
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				Reporter.log("Controllo fault actor ["+org.openspcoop2.testsuite.core.CostantiTestSuite.OPENSPCOOP2_INTEGRATION_ACTOR+"]");
				Assert.assertTrue(org.openspcoop2.testsuite.core.CostantiTestSuite.OPENSPCOOP2_INTEGRATION_ACTOR.equals(error.getFaultActor()));
				Reporter.log("Controllo fault code ["+codiceErrore+"]");
				Assert.assertTrue(codiceErrore.equals(error.getFaultCode().getLocalPart()));
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
		
		// java 11 - wildfly
		ErroreAttesoOpenSPCoopLogCore err3 = new ErroreAttesoOpenSPCoopLogCore();
		err3.setIntervalloInferiore(dataInizioTest);
		err3.setIntervalloSuperiore(dataFineTest);
		err3.setMsgErrore("Errore avvenuto durante la consegna HTTP: Connection reset by peer");
		this.erroriAttesiOpenSPCoopCore.add(err3);
		
	}
	@DataProvider (name="httpsWithClientAuthError")
	public Object[][]testHttpsWithClientAuthError()throws Exception{
		String id=this.repositoryHTTPSWithClientAuthError.getNext();
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),DatabaseProperties.getDatabaseComponentDiagnosticaFruitore(),id,false}
		};
	}
	@Test(groups={CostantiConnettori.ID_GRUPPO_CONNETTORI,HTTPS.ID_GRUPPO,HTTPS.ID_GRUPPO+".WITH_CLIENT_AUTH_ERROR"},dataProvider="httpsWithClientAuthError",dependsOnMethods={"httpsWithClientAuthError_genericCode_wrap"})
	public void testHttpsWithClientAuthError_genericCode_wrap(DatabaseComponent data,DatabaseMsgDiagnosticiComponent dataMsg,String id,boolean checkServizioApplicativo) throws Exception{
		_testHttpsWithClientAuthError(data, dataMsg, id, checkServizioApplicativo);
	}
	@Test(groups={CostantiConnettori.ID_GRUPPO_CONNETTORI,HTTPS.ID_GRUPPO,HTTPS.ID_GRUPPO+".WITH_CLIENT_AUTH_ERROR"},dataProvider="httpsWithClientAuthError",dependsOnMethods={"httpsWithClientAuthError_genericCode_unwrap"})
	public void testHttpsWithClientAuthError_genericCode_unwrap(DatabaseComponent data,DatabaseMsgDiagnosticiComponent dataMsg,String id,boolean checkServizioApplicativo) throws Exception{
		_testHttpsWithClientAuthError(data, dataMsg, id, checkServizioApplicativo);
	}
	@Test(groups={CostantiConnettori.ID_GRUPPO_CONNETTORI,HTTPS.ID_GRUPPO,HTTPS.ID_GRUPPO+".WITH_CLIENT_AUTH_ERROR"},dataProvider="httpsWithClientAuthError",dependsOnMethods={"httpsWithClientAuthError_specificCode"})
	public void testHttpsWithClientAuthError_specificCode(DatabaseComponent data,DatabaseMsgDiagnosticiComponent dataMsg,String id,boolean checkServizioApplicativo) throws Exception{
		_testHttpsWithClientAuthError(data, dataMsg, id, checkServizioApplicativo);
	}
	private void _testHttpsWithClientAuthError(DatabaseComponent data,DatabaseMsgDiagnosticiComponent dataMsg,String id,boolean checkServizioApplicativo) throws Exception{
		
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
				// succedono casualmente
				boolean badCertificate3 = dataMsg.isTracedMessaggioWithLike(id,BAD_CERTIFICATE_3); // java11 e wildfly
				boolean badCertificate4 = dataMsg.isTracedMessaggioWithLike(id,BAD_CERTIFICATE_4); // java11 e wildfly
				boolean badCertificate5 = dataMsg.isTracedMessaggioWithLike(id,BAD_CERTIFICATE_5); // java11 e wildfly
				Assert.assertTrue(badCertificate || badCertificate2 || badCertificate3 || badCertificate4 || badCertificate5);
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
	@Test(groups={CostantiConnettori.ID_GRUPPO_CONNETTORI,HTTPS.ID_GRUPPO,HTTPS.ID_GRUPPO+".WITHOUT_CLIENT_AUTH"},description="Test di tipo sincrono, Viene controllato se i body sono uguali e se gli attachment sono uguali")
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
	@Test(groups={CostantiConnettori.ID_GRUPPO_CONNETTORI,HTTPS.ID_GRUPPO,HTTPS.ID_GRUPPO+".WITHOUT_CLIENT_AUTH"},dataProvider="httpsWithoutClientAuth",dependsOnMethods={"httpsWithoutClientAuth"})
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
				
				String msg2 = AUTORIZZAZIONE_IN_CORSO_SENZA_AUTENTICAZIONE.replace("@IDEGOV@", id);
				msg2 = msg2.replace("@MITTENTE@", this.collaborazioneSPCoopBase.getMittente().getTipo()+"/"+this.collaborazioneSPCoopBase.getMittente());
				msg2 = msg2.replace("@DESTINATARIO@", this.collaborazioneSPCoopBase.getDestinatario().getTipo()+"/"+this.collaborazioneSPCoopBase.getDestinatario());
				msg2 = msg2.replace("@SERVIZIO@", CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO+"/"+CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO);
				msg2 = msg2.replace("@AZIONE@", CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_HTTPS_WITH_CLIENT_AUTH);
				msg2 = msg2.replace("@CN@", "Soggetto1");
				Reporter.log("Controllo messaggio (id:"+id+") msg["+msg2+"]");
				Assert.assertTrue(dataMsg.isTracedMessaggioWithLike(id, msg2)==false);			
				msg2 = AUTORIZZAZIONE_EFFETTUATA_SENZA_AUTENTICAZIONE.replace("@IDEGOV@", id);
				msg2 = msg2.replace("@MITTENTE@", this.collaborazioneSPCoopBase.getMittente().getTipo()+"/"+this.collaborazioneSPCoopBase.getMittente());
				msg2 = msg2.replace("@DESTINATARIO@", this.collaborazioneSPCoopBase.getDestinatario().getTipo()+"/"+this.collaborazioneSPCoopBase.getDestinatario());
				msg2 = msg2.replace("@SERVIZIO@", CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO+"/"+CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO);
				msg2 = msg2.replace("@AZIONE@", CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_HTTPS_WITH_CLIENT_AUTH);
				msg2 = msg2.replace("@CN@", "Soggetto1");
				Reporter.log("Controllo messaggio (id:"+id+") msg["+msg2+"]");
				Assert.assertTrue(dataMsg.isTracedMessaggioWithLike(id, msg2)==false);
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
	@Test(groups={CostantiConnettori.ID_GRUPPO_CONNETTORI,HTTPS.ID_GRUPPO,HTTPS.ID_GRUPPO+".CA_NON_PRESENTE"},description="Test di tipo sincrono, Viene controllato se i body sono uguali e se gli attachment sono uguali")
	public void httpsCANonPresente_genericCode_wrap() throws TestSuiteException, IOException, Exception{
		boolean genericCode = true;
		boolean unwrap = false;
		try {
			super.lockForCode(genericCode, unwrap);
			
			_httpsCANonPresente(genericCode, unwrap);
		}finally {
			super.unlockForCode(genericCode);
		}
	}
	@Test(groups={CostantiConnettori.ID_GRUPPO_CONNETTORI,HTTPS.ID_GRUPPO,HTTPS.ID_GRUPPO+".CA_NON_PRESENTE"},description="Test di tipo sincrono, Viene controllato se i body sono uguali e se gli attachment sono uguali")
	public void httpsCANonPresente_genericCode_unwrap() throws TestSuiteException, IOException, Exception{
		boolean genericCode = true;
		boolean unwrap = true;
		try {
			super.lockForCode(genericCode, unwrap);
			
			_httpsCANonPresente(genericCode, unwrap);
		}finally {
			super.unlockForCode(genericCode);
		}
	}
	@Test(groups={CostantiConnettori.ID_GRUPPO_CONNETTORI,HTTPS.ID_GRUPPO,HTTPS.ID_GRUPPO+".CA_NON_PRESENTE"},description="Test di tipo sincrono, Viene controllato se i body sono uguali e se gli attachment sono uguali")
	public void httpsCANonPresente_specificCode() throws TestSuiteException, IOException, Exception{
		boolean genericCode = false;
		boolean unwrap = false;
		try {
			super.lockForCode(genericCode, unwrap);
			
			_httpsCANonPresente(genericCode, unwrap);
		}finally {
			super.unlockForCode(genericCode);
		}
	}
	
	private void _httpsCANonPresente(boolean genericCode, boolean unwrap) throws TestSuiteException, IOException, Exception{
		
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
				
				String codiceErrore = Utilities.toString(CodiceErroreIntegrazione.CODICE_516_CONNETTORE_UTILIZZO_CON_ERRORE);
				String msg2 = PDD_NON_DISPONIBILE.replace("@DESTINATARIO@", this.collaborazioneSPCoopBase.getDestinatario().getTipo()+"-"+this.collaborazioneSPCoopBase.getDestinatario().getNome());
				
				if(genericCode) {
					IntegrationFunctionError integrationFunctionError = IntegrationFunctionError.SERVICE_UNAVAILABLE;
					ErroriProperties erroriProperties = ErroriProperties.getInstance(this.log);
					codiceErrore = org.openspcoop2.message.constants.Costanti.SOAP11_FAULT_CODE_SERVER +
							org.openspcoop2.message.constants.Costanti.SOAP11_FAULT_CODE_SEPARATOR+erroriProperties.getErrorType_noWrap(integrationFunctionError);
					if(erroriProperties.isForceGenericDetails_noWrap(integrationFunctionError)) {
						msg2 = erroriProperties.getGenericDetails_noWrap(integrationFunctionError);
					}
				}
				
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				Reporter.log("Controllo fault actor ["+org.openspcoop2.testsuite.core.CostantiTestSuite.OPENSPCOOP2_INTEGRATION_ACTOR+"]");
				Assert.assertTrue(org.openspcoop2.testsuite.core.CostantiTestSuite.OPENSPCOOP2_INTEGRATION_ACTOR.equals(error.getFaultActor()));
				Reporter.log("Controllo fault code ["+codiceErrore+"]");
				Assert.assertTrue(codiceErrore.equals(error.getFaultCode().getLocalPart()));
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
	@Test(groups={CostantiConnettori.ID_GRUPPO_CONNETTORI,HTTPS.ID_GRUPPO,HTTPS.ID_GRUPPO+".CA_NON_PRESENTE"},dataProvider="httpsCANonPresente",dependsOnMethods={"httpsCANonPresente_genericCode_wrap"})
	public void testHttpsCANonPresente_genericCode_wrap(DatabaseComponent data,DatabaseMsgDiagnosticiComponent dataMsg,String id,boolean checkServizioApplicativo) throws Exception{
		_testHttpsCANonPresente(data, dataMsg, id, checkServizioApplicativo);
	}
	@Test(groups={CostantiConnettori.ID_GRUPPO_CONNETTORI,HTTPS.ID_GRUPPO,HTTPS.ID_GRUPPO+".CA_NON_PRESENTE"},dataProvider="httpsCANonPresente",dependsOnMethods={"httpsCANonPresente_genericCode_unwrap"})
	public void testHttpsCANonPresente_genericCode_unwrap(DatabaseComponent data,DatabaseMsgDiagnosticiComponent dataMsg,String id,boolean checkServizioApplicativo) throws Exception{
		_testHttpsCANonPresente(data, dataMsg, id, checkServizioApplicativo);
	}
	@Test(groups={CostantiConnettori.ID_GRUPPO_CONNETTORI,HTTPS.ID_GRUPPO,HTTPS.ID_GRUPPO+".CA_NON_PRESENTE"},dataProvider="httpsCANonPresente",dependsOnMethods={"httpsCANonPresente_specificCode"})
	public void testHttpsCANonPresente_specificCode(DatabaseComponent data,DatabaseMsgDiagnosticiComponent dataMsg,String id,boolean checkServizioApplicativo) throws Exception{
		_testHttpsCANonPresente(data, dataMsg, id, checkServizioApplicativo);
	}
	private void _testHttpsCANonPresente(DatabaseComponent data,DatabaseMsgDiagnosticiComponent dataMsg,String id,boolean checkServizioApplicativo) throws Exception{
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
	@Test(groups={CostantiConnettori.ID_GRUPPO_CONNETTORI,HTTPS.ID_GRUPPO,HTTPS.ID_GRUPPO+".HOSTNAME_VERIFIER"},description="Test di tipo sincrono, Viene controllato se i body sono uguali e se gli attachment sono uguali")
	public void httpsHostnameVerifier_genericCode_wrap() throws TestSuiteException, IOException, Exception{
		boolean genericCode = true;
		boolean unwrap = false;
		try {
			super.lockForCode(genericCode, unwrap);
			
			_httpsHostnameVerifier(genericCode, unwrap);
		}finally {
			super.unlockForCode(genericCode);
		}
	}
	@Test(groups={CostantiConnettori.ID_GRUPPO_CONNETTORI,HTTPS.ID_GRUPPO,HTTPS.ID_GRUPPO+".HOSTNAME_VERIFIER"},description="Test di tipo sincrono, Viene controllato se i body sono uguali e se gli attachment sono uguali")
	public void httpsHostnameVerifier_genericCode_unwrap() throws TestSuiteException, IOException, Exception{
		boolean genericCode = true;
		boolean unwrap = true;
		try {
			super.lockForCode(genericCode, unwrap);
			
			_httpsHostnameVerifier(genericCode, unwrap);
		}finally {
			super.unlockForCode(genericCode);
		}
	}
	@Test(groups={CostantiConnettori.ID_GRUPPO_CONNETTORI,HTTPS.ID_GRUPPO,HTTPS.ID_GRUPPO+".HOSTNAME_VERIFIER"},description="Test di tipo sincrono, Viene controllato se i body sono uguali e se gli attachment sono uguali")
	public void httpsHostnameVerifier_specificCode() throws TestSuiteException, IOException, Exception{
		boolean genericCode = false;
		boolean unwrap = false;
		try {
			super.lockForCode(genericCode, unwrap);
			
			_httpsHostnameVerifier(genericCode, unwrap);
		}finally {
			super.unlockForCode(genericCode);
		}
	}
	
	private void _httpsHostnameVerifier(boolean genericCode, boolean unwrap) throws TestSuiteException, IOException, Exception{
		
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
				
				String codiceErrore = Utilities.toString(CodiceErroreIntegrazione.CODICE_516_CONNETTORE_UTILIZZO_CON_ERRORE);
				String msg2 = PDD_NON_DISPONIBILE.replace("@DESTINATARIO@", this.collaborazioneSPCoopBase.getDestinatario().getTipo()+"-"+this.collaborazioneSPCoopBase.getDestinatario().getNome());
				
				if(genericCode) {
					IntegrationFunctionError integrationFunctionError = IntegrationFunctionError.SERVICE_UNAVAILABLE;
					ErroriProperties erroriProperties = ErroriProperties.getInstance(this.log);
					codiceErrore = org.openspcoop2.message.constants.Costanti.SOAP11_FAULT_CODE_SERVER +
							org.openspcoop2.message.constants.Costanti.SOAP11_FAULT_CODE_SEPARATOR+erroriProperties.getErrorType_noWrap(integrationFunctionError);
					if(erroriProperties.isForceGenericDetails_noWrap(integrationFunctionError)) {
						msg2 = erroriProperties.getGenericDetails_noWrap(integrationFunctionError);
					}
				}
				
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				Reporter.log("Controllo fault actor ["+org.openspcoop2.testsuite.core.CostantiTestSuite.OPENSPCOOP2_INTEGRATION_ACTOR+"]");
				Assert.assertTrue(org.openspcoop2.testsuite.core.CostantiTestSuite.OPENSPCOOP2_INTEGRATION_ACTOR.equals(error.getFaultActor()));
				Reporter.log("Controllo fault code ["+codiceErrore+"]");
				Assert.assertTrue(codiceErrore.equals(error.getFaultCode().getLocalPart()));
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
	@Test(groups={CostantiConnettori.ID_GRUPPO_CONNETTORI,HTTPS.ID_GRUPPO,HTTPS.ID_GRUPPO+".HOSTNAME_VERIFIER"},dataProvider="httpsHostnameVerifier",dependsOnMethods={"httpsHostnameVerifier_genericCode_wrap"})
	public void testHttpsHostnameVerifier_genericCode_wrap(DatabaseComponent data,DatabaseMsgDiagnosticiComponent dataMsg,String id,boolean checkServizioApplicativo) throws Exception{
		_testHttpsHostnameVerifier(data, dataMsg, id, checkServizioApplicativo);
	}
	@Test(groups={CostantiConnettori.ID_GRUPPO_CONNETTORI,HTTPS.ID_GRUPPO,HTTPS.ID_GRUPPO+".HOSTNAME_VERIFIER"},dataProvider="httpsHostnameVerifier",dependsOnMethods={"httpsHostnameVerifier_genericCode_unwrap"})
	public void testHttpsHostnameVerifier_genericCode_unwrap(DatabaseComponent data,DatabaseMsgDiagnosticiComponent dataMsg,String id,boolean checkServizioApplicativo) throws Exception{
		_testHttpsHostnameVerifier(data, dataMsg, id, checkServizioApplicativo);
	}
	@Test(groups={CostantiConnettori.ID_GRUPPO_CONNETTORI,HTTPS.ID_GRUPPO,HTTPS.ID_GRUPPO+".HOSTNAME_VERIFIER"},dataProvider="httpsHostnameVerifier",dependsOnMethods={"httpsHostnameVerifier_specificCode"})
	public void testHttpsHostnameVerifier_specificCode(DatabaseComponent data,DatabaseMsgDiagnosticiComponent dataMsg,String id,boolean checkServizioApplicativo) throws Exception{
		_testHttpsHostnameVerifier(data, dataMsg, id, checkServizioApplicativo);
	}
	private void _testHttpsHostnameVerifier(DatabaseComponent data,DatabaseMsgDiagnosticiComponent dataMsg,String id,boolean checkServizioApplicativo) throws Exception{
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
				{"/etc/govway/keys/sil3.jks","openspcoopjks","openspcoop"}, // crendeziali non corrette
		};
	}
	Repository repositoryHTTPSautenticazioneSSL=new Repository();
	@Test(groups={CostantiConnettori.ID_GRUPPO_CONNETTORI,HTTPS.ID_GRUPPO,HTTPS.ID_GRUPPO+".CREDENZIALI_NON_CORRETTE"},dataProvider="credenzialiScorrette")
	public void testAutenticazioneCredenzialiScorrette_genericCode_wrap(String location,String passwordKeystore,String passwordKey) throws TestSuiteException, IOException, Exception{
		boolean genericCode = true;
		boolean unwrap = false;
		try {
			super.lockForCode(genericCode, unwrap);
			
			_testAutenticazioneCredenzialiScorrette(location,passwordKeystore, passwordKey, genericCode, unwrap);
		}finally {
			super.unlockForCode(genericCode);
		}
	}
	@Test(groups={CostantiConnettori.ID_GRUPPO_CONNETTORI,HTTPS.ID_GRUPPO,HTTPS.ID_GRUPPO+".CREDENZIALI_NON_CORRETTE"},dataProvider="credenzialiScorrette")
	public void testAutenticazioneCredenzialiScorrette_genericCode_unwrap(String location,String passwordKeystore,String passwordKey) throws TestSuiteException, IOException, Exception{
		boolean genericCode = true;
		boolean unwrap = true;
		try {
			super.lockForCode(genericCode, unwrap);
			
			_testAutenticazioneCredenzialiScorrette(location,passwordKeystore, passwordKey, genericCode, unwrap);
		}finally {
			super.unlockForCode(genericCode);
		}
	}
	@Test(groups={CostantiConnettori.ID_GRUPPO_CONNETTORI,HTTPS.ID_GRUPPO,HTTPS.ID_GRUPPO+".CREDENZIALI_NON_CORRETTE"},dataProvider="credenzialiScorrette")
	public void testAutenticazioneCredenzialiScorrette_specificCode(String location,String passwordKeystore,String passwordKey) throws TestSuiteException, IOException, Exception{
		boolean genericCode = false;
		boolean unwrap = false;
		try {
			super.lockForCode(genericCode, unwrap);
			
			_testAutenticazioneCredenzialiScorrette(location,passwordKeystore, passwordKey, genericCode, unwrap);
		}finally {
			super.unlockForCode(genericCode);
		}
	}
	
	private void _testAutenticazioneCredenzialiScorrette(String location,String passwordKeystore,String passwordKey, boolean genericCode, boolean unwrap) throws Exception{
		
		Date dataInizioTest = DateManager.getDate();
		
		java.io.FileInputStream fin = null;
		DatabaseComponent dbComponentFruitore = null;
		try{
			fin = new java.io.FileInputStream(new File(Utilities.testSuiteProperties.getSoap11FileName()));

			Message msg=new Message(fin);
			msg.getSOAPPartAsBytes();
			
			// Contesto SSL
			java.util.Map<String, String> sslContext = null;
			if(location!=null){
				sslContext = new HashMap<String, String>();
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
				Reporter.log("Controllo fault actor ["+org.openspcoop2.testsuite.core.CostantiTestSuite.OPENSPCOOP2_INTEGRATION_ACTOR+"]");
				Assert.assertTrue(org.openspcoop2.testsuite.core.CostantiTestSuite.OPENSPCOOP2_INTEGRATION_ACTOR.equals(error.getFaultActor()));
				
				if(location!=null){
					
					String codiceEccezione = Utilities.toString(CodiceErroreIntegrazione.CODICE_404_AUTORIZZAZIONE_FALLITA);
					String msg2 = AUTORIZZAZIONE_FALLITA.replace("@SILNAME@", "Anonimo");
					
					if(genericCode) {
						IntegrationFunctionError integrationFunctionError = IntegrationFunctionError.AUTHORIZATION_DENY;
						ErroriProperties erroriProperties = ErroriProperties.getInstance(this.log);
						codiceEccezione = org.openspcoop2.message.constants.Costanti.SOAP11_FAULT_CODE_CLIENT +
								org.openspcoop2.message.constants.Costanti.SOAP11_FAULT_CODE_SEPARATOR+erroriProperties.getErrorType_noWrap(integrationFunctionError);
						if(erroriProperties.isForceGenericDetails_noWrap(integrationFunctionError)) {
							msg2 = erroriProperties.getGenericDetails_noWrap(integrationFunctionError);
						}
					}
					
					// cmq autenticazione ssl poich' ci sono credenziali ssl. Poi l'identificazione di un sa non porta a riconoscerne alcuno.
					Reporter.log("Controllo fault code ["+codiceEccezione+"]");
					Assert.assertTrue(codiceEccezione.equals(error.getFaultCode().getLocalPart()));
				
					Reporter.log("Controllo fault string non corrette ["+msg2+"]");
					Assert.assertTrue(msg2.equals(error.getFaultString()));
				}else{
					
					String codiceEccezione = Utilities.toString(CodiceErroreIntegrazione.CODICE_402_AUTENTICAZIONE_FALLITA);
					String msg2 =  CREDENZIALI_NON_FORNITE;
					
					if(genericCode) {
						IntegrationFunctionError integrationFunctionError = IntegrationFunctionError.AUTHENTICATION_CREDENTIALS_NOT_FOUND;
						ErroriProperties erroriProperties = ErroriProperties.getInstance(this.log);
						codiceEccezione = org.openspcoop2.message.constants.Costanti.SOAP11_FAULT_CODE_CLIENT +
								org.openspcoop2.message.constants.Costanti.SOAP11_FAULT_CODE_SEPARATOR+erroriProperties.getErrorType_noWrap(integrationFunctionError);
						if(erroriProperties.isForceGenericDetails_noWrap(integrationFunctionError)) {
							msg2 = erroriProperties.getGenericDetails_noWrap(integrationFunctionError);
						}
					}
					
					Reporter.log("Controllo fault code ["+codiceEccezione+"]");
					Assert.assertTrue(codiceEccezione.equals(error.getFaultCode().getLocalPart()));
										
					Reporter.log("Controllo fault string non fornite ["+msg2+"]");
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
		
		Date dataFineTest = DateManager.getDate();
		
		ErroreAttesoOpenSPCoopLogCore err = new ErroreAttesoOpenSPCoopLogCore();
		err.setIntervalloInferiore(dataInizioTest);
		err.setIntervalloSuperiore(dataFineTest);
		err.setMsgErrore(CREDENZIALI_NON_FORNITE);
		this.erroriAttesiOpenSPCoopCore.add(err);
		
		ErroreAttesoOpenSPCoopLogCore err2 = new ErroreAttesoOpenSPCoopLogCore();
		err2.setIntervalloInferiore(dataInizioTest);
		err2.setIntervalloSuperiore(dataFineTest);
		err2.setMsgErrore( AUTORIZZAZIONE_FALLITA.replace("@SILNAME@", "Anonimo"));
		this.erroriAttesiOpenSPCoopCore.add(err2);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/***
	 * Test https con autenticazione client
	 */
	Repository repositoryHTTPSAutenticazioneSIL=new Repository();
	Date dataTestAutenticazioneSIL = null;
	@Test(groups={CostantiConnettori.ID_GRUPPO_CONNETTORI,HTTPS.ID_GRUPPO,HTTPS.ID_GRUPPO+".AUTENTICAZIONE_SIL"},description="Test di tipo sincrono, Viene controllato se i body sono uguali e se gli attachment sono uguali")
	public void httpsAutenticazioneSIL() throws TestSuiteException, IOException, Exception{
		
		this.dataTestAutenticazioneSIL = new Date();
		
		java.io.FileInputStream fin = null;
		DatabaseComponent dbComponentFruitore = null;
		try{
			fin = new java.io.FileInputStream(new File(Utilities.testSuiteProperties.getSoap11FileName()));

			Message msg=new Message(fin);
			msg.getSOAPPartAsBytes();
			
			// Contesto SSL
			java.util.Map<String, String> sslContext = null;
			sslContext = new HashMap<String, String>();
			sslContext.put("trustStoreLocation", "/etc/govway/keys/sil1.jks");
			sslContext.put("keyStoreLocation", "/etc/govway/keys/sil1.jks");
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
	@Test(groups={CostantiConnettori.ID_GRUPPO_CONNETTORI,HTTPS.ID_GRUPPO,HTTPS.ID_GRUPPO+".AUTENTICAZIONE_SIL"},dataProvider="httpsAutenticazioneSIL",dependsOnMethods={"httpsAutenticazioneSIL"})
	public void testHttpsAutenticazioneSIL(DatabaseComponent data,DatabaseMsgDiagnosticiComponent dataMsg,String id,boolean checkServizioApplicativo) throws Exception{
		try{
			this.collaborazioneSPCoopBase.testSincrono(data, id, CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,null, 
					checkServizioApplicativo,null);
			
			// Check msgDiag
			if(dataMsg!=null){
				String msg1 = AUTH_SSL_IN_CORSO.replace("@SIL@", "sil1");
				Assert.assertTrue(dataMsg.isTracedMessaggioWithLike(this.dataTestAutenticazioneSIL , msg1));
				
				msg1 = AUTH_SSL_EFFETTUATA;
				Assert.assertTrue(dataMsg.isTracedMessaggioWithLike(this.dataTestAutenticazioneSIL , msg1));
				
				msg1 = SA_RICONOSCIUTO.replace("@SIL@", "sil1");
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
	/*
	 * Il test non ha più senso, una credenziale è univoca totalmente nella base dati, indipendentemente dal soggetto a cui appartiene il servizio applicativo
	 */
	/*
	Date dataTestAutenticazioneSIL2 = null;
	Repository repositoryHTTPSAutenticazioneSIL_Test2=new Repository();
	private CooperazioneBaseInformazioni infoTestSil2 = CooperazioneSPCoopBase.getCooperazioneBaseInformazioni(CostantiTestSuite.SPCOOP_SOGGETTO_1,
			CostantiTestSuite.SPCOOP_SOGGETTO_EROGATORE,
			false,SPCoopCostanti.PROFILO_TRASMISSIONE_CON_DUPLICATI,Inoltro.CON_DUPLICATI);	
	private CooperazioneBase collaborazioneSPCoopBaseTestSil2 = 
		new CooperazioneBase(false,MessageType.SOAP_11,  this.infoTestSil2, 
				org.openspcoop2.protocol.spcoop.testsuite.core.TestSuiteProperties.getInstance(), 
				DatabaseProperties.getInstance(), SPCoopTestsuiteLogger.getInstance());
		
	@Test(groups={CostantiConnettori.ID_GRUPPO_CONNETTORI,HTTPS.ID_GRUPPO,HTTPS.ID_GRUPPO+".AUTENTICAZIONE_SIL_2"},description="Test di tipo sincrono, Viene controllato se i body sono uguali e se gli attachment sono uguali")
	public void httpsAutenticazioneSIL_2() throws TestSuiteException, IOException, Exception{
		
		this.dataTestAutenticazioneSIL2 = new Date();
		
		java.io.FileInputStream fin = null;
		DatabaseComponent dbComponentFruitore = null;
		try{
			fin = new java.io.FileInputStream(new File(Utilities.testSuiteProperties.getSoap11FileName()));

			Message msg=new Message(fin);
			msg.getSOAPPartAsBytes();
			
			// Contesto SSL
			java.util.Map<String, String> sslContext = null;
			sslContext = new HashMap<String, String>();
			sslContext.put("trustStoreLocation", "/etc/govway/keys/sil1.jks");
			sslContext.put("keyStoreLocation", "/etc/govway/keys/sil1.jks");
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
	@Test(groups={CostantiConnettori.ID_GRUPPO_CONNETTORI,HTTPS.ID_GRUPPO,HTTPS.ID_GRUPPO+".AUTENTICAZIONE_SIL_2"},dataProvider="httpsAutenticazioneSIL_2",dependsOnMethods={"httpsAutenticazioneSIL_2"})
	public void testHttpsAutenticazioneSIL_2(DatabaseComponent data,DatabaseMsgDiagnosticiComponent dataMsg,String id,boolean checkServizioApplicativo) throws Exception{
		try{
			this.collaborazioneSPCoopBaseTestSil2.testSincrono(data, id, CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,null, 
					checkServizioApplicativo,null);
			
			// Check msgDiag
			if(dataMsg!=null){
				String msg1 = SA_IDENTIFICATO.replace("@SIL@", "sil1");
				msg1 = msg1.replace("@SILNAME@", "sil1HTTPS_test2");
				msg1 = msg1.replace("@PD@", CostantiTestSuite.PORTA_DELEGATA_HTTPS_AUTENTICAZIONE_SSL_2);
				Reporter.log("Verifico msg ["+msg1+"]");
				Assert.assertTrue(dataMsg.isTracedMessaggioWithLike(this.dataTestAutenticazioneSIL2, msg1));
				
				msg1 = SA_RICONOSCIUTO.replace("@SIL@", "sil1");
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
	*/
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * Test di invocazione di una porta delegata fornendo autenticazioni sbagliate
	 */
	@DataProvider(name="autorizzazioneFallita")
	public Object[][] autorizzazioneFallita(){
		return new Object[][]{
				{"/etc/govway/keys/sil2.jks","openspcoopjks","openspcoop"}, 
		};
	}
	Repository repositoryHTTPSautorizzazioneFallita=new Repository();
	@Test(groups={CostantiConnettori.ID_GRUPPO_CONNETTORI,HTTPS.ID_GRUPPO,HTTPS.ID_GRUPPO+".AUTORIZZAZIONE_FALLITA"},dataProvider="autorizzazioneFallita")
	public void testAutorizzazioneFallita_genericCode_wrap(String location,String passwordKeystore,String passwordKey) throws TestSuiteException, IOException, Exception{
		boolean genericCode = true;
		boolean unwrap = false;
		try {
			super.lockForCode(genericCode, unwrap);
			
			_testAutorizzazioneFallita(location, passwordKeystore, passwordKey,genericCode, unwrap);
		}finally {
			super.unlockForCode(genericCode);
		}
	}
	@Test(groups={CostantiConnettori.ID_GRUPPO_CONNETTORI,HTTPS.ID_GRUPPO,HTTPS.ID_GRUPPO+".AUTORIZZAZIONE_FALLITA"},dataProvider="autorizzazioneFallita")
	public void testAutorizzazioneFallita_genericCode_unwrap(String location,String passwordKeystore,String passwordKey) throws TestSuiteException, IOException, Exception{
		boolean genericCode = true;
		boolean unwrap = true;
		try {
			super.lockForCode(genericCode, unwrap);
			
			_testAutorizzazioneFallita(location, passwordKeystore, passwordKey,genericCode, unwrap);
		}finally {
			super.unlockForCode(genericCode);
		}
	}
	@Test(groups={CostantiConnettori.ID_GRUPPO_CONNETTORI,HTTPS.ID_GRUPPO,HTTPS.ID_GRUPPO+".AUTORIZZAZIONE_FALLITA"},dataProvider="autorizzazioneFallita")
	public void testAutorizzazioneFallita_specificCode(String location,String passwordKeystore,String passwordKey) throws TestSuiteException, IOException, Exception{
		boolean genericCode = false;
		boolean unwrap = false;
		try {
			super.lockForCode(genericCode, unwrap);
			
			_testAutorizzazioneFallita(location, passwordKeystore, passwordKey,genericCode, unwrap);
		}finally {
			super.unlockForCode(genericCode);
		}
	}
	
	private void _testAutorizzazioneFallita(String location,String passwordKeystore,String passwordKey, boolean genericCode, boolean unwrap) throws Exception{
		
		Date dataInizioTest = DateManager.getDate();
		
		java.io.FileInputStream fin = null;
		DatabaseComponent dbComponentFruitore = null;
		try{
			fin = new java.io.FileInputStream(new File(Utilities.testSuiteProperties.getSoap11FileName()));

			Message msg=new Message(fin);
			msg.getSOAPPartAsBytes();
			
			// Contesto SSL
			java.util.Map<String, String> sslContext = null;
			if(location!=null){
				sslContext = new HashMap<String, String>();
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
				
				String codiceErrore = Utilities.toString(CodiceErroreIntegrazione.CODICE_404_AUTORIZZAZIONE_FALLITA);
				String msg2 = AUTORIZZAZIONE_FALLITA.replace("@SILNAME@", "sil2HTTPS");
				msg2 = msg2.replace("@SILNAME@", "sil2HTTPS");
				msg2 = msg2.replace("@PD@", CostantiTestSuite.PORTA_DELEGATA_HTTPS_AUTENTICAZIONE_SSL);
				
				if(genericCode) {
					IntegrationFunctionError integrationFunctionError = IntegrationFunctionError.AUTHORIZATION_DENY;
					ErroriProperties erroriProperties = ErroriProperties.getInstance(this.log);
					codiceErrore = org.openspcoop2.message.constants.Costanti.SOAP11_FAULT_CODE_CLIENT +
							org.openspcoop2.message.constants.Costanti.SOAP11_FAULT_CODE_SEPARATOR+erroriProperties.getErrorType_noWrap(integrationFunctionError);
					if(erroriProperties.isForceGenericDetails_noWrap(integrationFunctionError)) {
						msg2 = erroriProperties.getGenericDetails_noWrap(integrationFunctionError);
					}
				}
				
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				Reporter.log("Controllo fault actor ["+org.openspcoop2.testsuite.core.CostantiTestSuite.OPENSPCOOP2_INTEGRATION_ACTOR+"]");
				Assert.assertTrue(org.openspcoop2.testsuite.core.CostantiTestSuite.OPENSPCOOP2_INTEGRATION_ACTOR.equals(error.getFaultActor()));
				Reporter.log("Controllo fault code ["+codiceErrore+"]");
				Assert.assertTrue(codiceErrore.equals(error.getFaultCode().getLocalPart()));
								
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
		
		Date dataFineTest = DateManager.getDate();
		
		ErroreAttesoOpenSPCoopLogCore err = new ErroreAttesoOpenSPCoopLogCore();
		err.setIntervalloInferiore(dataInizioTest);
		err.setIntervalloSuperiore(dataFineTest);
		err.setMsgErrore(AUTORIZZAZIONE_FALLITA.replace("@SILNAME@", "sil2HTTPS"));
		this.erroriAttesiOpenSPCoopCore.add(err);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/***
	 * Test https con autorizzazione spcoop ok
	 */
	private CooperazioneBaseInformazioni infoFruitoreSoggetto1 = CooperazioneSPCoopBase.getCooperazioneBaseInformazioni(CostantiTestSuite.SPCOOP_SOGGETTO_1,
			CostantiTestSuite.SPCOOP_SOGGETTO_EROGATORE,
			false,SPCoopCostanti.PROFILO_TRASMISSIONE_CON_DUPLICATI,Inoltro.CON_DUPLICATI);	
	private CooperazioneBase collaborazioneSPCoopFruitoreSoggetto1 = 
		new CooperazioneBase(false,MessageType.SOAP_11,  this.infoFruitoreSoggetto1, 
				org.openspcoop2.protocol.spcoop.testsuite.core.TestSuiteProperties.getInstance(), 
				DatabaseProperties.getInstance(), SPCoopTestsuiteLogger.getInstance());
	
	Repository repositoryHTTPSAutorizzazioneSPCoopOkSoggetto1=new Repository();
	@Test(groups={CostantiConnettori.ID_GRUPPO_CONNETTORI,HTTPS.ID_GRUPPO,HTTPS.ID_GRUPPO+".AUTORIZZAZIONE_SPCOOP"},description="Test di tipo sincrono, Viene controllato se i body sono uguali e se gli attachment sono uguali")
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
	@Test(groups={CostantiConnettori.ID_GRUPPO_CONNETTORI,HTTPS.ID_GRUPPO,HTTPS.ID_GRUPPO+".AUTORIZZAZIONE_SPCOOP"},dataProvider="httpsAutorizzazioneSPCoopOkSoggetto1",dependsOnMethods={"httpsAutorizzazioneSPCoopOkSoggetto1"})
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
				
				String msg2 = AUTORIZZAZIONE_IN_CORSO_SENZA_AUTENTICAZIONE.replace("@IDEGOV@", id);
				msg2 = msg2.replace("@MITTENTE@", this.collaborazioneSPCoopFruitoreSoggetto1.getMittente().getTipo()+"/"+this.collaborazioneSPCoopFruitoreSoggetto1.getMittente().getNome());
				msg2 = msg2.replace("@DESTINATARIO@", this.collaborazioneSPCoopFruitoreSoggetto1.getDestinatario().getTipo()+"/"+this.collaborazioneSPCoopFruitoreSoggetto1.getDestinatario().getNome());
				msg2 = msg2.replace("@SERVIZIO@", CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO+"/"+CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO);
				msg2 = msg2.replace(":@AZIONE@", "");
				msg2 = msg2.replace("@CN@", "Soggetto1");
				Reporter.log("Controllo messaggio (id:"+id+") msg["+msg2+"]");
				Assert.assertTrue(dataMsg.isTracedMessaggioWithLike(id, msg2));			
				msg2 = AUTORIZZAZIONE_EFFETTUATA_SENZA_AUTENTICAZIONE.replace("@IDEGOV@", id);
				msg2 = msg2.replace("@MITTENTE@", this.collaborazioneSPCoopFruitoreSoggetto1.getMittente().getTipo()+"/"+this.collaborazioneSPCoopFruitoreSoggetto1.getMittente().getNome());
				msg2 = msg2.replace("@DESTINATARIO@", this.collaborazioneSPCoopFruitoreSoggetto1.getDestinatario().getTipo()+"/"+this.collaborazioneSPCoopFruitoreSoggetto1.getDestinatario().getNome());
				msg2 = msg2.replace("@SERVIZIO@", CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO+"/"+CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO);
				msg2 = msg2.replace(":@AZIONE@", "");
				msg2 = msg2.replace("@CN@", "Soggetto1");
				Reporter.log("Controllo messaggio (id:"+id+") msg["+msg2+"]");
				Assert.assertTrue(dataMsg.isTracedMessaggioWithLike(id, msg2));
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
	@Test(groups={CostantiConnettori.ID_GRUPPO_CONNETTORI,HTTPS.ID_GRUPPO,HTTPS.ID_GRUPPO+".FRUITORE_NON_PRESENTE"},description="Test di tipo sincrono, Viene controllato se i body sono uguali e se gli attachment sono uguali")
	public void fruitoreNonPresente_genericCode_wrap() throws TestSuiteException, IOException, Exception{
		boolean genericCode = true;
		boolean unwrap = false;
		try {
			super.lockForCode(genericCode, unwrap);
			
			_fruitoreNonPresente(genericCode, unwrap);
		}finally {
			super.unlockForCode(genericCode);
		}
	}
	@Test(groups={CostantiConnettori.ID_GRUPPO_CONNETTORI,HTTPS.ID_GRUPPO,HTTPS.ID_GRUPPO+".FRUITORE_NON_PRESENTE"},description="Test di tipo sincrono, Viene controllato se i body sono uguali e se gli attachment sono uguali")
	public void fruitoreNonPresente_genericCode_unwrap() throws TestSuiteException, IOException, Exception{
		boolean genericCode = true;
		boolean unwrap = true;
		try {
			super.lockForCode(genericCode, unwrap);
			
			_fruitoreNonPresente(genericCode, unwrap);
		}finally {
			super.unlockForCode(genericCode);
		}
	}
	@Test(groups={CostantiConnettori.ID_GRUPPO_CONNETTORI,HTTPS.ID_GRUPPO,HTTPS.ID_GRUPPO+".FRUITORE_NON_PRESENTE"},description="Test di tipo sincrono, Viene controllato se i body sono uguali e se gli attachment sono uguali")
	public void fruitoreNonPresente_specificCode() throws TestSuiteException, IOException, Exception{
		boolean genericCode = false;
		boolean unwrap = false;
		try {
			super.lockForCode(genericCode, unwrap);
			
			_fruitoreNonPresente(genericCode, unwrap);
		}finally {
			super.unlockForCode(genericCode);
		}
	}
	
	private void _fruitoreNonPresente(boolean genericCode, boolean unwrap) throws TestSuiteException, IOException, Exception{
		
		Date dataInizioTest = DateManager.getDate();
						
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
					
					String msg2 = SOAP_FAULT_AUTORIZZAZIONE_SPCOOP_FALLITA_SUBJECT_NON_PRESENTE.replace("@MITTENTE@", this.collaborazioneSPCoopFruitoreSoggetto1.getMittente().getTipo()+"/"+this.collaborazioneSPCoopFruitoreSoggetto1.getMittente().getNome());
					msg2 = msg2.replace("@DESTINATARIO@", this.collaborazioneSPCoopFruitoreSoggetto1.getDestinatario().getTipo()+"/"+this.collaborazioneSPCoopFruitoreSoggetto1.getDestinatario().getNome());
					msg2 = msg2.replace("@SERVIZIO@", CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY+"/"+CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY);
					msg2 = msg2.replace("_@AZIONE@", "");
					
					String codiceErrore = "EGOV_IT_201";
					
					if(genericCode) {
						IntegrationFunctionError integrationFunctionError = IntegrationFunctionError.WRAP_502_BAD_RESPONSE;
						if(unwrap) {
							integrationFunctionError = IntegrationFunctionError.INTEROPERABILITY_PROFILE_RESPONSE_ERROR;
						}
						ErroriProperties erroriProperties = ErroriProperties.getInstance(this.log);
						codiceErrore = org.openspcoop2.message.constants.Costanti.SOAP11_FAULT_CODE_SERVER +
								org.openspcoop2.message.constants.Costanti.SOAP11_FAULT_CODE_SEPARATOR+erroriProperties.getErrorType_noWrap(integrationFunctionError);
						if(erroriProperties.isForceGenericDetails_noWrap(integrationFunctionError)) {
							msg2 = erroriProperties.getGenericDetails_noWrap(integrationFunctionError);
						}
					}
					
					Reporter.log("Controllo fault actor ["+org.openspcoop2.testsuite.core.CostantiTestSuite.OPENSPCOOP2_INTEGRATION_ACTOR+"]");
					Assert.assertTrue(org.openspcoop2.testsuite.core.CostantiTestSuite.OPENSPCOOP2_INTEGRATION_ACTOR.equals(error.getFaultActor()));
					Reporter.log("Controllo fault code ["+codiceErrore+"]");
					Assert.assertTrue(codiceErrore.equals(error.getFaultCode().getLocalPart()));
														
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
		
		Date dataFineTest = DateManager.getDate();
		
		ErroreAttesoOpenSPCoopLogCore err = new ErroreAttesoOpenSPCoopLogCore();
		err.setIntervalloInferiore(dataInizioTest);
		err.setIntervalloSuperiore(dataFineTest);
		String msgAuth = NON_AUTORIZZATO.replace("@MITTENTE@", this.collaborazioneSPCoopFruitoreSoggetto1.getMittente().getTipo()+"/"+this.collaborazioneSPCoopFruitoreSoggetto1.getMittente().getNome());
		msgAuth = msgAuth.replace("@DESTINATARIO@", this.collaborazioneSPCoopFruitoreSoggetto1.getDestinatario().getTipo()+"/"+this.collaborazioneSPCoopFruitoreSoggetto1.getDestinatario().getNome());
		msgAuth = msgAuth.replace("@SERVIZIO@", CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY+"/"+CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY);
		msgAuth = msgAuth.replace("_@AZIONE@", "");
		err.setMsgErrore(msgAuth);
		this.erroriAttesiOpenSPCoopCore.add(err);
	}
	@DataProvider (name="FruitoreNonPresente")
	public Object[][]testFruitoreNonPresente()throws Exception{
		String id=this.repositoryFruitoreNonPresente.getNext();
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),null,id,false},	
				{DatabaseProperties.getDatabaseComponentErogatore(),DatabaseProperties.getDatabaseComponentDiagnosticaErogatore(),id,true}
		};
	}
	@Test(groups={CostantiConnettori.ID_GRUPPO_CONNETTORI,HTTPS.ID_GRUPPO,HTTPS.ID_GRUPPO+".FRUITORE_NON_PRESENTE"},dataProvider="FruitoreNonPresente",dependsOnMethods={"fruitoreNonPresente_genericCode_wrap"})
	public void testFruitoreNonPresente_genericCode_wrap(DatabaseComponent data,DatabaseMsgDiagnosticiComponent dataMsg,String id,boolean checkServizioApplicativo) throws Exception{
		_testFruitoreNonPresente(data, dataMsg, id, checkServizioApplicativo);
	}
	@Test(groups={CostantiConnettori.ID_GRUPPO_CONNETTORI,HTTPS.ID_GRUPPO,HTTPS.ID_GRUPPO+".FRUITORE_NON_PRESENTE"},dataProvider="FruitoreNonPresente",dependsOnMethods={"fruitoreNonPresente_genericCode_unwrap"})
	public void testFruitoreNonPresente_genericCode_unwrap(DatabaseComponent data,DatabaseMsgDiagnosticiComponent dataMsg,String id,boolean checkServizioApplicativo) throws Exception{
		_testFruitoreNonPresente(data, dataMsg, id, checkServizioApplicativo);
	}
	@Test(groups={CostantiConnettori.ID_GRUPPO_CONNETTORI,HTTPS.ID_GRUPPO,HTTPS.ID_GRUPPO+".FRUITORE_NON_PRESENTE"},dataProvider="FruitoreNonPresente",dependsOnMethods={"fruitoreNonPresente_specificCode"})
	public void testFruitoreNonPresente_specificCode(DatabaseComponent data,DatabaseMsgDiagnosticiComponent dataMsg,String id,boolean checkServizioApplicativo) throws Exception{
		_testFruitoreNonPresente(data, dataMsg, id, checkServizioApplicativo);
	}
	private void _testFruitoreNonPresente(DatabaseComponent data,DatabaseMsgDiagnosticiComponent dataMsg,String id,boolean checkServizioApplicativo) throws Exception{
		
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
				msg2 = msg2.replace(":@AZIONE@", "");
				msg2 = msg2.replace("_@AZIONE@", "");
				msg2 = msg2.replace("@PDD@", "");
				String msg2_inCache = msg2.replace(" fallita", " fallita (in cache)");
				Reporter.log("Controllo Messaggio (id:"+id+") msg["+msg2+"]");
				Assert.assertTrue(dataMsg.isTracedMessaggio(id, msg2) || dataMsg.isTracedMessaggio(id, msg2_inCache));
				
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
	 * Test spoofing rilevato (vecchio meccanismo tramite PDD, i test con la nuova autenticazione https sono in fondo alla classe)
	 */
	private CooperazioneBaseInformazioni infoFruitoreSoggetto2 = CooperazioneSPCoopBase.getCooperazioneBaseInformazioni(CostantiTestSuite.SPCOOP_SOGGETTO_2,
			CostantiTestSuite.SPCOOP_SOGGETTO_EROGATORE,
			false,SPCoopCostanti.PROFILO_TRASMISSIONE_CON_DUPLICATI,Inoltro.CON_DUPLICATI);	
	private CooperazioneBase collaborazioneSPCoopFruitoreSoggetto2 = 
		new CooperazioneBase(false,MessageType.SOAP_11,  this.infoFruitoreSoggetto2, 
				org.openspcoop2.protocol.spcoop.testsuite.core.TestSuiteProperties.getInstance(), 
				DatabaseProperties.getInstance(), SPCoopTestsuiteLogger.getInstance());
	Repository repositorySpoofingRilevato=new Repository();
	@Test(groups={CostantiConnettori.ID_GRUPPO_CONNETTORI,HTTPS.ID_GRUPPO,HTTPS.ID_GRUPPO+".SPOOFING_RILEVATO"},description="Test di tipo sincrono, Viene controllato se i body sono uguali e se gli attachment sono uguali")
	public void spoofingRilevato_genericCode_wrap() throws TestSuiteException, IOException, Exception{
		boolean genericCode = true;
		boolean unwrap = false;
		try {
			super.lockForCode(genericCode, unwrap);
			
			_spoofingRilevato(genericCode, unwrap);
		}finally {
			super.unlockForCode(genericCode);
		}
	}
	@Test(groups={CostantiConnettori.ID_GRUPPO_CONNETTORI,HTTPS.ID_GRUPPO,HTTPS.ID_GRUPPO+".SPOOFING_RILEVATO"},description="Test di tipo sincrono, Viene controllato se i body sono uguali e se gli attachment sono uguali")
	public void spoofingRilevato_genericCode_unwrap() throws TestSuiteException, IOException, Exception{
		boolean genericCode = true;
		boolean unwrap = true;
		try {
			super.lockForCode(genericCode, unwrap);
			
			_spoofingRilevato(genericCode, unwrap);
		}finally {
			super.unlockForCode(genericCode);
		}
	}
	@Test(groups={CostantiConnettori.ID_GRUPPO_CONNETTORI,HTTPS.ID_GRUPPO,HTTPS.ID_GRUPPO+".SPOOFING_RILEVATO"},description="Test di tipo sincrono, Viene controllato se i body sono uguali e se gli attachment sono uguali")
	public void spoofingRilevato_specificCode() throws TestSuiteException, IOException, Exception{
		boolean genericCode = false;
		boolean unwrap = false;
		try {
			super.lockForCode(genericCode, unwrap);
			
			_spoofingRilevato(genericCode, unwrap);
		}finally {
			super.unlockForCode(genericCode);
		}
	}
	private void _spoofingRilevato(boolean genericCode, boolean unwrap) throws TestSuiteException, IOException, Exception{
		
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
				
				String msg2 = SOAP_FAULT_AUTORIZZAZIONE_SPCOOP_FALLITA_SUBJECT_NON_PRESENTE.replace("@MITTENTE@", this.collaborazioneSPCoopFruitoreSoggetto2.getMittente().getTipo()+"/"+this.collaborazioneSPCoopFruitoreSoggetto2.getMittente().getNome());
				msg2 = msg2.replace("@DESTINATARIO@", this.collaborazioneSPCoopFruitoreSoggetto2.getDestinatario().getTipo()+"/"+this.collaborazioneSPCoopFruitoreSoggetto2.getDestinatario().getNome());
				msg2 = msg2.replace("@SERVIZIO@", CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO+"/"+CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO);
				msg2 = msg2.replace("_@AZIONE@", "");
				
				String codiceErrore = "EGOV_IT_201";
				
				if(genericCode) {
					IntegrationFunctionError integrationFunctionError = IntegrationFunctionError.WRAP_502_BAD_RESPONSE;
					if(unwrap) {
						integrationFunctionError = IntegrationFunctionError.INTEROPERABILITY_PROFILE_RESPONSE_ERROR;
					}
					ErroriProperties erroriProperties = ErroriProperties.getInstance(this.log);
					codiceErrore = org.openspcoop2.message.constants.Costanti.SOAP11_FAULT_CODE_SERVER +
							org.openspcoop2.message.constants.Costanti.SOAP11_FAULT_CODE_SEPARATOR+erroriProperties.getErrorType_noWrap(integrationFunctionError);
					if(erroriProperties.isForceGenericDetails_noWrap(integrationFunctionError)) {
						msg2 = erroriProperties.getGenericDetails_noWrap(integrationFunctionError);
					}
				}
				
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: ["+error.getFaultString()+"]");
				Reporter.log("Controllo fault actor ["+org.openspcoop2.testsuite.core.CostantiTestSuite.OPENSPCOOP2_INTEGRATION_ACTOR+"]");
				Assert.assertTrue(org.openspcoop2.testsuite.core.CostantiTestSuite.OPENSPCOOP2_INTEGRATION_ACTOR.equals(error.getFaultActor()));
				Reporter.log("Controllo fault code ["+codiceErrore+"]");
				Assert.assertTrue(codiceErrore.equals(error.getFaultCode().getLocalPart()));
												
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
	@Test(groups={CostantiConnettori.ID_GRUPPO_CONNETTORI,HTTPS.ID_GRUPPO,HTTPS.ID_GRUPPO+".SPOOFING_RILEVATO"},dataProvider="SpoofingRilevato",dependsOnMethods={"spoofingRilevato_genericCode_wrap"})
	public void testSpoofingRilevato_genericCode_wrap(DatabaseComponent data,DatabaseMsgDiagnosticiComponent dataMsg,String id,boolean checkServizioApplicativo) throws Exception{
		_testSpoofingRilevato(data, dataMsg, id, checkServizioApplicativo);
	}
	@Test(groups={CostantiConnettori.ID_GRUPPO_CONNETTORI,HTTPS.ID_GRUPPO,HTTPS.ID_GRUPPO+".SPOOFING_RILEVATO"},dataProvider="SpoofingRilevato",dependsOnMethods={"spoofingRilevato_genericCode_unwrap"})
	public void testSpoofingRilevato_genericCode_unwrap(DatabaseComponent data,DatabaseMsgDiagnosticiComponent dataMsg,String id,boolean checkServizioApplicativo) throws Exception{
		_testSpoofingRilevato(data, dataMsg, id, checkServizioApplicativo);
	}
	@Test(groups={CostantiConnettori.ID_GRUPPO_CONNETTORI,HTTPS.ID_GRUPPO,HTTPS.ID_GRUPPO+".SPOOFING_RILEVATO"},dataProvider="SpoofingRilevato",dependsOnMethods={"spoofingRilevato_specificCode"})
	public void testSpoofingRilevato_specificCode(DatabaseComponent data,DatabaseMsgDiagnosticiComponent dataMsg,String id,boolean checkServizioApplicativo) throws Exception{
		_testSpoofingRilevato(data, dataMsg, id, checkServizioApplicativo);
	}
	private void _testSpoofingRilevato(DatabaseComponent data,DatabaseMsgDiagnosticiComponent dataMsg,String id,boolean checkServizioApplicativo) throws Exception{
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
				msg2 = msg2.replace(":@AZIONE@", "");
				msg2 = msg2.replace("_@AZIONE@", "");
				msg2 = msg2.replace("@PDD@", " credenzialiMittente ( SSL-Subject 'CN=Soggetto1, OU=test, O=openspcoop.org, L=Pisa, ST=Italy, C=IT, EMAILADDRESS=apoli@link.it' )");
				String XML = msg2 + " (subject estratto dal certificato client [CN=Soggetto1, OU=test, O=openspcoop.org, L=Pisa, ST=Italy, C=IT, EMAILADDRESS=apoli@link.it] diverso da quello registrato per la porta di dominio PdDSoggetto2 del mittente [CN=Soggetto2, OU=test, O=openspcoop.org, L=Pisa, ST=Italy, C=IT, EMAILADDRESS=apoli@link.it])";
				String template = "CREDENZIALIDB";
				String _DB = msg2 + " (subject estratto dal certificato client [CN=Soggetto1, OU=test, O=openspcoop.org, L=Pisa, ST=Italy, C=IT, EMAILADDRESS=apoli@link.it] diverso da quello registrato per la porta di dominio PdDSoggetto2 del mittente ["+template+"])";
				String DB1 = _DB.replace(template, "/l=Pisa/st=Italy/ou=test/emailaddress=apoli@link.it/o=openspcoop.org/c=IT/cn=Soggetto2/");
				String DB2 = _DB.replace(template, "/st=Italy/c=IT/ou=test/emailaddress=apoli@link.it/cn=Soggetto2/l=Pisa/o=openspcoop.org/");
				String XML_inCache = XML.replace(" fallita", " fallita (in cache)");
				String DB1_inCache = DB1.replace(" fallita", " fallita (in cache)");
				String DB2_inCache = DB2.replace(" fallita", " fallita (in cache)");
								
				Reporter.log("Controllo Messaggio (id:"+id+") msgXML["+XML+"] msgDB1["+DB1+"] msgDB2["+DB2+"]");
				boolean esito = dataMsg.isTracedMessaggio(id, XML) || 
						dataMsg.isTracedMessaggio(id, DB1)  ||
						dataMsg.isTracedMessaggio(id, DB2)  ||
						dataMsg.isTracedMessaggio(id, XML_inCache) || 
						dataMsg.isTracedMessaggio(id, DB1_inCache) || 
						dataMsg.isTracedMessaggio(id, DB2_inCache)  ;
				if(!esito) {
					Reporter.log("Controllo fallito, recupero diagnostici... ");
					Vector<String> diag = dataMsg.getMessaggiDiagnostici(id);
					if(diag!=null) {
						Reporter.log("Controllo fallito recuperati diagnostici ["+diag.size()+"] ");
						if(!diag.isEmpty()) {
							int i = 0;
							for (String d : diag) {
								Reporter.log("Diagnostico-"+i+" ["+d+"]");
								i++;
							}
						}
					}
				}
				Assert.assertTrue( 
						esito
						);
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
			new CooperazioneBase(false,MessageType.SOAP_11,  this.infoFruitoreSoggettoNonAutenticato, 
					org.openspcoop2.protocol.spcoop.testsuite.core.TestSuiteProperties.getInstance(), 
					DatabaseProperties.getInstance(), SPCoopTestsuiteLogger.getInstance());
	/*
	 * Possibilita di definire il client auth attivo sul fruitore non è piu supportato in openspcoop 3.0
	 */
	/*
	Repository repositorySpoofingRilevatoTramiteFruitore=new Repository();
	@Test(groups={CostantiConnettori.ID_GRUPPO_CONNETTORI,HTTPS.ID_GRUPPO,HTTPS.ID_GRUPPO+".SPOOFING_RILEVATO_TRAMITE_FRUIZIONE"},description="Test di tipo sincrono, Viene controllato se i body sono uguali e se gli attachment sono uguali")
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
					
					String msg2 = SOAP_FAULT_AUTORIZZAZIONE_SPCOOP_FALLITA_SUBJECT_NON_PRESENTE.replace("@MITTENTE@", this.collaborazioneSPCoopFruitoreSoggettoNonAutenticato.getMittente().getTipo()+"/"+this.collaborazioneSPCoopFruitoreSoggettoNonAutenticato.getMittente().getNome());
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
	@Test(groups={CostantiConnettori.ID_GRUPPO_CONNETTORI,HTTPS.ID_GRUPPO,HTTPS.ID_GRUPPO+".SPOOFING_RILEVATO_TRAMITE_FRUIZIONE"},dataProvider="SpoofingRilevatoTramiteFruitore",dependsOnMethods={"SpoofingRilevatoTramiteFruitore"})
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
				msg2 = msg2.replace(":@AZIONE@", "");
				msg2 = msg2.replace("_@AZIONE@", "");
				msg2 = msg2.replace("@PDD@", " credenzialiMittente ( SSL-Subject 'CN=Soggetto1, OU=test, O=openspcoop.org, L=Pisa, ST=Italy, C=IT, EMAILADDRESS=apoli@link.it' )");
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
	*/
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/***
	 * Test autorizzazione SPCoop disabilitato
	 */
	Repository repositoryHTTPSAutorizzazioneSPCoopDisabilitata=new Repository();
	@Test(groups={CostantiConnettori.ID_GRUPPO_CONNETTORI,HTTPS.ID_GRUPPO,HTTPS.ID_GRUPPO+".AUTORIZZAZIONE_SPCOOP_DISABILITATA"},description="Test di tipo sincrono, Viene controllato se i body sono uguali e se gli attachment sono uguali")
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
	@Test(groups={CostantiConnettori.ID_GRUPPO_CONNETTORI,HTTPS.ID_GRUPPO,HTTPS.ID_GRUPPO+".AUTORIZZAZIONE_SPCOOP_DISABILITATA"},dataProvider="httpsAutorizzazioneSPCoopDisabilitata",dependsOnMethods={"httpsAutorizzazioneSPCoopDisabilitata"})
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
				
				String msg2 = AUTORIZZAZIONE_IN_CORSO_SENZA_AUTENTICAZIONE.replace("@IDEGOV@", id);
				msg2 = msg2.replace("@MITTENTE@", this.collaborazioneSPCoopFruitoreSoggettoNonAutenticato.getMittente().getTipo()+"/"+this.collaborazioneSPCoopFruitoreSoggettoNonAutenticato.getMittente().getNome());
				msg2 = msg2.replace("@DESTINATARIO@", this.collaborazioneSPCoopFruitoreSoggettoNonAutenticato.getDestinatario().getTipo()+"/"+this.collaborazioneSPCoopFruitoreSoggettoNonAutenticato.getDestinatario().getNome());
				msg2 = msg2.replace("@SERVIZIO@", CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO+"/"+CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO);
				msg2 = msg2.replace(":@AZIONE@", "");
				msg2 = msg2.replace("@CN@", "Soggetto1");
				Reporter.log("Controllo messaggio (id:"+id+") msg["+msg2+"]");
				Assert.assertTrue(dataMsg.isTracedMessaggioWithLike(id, msg2));			
				msg2 = AUTORIZZAZIONE_EFFETTUATA_SENZA_AUTENTICAZIONE.replace("@IDEGOV@", id);
				msg2 = msg2.replace("@MITTENTE@", this.collaborazioneSPCoopFruitoreSoggettoNonAutenticato.getMittente().getTipo()+"/"+this.collaborazioneSPCoopFruitoreSoggettoNonAutenticato.getMittente().getNome());
				msg2 = msg2.replace("@DESTINATARIO@", this.collaborazioneSPCoopFruitoreSoggettoNonAutenticato.getDestinatario().getTipo()+"/"+this.collaborazioneSPCoopFruitoreSoggettoNonAutenticato.getDestinatario().getNome());
				msg2 = msg2.replace("@SERVIZIO@", CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO+"/"+CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO);
				msg2 = msg2.replace(":@AZIONE@", "");
				msg2 = msg2.replace("@CN@", "Soggetto1");
				String msg2_check1 = msg2 + " (in cache) (client-auth disabilitato nella porta di dominio PdDSoggettoNonAutenticato)";
				String msg2_check2 = msg2 + " (client-auth disabilitato nella porta di dominio PdDSoggettoNonAutenticato)";
				boolean esito1 = dataMsg.isTracedMessaggioWithLike(id, msg2_check1);
				boolean esito2 = dataMsg.isTracedMessaggioWithLike(id, msg2_check2);
				Reporter.log("Controllo messaggio (id:"+id+") msg1["+msg2_check1+"] esito:"+esito1+"");
				Reporter.log("Controllo messaggio (id:"+id+") msg1["+msg2_check2+"] esito:"+esito2+"");
				Assert.assertTrue(esito1 || esito2);
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
	/*
	 * Possibilita di definire il client auth non abilitato sul fruitore non è piu supportato in openspcoop 3.0
	 */
	/*
	Repository repositoryHTTPSAutorizzazioneSPCoopDisabilitataTramiteFruizione=new Repository();
	@Test(groups={CostantiConnettori.ID_GRUPPO_CONNETTORI,HTTPS.ID_GRUPPO,HTTPS.ID_GRUPPO+".AUTORIZZAZIONE_SPCOOP_DISABILITATA_TRAMITE_FRUZIONE"},description="Test di tipo sincrono, Viene controllato se i body sono uguali e se gli attachment sono uguali")
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
	@Test(groups={CostantiConnettori.ID_GRUPPO_CONNETTORI,HTTPS.ID_GRUPPO,HTTPS.ID_GRUPPO+".AUTORIZZAZIONE_SPCOOP_DISABILITATA_TRAMITE_FRUZIONE"},dataProvider="httpsAutorizzazioneSPCoopDisabilitataTramiteFruizione",dependsOnMethods={"httpsAutorizzazioneSPCoopDisabilitataTramiteFruizione"})
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
	*/
	
	
	
	
	
	
	
	
	
	/***
	 * Test per il profilo di collaborazione Sincrono con consegna HTTPS
	 */
	Repository repositorySincronoConsegnaHTTPS=new Repository();
	@Test(groups={CostantiConnettori.ID_GRUPPO_CONNETTORI,HTTPS.ID_GRUPPO,HTTPS.ID_GRUPPO+".CONSEGNA_SIL_HTTPS"},description="Test di tipo sincrono, Viene controllato se i body sono uguali e se gli attachment sono uguali")
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
	@Test(groups={CostantiConnettori.ID_GRUPPO_CONNETTORI,HTTPS.ID_GRUPPO,HTTPS.ID_GRUPPO+".CONSEGNA_SIL_HTTPS"},dataProvider="SincronoConsegnaHTTPS",dependsOnMethods={"sincronoConsegnaHTTPS"})
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
	
	
	
	
	
	
	
	
	
	
	/***
	 * Test https con autenticazione https e autorizzazione spcoop ok
	 */
	private CooperazioneBaseInformazioni infoFruitoreSoggetto1conCredenziali = CooperazioneSPCoopBase.getCooperazioneBaseInformazioni(CostantiTestSuite.SPCOOP_SOGGETTO_1_CON_CREDENZIALI_SSL,
			CostantiTestSuite.SPCOOP_SOGGETTO_EROGATORE,
			false,SPCoopCostanti.PROFILO_TRASMISSIONE_CON_DUPLICATI,Inoltro.CON_DUPLICATI);	
	private CooperazioneBase collaborazioneSPCoopFruitoreSoggetto1conCredenziali = 
		new CooperazioneBase(false,MessageType.SOAP_11,  this.infoFruitoreSoggetto1conCredenziali, 
				org.openspcoop2.protocol.spcoop.testsuite.core.TestSuiteProperties.getInstance(), 
				DatabaseProperties.getInstance(), SPCoopTestsuiteLogger.getInstance());
	
	Repository repositoryHTTPSAutorizzazioneSPCoopOkSoggetto1conCredenziali=new Repository();
	@Test(groups={CostantiConnettori.ID_GRUPPO_CONNETTORI,HTTPS.ID_GRUPPO,HTTPS.ID_GRUPPO+".AUTENTICAZIONE_HTTPS_AUTORIZZAZIONE_SPCOOP"},description="Test di tipo sincrono, Viene controllato se i body sono uguali e se gli attachment sono uguali")
	public void httpsAutorizzazioneSPCoopOkSoggetto1conCredenziali() throws TestSuiteException, IOException, SOAPException{
		this.collaborazioneSPCoopFruitoreSoggetto1conCredenziali.sincrono(this.repositoryHTTPSAutorizzazioneSPCoopOkSoggetto1conCredenziali,
				CostantiTestSuite.PORTA_DELEGATA_AUTORIZZAZIONE_SPCOOP_AUTENTICAZIONE_HTTPS_PA_SOGGETTO1,addIDUnivoco);
	}
	@DataProvider (name="httpsAutorizzazioneSPCoopOkSoggetto1conCredenziali")
	public Object[][]testHttpsAutorizzazioneSPCoopOkSoggetto1conCredenziali()throws Exception{
		String id=this.repositoryHTTPSAutorizzazioneSPCoopOkSoggetto1conCredenziali.getNext();
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),null,id,false},	
				{DatabaseProperties.getDatabaseComponentErogatore(),DatabaseProperties.getDatabaseComponentDiagnosticaErogatore(),id,true}	
		};
	}
	@Test(groups={CostantiConnettori.ID_GRUPPO_CONNETTORI,HTTPS.ID_GRUPPO,HTTPS.ID_GRUPPO+".AUTENTICAZIONE_HTTPS_AUTORIZZAZIONE_SPCOOP"},dataProvider="httpsAutorizzazioneSPCoopOkSoggetto1conCredenziali",dependsOnMethods={"httpsAutorizzazioneSPCoopOkSoggetto1conCredenziali"})
	public void testHttpsAutorizzazioneSPCoopOkSoggetto1conCredenziali(DatabaseComponent data,DatabaseMsgDiagnosticiComponent dataMsg,String id,boolean checkServizioApplicativo) throws Exception{
		try{
			this.collaborazioneSPCoopFruitoreSoggetto1conCredenziali.testSincrono(data, id, CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_HTTPS_AUTENTICAZIONE_PA, 
					checkServizioApplicativo,null);
			
			// Check msgDiag
			if(dataMsg!=null){
				
				String msg1 = MESSAGGIO_SPCOOP_RICEVUTO_AUTENTICAZIONE_HTTPS_IN_CORSO.replace("@CN@", "Soggetto1");
				Reporter.log("Controllo messaggio (id:"+id+") msg["+msg1+"]");
				Assert.assertTrue(dataMsg.isTracedMessaggio(id, msg1));
				
				msg1 = MESSAGGIO_SPCOOP_RICEVUTO_AUTENTICAZIONE_HTTPS_SUCCESS_PREFIX;
				Reporter.log("Controllo messaggio (id:"+id+") msg["+msg1+"]");
				Assert.assertTrue(dataMsg.isTracedMessaggioWithLike(id, msg1));
				
				msg1 = MESSAGGIO_SPCOOP_RICEVUTO_AUTENTICAZIONE_HTTPS.replace("@IDEGOV@", id);
				msg1 = msg1.replace("@MITTENTE@", this.collaborazioneSPCoopFruitoreSoggetto1conCredenziali.getMittente().getTipo()+"/"+this.collaborazioneSPCoopFruitoreSoggetto1conCredenziali.getMittente().getNome());
				msg1 = msg1.replace("@CN@", "Soggetto1");
				Reporter.log("Controllo messaggio (id:"+id+") msg["+msg1+"]");
				Assert.assertTrue(dataMsg.isTracedMessaggioWithLike(id, msg1));
				
				String msg2 = AUTORIZZAZIONE_IN_CORSO_AUTENTICAZIONE_HTTPS.replace("@IDEGOV@", id);
				msg2 = msg2.replace("@MITTENTE@", this.collaborazioneSPCoopFruitoreSoggetto1conCredenziali.getMittente().getTipo()+"/"+this.collaborazioneSPCoopFruitoreSoggetto1conCredenziali.getMittente().getNome());
				msg2 = msg2.replace("@DESTINATARIO@", this.collaborazioneSPCoopFruitoreSoggetto1conCredenziali.getDestinatario().getTipo()+"/"+this.collaborazioneSPCoopFruitoreSoggetto1conCredenziali.getDestinatario().getNome());
				msg2 = msg2.replace("@SERVIZIO@", CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO+"/"+CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO);
				msg2 = msg2.replace("@AZIONE@", CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_HTTPS_AUTENTICAZIONE_PA);
				Reporter.log("Controllo messaggio (id:"+id+") msg["+msg2+"]");
				Assert.assertTrue(dataMsg.isTracedMessaggioWithLike(id, msg2));			
				msg2 = AUTORIZZAZIONE_EFFETTUATA_AUTENTICAZIONE_HTTPS.replace("@IDEGOV@", id);
				msg2 = msg2.replace("@MITTENTE@", this.collaborazioneSPCoopFruitoreSoggetto1conCredenziali.getMittente().getTipo()+"/"+this.collaborazioneSPCoopFruitoreSoggetto1conCredenziali.getMittente().getNome());
				msg2 = msg2.replace("@DESTINATARIO@", this.collaborazioneSPCoopFruitoreSoggetto1conCredenziali.getDestinatario().getTipo()+"/"+this.collaborazioneSPCoopFruitoreSoggetto1conCredenziali.getDestinatario().getNome());
				msg2 = msg2.replace("@SERVIZIO@", CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO+"/"+CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO);
				msg2 = msg2.replace("@AZIONE@", CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_HTTPS_AUTENTICAZIONE_PA);
				Reporter.log("Controllo messaggio (id:"+id+") msg["+msg2+"]");
				Assert.assertTrue(dataMsg.isTracedMessaggioWithLike(id, msg2));
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
	 * Test https con autenticazione https opzionale e autorizzazione spcoop ok
	 */
	private CooperazioneBaseInformazioni infoFruitoreSoggetto1conCredenzialiAuthnOpzionale = CooperazioneSPCoopBase.getCooperazioneBaseInformazioni(CostantiTestSuite.SPCOOP_SOGGETTO_1_CON_CREDENZIALI_SSL,
			CostantiTestSuite.SPCOOP_SOGGETTO_EROGATORE,
			false,SPCoopCostanti.PROFILO_TRASMISSIONE_CON_DUPLICATI,Inoltro.CON_DUPLICATI);	
	private CooperazioneBase collaborazioneSPCoopFruitoreSoggetto1conCredenzialiAuthnOpzionale = 
		new CooperazioneBase(false,MessageType.SOAP_11,  this.infoFruitoreSoggetto1conCredenzialiAuthnOpzionale, 
				org.openspcoop2.protocol.spcoop.testsuite.core.TestSuiteProperties.getInstance(), 
				DatabaseProperties.getInstance(), SPCoopTestsuiteLogger.getInstance());
	
	Repository repositoryHTTPSAutorizzazioneSPCoopOkSoggetto1conCredenzialiAuthnOpzionale=new Repository();
	@Test(groups={CostantiConnettori.ID_GRUPPO_CONNETTORI,HTTPS.ID_GRUPPO,HTTPS.ID_GRUPPO+".AUTENTICAZIONE_HTTPS_OPZIONALE_AUTORIZZAZIONE_SPCOOP"},description="Test di tipo sincrono, Viene controllato se i body sono uguali e se gli attachment sono uguali")
	public void httpsAutorizzazioneSPCoopOkSoggetto1conCredenzialiAuthnOpzionale() throws TestSuiteException, IOException, SOAPException{
		this.collaborazioneSPCoopFruitoreSoggetto1conCredenzialiAuthnOpzionale.sincrono(this.repositoryHTTPSAutorizzazioneSPCoopOkSoggetto1conCredenzialiAuthnOpzionale,
				CostantiTestSuite.PORTA_DELEGATA_AUTORIZZAZIONE_SPCOOP_AUTENTICAZIONE_HTTPS_PA_OPZIONALE_SOGGETTO1,addIDUnivoco);
	}
	@DataProvider (name="httpsAutorizzazioneSPCoopOkSoggetto1conCredenzialiAuthnOpzionale")
	public Object[][]testHttpsAutorizzazioneSPCoopOkSoggetto1conCredenzialiAuthnOpzionale()throws Exception{
		String id=this.repositoryHTTPSAutorizzazioneSPCoopOkSoggetto1conCredenzialiAuthnOpzionale.getNext();
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),null,id,false},	
				{DatabaseProperties.getDatabaseComponentErogatore(),DatabaseProperties.getDatabaseComponentDiagnosticaErogatore(),id,true}	
		};
	}
	@Test(groups={CostantiConnettori.ID_GRUPPO_CONNETTORI,HTTPS.ID_GRUPPO,HTTPS.ID_GRUPPO+".AUTENTICAZIONE_HTTPS_OPZIONALE_AUTORIZZAZIONE_SPCOOP"},dataProvider="httpsAutorizzazioneSPCoopOkSoggetto1conCredenzialiAuthnOpzionale",dependsOnMethods={"httpsAutorizzazioneSPCoopOkSoggetto1conCredenzialiAuthnOpzionale"})
	public void testHttpsAutorizzazioneSPCoopOkSoggetto1conCredenzialiAuthnOpzionale(DatabaseComponent data,DatabaseMsgDiagnosticiComponent dataMsg,String id,boolean checkServizioApplicativo) throws Exception{
		try{
			this.collaborazioneSPCoopFruitoreSoggetto1conCredenzialiAuthnOpzionale.testSincrono(data, id, CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_HTTPS_AUTENTICAZIONE_PA_OPZIONALE, 
					checkServizioApplicativo,null);
			
			// Check msgDiag
			if(dataMsg!=null){
				
				String msg1 = MESSAGGIO_SPCOOP_RICEVUTO_AUTENTICAZIONE_HTTPS_IN_CORSO.replace("@CN@", "Soggetto1");
				Reporter.log("Controllo messaggio (id:"+id+") msg["+msg1+"]");
				Assert.assertTrue(dataMsg.isTracedMessaggio(id, msg1));
				
				msg1 = MESSAGGIO_SPCOOP_RICEVUTO_AUTENTICAZIONE_HTTPS_SUCCESS_PREFIX;
				Reporter.log("Controllo messaggio (id:"+id+") msg["+msg1+"]");
				Assert.assertTrue(dataMsg.isTracedMessaggioWithLike(id, msg1));
				
				msg1 = MESSAGGIO_SPCOOP_RICEVUTO_AUTENTICAZIONE_HTTPS.replace("@IDEGOV@", id);
				msg1 = msg1.replace("@MITTENTE@", this.collaborazioneSPCoopFruitoreSoggetto1conCredenzialiAuthnOpzionale.getMittente().getTipo()+"/"+this.collaborazioneSPCoopFruitoreSoggetto1conCredenzialiAuthnOpzionale.getMittente().getNome());
				msg1 = msg1.replace("@CN@", "Soggetto1");
				Reporter.log("Controllo messaggio (id:"+id+") msg["+msg1+"]");
				Assert.assertTrue(dataMsg.isTracedMessaggioWithLike(id, msg1));
				
				String msg2 = AUTORIZZAZIONE_IN_CORSO_AUTENTICAZIONE_HTTPS.replace("@IDEGOV@", id);
				msg2 = msg2.replace("@MITTENTE@", this.collaborazioneSPCoopFruitoreSoggetto1conCredenzialiAuthnOpzionale.getMittente().getTipo()+"/"+this.collaborazioneSPCoopFruitoreSoggetto1conCredenzialiAuthnOpzionale.getMittente().getNome());
				msg2 = msg2.replace("@DESTINATARIO@", this.collaborazioneSPCoopFruitoreSoggetto1conCredenzialiAuthnOpzionale.getDestinatario().getTipo()+"/"+this.collaborazioneSPCoopFruitoreSoggetto1conCredenzialiAuthnOpzionale.getDestinatario().getNome());
				msg2 = msg2.replace("@SERVIZIO@", CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO+"/"+CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO);
				msg2 = msg2.replace("@AZIONE@", CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_HTTPS_AUTENTICAZIONE_PA_OPZIONALE);
				Reporter.log("Controllo messaggio (id:"+id+") msg["+msg2+"]");
				Assert.assertTrue(dataMsg.isTracedMessaggioWithLike(id, msg2));			
				msg2 = AUTORIZZAZIONE_EFFETTUATA_AUTENTICAZIONE_HTTPS.replace("@IDEGOV@", id);
				msg2 = msg2.replace("@MITTENTE@", this.collaborazioneSPCoopFruitoreSoggetto1conCredenzialiAuthnOpzionale.getMittente().getTipo()+"/"+this.collaborazioneSPCoopFruitoreSoggetto1conCredenzialiAuthnOpzionale.getMittente().getNome());
				msg2 = msg2.replace("@DESTINATARIO@", this.collaborazioneSPCoopFruitoreSoggetto1conCredenzialiAuthnOpzionale.getDestinatario().getTipo()+"/"+this.collaborazioneSPCoopFruitoreSoggetto1conCredenzialiAuthnOpzionale.getDestinatario().getNome());
				msg2 = msg2.replace("@SERVIZIO@", CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO+"/"+CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO);
				msg2 = msg2.replace("@AZIONE@", CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_HTTPS_AUTENTICAZIONE_PA_OPZIONALE);
				Reporter.log("Controllo messaggio (id:"+id+") msg["+msg2+"]");
				Assert.assertTrue(dataMsg.isTracedMessaggioWithLike(id, msg2));
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
	
	
	

	
	
	
	private CooperazioneBaseInformazioni infoFruitoreSoggetto2conCredenziali = CooperazioneSPCoopBase.getCooperazioneBaseInformazioni(CostantiTestSuite.SPCOOP_SOGGETTO_2,
			CostantiTestSuite.SPCOOP_SOGGETTO_EROGATORE,
			false,SPCoopCostanti.PROFILO_TRASMISSIONE_CON_DUPLICATI,Inoltro.CON_DUPLICATI);	
	private CooperazioneBase collaborazioneSPCoopFruitoreSoggetto2conCredenziali = 
		new CooperazioneBase(false,MessageType.SOAP_11,  this.infoFruitoreSoggetto2conCredenziali, 
				org.openspcoop2.protocol.spcoop.testsuite.core.TestSuiteProperties.getInstance(), 
				DatabaseProperties.getInstance(), SPCoopTestsuiteLogger.getInstance());
	Repository repositorySpoofingRilevatoAutenticazioneSoggettoIdentificatoAltroSoggetto=new Repository();
	@Test(groups={CostantiConnettori.ID_GRUPPO_CONNETTORI,HTTPS.ID_GRUPPO,HTTPS.ID_GRUPPO+".SPOOFING_RILEVATO_AUTHN_SSL_IDENTIFICATO_ALTRO_SOGGETTO"},description="Test di tipo sincrono, Viene controllato se i body sono uguali e se gli attachment sono uguali")
	public void spoofingRilevatoAutenticazioneSoggettoIdentificatoAltroSoggetto_genericCode_wrap() throws TestSuiteException, IOException, Exception{
		boolean genericCode = true;
		boolean unwrap = false;
		try {
			super.lockForCode(genericCode, unwrap);
			
			_spoofingRilevatoAutenticazioneSoggettoIdentificatoAltroSoggetto(genericCode, unwrap);
		}finally {
			super.unlockForCode(genericCode);
		}
	}
	@Test(groups={CostantiConnettori.ID_GRUPPO_CONNETTORI,HTTPS.ID_GRUPPO,HTTPS.ID_GRUPPO+".SPOOFING_RILEVATO_AUTHN_SSL_IDENTIFICATO_ALTRO_SOGGETTO"},description="Test di tipo sincrono, Viene controllato se i body sono uguali e se gli attachment sono uguali")
	public void spoofingRilevatoAutenticazioneSoggettoIdentificatoAltroSoggetto_genericCode_unwrap() throws TestSuiteException, IOException, Exception{
		boolean genericCode = true;
		boolean unwrap = true;
		try {
			super.lockForCode(genericCode, unwrap);
			
			_spoofingRilevatoAutenticazioneSoggettoIdentificatoAltroSoggetto(genericCode, unwrap);
		}finally {
			super.unlockForCode(genericCode);
		}
	}
	@Test(groups={CostantiConnettori.ID_GRUPPO_CONNETTORI,HTTPS.ID_GRUPPO,HTTPS.ID_GRUPPO+".SPOOFING_RILEVATO_AUTHN_SSL_IDENTIFICATO_ALTRO_SOGGETTO"},description="Test di tipo sincrono, Viene controllato se i body sono uguali e se gli attachment sono uguali")
	public void spoofingRilevatoAutenticazioneSoggettoIdentificatoAltroSoggetto_specificCode() throws TestSuiteException, IOException, Exception{
		boolean genericCode = false;
		boolean unwrap = false;
		try {
			super.lockForCode(genericCode, unwrap);
			
			_spoofingRilevatoAutenticazioneSoggettoIdentificatoAltroSoggetto(genericCode, unwrap);
		}finally {
			super.unlockForCode(genericCode);
		}
	}
	private void _spoofingRilevatoAutenticazioneSoggettoIdentificatoAltroSoggetto(boolean genericCode, boolean unwrap) throws TestSuiteException, IOException, Exception{
		
		java.io.FileInputStream fin = null;
		DatabaseComponent dbComponentFruitore = null;
		try{
			fin = new java.io.FileInputStream(new File(Utilities.testSuiteProperties.getSoap11FileName()));

			Message msg=new Message(fin);
			msg.getSOAPPartAsBytes();
			
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositorySpoofingRilevatoAutenticazioneSoggettoIdentificatoAltroSoggetto);
			client.setSoapAction("\"TEST\"");
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_AUTORIZZAZIONE_SPCOOP_AUTENTICAZIONE_HTTPS_PA_SOGGETTO2);
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
				String msg2 = AUTENTICAZIONE_FALLITA_MITTENTE;
				msg2 = msg2.replace("@DESTINATARIO@", this.collaborazioneSPCoopFruitoreSoggetto2conCredenziali.getDestinatario().getTipo()+"/"+this.collaborazioneSPCoopFruitoreSoggetto2conCredenziali.getDestinatario().getNome());
				
				String codiceErrore = "EGOV_IT_101";
				
				if(genericCode) {
					IntegrationFunctionError integrationFunctionError = IntegrationFunctionError.WRAP_502_BAD_RESPONSE;
					if(unwrap) {
						integrationFunctionError = IntegrationFunctionError.INTEROPERABILITY_PROFILE_RESPONSE_ERROR;
					}
					ErroriProperties erroriProperties = ErroriProperties.getInstance(this.log);
					codiceErrore = org.openspcoop2.message.constants.Costanti.SOAP11_FAULT_CODE_SERVER +
							org.openspcoop2.message.constants.Costanti.SOAP11_FAULT_CODE_SEPARATOR+erroriProperties.getErrorType_noWrap(integrationFunctionError);
					if(erroriProperties.isForceGenericDetails_noWrap(integrationFunctionError)) {
						msg2 = erroriProperties.getGenericDetails_noWrap(integrationFunctionError);
					}
				}
				
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: ["+error.getFaultString()+"]");
				Reporter.log("Controllo fault actor ["+org.openspcoop2.testsuite.core.CostantiTestSuite.OPENSPCOOP2_INTEGRATION_ACTOR+"]");
				Assert.assertTrue(org.openspcoop2.testsuite.core.CostantiTestSuite.OPENSPCOOP2_INTEGRATION_ACTOR.equals(error.getFaultActor()));
				Reporter.log("Controllo fault code ["+codiceErrore+"]");
				Assert.assertTrue(codiceErrore.equals(error.getFaultCode().getLocalPart()));
												
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
				this.repositorySpoofingRilevatoAutenticazioneSoggettoIdentificatoAltroSoggetto.add(client.getIdMessaggio());
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
	@DataProvider (name="SpoofingRilevatoAutenticazioneSoggettoIdentificatoAltroSoggetto")
	public Object[][]testSpoofingRilevatoAutenticazioneSoggettoIdentificatoAltroSoggetto()throws Exception{
		String id=this.repositorySpoofingRilevatoAutenticazioneSoggettoIdentificatoAltroSoggetto.getNext();
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),null,id,false},	
				{DatabaseProperties.getDatabaseComponentErogatore(),DatabaseProperties.getDatabaseComponentDiagnosticaErogatore(),id,true}
		};
	}
	@Test(groups={CostantiConnettori.ID_GRUPPO_CONNETTORI,HTTPS.ID_GRUPPO,HTTPS.ID_GRUPPO+".SPOOFING_RILEVATO_AUTHN_SSL_IDENTIFICATO_ALTRO_SOGGETTO"},dataProvider="SpoofingRilevatoAutenticazioneSoggettoIdentificatoAltroSoggetto",dependsOnMethods={"spoofingRilevatoAutenticazioneSoggettoIdentificatoAltroSoggetto_genericCode_wrap"})
	public void testSpoofingRilevatoAutenticazioneSoggettoIdentificatoAltroSoggetto_genericCode_wrap(DatabaseComponent data,DatabaseMsgDiagnosticiComponent dataMsg,String id,boolean checkServizioApplicativo) throws Exception{
		_testSpoofingRilevatoAutenticazioneSoggettoIdentificatoAltroSoggetto(data, dataMsg, id, checkServizioApplicativo);
	}
	@Test(groups={CostantiConnettori.ID_GRUPPO_CONNETTORI,HTTPS.ID_GRUPPO,HTTPS.ID_GRUPPO+".SPOOFING_RILEVATO_AUTHN_SSL_IDENTIFICATO_ALTRO_SOGGETTO"},dataProvider="SpoofingRilevatoAutenticazioneSoggettoIdentificatoAltroSoggetto",dependsOnMethods={"spoofingRilevatoAutenticazioneSoggettoIdentificatoAltroSoggetto_genericCode_unwrap"})
	public void testSpoofingRilevatoAutenticazioneSoggettoIdentificatoAltroSoggetto_genericCode_unwrap(DatabaseComponent data,DatabaseMsgDiagnosticiComponent dataMsg,String id,boolean checkServizioApplicativo) throws Exception{
		_testSpoofingRilevatoAutenticazioneSoggettoIdentificatoAltroSoggetto(data, dataMsg, id, checkServizioApplicativo);
	}
	@Test(groups={CostantiConnettori.ID_GRUPPO_CONNETTORI,HTTPS.ID_GRUPPO,HTTPS.ID_GRUPPO+".SPOOFING_RILEVATO_AUTHN_SSL_IDENTIFICATO_ALTRO_SOGGETTO"},dataProvider="SpoofingRilevatoAutenticazioneSoggettoIdentificatoAltroSoggetto",dependsOnMethods={"spoofingRilevatoAutenticazioneSoggettoIdentificatoAltroSoggetto_specificCode"})
	public void testSpoofingRilevatoAutenticazioneSoggettoIdentificatoAltroSoggetto_specificCode(DatabaseComponent data,DatabaseMsgDiagnosticiComponent dataMsg,String id,boolean checkServizioApplicativo) throws Exception{
		_testSpoofingRilevatoAutenticazioneSoggettoIdentificatoAltroSoggetto(data, dataMsg, id, checkServizioApplicativo);
	}
	private void _testSpoofingRilevatoAutenticazioneSoggettoIdentificatoAltroSoggetto(DatabaseComponent data,DatabaseMsgDiagnosticiComponent dataMsg,String id,boolean checkServizioApplicativo) throws Exception{
		try{
					
			Reporter.log("Controllo tracciamento richiesta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
			Reporter.log("Controllo valore Azione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedAzione(id, CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_HTTPS_AUTENTICAZIONE_PA));
			
			Reporter.log("Controllo tracciamento risposta con riferimento id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTraced(id));
			Reporter.log("Controllo tracciamento risposta valore Azione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedAzione(id, CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_HTTPS_AUTENTICAZIONE_PA));
			
			// Check msgDiag
			if(dataMsg!=null){
				
				String AUTENTICAZIONE_FALLITA_SPOOFING_ALTRO_SOGGETTO_PREFIX =	AUTENTICAZIONE_FALLITA_SPOOFING_PREFIX+
						" Identificato un soggetto (tramite profilo di interoperabilità) 'spc/Soggetto2conCredenzialiSSL' differente da quello identificato tramite il processo di autenticazione 'spc/Soggetto1conCredenzialiSSL'";
				AUTENTICAZIONE_FALLITA_SPOOFING_ALTRO_SOGGETTO_PREFIX = AUTENTICAZIONE_FALLITA_SPOOFING_ALTRO_SOGGETTO_PREFIX.replace("@SUBJECT@", 
						"CN=Soggetto1, OU=test, O=openspcoop.org, L=Pisa, ST=Italy, C=IT, EMAILADDRESS=apoli@link.it");
				Reporter.log("Controllo Messaggio (id:"+id+") msg["+AUTENTICAZIONE_FALLITA_SPOOFING_ALTRO_SOGGETTO_PREFIX+"]");
				Assert.assertTrue( 
						dataMsg.isTracedMessaggio(id, AUTENTICAZIONE_FALLITA_SPOOFING_ALTRO_SOGGETTO_PREFIX)
						);
				
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
	
	
	
	
	
	
	
	
	
	
	private CooperazioneBaseInformazioni infoFruitoreSoggetto2conCredenzialiAuthnOpzionale = CooperazioneSPCoopBase.getCooperazioneBaseInformazioni(CostantiTestSuite.SPCOOP_SOGGETTO_2,
			CostantiTestSuite.SPCOOP_SOGGETTO_EROGATORE,
			false,SPCoopCostanti.PROFILO_TRASMISSIONE_CON_DUPLICATI,Inoltro.CON_DUPLICATI);	
	private CooperazioneBase collaborazioneSPCoopFruitoreSoggetto2conCredenzialiAuthnOpzionale = 
		new CooperazioneBase(false,MessageType.SOAP_11,  this.infoFruitoreSoggetto2conCredenzialiAuthnOpzionale, 
				org.openspcoop2.protocol.spcoop.testsuite.core.TestSuiteProperties.getInstance(), 
				DatabaseProperties.getInstance(), SPCoopTestsuiteLogger.getInstance());
	Repository repositorySpoofingRilevatoAutenticazioneOpzionaleSoggettoIdentificatoAltroSoggetto=new Repository();
	@Test(groups={CostantiConnettori.ID_GRUPPO_CONNETTORI,HTTPS.ID_GRUPPO,HTTPS.ID_GRUPPO+".SPOOFING_RILEVATO_AUTHN_SSL_OPZIONALE_IDENTIFICATO_ALTRO_SOGGETTO"},description="Test di tipo sincrono, Viene controllato se i body sono uguali e se gli attachment sono uguali")
	public void spoofingRilevatoAutenticazioneOpzionaleSoggettoIdentificatoAltroSoggetto_genericCode_wrap() throws TestSuiteException, IOException, Exception{
		boolean genericCode = true;
		boolean unwrap = false;
		try {
			super.lockForCode(genericCode, unwrap);
			
			_spoofingRilevatoAutenticazioneOpzionaleSoggettoIdentificatoAltroSoggetto(genericCode, unwrap);
		}finally {
			super.unlockForCode(genericCode);
		}
	}
	@Test(groups={CostantiConnettori.ID_GRUPPO_CONNETTORI,HTTPS.ID_GRUPPO,HTTPS.ID_GRUPPO+".SPOOFING_RILEVATO_AUTHN_SSL_OPZIONALE_IDENTIFICATO_ALTRO_SOGGETTO"},description="Test di tipo sincrono, Viene controllato se i body sono uguali e se gli attachment sono uguali")
	public void spoofingRilevatoAutenticazioneOpzionaleSoggettoIdentificatoAltroSoggetto_genericCode_unwrap() throws TestSuiteException, IOException, Exception{
		boolean genericCode = true;
		boolean unwrap = true;
		try {
			super.lockForCode(genericCode, unwrap);
			
			_spoofingRilevatoAutenticazioneOpzionaleSoggettoIdentificatoAltroSoggetto(genericCode, unwrap);
		}finally {
			super.unlockForCode(genericCode);
		}
	}
	@Test(groups={CostantiConnettori.ID_GRUPPO_CONNETTORI,HTTPS.ID_GRUPPO,HTTPS.ID_GRUPPO+".SPOOFING_RILEVATO_AUTHN_SSL_OPZIONALE_IDENTIFICATO_ALTRO_SOGGETTO"},description="Test di tipo sincrono, Viene controllato se i body sono uguali e se gli attachment sono uguali")
	public void spoofingRilevatoAutenticazioneOpzionaleSoggettoIdentificatoAltroSoggetto_specificCode() throws TestSuiteException, IOException, Exception{
		boolean genericCode = false;
		boolean unwrap = false;
		try {
			super.lockForCode(genericCode, unwrap);
			
			_spoofingRilevatoAutenticazioneOpzionaleSoggettoIdentificatoAltroSoggetto(genericCode, unwrap);
		}finally {
			super.unlockForCode(genericCode);
		}
	}
	private void _spoofingRilevatoAutenticazioneOpzionaleSoggettoIdentificatoAltroSoggetto(boolean genericCode, boolean unwrap) throws TestSuiteException, IOException, Exception{
		
		java.io.FileInputStream fin = null;
		DatabaseComponent dbComponentFruitore = null;
		try{
			fin = new java.io.FileInputStream(new File(Utilities.testSuiteProperties.getSoap11FileName()));

			Message msg=new Message(fin);
			msg.getSOAPPartAsBytes();
			
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositorySpoofingRilevatoAutenticazioneOpzionaleSoggettoIdentificatoAltroSoggetto);
			client.setSoapAction("\"TEST\"");
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_AUTORIZZAZIONE_SPCOOP_AUTENTICAZIONE_HTTPS_PA_OPZIONALE_SOGGETTO2);
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
				String msg2 = AUTENTICAZIONE_FALLITA_MITTENTE;
				msg2 = msg2.replace("@DESTINATARIO@", this.collaborazioneSPCoopFruitoreSoggetto2conCredenzialiAuthnOpzionale.getDestinatario().getTipo()+"/"+this.collaborazioneSPCoopFruitoreSoggetto2conCredenzialiAuthnOpzionale.getDestinatario().getNome());
				
				String codiceErrore = "EGOV_IT_101";
				
				if(genericCode) {
					IntegrationFunctionError integrationFunctionError = IntegrationFunctionError.WRAP_502_BAD_RESPONSE;
					if(unwrap) {
						integrationFunctionError = IntegrationFunctionError.INTEROPERABILITY_PROFILE_RESPONSE_ERROR;
					}
					ErroriProperties erroriProperties = ErroriProperties.getInstance(this.log);
					codiceErrore = org.openspcoop2.message.constants.Costanti.SOAP11_FAULT_CODE_SERVER +
							org.openspcoop2.message.constants.Costanti.SOAP11_FAULT_CODE_SEPARATOR+erroriProperties.getErrorType_noWrap(integrationFunctionError);
					if(erroriProperties.isForceGenericDetails_noWrap(integrationFunctionError)) {
						msg2 = erroriProperties.getGenericDetails_noWrap(integrationFunctionError);
					}
				}
				
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: ["+error.getFaultString()+"]");
				Reporter.log("Controllo fault actor ["+org.openspcoop2.testsuite.core.CostantiTestSuite.OPENSPCOOP2_INTEGRATION_ACTOR+"]");
				Assert.assertTrue(org.openspcoop2.testsuite.core.CostantiTestSuite.OPENSPCOOP2_INTEGRATION_ACTOR.equals(error.getFaultActor()));
				Reporter.log("Controllo fault code ["+codiceErrore+"]");
				Assert.assertTrue(codiceErrore.equals(error.getFaultCode().getLocalPart()));
												
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
				this.repositorySpoofingRilevatoAutenticazioneOpzionaleSoggettoIdentificatoAltroSoggetto.add(client.getIdMessaggio());
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
	@DataProvider (name="SpoofingRilevatoAutenticazioneOpzionaleSoggettoIdentificatoAltroSoggetto")
	public Object[][]testSpoofingRilevatoAutenticazioneOpzionaleSoggettoIdentificatoAltroSoggetto()throws Exception{
		String id=this.repositorySpoofingRilevatoAutenticazioneOpzionaleSoggettoIdentificatoAltroSoggetto.getNext();
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),null,id,false},	
				{DatabaseProperties.getDatabaseComponentErogatore(),DatabaseProperties.getDatabaseComponentDiagnosticaErogatore(),id,true}
		};
	}
	@Test(groups={CostantiConnettori.ID_GRUPPO_CONNETTORI,HTTPS.ID_GRUPPO,HTTPS.ID_GRUPPO+".SPOOFING_RILEVATO_AUTHN_SSL_OPZIONALE_IDENTIFICATO_ALTRO_SOGGETTO"},dataProvider="SpoofingRilevatoAutenticazioneOpzionaleSoggettoIdentificatoAltroSoggetto",dependsOnMethods={"spoofingRilevatoAutenticazioneOpzionaleSoggettoIdentificatoAltroSoggetto_genericCode_wrap"})
	public void testSpoofingRilevatoAutenticazioneOpzionaleSoggettoIdentificatoAltroSoggetto_genericCode_wrap(DatabaseComponent data,DatabaseMsgDiagnosticiComponent dataMsg,String id,boolean checkServizioApplicativo) throws Exception{
		_testSpoofingRilevatoAutenticazioneOpzionaleSoggettoIdentificatoAltroSoggetto(data, dataMsg, id, checkServizioApplicativo);
	}
	@Test(groups={CostantiConnettori.ID_GRUPPO_CONNETTORI,HTTPS.ID_GRUPPO,HTTPS.ID_GRUPPO+".SPOOFING_RILEVATO_AUTHN_SSL_OPZIONALE_IDENTIFICATO_ALTRO_SOGGETTO"},dataProvider="SpoofingRilevatoAutenticazioneOpzionaleSoggettoIdentificatoAltroSoggetto",dependsOnMethods={"spoofingRilevatoAutenticazioneOpzionaleSoggettoIdentificatoAltroSoggetto_genericCode_unwrap"})
	public void testSpoofingRilevatoAutenticazioneOpzionaleSoggettoIdentificatoAltroSoggetto_genericCode_unwrap(DatabaseComponent data,DatabaseMsgDiagnosticiComponent dataMsg,String id,boolean checkServizioApplicativo) throws Exception{
		_testSpoofingRilevatoAutenticazioneOpzionaleSoggettoIdentificatoAltroSoggetto(data, dataMsg, id, checkServizioApplicativo);
	}
	@Test(groups={CostantiConnettori.ID_GRUPPO_CONNETTORI,HTTPS.ID_GRUPPO,HTTPS.ID_GRUPPO+".SPOOFING_RILEVATO_AUTHN_SSL_OPZIONALE_IDENTIFICATO_ALTRO_SOGGETTO"},dataProvider="SpoofingRilevatoAutenticazioneOpzionaleSoggettoIdentificatoAltroSoggetto",dependsOnMethods={"spoofingRilevatoAutenticazioneOpzionaleSoggettoIdentificatoAltroSoggetto_specificCode"})
	public void testSpoofingRilevatoAutenticazioneOpzionaleSoggettoIdentificatoAltroSoggetto_specificCode(DatabaseComponent data,DatabaseMsgDiagnosticiComponent dataMsg,String id,boolean checkServizioApplicativo) throws Exception{
		_testSpoofingRilevatoAutenticazioneOpzionaleSoggettoIdentificatoAltroSoggetto(data, dataMsg, id, checkServizioApplicativo);
	}
	private void _testSpoofingRilevatoAutenticazioneOpzionaleSoggettoIdentificatoAltroSoggetto(DatabaseComponent data,DatabaseMsgDiagnosticiComponent dataMsg,String id,boolean checkServizioApplicativo) throws Exception{
		try{
					
			Reporter.log("Controllo tracciamento richiesta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
			Reporter.log("Controllo valore Azione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedAzione(id, CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_HTTPS_AUTENTICAZIONE_PA_OPZIONALE));
			
			Reporter.log("Controllo tracciamento risposta con riferimento id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTraced(id));
			Reporter.log("Controllo tracciamento risposta valore Azione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedAzione(id, CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_HTTPS_AUTENTICAZIONE_PA_OPZIONALE));
			
			// Check msgDiag
			if(dataMsg!=null){
				
				String AUTENTICAZIONE_FALLITA_SPOOFING_ALTRO_SOGGETTO_PREFIX =	AUTENTICAZIONE_FALLITA_SPOOFING_PREFIX+
						" Identificato un soggetto (tramite profilo di interoperabilità) 'spc/Soggetto2conCredenzialiSSL' differente da quello identificato tramite il processo di autenticazione 'spc/Soggetto1conCredenzialiSSL'";
				AUTENTICAZIONE_FALLITA_SPOOFING_ALTRO_SOGGETTO_PREFIX = AUTENTICAZIONE_FALLITA_SPOOFING_ALTRO_SOGGETTO_PREFIX.replace("@SUBJECT@", 
						"CN=Soggetto1, OU=test, O=openspcoop.org, L=Pisa, ST=Italy, C=IT, EMAILADDRESS=apoli@link.it");
				Reporter.log("Controllo Messaggio (id:"+id+") msg["+AUTENTICAZIONE_FALLITA_SPOOFING_ALTRO_SOGGETTO_PREFIX+"]");
				Assert.assertTrue( 
						dataMsg.isTracedMessaggio(id, AUTENTICAZIONE_FALLITA_SPOOFING_ALTRO_SOGGETTO_PREFIX)
						);
				
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
	
	
	
	
	
	
	
	
	
	private CooperazioneBaseInformazioni infoFruitoreSoggetto1senzaCredenziali = CooperazioneSPCoopBase.getCooperazioneBaseInformazioni(CostantiTestSuite.SPCOOP_SOGGETTO_1_SENZA_CREDENZIALI,
			CostantiTestSuite.SPCOOP_SOGGETTO_EROGATORE,
			false,SPCoopCostanti.PROFILO_TRASMISSIONE_CON_DUPLICATI,Inoltro.CON_DUPLICATI);	
	private CooperazioneBase collaborazioneSPCoopFruitoreSoggetto1senzaCredenziali = 
		new CooperazioneBase(false,MessageType.SOAP_11,  this.infoFruitoreSoggetto1senzaCredenziali, 
				org.openspcoop2.protocol.spcoop.testsuite.core.TestSuiteProperties.getInstance(), 
				DatabaseProperties.getInstance(), SPCoopTestsuiteLogger.getInstance());
	Repository repositorySpoofingRilevatoAutenticazioneSoggettoNessunSoggettoIdentificato=new Repository();
	@Test(groups={CostantiConnettori.ID_GRUPPO_CONNETTORI,HTTPS.ID_GRUPPO,HTTPS.ID_GRUPPO+".SPOOFING_RILEVATO_AUTHN_SSL_IDENTIFICATE_CREDENZIALI_DIFFERENTI"},description="Test di tipo sincrono, Viene controllato se i body sono uguali e se gli attachment sono uguali")
	public void spoofingRilevatoAutenticazioneSoggettoNessunSoggettoIdentificato_genericCode_wrap() throws TestSuiteException, IOException, Exception{
		boolean genericCode = true;
		boolean unwrap = false;
		try {
			super.lockForCode(genericCode, unwrap);
			
			_spoofingRilevatoAutenticazioneSoggettoNessunSoggettoIdentificato(genericCode, unwrap);
		}finally {
			super.unlockForCode(genericCode);
		}
	}
	@Test(groups={CostantiConnettori.ID_GRUPPO_CONNETTORI,HTTPS.ID_GRUPPO,HTTPS.ID_GRUPPO+".SPOOFING_RILEVATO_AUTHN_SSL_IDENTIFICATE_CREDENZIALI_DIFFERENTI"},description="Test di tipo sincrono, Viene controllato se i body sono uguali e se gli attachment sono uguali")
	public void spoofingRilevatoAutenticazioneSoggettoNessunSoggettoIdentificato_genericCode_unwrap() throws TestSuiteException, IOException, Exception{
		boolean genericCode = true;
		boolean unwrap = true;
		try {
			super.lockForCode(genericCode, unwrap);
			
			_spoofingRilevatoAutenticazioneSoggettoNessunSoggettoIdentificato(genericCode, unwrap);
		}finally {
			super.unlockForCode(genericCode);
		}
	}
	@Test(groups={CostantiConnettori.ID_GRUPPO_CONNETTORI,HTTPS.ID_GRUPPO,HTTPS.ID_GRUPPO+".SPOOFING_RILEVATO_AUTHN_SSL_IDENTIFICATE_CREDENZIALI_DIFFERENTI"},description="Test di tipo sincrono, Viene controllato se i body sono uguali e se gli attachment sono uguali")
	public void spoofingRilevatoAutenticazioneSoggettoNessunSoggettoIdentificato_specificCode() throws TestSuiteException, IOException, Exception{
		boolean genericCode = false;
		boolean unwrap = false;
		try {
			super.lockForCode(genericCode, unwrap);
			
			_spoofingRilevatoAutenticazioneSoggettoNessunSoggettoIdentificato(genericCode, unwrap);
		}finally {
			super.unlockForCode(genericCode);
		}
	}
	private void _spoofingRilevatoAutenticazioneSoggettoNessunSoggettoIdentificato(boolean genericCode, boolean unwrap) throws TestSuiteException, IOException, Exception{
		
		java.io.FileInputStream fin = null;
		DatabaseComponent dbComponentFruitore = null;
		try{
			fin = new java.io.FileInputStream(new File(Utilities.testSuiteProperties.getSoap11FileName()));

			Message msg=new Message(fin);
			msg.getSOAPPartAsBytes();
			
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositorySpoofingRilevatoAutenticazioneSoggettoNessunSoggettoIdentificato);
			client.setSoapAction("\"TEST\"");
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_AUTORIZZAZIONE_SPCOOP_AUTENTICAZIONE_HTTPS_PA_SOGGETTO1_SENZA_CREDENZIALI);
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
				String msg2 = AUTENTICAZIONE_FALLITA_MITTENTE;
				msg2 = msg2.replace("@DESTINATARIO@", this.collaborazioneSPCoopFruitoreSoggetto1senzaCredenziali.getDestinatario().getTipo()+"/"+this.collaborazioneSPCoopFruitoreSoggetto1senzaCredenziali.getDestinatario().getNome());
				
				String codiceErrore = "EGOV_IT_101";
				
				if(genericCode) {
					IntegrationFunctionError integrationFunctionError = IntegrationFunctionError.WRAP_502_BAD_RESPONSE;
					if(unwrap) {
						integrationFunctionError = IntegrationFunctionError.INTEROPERABILITY_PROFILE_RESPONSE_ERROR;
					}
					ErroriProperties erroriProperties = ErroriProperties.getInstance(this.log);
					codiceErrore = org.openspcoop2.message.constants.Costanti.SOAP11_FAULT_CODE_SERVER +
							org.openspcoop2.message.constants.Costanti.SOAP11_FAULT_CODE_SEPARATOR+erroriProperties.getErrorType_noWrap(integrationFunctionError);
					if(erroriProperties.isForceGenericDetails_noWrap(integrationFunctionError)) {
						msg2 = erroriProperties.getGenericDetails_noWrap(integrationFunctionError);
					}
				}
				
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: ["+error.getFaultString()+"]");
				Reporter.log("Controllo fault actor ["+org.openspcoop2.testsuite.core.CostantiTestSuite.OPENSPCOOP2_INTEGRATION_ACTOR+"]");
				Assert.assertTrue(org.openspcoop2.testsuite.core.CostantiTestSuite.OPENSPCOOP2_INTEGRATION_ACTOR.equals(error.getFaultActor()));
				Reporter.log("Controllo fault code ["+codiceErrore+"]");
				Assert.assertTrue(codiceErrore.equals(error.getFaultCode().getLocalPart()));
												
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
				this.repositorySpoofingRilevatoAutenticazioneSoggettoNessunSoggettoIdentificato.add(client.getIdMessaggio());
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
	@DataProvider (name="SpoofingRilevatoAutenticazioneSoggettoNessunSoggettoIdentificato")
	public Object[][]testSpoofingRilevatoAutenticazioneSoggettoNessunSoggettoIdentificato()throws Exception{
		String id=this.repositorySpoofingRilevatoAutenticazioneSoggettoNessunSoggettoIdentificato.getNext();
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),null,id,false},	
				{DatabaseProperties.getDatabaseComponentErogatore(),DatabaseProperties.getDatabaseComponentDiagnosticaErogatore(),id,true}
		};
	}
	@Test(groups={CostantiConnettori.ID_GRUPPO_CONNETTORI,HTTPS.ID_GRUPPO,HTTPS.ID_GRUPPO+".SPOOFING_RILEVATO_AUTHN_SSL_IDENTIFICATE_CREDENZIALI_DIFFERENTI"},dataProvider="SpoofingRilevatoAutenticazioneSoggettoNessunSoggettoIdentificato",dependsOnMethods={"spoofingRilevatoAutenticazioneSoggettoNessunSoggettoIdentificato_genericCode_wrap"})
	public void testSpoofingRilevatoAutenticazioneSoggettoNessunSoggettoIdentificato_genericCode_wrap(DatabaseComponent data,DatabaseMsgDiagnosticiComponent dataMsg,String id,boolean checkServizioApplicativo) throws Exception{
		_testSpoofingRilevatoAutenticazioneSoggettoNessunSoggettoIdentificato(data, dataMsg, id, checkServizioApplicativo);
	}
	@Test(groups={CostantiConnettori.ID_GRUPPO_CONNETTORI,HTTPS.ID_GRUPPO,HTTPS.ID_GRUPPO+".SPOOFING_RILEVATO_AUTHN_SSL_IDENTIFICATE_CREDENZIALI_DIFFERENTI"},dataProvider="SpoofingRilevatoAutenticazioneSoggettoNessunSoggettoIdentificato",dependsOnMethods={"spoofingRilevatoAutenticazioneSoggettoNessunSoggettoIdentificato_genericCode_unwrap"})
	public void testSpoofingRilevatoAutenticazioneSoggettoNessunSoggettoIdentificato_genericCode_unwrap(DatabaseComponent data,DatabaseMsgDiagnosticiComponent dataMsg,String id,boolean checkServizioApplicativo) throws Exception{
		_testSpoofingRilevatoAutenticazioneSoggettoNessunSoggettoIdentificato(data, dataMsg, id, checkServizioApplicativo);
	}
	@Test(groups={CostantiConnettori.ID_GRUPPO_CONNETTORI,HTTPS.ID_GRUPPO,HTTPS.ID_GRUPPO+".SPOOFING_RILEVATO_AUTHN_SSL_IDENTIFICATE_CREDENZIALI_DIFFERENTI"},dataProvider="SpoofingRilevatoAutenticazioneSoggettoNessunSoggettoIdentificato",dependsOnMethods={"spoofingRilevatoAutenticazioneSoggettoNessunSoggettoIdentificato_specificCode"})
	public void testSpoofingRilevatoAutenticazioneSoggettoNessunSoggettoIdentificato_specificCode(DatabaseComponent data,DatabaseMsgDiagnosticiComponent dataMsg,String id,boolean checkServizioApplicativo) throws Exception{
		_testSpoofingRilevatoAutenticazioneSoggettoNessunSoggettoIdentificato(data, dataMsg, id, checkServizioApplicativo);
	}
	private void _testSpoofingRilevatoAutenticazioneSoggettoNessunSoggettoIdentificato(DatabaseComponent data,DatabaseMsgDiagnosticiComponent dataMsg,String id,boolean checkServizioApplicativo) throws Exception{
		try{
					
			Reporter.log("Controllo tracciamento richiesta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
			Reporter.log("Controllo valore Azione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedAzione(id, CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_HTTPS_AUTENTICAZIONE_PA));
			
			Reporter.log("Controllo tracciamento risposta con riferimento id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTraced(id));
			Reporter.log("Controllo tracciamento risposta valore Azione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedAzione(id, CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_HTTPS_AUTENTICAZIONE_PA));
			
			// Check msgDiag
			if(dataMsg!=null){
				
				String msgErrore = "Autenticazione [ssl] fallita : [RicezioneBuste] processo di autenticazione [ssl] fallito, Identificato un soggetto (tramite profilo di interoperabilità) 'spc/Soggetto1SenzaCredenziali' registrato con credenziali differenti da quelle ricevute"; 
				Reporter.log("Controllo Messaggio (id:"+id+") msg["+msgErrore+"]");
				boolean condition = dataMsg.isTracedMessaggio(id, msgErrore);
				if(!condition) {
					// jenkins venendo caricate entrambe le configurazioni si ha l'effetto che viene identificato un soggetto del protocollo trasparente:
					String msgErroreJenkins = "processo di autenticazione [ssl] fallito, Identificato un soggetto (tramite profilo di interoperabilità) 'spc/Soggetto1SenzaCredenziali' differente da quello identificato tramite il processo di autenticazione 'gw/EsempioSoggettoTrasparenteCert1'";
					condition = dataMsg.isTracedMessaggioWithLike(id, msgErroreJenkins);
				}
				if(!condition) {
					List<String> l = dataMsg.getMessaggiDiagnostici(id);
					Reporter.log("Presenti "+l.size()+" diagnostici");
					if(!l.isEmpty()) {
						for (String d : l) {
							Reporter.log("Diagnostico ["+d+"]");
						}
					}
				}
				Assert.assertTrue( 
						condition
						);

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
	
	
	
	
	
	
	
	
	
	
	
	
	
	

	private CooperazioneBaseInformazioni infoFruitoreSoggetto1senzaCredenzialiAuthnOpzionale = CooperazioneSPCoopBase.getCooperazioneBaseInformazioni(CostantiTestSuite.SPCOOP_SOGGETTO_1_SENZA_CREDENZIALI,
			CostantiTestSuite.SPCOOP_SOGGETTO_EROGATORE,
			false,SPCoopCostanti.PROFILO_TRASMISSIONE_CON_DUPLICATI,Inoltro.CON_DUPLICATI);	
	private CooperazioneBase collaborazioneSPCoopFruitoreSoggetto1senzaCredenzialiAuthnOpzionale = 
		new CooperazioneBase(false,MessageType.SOAP_11,  this.infoFruitoreSoggetto1senzaCredenzialiAuthnOpzionale, 
				org.openspcoop2.protocol.spcoop.testsuite.core.TestSuiteProperties.getInstance(), 
				DatabaseProperties.getInstance(), SPCoopTestsuiteLogger.getInstance());
	Repository repositorySpoofingRilevatoAutenticazioneSoggettoNessunSoggettoIdentificatoAuhtnOpzionale=new Repository();
	@Test(groups={CostantiConnettori.ID_GRUPPO_CONNETTORI,HTTPS.ID_GRUPPO,HTTPS.ID_GRUPPO+".SPOOFING_RILEVATO_AUTHN_SSL_OPZIONALE_IDENTIFICATE_CREDENZIALI_DIFFERENTI"},description="Test di tipo sincrono, Viene controllato se i body sono uguali e se gli attachment sono uguali")
	public void spoofingRilevatoAutenticazioneSoggettoNessunSoggettoIdentificatoAuhtnOpzionale_genericCode_wrap() throws TestSuiteException, IOException, Exception{
		boolean genericCode = true;
		boolean unwrap = false;
		try {
			super.lockForCode(genericCode, unwrap);
			
			_spoofingRilevatoAutenticazioneSoggettoNessunSoggettoIdentificatoAuhtnOpzionale(genericCode, unwrap);
		}finally {
			super.unlockForCode(genericCode);
		}
	}
	@Test(groups={CostantiConnettori.ID_GRUPPO_CONNETTORI,HTTPS.ID_GRUPPO,HTTPS.ID_GRUPPO+".SPOOFING_RILEVATO_AUTHN_SSL_OPZIONALE_IDENTIFICATE_CREDENZIALI_DIFFERENTI"},description="Test di tipo sincrono, Viene controllato se i body sono uguali e se gli attachment sono uguali")
	public void spoofingRilevatoAutenticazioneSoggettoNessunSoggettoIdentificatoAuhtnOpzionale_genericCode_unwrap() throws TestSuiteException, IOException, Exception{
		boolean genericCode = true;
		boolean unwrap = true;
		try {
			super.lockForCode(genericCode, unwrap);
			
			_spoofingRilevatoAutenticazioneSoggettoNessunSoggettoIdentificatoAuhtnOpzionale(genericCode, unwrap);
		}finally {
			super.unlockForCode(genericCode);
		}
	}
	@Test(groups={CostantiConnettori.ID_GRUPPO_CONNETTORI,HTTPS.ID_GRUPPO,HTTPS.ID_GRUPPO+".SPOOFING_RILEVATO_AUTHN_SSL_OPZIONALE_IDENTIFICATE_CREDENZIALI_DIFFERENTI"},description="Test di tipo sincrono, Viene controllato se i body sono uguali e se gli attachment sono uguali")
	public void spoofingRilevatoAutenticazioneSoggettoNessunSoggettoIdentificatoAuhtnOpzionale_specificCode() throws TestSuiteException, IOException, Exception{
		boolean genericCode = false;
		boolean unwrap = false;
		try {
			super.lockForCode(genericCode, unwrap);
			
			_spoofingRilevatoAutenticazioneSoggettoNessunSoggettoIdentificatoAuhtnOpzionale(genericCode, unwrap);
		}finally {
			super.unlockForCode(genericCode);
		}
	}
	private void _spoofingRilevatoAutenticazioneSoggettoNessunSoggettoIdentificatoAuhtnOpzionale(boolean genericCode, boolean unwrap) throws TestSuiteException, IOException, Exception{
		
		java.io.FileInputStream fin = null;
		DatabaseComponent dbComponentFruitore = null;
		try{
			fin = new java.io.FileInputStream(new File(Utilities.testSuiteProperties.getSoap11FileName()));

			Message msg=new Message(fin);
			msg.getSOAPPartAsBytes();
			
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositorySpoofingRilevatoAutenticazioneSoggettoNessunSoggettoIdentificatoAuhtnOpzionale);
			client.setSoapAction("\"TEST\"");
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_AUTORIZZAZIONE_SPCOOP_AUTENTICAZIONE_HTTPS_PA_OPZIONALE_SOGGETTO1_SENZA_CREDENZIALI);
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
				String msg2 = AUTENTICAZIONE_FALLITA_MITTENTE;
				msg2 = msg2.replace("@DESTINATARIO@", this.collaborazioneSPCoopFruitoreSoggetto1senzaCredenzialiAuthnOpzionale.getDestinatario().getTipo()+"/"+this.collaborazioneSPCoopFruitoreSoggetto1senzaCredenzialiAuthnOpzionale.getDestinatario().getNome());
				
				String codiceErrore = "EGOV_IT_101";
				
				if(genericCode) {
					IntegrationFunctionError integrationFunctionError = IntegrationFunctionError.WRAP_502_BAD_RESPONSE;
					if(unwrap) {
						integrationFunctionError = IntegrationFunctionError.INTEROPERABILITY_PROFILE_RESPONSE_ERROR;
					}
					ErroriProperties erroriProperties = ErroriProperties.getInstance(this.log);
					codiceErrore = org.openspcoop2.message.constants.Costanti.SOAP11_FAULT_CODE_SERVER +
							org.openspcoop2.message.constants.Costanti.SOAP11_FAULT_CODE_SEPARATOR+erroriProperties.getErrorType_noWrap(integrationFunctionError);
					if(erroriProperties.isForceGenericDetails_noWrap(integrationFunctionError)) {
						msg2 = erroriProperties.getGenericDetails_noWrap(integrationFunctionError);
					}
				}
				
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: ["+error.getFaultString()+"]");
				Reporter.log("Controllo fault actor ["+org.openspcoop2.testsuite.core.CostantiTestSuite.OPENSPCOOP2_INTEGRATION_ACTOR+"]");
				Assert.assertTrue(org.openspcoop2.testsuite.core.CostantiTestSuite.OPENSPCOOP2_INTEGRATION_ACTOR.equals(error.getFaultActor()));
				Reporter.log("Controllo fault code ["+codiceErrore+"]");
				Assert.assertTrue(codiceErrore.equals(error.getFaultCode().getLocalPart()));
												
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
				this.repositorySpoofingRilevatoAutenticazioneSoggettoNessunSoggettoIdentificatoAuhtnOpzionale.add(client.getIdMessaggio());
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
	@DataProvider (name="SpoofingRilevatoAutenticazioneSoggettoNessunSoggettoIdentificatoAuhtnOpzionale")
	public Object[][]testSpoofingRilevatoAutenticazioneSoggettoNessunSoggettoIdentificatoAuhtnOpzionale()throws Exception{
		String id=this.repositorySpoofingRilevatoAutenticazioneSoggettoNessunSoggettoIdentificatoAuhtnOpzionale.getNext();
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),null,id,false},	
				{DatabaseProperties.getDatabaseComponentErogatore(),DatabaseProperties.getDatabaseComponentDiagnosticaErogatore(),id,true}
		};
	}
	@Test(groups={CostantiConnettori.ID_GRUPPO_CONNETTORI,HTTPS.ID_GRUPPO,HTTPS.ID_GRUPPO+".SPOOFING_RILEVATO_AUTHN_SSL_OPZIONALE_IDENTIFICATE_CREDENZIALI_DIFFERENTI"},dataProvider="SpoofingRilevatoAutenticazioneSoggettoNessunSoggettoIdentificatoAuhtnOpzionale",dependsOnMethods={"spoofingRilevatoAutenticazioneSoggettoNessunSoggettoIdentificatoAuhtnOpzionale_genericCode_wrap"})
	public void testSpoofingRilevatoAutenticazioneSoggettoNessunSoggettoIdentificatoAuhtnOpzionale_genericCode_wrap(DatabaseComponent data,DatabaseMsgDiagnosticiComponent dataMsg,String id,boolean checkServizioApplicativo) throws Exception{
		_testSpoofingRilevatoAutenticazioneSoggettoNessunSoggettoIdentificatoAuhtnOpzionale(data, dataMsg, id, checkServizioApplicativo);
	}
	@Test(groups={CostantiConnettori.ID_GRUPPO_CONNETTORI,HTTPS.ID_GRUPPO,HTTPS.ID_GRUPPO+".SPOOFING_RILEVATO_AUTHN_SSL_OPZIONALE_IDENTIFICATE_CREDENZIALI_DIFFERENTI"},dataProvider="SpoofingRilevatoAutenticazioneSoggettoNessunSoggettoIdentificatoAuhtnOpzionale",dependsOnMethods={"spoofingRilevatoAutenticazioneSoggettoNessunSoggettoIdentificatoAuhtnOpzionale_genericCode_unwrap"})
	public void testSpoofingRilevatoAutenticazioneSoggettoNessunSoggettoIdentificatoAuhtnOpzionale_genericCode_unwrap(DatabaseComponent data,DatabaseMsgDiagnosticiComponent dataMsg,String id,boolean checkServizioApplicativo) throws Exception{
		_testSpoofingRilevatoAutenticazioneSoggettoNessunSoggettoIdentificatoAuhtnOpzionale(data, dataMsg, id, checkServizioApplicativo);
	}
	@Test(groups={CostantiConnettori.ID_GRUPPO_CONNETTORI,HTTPS.ID_GRUPPO,HTTPS.ID_GRUPPO+".SPOOFING_RILEVATO_AUTHN_SSL_OPZIONALE_IDENTIFICATE_CREDENZIALI_DIFFERENTI"},dataProvider="SpoofingRilevatoAutenticazioneSoggettoNessunSoggettoIdentificatoAuhtnOpzionale",dependsOnMethods={"spoofingRilevatoAutenticazioneSoggettoNessunSoggettoIdentificatoAuhtnOpzionale_specificCode"})
	public void testSpoofingRilevatoAutenticazioneSoggettoNessunSoggettoIdentificatoAuhtnOpzionale_specificCode(DatabaseComponent data,DatabaseMsgDiagnosticiComponent dataMsg,String id,boolean checkServizioApplicativo) throws Exception{
		_testSpoofingRilevatoAutenticazioneSoggettoNessunSoggettoIdentificatoAuhtnOpzionale(data, dataMsg, id, checkServizioApplicativo);
	}
	private void _testSpoofingRilevatoAutenticazioneSoggettoNessunSoggettoIdentificatoAuhtnOpzionale(DatabaseComponent data,DatabaseMsgDiagnosticiComponent dataMsg,String id,boolean checkServizioApplicativo) throws Exception{
		try{
					
			Reporter.log("Controllo tracciamento richiesta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
			Reporter.log("Controllo valore Azione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedAzione(id, CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_HTTPS_AUTENTICAZIONE_PA_OPZIONALE));
			
			Reporter.log("Controllo tracciamento risposta con riferimento id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTraced(id));
			Reporter.log("Controllo tracciamento risposta valore Azione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedAzione(id, CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_HTTPS_AUTENTICAZIONE_PA_OPZIONALE));
			
			// Check msgDiag
			if(dataMsg!=null){
				
				String msgErrore = "Autenticazione [ssl] fallita : [RicezioneBuste] processo di autenticazione [ssl] fallito, Identificato un soggetto (tramite profilo di interoperabilità) 'spc/Soggetto1SenzaCredenziali' registrato con credenziali differenti da quelle ricevute"; 
				Reporter.log("Controllo Messaggio (id:"+id+") msg["+msgErrore+"]");
				boolean condition = dataMsg.isTracedMessaggio(id, msgErrore);
				if(!condition) {
					// jenkins venendo caricate entrambe le configurazioni si ha l'effetto che viene identificato un soggetto del protocollo trasparente:
					String msgErroreJenkins = "processo di autenticazione [ssl] fallito, Identificato un soggetto (tramite profilo di interoperabilità) 'spc/Soggetto1SenzaCredenziali' differente da quello identificato tramite il processo di autenticazione 'gw/EsempioSoggettoTrasparenteCert1'";
					condition = dataMsg.isTracedMessaggioWithLike(id, msgErroreJenkins);
				}
				if(!condition) {
					List<String> l = dataMsg.getMessaggiDiagnostici(id);
					Reporter.log("Presenti "+l.size()+" diagnostici");
					if(!l.isEmpty()) {
						for (String d : l) {
							Reporter.log("Diagnostico ["+d+"]");
						}
					}
				}
				Assert.assertTrue( 
						condition
						);

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
	
	
	
	
	
	
	
	private CooperazioneBaseInformazioni infoFruitoreSoggetto2senzaCredenziali = CooperazioneSPCoopBase.getCooperazioneBaseInformazioni(CostantiTestSuite.SPCOOP_SOGGETTO_2_SENZA_CREDENZIALI,
			CostantiTestSuite.SPCOOP_SOGGETTO_EROGATORE,
			false,SPCoopCostanti.PROFILO_TRASMISSIONE_CON_DUPLICATI,Inoltro.CON_DUPLICATI);	
	private CooperazioneBase collaborazioneSPCoopFruitoreSoggetto2senzaCredenziali = 
		new CooperazioneBase(false,MessageType.SOAP_11,  this.infoFruitoreSoggetto2senzaCredenziali, 
				org.openspcoop2.protocol.spcoop.testsuite.core.TestSuiteProperties.getInstance(), 
				DatabaseProperties.getInstance(), SPCoopTestsuiteLogger.getInstance());
	Repository repositoryAutenticazioneHttpsCredenzialiNonFornite=new Repository();
	@Test(groups={CostantiConnettori.ID_GRUPPO_CONNETTORI,HTTPS.ID_GRUPPO,HTTPS.ID_GRUPPO+".CREDENZIALI_NON_FORNITE"},description="Test di tipo sincrono, Viene controllato se i body sono uguali e se gli attachment sono uguali")
	public void autenticazioneHttpsCredenzialiNonFornite_genericCode_wrap() throws TestSuiteException, IOException, Exception{
		boolean genericCode = true;
		boolean unwrap = false;
		try {
			super.lockForCode(genericCode, unwrap);
			
			_autenticazioneHttpsCredenzialiNonFornite(genericCode, unwrap);
		}finally {
			super.unlockForCode(genericCode);
		}
	}
	@Test(groups={CostantiConnettori.ID_GRUPPO_CONNETTORI,HTTPS.ID_GRUPPO,HTTPS.ID_GRUPPO+".CREDENZIALI_NON_FORNITE"},description="Test di tipo sincrono, Viene controllato se i body sono uguali e se gli attachment sono uguali")
	public void autenticazioneHttpsCredenzialiNonFornite_genericCode_unwrap() throws TestSuiteException, IOException, Exception{
		boolean genericCode = true;
		boolean unwrap = true;
		try {
			super.lockForCode(genericCode, unwrap);
			
			_autenticazioneHttpsCredenzialiNonFornite(genericCode, unwrap);
		}finally {
			super.unlockForCode(genericCode);
		}
	}
	@Test(groups={CostantiConnettori.ID_GRUPPO_CONNETTORI,HTTPS.ID_GRUPPO,HTTPS.ID_GRUPPO+".CREDENZIALI_NON_FORNITE"},description="Test di tipo sincrono, Viene controllato se i body sono uguali e se gli attachment sono uguali")
	public void autenticazioneHttpsCredenzialiNonFornite_specificCode() throws TestSuiteException, IOException, Exception{
		boolean genericCode = false;
		boolean unwrap = false;
		try {
			super.lockForCode(genericCode, unwrap);
			
			_autenticazioneHttpsCredenzialiNonFornite(genericCode, unwrap);
		}finally {
			super.unlockForCode(genericCode);
		}
	}
	private void _autenticazioneHttpsCredenzialiNonFornite(boolean genericCode, boolean unwrap) throws TestSuiteException, IOException, Exception{
		
		java.io.FileInputStream fin = null;
		DatabaseComponent dbComponentFruitore = null;
		try{
			fin = new java.io.FileInputStream(new File(Utilities.testSuiteProperties.getSoap11FileName()));

			Message msg=new Message(fin);
			msg.getSOAPPartAsBytes();
			
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryAutenticazioneHttpsCredenzialiNonFornite);
			client.setSoapAction("\"TEST\"");
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_AUTORIZZAZIONE_SPCOOP_AUTENTICAZIONE_HTTPS_PA_SOGGETTO2_SENZA_CREDENZIALI);
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
				String msg2 = AUTENTICAZIONE_FALLITA_CREDENZIALI_NON_FORNITE;
				msg2 = msg2.replace("@DESTINATARIO@", this.collaborazioneSPCoopFruitoreSoggetto2senzaCredenziali.getDestinatario().getTipo()+"/"+this.collaborazioneSPCoopFruitoreSoggetto2senzaCredenziali.getDestinatario().getNome());
				
				String codiceErrore = "EGOV_IT_101";
				
				if(genericCode) {
					IntegrationFunctionError integrationFunctionError = IntegrationFunctionError.WRAP_502_BAD_RESPONSE;
					if(unwrap) {
						integrationFunctionError = IntegrationFunctionError.INTEROPERABILITY_PROFILE_RESPONSE_ERROR;
					}
					ErroriProperties erroriProperties = ErroriProperties.getInstance(this.log);
					codiceErrore = org.openspcoop2.message.constants.Costanti.SOAP11_FAULT_CODE_SERVER +
							org.openspcoop2.message.constants.Costanti.SOAP11_FAULT_CODE_SEPARATOR+erroriProperties.getErrorType_noWrap(integrationFunctionError);
					if(erroriProperties.isForceGenericDetails_noWrap(integrationFunctionError)) {
						msg2 = erroriProperties.getGenericDetails_noWrap(integrationFunctionError);
					}
				}
				
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: ["+error.getFaultString()+"]");
				Reporter.log("Controllo fault actor ["+org.openspcoop2.testsuite.core.CostantiTestSuite.OPENSPCOOP2_INTEGRATION_ACTOR+"]");
				Assert.assertTrue(org.openspcoop2.testsuite.core.CostantiTestSuite.OPENSPCOOP2_INTEGRATION_ACTOR.equals(error.getFaultActor()));
				Reporter.log("Controllo fault code ["+codiceErrore+"]");
				Assert.assertTrue(codiceErrore.equals(error.getFaultCode().getLocalPart()));
												
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
				this.repositoryAutenticazioneHttpsCredenzialiNonFornite.add(client.getIdMessaggio());
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
	@DataProvider (name="AutenticazioneHttpsCredenzialiNonFornite")
	public Object[][]testAutenticazioneHttpsCredenzialiNonFornite()throws Exception{
		String id=this.repositoryAutenticazioneHttpsCredenzialiNonFornite.getNext();
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),null,id,false},	
				{DatabaseProperties.getDatabaseComponentErogatore(),DatabaseProperties.getDatabaseComponentDiagnosticaErogatore(),id,true}
		};
	}
	@Test(groups={CostantiConnettori.ID_GRUPPO_CONNETTORI,HTTPS.ID_GRUPPO,HTTPS.ID_GRUPPO+".CREDENZIALI_NON_FORNITE"},dataProvider="AutenticazioneHttpsCredenzialiNonFornite",dependsOnMethods={"autenticazioneHttpsCredenzialiNonFornite_genericCode_wrap"})
	public void testAutenticazioneHttpsCredenzialiNonFornite_genericCode_wrap(DatabaseComponent data,DatabaseMsgDiagnosticiComponent dataMsg,String id,boolean checkServizioApplicativo) throws Exception{
		_testAutenticazioneHttpsCredenzialiNonFornite(data, dataMsg, id, checkServizioApplicativo);
	}
	@Test(groups={CostantiConnettori.ID_GRUPPO_CONNETTORI,HTTPS.ID_GRUPPO,HTTPS.ID_GRUPPO+".CREDENZIALI_NON_FORNITE"},dataProvider="AutenticazioneHttpsCredenzialiNonFornite",dependsOnMethods={"autenticazioneHttpsCredenzialiNonFornite_genericCode_unwrap"})
	public void testAutenticazioneHttpsCredenzialiNonFornite_genericCode_unwrap(DatabaseComponent data,DatabaseMsgDiagnosticiComponent dataMsg,String id,boolean checkServizioApplicativo) throws Exception{
		_testAutenticazioneHttpsCredenzialiNonFornite(data, dataMsg, id, checkServizioApplicativo);
	}
	@Test(groups={CostantiConnettori.ID_GRUPPO_CONNETTORI,HTTPS.ID_GRUPPO,HTTPS.ID_GRUPPO+".CREDENZIALI_NON_FORNITE"},dataProvider="AutenticazioneHttpsCredenzialiNonFornite",dependsOnMethods={"autenticazioneHttpsCredenzialiNonFornite_specificCode"})
	public void testAutenticazioneHttpsCredenzialiNonFornite_specificCode(DatabaseComponent data,DatabaseMsgDiagnosticiComponent dataMsg,String id,boolean checkServizioApplicativo) throws Exception{
		_testAutenticazioneHttpsCredenzialiNonFornite(data, dataMsg, id, checkServizioApplicativo);
	}
	private void _testAutenticazioneHttpsCredenzialiNonFornite(DatabaseComponent data,DatabaseMsgDiagnosticiComponent dataMsg,String id,boolean checkServizioApplicativo) throws Exception{
		try{
					
			Reporter.log("Controllo tracciamento richiesta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
			Reporter.log("Controllo valore Azione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedAzione(id, CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_HTTPS_AUTENTICAZIONE_PA));
			
			Reporter.log("Controllo tracciamento risposta con riferimento id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTraced(id));
			Reporter.log("Controllo tracciamento risposta valore Azione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedAzione(id, CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_HTTPS_AUTENTICAZIONE_PA));
			
			// Check msgDiag
			if(dataMsg!=null){
				
				String msgErrore = "Autenticazione fallita, credenziali non fornite"; 
				Reporter.log("Controllo Messaggio (id:"+id+") msg["+msgErrore+"]");
				Assert.assertTrue( 
						dataMsg.isTracedMessaggioWithLike(id, msgErrore)
						);

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
	
	
	
	
	
	
	

	private CooperazioneBaseInformazioni infoFruitoreSoggetto2senzaCredenzialiAuthnOpzionale = CooperazioneSPCoopBase.getCooperazioneBaseInformazioni(CostantiTestSuite.SPCOOP_SOGGETTO_2_SENZA_CREDENZIALI,
			CostantiTestSuite.SPCOOP_SOGGETTO_EROGATORE,
			false,SPCoopCostanti.PROFILO_TRASMISSIONE_CON_DUPLICATI,Inoltro.CON_DUPLICATI);	
	private CooperazioneBase collaborazioneSPCoopFruitoreSoggetto2senzaCredenzialiAuthnOpzionale = 
		new CooperazioneBase(false,MessageType.SOAP_11,  this.infoFruitoreSoggetto2senzaCredenzialiAuthnOpzionale, 
				org.openspcoop2.protocol.spcoop.testsuite.core.TestSuiteProperties.getInstance(), 
				DatabaseProperties.getInstance(), SPCoopTestsuiteLogger.getInstance());
	
	Repository repositoryHTTPSAutorizzazioneSPCoopOkSoggetto2senzaCredenzialiAuthnOpzionale=new Repository();
	@Test(groups={CostantiConnettori.ID_GRUPPO_CONNETTORI,HTTPS.ID_GRUPPO,HTTPS.ID_GRUPPO+".AUTENTICAZIONE_HTTPS_OPZIONALE_AUTORIZZAZIONE_SPCOOP_FRUITORE_SENZA_CREDENZIALI"},description="Test di tipo sincrono, Viene controllato se i body sono uguali e se gli attachment sono uguali")
	public void httpsAutorizzazioneSPCoopOkSoggetto2senzaCredenzialiAuthnOpzionale() throws TestSuiteException, IOException, SOAPException{
		this.collaborazioneSPCoopFruitoreSoggetto2senzaCredenzialiAuthnOpzionale.sincrono(this.repositoryHTTPSAutorizzazioneSPCoopOkSoggetto2senzaCredenzialiAuthnOpzionale,
				CostantiTestSuite.PORTA_DELEGATA_AUTORIZZAZIONE_SPCOOP_AUTENTICAZIONE_HTTPS_PA_OPZIONALE_SOGGETTO2_SENZA_CREDENZIALI,addIDUnivoco);
	}
	@DataProvider (name="httpsAutorizzazioneSPCoopOkSoggetto2senzaCredenzialiAuthnOpzionale")
	public Object[][]testHttpsAutorizzazioneSPCoopOkSoggetto2senzaCredenzialiAuthnOpzionale()throws Exception{
		String id=this.repositoryHTTPSAutorizzazioneSPCoopOkSoggetto2senzaCredenzialiAuthnOpzionale.getNext();
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),null,id,false},	
				{DatabaseProperties.getDatabaseComponentErogatore(),DatabaseProperties.getDatabaseComponentDiagnosticaErogatore(),id,true}	
		};
	}
	@Test(groups={CostantiConnettori.ID_GRUPPO_CONNETTORI,HTTPS.ID_GRUPPO,HTTPS.ID_GRUPPO+".AUTENTICAZIONE_HTTPS_OPZIONALE_AUTORIZZAZIONE_SPCOOP_FRUITORE_SENZA_CREDENZIALI"},dataProvider="httpsAutorizzazioneSPCoopOkSoggetto2senzaCredenzialiAuthnOpzionale",dependsOnMethods={"httpsAutorizzazioneSPCoopOkSoggetto2senzaCredenzialiAuthnOpzionale"})
	public void testHttpsAutorizzazioneSPCoopOkSoggetto2senzaCredenzialiAuthnOpzionale(DatabaseComponent data,DatabaseMsgDiagnosticiComponent dataMsg,String id,boolean checkServizioApplicativo) throws Exception{
		try{
			this.collaborazioneSPCoopFruitoreSoggetto2senzaCredenzialiAuthnOpzionale.testSincrono(data, id, CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_HTTPS_AUTENTICAZIONE_PA_OPZIONALE, 
					checkServizioApplicativo,null);
			
			// Check msgDiag
			if(dataMsg!=null){
				
				String msg1 = "Autenticazione [ssl] in corso ...";
				Reporter.log("Controllo messaggio (id:"+id+") msg["+msg1+"]");
				Assert.assertTrue(dataMsg.isTracedMessaggio(id, msg1));
				
				msg1 = "Autenticazione [ssl] 'opzionale' fallita";
				Reporter.log("Controllo messaggio (id:"+id+") msg["+msg1+"]");
				Assert.assertTrue(dataMsg.isTracedMessaggioWithLike(id, msg1));
				
				msg1 = "Autenticazione fallita, credenziali non fornite";
				Reporter.log("Controllo messaggio (id:"+id+") msg["+msg1+"]");
				Assert.assertTrue(dataMsg.isTracedMessaggioWithLike(id, msg1));
				
				msg1 = MESSAGGIO_SPCOOP_RICEVUTO_AUTENTICAZIONE_HTTPS.replace("@IDEGOV@", id);
				msg1 = msg1.replace("@MITTENTE@", this.collaborazioneSPCoopFruitoreSoggetto2senzaCredenzialiAuthnOpzionale.getMittente().getTipo()+"/"+this.collaborazioneSPCoopFruitoreSoggetto2senzaCredenzialiAuthnOpzionale.getMittente().getNome());
				Reporter.log("Controllo messaggio (id:"+id+") msg["+msg1+"]");
				Assert.assertTrue(dataMsg.isTracedMessaggioWithLike(id, msg1));
				
				String msg2 = AUTORIZZAZIONE_IN_CORSO_AUTENTICAZIONE_HTTPS.replace("@IDEGOV@", id);
				msg2 = msg2.replace("@MITTENTE@", this.collaborazioneSPCoopFruitoreSoggetto2senzaCredenzialiAuthnOpzionale.getMittente().getTipo()+"/"+this.collaborazioneSPCoopFruitoreSoggetto2senzaCredenzialiAuthnOpzionale.getMittente().getNome());
				msg2 = msg2.replace("@DESTINATARIO@", this.collaborazioneSPCoopFruitoreSoggetto2senzaCredenzialiAuthnOpzionale.getDestinatario().getTipo()+"/"+this.collaborazioneSPCoopFruitoreSoggetto2senzaCredenzialiAuthnOpzionale.getDestinatario().getNome());
				msg2 = msg2.replace("@SERVIZIO@", CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO+"/"+CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO);
				msg2 = msg2.replace("@AZIONE@", CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_HTTPS_AUTENTICAZIONE_PA_OPZIONALE);
				Reporter.log("Controllo messaggio (id:"+id+") msg["+msg2+"]");
				Assert.assertTrue(dataMsg.isTracedMessaggioWithLike(id, msg2));			
				msg2 = AUTORIZZAZIONE_EFFETTUATA_AUTENTICAZIONE_HTTPS.replace("@IDEGOV@", id);
				msg2 = msg2.replace("@MITTENTE@", this.collaborazioneSPCoopFruitoreSoggetto2senzaCredenzialiAuthnOpzionale.getMittente().getTipo()+"/"+this.collaborazioneSPCoopFruitoreSoggetto2senzaCredenzialiAuthnOpzionale.getMittente().getNome());
				msg2 = msg2.replace("@DESTINATARIO@", this.collaborazioneSPCoopFruitoreSoggetto2senzaCredenzialiAuthnOpzionale.getDestinatario().getTipo()+"/"+this.collaborazioneSPCoopFruitoreSoggetto2senzaCredenzialiAuthnOpzionale.getDestinatario().getNome());
				msg2 = msg2.replace("@SERVIZIO@", CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO+"/"+CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO);
				msg2 = msg2.replace("@AZIONE@", CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_HTTPS_AUTENTICAZIONE_PA_OPZIONALE);
				Reporter.log("Controllo messaggio (id:"+id+") msg["+msg2+"]");
				Assert.assertTrue(dataMsg.isTracedMessaggioWithLike(id, msg2));
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
