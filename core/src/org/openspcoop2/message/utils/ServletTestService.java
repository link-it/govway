/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
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
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;

import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPFault;

import org.slf4j.Logger;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.OpenSPCoop2MessageFactory;
import org.openspcoop2.message.OpenSPCoop2SoapMessage;
import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.message.soap.SOAPFaultCode;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.mime.MimeTypes;
import org.openspcoop2.utils.mime.MultipartUtils;
import org.openspcoop2.utils.resources.FileSystemUtilities;
import org.openspcoop2.utils.transport.TransportUtils;
import org.openspcoop2.utils.transport.http.ContentTypeUtilities;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.openspcoop2.utils.transport.http.HttpServletTransportRequestContext;


/**
 * Server echo per test
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ServletTestService extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	protected Logger log = null;
	protected File repositoryResponseFiles = null;
	protected List<String> whitePropertiesList = null;
	protected boolean genericError = false;
	
	public ServletTestService(Logger log, File repositoryResponseFiles,List<String> whitePropertiesList,boolean genericError){
		this.log = log;
		this.repositoryResponseFiles = repositoryResponseFiles;
		this.whitePropertiesList = whitePropertiesList;
		this.genericError = genericError;
	}
	public ServletTestService(Logger log, File repositoryResponseFiles,boolean genericError){
		this(log, repositoryResponseFiles, null,genericError);
	}
	public ServletTestService(Logger log, File repositoryResponseFiles){
		this(log, repositoryResponseFiles, null, false);
	}
	public ServletTestService(Logger log){
		this(log, null, null, false);
	}
	
	private static String getParameter_checkWhiteList(HttpServletRequest request, List<String> whitePropertiesList, String parameter) {
		String value = request.getParameter(parameter);
		if(value!=null) {
			value = value.trim();
		}
		if(whitePropertiesList!=null) {
			if(whitePropertiesList.contains(parameter)==false) {
				return null;
			}
		}
		return value;
	}
	
	public static void checkHttpServletRequestParameter(HttpServletRequest request) throws ServletException{
		checkHttpServletRequestParameter(request, null);
	}
	private static void checkHttpServletRequestParameter(HttpServletRequest request, List<String> whitePropertiesList) throws ServletException{
		
		String checkEqualsHttpMethod = getParameter_checkWhiteList(request, whitePropertiesList, "checkEqualsHttpMethod");
		if(checkEqualsHttpMethod!=null){
			checkEqualsHttpMethod = checkEqualsHttpMethod.trim();
			if(checkEqualsHttpMethod.equals(request.getMethod())==false){
				throw new ServletException("Ricevuta una richiesta con metodo http ["+request.getMethod()+"] differente da quella attesa ["+checkEqualsHttpMethod+"]");
			}
		}
		
		String checkEqualsHttpHeader = getParameter_checkWhiteList(request, whitePropertiesList, "checkEqualsHttpHeader");
		if(checkEqualsHttpHeader!=null){
			checkEqualsHttpHeader = checkEqualsHttpHeader.trim();
			if(checkEqualsHttpHeader.contains(":")==false){
				throw new ServletException("Ricevuta una richiesta di verifica header non conforme (pattern nome:valore)");
			}
			String [] split = checkEqualsHttpHeader.split(":");
			if(split==null){
				throw new ServletException("Ricevuta una richiesta di verifica header non conforme (pattern nome:valore) (split null)");
			}
			if(split.length!=2){
				throw new ServletException("Ricevuta una richiesta di verifica header non conforme (pattern nome:valore) (split:"+split.length+")");
			}
			String key = split[0];
			String valore = split[1];
			
			String v = request.getHeader(key);
			if(v==null){
				v = request.getHeader(key.toLowerCase());
			}
			if(v==null){
				v = request.getHeader(key.toUpperCase());
			}
			if(v==null){
				throw new ServletException("Ricevuta una richiesta di verifica header ("+key+":"+valore+"). Header ["+key+"] non presente");
			}
			if(v.equals(valore)==false){
				throw new ServletException("Ricevuta una richiesta di verifica header ("+key+":"+valore+"). Valore ["+v+"] differente da quello atteso");
			}
		}
		
	}
	
	public void doEngine(HttpServletRequest req, HttpServletResponse res, boolean oneway, Properties headerRisposta)
	throws ServletException, IOException {

		
		
		try{
			
			checkHttpServletRequestParameter(req, this.whitePropertiesList);
			
			
			
			
			// Autenticazione Basic
			String basicUsername = getParameter_checkWhiteList(req, this.whitePropertiesList, "basicUsername");
			String basicPassword = getParameter_checkWhiteList(req, this.whitePropertiesList, "basicPassword");
			String basicWWWAuthenticateDomain = getParameter_checkWhiteList(req, this.whitePropertiesList, "basicDomain");
			if(basicUsername!=null && basicPassword!=null) {
				HttpServletTransportRequestContext rc = new HttpServletTransportRequestContext(req, this.log);
				if(rc.getCredential()==null ||
						(!basicUsername.equals(rc.getCredential().getUsername())) ||
						(!basicPassword.equals(rc.getCredential().getPassword())) 
						) {
					String msgError = "TestService: credenziali fornite errate user["+rc.getCredential().getUsername()+"]passw["+rc.getCredential().getPassword()+
							"], attese user["+basicUsername+"]passw["+basicPassword+"]";
					if(this.log!=null) {
						this.log.error(msgError);
					}else {
						System.out.println("ERRORE TestService: "+msgError);
					}
					if(basicWWWAuthenticateDomain!=null) {
						res.setHeader(HttpConstants.AUTHORIZATION_RESPONSE_WWW_AUTHENTICATE,
								HttpConstants.AUTHORIZATION_RESPONSE_WWW_AUTHENTICATE_BASIC_REALM_PREFIX+
								basicWWWAuthenticateDomain+
								HttpConstants.AUTHORIZATION_RESPONSE_WWW_AUTHENTICATE_BASIC_REALM_SUFFIX);
					}
					res.setStatus(401);
					res.getOutputStream().flush();
					res.getOutputStream().close();
					return;
				}
			}

			
			
			
			
			/* ----  Ricezione richiesta di un proxy delegato  ---- */
						
			// Chunked
			boolean chunked = false;
			String chunkedValue = null;
			if(req.getHeader("Transfer-Encoding")!=null){
				chunkedValue = req.getHeader("Transfer-Encoding");
			}else if(req.getHeader("transfer-encoding")!=null){
				chunkedValue = req.getHeader("Transfer-Encoding");
			}
			if(chunkedValue!=null){
				chunkedValue = chunkedValue.trim();
				if("chunked".equals(chunkedValue)){
					chunked = true;
				}
			}
			
			
			
			
			// opzioni redirect
			String redirectOptions = getParameter_checkWhiteList(req, this.whitePropertiesList, "redirect");
			boolean redirect = false;
			if(redirectOptions!=null){
				redirectOptions = redirectOptions.trim();
				if("true".equals(redirectOptions))
					redirect = true;
			}
			if(redirect) {
				Properties p = new Properties();
				
				Integer returnCode = 307;
				String returnCodeOpt = getParameter_checkWhiteList(req, this.whitePropertiesList, "redirectReturnCode");
				if(returnCodeOpt!=null) {
					returnCode = Integer.parseInt(returnCodeOpt.trim());
					p.put("redirectReturnCode", returnCode+"");
				}
				
				String protocol = "http";
				String protocolOpt = getParameter_checkWhiteList(req, this.whitePropertiesList, "redirectProtocol");
				if(protocolOpt!=null) {
					protocol = protocolOpt.trim();
					p.put("redirectProtocol", protocol);
				}
				
				String host = "localhost";
				String hostOpt = getParameter_checkWhiteList(req, this.whitePropertiesList, "redirectHost");
				if(hostOpt!=null) {
					host = hostOpt.trim();
					p.put("redirectHost", host);
				}
				
				String port = "8080";
				String portOpt = getParameter_checkWhiteList(req, this.whitePropertiesList, "redirectPort");
				if(portOpt!=null) {
					port = portOpt.trim();
					p.put("redirectPort", port);
				}
				
				String contesto = req.getRequestURI();
				String contestoOpt = getParameter_checkWhiteList(req, this.whitePropertiesList, "redirectContext");
				if(contestoOpt!=null) {
					contesto = contestoOpt.trim();
					if(contesto.startsWith("/")==false) {
						contesto = "/" + contesto;
					}
					p.put("redirectContext", contesto);
				}
				
				Integer maxHop = 1;
				String maxOpt = getParameter_checkWhiteList(req, this.whitePropertiesList, "redirectMaxHop");
				if(maxOpt!=null) {
					maxHop = Integer.parseInt(maxOpt.trim());
					p.put("redirectMaxHop", maxHop+"");
				}
				
				Integer hop = 1;
				String hopOpt = getParameter_checkWhiteList(req, this.whitePropertiesList, "redirectHop");
				if(hopOpt!=null) {
					hop = Integer.parseInt(hopOpt.trim());
					p.put("redirectHop", hop+"");
				}
				
				if(hop<maxHop) {
					p.remove("redirectHop");
					p.put("redirectHop", (hop+1)+"");
					p.put("redirect", "true");
				}
				else {
					// terminato redirect hop
					p.clear();
				}
				
				String redirectUrl = protocol+"://"+host+":"+port+contesto;
				redirectUrl = TransportUtils.buildLocationWithURLBasedParameter(p, redirectUrl, this.log);
				
				res.setHeader(HttpConstants.REDIRECT_LOCATION,redirectUrl);
			
				res.setStatus(returnCode);
				
				return;
			}
			
			
					

			// opzioni connettore
			String fault = getParameter_checkWhiteList(req, this.whitePropertiesList, "fault");
			String faultActor = "OpenSPCoopTrace";
			String faultSoapVersion = null;
			String faultNamespaceCode = "http://www.openspcoop2.org/example";
			String faultCode = "Server.OpenSPCoopExampleFault";
			String faultMessage = "Fault ritornato dalla servlet di trace, esempio di OpenSPCoop";
			if(fault!=null && fault.equalsIgnoreCase("true")){
				
				if(getParameter_checkWhiteList(req, this.whitePropertiesList, "faultActor")!=null)
					faultActor = getParameter_checkWhiteList(req, this.whitePropertiesList, "faultActor");
				
				if(getParameter_checkWhiteList(req, this.whitePropertiesList, "faultSoapVersion")!=null)
					faultSoapVersion = getParameter_checkWhiteList(req, this.whitePropertiesList, "faultSoapVersion");
				
				if(getParameter_checkWhiteList(req, this.whitePropertiesList, "faultCode")!=null)
					faultCode = getParameter_checkWhiteList(req, this.whitePropertiesList, "faultCode");
				
				if(getParameter_checkWhiteList(req, this.whitePropertiesList, "faultNamespaceCode")!=null)
					faultNamespaceCode = getParameter_checkWhiteList(req, this.whitePropertiesList, "faultNamespaceCode");
				
				if(getParameter_checkWhiteList(req, this.whitePropertiesList, "faultMessage")!=null)
					faultMessage = getParameter_checkWhiteList(req, this.whitePropertiesList, "faultMessage");
			}
			
			
			
			
			
			// opzioni tunnel SOAP
			String tunnelSoap = getParameter_checkWhiteList(req, this.whitePropertiesList, "OpenSPCoop2TunnelSOAP");
			boolean tunnel = false;
			if(tunnelSoap!=null){
				tunnelSoap = tunnelSoap.trim();
				if("true".equals(tunnelSoap))
					tunnel = true;
			}
			String tunnelSoapMimeType = getParameter_checkWhiteList(req, this.whitePropertiesList, "OpenSPCoop2TunnelSOAPMimeType");
			if(tunnelSoapMimeType!=null){
				tunnelSoapMimeType = tunnelSoapMimeType.trim();
			}
			
			
			

			// opzione returnCode
			int returnCode = 200;
			String returnCodeString = getParameter_checkWhiteList(req, this.whitePropertiesList, "returnCode");
			if(returnCodeString!=null){
				try{
					returnCode = Integer.parseInt(returnCodeString.trim());
				}catch(Exception e){
					if(this.log!=null)
						this.log.warn("ERRORE TestService (param returnCode): "+e.toString());
					else
						System.out.println("ERRORE TestService (param returnCode): "+e.toString());
				}
			}
			
			
			
			
			// opzione replace
			// formato: old:new[,old:new,....,old:new]
			String replace = getParameter_checkWhiteList(req, this.whitePropertiesList, "replace");
			Map<String, String> replaceMap = new Hashtable<String, String>();
			if(replace!=null){
				// volutamente non faccio il trim
				if(replace.contains(",")){
					String [] list = replace.split(",");
					for (int i = 0; i < list.length; i++) {
						if(list[i].contains(":")){
							String [] tmp = list[i].split(":");
							if(tmp==null || tmp.length!=2){
								throw new Exception("Opzione replace con valore errato ["+list[i]+"] (caso1 iter"+i+"), formato atteso: old:new[,old:new,....,old:new]");
							}
							else{
								replaceMap.put(tmp[0], tmp[1]);
							}
						}
						else{
							throw new Exception("Opzione replace con valore errato ["+list[i]+"] (caso2 iter"+i+"), formato atteso: old:new[,old:new,....,old:new]");
						}	
					}
				}else{
					if(replace.contains(":")){
						String [] tmp = replace.split(":");
						if(tmp==null || tmp.length!=2){
							throw new Exception("Opzione replace con valore errato ["+replace+"] (caso1), formato atteso: old:new[,old:new,....,old:new]");
						}
						else{
							replaceMap.put(tmp[0], tmp[1]);
						}
					}
					else{
						throw new Exception("Opzione replace con valore errato ["+replace+"] (caso2), formato atteso: old:new[,old:new,....,old:new]");
					}
				}
			}



			
			
			
			// opzione returnHttpHeader
			String returnHeaderString = getParameter_checkWhiteList(req, this.whitePropertiesList, "returnHttpHeader");
			String returnHeaderKey = null; 
			String returnHeaderValue = null;
			if(returnHeaderString!=null){
				returnHeaderString = returnHeaderString.trim();
				if(returnHeaderString.contains(":")==false){
					throw new ServletException("Ricevuta una richiesta di generazione header di risposta non conforme (pattern nome:valore)");
				}
				String [] split = returnHeaderString.split(":");
				if(split==null){
					throw new ServletException("Ricevuta una richiesta di generazione header di risposta non conforme (pattern nome:valore) (split null)");
				}
				if(split.length!=2){
					throw new ServletException("Ricevuta una richiesta di generazione header di risposta non conforme (pattern nome:valore) (split:"+split.length+")");
				}
				returnHeaderKey = split[0];
				returnHeaderValue = split[1];	
			}
			
			
			
			
			// opzioni save msg
			String logMessageTmp = getParameter_checkWhiteList(req, this.whitePropertiesList, "logMessage");
			boolean logMessage = false;
			if(logMessageTmp!=null){
				logMessageTmp = logMessageTmp.trim();
				if("true".equals(logMessage))
					logMessage = true;
			}
			String saveMessageDir = getParameter_checkWhiteList(req, this.whitePropertiesList, "saveMessageDir");
			if(saveMessageDir!=null){
				saveMessageDir = saveMessageDir.trim();
			}
			
			
			byte[] contenuto = null;
			if(logMessage || saveMessageDir!=null){
				ServletInputStream sin = req.getInputStream();
				ByteArrayOutputStream outStr = new ByteArrayOutputStream();
				int read;
				while( (read = sin.read()) != -1)
					outStr.write(read);
				contenuto = outStr.toByteArray();
			}
			
			String contentTypeRichiesta = req.getContentType();
			StringBuffer sb = new StringBuffer();
			sb.append("--------  Messaggio ricevuto il : "+(new Date()).toString()+" [ct:"+contentTypeRichiesta+"] -------------\n\n");
			if(logMessage){
				sb.append(new String(contenuto));
			}
			if(saveMessageDir!=null){
				File dir = new File(saveMessageDir);
				if(dir.exists()==false){
					throw new Exception("Directory ["+dir.getAbsolutePath()+"] doesn't exists");
				}
				if(dir.canWrite()==false){
					throw new Exception("Directory ["+dir.getAbsolutePath()+"] without write permission");
				}
				if(dir.canRead()==false){
					throw new Exception("Directory ["+dir.getAbsolutePath()+"] without read permission");
				}
				String ext = "bin";
				try{
					String ct = new String(contentTypeRichiesta);
					if(ct.contains(";")){
						ct = ct.split(";")[0];
					}
					ext = MimeTypes.getInstance().getExtension(ct);
				}catch(Exception e){
					this.log.warn("Riconoscimento ext file tramite contentType["+contentTypeRichiesta+"] non riuscito: "+e.getMessage(),e);
				}
				File f = File.createTempFile("Message", "."+ext, dir);
				FileSystemUtilities.writeFile(f, contenuto);
				if(logMessage){
					sb.append("\n\n");
				}
				sb.append("saved in: "+f.getAbsolutePath());
	
			}
			if(sb.length()>0){
				sb.append("\n\n");
				this.log.info(sb.toString());
			}
			
			

			
			
			// sleep
			String sleep = getParameter_checkWhiteList(req, this.whitePropertiesList, "sleep");
			if(sleep!=null){
				int millisecond = Integer.parseInt(sleep);
				if(millisecond>1000){
					int count = millisecond/1000;
					int resto = millisecond%1000;
					this.log.info("sleep "+millisecond+"ms ...");
					for (int i = 0; i < count; i++) {
						Utilities.sleep(1000);
					}
					Utilities.sleep(resto);
					this.log.info("sleep "+millisecond+"ms terminated");
				}else{
					this.log.info("sleep "+millisecond+"ms ...");
					Utilities.sleep(millisecond);
					this.log.info("sleep "+millisecond+"ms terminated");
				}
			}
			
			
			
			
			
			
			// sleep in intervallo
			String min = getParameter_checkWhiteList(req, this.whitePropertiesList, "sleepMin");
			String max = getParameter_checkWhiteList(req, this.whitePropertiesList, "sleepMax");
			if(max!=null){
				int maxSleep = Integer.parseInt((String)max);
				int minSleep = 0;
				if(min!=null){
					minSleep = Integer.parseInt((String)min);
				}
				Random r = new Random();
				int sleepInteger = minSleep + r.nextInt(maxSleep-minSleep);
				if(sleepInteger>1000){
					int count = sleepInteger/1000;
					int resto = sleepInteger%1000;
					this.log.info("sleep "+sleepInteger+"ms ...");
					for (int i = 0; i < count; i++) {
						Utilities.sleep(1000);
					}
					Utilities.sleep(resto);
					this.log.info("sleep "+sleepInteger+"ms terminated");
				}else{
					this.log.info("sleep "+sleepInteger+"ms ...");
					Utilities.sleep(sleepInteger);
					this.log.info("sleep "+sleepInteger+"ms terminated");
				}
			}	
			
			
			
			

			
			
			// Echo risposta

			if(fault!=null && fault.equalsIgnoreCase("true")){
				
				MessageType soapVersion = MessageType.SOAP_11;
				if(faultSoapVersion!=null){
					if("12".equals(faultSoapVersion)){
						soapVersion = MessageType.SOAP_12;
					}
				}
				
				OpenSPCoop2MessageFactory factory = OpenSPCoop2MessageFactory.getMessageFactory();
				OpenSPCoop2Message msg = factory.createFaultMessage(soapVersion, faultMessage);
				OpenSPCoop2SoapMessage soapMsg = msg.castAsSoap();
				javax.xml.namespace.QName qName = new javax.xml.namespace.QName(faultNamespaceCode,faultCode);
				SOAPBody bdy = soapMsg.getSOAPBody();
				SOAPFault f = bdy.getFault();
				soapMsg.setFaultCode(f, SOAPFaultCode.Receiver, qName);
	            f.setFaultActor(faultActor);
				
	            if(returnHeaderKey!=null && returnHeaderValue!=null){
	            	res.setHeader(returnHeaderKey,returnHeaderValue);
	            }
	            
	            msg.saveChanges();
				res.setContentType(msg.getContentType());
				
				res.setStatus(returnCode);
				
	            ServletOutputStream sout = res.getOutputStream();
	            msg.writeTo(sout,true);
				
			}else{
				
				if(oneway){
					
		            if(returnHeaderKey!=null && returnHeaderValue!=null){
		            	res.setHeader(returnHeaderKey,returnHeaderValue);
		            }
					
					res.setStatus(returnCode);
					
					return;
				}
				
				if(tunnel){
					res.setHeader("X-OpenSPCoop2-TunnelSOAP", "true");
					if(tunnelSoapMimeType!=null)
						res.setHeader("X-OpenSPCoop2-TunnelSOAP-MimeType",tunnelSoapMimeType);
				}
				
				
				String contentTypeRisposta = contentTypeRichiesta;
				
				String fileDestinazione = getParameter_checkWhiteList(req, this.whitePropertiesList, "destFile");
				String fileResponse = getParameter_checkWhiteList(req, this.whitePropertiesList, "response");
				if(fileResponse==null) {
					fileResponse = getParameter_checkWhiteList(req, this.whitePropertiesList, "op"); // alias per response, in modo da rendere meno evidente l'operazione
				}
				ByteArrayOutputStream boutStaticFile = null;
				if(fileDestinazione!=null || fileResponse!=null){
					
					String path = null;
					
					if(fileDestinazione!=null) {
						fileDestinazione = fileDestinazione.trim();
						path = fileDestinazione;
					}
					else {
						fileResponse = fileResponse.trim();
						path = fileResponse;
						if(this.repositoryResponseFiles==null) {
							throw new Exception("Property 'response' non utilizzabile se non viene definito un repository dei files");
						}
						File f = new File(this.repositoryResponseFiles,fileResponse);
						path = f.getAbsolutePath();
					}
					
					FileInputStream fin = new FileInputStream(path);
					boutStaticFile = new ByteArrayOutputStream();
					FileSystemUtilities.copy(fin, boutStaticFile);
					boutStaticFile.flush();
					boutStaticFile.close();
					fin.close();
					
					String fileDestinazioneContentType = getParameter_checkWhiteList(req, this.whitePropertiesList, "destFileContentType");
					if(fileDestinazioneContentType!=null){
						fileDestinazioneContentType = fileDestinazioneContentType.trim();
						contentTypeRisposta = fileDestinazioneContentType;
					}
					else{
						
						if(contentTypeRichiesta!=null && contentTypeRichiesta.contains("multipart/related")==false){
							contentTypeRisposta = ContentTypeUtilities.readBaseTypeFromContentType(contentTypeRichiesta); // uso lo stesso contentType della richiesta.
						}else
							contentTypeRisposta = "text/xml"; // default soap 1.1
												
						byte[] a = boutStaticFile.toByteArray();
						if(MultipartUtils.messageWithAttachment(a)){
							
							String fileDestinazioneMultipartParameterType = getParameter_checkWhiteList(req, this.whitePropertiesList, "destFileContentTypeMultipartParameterType");
							if(fileDestinazioneMultipartParameterType!=null) {
								contentTypeRisposta = fileDestinazioneMultipartParameterType.trim();
							}
							
							String subType = "related";
							String fileDestinazioneMultipartSubType = getParameter_checkWhiteList(req, this.whitePropertiesList, "destFileContentTypeMultipartSubType");
							if(fileDestinazioneMultipartSubType!=null) {
								subType = fileDestinazioneMultipartSubType;
							}
							
							String IDfirst  = MultipartUtils.firstContentID(a);
							String boundary = MultipartUtils.findBoundary(a);
							if(boundary==null){
								throw new Exception("Errore avvenuto durante la lettura del boundary associato al multipart message.");
							}
							if(IDfirst==null)
								contentTypeRisposta = "multipart/"+subType+"; type=\""+contentTypeRisposta+"\"; \tboundary=\""+boundary.substring(2,boundary.length())+"\" "; 
							else
								contentTypeRisposta = "multipart/"+subType+"; type=\""+contentTypeRisposta+"\"; start=\""+IDfirst+"\"; \tboundary=\""+boundary.substring(2,boundary.length())+"\" "; 
						}
						
					}
					

									
				}
				
				//System.out.println("CHUNKED: "+chunked);
				
				// modalita'
				if(chunked){
					res.setHeader("Transfer-Encoding","chunked");
				}
				else{
					if(boutStaticFile!=null){
						res.setContentLength(boutStaticFile.size());
					}else if (contenuto!=null){
						res.setContentLength(contenuto.length);
					}else{
						// web server default
					}
				}
				
				// Header personalizzati della Testsuite di OpenSPCoop
				if(headerRisposta!=null){
					Enumeration<?> en = headerRisposta.keys();
					while (en.hasMoreElements()) {
						String key = (String) en.nextElement();
						String value = headerRisposta.getProperty(key);
						if(value!=null){
							res.setHeader(key, value);
						}
					}
				}
				
				// Return Header
	            if(returnHeaderKey!=null && returnHeaderValue!=null){
	            	res.setHeader(returnHeaderKey,returnHeaderValue);
	            }
				
				// contentType
				res.setContentType(contentTypeRisposta);
				
				// status
				res.setStatus(returnCode);
				
				// contenuto
				if(boutStaticFile!=null){
					res.getOutputStream().write(boutStaticFile.toByteArray());
				}else if (contenuto!=null){
					if(replaceMap!=null && replaceMap.size()>0){
						res.getOutputStream().write(this.replace(contenuto, replaceMap));
					}
					else{
						res.getOutputStream().write(contenuto);
					}
				}else{
					if(replaceMap!=null && replaceMap.size()>0){
						byte[] contenutoRequest = Utilities.getAsByteArray(req.getInputStream());
						res.getOutputStream().write(this.replace(contenutoRequest, replaceMap));
					}
					else{
						FileSystemUtilities.copy(req.getInputStream(), res.getOutputStream());
					}
				}
					
				res.getOutputStream().flush();
				res.getOutputStream().close();
				
			}
			
		}catch(Throwable e){
			if(this.log!=null) {
				this.log.error("TestService: "+e.getMessage(),e);
			}else {
				System.out.println("ERRORE TestService: "+e.toString());
				e.printStackTrace(System.out);
			}
			if(this.genericError) {
				res.setStatus(500);
				res.getOutputStream().flush();
				res.getOutputStream().close();
			}
			else {
				throw new ServletException(e.getMessage(),e);
			}
		}
	}

	// La doGet viene implementata normalmente da chi la estende
	
	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse res)
	throws ServletException, IOException {
		doGet(req,res);
	}

	
	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
		doGet(req,res);
	}

	@Override
	protected void doHead(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
		doGet(req,res);
	}


	@Override
	protected void doOptions(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
		doGet(req,res);
	}


	@Override
	protected void doPut(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
		doGet(req,res);
	}


	@Override
	protected void doTrace(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
		doGet(req,res);
	}
	
	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		HttpRequestMethod m = HttpRequestMethod.valueOf(req.getMethod().toUpperCase());
		switch (m) {
		
		// Standard
		
		case DELETE:
			this.doDelete(req, resp);
			break;
		case GET:
			this.doGet(req, resp);
			break;
		case HEAD:
			this.doHead(req, resp);
			break;
		case OPTIONS:
			this.doOptions(req, resp);
			break;
		case POST:
			this.doPost(req, resp);
			break;
		case PUT:
			this.doPut(req, resp);
			break;
		case TRACE:
			this.doTrace(req, resp);
			break;
			
		// Additionals
		case PATCH:
		case LINK:
		case UNLINK:
			doGet(req, resp);
			break;
			
		default:
			super.service(req, resp); // richiamo implementazione originale che genera errore: Method XXX is not defined in RFC 2068 and is not supported by the Servlet API
			break;
		}
	}
	
	private byte[] replace(byte[]contenuto,Map<String,String> map){
		String s = new String(contenuto);
		Iterator<String> it = map.keySet().iterator();
		while (it.hasNext()) {
			String oldValue = (String) it.next();
			String newValue = map.get(oldValue);
			s = s.replaceAll(oldValue, newValue);
			//System.out.println("oldValue ["+oldValue+"] replacewith["+map.get(oldValue)+"] ["+s+"]");
		}
		//System.out.println("OTTENUTO ["+s+"]");
		return s.getBytes();
	}

}
