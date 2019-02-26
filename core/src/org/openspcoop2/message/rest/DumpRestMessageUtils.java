/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it). 
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

package org.openspcoop2.message.rest;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Properties;

import javax.mail.BodyPart;

import org.openspcoop2.message.OpenSPCoop2RestMessage;
import org.openspcoop2.message.OpenSPCoop2RestMimeMultipartMessage;
import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.message.exception.MessageException;
import org.openspcoop2.message.utils.DumpAttachment;
import org.openspcoop2.message.utils.DumpMessaggio;
import org.openspcoop2.message.utils.DumpMessaggioConfig;
import org.openspcoop2.message.utils.DumpMessaggioMultipartInfo;
import org.openspcoop2.utils.dch.MailcapActivationReader;
import org.openspcoop2.utils.transport.http.HttpConstants;


/**
 * DumpRestMessageUtils
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DumpRestMessageUtils {

	public static DumpMessaggio dumpMessage(OpenSPCoop2RestMessage<?> msg, boolean dumpAllBodyParts) throws MessageException{
		return dumpMessage(msg, new DumpMessaggioConfig(), dumpAllBodyParts);
	}
	@SuppressWarnings("incomplete-switch")
	public static DumpMessaggio dumpMessage(OpenSPCoop2RestMessage<?> msg,
			DumpMessaggioConfig config,
			boolean dumpAllBodyParts) throws MessageException{
		try{
			DumpMessaggio dumpMessaggio = new DumpMessaggio();
			dumpMessaggio.setMessageType(msg.getMessageType());
						
			Properties pTrasporto = null;
			if(msg.getTransportRequestContext()!=null) {
				if(msg.getTransportRequestContext().getParametersTrasporto()!=null && 
						msg.getTransportRequestContext().getParametersTrasporto().size()>0){
					if(config.isDumpHeaders()) {
						pTrasporto = msg.getTransportRequestContext().getParametersTrasporto();
					}
				}
			}
			else if(msg.getTransportResponseContext()!=null) {
				if(msg.getTransportResponseContext().getParametersTrasporto()!=null && 
						msg.getTransportResponseContext().getParametersTrasporto().size()>0){
					if(config.isDumpHeaders()) {
						pTrasporto = msg.getTransportResponseContext().getParametersTrasporto();
					}
				}
			}
			if(config.isDumpHeaders() && pTrasporto!=null) {
				Enumeration<?> en = pTrasporto.keys();
				while (en.hasMoreElements()) {
					String key = (String) en.nextElement();
					if(key!=null){
						String value = pTrasporto.getProperty(key);
						dumpMessaggio.getHeaders().put(key, value);
					}
				}
			}
			
			boolean hasContent = msg.hasContent();
			if(hasContent){
				
				dumpMessaggio.setContentType(msg.getContentType());
																
				// La registrazione su log dello stream completo non e' una funzionalita' simmetrica rispetto al dump fatto su soap
				if(config.isDumpBody()) {
					if(!MessageType.MIME_MULTIPART.equals(msg.getMessageType())) {
						ByteArrayOutputStream bout = new ByteArrayOutputStream();
						switch (msg.getMessageType()) {
						case XML:
							bout.write(msg.castAsRestXml().getContentAsString().getBytes());
							break;
						case JSON:
							bout.write(msg.castAsRestJson().getContent().getBytes());
							break;
						case BINARY:
							bout.write(msg.castAsRestBinary().getContent());
							break;
						default:
							throw new MessageException("MessageType ["+msg.getMessageType()+"] unsupported");
						}
						bout.flush();
						bout.close();
						dumpMessaggio.setBody(bout);
					}
				}
			}
			
			if((config.isDumpBody() || config.isDumpAttachments()) && hasContent && 
					MessageType.MIME_MULTIPART.equals(msg.getMessageType())){
				OpenSPCoop2RestMimeMultipartMessage msgMime = msg.castAsRestMimeMultipart();
				for (int i = 0; i < msgMime.getContent().countBodyParts(); i++) {
					BodyPart bodyPart = msgMime.getContent().getBodyPart(i);
					
					if(i>0) {
						if(!config.isDumpAttachments()) {
							break;
						}
					}
					
					DumpMessaggioMultipartInfo multipartInfoBody = null;	
					DumpAttachment dumpAttach = null;
					if(i==0 && config.isDumpBody()) {
						
						multipartInfoBody = new DumpMessaggioMultipartInfo();
						
						multipartInfoBody.setContentId(msgMime.getContent().getContentID(bodyPart));
						multipartInfoBody.setContentLocation(msgMime.getContent().getContentLocation(bodyPart));
						multipartInfoBody.setContentType(bodyPart.getContentType());
						
					}
					else {
					
						dumpAttach = new DumpAttachment();
						
				    	dumpAttach.setContentId(msgMime.getContent().getContentID(bodyPart));
				    	dumpAttach.setContentLocation(msgMime.getContent().getContentLocation(bodyPart));
				    	dumpAttach.setContentType(bodyPart.getContentType());
					}
				    	
					if(config.isDumpMultipartHeaders()) {
				    	Enumeration<?> en = bodyPart.getAllHeaders();
				    	if(en!=null) {
					    	while(en.hasMoreElements()) {
					    		Object keyO = en.nextElement();
					    		if(keyO instanceof String) {
					    			String key = (String) keyO;
					    			String [] values = bodyPart.getHeader(key);
					    			String value = "";
					    			if(values!=null && values.length>0) {
					    				for (int j = 0; j < values.length; j++) {
											if(j>0) {
												value = value + ",";
											}
											value = value + values[j];
										}
					    			}
					    			if(multipartInfoBody!=null) {
					    				multipartInfoBody.getHeaders().put(key, value);
					    			}
					    			else {
					    				dumpAttach.getHeaders().put(key, value);
					    			}
					    		}
					    	}
				    	}
					}

			    	ByteArrayOutputStream boutAttach = null;
			    	if(dumpAllBodyParts){
			    		boutAttach = (ByteArrayOutputStream) DumpRestMessageUtils._dumpBodyPart(msg, bodyPart, true); 
			    	}else{
			    		Object o = _dumpBodyPart(msg, bodyPart, false);
			    		if(o == null){
			    			dumpAttach.setErrorContentNotSerializable("Contenuto attachment non recuperato??");
			    		}
			    		else if(o instanceof String){
			    			boutAttach = new ByteArrayOutputStream();
			    			boutAttach.write(((String)o).getBytes());
			    			boutAttach.flush();
			    			boutAttach.close();
			    		}
			    		else if(o instanceof java.io.ByteArrayOutputStream){
			    			boutAttach = (java.io.ByteArrayOutputStream) o;
			    		}
			    		else{
			    			dumpAttach.setErrorContentNotSerializable("Contenuto attachment non è visualizzabile, tipo: "+o.getClass().getName());
			    		}
			    	}
			    	if(multipartInfoBody!=null) {
			    		dumpMessaggio.setBody(boutAttach);
			    	}
			    	else {
			    		dumpAttach.setContent(boutAttach);
			    		
				    	if(dumpMessaggio.getAttachments()==null) {
				    		dumpMessaggio.setAttachments(new ArrayList<>());
				    	}
			    		dumpMessaggio.getAttachments().add(dumpAttach);
			    	}
				}
			}
			
		    return dumpMessaggio;
		}catch(Exception e){
			throw new MessageException(e.getMessage(),e);
		}
	}
	
	public static String dumpMessageAsString(DumpMessaggio msg, boolean dumpAllAttachments) throws MessageException{
		return dumpMessageAsString(msg,  new DumpMessaggioConfig(), dumpAllAttachments);
	}
	public static String dumpMessageAsString(DumpMessaggio msg,
			DumpMessaggioConfig config, boolean dumpAllAttachments) throws MessageException{
		try{
			StringBuffer out = new StringBuffer(msg.toString(config,dumpAllAttachments));
			return out.toString();
		}catch(Exception e){
			throw new MessageException(e.getMessage(),e);
		}
	}
	
	public static String dumpMessageAsString(OpenSPCoop2RestMessage<?> msg,
			boolean dumpAllBodyParts) throws MessageException{
		return dumpMessageAsString(msg,  new DumpMessaggioConfig(), dumpAllBodyParts);
	}
	public static String dumpMessageAsString(OpenSPCoop2RestMessage<?> msg,
			DumpMessaggioConfig config,
			boolean dumpAllBodyParts) throws MessageException{
		try{
			StringBuffer out = new StringBuffer();
			
			Properties pTrasporto = null;
			if(msg.getTransportRequestContext()!=null) {
				if(msg.getTransportRequestContext().getParametersTrasporto()!=null && 
						msg.getTransportRequestContext().getParametersTrasporto().size()>0){
					if(config.isDumpHeaders()) {
						pTrasporto = msg.getTransportRequestContext().getParametersTrasporto();
					}
				}
			}
			else if(msg.getTransportResponseContext()!=null) {
				if(msg.getTransportResponseContext().getParametersTrasporto()!=null && 
						msg.getTransportResponseContext().getParametersTrasporto().size()>0){
					if(config.isDumpHeaders()) {
						pTrasporto = msg.getTransportResponseContext().getParametersTrasporto();
					}
				}
			}
			if(config.isDumpHeaders()) {
				out.append("------ Header di trasporto ------\n");
				if(pTrasporto!=null && pTrasporto.size()>0) {
					Enumeration<?> en = pTrasporto.keys();
					while (en.hasMoreElements()) {
						String key = (String) en.nextElement();
						if(key!=null){
							String value = pTrasporto.getProperty(key);
							out.append("- "+key+": "+value+"\n");
						}
					}
				}
				else {
					out.append("Non presenti\n");
				}
			}
			
			boolean hasContent = msg.hasContent();
			String contentString = "Body";
			String contentType = "";
			if(!hasContent){
				contentString = "Empty Body";
			}
			if(hasContent) {
				if(msg.getContentType()!=null) {
					contentType = " (ContentType: "+msg.getContentType()+")";
				}
			}
			if(config.isDumpBody()) {
				out.append("------ "+contentString+contentType+" ("+msg.getMessageType()+") ------\n");

				// Nel caso di multipart non il body e gli attachments devono essere gestiti nell'iterazione successiva
				// Se si usa il metodo 'msg.getContentAsString()' si ottiene lo stream completo
				if(!MessageType.MIME_MULTIPART.equals(msg.getMessageType())) {
					
					out.append("\n");
					
					if(hasContent) {
						out.append(msg.getContentAsString());
					}
				}
			}
			
			if((config.isDumpBody() || config.isDumpAttachments()) && hasContent && MessageType.MIME_MULTIPART.equals(msg.getMessageType())){
				OpenSPCoop2RestMimeMultipartMessage msgMime = msg.castAsRestMimeMultipart();
				for (int i = 0; i < msgMime.getContent().countBodyParts(); i++) {
					
					if(i>0) {
						if(!config.isDumpAttachments()) {
							break;
						}
					}
					
					BodyPart bodyPart = msgMime.getContent().getBodyPart(i);
					
					if(i>0 || !config.isDumpBody()) {
						out.append("\n------ BodyPart-"+(i+1)+" ------\n");
					}
					
					out.append("\n*** MimePart Header ***\n");
					String contentIdBodyPart = msgMime.getContent().getContentID(bodyPart);
			    	if(contentIdBodyPart!=null) {
						out.append("- "+HttpConstants.CONTENT_ID+": "+contentIdBodyPart+"\n");
					}
					String contentLocationBodyPart = msgMime.getContent().getContentLocation(bodyPart);
					if(contentLocationBodyPart!=null) {
						out.append("- "+HttpConstants.CONTENT_LOCATION+": "+contentLocationBodyPart+"\n");
					}
					if(bodyPart.getContentType()!=null) {
						out.append("- "+HttpConstants.CONTENT_LOCATION+": "+bodyPart.getContentType()+"\n");
					}
					if(config.isDumpMultipartHeaders()) {
						Enumeration<?> en = bodyPart.getAllHeaders();
						if(en!=null) {
					    	while(en.hasMoreElements()) {
					    		Object keyO = en.nextElement();
					    		if(keyO instanceof String) {
					    			String key = (String) keyO;
					    			if(HttpConstants.CONTENT_ID.equalsIgnoreCase(key) ||
					    					HttpConstants.CONTENT_LOCATION.equalsIgnoreCase(key) ||
					    					HttpConstants.CONTENT_TYPE.equalsIgnoreCase(key)) {
					    				continue;
					    			}
					    			String [] values = bodyPart.getHeader(key);
					    			String value = "";
					    			if(values!=null && values.length>0) {
					    				for (int j = 0; j < values.length; j++) {
											if(j>0) {
												value = value + ",";
											}
											value = value + values[j];
										}
					    			}
					    			out.append("- "+key+": "+value+"\n");
					    		}
					    	}
						}
					}
					out.append("\n");

			    	if(dumpAllBodyParts){
			    		out.append(DumpRestMessageUtils.dumpBodyPart(msg, bodyPart));
			    	}else{
			    		//Object o = ap.getContent(); NON FUNZIONA CON TOMCAT
			    		Object o = bodyPart.getDataHandler().getContent();
			    		//System.out.println("["+o.getClass().getName()+"])"+ap.getContentType()+"(");			    		
			    		if(o instanceof String){
			    			out.append((String)o);
			    		}else{
			    			 out.append("Contenuto attachments non è visualizzabile, tipo: "+o.getClass().getName());
			    		}
			    	}
				}
			}
			
		    return out.toString();
		}catch(Exception e){
			throw new MessageException(e.getMessage(),e);
		}
	}
	
	public static String dumpBodyPart(OpenSPCoop2RestMessage<?> msg,BodyPart bodyPart) throws MessageException{
		Object o = _dumpBodyPart(msg, bodyPart, false);
		if(o == null){
			throw new MessageException("Dump error (return null reference)");
		}
		if(o instanceof String){
			return (String) o;
		}
		else if(o instanceof java.io.ByteArrayOutputStream){
			java.io.ByteArrayOutputStream bout = null;
			try{
				bout = (java.io.ByteArrayOutputStream) o;
				return bout.toString();
			}finally{
				try{
					if(bout!=null){
						bout.close();
					}
				}catch(Exception eClose){}
			}
		}
		else{
			throw new MessageException("Dump error (return type "+o.getClass().getName()+" unknown)");
		}
	}
	public static byte[] dumpBodyPartAsByteArray(OpenSPCoop2RestMessage<?> msg,BodyPart bodyPart) throws MessageException{
		Object o = _dumpBodyPart(msg, bodyPart, false);
		if(o == null){
			throw new MessageException("Dump error (return null reference)");
		}
		if(o instanceof String){
			return ((String) o).getBytes();
		}
		else if(o instanceof java.io.ByteArrayOutputStream){
			java.io.ByteArrayOutputStream bout = null;
			try{
				bout = (java.io.ByteArrayOutputStream) o;
				return bout.toByteArray();
			}finally{
				try{
					if(bout!=null){
						bout.close();
					}
				}catch(Exception eClose){}
			}
		}
		else{
			throw new MessageException("Dump error (return type "+o.getClass().getName()+" unknown)");
		}
	}
	
	private static Object _dumpBodyPart(OpenSPCoop2RestMessage<?> msg,BodyPart bodyPart, boolean forceReturnAsByteArrayOutputStream) throws MessageException{
		try{		
			java.io.ByteArrayOutputStream bout = null;
			//Object o = ap.getContent(); NON FUNZIONA CON TOMCAT
			//java.io.InputStream inputDH = dh.getInputStream(); NON FUNZIONA CON JBOSS7 e JAVA7 e imbustamentoSOAP con GestioneManifest e rootElementMaggioreUno (tipo: application/octet-stream)
			Object o = bodyPart.getDataHandler().getContent();
			String s = null;
			if(o!=null){
				if(o instanceof byte[]){
					byte[] b = (byte[]) o;
					bout = new ByteArrayOutputStream();
					bout.write(b);
					bout.flush();
					bout.close();
				}
				else if(o instanceof InputStream){
					InputStream is = (InputStream) o;
					bout = new java.io.ByteArrayOutputStream();
			    	byte [] readB = new byte[8192];
					int readByte = 0;
					while((readByte = is.read(readB))!= -1)
						bout.write(readB,0,readByte);
					is.close();
					bout.flush();
					bout.close();
				}
				else if(o instanceof String){
					s = (String) o;
					bout = new ByteArrayOutputStream();
					bout.write(s.getBytes());
					bout.flush();
					bout.close();
				}
				else{
					// Provo con codiceOriginale ma in jboss non funziona sempre
					javax.activation.DataHandler dh= bodyPart.getDataHandler();  
			    	java.io.InputStream inputDH = dh.getInputStream();
					bout = new java.io.ByteArrayOutputStream();
			    	byte [] readB = new byte[8192];
					int readByte = 0;
					while((readByte = inputDH.read(readB))!= -1)
						bout.write(readB,0,readByte);
					inputDH.close();
					bout.flush();
					bout.close();		
				}
			}
			else{
				// Provo con codiceOriginale ma in jboss non funziona sempre
				javax.activation.DataHandler dh= bodyPart.getDataHandler();  
		    	java.io.InputStream inputDH = dh.getInputStream();
				bout = new java.io.ByteArrayOutputStream();
		    	byte [] readB = new byte[8192];
				int readByte = 0;
				while((readByte = inputDH.read(readB))!= -1)
					bout.write(readB,0,readByte);
				inputDH.close();
				bout.flush();
				bout.close();		
			}
			// Per non perdere quanto letto
			boolean convert = false;
			// in rest con le api utilizzate non sembra necessario ricostruirlo come si fa in soap.
			// anche il caso del tunnel soap non esiste.
			if(convert && MailcapActivationReader.existsDataContentHandler(bodyPart.getContentType())){
				if(bodyPart.getContentType()!=null && bodyPart.getContentType().startsWith(HttpConstants.CONTENT_TYPE_PLAIN)){
					// Se siamo in text plain non devo fare nulla. Comunque non l'ho perso.
					// Se uso il codice sotto, poi si perde il content-type, non viene serializzato
					/*
					if(s!=null){
						bodyPart.setDataHandler(new javax.activation.DataHandler(s,bodyPart.getContentType()));
						//bodyPart.setContent(s,bodyPart.getContentType());
						//bodyPart.setText(s);
					}
					else{
						//bodyPart.setDataHandler(new javax.activation.DataHandler(bout.toByteArray(),bodyPart.getContentType()));
						// devo comunque impostare una stringa nel caso di text/plain, senno ottengo un errore:
						// "text/plain" DataContentHandler requires String object, was given object of type class [B
						bodyPart.setDataHandler(new javax.activation.DataHandler(bout.toString(),bodyPart.getContentType()));
						//bodyPart.setContent(bout.toString(),bodyPart.getContentType());
						//bodyPart.setText(bout.toString());
					}*/
				}
				else{
					//bodyPart.setDataHandler(new javax.activation.DataHandler(bout.toByteArray(),bodyPart.getContentType()));
					// Nel caso sia xml si ottiene il seguente errore:
					//Invalid Object type = class [B. XmlDCH can only convert DataSource or Source to XML.
					// Si potrebbe vederlo di gestire come e' stato fatto per il rebuild dell'attachment su SOAP.
				}
			}
			if(s!=null){
				if(forceReturnAsByteArrayOutputStream) {
					return bout;
				}
				else {
					return s;
				}
			}else{
				return bout;
			}
		}catch(Exception e){
			throw new MessageException(e.getMessage(),e);
		}
	}


}
