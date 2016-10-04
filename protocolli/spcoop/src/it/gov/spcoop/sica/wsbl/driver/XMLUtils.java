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



package it.gov.spcoop.sica.wsbl.driver;

import it.cnipa.collprofiles.driver.XMLUtilsException;
import it.gov.spcoop.sica.wsbl.ConceptualBehavior;
import it.gov.spcoop.sica.wsbl.MessageBehavior;
import it.gov.spcoop.sica.wsbl.TransitionType;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import org.slf4j.Logger;
import org.openspcoop2.utils.xml.AbstractXMLUtils;
import org.openspcoop2.utils.xml.JiBXUtils;
import org.openspcoop2.utils.xml.ValidatoreXSD;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


/**
 * Classe utilizzata per lavorare sul documento semiformale che contiene le informazioni eGov dei servizi di un accordo
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
			XMLUtils.validatoreXSD = new ValidatoreXSD(log,XMLUtils.class.getResourceAsStream("/WSBL_originale.xsd"));
		}
		return XMLUtils.validatoreXSD;
	}
	public static boolean validate(ConceptualBehavior conceptual,StringBuffer motivoErroreValidazione){
		
		int size = motivoErroreValidazione.length();
		
		// TODO
		
		if(motivoErroreValidazione.length()!=size)
			return false;
		else
			return true;

	}
	public static boolean validate(MessageBehavior conceptual,StringBuffer motivoErroreValidazione){
		
		int size = motivoErroreValidazione.length();
		
		// TODO
		
		if(motivoErroreValidazione.length()!=size)
			return false;
		else
			return true;

	}
	
	
	
	
	/* ----- Unmarshall ----- */
	
	/**
	 * Ritorna la rappresentazione java
	 * 
	 * @param m byte[]
	 * @return EgovDeclType
	 * @throws XMLUtilsException
	 */
	public static ConceptualBehavior getConceptualBehavior(Logger log,byte[] m) throws XMLUtilsException{
		ByteArrayInputStream bin = null;
		try{
			bin = new ByteArrayInputStream(m);
			return XMLUtils.getConceptualBehavior(log,bin);
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
	 * @return EgovDeclType
	 * @throws XMLUtilsException
	 */
	public static ConceptualBehavior getConceptualBehavior(Logger log,File m) throws XMLUtilsException{
		FileInputStream fin = null;
		try{
			fin = new FileInputStream(m);
			return XMLUtils.getConceptualBehavior(log,fin);
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
	 * @return EgovDeclType
	 * @throws XMLUtilsException
	 */
	public static ConceptualBehavior getConceptualBehavior(Logger log,String m) throws XMLUtilsException{
		return XMLUtils.getConceptualBehavior(log,m.getBytes());
	}
	
	/**
	 * Ritorna la rappresentazione java
	 * 
	 * @param m InputStream
	 * @return EgovDeclType
	 * @throws XMLUtilsException
	 */
	public static ConceptualBehavior getConceptualBehavior(Logger log,InputStream m) throws XMLUtilsException{
		try{
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			byte[]reads = new byte[1024];
			int letti = 0;
			while( (letti=m.read(reads)) != -1){
				bout.write(reads, 0, letti);
			}
			bout.flush();
			bout.close();
			byte [] xml = XMLUtils.unescapeXMLConceptualBehaviorForClientSICA(bout.toString());
			ByteArrayInputStream binValidazione = new ByteArrayInputStream(xml);
			
			ValidatoreXSD validatoreXSD = XMLUtils.getValidatoreXSD(log);
			validatoreXSD.valida(binValidazione);
			
			ByteArrayInputStream binTrasformazione = new ByteArrayInputStream(xml);
			
			return (ConceptualBehavior) JiBXUtils.xmlToObj(binTrasformazione, ConceptualBehavior.class);
		}catch(Exception e){
			throw new XMLUtilsException(e.getMessage(),e);
		}
	}
	
	private static byte[] unescapeXMLConceptualBehaviorForClientSICA(String xml) throws Exception{
		String xmlS = new String(xml);
		xmlS = xmlS.replace("<wsbl:ConceptualBehavior","<ConceptualBehavior");
		//xmlS = xmlS.replace("xmlns:wscp=\"http://spcoop.gov.it/sica/wscp\" xmlns","xmlns");
		xmlS = xmlS.replace("xmlns:wsbl","xmlns");
		xmlS = xmlS.replace("</wsbl:ConceptualBehavior","</ConceptualBehavior");
		return xmlS.getBytes();
	}
	
	
	
	/* ----- Marshall Manifest dell'accordo di servizio ----- */
	public static void generateConceptualBehavior(ConceptualBehavior manifest,File out) throws XMLUtilsException{
		FileOutputStream fout = null;
		try{
			fout = new FileOutputStream(out);
			byte[] xml = XMLUtils.escapeXMLConceptualBehaviorForClientSICA(manifest);
			fout.write(xml);
			fout.flush();		
		}catch(Exception e){
			throw new XMLUtilsException(e.getMessage(),e);
		}finally{
			if(fout!=null){
				try{
					fout.close();
				}catch(Exception e){}
			}
		}
	}
	
	public static void generateConceptualBehavior(ConceptualBehavior manifest,String fileName) throws XMLUtilsException{
		XMLUtils.generateConceptualBehavior(manifest,new File(fileName));
	}
	
	public static byte[] generateConceptualBehavior(ConceptualBehavior manifest) throws XMLUtilsException{
		try{
			return XMLUtils.escapeXMLConceptualBehaviorForClientSICA(manifest);
		}catch(Exception e){
			throw new XMLUtilsException(e.getMessage(),e);
		}
	}

	public static void generateConceptualBehavior(ConceptualBehavior manifest,OutputStream out) throws XMLUtilsException{
		try{
			byte[] xml =  XMLUtils.escapeXMLConceptualBehaviorForClientSICA(manifest);
			out.write(xml);		
		}catch(Exception e){
			throw new XMLUtilsException(e.getMessage(),e);
		}
	}

	private static byte[] escapeXMLConceptualBehaviorForClientSICA(ConceptualBehavior manifest) throws Exception{
		StringBuffer risultatoValidazione = new StringBuffer();
		if(XMLUtils.validate(manifest, risultatoValidazione)==false){
			throw new Exception(risultatoValidazione.toString());
		}
		
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		JiBXUtils.objToXml(bout, ConceptualBehavior.class, manifest);
		String xml = bout.toString();
		xml = xml.replace("<ConceptualBehavior", "<wsbl:ConceptualBehavior");
		//xml = xml.replace("xmlns", "xmlns:wscp=\"http://spcoop.gov.it/sica/wscp\" xmlns");
		xml = xml.replace("xmlns", "xmlns:wsbl");
		xml = xml.replace("</ConceptualBehavior", "</wsbl:ConceptualBehavior");
		return xml.getBytes();
	}

	
	
	
	

	public static boolean isConceptualBehavior(byte [] doc){
		try{
			AbstractXMLUtils xmlUtils = org.openspcoop2.message.XMLUtils.getInstance();		
			Document docXML = xmlUtils.newDocument(doc);
			Element elemXML = docXML.getDocumentElement();
			//System.out.println("LOCAL["+Costanti.ROOT_LOCAL_NAME+"]vs["+elemXML.getLocalName()+"]  NAMESPACE["+Costanti.TARGET_NAMESPACE+"]vs["+elemXML.getNamespaceURI()+"]");
			if(Costanti.ROOT_CONCEPTUAL_LOCAL_NAME.equals(elemXML.getLocalName()) && 
					Costanti.TARGET_NAMESPACE.equals(elemXML.getNamespaceURI()) ){
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
	
	
	public static String[] getOperazioniAsincrone(ConceptualBehavior wsblConcettuale) throws XMLUtilsException{
		try{
			String [] operationsAsincrone = new String[2];
			String statoIniziale = null;
			String statoFinale = null;
			if(wsblConcettuale.getStates()==null){
				throw new Exception("Non sono presenti stati nel documento WSBL ConceptualBehavior");
			}else{
				if(wsblConcettuale.getStates().getStateInitial()==null || wsblConcettuale.getStates().getStateInitial().getName()==null){
					throw new Exception("Stato iniziale non presente nel documento WSBL ConceptualBehavior");
				}
				statoIniziale = wsblConcettuale.getStates().getStateInitial().getName();
				if(wsblConcettuale.getStates().getStateFinal()==null || wsblConcettuale.getStates().getStateFinal().getName()==null){
					throw new Exception("Stato finale non presente nel documento WSBL ConceptualBehavior");
				}
				statoFinale = wsblConcettuale.getStates().getStateFinal().getName();
			}
			
			if(wsblConcettuale.getTransitions()==null || wsblConcettuale.getTransitions().sizeTransitionList()<=0 ){
				throw new Exception("Non sono presenti transizioni nel documento WSBL ConceptualBehavior");
			}else{
				for(int i=0; i<wsblConcettuale.getTransitions().sizeTransitionList(); i++){
					TransitionType tr = wsblConcettuale.getTransitions().getTransition(i);
					if(tr.getSource()==null){
						throw new Exception("Presente una transizione con source non definita nel documento WSBL ConceptualBehavior");
					}
					if(tr.getTarget()==null){
						throw new Exception("Presente una transizione con target non definito nel documento WSBL ConceptualBehavior");
					}
					if(tr.getSource().equals(statoIniziale)){
						if(tr.getEvents()==null || tr.getEvents().sizeEventList()<=0){
							throw new Exception("Presente una transizione senza eventi nel documento WSBL ConceptualBehavior");
						}
						operationsAsincrone[0] = tr.getEvents().getEvent(0).getName();
						if(operationsAsincrone[0]==null){
							throw new Exception("Presente una transizione con un evento senza nome nel documento WSBL ConceptualBehavior");
						}
						continue;
					}
					if(tr.getTarget().equals(statoFinale)){
						if(tr.getEvents()==null || tr.getEvents().sizeEventList()<=0){
							throw new Exception("Presente una transizione senza eventi nel documento WSBL ConceptualBehavior");
						}
						operationsAsincrone[1] = tr.getEvents().getEvent(0).getName();
						if(operationsAsincrone[1]==null){
							throw new Exception("Presente una transizione con un evento senza nome nel documento WSBL ConceptualBehavior");
						}
						continue;
					}
				}
				if(operationsAsincrone[0]==null){
					throw new Exception("Non e' stata trovata una transizione che possiede come source lo stato iniziale "+statoIniziale+" nel documento WSBL ConceptualBehavior");
				}
				if(operationsAsincrone[1]==null){
					throw new Exception("Non e' stata trovata una transizione che possiede come target lo stato finale "+statoFinale+" nel documento WSBL ConceptualBehavior");
				}
				return operationsAsincrone;
			}
			
		}catch(Exception e){
			throw new XMLUtilsException(e.getMessage(),e);
		}
	}
	
	
	
	
	
	
	
	/* ----- Unmarshall ----- */
	
	/**
	 * Ritorna la rappresentazione java
	 * 
	 * @param m byte[]
	 * @return EgovDeclType
	 * @throws XMLUtilsException
	 */
	public static MessageBehavior getMessageBehavior(Logger log,byte[] m) throws XMLUtilsException{
		ByteArrayInputStream bin = null;
		try{
			bin = new ByteArrayInputStream(m);
			return XMLUtils.getMessageBehavior(log,bin);
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
	 * @return EgovDeclType
	 * @throws XMLUtilsException
	 */
	public static MessageBehavior getMessageBehavior(Logger log,File m) throws XMLUtilsException{
		FileInputStream fin = null;
		try{
			fin = new FileInputStream(m);
			return XMLUtils.getMessageBehavior(log,fin);
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
	 * @return EgovDeclType
	 * @throws XMLUtilsException
	 */
	public static MessageBehavior getMessageBehavior(Logger log,String m) throws XMLUtilsException{
		return XMLUtils.getMessageBehavior(log,m.getBytes());
	}
	
	/**
	 * Ritorna la rappresentazione java
	 * 
	 * @param m InputStream
	 * @return EgovDeclType
	 * @throws XMLUtilsException
	 */
	public static MessageBehavior getMessageBehavior(Logger log,InputStream m) throws XMLUtilsException{
		try{
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			byte[]reads = new byte[1024];
			int letti = 0;
			while( (letti=m.read(reads)) != -1){
				bout.write(reads, 0, letti);
			}
			bout.flush();
			bout.close();
			byte [] xml = XMLUtils.unescapeXMLMessageBehaviorForClientSICA(bout.toString());
			ByteArrayInputStream binValidazione = new ByteArrayInputStream(xml);
			
			ValidatoreXSD validatoreXSD = XMLUtils.getValidatoreXSD(log);
			validatoreXSD.valida(binValidazione);
			
			ByteArrayInputStream binTrasformazione = new ByteArrayInputStream(xml);
			
			return (MessageBehavior) JiBXUtils.xmlToObj(binTrasformazione, MessageBehavior.class);
		}catch(Exception e){
			throw new XMLUtilsException(e.getMessage(),e);
		}
	}
	
	private static byte[] unescapeXMLMessageBehaviorForClientSICA(String xml) throws Exception{
		String xmlS = new String(xml);
		xmlS = xmlS.replace("<wsbl:MessageBehavior","<MessageBehavior");
		//xmlS = xmlS.replace("xmlns:wscp=\"http://spcoop.gov.it/sica/wscp\" xmlns","xmlns");
		xmlS = xmlS.replace("xmlns:wsbl","xmlns");
		xmlS = xmlS.replace("</wsbl:MessageBehavior","</MessageBehavior");
		return xmlS.getBytes();
	}
	
	
	
	
	
	/* ----- Marshall Manifest dell'accordo di servizio ----- */
	public static void generateMessageBehavior(MessageBehavior manifest,File out) throws XMLUtilsException{
		FileOutputStream fout = null;
		try{
			fout = new FileOutputStream(out);
			byte[] xml = XMLUtils.escapeXMLMessageBehaviorForClientSICA(manifest);
			fout.write(xml);
			fout.flush();		
		}catch(Exception e){
			throw new XMLUtilsException(e.getMessage(),e);
		}finally{
			if(fout!=null){
				try{
					fout.close();
				}catch(Exception e){}
			}
		}
	}
	
	public static void generateMessageBehavior(MessageBehavior manifest,String fileName) throws XMLUtilsException{
		XMLUtils.generateMessageBehavior(manifest,new File(fileName));
	}
	
	public static byte[] generateMessageBehavior(MessageBehavior manifest) throws XMLUtilsException{
		try{
			return XMLUtils.escapeXMLMessageBehaviorForClientSICA(manifest);
		}catch(Exception e){
			throw new XMLUtilsException(e.getMessage(),e);
		}
	}

	public static void generateMessageBehavior(MessageBehavior manifest,OutputStream out) throws XMLUtilsException{
		try{
			byte[] xml = XMLUtils.escapeXMLMessageBehaviorForClientSICA(manifest);
			out.write(xml);		
		}catch(Exception e){
			throw new XMLUtilsException(e.getMessage(),e);
		}
	}
	
	private static byte[] escapeXMLMessageBehaviorForClientSICA(MessageBehavior manifest) throws Exception{
		StringBuffer risultatoValidazione = new StringBuffer();
		if(XMLUtils.validate(manifest, risultatoValidazione)==false){
			throw new Exception(risultatoValidazione.toString());
		}
		
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		JiBXUtils.objToXml(bout, MessageBehavior.class, manifest);
		String xml = bout.toString();
		xml = xml.replace("<MessageBehavior", "<wsbl:MessageBehavior");
		//xml = xml.replace("xmlns", "xmlns:wscp=\"http://spcoop.gov.it/sica/wscp\" xmlns");
		xml = xml.replace("xmlns", "xmlns:wsbl");
		xml = xml.replace("</MessageBehavior", "</wsbl:MessageBehavior");
		return xml.getBytes();
	}
	
	public static boolean isMessageBehavior(byte [] doc){
		try{
			AbstractXMLUtils xmlUtils = org.openspcoop2.message.XMLUtils.getInstance();		
			Document docXML = xmlUtils.newDocument(doc);
			Element elemXML = docXML.getDocumentElement();
			//System.out.println("LOCAL["+Costanti.ROOT_LOCAL_NAME+"]vs["+elemXML.getLocalName()+"]  NAMESPACE["+Costanti.TARGET_NAMESPACE+"]vs["+elemXML.getNamespaceURI()+"]");
			if(Costanti.ROOT_MESSAGE_LOCAL_NAME.equals(elemXML.getLocalName()) && 
					Costanti.TARGET_NAMESPACE.equals(elemXML.getNamespaceURI()) ){
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