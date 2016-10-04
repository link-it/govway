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



package org.openspcoop2.pdd.core;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.xml.soap.SOAPEnvelope;

import org.slf4j.Logger;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.config.CorrelazioneApplicativa;
import org.openspcoop2.core.config.CorrelazioneApplicativaElemento;
import org.openspcoop2.core.config.CorrelazioneApplicativaRisposta;
import org.openspcoop2.core.config.CorrelazioneApplicativaRispostaElemento;
import org.openspcoop2.message.DynamicNamespaceContextFactory;
import org.openspcoop2.pdd.core.integrazione.HeaderIntegrazione;
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
import org.openspcoop2.utils.xml.DynamicNamespaceContext;
import org.openspcoop2.utils.xml.AbstractXPathExpressionEngine;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.w3c.dom.Comment;

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
	private IProtocolFactory protocolFactory = null;

	public boolean isRiusoIdentificativo() {
		return this.riusoIdentificativo;
	}
	
	/* ********  C O S T R U T T O R E  ******** */
	/**
	 * Costruttore. 
	 *
	 */
	public GestoreCorrelazioneApplicativa(IState state,Logger alog,IDSoggetto soggettoFruitore,IDServizio idServizio,
			String servizioApplicativo,IProtocolFactory protocolFactory){
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
	}
	/**
	 * Costruttore. 
	 *
	 */
	public GestoreCorrelazioneApplicativa(IState state,Logger alog,IProtocolFactory protocolFactory){
		this(state,alog,null,null,null,protocolFactory);
	}


	
	
	
	
	
	/* *********** GESTIONE CORRELAZIONE APPLICATIVA (PORTA DELEGATA) ************ */


	/**
	 * Ritorna true se l'id passato come parametro e' gia' stato precedentemente associato all'id di correlazione identificato grazie ai pattern specificati.
	 * Se avviene un errore durante la ricerca della correlazione, viene lanciata una eccezione.
	 *
	 * @param correlazioneApplicativa Parametri di correlazione applicativa
	 * @param urlProtocolContext utile per identificazioni url-based.
	 * @param envelope ByteApplicativo utilizzato per l'invocazione della porta delegata (utile per identificazioni content-based)
	 * @param headerIntegrazione Header di Integrazione
	 * @return true se esiste una precedente correlazione, false altrimenti.
	 * @throws ProtocolException 
	 */
	public boolean verificaCorrelazione(CorrelazioneApplicativa correlazioneApplicativa,
			URLProtocolContext urlProtocolContext,SOAPEnvelope envelope,HeaderIntegrazione headerIntegrazione,boolean readFirstHeaderIntegrazione) throws GestoreMessaggiException, ProtocolException{

		if(correlazioneApplicativa==null){
			this.errore = ErroriIntegrazione.ERRORE_416_CORRELAZIONE_APPLICATIVA_RICHIESTA_ERRORE.
					getErrore416_CorrelazioneApplicativaRichiesta("dati per l'identificazione dell'id di correlazione non presenti");
			throw new GestoreMessaggiException(this.errore.getDescrizione(this.protocolFactory));
		}

		/** Fase di identificazione dell'id di correlazione */
		boolean checkBody = false;
		if(correlazioneApplicativa.sizeElementoList()>1){
			checkBody = true;
		}
		else{
			if(correlazioneApplicativa.sizeElementoList()>0){
				CorrelazioneApplicativaElemento elemento = correlazioneApplicativa.getElemento(0);
				if( (elemento.getNome()!=null) && !"".equals(elemento.getNome()) ){
					checkBody = true;
				}
			}
		}
		NodeList nListSoapBody = null;
		if(checkBody){
			try{
				if(envelope==null){
					throw new Exception("Envelope non presente nel messaggio Soap");
				}
				if(envelope.getBody()==null || envelope.getBody().hasChildNodes()==false){
					throw new Exception("Body applicativo non presente nel messaggio Soap");
				}
				nListSoapBody = envelope.getBody().getChildNodes();
				if(nListSoapBody==null || nListSoapBody.getLength()==0){
					throw new Exception("Elementi del Body non presenti?");
				}
			}catch(Exception e){
				this.errore = ErroriIntegrazione.ERRORE_416_CORRELAZIONE_APPLICATIVA_RICHIESTA_ERRORE.
						getErrore416_CorrelazioneApplicativaRichiesta("errore durante l'analisi dell'elementName: "+e.getMessage());
				throw new GestoreMessaggiException(this.errore.getDescrizione(this.protocolFactory),e);
			}
		}

		// XPathExpressionEngine
		AbstractXPathExpressionEngine xPathEngine = new org.openspcoop2.message.XPathExpressionEngine();
		
		/** Gestioni correlazioni, in modo da avere lo '*' in fondo */
		java.util.Vector<CorrelazioneApplicativaElemento> c = new java.util.Vector<CorrelazioneApplicativaElemento>();
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
		if(checkBody){
			for (int elem=0; elem<nListSoapBody.getLength(); elem++){
				Node nodeInEsame =  nListSoapBody.item(elem);
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
		if(checkBody){
			for (int elem=0; elem<nListSoapBody.getLength(); elem++){
				String elementName = null;
				Node nodeInEsame =  nListSoapBody.item(elem);
				try{
					elementName = nodeInEsame.getLocalName();
				}catch(Exception e){
					this.errore = ErroriIntegrazione.ERRORE_416_CORRELAZIONE_APPLICATIVA_RICHIESTA_ERRORE.
							getErrore416_CorrelazioneApplicativaRichiesta("errore durante l'analisi dell'elementName: "+e.getMessage());
					throw new GestoreMessaggiException(this.errore.getDescrizione(this.protocolFactory),e);
				}
				if(nodeInEsame instanceof Text || nodeInEsame instanceof Comment){
					continue;
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
					
				}else if( elementName.equals(elemento.getNome()) ){
					matchNodePerCorrelazioneApplicativa = true;
					nomeElemento = elemento.getNome();
				}else{
					// Ricerco elemento con una espressione che potrebbe essere riferita a tutta l'envelope
					try{
						DynamicNamespaceContext dnc = DynamicNamespaceContextFactory.getInstance().getNamespaceContext(envelope);
						nomeElemento = xPathEngine.getStringMatchPattern(envelope,dnc,elemento.getNome());
						if(nomeElemento!=null){
							matchNodePerCorrelazioneApplicativa = true;
						}
					}catch(Exception e){
						this.log.info("Calcolo non riuscito ["+elementName+"] ["+elemento.getNome()+"]: "+e.getMessage());
					}
					// Ricerco elemento con una espressione che potrebbe essere riferita localmente al nodo in esame
					/*try{
						DynamicNamespaceContext dncNode = DynamicNamespaceContext.getNamespaceContext(nodeInEsame);
						nomeElemento = RegularExpressionEngine.getStringMatchPatternContentBased(nodeInEsame,dncNode,elemento.getNome());
					}catch(Exception e){}*/
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
							idCorrelazioneApplicativa = RegularExpressionEngine.getStringMatchPattern(urlProtocolContext.getUrlInvocazione_formBased(), 
									elemento.getPattern());
						}catch(Exception e){
							if(bloccaIdentificazioneNonRiuscita){
								this.errore = ErroriIntegrazione.ERRORE_416_CORRELAZIONE_APPLICATIVA_RICHIESTA_ERRORE.
										getErrore416_CorrelazioneApplicativaRichiesta("identificativo di correlazione applicativa non identificato nell'elemento ["+nomeElemento+"] con modalita' di acquisizione urlBased (Pattern:"+elemento.getPattern()+"): "+e.getMessage());
								throw new GestoreMessaggiException(this.errore.getDescrizione(this.protocolFactory),e);
							}else{
								correlazioneNonRiuscitaDaAccettare = true;
							}
						}
					}else if( CostantiConfigurazione.CORRELAZIONE_APPLICATIVA_RICHIESTA_INPUT_BASED.equals(elemento.getIdentificazione()) ){
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
							DynamicNamespaceContext dnc = DynamicNamespaceContextFactory.getInstance().getNamespaceContext(envelope);
							idCorrelazioneApplicativa = xPathEngine.getStringMatchPattern(envelope,dnc,elemento.getPattern());
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
						" TIPO_MITTENTE=? AND MITTENTE=? AND TIPO_DESTINATARIO=? AND DESTINATARIO=? AND TIPO_SERVIZIO=? AND SERVIZIO=? AND "+valoreAzione);
				pstmt = connectionDB.prepareStatement(query.toString());
				pstmt.setString(1,idCorrelazioneApplicativa);
				pstmt.setString(2,this.servizioApplicativo);
				pstmt.setString(3,this.soggettoFruitore.getTipo());
				pstmt.setString(4,this.soggettoFruitore.getNome());
				pstmt.setString(5,this.idServizio.getSoggettoErogatore().getTipo());
				pstmt.setString(6,this.idServizio.getSoggettoErogatore().getNome());
				pstmt.setString(7,this.idServizio.getTipoServizio());
				pstmt.setString(8,this.idServizio.getServizio());
				if( (this.idServizio.getAzione()!=null) && ("".equals(this.idServizio.getAzione())==false) ){
					pstmt.setString(9,this.idServizio.getAzione());
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

	
	
	
	
	
	
	/* *********** GESTIONE CORRELAZIONE APPLICATIVA RISPOSTA (PORTA APPLICATIVA) ************ */
	
	/**
	 * Se avviene un errore durante la ricerca della correlazione, viene lanciata una eccezione.
	 *
	 * @param correlazioneApplicativa Parametri di correlazione applicativa
	 * @param envelope ByteApplicativo utilizzato per l'invocazione della porta delegata (utile per identificazioni content-based)
	 * @param headerIntegrazione Header di Integrazione
	 * @throws ProtocolException 
	 */
	public void verificaCorrelazioneRisposta(CorrelazioneApplicativaRisposta correlazioneApplicativa,
			SOAPEnvelope envelope,HeaderIntegrazione headerIntegrazione,boolean readFirstHeaderIntegrazione) throws GestoreMessaggiException, ProtocolException{

		if(correlazioneApplicativa==null){
			this.errore = ErroriIntegrazione.ERRORE_434_CORRELAZIONE_APPLICATIVA_RISPOSTA_ERRORE.
					getErrore434_CorrelazioneApplicativaRisposta("dati per l'identificazione dell'id di correlazione non presenti");
			throw new GestoreMessaggiException(this.errore.getDescrizione(this.protocolFactory));
		}

		// XPathExpressionEngine
		AbstractXPathExpressionEngine xPathEngine = new org.openspcoop2.message.XPathExpressionEngine();
		
		/** Fase di identificazione dell'id di correlazione */
		boolean checkBody = false;
		if(correlazioneApplicativa.sizeElementoList()>1){
			checkBody = true;
		}
		else{
			if(correlazioneApplicativa.sizeElementoList()>0){
				CorrelazioneApplicativaRispostaElemento elemento = correlazioneApplicativa.getElemento(0);
				if( (elemento.getNome()!=null) && !"".equals(elemento.getNome()) ){
					checkBody = true;
				}
			}
		}
		NodeList nListSoapBody = null;
		if(checkBody){
			try{
				if(envelope==null){
					throw new Exception("Envelope non presente nel messaggio Soap");
				}
				if(envelope.getBody()==null || envelope.getBody().hasChildNodes()==false){
					throw new Exception("Body applicativo non presente nel messaggio Soap");
				}
				nListSoapBody = envelope.getBody().getChildNodes();
				if(nListSoapBody==null || nListSoapBody.getLength()==0){
					throw new Exception("Elementi del Body non presenti?");
				}
			}catch(Exception e){
				this.errore = ErroriIntegrazione.ERRORE_434_CORRELAZIONE_APPLICATIVA_RISPOSTA_ERRORE.
						getErrore434_CorrelazioneApplicativaRisposta("errore durante l'analisi dell'elementName: "+e.getMessage());
				throw new GestoreMessaggiException(this.errore.getDescrizione(this.protocolFactory),e);
			}
		}

		
		/** Gestioni correlazioni, in modo da avere lo '*' in fondo */
		java.util.Vector<CorrelazioneApplicativaRispostaElemento> c = new java.util.Vector<CorrelazioneApplicativaRispostaElemento>();
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
		if(checkBody){
			for (int elem=0; elem<nListSoapBody.getLength(); elem++){
				Node nodeInEsame =  nListSoapBody.item(elem);
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
		if(checkBody){
			for (int elem=0; elem<nListSoapBody.getLength(); elem++){
				String elementName = null;
				Node nodeInEsame =  nListSoapBody.item(elem);
				try{
					elementName = nodeInEsame.getLocalName();
				}catch(Exception e){
					this.errore = ErroriIntegrazione.ERRORE_434_CORRELAZIONE_APPLICATIVA_RISPOSTA_ERRORE.
							getErrore434_CorrelazioneApplicativaRisposta("errore durante l'analisi dell'elementName: "+e.getMessage());
					throw new GestoreMessaggiException(this.errore.getDescrizione(this.protocolFactory),e);
				}
				if(nodeInEsame instanceof Text || nodeInEsame instanceof Comment){
					continue;
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
					
				}else if( elementName.equals(elemento.getNome()) ){
					matchNodePerCorrelazioneApplicativa = true;
					nomeElemento = elemento.getNome();
				}else{
					// Ricerco elemento con una espressione che potrebbe essere riferita a tutta l'envelope
					try{
						DynamicNamespaceContext dnc = DynamicNamespaceContextFactory.getInstance().getNamespaceContext(envelope);
						nomeElemento = xPathEngine.getStringMatchPattern(envelope,dnc,elemento.getNome());
						if(nomeElemento!=null){
							matchNodePerCorrelazioneApplicativa = true;
						}
					}catch(Exception e){
						this.log.info("Calcolo non riuscito ["+elementName+"] ["+elemento.getNome()+"]: "+e.getMessage());
					}
					// Ricerco elemento con una espressione che potrebbe essere riferita localmente al nodo in esame
					/*try{
						DynamicNamespaceContext dncNode = DynamicNamespaceContext.getNamespaceContext(nodeInEsame);
						nomeElemento = RegularExpressionEngine.getStringMatchPatternContentBased(nodeInEsame,dncNode,elemento.getNome());
					}catch(Exception e){}*/
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

					if( CostantiConfigurazione.CORRELAZIONE_APPLICATIVA_RISPOSTA_INPUT_BASED.equals(elemento.getIdentificazione()) ){
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
							DynamicNamespaceContext dnc = DynamicNamespaceContextFactory.getInstance().getNamespaceContext(envelope);
							idCorrelazioneApplicativa = xPathEngine.getStringMatchPattern(envelope,dnc,elemento.getPattern());
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
			queryInsert.append(" (ID_MESSAGGIO,ID_APPLICATIVO,SERVIZIO_APPLICATIVO,TIPO_MITTENTE,MITTENTE,TIPO_DESTINATARIO,DESTINATARIO,TIPO_SERVIZIO,SERVIZIO,AZIONE ");
			if(scadenzaCorrelazioneT!=null){
				queryInsert.append(",SCADENZA");
			}
			queryInsert.append(") VALUES (?,?,?,?,?,?,?,?,?,?");
			if(scadenzaCorrelazioneT!=null){
				queryInsert.append(",?");
			}
			queryInsert.append(")");
			pstmtInsert = connectionDB.prepareStatement(queryInsert.toString());
			pstmtInsert.setString(1,idBustaRequest);
			pstmtInsert.setString(2,idApplicativo);
			pstmtInsert.setString(3,this.servizioApplicativo);
			pstmtInsert.setString(4,this.soggettoFruitore.getTipo());
			pstmtInsert.setString(5,this.soggettoFruitore.getNome());
			pstmtInsert.setString(6,this.idServizio.getSoggettoErogatore().getTipo());
			pstmtInsert.setString(7,this.idServizio.getSoggettoErogatore().getNome());
			pstmtInsert.setString(8,this.idServizio.getTipoServizio());
			pstmtInsert.setString(9,this.idServizio.getServizio());
			pstmtInsert.setString(10,this.idServizio.getAzione());
			if(scadenzaCorrelazioneT!=null){
				pstmtInsert.setTimestamp(11,scadenzaCorrelazioneT);
			}

			//	Add PreparedStatement
			String valoreAzione = "N.D.";
			if( (this.idServizio.getAzione()!=null) && ("".equals(this.idServizio.getAzione())==false) ){
				valoreAzione = this.idServizio.getAzione();
			}
			stateMSG.getPreparedStatement().put("INSERT CorrelazioneApplicativa_"+idBustaRequest+"_"+idApplicativo+"_"+this.soggettoFruitore.getTipo()+this.soggettoFruitore.getNome()+
					"_"+this.idServizio.getSoggettoErogatore().getTipo()+this.idServizio.getSoggettoErogatore().getNome()+"_"+
					this.idServizio.getTipoServizio()+this.idServizio.getServizio()+"_"+valoreAzione,pstmtInsert);

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
	public java.util.Vector<Long> getCorrelazioniScadute(int limit,boolean logQuery,boolean orderBy) throws GestoreMessaggiException{

		java.util.Vector<Long> idMsg = new java.util.Vector<Long>();

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
	public java.util.Vector<Long> getCorrelazioniScaduteRispettoOraRegistrazione(int limit,long scadenzaMsg,boolean logQuery,boolean orderBy,boolean escludiCorrelazioniConScadenza) throws GestoreMessaggiException{

		java.util.Vector<Long> idMsg = new java.util.Vector<Long>();
		
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

