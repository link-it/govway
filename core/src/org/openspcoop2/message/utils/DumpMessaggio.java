/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it). 
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


package org.openspcoop2.message.utils;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.message.exception.MessageException;
import org.openspcoop2.utils.transport.http.HttpConstants;

/**
 * DumpMessaggio
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DumpMessaggio implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4718160136521047108L;
		
	private MessageType messageType;
	
	private String contentType;
	
	private HashMap<String, String> headers = new HashMap<>();
	
	private ByteArrayOutputStream body;
	private DumpMessaggioMultipartInfo multipartInfoBody;
	
	private List<DumpAttachment> attachments;
	

	public long getBodyLength() {
		if(this.body!=null) {
			return this.body.size();
		}
		return 0;
	}
	
	public byte[] getBody() {
		if(this.body!=null) {
			return this.body.toByteArray();
		}
		return null;
	}
	
	public String getBodyAsString() {
		if(this.body!=null) {
			return this.body.toString();
		}
		return null;
	}

	public void setBody(ByteArrayOutputStream body) {
		this.body = body;
	}

	public String getContentType() {
		return this.contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	
	public List<DumpAttachment> getAttachments() {
		return this.attachments;
	}

	public void setAttachments(List<DumpAttachment> attachments) {
		this.attachments = attachments;
	}
	
	public DumpMessaggioMultipartInfo getMultipartInfoBody() {
		return this.multipartInfoBody;
	}

	public void setMultipartInfoBody(DumpMessaggioMultipartInfo multipartInfoBody) {
		this.multipartInfoBody = multipartInfoBody;
	}


	public HashMap<String, String> getHeaders() {
		return this.headers;
	}

	public void setHeaders(HashMap<String, String> headers) {
		this.headers = headers;
	}
	
	public MessageType getMessageType() {
		return this.messageType;
	}

	public void setMessageType(MessageType messageType) {
		this.messageType = messageType;
	}

	
	public String toString(DumpMessaggioConfig config, boolean dumpAllAttachments) throws MessageException{
		try{
			StringBuffer out = new StringBuffer();
						
			if(config.isDumpHeaders()) {
				out.append("------ Header di trasporto ------\n");
				if(this.getHeaders()!=null && this.getHeaders().size()>0){
					Iterator<?> it = this.getHeaders().keySet().iterator();
					while (it.hasNext()) {
						String key = (String) it.next();
						if(key!=null){
							String value = this.getHeaders().get(key);
							out.append("- "+key+": "+value+"\n");
						}
					}
				}
				else {
					out.append("Non presenti\n");
				}
			}
			
			boolean hasContent = this.getBodyLength()>0;
			String contentString = "Body";
			String contentType = "";
			String attachString = "BodyPart";
			if(!hasContent){
				contentString = "Empty Body";
			}
			if(MessageType.SOAP_11.equals(this.getMessageType()) || MessageType.SOAP_12.equals(this.getMessageType())) {
				contentString = "SOAPEnvelope";
				attachString = "Attachment";
			}
			if(hasContent) {
				if(this.getContentType()!=null) {
					contentType = " (ContentType: "+this.getContentType()+")";
				}
			}
			
			if(config.isDumpBody()) {
				out.append("------ "+contentString+contentType+" (MessageType: "+this.getMessageType()+") ------\n");

				if(this.getMultipartInfoBody()!=null) {
					out.append("\n*** MimePart Header ***\n");
					if(this.getMultipartInfoBody().getContentId()!=null) {
						out.append("- "+HttpConstants.CONTENT_ID+": "+this.getMultipartInfoBody().getContentId()+"\n");
					}
					if(this.getMultipartInfoBody().getContentLocation()!=null) {
						out.append("- "+HttpConstants.CONTENT_LOCATION+": "+this.getMultipartInfoBody().getContentLocation()+"\n");
					}
					if(this.getMultipartInfoBody().getContentType()!=null) {
						out.append("- "+HttpConstants.CONTENT_TYPE+": "+this.getMultipartInfoBody().getContentType()+"\n");
					}
					if(config.isDumpMultipartHeaders() && this.getMultipartInfoBody().getHeaders().size()>0) {
						Iterator<?> itM = this.getMultipartInfoBody().getHeaders().keySet().iterator();
				    	while(itM.hasNext()) {
				    		Object keyO = itM.next();
				    		if(keyO instanceof String) {
				    			String key = (String) keyO;
				    			if(HttpConstants.CONTENT_ID.equalsIgnoreCase(key) ||
				    					HttpConstants.CONTENT_LOCATION.equalsIgnoreCase(key) ||
				    					HttpConstants.CONTENT_TYPE.equalsIgnoreCase(key)) {
				    				continue;
				    			}
				    			String value = this.getMultipartInfoBody().getHeaders().get(key);
				    			out.append("- "+key+": "+value+"\n");
				    		}
				    	}
					}
				}
				
				out.append("\n");
				
				if(this.getBodyLength()>0) {
					out.append(this.getBodyAsString());
				}
			}
			
			if(config.isDumpAttachments() && this.getAttachments()!=null && this.getAttachments().size()>0){
				java.util.Iterator<?> it = this.getAttachments().iterator();
				int index = 1;
			    while(it.hasNext()){
			    	DumpAttachment ap = 
			    		(DumpAttachment) it.next();
			    	out.append("\n------ "+attachString+"-"+(index)+" ------\n");
				
		    		out.append("\n*** MimePart Header ***\n");
			    	if(ap.getContentId()!=null) {
						out.append("- "+HttpConstants.CONTENT_ID+": "+ap.getContentId()+"\n");
					}
					if(ap.getContentLocation()!=null) {
						out.append("- "+HttpConstants.CONTENT_LOCATION+": "+ap.getContentLocation()+"\n");
					}
					if(ap.getContentType()!=null) {
						out.append("- "+HttpConstants.CONTENT_TYPE+": "+ap.getContentType()+"\n");
					}
					if(config.isDumpMultipartHeaders() && ap.getHeaders().size()>0) {
						Iterator<?> itM = ap.getHeaders().keySet().iterator();
				    	while(itM.hasNext()) {
				    		Object keyO = itM.next();
				    		if(keyO instanceof String) {
				    			String key = (String) keyO;
				    			if(HttpConstants.CONTENT_ID.equalsIgnoreCase(key) ||
				    					HttpConstants.CONTENT_LOCATION.equalsIgnoreCase(key) ||
				    					HttpConstants.CONTENT_TYPE.equalsIgnoreCase(key)) {
				    				continue;
				    			}
				    			String value = ap.getHeaders().get(key);
				    			out.append("- "+key+": "+value+"\n");
				    		}
				    	}
					}
					out.append("\n");
			    				
					if(ap.getErrorContentNotSerializable()!=null) {
						out.append(ap.getErrorContentNotSerializable());
					}
					else {
						out.append(ap.getContentAsString(!dumpAllAttachments));
					}
					
			    	index++;
				}
			}
			
		    return out.toString();
		}catch(Exception e){
			throw new MessageException(e.getMessage(),e);
		}
	}
}
