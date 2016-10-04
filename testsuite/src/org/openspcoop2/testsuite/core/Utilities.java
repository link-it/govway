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



package org.openspcoop2.testsuite.core;


import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Vector;

import javax.activation.DataHandler;
import javax.xml.XMLConstants;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.apache.axis.Message;
import org.apache.axis.attachments.AttachmentPart;
import org.apache.axis.message.MessageElement;
import org.apache.axis.message.PrefixedQName;
import org.apache.axis.message.Text;
import org.slf4j.Logger;
import org.openspcoop2.core.integrazione.EsitoRichiesta;
import org.openspcoop2.core.integrazione.utils.EsitoRichiestaXMLUtils;
import org.openspcoop2.message.SOAPVersion;
import org.openspcoop2.message.XMLUtils;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.constants.CodiceErroreCooperazione;
import org.openspcoop2.protocol.sdk.constants.CodiceErroreIntegrazione;
import org.openspcoop2.protocol.sdk.constants.ErroreCooperazione;
import org.openspcoop2.protocol.sdk.constants.ErroreIntegrazione;
import org.openspcoop2.protocol.sdk.constants.MessaggiFaultErroreCooperazione;
import org.openspcoop2.testsuite.axis14.Axis14SoapUtils;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Classe di utility generiche
 * 
 * @author Andi Rexha (rexha@openspcoop.org)
 * @author Carlo Ciurli (ciurli@openspcoop.org)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class Utilities {
	
	/**
	 * Confronta due SoapBody
	 * 
	 * @param body
	 * @param body2
	 * @return true se i due corpi sono uguali
	 */
	public static boolean equalsSoapBody(SOAPBody body,SOAPBody body2){
		
		// METODO ANALOGO ESISTENTE IN SOAPUTILS DI org.openspcoop2.message.MessageUtils
		// PERO' IL METODO NEL PACKAGE SUDDESSO NON USA AXIS.
		// EVENTUALI FIX RIPORTARLI ANCHE NEL METODO SUDDETTO
		
		Iterator<?> it=body.getChildElements();
		Iterator<?> it2=body2.getChildElements();
		/*try{
			System.out.println("b1["+((org.apache.axis.message.SOAPBody)body).getAsString()+"]");
			System.out.println("b2["+((org.apache.axis.message.SOAPBody)body2).getAsString()+"]");
		}catch(Exception e){}
		*/
		while(it.hasNext()){
			if(!it2.hasNext()){
				return false;
			}
			if(!equalsSoapElements((SOAPElement)it.next(), (SOAPElement)it2.next(), new Vector<String>())){
				return false;
			}
		}
		if(it2.hasNext()){
			return false;
		}
		return true;
	}

	/**
	 * Confronta due SoapElement.
	 * TODO: Un errore non mi permette di individuare tutti i nameSpace in un nodo
	 * 
	 */
	public static boolean equalsSoapElements(SOAPElement el1,SOAPElement el2,Vector<String> namespacePrefixEl1){
		
		// METODO ANALOGO ESISTENTE IN SOAPUTILS DI org.openspcoop2.message.MessageUtils
		// PERO' IL METODO NEL PACKAGE SUDDESSO NON USA AXIS.
		// EVENTUALI FIX RIPORTARLI ANCHE NEL METODO SUDDETTO
		
		/**************** controllo nome del nodo *****************************/
		if(!el1.getNodeName().equals(el2.getNodeName())){
			//System.out.println("NOME DIVERSO");
			return false;
		}
		
		
		Iterator<?> it=el1.getAllAttributes();
		Iterator<?> it2=el2.getAllAttributes();
		Vector <String>vet=new Vector<String>();
		Vector <String>vet2=new Vector<String>();
		/**************** controllo se gli attributi sono uguali*****************************/
		while(it.hasNext()){
			if(!it2.hasNext()){
				//System.out.println("ATTR 1");
				return false;
			}
			//Attributes att=(Attributes)it.next();
			vet.add(((PrefixedQName) it.next()).getQualifiedName());
			vet2.add(((PrefixedQName) it2.next()).getQualifiedName());
		}
		if(it2.hasNext()){
			//System.out.println("ATTR 2");
			return false;
		}
		if(!vet.containsAll(vet2)){
			//System.out.println("ATTR 3");
			return false;
		}


		for(int i=0;i<vet.size();i++){
			String value=vet.get(i);
			if(!el1.getAttribute(value).equals(el2.getAttribute(value)))return false;
		}

		/***************************** Namespace URI del nodo ********************************/
        String str1=el1.getNamespaceURI();
        String str2=el2.getNamespaceURI();
       // System.out.println("el1 -- il namespace Uri del nodo e' "+str1);
        //System.out.println("el2 -- il namespace Uri del nodo e' "+str2);
        if(!str1.equals(str2)){
        	//System.out.println("URI");
        	return false;
        }
		
		
		/*****************************Controllo se i namespace sono uguali********************************/
        Iterator<?> nameSp1=el1.getNamespacePrefixes();
        Iterator<?> nameSp2=el2.getNamespacePrefixes();
        Vector <String>nameSpVet1=new Vector<String>();
        Vector <String>nameSpVet2=new Vector<String>();
        String prefix1, prefix2, urlPrefix1, urlPrefix2;
        while(nameSp1.hasNext() && nameSp2.hasNext())
        {
            prefix1=(String) nameSp1.next();
            try{
            	urlPrefix1 = el1.getNamespaceURI(prefix1);
            }catch(Exception e){
            	urlPrefix1 = el1.getNamespaceURI();
            }
            nameSpVet1.add(prefix1+"="+urlPrefix1);
            
            if(namespacePrefixEl1.contains((prefix1+"="+urlPrefix1))==false){
            	//System.out.println("ADD COMPLESSIVO: "+prefix1+"="+urlPrefix1);
            	namespacePrefixEl1.add(prefix1+"="+urlPrefix1);
            }
            
            prefix2=(String) nameSp2.next();
            try{
            	urlPrefix2 = el2.getNamespaceURI(prefix2);
            }catch(Exception e){
            	urlPrefix2 = el2.getNamespaceURI();
            }
            nameSpVet2.add(prefix2+"="+urlPrefix2);            
        }
        
        // Controllo uguaglianza
        for(int i=0; i<nameSpVet1.size(); i++){
        	String n1 = (String) nameSpVet1.get(i);
        	boolean trovato = false;
        	for(int j=0; j<nameSpVet2.size(); j++){
        		String n2 = (String) nameSpVet2.get(j);
        		if(n1.equals(n2)){
        			trovato = true;
        			break;
        		}			
        	}
        	if(trovato==false){
        		// Cerco nei namespaces del padre
        		if(namespacePrefixEl1.contains(n1)==false){
        			//System.out.println("NON TROVATO: "+n1);
        			return false;
        		}
        	}
        }
        

        if(!(nameSpVet1.size() == nameSpVet2.size())){
        	//System.out.println("SIZE NAMESPACE");
        	return false; 
        }    
        


		/*****************chiamata ricorsiva per i figli********************/
		Iterator<?> child=el1.getChildElements();
		Iterator<?> child2=el2.getChildElements();
		while(child.hasNext()){
			if(!child2.hasNext()){
				//System.out.println("CHILD1");
				return false;
			}
			Object obj=child.next();
			Object obj2=child2.next();
			if (obj instanceof Text) {
				Text text = (Text) obj;
				if (!(obj2 instanceof Text)){
					//System.out.println("CHILD2");
					return false;
				}
				else{
					Text text2 = (Text) obj2;
					boolean value = text.toString().equals(text2.toString());
					//System.out.println("CHILD3 ["+value+"]");
					return value;
				}
			}
			else{
				if(obj2 instanceof Text){
					//System.out.println("CHILD4");
					return false;
				}
				@SuppressWarnings("unchecked")
				Vector<String> namespacePrefixEl1Parent = (Vector<String>) namespacePrefixEl1.clone();
				if(!equalsSoapElements((SOAPElement)obj, (SOAPElement)obj2 , namespacePrefixEl1Parent)){
					//System.out.println("CHILD5");
					return false;
				}
			}
		}


		return true;
	}






	/**
	 * Aggiunge il valore id nelle mimeHeaders
	 * @param id il valore id
	 * @param msg il messaggio a cui aggiungere
	 */
	public static void setIdInMimeHeaders(String nameId,String id,Message msg) throws TestSuiteException{
		MimeHeaders mime=msg.getMimeHeaders();
		mime.setHeader(nameId, id);
	}


	/**
	 * Nel caso di una risposta openspcoopOK, ricava dal corpo il codice identificativo
	 * @return il valore openspcoopID
	 */
	public static String getIDFromOpenSPCoopOKMessage(Logger log,Message msg) {
		try {
			SOAPBody body = msg.getSOAPBody();
			
			NodeList lista = body.getChildNodes();
			if(lista==null){
				throw new Exception("Non sono presenti child nel messaggio");
			}
			for (int i = 0; i < lista.getLength(); i++) {
				Node n = lista.item(i);
				byte[] xml = XMLUtils.getInstance().toByteArray(n);
				if(EsitoRichiestaXMLUtils.isEsitoRichiesta(xml)){
					EsitoRichiesta esito = EsitoRichiestaXMLUtils.getEsitoRichiesta(log, xml);
					return esito.getIdentificativoMessaggio();
				}
			}
			
			throw new Exception("Child is not EsitoRichiesta di openspcoop2");
				
		} catch (Exception e) {
			throw new TestSuiteException(e,
					"creazione dell' elemento di risposta nella invoke RequestWSForm.OneWay");
		}
	}




	/**
	 * Metodo specifico per prelevare il codice di identificazione ricevuto dalla porta di dominio
	 * @return Il codice della cooperazione
	 */
	public static String getValueFromHeaders(Message message,String idName) {
		MimeHeaders headers = message.getMimeHeaders();

		java.util.Iterator<?> it = headers.getAllHeaders();
		StringBuffer mimes = new StringBuffer();
		while(it.hasNext()){
			javax.xml.soap.MimeHeader header = (javax.xml.soap.MimeHeader) it.next();
			mimes.append("["+header.getName()+"="+header.getValue()+"]");
		}
		//Logger log=LogUtilities.getLogger("openspcoop2.testsuite");
		//log.info("mimes: "+mimes.toString());
		String[] header = headers.getHeader(idName);
		String ret = null;
		try {
			ret = header[0];
		} catch (Exception e) {
			throw new NullPointerException("L'header non aveva l'header ["+idName+"], mime headers presenti: "+mimes.toString());
		}
		return ret;
	}





	/**
	 * Aggiunge una proprieta' al mime header (ovvero header relativi al package di trasporto, in questo caso http)
	 * 
	 * @param key il nome della chiave
	 * @param value il valore assocciato
	 * @param msg il messaggio a cui aggiungere la mime header
	 */
	public static void setMimeHeader(String key,String value,Message msg) {
		MimeHeaders mime=msg.getMimeHeaders();
		mime.addHeader(key, value);
		try{
			msg.saveChanges();
		}catch(SOAPException soap){
			throw new TestSuiteException(soap,"setMimeHeader su utils");
		}
	} 


	/**
	 * Controlla se il messaggio passatto come parametro sia conforme allo schema openspcoopOk.xsd
	 * @param msg il messaggio da mandare
	 * @return true se il messaggio e' conforme allo schema
	 */

	public static boolean isOpenSPCoopOKMessage(Message msg) {
		
		// Il body puo' essere null in caso si voglia che il oneway non ritorna contenuto applicativo.
		try{
			if(msg.getSOAPBody()!=null && msg.getSOAPBody().hasChildNodes()){
				SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
				Source schemaFile = new StreamSource(Utilities.class.getResourceAsStream("/openspcoopPresaInCarico.xsd"));
				Schema schema=null;
				try {
					schema = factory.newSchema(schemaFile);
				} catch (SAXException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
		
				// create a Validator instance, which can be used to validate an instance document
				Validator validator = schema.newValidator();
				SOAPBody body=null;
				try {
					body = msg.getSOAPBody();
				} catch (SOAPException e1) {
					// TODO Auto-generated catch block
					throw new TestSuiteException(e1, "nella fase di getSOAPBOdy nella convalida schema ok");
				}
				Iterator<?> it=body.getChildElements();
				if(!it.hasNext())return false;
				it.next();
				if(it.hasNext())return false;
				// validate the DOM tree
				try {
					MessageElement bodyElement = (MessageElement) msg.getSOAPBody().getFirstChild();
					ByteArrayInputStream bin = new ByteArrayInputStream(bodyElement.getAsString().getBytes());
					validator.validate(new StreamSource(bin));
				} catch (Exception e) {
					throw new TestSuiteException(e, "Convalida messaggio con schema openSPCoopOK fallita");
				} 
				return true;
			}else{
				return true;
			}
		} catch (Exception e) {
			throw new TestSuiteException(e, "Convalida messaggio con schema openSPCoopOK fallita (Errore generale)");
		} 
	}



	
	/**
	 * Crea un messaggio con attachment da un file realizzato in questo modo
	 * 
	 * @param fileName nome del file
	 * @return Il messaggio costruito
	 * @throws IOException
	 */
	public static Message createMessageWithAttachmentsFromFile(String fileName,boolean soapBodyEmpty) throws IOException{
		return createMessageWithAttachmentsFromFile(SOAPVersion.SOAP11, fileName, soapBodyEmpty);
	}
	public static Message createMessageWithAttachmentsFromFile(SOAPVersion soapVersion, String fileName,boolean soapBodyEmpty) throws IOException{
		/*UtilitiesGestioneMessaggiSoap utility=new UtilitiesGestioneMessaggiSoap();
		utility.readFromFile(fileName, mime);
		Message build=utility.buildMessage();*/
		FileInputStream fin = null;
		try{
			fin = new FileInputStream(new File(fileName));
			byte[] bytes = new byte[org.openspcoop2.utils.Utilities.DIMENSIONE_BUFFER];
			int letti = 0;
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			while( (letti=fin.read(bytes)) != -1 ){
				bout.write(bytes, 0, letti);
			}
			bout.flush();
			bout.close();
			
			return Axis14SoapUtils.build(soapVersion, bout.toByteArray(), false, false, !soapBodyEmpty);
		}catch(Exception e){
			e.printStackTrace(System.out);
			throw new IOException(e.getMessage());
		}finally{
			try{
				if(fin!=null)
					fin.close();
			}catch(Exception e){}
		}
	}



	/**
	 * Controlla se un' attachment part sia uguale ad un altro
	 * 
	 * @param att 
	 * @param att2
	 * @return true se i due attachment part hanno lo stesso contenuto
	 * @throws SOAPException
	 * @throws IOException
	 */
	public static boolean equalsAttachment(AttachmentPart att,AttachmentPart att2) throws SOAPException, IOException{

		DataHandler hand= att.getDataHandler();
		BufferedInputStream buff1=new BufferedInputStream(hand.getInputStream());
		
		//String s1 = (String) hand.getContent();
		
		DataHandler hand2=att2.getDataHandler();
		BufferedInputStream buff2=new BufferedInputStream(hand2.getInputStream());
				
		//String s2 = (String) hand2.getContent();
		
		//System.out.println("s1["+s1+"]");
		//System.out.println("s2["+s2+"]");
		//if(s1.equals(s2)==false){
		//	System.out.println("NON UGUALI");
		//}
		
		int temp;
		while((temp=buff1.read())!=-1){
			int temp2=buff2.read();
			if(temp2==-1){
				//System.out.println("RET 1");
				return false;
			}
			if(temp!=temp2){
				//System.out.println("RET 2 ["+temp+"]!=["+temp2+"]");
				return false;
			}
		}
		//buff2.read();
		int num;
		if((num=buff2.read())!=-1){
			while(num!=-1){
				num=buff2.read();
			}
			//System.out.println("RET 3");
			return false;
		}
		return true;
	}



	public static String getHeaderValue(String porta, int numero){
		return porta+"@"+numero;
	}

	public static String getPorta(String id){
		String [] str=id.split("@");
		return str[0];
	}

	public static String toString(CodiceErroreCooperazione codiceErrore,String protocollo) throws ProtocolException{
		return ProtocolFactoryManager.getInstance().getProtocolFactoryByName(protocollo).createTraduttore().toString(codiceErrore);
	}
	
	public static String toString(MessaggiFaultErroreCooperazione msgErrore,String protocollo) throws ProtocolException{
		return ProtocolFactoryManager.getInstance().getProtocolFactoryByName(protocollo).createTraduttore().toString(msgErrore);
	}
	
	public static String toString(ErroreCooperazione errore,String protocollo) throws ProtocolException{
		return ProtocolFactoryManager.getInstance().getProtocolFactoryByName(protocollo).createTraduttore().toString(errore);
	}
	
	public static String toString(CodiceErroreIntegrazione codiceErrore,String prefix,boolean isGeneric,String protocollo) throws ProtocolException{
		return ProtocolFactoryManager.getInstance().getProtocolFactoryByName(protocollo).createTraduttore().toString(codiceErrore,prefix,isGeneric);
	}
	
	public static String toString(ErroreIntegrazione errore,String protocollo) throws ProtocolException{
		return ProtocolFactoryManager.getInstance().getProtocolFactoryByName(protocollo).createTraduttore().toString(errore);
	}
}


