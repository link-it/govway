/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
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


import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import javax.xml.soap.MimeHeader;
import javax.xml.soap.SOAPException;

import org.apache.axis.AxisFault;
import org.apache.axis.Message;
import org.apache.axis.attachments.AttachmentPart;
import org.apache.axis.message.SOAPBody;
import org.openspcoop2.message.SOAPVersion;
import org.openspcoop2.testsuite.axis14.Axis14WSSBaseUtils;
import org.openspcoop2.testsuite.axis14.Axis14WSSReceiver;
import org.openspcoop2.testsuite.axis14.Axis14WSSSender;
import org.openspcoop2.testsuite.core.CostantiTestSuite;
import org.openspcoop2.testsuite.core.Repository;
import org.openspcoop2.testsuite.core.SOAPEngine;
import org.openspcoop2.testsuite.core.TestSuiteException;
import org.openspcoop2.testsuite.core.TestSuiteProperties;
import org.openspcoop2.testsuite.core.Utilities;
import org.openspcoop2.testsuite.db.DatabaseComponent;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.UtilsException;
import org.slf4j.Logger;

/**
 * Client core.
 * 
 * @author Andi Rexha (rexha@openspcoop.org)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class ClientCore {

	/** Identificativo prelevato nell'header della risposta*/
	protected String idMessaggio; 
	/** Eventuale riferimentoAsincrono prelevato nell'header della risposta*/
	protected String riferimentoAsincrono; 
	/** ServerProperties */
	protected TestSuiteProperties testsuiteProperties;
	/** Username per l'invocazione web service */
	protected String username;
	/** Password per l'invocazione web service */
	protected String password;
	/** Porta Delegata */
	protected String portaDelegata;
	/** Url di accesso ad un servizio offerto dalla Porta di Dominio */
	protected String urlPortaDiDominio;
	/** Logger */
	protected Logger log;
	/** SOAPEngine utilizzato dal Client */
	protected SOAPEngine soapEngine;
	/** SOAPVersion */
	protected SOAPVersion soapVersion;

	/** Messaggio da spedire */
	public Message sentMessage;
	/** Messaggio ricevuto */
	public Message receivedMessage;

	/** Repository ID Messaggi */
	protected Repository repository;
	
	/** Controllo numero messaggi ancora attivi */
	protected boolean attesaTerminazioneMessaggi = false;
	/** DatabasaComponent per il controllo dei messaggi attivi sulla PdD Fruitore */
	protected DatabaseComponent dbAttesaTerminazioneMessaggiFruitore = null;
	/** DatabasaComponent per il controllo dei messaggi attivi sulla PdD Erogatore */
	protected DatabaseComponent dbAttesaTerminazioneMessaggiErogatore = null;
	
	
	/**
	 * @throws UtilsException 
	 * 
	 */
	public ClientCore() throws TestSuiteException {
		
		// Inizializzazione TestSuiteProperties
		if(TestSuiteProperties.isInitialized()==false){
			// Server Properties
			TestSuiteProperties.initialize();
			// Logger
			try{
				LoggerWrapperFactory.setLogConfiguration(ClientCore.class.getResource("/"+CostantiTestSuite.LOGGER_PROPERTIES));
			}catch(Exception e){
				throw new TestSuiteException(e.getMessage(),e);
			}
		}
		
		// Istanza di Server Properties
		this.testsuiteProperties = TestSuiteProperties.getInstance();
		
		// Istanza di logger
		this.log=LoggerWrapperFactory.getLogger("openspcoop2.testsuite");
			
	}
	
	
	
	
	




	/** SORGENTI PER IL MESSAGGIO DI RICHIESTA */

	/**
	 * Setta il messaggio soap da un file.
	 * @param fileName il nome del file da prelevare il messaggio
	 * @param isBody se il valore e' true, nel file si trova solo il soap body del messaggio, il metodo imbusta.
	 */
	public void setMessageFromFile(String fileName,boolean isBody,boolean generaIDUnivoco) throws TestSuiteException{
		this.soapEngine.setMessageFromFile(fileName,isBody,generaIDUnivoco);
		this.sentMessage = this.soapEngine.getRequestMessage();
	}
	public void setMessageFromFile(String fileName,boolean isBody) throws TestSuiteException{
		setMessageFromFile(fileName,isBody,false);
	}

	/**
	 * Il messaggio da mandare viene specificato da un file. Se il file possiede anche le mime header http , viene
	 * costruita una copia del messaggio.
	 * @param fileName nome del file
	 * @throws IOException lancia un Ecezione di tipo IO
	 */
	public void setMessageWithAttachmentsFromFile(String fileName,boolean generaIDUnivoco,boolean soapBodyEmpty) throws IOException{
		this.soapEngine.setMessageWithAttachmentsFromFile(fileName,generaIDUnivoco,soapBodyEmpty);
		this.sentMessage = this.soapEngine.getRequestMessage();
	}
	public void setMessageWithAttachmentsFromFile(String fileName,boolean generaIDUnivoco) throws IOException{
		setMessageWithAttachmentsFromFile(fileName,false,false);
	}
	public void setMessageWithAttachmentsFromFile(String fileName) throws IOException{
		setMessageWithAttachmentsFromFile(fileName,false,false);
	}
	
	/**
	 * Setta il messaggio con un messaggio axis Message. In questo metodo il messaggio deve essere senza attachement
	 * @param msg
	 */
	public void setMessage(Message msg,boolean generaIDUnivoco){
		this.soapEngine.setMessage(msg,generaIDUnivoco);
		this.sentMessage = this.soapEngine.getRequestMessage();
	}
	public void setMessage(Message msg){
		setMessage(msg,false);
	}
	
	/**
	 * Aggiunge un attachment part prelevato in un file
	 * @param fileName nome del file da aggiungere
	 */
	public void addAttachment(String fileName){
		try {
			this.soapEngine.addAttachment(fileName, null);
		} catch (Exception e) {
			throw new TestSuiteException("Errore durante la setAttachment: "+e.getMessage());
		}
	}
	public void addAttachment(String fileName,String contentType){
		try {
			this.soapEngine.addAttachment(fileName, contentType);
		} catch (Exception e) {
			throw new TestSuiteException("Errore durante la setAttachment: "+e.getMessage());
		}
	}
	
	public void addAnotherIDUnivoco(){
		this.soapEngine.addIDUnivoco();
		this.sentMessage = this.soapEngine.getRequestMessage();
	}
	
	
	
	
	
	
	/** METODI DI CONFIGURAZIONE */

	/**
	 * Crea il soapEngine utilizzato per la gestione della richiesta SOAP.
	 * Deve essere chiamato dopo aver impostato la url della Porta di Dominio, e la porta delegata da invocare
	 */
	public void connectToSoapEngine() throws TestSuiteException{
		this.connectToSoapEngine(SOAPVersion.SOAP11);
	}
	public void connectToSoapEngine(SOAPVersion soapVersion) throws TestSuiteException{
		if(this.portaDelegata==null)
			this.portaDelegata="";
		this.soapEngine=new SOAPEngine(this.urlPortaDiDominio+this.portaDelegata,soapVersion);
		this.soapVersion = soapVersion;
	}

	/**
	 * Setta il valore della porta delegata
	 */
	public void setPortaDelegata(String PD){
		this.portaDelegata=PD;
	}

	/**
	 * Setta l' url dove risiede la porta di dominio
	 * @param str il valore dell' URL
	 */
	public void setUrlPortaDiDominio(String str){
		this.urlPortaDiDominio=str;
	}

	/**
	 * Setta username e password
	 * @param user
	 * @param password
	 */
	public void setAutenticazione(String user,String password){
		this.username=user;
		this.soapEngine.setUserName(user);
		this.password=password;
		this.soapEngine.setPassword(password); // Aggiunto da Andrea.
	}

	/**
	 * Setta username utilizzata nell'autenticazione
	 * @param user
	 */
	public void setUsername(String user){
		this.username=user;
		this.soapEngine.setUserName(user); // Aggiunto da Andrea.
	}
	/**
	 * Setta password utilizzata nell'autenticazione
	 * @param password
	 */
	public void setPassword(String password){
		this.password=password;
		this.soapEngine.setPassword(password); // Aggiunto da Andrea.
	}



	
	
	
	






	

	/** MESSAGGI GESTITI */
	
	/**
	 * Preleva il messaggio mandato
	 */
	public Message getSentMessage(){
		return this.sentMessage;
	}

	/**
	 * Preleva il messaggio di risposta
	 * @return messaggio di risposta
	 */
	public Message getResponseMessage(){
		return this.receivedMessage;
	}



	
	/** INVOCAZIONI */
	
	/** 
	 * Invocazione in modalita' Asincrona, 
	 * In una invocazione asincrona (wsdl:input only per il servizio) 
	 * il requisito della risposta e' che deve soddisfare il tipo 
	 *  OpenSPCoopOK.xsd
	 *  
	 * @throws TestSuiteException 
	 * @throws AxisFault 
	 * @throws SOAPException */
	protected void invocazioneAsincrona() throws TestSuiteException, AxisFault{
		this.soapEngine.invoke(this.repository);
		this.receivedMessage=this.soapEngine.getResponseMessage();
		if(this.receivedMessage!=null && !Utilities.isOpenSPCoopOKMessage(this.receivedMessage))
				throw new TestSuiteException("Il messaggio ricevuto con una interazione asincrona, non e' conforme allo schema OpenSPCoopOK.xsd");
		if(this.testsuiteProperties.getIdMessaggioTrasporto()==null)
			throw new TestSuiteException("Nome dell'header contenente l'id nella riposta http, non definito");
		this.idMessaggio=Utilities.getValueFromHeaders(this.receivedMessage,this.testsuiteProperties.getIdMessaggioTrasporto()); // Aggiunto da Andrea.
		try{
			this.riferimentoAsincrono=Utilities.getValueFromHeaders(this.receivedMessage,this.testsuiteProperties.getRiferimentoAsincronoTrasporto()); // Aggiunto da Andrea.
		}catch(Exception e){}
		if(this.attesaTerminazioneMessaggi){
			if(this.dbAttesaTerminazioneMessaggiFruitore!=null){
				long countTimeout = System.currentTimeMillis() + (1000*this.testsuiteProperties.getTimeoutProcessamentoMessaggiOpenSPCoop());
				while(this.dbAttesaTerminazioneMessaggiFruitore.getVerificatoreMessaggi().countMsgOpenSPCoop(this.idMessaggio)!=0 && countTimeout>System.currentTimeMillis()){
					try{
					Thread.sleep(this.testsuiteProperties.getCheckIntervalProcessamentoMessaggiOpenSPCoop());
					}catch(Exception e){}
				}
			}
			if(this.dbAttesaTerminazioneMessaggiErogatore!=null){
				long countTimeout = System.currentTimeMillis() + (1000*this.testsuiteProperties.getTimeoutProcessamentoMessaggiOpenSPCoop());
				while(this.dbAttesaTerminazioneMessaggiErogatore.getVerificatoreMessaggi().countMsgOpenSPCoop(this.idMessaggio)!=0 && countTimeout>System.currentTimeMillis()){
					try{
						Thread.sleep(this.testsuiteProperties.getCheckIntervalProcessamentoMessaggiOpenSPCoop());
					}catch(Exception e){}
				}
			}
		}
	}

	/**
	 * In una invocazione sincrona (wsdl:input e wsdl:output 
	 * non viene controllata la possibile risposta, a differenza della modalita' asincrona.
	 * Viene prelevato il valore id nell'header del messaggio di risposta
	 *
	 * @throws AxisFault
	 */
	protected void invocazioneSincrona() throws TestSuiteException,AxisFault{
		this.soapEngine.invoke(this.repository);
		this.receivedMessage=this.soapEngine.getResponseMessage();
		if(this.testsuiteProperties.getIdMessaggioTrasporto()==null)
			throw new TestSuiteException("Nome dell'header contenente l'id nella riposta http, non definito");
		this.idMessaggio=Utilities.getValueFromHeaders(this.receivedMessage,this.testsuiteProperties.getIdMessaggioTrasporto());
		try{
			this.riferimentoAsincrono=Utilities.getValueFromHeaders(this.receivedMessage,this.testsuiteProperties.getRiferimentoAsincronoTrasporto()); // Aggiunto da Andrea.
		}catch(Exception e){}
	}



	
	/** UTILITY */

	public void puliziaBodyIDServer() throws SOAPException{
		ClientCore.puliziaBodyIDServer(this.receivedMessage);
	}
	
	public static void puliziaBodyIDServer(Message receivedMessage) throws SOAPException{
		// pulizia eventuale ID inserito dal server
		Iterator<?> itPulizia=receivedMessage.getSOAPBody().getChildElements();
		org.apache.axis.message.MessageElement elenDaRimuovere = null;
		while(itPulizia.hasNext()){
			org.apache.axis.message.MessageElement elen = (org.apache.axis.message.MessageElement) itPulizia.next();
			if("idUnivocoServer".equals(elen.getLocalName())){
				elenDaRimuovere = elen;
				break;
			}
		}
		if(elenDaRimuovere!=null){
			receivedMessage.getSOAPBody().removeChild(elenDaRimuovere);
		}
		//receivedMessage.saveChanges();
	}
	
	/**
	 * Metodo per confrontare se due soap body sono uguali.\
	 * Per ugualianza tra i body si intende che le informazioni specificate nel body mandato siano uguali a quello
	 * di quello ricevuto
	 * @return true se i due soap body sono uguali
	 */
	public boolean isEqualsSentAndResponseMessage(){
		return ClientCore.isEqualsSentAndResponseMessage(this.sentMessage, this.receivedMessage);
	}
	
	public static  boolean isEqualsSentAndResponseMessage(Message sentMessage,Message receivedMessage){
		try{
			ClientCore.puliziaBodyIDServer(receivedMessage);
			
			SOAPBody body1=(SOAPBody) sentMessage.getSOAPBody();
			SOAPBody body2=(SOAPBody) receivedMessage.getSOAPBody();
			
			/* TEST CON XSI SPOSTATO DOVE EFFETTIVAMENTE UTILIZZATO 
			String xml1 = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"><soapenv:Body xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"><ns1:getQuote se:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\" xmlns:ns1=\"urn:xmethods-delayed-quotes\" xmlns:xsi=\"http://www.w3.org/1999/XMLSchema-instance\" xmlns:se=\"http://schemas.xmlsoap.org/soap/envelope/\">"+
				"<symbol xsi:type=\"xsd:string\">IBM</symbol>"+
				"<symbol xsi:type=\"xsd:string\">IIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIII</symbol>"+
				"</ns1:getQuote><test:idUnivoco xsi:type=\"xsd:string\" xmlns:test=\"http://www.openspcoop.org\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">ID-1</test:idUnivoco></soapenv:Body></soapenv:Envelope>";
			
			String xml2 = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">"+
				"<soapenv:Body xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"><ns1:getQuote se:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\" xmlns:ns1=\"urn:xmethods-delayed-quotes\" xmlns:se=\"http://schemas.xmlsoap.org/soap/envelope/\">"+
				"<symbol xsi:type=\"xsd:string\" xmlns:xsi=\"http://www.w3.org/1999/XMLSchema-instance\">IBM</symbol>"+
				"<symbol xsi:type=\"xsd:string\" xmlns:xsi=\"http://www.w3.org/1999/XMLSchema-instance\">IIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIII</symbol>"+
				"</ns1:getQuote><test:idUnivoco xsi:type=\"xsd:string\" xmlns:test=\"http://www.openspcoop.org\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">ID-1</test:idUnivoco></soapenv:Body>"+
				"</soapenv:Envelope>";
			Message msg1 = new Message(new ByteArrayInputStream(xml1.getBytes()));
			Message msg2 = new Message(new ByteArrayInputStream(xml2.getBytes()));
			SOAPBody body1=(SOAPBody) msg1.getSOAPBody();
			SOAPBody body2=(SOAPBody) msg2.getSOAPBody(); */
			
			//return Utilities.equalsSoapBody(body1, body2);
			
			if(Utilities.equalsSoapBody(body1, body2)==false){
				try{
					System.out.println("B1["+((org.apache.axis.message.SOAPBody)sentMessage.getSOAPBody()).getAsString()+"]");
					System.out.println("B2["+((org.apache.axis.message.SOAPBody)receivedMessage.getSOAPBody()).getAsString()+"]");
				}catch(Exception e){
					System.out.println("ERROR");
				}
				return false;
			}else
				return true;
		}
		catch(SOAPException e){
			throw new TestSuiteException(e.getMessage());
		}
	}
	
	/**
	 * Metodo per il confronto degli attachment. Premessa e' che gli attachment non devono essere modificati da parte
	 * della porta di dominio. Il confronto viene realizzato a livello di byte
	 */
	public boolean isEqualsSentAndResponseAttachments() throws SOAPException, IOException{
		
		puliziaBodyIDServer();
		
		Message msg=this.sentMessage;
		Message msg2=this.receivedMessage;
		
		Iterator<?> parts=msg.getAttachments();
		Vector<?> vet=getVector(parts);
		Iterator<?> parts2=msg2.getAttachments();
		HashMap<?,?> hash2=putOnHash(parts2);
		if(vet.size()!=hash2.size()){
			return false;
		}
		for(int i=0;i<vet.size();i++){
			AttachmentPart att=(AttachmentPart)vet.get(i);
			java.util.Iterator<?> it = att.getAllMimeHeaders();
			String content=null;
			while(it.hasNext()){
				MimeHeader mime=(MimeHeader) it.next();
				if("content-id".equalsIgnoreCase(mime.getName())){
					content=mime.getValue();
					break;
				}
			}
			AttachmentPart att2=(AttachmentPart)hash2.get(content);
			if(att2==null)throw new TestSuiteException("Non esiste un attachment con lo stesso id ["+content+"]");
			if(!Utilities.equalsAttachment(att, att2)){
				//System.out.println("ATTACH DIVERSI");
				return false;
			}

		}
		return true;
	}

	/** 
	 * Ritorna gli attachments presenti nell'iterator, in un HashMap.
	 * @param parts
	 * @return HashMap
	 */
	private HashMap<?,?> putOnHash(Iterator<?> parts){
		HashMap<String, AttachmentPart> hash=new HashMap<String, AttachmentPart>();
		while(parts.hasNext()){
			AttachmentPart att=(AttachmentPart)parts.next();
			java.util.Iterator<?> it = att.getAllMimeHeaders();
			while(it.hasNext()){
				MimeHeader mime=(MimeHeader) it.next();
				if("content-id".equalsIgnoreCase(mime.getName())){
					hash.put(mime.getValue(), att);
					break;
				}
			}
		}
		return hash;
	}

	/**
	 * Ritorna gli attachments presenti nell'iterator in un Vector
	 * 
	 * @param it
	 * @return Vector
	 */
	private Vector<?> getVector(Iterator<?> it){
		Vector<AttachmentPart> vet=new Vector<AttachmentPart>();
		while(it.hasNext()){
			vet.add((AttachmentPart)it.next());
		}
		return vet;
	}









	public final void setAttesaTerminazioneMessaggi(
			boolean attesaTerminazioneMessaggi) {
		this.attesaTerminazioneMessaggi = attesaTerminazioneMessaggi;
	}









	public final void setDbAttesaTerminazioneMessaggiFruitore(
			DatabaseComponent dbAttesaTerminazioneMessaggiFruitore) {
		this.dbAttesaTerminazioneMessaggiFruitore = dbAttesaTerminazioneMessaggiFruitore;
	}









	public final void setDbAttesaTerminazioneMessaggiErogatore(
			DatabaseComponent dbAttesaTerminazioneMessaggiErogatore) {
		this.dbAttesaTerminazioneMessaggiErogatore = dbAttesaTerminazioneMessaggiErogatore;
	}


	
	
	
	
	
	
	/** WSSecurity */
	public void processWSSRequest(java.util.Hashtable<String,String> wssProperties,Axis14WSSBaseUtils baseWSS) throws Exception{
		this.log.info("Costruzione messaggio di richiesta con wss ...");
		
		if(this.sentMessage==null)
			throw new Exception("Messaggio di richiesta is null");
		
		Axis14WSSSender wssSender = new Axis14WSSSender(wssProperties,baseWSS);
		if(wssSender.process(this.sentMessage)==false) {
			throw new Exception("Costruzione WSS in spedizione non riuscita ["+wssSender.getCodiceErrore()+"]: "+wssSender.getMsgErrore());
		}
		this.log.info("Costruzione messaggio di richiesta terminata");
	}
	
	/** WSSecurity */
	public void processWSSResponse(java.util.Hashtable<String,String> wssProperties,Axis14WSSBaseUtils baseWSS) throws Exception{
		this.log.info("Costruzione messaggio di risposta con wss ...");
		
		if(this.receivedMessage==null)
			throw new Exception("Messaggio di risposta is null");
		
		Axis14WSSReceiver wssReceiver = new Axis14WSSReceiver(wssProperties,baseWSS);
		if(wssReceiver.process(this.receivedMessage,null) == false){   
			throw new Exception("Validazione WSS in ricezione non riuscita ["+wssReceiver.getCodiceErrore()+"]: "+wssReceiver.getMsgErrore());
		}
		this.log.info("Costruzione messaggio di risposta terminata");
	}


	public String getIdMessaggio() {
		return this.idMessaggio;
	}
	
	public String getLastIDUnivocoGenerato(){
		return this.soapEngine.getLastIDUnivoco();
	}
	public java.util.Properties getPropertiesTrasportoRisposta() {
		return this.soapEngine.getPropertiesTrasportoRisposta();
	}

	public Logger getLog() {
		return this.log;
	}
}