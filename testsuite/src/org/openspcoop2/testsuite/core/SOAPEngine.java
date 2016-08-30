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



package org.openspcoop2.testsuite.core;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Properties;

import javax.xml.namespace.QName;
import javax.xml.rpc.ServiceException;
import javax.xml.soap.MimeHeader;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;

import org.apache.axis.AxisFault;
import org.apache.axis.Message;
import org.apache.axis.attachments.AttachmentPart;
import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.message.MessageElement;
import org.apache.axis.message.PrefixedQName;
import org.apache.axis.message.SOAPHeaderElement;
import org.apache.axis.soap.SOAPConstants;
import org.apache.axis.utils.ByteArrayOutputStream;
import org.openspcoop2.message.SOAPVersion;
import org.openspcoop2.message.SoapUtils;

/**
 * Engine per la gestione/spedizion di messaggi Soap
 * 
 * @author Andi Rexha (rexha@openspcoop.org)
 * @author $Author$
 * @version $Rev$, $Date$
 */


public class SOAPEngine {

	/** Url di invocazione di un WebService */
	private String url;
	/** StubClient verso il WebService indicato nella url */
	private Call call; 
	/** Username utilizzato nella Call */
	private String userName;
	/** Password utilizzato nella Call */
	private String password;
	/** Proprieta' del trasporto della risposta */
	private java.util.Properties propertiesTrasportoRisposta;

	private static long identificatoreUnivoco = 0;
	public static synchronized long getIDUnivoco(){
		SOAPEngine.identificatoreUnivoco++;
		return SOAPEngine.identificatoreUnivoco;
	}
	
	

	/**
	 * Messaggio da spedire
	 * NOTA: Sorgenti per il messaggio da spedire.
	 * 
	 * Nel caso in cui vengono settati piu' tipi di input per mandare alla PD avremo una priorita':
	 * 1) File
	 * 2) InputStream/Message    a seconda di quale viene specificata per prima
	 * 3) Da method,param,operation;
	 * 4) Default
	 */
	public Message sentMessage;
	/** Messaggio ricevuto */
	Message receivedMessage;
	
	/** Gestore Attachments */
	Message attachmentGenerator;
	/** Indicazione sulla gestione degli attachments */
	boolean withAttachment;

	private SOAPVersion soapVersion;
	private String idMessaggioSoap;

	/**
	 * Costruttore che costruisce una richiesta di tipo webservice verso una url con nome della operazione, metodo, e i parametri passati
	 **/	
	public SOAPEngine(String url) throws TestSuiteException {
		this(url, SOAPVersion.SOAP11);
	}
	public SOAPEngine(String url,SOAPVersion soapVersione) throws TestSuiteException {
		this.soapVersion = soapVersione;
		this.url = url;
		this.withAttachment=false;
		initCall();
		switch (soapVersione) {
		case SOAP11:
			this.call.setSOAPVersion(SOAPConstants.SOAP11_CONSTANTS);
			this.call.setEncodingStyle(org.apache.axis.Constants.URI_SOAP11_ENC);
			break;
		case SOAP12:
			this.call.setSOAPVersion(SOAPConstants.SOAP12_CONSTANTS);
			this.call.setEncodingStyle(org.apache.axis.Constants.URI_SOAP12_ENC);
			break;
		}
		this.idMessaggioSoap = TestSuiteProperties.getInstance().getIdMessaggioSoap();
	}



	/**
	 * Inizializzazione di una Call
	 * 
	 * @throws TestExceptionFatal
	 */
	private void initCall() throws TestSuiteException{
		Service service = new Service();
		try {
			this.call = (Call) service.createCall();
		} catch (ServiceException e) {
			throw new TestSuiteException(e.getMessage());
		}
	}

	/**
	 * Impostazione del target endpoint nella Call
	 * 
	 * @param string
	 */
	public void setTargetEndPointAddress(String string){
		this.call.setTargetEndpointAddress(string);
	}

