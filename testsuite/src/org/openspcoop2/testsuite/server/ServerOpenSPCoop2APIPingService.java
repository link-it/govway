/*
 * OpenSPCoop - Customizable API Gateway 
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



package org.openspcoop2.testsuite.server;


import java.io.IOException;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;



/**
 * Server EchoService
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

@SuppressWarnings("serial")
public class ServerOpenSPCoop2APIPingService extends ServerCore{


	/**
	 * 
	 */
	public ServerOpenSPCoop2APIPingService() {
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
		pHeaderRisposta.put(this.testsuiteProperties.getHeaderRispostaServletName(), "APIPingService");
		
		ServerOpenSPCoop2PingService ping = new ServerOpenSPCoop2PingService();
		ping.engine(request, response, pHeaderRisposta);
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
}
