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



package org.openspcoop2.testsuite.server;


import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openspcoop2.message.ServletTestService;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;



/**
 * Server PingService
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author: apoli $
 * @version $Rev: 10489 $, $Date: 2015-01-13 10:15:51 +0100 (Tue, 13 Jan 2015) $
 */

@SuppressWarnings("serial")
public class ServerOpenSPCoop2PingService extends ServerCore{

	/**
	 * 
	 */
	public ServerOpenSPCoop2PingService() {
		super();
		// TODO Auto-generated constructor stub
	}


	@Override
	public void init() {}


	@Override
	public void doGet(HttpServletRequest request,HttpServletResponse response) throws IOException, ServletException{

		ServletTestService servlet = new ServletTestService(this.log);
		servlet.doEngine(request, response, true);
		
		try{

			String id=request.getHeader(this.testsuiteProperties.getIdMessaggioTrasporto());
			if(id==null){
				this.log.error("Id della richiesta non presente");
			} 
			String destinatario = request.getHeader(this.testsuiteProperties.getDestinatarioTrasporto());
			if(destinatario==null){
				this.log.error("Destinatario della richiesta non presente");
			} 
			String mittente = request.getHeader(this.testsuiteProperties.getMittenteTrasporto());
			if(mittente==null){
				this.log.error("Destinatario della richiesta non presente");
			} 
			
			String protocollo = request.getParameter("protocol");
			if(protocollo==null){
				protocollo = ProtocolFactoryManager.getInstance().getDefaultProtocolFactory().getProtocol();
			}
			
			String dominio = destinatario;
			String traceIsArrived = request.getParameter("traceIsArrived");
			if(traceIsArrived!=null){
				if("mittente".equalsIgnoreCase(traceIsArrived.trim())){
					dominio = mittente;
				}
			}
			
			if(this.testsuiteProperties.traceArrivedIntoDB()){
				if(id!=null){
					this.tracciaIsArrivedIntoDatabase(id,dominio,protocollo);
				}
			}
			
			//this.log.info("Gestione richiesta con id="+id+" effettata, messaggio gestito:\n"+bout.toString());
			this.log.info("Gestione richiesta con id="+id+" effettata");

		}catch(Exception e){
			this.log.error("Errore durante la gestione di una richiesta: "+e.getMessage());
			throw new ServletException("Errore durante la gestione di una richiesta: "+e.getMessage());
		}
	}

	@Override
	public void doPost(HttpServletRequest request,HttpServletResponse response) throws IOException, ServletException{
		doGet(request,response);
	}
}
