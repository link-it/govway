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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.pdd.services.RicezioneContenutiApplicativiHTTPtoSOAP;

/**
 * Servlet che serve per creare un tunnel da un servizio che non conosce SOAP verso OpenSPCoop (che utilizza SOAP)
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class RicezioneContenutiApplicativiHTTPtoSOAPConnector extends HttpServlet {

	 /**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	/* ********  F I E L D S  P R I V A T I S T A T I C I  ******** */

	/** Variabile che indica il Nome del modulo dell'architettura di OpenSPCoop rappresentato da questa classe */
	public final static String ID_MODULO = "RicezioneContenutiApplicativiHTTP";


	@Override public void doPost(HttpServletRequest req, HttpServletResponse res)
	throws ServletException, IOException {
	
		RicezioneContenutiApplicativiHTTPtoSOAP httpToSoapConnector = new RicezioneContenutiApplicativiHTTPtoSOAP();
		
		HttpServletConnectorInMessage httpIn = null;
		try{
			httpIn = new HttpServletConnectorInMessage(req, ID_MODULO);
		}catch(Exception e){
			OpenSPCoop2Logger.getLoggerOpenSPCoopCore().error("HttpServletConnectorInMessage init error: "+e.getMessage(),e);
			throw new ServletException(e.getMessage(),e);
		}
		
		HttpServletConnectorOutMessage httpOut = null;
		try{
			httpOut = new HttpServletConnectorOutMessage(res);
		}catch(Exception e){
			OpenSPCoop2Logger.getLoggerOpenSPCoopCore().error("HttpServletConnectorOutMessage init error: "+e.getMessage(),e);
			throw new ServletException(e.getMessage(),e);
		}
			
		try{
			httpToSoapConnector.process(httpIn, httpOut);
		}catch(Exception e){
			OpenSPCoop2Logger.getLoggerOpenSPCoopCore().error("SoapConnector.process error: "+e.getMessage(),e);
			throw new ServletException(e.getMessage(),e);
		}
		
	}
	
	@Override public void doGet(HttpServletRequest req, HttpServletResponse res)
	throws ServletException, IOException {		
		
		String versione = "Porta di Dominio "+OpenSPCoop2Properties.getInstance().getPddDetailsForServices();
		
		res.setStatus(500);
		StringBuffer risposta = new StringBuffer();
		risposta.append("<html>\n");
		risposta.append("<body>\n");
		risposta.append("<h1>" + req.getContextPath() + req.getServletPath() + "/&lt;NOME_PD&gt;</h1>\n");
		risposta.append("<p>"+versione+"</p>\n");
		risposta.append("<p>Method HTTP GET non supportato</p>\n");
		risposta.append("<i>Servizio utilizzabile per l'invocazione di Porte Delegate esposte dalla PdD OpenSPCoop v2, con messaggi xml non imbustati nel protocollo SOAP</i>\n");
		risposta.append("</body>\n");
		risposta.append("</html>\n");

		res.getOutputStream().write(risposta.toString().getBytes());
	}

}
