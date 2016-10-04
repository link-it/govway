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


package org.openspcoop2.testsuite.clients;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.security.KeyStore;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.xml.soap.SOAPException;

import org.apache.axis.AxisFault;
import org.apache.axis.Message;
import org.apache.axis.encoding.Base64;
import org.apache.axis.message.MessageElement;
import org.apache.axis.message.SOAPFault;
import org.apache.axis.transport.http.HTTPConstants;
import org.openspcoop2.message.Costanti;
import org.openspcoop2.message.SOAPVersion;
import org.openspcoop2.message.XMLUtils;
import org.openspcoop2.utils.resources.SSLHostNameVerifierDisabled;
import org.openspcoop2.pdd.core.connettori.ConnettoreHTTPSProperties;
import org.openspcoop2.testsuite.axis14.Axis14DynamicNamespaceContextFactory;
import org.openspcoop2.testsuite.core.CostantiTestSuite;
import org.openspcoop2.testsuite.core.TestSuiteException;
import org.openspcoop2.testsuite.core.Repository;
import org.openspcoop2.testsuite.core.SOAPEngine;
import org.openspcoop2.testsuite.core.Utilities;
import org.openspcoop2.utils.xml.AbstractXPathExpressionEngine;
import org.openspcoop2.utils.xml.DynamicNamespaceContext;
import org.openspcoop2.utils.xml.XPathExpressionEngine;
import org.openspcoop2.utils.xml.XPathNotFoundException;
import org.openspcoop2.utils.xml.XPathReturnType;
import org.w3c.dom.Comment;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

