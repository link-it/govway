package org.openspcoop2.pdd.services.connector;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.constants.IntegrationError;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.message.exception.ParseException;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.core.CostantiPdD;
import org.openspcoop2.pdd.services.RequestInfo;
import org.openspcoop2.pdd.services.connector.messages.ConnectorOutMessage;
import org.openspcoop2.pdd.services.error.RicezioneBusteExternalErrorGenerator;
import org.openspcoop2.pdd.services.error.RicezioneContenutiApplicativiInternalErrorGenerator;
import org.openspcoop2.protocol.engine.constants.IDService;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.constants.ErroreIntegrazione;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.slf4j.Logger;
import org.w3c.dom.Document;

public class ConnectorDispatcherUtils {

	public static void doServiceBindingNotSupported(HttpServletRequest req, HttpServletResponse res, HttpRequestMethod httpMethod, ServiceBinding serviceBinding, IDService idService) throws IOException{
		
		OpenSPCoop2Properties op2Properties = OpenSPCoop2Properties.getInstance();
		
		// messaggio di errore
		boolean errore404 = false;
		if(op2Properties!=null){
			if(IDService.PORTA_DELEGATA.equals(idService)){
				if(op2Properties.isGenerazioneErroreHttpMethodUnsupportedPortaDelegataEnabled()==false){
					errore404 = true;
				}
			}
			else if(IDService.PORTA_DELEGATA_XML_TO_SOAP.equals(idService)){
				if(op2Properties.isGenerazioneErroreHttpMethodUnsupportedPortaDelegataImbustamentoSOAPEnabled()==false){
					errore404 = true;
				}
			}
			else if(IDService.PORTA_APPLICATIVA.equals(idService)){
				if(op2Properties.isGenerazioneErroreHttpMethodUnsupportedPortaDelegataEnabled()==false){
					errore404 = true;
				}
			}
			else{
				throw new IOException("Service ["+idService+"] not supported");
			}
		}
		
		if(errore404){
			res.sendError(404,ConnectorUtils.generateError404Message(ConnectorUtils.getFullCodeServiceBindingNotSupported(idService, serviceBinding)));
		}
		else{
		
			res.setStatus(500);
			
			ConnectorUtils.generateErrorMessage(idService,httpMethod,req,res, ConnectorUtils.getMessageServiceBindingNotSupported(serviceBinding), false, true);
			
			try{
				res.getOutputStream().flush();
			}catch(Exception eClose){}
			try{
				res.getOutputStream().close();
			}catch(Exception eClose){}
			
		}
	}
	
