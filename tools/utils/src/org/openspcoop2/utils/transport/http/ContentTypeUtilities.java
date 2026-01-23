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
package org.openspcoop2.utils.transport.http;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

//import jakarta.mail.internet.ContentType;
//import jakarta.mail.internet.ParameterList;
import com.sun.xml.messaging.saaj.packaging.mime.internet.ContentType;
import com.sun.xml.messaging.saaj.packaging.mime.internet.ParameterList;

import jakarta.xml.soap.SOAPException;

import org.apache.commons.lang3.StringUtils;

import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.UtilsRuntimeException;
import org.openspcoop2.utils.mime.MultipartUtils;
import org.openspcoop2.utils.regexp.RegExpNotFoundException;
import org.openspcoop2.utils.regexp.RegularExpressionEngine;
import org.slf4j.Logger;



/**
 * ContentTypeUtilities
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ContentTypeUtilities {
	
	private ContentTypeUtilities() {}
	
	
	// Utilities generiche
	
	public static void validateContentType(String ct) throws UtilsException{
		try {
			if(ct!=null && !"".equals(ct)) {
				
				(new jakarta.mail.internet.ContentType(ct)).getBaseType(); // uso jakarta.mail.per validare, restituisce un errore migliore
				
				if(ContentTypeUtilities.isMultipartRelated(ct)){
					String internal = ContentTypeUtilities.getInternalMultipartContentType(ct);
					if(internal!=null){
						ContentTypeUtilities.isMtom(internal);
					}
				}
			}
		}catch(Throwable e) {
			String msgError = e.getMessage();
			if(msgError==null || "".equals(msgError) || "null".equals(msgError)) {
				msgError = Utilities.getInnerNotEmptyMessageException(e).getMessage();
			}
			if(msgError==null || "".equals(msgError) || "null".equals(msgError)) {
				msgError = "Parsing failed";
			}
			throw new UtilsException(msgError,e);
		}
	}
	
	public static String buildContentType(String baseType,Map<String, String> parameters) {
		try{
			ContentType cType = new ContentType(baseType);
			if(parameters!=null && parameters.size()>0){
				Iterator<String> itP = parameters.keySet().iterator();
				while (itP.hasNext()) {
					String parameterName = itP.next();
					String parameterValue = parameters.get(parameterName);
					if(cType.getParameterList()==null){
						cType.setParameterList(new ParameterList());
					}
					cType.getParameterList().remove(parameterName);
					cType.getParameterList().set(parameterName, parameterValue);
				}
				
			}
			
			/**
			 * //import jakarta.mail.internet.ContentType;
				//import jakarta.mail.internet.ParameterList;

				import com.sun.xml.messaging.saaj.packaging.mime.internet.ContentType;
				import com.sun.xml.messaging.saaj.packaging.mime.internet.ParameterList;
				
				Utilizzo la versione saaj poiche il toString di jakarta.mail.internet.ContentType in presenza di un valore con ':' non funziona correttamente e genera valori action*0 e action*1
			 * 
			 * */
			
			String ct = cType.toString(); // il toString in presenza di action con valore http://... non funziona correttamente e genera valori action*0 e action*1
			
			// Reimplementare il toString non basta poiche' i ':' fanno schiantare un successivo parser del jakarta.mail.internet.ContentType
			/**
//			StringBuilder ctBufferParam = new StringBuilder();
//			ParameterList pList = cType.getParameterList();
//			if(pList!=null && pList.size()>0) {
//				java.util.Enumeration<String> en = pList.getNames();
//				while (en.hasMoreElements()) {
//					String name = (String) en.nextElement();
//					ctBufferParam.append("; ");
//					ctBufferParam.append(name).append("=").append(pList.get(name));
//				}
//			}
//			String ct = cType.getBaseType();
//			if(ctBufferParam.length()>0) {
//				ct = ct + ctBufferParam.toString();
//			}
			 */
			ct = normalizeToRfc7230(ct);			
			ct = ct.trim();
			return ct;
		}catch(Exception e){
			throw new UtilsRuntimeException("Error during buildContentType: "+e.getMessage(), e);
		}
	}
	
	public static String normalizeToRfc7230(String ct) {
		// Line folding of headers has been deprecated in the latest HTTP RFC:
		// http://tools.ietf.org/html/rfc7230#section-3.2.4
		while (ct.contains("\r\n")) {
			ct = ct.replace("\r\n", " ");
		}
		while (ct.contains("\r")) {
			ct = ct.replace("\r", " ");
		}
		while (ct.contains("\n")) {
			ct = ct.replace("\n", " ");
		}
		while (ct.contains("\t")) {
			ct = ct.replace("\t", " ");
		}
		return ct.trim();
	}
	
	public static String readBaseTypeFromContentType(String cType) throws UtilsException {
		try{
			ContentType contentType = new ContentType(cType);
			return contentType.getBaseType();
		} catch (Exception e) {
			throw new UtilsException(e.getMessage(),e);
		}
	}
	public static String readPrimaryTypeFromContentType(String cType) throws UtilsException {
		try{
			ContentType contentType = new ContentType(cType);
			return contentType.getPrimaryType();
		} catch (Exception e) {
			throw new UtilsException(e.getMessage(),e);
		}
	}
	public static String readSubTypeFromContentType(String cType) throws UtilsException {
		try{
			ContentType contentType = new ContentType(cType);
			return contentType.getSubType();
		} catch (Exception e) {
			throw new UtilsException(e.getMessage(),e);
		}
	}
	
	public static String readCharsetFromContentType(String cType) throws UtilsException {
		try{
			ContentType contentType = new ContentType(cType);
			String charsetParam = contentType.getParameter(HttpConstants.CONTENT_TYPE_PARAMETER_CHARSET); 
			if (charsetParam != null) {
				return charsetParam.trim();
			}
			return null;
		} catch (Exception e) {
			throw new UtilsException(e.getMessage(),e);
		}
	}
	
	
	
	// match
	
	public static boolean isMatch(Logger logNullable, String contentTypeParam, String contentTypeAtteso) throws UtilsException {
		List<String> l = new ArrayList<>();
		l.add(contentTypeAtteso);
		return isMatch(logNullable, contentTypeParam, l);
	}
	public static boolean isMatch(Logger logNullable, String contentTypeParam, List<String> contentTypeAttesi) throws UtilsException {
		
		if(contentTypeAttesi==null || contentTypeAttesi.isEmpty()) {
			return true;
		}
		
		String baseTypeHttp = contentTypeParam!=null && StringUtils.isNotEmpty(contentTypeParam) ? ContentTypeUtilities.readBaseTypeFromContentType(contentTypeParam) : null;
		if(baseTypeHttp!=null) {
			baseTypeHttp = baseTypeHttp.toLowerCase(); // case insensitive
		}
		
		boolean found = false;
		for (String checkContentTypeOrig : contentTypeAttesi) {
			String checkContentType = checkContentTypeOrig.toLowerCase(); // case insensitive
			if("empty".equals(checkContentType)){
				if(baseTypeHttp==null || "".equals(baseTypeHttp)) {
					found = true;
					break;
				}
			}
			else {
				if(baseTypeHttp==null) {
					continue;
				}
				if(checkContentType==null || "".equals(checkContentType) ||
						!checkContentType.contains("/") ||
						checkContentType.startsWith("/") ||
						checkContentType.endsWith("/")) {
					throw new UtilsException("Configurazione errata, content type indicato ("+checkContentType+") possiede un formato non corretto (atteso: type/subtype)");
				}
				String [] ctVerifica = checkContentType.split("/");
				String contentTypeEscaped = null;
				if(ctVerifica!=null && ctVerifica.length==2) {
					StringBuilder bf = new StringBuilder();
					String part1 = ctVerifica[0].trim();
					if("*".equals(part1)) {
						bf.append("(.+)");
					}
					else {
						// escape special char
						part1 = part1.replace("+", "\\+");
						bf.append(part1);
					}
					bf.append("/");
					String part2 = ctVerifica[1].trim();
					if("*".equals(part2)) {
						bf.append("(.+)");
					}
					else if(part2.startsWith("*")) {
						bf.append("(.+)");
						String sub = part2.substring(1);
						// escape special char
						sub = sub.replace("+", "\\+");
						bf.append(sub);
					}
					else {
						// escape special char
						part2 = part2.replace("+", "\\+");
						bf.append(part2);
					}
					contentTypeEscaped = bf.toString();
				}
				boolean isMatchEscaped = false; // gestisce le espressioni type/* e */*+xml
				boolean isMatchRegExp = false; // gestisce le espressioni regexpType/regexpSubType
				if(contentTypeEscaped!=null) {
					try {
						isMatchEscaped = RegularExpressionEngine.isMatch(baseTypeHttp, contentTypeEscaped);
					}catch(RegExpNotFoundException notFound) {
						// ignore
					}catch(Exception e) {
						throw new UtilsException(e.getMessage(),e);
					}
				}
				if(!isMatchEscaped) {
					try {
						isMatchRegExp = RegularExpressionEngine.isMatch(baseTypeHttp, checkContentType);
					}catch(RegExpNotFoundException notFound) {
						// ignore
					}catch(Exception e) {
						if(contentTypeEscaped==null) {
							throw new UtilsException(e.getMessage(),e);
						}
						else {
							// ignore
							// per evitare errori tipo:
							//org.openspcoop2.utils.UtilsException: Validazione del pattern indicato [*/*son] fallita: Dangling meta character '*' near index 0
							//                                                                        */*son
							//                                                                        ^
							if(logNullable!=null) {
								logNullable.debug("isMatch failed: "+e.getMessage(),e);
							}
						}
					}
				}
				if(isMatchEscaped || isMatchRegExp) {
					found = true;
					break;
				}
			}
			
		}
		return found;
	}
	
	
	
	
	// Utilities Multipart
	
	public static boolean isMultipartType(String cType) throws UtilsException {
		try{
			ContentType contentType = new ContentType(cType);
			String baseType = contentType.getBaseType();
			if(baseType!=null) {
				baseType = baseType.toLowerCase();
			}
			if(baseType!=null && baseType.startsWith(HttpConstants.CONTENT_TYPE_MULTIPART_TYPE+"/")){
				return true;
			}
			return false;
			
		} catch (Exception e) {
			throw new UtilsException(e.getMessage(),e);
		}
	}
	
	@Deprecated
	public static boolean isMultipart(String cType) throws UtilsException {
		return isMultipartRelated(cType);
	}
	public static boolean isMultipartRelated(String cType) throws UtilsException {
		return isMultipartEngine(cType, HttpConstants.CONTENT_TYPE_MULTIPART_RELATED);
	}
	public static boolean isMultipartMixed(String cType) throws UtilsException {
		return isMultipartEngine(cType, HttpConstants.CONTENT_TYPE_MULTIPART_MIXED);
	}
	public static boolean isMultipartAlternative(String cType) throws UtilsException {
		return isMultipartEngine(cType, HttpConstants.CONTENT_TYPE_MULTIPART_ALTERNATIVE);
	}
	public static boolean isMultipartFormData(String cType) throws UtilsException {
		return isMultipartEngine(cType, HttpConstants.CONTENT_TYPE_MULTIPART_FORM_DATA);
	}
	private static boolean isMultipartEngine(String cType, String expected) throws UtilsException {
		try{
			ContentType contentType = new ContentType(cType);
			String baseType = contentType.getBaseType();
			if(baseType!=null) {
				baseType = baseType.toLowerCase();
			}
			return baseType!=null && baseType.equals(expected);
			
		} catch (Exception e) {
			throw new UtilsException(e.getMessage(),e);
		}
	}
	
	public static boolean isMultipartContentType(String cType) throws UtilsException {
		try{
			ContentType contentType = new ContentType(cType);
			String baseType = contentType.getBaseType();
			if(baseType!=null) {
				baseType = baseType.toLowerCase();
			}
			if(baseType!=null && baseType.startsWith( (HttpConstants.CONTENT_TYPE_MULTIPART_TYPE+"/")) ){
				return true;
			}
			return false;
			
		} catch (Exception e) {
			throw new UtilsException(e.getMessage(),e);
		}
	}
	
	public static String getInternalMultipartContentType(String cType) throws UtilsException {
		try{
			ContentType contentType = new ContentType(cType);
			String baseType = contentType.getBaseType();
			if(baseType!=null) {
				baseType = baseType.toLowerCase();
			}
			String internalContentType = null;
			boolean mtom = false;
			if(baseType == null) {
				/**internalContentType = null;*/
			}
			else if(baseType.equals(HttpConstants.CONTENT_TYPE_MULTIPART_RELATED)){
				String typeParam = contentType.getParameter(HttpConstants.CONTENT_TYPE_MULTIPART_PARAMETER_TYPE); 
				if (typeParam != null) {
					internalContentType = typeParam.toLowerCase();
					if(HttpConstants.CONTENT_TYPE_APPLICATION_XOP_XML.equals(internalContentType)){
						mtom = true;
					}
				} 
			}
			else {
				internalContentType = baseType;
			}
			
			if(mtom) {
				internalContentType = readInternalMultipartMtomContentType(contentType);
			}
			
			return internalContentType;
		} catch (Exception e) {
			throw new UtilsException(e.getMessage(),e);
		}
	}
	
	@Deprecated
	public static String buildMultipartContentType(byte [] message, String type) throws UtilsException{
		return buildMultipartRelatedContentType(message, type);
	}
	public static String buildMultipartRelatedContentType(byte [] message, String type) throws UtilsException{
		return buildMultipartContentType(HttpConstants.CONTENT_TYPE_MULTIPART_RELATED_SUBTYPE, message, type);
	}
	public static String buildMultipartContentType(String subtype, byte [] message, String type) throws UtilsException{
		if(MultipartUtils.messageWithAttachment(message)){
			String idfirst  = MultipartUtils.firstContentID(message);
			return buildMultipartContentType(subtype, message, type, idfirst);
		}
		throw new UtilsException("Messaggio non contiene una struttura mime");
	}
	@Deprecated
	public static String buildMultipartContentType(byte [] message, String type, String id) throws UtilsException{
		return buildMultipartRelatedContentType(message, type, id);
	}
	public static String buildMultipartRelatedContentType(byte [] message, String type, String id) throws UtilsException{
		return buildMultipartContentType(HttpConstants.CONTENT_TYPE_MULTIPART_RELATED_SUBTYPE, message, type, id);
	}
	public static String buildMultipartContentType(String subtype, byte [] message, String type, String id) throws UtilsException{
		if(MultipartUtils.messageWithAttachment(message)){
						
			String boundary = MultipartUtils.findBoundary(message);
			if(boundary==null){
				throw new UtilsException("Errore avvenuto durante la lettura del boundary associato al multipart message.");
			}
			StringBuilder bf = new StringBuilder();
			bf.append(HttpConstants.CONTENT_TYPE_MULTIPART_TYPE+"/"+subtype);
			if(type!=null){
				bf.append("; ").append(HttpConstants.CONTENT_TYPE_MULTIPART_PARAMETER_TYPE).append("=\"").append(type).append("\"");
			}
			if(id!=null){
				bf.append("; ").append(HttpConstants.CONTENT_TYPE_MULTIPART_PARAMETER_START).append("=\"").append(id).append("\"");
			}
			bf.append("; ").append(HttpConstants.CONTENT_TYPE_MULTIPART_PARAMETER_BOUNDARY).append("=\"").append(boundary.substring(2,boundary.length())).append("\"");
			
			return bf.toString();
		}
		throw new UtilsException("Messaggio non contiene una struttura mime");
	}
	
	
	
	
	// Utilities MTOM
	
	public static boolean isMtom(String cType) throws UtilsException{
		try{
			ContentType contentType = new ContentType(cType);
			String baseType = contentType.getBaseType();
			if(baseType!=null) {
				baseType = baseType.toLowerCase();
			}
			boolean mtom = false;
			if(baseType!=null && baseType.equals(HttpConstants.CONTENT_TYPE_MULTIPART_RELATED)){
				String typeParam = contentType.getParameter(HttpConstants.CONTENT_TYPE_MULTIPART_PARAMETER_TYPE); 
				if (typeParam == null) {
					throw new SOAPException("Missing '"+HttpConstants.CONTENT_TYPE_MULTIPART_PARAMETER_TYPE+"' parameter in "+HttpConstants.CONTENT_TYPE_MULTIPART_RELATED);
				} else {
					String soapContentType = typeParam.toLowerCase();
					if(HttpConstants.CONTENT_TYPE_APPLICATION_XOP_XML.equals(soapContentType)){
						mtom = true;
					}
				} 
			}
			return mtom;
			
		} catch (Exception e) {
			throw new UtilsException(e.getMessage(),e);
		}
	}
	
	public static String readInternalMultipartMtomContentType(String contentType) throws UtilsException{
		try{
			return readInternalMultipartMtomContentType(new ContentType(contentType));
		}
		catch(UtilsException e){
			throw e;
		}
		catch(Exception e){
			throw new UtilsException(e.getMessage(),e);
		}
	}
	public static String readInternalMultipartMtomContentType(ContentType contentType) throws UtilsException{
		try{
			if(contentType==null){
				throw new UtilsException("ContentType non fornito");
			}
			
			// baseType
			if(contentType.getBaseType()==null){
				throw new UtilsException("ContentType.baseType non definito");
			}
			if(!HttpConstants.CONTENT_TYPE_MULTIPART_RELATED.equalsIgnoreCase(contentType.getBaseType())){
				throw new UtilsException("ContentType.baseType ["+contentType.getBaseType()+
						"] differente da quello atteso per un messaggio MTOM/XOP ["+HttpConstants.CONTENT_TYPE_MULTIPART_RELATED+"]");
			}
			if(contentType.getParameterList()==null || contentType.getParameterList().size()<=0){
				throw new UtilsException("ContentType non conforme a quanto definito nella specifica MTOM/XOP (non sono presenti parametri)");
			}
			
			// type
			String type = contentType.getParameter(HttpConstants.CONTENT_TYPE_MULTIPART_PARAMETER_TYPE);
			if(type==null){
				throw new UtilsException("ContentType non conforme a quanto definito nella specifica MTOM/XOP (Parametro '"+
						HttpConstants.CONTENT_TYPE_MULTIPART_PARAMETER_TYPE+"' non presente)");
			}
			if(!HttpConstants.CONTENT_TYPE_APPLICATION_XOP_XML.equalsIgnoreCase(type)){
				throw new UtilsException("ContentType.parameters."+HttpConstants.CONTENT_TYPE_MULTIPART_PARAMETER_TYPE+" ["+type+
						"] differente da quello atteso per un messaggio MTOM/XOP ["+HttpConstants.CONTENT_TYPE_APPLICATION_XOP_XML+"]");
			}
			
			// startInfo
			String startInfo = contentType.getParameter(HttpConstants.CONTENT_TYPE_MULTIPART_PARAMETER_START_INFO);
			if(startInfo==null){
				throw new UtilsException("ContentType non conforme a quanto definito nella specifica MTOM/XOP (Parametro '"+
						HttpConstants.CONTENT_TYPE_MULTIPART_PARAMETER_START_INFO+"' non presente)");
			}
			// startInfo puo' contenere il ';'
			return readBaseTypeFromContentType(startInfo);
			
		}
		catch(UtilsException e){
			throw e;
		}
		catch(Exception e){
			throw new UtilsException(e.getMessage(),e);
		}
	}


	
	public static String readMultipartBoundaryFromContentType(String cType) throws UtilsException {
		try{
			ContentType contentType = new ContentType(cType);
			String boundaryParam = contentType.getParameter(HttpConstants.CONTENT_TYPE_MULTIPART_PARAMETER_BOUNDARY); 
			if (boundaryParam != null) {
				return boundaryParam.trim();
			}
			return null;
		} catch (Exception e) {
			throw new UtilsException(e.getMessage(),e);
		}
	}
	
	
	
	
	// equals
	
	public static boolean equals(List<String> hdrFound, String hdrExpected) {
		if(hdrFound!=null && !hdrFound.isEmpty()) {
			for (String h : hdrFound) {
				if(equals(h, hdrExpected)) {
					return true;
				}
			}
		}
		return false;
	}
	public static boolean equals(String hdrFound, String hdrExpected) {
		
		/**
		 * RFC 2045 - Section 5.1 (MIME)
		   La specifica RFC 2045, sezione 5.1, che definisce la grammatica ABNF per i media types:
			Content-Type := type "/" subtype *(";" parameter)
			parameter := attribute "=" value
			E nella stessa sezione specifica:
			"White space characters MUST NOT be generated between a token and the ";" that follows it, but such white space is permitted in received headers and MUST be discarded before the Content-Type value is interpreted."
			
			Per il contesto HTTP specifico, RFC 7231 (HTTP/1.1 Semantics) sezione 3.1.1.1 definisce:
			media-type = type "/" subtype *( OWS ";" OWS parameter )
			OWS = *( SP / HTAB )  ; optional whitespace
			Dove OWS (Optional WhiteSpace) indica esplicitamente che gli spazi sono opzionali attorno al punto e virgola.
			
			La versione più aggiornata è RFC 9110, sezione 8.3.1:
			media-type = type "/" subtype parameters
			parameters = *( OWS ";" OWS [ parameter ] )
			Conferma lo stesso comportamento.
			
			Conclusione: Gli spazi attorno al ; sono esplicitamente permessi e devono essere ignorati durante il parsing secondo RFC 2045, RFC 7231 e RFC 9110.
		 */
		/**
		 * Non gestisce RFC 2045 - Section 5.1: The type, subtype, and parameter names are not case sensitive.
		 * I parameter name differenti li rileva diversi 
		 *"
		 * org.springframework.http.MediaType mtHdrFound = org.springframework.http.MediaType.parseMediaType(hdrFound);
		org.springframework.http.MediaType mtHdrExpected = org.springframework.http.MediaType.parseMediaType(hdrExpected);*/
		
		/**
		 * RFC 2045 specifica:
		  "Parameter values might or might not be case-sensitive, depending on the semantics of the parameter name."
		 Per il parametro charset: Case-Insensitive
		RFC 2978 (IANA Charset Registration) e RFC 2046 specificano che i valori di charset sono case-insensitive:
			charset=UTF-8 = charset=utf-8 = charset=Utf-8 sono equivalenti
		  Questo aspetto del valore del charset non viene gestito
		 * javax.ws.rs.core.MediaType mtHdrFound = javax.ws.rs.core.MediaType.valueOf(hdrFound);
		javax.ws.rs.core.MediaType mtHdrExpected = javax.ws.rs.core.MediaType.valueOf(hdrExpected);
		*/
		
		com.google.common.net.MediaType mtHdrFound = com.google.common.net.MediaType.parse(hdrFound);
		com.google.common.net.MediaType mtHdrExpected = com.google.common.net.MediaType.parse(hdrExpected);
		
		return mtHdrFound.equals(mtHdrExpected);
	}
}
