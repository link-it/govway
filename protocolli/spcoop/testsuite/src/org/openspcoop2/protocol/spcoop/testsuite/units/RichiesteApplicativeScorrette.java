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
import java.util.Date;
import java.util.Vector;

import javax.xml.soap.SOAPException;

import org.apache.axis.AxisFault;
import org.apache.axis.Message;
import org.apache.axis.message.MessageElement;
import org.openspcoop2.pdd.core.CostantiPdD;
import org.openspcoop2.pdd.core.autenticazione.GestoreCredenzialiTest;
import org.openspcoop2.pdd.mdb.InoltroBuste;
import org.openspcoop2.pdd.services.axis14.IntegrationManagerException;
import org.openspcoop2.pdd.services.axis14.IntegrationManagerMessage;
import org.openspcoop2.protocol.sdk.constants.CodiceErroreIntegrazione;
import org.openspcoop2.protocol.spcoop.constants.SPCoopCostanti;
import org.openspcoop2.protocol.spcoop.testsuite.core.CostantiErroriIntegrazione;
import org.openspcoop2.protocol.spcoop.testsuite.core.CostantiTestSuite;
import org.openspcoop2.protocol.spcoop.testsuite.core.DatabaseProperties;
import org.openspcoop2.protocol.spcoop.testsuite.core.FileSystemUtilities;
import org.openspcoop2.protocol.spcoop.testsuite.core.Utilities;
import org.openspcoop2.protocol.spcoop.testsuite.core.UtilitiesEGov;
import org.openspcoop2.testsuite.clients.ClientHttpGenerico;
import org.openspcoop2.testsuite.core.ErroreAttesoOpenSPCoopLogCore;
import org.openspcoop2.testsuite.core.TestSuiteException;
import org.openspcoop2.testsuite.core.Repository;
import org.openspcoop2.testsuite.core.SOAPEngine;
import org.openspcoop2.testsuite.core.TestSuiteProperties;
import org.openspcoop2.testsuite.db.DatabaseComponent;
import org.openspcoop2.utils.date.DateManager;
import org.testng.Assert;
import org.testng.Reporter;
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
public class RichiesteApplicativeScorrette {

	/** Identificativo del gruppo */
	public static final String ID_GRUPPO = "RichiesteApplicativeScorrette";


	
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
	
	
	


	
	
	
	
	