	/**
	 * Aggiunge un mime Header
	 * 
	 */
	public void addMimeHeader(String name,String value){
		javax.xml.soap.MimeHeaders mime=this.sentMessage.getMimeHeaders();
		mime.addHeader(name, value);
	}



	/** 
	 * Imposta i parametri della connessione
	 * 
	 * Vengono settati i valori:    URL, USERNAME, PASSWORD   settati prima della  getConnection 
	 */  
	public void setConnectionParameters() {
		this.call.setTargetEndpointAddress(this.url);
		if (this.userName != null) 
			this.call.setUsername(this.userName);
		if( this.password != null) 
			this.call.setPassword(this.password);
	}
	
	/**
	 * Invokazione 
	 * @throws FileNotFoundException 
	 */
	public void invoke(Repository repository) throws AxisFault {

		// Backup msg richiesta da spedire
		String contentType = null;
		switch (this.soapVersion) {
		case SOAP11:
			contentType = this.sentMessage.getContentType(new org.apache.axis.soap.SOAP11Constants());
			break;
		case SOAP12:
			contentType = this.sentMessage.getContentType(new org.apache.axis.soap.SOAP12Constants());
			break;
		}
		
		byte[] msg = null;
		MimeHeaders mh = this.sentMessage.getMimeHeaders();
		try{
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			this.sentMessage.writeTo(bout);
			msg = bout.toByteArray();
			
			// reimposto richiesta
			ByteArrayInputStream messageInput = new ByteArrayInputStream(msg);
			this.sentMessage = new Message(messageInput,false,contentType,null);
			Iterator<?> it = mh.getAllHeaders();
			while(it.hasNext()){
				MimeHeader mhi = (MimeHeader) it.next();
				this.sentMessage.getMimeHeaders().addHeader(mhi.getName(), mhi.getValue());
			}
			if(this.sentMessage.countAttachments()==0){
				this.sentMessage.getSOAPPartAsBytes();
			}
		}catch(Exception e){
			e.printStackTrace(System.out);
			throw new TestSuiteException(e.getMessage());
		}
		
		try {
			this.call.setTimeout(CostantiTestSuite.READ_TIMEOUT);
			this.call.invoke(this.sentMessage);
		} catch (AxisFault e) {
			if(e.getHeaders() != null) {
				String id = null;
				for(Object header : e.getHeaders()) {
					org.apache.axis.message.SOAPHeaderElement headerElement = (org.apache.axis.message.SOAPHeaderElement) header;
					id = headerElement.getAttribute(this.idMessaggioSoap);
					if(id != null) {
						break;
					}
					
				}
				if(id != null) {
					repository.add(id);
				}
			}
			throw e;			
		}
		
		// reimposto richiesta
		ByteArrayInputStream messageInput = new ByteArrayInputStream(msg);
		this.sentMessage = new Message(messageInput,false,contentType,null);
		Iterator<?> it = mh.getAllHeaders();
		while(it.hasNext()){
			MimeHeader mhi = (MimeHeader) it.next();
			this.sentMessage.getMimeHeaders().addHeader(mhi.getName(), mhi.getValue());
		}
		if(this.sentMessage.countAttachments()==0){
			this.sentMessage.getSOAPPartAsBytes();
		}
		
		// prendo risposta
		this.receivedMessage=this.call.getResponseMessage();
		
		MimeHeaders mhResponse = this.receivedMessage.getMimeHeaders();
		if(mhResponse!=null){
			Iterator<?> itResponse = mhResponse.getAllHeaders();
			this.propertiesTrasportoRisposta = new Properties();
			while(itResponse.hasNext()){
				MimeHeader mhiResponse = (MimeHeader) itResponse.next();
				//System.out.println("N:"+mhiResponse.getName()+" V:"+ mhiResponse.getValue());
				this.propertiesTrasportoRisposta.put(mhiResponse.getName(), mhiResponse.getValue());
			}
		}
	}	



