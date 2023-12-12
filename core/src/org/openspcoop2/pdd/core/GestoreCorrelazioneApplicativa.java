/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it). 
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

import java.io.ByteArrayOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.xml.soap.SOAPEnvelope;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.commons.CoreException;
import org.openspcoop2.core.config.CorrelazioneApplicativa;
import org.openspcoop2.core.config.CorrelazioneApplicativaElemento;
import org.openspcoop2.core.config.CorrelazioneApplicativaRisposta;
import org.openspcoop2.core.config.CorrelazioneApplicativaRispostaElemento;
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.Proprieta;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.core.id.IDPortaApplicativa;
import org.openspcoop2.core.id.IDPortaDelegata;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.Resource;
import org.openspcoop2.message.MessageUtils;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.pdd.config.ConfigurazionePdDManager;
import org.openspcoop2.pdd.config.CostantiProprieta;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.core.behaviour.conditional.ConditionalUtils;
import org.openspcoop2.pdd.core.dynamic.DynamicUtils;
import org.openspcoop2.pdd.core.dynamic.ErrorHandler;
import org.openspcoop2.pdd.core.dynamic.MessageContent;
import org.openspcoop2.pdd.core.dynamic.Template;
import org.openspcoop2.pdd.core.integrazione.HeaderIntegrazione;
import org.openspcoop2.pdd.core.transazioni.Transaction;
import org.openspcoop2.pdd.services.connector.FormUrlEncodedHttpServletRequest;
import org.openspcoop2.protocol.engine.Configurazione;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.constants.CodiceErroreIntegrazione;
import org.openspcoop2.protocol.sdk.constants.ErroreIntegrazione;
import org.openspcoop2.protocol.sdk.constants.ErroriIntegrazione;
import org.openspcoop2.protocol.sdk.state.IState;
import org.openspcoop2.protocol.sdk.state.RequestInfo;
import org.openspcoop2.protocol.sdk.state.StateMessage;
import org.openspcoop2.protocol.sdk.state.URLProtocolContext;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.MapKey;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.regexp.RegularExpressionEngine;
import org.openspcoop2.utils.sql.ISQLQueryObject;
import org.openspcoop2.utils.sql.SQLObjectFactory;
import org.openspcoop2.utils.sql.SQLQueryObjectException;
import org.openspcoop2.utils.transport.http.HttpServletTransportRequestContext;
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
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class GestoreCorrelazioneApplicativa {

	/** Logger utilizzato per debug. */
	protected Logger log = null;


	/* ********  F I E L D S    S T A T I C    P U B L I C  ******** */
	/** Tabella che mantiene i messaggi da consegnare ai servizi applicativi*/
	public static final String CORRELAZIONE_APPLICATIVA  = "CORRELAZIONE_APPLICATIVA";

	/* Colonne della tabella */
	public static final String CORRELAZIONE_APPLICATIVA_COLUMN_ID_APPLICATIVO  = "ID_APPLICATIVO";
	public static final String CORRELAZIONE_APPLICATIVA_COLUMN_ID_MESSAGGIO  = "ID_MESSAGGIO";
	
	/** Salvataggio in context */
	public static final MapKey<String> CONTEXT_CORRELAZIONE_APPLICATIVA_RICHIESTA = org.openspcoop2.utils.Map.newMapKey("CONTEXT_CORRELAZIONE_APPLICATIVA_RICHIESTA");
	public static final MapKey<String> CONTEXT_CORRELAZIONE_APPLICATIVA_RISPOSTA = org.openspcoop2.utils.Map.newMapKey("CONTEXT_CORRELAZIONE_APPLICATIVA_RISPOSTA");
	
	/** Se IState e' un'istanza di StatefulMessage possiede una Connessione SQL in autoCommit mode su cui effettuare query
	 *  Altrimenti, e' un'istanza di StatelessMessage e nn necessita di connessioni
	 * */
	protected IState state;

	/** id precedentemente associato alla correlazione */
	private String idBustaCorrelato;
	/** id Correlazione Applicativo */
	private String idCorrelazione;
	/** errore */
	protected ErroreIntegrazione errore;
	/** SoggettoFruitore */
	protected IDSoggetto soggettoFruitore;
	/** Servizio */
	protected IDServizio idServizio;
	/** Busta */
	private Busta busta;
	/** Resource REST */
	private Resource restResource;
	/** Servizio Applicativo */
	protected String servizioApplicativo;
	/** Scadenza correlazione applicativa */
	//private long scadenzaDefault; e' stata aggiunta l'ora di registrazione per differenziare la gestione tra quelle con scadenza e quelle senza.
	/** Indicazione se deve essere effettuato riuso id */
	private boolean riusoIdentificativo = false;
	/** ProtocolFactory */
	protected IProtocolFactory<?> protocolFactory = null;
	/** Transaction */
	private Transaction transaction;
	/** PddContext */
	private PdDContext pddContext;
	/** RequestInfo */
	private RequestInfo requestInfo;
	
	/** Porta */
	private PortaDelegata pd;
	private PortaApplicativa pa;
	
	private int maxLengthCorrelazioneApplicativa = 255;
	private int maxLengthExceededCorrelazioneApplicativaIdentificazioneFallitaBloccaTruncateRequest = -1;
	private int maxLengthExceededCorrelazioneApplicativaIdentificazioneFallitaAccettaTruncateRequest = -1;
	private int maxLengthExceededCorrelazioneApplicativaIdentificazioneFallitaBloccaTruncateResponse = -1;
	private int maxLengthExceededCorrelazioneApplicativaIdentificazioneFallitaAccettaTruncateResponse = -1;
	
	private boolean isRichiestaIdentificativoEstrattoIsNullThrowError = false;
	private boolean isRispostaIdentificativoEstrattoIsNullThrowError = false;
	
	private boolean isRichiestaIdentificativoEstrattoIsEmptyThrowError = false;
	private boolean isRispostaIdentificativoEstrattoIsEmptyThrowError = false;
	
	private boolean isRichiestaRegolaCorrelazioneNonTrovataBlocca = false;
	private boolean isRispostaRegolaCorrelazioneNonTrovataBlocca = false;

	public boolean isRiusoIdentificativo() {
		return this.riusoIdentificativo;
	}
	
	/* ********  C O S T R U T T O R E  ******** */
	/**
	 * Costruttore. 
	 *
	 */
	public GestoreCorrelazioneApplicativa(GestoreCorrelazioneApplicativaConfig config){		
		this.state = config.getState();
		if(config.getAlog()!=null){
			this.log = config.getAlog();
		}else{
			this.log = LoggerWrapperFactory.getLogger(GestoreCorrelazioneApplicativa.class);
		}
		this.soggettoFruitore = config.getSoggettoFruitore();
		this.idServizio = config.getIdServizio();
		this.busta = config.getBusta();
		this.servizioApplicativo = config.getServizioApplicativo();
		this.protocolFactory = config.getProtocolFactory();
		this.transaction = config.getTransaction();
		this.pddContext = config.getPddContext();
		if(this.pddContext!=null && this.pddContext.containsKey(org.openspcoop2.core.constants.Costanti.REQUEST_INFO)) {
			this.requestInfo = (RequestInfo) this.pddContext.getObject(org.openspcoop2.core.constants.Costanti.REQUEST_INFO);
		}
		
		List<Proprieta> proprieta = null;
		if(config.getPd()!=null) {
			this.pd = config.getPd();
			proprieta = this.pd.getProprietaList();
		}
		else if(config.getPa()!=null) {
			this.pa = config.getPa(); 
			proprieta = this.pa.getProprietaList();
		}
		
		OpenSPCoop2Properties op2Properties = OpenSPCoop2Properties.getInstance();
		this.maxLengthCorrelazioneApplicativa = op2Properties.getMaxLengthCorrelazioneApplicativa();
		try {
			if(CostantiProprieta.isCorrelazioneApplicativaRichiestaIdentificazioneFallitaBloccaTruncate(proprieta, op2Properties.isMaxLengthExceededCorrelazioneApplicativaIdentificazioneFallitaBloccaTruncate())) {
				this.maxLengthExceededCorrelazioneApplicativaIdentificazioneFallitaBloccaTruncateRequest = op2Properties.getMaxLengthExceededCorrelazioneApplicativaIdentificazioneFallitaBloccaTruncate();
			}
		}catch(Exception e) {
			this.log.error("[isCorrelazioneApplicativaRichiesta_identificazioneFallita_blocca_truncate] failed: "+e.getMessage(),e);
		}
		try {
			if(CostantiProprieta.isCorrelazioneApplicativaRispostaIdentificazioneFallitaBloccaTruncate(proprieta, op2Properties.isMaxLengthExceededCorrelazioneApplicativaIdentificazioneFallitaBloccaTruncate())) {
				this.maxLengthExceededCorrelazioneApplicativaIdentificazioneFallitaBloccaTruncateResponse = op2Properties.getMaxLengthExceededCorrelazioneApplicativaIdentificazioneFallitaBloccaTruncate();
			}
		}catch(Exception e) {
			this.log.error("[isCorrelazioneApplicativaRisposta_identificazioneFallita_blocca_truncate] failed: "+e.getMessage(),e);
		}
		try {
			if(CostantiProprieta.isCorrelazioneApplicativaRichiestaIdentificazioneFallitaAccettaTruncate(proprieta, op2Properties.isMaxLengthExceededCorrelazioneApplicativaIdentificazioneFallitaAccettaTruncate())) {
				this.maxLengthExceededCorrelazioneApplicativaIdentificazioneFallitaAccettaTruncateRequest = op2Properties.getMaxLengthExceededCorrelazioneApplicativaIdentificazioneFallitaAccettaTruncate();
			}
		}catch(Exception e) {
			this.log.error("[isCorrelazioneApplicativaRichiesta_identificazioneFallita_accetta_truncate] failed: "+e.getMessage(),e);
		}
		try {
			if(CostantiProprieta.isCorrelazioneApplicativaRispostaIdentificazioneFallitaAccettaTruncate(proprieta, op2Properties.isMaxLengthExceededCorrelazioneApplicativaIdentificazioneFallitaAccettaTruncate())) {
				this.maxLengthExceededCorrelazioneApplicativaIdentificazioneFallitaAccettaTruncateResponse = op2Properties.getMaxLengthExceededCorrelazioneApplicativaIdentificazioneFallitaAccettaTruncate();
			}
		}catch(Exception e) {
			this.log.error("[isCorrelazioneApplicativaRisposta_identificazioneFallita_accetta_truncate] failed: "+e.getMessage(),e);
		}
	
		try {
			this.isRichiestaIdentificativoEstrattoIsNullThrowError = CostantiProprieta.isCorrelazioneApplicativaRichiestaIdentificativoEstrattoIsNullTerminaTransazioneConErrore(proprieta, 
					op2Properties.isRepositoryCorrelazioneApplicativaRichiestaIdentificativoEstrattoIsNullConsideraErrore());
		}catch(Exception e) {
			this.log.error("[isCorrelazioneApplicativaRichiesta_identificativoEstrattoIsNull_terminaTransazioneConErrore] failed: "+e.getMessage(),e);
		}
		try {
			this.isRispostaIdentificativoEstrattoIsNullThrowError = CostantiProprieta.isCorrelazioneApplicativaRispostaIdentificativoEstrattoIsNullTerminaTransazioneConErrore(proprieta, 
					op2Properties.isRepositoryCorrelazioneApplicativaRispostaIdentificativoEstrattoIsNullConsideraErrore());
		}catch(Exception e) {
			this.log.error("[isCorrelazioneApplicativaRisposta_identificativoEstrattoIsNull_terminaTransazioneConErrore] failed: "+e.getMessage(),e);
		}
		
		try {
			this.isRichiestaIdentificativoEstrattoIsEmptyThrowError = CostantiProprieta.isCorrelazioneApplicativaRichiestaIdentificativoEstrattoIsEmptyTerminaTransazioneConErrore(proprieta, 
					op2Properties.isRepositoryCorrelazioneApplicativaRichiestaIdentificativoEstrattoIsEmptyConsideraErrore());
		}catch(Exception e) {
			this.log.error("[isCorrelazioneApplicativaRichiesta_identificativoEstrattoIsEmpty_terminaTransazioneConErrore] failed: "+e.getMessage(),e);
		}
		try {
			this.isRispostaIdentificativoEstrattoIsEmptyThrowError = CostantiProprieta.isCorrelazioneApplicativaRispostaIdentificativoEstrattoIsEmptyTerminaTransazioneConErrore(proprieta, 
					op2Properties.isRepositoryCorrelazioneApplicativaRispostaIdentificativoEstrattoIsEmptyConsideraErrore());
		}catch(Exception e) {
			this.log.error("[isCorrelazioneApplicativaRisposta_identificativoEstrattoIsEmpty_terminaTransazioneConErrore] failed: "+e.getMessage(),e);
		}
		
		try {
			this.isRichiestaRegolaCorrelazioneNonTrovataBlocca = CostantiProprieta.isCorrelazioneApplicativaRichiestaRegolaNonTrovataTerminaTransazioneConErrore(proprieta, 
					op2Properties.isRepositoryCorrelazioneApplicativaRichiestaRegolaCorrelazioneNonTrovataBlocca());
		}catch(Exception e) {
			this.log.error("[isCorrelazioneApplicativaRichiesta_regolaNonTrovata_terminaTransazioneConErrore] failed: "+e.getMessage(),e);
		}
		try {
			this.isRispostaRegolaCorrelazioneNonTrovataBlocca = CostantiProprieta.isCorrelazioneApplicativaRispostaRegolaNonTrovataTerminaTransazioneConErrore(proprieta, 
					op2Properties.isRepositoryCorrelazioneApplicativaRispostaRegolaCorrelazioneNonTrovataBlocca());
		}catch(Exception e) {
			this.log.error("[isCorrelazioneApplicativaRisposta_regolaNonTrovata_terminaTransazioneConErrore] failed: "+e.getMessage(),e);
		}
		
	}

	public void updateState(IState state){
		this.state = state;
	}
	
	private static final String ERRORE_IDENTIFICAZIONE_MESSAGE = "errore durante l'analisi dell'elementName: ";
	private static final String ERRORE_IDENTIFICAZIONE_NON_IDENTIFICATO_NEL_ELEMENTO = "identificativo di correlazione applicativa non identificato nell'elemento [";
	private static final String ERRORE_IDENTIFICAZIONE_NON_IDENTIFICATO_NEL_ELEMENTO_MODALITA_URL_BASED = "] con modalita' di acquisizione urlBased (Pattern:";
	private static final String ERRORE_IDENTIFICAZIONE_NON_IDENTIFICATO_NEL_ELEMENTO_MODALITA_HEADER_BASED = "] con modalita' di acquisizione headerBased (Header:";
	private static final String ERRORE_IDENTIFICAZIONE_NON_IDENTIFICATO_NEL_ELEMENTO_MODALITA_CONTENT_BASED = "] con modalita' di acquisizione contentBased (Pattern:";
	private static final String ERRORE_IDENTIFICAZIONE_NON_IDENTIFICATO_NEL_ELEMENTO_MODALITA_TEMPLATE_BASED = "] con modalita' di acquisizione "; // usato per template, freemarkerTemplate e velocityTemplate
	private static final String ERRORE_IDENTIFICAZIONE_PER_ELEMENTO = "identificativo di correlazione applicativa per l'elemento [";
	private static final String ERRORE_IDENTIFICAZIONE_PER_ELEMENTO_MODALITA_INPUT_BASED = "con modalita' di acquisizione inputBased non presente tra le informazioni di integrazione";

	private String buildErroreLunghezzaIdentificativo(String idCorrelazioneApplicativa) {
		return "Identificativo di correlazione applicativa identificato possiede una lunghezza ("+idCorrelazioneApplicativa.length()+") superiore ai "+this.maxLengthCorrelazioneApplicativa+" caratteri consentiti";
	}
	
	private void checkExtractedIdentifierIsNull(String idCorrelazioneApplicativa, boolean request) throws GestoreMessaggiException {
		boolean identificativoEstrattoIsNullThrowError = request ? this.isRichiestaIdentificativoEstrattoIsNullThrowError : this.isRispostaIdentificativoEstrattoIsNullThrowError;
		if(idCorrelazioneApplicativa==null && identificativoEstrattoIsNullThrowError) {
			throw new GestoreMessaggiException("extracted identifier is null");
		}
	}
	private void checkExtractedIdentifierIsEmpty(String idCorrelazioneApplicativa, boolean request) throws GestoreMessaggiException {
		boolean identificativoEstrattoIsEmptyThrowError = request ? this.isRichiestaIdentificativoEstrattoIsEmptyThrowError : this.isRispostaIdentificativoEstrattoIsEmptyThrowError;
		if(StringUtils.isEmpty(idCorrelazioneApplicativa) && identificativoEstrattoIsEmptyThrowError) {
			throw new GestoreMessaggiException("extracted identifier is empty");
		}
	}
	
	
	
	/* *********** GESTIONE CORRELAZIONE APPLICATIVA (RICHIESTA) ************ */

	public void verificaCorrelazione(CorrelazioneApplicativa correlazioneApplicativa,
			URLProtocolContext urlProtocolContext,OpenSPCoop2Message message,HeaderIntegrazione headerIntegrazione,boolean readFirstHeaderIntegrazione) throws GestoreMessaggiException, ProtocolException{
		if(this.transaction!=null) {
			this.transaction.getTempiElaborazione().startCorrelazioneApplicativaRichiesta();
		}
		try {
			this.verificaCorrelazioneEngine(correlazioneApplicativa, urlProtocolContext, message, headerIntegrazione, readFirstHeaderIntegrazione);
		}
		finally {
			if(this.transaction!=null) {
				this.transaction.getTempiElaborazione().endCorrelazioneApplicativaRichiesta();
			}
		}
	}
	public boolean verificaCorrelazioneIdentificativoRichiesta() throws GestoreMessaggiException{
		try {
			return this.verificaCorrelazioneIdentificativoRichiestaEngine();
		}
		finally {
			if(this.transaction!=null) {
				this.transaction.getTempiElaborazione().endCorrelazioneApplicativaRichiesta(); // sovrascrivo precedente data
			}
		}
	}
	private void verificaCorrelazioneEngine(CorrelazioneApplicativa correlazioneApplicativa,
			URLProtocolContext urlProtocolContext,OpenSPCoop2Message message,HeaderIntegrazione headerIntegrazione,boolean readFirstHeaderIntegrazione) throws GestoreMessaggiException, ProtocolException{

		if(correlazioneApplicativa==null){
			this.errore = ErroriIntegrazione.ERRORE_416_CORRELAZIONE_APPLICATIVA_RICHIESTA_ERRORE.
					getErrore416_CorrelazioneApplicativaRichiesta("dati per l'identificazione dell'id di correlazione non presenti");
			throw new GestoreMessaggiException(this.errore.getDescrizione(this.protocolFactory));
		}

		if(message==null) {
			this.errore = ErroriIntegrazione.ERRORE_416_CORRELAZIONE_APPLICATIVA_RICHIESTA_ERRORE.
					getErrore416_CorrelazioneApplicativaRichiesta("messaggio non presente");
			throw new GestoreMessaggiException(this.errore.getDescrizione(this.protocolFactory));
		}
		
		Element element = null;
		String elementJson = null;
		try{
			String idTransazione = null;
			if(this.pddContext!=null) {
				idTransazione = (String)this.pddContext.getObject(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE);
			}
			boolean bufferMessageReadOnly =  OpenSPCoop2Properties.getInstance().isReadByPathBufferEnabled();
			boolean checkSoapBodyEmpty = false; // devo poter fare xpath anche su soapBody empty
			element = MessageUtils.getContentElement(message, checkSoapBodyEmpty, bufferMessageReadOnly, idTransazione);
			elementJson = MessageUtils.getContentString(message, bufferMessageReadOnly, idTransazione);
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
						throw new CoreException("Envelope non presente nel messaggio Soap");
					}
					if(envelope.getBody()==null || !envelope.getBody().hasChildNodes()){
						throw new CoreException("Body applicativo non presente nel messaggio Soap");
					}
					NodeList nListSoapBody = envelope.getBody().getChildNodes();
					if(nListSoapBody==null || nListSoapBody.getLength()==0){
						throw new CoreException("Elementi del Body non presenti?");
					}
					nList = new ArrayList<>();
					for (int elem=0; elem<nListSoapBody.getLength(); elem++){
						Node nodeInEsame =  nListSoapBody.item(elem);
						if(nodeInEsame instanceof Text || nodeInEsame instanceof Comment){
							continue;
						}
						nList.add(nodeInEsame);
					}
				}
				else{
					if(MessageType.XML.equals(message.getMessageType()) || 
							MessageType.MIME_MULTIPART.equals(message.getMessageType()) // viene prelevato il primo xml trovato
							){
						/** Nei GET il messaggio e' vuoto
						if(element==null){
							throw new Exception("Contenuto non presente nel messaggio");
						}*/
						if(element!=null){
							nList = new ArrayList<>();
							nList.add(element);
						}
					}
					else{
						checkElementoInTransito = false;
					}
				}
			}catch(Exception e){
				this.errore = ErroriIntegrazione.ERRORE_416_CORRELAZIONE_APPLICATIVA_RICHIESTA_ERRORE.
						getErrore416_CorrelazioneApplicativaRichiesta(ERRORE_IDENTIFICAZIONE_MESSAGE+e.getMessage());
				throw new GestoreMessaggiException(this.errore.getDescrizione(this.protocolFactory),e);
			}
		}

		// XPathExpressionEngine
		AbstractXPathExpressionEngine xPathEngine = new org.openspcoop2.message.xml.XPathExpressionEngine(message.getFactory());
		
		/** Gestioni correlazioni, in modo da avere lo '*' in fondo */
		java.util.List<CorrelazioneApplicativaElemento> c = new java.util.ArrayList<>();
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
		List<String> nomiPresentiBody = new ArrayList<>();
		if(checkElementoInTransito && nList!=null){
			for (int elem=0; elem<nList.size(); elem++){
				String elementName = null;
				Node nodeInEsame =  nList.get(elem);
				if( (!(nodeInEsame instanceof Text)) && (!(nodeInEsame instanceof Comment)) ){
					try{
						elementName = nodeInEsame.getLocalName();
					}catch(Exception e){
						this.errore = ErroriIntegrazione.ERRORE_416_CORRELAZIONE_APPLICATIVA_RICHIESTA_ERRORE.
								getErrore416_CorrelazioneApplicativaRichiesta(ERRORE_IDENTIFICAZIONE_MESSAGE+e.getMessage());
						throw new GestoreMessaggiException(this.errore.getDescrizione(this.protocolFactory),e);
					}
					if(elementName==null){
						continue;
					}
					nomiPresentiBody.add(elementName);
				}
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
				// non definito: significa per qualsiasi richiesta
				// nome: il nome può rappresentare:
				// - nome dell'identificativo dell'azione
				// - nome dell'elemento radice dell'xml
				// xpath o json expression: per identificare il nome dell'elemento radice XML
				boolean matchNodePerCorrelazioneApplicativa = false;
				String nomeElemento = null;
				if( (elemento.getNome()==null) || "".equals(elemento.getNome()) ){
					matchNodePerCorrelazioneApplicativa = true;
					nomeElemento = "*";
					
					// Devo applicare lo '*' solo se ho prima guardato tutti i nodi radice e sono ad esaminare l'ultimo nodo radice.
					// L'ordine delle correlazioni mi garantira' che lo '*' sara' esaminato per ultimo
					if( elem!=posizioneUltimoNodo )
						continue;
					
				}
				else if(
							(
								// Ricerco per match sull'azione
								this.idServizio!=null && this.idServizio.getAzione()!=null &&
								this.idServizio.getAzione().equals(elemento.getNome())
							)
						||
							(
								// Ricerco per match sul localName dell'elemento (se XML)
								elementName.equals(elemento.getNome())	
							)
						) {
					
					matchNodePerCorrelazioneApplicativa = true;
					nomeElemento = elemento.getNome();
				}
				else{
					
					// se siamo in REST provo a cercare per metodo e path
					boolean isResourceRest = false;
					if(ServiceBinding.REST.equals(message.getServiceBinding())){
						isResourceRest = isMatchResourceRest(elemento.getNome());
					}
					if(isResourceRest) {
						matchNodePerCorrelazioneApplicativa = true;
						nomeElemento = elemento.getNome();
					}
					else {
						
						// Ricerco elemento con una espressione
						try{
							if(element==null && elementJson==null){
								throw new CoreException("Contenuto non disponibile su cui effettuare un match");
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
							String error = "Calcolo (contentBased) non riuscito ["+elementName+"] ["+elemento.getNome()+"]: "+e.getMessage();
							this.log.info(error);
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
								String error = "Calcolo (urlBased) non riuscito ["+elementName+"] ["+elemento.getNome()+"]: "+e.getMessage();
								this.log.info(error);
							}
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
					
					
					if(readFirstHeaderIntegrazione &&
						headerIntegrazione.getIdApplicativo()!=null){
						idCorrelazioneApplicativa = headerIntegrazione.getIdApplicativo();
						findCorrelazione = true;
						break;
					}

					if( CostantiConfigurazione.CORRELAZIONE_APPLICATIVA_RICHIESTA_URL_BASED.equals(elemento.getIdentificazione())){
						try{
							List<String> l = RegularExpressionEngine.getAllStringMatchPattern(urlProtocolContext.getUrlInvocazione_formBased(), 
									elemento.getPattern());
							if(l!=null && !l.isEmpty()) {
								StringBuilder bf = new StringBuilder();
								for (String id : l) {
									if(bf.length()>0) {
										bf.append(" ");
									}
									bf.append(id);
								}
								idCorrelazioneApplicativa =  bf.toString();
							}
							checkExtractedIdentifierIsNull(idCorrelazioneApplicativa, true);
							checkExtractedIdentifierIsEmpty(idCorrelazioneApplicativa, true);
						}catch(Exception e){
							if(bloccaIdentificazioneNonRiuscita){
								this.errore = ErroriIntegrazione.ERRORE_416_CORRELAZIONE_APPLICATIVA_RICHIESTA_ERRORE.
										getErrore416_CorrelazioneApplicativaRichiesta(ERRORE_IDENTIFICAZIONE_NON_IDENTIFICATO_NEL_ELEMENTO+nomeElemento+ERRORE_IDENTIFICAZIONE_NON_IDENTIFICATO_NEL_ELEMENTO_MODALITA_URL_BASED+elemento.getPattern()+"): "+e.getMessage());
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
									message.getTransportRequestContext().getHeaders()==null ||
									message.getTransportRequestContext().getHeaders().isEmpty()) {
								throw new CoreException ("headers not found");
							}
							idCorrelazioneApplicativa = message.getTransportRequestContext().getHeaderFirstValue(elemento.getPattern());
							if(idCorrelazioneApplicativa==null) {
								// NOTA: deve essere considerato come vi fosse un errore nel pattern
								throw new GestoreMessaggiException("header not found");
							}
							checkExtractedIdentifierIsEmpty(idCorrelazioneApplicativa, true);
						}catch(Exception e){
							if(bloccaIdentificazioneNonRiuscita){
								this.errore = ErroriIntegrazione.ERRORE_416_CORRELAZIONE_APPLICATIVA_RICHIESTA_ERRORE.
										getErrore416_CorrelazioneApplicativaRichiesta(ERRORE_IDENTIFICAZIONE_NON_IDENTIFICATO_NEL_ELEMENTO+nomeElemento+ERRORE_IDENTIFICAZIONE_NON_IDENTIFICATO_NEL_ELEMENTO_MODALITA_HEADER_BASED+elemento.getPattern()+"): "+e.getMessage());
								throw new GestoreMessaggiException(this.errore.getDescrizione(this.protocolFactory),e);
							}else{
								correlazioneNonRiuscitaDaAccettare = true;
							}
						}
					}
					else if( CostantiConfigurazione.CORRELAZIONE_APPLICATIVA_RICHIESTA_INPUT_BASED.equals(elemento.getIdentificazione()) ){
						idCorrelazioneApplicativa = headerIntegrazione.getIdApplicativo();
						if(
								(idCorrelazioneApplicativa==null) // NOTA: deve essere considerato come vi fosse un errore
								|| 
								(StringUtils.isEmpty(idCorrelazioneApplicativa) && this.isRichiestaIdentificativoEstrattoIsEmptyThrowError)
							){
							if(bloccaIdentificazioneNonRiuscita){
								this.errore = ErroriIntegrazione.ERRORE_416_CORRELAZIONE_APPLICATIVA_RICHIESTA_ERRORE.
										getErrore416_CorrelazioneApplicativaRichiesta(ERRORE_IDENTIFICAZIONE_PER_ELEMENTO+nomeElemento+"] " +
												ERRORE_IDENTIFICAZIONE_PER_ELEMENTO_MODALITA_INPUT_BASED);
								throw new GestoreMessaggiException(this.errore.getDescrizione(this.protocolFactory));
							}else{
								correlazioneNonRiuscitaDaAccettare = true;
							}
						}
					}
					else if( CostantiConfigurazione.CORRELAZIONE_APPLICATIVA_RICHIESTA_TEMPLATE.equals(elemento.getIdentificazione())
							||
							CostantiConfigurazione.CORRELAZIONE_APPLICATIVA_RICHIESTA_FREEMARKER_TEMPLATE.equals(elemento.getIdentificazione())
							||
							CostantiConfigurazione.CORRELAZIONE_APPLICATIVA_RICHIESTA_VELOCITY_TEMPLATE.equals(elemento.getIdentificazione())){
						try{
							
							if(elemento.getPattern()==null || StringUtils.isEmpty(elemento.getPattern())) {
								throw new CoreException ("Template non disponibile");
							}
							
							Map<String, List<String>> pTrasporto = null;
							String urlInvocazione = null;
							Map<String, List<String>> pQuery = null;
							Map<String, List<String>> pForm = null;
							if(this.requestInfo!=null && this.requestInfo.getProtocolContext()!=null) {
								pTrasporto = this.requestInfo.getProtocolContext().getHeaders();
								urlInvocazione = this.requestInfo.getProtocolContext().getUrlInvocazione_formBased();
								pQuery = this.requestInfo.getProtocolContext().getParameters();
								if(this.requestInfo.getProtocolContext() instanceof HttpServletTransportRequestContext) {
									HttpServletTransportRequestContext httpServletContext = this.requestInfo.getProtocolContext();
									HttpServletRequest httpServletRequest = httpServletContext.getHttpServletRequest();
									if(httpServletRequest instanceof FormUrlEncodedHttpServletRequest) {
										FormUrlEncodedHttpServletRequest formServlet = (FormUrlEncodedHttpServletRequest) httpServletRequest;
										if(formServlet.getFormUrlEncodedParametersValues()!=null &&
												!formServlet.getFormUrlEncodedParametersValues().isEmpty()) {
											pForm = formServlet.getFormUrlEncodedParametersValues();
										}
									}
								}
							}
							MessageContent messageContent = null;
							boolean bufferMessageReadOnly =  OpenSPCoop2Properties.getInstance().isReadByPathBufferEnabled();
							if(ServiceBinding.SOAP.equals(message.getServiceBinding())){
								messageContent = new MessageContent(message.castAsSoap(), bufferMessageReadOnly, this.pddContext);
							}
							else{
								if(MessageType.XML.equals(message.getMessageType())){
									messageContent = new MessageContent(message.castAsRestXml(), bufferMessageReadOnly, this.pddContext);
								}
								else if(MessageType.JSON.equals(message.getMessageType())){
									messageContent = new MessageContent(message.castAsRestJson(), bufferMessageReadOnly, this.pddContext);
								}
							}
							
							Map<String, Object> dynamicMap = new HashMap<>();
							ErrorHandler errorHandler = new ErrorHandler();
							DynamicUtils.fillDynamicMapRequest(this.log, dynamicMap, this.pddContext, urlInvocazione,
									message,
									messageContent, 
									this.busta, 
									pTrasporto, 
									pQuery,
									pForm,
									errorHandler);
							if(CostantiConfigurazione.CORRELAZIONE_APPLICATIVA_RICHIESTA_TEMPLATE.equals(elemento.getIdentificazione())) {
								idCorrelazioneApplicativa = DynamicUtils.convertDynamicPropertyValue("CorrelazioneApplicativaRichiesta.gwt", elemento.getPattern(), dynamicMap, this.pddContext);
								if(idCorrelazioneApplicativa!=null) {
									idCorrelazioneApplicativa = ConditionalUtils.normalizeTemplateResult(idCorrelazioneApplicativa);
								}
							}
							else {
								ByteArrayOutputStream bout = new ByteArrayOutputStream();
								ConfigurazionePdDManager configurazionePdDManager = ConfigurazionePdDManager.getInstance(this.state);
								IDPortaApplicativa idPA = null;
								IDPortaDelegata idPD = null;
								if(this.pa!=null) {
									idPA = new IDPortaApplicativa();
									idPA.setNome(this.pa.getNome());
								}
								else if(this.pd!=null){
									idPD = new IDPortaDelegata();
									idPD.setNome(this.pd.getNome());
								}
								else {
									throw new CoreException ("Porta non disponibile");
								}
								Template template = idPA!=null ?
										configurazionePdDManager.getTemplateCorrelazioneApplicativaRichiesta(idPA, elemento.getNome(), elemento.getPattern().getBytes(), this.requestInfo)
										:
										configurazionePdDManager.getTemplateCorrelazioneApplicativaRichiesta(idPD, elemento.getNome(), elemento.getPattern().getBytes(), this.requestInfo);
								
								if(CostantiConfigurazione.CORRELAZIONE_APPLICATIVA_RICHIESTA_FREEMARKER_TEMPLATE.equals(elemento.getIdentificazione())) {
									DynamicUtils.convertFreeMarkerTemplate(template, dynamicMap, bout);
								}
								else {
									/** if(CostantiConfigurazione.CORRELAZIONE_APPLICATIVA_RICHIESTA_VELOCITY_TEMPLATE.equals(elemento.getIdentificazione())) { */
									DynamicUtils.convertVelocityTemplate(template, dynamicMap, bout);
								}
								bout.flush();
								bout.close();
								idCorrelazioneApplicativa = bout.toString();
								if(idCorrelazioneApplicativa!=null) {
									idCorrelazioneApplicativa = ConditionalUtils.normalizeTemplateResult(idCorrelazioneApplicativa);
								}
							}
							
							checkExtractedIdentifierIsNull(idCorrelazioneApplicativa, true);
							checkExtractedIdentifierIsEmpty(idCorrelazioneApplicativa, true);

						}catch(Exception e){
							if(bloccaIdentificazioneNonRiuscita){
								this.errore = ErroriIntegrazione.ERRORE_416_CORRELAZIONE_APPLICATIVA_RICHIESTA_ERRORE.
										getErrore416_CorrelazioneApplicativaRichiesta(ERRORE_IDENTIFICAZIONE_NON_IDENTIFICATO_NEL_ELEMENTO+nomeElemento+ERRORE_IDENTIFICAZIONE_NON_IDENTIFICATO_NEL_ELEMENTO_MODALITA_TEMPLATE_BASED+elemento.getIdentificazione().getValue()+"): "+e.getMessage());
								throw new GestoreMessaggiException(this.errore.getDescrizione(this.protocolFactory),e);
							}else{
								correlazioneNonRiuscitaDaAccettare = true;
							}
						}
					}
					else{
						// Content-Based
						try{
							if(ServiceBinding.REST.equals(message.getServiceBinding()) && 
									!MessageType.XML.equals(message.getMessageType()) && 
									!MessageType.JSON.equals(message.getMessageType()) &&
									!MessageType.MIME_MULTIPART.equals(message.getMessageType())){
								throw new CoreException("MessageType ["+message.getMessageType()+"] non supportato con correlazione di tipo '"+
										CostantiConfigurazione.CORRELAZIONE_APPLICATIVA_RICHIESTA_CONTENT_BASED.toString()+"'");
							}
							if(element==null && elementJson==null){
								throw new CoreException("Contenuto non disponibile su cui effettuare una correlazione di tipo '"+
										CostantiConfigurazione.CORRELAZIONE_APPLICATIVA_RICHIESTA_CONTENT_BASED.toString()+"'");
							}
							if(element!=null) {
								idCorrelazioneApplicativa = AbstractXPathExpressionEngine.extractAndConvertResultAsString(element, xPathEngine, elemento.getPattern(), this.log);
							}
							else {
								idCorrelazioneApplicativa = JsonXmlPathExpressionEngine.extractAndConvertResultAsString(elementJson, elemento.getPattern(), this.log);
							}
									
							checkExtractedIdentifierIsNull(idCorrelazioneApplicativa, true);
							checkExtractedIdentifierIsEmpty(idCorrelazioneApplicativa, true);
							
						}catch(Exception e){
							if(bloccaIdentificazioneNonRiuscita){
								this.errore = ErroriIntegrazione.ERRORE_416_CORRELAZIONE_APPLICATIVA_RICHIESTA_ERRORE.
										getErrore416_CorrelazioneApplicativaRichiesta(ERRORE_IDENTIFICAZIONE_NON_IDENTIFICATO_NEL_ELEMENTO+nomeElemento+ERRORE_IDENTIFICAZIONE_NON_IDENTIFICATO_NEL_ELEMENTO_MODALITA_CONTENT_BASED+elemento.getPattern()+"): "+e.getMessage());
								throw new GestoreMessaggiException(this.errore.getDescrizione(this.protocolFactory),e);
							}else{
								correlazioneNonRiuscitaDaAccettare = true;
								String debugInfo = "[AccettaIdentificazioneFallita] Identificativo di correlazione applicativa non identificato nell'elemento ["+nomeElemento+ERRORE_IDENTIFICAZIONE_NON_IDENTIFICATO_NEL_ELEMENTO_MODALITA_CONTENT_BASED+elemento.getPattern()+"): "+e.getMessage();
								this.log.info(debugInfo);
							}
						}
					}
					
					
					if(idCorrelazioneApplicativa!=null && idCorrelazioneApplicativa.length()>this.maxLengthCorrelazioneApplicativa) {
						if(bloccaIdentificazioneNonRiuscita) {
							if(this.isTruncateEnabled(RICHIESTA, BLOCCA)) {
								idCorrelazioneApplicativa = this.truncate(idCorrelazioneApplicativa, RICHIESTA, BLOCCA);
							}
							else {
								this.errore = ErroriIntegrazione.ERRORE_416_CORRELAZIONE_APPLICATIVA_RICHIESTA_ERRORE.
										getErrore416_CorrelazioneApplicativaRichiesta(this.buildErroreLunghezzaIdentificativo(idCorrelazioneApplicativa));
								throw new GestoreMessaggiException(this.errore.getDescrizione(this.protocolFactory));
							}
						}
						else {
							if(this.isTruncateEnabled(RICHIESTA, ACCETTA)) {
								idCorrelazioneApplicativa = this.truncate(idCorrelazioneApplicativa, RICHIESTA, ACCETTA);
							}
							else {
								String erroreId = this.buildErroreLunghezzaIdentificativo(idCorrelazioneApplicativa);
								this.log.error(erroreId);
								correlazioneNonRiuscitaDaAccettare = true;
								idCorrelazioneApplicativa = null;
							}
						}
					}
					
					findCorrelazione = true;
					break;
				}
			}
		}
		
		if(idCorrelazioneApplicativa == null){
			boolean generaErrore = false;
			if( !findCorrelazione ) {
				generaErrore = this.isRichiestaRegolaCorrelazioneNonTrovataBlocca;
			}
			else {
				generaErrore = !correlazioneNonRiuscitaDaAccettare;
			}
			if( generaErrore ){
				this.errore = ErroriIntegrazione.ERRORE_416_CORRELAZIONE_APPLICATIVA_RICHIESTA_ERRORE.
						getErrore416_CorrelazioneApplicativaRichiesta("Identificativo di correlazione applicativa non identificato; nessun elemento tra quelli di correlazione definiti, sono presenti nel body");
				throw new GestoreMessaggiException(this.errore.getDescrizione(this.protocolFactory));
			}
		}else{			
			this.idCorrelazione = idCorrelazioneApplicativa;
			if(this.pddContext!=null) {
				this.pddContext.addObject(CONTEXT_CORRELAZIONE_APPLICATIVA_RICHIESTA, this.idCorrelazione);
			}
		}

	}
	
	private boolean verificaCorrelazioneIdentificativoRichiestaEngine() throws GestoreMessaggiException{
		
		/** Fase di verifica dell'id di correlazione con l'id */
		
		if(this.riusoIdentificativo){
			try{
				StateMessage stateMSG = (StateMessage)this.state;
				Connection connectionDB = stateMSG.getConnectionDB();
	
				// Lettura attuale valore
				
				executeVerificaCorrelazioneIdentificativoRichiesta(connectionDB);
	
			} catch(Exception er) {
				this.errore = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
						get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_529_CORRELAZIONE_APPLICATIVA_RICHIESTA_NON_RIUSCITA);
				String error = "Verifica correlazione IDApplicativo - ID non riuscita: "+er.getMessage();
				this.log.error(error);
				throw new GestoreMessaggiException("Verifica correlazione IDApplicativo - ID non riuscita: "+er.getMessage(),er);
			} 
	
			return this.idBustaCorrelato!=null;

		}else{
			return false;
		}

	}
	private boolean executeVerificaCorrelazioneIdentificativoRichiesta(Connection connectionDB) throws SQLException, GestoreMessaggiException{
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		boolean correlazionePresente = false;
		try{
		
			// Azione
			String valoreAzione = "(AZIONE is null)";
			if( (this.idServizio.getAzione()!=null) && (!"".equals(this.idServizio.getAzione())) ){
				valoreAzione = "AZIONE=?";
			}
			
			StringBuilder query = new StringBuilder();
			query.append("SELECT * FROM "+GestoreCorrelazioneApplicativa.CORRELAZIONE_APPLICATIVA+" WHERE ID_APPLICATIVO=? AND SERVIZIO_APPLICATIVO=? AND"+
					" TIPO_MITTENTE=? AND MITTENTE=? AND TIPO_DESTINATARIO=? AND DESTINATARIO=? AND TIPO_SERVIZIO=? AND SERVIZIO=? AND VERSIONE_SERVIZIO=? AND "+valoreAzione);
			pstmt = connectionDB.prepareStatement(query.toString());
			int index = 1;
			pstmt.setString(index++,this.idCorrelazione);
			pstmt.setString(index++,this.servizioApplicativo);
			pstmt.setString(index++,this.soggettoFruitore.getTipo());
			pstmt.setString(index++,this.soggettoFruitore.getNome());
			pstmt.setString(index++,this.idServizio.getSoggettoErogatore().getTipo());
			pstmt.setString(index++,this.idServizio.getSoggettoErogatore().getNome());
			pstmt.setString(index++,this.idServizio.getTipo());
			pstmt.setString(index++,this.idServizio.getNome());
			pstmt.setInt(index++,this.idServizio.getVersione());
			
			if( (this.idServizio.getAzione()!=null) && (!"".equals(this.idServizio.getAzione())) ){
				pstmt.setString(index,this.idServizio.getAzione());
			}
			
			rs = pstmt.executeQuery();
			if(rs == null) {
				pstmt.close();
				this.log.error("Verifica correlazione IDApplicativo - ID non riuscita: ResultSet is null?");
				throw new GestoreMessaggiException("Verifica correlazione IDApplicativo - ID non riuscita: ResultSet is null?");		
			}
			correlazionePresente = rs.next();
			if(correlazionePresente){
				this.idBustaCorrelato = rs.getString(CORRELAZIONE_APPLICATIVA_COLUMN_ID_MESSAGGIO);
			}
		} finally {
			try {
				if(rs!=null) {
					rs.close();
				}
			}catch(Exception t) {
				// ignore
			}
			try {
				if(pstmt!=null) {
					pstmt.close();
				}
			}catch(Exception t) {
				// ignore
			}
		}
		
		return correlazionePresente;
	}


	
	
	
	
	/* *********** GESTIONE CORRELAZIONE APPLICATIVA RISPOSTA (RISPOSTA) ************ */
	
	public void verificaCorrelazioneRisposta(CorrelazioneApplicativaRisposta correlazioneApplicativa,
			OpenSPCoop2Message message,HeaderIntegrazione headerIntegrazione,boolean readFirstHeaderIntegrazione) throws GestoreMessaggiException, ProtocolException{
		if(this.transaction!=null) {
			this.transaction.getTempiElaborazione().startCorrelazioneApplicativaRisposta();
		}
		try {
			this.verificaCorrelazioneRispostaEngine(correlazioneApplicativa, message, headerIntegrazione, readFirstHeaderIntegrazione);
		}
		finally {
			if(this.transaction!=null) {
				this.transaction.getTempiElaborazione().endCorrelazioneApplicativaRisposta();
			}
		}
	}
	private void verificaCorrelazioneRispostaEngine(CorrelazioneApplicativaRisposta correlazioneApplicativa,
			OpenSPCoop2Message message,HeaderIntegrazione headerIntegrazione,boolean readFirstHeaderIntegrazione) throws GestoreMessaggiException, ProtocolException{

		if(correlazioneApplicativa==null){
			this.errore = ErroriIntegrazione.ERRORE_434_CORRELAZIONE_APPLICATIVA_RISPOSTA_ERRORE.
					getErrore434_CorrelazioneApplicativaRisposta("dati per l'identificazione dell'id di correlazione non presenti");
			throw new GestoreMessaggiException(this.errore.getDescrizione(this.protocolFactory));
		}
		
		if(message==null) {
			this.errore = ErroriIntegrazione.ERRORE_434_CORRELAZIONE_APPLICATIVA_RISPOSTA_ERRORE.
					getErrore434_CorrelazioneApplicativaRisposta("messaggio non presente");
			throw new GestoreMessaggiException(this.errore.getDescrizione(this.protocolFactory));
		}

		Element element = null;
		String elementJson = null;
		try{
			String idTransazione = null;
			if(this.pddContext!=null) {
				idTransazione = (String)this.pddContext.getObject(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE);
			}
			boolean bufferMessageReadOnly =  OpenSPCoop2Properties.getInstance().isReadByPathBufferEnabled();
			boolean checkSoapBodyEmpty = false; // devo poter fare xpath anche su soapBody empty
			element = MessageUtils.getContentElement(message, checkSoapBodyEmpty, bufferMessageReadOnly, idTransazione);
			elementJson = MessageUtils.getContentString(message, bufferMessageReadOnly, idTransazione);
		}catch(Exception e){
			throw new GestoreMessaggiException(e.getMessage(),e);
		}
		
		// XPathExpressionEngine
		AbstractXPathExpressionEngine xPathEngine = new org.openspcoop2.message.xml.XPathExpressionEngine(message.getFactory());
		
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
						throw new CoreException("Envelope non presente nel messaggio Soap");
					}
					if(envelope.getBody()==null || !envelope.getBody().hasChildNodes()){
						throw new CoreException("Body applicativo non presente nel messaggio Soap");
					}
					NodeList nListSoapBody = envelope.getBody().getChildNodes();
					if(nListSoapBody==null || nListSoapBody.getLength()==0){
						throw new CoreException("Elementi del Body non presenti?");
					}
					nList = new ArrayList<>();
					for (int elem=0; elem<nListSoapBody.getLength(); elem++){
						Node nodeInEsame =  nListSoapBody.item(elem);
						if(nodeInEsame instanceof Text || nodeInEsame instanceof Comment){
							continue;
						}
						nList.add(nodeInEsame);
					}
				}
				else{
					if(MessageType.XML.equals(message.getMessageType()) || 
							MessageType.MIME_MULTIPART.equals(message.getMessageType()) // viene prelevato il primo xml trovato
							){
						/** il body http puo' essere vuoto
						if(element==null){
							throw new Exception("Contenuto non presente nel messaggio");
						}*/
						if(element!=null) {
							nList = new ArrayList<>();
							nList.add(element);
						}
					}
					else{
						checkElementiInTransito = false;
					}
				}
			}catch(Exception e){
				this.errore = ErroriIntegrazione.ERRORE_434_CORRELAZIONE_APPLICATIVA_RISPOSTA_ERRORE.
						getErrore434_CorrelazioneApplicativaRisposta(ERRORE_IDENTIFICAZIONE_MESSAGE+e.getMessage());
				throw new GestoreMessaggiException(this.errore.getDescrizione(this.protocolFactory),e);
			}
		}

		
		/** Gestioni correlazioni, in modo da avere lo '*' in fondo */
		java.util.List<CorrelazioneApplicativaRispostaElemento> c = new java.util.ArrayList<>();
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
		List<String> nomiPresentiBody = new ArrayList<>();
		if(checkElementiInTransito && nList!=null){
			for (int elem=0; elem<nList.size(); elem++){
				String elementName = null;
				Node nodeInEsame =  nList.get(elem);
				if( (!(nodeInEsame instanceof Text)) && (!(nodeInEsame instanceof Comment)) ){
					try{
						elementName = nodeInEsame.getLocalName();
					}catch(Exception e){
						this.errore = ErroriIntegrazione.ERRORE_434_CORRELAZIONE_APPLICATIVA_RISPOSTA_ERRORE.
								getErrore434_CorrelazioneApplicativaRisposta(ERRORE_IDENTIFICAZIONE_MESSAGE+e.getMessage());
						throw new GestoreMessaggiException(this.errore.getDescrizione(this.protocolFactory),e);
					}
					if(elementName==null){
						continue;
					}
					nomiPresentiBody.add(elementName);
				}
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
					if( elem!=posizioneUltimoNodo ) {
						continue;
					}
					
				}
				else if(
							(
								// Ricerco per match sull'azione
								this.idServizio!=null && this.idServizio.getAzione()!=null &&
								this.idServizio.getAzione().equals(elemento.getNome())
							)
							||
							(
								// Ricerco per match sul localName dell'elemento (se XML)
								elementName.equals(elemento.getNome())
							)
						) {
					
					matchNodePerCorrelazioneApplicativa = true;
					nomeElemento = elemento.getNome();
				}
				else{
					
					// se siamo in REST provo a cercare per metodo e path
					boolean isResourceRest = false;
					if(ServiceBinding.REST.equals(message.getServiceBinding())){
						isResourceRest = isMatchResourceRest(elemento.getNome());
					}
					if(isResourceRest) {
						matchNodePerCorrelazioneApplicativa = true;
						nomeElemento = elemento.getNome();
					}
					else {
					
						// Ricerco elemento con una espressione che potrebbe essere riferita a tutta l'envelope
						try{
							if(element==null && elementJson==null){
								throw new CoreException("Contenuto non disponibile su cui effettuare un match");
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
							String error = "Calcolo non riuscito ["+elementName+"] ["+elemento.getNome()+"]: "+e.getMessage();
							this.log.info(error);
						}
						// Ricerco tramuite espressione regolare sulla url
						// Non possibile sulla risposta
						
					}
				}


				// Correlazione
				if( matchNodePerCorrelazioneApplicativa ){

					// Puo' darsi che questo elemento identificato non abbia bisogna effettuare correlazione applicativa
					if(CostantiConfigurazione.CORRELAZIONE_APPLICATIVA_RISPOSTA_DISABILITATO.equals(elemento.getIdentificazione())){
						correlazioneNonRiuscitaDaAccettare = true;
						findCorrelazione = true;
						break;
					}
					
					
					if(readFirstHeaderIntegrazione &&
						headerIntegrazione.getIdApplicativo()!=null){
						idCorrelazioneApplicativa = headerIntegrazione.getIdApplicativo();
						findCorrelazione = true;
						break;
					}

					if( CostantiConfigurazione.CORRELAZIONE_APPLICATIVA_RISPOSTA_HEADER_BASED.equals(elemento.getIdentificazione())){
						try{
							if(message==null ||
									message.getTransportResponseContext()==null || 
									message.getTransportResponseContext().getHeaders()==null ||
									message.getTransportResponseContext().getHeaders().isEmpty()) {
								throw new CoreException ("headers not found");
							}
							idCorrelazioneApplicativa = message.getTransportResponseContext().getHeaderFirstValue(elemento.getPattern());
							if(idCorrelazioneApplicativa==null) {
								// NOTA: deve essere considerato come vi fosse un errore nel pattern
								throw new GestoreMessaggiException("header not found");
							}
							checkExtractedIdentifierIsEmpty(idCorrelazioneApplicativa, false);
						}catch(Exception e){
							if(bloccaIdentificazioneNonRiuscita){
								this.errore = ErroriIntegrazione.ERRORE_434_CORRELAZIONE_APPLICATIVA_RISPOSTA_ERRORE.
										getErrore434_CorrelazioneApplicativaRisposta(ERRORE_IDENTIFICAZIONE_PER_ELEMENTO+nomeElemento+"] " +
												ERRORE_IDENTIFICAZIONE_PER_ELEMENTO_MODALITA_INPUT_BASED);
								throw new GestoreMessaggiException(this.errore.getDescrizione(this.protocolFactory));
							}else{
								correlazioneNonRiuscitaDaAccettare = true;
							}
						}
					}
					else if( CostantiConfigurazione.CORRELAZIONE_APPLICATIVA_RISPOSTA_INPUT_BASED.equals(elemento.getIdentificazione()) ){
						idCorrelazioneApplicativa = headerIntegrazione.getIdApplicativo();
						if(
								(idCorrelazioneApplicativa==null) // NOTA: deve essere considerato come vi fosse un errore
								|| 
								(StringUtils.isEmpty(idCorrelazioneApplicativa) && this.isRispostaIdentificativoEstrattoIsEmptyThrowError)
							){
							if(bloccaIdentificazioneNonRiuscita){
								this.errore = ErroriIntegrazione.ERRORE_434_CORRELAZIONE_APPLICATIVA_RISPOSTA_ERRORE.
										getErrore434_CorrelazioneApplicativaRisposta(ERRORE_IDENTIFICAZIONE_PER_ELEMENTO+nomeElemento+"] " +
												ERRORE_IDENTIFICAZIONE_PER_ELEMENTO_MODALITA_INPUT_BASED);
								throw new GestoreMessaggiException(this.errore.getDescrizione(this.protocolFactory));
							}else{
								correlazioneNonRiuscitaDaAccettare = true;
							}
						}
					}
					else if( CostantiConfigurazione.CORRELAZIONE_APPLICATIVA_RISPOSTA_TEMPLATE.equals(elemento.getIdentificazione())
							||
							CostantiConfigurazione.CORRELAZIONE_APPLICATIVA_RISPOSTA_FREEMARKER_TEMPLATE.equals(elemento.getIdentificazione())
							||
							CostantiConfigurazione.CORRELAZIONE_APPLICATIVA_RISPOSTA_VELOCITY_TEMPLATE.equals(elemento.getIdentificazione())){
						try{
							
							if(elemento.getPattern()==null || StringUtils.isEmpty(elemento.getPattern())) {
								throw new CoreException ("Template non disponibile");
							}
							
							Map<String, List<String>> pTrasporto = null;
							String urlInvocazione = null;
							Map<String, List<String>> pQuery = null;
							Map<String, List<String>> pForm = null;
							if(this.requestInfo!=null && this.requestInfo.getProtocolContext()!=null) {
								pTrasporto = this.requestInfo.getProtocolContext().getHeaders();
								urlInvocazione = this.requestInfo.getProtocolContext().getUrlInvocazione_formBased();
								pQuery = this.requestInfo.getProtocolContext().getParameters();
								if(this.requestInfo.getProtocolContext() instanceof HttpServletTransportRequestContext) {
									HttpServletTransportRequestContext httpServletContext = this.requestInfo.getProtocolContext();
									HttpServletRequest httpServletRequest = httpServletContext.getHttpServletRequest();
									if(httpServletRequest instanceof FormUrlEncodedHttpServletRequest) {
										FormUrlEncodedHttpServletRequest formServlet = (FormUrlEncodedHttpServletRequest) httpServletRequest;
										if(formServlet.getFormUrlEncodedParametersValues()!=null &&
												!formServlet.getFormUrlEncodedParametersValues().isEmpty()) {
											pForm = formServlet.getFormUrlEncodedParametersValues();
										}
									}
								}
							}
							
							Map<String, List<String>> parametriTrasporto = null;
							if(message.getTransportResponseContext()!=null) {
								if(message.getTransportResponseContext().getHeaders()!=null &&
									!message.getTransportResponseContext().getHeaders().isEmpty()) {
									parametriTrasporto = message.getTransportResponseContext().getHeaders();
								}
								else {
									parametriTrasporto = new HashMap<>();
									message.getTransportResponseContext().setHeaders(parametriTrasporto);
								}
							}

							MessageContent messageContent = null;
							boolean bufferMessageReadOnly =  OpenSPCoop2Properties.getInstance().isReadByPathBufferEnabled();
							if(ServiceBinding.SOAP.equals(message.getServiceBinding())){
								messageContent = new MessageContent(message.castAsSoap(), bufferMessageReadOnly, this.pddContext);
							}
							else{
								if(MessageType.XML.equals(message.getMessageType())){
									messageContent = new MessageContent(message.castAsRestXml(), bufferMessageReadOnly, this.pddContext);
								}
								else if(MessageType.JSON.equals(message.getMessageType())){
									messageContent = new MessageContent(message.castAsRestJson(), bufferMessageReadOnly, this.pddContext);
								}
							}
							
							Map<String, Object> dynamicMapRequest = new HashMap<>();
							ErrorHandler errorHandlerRequest = new ErrorHandler();
							DynamicUtils.fillDynamicMapRequest(this.log, dynamicMapRequest, this.pddContext, urlInvocazione,
									null, //message,
									null, //messageContent, 
									this.busta, 
									pTrasporto, 
									pQuery,
									pForm,
									errorHandlerRequest);
							
							Map<String, Object> dynamicMap = new HashMap<>();
							ErrorHandler errorHandler = new ErrorHandler();
							DynamicUtils.fillDynamicMapResponse(this.log, dynamicMap, dynamicMapRequest, this.pddContext, 
									message,
									messageContent, this.busta, parametriTrasporto,
									errorHandler);
							if(CostantiConfigurazione.CORRELAZIONE_APPLICATIVA_RISPOSTA_TEMPLATE.equals(elemento.getIdentificazione())) {
								idCorrelazioneApplicativa = DynamicUtils.convertDynamicPropertyValue("CorrelazioneApplicativaRisposta.gwt", elemento.getPattern(), dynamicMap, this.pddContext);
								if(idCorrelazioneApplicativa!=null) {
									idCorrelazioneApplicativa = ConditionalUtils.normalizeTemplateResult(idCorrelazioneApplicativa);
								}
							}
							else {
								ByteArrayOutputStream bout = new ByteArrayOutputStream();
								ConfigurazionePdDManager configurazionePdDManager = ConfigurazionePdDManager.getInstance(this.state);
								IDPortaApplicativa idPA = null;
								IDPortaDelegata idPD = null;
								if(this.pa!=null) {
									idPA = new IDPortaApplicativa();
									idPA.setNome(this.pa.getNome());
								}
								else if(this.pd!=null){
									idPD = new IDPortaDelegata();
									idPD.setNome(this.pd.getNome());
								}
								else {
									throw new CoreException ("Porta non disponibile");
								}
								Template template = idPA!=null ?
										configurazionePdDManager.getTemplateCorrelazioneApplicativaRisposta(idPA, elemento.getNome(), elemento.getPattern().getBytes(), this.requestInfo)
										:
										configurazionePdDManager.getTemplateCorrelazioneApplicativaRisposta(idPD, elemento.getNome(), elemento.getPattern().getBytes(), this.requestInfo);
								
								if(CostantiConfigurazione.CORRELAZIONE_APPLICATIVA_RISPOSTA_FREEMARKER_TEMPLATE.equals(elemento.getIdentificazione())) {
									DynamicUtils.convertFreeMarkerTemplate(template, dynamicMap, bout);
								}
								else {
									/** if(CostantiConfigurazione.CORRELAZIONE_APPLICATIVA_RISPOSTA_VELOCITY_TEMPLATE.equals(elemento.getIdentificazione())) { */
									DynamicUtils.convertVelocityTemplate(template, dynamicMap, bout);
								}
								bout.flush();
								bout.close();
								idCorrelazioneApplicativa = bout.toString();
								if(idCorrelazioneApplicativa!=null) {
									idCorrelazioneApplicativa = ConditionalUtils.normalizeTemplateResult(idCorrelazioneApplicativa);
								}
							}

							checkExtractedIdentifierIsNull(idCorrelazioneApplicativa, false);
							checkExtractedIdentifierIsEmpty(idCorrelazioneApplicativa, false);
							
						}catch(Exception e){
							if(bloccaIdentificazioneNonRiuscita){
								this.errore = ErroriIntegrazione.ERRORE_416_CORRELAZIONE_APPLICATIVA_RICHIESTA_ERRORE.
										getErrore416_CorrelazioneApplicativaRichiesta(ERRORE_IDENTIFICAZIONE_NON_IDENTIFICATO_NEL_ELEMENTO+nomeElemento+ERRORE_IDENTIFICAZIONE_NON_IDENTIFICATO_NEL_ELEMENTO_MODALITA_TEMPLATE_BASED+elemento.getIdentificazione().getValue()+"): "+e.getMessage());
								throw new GestoreMessaggiException(this.errore.getDescrizione(this.protocolFactory),e);
							}else{
								correlazioneNonRiuscitaDaAccettare = true;
							}
						}
					}
					else{
						// Content-Based
						try{
							if(ServiceBinding.REST.equals(message.getServiceBinding()) && 
									!MessageType.XML.equals(message.getMessageType()) && 
									!MessageType.JSON.equals(message.getMessageType()) &&
									!MessageType.MIME_MULTIPART.equals(message.getMessageType())){
								throw new CoreException("MessageType ["+message.getMessageType()+"] non supportato con correlazione di tipo '"+
										CostantiConfigurazione.CORRELAZIONE_APPLICATIVA_RICHIESTA_CONTENT_BASED.toString()+"'");
							}
							if(element==null && elementJson==null){
								throw new CoreException("Contenuto non disponibile su cui effettuare una correlazione di tipo '"+
										CostantiConfigurazione.CORRELAZIONE_APPLICATIVA_RICHIESTA_CONTENT_BASED.toString()+"'");
							}
							if(element!=null) {
								idCorrelazioneApplicativa = AbstractXPathExpressionEngine.extractAndConvertResultAsString(element, xPathEngine, elemento.getPattern(), this.log);
							}
							else {
								idCorrelazioneApplicativa = JsonXmlPathExpressionEngine.extractAndConvertResultAsString(elementJson, elemento.getPattern(), this.log);
							}
									
							checkExtractedIdentifierIsNull(idCorrelazioneApplicativa, false);
							checkExtractedIdentifierIsEmpty(idCorrelazioneApplicativa, false);
							
						}catch(Exception e){
							if(bloccaIdentificazioneNonRiuscita){
								this.errore = ErroriIntegrazione.ERRORE_434_CORRELAZIONE_APPLICATIVA_RISPOSTA_ERRORE.
										getErrore434_CorrelazioneApplicativaRisposta("Identificativo di correlazione applicativa non identificato nell'elemento ["+nomeElemento+ERRORE_IDENTIFICAZIONE_NON_IDENTIFICATO_NEL_ELEMENTO_MODALITA_CONTENT_BASED+elemento.getPattern()+"): "+e.getMessage());
								throw new GestoreMessaggiException(this.errore.getDescrizione(this.protocolFactory),e);
							}else{
								correlazioneNonRiuscitaDaAccettare = true;
							}
						}
					}
					
					if(idCorrelazioneApplicativa!=null && idCorrelazioneApplicativa.length()>this.maxLengthCorrelazioneApplicativa) {
						if(bloccaIdentificazioneNonRiuscita) {
							if(this.isTruncateEnabled(RISPOSTA, BLOCCA)) {
								idCorrelazioneApplicativa = this.truncate(idCorrelazioneApplicativa, RISPOSTA, BLOCCA);
							}
							else {
								this.errore = ErroriIntegrazione.ERRORE_434_CORRELAZIONE_APPLICATIVA_RISPOSTA_ERRORE.
										getErrore434_CorrelazioneApplicativaRisposta(this.buildErroreLunghezzaIdentificativo(idCorrelazioneApplicativa));
								throw new GestoreMessaggiException(this.errore.getDescrizione(this.protocolFactory));
							}
						}
						else {
							if(this.isTruncateEnabled(RISPOSTA, ACCETTA)) {
								idCorrelazioneApplicativa = this.truncate(idCorrelazioneApplicativa, RISPOSTA, ACCETTA);
							}
							else {
								String errorId = this.buildErroreLunghezzaIdentificativo(idCorrelazioneApplicativa);
								this.log.error(errorId);
								correlazioneNonRiuscitaDaAccettare = true;
								idCorrelazioneApplicativa = null;
							}
						}
					}
					
					findCorrelazione = true;
					break;
				}
			}
		}

		if(idCorrelazioneApplicativa == null){
			boolean generaErrore = false;
			if( !findCorrelazione ) {
				generaErrore = this.isRispostaRegolaCorrelazioneNonTrovataBlocca;
			}
			else {
				generaErrore = !correlazioneNonRiuscitaDaAccettare;
			}
			if( generaErrore ){
				this.errore = ErroriIntegrazione.ERRORE_434_CORRELAZIONE_APPLICATIVA_RISPOSTA_ERRORE.
						getErrore434_CorrelazioneApplicativaRisposta("Identificativo di correlazione applicativa non identificato; nessun elemento tra quelli di correlazione definiti, sono presenti nel body");
				throw new GestoreMessaggiException(this.errore.getDescrizione(this.protocolFactory));
			}
		}else{
			this.idCorrelazione = idCorrelazioneApplicativa;
			if(this.pddContext!=null) {
				this.pddContext.addObject(CONTEXT_CORRELAZIONE_APPLICATIVA_RISPOSTA, this.idCorrelazione);
			}
		}

	}
	
	
	
	
	
	/* *********** INDIVIDUAZIONE RISORSA REST ************ */
	
	private boolean isMatchResourceRest(String elemento) {
		boolean isResourceRest = false;
		if(elemento!=null && !"".equals(elemento)) {
			String [] parseResourceRest = Utilities.parseResourceRest(elemento);
			if(parseResourceRest!=null) {
				this.initRestResource();
				if(this.restResource!=null) {
					isResourceRest = Utilities.isRestResourceMatch(parseResourceRest, this.restResource);
				}
			}
		}
		return isResourceRest;
	}
	
	private void initRestResource() {
		if(this.restResource!=null) {
			return;
		}
		this.restResource = Utilities.getRestResource(this.log, this.state, this.idServizio, this.requestInfo);
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
		GestoreCorrelazioneApplicativaPSUtilities.applicaCorrelazione(this, correlazioneApplicativa, idApplicativo, idBustaRequest);
	}



	/**
	 * Cerca nella tabella CORRELAZIONE_APPLICATIVA le correlazioni scadute 
	 *
	 * @return Nel caso l'operazione ha successo ritorna gli id delle tabelle delle correlazioni scadute
	 */
	private static final String COLUMN_SCADENZA = "SCADENZA";
	private static final String COLUMN_ORA_REGISTRAZIONE = "ORA_REGISTRAZIONE";
	public java.util.List<Long> getCorrelazioniScadute(int limit,boolean logQuery,boolean orderBy) throws GestoreMessaggiException{

		java.util.List<Long> idMsg = new java.util.ArrayList<>();

		PreparedStatement pstmtMsgScaduti = null;
		ResultSet rs = null;
		String queryString = null;
		try{	
			StateMessage stateMSG = (StateMessage)this.state;
			Connection connectionDB = stateMSG.getConnectionDB();

			/** Query per Ricerca messaggi scaduti
			 Algoritmo:
			    if( scaduto < now )
			       msgScaduto
			 */
			java.sql.Timestamp nowT = DateManager.getTimestamp();


			queryString = buildQueryCorrelazioniScadute(limit, orderBy);
			/** System.out.println("QUERY CORRELAZIONE APPLICATIVA IS: ["+queryString+"] 1["+nowT+"]"); */


			pstmtMsgScaduti = connectionDB.prepareStatement(queryString);
			pstmtMsgScaduti.setTimestamp(1,nowT);

			long startDateSQLCommand = DateManager.getTimeMillis();
			if(logQuery) {
				String debugS = "[QUERY] (CorrelazioneApplicativa.scaduta) ["+queryString+"] 1["+nowT+"]...";
				this.log.debug(debugS);
			}
			rs = pstmtMsgScaduti.executeQuery();
			long endDateSQLCommand = DateManager.getTimeMillis();
			long secondSQLCommand = (endDateSQLCommand - startDateSQLCommand) / 1000;
			if(logQuery) {
				String debugS = "[QUERY] (CorrelazioneApplicativa.scaduta) ["+queryString+"] 1["+nowT+"] effettuata in "+secondSQLCommand+" secondi"; 
				this.log.debug(debugS);
			}

			while(rs.next()){
				if(!readCorrelazioniScaduteEngine(idMsg, rs, limit)) {
					break;
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
			} catch(Exception er) {
				// close
			}
			try{
				if(pstmtMsgScaduti != null)
					pstmtMsgScaduti.close();
			} catch(Exception er) {
				// close
			}
			this.log.error(errorMsg);
			throw new GestoreMessaggiException(errorMsg,e);
		}	
	}
	private String buildQueryCorrelazioniScadute(int limit,boolean orderBy) throws SQLQueryObjectException{
		String queryString = null;
		if(Configurazione.getSqlQueryObjectType()==null){
			StringBuilder query = new StringBuilder();
			query.append("SELECT id FROM ");
			query.append(GestoreCorrelazioneApplicativa.CORRELAZIONE_APPLICATIVA);
			query.append(" WHERE "+COLUMN_SCADENZA+" is not null AND "+COLUMN_SCADENZA+" < ?");
			queryString = query.toString();
		}else{
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(Configurazione.getSqlQueryObjectType());	
			sqlQueryObject.addSelectField("id");
			sqlQueryObject.addSelectField(COLUMN_SCADENZA);
			sqlQueryObject.addFromTable(GestoreCorrelazioneApplicativa.CORRELAZIONE_APPLICATIVA);
			sqlQueryObject.addWhereCondition(COLUMN_SCADENZA+" is not null");
			sqlQueryObject.addWhereCondition(COLUMN_SCADENZA+" < ?");
			sqlQueryObject.setANDLogicOperator(true);
			if(orderBy){
				sqlQueryObject.addOrderBy(COLUMN_SCADENZA);
				sqlQueryObject.setSortType(true);
			}
			sqlQueryObject.setLimit(limit);
			queryString = sqlQueryObject.createSQLQuery();
		}
		return queryString;
	}
	
	
	/**
	 * Cerca nella tabella CORRELAZIONE_APPLICATIVA le correlazioni 'vecchie' (rispetto all'ora di registrazione)
	 *
	 * @return Nel caso l'operazione ha successo ritorna gli id delle tabelle delle correlazioni 'vecchie' (rispetto all'ora di registrazione) 
	 */
	public java.util.List<Long> getCorrelazioniScaduteRispettoOraRegistrazione(int limit,long scadenzaMsg,boolean logQuery,boolean orderBy,boolean escludiCorrelazioniConScadenza) throws GestoreMessaggiException{

		java.util.List<Long> idMsg = new java.util.ArrayList<>();
		
		PreparedStatement pstmtMsgScaduti = null;
		ResultSet rs = null;
		String queryString = null;
		try{	
			StateMessage stateMSG = (StateMessage)this.state;
			Connection connectionDB = stateMSG.getConnectionDB();

			/** Query per Ricerca messaggi scaduti
			 Algoritmo:
			    if( (now-timeout) > oraRegistrazione )
			       msgScaduto
			 */
			long scadenza = DateManager.getTimeMillis() - (scadenzaMsg * 60 * 1000);
			java.sql.Timestamp scandenzaT = new java.sql.Timestamp(scadenza);

			queryString = buildQueryCorrelazioniScaduteRispettoOraRegistrazione(limit, orderBy, escludiCorrelazioniConScadenza);
			/** System.out.println("QUERY CORRELAZIONE APPLICATIVA IS: ["+queryString+"] 1["+nowT+"]"); */


			pstmtMsgScaduti = connectionDB.prepareStatement(queryString);
			pstmtMsgScaduti.setTimestamp(1,scandenzaT);

			long startDateSQLCommand = DateManager.getTimeMillis();
			if(logQuery) {
				String sDebug = "[QUERY] (CorrelazioneApplicativa.storiche) ["+queryString+"] 1["+scandenzaT+"]...";
				this.log.debug(sDebug);
			}
			rs = pstmtMsgScaduti.executeQuery();
			long endDateSQLCommand = DateManager.getTimeMillis();
			long secondSQLCommand = (endDateSQLCommand - startDateSQLCommand) / 1000;
			if(logQuery) {
				String sDebug = "[QUERY] (CorrelazioneApplicativa.storiche) ["+queryString+"] 1["+scandenzaT+"] effettuata in "+secondSQLCommand+" secondi";
				this.log.debug(sDebug);
			}

			while(rs.next()){
				if(!readCorrelazioniScaduteEngine(idMsg, rs, limit)) {
					break;
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
			} catch(Exception er) {
				// close
			}
			try{
				if(pstmtMsgScaduti != null)
					pstmtMsgScaduti.close();
			} catch(Exception er) {
				// close
			}
			this.log.error(errorMsg);
			throw new GestoreMessaggiException(errorMsg,e);
		}	
	}
	private String buildQueryCorrelazioniScaduteRispettoOraRegistrazione(int limit,boolean orderBy, boolean escludiCorrelazioniConScadenza) throws SQLQueryObjectException{
		String queryString = null;
		if(Configurazione.getSqlQueryObjectType()==null){
			StringBuilder query = new StringBuilder();
			query.append("SELECT id FROM ");
			query.append(GestoreCorrelazioneApplicativa.CORRELAZIONE_APPLICATIVA);
			query.append(" WHERE "+COLUMN_ORA_REGISTRAZIONE+" < ?");
			if(escludiCorrelazioniConScadenza){
				query.append(" AND "+COLUMN_SCADENZA+" is null");
			}
			queryString = query.toString();
		}else{
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(Configurazione.getSqlQueryObjectType());	
			sqlQueryObject.addSelectField("id");
			sqlQueryObject.addSelectField(COLUMN_SCADENZA);
			sqlQueryObject.addFromTable(GestoreCorrelazioneApplicativa.CORRELAZIONE_APPLICATIVA);
			sqlQueryObject.addWhereCondition(COLUMN_ORA_REGISTRAZIONE+" < ?");
			if(escludiCorrelazioniConScadenza){
				sqlQueryObject.addWhereCondition(COLUMN_SCADENZA+" is null");
			}
			sqlQueryObject.setANDLogicOperator(true);
			if(orderBy){
				sqlQueryObject.addOrderBy(COLUMN_ORA_REGISTRAZIONE);
				sqlQueryObject.setSortType(true);
			}
			sqlQueryObject.setLimit(limit);
			queryString = sqlQueryObject.createSQLQuery();
		}
		return queryString;
	}
	
	private boolean readCorrelazioniScaduteEngine(java.util.List<Long> idMsg, ResultSet rs, int limit) throws SQLException {
		int countLimit = 0;
		if(Configurazione.getSqlQueryObjectType()==null){
			// LIMIT Applicativo
			idMsg.add(rs.getLong("id"));
			countLimit++;
			if(countLimit==limit) {
				return false;
			}
		}
		else{
			idMsg.add(rs.getLong("id"));
		}
		return true;
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

			String query = "SELECT "+GestoreCorrelazioneApplicativa.CORRELAZIONE_APPLICATIVA_COLUMN_ID_MESSAGGIO+","+GestoreCorrelazioneApplicativa.CORRELAZIONE_APPLICATIVA_COLUMN_ID_APPLICATIVO+"  FROM "+GestoreCorrelazioneApplicativa.CORRELAZIONE_APPLICATIVA+" WHERE id=?";
			/** log.debug("Query: "+query); */
			pstmtReadMSG=connectionDB.prepareStatement(query);
			pstmtReadMSG.setLong(1,idCorrelazioneApplicativa);
			rs = pstmtReadMSG.executeQuery();
			if(rs.next()){
				String [] s = new String[2];
				s[0] = rs.getString(GestoreCorrelazioneApplicativa.CORRELAZIONE_APPLICATIVA_COLUMN_ID_MESSAGGIO);
				s[1] = rs.getString(GestoreCorrelazioneApplicativa.CORRELAZIONE_APPLICATIVA_COLUMN_ID_APPLICATIVO);
				rs.close();
				pstmtReadMSG.close();
				return s;
			}
			rs.close();
			pstmtReadMSG.close();
			throw new CoreException("CorrelazioneApplicativa con id["+idCorrelazioneApplicativa+"] non trovata");
		}
		catch(Exception e) {
			String errorMsg = "GestoreCorrelazioneApplicativa, error getIDMappingCorrelazioneApplicativa ["+idCorrelazioneApplicativa+"] : "+e.getMessage();		
			try{
				if(rs != null)
					rs.close();
			} catch(Exception er) {
				// close
			}
			try{
				if(pstmtReadMSG != null)
					pstmtReadMSG.close();
			} catch(Exception er) {
				// close
			}
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
			/** log.debug("Query: "+query); */
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
			} catch(Exception er) {
				// close
			}
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

	private static final boolean RICHIESTA = true;
	private static final boolean RISPOSTA = false;
	private static final boolean BLOCCA = true;
	private static final boolean ACCETTA = false;
	public boolean isTruncateEnabled(boolean request, boolean blocca) {
		if(blocca) {
			if(request) {
				return this.maxLengthExceededCorrelazioneApplicativaIdentificazioneFallitaBloccaTruncateRequest>0 && 
						this.maxLengthExceededCorrelazioneApplicativaIdentificazioneFallitaBloccaTruncateRequest<this.maxLengthCorrelazioneApplicativa;
			}
			else {
				return this.maxLengthExceededCorrelazioneApplicativaIdentificazioneFallitaBloccaTruncateResponse>0 && 
						this.maxLengthExceededCorrelazioneApplicativaIdentificazioneFallitaBloccaTruncateResponse<this.maxLengthCorrelazioneApplicativa;
			}
		}
		else {
			if(request) {
				return this.maxLengthExceededCorrelazioneApplicativaIdentificazioneFallitaAccettaTruncateRequest>0 && 
						this.maxLengthExceededCorrelazioneApplicativaIdentificazioneFallitaAccettaTruncateRequest<this.maxLengthCorrelazioneApplicativa;
			}
			else {
				return this.maxLengthExceededCorrelazioneApplicativaIdentificazioneFallitaAccettaTruncateResponse>0 && 
						this.maxLengthExceededCorrelazioneApplicativaIdentificazioneFallitaAccettaTruncateResponse<this.maxLengthCorrelazioneApplicativa;
			}
		}
	}
	public String truncate(String id, boolean request, boolean blocca) {
		if(blocca) {
			if(request) {
				return id.substring(0, this.maxLengthExceededCorrelazioneApplicativaIdentificazioneFallitaBloccaTruncateRequest);
			}
			else {
				return id.substring(0, this.maxLengthExceededCorrelazioneApplicativaIdentificazioneFallitaBloccaTruncateResponse);
			}
		}
		else {
			if(request) {
				return id.substring(0, this.maxLengthExceededCorrelazioneApplicativaIdentificazioneFallitaAccettaTruncateRequest);
			}
			else {
				return id.substring(0, this.maxLengthExceededCorrelazioneApplicativaIdentificazioneFallitaAccettaTruncateResponse);
			}
		}
	}
}

