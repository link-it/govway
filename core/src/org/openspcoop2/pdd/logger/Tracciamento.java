/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it). 
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




package org.openspcoop2.pdd.logger;

import java.io.ByteArrayOutputStream;
import java.sql.Connection;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import javax.mail.BodyPart;
import javax.xml.soap.AttachmentPart;
import javax.xml.soap.SOAPBody;

import org.openspcoop2.core.constants.TipoPdD;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.constants.MessageRole;
import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.message.soap.SoapUtils;
import org.openspcoop2.pdd.config.ConfigurazionePdDManager;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.core.CostantiPdD;
import org.openspcoop2.pdd.core.PdDContext;
import org.openspcoop2.pdd.core.connettori.ConnettoreUtils;
import org.openspcoop2.pdd.core.handlers.GeneratoreCasualeDate;
import org.openspcoop2.pdd.core.transazioni.RepositoryGestioneStateful;
import org.openspcoop2.pdd.core.transazioni.Transaction;
import org.openspcoop2.pdd.core.transazioni.TransactionContext;
import org.openspcoop2.pdd.core.transazioni.TransactionDeletedException;
import org.openspcoop2.pdd.core.transazioni.TransactionNotExistsException;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.engine.builder.TracciaBuilder;
import org.openspcoop2.protocol.sdk.Allegato;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.BustaRawContent;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.SecurityInfo;
import org.openspcoop2.protocol.sdk.constants.RuoloMessaggio;
import org.openspcoop2.protocol.sdk.constants.TipoSerializzazione;
import org.openspcoop2.protocol.sdk.state.IState;
import org.openspcoop2.protocol.sdk.state.StateMessage;
import org.openspcoop2.protocol.sdk.tracciamento.EsitoElaborazioneMessaggioTracciato;
import org.openspcoop2.protocol.sdk.tracciamento.ITracciaProducer;
import org.openspcoop2.protocol.sdk.tracciamento.Traccia;
import org.openspcoop2.protocol.sdk.tracciamento.TracciamentoException;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.mime.MimeMultipart;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.slf4j.Logger;


