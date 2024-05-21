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

package org.openspcoop2.security.message.wss4j;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.activation.DataHandler;
import javax.xml.soap.AttachmentPart;
import javax.xml.soap.MimeHeader;
import javax.xml.soap.MimeHeaders;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;

import org.apache.commons.codec.binary.Base64InputStream;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.cxf.attachment.AttachmentImpl;
import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.message.Attachment;
import org.apache.wss4j.dom.engine.WSSecurityEngineResult;
import org.apache.wss4j.dom.handler.WSHandlerResult;
import org.openspcoop2.message.OpenSPCoop2SoapMessage;
import org.openspcoop2.message.xml.MessageXMLUtils;
import org.openspcoop2.security.message.constants.SecurityConstants;
import org.openspcoop2.security.message.utils.AttachmentProcessingPart;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.dch.InputStreamDataSource;
import org.openspcoop2.utils.io.Base64Utilities;
import org.openspcoop2.utils.regexp.RegularExpressionEngine;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.slf4j.Logger;
import org.w3c.dom.Node;

/**
 * WSSUtilities
 *
 * @author Lorenzo Nardi (nardi@link.it)
 * @author Tommaso Burlon (tommaso.burlon@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class WSSUtilities {

	public static void printWSResult(Logger log,List<?> results){
		if(results!=null){
			Iterator<?> it = results.iterator();
			while (it.hasNext()) {
				Object object = it.next();
				if(object instanceof WSHandlerResult){
					WSHandlerResult wsResult = (WSHandlerResult) object;
					log.debug("Actor ["+wsResult.getActor()+"]");
					List<WSSecurityEngineResult> wsResultList =  wsResult.getResults();
					if(wsResultList!=null){
						for (int i = 0; i < wsResultList.size(); i++) {
							log.debug("WSResult["+i+"]="+wsResultList.get(i).toString());
						}
					}
				}
			}
		}
	}
	
	public static List<Attachment> readAttachments(List<String> cidAttachmentsForSecurity,OpenSPCoop2SoapMessage message, SoapMessage msgCtx) throws Exception{
		List<Attachment> listAttachments = null;
        if(cidAttachmentsForSecurity!=null && cidAttachmentsForSecurity.size()>0){
        	listAttachments = new ArrayList<Attachment>();
        	for (String cid : cidAttachmentsForSecurity) {       		
        		//System.out.println("GET ID ["+cid+"]");
        		MimeHeaders mhs = new MimeHeaders();
        		mhs.addHeader(HttpConstants.CONTENT_ID, cid);
        		Iterator<?> itAttach = message.getAttachments(mhs);
        		if(itAttach==null){
        			throw new Exception("Attachment with Content-ID ["+cid+"] not found");
        		}
        		AttachmentPart ap = (AttachmentPart) itAttach.next();
        		if(ap==null){
        			throw new Exception("Attachment with Content-ID ["+cid+"] not found");
        		}
        		listAttachments.add(convertToCxfAttachment(ap, msgCtx));
			}
        }
        return listAttachments;
	}
	
	public static List<Attachment> readAttachments(AttachmentProcessingPart app,OpenSPCoop2SoapMessage message, SoapMessage msgCtx) throws Exception{
        List<Attachment> listAttachments = null;
        if(app!=null){
        	List<AttachmentPart> listApDaTrattare = app.getOutput(message);
        	if(listApDaTrattare!=null && listApDaTrattare.size()>0){
        		listAttachments = new ArrayList<Attachment>();
        		for (int i = 0; i < listApDaTrattare.size(); i++) {
        			AttachmentPart ap = listApDaTrattare.get(i);
        			//System.out.println("AP ["+ap.getContentId()+"] ["+StringEscapeUtils.escapeXml(ap.getContentId())+"] ["+ap.getContentType()+"] add");
					listAttachments.add(convertToCxfAttachment(ap, msgCtx));
				}
        	}
        }
        return listAttachments;
	}
	
	private static Attachment convertToCxfAttachment(AttachmentPart ap, SoapMessage msgCtx) throws Exception{
		DataHandler dh = ap.getDataHandler();
		DataHandler dhNEW = null;
		byte[]bufferArray = null;
		String s = null;
		boolean encodeBase64 = msgCtx.containsKey(SecurityConstants.PRE_BASE64_ENCODING_ATTACHMENT) ?
				msgCtx.get(SecurityConstants.PRE_BASE64_ENCODING_ATTACHMENT).equals(SecurityConstants.PRE_BASE64_ENCODING_ATTACHMENT_TRUE) :
				SecurityConstants.PRE_BASE64_ENCODING_ATTACHMENT_DEFAULT;

		if(dh.getContentType()!=null && dh.getContentType().startsWith(HttpConstants.CONTENT_TYPE_PLAIN) && !encodeBase64){
			dhNEW = dh;
		}
		else if(!encodeBase64 && (RegularExpressionEngine.isMatch(dh.getContentType(),".*\\/xml")
				||
				RegularExpressionEngine.isMatch(dh.getContentType(),".*\\+xml"))){
			dhNEW = dh;
		}
		else{
			try{
				InputStream ins = null;
				if (encodeBase64) {
					ins = new Base64InputStream(dh.getInputStream(), true);
				} else {
					ins = dh.getInputStream();
				}

				InputStreamDataSource isds = new InputStreamDataSource(ap.getContentId(), dh.getContentType(), ins);
				dhNEW = new DataHandler(isds);
			}catch(javax.activation.UnsupportedDataTypeException edtx){
				// eccezione che può essere lanciata da dh.getInputStream() se il datahandler non è stato creato con un datasource

				// provo a prendere getContent
				Object o = dh.getContent();
				if(o!=null){
					if(o instanceof String){
						s = (String) o;
						//System.out.println("SET AS STRING");
						dhNEW = new DataHandler(s, dh.getContentType());
					}
					else if(o instanceof byte[]){
						bufferArray = (byte[])o;
						InputStreamDataSource isds = new InputStreamDataSource(ap.getContentId(), dh.getContentType(), bufferArray);
						dhNEW = new DataHandler(isds);
					}
					else if(o instanceof InputStream){
						InputStreamDataSource isds = new InputStreamDataSource(ap.getContentId(), dh.getContentType(), (InputStream)o);
						dhNEW = new DataHandler(isds);
					}
					else{
						throw new Exception("Attach ["+ap.getContentId()+"] ["+dh.getContentType()+"] with type not supported: "+o.getClass().getName(),edtx);
					}
				}
				else{
					throw new Exception("Attach ["+ap.getContentId()+"] ["+dh.getContentType()+"] error: "+edtx.getMessage(),edtx);
				}
			}
			
		}
		
		String id = ap.getContentId();
		boolean addAttachmentIdBrackets = msgCtx.containsKey(SecurityConstants.ADD_ATTACHMENT_ID_BRACKETS) ?
				msgCtx.get(SecurityConstants.ADD_ATTACHMENT_ID_BRACKETS).equals(SecurityConstants.ADD_ATTACHMENT_ID_BRACKETS_TRUE) :
					SecurityConstants.ADD_ATTACHMENT_ID_BRACKETS_DEFAULT;
		if (!addAttachmentIdBrackets) {
			id = id.replaceAll("(^<)|(>$)", "");
		}

		AttachmentImpl at = new AttachmentImpl(StringEscapeUtils.escapeXml(id));
		Iterator<MimeHeader> hdrs = ap.getAllMimeHeaders();
		while (hdrs.hasNext()) {
			MimeHeader hdr = hdrs.next();
			at.setHeader(hdr.getName(), hdr.getValue());
		}
		at.setDataHandler(dhNEW);
		return at;
	}
	
	private static Object postProcessAttachment(Object o, SoapMessage msgCtx) {
		boolean decodeBase64 = msgCtx.containsKey(SecurityConstants.POST_BASE64_DECODING_ATTACHMENT) ?
				msgCtx.get(SecurityConstants.POST_BASE64_DECODING_ATTACHMENT).equals(SecurityConstants.POST_BASE64_DECODING_ATTACHMENT_TRUE) :
				SecurityConstants.POST_BASE64_DECODING_ATTACHMENT_DEFAULT;

		if (decodeBase64) {
			if (o instanceof String) {
				return new String(Base64Utilities.decode((String) o));
			}
				
			if (o instanceof byte[]) {
				return Base64Utilities.decode((byte[])o);
			}
				
			if (o instanceof InputStream) {
				return new Base64InputStream((InputStream)o);
			}
		}
		return o;
	}

	public static void updateAttachments(List<Attachment> listAttachments,OpenSPCoop2SoapMessage message, SoapMessage msgCtx) throws Exception{
		if(listAttachments!=null && listAttachments.size()>0){
			boolean decodeBase64 = msgCtx.containsKey(SecurityConstants.POST_BASE64_DECODING_ATTACHMENT) ?
					msgCtx.get(SecurityConstants.POST_BASE64_DECODING_ATTACHMENT).equals(SecurityConstants.POST_BASE64_DECODING_ATTACHMENT_TRUE) :
					SecurityConstants.POST_BASE64_DECODING_ATTACHMENT_DEFAULT;
			boolean encodeBase64 = msgCtx.containsKey(SecurityConstants.POST_BASE64_ENCODING_ATTACHMENT) ?
					msgCtx.get(SecurityConstants.POST_BASE64_ENCODING_ATTACHMENT).equals(SecurityConstants.POST_BASE64_ENCODING_ATTACHMENT_TRUE) :
					SecurityConstants.POST_BASE64_ENCODING_ATTACHMENT_DEFAULT;

			for (Attachment attachmentPart : listAttachments) {

				MimeHeaders mhs = new MimeHeaders();
				mhs.addHeader(HttpConstants.CONTENT_ID, StringEscapeUtils.unescapeXml(attachmentPart.getId()));

				AttachmentPart ap = (AttachmentPart) message.getAttachments(mhs).next();
				if (encodeBase64)
					ap.addMimeHeader(HttpConstants.CONTENT_TRANSFER_ENCODING, HttpConstants.CONTENT_TRANSFER_ENCODING_VALUE_BASE64);
				if (decodeBase64)
					ap.addMimeHeader(HttpConstants.CONTENT_TRANSFER_ENCODING, HttpConstants.CONTENT_TRANSFER_ENCODING_VALUE_BINARY);

				DataHandler dh = attachmentPart.getDataHandler();
				byte[]bufferArray = null;
				String s = null;
				if(dh.getContentType()!=null && dh.getContentType().startsWith(HttpConstants.CONTENT_TYPE_PLAIN)){
					Object o = dh.getContent();
					if(o instanceof String){
						s = (String) postProcessAttachment(o, msgCtx);
						message.updateAttachmentPart(ap, s, dh.getContentType());
					}
					else if(o instanceof byte[]){
						bufferArray = (byte[]) postProcessAttachment(o, msgCtx);
						message.updateAttachmentPart(ap, bufferArray, dh.getContentType());
					}
					else if(o instanceof InputStream){
						bufferArray = Utilities.getAsByteArray((InputStream)postProcessAttachment(o, msgCtx));
						message.updateAttachmentPart(ap, bufferArray, dh.getContentType());
					}
					else{
						throw new Exception("Attach-Plain ["+attachmentPart.getId()+"] ["+dh.getContentType()+"] with type not supported: "+o.getClass().getName());
					}
				}
				else if(RegularExpressionEngine.isMatch(dh.getContentType(),".*\\/xml")
						||
						RegularExpressionEngine.isMatch(dh.getContentType(),".*\\+xml")){
					// potenziale xml
					
					Object o = dh.getContent();
					boolean testXml = false;
					boolean updated = false;
					Node n = null;
					if(o instanceof String){
						s = (String) postProcessAttachment(o, msgCtx);
						//System.out.println("SET AS STRING");
						message.updateAttachmentPart(ap, s, dh.getContentType());
						updated = true;
					}
					else if(o instanceof byte[]){
						bufferArray = (byte[])postProcessAttachment(o, msgCtx);
						//System.out.println("SET AS SOURCE (byte[])");
						testXml = true;
					}
					else if(o instanceof InputStream){
						bufferArray = Utilities.getAsByteArray((InputStream)postProcessAttachment(o, msgCtx));
						//System.out.println("SET AS SOURCE (IS)");
						testXml = true;
					}
					else if(o instanceof Node){
						n = (Node) postProcessAttachment(o, msgCtx);
						//System.out.println("SET AS SOURCE (NODE)");
					}
					else{
						throw new Exception("Attach-XML ["+attachmentPart.getId()+"] ["+dh.getContentType()+"] with type not supported: "+o.getClass().getName());
					}
					
					if(testXml){
						try{
							if(n==null){
								n = MessageXMLUtils.getInstance(message.getFactory()).newElement(bufferArray);
							}
						}catch(Exception e){
							// no xml
							message.updateAttachmentPart(ap, bufferArray, dh.getContentType());
							updated = true;
						}
					}
					
					if(updated==false){
						Source streamSource = new DOMSource(n);
						DataHandler newDH = new DataHandler(streamSource, dh.getContentType());
						message.updateAttachmentPart(ap,newDH);
					}
				}
				else{
					try{
						InputStream ins = (InputStream) postProcessAttachment(dh.getInputStream(), msgCtx);
						InputStreamDataSource isds = new InputStreamDataSource(attachmentPart.getId(), dh.getContentType(), ins);
						DataHandler dhNEW = new DataHandler(isds);
						message.updateAttachmentPart(ap, dhNEW);
					}catch(javax.activation.UnsupportedDataTypeException edtx){
						// eccezione che può essere lanciata da dh.getInputStream() se il datahandler non è stato creato con un datasource

						// provo a prendere getContent
						Object o = dh.getContent();
						DataHandler dhNEW = null;
						if(o!=null){
    						if(o instanceof String){
        						s = (String) o;
        						//System.out.println("SET AS STRING");
        						dhNEW = new DataHandler(s, dh.getContentType());
        					}
    						else if(o instanceof byte[]){
    							bufferArray = (byte[])o;
    							InputStreamDataSource isds = new InputStreamDataSource(attachmentPart.getId(), dh.getContentType(), bufferArray);
        						dhNEW = new DataHandler(isds);
    						}
    						else if(o instanceof InputStream){
    							InputStreamDataSource isds = new InputStreamDataSource(attachmentPart.getId(), dh.getContentType(), (InputStream)o);
        						dhNEW = new DataHandler(isds);
    						}
    						else{
        						throw new Exception("Attach ["+ap.getContentId()+"] ["+dh.getContentType()+"] with type not supported: "+o.getClass().getName(),edtx);
        					}
						}
						else{
							throw new Exception("Attach ["+ap.getContentId()+"] ["+dh.getContentType()+"] error: "+edtx.getMessage(),edtx);
						}
						message.updateAttachmentPart(ap, dhNEW);
					}
				}

			}
		}
	}
	
}
