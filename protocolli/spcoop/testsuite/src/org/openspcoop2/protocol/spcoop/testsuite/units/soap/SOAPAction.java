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



package org.openspcoop2.protocol.spcoop.testsuite.units.soap;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.soap.SOAPException;

import org.apache.axis.AxisFault;
import org.apache.axis.Message;
import org.openspcoop2.core.constants.TipoPdD;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.constants.CodiceErroreIntegrazione;
import org.openspcoop2.protocol.sdk.constants.IntegrationFunctionError;
import org.openspcoop2.protocol.spcoop.constants.SPCoopCostanti;
import org.openspcoop2.protocol.spcoop.testsuite.core.CostantiErroriIntegrazione;
import org.openspcoop2.protocol.spcoop.testsuite.core.CostantiTestSuite;
import org.openspcoop2.protocol.spcoop.testsuite.core.DatabaseProperties;
import org.openspcoop2.protocol.spcoop.testsuite.core.SPCoopTestsuiteLogger;
import org.openspcoop2.protocol.spcoop.testsuite.core.Utilities;
import org.openspcoop2.protocol.spcoop.testsuite.core.UtilitiesEGov;
import org.openspcoop2.protocol.utils.ErroriProperties;
import org.openspcoop2.testsuite.clients.ClientHttpGenerico;
import org.openspcoop2.testsuite.core.ErroreAttesoOpenSPCoopLogCore;
import org.openspcoop2.testsuite.core.Repository;
import org.openspcoop2.testsuite.core.TestSuiteException;
import org.openspcoop2.testsuite.db.DatabaseComponent;
import org.openspcoop2.testsuite.units.GestioneViaJmx;
import org.openspcoop2.testsuite.units.utils.OpenSPCoopDetailsUtilities;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.resources.FileSystemUtilities;
import org.slf4j.Logger;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.AfterGroups;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;


