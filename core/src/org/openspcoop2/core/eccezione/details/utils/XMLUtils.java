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



package org.openspcoop2.core.eccezione.details.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Iterator;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPFault;

import org.slf4j.Logger;
import org.openspcoop2.core.eccezione.details.constants.Costanti;
import org.openspcoop2.core.eccezione.details.Dettaglio;
import org.openspcoop2.core.eccezione.details.DettaglioEccezione;
import org.openspcoop2.core.eccezione.details.Eccezione;
import org.openspcoop2.core.eccezione.details.Dominio;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.ValidatoreXSD;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;


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
			XMLUtils.validatoreXSD = new ValidatoreXSD(log,XMLUtils.class.getResourceAsStream("/openspcoopDetail.xsd"));
		}
		return XMLUtils.validatoreXSD;
	}
	public static boolean validate(DettaglioEccezione eccezione,StringBuffer motivoErroreValidazione){
		
		int size = motivoErroreValidazione.length();
		
		if(eccezione.getDominio()==null){
			motivoErroreValidazione.append("Dominio non definito\n");
		}
		else{
			validate(eccezione.getDominio(), motivoErroreValidazione);
		}
		
		if(eccezione.getOraRegistrazione()==null){
			motivoErroreValidazione.append("OraRegistrazione non definita\n");
		}

		if(eccezione.getEccezioni()==null){
			motivoErroreValidazione.append("Nessuna eccezione definita\n");
		}else{
			if(eccezione.getEccezioni().sizeEccezioneList()<=0){
				motivoErroreValidazione.append("Nessuna eccezione definita\n");
			}
			else{
				for (int i = 0; i < eccezione.getEccezioni().sizeEccezioneList(); i++) {
					Eccezione ecc = eccezione.getEccezioni().getEccezione(i);
					if(ecc.getCodice()==null){
						motivoErroreValidazione.append("Eccezione.codice non definito\n");
					}
					if(ecc.getDescrizione()==null){
						motivoErroreValidazione.append("Eccezione.codice non definito\n");
					}
					if(ecc.getTipo()==null){
						motivoErroreValidazione.append("Eccezione.tipo non definito\n");
					}
				}
			}
		}
		if(eccezione.getDettagli()!=null){
			if(eccezione.getDettagli().sizeDettaglioList()>0){
				for (int i = 0; i < eccezione.getDettagli().sizeDettaglioList(); i++) {
					Dettaglio detail = eccezione.getDettagli().getDettaglio(i);
					if(detail==null){
						motivoErroreValidazione.append("Detail presente e non definito??\n");
					}
					else{
						if(detail.getTipo()==null){
							motivoErroreValidazione.append("Detail["+i+"].tipo non definito\n");
						}
						if(detail.getBase()==null){
							motivoErroreValidazione.append("Detail["+i+"].tipo non definito\n");
						}
					}
				}
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
		if(dominio.getFunzione()==null){
			motivoErroreValidazione.append("Dominio.funzione non definito\n");
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
	 * @return DettaglioEccezione
	 * @throws XMLUtilsException
	 */
	public static DettaglioEccezione getDettaglioEccezione(Logger log,byte[] m) throws XMLUtilsException{
		ByteArrayInputStream bin = null;
		try{
			bin = new ByteArrayInputStream(m);
			return XMLUtils.getDettaglioEccezione(log,bin);
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
	 * @return DettaglioEccezione
	 * @throws XMLUtilsException
	 */
	public static DettaglioEccezione getDettaglioEccezione(Logger log,File m) throws XMLUtilsException{
		FileInputStream fin = null;
		try{
			fin = new FileInputStream(m);
			return XMLUtils.getDettaglioEccezione(log,fin);
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
	 * @return DettaglioEccezione
	 * @throws XMLUtilsException
	 */
	public static DettaglioEccezione getDettaglioEccezione(Logger log,String m) throws XMLUtilsException{
		return XMLUtils.getDettaglioEccezione(log,m.getBytes());
	}
	
	/**
	 * Ritorna la rappresentazione java
	 * 
	 * @param m InputStream
	 * @return DettaglioEccezione
	 * @throws XMLUtilsException
	 */
	public static DettaglioEccezione getDettaglioEccezione(Logger log,InputStream m) throws XMLUtilsException{
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
			return (DettaglioEccezione) org.openspcoop2.utils.xml.JiBXUtils.xmlToObj(binTrasformazione, DettaglioEccezione.class);
			
		}catch(Exception e){
			throw new XMLUtilsException(e.getMessage(),e);
		}
	}

	
	
	
	
	/* ----- Marshall ----- */
	public static void generateDettaglioEccezione(DettaglioEccezione eccezione,File out) throws XMLUtilsException{
		try{
			StringBuffer risultatoValidazione = new StringBuffer();
			if(XMLUtils.validate(eccezione, risultatoValidazione)==false){
				throw new Exception(risultatoValidazione.toString());
			}
			org.openspcoop2.utils.xml.JiBXUtils.objToXml(out.getName(),XMLUtils.generateDettaglioEccezione_engine(eccezione));
		}catch(Exception e){
			throw new XMLUtilsException(e.getMessage(),e);
		}
	}
	
	public static void generateDettaglioEccezione(DettaglioEccezione eccezione,String fileName) throws XMLUtilsException{
		try{
			StringBuffer risultatoValidazione = new StringBuffer();
			if(XMLUtils.validate(eccezione, risultatoValidazione)==false){
				throw new Exception(risultatoValidazione.toString());
			}
			org.openspcoop2.utils.xml.JiBXUtils.objToXml(fileName,XMLUtils.generateDettaglioEccezione_engine(eccezione));
		}catch(Exception e){
			throw new XMLUtilsException(e.getMessage(),e);
		}
	}
	
	public static byte[] generateDettaglioEccezione(DettaglioEccezione eccezione) throws XMLUtilsException{
		try{
			StringBuffer risultatoValidazione = new StringBuffer();
			if(XMLUtils.validate(eccezione, risultatoValidazione)==false){
				throw new Exception(risultatoValidazione.toString());
			}
			return XMLUtils.generateDettaglioEccezione_engine(eccezione);
		}catch(Exception e){
			throw new XMLUtilsException(e.getMessage(),e);
		}
	}

	public static void generateDettaglioEccezione(DettaglioEccezione eccezione,OutputStream out) throws XMLUtilsException{
		try{
			StringBuffer risultatoValidazione = new StringBuffer();
			if(XMLUtils.validate(eccezione, risultatoValidazione)==false){
				throw new Exception(risultatoValidazione.toString());
			}
			out.write(XMLUtils.generateDettaglioEccezione_engine(eccezione));
			out.flush();
			out.close();
		}catch(Exception e){
			throw new XMLUtilsException(e.getMessage(),e);
		}
	}
	
	private static byte[] generateDettaglioEccezione_engine(DettaglioEccezione eccezione) throws XMLUtilsException{
		try{
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			org.openspcoop2.utils.xml.JiBXUtils.objToXml(bout, DettaglioEccezione.class, eccezione);
			byte[] dichiarazione = bout.toByteArray();
			return dichiarazione;
		}catch(Exception e){
			throw new XMLUtilsException(e.getMessage(),e);
		}
	}
	
	
	
	
	public static DettaglioEccezione getDettaglioEccezione(Logger log,OpenSPCoop2Message msg)throws XMLUtilsException{
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
							if(XMLUtils.isDettaglioEccezione(elem)){
								//System.out.println("ITEM ["+elem.getLocalName()+"] TROVATO");
								org.openspcoop2.message.XMLUtils xmlUtils = org.openspcoop2.message.XMLUtils.getInstance();
								byte [] xml = xmlUtils.toByteArray(elem);
								//System.out.println("XML S: "+new String(xml));
								DettaglioEccezione de = XMLUtils.getDettaglioEccezione(log,xml);
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
	
	public static boolean isDettaglioEccezione(byte [] doc){
		try{
			org.openspcoop2.message.XMLUtils xmlUtils = org.openspcoop2.message.XMLUtils.getInstance();
			Document docXML = xmlUtils.newDocument(doc);
			Element elemXML = docXML.getDocumentElement();
			return XMLUtils.isDettaglioEccezione_engine(elemXML);
		}catch(Exception e){
			//System.out.println("NON e' un DOCUMENTO VALIDO: "+e.getMessage());
			return false;
		}
	}
	public static boolean isDettaglioEccezione(Document docXML){
		try{
			Element elemXML = docXML.getDocumentElement();
			return XMLUtils.isDettaglioEccezione_engine(elemXML);
		}catch(Exception e){
			//System.out.println("NON e' un DOCUMENTO VALIDO: "+e.getMessage());
			return false;
		}
	}
	public static boolean isDettaglioEccezione(Element elemXML){
		return isDettaglioEccezione_engine(elemXML);
	}
	public static boolean isDettaglioEccezione(Node nodeXml){
		return isDettaglioEccezione_engine(nodeXml);
	}
	private static boolean isDettaglioEccezione_engine(Node nodeXml){
		try{
			//System.out.println("LOCAL["+Costanti.ROOT_LOCAL_NAME_DETTAGLIO_ECCEZIONE+"]vs["+elemXML.getLocalName()+"]  NAMESPACE["+Costanti.TARGET_NAMESPACE+"]vs["+elemXML.getNamespaceURI()+"]");
			if(Costanti.ROOT_LOCAL_NAME_DETTAGLIO_ECCEZIONE.equals(nodeXml.getLocalName()) && 
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
	
	

	
	
	
	
	public static String toString(DettaglioEccezione de){
		StringBuffer bf = new StringBuffer();
		if(de!=null){
			
			if(de.getOraRegistrazione()!=null){
				SimpleDateFormat dateformat = new SimpleDateFormat ("yyyy-MM-dd_HH:mm:ss.SSS"); // SimpleDateFormat non e' thread-safe
				bf.append("("+dateformat.format(de.getOraRegistrazione())+")");
			}
			
			if(de.getDominio()!=null){
				
				bf.append(" ");
				
				boolean writeSoggetto = false;
				if(de.getDominio().getIdentificativoPorta()!=null){
					bf.append(de.getDominio().getIdentificativoPorta());
					writeSoggetto = true;
					if(de.getDominio().getSoggetto()!=null){
						bf.append(".");
					}
				}
				
				if(de.getDominio().getSoggetto()!=null){
					bf.append(de.getDominio().getSoggetto().getTipo()+"/"+de.getDominio().getSoggetto().getBase());
					writeSoggetto = true;
				}
				
				if(de.getDominio().getFunzione()!=null || de.getDominio().getModulo()!=null){
					if(writeSoggetto){
						bf.append(" ");
					}
				}
				
				if(de.getDominio().getFunzione()!=null){
					bf.append(de.getDominio().getFunzione());
					if(de.getDominio().getModulo()!=null){
						bf.append(".");
					}
				}
				if(de.getDominio().getModulo()!=null){
					bf.append(de.getDominio().getModulo());
				}
			}
			
			
			if(bf.length()>0){
				bf.append(" ");
			}
			
			int sizeEccezioni = 0;
			if(de.getEccezioni()!=null){
				sizeEccezioni = de.getEccezioni().sizeEccezioneList();
			}
			
			int sizeDettagli = 0;
			if(de.getDettagli()!=null){
				sizeDettagli = de.getDettagli().sizeDettaglioList();
			}
			
			bf.append("ha rilevato "+sizeEccezioni+" eccezione/i (dettagli:"+sizeDettagli+")");
			for(int k=0; k<sizeEccezioni;k++){
				bf.append("\n- Eccezione ("+de.getEccezioni().getEccezione(k).getTipo()+") ["+de.getEccezioni().getEccezione(k).getCodice()+"] "+de.getEccezioni().getEccezione(k).getDescrizione());
			}
			if(sizeDettagli>0){
				for(int k=0; k<sizeDettagli;k++){
					bf.append("\n- Dettaglio ["+de.getDettagli().getDettaglio(k).getTipo()+"] "+de.getDettagli().getDettaglio(k).getBase());
				}
			}
		}
		return bf.toString();
	}
}