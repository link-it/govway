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


package org.openspcoop2.message.utils;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.digest.DigestEncoding;
import org.openspcoop2.utils.transport.TransportUtils;

/**
 * Allegato
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DumpAttachment implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String contentId;
	private String contentLocation;
	private String contentType;
	private Map<String, List<String>> headers = new HashMap<>();
	
	private String errorContentNotSerializable;
	private transient ByteArrayOutputStream content;
	private StringBuilder printableContent;
	
	public String getContentId() {
		return this.contentId;
	}
	public void setContentId(String contentId) {
		this.contentId = contentId;
	}
	public String getContentLocation() {
		return this.contentLocation;
	}
	public void setContentLocation(String contentLocation) {
		this.contentLocation = contentLocation;
	}
	public String getContentType() {
		return this.contentType;
	}
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	public String getErrorContentNotSerializable() {
		return this.errorContentNotSerializable;
	}
	public void setErrorContentNotSerializable(String errorContentNotSerializable) {
		this.errorContentNotSerializable = errorContentNotSerializable;
	}
	public long getContentLength() {
		if(this.content!=null) {
			return this.content.size();
		}
		return 0;
	}
	public byte[] getContent() {
		if(this.content!=null) {
			return this.content.toByteArray();
		}
		return null;
	}
	private static String getTestoVisualizzabile(byte [] b,StringBuilder stringBuffer) {
		 try{
			 // 1024 = 1K
			 // Visualizzo al massimo 50K (per i log)
			 int max = 50 * 1024;
			 stringBuffer.append(org.openspcoop2.utils.Utilities.convertToPrintableText(b, max));
			 return null;	
		 }catch(Exception e){
			 return e.getMessage();
		 }

	 }
	public String getContentAsString(boolean ifPrintableContent) {
		if(this.content!=null) {
			if(ifPrintableContent) {
				if(this.printableContent!=null) {
					return this.printableContent.toString();
				}
				else {
					this.printableContent = new StringBuilder();
					String errore = getTestoVisualizzabile(this.content.toByteArray(),this.printableContent);
					if(errore!=null) {
						this.printableContent = new StringBuilder();
						this.printableContent.append(errore);
					}
					return this.printableContent.toString();
				}
			}
			else {
				return this.content.toString();
			}
		}
		return null;
	}
	public void setContent(ByteArrayOutputStream content) {
		this.content = content;
	}
	
	public String getContentBase64Digest(String algorithm) throws UtilsException{
		return getContentDigest(algorithm, DigestEncoding.BASE64, false);
	}
	public String getContentBase64Digest(String algorithm, String rfc3230) throws UtilsException{
		// // per invocazioni dinamiche
		boolean v = false;
		try {
			v = Boolean.valueOf(rfc3230);
		}catch(Exception e) {
			throw new UtilsException("Uncorrect boolean value '"+rfc3230+"': "+e.getMessage(),e);
		}
		return getContentBase64Digest(algorithm, v);
	}
	public String getContentBase64Digest(String algorithm, boolean rfc3230) throws UtilsException{
		return getContentDigest(algorithm, DigestEncoding.BASE64, rfc3230);
	}
	
	public String getContentHexDigest(String algorithm) throws UtilsException{
		return getContentDigest(algorithm, DigestEncoding.HEX, false);
	}
	public String getContentHexDigest(String algorithm, String rfc3230) throws UtilsException{
		// // per invocazioni dinamiche
		boolean v = false;
		try {
			v = Boolean.valueOf(rfc3230);
		}catch(Exception e) {
			throw new UtilsException("Uncorrect boolean value '"+rfc3230+"': "+e.getMessage(),e);
		}
		return getContentHexDigest(algorithm, v);
	}
	public String getContentHexDigest(String algorithm, boolean rfc3230) throws UtilsException{
		return getContentDigest(algorithm, DigestEncoding.HEX, rfc3230);
	}
	
	public String getContentDigest(String algorithm, String digestEncodingParam) throws UtilsException{
		return getContentDigest(algorithm, digestEncodingParam, false);
	}
	public String getContentDigest(String algorithm, DigestEncoding digestEncoding) throws UtilsException{
		return getContentDigest(algorithm, digestEncoding, false);
	}
	public String getContentDigest(String algorithm, String digestEncodingParam, String rfc3230) throws UtilsException{
		// // per invocazioni dinamiche
		boolean v = false;
		try {
			v = Boolean.valueOf(rfc3230);
		}catch(Exception e) {
			throw new UtilsException("Uncorrect boolean value '"+rfc3230+"': "+e.getMessage(),e);
		}
		return getContentDigest(algorithm, digestEncodingParam, v);
	}
	public String getContentDigest(String algorithm, String digestEncodingParam,
			boolean rfc3230 // aggiunge prefisso algoritmo=
			) throws UtilsException{
		DigestEncoding digestEncoding = null;
		try {
			digestEncoding = DigestEncoding.valueOf(digestEncodingParam);
		}catch(Throwable t) {
			throw new UtilsException("DigestEncoding '"+digestEncodingParam+"' unsupported");
		}
		return getContentDigest(algorithm, digestEncoding, rfc3230);
	}
	public String getContentDigest(String algorithm, DigestEncoding digestEncoding, String rfc3230) throws UtilsException{
		// // per invocazioni dinamiche
		boolean v = false;
		try {
			v = Boolean.valueOf(rfc3230);
		}catch(Exception e) {
			throw new UtilsException("Uncorrect boolean value '"+rfc3230+"': "+e.getMessage(),e);
		}
		return getContentDigest(algorithm, digestEncoding, v);
	}
	public String getContentDigest(String algorithm, DigestEncoding digestEncoding,
			boolean rfc3230 // aggiunge prefisso algoritmo=
			) throws UtilsException{
		byte[] content = getContent();
		if(content==null) {
			throw new UtilsException("Content null");
		}
		return org.openspcoop2.utils.digest.DigestUtils.getDigestValue(content, algorithm, digestEncoding, rfc3230);
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
}
