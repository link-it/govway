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



/**
 * Server 
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

@SuppressWarnings("serial")
public class ServerMalformazioneXML extends ServerCore{

	/**
	 * 
	 */
	public ServerMalformazioneXML() {
		super();
		// TODO Auto-generated constructor stub
	}


	@Override
	public void init() {}


	@Override
	public void doGet(HttpServletRequest request,HttpServletResponse response) throws IOException, ServletException{

		try{

			boolean malformazioneHeader = false;
			String prop = request.getParameter("headerEGov"); // Lasciare eGov, questa servlet e' specifica per il protocollo SPC
			if(prop!=null)
				malformazioneHeader = Boolean.parseBoolean(prop.trim());
			
			boolean malformazioneContentTypeRisposta = false;
			String propMalformazioneContentTypeRisposta = request.getParameter("contentTypeRisposta"); // Lasciare eGov, questa servlet e' specifica per il protocollo SPC
			if(propMalformazioneContentTypeRisposta!=null)
				malformazioneContentTypeRisposta = Boolean.parseBoolean(propMalformazioneContentTypeRisposta.trim());
			
			String malformazioneBody = "";
			malformazioneBody = request.getParameter("body");
			
			
			// Lettura Richiesta
			InputStream in=request.getInputStream();
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			int read=0;
			while((read=in.read()) != -1){
				bout.write(read);
			}
			in.close();
			bout.flush();
			bout.close();
			String msgS = bout.toString();
			
			if(msgS.contains("<eGov_IT:Intestazione")){
				msgS = msgS.replaceFirst("MinisteroErogatore", "MinisteroFruitore");
				msgS = msgS.replaceFirst("MinisteroFruitore", "MinisteroErogatore");					 
				int initID = msgS.indexOf("<eGov_IT:Identificatore>");
				int andID = msgS.indexOf("</eGov_IT:Identificatore>");
				String id = msgS.substring(initID + 24, andID);
				msgS = msgS.replace(id, id.replaceAll("MinisteroFruitore", "MinisteroErogatore"));
				msgS = msgS.replace("</eGov_IT:Messaggio>", "<eGov_IT:RiferimentoMessaggio>"+id+"</eGov_IT:RiferimentoMessaggio></eGov_IT:Messaggio>");
			}
			
			if(malformazioneHeader){
				this.log.info("Malformazione header messaggio");
				msgS = msgS.replace("Mittente>", "Mittente");
			}
			if("body".equals(malformazioneBody)){
				this.log.info("Malformazione body applicativo");
				msgS = msgS.replace("</soapenv:Body>", "<soapenv:Body>");
			}
			if("firstchild".equals(malformazioneBody)){
				this.log.info("Malformazione body applicativo");
				msgS = msgS.replace("</helloworld>", "</helloworldXX>");
			}
			if("insidebody".equals(malformazioneBody)){
				this.log.info("Malformazione body applicativo");
				msgS = msgS.replace("</b>", "</bX>");
			}
			
			
			byte [] bytes = msgS.getBytes();
			
			response.setContentLength(bytes.length);
			if(malformazioneContentTypeRisposta){
				response.setContentType("text/ERRATO_CT");
			}else{
				response.setContentType("text/xml");
			}
			response.getOutputStream().write(bytes);
			response.getOutputStream().flush();
			response.getOutputStream().close();
			
			this.log.info("Gestione malformazione xml effettata, messaggio gestito:\n"+msgS);

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
