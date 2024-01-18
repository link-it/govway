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

package org.openspcoop2.pdd.services.connector;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.message.exception.ParseException;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.core.CostantiPdD;
import org.openspcoop2.pdd.core.integrazione.HeaderIntegrazione;
import org.openspcoop2.pdd.core.integrazione.UtilitiesIntegrazione;
import org.openspcoop2.pdd.services.connector.messages.ConnectorOutMessage;
import org.openspcoop2.pdd.services.error.RicezioneBusteExternalErrorGenerator;
import org.openspcoop2.pdd.services.error.RicezioneContenutiApplicativiInternalErrorGenerator;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.constants.ErroreIntegrazione;
import org.openspcoop2.protocol.sdk.constants.ErroriIntegrazione;
import org.openspcoop2.protocol.sdk.constants.IDService;
import org.openspcoop2.protocol.sdk.constants.IntegrationFunctionError;
import org.openspcoop2.protocol.sdk.state.RequestInfo;
import org.openspcoop2.utils.CopyStream;
import org.openspcoop2.utils.transport.TransportUtils;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.slf4j.Logger;
import org.w3c.dom.Document;

/**
 * RicezioneContenutiApplicativiIntegrationManagerService
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ConnectorDispatcherUtils {

	public static final boolean CLIENT_ERROR = true;
	public static final boolean GENERAL_ERROR = false;
	
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
			}catch(Exception eClose){
				// ignore
			}
			try{
				res.getOutputStream().close();
			}catch(Exception eClose){
				// ignore
			}
			
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
			}catch(Exception eClose){
				// ignore
			}
			try{
				res.getOutputStream().close();
			}catch(Exception eClose){
				// ignore
			}
			
		}
	}
	
	
	public static void doWsdl(HttpServletRequest req, HttpServletResponse res, HttpRequestMethod method, IDService idService ) throws IOException{
		
		OpenSPCoop2Properties op2Properties = OpenSPCoop2Properties.getInstance();
		String versione = CostantiPdD.OPENSPCOOP2_PRODUCT_VERSION;
		if(op2Properties!=null){
			versione = op2Properties.getPddDetailsForServices();
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
//				int letti = 0;
//				byte [] buffer = new byte[Utilities.DIMENSIONE_BUFFER];
//				while( (letti=is.read(buffer)) != -1 ){
//					bout.write(buffer, 0, letti);
//				}
				CopyStream.copy(is, bout);
				bout.flush();
				bout.close();
			}
			
			if(generazioneWsdlEnabled){
			
				if(bout.size()<=0){
					throw new Exception("WSDL Not Found");
				}
				
				byte[] b = bout.toByteArray();
				org.openspcoop2.message.xml.MessageXMLUtils xmlUtils = org.openspcoop2.message.xml.MessageXMLUtils.DEFAULT;
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
			}catch(Exception eClose){
				// ignore
			}
			try{
				res.getOutputStream().close();
			}catch(Exception eClose){
				// ignore
			}
			try{
				if(is!=null)
					is.close();
			}catch(Exception eClose){
				// ignore
			}
		}
		return;
	}
	
	public static void doError(RequestInfo requestInfo,
			RicezioneContenutiApplicativiInternalErrorGenerator generatoreErrore, ErroreIntegrazione erroreIntegrazione,
			IntegrationFunctionError integrationFunctionError, Throwable e, HttpServletResponse res, Logger log){
		
		IProtocolFactory<?> protocolFactory = requestInfo.getProtocolFactory();
				
		if(generatoreErrore!=null){
			OpenSPCoop2Message msgErrore = generatoreErrore.build(null, integrationFunctionError, erroreIntegrazione, e, null);
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
	
	public static ConnectorDispatcherErrorInfo doError(RequestInfo requestInfo,
			RicezioneContenutiApplicativiInternalErrorGenerator generatoreErrore, ErroreIntegrazione erroreIntegrazione,
			IntegrationFunctionError integrationFunctionError, Throwable e, ParseException parseException,
			ConnectorOutMessage res, Logger log, boolean clientError) throws ConnectorException{
		
		IProtocolFactory<?> protocolFactory = requestInfo.getProtocolFactory();
		
		if(generatoreErrore!=null){
			
			OpenSPCoop2Message msgErrore = generatoreErrore.build(null, integrationFunctionError, erroreIntegrazione, e, parseException);
			
			return _doError(requestInfo, res, log, true, erroreIntegrazione, integrationFunctionError, e, parseException, msgErrore, clientError);
			
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
			IntegrationFunctionError integrationFunctionError, Throwable e, HttpServletResponse res, Logger log){
		
		IProtocolFactory<?> protocolFactory = requestInfo.getProtocolFactory();
				
		if(generatoreErrore!=null){
			OpenSPCoop2Message msgErrore = generatoreErrore.buildErroreProcessamento(null, integrationFunctionError, erroreIntegrazione, e);
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
	
	public static ConnectorDispatcherErrorInfo doError(RequestInfo requestInfo,
			RicezioneBusteExternalErrorGenerator generatoreErrore, ErroreIntegrazione erroreIntegrazione,
			IntegrationFunctionError integrationFunctionError, Throwable e, ParseException parseException,
			ConnectorOutMessage res, Logger log, boolean clientError) throws ConnectorException{
		
		IProtocolFactory<?> protocolFactory = requestInfo.getProtocolFactory();
				
		if(generatoreErrore!=null){
		
			OpenSPCoop2Message msgErrore = generatoreErrore.buildErroreProcessamento(null, integrationFunctionError, erroreIntegrazione, e);
			
			return _doError(requestInfo, res, log, false, erroreIntegrazione, integrationFunctionError, e, parseException, msgErrore, clientError);
			
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
	
	
	
	private static ConnectorDispatcherErrorInfo _doError(RequestInfo requestInfo,ConnectorOutMessage res,Logger log,boolean portaDelegata,
			ErroreIntegrazione erroreIntegrazione,
			IntegrationFunctionError integrationFunctionError, Throwable e, ParseException parseException,
			OpenSPCoop2Message msg, boolean clientError) throws ConnectorException{
		
		if(requestInfo==null) {
			throw new ConnectorException("RequestInfo param is null");
		}
		
		IProtocolFactory<?> protocolFactory = requestInfo.getProtocolFactory();
		
		Map<String, List<String>> trasporto = new HashMap<>();
		try {
			UtilitiesIntegrazione utilitiesIntegrazione = null;
			if(portaDelegata) {
				utilitiesIntegrazione = UtilitiesIntegrazione.getInstancePDResponse(log);
			}
			else {
				utilitiesIntegrazione = UtilitiesIntegrazione.getInstancePAResponse(log);
			}
			if(requestInfo!=null && requestInfo.getIdTransazione()!=null) {
				HeaderIntegrazione hdr = new HeaderIntegrazione(requestInfo.getIdTransazione());
				utilitiesIntegrazione.setTransportProperties(hdr,trasporto,null);
			}
			else {
				utilitiesIntegrazione.setInfoProductTransportProperties(trasporto);
			}
			utilitiesIntegrazione.setSecurityHeaders(msg!=null ? msg.getServiceBinding() : ServiceBinding.REST, requestInfo, trasporto, null);
			if(trasporto.size()>0){
				Iterator<String> keys = trasporto.keySet().iterator();
				while (keys.hasNext()) {
					String key = (String) keys.next();
					List<String> values = trasporto.get(key);
					if(values!=null && !values.isEmpty()) {
						for (String value : values) {
				    		res.addHeader(key,value);
						}
					}
		    	}	
			}
			
		}catch(Throwable error){
			log.error("Errore durante la serializzazione degli headers: "+error.getMessage(),error);
			try{
				throw new ConnectorException(erroreIntegrazione.getDescrizione(protocolFactory),e);
			}catch(Throwable eInternal){
				// rilancio eccezione originale
				throw new ConnectorException(e.getMessage(),e);
			}
		}
		
		int status = 200;
		String contentTypeRisposta = null;
		if(msg!=null && msg.getForcedResponseCode()!=null){
			status = Integer.parseInt(msg.getForcedResponseCode());
			res.setStatus(status);
		}
		
		// content type
		// Alcune implementazioni richiedono di aggiornare il Content-Type
		try{
			if(msg!=null) {
				msg.updateContentType();
				contentTypeRisposta = msg.getContentType();
				if (contentTypeRisposta != null) {
					res.setContentType(contentTypeRisposta);
					TransportUtils.setHeader(trasporto,HttpConstants.CONTENT_TYPE, contentTypeRisposta);
				}
			}
		}catch(Throwable error){
			log.error("Errore durante la serializzazione del contentType: "+error.getMessage(),error);
			try{
				throw new ConnectorException(erroreIntegrazione.getDescrizione(protocolFactory),e);
			}catch(Throwable eInternal){
				// rilancio eccezione originale
				throw new ConnectorException(e.getMessage(),e);
			}
		}
		
		ConnectorDispatcherErrorInfo errorInfo = null;
		try{
			if(clientError) {
				errorInfo = ConnectorDispatcherErrorInfo.getClientError(msg, status, contentTypeRisposta, trasporto, requestInfo, protocolFactory);
			}else{
				errorInfo = ConnectorDispatcherErrorInfo.getGenericError(msg, status, contentTypeRisposta, trasporto, requestInfo, protocolFactory);	
			}
		}catch(Throwable error){
			log.error("Errore durante la generazione delle informazioni di errore: "+error.getMessage(),error);
			try{
				throw new ConnectorException(erroreIntegrazione.getDescrizione(protocolFactory),e);
			}catch(Throwable eInternal){
				// rilancio eccezione originale
				throw new ConnectorException(e.getMessage(),e);
			}
		}

		boolean consume = false; // può essere usato nel post out response handler
		try{
			res.sendResponse(msg, consume);
		}catch(Throwable error){
			log.error("Errore durante la serializzazione dell'errore: "+error.getMessage(),error);
			try{
				throw new ConnectorException(erroreIntegrazione.getDescrizione(protocolFactory),e);
			}catch(Throwable eInternal){
				// rilancio eccezione originale
				throw new ConnectorException(e.getMessage(),e);
			}
		}finally {
			res.flush(false);
			res.close(false);
		}
		
		return errorInfo;
	}
	
	
	public static void doInfo(ConnectorDispatcherInfo info, RequestInfo requestInfo,ConnectorOutMessage res, Logger log, boolean portaDelegata) throws ConnectorException{
		
		if(requestInfo==null) {
			throw new ConnectorException("RequestInfo param is null");
		}
		
		IProtocolFactory<?> protocolFactory = requestInfo.getProtocolFactory();
		
		OpenSPCoop2Message msg = info.getMessage();
		
		Map<String, List<String>> trasporto = info.getTrasporto();
		try {
			UtilitiesIntegrazione utilitiesIntegrazione = null;
			if(portaDelegata) {
				utilitiesIntegrazione = UtilitiesIntegrazione.getInstancePDResponse(log);
			}
			else {
				utilitiesIntegrazione = UtilitiesIntegrazione.getInstancePAResponse(log);
			}
			if(requestInfo!=null && requestInfo.getIdTransazione()!=null) {
				HeaderIntegrazione hdr = new HeaderIntegrazione(requestInfo.getIdTransazione());
				utilitiesIntegrazione.setTransportProperties(hdr,trasporto,null);
			}
			else {
				utilitiesIntegrazione.setInfoProductTransportProperties(trasporto);
			}
			utilitiesIntegrazione.setSecurityHeaders(msg!=null ? msg.getServiceBinding() : ServiceBinding.REST, requestInfo, trasporto, null);
			if(trasporto.size()>0){
				Iterator<String> keys = trasporto.keySet().iterator();
				while (keys.hasNext()) {
					String key = (String) keys.next();
					List<String> values = trasporto.get(key);
					if(values!=null && !values.isEmpty()) {
						for (String value : values) {
				    		res.addHeader(key,value);
						}
					}
		    	}	
			}
			
		}catch(Throwable error){
			log.error("Errore durante la serializzazione degli headers: "+error.getMessage(),error);
			try{
				throw new ConnectorException(ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.get5XX_ErroreProcessamento(error.getMessage())
						.getDescrizione(protocolFactory),error);
			}catch(Throwable eInternal){
				// rilancio eccezione originale
				throw new ConnectorException(error.getMessage(),error);
			}
		}
		
		int status = 200;
		String contentTypeRisposta = null;
		if(msg!=null && msg.getForcedResponseCode()!=null){
			status = Integer.parseInt(msg.getForcedResponseCode());
			res.setStatus(status);
		}
		else if(info.getStatus()>0) {
			status = info.getStatus();
			res.setStatus(status);
		}
		
		// content type
		// Alcune implementazioni richiedono di aggiornare il Content-Type
		try{
			if(msg!=null) {
				msg.updateContentType();
				contentTypeRisposta = msg.getContentType();
				if (contentTypeRisposta != null) {
					res.setContentType(contentTypeRisposta);
					TransportUtils.setHeader(trasporto,HttpConstants.CONTENT_TYPE, contentTypeRisposta);
				}
			}
		}catch(Throwable error){
			log.error("Errore durante la serializzazione del contentType: "+error.getMessage(),error);
			try{
				throw new ConnectorException(ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.get5XX_ErroreProcessamento(error.getMessage())
						.getDescrizione(protocolFactory),error);
			}catch(Throwable eInternal){
				// rilancio eccezione originale
				throw new ConnectorException(error.getMessage(),error);
			}
		}
	
		boolean consume = false; // può essere usato nel post out response handler
		try{
			res.sendResponse(msg, consume);
		}catch(Throwable error){
			log.error("Errore durante la serializzazione dell'errore: "+error.getMessage(),error);
			try{
				throw new ConnectorException(ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.get5XX_ErroreProcessamento(error.getMessage())
						.getDescrizione(protocolFactory),error);
			}catch(Throwable eInternal){
				// rilancio eccezione originale
				throw new ConnectorException(error.getMessage(),error);
			}
		}finally {
			res.flush(false);
			res.close(false);
		}
		
	}
}