	/**
	 * Setta username utilizzata nell'autenticazione
	 * @param user
	 */
	public void setUserName(String user){
		this.userName=user;
	}
	/**
	 * Setta password utilizzata nell'autenticazione
	 * @param password
	 */
	public void setPassword(String password){
		this.password=password;
	}




	/**
	 * Aggiunge un Identificativo utilizzato dalla TestSuite per correlazioni in modalita' sincrone.
	 * 
	 * @param value
	 */
	public void setIDTestSuiteHeader(String value){
		try {
			javax.xml.soap.SOAPHeader head=this.sentMessage.getSOAPHeader();
			SOAPHeaderElement el=(SOAPHeaderElement) ((javax.xml.soap.SOAPHeader) head).addHeaderElement(new PrefixedQName(new QName(CostantiTestSuite.TAG_NAME)));
			el.addAttribute(new PrefixedQName(new QName(CostantiTestSuite.ID_HEADER_ATTRIBUTE)), value);
			el.setActor("http://www.openspcoop.org/TestSuite/CooperazioneAsincronaSimmetrica");
			el.setMustUnderstand(false);
		} catch (SOAPException e) {
			throw new TestSuiteException(e,"Errore nell' aggiuntere un attributo al messaggio nel header");
		}	
	}
	
	
	public void setCredenzialiRispostaAsincronaSimmetrica(String user,String password){
		try {
			javax.xml.soap.SOAPHeader head=this.sentMessage.getSOAPHeader();
			SOAPHeaderElement el=(SOAPHeaderElement) ((javax.xml.soap.SOAPHeader) head).addHeaderElement(new PrefixedQName(new QName(CostantiTestSuite.CREDENZIALI_RISPOSTA_ASINCRONA_SIMMETRICA)));
			el.addAttribute(new PrefixedQName(new QName(CostantiTestSuite.CREDENZIALI_USERNAME)), user);
			el.addAttribute(new PrefixedQName(new QName(CostantiTestSuite.CREDENZIALI_PASSWORD)), password);
			el.setActor("http://www.openspcoop.org/TestSuite/CooperazioneAsincronaSimmetrica");
			el.setMustUnderstand(false);
		} catch (SOAPException e) {
			throw new TestSuiteException(e,"Errore nell' aggiuntere un attributo al messaggio nel header");
		}	
	}

	/*
    public void addBodyAttribute(String attribute, String value){
    	try{
    		SOAPBody body=(SOAPBody) sentMessage.getSOAPBody();
    		QName name=new QName("andiRexha",attribute+"OTTIMO");
    		body.addAttribute((Name) new PrefixedQName(name), value);
    		sentMessage.saveChanges();
    	}catch(SOAPException e){
    		throw new TestSuiteException(e,"Errore nell' aggiuntere un attributo al messaggio nel body");
    	}
    }*/

	
	
	

	/** SORGENTI PER IL MESSAGGIO DI RICHIESTA */

