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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.axis.Message;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.testsuite.axis14.Axis14SoapUtils;



/**
 * Server Generale utilizzato per il profilo di collaborazione Asincrono Simmetrico, modalita' asincrona
 * 
 * @author Andi Rexha (rexha@openspcoop.org)
 * @author $Author$
 * @version $Rev$, $Date$
 */


@SuppressWarnings("serial")
public class ServerAsincronoSimmetrico_modalitaAsincrona extends ServerCore {


	/**
	 * 
	 */
	public ServerAsincronoSimmetrico_modalitaAsincrona() {
		super();
		// TODO Auto-generated constructor stub
	}




	@Override
	public void init() {}




	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response)
	throws IOException, ServletException {
		try{
			// Lettura Richiesta
			InputStream in=request.getInputStream();
			//int length=request.getContentLength();
			String type=request.getContentType();
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			int read=0;
			while((read=in.read()) != -1){
				bout.write(read);
			}
			in.close();

			String id = request.getHeader(this.testsuiteProperties.getIdMessaggioTrasporto());
			if(id==null){
				this.log.error("[AsincronoSimmetrico_modalitaAsincrona] id della richiesta non presente");
			} 
			String destinatario = request.getHeader(this.testsuiteProperties.getDestinatarioTrasporto());
			if(destinatario==null){
				this.log.error("[AsincronoSimmetrico_modalitaAsincrona] destinatario della richiesta non presente");
			} 
			
			String protocollo = request.getParameter("protocol");
			if(protocollo==null){
				protocollo = ProtocolFactoryManager.getInstance().getDefaultProtocolFactory().getProtocol();
			}
			
			
			// Ritorno un msg senza Body
			Message msgRisposta = Axis14SoapUtils.build(bout.toByteArray(), false);
			msgRisposta.getSOAPBody().removeContents();
			msgRisposta.saveChanges();
			
			response.setContentLength((int)msgRisposta.getContentLength());
			response.setContentType(msgRisposta.getContentType(new org.apache.axis.soap.SOAP11Constants()));
			msgRisposta.writeTo(response.getOutputStream());

			String checkTracciaString = request.getParameter("checkTraccia");
			boolean checkTraccia = true;
			if(checkTracciaString != null) {
				checkTraccia = Boolean.parseBoolean(checkTracciaString.trim());
			}
			
			if(this.testsuiteProperties.traceArrivedIntoDB() && checkTraccia){
				if(id!=null)
					this.tracciaIsArrivedIntoDatabase(id,destinatario,protocollo);
			}
			
			this.log.info("[AsincronoSimmetrico_modalitaAsincrona] Gestione richiesta con id="+id+" effettata, messaggio gestito:\n"+bout.toString());



			// GenerazioneRisposta
			ServerAsincronoSimmetricoThreadConsegnaRisposta tR =
				new ServerAsincronoSimmetricoThreadConsegnaRisposta(true,this.log,this.testsuiteProperties,
						type.contains("multipart/related"),bout.toByteArray(),id,protocollo);
			tR.start();

		}catch(Exception e){
			this.log.error("Errore durante la gestione di una richiesta asincrona simmetrica modalita asincrona: "+e.getMessage(),e);
			throw new ServletException("Errore durante la gestione di una richiesta asincrona simmetrica modalita asincrona: "+e.getMessage(),e);
		}
	}



	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response)
	throws IOException, ServletException {
		doGet(request, response);
	}
}
