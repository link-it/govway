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



package org.openspcoop2.protocol.spcoop.testsuite.units.errori_applicativi;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Date;
import java.util.Vector;

import org.apache.axis.AxisFault;
import org.apache.axis.Message;
import org.openspcoop2.message.OpenSPCoop2MessageFactory;
import org.openspcoop2.message.constants.Costanti;
import org.openspcoop2.message.soap.SoapUtils;
import org.openspcoop2.protocol.sdk.constants.CodiceErroreCooperazione;
import org.openspcoop2.protocol.sdk.constants.CodiceErroreIntegrazione;
import org.openspcoop2.protocol.sdk.constants.CostantiProtocollo;
import org.openspcoop2.protocol.sdk.constants.IntegrationFunctionError;
import org.openspcoop2.protocol.spcoop.testsuite.core.CostantiErroriIntegrazione;
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
import org.openspcoop2.testsuite.core.TestSuiteProperties;
import org.openspcoop2.testsuite.db.DatabaseComponent;
import org.openspcoop2.testsuite.units.GestioneViaJmx;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.xml.XMLDiff;
import org.openspcoop2.utils.xml.XMLDiffImplType;
import org.openspcoop2.utils.xml.XMLDiffOptions;
import org.openspcoop2.utils.xml.XMLException;
import org.openspcoop2.utils.xml.XMLUtils;
import org.slf4j.Logger;
import org.testng.Assert;
import org.testng.Reporter;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Test su richieste applicative malformate indirizzate alla Porta di Dominio
 * 
 * @author Andi Rexha (rexha@openspcoop.org)
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ErroreApplicativoCNIPA extends GestioneViaJmx  {

	/** Identificativo del gruppo */
	public static final String ID_GRUPPO = "ErroreApplicativoCNIPA";


	private Logger log = SPCoopTestsuiteLogger.getInstance();
	
	private boolean genericCode = false;
	private boolean unwrap = false;
	
	protected ErroreApplicativoCNIPA(boolean genericCode, boolean unwrap) {
		super(org.openspcoop2.protocol.spcoop.testsuite.core.TestSuiteProperties.getInstance());
		this.genericCode = genericCode;
		this.unwrap = unwrap;
	}
	
	
	
	private Date dataAvvioGruppoTest = null;
	protected void _testOpenspcoopCoreLog_raccoltaTempoAvvioTest() throws Exception{
		this.dataAvvioGruppoTest = DateManager.getDate();
	} 	
	private Vector<ErroreAttesoOpenSPCoopLogCore> erroriAttesiOpenSPCoopCore = new Vector<ErroreAttesoOpenSPCoopLogCore>();
	protected void _testOpenspcoopCoreLog() throws Exception{
		if(this.erroriAttesiOpenSPCoopCore.size()>0){
			FileSystemUtilities.verificaOpenspcoopCore(this.dataAvvioGruppoTest,
					this.erroriAttesiOpenSPCoopCore.toArray(new ErroreAttesoOpenSPCoopLogCore[1]));
		}else{
			FileSystemUtilities.verificaOpenspcoopCore(this.dataAvvioGruppoTest);
		}
	} 
	
	
	private static boolean init = false;
	private static XMLDiff xmlDiff = null;
	private static synchronized void init() throws XMLException{
		if(init==false){
			xmlDiff = new XMLDiff();
			xmlDiff.initialize(XMLDiffImplType.XML_UNIT, new XMLDiffOptions());
			init = true;
		}
	}
	
	
	
	
	
	
	
	/** ERRORI 4XX */
	
	protected Object[][] _personalizzazioniErroriApplicativi4XX(){
		return new Object[][]{
				{null,null,"PORTA_DELEGATA_NON_ESISTENTE"},
				{"erroreApplicativoAsSoapFaultDefault","123456",CostantiTestSuite.PORTA_DELEGATA_ERRORE_APPLICATIVO_CNIPA},
				{"erroreApplicativoAsSoapFaultRidefinito","123456",CostantiTestSuite.PORTA_DELEGATA_ERRORE_APPLICATIVO_CNIPA},
				{"erroreApplicativoAsSoapXmlDefault","123456",CostantiTestSuite.PORTA_DELEGATA_ERRORE_APPLICATIVO_CNIPA},
				{"erroreApplicativoAsXmlRidefinito","123456",CostantiTestSuite.PORTA_DELEGATA_ERRORE_APPLICATIVO_CNIPA}
		};
	}
	
	protected Object _testErroriApplicativi4XX(String username,String password,String portaDelegata) throws Exception{

		DatabaseComponent dbComponentFruitore = null;
		DatabaseComponent dbComponentErogatore = null;
		Object response = null;
		
		Date dataInizioTest = null;
		Date dataFineTest = null;
		try{
			super.lockForCode(this.genericCode, this.unwrap);
			dataInizioTest = DateManager.getDate();
			
			ClientHttpGenerico client=new ClientHttpGenerico(new Repository());
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(portaDelegata);
			client.connectToSoapEngine();
			if(username!=null && password!=null)
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
			
			String actor = org.openspcoop2.testsuite.core.CostantiTestSuite.OPENSPCOOP2_INTEGRATION_ACTOR;
			String idPorta = "MinisteroFruitoreSPCoopIT";
			String codice = Utilities.toString(CodiceErroreIntegrazione.CODICE_423_SERVIZIO_CON_AZIONE_SCORRETTA);
			String msg = CostantiErroriIntegrazione.MSG_423_SERVIZIO_CON_AZIONE_NON_CORRETTA_PREFIX;
			boolean equalsMatch = false;
			
			ErroriProperties erroriProperties = ErroriProperties.getInstance(this.log);
			if(this.genericCode) {
				IntegrationFunctionError integrationFunctionError = IntegrationFunctionError.OPERATION_UNDEFINED;
				codice = erroriProperties.getErrorType_noWrap(integrationFunctionError);
				if(erroriProperties.isForceGenericDetails_noWrap(integrationFunctionError)) {
					msg = erroriProperties.getGenericDetails_noWrap(integrationFunctionError);
					equalsMatch = true;
				}
			}
			
			if(username==null){
				idPorta = Utilities.testSuiteProperties.getIdentitaDefault_dominio();
				codice = Utilities.toString(CodiceErroreIntegrazione.CODICE_401_PORTA_INESISTENTE);
				msg = CostantiErroriIntegrazione.MSG_401_PD_INESISTENTE;
				equalsMatch = false;
				if(this.genericCode) {
					IntegrationFunctionError integrationFunctionError = IntegrationFunctionError.API_OUT_UNKNOWN;
					codice = erroriProperties.getErrorType_noWrap(integrationFunctionError);
					if(erroriProperties.isForceGenericDetails_noWrap(integrationFunctionError)) {
						msg = erroriProperties.getGenericDetails_noWrap(integrationFunctionError);
						equalsMatch = true;
					}
				}
			}
			else if("erroreApplicativoAsSoapFaultRidefinito".equals(username) || "erroreApplicativoAsXmlRidefinito".equals(username)){
				if(!this.genericCode) {
					codice = "PREFIX_PERSONALIZZATO_423";
				}
				actor = "ACTOR_RIDEFINITO";
			}
			
			try {
				boolean responseAsXml = false;
				if("erroreApplicativoAsXmlRidefinito".equals(username) || "erroreApplicativoAsSoapXmlDefault".equals(username)) {
					responseAsXml = true;
				}
				if(responseAsXml) {
					client.setForceResponseAsBinaryProcessor(true);
				}
				
				client.run();

				if("erroreApplicativoAsSoapFaultDefault".equals(username) ||
						"erroreApplicativoAsSoapFaultRidefinito".equals(username)){
					Reporter.log("Invocazione porta delegata inesistente non ha causato errori.");
					throw new TestSuiteException("Invocazione porta delegata inesistente non ha causato errori.");
				}

				Reporter.log("Return Code '"+username+"' '"+client.getCodiceStatoHTTP()+"'");
				if(responseAsXml) {
					Assert.assertTrue(client.getCodiceStatoHTTP()==404);
				}
				else {
					Assert.assertTrue(client.getCodiceStatoHTTP()==200);
				}
				
				byte [] xmlErroreApplicativo = null;
				if(responseAsXml) {
					xmlErroreApplicativo = client.getMessaggioXMLRisposta();
					response = xmlErroreApplicativo;
					Assert.assertTrue(xmlErroreApplicativo!=null);
				}
				else {
					Message msgRisposta = client.getResponseMessage();
					Assert.assertTrue(msgRisposta!=null);
					Assert.assertTrue(msgRisposta.getSOAPBody()!=null);
					Assert.assertTrue(msgRisposta.getSOAPBody().hasChildNodes());
					
					try {
						xmlErroreApplicativo = org.openspcoop2.message.xml.MessageXMLUtils.DEFAULT.toByteArray(msgRisposta.getSOAPBody().getFirstChild(),true);
					}catch(Throwable t) {
						// normalize per conflito di librerie axis - saaj
						org.w3c.dom.Document d = org.openspcoop2.message.xml.MessageXMLUtils.DEFAULT.newDocument();
						Node n = d.importNode(msgRisposta.getSOAPBody().getFirstChild(), true);
						xmlErroreApplicativo = org.openspcoop2.message.xml.MessageXMLUtils.DEFAULT.toByteArray(n,true);
					}
					response = msgRisposta.getSOAPBody();
				}
				
				Utilities.verificaErroreApplicativoCnipa(org.openspcoop2.message.xml.MessageXMLUtils.DEFAULT.newElement(xmlErroreApplicativo), 
						idPorta,"RicezioneContenutiApplicativi", 
						codice, 
						msg, equalsMatch);	
				
			} catch (AxisFault error) {
				
				response = error;
				
				Assert.assertTrue(client.getCodiceStatoHTTP()==500);
								
				Utilities.verificaFaultIntegrazione(error, actor,
						idPorta,"RicezioneContenutiApplicativi", 
						codice, 
						msg, equalsMatch);				
			}finally{
				try {
					dbComponentFruitore.close();
				}catch(Exception eClose) {}
				try {
					dbComponentErogatore.close();
				}catch(Exception eClose) {}	
			}
		}catch(Exception e){
			throw e;
		}finally{
			try {
				dbComponentFruitore.close();
			}catch(Exception eClose) {}
			try {
				dbComponentErogatore.close();
			}catch(Exception eClose) {}
			
			super.unlockForCode(this.genericCode);
			dataFineTest = DateManager.getDate();
			
			// Aggiungo errori attesi
			ErroreAttesoOpenSPCoopLogCore err = new ErroreAttesoOpenSPCoopLogCore();
			err.setIntervalloInferiore(dataInizioTest);
			err.setIntervalloSuperiore(dataFineTest);
			err.setMsgErrore("La porta invocata non esiste porta["+portaDelegata+"] urlInvocazione[/govway/spcoop/out/"+portaDelegata+"]: verificare i parametri di accesso utilizzati");
			//System.out.println("CHECK ["+err.getMsgErrore()+"]");
			this.erroriAttesiOpenSPCoopCore.add(err);
			
		}	
				
		return response;
	}
	
	
	/** SERVIZIO TUNNEL SOAP: ERRORI 4XX */
	
	protected Object[][] _personalizzazioniErroriApplicativi4XXTunnelSOAP(){
		return new Object[][]{
				{null,null,"PORTA_DELEGATA_NON_ESISTENTE"},
				{"erroreApplicativoAsSoapFaultDefault","123456",CostantiTestSuite.PORTA_DELEGATA_ERRORE_APPLICATIVO_CNIPA},
				{"erroreApplicativoAsSoapFaultRidefinito","123456",CostantiTestSuite.PORTA_DELEGATA_ERRORE_APPLICATIVO_CNIPA},
				{"erroreApplicativoAsSoapXmlDefault","123456",CostantiTestSuite.PORTA_DELEGATA_ERRORE_APPLICATIVO_CNIPA},
				{"erroreApplicativoAsXmlRidefinito","123456",CostantiTestSuite.PORTA_DELEGATA_ERRORE_APPLICATIVO_CNIPA}
		};
	}
	
	protected void _testErroriApplicativi4XXTunnelSOAP(String username,String password,String portaDelegata) throws Exception{

		DatabaseComponent dbComponentFruitore = null;
		DatabaseComponent dbComponentErogatore = null;
		ByteArrayOutputStream bout = null;
		java.io.FileInputStream fin = null;
		
		Date dataInizioTest = null;
		Date dataFineTest = null;
		try{
			super.lockForCode(this.genericCode, this.unwrap);
			dataInizioTest = DateManager.getDate();

			fin = new java.io.FileInputStream(new File(Utilities.testSuiteProperties.getXmlSenzaSoapFileName()));
			bout = new ByteArrayOutputStream();
			byte[]bytes = new byte[2048];
			int letti = 0;
			while( (letti = fin.read(bytes)) != -1 ){
				bout.write(bytes, 0, letti);
			}
			bout.flush();
			bout.close();
			
			ClientHttpGenerico client=new ClientHttpGenerico(new Repository());
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore().replace("out", "out/xml2soap"));
			client.setPortaDelegata(portaDelegata);
			client.connectToSoapEngine();
			if(username!=null && password!=null)
				client.setAutenticazione(username,password);
			client.setMessaggioXMLRichiesta(bout.toByteArray());
			client.setRispostaDaGestire(true);
			// AttesaTerminazioneMessaggi
			if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()){
				dbComponentFruitore = DatabaseProperties.getDatabaseComponentFruitore();
				dbComponentErogatore = DatabaseProperties.getDatabaseComponentErogatore();

				client.setAttesaTerminazioneMessaggi(true);
				client.setDbAttesaTerminazioneMessaggiFruitore(dbComponentFruitore);
				client.setDbAttesaTerminazioneMessaggiErogatore(dbComponentErogatore);
			}
			
			String idPorta = "MinisteroFruitoreSPCoopIT";
			String codice = Utilities.toString(CodiceErroreIntegrazione.CODICE_423_SERVIZIO_CON_AZIONE_SCORRETTA);
			String msg = CostantiErroriIntegrazione.MSG_423_SERVIZIO_CON_AZIONE_NON_CORRETTA_PREFIX;
			boolean equalsMatch = true;
			
			ErroriProperties erroriProperties = ErroriProperties.getInstance(this.log);
			if(this.genericCode) {
				IntegrationFunctionError integrationFunctionError = IntegrationFunctionError.OPERATION_UNDEFINED;
				codice = erroriProperties.getErrorType_noWrap(integrationFunctionError);
				if(erroriProperties.isForceGenericDetails_noWrap(integrationFunctionError)) {
					msg = erroriProperties.getGenericDetails_noWrap(integrationFunctionError);
					equalsMatch = true;
				}
			}
			
			if(username==null){
				idPorta = Utilities.testSuiteProperties.getIdentitaDefault_dominio();
				codice = Utilities.toString(CodiceErroreIntegrazione.CODICE_401_PORTA_INESISTENTE);
				msg = CostantiErroriIntegrazione.MSG_401_PD_INESISTENTE;
				equalsMatch = false;
				if(this.genericCode) {
					IntegrationFunctionError integrationFunctionError = IntegrationFunctionError.API_OUT_UNKNOWN;
					codice = erroriProperties.getErrorType_noWrap(integrationFunctionError);
					if(erroriProperties.isForceGenericDetails_noWrap(integrationFunctionError)) {
						msg = erroriProperties.getGenericDetails_noWrap(integrationFunctionError);
						equalsMatch = true;
					}
				}
			}
			else if("erroreApplicativoAsSoapFaultRidefinito".equals(username) || "erroreApplicativoAsXmlRidefinito".equals(username)){
				if(!this.genericCode) {
					codice = "PREFIX_PERSONALIZZATO_423";
				}
			}
			
			try {
				boolean responseAsXml = true; // tunnel soap sono tutti errori xml in risposta
				if(responseAsXml) {
					client.setForceResponseAsBinaryProcessor(true);
				}
				
				client.run();

				Reporter.log("Return Code '"+username+"' '"+client.getCodiceStatoHTTP()+"'");
				Assert.assertTrue(client.getCodiceStatoHTTP()==404);
				
				byte [] xmlErroreApplicativo = client.getMessaggioXMLRisposta();
				Assert.assertTrue(xmlErroreApplicativo!=null);
				
				Utilities.verificaErroreApplicativoCnipa(org.openspcoop2.message.xml.MessageXMLUtils.DEFAULT.newElement(xmlErroreApplicativo), 
						idPorta,"RicezioneContenutiApplicativiHTTP", 
						codice, 
						msg, equalsMatch);	
								
			} catch (AxisFault error) {
				throw error;			
			}finally{
				try {
					dbComponentFruitore.close();
				}catch(Exception eClose) {}
				try {
					dbComponentErogatore.close();
				}catch(Exception eClose) {}	
			}
		}catch(Exception e){
			throw e;
		}finally{
			try{
				fin.close();
			}catch(Exception e){}
			try {
				dbComponentFruitore.close();
			}catch(Exception eClose) {}
			try {
				dbComponentErogatore.close();
			}catch(Exception eClose) {}
			
			super.unlockForCode(this.genericCode);
			dataFineTest = DateManager.getDate();
		}	
		
		// Aggiungo errori attesi
		ErroreAttesoOpenSPCoopLogCore err = new ErroreAttesoOpenSPCoopLogCore();
		err.setIntervalloInferiore(dataInizioTest);
		err.setIntervalloSuperiore(dataFineTest);
		err.setMsgErrore("La porta invocata non esiste porta["+portaDelegata+"] urlInvocazione[/govway/spcoop/out/xml2soap/"+portaDelegata+"]: verificare i parametri di accesso utilizzati");
		this.erroriAttesiOpenSPCoopCore.add(err);

	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/** ERRORI 5XX */
	
	protected Object[][] _personalizzazioniErroriApplicativi5XX(){
		return new Object[][]{
				{"erroreApplicativoAsSoapFaultDefault","123456",CostantiTestSuite.PORTA_DELEGATA_VERIFICA_ERRORE_PROCESSAMENTO_5XX},
				{"erroreApplicativoAsSoapFaultRidefinito","123456",CostantiTestSuite.PORTA_DELEGATA_VERIFICA_ERRORE_PROCESSAMENTO_5XX},
				{"erroreApplicativoAsSoapXmlDefault","123456",CostantiTestSuite.PORTA_DELEGATA_VERIFICA_ERRORE_PROCESSAMENTO_5XX},
				{"erroreApplicativoAsXmlRidefinito","123456",CostantiTestSuite.PORTA_DELEGATA_VERIFICA_ERRORE_PROCESSAMENTO_5XX}
		};
	}
	
	protected Object _testErroriApplicativi5XX(String username,String password,String portaDelegata) throws Exception{

		DatabaseComponent dbComponentFruitore = null;
		DatabaseComponent dbComponentErogatore = null;
		Object response = null;
		
		Date dataInizioTest = null;
		Date dataFineTest = null;
		try{
			super.lockForCode(this.genericCode, this.unwrap);
			dataInizioTest = DateManager.getDate();

			ClientHttpGenerico client=new ClientHttpGenerico(new Repository());
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(portaDelegata);
			client.connectToSoapEngine();
			if(username!=null && password!=null)
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
			
			String actor = org.openspcoop2.testsuite.core.CostantiTestSuite.OPENSPCOOP2_INTEGRATION_ACTOR;
			String idPorta = "MinisteroFruitoreSPCoopIT";
			String codice = Utilities.toString(CodiceErroreIntegrazione.CODICE_500_ERRORE_INTERNO);
			String msg = CostantiErroriIntegrazione.MSG_5XX_SISTEMA_NON_DISPONIBILE;
			boolean equalsMatch = true;
			
			ErroriProperties erroriProperties = ErroriProperties.getInstance(this.log);
			if(this.genericCode) {
				IntegrationFunctionError integrationFunctionError = IntegrationFunctionError.WRAP_503_INTERNAL_ERROR;
				if(this.unwrap) {
					integrationFunctionError = IntegrationFunctionError.INTERNAL_REQUEST_ERROR;
				}
				codice = erroriProperties.getErrorType_noWrap(integrationFunctionError);
				if(erroriProperties.isForceGenericDetails_noWrap(integrationFunctionError)) {
					msg = erroriProperties.getGenericDetails_noWrap(integrationFunctionError);
					equalsMatch = true;
				}
			}
			
			if("erroreApplicativoAsSoapFaultRidefinito".equals(username) || "erroreApplicativoAsXmlRidefinito".equals(username)){
				actor = "ACTOR_RIDEFINITO";
				
				if(!this.genericCode) {
					codice = "PREFIX_PERSONALIZZATO_504";
					msg = "processo di autorizzazione [testOpenSPCoop2] fallito, Autorizzazione fallita per verifica Errore Processamento (TestSuiteOpenSPCoop)";
				}
			}
			
			try {
				boolean responseAsXml = false;
				if("erroreApplicativoAsXmlRidefinito".equals(username) || "erroreApplicativoAsSoapXmlDefault".equals(username)) {
					responseAsXml = true;
				}
				if(responseAsXml) {
					client.setForceResponseAsBinaryProcessor(true);
				}
				
				client.run();

				if("erroreApplicativoAsSoapFaultDefault".equals(username) ||
						"erroreApplicativoAsSoapFaultRidefinito".equals(username)){
					Reporter.log("Invocazione porta delegata inesistente non ha causato errori.");
					throw new TestSuiteException("Invocazione porta delegata inesistente non ha causato errori.");
				}

				Reporter.log("Return Code '"+username+"' '"+client.getCodiceStatoHTTP()+"'");
				if(responseAsXml) {
					Assert.assertTrue(client.getCodiceStatoHTTP()==503);
				}
				else {
					Assert.assertTrue(client.getCodiceStatoHTTP()==200);
				}
				
				byte [] xmlErroreApplicativo = null;
				if(responseAsXml) {
					xmlErroreApplicativo = client.getMessaggioXMLRisposta();
					response = xmlErroreApplicativo;
					Assert.assertTrue(xmlErroreApplicativo!=null);
				}
				else {
				
					Message msgRisposta = client.getResponseMessage();
					Assert.assertTrue(msgRisposta!=null);
					Assert.assertTrue(msgRisposta.getSOAPBody()!=null);
					Assert.assertTrue(msgRisposta.getSOAPBody().hasChildNodes());
					
					try {
						xmlErroreApplicativo = org.openspcoop2.message.xml.MessageXMLUtils.DEFAULT.toByteArray(msgRisposta.getSOAPBody().getFirstChild(),true);
					}catch(Throwable t) {
						// normalize per conflito di librerie axis - saaj
						org.w3c.dom.Document d = org.openspcoop2.message.xml.MessageXMLUtils.DEFAULT.newDocument();
						Node n = d.importNode(msgRisposta.getSOAPBody().getFirstChild(), true);
						xmlErroreApplicativo = org.openspcoop2.message.xml.MessageXMLUtils.DEFAULT.toByteArray(n,true);
					}
					response = msgRisposta.getSOAPBody();
				
				}
				
				Utilities.verificaErroreApplicativoCnipa(org.openspcoop2.message.xml.MessageXMLUtils.DEFAULT.newElement(xmlErroreApplicativo), 
						idPorta,"RicezioneContenutiApplicativi", 
						codice, 
						msg, equalsMatch);	
				
			} catch (AxisFault error) {
				
				response = error;
				
				Assert.assertTrue(client.getCodiceStatoHTTP()==500);
								
				Utilities.verificaFaultIntegrazione(error, actor,
						idPorta,"RicezioneContenutiApplicativi", 
						codice, 
						msg, equalsMatch);				
			}finally{
				try {
					dbComponentFruitore.close();
				}catch(Exception eClose) {}
				try {
					dbComponentErogatore.close();
				}catch(Exception eClose) {}	
			}
		}catch(Exception e){
			throw e;
		}finally{
			try {
				dbComponentFruitore.close();
			}catch(Exception eClose) {}
			try {
				dbComponentErogatore.close();
			}catch(Exception eClose) {}

			super.unlockForCode(this.genericCode);
			dataFineTest = DateManager.getDate();
		}
		
		ErroreAttesoOpenSPCoopLogCore err = new ErroreAttesoOpenSPCoopLogCore();
		err.setIntervalloInferiore(dataInizioTest);
		err.setIntervalloSuperiore(dataFineTest);
		err.setMsgErrore("processo di autorizzazione [testOpenSPCoop2] fallito, Autorizzazione fallita per verifica Errore Processamento (TestSuiteOpenSPCoop)");
		this.erroriAttesiOpenSPCoopCore.add(err);
		
		return response;
	}
	
	
	/** SERVIZIO TUNNEL SOAP: ERRORI 5XX */
	
	protected Object[][] _personalizzazioniErroriApplicativi5XXTunnelSOAP(){
		return new Object[][]{
				{"erroreApplicativoAsSoapFaultDefault","123456",CostantiTestSuite.PORTA_DELEGATA_VERIFICA_ERRORE_PROCESSAMENTO_5XX},
				{"erroreApplicativoAsSoapFaultRidefinito","123456",CostantiTestSuite.PORTA_DELEGATA_VERIFICA_ERRORE_PROCESSAMENTO_5XX},
				{"erroreApplicativoAsSoapXmlDefault","123456",CostantiTestSuite.PORTA_DELEGATA_VERIFICA_ERRORE_PROCESSAMENTO_5XX},
				{"erroreApplicativoAsXmlRidefinito","123456",CostantiTestSuite.PORTA_DELEGATA_VERIFICA_ERRORE_PROCESSAMENTO_5XX}
		};
	}
	
	protected void _testErroriApplicativi5XXTunnelSOAP(String username,String password,String portaDelegata) throws Exception{

		DatabaseComponent dbComponentFruitore = null;
		DatabaseComponent dbComponentErogatore = null;
		ByteArrayOutputStream bout = null;
		java.io.FileInputStream fin = null;
		
		Date dataInizioTest = null;
		Date dataFineTest = null;
		try{
			super.lockForCode(this.genericCode, this.unwrap);
			dataInizioTest = DateManager.getDate();

			fin = new java.io.FileInputStream(new File(Utilities.testSuiteProperties.getXmlSenzaSoapFileName()));
			bout = new ByteArrayOutputStream();
			byte[]bytes = new byte[2048];
			int letti = 0;
			while( (letti = fin.read(bytes)) != -1 ){
				bout.write(bytes, 0, letti);
			}
			bout.flush();
			bout.close();
			
			ClientHttpGenerico client=new ClientHttpGenerico(new Repository());
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore().replace("out", "out/xml2soap"));
			client.setPortaDelegata(portaDelegata);
			client.connectToSoapEngine();
			if(username!=null && password!=null)
				client.setAutenticazione(username,password);
			client.setMessaggioXMLRichiesta(bout.toByteArray());
			client.setRispostaDaGestire(true);
			// AttesaTerminazioneMessaggi
			if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()){
				dbComponentFruitore = DatabaseProperties.getDatabaseComponentFruitore();
				dbComponentErogatore = DatabaseProperties.getDatabaseComponentErogatore();

				client.setAttesaTerminazioneMessaggi(true);
				client.setDbAttesaTerminazioneMessaggiFruitore(dbComponentFruitore);
				client.setDbAttesaTerminazioneMessaggiErogatore(dbComponentErogatore);
			}
			
			String idPorta = "MinisteroFruitoreSPCoopIT";
			String codice = Utilities.toString(CodiceErroreIntegrazione.CODICE_500_ERRORE_INTERNO);
			String msg = CostantiErroriIntegrazione.MSG_5XX_SISTEMA_NON_DISPONIBILE;
			boolean equalsMatch = true;
			
			ErroriProperties erroriProperties = ErroriProperties.getInstance(this.log);
			if(this.genericCode) {
				IntegrationFunctionError integrationFunctionError = IntegrationFunctionError.WRAP_503_INTERNAL_ERROR;
				if(this.unwrap) {
					integrationFunctionError = IntegrationFunctionError.INTERNAL_REQUEST_ERROR;
				}
				codice = erroriProperties.getErrorType_noWrap(integrationFunctionError);
				if(erroriProperties.isForceGenericDetails_noWrap(integrationFunctionError)) {
					msg = erroriProperties.getGenericDetails_noWrap(integrationFunctionError);
					equalsMatch = true;
				}
			}
			
			if("erroreApplicativoAsSoapFaultRidefinito".equals(username) || "erroreApplicativoAsXmlRidefinito".equals(username)){
				if(!this.genericCode) {
					codice = "PREFIX_PERSONALIZZATO_504";
					msg = "processo di autorizzazione [testOpenSPCoop2] fallito, Autorizzazione fallita per verifica Errore Processamento (TestSuiteOpenSPCoop)";
				}
			}
			
			try {
				boolean responseAsXml = true; // tunnel soap sono tutti errori xml in risposta
				if(responseAsXml) {
					client.setForceResponseAsBinaryProcessor(true);
				}
				
				client.run();

				Reporter.log("Return Code '"+username+"' '"+client.getCodiceStatoHTTP()+"'");
				Assert.assertTrue(client.getCodiceStatoHTTP()==503);
				
				byte [] xmlErroreApplicativo = client.getMessaggioXMLRisposta();
				Assert.assertTrue(xmlErroreApplicativo!=null);
				
				Utilities.verificaErroreApplicativoCnipa(org.openspcoop2.message.xml.MessageXMLUtils.DEFAULT.newElement(xmlErroreApplicativo), 
						idPorta,"RicezioneContenutiApplicativiHTTP", 
						codice, 
						msg, equalsMatch);	
								
			} catch (AxisFault error) {
				throw error;			
			}finally{
				try {
					dbComponentFruitore.close();
				}catch(Exception eClose) {}
				try {
					dbComponentErogatore.close();
				}catch(Exception eClose) {}	
			}
		}catch(Exception e){
			throw e;
		}finally{
			try{
				fin.close();
			}catch(Exception e){}
			try {
				dbComponentFruitore.close();
			}catch(Exception eClose) {}
			try {
				dbComponentErogatore.close();
			}catch(Exception eClose) {}
			
			super.unlockForCode(this.genericCode);
			dataFineTest = DateManager.getDate();
		}
		
		ErroreAttesoOpenSPCoopLogCore err = new ErroreAttesoOpenSPCoopLogCore();
		err.setIntervalloInferiore(dataInizioTest);
		err.setIntervalloSuperiore(dataFineTest);
		err.setMsgErrore("processo di autorizzazione [testOpenSPCoop2] fallito, Autorizzazione fallita per verifica Errore Processamento (TestSuiteOpenSPCoop)");
		this.erroriAttesiOpenSPCoopCore.add(err);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	

	
	
	/** SOAP FAULT APPLICATIVO ARRICCHITO */
	
	protected Object[][] _personalizzazioniFaultApplicativi(){
		return new Object[][]{
				{null},
				{"erroreApplicativoAsSoapFaultDefault"},
				{"erroreApplicativoAsSoapFaultRidefinito"},
				{"erroreApplicativoAsSoapXmlDefault"},
				{"erroreApplicativoAsXmlRidefinito"},
		};
	}
	protected Object _testFaultApplicativoArricchitoFAULTCNIPA_Default(String servizioApplicativoFruitore) throws Exception{

		DatabaseComponent dbComponentFruitore = null;
		DatabaseComponent dbComponentErogatore = null;
		String portaDelegata = CostantiTestSuite.PORTA_DELEGATA_SOAP_FAULT_SERVIZIO_APPLICATIVO_SINCRONO;
		Object response = null;
		
		try{
			super.lockForCode(this.genericCode, this.unwrap);
			
			ClientHttpGenerico client=new ClientHttpGenerico(new Repository());
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(portaDelegata);
			client.connectToSoapEngine();
			if(servizioApplicativoFruitore!=null){
				client.setProperty(TestSuiteProperties.getInstance().getServizioApplicativoTrasporto(), servizioApplicativoFruitore);
			}
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

				Reporter.log("Invocazione porta delegata inesistente non ha causato errori.");
				throw new TestSuiteException("Invocazione porta delegata inesistente non ha causato errori.");

			} catch (AxisFault error) {
				
				response = error;
				
				Assert.assertTrue(client.getCodiceStatoHTTP()==500);
				
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				Reporter.log("Controllo actor ["+null+"]");
				Assert.assertTrue(error.getFaultActor()==null);
				Reporter.log("Controllo fault code [Server.faultExample]");
				Assert.assertTrue("Server.faultExample".equals(error.getFaultCode().getLocalPart()));
				Reporter.log("Controllo fault string [Fault ritornato dalla servlet di esempio di OpenSPCoop]");
				Assert.assertTrue(error.getFaultString().equals("Fault ritornato dalla servlet di esempio di OpenSPCoop"));
				Reporter.log("Controllo details openspcoop example");
				Element [] details = error.getFaultDetails();
				Assert.assertTrue(details!=null);
				Assert.assertTrue(details.length>0);
				Element erroreDetailsOpenSPCoop = null;
				Reporter.log("Details presenti: "+details.length);
				for(int i=0; i<details.length; i++){
					Element detail = details[i];
					Reporter.log("Detail["+detail.getLocalName()+"]");
					if("detailEsempioOpenSPCoop".equals(detail.getLocalName())){
						erroreDetailsOpenSPCoop = detail;
						break;
					}
				}
				Assert.assertTrue(erroreDetailsOpenSPCoop!=null);
				Utilities.verificaDetailsOpenSPCoopExample(erroreDetailsOpenSPCoop);
				
				String codice = Utilities.toString(CodiceErroreIntegrazione.CODICE_516_CONNETTORE_UTILIZZO_CON_ERRORE);
				if("erroreApplicativoAsXmlRidefinito".equals(servizioApplicativoFruitore) || "erroreApplicativoAsSoapFaultRidefinito".equals(servizioApplicativoFruitore)){
					codice = "PREFIX_PERSONALIZZATO_516";
				}
				String msgErrore = CostantiErroriIntegrazione.MSG_516_SERVIZIO_APPLICATIVO_NON_DISPONIBILE;
				boolean equalsMatch = Utilities.CONTROLLO_DESCRIZIONE_TRAMITE_METODO_EQUALS;
								
				ErroriProperties erroriProperties = ErroriProperties.getInstance(this.log);
				if(this.genericCode) {
					IntegrationFunctionError integrationFunctionError = IntegrationFunctionError.SERVICE_UNAVAILABLE;
					codice = erroriProperties.getErrorType_noWrap(integrationFunctionError);
					if(erroriProperties.isForceGenericDetails_noWrap(integrationFunctionError)) {
						msgErrore = erroriProperties.getGenericDetails_noWrap(integrationFunctionError);
						equalsMatch = true;
					}
				}
				
				Reporter.log("Controllo xml errore applicativo cnipa definito nei details (Codice:"+codice+")");
				Utilities.verificaFaultDetailsRispettoErroreApplicativoCnipa(error,"MinisteroFruitoreSPCoopIT","InoltroBuste", 
						codice,	msgErrore, equalsMatch);				
			}finally{
				try {
					dbComponentFruitore.close();
				}catch(Exception eClose) {}
				try {
					dbComponentErogatore.close();
				}catch(Exception eClose) {}	
			}
		}catch(Exception e){
			throw e;
		}finally{
			try {
				dbComponentFruitore.close();
			}catch(Exception eClose) {}
			try {
				dbComponentErogatore.close();
			}catch(Exception eClose) {}
			
			super.unlockForCode(this.genericCode);
		}
		
		return response;
	}
	
	protected Object _testFaultApplicativoArricchitoFAULTCNIPA_FaultGeneratoConPrefixErrato(String servizioApplicativoFruitore) throws Exception{

		DatabaseComponent dbComponentFruitore = null;
		DatabaseComponent dbComponentErogatore = null;
		String portaDelegata = CostantiTestSuite.PORTA_DELEGATA_ERRORE_APPLICATIVO_SOAP_FAULT_CUSTOM+"/"+
				CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_SOAP_FAULT_SA_CON_PREFIX_ERRATO;

		init();
		
		Object response = null;
		
		try{
			super.lockForCode(this.genericCode, this.unwrap);
			
			OpenSPCoop2MessageFactory messageFactory = OpenSPCoop2MessageFactory.getDefaultMessageFactory();
			
			ClientHttpGenerico client=new ClientHttpGenerico(new Repository());
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(portaDelegata);
			client.connectToSoapEngine();
			if(servizioApplicativoFruitore!=null){
				client.setProperty(TestSuiteProperties.getInstance().getServizioApplicativoTrasporto(), servizioApplicativoFruitore);
			}
			byte [] xmlRichiesta = org.openspcoop2.utils.resources.FileSystemUtilities.readBytesFromFile(Utilities.testSuiteProperties.getSoap11FileName());
			client.setMessaggioXMLRichiesta(xmlRichiesta); // in modo da poter verificare a mano la risposta. Axis altrimenti in questi casi particolari non funziona
			
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

				byte[] responseByte = client.getMessaggioXMLRisposta();
				if(responseByte==null){
					throw new Exception("Risposta non ritornata");
				}
				
				// Risposta
				Element e = XMLUtils.getInstance().newElement(responseByte);
				//System.out.println("Ricevuto SOAP ["+e.getLocalName()+"] ["+e.getNamespaceURI()+"]");
				//System.out.println("RISPOSTA ["+XMLUtils.getInstance().toString(e)+"]");
				Assert.assertTrue(Costanti.SOAP_ENVELOPE_NAMESPACE.equals(e.getNamespaceURI()));
				Assert.assertTrue("Envelope".equals(e.getLocalName()));
				NodeList nL = e.getChildNodes();
				Node fault = null;
				for (int i = 0; i < nL.getLength(); i++) {
					Node n = nL.item(i);
					if(Costanti.SOAP_ENVELOPE_NAMESPACE.equals(n.getNamespaceURI()) && "Body".equals(n.getLocalName())){
						fault = SoapUtils.getFirstNotEmptyChildNode(messageFactory, n, false);
					}
				}

				Node faultSenzaDetails = fault.cloneNode(true);
				Node CNIPA = null;
				nL = faultSenzaDetails.getChildNodes();
				for (int i = 0; i < nL.getLength(); i++) {
					Node n = nL.item(i);
					if("detail".equals(n.getLocalName())){
						NodeList nDetail = n.getChildNodes();
						for (int j = 0; j < nDetail.getLength(); j++) {
							Node nDetailI = nDetail.item(j);
							if("http://www.cnipa.it/schemas/2003/eGovIT/Exception1_0/".equals(nDetailI.getNamespaceURI()) && 
									"MessaggioDiErroreApplicativo".equals(nDetailI.getLocalName())){
								CNIPA = nDetailI;
								break;
							}
						}
						if(CNIPA!=null){
							n.removeChild(CNIPA);
						}
					}
				}
				

				// Atteso
				byte [] xmlAtteso = org.openspcoop2.utils.resources.FileSystemUtilities.readBytesFromFile(Utilities.testSuiteProperties.getSoapTestSOAPScorretto_soapFaultPrefixErrato());
				Element eAtteso = XMLUtils.getInstance().newElement(xmlAtteso);
				nL = eAtteso.getChildNodes();
				Node faultAtteso = null;
				for (int i = 0; i < nL.getLength(); i++) {
					Node n = nL.item(i);
					if(Costanti.SOAP_ENVELOPE_NAMESPACE.equals(n.getNamespaceURI()) && "Body".equals(n.getLocalName())){
						faultAtteso = SoapUtils.getFirstNotEmptyChildNode(messageFactory, n, false);
					}
				}
				
				byte[] tmp = XMLUtils.getInstance().toByteArray(faultSenzaDetails);
				faultSenzaDetails = XMLUtils.getInstance().newElement(tmp);
				tmp = XMLUtils.getInstance().toByteArray(faultAtteso);
				faultAtteso = XMLUtils.getInstance().newElement(tmp);
				boolean diff = xmlDiff.diff(faultSenzaDetails, faultAtteso);
				if(!diff){
					System.out.println("FAULT ["+XMLUtils.getInstance().toString(faultSenzaDetails)+"]");
					System.out.println("FAULTAtteso ["+XMLUtils.getInstance().toString(faultAtteso)+"]");
					System.out.println("Diff: "+xmlDiff.getDifferenceDetails());
				}
				Assert.assertTrue(diff);

				String codice = Utilities.toString(CodiceErroreIntegrazione.CODICE_516_CONNETTORE_UTILIZZO_CON_ERRORE);
				if("erroreApplicativoAsXmlRidefinito".equals(servizioApplicativoFruitore) || "erroreApplicativoAsSoapFaultRidefinito".equals(servizioApplicativoFruitore)){
					codice = "PREFIX_PERSONALIZZATO_516";
				}
				String msgErrore = CostantiErroriIntegrazione.MSG_516_SERVIZIO_APPLICATIVO_NON_DISPONIBILE;
				boolean equalsMatch = Utilities.CONTROLLO_DESCRIZIONE_TRAMITE_METODO_EQUALS;
								
				ErroriProperties erroriProperties = ErroriProperties.getInstance(this.log);
				if(this.genericCode) {
					IntegrationFunctionError integrationFunctionError = IntegrationFunctionError.SERVICE_UNAVAILABLE;
					codice = erroriProperties.getErrorType_noWrap(integrationFunctionError);
					if(erroriProperties.isForceGenericDetails_noWrap(integrationFunctionError)) {
						msgErrore = erroriProperties.getGenericDetails_noWrap(integrationFunctionError);
						equalsMatch = true;
					}
				}
				
				Reporter.log("Controllo xml errore applicativo cnipa definito nei details (Codice:"+codice+")");
				Utilities.verificaErroreApplicativoCnipa(CNIPA,"MinisteroFruitoreSPCoopIT","InoltroBuste", 
						codice, msgErrore, equalsMatch);		

			}finally{
				try {
					dbComponentFruitore.close();
				}catch(Exception eClose) {}
				try {
					dbComponentErogatore.close();
				}catch(Exception eClose) {}	
			}
		}catch(Exception e){
			throw e;
		}finally{
			try {
				dbComponentFruitore.close();
			}catch(Exception eClose) {}
			try {
				dbComponentErogatore.close();
			}catch(Exception eClose) {}
			
			super.unlockForCode(this.genericCode);
		}
		return response;	
	}
	
	protected Object _testFaultApplicativoArricchitoFAULTCNIPA_FaultGeneratoConSenzaPrefix(String servizioApplicativoFruitore) throws Exception{

		DatabaseComponent dbComponentFruitore = null;
		DatabaseComponent dbComponentErogatore = null;
		String portaDelegata = CostantiTestSuite.PORTA_DELEGATA_ERRORE_APPLICATIVO_SOAP_FAULT_CUSTOM+"/"+
				CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_SOAP_FAULT_SA_SENZA_PREFIX;

		init();
		
		Object response = null;
		
		try{
			super.lockForCode(this.genericCode, this.unwrap);
			
			OpenSPCoop2MessageFactory messageFactory = OpenSPCoop2MessageFactory.getDefaultMessageFactory();
			
			ClientHttpGenerico client=new ClientHttpGenerico(new Repository());
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(portaDelegata);
			client.connectToSoapEngine();
			if(servizioApplicativoFruitore!=null){
				client.setProperty(TestSuiteProperties.getInstance().getServizioApplicativoTrasporto(), servizioApplicativoFruitore);
			}
			byte [] xmlRichiesta = org.openspcoop2.utils.resources.FileSystemUtilities.readBytesFromFile(Utilities.testSuiteProperties.getSoap11FileName());
			client.setMessaggioXMLRichiesta(xmlRichiesta); // in modo da poter verificare a mano la risposta. Axis altrimenti in questi casi particolari non funziona
			
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

				byte[] responseByte = client.getMessaggioXMLRisposta();
				if(responseByte==null){
					throw new Exception("Risposta non ritornata");
				}
				
				// Risposta
				Element e = XMLUtils.getInstance().newElement(responseByte);
				//System.out.println("Ricevuto SOAP ["+e.getLocalName()+"] ["+e.getNamespaceURI()+"]");
				//System.out.println("RISPOSTA ["+XMLUtils.getInstance().toString(e)+"]");
				Assert.assertTrue(Costanti.SOAP_ENVELOPE_NAMESPACE.equals(e.getNamespaceURI()));
				Assert.assertTrue("Envelope".equals(e.getLocalName()));
				NodeList nL = e.getChildNodes();
				Node fault = null;
				for (int i = 0; i < nL.getLength(); i++) {
					Node n = nL.item(i);
					if(Costanti.SOAP_ENVELOPE_NAMESPACE.equals(n.getNamespaceURI()) && "Body".equals(n.getLocalName())){
						fault = SoapUtils.getFirstNotEmptyChildNode(messageFactory, n, false);
					}
				}

				Node faultSenzaDetails = fault.cloneNode(true);
				Node CNIPA = null;
				nL = faultSenzaDetails.getChildNodes();
				for (int i = 0; i < nL.getLength(); i++) {
					Node n = nL.item(i);
					if("detail".equals(n.getLocalName())){
						NodeList nDetail = n.getChildNodes();
						for (int j = 0; j < nDetail.getLength(); j++) {
							Node nDetailI = nDetail.item(j);
							if("http://www.cnipa.it/schemas/2003/eGovIT/Exception1_0/".equals(nDetailI.getNamespaceURI()) && 
									"MessaggioDiErroreApplicativo".equals(nDetailI.getLocalName())){
								CNIPA = nDetailI;
								break;
							}
						}
						if(CNIPA!=null){
							n.removeChild(CNIPA);
						}
					}
				}
				

				// Atteso
				byte [] xmlAtteso = org.openspcoop2.utils.resources.FileSystemUtilities.readBytesFromFile(Utilities.testSuiteProperties.getSoapTestSOAPScorretto_soapFaultSenzaPrefix());
				Element eAtteso = XMLUtils.getInstance().newElement(xmlAtteso);
				nL = eAtteso.getChildNodes();
				Node faultAtteso = null;
				for (int i = 0; i < nL.getLength(); i++) {
					Node n = nL.item(i);
					if(Costanti.SOAP_ENVELOPE_NAMESPACE.equals(n.getNamespaceURI()) && "Body".equals(n.getLocalName())){
						faultAtteso = SoapUtils.getFirstNotEmptyChildNode(messageFactory, n, false);
					}
				}
				
				byte[] tmp = XMLUtils.getInstance().toByteArray(faultSenzaDetails);
				faultSenzaDetails = XMLUtils.getInstance().newElement(tmp);
				tmp = XMLUtils.getInstance().toByteArray(faultAtteso);
				faultAtteso = XMLUtils.getInstance().newElement(tmp);
				boolean diff = xmlDiff.diff(faultSenzaDetails, faultAtteso);
				if(!diff){
					System.out.println("FAULT ["+XMLUtils.getInstance().toString(faultSenzaDetails)+"]");
					System.out.println("FAULTAtteso ["+XMLUtils.getInstance().toString(faultAtteso)+"]");
					System.out.println("Diff: "+xmlDiff.getDifferenceDetails());
				}
				Assert.assertTrue(diff);

				String codice = Utilities.toString(CodiceErroreIntegrazione.CODICE_516_CONNETTORE_UTILIZZO_CON_ERRORE);
				if("erroreApplicativoAsXmlRidefinito".equals(servizioApplicativoFruitore) || "erroreApplicativoAsSoapFaultRidefinito".equals(servizioApplicativoFruitore)){
					codice = "PREFIX_PERSONALIZZATO_516";
				}
				String msgErrore = CostantiErroriIntegrazione.MSG_516_SERVIZIO_APPLICATIVO_NON_DISPONIBILE;
				boolean equalsMatch = Utilities.CONTROLLO_DESCRIZIONE_TRAMITE_METODO_EQUALS;
								
				ErroriProperties erroriProperties = ErroriProperties.getInstance(this.log);
				if(this.genericCode) {
					IntegrationFunctionError integrationFunctionError = IntegrationFunctionError.SERVICE_UNAVAILABLE;
					codice = erroriProperties.getErrorType_noWrap(integrationFunctionError);
					if(erroriProperties.isForceGenericDetails_noWrap(integrationFunctionError)) {
						msgErrore = erroriProperties.getGenericDetails_noWrap(integrationFunctionError);
						equalsMatch = true;
					}
				}
				
				Reporter.log("Controllo xml errore applicativo cnipa definito nei details (Codice:"+codice+")");
				Utilities.verificaErroreApplicativoCnipa(CNIPA,"MinisteroFruitoreSPCoopIT","InoltroBuste", 
						codice, msgErrore, equalsMatch);		

			}finally{
				try {
					dbComponentFruitore.close();
				}catch(Exception eClose) {}
				try {
					dbComponentErogatore.close();
				}catch(Exception eClose) {}	
			}
		}catch(Exception e){
			throw e;
		}finally{
			try {
				dbComponentFruitore.close();
			}catch(Exception eClose) {}
			try {
				dbComponentErogatore.close();
			}catch(Exception eClose) {}
			
			super.unlockForCode(this.genericCode);
		}
		return response;	
	}
	
	
	/** SERVIZIO TUNNEL SOAP: SOAP FAULT APPLICATIVO ARRICCHITO */
	
	protected Object[][] _personalizzazioniFaultApplicativiTunnelSOAP(){
		return new Object[][]{{null},
				{"erroreApplicativoAsSoapFaultDefault"},
				{"erroreApplicativoAsSoapFaultRidefinito"},
				{"erroreApplicativoAsSoapXmlDefault"},
				{"erroreApplicativoAsXmlRidefinito"},
		};
	}
	protected void _testFaultApplicativoArricchitoFAULTCNIPA_Default_TunnelSOAP(String servizioApplicativoFruitore) throws Exception{

		DatabaseComponent dbComponentFruitore = null;
		DatabaseComponent dbComponentErogatore = null;
		String portaDelegata = CostantiTestSuite.PORTA_DELEGATA_SOAP_FAULT_SERVIZIO_APPLICATIVO_SINCRONO;
		ByteArrayOutputStream bout = null;
		java.io.FileInputStream fin = null;
		
		try{
			super.lockForCode(this.genericCode, this.unwrap);
			
			OpenSPCoop2MessageFactory messageFactory = OpenSPCoop2MessageFactory.getDefaultMessageFactory();
			
			fin = new java.io.FileInputStream(new File(Utilities.testSuiteProperties.getXmlSenzaSoapFileName()));
			bout = new ByteArrayOutputStream();
			byte[]bytes = new byte[2048];
			int letti = 0;
			while( (letti = fin.read(bytes)) != -1 ){
				bout.write(bytes, 0, letti);
			}
			bout.flush();
			bout.close();
			
			ClientHttpGenerico client=new ClientHttpGenerico(new Repository());
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore().replace("out", "out/xml2soap"));
			client.setPortaDelegata(portaDelegata);
			client.connectToSoapEngine();
			if(servizioApplicativoFruitore!=null){
				client.setProperty(TestSuiteProperties.getInstance().getServizioApplicativoTrasporto(), servizioApplicativoFruitore);
			}
			client.setMessaggioXMLRichiesta(bout.toByteArray());
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

				Reporter.log("Invocazione porta delegata inesistente non ha causato errori.");
				
				Assert.assertTrue(client.getCodiceStatoHTTP()==500);
								
				byte [] xmlErroreApplicativo = client.getMessaggioXMLRisposta();
				Assert.assertTrue(xmlErroreApplicativo!=null);

				Element fault = org.openspcoop2.message.xml.MessageXMLUtils.getInstance(messageFactory).newElement(xmlErroreApplicativo);
				Assert.assertTrue("Fault".equals(fault.getLocalName()));
				Assert.assertTrue("http://schemas.xmlsoap.org/soap/envelope/".equals(fault.getNamespaceURI()));
				
				Reporter.log("Controllo actor ["+null+"]");
				Assert.assertTrue(fault.getElementsByTagName("faultactor")==null || fault.getElementsByTagName("faultactor").getLength()==0);
				
				Reporter.log("Controllo fault code [Server.faultExample]");
				Assert.assertTrue(fault.getElementsByTagName("faultcode")!=null);
				Assert.assertTrue(fault.getElementsByTagName("faultcode").getLength()==1);
				String code = fault.getElementsByTagName("faultcode").item(0).getTextContent();
				Reporter.log("Controllo fault code [Server.faultExample]: ["+code+"]");
				Assert.assertTrue(code!=null && ("Server.faultExample".equals(code) || code.endsWith(":Server.faultExample")));
				
				Reporter.log("Controllo fault string [Fault ritornato dalla servlet di esempio di OpenSPCoop]");
				Assert.assertTrue(fault.getElementsByTagName("faultstring")!=null);
				Assert.assertTrue(fault.getElementsByTagName("faultstring").getLength()==1);
				Assert.assertTrue("Fault ritornato dalla servlet di esempio di OpenSPCoop".equals(fault.getElementsByTagName("faultstring").item(0).getTextContent()));
				
				Reporter.log("Controllo details ...");
				Assert.assertTrue(fault.getElementsByTagName("detail")!=null);
				NodeList nodeListDetails = fault.getElementsByTagName("detail");
				Assert.assertTrue(nodeListDetails!=null);
				Assert.assertTrue(nodeListDetails.getLength()>0);
				
				Reporter.log("Controllo details childs ...");
				NodeList nodeListDetailsChilds = nodeListDetails.item(0).getChildNodes();
				Assert.assertTrue(nodeListDetailsChilds!=null);
				Assert.assertTrue(nodeListDetailsChilds.getLength()>0);
				Node erroreDetailsOpenSPCoop = null;
				Node erroreCNIPA = null;
				Reporter.log("Details presenti: "+nodeListDetailsChilds.getLength());
				for(int i=0; i<nodeListDetailsChilds.getLength(); i++){
					Node detail = nodeListDetailsChilds.item(i);
					Reporter.log("Detail["+detail.getLocalName()+"]");
					if("detailEsempioOpenSPCoop".equals(detail.getLocalName())){
						erroreDetailsOpenSPCoop = detail;
					}
					if("MessaggioDiErroreApplicativo".equals(detail.getLocalName())){
						erroreCNIPA = detail;
					}
				}
				
				Reporter.log("Controllo details openspcoop example");
				Assert.assertTrue(erroreDetailsOpenSPCoop!=null);
				Utilities.verificaDetailsOpenSPCoopExample(erroreDetailsOpenSPCoop);
				
				String codice = Utilities.toString(CodiceErroreIntegrazione.CODICE_516_CONNETTORE_UTILIZZO_CON_ERRORE);
				if("erroreApplicativoAsXmlRidefinito".equals(servizioApplicativoFruitore) || "erroreApplicativoAsSoapFaultRidefinito".equals(servizioApplicativoFruitore)){
					codice = "PREFIX_PERSONALIZZATO_516";
				}
				String msgErrore = CostantiErroriIntegrazione.MSG_516_SERVIZIO_APPLICATIVO_NON_DISPONIBILE;
				boolean equalsMatch = Utilities.CONTROLLO_DESCRIZIONE_TRAMITE_METODO_EQUALS;
								
				ErroriProperties erroriProperties = ErroriProperties.getInstance(this.log);
				if(this.genericCode) {
					IntegrationFunctionError integrationFunctionError = IntegrationFunctionError.SERVICE_UNAVAILABLE;
					codice = erroriProperties.getErrorType_noWrap(integrationFunctionError);
					if(erroriProperties.isForceGenericDetails_noWrap(integrationFunctionError)) {
						msgErrore = erroriProperties.getGenericDetails_noWrap(integrationFunctionError);
						equalsMatch = true;
					}
				}
				
				Reporter.log("Controllo xml errore applicativo cnipa definito nei details (Codice:"+codice+")");
				Assert.assertTrue(erroreCNIPA!=null);
				Utilities.verificaErroreApplicativoCnipa(erroreCNIPA, "MinisteroFruitoreSPCoopIT","InoltroBuste", 
						codice, msgErrore, equalsMatch);				
				
			} catch (AxisFault error) {
				
				throw error;
				
			}finally{
				try {
					dbComponentFruitore.close();
				}catch(Exception eClose) {}
				try {
					dbComponentErogatore.close();
				}catch(Exception eClose) {}	
			}
		}catch(Exception e){
			throw e;
		}finally{
			try {
				dbComponentFruitore.close();
			}catch(Exception eClose) {}
			try {
				dbComponentErogatore.close();
			}catch(Exception eClose) {}
			fin.close();
			
			super.unlockForCode(this.genericCode);
		}
				
	}
	
	
	
	
	
	
	
	
	
	
	
	/** SOAP FAULT PDD ARRICCHITO */
	
	protected Object[][] _personalizzazioniFaultApplicativiPdD(){
		return new Object[][]{
				{null},
				{"erroreApplicativoAsSoapFaultDefault"},
				{"erroreApplicativoAsSoapFaultRidefinito"},
				{"erroreApplicativoAsSoapXmlDefault"},
				{"erroreApplicativoAsXmlRidefinito"},
		};
	}
	protected Object _testFaultPddArricchitoFAULTCNIPA_Default(String servizioApplicativoFruitore) throws Exception{

		DatabaseComponent dbComponentFruitore = null;
		DatabaseComponent dbComponentErogatore = null;
		String portaDelegata = CostantiTestSuite.PORTA_DELEGATA_SOAP_FAULT_PDD_DESTINAZIONE_SINCRONO;
		Object response = null;
		
		try{
			super.lockForCode(this.genericCode, this.unwrap);
			
			ClientHttpGenerico client=new ClientHttpGenerico(new Repository());
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(portaDelegata);
			client.connectToSoapEngine();
			if(servizioApplicativoFruitore!=null){
				client.setProperty(TestSuiteProperties.getInstance().getServizioApplicativoTrasporto(), servizioApplicativoFruitore);
			}
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

				Reporter.log("Invocazione porta delegata inesistente non ha causato errori.");
				throw new TestSuiteException("Invocazione porta delegata inesistente non ha causato errori.");

			} catch (AxisFault error) {
				
				response = error;
				
				Assert.assertTrue(client.getCodiceStatoHTTP()==500);
				
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				Reporter.log("Controllo actor ["+null+"]");
				Assert.assertTrue(error.getFaultActor()==null);
				Reporter.log("Controllo fault code [Server.faultExample]");
				Assert.assertTrue("Server.faultExample".equals(error.getFaultCode().getLocalPart()));
				Reporter.log("Controllo fault string [Fault ritornato dalla servlet di esempio di OpenSPCoop]");
				Assert.assertTrue(error.getFaultString().equals("Fault ritornato dalla servlet di esempio di OpenSPCoop"));
				Reporter.log("Controllo details openspcoop example");
				Element [] details = error.getFaultDetails();
				Assert.assertTrue(details!=null);
				Assert.assertTrue(details.length>0);
				Element erroreDetailsOpenSPCoop = null;
				Reporter.log("Details presenti: "+details.length);
				for(int i=0; i<details.length; i++){
					Element detail = details[i];
					Reporter.log("Detail["+detail.getLocalName()+"]");
					if("detailEsempioOpenSPCoop".equals(detail.getLocalName())){
						erroreDetailsOpenSPCoop = detail;
						break;
					}
				}
				Assert.assertTrue(erroreDetailsOpenSPCoop!=null);
				Utilities.verificaDetailsOpenSPCoopExample(erroreDetailsOpenSPCoop);
				
				String codice = Utilities.toString(CodiceErroreIntegrazione.CODICE_516_CONNETTORE_UTILIZZO_CON_ERRORE);
				if("erroreApplicativoAsXmlRidefinito".equals(servizioApplicativoFruitore) || "erroreApplicativoAsSoapFaultRidefinito".equals(servizioApplicativoFruitore)){
					codice = "PREFIX_PERSONALIZZATO_516";
				}
				String msgErrore = CostantiErroriIntegrazione.MSG_516_PDD_NON_DISPONIBILE.replace(CostantiProtocollo.KEYWORDPDD_NON_DISPONIBILE, "spc-SoggettoConnettoreSOAPFaultServer");
				boolean equalsMatch = Utilities.CONTROLLO_DESCRIZIONE_TRAMITE_METODO_EQUALS;
								
				ErroriProperties erroriProperties = ErroriProperties.getInstance(this.log);
				if(this.genericCode) {
					IntegrationFunctionError integrationFunctionError = IntegrationFunctionError.SERVICE_UNAVAILABLE;
					codice = erroriProperties.getErrorType_noWrap(integrationFunctionError);
					if(erroriProperties.isForceGenericDetails_noWrap(integrationFunctionError)) {
						msgErrore = erroriProperties.getGenericDetails_noWrap(integrationFunctionError);
						equalsMatch = true;
					}
				}
				
				Reporter.log("Controllo xml errore applicativo cnipa definito nei details (Codice:"+codice+")");
				Utilities.verificaFaultDetailsRispettoErroreApplicativoCnipa(error,"MinisteroFruitoreSPCoopIT","InoltroBuste", 
						codice, msgErrore, equalsMatch);				
			}finally{
				try {
					dbComponentFruitore.close();
				}catch(Exception eClose) {}
				try {
					dbComponentErogatore.close();
				}catch(Exception eClose) {}	
			}
		}catch(Exception e){
			throw e;
		}finally{
			try {
				dbComponentFruitore.close();
			}catch(Exception eClose) {}
			try {
				dbComponentErogatore.close();
			}catch(Exception eClose) {}
			
			super.unlockForCode(this.genericCode);
		}
		return response;	
	}
	
	
	protected Object _testFaultPddArricchitoFAULTCNIPA_FaultGeneratoTramiteServizioWEBReale(String servizioApplicativoFruitore) throws Exception{

		DatabaseComponent dbComponentFruitore = null;
		DatabaseComponent dbComponentErogatore = null;
		String portaDelegata = CostantiTestSuite.PORTA_DELEGATA_ERRORE_APPLICATIVO_SOAP_FAULT_IM;
		Object response = null;
		
		try{
			super.lockForCode(this.genericCode, this.unwrap);
			
			ClientHttpGenerico client=new ClientHttpGenerico(new Repository());
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(portaDelegata);
			client.connectToSoapEngine();
			if(servizioApplicativoFruitore!=null){
				client.setProperty(TestSuiteProperties.getInstance().getServizioApplicativoTrasporto(), servizioApplicativoFruitore);
			}
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

				Reporter.log("Invocazione porta delegata inesistente non ha causato errori.");
				throw new TestSuiteException("Invocazione porta delegata inesistente non ha causato errori.");

			} catch (AxisFault error) {
				
				response = error;
				
				Assert.assertTrue(client.getCodiceStatoHTTP()==500);
				
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				Reporter.log("Controllo actor ["+null+"]");
				Assert.assertTrue(error.getFaultActor()==null);
				Reporter.log("Controllo fault code [Client]");
				Assert.assertTrue("Client".equals(error.getFaultCode().getLocalPart()));
				String msgAtteso = "No such operation 'getQuote'";
				String msgAtteso2 = "getQuote was not recognized.";
				Reporter.log("Controllo fault string");
				Assert.assertTrue(error.getFaultString().equals(msgAtteso) || error.getFaultString().contains(msgAtteso2));
				
				String codice = Utilities.toString(CodiceErroreIntegrazione.CODICE_516_CONNETTORE_UTILIZZO_CON_ERRORE);
				if("erroreApplicativoAsXmlRidefinito".equals(servizioApplicativoFruitore) || "erroreApplicativoAsSoapFaultRidefinito".equals(servizioApplicativoFruitore)){
					codice = "PREFIX_PERSONALIZZATO_516";
				}
				String msgErrore = CostantiErroriIntegrazione.MSG_516_PDD_NON_DISPONIBILE.replace(CostantiProtocollo.KEYWORDPDD_NON_DISPONIBILE, "spc-SOAPFaultIM");
				boolean equalsMatch = Utilities.CONTROLLO_DESCRIZIONE_TRAMITE_METODO_EQUALS;
								
				ErroriProperties erroriProperties = ErroriProperties.getInstance(this.log);
				if(this.genericCode) {
					IntegrationFunctionError integrationFunctionError = IntegrationFunctionError.SERVICE_UNAVAILABLE;
					codice = erroriProperties.getErrorType_noWrap(integrationFunctionError);
					if(erroriProperties.isForceGenericDetails_noWrap(integrationFunctionError)) {
						msgErrore = erroriProperties.getGenericDetails_noWrap(integrationFunctionError);
						equalsMatch = true;
					}
				}
				
				Reporter.log("Controllo xml errore applicativo cnipa definito nei details (Codice:"+codice+")");
				Utilities.verificaFaultDetailsRispettoErroreApplicativoCnipa(error,"MinisteroFruitoreSPCoopIT","InoltroBuste", 
						codice, msgErrore, equalsMatch);				
			}finally{
				try {
					dbComponentFruitore.close();
				}catch(Exception eClose) {}
				try {
					dbComponentErogatore.close();
				}catch(Exception eClose) {}	
			}
		}catch(Exception e){
			throw e;
		}finally{
			try {
				dbComponentFruitore.close();
			}catch(Exception eClose) {}
			try {
				dbComponentErogatore.close();
			}catch(Exception eClose) {}
			
			super.unlockForCode(this.genericCode);
		}
		return response;	
	}
	
	

	protected Object _testFaultPddArricchitoFAULTCNIPA_FaultGeneratoSenzaDetails(String servizioApplicativoFruitore) throws Exception{

		DatabaseComponent dbComponentFruitore = null;
		DatabaseComponent dbComponentErogatore = null;
		String portaDelegata = CostantiTestSuite.PORTA_DELEGATA_ERRORE_APPLICATIVO_SOAP_FAULT_SENZA_DETAILS;
		Object response = null;
		
		try{
			super.lockForCode(this.genericCode, this.unwrap);
			
			ClientHttpGenerico client=new ClientHttpGenerico(new Repository());
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(portaDelegata);
			client.connectToSoapEngine();
			if(servizioApplicativoFruitore!=null){
				client.setProperty(TestSuiteProperties.getInstance().getServizioApplicativoTrasporto(), servizioApplicativoFruitore);
			}
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

				Reporter.log("Invocazione porta delegata inesistente non ha causato errori.");
				throw new TestSuiteException("Invocazione porta delegata inesistente non ha causato errori.");

			} catch (AxisFault error) {
				
				response = error;
				
				Assert.assertTrue(client.getCodiceStatoHTTP()==500);
				
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				Reporter.log("Controllo actor ["+null+"]");
				Assert.assertTrue(error.getFaultActor()==null);
				Reporter.log("Controllo fault code [Server.faultExample]");
				Assert.assertTrue("Server.faultExample".equals(error.getFaultCode().getLocalPart()));
				Reporter.log("Controllo fault string [Fault ritornato dalla servlet di esempio di OpenSPCoop]");
				Assert.assertTrue(error.getFaultString().equals("Fault ritornato dalla servlet di esempio di OpenSPCoop"));
				
				String codice = Utilities.toString(CodiceErroreIntegrazione.CODICE_516_CONNETTORE_UTILIZZO_CON_ERRORE);
				if("erroreApplicativoAsXmlRidefinito".equals(servizioApplicativoFruitore) || "erroreApplicativoAsSoapFaultRidefinito".equals(servizioApplicativoFruitore)){
					codice = "PREFIX_PERSONALIZZATO_516";
				}
				String msgErrore = CostantiErroriIntegrazione.MSG_516_PDD_NON_DISPONIBILE.replace(CostantiProtocollo.KEYWORDPDD_NON_DISPONIBILE, "spc-SOAPFaultSenzaDetails");
				boolean equalsMatch = Utilities.CONTROLLO_DESCRIZIONE_TRAMITE_METODO_EQUALS;
								
				ErroriProperties erroriProperties = ErroriProperties.getInstance(this.log);
				if(this.genericCode) {
					IntegrationFunctionError integrationFunctionError = IntegrationFunctionError.SERVICE_UNAVAILABLE;
					codice = erroriProperties.getErrorType_noWrap(integrationFunctionError);
					if(erroriProperties.isForceGenericDetails_noWrap(integrationFunctionError)) {
						msgErrore = erroriProperties.getGenericDetails_noWrap(integrationFunctionError);
						equalsMatch = true;
					}
				}
				
				Reporter.log("Controllo xml errore applicativo cnipa definito nei details (Codice:"+codice+")");
				Utilities.verificaFaultDetailsRispettoErroreApplicativoCnipa(error,"MinisteroFruitoreSPCoopIT","InoltroBuste", 
						codice, msgErrore, equalsMatch);				
			}finally{
				try {
					dbComponentFruitore.close();
				}catch(Exception eClose) {}
				try {
					dbComponentErogatore.close();
				}catch(Exception eClose) {}	
			}
		}catch(Exception e){
			throw e;
		}finally{
			try {
				dbComponentFruitore.close();
			}catch(Exception eClose) {}
			try {
				dbComponentErogatore.close();
			}catch(Exception eClose) {}
			
			super.unlockForCode(this.genericCode);
		}
		return response;	
	}
	
	protected Object _testFaultPddArricchitoFAULTCNIPA_FaultGeneratoConPrefixErrato(String servizioApplicativoFruitore) throws Exception{

		DatabaseComponent dbComponentFruitore = null;
		DatabaseComponent dbComponentErogatore = null;
		String portaDelegata = CostantiTestSuite.PORTA_DELEGATA_ERRORE_APPLICATIVO_SOAP_FAULT_CUSTOM+"/"+CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_SOAP_FAULT_PDD_CON_PREFIX_ERRATO;

		init();
		
		Object response = null;
		
		try{
			super.lockForCode(this.genericCode, this.unwrap);
			
			OpenSPCoop2MessageFactory messageFactory = OpenSPCoop2MessageFactory.getDefaultMessageFactory();
			
			ClientHttpGenerico client=new ClientHttpGenerico(new Repository());
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(portaDelegata);
			client.connectToSoapEngine();
			if(servizioApplicativoFruitore!=null){
				client.setProperty(TestSuiteProperties.getInstance().getServizioApplicativoTrasporto(), servizioApplicativoFruitore);
			}
			byte [] xmlRichiesta = org.openspcoop2.utils.resources.FileSystemUtilities.readBytesFromFile(Utilities.testSuiteProperties.getSoap11FileName());
			client.setMessaggioXMLRichiesta(xmlRichiesta); // in modo da poter verificare a mano la risposta. Axis altrimenti in questi casi particolari non funziona
			
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

				byte[] responseByte = client.getMessaggioXMLRisposta();
				if(responseByte==null){
					throw new Exception("Risposta non ritornata");
				}
				
				// Risposta
				Element e = XMLUtils.getInstance().newElement(responseByte);
				//System.out.println("Ricevuto SOAP ["+e.getLocalName()+"] ["+e.getNamespaceURI()+"]");
				//System.out.println("RISPOSTA ["+XMLUtils.getInstance().toString(e)+"]");
				Assert.assertTrue(Costanti.SOAP_ENVELOPE_NAMESPACE.equals(e.getNamespaceURI()));
				Assert.assertTrue("Envelope".equals(e.getLocalName()));
				NodeList nL = e.getChildNodes();
				Node fault = null;
				for (int i = 0; i < nL.getLength(); i++) {
					Node n = nL.item(i);
					if(Costanti.SOAP_ENVELOPE_NAMESPACE.equals(n.getNamespaceURI()) && "Body".equals(n.getLocalName())){
						fault = SoapUtils.getFirstNotEmptyChildNode(messageFactory, n, false);
					}
				}

				Node faultSenzaDetails = fault.cloneNode(true);
				Node CNIPA = null;
				nL = faultSenzaDetails.getChildNodes();
				for (int i = 0; i < nL.getLength(); i++) {
					Node n = nL.item(i);
					if("detail".equals(n.getLocalName())){
						NodeList nDetail = n.getChildNodes();
						for (int j = 0; j < nDetail.getLength(); j++) {
							Node nDetailI = nDetail.item(j);
							if("http://www.cnipa.it/schemas/2003/eGovIT/Exception1_0/".equals(nDetailI.getNamespaceURI()) && 
									"MessaggioDiErroreApplicativo".equals(nDetailI.getLocalName())){
								CNIPA = nDetailI;
								break;
							}
						}
						if(CNIPA!=null){
							n.removeChild(CNIPA);
						}
					}
				}
				

				// Atteso
				byte [] xmlAtteso = org.openspcoop2.utils.resources.FileSystemUtilities.readBytesFromFile(Utilities.testSuiteProperties.getSoapTestSOAPScorretto_soapFaultPrefixErrato());
				Element eAtteso = XMLUtils.getInstance().newElement(xmlAtteso);
				nL = eAtteso.getChildNodes();
				Node faultAtteso = null;
				for (int i = 0; i < nL.getLength(); i++) {
					Node n = nL.item(i);
					if(Costanti.SOAP_ENVELOPE_NAMESPACE.equals(n.getNamespaceURI()) && "Body".equals(n.getLocalName())){
						faultAtteso = SoapUtils.getFirstNotEmptyChildNode(messageFactory, n, false);
					}
				}
				
				byte[] tmp = XMLUtils.getInstance().toByteArray(faultSenzaDetails);
				faultSenzaDetails = XMLUtils.getInstance().newElement(tmp);
				tmp = XMLUtils.getInstance().toByteArray(faultAtteso);
				faultAtteso = XMLUtils.getInstance().newElement(tmp);
				boolean diff = xmlDiff.diff(faultSenzaDetails, faultAtteso);
				if(!diff){
					System.out.println("FAULT ["+XMLUtils.getInstance().toString(faultSenzaDetails)+"]");
					System.out.println("FAULTAtteso ["+XMLUtils.getInstance().toString(faultAtteso)+"]");
					System.out.println("Diff: "+xmlDiff.getDifferenceDetails());
				}
				Assert.assertTrue(diff);

				String codice = Utilities.toString(CodiceErroreIntegrazione.CODICE_516_CONNETTORE_UTILIZZO_CON_ERRORE);
				if("erroreApplicativoAsXmlRidefinito".equals(servizioApplicativoFruitore) || "erroreApplicativoAsSoapFaultRidefinito".equals(servizioApplicativoFruitore)){
					codice = "PREFIX_PERSONALIZZATO_516";
				}
				String msgErrore =CostantiErroriIntegrazione.MSG_516_PDD_NON_DISPONIBILE.replace(CostantiProtocollo.KEYWORDPDD_NON_DISPONIBILE, 
						CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_EROGATORE+"-"+CostantiTestSuite.SPCOOP_NOME_SOGGETTO_EROGATORE);
				boolean equalsMatch = Utilities.CONTROLLO_DESCRIZIONE_TRAMITE_METODO_EQUALS;
								
				ErroriProperties erroriProperties = ErroriProperties.getInstance(this.log);
				if(this.genericCode) {
					IntegrationFunctionError integrationFunctionError = IntegrationFunctionError.SERVICE_UNAVAILABLE;
					codice = erroriProperties.getErrorType_noWrap(integrationFunctionError);
					if(erroriProperties.isForceGenericDetails_noWrap(integrationFunctionError)) {
						msgErrore = erroriProperties.getGenericDetails_noWrap(integrationFunctionError);
						equalsMatch = true;
					}
				}
				
				Reporter.log("Controllo xml errore applicativo cnipa definito nei details (Codice:"+codice+")");
				Utilities.verificaErroreApplicativoCnipa(CNIPA,"MinisteroFruitoreSPCoopIT","InoltroBuste", 
						codice, msgErrore, equalsMatch);				

			}finally{
				try {
					dbComponentFruitore.close();
				}catch(Exception eClose) {}
				try {
					dbComponentErogatore.close();
				}catch(Exception eClose) {}	
			}
		}catch(Exception e){
			throw e;
		}finally{
			try {
				dbComponentFruitore.close();
			}catch(Exception eClose) {}
			try {
				dbComponentErogatore.close();
			}catch(Exception eClose) {}
			
			super.unlockForCode(this.genericCode);
		}
		return response;	
	}
	
	protected Object _testFaultPddArricchitoFAULTCNIPA_FaultGeneratoConSenzaPrefix(String servizioApplicativoFruitore) throws Exception{

		DatabaseComponent dbComponentFruitore = null;
		DatabaseComponent dbComponentErogatore = null;
		String portaDelegata = CostantiTestSuite.PORTA_DELEGATA_ERRORE_APPLICATIVO_SOAP_FAULT_CUSTOM+"/"+
				CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_SOAP_FAULT_PDD_SENZA_PREFIX;

		init();
		
		Object response = null;
		
		try{
			super.lockForCode(this.genericCode, this.unwrap);
			
			OpenSPCoop2MessageFactory messageFactory = OpenSPCoop2MessageFactory.getDefaultMessageFactory();
			
			ClientHttpGenerico client=new ClientHttpGenerico(new Repository());
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(portaDelegata);
			client.connectToSoapEngine();
			if(servizioApplicativoFruitore!=null){
				client.setProperty(TestSuiteProperties.getInstance().getServizioApplicativoTrasporto(), servizioApplicativoFruitore);
			}
			byte [] xmlRichiesta = org.openspcoop2.utils.resources.FileSystemUtilities.readBytesFromFile(Utilities.testSuiteProperties.getSoap11FileName());
			client.setMessaggioXMLRichiesta(xmlRichiesta); // in modo da poter verificare a mano la risposta. Axis altrimenti in questi casi particolari non funziona
			
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

				byte[] responseByte = client.getMessaggioXMLRisposta();
				if(responseByte==null){
					throw new Exception("Risposta non ritornata");
				}
				
				// Risposta
				Element e = XMLUtils.getInstance().newElement(responseByte);
				//System.out.println("Ricevuto SOAP ["+e.getLocalName()+"] ["+e.getNamespaceURI()+"]");
				//System.out.println("RISPOSTA ["+XMLUtils.getInstance().toString(e)+"]");
				Assert.assertTrue(Costanti.SOAP_ENVELOPE_NAMESPACE.equals(e.getNamespaceURI()));
				Assert.assertTrue("Envelope".equals(e.getLocalName()));
				NodeList nL = e.getChildNodes();
				Node fault = null;
				for (int i = 0; i < nL.getLength(); i++) {
					Node n = nL.item(i);
					if(Costanti.SOAP_ENVELOPE_NAMESPACE.equals(n.getNamespaceURI()) && "Body".equals(n.getLocalName())){
						fault = SoapUtils.getFirstNotEmptyChildNode(messageFactory, n, false);
					}
				}

				Node faultSenzaDetails = fault.cloneNode(true);
				Node CNIPA = null;
				nL = faultSenzaDetails.getChildNodes();
				for (int i = 0; i < nL.getLength(); i++) {
					Node n = nL.item(i);
					if("detail".equals(n.getLocalName())){
						NodeList nDetail = n.getChildNodes();
						for (int j = 0; j < nDetail.getLength(); j++) {
							Node nDetailI = nDetail.item(j);
							if("http://www.cnipa.it/schemas/2003/eGovIT/Exception1_0/".equals(nDetailI.getNamespaceURI()) && 
									"MessaggioDiErroreApplicativo".equals(nDetailI.getLocalName())){
								CNIPA = nDetailI;
								break;
							}
						}
						if(CNIPA!=null){
							n.removeChild(CNIPA);
						}
					}
				}
				

				// Atteso
				byte [] xmlAtteso = org.openspcoop2.utils.resources.FileSystemUtilities.readBytesFromFile(Utilities.testSuiteProperties.getSoapTestSOAPScorretto_soapFaultSenzaPrefix());
				Element eAtteso = XMLUtils.getInstance().newElement(xmlAtteso);
				nL = eAtteso.getChildNodes();
				Node faultAtteso = null;
				for (int i = 0; i < nL.getLength(); i++) {
					Node n = nL.item(i);
					if(Costanti.SOAP_ENVELOPE_NAMESPACE.equals(n.getNamespaceURI()) && "Body".equals(n.getLocalName())){
						faultAtteso = SoapUtils.getFirstNotEmptyChildNode(messageFactory, n, false);
					}
				}
				
				byte[] tmp = XMLUtils.getInstance().toByteArray(faultSenzaDetails);
				faultSenzaDetails = XMLUtils.getInstance().newElement(tmp);
				tmp = XMLUtils.getInstance().toByteArray(faultAtteso);
				faultAtteso = XMLUtils.getInstance().newElement(tmp);
				boolean diff = xmlDiff.diff(faultSenzaDetails, faultAtteso);
				if(!diff){
					System.out.println("FAULT ["+XMLUtils.getInstance().toString(faultSenzaDetails)+"]");
					System.out.println("FAULTAtteso ["+XMLUtils.getInstance().toString(faultAtteso)+"]");
					System.out.println("Diff: "+xmlDiff.getDifferenceDetails());
				}
				Assert.assertTrue(diff);

				String codice = Utilities.toString(CodiceErroreIntegrazione.CODICE_516_CONNETTORE_UTILIZZO_CON_ERRORE);
				if("erroreApplicativoAsXmlRidefinito".equals(servizioApplicativoFruitore) || "erroreApplicativoAsSoapFaultRidefinito".equals(servizioApplicativoFruitore)){
					codice = "PREFIX_PERSONALIZZATO_516";
				}
				String msgErrore =CostantiErroriIntegrazione.MSG_516_PDD_NON_DISPONIBILE.replace(CostantiProtocollo.KEYWORDPDD_NON_DISPONIBILE, 
						CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_EROGATORE+"-"+CostantiTestSuite.SPCOOP_NOME_SOGGETTO_EROGATORE);
				boolean equalsMatch = Utilities.CONTROLLO_DESCRIZIONE_TRAMITE_METODO_EQUALS;
								
				ErroriProperties erroriProperties = ErroriProperties.getInstance(this.log);
				if(this.genericCode) {
					IntegrationFunctionError integrationFunctionError = IntegrationFunctionError.SERVICE_UNAVAILABLE;
					codice = erroriProperties.getErrorType_noWrap(integrationFunctionError);
					if(erroriProperties.isForceGenericDetails_noWrap(integrationFunctionError)) {
						msgErrore = erroriProperties.getGenericDetails_noWrap(integrationFunctionError);
						equalsMatch = true;
					}
				}
				
				Reporter.log("Controllo xml errore applicativo cnipa definito nei details (Codice:"+codice+")");
				Utilities.verificaErroreApplicativoCnipa(CNIPA,"MinisteroFruitoreSPCoopIT","InoltroBuste", 
						codice, msgErrore, equalsMatch);				

			}finally{
				try {
					dbComponentFruitore.close();
				}catch(Exception eClose) {}
				try {
					dbComponentErogatore.close();
				}catch(Exception eClose) {}	
			}
		}catch(Exception e){
			throw e;
		}finally{
			try {
				dbComponentFruitore.close();
			}catch(Exception eClose) {}
			try {
				dbComponentErogatore.close();
			}catch(Exception eClose) {}
			
			super.unlockForCode(this.genericCode);
		}
		return response;	
	}
	
	
	/** SERVIZIO TUNNEL SOAP: SOAP FAULT PDD ARRICCHITO */
	
	protected Object[][] _personalizzazioniFaultApplicativiPdDTunnelSOAP(){
		return new Object[][]{{null},
				{"erroreApplicativoAsSoapFaultDefault"},
				{"erroreApplicativoAsSoapFaultRidefinito"},
				{"erroreApplicativoAsSoapXmlDefault"},
				{"erroreApplicativoAsXmlRidefinito"},
		};
	}
	protected void _testFaultPddArricchitoFAULTCNIPA_Default_TunnelSOAP(String servizioApplicativoFruitore) throws Exception{

		DatabaseComponent dbComponentFruitore = null;
		DatabaseComponent dbComponentErogatore = null;
		String portaDelegata = CostantiTestSuite.PORTA_DELEGATA_SOAP_FAULT_PDD_DESTINAZIONE_SINCRONO;
		ByteArrayOutputStream bout = null;
		java.io.FileInputStream fin = null;
		
		try{
			super.lockForCode(this.genericCode, this.unwrap);
			
			OpenSPCoop2MessageFactory messageFactory = OpenSPCoop2MessageFactory.getDefaultMessageFactory();
			
			fin = new java.io.FileInputStream(new File(Utilities.testSuiteProperties.getXmlSenzaSoapFileName()));
			bout = new ByteArrayOutputStream();
			byte[]bytes = new byte[2048];
			int letti = 0;
			while( (letti = fin.read(bytes)) != -1 ){
				bout.write(bytes, 0, letti);
			}
			bout.flush();
			bout.close();
			
			ClientHttpGenerico client=new ClientHttpGenerico(new Repository());
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore().replace("out", "out/xml2soap"));
			client.setPortaDelegata(portaDelegata);
			client.connectToSoapEngine();
			if(servizioApplicativoFruitore!=null){
				client.setProperty(TestSuiteProperties.getInstance().getServizioApplicativoTrasporto(), servizioApplicativoFruitore);
			}
			client.setMessaggioXMLRichiesta(bout.toByteArray());
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
				boolean responseAsXml = true; // tunnel soap sono tutti errori xml in risposta
				if(responseAsXml) {
					client.setForceResponseAsBinaryProcessor(true);
				}
				
				client.run();

				Reporter.log("Invocazione porta delegata inesistente non ha causato errori.");
				
				Assert.assertTrue(client.getCodiceStatoHTTP()==500);
								
				byte [] xmlErroreApplicativo = client.getMessaggioXMLRisposta();
				Assert.assertTrue(xmlErroreApplicativo!=null);

				Element fault = org.openspcoop2.message.xml.MessageXMLUtils.getInstance(messageFactory).newElement(xmlErroreApplicativo);
				Assert.assertTrue("Fault".equals(fault.getLocalName()));
				Assert.assertTrue("http://schemas.xmlsoap.org/soap/envelope/".equals(fault.getNamespaceURI()));
				
				Reporter.log("Controllo actor ["+null+"]");
				Assert.assertTrue(fault.getElementsByTagName("faultactor")==null || fault.getElementsByTagName("faultactor").getLength()==0);
				
				Reporter.log("Controllo fault code [Server.faultExample]");
				Assert.assertTrue(fault.getElementsByTagName("faultcode")!=null);
				Assert.assertTrue(fault.getElementsByTagName("faultcode").getLength()==1);
				String code = fault.getElementsByTagName("faultcode").item(0).getTextContent();
				Reporter.log("Controllo fault code [Server.faultExample]: ["+code+"]");
				Assert.assertTrue(code!=null && ("Server.faultExample".equals(code) || code.endsWith(":Server.faultExample")));
				
				Reporter.log("Controllo fault string [Fault ritornato dalla servlet di esempio di OpenSPCoop]");
				Assert.assertTrue(fault.getElementsByTagName("faultstring")!=null);
				Assert.assertTrue(fault.getElementsByTagName("faultstring").getLength()==1);
				Assert.assertTrue("Fault ritornato dalla servlet di esempio di OpenSPCoop".equals(fault.getElementsByTagName("faultstring").item(0).getTextContent()));
				
				Reporter.log("Controllo details ...");
				Assert.assertTrue(fault.getElementsByTagName("detail")!=null);
				NodeList nodeListDetails = fault.getElementsByTagName("detail");
				Assert.assertTrue(nodeListDetails!=null);
				Assert.assertTrue(nodeListDetails.getLength()>0);
				
				Reporter.log("Controllo details childs ...");
				NodeList nodeListDetailsChilds = nodeListDetails.item(0).getChildNodes();
				Assert.assertTrue(nodeListDetailsChilds!=null);
				Assert.assertTrue(nodeListDetailsChilds.getLength()>0);
				Node erroreDetailsOpenSPCoop = null;
				Node erroreCNIPA = null;
				Reporter.log("Details presenti: "+nodeListDetailsChilds.getLength());
				for(int i=0; i<nodeListDetailsChilds.getLength(); i++){
					Node detail = nodeListDetailsChilds.item(i);
					Reporter.log("Detail["+detail.getLocalName()+"]");
					if("detailEsempioOpenSPCoop".equals(detail.getLocalName())){
						erroreDetailsOpenSPCoop = detail;
					}
					if("MessaggioDiErroreApplicativo".equals(detail.getLocalName())){
						erroreCNIPA = detail;
					}
				}
				
				Reporter.log("Controllo details openspcoop example");
				Assert.assertTrue(erroreDetailsOpenSPCoop!=null);
				Utilities.verificaDetailsOpenSPCoopExample(erroreDetailsOpenSPCoop);
				
				String codice = Utilities.toString(CodiceErroreIntegrazione.CODICE_516_CONNETTORE_UTILIZZO_CON_ERRORE);
				if("erroreApplicativoAsXmlRidefinito".equals(servizioApplicativoFruitore) || "erroreApplicativoAsSoapFaultRidefinito".equals(servizioApplicativoFruitore)){
					codice = "PREFIX_PERSONALIZZATO_516";
				}
				String msgErrore =CostantiErroriIntegrazione.MSG_516_PDD_NON_DISPONIBILE.replace(CostantiProtocollo.KEYWORDPDD_NON_DISPONIBILE, "spc-SoggettoConnettoreSOAPFaultServer");
				boolean equalsMatch = Utilities.CONTROLLO_DESCRIZIONE_TRAMITE_METODO_EQUALS;
								
				ErroriProperties erroriProperties = ErroriProperties.getInstance(this.log);
				if(this.genericCode) {
					IntegrationFunctionError integrationFunctionError = IntegrationFunctionError.SERVICE_UNAVAILABLE;
					codice = erroriProperties.getErrorType_noWrap(integrationFunctionError);
					if(erroriProperties.isForceGenericDetails_noWrap(integrationFunctionError)) {
						msgErrore = erroriProperties.getGenericDetails_noWrap(integrationFunctionError);
						equalsMatch = true;
					}
				}
				
				Reporter.log("Controllo xml errore applicativo cnipa definito nei details (Codice:"+codice+")");
				Assert.assertTrue(erroreCNIPA!=null);
				Utilities.verificaErroreApplicativoCnipa(erroreCNIPA, "MinisteroFruitoreSPCoopIT","InoltroBuste", 
						codice, msgErrore, equalsMatch);				
		
			} catch (AxisFault error) {
				
				throw error;
				
			}finally{
				try {
					dbComponentFruitore.close();
				}catch(Exception eClose) {}
				try {
					dbComponentErogatore.close();
				}catch(Exception eClose) {}	
			}
		}catch(Exception e){
			throw e;
		}finally{
			try {
				dbComponentFruitore.close();
			}catch(Exception eClose) {}
			try {
				dbComponentErogatore.close();
			}catch(Exception eClose) {}
			fin.close();
			
			super.unlockForCode(this.genericCode);
		}
				
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/** ERRORE CONNETTORE PDD: connection refused ARRICCHITO */
	
	protected Object[][] _personalizzazioniErroreConnectionRefused(){
		return new Object[][]{{null},
				{"erroreApplicativoAsSoapFaultDefault"},
				{"erroreApplicativoAsSoapFaultRidefinito"},
				{"erroreApplicativoAsSoapXmlDefault"},
				{"erroreApplicativoAsXmlRidefinito"},
		};
	}
	protected Object _testServizioApplicativoConnectionRefused(String servizioApplicativoFruitore) throws Exception{

		DatabaseComponent dbComponentFruitore = null;
		DatabaseComponent dbComponentErogatore = null;
		Object response = null;
		
		Date dataInizioTest = null;
		Date dataFineTest = null;
		try{
			super.lockForCode(this.genericCode, this.unwrap);
			dataInizioTest = DateManager.getDate();
		
			OpenSPCoop2MessageFactory messageFactory = OpenSPCoop2MessageFactory.getDefaultMessageFactory();
			
			String portaDelegata = CostantiTestSuite.PORTA_DELEGATA_CONNETTORE_ERRATO_SINCRONO_STATELESS;
			
			ClientHttpGenerico client=new ClientHttpGenerico(new Repository());
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(portaDelegata);
			client.connectToSoapEngine();
			if(servizioApplicativoFruitore!=null){
				client.setProperty(TestSuiteProperties.getInstance().getServizioApplicativoTrasporto(), servizioApplicativoFruitore);
			}
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
			
			String actor = org.openspcoop2.testsuite.core.CostantiTestSuite.OPENSPCOOP2_INTEGRATION_ACTOR;
			String idPorta = "MinisteroFruitoreSPCoopIT";
			String codice = Utilities.toString(CodiceErroreIntegrazione.CODICE_516_CONNETTORE_UTILIZZO_CON_ERRORE);
			String msg = CostantiErroriIntegrazione.MSG_516_PDD_NON_DISPONIBILE.replace(CostantiProtocollo.KEYWORDPDD_NON_DISPONIBILE, "spc-SoggettoConnettoreErrato");
			boolean equalsMatch = true;
									
			ErroriProperties erroriProperties = ErroriProperties.getInstance(this.log);
			if(this.genericCode) {
				IntegrationFunctionError integrationFunctionError = IntegrationFunctionError.SERVICE_UNAVAILABLE;
				codice = erroriProperties.getErrorType_noWrap(integrationFunctionError);
				if(erroriProperties.isForceGenericDetails_noWrap(integrationFunctionError)) {
					msg = erroriProperties.getGenericDetails_noWrap(integrationFunctionError);
					equalsMatch = true;
				}
			}
			
			if("erroreApplicativoAsSoapFaultRidefinito".equals(servizioApplicativoFruitore) || "erroreApplicativoAsXmlRidefinito".equals(servizioApplicativoFruitore)){
				actor = "ACTOR_RIDEFINITO";
				if(!this.genericCode) {
					codice = "PREFIX_PERSONALIZZATO_516";
				}
			}
			
			try {
				boolean responseAsXml = false;
				if("erroreApplicativoAsXmlRidefinito".equals(servizioApplicativoFruitore) || "erroreApplicativoAsSoapXmlDefault".equals(servizioApplicativoFruitore)) {
					responseAsXml = true;
				}
				if(responseAsXml) {
					client.setForceResponseAsBinaryProcessor(true);
				}
				
				client.run();

				if("erroreApplicativoAsSoapFaultDefault".equals(servizioApplicativoFruitore) ||
						"erroreApplicativoAsSoapFaultRidefinito".equals(servizioApplicativoFruitore)){
					Reporter.log("Invocazione porta delegata inesistente non ha causato errori.");
					throw new TestSuiteException("Invocazione porta delegata inesistente non ha causato errori.");
				}

				Reporter.log("Return Code '"+servizioApplicativoFruitore+"' '"+client.getCodiceStatoHTTP()+"'");
				if(responseAsXml) {
					Assert.assertTrue(client.getCodiceStatoHTTP()==503);
				}
				else {
					Assert.assertTrue(client.getCodiceStatoHTTP()==200);
				}
				
				byte [] xmlErroreApplicativo = null;
				if(responseAsXml) {
					xmlErroreApplicativo = client.getMessaggioXMLRisposta();
					response = xmlErroreApplicativo;
					Assert.assertTrue(xmlErroreApplicativo!=null);
				}
				else {
				
					Message msgRisposta = client.getResponseMessage();
					Assert.assertTrue(msgRisposta!=null);
					Assert.assertTrue(msgRisposta.getSOAPBody()!=null);
					Assert.assertTrue(msgRisposta.getSOAPBody().hasChildNodes());
					
					try {
						xmlErroreApplicativo = org.openspcoop2.message.xml.MessageXMLUtils.DEFAULT.toByteArray(msgRisposta.getSOAPBody().getFirstChild(),true);
					}catch(Throwable t) {
						// normalize per conflito di librerie axis - saaj
						org.w3c.dom.Document d = org.openspcoop2.message.xml.MessageXMLUtils.DEFAULT.newDocument();
						Node n = d.importNode(msgRisposta.getSOAPBody().getFirstChild(), true);
						xmlErroreApplicativo = org.openspcoop2.message.xml.MessageXMLUtils.DEFAULT.toByteArray(n,true);
					}
					response = msgRisposta.getSOAPBody();
					
				}
				
				Utilities.verificaErroreApplicativoCnipa(org.openspcoop2.message.xml.MessageXMLUtils.getInstance(messageFactory).newElement(xmlErroreApplicativo), 
						idPorta,"InoltroBuste", 
						codice, 
						msg, equalsMatch);	
				
			} catch (AxisFault error) {
				
				response = error;
				
				Assert.assertTrue(client.getCodiceStatoHTTP()==500);
								
				Utilities.verificaFaultIntegrazione(error, actor,
						idPorta,"InoltroBuste", 
						codice, 
						msg, equalsMatch);				
			}finally{
				try {
					dbComponentFruitore.close();
				}catch(Exception eClose) {}
				try {
					dbComponentErogatore.close();
				}catch(Exception eClose) {}	
				
				ErroreAttesoOpenSPCoopLogCore err = new ErroreAttesoOpenSPCoopLogCore();
				err.setIntervalloInferiore(dataInizioTest);
				err.setIntervalloSuperiore(dataFineTest);
				err.setMsgErrore("Errore avvenuto durante la consegna HTTP: Connection refused");
				this.erroriAttesiOpenSPCoopCore.add(err);
			}
		}catch(Exception e){
			throw e;
		}finally{
			try {
				dbComponentFruitore.close();
			}catch(Exception eClose) {}
			try {
				dbComponentErogatore.close();
			}catch(Exception eClose) {}
			
			super.unlockForCode(this.genericCode);
			dataFineTest = DateManager.getDate();
		}	
		
		return response;
	}
	
	/** SERVIZIO TUNNEL SOAP: ERRORE CONNETTORE, connection refused ARRICCHITO */
	
	protected Object[][] _personalizzazioniErroreConnectionRefusedTunnelSOAP(){
		return new Object[][]{{null},
				{"erroreApplicativoAsSoapFaultDefault"},
				{"erroreApplicativoAsSoapFaultRidefinito"},
				{"erroreApplicativoAsSoapXmlDefault"},
				{"erroreApplicativoAsXmlRidefinito"},
		};
	}
	protected void _testServizioApplicativoConnectionRefused_TunnelSOAP(String servizioApplicativoFruitore) throws Exception{

		DatabaseComponent dbComponentFruitore = null;
		DatabaseComponent dbComponentErogatore = null;
		ByteArrayOutputStream bout = null;
		java.io.FileInputStream fin = null;
		
		Date dataInizioTest = null;
		Date dataFineTest = null;
		try{
			super.lockForCode(this.genericCode, this.unwrap);
			dataInizioTest = DateManager.getDate();
		
			OpenSPCoop2MessageFactory messageFactory = OpenSPCoop2MessageFactory.getDefaultMessageFactory();
			
			String portaDelegata = CostantiTestSuite.PORTA_DELEGATA_CONNETTORE_ERRATO_SINCRONO_STATELESS;
			
			fin = new java.io.FileInputStream(new File(Utilities.testSuiteProperties.getXmlSenzaSoapFileName()));
			bout = new ByteArrayOutputStream();
			byte[]bytes = new byte[2048];
			int letti = 0;
			while( (letti = fin.read(bytes)) != -1 ){
				bout.write(bytes, 0, letti);
			}
			bout.flush();
			bout.close();
			
			ClientHttpGenerico client=new ClientHttpGenerico(new Repository());
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore().replace("out", "out/xml2soap"));
			client.setPortaDelegata(portaDelegata);
			client.connectToSoapEngine();
			if(servizioApplicativoFruitore!=null){
				client.setProperty(TestSuiteProperties.getInstance().getServizioApplicativoTrasporto(), servizioApplicativoFruitore);
			}
			client.setMessaggioXMLRichiesta(bout.toByteArray());
			client.setRispostaDaGestire(true);
			// AttesaTerminazioneMessaggi
			if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()){
				dbComponentFruitore = DatabaseProperties.getDatabaseComponentFruitore();
				dbComponentErogatore = DatabaseProperties.getDatabaseComponentErogatore();

				client.setAttesaTerminazioneMessaggi(true);
				client.setDbAttesaTerminazioneMessaggiFruitore(dbComponentFruitore);
				client.setDbAttesaTerminazioneMessaggiErogatore(dbComponentErogatore);
			}
			
			String idPorta = "MinisteroFruitoreSPCoopIT";
			String codice = Utilities.toString(CodiceErroreIntegrazione.CODICE_516_CONNETTORE_UTILIZZO_CON_ERRORE);
			String msg = CostantiErroriIntegrazione.MSG_516_PDD_NON_DISPONIBILE.replace(CostantiProtocollo.KEYWORDPDD_NON_DISPONIBILE, "spc-SoggettoConnettoreErrato");
			boolean equalsMatch = true;
			
			ErroriProperties erroriProperties = ErroriProperties.getInstance(this.log);
			if(this.genericCode) {
				IntegrationFunctionError integrationFunctionError = IntegrationFunctionError.SERVICE_UNAVAILABLE;
				codice = erroriProperties.getErrorType_noWrap(integrationFunctionError);
				if(erroriProperties.isForceGenericDetails_noWrap(integrationFunctionError)) {
					msg = erroriProperties.getGenericDetails_noWrap(integrationFunctionError);
					equalsMatch = true;
				}
			}
			
			if("erroreApplicativoAsSoapFaultRidefinito".equals(servizioApplicativoFruitore) || "erroreApplicativoAsXmlRidefinito".equals(servizioApplicativoFruitore)){
				if(!this.genericCode) {
					codice = "PREFIX_PERSONALIZZATO_516";
				}
			}
			
			try {
				boolean responseAsXml = true; // tunnel soap sono tutti errori xml in risposta
				if(responseAsXml) {
					client.setForceResponseAsBinaryProcessor(true);
				}
				
				client.run();

				Reporter.log("Return Code '"+servizioApplicativoFruitore+"' '"+client.getCodiceStatoHTTP()+"'");
				if(responseAsXml) {
					Assert.assertTrue(client.getCodiceStatoHTTP()==503);
				}
				else {
					Assert.assertTrue(client.getCodiceStatoHTTP()==500);
				}
				
				byte [] xmlErroreApplicativo = client.getMessaggioXMLRisposta();
				Assert.assertTrue(xmlErroreApplicativo!=null);
				
				Utilities.verificaErroreApplicativoCnipa(org.openspcoop2.message.xml.MessageXMLUtils.getInstance(messageFactory).newElement(xmlErroreApplicativo), 
						idPorta,"InoltroBuste", 
						codice, 
						msg, equalsMatch);	
								
			} catch (AxisFault error) {
				throw error;			
			}finally{
				try {
					dbComponentFruitore.close();
				}catch(Exception eClose) {}
				try {
					dbComponentErogatore.close();
				}catch(Exception eClose) {}	
				
				ErroreAttesoOpenSPCoopLogCore err = new ErroreAttesoOpenSPCoopLogCore();
				err.setIntervalloInferiore(dataInizioTest);
				err.setIntervalloSuperiore(dataFineTest);
				err.setMsgErrore("Errore avvenuto durante la consegna HTTP: Connection refused");
				this.erroriAttesiOpenSPCoopCore.add(err);
			}
		}catch(Exception e){
			throw e;
		}finally{
			try{
				fin.close();
			}catch(Exception e){}
			try {
				dbComponentFruitore.close();
			}catch(Exception eClose) {}
			try {
				dbComponentErogatore.close();
			}catch(Exception eClose) {}
			
			super.unlockForCode(this.genericCode);
			dataFineTest = DateManager.getDate();
		}	
				
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/** ERRORE CONNETTORE SA: connection refused ARRICCHITO */
	
	protected Object[][] _personalizzazioniErroreConnectionRefusedServizioApplicativo(){
		return new Object[][]{{null},
				{"erroreApplicativoAsSoapFaultDefault"},
				{"erroreApplicativoAsSoapFaultRidefinito"},
				{"erroreApplicativoAsSoapXmlDefault"},
				{"erroreApplicativoAsXmlRidefinito"},
		};
	}
	protected Object _testServizioApplicativoConnectionRefusedServizioApplicativo(String servizioApplicativoFruitore) throws Exception{

		DatabaseComponent dbComponentFruitore = null;
		DatabaseComponent dbComponentErogatore = null;
		Object response = null;
		
		Date dataInizioTest = null;
		Date dataFineTest = null;
		try{
			super.lockForCode(this.genericCode, this.unwrap);
			dataInizioTest = DateManager.getDate();
		
			OpenSPCoop2MessageFactory messageFactory = OpenSPCoop2MessageFactory.getDefaultMessageFactory();
			
			String portaDelegata = CostantiTestSuite.PORTA_DELEGATA_CONNETTORE_ERRATO_SA_SINCRONO_STATELESS;
			
			ClientHttpGenerico client=new ClientHttpGenerico(new Repository());
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(portaDelegata);
			client.connectToSoapEngine();
			if(servizioApplicativoFruitore!=null){
				client.setProperty(TestSuiteProperties.getInstance().getServizioApplicativoTrasporto(), servizioApplicativoFruitore);
			}
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
			
			String actor = org.openspcoop2.testsuite.core.CostantiTestSuite.OPENSPCOOP2_INTEGRATION_ACTOR;
			String idPorta = "MinisteroFruitoreSPCoopIT";
			String codice = Utilities.toString(CodiceErroreCooperazione.ERRORE_GENERICO_PROCESSAMENTO_MESSAGGIO);
			String codiceEGOV = Utilities.toString(CodiceErroreCooperazione.ERRORE_GENERICO_PROCESSAMENTO_MESSAGGIO);
			String msg = CostantiErroriIntegrazione.MSG_516_SERVIZIO_APPLICATIVO_NON_DISPONIBILE;
			boolean equalsMatch = false;
			
			ErroriProperties erroriProperties = ErroriProperties.getInstance(this.log);
			if(this.genericCode) {
				IntegrationFunctionError integrationFunctionError = IntegrationFunctionError.WRAP_502_BAD_RESPONSE;
				if(this.unwrap) {
					integrationFunctionError = IntegrationFunctionError.INTEROPERABILITY_PROFILE_RESPONSE_ERROR;
				}
				codice = erroriProperties.getErrorType_noWrap(integrationFunctionError);
				if(erroriProperties.isForceGenericDetails_noWrap(integrationFunctionError)) {
					msg = erroriProperties.getGenericDetails_noWrap(integrationFunctionError);
					equalsMatch = true;
				}
			}
			
			if("erroreApplicativoAsSoapFaultRidefinito".equals(servizioApplicativoFruitore) || "erroreApplicativoAsXmlRidefinito".equals(servizioApplicativoFruitore)){
				actor = "ACTOR_RIDEFINITO";
			}
			
			try {
				boolean responseAsXml = false;
				if("erroreApplicativoAsXmlRidefinito".equals(servizioApplicativoFruitore) || "erroreApplicativoAsSoapXmlDefault".equals(servizioApplicativoFruitore)) {
					responseAsXml = true;
				}
				if(responseAsXml) {
					client.setForceResponseAsBinaryProcessor(true);
				}
				
				client.run();

				if("erroreApplicativoAsSoapFaultDefault".equals(servizioApplicativoFruitore) ||
						"erroreApplicativoAsSoapFaultRidefinito".equals(servizioApplicativoFruitore)){
					Reporter.log("Invocazione porta delegata inesistente non ha causato errori.");
					throw new TestSuiteException("Invocazione porta delegata inesistente non ha causato errori.");
				}

				Reporter.log("Return Code '"+servizioApplicativoFruitore+"' '"+client.getCodiceStatoHTTP()+"'");
				if(responseAsXml) {
					Assert.assertTrue(client.getCodiceStatoHTTP()==502);
				}
				else {
					Assert.assertTrue(client.getCodiceStatoHTTP()==200);
				}
				
				byte [] xmlErroreApplicativo = null;
				if(responseAsXml) {
					xmlErroreApplicativo = client.getMessaggioXMLRisposta();
					response = xmlErroreApplicativo;
					Assert.assertTrue(xmlErroreApplicativo!=null);
				}
				else {
				
					Message msgRisposta = client.getResponseMessage();
					Assert.assertTrue(msgRisposta!=null);
					Assert.assertTrue(msgRisposta.getSOAPBody()!=null);
					Assert.assertTrue(msgRisposta.getSOAPBody().hasChildNodes());
					
					try {
						xmlErroreApplicativo = org.openspcoop2.message.xml.MessageXMLUtils.DEFAULT.toByteArray(msgRisposta.getSOAPBody().getFirstChild(),true);
					}catch(Throwable t) {
						// normalize per conflito di librerie axis - saaj
						org.w3c.dom.Document d = org.openspcoop2.message.xml.MessageXMLUtils.DEFAULT.newDocument();
						Node n = d.importNode(msgRisposta.getSOAPBody().getFirstChild(), true);
						xmlErroreApplicativo = org.openspcoop2.message.xml.MessageXMLUtils.DEFAULT.toByteArray(n,true);
					}
					response = msgRisposta.getSOAPBody();
					
				}
					
				Utilities.verificaErroreApplicativoCnipa(org.openspcoop2.message.xml.MessageXMLUtils.getInstance(messageFactory).newElement(xmlErroreApplicativo), 
						idPorta,"SbustamentoRisposte", 
						codiceEGOV, 
						msg, equalsMatch);	
				
			} catch (AxisFault error) {
				
				response = error;
				
				Assert.assertTrue(client.getCodiceStatoHTTP()==500);
								
				Utilities.verificaFaultIntegrazione(error, actor,
						idPorta,"SbustamentoRisposte", 
						codice, 
						msg, equalsMatch,
						codiceEGOV);				
			}finally{
				try {
					dbComponentFruitore.close();
				}catch(Exception eClose) {}
				try {
					dbComponentErogatore.close();
				}catch(Exception eClose) {}	
				
				ErroreAttesoOpenSPCoopLogCore err = new ErroreAttesoOpenSPCoopLogCore();
				err.setIntervalloInferiore(dataInizioTest);
				err.setIntervalloSuperiore(dataFineTest);
				err.setMsgErrore("Errore avvenuto durante la consegna HTTP: Connection refused");
				this.erroriAttesiOpenSPCoopCore.add(err);
			}
		}catch(Exception e){
			throw e;
		}finally{
			try {
				dbComponentFruitore.close();
			}catch(Exception eClose) {}
			try {
				dbComponentErogatore.close();
			}catch(Exception eClose) {}
			
			super.unlockForCode(this.genericCode);
			dataFineTest = DateManager.getDate();
		}	
		
		return response;
	}
	
	/** SERVIZIO TUNNEL SOAP: ERRORE CONNETTORE SA, connection refused ARRICCHITO */
	
	protected Object[][] _personalizzazioniErroreConnectionRefusedServizioApplicativoTunnelSOAP(){
		return new Object[][]{{null},
				{"erroreApplicativoAsSoapFaultDefault"},
				{"erroreApplicativoAsSoapFaultRidefinito"},
				{"erroreApplicativoAsSoapXmlDefault"},
				{"erroreApplicativoAsXmlRidefinito"},
		};
	}
	protected void _testServizioApplicativoConnectionRefusedServizioApplicativo_TunnelSOAP(String servizioApplicativoFruitore) throws Exception{

		DatabaseComponent dbComponentFruitore = null;
		DatabaseComponent dbComponentErogatore = null;
		ByteArrayOutputStream bout = null;
		java.io.FileInputStream fin = null;
		
		Date dataInizioTest = null;
		Date dataFineTest = null;
		try{
			super.lockForCode(this.genericCode, this.unwrap);
			dataInizioTest = DateManager.getDate();
		
			OpenSPCoop2MessageFactory messageFactory = OpenSPCoop2MessageFactory.getDefaultMessageFactory();
			
			String portaDelegata = CostantiTestSuite.PORTA_DELEGATA_CONNETTORE_ERRATO_SA_SINCRONO_STATELESS;
			
			fin = new java.io.FileInputStream(new File(Utilities.testSuiteProperties.getXmlSenzaSoapFileName()));
			bout = new ByteArrayOutputStream();
			byte[]bytes = new byte[2048];
			int letti = 0;
			while( (letti = fin.read(bytes)) != -1 ){
				bout.write(bytes, 0, letti);
			}
			bout.flush();
			bout.close();
			
			ClientHttpGenerico client=new ClientHttpGenerico(new Repository());
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore().replace("out", "out/xml2soap"));
			client.setPortaDelegata(portaDelegata);
			client.connectToSoapEngine();
			if(servizioApplicativoFruitore!=null){
				client.setProperty(TestSuiteProperties.getInstance().getServizioApplicativoTrasporto(), servizioApplicativoFruitore);
			}
			client.setMessaggioXMLRichiesta(bout.toByteArray());
			client.setRispostaDaGestire(true);
			// AttesaTerminazioneMessaggi
			if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()){
				dbComponentFruitore = DatabaseProperties.getDatabaseComponentFruitore();
				dbComponentErogatore = DatabaseProperties.getDatabaseComponentErogatore();

				client.setAttesaTerminazioneMessaggi(true);
				client.setDbAttesaTerminazioneMessaggiFruitore(dbComponentFruitore);
				client.setDbAttesaTerminazioneMessaggiErogatore(dbComponentErogatore);
			}
			
			String idPorta = "MinisteroFruitoreSPCoopIT";
			// CNIPA richiede un codice eGov 
			@SuppressWarnings("unused")
			String codice = Utilities.toString(CodiceErroreCooperazione.ERRORE_GENERICO_PROCESSAMENTO_MESSAGGIO);
			String codiceEGOV = Utilities.toString(CodiceErroreCooperazione.ERRORE_GENERICO_PROCESSAMENTO_MESSAGGIO);
			String msg = CostantiErroriIntegrazione.MSG_516_SERVIZIO_APPLICATIVO_NON_DISPONIBILE;
			boolean equalsMatch = false;
			
			ErroriProperties erroriProperties = ErroriProperties.getInstance(this.log);
			if(this.genericCode) {
				IntegrationFunctionError integrationFunctionError = IntegrationFunctionError.WRAP_502_BAD_RESPONSE;
				if(this.unwrap) {
					integrationFunctionError = IntegrationFunctionError.INTEROPERABILITY_PROFILE_RESPONSE_ERROR;
				}
				codice = erroriProperties.getErrorType_noWrap(integrationFunctionError);
				if(erroriProperties.isForceGenericDetails_noWrap(integrationFunctionError)) {
					msg = erroriProperties.getGenericDetails_noWrap(integrationFunctionError);
					equalsMatch = true;
				}
			}
			
			if("erroreApplicativoAsSoapFaultRidefinito".equals(servizioApplicativoFruitore) || "erroreApplicativoAsXmlRidefinito".equals(servizioApplicativoFruitore)){
			}
			
			try {
				boolean responseAsXml = true; // tunnel soap sono tutti errori xml in risposta
				if(responseAsXml) {
					client.setForceResponseAsBinaryProcessor(true);
				}
				
				client.run();

				Reporter.log("Return Code '"+servizioApplicativoFruitore+"' '"+client.getCodiceStatoHTTP()+"'");
				if(responseAsXml) {
					Assert.assertTrue(client.getCodiceStatoHTTP()==502);
				}
				else {
					Assert.assertTrue(client.getCodiceStatoHTTP()==500);
				}
				
				byte [] xmlErroreApplicativo = client.getMessaggioXMLRisposta();
				Assert.assertTrue(xmlErroreApplicativo!=null);
				
				Utilities.verificaErroreApplicativoCnipa(org.openspcoop2.message.xml.MessageXMLUtils.getInstance(messageFactory).newElement(xmlErroreApplicativo), 
						idPorta,"SbustamentoRisposte", 
						codiceEGOV, 
						msg, equalsMatch);	
								
			} catch (AxisFault error) {
				throw error;			
			}finally{
				try {
					dbComponentFruitore.close();
				}catch(Exception eClose) {}
				try {
					dbComponentErogatore.close();
				}catch(Exception eClose) {}	
				
				ErroreAttesoOpenSPCoopLogCore err = new ErroreAttesoOpenSPCoopLogCore();
				err.setIntervalloInferiore(dataInizioTest);
				err.setIntervalloSuperiore(dataFineTest);
				err.setMsgErrore("Errore avvenuto durante la consegna HTTP: Connection refused");
				this.erroriAttesiOpenSPCoopCore.add(err);
			}
		}catch(Exception e){
			throw e;
		}finally{
			try{
				fin.close();
			}catch(Exception e){}
			try {
				dbComponentFruitore.close();
			}catch(Exception eClose) {}
			try {
				dbComponentErogatore.close();
			}catch(Exception eClose) {}
			
			super.unlockForCode(this.genericCode);
			dataFineTest = DateManager.getDate();
		}	
				
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/** ERRORE CONNETTORE PDD: Connect Timed Out ARRICCHITO */
	
	protected Object[][] _personalizzazioniErroreTimedOut(){
		return new Object[][]{{null},
				{"erroreApplicativoAsSoapFaultDefault"},
				{"erroreApplicativoAsSoapFaultRidefinito"},
				{"erroreApplicativoAsSoapXmlDefault"},
				{"erroreApplicativoAsXmlRidefinito"},
		};
	}
	protected Object _testServizioApplicativoTimedOut(String servizioApplicativoFruitore, boolean readTimedOut) throws Exception{

		DatabaseComponent dbComponentFruitore = null;
		DatabaseComponent dbComponentErogatore = null;
		Object response = null;
		
		Date dataInizioTest = null;
		Date dataFineTest = null;
		try{
			super.lockForCode(this.genericCode, this.unwrap);
			dataInizioTest = DateManager.getDate();
		
			OpenSPCoop2MessageFactory messageFactory = OpenSPCoop2MessageFactory.getDefaultMessageFactory();
			
			String portaDelegata = CostantiTestSuite.PORTA_DELEGATA_CONNETTORE_ERRATO_PDD_SINCRONO_STATELESS_CONNECT_TIMED_OUT;
			if(readTimedOut) {
				portaDelegata = CostantiTestSuite.PORTA_DELEGATA_CONNETTORE_ERRATO_PDD_SINCRONO_STATELESS_CONNECT_READ_TIMED_OUT;
			}
			
			ClientHttpGenerico client=new ClientHttpGenerico(new Repository());
			client.setConnectionReadTimeout(60000);
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(portaDelegata);
			client.connectToSoapEngine();
			if(servizioApplicativoFruitore!=null){
				client.setProperty(TestSuiteProperties.getInstance().getServizioApplicativoTrasporto(), servizioApplicativoFruitore);
			}
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
			
			String actor = org.openspcoop2.testsuite.core.CostantiTestSuite.OPENSPCOOP2_INTEGRATION_ACTOR;
			String idPorta = "MinisteroFruitoreSPCoopIT";
			String codice = Utilities.toString(CodiceErroreIntegrazione.CODICE_516_CONNETTORE_UTILIZZO_CON_ERRORE);
			String msg = CostantiErroriIntegrazione.MSG_516_PDD_NON_DISPONIBILE.replace(CostantiProtocollo.KEYWORDPDD_NON_DISPONIBILE, 
					readTimedOut ? "spc-SoggettoConnettoreErratoConnectReadTimedOut" : "spc-SoggettoConnettoreErratoConnectTimedOut");
			boolean equalsMatch = true;
			
			ErroriProperties erroriProperties = ErroriProperties.getInstance(this.log);
			if(this.genericCode) {
				IntegrationFunctionError integrationFunctionError = IntegrationFunctionError.SERVICE_UNAVAILABLE;
				if(readTimedOut) {
					integrationFunctionError = IntegrationFunctionError.ENDPOINT_REQUEST_TIMED_OUT;
				}
				codice = erroriProperties.getErrorType_noWrap(integrationFunctionError);
				if(erroriProperties.isForceGenericDetails_noWrap(integrationFunctionError)) {
					msg = erroriProperties.getGenericDetails_noWrap(integrationFunctionError);
					equalsMatch = true;
				}
			}
			
			if("erroreApplicativoAsSoapFaultRidefinito".equals(servizioApplicativoFruitore) || "erroreApplicativoAsXmlRidefinito".equals(servizioApplicativoFruitore)){
				actor = "ACTOR_RIDEFINITO";
				if(!this.genericCode) {
					codice = "PREFIX_PERSONALIZZATO_516";
				}
			}
			
			try {
				boolean responseAsXml = false;
				if("erroreApplicativoAsXmlRidefinito".equals(servizioApplicativoFruitore) || "erroreApplicativoAsSoapXmlDefault".equals(servizioApplicativoFruitore)) {
					responseAsXml = true;
				}
				if(responseAsXml) {
					client.setForceResponseAsBinaryProcessor(true);
				}
				
				client.run();

				if("erroreApplicativoAsSoapFaultDefault".equals(servizioApplicativoFruitore) ||
						"erroreApplicativoAsSoapFaultRidefinito".equals(servizioApplicativoFruitore)){
					Reporter.log("Invocazione porta delegata inesistente non ha causato errori.");
					throw new TestSuiteException("Invocazione porta delegata inesistente non ha causato errori.");
				}

				Reporter.log("Return Code '"+servizioApplicativoFruitore+"' '"+client.getCodiceStatoHTTP()+"'");
				if(responseAsXml) {
					if(readTimedOut) {
						Assert.assertTrue(client.getCodiceStatoHTTP()==504);
					}
					else {
						Assert.assertTrue(client.getCodiceStatoHTTP()==503);
					}
				}
				else {
					Assert.assertTrue(client.getCodiceStatoHTTP()==200);
				}
				
				byte [] xmlErroreApplicativo = null;
				if(responseAsXml) {
					xmlErroreApplicativo = client.getMessaggioXMLRisposta();
					response = xmlErroreApplicativo;
					Assert.assertTrue(xmlErroreApplicativo!=null);
				}
				else {
				
					Message msgRisposta = client.getResponseMessage();
					Assert.assertTrue(msgRisposta!=null);
					Assert.assertTrue(msgRisposta.getSOAPBody()!=null);
					Assert.assertTrue(msgRisposta.getSOAPBody().hasChildNodes());
					
					try {
						xmlErroreApplicativo = org.openspcoop2.message.xml.MessageXMLUtils.DEFAULT.toByteArray(msgRisposta.getSOAPBody().getFirstChild(),true);
					}catch(Throwable t) {
						// normalize per conflito di librerie axis - saaj
						org.w3c.dom.Document d = org.openspcoop2.message.xml.MessageXMLUtils.DEFAULT.newDocument();
						Node n = d.importNode(msgRisposta.getSOAPBody().getFirstChild(), true);
						xmlErroreApplicativo = org.openspcoop2.message.xml.MessageXMLUtils.DEFAULT.toByteArray(n,true);
					}
					response = msgRisposta.getSOAPBody();
					
				}
				
				Utilities.verificaErroreApplicativoCnipa(org.openspcoop2.message.xml.MessageXMLUtils.getInstance(messageFactory).newElement(xmlErroreApplicativo), 
						idPorta,"InoltroBuste", 
						codice, 
						msg, equalsMatch);	
				
			} catch (AxisFault error) {
				
				response = error;
				
				Assert.assertTrue(client.getCodiceStatoHTTP()==500);
								
				Utilities.verificaFaultIntegrazione(error, actor,
						idPorta,"InoltroBuste", 
						codice, 
						msg, equalsMatch);				
			}finally{
				try {
					dbComponentFruitore.close();
				}catch(Exception eClose) {}
				try {
					dbComponentErogatore.close();
				}catch(Exception eClose) {}	
				
				ErroreAttesoOpenSPCoopLogCore err = new ErroreAttesoOpenSPCoopLogCore();
				err.setIntervalloInferiore(dataInizioTest);
				err.setIntervalloSuperiore(dataFineTest);
				if(readTimedOut) {
					err.setMsgErrore("Errore avvenuto durante la consegna HTTP: Read timed out");
				}
				else {
					err.setMsgErrore("Errore avvenuto durante la consegna HTTP: connect timed out");
				}
				this.erroriAttesiOpenSPCoopCore.add(err);
			}
		}catch(Exception e){
			throw e;
		}finally{
			try {
				dbComponentFruitore.close();
			}catch(Exception eClose) {}
			try {
				dbComponentErogatore.close();
			}catch(Exception eClose) {}
			
			super.unlockForCode(this.genericCode);
			dataFineTest = DateManager.getDate();
		}	
		
		return response;
	}
	
	/** SERVIZIO TUNNEL SOAP: ERRORE CONNETTORE, Connect Timed Out ARRICCHITO */
	
	protected Object[][] _personalizzazioniErroreTimedOutTunnelSOAP(){
		return new Object[][]{{null},
				{"erroreApplicativoAsSoapFaultDefault"},
				{"erroreApplicativoAsSoapFaultRidefinito"},
				{"erroreApplicativoAsSoapXmlDefault"},
				{"erroreApplicativoAsXmlRidefinito"},
		};
	}
	protected void _testServizioApplicativoTimedOut_TunnelSOAP(String servizioApplicativoFruitore, boolean readTimedOut) throws Exception{

		DatabaseComponent dbComponentFruitore = null;
		DatabaseComponent dbComponentErogatore = null;
		ByteArrayOutputStream bout = null;
		java.io.FileInputStream fin = null;
		
		Date dataInizioTest = null;
		Date dataFineTest = null;
		try{
			super.lockForCode(this.genericCode, this.unwrap);
			dataInizioTest = DateManager.getDate();
		
			OpenSPCoop2MessageFactory messageFactory = OpenSPCoop2MessageFactory.getDefaultMessageFactory();
			
			String portaDelegata = CostantiTestSuite.PORTA_DELEGATA_CONNETTORE_ERRATO_PDD_SINCRONO_STATELESS_CONNECT_TIMED_OUT;
			if(readTimedOut) {
				portaDelegata = CostantiTestSuite.PORTA_DELEGATA_CONNETTORE_ERRATO_PDD_SINCRONO_STATELESS_CONNECT_READ_TIMED_OUT;
			}
			
			fin = new java.io.FileInputStream(new File(Utilities.testSuiteProperties.getXmlSenzaSoapFileName()));
			bout = new ByteArrayOutputStream();
			byte[]bytes = new byte[2048];
			int letti = 0;
			while( (letti = fin.read(bytes)) != -1 ){
				bout.write(bytes, 0, letti);
			}
			bout.flush();
			bout.close();
			
			ClientHttpGenerico client=new ClientHttpGenerico(new Repository());
			client.setConnectionReadTimeout(60000);
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore().replace("out", "out/xml2soap"));
			client.setPortaDelegata(portaDelegata);
			client.connectToSoapEngine();
			if(servizioApplicativoFruitore!=null){
				client.setProperty(TestSuiteProperties.getInstance().getServizioApplicativoTrasporto(), servizioApplicativoFruitore);
			}
			client.setMessaggioXMLRichiesta(bout.toByteArray());
			client.setRispostaDaGestire(true);
			// AttesaTerminazioneMessaggi
			if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()){
				dbComponentFruitore = DatabaseProperties.getDatabaseComponentFruitore();
				dbComponentErogatore = DatabaseProperties.getDatabaseComponentErogatore();

				client.setAttesaTerminazioneMessaggi(true);
				client.setDbAttesaTerminazioneMessaggiFruitore(dbComponentFruitore);
				client.setDbAttesaTerminazioneMessaggiErogatore(dbComponentErogatore);
			}
			
			String idPorta = "MinisteroFruitoreSPCoopIT";
			String codice = Utilities.toString(CodiceErroreIntegrazione.CODICE_516_CONNETTORE_UTILIZZO_CON_ERRORE);
			String msg = CostantiErroriIntegrazione.MSG_516_PDD_NON_DISPONIBILE.replace(CostantiProtocollo.KEYWORDPDD_NON_DISPONIBILE, 
					readTimedOut ? "spc-SoggettoConnettoreErratoConnectReadTimedOut" : "spc-SoggettoConnettoreErratoConnectTimedOut");
			boolean equalsMatch = true;
			
			ErroriProperties erroriProperties = ErroriProperties.getInstance(this.log);
			if(this.genericCode) {
				IntegrationFunctionError integrationFunctionError = IntegrationFunctionError.SERVICE_UNAVAILABLE;
				if(readTimedOut) {
					integrationFunctionError = IntegrationFunctionError.ENDPOINT_REQUEST_TIMED_OUT;
				}
				codice = erroriProperties.getErrorType_noWrap(integrationFunctionError);
				if(erroriProperties.isForceGenericDetails_noWrap(integrationFunctionError)) {
					msg = erroriProperties.getGenericDetails_noWrap(integrationFunctionError);
					equalsMatch = true;
				}
			}
			
			if("erroreApplicativoAsSoapFaultRidefinito".equals(servizioApplicativoFruitore) || "erroreApplicativoAsXmlRidefinito".equals(servizioApplicativoFruitore)){
				if(!this.genericCode) {
					codice = "PREFIX_PERSONALIZZATO_516";
				}
			}
			
			try {
				boolean responseAsXml = true; // tunnel soap sono tutti errori xml in risposta
				if(responseAsXml) {
					client.setForceResponseAsBinaryProcessor(true);
				}
				
				client.run();

				Reporter.log("Return Code '"+servizioApplicativoFruitore+"' '"+client.getCodiceStatoHTTP()+"'");
				if(responseAsXml) {
					if(readTimedOut) {
						Assert.assertTrue(client.getCodiceStatoHTTP()==504);
					}
					else {
						Assert.assertTrue(client.getCodiceStatoHTTP()==503);
					}
				}
				else {
					Assert.assertTrue(client.getCodiceStatoHTTP()==500);
				}
				
				byte [] xmlErroreApplicativo = client.getMessaggioXMLRisposta();
				Assert.assertTrue(xmlErroreApplicativo!=null);
				
				Utilities.verificaErroreApplicativoCnipa(org.openspcoop2.message.xml.MessageXMLUtils.getInstance(messageFactory).newElement(xmlErroreApplicativo), 
						idPorta,"InoltroBuste", 
						codice, 
						msg, equalsMatch);	
								
			} catch (AxisFault error) {
				throw error;			
			}finally{
				try {
					dbComponentFruitore.close();
				}catch(Exception eClose) {}
				try {
					dbComponentErogatore.close();
				}catch(Exception eClose) {}	
				
				ErroreAttesoOpenSPCoopLogCore err = new ErroreAttesoOpenSPCoopLogCore();
				err.setIntervalloInferiore(dataInizioTest);
				err.setIntervalloSuperiore(dataFineTest);
				if(readTimedOut) {
					err.setMsgErrore("Errore avvenuto durante la consegna HTTP: Read timed out");
				}
				else {
					err.setMsgErrore("Errore avvenuto durante la consegna HTTP: connect timed out");
				}
				this.erroriAttesiOpenSPCoopCore.add(err);
			}
		}catch(Exception e){
			throw e;
		}finally{
			try{
				fin.close();
			}catch(Exception e){}
			try {
				dbComponentFruitore.close();
			}catch(Exception eClose) {}
			try {
				dbComponentErogatore.close();
			}catch(Exception eClose) {}
			
			super.unlockForCode(this.genericCode);
			dataFineTest = DateManager.getDate();
		}	
				
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/** ERRORE CONNETTORE SA: Connect Timed Out ARRICCHITO */
	
	protected Object[][] _personalizzazioniErroreTimedOutServizioApplicativo(){
		return new Object[][]{{null},
				{"erroreApplicativoAsSoapFaultDefault"},
				{"erroreApplicativoAsSoapFaultRidefinito"},
				{"erroreApplicativoAsSoapXmlDefault"},
				{"erroreApplicativoAsXmlRidefinito"},
		};
	}
	protected Object _testServizioApplicativoTimedOutServizioApplicativo(String servizioApplicativoFruitore, boolean readTimedOut) throws Exception{

		DatabaseComponent dbComponentFruitore = null;
		DatabaseComponent dbComponentErogatore = null;
		Object response = null;
		
		Date dataInizioTest = null;
		Date dataFineTest = null;
		try{
			super.lockForCode(this.genericCode, this.unwrap);
			dataInizioTest = DateManager.getDate();
		
			OpenSPCoop2MessageFactory messageFactory = OpenSPCoop2MessageFactory.getDefaultMessageFactory();
			
			String portaDelegata = CostantiTestSuite.PORTA_DELEGATA_CONNETTORE_ERRATO_SA_SINCRONO_STATELESS_CONNECT_TIMED_OUT;
			if(readTimedOut) {
				portaDelegata = CostantiTestSuite.PORTA_DELEGATA_CONNETTORE_ERRATO_SA_SINCRONO_STATELESS_CONNECT_READ_TIMED_OUT;
			}
			
			ClientHttpGenerico client=new ClientHttpGenerico(new Repository());
			client.setConnectionReadTimeout(60000);
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(portaDelegata);
			client.connectToSoapEngine();
			if(servizioApplicativoFruitore!=null){
				client.setProperty(TestSuiteProperties.getInstance().getServizioApplicativoTrasporto(), servizioApplicativoFruitore);
			}
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
			
			String actor = org.openspcoop2.testsuite.core.CostantiTestSuite.OPENSPCOOP2_INTEGRATION_ACTOR;
			String idPorta = "MinisteroFruitoreSPCoopIT";
			String codice = Utilities.toString(CodiceErroreCooperazione.ERRORE_GENERICO_PROCESSAMENTO_MESSAGGIO);
			String codiceEGOV = Utilities.toString(CodiceErroreCooperazione.ERRORE_GENERICO_PROCESSAMENTO_MESSAGGIO);
			String msg = CostantiErroriIntegrazione.MSG_516_SERVIZIO_APPLICATIVO_NON_DISPONIBILE;
			boolean equalsMatch = false;
			
			ErroriProperties erroriProperties = ErroriProperties.getInstance(this.log);
			if(this.genericCode) {
				IntegrationFunctionError integrationFunctionError = IntegrationFunctionError.WRAP_502_BAD_RESPONSE;
				if(this.unwrap) {
					integrationFunctionError = IntegrationFunctionError.INTEROPERABILITY_PROFILE_RESPONSE_ERROR;
				}
				codice = erroriProperties.getErrorType_noWrap(integrationFunctionError);
				if(erroriProperties.isForceGenericDetails_noWrap(integrationFunctionError)) {
					msg = erroriProperties.getGenericDetails_noWrap(integrationFunctionError);
					equalsMatch = true;
				}
			}
			
			if("erroreApplicativoAsSoapFaultRidefinito".equals(servizioApplicativoFruitore) || "erroreApplicativoAsXmlRidefinito".equals(servizioApplicativoFruitore)){
				actor = "ACTOR_RIDEFINITO";
			}
			
			try {
				boolean responseAsXml = false;
				if("erroreApplicativoAsXmlRidefinito".equals(servizioApplicativoFruitore) || "erroreApplicativoAsSoapXmlDefault".equals(servizioApplicativoFruitore)) {
					responseAsXml = true;
				}
				if(responseAsXml) {
					client.setForceResponseAsBinaryProcessor(true);
				}
				
				client.run();

				if("erroreApplicativoAsSoapFaultDefault".equals(servizioApplicativoFruitore) ||
						"erroreApplicativoAsSoapFaultRidefinito".equals(servizioApplicativoFruitore)){
					Reporter.log("Invocazione porta delegata inesistente non ha causato errori.");
					throw new TestSuiteException("Invocazione porta delegata inesistente non ha causato errori.");
				}

				Reporter.log("Return Code '"+servizioApplicativoFruitore+"' '"+client.getCodiceStatoHTTP()+"'");
				if(responseAsXml) {
					Assert.assertTrue(client.getCodiceStatoHTTP()==502);
				}
				else {
					Assert.assertTrue(client.getCodiceStatoHTTP()==200);
				}
				
				byte [] xmlErroreApplicativo = null;
				if(responseAsXml) {
					xmlErroreApplicativo = client.getMessaggioXMLRisposta();
					response = xmlErroreApplicativo;
					Assert.assertTrue(xmlErroreApplicativo!=null);
				}
				else {
				
					Message msgRisposta = client.getResponseMessage();
					Assert.assertTrue(msgRisposta!=null);
					Assert.assertTrue(msgRisposta.getSOAPBody()!=null);
					Assert.assertTrue(msgRisposta.getSOAPBody().hasChildNodes());
					
					try {
						xmlErroreApplicativo = org.openspcoop2.message.xml.MessageXMLUtils.DEFAULT.toByteArray(msgRisposta.getSOAPBody().getFirstChild(),true);
					}catch(Throwable t) {
						// normalize per conflito di librerie axis - saaj
						org.w3c.dom.Document d = org.openspcoop2.message.xml.MessageXMLUtils.DEFAULT.newDocument();
						Node n = d.importNode(msgRisposta.getSOAPBody().getFirstChild(), true);
						xmlErroreApplicativo = org.openspcoop2.message.xml.MessageXMLUtils.DEFAULT.toByteArray(n,true);
					}
					response = msgRisposta.getSOAPBody();
					
				}
				
				Utilities.verificaErroreApplicativoCnipa(org.openspcoop2.message.xml.MessageXMLUtils.getInstance(messageFactory).newElement(xmlErroreApplicativo), 
						idPorta,"SbustamentoRisposte", 
						codiceEGOV, 
						msg, equalsMatch);	
				
			} catch (AxisFault error) {
				
				response = error;
				
				Assert.assertTrue(client.getCodiceStatoHTTP()==500);
								
				Utilities.verificaFaultIntegrazione(error, actor,
						idPorta,"SbustamentoRisposte", 
						codice, 
						msg, equalsMatch, codiceEGOV);				
			}finally{
				try {
					dbComponentFruitore.close();
				}catch(Exception eClose) {}
				try {
					dbComponentErogatore.close();
				}catch(Exception eClose) {}	
							
				ErroreAttesoOpenSPCoopLogCore err = new ErroreAttesoOpenSPCoopLogCore();
				err.setIntervalloInferiore(dataInizioTest);
				err.setIntervalloSuperiore(dataFineTest);
				if(readTimedOut) {
					err.setMsgErrore("Errore avvenuto durante la consegna HTTP: Read timed out");
				}
				else {
					err.setMsgErrore("Errore avvenuto durante la consegna HTTP: connect timed out");
				}
				this.erroriAttesiOpenSPCoopCore.add(err);
			}
		}catch(Exception e){
			throw e;
		}finally{
			try {
				dbComponentFruitore.close();
			}catch(Exception eClose) {}
			try {
				dbComponentErogatore.close();
			}catch(Exception eClose) {}
			
			super.unlockForCode(this.genericCode);
			dataFineTest = DateManager.getDate();
		}	
		
		return response;
	}
	
	/** SERVIZIO TUNNEL SOAP: ERRORE CONNETTORE SA, Connect Timed Out ARRICCHITO */
	
	protected Object[][] _personalizzazioniErroreTimedOutServizioApplicativoTunnelSOAP(){
		return new Object[][]{{null},
				{"erroreApplicativoAsSoapFaultDefault"},
				{"erroreApplicativoAsSoapFaultRidefinito"},
				{"erroreApplicativoAsSoapXmlDefault"},
				{"erroreApplicativoAsXmlRidefinito"},
		};
	}
	protected void _testServizioApplicativoTimedOutServizioApplicativo_TunnelSOAP(String servizioApplicativoFruitore, boolean readTimedOut) throws Exception{
		
		DatabaseComponent dbComponentFruitore = null;
		DatabaseComponent dbComponentErogatore = null;
		ByteArrayOutputStream bout = null;
		java.io.FileInputStream fin = null;
		
		Date dataInizioTest = null;
		Date dataFineTest = null;
		try{
			super.lockForCode(this.genericCode, this.unwrap);
			dataInizioTest = DateManager.getDate();
		
			OpenSPCoop2MessageFactory messageFactory = OpenSPCoop2MessageFactory.getDefaultMessageFactory();
			
			String portaDelegata = CostantiTestSuite.PORTA_DELEGATA_CONNETTORE_ERRATO_SA_SINCRONO_STATELESS_CONNECT_TIMED_OUT;
			if(readTimedOut) {
				portaDelegata = CostantiTestSuite.PORTA_DELEGATA_CONNETTORE_ERRATO_SA_SINCRONO_STATELESS_CONNECT_READ_TIMED_OUT;
			}
			
			fin = new java.io.FileInputStream(new File(Utilities.testSuiteProperties.getXmlSenzaSoapFileName()));
			bout = new ByteArrayOutputStream();
			byte[]bytes = new byte[2048];
			int letti = 0;
			while( (letti = fin.read(bytes)) != -1 ){
				bout.write(bytes, 0, letti);
			}
			bout.flush();
			bout.close();
			
			ClientHttpGenerico client=new ClientHttpGenerico(new Repository());
			client.setConnectionReadTimeout(60000);
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore().replace("out", "out/xml2soap"));
			client.setPortaDelegata(portaDelegata);
			client.connectToSoapEngine();
			if(servizioApplicativoFruitore!=null){
				client.setProperty(TestSuiteProperties.getInstance().getServizioApplicativoTrasporto(), servizioApplicativoFruitore);
			}
			client.setMessaggioXMLRichiesta(bout.toByteArray());
			client.setRispostaDaGestire(true);
			// AttesaTerminazioneMessaggi
			if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()){
				dbComponentFruitore = DatabaseProperties.getDatabaseComponentFruitore();
				dbComponentErogatore = DatabaseProperties.getDatabaseComponentErogatore();

				client.setAttesaTerminazioneMessaggi(true);
				client.setDbAttesaTerminazioneMessaggiFruitore(dbComponentFruitore);
				client.setDbAttesaTerminazioneMessaggiErogatore(dbComponentErogatore);
			}
			
			String idPorta = "MinisteroFruitoreSPCoopIT";
			// CNIPA prevede un codice egov
			@SuppressWarnings("unused")
			String codice = Utilities.toString(CodiceErroreCooperazione.ERRORE_GENERICO_PROCESSAMENTO_MESSAGGIO);
			String codiceEGOV = Utilities.toString(CodiceErroreCooperazione.ERRORE_GENERICO_PROCESSAMENTO_MESSAGGIO);
			String msg = CostantiErroriIntegrazione.MSG_516_SERVIZIO_APPLICATIVO_NON_DISPONIBILE;
			boolean equalsMatch = false;
			
			ErroriProperties erroriProperties = ErroriProperties.getInstance(this.log);
			if(this.genericCode) {
				IntegrationFunctionError integrationFunctionError = IntegrationFunctionError.WRAP_502_BAD_RESPONSE;
				if(this.unwrap) {
					integrationFunctionError = IntegrationFunctionError.INTEROPERABILITY_PROFILE_RESPONSE_ERROR;
				}
				codice = erroriProperties.getErrorType_noWrap(integrationFunctionError);
				if(erroriProperties.isForceGenericDetails_noWrap(integrationFunctionError)) {
					msg = erroriProperties.getGenericDetails_noWrap(integrationFunctionError);
					equalsMatch = true;
				}
			}
			
			if("erroreApplicativoAsSoapFaultRidefinito".equals(servizioApplicativoFruitore) || "erroreApplicativoAsXmlRidefinito".equals(servizioApplicativoFruitore)){
			}
			
			try {
				boolean responseAsXml = true; // tunnel soap sono tutti errori xml in risposta
				if(responseAsXml) {
					client.setForceResponseAsBinaryProcessor(true);
				}
				
				client.run();

				Reporter.log("Return Code '"+servizioApplicativoFruitore+"' '"+client.getCodiceStatoHTTP()+"'");
				if(responseAsXml) {
					Assert.assertTrue(client.getCodiceStatoHTTP()==502);
				}
				else {
					Assert.assertTrue(client.getCodiceStatoHTTP()==500);
				}
				
				byte [] xmlErroreApplicativo = client.getMessaggioXMLRisposta();
				Assert.assertTrue(xmlErroreApplicativo!=null);
				
				Utilities.verificaErroreApplicativoCnipa(org.openspcoop2.message.xml.MessageXMLUtils.getInstance(messageFactory).newElement(xmlErroreApplicativo), 
						idPorta,"SbustamentoRisposte", 
						codiceEGOV, 
						msg, equalsMatch);	
								
			} catch (AxisFault error) {
				throw error;			
			}finally{
				try {
					dbComponentFruitore.close();
				}catch(Exception eClose) {}
				try {
					dbComponentErogatore.close();
				}catch(Exception eClose) {}	
							
				ErroreAttesoOpenSPCoopLogCore err = new ErroreAttesoOpenSPCoopLogCore();
				err.setIntervalloInferiore(dataInizioTest);
				err.setIntervalloSuperiore(dataFineTest);
				if(readTimedOut) {
					err.setMsgErrore("Errore avvenuto durante la consegna HTTP: Read timed out");
				}
				else {
					err.setMsgErrore("Errore avvenuto durante la consegna HTTP: connect timed out");
				}
				this.erroriAttesiOpenSPCoopCore.add(err);
			}
		}catch(Exception e){
			throw e;
		}finally{
			try{
				fin.close();
			}catch(Exception e){}
			try {
				dbComponentFruitore.close();
			}catch(Exception eClose) {}
			try {
				dbComponentErogatore.close();
			}catch(Exception eClose) {}
			
			super.unlockForCode(this.genericCode);
			dataFineTest = DateManager.getDate();
		}	
				
	}
	
	
}