	public static void doMethodNotSupported(HttpServletRequest req, HttpServletResponse res, HttpRequestMethod method, IDService idService) throws IOException{
		
		OpenSPCoop2Properties op2Properties = OpenSPCoop2Properties.getInstance();
		
		// messaggio di errore
		boolean errore404 = false;
		if(op2Properties!=null){
			if(IDService.PORTA_DELEGATA.equals(idService)){
				if(op2Properties.isGenerazioneErroreHttpMethodUnsupportedPortaDelegataEnabled()==false){
					errore404 = true;
				}
			}
			else if(IDService.PORTA_DELEGATA_XML_TO_SOAP.equals(idService)){
				if(op2Properties.isGenerazioneErroreHttpMethodUnsupportedPortaDelegataImbustamentoSOAPEnabled()==false){
					errore404 = true;
				}
			}
			else if(IDService.PORTA_APPLICATIVA.equals(idService)){
				if(op2Properties.isGenerazioneErroreHttpMethodUnsupportedPortaDelegataEnabled()==false){
					errore404 = true;
				}
			}
			else{
				throw new IOException("Service ["+idService+"] not supported");
			}
		}
		
		if(errore404){
			res.sendError(404,ConnectorUtils.generateError404Message(ConnectorUtils.getFullCodeHttpMethodNotSupported(idService, method)));
		}
		else{
		
			res.setStatus(500);
			
			ConnectorUtils.generateErrorMessage(idService,method,req,res, ConnectorUtils.getMessageHttpMethodNotSupported(method), false, true);
			
			try{
				res.getOutputStream().flush();
			}catch(Exception eClose){}
			try{
				res.getOutputStream().close();
			}catch(Exception eClose){}
			
		}
	}
	
	
	public static void doWsdl(HttpServletRequest req, HttpServletResponse res, HttpRequestMethod method, IDService idService ) throws IOException{
		
		OpenSPCoop2Properties op2Properties = OpenSPCoop2Properties.getInstance();
		String versione = "Porta di Dominio "+CostantiPdD.OPENSPCOOP2_PRODUCT_VERSION;
		if(op2Properties!=null){
			versione = "Porta di Dominio "+op2Properties.getPddDetailsForServices();
		}
		
		
		InputStream is =null;
		try{
			
			String wsdl = null;
			boolean generazioneWsdlEnabled = false;
			if(IDService.PORTA_DELEGATA.equals(idService)){
				wsdl = "/PD.wsdl";
				if(op2Properties!=null){
					generazioneWsdlEnabled = op2Properties.isGenerazioneWsdlPortaDelegataEnabled();
				}
			}
			else if(IDService.PORTA_APPLICATIVA.equals(idService)){
				wsdl = "/PA.wsdl";
				if(op2Properties!=null){
					generazioneWsdlEnabled = op2Properties.isGenerazioneWsdlPortaApplicativaEnabled();
				}
			}
			else{
				throw new Exception("Service ["+idService+"] not supported");
			}
			
			is = RicezioneContenutiApplicativiConnector.class.getResourceAsStream(wsdl);
			ByteArrayOutputStream bout = new ByteArrayOutputStream();

			if(is!=null){
				int letti = 0;
				byte [] buffer = new byte[Utilities.DIMENSIONE_BUFFER];
				while( (letti=is.read(buffer)) != -1 ){
					bout.write(buffer, 0, letti);
				}
				bout.flush();
				bout.close();
			}
			
			if(generazioneWsdlEnabled){
			
				if(bout.size()<=0){
					throw new Exception("WSDL Not Found");
				}
				
				byte[] b = bout.toByteArray();
				org.openspcoop2.message.xml.XMLUtils xmlUtils = org.openspcoop2.message.xml.XMLUtils.getInstance();
				Document d = xmlUtils.newDocument(b);
				d.getFirstChild().appendChild(d.createComment(versione));
				xmlUtils.writeTo(d, res.getOutputStream());
				
			}
			else{
				
				res.sendError(404, ConnectorUtils.generateError404Message(ConnectorUtils.getFullCodeWsdlUnsupported(idService)));
				
			}
			
		}catch(Exception e){					
			res.setStatus(500);
			
			ConnectorUtils.generateErrorMessage(idService,method,req,res, "Generazione WSDL non riuscita", false, true);
			
			ConnectorUtils.getErrorLog().error("Generazione WSDL "+idService+" non riuscita",e);	
		}finally{
			try{
				res.getOutputStream().flush();
			}catch(Exception eClose){}
			try{
				res.getOutputStream().close();
			}catch(Exception eClose){}
			try{
				if(is!=null)
					is.close();
			}catch(Exception eClose){}
		}
		return;
	}
	
	public static void doError(RequestInfo requestInfo,
			RicezioneContenutiApplicativiInternalErrorGenerator generatoreErrore, ErroreIntegrazione erroreIntegrazione,
			IntegrationError integrationError, Throwable e, HttpServletResponse res, Logger log){
		
		IProtocolFactory protocolFactory = requestInfo.getProtocolFactory();
				
		if(generatoreErrore!=null){
			OpenSPCoop2Message msgErrore = generatoreErrore.build(integrationError, erroreIntegrazione, e, null);
			if(msgErrore.getForcedResponseCode()!=null){
				res.setStatus(Integer.parseInt(msgErrore.getForcedResponseCode()));
			}
			try{
				msgErrore.writeTo(res.getOutputStream(), true);
			}catch(Exception eWriteTo){
				log.error("Errore durante la serializzazione dell'errore: "+e.getMessage(),e);
				try{
					res.sendError(500);
				}catch(Exception eInternal){
					throw new RuntimeException(eInternal.getMessage(),eInternal);
				}
			}
		}
		else{
			try{
				res.sendError(500,erroreIntegrazione.getDescrizione(protocolFactory));
			}catch(Exception eInternal){
				throw new RuntimeException(eInternal.getMessage(),eInternal);
			}
		}
		
	}
	
