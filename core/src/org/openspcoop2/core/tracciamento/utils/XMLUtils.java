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



package org.openspcoop2.core.tracciamento.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;

import org.slf4j.Logger;
import org.openspcoop2.core.tracciamento.Allegati;
import org.openspcoop2.core.tracciamento.Allegato;
import org.openspcoop2.core.tracciamento.Busta;
import org.openspcoop2.core.tracciamento.Data;
import org.openspcoop2.core.tracciamento.Dominio;
import org.openspcoop2.core.tracciamento.Eccezione;
import org.openspcoop2.core.tracciamento.Eccezioni;
import org.openspcoop2.core.tracciamento.ProfiloCollaborazione;
import org.openspcoop2.core.tracciamento.ProfiloTrasmissione;
import org.openspcoop2.core.tracciamento.Proprieta;
import org.openspcoop2.core.tracciamento.Protocollo;
import org.openspcoop2.core.tracciamento.Riscontri;
import org.openspcoop2.core.tracciamento.Riscontro;
import org.openspcoop2.core.tracciamento.Servizio;
import org.openspcoop2.core.tracciamento.Soggetto;
import org.openspcoop2.core.tracciamento.Traccia;
import org.openspcoop2.core.tracciamento.Trasmissione;
import org.openspcoop2.core.tracciamento.Trasmissioni;
import org.openspcoop2.core.tracciamento.constants.CostantiTracciamento;
import org.openspcoop2.core.tracciamento.utils.serializer.JsonJacksonDeserializer;
import org.openspcoop2.message.OpenSPCoop2MessageFactory;
import org.openspcoop2.message.xml.ValidatoreXSD;
import org.openspcoop2.utils.beans.WriteToSerializerType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;


