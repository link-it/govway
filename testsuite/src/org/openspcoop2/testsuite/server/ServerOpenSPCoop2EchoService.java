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



package org.openspcoop2.testsuite.server;


import java.io.IOException;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openspcoop2.message.ServletTestService;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;



/**
 * Server EchoService
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

@SuppressWarnings("serial")
public class ServerOpenSPCoop2EchoService extends ServerCore{

	/**
	 * 
	 */
	public ServerOpenSPCoop2EchoService() {
		super();
		// TODO Auto-generated constructor stub
	}


	@Override
	public void init() {}


	public void engine(HttpServletRequest request,HttpServletResponse response, Properties pHeaderRisposta) throws IOException, ServletException{
		
		ServletTestService servlet = new ServletTestService(this.log);
		servlet.doEngine(request, response, false, pHeaderRisposta);
		
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

			String checkTracciaString = request.getParameter("checkTraccia");
			boolean checkTraccia = true;
			if(checkTracciaString != null) {
				checkTraccia = Boolean.parseBoolean(checkTracciaString.trim());
			}

			if(this.testsuiteProperties.traceArrivedIntoDB() && checkTraccia){
				if(id!=null){
					this.tracciaIsArrivedIntoDatabase(id,dominio,protocollo);
				}
			}
			
			//this.log.info("Gestione richiesta con id="+id+" effettata, messaggio gestito:\n"+bout.toString());
			this.log.info("Gestione richiesta con id="+id+" effettata");

		}catch(Exception e){
			this.log.error("Errore durante la gestione di una richiesta: "+e.getMessage(),e);
			throw new ServletException("Errore durante la gestione di una richiesta: "+e.getMessage(),e);
		}
	}

	@Override
	public void doGet(HttpServletRequest request,HttpServletResponse response) throws IOException, ServletException{
		
		Properties pHeaderRisposta = this.testsuiteProperties.getHeaderRisposta();
		if(pHeaderRisposta==null){
			pHeaderRisposta = new Properties();
		}
		pHeaderRisposta.put(this.testsuiteProperties.getHeaderRispostaServletName(), "EchoService");
		this.engine(request, response, pHeaderRisposta);
		
	}
	
	@Override
	public void doPost(HttpServletRequest request,HttpServletResponse response) throws IOException, ServletException{
		doGet(request,response);
	}
}
