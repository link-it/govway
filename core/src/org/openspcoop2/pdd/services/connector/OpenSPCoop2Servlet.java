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

package org.openspcoop2.pdd.services.connector;

import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.engine.URLProtocolContext;
import org.openspcoop2.protocol.manifest.constants.Costanti;
import org.openspcoop2.protocol.sdk.IProtocolFactory;

/**
 * OpenSPCoop2Servlet
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
@SuppressWarnings("serial")
public class OpenSPCoop2Servlet extends HttpServlet {

	@Override public void doPost(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
		dispatch(req, res, true);
	}
	
	@Override public void doGet(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
		dispatch(req, res, false);
	}
	
	private void dispatch(HttpServletRequest req, HttpServletResponse res,boolean doPost) throws ServletException, IOException {
		
		Logger logCore = OpenSPCoop2Logger.getLoggerOpenSPCoopCore();
		Logger logOpenSPCoop2Servlet = Logger.getLogger("openspcoop2.startup");
		
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
					r.doGet(req, res);
			}
			else if(function.equals(URLProtocolContext.PDtoSOAP_FUNCTION)){
				RicezioneContenutiApplicativiHTTPtoSOAPConnector r = new RicezioneContenutiApplicativiHTTPtoSOAPConnector();
				r.init();
				if(doPost)
					r.doPost(req, res);
				else
					r.doGet(req, res);
			}
			else if(function.equals(URLProtocolContext.PA_FUNCTION)){
				RicezioneBusteSOAPConnector r = new RicezioneBusteSOAPConnector();
				r.init();
				if(doPost)
					r.doPost(req, res);
				else
					r.doGet(req, res);
			}
			else if(function.equals(URLProtocolContext.IntegrationManager_FUNCTION)){
				
				boolean wsdl = false;
				Enumeration<?> parameters = req.getParameterNames();
				while(parameters.hasMoreElements()){
					String key = (String) parameters.nextElement();
					String value = req.getParameter(key);
					if("wsdl".equalsIgnoreCase(key) && (value==null || "".equals(value)) ){
						// richiesta del wsdl
						if(op2Properties!=null && op2Properties.isGenerazioneWsdlIntegrationManagerEnabled()==false){
							res.sendError(404, ConnectorUtils.generateError404Message(ConnectorCostanti.INTEGRATION_MANAGER_WSDL));
							return;
						}
						else{
							wsdl = true;
							break;
						}
					}
				}
				
				if(!doPost && !wsdl){
					// messaggio di errore
					boolean errore404 = false;
					if(op2Properties!=null && !op2Properties.isGenerazioneErroreHttpGetIntegrationManagerEnabled()){
						errore404 = true;
					}
					
					if(errore404){
						res.sendError(404,ConnectorUtils.generateError404Message(ConnectorCostanti.INTEGRATION_MANAGER_HTTP_GET));
						return;
					}
					else{
					
						res.setStatus(500);
						
						res.getOutputStream().write(ConnectorUtils.generateErrorMessage(req, ConnectorCostanti.METHOD_HTTP_GET_NOT_SUPPORTED, false, true).getBytes());
								
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
				
				// Dispatching al servizio di IntegrationManager implementato tramite CXF
				String forwardUrl = "/"+URLProtocolContext.CheckPdD_FUNCTION;
				if(protocolContext.getFunctionParameters()!=null){
					forwardUrl = forwardUrl+"/"+protocolContext.getFunctionParameters();
				}
				req.setAttribute(org.openspcoop2.core.constants.Costanti.PROTOCOLLO, protocolContext.getProtocol());
				RequestDispatcher dispatcher = req.getRequestDispatcher(forwardUrl);
				dispatcher.forward(req, res);
				
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
			if(logCore!=null){
				logCore.error(ConnectorUtils.generateErrorMessage(req, e.getMessage(), true, false));
			}
			else{
				logOpenSPCoop2Servlet.error(ConnectorUtils.generateErrorMessage(req, e.getMessage(), true, false));
			}
			
			// messaggio di errore
			boolean errore404 = true;
			if(op2Properties!=null && op2Properties.isGenerazioneErroreProtocolloNonSupportato()){
				errore404 = false;
			}
			
			if(errore404){
				res.sendError(404,ConnectorUtils.generateError404Message(ConnectorCostanti.PROTOCOL_NOT_SUPPORTED));
			}
			else{
				res.setStatus(500);

				res.getOutputStream().write(ConnectorUtils.generateErrorMessage(req, e.getMessage(), true, true).getBytes());
				
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
