/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it). 
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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.Date;
import java.util.List;

import javax.xml.soap.SOAPException;

import org.apache.axis.AxisFault;
import org.apache.axis.Message;
import org.apache.axis.message.MessageElement;
import org.openspcoop2.pdd.core.credenziali.GestoreCredenzialiTest;
import org.openspcoop2.pdd.mdb.Imbustamento;
import org.openspcoop2.pdd.mdb.InoltroBuste;
import org.openspcoop2.pdd.services.axis14.IntegrationManagerException;
import org.openspcoop2.pdd.services.axis14.IntegrationManagerMessage;
import org.openspcoop2.pdd.services.connector.RicezioneContenutiApplicativiHTTPtoSOAPConnector;
import org.openspcoop2.pdd.services.core.RicezioneContenutiApplicativi;
import org.openspcoop2.protocol.sdk.constants.CodiceErroreIntegrazione;
import org.openspcoop2.protocol.sdk.constants.IntegrationFunctionError;
import org.openspcoop2.protocol.spcoop.constants.SPCoopCostanti;
import org.openspcoop2.protocol.spcoop.testsuite.core.CostantiErroriIntegrazione;
import org.openspcoop2.protocol.spcoop.testsuite.core.CostantiTestSuite;
import org.openspcoop2.protocol.spcoop.testsuite.core.DatabaseProperties;
import org.openspcoop2.protocol.spcoop.testsuite.core.FileSystemUtilities;
import org.openspcoop2.protocol.spcoop.testsuite.core.SPCoopTestsuiteLogger;
import org.openspcoop2.protocol.spcoop.testsuite.core.Utilities;
import org.openspcoop2.protocol.spcoop.testsuite.core.UtilitiesEGov;
import org.openspcoop2.protocol.spcoop.testsuite.units.integration_manager.IntegrationManager;
import org.openspcoop2.protocol.utils.ErroriProperties;
import org.openspcoop2.testsuite.clients.ClientHttpGenerico;
import org.openspcoop2.testsuite.core.ErroreAttesoOpenSPCoopLogCore;
import org.openspcoop2.testsuite.core.Repository;
import org.openspcoop2.testsuite.core.SOAPEngine;
import org.openspcoop2.testsuite.core.TestSuiteException;
import org.openspcoop2.testsuite.core.TestSuiteProperties;
import org.openspcoop2.testsuite.db.DatabaseComponent;
import org.openspcoop2.testsuite.units.GestioneViaJmx;
import org.openspcoop2.utils.date.DateManager;
import org.slf4j.Logger;
import org.testng.Assert;
import org.testng.Reporter;


/**
 * Test su richieste applicative malformate indirizzate alla Porta di Dominio
 * 
 * @author Andi Rexha (rexha@openspcoop.org)
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class RichiesteApplicativeScorrette extends GestioneViaJmx {

	/** Identificativo del gruppo */
	public static final String ID_GRUPPO = "RichiesteApplicativeScorrette";
	
	
	
	private Logger log = SPCoopTestsuiteLogger.getInstance();
	
	private boolean genericCode = false;
	
	protected RichiesteApplicativeScorrette(boolean genericCode) {
		super(org.openspcoop2.protocol.spcoop.testsuite.core.TestSuiteProperties.getInstance());
		this.genericCode = genericCode;
	}

	private void _lock() throws Exception {
		this._lock(false);
	}
	private void _lock(boolean unwrap) throws Exception {
		super.lockForCode(this.genericCode, unwrap);
	}
	private void _unlock() throws Exception {
		super.unlockForCode(this.genericCode);
	}
	
	private Date dataAvvioGruppoTest = null;
	protected void _testOpenspcoopCoreLog_raccoltaTempoAvvioTest() throws Exception{
		this.dataAvvioGruppoTest = DateManager.getDate();
	} 	
	private List<ErroreAttesoOpenSPCoopLogCore> erroriAttesiOpenSPCoopCore = new java.util.ArrayList<>();
	protected void _testOpenspcoopCoreLog() throws Exception{
		if(this.erroriAttesiOpenSPCoopCore.size()>0){
			FileSystemUtilities.verificaOpenspcoopCore(this.dataAvvioGruppoTest,
					this.erroriAttesiOpenSPCoopCore.toArray(new ErroreAttesoOpenSPCoopLogCore[1]));
		}else{
			FileSystemUtilities.verificaOpenspcoopCore(this.dataAvvioGruppoTest);
		}
	} 
	
	
	


	
	
	
	
	

	/**
	 * Porta delegata non esistente
	 * "401";
	 */
	protected Object[][] _porteDelegateInesistenti(){
		return new Object[][]{{"HelloWorl"},
				{"Helloworld"},
				{""},
				{"jkflkasjfdlsjal;0"},
				{"/"},
		};
	}
	protected void _testPorteDelegateInesistenti(String portaDelegata) throws Exception{

		DatabaseComponent dbComponentFruitore = null;
		DatabaseComponent dbComponentErogatore = null;
		Date dataInizioTest = null;
		Date dataFineTest = null;
		try{
			_lock();
			dataInizioTest = DateManager.getDate();
			
			ClientHttpGenerico client=new ClientHttpGenerico(new Repository());
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(portaDelegata);
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

				Reporter.log("Invocazione porta delegata inesistente ("+portaDelegata+") non ha causato errori.");
				throw new TestSuiteException("Invocazione porta delegata inesistente ("+portaDelegata+") non ha causato errori.");

			} catch (AxisFault error) {
				
				Assert.assertTrue(client.getCodiceStatoHTTP()==500);
				
				String codiceEccezioneAtteso = Utilities.toString(CodiceErroreIntegrazione.CODICE_401_PORTA_INESISTENTE);
				String descrizioneEccezioneAttesa = CostantiErroriIntegrazione.MSG_401_PD_INESISTENTE;
				boolean checkDescrizioneTramiteMatchEsatto = Utilities.CONTROLLO_DESCRIZIONE_TRAMITE_METODO_CONTAINS;
				if(this.genericCode) {
					IntegrationFunctionError integrationFunctionError = IntegrationFunctionError.API_OUT_UNKNOWN;
					ErroriProperties erroriProperties = ErroriProperties.getInstance(this.log); 
					codiceEccezioneAtteso = erroriProperties.getErrorType_noWrap(integrationFunctionError);
					if(erroriProperties.isForceGenericDetails_noWrap(integrationFunctionError)) {
						descrizioneEccezioneAttesa = erroriProperties.getGenericDetails_noWrap(integrationFunctionError);
						checkDescrizioneTramiteMatchEsatto = Utilities.CONTROLLO_DESCRIZIONE_TRAMITE_METODO_EQUALS;
					}
				}
	
				Utilities.verificaFaultIntegrazione(error, 
						Utilities.testSuiteProperties.getIdentitaDefault_dominio(),RicezioneContenutiApplicativi.ID_MODULO, 
						codiceEccezioneAtteso, descrizioneEccezioneAttesa, checkDescrizioneTramiteMatchEsatto);				
			}finally{
				dbComponentFruitore.close();
				dbComponentErogatore.close();
			}
		}catch(Exception e){
			throw e;
		}finally{
			dbComponentFruitore.close();
			dbComponentErogatore.close();
			
			this._unlock();
			dataFineTest = DateManager.getDate();
		}
		
		// Aggiungo errori attesi
