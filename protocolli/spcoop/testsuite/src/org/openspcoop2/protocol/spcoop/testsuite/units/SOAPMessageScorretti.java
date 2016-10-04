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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
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
import org.openspcoop2.pdd.services.axis14.IntegrationManagerException;
import org.openspcoop2.pdd.services.axis14.IntegrationManagerMessage;
import org.openspcoop2.protocol.sdk.constants.CodiceErroreIntegrazione;
import org.openspcoop2.protocol.spcoop.constants.SPCoopCostanti;
import org.openspcoop2.protocol.spcoop.testsuite.core.CostantiErroriIntegrazione;
import org.openspcoop2.protocol.spcoop.testsuite.core.CostantiTestSuite;
import org.openspcoop2.protocol.spcoop.testsuite.core.DatabaseProperties;
import org.openspcoop2.protocol.spcoop.testsuite.core.Utilities;
import org.openspcoop2.protocol.spcoop.testsuite.core.UtilitiesEGov;
import org.openspcoop2.testsuite.clients.ClientHttpGenerico;
import org.openspcoop2.testsuite.core.ErroreAttesoOpenSPCoopLogCore;
import org.openspcoop2.testsuite.core.TestSuiteException;
import org.openspcoop2.testsuite.core.Repository;
import org.openspcoop2.testsuite.db.DatabaseComponent;
import org.openspcoop2.testsuite.db.DatabaseMsgDiagnosticiComponent;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.resources.FileSystemUtilities;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.AfterGroups;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;


