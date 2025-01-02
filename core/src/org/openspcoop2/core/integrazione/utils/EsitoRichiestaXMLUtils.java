/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it). 
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



package org.openspcoop2.core.integrazione.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;

import org.slf4j.Logger;
import org.openspcoop2.core.integrazione.EsitoRichiesta;
import org.openspcoop2.core.integrazione.constants.Costanti;
import org.openspcoop2.message.OpenSPCoop2MessageFactory;
import org.openspcoop2.message.xml.ValidatoreXSD;
import org.openspcoop2.utils.beans.WriteToSerializerType;
import org.openspcoop2.utils.xml.XSDResourceResolver;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;


/**
 * Classe utilizzata per la generazione dei dati di esitoRichiesta
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class EsitoRichiestaXMLUtils  {

	/** Validatore XSD */
	static ValidatoreXSD validatoreXSD = null;
	private static synchronized void initValidatoreXSD(Logger log) throws Exception{
		if(EsitoRichiestaXMLUtils.validatoreXSD==null){
			XSDResourceResolver xsdResourceResolver = new XSDResourceResolver();
			xsdResourceResolver.addResource("soapEnvelope.xsd", EsitoRichiestaXMLUtils.class.getResourceAsStream("/soapEnvelope.xsd"));
			EsitoRichiestaXMLUtils.validatoreXSD = new ValidatoreXSD(OpenSPCoop2MessageFactory.getDefaultMessageFactory(), log,xsdResourceResolver,EsitoRichiestaXMLUtils.class.getResourceAsStream("/openspcoopPresaInCarico.xsd"));
		}
	}
	public static ValidatoreXSD getValidatoreXSD(Logger log) throws Exception{
		if(EsitoRichiestaXMLUtils.validatoreXSD==null){
			initValidatoreXSD(log);
		}
		return EsitoRichiestaXMLUtils.validatoreXSD;
	}
	
	public static boolean validate(EsitoRichiesta esito,StringBuilder motivoErroreValidazione){
		int size = motivoErroreValidazione.length();
		
		if(esito.getMessageId()==null){
			motivoErroreValidazione.append("Identificativo Messaggio non definito\n");
		}
		if(esito.getState()==null){
			motivoErroreValidazione.append("Stato non definito\n");
		}
		
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
	 * @return DettaglioEccezione
	 * @throws XMLUtilsException
	 */
	public static EsitoRichiesta getEsitoRichiesta(Logger log,byte[] m) throws XMLUtilsException{
		ByteArrayInputStream bin = null;
		try{
			bin = new ByteArrayInputStream(m);
			return EsitoRichiestaXMLUtils.getEsitoRichiesta(log,bin);
		}catch(Exception e){
			throw new XMLUtilsException(e.getMessage(),e);
		}finally{
			try{
				if(bin!=null)
					bin.close();
			}catch(Exception eClose){
				// close
			}
		}
	}
	
	/**
	 * Ritorna la rappresentazione java
	 * 
	 * @param m File
	 * @return DettaglioEccezione
	 * @throws XMLUtilsException
	 */
	public static EsitoRichiesta getEsitoRichiesta(Logger log,File m) throws XMLUtilsException{
		FileInputStream fin = null;
		try{
			fin = new FileInputStream(m);
			return EsitoRichiestaXMLUtils.getEsitoRichiesta(log,fin);
		}catch(Exception e){
			throw new XMLUtilsException(e.getMessage(),e);
		}finally{
			try{
				if(fin!=null)
					fin.close();
			}catch(Exception eClose){
				// close
			}
		}
	}
	
	/**
	 * Ritorna la rappresentazione java
	 * 
	 * @param m String
	 * @return DettaglioEccezione
	 * @throws XMLUtilsException
	 */
	public static EsitoRichiesta getEsitoRichiesta(Logger log,String m) throws XMLUtilsException{
		return EsitoRichiestaXMLUtils.getEsitoRichiesta(log,m.getBytes());
	}
	
	/**
	 * Ritorna la rappresentazione java
	 * 
	 * @param m InputStream
	 * @return DettaglioEccezione
	 * @throws XMLUtilsException
	 */
	public static EsitoRichiesta getEsitoRichiesta(Logger log,InputStream m) throws XMLUtilsException{
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
			ValidatoreXSD validatoreXSD = EsitoRichiestaXMLUtils.getValidatoreXSD(log);
			validatoreXSD.valida(binValidazione);
			
			// trasformazione in oggetto DettaglioEccezione
			ByteArrayInputStream binTrasformazione = new ByteArrayInputStream(xml);
			return (EsitoRichiesta) org.openspcoop2.utils.xml.JaxbUtils.xmlToObj(binTrasformazione, EsitoRichiesta.class);
			
		}catch(Exception e){
			throw new XMLUtilsException(e.getMessage(),e);
		}
	}

	
	
	
	
	/* ----- Marshall ----- */
	public static void generateEsitoRichiesta(EsitoRichiesta esitoRichiesta,File out) throws XMLUtilsException{
		try{
			StringBuilder risultatoValidazione = new StringBuilder();
			if(EsitoRichiestaXMLUtils.validate(esitoRichiesta, risultatoValidazione)==false){
				throw new Exception(risultatoValidazione.toString());
			}
			org.openspcoop2.utils.xml.JaxbUtils.objToXml(out.getName(),EsitoRichiestaXMLUtils.generateEsitoRichiesta_engine(esitoRichiesta));
		}catch(Exception e){
			throw new XMLUtilsException(e.getMessage(),e);
		}
	}
	
	public static void generateEsitoRichiesta(EsitoRichiesta esitoRichiesta,String fileName) throws XMLUtilsException{
		try{
			StringBuilder risultatoValidazione = new StringBuilder();
			if(EsitoRichiestaXMLUtils.validate(esitoRichiesta, risultatoValidazione)==false){
				throw new Exception(risultatoValidazione.toString());
			}
			org.openspcoop2.utils.xml.JaxbUtils.objToXml(fileName,EsitoRichiestaXMLUtils.generateEsitoRichiesta_engine(esitoRichiesta));
		}catch(Exception e){
			throw new XMLUtilsException(e.getMessage(),e);
		}
	}
	
	public static byte[] generateEsitoRichiesta(EsitoRichiesta esitoRichiesta) throws XMLUtilsException{
		try{
			StringBuilder risultatoValidazione = new StringBuilder();
			if(EsitoRichiestaXMLUtils.validate(esitoRichiesta, risultatoValidazione)==false){
				throw new Exception(risultatoValidazione.toString());
			}
			return EsitoRichiestaXMLUtils.generateEsitoRichiesta_engine(esitoRichiesta);
		}catch(Exception e){
			throw new XMLUtilsException(e.getMessage(),e);
		}
	}

	public static void generateEsitoRichiesta(EsitoRichiesta esitoRichiesta,OutputStream out) throws XMLUtilsException{
		try{
			StringBuilder risultatoValidazione = new StringBuilder();
			if(EsitoRichiestaXMLUtils.validate(esitoRichiesta, risultatoValidazione)==false){
				throw new Exception(risultatoValidazione.toString());
			}
			out.write(EsitoRichiestaXMLUtils.generateEsitoRichiesta_engine(esitoRichiesta));
			out.flush();
			out.close();
		}catch(Exception e){
			throw new XMLUtilsException(e.getMessage(),e);
		}
	}
	
	public static String generateEsitoRichiestaAsJson(EsitoRichiesta esitoRichiesta) throws XMLUtilsException{
		try{
			StringBuilder risultatoValidazione = new StringBuilder();
			if(EsitoRichiestaXMLUtils.validate(esitoRichiesta, risultatoValidazione)==false){
				throw new Exception(risultatoValidazione.toString());
			}
			return EsitoRichiestaXMLUtils.generateEsitoRichiestaAsJson_engine(esitoRichiesta);
		}catch(Exception e){
			throw new XMLUtilsException(e.getMessage(),e);
		}
	}
	
	private static byte[] generateEsitoRichiesta_engine(EsitoRichiesta esitoRichiesta) throws XMLUtilsException{
		try{
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			org.openspcoop2.utils.xml.JaxbUtils.objToXml(bout, EsitoRichiesta.class, esitoRichiesta);
			byte[] dichiarazione = bout.toByteArray();
			return dichiarazione;
		}catch(Exception e){
			throw new XMLUtilsException(e.getMessage(),e);
		}
	}
	
	private static String generateEsitoRichiestaAsJson_engine(EsitoRichiesta esitoRichiesta) throws XMLUtilsException{
		try{
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			esitoRichiesta.writeTo(bout, WriteToSerializerType.JSON_JACKSON);
			bout.flush();
			bout.close();
			return bout.toString();
		}catch(Exception e){
			throw new XMLUtilsException(e.getMessage(),e);
		}
	}
	
	
	
	
	
	public static boolean isEsitoRichiesta(byte [] doc){
		try{
			org.openspcoop2.message.xml.MessageXMLUtils xmlUtils = org.openspcoop2.message.xml.MessageXMLUtils.DEFAULT;
			Document docXML = xmlUtils.newDocument(doc);
			Element elemXML = docXML.getDocumentElement();
			return EsitoRichiestaXMLUtils.isEsitoRichiesta_engine(elemXML);
		}catch(Exception e){
			//System.out.println("NON e' un DOCUMENTO VALIDO: "+e.getMessage());
			return false;
		}
	}
	public static boolean isEsitoRichiesta(Document docXML){
		try{
			Element elemXML = docXML.getDocumentElement();
			return EsitoRichiestaXMLUtils.isEsitoRichiesta_engine(elemXML);
		}catch(Exception e){
			//System.out.println("NON e' un DOCUMENTO VALIDO: "+e.getMessage());
			return false;
		}
	}
	public static boolean isEsitoRichiesta(Element elemXML){
		return isEsitoRichiesta_engine(elemXML);
	}
	public static boolean isEsitoRichiesta(Node nodeXml){
		return isEsitoRichiesta_engine(nodeXml);
	}
	private static boolean isEsitoRichiesta_engine(Node nodeXml){
		try{
			//System.out.println("LOCAL["+Costanti.ROOT_LOCAL_NAME_DETTAGLIO_ECCEZIONE+"]vs["+elemXML.getLocalName()+"]  NAMESPACE["+Costanti.TARGET_NAMESPACE+"]vs["+elemXML.getNamespaceURI()+"]");
			if(Costanti.ROOT_LOCAL_NAME_ESITO_RICHIESTA.equals(nodeXml.getLocalName()) && 
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