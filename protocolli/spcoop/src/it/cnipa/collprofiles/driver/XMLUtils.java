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



package it.cnipa.collprofiles.driver;

import it.cnipa.collprofiles.EgovDecllElement;
import it.cnipa.collprofiles.OperationListType;
import it.cnipa.collprofiles.OperationType;
import it.gov.spcoop.sica.manifest.driver.TipiDocumentoSemiformale;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import org.slf4j.Logger;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.Operation;
import org.openspcoop2.core.registry.PortType;
import org.openspcoop2.core.registry.constants.CostantiRegistroServizi;
import org.openspcoop2.core.registry.constants.ProfiloCollaborazione;
import org.openspcoop2.core.registry.constants.StatoFunzionalita;
import org.openspcoop2.core.registry.wsdl.RegistroOpenSPCoopUtilities;
import org.openspcoop2.utils.wsdl.DefinitionWrapper;
import org.openspcoop2.utils.xml.AbstractXMLUtils;
import org.openspcoop2.utils.xml.JiBXUtils;
import org.openspcoop2.utils.xml.ValidatoreXSD;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;


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
			XMLUtils.validatoreXSD = new ValidatoreXSD(log,XMLUtils.class.getResourceAsStream("/SpecificaSemiformaleEGov.xsd"));
		}
		return XMLUtils.validatoreXSD;
	}
	public static boolean validate(EgovDecllElement egov,StringBuffer motivoErroreValidazione){
		
		int size = motivoErroreValidazione.length();
		
		if(egov.getEGovVersion()==null){
			motivoErroreValidazione.append("VersioneEGov non definita\n");
		}
		if(egov.getRifDefinizioneInterfaccia()==null){
			motivoErroreValidazione.append("RifInterfaccia non definita\n");
		}
		
		OperationListType operations = egov.getOperationList();
		if(operations == null){
			motivoErroreValidazione.append("OperationListType non presente\n");
		}else{
			if(operations.sizeOperationList()<=0){
				motivoErroreValidazione.append("OperationListType vuota\n");
			}
			for(int i=0; i<operations.sizeOperationList(); i++){
				OperationType op = operations.getOperation(i);
				if(op.getServizio()==null){
					motivoErroreValidazione.append("OperationList["+i+"] senza il servizio\n");
				}
				if(op.getOperazione()==null){
					motivoErroreValidazione.append("OperationList["+i+"] senza l'operation\n");
				}
				if(op.getProfiloDiCollaborazione()==null){
					motivoErroreValidazione.append("OperationList["+i+"] senza un profilo di collaborazione\n");
				}
				if(TipiProfiliCollaborazione.OneWay.toString().equals(op.getProfiloDiCollaborazione())==false && 
						TipiProfiliCollaborazione.Sincrono.toString().equals(op.getProfiloDiCollaborazione())==false &&
						TipiProfiliCollaborazione.AsincronoSimmetrico.toString().equals(op.getProfiloDiCollaborazione())==false &&
						TipiProfiliCollaborazione.AsincronoAsimmetrico.toString().equals(op.getProfiloDiCollaborazione())==false ){
					motivoErroreValidazione.append("OperationList["+i+"] con un profilo di collaborazione non conosciuto ("+op.getProfiloDiCollaborazione()+")\n");
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
	 * @return EgovDeclType
	 * @throws XMLUtilsException
	 */
	public static EgovDecllElement getDichiarazioneEGov(Logger log,byte[] m,boolean acceptChildUnqualified) throws XMLUtilsException{
		ByteArrayInputStream bin = null;
		try{
			bin = new ByteArrayInputStream(m);
			return XMLUtils.getDichiarazioneEGov(log,bin,acceptChildUnqualified);
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
	public static EgovDecllElement getDichiarazioneEGov(Logger log,File m,boolean acceptChildUnqualified) throws XMLUtilsException{
		FileInputStream fin = null;
		try{
			fin = new FileInputStream(m);
			return XMLUtils.getDichiarazioneEGov(log,fin,acceptChildUnqualified);
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
	public static EgovDecllElement getDichiarazioneEGov(Logger log,String m,boolean acceptChildUnqualified) throws XMLUtilsException{
		return XMLUtils.getDichiarazioneEGov(log,m.getBytes(),acceptChildUnqualified);
	}
	
	/**
	 * Ritorna la rappresentazione java
	 * 
	 * @param m InputStream
	 * @return EgovDeclType
	 * @throws XMLUtilsException
	 */
	public static EgovDecllElement getDichiarazioneEGov(Logger log,InputStream m,boolean acceptChildUnqualified) throws XMLUtilsException{
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
			
			// Modifico namespace spcoop.gov.it in  www.cnipa.it
			// Altrimenti la validazione XSD fallisce.
			// Viene effettuato questa trasformazione per accettare comunque gli xml con il namespace spcoop.gov.it
			if(XMLUtils.isProfiloCollaborazioneEGOV_NamespaceSPCoopGovIT(xml)){
				xml = bout.toString().replaceAll("spcoop\\.gov\\.it","www.cnipa.it").getBytes();
			}
			
			// il Client SICA permette di costruire XML che possiedono un prefix nel root element,
			// ma negli elementi interni tale prefix non viene utilizzato.
			// Es:
			// <tns:egovDecllElement xmlns:tns="http://spcoop.gov.it/collProfiles" ...>
			// 		<e-govVersion>....
			// Questo XML non e' validabile rispetto all'XSD poiche' e-govVersion non e' qualificato.
			if(acceptChildUnqualified){
				xml = XMLUtils.unescapeXMLMalformatoChildElements(xml);
			}
			
			// Validazione XSD
			ByteArrayInputStream binValidazione = new ByteArrayInputStream(xml);
			ValidatoreXSD validatoreXSD = XMLUtils.getValidatoreXSD(log);
			validatoreXSD.valida(binValidazione);
			
			// trasformazione in oggetto EgovDecllElement
			ByteArrayInputStream binTrasformazione = new ByteArrayInputStream(xml);
			return (EgovDecllElement) JiBXUtils.xmlToObj(binTrasformazione, EgovDecllElement.class);
			
		}catch(Exception e){
			throw new XMLUtilsException(e.getMessage(),e);
		}
	}
	
	private static byte [] unescapeXMLMalformatoChildElements(byte [] xml){
		try{
			AbstractXMLUtils xmlUtils = org.openspcoop2.message.XMLUtils.getInstance();		
			Document docXML = xmlUtils.newDocument(xml);
			Element elemXML = docXML.getDocumentElement();
			String prefixRootElement = elemXML.getPrefix();
			String prefixChildElementEgovVersione = null;
			for(int i=0; i< elemXML.getChildNodes().getLength(); i++){
				Node child = elemXML.getChildNodes().item(i);
				if(Costanti.CHILD_ELEMENT_EGOV_VERSIONE_LOCAL_NAME.equals(child.getLocalName())){
					prefixChildElementEgovVersione = child.getPrefix();
				}
			}
			if(prefixRootElement!=null && prefixChildElementEgovVersione==null){
				String xmlS = new String(xml);
				xmlS = xmlS.replace("<"+prefixRootElement+":"+Costanti.ROOT_LOCAL_NAME,"<"+Costanti.ROOT_LOCAL_NAME);
				//xmlS = xmlS.replace("xmlns:wscp=\"http://spcoop.gov.it/sica/wscp\" xmlns","xmlns");
				xmlS = xmlS.replace("xmlns:"+prefixRootElement,"xmlns");
				xmlS = xmlS.replace("</"+prefixRootElement+":"+Costanti.ROOT_LOCAL_NAME,"</"+Costanti.ROOT_LOCAL_NAME);
				xml = xmlS.getBytes();
			}
			
			return xml;
		}catch(Exception e){
			return xml; // ritorno xml senza trasformazioni
		}
	}
	
	
	
	
	/* ----- Marshall Manifest dell'accordo di servizio ----- */
	public static void generateDichiarazioneEGov(EgovDecllElement manifest,File out,boolean namespaceCnipa) throws XMLUtilsException{
		try{
			StringBuffer risultatoValidazione = new StringBuffer();
			if(XMLUtils.validate(manifest, risultatoValidazione)==false){
				throw new Exception(risultatoValidazione.toString());
			}
			JiBXUtils.objToXml(out.getName(),XMLUtils.generateDichiarazioneEGov_engine(manifest, namespaceCnipa));
		}catch(Exception e){
			throw new XMLUtilsException(e.getMessage(),e);
		}
	}
	
	public static void generateDichiarazioneEGov(EgovDecllElement manifest,String fileName,boolean namespaceCnipa) throws XMLUtilsException{
		try{
			StringBuffer risultatoValidazione = new StringBuffer();
			if(XMLUtils.validate(manifest, risultatoValidazione)==false){
				throw new Exception(risultatoValidazione.toString());
			}
			JiBXUtils.objToXml(fileName,XMLUtils.generateDichiarazioneEGov_engine(manifest, namespaceCnipa));
		}catch(Exception e){
			throw new XMLUtilsException(e.getMessage(),e);
		}
	}
	
	public static byte[] generateDichiarazioneEGov(EgovDecllElement manifest,boolean namespaceCnipa) throws XMLUtilsException{
		try{
			StringBuffer risultatoValidazione = new StringBuffer();
			if(XMLUtils.validate(manifest, risultatoValidazione)==false){
				throw new Exception(risultatoValidazione.toString());
			}
			return XMLUtils.generateDichiarazioneEGov_engine(manifest, namespaceCnipa);
		}catch(Exception e){
			throw new XMLUtilsException(e.getMessage(),e);
		}
	}

	public static void generateDichiarazioneEGov(EgovDecllElement manifest,OutputStream out,boolean namespaceCnipa) throws XMLUtilsException{
		try{
			StringBuffer risultatoValidazione = new StringBuffer();
			if(XMLUtils.validate(manifest, risultatoValidazione)==false){
				throw new Exception(risultatoValidazione.toString());
			}
			out.write(XMLUtils.generateDichiarazioneEGov_engine(manifest, namespaceCnipa));
			out.flush();
			out.close();
		}catch(Exception e){
			throw new XMLUtilsException(e.getMessage(),e);
		}
	}
	
	private static byte[] generateDichiarazioneEGov_engine(EgovDecllElement manifest,boolean namespaceCnipa) throws XMLUtilsException{
		try{
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			JiBXUtils.objToXml(bout, EgovDecllElement.class, manifest);
			byte[] dichiarazione = bout.toByteArray();
			if(namespaceCnipa==false){
				// Modifico namespace www.cnipa.it in spcoop.gov.it
				dichiarazione = bout.toString().replaceAll("www\\.cnipa\\.it", "spcoop.gov.it").getBytes();
			}
			return dichiarazione;
		}catch(Exception e){
			throw new XMLUtilsException(e.getMessage(),e);
		}
	}
	
	
	
	
	
	
	
	public static boolean isProfiloCollaborazioneEGOV(byte [] doc){
		try{
			AbstractXMLUtils xmlUtils = org.openspcoop2.message.XMLUtils.getInstance();		
			Document docXML = xmlUtils.newDocument(doc);
			Element elemXML = docXML.getDocumentElement();
			//System.out.println("LOCAL["+Costanti.ROOT_LOCAL_NAME+"]vs["+elemXML.getLocalName()+"]  NAMESPACE["+Costanti.TARGET_NAMESPACE+"]vs["+elemXML.getNamespaceURI()+"]");
			if(Costanti.ROOT_LOCAL_NAME.equals(elemXML.getLocalName()) && 
					( Costanti.TARGET_NAMESPACE.equals(elemXML.getNamespaceURI()) || Costanti.TARGET_NAMESPACE_SPCOOP_GOV_IT.equals(elemXML.getNamespaceURI()) ) 
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
	
	private static boolean isProfiloCollaborazioneEGOV_NamespaceSPCoopGovIT(byte [] doc){
		try{
			AbstractXMLUtils xmlUtils = org.openspcoop2.message.XMLUtils.getInstance();		
			Document docXML = xmlUtils.newDocument(doc);
			Element elemXML = docXML.getDocumentElement();
			//System.out.println("LOCAL["+Costanti.ROOT_LOCAL_NAME+"]vs["+elemXML.getLocalName()+"]  NAMESPACE["+Costanti.TARGET_NAMESPACE+"]vs["+elemXML.getNamespaceURI()+"]");
			if(Costanti.ROOT_LOCAL_NAME.equals(elemXML.getLocalName()) && 
					Costanti.TARGET_NAMESPACE_SPCOOP_GOV_IT.equals(elemXML.getNamespaceURI())  
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
	
	private static String readNomeSPCoop(String QName,Hashtable<String, String> mapPrefixNamespaces) throws XMLUtilsException{
		if(QName==null){
			return null; // per correlati
		}
		else{
			if(QName.contains(":")){
				String [] split = QName.split(":");
				if(split.length!=2){
					 throw new XMLUtilsException("QName ["+QName+"] non valido");
				}
				String prefix = split[0].trim();
				String nomeSPCoop = split[1].trim();
				if(mapPrefixNamespaces.containsKey(prefix)){
					//System.out.println("EFFETTUATO MAPPING IN: "+nomeSPCoop+" da ["+QName+"] del namespace ["+mapPrefixNamespaces.get(prefix)+"]");
					return nomeSPCoop;
				}else{
					throw new XMLUtilsException("QName ["+QName+"] non valido, prefix ["+prefix+"] non associato a nessun namespace");
				}
			}else{
				return QName;
			}
		}
	}
	
	public static void mapProfiloCollaborazioneEGOVIntoAS(Logger log,byte [] doc,AccordoServizioParteComune as,boolean acceptChildUnqualified) throws XMLUtilsException{
		
		// SpecificaSemiformale
		EgovDecllElement egov = null;
		try{
			egov = XMLUtils.getDichiarazioneEGov(log,doc,acceptChildUnqualified);
		}catch(Exception e){
			throw new XMLUtilsException("Documento con informazione egov non leggibile: "+e.getMessage(),e);
		}
		// Lettura specifica come document
		Hashtable<String, String> mapPrefixNamespaces = new Hashtable<String, String>();
		try{
			AbstractXMLUtils xmlUtils = org.openspcoop2.message.XMLUtils.getInstance();		
			Document docXML = xmlUtils.newDocument(doc);
			Element elemXML = docXML.getDocumentElement();
			NamedNodeMap map = elemXML.getAttributes();
			for(int i=0;i<map.getLength();i++){
				Object o = map.item(i);
				if(o instanceof Attr){
					Attr attr = (Attr) o;
					String prefix = "";
					if(attr.getPrefix()!=null){
						prefix = attr.getLocalName(); // il prefisso sara' xmlns
					}
					mapPrefixNamespaces.put(prefix, attr.getValue());
				}
			}
		}catch(Exception e){
			throw new XMLUtilsException("Documento XML con informazione egov non leggibile: "+e.getMessage(),e);
		}
				
		Hashtable<String, PortType> servizi = new Hashtable<String, PortType>();
		OperationListType list = egov.getOperationList();
		for(int h=0; h<list.sizeOperationList(); h++){
			OperationType operationType =  list.getOperation(h);
			String nomeAzione = XMLUtils.readNomeSPCoop(operationType.getOperazione(),mapPrefixNamespaces);
			String nomeServizio = XMLUtils.readNomeSPCoop(operationType.getServizio(),mapPrefixNamespaces);
			String nomeAzioneCorrelata = XMLUtils.readNomeSPCoop(operationType.getOperazioneCorrelata(),mapPrefixNamespaces);
			String nomeServizioCorrelato = XMLUtils.readNomeSPCoop(operationType.getServizioCorrelato(),mapPrefixNamespaces);
			String profiloCollaborazione = operationType.getProfiloDiCollaborazione();
			
			PortType pt = null;
			if(servizi.containsKey(nomeServizio)){
				pt = servizi.remove(nomeServizio);
			}else{
				pt = new PortType();
				pt.setProfiloPT(CostantiRegistroServizi.PROFILO_AZIONE_RIDEFINITO);
				pt.setFiltroDuplicati(StatoFunzionalita.ABILITATO); // secondo le ultime linee guida
				if(TipiProfiliCollaborazione.OneWay.toString().equals(profiloCollaborazione))
					pt.setProfiloCollaborazione(CostantiRegistroServizi.ONEWAY);
				else if(TipiProfiliCollaborazione.Sincrono.toString().equals(profiloCollaborazione))
					pt.setProfiloCollaborazione(CostantiRegistroServizi.SINCRONO);
				else if(TipiProfiliCollaborazione.AsincronoSimmetrico.toString().equals(profiloCollaborazione))
					pt.setProfiloCollaborazione(CostantiRegistroServizi.ASINCRONO_SIMMETRICO);
				else if(TipiProfiliCollaborazione.AsincronoAsimmetrico.toString().equals(profiloCollaborazione))
					pt.setProfiloCollaborazione(CostantiRegistroServizi.ASINCRONO_ASIMMETRICO);
				else
					throw new XMLUtilsException("Profilo di collaborazione non valido ["+profiloCollaborazione+"]");
				pt.setNome(nomeServizio);
			}
			Operation azione = new Operation();
			azione.setNome(nomeAzione);
			azione.setProfAzione(CostantiRegistroServizi.PROFILO_AZIONE_RIDEFINITO);
			azione.setFiltroDuplicati(StatoFunzionalita.ABILITATO); // secondo le ultime linee guida
			if(TipiProfiliCollaborazione.OneWay.toString().equals(profiloCollaborazione))
				azione.setProfiloCollaborazione(CostantiRegistroServizi.ONEWAY);
			else if(TipiProfiliCollaborazione.Sincrono.toString().equals(profiloCollaborazione))
				azione.setProfiloCollaborazione(CostantiRegistroServizi.SINCRONO);
			else if(TipiProfiliCollaborazione.AsincronoSimmetrico.toString().equals(profiloCollaborazione))
				azione.setProfiloCollaborazione(CostantiRegistroServizi.ASINCRONO_SIMMETRICO);
			else if(TipiProfiliCollaborazione.AsincronoAsimmetrico.toString().equals(profiloCollaborazione))
				azione.setProfiloCollaborazione(CostantiRegistroServizi.ASINCRONO_ASIMMETRICO);
			else
				throw new XMLUtilsException("Profilo di collaborazione non valido ["+profiloCollaborazione+"]");
			pt.addAzione(azione);
			servizi.put(nomeServizio, pt);
			
			if(TipiProfiliCollaborazione.AsincronoSimmetrico.toString().equals(profiloCollaborazione) || 
					TipiProfiliCollaborazione.AsincronoAsimmetrico.toString().equals(profiloCollaborazione) ){
				
				if(nomeAzioneCorrelata==null){
					continue;
				}
				
				String servizio = nomeServizioCorrelato;
				if(servizio==null)
					servizio= nomeServizio;
				
				PortType ptCorrelato = null;
				if(servizi.containsKey(servizio)){
					ptCorrelato = servizi.remove(servizio);
				}else{
					ptCorrelato = new PortType();
					ptCorrelato.setProfiloPT(CostantiRegistroServizi.PROFILO_AZIONE_RIDEFINITO);
					ptCorrelato.setFiltroDuplicati(StatoFunzionalita.ABILITATO); // secondo le ultime linee guida
					if(TipiProfiliCollaborazione.AsincronoSimmetrico.toString().equals(profiloCollaborazione))
						ptCorrelato.setProfiloCollaborazione(CostantiRegistroServizi.ASINCRONO_SIMMETRICO);
					else if(TipiProfiliCollaborazione.AsincronoAsimmetrico.toString().equals(profiloCollaborazione))
						ptCorrelato.setProfiloCollaborazione(CostantiRegistroServizi.ASINCRONO_ASIMMETRICO);
					ptCorrelato.setNome(servizio);
				}
				Operation azioneCorrelata = new Operation();
				azioneCorrelata.setNome(nomeAzioneCorrelata);
				azioneCorrelata.setProfAzione(CostantiRegistroServizi.PROFILO_AZIONE_RIDEFINITO);
				azioneCorrelata.setFiltroDuplicati(StatoFunzionalita.ABILITATO); // secondo le ultime linee guida
				if(TipiProfiliCollaborazione.AsincronoSimmetrico.toString().equals(profiloCollaborazione))
					azioneCorrelata.setProfiloCollaborazione(CostantiRegistroServizi.ASINCRONO_SIMMETRICO);
				else if(TipiProfiliCollaborazione.AsincronoAsimmetrico.toString().equals(profiloCollaborazione))
					azioneCorrelata.setProfiloCollaborazione(CostantiRegistroServizi.ASINCRONO_ASIMMETRICO);
				if(nomeServizio!=null)
					azioneCorrelata.setCorrelataServizio(nomeServizio);
				azioneCorrelata.setCorrelata(nomeAzione);
				ptCorrelato.addAzione(azioneCorrelata);
				
				servizi.put(servizio, ptCorrelato);
			}
		}
		
		Enumeration<String> keys = servizi.keys();
		while(keys.hasMoreElements()){
			String pt = keys.nextElement();
			as.addPortType(servizi.get(pt));
		}

	}
	
	
	
	
	
	
	
	
	
	public static it.gov.spcoop.sica.manifest.DocumentoSemiformale generaDocumentoSemiformale(AccordoServizioParteComune as,
			it.gov.spcoop.sica.dao.AccordoServizioParteComune accServParteComuneSICA,boolean namespaceCnipa,boolean nomiSPCoopQualified) throws XMLUtilsException{
		it.gov.spcoop.sica.manifest.DocumentoSemiformale docSemiformalePerManifest = XMLUtils.generaDocumentoSemiformalePerManifesto();
		it.gov.spcoop.sica.dao.Documento docSICA = XMLUtils.generaDocumentoSemiformale(as,namespaceCnipa,nomiSPCoopQualified);
		accServParteComuneSICA.addSpecificaSemiformale(docSICA);
		return docSemiformalePerManifest;
	}
	public static it.gov.spcoop.sica.manifest.DocumentoSemiformale generaDocumentoSemiformale(AccordoServizioParteComune as,
			it.gov.spcoop.sica.dao.AccordoServizioComposto accServCompostoSICA,boolean namespaceCnipa,boolean nomiSPCoopQualified) throws XMLUtilsException{
		it.gov.spcoop.sica.manifest.DocumentoSemiformale docSemiformalePerManifest = XMLUtils.generaDocumentoSemiformalePerManifesto();
		it.gov.spcoop.sica.dao.Documento docSICA = XMLUtils.generaDocumentoSemiformale(as,namespaceCnipa,nomiSPCoopQualified);
		accServCompostoSICA.addSpecificaSemiformale(docSICA);
		return docSemiformalePerManifest;
	}
	private static it.gov.spcoop.sica.manifest.DocumentoSemiformale generaDocumentoSemiformalePerManifesto() throws XMLUtilsException{
		it.gov.spcoop.sica.manifest.DocumentoSemiformale docSemiformale = new it.gov.spcoop.sica.manifest.DocumentoSemiformale();
		docSemiformale.setTipo(TipiDocumentoSemiformale.XML.toString());
		docSemiformale.setBase(it.cnipa.collprofiles.driver.Costanti.SPECIFICA_SEMIFORMALE_INFORMAZIONI_EGOV);
		return docSemiformale;
	}
	private static it.gov.spcoop.sica.dao.Documento generaDocumentoSemiformale(AccordoServizioParteComune as,boolean namespaceCnipa,boolean nomiSPCoopQualified) throws XMLUtilsException{
		it.gov.spcoop.sica.dao.Documento docSICA = new it.gov.spcoop.sica.dao.Documento();
		docSICA.setTipo(TipiDocumentoSemiformale.XML.toString());
		docSICA.setNome(it.cnipa.collprofiles.driver.Costanti.SPECIFICA_SEMIFORMALE_INFORMAZIONI_EGOV);
		try{
			byte[] dichiarazioneEGov = XMLUtils.generaDocumentoEGov(as, namespaceCnipa, nomiSPCoopQualified);
			docSICA.setContenuto(dichiarazioneEGov);
		}catch(Exception e){
			throw new XMLUtilsException("Generazione dichiarazione eGov fallita: "+e.getMessage(),e);
		}
		return docSICA;
	}
	
	public static String generaGenericoDocumento(AccordoServizioParteComune as,
			it.gov.spcoop.sica.dao.AccordoServizioParteComune accServParteComuneSICA,boolean namespaceCnipa,boolean nomiSPCoopQualified) throws XMLUtilsException{
		String genericoDocumentoPerManifest =  XMLUtils.generaGenericoDocumentoPerManifesto();
		it.gov.spcoop.sica.dao.Documento docSICA = XMLUtils.generaGenericoDocumento(as,namespaceCnipa,nomiSPCoopQualified);
		accServParteComuneSICA.addAllegato(docSICA);	
		return genericoDocumentoPerManifest;
	}
	public static String generaGenericoDocumento(AccordoServizioParteComune as,
			it.gov.spcoop.sica.dao.AccordoServizioComposto accServCompostoSICA,boolean namespaceCnipa,boolean nomiSPCoopQualified) throws XMLUtilsException{
		String genericoDocumentoPerManifest =  XMLUtils.generaGenericoDocumentoPerManifesto();
		it.gov.spcoop.sica.dao.Documento docSICA = XMLUtils.generaGenericoDocumento(as,namespaceCnipa,nomiSPCoopQualified);
		accServCompostoSICA.addAllegato(docSICA);	
		return genericoDocumentoPerManifest;
	}
	private static String generaGenericoDocumentoPerManifesto() throws XMLUtilsException{
		return it.cnipa.collprofiles.driver.Costanti.SPECIFICA_SEMIFORMALE_INFORMAZIONI_EGOV;
	}
	private static it.gov.spcoop.sica.dao.Documento generaGenericoDocumento(AccordoServizioParteComune as,boolean namespaceCnipa,boolean nomiSPCoopQualified) throws XMLUtilsException{
		it.gov.spcoop.sica.dao.Documento docSICA = new it.gov.spcoop.sica.dao.Documento();
		docSICA.setTipo(TipiDocumentoSemiformale.XML.toString());
		docSICA.setNome(it.cnipa.collprofiles.driver.Costanti.SPECIFICA_SEMIFORMALE_INFORMAZIONI_EGOV);
		try{
			byte[] dichiarazioneEGov = XMLUtils.generaDocumentoEGov(as, namespaceCnipa, nomiSPCoopQualified);
			docSICA.setContenuto(dichiarazioneEGov);
		}catch(Exception e){
			throw new XMLUtilsException("Generazione dichiarazione eGov fallita: "+e.getMessage(),e);
		}
		return docSICA;
	}
	
	private static byte[] generaDocumentoEGov(AccordoServizioParteComune as,boolean namespaceCnipa,boolean nomiSPCoopQualified) throws XMLUtilsException{
		String prefissoNomiSPCoop = "";
		String namespaceQualified = null;
		if(nomiSPCoopQualified){
			try{
				byte[] wsdlConcettuale = as.getByteWsdlConcettuale();
				if(wsdlConcettuale!=null){
					RegistroOpenSPCoopUtilities wsdlUtility = new RegistroOpenSPCoopUtilities(null);
					wsdlConcettuale = wsdlUtility.eliminaImportASParteComune(wsdlConcettuale);
					DefinitionWrapper wsdl = new DefinitionWrapper(wsdlConcettuale,org.openspcoop2.message.XMLUtils.getInstance());
					String targetNamespace = wsdl.getTargetNamespace();
					String prefix = null;
					if(targetNamespace!=null){
						prefix = wsdl.getPrefix(targetNamespace);
						if(prefix==null)
							prefix = "tnsService";
					}
					if(targetNamespace!=null){
						prefissoNomiSPCoop = prefix+":";
						namespaceQualified = "xmlns"+":"+prefix+"=\""+targetNamespace+"\"";
					}
				}
			}catch(Exception e){
				// errore non importantissimo. Se non vi e' un corretto wsdl, non viene utilizzato un prefisso per i nomi spcoop
				System.out.println("errore durante la lettura del target namespace del wsdl: "+e.getMessage());
			}
		}
		byte[] dichiarazioneEGov = XMLUtils.generateDichiarazioneEGov(XMLUtils.generaProfiloCollaborazioneEGOV(as,prefissoNomiSPCoop),namespaceCnipa);
		if(nomiSPCoopQualified && namespaceQualified!=null){
			dichiarazioneEGov = new String(dichiarazioneEGov).replaceFirst(Costanti.ROOT_LOCAL_NAME, Costanti.ROOT_LOCAL_NAME+" "+namespaceQualified).getBytes();
		}
		return dichiarazioneEGov;
	}
	
	private static EgovDecllElement generaProfiloCollaborazioneEGOV(AccordoServizioParteComune as,String prefissoNomiSPCoop) throws XMLUtilsException{
		
		if(as.sizePortTypeList()>0){
			EgovDecllElement egov = new EgovDecllElement();
			
			egov.setEGovVersion(it.cnipa.collprofiles.driver.Costanti.VERSIONE_BUSTA);
			egov.setRifDefinizioneInterfaccia(as.getNome());
			
			OperationListType operations = new OperationListType();
			
			Vector<OperationType> operationsSICA_asincrone_nonAncoraInserite = new Vector<OperationType>();
					
			for(int i=0; i<as.sizePortTypeList(); i++){
				
				PortType pt = as.getPortType(i);
				String nomeServizio = prefissoNomiSPCoop+pt.getNome();
				
				for(int j=0; j<pt.sizeAzioneList(); j++){
					
					Operation op = pt.getAzione(j);
					
					String nomeAzione = prefissoNomiSPCoop+op.getNome();
					String nomeAzioneCorrelata = op.getCorrelata();
					if(nomeAzioneCorrelata!=null)
						nomeAzioneCorrelata=prefissoNomiSPCoop+nomeAzioneCorrelata;
					String nomeServizioCorrelato = op.getCorrelataServizio();
					if(nomeServizioCorrelato!=null)
						nomeServizioCorrelato=prefissoNomiSPCoop+nomeServizioCorrelato;
			
					ProfiloCollaborazione profilo = null;
					if(CostantiRegistroServizi.PROFILO_AZIONE_RIDEFINITO.equals(op.getProfAzione())){
						profilo = op.getProfiloCollaborazione();
					}else{
						profilo = pt.getProfiloCollaborazione();
					}
					if(CostantiRegistroServizi.ASINCRONO_ASIMMETRICO.equals(profilo) ||  CostantiRegistroServizi.ASINCRONO_SIMMETRICO.equals(profilo)){
						
						// Nel profilo asincrono asimmetrico il registro dei servizi di openspcoop dispone delle informazioni
						// necessarie per la correlazione.
						// Per l'asincrono simmetrico invece no.
						// Comunque anche per l'asincrono asimmetrico dove vengono usati due servizi correlati tale fatto non succede.
						// Questo aspetto viene gestito come segue:
						// Se l'operation e' null, viene tenuta in memoria tale operation (asincrona senza correlazione)
						// Se poi e' presente un'altra azione che contiene la correlazione, tale operation non sara' inserita.
						
						if(nomeAzioneCorrelata==null){
							
							// Vedo dopo se aggiungerlo
							
							OperationType opSICA = new OperationType();
							opSICA.setOperazione(nomeAzione); 
							if(CostantiRegistroServizi.ASINCRONO_ASIMMETRICO.equals(profilo))
								opSICA.setProfiloDiCollaborazione(TipiProfiliCollaborazione.AsincronoAsimmetrico.name());
							else if(CostantiRegistroServizi.ASINCRONO_SIMMETRICO.equals(profilo))
								opSICA.setProfiloDiCollaborazione(TipiProfiliCollaborazione.AsincronoSimmetrico.name());
							opSICA.setServizio(nomeServizio);
							operationsSICA_asincrone_nonAncoraInserite.add(opSICA);
						}else{
							
							// Operazione asincrono con info correlata
							
							OperationType opSICA = new OperationType();
							opSICA.setOperazione(nomeAzioneCorrelata);
							opSICA.setOperazioneCorrelata(nomeAzione);
							if(CostantiRegistroServizi.ASINCRONO_ASIMMETRICO.equals(profilo))
								opSICA.setProfiloDiCollaborazione(TipiProfiliCollaborazione.AsincronoAsimmetrico.name());
							else if(CostantiRegistroServizi.ASINCRONO_SIMMETRICO.equals(profilo))
								opSICA.setProfiloDiCollaborazione(TipiProfiliCollaborazione.AsincronoSimmetrico.name());
							if(nomeServizioCorrelato!=null)
								opSICA.setServizio(nomeServizioCorrelato);
							else
								opSICA.setServizio(nomeServizio);
							opSICA.setServizioCorrelato(nomeServizio);
							operations.addOperation(opSICA);
						}
					}else{
						OperationType opSICA = new OperationType();
						opSICA.setOperazione(nomeAzione); 
						if(CostantiRegistroServizi.ONEWAY.equals(profilo))
							opSICA.setProfiloDiCollaborazione(TipiProfiliCollaborazione.OneWay.name());
						else if(CostantiRegistroServizi.SINCRONO.equals(profilo))
							opSICA.setProfiloDiCollaborazione(TipiProfiliCollaborazione.Sincrono.name());
						opSICA.setServizio(nomeServizio);
						operations.addOperation(opSICA);
					}
					
				}
			}
			
			
			// check operazioni non ancora inserite
			while(operationsSICA_asincrone_nonAncoraInserite.size()>0){
				OperationType opSICA = operationsSICA_asincrone_nonAncoraInserite.remove(0);
				boolean find = false;
				for(int i=0;i<operations.sizeOperationList();i++){
					if(opSICA.getServizio().equals(operations.getOperation(i).getServizio()) &&
							opSICA.getOperazione().equals(operations.getOperation(i).getOperazione()) ){
						find =  true;
						break;
					}
				}
				if(find==false){
					operations.addOperation(opSICA);
				}
			}
			
			
			egov.setOperationList(operations);
			
			return egov;
		}
		else {
			throw new  XMLUtilsException("PortTypes non definiti per l'accordo di servizio");
		}
	}
}