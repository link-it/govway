/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it). 
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
import java.util.Map;

import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.message.exception.MessageException;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.digest.DigestEncoding;
import org.openspcoop2.utils.transport.TransportUtils;
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
	
	private Map<String, List<String>> headers = new HashMap<>();
	
	private transient ByteArrayOutputStream body;
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

	public String getBodyBase64Digest(String algorithm) throws UtilsException{
		return getBodyDigest(algorithm, DigestEncoding.BASE64, false);
	}
	public String getBodyBase64Digest(String algorithm, String rfc3230) throws UtilsException{
		// // per invocazioni dinamiche
		boolean v = false;
		try {
			v = Boolean.valueOf(rfc3230);
		}catch(Exception e) {
			throw new UtilsException("Uncorrect boolean value '"+rfc3230+"': "+e.getMessage(),e);
		}
		return getBodyBase64Digest(algorithm, v);
	}
	public String getBodyBase64Digest(String algorithm, boolean rfc3230) throws UtilsException{
		return getBodyDigest(algorithm, DigestEncoding.BASE64, rfc3230);
	}
	
	public String getBodyHexDigest(String algorithm) throws UtilsException{
		return getBodyDigest(algorithm, DigestEncoding.HEX, false);
	}
	public String getBodyHexDigest(String algorithm, String rfc3230) throws UtilsException{
		// // per invocazioni dinamiche
		boolean v = false;
		try {
			v = Boolean.valueOf(rfc3230);
		}catch(Exception e) {
			throw new UtilsException("Uncorrect boolean value '"+rfc3230+"': "+e.getMessage(),e);
		}
		return getBodyHexDigest(algorithm, v);
	}
	public String getBodyHexDigest(String algorithm, boolean rfc3230) throws UtilsException{
		return getBodyDigest(algorithm, DigestEncoding.HEX, rfc3230);
	}
	
	public String getBodyDigest(String algorithm, String digestEncodingParam) throws UtilsException{
		return getBodyDigest(algorithm, digestEncodingParam, false);
	}
	public String getBodyDigest(String algorithm, DigestEncoding digestEncoding) throws UtilsException{
		return getBodyDigest(algorithm, digestEncoding, false);
	}
	public String getBodyDigest(String algorithm, String digestEncodingParam, String rfc3230) throws UtilsException{
		// // per invocazioni dinamiche
		boolean v = false;
		try {
			v = Boolean.valueOf(rfc3230);
		}catch(Exception e) {
			throw new UtilsException("Uncorrect boolean value '"+rfc3230+"': "+e.getMessage(),e);
		}
		return getBodyDigest(algorithm, digestEncodingParam, v);
	}
	public String getBodyDigest(String algorithm, String digestEncodingParam,
			boolean rfc3230 // aggiunge prefisso algoritmo=
			) throws UtilsException{
		DigestEncoding digestEncoding = null;
		try {
			digestEncoding = DigestEncoding.valueOf(digestEncodingParam);
		}catch(Throwable t) {
			throw new UtilsException("DigestEncoding '"+digestEncodingParam+"' unsupported");
		}
		return getBodyDigest(algorithm, digestEncoding, rfc3230);
	}
	public String getBodyDigest(String algorithm, DigestEncoding digestEncoding, String rfc3230) throws UtilsException{
		// // per invocazioni dinamiche
		boolean v = false;
		try {
			v = Boolean.valueOf(rfc3230);
		}catch(Exception e) {
			throw new UtilsException("Uncorrect boolean value '"+rfc3230+"': "+e.getMessage(),e);
		}
		return getBodyDigest(algorithm, digestEncoding, v);
	}
	public String getBodyDigest(String algorithm, DigestEncoding digestEncoding,
			boolean rfc3230 // aggiunge prefisso algoritmo=
			) throws UtilsException{
		byte[] content = getBody();
		if(content==null) {
			throw new UtilsException("Content null");
		}
		return org.openspcoop2.utils.digest.DigestUtils.getDigestValue(content, algorithm, digestEncoding, rfc3230);
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
	
	public DumpAttachment getAttachmentByIndex(String index) throws UtilsException {
		// // per invocazioni dinamiche
		int pos = -1;
		try {
			pos = Integer.valueOf(index);
			if(pos<0) {
				throw new Exception("negative index");
			}
		}catch(Exception e) {
			throw new UtilsException("Uncorrect position '"+pos+"': "+e.getMessage(),e);
		}
		return getAttachment(pos);
	}
	public DumpAttachment getAttachment(int index) {
		if(this.attachments==null || this.attachments.isEmpty()) {
			return null;
		}
		if(index>=this.attachments.size()) {
			return null;
		}
		return this.attachments.get(index);
	}
	public DumpAttachment getAttachmentById(String id) {
		return getAttachment(id);
	}
	public DumpAttachment getAttachmentByContentId(String id) {
		return getAttachment(id);
	}
	public DumpAttachment getAttachment(String id) {
		if(this.attachments==null || this.attachments.isEmpty()) {
			return null;
		}
		for (DumpAttachment dumpAttachment : this.attachments) {
			if(id.equals(dumpAttachment.getContentId())) {
				return dumpAttachment;
			}
		}
		return null;
	}
	
	public DumpMessaggioMultipartInfo getMultipartInfoBody() {
		return this.multipartInfoBody;
	}

	public void setMultipartInfoBody(DumpMessaggioMultipartInfo multipartInfoBody) {
		this.multipartInfoBody = multipartInfoBody;
	}


	@Deprecated
	public Map<String, String> getHeaders() {
		return TransportUtils.convertToMapSingleValue(this.headers);
	}
	public Map<String, List<String>> getHeadersValues() {
		return this.headers;
	}

	@Deprecated
	public void setHeaders(Map<String, String> headers) {
		this.headers = TransportUtils.convertToMapListValues(headers);
	}
	public void setHeadersValues(Map<String, List<String>> headers) {
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
			StringBuilder out = new StringBuilder();
						
			if(config.isDumpHeaders()) {
				out.append("------ Header di trasporto ------\n");
				if(this.getHeadersValues()!=null && this.getHeadersValues().size()>0){
					Iterator<?> it = this.getHeadersValues().keySet().iterator();
					while (it.hasNext()) {
						String key = (String) it.next();
						if(key!=null){
							List<String> values = this.getHeadersValues().get(key);
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
					if(config.isDumpMultipartHeaders() &&  this.getMultipartInfoBody().getHeadersValues()!=null &&
							this.getMultipartInfoBody().getHeadersValues().size()>0) {
						Iterator<?> itM = this.getMultipartInfoBody().getHeadersValues().keySet().iterator();
				    	while(itM.hasNext()) {
				    		Object keyO = itM.next();
				    		if(keyO instanceof String) {
				    			String key = (String) keyO;
				    			if(HttpConstants.CONTENT_ID.equalsIgnoreCase(key) ||
				    					HttpConstants.CONTENT_LOCATION.equalsIgnoreCase(key) ||
				    					HttpConstants.CONTENT_TYPE.equalsIgnoreCase(key)) {
				    				continue;
				    			}
				    			List<String> values = this.getMultipartInfoBody().getHeadersValues().get(key);
								if(values!=null && !values.isEmpty()) {
									for (String value : values) {
										out.append("- "+key+": "+value+"\n");
									}
								}
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
					if(config.isDumpMultipartHeaders() && ap.getHeadersValues()!=null &&
							ap.getHeadersValues().size()>0) {
						Iterator<?> itM = ap.getHeadersValues().keySet().iterator();
				    	while(itM.hasNext()) {
				    		Object keyO = itM.next();
				    		if(keyO instanceof String) {
				    			String key = (String) keyO;
				    			if(HttpConstants.CONTENT_ID.equalsIgnoreCase(key) ||
				    					HttpConstants.CONTENT_LOCATION.equalsIgnoreCase(key) ||
				    					HttpConstants.CONTENT_TYPE.equalsIgnoreCase(key)) {
				    				continue;
				    			}
				    			List<String> values = ap.getHeadersValues().get(key);
								if(values!=null && !values.isEmpty()) {
									for (String value : values) {
										out.append("- "+key+": "+value+"\n");
									}
								}
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
