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



package org.openspcoop2.core.eccezione.router_details.utils;

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

import org.slf4j.Logger;
import org.openspcoop2.core.eccezione.router_details.DettaglioRouting;
import org.openspcoop2.core.eccezione.router_details.Dominio;
import org.openspcoop2.core.eccezione.router_details.constants.Costanti;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.ValidatoreXSD;
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
	public static synchronized ValidatoreXSD getValidatoreXSD(Logger log) throws Exception{
		if(XMLUtils.validatoreXSD==null){
			XMLUtils.validatoreXSD = new ValidatoreXSD(log,XMLUtils.class.getResourceAsStream("/openspcoopRouterDetail.xsd"));
		}
		return XMLUtils.validatoreXSD;
	}
	public static boolean validate(DettaglioRouting dettaglioRouting,StringBuffer motivoErroreValidazione){
		
		int size = motivoErroreValidazione.length();
		
		if(dettaglioRouting.getDominio()==null){
			motivoErroreValidazione.append("Dominio non definito\n");
		}
		else{
			validate(dettaglioRouting.getDominio(), motivoErroreValidazione);
		}
		
		if(dettaglioRouting.getOraRegistrazione()==null){
			motivoErroreValidazione.append("OraRegistrazione non definita\n");
		}
		
		if(dettaglioRouting.getDettaglio()==null){
			motivoErroreValidazione.append("Dettaglio non definita\n");
		}
		else{
			if(dettaglioRouting.getDettaglio().getDescrizione()==null){
				motivoErroreValidazione.append("Dettaglio.descrizione non definita\n");
			}
			if(dettaglioRouting.getDettaglio().getEsito()==null){
				motivoErroreValidazione.append("Dettaglio.esito non definita\n");
			}
		}
		
		if(motivoErroreValidazione.length()!=size)
			return false;
		else
			return true;

	}
	private static void validate(Dominio dominio,StringBuffer motivoErroreValidazione){
		if(dominio.getIdentificativoPorta()==null){
			motivoErroreValidazione.append("Dominio.identificativoPorta non definito\n");
		}
		if(dominio.getModulo()==null){
			motivoErroreValidazione.append("Dominio.modulo non definito\n");
		}
		if(dominio.getSoggetto()==null){
			motivoErroreValidazione.append("Dominio.soggetto non definita\n");
		}
		else{
			if(dominio.getSoggetto().getTipo()==null){
				motivoErroreValidazione.append("Dominio.soggetto.tipo non definita\n");
			}
			if(dominio.getSoggetto().getBase()==null){
				motivoErroreValidazione.append("Dominio.soggetto.nome non definita\n");
			}
		}
	}
	
	
	
	/* ----- Unmarshall ----- */
	
	/**
	 * Ritorna la rappresentazione java
	 * 
	 * @param m byte[]
	 * @return DettaglioRouting
	 * @throws XMLUtilsException
	 */
	public static DettaglioRouting getDettaglioRouting(Logger log,byte[] m) throws XMLUtilsException{
		ByteArrayInputStream bin = null;
		try{
			bin = new ByteArrayInputStream(m);
			return XMLUtils.getDettaglioRouting(log,bin);
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
	 * @return DettaglioRouting
	 * @throws XMLUtilsException
	 */
	public static DettaglioRouting getDettaglioRouting(Logger log,File m) throws XMLUtilsException{
		FileInputStream fin = null;
		try{
			fin = new FileInputStream(m);
			return XMLUtils.getDettaglioRouting(log,fin);
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
	 * @return DettaglioRouting
	 * @throws XMLUtilsException
	 */
	public static DettaglioRouting getDettaglioRouting(Logger log,String m) throws XMLUtilsException{
		return XMLUtils.getDettaglioRouting(log,m.getBytes());
	}
	
	/**
	 * Ritorna la rappresentazione java
	 * 
	 * @param m InputStream
	 * @return DettaglioRouting
	 * @throws XMLUtilsException
	 */
	public static DettaglioRouting getDettaglioRouting(Logger log,InputStream m) throws XMLUtilsException{
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
			
			// trasformazione in oggetto DettaglioRouting
			ByteArrayInputStream binTrasformazione = new ByteArrayInputStream(xml);
			return (DettaglioRouting) org.openspcoop2.utils.xml.JiBXUtils.xmlToObj(binTrasformazione, DettaglioRouting.class);
			
		}catch(Exception e){
			throw new XMLUtilsException(e.getMessage(),e);
		}
	}

	
	
	
	
	/* ----- Marshall ----- */
	public static void generateDettaglioRouting(DettaglioRouting eccezione,File out) throws XMLUtilsException{
		try{
			StringBuffer risultatoValidazione = new StringBuffer();
			if(XMLUtils.validate(eccezione, risultatoValidazione)==false){
				throw new Exception(risultatoValidazione.toString());
			}
			org.openspcoop2.utils.xml.JiBXUtils.objToXml(out.getName(),XMLUtils.generateDettaglioRouting_engine(eccezione));
		}catch(Exception e){
			throw new XMLUtilsException(e.getMessage(),e);
		}
	}
	
	public static void generateDettaglioRouting(DettaglioRouting eccezione,String fileName) throws XMLUtilsException{
		try{
			StringBuffer risultatoValidazione = new StringBuffer();
			if(XMLUtils.validate(eccezione, risultatoValidazione)==false){
				throw new Exception(risultatoValidazione.toString());
			}
			org.openspcoop2.utils.xml.JiBXUtils.objToXml(fileName,XMLUtils.generateDettaglioRouting_engine(eccezione));
		}catch(Exception e){
			throw new XMLUtilsException(e.getMessage(),e);
		}
	}
	
	public static byte[] generateDettaglioRouting(DettaglioRouting eccezione) throws XMLUtilsException{
		try{
			StringBuffer risultatoValidazione = new StringBuffer();
			if(XMLUtils.validate(eccezione, risultatoValidazione)==false){
				throw new Exception(risultatoValidazione.toString());
			}
			return XMLUtils.generateDettaglioRouting_engine(eccezione);
		}catch(Exception e){
			throw new XMLUtilsException(e.getMessage(),e);
		}
	}

	public static void generateDettaglioRouting(DettaglioRouting eccezione,OutputStream out) throws XMLUtilsException{
		try{
			StringBuffer risultatoValidazione = new StringBuffer();
			if(XMLUtils.validate(eccezione, risultatoValidazione)==false){
				throw new Exception(risultatoValidazione.toString());
			}
			out.write(XMLUtils.generateDettaglioRouting_engine(eccezione));
			out.flush();
			out.close();
		}catch(Exception e){
			throw new XMLUtilsException(e.getMessage(),e);
		}
	}
	
	private static byte[] generateDettaglioRouting_engine(DettaglioRouting eccezione) throws XMLUtilsException{
		try{
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			org.openspcoop2.utils.xml.JiBXUtils.objToXml(bout, DettaglioRouting.class, eccezione);
			byte[] dichiarazione = bout.toByteArray();
			return dichiarazione;
		}catch(Exception e){
			throw new XMLUtilsException(e.getMessage(),e);
		}
	}
	
	
	
	
	public static DettaglioRouting getDettaglioRouting(Logger log,OpenSPCoop2Message msg)throws XMLUtilsException{
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
							if(XMLUtils.isDettaglioRouting(elem)){
								//System.out.println("ITEM ["+elem.getLocalName()+"] TROVATO");
								org.openspcoop2.message.XMLUtils xmlUtils = org.openspcoop2.message.XMLUtils.getInstance();
								byte [] xml = xmlUtils.toByteArray(elem);
								//System.out.println("XML S: "+new String(xml));
								DettaglioRouting de = XMLUtils.getDettaglioRouting(log,xml);
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
	
	public static boolean isDettaglioRouting(byte [] doc){
		try{
			org.openspcoop2.message.XMLUtils xmlUtils = org.openspcoop2.message.XMLUtils.getInstance();
			Document docXML = xmlUtils.newDocument(doc);
			Element elemXML = docXML.getDocumentElement();
			return XMLUtils.isDettaglioRouting_engine(elemXML);
		}catch(Exception e){
			//System.out.println("NON e' un DOCUMENTO VALIDO: "+e.getMessage());
			return false;
		}
	}
	public static boolean isDettaglioRouting(Document docXML){
		try{
			Element elemXML = docXML.getDocumentElement();
			return XMLUtils.isDettaglioRouting_engine(elemXML);
		}catch(Exception e){
			//System.out.println("NON e' un DOCUMENTO VALIDO: "+e.getMessage());
			return false;
		}
	}
	public static boolean isDettaglioRouting(Element elemXML){
		return isDettaglioRouting_engine(elemXML);
	}
	public static boolean isDettaglioRouting(Node nodeXml){
		return isDettaglioRouting_engine(nodeXml);
	}
	private static boolean isDettaglioRouting_engine(Node nodeXml){
		try{
			//System.out.println("LOCAL["+Costanti.ROOT_LOCAL_NAME_DETTAGLIO_ECCEZIONE+"]vs["+elemXML.getLocalName()+"]  NAMESPACE["+Costanti.TARGET_NAMESPACE+"]vs["+elemXML.getNamespaceURI()+"]");
			if(Costanti.ROOT_LOCAL_NAME_DETTAGLIO_ROUTING.equals(nodeXml.getLocalName()) && 
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