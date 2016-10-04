/*
 * OpenSPCoop - Customizable API Gateway 
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
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.testsuite.axis14.Axis14SoapUtils;
import org.openspcoop2.testsuite.axis14.Axis14WSSBaseUtils;
import org.openspcoop2.testsuite.axis14.Axis14WSSReceiver;
import org.openspcoop2.testsuite.axis14.Axis14WSSSender;



/**
 * Server Generale utilizzato per profili di collaborazione OneWay, Sincrono e AsincronoAsimmetrico
 * 
 * @author Andi Rexha (rexha@openspcoop.org)
 * @author $Author$
 * @version $Rev$, $Date$
 */

@SuppressWarnings("serial")
public class ServerWSSecurity extends ServerCore{

	/**
	 * 
	 */
	public ServerWSSecurity() {
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
			
			String id=request.getHeader(this.testsuiteProperties.getIdMessaggioTrasporto());
			if(id==null){
				this.log.error("Id della richiesta non presente");
			} 
			String destinatario = request.getHeader(this.testsuiteProperties.getDestinatarioTrasporto());
			if(destinatario==null){
				this.log.error("Destinatario della richiesta non presente");
			} 
			
			String mustUnderstand = request.getParameter("mustUnderstand");
			if(mustUnderstand==null)
				mustUnderstand = "false";
			if(mustUnderstand.equals("true")==false)
				mustUnderstand = "false";
			String actor = request.getParameter("actor");
			
			String protocollo = request.getParameter("protocol");
			if(protocollo==null){
				protocollo = ProtocolFactoryManager.getInstance().getDefaultProtocolFactory().getProtocol();
			}
			
			
			String wssFunction = request.getParameter("wssecurityFunction");
			StringBuffer functionsRichieste = new StringBuffer(); 
			boolean signature = false;
			boolean encrypt = false;
			if(wssFunction==null){
				throw new Exception("Parametro 'wssecurityFunction' non fornito");
			}
			wssFunction = wssFunction.trim();
			
			String [] functions = null;
			if(wssFunction.contains(",")){
				functions = wssFunction.split(",");
				for (int i = 0; i < functions.length; i++) {
					functions[i] = functions[i].trim();
				}
			}
			else{
				functions = new String[] {wssFunction};
			}
			
			for (int i = 0; i < functions.length; i++) {
				functionsRichieste.append(" ");
				if("signature".equalsIgnoreCase(functions[i])){
					signature = true;
					functionsRichieste.append("Signature");
				}
				else if("encrypt".equalsIgnoreCase(functions[i])){
					encrypt = true;
					functionsRichieste.append("Encrypt");
				}
				else{
					throw new Exception("Parametro 'wssecurityFunction' possiede un valore non valido (valori validi: signature,encrypt)");
				}
			}
			

			// Costruzione messaggio richiesta
			this.log.info("Analisi messaggio di richiesta (Funzionalita' WSS richieste: "+functionsRichieste.toString()+" )...");
			Message msgRequest = Axis14SoapUtils.build(bout.toByteArray(), false);
			java.util.Hashtable<String,String> wssPropertiesRequest= new java.util.Hashtable<String,String>();
			wssPropertiesRequest.put("action", "Timestamp"+functionsRichieste.toString());
			wssPropertiesRequest.put("passwordCallbackClass", "org.openspcoop2.testsuite.wssecurity.PWCallbackSend");
			if(signature){
				this.log.info("Applico signature.");
				wssPropertiesRequest.put("signatureKeyIdentifier", "DirectReference");
				wssPropertiesRequest.put("signaturePropFile", "testsuiteServer-crypto.properties");
			}
			if(encrypt){
				this.log.info("Applico encrypt.");
				wssPropertiesRequest.put("decryptionPropFile", "testsuiteServer-crypto.properties");
			}
			wssPropertiesRequest.put("mustUnderstand", mustUnderstand);
			if(actor!=null)
				wssPropertiesRequest.put("actor", actor);
			Axis14WSSBaseUtils baseWSS = new Axis14WSSBaseUtils(Axis14SoapUtils.getAxisClient(),false,"TESTSUITE",this.log,"ServerTest-");
			baseWSS.setUseActorDefaultIfNotDefined(false);
			Axis14WSSReceiver wssReceiver = new Axis14WSSReceiver(wssPropertiesRequest,baseWSS);
			if(wssReceiver.process(msgRequest,null) == false){   
				throw new Exception("Validazione WSS in ricezione non riuscita ["+wssReceiver.getCodiceErrore()+"]: "+wssReceiver.getMsgErrore());
			}
			this.log.info("Analisi messaggio di richiesta terminata");
			ByteArrayOutputStream bosRequest=new ByteArrayOutputStream();
			msgRequest.writeTo(bosRequest);
			bosRequest.flush();
			bosRequest.close();
			this.log.info("Analisi messaggio dopo gestione WSS della richiesta: "+bosRequest.toString());
			
			
			// Costruzione messaggio risposta
			this.log.info("Costruzione messaggio di risposta...");
			Message msgResponse = Axis14SoapUtils.build(bosRequest.toByteArray(), false);
			java.util.Hashtable<String,String> wssPropertiesResponse= new java.util.Hashtable<String,String>();
			wssPropertiesResponse.put("action", "Timestamp"+functionsRichieste.toString());
			wssPropertiesResponse.put("timeToLive", "600");
			wssPropertiesResponse.put("passwordCallbackClass", "org.openspcoop2.testsuite.wssecurity.PWCallbackSend");
			if(signature){
				this.log.info("Applico signature.");
				wssPropertiesResponse.put("user", "testsuiteserver");
				wssPropertiesResponse.put("signatureKeyIdentifier", "DirectReference");
				wssPropertiesResponse.put("signaturePropFile", "testsuiteServer-crypto.properties");
				wssPropertiesResponse.put("signatureParts", "{Content}{http://schemas.xmlsoap.org/soap/envelope/}Body;{Content}{http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd}Timestamp;");
			}
			if(encrypt){
				this.log.info("Applico encrypt.");
				wssPropertiesResponse.put("encryptionUser", "testsuiteclient");
				wssPropertiesResponse.put("encryptionPropFile", "testsuiteServer-crypto.properties");
				wssPropertiesResponse.put("encryptionParts", "{Content}{http://schemas.xmlsoap.org/soap/envelope/}Body;{Content}{http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd}Timestamp;");
			}
			wssPropertiesResponse.put("mustUnderstand", mustUnderstand);
			if(actor!=null)
				wssPropertiesResponse.put("actor", actor);
			Axis14WSSSender wssSender = new Axis14WSSSender(wssPropertiesResponse,baseWSS);
			if(wssSender.process(msgResponse)==false) {
				throw new Exception("Costruzione WSS in spedizione non riuscita ["+wssSender.getCodiceErrore()+"]: "+wssSender.getMsgErrore());
			}
			this.log.info("Costruzione messaggio di risposta terminata");
			
			
			response.setContentLength((int) msgResponse.getContentLength());
			response.setContentType(msgResponse.getContentType(new org.apache.axis.soap.SOAP11Constants()));
			BufferedOutputStream bos=new BufferedOutputStream(response.getOutputStream());
			msgResponse.writeTo(bos);
			bos.flush();
			bos.close();

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

		}catch(Throwable e){
			this.log.error("Errore durante la gestione di una richiesta: "+e.getMessage(),e);
			throw new ServletException("Errore durante la gestione di una richiesta: "+e.getMessage(),e);
		}
	}

	@Override
	public void doPost(HttpServletRequest request,HttpServletResponse response) throws IOException, ServletException{
		doGet(request,response);
	}
}
