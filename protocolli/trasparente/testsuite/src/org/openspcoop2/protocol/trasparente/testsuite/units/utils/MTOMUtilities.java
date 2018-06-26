/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it).
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
package org.openspcoop2.protocol.trasparente.testsuite.units.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.xml.transform.dom.DOMSource;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.handler.MessageContext;

import org.apache.axis.Message;
import org.apache.commons.io.input.ReaderInputStream;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.example.server.mtom.Echo;
import org.openspcoop2.example.server.mtom.EchoResponse;
import org.openspcoop2.example.server.mtom.ws.MTOMServiceExample;
import org.openspcoop2.example.server.mtom.ws.MTOMServiceExampleSOAP11Service;
import org.openspcoop2.example.server.mtom.ws.MTOMServiceExampleSOAP12Service;
import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.protocol.sdk.constants.Inoltro;
import org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione;
import org.openspcoop2.protocol.sdk.constants.TipoOraRegistrazione;
import org.openspcoop2.protocol.trasparente.testsuite.core.CooperazioneTrasparenteBase;
import org.openspcoop2.protocol.trasparente.testsuite.core.CostantiTestSuite;
import org.openspcoop2.protocol.trasparente.testsuite.core.DatabaseProperties;
import org.openspcoop2.testsuite.axis14.Axis14SoapUtils;
import org.openspcoop2.testsuite.clients.ClientHttpGenerico;
import org.openspcoop2.testsuite.core.Repository;
import org.openspcoop2.testsuite.core.SOAPEngine;
import org.openspcoop2.testsuite.core.TestSuiteProperties;
import org.openspcoop2.testsuite.db.DatabaseComponent;
import org.openspcoop2.testsuite.db.DatabaseMsgDiagnosticiComponent;
import org.openspcoop2.testsuite.db.DatiServizio;
import org.openspcoop2.testsuite.db.DatiServizioAzione;
import org.openspcoop2.testsuite.units.CooperazioneBaseInformazioni;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.utils.xml.JaxbUtils;
import org.openspcoop2.utils.xml.XMLDiff;
import org.openspcoop2.utils.xml.XMLDiffImplType;
import org.openspcoop2.utils.xml.XMLDiffOptions;
import org.openspcoop2.utils.xml.XMLException;
import org.openspcoop2.utils.xml.XMLUtils;
import org.testng.Assert;
import org.testng.Reporter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * MTOMUtilities
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class MTOMUtilities {

	/** Gestore della Collaborazione di Base */
	private static CooperazioneBaseInformazioni info = CooperazioneTrasparenteBase.getCooperazioneBaseInformazioni(CostantiTestSuite.PROXY_SOGGETTO_FRUITORE,
			CostantiTestSuite.PROXY_SOGGETTO_EROGATORE,
				false,CostantiTestSuite.PROXY_PROFILO_TRASMISSIONE_CON_DUPLICATI,Inoltro.CON_DUPLICATI);	
	
	public static boolean SOAP11=true;
	public static boolean SOAP12=false;
	public static boolean PORTA_DELEGATA=true;
	public static boolean PORTA_APPLICATIVA=false;
	public static boolean OTHER_ATTACHMENTS_ENABLED=true;
	public static boolean OTHER_ATTACHMENTS_DISABLED=false;
	
	
	private static boolean init = false;
	private static XMLDiff xmlDiff = null;
	private static synchronized void init() throws XMLException{
		if(init==false){
			xmlDiff = new XMLDiff();
			xmlDiff.initialize(XMLDiffImplType.XML_UNIT, new XMLDiffOptions());
			init = true;
		}
	}
	
	
	
	
	/***
	 * Test per il profilo di collaborazione Sincrono
	 */
	public static void testMTOM(boolean soap11, boolean portaDelegata, String url, String username, String password, boolean addIDUnivoco,
			boolean addOtherAttachments, String azione, int numeroAttachmentsRequest, int numeroAttachmentResponse) throws Exception{
		testMTOM(soap11, portaDelegata, url, username, password, addIDUnivoco, 
				addOtherAttachments, azione, numeroAttachmentsRequest, numeroAttachmentResponse, 
				false, null, false, null);
	}
	public static void testMTOM(boolean soap11, boolean portaDelegata, String url, String username, String password, boolean addIDUnivoco,
			boolean addOtherAttachments, String azione, int numeroAttachmentsRequest, int numeroAttachmentResponse,
			boolean invocazioneConErrore,String erroreAtteso, boolean ricercaEsatta, Date dataInizioTest) throws Exception{
		
        init();
		
        String servizio = null;
        
		MTOMServiceExample port = null;
		if(soap11){
			MTOMServiceExampleSOAP11Service ss = new MTOMServiceExampleSOAP11Service();
			port = ss.getMTOMServiceExampleSOAP11InterfaceEndpoint();  
			servizio = CostantiTestSuite.PROXY_NOME_SERVIZIO_MTOM_SOAP11;
		}
		else{
			MTOMServiceExampleSOAP12Service ss = new MTOMServiceExampleSOAP12Service();
			port = ss.getMTOMServiceExampleSOAP12InterfaceEndpoint();
			servizio = CostantiTestSuite.PROXY_NOME_SERVIZIO_MTOM_SOAP12;
		}
		 
        ((BindingProvider)port).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY,  url);
        ((BindingProvider)port).getRequestContext().put(BindingProvider.USERNAME_PROPERTY, username);
    	((BindingProvider)port).getRequestContext().put(BindingProvider.PASSWORD_PROPERTY, password);
		   	
    	String file = org.openspcoop2.protocol.trasparente.testsuite.core.TestSuiteProperties.getInstance().getXmlSenzaSoap();
    	byte [] xml = org.openspcoop2.utils.resources.FileSystemUtilities.readBytesFromFile(file);
    	Document d = XMLUtils.getInstance().newDocument(xml);
    	if(addIDUnivoco){
    		Element elem = d.createElementNS("http://www.openspcoop.org", "test:idUnivoco");
    		String idUnivoco = "ID-"+SOAPEngine.getIDUnivoco();
    		elem.setNodeValue(idUnivoco);
    		d.getDocumentElement().appendChild(elem);
    	}
    	javax.xml.transform.Source _echo_imageData = new DOMSource(d.getDocumentElement());
    	
    	java.util.List<javax.activation.DataHandler> _echo_other = new ArrayList<javax.activation.DataHandler>();
    	File f_other1 = null;
    	File f_other2 = null;
    	if(addOtherAttachments){
	    	f_other1 = new File(org.openspcoop2.protocol.trasparente.testsuite.core.TestSuiteProperties.getInstance().getPDFFileName());
	    	if(f_other1!=null){
	    		FileDataSource fDS = new FileDataSource(f_other1);
	    		javax.activation.DataHandler dh = new DataHandler(fDS);
	    		_echo_other.add(dh);
	    	}
	    	f_other2 = new File(org.openspcoop2.protocol.trasparente.testsuite.core.TestSuiteProperties.getInstance().getZIPFileName());
	    	if(f_other2!=null){
	    		FileDataSource fDS = new FileDataSource(f_other2);
	    		javax.activation.DataHandler dh = new DataHandler(fDS);
	    		_echo_other.add(dh);
	    	}
    	}
    	
        javax.xml.ws.Holder<java.lang.String> _echo_risposta = new javax.xml.ws.Holder<java.lang.String>();
        javax.xml.ws.Holder<javax.xml.transform.Source> _echo_imageDataResponse = new javax.xml.ws.Holder<javax.xml.transform.Source>();
        javax.xml.ws.Holder<java.util.List<javax.activation.DataHandler>> _echo_otherResponse = new javax.xml.ws.Holder<java.util.List<javax.activation.DataHandler>>();
        
        try{
	        port.echo(file, _echo_imageData, _echo_other,
	        		_echo_risposta, _echo_imageDataResponse, _echo_otherResponse);
	        
	        if(invocazioneConErrore){
	        	throw new Exception("Atteso Errore");
	        }
	        else{
	        	
	        	Reporter.log("echo._echo_risposta=" + _echo_risposta.value);
	            Assert.assertEquals(file, _echo_risposta.value); 
	            Assert.assertTrue(_echo_imageDataResponse.value instanceof javax.xml.transform.stream.StreamSource);
	            javax.xml.transform.stream.StreamSource ssi = (javax.xml.transform.stream.StreamSource) _echo_imageDataResponse.value;
	            Document dResponse = null;
	            if(ssi.getReader()!=null){
	    	        ReaderInputStream ris = new ReaderInputStream(ssi.getReader(),StandardCharsets.UTF_8);
	    	        dResponse = XMLUtils.getInstance().newDocument(ris);
	            }
	            else{
	            	dResponse = XMLUtils.getInstance().newDocument(ssi.getInputStream());
	            }

	            boolean diff = xmlDiff.diff(d, dResponse);
	    		if(!diff){
	    			System.out.println("Request ["+XMLUtils.getInstance().toString(d)+"]");
	    			System.out.println("Response ["+XMLUtils.getInstance().toString(dResponse)+"]");
	    			System.out.println("Diff: "+xmlDiff.getDifferenceDetails());
	    		}
	    		Assert.assertTrue(diff);
	    		
	    		Reporter.log("echo._echo_other.size=" +_echo_otherResponse.value.size());
	    		Assert.assertEquals(_echo_other.size(),_echo_otherResponse.value.size());
	    		
	    		java.util.List<javax.activation.DataHandler> other = _echo_otherResponse.value;
	    		for (int i = 0; i < other.size(); i++) {
	        		javax.activation.DataHandler dh = other.get(i);
	        		//System.out.println("richiesta.other.size[i] received: "+dh.getContent().getClass().getName());
	        		ByteArrayOutputStream bout = new ByteArrayOutputStream();
	        		InputStream is = dh.getInputStream();
	        		int letti = 0;
	        		byte [] buffer = new byte[1024];
	        		while((letti=is.read(buffer))!=-1){
	        			bout.write(buffer, 0, letti);
	        		}
	        		bout.flush();
	        		bout.close();
	        		
	        		byte[] received = bout.toByteArray();
	        		byte [] src = null;
	        		String nomeFile = null;
	        		if(i==0){
	        			src = org.openspcoop2.utils.resources.FileSystemUtilities.readBytesFromFile(f_other1);
	        			nomeFile = f_other1.getAbsolutePath();
	        		}
	        		else{
	        			src = org.openspcoop2.utils.resources.FileSystemUtilities.readBytesFromFile(f_other2);
	        			nomeFile = f_other2.getAbsolutePath();
	        		}

	    			boolean check = Arrays.equals(src,received);
	    			if(!check){
	    				File f = File.createTempFile("test", ".bin");
	    				org.openspcoop2.utils.resources.FileSystemUtilities.writeFile(f, received);
	    				System.out.println("Contenuto ricevuto (salvato in "+f.getAbsolutePath()+") differente dal contenuto atteso inviato presente nel file "+nomeFile);
	    			}
	    			Assert.assertTrue(check);
	    		}
	    		
	    		@SuppressWarnings("unchecked")
	    		Map<String,java.util.List<String>> headers = (Map<String,java.util.List<String>>)((BindingProvider)port).getResponseContext().
	    				get(MessageContext.HTTP_RESPONSE_HEADERS);
	    		String id = headers.get(TestSuiteProperties.getInstance().getIdMessaggioTrasporto()).get(0);
	    	
	    		
	    		DatabaseComponent data = null;
	    		try{
	    			boolean checkServizioApplicativo = false;
	    			if(portaDelegata)
	    				data = DatabaseProperties.getDatabaseComponentFruitore();
	    			else
	    				data = DatabaseProperties.getDatabaseComponentErogatore();
	    			testSincrono(data, id,
	    					CostantiTestSuite.PROXY_SOGGETTO_FRUITORE,
	    					CostantiTestSuite.PROXY_SOGGETTO_EROGATORE,
	    					CostantiTestSuite.SOAP_TIPO_SERVIZIO, servizio,azione,
	    					false,CostantiTestSuite.PROXY_PROFILO_TRASMISSIONE_CON_DUPLICATI,Inoltro.CON_DUPLICATI,
	    					checkServizioApplicativo, null, null, null, false, numeroAttachmentsRequest, numeroAttachmentResponse, false);
	    		}catch(Exception e){
	    			throw e;
	    		}finally{
	    			data.close();
	    		}
	        	
	        }
	        
        }catch(Exception e){
        	if(invocazioneConErrore){
        		Reporter.log("Exception ["+e.getClass().getName()+"]");
				Assert.assertTrue(e instanceof javax.xml.ws.soap.SOAPFaultException);
				javax.xml.ws.soap.SOAPFaultException fault = (javax.xml.ws.soap.SOAPFaultException) e;
				Reporter.log("FaultCode ["+fault.getFault().getFaultCodeAsName().getLocalName()+"]");
				String sub = null;
				if(soap11==false){
					if(fault.getFault().getFaultSubcodes().hasNext()){
						javax.xml.namespace.QName q = (javax.xml.namespace.QName)  fault.getFault().getFaultSubcodes().next();
						sub = q.getLocalPart();
						Reporter.log("SubFaultCode ["+sub+"]");
					}
				}
				Reporter.log("FaultString ["+fault.getFault().getFaultString()+"]");
				Reporter.log("FaultActor ["+fault.getFault().getFaultActor()+"]");
				if(portaDelegata){
					if(soap11){
						Assert.assertEquals(fault.getFault().getFaultCodeAsName().getLocalName(), "GOVWAY_ORG_500");
					}
					else{
						Assert.assertEquals(fault.getFault().getFaultCodeAsName().getLocalName(), "Receiver");
						Assert.assertEquals(sub, "GOVWAY_ORG_500");
					}
					Assert.assertEquals(fault.getFault().getFaultString(), "Sistema non disponibile");
					Assert.assertEquals(fault.getFault().getFaultActor(), "http://govway.org/integrazione");
				}
				else{
					if(soap11){
						Assert.assertEquals(fault.getFault().getFaultCodeAsName().getLocalName(), "Server");
					}
					else{
						Assert.assertEquals(fault.getFault().getFaultCodeAsName().getLocalName(), "Receiver");
					}
					Assert.assertEquals(fault.getFault().getFaultString(), "300 - Errore nel processamento del messaggio di cooperazione");
					Assert.assertTrue(fault.getFault().getFaultActor()==null);
				}
				
	    		@SuppressWarnings("unchecked")
	    		Map<String,java.util.List<String>> headers = (Map<String,java.util.List<String>>)((BindingProvider)port).getResponseContext().
	    				get(MessageContext.HTTP_RESPONSE_HEADERS);
	    		String id = null;
	    		if(portaDelegata){
	    			id = headers.get(TestSuiteProperties.getInstance().getIdMessaggioTrasporto()).get(0);
	    			Reporter.log("ID ["+id+"]");
	    		}
				
	    		DatabaseMsgDiagnosticiComponent data = null;
	    		try{
	    			if(portaDelegata)
	    				data = DatabaseProperties.getDatabaseComponentDiagnosticaFruitore();
	    			else
	    				data = DatabaseProperties.getDatabaseComponentDiagnosticaErogatore();
	    			if(portaDelegata){
		    			Assert.assertTrue(data.isTracedErrorMsg(id));
		    			if(ricercaEsatta){
		    				Assert.assertTrue(data.isTracedMessaggio(id, erroreAtteso));
		    			}
		    			else{
		    				Assert.assertTrue(data.isTracedMessaggio(id, true, erroreAtteso));
		    			}
	    			}
	    			else{
	    				Assert.assertTrue(data.isTracedMessaggioWithLike(dataInizioTest, erroreAtteso));
	    			}
	    		}catch(Exception eInternal){
	    			throw eInternal;
	    		}finally{
	    			data.close();
	    		}
        	}
        	else{
        		throw e;
        	}
		}

	}
	
	
	
	public static void testNoMTOM(boolean soap11, boolean portaDelegata, String nomePorta, String username, String password, boolean addIDUnivoco,
			boolean addOtherAttachments, String azione, int numeroAttachmentsRequest, int numeroAttachmentResponse) throws Exception {

		 init();
			
        String servizio = null;
        String envelope = null;
        MessageType messageType = null;
        String contentType = null;
        
		if(soap11){
			servizio = CostantiTestSuite.PROXY_NOME_SERVIZIO_MTOM_SOAP11;
			envelope = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"><soapenv:Body>CORPO</soapenv:Body></soapenv:Envelope>";
			messageType = MessageType.SOAP_11;
			contentType = HttpConstants.CONTENT_TYPE_SOAP_1_1;
		}
		else{
			servizio = CostantiTestSuite.PROXY_NOME_SERVIZIO_MTOM_SOAP12;
			envelope = "<soapenv:Envelope xmlns:soapenv=\"http://www.w3.org/2003/05/soap-envelope\"><soapenv:Body>CORPO</soapenv:Body></soapenv:Envelope>";
			messageType = MessageType.SOAP_12;
			contentType = HttpConstants.CONTENT_TYPE_SOAP_1_2;
		}
		
		Repository r = new Repository();

		ClientHttpGenerico client=new ClientHttpGenerico(r);
		if(portaDelegata){
			client.setUrlPortaDiDominio(org.openspcoop2.protocol.trasparente.testsuite.core.TestSuiteProperties.getInstance().getServizioRicezioneContenutiApplicativiFruitore());
		}
		else{
			client.setUrlPortaDiDominio(org.openspcoop2.protocol.trasparente.testsuite.core.TestSuiteProperties.getInstance().getServizioRicezioneBusteErogatore());
		}
		client.setPortaDelegata(nomePorta);
		client.setSoapAction("\"echo\"");
		client.connectToSoapEngine(messageType);
		client.setUsername(username);
		client.setPassword(password);
		
		Echo echoRequest = new Echo();
		
		String file = org.openspcoop2.protocol.trasparente.testsuite.core.TestSuiteProperties.getInstance().getXmlSenzaSoap();
		echoRequest.setRichiesta(file);
		
    	byte [] xml = org.openspcoop2.utils.resources.FileSystemUtilities.readBytesFromFile(file);
    	Document d = XMLUtils.getInstance().newDocument(xml);
    	if(addIDUnivoco){
    		Element elem = d.createElementNS("http://www.openspcoop.org", "test:idUnivoco");
    		String idUnivoco = "ID-"+SOAPEngine.getIDUnivoco();
    		elem.setNodeValue(idUnivoco);
    		d.getDocumentElement().appendChild(elem);
    	}
    	javax.xml.transform.Source _echo_imageData = new DOMSource(d.getDocumentElement());
    	echoRequest.setImageData(_echo_imageData);
    	
    	java.util.List<javax.activation.DataHandler> _echo_other = new ArrayList<javax.activation.DataHandler>();
    	File f_other1 = null;
    	File f_other2 = null;
    	if(addOtherAttachments){
	    	f_other1 = new File(org.openspcoop2.protocol.trasparente.testsuite.core.TestSuiteProperties.getInstance().getPDFFileName());
	    	if(f_other1!=null){
	    		FileDataSource fDS = new FileDataSource(f_other1);
	    		javax.activation.DataHandler dh = new DataHandler(fDS);
	    		_echo_other.add(dh);
	    	}
	    	f_other2 = new File(org.openspcoop2.protocol.trasparente.testsuite.core.TestSuiteProperties.getInstance().getZIPFileName());
	    	if(f_other2!=null){
	    		FileDataSource fDS = new FileDataSource(f_other2);
	    		javax.activation.DataHandler dh = new DataHandler(fDS);
	    		_echo_other.add(dh);
	    	}
    	}
    	echoRequest.getOther().addAll(_echo_other);
		
    	ByteArrayOutputStream bout = new ByteArrayOutputStream();
		JaxbUtils.objToXml(bout, Echo.class, echoRequest, false, true);
		bout.flush();
		bout.close();
		envelope = envelope.replace("CORPO", bout.toString());
			
		//System.out.println("Request ["+bout.toString()+"]");
			
		
		Message msg = new Message(envelope.getBytes(),false,contentType,null);
		client.setMessage(msg);
		client.setContentType(contentType);
		client.setRispostaDaGestire(true);
		client.run();


		Message msgResponse = client.getResponseMessage();
		bout = new ByteArrayOutputStream();
		msgResponse.writeTo(bout);
		bout.flush();
		bout.close();
		byte[] res = Axis14SoapUtils.sbustamentoMessaggio(msgResponse);
		
		EchoResponse echoRespone = (EchoResponse) JaxbUtils.xmlToObj(new ByteArrayInputStream(res), EchoResponse.class);

		Reporter.log("echo._echo_risposta=" + echoRespone.getRisposta());
        Assert.assertEquals(file, echoRespone.getRisposta()); 
        Assert.assertTrue(echoRespone.getImageDataResponse() instanceof javax.xml.transform.stream.StreamSource);
        javax.xml.transform.stream.StreamSource ssi = (javax.xml.transform.stream.StreamSource) echoRespone.getImageDataResponse();
        Document dResponse = null;
        if(ssi.getReader()!=null){
	        ReaderInputStream ris = new ReaderInputStream(ssi.getReader(),StandardCharsets.UTF_8);
	        dResponse = XMLUtils.getInstance().newDocument(ris);
        }
        else{
        	dResponse = XMLUtils.getInstance().newDocument(ssi.getInputStream());
        }

        boolean diff = xmlDiff.diff(d, dResponse);
		if(!diff){
			System.out.println("Request ["+XMLUtils.getInstance().toString(d)+"]");
			System.out.println("Response ["+XMLUtils.getInstance().toString(dResponse)+"]");
			System.out.println("Diff: "+xmlDiff.getDifferenceDetails());
		}
		Assert.assertTrue(diff);
		
		Reporter.log("echo._echo_other.size=" +echoRespone.getOtherResponse().size());
		Assert.assertEquals(_echo_other.size(),echoRespone.getOtherResponse().size());
		
		java.util.List<javax.activation.DataHandler> other = echoRespone.getOtherResponse();
		for (int i = 0; i < other.size(); i++) {
    		javax.activation.DataHandler dh = other.get(i);
    		//System.out.println("richiesta.other.size[i] received: "+dh.getContent().getClass().getName());
    		bout = new ByteArrayOutputStream();
    		InputStream is = dh.getInputStream();
    		int letti = 0;
    		byte [] buffer = new byte[1024];
    		while((letti=is.read(buffer))!=-1){
    			bout.write(buffer, 0, letti);
    		}
    		bout.flush();
    		bout.close();
    		
    		byte[] received = bout.toByteArray();
    		byte [] src = null;
    		String nomeFile = null;
    		if(i==0){
    			src = org.openspcoop2.utils.resources.FileSystemUtilities.readBytesFromFile(f_other1);
    			nomeFile = f_other1.getAbsolutePath();
    		}
    		else{
    			src = org.openspcoop2.utils.resources.FileSystemUtilities.readBytesFromFile(f_other2);
    			nomeFile = f_other2.getAbsolutePath();
    		}

			boolean check = Arrays.equals(src,received);
			if(!check){
				File f = File.createTempFile("test", ".bin");
				org.openspcoop2.utils.resources.FileSystemUtilities.writeFile(f, received);
				System.out.println("Contenuto ricevuto (salvato in "+f.getAbsolutePath()+") differente dal contenuto atteso inviato presente nel file "+nomeFile);
			}
			Assert.assertTrue(check);
		}
		

		String id = r.getNext();
	
		
		DatabaseComponent data = null;
		try{
			boolean checkServizioApplicativo = false;
			if(portaDelegata)
				data = DatabaseProperties.getDatabaseComponentFruitore();
			else
				data = DatabaseProperties.getDatabaseComponentErogatore();
			testSincrono(data, id,
					CostantiTestSuite.PROXY_SOGGETTO_FRUITORE,
					CostantiTestSuite.PROXY_SOGGETTO_EROGATORE,
					CostantiTestSuite.SOAP_TIPO_SERVIZIO, servizio,azione,
					false,CostantiTestSuite.PROXY_PROFILO_TRASMISSIONE_CON_DUPLICATI,Inoltro.CON_DUPLICATI,
					checkServizioApplicativo, null, null, null, false, numeroAttachmentsRequest, numeroAttachmentResponse, false);
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
		
		
	}
	
	
	private static void testSincrono(DatabaseComponent data,String id,
			IDSoggetto mittente, IDSoggetto destinatario,
			String tipoServizio,String servizio,String azione,
			boolean confermaRicezione, String inoltro, Inoltro inoltroSdk,
			boolean checkServizioApplicativo,String collaborazione,String tipoTempoAtteso,TipoOraRegistrazione tipoTempoAttesoSdk,
			boolean manifestAbilitato, int numeroAttachmentsRequest, int numeroAttachmentsResponse, boolean responseIsFault) throws Exception{
		Reporter.log("Controllo tracciamento richiesta con id: " +id);
		Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
		Reporter.log("Controllo valore Mittente Busta con id: " +id);
		Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id, mittente, null));
		Reporter.log("Controllo valore Destinatario Busta con id: " +id);
		Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedDestinatario(id, destinatario, null));
		Reporter.log("Controllo valore OraRegistrazione con id: " +id);
		if(tipoTempoAtteso!=null){
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedOraRegistrazione(id,tipoTempoAtteso,tipoTempoAttesoSdk));
		}else{
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedOraRegistrazione(id));
		}
		Reporter.log("Controllo valore Servizio ["+servizio+"] Busta con id: " +id);
		DatiServizio datiServizio = new DatiServizio(tipoServizio,servizio, info.getServizio_versioneDefault());
		Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedServizio(id, datiServizio));
		if(azione!=null){
			Reporter.log("Controllo valore Azione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedAzione(id, azione));
		}
		Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
		Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloDiCollaborazione(id, info.getProfiloCollaborazione_protocollo_sincrono(), ProfiloDiCollaborazione.SINCRONO));
		Reporter.log("Controllo valore Collaborazione Busta con id: " +id);
		Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedCollaborazione(id, collaborazione));
		Reporter.log("Controllo valore Profilo di Trasmissione Busta con id: " +id);
		Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloTrasmissione(id, confermaRicezione,inoltro, inoltroSdk));
		Reporter.log("Controllo che la busta non abbia generato eccezioni, id: " +id);
		Assert.assertTrue(data.getVerificatoreTracciaRisposta().existsListaEccezioni(id)==false);
		Reporter.log("Controllo lista trasmissione con id: " +id);
		if(tipoTempoAtteso!=null){
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedTrasmissione(id, mittente, null, destinatario, null, true,tipoTempoAtteso, tipoTempoAttesoSdk));
		}else{
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedTrasmissione(id, mittente, null, destinatario, null));
		}
		checkCountAttachmentsRequest(data, id, null, manifestAbilitato, numeroAttachmentsRequest);
		if(checkServizioApplicativo){
			Reporter.log("Numero messaggi arrivati al servizio applicativo: "+data.getVerificatoreTracciaRichiesta().isArrivedCount(id));
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isArrivedCount(id)==1);
		}
		Reporter.log("----------------------------------------------------------");

		Reporter.log("Controllo tracciamento risposta con riferimento id: " +id);
		Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTraced(id));
		Reporter.log("Controllo valore Mittente Busta della risposta con riferimento id: " +id);
		Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedMittente(id, destinatario, null));
		Reporter.log("Controllo valore Destinatario Busta della risposta con riferimento id: " +id);
		Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedDestinatario(id, mittente, null));
		Reporter.log("Controllo valore OraRegistrazione con id: " +id);
		if(tipoTempoAtteso!=null){
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedOraRegistrazione(id,tipoTempoAtteso,tipoTempoAttesoSdk));
		}else{
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedOraRegistrazione(id));
		}
		Reporter.log("Controllo valore Servizio Busta della risposta con riferimento id: " +id);
		Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedServizio(id, datiServizio));
		if(azione!=null){
			Reporter.log("Controllo valore Azione Busta della risposta con riferimento id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedAzione(id, azione));
		}
		Reporter.log("Controllo valore Profilo di Collaborazione Busta della risposta con riferimento id: " +id);
		Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedProfiloDiCollaborazione(id, info.getProfiloCollaborazione_protocollo_sincrono(), ProfiloDiCollaborazione.SINCRONO));
		Reporter.log("Controllo valore Collaborazione Busta con id: " +id);
		Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedCollaborazione(id, collaborazione));
		Reporter.log("Controllo valore Profilo di Trasmissione Busta con id: " +id);
		Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedProfiloTrasmissione(id, confermaRicezione,inoltro,inoltroSdk));
		Reporter.log("Controllo che la busta non abbia generato eccezioni, riferimento messaggio: " +id);
		Assert.assertTrue(data.getVerificatoreTracciaRisposta().existsListaEccezioni(id)==false);
		Reporter.log("Controllo lista trasmissione, riferimento messaggio: " +id);
		if(tipoTempoAtteso!=null){
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedTrasmissione(id, destinatario,null, mittente, null));
		}else{
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedTrasmissione(id, destinatario,null, mittente, null, true,tipoTempoAtteso,tipoTempoAttesoSdk));
		}
		
		if(!responseIsFault)
			checkCountAttachmentsResponse(data, id, null, manifestAbilitato,  numeroAttachmentsResponse);
	}
	private static void checkCountAttachmentsRequest(DatabaseComponent data, String id, DatiServizioAzione datiServizioAzione ,boolean withManifest, int numeroAttachments){
		_checkAttachments(true, data, id, datiServizioAzione, withManifest, numeroAttachments);
	}
	private static void checkCountAttachmentsResponse(DatabaseComponent data, String id, DatiServizioAzione datiServizioAzione ,boolean withManifest, int numeroAttachments){
		_checkAttachments(false, data, id, datiServizioAzione, withManifest, numeroAttachments);
	}
	private static void _checkAttachments(boolean isRequest,DatabaseComponent data, String id, DatiServizioAzione datiServizioAzione ,boolean withManifest, int numeroAttachments){
			
		try{
			Thread.sleep(1250);
		}catch(Exception e){}
		
		try{
			
			int countAttachments = numeroAttachments;
			if(withManifest){
				countAttachments = countAttachments+1; //manifest comporta che il body originale finisca come allegato
			}
			
			if(isRequest){
				if(datiServizioAzione!=null){
					long count = data.getVerificatoreTracciaRichiesta().countTracedAllegati(id, datiServizioAzione);
					Reporter.log("Controllo numero allegati id("+id+") attesi("+countAttachments+") trovati ("+count+")");
					Assert.assertTrue(count==countAttachments);
				}
				else{
					long count = data.getVerificatoreTracciaRichiesta().countTracedAllegati(id);
					Reporter.log("Controllo numero allegati id("+id+") attesi("+countAttachments+") trovati ("+count+")");
					Assert.assertTrue(count==countAttachments);
				}
			}
			else{
				if(datiServizioAzione!=null){
					long count = data.getVerificatoreTracciaRisposta().countTracedAllegati(id, datiServizioAzione);
					Reporter.log("Controllo numero allegati id("+id+") attesi("+countAttachments+") trovati ("+count+")");
					Assert.assertTrue(count==countAttachments);
				}
				else{
					long count = data.getVerificatoreTracciaRisposta().countTracedAllegati(id);
					Reporter.log("Controllo numero allegati id("+id+") attesi("+countAttachments+") trovati ("+count+")");
					Assert.assertTrue(count==countAttachments);
				}
			}
		}catch(Exception e){
			throw e;
		}
	}

}
