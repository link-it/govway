/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it). 
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



package org.openspcoop2.pdd.core;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.xml.soap.SOAPEnvelope;

import org.openspcoop2.core.config.CorrelazioneApplicativa;
import org.openspcoop2.core.config.CorrelazioneApplicativaElemento;
import org.openspcoop2.core.config.CorrelazioneApplicativaRisposta;
import org.openspcoop2.core.config.CorrelazioneApplicativaRispostaElemento;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.OpenSPCoop2RestJsonMessage;
import org.openspcoop2.message.OpenSPCoop2RestXmlMessage;
import org.openspcoop2.message.OpenSPCoop2SoapMessage;
import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.core.integrazione.HeaderIntegrazione;
import org.openspcoop2.pdd.core.transazioni.Transaction;
import org.openspcoop2.protocol.engine.Configurazione;
import org.openspcoop2.protocol.engine.URLProtocolContext;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.constants.CodiceErroreIntegrazione;
import org.openspcoop2.protocol.sdk.constants.ErroreIntegrazione;
import org.openspcoop2.protocol.sdk.constants.ErroriIntegrazione;
import org.openspcoop2.protocol.sdk.state.IState;
import org.openspcoop2.protocol.sdk.state.StateMessage;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.regexp.RegularExpressionEngine;
import org.openspcoop2.utils.sql.ISQLQueryObject;
import org.openspcoop2.utils.sql.SQLObjectFactory;
import org.openspcoop2.utils.xml.AbstractXPathExpressionEngine;
import org.openspcoop2.utils.xml2json.JsonXmlPathExpressionEngine;
import org.slf4j.Logger;
import org.w3c.dom.Comment;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

