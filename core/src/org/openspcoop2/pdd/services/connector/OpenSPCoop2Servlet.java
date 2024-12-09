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

import java.io.IOException;
import java.util.Enumeration;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.openspcoop2.core.commons.CoreException;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.core.CostantiPdD;
import org.openspcoop2.pdd.core.byok.BYOKMapProperties;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.pdd.services.OpenSPCoop2Startup;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.engine.URLProtocolContextImpl;
import org.openspcoop2.protocol.manifest.constants.Costanti;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.constants.IDService;
import org.openspcoop2.protocol.sdk.state.FunctionContextsCustom;
import org.openspcoop2.protocol.sdk.state.URLProtocolContext;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.Semaphore;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.slf4j.Logger;

/**
 * OpenSPCoop2Servlet
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
@SuppressWarnings("serial")
public class OpenSPCoop2Servlet extends HttpServlet {
	private static Logger logger = null;

	private static synchronized Logger getLoggerStartup() {
		if ( logger == null ) {
			logger = LoggerWrapperFactory.getLogger("govway.startup");
		}
		return logger;
	}
	private static Logger getLogger() {
		if ( logger == null )
			getLoggerStartup();
		return logger;
	}

	private static boolean checkSecrets = false;
	private static final Semaphore semaphoreCheckSecrets = new Semaphore("GovWaySecrets");
	private static void checkSecrets() {
		if(!checkSecrets) {
			initSecrets();
		}
	}
	private static void initSecrets() {
		semaphoreCheckSecrets.acquireThrowRuntime("initSecrets");
		try {
			if(!checkSecrets) {
				BYOKMapProperties secretsProperties = BYOKMapProperties.getInstance();
				if(secretsProperties!=null && secretsProperties.isExistsUnwrapPropertiesAfterGovWayStartup()) {
					secretsProperties.setGovWayStarted(true);
					try {
						secretsProperties.initEnvironment();
						String secretsConfig = OpenSPCoop2Properties.getInstance().getBYOKEnvSecretsConfig();
						String msgInit = "Environment inizializzato con i secrets definiti nel file '"+secretsConfig+"' dopo il completamento dell'avvio di GovWay"+
								"\n\tJavaProperties: "+secretsProperties.getJavaMap().keys()+
								"\n\tEnvProperties: "+secretsProperties.getEnvMap().keys()+
								"\n\tObfuscateMode: "+secretsProperties.getObfuscateModeDescription();
						OpenSPCoop2Startup.logStartupInfo(msgInit);
					} catch (Exception e) {
						OpenSPCoop2Startup.logStartupError("Inizializzazione ambiente (secrets) non riuscita: "+e.getMessage(),e);
					}
				}
				checkSecrets = true;
			}
		}finally {
			semaphoreCheckSecrets.release("initSecrets");
		}
	}
	
	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
				
		HttpRequestMethod m = null;
		try {
			m = HttpRequestMethod.valueOf(req.getMethod().toUpperCase());
		}catch(Exception e) {
			// richiamo implementazione originale che genera errore: Method is not defined in RFC 2068 and is not supported by the Servlet API
			super.service(req, resp); 
			return;
		}
		switch (m) {
		
		// Standard
		
		case DELETE:
			this.doDelete(req, resp);
			break;
		case GET:
			this.doGet(req, resp);
			break;
		case HEAD:
			this.doHead(req, resp);
			break;
		case OPTIONS:
			this.doOptions(req, resp);
			break;
		case POST:
			this.doPost(req, resp);
			break;
		case PUT:
			this.doPut(req, resp);
			break;
		case TRACE:
			this.doTrace(req, resp);
			break;
			
		// Additionals
		case PATCH, LINK, UNLINK:
			boolean enabled = true;
			OpenSPCoop2Properties op2Properties = null;
			try {
				op2Properties = OpenSPCoop2Properties.getInstance();
			}catch(Exception t) { 
				//come default si lasciano abilitati
			}
			if(op2Properties!=null) {
				if(HttpRequestMethod.PATCH.equals(m)) {
					enabled = op2Properties.isServiceRequestHttpMethodPatchEnabled();
				}
				else if(HttpRequestMethod.LINK.equals(m)) {
					enabled = op2Properties.isServiceRequestHttpMethodLinkEnabled();
				}
				else {/**else if(HttpRequestMethod.UNLINK.equals(m)) {*/
					enabled = op2Properties.isServiceRequestHttpMethodUnlinkEnabled();
				}
			}
			if(enabled) {
				dispatch(req, resp, m);
			}
			else {
				super.service(req, resp); // richiamo implementazione originale che genera errore: Method is not defined in RFC 2068 and is not supported by the Servlet API
			}
			break;
			
		default:
			super.service(req, resp); // richiamo implementazione originale che genera errore: Method is not defined in RFC 2068 and is not supported by the Servlet API
			break;
		}
	}

	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		dispatch(req, resp, HttpRequestMethod.DELETE);
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		dispatch(req, resp, HttpRequestMethod.GET);
	}

	@Override
	protected void doHead(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		dispatch(req, resp, HttpRequestMethod.HEAD);
	}

	@Override
	protected void doOptions(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		dispatch(req, resp, HttpRequestMethod.OPTIONS);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		dispatch(req, resp, HttpRequestMethod.POST);
	}

	@Override
	protected void doPut(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		dispatch(req, resp, HttpRequestMethod.PUT);
	}

	@Override
	protected void doTrace(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		dispatch(req, resp, HttpRequestMethod.TRACE);
	}

	
	private void dispatch(HttpServletRequest req, HttpServletResponse res,HttpRequestMethod method) {
		
		Logger logCore = OpenSPCoop2Logger.getLoggerOpenSPCoopCore();
		Logger logOpenSPCoop2Servlet = getLogger();
		
		OpenSPCoop2Properties op2Properties = null;
		try {
			
			if (!OpenSPCoop2Startup.initialize) {
				
				// req.getContextPath()[/govway] req.getRequestURI()[/govway/check]
				String contextPath = req.getContextPath();
				String requestUri = req.getRequestURI();
				if(requestUri!=null && contextPath!=null && requestUri.startsWith(contextPath) && requestUri.length()>contextPath.length()) {
					String function = requestUri.substring(contextPath.length(), requestUri.length());
					if(function.startsWith("/") && function.length()>1) {
						function = function.substring(1);
					}
					if(function.equals(URLProtocolContext.CHECK_FUNCTION) || function.equals(URLProtocolContext.PROXY_FUNCTION)){
						CheckStatoPdD.serializeNotInitializedResponse(res, (logCore!=null) ? logCore : logOpenSPCoop2Servlet);
						return;
					}
				}
				
				// Attendo inizializzazione
				int max = CostantiPdD.WAIT_STARTUP_TIMEOUT_SECONDS;
				int sleepCheck = CostantiPdD.WAIT_STARTUP_CHECK_INTERVAL_MS;
				for (int i = 0; i < 5; i++) { // attendo 5 secondi la presenza delle proprietà
					if(OpenSPCoop2Properties.getInstance()==null) {
						Utilities.sleep(1000);
					}
					if(OpenSPCoop2Properties.getInstance()!=null) {
						break;
					}
				}
				if(OpenSPCoop2Properties.getInstance()!=null) {
					max = OpenSPCoop2Properties.getInstance().getStartupRichiesteIngressoTimeoutSecondi();
					sleepCheck = OpenSPCoop2Properties.getInstance().getStartupRichiesteIngressoCheckMs();
				}
				
				int maxMs = (max * 1000);
				int actualMs = 0;
				while(!OpenSPCoop2Startup.initialize && actualMs<maxMs) {
					Utilities.sleep(sleepCheck);
					actualMs = actualMs + sleepCheck;
				}
				
				if (!OpenSPCoop2Startup.initialize) {
				
					// log su file core
					StringBuilder bfLogError = new StringBuilder();
					ConnectorUtils.generateErrorMessage(IDService.OPENSPCOOP2_SERVLET,method,req,bfLogError, "GovWay non inizializzato", true, false);
					if(logCore!=null){
						String msg = bfLogError.toString();
						logCore.error(msg);
					}
					else{
						String msg = bfLogError.toString();
						logOpenSPCoop2Servlet.error(msg);
					}
					res.sendError(404,ConnectorUtils.generateError404Message(ConnectorUtils.getFullCodeGovWayNotInitialized(IDService.OPENSPCOOP2_SERVLET)));
					return;
					
				}
			}
			op2Properties = OpenSPCoop2Properties.getInstance();
			
			boolean nioEnabled = op2Properties.isNIOEnabled();
			
			boolean printCertificate = false;
			FunctionContextsCustom customContexts = null;
			if(op2Properties!=null) {
				printCertificate = op2Properties.isPrintInfoCertificate();
				customContexts = op2Properties.getCustomContexts();
			}
			
			URLProtocolContext protocolContext = new URLProtocolContextImpl(req, logCore, printCertificate, customContexts);
			String function = protocolContext.getFunction();
			String prefixFunction = "Service ["+function+"] ";
			IDService idServiceCustom = protocolContext.getIdServiceCustom();
			
			IProtocolFactory<?> pf = ProtocolFactoryManager.getInstance().getProtocolFactoryByServletContext(protocolContext.getProtocolWebContext());
			if(pf==null){
				if(!Costanti.CONTEXT_EMPTY.equals(protocolContext.getProtocolWebContext()))
					throw new CoreException("Non risulta registrato un protocollo con contesto ["+protocolContext.getProtocolWebContext()+"]");
				else
					throw new CoreException("Non risulta registrato un protocollo con contesto speciale 'vuoto'");
			}
						
			if( 
					(function.equals(URLProtocolContext.PD_FUNCTION) && op2Properties.isEnabledFunctionPD()) 
					|| 
					(idServiceCustom!=null && IDService.PORTA_DELEGATA.equals(idServiceCustom))
				){
				
				checkSecrets();
				
				RicezioneContenutiApplicativiConnector r = new RicezioneContenutiApplicativiConnector();
				r.doEngine(ConnectorUtils.getRequestInfo(pf, protocolContext), req, res, method);
				
			}
			else if(nioEnabled && (idServiceCustom!=null && IDService.PORTA_DELEGATA_NIO.equals(idServiceCustom))
					){
					
				checkSecrets();
				
				RicezioneContenutiApplicativiConnectorAsync r = new RicezioneContenutiApplicativiConnectorAsync();
				r.doEngine(ConnectorUtils.getRequestInfo(pf, protocolContext), req, res, method);
					
			}
			else if(
					(function.equals(URLProtocolContext.PD_TO_SOAP_FUNCTION) && op2Properties.isEnabledFunctionPDtoSOAP()) 
					|| 
					(idServiceCustom!=null && IDService.PORTA_DELEGATA_XML_TO_SOAP.equals(idServiceCustom))
				){
				
				checkSecrets();
				
				RicezioneContenutiApplicativiHTTPtoSOAPConnector r = new RicezioneContenutiApplicativiHTTPtoSOAPConnector();
				r.doEngine(ConnectorUtils.getRequestInfo(pf, protocolContext), req, res, method);
				
			}
			else if(nioEnabled && (idServiceCustom!=null && IDService.PORTA_DELEGATA_XML_TO_SOAP_NIO.equals(idServiceCustom))
					){
					
				checkSecrets();
				
				RicezioneContenutiApplicativiHTTPtoSOAPConnectorAsync r = new RicezioneContenutiApplicativiHTTPtoSOAPConnectorAsync();
				r.doEngine(ConnectorUtils.getRequestInfo(pf, protocolContext), req, res, method);
					
			}
			else if(
					(function.equals(URLProtocolContext.PA_FUNCTION) && op2Properties.isEnabledFunctionPA()) 
					|| 
					(idServiceCustom!=null && IDService.PORTA_APPLICATIVA.equals(idServiceCustom))
				){
				
				checkSecrets();
				
				RicezioneBusteConnector r = new RicezioneBusteConnector();
				r.doEngine(ConnectorUtils.getRequestInfo(pf, protocolContext), req, res, method);
			}
			else if(nioEnabled && (idServiceCustom!=null && IDService.PORTA_APPLICATIVA_NIO.equals(idServiceCustom))
					){
					
				checkSecrets();
				
				RicezioneBusteConnectorAsync r = new RicezioneBusteConnectorAsync();
				r.doEngine(ConnectorUtils.getRequestInfo(pf, protocolContext), req, res, method);
					
			}
			else if(function.equals(URLProtocolContext.INTEGRATION_MANAGER_FUNCTION) || (idServiceCustom!=null && IDService.INTEGRATION_MANAGER_SOAP.equals(idServiceCustom))){
				
				checkSecrets();
				
				if(op2Properties!=null && !op2Properties.isIntegrationManagerEnabled()) {
					throw new CoreException(prefixFunction+"not active");
				}
				
				boolean wsdl = false;
				if(HttpRequestMethod.GET.equals(method)){
					Enumeration<?> parameters = req.getParameterNames();
					while(parameters.hasMoreElements()){
						String key = (String) parameters.nextElement();
						String value = req.getParameter(key);
						if("wsdl".equalsIgnoreCase(key) && (value==null || "".equals(value)) ){
							// richiesta del wsdl
							if(op2Properties!=null && !op2Properties.isGenerazioneWsdlIntegrationManagerEnabled()){
								res.sendError(404, ConnectorUtils.generateError404Message(ConnectorUtils.getFullCodeWsdlUnsupported(IDService.INTEGRATION_MANAGER_SOAP)));
								return;
							}
							else{
								wsdl = true;
								break;
							}
						}
					}
				}
				
				if(!HttpRequestMethod.POST.equals(method) && !wsdl){
					// messaggio di errore
					boolean errore404 = false;
					if(op2Properties!=null && !op2Properties.isGenerazioneErroreHttpMethodUnsupportedIntegrationManagerEnabled()){
						errore404 = true;
					}
					
					if(errore404){
						res.sendError(404,ConnectorUtils.generateError404Message(ConnectorUtils.getFullCodeHttpMethodNotSupported(IDService.INTEGRATION_MANAGER_SOAP, method)));
						return;
					}
					else{
					
						res.setStatus(500);
						
						ConnectorUtils.generateErrorMessage(IDService.INTEGRATION_MANAGER_SOAP, method, req, res, ConnectorUtils.getMessageHttpMethodNotSupported(method), false, true);
								
						resFlushAndCloseSafe(res);
						
						return;
					}
				}
								
				// Dispatching al servizio di IntegrationManager implementato tramite CXF
				String serviceIM = protocolContext.getFunctionParameters();
				if(URLProtocolContext.INTEGRATION_MANAGER_SERVICE_PD_GOVWAY.equals(serviceIM) || 
						(URLProtocolContext.INTEGRATION_MANAGER_SERVICE_PD_GOVWAY+"/").equals(serviceIM)) {
					serviceIM = URLProtocolContext.INTEGRATION_MANAGER_SERVICE_PD;
				}
				String forwardUrl = "/"+URLProtocolContext.INTEGRATION_MANAGER_ENGINE+"/"+serviceIM;
				req.setAttribute(org.openspcoop2.core.constants.Costanti.PROTOCOL_NAME.getValue(), protocolContext.getProtocolName());
				req.setAttribute(org.openspcoop2.core.constants.Costanti.PROTOCOL_WEB_CONTEXT.getValue(), protocolContext.getProtocolWebContext());
				req.setAttribute(org.openspcoop2.core.constants.Costanti.INTEGRATION_MANAGER_ENGINE_AUTHORIZED.getValue(), true);
				RequestDispatcher dispatcher = req.getRequestDispatcher(forwardUrl);
				dispatcher.forward(req, res);
				
			}
			else if(function.equals(URLProtocolContext.CHECK_FUNCTION)){
				
				if(!HttpRequestMethod.GET.equals(method)){
					// messaggio di errore
					boolean errore404 = false;
					if(op2Properties!=null && !op2Properties.isGenerazioneErroreHttpMethodUnsupportedCheckEnabled()){
						errore404 = true;
					}
					
					if(errore404){
						res.sendError(404,ConnectorUtils.generateError404Message(ConnectorUtils.getFullCodeHttpMethodNotSupported(IDService.CHECK_PDD, method)));
						return;
					}
					else{
						
						res.setStatus(500);
						
						ConnectorUtils.generateErrorMessage(IDService.CHECK_PDD,method,req,res, ConnectorUtils.getMessageHttpMethodNotSupported(method), false, true);
								
						resFlushAndCloseSafe(res);
						
						return;
					}
				}
				
				// Dispatching al servizio 
				CheckStatoPdD checkStatoPdD = new CheckStatoPdD();
				req.setAttribute(org.openspcoop2.core.constants.Costanti.PROTOCOL_NAME.getValue(), protocolContext.getProtocolName());
				checkStatoPdD.doGet(req, res);
				
			}
			else if(function.equals(URLProtocolContext.PROXY_FUNCTION)){
				
				if(op2Properties!=null && !op2Properties.isProxyReadJMXResourcesEnabled()) {
					throw new CoreException(prefixFunction+"not supported");
				}
				
				if(!HttpRequestMethod.GET.equals(method)){
					// messaggio di errore
					boolean errore404 = false;
					if(op2Properties!=null && !op2Properties.isGenerazioneErroreHttpMethodUnsupportedProxyEnabled()){
						errore404 = true;
					}
					
					if(errore404){
						res.sendError(404,ConnectorUtils.generateError404Message(ConnectorUtils.getFullCodeHttpMethodNotSupported(IDService.PROXY, method)));
						return;
					}
					else{
						
						res.setStatus(500);
						
						ConnectorUtils.generateErrorMessage(IDService.PROXY,method,req,res, ConnectorUtils.getMessageHttpMethodNotSupported(method), false, true);
								
						resFlushAndCloseSafe(res);
						
						return;
					}
				}
				
				// Dispatching al servizio 
				Proxy proxy = new Proxy();
				req.setAttribute(org.openspcoop2.core.constants.Costanti.PROTOCOL_NAME.getValue(), protocolContext.getProtocolName());
				proxy.doGet(req, res);
				
			}
			else{
				throw new CoreException(prefixFunction+"not supported");
			}
			
		} catch (Exception e) {
			
			StringBuilder bf = new StringBuilder();
			bf.append("RemoteAddr["+req.getRemoteAddr()+"] ");
			bf.append("RemoteHost["+req.getRemoteHost()+"] ");
			bf.append("RemotePort["+req.getRemotePort()+"] ");
			bf.append("RemoteUser["+req.getRemoteUser()+"] ");
			bf.append("LocalAddr["+req.getLocalAddr()+"] ");
			bf.append("LocalHost["+req.getLocalName()+"] ");
			bf.append("LocalPort["+req.getLocalPort()+"] ");
			bf.append("ServerName["+req.getServerName()+"] ");
			bf.append("ServerPort["+req.getServerPort()+"] ");
						
			if(logCore!=null){
				logCore.error(e.getMessage(),e);
				String msgError = "Detail: "+bf.toString();
				logCore.error(msgError);
			}
			else{
				logOpenSPCoop2Servlet.error(e.getMessage(),e);
				String msgError = "Detail: "+bf.toString();
				logOpenSPCoop2Servlet.error(msgError);
			}
			
			// log su file core
			StringBuilder bfLogError = new StringBuilder();
			try {
				ConnectorUtils.generateErrorMessage(IDService.OPENSPCOOP2_SERVLET,method,req,bfLogError, e.getMessage(), true, false);
				if(logCore!=null){
					logCore.error(bfLogError.toString());
				}
				else{
					logOpenSPCoop2Servlet.error(bfLogError.toString());
				}
			}catch(Throwable t) {
				if(logCore!=null){
					logCore.error("generateErrorMessage log failed: "+t.getMessage(),t);
				}
				else{
					logOpenSPCoop2Servlet.error("generateErrorMessage log failed: "+t.getMessage(),t);
				}
			}
			
			// messaggio di errore
			boolean errore404 = true;
			if(op2Properties!=null && op2Properties.isGenerazioneErroreProtocolloNonSupportato()){
				errore404 = false;
			}
			
			if(errore404){
				try {
					res.sendError(404,ConnectorUtils.generateError404Message(ConnectorUtils.getFullCodeProtocolUnsupported(IDService.OPENSPCOOP2_SERVLET)));
				}catch(Throwable t) {
					if(logCore!=null){
						logCore.error("sendError404 failed: "+t.getMessage(),t);
					}
					else{
						logOpenSPCoop2Servlet.error("sendError404 failed: "+t.getMessage(),t);
					}
				}
			}
			else{
				res.setStatus(500);

				try {
					ConnectorUtils.generateErrorMessage(IDService.OPENSPCOOP2_SERVLET,method,req,res, e.getMessage(), true, true);
				}catch(Throwable t) {
					if(logCore!=null){
						logCore.error("generateErrorMessage failed: "+t.getMessage(),t);
					}
					else{
						logOpenSPCoop2Servlet.error("generateErrorMessage failed: "+t.getMessage(),t);
					}
				}
				
				resFlushAndCloseSafe(res);
				
			}
			
		}
		

		
	}
	

	private void resFlushAndCloseSafe(HttpServletResponse res) {
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
