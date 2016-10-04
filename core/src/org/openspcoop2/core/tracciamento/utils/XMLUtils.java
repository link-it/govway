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
import org.openspcoop2.message.ValidatoreXSD;
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

	/** Validatore XSD */
	static ValidatoreXSD validatoreXSD = null;
	public static synchronized ValidatoreXSD getValidatoreXSD(Logger log) throws Exception{
		if(XMLUtils.validatoreXSD==null){
			XMLUtils.validatoreXSD = new ValidatoreXSD(log,XMLUtils.class.getResourceAsStream("/openspcoopTracciamento.xsd"));
		}
		return XMLUtils.validatoreXSD;
	}
	public static boolean validate(Traccia traccia,StringBuffer motivoErroreValidazione){
		
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
	private static void validate(Busta busta,StringBuffer motivoErroreValidazione){
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
	private static void validate(Soggetto soggetto,StringBuffer motivoErroreValidazione,String tipo){
		if(soggetto.getIdentificativo()==null){
			motivoErroreValidazione.append("Busta."+tipo+".identificativo non definita\n");
		}
		else{
			if(soggetto.getIdentificativo().getTipo()==null){
				motivoErroreValidazione.append("Busta."+tipo+".identificativo.tipo non definita\n");
			}
			if(soggetto.getIdentificativo().getBase()==null){
				motivoErroreValidazione.append("Busta."+tipo+".identificativo.base non definita\n");
			}
		}
	}
	private static void validate(ProfiloCollaborazione profilo,StringBuffer motivoErroreValidazione){
		if(profilo.getTipo()==null){
			motivoErroreValidazione.append("Busta.profiloCollaborazione.identificativo.tipo non definita\n");
		}
		if(profilo.getBase()==null){
			motivoErroreValidazione.append("Busta.profiloCollaborazione.identificativo.base non definita\n");
		}
	}
	private static void validate(Servizio servizio,StringBuffer motivoErroreValidazione,String tipo){
		if(servizio.getBase()==null){
			motivoErroreValidazione.append("Busta."+tipo+".base non definita\n");
		}
	}
	private static void validate(Data data,StringBuffer motivoErroreValidazione,String tipo){
		if(data.getSorgente()!=null){
			if(data.getSorgente().getBase()==null){
				motivoErroreValidazione.append("Busta."+tipo+".sorgente.base non definita\n");
			}
			if(data.getSorgente().getTipo()==null){
				motivoErroreValidazione.append("Busta."+tipo+".sorgente.tipo non definita\n");
			}
		}
	}
	private static void validate(ProfiloTrasmissione profiloTrasmissione,StringBuffer motivoErroreValidazione){
		if(profiloTrasmissione.getInoltro()!=null){
			if(profiloTrasmissione.getInoltro().getBase()==null){
				motivoErroreValidazione.append("Busta.profiloTrasmissione.base non definita\n");
			}
			if(profiloTrasmissione.getInoltro().getTipo()==null){
				motivoErroreValidazione.append("Busta.profiloTrasmissione.tipo non definita\n");
			}
		}
	}
	private static void validate(Trasmissioni trasmissioni,StringBuffer motivoErroreValidazione){
		for (int i = 0; i < trasmissioni.sizeTrasmissioneList(); i++) {
			Trasmissione tr = trasmissioni.getTrasmissione(i);
			if(tr==null){
				motivoErroreValidazione.append("Busta.tramissione["+i+"] non definita\n");
			}
			else{
				if(tr.getOrigine()!=null){
					validate(tr.getOrigine(), motivoErroreValidazione, "tramissione["+i+"].origine");
				}
				if(tr.getDestinazione()!=null){
					validate(tr.getDestinazione(), motivoErroreValidazione, "tramissione["+i+"].destinazione");
				}
				if(tr.getOraRegistrazione()!=null){
					validate(tr.getOraRegistrazione(), motivoErroreValidazione, "tramissione["+i+"].ora-registrazione");
				}
			}
		}
	}
	private static void validate(Riscontri riscontri,StringBuffer motivoErroreValidazione){
		for (int i = 0; i < riscontri.sizeRiscontroList(); i++) {
			Riscontro r = riscontri.getRiscontro(i);
			if(r==null){
				motivoErroreValidazione.append("Busta.riscontro["+i+"] non definita\n");
			}
			else{
				if(r.getOraRegistrazione()!=null){
					validate(r.getOraRegistrazione(), motivoErroreValidazione, "riscontro["+i+"].ora-registrazione");
				}
			}
		}
	}
	private static void validate(Eccezioni eccezioni,StringBuffer motivoErroreValidazione){
		for (int i = 0; i < eccezioni.sizeEccezioneList(); i++) {
			Eccezione e = eccezioni.getEccezione(i);
			if(e==null){
				motivoErroreValidazione.append("Busta.eccezione["+i+"] non definita\n");
			}
			else{
				if(e.getCodice()!=null){
					if(e.getCodice().getBase()==null){
						motivoErroreValidazione.append("Busta.eccezione["+i+"].codice.base non definita\n");
					}
					if(e.getCodice().getTipo()==null){
						motivoErroreValidazione.append("Busta.eccezione["+i+"].codice.tipo non definita\n");
					}
				}
				if(e.getContestoCodifica()!=null){
					if(e.getContestoCodifica().getBase()==null){
						motivoErroreValidazione.append("Busta.eccezione["+i+"].contesto-codifica.base non definita\n");
					}
					if(e.getContestoCodifica().getTipo()==null){
						motivoErroreValidazione.append("Busta.eccezione["+i+"].contesto-codifica.tipo non definita\n");
					}
				}
				if(e.getRilevanza()!=null){
					if(e.getRilevanza().getBase()==null){
						motivoErroreValidazione.append("Busta.eccezione["+i+"].rilevanza.base non definita\n");
					}
					if(e.getRilevanza().getTipo()==null){
						motivoErroreValidazione.append("Busta.eccezione["+i+"].rilevanza.tipo non definita\n");
					}
				}
			}
		}
	}
	private static void validate(Allegati allegati,StringBuffer motivoErroreValidazione){
		for (int i = 0; i < allegati.sizeAllegatoList(); i++) {
			Allegato a = allegati.getAllegato(i);
			if(a==null){
				motivoErroreValidazione.append("Busta.allegato["+i+"] non definita\n");
			}
		}
	}
	private static void validate(Protocollo protocollo,StringBuffer motivoErroreValidazione){
		if(protocollo.getIdentificativo()==null){
			motivoErroreValidazione.append("Busta.protocollo.identificativo non definito\n");
		}
		for (int i = 0; i < protocollo.sizeProprietaList(); i++) {
			Proprieta pp = protocollo.getProprieta(i);
			if(pp==null){
				motivoErroreValidazione.append("Busta.protocollo.proprieta["+i+"] non definito\n");
			}
			else{
				if(pp.getNome()==null){
					motivoErroreValidazione.append("Busta.protocollo.proprieta["+i+"].nome non definito\n");
				}
				if(pp.getValore()==null){
					motivoErroreValidazione.append("Busta.protocollo.proprieta["+i+"]["+pp.getNome()+"].valore non definito\n");
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
		ByteArrayInputStream bin = null;
		try{
			bin = new ByteArrayInputStream(m);
			return XMLUtils.getTraccia(log,bin);
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
	public static Traccia getTraccia(Logger log,File m) throws XMLUtilsException{
		FileInputStream fin = null;
		try{
			fin = new FileInputStream(m);
			return XMLUtils.getTraccia(log,fin);
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
			return (Traccia) org.openspcoop2.utils.xml.JiBXUtils.xmlToObj(binTrasformazione, Traccia.class);
			
		}catch(Exception e){
			throw new XMLUtilsException(e.getMessage(),e);
		}
	}

	
	
	
	
	/* ----- Marshall ----- */
	public static void generateTraccia(Traccia traccia,File out) throws XMLUtilsException{
		try{
			StringBuffer risultatoValidazione = new StringBuffer();
			if(XMLUtils.validate(traccia, risultatoValidazione)==false){
				throw new Exception(risultatoValidazione.toString());
			}
			org.openspcoop2.utils.xml.JiBXUtils.objToXml(out.getName(),XMLUtils.generateTraccia_engine(traccia));
		}catch(Exception e){
			throw new XMLUtilsException(e.getMessage(),e);
		}
	}
	
	public static void generateTraccia(Traccia traccia,String fileName) throws XMLUtilsException{
		try{
			StringBuffer risultatoValidazione = new StringBuffer();
			if(XMLUtils.validate(traccia, risultatoValidazione)==false){
				throw new Exception(risultatoValidazione.toString());
			}
			org.openspcoop2.utils.xml.JiBXUtils.objToXml(fileName,XMLUtils.generateTraccia_engine(traccia));
		}catch(Exception e){
			throw new XMLUtilsException(e.getMessage(),e);
		}
	}
	
	public static byte[] generateTraccia(Traccia traccia) throws XMLUtilsException{
		try{
			StringBuffer risultatoValidazione = new StringBuffer();
			if(XMLUtils.validate(traccia, risultatoValidazione)==false){
				throw new Exception(risultatoValidazione.toString());
			}
			return XMLUtils.generateTraccia_engine(traccia);
		}catch(Exception e){
			throw new XMLUtilsException(e.getMessage(),e);
		}
	}

	public static void generateTraccia(Traccia traccia,OutputStream out) throws XMLUtilsException{
		try{
			StringBuffer risultatoValidazione = new StringBuffer();
			if(XMLUtils.validate(traccia, risultatoValidazione)==false){
				throw new Exception(risultatoValidazione.toString());
			}
			out.write(XMLUtils.generateTraccia_engine(traccia));
			out.flush();
			out.close();
		}catch(Exception e){
			throw new XMLUtilsException(e.getMessage(),e);
		}
	}
	
	private static byte[] generateTraccia_engine(Traccia traccia) throws XMLUtilsException{
		try{
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			org.openspcoop2.utils.xml.JiBXUtils.objToXml(bout, Traccia.class, traccia);
			byte[] dichiarazione = bout.toByteArray();
			return dichiarazione;
		}catch(Exception e){
			throw new XMLUtilsException(e.getMessage(),e);
		}
	}
	
	
	
	
	public static boolean isTraccia(byte [] doc){
		try{
			org.openspcoop2.message.XMLUtils xmlUtils = org.openspcoop2.message.XMLUtils.getInstance();
			Document docXML = xmlUtils.newDocument(doc);
			Element elemXML = docXML.getDocumentElement();
			return XMLUtils.isTraccia_engine(elemXML);
		}catch(Exception e){
			//System.out.println("NON e' un DOCUMENTO VALIDO: "+e.getMessage());
			return false;
		}
	}
	public static boolean isTraccia(Document docXML){
		try{
			Element elemXML = docXML.getDocumentElement();
			return XMLUtils.isTraccia_engine(elemXML);
		}catch(Exception e){
			//System.out.println("NON e' un DOCUMENTO VALIDO: "+e.getMessage());
			return false;
		}
	}
	public static boolean isTraccia(Element elemXML){
		return isTraccia_engine(elemXML);
	}
	public static boolean isTraccia(Node nodeXml){
		return isTraccia_engine(nodeXml);
	}
	private static boolean isTraccia_engine(Node nodeXml){
		try{
			//System.out.println("LOCAL["+Costanti.ROOT_LOCAL_NAME_DETTAGLIO_ECCEZIONE+"]vs["+elemXML.getLocalName()+"]  NAMESPACE["+Costanti.TARGET_NAMESPACE+"]vs["+elemXML.getNamespaceURI()+"]");
			if(CostantiTracciamento.ROOT_LOCAL_NAME_TRACCIA.equals(nodeXml.getLocalName()) && 
					CostantiTracciamento.TARGET_NAMESPACE.equals(nodeXml.getNamespaceURI() ) 
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