/**
 * Gestione della correlazione applicativa con gli id
 *
 * @author Andrea Poli <apoli@link.it>
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class GestoreCorrelazioneApplicativa {

	/** Logger utilizzato per debug. */
	private Logger log = null;


	/* ********  F I E L D S    S T A T I C    P U B L I C  ******** */
	/** Tabella che mantiene i messaggi da consegnare ai servizi applicativi*/
	public static final String CORRELAZIONE_APPLICATIVA  = "CORRELAZIONE_APPLICATIVA";

	/* Colonne della tabella */
	public static final String CORRELAZIONE_APPLICATIVA_COLUMN_ID_APPLICATIVO  = "ID_APPLICATIVO";
	
	/** Se IState e' un'istanza di StatefulMessage possiede una Connessione SQL in autoCommit mode su cui effettuare query
	 *  Altrimenti, e' un'istanza di StatelessMessage e nn necessita di connessioni
	 * */
	private IState state;

	/** id precedentemente associato alla correlazione */
	private String idBustaCorrelato;
	/** id Correlazione Applicativo */
	private String idCorrelazione;
	/** errore */
	private ErroreIntegrazione errore;
	/** SoggettoFruitore */
	private IDSoggetto soggettoFruitore;
	/** Servizio */
	private IDServizio idServizio;
	/** Servizio Applicativo */
	private String servizioApplicativo;
	/** Scadenza correlazione applicativa */
	//private long scadenzaDefault; e' stata aggiunta l'ora di registrazione per differenziare la gestione tra quelle con scadenza e quelle senza.
	/** Indicazione se deve essere effettuato riuso id */
	private boolean riusoIdentificativo = false;
	/** ProtocolFactory */
	private IProtocolFactory<?> protocolFactory = null;
	/** Transaction */
	private Transaction transaction;
	
	private int maxLengthCorrelazioneApplicativa = 255;

	public boolean isRiusoIdentificativo() {
		return this.riusoIdentificativo;
	}
	
	/* ********  C O S T R U T T O R E  ******** */
	/**
	 * Costruttore. 
	 *
	 */
	public GestoreCorrelazioneApplicativa(IState state,Logger alog,IDSoggetto soggettoFruitore,IDServizio idServizio,
			String servizioApplicativo,IProtocolFactory<?> protocolFactory,
			Transaction transaction){
		this.state = state;
		if(alog!=null){
			this.log = alog;
		}else{
			this.log = LoggerWrapperFactory.getLogger(GestoreCorrelazioneApplicativa.class);
		}
		this.soggettoFruitore = soggettoFruitore;
		this.idServizio = idServizio;
		this.servizioApplicativo = servizioApplicativo;
		this.protocolFactory = protocolFactory;
		this.maxLengthCorrelazioneApplicativa = OpenSPCoop2Properties.getInstance().getMaxLengthCorrelazioneApplicativa();
		this.transaction = transaction;
	}
	/**
	 * Costruttore. 
	 *
	 */
	public GestoreCorrelazioneApplicativa(IState state,Logger alog,
			IProtocolFactory<?> protocolFactory,
			Transaction transaction){
		this(state,alog,null,null,null,protocolFactory,transaction);
	}


	
	
	
	
	
	/* *********** GESTIONE CORRELAZIONE APPLICATIVA (RICHIESTA) ************ */

	public boolean verificaCorrelazione(CorrelazioneApplicativa correlazioneApplicativa,
			URLProtocolContext urlProtocolContext,OpenSPCoop2Message message,HeaderIntegrazione headerIntegrazione,boolean readFirstHeaderIntegrazione) throws GestoreMessaggiException, ProtocolException{
		if(this.transaction!=null) {
			this.transaction.getTempiElaborazione().startCorrelazioneApplicativaRichiesta();
		}
		try {
			return this._verificaCorrelazione(correlazioneApplicativa, urlProtocolContext, message, headerIntegrazione, readFirstHeaderIntegrazione);
		}
		finally {
			if(this.transaction!=null) {
				this.transaction.getTempiElaborazione().endCorrelazioneApplicativaRichiesta();
			}
		}
	}
	private boolean _verificaCorrelazione(CorrelazioneApplicativa correlazioneApplicativa,
			URLProtocolContext urlProtocolContext,OpenSPCoop2Message message,HeaderIntegrazione headerIntegrazione,boolean readFirstHeaderIntegrazione) throws GestoreMessaggiException, ProtocolException{

		if(correlazioneApplicativa==null){
			this.errore = ErroriIntegrazione.ERRORE_416_CORRELAZIONE_APPLICATIVA_RICHIESTA_ERRORE.
					getErrore416_CorrelazioneApplicativaRichiesta("dati per l'identificazione dell'id di correlazione non presenti");
			throw new GestoreMessaggiException(this.errore.getDescrizione(this.protocolFactory));
		}

		Element element = null;
		String elementJson = null;
		try{
			if(ServiceBinding.SOAP.equals(message.getServiceBinding())){
				OpenSPCoop2SoapMessage soapMessage = message.castAsSoap();
				element = soapMessage.getSOAPPart().getEnvelope();
			}
			else{
				if(MessageType.XML.equals(message.getMessageType())){
					OpenSPCoop2RestXmlMessage xml = message.castAsRestXml();
					element = xml.getContent();	
				}
				else if(MessageType.JSON.equals(message.getMessageType())){
					OpenSPCoop2RestJsonMessage json = message.castAsRestJson();
					elementJson = json.getContent();
				}
			}
		}catch(Exception e){
			throw new GestoreMessaggiException(e.getMessage(),e);
		}
		
		/** Fase di identificazione dell'id di correlazione */
		boolean checkElementoInTransito = false;
		if(correlazioneApplicativa.sizeElementoList()>1){
			checkElementoInTransito = true;
		}
		else{
			if(correlazioneApplicativa.sizeElementoList()>0){
				CorrelazioneApplicativaElemento elemento = correlazioneApplicativa.getElemento(0);
				if( (elemento.getNome()!=null) && !"".equals(elemento.getNome()) ){
					checkElementoInTransito = true;
				}
			}
		}
		List<Node> nList = null;
		if(checkElementoInTransito){
			try{
				if(ServiceBinding.SOAP.equals(message.getServiceBinding())){
					SOAPEnvelope envelope = (SOAPEnvelope) element;
					if(envelope==null){
						throw new Exception("Envelope non presente nel messaggio Soap");
					}
					if(envelope.getBody()==null || envelope.getBody().hasChildNodes()==false){
						throw new Exception("Body applicativo non presente nel messaggio Soap");
					}
					NodeList nListSoapBody = envelope.getBody().getChildNodes();
					if(nListSoapBody==null || nListSoapBody.getLength()==0){
						throw new Exception("Elementi del Body non presenti?");
					}
					nList = new ArrayList<Node>();
					for (int elem=0; elem<nListSoapBody.getLength(); elem++){
						Node nodeInEsame =  nListSoapBody.item(elem);
						if(nodeInEsame instanceof Text || nodeInEsame instanceof Comment){
							continue;
						}
						nList.add(nodeInEsame);
					}
				}
				else{
					if(MessageType.XML.equals(message.getMessageType())){
						// Nei GET il messaggio e' vuoto
//						if(element==null){
//							throw new Exception("Contenuto non presente nel messaggio");
//						}
						if(element!=null){
							nList = new ArrayList<Node>();
							nList.add(element);
						}
					}
					else{
						throw new GestoreMessaggiException("MessageType ["+message.getMessageType()+"] non supportato in presenza di specifici elementi");
					}
				}
			}catch(Exception e){
				this.errore = ErroriIntegrazione.ERRORE_416_CORRELAZIONE_APPLICATIVA_RICHIESTA_ERRORE.
						getErrore416_CorrelazioneApplicativaRichiesta("errore durante l'analisi dell'elementName: "+e.getMessage());
				throw new GestoreMessaggiException(this.errore.getDescrizione(this.protocolFactory),e);
			}
		}

		// XPathExpressionEngine
		AbstractXPathExpressionEngine xPathEngine = new org.openspcoop2.message.xml.XPathExpressionEngine();
		
		/** Gestioni correlazioni, in modo da avere lo '*' in fondo */
		java.util.List<CorrelazioneApplicativaElemento> c = new java.util.ArrayList<CorrelazioneApplicativaElemento>();
		int posizioneElementoQualsiasi = -1;
		for(int i=0; i<correlazioneApplicativa.sizeElementoList(); i++){
			CorrelazioneApplicativaElemento elemento = correlazioneApplicativa.getElemento(i);
			if( (elemento.getNome()==null) || "".equals(elemento.getNome()) ){
				if(posizioneElementoQualsiasi!=-1){
					this.errore = ErroriIntegrazione.ERRORE_416_CORRELAZIONE_APPLICATIVA_RICHIESTA_ERRORE.
							getErrore416_CorrelazioneApplicativaRichiesta("errore durante l'analisi dell'elementName: piu' di un elemento '*' non puo' essere definito");
					throw new GestoreMessaggiException(this.errore.getDescrizione(this.protocolFactory));
				}else{
					posizioneElementoQualsiasi = i;
				}
			}else{
				c.add(elemento);
			}
		}
		if(posizioneElementoQualsiasi>=0){
			// deve essere analizzato per ultimo
			c.add(correlazioneApplicativa.getElemento(posizioneElementoQualsiasi));
		}
		

		/** Fase di identificazione dell'id di correlazione */
		boolean findCorrelazione = false;
		boolean correlazioneNonRiuscitaDaAccettare = false;
		String idCorrelazioneApplicativa = null;
		
		// Calcolo posizione ultimo nodo "buono"
		int posizioneUltimoNodo = -1;
		if(checkElementoInTransito && nList!=null){
			for (int elem=0; elem<nList.size(); elem++){
				Node nodeInEsame =  nList.get(elem);
				if(nodeInEsame instanceof Text || nodeInEsame instanceof Comment){
					continue;
				}
				posizioneUltimoNodo = elem;
			}
		}
		else{
			posizioneUltimoNodo = 0;
		}
		
		// Calcolo nomi
		List<String> nomiPresentiBody = new ArrayList<String>();
		if(checkElementoInTransito && nList!=null){
			for (int elem=0; elem<nList.size(); elem++){
				String elementName = null;
				Node nodeInEsame =  nList.get(elem);
				if(nodeInEsame instanceof Text || nodeInEsame instanceof Comment){
					continue;
				}
				try{
					elementName = nodeInEsame.getLocalName();
				}catch(Exception e){
					this.errore = ErroriIntegrazione.ERRORE_416_CORRELAZIONE_APPLICATIVA_RICHIESTA_ERRORE.
							getErrore416_CorrelazioneApplicativaRichiesta("errore durante l'analisi dell'elementName: "+e.getMessage());
					throw new GestoreMessaggiException(this.errore.getDescrizione(this.protocolFactory),e);
				}
				if(elementName==null){
					continue;
				}
				nomiPresentiBody.add(elementName);
			}
		}
		else{
			nomiPresentiBody.add("PresenteSoloRegola*");
		}
		
		for (int elem=0; elem<nomiPresentiBody.size(); elem++){

			String elementName = nomiPresentiBody.get(elem);
			
			if(findCorrelazione)
				break;
			
			for(int i=0; i<c.size(); i++){
				
				CorrelazioneApplicativaElemento elemento = c.get(i);
				
				boolean bloccaIdentificazioneNonRiuscita = true;
				if(CostantiConfigurazione.ACCETTA.equals(elemento.getIdentificazioneFallita()))
					bloccaIdentificazioneNonRiuscita = false;
				
				// Il nome dell'elemento di una correlazione applicativa puo' essere:
				// non definito: significa per qualsiasi XML
				// nome: nome dell'elemento radice dell'xml
				// xpath expression: per identificare il nome dell'elemento radice XML
				boolean matchNodePerCorrelazioneApplicativa = false;
				String nomeElemento = null;
				if( (elemento.getNome()==null) || "".equals(elemento.getNome()) ){
					matchNodePerCorrelazioneApplicativa = true;
					nomeElemento = "*";
					
					// Devo applicare lo '*' solo se ho prima guardato tutti i nodi radice e sono ad esaminare l'ultimo nodo radice.
					// L'ordine delle correlazioni mi garantira' che lo '*' sara' esaminato per ultimo
					if( (elem==posizioneUltimoNodo) == false)
						continue;
					
				}
				else if(this.idServizio!=null && this.idServizio.getAzione()!=null &&
						this.idServizio.getAzione().equals(elemento.getNome())) {
					// Ricerco per match sull'azione
					matchNodePerCorrelazioneApplicativa = true;
					nomeElemento = elemento.getNome();
				}
				else if( elementName.equals(elemento.getNome()) ){
					// Ricerco per match sul localName dell'elemento (se XML)
					matchNodePerCorrelazioneApplicativa = true;
					nomeElemento = elemento.getNome();
				}
				else{
					// Ricerco elemento con una espressione
					try{
						if(element==null && elementJson==null){
							throw new Exception("Contenuto non disponibile su cui effettuare un match");
						}
						if(element!=null) {
							nomeElemento = AbstractXPathExpressionEngine.extractAndConvertResultAsString(element, xPathEngine, elemento.getNome(), this.log);
						}
						else {
							nomeElemento = JsonXmlPathExpressionEngine.extractAndConvertResultAsString(elementJson, elemento.getNome(), this.log);
						}
						if(nomeElemento!=null) {
							matchNodePerCorrelazioneApplicativa = true;
						}
					}catch(Exception e){
						this.log.info("Calcolo (contentBased) non riuscito ["+elementName+"] ["+elemento.getNome()+"]: "+e.getMessage());
					}
					// Ricerco tramuite espressione regolare sulla url
					if(!matchNodePerCorrelazioneApplicativa) {
						try{
							nomeElemento = RegularExpressionEngine.getStringMatchPattern(urlProtocolContext.getUrlInvocazione_formBased(), 
									elemento.getNome());
							if(nomeElemento!=null) {
								matchNodePerCorrelazioneApplicativa = true;
							}
						}catch(Exception e){
							this.log.info("Calcolo (urlBased) non riuscito ["+elementName+"] ["+elemento.getNome()+"]: "+e.getMessage());
						}
					}
				}


				// Correlazione
				this.riusoIdentificativo = CostantiConfigurazione.ABILITATO.equals(elemento.getRiusoIdentificativo());
				if( matchNodePerCorrelazioneApplicativa ){

					// Puo' darsi che questo elemento identificato non abbia bisogna effettuare correlazione applicativa
					if(CostantiConfigurazione.CORRELAZIONE_APPLICATIVA_RICHIESTA_DISABILITATO.equals(elemento.getIdentificazione())){
						correlazioneNonRiuscitaDaAccettare = true;
						findCorrelazione = true;
						break;
					}
					
					
					if(readFirstHeaderIntegrazione){
						if(headerIntegrazione.getIdApplicativo()!=null){
							idCorrelazioneApplicativa = headerIntegrazione.getIdApplicativo();
							findCorrelazione = true;
							break;
						}
					}

					if( CostantiConfigurazione.CORRELAZIONE_APPLICATIVA_RICHIESTA_URL_BASED.equals(elemento.getIdentificazione())){
						try{
							List<String> l = RegularExpressionEngine.getAllStringMatchPattern(urlProtocolContext.getUrlInvocazione_formBased(), 
									elemento.getPattern());
							if(l!=null && l.size()>0) {
								StringBuffer bf = new StringBuffer();
								for (String id : l) {
									if(bf.length()>0) {
										bf.append(" ");
									}
									bf.append(id);
								}
								idCorrelazioneApplicativa =  bf.toString();
							}
						}catch(Exception e){
							if(bloccaIdentificazioneNonRiuscita){
								this.errore = ErroriIntegrazione.ERRORE_416_CORRELAZIONE_APPLICATIVA_RICHIESTA_ERRORE.
										getErrore416_CorrelazioneApplicativaRichiesta("identificativo di correlazione applicativa non identificato nell'elemento ["+nomeElemento+"] con modalita' di acquisizione urlBased (Pattern:"+elemento.getPattern()+"): "+e.getMessage());
								throw new GestoreMessaggiException(this.errore.getDescrizione(this.protocolFactory),e);
							}else{
								correlazioneNonRiuscitaDaAccettare = true;
							}
						}
					}
					else if( CostantiConfigurazione.CORRELAZIONE_APPLICATIVA_RICHIESTA_HEADER_BASED.equals(elemento.getIdentificazione())){
						try{
							if(message==null ||
									message.getTransportRequestContext()==null || 
									message.getTransportRequestContext().getParametersTrasporto()==null ||
									message.getTransportRequestContext().getParametersTrasporto().isEmpty()) {
								throw new Exception ("Header trasporto non disponibile");
							}
							idCorrelazioneApplicativa = message.getTransportRequestContext().getParameterTrasporto(elemento.getPattern());
						}catch(Exception e){
							if(bloccaIdentificazioneNonRiuscita){
								this.errore = ErroriIntegrazione.ERRORE_416_CORRELAZIONE_APPLICATIVA_RICHIESTA_ERRORE.
										getErrore416_CorrelazioneApplicativaRichiesta("identificativo di correlazione applicativa non identificato nell'elemento ["+nomeElemento+"] con modalita' di acquisizione headerBased (Header:"+elemento.getPattern()+"): "+e.getMessage());
								throw new GestoreMessaggiException(this.errore.getDescrizione(this.protocolFactory),e);
							}else{
								correlazioneNonRiuscitaDaAccettare = true;
							}
						}
					}
					else if( CostantiConfigurazione.CORRELAZIONE_APPLICATIVA_RICHIESTA_INPUT_BASED.equals(elemento.getIdentificazione()) ){
						idCorrelazioneApplicativa = headerIntegrazione.getIdApplicativo();
						if(idCorrelazioneApplicativa==null){
							if(bloccaIdentificazioneNonRiuscita){
								this.errore = ErroriIntegrazione.ERRORE_416_CORRELAZIONE_APPLICATIVA_RICHIESTA_ERRORE.
										getErrore416_CorrelazioneApplicativaRichiesta("identificativo di correlazione applicativa per l'elemento ["+nomeElemento+"] " +
												"con modalita' di acquisizione inputBased non presente tra le informazioni di integrazione");
								throw new GestoreMessaggiException(this.errore.getDescrizione(this.protocolFactory));
							}else{
								correlazioneNonRiuscitaDaAccettare = true;
							}
						}
					}
					else{
						try{
							if(ServiceBinding.REST.equals(message.getServiceBinding()) && !MessageType.XML.equals(message.getMessageType())
									&& !MessageType.JSON.equals(message.getMessageType())){
								throw new Exception("MessageType ["+message.getMessageType()+"] non supportato con correlazione di tipo '"+
										CostantiConfigurazione.CORRELAZIONE_APPLICATIVA_RICHIESTA_CONTENT_BASED.toString()+"'");
							}
							if(element==null && elementJson==null){
								throw new Exception("Contenuto non disponibile su cui effettuare una correlazione di tipo '"+
										CostantiConfigurazione.CORRELAZIONE_APPLICATIVA_RICHIESTA_CONTENT_BASED.toString()+"'");
							}
							if(element!=null) {
								idCorrelazioneApplicativa = AbstractXPathExpressionEngine.extractAndConvertResultAsString(element, xPathEngine, elemento.getPattern(), this.log);
							}
							else {
								idCorrelazioneApplicativa = JsonXmlPathExpressionEngine.extractAndConvertResultAsString(elementJson, elemento.getPattern(), this.log);
							}
														
						}catch(Exception e){
							if(bloccaIdentificazioneNonRiuscita){
								this.errore = ErroriIntegrazione.ERRORE_416_CORRELAZIONE_APPLICATIVA_RICHIESTA_ERRORE.
										getErrore416_CorrelazioneApplicativaRichiesta("identificativo di correlazione applicativa non identificato nell'elemento ["+nomeElemento+"] con modalita' di acquisizione contentBased (Pattern:"+elemento.getPattern()+"): "+e.getMessage());
								throw new GestoreMessaggiException(this.errore.getDescrizione(this.protocolFactory),e);
							}else{
								correlazioneNonRiuscitaDaAccettare = true;
								this.log.info("[AccettaIdentificazioneFallita] Identificativo di correlazione applicativa non identificato nell'elemento ["+nomeElemento+"] con modalita' di acquisizione contentBased (Pattern:"+elemento.getPattern()+"): "+e.getMessage());
							}
						}
					}
					
					
					if(idCorrelazioneApplicativa!=null && idCorrelazioneApplicativa.length()>this.maxLengthCorrelazioneApplicativa) {
						if(bloccaIdentificazioneNonRiuscita) {
							this.errore = ErroriIntegrazione.ERRORE_416_CORRELAZIONE_APPLICATIVA_RICHIESTA_ERRORE.
									getErrore416_CorrelazioneApplicativaRichiesta("Identificativo di correlazione applicativa identificato possiede una lunghezza ("+idCorrelazioneApplicativa.length()+") superiore ai 255 caratteri consentiti");
							throw new GestoreMessaggiException(this.errore.getDescrizione(this.protocolFactory));
						}
						else {
							this.log.error("Identificativo di correlazione applicativa identificato possiede una lunghezza ("+idCorrelazioneApplicativa.length()+") superiore ai 255 caratteri consentiti");
							correlazioneNonRiuscitaDaAccettare = true;
							idCorrelazioneApplicativa = null;
						}
					}
					
					findCorrelazione = true;
					break;
				}
			}
		}
		
		if(idCorrelazioneApplicativa == null){
			if(correlazioneNonRiuscitaDaAccettare==false){
				this.errore = ErroriIntegrazione.ERRORE_416_CORRELAZIONE_APPLICATIVA_RICHIESTA_ERRORE.
						getErrore416_CorrelazioneApplicativaRichiesta("Identificativo di correlazione applicativa non identificato; nessun elemento tra quelli di correlazione definiti, sono presenti nel body");
				throw new GestoreMessaggiException(this.errore.getDescrizione(this.protocolFactory));
			}else{
				return false;
			}
		}else{			
			this.idCorrelazione = idCorrelazioneApplicativa;
		}

		/** Fase di verifica dell'id di correlazione con l'id */
		if(this.riusoIdentificativo){
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			try{
				StateMessage stateMSG = (StateMessage)this.state;
				Connection connectionDB = stateMSG.getConnectionDB();
	
				// Lettura attuale valore
				
				// Azione
				String valoreAzione = "(AZIONE is null)";
				if( (this.idServizio.getAzione()!=null) && ("".equals(this.idServizio.getAzione())==false) ){
					valoreAzione = "AZIONE=?";
				}
				
				StringBuffer query = new StringBuffer();
				query.append("SELECT * FROM "+GestoreCorrelazioneApplicativa.CORRELAZIONE_APPLICATIVA+" WHERE ID_APPLICATIVO=? AND SERVIZIO_APPLICATIVO=? AND"+
						" TIPO_MITTENTE=? AND MITTENTE=? AND TIPO_DESTINATARIO=? AND DESTINATARIO=? AND TIPO_SERVIZIO=? AND SERVIZIO=? AND VERSIONE_SERVIZIO=? AND "+valoreAzione);
				pstmt = connectionDB.prepareStatement(query.toString());
				int index = 1;
				pstmt.setString(index++,idCorrelazioneApplicativa);
				pstmt.setString(index++,this.servizioApplicativo);
				pstmt.setString(index++,this.soggettoFruitore.getTipo());
				pstmt.setString(index++,this.soggettoFruitore.getNome());
				pstmt.setString(index++,this.idServizio.getSoggettoErogatore().getTipo());
				pstmt.setString(index++,this.idServizio.getSoggettoErogatore().getNome());
				pstmt.setString(index++,this.idServizio.getTipo());
				pstmt.setString(index++,this.idServizio.getNome());
				pstmt.setInt(index++,this.idServizio.getVersione());
				
				if( (this.idServizio.getAzione()!=null) && ("".equals(this.idServizio.getAzione())==false) ){
					pstmt.setString(index++,this.idServizio.getAzione());
				}
				rs = pstmt.executeQuery();
				if(rs == null) {
					pstmt.close();
					this.log.error("Verifica correlazione IDApplicativo - ID non riuscita: ResultSet is null?");
					throw new GestoreMessaggiException("Verifica correlazione IDApplicativo - ID non riuscita: ResultSet is null?");		
				}
				boolean correlazionePresente = rs.next();
				if(correlazionePresente){
					this.idBustaCorrelato = rs.getString("ID_MESSAGGIO");
				}		
				rs.close();
				pstmt.close();
	
			} catch(Exception er) {
				this.errore = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
						get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_529_CORRELAZIONE_APPLICATIVA_RICHIESTA_NON_RIUSCITA);
				this.log.error("Verifica correlazione IDApplicativo - ID non riuscita: "+er.getMessage());
				throw new GestoreMessaggiException("Verifica correlazione IDApplicativo - ID non riuscita: "+er.getMessage(),er);
			}
	
			if(this.idBustaCorrelato!=null)
				return true;
			else
				return false;
		}else{
			return false;
		}

	}

	

	
	
	
	
	/* *********** GESTIONE CORRELAZIONE APPLICATIVA RISPOSTA (RISPOSTA) ************ */
	
	public void verificaCorrelazioneRisposta(CorrelazioneApplicativaRisposta correlazioneApplicativa,
			OpenSPCoop2Message message,HeaderIntegrazione headerIntegrazione,boolean readFirstHeaderIntegrazione) throws GestoreMessaggiException, ProtocolException{
		if(this.transaction!=null) {
			this.transaction.getTempiElaborazione().startCorrelazioneApplicativaRisposta();
		}
		try {
			this._verificaCorrelazioneRisposta(correlazioneApplicativa, message, headerIntegrazione, readFirstHeaderIntegrazione);
		}
		finally {
			if(this.transaction!=null) {
				this.transaction.getTempiElaborazione().endCorrelazioneApplicativaRisposta();
			}
		}
	}
	private void _verificaCorrelazioneRisposta(CorrelazioneApplicativaRisposta correlazioneApplicativa,
			OpenSPCoop2Message message,HeaderIntegrazione headerIntegrazione,boolean readFirstHeaderIntegrazione) throws GestoreMessaggiException, ProtocolException{

		if(correlazioneApplicativa==null){
			this.errore = ErroriIntegrazione.ERRORE_434_CORRELAZIONE_APPLICATIVA_RISPOSTA_ERRORE.
					getErrore434_CorrelazioneApplicativaRisposta("dati per l'identificazione dell'id di correlazione non presenti");
			throw new GestoreMessaggiException(this.errore.getDescrizione(this.protocolFactory));
		}

		Element element = null;
		String elementJson = null;
		try{
			if(ServiceBinding.SOAP.equals(message.getServiceBinding())){
				OpenSPCoop2SoapMessage soapMessage = message.castAsSoap();
				element = soapMessage.getSOAPPart().getEnvelope();
			}
			else{
				if(MessageType.XML.equals(message.getMessageType())){
					OpenSPCoop2RestXmlMessage xml = message.castAsRestXml();
					element = xml.getContent();	
				}
				else if(MessageType.JSON.equals(message.getMessageType())){
					OpenSPCoop2RestJsonMessage json = message.castAsRestJson();
					elementJson = json.getContent();
				}
			}
		}catch(Exception e){
			throw new GestoreMessaggiException(e.getMessage(),e);
		}
		
		// XPathExpressionEngine
		AbstractXPathExpressionEngine xPathEngine = new org.openspcoop2.message.xml.XPathExpressionEngine();
		
		/** Fase di identificazione dell'id di correlazione */
		boolean checkElementiInTransito = false;
		if(correlazioneApplicativa.sizeElementoList()>1){
			checkElementiInTransito = true;
		}
		else{
			if(correlazioneApplicativa.sizeElementoList()>0){
				CorrelazioneApplicativaRispostaElemento elemento = correlazioneApplicativa.getElemento(0);
				if( (elemento.getNome()!=null) && !"".equals(elemento.getNome()) ){
					checkElementiInTransito = true;
				}
			}
		}
		List<Node> nList = null;
		if(checkElementiInTransito){
			try{
				if(ServiceBinding.SOAP.equals(message.getServiceBinding())){
					SOAPEnvelope envelope = (SOAPEnvelope) element;
					if(envelope==null){
						throw new Exception("Envelope non presente nel messaggio Soap");
					}
					if(envelope.getBody()==null || envelope.getBody().hasChildNodes()==false){
						throw new Exception("Body applicativo non presente nel messaggio Soap");
					}
					NodeList nListSoapBody = envelope.getBody().getChildNodes();
					if(nListSoapBody==null || nListSoapBody.getLength()==0){
						throw new Exception("Elementi del Body non presenti?");
					}
					nList = new ArrayList<Node>();
					for (int elem=0; elem<nListSoapBody.getLength(); elem++){
						Node nodeInEsame =  nListSoapBody.item(elem);
						if(nodeInEsame instanceof Text || nodeInEsame instanceof Comment){
							continue;
						}
						nList.add(nodeInEsame);
					}
				}
				else{
					if(MessageType.XML.equals(message.getMessageType())){
						// il body http puo' essere vuoto
//						if(element==null){
//							throw new Exception("Contenuto non presente nel messaggio");
//						}
						if(element!=null) {
							nList = new ArrayList<Node>();
							nList.add(element);
						}
					}
					else{
						throw new GestoreMessaggiException("MessageType ["+message.getMessageType()+"] non supportato in presenza di specifici elementi");
					}
				}
			}catch(Exception e){
				this.errore = ErroriIntegrazione.ERRORE_434_CORRELAZIONE_APPLICATIVA_RISPOSTA_ERRORE.
						getErrore434_CorrelazioneApplicativaRisposta("errore durante l'analisi dell'elementName: "+e.getMessage());
				throw new GestoreMessaggiException(this.errore.getDescrizione(this.protocolFactory),e);
			}
		}

		
		/** Gestioni correlazioni, in modo da avere lo '*' in fondo */
		java.util.List<CorrelazioneApplicativaRispostaElemento> c = new java.util.ArrayList<CorrelazioneApplicativaRispostaElemento>();
		int posizioneElementoQualsiasi = -1;
		for(int i=0; i<correlazioneApplicativa.sizeElementoList(); i++){
			CorrelazioneApplicativaRispostaElemento elemento = correlazioneApplicativa.getElemento(i);
			if( (elemento.getNome()==null) || "".equals(elemento.getNome()) ){
				if(posizioneElementoQualsiasi!=-1){
					this.errore = ErroriIntegrazione.ERRORE_434_CORRELAZIONE_APPLICATIVA_RISPOSTA_ERRORE.
							getErrore434_CorrelazioneApplicativaRisposta("errore durante l'analisi dell'elementName: piu' di un elemento '*' non puo' essere definito");
					throw new GestoreMessaggiException(this.errore.getDescrizione(this.protocolFactory));
				}else{
					posizioneElementoQualsiasi = i;
				}
			}else{
				c.add(elemento);
			}
		}
		if(posizioneElementoQualsiasi>=0){
			// deve essere analizzato per ultimo
			c.add(correlazioneApplicativa.getElemento(posizioneElementoQualsiasi));
		}
		

		/** Fase di identificazione dell'id di correlazione */
		boolean findCorrelazione = false;
		boolean correlazioneNonRiuscitaDaAccettare = false;
		String idCorrelazioneApplicativa = null;
		
		// Calcolo posizione ultimo nodo "buono"
		int posizioneUltimoNodo = -1;
		if(checkElementiInTransito && nList!=null){
			for (int elem=0; elem<nList.size(); elem++){
				Node nodeInEsame =  nList.get(elem);
				if(nodeInEsame instanceof Text || nodeInEsame instanceof Comment){
					continue;
				}
				posizioneUltimoNodo = elem;
			}
		}
		else{
			posizioneUltimoNodo = 0;
		}
		
		// Calcolo nomi
		List<String> nomiPresentiBody = new ArrayList<String>();
		if(checkElementiInTransito && nList!=null){
			for (int elem=0; elem<nList.size(); elem++){
				String elementName = null;
				Node nodeInEsame =  nList.get(elem);
				if(nodeInEsame instanceof Text || nodeInEsame instanceof Comment){
					continue;
				}
				try{
					elementName = nodeInEsame.getLocalName();
				}catch(Exception e){
					this.errore = ErroriIntegrazione.ERRORE_434_CORRELAZIONE_APPLICATIVA_RISPOSTA_ERRORE.
							getErrore434_CorrelazioneApplicativaRisposta("errore durante l'analisi dell'elementName: "+e.getMessage());
					throw new GestoreMessaggiException(this.errore.getDescrizione(this.protocolFactory),e);
				}
				if(elementName==null){
					continue;
				}
				nomiPresentiBody.add(elementName);
			}
		}
		else{
			nomiPresentiBody.add("PresenteSoloRegola*");
		}
		
		
		for (int elem=0; elem<nomiPresentiBody.size(); elem++){

			String elementName = nomiPresentiBody.get(elem);

			if(findCorrelazione)
				break;

			for(int i=0; i<c.size(); i++){
				
				CorrelazioneApplicativaRispostaElemento elemento = c.get(i);
				
				boolean bloccaIdentificazioneNonRiuscita = true;
				if(CostantiConfigurazione.ACCETTA.equals(elemento.getIdentificazioneFallita()))
					bloccaIdentificazioneNonRiuscita = false;
				
				// Il nome dell'elemento di una correlazione applicativa puo' essere:
				// non definito: significa per qualsiasi XML
				// nome: nome dell'elemento radice dell'xml
				// xpath expression: per identificare il nome dell'elemento radice XML
				boolean matchNodePerCorrelazioneApplicativa = false;
				String nomeElemento = null;
				if( (elemento.getNome()==null) || "".equals(elemento.getNome()) ){
					matchNodePerCorrelazioneApplicativa = true;
					nomeElemento = "*";
					
					// Devo applicare lo '*' solo se ho prima guardato tutti i nodi radice e sono ad esaminare l'ultimo nodo radice.
					// L'ordine delle correlazioni mi garantira' che lo '*' sara' esaminato per ultimo
					if( (elem==posizioneUltimoNodo) == false)
						continue;
					
				}
				else if(this.idServizio!=null && this.idServizio.getAzione()!=null &&
						this.idServizio.getAzione().equals(elemento.getNome())) {
					// Ricerco per match sull'azione
					matchNodePerCorrelazioneApplicativa = true;
					nomeElemento = elemento.getNome();
				}
				else if( elementName.equals(elemento.getNome()) ){
					// Ricerco per match sul localName dell'elemento (se XML)
					matchNodePerCorrelazioneApplicativa = true;
					nomeElemento = elemento.getNome();
				}
				else{
					// Ricerco elemento con una espressione che potrebbe essere riferita a tutta l'envelope
					try{
						if(element==null && elementJson==null){
							throw new Exception("Contenuto non disponibile su cui effettuare un match");
						}
						if(element!=null) {
							nomeElemento = AbstractXPathExpressionEngine.extractAndConvertResultAsString(element, xPathEngine, elemento.getNome(), this.log);
						}
						else {
							nomeElemento = JsonXmlPathExpressionEngine.extractAndConvertResultAsString(elementJson, elemento.getNome(), this.log);
						}
						if(nomeElemento!=null) {
							matchNodePerCorrelazioneApplicativa = true;
						}
					}catch(Exception e){
						this.log.info("Calcolo non riuscito ["+elementName+"] ["+elemento.getNome()+"]: "+e.getMessage());
					}
					// Ricerco tramuite espressione regolare sulla url
					// Non possibile sulla risposta
				}


				// Correlazione
				if( matchNodePerCorrelazioneApplicativa ){

					// Puo' darsi che questo elemento identificato non abbia bisogna effettuare correlazione applicativa
					if(CostantiConfigurazione.CORRELAZIONE_APPLICATIVA_RISPOSTA_DISABILITATO.equals(elemento.getIdentificazione())){
						correlazioneNonRiuscitaDaAccettare = true;
						findCorrelazione = true;
						break;
					}
					
					
					if(readFirstHeaderIntegrazione){
						if(headerIntegrazione.getIdApplicativo()!=null){
							idCorrelazioneApplicativa = headerIntegrazione.getIdApplicativo();
							findCorrelazione = true;
							break;
						}
					}

					if( CostantiConfigurazione.CORRELAZIONE_APPLICATIVA_RISPOSTA_HEADER_BASED.equals(elemento.getIdentificazione())){
						try{
							if(message==null ||
									message.getTransportResponseContext()==null || 
									message.getTransportResponseContext().getParametersTrasporto()==null ||
									message.getTransportResponseContext().getParametersTrasporto().isEmpty()) {
								throw new Exception ("Header trasporto non disponibile");
							}
							idCorrelazioneApplicativa = message.getTransportResponseContext().getParameterTrasporto(elemento.getPattern());
						}catch(Exception e){
							if(bloccaIdentificazioneNonRiuscita){
								this.errore = ErroriIntegrazione.ERRORE_434_CORRELAZIONE_APPLICATIVA_RISPOSTA_ERRORE.
										getErrore434_CorrelazioneApplicativaRisposta("identificativo di correlazione applicativa per l'elemento ["+nomeElemento+"] " +
												"con modalita' di acquisizione inputBased non presente tra le informazioni di integrazione");
								throw new GestoreMessaggiException(this.errore.getDescrizione(this.protocolFactory));
							}else{
								correlazioneNonRiuscitaDaAccettare = true;
							}
						}
					}
					else if( CostantiConfigurazione.CORRELAZIONE_APPLICATIVA_RISPOSTA_INPUT_BASED.equals(elemento.getIdentificazione()) ){
						idCorrelazioneApplicativa = headerIntegrazione.getIdApplicativo();
						if(idCorrelazioneApplicativa==null){
							if(bloccaIdentificazioneNonRiuscita){
								this.errore = ErroriIntegrazione.ERRORE_434_CORRELAZIONE_APPLICATIVA_RISPOSTA_ERRORE.
										getErrore434_CorrelazioneApplicativaRisposta("identificativo di correlazione applicativa per l'elemento ["+nomeElemento+"] " +
												"con modalita' di acquisizione inputBased non presente tra le informazioni di integrazione");
								throw new GestoreMessaggiException(this.errore.getDescrizione(this.protocolFactory));
							}else{
								correlazioneNonRiuscitaDaAccettare = true;
							}
						}
					}
					else{
						try{
							if(ServiceBinding.REST.equals(message.getServiceBinding()) && !MessageType.XML.equals(message.getMessageType())
									&& !MessageType.JSON.equals(message.getMessageType())){
								throw new Exception("MessageType ["+message.getMessageType()+"] non supportato con correlazione di tipo '"+
										CostantiConfigurazione.CORRELAZIONE_APPLICATIVA_RICHIESTA_CONTENT_BASED.toString()+"'");
							}
							if(element==null && elementJson==null){
								throw new Exception("Contenuto non disponibile su cui effettuare una correlazione di tipo '"+
										CostantiConfigurazione.CORRELAZIONE_APPLICATIVA_RICHIESTA_CONTENT_BASED.toString()+"'");
							}
							if(element!=null) {
								idCorrelazioneApplicativa = AbstractXPathExpressionEngine.extractAndConvertResultAsString(element, xPathEngine, elemento.getPattern(), this.log);
							}
							else {
								idCorrelazioneApplicativa = JsonXmlPathExpressionEngine.extractAndConvertResultAsString(elementJson, elemento.getPattern(), this.log);
							}
														
						}catch(Exception e){
							if(bloccaIdentificazioneNonRiuscita){
								this.errore = ErroriIntegrazione.ERRORE_434_CORRELAZIONE_APPLICATIVA_RISPOSTA_ERRORE.
										getErrore434_CorrelazioneApplicativaRisposta("Identificativo di correlazione applicativa non identificato nell'elemento ["+nomeElemento+"] con modalita' di acquisizione contentBased (Pattern:"+elemento.getPattern()+"): "+e.getMessage());
								throw new GestoreMessaggiException(this.errore.getDescrizione(this.protocolFactory),e);
							}else{
								correlazioneNonRiuscitaDaAccettare = true;
							}
						}
					}
					
					if(idCorrelazioneApplicativa!=null && idCorrelazioneApplicativa.length()>this.maxLengthCorrelazioneApplicativa) {
						if(bloccaIdentificazioneNonRiuscita) {
							this.errore = ErroriIntegrazione.ERRORE_434_CORRELAZIONE_APPLICATIVA_RISPOSTA_ERRORE.
									getErrore434_CorrelazioneApplicativaRisposta("Identificativo di correlazione applicativa identificato possiede una lunghezza ("+idCorrelazioneApplicativa.length()+") superiore ai 255 caratteri consentiti");
							throw new GestoreMessaggiException(this.errore.getDescrizione(this.protocolFactory));
						}
						else {
							this.log.error("Identificativo di correlazione applicativa identificato possiede una lunghezza ("+idCorrelazioneApplicativa.length()+") superiore ai 255 caratteri consentiti");
							correlazioneNonRiuscitaDaAccettare = true;
							idCorrelazioneApplicativa = null;
						}
					}
					
					findCorrelazione = true;
					break;
				}
			}
		}

		if(idCorrelazioneApplicativa == null){
			if(correlazioneNonRiuscitaDaAccettare==false){
				this.errore = ErroriIntegrazione.ERRORE_434_CORRELAZIONE_APPLICATIVA_RISPOSTA_ERRORE.
						getErrore434_CorrelazioneApplicativaRisposta("Identificativo di correlazione applicativa non identificato; nessun elemento tra quelli di correlazione definiti, sono presenti nel body");
				throw new GestoreMessaggiException(this.errore.getDescrizione(this.protocolFactory));
			}
		}else{
			this.idCorrelazione = idCorrelazioneApplicativa;
		}

	}
	
	

	
	
	
	/* *********** RIUSO DELL'ID ************ */
	
	
	/**
	 * Ritorna true se l'id passato come parametro viene correlato all'id applicativo.
	 * Se avviene un errore durante la ricerca della correlazione, viene lanciata una eccezione.
	 *
	 * @param correlazioneApplicativa Parametri di correlazione applicativa
	 * @param idApplicativo IDApplicativo da associare alla richiesta
	 * @param idBustaRequest Nuovo potenziale ID da associare alla richiesta
	 * @throws ProtocolException 
	 */
	public void applicaCorrelazione(CorrelazioneApplicativa correlazioneApplicativa,String idApplicativo,String idBustaRequest) throws GestoreMessaggiException, ProtocolException{

		if(correlazioneApplicativa==null){
			this.errore = ErroriIntegrazione.ERRORE_416_CORRELAZIONE_APPLICATIVA_RICHIESTA_ERRORE.
					getErrore416_CorrelazioneApplicativaRichiesta("dati per l'identificazione dell'id di correlazione non presenti");
			throw new GestoreMessaggiException(this.errore.getDescrizione(this.protocolFactory));
		}
		if(idBustaRequest==null){
			this.errore = ErroriIntegrazione.ERRORE_416_CORRELAZIONE_APPLICATIVA_RICHIESTA_ERRORE.
					getErrore416_CorrelazioneApplicativaRichiesta("identificativo non presente tra i parametri di invocazione");
			throw new GestoreMessaggiException(this.errore.getDescrizione(this.protocolFactory));
		}
		if(idApplicativo==null){
			this.errore = ErroriIntegrazione.ERRORE_416_CORRELAZIONE_APPLICATIVA_RICHIESTA_ERRORE.
					getErrore416_CorrelazioneApplicativaRichiesta("identificativo applicativo non presente tra i parametri di invocazione");
			throw new GestoreMessaggiException(this.errore.getDescrizione(this.protocolFactory));
		}

		Timestamp scadenzaCorrelazioneT = null;
		if(correlazioneApplicativa.getScadenza()!=null){
			try{
				long scadenza = Long.parseLong(correlazioneApplicativa.getScadenza());
				scadenzaCorrelazioneT = new Timestamp(DateManager.getTimeMillis()+(scadenza*60*1000));
			}catch(Exception e){
				this.errore = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
						get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_529_CORRELAZIONE_APPLICATIVA_RICHIESTA_NON_RIUSCITA);
				throw new GestoreMessaggiException("Scadenza impostata per la correlazione applicativa non corretta: "+e.getMessage(),e);
			}
		}
		// E' stata introdotta l'ora di registrazione per gestire le scadenze null