/**
 * Classe utilizzata per la generazione delle tracce generati dalla PdD
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
			XMLUtils.validatoreXSD = new ValidatoreXSD(OpenSPCoop2MessageFactory.getDefaultMessageFactory(), log,XMLUtils.class.getResourceAsStream("/openspcoopTracciamento.xsd"));
		}
	}
	public static ValidatoreXSD getValidatoreXSD(Logger log) throws Exception{
		if(XMLUtils.validatoreXSD==null){
			initValidatoreXSD(log);
		}
		return XMLUtils.validatoreXSD;
	}
	private static final String BUSTA_PREFIX = "Busta.";
	public static boolean validate(Traccia traccia,StringBuilder motivoErroreValidazione){
		
		int size = motivoErroreValidazione.length();
		
		if(traccia.getTipo()==null){
			motivoErroreValidazione.append("Tipo non definito\n");
		}
		
		if(traccia.getDominio()==null){
			motivoErroreValidazione.append("Dominio non definito\n");
		}
		else{
			validate(traccia.getDominio(), motivoErroreValidazione);
		}
		
		if(traccia.getOraRegistrazione()==null){
			motivoErroreValidazione.append("OraRegistrazione non definita\n");
		}
		
		if(traccia.getEsitoElaborazione()==null){
			motivoErroreValidazione.append("EsitoElaborazione non definito\n");
		}
		else{
			if(traccia.getEsitoElaborazione().getTipo()==null){
				motivoErroreValidazione.append("EsitoElaborazione.tipo non definito\n");
			}
		}
		
		if(traccia.getLocation()==null){
			motivoErroreValidazione.append("Location non definito\n");
		}
		
		if(traccia.getBusta()==null){
			motivoErroreValidazione.append("Busta non definita\n");
		}
		else{
			validate(traccia.getBusta(), motivoErroreValidazione);
		}
		
		if(traccia.getAllegati()!=null){
			validate(traccia.getAllegati(), motivoErroreValidazione);
		}
		
		return motivoErroreValidazione.length()==size;

	}
	private static void validate(Dominio dominio,StringBuilder motivoErroreValidazione){
		if(dominio.getIdentificativoPorta()==null){
			motivoErroreValidazione.append("Dominio.identificativoPorta non definito\n");
		}
		if(dominio.getFunzione()==null){
			motivoErroreValidazione.append("Dominio.funzione non definito\n");
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
	private static void validate(Busta busta,StringBuilder motivoErroreValidazione){
		if(busta.getMittente()!=null){
			validate(busta.getMittente(), motivoErroreValidazione, "mittente");
		}
		if(busta.getDestinatario()!=null){
			validate(busta.getDestinatario(), motivoErroreValidazione , "destinatario");
		}
		if(busta.getProfiloCollaborazione()!=null){
			validate(busta.getProfiloCollaborazione(), motivoErroreValidazione);
		}
		if(busta.getServizio()!=null){
			validate(busta.getServizio(), motivoErroreValidazione,"servizio");
		}
		if(busta.getServizioCorrelato()!=null){
			validate(busta.getServizio(), motivoErroreValidazione,"servizio-correlato");
		}
		if(busta.getOraRegistrazione()!=null){
			validate(busta.getOraRegistrazione(), motivoErroreValidazione,"ora-registrazione");
		}
		if(busta.getProfiloTrasmissione()!=null){
			validate(busta.getProfiloTrasmissione(), motivoErroreValidazione);
		}
		if(busta.getTrasmissioni()!=null){
			validate(busta.getTrasmissioni(), motivoErroreValidazione);
		}
		if(busta.getRiscontri()!=null){
			validate(busta.getRiscontri(), motivoErroreValidazione);
		}
		if(busta.getEccezioni()!=null){
			validate(busta.getEccezioni(), motivoErroreValidazione);
		}
		if(busta.getProtocollo()==null){
			motivoErroreValidazione.append("Busta.protocollo non definita\n");
		}
		else{
			validate(busta.getProtocollo(), motivoErroreValidazione);
		}
	}
	private static void validate(Soggetto soggetto,StringBuilder motivoErroreValidazione,String tipo){
		String prefix = BUSTA_PREFIX+tipo+".identificativo";
		if(soggetto.getIdentificativo()==null){
			motivoErroreValidazione.append(prefix+" non definita\n");
		}
		else{
			if(soggetto.getIdentificativo().getTipo()==null){
				motivoErroreValidazione.append(prefix+".tipo non definita\n");
			}
			if(soggetto.getIdentificativo().getBase()==null){
				motivoErroreValidazione.append(prefix+".base non definita\n");
			}
		}
	}
	private static void validate(ProfiloCollaborazione profilo,StringBuilder motivoErroreValidazione){
		if(profilo.getTipo()==null){
			motivoErroreValidazione.append("Busta.profiloCollaborazione.identificativo.tipo non definita\n");
		}
		if(profilo.getBase()==null){
			motivoErroreValidazione.append("Busta.profiloCollaborazione.identificativo.base non definita\n");
		}
	}
	private static void validate(Servizio servizio,StringBuilder motivoErroreValidazione,String tipo){
		if(servizio.getBase()==null){
			motivoErroreValidazione.append(BUSTA_PREFIX+tipo+".base non definita\n");
		}
	}
	private static void validate(Data data,StringBuilder motivoErroreValidazione,String tipo){
		if(data.getSorgente()!=null){
			if(data.getSorgente().getBase()==null){
				motivoErroreValidazione.append(BUSTA_PREFIX+tipo+".sorgente.base non definita\n");
			}
			if(data.getSorgente().getTipo()==null){
				motivoErroreValidazione.append(BUSTA_PREFIX+tipo+".sorgente.tipo non definita\n");
			}
		}
	}
	private static void validate(ProfiloTrasmissione profiloTrasmissione,StringBuilder motivoErroreValidazione){
		if(profiloTrasmissione.getInoltro()!=null){
			if(profiloTrasmissione.getInoltro().getBase()==null){
				motivoErroreValidazione.append("Busta.profiloTrasmissione.base non definita\n");
			}
			if(profiloTrasmissione.getInoltro().getTipo()==null){
				motivoErroreValidazione.append("Busta.profiloTrasmissione.tipo non definita\n");
			}
		}
	}
	private static void validate(Trasmissioni trasmissioni,StringBuilder motivoErroreValidazione){
		for (int i = 0; i < trasmissioni.sizeTrasmissioneList(); i++) {
			Trasmissione tr = trasmissioni.getTrasmissione(i);
			if(tr==null){
				motivoErroreValidazione.append("Busta.tramissione["+i+"] non definita\n");
			}
			else{
				String prefix = "tramissione["+i+"]";
				if(tr.getOrigine()!=null){
					validate(tr.getOrigine(), motivoErroreValidazione, prefix+".origine");
				}
				if(tr.getDestinazione()!=null){
					validate(tr.getDestinazione(), motivoErroreValidazione, prefix+".destinazione");
				}
				if(tr.getOraRegistrazione()!=null){
					validate(tr.getOraRegistrazione(), motivoErroreValidazione, prefix+".ora-registrazione");
				}
			}
		}
	}
	private static void validate(Riscontri riscontri,StringBuilder motivoErroreValidazione){
		for (int i = 0; i < riscontri.sizeRiscontroList(); i++) {
			Riscontro r = riscontri.getRiscontro(i);
			if(r==null){
				motivoErroreValidazione.append("Busta.riscontro["+i+"] non definito\n");
			}
			else{
				if(r.getOraRegistrazione()!=null){
					validate(r.getOraRegistrazione(), motivoErroreValidazione, "riscontro["+i+"].ora-registrazione");
				}
			}
		}
	}
	private static void validate(Eccezioni eccezioni,StringBuilder motivoErroreValidazione){
		for (int i = 0; i < eccezioni.sizeEccezioneList(); i++) {
			String prefix = "Busta.eccezione["+i+"]";
			Eccezione e = eccezioni.getEccezione(i);
			if(e==null){
				motivoErroreValidazione.append(prefix+" non definita\n");
			}
			else{
				validate(prefix, e, motivoErroreValidazione);
			}
		}
	}
	private static void validate(String prefix, Eccezione e,StringBuilder motivoErroreValidazione){
		if(e.getCodice()!=null){
			if(e.getCodice().getBase()==null){
				motivoErroreValidazione.append(prefix+".codice.base non definita\n");
			}
			if(e.getCodice().getTipo()==null){
				motivoErroreValidazione.append(prefix+".codice.tipo non definita\n");
			}
		}
		if(e.getContestoCodifica()!=null){
			if(e.getContestoCodifica().getBase()==null){
				motivoErroreValidazione.append(prefix+".contesto-codifica.base non definita\n");
			}
			if(e.getContestoCodifica().getTipo()==null){
				motivoErroreValidazione.append(prefix+".contesto-codifica.tipo non definita\n");
			}
		}
		if(e.getRilevanza()!=null){
			if(e.getRilevanza().getBase()==null){
				motivoErroreValidazione.append(prefix+".rilevanza.base non definita\n");
			}
			if(e.getRilevanza().getTipo()==null){
				motivoErroreValidazione.append(prefix+".rilevanza.tipo non definita\n");
			}
		}
	}
	private static void validate(Allegati allegati,StringBuilder motivoErroreValidazione){
		for (int i = 0; i < allegati.sizeAllegatoList(); i++) {
			Allegato a = allegati.getAllegato(i);
			if(a==null){
				motivoErroreValidazione.append("Busta.allegato["+i+"] non definita\n");
			}
		}
	}
	private static void validate(Protocollo protocollo,StringBuilder motivoErroreValidazione){
		if(protocollo.getIdentificativo()==null){
			motivoErroreValidazione.append("Busta.protocollo.identificativo non definito\n");
		}
		for (int i = 0; i < protocollo.sizeProprietaList(); i++) {
			String prefix = "Busta.protocollo.proprieta["+i+"]";
			Proprieta pp = protocollo.getProprieta(i);
			if(pp==null){
				motivoErroreValidazione.append(prefix+" non definito\n");
			}
			else{
				if(pp.getNome()==null){
					motivoErroreValidazione.append(prefix+".nome non definito\n");
				}
				if(pp.getValore()==null){
					motivoErroreValidazione.append(prefix+"["+pp.getNome()+"].valore non definito\n");
				}
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
	public static Traccia getTraccia(Logger log,byte[] m) throws XMLUtilsException{
		try (ByteArrayInputStream bin = new ByteArrayInputStream(m);){
			return XMLUtils.getTraccia(log,bin);
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
	public static Traccia getTraccia(Logger log,File m) throws XMLUtilsException{
		try (FileInputStream fin = new FileInputStream(m);){
			return XMLUtils.getTraccia(log,fin);
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
	public static Traccia getTraccia(Logger log,String m) throws XMLUtilsException{
		return XMLUtils.getTraccia(log,m.getBytes());
	}
	
	/**
	 * Ritorna la rappresentazione java
	 * 
	 * @param m InputStream
	 * @return DettaglioEccezione
	 * @throws XMLUtilsException
	 */
	public static Traccia getTraccia(Logger log,InputStream m) throws XMLUtilsException{
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
			return (Traccia) org.openspcoop2.utils.xml.JaxbUtils.xmlToObj(binTrasformazione, Traccia.class);
			
		}catch(Exception e){
			throw new XMLUtilsException(e.getMessage(),e);
		}
	}


	public static Traccia getTracciaFromJson(Logger log,InputStream is) throws XMLUtilsException{
		try{			
			if(log!=null) {
				// ignore
			}
			JsonJacksonDeserializer deserializer = new JsonJacksonDeserializer();
			return deserializer.readTraccia(is);
		}catch(Exception e){
			throw new XMLUtilsException(e.getMessage(),e);
		}
	}
	
	
	
	/* ----- Marshall ----- */
	public static void generateTraccia(Traccia traccia,File out) throws XMLUtilsException{
		generateTraccia(traccia, out, false, false);
	}
	public static void generateTraccia(Traccia traccia,File out,boolean prettyDocument, boolean omitXmlDeclaration) throws XMLUtilsException{
		try{
			StringBuilder risultatoValidazione = new StringBuilder();
			if(!XMLUtils.validate(traccia, risultatoValidazione)){
				throw new XMLUtilsException(risultatoValidazione.toString());
			}
			org.openspcoop2.utils.xml.JaxbUtils.objToXml(out.getName(),XMLUtils.generateTracciaEngine(traccia, prettyDocument, omitXmlDeclaration));
		}catch(Exception e){
			throw new XMLUtilsException(e.getMessage(),e);
		}
	}
	
	public static void generateTraccia(Traccia traccia,String fileName) throws XMLUtilsException{
		generateTraccia(traccia, fileName, false, false);
	}
	public static void generateTraccia(Traccia traccia,String fileName,boolean prettyDocument, boolean omitXmlDeclaration) throws XMLUtilsException{
		try{
			StringBuilder risultatoValidazione = new StringBuilder();
			if(!XMLUtils.validate(traccia, risultatoValidazione)){
				throw new XMLUtilsException(risultatoValidazione.toString());
			}
			org.openspcoop2.utils.xml.JaxbUtils.objToXml(fileName,XMLUtils.generateTracciaEngine(traccia, prettyDocument, omitXmlDeclaration));
		}catch(Exception e){
			throw new XMLUtilsException(e.getMessage(),e);
		}
	}
	
	public static byte[] generateTraccia(Traccia traccia) throws XMLUtilsException{
		return generateTraccia(traccia, false, false);
	}
	public static byte[] generateTraccia(Traccia traccia,boolean prettyDocument, boolean omitXmlDeclaration) throws XMLUtilsException{
		try{
			StringBuilder risultatoValidazione = new StringBuilder();
			if(!XMLUtils.validate(traccia, risultatoValidazione)){
				throw new XMLUtilsException(risultatoValidazione.toString());
			}
			return XMLUtils.generateTracciaEngine(traccia, prettyDocument, omitXmlDeclaration);
		}catch(Exception e){
			throw new XMLUtilsException(e.getMessage(),e);
		}
	}

	public static void generateTraccia(Traccia traccia,OutputStream out) throws XMLUtilsException{
		generateTraccia(traccia, out, false, false);
	}
	public static void generateTraccia(Traccia traccia,OutputStream out, boolean prettyDocument, boolean omitXmlDeclaration) throws XMLUtilsException{
		try{
			StringBuilder risultatoValidazione = new StringBuilder();
			if(!XMLUtils.validate(traccia, risultatoValidazione)){
				throw new XMLUtilsException(risultatoValidazione.toString());
			}
			out.write(XMLUtils.generateTracciaEngine(traccia, prettyDocument, omitXmlDeclaration));
			out.flush();
			out.close();
		}catch(Exception e){
			throw new XMLUtilsException(e.getMessage(),e);
		}
	}
	
	public static String generateTracciaAsJson(Traccia traccia) throws XMLUtilsException{
		return generateTracciaAsJson(traccia, false);
	}
	public static String generateTracciaAsJson(Traccia traccia, boolean prettyDocument) throws XMLUtilsException{
		try{
			StringBuilder risultatoValidazione = new StringBuilder();
			if(!XMLUtils.validate(traccia, risultatoValidazione)){
				throw new XMLUtilsException(risultatoValidazione.toString());
			}
			return XMLUtils.generateTracciaAsJsonEngine(traccia, prettyDocument);
		}catch(Exception e){
			throw new XMLUtilsException(e.getMessage(),e);
		}
	}
	
	private static byte[] generateTracciaEngine(Traccia traccia,boolean prettyDocument, boolean omitXmlDeclaration) throws XMLUtilsException{
		try{
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			org.openspcoop2.utils.xml.JaxbUtils.objToXml(bout, Traccia.class, traccia, prettyDocument, omitXmlDeclaration);
			return bout.toByteArray();
		}catch(Exception e){
			throw new XMLUtilsException(e.getMessage(),e);
		}
	}
	
	private static String generateTracciaAsJsonEngine(Traccia traccia,boolean prettyDocument) throws XMLUtilsException{
		try{
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			traccia.writeTo(bout, WriteToSerializerType.JSON_JACKSON,prettyDocument);
			bout.flush();
			bout.close();
			return bout.toString();
		}catch(Exception e){
			throw new XMLUtilsException(e.getMessage(),e);
		}
	}
	
	
	
	public static boolean isTraccia(byte [] doc){
		try{
			org.openspcoop2.message.xml.MessageXMLUtils xmlUtils = org.openspcoop2.message.xml.MessageXMLUtils.DEFAULT;
			Document docXML = xmlUtils.newDocument(doc);
			Element elemXML = docXML.getDocumentElement();
			return XMLUtils.isTracciaEngine(elemXML);
		}catch(Exception e){
			/**System.out.println("NON e' un DOCUMENTO VALIDO: "+e.getMessage());*/
			return false;
		}
	}
	public static boolean isTraccia(Document docXML){
		try{
			Element elemXML = docXML.getDocumentElement();
			return XMLUtils.isTracciaEngine(elemXML);
		}catch(Exception e){
			/**System.out.println("NON e' un DOCUMENTO VALIDO: "+e.getMessage());*/
			return false;
		}
	}
	public static boolean isTraccia(Element elemXML){
		return isTracciaEngine(elemXML);
	}
	public static boolean isTraccia(Node nodeXml){
		return isTracciaEngine(nodeXml);
	}
	private static boolean isTracciaEngine(Node nodeXml){
		try{
			/**System.out.println("LOCAL["+Costanti.ROOT_LOCAL_NAME_DETTAGLIO_ECCEZIONE+"]vs["+elemXML.getLocalName()+"]  NAMESPACE["+Costanti.TARGET_NAMESPACE+"]vs["+elemXML.getNamespaceURI()+"]");*/
			return CostantiTracciamento.ROOT_LOCAL_NAME_TRACCIA.equals(nodeXml.getLocalName()) && 
					CostantiTracciamento.TARGET_NAMESPACE.equals(nodeXml.getNamespaceURI() ) ;
		}catch(Exception e){
			/** System.out.println("NON e' un DOCUMENTO VALIDO: "+e.getMessage()); */
			return false;
		}
	}
	
}