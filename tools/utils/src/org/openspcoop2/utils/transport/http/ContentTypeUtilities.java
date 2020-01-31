/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it). 
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

//import javax.mail.internet.ContentType;
//import javax.mail.internet.ParameterList;
import com.sun.xml.messaging.saaj.packaging.mime.internet.ContentType;
import com.sun.xml.messaging.saaj.packaging.mime.internet.ParameterList;

import javax.xml.soap.SOAPException;

import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.mime.MultipartUtils;
import org.openspcoop2.utils.regexp.RegularExpressionEngine;



/**
 * ContentTypeUtilities
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ContentTypeUtilities {
	
	
	
	// Utilities generiche
	
	public static String buildContentType(String baseType,Map<String, String> parameters) throws UtilsException{
		try{
			ContentType cType = new ContentType(baseType);
			if(parameters.size()>0){
				Iterator<String> itP = parameters.keySet().iterator();
				while (itP.hasNext()) {
					String parameterName = (String) itP.next();
					String parameterValue = parameters.get(parameterName);
					if(cType.getParameterList()==null){
						cType.setParameterList(new ParameterList());
					}
					cType.getParameterList().remove(parameterName);
					cType.getParameterList().set(parameterName, parameterValue);
				}
				
			}
			
			/*
			 * //import javax.mail.internet.ContentType;
				//import javax.mail.internet.ParameterList;

				import com.sun.xml.messaging.saaj.packaging.mime.internet.ContentType;
				import com.sun.xml.messaging.saaj.packaging.mime.internet.ParameterList;
				
				Utilizzo la versione saaj poiche il toString di javax.mail.internet.ContentType in presenza di un valore con ':' non funziona correttamente e genera valori action*0 e action*1
			 * 
			 * */
			
			String ct = cType.toString(); // il toString in presenza di action con valore http://... non funziona correttamente e genera valori action*0 e action*1
			
			// Reimplementare il toString non basta poiche' i ':' fanno schiantare un successivo parser del javax.mail.internet.ContentType
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
			ct = normalizeToRfc7230(ct);			
			ct = ct.trim();
			return ct;
		}catch(Exception e){
			throw new RuntimeException("Error during buildContentType: "+e.getMessage(), e);
		}
	}
	
	public static String normalizeToRfc7230(String ct) {
		// Line folding of headers has been deprecated in the latest HTTP RFC:
		// http://tools.ietf.org/html/rfc7230#section-3.2.4
		if(ct.contains("\r\n")) {
			while (ct.contains("\r\n")) {
				ct = ct.replace("\r\n", " ");
			}
		}
		if(ct.contains("\r")) {
			while (ct.contains("\r")) {
				ct = ct.replace("\r", " ");
			}
		}
		if(ct.contains("\n")) {
			while (ct.contains("\n")) {
				ct = ct.replace("\n", " ");
			}
		}
		if(ct.contains("\t")) {
			while (ct.contains("\t")) {
				ct = ct.replace("\t", " ");
			}
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
	
	public static boolean isMatch(String contentType, String contentTypeAtteso) throws Exception {
		List<String> l = new ArrayList<String>();
		l.add(contentTypeAtteso);
		return isMatch(contentType, l);
	}
	public static boolean isMatch(String contentType, List<String> contentTypeAttesi) throws Exception {
		
		if(contentTypeAttesi==null || contentTypeAttesi.size()<=0) {
			return true;
		}
		
		boolean found = false;
		for (String checkContentType : contentTypeAttesi) {
			if("empty".equals(checkContentType)){
				if(contentType==null || "".equals(contentType)) {
					found = true;
					break;
				}
			}
			else {
				if(contentType==null) {
					continue;
				}
				if(checkContentType==null || "".equals(checkContentType) ||
						checkContentType.contains("/")==false ||
						checkContentType.startsWith("/") ||
						checkContentType.endsWith("/")) {
					throw new Exception("Configurazione errata, content type indicato ("+checkContentType+") possiede un formato non corretto (atteso: type/subtype)");
				}
				String [] ctVerifica = checkContentType.split("/");
				if(ctVerifica!=null && ctVerifica.length==2) {
					StringBuilder bf = new StringBuilder();
					String part1 = ctVerifica[0].trim();
					if("*".equals(part1)) {
						bf.append("(.+)");
					}
					else {
						// escape special char
						part1 = part1.replaceAll("\\+", "\\\\+");
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
						sub = sub.replaceAll("\\+", "\\\\+");
						bf.append(sub);
					}
					else {
						// escape special char
						part2 = part2.replaceAll("\\+", "\\\\+");
						bf.append(part2);
					}
					checkContentType = bf.toString();
				}
				if(RegularExpressionEngine.isMatch(contentType, checkContentType)) {
					found = true;
					break;
				}
			}
			
		}
		return found;
	}
	
	
	
	
	// Utilities Multipart
	
	public static boolean isMultipart(String cType) throws UtilsException {
		try{
			ContentType contentType = new ContentType(cType);
			String baseType = contentType.getBaseType().toLowerCase(); 
			if(baseType!=null && baseType.equals(HttpConstants.CONTENT_TYPE_MULTIPART)){
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
			String baseType = contentType.getBaseType().toLowerCase(); 
			String internalContentType = null;
			if(baseType == null)
				internalContentType = null;
			boolean mtom = false;
			if(baseType.equals(HttpConstants.CONTENT_TYPE_MULTIPART)){
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
	
	public static String buildMultipartContentType(byte [] message, String type) throws UtilsException{
		if(MultipartUtils.messageWithAttachment(message)){
						
			String IDfirst  = MultipartUtils.firstContentID(message);
			String boundary = MultipartUtils.findBoundary(message);
			if(boundary==null){
				throw new UtilsException("Errore avvenuto durante la lettura del boundary associato al multipart message.");
			}
			StringBuilder bf = new StringBuilder();
			bf.append(HttpConstants.CONTENT_TYPE_MULTIPART);
			if(type!=null){
				bf.append("; ").append(HttpConstants.CONTENT_TYPE_MULTIPART_PARAMETER_TYPE).append("=\"").append(type).append("\"");
			}
			if(IDfirst!=null){
				bf.append("; ").append(HttpConstants.CONTENT_TYPE_MULTIPART_PARAMETER_START).append("=\"").append(IDfirst).append("\"");
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
			String baseType = contentType.getBaseType().toLowerCase(); 
			String soapContentType = null;
			if(baseType == null)
				soapContentType = null;
			boolean mtom = false;
			if(baseType.equals(HttpConstants.CONTENT_TYPE_MULTIPART)){
				String typeParam = contentType.getParameter(HttpConstants.CONTENT_TYPE_MULTIPART_PARAMETER_TYPE); 
				if (typeParam == null) {
					throw new SOAPException("Missing '"+HttpConstants.CONTENT_TYPE_MULTIPART_PARAMETER_TYPE+"' parameter in "+HttpConstants.CONTENT_TYPE_MULTIPART);
				} else {
					soapContentType = typeParam.toLowerCase();
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
			if(HttpConstants.CONTENT_TYPE_MULTIPART.equals(contentType.getBaseType().toLowerCase())==false){
				throw new UtilsException("ContentType.baseType ["+contentType.getBaseType()+
						"] differente da quello atteso per un messaggio MTOM/XOP ["+HttpConstants.CONTENT_TYPE_MULTIPART+"]");
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
			if(HttpConstants.CONTENT_TYPE_APPLICATION_XOP_XML.equals(type.toLowerCase())==false){
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

	
}