//		if("HelloWorl".equals(portaDelegata) ||
//				"Helloworld".equals(portaDelegata) ||
//				"jkflkasjfdlsjal;0".equals(portaDelegata) ){
		ErroreAttesoOpenSPCoopLogCore err = new ErroreAttesoOpenSPCoopLogCore();
		err.setIntervalloInferiore(dataInizioTest);
		err.setIntervalloSuperiore(dataFineTest);
		err.setMsgErrore("La porta invocata non esiste porta["+portaDelegata+"] urlInvocazione[/govway/spcoop/out/"+portaDelegata+"]");
		this.erroriAttesiOpenSPCoopCore.add(err);
		
		ErroreAttesoOpenSPCoopLogCore err2 = new ErroreAttesoOpenSPCoopLogCore();
		err2.setIntervalloInferiore(dataInizioTest);
		err2.setIntervalloSuperiore(dataFineTest);
		err2.setMsgErrore("nella url di invocazione alla Porta di Dominio non e' stata fornita il nome di una Porta");
		this.erroriAttesiOpenSPCoopCore.add(err2);
		
		//}
		
	}


	
	
	
	
	
	
	/**
	 * Autenticazione fallita
	 * "402";
	 * 
	 * messaggio: credenziali non fornite
	 */
	protected Object[][] _credenzialiNonFornite(){
		return new Object[][]{
				{null,null},//username e password null
				{"adminSilX",null},//username corretto, password null
				{null,"123456"},//username null, password corretta
				{"",""},//username e password vuote
				{"","password"},//username vuoto password errata
				{"","123456"},//username vuoto password corretta
				{"adminSilX",""}//user corretto, password vuoto
		};
	}
	protected void _testAutenticazioneCredenzialiNonFornite(String username,String password) throws Exception{
		DatabaseComponent dbComponentFruitore = null;
		DatabaseComponent dbComponentErogatore = null;

		Date dataInizioTest = null;
		Date dataFineTest = null;
		try{
			_lock();
			dataInizioTest = DateManager.getDate();
			
			ClientHttpGenerico client=new ClientHttpGenerico(new Repository());
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_AUTENTICAZIONE_BASIC);
			client.connectToSoapEngine();
			client.setAutenticazione(username,password);
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

				Reporter.log("Invocazione porta delegata con credenziali non fornite (user: "+username+" password:"+password+") non ha causato errori.");
				throw new TestSuiteException("Invocazione porta delegata con credenziali non fornite (user: "+username+" password:"+password+") non ha causato errori.");

			} catch (AxisFault error) {
				
				Assert.assertTrue(client.getCodiceStatoHTTP()==500);
				
				String codiceEccezioneAtteso = Utilities.toString(CodiceErroreIntegrazione.CODICE_402_AUTENTICAZIONE_FALLITA);
				String descrizioneEccezioneAttesa = CostantiErroriIntegrazione.MSG_402_AUTENTICAZIONE_FALLITA_CREDENZIALI_NON_FORNITE;
				boolean checkDescrizioneTramiteMatchEsatto = Utilities.CONTROLLO_DESCRIZIONE_TRAMITE_METODO_CONTAINS;
				if(this.genericCode) {
					IntegrationFunctionError integrationFunctionError = IntegrationFunctionError.AUTHENTICATION_CREDENTIALS_NOT_FOUND;
					ErroriProperties erroriProperties = ErroriProperties.getInstance(this.log); 
					codiceEccezioneAtteso = erroriProperties.getErrorType_noWrap(integrationFunctionError);
					if(erroriProperties.isForceGenericDetails_noWrap(integrationFunctionError)) {
						descrizioneEccezioneAttesa = erroriProperties.getGenericDetails_noWrap(integrationFunctionError);
						checkDescrizioneTramiteMatchEsatto = Utilities.CONTROLLO_DESCRIZIONE_TRAMITE_METODO_EQUALS;
					}
				}
				
				Utilities.verificaFaultIntegrazione(error, 
						CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE.getCodicePorta(),RicezioneContenutiApplicativi.ID_MODULO, 
						codiceEccezioneAtteso, descrizioneEccezioneAttesa, checkDescrizioneTramiteMatchEsatto);				
			}finally{
				dbComponentFruitore.close();
				dbComponentErogatore.close();
			}
		}catch(Exception e){
			throw e;
		}finally{
			dbComponentFruitore.close();
			dbComponentErogatore.close();
			
			this._unlock();
			dataFineTest = DateManager.getDate();
		}
		
		ErroreAttesoOpenSPCoopLogCore err = new ErroreAttesoOpenSPCoopLogCore();
		err.setIntervalloInferiore(dataInizioTest);
		err.setIntervalloSuperiore(dataFineTest);
		err.setMsgErrore(CostantiErroriIntegrazione.MSG_402_AUTENTICAZIONE_FALLITA_CREDENZIALI_NON_FORNITE);
		this.erroriAttesiOpenSPCoopCore.add(err);
	}
	
	
	/**
	 * Autenticazione fallita
	 * "402";
	 * 
	 * messaggio: credenziali fornite non corrette
	 */
	protected Object[][] _credenzialiScorrette(){
		return new Object[][]{
				{"admi","123456"},//password corretta, user errato
				{"adminSilX","121213"},//user corretto , password errata
		};
	}
	protected void _testAutenticazioneCredenzialiScorrette(String username,String password) throws Exception{
		DatabaseComponent dbComponentFruitore = null;
		DatabaseComponent dbComponentErogatore = null;

		Date dataInizioTest = null;
		Date dataFineTest = null;
		try{
			_lock();
			dataInizioTest = DateManager.getDate();
			
			ClientHttpGenerico client=new ClientHttpGenerico(new Repository());
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_AUTENTICAZIONE_BASIC);
			client.connectToSoapEngine();
			client.setAutenticazione(username,password);
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

				Reporter.log("Invocazione porta delegata con credenziali scorrette (user: "+username+" password:"+password+") non ha causato errori.");
				throw new TestSuiteException("Invocazione porta delegata con credenziali scorrette (user: "+username+" password:"+password+") non ha causato errori.");

			} catch (AxisFault error) {
				
				Assert.assertTrue(client.getCodiceStatoHTTP()==500);
				
				String codiceEccezioneAtteso = Utilities.toString(CodiceErroreIntegrazione.CODICE_402_AUTENTICAZIONE_FALLITA);
				String descrizioneEccezioneAttesa = CostantiErroriIntegrazione.MSG_402_AUTENTICAZIONE_FALLITA;
				boolean checkDescrizioneTramiteMatchEsatto = Utilities.CONTROLLO_DESCRIZIONE_TRAMITE_METODO_CONTAINS;
				if(this.genericCode) {
					IntegrationFunctionError integrationFunctionError = IntegrationFunctionError.AUTHENTICATION_INVALID_CREDENTIALS;
					ErroriProperties erroriProperties = ErroriProperties.getInstance(this.log); 
					codiceEccezioneAtteso = erroriProperties.getErrorType_noWrap(integrationFunctionError);
					if(erroriProperties.isForceGenericDetails_noWrap(integrationFunctionError)) {
						descrizioneEccezioneAttesa = erroriProperties.getGenericDetails_noWrap(integrationFunctionError);
						checkDescrizioneTramiteMatchEsatto = Utilities.CONTROLLO_DESCRIZIONE_TRAMITE_METODO_EQUALS;
					}
				}
				
				Utilities.verificaFaultIntegrazione(error, 
						CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE.getCodicePorta(),RicezioneContenutiApplicativi.ID_MODULO, 
						codiceEccezioneAtteso, descrizioneEccezioneAttesa, checkDescrizioneTramiteMatchEsatto);				
			}finally{
				dbComponentFruitore.close();
				dbComponentErogatore.close();
			}
		}catch(Exception e){
			throw e;
		}finally{
			dbComponentFruitore.close();
			dbComponentErogatore.close();
			
			this._unlock();
			dataFineTest = DateManager.getDate();
		}
		
		ErroreAttesoOpenSPCoopLogCore err = new ErroreAttesoOpenSPCoopLogCore();
		err.setIntervalloInferiore(dataInizioTest);
		err.setIntervalloSuperiore(dataFineTest);
		err.setMsgErrore(CostantiErroriIntegrazione.MSG_402_AUTENTICAZIONE_FALLITA);
		this.erroriAttesiOpenSPCoopCore.add(err);
	}
	
	
	/**
	 * Autenticazione fallita
	 * "402";
	 * 
	 * messaggio: identità del servizio applicativo fornita non esiste nella configurazione
	 */
	protected void _testAutenticazioneSANonEsistente() throws Exception{
		DatabaseComponent dbComponentFruitore = null;
		DatabaseComponent dbComponentErogatore = null;

		try{
			_lock();
			
			ClientHttpGenerico client=new ClientHttpGenerico(new Repository());
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_PROFILO_SINCRONO);
			client.connectToSoapEngine();
			TestSuiteProperties testsuiteProperties = TestSuiteProperties.getInstance();
			client.setProperty(testsuiteProperties.getServizioApplicativoTrasporto(),"ServizioApplicativoNonEsistente");
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

				Reporter.log("Invocazione porta delegata con identita ServizioApplicativo non esistente non ha causato errori.");
				throw new TestSuiteException("Invocazione porta delegata con identita ServizioApplicativo non esistente non ha causato errori.");

			} catch (AxisFault error) {
				
				Assert.assertTrue(client.getCodiceStatoHTTP()==500);
				
				String codiceEccezioneAtteso = Utilities.toString(CodiceErroreIntegrazione.CODICE_402_AUTENTICAZIONE_FALLITA);
				String descrizioneEccezioneAttesa = CostantiErroriIntegrazione.MSG_402_AUTENTICAZIONE_FALLITA_IDENTITA_SERVIZIO_APPLICATIVO_ERRATA.replace("SERVIZIO_APPLICATIVO", "ServizioApplicativoNonEsistente");
				boolean checkDescrizioneTramiteMatchEsatto = Utilities.CONTROLLO_DESCRIZIONE_TRAMITE_METODO_CONTAINS;
				if(this.genericCode) {
					IntegrationFunctionError integrationFunctionError = IntegrationFunctionError.AUTHENTICATION;
					ErroriProperties erroriProperties = ErroriProperties.getInstance(this.log); 
					codiceEccezioneAtteso = erroriProperties.getErrorType_noWrap(integrationFunctionError);
					if(erroriProperties.isForceGenericDetails_noWrap(integrationFunctionError)) {
						descrizioneEccezioneAttesa = erroriProperties.getGenericDetails_noWrap(integrationFunctionError);
						checkDescrizioneTramiteMatchEsatto = Utilities.CONTROLLO_DESCRIZIONE_TRAMITE_METODO_EQUALS;
					}
				}
				
				Utilities.verificaFaultIntegrazione(error, 
						CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE.getCodicePorta(),RicezioneContenutiApplicativi.ID_MODULO, 
						codiceEccezioneAtteso, descrizioneEccezioneAttesa, checkDescrizioneTramiteMatchEsatto);					
			}finally{
				dbComponentFruitore.close();
				dbComponentErogatore.close();
			}
		}catch(Exception e){
			throw e;
		}finally{
			dbComponentFruitore.close();
			dbComponentErogatore.close();
			
			this._unlock();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * Pattern Ricerca Porta Delegata Non Validi
	 * "403";
	 */
	
	// CONTENT-BASED
	protected void _testParametriIdentificazionePortaDelegataNonValidi_contentBased() throws Exception{
		DatabaseComponent dbComponentFruitore = null;
		DatabaseComponent dbComponentErogatore = null;

		try{
			_lock();
			
			ClientHttpGenerico client=new ClientHttpGenerico(new Repository());
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_CONTENT_BASED_EXAMPLE1);
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
				
				String codiceEccezioneAtteso = Utilities.toString(CodiceErroreIntegrazione.CODICE_403_AZIONE_NON_IDENTIFICATA);
				String descrizioneEccezioneAttesa = CostantiErroriIntegrazione.MSG_403_PD_PATTERN_AZIONE_NON_VALIDA;
				boolean checkDescrizioneTramiteMatchEsatto = Utilities.CONTROLLO_DESCRIZIONE_TRAMITE_METODO_EQUALS;
				if(this.genericCode) {
					IntegrationFunctionError integrationFunctionError = IntegrationFunctionError.OPERATION_UNDEFINED;
					ErroriProperties erroriProperties = ErroriProperties.getInstance(this.log); 
					codiceEccezioneAtteso = erroriProperties.getErrorType_noWrap(integrationFunctionError);
					if(erroriProperties.isForceGenericDetails_noWrap(integrationFunctionError)) {
						descrizioneEccezioneAttesa = erroriProperties.getGenericDetails_noWrap(integrationFunctionError);
						checkDescrizioneTramiteMatchEsatto = Utilities.CONTROLLO_DESCRIZIONE_TRAMITE_METODO_EQUALS;
					}
				}
				
				Utilities.verificaFaultIntegrazione(error, 
						CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE.getCodicePorta(),RicezioneContenutiApplicativi.ID_MODULO, 
						codiceEccezioneAtteso, descrizioneEccezioneAttesa, checkDescrizioneTramiteMatchEsatto);			
			}finally{
				dbComponentFruitore.close();
				dbComponentErogatore.close();
			}
		}catch(Exception e){
			throw e;
		}finally{
			dbComponentFruitore.close();
			dbComponentErogatore.close();
			
			this._unlock();
		}
	}
	
	// URL-BASED
	protected void _testParametriIdentificazionePortaDelegataNonValidi_urlBased() throws Exception{
		DatabaseComponent dbComponentFruitore = null;
		DatabaseComponent dbComponentErogatore = null;

		try{
			_lock();
			
			ClientHttpGenerico client=new ClientHttpGenerico(new Repository());
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_URL_BASED_EXAMPLE1);
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
				
				String codiceEccezioneAtteso = Utilities.toString(CodiceErroreIntegrazione.CODICE_403_AZIONE_NON_IDENTIFICATA);
				String descrizioneEccezioneAttesa = CostantiErroriIntegrazione.MSG_403_PD_PATTERN_AZIONE_NON_VALIDA;
				boolean checkDescrizioneTramiteMatchEsatto = Utilities.CONTROLLO_DESCRIZIONE_TRAMITE_METODO_EQUALS;
				if(this.genericCode) {
					IntegrationFunctionError integrationFunctionError = IntegrationFunctionError.OPERATION_UNDEFINED;
					ErroriProperties erroriProperties = ErroriProperties.getInstance(this.log); 
					codiceEccezioneAtteso = erroriProperties.getErrorType_noWrap(integrationFunctionError);
					if(erroriProperties.isForceGenericDetails_noWrap(integrationFunctionError)) {
						descrizioneEccezioneAttesa = erroriProperties.getGenericDetails_noWrap(integrationFunctionError);
						checkDescrizioneTramiteMatchEsatto = Utilities.CONTROLLO_DESCRIZIONE_TRAMITE_METODO_EQUALS;
					}
				}
				
				Utilities.verificaFaultIntegrazione(error, 
						CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE.getCodicePorta(),RicezioneContenutiApplicativi.ID_MODULO, 
						codiceEccezioneAtteso, descrizioneEccezioneAttesa, checkDescrizioneTramiteMatchEsatto);			
			}finally{
				dbComponentFruitore.close();
				dbComponentErogatore.close();
			}
		}catch(Exception e){
			throw e;
		}finally{
			dbComponentFruitore.close();
			dbComponentErogatore.close();
			
			this._unlock();
		}
	}
	
	// URL-FORM-BASED
	protected void _testParametriIdentificazionePortaDelegataNonValidi_urlFormBased() throws Exception{
		DatabaseComponent dbComponentFruitore = null;
		DatabaseComponent dbComponentErogatore = null;

		try{
			_lock();
			
			ClientHttpGenerico client=new ClientHttpGenerico(new Repository());
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_URL_FORM_BASED_EXAMPLE1);
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
				
				String codiceEccezioneAtteso = Utilities.toString(CodiceErroreIntegrazione.CODICE_403_AZIONE_NON_IDENTIFICATA);
				String descrizioneEccezioneAttesa = CostantiErroriIntegrazione.MSG_403_PD_PATTERN_AZIONE_NON_VALIDA;
				boolean checkDescrizioneTramiteMatchEsatto = Utilities.CONTROLLO_DESCRIZIONE_TRAMITE_METODO_EQUALS;
				if(this.genericCode) {
					IntegrationFunctionError integrationFunctionError = IntegrationFunctionError.OPERATION_UNDEFINED;
					ErroriProperties erroriProperties = ErroriProperties.getInstance(this.log); 
					codiceEccezioneAtteso = erroriProperties.getErrorType_noWrap(integrationFunctionError);
					if(erroriProperties.isForceGenericDetails_noWrap(integrationFunctionError)) {
						descrizioneEccezioneAttesa = erroriProperties.getGenericDetails_noWrap(integrationFunctionError);
						checkDescrizioneTramiteMatchEsatto = Utilities.CONTROLLO_DESCRIZIONE_TRAMITE_METODO_EQUALS;
					}
				}
				
				Utilities.verificaFaultIntegrazione(error, 
						CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE.getCodicePorta(),RicezioneContenutiApplicativi.ID_MODULO, 
						codiceEccezioneAtteso, descrizioneEccezioneAttesa, checkDescrizioneTramiteMatchEsatto);			
			}finally{
				dbComponentFruitore.close();
				dbComponentErogatore.close();
			}
		}catch(Exception e){
			throw e;
		}finally{
			dbComponentFruitore.close();
			dbComponentErogatore.close();
			
			this._unlock();
		}
	}
	
	// INPUT-BASED
	protected void _testParametriIdentificazionePortaDelegataNonValidi_inputBased() throws Exception{
		DatabaseComponent dbComponentFruitore = null;
		DatabaseComponent dbComponentErogatore = null;

		try{
			_lock();
			
			ClientHttpGenerico client=new ClientHttpGenerico(new Repository());
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_INPUT_BASED_EXAMPLE1);
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
				
				String codiceEccezioneAtteso = Utilities.toString(CodiceErroreIntegrazione.CODICE_403_AZIONE_NON_IDENTIFICATA);
				String descrizioneEccezioneAttesa = CostantiErroriIntegrazione.MSG_403_PD_PATTERN_AZIONE_NON_VALIDA;
				boolean checkDescrizioneTramiteMatchEsatto = Utilities.CONTROLLO_DESCRIZIONE_TRAMITE_METODO_EQUALS;
				if(this.genericCode) {
					IntegrationFunctionError integrationFunctionError = IntegrationFunctionError.OPERATION_UNDEFINED;
					ErroriProperties erroriProperties = ErroriProperties.getInstance(this.log); 
					codiceEccezioneAtteso = erroriProperties.getErrorType_noWrap(integrationFunctionError);
					if(erroriProperties.isForceGenericDetails_noWrap(integrationFunctionError)) {
						descrizioneEccezioneAttesa = erroriProperties.getGenericDetails_noWrap(integrationFunctionError);
						checkDescrizioneTramiteMatchEsatto = Utilities.CONTROLLO_DESCRIZIONE_TRAMITE_METODO_EQUALS;
					}
				}
				
				Utilities.verificaFaultIntegrazione(error, 
						CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE.getCodicePorta(),RicezioneContenutiApplicativi.ID_MODULO, 
						codiceEccezioneAtteso, descrizioneEccezioneAttesa, checkDescrizioneTramiteMatchEsatto);			
			}finally{
				dbComponentFruitore.close();
				dbComponentErogatore.close();
			}
		}catch(Exception e){
			throw e;
		}finally{
			dbComponentFruitore.close();
			dbComponentErogatore.close();
			
			this._unlock();
		}
	}
	
	



	
	
	
	
	/**
	 * Autorizzazione Fallita
	 * "404";
	 */
	protected void _testAutorizzazioneFallita() throws Exception{
		DatabaseComponent dbComponentFruitore = null;
		DatabaseComponent dbComponentErogatore = null;

		Date dataInizioTest = null;
		Date dataFineTest = null;
		try{
			_lock();
			dataInizioTest = DateManager.getDate();
			
			ClientHttpGenerico client=new ClientHttpGenerico(new Repository());
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_AUTORIZZAZIONE_EXAMPLE);
			client.connectToSoapEngine();
			client.setAutenticazione("adminSilY", "123456");
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

				Reporter.log("Invocazione porta delegata per controllo autorizzazione non ha causato errori.");
				throw new TestSuiteException("Invocazione porta delegata per controllo autorizzazione non ha causato errori.");
			} catch (AxisFault error) {
				
				Assert.assertTrue(client.getCodiceStatoHTTP()==500);
				
				String codiceEccezioneAtteso = Utilities.toString(CodiceErroreIntegrazione.CODICE_404_AUTORIZZAZIONE_FALLITA);
				String descrizioneEccezioneAttesa = CostantiErroriIntegrazione.MSG_404_AUTORIZZAZIONE_FALLITA.replace("SERVIZIO_APPLICATIVO", "silY");
				boolean checkDescrizioneTramiteMatchEsatto = Utilities.CONTROLLO_DESCRIZIONE_TRAMITE_METODO_CONTAINS;
				if(this.genericCode) {
					IntegrationFunctionError integrationFunctionError = IntegrationFunctionError.AUTHORIZATION_DENY;
					ErroriProperties erroriProperties = ErroriProperties.getInstance(this.log); 
					codiceEccezioneAtteso = erroriProperties.getErrorType_noWrap(integrationFunctionError);
					if(erroriProperties.isForceGenericDetails_noWrap(integrationFunctionError)) {
						descrizioneEccezioneAttesa = erroriProperties.getGenericDetails_noWrap(integrationFunctionError);
						checkDescrizioneTramiteMatchEsatto = Utilities.CONTROLLO_DESCRIZIONE_TRAMITE_METODO_EQUALS;
					}
				}
				
				Utilities.verificaFaultIntegrazione(error, 
						CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE.getCodicePorta(),RicezioneContenutiApplicativi.ID_MODULO, 
						codiceEccezioneAtteso, descrizioneEccezioneAttesa, checkDescrizioneTramiteMatchEsatto);	
			}finally{
				dbComponentFruitore.close();
				dbComponentErogatore.close();
			}
		}catch(Exception e){
			throw e;
		}finally{
			dbComponentFruitore.close();
			dbComponentErogatore.close();

			this._unlock();
			dataFineTest = DateManager.getDate();
		}
		
		ErroreAttesoOpenSPCoopLogCore err = new ErroreAttesoOpenSPCoopLogCore();
		err.setIntervalloInferiore(dataInizioTest);
		err.setIntervalloSuperiore(dataFineTest);
		err.setMsgErrore(CostantiErroriIntegrazione.MSG_404_AUTORIZZAZIONE_FALLITA.replace("SERVIZIO_APPLICATIVO", "silY"));
		this.erroriAttesiOpenSPCoopCore.add(err);
	}


	
	
	
	

	
	/**
	 * Servizio SPCoop abbinato alla Porta Delegata Inesistente
	 * "405";
	 */
	protected void _testServizioSPCoopNonEsistente() throws Exception{
		DatabaseComponent dbComponentFruitore = null;
		DatabaseComponent dbComponentErogatore = null;

		try{
			_lock();
			
			ClientHttpGenerico client=new ClientHttpGenerico(new Repository());
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_SERVIZIO_INESISTENTE);
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

				Reporter.log("Invocazione porta delegata con servizio non esistente (PortaDelegata: "+CostantiTestSuite.PORTA_DELEGATA_SERVIZIO_INESISTENTE+") non ha causato errori.");
				throw new TestSuiteException("Invocazione porta delegata con servizio non esistente (PortaDelegata: "+CostantiTestSuite.PORTA_DELEGATA_SERVIZIO_INESISTENTE+") non ha causato errori.");
			} catch (AxisFault error) {
				
				Assert.assertTrue(client.getCodiceStatoHTTP()==500);
				
				String codiceEccezioneAtteso = Utilities.toString(CodiceErroreIntegrazione.CODICE_405_SERVIZIO_NON_TROVATO);
				String descrizioneEccezioneAttesa = CostantiErroriIntegrazione.MSG_405_SERVIZIO_NON_TROVATO;
				boolean checkDescrizioneTramiteMatchEsatto = Utilities.CONTROLLO_DESCRIZIONE_TRAMITE_METODO_CONTAINS;
				if(this.genericCode) {
					IntegrationFunctionError integrationFunctionError = IntegrationFunctionError.API_OUT_UNKNOWN;
					ErroriProperties erroriProperties = ErroriProperties.getInstance(this.log); 
					codiceEccezioneAtteso = erroriProperties.getErrorType_noWrap(integrationFunctionError);
					if(erroriProperties.isForceGenericDetails_noWrap(integrationFunctionError)) {
						descrizioneEccezioneAttesa = erroriProperties.getGenericDetails_noWrap(integrationFunctionError);
						checkDescrizioneTramiteMatchEsatto = Utilities.CONTROLLO_DESCRIZIONE_TRAMITE_METODO_EQUALS;
					}
				}
				
				Utilities.verificaFaultIntegrazione(error, 
						CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE.getCodicePorta(),RicezioneContenutiApplicativi.ID_MODULO, 
						codiceEccezioneAtteso, descrizioneEccezioneAttesa, checkDescrizioneTramiteMatchEsatto);	
			}finally{
				dbComponentFruitore.close();
				dbComponentErogatore.close();
			}
		}catch(Exception e){
			throw e;
		}finally{
			dbComponentFruitore.close();
			dbComponentErogatore.close();

			this._unlock();
		}
	}

	
	
	
	
	
	
	
	
	
	
	
	/**
	 * Nessun Messaggio disponibile per il Servizio Applicativo (Integration Manager)
	 * "406";
	 */
	protected void _testIM_messaggiNonDisponibili() throws Exception{
		
		try{
			_lock();
		
			org.openspcoop2.pdd.services.axis14.MessageBox_PortType im = 
				IntegrationManager.getIntegrationManagerMessageBox_axis14(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore().replace("out","IntegrationManager/MessageBox"), 
					"sil01","SIL_01");
			try{
				im.getAllMessagesId();
				throw new Exception("Metodo getAllMessagesId non ha causato errori");
			}catch(IntegrationManagerException e){
				verificaSPCoopException_406(e);
			}
			try{
				im.getAllMessagesIdByService("SSS", "SSS", "AAA");
				throw new Exception("Metodo getAllMessagesIdByService non ha causato errori");
			}catch(IntegrationManagerException e){
				verificaSPCoopException_406(e);
			}
			try{
				im.getMessagesIdArray(0, 2);
				throw new Exception("Metodo getMessagesIdArray non ha causato errori");
			}catch(IntegrationManagerException e){
				verificaSPCoopException_406(e);
			}
			try{
				im.getMessagesIdArrayByService(0, 2, "sss", "ssss", "aaa");
				throw new Exception("Metodo getMessagesIdArray non ha causato errori");
			}catch(IntegrationManagerException e){
				verificaSPCoopException_406(e);
			}
			try{
				im.getNextMessagesId(30);
				throw new Exception("Metodo getNextMessagesId non ha causato errori");
			}catch(IntegrationManagerException e){
				verificaSPCoopException_406(e);
			}
			try{
				im.getNextMessagesIdByService(30, "sss", "sss", "aaaa");
				throw new Exception("Metodo getNextMessagesId non ha causato errori");
			}catch(IntegrationManagerException e){
				verificaSPCoopException_406(e);
			}
			try{
				im.deleteAllMessages();
				throw new Exception("Metodo deleteAllMessages non ha causato errori");
			}catch(IntegrationManagerException e){
				verificaSPCoopException_406(e);
			}
		}
		finally{
			this._unlock();
		}
	}
	private void verificaSPCoopException_406(IntegrationManagerException e) throws Exception{
		
		ErroriProperties erroriProperties = ErroriProperties.getInstance(this.log); 
		IntegrationFunctionError integrationFunctionError = IntegrationFunctionError.IM_MESSAGES_NOT_FOUND;
		
		String codiceErrore = Utilities.toString(CodiceErroreIntegrazione.CODICE_406_INTEGRATION_MANAGER_MESSAGGI_FOR_SIL_NON_TROVATI);
		if(this.genericCode) {
			codiceErrore = erroriProperties.getErrorType_noWrap(integrationFunctionError);
		}
		Reporter.log("Codice errore ["+e.getCodiceEccezione()+"]");
		Reporter.log("confronto con ["+codiceErrore+"]");
		Assert.assertTrue(codiceErrore.equals(e.getCodiceEccezione()));
		
		Reporter.log("ID Porta ["+e.getIdentificativoPorta()+"]");
		Reporter.log("confronto con [OpenSPCoopSPCoopIT]");
		Assert.assertTrue(Utilities.testSuiteProperties.getIdentitaDefault_dominio().equals(e.getIdentificativoPorta()));
		
		Reporter.log("ID funzione ["+e.getIdentificativoFunzione()+"]");
		Reporter.log("confronto con [IntegrationManager]");
		Assert.assertTrue("IntegrationManager".equals(e.getIdentificativoFunzione()));
		
		Reporter.log("TipoEccezione ["+e.getTipoEccezione()+"]");
		Reporter.log("confronto con [EccezioneIntegrazione]");
		Assert.assertTrue("EccezioneIntegrazione".equals(e.getTipoEccezione()));
		
		String descrizioneErrore = CostantiErroriIntegrazione.MSG_406_INTEGRATION_MANAGER_MESSAGGI_FOR_SIL_NON_TROVATI;
		if(this.genericCode) {
			if(erroriProperties.isForceGenericDetails_noWrap(integrationFunctionError)) {
				descrizioneErrore = erroriProperties.getGenericDetails_noWrap(integrationFunctionError);
			}
		}
		Reporter.log("MessaggioErrore ["+e.getDescrizioneEccezione()+"]");
		Reporter.log("confronto con ["+descrizioneErrore+"]");
		Assert.assertTrue(descrizioneErrore.equals(e.getDescrizioneEccezione()));
	}
	
	
	
	
	
	
	
	
	
	
	/**
	 * Messaggio Richiesto Inesistente (Integration Manager)
	 * "407";
	 */
	protected void _testIM_messaggioNonEsistente() throws Exception{
		
		try{
			_lock();
		
			org.openspcoop2.pdd.services.axis14.MessageBox_PortType im = 
				IntegrationManager.getIntegrationManagerMessageBox_axis14(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore().replace("out","IntegrationManager/MessageBox"), 
					"sil01","SIL_01");
			try{
				im.getMessage("ID_EGOV_XXX");
				throw new Exception("Metodo getMessage non ha causato errori");
			}catch(IntegrationManagerException e){
				verificaSPCoopException_407(e);
			}
			try{
				im.getMessageByReference("ID_EGOV_XXX");
				throw new Exception("Metodo getMessageByReference non ha causato errori");
			}catch(IntegrationManagerException e){
				verificaSPCoopException_407(e);
			}
			try{
				im.deleteMessage("ID_EGOV_XXX");
				throw new Exception("Metodo deleteMessage non ha causato errori");
			}catch(IntegrationManagerException e){
				verificaSPCoopException_407(e);
			}
			try{
				im.deleteMessageByReference("ID_EGOV_XXX");
				throw new Exception("Metodo deleteMessageByReference non ha causato errori");
			}catch(IntegrationManagerException e){
				verificaSPCoopException_407(e);
			}
		
		}finally{
			this._unlock();
		}
	}
	private void verificaSPCoopException_407(IntegrationManagerException e) throws Exception{
		
		ErroriProperties erroriProperties = ErroriProperties.getInstance(this.log); 
		IntegrationFunctionError integrationFunctionError = IntegrationFunctionError.IM_MESSAGE_NOT_FOUND;
		
		String codiceErrore = Utilities.toString(CodiceErroreIntegrazione.CODICE_407_INTEGRATION_MANAGER_MSG_RICHIESTO_NON_TROVATO);
		if(this.genericCode) {
			codiceErrore = erroriProperties.getErrorType_noWrap(integrationFunctionError);
		}
		Reporter.log("Codice errore ["+e.getCodiceEccezione()+"]");
		Reporter.log("confronto con ["+codiceErrore+"]");
		Assert.assertTrue(codiceErrore.equals(e.getCodiceEccezione()));
		
		Reporter.log("ID Porta ["+e.getIdentificativoPorta()+"]");
		Reporter.log("confronto con [OpenSPCoopSPCoopIT]");
		Assert.assertTrue(Utilities.testSuiteProperties.getIdentitaDefault_dominio().equals(e.getIdentificativoPorta()));
		
		Reporter.log("ID funzione ["+e.getIdentificativoFunzione()+"]");
		Reporter.log("confronto con [IntegrationManager]");
		Assert.assertTrue("IntegrationManager".equals(e.getIdentificativoFunzione()));
		
		Reporter.log("TipoEccezione ["+e.getTipoEccezione()+"]");
		Reporter.log("confronto con [EccezioneIntegrazione]");
		Assert.assertTrue("EccezioneIntegrazione".equals(e.getTipoEccezione()));
		
		String descrizioneErrore = CostantiErroriIntegrazione.MSG_407_INTEGRATION_MANAGER_MSG_RICHIESTO_NON_TROVATO;
		if(this.genericCode) {
			if(erroriProperties.isForceGenericDetails_noWrap(integrationFunctionError)) {
				descrizioneErrore = erroriProperties.getGenericDetails_noWrap(integrationFunctionError);
			}
		}
		Reporter.log("MessaggioErrore ["+e.getDescrizioneEccezione()+"]");
		Reporter.log("confronto con ["+descrizioneErrore+"]");
		Assert.assertTrue(descrizioneErrore.equals(e.getDescrizioneEccezione()));
	}
	
	


	
	
	
	
	
	
	
	/**
	 * Servizio Correlato associato ad un Servizio Asincrono non esistente
	 * "408";
	 */
	protected void _testServizioCorrelatoNonEsistenteAS(boolean unwrap) throws Exception{
		DatabaseComponent dbComponentFruitore = null;
		DatabaseComponent dbComponentErogatore = null;

		try{
			_lock(unwrap);
			
			ClientHttpGenerico client=new ClientHttpGenerico(new Repository());
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_SERVIZIO_ASINCRONO_SIMMETRICO_CORRELATO_NON_ESISTENTE);
			client.connectToSoapEngine();
			client.setAutenticazione("ProfiloAsincrono_richiestaSincrona_testRichiesteApplicativeScorrette", "123456");
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

				Reporter.log("Invocazione porta delegata con servizio correlato non esistente (PortaDelegata: "+CostantiTestSuite.PORTA_DELEGATA_SERVIZIO_ASINCRONO_SIMMETRICO_CORRELATO_NON_ESISTENTE+") non ha causato errori.");
				throw new TestSuiteException("Invocazione porta delegata con servizio correlato non esistente (PortaDelegata: "+CostantiTestSuite.PORTA_DELEGATA_SERVIZIO_ASINCRONO_SIMMETRICO_CORRELATO_NON_ESISTENTE+") non ha causato errori.");
			} catch (AxisFault error) {
				
				Assert.assertTrue(client.getCodiceStatoHTTP()==500);
				
				String codiceEccezioneAtteso = Utilities.toString(CodiceErroreIntegrazione.CODICE_408_SERVIZIO_CORRELATO_NON_TROVATO);
				String descrizioneEccezioneAttesa = CostantiErroriIntegrazione.MSG_408_SERVIZIO_CORRELATO_NON_TROVATO;
				boolean checkDescrizioneTramiteMatchEsatto = Utilities.CONTROLLO_DESCRIZIONE_TRAMITE_METODO_EQUALS;
				if(this.genericCode) {
					IntegrationFunctionError integrationFunctionError = IntegrationFunctionError.WRAP_503_INTERNAL_ERROR;
					if(unwrap) {
						integrationFunctionError = IntegrationFunctionError.INTERNAL_REQUEST_ERROR;
					}
					ErroriProperties erroriProperties = ErroriProperties.getInstance(this.log); 
					codiceEccezioneAtteso = erroriProperties.getErrorType_noWrap(integrationFunctionError);
					if(erroriProperties.isForceGenericDetails_noWrap(integrationFunctionError)) {
						descrizioneEccezioneAttesa = erroriProperties.getGenericDetails_noWrap(integrationFunctionError);
						checkDescrizioneTramiteMatchEsatto = Utilities.CONTROLLO_DESCRIZIONE_TRAMITE_METODO_EQUALS;
					}
				}
				
				Utilities.verificaFaultIntegrazione(error, 
						"TestRichiesteScorretteSPCoopIT",RicezioneContenutiApplicativi.ID_MODULO, 
						codiceEccezioneAtteso, descrizioneEccezioneAttesa, checkDescrizioneTramiteMatchEsatto);	
			}finally{
				dbComponentFruitore.close();
				dbComponentErogatore.close();
			}
		}catch(Exception e){
			throw e;
		}finally{
			dbComponentFruitore.close();
			dbComponentErogatore.close();
			
			_unlock();
		}
	}
	
	protected void _testServizioCorrelatoNonEsistenteAA(boolean unwrap) throws Exception{
		DatabaseComponent dbComponentFruitore = null;
		DatabaseComponent dbComponentErogatore = null;

		try{
			_lock(unwrap);
			
			ClientHttpGenerico client=new ClientHttpGenerico(new Repository());
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_SERVIZIO_ASINCRONO_ASIMMETRICO_CORRELATO_NON_ESISTENTE);
			client.connectToSoapEngine();
			client.setAutenticazione("ProfiloAsincrono_richiestaSincrona_testRichiesteApplicativeScorrette", "123456");
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

				Reporter.log("Invocazione porta delegata con servizio correlato non esistente (PortaDelegata: "+CostantiTestSuite.PORTA_DELEGATA_SERVIZIO_ASINCRONO_ASIMMETRICO_CORRELATO_NON_ESISTENTE+") non ha causato errori.");
				throw new TestSuiteException("Invocazione porta delegata con servizio correlato non esistente (PortaDelegata: "+CostantiTestSuite.PORTA_DELEGATA_SERVIZIO_ASINCRONO_ASIMMETRICO_CORRELATO_NON_ESISTENTE+") non ha causato errori.");
			} catch (AxisFault error) {
				
				Assert.assertTrue(client.getCodiceStatoHTTP()==500);
				
				String codiceEccezioneAtteso = Utilities.toString(CodiceErroreIntegrazione.CODICE_408_SERVIZIO_CORRELATO_NON_TROVATO);
				String descrizioneEccezioneAttesa = CostantiErroriIntegrazione.MSG_408_SERVIZIO_CORRELATO_NON_TROVATO;
				boolean checkDescrizioneTramiteMatchEsatto = Utilities.CONTROLLO_DESCRIZIONE_TRAMITE_METODO_EQUALS;
				if(this.genericCode) {
					IntegrationFunctionError integrationFunctionError = IntegrationFunctionError.WRAP_503_INTERNAL_ERROR;
					if(unwrap) {
						integrationFunctionError = IntegrationFunctionError.INTERNAL_REQUEST_ERROR;
					}
					ErroriProperties erroriProperties = ErroriProperties.getInstance(this.log); 
					codiceEccezioneAtteso = erroriProperties.getErrorType_noWrap(integrationFunctionError);
					if(erroriProperties.isForceGenericDetails_noWrap(integrationFunctionError)) {
						descrizioneEccezioneAttesa = erroriProperties.getGenericDetails_noWrap(integrationFunctionError);
						checkDescrizioneTramiteMatchEsatto = Utilities.CONTROLLO_DESCRIZIONE_TRAMITE_METODO_EQUALS;
					}
				}
				
				Utilities.verificaFaultIntegrazione(error, 
						"TestRichiesteScorretteSPCoopIT",RicezioneContenutiApplicativi.ID_MODULO, 
						codiceEccezioneAtteso, descrizioneEccezioneAttesa, checkDescrizioneTramiteMatchEsatto);	
			}finally{
				dbComponentFruitore.close();
				dbComponentErogatore.close();
			}
		}catch(Exception e){
			throw e;
		}finally{
			dbComponentFruitore.close();
			dbComponentErogatore.close();
			
			this._unlock();
		}
	}
	
	
	
	
	
	
	
	
	
	/**
	 * Risposta/RichiestaStato asincrona non correlata ad una precedente richiesta
	 * "409";
	 */
	protected void _testRispostaAsincronaSimmetricaNonGenerabile() throws Exception{
		
		DatabaseComponent dbComponentFruitore = null;
		DatabaseComponent dbComponentErogatore = null;

		Date dataInizioTest = null;
		Date dataFineTest = null;
		try{
			_lock();
			dataInizioTest = DateManager.getDate();
			
			TestSuiteProperties testsuiteProperties = TestSuiteProperties.getInstance();
			
			ClientHttpGenerico client=new ClientHttpGenerico(new Repository());
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_PROFILO_ASINCRONO_SIMMETRICO_CORRELATO_MODALITA_SINCRONA);
			client.connectToSoapEngine();
			client.setProperty(testsuiteProperties.getRiferimentoAsincronoTrasporto(), "ID_EGOV_XXX");
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

				Reporter.log("Invocazione porta delegata per risposta asincrona simmetrica non preceduta da richiesta (PortaDelegata: "+CostantiTestSuite.PORTA_DELEGATA_PROFILO_ASINCRONO_SIMMETRICO_CORRELATO_MODALITA_SINCRONA+") non ha causato errori.");
				throw new TestSuiteException("Invocazione porta delegata per risposta asincrona simmetrica non preceduta da richiesta (PortaDelegata: "+CostantiTestSuite.PORTA_DELEGATA_PROFILO_ASINCRONO_SIMMETRICO_CORRELATO_MODALITA_SINCRONA+") non ha causato errori.");
			} catch (AxisFault error) {
				
				Assert.assertTrue(client.getCodiceStatoHTTP()==500);
				
				String codiceEccezioneAtteso = Utilities.toString(CodiceErroreIntegrazione.CODICE_409_RISPOSTA_ASINCRONA_NON_CORRELATA_ALLA_RICHIESTA);
				String descrizioneEccezioneAttesa = CostantiErroriIntegrazione.MSG_409_RISPOSTA_ASINCRONA_NON_CORRELATA_ALLA_RICHIESTA;
				boolean checkDescrizioneTramiteMatchEsatto = Utilities.CONTROLLO_DESCRIZIONE_TRAMITE_METODO_EQUALS;
				if(this.genericCode) {
					IntegrationFunctionError integrationFunctionError = IntegrationFunctionError.CORRELATION_INFORMATION_NOT_FOUND;
					ErroriProperties erroriProperties = ErroriProperties.getInstance(this.log); 
					codiceEccezioneAtteso = erroriProperties.getErrorType_noWrap(integrationFunctionError);
					if(erroriProperties.isForceGenericDetails_noWrap(integrationFunctionError)) {
						descrizioneEccezioneAttesa = erroriProperties.getGenericDetails_noWrap(integrationFunctionError);
						checkDescrizioneTramiteMatchEsatto = Utilities.CONTROLLO_DESCRIZIONE_TRAMITE_METODO_EQUALS;
					}
				}
				
				Utilities.verificaFaultIntegrazione(error, 
						CostantiTestSuite.SPCOOP_SOGGETTO_EROGATORE.getCodicePorta(), Imbustamento.ID_MODULO, 
						codiceEccezioneAtteso, descrizioneEccezioneAttesa, checkDescrizioneTramiteMatchEsatto);	
			}finally{
				dbComponentFruitore.close();
				dbComponentErogatore.close();
			}
		}catch(Exception e){
			throw e;
		}finally{
			dbComponentFruitore.close();
			dbComponentErogatore.close();
			
			this._unlock();
			dataFineTest = DateManager.getDate();
		}
		
		ErroreAttesoOpenSPCoopLogCore err = new ErroreAttesoOpenSPCoopLogCore();
		err.setIntervalloInferiore(dataInizioTest);
		err.setIntervalloSuperiore(dataFineTest);		
		err.setMsgErrore("Errore  getSomeValues OUTBOX/ID_EGOV_XXX: Busta non trovata");
		this.erroriAttesiOpenSPCoopCore.add(err);
		
		ErroreAttesoOpenSPCoopLogCore err2 = new ErroreAttesoOpenSPCoopLogCore();
		err2.setIntervalloInferiore(dataInizioTest);
		err2.setIntervalloSuperiore(dataFineTest);
		err2.setMsgErrore("Busta per correlazione asincrona simmetrica (ID_EGOV_XXX) non trovata");
		this.erroriAttesiOpenSPCoopCore.add(err2);
		
		ErroreAttesoOpenSPCoopLogCore err3 = new ErroreAttesoOpenSPCoopLogCore();
		err3.setIntervalloInferiore(dataInizioTest);
		err3.setIntervalloSuperiore(dataFineTest);
		err3.setMsgErrore("Errore durante la getBusta dal repository ID_EGOV_XXX: Busta non trovata");
		this.erroriAttesiOpenSPCoopCore.add(err3);
	}
	
	protected void _testRispostaAsincronaAsimmetricaNonGenerabile() throws Exception{
		
		DatabaseComponent dbComponentFruitore = null;
		DatabaseComponent dbComponentErogatore = null;

		Date dataInizioTest = null;
		Date dataFineTest = null;
		try{
			_lock();
			dataInizioTest = DateManager.getDate();
			
			TestSuiteProperties testsuiteProperties = TestSuiteProperties.getInstance();
			
			ClientHttpGenerico client=new ClientHttpGenerico(new Repository());
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_PROFILO_ASINCRONO_ASIMMETRICO_CORRELATO_MODALITA_SINCRONA);
			client.connectToSoapEngine();
			client.setProperty(testsuiteProperties.getRiferimentoAsincronoTrasporto(), "ID_EGOV_XXX");
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

				Reporter.log("Invocazione porta delegata per risposta asincrona asimmetrica non preceduta da richiesta (PortaDelegata: "+CostantiTestSuite.PORTA_DELEGATA_PROFILO_ASINCRONO_ASIMMETRICO_CORRELATO_MODALITA_SINCRONA+") non ha causato errori.");
				throw new TestSuiteException("Invocazione porta delegata per risposta asincrona asimmetrica non preceduta da richiesta (PortaDelegata: "+CostantiTestSuite.PORTA_DELEGATA_PROFILO_ASINCRONO_ASIMMETRICO_CORRELATO_MODALITA_SINCRONA+") non ha causato errori.");
			} catch (AxisFault error) {
				
				Assert.assertTrue(client.getCodiceStatoHTTP()==500);
				
				String codiceEccezioneAtteso = Utilities.toString(CodiceErroreIntegrazione.CODICE_409_RISPOSTA_ASINCRONA_NON_CORRELATA_ALLA_RICHIESTA);
				String descrizioneEccezioneAttesa = CostantiErroriIntegrazione.MSG_409_RISPOSTA_ASINCRONA_NON_CORRELATA_ALLA_RICHIESTA;
				boolean checkDescrizioneTramiteMatchEsatto = Utilities.CONTROLLO_DESCRIZIONE_TRAMITE_METODO_EQUALS;
				if(this.genericCode) {
					IntegrationFunctionError integrationFunctionError = IntegrationFunctionError.CORRELATION_INFORMATION_NOT_FOUND;
					ErroriProperties erroriProperties = ErroriProperties.getInstance(this.log); 
					codiceEccezioneAtteso = erroriProperties.getErrorType_noWrap(integrationFunctionError);
					if(erroriProperties.isForceGenericDetails_noWrap(integrationFunctionError)) {
						descrizioneEccezioneAttesa = erroriProperties.getGenericDetails_noWrap(integrationFunctionError);
						checkDescrizioneTramiteMatchEsatto = Utilities.CONTROLLO_DESCRIZIONE_TRAMITE_METODO_EQUALS;
					}
				}
				
				Utilities.verificaFaultIntegrazione(error, 
						CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE.getCodicePorta(), Imbustamento.ID_MODULO, 
						codiceEccezioneAtteso, descrizioneEccezioneAttesa, checkDescrizioneTramiteMatchEsatto);	
			}finally{
				dbComponentFruitore.close();
				dbComponentErogatore.close();
			}
		}catch(Exception e){
			throw e;
		}finally{
			dbComponentFruitore.close();
			dbComponentErogatore.close();

			this._unlock();
			dataFineTest = DateManager.getDate();
		}
		
		ErroreAttesoOpenSPCoopLogCore err = new ErroreAttesoOpenSPCoopLogCore();
		err.setIntervalloInferiore(dataInizioTest);
		err.setIntervalloSuperiore(dataFineTest);
		err.setMsgErrore("Errore  getSomeValues OUTBOX/ID_EGOV_XXX: Busta non trovata");
		this.erroriAttesiOpenSPCoopCore.add(err);
		
		ErroreAttesoOpenSPCoopLogCore err2 = new ErroreAttesoOpenSPCoopLogCore();
		err2.setIntervalloInferiore(dataInizioTest);
		err2.setIntervalloSuperiore(dataFineTest);
		err2.setMsgErrore("Busta per correlazione asincrona asimmetrica (ID_EGOV_XXX) non trovata");
		this.erroriAttesiOpenSPCoopCore.add(err2);
		
		ErroreAttesoOpenSPCoopLogCore err3 = new ErroreAttesoOpenSPCoopLogCore();
		err3.setIntervalloInferiore(dataInizioTest);
		err3.setIntervalloSuperiore(dataFineTest);
		err3.setMsgErrore("Errore durante la getBusta dal repository ID_EGOV_XXX: Busta non trovata");
		this.erroriAttesiOpenSPCoopCore.add(err3);
	}
	
	
	
	
	
	
	
	
	/**
	 * Autenticazione richiesta per l'invocazione della Porta Delegata
	 * "410";
	 */
	protected void _testAutenticazioneRichiestaServizioApplicativoAsincronoSimmetrico() throws Exception{
		DatabaseComponent dbComponentFruitore = null;
		DatabaseComponent dbComponentErogatore = null;

		try{
			_lock();
			
			ClientHttpGenerico client=new ClientHttpGenerico(new Repository());
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_SERVIZIO_ASINCRONO_SIMMETRICO_PD_SENZA_AUTENTICAZIONE);
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

				Reporter.log("Invocazione porta delegata (errore 410) (PortaDelegata: "+CostantiTestSuite.PORTA_DELEGATA_SERVIZIO_ASINCRONO_SIMMETRICO_PD_SENZA_AUTENTICAZIONE+") non ha causato errori.");
				throw new TestSuiteException("Invocazione porta delegata errore 410) (PortaDelegata: "+CostantiTestSuite.PORTA_DELEGATA_SERVIZIO_ASINCRONO_SIMMETRICO_PD_SENZA_AUTENTICAZIONE+") non ha causato errori.");
			} catch (AxisFault error) {
				
				Assert.assertTrue(client.getCodiceStatoHTTP()==500);
				
				String codiceEccezioneAtteso = Utilities.toString(CodiceErroreIntegrazione.CODICE_410_AUTENTICAZIONE_RICHIESTA);
				String descrizioneEccezioneAttesa = CostantiErroriIntegrazione.MSG_410_AUTENTICAZIONE_RICHIESTA;
				boolean checkDescrizioneTramiteMatchEsatto = Utilities.CONTROLLO_DESCRIZIONE_TRAMITE_METODO_EQUALS;
				if(this.genericCode) {
					IntegrationFunctionError integrationFunctionError = IntegrationFunctionError.AUTHENTICATION;
					ErroriProperties erroriProperties = ErroriProperties.getInstance(this.log); 
					codiceEccezioneAtteso = erroriProperties.getErrorType_noWrap(integrationFunctionError);
					if(erroriProperties.isForceGenericDetails_noWrap(integrationFunctionError)) {
						descrizioneEccezioneAttesa = erroriProperties.getGenericDetails_noWrap(integrationFunctionError);
						checkDescrizioneTramiteMatchEsatto = Utilities.CONTROLLO_DESCRIZIONE_TRAMITE_METODO_EQUALS;
					}
				}
				
				Utilities.verificaFaultIntegrazione(error, 
						CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE.getCodicePorta(),Imbustamento.ID_MODULO, 
						codiceEccezioneAtteso, descrizioneEccezioneAttesa, checkDescrizioneTramiteMatchEsatto);	
			}finally{
				dbComponentFruitore.close();
				dbComponentErogatore.close();
			}
		}catch(Exception e){
			throw e;
		}finally{
			dbComponentFruitore.close();
			dbComponentErogatore.close();
			
			this._unlock();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * Elemento Risposta Asincrona richiesto per l'invocazione della Porta Delegata
	 * "411";
	 */
	protected void _testElementoRispostaAsincronaNonPresente(boolean unwrap) throws Exception{
		DatabaseComponent dbComponentFruitore = null;
		DatabaseComponent dbComponentErogatore = null;

		try{
			_lock(unwrap);
			
			ClientHttpGenerico client=new ClientHttpGenerico(new Repository());
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_PROFILO_ASINCRONO_SIMMETRICO_MODALITA_SINCRONA);
			client.connectToSoapEngine();
			client.setAutenticazione("adminSilY", "123456");
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

				Reporter.log("Invocazione porta delegata (411) (PortaDelegata: "+CostantiTestSuite.PORTA_DELEGATA_PROFILO_ASINCRONO_SIMMETRICO_CORRELATO_MODALITA_SINCRONA+") non ha causato errori.");
				throw new TestSuiteException("Invocazione porta delegata (411) (PortaDelegata: "+CostantiTestSuite.PORTA_DELEGATA_PROFILO_ASINCRONO_SIMMETRICO_CORRELATO_MODALITA_SINCRONA+") non ha causato errori.");
			} catch (AxisFault error) {
				
				Assert.assertTrue(client.getCodiceStatoHTTP()==500);
				
				String codiceEccezioneAtteso = Utilities.toString(CodiceErroreIntegrazione.CODICE_411_RICEZIONE_CONTENUTI_ASINCRONA_RICHIESTA);
				String descrizioneEccezioneAttesa = CostantiErroriIntegrazione.MSG_411_RICEZIONE_CONTENUTI_ASINCRONA_RICHIESTA;
				boolean checkDescrizioneTramiteMatchEsatto = Utilities.CONTROLLO_DESCRIZIONE_TRAMITE_METODO_EQUALS;
				if(this.genericCode) {
					IntegrationFunctionError integrationFunctionError = IntegrationFunctionError.WRAP_503_INTERNAL_ERROR;
					if(unwrap) {
						integrationFunctionError = IntegrationFunctionError.INTERNAL_REQUEST_ERROR;
					}
					ErroriProperties erroriProperties = ErroriProperties.getInstance(this.log); 
					codiceEccezioneAtteso = erroriProperties.getErrorType_noWrap(integrationFunctionError);
					if(erroriProperties.isForceGenericDetails_noWrap(integrationFunctionError)) {
						descrizioneEccezioneAttesa = erroriProperties.getGenericDetails_noWrap(integrationFunctionError);
						checkDescrizioneTramiteMatchEsatto = Utilities.CONTROLLO_DESCRIZIONE_TRAMITE_METODO_EQUALS;
					}
				}
				
				Utilities.verificaFaultIntegrazione(error, 
						CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE.getCodicePorta(),Imbustamento.ID_MODULO, 
						codiceEccezioneAtteso, descrizioneEccezioneAttesa, checkDescrizioneTramiteMatchEsatto);	
			}finally{
				dbComponentFruitore.close();
				dbComponentErogatore.close();
			}
		}catch(Exception e){
			throw e;
		}finally{
			dbComponentFruitore.close();
			dbComponentErogatore.close();
			
			this._unlock();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * Porta Delegata invocabile dal servizio applicativo solo per riferimento
	 * "412";
	 */
	protected void _testInvioPerRiferimento() throws Exception{
		DatabaseComponent dbComponentFruitore = null;
		DatabaseComponent dbComponentErogatore = null;

		try{
			_lock();
			
			ClientHttpGenerico client=new ClientHttpGenerico(new Repository());
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_MESSAGE_BOX_INVOCAZIONE_PER_RIFERIMENTO);
			client.connectToSoapEngine();
			client.setAutenticazione("gop1", "123456");
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
				
				Reporter.log("Invocazione porta delegata (412) (PortaDelegata: "+CostantiTestSuite.PORTA_DELEGATA_MESSAGE_BOX_INVOCAZIONE_PER_RIFERIMENTO+") non ha causato errori.");
				throw new TestSuiteException("Invocazione porta delegata (412) (PortaDelegata: "+CostantiTestSuite.PORTA_DELEGATA_MESSAGE_BOX_INVOCAZIONE_PER_RIFERIMENTO+") non ha causato errori.");
			} catch (AxisFault error) {
				
				Assert.assertTrue(client.getCodiceStatoHTTP()==500);
				
				String codiceEccezioneAtteso = Utilities.toString(CodiceErroreIntegrazione.CODICE_412_PD_INVOCABILE_SOLO_PER_RIFERIMENTO);
				String descrizioneEccezioneAttesa = CostantiErroriIntegrazione.MSG_412_PD_INVOCABILE_SOLO_PER_RIFERIMENTO;
				boolean checkDescrizioneTramiteMatchEsatto = Utilities.CONTROLLO_DESCRIZIONE_TRAMITE_METODO_EQUALS;
				if(this.genericCode) {
					IntegrationFunctionError integrationFunctionError = IntegrationFunctionError.BAD_REQUEST;
					ErroriProperties erroriProperties = ErroriProperties.getInstance(this.log); 
					codiceEccezioneAtteso = erroriProperties.getErrorType_noWrap(integrationFunctionError);
					if(erroriProperties.isForceGenericDetails_noWrap(integrationFunctionError)) {
						descrizioneEccezioneAttesa = erroriProperties.getGenericDetails_noWrap(integrationFunctionError);
						checkDescrizioneTramiteMatchEsatto = Utilities.CONTROLLO_DESCRIZIONE_TRAMITE_METODO_EQUALS;
					}
				}
				
				Utilities.verificaFaultIntegrazione(error, 
						CostantiTestSuite.SPCOOP_SOGGETTO_EROGATORE.getCodicePorta(),RicezioneContenutiApplicativi.ID_MODULO, 
						codiceEccezioneAtteso, descrizioneEccezioneAttesa, checkDescrizioneTramiteMatchEsatto);	
			}finally{
				dbComponentFruitore.close();
				dbComponentErogatore.close();
			}
		}catch(Exception e){
			throw e;
		}finally{
			dbComponentFruitore.close();
			dbComponentErogatore.close();
			
			this._unlock();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * Porta Delegata invocabile dal servizio applicativo solo senza riferimento
	 * "413";
	 */
	protected void _testInvioPerRiferimentoNonAutorizzato() throws Exception{
		
		DatabaseComponent db = null;
		String idEGov = null;
		try{
			_lock();
			
			Reporter.log("Pubblico un messaggio");
			SOAPEngine utility = new SOAPEngine(null);
			utility.setMessageFromFile(Utilities.testSuiteProperties.getSoap11FileName(), false,false);
			Message msgAxis = utility.getRequestMessage();
			idEGov = IntegrationManager.gestioneProfiloOneway(CostantiTestSuite.PORTA_DELEGATA_MESSAGE_BOX, null, null, null,msgAxis,false);
			try{
				Thread.sleep(10000);
			}catch(Exception e){}
			db = DatabaseProperties.getDatabaseComponentErogatore();
			
			Reporter.log("Effettuo invocazione per riferimento");
			org.openspcoop2.pdd.services.axis14.PD_PortType im = 
				IntegrationManager.getIntegrationManagerPD_axis14(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore().replace("out","IntegrationManager/out"),
						"adminSilX", "123456");
			IntegrationManagerMessage msg = new IntegrationManagerMessage();
			msg.setMessage(org.openspcoop2.utils.resources.FileSystemUtilities.readBytesFromFile(Utilities.testSuiteProperties.getSoap11FileName()));
			im.invocaPortaDelegataPerRiferimento(CostantiTestSuite.PORTA_DELEGATA_AUTENTICAZIONE_BASIC, msg, idEGov);
			
			throw new Exception("Metodo invocaPortaDelegataPerRiferimento non ha causato errori");
		}catch(IntegrationManagerException e){
			verificaSPCoopException_413(e);
			
			db.getVerificatoreMessaggi().deleteMessage(idEGov, "INBOX", Utilities.testSuiteProperties.isUseTransazioni());
		}finally{
			try{
				db.close();
			}catch(Exception e){}

			this._unlock();
		}
			
	}
	private void verificaSPCoopException_413(IntegrationManagerException e) throws Exception{
		
		ErroriProperties erroriProperties = ErroriProperties.getInstance(this.log); 
		IntegrationFunctionError integrationFunctionError = IntegrationFunctionError.BAD_REQUEST;
		
		String codiceErrore = Utilities.toString(CodiceErroreIntegrazione.CODICE_413_PD_INVOCABILE_SOLO_SENZA_RIFERIMENTO);
		if(this.genericCode) {
			codiceErrore = erroriProperties.getErrorType_noWrap(integrationFunctionError);
		}
		Reporter.log("Codice errore ["+e.getCodiceEccezione()+"]");
		Reporter.log("confronto con ["+codiceErrore+"]");
		Assert.assertTrue(codiceErrore.equals(e.getCodiceEccezione()));
			
		Reporter.log("ID Porta ["+e.getIdentificativoPorta()+"]");
		Reporter.log("confronto con [MinisteroFruitoreSPCoopIT]");
		Assert.assertTrue(CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE.getCodicePorta().equals(e.getIdentificativoPorta()));
			
		Reporter.log("ID funzione ["+e.getIdentificativoFunzione()+"]");
		Reporter.log("confronto con [RicezioneContenutiApplicativiIntegrationManager]");
		Assert.assertTrue("RicezioneContenutiApplicativiIntegrationManager".equals(e.getIdentificativoFunzione()));
			
		Reporter.log("TipoEccezione ["+e.getTipoEccezione()+"]");
		Reporter.log("confronto con [EccezioneIntegrazione]");
		Assert.assertTrue("EccezioneIntegrazione".equals(e.getTipoEccezione()));
			
		Reporter.log("MessaggioErrore ["+e.getDescrizioneEccezione()+"]");
		String detail = CostantiErroriIntegrazione.MSG_413_PD_INVOCABILE_SOLO_SENZA_RIFERIMENTO;
		if(this.genericCode && erroriProperties.isForceGenericDetails_noWrap(integrationFunctionError)) {
			detail = erroriProperties.getGenericDetails_noWrap(integrationFunctionError);
		}
		Reporter.log("confronto con ["+detail+"]");
		Assert.assertTrue(detail.equals(e.getDescrizioneEccezione()));
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * Consegna in ordine utilizzabile sono con profilo Oneway
	 * "414";
	 */
	protected void _testConsegnaOrdineProfiloSincrono() throws Exception{
		DatabaseComponent dbComponentFruitore = null;
		DatabaseComponent dbComponentErogatore = null;

		try{
			_lock();
			
			ClientHttpGenerico client=new ClientHttpGenerico(new Repository());
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_CONSEGNA_IN_ORDINE_PROFILO_SINCRONO);
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
				
				Reporter.log("Invocazione porta delegata (414) (PortaDelegata: "+CostantiTestSuite.PORTA_DELEGATA_CONSEGNA_IN_ORDINE_PROFILO_SINCRONO+") non ha causato errori.");
				throw new TestSuiteException("Invocazione porta delegata (414) (PortaDelegata: "+CostantiTestSuite.PORTA_DELEGATA_CONSEGNA_IN_ORDINE_PROFILO_SINCRONO+") non ha causato errori.");
			} catch (AxisFault error) {
				
				Assert.assertTrue(client.getCodiceStatoHTTP()==500);
				
				String codiceEccezioneAtteso = Utilities.toString(CodiceErroreIntegrazione.CODICE_414_CONSEGNA_IN_ORDINE_CON_PROFILO_NO_ONEWAY);
				String descrizioneEccezioneAttesa = CostantiErroriIntegrazione.MSG_414_CONSEGNA_IN_ORDINE_CON_PROFILO_NO_ONEWAY;
				boolean checkDescrizioneTramiteMatchEsatto = Utilities.CONTROLLO_DESCRIZIONE_TRAMITE_METODO_EQUALS;
				if(this.genericCode) {
					IntegrationFunctionError integrationFunctionError = IntegrationFunctionError.NOT_SUPPORTED_BY_PROTOCOL;
					ErroriProperties erroriProperties = ErroriProperties.getInstance(this.log); 
					codiceEccezioneAtteso = erroriProperties.getErrorType_noWrap(integrationFunctionError);
					if(erroriProperties.isForceGenericDetails_noWrap(integrationFunctionError)) {
						descrizioneEccezioneAttesa = erroriProperties.getGenericDetails_noWrap(integrationFunctionError);
						checkDescrizioneTramiteMatchEsatto = Utilities.CONTROLLO_DESCRIZIONE_TRAMITE_METODO_EQUALS;
					}
				}
				
				Utilities.verificaFaultIntegrazione(error, 
						CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE.getCodicePorta(),Imbustamento.ID_MODULO, 
						codiceEccezioneAtteso, descrizioneEccezioneAttesa, checkDescrizioneTramiteMatchEsatto);	
			}finally{
				dbComponentFruitore.close();
				dbComponentErogatore.close();
			}
		}catch(Exception e){
			throw e;
		}finally{
			dbComponentFruitore.close();
			dbComponentErogatore.close();
			
			this._unlock();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * Consegna in ordine non utilizzabile per mancanza di dati necessari
	 * "415";
	 */
	protected void _testConsegnaOrdineConfigurazioneErrata_confermaRicezioneMancante() throws Exception{
		DatabaseComponent dbComponentFruitore = null;
		DatabaseComponent dbComponentErogatore = null;

		try{
			_lock();
			
			ClientHttpGenerico client=new ClientHttpGenerico(new Repository());
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_CONSEGNA_IN_ORDINE_CONFIGURAZIONE_ERRATA_CONFERMA_RICEZIONE);
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
				
				Reporter.log("Invocazione porta delegata (415) (PortaDelegata: "+CostantiTestSuite.PORTA_DELEGATA_CONSEGNA_IN_ORDINE_CONFIGURAZIONE_ERRATA_CONFERMA_RICEZIONE+") non ha causato errori.");
				throw new TestSuiteException("Invocazione porta delegata (415) (PortaDelegata: "+CostantiTestSuite.PORTA_DELEGATA_CONSEGNA_IN_ORDINE_CONFIGURAZIONE_ERRATA_CONFERMA_RICEZIONE+") non ha causato errori.");
			} catch (AxisFault error) {
				
				Assert.assertTrue(client.getCodiceStatoHTTP()==500);
				
				String codiceEccezioneAtteso = Utilities.toString(CodiceErroreIntegrazione.CODICE_415_CONSEGNA_IN_ORDINE_SENZA_VINCOLI_RICHIESTI);
				String descrizioneEccezioneAttesa = CostantiErroriIntegrazione.MSG_415_CONSEGNA_IN_ORDINE_SENZA_VINCOLI_RICHIESTI;
				boolean checkDescrizioneTramiteMatchEsatto = Utilities.CONTROLLO_DESCRIZIONE_TRAMITE_METODO_EQUALS;
				if(this.genericCode) {
					IntegrationFunctionError integrationFunctionError = IntegrationFunctionError.NOT_SUPPORTED_BY_PROTOCOL;
					ErroriProperties erroriProperties = ErroriProperties.getInstance(this.log); 
					codiceEccezioneAtteso = erroriProperties.getErrorType_noWrap(integrationFunctionError);
					if(erroriProperties.isForceGenericDetails_noWrap(integrationFunctionError)) {
						descrizioneEccezioneAttesa = erroriProperties.getGenericDetails_noWrap(integrationFunctionError);
						checkDescrizioneTramiteMatchEsatto = Utilities.CONTROLLO_DESCRIZIONE_TRAMITE_METODO_EQUALS;
					}
				}
				
				Utilities.verificaFaultIntegrazione(error, 
						CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE.getCodicePorta(),Imbustamento.ID_MODULO, 
						codiceEccezioneAtteso, descrizioneEccezioneAttesa, checkDescrizioneTramiteMatchEsatto);		
			}finally{
				dbComponentFruitore.close();
				dbComponentErogatore.close();
			}
		}catch(Exception e){
			throw e;
		}finally{
			dbComponentFruitore.close();
			dbComponentErogatore.close();
			
			this._unlock();
		}
	}
	
	protected void _testConsegnaOrdineConfigurazioneErrata_filtroDuplicatiMancante() throws Exception{
		DatabaseComponent dbComponentFruitore = null;
		DatabaseComponent dbComponentErogatore = null;

		try{
			_lock();
			
			ClientHttpGenerico client=new ClientHttpGenerico(new Repository());
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_CONSEGNA_IN_ORDINE_CONFIGURAZIONE_ERRATA_FILTRO_DUPLICATI);
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
				
				Reporter.log("Invocazione porta delegata (415) (PortaDelegata: "+CostantiTestSuite.PORTA_DELEGATA_CONSEGNA_IN_ORDINE_CONFIGURAZIONE_ERRATA_FILTRO_DUPLICATI+") non ha causato errori.");
				throw new TestSuiteException("Invocazione porta delegata (415) (PortaDelegata: "+CostantiTestSuite.PORTA_DELEGATA_CONSEGNA_IN_ORDINE_CONFIGURAZIONE_ERRATA_FILTRO_DUPLICATI+") non ha causato errori.");
			} catch (AxisFault error) {
				
				Assert.assertTrue(client.getCodiceStatoHTTP()==500);
				
				String codiceEccezioneAtteso = Utilities.toString(CodiceErroreIntegrazione.CODICE_415_CONSEGNA_IN_ORDINE_SENZA_VINCOLI_RICHIESTI);
				String descrizioneEccezioneAttesa = CostantiErroriIntegrazione.MSG_415_CONSEGNA_IN_ORDINE_SENZA_VINCOLI_RICHIESTI;
				boolean checkDescrizioneTramiteMatchEsatto = Utilities.CONTROLLO_DESCRIZIONE_TRAMITE_METODO_EQUALS;
				if(this.genericCode) {
					IntegrationFunctionError integrationFunctionError = IntegrationFunctionError.NOT_SUPPORTED_BY_PROTOCOL;
					ErroriProperties erroriProperties = ErroriProperties.getInstance(this.log); 
					codiceEccezioneAtteso = erroriProperties.getErrorType_noWrap(integrationFunctionError);
					if(erroriProperties.isForceGenericDetails_noWrap(integrationFunctionError)) {
						descrizioneEccezioneAttesa = erroriProperties.getGenericDetails_noWrap(integrationFunctionError);
						checkDescrizioneTramiteMatchEsatto = Utilities.CONTROLLO_DESCRIZIONE_TRAMITE_METODO_EQUALS;
					}
				}
				
				Utilities.verificaFaultIntegrazione(error, 
						CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE.getCodicePorta(),Imbustamento.ID_MODULO, 
						codiceEccezioneAtteso, descrizioneEccezioneAttesa, checkDescrizioneTramiteMatchEsatto);	
			}finally{
				dbComponentFruitore.close();
				dbComponentErogatore.close();
			}
		}catch(Exception e){
			throw e;
		}finally{
			dbComponentFruitore.close();
			dbComponentErogatore.close();
			
			this._unlock();
		}
	}
	
	protected void _testConsegnaOrdineConfigurazioneErrata_idCollaborazioneMancante() throws Exception{
		DatabaseComponent dbComponentFruitore = null;
		DatabaseComponent dbComponentErogatore = null;

		try{
			_lock();
			
			ClientHttpGenerico client=new ClientHttpGenerico(new Repository());
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_CONSEGNA_IN_ORDINE_CONFIGURAZIONE_ERRATA_ID_COLLABORAZIONE);
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
				
				Reporter.log("Invocazione porta delegata (415) (PortaDelegata: "+CostantiTestSuite.PORTA_DELEGATA_CONSEGNA_IN_ORDINE_CONFIGURAZIONE_ERRATA_ID_COLLABORAZIONE+") non ha causato errori.");
				throw new TestSuiteException("Invocazione porta delegata (415) (PortaDelegata: "+CostantiTestSuite.PORTA_DELEGATA_CONSEGNA_IN_ORDINE_CONFIGURAZIONE_ERRATA_ID_COLLABORAZIONE+") non ha causato errori.");
			} catch (AxisFault error) {
				
				Assert.assertTrue(client.getCodiceStatoHTTP()==500);
				
				String codiceEccezioneAtteso = Utilities.toString(CodiceErroreIntegrazione.CODICE_415_CONSEGNA_IN_ORDINE_SENZA_VINCOLI_RICHIESTI);
				String descrizioneEccezioneAttesa = CostantiErroriIntegrazione.MSG_415_CONSEGNA_IN_ORDINE_SENZA_VINCOLI_RICHIESTI;
				boolean checkDescrizioneTramiteMatchEsatto = Utilities.CONTROLLO_DESCRIZIONE_TRAMITE_METODO_EQUALS;
				if(this.genericCode) {
					IntegrationFunctionError integrationFunctionError = IntegrationFunctionError.NOT_SUPPORTED_BY_PROTOCOL;
					ErroriProperties erroriProperties = ErroriProperties.getInstance(this.log); 
					codiceEccezioneAtteso = erroriProperties.getErrorType_noWrap(integrationFunctionError);
					if(erroriProperties.isForceGenericDetails_noWrap(integrationFunctionError)) {
						descrizioneEccezioneAttesa = erroriProperties.getGenericDetails_noWrap(integrationFunctionError);
						checkDescrizioneTramiteMatchEsatto = Utilities.CONTROLLO_DESCRIZIONE_TRAMITE_METODO_EQUALS;
					}
				}
				
				Utilities.verificaFaultIntegrazione(error, 
						CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE.getCodicePorta(),Imbustamento.ID_MODULO, 
						codiceEccezioneAtteso, descrizioneEccezioneAttesa, checkDescrizioneTramiteMatchEsatto);	
			}finally{
				dbComponentFruitore.close();
				dbComponentErogatore.close();
			}
		}catch(Exception e){
			throw e;
		}finally{
			dbComponentFruitore.close();
			dbComponentErogatore.close();
			
			this._unlock();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * Correlazione Applicativa non riuscita
	 * "416";
	 */
	protected void _testCorrelazioneApplicativaErrata() throws Exception{
		
		DatabaseComponent dbComponentFruitore = null;
		DatabaseComponent dbComponentErogatore = null;

		Date dataInizioTest = null;
		Date dataFineTest = null;
		try{
			_lock();
			dataInizioTest = DateManager.getDate();
			
			ClientHttpGenerico client=new ClientHttpGenerico(new Repository());
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_PROFILO_SINCRONO_INTEGRAZIONE_CORRELAZIONE_APPLICATIVA_URL_BASED);
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
				
				Reporter.log("Invocazione porta delegata (416) (PortaDelegata: "+CostantiTestSuite.PORTA_DELEGATA_PROFILO_SINCRONO_INTEGRAZIONE_CORRELAZIONE_APPLICATIVA_URL_BASED+") non ha causato errori.");
				throw new TestSuiteException("Invocazione porta delegata (416) (PortaDelegata: "+CostantiTestSuite.PORTA_DELEGATA_PROFILO_SINCRONO_INTEGRAZIONE_CORRELAZIONE_APPLICATIVA_URL_BASED+") non ha causato errori.");
			} catch (AxisFault error) {
				
				Assert.assertTrue(client.getCodiceStatoHTTP()==500);
				
				String codiceEccezioneAtteso = Utilities.toString(CodiceErroreIntegrazione.CODICE_416_CORRELAZIONE_APPLICATIVA_RICHIESTA_ERRORE);
				String descrizioneEccezioneAttesa = CostantiErroriIntegrazione.MSG_416_CORRELAZIONE_APPLICATIVA_RICHIESTA_ERRORE;
				boolean checkDescrizioneTramiteMatchEsatto = Utilities.CONTROLLO_DESCRIZIONE_TRAMITE_METODO_CONTAINS;
				if(this.genericCode) {
					IntegrationFunctionError integrationFunctionError = IntegrationFunctionError.APPLICATION_CORRELATION_IDENTIFICATION_REQUEST_FAILED;
					ErroriProperties erroriProperties = ErroriProperties.getInstance(this.log); 
					codiceEccezioneAtteso = erroriProperties.getErrorType_noWrap(integrationFunctionError);
					if(erroriProperties.isForceGenericDetails_noWrap(integrationFunctionError)) {
						descrizioneEccezioneAttesa = erroriProperties.getGenericDetails_noWrap(integrationFunctionError);
						checkDescrizioneTramiteMatchEsatto = Utilities.CONTROLLO_DESCRIZIONE_TRAMITE_METODO_EQUALS;
					}
				}
				
				Utilities.verificaFaultIntegrazione(error, 
						CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE.getCodicePorta(),RicezioneContenutiApplicativi.ID_MODULO, 
						codiceEccezioneAtteso, descrizioneEccezioneAttesa, checkDescrizioneTramiteMatchEsatto);	
			}finally{
				dbComponentFruitore.close();
				dbComponentErogatore.close();
			}
		}catch(Exception e){
			throw e;
		}finally{
			dbComponentFruitore.close();
			dbComponentErogatore.close();

			this._unlock();
			dataFineTest = DateManager.getDate();
		}
		
		ErroreAttesoOpenSPCoopLogCore err = new ErroreAttesoOpenSPCoopLogCore();
		err.setIntervalloInferiore(dataInizioTest);
		err.setIntervalloSuperiore(dataFineTest);
		err.setMsgErrore("identificativo di correlazione applicativa non identificato nell'elemento [*] con modalita' di acquisizione urlBased (Pattern:.+correlazioneApplicativa=([^&]*).*): nessun match trovato");
		this.erroriAttesiOpenSPCoopCore.add(err);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * Impossibile istanziare un validatore: XSD non valido o mancante
	 * "417";
	 */
	protected void _testValidazioneApplicativaSenzaXsd_tipoXSD(boolean unwrap) throws Exception{
		
		DatabaseComponent dbComponentFruitore = null;
		DatabaseComponent dbComponentErogatore = null;

		Date dataInizioTest = null;
		Date dataFineTest = null;
		try{
			_lock(unwrap);
			dataInizioTest = DateManager.getDate();
			
			ClientHttpGenerico client=new ClientHttpGenerico(new Repository());
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_VALIDAZIONE_APPLICATIVA_SENZA_XSD_TIPO_XSD);
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
				
				Reporter.log("Invocazione porta delegata (417) (PortaDelegata: "+CostantiTestSuite.PORTA_DELEGATA_VALIDAZIONE_APPLICATIVA_SENZA_XSD_TIPO_XSD+") non ha causato errori.");
				throw new TestSuiteException("Invocazione porta delegata (417) (PortaDelegata: "+CostantiTestSuite.PORTA_DELEGATA_VALIDAZIONE_APPLICATIVA_SENZA_XSD_TIPO_XSD+") non ha causato errori.");
			} catch (AxisFault error) {
				
				Assert.assertTrue(client.getCodiceStatoHTTP()==500);
				
				String codiceEccezioneAtteso = Utilities.toString(CodiceErroreIntegrazione.CODICE_417_COSTRUZIONE_VALIDATORE_TRAMITE_INTERFACCIA_FALLITA);
				String descrizioneEccezioneAttesa = CostantiErroriIntegrazione.MSG_417_COSTRUZIONE_VALIDATORE_WSDL_FALLITA.replace("TIPO_WSDL", "Schema xsd dei messaggi");
				boolean checkDescrizioneTramiteMatchEsatto = Utilities.CONTROLLO_DESCRIZIONE_TRAMITE_METODO_EQUALS;
				if(this.genericCode) {
					IntegrationFunctionError integrationFunctionError = IntegrationFunctionError.WRAP_503_INTERNAL_ERROR;
					if(unwrap) {
						integrationFunctionError = IntegrationFunctionError.INTERNAL_REQUEST_ERROR;
					}
					ErroriProperties erroriProperties = ErroriProperties.getInstance(this.log); 
					codiceEccezioneAtteso = erroriProperties.getErrorType_noWrap(integrationFunctionError);
					if(erroriProperties.isForceGenericDetails_noWrap(integrationFunctionError)) {
						descrizioneEccezioneAttesa = erroriProperties.getGenericDetails_noWrap(integrationFunctionError);
						checkDescrizioneTramiteMatchEsatto = Utilities.CONTROLLO_DESCRIZIONE_TRAMITE_METODO_EQUALS;
					}
				}
				
				Utilities.verificaFaultIntegrazione(error, 
						CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE.getCodicePorta(),RicezioneContenutiApplicativi.ID_MODULO, 
						codiceEccezioneAtteso, descrizioneEccezioneAttesa, checkDescrizioneTramiteMatchEsatto);	
			}finally{
				dbComponentFruitore.close();
				dbComponentErogatore.close();
			}
		}catch(Exception e){
			throw e;
		}finally{
			dbComponentFruitore.close();
			dbComponentErogatore.close();

			this._unlock();
			dataFineTest = DateManager.getDate();
		}
		
		ErroreAttesoOpenSPCoopLogCore err = new ErroreAttesoOpenSPCoopLogCore();
		err.setIntervalloInferiore(dataInizioTest);
		err.setIntervalloSuperiore(dataFineTest);
		err.setMsgErrore("Riscontrato errore durante l'inizializzazione: [SchemaXSD] L'accordo di servizio parte comune non contiene schemi");
		this.erroriAttesiOpenSPCoopCore.add(err);
	}
	
	protected void _testValidazioneApplicativaSenzaXsd_tipoWSDL(boolean unwrap) throws Exception{
		
		DatabaseComponent dbComponentFruitore = null;
		DatabaseComponent dbComponentErogatore = null;

		Date dataInizioTest = null;
		Date dataFineTest = null;
		try{
			_lock(unwrap);
			dataInizioTest = DateManager.getDate();
			
			ClientHttpGenerico client=new ClientHttpGenerico(new Repository());
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_VALIDAZIONE_APPLICATIVA_SENZA_XSD_TIPO_WSDL);
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
				
				Reporter.log("Invocazione porta delegata (417) (PortaDelegata: "+CostantiTestSuite.PORTA_DELEGATA_VALIDAZIONE_APPLICATIVA_SENZA_XSD_TIPO_WSDL+") non ha causato errori.");
				throw new TestSuiteException("Invocazione porta delegata (417) (PortaDelegata: "+CostantiTestSuite.PORTA_DELEGATA_VALIDAZIONE_APPLICATIVA_SENZA_XSD_TIPO_WSDL+") non ha causato errori.");
			} catch (AxisFault error) {
				
				Assert.assertTrue(client.getCodiceStatoHTTP()==500);
				
				String codiceEccezioneAtteso = Utilities.toString(CodiceErroreIntegrazione.CODICE_417_COSTRUZIONE_VALIDATORE_TRAMITE_INTERFACCIA_FALLITA);
				String descrizioneEccezioneAttesa = CostantiErroriIntegrazione.MSG_417_COSTRUZIONE_VALIDATORE_WSDL_FALLITA.replace("TIPO_WSDL", "Schema xsd dei messaggi");
				boolean checkDescrizioneTramiteMatchEsatto = Utilities.CONTROLLO_DESCRIZIONE_TRAMITE_METODO_EQUALS;
				if(this.genericCode) {
					IntegrationFunctionError integrationFunctionError = IntegrationFunctionError.WRAP_503_INTERNAL_ERROR;
					if(unwrap) {
						integrationFunctionError = IntegrationFunctionError.INTERNAL_REQUEST_ERROR;
					}
					ErroriProperties erroriProperties = ErroriProperties.getInstance(this.log); 
					codiceEccezioneAtteso = erroriProperties.getErrorType_noWrap(integrationFunctionError);
					if(erroriProperties.isForceGenericDetails_noWrap(integrationFunctionError)) {
						descrizioneEccezioneAttesa = erroriProperties.getGenericDetails_noWrap(integrationFunctionError);
						checkDescrizioneTramiteMatchEsatto = Utilities.CONTROLLO_DESCRIZIONE_TRAMITE_METODO_EQUALS;
					}
				}
				
				Utilities.verificaFaultIntegrazione(error, 
						CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE.getCodicePorta(),RicezioneContenutiApplicativi.ID_MODULO, 
						codiceEccezioneAtteso, descrizioneEccezioneAttesa, checkDescrizioneTramiteMatchEsatto);	
			}finally{
				dbComponentFruitore.close();
				dbComponentErogatore.close();
			}
		}catch(Exception e){
			throw e;
		}finally{
			dbComponentFruitore.close();
			dbComponentErogatore.close();

			this._unlock();
			dataFineTest = DateManager.getDate();
		}
		
		ErroreAttesoOpenSPCoopLogCore err = new ErroreAttesoOpenSPCoopLogCore();
		err.setIntervalloInferiore(dataInizioTest);
		err.setIntervalloSuperiore(dataFineTest);
		err.setMsgErrore("Riscontrato errore durante l'inizializzazione: [SchemaXSD] L'accordo di servizio parte comune non contiene schemi");
		this.erroriAttesiOpenSPCoopCore.add(err);
	}
	
	protected void _testValidazioneApplicativaSenzaXsd_tipoOPENSPCOOP(boolean unwrap) throws Exception{
		
		DatabaseComponent dbComponentFruitore = null;
		DatabaseComponent dbComponentErogatore = null;

		Date dataInizioTest = null;
		Date dataFineTest = null;
		try{
			_lock(unwrap);
			dataInizioTest = DateManager.getDate();
			
			ClientHttpGenerico client=new ClientHttpGenerico(new Repository());
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_VALIDAZIONE_APPLICATIVA_SENZA_XSD_TIPO_OPENSPCOOP);
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
				
				Reporter.log("Invocazione porta delegata (417) (PortaDelegata: "+CostantiTestSuite.PORTA_DELEGATA_VALIDAZIONE_APPLICATIVA_SENZA_XSD_TIPO_OPENSPCOOP+") non ha causato errori.");
				throw new TestSuiteException("Invocazione porta delegata (417) (PortaDelegata: "+CostantiTestSuite.PORTA_DELEGATA_VALIDAZIONE_APPLICATIVA_SENZA_XSD_TIPO_OPENSPCOOP+") non ha causato errori.");
			} catch (AxisFault error) {
				
				Assert.assertTrue(client.getCodiceStatoHTTP()==500);
				
				String codiceEccezioneAtteso = Utilities.toString(CodiceErroreIntegrazione.CODICE_417_COSTRUZIONE_VALIDATORE_TRAMITE_INTERFACCIA_FALLITA);
				String descrizioneEccezioneAttesa = CostantiErroriIntegrazione.MSG_417_COSTRUZIONE_VALIDATORE_WSDL_FALLITA.replace("TIPO_WSDL", "Schema xsd dei messaggi");
				boolean checkDescrizioneTramiteMatchEsatto = Utilities.CONTROLLO_DESCRIZIONE_TRAMITE_METODO_EQUALS;
				if(this.genericCode) {
					IntegrationFunctionError integrationFunctionError = IntegrationFunctionError.WRAP_503_INTERNAL_ERROR;
					if(unwrap) {
						integrationFunctionError = IntegrationFunctionError.INTERNAL_REQUEST_ERROR;
					}
					ErroriProperties erroriProperties = ErroriProperties.getInstance(this.log); 
					codiceEccezioneAtteso = erroriProperties.getErrorType_noWrap(integrationFunctionError);
					if(erroriProperties.isForceGenericDetails_noWrap(integrationFunctionError)) {
						descrizioneEccezioneAttesa = erroriProperties.getGenericDetails_noWrap(integrationFunctionError);
						checkDescrizioneTramiteMatchEsatto = Utilities.CONTROLLO_DESCRIZIONE_TRAMITE_METODO_EQUALS;
					}
				}
				
				Utilities.verificaFaultIntegrazione(error, 
						CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE.getCodicePorta(),RicezioneContenutiApplicativi.ID_MODULO, 
						codiceEccezioneAtteso, descrizioneEccezioneAttesa, checkDescrizioneTramiteMatchEsatto);	
			}finally{
				dbComponentFruitore.close();
				dbComponentErogatore.close();
			}
		}catch(Exception e){
			throw e;
		}finally{
			dbComponentFruitore.close();
			dbComponentErogatore.close();

			this._unlock();
			dataFineTest = DateManager.getDate();
		}
		
		ErroreAttesoOpenSPCoopLogCore err = new ErroreAttesoOpenSPCoopLogCore();
		err.setIntervalloInferiore(dataInizioTest);
		err.setIntervalloSuperiore(dataFineTest);
		err.setMsgErrore("Riscontrato errore durante l'inizializzazione: [SchemaXSD] L'accordo di servizio parte comune non contiene schemi");
		this.erroriAttesiOpenSPCoopCore.add(err);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	

	/**
	 * Validazione del messaggio di richiesta fallita
	 * "418";
	 */
	protected void _testValidazioneApplicativaRichiestaFallita() throws Exception{
		
		DatabaseComponent dbComponentFruitore = null;
		DatabaseComponent dbComponentErogatore = null;

		String portaDelegata = CostantiTestSuite.PORTA_DELEGATA_VALIDAZIONE_WSDL_GENERICA_GESTIONE_UTENTI_WDL+"/"+
			CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_VALIDAZIONE_WSDL_OPENSPCOOP_AZIONE_REGISTRAZIONE_UTENTE_WDL;
		
		Date dataInizioTest = null;
		Date dataFineTest = null;
		try{
			_lock();
			dataInizioTest = DateManager.getDate();
			
			ClientHttpGenerico client=new ClientHttpGenerico(new Repository());
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(portaDelegata);
			client.connectToSoapEngine();
			client.setMessageFromFile(Utilities.testSuiteProperties.getPathTestValidazioneContenutiApplicativi()+
					File.separator+"operazioneRegistrazioneUtenteWDLNonCorrettamenteFormato.xml", false);
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
				
				Reporter.log("Invocazione porta delegata (418) (PortaDelegata: "+portaDelegata+") non ha causato errori.");
				throw new TestSuiteException("Invocazione porta delegata (418) (PortaDelegata: "+portaDelegata+") non ha causato errori.");
			} catch (AxisFault error) {
				
				Assert.assertTrue(client.getCodiceStatoHTTP()==500);
				
				String codiceEccezioneAtteso = Utilities.toString(CodiceErroreIntegrazione.CODICE_418_VALIDAZIONE_RICHIESTA_TRAMITE_INTERFACCIA_FALLITA);
				ErroriProperties erroriProperties = ErroriProperties.getInstance(this.log); 
				IntegrationFunctionError integrationFunctionError = IntegrationFunctionError.INVALID_REQUEST_CONTENT;
				String descrizioneEccezioneAttesa = erroriProperties.getGenericDetails_noWrap(integrationFunctionError)+": Invalid request by WSDL specification";
				boolean checkDescrizioneTramiteMatchEsatto = Utilities.CONTROLLO_DESCRIZIONE_TRAMITE_METODO_CONTAINS;
				if(this.genericCode) {
					codiceEccezioneAtteso = erroriProperties.getErrorType_noWrap(integrationFunctionError);
					if(erroriProperties.isForceGenericDetails_noWrap(integrationFunctionError)) {
						descrizioneEccezioneAttesa = erroriProperties.getGenericDetails_noWrap(integrationFunctionError);
						checkDescrizioneTramiteMatchEsatto = Utilities.CONTROLLO_DESCRIZIONE_TRAMITE_METODO_EQUALS;
					}
				}
				
				Utilities.verificaFaultIntegrazione(error, 
						CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE.getCodicePorta(),RicezioneContenutiApplicativi.ID_MODULO, 
						codiceEccezioneAtteso, descrizioneEccezioneAttesa, checkDescrizioneTramiteMatchEsatto);	
			}finally{
				dbComponentFruitore.close();
				dbComponentErogatore.close();
			}
		}catch(Exception e){
			throw e;
		}finally{
			dbComponentFruitore.close();
			dbComponentErogatore.close();

			this._unlock();
			dataFineTest = DateManager.getDate();
		}
		
		ErroreAttesoOpenSPCoopLogCore err = new ErroreAttesoOpenSPCoopLogCore();
		err.setIntervalloInferiore(dataInizioTest);
		err.setIntervalloSuperiore(dataFineTest);
		err.setMsgErrore("Validazione WSDL (true) fallita: Messaggio con elementi non conformi alla definizione wsdl dell'Operation [registrazioneUtenteWDL] del port-type [GestioneUtentiWrappedDocumentLiteral] (AccordoServizio:ASMultiPortTypeValidazioneWSDL:1 style:document use:literal)");
		this.erroriAttesiOpenSPCoopCore.add(err);
		
		ErroreAttesoOpenSPCoopLogCore err2 = new ErroreAttesoOpenSPCoopLogCore();
		err2.setIntervalloInferiore(dataInizioTest);
		err2.setIntervalloSuperiore(dataFineTest);
		err2.setMsgErrore("[ValidazioneContenutiApplicativi Richiesta] Riscontrata non conformità rispetto all'interfaccia WSDL; Messaggio con elementi non conformi alla definizione wsdl dell'Operation [registrazioneUtenteWDL] del port-type [GestioneUtentiWrappedDocumentLiteral] (AccordoServizio:ASMultiPortTypeValidazioneWSDL:1 style:document use:literal)");
		this.erroriAttesiOpenSPCoopCore.add(err2);
		
	}
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * Validazione del messaggio di risposta fallita
	 * "419";
	 */
	protected void _testValidazioneApplicativaRispostaFallita(boolean unwrap) throws Exception{
		
		DatabaseComponent dbComponentFruitore = null;
		DatabaseComponent dbComponentErogatore = null;

		String portaDelegata = CostantiTestSuite.PORTA_DELEGATA_VALIDAZIONE_APPLICATIVA_RISPOSTA_FALLITA;
		
		Date dataInizioTest = null;
		Date dataFineTest = null;
		try{
			_lock(unwrap);
			dataInizioTest = DateManager.getDate();
			
			ClientHttpGenerico client=new ClientHttpGenerico(new Repository());
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(portaDelegata);
			client.connectToSoapEngine();
			client.setMessageFromFile(Utilities.testSuiteProperties.getPathTestValidazioneContenutiApplicativi()+
					File.separator+"operazioneRegistrazioneUtenteWDL.xml", false);
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
				
				Reporter.log("Invocazione porta delegata (419) (PortaDelegata: "+portaDelegata+") non ha causato errori.");
				throw new TestSuiteException("Invocazione porta delegata (419) (PortaDelegata: "+portaDelegata+") non ha causato errori.");
			} catch (AxisFault error) {
				
				Assert.assertTrue(client.getCodiceStatoHTTP()==500);
				
				String codiceEccezioneAtteso = Utilities.toString(CodiceErroreIntegrazione.CODICE_419_VALIDAZIONE_RISPOSTA_TRAMITE_INTERFACCIA_FALLITA);
				ErroriProperties erroriProperties = ErroriProperties.getInstance(this.log); 
				IntegrationFunctionError integrationFunctionError = IntegrationFunctionError.INVALID_RESPONSE_CONTENT;
				String descrizioneEccezioneAttesa = erroriProperties.getGenericDetails_noWrap(integrationFunctionError)+
						": (element {http://schemas.xmlsoap.org/soap/envelope/}AlterazioneMessaggio) cvc-elt.1.a: Cannot find the declaration of element 'soapenv:AlterazioneMessaggio'.";
				boolean checkDescrizioneTramiteMatchEsatto = Utilities.CONTROLLO_DESCRIZIONE_TRAMITE_METODO_EQUALS;
				if(this.genericCode) {
					if(!unwrap) {
						integrationFunctionError = IntegrationFunctionError.WRAP_502_BAD_RESPONSE;
						descrizioneEccezioneAttesa =  erroriProperties.getGenericDetails_noWrap(integrationFunctionError);
					}
					codiceEccezioneAtteso = erroriProperties.getErrorType_noWrap(integrationFunctionError);
					if(erroriProperties.isForceGenericDetails_noWrap(integrationFunctionError)) {
						descrizioneEccezioneAttesa = erroriProperties.getGenericDetails_noWrap(integrationFunctionError);
						checkDescrizioneTramiteMatchEsatto = Utilities.CONTROLLO_DESCRIZIONE_TRAMITE_METODO_EQUALS;
					}
				}
				
				Utilities.verificaFaultIntegrazione(error, 
						CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE.getCodicePorta(),InoltroBuste.ID_MODULO, 
						codiceEccezioneAtteso, descrizioneEccezioneAttesa, checkDescrizioneTramiteMatchEsatto);	
			}finally{
				dbComponentFruitore.close();
				dbComponentErogatore.close();
			}
		}catch(Exception e){
			throw e;
		}finally{
			dbComponentFruitore.close();
			dbComponentErogatore.close();

			this._unlock();
			dataFineTest = DateManager.getDate();
		}
		
		ErroreAttesoOpenSPCoopLogCore err = new ErroreAttesoOpenSPCoopLogCore();
		err.setIntervalloInferiore(dataInizioTest);
		err.setIntervalloSuperiore(dataFineTest);
		err.setMsgErrore("Validazione fallita (elemento {http://schemas.xmlsoap.org/soap/envelope/}AlterazioneMessaggio) [<?xml version=\"1.0\" encoding=\"UTF-8\"?><soapenv:AlterazioneMessaggio xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"/>]: cvc-elt.1: Cannot find the declaration of element 'soapenv:AlterazioneMessaggio'.");
		this.erroriAttesiOpenSPCoopCore.add(err);
		
		ErroreAttesoOpenSPCoopLogCore err_a = new ErroreAttesoOpenSPCoopLogCore();
		err_a.setIntervalloInferiore(dataInizioTest);
		err_a.setIntervalloSuperiore(dataFineTest);
		err_a.setMsgErrore("Validazione fallita (elemento {http://schemas.xmlsoap.org/soap/envelope/}AlterazioneMessaggio) [<?xml version=\"1.0\" encoding=\"UTF-8\"?><soapenv:AlterazioneMessaggio xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"/>]: cvc-elt.1.a: Cannot find the declaration of element 'soapenv:AlterazioneMessaggio'.");
		this.erroriAttesiOpenSPCoopCore.add(err_a);
		
		ErroreAttesoOpenSPCoopLogCore err2 = new ErroreAttesoOpenSPCoopLogCore();
		err2.setIntervalloInferiore(dataInizioTest);
		err2.setIntervalloSuperiore(dataFineTest);
		err2.setMsgErrore("Riscontrata non conformità rispetto agli schemi XSD");
		this.erroriAttesiOpenSPCoopCore.add(err2);
	}
	
	
	
	
	
	
	
	
	
	

	



	/**
	 * Busta E-Gov presente nel messaggio di richiesta
	 * "420";
	 */
	protected void _testBustaEGovInviataVersoPortaDelegata() throws Exception{

		String egov=UtilitiesEGov.getIDEGov(CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE,
				CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE+CostantiTestSuite.SPCOOP_PORTA_DOMINIO);
		String bustaSOAP=UtilitiesEGov.getBustaEGov(CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_FRUITORE,
				CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE,
				CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_EROGATORE,
				CostantiTestSuite.SPCOOP_NOME_SOGGETTO_EROGATORE,
				SPCoopCostanti.PROFILO_COLLABORAZIONE_ONEWAY,
				CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY,
				CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY,
				egov,
				false,
				SPCoopCostanti.PROFILO_TRASMISSIONE_SENZA_DUPLICATI,
				false,CostantiTestSuite.SPCOOP_SERVIZIO_ONEWAY_AZIONE_FILTRO_DUPLICATI);

		// costruzione busta
		java.io.ByteArrayInputStream bin = 
			new java.io.ByteArrayInputStream(bustaSOAP.getBytes());
		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();

		DatabaseComponent dbComponentFruitore = null;
		DatabaseComponent dbComponentErogatore = null;

		try{
			_lock();
			
			ClientHttpGenerico client=new ClientHttpGenerico(new Repository());
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_PROFILO_ONEWAY);
			client.connectToSoapEngine();
			client.setMessage(msg);
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

				Reporter.log("Invocazione porta delegata con messaggio contenente gia' una busta egov non ha causato errori.");
				throw new TestSuiteException("Invocazione porta delegata con messaggio contenente gia' una busta egov non ha causato errori.");
			} catch (AxisFault error) {
				
				Assert.assertTrue(client.getCodiceStatoHTTP()==500);
				
				String codiceEccezioneAtteso = Utilities.toString(CodiceErroreIntegrazione.CODICE_420_BUSTA_PRESENTE_RICHIESTA_APPLICATIVA);
				String descrizioneEccezioneAttesa = CostantiErroriIntegrazione.MSG_420_BUSTA_PRESENTE_RICHIESTA_APPLICATIVA;
				boolean checkDescrizioneTramiteMatchEsatto = Utilities.CONTROLLO_DESCRIZIONE_TRAMITE_METODO_EQUALS;
				if(this.genericCode) {
					IntegrationFunctionError integrationFunctionError = IntegrationFunctionError.INTEROPERABILITY_PROFILE_REQUEST_ALREADY_EXISTS;
					ErroriProperties erroriProperties = ErroriProperties.getInstance(this.log); 
					codiceEccezioneAtteso = erroriProperties.getErrorType_noWrap(integrationFunctionError);
					if(erroriProperties.isForceGenericDetails_noWrap(integrationFunctionError)) {
						descrizioneEccezioneAttesa = erroriProperties.getGenericDetails_noWrap(integrationFunctionError);
						checkDescrizioneTramiteMatchEsatto = Utilities.CONTROLLO_DESCRIZIONE_TRAMITE_METODO_EQUALS;
					}
				}
				
				Utilities.verificaFaultIntegrazione(error, 
						CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE.getCodicePorta(),RicezioneContenutiApplicativi.ID_MODULO, 
						codiceEccezioneAtteso, descrizioneEccezioneAttesa, checkDescrizioneTramiteMatchEsatto);	
			}finally{
				dbComponentFruitore.close();
				dbComponentErogatore.close();
			}
		}catch(Exception e){
			throw e;
		}finally{
			dbComponentFruitore.close();
			dbComponentErogatore.close();
			
			this._unlock();
		}
	}

	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * Il messaggio di richiesta utilizzato con IM per invocare la Porta Delegata non rispetta il formato SOAP
	 * "421";
	 */
	protected void _testInvioMessaggioNonSOAPConXML() throws Exception{
		
		try{
			_lock();
			
			Reporter.log("Effettuo invocazione per riferimento");
			org.openspcoop2.pdd.services.axis14.PD_PortType im = 
				IntegrationManager.getIntegrationManagerPD_axis14(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore().replace("out","IntegrationManager/out"),
						"adminSilX", "123456");
			IntegrationManagerMessage msg = new IntegrationManagerMessage();
			msg.setMessage("CONTENUTO_ERRATO".getBytes());
			im.invocaPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_AUTENTICAZIONE_BASIC, msg);
			
			throw new Exception("Metodo invocaPortaDelegata non ha causato errori");
		}catch(IntegrationManagerException e){
			verificaSPCoopException_421(e);
		}
		finally {
			this._unlock();
		}
			
	}
	private void verificaSPCoopException_421(IntegrationManagerException e) throws Exception{
		
		ErroriProperties erroriProperties = ErroriProperties.getInstance(this.log); 
		IntegrationFunctionError integrationFunctionError = IntegrationFunctionError.UNPROCESSABLE_REQUEST_CONTENT;
		
		String codiceErrore = Utilities.toString(CodiceErroreIntegrazione.CODICE_421_MSG_SOAP_NON_PRESENTE_RICHIESTA_APPLICATIVA);
		if(this.genericCode) {
			codiceErrore = erroriProperties.getErrorType_noWrap(integrationFunctionError);
		}
		Reporter.log("Codice errore ["+e.getCodiceEccezione()+"]");
		Reporter.log("confronto con ["+codiceErrore+"]");
		Assert.assertTrue(codiceErrore.equals(e.getCodiceEccezione()));
			
		Reporter.log("ID Porta ["+e.getIdentificativoPorta()+"]");
		Reporter.log("confronto con [OpenSPCoopSPCoopIT]");
		Assert.assertTrue(Utilities.testSuiteProperties.getIdentitaDefault_dominio().equals(e.getIdentificativoPorta()));
			
		Reporter.log("ID funzione ["+e.getIdentificativoFunzione()+"]");
		Reporter.log("confronto con [IntegrationManager]");
		Assert.assertTrue("IntegrationManager".equals(e.getIdentificativoFunzione()));
			
		Reporter.log("TipoEccezione ["+e.getTipoEccezione()+"]");
		Reporter.log("confronto con [EccezioneIntegrazione]");
		Assert.assertTrue("EccezioneIntegrazione".equals(e.getTipoEccezione()));
			
		Reporter.log("MessaggioErrore ["+e.getDescrizioneEccezione()+"]");
		String detail = CostantiErroriIntegrazione.MSG_421_MSG_SOAP_NON_PRESENTE_RICHIESTA_APPLICATIVA;
		if(this.genericCode && erroriProperties.isForceGenericDetails_noWrap(integrationFunctionError)) {
			detail = erroriProperties.getGenericDetails_noWrap(integrationFunctionError);
			Reporter.log("confronto con ["+detail+"]");
			Assert.assertTrue(detail.equals(e.getDescrizioneEccezione()));
		}
		else {
			Reporter.log("confronto con ["+detail+"]");
			Assert.assertTrue(e.getDescrizioneEccezione().startsWith(detail));
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * Il messaggio di richiesta utilizzato con il tunnel SOAP e  con IM per invocare la Porta Delegata non e' imbustabile
	 * "422";
	 */
	protected void _testInvioMessaggioTramiteTunnelSOAP_nonImbustabileInSOAP() throws Exception{
		
		DatabaseComponent dbComponentFruitore = null;
		DatabaseComponent dbComponentErogatore = null;

		String portaDelegata = CostantiTestSuite.PORTA_DELEGATA_PROFILO_SINCRONO;
		
		Date dataInizioTest = null;
		Date dataFineTest = null;
		try{
			_lock();
			dataInizioTest = DateManager.getDate();
			
			ClientHttpGenerico client=new ClientHttpGenerico(new Repository());
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore().replace("out", "out/xml2soap"));
			client.setPortaDelegata(portaDelegata);
			client.connectToSoapEngine();
			client.setMessaggioXMLRichiesta("<CONTENUTO_ERRATO".getBytes());
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
				
				Reporter.log("Return Code '"+client.getCodiceStatoHTTP()+"'");
				Assert.assertTrue(client.getCodiceStatoHTTP()==400);
				
				byte [] xmlErroreApplicativo = client.getMessaggioXMLRisposta();
				Assert.assertTrue(xmlErroreApplicativo!=null);
				
				String codiceEccezioneAtteso = Utilities.toString(CodiceErroreIntegrazione.CODICE_422_IMBUSTAMENTO_SOAP_NON_RIUSCITO_RICHIESTA_APPLICATIVA);
				String descrizioneEccezioneAttesa = CostantiErroriIntegrazione.MSG_422_IMBUSTAMENTO_SOAP_NON_RIUSCITO_RICHIESTA_APPLICATIVA;
				boolean checkDescrizioneTramiteMatchEsatto = Utilities.CONTROLLO_DESCRIZIONE_TRAMITE_METODO_CONTAINS;
				if(this.genericCode) {
					IntegrationFunctionError integrationFunctionError = IntegrationFunctionError.UNPROCESSABLE_REQUEST_CONTENT;
					ErroriProperties erroriProperties = ErroriProperties.getInstance(this.log); 
					codiceEccezioneAtteso = erroriProperties.getErrorType_noWrap(integrationFunctionError);
					if(erroriProperties.isForceGenericDetails_noWrap(integrationFunctionError)) {
						descrizioneEccezioneAttesa = erroriProperties.getGenericDetails_noWrap(integrationFunctionError);
						checkDescrizioneTramiteMatchEsatto = Utilities.CONTROLLO_DESCRIZIONE_TRAMITE_METODO_EQUALS;
					}
				}
				
				Utilities.verificaErroreApplicativoCnipa(org.openspcoop2.message.xml.MessageXMLUtils.DEFAULT.newElement(xmlErroreApplicativo),
						CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE.getCodicePorta(),RicezioneContenutiApplicativiHTTPtoSOAPConnector.ID_MODULO, 
						codiceEccezioneAtteso, descrizioneEccezioneAttesa, checkDescrizioneTramiteMatchEsatto);	
				
			}finally{
				dbComponentFruitore.close();
				dbComponentErogatore.close();
			}
		}catch(Exception e){
			throw e;
		}finally{
			dbComponentFruitore.close();
			dbComponentErogatore.close();

			this._unlock();
			dataFineTest = DateManager.getDate();
		}
		
		ErroreAttesoOpenSPCoopLogCore err = new ErroreAttesoOpenSPCoopLogCore();
		err.setIntervalloInferiore(dataInizioTest);
		err.setIntervalloSuperiore(dataFineTest);
		err.setMsgErrore("ImbustamentoSOAP");
		this.erroriAttesiOpenSPCoopCore.add(err);
		
		ErroreAttesoOpenSPCoopLogCore err2 = new ErroreAttesoOpenSPCoopLogCore();
		err2.setIntervalloInferiore(dataInizioTest);
		err2.setIntervalloSuperiore(dataFineTest);
		err2.setMsgErrore("ImbustamentoSoap non riuscito");
		this.erroriAttesiOpenSPCoopCore.add(err2);
		
		ErroreAttesoOpenSPCoopLogCore err3 = new ErroreAttesoOpenSPCoopLogCore();
		err3.setIntervalloInferiore(dataInizioTest);
		err3.setIntervalloSuperiore(dataFineTest);
		err3.setMsgErrore("Imbustamento messaggio in un messaggio SOAP con errore");
		this.erroriAttesiOpenSPCoopCore.add(err3);
	}
	
	protected void _testInvioMessaggioTramiteIM_nonImbustabileInSOAP() throws Exception{
		
		try{
			_lock();
			
			Reporter.log("Effettuo invocazione per riferimento");
			org.openspcoop2.pdd.services.axis14.PD_PortType im = 
				IntegrationManager.getIntegrationManagerPD_axis14(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore().replace("out","IntegrationManager/out"),
						"adminSilX", "123456");
			IntegrationManagerMessage msg = new IntegrationManagerMessage();
			msg.setImbustamento(true);
			msg.setMessage("<CONTENUTO_ERRATO".getBytes());
			im.invocaPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_AUTENTICAZIONE_BASIC, msg);
			
			throw new Exception("Metodo imbustaMessaggio non ha causato errori");
		}catch(IntegrationManagerException e){
			verificaSPCoopException_422(e);
		}
		finally{
			this._unlock();
		}
			
	}
	private void verificaSPCoopException_422(IntegrationManagerException e) throws Exception{
		
		ErroriProperties erroriProperties = ErroriProperties.getInstance(this.log); 
		IntegrationFunctionError integrationFunctionError = IntegrationFunctionError.UNPROCESSABLE_REQUEST_CONTENT;
		
		String codiceErrore = Utilities.toString(CodiceErroreIntegrazione.CODICE_422_IMBUSTAMENTO_SOAP_NON_RIUSCITO_RICHIESTA_APPLICATIVA);
		if(this.genericCode) {
			codiceErrore = erroriProperties.getErrorType_noWrap(integrationFunctionError);
		}
		Reporter.log("Codice errore ["+e.getCodiceEccezione()+"]");
		Reporter.log("confronto con ["+codiceErrore+"]");
		Assert.assertTrue(codiceErrore.equals(e.getCodiceEccezione()));
			
		Reporter.log("ID Porta ["+e.getIdentificativoPorta()+"]");
		Reporter.log("confronto con [OpenSPCoopSPCoopIT]");
		Assert.assertTrue(Utilities.testSuiteProperties.getIdentitaDefault_dominio().equals(e.getIdentificativoPorta()));
			
		Reporter.log("ID funzione ["+e.getIdentificativoFunzione()+"]");
		Reporter.log("confronto con [IntegrationManager]");
		Assert.assertTrue("IntegrationManager".equals(e.getIdentificativoFunzione()));
			
		Reporter.log("TipoEccezione ["+e.getTipoEccezione()+"]");
		Reporter.log("confronto con [EccezioneIntegrazione]");
		Assert.assertTrue("EccezioneIntegrazione".equals(e.getTipoEccezione()));
			
		Reporter.log("MessaggioErrore ["+e.getDescrizioneEccezione()+"]");
		String detail = CostantiErroriIntegrazione.MSG_422_IMBUSTAMENTO_SOAP_NON_RIUSCITO_RICHIESTA_APPLICATIVA;
		if(this.genericCode && erroriProperties.isForceGenericDetails_noWrap(integrationFunctionError)) {
			detail = erroriProperties.getGenericDetails_noWrap(integrationFunctionError);
			Reporter.log("confronto con ["+detail+"]");
			Assert.assertTrue(detail.equals(e.getDescrizioneEccezione()));
		}
		else {
			Reporter.log("confronto con ["+detail+"]");
			Assert.assertTrue(e.getDescrizioneEccezione().startsWith(detail));
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * Servizio SPCoop invocato con azione non corretta
	 * "423";
	 */
	protected void _testInvocazioneServizioSenzaAzione() throws Exception{
		DatabaseComponent dbComponentFruitore = null;
		DatabaseComponent dbComponentErogatore = null;

		try{
			_lock();
			
			ClientHttpGenerico client=new ClientHttpGenerico(new Repository());
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_SERVIZIO_NON_INVOCABILE_SENZA_AZIONE);
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

				Reporter.log("Invocazione porta delegata (423) (PortaDelegata: "+CostantiTestSuite.PORTA_DELEGATA_SERVIZIO_NON_INVOCABILE_SENZA_AZIONE+") non ha causato errori.");
				throw new TestSuiteException("Invocazione porta delegata (423) (PortaDelegata: "+CostantiTestSuite.PORTA_DELEGATA_SERVIZIO_NON_INVOCABILE_SENZA_AZIONE+") non ha causato errori.");
			} catch (AxisFault error) {
				
				Assert.assertTrue(client.getCodiceStatoHTTP()==500);
				
				String codiceEccezioneAtteso = Utilities.toString(CodiceErroreIntegrazione.CODICE_423_SERVIZIO_CON_AZIONE_SCORRETTA);
				String descrizioneEccezioneAttesa = CostantiErroriIntegrazione.MSG_423_SERVIZIO_CON_AZIONE_SCORRETTA.replace(CostantiErroriIntegrazione.MSG_423_SERVIZIO_CON_AZIONE_NON_CORRETTA_API_TEMPLATE, "ASRichiestaStatoAvanzamentoAsincronoSimmetrico:1"); 
				boolean checkDescrizioneTramiteMatchEsatto = Utilities.CONTROLLO_DESCRIZIONE_TRAMITE_METODO_EQUALS;
				if(this.genericCode) {
					IntegrationFunctionError integrationFunctionError = IntegrationFunctionError.OPERATION_UNDEFINED;
					ErroriProperties erroriProperties = ErroriProperties.getInstance(this.log); 
					codiceEccezioneAtteso = erroriProperties.getErrorType_noWrap(integrationFunctionError);
					if(erroriProperties.isForceGenericDetails_noWrap(integrationFunctionError)) {
						descrizioneEccezioneAttesa = erroriProperties.getGenericDetails_noWrap(integrationFunctionError);
						checkDescrizioneTramiteMatchEsatto = Utilities.CONTROLLO_DESCRIZIONE_TRAMITE_METODO_EQUALS;
					}
				}
				
				Utilities.verificaFaultIntegrazione(error, 
						CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE.getCodicePorta(),RicezioneContenutiApplicativi.ID_MODULO, 
						codiceEccezioneAtteso, descrizioneEccezioneAttesa, checkDescrizioneTramiteMatchEsatto);	
			}finally{
				dbComponentFruitore.close();
				dbComponentErogatore.close();
			}
		}catch(Exception e){
			throw e;
		}finally{
			dbComponentFruitore.close();
			dbComponentErogatore.close();
			
			this._unlock();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * Funzione "Allega Body" non riuscita sul messaggio di richiesta
	 * "424";
	 */
	protected void _testAllegaBodyNonRiuscito() throws Exception{
		DatabaseComponent dbComponentFruitore = null;
		DatabaseComponent dbComponentErogatore = null;

		try{
			_lock();
			
			ClientHttpGenerico client=new ClientHttpGenerico(new Repository());
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_PROFILO_SINCRONO_TUNNEL_SOAP_ALLEGA_BODY);
			client.connectToSoapEngine();
			client.setMessageWithAttachmentsFromFile(Utilities.testSuiteProperties.getSoap11WithAttachmentsFileName(), false);
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

				Reporter.log("Invocazione porta delegata (424) (PortaDelegata: "+CostantiTestSuite.PORTA_DELEGATA_PROFILO_SINCRONO_TUNNEL_SOAP_ALLEGA_BODY+") non ha causato errori.");
				throw new TestSuiteException("Invocazione porta delegata (424) (PortaDelegata: "+CostantiTestSuite.PORTA_DELEGATA_PROFILO_SINCRONO_TUNNEL_SOAP_ALLEGA_BODY+") non ha causato errori.");
			} catch (AxisFault error) {
				
				Assert.assertTrue(client.getCodiceStatoHTTP()==500);
				
				String codiceEccezioneAtteso = Utilities.toString(CodiceErroreIntegrazione.CODICE_424_ALLEGA_BODY);
				String descrizioneEccezioneAttesa = CostantiErroriIntegrazione.MSG_424_ALLEGA_BODY; 
				boolean checkDescrizioneTramiteMatchEsatto = Utilities.CONTROLLO_DESCRIZIONE_TRAMITE_METODO_CONTAINS;
				if(this.genericCode) {
					IntegrationFunctionError integrationFunctionError = IntegrationFunctionError.BAD_REQUEST;
					ErroriProperties erroriProperties = ErroriProperties.getInstance(this.log); 
					codiceEccezioneAtteso = erroriProperties.getErrorType_noWrap(integrationFunctionError);
					if(erroriProperties.isForceGenericDetails_noWrap(integrationFunctionError)) {
						descrizioneEccezioneAttesa = erroriProperties.getGenericDetails_noWrap(integrationFunctionError);
						checkDescrizioneTramiteMatchEsatto = Utilities.CONTROLLO_DESCRIZIONE_TRAMITE_METODO_EQUALS;
					}
				}
				
				Utilities.verificaFaultIntegrazione(error, 
						CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE.getCodicePorta(),RicezioneContenutiApplicativi.ID_MODULO, 
						codiceEccezioneAtteso, descrizioneEccezioneAttesa, checkDescrizioneTramiteMatchEsatto);	
			}finally{
				dbComponentFruitore.close();
				dbComponentErogatore.close();
			}
		}catch(Exception e){
			throw e;
		}finally{
			dbComponentFruitore.close();
			dbComponentErogatore.close();
			
			this._unlock();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * Funzione "Scarta Body" non riuscita sul messaggio di richiesta
	 * "425";
	 */
	protected void _testScartaBodyNonRiuscito() throws Exception{
		DatabaseComponent dbComponentFruitore = null;
		DatabaseComponent dbComponentErogatore = null;

		try{
			_lock();
			
			ClientHttpGenerico client=new ClientHttpGenerico(new Repository());
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_PROFILO_SINCRONO_TUNNEL_SOAP_SCARTA_BODY);
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

				Reporter.log("Invocazione porta delegata (425) (PortaDelegata: "+CostantiTestSuite.PORTA_DELEGATA_PROFILO_SINCRONO_TUNNEL_SOAP_SCARTA_BODY+") non ha causato errori.");
				throw new TestSuiteException("Invocazione porta delegata (425) (PortaDelegata: "+CostantiTestSuite.PORTA_DELEGATA_PROFILO_SINCRONO_TUNNEL_SOAP_SCARTA_BODY+") non ha causato errori.");
			} catch (AxisFault error) {
				
				Assert.assertTrue(client.getCodiceStatoHTTP()==500);
				
				String codiceEccezioneAtteso = Utilities.toString(CodiceErroreIntegrazione.CODICE_425_SCARTA_BODY);
				String descrizioneEccezioneAttesa = CostantiErroriIntegrazione.MSG_425_SCARTA_BODY; 
				boolean checkDescrizioneTramiteMatchEsatto = Utilities.CONTROLLO_DESCRIZIONE_TRAMITE_METODO_CONTAINS;
				if(this.genericCode) {
					IntegrationFunctionError integrationFunctionError = IntegrationFunctionError.BAD_REQUEST;
					ErroriProperties erroriProperties = ErroriProperties.getInstance(this.log); 
					codiceEccezioneAtteso = erroriProperties.getErrorType_noWrap(integrationFunctionError);
					if(erroriProperties.isForceGenericDetails_noWrap(integrationFunctionError)) {
						descrizioneEccezioneAttesa = erroriProperties.getGenericDetails_noWrap(integrationFunctionError);
						checkDescrizioneTramiteMatchEsatto = Utilities.CONTROLLO_DESCRIZIONE_TRAMITE_METODO_EQUALS;
					}
				}
				
				Utilities.verificaFaultIntegrazione(error, 
						CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE.getCodicePorta(),RicezioneContenutiApplicativi.ID_MODULO, 
						codiceEccezioneAtteso, descrizioneEccezioneAttesa, checkDescrizioneTramiteMatchEsatto);	
			}finally{
				dbComponentFruitore.close();
				dbComponentErogatore.close();
			}
		}catch(Exception e){
			throw e;
		}finally{
			dbComponentFruitore.close();
			dbComponentErogatore.close();
			
			this._unlock();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * Errore di processamento SOAP del messaggio di richiesta 
	 * "426";
	 */
	protected void _testSoapEngineFallito_errore_processamento() throws Exception{
		
		DatabaseComponent dbComponentFruitore = null;
		DatabaseComponent dbComponentErogatore = null;

		Date dataInizioTest = null;
		Date dataFineTest = null;
		try{
			_lock();
			dataInizioTest = DateManager.getDate();
			
			ClientHttpGenerico client=new ClientHttpGenerico(new Repository());
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_PROFILO_SINCRONO);
			client.connectToSoapEngine();
			// NOTA: invio un messaggio con SOAPAction malformata 
			client.setMessageFromFile(Utilities.testSuiteProperties.getSoap11FileName(), false);
			client.setSoapAction("\"soapAction");
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

				Reporter.log("Invocazione porta delegata (426) (PortaDelegata: "+CostantiTestSuite.PORTA_DELEGATA_PROFILO_SINCRONO+") non ha causato errori.");
				throw new TestSuiteException("Invocazione porta delegata (426) (PortaDelegata: "+CostantiTestSuite.PORTA_DELEGATA_PROFILO_SINCRONO+") non ha causato errori.");
			} catch (AxisFault error) {
				
				Assert.assertTrue(client.getCodiceStatoHTTP()==500);
				
				String codiceEccezioneAtteso = Utilities.toString(CodiceErroreIntegrazione.CODICE_426_SERVLET_ERROR);
				String descrizioneEccezioneAttesa = CostantiErroriIntegrazione.MSG_426_SERVLET_REQUEST_ERROR; 
				boolean checkDescrizioneTramiteMatchEsatto = Utilities.CONTROLLO_DESCRIZIONE_TRAMITE_METODO_CONTAINS;
				if(this.genericCode) {
					IntegrationFunctionError integrationFunctionError = IntegrationFunctionError.BAD_REQUEST;
					ErroriProperties erroriProperties = ErroriProperties.getInstance(this.log); 
					codiceEccezioneAtteso = erroriProperties.getErrorType_noWrap(integrationFunctionError);
					if(erroriProperties.isForceGenericDetails_noWrap(integrationFunctionError)) {
						descrizioneEccezioneAttesa = erroriProperties.getGenericDetails_noWrap(integrationFunctionError);
						checkDescrizioneTramiteMatchEsatto = Utilities.CONTROLLO_DESCRIZIONE_TRAMITE_METODO_EQUALS;
					}
				}
				
				Utilities.verificaFaultIntegrazione(error, 
						CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE.getCodicePorta(),RicezioneContenutiApplicativi.ID_MODULO, 
						codiceEccezioneAtteso, descrizioneEccezioneAttesa, checkDescrizioneTramiteMatchEsatto);	
			}finally{
				dbComponentFruitore.close();
				dbComponentErogatore.close();
			}
		}catch(Exception e){
			throw e;
		}finally{
			dbComponentFruitore.close();
			dbComponentErogatore.close();

			this._unlock();
			dataFineTest = DateManager.getDate();
		}
		
		ErroreAttesoOpenSPCoopLogCore err = new ErroreAttesoOpenSPCoopLogCore();
		err.setIntervalloInferiore(dataInizioTest);
		err.setIntervalloSuperiore(dataFineTest);
		err.setMsgErrore("ErroreGenerale");
		this.erroriAttesiOpenSPCoopCore.add(err);
		
		ErroreAttesoOpenSPCoopLogCore err2 = new ErroreAttesoOpenSPCoopLogCore();
		err2.setIntervalloInferiore(dataInizioTest);
		err2.setIntervalloSuperiore(dataFineTest);
		err2.setMsgErrore("Generale(richiesta): Content is not allowed in prolog.");
		this.erroriAttesiOpenSPCoopCore.add(err2);
		
		ErroreAttesoOpenSPCoopLogCore err3 = new ErroreAttesoOpenSPCoopLogCore();
		err3.setIntervalloInferiore(dataInizioTest);
		err3.setIntervalloSuperiore(dataFineTest);
		err3.setMsgErrore("Header http 'SOAPAction' non valorizzato correttamente (action quotata? Non è stato trovato il carattere di chiusura \" ma è presente quello di apertura)");
		this.erroriAttesiOpenSPCoopCore.add(err3);
		
		// Altro framework
		
		ErroreAttesoOpenSPCoopLogCore err4 = new ErroreAttesoOpenSPCoopLogCore();
		err4.setIntervalloInferiore(dataInizioTest);
		err4.setIntervalloSuperiore(dataFineTest);
		err4.setMsgErrore("(WSI-BP-1.1 R1109)");
		this.erroriAttesiOpenSPCoopCore.add(err4);
	}
	
	
	
	
	
	
	
	
	
	
	/**
	 * Impossibile processare header SOAP in messaggio con opzione mustUnderstand
	 * "427";
	 */
	protected void _testMustUnderstad() throws Exception{
		DatabaseComponent dbComponentFruitore = null;
		DatabaseComponent dbComponentErogatore = null;

		try{
			_lock();
			
			ClientHttpGenerico client=new ClientHttpGenerico(new Repository());
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_PROFILO_SINCRONO);
			client.connectToSoapEngine();
			client.setMessageFromFile(Utilities.testSuiteProperties.getSoapWithHeaderMustUnderstandUnknonw(), false);
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

				Reporter.log("Invocazione porta delegata (427) (PortaDelegata: "+CostantiTestSuite.PORTA_DELEGATA_PROFILO_SINCRONO+") non ha causato errori.");
				throw new TestSuiteException("Invocazione porta delegata (427) (PortaDelegata: "+CostantiTestSuite.PORTA_DELEGATA_PROFILO_SINCRONO+") non ha causato errori.");
			} catch (AxisFault error) {
				
				Assert.assertTrue(client.getCodiceStatoHTTP()==500);
				
				String codiceEccezioneAtteso = Utilities.toString(CodiceErroreIntegrazione.CODICE_427_MUSTUNDERSTAND_ERROR);
				String descrizioneEccezioneAttesa = CostantiErroriIntegrazione.MSG_427_MUSTUNDERSTAND_ERROR; 
				boolean checkDescrizioneTramiteMatchEsatto = Utilities.CONTROLLO_DESCRIZIONE_TRAMITE_METODO_CONTAINS;
				if(this.genericCode) {
					IntegrationFunctionError integrationFunctionError = IntegrationFunctionError.SOAP_MUST_UNDERSTAND_UNKNOWN;
					ErroriProperties erroriProperties = ErroriProperties.getInstance(this.log); 
					codiceEccezioneAtteso = erroriProperties.getErrorType_noWrap(integrationFunctionError);
					if(erroriProperties.isForceGenericDetails_noWrap(integrationFunctionError)) {
						descrizioneEccezioneAttesa = erroriProperties.getGenericDetails_noWrap(integrationFunctionError);
						checkDescrizioneTramiteMatchEsatto = Utilities.CONTROLLO_DESCRIZIONE_TRAMITE_METODO_EQUALS;
					}
				}
				
				Utilities.verificaFaultIntegrazione(error, 
						CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE.getCodicePorta(),RicezioneContenutiApplicativi.ID_MODULO, 
						codiceEccezioneAtteso, descrizioneEccezioneAttesa, checkDescrizioneTramiteMatchEsatto);	
			}finally{
				dbComponentFruitore.close();
				dbComponentErogatore.close();
			}
		}catch(Exception e){
			throw e;
		}finally{
			dbComponentFruitore.close();
			dbComponentErogatore.close();
			
			this._unlock();
		}
	}
	
	
	
	
	
	
	
	
	
	
	/**
	 * Autorizzazione basata sul contenuto fallita
	 * "428";
	 */
	protected void _testAutorizzazioneContenutoKO() throws Exception{
		DatabaseComponent dbComponentFruitore = null;
		DatabaseComponent dbComponentErogatore = null;

		try{
			_lock();
			
			ClientHttpGenerico client=new ClientHttpGenerico(new Repository());
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_AUTORIZZAZIONE_CONTENUTO_SINCRONO_KO);
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

				Reporter.log("Invocazione porta delegata (428) (PortaDelegata: "+CostantiTestSuite.PORTA_DELEGATA_AUTORIZZAZIONE_CONTENUTO_SINCRONO_KO+") non ha causato errori.");
				throw new TestSuiteException("Invocazione porta delegata (428) (PortaDelegata: "+CostantiTestSuite.PORTA_DELEGATA_AUTORIZZAZIONE_CONTENUTO_SINCRONO_KO+") non ha causato errori.");
			} catch (AxisFault error) {
				
				Assert.assertTrue(client.getCodiceStatoHTTP()==500);
				
				String codiceEccezioneAtteso = Utilities.toString(CodiceErroreIntegrazione.CODICE_428_AUTORIZZAZIONE_CONTENUTO_FALLITA);
				String descrizioneEccezioneAttesa = CostantiErroriIntegrazione.MSG_428_AUTORIZZAZIONE_CONTENUTO_FALLITA; 
				boolean checkDescrizioneTramiteMatchEsatto = Utilities.CONTROLLO_DESCRIZIONE_TRAMITE_METODO_CONTAINS;
				if(this.genericCode) {
					IntegrationFunctionError integrationFunctionError = IntegrationFunctionError.CONTENT_AUTHORIZATION_DENY;
					ErroriProperties erroriProperties = ErroriProperties.getInstance(this.log); 
					codiceEccezioneAtteso = erroriProperties.getErrorType_noWrap(integrationFunctionError);
					if(erroriProperties.isForceGenericDetails_noWrap(integrationFunctionError)) {
						descrizioneEccezioneAttesa = erroriProperties.getGenericDetails_noWrap(integrationFunctionError);
						checkDescrizioneTramiteMatchEsatto = Utilities.CONTROLLO_DESCRIZIONE_TRAMITE_METODO_EQUALS;
					}
				}
				
				Utilities.verificaFaultIntegrazione(error, 
						CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE.getCodicePorta(),RicezioneContenutiApplicativi.ID_MODULO, 
						codiceEccezioneAtteso, descrizioneEccezioneAttesa, checkDescrizioneTramiteMatchEsatto);	
			}finally{
				dbComponentFruitore.close();
				dbComponentErogatore.close();
			}
		}catch(Exception e){
			throw e;
		}finally{
			dbComponentFruitore.close();
			dbComponentErogatore.close();
			
			this._unlock();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * L'header HTTP riporta un Content-Type non previsto in SOAP 1.1
	 * "429";
	 */
	protected void _testContentTypeErrato() throws Exception{
		DatabaseComponent dbComponentFruitore = null;
		DatabaseComponent dbComponentErogatore = null;

		try{
			_lock();
			
			ClientHttpGenerico client=new ClientHttpGenerico(new Repository());
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_PROFILO_SINCRONO);
			client.setContentType("application/soap+xml"); // ContentType per messaggi SOAP 1.2
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

				Reporter.log("Invocazione porta delegata (429) (PortaDelegata: "+CostantiTestSuite.PORTA_DELEGATA_PROFILO_SINCRONO+") non ha causato errori.");
				throw new TestSuiteException("Invocazione porta delegata (429) (PortaDelegata: "+CostantiTestSuite.PORTA_DELEGATA_PROFILO_SINCRONO+") non ha causato errori.");
			} catch (AxisFault error) {
				
				Assert.assertTrue(client.getCodiceStatoHTTP()==500);
				
				String codiceEccezioneAtteso = Utilities.toString(CodiceErroreIntegrazione.CODICE_429_CONTENT_TYPE_NON_SUPPORTATO);
				String descrizioneEccezioneAttesa = CostantiErroriIntegrazione.MSG_429_CONTENT_TYPE_NON_SUPPORTATO.replace(CostantiErroriIntegrazione.MSG_429_CONTENT_TYPE_KEY, "application/soap+xml");
				boolean checkDescrizioneTramiteMatchEsatto = Utilities.CONTROLLO_DESCRIZIONE_TRAMITE_METODO_EQUALS;
				if(this.genericCode) {
					IntegrationFunctionError integrationFunctionError = IntegrationFunctionError.CONTENT_TYPE_NOT_SUPPORTED;
					ErroriProperties erroriProperties = ErroriProperties.getInstance(this.log); 
					codiceEccezioneAtteso = erroriProperties.getErrorType_noWrap(integrationFunctionError);
					if(erroriProperties.isForceGenericDetails_noWrap(integrationFunctionError)) {
						descrizioneEccezioneAttesa = erroriProperties.getGenericDetails_noWrap(integrationFunctionError);
						checkDescrizioneTramiteMatchEsatto = Utilities.CONTROLLO_DESCRIZIONE_TRAMITE_METODO_EQUALS;
					}
				}
				
				Utilities.verificaFaultIntegrazione(error, 
						CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE.getCodicePorta(),RicezioneContenutiApplicativi.ID_MODULO, 
						codiceEccezioneAtteso, descrizioneEccezioneAttesa, checkDescrizioneTramiteMatchEsatto);	
			}finally{
				dbComponentFruitore.close();
				dbComponentErogatore.close();
			}
		}catch(Exception e){
			throw e;
		}finally{
			dbComponentFruitore.close();
			dbComponentErogatore.close();
			
			this._unlock();
		}
	}
	
	
	
	
	
	
	
	
	
	/**
	 * Envelope con Namespace non previsto in SOAP 1.1
	 * "430";
	 */
	protected void _testNamespaceEnvelopeErrato() throws Exception{
		
		DatabaseComponent dbComponentFruitore = null;
		DatabaseComponent dbComponentErogatore = null;

		Date dataInizioTest = null;
		Date dataFineTest = null;
		try{
			_lock();
			dataInizioTest = DateManager.getDate();
			
			String xml = org.openspcoop2.utils.resources.FileSystemUtilities.readFile(Utilities.testSuiteProperties.getSoap11FileName());
			xml = xml.replace("http://schemas.xmlsoap.org/soap/envelope/", "http://www.w3.org/2003/05/soap-envelope"); // imposto namespace di SOAP 1.2
			Message msg=new Message(new ByteArrayInputStream(xml.getBytes()));
			msg.getSOAPPartAsBytes();
			
			
			ClientHttpGenerico client=new ClientHttpGenerico(new Repository());
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_PROFILO_SINCRONO);
			client.connectToSoapEngine();
			client.setMessage(msg);
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

				Reporter.log("Invocazione porta delegata (430) (PortaDelegata: "+CostantiTestSuite.PORTA_DELEGATA_PROFILO_SINCRONO+") non ha causato errori.");
				throw new TestSuiteException("Invocazione porta delegata (430) (PortaDelegata: "+CostantiTestSuite.PORTA_DELEGATA_PROFILO_SINCRONO+") non ha causato errori.");
			} catch (AxisFault error) {
				
				Assert.assertTrue(client.getCodiceStatoHTTP()==500);
				
				try{
					Reporter.log("Verifica con [http://www.w3.org/2003/05/soap-envelope] ...");
					
					String codiceEccezioneAtteso = Utilities.toString(CodiceErroreIntegrazione.CODICE_430_SOAP_ENVELOPE_NAMESPACE_ERROR);
					String descrizioneEccezioneAttesa = CostantiErroriIntegrazione.MSG_430_SOAP_ENVELOPE_NAMESPACE_ERROR.replace(CostantiErroriIntegrazione.MSG_430_NAMESPACE_KEY, "http://www.w3.org/2003/05/soap-envelope");
					boolean checkDescrizioneTramiteMatchEsatto = Utilities.CONTROLLO_DESCRIZIONE_TRAMITE_METODO_EQUALS;
					if(this.genericCode) {
						IntegrationFunctionError integrationFunctionError = IntegrationFunctionError.SOAP_VERSION_MISMATCH;
						ErroriProperties erroriProperties = ErroriProperties.getInstance(this.log); 
						codiceEccezioneAtteso = erroriProperties.getErrorType_noWrap(integrationFunctionError);
						if(erroriProperties.isForceGenericDetails_noWrap(integrationFunctionError)) {
							descrizioneEccezioneAttesa = erroriProperties.getGenericDetails_noWrap(integrationFunctionError);
							checkDescrizioneTramiteMatchEsatto = Utilities.CONTROLLO_DESCRIZIONE_TRAMITE_METODO_EQUALS;
						}
					}
					
					Utilities.verificaFaultIntegrazione(error, 
							CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE.getCodicePorta(),RicezioneContenutiApplicativi.ID_MODULO, 
							codiceEccezioneAtteso, descrizioneEccezioneAttesa, checkDescrizioneTramiteMatchEsatto);	
				}catch(Throwable e){
					// Provo a vedere se sono in un caso dove il framework non e' riuscito a comprendere il namespace del soap envelope
					Reporter.log("Verifica con [http://www.w3.org/2003/05/soap-envelope] fallita: "+e.getMessage());
					Reporter.log("Verifica con [Impossibile recuperare il valore del namespace] ...");
					
					String codiceEccezioneAtteso = Utilities.toString(CodiceErroreIntegrazione.CODICE_430_SOAP_ENVELOPE_NAMESPACE_ERROR);
					String descrizioneEccezioneAttesa = CostantiErroriIntegrazione.MSG_430_SOAP_ENVELOPE_NAMESPACE_ERROR.replace(CostantiErroriIntegrazione.MSG_430_NAMESPACE_KEY, "Impossibile recuperare il valore del namespace");
					boolean checkDescrizioneTramiteMatchEsatto = Utilities.CONTROLLO_DESCRIZIONE_TRAMITE_METODO_EQUALS;
					if(this.genericCode) {
						IntegrationFunctionError integrationFunctionError = IntegrationFunctionError.SOAP_VERSION_MISMATCH;
						ErroriProperties erroriProperties = ErroriProperties.getInstance(this.log); 
						codiceEccezioneAtteso = erroriProperties.getErrorType_noWrap(integrationFunctionError);
						if(erroriProperties.isForceGenericDetails_noWrap(integrationFunctionError)) {
							descrizioneEccezioneAttesa = erroriProperties.getGenericDetails_noWrap(integrationFunctionError);
							checkDescrizioneTramiteMatchEsatto = Utilities.CONTROLLO_DESCRIZIONE_TRAMITE_METODO_EQUALS;
						}
					}
					
					Utilities.verificaFaultIntegrazione(error, 
							CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE.getCodicePorta(),RicezioneContenutiApplicativi.ID_MODULO, 
							codiceEccezioneAtteso, descrizioneEccezioneAttesa, checkDescrizioneTramiteMatchEsatto);	
				}
			}finally{
				dbComponentFruitore.close();
				dbComponentErogatore.close();
			}
		}catch(Exception e){
			throw e;
		}finally{
			dbComponentFruitore.close();
			dbComponentErogatore.close();

			this._unlock();
			dataFineTest = DateManager.getDate();
		}
		
		
		// Altro framework
		
		ErroreAttesoOpenSPCoopLogCore err = new ErroreAttesoOpenSPCoopLogCore();
		err.setIntervalloInferiore(dataInizioTest);
		err.setIntervalloSuperiore(dataFineTest);
		err.setMsgErrore("ErroreGenerale");
		this.erroriAttesiOpenSPCoopCore.add(err);
		
		ErroreAttesoOpenSPCoopLogCore err2 = new ErroreAttesoOpenSPCoopLogCore();
		err2.setIntervalloInferiore(dataInizioTest);
		err2.setIntervalloSuperiore(dataFineTest);
		err2.setMsgErrore("Generale(richiesta): Transport level information does not match with SOAP Message namespace URI");
		this.erroriAttesiOpenSPCoopCore.add(err2);

	}
	
	
	
	
	
	
	
	
	
	
	/**
	 * Test Errore Configurazione del GEstoreCredenziali
	 * "431";
	 **/
	Repository repositoryLetturaCredenzialeERRORE_CONFIGURAZIONE_PD=new Repository();
	Date dataLetturaCredenzialeERRORE_CONFIGURAZIONE_PD = null;
	protected void _testLetturaCredenzialeERRORE_CONFIGURAZIONE_PD() throws TestSuiteException, Exception{

		this.dataLetturaCredenzialeERRORE_CONFIGURAZIONE_PD = new Date();
		
		java.io.FileInputStream fin = null;
		DatabaseComponent dbComponentFruitore = null;
		Date dataInizioTest = null;
		Date dataFineTest = null;
		try{
			_lock();
			dataInizioTest = DateManager.getDate();
			
			fin = new java.io.FileInputStream(new File(Utilities.testSuiteProperties.getSoap11FileName()));

			Message msg=new Message(fin);
			msg.getSOAPPartAsBytes();
		
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryLetturaCredenzialeERRORE_CONFIGURAZIONE_PD);
			client.setSoapAction("\"TEST\"");
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_AUTENTICAZIONE_BASIC);
			client.connectToSoapEngine();
			client.setAutenticazione("adminSilX", "123456");
			client.setProperty(GestoreCredenzialiTest.TEST_CREDENZIALI_SIMULAZIONE_ERRORE_CONFIGURAZIONE, "errore");
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
				
				Assert.assertTrue(client.getCodiceStatoHTTP()==500);
				
				String codiceEccezioneAtteso = Utilities.toString(CodiceErroreIntegrazione.CODICE_431_GESTORE_CREDENZIALI_ERROR);
				String descrizioneEccezioneAttesa = CostantiErroriIntegrazione.MSG_431_GESTORE_CREDENZIALI_ERROR.replace(CostantiErroriIntegrazione.MSG_431_TIPO_GESTORE_CREDENZIALI_KEY, "testOpenSPCoop2");
				boolean checkDescrizioneTramiteMatchEsatto = Utilities.CONTROLLO_DESCRIZIONE_TRAMITE_METODO_CONTAINS;
				if(this.genericCode) {
					IntegrationFunctionError integrationFunctionError = IntegrationFunctionError.PROXY_AUTHENTICATION_INVALID_CREDENTIALS;
					ErroriProperties erroriProperties = ErroriProperties.getInstance(this.log); 
					codiceEccezioneAtteso = erroriProperties.getErrorType_noWrap(integrationFunctionError);
					if(erroriProperties.isForceGenericDetails_noWrap(integrationFunctionError)) {
						descrizioneEccezioneAttesa = erroriProperties.getGenericDetails_noWrap(integrationFunctionError);
						checkDescrizioneTramiteMatchEsatto = Utilities.CONTROLLO_DESCRIZIONE_TRAMITE_METODO_EQUALS;
					}
				}
				
				Utilities.verificaFaultIntegrazione(error, 
						CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE.getCodicePorta(),RicezioneContenutiApplicativi.ID_MODULO, 
						codiceEccezioneAtteso, descrizioneEccezioneAttesa, checkDescrizioneTramiteMatchEsatto);	
				
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

			this._unlock();
			dataFineTest = DateManager.getDate();
		}
		
		ErroreAttesoOpenSPCoopLogCore err = new ErroreAttesoOpenSPCoopLogCore();
		err.setIntervalloInferiore(dataInizioTest);
		err.setIntervalloSuperiore(dataFineTest);
		err.setMsgErrore("Errore durante l'identificazione delle credenziali [testOpenSPCoop2]");
		this.erroriAttesiOpenSPCoopCore.add(err);
		
		ErroreAttesoOpenSPCoopLogCore err2 = new ErroreAttesoOpenSPCoopLogCore();
		err2.setIntervalloInferiore(dataInizioTest);
		err2.setIntervalloSuperiore(dataFineTest);
		err2.setMsgErrore("Eccezione generale richiesta dalla testsuite");
		this.erroriAttesiOpenSPCoopCore.add(err2);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * Errore di processamento SOAP del messaggio di richiesta 
	 * "432";
	 */
	protected void _testSoapEngineFallito_ricostruzioneMessaggioNonRiuscito() throws Exception{
		
		DatabaseComponent dbComponentFruitore = null;
		DatabaseComponent dbComponentErogatore = null;

		Date dataInizioTest = null;
		Date dataFineTest = null;
		try{
			_lock();
			dataInizioTest = DateManager.getDate();
			
			ClientHttpGenerico client=new ClientHttpGenerico(new Repository());
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_PROFILO_SINCRONO);
			client.connectToSoapEngine();
			// NOTA: invio un messaggio malformato forzando il test in modo da mangare un soap with attachments come fosse un messaggio senza attachments
			client.setMessageFromFile(Utilities.testSuiteProperties.getSoap11WithAttachmentsFileName(), false);
			//client.setMessaggioXMLRichiesta("CONTENTUO_ERRATO".getBytes()); QUESTO non rilancia il soap fault
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

				Reporter.log("Invocazione porta delegata (432) (PortaDelegata: "+CostantiTestSuite.PORTA_DELEGATA_PROFILO_SINCRONO+") non ha causato errori.");
				throw new TestSuiteException("Invocazione porta delegata (432) (PortaDelegata: "+CostantiTestSuite.PORTA_DELEGATA_PROFILO_SINCRONO+") non ha causato errori.");
			} catch (AxisFault error) {
				
				Assert.assertTrue(client.getCodiceStatoHTTP()==500);
				
				String codiceEccezioneAtteso = Utilities.toString(CodiceErroreIntegrazione.CODICE_432_PARSING_EXCEPTION_RICHIESTA);
				String descrizioneEccezioneAttesa = CostantiErroriIntegrazione.MSG_432_MESSAGGIO_XML_MALFORMATO_RICHIESTA;
				boolean checkDescrizioneTramiteMatchEsatto = Utilities.CONTROLLO_DESCRIZIONE_TRAMITE_METODO_CONTAINS;
				if(this.genericCode) {
					IntegrationFunctionError integrationFunctionError = IntegrationFunctionError.UNPROCESSABLE_REQUEST_CONTENT;
					ErroriProperties erroriProperties = ErroriProperties.getInstance(this.log); 
					codiceEccezioneAtteso = erroriProperties.getErrorType_noWrap(integrationFunctionError);
					if(erroriProperties.isForceGenericDetails_noWrap(integrationFunctionError)) {
						descrizioneEccezioneAttesa = erroriProperties.getGenericDetails_noWrap(integrationFunctionError);
						checkDescrizioneTramiteMatchEsatto = Utilities.CONTROLLO_DESCRIZIONE_TRAMITE_METODO_EQUALS;
					}
				}
				
				Utilities.verificaFaultIntegrazione(error, 
						CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE.getCodicePorta(),RicezioneContenutiApplicativi.ID_MODULO, 
						codiceEccezioneAtteso, descrizioneEccezioneAttesa, checkDescrizioneTramiteMatchEsatto);	

			}finally{
				dbComponentFruitore.close();
				dbComponentErogatore.close();
			}
		}catch(Exception e){
			throw e;
		}finally{
			dbComponentFruitore.close();
			dbComponentErogatore.close();

			this._unlock();
			dataFineTest = DateManager.getDate();
		}
		
		ErroreAttesoOpenSPCoopLogCore err = new ErroreAttesoOpenSPCoopLogCore();
		err.setIntervalloInferiore(dataInizioTest);
		err.setIntervalloSuperiore(dataFineTest);
		err.setMsgErrore("ErroreGenerale");
		this.erroriAttesiOpenSPCoopCore.add(err);
		
		ErroreAttesoOpenSPCoopLogCore err2 = new ErroreAttesoOpenSPCoopLogCore();
		err2.setIntervalloInferiore(dataInizioTest);
		err2.setIntervalloSuperiore(dataFineTest);
		err2.setMsgErrore("Generale(richiesta): Content is not allowed in prolog.");
		this.erroriAttesiOpenSPCoopCore.add(err2);
		
		// Altro framework
		
		ErroreAttesoOpenSPCoopLogCore err3 = new ErroreAttesoOpenSPCoopLogCore();
		err3.setIntervalloInferiore(dataInizioTest);
		err3.setIntervalloSuperiore(dataFineTest);
		err3.setMsgErrore("Generale(richiesta): com.ctc.wstx.exc.WstxUnexpectedCharException: Unexpected character '-' (code 45) in prolog; expected '<'");
		this.erroriAttesiOpenSPCoopCore.add(err3);
		
		ErroreAttesoOpenSPCoopLogCore err4 = new ErroreAttesoOpenSPCoopLogCore();
		err4.setIntervalloInferiore(dataInizioTest);
		err4.setIntervalloSuperiore(dataFineTest);
		err4.setMsgErrore("parsingExceptionRichiesta");
		this.erroriAttesiOpenSPCoopCore.add(err4);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * Errore di processamento content type non presente
	 * "433";
	 */
	// LA LIBRERIA CLIENT HTTP GENERICO NON PERMETTE UNA INVOCAZIONE SENZA CONTENT TYPE
	protected void _testContentTypeNonPresente() throws Exception{
		
		
		DatabaseComponent dbComponentFruitore = null;
		DatabaseComponent dbComponentErogatore = null;

		try{
			ClientHttpGenerico client=new ClientHttpGenerico(new Repository());
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_PROFILO_SINCRONO);
			client.connectToSoapEngine();
			// NOTA: invio un messaggio malformato forzando il test in modo da mangare un soap with attachments come fosse un messaggio senza attachments
			client.setMessageFromFile(Utilities.testSuiteProperties.getSoap11WithAttachmentsFileName(), false);
			//client.setMessaggioXMLRichiesta("CONTENTUO_ERRATO".getBytes()); QUESTO non rilancia il soap fault
			client.setRispostaDaGestire(true);
			// AttesaTerminazioneMessaggi
			
			client.setContentType("");
			
			if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()){
				dbComponentFruitore = DatabaseProperties.getDatabaseComponentFruitore();
				dbComponentErogatore = DatabaseProperties.getDatabaseComponentErogatore();

				client.setAttesaTerminazioneMessaggi(true);
				client.setDbAttesaTerminazioneMessaggiFruitore(dbComponentFruitore);
				client.setDbAttesaTerminazioneMessaggiErogatore(dbComponentErogatore);
			}
			try {
				client.run();

				Reporter.log("Invocazione porta delegata (433) (PortaDelegata: "+CostantiTestSuite.PORTA_DELEGATA_PROFILO_SINCRONO+") non ha causato errori.");
				throw new TestSuiteException("Invocazione porta delegata (433) (PortaDelegata: "+CostantiTestSuite.PORTA_DELEGATA_PROFILO_SINCRONO+") non ha causato errori.");
			} catch (AxisFault error) {
				
				Assert.assertTrue(client.getCodiceStatoHTTP()==500);
				
				String codiceEccezioneAtteso = Utilities.toString(CodiceErroreIntegrazione.CODICE_433_CONTENT_TYPE_NON_PRESENTE);
				String descrizioneEccezioneAttesa = CostantiErroriIntegrazione.MSG_433_CONTENT_TYPE_NON_PRESENTE;
				boolean checkDescrizioneTramiteMatchEsatto = Utilities.CONTROLLO_DESCRIZIONE_TRAMITE_METODO_CONTAINS;
				if(this.genericCode) {
					IntegrationFunctionError integrationFunctionError = IntegrationFunctionError.CONTENT_TYPE_NOT_PROVIDED;
					ErroriProperties erroriProperties = ErroriProperties.getInstance(this.log); 
					codiceEccezioneAtteso = erroriProperties.getErrorType_noWrap(integrationFunctionError);
					if(erroriProperties.isForceGenericDetails_noWrap(integrationFunctionError)) {
						descrizioneEccezioneAttesa = erroriProperties.getGenericDetails_noWrap(integrationFunctionError);
						checkDescrizioneTramiteMatchEsatto = Utilities.CONTROLLO_DESCRIZIONE_TRAMITE_METODO_EQUALS;
					}
				}
				
				Utilities.verificaFaultIntegrazione(error, 
						Utilities.testSuiteProperties.getIdentitaDefault_dominio(),RicezioneContenutiApplicativi.ID_MODULO, 
						codiceEccezioneAtteso, descrizioneEccezioneAttesa, checkDescrizioneTramiteMatchEsatto);		

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
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * Errore di processamento correlazione applicativa risposta
	 * "434";
	 */
	protected void _testCorrelazioneApplicativaRispostaErrata(boolean unwrap) throws Exception{
		
		DatabaseComponent dbComponentFruitore = null;
		DatabaseComponent dbComponentErogatore = null;

		java.io.FileInputStream fin = null;
		Date dataInizioTest = null;
		Date dataFineTest = null;
		try{
			_lock(unwrap);
			dataInizioTest = DateManager.getDate();
			
			fin = new java.io.FileInputStream(new File(Utilities.testSuiteProperties.getSoap11FileName()));

			Message msg=new Message(fin);
			msg.getSOAPPartAsBytes();

			String idUnivoco = "XXXXXXXIDXXXXXXXX_"+System.currentTimeMillis();
			
			MessageElement elem = (MessageElement) msg.getSOAPBody().addChildElement("idApplicativo", "test", "http://www.openspcoop.org");
			elem.setValue(idUnivoco);
			Reporter.log("Messaggio: "+msg.getSOAPPartAsString());
			msg.getSOAPPartAsBytes();
			
			ClientHttpGenerico client=new ClientHttpGenerico(new Repository());
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_PROFILO_SINCRONO_INTEGRAZIONE_CORRELAZIONE_APPLICATIVA_RISPOSTA_CONTENT_BASED_SOAP_STATELESS_ERRORE_CONFIGURAZIONE);
			client.connectToSoapEngine();
			client.setMessage(msg);
			//client.setMessaggioXMLRichiesta("CONTENTUO_ERRATO".getBytes()); QUESTO non rilancia il soap fault
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

				Reporter.log("Invocazione porta delegata (434) (PortaDelegata: "+CostantiTestSuite.PORTA_DELEGATA_PROFILO_SINCRONO_INTEGRAZIONE_CORRELAZIONE_APPLICATIVA_RISPOSTA_CONTENT_BASED_SOAP_STATELESS_ERRORE_CONFIGURAZIONE+") non ha causato errori.");
				throw new TestSuiteException("Invocazione porta delegata (434) (PortaDelegata: "+CostantiTestSuite.PORTA_DELEGATA_PROFILO_SINCRONO_INTEGRAZIONE_CORRELAZIONE_APPLICATIVA_RISPOSTA_CONTENT_BASED_SOAP_STATELESS_ERRORE_CONFIGURAZIONE+") non ha causato errori.");
			} catch (AxisFault error) {
				
				Assert.assertTrue(client.getCodiceStatoHTTP()==500);
				
				String codiceEccezioneAtteso = Utilities.toString(CodiceErroreIntegrazione.CODICE_434_CORRELAZIONE_APPLICATIVA_RISPOSTA_ERRORE);
				String descrizioneEccezioneAttesa = CostantiErroriIntegrazione.MSG_434_CORRELAZIONE_APPLICATIVA_RISPOSTA_ERRORE;
				boolean checkDescrizioneTramiteMatchEsatto = Utilities.CONTROLLO_DESCRIZIONE_TRAMITE_METODO_CONTAINS;
				if(this.genericCode) {
					IntegrationFunctionError integrationFunctionError = IntegrationFunctionError.WRAP_502_BAD_RESPONSE;
					if(unwrap) {
						integrationFunctionError = IntegrationFunctionError.APPLICATION_CORRELATION_IDENTIFICATION_RESPONSE_FAILED;
					}
					
					ErroriProperties erroriProperties = ErroriProperties.getInstance(this.log); 
					codiceEccezioneAtteso = erroriProperties.getErrorType_noWrap(integrationFunctionError);
					if(erroriProperties.isForceGenericDetails_noWrap(integrationFunctionError)) {
						descrizioneEccezioneAttesa = erroriProperties.getGenericDetails_noWrap(integrationFunctionError);
						checkDescrizioneTramiteMatchEsatto = Utilities.CONTROLLO_DESCRIZIONE_TRAMITE_METODO_EQUALS;
					}
				}
				
				Utilities.verificaFaultIntegrazione(error, CostantiTestSuite.SPCOOP_IDPORTA_SOGGETTO_FRUITORE,InoltroBuste.ID_MODULO, 
						codiceEccezioneAtteso, descrizioneEccezioneAttesa, checkDescrizioneTramiteMatchEsatto);		

			}finally{
				dbComponentFruitore.close();
				dbComponentErogatore.close();
			}
		}catch(Exception e){
			throw e;
		}finally{
			try{
				fin.close();
			}catch(Exception e){}
			dbComponentFruitore.close();
			dbComponentErogatore.close();

			this._unlock();
			dataFineTest = DateManager.getDate();
		}
		
		ErroreAttesoOpenSPCoopLogCore err = new ErroreAttesoOpenSPCoopLogCore();
		err.setIntervalloInferiore(dataInizioTest);
		err.setIntervalloSuperiore(dataFineTest);
		err.setMsgErrore("nessun match trovato per l'espressione xpath [//test:idApplicativoERRATO/text()]");
		this.erroriAttesiOpenSPCoopCore.add(err);
	}
	
	
	
	
	
	
	
	
	
	
	
	/***
	 * LocalForward configErrata
	 * "435";
	 */
	protected void _localForward_invokePD_ASINCRONI(boolean unwrap) throws TestSuiteException, Exception{
		DatabaseComponent dbComponentFruitore = null;
		DatabaseComponent dbComponentErogatore = null;

		try{
			_lock(unwrap);
			
			ClientHttpGenerico client=new ClientHttpGenerico(new Repository());
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_LOCAL_FORWARD_ASINCRONI);
			client.connectToSoapEngine();
			// NOTA: invio un messaggio malformato forzando il test in modo da mangare un soap with attachments come fosse un messaggio senza attachments
			client.setMessageFromFile(Utilities.testSuiteProperties.getLocalForwardFileName(), false);
			//client.setMessaggioXMLRichiesta("CONTENTUO_ERRATO".getBytes()); QUESTO non rilancia il soap fault
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
				
				Reporter.log("Invocazione porta delegata  (PortaDelegata: "+CostantiTestSuite.PORTA_DELEGATA_LOCAL_FORWARD_ASINCRONI+") non ha causato errori.");
				throw new TestSuiteException("Invocazione porta delegata (PortaDelegata: "+CostantiTestSuite.PORTA_DELEGATA_LOCAL_FORWARD_ASINCRONI+") non ha causato errori.");
	
			} catch (AxisFault error) {
				
				Assert.assertTrue(client.getCodiceStatoHTTP()==500);

				String infoServizio = "( Servizio spc/MinisteroErogatore:spc/RichiestaStatoAvanzamentoAsincronoAsimmetrico:1 )";
				String msgErrore = CostantiErroriIntegrazione.MSG_435_LOCAL_FORWARD_CONFIG_ERRORE+ infoServizio+" profilo di collaborazione AsincronoAsimmetrico non supportato";
				
				String codiceEccezioneAtteso = Utilities.toString(CodiceErroreIntegrazione.CODICE_435_LOCAL_FORWARD_CONFIG_ERROR);
				String descrizioneEccezioneAttesa = msgErrore;
				boolean checkDescrizioneTramiteMatchEsatto = Utilities.CONTROLLO_DESCRIZIONE_TRAMITE_METODO_EQUALS;
				if(this.genericCode) {
					IntegrationFunctionError integrationFunctionError = IntegrationFunctionError.WRAP_503_INTERNAL_ERROR;
					if(unwrap) {
						integrationFunctionError = IntegrationFunctionError.INTERNAL_REQUEST_ERROR;
					}
					
					ErroriProperties erroriProperties = ErroriProperties.getInstance(this.log); 
					codiceEccezioneAtteso = erroriProperties.getErrorType_noWrap(integrationFunctionError);
					if(erroriProperties.isForceGenericDetails_noWrap(integrationFunctionError)) {
						descrizioneEccezioneAttesa = erroriProperties.getGenericDetails_noWrap(integrationFunctionError);
						checkDescrizioneTramiteMatchEsatto = Utilities.CONTROLLO_DESCRIZIONE_TRAMITE_METODO_EQUALS;
					}
				}
				
				Utilities.verificaFaultIntegrazione(error, 
						 CostantiTestSuite.SPCOOP_IDPORTA_SOGGETTO_FRUITORE,RicezioneContenutiApplicativi.ID_MODULO, 
						 codiceEccezioneAtteso, descrizioneEccezioneAttesa, checkDescrizioneTramiteMatchEsatto);		
			
			}
			
		}catch(Exception e){
			throw e;
		}finally{
			try{
				dbComponentFruitore.close();
			}catch(Exception eClose){}
			try{
				dbComponentErogatore.close();
			}catch(Exception eClose){}
			
			this._unlock();
		}
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
	private Repository repositoryStrutturaXMLBodyRispostaPdDErrato=new Repository();
	protected void _strutturaXMLRispostaErrata(boolean unwrap)throws TestSuiteException,SOAPException, Exception{
		
		// costruzione busta
		String xml = org.openspcoop2.utils.resources.FileSystemUtilities.readFile(Utilities.testSuiteProperties.getSoap11FileName());
		Message msg=new Message(new ByteArrayInputStream(xml.getBytes()));
		msg.getSOAPPartAsBytes();

		DatabaseComponent dbComponentErogatore = null;
		Date dataInizioTest = null;
		Date dataFineTest = null;
		try{
			_lock(unwrap);
			dataInizioTest = DateManager.getDate();
			
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryStrutturaXMLBodyRispostaPdDErrato);
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_PROFILO_SINCRONO_XML_ERRATO_BODY_RISPOSTA_PDD);
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
				throw new Exception("Attesa eccezione per controllo soapAction");
			} catch (AxisFault error) {
				Reporter.log("Ricevuto SoapFAULT con Busta codice["+error.getFaultCode().getLocalPart()+"] actor ["+error.getFaultActor()+"]: "+error.getFaultString());
				
				String codiceEccezioneAtteso = Utilities.toString(CodiceErroreIntegrazione.CODICE_440_PARSING_EXCEPTION_RISPOSTA);
				String msgErrore = "Unexpected close tag </soapenv:Envelope>; expected </soapenv:Body>";
				String msgErrore2 = "Unexpected close tag";
				String msgErrore3 = "The element type \"soapenv:Body\" must be terminated by the matching end-tag \"</soapenv:Body>\"";
				boolean multiMessageOptions = true;
				
				if(this.genericCode) {
					IntegrationFunctionError integrationFunctionError = IntegrationFunctionError.WRAP_502_BAD_RESPONSE;
					if(unwrap) {
						integrationFunctionError = IntegrationFunctionError.UNPROCESSABLE_RESPONSE_CONTENT;
					}
					
					ErroriProperties erroriProperties = ErroriProperties.getInstance(this.log); 
					codiceEccezioneAtteso = erroriProperties.getErrorType_noWrap(integrationFunctionError);
					codiceEccezioneAtteso = org.openspcoop2.message.constants.Costanti.SOAP11_FAULT_CODE_SERVER +
							org.openspcoop2.message.constants.Costanti.SOAP11_FAULT_CODE_SEPARATOR+codiceEccezioneAtteso;
					
					if(erroriProperties.isForceGenericDetails_noWrap(integrationFunctionError)) {
						msgErrore = erroriProperties.getGenericDetails_noWrap(integrationFunctionError);
					}
				}
				
				Reporter.log("Controllo fault code. Atteso ["+codiceEccezioneAtteso
					+"] - Trovato [" + error.getFaultCode().getLocalPart() + "]");
				Assert.assertTrue(codiceEccezioneAtteso.equals(error.getFaultCode().getLocalPart()));
				
				Reporter.log("Controllo fault string. Trovato [" + error.getFaultString() + "] (multiMessageOptions:"+multiMessageOptions+")");
				if(multiMessageOptions) {
					Assert.assertTrue(error.getFaultString().contains(msgErrore) || error.getFaultString().contains(msgErrore2) || error.getFaultString().contains(msgErrore3));
				}
				else {
					Assert.assertTrue(error.getFaultString().equals(msgErrore));
				}
				
				Reporter.log("Controllo fault actor ["+org.openspcoop2.testsuite.core.CostantiTestSuite.OPENSPCOOP2_INTEGRATION_ACTOR+"]");
				Assert.assertTrue(org.openspcoop2.testsuite.core.CostantiTestSuite.OPENSPCOOP2_INTEGRATION_ACTOR.equals(error.getFaultActor()));
			}finally{
				dbComponentErogatore.close();
			}

		}catch(Exception e){
			throw e;
		}finally{
			dbComponentErogatore.close();

			this._unlock();
			dataFineTest = DateManager.getDate();
		}

		ErroreAttesoOpenSPCoopLogCore err = new ErroreAttesoOpenSPCoopLogCore();
		err.setIntervalloInferiore(dataInizioTest);
		err.setIntervalloSuperiore(dataFineTest);
		err.setMsgErrore("Errore avvenuto durante la consegna HTTP (Parsing Risposta SOAP)");
		this.erroriAttesiOpenSPCoopCore.add(err);

		ErroreAttesoOpenSPCoopLogCore err2 = new ErroreAttesoOpenSPCoopLogCore();
		err2.setIntervalloInferiore(dataInizioTest);
		err2.setIntervalloSuperiore(dataFineTest);
		err2.setMsgErrore("com.ctc.wstx.exc.WstxParsingException: Unexpected close tag </soapenv:Envelope>; expected </soapenv:Body>");
		this.erroriAttesiOpenSPCoopCore.add(err2);
		
		ErroreAttesoOpenSPCoopLogCore err3 = new ErroreAttesoOpenSPCoopLogCore();
		err3.setIntervalloInferiore(dataInizioTest);
		err3.setIntervalloSuperiore(dataFineTest);
		err3.setMsgErrore("ErroreGenerale");
		this.erroriAttesiOpenSPCoopCore.add(err3);
		
		ErroreAttesoOpenSPCoopLogCore err4 = new ErroreAttesoOpenSPCoopLogCore();
		err4.setIntervalloInferiore(dataInizioTest);
		err4.setIntervalloSuperiore(dataFineTest);
		err4.setMsgErrore("parsingExceptionRisposta");
		this.erroriAttesiOpenSPCoopCore.add(err4);


	}
}