/**
 * Test sui profili di collaborazione implementati nella Porta di Dominio
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SOAPAction extends GestioneViaJmx {

	/** Identificativo del gruppo */
	public static final String ID_GRUPPO = "SOAPAction";
	
	
	
	
	private Logger log = SPCoopTestsuiteLogger.getInstance();
	
	protected SOAPAction() {
		super(org.openspcoop2.protocol.spcoop.testsuite.core.TestSuiteProperties.getInstance());
	}
	
	
	
	
	private Date dataAvvioGruppoTest = null;
	@BeforeGroups (alwaysRun=true , groups=ID_GRUPPO)
	public void testOpenspcoopCoreLog_raccoltaTempoAvvioTest() throws Exception{
		this.dataAvvioGruppoTest = DateManager.getDate();
	} 	
	private List<ErroreAttesoOpenSPCoopLogCore> erroriAttesiOpenSPCoopCore = new java.util.ArrayList<>();
	@AfterGroups (alwaysRun=true , groups=ID_GRUPPO)
	public void testOpenspcoopCoreLog() throws Exception{
		if(this.erroriAttesiOpenSPCoopCore.size()>0){
			org.openspcoop2.protocol.spcoop.testsuite.core.FileSystemUtilities.verificaOpenspcoopCore(this.dataAvvioGruppoTest,
					this.erroriAttesiOpenSPCoopCore.toArray(new ErroreAttesoOpenSPCoopLogCore[1]));
		}else{
			org.openspcoop2.protocol.spcoop.testsuite.core.FileSystemUtilities.verificaOpenspcoopCore(this.dataAvvioGruppoTest);
		}
	} 
	
	
	

	
	private byte[] getBusta() throws ProtocolException{
		String egov=UtilitiesEGov.getIDEGov(CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE,
				CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE+CostantiTestSuite.SPCOOP_PORTA_DOMINIO);
		String bustaSOAP=UtilitiesEGov.getBustaEGov(CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_FRUITORE,
				CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE,
				CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_EROGATORE,
				CostantiTestSuite.SPCOOP_NOME_SOGGETTO_EROGATORE,
				SPCoopCostanti.PROFILO_COLLABORAZIONE_SINCRONO,
				CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
				CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
				egov,
				false,
				SPCoopCostanti.PROFILO_TRASMISSIONE_SENZA_DUPLICATI,
				false,
				null,
				null,
				SPCoopCostanti.TIPO_TEMPO_SPC);
		return bustaSOAP.getBytes();
	}
	
	


	
	
	
	
	// ------------- SOAP ACTION NON PRESENTE ------------------

	Repository repositorySoapActionNonPresentePD=new Repository();
	@Test(groups={CostantiSOAP.ID_GRUPPO_SOAP,SOAPAction.ID_GRUPPO,SOAPAction.ID_GRUPPO+".SOAPActionNonPresente_PD"})
	public void soapActionNonPresente_PD_genericCode() throws TestSuiteException, IOException, Exception{
		boolean genericCode = true;
		boolean unwrap = false;
		try {
			super.lockForCode(genericCode, unwrap);
			
			_soapActionNonPresente_PD(genericCode);
		}finally {
			super.unlockForCode(genericCode);
		}
	}
	@Test(groups={CostantiSOAP.ID_GRUPPO_SOAP,SOAPAction.ID_GRUPPO,SOAPAction.ID_GRUPPO+".SOAPActionNonPresente_PD"})
	public void soapActionNonPresente_PD_specificCode() throws TestSuiteException, IOException, Exception{
		boolean genericCode = false;
		boolean unwrap = false;
		try {
			super.lockForCode(genericCode, unwrap);
			
			_soapActionNonPresente_PD(genericCode);
		}finally {
			super.unlockForCode(genericCode);
		}
	}
	
	private void _soapActionNonPresente_PD(boolean genericCode)throws TestSuiteException,SOAPException, Exception{
		
		Date dataInizioTest = DateManager.getDate();
		
		// costruzione busta
		java.io.ByteArrayInputStream bin =
			new java.io.ByteArrayInputStream(FileSystemUtilities.readBytesFromFile(Utilities.testSuiteProperties.getSoap11FileName()));
		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();

		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositorySoapActionNonPresentePD);
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_PROFILO_SINCRONO);
			client.setSoapAction(null);
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
				
				String codiceErrore = Utilities.toString(CodiceErroreIntegrazione.CODICE_426_SERVLET_ERROR);
				String msgErrore = CostantiErroriIntegrazione.MSG_426_SERVLET_REQUEST_ERROR+"ErroreProcessamento: Header http 'SOAPAction' non presente";
				
				if(genericCode) {
					IntegrationFunctionError integrationFunctionError = IntegrationFunctionError.BAD_REQUEST;
					ErroriProperties erroriProperties = ErroriProperties.getInstance(this.log);
					codiceErrore = org.openspcoop2.message.constants.Costanti.SOAP11_FAULT_CODE_CLIENT +
							org.openspcoop2.message.constants.Costanti.SOAP11_FAULT_CODE_SEPARATOR+erroriProperties.getErrorType_noWrap(integrationFunctionError);
					if(erroriProperties.isForceGenericDetails_noWrap(integrationFunctionError)) {
						msgErrore = erroriProperties.getGenericDetails_noWrap(integrationFunctionError);
					}
				}
				
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				Reporter.log("Controllo fault code ["+codiceErrore+"]");
				Assert.assertTrue(codiceErrore.equals(error.getFaultCode().getLocalPart()));
				Reporter.log("Controllo fault string ["+msgErrore+"]");
				Assert.assertTrue(msgErrore.equals(error.getFaultString()));
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
		err.setMsgErrore("ErroreGenerale");
		this.erroriAttesiOpenSPCoopCore.add(err);

		ErroreAttesoOpenSPCoopLogCore err2 = new ErroreAttesoOpenSPCoopLogCore();
		err2.setIntervalloInferiore(dataInizioTest);
		err2.setIntervalloSuperiore(dataFineTest);
		err2.setMsgErrore("Header http 'SOAPAction' non presente");
		this.erroriAttesiOpenSPCoopCore.add(err2);
	}
	
	
	Repository repositorySoapActionNonPresentePA=new Repository();
	@Test(groups={CostantiSOAP.ID_GRUPPO_SOAP,SOAPAction.ID_GRUPPO,SOAPAction.ID_GRUPPO+".SOAPActionNonPresente_PA"})
	public void soapActionNonPresente_PA_genericCode() throws TestSuiteException, IOException, Exception{
		boolean genericCode = true;
		boolean unwrap = false;
		try {
			super.lockForCode(genericCode, unwrap);
			
			_soapActionNonPresente_PA(genericCode);
		}finally {
			super.unlockForCode(genericCode);
		}
	}
	@Test(groups={CostantiSOAP.ID_GRUPPO_SOAP,SOAPAction.ID_GRUPPO,SOAPAction.ID_GRUPPO+".SOAPActionNonPresente_PA"})
	public void soapActionNonPresente_PA_specificCode() throws TestSuiteException, IOException, Exception{
		boolean genericCode = false;
		boolean unwrap = false;
		try {
			super.lockForCode(genericCode, unwrap);
			
			_soapActionNonPresente_PA(genericCode);
		}finally {
			super.unlockForCode(genericCode);
		}
	}
	
	private void _soapActionNonPresente_PA(boolean genericCode)throws TestSuiteException,SOAPException, Exception{
		
		Date dataInizioTest = DateManager.getDate();
		
		// costruzione busta
		java.io.ByteArrayInputStream bin =
			new java.io.ByteArrayInputStream(getBusta());
		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();

		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositorySoapActionNonPresentePA);
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneBusteErogatore());
			client.setSoapAction(null);
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
				
				String codiceErrore = Utilities.toString(CodiceErroreIntegrazione.CODICE_426_SERVLET_ERROR);
				String descrErrore = CostantiErroriIntegrazione.MSG_426_SERVLET_REQUEST_ERROR+"ErroreProcessamento: Header http 'SOAPAction' non presente";
				
				if(genericCode) {
					IntegrationFunctionError integrationFunctionError = IntegrationFunctionError.BAD_REQUEST;
					ErroriProperties erroriProperties = ErroriProperties.getInstance(this.log);
					codiceErrore = erroriProperties.getErrorType_noWrap(integrationFunctionError);
					if(erroriProperties.isForceGenericDetails_noWrap(integrationFunctionError)) {
						descrErrore = erroriProperties.getGenericDetails_noWrap(integrationFunctionError);
					}
				}
				
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				Assert.assertTrue(SPCoopCostanti.FAULT_CODE_PROCESSAMENTO_SPCOOP.equals(error.getFaultCode().getLocalPart()) ||
						SPCoopCostanti.FAULT_CODE_PROCESSAMENTO_SPCOOP.equals("soap:"+error.getFaultCode().getLocalPart()) ||
						SPCoopCostanti.FAULT_CODE_PROCESSAMENTO_SPCOOP.equals("soapenv:"+error.getFaultCode().getLocalPart()));
				String msgErrore = SPCoopCostanti.FAULT_STRING_PROCESSAMENTO_SPCOOP;
				Reporter.log("Controllo fault string ["+msgErrore+"]");
				Assert.assertTrue(msgErrore.equals(error.getFaultString()));
				
				List<org.openspcoop2.testsuite.units.utils.OpenSPCoopDetail> eccezioni = 
					new ArrayList<org.openspcoop2.testsuite.units.utils.OpenSPCoopDetail>();
				org.openspcoop2.testsuite.units.utils.OpenSPCoopDetail ecc = new org.openspcoop2.testsuite.units.utils.OpenSPCoopDetail();
				ecc.setCodice(codiceErrore);
				ecc.setDescrizione(descrErrore);
				ecc.setCheckDescrizioneTramiteMatchEsatto(true);
				eccezioni.add(ecc);
				
				Assert.assertTrue(OpenSPCoopDetailsUtilities.existsOpenSPCoopDetails(error)); // vengono generati in caso di 5XX
					
				OpenSPCoopDetailsUtilities.verificaFaultOpenSPCoopDetail(error, 
						Utilities.testSuiteProperties.getIdentitaDefault(),TipoPdD.APPLICATIVA,"RicezioneBuste", 
						eccezioni, new ArrayList<org.openspcoop2.testsuite.units.utils.OpenSPCoopDetail>());
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
		err.setMsgErrore("ErroreGenerale");
		this.erroriAttesiOpenSPCoopCore.add(err);

		ErroreAttesoOpenSPCoopLogCore err2 = new ErroreAttesoOpenSPCoopLogCore();
		err2.setIntervalloInferiore(dataInizioTest);
		err2.setIntervalloSuperiore(dataFineTest);
		err2.setMsgErrore("Header http 'SOAPAction' non presente");
		this.erroriAttesiOpenSPCoopCore.add(err2);
	}
	
	
	
	
	
	
	
	// ------------- SOAP ACTION VALUE NON PRESENTE ------------------
	
	Repository repositorySoapActionValueNonPresentePD=new Repository();
	@Test(groups={CostantiSOAP.ID_GRUPPO_SOAP,SOAPAction.ID_GRUPPO,SOAPAction.ID_GRUPPO+".SOAPActionValueNonPresente_PD"})
	public void soapActionValueNonPresente_PD_genericCode() throws TestSuiteException, IOException, Exception{
		boolean genericCode = true;
		boolean unwrap = false;
		try {
			super.lockForCode(genericCode, unwrap);
			
			_soapActionValueNonPresente_PD(genericCode);
		}finally {
			super.unlockForCode(genericCode);
		}
	}
	@Test(groups={CostantiSOAP.ID_GRUPPO_SOAP,SOAPAction.ID_GRUPPO,SOAPAction.ID_GRUPPO+".SOAPActionValueNonPresente_PD"})
	public void soapActionValueNonPresente_PD_specificCode() throws TestSuiteException, IOException, Exception{
		boolean genericCode = false;
		boolean unwrap = false;
		try {
			super.lockForCode(genericCode, unwrap);
			
			_soapActionValueNonPresente_PD(genericCode);
		}finally {
			super.unlockForCode(genericCode);
		}
	}
	
	private void _soapActionValueNonPresente_PD(boolean genericCode)throws TestSuiteException,SOAPException, Exception{
		
		Date dataInizioTest = DateManager.getDate();
		
		String version_jbossas = Utilities.readApplicationServerVersion();
				
		// costruzione busta
		java.io.ByteArrayInputStream bin =
			new java.io.ByteArrayInputStream(FileSystemUtilities.readBytesFromFile(Utilities.testSuiteProperties.getSoap11FileName()));
		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();

		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositorySoapActionValueNonPresentePD);
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_PROFILO_SINCRONO);
			client.setSoapAction(ClientHttpGenerico.SOAP_ACTION_VALUE_NULL);
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
				if(version_jbossas!=null && version_jbossas.startsWith("tomcat")){
					// alcune versioni tornato 400 poichè l'header SOAPAction non ha un valore
					if(400 == client.getCodiceStatoHTTP()) {
						System.out.println("Codice Http 400 Atteso per Tomcat in invocazioni con soapAction non valorizzata");
						return;
					}
				}
				throw new Exception("Attesa eccezione per controllo soapAction");
			} catch (AxisFault error) {
				
				String codiceErrore = Utilities.toString(CodiceErroreIntegrazione.CODICE_426_SERVLET_ERROR);
				String msgErrore = CostantiErroriIntegrazione.MSG_426_SERVLET_REQUEST_ERROR+"ErroreProcessamento: Header http 'SOAPAction' non presente";
				String msgErroreNonQuotata = null;
				//if(version_jbossas!=null && version_jbossas.startsWith("wildfly")){ dipende anche dalla versione di java
				// una soap action non presente viene tradotta nel nuovo web container in una stringa vuota
				msgErroreNonQuotata = CostantiErroriIntegrazione.MSG_426_SERVLET_REQUEST_ERROR+"ErroreProcessamento: Header http 'SOAPAction' valorizzato tramite una stringa non quotata (WSI-BP-1.1 R1109)";
				//}
				
				if(genericCode) {
					IntegrationFunctionError integrationFunctionError = IntegrationFunctionError.BAD_REQUEST;
					ErroriProperties erroriProperties = ErroriProperties.getInstance(this.log);
					codiceErrore = org.openspcoop2.message.constants.Costanti.SOAP11_FAULT_CODE_CLIENT +
							org.openspcoop2.message.constants.Costanti.SOAP11_FAULT_CODE_SEPARATOR+erroriProperties.getErrorType_noWrap(integrationFunctionError);
					if(erroriProperties.isForceGenericDetails_noWrap(integrationFunctionError)) {
						msgErrore = erroriProperties.getGenericDetails_noWrap(integrationFunctionError);
					}
				}
				
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				Reporter.log("Controllo fault code ["+codiceErrore+"]");
				Assert.assertTrue(codiceErrore.equals(error.getFaultCode().getLocalPart()));
				
				Reporter.log("Controllo fault string 1 ["+msgErrore+"]");
				Reporter.log("Controllo fault string 2 ["+msgErrore+"]");
				Assert.assertTrue(msgErrore.equals(error.getFaultString()) || msgErroreNonQuotata.equals(error.getFaultString()));
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
		err.setMsgErrore("ErroreGenerale");
		this.erroriAttesiOpenSPCoopCore.add(err);

		ErroreAttesoOpenSPCoopLogCore err2 = new ErroreAttesoOpenSPCoopLogCore();
		err2.setIntervalloInferiore(dataInizioTest);
		err2.setIntervalloSuperiore(dataFineTest);
		//if(version_jbossas!=null && version_jbossas.startsWith("wildfly")){ dipende anche dalla versione di java
		// una soap action non presente viene tradotta nel nuovo web container in una stringa vuota
		err2.setMsgErrore("Generale(richiesta): Header http 'SOAPAction' valorizzato tramite una stringa non quotata (WSI-BP-1.1 R1109)");
		//}else{
		//	err2.setMsgErrore("Generale(richiesta): Header http 'SOAPAction' non presente");
		//}
		this.erroriAttesiOpenSPCoopCore.add(err2);
		
		ErroreAttesoOpenSPCoopLogCore err3 = new ErroreAttesoOpenSPCoopLogCore();
		err3.setIntervalloInferiore(dataInizioTest);
		err3.setIntervalloSuperiore(dataFineTest);
		err3.setMsgErrore("Generale(richiesta): Header http 'SOAPAction' non presente");
		this.erroriAttesiOpenSPCoopCore.add(err3);
	}
	
	
	Repository repositorySoapActionValueNonPresentePA=new Repository();
	@Test(groups={CostantiSOAP.ID_GRUPPO_SOAP,SOAPAction.ID_GRUPPO,SOAPAction.ID_GRUPPO+".SOAPActionValueNonPresente_PA"})
	public void soapActionValueNonPresente_PA_genericCode() throws TestSuiteException, IOException, Exception{
		boolean genericCode = true;
		boolean unwrap = false;
		try {
			super.lockForCode(genericCode, unwrap);
			
			_soapActionValueNonPresente_PA(genericCode);
		}finally {
			super.unlockForCode(genericCode);
		}
	}
	@Test(groups={CostantiSOAP.ID_GRUPPO_SOAP,SOAPAction.ID_GRUPPO,SOAPAction.ID_GRUPPO+".SOAPActionValueNonPresente_PA"})
	public void soapActionValueNonPresente_PA_specificCode() throws TestSuiteException, IOException, Exception{
		boolean genericCode = false;
		boolean unwrap = false;
		try {
			super.lockForCode(genericCode, unwrap);
			
			_soapActionValueNonPresente_PA(genericCode);
		}finally {
			super.unlockForCode(genericCode);
		}
	}
	
	private void _soapActionValueNonPresente_PA(boolean genericCode)throws TestSuiteException,SOAPException, Exception{
		
		Date dataInizioTest = DateManager.getDate();
		
		String version_jbossas = Utilities.readApplicationServerVersion();
				
		// costruzione busta
		java.io.ByteArrayInputStream bin =
			new java.io.ByteArrayInputStream(getBusta());
		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();

		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositorySoapActionValueNonPresentePA);
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneBusteErogatore());
			client.setSoapAction(ClientHttpGenerico.SOAP_ACTION_VALUE_NULL);
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
				if(version_jbossas!=null && version_jbossas.startsWith("tomcat")){
					// alcune versioni tornato 400 poichè l'header SOAPAction non ha un valore
					if(400 == client.getCodiceStatoHTTP()) {
						System.out.println("Codice Http 400 Atteso per Tomcat in invocazioni con soapAction non valorizzata");
						return;
					}
				}
				throw new Exception("Attesa eccezione per controllo soapAction");
			} catch (AxisFault error) {
				
				String codiceErrore = Utilities.toString(CodiceErroreIntegrazione.CODICE_426_SERVLET_ERROR);
				String descrErrore = null;
				String descrErroreNonQuotata = null;
				//if(version_jbossas!=null && version_jbossas.startsWith("wildfly")){ dipende anche dalla versione di java
				// una soap action non presente viene tradotta nel nuovo web container in una stringa vuota
				descrErroreNonQuotata = CostantiErroriIntegrazione.MSG_426_SERVLET_REQUEST_ERROR+"ErroreProcessamento: Header http 'SOAPAction' valorizzato tramite una stringa non quotata (WSI-BP-1.1 R1109)";
				//}else{
				descrErrore = CostantiErroriIntegrazione.MSG_426_SERVLET_REQUEST_ERROR+"ErroreProcessamento: Header http 'SOAPAction' non presente";
				//}
				
				if(genericCode) {
					IntegrationFunctionError integrationFunctionError = IntegrationFunctionError.BAD_REQUEST;
					ErroriProperties erroriProperties = ErroriProperties.getInstance(this.log);
					codiceErrore = erroriProperties.getErrorType_noWrap(integrationFunctionError);
					if(erroriProperties.isForceGenericDetails_noWrap(integrationFunctionError)) {
						descrErrore = erroriProperties.getGenericDetails_noWrap(integrationFunctionError);
						descrErroreNonQuotata = erroriProperties.getGenericDetails_noWrap(integrationFunctionError);
					}
				}
				
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				Assert.assertTrue(SPCoopCostanti.FAULT_CODE_PROCESSAMENTO_SPCOOP.equals(error.getFaultCode().getLocalPart()) ||
						SPCoopCostanti.FAULT_CODE_PROCESSAMENTO_SPCOOP.equals("soap:"+error.getFaultCode().getLocalPart()) ||
						SPCoopCostanti.FAULT_CODE_PROCESSAMENTO_SPCOOP.equals("soapenv:"+error.getFaultCode().getLocalPart()));
				String msgErrore = SPCoopCostanti.FAULT_STRING_PROCESSAMENTO_SPCOOP;
				Reporter.log("Controllo fault string ["+msgErrore+"]");
				Assert.assertTrue(msgErrore.equals(error.getFaultString()));
				
				List<org.openspcoop2.testsuite.units.utils.OpenSPCoopDetail> eccezioni = 
					new ArrayList<org.openspcoop2.testsuite.units.utils.OpenSPCoopDetail>();
				org.openspcoop2.testsuite.units.utils.OpenSPCoopDetail ecc = new org.openspcoop2.testsuite.units.utils.OpenSPCoopDetail();
				ecc.setCodice(codiceErrore);
				ecc.setDescrizione(descrErrore);
				ecc.setCheckDescrizioneTramiteMatchEsatto(true);
				eccezioni.add(ecc);
				org.openspcoop2.testsuite.units.utils.OpenSPCoopDetail ecc2 = new org.openspcoop2.testsuite.units.utils.OpenSPCoopDetail();
				ecc2.setCodice(codiceErrore);
				ecc2.setDescrizione(descrErroreNonQuotata);
				ecc2.setCheckDescrizioneTramiteMatchEsatto(true);
				eccezioni.add(ecc2);
				
				Assert.assertTrue(OpenSPCoopDetailsUtilities.existsOpenSPCoopDetails(error)); // vengono generati in caso di 5XX
					
				OpenSPCoopDetailsUtilities.verificaFaultOpenSPCoopDetail(error, 
						Utilities.testSuiteProperties.getIdentitaDefault(),TipoPdD.APPLICATIVA,"RicezioneBuste", 
						eccezioni, new ArrayList<org.openspcoop2.testsuite.units.utils.OpenSPCoopDetail>(), false);
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
		err.setMsgErrore("ErroreGenerale");
		this.erroriAttesiOpenSPCoopCore.add(err);

		ErroreAttesoOpenSPCoopLogCore err2 = new ErroreAttesoOpenSPCoopLogCore();
		err2.setIntervalloInferiore(dataInizioTest);
		err2.setIntervalloSuperiore(dataFineTest);
		//if(version_jbossas!=null && version_jbossas.startsWith("wildfly")){ dipende anche dalla versione di java
		// una soap action non presente viene tradotta nel nuovo web container in una stringa vuota
		err2.setMsgErrore("Generale(richiesta): Header http 'SOAPAction' valorizzato tramite una stringa non quotata (WSI-BP-1.1 R1109)");
		//}else{
		//	err2.setMsgErrore("Generale(richiesta): Header http 'SOAPAction' non presente");
		//}
		this.erroriAttesiOpenSPCoopCore.add(err2);
		
		ErroreAttesoOpenSPCoopLogCore err3 = new ErroreAttesoOpenSPCoopLogCore();
		err3.setIntervalloInferiore(dataInizioTest);
		err3.setIntervalloSuperiore(dataFineTest);
		err3.setMsgErrore("Generale(richiesta): Header http 'SOAPAction' non presente");
		this.erroriAttesiOpenSPCoopCore.add(err3);
	}


	
	

	
	
	
	
	
	
	
	
	
	// ------------- SOAP ACTION PRESENTE COME STRINGA VUOTA E NON QUOTATA ------------------

	Repository repositorySoapActionStringVuotaNonQuotataPD=new Repository();
	@Test(groups={CostantiSOAP.ID_GRUPPO_SOAP,SOAPAction.ID_GRUPPO,SOAPAction.ID_GRUPPO+".SoapActionStringVuotaNonQuotata_PD"})
	public void SoapActionStringVuotaNonQuotata_PD_genericCode() throws TestSuiteException, IOException, Exception{
		boolean genericCode = true;
		boolean unwrap = false;
		try {
			super.lockForCode(genericCode, unwrap);
			
			_SoapActionStringVuotaNonQuotata_PD(genericCode);
		}finally {
			super.unlockForCode(genericCode);
		}
	}
	@Test(groups={CostantiSOAP.ID_GRUPPO_SOAP,SOAPAction.ID_GRUPPO,SOAPAction.ID_GRUPPO+".SoapActionStringVuotaNonQuotata_PD"})
	public void SoapActionStringVuotaNonQuotata_PD_specificCode() throws TestSuiteException, IOException, Exception{
		boolean genericCode = false;
		boolean unwrap = false;
		try {
			super.lockForCode(genericCode, unwrap);
			
			_SoapActionStringVuotaNonQuotata_PD(genericCode);
		}finally {
			super.unlockForCode(genericCode);
		}
	}
	
	private void _SoapActionStringVuotaNonQuotata_PD(boolean genericCode)throws TestSuiteException,SOAPException, Exception{
		
		Date dataInizioTest = DateManager.getDate();
		
		// costruzione busta
		java.io.ByteArrayInputStream bin =
			new java.io.ByteArrayInputStream(FileSystemUtilities.readBytesFromFile(Utilities.testSuiteProperties.getSoap11FileName()));
		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();

		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositorySoapActionStringVuotaNonQuotataPD);
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_PROFILO_SINCRONO);
			client.setSoapAction("");
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
				
				String codiceErrore = Utilities.toString(CodiceErroreIntegrazione.CODICE_426_SERVLET_ERROR);
				String msgErrore = CostantiErroriIntegrazione.MSG_426_SERVLET_REQUEST_ERROR+"ErroreProcessamento: Header http 'SOAPAction' valorizzato tramite una stringa non quotata (WSI-BP-1.1 R1109)";
				
				
				if(genericCode) {
					IntegrationFunctionError integrationFunctionError = IntegrationFunctionError.BAD_REQUEST;
					ErroriProperties erroriProperties = ErroriProperties.getInstance(this.log);
					codiceErrore = org.openspcoop2.message.constants.Costanti.SOAP11_FAULT_CODE_CLIENT +
							org.openspcoop2.message.constants.Costanti.SOAP11_FAULT_CODE_SEPARATOR+erroriProperties.getErrorType_noWrap(integrationFunctionError);
					if(erroriProperties.isForceGenericDetails_noWrap(integrationFunctionError)) {
						msgErrore = erroriProperties.getGenericDetails_noWrap(integrationFunctionError);
					}
				}
				
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				Reporter.log("Controllo fault code ["+codiceErrore+"]");
				Assert.assertTrue(codiceErrore.equals(error.getFaultCode().getLocalPart()));
				Reporter.log("Controllo fault string ["+msgErrore+"]");
				Assert.assertTrue(msgErrore.equals(error.getFaultString()));
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
		err.setMsgErrore("ErroreGenerale");
		this.erroriAttesiOpenSPCoopCore.add(err);

		ErroreAttesoOpenSPCoopLogCore err2 = new ErroreAttesoOpenSPCoopLogCore();
		err2.setIntervalloInferiore(dataInizioTest);
		err2.setIntervalloSuperiore(dataFineTest);
		err2.setMsgErrore("Header http 'SOAPAction' valorizzato tramite una stringa non quotata (WSI-BP-1.1 R1109)");
		this.erroriAttesiOpenSPCoopCore.add(err2);
	}
	
	
	Repository repositorySoapActionStringVuotaNonQuotataPA=new Repository();
	@Test(groups={CostantiSOAP.ID_GRUPPO_SOAP,SOAPAction.ID_GRUPPO,SOAPAction.ID_GRUPPO+".SoapActionStringVuotaNonQuotata_PA"})
	public void SoapActionStringVuotaNonQuotata_PA_genericCode() throws TestSuiteException, IOException, Exception{
		boolean genericCode = true;
		boolean unwrap = false;
		try {
			super.lockForCode(genericCode, unwrap);
			
			_SoapActionStringVuotaNonQuotata_PA(genericCode);
		}finally {
			super.unlockForCode(genericCode);
		}
	}
	@Test(groups={CostantiSOAP.ID_GRUPPO_SOAP,SOAPAction.ID_GRUPPO,SOAPAction.ID_GRUPPO+".SoapActionStringVuotaNonQuotata_PA"})
	public void SoapActionStringVuotaNonQuotata_PA_specificCode() throws TestSuiteException, IOException, Exception{
		boolean genericCode = false;
		boolean unwrap = false;
		try {
			super.lockForCode(genericCode, unwrap);
			
			_SoapActionStringVuotaNonQuotata_PA(genericCode);
		}finally {
			super.unlockForCode(genericCode);
		}
	}

	private void _SoapActionStringVuotaNonQuotata_PA(boolean genericCode)throws TestSuiteException,SOAPException, Exception{
		
		Date dataInizioTest = DateManager.getDate();
		
		// costruzione busta
		java.io.ByteArrayInputStream bin =
			new java.io.ByteArrayInputStream(getBusta());
		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();

		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositorySoapActionStringVuotaNonQuotataPA);
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneBusteErogatore());
			client.setSoapAction("");
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
				
				String codiceErrore = Utilities.toString(CodiceErroreIntegrazione.CODICE_426_SERVLET_ERROR);
				String descrErrore = CostantiErroriIntegrazione.MSG_426_SERVLET_REQUEST_ERROR+"ErroreProcessamento: Header http 'SOAPAction' valorizzato tramite una stringa non quotata (WSI-BP-1.1 R1109)";
				
				if(genericCode) {
					IntegrationFunctionError integrationFunctionError = IntegrationFunctionError.BAD_REQUEST;
					ErroriProperties erroriProperties = ErroriProperties.getInstance(this.log);
					codiceErrore = erroriProperties.getErrorType_noWrap(integrationFunctionError);
					if(erroriProperties.isForceGenericDetails_noWrap(integrationFunctionError)) {
						descrErrore = erroriProperties.getGenericDetails_noWrap(integrationFunctionError);
					}
				}
				
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				Assert.assertTrue(SPCoopCostanti.FAULT_CODE_PROCESSAMENTO_SPCOOP.equals(error.getFaultCode().getLocalPart()) ||
						SPCoopCostanti.FAULT_CODE_PROCESSAMENTO_SPCOOP.equals("soap:"+error.getFaultCode().getLocalPart()) ||
						SPCoopCostanti.FAULT_CODE_PROCESSAMENTO_SPCOOP.equals("soapenv:"+error.getFaultCode().getLocalPart()));
				String msgErrore = SPCoopCostanti.FAULT_STRING_PROCESSAMENTO_SPCOOP;
				Reporter.log("Controllo fault string ["+msgErrore+"]");
				Assert.assertTrue(msgErrore.equals(error.getFaultString()));
				
				List<org.openspcoop2.testsuite.units.utils.OpenSPCoopDetail> eccezioni = 
					new ArrayList<org.openspcoop2.testsuite.units.utils.OpenSPCoopDetail>();
				org.openspcoop2.testsuite.units.utils.OpenSPCoopDetail ecc = new org.openspcoop2.testsuite.units.utils.OpenSPCoopDetail();
				ecc.setCodice(codiceErrore);
				ecc.setDescrizione(descrErrore);
				ecc.setCheckDescrizioneTramiteMatchEsatto(true);
				eccezioni.add(ecc);
				
				Assert.assertTrue(OpenSPCoopDetailsUtilities.existsOpenSPCoopDetails(error)); // vengono generati in caso di 5XX
					
				OpenSPCoopDetailsUtilities.verificaFaultOpenSPCoopDetail(error, 
						Utilities.testSuiteProperties.getIdentitaDefault(),TipoPdD.APPLICATIVA,"RicezioneBuste", 
						eccezioni, new ArrayList<org.openspcoop2.testsuite.units.utils.OpenSPCoopDetail>());
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
		err.setMsgErrore("ErroreGenerale");
		this.erroriAttesiOpenSPCoopCore.add(err);

		ErroreAttesoOpenSPCoopLogCore err2 = new ErroreAttesoOpenSPCoopLogCore();
		err2.setIntervalloInferiore(dataInizioTest);
		err2.setIntervalloSuperiore(dataFineTest);
		err2.setMsgErrore("Header http 'SOAPAction' valorizzato tramite una stringa non quotata (WSI-BP-1.1 R1109)");
		this.erroriAttesiOpenSPCoopCore.add(err2);
	}
	
	
	
	
	
	
	
	
	
	


	
	
	
	
	
	
	// ------------- SOAP ACTION PRESENTE E NON QUOTATA ------------------

	Repository repositorySoapActionNonQuotataPD=new Repository();
	@Test(groups={CostantiSOAP.ID_GRUPPO_SOAP,SOAPAction.ID_GRUPPO,SOAPAction.ID_GRUPPO+".SOAPActionNonQuotata_PD"})
	public void soapActionNonQuotata_PD_genericCode() throws TestSuiteException, IOException, Exception{
		boolean genericCode = true;
		boolean unwrap = false;
		try {
			super.lockForCode(genericCode, unwrap);
			
			_soapActionNonQuotata_PD(genericCode);
		}finally {
			super.unlockForCode(genericCode);
		}
	}
	@Test(groups={CostantiSOAP.ID_GRUPPO_SOAP,SOAPAction.ID_GRUPPO,SOAPAction.ID_GRUPPO+".SOAPActionNonQuotata_PD"})
	public void soapActionNonQuotata_PD_specificCode() throws TestSuiteException, IOException, Exception{
		boolean genericCode = false;
		boolean unwrap = false;
		try {
			super.lockForCode(genericCode, unwrap);
			
			_soapActionNonQuotata_PD(genericCode);
		}finally {
			super.unlockForCode(genericCode);
		}
	}
	
	private void _soapActionNonQuotata_PD(boolean genericCode)throws TestSuiteException,SOAPException, Exception{
		
		Date dataInizioTest = DateManager.getDate();
		
		// costruzione busta
		java.io.ByteArrayInputStream bin =
			new java.io.ByteArrayInputStream(FileSystemUtilities.readBytesFromFile(Utilities.testSuiteProperties.getSoap11FileName()));
		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();

		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositorySoapActionNonQuotataPD);
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_PROFILO_SINCRONO);
			client.setSoapAction("Action");
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
				
				String codiceErrore = Utilities.toString(CodiceErroreIntegrazione.CODICE_426_SERVLET_ERROR);
				String msgErrore = CostantiErroriIntegrazione.MSG_426_SERVLET_REQUEST_ERROR+"ErroreProcessamento: Header http 'SOAPAction' valorizzato tramite una stringa non quotata (WSI-BP-1.1 R1109)";
				
				if(genericCode) {
					IntegrationFunctionError integrationFunctionError = IntegrationFunctionError.BAD_REQUEST;
					ErroriProperties erroriProperties = ErroriProperties.getInstance(this.log);
					codiceErrore = org.openspcoop2.message.constants.Costanti.SOAP11_FAULT_CODE_CLIENT +
							org.openspcoop2.message.constants.Costanti.SOAP11_FAULT_CODE_SEPARATOR+erroriProperties.getErrorType_noWrap(integrationFunctionError);
					if(erroriProperties.isForceGenericDetails_noWrap(integrationFunctionError)) {
						msgErrore = erroriProperties.getGenericDetails_noWrap(integrationFunctionError);
					}
				}
				
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				Reporter.log("Controllo fault code ["+codiceErrore+"]");
				Assert.assertTrue(codiceErrore.equals(error.getFaultCode().getLocalPart()));
				Reporter.log("Controllo fault string ["+msgErrore+"]");
				Assert.assertTrue(msgErrore.equals(error.getFaultString()));
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
		err.setMsgErrore("ErroreGenerale");
		this.erroriAttesiOpenSPCoopCore.add(err);

		ErroreAttesoOpenSPCoopLogCore err2 = new ErroreAttesoOpenSPCoopLogCore();
		err2.setIntervalloInferiore(dataInizioTest);
		err2.setIntervalloSuperiore(dataFineTest);
		err2.setMsgErrore("Header http 'SOAPAction' valorizzato tramite una stringa non quotata (WSI-BP-1.1 R1109)");
		this.erroriAttesiOpenSPCoopCore.add(err2);
	}
	
	
	Repository repositorySoapActionNonQuotataPA=new Repository();
	@Test(groups={CostantiSOAP.ID_GRUPPO_SOAP,SOAPAction.ID_GRUPPO,SOAPAction.ID_GRUPPO+".SOAPActionNonQuotata_PA"})
	public void soapActionNonQuotata_PA_genericCode() throws TestSuiteException, IOException, Exception{
		boolean genericCode = true;
		boolean unwrap = false;
		try {
			super.lockForCode(genericCode, unwrap);
			
			_soapActionNonQuotata_PA(genericCode);
		}finally {
			super.unlockForCode(genericCode);
		}
	}
	@Test(groups={CostantiSOAP.ID_GRUPPO_SOAP,SOAPAction.ID_GRUPPO,SOAPAction.ID_GRUPPO+".SOAPActionNonQuotata_PA"})
	public void soapActionNonQuotata_PA_specificCode() throws TestSuiteException, IOException, Exception{
		boolean genericCode = false;
		boolean unwrap = false;
		try {
			super.lockForCode(genericCode, unwrap);
			
			_soapActionNonQuotata_PA(genericCode);
		}finally {
			super.unlockForCode(genericCode);
		}
	}
	
	private void _soapActionNonQuotata_PA(boolean genericCode)throws TestSuiteException,SOAPException, Exception{
		
		Date dataInizioTest = DateManager.getDate();
		
		// costruzione busta
		java.io.ByteArrayInputStream bin =
			new java.io.ByteArrayInputStream(getBusta());
		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();

		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositorySoapActionNonQuotataPA);
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneBusteErogatore());
			client.setSoapAction("Action");
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
				
				String codiceErrore = Utilities.toString(CodiceErroreIntegrazione.CODICE_426_SERVLET_ERROR);
				String descrErrore = CostantiErroriIntegrazione.MSG_426_SERVLET_REQUEST_ERROR+"ErroreProcessamento: Header http 'SOAPAction' valorizzato tramite una stringa non quotata (WSI-BP-1.1 R1109)";
				
				if(genericCode) {
					IntegrationFunctionError integrationFunctionError = IntegrationFunctionError.BAD_REQUEST;
					ErroriProperties erroriProperties = ErroriProperties.getInstance(this.log);
					codiceErrore = erroriProperties.getErrorType_noWrap(integrationFunctionError);
					if(erroriProperties.isForceGenericDetails_noWrap(integrationFunctionError)) {
						descrErrore = erroriProperties.getGenericDetails_noWrap(integrationFunctionError);
					}
				}
				
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				Assert.assertTrue(SPCoopCostanti.FAULT_CODE_PROCESSAMENTO_SPCOOP.equals(error.getFaultCode().getLocalPart()) ||
						SPCoopCostanti.FAULT_CODE_PROCESSAMENTO_SPCOOP.equals("soap:"+error.getFaultCode().getLocalPart()) ||
						SPCoopCostanti.FAULT_CODE_PROCESSAMENTO_SPCOOP.equals("soapenv:"+error.getFaultCode().getLocalPart()));
				String msgErrore = SPCoopCostanti.FAULT_STRING_PROCESSAMENTO_SPCOOP;
				Reporter.log("Controllo fault string ["+msgErrore+"]");
				Assert.assertTrue(msgErrore.equals(error.getFaultString()));
				
				List<org.openspcoop2.testsuite.units.utils.OpenSPCoopDetail> eccezioni = 
					new ArrayList<org.openspcoop2.testsuite.units.utils.OpenSPCoopDetail>();
				org.openspcoop2.testsuite.units.utils.OpenSPCoopDetail ecc = new org.openspcoop2.testsuite.units.utils.OpenSPCoopDetail();
				ecc.setCodice(codiceErrore);
				ecc.setDescrizione(descrErrore);
				ecc.setCheckDescrizioneTramiteMatchEsatto(true);
				eccezioni.add(ecc);
				
				Assert.assertTrue(OpenSPCoopDetailsUtilities.existsOpenSPCoopDetails(error)); // vengono generati in caso di 5XX
					
				OpenSPCoopDetailsUtilities.verificaFaultOpenSPCoopDetail(error, 
						Utilities.testSuiteProperties.getIdentitaDefault(),TipoPdD.APPLICATIVA,"RicezioneBuste", 
						eccezioni, new ArrayList<org.openspcoop2.testsuite.units.utils.OpenSPCoopDetail>());
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
		err.setMsgErrore("ErroreGenerale");
		this.erroriAttesiOpenSPCoopCore.add(err);

		ErroreAttesoOpenSPCoopLogCore err2 = new ErroreAttesoOpenSPCoopLogCore();
		err2.setIntervalloInferiore(dataInizioTest);
		err2.setIntervalloSuperiore(dataFineTest);
		err2.setMsgErrore("Header http 'SOAPAction' valorizzato tramite una stringa non quotata (WSI-BP-1.1 R1109)");
		this.erroriAttesiOpenSPCoopCore.add(err2);
	}

	
	
	
	
	
	
	
	
	// ------------- SOAP ACTION PRESENTE COME STRINGA VUOTA QUOTATA ------------------

	Repository repositorySoapActionStringVuotaQuotataPD=new Repository();
	@Test(groups={CostantiSOAP.ID_GRUPPO_SOAP,SOAPAction.ID_GRUPPO,SOAPAction.ID_GRUPPO+".SoapActionStringVuotaQuotata_PD"})
	public void SoapActionStringVuotaQuotata_PD()throws TestSuiteException,SOAPException, Exception{
		
		// costruzione busta
		java.io.ByteArrayInputStream bin =
			new java.io.ByteArrayInputStream(FileSystemUtilities.readBytesFromFile(Utilities.testSuiteProperties.getSoap11FileName()));
		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();

		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositorySoapActionStringVuotaQuotataPD);
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_PROFILO_SINCRONO);
			client.setSoapAction("\"\"");
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
				throw new Exception("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
			}finally{
				dbComponentErogatore.close();
			}
			
		}catch(Exception e){
			throw e;
		}finally{
			dbComponentErogatore.close();
		}
	}
	
	
	Repository repositorySoapActionStringVuotaQuotataPA=new Repository();
	@Test(groups={CostantiSOAP.ID_GRUPPO_SOAP,SOAPAction.ID_GRUPPO,SOAPAction.ID_GRUPPO+".SoapActionStringVuotaQuotata_PA"})
	public void SoapActionStringVuotaQuotata_PA()throws TestSuiteException,SOAPException, Exception{
		
		// costruzione busta
		java.io.ByteArrayInputStream bin =
			new java.io.ByteArrayInputStream(getBusta());
		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();

		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositorySoapActionStringVuotaQuotataPA);
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneBusteErogatore());
			client.setSoapAction("\"\"");
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
				throw new Exception("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
			}finally{
				dbComponentErogatore.close();
			}
			
		}catch(Exception e){
			throw e;
		}finally{
			dbComponentErogatore.close();
		}
	}
	
	
	
	
	
	
	
	
	
	// ------------- SOAP ACTION PRESENTE CON STRINGA QUOTATA ------------------

	Repository repositorySoapActionStringQuotataPD=new Repository();
	@Test(groups={CostantiSOAP.ID_GRUPPO_SOAP,SOAPAction.ID_GRUPPO,SOAPAction.ID_GRUPPO+".SoapActionStringQuotata_PD"})
	public void SoapActionStringQuotata_PD()throws TestSuiteException,SOAPException, Exception{
		
		// costruzione busta
		java.io.ByteArrayInputStream bin =
			new java.io.ByteArrayInputStream(FileSystemUtilities.readBytesFromFile(Utilities.testSuiteProperties.getSoap11FileName()));
		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();

		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositorySoapActionStringQuotataPD);
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_PROFILO_SINCRONO);
			client.setSoapAction("\"Action\"");
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
				throw new Exception("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
			}finally{
				dbComponentErogatore.close();
			}
			
		}catch(Exception e){
			throw e;
		}finally{
			dbComponentErogatore.close();
		}
	}
	
	
	Repository repositorySoapActionStringQuotataPA=new Repository();
	@Test(groups={CostantiSOAP.ID_GRUPPO_SOAP,SOAPAction.ID_GRUPPO,SOAPAction.ID_GRUPPO+".SoapActionStringQuotata_PA"})
	public void SoapActionStringQuotata_PA()throws TestSuiteException,SOAPException, Exception{
		
		// costruzione busta
		java.io.ByteArrayInputStream bin =
			new java.io.ByteArrayInputStream(getBusta());
		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();

		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositorySoapActionStringQuotataPA);
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneBusteErogatore());
			client.setSoapAction("\"Action\"");
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
				throw new Exception("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
			}finally{
				dbComponentErogatore.close();
			}
			
		}catch(Exception e){
			throw e;
		}finally{
			dbComponentErogatore.close();
		}
	}
	
}
