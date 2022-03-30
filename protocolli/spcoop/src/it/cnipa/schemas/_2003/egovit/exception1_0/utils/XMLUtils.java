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



package it.cnipa.schemas._2003.egovit.exception1_0.utils;

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

import org.openspcoop2.message.OpenSPCoop2SoapMessage;
import org.openspcoop2.utils.beans.WriteToSerializerType;
import org.openspcoop2.utils.xml.AbstractValidatoreXSD;
import org.slf4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import it.cnipa.schemas._2003.egovit.exception1_0.Eccezione;
import it.cnipa.schemas._2003.egovit.exception1_0.EccezioneBusta;
import it.cnipa.schemas._2003.egovit.exception1_0.EccezioneProcessamento;
import it.cnipa.schemas._2003.egovit.exception1_0.MessaggioDiErroreApplicativo;
import it.cnipa.schemas._2003.egovit.exception1_0.utils.serializer.JsonJacksonDeserializer;


/**
 * Classe utilizzata per la generazione MessaggioDiErroreApplicativo
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class XMLUtils  {


	public static boolean validate(MessaggioDiErroreApplicativo erroreApplicativo,StringBuilder motivoErroreValidazione){
		
		int size = motivoErroreValidazione.length();
		
		if(erroreApplicativo.getOraRegistrazione()==null){
			motivoErroreValidazione.append("OraRegistrazione non definita\n");
		}
		
		if(erroreApplicativo.getIdentificativoPorta()==null){
			motivoErroreValidazione.append("IdentificativoPorta non definito\n");
		}
		
		if(erroreApplicativo.getIdentificativoFunzione()==null){
			motivoErroreValidazione.append("IdentificativoFunzione non definito\n");
		}

		
		validate(erroreApplicativo.getEccezione(),motivoErroreValidazione);	
		
		if(motivoErroreValidazione.length()!=size)
			return false;
		else
			return true;

	}

	private static void validate(Eccezione eccezione,StringBuilder motivoErroreValidazione){
		if(eccezione==null){
			motivoErroreValidazione.append("Eccezione non definita\n");
		}
		else{
			if(eccezione.getEccezioneBusta()==null && eccezione.getEccezioneProcessamento()==null){
				motivoErroreValidazione.append("Nessuna eccezione (busta o processamento) definita\n");
			}
			else if(eccezione.getEccezioneBusta()!=null && eccezione.getEccezioneProcessamento()!=null){
				motivoErroreValidazione.append("Entrambe le eccezioni (busta o processamento) sono definite\n");
			}
			else{
				if(eccezione.getEccezioneBusta()!=null){
					validate(eccezione.getEccezioneBusta(),motivoErroreValidazione);
				}
				else{
					validate(eccezione.getEccezioneProcessamento(),motivoErroreValidazione);
				}
			}
		}
	}
	
	private static void validate(EccezioneBusta eccezione,StringBuilder motivoErroreValidazione){
		if(eccezione==null){
			motivoErroreValidazione.append("EccezioneBusta non definita\n");
		}
		else{
			if(eccezione.getCodiceEccezione()==null){
				motivoErroreValidazione.append("EccezioneBusta.codiceEccezione non definito\n");
			}
			if(eccezione.getDescrizioneEccezione()==null){
				motivoErroreValidazione.append("EccezioneBusta.descrizioneEccezione non definito\n");
			}
		}
	}
	
	private static void validate(EccezioneProcessamento eccezione,StringBuilder motivoErroreValidazione){
		if(eccezione==null){
			motivoErroreValidazione.append("EccezioneProcessamento non definita\n");
		}
		else{
			if(eccezione.getCodiceEccezione()==null){
				motivoErroreValidazione.append("EccezioneProcessamento.codiceEccezione non definito\n");
			}
			if(eccezione.getDescrizioneEccezione()==null){
				motivoErroreValidazione.append("EccezioneProcessamento.descrizioneEccezione non definito\n");
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
	public static MessaggioDiErroreApplicativo getErroreApplicativo(Logger log,byte[] m) throws XMLUtilsException{
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
	public static MessaggioDiErroreApplicativo getErroreApplicativo(Logger log,File m) throws XMLUtilsException{
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
	public static MessaggioDiErroreApplicativo getErroreApplicativo(Logger log,String m) throws XMLUtilsException{
		return XMLUtils.getErroreApplicativo(log,m.getBytes());
	}
	
	/**
	 * Ritorna la rappresentazione java
	 * 
	 * @param m InputStream
	 * @return ErroreApplicativo
	 * @throws XMLUtilsException
	 */
	public static MessaggioDiErroreApplicativo getErroreApplicativo(Logger log,InputStream m) throws XMLUtilsException{
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
			AbstractValidatoreXSD validatoreXSD = XSDValidator.getXSDValidator(log);
			validatoreXSD.valida(binValidazione);
			
			// trasformazione in oggetto ErroreApplicativo
			ByteArrayInputStream binTrasformazione = new ByteArrayInputStream(xml);
			return (MessaggioDiErroreApplicativo) org.openspcoop2.utils.xml.JaxbUtils.xmlToObj(binTrasformazione, MessaggioDiErroreApplicativo.class);
			
		}catch(Exception e){
			throw new XMLUtilsException(e.getMessage(),e);
		}
	}
	
	public static MessaggioDiErroreApplicativo getErroreApplicativoFromJson(Logger log,InputStream is) throws XMLUtilsException{
		try{			
			JsonJacksonDeserializer deserializer = new JsonJacksonDeserializer();
			return deserializer.readMessaggioDiErroreApplicativo(is);
		}catch(Exception e){
			throw new XMLUtilsException(e.getMessage(),e);
		}
	}

	
	
	
	
	/* ----- Marshall ----- */
	public static void generateErroreApplicativo(MessaggioDiErroreApplicativo eccezione,File out) throws XMLUtilsException{
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
	
	public static void generateErroreApplicativo(MessaggioDiErroreApplicativo eccezione,String fileName) throws XMLUtilsException{
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
	
	public static byte[] generateErroreApplicativo(MessaggioDiErroreApplicativo eccezione) throws XMLUtilsException{
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
	
	public static String generateErroreApplicativoAsJson(MessaggioDiErroreApplicativo eccezione) throws XMLUtilsException{
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

	public static void generateErroreApplicativo(MessaggioDiErroreApplicativo eccezione,OutputStream out) throws XMLUtilsException{
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
	
	private static byte[] generateErroreApplicativo_engine(MessaggioDiErroreApplicativo eccezione) throws XMLUtilsException{
		try{
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			org.openspcoop2.utils.xml.JaxbUtils.objToXml(bout, MessaggioDiErroreApplicativo.class, eccezione);
			bout.flush();
			bout.close();
			return bout.toByteArray();
		}catch(Exception e){
			throw new XMLUtilsException(e.getMessage(),e);
		}
	}
	
	private static String generateErroreApplicativoAsJson_engine(MessaggioDiErroreApplicativo eccezione) throws XMLUtilsException{
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
	
	
	
	
	public static MessaggioDiErroreApplicativo getErroreApplicativo(Logger log,OpenSPCoop2SoapMessage msg)throws XMLUtilsException{
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
								MessaggioDiErroreApplicativo de = XMLUtils.getErroreApplicativo(log,xml);
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
			//System.out.println("LOCAL["+Costanti.ROOT_LOCAL_NAME_DETTAGLIO_ECCEZIONE+"]vs["+elemXML.getLocalName()+"]  NAMESPACE["+Costanti.TARGET_NAMESPACE+"]vs["+elemXML.getNamespaceURI()+"]");
			if("MessaggioDiErroreApplicativo".equals(nodeXml.getLocalName()) && 
					ProjectInfo.getInstance().getProjectNamespace().equals(nodeXml.getNamespaceURI() ) 
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