	/**
	 * Porta delegata non esistente
	 * "OPENSPCOOP_ORG_401";
	 */
	@DataProvider (name="porteDelegateInesistenti")
	public Object[][] porteDelegateInesistenti(){
		return new Object[][]{{"HelloWorl"},
				{"Helloworld"},
				{""},
				{"jkflkasjfdlsjal;0"},
				{"/"},
		};
	}
	@Test(groups={RichiesteApplicativeScorrette.ID_GRUPPO,RichiesteApplicativeScorrette.ID_GRUPPO+".401"},dataProvider="porteDelegateInesistenti")
	public void testPorteDelegateInesistenti(String portaDelegata) throws Exception{

		DatabaseComponent dbComponentFruitore = null;
		DatabaseComponent dbComponentErogatore = null;
		Date dataInizioTest = DateManager.getDate();
		try{

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
				
				Utilities.verificaFaultIntegrazione(error, 
						Utilities.testSuiteProperties.getIdentitaDefault_dominio(),"RicezioneContenutiApplicativiSOAP", 
						Utilities.toString(CodiceErroreIntegrazione.CODICE_401_PD_INESISTENTE), 
						CostantiErroriIntegrazione.MSG_401_PD_INESISTENTE, Utilities.CONTROLLO_DESCRIZIONE_TRAMITE_METODO_CONTAINS);				
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
		
		// Aggiungo errori attesi
//		if("HelloWorl".equals(portaDelegata) ||
//				"Helloworld".equals(portaDelegata) ||
//				"jkflkasjfdlsjal;0".equals(portaDelegata) ){
		ErroreAttesoOpenSPCoopLogCore err = new ErroreAttesoOpenSPCoopLogCore();
		err.setIntervalloInferiore(dataInizioTest);
		err.setIntervalloSuperiore(dataFineTest);
		err.setMsgErrore("La porta delegata invocata non esiste pd["+portaDelegata+"] urlInvocazione[/openspcoop2/spcoop/PD/"+portaDelegata+"]");
		this.erroriAttesiOpenSPCoopCore.add(err);
		//}
		
	}


	
	
	
	
	
	
	/**
	 * Autenticazione fallita
	 * "OPENSPCOOP_ORG_402";
	 * 
	 * messaggio: credenziali non fornite
	 */
	@DataProvider(name="credenzialiNonFornite")
	public Object[][] credenzialiNonFornite(){
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
	@Test(groups={RichiesteApplicativeScorrette.ID_GRUPPO,RichiesteApplicativeScorrette.ID_GRUPPO+".402"},dataProvider="credenzialiNonFornite")
	public void testAutenticazioneCredenzialiNonFornite(String username,String password) throws Exception{
		DatabaseComponent dbComponentFruitore = null;
		DatabaseComponent dbComponentErogatore = null;

		try{
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
				
				Utilities.verificaFaultIntegrazione(error, 
						CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE.getCodicePorta(),"RicezioneContenutiApplicativiSOAP", 
						Utilities.toString(CodiceErroreIntegrazione.CODICE_402_AUTENTICAZIONE_FALLITA),
						CostantiErroriIntegrazione.MSG_402_AUTENTICAZIONE_FALLITA_CREDENZIALI_NON_FORNITE, Utilities.CONTROLLO_DESCRIZIONE_TRAMITE_METODO_CONTAINS);	
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
	 * Autenticazione fallita
	 * "OPENSPCOOP_ORG_402";
	 * 
	 * messaggio: credenziali fornite non corrette
	 */
	@DataProvider(name="credenzialiScorrette")
	public Object[][] credenzialiScorrette(){
		return new Object[][]{
				{"admi","123456"},//password corretta, user errato
				{"adminSilX","121213"},//user corretto , password errata
		};
	}
	@Test(groups={RichiesteApplicativeScorrette.ID_GRUPPO,RichiesteApplicativeScorrette.ID_GRUPPO+".402"},dataProvider="credenzialiScorrette",
			dependsOnMethods="testAutenticazioneCredenzialiNonFornite")
	public void testAutenticazioneCredenzialiScorrette(String username,String password) throws Exception{
		DatabaseComponent dbComponentFruitore = null;
		DatabaseComponent dbComponentErogatore = null;

		try{
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
				
				Utilities.verificaFaultIntegrazione(error, 
						CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE.getCodicePorta(),"RicezioneContenutiApplicativiSOAP", 
						Utilities.toString(CodiceErroreIntegrazione.CODICE_402_AUTENTICAZIONE_FALLITA), 
						CostantiErroriIntegrazione.MSG_402_AUTENTICAZIONE_FALLITA, Utilities.CONTROLLO_DESCRIZIONE_TRAMITE_METODO_CONTAINS);	
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
	 * Autenticazione fallita
	 * "OPENSPCOOP_ORG_402";
	 * 
	 * messaggio: identità del servizio applicativo fornita non esiste nella configurazione
	 */
	@Test(groups={RichiesteApplicativeScorrette.ID_GRUPPO,RichiesteApplicativeScorrette.ID_GRUPPO+".402"},
			dependsOnMethods="testAutenticazioneCredenzialiScorrette")
	public void testAutenticazioneSANonEsistente() throws Exception{
		DatabaseComponent dbComponentFruitore = null;
		DatabaseComponent dbComponentErogatore = null;

		try{
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
				
				Utilities.verificaFaultIntegrazione(error, 
						CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE.getCodicePorta(),"RicezioneContenutiApplicativiSOAP", 
						Utilities.toString(CodiceErroreIntegrazione.CODICE_402_AUTENTICAZIONE_FALLITA), 
						CostantiErroriIntegrazione.MSG_402_AUTENTICAZIONE_FALLITA_IDENTITA_SERVIZIO_APPLICATIVO_ERRATA.replace("SERVIZIO_APPLICATIVO", "ServizioApplicativoNonEsistente"), 
						Utilities.CONTROLLO_DESCRIZIONE_TRAMITE_METODO_CONTAINS);	
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
	 * Pattern Ricerca Porta Delegata Non Validi
	 * "OPENSPCOOP_ORG_403";
	 */
	
	// CONTENT-BASED
	@Test(groups={RichiesteApplicativeScorrette.ID_GRUPPO,RichiesteApplicativeScorrette.ID_GRUPPO+".403"})
	public void testParametriIdentificazionePortaDelegataNonValidi_contentBased() throws Exception{
		DatabaseComponent dbComponentFruitore = null;
		DatabaseComponent dbComponentErogatore = null;

		try{
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
				
				Utilities.verificaFaultIntegrazione(error, 
						CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE.getCodicePorta(),"RicezioneContenutiApplicativiSOAP", 
						Utilities.toString(CodiceErroreIntegrazione.CODICE_403_PD_PATTERN_NON_VALIDO), 
						CostantiErroriIntegrazione.MSG_403_PD_PATTERN_NON_VALIDO.replace("TIPO", "SOGGETTO_EROGATORE"), 
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
	
	// URL-BASED
	@Test(groups={RichiesteApplicativeScorrette.ID_GRUPPO,RichiesteApplicativeScorrette.ID_GRUPPO+".403"},
			dependsOnMethods="testParametriIdentificazionePortaDelegataNonValidi_contentBased")
	public void testParametriIdentificazionePortaDelegataNonValidi_urlBased() throws Exception{
		DatabaseComponent dbComponentFruitore = null;
		DatabaseComponent dbComponentErogatore = null;

		try{
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
				
				Utilities.verificaFaultIntegrazione(error, 
						CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE.getCodicePorta(),"RicezioneContenutiApplicativiSOAP", 
						Utilities.toString(CodiceErroreIntegrazione.CODICE_403_PD_PATTERN_NON_VALIDO), 
						CostantiErroriIntegrazione.MSG_403_PD_PATTERN_NON_VALIDO.replace("TIPO", "SOGGETTO_EROGATORE"), 
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
	
	// URL-FORM-BASED
	@Test(groups={RichiesteApplicativeScorrette.ID_GRUPPO,RichiesteApplicativeScorrette.ID_GRUPPO+".403"},
			dependsOnMethods="testParametriIdentificazionePortaDelegataNonValidi_urlBased")
	public void testParametriIdentificazionePortaDelegataNonValidi_urlFormBased() throws Exception{
		DatabaseComponent dbComponentFruitore = null;
		DatabaseComponent dbComponentErogatore = null;

		try{
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
				
				Utilities.verificaFaultIntegrazione(error, 
						CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE.getCodicePorta(),"RicezioneContenutiApplicativiSOAP", 
						Utilities.toString(CodiceErroreIntegrazione.CODICE_403_PD_PATTERN_NON_VALIDO), 
						CostantiErroriIntegrazione.MSG_403_PD_PATTERN_NON_VALIDO.replace("TIPO", "SOGGETTO_EROGATORE"), 
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
	
	// INPUT-BASED
	@Test(groups={RichiesteApplicativeScorrette.ID_GRUPPO,RichiesteApplicativeScorrette.ID_GRUPPO+".403"},
			dependsOnMethods="testParametriIdentificazionePortaDelegataNonValidi_urlFormBased")
	public void testParametriIdentificazionePortaDelegataNonValidi_inputBased() throws Exception{
		DatabaseComponent dbComponentFruitore = null;
		DatabaseComponent dbComponentErogatore = null;

		try{
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
				
				Utilities.verificaFaultIntegrazione(error, 
						CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE.getCodicePorta(),"RicezioneContenutiApplicativiSOAP", 
						Utilities.toString(CodiceErroreIntegrazione.CODICE_403_PD_PATTERN_NON_VALIDO), 
						CostantiErroriIntegrazione.MSG_403_PD_PATTERN_NON_VALIDO.replace("TIPO", "SOGGETTO_EROGATORE"), 
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
	
	



	
	
	
	
	/**
	 * Autorizzazione Fallita
	 * "OPENSPCOOP_ORG_404";
	 */
	@Test(groups={RichiesteApplicativeScorrette.ID_GRUPPO,RichiesteApplicativeScorrette.ID_GRUPPO+".404"})
	public void testAutorizzazioneFallita() throws Exception{
		DatabaseComponent dbComponentFruitore = null;
		DatabaseComponent dbComponentErogatore = null;

		try{
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
				
				Utilities.verificaFaultIntegrazione(error, 
						CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE.getCodicePorta(),"RicezioneContenutiApplicativiSOAP", 
						Utilities.toString(CodiceErroreIntegrazione.CODICE_404_AUTORIZZAZIONE_FALLITA), 
						CostantiErroriIntegrazione.MSG_404_AUTORIZZAZIONE_FALLITA.replace("SERVIZIO_APPLICATIVO", "silY"), 
						Utilities.CONTROLLO_DESCRIZIONE_TRAMITE_METODO_CONTAINS);	
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
	 * Servizio SPCoop abbinato alla Porta Delegata Inesistente
	 * "OPENSPCOOP_ORG_405";
	 */
	@Test(groups={RichiesteApplicativeScorrette.ID_GRUPPO,RichiesteApplicativeScorrette.ID_GRUPPO+".405"})
	public void testServizioSPCoopNonEsistente() throws Exception{
		DatabaseComponent dbComponentFruitore = null;
		DatabaseComponent dbComponentErogatore = null;

		try{
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
				
				Utilities.verificaFaultIntegrazione(error, 
						CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE.getCodicePorta(),"RicezioneContenutiApplicativiSOAP", 
						Utilities.toString(CodiceErroreIntegrazione.CODICE_405_SERVIZIO_NON_TROVATO), 
						CostantiErroriIntegrazione.MSG_405_SERVIZIO_NON_TROVATO, Utilities.CONTROLLO_DESCRIZIONE_TRAMITE_METODO_CONTAINS);	
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
	 * Nessun Messaggio disponibile per il Servizio Applicativo (Integration Manager)
	 * "OPENSPCOOP_ORG_406";
	 */
	@Test(groups={RichiesteApplicativeScorrette.ID_GRUPPO,RichiesteApplicativeScorrette.ID_GRUPPO+".406"})
	public void testIM_messaggiNonDisponibili() throws Exception{
		org.openspcoop2.pdd.services.axis14.MessageBox_PortType im = 
			IntegrationManager.getIntegrationManagerMessageBox_axis14(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore().replace("PD","IntegrationManager/MessageBox"), 
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
	private void verificaSPCoopException_406(IntegrationManagerException e) throws Exception{
		Reporter.log("Codice errore ["+e.getCodiceEccezione()+"]");
		Reporter.log("confronto con ["+Utilities.toString(CodiceErroreIntegrazione.CODICE_406_INTEGRATION_MANAGER_MESSAGGI_FOR_SIL_NON_TROVATI)+"]");
		Assert.assertTrue(Utilities.toString(CodiceErroreIntegrazione.CODICE_406_INTEGRATION_MANAGER_MESSAGGI_FOR_SIL_NON_TROVATI).equals(e.getCodiceEccezione()));
		
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
		Reporter.log("confronto con ["+CostantiErroriIntegrazione.MSG_406_INTEGRATION_MANAGER_MESSAGGI_FOR_SIL_NON_TROVATI+"]");
		Assert.assertTrue(CostantiErroriIntegrazione.MSG_406_INTEGRATION_MANAGER_MESSAGGI_FOR_SIL_NON_TROVATI.equals(e.getDescrizioneEccezione()));
	}
	
	
	
	
	
	
	
	
	
	
	/**
	 * Messaggio Richiesto Inesistente (Integration Manager)
	 * "OPENSPCOOP_ORG_407";
	 */
	@Test(groups={RichiesteApplicativeScorrette.ID_GRUPPO,RichiesteApplicativeScorrette.ID_GRUPPO+".407"})
	public void testIM_messaggioNonEsistente() throws Exception{
		org.openspcoop2.pdd.services.axis14.MessageBox_PortType im = 
			IntegrationManager.getIntegrationManagerMessageBox_axis14(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore().replace("PD","IntegrationManager/MessageBox"), 
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
		
	}
	private void verificaSPCoopException_407(IntegrationManagerException e) throws Exception{
		Reporter.log("Codice errore ["+e.getCodiceEccezione()+"]");
		Reporter.log("confronto con ["+Utilities.toString(CodiceErroreIntegrazione.CODICE_407_INTEGRATION_MANAGER_MSG_RICHIESTO_NON_TROVATO)+"]");
		Assert.assertTrue(Utilities.toString(CodiceErroreIntegrazione.CODICE_407_INTEGRATION_MANAGER_MSG_RICHIESTO_NON_TROVATO).equals(e.getCodiceEccezione()));
		
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
		Reporter.log("confronto con ["+CostantiErroriIntegrazione.MSG_407_INTEGRATION_MANAGER_MSG_RICHIESTO_NON_TROVATO+"]");
		Assert.assertTrue(CostantiErroriIntegrazione.MSG_407_INTEGRATION_MANAGER_MSG_RICHIESTO_NON_TROVATO.equals(e.getDescrizioneEccezione()));
	}
	
	


	
	
	
	
	
	
	
	/**
	 * Servizio Correlato associato ad un Servizio Asincrono non esistente
	 * "OPENSPCOOP_ORG_408";
	 */
	@Test(groups={RichiesteApplicativeScorrette.ID_GRUPPO,RichiesteApplicativeScorrette.ID_GRUPPO+".408"})
	public void testServizioCorrelatoNonEsistenteAS() throws Exception{
		DatabaseComponent dbComponentFruitore = null;
		DatabaseComponent dbComponentErogatore = null;

		try{
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
				
				Utilities.verificaFaultIntegrazione(error, 
						"TestRichiesteScorretteSPCoopIT","RicezioneContenutiApplicativiSOAP", 
						Utilities.toString(CodiceErroreIntegrazione.CODICE_408_SERVIZIO_CORRELATO_NON_TROVATO), 
						CostantiErroriIntegrazione.MSG_408_SERVIZIO_CORRELATO_NON_TROVATO, Utilities.CONTROLLO_DESCRIZIONE_TRAMITE_METODO_EQUALS);	
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
	
	@Test(groups={RichiesteApplicativeScorrette.ID_GRUPPO,RichiesteApplicativeScorrette.ID_GRUPPO+".408"}, 
			dependsOnMethods="testServizioCorrelatoNonEsistenteAS")
	public void testServizioCorrelatoNonEsistenteAA() throws Exception{
		DatabaseComponent dbComponentFruitore = null;
		DatabaseComponent dbComponentErogatore = null;

		try{
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
				
				Utilities.verificaFaultIntegrazione(error, 
						"TestRichiesteScorretteSPCoopIT","RicezioneContenutiApplicativiSOAP", 
						Utilities.toString(CodiceErroreIntegrazione.CODICE_408_SERVIZIO_CORRELATO_NON_TROVATO), 
						CostantiErroriIntegrazione.MSG_408_SERVIZIO_CORRELATO_NON_TROVATO, Utilities.CONTROLLO_DESCRIZIONE_TRAMITE_METODO_EQUALS);	
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
	 * Risposta/RichiestaStato asincrona non correlata ad una precedente richiesta
	 * "OPENSPCOOP_ORG_409";
	 */
	@Test(groups={RichiesteApplicativeScorrette.ID_GRUPPO,RichiesteApplicativeScorrette.ID_GRUPPO+".409"})
	public void testRispostaAsincronaSimmetricaNonGenerabile() throws Exception{
		
		Date dataInizioTest = DateManager.getDate();
		DatabaseComponent dbComponentFruitore = null;
		DatabaseComponent dbComponentErogatore = null;

		try{
			
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
				
				Utilities.verificaFaultIntegrazione(error, 
						CostantiTestSuite.SPCOOP_SOGGETTO_EROGATORE.getCodicePorta(),
						"Imbustamento", 
						Utilities.toString(CodiceErroreIntegrazione.CODICE_409_RISPOSTA_ASINCRONA_NON_CORRELATA_ALLA_RICHIESTA), 
						CostantiErroriIntegrazione.MSG_409_RISPOSTA_ASINCRONA_NON_CORRELATA_ALLA_RICHIESTA, Utilities.CONTROLLO_DESCRIZIONE_TRAMITE_METODO_EQUALS);	
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
	
	@Test(groups={RichiesteApplicativeScorrette.ID_GRUPPO,RichiesteApplicativeScorrette.ID_GRUPPO+".409"},
			dependsOnMethods="testRispostaAsincronaSimmetricaNonGenerabile")
	public void testRispostaAsincronaAsimmetricaNonGenerabile() throws Exception{
		
		Date dataInizioTest = DateManager.getDate();
		DatabaseComponent dbComponentFruitore = null;
		DatabaseComponent dbComponentErogatore = null;

		try{
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
				
				Utilities.verificaFaultIntegrazione(error, 
						CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE.getCodicePorta(),"Imbustamento", 
						Utilities.toString(CodiceErroreIntegrazione.CODICE_409_RISPOSTA_ASINCRONA_NON_CORRELATA_ALLA_RICHIESTA), 
						CostantiErroriIntegrazione.MSG_409_RISPOSTA_ASINCRONA_NON_CORRELATA_ALLA_RICHIESTA, Utilities.CONTROLLO_DESCRIZIONE_TRAMITE_METODO_EQUALS);	
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
	 * "OPENSPCOOP_ORG_410";
	 */
	@Test(groups={RichiesteApplicativeScorrette.ID_GRUPPO,RichiesteApplicativeScorrette.ID_GRUPPO+".410"})
	public void testAutenticazioneRichiestaServizioApplicativoAsincronoSimmetrico() throws Exception{
		DatabaseComponent dbComponentFruitore = null;
		DatabaseComponent dbComponentErogatore = null;

		try{
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
				
				Utilities.verificaFaultIntegrazione(error, 
						CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE.getCodicePorta(),"Imbustamento", 
						Utilities.toString(CodiceErroreIntegrazione.CODICE_410_AUTENTICAZIONE_RICHIESTA), 
						CostantiErroriIntegrazione.MSG_410_AUTENTICAZIONE_RICHIESTA, Utilities.CONTROLLO_DESCRIZIONE_TRAMITE_METODO_EQUALS);	
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
	 * Elemento Risposta Asincrona richiesto per l'invocazione della Porta Delegata
	 * "OPENSPCOOP_ORG_411";
	 */
	@Test(groups={RichiesteApplicativeScorrette.ID_GRUPPO,RichiesteApplicativeScorrette.ID_GRUPPO+".411"})
	public void testElementoRispostaAsincronaNonPresente() throws Exception{
		DatabaseComponent dbComponentFruitore = null;
		DatabaseComponent dbComponentErogatore = null;

		try{
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
				
				Utilities.verificaFaultIntegrazione(error, 
						CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE.getCodicePorta(),"Imbustamento", 
						Utilities.toString(CodiceErroreIntegrazione.CODICE_411_RICEZIONE_CONTENUTI_ASINCRONA_RICHIESTA), 
						CostantiErroriIntegrazione.MSG_411_RICEZIONE_CONTENUTI_ASINCRONA_RICHIESTA, Utilities.CONTROLLO_DESCRIZIONE_TRAMITE_METODO_EQUALS);	
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
	 * Porta Delegata invocabile dal servizio applicativo solo per riferimento
	 * "OPENSPCOOP_ORG_412";
	 */
	@Test(groups={RichiesteApplicativeScorrette.ID_GRUPPO,RichiesteApplicativeScorrette.ID_GRUPPO+".412"})
	public void testInvioPerRiferimento() throws Exception{
		DatabaseComponent dbComponentFruitore = null;
		DatabaseComponent dbComponentErogatore = null;

		try{
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
				
				Utilities.verificaFaultIntegrazione(error, 
						CostantiTestSuite.SPCOOP_SOGGETTO_EROGATORE.getCodicePorta(),"RicezioneContenutiApplicativiSOAP", 
						Utilities.toString(CodiceErroreIntegrazione.CODICE_412_PD_INVOCABILE_SOLO_PER_RIFERIMENTO), 
						CostantiErroriIntegrazione.MSG_412_PD_INVOCABILE_SOLO_PER_RIFERIMENTO, Utilities.CONTROLLO_DESCRIZIONE_TRAMITE_METODO_EQUALS);	
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
	 * Porta Delegata invocabile dal servizio applicativo solo senza riferimento
	 * "OPENSPCOOP_ORG_413";
	 */
	@Test(groups={RichiesteApplicativeScorrette.ID_GRUPPO,RichiesteApplicativeScorrette.ID_GRUPPO+".413"})
	public void testInvioPerRiferimentoNonAutorizzato() throws Exception{
		
		DatabaseComponent db = null;
		String idEGov = null;
		try{
			
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
				IntegrationManager.getIntegrationManagerPD_axis14(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore().replace("PD","IntegrationManager/PD"),
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
		}
			
	}
	private void verificaSPCoopException_413(IntegrationManagerException e) throws Exception{
		Reporter.log("Codice errore ["+e.getCodiceEccezione()+"]");
		Reporter.log("confronto con ["+Utilities.toString(CodiceErroreIntegrazione.CODICE_413_PD_INVOCABILE_SOLO_SENZA_RIFERIMENTO)+"]");
		Assert.assertTrue(Utilities.toString(CodiceErroreIntegrazione.CODICE_413_PD_INVOCABILE_SOLO_SENZA_RIFERIMENTO).equals(e.getCodiceEccezione()));
			
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
		Reporter.log("confronto con ["+CostantiErroriIntegrazione.MSG_413_PD_INVOCABILE_SOLO_SENZA_RIFERIMENTO+"]");
		Assert.assertTrue(CostantiErroriIntegrazione.MSG_413_PD_INVOCABILE_SOLO_SENZA_RIFERIMENTO.equals(e.getDescrizioneEccezione()));
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * Consegna in ordine utilizzabile sono con profilo Oneway
	 * "OPENSPCOOP_ORG_414";
	 */
	@Test(groups={RichiesteApplicativeScorrette.ID_GRUPPO,RichiesteApplicativeScorrette.ID_GRUPPO+".414"})
	public void testConsegnaOrdineProfiloSincrono() throws Exception{
		DatabaseComponent dbComponentFruitore = null;
		DatabaseComponent dbComponentErogatore = null;

		try{
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
				
				Utilities.verificaFaultIntegrazione(error, 
						CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE.getCodicePorta(),"Imbustamento", 
						Utilities.toString(CodiceErroreIntegrazione.CODICE_414_CONSEGNA_IN_ORDINE_CON_PROFILO_NO_ONEWAY), 
						CostantiErroriIntegrazione.MSG_414_CONSEGNA_IN_ORDINE_CON_PROFILO_NO_ONEWAY, Utilities.CONTROLLO_DESCRIZIONE_TRAMITE_METODO_EQUALS);	
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
	 * Consegna in ordine non utilizzabile per mancanza di dati necessari
	 * "OPENSPCOOP_ORG_415";
	 */
	@Test(groups={RichiesteApplicativeScorrette.ID_GRUPPO,RichiesteApplicativeScorrette.ID_GRUPPO+".415"})
	public void testConsegnaOrdineConfigurazioneErrata_confermaRicezioneMancante() throws Exception{
		DatabaseComponent dbComponentFruitore = null;
		DatabaseComponent dbComponentErogatore = null;

		try{
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
				
				Utilities.verificaFaultIntegrazione(error, 
						CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE.getCodicePorta(),"Imbustamento", 
						Utilities.toString(CodiceErroreIntegrazione.CODICE_415_CONSEGNA_IN_ORDINE_SENZA_VINCOLI_RICHIESTI), 
						CostantiErroriIntegrazione.MSG_415_CONSEGNA_IN_ORDINE_SENZA_VINCOLI_RICHIESTI, Utilities.CONTROLLO_DESCRIZIONE_TRAMITE_METODO_EQUALS);	
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
	
	@Test(groups={RichiesteApplicativeScorrette.ID_GRUPPO,RichiesteApplicativeScorrette.ID_GRUPPO+".415"},
			dependsOnMethods="testConsegnaOrdineConfigurazioneErrata_confermaRicezioneMancante")
	public void testConsegnaOrdineConfigurazioneErrata_filtroDuplicatiMancante() throws Exception{
		DatabaseComponent dbComponentFruitore = null;
		DatabaseComponent dbComponentErogatore = null;

		try{
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
				
				Utilities.verificaFaultIntegrazione(error, 
						CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE.getCodicePorta(),"Imbustamento", 
						Utilities.toString(CodiceErroreIntegrazione.CODICE_415_CONSEGNA_IN_ORDINE_SENZA_VINCOLI_RICHIESTI), 
						CostantiErroriIntegrazione.MSG_415_CONSEGNA_IN_ORDINE_SENZA_VINCOLI_RICHIESTI, Utilities.CONTROLLO_DESCRIZIONE_TRAMITE_METODO_EQUALS);	
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
	
	@Test(groups={RichiesteApplicativeScorrette.ID_GRUPPO,RichiesteApplicativeScorrette.ID_GRUPPO+".415"},
			dependsOnMethods="testConsegnaOrdineConfigurazioneErrata_filtroDuplicatiMancante")
	public void testConsegnaOrdineConfigurazioneErrata_idCollaborazioneMancante() throws Exception{
		DatabaseComponent dbComponentFruitore = null;
		DatabaseComponent dbComponentErogatore = null;

		try{
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
				
				Utilities.verificaFaultIntegrazione(error, 
						CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE.getCodicePorta(),"Imbustamento", 
						Utilities.toString(CodiceErroreIntegrazione.CODICE_415_CONSEGNA_IN_ORDINE_SENZA_VINCOLI_RICHIESTI), 
						CostantiErroriIntegrazione.MSG_415_CONSEGNA_IN_ORDINE_SENZA_VINCOLI_RICHIESTI, Utilities.CONTROLLO_DESCRIZIONE_TRAMITE_METODO_EQUALS);	
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
	 * Correlazione Applicativa non riuscita
	 * "OPENSPCOOP_ORG_416";
	 */
	@Test(groups={RichiesteApplicativeScorrette.ID_GRUPPO,RichiesteApplicativeScorrette.ID_GRUPPO+".416"})
	public void testCorrelazioneApplicativaErrata() throws Exception{
		
		Date dataInizioTest = DateManager.getDate();
		
		DatabaseComponent dbComponentFruitore = null;
		DatabaseComponent dbComponentErogatore = null;

		try{
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
				
				Utilities.verificaFaultIntegrazione(error, 
						CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE.getCodicePorta(),"RicezioneContenutiApplicativiSOAP", 
						Utilities.toString(CodiceErroreIntegrazione.CODICE_416_CORRELAZIONE_APPLICATIVA_RICHIESTA_ERRORE), 
						CostantiErroriIntegrazione.MSG_416_CORRELAZIONE_APPLICATIVA_RICHIESTA_ERRORE, Utilities.CONTROLLO_DESCRIZIONE_TRAMITE_METODO_CONTAINS);	
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
		err.setMsgErrore("identificativo di correlazione applicativa non identificato nell'elemento [*] con modalita' di acquisizione urlBased (Pattern:.+correlazioneApplicativa=([^&]*).*): nessun match trovato");
		this.erroriAttesiOpenSPCoopCore.add(err);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * Impossibile istanziare un validatore: XSD non valido o mancante
	 * "OPENSPCOOP_ORG_417";
	 */
	@Test(groups={RichiesteApplicativeScorrette.ID_GRUPPO,RichiesteApplicativeScorrette.ID_GRUPPO+".417"})
	public void testValidazioneApplicativaSenzaXsd_tipoXSD() throws Exception{
		
		Date dataInizioTest = DateManager.getDate();
		
		DatabaseComponent dbComponentFruitore = null;
		DatabaseComponent dbComponentErogatore = null;

		try{
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
				
				Utilities.verificaFaultIntegrazione(error, 
						CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE.getCodicePorta(),"RicezioneContenutiApplicativiSOAP", 
						Utilities.toString(CodiceErroreIntegrazione.CODICE_417_COSTRUZIONE_VALIDATORE_WSDL_FALLITA), 
						CostantiErroriIntegrazione.MSG_417_COSTRUZIONE_VALIDATORE_WSDL_FALLITA.replace("TIPO_WSDL", "Schema xsd dei messaggi"), Utilities.CONTROLLO_DESCRIZIONE_TRAMITE_METODO_EQUALS);	
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
		err.setMsgErrore("Riscontrato errore durante l'inizializzazione: [SchemaXSD] L'accordo di servizio parte comune non contiene schemi");
		this.erroriAttesiOpenSPCoopCore.add(err);
	}
	
	@Test(groups={RichiesteApplicativeScorrette.ID_GRUPPO,RichiesteApplicativeScorrette.ID_GRUPPO+".417"},
			dependsOnMethods="testValidazioneApplicativaSenzaXsd_tipoXSD")
	public void testValidazioneApplicativaSenzaXsd_tipoWSDL() throws Exception{
		
		Date dataInizioTest = DateManager.getDate();
		
		DatabaseComponent dbComponentFruitore = null;
		DatabaseComponent dbComponentErogatore = null;

		try{
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
				
				Utilities.verificaFaultIntegrazione(error, 
						CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE.getCodicePorta(),"RicezioneContenutiApplicativiSOAP", 
						Utilities.toString(CodiceErroreIntegrazione.CODICE_417_COSTRUZIONE_VALIDATORE_WSDL_FALLITA), 
						CostantiErroriIntegrazione.MSG_417_COSTRUZIONE_VALIDATORE_WSDL_FALLITA.replace("TIPO_WSDL", "Schema xsd dei messaggi"), Utilities.CONTROLLO_DESCRIZIONE_TRAMITE_METODO_EQUALS);	
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
		err.setMsgErrore("Riscontrato errore durante l'inizializzazione: [SchemaXSD] L'accordo di servizio parte comune non contiene schemi");
		this.erroriAttesiOpenSPCoopCore.add(err);
	}
	
	@Test(groups={RichiesteApplicativeScorrette.ID_GRUPPO,RichiesteApplicativeScorrette.ID_GRUPPO+".417"},
			dependsOnMethods="testValidazioneApplicativaSenzaXsd_tipoWSDL")
	public void testValidazioneApplicativaSenzaXsd_tipoOPENSPCOOP() throws Exception{
		
		Date dataInizioTest = DateManager.getDate();
		
		DatabaseComponent dbComponentFruitore = null;
		DatabaseComponent dbComponentErogatore = null;

		try{
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
				
				Utilities.verificaFaultIntegrazione(error, 
						CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE.getCodicePorta(),"RicezioneContenutiApplicativiSOAP", 
						Utilities.toString(CodiceErroreIntegrazione.CODICE_417_COSTRUZIONE_VALIDATORE_WSDL_FALLITA), 
						CostantiErroriIntegrazione.MSG_417_COSTRUZIONE_VALIDATORE_WSDL_FALLITA.replace("TIPO_WSDL", "Schema xsd dei messaggi"), Utilities.CONTROLLO_DESCRIZIONE_TRAMITE_METODO_EQUALS);	
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
		err.setMsgErrore("Riscontrato errore durante l'inizializzazione: [SchemaXSD] L'accordo di servizio parte comune non contiene schemi");
		this.erroriAttesiOpenSPCoopCore.add(err);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	

	/**
	 * Validazione del messaggio di richiesta fallita
	 * "OPENSPCOOP_ORG_418";
	 */
	@Test(groups={RichiesteApplicativeScorrette.ID_GRUPPO,RichiesteApplicativeScorrette.ID_GRUPPO+".418"})
	public void testValidazioneApplicativaRichiestaFallita() throws Exception{
		
		Date dataInizioTest = DateManager.getDate();
		
		DatabaseComponent dbComponentFruitore = null;
		DatabaseComponent dbComponentErogatore = null;

		String portaDelegata = CostantiTestSuite.PORTA_DELEGATA_VALIDAZIONE_WSDL_GENERICA+"/"+
			CostantiTestSuite.SPCOOP_NOME_SERVIZIO_VAL_WSDL_GESTIONE_UTENTI_WDL+"/"+
			CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_VALIDAZIONE_WSDL_OPENSPCOOP_AZIONE_REGISTRAZIONE_UTENTE_WDL;
		
		try{
			
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
				
				Utilities.verificaFaultIntegrazione(error, 
						CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE.getCodicePorta(),"RicezioneContenutiApplicativiSOAP", 
						Utilities.toString(CodiceErroreIntegrazione.CODICE_418_VALIDAZIONE_WSDL_RICHIESTA_FALLITA), 
						CostantiErroriIntegrazione.MSG_418_VALIDAZIONE_WSDL_RICHIESTA_FALLITA.replace("TIPO_WSDL", "Wsdl erogatore"), Utilities.CONTROLLO_DESCRIZIONE_TRAMITE_METODO_EQUALS);	
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
		err.setMsgErrore("Validazione WSDL (true) fallita: Messaggio con elementi non conformi alla definizione wsdl dell'Operation [registrazioneUtenteWDL] del port-type [GestioneUtentiWrappedDocumentLiteral] (AccordoServizio:ASMultiPortTypeValidazioneWSDL style:document use:literal)");
		this.erroriAttesiOpenSPCoopCore.add(err);
		
		ErroreAttesoOpenSPCoopLogCore err2 = new ErroreAttesoOpenSPCoopLogCore();
		err2.setIntervalloInferiore(dataInizioTest);
		err2.setIntervalloSuperiore(dataFineTest);
		err2.setMsgErrore("[ValidazioneContenutiApplicativi Richiesta] Riscontrata non conformità rispetto all'interfaccia WSDL; Messaggio con elementi non conformi alla definizione wsdl dell'Operation [registrazioneUtenteWDL] del port-type [GestioneUtentiWrappedDocumentLiteral] (AccordoServizio:ASMultiPortTypeValidazioneWSDL style:document use:literal)");
		this.erroriAttesiOpenSPCoopCore.add(err2);
		
	}
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * Validazione del messaggio di risposta fallita
	 * "OPENSPCOOP_ORG_419";
	 */
	@Test(groups={RichiesteApplicativeScorrette.ID_GRUPPO,RichiesteApplicativeScorrette.ID_GRUPPO+".419"})
	public void testValidazioneApplicativaRispostaFallita() throws Exception{
		
		Date dataInizioTest = DateManager.getDate();
		
		DatabaseComponent dbComponentFruitore = null;
		DatabaseComponent dbComponentErogatore = null;

		String portaDelegata = CostantiTestSuite.PORTA_DELEGATA_VALIDAZIONE_APPLICATIVA_RISPOSTA_FALLITA;
		
		try{
			
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
				
				Utilities.verificaFaultIntegrazione(error, 
						CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE.getCodicePorta(),"InoltroBuste", 
						Utilities.toString(CodiceErroreIntegrazione.CODICE_419_VALIDAZIONE_WSDL_RISPOSTA_FALLITA), 
						CostantiErroriIntegrazione.MSG_419_VALIDAZIONE_WSDL_RISPOSTA_FALLITA.replace("TIPO_WSDL", "Schema xsd dei messaggi"), Utilities.CONTROLLO_DESCRIZIONE_TRAMITE_METODO_EQUALS);	
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
		err.setMsgErrore("Validazione fallita (elemento {http://schemas.xmlsoap.org/soap/envelope/}AlterazioneMessaggio) [<?xml version=\"1.0\" encoding=\"UTF-8\"?><soapenv:AlterazioneMessaggio xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"/>]: cvc-elt.1: Cannot find the declaration of element 'soapenv:AlterazioneMessaggio'.");
		this.erroriAttesiOpenSPCoopCore.add(err);
		
		ErroreAttesoOpenSPCoopLogCore err2 = new ErroreAttesoOpenSPCoopLogCore();
		err2.setIntervalloInferiore(dataInizioTest);
		err2.setIntervalloSuperiore(dataFineTest);
		err2.setMsgErrore("Riscontrata non conformità rispetto agli schemi XSD");
		this.erroriAttesiOpenSPCoopCore.add(err2);
	}
	
	
	
	
	
	
	
	
	
	

	



	/**
	 * Busta E-Gov presente nel messaggio di richiesta
	 * "OPENSPCOOP_ORG_420";
	 */
	@Test(groups={RichiesteApplicativeScorrette.ID_GRUPPO,RichiesteApplicativeScorrette.ID_GRUPPO+".420"})
	public void TestBustaEGovInviataVersoPortaDelegata() throws Exception{

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
				
				Utilities.verificaFaultIntegrazione(error, 
						CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE.getCodicePorta(),"RicezioneContenutiApplicativiSOAP", 
						Utilities.toString(CodiceErroreIntegrazione.CODICE_420_BUSTA_PRESENTE_RICHIESTA_APPLICATIVA), 
						CostantiErroriIntegrazione.MSG_420_BUSTA_PRESENTE_RICHIESTA_APPLICATIVA, Utilities.CONTROLLO_DESCRIZIONE_TRAMITE_METODO_EQUALS);	
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
	 * Il messaggio di richiesta utilizzato con IM per invocare la Porta Delegata non rispetta il formato SOAP
	 * "OPENSPCOOP_ORG_421";
	 */
	@Test(groups={RichiesteApplicativeScorrette.ID_GRUPPO,RichiesteApplicativeScorrette.ID_GRUPPO+".421"})
	public void testInvioMessaggioNonSOAPConXML() throws Exception{
		
		try{
			
			Reporter.log("Effettuo invocazione per riferimento");
			org.openspcoop2.pdd.services.axis14.PD_PortType im = 
				IntegrationManager.getIntegrationManagerPD_axis14(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore().replace("PD","IntegrationManager/PD"),
						"adminSilX", "123456");
			IntegrationManagerMessage msg = new IntegrationManagerMessage();
			msg.setMessage("CONTENUTO_ERRATO".getBytes());
			im.invocaPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_AUTENTICAZIONE_BASIC, msg);
			
			throw new Exception("Metodo invocaPortaDelegataPerRiferimento non ha causato errori");
		}catch(IntegrationManagerException e){
			verificaSPCoopException_421(e);
		}
			
	}
	private void verificaSPCoopException_421(IntegrationManagerException e) throws Exception{
		Reporter.log("Codice errore ["+e.getCodiceEccezione()+"]");
		Reporter.log("confronto con ["+Utilities.toString(CodiceErroreIntegrazione.CODICE_421_MSG_SOAP_NON_PRESENTE_RICHIESTA_APPLICATIVA)+"]");
		Assert.assertTrue(Utilities.toString(CodiceErroreIntegrazione.CODICE_421_MSG_SOAP_NON_PRESENTE_RICHIESTA_APPLICATIVA).equals(e.getCodiceEccezione()));
			
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
		Reporter.log("confronto con ["+CostantiErroriIntegrazione.MSG_421_MSG_SOAP_NON_PRESENTE_RICHIESTA_APPLICATIVA+"]");
		Assert.assertTrue(e.getDescrizioneEccezione().contains(CostantiErroriIntegrazione.MSG_421_MSG_SOAP_NON_PRESENTE_RICHIESTA_APPLICATIVA));
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * Il messaggio di richiesta utilizzato con il tunnel SOAP e  con IM per invocare la Porta Delegata non e' imbustabile
	 * "OPENSPCOOP_ORG_422";
	 */
	@Test(groups={RichiesteApplicativeScorrette.ID_GRUPPO,RichiesteApplicativeScorrette.ID_GRUPPO+".422"})
	public void testInvioMessaggioTramiteTunnelSOAP_nonImbustabileInSOAP() throws Exception{
		
		Date dataInizioTest = DateManager.getDate();
		
		DatabaseComponent dbComponentFruitore = null;
		DatabaseComponent dbComponentErogatore = null;

		String portaDelegata = CostantiTestSuite.PORTA_DELEGATA_PROFILO_SINCRONO;
		
		try{
			
			ClientHttpGenerico client=new ClientHttpGenerico(new Repository());
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore().replace("PD", "PDtoSOAP"));
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
				
				Assert.assertTrue(client.getCodiceStatoHTTP()==500);
				
				byte [] xmlErroreApplicativo = client.getMessaggioXMLRisposta();
				Assert.assertTrue(xmlErroreApplicativo!=null);
				
				Utilities.verificaErroreApplicativoCnipa(org.openspcoop2.message.XMLUtils.getInstance().newElement(xmlErroreApplicativo),
						Utilities.testSuiteProperties.getIdentitaDefault_dominio(),"RicezioneContenutiApplicativiHTTP", 
						Utilities.toString(CodiceErroreIntegrazione.CODICE_422_IMBUSTAMENTO_SOAP_NON_RIUSCITO_RICHIESTA_APPLICATIVA), 
						CostantiErroriIntegrazione.MSG_422_IMBUSTAMENTO_SOAP_NON_RIUSCITO_RICHIESTA_APPLICATIVA, Utilities.CONTROLLO_DESCRIZIONE_TRAMITE_METODO_CONTAINS);
				
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
	
	@Test(groups={RichiesteApplicativeScorrette.ID_GRUPPO,RichiesteApplicativeScorrette.ID_GRUPPO+".422"},
			dependsOnMethods="testInvioMessaggioTramiteTunnelSOAP_nonImbustabileInSOAP")
	public void testInvioMessaggioTramiteIM_nonImbustabileInSOAP() throws Exception{
		
		try{
			
			Reporter.log("Effettuo invocazione per riferimento");
			org.openspcoop2.pdd.services.axis14.PD_PortType im = 
				IntegrationManager.getIntegrationManagerPD_axis14(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore().replace("PD","IntegrationManager/PD"),
						"adminSilX", "123456");
			IntegrationManagerMessage msg = new IntegrationManagerMessage();
			msg.setImbustamento(true);
			msg.setMessage("<CONTENUTO_ERRATO".getBytes());
			im.invocaPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_AUTENTICAZIONE_BASIC, msg);
			
			throw new Exception("Metodo imbustaMessaggio non ha causato errori");
		}catch(IntegrationManagerException e){
			verificaSPCoopException_422(e);
		}
			
	}
	private void verificaSPCoopException_422(IntegrationManagerException e) throws Exception{
		Reporter.log("Codice errore ["+e.getCodiceEccezione()+"]");
		Reporter.log("confronto con ["+Utilities.toString(CodiceErroreIntegrazione.CODICE_422_IMBUSTAMENTO_SOAP_NON_RIUSCITO_RICHIESTA_APPLICATIVA)+"]");
		Assert.assertTrue(Utilities.toString(CodiceErroreIntegrazione.CODICE_422_IMBUSTAMENTO_SOAP_NON_RIUSCITO_RICHIESTA_APPLICATIVA).equals(e.getCodiceEccezione()));
			
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
		Reporter.log("confronto con ["+CostantiErroriIntegrazione.MSG_422_IMBUSTAMENTO_SOAP_NON_RIUSCITO_RICHIESTA_APPLICATIVA+"]");
		Assert.assertTrue(e.getDescrizioneEccezione().contains(CostantiErroriIntegrazione.MSG_422_IMBUSTAMENTO_SOAP_NON_RIUSCITO_RICHIESTA_APPLICATIVA));
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * Servizio SPCoop invocato con azione non corretta
	 * "OPENSPCOOP_ORG_423";
	 */
	@Test(groups={RichiesteApplicativeScorrette.ID_GRUPPO,RichiesteApplicativeScorrette.ID_GRUPPO+".423"})
	public void testInvocazioneServizioSenzaAzione() throws Exception{
		DatabaseComponent dbComponentFruitore = null;
		DatabaseComponent dbComponentErogatore = null;

		try{
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
				
				Utilities.verificaFaultIntegrazione(error, 
						CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE.getCodicePorta(),"RicezioneContenutiApplicativiSOAP", 
						Utilities.toString(CodiceErroreIntegrazione.CODICE_423_SERVIZIO_CON_AZIONE_SCORRETTA), 
						CostantiErroriIntegrazione.MSG_423_SERVIZIO_CON_AZIONE_SCORRETTA, Utilities.CONTROLLO_DESCRIZIONE_TRAMITE_METODO_CONTAINS);	
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
	 * Funzione "Allega Body" non riuscita sul messaggio di richiesta
	 * "OPENSPCOOP_ORG_424";
	 */
	@Test(groups={RichiesteApplicativeScorrette.ID_GRUPPO,RichiesteApplicativeScorrette.ID_GRUPPO+".424"})
	public void testAllegaBodyNonRiuscito() throws Exception{
		DatabaseComponent dbComponentFruitore = null;
		DatabaseComponent dbComponentErogatore = null;

		try{
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
				
				Utilities.verificaFaultIntegrazione(error, 
						CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE.getCodicePorta(),"RicezioneContenutiApplicativiSOAP", 
						Utilities.toString(CodiceErroreIntegrazione.CODICE_424_ALLEGA_BODY), 
						CostantiErroriIntegrazione.MSG_424_ALLEGA_BODY, Utilities.CONTROLLO_DESCRIZIONE_TRAMITE_METODO_CONTAINS);	
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
	 * Funzione "Scarta Body" non riuscita sul messaggio di richiesta
	 * "OPENSPCOOP_ORG_425";
	 */
	@Test(groups={RichiesteApplicativeScorrette.ID_GRUPPO,RichiesteApplicativeScorrette.ID_GRUPPO+".425"})
	public void testScartaBodyNonRiuscito() throws Exception{
		DatabaseComponent dbComponentFruitore = null;
		DatabaseComponent dbComponentErogatore = null;

		try{
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
				
				Utilities.verificaFaultIntegrazione(error, 
						CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE.getCodicePorta(),"RicezioneContenutiApplicativiSOAP", 
						Utilities.toString(CodiceErroreIntegrazione.CODICE_425_SCARTA_BODY), 
						CostantiErroriIntegrazione.MSG_425_SCARTA_BODY, Utilities.CONTROLLO_DESCRIZIONE_TRAMITE_METODO_CONTAINS);	
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
	 * Errore di processamento SOAP del messaggio di richiesta 
	 * "OPENSPCOOP_ORG_426";
	 */
	@Test(groups={RichiesteApplicativeScorrette.ID_GRUPPO,RichiesteApplicativeScorrette.ID_GRUPPO+".426"})
	public void testSoapEngineFallito_errore_processamento() throws Exception{
		
		Date dataInizioTest = DateManager.getDate();
		
		DatabaseComponent dbComponentFruitore = null;
		DatabaseComponent dbComponentErogatore = null;

		try{
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
				
				Utilities.verificaFaultIntegrazione(error, 
						Utilities.testSuiteProperties.getIdentitaDefault_dominio(),"RicezioneContenutiApplicativiSOAP", 
						Utilities.toString(CodiceErroreIntegrazione.CODICE_426_SERVLET_ERROR), 
						CostantiErroriIntegrazione.MSG_426_SERVLET_REQUEST_ERROR, Utilities.CONTROLLO_DESCRIZIONE_TRAMITE_METODO_CONTAINS);	
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
	 * "OPENSPCOOP_ORG_427";
	 */
	@Test(groups={RichiesteApplicativeScorrette.ID_GRUPPO,RichiesteApplicativeScorrette.ID_GRUPPO+".427"})
	public void testMustUnderstad() throws Exception{
		DatabaseComponent dbComponentFruitore = null;
		DatabaseComponent dbComponentErogatore = null;

		try{
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
				
				Utilities.verificaFaultIntegrazione(error, 
						Utilities.testSuiteProperties.getIdentitaDefault_dominio(),"RicezioneContenutiApplicativiSOAP", 
						Utilities.toString(CodiceErroreIntegrazione.CODICE_427_MUSTUNDERSTAND_ERROR), 
						CostantiErroriIntegrazione.MSG_427_MUSTUNDERSTAND_ERROR, Utilities.CONTROLLO_DESCRIZIONE_TRAMITE_METODO_CONTAINS);	
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
	 * Autorizzazione basata sul contenuto fallita
	 * "OPENSPCOOP_ORG_428";
	 */
	@Test(groups={RichiesteApplicativeScorrette.ID_GRUPPO,RichiesteApplicativeScorrette.ID_GRUPPO+".428"})
	public void testAutorizzazioneContenutoKO() throws Exception{
		DatabaseComponent dbComponentFruitore = null;
		DatabaseComponent dbComponentErogatore = null;

		try{
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
				
				Utilities.verificaFaultIntegrazione(error, 
						CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE.getCodicePorta(),"RicezioneContenutiApplicativiSOAP", 
						Utilities.toString(CodiceErroreIntegrazione.CODICE_428_AUTORIZZAZIONE_CONTENUTO_FALLITA), 
						CostantiErroriIntegrazione.MSG_428_AUTORIZZAZIONE_CONTENUTO_FALLITA, Utilities.CONTROLLO_DESCRIZIONE_TRAMITE_METODO_CONTAINS);	
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
	 * L'header HTTP riporta un Content-Type non previsto in SOAP 1.1
	 * "OPENSPCOOP_ORG_429";
	 */
	@Test(groups={RichiesteApplicativeScorrette.ID_GRUPPO,RichiesteApplicativeScorrette.ID_GRUPPO+".429"})
	public void testContentTypeErrato() throws Exception{
		DatabaseComponent dbComponentFruitore = null;
		DatabaseComponent dbComponentErogatore = null;

		try{
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
				
				Utilities.verificaFaultIntegrazione(error, 
						Utilities.testSuiteProperties.getIdentitaDefault_dominio(),"RicezioneContenutiApplicativiSOAP", 
						Utilities.toString(CodiceErroreIntegrazione.CODICE_429_CONTENT_TYPE_NON_SUPPORTATO), 
						CostantiErroriIntegrazione.MSG_429_CONTENT_TYPE_NON_SUPPORTATO.replace(CostantiErroriIntegrazione.MSG_429_CONTENT_TYPE_KEY, "application/soap+xml"), 
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
	
	
	
	
	
	
	
	
	
	/**
	 * Envelope con Namespace non previsto in SOAP 1.1
	 * "OPENSPCOOP_ORG_430";
	 */
	@Test(groups={RichiesteApplicativeScorrette.ID_GRUPPO,RichiesteApplicativeScorrette.ID_GRUPPO+".430"})
	public void testNamespaceEnvelopeErrato() throws Exception{
		
		Date dataInizioTest = DateManager.getDate();
		
		DatabaseComponent dbComponentFruitore = null;
		DatabaseComponent dbComponentErogatore = null;

		try{
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
					Utilities.verificaFaultIntegrazione(error, 
							Utilities.testSuiteProperties.getIdentitaDefault_dominio(),"RicezioneContenutiApplicativiSOAP", 
							Utilities.toString(CodiceErroreIntegrazione.CODICE_430_SOAP_ENVELOPE_NAMESPACE_ERROR), 
							CostantiErroriIntegrazione.MSG_430_SOAP_ENVELOPE_NAMESPACE_ERROR.replace(CostantiErroriIntegrazione.MSG_430_NAMESPACE_KEY, "http://www.w3.org/2003/05/soap-envelope"), 
								Utilities.CONTROLLO_DESCRIZIONE_TRAMITE_METODO_EQUALS);	
				}catch(Throwable e){
					// Provo a vedere se sono in un caso dove il framework non e' riuscito a comprendere il namespace del soap envelope
					Reporter.log("Verifica con [http://www.w3.org/2003/05/soap-envelope] fallita: "+e.getMessage());
					Reporter.log("Verifica con [Impossibile recuperare il valore del namespace] ...");
					Utilities.verificaFaultIntegrazione(error, 
							Utilities.testSuiteProperties.getIdentitaDefault_dominio(),"RicezioneContenutiApplicativiSOAP", 
							Utilities.toString(CodiceErroreIntegrazione.CODICE_430_SOAP_ENVELOPE_NAMESPACE_ERROR), 
							CostantiErroriIntegrazione.MSG_430_SOAP_ENVELOPE_NAMESPACE_ERROR.replace(CostantiErroriIntegrazione.MSG_430_NAMESPACE_KEY, "Impossibile recuperare il valore del namespace"), 
								Utilities.CONTROLLO_DESCRIZIONE_TRAMITE_METODO_EQUALS);	
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
		}
		
		
		Date dataFineTest = DateManager.getDate();
		
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
	 * "OPENSPCOOP_ORG_431";
	 **/
	Repository repositoryLetturaCredenzialeERRORE_CONFIGURAZIONE_PD=new Repository();
	Date dataLetturaCredenzialeERRORE_CONFIGURAZIONE_PD = null;
	@Test(groups={RichiesteApplicativeScorrette.ID_GRUPPO,RichiesteApplicativeScorrette.ID_GRUPPO+".431"})
	public void testLetturaCredenzialeERRORE_CONFIGURAZIONE_PD() throws TestSuiteException, Exception{

		Date dataInizioTest = DateManager.getDate();
		
		this.dataLetturaCredenzialeERRORE_CONFIGURAZIONE_PD = new Date();
		
		java.io.FileInputStream fin = null;
		DatabaseComponent dbComponentFruitore = null;
		try{
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
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				Reporter.log("Controllo actor code [OpenSPCoop]");
				Assert.assertTrue(CostantiPdD.OPENSPCOOP2.equals(error.getFaultActor()));
				Reporter.log("Controllo fault code ["+Utilities.toString(CodiceErroreIntegrazione.CODICE_431_GESTORE_CREDENZIALI_ERROR)+"]");
				Assert.assertTrue(Utilities.toString(CodiceErroreIntegrazione.CODICE_431_GESTORE_CREDENZIALI_ERROR).equals(error.getFaultCode().getLocalPart()));
				
				String msgErrore = CostantiErroriIntegrazione.MSG_431_GESTORE_CREDENZIALI_ERROR.replace(CostantiErroriIntegrazione.MSG_431_TIPO_GESTORE_CREDENZIALI_KEY, "testOpenSPCoop2");
				msgErrore = msgErrore+ "Eccezione, di configurazione, richiesta dalla testsuite";
				Reporter.log("Controllo fault string ["+msgErrore+"]");
				Assert.assertTrue(msgErrore.equals(error.getFaultString()));
				
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
	 * "OPENSPCOOP_ORG_432";
	 */
	@Test(groups={RichiesteApplicativeScorrette.ID_GRUPPO,RichiesteApplicativeScorrette.ID_GRUPPO+".432"})
	public void testSoapEngineFallito_ricostruzioneMessaggioNonRiuscito() throws Exception{
		
		Date dataInizioTest = DateManager.getDate();
		
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
				
				if(Utilities.toString(CodiceErroreIntegrazione.CODICE_432_PARSING_EXCEPTION_RICHIESTA).equals(error.getFaultCode().getLocalPart()))
					Utilities.verificaFaultIntegrazione(error, 
							Utilities.testSuiteProperties.getIdentitaDefault_dominio(),"RicezioneContenutiApplicativiSOAP", 
							Utilities.toString(CodiceErroreIntegrazione.CODICE_432_PARSING_EXCEPTION_RICHIESTA), 
							CostantiErroriIntegrazione.MSG_432_MESSAGGIO_XML_MALFORMATO_RICHIESTA, Utilities.CONTROLLO_DESCRIZIONE_TRAMITE_METODO_CONTAINS);	
				else Assert.assertTrue(false,"FaultCode non tra quelli attesi (432): " + error.getFaultCode().getLocalPart());
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
	 * "OPENSPCOOP_ORG_433";
	 */
	// LA LIBRERIA CLIENT HTTP GENERICO NON PERMETTE UNA INVOCAZIONE SENZA CONTENT TYPE
//	@Test(groups={RichiesteApplicativeScorrette.ID_GRUPPO,RichiesteApplicativeScorrette.ID_GRUPPO+".433"})
//	public void testContentTypeNonPresente() throws Exception{
//		
//		
//		DatabaseComponent dbComponentFruitore = null;
//		DatabaseComponent dbComponentErogatore = null;
//
//		try{
//			ClientHttpGenerico client=new ClientHttpGenerico(new Repository());
//			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
//			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_PROFILO_SINCRONO);
//			client.connectToSoapEngine();
//			// NOTA: invio un messaggio malformato forzando il test in modo da mangare un soap with attachments come fosse un messaggio senza attachments
//			client.setMessageFromFile(Utilities.testSuiteProperties.getSoap11WithAttachmentsFileName(), false);
//			//client.setMessaggioXMLRichiesta("CONTENTUO_ERRATO".getBytes()); QUESTO non rilancia il soap fault
//			client.setRispostaDaGestire(true);
//			// AttesaTerminazioneMessaggi
//			
//			client.setContentType("");
//			
//			if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()){
//				dbComponentFruitore = DatabaseProperties.getDatabaseComponentFruitore();
//				dbComponentErogatore = DatabaseProperties.getDatabaseComponentErogatore();
//
//				client.setAttesaTerminazioneMessaggi(true);
//				client.setDbAttesaTerminazioneMessaggiFruitore(dbComponentFruitore);
//				client.setDbAttesaTerminazioneMessaggiErogatore(dbComponentErogatore);
//			}
//			try {
//				client.run();
//
//				Reporter.log("Invocazione porta delegata (433) (PortaDelegata: "+CostantiTestSuite.PORTA_DELEGATA_PROFILO_SINCRONO+") non ha causato errori.");
//				throw new TestSuiteException("Invocazione porta delegata (433) (PortaDelegata: "+CostantiTestSuite.PORTA_DELEGATA_PROFILO_SINCRONO+") non ha causato errori.");
//			} catch (AxisFault error) {
//				
//				Assert.assertTrue(client.getCodiceStatoHTTP()==500);
//				
//				if(Utilities.toString(CodiceErroreIntegrazione.CODICE_433_CONTENT_TYPE_NON_PRESENTE).equals(error.getFaultCode().getLocalPart()))
//					Utilities.verificaFaultIntegrazione(error, 
//							Utilities.testSuiteProperties.getIdentitaDefault_dominio(),"RicezioneContenutiApplicativiSOAP", 
//							Utilities.toString(CodiceErroreIntegrazione.CODICE_433_CONTENT_TYPE_NON_PRESENTE), 
//							CostantiErroriIntegrazione.MSG_433_CONTENT_TYPE_NON_PRESENTE, Utilities.CONTROLLO_DESCRIZIONE_TRAMITE_METODO_CONTAINS);	
//				else Assert.assertTrue(false,"FaultCode non tra quelli attesi (433): " + error.getFaultCode().getLocalPart());
//			}finally{
//				dbComponentFruitore.close();
//				dbComponentErogatore.close();
//			}
//		}catch(Exception e){
//			throw e;
//		}finally{
//			dbComponentFruitore.close();
//			dbComponentErogatore.close();
//		}
//		
//	}
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * Errore di processamento correlazione applicativa risposta
	 * "OPENSPCOOP_ORG_434";
	 */
	@Test(groups={RichiesteApplicativeScorrette.ID_GRUPPO,RichiesteApplicativeScorrette.ID_GRUPPO+".434"})
	public void testCorrelazioneApplicativaRispostaErrata() throws Exception{
		
		Date dataInizioTest = DateManager.getDate();
		
		DatabaseComponent dbComponentFruitore = null;
		DatabaseComponent dbComponentErogatore = null;

		java.io.FileInputStream fin = null;
		try{
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
				
				if(Utilities.toString(CodiceErroreIntegrazione.CODICE_434_CORRELAZIONE_APPLICATIVA_RISPOSTA_ERRORE).equals(error.getFaultCode().getLocalPart()))
					Utilities.verificaFaultIntegrazione(error, CostantiTestSuite.SPCOOP_IDPORTA_SOGGETTO_FRUITORE,InoltroBuste.ID_MODULO, 
							Utilities.toString(CodiceErroreIntegrazione.CODICE_434_CORRELAZIONE_APPLICATIVA_RISPOSTA_ERRORE), 
							CostantiErroriIntegrazione.MSG_434_CORRELAZIONE_APPLICATIVA_RISPOSTA_ERRORE, Utilities.CONTROLLO_DESCRIZIONE_TRAMITE_METODO_CONTAINS);	
				else Assert.assertTrue(false,"FaultCode non tra quelli attesi (434): " + error.getFaultCode().getLocalPart());
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
		}
		
		
		Date dataFineTest = DateManager.getDate();
		
		ErroreAttesoOpenSPCoopLogCore err = new ErroreAttesoOpenSPCoopLogCore();
		err.setIntervalloInferiore(dataInizioTest);
		err.setIntervalloSuperiore(dataFineTest);
		err.setMsgErrore("nessun match trovato per l'espressione xpath [//test:idApplicativoERRATO/text()]");
		this.erroriAttesiOpenSPCoopCore.add(err);
	}
	
	
	
	
	
	
	
	
	
	
	
	/***
	 * LocalForward configErrata
	 * "OPENSPCOOP_ORG_435";
	 */
	@Test(groups={RichiesteApplicativeScorrette.ID_GRUPPO,RichiesteApplicativeScorrette.ID_GRUPPO+".435"})
	public void localForward_invokePD_ASINCRONI() throws TestSuiteException, Exception{
		DatabaseComponent dbComponentFruitore = null;
		DatabaseComponent dbComponentErogatore = null;

		try{
			
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
				
				String infoServizio = "( Servizio v1 SPC/RichiestaStatoAvanzamentoAsincronoAsimmetrico Azione richiestaAsincrona Erogatore SPC/MinisteroErogatore )";
				String msgErrore = CostantiErroriIntegrazione.MSG_435_LOCAL_FORWARD_CONFIG_ERRORE+ infoServizio+" profilo di collaborazione AsincronoAsimmetrico non supportato";
				
				if(Utilities.toString(CodiceErroreIntegrazione.CODICE_435_LOCAL_FORWARD_CONFIG_ERROR).equals(error.getFaultCode().getLocalPart()))
					Utilities.verificaFaultIntegrazione(error, 
							 CostantiTestSuite.SPCOOP_IDPORTA_SOGGETTO_FRUITORE,"RicezioneContenutiApplicativiSOAP", 
							Utilities.toString(CodiceErroreIntegrazione.CODICE_435_LOCAL_FORWARD_CONFIG_ERROR), 
							msgErrore, Utilities.CONTROLLO_DESCRIZIONE_TRAMITE_METODO_EQUALS);	
				else Assert.assertTrue(false,"FaultCode non tra quelli attesi (435): " + error.getFaultCode().getLocalPart());
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
		}
	}
	
	
	
	/*
	
	OPENSPCOOP2_ORG_436: Tipo del Soggetto Fruitore non supportato dal Protocollo

	OPENSPCOOP2_ORG_437: Tipo del Soggetto Erogatore non supportato dal Protocollo

	OPENSPCOOP2_ORG_438: Tipo di Servizio non supportato dal Protocollo

	OPENSPCOOP2_ORG_439: Funzionalità non supportato dal Protocollo (es. profiloAsincrono sul protocollo trasparente
	
	NON VERIFICABILI
	
	*/
	
	
	Repository repositoryStrutturaXMLBodyRispostaPdDErrato=new Repository();
	@Test(groups={RichiesteApplicativeScorrette.ID_GRUPPO,RichiesteApplicativeScorrette.ID_GRUPPO+".440"})
	public void strutturaXMLRispostaErrata()throws TestSuiteException,SOAPException, Exception{
		Date dataInizioTest = DateManager.getDate();

		// costruzione busta
		String xml = org.openspcoop2.utils.resources.FileSystemUtilities.readFile(Utilities.testSuiteProperties.getSoap11FileName());
		Message msg=new Message(new ByteArrayInputStream(xml.getBytes()));
		msg.getSOAPPartAsBytes();

		DatabaseComponent dbComponentErogatore = null;
		try{
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
				if(Utilities.toString(CodiceErroreIntegrazione.CODICE_440_PARSING_EXCEPTION_RISPOSTA).equals(error.getFaultCode().getLocalPart())){
					Reporter.log("Controllo fault code. Atteso ["+Utilities.toString(CodiceErroreIntegrazione.CODICE_440_PARSING_EXCEPTION_RISPOSTA)
						+"] - Trovato [" + error.getFaultCode().getLocalPart() + "]");
					Assert.assertTrue(Utilities.toString(CodiceErroreIntegrazione.CODICE_440_PARSING_EXCEPTION_RISPOSTA).equals(error.getFaultCode().getLocalPart()));
					String msgErrore = "Unexpected close tag </soapenv:Envelope>; expected </soapenv:Body>";
					String msgErrore2 = "Unexpected close tag";
					String msgErrore3 = "The element type \"soapenv:Body\" must be terminated by the matching end-tag \"</soapenv:Body>\"";
					Reporter.log("Controllo fault string. Trovato [" + error.getFaultString() + "]");
					Assert.assertTrue(error.getFaultString().contains(msgErrore) || error.getFaultString().contains(msgErrore2) || error.getFaultString().contains(msgErrore3));
				} else {
					Assert.assertTrue(false, "FaultCode non atteso");
				}

				Reporter.log("Controllo fault actor. Atteso [OpenSPCoop] - Trovato [" + error.getFaultActor() + "]");
				Assert.assertTrue(CostantiPdD.OPENSPCOOP2.equals(error.getFaultActor()));
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
