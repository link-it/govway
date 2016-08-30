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



package org.openspcoop2.message;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.slf4j.Logger;
import org.w3c.dom.NamedNodeMap;


import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;

import javax.xml.soap.SOAPEnvelope;

import java.io.InputStream;

import java.util.Vector;


/**
 * Libreria contenente metodi utili per la gestione degli Attachments SOAP.
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class AttachmentsUtils {


	/* ********  F I E L D S  P R I V A T I  ******** */


	/**
	 * Trova tutti i riferimenti ad attachments presenti all'interno di un messaggio SOAPEnvelope,
	 * fornito attraverso il parametro <var>soapEnvelope</var>.
	 * Ogni riferimento trovato (Content-ID), viene inserito all'interno del vector <var>href</var>.
	 *
	 * @deprecated  utility che impone la presenza di un attributo 'href' negli elementi del body
	 * @param soapEnvelope byte[] del SOAPEnvelope da esaminare.
	 * @param href Vector dove depositare i riferimenti trovati.
	 * 
	 */
	@Deprecated public static void findHRef(byte [] soapEnvelope , java.util.Vector<String> href){
		try{
			OpenSPCoop2MessageParseResult pr = (OpenSPCoop2MessageFactory.getMessageFactory()).createMessage(SOAPVersion.SOAP11, soapEnvelope);
			OpenSPCoop2Message msg = pr.getMessage_throwParseException();
			SOAPEnvelope env = (msg.getSOAPPart()).getEnvelope();
			AttachmentsUtils.findHRef(env.getBody(),href);
		} catch (Throwable e) {
			//log.info("ERROR["+e.getMessage()+"]");
			return;
		}
	}

	/**
	 * Trova tutti i riferimenti ad attachments presenti all'interno di un nodo,
	 * fornito attraverso il parametro <var>node</var>.
	 * Ogni riferimento trovato (Content-ID), viene inserito all'interno del vector <var>href</var>.
	 *
	 * @deprecated  utility che impone la presenza di un attributo 'href' negli elementi del body
	 * @param node Node da esaminare.
	 * @param href Vector dove depositare i riferimenti trovati.
	 * 
	 */
	@Deprecated public static void findHRef(Node node , java.util.Vector<String> href) {
		if(node == null)
			return;

		// Esamino attributi del nodo
		NamedNodeMap att = node.getAttributes();
		if(att!=null){
			Node hrefFind = att.getNamedItem("href");
			if(hrefFind!=null){
				href.add(hrefFind.getNodeValue());
				//log.info("find ["+hrefFind.getNodeValue()+"]!!!");
			}
		}

		NodeList list = node.getChildNodes();
		if(list == null)
			return;

		int nodes = list.getLength();
		for(int i=0;i<nodes;i++){
			Node child = list.item(i);
			AttachmentsUtils.findHRef(child,href);
		}
	}

	/**
	 * Trova la stringa utilizzata, all'interno del SOAPMessage <var>xml</var>,
	 * per delimitare i vari attachments presenti.
	 *
	 * @param xml byte[] del SOAPMessage da esaminare
	 * @return String del boundary, in caso di ricerca con successo, null altrimenti.
	 * 
	 */
	public static String findBoundary(byte [] xml){

		ByteArrayOutputStream boundary = null;
		try{

			//if(!messageWithAttachment(xml))
			//  return null;

			boundary = new ByteArrayOutputStream();
			int i=0;
			while(i<xml.length){
				boundary.write(xml[i]);
				if(xml[i]=='\n' || i==xml.length){
					if(boundary.toString().startsWith("--"))
						break;
					else
						boundary.reset();
				}
				i++;
			}

			String bS = null;
			if(boundary.size() != 0)
				bS = boundary.toString().substring(0,boundary.toString().length()-2);

			boundary.close();
			return bS;

		}catch(Exception e){
			try{
				if(boundary!=null)
					boundary.close();
			}catch(Exception eis){}
			return null;
		}
	}


	/**
	 * Trova la stringa utilizzata, all'interno del SOAPMessage <var>xml</var>,
	 * per delimitare i vari attachments presenti.
	 *
	 * @param inputXML InputStream del SOAPMessage da esaminare
	 * @return String del boundary, in caso di ricerca con successo, null altrimenti.
	 * 
	 */
	public static String findBoundary(InputStream inputXML){

		ByteArrayOutputStream boundary = null;
		try{

			//if(!messageWithAttachment(xml))
			//  return null;

			boundary = new ByteArrayOutputStream();
			byte date = 0;
			while((date=(byte)inputXML.read())!=-1){
				boundary.write(date);
				if(((char)date)=='\n'){
					if(boundary.toString().startsWith("--"))
						break;
					else
						boundary.reset();
				}
			}

			String bS = null;
			if(boundary.size() != 0)
				bS = boundary.toString().substring(0,boundary.toString().length()-2);

			boundary.close();
			return bS;

		}catch(Exception e){
			try{
				if(boundary!=null)
					boundary.close();
			}catch(Exception eis){}
			return null;
		}
	}




	/**
	 * Esamina il contenuto del messaggio <var>xml</var>, per vedere se sono presenti o meno degli attachments.
	 *
	 * @param xml byte[] del SOAPMessage da esaminare
	 * @return true in caso di presenza di attachments, false altrimenti.
	 * 
	 */
	public static boolean messageWithAttachment(byte [] xml){
		// read first line
		if(xml.length < 10)
			return false;

		// Cerco -- nei primi 10 caratteri, e che non incontro <
		for(int i=0; i<9; i++){
			if( ((char)xml[i] == '-') &&  ((char)xml[i+1] == '-') ){
				return true;
			}else if( (char)xml[i] == '<'  )
				return false;
		}

		return false;
	}




	/**
	 * Trova il Content-ID utilizzato, all'interno del SOAPMessage <var>xml</var>,
	 * dal blocco di dati che contiene il SOAPEnvelope.
	 *
	 * @param xml byte[] del SOAPMessage da esaminare
	 * @return String del ContentID, in caso di ricerca con successo, null altrimenti.
	 * 
	 */
	public static String firstContentID(byte [] xml){

		ByteArrayOutputStream line = null;
		try{

			//if(!messageWithAttachment(xml))
			//return null;

			//log.info("Check ID First");
			String IDfirst = null;
			int index=0;
			boolean found = false;
			while(!found && index<xml.length){
				line  = new ByteArrayOutputStream();
				while(true){
					if(xml[index]=='\r'){
						index++;
						if(xml[index]=='\n'){
							index++;//elimino anche \n
						}
						break;
					} 
					line.write(xml[index]);
					index++;
				}
				if (line.toString().toLowerCase().startsWith("Content-Id:".toLowerCase())){
					found = true;
					String[] rr = line.toString().split(" ");
					//log.info("ID FIRST ["+rr[1]+"]");
					IDfirst = rr[1];
				}
				line.close();
			}  

			return IDfirst;

		}catch(Exception e){
			try{
				if(line!=null)
					line.close();
			}catch(Exception eis){}
			return null;
		}

	}
	
	public static SOAPVersion getSOAPVersion(Logger log, byte [] xml){
//		if(messageWithAttachment(xml)){
//			String s = new String(xml);
//			if(s.contains(Costanti.SOAP_ENVELOPE_NAMESPACE)){
//				return SOAPVersion.SOAP11;
//			}
//			else if(s.contains(Costanti.SOAP12_ENVELOPE_NAMESPACE)){
//				return SOAPVersion.SOAP12;
//			}
//		}
//		else{
//			try{
//				org.w3c.dom.Element e = XMLUtils.getInstance().newElement(xml);
//				if(e.getLocalName()!=null && "Envelope".equals(e.getLocalName())){
//					if(Costanti.SOAP_ENVELOPE_NAMESPACE.equals(e.getNamespaceURI())){
//						return SOAPVersion.SOAP11;
//					}
//					else if(Costanti.SOAP12_ENVELOPE_NAMESPACE.equals(e.getNamespaceURI())){
//						return SOAPVersion.SOAP12;
//					}
//				}		
//			}catch(Exception e){
//				if(log!=null){
//					log.error(e.getMessage(),e);
//				}
//			}
//		}
		// La posizione dovrebbe garantirmi il giusto namespace
		// Nel caso all'interno del body viene usato l'altro.
		String s = new String(xml);
		if( (s.contains("<Envelope") || s.contains(":Envelope") ) ){
			int indexOfSoap11 = s.indexOf(Costanti.SOAP_ENVELOPE_NAMESPACE);
			int indexOfSoap12 = s.indexOf(Costanti.SOAP12_ENVELOPE_NAMESPACE);
			if(indexOfSoap11>0 && indexOfSoap12>0){
				if(indexOfSoap11<indexOfSoap12){
					return SOAPVersion.SOAP11;
				}
				else{
					return SOAPVersion.SOAP12;
				}
			}
			else if(indexOfSoap11>0){
				return SOAPVersion.SOAP11;
			}
			else if(indexOfSoap12>0){
				return SOAPVersion.SOAP12;
			}
		}
		return null;
	}



	/**
	 * Dato un soapMessage, <var>xml</var> con attachments, 
	 * popola i Vector passati con i parametri, rispettivamente per ogni attachment,  con :
	 * <ul>
	 *   <li> byte[] dell'attachment : parametro <var>attachment</var>,
	 *   <li> ContentType : parametro <var>attachmentType</var>,
	 *   <li> ContentID : parametro <var>attachmentID</var>,
	 * </ul>
	 *
	 * @deprecated  utility inefficiente, utilizzare costruttore apposito di Message(inputStream,isBodyStream,ContentType,ContenLocation)
	 * @param xml byte[] del SOAPMessage da esaminare
	 * @param attachment Vector che sara' riempito dai byte[] di ogni attachment trovato.
	 * @param attachmentType Vector che sara' riempito da String rappresentanti il Content-Type di ogni attachment trovato.
	 * @param attachmentID Vector che sara' riempito da String rappresentanti il Content-ID di ogni attachment trovato.
	 * @return byte[] del SOAPEnvelope trovato, in caso di mapping con successo, null altrimenti.
	 * 
	 */
	@Deprecated public static byte[] mappingXMLwithAttachments(byte [] xml , 
			Vector<byte[]> attachment,
			Vector<String> attachmentType,
			Vector<String> attachmentID ){
		ByteArrayInputStream bin = new ByteArrayInputStream(xml);
		byte[] risultato =  AttachmentsUtils.mappingXMLwithAttachments(bin,attachment,attachmentType,attachmentID);
		try{
			bin.close();
		}catch(Exception e){}
		return risultato;
	}


	/**
	 * Dato un soapMessage, <var>xml</var> con attachments, 
	 * popola i Vector passati con i parametri, rispettivamente per ogni attachment,  con :
	 * <ul>
	 *   <li> byte[] dell'attachment : parametro <var>attachment</var>,
	 *   <li> ContentType : parametro <var>attachmentType</var>,
	 *   <li> ContentID : parametro <var>attachmentID</var>,
	 * </ul>
	 *
	 * @deprecated  utility inefficiente, utilizzare costruttore apposito di Message(inputStream,isBodyStream,ContentType,ContenLocation)
	 * @param inputXML InputStream del SOAPMessage da esaminare
	 * @param attachment Vector che sara' riempito dai byte[] di ogni attachment trovato.
	 * @param attachmentType Vector che sara' riempito da String rappresentanti il Content-Type di ogni attachment trovato.
	 * @param attachmentID Vector che sara' riempito da String rappresentanti il Content-ID di ogni attachment trovato.
	 * @return byte[] del SOAPEnvelope trovato, in caso di mapping con successo, null altrimenti.
	 * 
	 */
	@Deprecated public static byte[] mappingXMLwithAttachments(InputStream inputXML , 
			Vector<byte[]> attachment,
			Vector<String> attachmentType,
			Vector<String> attachmentID ){	
		ByteArrayOutputStream soapEnvelope = null;
		try{

			// read -----part Consumo Primo Boundary
			String boundary = AttachmentsUtils.findBoundary(inputXML);
			if(boundary == null){
				return null;
			}

			soapEnvelope = new ByteArrayOutputStream();

			boolean findSoapEnvelope = false;
			// Mapping
			//log.info("Start Mapping process");

			byte date = 0;

			// read fino a boundary
			/*
	    while((date=(byte)inputXML.read())!=-1){

		ByteArrayOutputStream line  = new ByteArrayOutputStream();
		while(true){
		    line.write(date);
		    if(date=='\n'){
			break;
		    } 
		    date=(byte)inputXML.read();
		}

		// trovato boundary primo boundary ??
		if(line.toString().startsWith(boundary))
		    break;
	    }
			 */

			while(date != -1){
				//log.info("check header");
				// Read Header
				boolean existHeader = true;
				while(existHeader){
					ByteArrayOutputStream lineHeader  = new ByteArrayOutputStream();
					while(true){
						lineHeader.write(date); 
						if(date=='\n'){
							date=(byte)inputXML.read();
							break;
						}
						date=(byte)inputXML.read();
					}
					if(lineHeader.toString().equals("\r\n"))
						existHeader = false;
					else{
						//log.info("found header ["+lineHeader.toString()+"]");
						if(lineHeader.toString().startsWith("Content-Type")){
							String[] split = lineHeader.toString().split(" ");
							if(findSoapEnvelope == true)
								attachmentType.add(split[1].substring(0,(split[1].length()-2)));
						}
						if(lineHeader.toString().startsWith("Content-Id")){
							String[] split = lineHeader.toString().split(" ");
							if(findSoapEnvelope == true)
								attachmentID.add(split[1].substring(1,(split[1].length()-3)));
						}
					}
				}
				//log.info("Calcolate contenute...");
				ByteArrayOutputStream body  = new ByteArrayOutputStream();
				boolean endBody = false; 
				while(!endBody){
					while(true){
						if(date == '\r'){
							//log.info("find potential boundary [-r]");

							date=(byte)inputXML.read();

							if(date == '\n'){
								//log.info("find potential boundary [-n]");

								date=(byte)inputXML.read();

								int indexBoundary = 0;
								//string BoundaryCheck;
								while( ((char)date) == boundary.charAt(indexBoundary) ){
									//log.info("examine DATE["+(char)date+"] == BOUN["+boundary.charAt(indexBoundary)+"] with index["+indexBoundary+"]");
									indexBoundary++;
									date=(byte)inputXML.read();

									if (!(indexBoundary <  boundary.length() ))
										break;
								}


								if(indexBoundary ==  boundary.length()){	
									endBody = true;
									break;
								}    
								else{
									body.write('\r');
									body.write('\n');
									body.write(boundary.getBytes(),0,indexBoundary);
								}
							}else{
								body.write('\r');
							}
						}else{
							body.write(date);
							date=(byte)inputXML.read();
						}
					}
				}
				if(findSoapEnvelope == false){
					soapEnvelope.write(body.toByteArray(),0,body.toByteArray().length);
					findSoapEnvelope = true;
				}
				else{
					attachment.add(body.toByteArray());
				}

				// Controllo che non sia un altro -, senno indica la fine del file (finisce con --\r\n)
				// Controllo comunque che finisca con --\r\n, ma se non e' cosi' devo segnalare un errore!!!!!!!
				if(((char)date) == '-'){
					byte date1=(byte)inputXML.read();
					byte date2=(byte)inputXML.read();
					byte date3=(byte)inputXML.read();
					if ( (((char)date1) == '-') &&
							(((char)date2) == '\r') &&
							(((char)date3) == '\n') )
						break;
					//else{
					//ERRORE
					//}    
				}
				// Altrimenti mi bruci il \r\n che e' presente alla fine del bound
				date=(byte)inputXML.read(); //bruciato \r
				date=(byte)inputXML.read(); //bruciato \n  

			}  

			byte [] risultato = soapEnvelope.toByteArray();
			soapEnvelope.close();
			return risultato;

		}  catch(Exception e){
			try{
				if(soapEnvelope!=null)
					soapEnvelope.close();
			}catch(Exception eis){}
			return null;	   
		}
	}



}

