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

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.openspcoop2.core.api.constants.MethodType;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.engine.URLProtocolContext;
import org.openspcoop2.protocol.engine.constants.IDService;
import org.openspcoop2.protocol.manifest.constants.Costanti;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.utils.LoggerWrapperFactory;

/**
 * OpenSPCoop2Servlet
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
@SuppressWarnings("serial")
public class OpenSPCoop2Servlet extends HttpServlet {

	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		dispatch(req, resp, MethodType.DELETE);
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		dispatch(req, resp, MethodType.GET);
	}

	@Override
	protected void doHead(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		dispatch(req, resp, MethodType.HEAD);
	}

	@Override
	protected void doOptions(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		dispatch(req, resp, MethodType.OPTIONS);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		dispatch(req, resp, MethodType.POST);
	}

	@Override
	protected void doPut(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		dispatch(req, resp, MethodType.PUT);
	}

	@Override
	protected void doTrace(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		dispatch(req, resp, MethodType.TRACE);
	}

	
	private void dispatch(HttpServletRequest req, HttpServletResponse res,MethodType method) throws ServletException, IOException {
		
		Logger logCore = OpenSPCoop2Logger.getLoggerOpenSPCoopCore();
		Logger logOpenSPCoop2Servlet = LoggerWrapperFactory.getLogger("openspcoop2.startup");
		
		boolean doPost = MethodType.POST.equals(method);
		
		OpenSPCoop2Properties op2Properties = null;
		try {
			
			op2Properties = OpenSPCoop2Properties.getInstance();
			
			URLProtocolContext protocolContext = new URLProtocolContext(req, logCore);
			String function = protocolContext.getFunction();
			
			IProtocolFactory pf = ProtocolFactoryManager.getInstance().getProtocolFactoryByServletContext(protocolContext.getProtocol());
			if(pf==null){
				if(!Costanti.CONTEXT_EMPTY.equals(protocolContext.getProtocol()))
					throw new Exception("Non risulta registrato un protocollo con contesto ["+protocolContext.getProtocol()+"]");
				else
					throw new Exception("Non risulta registrato un protocollo con contesto speciale 'vuoto'");
			}
			
			if(function.equals(URLProtocolContext.PD_FUNCTION)){
				RicezioneContenutiApplicativiSOAPConnector r = new RicezioneContenutiApplicativiSOAPConnector();
				r.init();
				if(doPost)
					r.doPost(req, res);
				else
					r.engine(req, res, method);
			}
			else if(function.equals(URLProtocolContext.PDtoSOAP_FUNCTION)){
				RicezioneContenutiApplicativiHTTPtoSOAPConnector r = new RicezioneContenutiApplicativiHTTPtoSOAPConnector();
				r.init();
				if(doPost)
					r.doPost(req, res);
				else
					r.engine(req, res, method);
			}
			else if(function.equals(URLProtocolContext.PA_FUNCTION)){
				RicezioneBusteSOAPConnector r = new RicezioneBusteSOAPConnector();
				r.init();
				if(doPost)
					r.doPost(req, res);
				else
					r.engine(req, res, method);
			}
			else if(function.equals(URLProtocolContext.IntegrationManager_FUNCTION)){
				
				boolean wsdl = false;
				if(MethodType.GET.equals(method)){
					Enumeration<?> parameters = req.getParameterNames();
					while(parameters.hasMoreElements()){
						String key = (String) parameters.nextElement();
						String value = req.getParameter(key);
						if("wsdl".equalsIgnoreCase(key) && (value==null || "".equals(value)) ){
							// richiesta del wsdl
							if(op2Properties!=null && op2Properties.isGenerazioneWsdlIntegrationManagerEnabled()==false){
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
				
				if(!doPost && !wsdl){
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
								
						try{
							res.getOutputStream().flush();
						}catch(Exception eClose){}
						try{
							res.getOutputStream().close();
						}catch(Exception eClose){}
						
						return;
					}
				}
								
				// Dispatching al servizio di IntegrationManager implementato tramite CXF
				String forwardUrl = "/"+URLProtocolContext.IntegrationManager_ENGINE+"/"+protocolContext.getFunctionParameters();
				req.setAttribute(org.openspcoop2.core.constants.Costanti.PROTOCOLLO, protocolContext.getProtocol());
				req.setAttribute(org.openspcoop2.core.constants.Costanti.INTEGRATION_MANAGER_ENGINE_AUTHORIZED, true);
				RequestDispatcher dispatcher = req.getRequestDispatcher(forwardUrl);
				dispatcher.forward(req, res);
				
			}
			else if(function.equals(URLProtocolContext.CheckPdD_FUNCTION)){
				
				if(MethodType.GET.equals(method)==false){
					// messaggio di errore
					boolean errore404 = false;
					if(op2Properties!=null && !op2Properties.isGenerazioneErroreHttpMethodUnsupportedCheckPdDEnabled()){
						errore404 = true;
					}
					
					if(errore404){
						res.sendError(404,ConnectorUtils.generateError404Message(ConnectorUtils.getFullCodeHttpMethodNotSupported(IDService.CHECK_PDD, method)));
						return;
					}
					else{
						
						res.setStatus(500);
						
						ConnectorUtils.generateErrorMessage(IDService.CHECK_PDD,method,req,res, ConnectorUtils.getMessageHttpMethodNotSupported(method), false, true);
								
						try{
							res.getOutputStream().flush();
						}catch(Exception eClose){}
						try{
							res.getOutputStream().close();
						}catch(Exception eClose){}
						
						return;
					}
				}
				
				// Dispatching al servizio 
				CheckStatoPdD checkStatoPdD = new CheckStatoPdD();
				req.setAttribute(org.openspcoop2.core.constants.Costanti.PROTOCOLLO, protocolContext.getProtocol());
				checkStatoPdD.doGet(req, res);
				
			}
			else if(function.equals(URLProtocolContext.API_ENGINE+"/"+URLProtocolContext.API_FUNCTION_PD)){
				if(op2Properties.isAPIServicesEnabled()==false){
					res.sendError(404,ConnectorUtils.generateError404Message(ConnectorUtils.getFullCodeFunctionUnsupported(IDService.PORTA_DELEGATA_API)));
					return;
				}
				RicezioneContenutiApplicativiAPIConnector r = new RicezioneContenutiApplicativiAPIConnector();
				r.init();
				r.engine(req, res);
			}
			else if(function.equals(URLProtocolContext.API_ENGINE+"/"+URLProtocolContext.API_FUNCTION_PA)){
				if(op2Properties.isAPIServicesEnabled()==false){
					res.sendError(404,ConnectorUtils.generateError404Message(ConnectorUtils.getFullCodeFunctionUnsupported(IDService.PORTA_APPLICATIVA_API)));
					return;
				}
				RicezioneBusteAPIConnector r = new RicezioneBusteAPIConnector();
				r.init();
				r.engine(req, res);
			}
			else if(function.equals(URLProtocolContext.API_ENGINE+"/"+URLProtocolContext.API_FUNCTION_MessageBox)){
				if(op2Properties.isAPIServicesEnabled()==false){
					res.sendError(404,ConnectorUtils.generateError404Message(ConnectorUtils.getFullCodeFunctionUnsupported(IDService.INTEGRATION_MANAGER_API)));
					return;
				}
				// TODO
				throw new Exception("Service ["+function+"] not implemented (TODO)");
			}
			else{
				throw new Exception("Service ["+function+"] not supported");
			}
			
		} catch (Exception e) {
			
			StringBuffer bf = new StringBuffer();
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
				logCore.error("Detail: "+bf.toString());
			}
			else{
				logOpenSPCoop2Servlet.error(e.getMessage(),e);
				logOpenSPCoop2Servlet.error("Detail: "+bf.toString());
			}
			
			// log su file core
			StringBuffer bfLogError = new StringBuffer();
			ConnectorUtils.generateErrorMessage(IDService.OPENSPCOOP2_SERVLET,method,req,bfLogError, e.getMessage(), true, false);
			if(logCore!=null){
				logCore.error(bfLogError.toString());
			}
			else{
				logOpenSPCoop2Servlet.error(bfLogError.toString());
			}
			
			// messaggio di errore
			boolean errore404 = true;
			if(op2Properties!=null && op2Properties.isGenerazioneErroreProtocolloNonSupportato()){
				errore404 = false;
			}
			
			if(errore404){
				res.sendError(404,ConnectorUtils.generateError404Message(ConnectorUtils.getFullCodeProtocolUnsupported(IDService.OPENSPCOOP2_SERVLET)));
			}
			else{
				res.setStatus(500);

				ConnectorUtils.generateErrorMessage(IDService.OPENSPCOOP2_SERVLET,method,req,res, e.getMessage(), true, true);
				
				try{
					res.getOutputStream().flush();
				}catch(Exception eClose){}
				try{
					res.getOutputStream().close();
				}catch(Exception eClose){}
				
			}
			
		}
		

		
	}
}
