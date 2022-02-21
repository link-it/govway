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

package org.openspcoop2.message.soap.reader;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.SequenceInputStream;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPHeader;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.OpenSPCoop2MessageFactory;
import org.openspcoop2.message.constants.Costanti;
import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.message.exception.MessageException;
import org.openspcoop2.message.soap.OpenSPCoop2Message_saaj_11_impl;
import org.openspcoop2.message.soap.OpenSPCoop2Message_saaj_12_impl;
import org.openspcoop2.message.soap.SoapUtils;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.resources.Charset;
import org.openspcoop2.utils.transport.http.ContentTypeUtilities;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.utils.xml.TransformerConfig;
import org.openspcoop2.utils.xml.XMLUtils;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

/**
 * OpenSPCoop2MessageSoapParser
 *
 * @author Andrea Poli (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class OpenSPCoop2MessageSoapStreamReader {

	public static boolean SOAP_HEADER_OPTIMIZATION_ENABLED = true;
	
	@SuppressWarnings("unused")
	private OpenSPCoop2MessageFactory msgFactory;
	private InputStream is;
	private String contentType;
	private int bufferThresholdKb;
	
	public OpenSPCoop2MessageSoapStreamReader(OpenSPCoop2MessageFactory msgFactory, String contentType, InputStream is, int bufferThresholdKb) {
		this.msgFactory = msgFactory;
		this.contentType = contentType;
		this.is = is;
		this.bufferThresholdKb = bufferThresholdKb;
	}

	private String envelope;
	private String prefixEnvelope = null;
	private String namespace;
	
	private String header;
	private String prefixHeader = null;
	private OpenSPCoop2Message _headerMsgCompleto;
	private boolean soapHeaderModified = false;
	
	private boolean soapHeaderOptimizable = false;
	private long startHeaderOffset;
	private long endHeaderOffset;
	private long startBodyOffset;
	private String isCharset;
	
	private String body;
	private boolean bodyEmpty;
	//private Element rootElement;
	private String rootElementNamespace;
	private String rootElementLocalName;
	private String fault;
	
	private int bufferSize;
	
	private boolean parsingComplete = false;
	
	private MessageException tBuffered = null;
	
	public void checkException() throws MessageException {
		if(this.tBuffered!=null) {
			throw this.tBuffered;
		}
	}
	private InputStream bufferedInputStream = null;
	public InputStream getBufferedInputStream() {
		return this.bufferedInputStream;
	}
	public void releaseBufferedInputStream() {
		this.bufferedInputStream = null;
	}
	public void read() throws MessageException {

		this.bufferedInputStream = this.is;
				
		if(this.tBuffered!=null) {
			throw this.tBuffered;
		}
		
		if(this.contentType==null || StringUtils.isEmpty(this.contentType) || this.is == null) {
			return;
		}
		boolean multipart = false;
		try {
			multipart = ContentTypeUtilities.isMultipartType(this.contentType);
		}catch(Throwable t) {}
		String charset = null;
		try {
			if(!multipart) {
				charset = ContentTypeUtilities.readCharsetFromContentType(this.contentType);
				this.isCharset = charset; 
				if(Charset.UTF_8.getValue().equalsIgnoreCase(charset)) {
					charset = null;
				}
			}
			else {
				String internalCT = ContentTypeUtilities.getInternalMultipartContentType(this.contentType);
				this.isCharset = ContentTypeUtilities.readCharsetFromContentType(internalCT);
			}
		}catch(Throwable t) {}
		
		try {
			ByteArrayOutputStream bufferReaded = new ByteArrayOutputStream();
			int lettiBuffer = 0;
			
			StringBuilder sbActual = null;
			long sbActualOffsetStart = -1;
			byte[] bufferRaw = new byte[1024];
			long bLetti = 0;
			int letturaPrecedente = 0;
			int kbLetti = 0;
			
			boolean envelopeFound = false; 
			
			StringBuilder headerBuilder = null;
			String headerClosure = null;
			boolean headerCompletato = false;
			
			boolean bodyFound = false;
			String prefixBody = null;
			
			String elementAfterBody = null;
			
			boolean cdataFound = false;
			boolean commentFound = false;
			
			boolean analisiTerminata = false;
			
//			String check = org.openspcoop2.utils.Utilities.getAsString(this.is, "UTF8");
//			System.out.println("STREAM: "+check);
//			this.is = new java.io.ByteArrayInputStream(check.getBytes());
			
			while ( (lettiBuffer=this.is.read(bufferRaw)) !=-1) {
				
				// bufferizzo (1024 byte = 1kb alla volta)
				bufferReaded.write(bufferRaw, 0, lettiBuffer);
				kbLetti++;
				if(letturaPrecedente>0) {
					bLetti = bLetti + letturaPrecedente;
				}
				letturaPrecedente = lettiBuffer;
			
				// In caso di charset differente, uso una stringa
				String bufferString = null;
				if(charset!=null) {
					ByteArrayOutputStream bufferTmp = new ByteArrayOutputStream();
					bufferTmp.write(bufferRaw, 0, lettiBuffer);
					bufferTmp.flush();
					bufferTmp.close();
					bufferString = new String(bufferTmp.toByteArray(), charset);
					lettiBuffer = bufferString.length();
				}
				
				// analizzo
				for (int i = 0; i < lettiBuffer; i++) {
					char c = bufferString!=null ? bufferString.charAt(i) : (char) bufferRaw[i];
					
					if(headerCompletato && this.endHeaderOffset<=0) {
						this.endHeaderOffset = sbActualOffsetStart;
					}
					
					if( (c == '<' || (sbActual!=null && sbActual.toString().startsWith("&lt;"))) 
							&& 
							!cdataFound &&
							!commentFound) {
												
						if(sbActual!=null) {
							if(sbActual.length()>0) {
								
								// devo verificare se ho incontrato un cdata o sto continuando a leggere un header
								String sPreClosure = sbActual.toString();
								sbActual.delete(0, sbActual.length());
								sbActualOffsetStart = i+bLetti;
								
								if(sPreClosure.startsWith("<![CDATA[")) {
									//System.out.println("FOND CDATA! in sPreClosure");
									cdataFound=true;
								}
								else if(sPreClosure.startsWith("<!--")) {
									//System.out.println("FOND <!-- COMMENTO in sPreClosure: ["+sPreClosure+"]");
									if(!sPreClosure.endsWith("-->")){
										commentFound=true;
									}
								}
								
								if(headerClosure!=null) {
									//System.out.println("AGGIUNGO AD HEADER TESTO ["+sPreClosure+"]");
									headerBuilder.append(sPreClosure);
								}
								
							}
						}
						else {
							sbActual = new StringBuilder();
							sbActualOffsetStart = i+bLetti;
						}
					}
					if(sbActual!=null) {
						sbActual.append(c);
					}
					if(c == '>' || (sbActual!=null && sbActual.toString().endsWith("&gt;"))) {
						
						if(sbActual==null) {
							//NO: gli attachments potrebbero rientrare in questo caso
							if(multipart) {
								continue;
							}
							else {
								throw new Exception("Invalid content; found premature '>' character ("+bufferReaded.toString()+")");
							}
						}
						
						String s = sbActual.toString();
						
						//System.out.println("S ["+s+"]");
						
						if(cdataFound) {
							if(headerClosure!=null) {
								//System.out.println("AGGIUNGO AD HEADER CDATA ["+sbActual.toString()+"]");
								headerBuilder.append(sbActual.toString());
							}
							sbActual.delete(0, sbActual.length());
							sbActualOffsetStart = i+bLetti;
							//System.out.println("continuo perche CDATA... ");
							if(s.endsWith("]]>")) {
								cdataFound=false;
								//System.out.println("FOUND FINE CDATA");
							}
							continue;
						}
						if(s.startsWith("<![CDATA[")) {
							//System.out.println("FOUND CDATA!");
							cdataFound=true;
							continue;
						}
						
						if(commentFound) {
							if(headerClosure!=null) {
								//System.out.println("AGGIUNGO AD HEADER COMMENTO ["+sbActual.toString()+"]");
								headerBuilder.append(sbActual.toString());
							}
							sbActual.delete(0, sbActual.length());
							sbActualOffsetStart = i+bLetti;
							//System.out.println("continuo perche COMMENTO... ");
							if(s.endsWith("-->")) {
								commentFound=false;
								//System.out.println("FOUND FINE COMMENTO");
							}
							continue;
						}
						if(s.startsWith("<!--")) {
							//System.out.println("FOUND <!-- COMMENTO: ["+s+"]");
							if(s.endsWith("-->")){
								if(headerClosure!=null) {
									//System.out.println("AGGIUNGO AD HEADER COMMENTO ["+sbActual.toString()+"]");
									headerBuilder.append(sbActual.toString());
								}
								sbActual.delete(0, sbActual.length());
								sbActualOffsetStart = i+bLetti;
							}
							else {
								commentFound=true;
							}
							continue;
						}
						
						if(s.startsWith("<?")) {
							continue;
						}
						
						if(!envelopeFound && isOpenedElement(s,"<Envelope")) {
							this.envelope = s;
							//System.out.println("INIZIALIZZO ENVELOPE ["+s+"]");
							if(!analizyEnvelopeNamespace(this.envelope)) {
								//System.out.println("TERMINO");
								analisiTerminata=true;
								break;
							}
							envelopeFound = true;
							this.prefixEnvelope = "";
						}
						else if(!envelopeFound && isOpenedElement(s,":Envelope")) {
							String sPrefix = s.substring(0, s.indexOf(":Envelope"));
							if(sPrefix.startsWith("<") && !sPrefix.contains(" ") && sPrefix.length()>1) {
								this.envelope = s;
								//System.out.println("INIZIALIZZO ENVELOPE ["+s+"]");
								if(!analizyEnvelopeNamespace(this.envelope)) {
									//System.out.println("TERMINO");
									analisiTerminata=true;
									break;
								}
								envelopeFound = true;
								this.prefixEnvelope = sPrefix.substring(1)+":";
							}
							else {
								analisiTerminata=true;
								break;
							}
						}
						else if(envelopeFound) {
						
							if(headerClosure!=null) {
								//System.out.println("AGGIUNGO AD HEADER ["+s+"]");
								headerBuilder.append(s);
								sbActual.delete(0, sbActual.length());
								sbActualOffsetStart = i+bLetti;
								if(s.startsWith(headerClosure)) {
									headerClosure=null;	
									headerCompletato = true;
								}
								continue;
							}
							
							if(headerBuilder==null && isOpenedElement(s,"<Header")) {
								//System.out.println("INIZIALIZZO HEADER ["+s+"]");
								//headerBuilder = new StringBuilder(s);
								headerBuilder = new StringBuilder();
								if(!s.endsWith("/>")) {
									headerClosure = "</Header";
								}
								if(bodyFound) {
									analisiTerminata=true;
									break;
								}
								else {
									this.startHeaderOffset = sbActualOffsetStart;
									this.prefixHeader = "";
								}
							}
							else if(headerBuilder==null && isOpenedElement(s,":Header")) {
								String sPrefix = s.substring(0, s.indexOf(":Header"));
								if(sPrefix.startsWith("<") && !sPrefix.contains(" ") && sPrefix.length()>1) {
									//System.out.println("INIZIALIZZO HEADER ["+s+"]");
									//headerBuilder = new StringBuilder(s);
									headerBuilder = new StringBuilder();
									if(!s.endsWith("/>")) {
										headerClosure = "</"+sPrefix.substring(1)+":"+"Header";
									}
									if(bodyFound) {
										analisiTerminata=true;
										break;
									}
									else {
										this.startHeaderOffset = sbActualOffsetStart;
										this.prefixHeader = sPrefix.substring(1)+":";
									}
								}
								else {
									analisiTerminata=true;
									break;
								}
							}
							else if(!bodyFound && isOpenedElement(s,"<Body")) {
								//System.out.println("INIZIALIZZO BODY ["+s+"]");
								this.body = s;
								bodyFound = true;
								prefixBody = "";
								if(this.startHeaderOffset<=0) {
									this.startHeaderOffset = sbActualOffsetStart;
								}
								this.startBodyOffset=sbActualOffsetStart;
							}
							else if(!bodyFound && isOpenedElement(s,":Body")) {
								String sPrefix = s.substring(0, s.indexOf(":Body"));
								if(sPrefix.startsWith("<") && !sPrefix.contains(" ") && sPrefix.length()>1) {
									//System.out.println("INIZIALIZZO BODY ["+s+"]");
									this.body = s;
									bodyFound = true;
									prefixBody = sPrefix.substring(1)+":";
									if(this.startHeaderOffset<=0) {
										this.startHeaderOffset = sbActualOffsetStart;
									}
									this.startBodyOffset=sbActualOffsetStart;
								}
								else {
									analisiTerminata=true;
									break;
								}
							}
							else if(bodyFound) {
								if(this.fault==null && isOpenedElement(s,"<Fault")) {
									//System.out.println("INIZIALIZZO FAULT ["+s+"]");
									this.fault = s;
								}
								else if(this.fault==null && isOpenedElement(s,":Fault")) {
									String sPrefix = s.substring(0, s.indexOf(":Fault"));
									if(sPrefix.startsWith("<") && !sPrefix.contains(" ")) {
										//System.out.println("INIZIALIZZO FAULT ["+s+"]");
										this.fault = s;
									}
								}
								else {
									
									String closeBody = "</"+prefixBody+"Body";
									String closeEnvelope = "</"+this.prefixEnvelope+"Envelope";
									if( isClosedElement(s,closeBody) || isClosedElement(s,closeEnvelope) ){
										this.parsingComplete = true;
									}
									else {
										if(!s.endsWith("&gt;")){
											elementAfterBody = s;
										}
									}
									
									analisiTerminata=true;
									break;
								}
							}
							
						}
					}
					
				}
				if(analisiTerminata) {
					break;
				}
				if(kbLetti==this.bufferThresholdKb) {
					// buffer dimensione massima raggiunta
					break;
				}
			}
			
			// svuoto
			if(sbActual!=null) {
				if(sbActual.length()>0) {
					sbActual.delete(0, sbActual.length());
				}
			}
			
			
			bufferReaded.flush();
			bufferReaded.close();
			if(bufferReaded.size()>0) {
				this.bufferSize = bufferReaded.size();
				ByteArrayInputStream bin = new ByteArrayInputStream(bufferReaded.toByteArray());
				if(lettiBuffer!=-1) {
					// sono uscita prima della chiusura dello stream
					this.bufferedInputStream = new SequenceInputStream(bin,this.is);
				}
				else {
					// letto tutto
					this.bufferedInputStream = bin;
				}
			}
			
			
			boolean throwException = false;
			if(elementAfterBody!=null || this.fault!=null) {
				throwException=true; // ho terminato di leggere la parte interessante del messaggio
				this.parsingComplete = true;
			}

			if(headerBuilder!=null && headerBuilder.length()>0) {
				if(this.envelope.endsWith("/>")) {
					if(throwException) {
						throw new Exception("Invalid content; found 'Header' after envelope closure '/>': ("+headerBuilder+")");
					}
					else{
						return;
					}
				}
				if(headerCompletato) {
					StringBuilder sbHeaderAnalizer = new StringBuilder();
					sbHeaderAnalizer.append(this.envelope);
					sbHeaderAnalizer.append(headerBuilder.toString());
					sbHeaderAnalizer.append("<").append(this.prefixEnvelope).append("Body/>");
					sbHeaderAnalizer.append("</").append(this.prefixEnvelope).append("Envelope>");
					this.header = sbHeaderAnalizer.toString();
				}
			}
			if(this.body!=null) {
				//System.out.println("BODY ["+this.body+"]");
				if(this.envelope.endsWith("/>")) {
					if(throwException) {
						throw new Exception("Invalid content; found 'Body' after envelope closure '/>': ("+this.body+")");
					}
					else{
						return;
					}
				}
				if(this.body.endsWith("/>")) {
					this.bodyEmpty = true;
				}
			}
			
			if(this.fault!=null) {
				//System.out.println("TROVATO FAULT ["+this.fault+"]");
				if(this.body.endsWith("/>")) {
					if(throwException) {
						throw new Exception("Invalid content; found Fault after body closure '/>': ("+this.fault+")");
					}
					else{
						return;
					}
				}
				StringBuilder sbFaultAnalizer = new StringBuilder();
				sbFaultAnalizer.append(this.envelope);
				sbFaultAnalizer.append(this.body);
				sbFaultAnalizer.append(this.fault);
				if(!this.fault.endsWith("/>")) {
					StringBuilder sb = new StringBuilder();
					for (int i = 1; i < this.fault.length(); i++) {
						char c = this.fault.charAt(i);
						if(c==' ' || c=='>' || c=='\t' || c=='\r' || c=='\n') {
							break;
						}
						sb.append(c);
					}
					sbFaultAnalizer.append("</").append(sb.toString()).append(">");
				}
				sbFaultAnalizer.append("</").append(prefixBody).append("Body>");
				sbFaultAnalizer.append("</").append(this.prefixEnvelope).append("Envelope>");
				//Element e = null;
				String namespaceFaultTrovato = null;
				try {
					OpenSPCoop2Message msg = buildOp2Message(sbFaultAnalizer.toString().getBytes(), this.getMessageType());
					SOAPBody soapBody = msg.castAsSoap().getSOAPBody();
					if(soapBody!=null) {
						if(soapBody.hasFault()) {
							namespaceFaultTrovato = soapBody.getFault().getNamespaceURI();
						}
					}
				}catch(Throwable t) {
					if(throwException) {
						throw new Exception("Invalid content ("+sbFaultAnalizer.toString()+"): "+t.getMessage(),t);
					}
					else{
						return;
					}
				}
				if(!this.namespace.equals(namespaceFaultTrovato)) {
					elementAfterBody = this.fault;
					this.fault = null; // si tratta di un contenuto applicativo con nome Fault
				}
			}
			
			
			if(this.fault==null && elementAfterBody!=null) {
				//System.out.println("elementAfterBody ["+elementAfterBody+"]");
				if(this.body.endsWith("/>")) {
					if(throwException) {
						throw new Exception("Invalid content; found element after body closure '/>': ("+elementAfterBody+")");
					}
					else{
						return;
					}
				}
				StringBuilder sbElementAnalizer = new StringBuilder();
				sbElementAnalizer.append(this.envelope);
				sbElementAnalizer.append(this.body);
				sbElementAnalizer.append(elementAfterBody);
				if(!elementAfterBody.endsWith("/>")) {
					StringBuilder sb = new StringBuilder();
					for (int i = 1; i < elementAfterBody.length(); i++) {
						char c = elementAfterBody.charAt(i);
						if(c==' ' || c=='>' || c=='\t' || c=='\r' || c=='\n') {
							break;
						}
						sb.append(c);
					}
					sbElementAnalizer.append("</").append(sb.toString()).append(">");
				}
				sbElementAnalizer.append("</").append(prefixBody).append("Body>");
				sbElementAnalizer.append("</").append(this.prefixEnvelope).append("Envelope>");
				//Element e = null;
				SAXParser saxParser = null;
				try {
					//e = XMLUtils.getInstance().newElement(sbElementAnalizer.toString().getBytes());
					
					saxParser = getParser();
					XMLReader xmlReader = saxParser.getXMLReader();
					RootElementSaxContentHandler saxHandler = new RootElementSaxContentHandler(this.namespace);
					xmlReader.setContentHandler(saxHandler);
					//System.out.println("ANALIZZO '"+sbElementAnalizer.toString()+"'");
					try(ByteArrayInputStream bin = new ByteArrayInputStream(sbElementAnalizer.toString().getBytes())){
						InputSource inputSource = new InputSource(bin);
						xmlReader.parse(inputSource);
					}
					this.rootElementLocalName = saxHandler.getLocalName();
					this.rootElementNamespace = saxHandler.getNamespace();					
				}catch(Throwable t) {
					if(throwException) {
						throw new Exception("Invalid content '"+elementAfterBody+"' ("+sbElementAnalizer.toString()+"): "+t.getMessage(),t);
					}
					else{
						return;
					}
				}finally {
					if(saxParser!=null) {
						returnParser(saxParser);
					}
				}
//				Node body = SoapUtils.getFirstNotEmptyChildNode(this.msgFactory, e, false);
//				if(body!=null) {
//					Node element = SoapUtils.getFirstNotEmptyChildNode(this.msgFactory, body, false);
//					if(element instanceof Element) {
//						this.rootElement = (Element) element;
//					}
//				}
				
			}
			
			if(this.fault==null && elementAfterBody==null) {
				if(this.parsingComplete) { // siamo arrivato alla chiusura del body o dell'envelope
					this.bodyEmpty=true;
				}
			}
			
			
			//System.out.println("parsingComplete["+this.parsingComplete+"] charset["+charset+"] startHeaderOffset["+this.startHeaderOffset+"] < endHeaderOffset["+this.endHeaderOffset+"] < startBodyOffset["+this.startBodyOffset+"]");
			if(this.parsingComplete) {
				if(charset==null || !charset.startsWith("UTF-16")) {
					if(this.startHeaderOffset>0 && this.startBodyOffset>=this.startHeaderOffset) {
						this.soapHeaderOptimizable = true;
						if(this.endHeaderOffset>0) {
							if(this.endHeaderOffset<this.startHeaderOffset || this.endHeaderOffset>this.startBodyOffset) {
								// inconsistenza
								this.soapHeaderOptimizable = false;
							}
						}
					}
				}
			}
			
		}catch(Throwable t) {
			this.tBuffered = new MessageException(t.getMessage(),t);
			throw this.tBuffered;
		}
		
		return;
		
	}
	
	private boolean isOpenedElement(String s, String prefix) {
		if(prefix.startsWith(":")) {
			return s.contains(prefix+" ") || s.contains(prefix+">") || s.contains(prefix+"/")  || s.contains(prefix+"\n") || s.contains(prefix+"\t");
		}
		else {
			return s.startsWith(prefix+" ") || s.startsWith(prefix+">") || s.startsWith(prefix+"/")  || s.startsWith(prefix+"\n") || s.startsWith(prefix+"\t");
		}
	}
	private boolean isClosedElement(String s, String prefix) {
		return s.startsWith(prefix+" ") || s.startsWith(prefix+">") || s.startsWith(prefix+"\n") || s.startsWith(prefix+"\t");
	}
	
	private boolean analizyEnvelopeNamespace(String envelope) throws Exception {
		if(envelope!=null) {
			String s = null;
			if(envelope.endsWith("/>")) {
				s = envelope;
			}
			else {
				s = envelope.replace(">", "/>");
			}
			
			//Element e = XMLUtils.getInstance().newElement(s.getBytes());
			//this.namespace = e.getNamespaceURI();
			//System.out.println("ENVELOPE PARSE ["+envelope+"]");
			SAXParser saxParser = null;
			try {
				saxParser = getParser();
				XMLReader xmlReader = saxParser.getXMLReader();
				SoapEnvelopeSaxContentHandler saxHandler = new SoapEnvelopeSaxContentHandler();
				xmlReader.setContentHandler(saxHandler);
				try(ByteArrayInputStream bin = new ByteArrayInputStream(s.getBytes())){
					InputSource inputSource = new InputSource(bin);
					xmlReader.parse(inputSource);
				}
				this.namespace = saxHandler.getNamespace();
				//System.out.println("ENVELOPE PARSE ["+envelope+"], found namespace: "+this.namespace);
			}
			catch(Throwable t) {
				throw new Exception("Invalid content ("+s+"): "+t.getMessage(),t);
			}
			finally {
				if(saxParser!=null) {
					returnParser(saxParser);
				}
			}
			
			
			return Costanti.SOAP_ENVELOPE_NAMESPACE.equals(this.namespace) ||
					Costanti.SOAP12_ENVELOPE_NAMESPACE.equals(this.namespace);
		}
		return false;
	}

	public String getEnvelope() {
		return this.envelope;
	}

	public String getNamespace() {
		return this.namespace;
	}
	
	public MessageType getMessageType() {
		if(Costanti.SOAP_ENVELOPE_NAMESPACE.equals(this.namespace)) {
			return MessageType.SOAP_11;
		}
		else {
			return MessageType.SOAP_12;
		}
	}

	public boolean isParsingComplete() {
		return this.parsingComplete;
	}
	
//	public String getBody() {
//		return this.body;
//	}

	public boolean isFault() {
		return this.fault!=null;
	}
	
	public boolean isEmpty() {
		return this.bodyEmpty;
	}
	
	public int getBufferSize() {
		return this.bufferSize;
	}
	
	public OpenSPCoop2Message getHeader_OpenSPCoop2Message() throws MessageException {
		boolean checkIsEmpty = true;
		SOAPHeader s = _getHeader(checkIsEmpty, false); // perchè la struttura del soap header viene mantenuta anche vuota, per preservare i commenti.
		if(s!=null) {
			return this._headerMsgCompleto;
		}
		return null;
	}
	public SOAPHeader getHeader() throws MessageException {
		return _getHeader(false, false);
	}
	public SOAPHeader getModifiableHeader() throws MessageException {
		this.soapHeaderModified = true;
		return _getHeader(false, false);
	}
	public SOAPHeader addHeader() throws MessageException {
		return _getHeader(false, true);
	}
	public boolean isSoapHeaderModified() {
		return this.soapHeaderModified;
	}
	public void setSoapHeaderModified(boolean soapHeaderModified) {
		this.soapHeaderModified = soapHeaderModified;
	}
	private SOAPHeader _getHeader(boolean checkIsEmpty, boolean buildIfEmpty) throws MessageException {
		
		SOAPHeader soapHeader = null;
		
		if(this._headerMsgCompleto!=null) {
			try{
				soapHeader = this._headerMsgCompleto.castAsSoap().getSOAPHeader();
			}catch(Throwable t) {
				throw SoapUtils.buildMessageException("Error during access header: "+t.getMessage(),t);
			}
		}
		else {
			if(this._headerMsgCompleto==null && this.header!=null) {
				try {
					//System.out.println("COSTRUISCO ["+this.header+"]");
					this._headerMsgCompleto = buildOp2Message(this.header.getBytes(), this.getMessageType());
					soapHeader = this._headerMsgCompleto.castAsSoap().getSOAPHeader();
										
					if(soapHeader!=null) {
						if("".equals(this.prefixEnvelope)) {
							this._headerMsgCompleto.castAsSoap().getSOAPPart().getEnvelope().setPrefix("");
						}
						if("".equals(this.prefixHeader)) {
							soapHeader.setPrefix("");
						}
						//System.out.println("HEADER COSTRUITO DA QUANTO LETTO CON SAAJ PREFIX ["+soapHeader.getPrefix()+"], LETTO: "+this.header);
					}
					
					//this._header = XMLUtils.getInstance().newElement(this.header.getBytes());
					
					this.header = null; // libero memoria
					
				}catch(Throwable t) {
					throw SoapUtils.buildMessageException("Invalid header ("+this.header+"): "+t.getMessage(),t);
				}
			}
			
			if(this._headerMsgCompleto==null && buildIfEmpty) {
				try {
					String xmlns = "";
					if(this.prefixEnvelope!=null && !"".equals(this.prefixEnvelope)) {
						xmlns=":"+this.prefixEnvelope;
						if(xmlns.endsWith(":") && xmlns.length()>1) {
							xmlns = xmlns.substring(0, xmlns.length()-1);
						}
					}
					String envelope = "<"+this.prefixEnvelope+"Envelope xmlns"+xmlns+"=\""+this.namespace+"\"><"+this.prefixEnvelope+"Header/></"+this.prefixEnvelope+"Envelope>";
					this._headerMsgCompleto = buildOp2Message(envelope.getBytes(), this.getMessageType());
					soapHeader = this._headerMsgCompleto.castAsSoap().getSOAPHeader();
					if("".equals(xmlns)) {
						this._headerMsgCompleto.castAsSoap().getSOAPPart().getEnvelope().setPrefix("");
						soapHeader.setPrefix("");
					}
					//System.out.println("HEADER CREATO CON SAAJ PREFIX ["+soapHeader.getPrefix()+"]");
					this.soapHeaderModified = true;
				}catch(Throwable t) {
					throw SoapUtils.buildMessageException("Build header failed: "+t.getMessage(),t);
				}
			}
		}
		
		if(soapHeader!=null && checkIsEmpty) {
			Node n = SoapUtils.getFirstNotEmptyChildNode(OpenSPCoop2MessageFactory.getDefaultMessageFactory(), soapHeader, false);
			if(n==null) {
				soapHeader = null;
			}
		}
		
		/*if(soapHeader!=null) {
			try {
				TransformerConfig xmlConfig = new TransformerConfig();
				xmlConfig.setOmitXMLDeclaration(true);
				xmlConfig.setCharset(this.isCharset);
				System.out.println("COSTRUITO ["+org.openspcoop2.message.xml.XMLUtils.getInstance(OpenSPCoop2MessageFactory.getDefaultMessageFactory()).toString(soapHeader, xmlConfig)+"]");
				org.w3c.dom.NodeList l = soapHeader.getChildNodes();
				for (int i = 0; i < l.getLength(); i++) {
					System.out.println("BEFORE ["+l.item(i).getClass().getName()+"] ["+l.item(i).getLocalName()+"]");
				}
			}catch(Throwable t) {
				System.out.println("ERRORE");
			}
		}*/
		
		return soapHeader;
	}
	public void clearHeader() {
		this._headerMsgCompleto=null;
		this.soapHeaderOptimizable=false;
	}


