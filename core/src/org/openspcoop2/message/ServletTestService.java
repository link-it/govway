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



package org.openspcoop2.message;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Enumeration;
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
import org.openspcoop2.utils.resources.FileSystemUtilities;
import org.openspcoop2.utils.resources.MimeTypes;


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
	
	private Logger log = null;
	
	public ServletTestService(Logger log){
		this.log = log;
	}
	
	public static void checkHttpServletRequestParameter(HttpServletRequest request) throws ServletException{
		
		String checkEqualsHttpMethod = request.getParameter("checkEqualsHttpMethod");
		if(checkEqualsHttpMethod!=null){
			checkEqualsHttpMethod = checkEqualsHttpMethod.trim();
			if(checkEqualsHttpMethod.equals(request.getMethod())==false){
				throw new ServletException("Ricevuta una richiesta con metodo http ["+request.getMethod()+"] differente da quella attesa ["+checkEqualsHttpMethod+"]");
			}
		}
		
		String checkEqualsHttpHeader = request.getParameter("checkEqualsHttpHeader");
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
			
			checkHttpServletRequestParameter(req);
			
			
			
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
			
			
					

			// opzioni connettore
			String fault = req.getParameter("fault");
			String faultActor = "OpenSPCoopTrace";
			String faultSoapVersion = null;
			String faultNamespaceCode = "http://www.openspcoop2.org/example";
			String faultCode = "Server.OpenSPCoopExampleFault";
			String faultMessage = "Fault ritornato dalla servlet di trace, esempio di OpenSPCoop";
			if(fault!=null && fault.equalsIgnoreCase("true")){
				
				if(req.getParameter("faultActor")!=null)
					faultActor = req.getParameter("faultActor");
				
				if(req.getParameter("faultSoapVersion")!=null)
					faultSoapVersion = req.getParameter("faultSoapVersion");
				
				if(req.getParameter("faultCode")!=null)
					faultCode = req.getParameter("faultCode");
				
				if(req.getParameter("faultNamespaceCode")!=null)
					faultNamespaceCode = req.getParameter("faultNamespaceCode");
				
				if(req.getParameter("faultMessage")!=null)
					faultMessage = req.getParameter("faultMessage");
			}
			
			
			
			
			
			// opzioni tunnel SOAP
			String tunnelSoap = req.getParameter("OpenSPCoop2TunnelSOAP");
			boolean tunnel = false;
			if(tunnelSoap!=null){
				tunnelSoap = tunnelSoap.trim();
				if("true".equals(tunnelSoap))
					tunnel = true;
			}
			String tunnelSoapMimeType = req.getParameter("OpenSPCoop2TunnelSOAPMimeType");
			if(tunnelSoapMimeType!=null){
				tunnelSoapMimeType = tunnelSoapMimeType.trim();
			}
			
			
			

			// opzione returnCode
			int returnCode = 200;
			String returnCodeString = req.getParameter("returnCode");
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
			
			
			
			
			
			// opzioni save msg
			String logMessageTmp = req.getParameter("logMessage");
			boolean logMessage = false;
			if(logMessageTmp!=null){
				logMessageTmp = logMessageTmp.trim();
				if("true".equals(logMessage))
					logMessage = true;
			}
			String saveMessageDir = req.getParameter("saveMessageDir");
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
			String sleep = req.getParameter("sleep");
			if(sleep!=null){
				int millisecond = Integer.parseInt(sleep);
				if(millisecond>1000){
					int count = millisecond/1000;
					int resto = millisecond%1000;
					this.log.info("sleep "+millisecond+"ms ...");
					for (int i = 0; i < count; i++) {
						try{
							Thread.sleep(1000);
						}catch(Exception e){}
					}
					try{
						Thread.sleep(resto);
					}catch(Exception e){}
					this.log.info("sleep "+millisecond+"ms terminated");
				}else{
					this.log.info("sleep "+millisecond+"ms ...");
					try{
						Thread.sleep(millisecond);
					}catch(Exception e){}
					this.log.info("sleep "+millisecond+"ms terminated");
				}
			}
			
			
			
			
			
			
			// sleep in intervallo
			String min = req.getParameter("sleepMin");
			String max = req.getParameter("sleepMax");
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
						try{
							Thread.sleep(1000);
						}catch(Exception e){}
					}
					try{
						Thread.sleep(resto);
					}catch(Exception e){}
					this.log.info("sleep "+sleepInteger+"ms terminated");
				}else{
					this.log.info("sleep "+sleepInteger+"ms ...");
					try{
						Thread.sleep(sleepInteger);
					}catch(Exception e){}
					this.log.info("sleep "+sleepInteger+"ms terminated");
				}
			}	
			
			
			
			

			
			
			// Echo risposta

			if(fault!=null && fault.equalsIgnoreCase("true")){
				
				SOAPVersion soapVersion = SOAPVersion.SOAP11;
				if(faultSoapVersion!=null){
					if("12".equals(faultSoapVersion)){
						soapVersion = SOAPVersion.SOAP12;
					}
				}
				
				OpenSPCoop2MessageFactory factory = OpenSPCoop2MessageFactory.getMessageFactory();
				OpenSPCoop2Message msg = factory.createFaultMessage(soapVersion, faultMessage);
				javax.xml.namespace.QName qName = new javax.xml.namespace.QName(faultNamespaceCode,faultCode);
				SOAPBody bdy = msg.getSOAPBody();
				SOAPFault f = bdy.getFault();
				msg.setFaultCode(f, SOAPFaultCode.Receiver, qName);
	            f.setFaultActor(faultActor);
				
	            msg.saveChanges();
				res.setContentType(msg.getContentType());
				
				res.setStatus(returnCode);
				
	            ServletOutputStream sout = res.getOutputStream();
	            msg.writeTo(sout,true);
				
			}else{
				
				if(oneway){
					
					res.setStatus(returnCode);
					
					return;
				}
				
				if(tunnel){
					res.setHeader("X-OpenSPCoop2-TunnelSOAP", "true");
					if(tunnelSoapMimeType!=null)
						res.setHeader("X-OpenSPCoop2-TunnelSOAP-MimeType",tunnelSoapMimeType);
				}
				
				
				String contentTypeRisposta = contentTypeRichiesta;
				
				String fileDestinazione = req.getParameter("destFile");
				ByteArrayOutputStream boutStaticFile = null;
				if(fileDestinazione!=null){
					
					fileDestinazione = fileDestinazione.trim();
					
					FileInputStream fin = new FileInputStream(fileDestinazione);
					boutStaticFile = new ByteArrayOutputStream();
					FileSystemUtilities.copy(fin, boutStaticFile);
					boutStaticFile.flush();
					boutStaticFile.close();
					fin.close();
					
					String fileDestinazioneContentType = req.getParameter("destFileContentType");
					if(fileDestinazioneContentType!=null){
						fileDestinazioneContentType = fileDestinazioneContentType.trim();
						contentTypeRisposta = fileDestinazioneContentType;
					}
					else{
						
						if(contentTypeRichiesta.contains("multipart/related")==false)
							contentTypeRisposta = contentTypeRichiesta; // uso lo stesso contentType della richiesta.
						else
							contentTypeRisposta = "text/xml"; // default soap 1.1
						
						byte[] a = boutStaticFile.toByteArray();
						if(AttachmentsUtils.messageWithAttachment(a)){
							String IDfirst  = AttachmentsUtils.firstContentID(a);
							String boundary = AttachmentsUtils.findBoundary(a);
							if(boundary==null){
								throw new Exception("Errore avvenuto durante la lettura del boundary associato al multipart message.");
							}
							if(IDfirst==null)
								contentTypeRisposta = "multipart/related; type=\""+contentTypeRisposta+"\"; \tboundary=\""+boundary.substring(2,boundary.length())+"\" "; 
							else
								contentTypeRisposta = "multipart/related; type=\""+contentTypeRisposta+"\"; start=\""+IDfirst+"\"; \tboundary=\""+boundary.substring(2,boundary.length())+"\" "; 
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
				
				// contentType
				res.setContentType(contentTypeRisposta);
				
				// status
				res.setStatus(returnCode);
				
				// contenuto
				if(boutStaticFile!=null){
					res.getOutputStream().write(boutStaticFile.toByteArray());
				}else if (contenuto!=null){
					res.getOutputStream().write(contenuto);
				}else{
					FileSystemUtilities.copy(req.getInputStream(), res.getOutputStream());
				}
					
				res.getOutputStream().flush();
				res.getOutputStream().close();
				
			}
			
		}catch(Exception e){
			e.printStackTrace(System.out);
			if(this.log!=null)
				this.log.error("ERRORE TestService: "+e.toString());
			else
				System.out.println("ERRORE TestService: "+e.toString());
			throw new ServletException(e.getMessage(),e);
		}
	}

	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse res)
	throws ServletException, IOException {
		doGet(req,res);
	}


}
