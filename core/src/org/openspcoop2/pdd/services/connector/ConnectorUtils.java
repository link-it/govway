/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it). 
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

import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringEscapeUtils;
import org.openspcoop2.message.config.ServiceBindingConfiguration;
import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.message.exception.MessageException;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.core.CostantiPdD;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.engine.URLProtocolContextImpl;
import org.openspcoop2.protocol.manifest.Context;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.config.IProtocolConfiguration;
import org.openspcoop2.protocol.sdk.constants.IDService;
import org.openspcoop2.protocol.sdk.state.RequestInfo;
import org.openspcoop2.protocol.sdk.state.URLProtocolContext;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.resources.MapReader;
import org.openspcoop2.utils.transport.http.ContentTypeUtilities;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.slf4j.Logger;

/**
 * ConnectorUtils
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ConnectorUtils {

	public static Logger getErrorLog(){
		Logger log = OpenSPCoop2Logger.getLoggerOpenSPCoopCore();
		if(log == null){
			log = LoggerWrapperFactory.getLogger(ConnectorUtils.class);
		}
		return log;
	}
	
	public static RequestInfo getRequestInfo(IProtocolFactory<?> pf,URLProtocolContext protocolContext) throws ProtocolException, MessageException{
		
		OpenSPCoop2Properties op2Properties = OpenSPCoop2Properties.getInstance();
		IProtocolConfiguration pc = pf.createProtocolConfiguration();
		
		ServiceBindingConfiguration bindingConfig = pc.getDefaultServiceBindingConfiguration(protocolContext);
		ServiceBinding integrationServiceBinding = bindingConfig.getDefaultBinding();
		
		ServiceBinding protocolServiceBinding = pc.getProtocolServiceBinding(integrationServiceBinding, protocolContext);
				
		ServiceBinding serviceBindingDaUtilizzare = integrationServiceBinding;
		boolean pa = false;
		if(protocolContext.isPortaApplicativaService()) {
			serviceBindingDaUtilizzare = protocolServiceBinding;
			pa = true;
		}
		
		String ct = null;
		try {
			ct = protocolContext.getContentType();
			if(ct!=null && !"".equals(ct)) {
				//(new ContentType(ct)).getBaseType();
				ContentTypeUtilities.validateContentType(ct);
			}
		}catch(Throwable e) {
			// valido content type ricevuto; eventuale tipo non correto verrà segnalato in seguito
			ct = null;
		}
		
		MessageType requestMessageType = bindingConfig.getRequestMessageType(serviceBindingDaUtilizzare, 
				protocolContext, ct);
		
		RequestInfo requestInfo = new RequestInfo();
		requestInfo.setProtocolContext(protocolContext);
		requestInfo.setProtocolFactory(pf);
		if(pa){
			requestInfo.setProtocolRequestMessageType(requestMessageType);
		}
		else{
			requestInfo.setIntegrationRequestMessageType(requestMessageType);
		}
		requestInfo.setProtocolServiceBinding(protocolServiceBinding);
		requestInfo.setIntegrationServiceBinding(integrationServiceBinding);
		requestInfo.setBindingConfig(bindingConfig);
		requestInfo.setIdentitaPdD(op2Properties!=null ? op2Properties.getIdentitaPortaDefault(pf.getProtocol(), requestInfo) : null );
		
		return requestInfo;
	}
	
	public static String getMessageHttpMethodNotSupported(HttpRequestMethod method){
		return ConnectorCostanti.MESSAGE_METHOD_HTTP_NOT_SUPPORTED.replace(ConnectorCostanti.KEYWORD_METHOD_HTTP, method.name());
	}
	
	public static String getMessageServiceBindingNotSupported(ServiceBinding serviceBinding){
		return ConnectorCostanti.MESSAGE_SERVICE_BINDING_NOT_SUPPORTED.replace(ConnectorCostanti.KEYWORD_SERVICE_BINDING, serviceBinding.name());
	}
	
	private static StringBuilder getPrefixCode(IDService idService) {
		
		Logger log = OpenSPCoop2Logger.getLoggerOpenSPCoopCore();
		if(log==null){
			log = OpenSPCoop2Logger.getLoggerOpenSPCoopConsole();
		}
		if(log==null){
			log = LoggerWrapperFactory.getLogger(ConnectorUtils.class);
		}
		
		StringBuilder bf = new StringBuilder();
		try{
			bf.append(idService.getCode()).append(ConnectorCostanti.SEPARATOR_CODE);
		}catch(Exception e){
			log.error(e.getMessage(),e);
			bf = new StringBuilder();
			bf.append(ConnectorCostanti.ID_ERRORE_GENERICO);
		}
		return bf;
	}
	
	public static String getFullCodeGovWayNotInitialized(IDService idService) {
		StringBuilder bf = getPrefixCode(idService);
		bf.append(ConnectorCostanti.GOVWAY_NOT_INITIALIZED);
		return bf.toString();
	}
	
	public static String getFullCodeProtocolUnsupported(IDService idService) {
		StringBuilder bf = getPrefixCode(idService);
		bf.append(ConnectorCostanti.CODE_PROTOCOL_NOT_SUPPORTED);
		return bf.toString();
	}
	
	public static String getFullCodeWsdlUnsupported(IDService idService) {
		StringBuilder bf = getPrefixCode(idService);
		bf.append(ConnectorCostanti.CODE_WSDL_UNSUPPORTED);
		return bf.toString();
	}
	public static String getFullCodeWsdlNotDefined(IDService idService) {
		StringBuilder bf = getPrefixCode(idService);
		bf.append(ConnectorCostanti.CODE_WSDL_NOT_DEFINED);
		return bf.toString();
	}
	
	public static String getFullCodeEngineFilter(IDService idService) {
		StringBuilder bf = getPrefixCode(idService);
		bf.append(ConnectorCostanti.CODE_ENGINE_FILTER);
		return bf.toString();
	}
	
	public static String getFullCodeFunctionUnsupported(IDService idService) {
		StringBuilder bf = getPrefixCode(idService);
		bf.append(ConnectorCostanti.CODE_FUNCTION_UNSUPPORTED);
		return bf.toString();
	}
	
	public static String getFullCodeHttpMethodNotSupported(IDService idService, HttpRequestMethod method) {
		StringBuilder bf = getPrefixCode(idService);
		switch (method) {
		case GET:
			bf.append(ConnectorCostanti.CODE_HTTP_METHOD_GET_UNSUPPORTED);
			break;
		case POST:
			bf.append(ConnectorCostanti.CODE_HTTP_METHOD_POST_UNSUPPORTED);
			break;
		case PUT:
			bf.append(ConnectorCostanti.CODE_HTTP_METHOD_PUT_UNSUPPORTED);
			break;
		case HEAD:
			bf.append(ConnectorCostanti.CODE_HTTP_METHOD_HEAD_UNSUPPORTED);
			break;
		case DELETE:
			bf.append(ConnectorCostanti.CODE_HTTP_METHOD_DELETE_UNSUPPORTED);
			break;
		case OPTIONS:
			bf.append(ConnectorCostanti.CODE_HTTP_METHOD_OPTIONS_UNSUPPORTED);
			break;
		case TRACE:
			bf.append(ConnectorCostanti.CODE_HTTP_METHOD_TRACE_UNSUPPORTED);
			break;
		case PATCH:
			bf.append(ConnectorCostanti.CODE_HTTP_METHOD_PATCH_UNSUPPORTED);
			break;
		case LINK:
			bf.append(ConnectorCostanti.CODE_HTTP_METHOD_LINK_UNSUPPORTED);
			break;
		case UNLINK:
			bf.append(ConnectorCostanti.CODE_HTTP_METHOD_UNLINK_UNSUPPORTED);
			break;
		}
		return bf.toString();
	}
	
	public static String getFullCodeServiceBindingNotSupported(IDService idService, ServiceBinding serviceBinding) {
		StringBuilder bf = getPrefixCode(idService);
		switch (serviceBinding) {
		case SOAP:
			bf.append(ConnectorCostanti.CODE_SERVICE_BINDING_SOAP_UNSUPPORTED);
			break;
		case REST:
			bf.append(ConnectorCostanti.CODE_SERVICE_BINDING_REST_UNSUPPORTED);
			break;
		}
		return bf.toString();
	}
	
	
	public static String generateError404Message(String code){
		return "GovWay-"+code;
	}
	
	public static void generateErrorMessage(IDService idService, HttpRequestMethod httpMethod,
			HttpServletRequest req, HttpServletResponse res, String msgErrore, boolean erroreGenerale, boolean htmlMessage) throws IOException{
		generateErrorMessage(idService, httpMethod, req, res, msgErrore, erroreGenerale, htmlMessage, null);
	}
	public static void generateErrorMessage(IDService idService, HttpRequestMethod httpMethod,
			HttpServletRequest req, StringBuilder log, String msgErrore, boolean erroreGenerale, boolean htmlMessage) throws IOException{
		generateErrorMessage(idService, httpMethod, req, null, msgErrore, erroreGenerale, htmlMessage, log);
	}
	private static void generateErrorMessage(IDService idService, HttpRequestMethod httpMethod,
			HttpServletRequest req, HttpServletResponse response, String msgErrore, boolean erroreGenerale, boolean htmlMessage, StringBuilder log) throws IOException{
		
		Logger logCore = OpenSPCoop2Logger.getLoggerOpenSPCoopCore();
		OpenSPCoop2Properties op2Properties = OpenSPCoop2Properties.getInstance();
		
		
		
		// versione
		String versione = CostantiPdD.OPENSPCOOP2_PRODUCT_VERSION;
		if(op2Properties!=null){
			versione = op2Properties.getPddDetailsForServices();
		}
		if(htmlMessage){
			versione = StringEscapeUtils.escapeHtml(versione);
			if(response!=null)
				response.setContentType(HttpConstants.CONTENT_TYPE_HTML);
		}
		else{
			if(response!=null)
				response.setContentType(HttpConstants.CONTENT_TYPE_PLAIN);
		}

		
		// produzione Body
		boolean doBody = !HttpRequestMethod.HEAD.equals(httpMethod);
		if(!doBody){
			if(log!=null){
				log.append(CostantiPdD.HEADER_HTTP_X_PDD).append("=").append(versione);
				log.append(CostantiPdD.HEADER_HTTP_X_PDD_DETAILS).append("=").append(msgErrore);
			}
			else{
				response.setHeader(CostantiPdD.HEADER_HTTP_X_PDD, versione);
				response.setHeader(CostantiPdD.HEADER_HTTP_X_PDD_DETAILS, msgErrore);
				response.setContentLength(0);
			}
			return;
		}
		
		StringBuilder risposta = new StringBuilder();
		risposta.append("<html>\n");
		risposta.append("<head>\n");
		risposta.append("<title>"+versione+"</title>\n");
		risposta.append("</head>\n");
		risposta.append("<body>\n");
		
		risposta.append("<h1>"+versione+"</h1>\n");
			
		// url
		String function = null;
		String parameters = null;
		try{
			URLProtocolContext protocolContext = new URLProtocolContextImpl(req, logCore, true, (op2Properties!=null ? op2Properties.getCustomContexts() : null));
			String url = protocolContext.getUrlInvocazione_formBased();
			if(url.endsWith("?wsdl=")){
				// richiesta di un wsdl
				url = url.substring(0, url.length()-"=".length());
			}
			if(htmlMessage){
				url = StringEscapeUtils.escapeHtml( url );
			}
			risposta.append("<p>" +url+"</p>\n");
			function = protocolContext.getFunction();
			parameters = protocolContext.getFunctionParameters();
		}catch(Exception e){
			if(logCore==null){
				LoggerWrapperFactory.getLogger(ConnectorUtils.class).error(e.getMessage(),e);
			}else{
				logCore.error(e.getMessage(),e);
			}
			String context = req.getContextPath();
			if(htmlMessage){
				context = StringEscapeUtils.escapeHtml( context );
			}
			risposta.append("<p>" +context+"</p>\n");
		}

		// errore
		String errore = msgErrore;
		if(htmlMessage){
			errore = StringEscapeUtils.escapeHtml(errore);
		}
		risposta.append("<p>"+errore+"</p>\n");	
		
		
		// other infos
		switch (idService) {
		case PORTA_DELEGATA:
			risposta.append("<i>Servizio utilizzabile per l'invocazione delle fruizioni esposte dall'API Gateway GovWay</i><br/><br/>\n");
			break;
		case PORTA_APPLICATIVA:
			risposta.append("<i>Servizio utilizzabile per l'invocazione delle erogazioni esposte dall'API Gateway GovWay</i><br/><br/>\n");
			break;
		case PORTA_DELEGATA_XML_TO_SOAP:
			risposta.append("<i>Servizio utilizzabile per l'invocazione delle fruizioni esposte dall'API Gateway GovWay, con messaggi xml non imbustati nel protocollo SOAP</i><br/><br/>\n");
			break;
		case INTEGRATION_MANAGER_SOAP:
			if(parameters==null){
				risposta.append("<i>Servizio IntegrationManager</i><br/><br/>\n");
			}
			else if( (function+"/"+parameters).equals(URLProtocolContext.IntegrationManager_FUNCTION_PD) ){
				risposta.append("<i>Servizio utilizzabile per l'invocazione delle fruizioni esposte dall'API Gateway GovWay</i><br/><br/>\n");
			}
			else if( (function+"/"+parameters).startsWith(URLProtocolContext.IntegrationManager_FUNCTION_PD+"/") ){
				risposta.append("<i>Servizio utilizzabile per l'invocazione delle fruizioni esposte dall'API Gateway GovWay</i><br/><br/>\n");
			}
			else if( (function+"/"+parameters).equals(URLProtocolContext.IntegrationManager_FUNCTION_MessageBox) ){
				risposta.append("<i>Servizio utilizzabile per accedere alla MessageBox esposta dall'API Gateway GovWay</i><br/><br/>\n");
			}
			else if( (function+"/"+parameters).startsWith(URLProtocolContext.IntegrationManager_FUNCTION_MessageBox+"/") ){
				risposta.append("<i>Servizio utilizzabile per accedere alla MessageBox esposta dall'API Gateway GovWay</i><br/><br/>\n");
			}
			else{
				risposta.append("<i>Servizio IntegrationManager dell'API Gateway GovWay</i><br/><br/>\n");
			}
			break;
		case CHECK_PDD:
			risposta.append("<i>Servizio utilizzabile per comprendere lo stato di funzionamento dell'API Gateway GovWay</i><br/><br/>\n");
			break;
		case PROXY:
			risposta.append("<i>Servizio utilizzato in installazioni container dell'API Gateway GovWay</i><br/><br/>\n");
			break;
		default:
			if(htmlMessage){
				// use as
				String useAs = "Use as http[s]://<server>"+ req.getContextPath()+"/<protocol-context>/<service>[/...]";
				useAs = StringEscapeUtils.escapeHtml(useAs);
				risposta.append("<i>"+useAs+"</i><br/>\n");
			}
				
			// protocolli
			try{
				MapReader<String, IProtocolFactory<?>> prots = ProtocolFactoryManager.getInstance().getProtocolFactories();
				if(prots.size()<=0){
					risposta.append("<i>ERROR: No protocol installed</i><br/>\n");
				}
				else{
					StringBuilder bfProtocols = new StringBuilder();
					Enumeration<String> keys = prots.keys();
					while (keys.hasMoreElements()) {
						String key = (String) keys.nextElement();
						IProtocolFactory<?> pf = prots.get(key);
						if(pf.getManifest().getWeb().getEmptyContext()!=null && pf.getManifest().getWeb().getEmptyContext().isEnabled()){
							if(bfProtocols.length()>0){
								bfProtocols.append(", ");
							}
							bfProtocols.append("\"\" (protocol:"+key+")");
						}
						if(pf.getManifest().getWeb().sizeContextList()>0){
							for (Context context : pf.getManifest().getWeb().getContextList()) {
								if(bfProtocols.length()>0){
									bfProtocols.append(", ");
								}
								bfProtocols.append(context.getName()+" (protocol:"+key+")");
							}
						}
					}
					String enabledProtocols = "Enabled protocol-contexts: "+bfProtocols.toString();
					if(htmlMessage){
						enabledProtocols = StringEscapeUtils.escapeHtml(enabledProtocols);
					}
					risposta.append("<i>"+enabledProtocols+"</i><br/>\n");
				}
			}catch(Exception e){
				if(logCore==null){
					LoggerWrapperFactory.getLogger(ConnectorUtils.class).error(e.getMessage(),e);
				}else{
					logCore.error(e.getMessage(),e);
				}
				risposta.append("<i>ERROR: No protocol installed</i><br/>\n");
			}
			
			if(htmlMessage){
				// servizi
				risposta.append("<i>Enabled services: in, out, out/xml2soap, check, IntegrationManager</i><br/><br/>\n");
			
				// web site
				risposta.append("<i>Official website: https://govway.org</i><br/><br/>\n");
			}
			break;
		}

			
		risposta.append("</body>\n");
		risposta.append("</html>\n");
		if(log!=null){
			log.append(risposta.toString());
		}else{
			response.getOutputStream().write(risposta.toString().getBytes());
		}
	}
	
	
}
