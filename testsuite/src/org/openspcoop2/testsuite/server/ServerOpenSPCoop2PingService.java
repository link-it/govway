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



package org.openspcoop2.testsuite.server;


import java.io.IOException;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openspcoop2.message.utils.ServletTestService;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;



/**
 * Server PingService
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
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


	public void engine(HttpServletRequest request,HttpServletResponse response, Properties pHeaderRisposta) throws IOException, ServletException{
		
		ServletTestService servlet = new ServletTestService(this.log, 
				this.testsuiteProperties.getDumpRequestThresholdRequestDump(), this.testsuiteProperties.getDumpRequestRepository());
		servlet.doEngine(request, response, true, pHeaderRisposta);
		
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
				this.log.error("Mittente della richiesta non presente");
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
		pHeaderRisposta.put(this.testsuiteProperties.getHeaderRispostaServletName(), "PingService");
		this.engine(request, response, pHeaderRisposta);
		
	}
	
	@Override
	public void doPost(HttpServletRequest request,HttpServletResponse response) throws IOException, ServletException{
		doGet(request,response);
	}
}