/**
 * Test sui profili di collaborazione implementati nella Porta di Dominio
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SOAPMessageScorretti {

	/**
	 * NOTA:
	 * Usare i seguenti grupi:
	 * SOAPMessageScorretti.RICHIESTA_ALTRI_DATI
	 * SOAPMessageScorretti.PD_XML_RICHIESTA
	 * SOAPMessageScorretti.PA_XML_RICHIESTA
	 * SOAPMessageScorretti.PD_XML_RISPOSTA
	 * SOAPMessageScorretti.PA_XML_RISPOSTA
	 * SOAPMessageScorretti.INTEGRATION_MANAGER
	 * SOAPMessageScorretti.PD2SOAP
	 **/
	
	/** Identificativo del gruppo */
	public static final String ID_GRUPPO = "SOAPMessageScorretti";

	private final static int READ_TIMEOUT = 20000;



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
		String identificativoEGov=UtilitiesEGov.getIDEGov(CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE,
				CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE+CostantiTestSuite.SPCOOP_PORTA_DOMINIO);
		String bustaSOAP=UtilitiesEGov.getBustaEGov(CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_FRUITORE,
				CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE,
				CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_EROGATORE,
				CostantiTestSuite.SPCOOP_NOME_SOGGETTO_EROGATORE,
				SPCoopCostanti.PROFILO_COLLABORAZIONE_SINCRONO,
				CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
				CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
				identificativoEGov,
				false,
				SPCoopCostanti.PROFILO_TRASMISSIONE_SENZA_DUPLICATI,
				false,
				azione,
				null,
				SPCoopCostanti.TIPO_TEMPO_SPC);
		return bustaSOAP.getBytes();
	}
	
	private String getHeaderBusta(String servizio,String azione){
		String identificativoEGov=UtilitiesEGov.getIDEGov(CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE,
				CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE+CostantiTestSuite.SPCOOP_PORTA_DOMINIO);
		String bustaSOAP=UtilitiesEGov.buildBustaEGov(CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_FRUITORE,
				CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE,
				CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_EROGATORE,
				CostantiTestSuite.SPCOOP_NOME_SOGGETTO_EROGATORE,
				SPCoopCostanti.PROFILO_COLLABORAZIONE_SINCRONO,
				CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
				servizio,
				identificativoEGov,
				false,
				SPCoopCostanti.PROFILO_TRASMISSIONE_SENZA_DUPLICATI,
				false,
				azione,
				null,
				SPCoopCostanti.TIPO_TEMPO_SPC,
				new Date());
		return bustaSOAP;
	}







	// ------------- CONTENT TYPE NON SUPPORTATO ------------------

	Repository repositoryContentTypeNonSupportatoPD=new Repository();
	@Test(groups={SOAPMessageScorretti.ID_GRUPPO,
			SOAPMessageScorretti.ID_GRUPPO+".RICHIESTA_ALTRI_DATI",
			SOAPMessageScorretti.ID_GRUPPO+".CONTENT_TYPE_NON_SUPPORTATO_PD"})
	public void contentTypeNonSupportato_PD()throws TestSuiteException,SOAPException, Exception{

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
	@Test(groups={SOAPMessageScorretti.ID_GRUPPO,
			SOAPMessageScorretti.ID_GRUPPO+".RICHIESTA_ALTRI_DATI",
			SOAPMessageScorretti.ID_GRUPPO+".CONTENT_TYPE_NON_SUPPORTATO_PA"})
	public void contentTypeNonSupportato_PA()throws TestSuiteException,SOAPException, Exception{

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
						Utilities.testSuiteProperties.getIdentitaDefault(),TipoPdD.APPLICATIVA,"RicezioneBusteSOAP", 
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
	@Test(groups={SOAPMessageScorretti.ID_GRUPPO,
			SOAPMessageScorretti.ID_GRUPPO+".RICHIESTA_ALTRI_DATI",
			SOAPMessageScorretti.ID_GRUPPO+".HEADER_NOT_UNDERSTAND_PD"})
	public void headerDontUnderstand_PD()throws TestSuiteException,SOAPException, Exception{

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
	@Test(groups={SOAPMessageScorretti.ID_GRUPPO,
			SOAPMessageScorretti.ID_GRUPPO+".RICHIESTA_ALTRI_DATI",
			SOAPMessageScorretti.ID_GRUPPO+".HEADER_NOT_UNDERSTAND_PA"})
	public void headerDontUnderstand_PA()throws TestSuiteException,SOAPException, Exception{

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
						Utilities.testSuiteProperties.getIdentitaDefault(),TipoPdD.APPLICATIVA,"RicezioneBusteSOAP", 
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
	@Test(groups={SOAPMessageScorretti.ID_GRUPPO,
			SOAPMessageScorretti.ID_GRUPPO+".RICHIESTA_ALTRI_DATI",
			SOAPMessageScorretti.ID_GRUPPO+".NAMESPACE_ERRATO_PD"})
	public void namespaceErrato_PD()throws TestSuiteException,SOAPException, Exception{

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
	@Test(groups={SOAPMessageScorretti.ID_GRUPPO,
			SOAPMessageScorretti.ID_GRUPPO+".RICHIESTA_ALTRI_DATI",
			SOAPMessageScorretti.ID_GRUPPO+".NAMESPACE_ERRATO_PA"})
	public void namespaceErrato_PA()throws TestSuiteException,SOAPException, Exception{

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
						Utilities.testSuiteProperties.getIdentitaDefault(),TipoPdD.APPLICATIVA,"RicezioneBusteSOAP", 
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

	private static final String TEMPLATE_HEADER = "<!-- EGOV -->";
	
	private String readFile(String fileName)throws Exception{
		String xml = FileSystemUtilities.readFile(fileName);
		xml = xml.replace("<soapenv:Header>", "<soapenv:Header>"+TEMPLATE_HEADER); // serve per eventuale replace con header eGov
		return xml;
	}
	
	@DataProvider (name="strutturaSoapErrata")
	public Object[][] strutturaSoapErrata() throws Exception{

		Object[][] params = new Object[14][4];
		//Object[][] params = new Object[1][4];
		int index = 0;


		{
			// Chiusura SOAPBody non Effettuata
			String xml = readFile(Utilities.testSuiteProperties.getSoap11FileName());
			xml = xml.replace("</soapenv:Body>", "<soapenv:Body>"); // imposto struttura xml errata
			params[index][0] = xml.getBytes();
			params[index][1] = "Chiusura SOAPBody non Effettuata";
			List<String> motivoErroreParser = new ArrayList<String>();
			motivoErroreParser.add("Unexpected close tag </soapenv:Envelope>; expected </soapenv:Body>");
			motivoErroreParser.add("The element type \"soapenv:Body\" must be terminated by the matching end-tag \"</soapenv:Body>\"");
			params[index][2] = motivoErroreParser;
			List<String> listErroriAttesi = new ArrayList<String>();
			listErroriAttesi.add("parsingExceptionRichiesta");
			listErroriAttesi.add("Unexpected close tag </soapenv:Envelope>; expected </soapenv:Body>");
			listErroriAttesi.add("Unexpected EOF; was expecting a close tag for element <soapenv:Body>");
			listErroriAttesi.add("ErroreGenerale");
			listErroriAttesi.add("Chiusura stream non riuscita");
			listErroriAttesi.add("Generazione di un risposta errore non riuscita");
			listErroriAttesi.add("Invalid chunk header");
			params[index][3] = listErroriAttesi;
			index++;
		}

		{
			// Apertura SOAPBody non Effettuata
			String xml = readFile(Utilities.testSuiteProperties.getSoap11FileName());
			xml = xml.replace("<soapenv:Body>", "</soapenv:Body>"); // imposto struttura xml errata
			params[index][0] = xml.getBytes();
			params[index][1] = "Apertura SOAPBody non Effettuata";
			List<String> motivoErroreParser = new ArrayList<String>();
			motivoErroreParser.add("Unexpected close tag </soapenv:Body>; expected </soapenv:Envelope>");
			motivoErroreParser.add("The element type \"soapenv:Envelope\" must be terminated by the matching end-tag \"</soapenv:Envelope>\"");
			params[index][2] = motivoErroreParser;
			List<String> listErroriAttesi = new ArrayList<String>();
			listErroriAttesi.add("parsingExceptionRichiesta");
			listErroriAttesi.add("Unexpected close tag </soapenv:Body>; expected </soapenv:Envelope>");
			listErroriAttesi.add("ErroreGenerale");
			listErroriAttesi.add("Chiusura stream non riuscita");
			listErroriAttesi.add("Generazione di un risposta errore non riuscita");
			listErroriAttesi.add("Invalid chunk header");
			params[index][3] = listErroriAttesi;
			index++;
		}

		{
			// Chiusura SOAPHeader non Effettuata
			String xml = readFile(Utilities.testSuiteProperties.getSoap11FileName());
			xml = xml.replace("</soapenv:Header>", "<soapenv:Header>"); // imposto struttura xml errata
			params[index][0] = xml.getBytes();
			params[index][1] = "Chiusura SOAPHeader non Effettuata";
			List<String> motivoErroreParser = new ArrayList<String>();
			motivoErroreParser.add("Unexpected close tag </soapenv:Envelope>; expected </soapenv:Header>");
			motivoErroreParser.add("The element type \"soapenv:Header\" must be terminated by the matching end-tag \"</soapenv:Header>\"");
			params[index][2] = motivoErroreParser;
			List<String> listErroriAttesi = new ArrayList<String>();
			listErroriAttesi.add("parsingExceptionRichiesta");
			listErroriAttesi.add("Unexpected close tag </soapenv:Envelope>; expected </soapenv:Header>");
			listErroriAttesi.add("ErroreGenerale");
			listErroriAttesi.add("Chiusura stream non riuscita");
			listErroriAttesi.add("Generazione di un risposta errore non riuscita");
			listErroriAttesi.add("Invalid chunk header");
			params[index][3] = listErroriAttesi;
			index++;
		}

		{
			// Apertura SOAPHeader non Effettuata
			String xml = readFile(Utilities.testSuiteProperties.getSoap11FileName());
			xml = xml.replace("<soapenv:Header>", "</soapenv:Header>"); // imposto struttura xml errata
			params[index][0] = xml.getBytes();
			params[index][1] = "Apertura SOAPHeader non Effettuata";
			List<String> motivoErroreParser = new ArrayList<String>();
			motivoErroreParser.add("Unexpected close tag </soapenv:Header>; expected </soapenv:Envelope>");
			motivoErroreParser.add("The element type \"soapenv:Envelope\" must be terminated by the matching end-tag \"</soapenv:Envelope>\"");
			params[index][2] = motivoErroreParser;
			List<String> listErroriAttesi = new ArrayList<String>();
			listErroriAttesi.add("parsingExceptionRichiesta");
			listErroriAttesi.add("Unexpected close tag </soapenv:Header>; expected </soapenv:Envelope>");
			listErroriAttesi.add("ErroreGenerale");
			listErroriAttesi.add("Chiusura stream non riuscita");
			listErroriAttesi.add("Generazione di un risposta errore non riuscita");
			listErroriAttesi.add("Invalid chunk header");
			params[index][3] = listErroriAttesi;
			index++;
		}

		{
			// Chiusura SOAPEnvelope non Effettuata
			String xml = readFile(Utilities.testSuiteProperties.getSoap11FileName());
			xml = xml.replace("</soapenv:Envelope>", "<soapenv:Envelope>"); // imposto struttura xml errata
			params[index][0] = xml.getBytes();
			params[index][1] = "Chiusura SOAPEnvelope non Effettuata";
			List<String> motivoErroreParser = new ArrayList<String>();
			motivoErroreParser.add("Unexpected EOF; was expecting a close tag for element <soapenv:Envelope>");
			motivoErroreParser.add("Envelope is not supported here. Envelope can not have elements other than Header and Body");
			motivoErroreParser.add("Unexpected close tag </SOAP-ENV:Body>; expected </soapenv:Envelope>");
			motivoErroreParser.add("XML document structures must start and end within the same entity");
			params[index][2] = motivoErroreParser;
			List<String> listErroriAttesi = new ArrayList<String>();
			listErroriAttesi.add("parsingExceptionRichiesta");
			listErroriAttesi.add("Unexpected EOF; was expecting a close tag for element <soapenv:Envelope>");
			listErroriAttesi.add("Envelope is not supported here. Envelope can not have elements other than Header and Body");
			listErroriAttesi.add("Unexpected close tag </SOAP-ENV:Body>; expected </soapenv:Envelope>");
			listErroriAttesi.add("ErroreGenerale");
			listErroriAttesi.add("Chiusura stream non riuscita");
			listErroriAttesi.add("Generazione di un risposta errore non riuscita");
			listErroriAttesi.add("Invalid chunk header");
			params[index][3] = listErroriAttesi;
			index++;
		}

		{
			// Apertura SOAPEnvelope non Effettuata
			String xml = readFile(Utilities.testSuiteProperties.getSoap11FileName());
			xml = xml.replace("<soapenv:Envelope ", "</soapenv:Envelope "); // imposto struttura xml errata
			params[index][0] = xml.getBytes();
			params[index][1] = "Apertura SOAPEnvelope non Effettuata";
			List<String> motivoErroreParser = new ArrayList<String>();
			motivoErroreParser.add("Unexpected character combination '</' in prolog");
			motivoErroreParser.add("The markup in the document preceding the root element must be well-formed");
			params[index][2] = motivoErroreParser;
			List<String> listErroriAttesi = new ArrayList<String>();
			listErroriAttesi.add("parsingExceptionRichiesta");
			listErroriAttesi.add("Unexpected character combination '</' in prolog");
			listErroriAttesi.add("ErroreGenerale");
			listErroriAttesi.add("Chiusura stream non riuscita");
			listErroriAttesi.add("Generazione di un risposta errore non riuscita");
			listErroriAttesi.add("Invalid chunk header");
			params[index][3] = listErroriAttesi;
			index++;
		}
		{
			// Caratteri Iniziali
			String xml = readFile(Utilities.testSuiteProperties.getSoap11FileName());
			xml = xml.replace("<soapenv:Envelope ", "CARATTERI INIZIALI <soapenv:Envelope "); // imposto struttura xml errata
			params[index][0] = xml.getBytes();
			params[index][1] = "Caratteri prima dell'Apertura della SOAPEnvelope";
			List<String> motivoErroreParser = new ArrayList<String>();
			motivoErroreParser.add("Unexpected character 'C'");
			motivoErroreParser.add("Content is not allowed in prolog");
			params[index][2] = motivoErroreParser;
			List<String> listErroriAttesi = new ArrayList<String>();
			listErroriAttesi.add("parsingExceptionRichiesta");
			listErroriAttesi.add("Unexpected character 'C'");
			listErroriAttesi.add("ErroreGenerale");
			listErroriAttesi.add("Chiusura stream non riuscita");
			listErroriAttesi.add("Generazione di un risposta errore non riuscita");
			listErroriAttesi.add("Invalid chunk header");
			params[index][3] = listErroriAttesi;
			index++;
		}
		{
			// Primo Elemento del Body non viene chiuso
			String xml = readFile(Utilities.testSuiteProperties.getSoap11FileName());
			xml = xml.replace("</ns1:getQuote>", "<ns1:getQuote>"); // imposto struttura xml errata
			params[index][0] = xml.getBytes();
			params[index][1] = "Primo elemento del Body non viene chiuso";
			List<String> motivoErroreParser = new ArrayList<String>();
			motivoErroreParser.add("Unexpected close tag </soapenv:Body>; expected </ns1:getQuote>");
			motivoErroreParser.add("The element type \"ns1:getQuote\" must be terminated by the matching end-tag \"</ns1:getQuote>\"");
			params[index][2] = motivoErroreParser;
			List<String> listErroriAttesi = new ArrayList<String>();
			listErroriAttesi.add("parsingExceptionRichiesta");
			listErroriAttesi.add("Unexpected close tag </soapenv:Body>; expected </ns1:getQuote>");
			listErroriAttesi.add("Unexpected close tag </soapenv:Envelope>; expected </ns1:getQuote>");
			listErroriAttesi.add("ErroreGenerale");
			listErroriAttesi.add("Chiusura stream non riuscita");
			listErroriAttesi.add("Generazione di un risposta errore non riuscita");
			listErroriAttesi.add("Invalid chunk header");
			params[index][3] = listErroriAttesi;
			index++;
		}
		{
			// Primo Elemento del Body non viene aperto
			String xml = readFile(Utilities.testSuiteProperties.getSoap11FileName());
			xml = xml.replace("<ns1:getQuote ", "</ns1:getQuote "); // imposto struttura xml errata
			params[index][0] = xml.getBytes();
			params[index][1] = "Primo elemento del Body non viene aperto";
			List<String> motivoErroreParser = new ArrayList<String>();
			motivoErroreParser.add("Unexpected close tag </ns1:getQuote>; expected </soapenv:Body>");
			motivoErroreParser.add("The element type \"soapenv:Body\" must be terminated by the matching end-tag \"</soapenv:Body>\"");
			params[index][2] = motivoErroreParser;
			List<String> listErroriAttesi = new ArrayList<String>();
			listErroriAttesi.add("parsingExceptionRichiesta");
			listErroriAttesi.add("Unexpected close tag </ns1:getQuote>; expected </soapenv:Body>");
			listErroriAttesi.add("ErroreGenerale");
			listErroriAttesi.add("Chiusura stream non riuscita");
			listErroriAttesi.add("Generazione di un risposta errore non riuscita");
			listErroriAttesi.add("Invalid chunk header");
			params[index][3] = listErroriAttesi;
			index++;
		}
		{
			// Elemento interno del Body non viene chiuso
			String xml = readFile(Utilities.testSuiteProperties.getSoap11FileName());
			xml = xml.replace("</symbol>", "<symbol>"); // imposto struttura xml errata
			params[index][0] = xml.getBytes();
			params[index][1] = "Elemento interno del Body non viene chiuso";
			List<String> motivoErroreParser = new ArrayList<String>();
			motivoErroreParser.add("Unexpected close tag </ns1:getQuote>; expected </symbol>");
			motivoErroreParser.add("The element type \"symbol\" must be terminated by the matching end-tag \"</symbol>\"");
			params[index][2] = motivoErroreParser;
			List<String> listErroriAttesi = new ArrayList<String>();
			listErroriAttesi.add("parsingExceptionRichiesta");
			listErroriAttesi.add("Unexpected close tag </ns1:getQuote>; expected </symbol>");
			listErroriAttesi.add("Unexpected close tag </soapenv:Body>; expected </symbol>");
			listErroriAttesi.add("ErroreGenerale");
			listErroriAttesi.add("Chiusura stream non riuscita");
			listErroriAttesi.add("Generazione di un risposta errore non riuscita");
			listErroriAttesi.add("Invalid chunk header");
			params[index][3] = listErroriAttesi;
			index++;
		}
		{
			// Elemento interno del Body non viene aperto
			String xml = readFile(Utilities.testSuiteProperties.getSoap11FileName());
			xml = xml.replace("<symbol ", "</symbol "); // imposto struttura xml errata
			params[index][0] = xml.getBytes();
			params[index][1] = "Elemento interno del Body non viene aperto";
			List<String> motivoErroreParser = new ArrayList<String>();
			motivoErroreParser.add("Unexpected close tag </symbol>; expected </ns1:getQuote>");
			motivoErroreParser.add("The element type \"ns1:getQuote\" must be terminated by the matching end-tag \"</ns1:getQuote>\"");
			params[index][2] = motivoErroreParser;
			List<String> listErroriAttesi = new ArrayList<String>();
			listErroriAttesi.add("parsingExceptionRichiesta");
			listErroriAttesi.add("Unexpected close tag </symbol>; expected </ns1:getQuote>");
			listErroriAttesi.add("ErroreGenerale");
			listErroriAttesi.add("Chiusura stream non riuscita");
			listErroriAttesi.add("Generazione di un risposta errore non riuscita");
			listErroriAttesi.add("Invalid chunk header");
			params[index][3] = listErroriAttesi;
			index++;
		}
		{
			// Elemento grande (50K) con errore all'inizio del soap body
			String xml = readFile(Utilities.testSuiteProperties.getSoapTestSOAPScorretto_erroreInCima());
			params[index][0] = xml.getBytes();
			params[index][1] = "Elemento grande (50K) con errore all'inizio del soap body";
			List<String> motivoErroreParser = new ArrayList<String>();
			motivoErroreParser.add("Unexpected character 'E' ");
			motivoErroreParser.add("Attribute name \"VOLUTAMENTE\" associated with an element type \"ELEMENTO\" must be followed by the ' = ' character");
			params[index][2] = motivoErroreParser;
			List<String> listErroriAttesi = new ArrayList<String>();
			listErroriAttesi.add("parsingExceptionRichiesta");
			listErroriAttesi.add("Unexpected character 'E' ");
			listErroriAttesi.add("Unexpected close tag </soapenv:Body>; expected </ELEMENTO>");
			listErroriAttesi.add("ErroreGenerale");
			listErroriAttesi.add("Chiusura stream non riuscita");
			listErroriAttesi.add("Generazione di un risposta errore non riuscita");
			listErroriAttesi.add("Invalid chunk header");
			params[index][3] = listErroriAttesi;
			index++;
		}
		{
			// Elemento grande (50K) con errore in mezzo del soap body
			String xml = readFile(Utilities.testSuiteProperties.getSoapTestSOAPScorretto_erroreInMezzo());
			params[index][0] = xml.getBytes();
			params[index][1] = "Elemento grande (50K) con errore in mezzo del soap body";
			List<String> motivoErroreParser = new ArrayList<String>();
			motivoErroreParser.add("Unexpected character 'E' ");
			motivoErroreParser.add("Attribute name \"VOLUTAMENTE\" associated with an element type \"ELEMENTO\" must be followed by the ' = ' character");
			params[index][2] = motivoErroreParser;
			List<String> listErroriAttesi = new ArrayList<String>();
			listErroriAttesi.add("parsingExceptionRichiesta");
			listErroriAttesi.add("Unexpected character 'E' ");
			listErroriAttesi.add("Unexpected close tag </soapenv:Body>; expected </ELEMENTO>");
			listErroriAttesi.add("Unexpected close tag </xsd:skcotSyub>; expected </ELEMENTO>");
			listErroriAttesi.add("ErroreGenerale");
			listErroriAttesi.add("Chiusura stream non riuscita");
			listErroriAttesi.add("Generazione di un risposta errore non riuscita");
			listErroriAttesi.add("Invalid chunk header");
			params[index][3] = listErroriAttesi;
			index++;
		}

		{
			// Elemento grande (50K) con errore in fondo del soap body
			String xml = readFile(Utilities.testSuiteProperties.getSoapTestSOAPScorretto_erroreInFondo());
			params[index][0] = xml.getBytes();
			params[index][1] = "Elemento grande (50K) con errore in fondo del soap body";
			List<String> motivoErroreParser = new ArrayList<String>();
			motivoErroreParser.add("Unexpected character 'E' ");
			motivoErroreParser.add("Attribute name \"VOLUTAMENTE\" associated with an element type \"ELEMENTO\" must be followed by the ' = ' character");
			params[index][2] = motivoErroreParser;
			List<String> listErroriAttesi = new ArrayList<String>();
			listErroriAttesi.add("parsingExceptionRichiesta");
			listErroriAttesi.add("Unexpected character 'E' ");
			listErroriAttesi.add("Unexpected close tag </soapenv:Body>; expected </ELEMENTO>");
			listErroriAttesi.add("ErroreGenerale");
			listErroriAttesi.add("Chiusura stream non riuscita");
			listErroriAttesi.add("Generazione di un risposta errore non riuscita");
			listErroriAttesi.add("Invalid chunk header");
			params[index][3] = listErroriAttesi;
			index++;
		}

		return params;
	}

	
	private void invocazionePD(byte[] messaggioXMLRichiesta,String identificativoTest,List<String> motivoErroreParser,List<String> listErroriAttesi,
			Repository repository,String nomePortaDelegata,boolean checkDiagnostico,boolean PD2SOAP)throws TestSuiteException,SOAPException, Exception{
		invocazionePD(messaggioXMLRichiesta, identificativoTest, motivoErroreParser, listErroriAttesi, repository, nomePortaDelegata, 
				CodiceErroreIntegrazione.CODICE_432_PARSING_EXCEPTION_RICHIESTA,checkDiagnostico,PD2SOAP);
	}
	private void invocazionePD(byte[] messaggioXMLRichiesta,String identificativoTest,List<String> motivoErroreParser,List<String> listErroriAttesi,
			Repository repository,String nomePortaDelegata,CodiceErroreIntegrazione codiceErroreIntegrazione,boolean checkDiagnostico,
			boolean PD2SOAP)throws TestSuiteException,SOAPException, Exception{

		Date dataInizioTest = DateManager.getDate();

		DatabaseComponent dbComponentErogatore = null;
		DatabaseMsgDiagnosticiComponent dbDiagnosticFruitore = null;
		try{
			Reporter.log("Test ["+identificativoTest+"]");
			ClientHttpGenerico client=new ClientHttpGenerico(repository);
			if(PD2SOAP){
				client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore().replace("PD", "PDtoSOAP"));
			}
			else{
				client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			}
			client.setPortaDelegata(nomePortaDelegata);
			client.connectToSoapEngine();
			client.setMessaggioXMLRichiesta(messaggioXMLRichiesta);
			client.setConnectionReadTimeout(READ_TIMEOUT);
			client.setRispostaDaGestire(true);
			if(PD2SOAP == false){
				client.setForceResponseAsSOAPProcessor(true); // per lanciare l'axis fault
			}
			dbDiagnosticFruitore = DatabaseProperties.getDatabaseComponentDiagnosticaFruitore();
			// AttesaTerminazioneMessaggi
			if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()){
				dbComponentErogatore = DatabaseProperties.getDatabaseComponentErogatore();

				client.setAttesaTerminazioneMessaggi(true);
				client.setDbAttesaTerminazioneMessaggiErogatore(dbComponentErogatore);
			}

			try {
				client.run();
				
				if(PD2SOAP){
					
					if("Caratteri prima dell'Apertura della SOAPEnvelope".equals(identificativoTest)){
						// Essendo tutto l'xml imbustato funziona.
						Assert.assertTrue(client.getCodiceStatoHTTP()==200); 
					}
					else{
					
						Assert.assertTrue(client.getCodiceStatoHTTP()==500);
						
						byte [] xmlErroreApplicativo = client.getMessaggioXMLRisposta();
						Assert.assertTrue(xmlErroreApplicativo!=null);
						
						boolean faultStringMatch = false;
						boolean match = false;
						for (String erroreParser : motivoErroreParser) {
							try{
								Utilities.verificaErroreApplicativoCnipa(org.openspcoop2.message.XMLUtils.getInstance().newElement(xmlErroreApplicativo),
										Utilities.testSuiteProperties.getIdentitaDefault_dominio(),"RicezioneContenutiApplicativiHTTP", 
										Utilities.toString(codiceErroreIntegrazione), 
										erroreParser, Utilities.CONTROLLO_DESCRIZIONE_TRAMITE_METODO_CONTAINS);
								match = true;
							}catch(Throwable e){
								match = false;
							}
							Reporter.log("Check fault string rispetto a ["+erroreParser+"]: "+match);
							if(match){
								faultStringMatch = true;
								break;
							}
						}
						Assert.assertTrue(faultStringMatch); // il messaggio di qualsiasi parser dovrebbe cmq indicare il motivo del perche' il msg non e' corretto
					}
					
				}
				else{
					throw new Exception("Attesa eccezione per xml errato ("+identificativoTest+")");
				}
			} catch (AxisFault error) {
				
				if(PD2SOAP){
					throw error;
				}
				
				try{
					Thread.sleep(2000); // aspetto scrittura diagnostici in streaming che avviene dopo
				}catch(Exception e){}
				
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				Reporter.log("Controllo fault code ["+Utilities.toString(codiceErroreIntegrazione)+"]");
				Assert.assertTrue(Utilities.toString(codiceErroreIntegrazione).equals(error.getFaultCode().getLocalPart()));
				Reporter.log("Controllo fault string ["+error.getFaultString()+"]");
				boolean faultStringMatch = false;
				for (String erroreParser : motivoErroreParser) {
					boolean match = error.getFaultString().contains(erroreParser);
					Reporter.log("Check fault string rispetto a ["+erroreParser+"]: "+match);
					if(match){
						faultStringMatch = true;
						break;
					}
				}
				Assert.assertTrue(faultStringMatch); // il messaggio di qualsiasi parser dovrebbe cmq indicare il motivo del perche' il msg non e' corretto
				
				if(checkDiagnostico){
					SimpleDateFormat dateformat = new SimpleDateFormat ("yyyy-MM-dd_HH:mm:ss.SSS");
					Reporter.log("Check diagnostico 001054 rispetto a data ["+dateformat.format(dataInizioTest)+"]");
					Assert.assertTrue(dbDiagnosticFruitore.isTracedCodice(dataInizioTest, "001054"));
					
					faultStringMatch = false;
					for (String erroreParser : motivoErroreParser) {
						boolean match = dbDiagnosticFruitore.isTracedMessaggioWithLike(dataInizioTest, erroreParser);
						Reporter.log("Check diagnostico rispetto a ["+erroreParser+"]: "+match);
						if(match){
							faultStringMatch = true;
							break;
						}
					}
					Assert.assertTrue(faultStringMatch); // il messaggio di qualsiasi parser dovrebbe cmq indicare il motivo del perche' il msg non e' corretto
				}
			}finally{
				try{
					dbComponentErogatore.close();
				}catch(Exception eClose){}
				try{
					dbDiagnosticFruitore.close();
				}catch(Exception eClose){}
			}

		}catch(Exception e){
			throw e;
		}finally{
			try{
				dbComponentErogatore.close();
			}catch(Exception eClose){}
			try{
				dbDiagnosticFruitore.close();
			}catch(Exception eClose){}
		}

		try{
			// Aspetto che anche il servizio PA processi con errore l'eventuale richiesta che riceve (Poi troverà broken pipe)
			Thread.sleep(2000);
		}catch(Exception e){}

		Date dataFineTest = DateManager.getDate();

		if(listErroriAttesi!=null && listErroriAttesi.size()>0){
			for (String motivoErrore : listErroriAttesi) {
				ErroreAttesoOpenSPCoopLogCore err = new ErroreAttesoOpenSPCoopLogCore();
				err.setIntervalloInferiore(dataInizioTest);
				err.setIntervalloSuperiore(dataFineTest);
				err.setMsgErrore(motivoErrore);
				this.erroriAttesiOpenSPCoopCore.add(err);
			}
		}

	}
	
	
	
	Repository repositoryStrutturaXMLErrataPortaDelegata=new Repository();
	@Test(groups={SOAPMessageScorretti.ID_GRUPPO,
			SOAPMessageScorretti.ID_GRUPPO+".PD_XML_RICHIESTA",
			SOAPMessageScorretti.ID_GRUPPO+".STRUTTURA_XML_ERRATA_PD_RICHIESTA"},dataProvider="strutturaSoapErrata")
	public void strutturaXMLErrata_PortaDelegata_Richiesta(byte[] messaggioXMLRichiesta,String identificativoTest,List<String> motivoErroreParser,List<String> listErroriAttesi)throws TestSuiteException,SOAPException, Exception{

		this.invocazionePD(messaggioXMLRichiesta, identificativoTest, motivoErroreParser, listErroriAttesi, 
				this.repositoryStrutturaXMLErrataPortaDelegata, CostantiTestSuite.PORTA_DELEGATA_PROFILO_SINCRONO,
				true,false);

	}
	
	Repository repositoryStrutturaXMLErrataPortaDelegata_PD2SOAP=new Repository();
	@Test(groups={SOAPMessageScorretti.ID_GRUPPO,
			SOAPMessageScorretti.ID_GRUPPO+".PD2SOAP",
			SOAPMessageScorretti.ID_GRUPPO+".STRUTTURA_XML_ERRATA_PD2SOAP_RICHIESTA"},dataProvider="strutturaSoapErrata")
	public void strutturaXMLErrata_PortaDelegata_PD2SOAP_Richiesta(byte[] messaggioXMLRichiesta,String identificativoTest,List<String> motivoErroreParser,List<String> listErroriAttesi)throws TestSuiteException,SOAPException, Exception{

		Date dataInizioTest = DateManager.getDate();
		
		listErroriAttesi.add("ImbustamentoSOAP");
		listErroriAttesi.add("Imbustamento messaggio in un messaggio SOAP");
		
		try{
		
			File f = null;
			PrintWriter pw = null;
			
			boolean esito432 = false;
			//motivoErroreParser.add("Il contenuto applicativo della richiesta ricevuta non è processabile dalla Porta di Dominio");
			try{
				this.invocazionePD(messaggioXMLRichiesta, identificativoTest, motivoErroreParser, null, 
						this.repositoryStrutturaXMLErrataPortaDelegata_PD2SOAP, CostantiTestSuite.PORTA_DELEGATA_PROFILO_SINCRONO,
						CodiceErroreIntegrazione.CODICE_432_PARSING_EXCEPTION_RICHIESTA,true,true);
				esito432 = true;
			}catch(Throwable t){
				f = File.createTempFile("test", ".txt");
				pw = new PrintWriter(f);
				pw.write("Test 432\n\n");
				t.printStackTrace(pw);
			}
			if(esito432){
				return;
			}
			
			boolean esito422 = false;
			motivoErroreParser.add("I bytes inviati al servizio di ricezione contenuti applicativi non sono utilizzabili per formare un messaggio SOAP");
			try{
				this.invocazionePD(messaggioXMLRichiesta, identificativoTest, motivoErroreParser, null, 
						this.repositoryStrutturaXMLErrataPortaDelegata_PD2SOAP, CostantiTestSuite.PORTA_DELEGATA_PROFILO_SINCRONO,
						CodiceErroreIntegrazione.CODICE_422_IMBUSTAMENTO_SOAP_NON_RIUSCITO_RICHIESTA_APPLICATIVA,true,true);
				esito422 = true;
			}catch(Throwable t){
				if(f==null){
					f = File.createTempFile("test", ".txt");
					pw = new PrintWriter(f);
				}
				pw.write("Test 422\n\n");
				t.printStackTrace(pw);
			}
			if(esito422){
				return;
			}
			
			if(pw!=null){
				pw.flush();
				pw.close();
			}
			
			throw new Exception("Non è stato riscontrata ne una casistica 432 ne una casistica 422 (Dettagli in ["+f.getAbsolutePath()+"])");
			
		}finally{

			Date dataFineTest = DateManager.getDate();
	
			if(listErroriAttesi!=null && listErroriAttesi.size()>0){
				for (String motivoErrore : listErroriAttesi) {
					ErroreAttesoOpenSPCoopLogCore err = new ErroreAttesoOpenSPCoopLogCore();
					err.setIntervalloInferiore(dataInizioTest);
					err.setIntervalloSuperiore(dataFineTest);
					err.setMsgErrore(motivoErrore);
					this.erroriAttesiOpenSPCoopCore.add(err);
				}
			}
			
		}
	}
	
	Repository repositoryStrutturaXMLErrataPortaDelegataStateful=new Repository();
	@Test(groups={SOAPMessageScorretti.ID_GRUPPO,
			SOAPMessageScorretti.ID_GRUPPO+".PD_XML_RICHIESTA",
			SOAPMessageScorretti.ID_GRUPPO+".STRUTTURA_XML_ERRATA_PD_STATEFUL_RICHIESTA"},dataProvider="strutturaSoapErrata")
	public void strutturaXMLErrata_PortaDelegata_stateful_Richiesta(byte[] messaggioXMLRichiesta,String identificativoTest,List<String> motivoErroreParser,List<String> listErroriAttesi)throws TestSuiteException,SOAPException, Exception{

		this.invocazionePD(messaggioXMLRichiesta, identificativoTest, motivoErroreParser, listErroriAttesi, 
				this.repositoryStrutturaXMLErrataPortaDelegataStateful, CostantiTestSuite.PORTA_DELEGATA_PROFILO_SINCRONO_STATEFUL,
				true,false);

	}
	
	Repository repositoryStrutturaXMLErrataPortaDelegataIntegrazioneContentBased=new Repository();
	@Test(groups={SOAPMessageScorretti.ID_GRUPPO,
			SOAPMessageScorretti.ID_GRUPPO+".PD_XML_RICHIESTA",
			SOAPMessageScorretti.ID_GRUPPO+".STRUTTURA_XML_ERRATA_PD_INTEGRAZIONE_CONTENT_BASED_RICHIESTA"},dataProvider="strutturaSoapErrata")
	public void strutturaXMLErrata_PortaDelegata_integrazioneContentBased_Richiesta(byte[] messaggioXMLRichiesta,String identificativoTest,List<String> motivoErroreParser,List<String> listErroriAttesi)throws TestSuiteException,SOAPException, Exception{

		this.invocazionePD(messaggioXMLRichiesta, identificativoTest, motivoErroreParser, listErroriAttesi, 
				this.repositoryStrutturaXMLErrataPortaDelegataIntegrazioneContentBased, CostantiTestSuite.PORTA_DELEGATA_PROFILO_SINCRONO_INTEGRAZIONE_CONTENT_BASED2,
				true,false);

	}
	
	Repository repositoryStrutturaXMLErrataPortaDelegataCorrelazioneApplicativaContentBased=new Repository();
	@Test(groups={SOAPMessageScorretti.ID_GRUPPO,
			SOAPMessageScorretti.ID_GRUPPO+".PD_XML_RICHIESTA",
			SOAPMessageScorretti.ID_GRUPPO+".STRUTTURA_XML_ERRATA_PD_CORRELAZIONE_APPLICATIVA_CONTENT_BASED_RICHIESTA"},dataProvider="strutturaSoapErrata")
	public void strutturaXMLErrata_PortaDelegata_correlazioneApplicativaContentBased_Richiesta(byte[] messaggioXMLRichiesta,String identificativoTest,List<String> motivoErroreParser,List<String> listErroriAttesi)throws TestSuiteException,SOAPException, Exception{

		this.invocazionePD(messaggioXMLRichiesta, identificativoTest, motivoErroreParser, listErroriAttesi, 
				this.repositoryStrutturaXMLErrataPortaDelegataCorrelazioneApplicativaContentBased, 
				CostantiTestSuite.PORTA_DELEGATA_PROFILO_SINCRONO_INTEGRAZIONE_CORRELAZIONE_APPLICATIVA_CONTENT_BASED,
				true,false);

	}
	
	Repository repositoryStrutturaXMLErrataPortaDelegataValidazioneContenutiApplicativiWarningOnly=new Repository();
	@Test(groups={SOAPMessageScorretti.ID_GRUPPO,
			SOAPMessageScorretti.ID_GRUPPO+".PD_XML_RICHIESTA",
			SOAPMessageScorretti.ID_GRUPPO+".STRUTTURA_XML_ERRATA_PD_VALIDAZIONE_CONTENUTI_WARN_RICHIESTA"},dataProvider="strutturaSoapErrata")
	public void strutturaXMLErrata_PortaDelegata_validazioneContenutiApplicativiWarningOnly_Richiesta(byte[] messaggioXMLRichiesta,String identificativoTest,List<String> motivoErroreParser,List<String> listErroriAttesi)throws TestSuiteException,SOAPException, Exception{

		listErroriAttesi.add("Riscontrata non conformità rispetto all'interfaccia WSDL");
		listErroriAttesi.add("Validazione WSDL (true) fallita");
		listErroriAttesi.add("Riscontrato errore durante la validazione xsd della richiesta applicativa");
		
		String portaDelegata = CostantiTestSuite.PORTA_DELEGATA_VALIDAZIONE_WSDL_GENERICA_WARNING_ONLY+"/"+
				CostantiTestSuite.SPCOOP_NOME_SERVIZIO_VAL_WSDL_GESTIONE_UTENTI_WDL+"/"+
				CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_VALIDAZIONE_WSDL_OPENSPCOOP_AZIONE_REGISTRAZIONE_UTENTE_WDL;
		this.invocazionePD(messaggioXMLRichiesta, identificativoTest, motivoErroreParser, listErroriAttesi, 
				this.repositoryStrutturaXMLErrataPortaDelegataValidazioneContenutiApplicativiWarningOnly, portaDelegata,
				true,false);

	}
	
	Repository repositoryStrutturaXMLErrataPortaDelegataValidazioneContenutiApplicativi=new Repository();
	@Test(groups={SOAPMessageScorretti.ID_GRUPPO,
			SOAPMessageScorretti.ID_GRUPPO+".PD_XML_RICHIESTA",
			SOAPMessageScorretti.ID_GRUPPO+".STRUTTURA_XML_ERRATA_PD_VALIDAZIONE_CONTENUTI_RICHIESTA"},dataProvider="strutturaSoapErrata")
	public void strutturaXMLErrata_PortaDelegata_validazioneContenutiApplicativi_Richiesta(byte[] messaggioXMLRichiesta,String identificativoTest,List<String> motivoErroreParser,List<String> listErroriAttesi)throws TestSuiteException,SOAPException, Exception{
		
		listErroriAttesi.add("Riscontrata non conformità rispetto all'interfaccia WSDL");
		listErroriAttesi.add("Validazione WSDL (true) fallita");
		listErroriAttesi.add("Riscontrato errore durante la validazione xsd della richiesta applicativa");
		
		Date dataInizioTest = DateManager.getDate();
		
		String portaDelegata = CostantiTestSuite.PORTA_DELEGATA_VALIDAZIONE_WSDL_GENERICA+"/"+
				CostantiTestSuite.SPCOOP_NOME_SERVIZIO_VAL_WSDL_GESTIONE_UTENTI_WDL+"/"+
				CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_VALIDAZIONE_WSDL_OPENSPCOOP_AZIONE_REGISTRAZIONE_UTENTE_WDL;
		try{
			this.invocazionePD(messaggioXMLRichiesta, identificativoTest, motivoErroreParser, null, 
					this.repositoryStrutturaXMLErrataPortaDelegataValidazioneContenutiApplicativiWarningOnly, portaDelegata,
					true,false);
		}catch(Throwable t){
			if("Chiusura SOAPEnvelope non Effettuata".equals(identificativoTest)){
				try{
					// Nel caso di processamento in streaming non si arriva ad avere l'errore
					// Provo a verificare di essere in questo caso
					motivoErroreParser.add("Il contenuto applicativo del messaggio di richiesta non rispetta l'accordo di servizio (Wsdl erogatore) definito nel Registro dei Servizi");
					this.invocazionePD(messaggioXMLRichiesta, identificativoTest, motivoErroreParser, null, 
							this.repositoryStrutturaXMLErrataPortaDelegataValidazioneContenutiApplicativiWarningOnly, portaDelegata,
							CodiceErroreIntegrazione.CODICE_418_VALIDAZIONE_WSDL_RICHIESTA_FALLITA,
							false,false);
				}catch(Throwable tInternal){
					// rilancio l'originale
					throw new RuntimeException(t);
				}
			}
			else{
				throw new RuntimeException(t);
			}
		}
		
		Date dataFineTest = DateManager.getDate();

		if(listErroriAttesi!=null && listErroriAttesi.size()>0){
			for (String motivoErrore : listErroriAttesi) {
				ErroreAttesoOpenSPCoopLogCore err = new ErroreAttesoOpenSPCoopLogCore();
				err.setIntervalloInferiore(dataInizioTest);
				err.setIntervalloSuperiore(dataFineTest);
				err.setMsgErrore(motivoErrore);
				this.erroriAttesiOpenSPCoopCore.add(err);
			}
		}

	}

	Repository repositoryStrutturaXMLErrataPortaDelegataWSSecurityEncrypt=new Repository();
	@Test(groups={SOAPMessageScorretti.ID_GRUPPO,
			SOAPMessageScorretti.ID_GRUPPO+".PD_XML_RICHIESTA",
			SOAPMessageScorretti.ID_GRUPPO+".STRUTTURA_XML_ERRATA_PD_WSSECURITY_ENCRYPT_RICHIESTA"},dataProvider="strutturaSoapErrata")
	public void strutturaXMLErrata_PortaDelegata_WSSecurityEncrypt_Richiesta(byte[] messaggioXMLRichiesta,String identificativoTest,List<String> motivoErroreParser,List<String> listErroriAttesi)throws TestSuiteException,SOAPException, Exception{

		listErroriAttesi.add("Generatosi errore durante il processamento Message-Security(Sender)");
		listErroriAttesi.add("BypassMustUnderstand, errore durante il set processed degli header con mustUnderstand='1' e actor non presente");
		
		this.invocazionePD(messaggioXMLRichiesta, identificativoTest, motivoErroreParser, listErroriAttesi, 
				this.repositoryStrutturaXMLErrataPortaDelegataWSSecurityEncrypt, CostantiTestSuite.PORTA_DELEGATA_WSS_ENCRYPT,
				true,false);

	}
	
	Repository repositoryStrutturaXMLErrataPortaDelegataWSSecuritySignature=new Repository();
	@Test(groups={SOAPMessageScorretti.ID_GRUPPO,
			SOAPMessageScorretti.ID_GRUPPO+".PD_XML_RICHIESTA",
			SOAPMessageScorretti.ID_GRUPPO+".STRUTTURA_XML_ERRATA_PD_WSSECURITY_SIGNATURE_RICHIESTA"},dataProvider="strutturaSoapErrata")
	public void strutturaXMLErrata_PortaDelegata_WSSecuritySignature_Richiesta(byte[] messaggioXMLRichiesta,String identificativoTest,List<String> motivoErroreParser,List<String> listErroriAttesi)throws TestSuiteException,SOAPException, Exception{

		listErroriAttesi.add("Generatosi errore durante il processamento Message-Security(Sender)");
		listErroriAttesi.add("BypassMustUnderstand, errore durante il set processed degli header con mustUnderstand='1' e actor non presente");
		
		this.invocazionePD(messaggioXMLRichiesta, identificativoTest, motivoErroreParser, listErroriAttesi, 
				this.repositoryStrutturaXMLErrataPortaDelegataWSSecuritySignature, CostantiTestSuite.PORTA_DELEGATA_WSS_SIGNATURE,
				true,false);

	}
	








	
	
	
	
	
	private void invocazionePA(byte[] messaggioXMLRichiesta,String identificativoTest,List<String> motivoErroreParser,List<String> listErroriAttesi,
			Repository repository,String servizio, String azione,
			boolean erroreValidazione,boolean checkDiagnostico)throws TestSuiteException,SOAPException, Exception{
		invocazionePA(messaggioXMLRichiesta, identificativoTest, motivoErroreParser, listErroriAttesi, repository, servizio, azione,
				CodiceErroreIntegrazione.CODICE_432_PARSING_EXCEPTION_RICHIESTA,erroreValidazione,checkDiagnostico);
	}
	private void invocazionePA(byte[] messaggioXMLRichiesta,String identificativoTest,List<String> motivoErroreParser,List<String> listErroriAttesi,
			Repository repository,String servizio, String azione, CodiceErroreIntegrazione codiceErroreIntegrazione,
			boolean erroreValidazione,boolean checkDiagnostico)throws TestSuiteException,SOAPException, Exception{

		Date dataInizioTest = DateManager.getDate();

		DatabaseComponent dbComponentErogatore = null;
		DatabaseMsgDiagnosticiComponent dbDiagnosticErogatore = null;
		try{
			
			String msgXmlRichiesta = new String(messaggioXMLRichiesta);
			msgXmlRichiesta = msgXmlRichiesta.replace(TEMPLATE_HEADER, this.getHeaderBusta(servizio,azione));
			
			
			Reporter.log("Test ["+identificativoTest+"]");
			ClientHttpGenerico client=new ClientHttpGenerico(repository);
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneBusteErogatore());
			client.connectToSoapEngine();
			client.setMessaggioXMLRichiesta(msgXmlRichiesta.getBytes());
			client.setConnectionReadTimeout(READ_TIMEOUT);
			client.setRispostaDaGestire(true);
			client.setForceResponseAsSOAPProcessor(true); // per lanciare l'axis fault
			dbDiagnosticErogatore = DatabaseProperties.getDatabaseComponentDiagnosticaErogatore();
			// AttesaTerminazioneMessaggi
			if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()){
				dbComponentErogatore = DatabaseProperties.getDatabaseComponentErogatore();
					
				client.setAttesaTerminazioneMessaggi(true);
				client.setDbAttesaTerminazioneMessaggiErogatore(dbComponentErogatore);
			}

			try {
				client.run();
				throw new Exception("Attesa eccezione per xml errato ("+identificativoTest+")");
			} catch (AxisFault error) {
				
				try{
					Thread.sleep(2000); // aspetto scrittura diagnostici in streaming che avviene dopo
				}catch(Exception e){}
				
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				String msgErrore = null;
				String codErrore =  null;
				if(erroreValidazione){
					codErrore = SPCoopCostanti.FAULT_CODE_VALIDAZIONE_SPCOOP;
					msgErrore = SPCoopCostanti.FAULT_STRING_VALIDAZIONE_SPCOOP;
				}
				else{
					codErrore = SPCoopCostanti.FAULT_CODE_PROCESSAMENTO_SPCOOP;
					msgErrore = SPCoopCostanti.FAULT_STRING_PROCESSAMENTO_SPCOOP;
				}
				Reporter.log("Controllo fault code ["+codErrore+"]");
				Assert.assertTrue(codErrore.equals(error.getFaultCode().getLocalPart()) ||
						codErrore.equals("soap:"+error.getFaultCode().getLocalPart()) ||
						codErrore.equals("soapenv:"+error.getFaultCode().getLocalPart()));
				Reporter.log("Controllo fault string ["+msgErrore+"]");
				Assert.assertTrue(msgErrore.equals(error.getFaultString()));

				if(checkDiagnostico){
					SimpleDateFormat dateformat = new SimpleDateFormat ("yyyy-MM-dd_HH:mm:ss.SSS");
					Reporter.log("Check diagnostico 004071 rispetto a data ["+dateformat.format(dataInizioTest)+"]");
					Assert.assertTrue(dbDiagnosticErogatore.isTracedCodice(dataInizioTest, "004071"));
					
					boolean faultStringMatch = false;
					for (String erroreParser : motivoErroreParser) {
						boolean match = dbDiagnosticErogatore.isTracedMessaggioWithLike(dataInizioTest, erroreParser);
						Reporter.log("Check diagnostico rispetto a ["+erroreParser+"]: "+match);
						if(match){
							faultStringMatch = true;
							break;
						}
					}
					Assert.assertTrue(faultStringMatch); // il messaggio di qualsiasi parser dovrebbe cmq indicare il motivo del perche' il msg non e' corretto
				}
				
			}finally{
				try{
					dbComponentErogatore.close();
				}catch(Exception eClose){}
				try{
					dbDiagnosticErogatore.close();
				}catch(Exception eClose){}
			}

		}catch(Exception e){
			throw e;
		}finally{
			try{
				dbComponentErogatore.close();
			}catch(Exception eClose){}
			try{
				dbDiagnosticErogatore.close();
			}catch(Exception eClose){}
		}

		try{
			// Aspetto che anche il servizio PA processi con errore l'eventuale richiesta che riceve (Poi troverà broken pipe)
			Thread.sleep(2000);
		}catch(Exception e){}

		Date dataFineTest = DateManager.getDate();

		if(listErroriAttesi!=null && listErroriAttesi.size()>0){
			for (String motivoErrore : listErroriAttesi) {
				ErroreAttesoOpenSPCoopLogCore err = new ErroreAttesoOpenSPCoopLogCore();
				err.setIntervalloInferiore(dataInizioTest);
				err.setIntervalloSuperiore(dataFineTest);
				err.setMsgErrore(motivoErrore);
				this.erroriAttesiOpenSPCoopCore.add(err);
			}
		}

	}
	
	
	Repository repositoryStrutturaXMLErrataPortaApplicativa=new Repository();
	@Test(groups={SOAPMessageScorretti.ID_GRUPPO,
			SOAPMessageScorretti.ID_GRUPPO+".PA_XML_RICHIESTA",
			SOAPMessageScorretti.ID_GRUPPO+".STRUTTURA_XML_ERRATA_PA_RICHIESTA"},dataProvider="strutturaSoapErrata")
	public void strutturaXMLErrata_PortaApplicativa_Richiesta(byte[] messaggioXMLRichiesta,String identificativoTest,List<String> motivoErroreParser,List<String> listErroriAttesi)throws TestSuiteException,SOAPException, Exception{

		this.invocazionePA(messaggioXMLRichiesta, identificativoTest, motivoErroreParser, listErroriAttesi, 
				this.repositoryStrutturaXMLErrataPortaApplicativa, 
				CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO, 
				null,
				true,true);

	}
	
	Repository repositoryStrutturaXMLErrataPortaApplicativaStateful=new Repository();
	@Test(groups={SOAPMessageScorretti.ID_GRUPPO,
			SOAPMessageScorretti.ID_GRUPPO+".PA_XML_RICHIESTA",
			SOAPMessageScorretti.ID_GRUPPO+".STRUTTURA_XML_ERRATA_PA_STATEFUL_RICHIESTA"},dataProvider="strutturaSoapErrata")
	public void strutturaXMLErrata_PortaApplicativa_stateful_Richiesta(byte[] messaggioXMLRichiesta,String identificativoTest,List<String> motivoErroreParser,List<String> listErroriAttesi)throws TestSuiteException,SOAPException, Exception{

		this.invocazionePA(messaggioXMLRichiesta, identificativoTest, motivoErroreParser, listErroriAttesi, 
				this.repositoryStrutturaXMLErrataPortaApplicativaStateful, 
				CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO, 
				CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_STATEFUL,
				true,true);

	}
	
	Repository repositoryStrutturaXMLErrataPortaApplicativaCorrelazioneApplicativaContentBased=new Repository();
	@Test(groups={SOAPMessageScorretti.ID_GRUPPO,
			SOAPMessageScorretti.ID_GRUPPO+".PA_XML_RICHIESTA",
			SOAPMessageScorretti.ID_GRUPPO+".STRUTTURA_XML_ERRATA_PA_CORRELAZIONE_APPLICATIVA_CONTENT_BASED_RICHIESTA"},dataProvider="strutturaSoapErrata")
	public void strutturaXMLErrata_PortaApplicativa_correlazioneApplicativaContentBased_Richiesta(byte[] messaggioXMLRichiesta,String identificativoTest,List<String> motivoErroreParser,List<String> listErroriAttesi)throws TestSuiteException,SOAPException, Exception{

		listErroriAttesi.add("Riscontrato errore durante la correlazione applicativa");
		
		this.invocazionePA(messaggioXMLRichiesta, identificativoTest, motivoErroreParser, listErroriAttesi, 
				this.repositoryStrutturaXMLErrataPortaApplicativaCorrelazioneApplicativaContentBased, 
				CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO, 
				CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_TEST_CORRELAZIONE_APPLICATIVA_RISPOSTA_CONTENT_BASED_SOAP_STATELESS,
				true,true);

	}
	
	Repository repositoryStrutturaXMLErrataPortaApplicativaValidazioneContenutiApplicativi=new Repository();
	@Test(groups={SOAPMessageScorretti.ID_GRUPPO,
			SOAPMessageScorretti.ID_GRUPPO+".PA_XML_RICHIESTA",
			SOAPMessageScorretti.ID_GRUPPO+".STRUTTURA_XML_ERRATA_PA_VALIDAZIONE_CONTENUTI_RICHIESTA"},dataProvider="strutturaSoapErrata")
	public void strutturaXMLErrata_PortaApplicativa_validazioneContenutiApplicativi_Richiesta(byte[] messaggioXMLRichiesta,String identificativoTest,List<String> motivoErroreParser,List<String> listErroriAttesi)throws TestSuiteException,SOAPException, Exception{

		Date dataInizioTest = DateManager.getDate();
		
		listErroriAttesi.add("Messaggio con elementi non conformi alla definizione wsdl");
		listErroriAttesi.add("Riscontrato errore durante la validazione dei contenuti applicativi");
		listErroriAttesi.add("Validazione WSDL (true) fallita");
		listErroriAttesi.add("Messaggio con elementi non conformi alla definizione wsdl");
		
		try{
			this.invocazionePA(messaggioXMLRichiesta, identificativoTest, motivoErroreParser, null, 
					this.repositoryStrutturaXMLErrataPortaApplicativaValidazioneContenutiApplicativi, 
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_VAL_WSDL_GESTIONE_UTENTI_WDL, 
					CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_VALIDAZIONE_WSDL_OPENSPCOOP_AZIONE_REGISTRAZIONE_UTENTE_WDL,
					true,true);
		}catch(Throwable t){
			if("Chiusura SOAPEnvelope non Effettuata".equals(identificativoTest)){
				
				try{
					// Nel caso di processamento in streaming non si arriva ad avere l'errore
					// Provo a verificare di essere in questo caso
					this.invocazionePA(messaggioXMLRichiesta, identificativoTest, motivoErroreParser, null, 
							this.repositoryStrutturaXMLErrataPortaApplicativaValidazioneContenutiApplicativi, 
							CostantiTestSuite.SPCOOP_NOME_SERVIZIO_VAL_WSDL_GESTIONE_UTENTI_WDL, 
							CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_VALIDAZIONE_WSDL_OPENSPCOOP_AZIONE_REGISTRAZIONE_UTENTE_WDL,
							false,false);
				}catch(Throwable tInternal){
					// rilancio l'originale
					throw new RuntimeException(t);
				}
			}
			else{
				throw new RuntimeException(t);
			}
		}
		
		Date dataFineTest = DateManager.getDate();

		if(listErroriAttesi!=null && listErroriAttesi.size()>0){
			for (String motivoErrore : listErroriAttesi) {
				ErroreAttesoOpenSPCoopLogCore err = new ErroreAttesoOpenSPCoopLogCore();
				err.setIntervalloInferiore(dataInizioTest);
				err.setIntervalloSuperiore(dataFineTest);
				err.setMsgErrore(motivoErrore);
				this.erroriAttesiOpenSPCoopCore.add(err);
			}
		}
	}

	Repository repositoryStrutturaXMLErrataPortaApplicativaWSSecurityEncrypt=new Repository();
	@Test(groups={SOAPMessageScorretti.ID_GRUPPO,
			SOAPMessageScorretti.ID_GRUPPO+".PA_XML_RICHIESTA",
			SOAPMessageScorretti.ID_GRUPPO+".STRUTTURA_XML_ERRATA_PA_WSSECURITY_ENCRYPT_RICHIESTA"},dataProvider="strutturaSoapErrata")
	public void strutturaXMLErrata_PortaApplicativa_WSSecurityEncrypt_Richiesta(byte[] messaggioXMLRichiesta,String identificativoTest,List<String> motivoErroreParser,List<String> listErroriAttesi)throws TestSuiteException,SOAPException, Exception{

		Date dataInizioTest = DateManager.getDate();
		
		try{
			this.invocazionePA(messaggioXMLRichiesta, identificativoTest, motivoErroreParser, null, 
					this.repositoryStrutturaXMLErrataPortaApplicativaWSSecurityEncrypt, 
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO, 
					CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_WSS_ENCRYPT,
					true,true);
		}catch(Throwable t){
			if("Chiusura SOAPEnvelope non Effettuata".equals(identificativoTest) ||
					"Chiusura SOAPBody non Effettuata".equals(identificativoTest) ||
					"Primo elemento del Body non viene chiuso".equals(identificativoTest) ||
					"Elemento interno del Body non viene chiuso".equals(identificativoTest) ||
					"Elemento interno del Body non viene aperto".equals(identificativoTest) ||
					"Elemento grande (50K) con errore in mezzo del soap body".equals(identificativoTest) ||
					"Elemento grande (50K) con errore in fondo del soap body".equals(identificativoTest)){
				
				try{
					// Nel caso di processamento in streaming non si arriva ad avere l'errore
					// Provo a verificare di essere in questo caso
					this.invocazionePA(messaggioXMLRichiesta, identificativoTest, motivoErroreParser, null, 
							this.repositoryStrutturaXMLErrataPortaApplicativaWSSecurityEncrypt, 
							CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO, 
							CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_WSS_ENCRYPT,
							true,false);
				}catch(Throwable tInternal){
					// rilancio l'originale
					throw new RuntimeException(t);
				}
			}
			else{
				throw new RuntimeException(t);
			}
		}
		
		Date dataFineTest = DateManager.getDate();

		if(listErroriAttesi!=null && listErroriAttesi.size()>0){
			for (String motivoErrore : listErroriAttesi) {
				ErroreAttesoOpenSPCoopLogCore err = new ErroreAttesoOpenSPCoopLogCore();
				err.setIntervalloInferiore(dataInizioTest);
				err.setIntervalloSuperiore(dataFineTest);
				err.setMsgErrore(motivoErrore);
				this.erroriAttesiOpenSPCoopCore.add(err);
			}
		}
	}
	
	Repository repositoryStrutturaXMLErrataPortaApplicativaWSSecuritySignature=new Repository();
	@Test(groups={SOAPMessageScorretti.ID_GRUPPO,
			SOAPMessageScorretti.ID_GRUPPO+".PA_XML_RICHIESTA",
			SOAPMessageScorretti.ID_GRUPPO+".STRUTTURA_XML_ERRATA_PA_WSSECURITY_SIGNATURE_RICHIESTA"},dataProvider="strutturaSoapErrata")
	public void strutturaXMLErrata_PortaApplicativa_WSSecuritySignature_Richiesta(byte[] messaggioXMLRichiesta,String identificativoTest,List<String> motivoErroreParser,List<String> listErroriAttesi)throws TestSuiteException,SOAPException, Exception{

		Date dataInizioTest = DateManager.getDate();
		
		try{
			this.invocazionePA(messaggioXMLRichiesta, identificativoTest, motivoErroreParser, null, 
					this.repositoryStrutturaXMLErrataPortaApplicativaWSSecuritySignature, 
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO, 
					CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_WSS_SIGNATURE,
					true,true);
		}catch(Throwable t){
			if("Chiusura SOAPEnvelope non Effettuata".equals(identificativoTest) ||
					"Chiusura SOAPBody non Effettuata".equals(identificativoTest) ||
					"Primo elemento del Body non viene chiuso".equals(identificativoTest) ||
					"Elemento interno del Body non viene chiuso".equals(identificativoTest) ||
					"Elemento interno del Body non viene aperto".equals(identificativoTest) ||
					"Elemento grande (50K) con errore in mezzo del soap body".equals(identificativoTest) ||
					"Elemento grande (50K) con errore in fondo del soap body".equals(identificativoTest)){
				
				try{
					// Nel caso di processamento in streaming non si arriva ad avere l'errore
					// Provo a verificare di essere in questo caso
					this.invocazionePA(messaggioXMLRichiesta, identificativoTest, motivoErroreParser, null, 
							this.repositoryStrutturaXMLErrataPortaApplicativaWSSecuritySignature, 
							CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO, 
							CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_WSS_SIGNATURE,
							true,false);
				}catch(Throwable tInternal){
					// rilancio l'originale
					throw new RuntimeException(t);
				}
			}
			else{
				throw new RuntimeException(t);
			}
		}
		
		Date dataFineTest = DateManager.getDate();

		if(listErroriAttesi!=null && listErroriAttesi.size()>0){
			for (String motivoErrore : listErroriAttesi) {
				ErroreAttesoOpenSPCoopLogCore err = new ErroreAttesoOpenSPCoopLogCore();
				err.setIntervalloInferiore(dataInizioTest);
				err.setIntervalloSuperiore(dataFineTest);
				err.setMsgErrore(motivoErrore);
				this.erroriAttesiOpenSPCoopCore.add(err);
			}
		}

	}

	
	
	
	
	
	
	
	
	




	private void invocazionePA_bustaSintatticamenteErrata(byte[] messaggioXMLRichiesta,String identificativoTest,List<String> motivoErroreParser,List<String> listErroriAttesi,
			Repository repository,String servizio, String azione,
			boolean erroreValidazione,boolean checkDiagnostico)throws TestSuiteException,SOAPException, Exception{

		Date dataInizioTest = DateManager.getDate();

		DatabaseComponent dbComponentErogatore = null;
		DatabaseMsgDiagnosticiComponent dbDiagnosticErogatore = null;
		try{
			
			String msgXmlRichiesta = new String(messaggioXMLRichiesta);
			msgXmlRichiesta = msgXmlRichiesta.replace(TEMPLATE_HEADER, this.getHeaderBusta(servizio,azione));
			msgXmlRichiesta = msgXmlRichiesta.replace("<eGov_IT:Mittente>", "<eGov_IT:Mittente"); // imposto struttura xml errata
			
			
			Reporter.log("Test ["+identificativoTest+"]");
			ClientHttpGenerico client=new ClientHttpGenerico(repository);
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneBusteErogatore());
			client.connectToSoapEngine();
			client.setMessaggioXMLRichiesta(msgXmlRichiesta.getBytes());
			client.setConnectionReadTimeout(READ_TIMEOUT);
			client.setRispostaDaGestire(true);
			client.setForceResponseAsSOAPProcessor(true); // per lanciare l'axis fault
			dbDiagnosticErogatore = DatabaseProperties.getDatabaseComponentDiagnosticaErogatore();
			// AttesaTerminazioneMessaggi
			if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()){
				dbComponentErogatore = DatabaseProperties.getDatabaseComponentErogatore();
					
				client.setAttesaTerminazioneMessaggi(true);
				client.setDbAttesaTerminazioneMessaggiErogatore(dbComponentErogatore);
			}

			try {
				client.run();
				throw new Exception("Attesa eccezione per xml errato ("+identificativoTest+")");
			} catch (AxisFault error) {
				
				try{
					Thread.sleep(2000); // aspetto scrittura diagnostici in streaming che avviene dopo
				}catch(Exception e){}
				
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				String msgErrore = null;
				String codErrore =  null;
				if(erroreValidazione){
					codErrore = SPCoopCostanti.FAULT_CODE_VALIDAZIONE_SPCOOP;
					msgErrore = SPCoopCostanti.FAULT_STRING_VALIDAZIONE_SPCOOP;
				}
				else{
					codErrore = SPCoopCostanti.FAULT_CODE_PROCESSAMENTO_SPCOOP;
					msgErrore = SPCoopCostanti.FAULT_STRING_PROCESSAMENTO_SPCOOP;
				}
				Reporter.log("Controllo fault code ["+codErrore+"]");
				Assert.assertTrue(codErrore.equals(error.getFaultCode().getLocalPart()) ||
						codErrore.equals("soap:"+error.getFaultCode().getLocalPart()) ||
						codErrore.equals("soapenv:"+error.getFaultCode().getLocalPart()));
				Reporter.log("Controllo fault string ["+msgErrore+"]");
				Assert.assertTrue(msgErrore.equals(error.getFaultString()));

				if(checkDiagnostico){
					SimpleDateFormat dateformat = new SimpleDateFormat ("yyyy-MM-dd_HH:mm:ss.SSS");
					Reporter.log("Check diagnostico 004071 rispetto a data ["+dateformat.format(dataInizioTest)+"]");
					Assert.assertTrue(dbDiagnosticErogatore.isTracedCodice(dataInizioTest, "004071"));
					
					boolean faultStringMatch = false;
					for (String erroreParser : motivoErroreParser) {
						boolean match = dbDiagnosticErogatore.isTracedMessaggioWithLike(dataInizioTest, erroreParser);
						Reporter.log("Check diagnostico rispetto a ["+erroreParser+"]: "+match);
						if(match){
							faultStringMatch = true;
							break;
						}
					}
					Assert.assertTrue(faultStringMatch); // il messaggio di qualsiasi parser dovrebbe cmq indicare il motivo del perche' il msg non e' corretto
				}
				
			}finally{
				try{
					dbComponentErogatore.close();
				}catch(Exception eClose){}
				try{
					dbDiagnosticErogatore.close();
				}catch(Exception eClose){}
			}

		}catch(Exception e){
			throw e;
		}finally{
			try{
				dbComponentErogatore.close();
			}catch(Exception eClose){}
			try{
				dbDiagnosticErogatore.close();
			}catch(Exception eClose){}
		}

		try{
			// Aspetto che anche il servizio PA processi con errore l'eventuale richiesta che riceve (Poi troverà broken pipe)
			Thread.sleep(2000);
		}catch(Exception e){}

		Date dataFineTest = DateManager.getDate();

		if(listErroriAttesi!=null && listErroriAttesi.size()>0){
			for (String motivoErrore : listErroriAttesi) {
				ErroreAttesoOpenSPCoopLogCore err = new ErroreAttesoOpenSPCoopLogCore();
				err.setIntervalloInferiore(dataInizioTest);
				err.setIntervalloSuperiore(dataFineTest);
				err.setMsgErrore(motivoErrore);
				this.erroriAttesiOpenSPCoopCore.add(err);
			}
		}

	}
	
	
	Repository repositoryStrutturaXMLErrataPortaApplicativa_bustaSintatticamenteErrata=new Repository();
	@Test(groups={SOAPMessageScorretti.ID_GRUPPO,
			SOAPMessageScorretti.ID_GRUPPO+".PA_XML_RICHIESTA",
			SOAPMessageScorretti.ID_GRUPPO+".BUSTA_ERRATA_PA_RICHIESTA"},dataProvider="strutturaSoapErrata")
	public void strutturaXMLErrata_PortaApplicativa_Richiesta_bustaSintatticamenteErrata(byte[] messaggioXMLRichiesta,String identificativoTest,
			List<String> motivoErroreParser,List<String> listErroriAttesi)throws TestSuiteException,SOAPException, Exception{

		motivoErroreParser.add("Unexpected character '<'");
		listErroriAttesi.add("Unexpected character '<'");
		
		motivoErroreParser.add("Element type \"eGov_IT:Mittente\" must be followed by either attribute specifications, \">\" or \"/>\"");
		listErroriAttesi.add("Element type \"eGov_IT:Mittente\" must be followed by either attribute specifications, \">\" or \"/>\"");
		
		this.invocazionePA_bustaSintatticamenteErrata(messaggioXMLRichiesta, identificativoTest, motivoErroreParser, listErroriAttesi, 
				this.repositoryStrutturaXMLErrataPortaApplicativa_bustaSintatticamenteErrata, 
				CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO, 
				null,
				true,true);

	}
	
	









	
	
	
	/**
	 * Messaggio applicativo di risposta ottenuto dal servizio applicativo malformato
	 */
	Repository repositoryStrutturaXMLErratoPA_RispostaApplicativa=new Repository();


	@Test(groups={SOAPMessageScorretti.ID_GRUPPO,
			SOAPMessageScorretti.ID_GRUPPO+".PA_XML_RISPOSTA",
			SOAPMessageScorretti.ID_GRUPPO+".RISPOSTA_APPLICATIVA_XML_ERRATO_PA",
			SOAPMessageScorretti.ID_GRUPPO+".RISPOSTA_APPLICATIVA_XML_ERRATO_PA_CASO1"})
	public void strutturaXMLErrata_PA_RispostaApplicativa_Body()throws TestSuiteException,SOAPException, Exception{
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
				ecc.setCodice(Utilities.toString(CodiceErroreIntegrazione.CODICE_440_PARSING_EXCEPTION_RISPOSTA));
				ecc.setDescrizione("Unexpected close tag");
				ecc.setCheckDescrizioneTramiteMatchEsatto(false);
				eccezioni.add(ecc);
				
				org.openspcoop2.protocol.spcoop.testsuite.core.OpenSPCoopDetail ecc2 = new org.openspcoop2.protocol.spcoop.testsuite.core.OpenSPCoopDetail();
				ecc2.setCodice(Utilities.toString(CodiceErroreIntegrazione.CODICE_440_PARSING_EXCEPTION_RISPOSTA));
				ecc2.setDescrizione("The element type \"soapenv:Body\" must be terminated by the matching end-tag \"</soapenv:Body>\"");
				ecc2.setCheckDescrizioneTramiteMatchEsatto(false);
				eccezioni.add(ecc2);

				Assert.assertTrue(Utilities.existsOpenSPCoopDetails(error)); 
				String[] identificativiFunzione = new String[2];
				identificativiFunzione[0] = "MinisteroErogatoreSPCoopIT";
				identificativiFunzione[1] = "RicezioneBusteSOAP";
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
		err.setMsgErrore("Errore avvenuto durante la consegna HTTP (Parsing Risposta SOAP)");
		this.erroriAttesiOpenSPCoopCore.add(err);

		ErroreAttesoOpenSPCoopLogCore err2 = new ErroreAttesoOpenSPCoopLogCore();
		err2.setIntervalloInferiore(dataInizioTest);
		err2.setIntervalloSuperiore(dataFineTest);
		err2.setMsgErrore("parsingExceptionRisposta");
		this.erroriAttesiOpenSPCoopCore.add(err2);
		
		ErroreAttesoOpenSPCoopLogCore err3 = new ErroreAttesoOpenSPCoopLogCore();
		err3.setIntervalloInferiore(dataInizioTest);
		err3.setIntervalloSuperiore(dataFineTest);
		err3.setMsgErrore("ErroreGenerale");
		this.erroriAttesiOpenSPCoopCore.add(err3);
		
		ErroreAttesoOpenSPCoopLogCore err5 = new ErroreAttesoOpenSPCoopLogCore();
		err5.setIntervalloInferiore(dataInizioTest);
		err5.setIntervalloSuperiore(dataFineTest);
		err5.setMsgErrore("SendResponse error");
		this.erroriAttesiOpenSPCoopCore.add(err5);

	}

	@Test(groups={SOAPMessageScorretti.ID_GRUPPO,
			SOAPMessageScorretti.ID_GRUPPO+".PA_XML_RISPOSTA",
			SOAPMessageScorretti.ID_GRUPPO+".RISPOSTA_APPLICATIVA_XML_ERRATO_PA",
			SOAPMessageScorretti.ID_GRUPPO+".RISPOSTA_APPLICATIVA_XML_ERRATO_PA_CASO2"})
	public void strutturaXMLErrata_PA_RispostaApplicativa_BodyFirstChild()throws TestSuiteException,SOAPException, Exception{
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
				ecc.setCodice(Utilities.toString(CodiceErroreIntegrazione.CODICE_440_PARSING_EXCEPTION_RISPOSTA));
				ecc.setDescrizione("Unexpected close tag");
				ecc.setCheckDescrizioneTramiteMatchEsatto(false);
				eccezioni.add(ecc);
				
				org.openspcoop2.protocol.spcoop.testsuite.core.OpenSPCoopDetail ecc2 = new org.openspcoop2.protocol.spcoop.testsuite.core.OpenSPCoopDetail();
				ecc2.setCodice(Utilities.toString(CodiceErroreIntegrazione.CODICE_440_PARSING_EXCEPTION_RISPOSTA));
				ecc2.setDescrizione("The end-tag for element type \"helloworld\" must end with a '>' delimiter");
				ecc2.setCheckDescrizioneTramiteMatchEsatto(false);
				eccezioni.add(ecc2);

				Assert.assertTrue(Utilities.existsOpenSPCoopDetails(error));
				String[] identificativiFunzione = new String[2];
				identificativiFunzione[0] = "MinisteroErogatoreSPCoopIT";
				identificativiFunzione[1] = "RicezioneBusteSOAP";
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
		err.setMsgErrore("Errore avvenuto durante la consegna HTTP (Parsing Risposta SOAP)");
		this.erroriAttesiOpenSPCoopCore.add(err);

		ErroreAttesoOpenSPCoopLogCore err2 = new ErroreAttesoOpenSPCoopLogCore();
		err2.setIntervalloInferiore(dataInizioTest);
		err2.setIntervalloSuperiore(dataFineTest);
		err2.setMsgErrore("parsingExceptionRisposta");
		this.erroriAttesiOpenSPCoopCore.add(err2);
		
		ErroreAttesoOpenSPCoopLogCore err3 = new ErroreAttesoOpenSPCoopLogCore();
		err3.setIntervalloInferiore(dataInizioTest);
		err3.setIntervalloSuperiore(dataFineTest);
		err3.setMsgErrore("ErroreGenerale");
		this.erroriAttesiOpenSPCoopCore.add(err3);
		
		ErroreAttesoOpenSPCoopLogCore err5 = new ErroreAttesoOpenSPCoopLogCore();
		err5.setIntervalloInferiore(dataInizioTest);
		err5.setIntervalloSuperiore(dataFineTest);
		err5.setMsgErrore("SendResponse error");
		this.erroriAttesiOpenSPCoopCore.add(err5);
	}

	@Test(groups={SOAPMessageScorretti.ID_GRUPPO,
			SOAPMessageScorretti.ID_GRUPPO+".PA_XML_RISPOSTA",
			SOAPMessageScorretti.ID_GRUPPO+".RISPOSTA_APPLICATIVA_XML_ERRATO_PA",
			SOAPMessageScorretti.ID_GRUPPO+".RISPOSTA_APPLICATIVA_XML_ERRATO_PA_CASO3"})
	public void strutturaXMLErrata_PA_RispostaApplicativa_InsideBody()throws TestSuiteException,SOAPException, Exception{
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
				ecc.setCodice(Utilities.toString(CodiceErroreIntegrazione.CODICE_440_PARSING_EXCEPTION_RISPOSTA));
				ecc.setDescrizione("Unexpected close tag");
				ecc.setCheckDescrizioneTramiteMatchEsatto(false);
				eccezioni.add(ecc);
				
				org.openspcoop2.protocol.spcoop.testsuite.core.OpenSPCoopDetail ecc2 = new org.openspcoop2.protocol.spcoop.testsuite.core.OpenSPCoopDetail();
				ecc2.setCodice(Utilities.toString(CodiceErroreIntegrazione.CODICE_440_PARSING_EXCEPTION_RISPOSTA));
				ecc2.setDescrizione("The end-tag for element type \"b\" must end with a '>' delimiter");
				ecc2.setCheckDescrizioneTramiteMatchEsatto(false);
				eccezioni.add(ecc2);

				Assert.assertTrue(Utilities.existsOpenSPCoopDetails(error)); 
				String[] identificativiFunzione = new String[2];
				identificativiFunzione[0] = "MinisteroErogatoreSPCoopIT";
				identificativiFunzione[1] = "RicezioneBusteSOAP";
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
		err.setMsgErrore("Errore avvenuto durante la consegna HTTP (Parsing Risposta SOAP)");
		this.erroriAttesiOpenSPCoopCore.add(err);

		ErroreAttesoOpenSPCoopLogCore err2 = new ErroreAttesoOpenSPCoopLogCore();
		err2.setIntervalloInferiore(dataInizioTest);
		err2.setIntervalloSuperiore(dataFineTest);
		err2.setMsgErrore("parsingExceptionRisposta");
		this.erroriAttesiOpenSPCoopCore.add(err2);
		
		ErroreAttesoOpenSPCoopLogCore err3 = new ErroreAttesoOpenSPCoopLogCore();
		err3.setIntervalloInferiore(dataInizioTest);
		err3.setIntervalloSuperiore(dataFineTest);
		err3.setMsgErrore("ErroreGenerale");
		this.erroriAttesiOpenSPCoopCore.add(err3);
		
		ErroreAttesoOpenSPCoopLogCore err5 = new ErroreAttesoOpenSPCoopLogCore();
		err5.setIntervalloInferiore(dataInizioTest);
		err5.setIntervalloSuperiore(dataFineTest);
		err5.setMsgErrore("SendResponse error");
		this.erroriAttesiOpenSPCoopCore.add(err5);
	}

	
	
	
	
	
	
	
	
	
	
	
	
	



	/**
	 * Messaggio applicativo di risposta ottenuto dal servizio applicativo malformato in modalita stateful
	 */
	Repository repositoryStrutturaXMLErratoPA_Stateful_RispostaApplicativa=new Repository();

	@Test(groups={SOAPMessageScorretti.ID_GRUPPO,
			SOAPMessageScorretti.ID_GRUPPO+".PA_XML_RISPOSTA",
			SOAPMessageScorretti.ID_GRUPPO+".RISPOSTA_APPLICATIVA_XML_ERRATO_PA_STATEFUL",
			SOAPMessageScorretti.ID_GRUPPO+".RISPOSTA_APPLICATIVA_XML_ERRATO_PA_STATEFUL_CASO1"})
	public void strutturaXMLErrata_PA_RispostaApplicativa_Stateful_Body()throws TestSuiteException,SOAPException, Exception{

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

                                org.openspcoop2.protocol.spcoop.testsuite.core.OpenSPCoopDetail ecc3 = new org.openspcoop2.protocol.spcoop.testsuite.core.OpenSPCoopDetail();
                                ecc3.setCodice(Utilities.toString(CodiceErroreIntegrazione.CODICE_440_PARSING_EXCEPTION_RISPOSTA));
                                ecc3.setDescrizione("The element type \"soapenv:Body\" must be terminated by the matching end-tag");
                                ecc3.setCheckDescrizioneTramiteMatchEsatto(false);
                                eccezioni.add(ecc3);

				Assert.assertTrue(Utilities.existsOpenSPCoopDetails(error)); 
                                String[] identificativiFunzione = new String[2];
                                identificativiFunzione[0] = "ConsegnaContenutiApplicativi";
                                identificativiFunzione[1] = "RicezioneBusteSOAP";
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

                ErroreAttesoOpenSPCoopLogCore err2a = new ErroreAttesoOpenSPCoopLogCore();
                err2a.setIntervalloInferiore(dataInizioTest);
                err2a.setIntervalloSuperiore(dataFineTest);
                err2a.setMsgErrore("parsingExceptionRisposta");
                this.erroriAttesiOpenSPCoopCore.add(err2a);

		ErroreAttesoOpenSPCoopLogCore err2 = new ErroreAttesoOpenSPCoopLogCore();
		err2.setIntervalloInferiore(dataInizioTest);
		err2.setIntervalloSuperiore(dataFineTest);
		err2.setMsgErrore("Errore avvenuto durante la consegna HTTP (Parsing Risposta SOAP)");
		this.erroriAttesiOpenSPCoopCore.add(err2);

		ErroreAttesoOpenSPCoopLogCore err3 = new ErroreAttesoOpenSPCoopLogCore();
		err3.setIntervalloInferiore(dataInizioTest);
		err3.setIntervalloSuperiore(dataFineTest);
		err3.setMsgErrore("Registrazione messaggio di risposta nel RepositoryMessaggi non riuscita.");
		this.erroriAttesiOpenSPCoopCore.add(err3);
		
		ErroreAttesoOpenSPCoopLogCore err4 = new ErroreAttesoOpenSPCoopLogCore();
		err4.setIntervalloInferiore(dataInizioTest);
		err4.setIntervalloSuperiore(dataFineTest);
		err4.setMsgErrore("Unexpected close tag");
		this.erroriAttesiOpenSPCoopCore.add(err4);
		
		ErroreAttesoOpenSPCoopLogCore err5 = new ErroreAttesoOpenSPCoopLogCore();
		err5.setIntervalloInferiore(dataInizioTest);
		err5.setIntervalloSuperiore(dataFineTest);
		err5.setMsgErrore("SendResponse error");
		this.erroriAttesiOpenSPCoopCore.add(err5);
		
	}

	@Test(groups={SOAPMessageScorretti.ID_GRUPPO,
			SOAPMessageScorretti.ID_GRUPPO+".PA_XML_RISPOSTA",
			SOAPMessageScorretti.ID_GRUPPO+".RISPOSTA_APPLICATIVA_XML_ERRATO_PA_STATEFUL",
			SOAPMessageScorretti.ID_GRUPPO+".RISPOSTA_APPLICATIVA_XML_ERRATO_PA_STATEFUL_CASO2"})
	public void strutturaXMLErrata_PA_RispostaApplicativa_Stateful_BodyFirstChild()throws TestSuiteException,SOAPException, Exception{
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

                                org.openspcoop2.protocol.spcoop.testsuite.core.OpenSPCoopDetail ecc3 = new org.openspcoop2.protocol.spcoop.testsuite.core.OpenSPCoopDetail();
                                ecc3.setCodice(Utilities.toString(CodiceErroreIntegrazione.CODICE_440_PARSING_EXCEPTION_RISPOSTA));
                                ecc3.setDescrizione("The end-tag for element type \"helloworld\" must end with a '>' delimiter");
                                ecc3.setCheckDescrizioneTramiteMatchEsatto(false);
                                eccezioni.add(ecc3);

				Assert.assertTrue(Utilities.existsOpenSPCoopDetails(error));
                                String[] identificativiFunzione = new String[2];
                                identificativiFunzione[0] = "ConsegnaContenutiApplicativi";
                                identificativiFunzione[1] = "RicezioneBusteSOAP";
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

                ErroreAttesoOpenSPCoopLogCore err2a = new ErroreAttesoOpenSPCoopLogCore();
                err2a.setIntervalloInferiore(dataInizioTest);
                err2a.setIntervalloSuperiore(dataFineTest);
                err2a.setMsgErrore("parsingExceptionRisposta");
                this.erroriAttesiOpenSPCoopCore.add(err2a);

		ErroreAttesoOpenSPCoopLogCore err2 = new ErroreAttesoOpenSPCoopLogCore();
		err2.setIntervalloInferiore(dataInizioTest);
		err2.setIntervalloSuperiore(dataFineTest);
		err2.setMsgErrore("Errore avvenuto durante la consegna HTTP (Parsing Risposta SOAP)");
		this.erroriAttesiOpenSPCoopCore.add(err2);

		ErroreAttesoOpenSPCoopLogCore err3 = new ErroreAttesoOpenSPCoopLogCore();
		err3.setIntervalloInferiore(dataInizioTest);
		err3.setIntervalloSuperiore(dataFineTest);
		err3.setMsgErrore("Registrazione messaggio di risposta nel RepositoryMessaggi non riuscita.");
		this.erroriAttesiOpenSPCoopCore.add(err3);
		
		ErroreAttesoOpenSPCoopLogCore err4 = new ErroreAttesoOpenSPCoopLogCore();
		err4.setIntervalloInferiore(dataInizioTest);
		err4.setIntervalloSuperiore(dataFineTest);
		err4.setMsgErrore("Unexpected close tag");
		this.erroriAttesiOpenSPCoopCore.add(err4);
		
		ErroreAttesoOpenSPCoopLogCore err5 = new ErroreAttesoOpenSPCoopLogCore();
		err5.setIntervalloInferiore(dataInizioTest);
		err5.setIntervalloSuperiore(dataFineTest);
		err5.setMsgErrore("SendResponse error");
		this.erroriAttesiOpenSPCoopCore.add(err5);
	}

	@Test(groups={SOAPMessageScorretti.ID_GRUPPO,
			SOAPMessageScorretti.ID_GRUPPO+".PA_XML_RISPOSTA",
			SOAPMessageScorretti.ID_GRUPPO+".RISPOSTA_APPLICATIVA_XML_ERRATO_PA_STATEFUL",
			SOAPMessageScorretti.ID_GRUPPO+".RISPOSTA_APPLICATIVA_XML_ERRATO_PA_STATEFUL_CASO3"})
	public void strutturaXMLErrata_PA_RispostaApplicativa_Stateful_InsideBody()throws TestSuiteException,SOAPException, Exception{
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

                                org.openspcoop2.protocol.spcoop.testsuite.core.OpenSPCoopDetail ecc3 = new org.openspcoop2.protocol.spcoop.testsuite.core.OpenSPCoopDetail();
                                ecc3.setCodice(Utilities.toString(CodiceErroreIntegrazione.CODICE_440_PARSING_EXCEPTION_RISPOSTA));
                                ecc3.setDescrizione("The end-tag for element type \"b\" must end with a '>' delimiter");
                                ecc3.setCheckDescrizioneTramiteMatchEsatto(false);
                                eccezioni.add(ecc3);

				Assert.assertTrue(Utilities.existsOpenSPCoopDetails(error));
                                String[] identificativiFunzione = new String[2];
                                identificativiFunzione[0] = "ConsegnaContenutiApplicativi";
                                identificativiFunzione[1] = "RicezioneBusteSOAP";
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

                ErroreAttesoOpenSPCoopLogCore err2a = new ErroreAttesoOpenSPCoopLogCore();
                err2a.setIntervalloInferiore(dataInizioTest);
                err2a.setIntervalloSuperiore(dataFineTest);
                err2a.setMsgErrore("parsingExceptionRisposta");
                this.erroriAttesiOpenSPCoopCore.add(err2a);

		ErroreAttesoOpenSPCoopLogCore err2 = new ErroreAttesoOpenSPCoopLogCore();
		err2.setIntervalloInferiore(dataInizioTest);
		err2.setIntervalloSuperiore(dataFineTest);
		err2.setMsgErrore("Errore avvenuto durante la consegna HTTP (Parsing Risposta SOAP)");
		this.erroriAttesiOpenSPCoopCore.add(err2);

		ErroreAttesoOpenSPCoopLogCore err3 = new ErroreAttesoOpenSPCoopLogCore();
		err3.setIntervalloInferiore(dataInizioTest);
		err3.setIntervalloSuperiore(dataFineTest);
		err3.setMsgErrore("Registrazione messaggio di risposta nel RepositoryMessaggi non riuscita.");
		this.erroriAttesiOpenSPCoopCore.add(err3);
		
		ErroreAttesoOpenSPCoopLogCore err4 = new ErroreAttesoOpenSPCoopLogCore();
		err4.setIntervalloInferiore(dataInizioTest);
		err4.setIntervalloSuperiore(dataFineTest);
		err4.setMsgErrore("Unexpected close tag");
		this.erroriAttesiOpenSPCoopCore.add(err4);
		
		ErroreAttesoOpenSPCoopLogCore err5 = new ErroreAttesoOpenSPCoopLogCore();
		err5.setIntervalloInferiore(dataInizioTest);
		err5.setIntervalloSuperiore(dataFineTest);
		err5.setMsgErrore("SendResponse error");
		this.erroriAttesiOpenSPCoopCore.add(err5);
	}


	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * Body errato ritornato dalla PdD Destinataria
	 */
	Repository repositoryStrutturaXMLBodyRispostaPdDErrato=new Repository();
	@Test(groups={SOAPMessageScorretti.ID_GRUPPO,
			SOAPMessageScorretti.ID_GRUPPO+".PD_XML_RISPOSTA",
			SOAPMessageScorretti.ID_GRUPPO+".STRUTTURA_XML_ERRATA_PD_RISPOSTA_PDD"})
	public void strutturaXMLBodyRispostaPdDErrato_PD()throws TestSuiteException,SOAPException, Exception{
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
		
		ErroreAttesoOpenSPCoopLogCore err5 = new ErroreAttesoOpenSPCoopLogCore();
		err5.setIntervalloInferiore(dataInizioTest);
		err5.setIntervalloSuperiore(dataFineTest);
		err5.setMsgErrore("SendResponse error");
		this.erroriAttesiOpenSPCoopCore.add(err5);


	}

	/**
	 * Header eGov errato ritornato dalla PdD Destinataria
	 */
	Repository repositoryStrutturaXMLHeaderRispostaPdDErrato=new Repository();
	@Test(groups={SOAPMessageScorretti.ID_GRUPPO,
			SOAPMessageScorretti.ID_GRUPPO+".PD_XML_RISPOSTA",
			SOAPMessageScorretti.ID_GRUPPO+".STRUTTURA_XML_ERRATA_PD_RISPOSTA_HEADER_PDD"})
	public void strutturaXMLHeaderRispostaPdDErrato_PD()throws TestSuiteException,SOAPException, Exception{
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
				if(Utilities.toString(CodiceErroreIntegrazione.CODICE_440_PARSING_EXCEPTION_RISPOSTA).equals(error.getFaultCode().getLocalPart())){
					Reporter.log("Controllo fault code. Atteso ["+Utilities.toString(CodiceErroreIntegrazione.CODICE_440_PARSING_EXCEPTION_RISPOSTA)+"] - Trovato [" + error.getFaultCode().getLocalPart() + "]");
					Assert.assertTrue(Utilities.toString(CodiceErroreIntegrazione.CODICE_440_PARSING_EXCEPTION_RISPOSTA).equals(error.getFaultCode().getLocalPart()));
					String msgErrore = "Unexpected character '<'";
					String msgErrore2 = "Element type \"eGov_IT:Mittente\" must be followed by either attribute specifications, \">\" or \"/>\"";
					Reporter.log("Controllo fault string. Trovato [" + error.getFaultString() + "]");
					Assert.assertTrue(error.getFaultString().contains(msgErrore) || error.getFaultString().contains(msgErrore2));
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
		err.setMsgErrore("Errore avvenuto durante la consegna HTTP (Parsing Risposta SOAP)");
		this.erroriAttesiOpenSPCoopCore.add(err);

		ErroreAttesoOpenSPCoopLogCore err2 = new ErroreAttesoOpenSPCoopLogCore();
		err2.setIntervalloInferiore(dataInizioTest);
		err2.setIntervalloSuperiore(dataFineTest);
		err2.setMsgErrore("Unexpected character '<' (code 60) excepted space, or '>' or \"/>\"");
		this.erroriAttesiOpenSPCoopCore.add(err2);
		
		ErroreAttesoOpenSPCoopLogCore err3 = new ErroreAttesoOpenSPCoopLogCore();
		err3.setIntervalloInferiore(dataInizioTest);
		err3.setIntervalloSuperiore(dataFineTest);
		err3.setMsgErrore("parsingExceptionRisposta");
		this.erroriAttesiOpenSPCoopCore.add(err3);
		
		ErroreAttesoOpenSPCoopLogCore err4 = new ErroreAttesoOpenSPCoopLogCore();
		err4.setIntervalloInferiore(dataInizioTest);
		err4.setIntervalloSuperiore(dataFineTest);
		err4.setMsgErrore("SendResponse error");
		this.erroriAttesiOpenSPCoopCore.add(err4);
		

	}
	
	
	
	
	/**
	 * ContentType errato ritornato dalla PdD Destinataria
	 */
	Repository repositoryStrutturaContentTypeRispostaPdDErrato=new Repository();
	@Test(groups={SOAPMessageScorretti.ID_GRUPPO,
			SOAPMessageScorretti.ID_GRUPPO+".PD_XML_RISPOSTA",
			SOAPMessageScorretti.ID_GRUPPO+".STRUTTURA_CONTENT_TYPE_ERRATO_PD_RISPOSTA_PDD"})
	public void strutturaContentTypeRispostaPdDErrato_PD()throws TestSuiteException,SOAPException, Exception{
		Date dataInizioTest = DateManager.getDate();

		// costruzione busta
		String xml = FileSystemUtilities.readFile(Utilities.testSuiteProperties.getSoap11FileName());
		Message msg=new Message(new ByteArrayInputStream(xml.getBytes()));
		msg.getSOAPPartAsBytes();

		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryStrutturaContentTypeRispostaPdDErrato);
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_PROFILO_SINCRONO_CONTENT_TYPE_ERRATO_HEADER_RISPOSTA_PDD);
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
				if(Utilities.toString(CodiceErroreIntegrazione.CODICE_440_PARSING_EXCEPTION_RISPOSTA).equals(error.getFaultCode().getLocalPart())){
					Reporter.log("Controllo fault code. Atteso ["+Utilities.toString(CodiceErroreIntegrazione.CODICE_440_PARSING_EXCEPTION_RISPOSTA)+"] - Trovato [" + error.getFaultCode().getLocalPart() + "]");
					Assert.assertTrue(Utilities.toString(CodiceErroreIntegrazione.CODICE_440_PARSING_EXCEPTION_RISPOSTA).equals(error.getFaultCode().getLocalPart()));
					String msgErrore = "Il valore dell'header HTTP Content-Type definito nell'http reply (text/ERRATO_CT)";
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
		err.setMsgErrore("Il valore dell'header HTTP Content-Type definito nell'http reply (text/ERRATO_CT)");
		this.erroriAttesiOpenSPCoopCore.add(err);

		ErroreAttesoOpenSPCoopLogCore err2 = new ErroreAttesoOpenSPCoopLogCore();
		err2.setIntervalloInferiore(dataInizioTest);
		err2.setIntervalloSuperiore(dataFineTest);
		err2.setMsgErrore("Unable to retrive SOAP Version");
		this.erroriAttesiOpenSPCoopCore.add(err2);
		
		ErroreAttesoOpenSPCoopLogCore err3 = new ErroreAttesoOpenSPCoopLogCore();
		err3.setIntervalloInferiore(dataInizioTest);
		err3.setIntervalloSuperiore(dataFineTest);
		err3.setMsgErrore("parsingExceptionRisposta");
		this.erroriAttesiOpenSPCoopCore.add(err3);
				
	}
	
	
	
	
	
	
	
	
	
	@Test(groups={SOAPMessageScorretti.ID_GRUPPO,SOAPMessageScorretti.ID_GRUPPO+".INTEGRATION_MANAGER"},dataProvider="strutturaSoapErrata")
	public void strutturaXMLErrata_integrationManager(byte[] messaggioXMLRichiesta,String identificativoTest,List<String> motivoErroreParser,List<String> listErroriAttesi)throws TestSuiteException,SOAPException, Exception{

		Date dataInizioTest = DateManager.getDate();
		
		try{
			
			Reporter.log("Effettuo invocazione per riferimento");
			org.openspcoop2.pdd.services.axis14.PD_PortType im = 
				IntegrationManager.getIntegrationManagerPD_axis14(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore().replace("PD","IntegrationManager/PD"),
						"adminSilX", "123456");
			IntegrationManagerMessage msg = new IntegrationManagerMessage();
			msg.setMessage(messaggioXMLRichiesta);
			im.invocaPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_AUTENTICAZIONE_BASIC, msg);
			
			throw new Exception("Metodo invocaPortaDelegata non ha causato errori");
		}catch(IntegrationManagerException e){
			verificaSPCoopException_421(e);
		}
			
		try{
			// Aspetto che anche il servizio PA processi con errore l'eventuale richiesta che riceve (Poi troverà broken pipe)
			Thread.sleep(2000);
		}catch(Exception e){}

		Date dataFineTest = DateManager.getDate();

		if(listErroriAttesi!=null && listErroriAttesi.size()>0){
			for (String motivoErrore : listErroriAttesi) {
				ErroreAttesoOpenSPCoopLogCore err = new ErroreAttesoOpenSPCoopLogCore();
				err.setIntervalloInferiore(dataInizioTest);
				err.setIntervalloSuperiore(dataFineTest);
				err.setMsgErrore(motivoErrore);
				this.erroriAttesiOpenSPCoopCore.add(err);
			}
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
}
