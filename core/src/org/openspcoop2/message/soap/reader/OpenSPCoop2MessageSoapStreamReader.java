/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it). 
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
import org.openspcoop2.utils.resources.Charset;
import org.openspcoop2.utils.transport.http.ContentTypeUtilities;
import org.openspcoop2.utils.transport.http.HttpConstants;
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
	private String header;
	private OpenSPCoop2Message _headerMsgCompleto;
	private SOAPHeader _header;
	private String body;
	private boolean bodyEmpty;
	//private Element rootElement;
	private String rootElementNamespace;
	private String rootElementLocalName;
	private String fault;
	private String namespace;
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
				if(Charset.UTF_8.getValue().equalsIgnoreCase(charset)) {
					charset = null;
				}
			}
		}catch(Throwable t) {}
		
		try {
			ByteArrayOutputStream bufferReaded = new ByteArrayOutputStream();
			int lettiBuffer = 0;
			
			StringBuilder sbActual = null;
			byte[] bufferRaw = new byte[1024];
			int kbLetti = 0;
			
			boolean envelopeFound = false; 
			String prefixEnvelope = null;
			
			StringBuilder header = null;
			String headerClosure = null;
			boolean headerCompletato = false;
			
			boolean bodyFound = false;
			String prefixBody = null;
			
			String elementAfterBody = null;
			
			boolean cdataFound = false;
			
			boolean analisiTerminata = false;
			
//			String check = org.openspcoop2.utils.Utilities.getAsString(this.is, "UTF8");
//			System.out.println("STREAM: "+check);
//			this.is = new java.io.ByteArrayInputStream(check.getBytes());
			
			while ( (lettiBuffer=this.is.read(bufferRaw)) !=-1) {
				
				// bufferizzo (1024 byte = 1kb alla volta)
				bufferReaded.write(bufferRaw, 0, lettiBuffer);
				kbLetti++;
			
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
					if( (c == '<' || (sbActual!=null && sbActual.toString().startsWith("&lt;"))) 
							&& 
							!cdataFound) {
												
						if(sbActual!=null) {
							if(sbActual.length()>0) {
								
								// devo verificare se ho incontrato un cdata o sto continuando a leggere un header
								String sPreClosure = sbActual.toString();
								sbActual.delete(0, sbActual.length());
								
								if(sPreClosure.startsWith("<![CDATA[")) {
									//System.out.println("FOND CDATA! in sPreClosure");
									cdataFound=true;
								}
								
							}
						}
						else {
							sbActual = new StringBuilder();
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
							sbActual.delete(0, sbActual.length());
							//System.out.println("continuo perche CDATA... ");
							if(s.endsWith("]]>")) {
								cdataFound=false;
								//System.out.println("FOUND FINE CDATA");
							}
							continue;
						}
						if(s.startsWith("<![CDATA[")) {
							//System.out.println("FOND CDATA!");
							cdataFound=true;
							continue;
						}
						
						if(s.startsWith("<!") || s.startsWith("<?")) {
							continue;
						}
						
						if(s.startsWith("<Envelope") && !envelopeFound) {
							this.envelope = s;
							if(!analizyEnvelopeNamespace(this.envelope)) {
								analisiTerminata=true;
								break;
							}
							envelopeFound = true;
							prefixEnvelope = "";
						}
						else if(s.contains(":Envelope") && !envelopeFound) {
							String sPrefix = s.substring(0, s.indexOf(":Envelope"));
							if(sPrefix.startsWith("<") && !sPrefix.contains(" ") && sPrefix.length()>1) {
								this.envelope = s;
								if(!analizyEnvelopeNamespace(this.envelope)) {
									analisiTerminata=true;
									break;
								}
								envelopeFound = true;
								prefixEnvelope = sPrefix.substring(1)+":";
							}
							else {
								analisiTerminata=true;
								break;
							}
						}
						else if(envelopeFound) {
						
							if(headerClosure!=null) {
								//System.out.println("AGGIUNGO AD HEADER ["+s+"]");
								header.append(s);
								sbActual.delete(0, sbActual.length());
								if(s.startsWith(headerClosure)) {
									headerClosure=null;	
									headerCompletato = true;
								}
								continue;
							}
							
							if(s.startsWith("<Header")) {
								header = new StringBuilder(s);
								if(!s.endsWith("/>")) {
									headerClosure = "</Header";
								}
								if(bodyFound) {
									analisiTerminata=true;
									break;
								}
							}
							else if(s.contains(":Header")) {
								String sPrefix = s.substring(0, s.indexOf(":Header"));
								if(sPrefix.startsWith("<") && !sPrefix.contains(" ") && sPrefix.length()>1) {
									header = new StringBuilder(s);
									if(!s.endsWith("/>")) {
										headerClosure = "</"+sPrefix.substring(1)+":"+"Header";
									}
									if(bodyFound) {
										analisiTerminata=true;
										break;
									}
								}
								else {
									analisiTerminata=true;
									break;
								}
							}
							else if(s.startsWith("<Body")) {
								this.body = s;
								bodyFound = true;
								prefixBody = "";
							}
							else if(s.contains(":Body")) {
								String sPrefix = s.substring(0, s.indexOf(":Body"));
								if(sPrefix.startsWith("<") && !sPrefix.contains(" ") && sPrefix.length()>1) {
									this.body = s;
									bodyFound = true;
									prefixBody = sPrefix.substring(1)+":";
								}
								else {
									analisiTerminata=true;
									break;
								}
							}
							else if(bodyFound) {
								if(s.startsWith("<Fault")) {
									this.fault = s;
								}
								else if(s.contains(":Fault")) {
									String sPrefix = s.substring(0, s.indexOf(":Fault"));
									if(sPrefix.startsWith("<") && !sPrefix.contains(" ")) {
										this.fault = s;
									}
								}
								else {
									
									String closeBody = "</"+prefixBody+"Body";
									String closeEnvelope = "</"+prefixEnvelope+"Envelope";
									if(s.startsWith(closeBody) || s.startsWith(closeEnvelope)) {
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
			
			if(header!=null && header.length()>0) {
				if(this.envelope.endsWith("/>")) {
					if(throwException) {
						throw new Exception("Invalid content; found 'Header' after envelope closure '/>': ("+header+")");
					}
					else{
						return;
					}
				}
				if(headerCompletato) {
					StringBuilder sbHeaderAnalizer = new StringBuilder();
					sbHeaderAnalizer.append(this.envelope);
					sbHeaderAnalizer.append(header.toString());
					sbHeaderAnalizer.append("<").append(prefixEnvelope).append("Body/>");
					sbHeaderAnalizer.append("</").append(prefixEnvelope).append("Envelope>");
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
				sbFaultAnalizer.append("</").append(prefixEnvelope).append("Envelope>");
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
				sbElementAnalizer.append("</").append(prefixEnvelope).append("Envelope>");
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
			
		}catch(Throwable t) {
			this.tBuffered = new MessageException(t.getMessage(),t);
			throw this.tBuffered;
		}
		
		return;
		
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
			//System.out.println("ENVELOPE ["+envelope+"]");
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
		return getHeader_OpenSPCoop2Message(true);
	}
	private OpenSPCoop2Message getHeader_OpenSPCoop2Message(boolean checkIsEmpty) throws MessageException {
		_getHeader(checkIsEmpty);
		return this._headerMsgCompleto;
	}
	public SOAPHeader getHeader() throws MessageException {
		return _getHeader(true);
	}
	private SOAPHeader _getHeader(boolean checkIsEmpty) throws MessageException {
		if(this._header==null && this.header!=null) {
			try {
				//System.out.println("COSTRUISCO ["+this.header+"]");
				this._headerMsgCompleto = buildOp2Message(this.header.getBytes(), this.getMessageType());
				this._header = this._headerMsgCompleto.castAsSoap().getSOAPHeader();
				
				if(checkIsEmpty) {
					Node n = SoapUtils.getFirstNotEmptyChildNode(OpenSPCoop2MessageFactory.getDefaultMessageFactory(), this._header, false);
					if(n==null) {
						this._header = null;
						this._headerMsgCompleto = null;
					}
				}
				
				//this._header = XMLUtils.getInstance().newElement(this.header.getBytes());
			}catch(Throwable t) {
				throw SoapUtils.buildMessageException("Invalid header ("+this.header+"): "+t.getMessage(),t);
			}
		}
		return this._header;
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
}
