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
public class ServerVerificaFunzionalitaScartaBody extends ServerCore{

	/**
	 * 
	 */
	public ServerVerificaFunzionalitaScartaBody() {
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
			
			// Verifico che siano presenti solo i 2 attachments spediti e il manifest (e quindi che il body originale non sia diventato un terzo attach)
			if(msg.countAttachments()==0){
				throw new Exception("Attachments non presenti");
			}
			if(msg.countAttachments()!=2){
				throw new Exception("Attesi 2 Attachments (trovati "+msg.countAttachments()+")");
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
			java.net.URL urlConnection = new java.net.URL(urlForward);
			Message responseMsg = (org.apache.axis.Message) connection.call(msg,urlConnection);
			

			// Verifico che siano presenti solo 1 attachments e il manifest 
			// Questo poiche' lo sbustamento della PA aveva la gestione manifest attiva. Quindi ha riportato un attachments di ruolo richiesta come body
			// Tale messaggio con 1 attach e 1 body e' ritornato indietro dal servizio di Echo, ritornato alla PA con funzionalita' scarta body.
			// Quindi il messaggio di risposta deve contenere solo 1 attachments.
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