//		else{
//			scadenzaCorrelazioneT = new Timestamp(DateManager.getTimeMillis()+(this.scadenzaDefault*60*1000));
//		}

		/** Fase di verifica dell'id di correlazione con l'id */
		PreparedStatement pstmtInsert = null;
		try{
			StateMessage stateMSG = (StateMessage)this.state;
			Connection connectionDB = stateMSG.getConnectionDB();

			// nuova correlazione
			StringBuffer queryInsert = new StringBuffer();
			queryInsert.append("INSERT INTO "+GestoreCorrelazioneApplicativa.CORRELAZIONE_APPLICATIVA);
			queryInsert.append(" (ID_MESSAGGIO,ID_APPLICATIVO,SERVIZIO_APPLICATIVO,TIPO_MITTENTE,MITTENTE,TIPO_DESTINATARIO,DESTINATARIO,TIPO_SERVIZIO,SERVIZIO,VERSIONE_SERVIZIO,AZIONE ");
			if(scadenzaCorrelazioneT!=null){
				queryInsert.append(",SCADENZA");
			}
			queryInsert.append(") VALUES (?,?,?,?,?,?,?,?,?,?,?");
			if(scadenzaCorrelazioneT!=null){
				queryInsert.append(",?");
			}
			queryInsert.append(")");
			pstmtInsert = connectionDB.prepareStatement(queryInsert.toString());
			int index = 1;
			pstmtInsert.setString(index++,idBustaRequest);
			pstmtInsert.setString(index++,idApplicativo);
			pstmtInsert.setString(index++,this.servizioApplicativo);
			pstmtInsert.setString(index++,this.soggettoFruitore.getTipo());
			pstmtInsert.setString(index++,this.soggettoFruitore.getNome());
			pstmtInsert.setString(index++,this.idServizio.getSoggettoErogatore().getTipo());
			pstmtInsert.setString(index++,this.idServizio.getSoggettoErogatore().getNome());
			pstmtInsert.setString(index++,this.idServizio.getTipo());
			pstmtInsert.setString(index++,this.idServizio.getNome());
			pstmtInsert.setInt(index++,this.idServizio.getVersione());
			pstmtInsert.setString(index++,this.idServizio.getAzione());
			if(scadenzaCorrelazioneT!=null){
				pstmtInsert.setTimestamp(index++,scadenzaCorrelazioneT);
			}

			//	Add PreparedStatement
			String valoreAzione = "N.D.";
			if( (this.idServizio.getAzione()!=null) && ("".equals(this.idServizio.getAzione())==false) ){
				valoreAzione = this.idServizio.getAzione();
			}
			stateMSG.getPreparedStatement().put("INSERT CorrelazioneApplicativa_"+idBustaRequest+"_"+idApplicativo+"_"+this.soggettoFruitore.getTipo()+this.soggettoFruitore.getNome()+
					"_"+this.idServizio.getSoggettoErogatore().getTipo()+this.idServizio.getSoggettoErogatore().getNome()+"_"+
					this.idServizio.getTipo()+this.idServizio.getNome()+":"+this.idServizio.getVersione()+"_"+valoreAzione,pstmtInsert);

		}catch(Exception er){
			this.errore = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
					get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_529_CORRELAZIONE_APPLICATIVA_RICHIESTA_NON_RIUSCITA);
			this.log.error("Correlazione IDApplicativo - ID non riuscita: "+er.getMessage());
			throw new GestoreMessaggiException("Correlazione IDApplicativo - ID non riuscita: "+er.getMessage(),er);
		}

	}



	/**
	 * Cerca nella tabella CORRELAZIONE_APPLICATIVA le correlazioni scadute 
	 *
	 * @return Nel caso l'operazione ha successo ritorna gli id delle tabelle delle correlazioni scadute
	 */
	public java.util.List<Long> getCorrelazioniScadute(int limit,boolean logQuery,boolean orderBy) throws GestoreMessaggiException{

		java.util.List<Long> idMsg = new java.util.ArrayList<Long>();

		PreparedStatement pstmtMsgScaduti = null;
		ResultSet rs = null;
		String queryString = null;
		try{	
			StateMessage stateMSG = (StateMessage)this.state;
			Connection connectionDB = stateMSG.getConnectionDB();

			// Query per Ricerca messaggi scaduti
			// Algoritmo:
			//    if( scaduto < now )
			//       msgScaduto
			java.sql.Timestamp nowT = DateManager.getTimestamp();


			if(Configurazione.getSqlQueryObjectType()==null){
				StringBuffer query = new StringBuffer();
				query.append("SELECT id FROM ");
				query.append(GestoreCorrelazioneApplicativa.CORRELAZIONE_APPLICATIVA);
				query.append(" WHERE SCADENZA is not null AND SCADENZA < ?");
				queryString = query.toString();
			}else{
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(Configurazione.getSqlQueryObjectType());	
				sqlQueryObject.addSelectField("id");
				sqlQueryObject.addSelectField("SCADENZA");
				sqlQueryObject.addFromTable(GestoreCorrelazioneApplicativa.CORRELAZIONE_APPLICATIVA);
				sqlQueryObject.addWhereCondition("SCADENZA is not null");
				sqlQueryObject.addWhereCondition("SCADENZA < ?");
				sqlQueryObject.setANDLogicOperator(true);
				if(orderBy){
					sqlQueryObject.addOrderBy("SCADENZA");
					sqlQueryObject.setSortType(true);
				}
				sqlQueryObject.setLimit(limit);
				queryString = sqlQueryObject.createSQLQuery();
			}
			//System.out.println("QUERY CORRELAZIONE APPLICATIVA IS: ["+queryString+"] 1["+nowT+"]");


			pstmtMsgScaduti = connectionDB.prepareStatement(queryString);
			pstmtMsgScaduti.setTimestamp(1,nowT);

			long startDateSQLCommand = DateManager.getTimeMillis();
			if(logQuery)
				this.log.debug("[QUERY] (CorrelazioneApplicativa.scaduta) ["+queryString+"] 1["+nowT+"]...");
			rs = pstmtMsgScaduti.executeQuery();
			long endDateSQLCommand = DateManager.getTimeMillis();
			long secondSQLCommand = (endDateSQLCommand - startDateSQLCommand) / 1000;
			if(logQuery)
				this.log.debug("[QUERY] (CorrelazioneApplicativa.scaduta) ["+queryString+"] 1["+nowT+"] effettuata in "+secondSQLCommand+" secondi");

			int countLimit = 0;
			while(rs.next()){
				if(Configurazione.getSqlQueryObjectType()==null){
					// LIMIT Applicativo
					idMsg.add(rs.getLong("id"));
					countLimit++;
					if(countLimit==limit)
						break;
				}
				else{
					idMsg.add(rs.getLong("id"));
				}
			}
			rs.close();
			pstmtMsgScaduti.close();

			return idMsg;

		} catch(Exception e) {
			String errorMsg = "[GestoreCorrelazioneApplicativa.getCorrelazioniScadute] errore, queryString["+queryString+"]: "+e.getMessage();
			try{
				if(rs != null)
					rs.close();
			} catch(Exception er) {}
			try{
				if(pstmtMsgScaduti != null)
					pstmtMsgScaduti.close();
			} catch(Exception er) {}
			this.log.error(errorMsg);
			throw new GestoreMessaggiException(errorMsg,e);
		}	
	}
	
	
	/**
	 * Cerca nella tabella CORRELAZIONE_APPLICATIVA le correlazioni 'vecchie' (rispetto all'ora di registrazione)
	 *
	 * @return Nel caso l'operazione ha successo ritorna gli id delle tabelle delle correlazioni 'vecchie' (rispetto all'ora di registrazione) 
	 */
	public java.util.List<Long> getCorrelazioniScaduteRispettoOraRegistrazione(int limit,long scadenzaMsg,boolean logQuery,boolean orderBy,boolean escludiCorrelazioniConScadenza) throws GestoreMessaggiException{

		java.util.List<Long> idMsg = new java.util.ArrayList<Long>();
		
		PreparedStatement pstmtMsgScaduti = null;
		ResultSet rs = null;
		String queryString = null;
		try{	
			StateMessage stateMSG = (StateMessage)this.state;
			Connection connectionDB = stateMSG.getConnectionDB();

			// Query per Ricerca messaggi scaduti
			// Algoritmo:
			//    if( (now-timeout) > oraRegistrazione )
			//       msgScaduto
			long scadenza = DateManager.getTimeMillis() - (scadenzaMsg * 60 * 1000);
			java.sql.Timestamp scandenzaT = new java.sql.Timestamp(scadenza);

			if(Configurazione.getSqlQueryObjectType()==null){
				StringBuffer query = new StringBuffer();
				query.append("SELECT id FROM ");
				query.append(GestoreCorrelazioneApplicativa.CORRELAZIONE_APPLICATIVA);
				query.append(" WHERE ORA_REGISTRAZIONE < ?");
				if(escludiCorrelazioniConScadenza){
					query.append(" AND SCADENZA is null");
				}
				queryString = query.toString();
			}else{
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(Configurazione.getSqlQueryObjectType());	
				sqlQueryObject.addSelectField("id");
				sqlQueryObject.addSelectField("SCADENZA");
				sqlQueryObject.addFromTable(GestoreCorrelazioneApplicativa.CORRELAZIONE_APPLICATIVA);
				sqlQueryObject.addWhereCondition("ORA_REGISTRAZIONE < ?");
				if(escludiCorrelazioniConScadenza){
					sqlQueryObject.addWhereCondition("SCADENZA is null");
				}
				sqlQueryObject.setANDLogicOperator(true);
				if(orderBy){
					sqlQueryObject.addOrderBy("ORA_REGISTRAZIONE");
					sqlQueryObject.setSortType(true);
				}
				sqlQueryObject.setLimit(limit);
				queryString = sqlQueryObject.createSQLQuery();
			}
			//System.out.println("QUERY CORRELAZIONE APPLICATIVA IS: ["+queryString+"] 1["+nowT+"]");


			pstmtMsgScaduti = connectionDB.prepareStatement(queryString);
			pstmtMsgScaduti.setTimestamp(1,scandenzaT);

			long startDateSQLCommand = DateManager.getTimeMillis();
			if(logQuery)
				this.log.debug("[QUERY] (CorrelazioneApplicativa.storiche) ["+queryString+"] 1["+scandenzaT+"]...");
			rs = pstmtMsgScaduti.executeQuery();
			long endDateSQLCommand = DateManager.getTimeMillis();
			long secondSQLCommand = (endDateSQLCommand - startDateSQLCommand) / 1000;
			if(logQuery)
				this.log.debug("[QUERY] (CorrelazioneApplicativa.storiche) ["+queryString+"] 1["+scandenzaT+"] effettuata in "+secondSQLCommand+" secondi");

			int countLimit = 0;
			while(rs.next()){
				if(Configurazione.getSqlQueryObjectType()==null){
					// LIMIT Applicativo
					idMsg.add(rs.getLong("id"));
					countLimit++;
					if(countLimit==limit)
						break;
				}
				else{
					idMsg.add(rs.getLong("id"));
				}
			}
			rs.close();
			pstmtMsgScaduti.close();

			return idMsg;

		} catch(Exception e) {
			String errorMsg = "[GestoreCorrelazioneApplicativa.getCorrelazioniStoriche] errore, queryString["+queryString+"]: "+e.getMessage();
			try{
				if(rs != null)
					rs.close();
			} catch(Exception er) {}
			try{
				if(pstmtMsgScaduti != null)
					pstmtMsgScaduti.close();
			} catch(Exception er) {}
			this.log.error(errorMsg);
			throw new GestoreMessaggiException(errorMsg,e);
		}	
	}

	
	/**
	 * Ritorna l'id e l'id di correlazione applicativa.
	 *
	 */
	public String[] getIDMappingCorrelazioneApplicativa(long idCorrelazioneApplicativa)throws GestoreMessaggiException{

		PreparedStatement pstmtReadMSG= null;
		ResultSet rs = null;
		try{

			StateMessage stateMSG = (StateMessage)this.state;
			Connection connectionDB = stateMSG.getConnectionDB();

			String query = "SELECT ID_MESSAGGIO,ID_APPLICATIVO  FROM "+GestoreCorrelazioneApplicativa.CORRELAZIONE_APPLICATIVA+" WHERE id=?";
			//log.debug("Query: "+query);
			pstmtReadMSG=connectionDB.prepareStatement(query);
			pstmtReadMSG.setLong(1,idCorrelazioneApplicativa);
			rs = pstmtReadMSG.executeQuery();
			if(rs.next()){
				String [] s = new String[2];
				s[0] = rs.getString("ID_MESSAGGIO");
				s[1] = rs.getString("ID_APPLICATIVO");
				rs.close();
				pstmtReadMSG.close();
				return s;
			}
			rs.close();
			pstmtReadMSG.close();
			throw new Exception("CorrelazioneApplicativa con id["+idCorrelazioneApplicativa+"] non trovata");
		}
		catch(Exception e) {
			String errorMsg = "GestoreCorrelazioneApplicativa, error getIDMappingCorrelazioneApplicativa ["+idCorrelazioneApplicativa+"] : "+e.getMessage();		
			try{
				if(rs != null)
					rs.close();
			} catch(Exception er) {}
			try{
				if(pstmtReadMSG != null)
					pstmtReadMSG.close();
			} catch(Exception er) {}
			this.log.error(errorMsg);
			throw new GestoreMessaggiException(errorMsg,e);
		}	

	}
	

	/**
	 * Elimina la correlazione applicativa gestito da OpenSPCoop. 
	 *
	 */
	public void deleteCorrelazioneApplicativa(long idCorrelazioneApplicativa)throws GestoreMessaggiException{

		PreparedStatement pstmtDeleteMSG= null;
		try{

			StateMessage stateMSG = (StateMessage)this.state;
			Connection connectionDB = stateMSG.getConnectionDB();

			String query = "DELETE FROM "+GestoreCorrelazioneApplicativa.CORRELAZIONE_APPLICATIVA+" WHERE id=?";
			//log.debug("Query: "+query);
			pstmtDeleteMSG=connectionDB.prepareStatement(query);
			pstmtDeleteMSG.setLong(1,idCorrelazioneApplicativa);
			pstmtDeleteMSG.execute();
			pstmtDeleteMSG.close();
		}
		catch(Exception e) {
			String errorMsg = "GestoreCorrelazioneApplicativa, error deleteCorrelazione ["+idCorrelazioneApplicativa+"] : "+e.getMessage();		
			try{
				if(pstmtDeleteMSG != null)
					pstmtDeleteMSG.close();
			} catch(Exception er) {}
			this.log.error(errorMsg);
			throw new GestoreMessaggiException(errorMsg,e);
		}	

	}

	/**
	 * id precedentemente correlato alla richiesta applicativa
	 * 
	 * @return id precedentemente correlato alla richiesta applicativa
	 */
	public String getIdBustaCorrelato() {
		return this.idBustaCorrelato;
	}
	public ErroreIntegrazione getErrore() {
		return this.errore;
	}


	public String getIdCorrelazione() {
		return this.idCorrelazione;
	}

}

