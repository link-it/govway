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



package org.openspcoop2.protocol.manifest.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;

import org.slf4j.Logger;
import org.openspcoop2.message.ValidatoreXSD;
import org.openspcoop2.protocol.manifest.Openspcoop2;
import org.openspcoop2.protocol.manifest.RegistroServizi;
import org.openspcoop2.protocol.manifest.Web;
import org.openspcoop2.protocol.manifest.constants.Costanti;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


/**
 * Classe utilizzata per la generazione degli element Details inseriti in un SOAPFault generato dalla PdD in caso di errore di processamento
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
			XMLUtils.validatoreXSD = new ValidatoreXSD(log,XMLUtils.class.getResourceAsStream("/protocolManifest.xsd"));
		}
		return XMLUtils.validatoreXSD;
	}
	public static boolean validate(Openspcoop2 openspcoop2,StringBuffer motivoErroreValidazione){
		
		int size = motivoErroreValidazione.length();
		
		if(openspcoop2.getFactory()==null){
			motivoErroreValidazione.append("Factory non definita\n");
		}
		
		if(openspcoop2.getWeb()==null){
			motivoErroreValidazione.append("Web non definito\n");
		}
		else{
			if(openspcoop2.getWeb().sizeContextList()<=0 && 
					(openspcoop2.getWeb().getEmptyContext()==null || !openspcoop2.getWeb().getEmptyContext().getEnabled())){
				motivoErroreValidazione.append("WebContext non definito ne tramite un context ne tramite un empty context\n");
			}
		}
		
		if(openspcoop2.getRegistroServizi()==null){
			motivoErroreValidazione.append("Registro non definito\n");
		}
		else{
			RegistroServizi reg = openspcoop2.getRegistroServizi();
			
			if(reg.getServizi()==null){
				motivoErroreValidazione.append("Registro.Servizi non definito\n");
			}
			else{
				if(reg.getServizi().getTipi()==null ||  reg.getServizi().getTipi().sizeTipoList()<=0){
					motivoErroreValidazione.append("Registro.Servizi.tipi non definito\n");
				}
			}
			
			if(reg.getSoggetti()==null){
				motivoErroreValidazione.append("Registro.Soggetti non definito\n");
			}
			else{
				if(reg.getSoggetti().getTipi()==null ||  reg.getSoggetti().getTipi().sizeTipoList()<=0){
					motivoErroreValidazione.append("Registro.Soggetti.tipi non definito\n");
				}
			}
			
			if(reg.getVersioni()==null){
				motivoErroreValidazione.append("Registro.Versioni non definito\n");
			}
			else{
				if(reg.getVersioni().sizeVersioneList()<=0){
					motivoErroreValidazione.append("Registro.Versioni.versione non definito\n");
				}
			}
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
	 * @return Openspcoop2
	 * @throws XMLUtilsException
	 */
	public static Openspcoop2 getOpenspcoop2Manifest(Logger log,byte[] m) throws XMLUtilsException{
		ByteArrayInputStream bin = null;
		try{
			bin = new ByteArrayInputStream(m);
			return XMLUtils.getOpenspcoop2Manifest(log,bin);
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
	 * @return Openspcoop2
	 * @throws XMLUtilsException
	 */
	public static Openspcoop2 getOpenspcoop2Manifest(Logger log,File m) throws XMLUtilsException{
		FileInputStream fin = null;
		try{
			fin = new FileInputStream(m);
			return XMLUtils.getOpenspcoop2Manifest(log,fin);
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
	 * @return Openspcoop2
	 * @throws XMLUtilsException
	 */
	public static Openspcoop2 getOpenspcoop2Manifest(Logger log,String m) throws XMLUtilsException{
		return XMLUtils.getOpenspcoop2Manifest(log,m.getBytes());
	}
	
	/**
	 * Ritorna la rappresentazione java
	 * 
	 * @param m InputStream
	 * @return Openspcoop2
	 * @throws XMLUtilsException
	 */
	public static Openspcoop2 getOpenspcoop2Manifest(Logger log,InputStream m) throws XMLUtilsException{
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
			
			// trasformazione in oggetto Openspcoop2
			ByteArrayInputStream binTrasformazione = new ByteArrayInputStream(xml);
			return (Openspcoop2) org.openspcoop2.utils.xml.JiBXUtils.xmlToObj(binTrasformazione, Openspcoop2.class);
			
		}catch(Exception e){
			throw new XMLUtilsException(e.getMessage(),e);
		}
	}

	
	
	
	
	/* ----- Marshall ----- */
	public static void generateOpenspcoop2Manifest(Openspcoop2 manifest,File out) throws XMLUtilsException{
		try{
			StringBuffer risultatoValidazione = new StringBuffer();
			if(XMLUtils.validate(manifest, risultatoValidazione)==false){
				throw new Exception(risultatoValidazione.toString());
			}
			org.openspcoop2.utils.xml.JiBXUtils.objToXml(out.getName(),XMLUtils.generateOpenspcoop2Manifest_engine(manifest));
		}catch(Exception e){
			throw new XMLUtilsException(e.getMessage(),e);
		}
	}
	
	public static void generateOpenspcoop2Manifest(Openspcoop2 manifest,String fileName) throws XMLUtilsException{
		try{
			StringBuffer risultatoValidazione = new StringBuffer();
			if(XMLUtils.validate(manifest, risultatoValidazione)==false){
				throw new Exception(risultatoValidazione.toString());
			}
			org.openspcoop2.utils.xml.JiBXUtils.objToXml(fileName,XMLUtils.generateOpenspcoop2Manifest_engine(manifest));
		}catch(Exception e){
			throw new XMLUtilsException(e.getMessage(),e);
		}
	}
	
	public static byte[] generateOpenspcoop2Manifest(Openspcoop2 manifest) throws XMLUtilsException{
		try{
			StringBuffer risultatoValidazione = new StringBuffer();
			if(XMLUtils.validate(manifest, risultatoValidazione)==false){
				throw new Exception(risultatoValidazione.toString());
			}
			return XMLUtils.generateOpenspcoop2Manifest_engine(manifest);
		}catch(Exception e){
			throw new XMLUtilsException(e.getMessage(),e);
		}
	}

	public static void generateOpenspcoop2Manifest(Openspcoop2 manifest,OutputStream out) throws XMLUtilsException{
		try{
			StringBuffer risultatoValidazione = new StringBuffer();
			if(XMLUtils.validate(manifest, risultatoValidazione)==false){
				throw new Exception(risultatoValidazione.toString());
			}
			out.write(XMLUtils.generateOpenspcoop2Manifest_engine(manifest));
			out.flush();
			out.close();
		}catch(Exception e){
			throw new XMLUtilsException(e.getMessage(),e);
		}
	}
	
	private static byte[] generateOpenspcoop2Manifest_engine(Openspcoop2 manifest) throws XMLUtilsException{
		try{
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			org.openspcoop2.utils.xml.JiBXUtils.objToXml(bout, Openspcoop2.class, manifest);
			byte[] dichiarazione = bout.toByteArray();
			return dichiarazione;
		}catch(Exception e){
			throw new XMLUtilsException(e.getMessage(),e);
		}
	}
	
	
	
	
	
	public static boolean isOpenspcoop2Manifest(byte [] doc){
		try{
			org.openspcoop2.message.XMLUtils xmlUtils = org.openspcoop2.message.XMLUtils.getInstance();
			Document docXML = xmlUtils.newDocument(doc);
			Element elemXML = docXML.getDocumentElement();
			return XMLUtils.isOpenspcoop2Manifest(elemXML);
		}catch(Exception e){
			//System.out.println("NON e' un DOCUMENTO VALIDO: "+e.getMessage());
			return false;
		}
	}
	public static boolean isOpenspcoop2Manifest(Document docXML){
		try{
			Element elemXML = docXML.getDocumentElement();
			return XMLUtils.isOpenspcoop2Manifest(elemXML);
		}catch(Exception e){
			//System.out.println("NON e' un DOCUMENTO VALIDO: "+e.getMessage());
			return false;
		}
	}
	public static boolean isOpenspcoop2Manifest(Element elemXML){
		try{
			//System.out.println("LOCAL["+Costanti.ROOT_LOCAL_NAME_DETTAGLIO_ECCEZIONE+"]vs["+elemXML.getLocalName()+"]  NAMESPACE["+Costanti.TARGET_NAMESPACE+"]vs["+elemXML.getNamespaceURI()+"]");
			if(Costanti.ROOT_LOCAL_NAME_OPENSPCOOP2.equals(elemXML.getLocalName()) && 
					Costanti.TARGET_NAMESPACE.equals(elemXML.getNamespaceURI() ) 
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
	
	

	
	
	
	
	public static String toString(Openspcoop2 manifest){
		StringBuffer bf = new StringBuffer();
		
		bf.append(" Protocollo "+manifest.getProtocolName()+" \n");
		
		bf.append("Factory["+manifest.getFactory()+"]\n");
		
		Web web = manifest.getWeb();
		for (int i = 0; i < web.sizeContextList(); i++) {
			bf.append("Context["+i+"]=["+web.getContext(i)+"]\n");
		}
		if(web.getEmptyContext()!=null){
			bf.append("EmptyContext=["+web.getEmptyContext().getEnabled()+"]\n");
		}
		else{
			bf.append("EmptyContext not defined\n");
		}
		
		RegistroServizi reg = manifest.getRegistroServizi();
		
		if(reg.getServizi()!=null && reg.getServizi().getTipi()!=null){
			for (int i = 0; i < reg.getServizi().getTipi().sizeTipoList(); i++) {
				bf.append("Servizi.tipoSupportato["+i+"]=["+reg.getServizi().getTipi().getTipo(i)+"]\n");
			}
		}
		
		if(reg.getSoggetti()!=null && reg.getSoggetti().getTipi()!=null){
			for (int i = 0; i < reg.getSoggetti().getTipi().sizeTipoList(); i++) {
				bf.append("Soggetti.tipoSupportato["+i+"]=["+reg.getSoggetti().getTipi().getTipo(i)+"]\n");
			}
		}
		
		if(reg.getVersioni()!=null){
			for (int i = 0; i < reg.getVersioni().sizeVersioneList(); i++) {
				bf.append("Versione["+i+"]=["+reg.getVersioni().getVersione(i)+"]\n");
			}
		}
		
		return bf.toString();
	}
}