	/**
	 * Setta il messaggio soap da un file.
	 * @param str il nome del file da prelevare il messaggio
	 * @param bodyFile se il valore e' true, nel file si trova solo il soap body del messaggio, il metodo imbusta.
	 */
	public void setMessageFromFile(String str,boolean bodyFile,boolean generaIDUnivoco)throws TestSuiteException{
		File file=new File(str);
		InputStream in=null;
		try {
			in = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			throw new TestSuiteException("File non esistente");
		}
		this.sentMessage=new Message(in,bodyFile);
		if(generaIDUnivoco)
			this.addIDUnivoco();
		try {
			// save changes.
			// N.B. il countAttachments serve per il msg con attachments come saveMessage!
			if(this.sentMessage.countAttachments()==0){
				this.sentMessage.getSOAPPartAsBytes();
			}
		} catch (Exception e) {
			throw new TestSuiteException("Errore durante il salvataggio: "+e.getMessage());
		}
		try {
			in.close();
		} catch (Exception e) {
			throw new TestSuiteException("Errore durante la chiusura dell'input stream: "+e.getMessage());
		}
	}
	public void setMessageFromFile(String str,boolean bodyFile)throws TestSuiteException{
		setMessageFromFile(str,bodyFile,false);
	}
	
	
	/** 
	 * Imposta il messagio da spedire
	 * 
	 * @param sentMessage2
	 */
	public void setMessage(Message sentMessage2,boolean generaIDUnivoco) {
		this.sentMessage=sentMessage2;
		if(generaIDUnivoco)
			this.addIDUnivoco();
	}
	public void setMessage(Message sentMessage2){
		this.setMessage(sentMessage2,false);
	}
	



	
	/**
	 * Il messaggio da mandare viene specificato da un file. Se il file possiede anche le mime header http , viene
	 * costruita una copia del messaggio.
	 * @param fileName nome del file
	 * @throws IOException lancia un Ecezione di tipo IO
	 */
	public void setMessageWithAttachmentsFromFile(String fileName, boolean generaIDUnivoco,boolean soapBodyEmpty) throws IOException{
		this.sentMessage=Utilities.createMessageWithAttachmentsFromFile(this.soapVersion,fileName, soapBodyEmpty);
		if(generaIDUnivoco)
			this.addIDUnivoco();
	}
	public void setMessageWithAttachmentsFromFile(String fileName) throws IOException{
		setMessageWithAttachmentsFromFile(fileName,false,false);
	}

	public String getLastIDUnivoco(){
		return this.lastIDUnivoco;
	}
	String lastIDUnivoco = null;
	public void addIDUnivoco(){
		try{
			if(this.sentMessage.getSOAPBody()!=null){
				MessageElement m = new MessageElement("idUnivoco", "test","http://www.openspcoop.org");
				this.lastIDUnivoco = "ID-"+SOAPEngine.getIDUnivoco();
				m.setValue(this.lastIDUnivoco);
				SOAPElement child = SoapUtils.getNotEmptyFirstChildSOAPElement(this.sentMessage.getSOAPBody());
				if(child!=null){
					child.addChildElement(m);
				}
				else{
					this.sentMessage.getSOAPBody().addChildElement(m);
				}
			}
		} catch (Exception e) {
			System.out.println("Errore durante la generazione dell'id unico: "+e.getMessage());
		}
	}
	
	/**
	 * Aggiunge un attachment part prelevato in un file
	 * @param fileName nome del file da aggiungere
	 */
	public void addAttachment(String fileName,String contentType)throws Exception{
		this.withAttachment=true;
		if(contentType==null)contentType="text/plain";
		File file=new File(fileName);
		if(file.isDirectory())addAttachmentsFromDirectory(file,this.sentMessage,contentType);
		else{
			addAttachment(file,contentType);
		}	
	}
	/**
	 * Aggiunge tutti gli attachments presenti nella directory 'dir'  al messaggio.
	 * 
	 * @param dir
	 * @param msg
	 * @param content
	 * @throws Exception
	 */
	private void addAttachmentsFromDirectory(File dir,Message msg,String content)throws Exception{
		File[] files=dir.listFiles();
		for(int i=0;i<files.length;i++){
			addAttachment(files[i],content);
		}
	}
	/**
	 * Aggiunge un attachment al messaggio da spedire
	 * 
	 * @param file
	 * @param content
	 * @throws Exception
	 */
	private void addAttachment(File file,String content) throws Exception{
		byte[] buff=new byte[(int) file.length()];
		InputStream input=new FileInputStream(file);
		input.read(buff, 0, buff.length);
		input.close();
		AttachmentPart at=new AttachmentPart();
		at.setContent(new ByteArrayInputStream(buff), content);
		this.sentMessage.addAttachmentPart(at);
	}



	


	
	/** Messagio di Risposta */
	public Message getResponseMessage(){
		return this.receivedMessage;
	}
	/** Messagio di Richiesta */
	public Message getRequestMessage(){
		return this.sentMessage;
	}
	public java.util.Properties getPropertiesTrasportoRisposta() {
		return this.propertiesTrasportoRisposta;
	}

}
