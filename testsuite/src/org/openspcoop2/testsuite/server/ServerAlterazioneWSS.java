/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it). 
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


import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.axis.Message;
import org.openspcoop2.testsuite.axis14.Axis14SoapUtils;



/**
 * Server 
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

@SuppressWarnings("serial")
public class ServerAlterazioneWSS extends ServerCore{

	/**
	 * 
	 */
	public ServerAlterazioneWSS() {
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
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			int read=0;
			while((read=in.read()) != -1){
				bout.write(read);
			}
			in.close();

			String urlForward = request.getParameter("forward");
			if(urlForward==null){
				throw new Exception("Proprieta 'forward' non fornita");
			}else{
				urlForward = urlForward.trim();
			}
			
			Message msg = Axis14SoapUtils.build(bout.toByteArray(), false);
			
			// Alterazione msg
			msg.getSOAPBody().removeContents();
			msg.getSOAPBody().addChildElement("AlterazioneMessaggio");
			msg.saveChanges();
			
			// forward
			org.apache.axis.soap.SOAPConnectionImpl connection = new  org.apache.axis.soap.SOAPConnectionImpl(); 	    
			java.net.URL urlConnection = new  java.net.URI(urlForward).toURL();
			Message responseMsg = (org.apache.axis.Message) connection.call(msg,urlConnection);
			

			if(responseMsg!=null && responseMsg.getContentLength()>0){
				response.setContentLength((int)responseMsg.getContentLength());
				response.setContentType(responseMsg.getContentType(new org.apache.axis.soap.SOAP11Constants()));
				BufferedOutputStream bos=new BufferedOutputStream(response.getOutputStream());
				responseMsg.writeTo(bos);
				bos.flush();
				bos.close();
			}
			
			this.log.info("Gestione busta effettata, messaggio gestito:\n"+bout.toString());

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
