/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it). 
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
import javax.xml.soap.MimeHeaders;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.cxf.attachment.AttachmentImpl;
import org.apache.cxf.message.Attachment;
import org.apache.wss4j.dom.engine.WSSecurityEngineResult;
import org.apache.wss4j.dom.handler.WSHandlerResult;
import org.openspcoop2.message.OpenSPCoop2SoapMessage;
import org.openspcoop2.message.xml.MessageXMLUtils;
import org.openspcoop2.security.message.utils.AttachmentProcessingPart;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.dch.InputStreamDataSource;
import org.openspcoop2.utils.regexp.RegularExpressionEngine;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.slf4j.Logger;
import org.w3c.dom.Node;

/**
 * WSSUtilities
 *
 * @author Lorenzo Nardi (nardi@link.it)
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
	
	public static List<Attachment> readAttachments(List<String> cidAttachmentsForSecurity,OpenSPCoop2SoapMessage message) throws Exception{
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
        		listAttachments.add(convertToCxfAttachment(ap));
			}
        }
        return listAttachments;
	}
	
	public static List<Attachment> readAttachments(AttachmentProcessingPart app,OpenSPCoop2SoapMessage message) throws Exception{
        List<Attachment> listAttachments = null;
        if(app!=null){
        	List<AttachmentPart> listApDaTrattare = app.getOutput(message);
        	if(listApDaTrattare!=null && listApDaTrattare.size()>0){
        		listAttachments = new ArrayList<Attachment>();
        		for (int i = 0; i < listApDaTrattare.size(); i++) {
        			AttachmentPart ap = listApDaTrattare.get(i);
        			//System.out.println("AP ["+ap.getContentId()+"] ["+StringEscapeUtils.escapeXml(ap.getContentId())+"] ["+ap.getContentType()+"] add");
					listAttachments.add(convertToCxfAttachment(ap));
				}
        	}
        }
        return listAttachments;
	}
	
	private static Attachment convertToCxfAttachment(AttachmentPart ap) throws Exception{
		DataHandler dh = ap.getDataHandler();
		DataHandler dhNEW = null;
		byte[]bufferArray = null;
		String s = null;
		if(dh.getContentType()!=null && dh.getContentType().startsWith(HttpConstants.CONTENT_TYPE_PLAIN)){
			//System.out.println("STRING");
			dhNEW = dh;
		}
		else if(RegularExpressionEngine.isMatch(dh.getContentType(),".*\\/xml")
				||
				RegularExpressionEngine.isMatch(dh.getContentType(),".*\\+xml")){
			//System.out.println("XML");
			dhNEW = dh;
		}
		else{   					
			//System.out.println("OTHER");    					
			try{
				InputStreamDataSource isds = new InputStreamDataSource(ap.getContentId(), dh.getContentType(), dh.getInputStream());
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
		
		Attachment at = new AttachmentImpl(StringEscapeUtils.escapeXml(ap.getContentId()), dhNEW);
		return at;
	}
	
	public static void updateAttachments(List<Attachment> listAttachments,OpenSPCoop2SoapMessage message) throws Exception{
		if(listAttachments!=null && listAttachments.size()>0){
			for (Attachment attachmentPart : listAttachments) {
				
				//System.out.println("AP ["+attachmentPart.getId()+"] ["+StringEscapeUtils.unescapeXml(attachmentPart.getId())+"]");
				
				MimeHeaders mhs = new MimeHeaders();
				mhs.addHeader(HttpConstants.CONTENT_ID, StringEscapeUtils.unescapeXml(attachmentPart.getId()));
				AttachmentPart ap = (AttachmentPart) message.getAttachments(mhs).next();
				
				DataHandler dh = attachmentPart.getDataHandler();
				//System.out.println("AP ["+dh.getContentType()+"] ["+dh.getClass().getName()+"]");
				byte[]bufferArray = null;
				String s = null;
				if(dh.getContentType()!=null && dh.getContentType().startsWith(HttpConstants.CONTENT_TYPE_PLAIN)){
					Object o = dh.getContent();
					if(o instanceof String){
						s = (String) o;
						message.updateAttachmentPart(ap, s, dh.getContentType());
					}
					else if(o instanceof byte[]){
						bufferArray = (byte[])o;
						message.updateAttachmentPart(ap, bufferArray, dh.getContentType());
					}
					else if(o instanceof InputStream){
						bufferArray = Utilities.getAsByteArray((InputStream)o);
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
						s = (String) o;
						//System.out.println("SET AS STRING");
						message.updateAttachmentPart(ap, s, dh.getContentType());
						updated = true;
					}
					else if(o instanceof byte[]){
						bufferArray = (byte[])o;
						//System.out.println("SET AS SOURCE (byte[])");
						testXml = true;
					}
					else if(o instanceof InputStream){
						bufferArray = Utilities.getAsByteArray((InputStream)o);
						//System.out.println("SET AS SOURCE (IS)");
						testXml = true;
					}
					else if(o instanceof Node){
						n = (Node) n;
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
						InputStreamDataSource isds = new InputStreamDataSource(attachmentPart.getId(), dh.getContentType(), dh.getInputStream());
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
