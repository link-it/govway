/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2014 Link.it srl (http://link.it). All rights reserved. 
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

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.protocol.engine.URLProtocolContext;

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
		
		try {
			
			URLProtocolContext protocolContext = new URLProtocolContext(req, logCore);
			String function = protocolContext.getFunction();
			
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
				
				// Dispatching al servizio di IntegrationManager implementato tramite CXF
				String forwardUrl = "/"+URLProtocolContext.IntegrationManager_FUNCTION+"/"+protocolContext.getFunctionParameters();
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
			
			res.setStatus(500);
			
			StringBuffer risposta = new StringBuffer();
			risposta.append("<html>\n");
			risposta.append("<body>\n");
			risposta.append("<h1>" + req.getContextPath() + req.getContextPath() +"</h1>\n");
			risposta.append("<p>OpenSPCoop2</p>\n");
			risposta.append("<p>"+e.getMessage()+"</p>\n");
			risposta.append("<i>Enabled services: PD, PA, PDtoSOAP, checkPdD, IntegrationManager</i>\n");
			risposta.append("<i>Sito ufficiale del progetto: http://www.openspcoop2.org</i>\n");
			risposta.append("</body>\n");
			risposta.append("</html>\n");

			res.getOutputStream().write(risposta.toString().getBytes());
			
			try{
				res.getOutputStream().flush();
			}catch(Exception eClose){}
			try{
				res.getOutputStream().close();
			}catch(Exception eClose){}
			
		}
		

		
	}
}
