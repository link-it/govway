/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2026 Link.it srl (https://link.it). 
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
import java.io.OutputStream;
import java.net.HttpURLConnection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.axis.Message;
import org.openspcoop2.testsuite.axis14.Axis14SoapUtils;
import org.openspcoop2.testsuite.core.StrutturaEnvelopeSoap11;



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
			
			String propStrutturaEnvelope = request.getParameter("strutturaEnvelope");
			if(propStrutturaEnvelope!=null && Boolean.parseBoolean(propStrutturaEnvelope.trim())) {
				// XML Signature Wrapping: la struttura dell'Envelope firmato viene alterata (es. aggiunta di un Body non firmato)
				// e il messaggio viene inoltrato così com'è, senza passare da un parser SOAP che lo normalizzerebbe
				inoltroStrutturaEnvelopeAlterata(request, response, bout.toString(), urlForward);
				return;
			}

			Message msg = Axis14SoapUtils.build(bout.toByteArray(), false);
			
			// Alterazione msg
			msg.getSOAPBody().removeContents();
			msg.getSOAPBody().addChildElement("AlterazioneMessaggio");
			msg.saveChanges();
			
			// forward
			org.apache.axis.soap.SOAPConnectionImpl connection = new  org.apache.axis.soap.SOAPConnectionImpl(); 	    
			java.net.URL urlConnection = new java.net.URL(urlForward);
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

	private void inoltroStrutturaEnvelopeAlterata(HttpServletRequest request,HttpServletResponse response, String msgS, String urlForward) throws Exception {

		StrutturaEnvelopeSoap11.Caso caso = StrutturaEnvelopeSoap11.getCasoDaMarcatore(msgS);
		if(caso==null) {
			throw new ServletException("Marcatore del caso di struttura dell'Envelope non presente nella richiesta");
		}
		this.log.info("Struttura Envelope (messaggio firmato): "+caso.name()+" ("+caso.getDescrizione()+")");
		byte[] alterato = caso.applica(msgS).getBytes();

		HttpURLConnection conn = (HttpURLConnection) new java.net.URI(urlForward).toURL().openConnection();
		conn.setRequestMethod("POST");
		conn.setDoInput(true);
		conn.setDoOutput(true);
		String contentType = request.getContentType();
		conn.setRequestProperty("Content-Type", contentType!=null ? contentType : "text/xml; charset=utf-8");
		String soapAction = request.getHeader("SOAPAction");
		if(soapAction!=null) {
			conn.setRequestProperty("SOAPAction", soapAction);
		}
		conn.setFixedLengthStreamingMode(alterato.length);
		try(OutputStream out = conn.getOutputStream()){
			out.write(alterato);
		}

		int status = conn.getResponseCode();
		InputStream is = status<300 ? conn.getInputStream() : conn.getErrorStream();
		byte[] risposta = null;
		if(is!=null) {
			try(InputStream isClose = is){
				risposta = isClose.readAllBytes();
			}
		}
		response.setStatus(status);
		if(risposta!=null && risposta.length>0) {
			if(conn.getContentType()!=null) {
				response.setContentType(conn.getContentType());
			}
			response.setContentLength(risposta.length);
			try(OutputStream os = response.getOutputStream()){
				os.write(risposta);
				os.flush();
			}
		}

		this.log.info("Inoltrato messaggio con struttura alterata (http "+status+"):\n"+new String(alterato));
	}

	@Override
	public void doPost(HttpServletRequest request,HttpServletResponse response) throws IOException, ServletException{
		doGet(request,response);
	}
}
