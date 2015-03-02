/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2015 Link.it srl (http://link.it). 
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import javax.xml.soap.Name;
import javax.xml.soap.SOAPException;

import org.apache.axis.AxisFault;
import org.apache.axis.Message;
import org.apache.axis.message.PrefixedQName;
import org.openspcoop2.core.constants.TipoPdD;
import org.openspcoop2.pdd.core.CostantiPdD;
import org.openspcoop2.protocol.sdk.constants.CodiceErroreIntegrazione;
import org.openspcoop2.protocol.sdk.constants.CostantiProtocollo;
import org.openspcoop2.protocol.spcoop.constants.SPCoopCostanti;
import org.openspcoop2.protocol.spcoop.testsuite.core.CostantiErroriIntegrazione;
import org.openspcoop2.protocol.spcoop.testsuite.core.CostantiTestSuite;
import org.openspcoop2.protocol.spcoop.testsuite.core.DatabaseProperties;
import org.openspcoop2.protocol.spcoop.testsuite.core.Utilities;
import org.openspcoop2.protocol.spcoop.testsuite.core.UtilitiesEGov;
import org.openspcoop2.testsuite.clients.ClientHttpGenerico;
import org.openspcoop2.testsuite.core.ErroreAttesoOpenSPCoopLogCore;
import org.openspcoop2.testsuite.core.FatalTestSuiteException;
import org.openspcoop2.testsuite.core.Repository;
import org.openspcoop2.testsuite.db.DatabaseComponent;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.resources.FileSystemUtilities;
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
public class SOAPMessageScorretti {

	/** Identificativo del gruppo */
	public static final String ID_GRUPPO = "SOAPMessageScorretti";
	

	
	
	
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
	
	
	
	
	private byte[] getBusta(){
		return this.getBusta(null);
	}
	private byte[] getBusta(String azione){
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
				azione,
				null,
				SPCoopCostanti.TIPO_TEMPO_SPC);
		return bustaSOAP.getBytes();
	}
	
	


	
	
	
	
	// ------------- CONTENT TYPE NON SUPPORTATO ------------------

	Repository repositoryContentTypeNonSupportatoPD=new Repository();
	@Test(groups={SOAPMessageScorretti.ID_GRUPPO,SOAPMessageScorretti.ID_GRUPPO+".CONTENT_TYPE_NON_SUPPORTATO_PD"})
	public void contentTypeNonSupportato_PD()throws FatalTestSuiteException,SOAPException, Exception{
		
		// costruzione busta
		java.io.ByteArrayInputStream bin =
			new java.io.ByteArrayInputStream(FileSystemUtilities.readBytesFromFile(Utilities.testSuiteProperties.getSoap11FileName()));
		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();

		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryHeaderDontUnderstandPD);
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_PROFILO_SINCRONO);
			client.setContentType("application/soap+xml"); // ContentType per messaggi SOAP 1.2
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
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				Reporter.log("Controllo fault code ["+Utilities.toString(CodiceErroreIntegrazione.CODICE_429_CONTENT_TYPE_NON_SUPPORTATO)+"]");
				Assert.assertTrue(Utilities.toString(CodiceErroreIntegrazione.CODICE_429_CONTENT_TYPE_NON_SUPPORTATO).equals(error.getFaultCode().getLocalPart()));
				String msgErrore = "Il valore dell'header HTTP Content-Type (application/soap+xml) non rientra tra quelli supportati dal protocollo (text/xml, multipart/related)";
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
	}
	
	
	Repository repositoryContentTypeNonSupportatoPA=new Repository();
	@Test(groups={SOAPMessageScorretti.ID_GRUPPO,SOAPMessageScorretti.ID_GRUPPO+".CONTENT_TYPE_NON_SUPPORTATO_PA"})
	public void contentTypeNonSupportato_PA()throws FatalTestSuiteException,SOAPException, Exception{
		
		// costruzione busta
		java.io.ByteArrayInputStream bin =
			new java.io.ByteArrayInputStream(getBusta());
		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();

		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryHeaderDontUnderstandPA);
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneBusteErogatore());
			client.setContentType("application/soap+xml"); // ContentType per messaggi SOAP 1.2
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
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				Assert.assertTrue(SPCoopCostanti.FAULT_CODE_PROCESSAMENTO_SPCOOP.equals(error.getFaultCode().getLocalPart()) ||
						SPCoopCostanti.FAULT_CODE_PROCESSAMENTO_SPCOOP.equals("soap:"+error.getFaultCode().getLocalPart()) ||
						SPCoopCostanti.FAULT_CODE_PROCESSAMENTO_SPCOOP.equals("soapenv:"+error.getFaultCode().getLocalPart()));
				String msgErrore = SPCoopCostanti.FAULT_STRING_PROCESSAMENTO_SPCOOP;
				Reporter.log("Controllo fault string ["+msgErrore+"]");
				Assert.assertTrue(msgErrore.equals(error.getFaultString()));
				
				List<org.openspcoop2.protocol.spcoop.testsuite.core.OpenSPCoopDetail> eccezioni = 
					new ArrayList<org.openspcoop2.protocol.spcoop.testsuite.core.OpenSPCoopDetail>();
				org.openspcoop2.protocol.spcoop.testsuite.core.OpenSPCoopDetail ecc = new org.openspcoop2.protocol.spcoop.testsuite.core.OpenSPCoopDetail();
				ecc.setCodice(Utilities.toString(CodiceErroreIntegrazione.CODICE_429_CONTENT_TYPE_NON_SUPPORTATO));
				ecc.setDescrizione(CostantiErroriIntegrazione.MSG_429_CONTENT_TYPE_NON_SUPPORTATO.replace(CostantiErroriIntegrazione.MSG_429_CONTENT_TYPE_KEY,"application/soap+xml"));
				ecc.setCheckDescrizioneTramiteMatchEsatto(true);
				eccezioni.add(ecc);
				
				Assert.assertTrue(Utilities.existsOpenSPCoopDetails(error)); // vengono generati in caso di 5XX
					
				Utilities.verificaFaultOpenSPCoopDetail(error, 
						Utilities.testSuiteProperties.getIdentitaDefault(),TipoPdD.APPLICATIVA,"RicezioneBuste_PA", 
						eccezioni, new ArrayList<org.openspcoop2.protocol.spcoop.testsuite.core.OpenSPCoopDetail>());
			}finally{
				dbComponentErogatore.close();
			}
			
		}catch(Exception e){
			throw e;
		}finally{
			dbComponentErogatore.close();
		}
	}
	
	
	
	
	
	
	// ------------- SOAP HEADER DON'T UNDERSTAND  ------------------

	Repository repositoryHeaderDontUnderstandPD=new Repository();
	@Test(groups={SOAPMessageScorretti.ID_GRUPPO,SOAPMessageScorretti.ID_GRUPPO+".HEADER_NOT_UNDERSTAND_PD"})
	public void headerDontUnderstand_PD()throws FatalTestSuiteException,SOAPException, Exception{
		
		// costruzione busta
		java.io.ByteArrayInputStream bin =
			new java.io.ByteArrayInputStream(FileSystemUtilities.readBytesFromFile(Utilities.testSuiteProperties.getSoap11FileName()));
		Message msg=new Message(bin);
		
		Name nameMustUnderstand = new PrefixedQName("http://openspcoop.exampleMustUnderstand.unknown/1","headerUnknownA",CostantiPdD.OPENSPCOOP2);
		org.apache.axis.message.SOAPHeaderElement headerMustUnderstand = 
			new org.apache.axis.message.SOAPHeaderElement(nameMustUnderstand);
		headerMustUnderstand.setMustUnderstand(true);
		headerMustUnderstand.setActor(null);
		headerMustUnderstand.addNamespaceDeclaration("SOAP_ENV","http://schemas.xmlsoap.org/soap/envelope/");
		
		Name nameMustUnderstand2 = new PrefixedQName("http://openspcoop.exampleMustUnderstand.unknown/2","headerUnknownB",CostantiPdD.OPENSPCOOP2);
		org.apache.axis.message.SOAPHeaderElement headerMustUnderstand2 = 
			new org.apache.axis.message.SOAPHeaderElement(nameMustUnderstand2);
		headerMustUnderstand2.setMustUnderstand(true);
		headerMustUnderstand2.setActor(null);
		headerMustUnderstand2.addNamespaceDeclaration("SOAP_ENV","http://schemas.xmlsoap.org/soap/envelope/");
		
		if(msg.getSOAPHeader()==null)
			msg.getSOAPEnvelope().addHeader();
		
		msg.getSOAPHeader().addChildElement(headerMustUnderstand);
		msg.getSOAPHeader().addChildElement(headerMustUnderstand2);
		
		msg.getSOAPPartAsBytes();

		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryHeaderDontUnderstandPD);
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_PROFILO_SINCRONO);
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
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				Reporter.log("Controllo fault code ["+Utilities.toString(CodiceErroreIntegrazione.CODICE_427_MUSTUNDERSTAND_ERROR)+"]");
				Assert.assertTrue(Utilities.toString(CodiceErroreIntegrazione.CODICE_427_MUSTUNDERSTAND_ERROR).equals(error.getFaultCode().getLocalPart()));
				String msgErrore = CostantiErroriIntegrazione.MSG_427_MUSTUNDERSTAND_ERROR+"{http://openspcoop.exampleMustUnderstand.unknown/1}headerUnknownA, {http://openspcoop.exampleMustUnderstand.unknown/2}headerUnknownB";
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
	}
	
	
	Repository repositoryHeaderDontUnderstandPA=new Repository();
	@Test(groups={SOAPMessageScorretti.ID_GRUPPO,SOAPMessageScorretti.ID_GRUPPO+".HEADER_NOT_UNDERSTAND_PA"})
	public void headerDontUnderstand_PA()throws FatalTestSuiteException,SOAPException, Exception{
		
		// costruzione busta
		java.io.ByteArrayInputStream bin =
			new java.io.ByteArrayInputStream(getBusta());
		Message msg=new Message(bin);
		
		Name nameMustUnderstand = new PrefixedQName("http://openspcoop.exampleMustUnderstand.unknown/1","headerUnknownA",CostantiPdD.OPENSPCOOP2);
		org.apache.axis.message.SOAPHeaderElement headerMustUnderstand = 
			new org.apache.axis.message.SOAPHeaderElement(nameMustUnderstand);
		headerMustUnderstand.setMustUnderstand(true);
		headerMustUnderstand.setActor(null);
		headerMustUnderstand.addNamespaceDeclaration("SOAP_ENV","http://schemas.xmlsoap.org/soap/envelope/");
		
		Name nameMustUnderstand2 = new PrefixedQName("http://openspcoop.exampleMustUnderstand.unknown/2","headerUnknownB",CostantiPdD.OPENSPCOOP2);
		org.apache.axis.message.SOAPHeaderElement headerMustUnderstand2 = 
			new org.apache.axis.message.SOAPHeaderElement(nameMustUnderstand2);
		headerMustUnderstand2.setMustUnderstand(true);
		headerMustUnderstand2.setActor(null);
		headerMustUnderstand2.addNamespaceDeclaration("SOAP_ENV","http://schemas.xmlsoap.org/soap/envelope/");
		
		if(msg.getSOAPHeader()==null)
			msg.getSOAPEnvelope().addHeader();
		
		msg.getSOAPHeader().addChildElement(headerMustUnderstand);
		msg.getSOAPHeader().addChildElement(headerMustUnderstand2);
		
		msg.getSOAPPartAsBytes();

		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryHeaderDontUnderstandPA);
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
				throw new Exception("Attesa eccezione per controllo soapAction");
			} catch (AxisFault error) {
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				Assert.assertTrue(SPCoopCostanti.FAULT_CODE_PROCESSAMENTO_SPCOOP.equals(error.getFaultCode().getLocalPart()) ||
						SPCoopCostanti.FAULT_CODE_PROCESSAMENTO_SPCOOP.equals("soap:"+error.getFaultCode().getLocalPart()) ||
						SPCoopCostanti.FAULT_CODE_PROCESSAMENTO_SPCOOP.equals("soapenv:"+error.getFaultCode().getLocalPart()));
				String msgErrore = SPCoopCostanti.FAULT_STRING_PROCESSAMENTO_SPCOOP;
				Reporter.log("Controllo fault string ["+msgErrore+"]");
				Assert.assertTrue(msgErrore.equals(error.getFaultString()));
				
				List<org.openspcoop2.protocol.spcoop.testsuite.core.OpenSPCoopDetail> eccezioni = 
					new ArrayList<org.openspcoop2.protocol.spcoop.testsuite.core.OpenSPCoopDetail>();
				org.openspcoop2.protocol.spcoop.testsuite.core.OpenSPCoopDetail ecc = new org.openspcoop2.protocol.spcoop.testsuite.core.OpenSPCoopDetail();
				ecc.setCodice(Utilities.toString(CodiceErroreIntegrazione.CODICE_427_MUSTUNDERSTAND_ERROR));
				ecc.setDescrizione(CostantiErroriIntegrazione.MSG_427_MUSTUNDERSTAND_ERROR +"{http://openspcoop.exampleMustUnderstand.unknown/1}headerUnknownA, {http://openspcoop.exampleMustUnderstand.unknown/2}headerUnknownB");
				ecc.setCheckDescrizioneTramiteMatchEsatto(true);
				eccezioni.add(ecc);
				
				Assert.assertTrue(Utilities.existsOpenSPCoopDetails(error)); // vengono generati in caso di 5XX
					
				Utilities.verificaFaultOpenSPCoopDetail(error, 
						Utilities.testSuiteProperties.getIdentitaDefault(),TipoPdD.APPLICATIVA,"RicezioneBuste_PA", 
						eccezioni, new ArrayList<org.openspcoop2.protocol.spcoop.testsuite.core.OpenSPCoopDetail>());
			}finally{
				dbComponentErogatore.close();
			}
			
		}catch(Exception e){
			throw e;
		}finally{
			dbComponentErogatore.close();
		}
	}
	
	
	
	
	
	
	// ------------- SOAP ENVELOPE NAMESPACE SCORRETTI ------------------

	Repository repositoryNamespaceErratoPD=new Repository();
	@Test(groups={SOAPMessageScorretti.ID_GRUPPO,SOAPMessageScorretti.ID_GRUPPO+".NAMESPACE_ERRATO_PD"})
	public void namespaceErrato_PD()throws FatalTestSuiteException,SOAPException, Exception{
		
		Date dataInizioTest = DateManager.getDate();
		
		// costruzione busta
		String xml = FileSystemUtilities.readFile(Utilities.testSuiteProperties.getSoap11FileName());
		xml = xml.replace("http://schemas.xmlsoap.org/soap/envelope/", "http://www.w3.org/2003/05/soap-envelope"); // imposto namespace di SOAP 1.2
		Message msg=new Message(new ByteArrayInputStream(xml.getBytes()));
		msg.getSOAPPartAsBytes();

		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryNamespaceErratoPD);
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_PROFILO_SINCRONO);
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
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				Reporter.log("Controllo fault code ["+Utilities.toString(CodiceErroreIntegrazione.CODICE_430_SOAP_ENVELOPE_NAMESPACE_ERROR)+"]");
				Assert.assertTrue(Utilities.toString(CodiceErroreIntegrazione.CODICE_430_SOAP_ENVELOPE_NAMESPACE_ERROR).equals(error.getFaultCode().getLocalPart()));
				String msgErrore = "SOAP Envelope contiene un namespace (http://www.w3.org/2003/05/soap-envelope) diverso da quello atteso per messaggi SOAP 1.1 (http://schemas.xmlsoap.org/soap/envelope/)";
				String msgErrore2 = "SOAP Envelope contiene un namespace (Impossibile recuperare il valore del namespace) diverso da quello atteso per messaggi SOAP 1.1 (http://schemas.xmlsoap.org/soap/envelope/)";
				Reporter.log("Controllo fault string ["+error.getFaultString()+"]");
				Assert.assertTrue(msgErrore.equals(error.getFaultString()) || msgErrore2.equals(error.getFaultString()));
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
		err2.setMsgErrore("Generale(richiesta): Transport level information does not match with SOAP Message namespace URI");
		this.erroriAttesiOpenSPCoopCore.add(err2);
	}
	
	
	Repository repositoryNamespaceErratoPA=new Repository();
	@Test(groups={SOAPMessageScorretti.ID_GRUPPO,SOAPMessageScorretti.ID_GRUPPO+".NAMESPACE_ERRATO_PA"})
	public void namespaceErrato_PA()throws FatalTestSuiteException,SOAPException, Exception{
		
		Date dataInizioTest = DateManager.getDate();
		
		// costruzione busta
		String busta = new String(getBusta());
		busta = busta.replace("http://schemas.xmlsoap.org/soap/envelope/", "http://www.w3.org/2003/05/soap-envelope"); // imposto namespace di SOAP 1.2
		java.io.ByteArrayInputStream bin =
			new java.io.ByteArrayInputStream(busta.getBytes());
		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();

		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryNamespaceErratoPA);
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
				throw new Exception("Attesa eccezione per controllo soapAction");
			} catch (AxisFault error) {
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				Assert.assertTrue(SPCoopCostanti.FAULT_CODE_PROCESSAMENTO_SPCOOP.equals(error.getFaultCode().getLocalPart()) ||
						SPCoopCostanti.FAULT_CODE_PROCESSAMENTO_SPCOOP.equals("soap:"+error.getFaultCode().getLocalPart()) ||
						SPCoopCostanti.FAULT_CODE_PROCESSAMENTO_SPCOOP.equals("soapenv:"+error.getFaultCode().getLocalPart()));
				String msgErrore = SPCoopCostanti.FAULT_STRING_PROCESSAMENTO_SPCOOP;
				String msgErrore2 = "SOAP Envelope contiene un namespace (Impossibile recuperare il valore del namespace) diverso da quello atteso per messaggi SOAP11 (http://schemas.xmlsoap.org/soap/envelope/)";
				Reporter.log("Controllo fault string ["+error.getFaultString()+"]");
				Assert.assertTrue(msgErrore.equals(error.getFaultString()) || msgErrore2.equals(error.getFaultString()));
				
				List<org.openspcoop2.protocol.spcoop.testsuite.core.OpenSPCoopDetail> eccezioni = 
					new ArrayList<org.openspcoop2.protocol.spcoop.testsuite.core.OpenSPCoopDetail>();
				org.openspcoop2.protocol.spcoop.testsuite.core.OpenSPCoopDetail ecc = new org.openspcoop2.protocol.spcoop.testsuite.core.OpenSPCoopDetail();
				ecc.setCodice(Utilities.toString(CodiceErroreIntegrazione.CODICE_430_SOAP_ENVELOPE_NAMESPACE_ERROR));
				ecc.setDescrizione(CostantiErroriIntegrazione.MSG_430_SOAP_ENVELOPE_NAMESPACE_ERROR.replace(CostantiErroriIntegrazione.MSG_430_NAMESPACE_KEY, "http://www.w3.org/2003/05/soap-envelope"));
				ecc.setCheckDescrizioneTramiteMatchEsatto(true);
				eccezioni.add(ecc);
				
				org.openspcoop2.protocol.spcoop.testsuite.core.OpenSPCoopDetail ecc2 = new org.openspcoop2.protocol.spcoop.testsuite.core.OpenSPCoopDetail();
				ecc2.setCodice(Utilities.toString(CodiceErroreIntegrazione.CODICE_430_SOAP_ENVELOPE_NAMESPACE_ERROR));
				ecc2.setDescrizione(CostantiErroriIntegrazione.MSG_430_SOAP_ENVELOPE_NAMESPACE_ERROR.replace(CostantiErroriIntegrazione.MSG_430_NAMESPACE_KEY, "Impossibile recuperare il valore del namespace"));
				ecc2.setCheckDescrizioneTramiteMatchEsatto(true);
				eccezioni.add(ecc2);
				
				Assert.assertTrue(Utilities.existsOpenSPCoopDetails(error)); // vengono generati in caso di 5XX
					
				Utilities.verificaFaultOpenSPCoopDetail(error, 
						Utilities.testSuiteProperties.getIdentitaDefault(),TipoPdD.APPLICATIVA,"RicezioneBuste_PA", 
						eccezioni, new ArrayList<org.openspcoop2.protocol.spcoop.testsuite.core.OpenSPCoopDetail>(), false);

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
		err2.setMsgErrore("Generale(richiesta): Transport level information does not match with SOAP Message namespace URI");
		this.erroriAttesiOpenSPCoopCore.add(err2);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	// ------------- STRUTTURA XML ERRATA ------------------

	/**
	 * Body errato inviato alla porta delegata
	 */
	Repository repositoryStrutturaXMLErrataPD=new Repository();
	@Test(groups={SOAPMessageScorretti.ID_GRUPPO,SOAPMessageScorretti.ID_GRUPPO+".STRUTTURA_XML_ERRATA_PD"})
	public void strutturaXMLErrata_PD_FirstBodyChild()throws FatalTestSuiteException,SOAPException, Exception{
		
		Date dataInizioTest = DateManager.getDate();
		
		// costruzione busta
		String xml = FileSystemUtilities.readFile(Utilities.testSuiteProperties.getSoap11FileName());
		xml = xml.replace("</ns1:getQuote>", "<ns1:getQuote>"); // imposto struttura xml errata
		Message msg=new Message(new ByteArrayInputStream(xml.getBytes()));
		msg.getSOAPPartAsBytes();

		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryStrutturaXMLErrataPD);
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_PROFILO_SINCRONO);
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
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				Reporter.log("Controllo fault code ["+Utilities.toString(CodiceErroreIntegrazione.CODICE_426_SERVLET_ERROR)+"] oppure ["+Utilities.toString(CodiceErroreIntegrazione.CODICE_432_MESSAGGIO_XML_MALFORMATO)+"]");
				Assert.assertTrue(Utilities.toString(CodiceErroreIntegrazione.CODICE_426_SERVLET_ERROR).equals(error.getFaultCode().getLocalPart()) || Utilities.toString(CodiceErroreIntegrazione.CODICE_432_MESSAGGIO_XML_MALFORMATO).equals(error.getFaultCode().getLocalPart()));
				Reporter.log("Controllo fault string");
				Assert.assertTrue(error.getFaultString().contains("getQuote")); // il messaggio di qualsiasi parser dovrebbe cmq indicare il motivo del perche' il msg non e' corretto
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
		err2.setMsgErrore("</soapenv:Envelope>; expected </ns1:getQuote>.");
		this.erroriAttesiOpenSPCoopCore.add(err2);
		

		ErroreAttesoOpenSPCoopLogCore err3 = new ErroreAttesoOpenSPCoopLogCore();
		err3.setIntervalloInferiore(dataInizioTest);
		err3.setIntervalloSuperiore(dataFineTest);
		err3.setMsgErrore("Invalid chunk");
		this.erroriAttesiOpenSPCoopCore.add(err3);

        ErroreAttesoOpenSPCoopLogCore err4 = new ErroreAttesoOpenSPCoopLogCore();
        err4.setIntervalloInferiore(dataInizioTest);
        err4.setIntervalloSuperiore(dataFineTest);
        err4.setMsgErrore("The element type \"ns1:getQuote\" must be terminated by the matching end-tag \"</ns1:getQuote>\"");
        this.erroriAttesiOpenSPCoopCore.add(err4);
        
        ErroreAttesoOpenSPCoopLogCore err5 = new ErroreAttesoOpenSPCoopLogCore();
        err5.setIntervalloInferiore(dataInizioTest);
        err5.setIntervalloSuperiore(dataFineTest);
        err5.setMsgErrore("Unexpected close tag </soapenv:Body>; expected </ns1:getQuote>");
        this.erroriAttesiOpenSPCoopCore.add(err5);
        
       
	}
	
	
	
	@Test(groups={SOAPMessageScorretti.ID_GRUPPO,SOAPMessageScorretti.ID_GRUPPO+".STRUTTURA_XML_ERRATA_PD"})
	public void strutturaXMLErrata_PD_Body()throws FatalTestSuiteException,SOAPException, Exception{
		
		Date dataInizioTest = DateManager.getDate();
		
		// costruzione busta
		String xml = FileSystemUtilities.readFile(Utilities.testSuiteProperties.getSoap11FileName());
		xml = xml.replace("</soapenv:Body>", "<soapenv:Body>"); // imposto struttura xml errata
		Message msg=new Message(new ByteArrayInputStream(xml.getBytes()));
		msg.getSOAPPartAsBytes();

		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryStrutturaXMLErrataPD);
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_PROFILO_SINCRONO);
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
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				Reporter.log("Controllo fault code ["+Utilities.toString(CodiceErroreIntegrazione.CODICE_426_SERVLET_ERROR)+"] oppure ["+Utilities.toString(CodiceErroreIntegrazione.CODICE_432_MESSAGGIO_XML_MALFORMATO)+"]");
				Assert.assertTrue(Utilities.toString(CodiceErroreIntegrazione.CODICE_426_SERVLET_ERROR).equals(error.getFaultCode().getLocalPart()) || Utilities.toString(CodiceErroreIntegrazione.CODICE_432_MESSAGGIO_XML_MALFORMATO).equals(error.getFaultCode().getLocalPart()));
				Reporter.log("Controllo fault string");
				Assert.assertTrue(error.getFaultString().contains("soapenv:Body")); // il messaggio di qualsiasi parser dovrebbe cmq indicare il motivo del perche' il msg non e' corretto
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
		err2.setMsgErrore("close tag for element <soapenv:Body>");
		this.erroriAttesiOpenSPCoopCore.add(err2);
		
		ErroreAttesoOpenSPCoopLogCore err3 = new ErroreAttesoOpenSPCoopLogCore();
		err3.setIntervalloInferiore(dataInizioTest);
		err3.setIntervalloSuperiore(dataFineTest);
		err3.setMsgErrore("Invalid chunk");
		this.erroriAttesiOpenSPCoopCore.add(err3);

        ErroreAttesoOpenSPCoopLogCore err4 = new ErroreAttesoOpenSPCoopLogCore();
        err4.setIntervalloInferiore(dataInizioTest);
        err4.setIntervalloSuperiore(dataFineTest);
        err4.setMsgErrore("The element type \"soapenv:Body\" must be terminated by the matching end-tag \"</soapenv:Body>\"");
        this.erroriAttesiOpenSPCoopCore.add(err4);
        
        ErroreAttesoOpenSPCoopLogCore err5 = new ErroreAttesoOpenSPCoopLogCore();
        err5.setIntervalloInferiore(dataInizioTest);
        err5.setIntervalloSuperiore(dataFineTest);
        err5.setMsgErrore("Unexpected close tag </soapenv:Envelope>; expected </soapenv:Body>");
        this.erroriAttesiOpenSPCoopCore.add(err5);
        
	}
	
	@Test(groups={SOAPMessageScorretti.ID_GRUPPO,SOAPMessageScorretti.ID_GRUPPO+".STRUTTURA_XML_ERRATA_PD"})
	public void strutturaXMLErrata_PD_IsideBody()throws FatalTestSuiteException,SOAPException, Exception{
		
		Date dataInizioTest = DateManager.getDate();
		
		// costruzione busta
		String xml = FileSystemUtilities.readFile(Utilities.testSuiteProperties.getSoap11FileName());
		xml = xml.replace("</symbol>", "<symbol>"); // imposto struttura xml errata
		Message msg=new Message(new ByteArrayInputStream(xml.getBytes()));
		msg.getSOAPPartAsBytes();

		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryStrutturaXMLErrataPD);
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_PROFILO_SINCRONO);
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
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				Reporter.log("Controllo fault code ["+Utilities.toString(CodiceErroreIntegrazione.CODICE_426_SERVLET_ERROR)+"] oppure ["+Utilities.toString(CodiceErroreIntegrazione.CODICE_432_MESSAGGIO_XML_MALFORMATO)+"]");
				Assert.assertTrue(Utilities.toString(CodiceErroreIntegrazione.CODICE_426_SERVLET_ERROR).equals(error.getFaultCode().getLocalPart()) || Utilities.toString(CodiceErroreIntegrazione.CODICE_432_MESSAGGIO_XML_MALFORMATO).equals(error.getFaultCode().getLocalPart()));
				Reporter.log("Controllo fault string");
				Assert.assertTrue(error.getFaultString().contains("symbol")); // il messaggio di qualsiasi parser dovrebbe cmq indicare il motivo del perche' il msg non e' corretto
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
		err2.setMsgErrore("Unexpected close tag </soapenv:Body>; expected </symbol>");
		this.erroriAttesiOpenSPCoopCore.add(err2);
		
		ErroreAttesoOpenSPCoopLogCore err3 = new ErroreAttesoOpenSPCoopLogCore();
		err3.setIntervalloInferiore(dataInizioTest);
		err3.setIntervalloSuperiore(dataFineTest);
		err3.setMsgErrore("Invalid chunk");
		this.erroriAttesiOpenSPCoopCore.add(err3);

        ErroreAttesoOpenSPCoopLogCore err4 = new ErroreAttesoOpenSPCoopLogCore();
        err4.setIntervalloInferiore(dataInizioTest);
        err4.setIntervalloSuperiore(dataFineTest);
        err4.setMsgErrore("The element type \"symbol\" must be terminated by the matching end-tag \"</symbol>\"");
        this.erroriAttesiOpenSPCoopCore.add(err4);
        
        ErroreAttesoOpenSPCoopLogCore err5 = new ErroreAttesoOpenSPCoopLogCore();
        err5.setIntervalloInferiore(dataInizioTest);
        err5.setIntervalloSuperiore(dataFineTest);
        err5.setMsgErrore("Unexpected close tag </ns1:getQuote>; expected </symbol>");
        this.erroriAttesiOpenSPCoopCore.add(err5);
        

	}
	
	
	
	
	
	/**
	 * Body errato inviato alla porta delegata stateful
	 */
	Repository repositoryStrutturaXMLErrataPD_Stateful=new Repository();
	@Test(groups={SOAPMessageScorretti.ID_GRUPPO,SOAPMessageScorretti.ID_GRUPPO+".STRUTTURA_XML_ERRATA_PD_STATEFUL"})
	public void strutturaXMLErrata_PD_Stateful_FirstBodyChild()throws FatalTestSuiteException,SOAPException, Exception{
		
		Date dataInizioTest = DateManager.getDate();
		
		// costruzione busta
		String xml = FileSystemUtilities.readFile(Utilities.testSuiteProperties.getSoap11FileName());
		xml = xml.replace("</ns1:getQuote>", "<ns1:getQuote>"); // imposto struttura xml errata
		Message msg=new Message(new ByteArrayInputStream(xml.getBytes()));
		msg.getSOAPPartAsBytes();

		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryStrutturaXMLErrataPD_Stateful);
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_PROFILO_SINCRONO_STATEFUL);
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
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				Reporter.log("Controllo fault code ["+Utilities.toString(CodiceErroreIntegrazione.CODICE_426_SERVLET_ERROR)+"] oppure ["+Utilities.toString(CodiceErroreIntegrazione.CODICE_432_MESSAGGIO_XML_MALFORMATO)+"]");
				Assert.assertTrue(Utilities.toString(CodiceErroreIntegrazione.CODICE_426_SERVLET_ERROR).equals(error.getFaultCode().getLocalPart()) || Utilities.toString(CodiceErroreIntegrazione.CODICE_432_MESSAGGIO_XML_MALFORMATO).equals(error.getFaultCode().getLocalPart()));
				Reporter.log("Controllo fault string");
				Assert.assertTrue(error.getFaultString().contains("getQuote")); // il messaggio di qualsiasi parser dovrebbe cmq indicare il motivo del perche' il msg non e' corretto
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
		err2.setMsgErrore("Unexpected close tag </soapenv:Envelope>; expected </ns1:getQuote>");
		this.erroriAttesiOpenSPCoopCore.add(err2);
		
		ErroreAttesoOpenSPCoopLogCore err3 = new ErroreAttesoOpenSPCoopLogCore();
		err3.setIntervalloInferiore(dataInizioTest);
		err3.setIntervalloSuperiore(dataFineTest);
		err3.setMsgErrore("Unexpected close tag </soapenv:Body>; expected </ns1:getQuote>");
		this.erroriAttesiOpenSPCoopCore.add(err3);
		
        ErroreAttesoOpenSPCoopLogCore err4 = new ErroreAttesoOpenSPCoopLogCore();
        err4.setIntervalloInferiore(dataInizioTest);
        err4.setIntervalloSuperiore(dataFineTest);
        err4.setMsgErrore("The element type \"ns1:getQuote\" must be terminated by the matching end-tag \"</ns1:getQuote>\"");
        this.erroriAttesiOpenSPCoopCore.add(err4);

	}
	
	
	
	@Test(groups={SOAPMessageScorretti.ID_GRUPPO,SOAPMessageScorretti.ID_GRUPPO+".STRUTTURA_XML_ERRATA_PD_STATEFUL"})
	public void strutturaXMLErrata_PD_Stateful_Body()throws FatalTestSuiteException,SOAPException, Exception{
		
		Date dataInizioTest = DateManager.getDate();
		
		// costruzione busta
		String xml = FileSystemUtilities.readFile(Utilities.testSuiteProperties.getSoap11FileName());
		xml = xml.replace("</soapenv:Body>", "<soapenv:Body>"); // imposto struttura xml errata
		Message msg=new Message(new ByteArrayInputStream(xml.getBytes()));
		msg.getSOAPPartAsBytes();

		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryStrutturaXMLErrataPD_Stateful);
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_PROFILO_SINCRONO_STATEFUL);
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
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				Reporter.log("Controllo fault code ["+Utilities.toString(CodiceErroreIntegrazione.CODICE_426_SERVLET_ERROR)+"] oppure ["+Utilities.toString(CodiceErroreIntegrazione.CODICE_432_MESSAGGIO_XML_MALFORMATO)+"]");
				Assert.assertTrue(Utilities.toString(CodiceErroreIntegrazione.CODICE_426_SERVLET_ERROR).equals(error.getFaultCode().getLocalPart()) || Utilities.toString(CodiceErroreIntegrazione.CODICE_432_MESSAGGIO_XML_MALFORMATO).equals(error.getFaultCode().getLocalPart()));
				Reporter.log("Controllo fault string");
				Assert.assertTrue(error.getFaultString().contains("soapenv:Body")); // il messaggio di qualsiasi parser dovrebbe cmq indicare il motivo del perche' il msg non e' corretto
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
		err2.setMsgErrore("close tag for element <soapenv:Body>");
		this.erroriAttesiOpenSPCoopCore.add(err2);
		
		ErroreAttesoOpenSPCoopLogCore err3 = new ErroreAttesoOpenSPCoopLogCore();
		err3.setIntervalloInferiore(dataInizioTest);
		err3.setIntervalloSuperiore(dataFineTest);
		err3.setMsgErrore("Unexpected close tag </soapenv:Envelope>; expected </soapenv:Body>");
		this.erroriAttesiOpenSPCoopCore.add(err3);
		
        ErroreAttesoOpenSPCoopLogCore err4 = new ErroreAttesoOpenSPCoopLogCore();
        err4.setIntervalloInferiore(dataInizioTest);
        err4.setIntervalloSuperiore(dataFineTest);
        err4.setMsgErrore("The element type \"soapenv:Body\" must be terminated by the matching end-tag \"</soapenv:Body>\"");
        this.erroriAttesiOpenSPCoopCore.add(err4);
	}
	
	@Test(groups={SOAPMessageScorretti.ID_GRUPPO,SOAPMessageScorretti.ID_GRUPPO+".STRUTTURA_XML_ERRATA_PD_STATEFUL"})
	public void strutturaXMLErrata_PD_Stateful_IsideBody()throws FatalTestSuiteException,SOAPException, Exception{
		
		Date dataInizioTest = DateManager.getDate();
		
		// costruzione busta
		String xml = FileSystemUtilities.readFile(Utilities.testSuiteProperties.getSoap11FileName());
		xml = xml.replace("</symbol>", "<symbol>"); // imposto struttura xml errata
		Message msg=new Message(new ByteArrayInputStream(xml.getBytes()));
		msg.getSOAPPartAsBytes();

		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryStrutturaXMLErrataPD_Stateful);
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_PROFILO_SINCRONO_STATEFUL);
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
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				Reporter.log("Controllo fault code ["+Utilities.toString(CodiceErroreIntegrazione.CODICE_426_SERVLET_ERROR)+"] oppure ["+Utilities.toString(CodiceErroreIntegrazione.CODICE_432_MESSAGGIO_XML_MALFORMATO)+"]");
				Assert.assertTrue(Utilities.toString(CodiceErroreIntegrazione.CODICE_426_SERVLET_ERROR).equals(error.getFaultCode().getLocalPart()) || Utilities.toString(CodiceErroreIntegrazione.CODICE_432_MESSAGGIO_XML_MALFORMATO).equals(error.getFaultCode().getLocalPart()));
				Reporter.log("Controllo fault string");
				Assert.assertTrue(error.getFaultString().contains("symbol")); // il messaggio di qualsiasi parser dovrebbe cmq indicare il motivo del perche' il msg non e' corretto
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
		err2.setMsgErrore("Unexpected close tag </soapenv:Body>; expected </symbol>");
		this.erroriAttesiOpenSPCoopCore.add(err2);
		
		ErroreAttesoOpenSPCoopLogCore err3 = new ErroreAttesoOpenSPCoopLogCore();
		err3.setIntervalloInferiore(dataInizioTest);
		err3.setIntervalloSuperiore(dataFineTest);
		err3.setMsgErrore("Unexpected close tag </ns1:getQuote>; expected </symbol>");
		this.erroriAttesiOpenSPCoopCore.add(err3);
		
        ErroreAttesoOpenSPCoopLogCore err4 = new ErroreAttesoOpenSPCoopLogCore();
        err4.setIntervalloInferiore(dataInizioTest);
        err4.setIntervalloSuperiore(dataFineTest);
        err4.setMsgErrore("The element type \"symbol\" must be terminated by the matching end-tag \"</symbol>\"");
        this.erroriAttesiOpenSPCoopCore.add(err4);
	}
	
	
	
	
	/**
	 * Body errato inviato alla porta applicativa
	 */
	Repository repositoryStrutturaXMLErrataPA=new Repository();
	@Test(groups={SOAPMessageScorretti.ID_GRUPPO,SOAPMessageScorretti.ID_GRUPPO+".STRUTTURA_XML_ERRATA_PA"})
	public void strutturaXMLErrata_PA_Body()throws FatalTestSuiteException,SOAPException, Exception{
		
		Date dataInizioTest = DateManager.getDate();
		
		// costruzione busta
		String busta = new String(getBusta());
		busta = busta.replace("</soapenv:Body>", "<soapenv:Body>"); // imposto struttura xml errata
		java.io.ByteArrayInputStream bin =
			new java.io.ByteArrayInputStream(busta.getBytes());
		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();

		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryStrutturaXMLErrataPA);
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
				throw new Exception("Attesa eccezione per controllo soapAction");
			} catch (AxisFault error) {
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				Reporter.log("Controllo fault code ["+SPCoopCostanti.FAULT_CODE_VALIDAZIONE_SPCOOP+"]");
				Assert.assertTrue(SPCoopCostanti.FAULT_CODE_VALIDAZIONE_SPCOOP.equals(error.getFaultCode().getLocalPart()) ||
						SPCoopCostanti.FAULT_CODE_VALIDAZIONE_SPCOOP.equals("soap:"+error.getFaultCode().getLocalPart()) ||
						SPCoopCostanti.FAULT_CODE_VALIDAZIONE_SPCOOP.equals("soapenv:"+error.getFaultCode().getLocalPart()));
				String msgErrore = SPCoopCostanti.FAULT_STRING_VALIDAZIONE_SPCOOP;
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
		err2.setMsgErrore("was expecting a close tag for element <soapenv:Body>");
		this.erroriAttesiOpenSPCoopCore.add(err2);
		
		ErroreAttesoOpenSPCoopLogCore err3 = new ErroreAttesoOpenSPCoopLogCore();
		err3.setIntervalloInferiore(dataInizioTest);
		err3.setIntervalloSuperiore(dataFineTest);
		err3.setMsgErrore("Unexpected close tag </soapenv:Envelope>; expected </soapenv:Body>");
		this.erroriAttesiOpenSPCoopCore.add(err3);
		
        ErroreAttesoOpenSPCoopLogCore err4 = new ErroreAttesoOpenSPCoopLogCore();
        err4.setIntervalloInferiore(dataInizioTest);
        err4.setIntervalloSuperiore(dataFineTest);
        err4.setMsgErrore("The element type \"soapenv:Body\" must be terminated by the matching end-tag \"</soapenv:Body>\"");
        this.erroriAttesiOpenSPCoopCore.add(err4);
		
		
	}
	@Test(groups={SOAPMessageScorretti.ID_GRUPPO,SOAPMessageScorretti.ID_GRUPPO+".STRUTTURA_XML_ERRATA_PA"})
	public void strutturaXMLErrata_PA_BodyFirstChild()throws FatalTestSuiteException,SOAPException, Exception{
		
		Date dataInizioTest = DateManager.getDate();
		
		// costruzione busta
		String busta = new String(getBusta());
		busta = busta.replace("</helloworld>", "<helloworld>"); // imposto struttura xml errata
		java.io.ByteArrayInputStream bin =
			new java.io.ByteArrayInputStream(busta.getBytes());
		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();

		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryStrutturaXMLErrataPA);
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
				throw new Exception("Attesa eccezione per controllo soapAction");
			} catch (AxisFault error) {
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				Reporter.log("Controllo fault code ["+SPCoopCostanti.FAULT_CODE_VALIDAZIONE_SPCOOP+"]");
				Assert.assertTrue(SPCoopCostanti.FAULT_CODE_VALIDAZIONE_SPCOOP.equals(error.getFaultCode().getLocalPart()) ||
						SPCoopCostanti.FAULT_CODE_VALIDAZIONE_SPCOOP.equals("soap:"+error.getFaultCode().getLocalPart()) ||
						SPCoopCostanti.FAULT_CODE_VALIDAZIONE_SPCOOP.equals("soapenv:"+error.getFaultCode().getLocalPart()));
				String msgErrore = SPCoopCostanti.FAULT_STRING_VALIDAZIONE_SPCOOP;
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
		err2.setMsgErrore("Unexpected close tag </soapenv:Envelope>; expected </helloworld>");
		this.erroriAttesiOpenSPCoopCore.add(err2);
		
		ErroreAttesoOpenSPCoopLogCore err3 = new ErroreAttesoOpenSPCoopLogCore();
		err3.setIntervalloInferiore(dataInizioTest);
		err3.setIntervalloSuperiore(dataFineTest);
		err3.setMsgErrore("Unexpected close tag </soapenv:Body>; expected </helloworld>");
		this.erroriAttesiOpenSPCoopCore.add(err3);
		
		ErroreAttesoOpenSPCoopLogCore err4 = new ErroreAttesoOpenSPCoopLogCore();
        err4.setIntervalloInferiore(dataInizioTest);
        err4.setIntervalloSuperiore(dataFineTest);
        err4.setMsgErrore("The element type \"helloworld\" must be terminated by the matching end-tag \"</helloworld>\"");
        this.erroriAttesiOpenSPCoopCore.add(err4);
	}
	
	@Test(groups={SOAPMessageScorretti.ID_GRUPPO,SOAPMessageScorretti.ID_GRUPPO+".STRUTTURA_XML_ERRATA_PA"})
	public void strutturaXMLErrata_PA_InsideBody()throws FatalTestSuiteException,SOAPException, Exception{
		
		Date dataInizioTest = DateManager.getDate();
		
		// costruzione busta
		String busta = new String(getBusta());
		busta = busta.replace("</b>", "<b>"); // imposto struttura xml errata
		java.io.ByteArrayInputStream bin =
			new java.io.ByteArrayInputStream(busta.getBytes());
		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();

		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryStrutturaXMLErrataPA);
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
				throw new Exception("Attesa eccezione per controllo soapAction");
			} catch (AxisFault error) {
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				Reporter.log("Controllo fault code ["+SPCoopCostanti.FAULT_CODE_VALIDAZIONE_SPCOOP+"]");
				Assert.assertTrue(SPCoopCostanti.FAULT_CODE_VALIDAZIONE_SPCOOP.equals(error.getFaultCode().getLocalPart()) ||
						SPCoopCostanti.FAULT_CODE_VALIDAZIONE_SPCOOP.equals("soap:"+error.getFaultCode().getLocalPart()) ||
						SPCoopCostanti.FAULT_CODE_VALIDAZIONE_SPCOOP.equals("soapenv:"+error.getFaultCode().getLocalPart()));
				String msgErrore = SPCoopCostanti.FAULT_STRING_VALIDAZIONE_SPCOOP;
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
		err2.setMsgErrore("Unexpected close tag </soapenv:Body>; expected </b>");
		this.erroriAttesiOpenSPCoopCore.add(err2);
		
		ErroreAttesoOpenSPCoopLogCore err3 = new ErroreAttesoOpenSPCoopLogCore();
		err3.setIntervalloInferiore(dataInizioTest);
		err3.setIntervalloSuperiore(dataFineTest);
		err3.setMsgErrore("Unexpected close tag </helloworld>; expected </b>");
		this.erroriAttesiOpenSPCoopCore.add(err3);
		
		ErroreAttesoOpenSPCoopLogCore err4 = new ErroreAttesoOpenSPCoopLogCore();
        err4.setIntervalloInferiore(dataInizioTest);
        err4.setIntervalloSuperiore(dataFineTest);
        err4.setMsgErrore("The element type \"b\" must be terminated by the matching end-tag \"</b>\"");
        this.erroriAttesiOpenSPCoopCore.add(err4);
	}
	
	
	
	
	
	
	
	/**
	 * Body errato inviato alla porta applicativa stateful
	 */
	Repository repositoryStrutturaXMLErrataPAStateful=new Repository();
	@Test(groups={SOAPMessageScorretti.ID_GRUPPO,SOAPMessageScorretti.ID_GRUPPO+".STRUTTURA_XML_ERRATA_PA_STATEFUL"})
	public void strutturaXMLErrata_PA_Stateful_Body()throws FatalTestSuiteException,SOAPException, Exception{
		
		Date dataInizioTest = DateManager.getDate();
		
		// costruzione busta
		String busta = new String(getBusta("stateful"));
		busta = busta.replace("</soapenv:Body>", "<soapenv:Body>"); // imposto struttura xml errata
		java.io.ByteArrayInputStream bin =
			new java.io.ByteArrayInputStream(busta.getBytes());
		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();

		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryStrutturaXMLErrataPAStateful);
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
				throw new Exception("Attesa eccezione per controllo soapAction");
			} catch (AxisFault error) {
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				Reporter.log("Controllo fault code ["+SPCoopCostanti.FAULT_CODE_VALIDAZIONE_SPCOOP+"]");
				Assert.assertTrue(SPCoopCostanti.FAULT_CODE_VALIDAZIONE_SPCOOP.equals(error.getFaultCode().getLocalPart()) ||
						SPCoopCostanti.FAULT_CODE_VALIDAZIONE_SPCOOP.equals("soap:"+error.getFaultCode().getLocalPart()) ||
						SPCoopCostanti.FAULT_CODE_VALIDAZIONE_SPCOOP.equals("soapenv:"+error.getFaultCode().getLocalPart()));
				String msgErrore = SPCoopCostanti.FAULT_STRING_VALIDAZIONE_SPCOOP;
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
		err2.setMsgErrore("was expecting a close tag for element <soapenv:Body>");
		this.erroriAttesiOpenSPCoopCore.add(err2);
		
		ErroreAttesoOpenSPCoopLogCore err3 = new ErroreAttesoOpenSPCoopLogCore();
		err3.setIntervalloInferiore(dataInizioTest);
		err3.setIntervalloSuperiore(dataFineTest);
		err3.setMsgErrore("Unexpected close tag </soapenv:Envelope>; expected </soapenv:Body>");
		this.erroriAttesiOpenSPCoopCore.add(err3);
		
		ErroreAttesoOpenSPCoopLogCore err4 = new ErroreAttesoOpenSPCoopLogCore();
        err4.setIntervalloInferiore(dataInizioTest);
        err4.setIntervalloSuperiore(dataFineTest);
        err4.setMsgErrore("The element type \"soapenv:Body\" must be terminated by the matching end-tag \"</soapenv:Body>\"");
        this.erroriAttesiOpenSPCoopCore.add(err4);
	}
	@Test(groups={SOAPMessageScorretti.ID_GRUPPO,SOAPMessageScorretti.ID_GRUPPO+".STRUTTURA_XML_ERRATA_PA_STATEFUL"})
	public void strutturaXMLErrata_PA_Stateful_BodyFirstChild()throws FatalTestSuiteException,SOAPException, Exception{
		
		Date dataInizioTest = DateManager.getDate();
		
		// costruzione busta
		String busta = new String(getBusta("stateful"));
		busta = busta.replace("</helloworld>", "<helloworld>"); // imposto struttura xml errata
		java.io.ByteArrayInputStream bin =
			new java.io.ByteArrayInputStream(busta.getBytes());
		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();

		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryStrutturaXMLErrataPAStateful);
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
				throw new Exception("Attesa eccezione per controllo soapAction");
			} catch (AxisFault error) {
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				Reporter.log("Controllo fault code ["+SPCoopCostanti.FAULT_CODE_VALIDAZIONE_SPCOOP+"]");
				Assert.assertTrue(SPCoopCostanti.FAULT_CODE_VALIDAZIONE_SPCOOP.equals(error.getFaultCode().getLocalPart()) ||
						SPCoopCostanti.FAULT_CODE_VALIDAZIONE_SPCOOP.equals("soap:"+error.getFaultCode().getLocalPart()) ||
						SPCoopCostanti.FAULT_CODE_VALIDAZIONE_SPCOOP.equals("soapenv:"+error.getFaultCode().getLocalPart()));
				String msgErrore = SPCoopCostanti.FAULT_STRING_VALIDAZIONE_SPCOOP;
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
		err2.setMsgErrore("Unexpected close tag </soapenv:Envelope>; expected </helloworld>");
		this.erroriAttesiOpenSPCoopCore.add(err2);
		
		ErroreAttesoOpenSPCoopLogCore err3 = new ErroreAttesoOpenSPCoopLogCore();
		err3.setIntervalloInferiore(dataInizioTest);
		err3.setIntervalloSuperiore(dataFineTest);
		err3.setMsgErrore("Unexpected close tag </soapenv:Body>; expected </helloworld>");
		this.erroriAttesiOpenSPCoopCore.add(err3);
		
		ErroreAttesoOpenSPCoopLogCore err4 = new ErroreAttesoOpenSPCoopLogCore();
        err4.setIntervalloInferiore(dataInizioTest);
        err4.setIntervalloSuperiore(dataFineTest);
        err4.setMsgErrore("The element type \"helloworld\" must be terminated by the matching end-tag \"</helloworld>\"");
        this.erroriAttesiOpenSPCoopCore.add(err4);
	}
	
	@Test(groups={SOAPMessageScorretti.ID_GRUPPO,SOAPMessageScorretti.ID_GRUPPO+".STRUTTURA_XML_ERRATA_PA_STATEFUL"})
	public void strutturaXMLErrata_PA_Stateful_InsideBody()throws FatalTestSuiteException,SOAPException, Exception{
		
		Date dataInizioTest = DateManager.getDate();
		
		// costruzione busta
		String busta = new String(getBusta("stateful"));
		busta = busta.replace("</b>", "<b>"); // imposto struttura xml errata
		java.io.ByteArrayInputStream bin =
			new java.io.ByteArrayInputStream(busta.getBytes());
		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();

		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryStrutturaXMLErrataPAStateful);
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
				throw new Exception("Attesa eccezione per controllo soapAction");
			} catch (AxisFault error) {
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				Reporter.log("Controllo fault code ["+SPCoopCostanti.FAULT_CODE_VALIDAZIONE_SPCOOP+"]");
				Assert.assertTrue(SPCoopCostanti.FAULT_CODE_VALIDAZIONE_SPCOOP.equals(error.getFaultCode().getLocalPart()) ||
						SPCoopCostanti.FAULT_CODE_VALIDAZIONE_SPCOOP.equals("soap:"+error.getFaultCode().getLocalPart()) ||
						SPCoopCostanti.FAULT_CODE_VALIDAZIONE_SPCOOP.equals("soapenv:"+error.getFaultCode().getLocalPart()));
				String msgErrore = SPCoopCostanti.FAULT_STRING_VALIDAZIONE_SPCOOP;
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
		err2.setMsgErrore("Unexpected close tag </soapenv:Body>; expected </b>");
		this.erroriAttesiOpenSPCoopCore.add(err2);
		
		ErroreAttesoOpenSPCoopLogCore err3 = new ErroreAttesoOpenSPCoopLogCore();
		err3.setIntervalloInferiore(dataInizioTest);
		err3.setIntervalloSuperiore(dataFineTest);
		err3.setMsgErrore("Unexpected close tag </helloworld>; expected </b>");
		this.erroriAttesiOpenSPCoopCore.add(err3);
		
		ErroreAttesoOpenSPCoopLogCore err4 = new ErroreAttesoOpenSPCoopLogCore();
        err4.setIntervalloInferiore(dataInizioTest);
        err4.setIntervalloSuperiore(dataFineTest);
        err4.setMsgErrore("The element type \"b\" must be terminated by the matching end-tag \"</b>\"");
        this.erroriAttesiOpenSPCoopCore.add(err4);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * Header egov errato inviato alla porta applicativa
	 */
	Repository repositoryStrutturaXMLErrataPA_BustaErrata=new Repository();
	@Test(groups={SOAPMessageScorretti.ID_GRUPPO,SOAPMessageScorretti.ID_GRUPPO+".BUSTA_XML_ERRATA_PA"})
	public void strutturaXMLErrata_PA_BustaErrata()throws FatalTestSuiteException,SOAPException, Exception{
		
		Date dataInizioTest = DateManager.getDate();
		
		// costruzione busta
		String busta = new String(getBusta());
		busta = busta.replace("<eGov_IT:Mittente>", "<eGov_IT:Mittente"); // imposto struttura xml errata
		java.io.ByteArrayInputStream bin =
			new java.io.ByteArrayInputStream(busta.getBytes());
		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();

		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryStrutturaXMLErrataPA_BustaErrata);
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
				throw new Exception("Attesa eccezione per controllo soapAction");
			} catch (AxisFault error) {
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				Reporter.log("Controllo fault code ["+SPCoopCostanti.FAULT_CODE_VALIDAZIONE_SPCOOP+"]");
				Assert.assertTrue(SPCoopCostanti.FAULT_CODE_VALIDAZIONE_SPCOOP.equals(error.getFaultCode().getLocalPart()) ||
						SPCoopCostanti.FAULT_CODE_VALIDAZIONE_SPCOOP.equals("soap:"+error.getFaultCode().getLocalPart()) ||
						SPCoopCostanti.FAULT_CODE_VALIDAZIONE_SPCOOP.equals("soapenv:"+error.getFaultCode().getLocalPart()));
				String msgErrore = SPCoopCostanti.FAULT_STRING_VALIDAZIONE_SPCOOP;
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
		err2.setMsgErrore("org.xml.sax.SAXParseException: Element type \"eGov_IT:Mittente\" must be followed by either attribute specifications, \">\" or \"/>\".");
		this.erroriAttesiOpenSPCoopCore.add(err2);
		
		ErroreAttesoOpenSPCoopLogCore err3 = new ErroreAttesoOpenSPCoopLogCore();
		err3.setIntervalloInferiore(dataInizioTest);
		err3.setIntervalloSuperiore(dataFineTest);
		err3.setMsgErrore("Unexpected character '<' (code 60) excepted space, or '>' or \"/>\"");
		this.erroriAttesiOpenSPCoopCore.add(err3);
		
		ErroreAttesoOpenSPCoopLogCore err4 = new ErroreAttesoOpenSPCoopLogCore();
		err4.setIntervalloInferiore(dataInizioTest);
		err4.setIntervalloSuperiore(dataFineTest);
		err4.setMsgErrore("Element type \"eGov_IT:Mittente\" must be followed by either attribute specifications, \">\" or \"/>\"");
		this.erroriAttesiOpenSPCoopCore.add(err4);
	}
	
	/**
	 * Header egov errato inviato alla porta applicativa
	 */
	Repository repositoryStrutturaXMLErrataPA_Stateful_BustaErrata=new Repository();
	@Test(groups={SOAPMessageScorretti.ID_GRUPPO,SOAPMessageScorretti.ID_GRUPPO+".BUSTA_XML_ERRATA_PA_STATEFUL"})
	public void strutturaXMLErrata_PA_Stateful_BustaErrata()throws FatalTestSuiteException,SOAPException, Exception{
		
		Date dataInizioTest = DateManager.getDate();
		
		// costruzione busta
		String busta = new String(getBusta("stateful"));
		busta = busta.replace("<eGov_IT:Mittente>", "<eGov_IT:Mittente"); // imposto struttura xml errata
		java.io.ByteArrayInputStream bin =
			new java.io.ByteArrayInputStream(busta.getBytes());
		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();

		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryStrutturaXMLErrataPA_Stateful_BustaErrata);
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
				throw new Exception("Attesa eccezione per controllo soapAction");
			} catch (AxisFault error) {
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				Reporter.log("Controllo fault code ["+SPCoopCostanti.FAULT_CODE_VALIDAZIONE_SPCOOP+"]");
				Assert.assertTrue(SPCoopCostanti.FAULT_CODE_VALIDAZIONE_SPCOOP.equals(error.getFaultCode().getLocalPart()) ||
						SPCoopCostanti.FAULT_CODE_VALIDAZIONE_SPCOOP.equals("soap:"+error.getFaultCode().getLocalPart()) ||
						SPCoopCostanti.FAULT_CODE_VALIDAZIONE_SPCOOP.equals("soapenv:"+error.getFaultCode().getLocalPart()));
				String msgErrore = SPCoopCostanti.FAULT_STRING_VALIDAZIONE_SPCOOP;
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
		err2.setMsgErrore("org.xml.sax.SAXParseException: Element type \"eGov_IT:Mittente\" must be followed by either attribute specifications, \">\" or \"/>\".");
		this.erroriAttesiOpenSPCoopCore.add(err2);
		
		ErroreAttesoOpenSPCoopLogCore err3 = new ErroreAttesoOpenSPCoopLogCore();
		err3.setIntervalloInferiore(dataInizioTest);
		err3.setIntervalloSuperiore(dataFineTest);
		err3.setMsgErrore("Unexpected character '<' (code 60) excepted space, or '>' or \"/>\"");
		this.erroriAttesiOpenSPCoopCore.add(err3);
		
		ErroreAttesoOpenSPCoopLogCore err4 = new ErroreAttesoOpenSPCoopLogCore();
		err4.setIntervalloInferiore(dataInizioTest);
		err4.setIntervalloSuperiore(dataFineTest);
		err4.setMsgErrore("Element type \"eGov_IT:Mittente\" must be followed by either attribute specifications, \">\" or \"/>\"");
		this.erroriAttesiOpenSPCoopCore.add(err4);
	}
	
	/**
	 * Messaggio applicativo di risposta ottenuto dal servizio applicativo malformato
	 */
	Repository repositoryStrutturaXMLErratoPA_RispostaApplicativa=new Repository();
	
	
	@Test(groups={SOAPMessageScorretti.ID_GRUPPO,SOAPMessageScorretti.ID_GRUPPO+".RISPOSTA_APPLICATIVA_XML_ERRATO_PA"})
	public void strutturaXMLErrata_PA_RispostaApplicativa_Body()throws FatalTestSuiteException,SOAPException, Exception{
		Date dataInizioTest = DateManager.getDate();
		// costruzione busta
		String busta = new String(getBusta(CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_XML_MALFORMATO_BODY_BODY));
		java.io.ByteArrayInputStream bin =
			new java.io.ByteArrayInputStream(busta.getBytes());
		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();

		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryStrutturaXMLErratoPA_RispostaApplicativa);
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
				throw new Exception("Attesa eccezione per controllo soapAction");
			} catch (AxisFault error) {
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				Assert.assertTrue(SPCoopCostanti.FAULT_CODE_PROCESSAMENTO_SPCOOP.equals(error.getFaultCode().getLocalPart()) ||
						SPCoopCostanti.FAULT_CODE_PROCESSAMENTO_SPCOOP.equals("soap:"+error.getFaultCode().getLocalPart()) ||
						SPCoopCostanti.FAULT_CODE_PROCESSAMENTO_SPCOOP.equals("soapenv:"+error.getFaultCode().getLocalPart()));
				String msgErrore = SPCoopCostanti.FAULT_STRING_PROCESSAMENTO_SPCOOP;
				Reporter.log("Controllo fault string ["+msgErrore+"]");
				Assert.assertTrue(msgErrore.equals(error.getFaultString()));
				
				List<org.openspcoop2.protocol.spcoop.testsuite.core.OpenSPCoopDetail> eccezioni = 
					new ArrayList<org.openspcoop2.protocol.spcoop.testsuite.core.OpenSPCoopDetail>();
				org.openspcoop2.protocol.spcoop.testsuite.core.OpenSPCoopDetail ecc = new org.openspcoop2.protocol.spcoop.testsuite.core.OpenSPCoopDetail();
				ecc.setCodice("EGOV_IT_300");
				ecc.setDescrizione(CostantiErroriIntegrazione.MSG_516_SERVIZIO_APPLICATIVO_NON_DISPONIBILE);
				ecc.setCheckDescrizioneTramiteMatchEsatto(true);
				eccezioni.add(ecc);
				
				org.openspcoop2.protocol.spcoop.testsuite.core.OpenSPCoopDetail ecc2 = new org.openspcoop2.protocol.spcoop.testsuite.core.OpenSPCoopDetail();
				ecc2.setCodice("EGOV_IT_300");
				ecc2.setDescrizione(SPCoopCostanti.FAULT_STRING_PROCESSAMENTO_SPCOOP_SENZA_CODICE);
				ecc2.setCheckDescrizioneTramiteMatchEsatto(false);
				eccezioni.add(ecc2);
				
				Assert.assertTrue(Utilities.existsOpenSPCoopDetails(error)); 
				String[] identificativiFunzione = new String[2];
				identificativiFunzione[0] = "MinisteroErogatoreSPCoopIT";
				identificativiFunzione[1] = "ConsegnaContenutiApplicativi";
				Utilities.verificaFaultOpenSPCoopDetail(error, 
						CostantiTestSuite.SPCOOP_SOGGETTO_EROGATORE,TipoPdD.APPLICATIVA,identificativiFunzione, 
						eccezioni, new ArrayList<org.openspcoop2.protocol.spcoop.testsuite.core.OpenSPCoopDetail>(), false);
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
		err.setMsgErrore("Errore avvenuto durante la consegna HTTP (Costruzione messaggio SOAP)");
		this.erroriAttesiOpenSPCoopCore.add(err);
		
		ErroreAttesoOpenSPCoopLogCore err2 = new ErroreAttesoOpenSPCoopLogCore();
		err2.setIntervalloInferiore(dataInizioTest);
		err2.setIntervalloSuperiore(dataFineTest);
		err2.setMsgErrore("Registrazione messaggio di risposta nel RepositoryMessaggi non riuscita.");
		this.erroriAttesiOpenSPCoopCore.add(err2);
		
	}
	
	@Test(groups={SOAPMessageScorretti.ID_GRUPPO,SOAPMessageScorretti.ID_GRUPPO+".RISPOSTA_APPLICATIVA_XML_ERRATO_PA"})
	public void strutturaXMLErrata_PA_RispostaApplicativa_BodyFirstChild()throws FatalTestSuiteException,SOAPException, Exception{
		Date dataInizioTest = DateManager.getDate();
		// costruzione busta
		String busta = new String(getBusta(CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_XML_MALFORMATO_BODY_BODYFIRSTCHILD));
		java.io.ByteArrayInputStream bin =
			new java.io.ByteArrayInputStream(busta.getBytes());
		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();

		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryStrutturaXMLErratoPA_RispostaApplicativa);
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
				throw new Exception("Attesa eccezione per controllo soapAction");
			} catch (AxisFault error) {
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				Assert.assertTrue(SPCoopCostanti.FAULT_CODE_PROCESSAMENTO_SPCOOP.equals(error.getFaultCode().getLocalPart()) ||
						SPCoopCostanti.FAULT_CODE_PROCESSAMENTO_SPCOOP.equals("soap:"+error.getFaultCode().getLocalPart()) ||
						SPCoopCostanti.FAULT_CODE_PROCESSAMENTO_SPCOOP.equals("soapenv:"+error.getFaultCode().getLocalPart()));
				String msgErrore = SPCoopCostanti.FAULT_STRING_PROCESSAMENTO_SPCOOP;
				Reporter.log("Controllo fault string ["+msgErrore+"]");
				Assert.assertTrue(msgErrore.equals(error.getFaultString()));
				
				List<org.openspcoop2.protocol.spcoop.testsuite.core.OpenSPCoopDetail> eccezioni = 
					new ArrayList<org.openspcoop2.protocol.spcoop.testsuite.core.OpenSPCoopDetail>();
				org.openspcoop2.protocol.spcoop.testsuite.core.OpenSPCoopDetail ecc = new org.openspcoop2.protocol.spcoop.testsuite.core.OpenSPCoopDetail();
				ecc.setCodice("EGOV_IT_300");
				ecc.setDescrizione(CostantiErroriIntegrazione.MSG_516_SERVIZIO_APPLICATIVO_NON_DISPONIBILE);
				ecc.setCheckDescrizioneTramiteMatchEsatto(true);
				eccezioni.add(ecc);
				
				org.openspcoop2.protocol.spcoop.testsuite.core.OpenSPCoopDetail ecc2 = new org.openspcoop2.protocol.spcoop.testsuite.core.OpenSPCoopDetail();
				ecc2.setCodice("EGOV_IT_300");
				ecc2.setDescrizione(SPCoopCostanti.FAULT_STRING_PROCESSAMENTO_SPCOOP_SENZA_CODICE);
				ecc2.setCheckDescrizioneTramiteMatchEsatto(false);
				eccezioni.add(ecc2);
				
				Assert.assertTrue(Utilities.existsOpenSPCoopDetails(error));
				String[] identificativiFunzione = new String[2];
				identificativiFunzione[0] = "MinisteroErogatoreSPCoopIT";
				identificativiFunzione[1] = "ConsegnaContenutiApplicativi";
				Utilities.verificaFaultOpenSPCoopDetail(error, 
						CostantiTestSuite.SPCOOP_SOGGETTO_EROGATORE,TipoPdD.APPLICATIVA,identificativiFunzione, 
						eccezioni, new ArrayList<org.openspcoop2.protocol.spcoop.testsuite.core.OpenSPCoopDetail>(), false);
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
		err.setMsgErrore("Errore avvenuto durante la consegna HTTP (Costruzione messaggio SOAP)");
		this.erroriAttesiOpenSPCoopCore.add(err);
		
		ErroreAttesoOpenSPCoopLogCore err2 = new ErroreAttesoOpenSPCoopLogCore();
		err2.setIntervalloInferiore(dataInizioTest);
		err2.setIntervalloSuperiore(dataFineTest);
		err2.setMsgErrore("Registrazione messaggio di risposta nel RepositoryMessaggi non riuscita.");
		this.erroriAttesiOpenSPCoopCore.add(err2);
	}

	@Test(groups={SOAPMessageScorretti.ID_GRUPPO,SOAPMessageScorretti.ID_GRUPPO+".RISPOSTA_APPLICATIVA_XML_ERRATO_PA"})
	public void strutturaXMLErrata_PA_RispostaApplicativa_InsideBody()throws FatalTestSuiteException,SOAPException, Exception{
		Date dataInizioTest = DateManager.getDate();
		// costruzione busta
		String busta = new String(getBusta(CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_XML_MALFORMATO_BODY_INSIDEBODY));
		java.io.ByteArrayInputStream bin =
			new java.io.ByteArrayInputStream(busta.getBytes());
		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();

		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryStrutturaXMLErratoPA_RispostaApplicativa);
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
				throw new Exception("Attesa eccezione per controllo soapAction");
			} catch (AxisFault error) {
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				Assert.assertTrue(SPCoopCostanti.FAULT_CODE_PROCESSAMENTO_SPCOOP.equals(error.getFaultCode().getLocalPart()) ||
						SPCoopCostanti.FAULT_CODE_PROCESSAMENTO_SPCOOP.equals("soap:"+error.getFaultCode().getLocalPart()) ||
						SPCoopCostanti.FAULT_CODE_PROCESSAMENTO_SPCOOP.equals("soapenv:"+error.getFaultCode().getLocalPart()));
				String msgErrore = SPCoopCostanti.FAULT_STRING_PROCESSAMENTO_SPCOOP;
				Reporter.log("Controllo fault string ["+msgErrore+"]");
				Assert.assertTrue(msgErrore.equals(error.getFaultString()));
				
				List<org.openspcoop2.protocol.spcoop.testsuite.core.OpenSPCoopDetail> eccezioni = 
					new ArrayList<org.openspcoop2.protocol.spcoop.testsuite.core.OpenSPCoopDetail>();
				org.openspcoop2.protocol.spcoop.testsuite.core.OpenSPCoopDetail ecc = new org.openspcoop2.protocol.spcoop.testsuite.core.OpenSPCoopDetail();
				ecc.setCodice("EGOV_IT_300");
				ecc.setDescrizione(CostantiErroriIntegrazione.MSG_516_SERVIZIO_APPLICATIVO_NON_DISPONIBILE);
				ecc.setCheckDescrizioneTramiteMatchEsatto(true);
				eccezioni.add(ecc);
				
				org.openspcoop2.protocol.spcoop.testsuite.core.OpenSPCoopDetail ecc2 = new org.openspcoop2.protocol.spcoop.testsuite.core.OpenSPCoopDetail();
				ecc2.setCodice("EGOV_IT_300");
				ecc2.setDescrizione(SPCoopCostanti.FAULT_STRING_PROCESSAMENTO_SPCOOP_SENZA_CODICE);
				ecc2.setCheckDescrizioneTramiteMatchEsatto(false);
				eccezioni.add(ecc2);
				
				Assert.assertTrue(Utilities.existsOpenSPCoopDetails(error)); 
				String[] identificativiFunzione = new String[2];
				identificativiFunzione[0] = "MinisteroErogatoreSPCoopIT";
				identificativiFunzione[1] = "ConsegnaContenutiApplicativi";
				Utilities.verificaFaultOpenSPCoopDetail(error, 
						CostantiTestSuite.SPCOOP_SOGGETTO_EROGATORE,TipoPdD.APPLICATIVA,identificativiFunzione, 
						eccezioni, new ArrayList<org.openspcoop2.protocol.spcoop.testsuite.core.OpenSPCoopDetail>(), false);
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
		err.setMsgErrore("Errore avvenuto durante la consegna HTTP (Costruzione messaggio SOAP)");
		this.erroriAttesiOpenSPCoopCore.add(err);
		
		ErroreAttesoOpenSPCoopLogCore err2 = new ErroreAttesoOpenSPCoopLogCore();
		err2.setIntervalloInferiore(dataInizioTest);
		err2.setIntervalloSuperiore(dataFineTest);
		err2.setMsgErrore("Registrazione messaggio di risposta nel RepositoryMessaggi non riuscita.");
		this.erroriAttesiOpenSPCoopCore.add(err2);
	}
	
	
	
	
	/**
	 * Messaggio applicativo di risposta ottenuto dal servizio applicativo malformato in modalita stateful
	 */
	Repository repositoryStrutturaXMLErratoPA_Stateful_RispostaApplicativa=new Repository();
	
	@Test(groups={SOAPMessageScorretti.ID_GRUPPO,SOAPMessageScorretti.ID_GRUPPO+".RISPOSTA_APPLICATIVA_XML_ERRATO_PA_STATEFUL"})
	public void strutturaXMLErrata_PA_RispostaApplicativa_Stateful_Body()throws FatalTestSuiteException,SOAPException, Exception{
	
		Date dataInizioTest = DateManager.getDate();	
		// costruzione busta
		String busta = new String(getBusta(CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_XML_MALFORMATO_BODY_BODY_STATEFUL));
		java.io.ByteArrayInputStream bin =
			new java.io.ByteArrayInputStream(busta.getBytes());
		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();

		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryStrutturaXMLErratoPA_Stateful_RispostaApplicativa);
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
				throw new Exception("Attesa eccezione per controllo soapAction");
			} catch (AxisFault error) {
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				Assert.assertTrue(SPCoopCostanti.FAULT_CODE_PROCESSAMENTO_SPCOOP.equals(error.getFaultCode().getLocalPart()) ||
						SPCoopCostanti.FAULT_CODE_PROCESSAMENTO_SPCOOP.equals("soap:"+error.getFaultCode().getLocalPart()) ||
						SPCoopCostanti.FAULT_CODE_PROCESSAMENTO_SPCOOP.equals("soapenv:"+error.getFaultCode().getLocalPart()));
				String msgErrore = SPCoopCostanti.FAULT_STRING_PROCESSAMENTO_SPCOOP;
				Reporter.log("Controllo fault string ["+msgErrore+"]");
				Assert.assertTrue(msgErrore.equals(error.getFaultString()));
				
				List<org.openspcoop2.protocol.spcoop.testsuite.core.OpenSPCoopDetail> eccezioni = 
					new ArrayList<org.openspcoop2.protocol.spcoop.testsuite.core.OpenSPCoopDetail>();
				org.openspcoop2.protocol.spcoop.testsuite.core.OpenSPCoopDetail ecc = new org.openspcoop2.protocol.spcoop.testsuite.core.OpenSPCoopDetail();
				ecc.setCodice("EGOV_IT_300");
				ecc.setDescrizione(SPCoopCostanti.FAULT_STRING_PROCESSAMENTO_SPCOOP_SENZA_CODICE);
				ecc.setCheckDescrizioneTramiteMatchEsatto(false);
				eccezioni.add(ecc);
				
				org.openspcoop2.protocol.spcoop.testsuite.core.OpenSPCoopDetail ecc2 = new org.openspcoop2.protocol.spcoop.testsuite.core.OpenSPCoopDetail();
				ecc2.setCodice("EGOV_IT_300");
				ecc2.setDescrizione(CostantiErroriIntegrazione.MSG_516_SERVIZIO_APPLICATIVO_NON_DISPONIBILE);
				ecc2.setCheckDescrizioneTramiteMatchEsatto(false);
				eccezioni.add(ecc2);
				
				Assert.assertTrue(Utilities.existsOpenSPCoopDetails(error)); 
				String identificativiFunzione = "ConsegnaContenutiApplicativi";
				Utilities.verificaFaultOpenSPCoopDetail(error, 
						CostantiTestSuite.SPCOOP_SOGGETTO_EROGATORE,TipoPdD.APPLICATIVA,identificativiFunzione, 
						eccezioni, new ArrayList<org.openspcoop2.protocol.spcoop.testsuite.core.OpenSPCoopDetail>(), false);
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
        err.setMsgErrore("was expecting a close tag for element <soapenv:Body>");
        this.erroriAttesiOpenSPCoopCore.add(err);

		
		ErroreAttesoOpenSPCoopLogCore err2 = new ErroreAttesoOpenSPCoopLogCore();
		err2.setIntervalloInferiore(dataInizioTest);
		err2.setIntervalloSuperiore(dataFineTest);
		err2.setMsgErrore("Errore avvenuto durante la consegna HTTP (Costruzione messaggio SOAP)");
		this.erroriAttesiOpenSPCoopCore.add(err2);
		
		ErroreAttesoOpenSPCoopLogCore err3 = new ErroreAttesoOpenSPCoopLogCore();
		err3.setIntervalloInferiore(dataInizioTest);
		err3.setIntervalloSuperiore(dataFineTest);
		err3.setMsgErrore("Registrazione messaggio di risposta nel RepositoryMessaggi non riuscita.");
		this.erroriAttesiOpenSPCoopCore.add(err3);
	}
	
	@Test(groups={SOAPMessageScorretti.ID_GRUPPO,SOAPMessageScorretti.ID_GRUPPO+".RISPOSTA_APPLICATIVA_XML_ERRATO_PA_STATEFUL"})
	public void strutturaXMLErrata_PA_RispostaApplicativa_Stateful_BodyFirstChild()throws FatalTestSuiteException,SOAPException, Exception{
		Date dataInizioTest = DateManager.getDate();	
		// costruzione busta
		String busta = new String(getBusta(CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_XML_MALFORMATO_BODY_BODYFIRSTCHILD_STATEFUL));
		java.io.ByteArrayInputStream bin =
			new java.io.ByteArrayInputStream(busta.getBytes());
		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();

		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryStrutturaXMLErratoPA_Stateful_RispostaApplicativa);
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
				throw new Exception("Attesa eccezione per controllo soapAction");
			} catch (AxisFault error) {
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				Assert.assertTrue(SPCoopCostanti.FAULT_CODE_PROCESSAMENTO_SPCOOP.equals(error.getFaultCode().getLocalPart()) ||
						SPCoopCostanti.FAULT_CODE_PROCESSAMENTO_SPCOOP.equals("soap:"+error.getFaultCode().getLocalPart()) ||
						SPCoopCostanti.FAULT_CODE_PROCESSAMENTO_SPCOOP.equals("soapenv:"+error.getFaultCode().getLocalPart()));
				String msgErrore = SPCoopCostanti.FAULT_STRING_PROCESSAMENTO_SPCOOP;
				Reporter.log("Controllo fault string ["+msgErrore+"]");
				Assert.assertTrue(msgErrore.equals(error.getFaultString()));
				
				List<org.openspcoop2.protocol.spcoop.testsuite.core.OpenSPCoopDetail> eccezioni = 
					new ArrayList<org.openspcoop2.protocol.spcoop.testsuite.core.OpenSPCoopDetail>();
				org.openspcoop2.protocol.spcoop.testsuite.core.OpenSPCoopDetail ecc = new org.openspcoop2.protocol.spcoop.testsuite.core.OpenSPCoopDetail();
				ecc.setCodice("EGOV_IT_300");
				ecc.setDescrizione(SPCoopCostanti.FAULT_STRING_PROCESSAMENTO_SPCOOP_SENZA_CODICE);
				ecc.setCheckDescrizioneTramiteMatchEsatto(false);
				eccezioni.add(ecc);
				
				org.openspcoop2.protocol.spcoop.testsuite.core.OpenSPCoopDetail ecc2 = new org.openspcoop2.protocol.spcoop.testsuite.core.OpenSPCoopDetail();
				ecc2.setCodice("EGOV_IT_300");
				ecc2.setDescrizione(CostantiErroriIntegrazione.MSG_516_SERVIZIO_APPLICATIVO_NON_DISPONIBILE);
				ecc2.setCheckDescrizioneTramiteMatchEsatto(false);
				eccezioni.add(ecc2);
				
				Assert.assertTrue(Utilities.existsOpenSPCoopDetails(error));
				String identificativiFunzione = "ConsegnaContenutiApplicativi";
				Utilities.verificaFaultOpenSPCoopDetail(error, 
						CostantiTestSuite.SPCOOP_SOGGETTO_EROGATORE,TipoPdD.APPLICATIVA,identificativiFunzione, 
						eccezioni, new ArrayList<org.openspcoop2.protocol.spcoop.testsuite.core.OpenSPCoopDetail>(), false);
			}finally{
				dbComponentErogatore.close();
			}
			
		}catch(Exception e){
			throw e;
		}finally{
			dbComponentErogatore.close();
		}

        Date dataFineTest = DateManager.getDate();

		ErroreAttesoOpenSPCoopLogCore err2 = new ErroreAttesoOpenSPCoopLogCore();
		err2.setIntervalloInferiore(dataInizioTest);
		err2.setIntervalloSuperiore(dataFineTest);
		err2.setMsgErrore("Errore avvenuto durante la consegna HTTP (Costruzione messaggio SOAP)");
		this.erroriAttesiOpenSPCoopCore.add(err2);
		
		ErroreAttesoOpenSPCoopLogCore err3 = new ErroreAttesoOpenSPCoopLogCore();
		err3.setIntervalloInferiore(dataInizioTest);
		err3.setIntervalloSuperiore(dataFineTest);
		err3.setMsgErrore("Registrazione messaggio di risposta nel RepositoryMessaggi non riuscita.");
		this.erroriAttesiOpenSPCoopCore.add(err3);
	}

	@Test(groups={SOAPMessageScorretti.ID_GRUPPO,SOAPMessageScorretti.ID_GRUPPO+".RISPOSTA_APPLICATIVA_XML_ERRATO_PA_STATEFUL"})
	public void strutturaXMLErrata_PA_RispostaApplicativa_Stateful_InsideBody()throws FatalTestSuiteException,SOAPException, Exception{
                Date dataInizioTest = DateManager.getDate();
	
		// costruzion
		String busta = new String(getBusta(CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_XML_MALFORMATO_BODY_INSIDEBODY_STATEFUL));
		java.io.ByteArrayInputStream bin =
			new java.io.ByteArrayInputStream(busta.getBytes());
		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();

		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryStrutturaXMLErratoPA_Stateful_RispostaApplicativa);
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
				throw new Exception("Attesa eccezione per controllo soapAction");
			} catch (AxisFault error) {
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				Assert.assertTrue(SPCoopCostanti.FAULT_CODE_PROCESSAMENTO_SPCOOP.equals(error.getFaultCode().getLocalPart()) ||
						SPCoopCostanti.FAULT_CODE_PROCESSAMENTO_SPCOOP.equals("soap:"+error.getFaultCode().getLocalPart()) ||
						SPCoopCostanti.FAULT_CODE_PROCESSAMENTO_SPCOOP.equals("soapenv:"+error.getFaultCode().getLocalPart()));
				String msgErrore = SPCoopCostanti.FAULT_STRING_PROCESSAMENTO_SPCOOP;
				Reporter.log("Controllo fault string ["+msgErrore+"]");
				Assert.assertTrue(msgErrore.equals(error.getFaultString()));
				
				List<org.openspcoop2.protocol.spcoop.testsuite.core.OpenSPCoopDetail> eccezioni = 
					new ArrayList<org.openspcoop2.protocol.spcoop.testsuite.core.OpenSPCoopDetail>();
				org.openspcoop2.protocol.spcoop.testsuite.core.OpenSPCoopDetail ecc = new org.openspcoop2.protocol.spcoop.testsuite.core.OpenSPCoopDetail();
				ecc.setCodice("EGOV_IT_300");
				ecc.setDescrizione(SPCoopCostanti.FAULT_STRING_PROCESSAMENTO_SPCOOP_SENZA_CODICE);
				ecc.setCheckDescrizioneTramiteMatchEsatto(false);
				eccezioni.add(ecc);
				
				org.openspcoop2.protocol.spcoop.testsuite.core.OpenSPCoopDetail ecc2 = new org.openspcoop2.protocol.spcoop.testsuite.core.OpenSPCoopDetail();
				ecc2.setCodice("EGOV_IT_300");
				ecc2.setDescrizione(CostantiErroriIntegrazione.MSG_516_SERVIZIO_APPLICATIVO_NON_DISPONIBILE);
				ecc2.setCheckDescrizioneTramiteMatchEsatto(false);
				eccezioni.add(ecc2);
				
				Assert.assertTrue(Utilities.existsOpenSPCoopDetails(error)); 
				String identificativiFunzione = "ConsegnaContenutiApplicativi";
				Utilities.verificaFaultOpenSPCoopDetail(error, 
						CostantiTestSuite.SPCOOP_SOGGETTO_EROGATORE,TipoPdD.APPLICATIVA,identificativiFunzione, 
						eccezioni, new ArrayList<org.openspcoop2.protocol.spcoop.testsuite.core.OpenSPCoopDetail>(), false);
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
        err.setMsgErrore("Unexpected close tag </helloworld>; expected </b>");
        this.erroriAttesiOpenSPCoopCore.add(err);

		ErroreAttesoOpenSPCoopLogCore err2 = new ErroreAttesoOpenSPCoopLogCore();
		err2.setIntervalloInferiore(dataInizioTest);
		err2.setIntervalloSuperiore(dataFineTest);
		err2.setMsgErrore("Errore avvenuto durante la consegna HTTP (Costruzione messaggio SOAP)");
		this.erroriAttesiOpenSPCoopCore.add(err2);
		
		ErroreAttesoOpenSPCoopLogCore err3 = new ErroreAttesoOpenSPCoopLogCore();
		err3.setIntervalloInferiore(dataInizioTest);
		err3.setIntervalloSuperiore(dataFineTest);
		err3.setMsgErrore("Registrazione messaggio di risposta nel RepositoryMessaggi non riuscita.");
		this.erroriAttesiOpenSPCoopCore.add(err3);
	}
	
	
	/**
	 * Body errato ritornato dalla PdD Destinataria
	 */
	Repository repositoryStrutturaXMLBodyRispostaPdDErrato=new Repository();
	@Test(groups={SOAPMessageScorretti.ID_GRUPPO,SOAPMessageScorretti.ID_GRUPPO+".STRUTTURA_XML_ERRATA_PD_RISPOSTA_PDD"})
	public void strutturaXMLBodyRispostaPdDErrato_PD()throws FatalTestSuiteException,SOAPException, Exception{
		Date dataInizioTest = DateManager.getDate();
	
		// costruzione busta
		String xml = FileSystemUtilities.readFile(Utilities.testSuiteProperties.getSoap11FileName());
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
				if(Utilities.toString(CodiceErroreIntegrazione.CODICE_516_CONNETTORE_UTILIZZO_CON_ERRORE).equals(error.getFaultCode().getLocalPart())){
					Reporter.log("Controllo fault code. Atteso ["+Utilities.toString(CodiceErroreIntegrazione.CODICE_516_CONNETTORE_UTILIZZO_CON_ERRORE)+"] - Trovato [" + error.getFaultCode().getLocalPart() + "]");
					Assert.assertTrue(Utilities.toString(CodiceErroreIntegrazione.CODICE_516_CONNETTORE_UTILIZZO_CON_ERRORE).equals(error.getFaultCode().getLocalPart()));
					String msgErrore = CostantiProtocollo.PDD_NON_DISPONIBILE.replace(CostantiProtocollo.KEYWORDPDD_NON_DISPONIBILE, 
							CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_EROGATORE+CostantiTestSuite.SPCOOP_NOME_SOGGETTO_EROGATORE);
					Reporter.log("Controllo fault string. Atteso ["+msgErrore+"] - Trovato [" + error.getFaultString() + "]");
					Assert.assertTrue(msgErrore.equals(error.getFaultString()));
				} else if(Utilities.toString(CodiceErroreIntegrazione.CODICE_432_MESSAGGIO_XML_MALFORMATO).equals(error.getFaultCode().getLocalPart())){
					Reporter.log("Controllo fault code. Atteso ["+Utilities.toString(CodiceErroreIntegrazione.CODICE_432_MESSAGGIO_XML_MALFORMATO)+"] - Trovato [" + error.getFaultCode().getLocalPart() + "]");
					Assert.assertTrue(Utilities.toString(CodiceErroreIntegrazione.CODICE_432_MESSAGGIO_XML_MALFORMATO).equals(error.getFaultCode().getLocalPart()));
					String msgErrore = "Unexpected close tag </soapenv:Envelope>; expected </soapenv:Body>";
					Reporter.log("Controllo fault string. Trovato [" + error.getFaultString() + "]");
					Assert.assertTrue(error.getFaultString().contains(msgErrore));
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
                err.setMsgErrore("Errore avvenuto durante la consegna HTTP (Costruzione messaggio SOAP)");
                this.erroriAttesiOpenSPCoopCore.add(err);

                ErroreAttesoOpenSPCoopLogCore err2 = new ErroreAttesoOpenSPCoopLogCore();
                err2.setIntervalloInferiore(dataInizioTest);
                err2.setIntervalloSuperiore(dataFineTest);
                err2.setMsgErrore("com.ctc.wstx.exc.WstxParsingException: Unexpected close tag </soapenv:Envelope>; expected </soapenv:Body>");
                this.erroriAttesiOpenSPCoopCore.add(err2);
                
		
	}
	
	/**
	 * Header eGov errato ritornato dalla PdD Destinataria
	 */
	Repository repositoryStrutturaXMLHeaderRispostaPdDErrato=new Repository();
	@Test(groups={SOAPMessageScorretti.ID_GRUPPO,SOAPMessageScorretti.ID_GRUPPO+".STRUTTURA_XML_ERRATA_PD_RISPOSTA_HEADER_PDD"})
	public void strutturaXMLHeaderRispostaPdDErrato_PD()throws FatalTestSuiteException,SOAPException, Exception{
		Date dataInizioTest = DateManager.getDate();
		
		// costruzione busta
		String xml = FileSystemUtilities.readFile(Utilities.testSuiteProperties.getSoap11FileName());
		Message msg=new Message(new ByteArrayInputStream(xml.getBytes()));
		msg.getSOAPPartAsBytes();

		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryStrutturaXMLHeaderRispostaPdDErrato);
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_PROFILO_SINCRONO_XML_ERRATO_HEADER_RISPOSTA_PDD);
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
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				if(Utilities.toString(CodiceErroreIntegrazione.CODICE_516_CONNETTORE_UTILIZZO_CON_ERRORE).equals(error.getFaultCode().getLocalPart())){
					Reporter.log("Controllo fault code ["+Utilities.toString(CodiceErroreIntegrazione.CODICE_516_CONNETTORE_UTILIZZO_CON_ERRORE)+"]");
					Assert.assertTrue(Utilities.toString(CodiceErroreIntegrazione.CODICE_516_CONNETTORE_UTILIZZO_CON_ERRORE).equals(error.getFaultCode().getLocalPart()));
					String msgErrore = CostantiProtocollo.PDD_NON_DISPONIBILE.replace(CostantiProtocollo.KEYWORDPDD_NON_DISPONIBILE, 
							CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_EROGATORE+CostantiTestSuite.SPCOOP_NOME_SOGGETTO_EROGATORE);
					Reporter.log("Controllo fault string ["+msgErrore+"]");
					Assert.assertTrue(msgErrore.equals(error.getFaultString()));
					Reporter.log("Controllo fault actor");
					Assert.assertTrue(CostantiPdD.OPENSPCOOP2.equals(error.getFaultActor()));
				} else if(Utilities.toString(CodiceErroreIntegrazione.CODICE_432_MESSAGGIO_XML_MALFORMATO).equals(error.getFaultCode().getLocalPart())){
					Reporter.log("Controllo fault code. Atteso ["+Utilities.toString(CodiceErroreIntegrazione.CODICE_432_MESSAGGIO_XML_MALFORMATO)+"] - Trovato [" + error.getFaultCode().getLocalPart() + "]");
					Assert.assertTrue(Utilities.toString(CodiceErroreIntegrazione.CODICE_432_MESSAGGIO_XML_MALFORMATO).equals(error.getFaultCode().getLocalPart()));
					String msgErrore = "Errore durante la lettura del messaggio di risposta";
					Reporter.log("Controllo fault string. Trovato [" + error.getFaultString() + "]");
					Assert.assertTrue(error.getFaultString().contains(msgErrore));
				} else {
					Assert.assertTrue(false, "FaultCode non atteso");
				}
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
        err.setMsgErrore("Errore avvenuto durante la consegna HTTP (Costruzione messaggio SOAP)");
        this.erroriAttesiOpenSPCoopCore.add(err);

        ErroreAttesoOpenSPCoopLogCore err2 = new ErroreAttesoOpenSPCoopLogCore();
        err2.setIntervalloInferiore(dataInizioTest);
        err2.setIntervalloSuperiore(dataFineTest);
		err2.setMsgErrore("Unexpected character '<' (code 60) excepted space, or '>' or \"/>\"");
        this.erroriAttesiOpenSPCoopCore.add(err2);
        
	}
}
