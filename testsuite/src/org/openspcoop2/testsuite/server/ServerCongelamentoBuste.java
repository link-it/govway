/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2017 Link.it srl (http://link.it). 
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


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPMessage;

import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.OpenSPCoop2MessageFactory;
import org.openspcoop2.message.OpenSPCoop2MessageParseResult;
import org.openspcoop2.message.constants.MessageRole;
import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.engine.validator.ValidazioneSintattica;
import org.openspcoop2.protocol.sdk.IProtocolFactory;



/**
 * Server Generale utilizzato per profili di collaborazione OneWay, Sincrono e AsincronoAsimmetrico
 * 
 * @author Andi Rexha (rexha@openspcoop.org)
 * @author $Author$
 * @version $Rev$, $Date$
 */

@SuppressWarnings("serial")
public class ServerCongelamentoBuste extends ServerCore{

	/**
	 * 
	 */
	public ServerCongelamentoBuste() {
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
			
			
			String tempoCongelamento = request.getParameter("secondiCongelamento");
			int  secondiSleep = 0;
			if(tempoCongelamento==null){
				throw new Exception("Proprieta 'secondiCongelamento' non fornita");
			}else{
				tempoCongelamento = tempoCongelamento.trim();
				Integer intSecondi = Integer.parseInt(tempoCongelamento);
				secondiSleep = intSecondi.intValue();
			}
			
			
			OpenSPCoop2MessageParseResult pr = OpenSPCoop2MessageFactory.getMessageFactory().createMessage(MessageType.SOAP_11, MessageRole.REQUEST, 
					request.getContentType(), bout.toByteArray());
			OpenSPCoop2Message msg = pr.getMessage_throwParseException();
			if(msg.castAsSoap().getSoapAction()==null) {
				msg.castAsSoap().setSoapAction("\"TestSuite\"");
			}
			// ??? Serve ancora ??? Configurazione.init(checkInterval, gestoreRepositoryBuste, sqlQueryObject, this.log);
			IProtocolFactory<?> pf = ProtocolFactoryManager.getInstance().getDefaultProtocolFactory();
			ValidazioneSintattica validator = new ValidazioneSintattica(null,msg,pf);
			boolean bustaCorretta = validator.valida();
			if(!bustaCorretta)
				throw new Exception ("Busta non corretta?");
			
			long sequenza = validator.getBusta().getSequenza();
			if( sequenza == 3){
				this.log.info("Effettuo congelamento della busta con ID ["+validator.getBusta().getID()+"] che possiede la sequenza: "+
					sequenza);
				try{
					Thread.sleep(secondiSleep*1000);
				}catch(Exception e){}
				this.log.info("Congelamento della busta con ID ["+validator.getBusta().getID()+"] che possiede la sequenza: "+
						sequenza+" terminato.");
			}else{
				this.log.info("non effettuo congelamento della busta con ID ["+validator.getBusta().getID()+"] che possiede la sequenza: "+
						sequenza);
			}
			
			SOAPConnection connection = OpenSPCoop2MessageFactory.getMessageFactory().getSOAPConnectionFactory().createConnection();
			javax.xml.messaging.URLEndpoint urlConnection = new  javax.xml.messaging.URLEndpoint(urlForward);
			Object responseMsgSoapAsObject = connection.call(msg.castAsSoap().getSOAPMessage(),urlConnection);
			//System.out.println("RESPONSE ["+responseMsgSoapAsObject.getClass().getName()+"] CONNECTION ["+connection.getClass().getName()+"]");
			SOAPMessage responseMsgSoap = (SOAPMessage) responseMsgSoapAsObject;
			OpenSPCoop2Message responseMsg = OpenSPCoop2MessageFactory.getMessageFactory().createMessage(msg.getMessageType(),MessageRole.RESPONSE,responseMsgSoap);

			if(responseMsg!=null){
				ByteArrayOutputStream boutResponse=new ByteArrayOutputStream();
				responseMsg.writeTo(boutResponse, true);
				boutResponse.flush();
				boutResponse.close();
				
				//System.out.println("RICEVUTO ["+boutResponse.toString()+"]");
				
				if(boutResponse.size()>0){
					response.setContentLength(boutResponse.size());
					response.setContentType(responseMsg.getContentType());
					response.getOutputStream().write(boutResponse.toByteArray());
					response.getOutputStream().flush();
					response.getOutputStream().close();
				}
			}
			
			this.log.info("Gestione busta con id="+validator.getBusta().getID()+" effettata, messaggio gestito:\n"+bout.toString());

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