/**
 * Contiene la definizione un Logger utilizzato dai nodi dell'infrastruttura di OpenSPCoop
 * per il tracciamento di buste.
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class Tracciamento {

	/** Indicazione di un tracciamento funzionante */
	public static boolean tracciamentoDisponibile = true;
	/** Primo errore avvenuto nel momento in cui è stato rilevato un malfunzionamento nel tracciamento */
	public static Exception motivoMalfunzionamentoTracciamento = null;
	
	/**  Logger log4j utilizzato per scrivere i tracciamenti */
	protected Logger loggerTracciamento = null;

	/** Tipo Gateway */
	private TipoPdD tipoPdD;
	
	/** Soggetto che richiede il logger */
	private IDSoggetto idSoggettoDominio;
	
	/** PdDContext */
	private PdDContext pddContext;
	
	/** Protocol Factory */
	private ProtocolFactoryManager protocolFactoryManager = null;
	private IProtocolFactory<?> protocolFactory;
	private boolean tracciamentoSupportatoProtocollo = true;
	
	/** Appender personalizzati per i tracciamenti di OpenSPCoop */
	private List<ITracciaProducer> loggerTracciamentoOpenSPCoopAppender = null; 
	private List<String> tipoTracciamentoOpenSPCoopAppender = null; 

	/** Reader della configurazione di OpenSPCoop */
	private ConfigurazionePdDManager _configurazionePdDReader;
	
	/** Stati */
	private StateMessage state = null;
	private StateMessage responseState = null;
	
	/** XMLBuilder */
	private TracciaBuilder xmlBuilder;

	/** MsgDiagnostico per eventuali errori di tracciamento */
	private MsgDiagnostico msgDiagErroreTracciamento = null;
	
	/** OpenSPCoopProperties */
	private OpenSPCoop2Properties openspcoopProperties = null;
	
	/** Generatore di date casuali*/
	private GeneratoreCasualeDate generatoreDateCasuali = null;
	
	/** Transaction */
	private Transaction transactionNullable = null;
	
	public static String createLocationString(boolean bustaRicevuta,String location){
		if(bustaRicevuta)
			return ConnettoreUtils.limitLocation255Character(CostantiPdD.TRACCIAMENTO_IN+HttpConstants.SEPARATOR_SOURCE+location);
		else
			return ConnettoreUtils.limitLocation255Character(CostantiPdD.TRACCIAMENTO_OUT+HttpConstants.SEPARATOR_SOURCE+location);
	}
	
	
	public Tracciamento(IDSoggetto idSoggettoDominio,String idFunzione,PdDContext pddContext,TipoPdD tipoPdD,String nomePorta, 
			ConfigurazionePdDManager configurazionePdDManager) throws TracciamentoException {
		this(idSoggettoDominio, idFunzione, pddContext, tipoPdD, nomePorta, 
				configurazionePdDManager, null, null);	
	}
	/*
	public Tracciamento(IDSoggetto idSoggettoDominio,String idFunzione,PdDContext pddContext,TipoPdD tipoPdD,String nomePorta, 
			IState statoRichiesta,IState statoRisposta) throws TracciamentoException {
		this(idSoggettoDominio, idFunzione, pddContext, tipoPdD, nomePorta, 
				null, statoRichiesta, statoRisposta);	
	}
	*/
	private Tracciamento(IDSoggetto idSoggettoDominio,String idFunzione,PdDContext pddContext,TipoPdD tipoPdD,String nomePorta, 
			ConfigurazionePdDManager configurazionePdDManagerParam, IState stateParam,IState responseStateParam) throws TracciamentoException {
		this.idSoggettoDominio = idSoggettoDominio;
		this.pddContext=pddContext;
		this.tipoPdD = tipoPdD;
		
		this.loggerTracciamento = OpenSPCoop2Logger.loggerTracciamento;
		this.loggerTracciamentoOpenSPCoopAppender = OpenSPCoop2Logger.loggerTracciamentoOpenSPCoopAppender;
		this.tipoTracciamentoOpenSPCoopAppender = OpenSPCoop2Logger.tipoTracciamentoOpenSPCoopAppender;
		
		if(configurazionePdDManagerParam!=null) {
			this._configurazionePdDReader = configurazionePdDManagerParam;
			this.state = this._configurazionePdDReader.getState();
			this.responseState = this._configurazionePdDReader.getResponseState();
		}
		else {
			if(stateParam!=null && stateParam instanceof StateMessage){
				this.state = (StateMessage) stateParam;
			}
			if(responseStateParam!=null && responseStateParam instanceof StateMessage){
				this.responseState = (StateMessage) responseStateParam;
			}
			this._configurazionePdDReader = ConfigurazionePdDManager.getInstance(this.state, this.responseState);
		}
		
		this.msgDiagErroreTracciamento = MsgDiagnostico.newInstance(tipoPdD,idSoggettoDominio,idFunzione,nomePorta,this._configurazionePdDReader);
		this.msgDiagErroreTracciamento.setPrefixMsgPersonalizzati(MsgDiagnosticiProperties.MSG_DIAG_TRACCIAMENTO);
		try{
			this.protocolFactoryManager = ProtocolFactoryManager.getInstance();
			this.protocolFactory = this.protocolFactoryManager.getProtocolFactoryByName((String) this.pddContext.getObject(org.openspcoop2.core.constants.Costanti.PROTOCOL_NAME));
			this.tracciamentoSupportatoProtocollo = this.protocolFactory.createProtocolConfiguration().isAbilitataGenerazioneTracce();
		} catch(Throwable e){
			throw new TracciamentoException(e.getMessage(),e);
		}
		this.xmlBuilder = new TracciaBuilder(this.protocolFactory);
		this.openspcoopProperties = OpenSPCoop2Properties.getInstance();
		if(this.openspcoopProperties.generazioneDateCasualiLogAbilitato()){
			this.generatoreDateCasuali = GeneratoreCasualeDate.getGeneratoreCasualeDate();
		}
		
		try{
			if(this.pddContext!=null && this.pddContext.containsKey(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE)) {
				String idTransazione = (String) this.pddContext.getObject(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE);
				this.transactionNullable = TransactionContext.getTransaction(idTransazione);
			}
		}catch(Exception e){
			// La transazione potrebbe essere stata eliminata nelle comunicazioni stateful
		}
	}
	
	private ConfigurazionePdDManager getConfigurazionePdDManager() {
		if(this._configurazionePdDReader!=null) {
			return this._configurazionePdDReader;
		}
		if(this.state!=null || this.responseState!=null) {
			return ConfigurazionePdDManager.getInstance(this.state, this.responseState);
		}
		return ConfigurazionePdDManager.getInstance();
	}
	

	public void updateState(IState requestStateParam, IState responseStateParam) {
		StateMessage requestState = null;
		StateMessage responseState = null;
		if(requestStateParam!=null && requestStateParam instanceof StateMessage) {
			requestState = (StateMessage) requestStateParam;
		}
		if(responseStateParam!=null && responseStateParam instanceof StateMessage) {
			responseState = (StateMessage) responseStateParam;
		}
		updateState(requestState, responseState);
	}
	public void updateState(StateMessage requestState, StateMessage responseState){
		this.state = requestState;
		this.responseState = responseState;
		if(this.state!=null || this.responseState!=null) {
			if(this._configurazionePdDReader!=null) {
				this._configurazionePdDReader = this._configurazionePdDReader.refreshState(this.state, this.responseState);
			}
			else {
				this._configurazionePdDReader = ConfigurazionePdDManager.getInstance(this.state, this.responseState);
			}
		}
		else {
			this._configurazionePdDReader = ConfigurazionePdDManager.getInstance();
		}
	}
	public void updateState(ConfigurazionePdDManager configurazionePdDManager){
		this._configurazionePdDReader = configurazionePdDManager;
		if(this._configurazionePdDReader!=null) {
			this.state = this._configurazionePdDReader.getState();
			this.responseState = this._configurazionePdDReader.getResponseState();
		}
	}
	


	/**
	 * Il Metodo si occupa di impostare il dominio del Soggetto che utilizza il logger. 
	 *
	 * @param dominio Soggetto che richiede il logger
	 * 
	 */
	public void setDominio(IDSoggetto dominio){
		this.idSoggettoDominio = dominio;
	}


	private Connection getConnectionFromState(boolean richiesta){
		if(richiesta){
			Connection c = StateMessage.getConnection(this.state);
			if(c!=null) {
				return c;
			}
		}
		else{
			Connection c = StateMessage.getConnection(this.responseState);
			if(c!=null) {
				return c;
			}
		}
		return null;
	}
	
	




	/** ----------------- METODI DI LOGGING (Tracciamento buste) ---------------- */

	/**
	 * Il Metodo si occupa di tracciare una busta di richiesta.
	 *
	 * @param busta Busta da registrare
	 * 
	 */
	@Deprecated
	public void registraRichiesta(OpenSPCoop2Message msg,SecurityInfo securityInfo,
			Busta busta,EsitoElaborazioneMessaggioTracciato esito,String location) throws TracciamentoException {
		registraRichiesta(msg,securityInfo,busta,esito,location,null);
	}
	@Deprecated
	public void registraRichiesta(OpenSPCoop2Message msg,SecurityInfo securityInfo,
			Busta busta,EsitoElaborazioneMessaggioTracciato esito,String location, 
			String idCorrelazioneApplicativa) throws TracciamentoException {
		
		if(this.transactionNullable!=null) {
			this.transactionNullable.getTempiElaborazione().startTracciamentoRichiesta();
		}
		try {
			if(this.tracciamentoSupportatoProtocollo && this.getConfigurazionePdDManager().tracciamentoBuste()){
				String xml = null;
				boolean erroreAppender = false;
				
				// Data
				Date gdo = DateManager.getDate();
				if(this.openspcoopProperties.generazioneDateCasualiLogAbilitato() && this.pddContext!=null && this.pddContext.getObject(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE)!=null){
					gdo = this.generatoreDateCasuali.getProssimaData((String)this.pddContext.getObject(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE));
					busta.setOraRegistrazione(gdo);
				}
				
				// Traccia
				Traccia traccia = this.getTraccia(busta, msg,securityInfo, esito, gdo, RuoloMessaggio.RICHIESTA, location, idCorrelazioneApplicativa);
				
				try{
					
					// Miglioramento performance
					if(OpenSPCoop2Logger.loggerTracciamentoAbilitato){
						xml = this.xmlBuilder.toString(traccia,TipoSerializzazione.DEFAULT);
						if(xml==null)
							throw new Exception("Traccia non costruita");
						this.loggerTracciamento.info(xml);
					}
	
					// TransazioneContext
					if(this.openspcoopProperties.isTransazioniSaveTracceInUniqueTransaction()) {
						this.logTracciaInTransactionContext(traccia, true);
					}
					
					//	Tracciamento personalizzato
					for(int i=0; i<this.loggerTracciamentoOpenSPCoopAppender.size();i++){
						try{
							this.loggerTracciamentoOpenSPCoopAppender.get(i).log(getConnectionFromState(true), traccia);
						}catch(Exception e){
							logError("Errore durante il tracciamento personalizzato ["+this.tipoTracciamentoOpenSPCoopAppender.get(i)+"] della richiesta: "+e.getMessage()+". Traccia non registrata:\n"+xml,e);
							try{
								this.msgDiagErroreTracciamento.addKeyword(CostantiPdD.KEY_TRACCIA_TIPO, RuoloMessaggio.RICHIESTA.toString());
								this.msgDiagErroreTracciamento.addKeyword(CostantiPdD.KEY_TRACCIA, xml);
								this.msgDiagErroreTracciamento.addKeyword(CostantiPdD.KEY_TRACCIAMENTO_PERSONALIZZATO,this.tipoTracciamentoOpenSPCoopAppender.get(i));
								this.msgDiagErroreTracciamento.addKeyword(CostantiPdD.KEY_TRACCIAMENTO_ERRORE,e.getMessage());
								this.msgDiagErroreTracciamento.logPersonalizzato("registrazioneNonRiuscita.openspcoopAppender");
							}catch(Exception eMsg){}
							if(this.openspcoopProperties.isTracciaturaFallita_BloccaCooperazioneInCorso()){
								erroreAppender = true;
								throw e; // Rilancio
							}
						}
					}
				}catch(Exception e){
					// check eventuale costruzione dell'xml non fatto per log4j disabilitato.
					if( (xml==null) && (OpenSPCoop2Logger.loggerTracciamentoAbilitato==false) ){
						try{
							xml = this.xmlBuilder.toString(traccia,TipoSerializzazione.DEFAULT);
						}catch(Exception eBuild){}
					}
					if(xml==null){
						logError("Errore durante la costruzione della traccia: "+e.getMessage(),e);
					}else{
						logError("Errore durante il tracciamento della richiesta: "+e.getMessage()+". Traccia non registrata:\n"+xml,e);
						if(!erroreAppender){
							try{
								this.msgDiagErroreTracciamento.addKeyword(CostantiPdD.KEY_TRACCIA_TIPO, RuoloMessaggio.RICHIESTA.toString());
								this.msgDiagErroreTracciamento.addKeyword(CostantiPdD.KEY_TRACCIA, xml);
								this.msgDiagErroreTracciamento.addKeyword(CostantiPdD.KEY_TRACCIAMENTO_ERRORE,e.getMessage());
								this.msgDiagErroreTracciamento.logPersonalizzato("registrazioneNonRiuscita");
							}catch(Exception eMsg){}
						}
					}
					gestioneErroreTracciamento(e);
				}
			}
		}finally {
			if(this.transactionNullable!=null) {
				this.transactionNullable.getTempiElaborazione().endTracciamentoRichiesta();
			}
		}
	}
	/**
	 * Il Metodo si occupa di tracciare una busta di richiesta.
	 *
	 * @param busta Busta da registrare in byte
	 * @param bustaObject Busta da registrare (per appender personalizzati)
	 * 
	 */
	@Deprecated
	public void registraRichiesta(OpenSPCoop2Message msg,SecurityInfo securityInfo,
			byte[] busta,Busta bustaObject,EsitoElaborazioneMessaggioTracciato esito,String location) throws TracciamentoException {
		registraRichiesta(msg,securityInfo,busta,bustaObject,esito,location,null);
	}
	@Deprecated
	public void registraRichiesta(OpenSPCoop2Message msg,SecurityInfo securityInfo,
			byte[] busta,Busta bustaObject,EsitoElaborazioneMessaggioTracciato esito,String location, 
			String idCorrelazioneApplicativa) throws TracciamentoException {
		if(this.transactionNullable!=null) {
			this.transactionNullable.getTempiElaborazione().startTracciamentoRichiesta();
		}
		try {
			if(this.tracciamentoSupportatoProtocollo && this.getConfigurazionePdDManager().tracciamentoBuste()){
				String xml = null;
				boolean erroreAppender = false;
				
				// Data
				Date gdo = DateManager.getDate();
				if(this.openspcoopProperties.generazioneDateCasualiLogAbilitato() && this.pddContext!=null && this.pddContext.getObject(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE)!=null){
					gdo = this.generatoreDateCasuali.getProssimaData((String)this.pddContext.getObject(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE));
					bustaObject.setOraRegistrazione(gdo);
				}
				
				// Traccia
				Traccia traccia = this.getTraccia(bustaObject, msg, securityInfo, esito, gdo, RuoloMessaggio.RICHIESTA, location, idCorrelazioneApplicativa);
				traccia.setBustaAsByteArray(busta);
				
				try{
			
					// Miglioramento performance
					if(OpenSPCoop2Logger.loggerTracciamentoAbilitato){
						xml = this.xmlBuilder.toString(traccia,TipoSerializzazione.DEFAULT);
						if(xml==null)
							throw new Exception("Traccia non costruita");
						this.loggerTracciamento.info(xml);
					}
					
					// TransazioneContext
					if(this.openspcoopProperties.isTransazioniSaveTracceInUniqueTransaction()) {
						this.logTracciaInTransactionContext(traccia, true);
					}
						
					// Tracciamento personalizzato
					for(int i=0; i<this.loggerTracciamentoOpenSPCoopAppender.size();i++){
						try{
							this.loggerTracciamentoOpenSPCoopAppender.get(i).log(getConnectionFromState(true), traccia);
						}catch(Exception e){
							logError("Errore durante il tracciamento personalizzato ["+this.tipoTracciamentoOpenSPCoopAppender.get(i)+"] della richiesta: "+e.getMessage()+". Traccia non registrata:\n"+xml,e);
							try{
								this.msgDiagErroreTracciamento.addKeyword(CostantiPdD.KEY_TRACCIA_TIPO, RuoloMessaggio.RICHIESTA.toString());
								this.msgDiagErroreTracciamento.addKeyword(CostantiPdD.KEY_TRACCIA, xml);
								this.msgDiagErroreTracciamento.addKeyword(CostantiPdD.KEY_TRACCIAMENTO_PERSONALIZZATO,this.tipoTracciamentoOpenSPCoopAppender.get(i));
								this.msgDiagErroreTracciamento.addKeyword(CostantiPdD.KEY_TRACCIAMENTO_ERRORE,e.getMessage());
								this.msgDiagErroreTracciamento.logPersonalizzato("registrazioneNonRiuscita.openspcoopAppender");
							}catch(Exception eMsg){}
							if(this.openspcoopProperties.isTracciaturaFallita_BloccaCooperazioneInCorso()){
								erroreAppender = true;
								throw e; // Rilancio
							}
						}
					}
				}catch(Exception e){
					// check eventuale costruzione dell'xml non fatto per log4j disabilitato.
					if( (xml==null) && (OpenSPCoop2Logger.loggerTracciamentoAbilitato==false) ){
						try{
							xml = this.xmlBuilder.toString(traccia,TipoSerializzazione.DEFAULT);
						}catch(Exception eBuild){}
					}
					if(xml==null){
						logError("Errore durante la costruzione della traccia: "+e.getMessage(),e);
					}else{
						logError("Errore durante il tracciamento della richiesta: "+e.getMessage()+". Traccia non registrata:\n"+xml,e);
						if(!erroreAppender){
							try{
								this.msgDiagErroreTracciamento.addKeyword(CostantiPdD.KEY_TRACCIA_TIPO, RuoloMessaggio.RICHIESTA.toString());
								this.msgDiagErroreTracciamento.addKeyword(CostantiPdD.KEY_TRACCIA, xml);
								this.msgDiagErroreTracciamento.addKeyword(CostantiPdD.KEY_TRACCIAMENTO_ERRORE,e.getMessage());
								this.msgDiagErroreTracciamento.logPersonalizzato("registrazioneNonRiuscita");
							}catch(Exception eMsg){}
						}
					}
					gestioneErroreTracciamento(e);
				}
			}
		}finally {
			if(this.transactionNullable!=null) {
				this.transactionNullable.getTempiElaborazione().endTracciamentoRichiesta();
			}
		}
	}
	/**
	 * Il Metodo si occupa di tracciare una busta di richiesta.
	 *
	 * @param busta Busta da registrare
	 * @param bustaObject Busta da registrare (per appender personalizzati)
	 * 
	 */
	public void registraRichiesta(OpenSPCoop2Message msg,SecurityInfo securityInfo,
			BustaRawContent<?> busta,Busta bustaObject,EsitoElaborazioneMessaggioTracciato esito, String location) throws TracciamentoException {
		registraRichiesta(msg,securityInfo,busta,bustaObject,esito,location,null);
	}
	public void registraRichiesta(OpenSPCoop2Message msg,SecurityInfo securityInfo,
			BustaRawContent<?> busta,Busta bustaObject,EsitoElaborazioneMessaggioTracciato esito, String location, 
			String idCorrelazioneApplicativa) throws TracciamentoException {
		if(this.transactionNullable!=null) {
			this.transactionNullable.getTempiElaborazione().startTracciamentoRichiesta();
		}
		try {
			if(this.tracciamentoSupportatoProtocollo && this.getConfigurazionePdDManager().tracciamentoBuste()) {
				String xml = null;
				boolean erroreAppender = false;
				
				// Data
				Date gdo = DateManager.getDate();
				if(this.openspcoopProperties.generazioneDateCasualiLogAbilitato() && this.pddContext!=null && this.pddContext.getObject(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE)!=null){
					gdo = this.generatoreDateCasuali.getProssimaData((String)this.pddContext.getObject(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE));
					bustaObject.setOraRegistrazione(gdo);
				}
				
				// Traccia
				Traccia traccia = this.getTraccia(bustaObject, msg, securityInfo, esito, gdo, RuoloMessaggio.RICHIESTA, location, idCorrelazioneApplicativa);
				boolean saveHeader = true;
				try {
					saveHeader = (this.protocolFactory==null || this.protocolFactory.createProtocolConfiguration().isAbilitatoSalvataggioHeaderProtocolloTracce());
				}catch(Throwable e) {
					this.logError("Comprensione opzione 'salvataggio header protocollo tracce' fallita: "+e.getMessage(), new Exception(e));
				}
				if(saveHeader) {
					traccia.setBustaAsRawContent(busta);
				}
				
				try{
					
					// Miglioramento performance
					if(OpenSPCoop2Logger.loggerTracciamentoAbilitato){
						xml = this.xmlBuilder.toString(traccia,TipoSerializzazione.DEFAULT);
						if(xml==null)
							throw new Exception("Traccia non costruita");
						this.loggerTracciamento.info(xml);
					}
						
					// TransazioneContext
					if(this.openspcoopProperties.isTransazioniSaveTracceInUniqueTransaction()) {
						this.logTracciaInTransactionContext(traccia, true);
					}
					
					// Tracciamento personalizzato
					for(int i=0; i<this.loggerTracciamentoOpenSPCoopAppender.size();i++){
						try{
							this.loggerTracciamentoOpenSPCoopAppender.get(i).log(getConnectionFromState(true), traccia);
						} catch(Exception e){
							logError("Errore durante il tracciamento personalizzato ["+this.tipoTracciamentoOpenSPCoopAppender.get(i)+"] della richiesta: "+e.getMessage()+". Traccia non registrata:\n"+xml,e);
							try{
								this.msgDiagErroreTracciamento.addKeyword(CostantiPdD.KEY_TRACCIA_TIPO, RuoloMessaggio.RICHIESTA.toString());
								this.msgDiagErroreTracciamento.addKeyword(CostantiPdD.KEY_TRACCIA, xml);
								this.msgDiagErroreTracciamento.addKeyword(CostantiPdD.KEY_TRACCIAMENTO_PERSONALIZZATO,this.tipoTracciamentoOpenSPCoopAppender.get(i));
								this.msgDiagErroreTracciamento.addKeyword(CostantiPdD.KEY_TRACCIAMENTO_ERRORE,e.getMessage());
								this.msgDiagErroreTracciamento.logPersonalizzato("registrazioneNonRiuscita.openspcoopAppender");
							}catch(Exception eMsg){}
							if(this.openspcoopProperties.isTracciaturaFallita_BloccaCooperazioneInCorso()){
								erroreAppender = true;
								throw e; // Rilancio
							}
						}
					}
				}catch(Exception e){
					// check eventuale costruzione dell'xml non fatto per log4j disabilitato.
					if( (xml==null) && (OpenSPCoop2Logger.loggerTracciamentoAbilitato==false) ){
						try{
							xml = this.xmlBuilder.toString(traccia,TipoSerializzazione.DEFAULT);
						}catch(Exception eBuild){}
					}
					if(xml==null){
						logError("Errore durante la costruzione della traccia: "+e.getMessage(),e);
					}else{
						logError("Errore durante il tracciamento della richiesta: "+e.getMessage()+". Traccia non registrata:\n"+xml,e);
						if(!erroreAppender){
							try{
								this.msgDiagErroreTracciamento.addKeyword(CostantiPdD.KEY_TRACCIA_TIPO, RuoloMessaggio.RICHIESTA.toString());
								this.msgDiagErroreTracciamento.addKeyword(CostantiPdD.KEY_TRACCIA, xml);
								this.msgDiagErroreTracciamento.addKeyword(CostantiPdD.KEY_TRACCIAMENTO_ERRORE,e.getMessage());
								this.msgDiagErroreTracciamento.logPersonalizzato("registrazioneNonRiuscita");
							}catch(Exception eMsg){}
						}
					}
					gestioneErroreTracciamento(e);
				}
			}
		}finally {
			if(this.transactionNullable!=null) {
				this.transactionNullable.getTempiElaborazione().endTracciamentoRichiesta();
			}
		}
	}
	



	/**
	 * Il Metodo si occupa di tracciare una busta di risposta.
	 *
	 * @param busta Busta da registrare
	 * 
	 */
	@Deprecated
	public void registraRisposta(OpenSPCoop2Message msg,SecurityInfo securityInfo,
			Busta busta,EsitoElaborazioneMessaggioTracciato esito, String location) throws TracciamentoException {
		registraRisposta(msg,securityInfo,busta,esito,location,null,null);
	}
	@Deprecated
	public void registraRisposta(OpenSPCoop2Message msg,SecurityInfo securityInfo,
			Busta busta,EsitoElaborazioneMessaggioTracciato esito, String location, 
			String idCorrelazioneApplicativa, String idCorrelazioneApplicativaRisposta) throws TracciamentoException {
		if(this.transactionNullable!=null) {
			this.transactionNullable.getTempiElaborazione().startTracciamentoRisposta();
		}
		try {
			if(this.tracciamentoSupportatoProtocollo && this.getConfigurazionePdDManager().tracciamentoBuste()){
				String xml = null;
				boolean erroreAppender = false;
				
				// Data
				Date gdo = DateManager.getDate();
				if(this.openspcoopProperties.generazioneDateCasualiLogAbilitato() && this.pddContext!=null && this.pddContext.getObject(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE)!=null){
					gdo = this.generatoreDateCasuali.getProssimaData((String)this.pddContext.getObject(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE));
					busta.setOraRegistrazione(gdo);
				}
				
				// Traccia
				Traccia traccia = this.getTraccia(busta, msg, securityInfo, esito, gdo, RuoloMessaggio.RISPOSTA, location, idCorrelazioneApplicativa, idCorrelazioneApplicativaRisposta);
				
				try{
								
					// Miglioramento performance
					if(OpenSPCoop2Logger.loggerTracciamentoAbilitato){
						xml = this.xmlBuilder.toString(traccia,TipoSerializzazione.DEFAULT);
						if(xml==null)
							throw new Exception("Traccia non costruita");
						this.loggerTracciamento.info(xml);
					}
						
					// TransazioneContext
					if(this.openspcoopProperties.isTransazioniSaveTracceInUniqueTransaction()) {
						this.logTracciaInTransactionContext(traccia, false);
					}
					
					// Tracciamento personalizzato
					for(int i=0; i<this.loggerTracciamentoOpenSPCoopAppender.size();i++){
						try{
							this.loggerTracciamentoOpenSPCoopAppender.get(i).log(getConnectionFromState(false), traccia);
						}catch(Exception e){
							logError("Errore durante il tracciamento personalizzato ["+this.tipoTracciamentoOpenSPCoopAppender.get(i)+"] della risposta: "+e.getMessage()+". Traccia non registrata:\n"+xml,e);
							try{
								this.msgDiagErroreTracciamento.addKeyword(CostantiPdD.KEY_TRACCIA_TIPO, RuoloMessaggio.RISPOSTA.toString());
								this.msgDiagErroreTracciamento.addKeyword(CostantiPdD.KEY_TRACCIA, xml);
								this.msgDiagErroreTracciamento.addKeyword(CostantiPdD.KEY_TRACCIAMENTO_PERSONALIZZATO,this.tipoTracciamentoOpenSPCoopAppender.get(i));
								this.msgDiagErroreTracciamento.addKeyword(CostantiPdD.KEY_TRACCIAMENTO_ERRORE,e.getMessage());
								this.msgDiagErroreTracciamento.logPersonalizzato("registrazioneNonRiuscita.openspcoopAppender");
							}catch(Exception eMsg){}
							if(this.openspcoopProperties.isTracciaturaFallita_BloccaCooperazioneInCorso()){
								erroreAppender = true;
								throw e; // Rilancio
							}
						}
					}
				}catch(Exception e){
					// check eventuale costruzione dell'xml non fatto per log4j disabilitato.
					if( (xml==null) && (OpenSPCoop2Logger.loggerTracciamentoAbilitato==false) ){
						try{
							xml = this.xmlBuilder.toString(traccia,TipoSerializzazione.DEFAULT);
						}catch(Exception eBuild){}
					}
					if(xml==null){
						logError("Errore durante la costruzione della traccia: "+e.getMessage(),e);
					}else{
						logError("Errore durante il tracciamento della risposta: "+e.getMessage()+". Traccia non registrata:\n"+xml,e);
						if(!erroreAppender){
							try{
								this.msgDiagErroreTracciamento.addKeyword(CostantiPdD.KEY_TRACCIA_TIPO, RuoloMessaggio.RISPOSTA.toString());
								this.msgDiagErroreTracciamento.addKeyword(CostantiPdD.KEY_TRACCIA, xml);
								this.msgDiagErroreTracciamento.addKeyword(CostantiPdD.KEY_TRACCIAMENTO_ERRORE,e.getMessage());
								this.msgDiagErroreTracciamento.logPersonalizzato("registrazioneNonRiuscita");
							}catch(Exception eMsg){}
						}
					}
					gestioneErroreTracciamento(e);
				}
			}
		}finally {
			if(this.transactionNullable!=null) {
				this.transactionNullable.getTempiElaborazione().endTracciamentoRisposta();
			}
		}
	}
	/**
	 * Il Metodo si occupa di tracciare una busta di risposta.
	 *
	 * @param busta Busta da registrare
	 * @param bustaObject Busta da registrare (per appender personalizzati)
	 * 
	 */
	@Deprecated
	public void registraRisposta(OpenSPCoop2Message msg,SecurityInfo securityInfo,
			byte[] busta,Busta bustaObject,EsitoElaborazioneMessaggioTracciato esito, String location) throws TracciamentoException {
		registraRisposta(msg,securityInfo,busta,bustaObject,esito, location,null,null);
	}
	@Deprecated
	public void registraRisposta(OpenSPCoop2Message msg,SecurityInfo securityInfo,
			byte[] busta,Busta bustaObject,EsitoElaborazioneMessaggioTracciato esito, String location, 
			String idCorrelazioneApplicativa, String idCorrelazioneApplicativaRisposta) throws TracciamentoException{
		if(this.transactionNullable!=null) {
			this.transactionNullable.getTempiElaborazione().startTracciamentoRisposta();
		}
		try {
			if(this.tracciamentoSupportatoProtocollo && this.getConfigurazionePdDManager().tracciamentoBuste()){
				String xml = null;
				boolean erroreAppender = false;
				
				// Data
				Date gdo = DateManager.getDate();
				if(this.openspcoopProperties.generazioneDateCasualiLogAbilitato() && this.pddContext!=null && this.pddContext.getObject(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE)!=null){
					gdo = this.generatoreDateCasuali.getProssimaData((String)this.pddContext.getObject(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE));
					bustaObject.setOraRegistrazione(gdo);
				}
				
				// Traccia
				Traccia traccia = this.getTraccia(bustaObject, msg, securityInfo, esito, gdo, RuoloMessaggio.RISPOSTA, location, idCorrelazioneApplicativa, idCorrelazioneApplicativaRisposta);
				traccia.setBustaAsByteArray(busta);
				
				try{
									
					// Miglioramento performance
					if(OpenSPCoop2Logger.loggerTracciamentoAbilitato){
						xml = this.xmlBuilder.toString(traccia,TipoSerializzazione.DEFAULT);
						if(xml==null)
							throw new Exception("Traccia non costruita");
						this.loggerTracciamento.info(xml);
					}
					
					// TransazioneContext
					if(this.openspcoopProperties.isTransazioniSaveTracceInUniqueTransaction()) {
						this.logTracciaInTransactionContext(traccia, false);
					}
						
					// Tracciamento personalizzato
					for(int i=0; i<this.loggerTracciamentoOpenSPCoopAppender.size();i++){
						try{
							this.loggerTracciamentoOpenSPCoopAppender.get(i).log(getConnectionFromState(false), traccia);
						}catch(Exception e){
							logError("Errore durante il tracciamento personalizzato ["+this.tipoTracciamentoOpenSPCoopAppender.get(i)+"] della risposta: "+e.getMessage()+". Traccia non registrata:\n"+xml,e);
							try{
								this.msgDiagErroreTracciamento.addKeyword(CostantiPdD.KEY_TRACCIA_TIPO, RuoloMessaggio.RISPOSTA.toString());
								this.msgDiagErroreTracciamento.addKeyword(CostantiPdD.KEY_TRACCIA, xml);
								this.msgDiagErroreTracciamento.addKeyword(CostantiPdD.KEY_TRACCIAMENTO_PERSONALIZZATO,this.tipoTracciamentoOpenSPCoopAppender.get(i));
								this.msgDiagErroreTracciamento.addKeyword(CostantiPdD.KEY_TRACCIAMENTO_ERRORE,e.getMessage());
								this.msgDiagErroreTracciamento.logPersonalizzato("registrazioneNonRiuscita.openspcoopAppender");
							}catch(Exception eMsg){}
							if(this.openspcoopProperties.isTracciaturaFallita_BloccaCooperazioneInCorso()){
								erroreAppender = true;
								throw e; // Rilancio
							}
						}
					}
				}catch(Exception e){
					// check eventuale costruzione dell'xml non fatto per log4j disabilitato.
					if( (xml==null) && (OpenSPCoop2Logger.loggerTracciamentoAbilitato==false) ){
						try{
							xml = this.xmlBuilder.toString(traccia,TipoSerializzazione.DEFAULT);
						}catch(Exception eBuild){}
					}
					if(xml==null){
						logError("Errore durante la costruzione della traccia: "+e.getMessage(),e);
					}else{
						logError("Errore durante il tracciamento della risposta: "+e.getMessage()+". Traccia non registrata:\n"+xml,e);
						if(!erroreAppender){
							try{
								this.msgDiagErroreTracciamento.addKeyword(CostantiPdD.KEY_TRACCIA_TIPO, RuoloMessaggio.RISPOSTA.toString());
								this.msgDiagErroreTracciamento.addKeyword(CostantiPdD.KEY_TRACCIA, xml);
								this.msgDiagErroreTracciamento.addKeyword(CostantiPdD.KEY_TRACCIAMENTO_ERRORE,e.getMessage());
								this.msgDiagErroreTracciamento.logPersonalizzato("registrazioneNonRiuscita");
							}catch(Exception eMsg){}
						}
					}
					gestioneErroreTracciamento(e);
				}
			}
		}finally {
			if(this.transactionNullable!=null) {
				this.transactionNullable.getTempiElaborazione().endTracciamentoRisposta();
			}
		}
	}
	/**
	 * Il Metodo si occupa di tracciare una busta di risposta.
	 *
	 * @param busta Busta da registrare
	 * @param bustaObject Busta da registrare (per appender personalizzati)
	 * 
	 */
	public void registraRisposta(OpenSPCoop2Message msg,SecurityInfo securityInfo,
			BustaRawContent<?> busta,Busta bustaObject,EsitoElaborazioneMessaggioTracciato esito, String location) throws TracciamentoException {
		registraRisposta(msg,securityInfo,busta,bustaObject,esito, location,null,null);
	}
	public void registraRisposta(OpenSPCoop2Message msg,SecurityInfo securityInfo,
			BustaRawContent<?> busta,Busta bustaObject,EsitoElaborazioneMessaggioTracciato esito, String location, 
			String idCorrelazioneApplicativa, String idCorrelazioneApplicativaRisposta) throws TracciamentoException {
		if(this.transactionNullable!=null) {
			this.transactionNullable.getTempiElaborazione().startTracciamentoRisposta();
		}
		try {
			if(this.tracciamentoSupportatoProtocollo && this.getConfigurazionePdDManager().tracciamentoBuste()){
				String xml = null;
				boolean erroreAppender = false;
				
				// Data
				Date gdo = DateManager.getDate();
				if(this.openspcoopProperties.generazioneDateCasualiLogAbilitato() && this.pddContext!=null && this.pddContext.getObject(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE)!=null){
					gdo = this.generatoreDateCasuali.getProssimaData((String)this.pddContext.getObject(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE));
					bustaObject.setOraRegistrazione(gdo);
				}
				
				// Traccia
				Traccia traccia = this.getTraccia(bustaObject, msg, securityInfo, esito, gdo, RuoloMessaggio.RISPOSTA, location, idCorrelazioneApplicativa, idCorrelazioneApplicativaRisposta);
				boolean saveHeader = true;
				try {
					saveHeader = (this.protocolFactory==null || this.protocolFactory.createProtocolConfiguration().isAbilitatoSalvataggioHeaderProtocolloTracce());
				}catch(Throwable e) {
					this.logError("Comprensione opzione 'salvataggio header protocollo tracce' fallita: "+e.getMessage(), new Exception(e));
				}
				if(saveHeader) {
					traccia.setBustaAsRawContent(busta);
				}
				
				try{
				
					// Miglioramento performance
					if(OpenSPCoop2Logger.loggerTracciamentoAbilitato){
						xml = this.xmlBuilder.toString(traccia,TipoSerializzazione.DEFAULT);
						if(xml==null)
							throw new Exception("Traccia non costruita");
						this.loggerTracciamento.info(xml);
					}
					
					// TransazioneContext
					if(this.openspcoopProperties.isTransazioniSaveTracceInUniqueTransaction()) {
						this.logTracciaInTransactionContext(traccia, false);
					}
					
					// Tracciamento personalizzato
					for(int i=0; i<this.loggerTracciamentoOpenSPCoopAppender.size();i++){
						try{
							this.loggerTracciamentoOpenSPCoopAppender.get(i).log(getConnectionFromState(false), traccia);
						}catch(Exception e){
							logError("Errore durante il tracciamento personalizzato ["+this.tipoTracciamentoOpenSPCoopAppender.get(i)+"] della risposta: "+e.getMessage()+". Traccia non registrata:\n"+xml,e);
							try{
								this.msgDiagErroreTracciamento.addKeyword(CostantiPdD.KEY_TRACCIA_TIPO, RuoloMessaggio.RISPOSTA.toString());
								this.msgDiagErroreTracciamento.addKeyword(CostantiPdD.KEY_TRACCIA, xml);
								this.msgDiagErroreTracciamento.addKeyword(CostantiPdD.KEY_TRACCIAMENTO_PERSONALIZZATO,this.tipoTracciamentoOpenSPCoopAppender.get(i));
								this.msgDiagErroreTracciamento.addKeyword(CostantiPdD.KEY_TRACCIAMENTO_ERRORE,e.getMessage());
								this.msgDiagErroreTracciamento.logPersonalizzato("registrazioneNonRiuscita.openspcoopAppender");
							}catch(Exception eMsg){}
							if(this.openspcoopProperties.isTracciaturaFallita_BloccaCooperazioneInCorso()){
								erroreAppender = true;
								throw e; // Rilancio
							}
						}
					}
				}catch(Exception e){
					// check eventuale costruzione dell'xml non fatto per log4j disabilitato.
					if( (xml==null) && (OpenSPCoop2Logger.loggerTracciamentoAbilitato==false) ){
						try{
							xml = this.xmlBuilder.toString(traccia,TipoSerializzazione.DEFAULT);
						}catch(Exception eBuild){}
					}
					if(xml==null){
						logError("Errore durante la costruzione della traccia: "+e.getMessage(),e);
					}else{
						logError("Errore durante il tracciamento della risposta: "+e.getMessage()+". Traccia non registrata:\n"+xml,e);
						if(!erroreAppender){
							try{
								this.msgDiagErroreTracciamento.addKeyword(CostantiPdD.KEY_TRACCIA_TIPO, RuoloMessaggio.RISPOSTA.toString());
								this.msgDiagErroreTracciamento.addKeyword(CostantiPdD.KEY_TRACCIA, xml);
								this.msgDiagErroreTracciamento.addKeyword(CostantiPdD.KEY_TRACCIAMENTO_ERRORE,e.getMessage());
								this.msgDiagErroreTracciamento.logPersonalizzato("registrazioneNonRiuscita");
							}catch(Exception eMsg){}
						}
					}
					gestioneErroreTracciamento(e);
				}
			}
		}finally {
			if(this.transactionNullable!=null) {
				this.transactionNullable.getTempiElaborazione().endTracciamentoRisposta();
			}
		}
	}
	

	private void logTracciaInTransactionContext(Traccia traccia, boolean richiesta) throws TracciamentoException {
		Exception exc = null;
		boolean gestioneStateful = false;
		try {
			Transaction tr = TransactionContext.getTransaction(traccia.getIdTransazione());
			if(richiesta) {
				tr.setTracciaRichiesta(traccia);
			}
			else {
				tr.setTracciaRisposta(traccia);
			}
		}catch(TransactionDeletedException e){
			gestioneStateful = true;
		}catch(TransactionNotExistsException e){
			gestioneStateful = true;
		}catch(Exception e){
			exc = e;
		}
		if(gestioneStateful){
			try{
				//System.out.println("@@@@@REPOSITORY@@@@@ LOG MSG DIAG ID TRANSAZIONE ["+idTransazione+"] ADD");
				RepositoryGestioneStateful.addTraccia(traccia.getIdTransazione(), traccia);
			}catch(Exception e){
				exc = e;
			}
		}
		if(exc!=null) {
			logError("Errore durante l'emissione della traccia nel contesto della transazione: "+exc.getMessage(),exc);
			gestioneErroreTracciamento(exc);
		}
	}
	
	
	private Traccia getTraccia(Busta busta,OpenSPCoop2Message msg,SecurityInfo securityInfo,EsitoElaborazioneMessaggioTracciato esito,Date gdo,RuoloMessaggio tipoTraccia,
			String location,String idCorrelazioneApplicativa){
		return getTraccia(busta, msg, securityInfo, esito, gdo, tipoTraccia, location, idCorrelazioneApplicativa, null);
	}
	private Traccia getTraccia(Busta busta,OpenSPCoop2Message msg,SecurityInfo securityInfo,EsitoElaborazioneMessaggioTracciato esito,Date gdo,RuoloMessaggio tipoTraccia,
			String location,String idCorrelazioneApplicativa,String idCorrelazioneApplicativaRisposta){
		
		Traccia traccia = new Traccia();
		
		traccia.setIdTransazione((String) this.pddContext.getObject(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE));
		
		// Esito
		traccia.setEsitoElaborazioneMessaggioTracciato(esito);
		try{
			if(RuoloMessaggio.RISPOSTA.equals(tipoTraccia) && msg!=null){
				boolean found = false;
				if(ServiceBinding.SOAP.equals(msg.getServiceBinding())){
					if(msg.castAsSoap().hasSOAPFault()){
						SOAPBody body = msg.castAsSoap().getSOAPBody();
						found = true;
						StringBuilder bf = new StringBuilder();
						if(esito.getDettaglio()!=null){
							bf.append(esito.getDettaglio());
							bf.append("\n");
						}
						bf.append(SoapUtils.safe_toString(msg.getFactory(), body.getFault(), OpenSPCoop2Logger.loggerOpenSPCoopCore));
						traccia.getEsitoElaborazioneMessaggioTracciato().setDettaglio(bf.toString());
					}
				}
				else {
					if(msg.castAsRest().isProblemDetailsForHttpApis_RFC7807()) {
						found = true;
						ByteArrayOutputStream bout = new ByteArrayOutputStream();
						msg.writeTo(bout, false);
						bout.flush();
						bout.close();
						StringBuilder bf = new StringBuilder();
						if(esito.getDettaglio()!=null){
							bf.append(esito.getDettaglio());
							bf.append("\n");
						}
						bf.append(bout.toString());
						traccia.getEsitoElaborazioneMessaggioTracciato().setDettaglio(bf.toString());
					}
				}

				if(!found && MessageRole.FAULT.equals(msg.getMessageRole())){
					ByteArrayOutputStream bout = new ByteArrayOutputStream();
					msg.writeTo(bout, false);
					bout.flush();
					bout.close();
					StringBuilder bf = new StringBuilder();
					if(esito.getDettaglio()!=null){
						bf.append(esito.getDettaglio());
						bf.append("\n");
					}
					bf.append(bout.toString());
					traccia.getEsitoElaborazioneMessaggioTracciato().setDettaglio(bf.toString());
				}
			}
		}catch(Exception e){
			this.logError("errore durante la registrazione del SOAPFault nelle tracce", e);
		}
		
		traccia.setGdo(gdo);
		traccia.setIdSoggetto(this.idSoggettoDominio);
		traccia.setTipoMessaggio(tipoTraccia);
		traccia.setTipoPdD(this.tipoPdD);
				
		traccia.setCorrelazioneApplicativa(idCorrelazioneApplicativa);
		traccia.setCorrelazioneApplicativaRisposta(idCorrelazioneApplicativaRisposta);
		if(location!=null) {
			if(location.length()>230) { // sto piu' basso di 255 per evitare caratteri strani che occupano più bytes.
				traccia.setLocation(location.substring(0, 230)+" ...");
			}
			else {
				traccia.setLocation(location);
			}
		}
		traccia.setProtocollo(this.protocolFactory.getProtocol());
		
		if(securityInfo!=null){
			busta.setDigest(securityInfo.getDigestHeader());
			Hashtable<String, String> properties = securityInfo.getProperties();
			Enumeration<String> keys = properties.keys();
			while (keys.hasMoreElements()) {
				String key = keys.nextElement();
				busta.addProperty(key, properties.get(key)); // aggiungo le proprieta' di sicurezza riscontrate
			}
		}
		traccia.setBusta(busta);
		
		if(msg!=null){
			try{
				if(ServiceBinding.SOAP.equals(msg.getServiceBinding())){
					// per motivi di streaming, se non e' costruito non lo tracciamo
					if(msg.isContentBuilded() && msg.castAsSoap().hasAttachments()) {
						java.util.Iterator<?> it = msg.castAsSoap().getAttachments();
					    while(it.hasNext()){
					    	AttachmentPart ap = 
					    		(AttachmentPart) it.next();
					    	Allegato allegato = new Allegato();
					    	allegato.setContentId(ap.getContentId());
					    	allegato.setContentLocation(ap.getContentLocation());
					    	allegato.setContentType(ap.getContentType());
					    	
					    	if(securityInfo!=null && ap.getContentId()!=null){
						    	for (int i = 0; i < securityInfo.sizeListaAllegati(); i++) {
									Allegato a = securityInfo.getAllegato(i);
									if(a.getContentId()!=null && a.getContentId().equals(ap.getContentId())){
										allegato.setDigest(a.getDigest());
									}
								}
					    	}
					    	
					    	traccia.addAllegato(allegato);
					    }
					}
				}
				else if(MessageType.MIME_MULTIPART.equals(msg.getMessageType())){
					// per motivi di streaming, se non e' costruito non lo tracciamo
					if(msg.isContentBuilded()){
						MimeMultipart mime = msg.castAsRestMimeMultipart().getContent();
						if(mime!=null){
							for (int i = 0; i < mime.countBodyParts(); i++) {
								BodyPart bodyPart = mime.getBodyPart(i);
								String contentId = mime.getContentID(bodyPart);
								if(contentId==null){
									// provo a vedere se c'e' un disposition
									contentId = mime.getContentDisposition(bodyPart);
								}
								
								Allegato allegato = new Allegato();
						    	allegato.setContentId(contentId);
						    	allegato.setContentLocation(mime.getContentLocation(bodyPart));
						    	allegato.setContentType(bodyPart.getContentType());
						    	
						    	if(securityInfo!=null && contentId!=null){
							    	for (int j = 0; j < securityInfo.sizeListaAllegati(); j++) {
										Allegato a = securityInfo.getAllegato(j);
										if(a.getContentId()!=null && a.getContentId().equals(contentId)){
											allegato.setDigest(a.getDigest());
										}
									}
						    	}
						    	
						    	traccia.addAllegato(allegato);
							}
						}
					}
				}
			}catch(Exception e){
				this.logError("errore durante la registrazione degli allegati nelle tracce", e);
			}
		}
		
		if(this.pddContext!=null){
			String [] key = org.openspcoop2.core.constants.Costanti.CONTEXT_OBJECT;
			if(key!=null){
				for (int j = 0; j < key.length; j++) {
					Object o = this.pddContext.getObject(key[j]);
					if(o!=null && o instanceof String){
						traccia.addProperty(key[j], (String)o);
					}
				}
			}
		}
		
		return traccia;
	}
	
	private void gestioneErroreTracciamento(Exception e) throws TracciamentoException{
		
		if(this.openspcoopProperties.isTracciaturaFallita_BloccoServiziPdD()){
			Tracciamento.tracciamentoDisponibile=false;
			Tracciamento.motivoMalfunzionamentoTracciamento=e;
			try{
				this.msgDiagErroreTracciamento.logPersonalizzato("errore.bloccoServizi");
			}catch(Exception eMsg){}
			logError("Il Sistema di tracciamento ha rilevato un errore durante la registrazione di una traccia legale,"+
					" tutti i servizi/moduli della porta di dominio sono sospesi. Si richiede un intervento sistemistico per la risoluzione del problema "+
					"e il riavvio di GovWay. Errore rilevato: ",e);
		}
		
		if(this.openspcoopProperties.isTracciaturaFallita_BloccaCooperazioneInCorso()){
			throw new TracciamentoException(e);
		}
	}
	
	
	private void logError(String msgErrore,Exception e){
		if(OpenSPCoop2Logger.loggerOpenSPCoopCore!=null){
			OpenSPCoop2Logger.loggerOpenSPCoopCore.error(msgErrore,e);
		}
		
		// Registro l'errore anche in questo logger.
		if(OpenSPCoop2Logger.loggerOpenSPCoopResources!=null){
			OpenSPCoop2Logger.loggerOpenSPCoopResources.error(msgErrore,e);
		}
		
	}
	@SuppressWarnings("unused")
	private void logError(String msgErrore){
		if(OpenSPCoop2Logger.loggerOpenSPCoopCore!=null){
			OpenSPCoop2Logger.loggerOpenSPCoopCore.error(msgErrore);
		}
		
		// Registro l'errore anche in questo logger.
		if(OpenSPCoop2Logger.loggerOpenSPCoopResources!=null){
			OpenSPCoop2Logger.loggerOpenSPCoopResources.error(msgErrore);
		}
		
	}
	
}

