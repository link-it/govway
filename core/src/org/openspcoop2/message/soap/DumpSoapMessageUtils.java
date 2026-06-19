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

package org.openspcoop2.message.soap;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import jakarta.activation.DataHandler;
import jakarta.mail.util.ByteArrayDataSource;
import jakarta.xml.soap.AttachmentPart;
import jakarta.xml.soap.SOAPPart;

import org.openspcoop2.message.OpenSPCoop2SoapMessage;
import org.openspcoop2.message.exception.MessageException;
import org.openspcoop2.message.utils.DumpAttachment;
import org.openspcoop2.message.utils.DumpMessaggio;
import org.openspcoop2.message.utils.DumpMessaggioConfig;
import org.openspcoop2.message.utils.DumpMessaggioMultipartInfo;
import org.openspcoop2.message.xml.MessageXMLUtils;
import org.openspcoop2.utils.UtilsMultiException;
import org.openspcoop2.utils.dch.MailcapActivationReader;
import org.openspcoop2.utils.transport.http.ContentTypeUtilities;
import org.openspcoop2.utils.transport.http.HttpConstants;


/**
 * DumpSoapMessageUtils
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DumpSoapMessageUtils {
	
	private DumpSoapMessageUtils() {}

	public static DumpMessaggio dumpMessage(OpenSPCoop2SoapMessage msg,boolean dumpAllAttachments) throws MessageException{
		return dumpMessage(msg, new DumpMessaggioConfig(), dumpAllAttachments);
	}
	public static DumpMessaggio dumpMessage(OpenSPCoop2SoapMessage msg,
			DumpMessaggioConfig config,
			boolean dumpAllAttachments) throws MessageException{
		try{
			DumpMessaggio dumpMessaggio = new DumpMessaggio();
			dumpMessaggio.setMessageType(msg.getMessageType());
			dumpMessaggio.setContentType(msg.getContentType());
						
			Map<String, List<String>> pTrasporto = null;
			if(msg.getTransportRequestContext()!=null) {
				if(msg.getTransportRequestContext().getHeaders()!=null && 
						msg.getTransportRequestContext().getHeaders().size()>0 &&
						config.isDumpHeaders()) {
					pTrasporto = msg.getTransportRequestContext().getHeaders();
				}
			}
			else if(msg.getTransportResponseContext()!=null) {
				if(msg.getTransportResponseContext().getHeaders()!=null && 
						msg.getTransportResponseContext().getHeaders().size()>0 &&
					config.isDumpHeaders()) {
					pTrasporto = msg.getTransportResponseContext().getHeaders();
				}
			}
			if(config.isDumpHeaders() && pTrasporto!=null) {
				Iterator<String> keys = pTrasporto.keySet().iterator();
				while (keys.hasNext()) {
					String key = keys.next();
					if(key!=null){
						List<String> values = pTrasporto.get(key);
						dumpMessaggio.getHeadersValues().put(key, values);
					}
				}
			}
			
			boolean msgWithAttachments = msg.countAttachments()>0;
			
			if(config.isDumpBody()) {
				
				SOAPPart soapPart = msg.getSOAPPart();
				
				DumpMessaggioMultipartInfo multipartInfoBody = null;
				if(msgWithAttachments) {
					
					Iterator<?> itM = soapPart.getAllMimeHeaders();
			    	if(itM!=null) {
				    	while(itM.hasNext()) {
				    		Object keyO = itM.next();
				    		if(keyO instanceof jakarta.xml.soap.MimeHeader mh) {
				    			String key = mh.getName();
				    			
				    			if(!HttpConstants.CONTENT_ID.equalsIgnoreCase(key) &&
				    					!HttpConstants.CONTENT_LOCATION.equalsIgnoreCase(key) &&
				    					!HttpConstants.CONTENT_TYPE.equalsIgnoreCase(key) &&
				    					!config.isDumpMultipartHeaders()) {
				    				continue;
				    			}
				    			
				    			List<String> l = new ArrayList<>();
				    			String [] values = soapPart.getMimeHeader(key);
				    			if(values!=null && values.length>0) {
				    				l.addAll(Arrays.asList(values));
				    			}
				    			else {
				    				l.add(mh.getValue());
				    			}
				    			
				    			if(!l.isEmpty()) {
					    			if(multipartInfoBody==null) {
					    				multipartInfoBody = new DumpMessaggioMultipartInfo();
					    			}
					    			
					    			if(HttpConstants.CONTENT_ID.equalsIgnoreCase(key)) {
					    				multipartInfoBody.setContentId(l.get(0));
					    			}
					    			else if(HttpConstants.CONTENT_LOCATION.equalsIgnoreCase(key)) {
					    				multipartInfoBody.setContentLocation(l.get(0));
					    			}
					    			else if(HttpConstants.CONTENT_TYPE.equalsIgnoreCase(key)) {
					    				multipartInfoBody.setContentType(l.get(0));
					    			}
					    			
					    			if(config.isDumpMultipartHeaders()) {
					    				multipartInfoBody.getHeadersValues().put(key, l);
					    			}
				    			}
				    			
				    		}
				    	}
			    	}
					
				}
				if(multipartInfoBody!=null) {
					dumpMessaggio.setMultipartInfoBody(multipartInfoBody);
				}
				
				ByteArrayOutputStream bout = new ByteArrayOutputStream();
				bout.write(msg.getAsByte(soapPart.getEnvelope(),false));
				bout.flush();
				bout.close();
				dumpMessaggio.setBody(bout);
			}
			
			if(config.isDumpAttachments()) {
				
				java.util.Iterator<?> it = msg.getAttachments();
			    while(it.hasNext()){
			    	AttachmentPart ap = 
			    		(AttachmentPart) it.next();
			    	DumpAttachment dumpAttach = new DumpAttachment();
			    	
			    	dumpAttach.setContentId(ap.getContentId());
			    	dumpAttach.setContentLocation(ap.getContentLocation());
			    	dumpAttach.setContentType(ap.getContentType());
			    	
			    	if(config.isDumpMultipartHeaders()) {
				    	Iterator<?> itM = ap.getAllMimeHeaders();
				    	if(itM!=null) {
					    	while(itM.hasNext()) {
					    		Object keyO = itM.next();
					    		if(keyO instanceof jakarta.xml.soap.MimeHeader mh) {
					    			String key = mh.getName();
					    			
					    			List<String> l = new ArrayList<>();
					    			String [] values = ap.getMimeHeader(key);
					    			if(values!=null && values.length>0) {
					    				l.addAll(Arrays.asList(values));
					    			}
					    			else {
					    				l.add(mh.getValue());
					    			}
					    			
					    			if(!l.isEmpty()) {
					    				dumpAttach.getHeadersValues().put(key, l);
					    			}
					    		}
					    	}
				    	}
			    	}
			    	
			    	ByteArrayOutputStream boutAttach = null;
			    	if(dumpAllAttachments){
			    		boutAttach = (ByteArrayOutputStream) dumpAttachmentEngine(msg, ap, true); 
			    	}else{
			    		Object o = dumpAttachmentEngine(msg, ap, false);
			    		if(o == null){
			    			dumpAttach.setErrorContentNotSerializable("Contenuto attachment non recuperato??");
			    		}
			    		else if(o instanceof String s){
			    			boutAttach = new ByteArrayOutputStream();
			    			boutAttach.write(s.getBytes());
			    			boutAttach.flush();
			    			boutAttach.close();
			    		}
			    		else if(o instanceof java.io.ByteArrayOutputStream b){
			    			boutAttach = b;
			    		}
			    		else{
			    			dumpAttach.setErrorContentNotSerializable("Contenuto attachment non è visualizzabile, tipo: "+o.getClass().getName());
			    		}
			    	}
			    	dumpAttach.setContent(boutAttach);
			    	
			    	if(dumpMessaggio.getAttachments()==null) {
			    		dumpMessaggio.setAttachments(new ArrayList<>());
			    	}
			    	dumpMessaggio.getAttachments().add(dumpAttach);
			    }
			    
			}
			
		    return dumpMessaggio;
		}catch(Exception e){
			throw new MessageException(e.getMessage(),e);
		}
	}
	
	public static String dumpMessageAsString(DumpMessaggio msg, boolean dumpAllAttachments) throws MessageException{
		return dumpMessageAsString(msg, new DumpMessaggioConfig(),dumpAllAttachments);
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
	
	public static String dumpMessageAsString(OpenSPCoop2SoapMessage msg,
			boolean dumpAllAttachments) throws MessageException{
		return dumpMessageAsString(msg, new DumpMessaggioConfig(), dumpAllAttachments);
	}
	public static String dumpMessageAsString(OpenSPCoop2SoapMessage msg,
			DumpMessaggioConfig config,
			boolean dumpAllAttachments) throws MessageException{
		try{
			StringBuilder out = new StringBuilder();
			
			Map<String, List<String>> pTrasporto = null;
			if(msg.getTransportRequestContext()!=null) {
				if(msg.getTransportRequestContext().getHeaders()!=null && 
						msg.getTransportRequestContext().getHeaders().size()>0 &&
						config.isDumpHeaders()) {
					pTrasporto = msg.getTransportRequestContext().getHeaders();
				}
			}
			else if(msg.getTransportResponseContext()!=null) {
				if(msg.getTransportResponseContext().getHeaders()!=null && 
						msg.getTransportResponseContext().getHeaders().size()>0 &&
						config.isDumpHeaders()) {
					pTrasporto = msg.getTransportResponseContext().getHeaders();
				}
			}
			if(config.isDumpHeaders()) {
				out.append("------ Header di trasporto ------\n");
				if(pTrasporto!=null && pTrasporto.size()>0) {
					Iterator<String> keys = pTrasporto.keySet().iterator();
					while (keys.hasNext()) {
						String key = keys.next();
						if(key!=null){
							List<String> values = pTrasporto.get(key);
							if(values!=null && !values.isEmpty()) {
								for (String value : values) {
									out.append("- ").append(key).append(": ").append(value).append("\n");	
								}
							}
						}
					}
				}
				else {
					out.append("Non presenti\n");
				}
			}
			
			boolean msgWithAttachments = msg.countAttachments()>0;
			
			if(config.isDumpBody()) {
				out.append("------ SOAPEnvelope (ContentType: "+msg.getContentType()+") (MessageType: "+msg.getMessageType()+") ------\n");
				
				SOAPPart soapPart = msg.getSOAPPart();
				
				if(msgWithAttachments) {
					
					Map<String, List<String>> mime = new HashMap<>();
					Iterator<?> itM = soapPart.getAllMimeHeaders();
			    	if(itM!=null) {
				    	while(itM.hasNext()) {
				    		Object keyO = itM.next();
				    		if(keyO instanceof String key) {
				    			
				    			if(!HttpConstants.CONTENT_ID.equalsIgnoreCase(key) &&
				    					!HttpConstants.CONTENT_LOCATION.equalsIgnoreCase(key) &&
				    					!HttpConstants.CONTENT_TYPE.equalsIgnoreCase(key) &&
				    					!config.isDumpMultipartHeaders()) {
				    				continue;
				    			}
				    			
				    			List<String> l = new ArrayList<>();
				    			String [] values = soapPart.getMimeHeader(key);
				    			if(values!=null && values.length>0) {
				    				l.addAll(Arrays.asList(values));
				    			}
				    			
				    			if(!l.isEmpty() &&
				    				(HttpConstants.CONTENT_ID.equalsIgnoreCase(key) ||
				    						HttpConstants.CONTENT_LOCATION.equalsIgnoreCase(key) ||
				    						HttpConstants.CONTENT_TYPE.equalsIgnoreCase(key) ||
				    						config.isDumpMultipartHeaders()) 
				    				){
				    				mime.put(key, l);
				    			}

				    		}
				    	}
			    	}
			    	
			    	if(mime.size()>0) {
			    		out.append("\n*** MimePart Header ***\n");
			    		Iterator<String> it = mime.keySet().iterator();
			    		while (it.hasNext()) {
							String key = it.next();
							List<String> l = mime.get(key);
							if(l!=null && !l.isEmpty()) {
								for (String v : l) {
									out.append("- ").append(key).append(": ").append(v).append("\n");			
								}
							}
						}
			    	}
					
				}
				
				out.append("\n");
				
				out.append(msg.getAsString(soapPart.getEnvelope(),false));
			}
			
			if(config.isDumpAttachments()) {
				java.util.Iterator<?> it = msg.getAttachments();
				int index = 1;
			    while(it.hasNext()){
			    	AttachmentPart ap = 
			    		(AttachmentPart) it.next();
			    	out.append("\n------ Attachment-").append(index).append(" ------\n");
			    	
			    	out.append("\n*** MimePart Header ***\n");
			    	if(ap.getContentId()!=null) {
						out.append("- ").append(HttpConstants.CONTENT_ID).append(": ").append(ap.getContentId()).append("\n");
					}
					if(ap.getContentLocation()!=null) {
						out.append("- ").append(HttpConstants.CONTENT_LOCATION).append(": ").append(ap.getContentLocation()).append("\n");
					}
					if(ap.getContentType()!=null) {
						out.append("- ").append(HttpConstants.CONTENT_TYPE).append(": ").append(ap.getContentType()).append("\n");
					}

					if(config.isDumpMultipartHeaders()) { 
						Iterator<?> itM = ap.getAllMimeHeaders();
						if(itM!=null) {
					    	while(itM.hasNext()) {
					    		Object keyO = itM.next();
					    		if(keyO instanceof String key) {
					    			if(HttpConstants.CONTENT_ID.equalsIgnoreCase(key) ||
					    					HttpConstants.CONTENT_LOCATION.equalsIgnoreCase(key) ||
					    					HttpConstants.CONTENT_TYPE.equalsIgnoreCase(key)) {
					    				continue;
					    			}
					    			String [] values = ap.getMimeHeader(key);
					    			if(values!=null && values.length>0) {
					    				for (String value : values) {
					    					out.append("- ").append(key).append(": ").append(value).append("\n");
										}
					    			}
					    		}
					    	}
						}
					}
					out.append("\n");
			    					
			    	if(dumpAllAttachments){
			    		out.append(DumpSoapMessageUtils.dumpAttachment(msg, ap));
			    	}else{
			    		//Object o = ap.getContent(); NON FUNZIONA CON TOMCAT
			    		Object o = ap.getDataHandler().getContent();
			    		/**System.out.println("["+o.getClass().getName()+"])"+ap.getContentType()+"(");*/
			    		if(HttpConstants.CONTENT_TYPE_OPENSPCOOP2_TUNNEL_SOAP.equals(ap.getContentType())){
			    			 // reimposto handler
			    			 DumpSoapMessageUtils.rebuildAttachmentAsByteArray(msg, ap);
			    		}
			    		
			    		if(o instanceof String s){
			    			out.append(s);
			    		}else{
			    			 out.append("Contenuto attachments non è visualizzabile, tipo: ").append(o.getClass().getName());
			    		}
			    	}
			    	index++;
			    }
			}
		    return out.toString();
		}catch(Exception e){
			throw new MessageException(e.getMessage(),e);
		}
	}
	
	public static String dumpAttachment(OpenSPCoop2SoapMessage msg,AttachmentPart ap) throws MessageException{
		Object o = dumpAttachmentEngine(msg, ap, false);
		// Metodo sopra non torna mai null, segnalato da sonarqube
		/**if(o == null){
			throw new MessageException("Dump error (return null reference)");
		}*/
		if(o instanceof String s){
			return s;
		}
		if(o instanceof java.io.ByteArrayOutputStream b){
			String s = null;
			try (java.io.ByteArrayOutputStream bout = b;){
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
	public static byte[] dumpAttachmentAsByteArray(OpenSPCoop2SoapMessage msg,AttachmentPart ap) throws MessageException{
		Object o = dumpAttachmentEngine(msg, ap, false);
		// Metodo sopra non torna mai null, segnalato da sonarqube
		/**if(o == null){
			throw new MessageException("Dump error (return null reference)");
		}*/
		if(o instanceof String str){
			return str.getBytes();
		}
		if(o instanceof java.io.ByteArrayOutputStream bo){
			byte[] b = null;
			try (java.io.ByteArrayOutputStream bout = bo;){
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
	
	private static Object dumpAttachmentEngine(OpenSPCoop2SoapMessage msg,AttachmentPart ap, boolean forceReturnAsByteArrayOutputStream) throws MessageException{
		try{		
			java.io.ByteArrayOutputStream bout = null;
			//Object o = ap.getContent(); NON FUNZIONA CON TOMCAT
			//java.io.InputStream inputDH = dh.getInputStream(); NON FUNZIONA CON JBOSS7 e JAVA7 e imbustamentoSOAP con GestioneManifest e rootElementMaggioreUno (tipo: application/octet-stream)
			Object o = ap.getDataHandler().getContent();
			String s = null;
			boolean forceRebuildDataHandler = false;
			if(o!=null){
				if(o instanceof byte[] b){
					bout = new ByteArrayOutputStream();
					bout.write(b);
					bout.flush();
					bout.close();
				}
				else if(o instanceof InputStream is){
					bout = new java.io.ByteArrayOutputStream();
			    	byte [] readB = new byte[8192];
					int readByte = 0;
					while((readByte = is.read(readB))!= -1)
						bout.write(readB,0,readByte);
					is.close();
					bout.flush();
					bout.close();
				}
				else if(o instanceof String st){
					s = st;
					bout = new ByteArrayOutputStream();
					bout.write(s.getBytes());
					bout.flush();
					bout.close();
				}
				else{
					// Provo con codiceOriginale ma in jboss non funziona sempre
					try {
						jakarta.activation.DataHandler dh= ap.getDataHandler();  
				    	java.io.InputStream inputDH = dh.getInputStream();
						bout = new java.io.ByteArrayOutputStream();
				    	byte [] readB = new byte[8192];
						int readByte = 0;
						while((readByte = inputDH.read(readB))!= -1)
							bout.write(readB,0,readByte);
						inputDH.close();
						bout.flush();
						bout.close();		
					}catch(Exception e) {
						if(o instanceof javax.xml.transform.dom.DOMSource domSource) {
							try {
								bout = new ByteArrayOutputStream();
								bout.write(MessageXMLUtils.getInstance(msg.getFactory()).toByteArray(domSource.getNode()));
								bout.flush();
								bout.close();
								forceRebuildDataHandler = true;
							}catch(Exception eInternal) {
								throw new UtilsMultiException(e, eInternal);
							}
						}
						else {
							throw e;
						}
					}
				}
			}
			else{
				// Provo con codiceOriginale ma in jboss non funziona sempre
				jakarta.activation.DataHandler dh= ap.getDataHandler();  
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
			if(HttpConstants.CONTENT_TYPE_OPENSPCOOP2_TUNNEL_SOAP.equals(ap.getContentType())){
				 // reimposto handler
				 DumpSoapMessageUtils.rebuildAttachmentAsByteArray(msg, ap);
			}
			else if(forceRebuildDataHandler){
				 // reimposto handler
				 DumpSoapMessageUtils.rebuildDataHandlerAttachment(msg, ap, bout);
			}
			else if(MailcapActivationReader.existsDataContentHandler(ap.getContentType())){
				/**ap.setDataHandler(new jakarta.activation.DataHandler(bout.toByteArray(),ap.getContentType()));*/
				// In axiom l'update del data handler non funziona
				if(ap.getContentType()!=null && ap.getContentType().startsWith(HttpConstants.CONTENT_TYPE_PLAIN)){
					if(s!=null){
						msg.updateAttachmentPart(ap, s, ap.getContentType());
					}
					else{
						msg.updateAttachmentPart(ap, bout.toString(),ap.getContentType());
					}
				}else{
					// Stessa guardia di updateAttachmentPart(byte[],contentType): solo per i content-type che attivano
					// una costruzione con parsing (oggi 'text/xml') la ricostruzione puo' fallire se il contenuto non e'
					// valido. In tal caso, se abilitato, l'allegato viene registrato come binario (byte + content-type)
					// senza far fallire la registrazione del messaggio.
					String baseType = ContentTypeUtilities.readBaseTypeFromContentType(ap.getContentType());
					if(DumpMessaggio.isFallbackBinaryOnParseError() && HttpConstants.CONTENT_TYPE_TEXT_XML.equals(baseType)){
						try{
							msg.updateAttachmentPart(ap, bout.toByteArray(),ap.getContentType());
						}catch(Exception parseError){
							// Contenuto non valido per il content-type dichiarato (es. xml non well-formed): registra come binario.
							// Si usa un DataHandler basato su DataSource (ByteArrayDataSource), non object-based: in serializzazione
							// 'new DataHandler(byte[],contentType)' invocherebbe l'XmlDataContentHandler ('[B cannot be cast to Source'),
							// mentre con una DataSource lo stream viene scritto grezzo, preservando byte e content-type senza ulteriore parsing.
							msg.updateAttachmentPart(ap, new DataHandler(new ByteArrayDataSource(bout.toByteArray(), ap.getContentType())));
						}
					}else{
						msg.updateAttachmentPart(ap, bout.toByteArray(),ap.getContentType());
					}
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
		
	private static void rebuildAttachmentAsByteArray(OpenSPCoop2SoapMessage msg,AttachmentPart ap) throws MessageException{
		try{
			jakarta.activation.DataHandler dh= ap.getDataHandler();  
	    	java.io.InputStream inputDH = dh.getInputStream();
			java.io.ByteArrayOutputStream bout = new java.io.ByteArrayOutputStream();
	    	byte [] readB = new byte[8192];
			int readByte = 0;
			while((readByte = inputDH.read(readB))!= -1)
				bout.write(readB,0,readByte);
			inputDH.close();
			bout.flush();
			bout.close();
			/**ap.setDataHandler(new jakarta.activation.DataHandler(bout.toByteArray(),ap.getContentType()));*/
			// In axiom l'update del data handler non funziona
			msg.updateAttachmentPart(ap, bout.toByteArray(),ap.getContentType());
		}catch(Exception e){
			throw new MessageException(e.getMessage(),e);
		}
	}
	
	private static void rebuildDataHandlerAttachment(OpenSPCoop2SoapMessage msg,AttachmentPart ap, ByteArrayOutputStream boutAlreadyRead) throws MessageException{
		try{
			/**ap.setDataHandler(new jakarta.activation.DataHandler(bout.toByteArray(),ap.getContentType()));*/
			// In axiom l'update del data handler non funziona
			msg.updateAttachmentPart(ap, new DataHandler(boutAlreadyRead.toByteArray(),ap.getContentType()));
		}catch(Exception e){
			throw new MessageException(e.getMessage(),e);
		}
	}


}
