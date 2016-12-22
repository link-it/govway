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

package org.openspcoop2.message.soap;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import javax.xml.soap.AttachmentPart;

import org.openspcoop2.message.OpenSPCoop2SoapMessage;
import org.openspcoop2.message.exception.MessageException;
import org.openspcoop2.utils.dch.MailcapActivationReader;
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

	
	public static String dumpMessage(OpenSPCoop2SoapMessage msg,boolean dumpAllAttachments) throws MessageException{
		try{
			StringBuffer out = new StringBuffer();
			out.append("------ SOAPEnvelope ------\n");
			out.append(msg.getAsString(msg.getSOAPPart().getEnvelope(),false));
			java.util.Iterator<?> it = msg.getAttachments();
		    while(it.hasNext()){
		    	AttachmentPart ap = 
		    		(AttachmentPart) it.next();
		    	if(ap.getContentId()!=null)
		    		out.append("\n------ Attachment id["+ap.getContentId()+"]------");
		    	else if(ap.getContentLocation()!=null)
		    		out.append("\n------ Attachment location["+ap.getContentLocation()+"]------");
		    	out.append("\nId["+ap.getContentId()+"] location["+ap.getContentLocation()+"] type["+ap.getContentType()+"]: ");
				
		    	if(dumpAllAttachments){
		    		out.append(DumpSoapMessageUtils.dumpAttachment(msg, ap));
		    	}else{
		    		//Object o = ap.getContent(); NON FUNZIONA CON TOMCAT
		    		Object o = ap.getDataHandler().getContent();
		    		//System.out.println("["+o.getClass().getName()+"])"+ap.getContentType()+"(");
		    		if(HttpConstants.CONTENT_TYPE_OPENSPCOOP2_TUNNEL_SOAP.equals(ap.getContentType())){
		    			 // reimposto handler
		    			 DumpSoapMessageUtils.rebuildAttachmentAsByteArray(msg, ap);
		    		}
		    		
		    		if(o instanceof String){
		    			out.append((String)o);
		    		}else{
		    			 out.append("Contenuto attachments non è visualizzabile, tipo: "+o.getClass().getName());
		    		}
		    	}
		    }
		    return out.toString();
		}catch(Exception e){
			throw new MessageException(e.getMessage(),e);
		}
	}
	
	public static String dumpAttachment(OpenSPCoop2SoapMessage msg,AttachmentPart ap) throws MessageException{
		Object o = _dumpAttachment(msg, ap);
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
	public static byte[] dumpAttachmentAsByteArray(OpenSPCoop2SoapMessage msg,AttachmentPart ap) throws MessageException{
		Object o = _dumpAttachment(msg, ap);
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
	
	private static Object _dumpAttachment(OpenSPCoop2SoapMessage msg,AttachmentPart ap) throws MessageException{
		try{		
			java.io.ByteArrayOutputStream bout = null;
			//Object o = ap.getContent(); NON FUNZIONA CON TOMCAT
			//java.io.InputStream inputDH = dh.getInputStream(); NON FUNZIONA CON JBOSS7 e JAVA7 e imbustamentoSOAP con GestioneManifest e rootElementMaggioreUno (tipo: application/octet-stream)
			Object o = ap.getDataHandler().getContent();
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
					javax.activation.DataHandler dh= ap.getDataHandler();  
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
				javax.activation.DataHandler dh= ap.getDataHandler();  
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
			else if(MailcapActivationReader.existsDataContentHandler(ap.getContentType())){
				//ap.setDataHandler(new javax.activation.DataHandler(bout.toByteArray(),ap.getContentType()));
				// In axiom l'update del data handler non funziona
				if(ap.getContentType()!=null && ap.getContentType().startsWith(HttpConstants.CONTENT_TYPE_PLAIN)){
					if(s!=null){
						msg.updateAttachmentPart(ap, s, ap.getContentType());
					}
					else{
						msg.updateAttachmentPart(ap, bout.toString(),ap.getContentType());
					}
				}else{
					msg.updateAttachmentPart(ap, bout.toByteArray(),ap.getContentType());
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
		
	private static void rebuildAttachmentAsByteArray(OpenSPCoop2SoapMessage msg,AttachmentPart ap) throws MessageException{
		try{
			javax.activation.DataHandler dh= ap.getDataHandler();  
	    	java.io.InputStream inputDH = dh.getInputStream();
			java.io.ByteArrayOutputStream bout = new java.io.ByteArrayOutputStream();
	    	byte [] readB = new byte[8192];
			int readByte = 0;
			while((readByte = inputDH.read(readB))!= -1)
				bout.write(readB,0,readByte);
			inputDH.close();
			bout.flush();
			bout.close();
			//ap.setDataHandler(new javax.activation.DataHandler(bout.toByteArray(),ap.getContentType()));
			// In axiom l'update del data handler non funziona
			msg.updateAttachmentPart(ap, bout.toByteArray(),ap.getContentType());
		}catch(Exception e){
			throw new MessageException(e.getMessage(),e);
		}
	}


}