	public static void doError(RequestInfo requestInfo,
			RicezioneContenutiApplicativiInternalErrorGenerator generatoreErrore, ErroreIntegrazione erroreIntegrazione,
			IntegrationError integrationError, Throwable e, ParseException parseException,
			ConnectorOutMessage res, Logger log) throws ConnectorException{
		
		IProtocolFactory protocolFactory = requestInfo.getProtocolFactory();
			
		if(generatoreErrore!=null){
			OpenSPCoop2Message msgErrore = generatoreErrore.build(integrationError, erroreIntegrazione, e, parseException);
			if(msgErrore.getForcedResponseCode()!=null){
				res.setStatus(Integer.parseInt(msgErrore.getForcedResponseCode()));
			}
			try{
				res.sendResponse(msgErrore, true);
			}catch(Throwable error){
				log.error("Errore durante la serializzazione dell'errore: "+error.getMessage(),error);
				try{
					throw new ConnectorException(erroreIntegrazione.getDescrizione(protocolFactory),e);
				}catch(Throwable eInternal){
					// rilancio eccezione originale
					throw new ConnectorException(e.getMessage(),e);
				}
			}
		}
		else{
			try{
				String errore = erroreIntegrazione.getDescrizione(protocolFactory);
				throw new ConnectorException(errore);
			}catch(Throwable eInternal){
				throw new ConnectorException(eInternal.getMessage(),eInternal);
			}
		}
		
	}
	
	public static void doError(RequestInfo requestInfo,
			RicezioneBusteExternalErrorGenerator generatoreErrore, ErroreIntegrazione erroreIntegrazione,
			IntegrationError integrationError, Throwable e, HttpServletResponse res, Logger log){
		
		IProtocolFactory protocolFactory = requestInfo.getProtocolFactory();
				
		if(generatoreErrore!=null){
			OpenSPCoop2Message msgErrore = generatoreErrore.buildErroreProcessamento(integrationError, erroreIntegrazione, e);
			if(msgErrore.getForcedResponseCode()!=null){
				res.setStatus(Integer.parseInt(msgErrore.getForcedResponseCode()));
			}
			try{
				msgErrore.writeTo(res.getOutputStream(), true);
			}catch(Exception eWriteTo){
				log.error("Errore durante la serializzazione dell'errore: "+e.getMessage(),e);
				try{
					res.sendError(500);
				}catch(Exception eInternal){
					throw new RuntimeException(eInternal.getMessage(),eInternal);
				}
			}
		}
		else{
			try{
				res.sendError(500,erroreIntegrazione.getDescrizione(protocolFactory));
			}catch(Exception eInternal){
				throw new RuntimeException(eInternal.getMessage(),eInternal);
			}
		}
		
	}
	
	public static void doError(RequestInfo requestInfo,
			RicezioneBusteExternalErrorGenerator generatoreErrore, ErroreIntegrazione erroreIntegrazione,
			IntegrationError integrationError, Throwable e, ParseException parseException,
			ConnectorOutMessage res, Logger log) throws ConnectorException{
		
		IProtocolFactory protocolFactory = requestInfo.getProtocolFactory();
							
		OpenSPCoop2Message msgErrore = generatoreErrore.buildErroreProcessamento(integrationError, erroreIntegrazione, e);
		if(msgErrore.getForcedResponseCode()!=null){
			res.setStatus(Integer.parseInt(msgErrore.getForcedResponseCode()));
		}
		try{
			res.sendResponse(msgErrore, true);
		}catch(Throwable error){
			log.error("Errore durante la serializzazione dell'errore: "+error.getMessage(),error);
			try{
				throw new ConnectorException(erroreIntegrazione.getDescrizione(protocolFactory),e);
			}catch(Throwable eInternal){
				// rilancio eccezione originale
				throw new ConnectorException(e.getMessage(),e);
			}
		}
		
	}
	
}