/**
 * Client HTTP Generico.
 * 
 * @author Andi Rexha (rexha@openspcoop.org)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class ClientHttpGenerico extends ClientCore{


	/** HTTP Method */
	private String method="POST";
	/** HTTP Url Connection */
	private HttpURLConnection conn;
	/** Gestione della Risposta */
	private boolean rispostaDaGestire=false;
	/** Codice Ritornato */
	private int codiceStatoHTTP;
	/** SoapAction */
	private String soapAction;
	/** Riferimento Messaggio */
	private String riferimentoMessaggio;
	/** MessaggioXML in byte */
	private byte[] messaggioXMLRichiesta;
	private byte[] messaggioXMLRisposta;
	private boolean forceResponseAsSOAPProcessor;
	/** ContentType */
	private String contentType;
	/** ContentType Risposta*/
	private String contentTypeRisposta;
	/** Proprieta' del trasporto della risposta */
	private java.util.Properties propertiesTrasportoRisposta;
	/** Timeout */
	private int connectTimedOut;
	private int connectionReadTimeout;
	
	/** SSL Configuration */
	private boolean https = false;
	private ConnettoreHTTPSProperties sslContextProperties;
	
	
	
	/** Costruttore */
	public ClientHttpGenerico(Repository rep,java.util.Hashtable<String, String> sslContext) throws TestSuiteException {
		super();
		this.repository=rep;
		this.method="POST";
		this.soapAction="\"TestsuiteOpenspcoop\"";
		if(sslContext!=null){
			this.https = true;
			try{
				this.sslContextProperties = ConnettoreHTTPSProperties.readProperties(sslContext);
			}catch(Exception e){
				throw new TestSuiteException("[HTTPS error]"+ e.getMessage());
			}
		}
	}
	/** Costruttore */
	public ClientHttpGenerico(Repository rep)throws TestSuiteException{
		this(rep,null);
	}
	public ClientHttpGenerico(java.util.Hashtable<String, String> sslContext)throws TestSuiteException{
		this(new Repository(),sslContext);
	}
	public ClientHttpGenerico()throws TestSuiteException{
		this(new Repository(),null);
	}

	
	
	/** SORGENTI PER IL MESSAGGIO DI RICHIESTA */

	public void addIDUnivoco(){
		try{
			if(this.sentMessage.getSOAPBody()!=null){
				MessageElement m = new MessageElement("idUnivoco", "test","http://www.openspcoop.org");
				m.setValue("ID-"+SOAPEngine.getIDUnivoco());
				this.sentMessage.getSOAPBody().addChildElement(m);
			}
		} catch (Exception e) {
			System.out.println("Errore durante la generazione dell'id unico: "+e.getMessage());
		}
	}
	
	/**
	 * Setta il messaggio soap da un file.
	 * @param fileName il nome del file da prelevare il messaggio
	 * @param isBody se il valore e' true, nel file si trova solo il soap body del messaggio, il metodo imbusta.
	 */
	@Override
	public void setMessageFromFile(String fileName,boolean isBody,boolean generaIDUnivoco){
		File fileToSend=new File(fileName);
		try {
			FileInputStream in = new FileInputStream(fileToSend);
			this.sentMessage=new Message(in,isBody);
			// save changes.
			// N.B. il countAttachments serve per il msg con attachments come saveMessage!
			if(this.sentMessage.countAttachments()==0){
				this.sentMessage.getSOAPPartAsBytes();
			}
			if(generaIDUnivoco)
				addIDUnivoco();
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
			throw new TestSuiteException(e.getMessage());
		}
	}
	@Override
	public void setMessageFromFile(String fileName,boolean isBody) throws TestSuiteException{
		setMessageFromFile(fileName,isBody,false);
	}


	/**
	 * Setta il messaggio con un messaggio axis Message. In questo metodo il messaggio deve essere senza attachement
	 * @param msg
	 */
	@Override
	public void setMessage(Message msg,boolean generaIDUnivoco){
		this.sentMessage = msg;
		if(generaIDUnivoco)
			addIDUnivoco();
	}
	@Override
	public void setMessage(Message msg){
		setMessage(msg,false);
	}

	/**
	 * Il messaggio da mandare viene specificato da un file. Se il file possiede anche le mime header http , viene
	 * costruita una copia del messaggio.
	 * @param fileName nome del file
	 * @throws IOException lancia un Ecezione di tipo IO
	 */
	@Override
	public void setMessageWithAttachmentsFromFile(String fileName, boolean generaIDUnivoco,boolean soapBodyEmpty) throws IOException{
		this.sentMessage=Utilities.createMessageWithAttachmentsFromFile(fileName, soapBodyEmpty);
		if(generaIDUnivoco)
			addIDUnivoco();
	}
	@Override
	public void setMessageWithAttachmentsFromFile(String fileName, boolean generaIDUnivoco) throws IOException{
		setMessageWithAttachmentsFromFile(fileName,generaIDUnivoco,false);
	}
	@Override
	public void setMessageWithAttachmentsFromFile(String fileName) throws IOException{
		setMessageWithAttachmentsFromFile(fileName,false,false);
	}
	
	
	/** METODI DI CONFIGURAZIONE DEL TRASPORTO HTTP */

	/** Imposta il metodo http utilizzato */
	public void setMethod(String method){
		this.method=method;
	}
	/**
	 * Imposta una proprieta' nell'header http.
	 * 
	 * @param id
	 * @param value
	 */
	public void setProperty(String id,String value){
		this.conn.setRequestProperty(id, value);
	}
	public void setProperties(Properties p){
		if(p!=null){
			Enumeration<?> keys = p.keys();
			while (keys.hasMoreElements()) {
				String id = (String) keys.nextElement();
				this.conn.setRequestProperty(id, p.getProperty(id));		
			}
		}
	}

	/**
	 * Setta username e password
	 * @param user
	 * @param passw
	 */
	@Override
	public void setAutenticazione(String user,String passw){
		String autent = null;
		if(user!=null && passw!=null){
			autent = user + ":" + passw;
			autent = "Basic " + Base64.encode(autent.getBytes());
			//System.out.println("CODE["+authentication+"]");
		}
		if(autent!=null)
			this.conn.setRequestProperty("Authorization",autent);
	}
	
	
	
	/**
	 * Crea il soapEngine utilizzato per la gestione della richiesta SOAP.
	 * Deve essere chiamato dopo aver impostato la url della Porta di Dominio, e la porta delegata da invocare
	 */
	@Override
	public void connectToSoapEngine() throws TestSuiteException{
		this.connectToSoapEngine(SOAPVersion.SOAP11);
	}
	@Override
	public void connectToSoapEngine(SOAPVersion soapVersion) throws TestSuiteException {
		FileInputStream finKeyStore = null;
		FileInputStream finTrustStore = null;
		try{
			// Gestione https
			SSLContext sslContext = null;
			if(this.https){
				this.log.debug("Creo contesto ssl...");
				KeyManager[] km = null;
				TrustManager[] tm = null;
				
				// Autenticazione CLIENT
				if(this.sslContextProperties.getKeyStoreLocation()!=null){
					this.log.debug("Gestione keystore...");
					KeyStore keystore = KeyStore.getInstance(this.sslContextProperties.getKeyStoreType()); // JKS,PKCS12,jceks,bks,uber,gkr
					finKeyStore = new FileInputStream(this.sslContextProperties.getKeyStoreLocation());
					keystore.load(finKeyStore, this.sslContextProperties.getKeyStorePassword().toCharArray());
					KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(this.sslContextProperties.getKeyManagementAlgorithm());
					keyManagerFactory.init(keystore, this.sslContextProperties.getKeyPassword().toCharArray());
					km = keyManagerFactory.getKeyManagers();
					this.log.debug("Gestione keystore effettuata");
				}
				
				
				// Autenticazione SERVER
				if(this.sslContextProperties.getTrustStoreLocation()!=null){
					this.log.debug("Gestione truststore...");
					KeyStore truststore = KeyStore.getInstance(this.sslContextProperties.getTrustStoreType()); // JKS,PKCS12,jceks,bks,uber,gkr
					finTrustStore = new FileInputStream(this.sslContextProperties.getTrustStoreLocation());
					truststore.load(finTrustStore, this.sslContextProperties.getTrustStorePassword().toCharArray());
					TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(this.sslContextProperties.getTrustManagementAlgorithm());
					trustManagerFactory.init(truststore);
					tm = trustManagerFactory.getTrustManagers();
					this.log.debug("Gestione truststore effettuata");
				}
				
				// Creo contesto SSL
				sslContext = SSLContext.getInstance(this.sslContextProperties.getSslType());
				sslContext.init(km, tm, null);				
			}
			
			// URL
			if(this.portaDelegata==null)
				this.portaDelegata="";
			URL url=new URL(this.urlPortaDiDominio+this.portaDelegata);
			URLConnection connection = url.openConnection();
			HttpURLConnection httpConn = (HttpURLConnection) connection;
			this.conn = httpConn;
			
			// Imposta Contesto SSL se attivo
			if(this.https){
				HttpsURLConnection httpsConn = (HttpsURLConnection) this.conn;
				httpsConn.setSSLSocketFactory(sslContext.getSocketFactory());
				
				if(this.sslContextProperties.isHostnameVerifier()){
					if(this.sslContextProperties.getClassNameHostnameVerifier()!=null){
						this.log.debug("HostNamve verifier enabled ["+this.sslContextProperties.getClassNameHostnameVerifier()+"]");
						Class<?> c = Class.forName(this.sslContextProperties.getClassNameHostnameVerifier());
						HostnameVerifier verifica = (HostnameVerifier) c.newInstance();
						httpsConn.setHostnameVerifier(verifica);
					}else{
						this.log.debug("HostNamve verifier enabled");
					}
				}else{
					this.log.debug("HostNamve verifier disabled");
					SSLHostNameVerifierDisabled disabilitato = new SSLHostNameVerifierDisabled(this.log);
					httpsConn.setHostnameVerifier(disabilitato);
				}
			}
			
		}catch(Exception e){
			this.log.error("La connessione HTTP non e' riuscita: "+e.getMessage(),e);
			throw new TestSuiteException("La connessione HTTP non e' riuscita:  "+e.getMessage());
		}finally{
			try{
				if(finKeyStore!=null){
					finKeyStore.close();
				}
			}catch(Exception e){}
			try{
				if(finTrustStore!=null){
					finTrustStore.close();
				}
			}catch(Exception e){}
		}
	}

	
	/**
	 * Impostazione se la risposta deve essere gestita
	 * 
	 * @param value
	 */
	public void setRispostaDaGestire(boolean value){
		this.rispostaDaGestire=value;
	}
	/**
	 * Indicazione se la risposta deve essere gestita
	 */
	public boolean isRispostaDaGestire(){
		return this.rispostaDaGestire;
	}
	
	/**
	 * Ritorna il codice di stato HTTP
	 * 
	 * @return codiceStatoHTTP
	 */
	public int getCodiceStatoHTTP() {
		return this.codiceStatoHTTP;
	}
	
		
	public static final String SOAP_ACTION_VALUE_NULL = "@SoapActionNull@";
	
	public void run() throws Exception, SOAPException{
		
		// Check parametri obbligatori
		if(this.testsuiteProperties.getIdMessaggioTrasporto()==null)
			throw new TestSuiteException("Nome dell'header contenente l'id nella riposta http, non definito");
				
		// Impostazione parametri di connessione
		this.conn.setRequestMethod(this.method);
		this.conn.setDoInput(true);
		this.conn.setDoOutput(true);
		
		// Impostazione Content-Type della Spedizione su HTTP
		String contentTypeSpedito = null;
		if(this.contentType!=null){
			contentTypeSpedito = this.contentType;
		}else if(this.sentMessage!=null){
			if(SOAPVersion.SOAP11.equals(this.soapVersion)){
				contentTypeSpedito = this.sentMessage.getContentType(new org.apache.axis.soap.SOAP11Constants());	
			}
			else if(SOAPVersion.SOAP12.equals(this.soapVersion)){
				contentTypeSpedito = this.sentMessage.getContentType(new org.apache.axis.soap.SOAP12Constants());	
			}
			else{
				// default 
				contentTypeSpedito = this.sentMessage.getContentType(new org.apache.axis.soap.SOAP11Constants());	
			}
		}else{
			if(SOAPVersion.SOAP11.equals(this.soapVersion)){
				contentTypeSpedito = Costanti.CONTENT_TYPE_SOAP_1_1;
			}
			else if(SOAPVersion.SOAP12.equals(this.soapVersion)){
				contentTypeSpedito = Costanti.CONTENT_TYPE_SOAP_1_2;
			}
			else{
				// default
				//contentTypeSpedito = "text/xml; charset=utf-8";
				contentTypeSpedito = Costanti.CONTENT_TYPE_SOAP_1_1;
			}
		}
		this.conn.setRequestProperty("Content-Type",contentTypeSpedito);
		
		// Timeout per verificare eventuali problemi dovuti al content length
		if(this.connectTimedOut>0){
			this.conn.setConnectTimeout(this.connectTimedOut); // attendo X secondi l'istanziazione della connessione
		}else{
			this.conn.setConnectTimeout(CostantiTestSuite.CONNECTION_TIMEOUT); // attendo X secondi l'istanziazione della connessione
		}
		if(this.connectionReadTimeout>0){
			this.conn.setReadTimeout(this.connectionReadTimeout); // attendo X secondi la risposta
		}else{
			this.conn.setReadTimeout(CostantiTestSuite.READ_TIMEOUT); // attendo X secondi la risposta
		}
		
		// SoapAction
		if(this.soapAction!=null){
			if(SOAP_ACTION_VALUE_NULL.equals(this.soapAction)){
				this.conn.setRequestProperty(CostantiTestSuite.SOAP_ACTION,null);	
			}else{
				this.conn.setRequestProperty(CostantiTestSuite.SOAP_ACTION,this.soapAction);
			}
		}
		
		// RiferimentoMessaggio
		if(this.riferimentoMessaggio!=null){
			this.conn.setRequestProperty(this.testsuiteProperties.getRiferimentoAsincronoTrasporto(), this.riferimentoMessaggio);
		}
		
		// spedizione messaggio di richiesta se presente
		if(this.sentMessage!=null){
			this.conn.setRequestProperty("Content-Length",new Long(this.sentMessage.getContentLength()).toString());
			OutputStream out=this.conn.getOutputStream();
			this.sentMessage.writeTo(out);
			out.close();
		}else{
			if(this.messaggioXMLRichiesta!=null){
				this.conn.setRequestProperty("Content-Length",new Long(this.messaggioXMLRichiesta.length).toString());
				OutputStream out=this.conn.getOutputStream();
				out.write(this.messaggioXMLRichiesta);
				out.close();
			}
		}
		
		// Analisi MimeType e ContentLocation della risposta
		Map<String, List<String>> mapHeaderHttpResponse = this.conn.getHeaderFields();
		if(mapHeaderHttpResponse!=null && mapHeaderHttpResponse.size()>0){
			if(this.propertiesTrasportoRisposta==null){
				this.propertiesTrasportoRisposta = new Properties();
			}
			Iterator<String> itHttpResponse = mapHeaderHttpResponse.keySet().iterator();
			while(itHttpResponse.hasNext()){
				String keyHttpResponse = itHttpResponse.next();
				List<String> valueHttpResponse = mapHeaderHttpResponse.get(keyHttpResponse);
				StringBuffer bfHttpResponse = new StringBuffer();
				for(int i=0;i<valueHttpResponse.size();i++){
					if(i>0){
						bfHttpResponse.append(",");
					}
					bfHttpResponse.append(valueHttpResponse.get(i));
				}
				if(keyHttpResponse==null){ // Check per evitare la coppia che ha come chiave null e come valore HTTP OK 200
					keyHttpResponse="ReturnCode";
				}
				this.propertiesTrasportoRisposta.put(keyHttpResponse, bfHttpResponse.toString());
			}
		}
		
		String tipoRisposta = this.conn.getHeaderField(HTTPConstants.HEADER_CONTENT_TYPE);
		if(tipoRisposta==null){
			tipoRisposta = this.conn.getHeaderField(HTTPConstants.HEADER_CONTENT_TYPE.toLowerCase());
		}
		if(tipoRisposta==null){
			tipoRisposta = this.conn.getHeaderField(HTTPConstants.HEADER_CONTENT_TYPE.toUpperCase());
		}
		this.contentTypeRisposta = tipoRisposta;
		String locationRisposta = this.conn.getHeaderField(HTTPConstants.HEADER_CONTENT_LOCATION);
		if(locationRisposta==null){
			locationRisposta = this.conn.getHeaderField(HTTPConstants.HEADER_CONTENT_LOCATION.toLowerCase());
		}
		if(locationRisposta==null){
			locationRisposta = this.conn.getHeaderField(HTTPConstants.HEADER_CONTENT_LOCATION.toUpperCase());
		}
				
		// Gestione codice di risposta
		this.codiceStatoHTTP = this.conn.getResponseCode();
		
		// Lettura risposta
		InputStream is = null;
		String idMessaggio = null;
		if(this.codiceStatoHTTP<300)
			is = this.conn.getInputStream();
		else{
			is = this.conn.getErrorStream();
		}
		if(!this.rispostaDaGestire){
			while(is.read()!=-1){}
		}
		else{
			// Lettura id
			if(this.conn.getHeaderFields().get(this.testsuiteProperties.getIdMessaggioTrasporto())!=null){
				idMessaggio = this.conn.getHeaderFields().get(this.testsuiteProperties.getIdMessaggioTrasporto()).get(0);
				this.repository.add(idMessaggio);
				this.idMessaggio = idMessaggio;
			}
			
			// Ricezione messaggio
			if(is!=null){
				if(this.messaggioXMLRichiesta==null || this.forceResponseAsSOAPProcessor){
					// gestione SOAP
					try{
						this.receivedMessage = new org.apache.axis.Message(is,false,tipoRisposta,locationRisposta);	
					}catch(Exception e){
						throw new TestSuiteException("Errore avvvenuto durante la consegna HTTP (ricezione risposta): " + e.getMessage());
					}
					// save Msg
					try{
						if(this.receivedMessage!=null){
							// save changes.
							// N.B. il countAttachments serve per il msg con attachments come saveMessage!
							if(this.receivedMessage.countAttachments()==0){
								this.receivedMessage.getSOAPPartAsBytes();
							}
						}
					}catch(Exception e){
						throw new TestSuiteException("Errore avvvenuto durante la consegna HTTP (salvataggio risposta): " + e.getMessage());
					}
					// checkConsistenza Msg
					try{
						this.receivedMessage.getSOAPBody();
					}catch(Exception e){
						this.receivedMessage=null;
						// L'errore 'premature end of file' consiste solo nel fatto che una risposta non e' stata ricevuta.
						boolean result2XX = (this.codiceStatoHTTP>=200 && this.codiceStatoHTTP<=299);
						boolean premature = (e.getMessage()!=null && (e.getMessage().indexOf("Premature end of file")!=-1) && result2XX);
						// Se non ho un premature, ed un errore di lettura in 200, allora devo segnalare l'errore, altrimenti comunque 
						// il msg ritornato e' null e nel codiceStato vi e' l'errore.
						if( premature == false ){
							if(result2XX){
								throw new TestSuiteException("Errore avvvenuto durante la consegna HTTP (lettura risposta): " + e.getMessage());
							}
						}
					}
					if(this.receivedMessage!=null&&this.receivedMessage.getSOAPBody()!=null && this.receivedMessage.getSOAPBody().hasFault()){
						SOAPFault fault = (SOAPFault) this.receivedMessage.getSOAPBody().getFault();
						
						Element [] details = null;
						Vector<Element> elementsW3C = new Vector<Element>();
						Iterator<?> childs = this.receivedMessage.getSOAPBody().getChildElements();
						if(childs!=null){
							while(childs.hasNext()){
								Element element = (Element) childs.next();
								
								if("Fault".equals(element.getLocalName())){
									
									NodeList faultChilds = element.getChildNodes();
									if(faultChilds!=null){
										for(int i=0; i<faultChilds.getLength(); i++){
											Node faultChild = faultChilds.item(i);
											if("detail".equals(faultChild.getLocalName())){
												
												NodeList detailChilds = faultChild.getChildNodes();
												if(detailChilds!=null){
													for(int j=0; j<detailChilds.getLength(); j++){
														Node n = detailChilds.item(j);
														byte [] nByte = XMLUtils.getInstance().toByteArray(n,true);
														elementsW3C.add(XMLUtils.getInstance().newElement(nByte));
													}
												}
												
											}
										}
									}
									
								}
							}
						}
						if(elementsW3C.size()>0){
							details = elementsW3C.toArray(new Element[1]);
						}
						
						throw new AxisFault(fault.getFaultCode(),fault.getFaultString(),fault.getFaultActor(),details);
					}
					else{
						if(contentTypeSpedito!=null && contentTypeSpedito.contains("application/soap+xml")){
							
							// Avendo fatto la richiesta con content type soap1.2 può darsi che l'errore ritornato sia in soap 1.2
							
							NodeList list = this.receivedMessage.getSOAPBody().getChildNodes();
							for (int i = 0; i < list.getLength(); i++) {
								Node node = list.item(i);
								if( (!(node instanceof Text)) && (!(node instanceof Comment)) ){
									String namespaceSoapEnvelope12 = "http://www.w3.org/2003/05/soap-envelope";
									String localNameSoapEnvelope12 = "Fault";
									//System.out.println("CHILD ["+node.getLocalName()+"] ["+node.getNamespaceURI()+"]");
									if(localNameSoapEnvelope12.equals(node.getLocalName()) && namespaceSoapEnvelope12.equals(node.getNamespaceURI())){
										try{
											Axis14DynamicNamespaceContextFactory dncFactory = new Axis14DynamicNamespaceContextFactory();
											DynamicNamespaceContext dnc = dncFactory.getNamespaceContextFromSoapEnvelope11(this.receivedMessage.getSOAPEnvelope());
											AbstractXPathExpressionEngine xpathEngine = new XPathExpressionEngine();
											
											/*
											 *   <env:Code>
										            <env:Value>env:Sender</env:Value>
										            <env:Subcode>
										               <env:Value xmlns:eccIntegrazione="http://www.openspcoop2.org/core/errore_integrazione">eccIntegrazione:OPENSPCOOP2_ORG_429</env:Value>
										            </env:Subcode>
										         </env:Code>
											 */
											// Axis1.4 non supporta correttamente la struttura soap1.2
											// workaroung portando in bytes il messaggio
											String xml = org.openspcoop2.utils.xml.XMLUtils.getInstance().toString(node,true);
											
											String faultCode = null;
											try{
												faultCode = xpathEngine.getStringMatchPattern(xml, dnc, "//{"+namespaceSoapEnvelope12+"}:Code/{"+namespaceSoapEnvelope12+"}:Subcode/{"+namespaceSoapEnvelope12+"}:Value/text()");
											}catch(XPathNotFoundException notFound){
												faultCode = xpathEngine.getStringMatchPattern(xml, dnc, "//{"+namespaceSoapEnvelope12+"}:Code/{"+namespaceSoapEnvelope12+"}:Value/text()");
											}
											if(faultCode.contains(":")){
												faultCode = faultCode.split(":")[1];
											}
											if(faultCode.equals("Receiver")){
												faultCode="Server";
											}
											else if(faultCode.equals("Sender")){
												faultCode="Client";
											}
											//System.out.println("FAULT_CODE ["+faultCode+"]");
											
											/*
											 *  <env:Reason>
								            		<env:Text xml:lang="en-US">Il valore dell'header HTTP Content-Type (application/soap+xml) non rientra tra quelli supportati dal protocollo (text/xml, multipart/related)</env:Text>
								         		</env:Reason>
								         	*/
											String faultString = xpathEngine.getStringMatchPattern(xml, dnc, "//{"+namespaceSoapEnvelope12+"}:Reason/{"+namespaceSoapEnvelope12+"}:Text/text()");
											//System.out.println("FAULT_STRING ["+faultString+"]");
											
											/*
											 *  <env:Role>OpenSPCoop2</env:Role>
											 *  
											 */  
											String faultActor = null;
											try{
												faultActor = xpathEngine.getStringMatchPattern(xml, dnc, "//{"+namespaceSoapEnvelope12+"}:Role/text()");
											}catch(XPathNotFoundException notFound){}
											//System.out.println("FAULT_ACTOR ["+faultActor+"]");
											
											/*
											 * <env:Detail>....</env:Detail>
											 */
											Element [] details = null;
											Vector<Element> elementsW3C = new Vector<Element>();
											Object objectDetails = null;
											try{
												objectDetails = xpathEngine.getMatchPattern(xml, dnc, "//{"+namespaceSoapEnvelope12+"}:Detail", XPathReturnType.NODE);
											}catch(XPathNotFoundException notFound){}
											if(objectDetails!=null){
												//System.out.println("DETAIL CLASS ["+objectDetails.getClass().getName()+"]");
												// Le scorro a mano (altrimenti si perdeva il namespace).
												NodeList listFault = node.getChildNodes();
												if(listFault!=null){
													for (int j = 0; j < listFault.getLength(); j++) {
														Node childFault = listFault.item(j);
														if( (!(childFault instanceof Text)) && (!(childFault instanceof Comment)) ){
															if("Detail".equals(childFault.getLocalName()) && namespaceSoapEnvelope12.equals(childFault.getNamespaceURI())){
																NodeList listFaultDetail = childFault.getChildNodes();
																if(listFaultDetail!=null){
																	for (int k = 0; k < listFaultDetail.getLength(); k++) {
																		Node childDetail = listFaultDetail.item(k);
																		if( (!(childDetail instanceof Text)) && (!(childDetail instanceof Comment)) ){
																			org.openspcoop2.utils.xml.XMLUtils xmlUtils = org.openspcoop2.utils.xml.XMLUtils.getInstance();
																			Element newElement = xmlUtils.newElement(xmlUtils.toByteArray(childDetail));
																			// La traduzione sopra è necessaria.
																			elementsW3C.add(newElement);
																			//System.out.println("FOUND ["+childDetail.getNamespaceURI()+"]....["+XMLUtils.getInstance().toString(childDetail,true)+"]");
																		}
																	}
																}
															}
														}
													}
												}
//												if(objectDetails instanceof Element){
//													NodeList detailChilds = ((Element)objectDetails).getChildNodes();
//													if(detailChilds!=null){
//														for(int j=0; j<detailChilds.getLength(); j++){
//															Node n = detailChilds.item(j);
//															byte [] nByte = XMLUtils.getInstance().toByteArray(n,true);
//															//System.out.println("CONVERT ["+n.getNamespaceURI()+"]....["+XMLUtils.getInstance().toString(n,true)+"]");
//															elementsW3C.add(XMLUtils.getInstance().newElement(nByte));
//															//System.out.println("DETAIL ["+j+"]: {"+n.getNamespaceURI()+"}"+n.getLocalName());
//														}
//													}
//												}
											}
											else{
												//System.out.println("DETAIL NON PRESENTE");
											}
											if(elementsW3C.size()>0){
												details = elementsW3C.toArray(new Element[1]);
											}
											
											throw new AxisFault(faultCode,faultString,faultActor,details);
											
										}catch(AxisFault t){
											throw t;
										}catch(Throwable t){
											System.out.println("ERRORE: "+t.getMessage());
											throw new Exception("Conversione SoapFault1.2 in SoapFault1.1 non riuscita: "+t.getMessage(),t);
										}
									}
								} 
							}
						}
					}
				
				}else{
					ByteArrayOutputStream bout = new ByteArrayOutputStream();
					byte[]bytes = new byte[2048];
					int letti = 0;
					while( (letti = is.read(bytes)) != -1 ){
						bout.write(bytes, 0, letti);
					}
					bout.flush();
					bout.close();
					this.messaggioXMLRisposta = bout.toByteArray();
				}
			}
		}
		
		// Gestione finale della connessione
		if(is!=null){
			is.close();
		}
		
		// fine HTTP.
		this.conn.disconnect();
		
		
		if(this.attesaTerminazioneMessaggi){
			if(this.dbAttesaTerminazioneMessaggiFruitore!=null){
				long countTimeout = System.currentTimeMillis() + (1000*this.testsuiteProperties.getTimeoutProcessamentoMessaggiOpenSPCoop());
				while(this.dbAttesaTerminazioneMessaggiFruitore.getVerificatoreMessaggi().countMsgOpenSPCoop(idMessaggio)!=0 && countTimeout>System.currentTimeMillis()){
					try{
					Thread.sleep(this.testsuiteProperties.getCheckIntervalProcessamentoMessaggiOpenSPCoop());
					}catch(Exception e){}
				}
			}
			if(this.dbAttesaTerminazioneMessaggiErogatore!=null){
				long countTimeout = System.currentTimeMillis() + (1000*this.testsuiteProperties.getTimeoutProcessamentoMessaggiOpenSPCoop());
				while(this.dbAttesaTerminazioneMessaggiErogatore.getVerificatoreMessaggi().countMsgOpenSPCoop(idMessaggio)!=0 && countTimeout>System.currentTimeMillis()){
					try{
						Thread.sleep(this.testsuiteProperties.getCheckIntervalProcessamentoMessaggiOpenSPCoop());
					}catch(Exception e){}
				}
			}
		}
	}
	public String getSoapAction() {
		return this.soapAction;
	}
	public void setSoapAction(String soapAction) {
		this.soapAction = soapAction;
	}
	public String getRiferimentoMessaggio() {
		return this.riferimentoMessaggio;
	}
	public void setRiferimentoMessaggio(String riferimentoMessaggio) {
		this.riferimentoMessaggio = riferimentoMessaggio;
	}
	public byte[] getMessaggioXMLRichiesta() {
		return this.messaggioXMLRichiesta;
	}
	public void setMessaggioXMLRichiesta(byte[] messaggioXMLRichiesta) {
		this.messaggioXMLRichiesta = messaggioXMLRichiesta;
	}
	public byte[] getMessaggioXMLRisposta() {
		return this.messaggioXMLRisposta;
	}
	public void setMessaggioXMLRisposta(byte[] messaggioXMLRisposta) {
		this.messaggioXMLRisposta = messaggioXMLRisposta;
	}
	public String getContentType() {
		return this.contentType;
	}
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	public String getContentTypeRisposta() {
		return this.contentTypeRisposta;
	}
	public void setContentTypeRisposta(String contentTypeRisposta) {
		this.contentTypeRisposta = contentTypeRisposta;
	}
	@Override
	public java.util.Properties getPropertiesTrasportoRisposta() {
		return this.propertiesTrasportoRisposta;
	}
	public void setConnectTimedOut(int connectTimedOut) {
		this.connectTimedOut = connectTimedOut;
	}
	public void setConnectionReadTimeout(int connectionReadTimeout) {
		this.connectionReadTimeout = connectionReadTimeout;
	}
	public boolean isForceResponseAsSOAPProcessor() {
		return this.forceResponseAsSOAPProcessor;
	}
	public void setForceResponseAsSOAPProcessor(boolean forceResponseAsSOAPProcessor) {
		this.forceResponseAsSOAPProcessor = forceResponseAsSOAPProcessor;
	}
}
