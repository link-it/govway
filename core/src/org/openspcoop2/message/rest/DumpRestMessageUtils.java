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

package org.openspcoop2.message.rest;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import jakarta.mail.BodyPart;

import org.openspcoop2.message.OpenSPCoop2RestMessage;
import org.openspcoop2.message.OpenSPCoop2RestMimeMultipartMessage;
import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.message.exception.MessageException;
import org.openspcoop2.message.utils.DumpAttachment;
import org.openspcoop2.message.utils.DumpMessaggio;
import org.openspcoop2.message.utils.DumpMessaggioConfig;
import org.openspcoop2.message.utils.DumpMessaggioMultipartInfo;
import org.openspcoop2.utils.dch.MailcapActivationReader;
import org.openspcoop2.utils.mime.MimeMultipart;
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
						
			Map<String, List<String>> pTrasporto = null;
			if(msg.getTransportRequestContext()!=null) {
				if(msg.getTransportRequestContext().getHeaders()!=null && 
						msg.getTransportRequestContext().getHeaders().size()>0){
					if(config.isDumpHeaders()) {
						pTrasporto = msg.getTransportRequestContext().getHeaders();
					}
				}
			}
			else if(msg.getTransportResponseContext()!=null) {
				if(msg.getTransportResponseContext().getHeaders()!=null && 
						msg.getTransportResponseContext().getHeaders().size()>0){
					if(config.isDumpHeaders()) {
						pTrasporto = msg.getTransportResponseContext().getHeaders();
					}
				}
			}
			if(config.isDumpHeaders() && pTrasporto!=null) {
				Iterator<String> keys = pTrasporto.keySet().iterator();
				while (keys.hasNext()) {
					String key = (String) keys.next();
					if(key!=null){
						List<String> values = pTrasporto.get(key);
						dumpMessaggio.getHeadersValues().put(key, values);
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
							bout.write(msg.castAsRestXml().getContentAsByteArray());
							break;
						case JSON:
							bout.write(msg.castAsRestJson().getContentAsByteArray());
							break;
						case BINARY:
							bout.write(msg.castAsRestBinary().getContent().getContent());
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
				MultipartContent mc = msgMime.getContent();
				MimeMultipart mimeMultipart = (mc!=null) ? mc.getMimeMultipart() : null;
				if(mimeMultipart!=null) {
					for (int i = 0; i < mimeMultipart.countBodyParts(); i++) {
						BodyPart bodyPart = mimeMultipart.getBodyPart(i);
						
						if(i>0) {
							if(!config.isDumpAttachments()) {
								break;
							}
						}
						
						DumpMessaggioMultipartInfo multipartInfoBody = null;	
						DumpAttachment dumpAttach = null;
						if(i==0 && config.isDumpBody()) {
							
							multipartInfoBody = new DumpMessaggioMultipartInfo();
							
							multipartInfoBody.setContentId(mimeMultipart.getContentID(bodyPart));
							multipartInfoBody.setContentLocation(mimeMultipart.getContentDisposition(bodyPart)); // Uso Disposition in REST, più opportuna
							multipartInfoBody.setContentType(bodyPart.getContentType());
							
						}
						else {
						
							dumpAttach = new DumpAttachment();
							
					    	dumpAttach.setContentId(mimeMultipart.getContentID(bodyPart));
					    	dumpAttach.setContentLocation(mimeMultipart.getContentDisposition(bodyPart)); // Uso Disposition in REST, più opportuna
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
						    			List<String> lValues = new ArrayList<>();
						    			if(values!=null && values.length>0) {
						    				for (int j = 0; j < values.length; j++) {
						    					lValues.add(values[j]);
											}
						    			}
						    			if(!lValues.isEmpty()) {
							    			if(multipartInfoBody!=null) {
							    				multipartInfoBody.getHeadersValues().put(key, lValues);
							    			}
							    			else {
							    				dumpAttach.getHeadersValues().put(key, lValues);
							    			}
						    			}
						    		}
						    		else if(keyO instanceof jakarta.mail.Header) {
						    			jakarta.mail.Header hdr = (jakarta.mail.Header) keyO;
						    			if(hdr!=null && hdr.getName()!=null) {
						    				if(multipartInfoBody!=null) {
						    					List<String> lValues = null;
						    					if(multipartInfoBody.getHeadersValues().containsKey(hdr.getName())) {
						    						lValues = multipartInfoBody.getHeadersValues().get(hdr.getName());
						    					}
						    					else {
						    						lValues = new ArrayList<>();
						    						multipartInfoBody.getHeadersValues().put(hdr.getName(), lValues);
						    					}
						    					lValues.add(hdr.getValue());
						    				}
							    			else {
							    				List<String> lValues = null;
						    					if(dumpAttach.getHeadersValues().containsKey(hdr.getName())) {
						    						lValues = dumpAttach.getHeadersValues().get(hdr.getName());
						    					}
						    					else {
						    						lValues = new ArrayList<>();
						    						dumpAttach.getHeadersValues().put(hdr.getName(), lValues);
						    					}
						    					lValues.add(hdr.getValue());
							    			}
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
				    		
				    		dumpMessaggio.setMultipartInfoBody(multipartInfoBody);
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
			StringBuilder out = new StringBuilder(msg.toString(config,dumpAllAttachments));
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
			StringBuilder out = new StringBuilder();
			
			Map<String,List<String>> pTrasporto = null;
			if(msg.getTransportRequestContext()!=null) {
				if(msg.getTransportRequestContext().getHeaders()!=null && 
						msg.getTransportRequestContext().getHeaders().size()>0){
					if(config.isDumpHeaders()) {
						pTrasporto = msg.getTransportRequestContext().getHeaders();
					}
				}
			}
			else if(msg.getTransportResponseContext()!=null) {
				if(msg.getTransportResponseContext().getHeaders()!=null && 
						msg.getTransportResponseContext().getHeaders().size()>0){
					if(config.isDumpHeaders()) {
						pTrasporto = msg.getTransportResponseContext().getHeaders();
					}
				}
			}
			if(config.isDumpHeaders()) {
				out.append("------ Header di trasporto ------\n");
				if(pTrasporto!=null && pTrasporto.size()>0) {
					Iterator<String> keys = pTrasporto.keySet().iterator();
					while (keys.hasNext()) {
						String key = (String) keys.next();
						if(key!=null){
							List<String> values = pTrasporto.get(key);
							if(values!=null && !values.isEmpty()) {
								for (String value : values) {
									out.append("- "+key+": "+value+"\n");
								}
							}
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
				MultipartContent mc = msgMime.getContent();
				MimeMultipart mimeMultipart = mc!=null ? mc.getMimeMultipart() : null;
				if(mimeMultipart!=null) {
					for (int i = 0; i < mimeMultipart.countBodyParts(); i++) {
						
						if(i>0) {
							if(!config.isDumpAttachments()) {
								break;
							}
						}
						
						BodyPart bodyPart = mimeMultipart.getBodyPart(i);
						
						if(i>0 || !config.isDumpBody()) {
							out.append("\n------ BodyPart-"+(i+1)+" ------\n");
						}
						
						out.append("\n*** MimePart Header ***\n");
						String contentIdBodyPart = mimeMultipart.getContentID(bodyPart);
				    	if(contentIdBodyPart!=null) {
							out.append("- "+HttpConstants.CONTENT_ID+": "+contentIdBodyPart+"\n");
						}
						String contentLocationBodyPart = mimeMultipart.getContentDisposition(bodyPart); // Uso Disposition in REST, più opportuna
						if(contentLocationBodyPart!=null) {
							out.append("- "+HttpConstants.CONTENT_DISPOSITION+": "+contentLocationBodyPart+"\n");
						}
						if(bodyPart.getContentType()!=null) {
							out.append("- "+HttpConstants.CONTENT_TYPE+": "+bodyPart.getContentType()+"\n");
						}
						if(config.isDumpMultipartHeaders()) {
							Enumeration<?> en = bodyPart.getAllHeaders();
							if(en!=null) {
						    	while(en.hasMoreElements()) {
						    		Object keyO = en.nextElement();
						    		if(keyO instanceof String) {
						    			String key = (String) keyO;
						    			if(HttpConstants.CONTENT_ID.equalsIgnoreCase(key) ||
						    					HttpConstants.CONTENT_DISPOSITION.equalsIgnoreCase(key) ||
						    					HttpConstants.CONTENT_TYPE.equalsIgnoreCase(key)) {
						    				continue;
						    			}
						    			String [] values = bodyPart.getHeader(key);
						    			if(values!=null && values.length>0) {
						    				for (int j = 0; j < values.length; j++) {
								    			out.append("- "+key+": "+values[j]+"\n");
											}
						    			}
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
			}
			
		    return out.toString();
		}catch(Exception e){
			throw new MessageException(e.getMessage(),e);
		}
	}
	
	public static String dumpBodyPart(OpenSPCoop2RestMessage<?> msg,BodyPart bodyPart) throws MessageException{
		Object o = _dumpBodyPart(msg, bodyPart, false);
		// Metodo sopra non torna mai null, segnalato da sonarqube
		/*if(o == null){
			throw new MessageException("Dump error (return null reference)");
		}*/
		if(o instanceof String){
			return (String) o;
		}
		else if(o instanceof java.io.ByteArrayOutputStream){
			String s = null;
			try (java.io.ByteArrayOutputStream bout = (java.io.ByteArrayOutputStream) o;){
				s = bout.toString();
			}catch(Exception eClose){
				// ignore exception close
			}
			return s;
		}
		else{
			throw new MessageException("Dump error (return type "+o.getClass().getName()+" unknown)");
		}
	}
	public static byte[] dumpBodyPartAsByteArray(OpenSPCoop2RestMessage<?> msg,BodyPart bodyPart) throws MessageException{
		Object o = _dumpBodyPart(msg, bodyPart, false);
		// Metodo sopra non torna mai null, segnalato da sonarqube
		/*if(o == null){
			throw new MessageException("Dump error (return null reference)");
		}*/
		if(o instanceof String){
			return ((String) o).getBytes();
		}
		else if(o instanceof java.io.ByteArrayOutputStream){
			byte[] b = null;
			try (java.io.ByteArrayOutputStream bout = (java.io.ByteArrayOutputStream) o;){
				b = bout.toByteArray();
			}catch(Exception eClose){
				// ignore exception close
			}
			return b;
		}
		else{
			throw new MessageException("Dump error (return type "+o.getClass().getName()+" unknown)");
		}
	}
	
	private static boolean convert = false;
	public static void setConvert(boolean convert) {
		DumpRestMessageUtils.convert = convert;
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
					jakarta.activation.DataHandler dh= bodyPart.getDataHandler();  
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
				jakarta.activation.DataHandler dh= bodyPart.getDataHandler();  
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
			// in rest con le api utilizzate non sembra necessario ricostruirlo come si fa in soap.
			// anche il caso del tunnel soap non esiste.
			if(convert && MailcapActivationReader.existsDataContentHandler(bodyPart.getContentType())){
				if(bodyPart.getContentType()!=null && bodyPart.getContentType().startsWith(HttpConstants.CONTENT_TYPE_PLAIN)){
					processContentTypeTextPlain(s, bodyPart, bout);
				}
				else{
					//bodyPart.setDataHandler(new jakarta.activation.DataHandler(bout.toByteArray(),bodyPart.getContentType()));
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

	private static void processContentTypeTextPlain(String s, BodyPart bodyPart, java.io.ByteArrayOutputStream bout) throws Exception {
		// Se siamo in text plain non devo fare nulla. Comunque non l'ho perso.
		// Se uso il codice sotto, poi si perde il content-type, non viene serializzato
		/*
		if(s!=null){
			bodyPart.setDataHandler(new jakarta.activation.DataHandler(s,bodyPart.getContentType()));
			//bodyPart.setContent(s,bodyPart.getContentType());
			//bodyPart.setText(s);
		}
		else{
			//bodyPart.setDataHandler(new jakarta.activation.DataHandler(bout.toByteArray(),bodyPart.getContentType()));
			// devo comunque impostare una stringa nel caso di text/plain, senno ottengo un errore:
			// "text/plain" DataContentHandler requires String object, was given object of type class [B
			bodyPart.setDataHandler(new jakarta.activation.DataHandler(bout.toString(),bodyPart.getContentType()));
			//bodyPart.setContent(bout.toString(),bodyPart.getContentType());
			//bodyPart.setText(bout.toString());
		}*/
	}
	
}
