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

package org.openspcoop2.message.rest;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import javax.mail.BodyPart;

import org.openspcoop2.message.OpenSPCoop2RestMessage;
import org.openspcoop2.message.OpenSPCoop2RestMimeMultipartMessage;
import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.message.exception.MessageException;
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

	
	public static String dumpMessage(OpenSPCoop2RestMessage<?> msg,boolean dumpAllBodyParts) throws MessageException{
		try{
			StringBuffer out = new StringBuffer();
			if(msg.hasContent()){
				out.append("------ Content ("+msg.getMessageType()+") ------\n");
				out.append(msg.getContentAsString());
			}
			else{
				out.append("------ No-Content ("+msg.getMessageType()+") ------\n");
			}
			if(MessageType.MIME_MULTIPART.equals(msg.getMessageType())){
				OpenSPCoop2RestMimeMultipartMessage msgMime = msg.castAsRestMimeMultipart();
				for (int i = 0; i < msgMime.getContent().countBodyParts(); i++) {
					BodyPart bodyPart = msgMime.getContent().getBodyPart(i);
					
					String tipoIdentificatore = "Position";
					String valoreIdentificatore = (i+1)+"";
					
					String tmp = msgMime.getContent().getContentID(bodyPart);
					if(tmp!=null){
						tipoIdentificatore = HttpConstants.CONTENT_ID;
						valoreIdentificatore = tmp;
					}
					
					if(tmp==null){
						tmp = msgMime.getContent().getContentLocation(bodyPart);
						if(tmp!=null){
							tipoIdentificatore = HttpConstants.CONTENT_LOCATION;
							valoreIdentificatore = tmp;
						}
					}
					
					if(tmp==null){
						tmp = msgMime.getContent().getContentDisposition(bodyPart);
						if(tmp!=null){
							tipoIdentificatore = HttpConstants.CONTENT_DISPOSITION;
							valoreIdentificatore = tmp;
						}
					}
					
					out.append("\n------ BodyPart "+tipoIdentificatore+" ["+valoreIdentificatore+"]------");
			    	
			    	out.append("\ntype["+bodyPart.getContentType()+"]: ");
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
		Object o = _dumpBodyPart(msg, bodyPart);
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
		Object o = _dumpBodyPart(msg, bodyPart);
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
	
	private static Object _dumpBodyPart(OpenSPCoop2RestMessage<?> msg,BodyPart bodyPart) throws MessageException{
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
			if(MailcapActivationReader.existsDataContentHandler(bodyPart.getContentType())){
				if(bodyPart.getContentType()!=null && bodyPart.getContentType().startsWith(HttpConstants.CONTENT_TYPE_PLAIN)){
					if(s!=null){
						bodyPart.setDataHandler(new javax.activation.DataHandler(s,bodyPart.getContentType()));
					}
					else{
						bodyPart.setDataHandler(new javax.activation.DataHandler(bout.toByteArray(),bodyPart.getContentType()));
					}
				}
				else{
					bodyPart.setDataHandler(new javax.activation.DataHandler(bout.toByteArray(),bodyPart.getContentType()));
				}
			}
			if(s!=null){
				return s;
			}else{
				return bout;
			}
		}catch(Exception e){
			throw new MessageException(e.getMessage(),e);
		}
	}


}
