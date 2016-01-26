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
public class ServerVerificaFunzionalitaAllegaBody extends ServerCore{

	/**
	 * 
	 */
	public ServerVerificaFunzionalitaAllegaBody() {
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
			
			// verifica messaggio di richiesta (deve essere presente 1 attach che era il body originale della richiesta)
			if(msg.countAttachments()==0){
				throw new Exception("Attachments non presenti");
			}
			if(msg.countAttachments()>1){
				throw new Exception("Atteso solo 1 Attachment (trovati "+msg.countAttachments()+")");
			}
			
			if(!msg.getSOAPBody().hasChildNodes()){
				throw new Exception("Manifest non presente nel body");
			}
			
			if(msg.getSOAPBody().getChildNodes()==null){
				throw new Exception("Manifest non presente nel body");
			}
			
			if(msg.getSOAPBody().getChildNodes().getLength()!=1){
				throw new Exception("Manifest non correttamente definito nel body ("+msg.getSOAPBody().getChildNodes().getLength()+")");
			}
			
			if(!"Descrizione".equals(msg.getSOAPBody().getFirstChild().getLocalName())){
				throw new Exception("Manifest non correttamente definito nel body: CHILD("+msg.getSOAPBody().getFirstChild().getLocalName()+")");
			}
			
			// forward
			org.apache.axis.soap.SOAPConnectionImpl connection = new  org.apache.axis.soap.SOAPConnectionImpl(); 	    
			javax.xml.messaging.URLEndpoint urlConnection = new  javax.xml.messaging.URLEndpoint(urlForward);
			Message responseMsg = (org.apache.axis.Message) connection.call(msg,urlConnection);
			
			// verifica messaggio di risposta (deve essere presente 1 attach che era il body originale della risposta)
			if(responseMsg.countAttachments()==0){
				throw new Exception("Attachments non presenti nella risposta");
			}
			if(responseMsg.countAttachments()>1){
				throw new Exception("Atteso solo 1 Attachment nella risposta (trovati "+responseMsg.countAttachments()+")");
			}
			
			if(!responseMsg.getSOAPBody().hasChildNodes()){
				throw new Exception("Manifest non presente nel body della risposta");
			}
			
			if(responseMsg.getSOAPBody().getChildNodes()==null){
				throw new Exception("Manifest non presente nel body della risposta");
			}
			
			if(responseMsg.getSOAPBody().getChildNodes().getLength()!=1){
				throw new Exception("Manifest non correttamente definito nel body della risposta ("+responseMsg.getSOAPBody().getChildNodes().getLength()+")");
			}
			
			if(!"Descrizione".equals(responseMsg.getSOAPBody().getFirstChild().getLocalName())){
				throw new Exception("Manifest non correttamente definito nel body della risposta: CHILD("+responseMsg.getSOAPBody().getFirstChild().getLocalName()+")");
			}
			

			// spedisco indietro la risposta
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
