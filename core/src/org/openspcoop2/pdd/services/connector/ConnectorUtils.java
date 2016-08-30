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

package org.openspcoop2.pdd.services.connector;

import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringEscapeUtils;
import org.slf4j.Logger;
import org.openspcoop2.core.api.constants.MethodType;
import org.openspcoop2.message.Costanti;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.core.CostantiPdD;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.engine.URLProtocolContext;
import org.openspcoop2.protocol.engine.constants.IDService;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.resources.MapReader;

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
	
	public static String getMessageHttpMethodNotSupported(MethodType method){
		return ConnectorCostanti.MESSAGE_METHOD_HTTP_NOT_SUPPORTED.replace(ConnectorCostanti.KEYWORD_METHOD_HTTP, method.name());
	}
	
	private static StringBuffer getPrefixCode(IDService idService) {
		
		Logger log = OpenSPCoop2Logger.getLoggerOpenSPCoopCore();
		if(log==null){
			log = OpenSPCoop2Logger.getLoggerOpenSPCoopConsole();
		}
		if(log==null){
			log = LoggerWrapperFactory.getLogger(ConnectorUtils.class);
		}
		
		StringBuffer bf = new StringBuffer();
		try{
			bf.append(idService.getCode()).append(ConnectorCostanti.SEPARATOR_CODE);
		}catch(Exception e){
			log.error(e.getMessage(),e);
			bf = new StringBuffer();
			bf.append(ConnectorCostanti.ID_ERRORE_GENERICO);
		}
		return bf;
	}
	
	public static String getFullCodeProtocolUnsupported(IDService idService) {
		StringBuffer bf = getPrefixCode(idService);
		bf.append(ConnectorCostanti.CODE_PROTOCOL_NOT_SUPPORTED);
		return bf.toString();
	}
	
	public static String getFullCodeWsdlUnsupported(IDService idService) {
		StringBuffer bf = getPrefixCode(idService);
		bf.append(ConnectorCostanti.CODE_WSDL);
		return bf.toString();
	}
	
	public static String getFullCodeEngineFilter(IDService idService) {
		StringBuffer bf = getPrefixCode(idService);
		bf.append(ConnectorCostanti.CODE_ENGINE_FILTER);
		return bf.toString();
	}
	
	public static String getFullCodeFunctionUnsupported(IDService idService) {
		StringBuffer bf = getPrefixCode(idService);
		bf.append(ConnectorCostanti.CODE_FUNCTION_UNSUPPORTED);
		return bf.toString();
	}
	
	public static String getFullCodeHttpMethodNotSupported(IDService idService, MethodType method) {
		StringBuffer bf = getPrefixCode(idService);
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
		}
		return bf.toString();
	}
	
	
	public static String generateError404Message(String code){
		return "OpenSPCoop2-"+code;
	}
	
	public static void generateErrorMessage(IDService idService, MethodType httpMethod,
			HttpServletRequest req, HttpServletResponse res, String msgErrore, boolean erroreGenerale, boolean htmlMessage) throws IOException{
		generateErrorMessage(idService, httpMethod, req, res, msgErrore, erroreGenerale, htmlMessage, null);
	}
	public static void generateErrorMessage(IDService idService, MethodType httpMethod,
			HttpServletRequest req, StringBuffer log, String msgErrore, boolean erroreGenerale, boolean htmlMessage) throws IOException{
		generateErrorMessage(idService, httpMethod, req, null, msgErrore, erroreGenerale, htmlMessage, log);
	}
	private static void generateErrorMessage(IDService idService, MethodType httpMethod,
			HttpServletRequest req, HttpServletResponse response, String msgErrore, boolean erroreGenerale, boolean htmlMessage, StringBuffer log) throws IOException{
		
		Logger logCore = OpenSPCoop2Logger.getLoggerOpenSPCoopCore();
		OpenSPCoop2Properties op2Properties = OpenSPCoop2Properties.getInstance();
		
		
		
		// versione
		String versione = "Porta di Dominio "+CostantiPdD.OPENSPCOOP2_PRODUCT_VERSION;
		if(op2Properties!=null){
			versione = "Porta di Dominio "+op2Properties.getPddDetailsForServices();
		}
		if(htmlMessage){
			versione = StringEscapeUtils.escapeHtml(versione);
			if(response!=null)
				response.setContentType(Costanti.CONTENT_TYPE_HTML);
		}
		else{
			if(response!=null)
				response.setContentType(Costanti.CONTENT_TYPE_PLAIN);
		}

		
		// produzione Body
		boolean doBody = !MethodType.HEAD.equals(httpMethod);
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
		
		StringBuffer risposta = new StringBuffer();
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
			URLProtocolContext protocolContext = new URLProtocolContext(req, logCore);
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
		case PORTA_DELEGATA_SOAP:
			risposta.append("<i>Servizio utilizzabile per l'invocazione di Porte Delegate esposte dalla PdD OpenSPCoop v2</i><br/><br/>\n");
			break;
		case PORTA_APPLICATIVA_SOAP:
			risposta.append("<i>Servizio utilizzabile per l'invocazione di Porte Applicative esposte dalla PdD OpenSPCoop v2</i><br/><br/>\n");
			break;
		case PORTA_DELEGATA_XML_TO_SOAP:
			risposta.append("<i>Servizio utilizzabile per l'invocazione di Porte Delegate esposte dalla PdD OpenSPCoop v2, con messaggi xml non imbustati nel protocollo SOAP</i><br/><br/>\n");
			break;
		case INTEGRATION_MANAGER_SOAP:
			if(parameters==null){
				risposta.append("<i>Servizio IntegrationManager</i><br/><br/>\n");
			}
			else if( (function+"/"+parameters).equals(URLProtocolContext.IntegrationManager_FUNCTION_PD) ){
				risposta.append("<i>Servizio utilizzabile per l'invocazione di Porte Delegate esposte dalla PdD OpenSPCoop v2</i><br/><br/>\n");
			}
			else if( (function+"/"+parameters).startsWith(URLProtocolContext.IntegrationManager_FUNCTION_PD+"/") ){
				risposta.append("<i>Servizio utilizzabile per l'invocazione di Porte Delegate esposte dalla PdD OpenSPCoop v2</i><br/><br/>\n");
			}
			else if( (function+"/"+parameters).equals(URLProtocolContext.IntegrationManager_FUNCTION_MessageBox) ){
				risposta.append("<i>Servizio utilizzabile per accedere alla MessageBox esposta dalla PdD OpenSPCoop v2</i><br/><br/>\n");
			}
			else if( (function+"/"+parameters).startsWith(URLProtocolContext.IntegrationManager_FUNCTION_MessageBox+"/") ){
				risposta.append("<i>Servizio utilizzabile per accedere alla MessageBox esposta dalla PdD OpenSPCoop v2</i><br/><br/>\n");
			}
			else{
				risposta.append("<i>Servizio IntegrationManager della PdD OpenSPCoop v2</i><br/><br/>\n");
			}
			break;
		case CHECK_PDD:
			risposta.append("<i>Servizio utilizzabile per comprendere lo stato di funzionamento della PdD OpenSPCoop v2</i><br/><br/>\n");
			break;
		case PORTA_DELEGATA_API:
			risposta.append("<i>Servizio utilizzabile per l'invocazione di Porte Delegate esposte dalla PdD OpenSPCoop v2, attraverso richieste HTTP</i><br/><br/>\n");
			break;
		case PORTA_APPLICATIVA_API:
			risposta.append("<i>Servizio utilizzabile per l'invocazione di Porte Applicative esposte dalla PdD OpenSPCoop v2, attraverso richieste HTTP</i><br/><br/>\n");
			break;
		case INTEGRATION_MANAGER_API:
			risposta.append("<i>Servizio utilizzabile per accedere alla MessageBox esposta dalla PdD OpenSPCoop v2, attraverso richieste HTTP</i><br/><br/>\n");
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
				MapReader<String, IProtocolFactory> prots = ProtocolFactoryManager.getInstance().getProtocolFactories();
				if(prots.size()<=0){
					risposta.append("<i>ERROR: No protocol installed</i><br/>\n");
				}
				else{
					StringBuffer bfProtocols = new StringBuffer();
					Enumeration<String> keys = prots.keys();
					while (keys.hasMoreElements()) {
						String key = (String) keys.nextElement();
						IProtocolFactory pf = prots.get(key);
						if(pf.getManifest().getWeb().getEmptyContext()!=null && pf.getManifest().getWeb().getEmptyContext().isEnabled()){
							if(bfProtocols.length()>0){
								bfProtocols.append(", ");
							}
							bfProtocols.append("\"\" (protocol:"+key+")");
						}
						if(pf.getManifest().getWeb().sizeContextList()>0){
							for (String context : pf.getManifest().getWeb().getContextList()) {
								if(bfProtocols.length()>0){
									bfProtocols.append(", ");
								}
								bfProtocols.append(context+" (protocol:"+key+")");
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
				risposta.append("<i>Enabled services: PD, PA, PDtoSOAP, checkPdD, IntegrationManager</i><br/><br/>\n");
			
				// web site
				risposta.append("<i>Official website: http://www.openspcoop.org</i><br/><br/>\n");
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
