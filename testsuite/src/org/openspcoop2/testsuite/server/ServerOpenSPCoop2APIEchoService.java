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



package org.openspcoop2.testsuite.server;


import java.io.IOException;
import java.util.Properties;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.openspcoop2.utils.transport.http.HttpRequestMethod;



/**
 * Server EchoService
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

@SuppressWarnings("serial")
public class ServerOpenSPCoop2APIEchoService extends ServerCore{


	/**
	 * 
	 */
	public ServerOpenSPCoop2APIEchoService() {
		super();
		// TODO Auto-generated constructor stub
	}


	@Override
	public void init() {}

	
	private void dispatch(HttpServletRequest request,HttpServletResponse response) throws IOException, ServletException{
		
		Properties pHeaderRisposta = this.testsuiteProperties.getHeaderRisposta();
		if(pHeaderRisposta==null){
			pHeaderRisposta = new Properties();
		}
		pHeaderRisposta.put(this.testsuiteProperties.getHeaderRispostaServletName(), "APIEchoService");
		
		ServerOpenSPCoop2EchoService echo = new ServerOpenSPCoop2EchoService();
		echo.engine(request, response, pHeaderRisposta);
	}
	
	
	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		this.dispatch(req, resp);
	}


	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		this.dispatch(req, resp);
	}


	@Override
	protected void doHead(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		this.dispatch(req, resp);
	}


	@Override
	protected void doOptions(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		this.dispatch(req, resp);
	}


	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		this.dispatch(req, resp);
	}


	@Override
	protected void doPut(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		this.dispatch(req, resp);
	}


	@Override
	protected void doTrace(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		this.dispatch(req, resp);
	}
	
	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		HttpRequestMethod m = HttpRequestMethod.valueOf(req.getMethod().toUpperCase());
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
		case PATCH:
		case LINK:
		case UNLINK:
			dispatch(req, resp);
			break;
			
		default:
			super.service(req, resp); // richiamo implementazione originale che genera errore: Method XXX is not defined in RFC 2068 and is not supported by the Servlet API
			break;
		}
	}
}
