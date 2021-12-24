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



package org.openspcoop2.core.eccezione.errore_applicativo.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPFault;

import org.openspcoop2.core.eccezione.errore_applicativo.DatiCooperazione;
import org.openspcoop2.core.eccezione.errore_applicativo.Dominio;
import org.openspcoop2.core.eccezione.errore_applicativo.Eccezione;
import org.openspcoop2.core.eccezione.errore_applicativo.ErroreApplicativo;
import org.openspcoop2.core.eccezione.errore_applicativo.Servizio;
import org.openspcoop2.core.eccezione.errore_applicativo.Soggetto;
import org.openspcoop2.core.eccezione.errore_applicativo.constants.Costanti;
import org.openspcoop2.core.eccezione.errore_applicativo.utils.serializer.JsonJacksonDeserializer;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.OpenSPCoop2MessageFactory;
import org.openspcoop2.message.OpenSPCoop2RestJsonMessage;
import org.openspcoop2.message.OpenSPCoop2RestXmlMessage;
import org.openspcoop2.message.OpenSPCoop2SoapMessage;
import org.openspcoop2.message.xml.ValidatoreXSD;
import org.openspcoop2.utils.beans.WriteToSerializerType;
import org.slf4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;


/**
 * Classe utilizzata per la generazione il detail errore-applicativo inseriti in un SOAPFault generato dalla PdD in caso di errore di processamento
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class XMLUtils  {

	/** Validatore XSD */
	static ValidatoreXSD validatoreXSD = null;
	private static synchronized void initValidatoreXSD(Logger log) throws Exception{
		if(XMLUtils.validatoreXSD==null){
			XMLUtils.validatoreXSD = new ValidatoreXSD(OpenSPCoop2MessageFactory.getDefaultMessageFactory(), log,XMLUtils.class.getResourceAsStream("/openspcoopErroreApplicativo.xsd"));
		}
	}
	public static ValidatoreXSD getValidatoreXSD(Logger log) throws Exception{
		if(XMLUtils.validatoreXSD==null){
			initValidatoreXSD(log);
		}
		return XMLUtils.validatoreXSD;
	}
	public static boolean validate(ErroreApplicativo erroreApplicativo,StringBuilder motivoErroreValidazione){
		
		int size = motivoErroreValidazione.length();
		
		if(erroreApplicativo.getDomain()==null){
			motivoErroreValidazione.append("Dominio non definito\n");
		}
		else{
			validate(erroreApplicativo.getDomain(), motivoErroreValidazione);
		}
		
		if(erroreApplicativo.getTimestamp()==null){
			motivoErroreValidazione.append("OraRegistrazione non definita\n");
		}
		
		if(erroreApplicativo.getService()!=null){
			validate(erroreApplicativo.getService(), motivoErroreValidazione);
		}
		
		validate(erroreApplicativo.getException(),motivoErroreValidazione);	
		
		if(motivoErroreValidazione.length()!=size)
			return false;
		else
			return true;

	}
	private static void validate(Dominio dominio,StringBuilder motivoErroreValidazione){
		if(dominio.getId()==null){
			motivoErroreValidazione.append("Dominio.id non definito\n");
		}
		if(dominio.getRole()==null){
			motivoErroreValidazione.append("Dominio.funzione non definito\n");
		}
		if(dominio.getModule()==null){
			motivoErroreValidazione.append("Dominio.modulo non definito\n");
		}
		if(dominio.getOrganization()==null){
			motivoErroreValidazione.append("Dominio.soggetto non definita\n");
		}
		else{
			if(dominio.getOrganization().getType()==null){
				motivoErroreValidazione.append("Dominio.soggetto.tipo non definita\n");
			}
			if(dominio.getOrganization().getBase()==null){
				motivoErroreValidazione.append("Dominio.soggetto.nome non definita\n");
			}
		}
	}
	private static void validate(DatiCooperazione datiCooperazione,StringBuilder motivoErroreValidazione){
		if(datiCooperazione.getSender()!=null){
			validate(datiCooperazione.getSender(), motivoErroreValidazione, "mittente");
		}
		if(datiCooperazione.getProvider()!=null){
			validate(datiCooperazione.getProvider(), motivoErroreValidazione , "destinatario");
		}
		if(datiCooperazione.getService()!=null){
			validate(datiCooperazione.getService(), motivoErroreValidazione);
		}
	}
	private static void validate(Soggetto soggetto,StringBuilder motivoErroreValidazione,String tipo){
		if(soggetto.getId()==null){
			motivoErroreValidazione.append("DatiCooperazione."+tipo+".identificativo non definita\n");
		}
		else{
			if(soggetto.getId().getType()==null){
				motivoErroreValidazione.append("DatiCooperazione."+tipo+".identificativo.tipo non definita\n");
			}
			if(soggetto.getId().getBase()==null){
				motivoErroreValidazione.append("DatiCooperazione."+tipo+".identificativo.base non definita\n");
			}
		}
		if(soggetto.getDomainId()==null){
			motivoErroreValidazione.append("DatiCooperazione."+tipo+".identificativoPorta non definita\n");
		}
	}
	private static void validate(Servizio servizio,StringBuilder motivoErroreValidazione){
		if(servizio.getBase()==null){
			motivoErroreValidazione.append("DatiCooperazione.servizio.base non definita\n");
		}
		if(servizio.getType()==null){
			motivoErroreValidazione.append("DatiCooperazione.servizio.tipo non definita\n");
		}
	}
	private static void validate(Eccezione eccezione,StringBuilder motivoErroreValidazione){
		if(eccezione==null){
			motivoErroreValidazione.append("Eccezione non definita\n");
		}
		else{
			if(eccezione.getCode()==null){
				motivoErroreValidazione.append("Eccezione.codice non definito\n");
			}
			else{
				if(eccezione.getCode().getBase()==null){
					motivoErroreValidazione.append("Eccezione.codice.base non definito\n");
				}
				if(eccezione.getCode().getType()==null){
					motivoErroreValidazione.append("Eccezione.codice.tipo non definito\n");
				}
			}
			if(eccezione.getDescription()==null){
				motivoErroreValidazione.append("Eccezione.descrizione non definito\n");
			}
			if(eccezione.getType()==null){
				motivoErroreValidazione.append("Eccezione.tipo non definito\n");
			}
		}
	}
	
	
	/* ----- Unmarshall ----- */
	
	/**
	 * Ritorna la rappresentazione java
	 * 
	 * @param m byte[]
	 * @return ErroreApplicativo
	 * @throws XMLUtilsException
	 */
	public static ErroreApplicativo getErroreApplicativo(Logger log,byte[] m) throws XMLUtilsException{
		ByteArrayInputStream bin = null;
		try{
			bin = new ByteArrayInputStream(m);
			return XMLUtils.getErroreApplicativo(log,bin);
		}catch(Exception e){
			throw new XMLUtilsException(e.getMessage(),e);
		}finally{
			try{
				if(bin!=null)
					bin.close();
			}catch(Exception eClose){}
		}
	}
	
	/**
	 * Ritorna la rappresentazione java
	 * 
	 * @param m File
	 * @return ErroreApplicativo
	 * @throws XMLUtilsException
	 */
	public static ErroreApplicativo getErroreApplicativo(Logger log,File m) throws XMLUtilsException{
		FileInputStream fin = null;
		try{
			fin = new FileInputStream(m);
			return XMLUtils.getErroreApplicativo(log,fin);
		}catch(Exception e){
			throw new XMLUtilsException(e.getMessage(),e);
		}finally{
			try{
				if(fin!=null)
					fin.close();
			}catch(Exception eClose){}
		}
	}
	
	/**
	 * Ritorna la rappresentazione java
	 * 
	 * @param m String
	 * @return ErroreApplicativo
	 * @throws XMLUtilsException
	 */
	public static ErroreApplicativo getErroreApplicativo(Logger log,String m) throws XMLUtilsException{
		return XMLUtils.getErroreApplicativo(log,m.getBytes());
	}
	
	/**
	 * Ritorna la rappresentazione java
	 * 
	 * @param m InputStream
	 * @return ErroreApplicativo
	 * @throws XMLUtilsException
	 */
	public static ErroreApplicativo getErroreApplicativo(Logger log,InputStream m) throws XMLUtilsException{
		try{
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			byte[]reads = new byte[1024];
			int letti = 0;
			while( (letti=m.read(reads)) != -1){
				bout.write(reads, 0, letti);
			}
			bout.flush();
			bout.close();
			byte [] xml = bout.toByteArray();
			
			// Validazione XSD
			ByteArrayInputStream binValidazione = new ByteArrayInputStream(xml);
			ValidatoreXSD validatoreXSD = XMLUtils.getValidatoreXSD(log);
			validatoreXSD.valida(binValidazione);
			
			// trasformazione in oggetto ErroreApplicativo
			ByteArrayInputStream binTrasformazione = new ByteArrayInputStream(xml);
			return (ErroreApplicativo) org.openspcoop2.utils.xml.JaxbUtils.xmlToObj(binTrasformazione, ErroreApplicativo.class);
			
		}catch(Exception e){
			throw new XMLUtilsException(e.getMessage(),e);
		}
	}
	
	public static ErroreApplicativo getErroreApplicativoFromJson(Logger log,InputStream is) throws XMLUtilsException{
		try{			
			JsonJacksonDeserializer deserializer = new JsonJacksonDeserializer();
			return deserializer.readErroreApplicativo(is);
		}catch(Exception e){
			throw new XMLUtilsException(e.getMessage(),e);
		}
	}

	
	
	
	
	/* ----- Marshall ----- */
	public static void generateErroreApplicativo(ErroreApplicativo eccezione,File out) throws XMLUtilsException{
		try{
			StringBuilder risultatoValidazione = new StringBuilder();
			if(XMLUtils.validate(eccezione, risultatoValidazione)==false){
				throw new Exception(risultatoValidazione.toString());
			}
			org.openspcoop2.utils.xml.JaxbUtils.objToXml(out.getName(),XMLUtils.generateErroreApplicativo_engine(eccezione));
		}catch(Exception e){
			throw new XMLUtilsException(e.getMessage(),e);
		}
	}
	
	public static void generateErroreApplicativo(ErroreApplicativo eccezione,String fileName) throws XMLUtilsException{
		try{
			StringBuilder risultatoValidazione = new StringBuilder();
			if(XMLUtils.validate(eccezione, risultatoValidazione)==false){
				throw new Exception(risultatoValidazione.toString());
			}
			org.openspcoop2.utils.xml.JaxbUtils.objToXml(fileName,XMLUtils.generateErroreApplicativo_engine(eccezione));
		}catch(Exception e){
			throw new XMLUtilsException(e.getMessage(),e);
		}
	}
	
	public static byte[] generateErroreApplicativo(ErroreApplicativo eccezione) throws XMLUtilsException{
		try{
			StringBuilder risultatoValidazione = new StringBuilder();
			if(XMLUtils.validate(eccezione, risultatoValidazione)==false){
				throw new Exception(risultatoValidazione.toString());
			}
			return XMLUtils.generateErroreApplicativo_engine(eccezione);
		}catch(Exception e){
			throw new XMLUtilsException(e.getMessage(),e);
		}
	}
	
	public static String generateErroreApplicativoAsJson(ErroreApplicativo eccezione) throws XMLUtilsException{
		try{
			StringBuilder risultatoValidazione = new StringBuilder();
			if(XMLUtils.validate(eccezione, risultatoValidazione)==false){
				throw new Exception(risultatoValidazione.toString());
			}
			return XMLUtils.generateErroreApplicativoAsJson_engine(eccezione);
		}catch(Exception e){
			throw new XMLUtilsException(e.getMessage(),e);
		}
	}

	public static void generateErroreApplicativo(ErroreApplicativo eccezione,OutputStream out) throws XMLUtilsException{
		try{
			StringBuilder risultatoValidazione = new StringBuilder();
			if(XMLUtils.validate(eccezione, risultatoValidazione)==false){
				throw new Exception(risultatoValidazione.toString());
			}
			out.write(XMLUtils.generateErroreApplicativo_engine(eccezione));
			out.flush();
			out.close();
		}catch(Exception e){
			throw new XMLUtilsException(e.getMessage(),e);
		}
	}
	
	private static byte[] generateErroreApplicativo_engine(ErroreApplicativo eccezione) throws XMLUtilsException{
		try{
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			org.openspcoop2.utils.xml.JaxbUtils.objToXml(bout, ErroreApplicativo.class, eccezione);
			bout.flush();
			bout.close();
			return bout.toByteArray();
		}catch(Exception e){
			throw new XMLUtilsException(e.getMessage(),e);
		}
	}
	
	private static String generateErroreApplicativoAsJson_engine(ErroreApplicativo eccezione) throws XMLUtilsException{
		try{
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			eccezione.writeTo(bout, WriteToSerializerType.JSON_JACKSON);
			bout.flush();
			bout.close();
			return bout.toString();
		}catch(Exception e){
			throw new XMLUtilsException(e.getMessage(),e);
		}
	}
	
	
	
	public static ErroreApplicativo getErroreApplicativo(Logger log,OpenSPCoop2Message msg)throws XMLUtilsException{
		switch (msg.getMessageType()) {
		case SOAP_11:
		case SOAP_12:
			try{
				return getErroreApplicativo(log, msg.castAsSoap());
			}catch(XMLUtilsException e){
				throw e;
			}catch(Exception e){
				throw new XMLUtilsException(e.getMessage(),e);
			}
		case XML:
			try{
				OpenSPCoop2RestXmlMessage rest = msg.castAsRestXml();
				if(rest.hasContent()){
					Element element = rest.getContent();
					if(XMLUtils.isErroreApplicativo(element)){
						org.openspcoop2.message.xml.XMLUtils xmlUtils = org.openspcoop2.message.xml.XMLUtils.DEFAULT;
						byte [] xml = xmlUtils.toByteArray(element);
						//System.out.println("XML S: "+new String(xml));
						ErroreApplicativo errore = XMLUtils.getErroreApplicativo(log,xml);
						return errore;
					}
				}
				return null;
			}catch(XMLUtilsException e){
				throw e;
			}catch(Exception e){
				throw new XMLUtilsException(e.getMessage(),e);
			}
		case JSON:
			try{
				OpenSPCoop2RestJsonMessage rest = msg.castAsRestJson();
				if(rest.hasContent()){
					String json = rest.getContent();
					try{
						ErroreApplicativo errore = XMLUtils.getErroreApplicativoFromJson(log, new ByteArrayInputStream(json.getBytes()));
						return errore;
					}catch(Exception e){}
				}
				return null;
			}catch(Exception e){
				throw new XMLUtilsException(e.getMessage(),e);
			}
		default:
			return null;
		}
	}
	public static ErroreApplicativo getErroreApplicativo(Logger log,OpenSPCoop2SoapMessage msg)throws XMLUtilsException{
		try{
			if(msg==null)
				throw new XMLUtilsException("Messaggio non presente");
			SOAPBody soapBody = msg.getSOAPBody();
			if(soapBody==null)
				throw new XMLUtilsException("SOAPBody non presente");
			SOAPFault faultOriginale = null;
			if(soapBody.hasFault()==false)
				return null; // casi di buste mal formate, verranno segnalate dal validatore
			else
				faultOriginale = soapBody.getFault();
			if(faultOriginale==null)
				throw new XMLUtilsException("SOAPFault is null");
			
			QName nameDetail = new QName("detail");
			Iterator<?> itSF = faultOriginale.getChildElements(nameDetail);
			SOAPElement detailsFaultOriginale = null;
			if(itSF.hasNext()){
				detailsFaultOriginale = (SOAPElement) itSF.next();
			}
				
			msg.saveChanges();
				
			if(detailsFaultOriginale!=null){
	            Iterator<?> it = detailsFaultOriginale.getChildElements();
				while (it.hasNext()) {
					Object o = it.next();
					if(o instanceof SOAPElement){
						SOAPElement elem = (SOAPElement) o;
						try{
							if(XMLUtils.isErroreApplicativo(elem)){
								//System.out.println("ITEM ["+elem.getLocalName()+"] TROVATO");
								org.openspcoop2.message.xml.XMLUtils xmlUtils = org.openspcoop2.message.xml.XMLUtils.DEFAULT;
								byte [] xml = xmlUtils.toByteArray(elem);
								//System.out.println("XML S: "+new String(xml));
								ErroreApplicativo de = XMLUtils.getErroreApplicativo(log,xml);
								return de;
							}
						}catch(Exception e){
							e.printStackTrace(System.out);
						}
					}
				}
            }
				
			return null;
		}catch(Exception e){
			throw new XMLUtilsException(e.getMessage(),e);
		}
	}
	
	public static boolean isErroreApplicativo(byte [] doc){
		try{
			org.openspcoop2.message.xml.XMLUtils xmlUtils = org.openspcoop2.message.xml.XMLUtils.DEFAULT;
			Document docXML = xmlUtils.newDocument(doc);
			Element elemXML = docXML.getDocumentElement();
			return XMLUtils.isErroreApplicativo_engine(elemXML);
		}catch(Exception e){
			//System.out.println("NON e' un DOCUMENTO VALIDO: "+e.getMessage());
			return false;
		}
	}
	public static boolean isErroreApplicativo(Document docXML){
		try{
			Element elemXML = docXML.getDocumentElement();
			return XMLUtils.isErroreApplicativo_engine(elemXML);
		}catch(Exception e){
			//System.out.println("NON e' un DOCUMENTO VALIDO: "+e.getMessage());
			return false;
		}
	}
	public static boolean isErroreApplicativo(Element elemXML){
		return isErroreApplicativo_engine(elemXML);
	}
	public static boolean isErroreApplicativo(Node nodeXml){
		return isErroreApplicativo_engine(nodeXml);
	}
	private static boolean isErroreApplicativo_engine(Node nodeXml){
		try{
			//System.out.println("LOCAL["+Costanti.ROOT_LOCAL_NAME_ERRORE_APPLICATIVO+"]vs["+nodeXml.getLocalName()+"]  NAMESPACE["+Costanti.TARGET_NAMESPACE+"]vs["+nodeXml.getNamespaceURI()+"]");
			if(Costanti.ROOT_LOCAL_NAME_ERRORE_APPLICATIVO.equals(nodeXml.getLocalName()) && 
					Costanti.TARGET_NAMESPACE.equals(nodeXml.getNamespaceURI() ) 
				){
				return true;
			}
			else{
				return false;
			}
		}catch(Exception e){
			//System.out.println("NON e' un DOCUMENTO VALIDO: "+e.getMessage());
			return false;
		}
	}
	
}