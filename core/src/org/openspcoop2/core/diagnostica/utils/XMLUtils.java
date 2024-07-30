/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it). 
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



package org.openspcoop2.core.diagnostica.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;

import org.slf4j.Logger;
import org.openspcoop2.core.diagnostica.DominioDiagnostico;
import org.openspcoop2.core.diagnostica.MessaggioDiagnostico;
import org.openspcoop2.core.diagnostica.Protocollo;
import org.openspcoop2.core.diagnostica.constants.CostantiDiagnostica;
import org.openspcoop2.core.diagnostica.utils.serializer.JsonJacksonDeserializer;
import org.openspcoop2.message.OpenSPCoop2MessageFactory;
import org.openspcoop2.message.xml.ValidatoreXSD;
import org.openspcoop2.utils.beans.WriteToSerializerType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;


/**
 * Classe utilizzata per la generazione dei diagnostici generati dalla PdD
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class XMLUtils  {
	
	private XMLUtils() {}

	/** Validatore XSD */
	static ValidatoreXSD validatoreXSD = null;
	private static synchronized void initValidatoreXSD(Logger log) throws Exception{
		if(XMLUtils.validatoreXSD==null){
			XMLUtils.validatoreXSD = new ValidatoreXSD(OpenSPCoop2MessageFactory.getDefaultMessageFactory(), log,XMLUtils.class.getResourceAsStream("/openspcoopDiagnostica.xsd"));
		}
	}
	public static ValidatoreXSD getValidatoreXSD(Logger log) throws Exception{
		if(XMLUtils.validatoreXSD==null){
			initValidatoreXSD(log);
		}
		return XMLUtils.validatoreXSD;
	}
	public static boolean validate(MessaggioDiagnostico messaggioDiagnostico,StringBuilder motivoErroreValidazione){
		
		int size = motivoErroreValidazione.length();
		
		if(messaggioDiagnostico.getDominio()==null){
			motivoErroreValidazione.append("Dominio non definito\n");
		}
		else{
			validate(messaggioDiagnostico.getDominio(), motivoErroreValidazione);
		}
		
		if(messaggioDiagnostico.getOraRegistrazione()==null){
			motivoErroreValidazione.append("OraRegistrazione non definita\n");
		}
		
		if(messaggioDiagnostico.getCodice()==null){
			motivoErroreValidazione.append("Codice non definito\n");
		}
		
		if(messaggioDiagnostico.getMessaggio()==null){
			motivoErroreValidazione.append("Messaggio non definito\n");
		}
		
		if(messaggioDiagnostico.getSeverita()==null){
			motivoErroreValidazione.append("Severita non definito\n");
		}
		
		if(messaggioDiagnostico.getProtocollo()!=null){ // sara' null per i messaggi di servizio emessi dalla PdD
			validate(messaggioDiagnostico.getProtocollo(), motivoErroreValidazione);
		}
		
		return motivoErroreValidazione.length()==size;

	}
	private static void validate(DominioDiagnostico dominio,StringBuilder motivoErroreValidazione){
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
	private static void validate(Protocollo protocollo,StringBuilder motivoErroreValidazione){
		if(protocollo.getIdentificativo()==null){
			motivoErroreValidazione.append("Protocollo.identificativo non definito\n");
		}
	}
	
	
	
	/* ----- Unmarshall ----- */
	
	/**
	 * Ritorna la rappresentazione java
	 * 
	 * @param m byte[]
	 * @return DettaglioEccezione
	 * @throws XMLUtilsException
	 */
	public static MessaggioDiagnostico getMessaggioDiagnostico(Logger log,byte[] m) throws XMLUtilsException{
		try (ByteArrayInputStream bin = new ByteArrayInputStream(m);){
			return XMLUtils.getMessaggioDiagnostico(log,bin);
		}catch(Exception e){
			throw new XMLUtilsException(e.getMessage(),e);
		}
	}
	
	/**
	 * Ritorna la rappresentazione java
	 * 
	 * @param m File
	 * @return DettaglioEccezione
	 * @throws XMLUtilsException
	 */
	public static MessaggioDiagnostico getMessaggioDiagnostico(Logger log,File m) throws XMLUtilsException{
		try (FileInputStream fin = new FileInputStream(m);){
			return XMLUtils.getMessaggioDiagnostico(log,fin);
		}catch(Exception e){
			throw new XMLUtilsException(e.getMessage(),e);
		}
	}
	
	/**
	 * Ritorna la rappresentazione java
	 * 
	 * @param m String
	 * @return DettaglioEccezione
	 * @throws XMLUtilsException
	 */
	public static MessaggioDiagnostico getMessaggioDiagnostico(Logger log,String m) throws XMLUtilsException{
		return XMLUtils.getMessaggioDiagnostico(log,m.getBytes());
	}
	
	/**
	 * Ritorna la rappresentazione java
	 * 
	 * @param m InputStream
	 * @return DettaglioEccezione
	 * @throws XMLUtilsException
	 */
	public static MessaggioDiagnostico getMessaggioDiagnostico(Logger log,InputStream m) throws XMLUtilsException{
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
			
			// trasformazione in oggetto DettaglioEccezione
			ByteArrayInputStream binTrasformazione = new ByteArrayInputStream(xml);
			return (MessaggioDiagnostico) org.openspcoop2.utils.xml.JaxbUtils.xmlToObj(binTrasformazione, MessaggioDiagnostico.class);
			
		}catch(Exception e){
			throw new XMLUtilsException(e.getMessage(),e);
		}
	}

	public static MessaggioDiagnostico getMessaggioDiagnosticoFromJson(Logger log,InputStream is) throws XMLUtilsException{
		try{			
			if(log!=null) {
				//nop
			}
			JsonJacksonDeserializer deserializer = new JsonJacksonDeserializer();
			return deserializer.readMessaggioDiagnostico(is);
		}catch(Exception e){
			throw new XMLUtilsException(e.getMessage(),e);
		}
	}
	
	
	
	
	/* ----- Marshall ----- */
	public static void generateMessaggioDiagnostico(MessaggioDiagnostico messaggioDiagnostico,File out) throws XMLUtilsException{
		generateMessaggioDiagnostico(messaggioDiagnostico, out, false, false);
	}
	public static void generateMessaggioDiagnostico(MessaggioDiagnostico messaggioDiagnostico,File out,boolean prettyDocument, boolean omitXmlDeclaration) throws XMLUtilsException{
		try{
			StringBuilder risultatoValidazione = new StringBuilder();
			if(!XMLUtils.validate(messaggioDiagnostico, risultatoValidazione)){
				throw new XMLUtilsException(risultatoValidazione.toString());
			}
			org.openspcoop2.utils.xml.JaxbUtils.objToXml(out.getName(),XMLUtils.generateMessaggioDiagnosticoEngine(messaggioDiagnostico, prettyDocument, omitXmlDeclaration));
		}catch(Exception e){
			throw new XMLUtilsException(e.getMessage(),e);
		}
	}
	
	public static void generateMessaggioDiagnostico(MessaggioDiagnostico messaggioDiagnostico,String fileName) throws XMLUtilsException{
		generateMessaggioDiagnostico(messaggioDiagnostico, fileName, false, false);
	}
	public static void generateMessaggioDiagnostico(MessaggioDiagnostico messaggioDiagnostico,String fileName,boolean prettyDocument, boolean omitXmlDeclaration) throws XMLUtilsException{
		try{
			StringBuilder risultatoValidazione = new StringBuilder();
			if(!XMLUtils.validate(messaggioDiagnostico, risultatoValidazione)){
				throw new XMLUtilsException(risultatoValidazione.toString());
			}
			org.openspcoop2.utils.xml.JaxbUtils.objToXml(fileName,XMLUtils.generateMessaggioDiagnosticoEngine(messaggioDiagnostico, prettyDocument, omitXmlDeclaration));
		}catch(Exception e){
			throw new XMLUtilsException(e.getMessage(),e);
		}
	}
	
	public static byte[] generateMessaggioDiagnostico(MessaggioDiagnostico messaggioDiagnostico) throws XMLUtilsException{
		return generateMessaggioDiagnostico(messaggioDiagnostico,false, false);
	}
	public static byte[] generateMessaggioDiagnostico(MessaggioDiagnostico messaggioDiagnostico,boolean prettyDocument, boolean omitXmlDeclaration) throws XMLUtilsException{
		try{
			StringBuilder risultatoValidazione = new StringBuilder();
			if(!XMLUtils.validate(messaggioDiagnostico, risultatoValidazione)){
				throw new XMLUtilsException(risultatoValidazione.toString());
			}
			return XMLUtils.generateMessaggioDiagnosticoEngine(messaggioDiagnostico, prettyDocument, omitXmlDeclaration);
		}catch(Exception e){
			throw new XMLUtilsException(e.getMessage(),e);
		}
	}

	public static void generateMessaggioDiagnostico(MessaggioDiagnostico messaggioDiagnostico,OutputStream out) throws XMLUtilsException{
		generateMessaggioDiagnostico(messaggioDiagnostico, out, false, false);
	}
	public static void generateMessaggioDiagnostico(MessaggioDiagnostico messaggioDiagnostico,OutputStream out,boolean prettyDocument, boolean omitXmlDeclaration) throws XMLUtilsException{
		try{
			StringBuilder risultatoValidazione = new StringBuilder();
			if(!XMLUtils.validate(messaggioDiagnostico, risultatoValidazione)){
				throw new XMLUtilsException(risultatoValidazione.toString());
			}
			out.write(XMLUtils.generateMessaggioDiagnosticoEngine(messaggioDiagnostico, prettyDocument, omitXmlDeclaration));
			out.flush();
			out.close();
		}catch(Exception e){
			throw new XMLUtilsException(e.getMessage(),e);
		}
	}
	
	public static String generateMessaggioDiagnosticoAsJson(MessaggioDiagnostico messaggioDiagnostico) throws XMLUtilsException{
		return generateMessaggioDiagnosticoAsJson(messaggioDiagnostico, false);
	}
	public static String generateMessaggioDiagnosticoAsJson(MessaggioDiagnostico messaggioDiagnostico,boolean prettyDocument) throws XMLUtilsException{
		try{
			StringBuilder risultatoValidazione = new StringBuilder();
			if(!XMLUtils.validate(messaggioDiagnostico, risultatoValidazione)){
				throw new XMLUtilsException(risultatoValidazione.toString());
			}
			return XMLUtils.generateMessaggioDiagnosticoAsJsonEngine(messaggioDiagnostico, prettyDocument);
		}catch(Exception e){
			throw new XMLUtilsException(e.getMessage(),e);
		}
	}
	
	private static byte[] generateMessaggioDiagnosticoEngine(MessaggioDiagnostico messaggioDiagnostico,boolean prettyDocument, boolean omitXmlDeclaration) throws XMLUtilsException{
		try{
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			org.openspcoop2.utils.xml.JaxbUtils.objToXml(bout, MessaggioDiagnostico.class, messaggioDiagnostico, prettyDocument, omitXmlDeclaration);
			return bout.toByteArray();
		}catch(Exception e){
			throw new XMLUtilsException(e.getMessage(),e);
		}
	}
	
	private static String generateMessaggioDiagnosticoAsJsonEngine(MessaggioDiagnostico messaggioDiagnostico,boolean prettyDocument) throws XMLUtilsException{
		try{
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			messaggioDiagnostico.writeTo(bout, WriteToSerializerType.JSON_JACKSON, prettyDocument);
			bout.flush();
			bout.close();
			return bout.toString();
		}catch(Exception e){
			throw new XMLUtilsException(e.getMessage(),e);
		}
	}
	
	
	
	
	public static boolean isMessaggioDiagnostico(byte [] doc){
		try{
			org.openspcoop2.message.xml.MessageXMLUtils xmlUtils = org.openspcoop2.message.xml.MessageXMLUtils.DEFAULT;
			Document docXML = xmlUtils.newDocument(doc);
			Element elemXML = docXML.getDocumentElement();
			return XMLUtils.isMessaggioDiagnostico_engine(elemXML);
		}catch(Exception e){
			//System.out.println("NON e' un DOCUMENTO VALIDO: "+e.getMessage());
			return false;
		}
	}
	public static boolean isMessaggioDiagnostico(Document docXML){
		try{
			Element elemXML = docXML.getDocumentElement();
			return XMLUtils.isMessaggioDiagnostico_engine(elemXML);
		}catch(Exception e){
			//System.out.println("NON e' un DOCUMENTO VALIDO: "+e.getMessage());
			return false;
		}
	}
	public static boolean isMessaggioDiagnostico(Element elemXML){
		return isMessaggioDiagnostico_engine(elemXML);
	}
	public static boolean isMessaggioDiagnostico(Node nodeXml){
		return isMessaggioDiagnostico_engine(nodeXml);
	}
	private static boolean isMessaggioDiagnostico_engine(Node nodeXml){
		try{
			//System.out.println("LOCAL["+Costanti.ROOT_LOCAL_NAME_DETTAGLIO_ECCEZIONE+"]vs["+elemXML.getLocalName()+"]  NAMESPACE["+Costanti.TARGET_NAMESPACE+"]vs["+elemXML.getNamespaceURI()+"]");
			if(CostantiDiagnostica.ROOT_LOCAL_NAME_MESSAGGIO_DIAGNOSTICO.equals(nodeXml.getLocalName()) && 
					CostantiDiagnostica.TARGET_NAMESPACE.equals(nodeXml.getNamespaceURI() ) 
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