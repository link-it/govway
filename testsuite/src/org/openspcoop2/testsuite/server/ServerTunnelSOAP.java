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


import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openspcoop2.protocol.engine.ProtocolFactoryManager;



/**
 * Server Generale utilizzato per profili di collaborazione OneWay, Sincrono e AsincronoAsimmetrico
 * 
 * @author Andi Rexha (rexha@openspcoop.org)
 * @author $Author$
 * @version $Rev$, $Date$
 */

@SuppressWarnings("serial")
public class ServerTunnelSOAP extends ServerCore{

	/**
	 * 
	 */
	public ServerTunnelSOAP() {
		super();
		// TODO Auto-generated constructor stub
	}


	@Override
	public void init() {}


	@Override
	public void doGet(HttpServletRequest request,HttpServletResponse response) throws IOException, ServletException{

		try{

			// Lettura Richiesta
			InputStream in=request.getInputStream();
			int length=request.getContentLength();
			String type=request.getContentType();
			
			this.log.debug("Ricevuto messaggio con content type: ["+type+"]");
			
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			int read=0;
			while((read=in.read()) != -1){
				bout.write(read);
			}
			in.close();
			
			this.log.debug("Inizio gestione...");
			
			
			String protocollo = request.getParameter("protocol");
			if(protocollo==null){
				protocollo = ProtocolFactoryManager.getInstance().getDefaultProtocolFactory().getProtocol();
			}
			
			// Verifica non presenza di soap
			if(bout.toString().indexOf("Envelope")>=0){
				throw new Exception("Messaggio atteso come xml e' contenuto invece in una busta SOAP");
			}
			
			// opzioni tunnel SOAP
            String tunnelSoap = request.getParameter("OpenSPCoop2TunnelSOAP");
            boolean tunnel = false;
            if(tunnelSoap!=null){
                    tunnelSoap = tunnelSoap.trim();
                    if("true".equals(tunnelSoap))
                            tunnel = true;
            }
            String tunnelSoapMimeType = request.getParameter("OpenSPCoop2TunnelSOAPMimeType");
            if(tunnelSoapMimeType!=null){
                    tunnelSoapMimeType = tunnelSoapMimeType.trim();
            }
            
            // no soap
            String produciMsgSOAPVuotoRisposta=request.getParameter("nosoap");
            boolean produciMsgSoap = true;
            if(produciMsgSOAPVuotoRisposta!=null && "true".equals(produciMsgSOAPVuotoRisposta.trim())){
                    produciMsgSoap = false;
            }

			String id=request.getHeader(this.testsuiteProperties.getIdMessaggioTrasporto());
			if(id==null){
				this.log.error("Id della richiesta non presente");
			} 
			String destinatario = request.getHeader(this.testsuiteProperties.getDestinatarioTrasporto());
			if(destinatario==null){
				this.log.error("Destinatario della richiesta non presente");
			} 

			if(tunnel){
				response.setHeader("X-OpenSPCoop2-TunnelSOAP", "true");
                if(tunnelSoapMimeType!=null)
                	response.setHeader("X-OpenSPCoop2-TunnelSOAP-MimeType",tunnelSoapMimeType);
			}
			
			if(produciMsgSoap){
				response.setContentLength(length);
				type = type.replace("charset=UTF-8;",""); // bug fix per axiom.
				type = type.trim();
				/*if(type.endsWith(";"))
					type = type.substring(0,(type.length()-1));*/
				this.log.debug("Imposto content type: ["+type+"]");
				response.setContentType(type);
				BufferedOutputStream bos=new BufferedOutputStream(response.getOutputStream());
				bos.write(bout.toByteArray());
				bos.close();
			}

			
			String checkTracciaString = request.getParameter("checkTraccia");
			boolean checkTraccia = true;
			if(checkTracciaString != null) {
				checkTraccia = Boolean.parseBoolean(checkTracciaString.trim());
			}
			
			if(this.testsuiteProperties.traceArrivedIntoDB() && checkTraccia){
				if(id!=null){
					this.tracciaIsArrivedIntoDatabase(id,destinatario,protocollo);
				}
			}
			
			this.log.info("Gestione richiesta con id="+id+" effettata, messaggio gestito:\n"+bout.toString());

		}catch(Exception e){
			this.log.error("Errore durante la gestione di una richiesta: "+e.getMessage(),e);
			throw new ServletException("Errore durante la gestione di una richiesta: "+e.getMessage(),e);
		}
	}

	@Override
	public void doPost(HttpServletRequest request,HttpServletResponse response) throws IOException, ServletException{
		doGet(request,response);
	}
}