//	public Element getRootElement() {
//		return this.rootElement;
//	}
	public String getRootElementNamespace() {
		return this.rootElementNamespace;
	}
	public String getRootElementLocalName() {
		return this.rootElementLocalName;
	}
	
    private static ThreadLocal<SAXParser> saxParserThreadLocal = 
	            new ThreadLocal<SAXParser>() {
	        @Override
			protected SAXParser initialValue() {
	        	try{
	    			SAXParserFactory saxFactory = XMLUtils.getInstance().getSAXParserFactory();
//	    	        try {
//	    	        	saxFactory.setFeature("jdk.xml.resetSymbolTable", true);
//	    	        } catch(Throwable e) {
//	    	        }
	    	        saxFactory.setNamespaceAware(true);
	    	        SAXParser parser = saxFactory.newSAXParser();
	    	        return parser;
				}catch(Throwable t){
					throw new RuntimeException("Inizializzazione SAXParser fallita: "+t.getMessage(),t);
				}
	        }
	};
	private static SAXParser getParser() {
		return saxParserThreadLocal.get();
	}
    		
    private static void returnParser(SAXParser saxParser) {
        saxParser.reset();
    }
    
    private static OpenSPCoop2Message buildOp2Message(byte[] bytes, MessageType messageType) throws Exception {
		MimeHeaders mhs = new MimeHeaders();
		mhs.addHeader(HttpConstants.CONTENT_TYPE, MessageType.SOAP_11.equals(messageType) ? HttpConstants.CONTENT_TYPE_SOAP_1_1 : HttpConstants.CONTENT_TYPE_SOAP_1_2);
		OpenSPCoop2Message msg = null;
		try(ByteArrayInputStream bin = new ByteArrayInputStream(bytes)){
			if(MessageType.SOAP_11.equals(messageType)){
				msg = new OpenSPCoop2Message_saaj_11_impl(OpenSPCoop2MessageFactory.getDefaultMessageFactory(), mhs, bin);
			}
			else {
				msg = new OpenSPCoop2Message_saaj_12_impl(OpenSPCoop2MessageFactory.getDefaultMessageFactory(), mhs, bin);
			}
		}
		return msg;
    }
    
	public boolean isSoapHeaderOptimizable() {
		return SOAP_HEADER_OPTIMIZATION_ENABLED && this.soapHeaderOptimizable;
	}
	
	public void writeOptimizedHeaderTo(InputStream is, OutputStream osParam, boolean consumeHeader) throws Exception {
		if(!this.soapHeaderOptimizable) {
			throw new Exception("SOAPHeader not optimizable");
		}
		
		if(this._headerMsgCompleto==null) {
			throw new Exception("SOAPMessage Optimized undefined");
		}
		SOAPHeader soapHeader = this._headerMsgCompleto.castAsSoap().getSOAPHeader();
		if(soapHeader==null) {
			throw new Exception("SOAPHeader undefined");
		}
		
		TransformerConfig xmlConfig = new TransformerConfig();
		xmlConfig.setOmitXMLDeclaration(true);
		xmlConfig.setCharset(this.isCharset);
				
		long indexBody = this.startBodyOffset;
		if(this.endHeaderOffset>0) {
			indexBody = this.endHeaderOffset+1;
		}
		
		// first
		byte[] buffer = new byte[Utilities.DIMENSIONE_BUFFER];
		int letti = 0;
		long index = 0;
		boolean writeHeader = false;
		
		boolean debug = false;
		OutputStream os = null;
		if(debug) {
			os = new ByteArrayOutputStream();
		}
		else {
			os = osParam;
		}
		while( (letti=is.read(buffer, 0, Utilities.DIMENSIONE_BUFFER)) != -1 ){
			
			if(index<this.startHeaderOffset) {
				for (int i=0; i < letti; i++) {
					if(!writeHeader || index>=indexBody) {
						os.write(buffer[i]);
					}
					index++;
					
					if(index==this.startHeaderOffset) {
						if(debug) {
							os.flush();
							//System.out.println("SCRITTO FINO A HEADER ["+os.toString()+"]");
						}
						
						//os.write(OpenSPCoop2MessageFactory.getAsByte(OpenSPCoop2MessageFactory.getDefaultMessageFactory(), this._header, true));
						os.write(org.openspcoop2.message.xml.XMLUtils.getInstance(OpenSPCoop2MessageFactory.getDefaultMessageFactory()).toByteArray(soapHeader, xmlConfig));
						writeHeader = true;
						if(debug) {
							os.flush();
							//System.out.println("SCRITTO HEADER ["+os.toString()+"]");
						}
					}
				}
			}
			else {
				if(!writeHeader) {
					//os.write(OpenSPCoop2MessageFactory.getAsByte(OpenSPCoop2MessageFactory.getDefaultMessageFactory(), this._header, true));
					os.write(org.openspcoop2.message.xml.XMLUtils.getInstance(OpenSPCoop2MessageFactory.getDefaultMessageFactory()).toByteArray(soapHeader, xmlConfig));
					writeHeader = true;
					if(debug) {
						os.flush();
						//System.out.println("SCRITTO HEADER GIRO SUCCESSIVO ["+os.toString()+"]");
					}
				}
				if(index>=indexBody) {
					os.write(buffer, 0, letti);
					index = index+letti;
				}
				else {
					for (int i=0; i < letti; i++) {
						if(index>=indexBody) {
							os.write(buffer[i]);
						}
						index++;
					}
				}
				if(debug) {
					os.flush();
					//System.out.println("SCRITTO PEZZO ["+os.toString()+"]");
				}
			}
			
		}
				
		if(debug) {
			os.flush();
			//System.out.println("SCRITTO TUTTO ["+os.toString()+"]");
		}
		
		
		// flush
		os.flush();
		
		// ** rilascio memoria **
		if(consumeHeader) {
			this._headerMsgCompleto = null;
			this.soapHeaderOptimizable=false;
			this.bufferedInputStream = null;
		}
	}
}
