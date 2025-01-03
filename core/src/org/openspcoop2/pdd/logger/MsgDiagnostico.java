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




package org.openspcoop2.pdd.logger;

import java.sql.Connection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.Level;
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.constants.Severita;
import org.openspcoop2.core.constants.Costanti;
import org.openspcoop2.core.constants.TipoPdD;
import org.openspcoop2.core.id.IDPortaApplicativa;
import org.openspcoop2.core.id.IDPortaDelegata;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.driver.IDServizioFactory;
import org.openspcoop2.core.transazioni.TransazioneApplicativoServer;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.pdd.config.ConfigurazionePdDManager;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.core.CostantiPdD;
import org.openspcoop2.pdd.core.PdDContext;
import org.openspcoop2.pdd.core.handlers.GeneratoreCasualeDate;
import org.openspcoop2.pdd.core.transazioni.GestoreConsegnaMultipla;
import org.openspcoop2.pdd.core.transazioni.RepositoryGestioneStateful;
import org.openspcoop2.pdd.core.transazioni.Transaction;
import org.openspcoop2.pdd.core.transazioni.TransactionContext;
import org.openspcoop2.pdd.core.transazioni.TransactionDeletedException;
import org.openspcoop2.pdd.core.transazioni.TransactionNotExistsException;
import org.openspcoop2.pdd.services.skeleton.IntegrationManager;
import org.openspcoop2.protocol.engine.BasicProtocolFactory;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.engine.builder.DiagnosticoBuilder;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.Context;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.config.ITraduttore;
import org.openspcoop2.protocol.sdk.constants.TipoSerializzazione;
import org.openspcoop2.protocol.sdk.diagnostica.IDiagnosticProducer;
import org.openspcoop2.protocol.sdk.state.IState;
import org.openspcoop2.protocol.sdk.state.RequestInfo;
import org.openspcoop2.protocol.sdk.state.StateMessage;
import org.openspcoop2.utils.MapKey;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.date.DateManager;
import org.slf4j.Logger;



/**
 * Contiene la definizione un Logger utilizzato dai nodi dell'infrastruttura di OpenSPCoop2
 * per la registrazione di messaggi diagnostici.
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class MsgDiagnostico {

	
	/** Indicazione di un gestore di msg diagnostici funzionante */
	public static boolean gestoreDiagnosticaDisponibile = true;
	/** Primo errore avvenuto nel momento in cui è stato rilevato un malfunzionamento nel sistema di diagnostica */
	public static Throwable motivoMalfunzionamentoDiagnostici = null;
	

	/**  Logger log4j utilizzato per scrivere i msgDiagnostici */
	private org.apache.logging.log4j.Logger loggerMsgDiagnostico = null;
	/**  Logger log4j utilizzato per scrivere i msgDiagnostici HumanReadable */
	private org.apache.logging.log4j.Logger loggerOpenSPCoop2 = null;
	/**  Logger log4j utilizzato per scrivere i msgDiagnostici HumanReadable del servìzio di IntegrationManager */
	private org.apache.logging.log4j.Logger loggerIntegrationManager = null;
	/**  Logger log4j utilizzato per scrivere i msgDiagnostici da passare agli appender che non possiedono un id di transazione */
	private org.apache.logging.log4j.Logger loggerResource = null;
	/**  Logger log4j utilizzato per segnalare a video errori gravi (FATAL) */
	private Logger loggerOpenSPCoop2Fatal = null;
	/**  Logger log4j utilizzato per scrivere lo stack trace degli errore nel core logger di openspcoop */
	private Logger loggerOpenSPCoop2Core = null;
	/** Appender personalizzati per i messaggi diagnostici di OpenSPCoop2 */
	private List<IDiagnosticProducer> loggerMsgDiagnosticoOpenSPCoopAppender = null; 
	private List<String> tipoMsgDiagnosticoOpenSPCoopAppender = null;

	/** Soggetto che richiede il logger */
	private IDSoggetto idSoggettoDominio;
	/** Modulo Funzionale */
	private String idModulo;
	/** Identificativo della richiesta */
	private String idMessaggioRichiesta;
	/** Identificativo della risposta */
	private String idMessaggioRisposta;
	/** PdDContext */
	private Context pddContext;
	/** XMLBuilder */
	private DiagnosticoBuilder diagnosticoBuilder;
	
	/** Protocol Factory */
	private IProtocolFactory<?> protocolFactory;
	private ITraduttore traduttore;
	/** ConfigurazionePdDReader */
	private ConfigurazionePdDManager _configurazionePdDReader;
	/** MsgDiagnosticiProperties reader */
	private MsgDiagnosticiProperties msgDiagPropertiesReader;
	/** OpenSPCoopProperties */
	private OpenSPCoop2Properties openspcoopProperties = null;
	
	/** Generatore di date casuali*/
	private GeneratoreCasualeDate generatoreDateCasuali = null;
	
	/** Properties da aggiungere ai diagnostici (utili nelle implementazioni handler) */
	private Map<String,String> properties = new HashMap<>();		
	public Map<String, String> getProperties() {
		return this.properties;
	}

	/** Keyword per i log personalizzati */
	private Map<String,String> keywordLogPersonalizzati = new HashMap<>();	
	public Map<String, String> getKeywordLogPersonalizzati() {
		return this.keywordLogPersonalizzati;
	}
	
	/** Informazioni sulla porta */
	private String porta;
	private boolean delegata;
	@SuppressWarnings("unused")
	private TipoPdD tipoPdD;
	private Severita severitaPorta;

	/** Stati */
	private StateMessage state = null;
	private StateMessage responseState = null;
	
	/** RequestInfo */
	private RequestInfo requestInfo = null;
	
	
	public static MsgDiagnostico newInstance(TipoPdD tipoPdD,IDSoggetto idSoggettoDominio, String modulo,String nomePorta, RequestInfo requestInfo, ConfigurazionePdDManager configurazionePdDManager) {
		return new MsgDiagnostico(tipoPdD, idSoggettoDominio, modulo, nomePorta, requestInfo,
				configurazionePdDManager, null, null);
	}
	public static MsgDiagnostico newInstance(TipoPdD tipoPdD,IDSoggetto idSoggettoDominio, String modulo,String nomePorta, RequestInfo requestInfo, IState state) {
		return new MsgDiagnostico(tipoPdD, idSoggettoDominio, modulo, nomePorta, requestInfo,
				null, state, null);
	}
	public static MsgDiagnostico newInstance(TipoPdD tipoPdD,IDSoggetto idSoggettoDominio, String modulo,String nomePorta, RequestInfo requestInfo, IState state, IState responseState) {
		return new MsgDiagnostico(tipoPdD, idSoggettoDominio, modulo, nomePorta, requestInfo,
				null, state, responseState);
	}
	
	public static MsgDiagnostico newInstance(TipoPdD tipoPdD,String modulo,String nomePorta, RequestInfo requestInfo, ConfigurazionePdDManager configurazionePdDManager) {
		return new MsgDiagnostico(tipoPdD, OpenSPCoop2Properties.getInstance().getIdentitaPortaDefaultWithoutProtocol(), modulo, nomePorta, requestInfo,
				configurazionePdDManager, null, null);
	}
	public static MsgDiagnostico newInstance(TipoPdD tipoPdD,String modulo,String nomePorta, RequestInfo requestInfo) {
		return new MsgDiagnostico(tipoPdD, OpenSPCoop2Properties.getInstance().getIdentitaPortaDefaultWithoutProtocol(), modulo, nomePorta, requestInfo,
				null, null, null);
	}
	
	public static MsgDiagnostico newInstance(TipoPdD tipoPdD,String modulo,ConfigurazionePdDManager configurazionePdDManager) {
		return new MsgDiagnostico(tipoPdD, OpenSPCoop2Properties.getInstance().getIdentitaPortaDefaultWithoutProtocol(), modulo, null, null,
				configurazionePdDManager, null, null);
	}
	public static MsgDiagnostico newInstance(TipoPdD tipoPdD,String modulo) {
		return new MsgDiagnostico(tipoPdD, OpenSPCoop2Properties.getInstance().getIdentitaPortaDefaultWithoutProtocol(), modulo, null, null,
				null, null, null);
	}
	
	public static MsgDiagnostico newInstance(IDSoggetto idSoggettoDominio, String modulo,ConfigurazionePdDManager configurazionePdDManager) {
		return new MsgDiagnostico(null, idSoggettoDominio, modulo, null, null,
				configurazionePdDManager, null, null);
	}
	
	public static MsgDiagnostico newInstance(String modulo,ConfigurazionePdDManager configurazionePdDManager) {
		return new MsgDiagnostico(null, OpenSPCoop2Properties.getInstance().getIdentitaPortaDefaultWithoutProtocol(), modulo, null, null,
				configurazionePdDManager, null, null); 
	}
	public static MsgDiagnostico newInstance(String modulo) {
		return new MsgDiagnostico(null, OpenSPCoop2Properties.getInstance().getIdentitaPortaDefaultWithoutProtocol(), modulo, null, null,
				null, null, null);
	}
	
	public static MsgDiagnostico newInstance() {
		return new MsgDiagnostico();
	}
	
	/**
	 * Costruttore. 
	 *
	 * @param idSoggettoDominio Soggetto che richiede il logger
	 * @param modulo Funzione che richiede il logger
	 * @throws ProtocolException 
	 * 
	 */
	private MsgDiagnostico(TipoPdD tipoPdD,IDSoggetto idSoggettoDominio, String modulo,String nomePorta, RequestInfo requestInfo,
			ConfigurazionePdDManager configurazionePdDManagerParam,IState stateParam, IState responseStateParam) {
		
		this.idSoggettoDominio = idSoggettoDominio;
		this.idModulo = modulo;
		this.loggerMsgDiagnostico = OpenSPCoop2Logger.loggerMsgDiagnostico;
		this.loggerOpenSPCoop2 = OpenSPCoop2Logger.loggerOpenSPCoop2;
		this.loggerIntegrationManager = OpenSPCoop2Logger.loggerIntegrationManager;
		this.loggerResource = OpenSPCoop2Logger.loggerOpenSPCoopResourcesAsLoggerImpl;
		this.loggerOpenSPCoop2Fatal = OpenSPCoop2Logger.loggerOpenSPCoopConsole;
		this.loggerMsgDiagnosticoOpenSPCoopAppender = OpenSPCoop2Logger.loggerMsgDiagnosticoOpenSPCoopAppender;
		this.tipoMsgDiagnosticoOpenSPCoopAppender = OpenSPCoop2Logger.tipoMsgDiagnosticoOpenSPCoopAppender;
		this.loggerOpenSPCoop2Core = OpenSPCoop2Logger.loggerOpenSPCoopCore;

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
		this.msgDiagPropertiesReader = MsgDiagnosticiProperties.getInstance();
		
		this.openspcoopProperties = OpenSPCoop2Properties.getInstance();
		if(this.openspcoopProperties.generazioneDateCasualiLogAbilitato()){
			this.generatoreDateCasuali = GeneratoreCasualeDate.getGeneratoreCasualeDate();
		}
		try{
			if(requestInfo!=null && requestInfo.getProtocolFactory()!=null) {
				this.protocolFactory = requestInfo.getProtocolFactory();
			}
			else {
				throw new Exception("ProtocolFactory unknow");
			}
			this.traduttore = this.protocolFactory.createTraduttore();
		} catch(Throwable e){
			// Succede quando non appartiene a nessun protocollo, ad esempio i diagnostici di startup
			this.protocolFactory = new BasicProtocolFactory(this.loggerOpenSPCoop2Core);
			try{
				this.traduttore = this.protocolFactory.createTraduttore();
			} catch(Throwable eClose){}
			//this.protocolFactory = null; 
		}
		this.diagnosticoBuilder = new DiagnosticoBuilder(this.protocolFactory); 
		// Impostazione filtri
		// Il filtro viene effettuato a livello di programma.
		
		this.requestInfo = requestInfo;
		
		this.porta = nomePorta;
		this.tipoPdD = tipoPdD;
		if(tipoPdD!=null) {
			if(TipoPdD.DELEGATA.equals(tipoPdD)) {
				this.delegata = true;
			}
			else {
				this.delegata = false;
			}
		}
		this.setPorta(this.requestInfo);
			
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
	
	private MsgDiagnostico(){
		this.msgDiagPropertiesReader = MsgDiagnosticiProperties.getInstance();
	}
	
	private void setPorta(RequestInfo requestInfo) {
		if(this.porta!=null) {
			if(this.delegata) {
				try {
					IDPortaDelegata idPD = new IDPortaDelegata();
					idPD.setNome(this.porta);
					PortaDelegata pd = this.getConfigurazionePdDManager().getPortaDelegataSafeMethod(idPD, requestInfo);
					if(pd!=null && pd.getTracciamento()!=null) {
						this.severitaPorta = pd.getTracciamento().getSeverita();
					}
				}
				catch(Throwable e){}
			}
			else {
				try {
					IDPortaApplicativa idPA = new IDPortaApplicativa();
					idPA.setNome(this.porta);
					PortaApplicativa pa = this.getConfigurazionePdDManager().getPortaApplicativaSafeMethod(idPA, requestInfo);
					if(pa!=null && pa.getTracciamento()!=null) {
						this.severitaPorta = pa.getTracciamento().getSeverita();
					}
				}
				catch(Throwable e){}
			}
		}
	}

	public void updateRequestInfo(RequestInfo requestInfo) {
		this.requestInfo = requestInfo;
	}
	
	public void updatePorta(String porta, RequestInfo requestInfo) {
		this.updatePorta(null, porta, requestInfo);
	}
	public void updatePorta(TipoPdD tipoPdD, String porta, RequestInfo requestInfo) {
		if(tipoPdD!=null) {
			this.tipoPdD = tipoPdD;
			if(TipoPdD.DELEGATA.equals(tipoPdD)) {
				this.delegata = true;
			}
			else {
				this.delegata = false;
			}
		}
		this.porta = porta;
		this.setPorta(requestInfo);
	}
	public String getPorta() {
		return this.porta;
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
		else {
			this._configurazionePdDReader = ConfigurazionePdDManager.getInstance();
		}
	}
	
	private Connection getConnectionFromState(){
		if(this.state!=null) {
			Connection c = StateMessage.getConnection(this.state);
			if(c!=null) {
				return c;
			}
		}
		if(this.responseState!=null) {
			Connection c = StateMessage.getConnection(this.responseState);
			if(c!=null) {
				return c;
			}
		}
		return null;
	}
	private IState getValidConnectionState(){
		if(this.state!=null) {
			Connection c = StateMessage.getConnection(this.state);
			if(c!=null) {
				return this.state;
			}
		}
		if(this.responseState!=null) {
			Connection c = StateMessage.getConnection(this.responseState);
			if(c!=null) {
				return this.responseState;
			}
		}
		return null;
	}
	
	/**
	 * Il Metodo si occupa di impostare il dominio del Soggetto che utilizza il logger. 
	 *
	 * @param idSoggettoDominio Soggetto che richiede il logger
	 * 
	 */
	public void setDominio(IDSoggetto idSoggettoDominio){
		this.idSoggettoDominio = idSoggettoDominio;
	}
	public IDSoggetto getDominio() {
		return this.idSoggettoDominio;
	}
	
	/**
	 * Il Metodo si occupa di impostare il contesto della PdD
	 *
	 * @param pddContext Contesto della PdD
	 * 
	 */
	public void setPddContext(Context pddContext, IProtocolFactory<?> protocolFactory) {
		this.pddContext = pddContext;
		this.protocolFactory = protocolFactory;
		try{
			this.traduttore = this.protocolFactory.createTraduttore();
		}catch(Exception e){
			// ignore
		}
		this.diagnosticoBuilder = new DiagnosticoBuilder(protocolFactory); 
		this.addKeywords(protocolFactory);
	}


	/**
	 * Il Metodo si occupa di impostare il modulo funzionale che che utilizza il logger. 
	 *
	 * @param modulo Funzione che richiede il logger
	 * 
	 */
	public void setFunzione(String modulo){
		this.idModulo = modulo;
	}
	public String getFunzione() {
		return this.idModulo;
	}

	/**
	 * Il Metodo si occupa di impostare l'identificativo della richiesta in gestione. 
	 *
	 * @param id Identificativo
	 */
	public void setIdMessaggioRichiesta(String id){
		this.idMessaggioRichiesta = id;
	}
	/**
	 * Il Metodo si occupa di impostare l'identificativo della risposta in gestione. 
	 *
	 * @param id Identificativo
	 */
	public void setIdMessaggioRisposta(String id){
		this.idMessaggioRisposta = id;
	}

	/**
	 * Il Metodo si occupa di impostare i filtri sui logger. 
	 *
	 * 
	 */
	public void aggiornaFiltri(){
		
		// Il filtro viene effettuato a livello di programma.

	}

	/**
	 * Filter. 
	 *
	 * 
	 * @deprecated  utility che elimina i caratteri XML codificati
	 */
	@Deprecated  public String filter(String msg){
		String xml = msg.replaceAll("&lt;","<");
		xml = xml.replaceAll("&quot;","\"");
		xml = xml.replaceAll("&gt;",">");
		return xml;
	}




	
	
	/** -----------------Impostazione Identificatori nei messaggi ---------------- */
	private IDSoggetto fruitore;
	private IDServizio servizio;
	private String servizioApplicativo;
	private String idCorrelazioneApplicativa;
	private String idCorrelazioneRisposta;
	public void setFruitore(IDSoggetto fruitore) {
		this.fruitore = fruitore;
	}

	public void setServizio(IDServizio servizio) {
		this.servizio = servizio;
	}

	public void setServizioApplicativo(String servizioApplicativo) {
		this.servizioApplicativo = servizioApplicativo;
	}
	public void setIdCorrelazioneApplicativa(String idCorrelazioneApplicativa) {
		this.idCorrelazioneApplicativa = idCorrelazioneApplicativa;
	}
	public void setIdCorrelazioneRisposta(String idCorrelazioneRisposta) {
		this.idCorrelazioneRisposta = idCorrelazioneRisposta;
	}
	
	/** -----------------Impostazione TransazioneApplicativoServer ---------------- */
	private TransazioneApplicativoServer transazioneApplicativoServer;
	private IDPortaApplicativa idPortaApplicativa;
	public void setTransazioneApplicativoServer(TransazioneApplicativoServer transazioneApplicativoServer, IDPortaApplicativa idPortaApplicativa) {
		this.transazioneApplicativoServer = transazioneApplicativoServer;
		this.idPortaApplicativa = idPortaApplicativa;
	}
	
	
	
	
	/*---------- Livelli/Messaggi personalizzati -------------*/
	
	/** Keyword replace per msg personalizzati */
	public void addKeyword(String key,String value){
		if(key!=null){
			String tmpValue = value;
			if(tmpValue == null)
				tmpValue = "";
			this.keywordLogPersonalizzati.put(key, tmpValue);
		}
	}
	public void addKeywordErroreProcessamento(Throwable t) {
		this.addKeywordErroreProcessamento(t, null);
	}
	public void addKeywordErroreProcessamento(Throwable t,String prefix) {
		String eccezione = t!=null ? Utilities.readFirstErrorValidMessageFromException(t) : "Internal Error";
		if(prefix!=null) {
			prefix = prefix.trim();
			if(prefix.endsWith(":")==false) {
				prefix = prefix +":";
			}
			prefix = prefix +" ";
			this.addKeyword(CostantiPdD.KEY_ERRORE_PROCESSAMENTO ,prefix +eccezione);
		}
		else {
			this.addKeyword(CostantiPdD.KEY_ERRORE_PROCESSAMENTO ,eccezione);
		}
	}
	public void addKeywords(IProtocolFactory<?> protocolFactory){
		this.addKeyword(CostantiPdD.KEY_PROTOCOLLO, protocolFactory.getProtocol());
		try{
			this.addKeyword(CostantiPdD.KEY_PROTOCOLLO_TIPI_SOGGETTI, protocolFactory.createProtocolConfiguration().getTipiSoggetti().toString());
		}catch(Exception e){
			// ignore
		}
		try{
			RequestInfo requestInfoParam = null;
			if(this.pddContext!=null && this.pddContext.containsKey(Costanti.REQUEST_INFO)){
				requestInfoParam = (RequestInfo) this.pddContext.getObject(Costanti.REQUEST_INFO);
			}
			else{
				requestInfoParam = this.requestInfo;
			}
			ServiceBinding serviceBinding = null;
			if(requestInfoParam!=null) {
				if(requestInfoParam.getProtocolContext().isPortaApplicativaService()) {
					serviceBinding = requestInfoParam.getProtocolServiceBinding();
				}
				else{
					serviceBinding = requestInfoParam.getIntegrationServiceBinding();
				}
			}
			this.addKeyword(CostantiPdD.KEY_PROTOCOLLO_TIPI_SERVIZI, protocolFactory.createProtocolConfiguration().getTipiServizi(serviceBinding).toString());
		}catch(Exception e){
			// ignore
		}
	}
	public void addKeywords(Busta busta,boolean richiesta){
		if(busta!=null){
			if(richiesta){
				if(busta.getID()!=null)
					this.keywordLogPersonalizzati.put(CostantiPdD.KEY_ID_MESSAGGIO_RICHIESTA, busta.getID());
				else
					this.keywordLogPersonalizzati.put(CostantiPdD.KEY_ID_MESSAGGIO_RICHIESTA, "N.D.");
				if(busta.getProfiloDiCollaborazione()!=null){
					String profilo = null;
					if(this.traduttore!=null){
						profilo = this.traduttore.toString(busta.getProfiloDiCollaborazione());
					}
					else if(busta.getProtocollo()!=null){
						try{
							IProtocolFactory<?> protocolFactory = ProtocolFactoryManager.getInstance().getProtocolFactoryByName(busta.getProtocollo());
							ITraduttore traduttore = protocolFactory.createTraduttore();
							profilo = traduttore.toString(busta.getProfiloDiCollaborazione());
						}catch(Exception e){
							profilo = busta.getProfiloDiCollaborazione().name();
						}
					}else{
						profilo = busta.getProfiloDiCollaborazione().name();
					}
					if(profilo==null){
						profilo = busta.getProfiloDiCollaborazione().getEngineValue();
					}
					this.keywordLogPersonalizzati.put(CostantiPdD.KEY_PROFILO_COLLABORAZIONE, profilo);
				}
				if(busta.getTipoMittente()!=null) {
					this.keywordLogPersonalizzati.put(CostantiPdD.KEY_TIPO_MITTENTE_BUSTA_RICHIESTA, busta.getTipoMittente());
				}
				else {
					try{
						this.keywordLogPersonalizzati.put(CostantiPdD.KEY_TIPO_MITTENTE_BUSTA_RICHIESTA, ProtocolFactoryManager.getInstance().getDefaultOrganizationTypes().get(busta.getProtocollo()));
					}catch(Exception e){}
				}
				if(busta.getMittente()!=null) {
					this.keywordLogPersonalizzati.put(CostantiPdD.KEY_MITTENTE_BUSTA_RICHIESTA, busta.getMittente());
				}else {
					this.keywordLogPersonalizzati.put(CostantiPdD.KEY_MITTENTE_BUSTA_RICHIESTA, CostantiPdD.SOGGETTO_ANONIMO);
				}
				if(busta.getTipoDestinatario()!=null)
					this.keywordLogPersonalizzati.put(CostantiPdD.KEY_TIPO_DESTINATARIO_BUSTA_RICHIESTA, busta.getTipoDestinatario());
				if(busta.getDestinatario()!=null)
					this.keywordLogPersonalizzati.put(CostantiPdD.KEY_DESTINATARIO_BUSTA_RICHIESTA, busta.getDestinatario());
				if(busta.getTipoServizio()!=null)
					this.keywordLogPersonalizzati.put(CostantiPdD.KEY_TIPO_SERVIZIO_BUSTA_RICHIESTA, busta.getTipoServizio());
				if(busta.getServizio()!=null)
					this.keywordLogPersonalizzati.put(CostantiPdD.KEY_SERVIZIO_BUSTA_RICHIESTA, busta.getServizio());
				if(busta.getVersioneServizio()!=null)
					this.keywordLogPersonalizzati.put(CostantiPdD.KEY_VERSIONE_SERVIZIO_BUSTA_RICHIESTA, busta.getVersioneServizio().intValue()+"");
				if(busta.getAzione()!=null)
					this.keywordLogPersonalizzati.put(CostantiPdD.KEY_AZIONE_BUSTA_RICHIESTA, busta.getAzione());
				else
					this.keywordLogPersonalizzati.put(CostantiPdD.KEY_AZIONE_BUSTA_RICHIESTA, "");
				if(busta.getRiferimentoMessaggio()!=null)
					this.keywordLogPersonalizzati.put(CostantiPdD.KEY_RIFERIMENTO_MESSAGGIO_RICHIESTA, busta.getRiferimentoMessaggio());
				if(busta.getSequenza()>0){
					this.keywordLogPersonalizzati.put(CostantiPdD.KEY_SEQUENZA, busta.getSequenza()+"");
				}
				if(busta.getIndirizzoMittente()!=null)
					this.keywordLogPersonalizzati.put(CostantiPdD.KEY_INDIRIZZO_TELEMATICO_MITTENTE_RICHIESTA, busta.getIndirizzoMittente());
				if(busta.getIndirizzoDestinatario()!=null)
					this.keywordLogPersonalizzati.put(CostantiPdD.KEY_INDIRIZZO_TELEMATICO_DESTINATARIO_RICHIESTA, busta.getIndirizzoDestinatario());
				if(busta.getScadenza()!=null)
					this.keywordLogPersonalizzati.put(CostantiPdD.KEY_SCADENZA_BUSTA_RICHIESTA, busta.getScadenza().toString());
				
				if(busta.getTipoDestinatario()!=null && busta.getDestinatario()!=null &&
						busta.getTipoServizio()!=null && busta.getServizio()!=null && busta.getVersioneServizio()!=null){
					try{
						this.keywordLogPersonalizzati.put(CostantiPdD.KEY_URI_ACCORDO_PARTE_SPECIFICA, 
								IDServizioFactory.getInstance().getUriFromValues(busta.getTipoServizio(), busta.getServizio(), 
										busta.getTipoDestinatario(), busta.getDestinatario(), busta.getVersioneServizio()));
					}catch(Exception e){
						throw new RuntimeException(e.getMessage(),e);
					}
				}
			}
			else{
				if(busta.getID()!=null)
					this.keywordLogPersonalizzati.put(CostantiPdD.KEY_ID_MESSAGGIO_RISPOSTA, busta.getID());
				else
					this.keywordLogPersonalizzati.put(CostantiPdD.KEY_ID_MESSAGGIO_RISPOSTA, "N.D.");
				if(busta.getProfiloDiCollaborazione()!=null){
					String profilo = null;
					if(this.traduttore!=null){
						profilo = this.traduttore.toString(busta.getProfiloDiCollaborazione());
					}
					else if(busta.getProtocollo()!=null){
						try{
							IProtocolFactory<?> protocolFactory = ProtocolFactoryManager.getInstance().getProtocolFactoryByName(busta.getProtocollo());
							ITraduttore traduttore = protocolFactory.createTraduttore();
							profilo = traduttore.toString(busta.getProfiloDiCollaborazione());
						}catch(Exception e){
							profilo = busta.getProfiloDiCollaborazione().getEngineValue();
						}
					}else{
						profilo = busta.getProfiloDiCollaborazione().getEngineValue();
					}
					if(profilo==null){
						profilo = busta.getProfiloDiCollaborazione().getEngineValue();
					}
					this.keywordLogPersonalizzati.put(CostantiPdD.KEY_PROFILO_COLLABORAZIONE, profilo);
				}
				if(busta.getTipoMittente()!=null)
					this.keywordLogPersonalizzati.put(CostantiPdD.KEY_TIPO_MITTENTE_BUSTA_RISPOSTA, busta.getTipoMittente());
				if(busta.getMittente()!=null)
					this.keywordLogPersonalizzati.put(CostantiPdD.KEY_MITTENTE_BUSTA_RISPOSTA, busta.getMittente());
				if(busta.getTipoDestinatario()!=null)
					this.keywordLogPersonalizzati.put(CostantiPdD.KEY_TIPO_DESTINATARIO_BUSTA_RISPOSTA, busta.getTipoDestinatario());
				if(busta.getDestinatario()!=null)
					this.keywordLogPersonalizzati.put(CostantiPdD.KEY_DESTINATARIO_BUSTA_RISPOSTA, busta.getDestinatario());
				if(busta.getTipoServizio()!=null)
					this.keywordLogPersonalizzati.put(CostantiPdD.KEY_TIPO_SERVIZIO_BUSTA_RISPOSTA, busta.getTipoServizio());
				if(busta.getServizio()!=null)
					this.keywordLogPersonalizzati.put(CostantiPdD.KEY_SERVIZIO_BUSTA_RISPOSTA, busta.getServizio());
				if(busta.getVersioneServizio()!=null)
					this.keywordLogPersonalizzati.put(CostantiPdD.KEY_VERSIONE_SERVIZIO_BUSTA_RISPOSTA, busta.getVersioneServizio().intValue()+"");
				if(busta.getAzione()!=null)
					this.keywordLogPersonalizzati.put(CostantiPdD.KEY_AZIONE_BUSTA_RISPOSTA, busta.getAzione());
				else
					this.keywordLogPersonalizzati.put(CostantiPdD.KEY_AZIONE_BUSTA_RISPOSTA, "");
				if(busta.getRiferimentoMessaggio()!=null)
					this.keywordLogPersonalizzati.put(CostantiPdD.KEY_RIFERIMENTO_MESSAGGIO_RISPOSTA, busta.getRiferimentoMessaggio());
				if(busta.getSequenza()>0){
					this.keywordLogPersonalizzati.put(CostantiPdD.KEY_SEQUENZA, busta.getSequenza()+"");
				}
				if(busta.getIndirizzoMittente()!=null)
					this.keywordLogPersonalizzati.put(CostantiPdD.KEY_INDIRIZZO_TELEMATICO_MITTENTE_RISPOSTA, busta.getIndirizzoMittente());
				if(busta.getIndirizzoDestinatario()!=null)
					this.keywordLogPersonalizzati.put(CostantiPdD.KEY_INDIRIZZO_TELEMATICO_DESTINATARIO_RISPOSTA, busta.getIndirizzoDestinatario());
				if(busta.getScadenza()!=null)
					this.keywordLogPersonalizzati.put(CostantiPdD.KEY_SCADENZA_BUSTA_RISPOSTA, busta.getScadenza().toString());
			}	
		}
	}
	public void addKeywords(IDSoggetto soggettoFruitore){
		this.addKeywords(soggettoFruitore,null,null);
	}
	public void addKeywords(IDServizio idServizio){
		this.addKeywords(null,idServizio,null);
	}
	public void addKeywords(IDSoggetto soggettoFruitore,IDServizio idServizio,String idMessaggio){
		if(idMessaggio!=null){
			this.keywordLogPersonalizzati.put(CostantiPdD.KEY_ID_MESSAGGIO_RICHIESTA, idMessaggio);
		}
		if(soggettoFruitore!=null){
			this.keywordLogPersonalizzati.put(CostantiPdD.KEY_TIPO_MITTENTE_BUSTA_RICHIESTA, soggettoFruitore.getTipo());
			this.keywordLogPersonalizzati.put(CostantiPdD.KEY_MITTENTE_BUSTA_RICHIESTA, soggettoFruitore.getNome());
		}
		else {
			if(idServizio!=null && idServizio.getTipo()!=null) {
				try{
					String protocollo = ProtocolFactoryManager.getInstance().getProtocolByServiceType(idServizio.getTipo());
					this.keywordLogPersonalizzati.put(CostantiPdD.KEY_TIPO_MITTENTE_BUSTA_RICHIESTA, ProtocolFactoryManager.getInstance().getDefaultOrganizationTypes().get(protocollo));
				}catch(Exception e){
					// ignore
				}
				this.keywordLogPersonalizzati.put(CostantiPdD.KEY_MITTENTE_BUSTA_RICHIESTA, CostantiPdD.SOGGETTO_ANONIMO);
			}
		}
		if(idServizio!=null){
			if(idServizio.getSoggettoErogatore()!=null){
				this.keywordLogPersonalizzati.put(CostantiPdD.KEY_TIPO_DESTINATARIO_BUSTA_RICHIESTA, idServizio.getSoggettoErogatore().getTipo());
				this.keywordLogPersonalizzati.put(CostantiPdD.KEY_DESTINATARIO_BUSTA_RICHIESTA, idServizio.getSoggettoErogatore().getNome());
			}
			this.keywordLogPersonalizzati.put(CostantiPdD.KEY_TIPO_SERVIZIO_BUSTA_RICHIESTA, idServizio.getTipo());
			this.keywordLogPersonalizzati.put(CostantiPdD.KEY_SERVIZIO_BUSTA_RICHIESTA, idServizio.getNome());
			if(idServizio.getVersione()!=null){
				this.keywordLogPersonalizzati.put(CostantiPdD.KEY_VERSIONE_SERVIZIO_BUSTA_RICHIESTA, idServizio.getVersione().intValue()+"");
			}
			if(idServizio.getAzione()!=null)
				this.keywordLogPersonalizzati.put(CostantiPdD.KEY_AZIONE_BUSTA_RICHIESTA, idServizio.getAzione());
			try{
				this.keywordLogPersonalizzati.put(CostantiPdD.KEY_URI_ACCORDO_PARTE_SPECIFICA, IDServizioFactory.getInstance().getUriFromIDServizio(idServizio));
			}catch(Exception e){
				throw new RuntimeException(e.getMessage(),e);
			}
		}
	}
	public void updateKeywordIdMessaggioRichiesta(String idMessaggio){
		if(idMessaggio!=null){
			this.keywordLogPersonalizzati.put(CostantiPdD.KEY_ID_MESSAGGIO_RICHIESTA, idMessaggio);
		}
	}
	public String replaceKeywords(String msg){
				
		/* Potenzialmente N^2	
		String tmp = msg;
		Enumeration<String> keys = this.keywordLogPersonalizzati.keys();
		while(keys.hasMoreElements()){
			String key = keys.nextElement();
			tmp = tmp.replace(key, this.keywordLogPersonalizzati.get(key));
		}
		return tmp;
		*/

		/* Costoso e inutile per quasi tutti i diagnostici
		// Check di esistenza di almeno 2 '@'
		if(msg!=null && msg.length()>2){
			int index1 = msg.indexOf("@");
			int index2 = msg.indexOf("@",index1+1);
			if(index1<0 || index2<0){
				return msg; // non serve il replace
			}
		}
		*/
		
		StringBuilder bf = new StringBuilder();
		StringBuilder keyword = new StringBuilder();
		boolean separator = false;
		char separatorChar = '@';
		for(int i=0; i<msg.length(); i++){
			char ch = msg.charAt(i);
			if(separatorChar == ch){
				if(separator==false){
					// inizio keyword
					keyword.append(separatorChar);
					separator = true;
				}
				else{
					// fine keyword
					keyword.append(separatorChar);
					String valoreRimpiazzato = this.keywordLogPersonalizzati.get(keyword.toString());
					if(valoreRimpiazzato==null){
						// keyword non esistente, devo utilizzare l'originale
						bf.append(keyword.toString());
					}else{
						bf.append(valoreRimpiazzato);
					}
					keyword.setLength(0);
					separator=false;
				}
			}else{
				if(separator){
					// sto scrivendo la keyword
					keyword.append(ch);
				}else{
					bf.append(ch);
				}
			}
		}
		if ( separator )
			bf.append(keyword.toString());
		return bf.toString();
	}
	
	private String prefixMsgPersonalizzati = null;
	public String getPrefixMsgPersonalizzati() {
		return this.prefixMsgPersonalizzati;
	}
	public void setPrefixMsgPersonalizzati(String v){
		this.prefixMsgPersonalizzati = v;
	}
	
	public int getLivello(String keyLivelloPersonalizzato){
		return getLivello(this.prefixMsgPersonalizzati, keyLivelloPersonalizzato);
	}
	public int getLivello(String prefix,String keyLivelloPersonalizzato){
		if(this.msgDiagPropertiesReader==null){
			return -1;
		}
		Integer livello = this.msgDiagPropertiesReader.getLivello(prefix,keyLivelloPersonalizzato);
		if(livello!=null){
			return livello;
		} else{
			return -1;
		}
	}
	
	public String getCodice(String keyCodicePersonalizzato){
		return getCodice(this.prefixMsgPersonalizzati, keyCodicePersonalizzato);
	}
	public String getCodice(String prefix,String keyCodicePersonalizzato){
		if(this.msgDiagPropertiesReader==null){
			return "PropertiesReader dei Messaggi Diagnostici non inizializzato";
		}
		return this.msgDiagPropertiesReader.getCodice(prefix,keyCodicePersonalizzato);
	}
	
	public String getMessaggio(String keyMsgPersonalizzato){
		return this.getMessaggio(this.prefixMsgPersonalizzati, keyMsgPersonalizzato, false);
	}
	public String getMessaggio(String prefix,String keyMsgPersonalizzato){
		return this.getMessaggio(prefix, keyMsgPersonalizzato, false);
	}
	public String getMessaggio_replaceKeywords(String keyMsgPersonalizzato){
		return this.getMessaggio(this.prefixMsgPersonalizzati, keyMsgPersonalizzato, true);
	}
	public String getMessaggio_replaceKeywords(String prefix,String keyMsgPersonalizzato){
		return this.getMessaggio(prefix, keyMsgPersonalizzato, true);
	}
	private String getMessaggio(String prefix,String keyMsgPersonalizzato,boolean replaceKeywords){
		if(this.msgDiagPropertiesReader==null){
			return "PropertiesReader dei Messaggi Diagnostici non inizializzato";
		}
		String msgTmp = this.msgDiagPropertiesReader.getMessaggio(prefix,keyMsgPersonalizzato);
		if(msgTmp==null){
			msgTmp = "Messaggio diagnostico ["+prefix+keyMsgPersonalizzato+"] non definito nella configurazione della porta di dominio??";
		}
		if(replaceKeywords){
			String msgReplaceKey = this.replaceKeywords(msgTmp);
			return msgReplaceKey;
		}else{
			return msgTmp;
		}
	}
	
	
	
	
	

	
	
	
	
	
	




	/** ----------------- METODI DI LOGGING (Messaggi Diagnostici) ---------------- */

	private void setEmitErrorConditionInContext(int livelloLog){
		if(this.pddContext!=null){
			if(livelloLog<=LogLevels.SEVERITA_ERROR_INTEGRATION){
				if(this.pddContext.containsKey(Costanti.EMESSI_DIAGNOSTICI_ERRORE)==false){
					this.pddContext.addObject(Costanti.EMESSI_DIAGNOSTICI_ERRORE, "true");
				}
			}
		}
	}
	
    public void emitRicezioneMessaggioModuloBuste(MsgDiagnostico msgDiag, PdDContext pddContext, boolean mittenteAnonimo) {
		if(mittenteAnonimo) {
			msgDiag.logPersonalizzato("ricezioneMessaggio.mittenteAnonimo");
		}
		else {
			// verifico che l'identita sia stata definita nel contesto del diagnostico non come 'Anonimo'
			if(this.keywordLogPersonalizzati!=null) {
				String v = this.keywordLogPersonalizzati.get(CostantiPdD.KEY_MITTENTE_BUSTA_RICHIESTA);
				if(v==null || "".equals(v) || CostantiPdD.SOGGETTO_ANONIMO.equals(v)) {
					mittenteAnonimo = true;
				}
			}
			if(mittenteAnonimo) {
				msgDiag.logPersonalizzato("ricezioneMessaggio.mittenteAnonimo");
			}
			else {
				msgDiag.logPersonalizzato("ricezioneMessaggio");
			}
		}
   }
	
	/**
	 * Il Metodo si occupa di creare un messaggio di livello 'Personalizzato'. 
	 *
	 * 
	 */
	public void logPersonalizzato_custom(String messaggio, int livelloLog, String codiceDiagnostico){
		_logPersonalizzato(null, null, null,
				messaggio, livelloLog, codiceDiagnostico);
	}
	public void logPersonalizzato_custom(String prefix, String messaggio, int livelloLog, String codiceDiagnostico){
		_logPersonalizzato(prefix, null, null,
				messaggio, livelloLog, codiceDiagnostico);
	}
	public void logPersonalizzato_prefix(String prefix, String idModuloFunzionale,String idDiagnostico){
		_logPersonalizzato(prefix, idModuloFunzionale, idDiagnostico,
				null,null,null);
	}
	public void logPersonalizzato_prefix(String prefix, String idDiagnostico){
		_logPersonalizzato(prefix, null, idDiagnostico,
				null,null,null);
	}
	public void logPersonalizzato(String idModuloFunzionale,String idDiagnostico){
		_logPersonalizzato(null, idModuloFunzionale, idDiagnostico,
				null,null,null);
	}
	public void logPersonalizzato(String idDiagnostico){
		_logPersonalizzato(null, null, idDiagnostico,
				null,null,null);
	}
	private void _logPersonalizzato(String prefix, String idModuloFunzionale,String idDiagnostico,
			String messaggioFromParam, Integer livelloLogFromParam, String codiceDiagnosticoFromParam) {

		if(this.msgDiagPropertiesReader==null){
			logError("MsgDiagnostico.logPersonalizzato [Risorsa non inizializzata], messaggio per idModuloFunzionale["+idModuloFunzionale+"] idDiagnostico["+idDiagnostico+"]");
			return;
		}
		
		int livelloLog = -1;
		if(livelloLogFromParam!=null) {
			livelloLog = livelloLogFromParam;
		}
		else {
			if(idModuloFunzionale!=null) {
				livelloLog = this.getLivello(idModuloFunzionale,idDiagnostico);
			}
			else {
				livelloLog = this.getLivello(idDiagnostico);
			}
		}
		
		int severitaLogEmessoPerFiltro = livelloLog;
		int severitaLivelloOpenSPCoop2 = LogLevels.toIntervalloOpenSPCoop2(livelloLog);
		if(severitaLivelloOpenSPCoop2<0 || severitaLivelloOpenSPCoop2>7){
			logError("MsgDiagnostico.logPersonalizzato error, conversione a livello OpenSPCoop non riuscita ["+severitaLivelloOpenSPCoop2+"]");
			return;
		}
		this.setEmitErrorConditionInContext(severitaLivelloOpenSPCoop2);
		Level logLevelseveritaLivelloLog4J = LogLevels.toLog4J(severitaLivelloOpenSPCoop2);
		int severitaRichiestaPdD = this.msgDiagPropertiesReader.getFiltroMsgDiagnosticoOpenSPCoop2level7();
		int severitaLog4JRichiestaPdD = this.msgDiagPropertiesReader.getFiltroMsgDiagnosticoOpenSPCoop2level7();
		ConfigurazionePdDManager configurazionePdDReader = getConfigurazionePdDManager();
		if(configurazionePdDReader!=null && configurazionePdDReader.isInitializedConfigurazionePdDReader()){
			severitaRichiestaPdD = this.msgDiagPropertiesReader.getValoreFiltroFromValoreOpenSPCoop2(configurazionePdDReader.getSeveritaMessaggiDiagnostici());
			severitaLog4JRichiestaPdD = this.msgDiagPropertiesReader.getValoreFiltroFromValoreOpenSPCoop2(configurazionePdDReader.getSeveritaLog4JMessaggiDiagnostici());
		}
		if(this.severitaPorta!=null) {
			severitaRichiestaPdD = this.msgDiagPropertiesReader.getValoreFiltroFromValoreOpenSPCoop2(LogLevels.toOpenSPCoop2(this.severitaPorta.getValue()));
		}
		
		try{
			boolean emettiLogSuFile = (severitaLogEmessoPerFiltro <= severitaLog4JRichiestaPdD);
			boolean emettiLog = (severitaLogEmessoPerFiltro <= severitaRichiestaPdD);
			if(!emettiLogSuFile && !emettiLog) {
				return;
			}
			
			String messaggio = null;
			String codiceDiagnostico = null;
			if(messaggioFromParam!=null) {
				messaggio = messaggioFromParam;
				codiceDiagnostico = codiceDiagnosticoFromParam;
			}
			else {
				if(idModuloFunzionale!=null) {
					messaggio = this.getMessaggio(idModuloFunzionale,idDiagnostico); 
					codiceDiagnostico = this.getCodice(idModuloFunzionale,idDiagnostico);
				}
				else {
					messaggio = this.getMessaggio(idDiagnostico); 
					codiceDiagnostico = this.getCodice(idDiagnostico);
				}
			}
			if(messaggio==null){
				logError("MsgDiagnostico.logPersonalizzato error, messaggio non definito.");
				return;
			}
						
			// Data
			Date gdo = DateManager.getDate();
			if(this.openspcoopProperties.generazioneDateCasualiLogAbilitato() && this.pddContext!=null && this.pddContext.getObject(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE)!=null){
				gdo = this.generatoreDateCasuali.getProssimaData((String)this.pddContext.getObject(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE));
			}
			
			// Replace keyword
			String msgReplaceKey = this.replaceKeywords(messaggio);	
			if(prefix!=null) {
				msgReplaceKey = prefix + msgReplaceKey;
			}
			org.openspcoop2.protocol.sdk.diagnostica.MsgDiagnostico msgDiag = this.getMsgDiagnostico(gdo, severitaLivelloOpenSPCoop2, msgReplaceKey, codiceDiagnostico);
			
			
			// Messaggio diagnostici
			//System.out.println("1. ["+severitaLogEmessoPerFiltro+"] <= ["+severitaRichiestaPdD+"]");
			if( emettiLogSuFile ){

				if(OpenSPCoop2Logger.loggerMsgDiagnosticoAbilitato){
					try{
						String xml = this.diagnosticoBuilder.toString(msgDiag,TipoSerializzazione.DEFAULT);
						this.loggerMsgDiagnostico.log(logLevelseveritaLivelloLog4J,xml);
					}catch(Exception e){
						logError("Errore durante l'emissione del msg diagnostico su log4j (struttura xml): "+e.getMessage(),e);
						gestioneErroreDiagnostica(e);
					}
				}
				
			}
			
			// Messaggio diagnostici
			//System.out.println("1. ["+severitaLogEmessoPerFiltro+"] <= ["+severitaRichiestaPdD+"]");
			if( emettiLog ){

				if(msgDiag.getIdTransazione()!=null) {
					
					// TransazioneContext
					if(this.openspcoopProperties.isTransazioniSaveDiagnosticiInUniqueTransaction()) {
						this.logMsgDiagnosticoInTransactionContext(msgDiag);
					}
					
					// Msg Diagnostico personalizzato
					for(int i=0; i<this.loggerMsgDiagnosticoOpenSPCoopAppender.size();i++){
						try{
							this.loggerMsgDiagnosticoOpenSPCoopAppender.get(i).log(this.getConnectionFromState(),msgDiag);
						}catch(Exception e){
							logError("Errore durante l'emissione del msg diagnostico personalizzato ["+this.tipoMsgDiagnosticoOpenSPCoopAppender.get(i)+"]: "+e.getMessage(),e);
							gestioneErroreDiagnostica(e);
						}
					}
				}
				else {
					// i diagnostici di sistema comunque non li registro sugli appender personalizzati.
					String message = OpenSPCoop2Logger.humanReadable(msgDiag,this.idCorrelazioneApplicativa,this.idCorrelazioneRisposta,
							this.porta,this.delegata,
							this.fruitore,this.servizio,this.servizioApplicativo,this.protocolFactory);
					try{
						this.loggerResource.log(logLevelseveritaLivelloLog4J,message);

					}catch(Exception e){
						logError("Errore durante l'emissione del msg diagnostico 'human readable' nel log delle risorse (id transazione non fornito): "+e.getMessage(),e);
						gestioneErroreDiagnostica(e);
					}
				}
			}

			// Human Readable
			// System.out.println("2. ["+severitaLogEmessoPerFiltro+"] <= ["+severitaOpenSPCoopRichiestaPdD+"]");
			boolean humanReadableAbilitato = false;
			if( IntegrationManager.ID_MODULO.equals(this.idModulo)){ 
				humanReadableAbilitato = OpenSPCoop2Logger.loggerIntegrationManagerAbilitato;
			}else{
				humanReadableAbilitato = OpenSPCoop2Logger.loggerMsgDiagnosticoReadableAbilitato;
			}
			if( humanReadableAbilitato &&  (emettiLogSuFile) ){
				
				//ProtocolFactory protocolFactory = ProtocolFactoryManager.getInstance((String) this.pddContext.getObject(CostantiPdD.PROTOCOLLO));
				String message = OpenSPCoop2Logger.humanReadable(msgDiag,this.idCorrelazioneApplicativa,this.idCorrelazioneRisposta,
						this.porta,this.delegata,
						this.fruitore,this.servizio,this.servizioApplicativo,this.protocolFactory);
				try{
					if( IntegrationManager.ID_MODULO.equals(this.idModulo)){ 
						this.loggerIntegrationManager.log(logLevelseveritaLivelloLog4J,message);
					}else{
						this.loggerOpenSPCoop2.log(logLevelseveritaLivelloLog4J,message);
					}
				}catch(Exception e){
					logError("Errore durante l'emissione del msg diagnostico 'human readable': "+e.getMessage(),e);
					gestioneErroreDiagnostica(e);
				}
			}
			
			// Log fatal emesso su console jboss
			if(msgReplaceKey!=null && severitaLivelloOpenSPCoop2==0){
				// dalla versione 2.3, essendo utilizzato slf4j, si converte fatal in error.
				//this.loggerOpenSPCoop2Fatal.log(LogLevels.LOG_LEVEL_FATAL,msgReplaceKey);
				this.loggerOpenSPCoop2Fatal.error(msgReplaceKey);
			}

		}catch(Exception e){
			logError("MsgDiagnostico.logPersonalizzato error "+e.getMessage(),e);
			gestioneErroreDiagnostica(e);
		}
	}
	
	
	public void addLogPersonalizzato(String idModuloFunzionale,String idDiagnostico, Transaction transaction, StringBuilder errorLog){

		if(this.msgDiagPropertiesReader==null){
			logError("MsgDiagnostico.logPersonalizzato [Risorsa non inizializzata], messaggio per idModuloFunzionale["+idModuloFunzionale+"] idDiagnostico["+idDiagnostico+"]");
			return;
		}
		
		int livelloLog = -1;
		if(idModuloFunzionale!=null) {
			livelloLog = this.getLivello(idModuloFunzionale,idDiagnostico);
		}
		else {
			livelloLog = this.getLivello(idDiagnostico);
		}
		
		int severitaLogEmessoPerFiltro = livelloLog;
		int severitaLivelloOpenSPCoop2 = LogLevels.toIntervalloOpenSPCoop2(livelloLog);
		if(severitaLivelloOpenSPCoop2<0 || severitaLivelloOpenSPCoop2>7){
			logError("MsgDiagnostico.logPersonalizzato error, conversione a livello OpenSPCoop non riuscita ["+severitaLivelloOpenSPCoop2+"]");
			return;
		}
		this.setEmitErrorConditionInContext(severitaLivelloOpenSPCoop2);
		int severitaRichiestaPdD = this.msgDiagPropertiesReader.getFiltroMsgDiagnosticoOpenSPCoop2level7();
		ConfigurazionePdDManager configurazionePdDReader = getConfigurazionePdDManager();
		if(configurazionePdDReader!=null && configurazionePdDReader.isInitializedConfigurazionePdDReader()){
			severitaRichiestaPdD = this.msgDiagPropertiesReader.getValoreFiltroFromValoreOpenSPCoop2(configurazionePdDReader.getSeveritaMessaggiDiagnostici());
		}
		if(this.severitaPorta!=null) {
			severitaRichiestaPdD = this.msgDiagPropertiesReader.getValoreFiltroFromValoreOpenSPCoop2(LogLevels.toOpenSPCoop2(this.severitaPorta.getValue()));
		}
		
		try{
			boolean emettiLog = (severitaLogEmessoPerFiltro <= severitaRichiestaPdD);
			if(!emettiLog) {
				return;
			}
			
			String messaggio = null;
			String codiceDiagnostico = null;
			if(idModuloFunzionale!=null) {
				messaggio = this.getMessaggio(idModuloFunzionale,idDiagnostico); 
				codiceDiagnostico = this.getCodice(idModuloFunzionale,idDiagnostico);
			}
			else {
				messaggio = this.getMessaggio(idDiagnostico); 
				codiceDiagnostico = this.getCodice(idDiagnostico);
			}
			if(messaggio==null){
				logError("MsgDiagnostico.logPersonalizzato error, messaggio non definito.");
				return;
			}
						
			// Data
			Date gdo = DateManager.getDate();
			if(this.openspcoopProperties.generazioneDateCasualiLogAbilitato() && this.pddContext!=null && this.pddContext.getObject(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE)!=null){
				gdo = this.generatoreDateCasuali.getProssimaData((String)this.pddContext.getObject(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE));
			}
			
			// Replace keyword
			String msgReplaceKey = this.replaceKeywords(messaggio);	
			org.openspcoop2.protocol.sdk.diagnostica.MsgDiagnostico msgDiag = this.getMsgDiagnostico(gdo, severitaLivelloOpenSPCoop2, msgReplaceKey, codiceDiagnostico);
			if(errorLog!=null) {
				errorLog.append(msgDiag.getMessaggio());
			}
			transaction.addMsgDiagnostico(msgDiag);
		}catch(Exception e){
			logError("MsgDiagnostico.logPersonalizzato error "+e.getMessage(),e);
			// log su core solamente
			/**gestioneErroreDiagnostica(e);*/
		}
	}
	
	
	public void logErroreGenerico(Throwable e, String posizioneErrore) {
		String msg = e!=null ? Utilities.readFirstErrorValidMessageFromException(e) : "Internal Error";
		this.logErroreGenerico(msg,posizioneErrore);
		// inoltre registro l'errore nel logger_core di openspcoop
		if(this.loggerOpenSPCoop2Core!=null)
			this.loggerOpenSPCoop2Core.error(posizioneErrore+": "+msg,e);
	}
	public void logErroreGenerico(String message, String posizioneErrore) {
		this.addKeyword(CostantiPdD.KEY_ERRORE_PROCESSAMENTO, message);
		this.addKeyword(CostantiPdD.KEY_POSIZIONE_ERRORE, posizioneErrore);
		this.logPersonalizzato(MsgDiagnosticiProperties.MSG_DIAG_ALL,"erroreGenerico");
	}
	
	
	public void logFatalError(Throwable e, String posizioneErrore) {
		String msg = e!=null ? Utilities.readFirstErrorValidMessageFromException(e) : "Internal Error";
		this.logFatalError(msg,posizioneErrore);
		// inoltre registro l'errore nel logger_core di openspcoop
		if(this.loggerOpenSPCoop2Core!=null){
			// dalla versione 2.3, essendo utilizzato slf4j, si converte fatal in error.
			//this.loggerOpenSPCoop2Core.fatal(posizioneErrore+": "+msg,e);
			this.loggerOpenSPCoop2Core.error(posizioneErrore+": "+msg,e);
		}
	}
	public void logFatalError(String message, String posizioneErrore) {
		this.addKeyword(CostantiPdD.KEY_ERRORE_PROCESSAMENTO, message);
		this.addKeyword(CostantiPdD.KEY_POSIZIONE_ERRORE, posizioneErrore);
		this.logPersonalizzato(MsgDiagnosticiProperties.MSG_DIAG_ALL,"erroreGenericoMalfunzionamentoPdD");
	}
	
	
	public void logStartupError(Throwable e, String posizioneErrore) {
		String msg = null;
		if(e instanceof NullPointerException)
			 msg = "NullPointerException";
		else
			msg = e.getMessage();
		this.logStartupError(msg,posizioneErrore);
		// inoltre registro l'errore nel logger_core di openspcoop
		if(this.loggerOpenSPCoop2Core!=null){
			// dalla versione 2.3, essendo utilizzato slf4j, si converte fatal in error.
			//this.loggerOpenSPCoop2Core.fatal(posizioneErrore+": "+msg,e);
			this.loggerOpenSPCoop2Core.error(posizioneErrore+": "+msg,e);
		}
	}
	public void logStartupError(String message, String posizioneErrore) {
		this.addKeyword(CostantiPdD.KEY_ERRORE_PROCESSAMENTO, message);
		this.addKeyword(CostantiPdD.KEY_POSIZIONE_ERRORE, posizioneErrore);
		this.logPersonalizzato(MsgDiagnosticiProperties.MSG_DIAG_OPENSPCOOP_STARTUP,"erroreGenerico");
	}
	
	public void logDisconnectError(Throwable e, String url) {
		String msg = null;
		if(e instanceof NullPointerException)
			 msg = "NullPointerException";
		else
			msg = e.getMessage();
		this.logDisconnectError(msg,url);
		// inoltre registro l'errore nel logger_core di openspcoop
		if(this.loggerOpenSPCoop2Core!=null)
			this.loggerOpenSPCoop2Core.error(url+": "+msg,e);
	}
	public void logDisconnectError(String message, String url) {
		this.addKeyword(CostantiPdD.KEY_ERRORE_PROCESSAMENTO, message);
		this.addKeyword(CostantiPdD.KEY_POSIZIONE_ERRORE, url);
		this.logPersonalizzato(MsgDiagnosticiProperties.MSG_DIAG_ALL,"connessioneUscita.disconnectError");
	}
	
	

	
	
	
	
	
	
	/**
	 * Il Metodo si occupa di creare un messaggio di livello 'FATAL'. 
	 * @deprecated in funzione del metodo logPersonalizzato
	 *
	 * @param msg Messaggio da registrare
	 * 
	 */
	@Deprecated
	public void fatal(String msg) {

		String codiceDiagnostico = null; // con questi metodi viene fornito un unico codice diagnostico associato ad ogni messaggio di questo livello
		
		this.setEmitErrorConditionInContext(LogLevels.SEVERITA_FATAL);
				
		int severitaLogEmessoPerFiltro = LogLevels.toIntervalloLog4J(LogLevels.SEVERITA_FATAL);
		int severitaRichiestaPdD = this.msgDiagPropertiesReader.getFiltroMsgDiagnosticoOpenSPCoop2level7();
		int severitaLog4JRichiestaPdD = this.msgDiagPropertiesReader.getFiltroMsgDiagnosticoOpenSPCoop2level7();
		ConfigurazionePdDManager configurazionePdDReader = getConfigurazionePdDManager();
		if(configurazionePdDReader!=null && configurazionePdDReader.isInitializedConfigurazionePdDReader()){
			severitaRichiestaPdD = this.msgDiagPropertiesReader.getValoreFiltroFromValoreOpenSPCoop2(configurazionePdDReader.getSeveritaMessaggiDiagnostici());
			severitaLog4JRichiestaPdD = this.msgDiagPropertiesReader.getValoreFiltroFromValoreOpenSPCoop2(configurazionePdDReader.getSeveritaLog4JMessaggiDiagnostici());
		}
		if(this.severitaPorta!=null) {
			severitaRichiestaPdD = this.msgDiagPropertiesReader.getValoreFiltroFromValoreOpenSPCoop2(LogLevels.toOpenSPCoop2(this.severitaPorta.getValue()));
		}
		
		try{
			
			boolean emettiLogSuFile = (severitaLogEmessoPerFiltro <= severitaLog4JRichiestaPdD);
			boolean emettiLog = (severitaLogEmessoPerFiltro <= severitaRichiestaPdD);
			if(!emettiLogSuFile && !emettiLog) {
				return;
			}
			
			codiceDiagnostico = this.msgDiagPropertiesReader.getCodiceDiagnosticoFatal();
			
			// Data
			Date gdo = DateManager.getDate();
			if(this.openspcoopProperties.generazioneDateCasualiLogAbilitato() && this.pddContext!=null && this.pddContext.getObject(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE)!=null){
				gdo = this.generatoreDateCasuali.getProssimaData((String)this.pddContext.getObject(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE));
			}
			
			org.openspcoop2.protocol.sdk.diagnostica.MsgDiagnostico msgDiag = null;
			
			// Messaggio diagnostici
			// System.out.println("1. ["+severitaLogEmessoPerFiltro+"] <= ["+severitaRichiestaPdD+"]");
			if( emettiLogSuFile ){

				if(msgDiag==null){
					msgDiag = this.getMsgDiagnostico(gdo, LogLevels.SEVERITA_FATAL, msg, codiceDiagnostico);
				}
				
				if(OpenSPCoop2Logger.loggerMsgDiagnosticoAbilitato){
					try{
						String xml = this.diagnosticoBuilder.toString(msgDiag,TipoSerializzazione.DEFAULT);
						this.loggerMsgDiagnostico.log(LogLevels.LOG_LEVEL_FATAL,xml);
					}catch(Exception e){
						logError("Errore durante l'emissione del msg diagnostico su log4j (struttura xml): "+e.getMessage(),e);
						gestioneErroreDiagnostica(e);
					}
				}
					
			}
			
			// Messaggio diagnostici
			// System.out.println("1. ["+severitaLogEmessoPerFiltro+"] <= ["+severitaRichiestaPdD+"]");
			if( emettiLog ){
					
				if(msgDiag==null){
					msgDiag = this.getMsgDiagnostico(gdo, LogLevels.SEVERITA_FATAL, msg, codiceDiagnostico);
				}
				
				if(msgDiag.getIdTransazione()!=null) {
					
					// TransazioneContext
					if(this.openspcoopProperties.isTransazioniSaveDiagnosticiInUniqueTransaction()) {
						this.logMsgDiagnosticoInTransactionContext(msgDiag);
					}
					
					// Msg Diagnostico personalizzato
					for(int i=0; i<this.loggerMsgDiagnosticoOpenSPCoopAppender.size();i++){
						try{
							this.loggerMsgDiagnosticoOpenSPCoopAppender.get(i).log(this.getConnectionFromState(),msgDiag);
						}catch(Exception e){
							logError("Errore durante l'emissione del msg diagnostico personalizzato ["+this.tipoMsgDiagnosticoOpenSPCoopAppender.get(i)+"]: "+e.getMessage(),e);
							gestioneErroreDiagnostica(e);
						}
					}
				}
				else {
					// i diagnostici di sistema comunque non li registro sugli appender personalizzati.
					String message = OpenSPCoop2Logger.humanReadable(msgDiag,this.idCorrelazioneApplicativa,this.idCorrelazioneRisposta,
							this.porta,this.delegata,
							this.fruitore,this.servizio,this.servizioApplicativo,this.protocolFactory);
					try{
						this.loggerResource.log(LogLevels.LOG_LEVEL_FATAL,message);

					}catch(Exception e){
						logError("Errore durante l'emissione del msg diagnostico 'human readable' nel log delle risorse (id transazione non fornito): "+e.getMessage(),e);
						gestioneErroreDiagnostica(e);
					}
				}
			}

			// Human Readable
			// System.out.println("2. ["+severitaLogEmessoPerFiltro+"] <= ["+severitaOpenSPCoopRichiestaPdD+"]");
			boolean humanReadableAbilitato = false;
			if( IntegrationManager.ID_MODULO.equals(this.idModulo)){ 
				humanReadableAbilitato = OpenSPCoop2Logger.loggerIntegrationManagerAbilitato;
			}else{
				humanReadableAbilitato = OpenSPCoop2Logger.loggerMsgDiagnosticoReadableAbilitato;
			}
			if( humanReadableAbilitato &&  (emettiLogSuFile) ){
				
				if(msgDiag==null){
					msgDiag = this.getMsgDiagnostico(gdo, LogLevels.SEVERITA_FATAL, msg, codiceDiagnostico);
				}
				
				String message = OpenSPCoop2Logger.humanReadable(msgDiag,this.idCorrelazioneApplicativa,this.idCorrelazioneRisposta,
						this.porta,this.delegata,
						this.fruitore,this.servizio,this.servizioApplicativo, this.protocolFactory);
				try{
					if( IntegrationManager.ID_MODULO.equals(this.idModulo)){ 
						this.loggerIntegrationManager.log(LogLevels.LOG_LEVEL_FATAL,message);
					}else{
						this.loggerOpenSPCoop2.log(LogLevels.LOG_LEVEL_FATAL,message);
					}
				}catch(Exception e){
					logError("Errore durante l'emissione del msg diagnostico 'human readable': "+e.getMessage(),e);
					gestioneErroreDiagnostica(e);
				}
			}
			
			// Log emesso su console jboss
			// dalla versione 2.3, essendo utilizzato slf4j, si converte fatal in error.
			//this.loggerOpenSPCoop2Fatal.log(LogLevels.LOG_LEVEL_FATAL,msg);
			this.loggerOpenSPCoop2Fatal.error(msg);

		}catch(Exception e){
			logError("MsgDiagnostico.fatalOpenSPCoop error "+e.getMessage(),e);
			gestioneErroreDiagnostica(e);
		}
	}

	/**
	 * Il Metodo si occupa di creare un messaggio di livello 'ERROR-PROTOCOL'.
	 * @deprecated in funzione del metodo logPersonalizzato 
	 *
	 * @param msg Messaggio da registrare
	 * 
	 */
	@Deprecated
	public void errorProtocol(String msg) {
		
		String codiceDiagnostico = null; // con questi metodi viene fornito un unico codice diagnostico associato ad ogni messaggio di questo livello
		
		this.setEmitErrorConditionInContext(LogLevels.SEVERITA_ERROR_PROTOCOL);
		
		int severitaLogEmessoPerFiltro = LogLevels.toIntervalloLog4J(LogLevels.SEVERITA_ERROR_PROTOCOL);
		int severitaRichiestaPdD = this.msgDiagPropertiesReader.getFiltroMsgDiagnosticoOpenSPCoop2level7();
		int severitaLog4JRichiestaPdD = this.msgDiagPropertiesReader.getFiltroMsgDiagnosticoOpenSPCoop2level7();
		ConfigurazionePdDManager configurazionePdDReader = getConfigurazionePdDManager();
		if(configurazionePdDReader!=null && configurazionePdDReader.isInitializedConfigurazionePdDReader()){
			severitaRichiestaPdD = this.msgDiagPropertiesReader.getValoreFiltroFromValoreOpenSPCoop2(configurazionePdDReader.getSeveritaMessaggiDiagnostici());
			severitaLog4JRichiestaPdD = this.msgDiagPropertiesReader.getValoreFiltroFromValoreOpenSPCoop2(configurazionePdDReader.getSeveritaLog4JMessaggiDiagnostici());
		}
		if(this.severitaPorta!=null) {
			severitaRichiestaPdD = this.msgDiagPropertiesReader.getValoreFiltroFromValoreOpenSPCoop2(LogLevels.toOpenSPCoop2(this.severitaPorta.getValue()));
		}
		
		
		try{
			
			boolean emettiLogSuFile = (severitaLogEmessoPerFiltro <= severitaLog4JRichiestaPdD);
			boolean emettiLog = (severitaLogEmessoPerFiltro <= severitaRichiestaPdD);
			if(!emettiLogSuFile && !emettiLog) {
				return;
			}
			
			codiceDiagnostico = this.msgDiagPropertiesReader.getCodiceDiagnosticoErrorProtocol();
			
			// Data
			Date gdo = DateManager.getDate();
			if(this.openspcoopProperties.generazioneDateCasualiLogAbilitato() && this.pddContext!=null && this.pddContext.getObject(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE)!=null){
				gdo = this.generatoreDateCasuali.getProssimaData((String)this.pddContext.getObject(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE));
			}
			
			org.openspcoop2.protocol.sdk.diagnostica.MsgDiagnostico msgDiag = null;
						
			// Messaggio diagnostici
			// System.out.println("1. ["+severitaLogEmessoPerFiltro+"] <= ["+severitaRichiestaPdD+"]");
			if( emettiLogSuFile){

				if(msgDiag==null){
					msgDiag = this.getMsgDiagnostico(gdo, LogLevels.SEVERITA_ERROR_PROTOCOL, msg, codiceDiagnostico);
				}
				
				if(OpenSPCoop2Logger.loggerMsgDiagnosticoAbilitato){
					try{
						String xml = this.diagnosticoBuilder.toString(msgDiag,TipoSerializzazione.DEFAULT);
						this.loggerMsgDiagnostico.log(LogLevels.LOG_LEVEL_ERROR_PROTOCOL,xml);
					}catch(Exception e){
						logError("Errore durante l'emissione del msg diagnostico su log4j (struttura xml): "+e.getMessage(),e);
						gestioneErroreDiagnostica(e);
					}
				}
					
			}
			
			// Messaggio diagnostici
			// System.out.println("1. ["+severitaLogEmessoPerFiltro+"] <= ["+severitaRichiestaPdD+"]");
			if( emettiLog){
					
				if(msgDiag==null){
					msgDiag = this.getMsgDiagnostico(gdo, LogLevels.SEVERITA_ERROR_PROTOCOL, msg, codiceDiagnostico);
				}
				
				if(msgDiag.getIdTransazione()!=null) {
					
					// TransazioneContext
					if(this.openspcoopProperties.isTransazioniSaveDiagnosticiInUniqueTransaction()) {
						this.logMsgDiagnosticoInTransactionContext(msgDiag);
					}
					
					// Msg Diagnostico personalizzato
					for(int i=0; i<this.loggerMsgDiagnosticoOpenSPCoopAppender.size();i++){
						try{
							this.loggerMsgDiagnosticoOpenSPCoopAppender.get(i).log(this.getConnectionFromState(),msgDiag);
						}catch(Exception e){
							logError("Errore durante l'emissione del msg diagnostico personalizzato ["+this.tipoMsgDiagnosticoOpenSPCoopAppender.get(i)+"]: "+e.getMessage(),e);
							gestioneErroreDiagnostica(e);
						}
					}
				}
				else {
					// i diagnostici di sistema comunque non li registro sugli appender personalizzati.
					String message = OpenSPCoop2Logger.humanReadable(msgDiag,this.idCorrelazioneApplicativa,this.idCorrelazioneRisposta,
							this.porta,this.delegata,
							this.fruitore,this.servizio,this.servizioApplicativo,this.protocolFactory);
					try{
						this.loggerResource.log(LogLevels.LOG_LEVEL_ERROR_PROTOCOL,message);

					}catch(Exception e){
						logError("Errore durante l'emissione del msg diagnostico 'human readable' nel log delle risorse (id transazione non fornito): "+e.getMessage(),e);
						gestioneErroreDiagnostica(e);
					}
				}
			}

			// Human Readable
			// System.out.println("2. ["+severitaLogEmessoPerFiltro+"] <= ["+severitaOpenSPCoopRichiestaPdD+"]");
			boolean humanReadableAbilitato = false;
			if( IntegrationManager.ID_MODULO.equals(this.idModulo)){ 
				humanReadableAbilitato = OpenSPCoop2Logger.loggerIntegrationManagerAbilitato;
			}else{
				humanReadableAbilitato = OpenSPCoop2Logger.loggerMsgDiagnosticoReadableAbilitato;
			}
			if( humanReadableAbilitato &&  (emettiLogSuFile) ){
				
				if(msgDiag==null){
					msgDiag = this.getMsgDiagnostico(gdo, LogLevels.SEVERITA_ERROR_PROTOCOL, msg, codiceDiagnostico);
				}
				
				//ProtocolFactory protocolFactory = ProtocolFactoryManager.getInstance((String) this.pddContext.getObject(CostantiPdD.PROTOCOLLO));
				String message = OpenSPCoop2Logger.humanReadable(msgDiag,this.idCorrelazioneApplicativa,this.idCorrelazioneRisposta,
						this.porta,this.delegata,
						this.fruitore,this.servizio,this.servizioApplicativo,this.protocolFactory);
				try{
					if( IntegrationManager.ID_MODULO.equals(this.idModulo)){ 
						this.loggerIntegrationManager.log(LogLevels.LOG_LEVEL_ERROR_PROTOCOL,message);
					}else{
						this.loggerOpenSPCoop2.log(LogLevels.LOG_LEVEL_ERROR_PROTOCOL,message);
					}
				}catch(Exception e){
					logError("Errore durante l'emissione del msg diagnostico 'human readable': "+e.getMessage(),e);
					gestioneErroreDiagnostica(e);
				}
			}
		}catch(Exception e){
			logError("MsgDiagnostico.errorProtocol error "+e.getMessage(),e);
			gestioneErroreDiagnostica(e);
		}

	}

	/**
	 * Il Metodo si occupa di creare un messaggio di livello 'ERROR-INTEGRATION'. 
	 * @deprecated in funzione del metodo logPersonalizzato 
	 *
	 * @param msg Messaggio da registrare
	 * 
	 */
	@Deprecated
	public void errorIntegration(String msg) {
		
		String codiceDiagnostico = null; // con questi metodi viene fornito un unico codice diagnostico associato ad ogni messaggio di questo livello
		
		this.setEmitErrorConditionInContext(LogLevels.SEVERITA_ERROR_INTEGRATION);
		
		int severitaLogEmessoPerFiltro = LogLevels.toIntervalloLog4J(LogLevels.SEVERITA_ERROR_INTEGRATION);
		int severitaRichiestaPdD = this.msgDiagPropertiesReader.getFiltroMsgDiagnosticoOpenSPCoop2level7();
		int severitaLog4JRichiestaPdD = this.msgDiagPropertiesReader.getFiltroMsgDiagnosticoOpenSPCoop2level7();
		ConfigurazionePdDManager configurazionePdDReader = getConfigurazionePdDManager();
		if(configurazionePdDReader!=null && configurazionePdDReader.isInitializedConfigurazionePdDReader()){
			severitaRichiestaPdD = this.msgDiagPropertiesReader.getValoreFiltroFromValoreOpenSPCoop2(configurazionePdDReader.getSeveritaMessaggiDiagnostici());
			severitaLog4JRichiestaPdD = this.msgDiagPropertiesReader.getValoreFiltroFromValoreOpenSPCoop2(configurazionePdDReader.getSeveritaLog4JMessaggiDiagnostici());
		}
		if(this.severitaPorta!=null) {
			severitaRichiestaPdD = this.msgDiagPropertiesReader.getValoreFiltroFromValoreOpenSPCoop2(LogLevels.toOpenSPCoop2(this.severitaPorta.getValue()));
		}
		
		try{
			
			boolean emettiLogSuFile = (severitaLogEmessoPerFiltro <= severitaLog4JRichiestaPdD);
			boolean emettiLog = (severitaLogEmessoPerFiltro <= severitaRichiestaPdD);
			if(!emettiLogSuFile && !emettiLog) {
				return;
			}
			
			codiceDiagnostico = this.msgDiagPropertiesReader.getCodiceDiagnosticoErrorIntegration();
			
			// Data
			Date gdo = DateManager.getDate();
			if(this.openspcoopProperties.generazioneDateCasualiLogAbilitato() && this.pddContext!=null && this.pddContext.getObject(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE)!=null){
				gdo = this.generatoreDateCasuali.getProssimaData((String)this.pddContext.getObject(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE));
			}
			
			org.openspcoop2.protocol.sdk.diagnostica.MsgDiagnostico msgDiag = null;
						
			// Messaggio diagnostici
			// System.out.println("1. ["+severitaLogEmessoPerFiltro+"] <= ["+severitaRichiestaPdD+"]");
			if( emettiLogSuFile){

				if(msgDiag==null){
					msgDiag = this.getMsgDiagnostico(gdo, LogLevels.SEVERITA_ERROR_INTEGRATION, msg, codiceDiagnostico);
				}
				
				if(OpenSPCoop2Logger.loggerMsgDiagnosticoAbilitato){
					try{
						String xml = this.diagnosticoBuilder.toString(msgDiag,TipoSerializzazione.DEFAULT);
						this.loggerMsgDiagnostico.log(LogLevels.LOG_LEVEL_ERROR_INTEGRATION,xml);
					}catch(Exception e){
						logError("Errore durante l'emissione del msg diagnostico su log4j (struttura xml): "+e.getMessage(),e);
						gestioneErroreDiagnostica(e);
					}
				}
					
			}	
			
			// Messaggio diagnostici
			// System.out.println("1. ["+severitaLogEmessoPerFiltro+"] <= ["+severitaRichiestaPdD+"]");
			if( emettiLog){
					
				if(msgDiag==null){
					msgDiag = this.getMsgDiagnostico(gdo, LogLevels.SEVERITA_ERROR_INTEGRATION, msg, codiceDiagnostico);
				}
				
				if(msgDiag.getIdTransazione()!=null) {
					
					// TransazioneContext
					if(this.openspcoopProperties.isTransazioniSaveDiagnosticiInUniqueTransaction()) {
						this.logMsgDiagnosticoInTransactionContext(msgDiag);
					}
					
					// Msg Diagnostico personalizzato
					for(int i=0; i<this.loggerMsgDiagnosticoOpenSPCoopAppender.size();i++){
						try{
							this.loggerMsgDiagnosticoOpenSPCoopAppender.get(i).log(this.getConnectionFromState(),msgDiag);
						}catch(Exception e){
							logError("Errore durante l'emissione del msg diagnostico personalizzato ["+this.tipoMsgDiagnosticoOpenSPCoopAppender.get(i)+"]: "+e.getMessage(),e);
							gestioneErroreDiagnostica(e);
						}
					}
				}
				else {
					// i diagnostici di sistema comunque non li registro sugli appender personalizzati.
					String message = OpenSPCoop2Logger.humanReadable(msgDiag,this.idCorrelazioneApplicativa,this.idCorrelazioneRisposta,
							this.porta,this.delegata,
							this.fruitore,this.servizio,this.servizioApplicativo,this.protocolFactory);
					try{
						this.loggerResource.log(LogLevels.LOG_LEVEL_ERROR_INTEGRATION,message);

					}catch(Exception e){
						logError("Errore durante l'emissione del msg diagnostico 'human readable' nel log delle risorse (id transazione non fornito): "+e.getMessage(),e);
						gestioneErroreDiagnostica(e);
					}
				}
			}	

			// Human Readable
			// System.out.println("2. ["+severitaLogEmessoPerFiltro+"] <= ["+severitaOpenSPCoopRichiestaPdD+"]");
			boolean humanReadableAbilitato = false;
			if( IntegrationManager.ID_MODULO.equals(this.idModulo)){ 
				humanReadableAbilitato = OpenSPCoop2Logger.loggerIntegrationManagerAbilitato;
			}else{
				humanReadableAbilitato = OpenSPCoop2Logger.loggerMsgDiagnosticoReadableAbilitato;
			}
			if( humanReadableAbilitato &&  (emettiLogSuFile) ){
				
				if(msgDiag==null){
					msgDiag = this.getMsgDiagnostico(gdo, LogLevels.SEVERITA_ERROR_INTEGRATION, msg, codiceDiagnostico);
				}
				
				//ProtocolFactory protocolFactory = ProtocolFactoryManager.getInstance((String) this.pddContext.getObject(CostantiPdD.PROTOCOLLO));
				String message = OpenSPCoop2Logger.humanReadable(msgDiag,this.idCorrelazioneApplicativa,this.idCorrelazioneRisposta,
						this.porta,this.delegata,
						this.fruitore,this.servizio,this.servizioApplicativo,this.protocolFactory);
				try{
					if( IntegrationManager.ID_MODULO.equals(this.idModulo)){ 
						this.loggerIntegrationManager.log(LogLevels.LOG_LEVEL_ERROR_INTEGRATION,message);
					}else{
						this.loggerOpenSPCoop2.log(LogLevels.LOG_LEVEL_ERROR_INTEGRATION,message);
					}
				}catch(Exception e){
					logError("Errore durante l'emissione del msg diagnostico 'human readable': "+e.getMessage(),e);
					gestioneErroreDiagnostica(e);
				}
			}

		}catch(Exception e){
			logError("MsgDiagnostico.errorIntegration error "+e.getMessage(),e);
			gestioneErroreDiagnostica(e);
		}
	}
	
	/**
	 * Il Metodo si occupa di creare un messaggio di livello 'INFO-PROTOCOL'. 
	 * @deprecated in funzione del metodo logPersonalizzato 
	 *
	 * @param msg Messaggio da registrare
	 * 
	 */
	@Deprecated
	public void infoProtocol(String msg) {
		
		String codiceDiagnostico = null; // con questi metodi viene fornito un unico codice diagnostico associato ad ogni messaggio di questo livello
		
		this.setEmitErrorConditionInContext(LogLevels.SEVERITA_INFO_PROTOCOL);
		
		int severitaLogEmessoPerFiltro = LogLevels.toIntervalloLog4J(LogLevels.SEVERITA_INFO_PROTOCOL);
		int severitaRichiestaPdD = this.msgDiagPropertiesReader.getFiltroMsgDiagnosticoOpenSPCoop2level7();
		int severitaLog4JRichiestaPdD = this.msgDiagPropertiesReader.getFiltroMsgDiagnosticoOpenSPCoop2level7();
		ConfigurazionePdDManager configurazionePdDReader = getConfigurazionePdDManager();
		if(configurazionePdDReader!=null && configurazionePdDReader.isInitializedConfigurazionePdDReader()){
			severitaRichiestaPdD = this.msgDiagPropertiesReader.getValoreFiltroFromValoreOpenSPCoop2(configurazionePdDReader.getSeveritaMessaggiDiagnostici());
			severitaLog4JRichiestaPdD = this.msgDiagPropertiesReader.getValoreFiltroFromValoreOpenSPCoop2(configurazionePdDReader.getSeveritaLog4JMessaggiDiagnostici());
		}
		if(this.severitaPorta!=null) {
			severitaRichiestaPdD = this.msgDiagPropertiesReader.getValoreFiltroFromValoreOpenSPCoop2(LogLevels.toOpenSPCoop2(this.severitaPorta.getValue()));
		}
		
		try{
			
			boolean emettiLogSuFile = (severitaLogEmessoPerFiltro <= severitaLog4JRichiestaPdD);
			boolean emettiLog = (severitaLogEmessoPerFiltro <= severitaRichiestaPdD);
			if(!emettiLogSuFile && !emettiLog) {
				return;
			}
			
			codiceDiagnostico = this.msgDiagPropertiesReader.getCodiceDiagnosticoInfoProtocol();
			
			// Data
			Date gdo = DateManager.getDate();
			if(this.openspcoopProperties.generazioneDateCasualiLogAbilitato() && this.pddContext!=null && this.pddContext.getObject(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE)!=null){
				gdo = this.generatoreDateCasuali.getProssimaData((String)this.pddContext.getObject(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE));
			}
			
			org.openspcoop2.protocol.sdk.diagnostica.MsgDiagnostico msgDiag = null;
						
			// Messaggio diagnostici
			// System.out.println("1. ["+severitaLogEmessoPerFiltro+"] <= ["+severitaRichiestaPdD+"]");
			if( emettiLogSuFile){

				if(msgDiag==null){
					msgDiag = this.getMsgDiagnostico(gdo, LogLevels.SEVERITA_INFO_PROTOCOL, msg, codiceDiagnostico);
				}
				
				if(OpenSPCoop2Logger.loggerMsgDiagnosticoAbilitato){
					try{
						String xml = this.diagnosticoBuilder.toString(msgDiag,TipoSerializzazione.DEFAULT);
						this.loggerMsgDiagnostico.log(LogLevels.LOG_LEVEL_INFO_PROTOCOL,xml);
					}catch(Exception e){
						logError("Errore durante l'emissione del msg diagnostico su log4j (struttura xml): "+e.getMessage(),e);
						gestioneErroreDiagnostica(e);
					}
				}
					
			}
			
			// Messaggio diagnostici
			// System.out.println("1. ["+severitaLogEmessoPerFiltro+"] <= ["+severitaRichiestaPdD+"]");
			if( emettiLog){
					
				if(msgDiag==null){
					msgDiag = this.getMsgDiagnostico(gdo, LogLevels.SEVERITA_INFO_PROTOCOL, msg, codiceDiagnostico);
				}
				
				if(msgDiag.getIdTransazione()!=null) {
					
					// TransazioneContext
					if(this.openspcoopProperties.isTransazioniSaveDiagnosticiInUniqueTransaction()) {
						this.logMsgDiagnosticoInTransactionContext(msgDiag);
					}
					
					// Msg Diagnostico personalizzato	
					for(int i=0; i<this.loggerMsgDiagnosticoOpenSPCoopAppender.size();i++){
						try{
							this.loggerMsgDiagnosticoOpenSPCoopAppender.get(i).log(this.getConnectionFromState(),msgDiag);
						}catch(Exception e){
							logError("Errore durante l'emissione del msg diagnostico personalizzato ["+this.tipoMsgDiagnosticoOpenSPCoopAppender.get(i)+"]: "+e.getMessage(),e);
							gestioneErroreDiagnostica(e);
						}
					}
				}
				else {
					// i diagnostici di sistema comunque non li registro sugli appender personalizzati.
					String message = OpenSPCoop2Logger.humanReadable(msgDiag,this.idCorrelazioneApplicativa,this.idCorrelazioneRisposta,
							this.porta,this.delegata,
							this.fruitore,this.servizio,this.servizioApplicativo,this.protocolFactory);
					try{
						this.loggerResource.log(LogLevels.LOG_LEVEL_INFO_PROTOCOL,message);

					}catch(Exception e){
						logError("Errore durante l'emissione del msg diagnostico 'human readable' nel log delle risorse (id transazione non fornito): "+e.getMessage(),e);
						gestioneErroreDiagnostica(e);
					}
				}
			}

			// Human Readable
			// System.out.println("2. ["+severitaLogEmessoPerFiltro+"] <= ["+severitaOpenSPCoopRichiestaPdD+"]");
			boolean humanReadableAbilitato = false;
			if( IntegrationManager.ID_MODULO.equals(this.idModulo)){ 
				humanReadableAbilitato = OpenSPCoop2Logger.loggerIntegrationManagerAbilitato;
			}else{
				humanReadableAbilitato = OpenSPCoop2Logger.loggerMsgDiagnosticoReadableAbilitato;
			}
			if( humanReadableAbilitato &&  (emettiLogSuFile) ){
				
				if(msgDiag==null){
					msgDiag = this.getMsgDiagnostico(gdo, LogLevels.SEVERITA_INFO_PROTOCOL, msg, codiceDiagnostico);
				}
				
				//ProtocolFactory protocolFactory = ProtocolFactoryManager.getInstance((String) this.pddContext.getObject(CostantiPdD.PROTOCOLLO));
				String message = OpenSPCoop2Logger.humanReadable(msgDiag,this.idCorrelazioneApplicativa,this.idCorrelazioneRisposta,
						this.porta,this.delegata,
						this.fruitore,this.servizio,this.servizioApplicativo,this.protocolFactory);
				try{
					if( IntegrationManager.ID_MODULO.equals(this.idModulo)){ 
						this.loggerIntegrationManager.log(LogLevels.LOG_LEVEL_INFO_PROTOCOL,message);
					}else{
						this.loggerOpenSPCoop2.log(LogLevels.LOG_LEVEL_INFO_PROTOCOL,message);
					}
				}catch(Exception e){
					logError("Errore durante l'emissione del msg diagnostico 'human readable': "+e.getMessage(),e);
					gestioneErroreDiagnostica(e);
				}
			}

		}catch(Exception e){
			logError("MsgDiagnostico.infoProtocol error "+e.getMessage(),e);
			gestioneErroreDiagnostica(e);
		}

	}

	/**
	 * Il Metodo si occupa di creare un messaggio di livello 'INFO-INTEGRATION'. 
	 * @deprecated in funzione del metodo logPersonalizzato 
	 *
	 * @param msg Messaggio da registrare
	 * 
	 */
	@Deprecated
	public void infoIntegration(String msg) {
		
		String codiceDiagnostico = null; // con questi metodi viene fornito un unico codice diagnostico associato ad ogni messaggio di questo livello
		
		this.setEmitErrorConditionInContext(LogLevels.SEVERITA_INFO_INTEGRATION);
		
		int severitaLogEmessoPerFiltro = LogLevels.toIntervalloLog4J(LogLevels.SEVERITA_INFO_INTEGRATION);
		int severitaRichiestaPdD = this.msgDiagPropertiesReader.getFiltroMsgDiagnosticoOpenSPCoop2level7();
		int severitaLog4JRichiestaPdD = this.msgDiagPropertiesReader.getFiltroMsgDiagnosticoOpenSPCoop2level7();
		ConfigurazionePdDManager configurazionePdDReader = getConfigurazionePdDManager();
		if(configurazionePdDReader!=null && configurazionePdDReader.isInitializedConfigurazionePdDReader()){
			severitaRichiestaPdD = this.msgDiagPropertiesReader.getValoreFiltroFromValoreOpenSPCoop2(configurazionePdDReader.getSeveritaMessaggiDiagnostici());
			severitaLog4JRichiestaPdD = this.msgDiagPropertiesReader.getValoreFiltroFromValoreOpenSPCoop2(configurazionePdDReader.getSeveritaLog4JMessaggiDiagnostici());
		}
		if(this.severitaPorta!=null) {
			severitaRichiestaPdD = this.msgDiagPropertiesReader.getValoreFiltroFromValoreOpenSPCoop2(LogLevels.toOpenSPCoop2(this.severitaPorta.getValue()));
		}
		
		try{
			
			boolean emettiLogSuFile = (severitaLogEmessoPerFiltro <= severitaLog4JRichiestaPdD);
			boolean emettiLog = (severitaLogEmessoPerFiltro <= severitaRichiestaPdD);
			if(!emettiLogSuFile && !emettiLog) {
				return;
			}
			
			codiceDiagnostico = this.msgDiagPropertiesReader.getCodiceDiagnosticoInfoIntegration();
			
			// Data
			Date gdo = DateManager.getDate();
			if(this.openspcoopProperties.generazioneDateCasualiLogAbilitato() && this.pddContext!=null && this.pddContext.getObject(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE)!=null){
				gdo = this.generatoreDateCasuali.getProssimaData((String)this.pddContext.getObject(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE));
			}
			
			org.openspcoop2.protocol.sdk.diagnostica.MsgDiagnostico msgDiag = null;
						
			// Messaggio diagnostici
			// System.out.println("1. ["+severitaLogEmessoPerFiltro+"] <= ["+severitaRichiestaPdD+"]");
			if( emettiLogSuFile){

				if(msgDiag==null){
					msgDiag = this.getMsgDiagnostico(gdo, LogLevels.SEVERITA_INFO_INTEGRATION, msg, codiceDiagnostico);
				}
				
				if(OpenSPCoop2Logger.loggerMsgDiagnosticoAbilitato){
					try{
						String xml = this.diagnosticoBuilder.toString(msgDiag,TipoSerializzazione.DEFAULT);
						this.loggerMsgDiagnostico.log(LogLevels.LOG_LEVEL_INFO_INTEGRATION,xml);
					}catch(Exception e){
						logError("Errore durante l'emissione del msg diagnostico su log4j (struttura xml): "+e.getMessage(),e);
						gestioneErroreDiagnostica(e);
					}
				}
				
			}
			
			// Messaggio diagnostici
			// System.out.println("1. ["+severitaLogEmessoPerFiltro+"] <= ["+severitaRichiestaPdD+"]");
			if( emettiLog){
					
				if(msgDiag==null){
					msgDiag = this.getMsgDiagnostico(gdo, LogLevels.SEVERITA_INFO_INTEGRATION, msg, codiceDiagnostico);
				}
				
				if(msgDiag.getIdTransazione()!=null) {
					
					// TransazioneContext
					if(this.openspcoopProperties.isTransazioniSaveDiagnosticiInUniqueTransaction()) {
						this.logMsgDiagnosticoInTransactionContext(msgDiag);
					}
					
					// Msg Diagnostico personalizzato
					for(int i=0; i<this.loggerMsgDiagnosticoOpenSPCoopAppender.size();i++){
						try{
							this.loggerMsgDiagnosticoOpenSPCoopAppender.get(i).log(this.getConnectionFromState(),msgDiag);
						}catch(Exception e){
							logError("Errore durante l'emissione del msg diagnostico personalizzato ["+this.tipoMsgDiagnosticoOpenSPCoopAppender.get(i)+"]: "+e.getMessage(),e);
							gestioneErroreDiagnostica(e);
						}
					}
				}
				else {
					// i diagnostici di sistema comunque non li registro sugli appender personalizzati.
					String message = OpenSPCoop2Logger.humanReadable(msgDiag,this.idCorrelazioneApplicativa,this.idCorrelazioneRisposta,
							this.porta,this.delegata,
							this.fruitore,this.servizio,this.servizioApplicativo,this.protocolFactory);
					try{
						this.loggerResource.log(LogLevels.LOG_LEVEL_INFO_INTEGRATION,message);

					}catch(Exception e){
						logError("Errore durante l'emissione del msg diagnostico 'human readable' nel log delle risorse (id transazione non fornito): "+e.getMessage(),e);
						gestioneErroreDiagnostica(e);
					}
				}
			}

			// Human Readable
			// System.out.println("2. ["+severitaLogEmessoPerFiltro+"] <= ["+severitaOpenSPCoopRichiestaPdD+"]");
			boolean humanReadableAbilitato = false;
			if( IntegrationManager.ID_MODULO.equals(this.idModulo)){ 
				humanReadableAbilitato = OpenSPCoop2Logger.loggerIntegrationManagerAbilitato;
			}else{
				humanReadableAbilitato = OpenSPCoop2Logger.loggerMsgDiagnosticoReadableAbilitato;
			}
			if( humanReadableAbilitato &&  (emettiLogSuFile) ){
				
				if(msgDiag==null){
					msgDiag = this.getMsgDiagnostico(gdo, LogLevels.SEVERITA_INFO_INTEGRATION, msg, codiceDiagnostico);
				}
				
				//ProtocolFactory protocolFactory = ProtocolFactoryManager.getInstance((String) this.pddContext.getObject(CostantiPdD.PROTOCOLLO));
				String message = OpenSPCoop2Logger.humanReadable(msgDiag,this.idCorrelazioneApplicativa,this.idCorrelazioneRisposta,
						this.porta,this.delegata,
						this.fruitore,this.servizio,this.servizioApplicativo,this.protocolFactory);
				try{
					if( IntegrationManager.ID_MODULO.equals(this.idModulo)){ 
						this.loggerIntegrationManager.log(LogLevels.LOG_LEVEL_INFO_INTEGRATION,message);
					}else{
						this.loggerOpenSPCoop2.log(LogLevels.LOG_LEVEL_INFO_INTEGRATION,message);
					}
				}catch(Exception e){
					logError("Errore durante l'emissione del msg diagnostico 'human readable': "+e.getMessage(),e);
					gestioneErroreDiagnostica(e);
				}
			}

		}catch(Exception e){
			logError("MsgDiagnostico.infoIntegration error "+e.getMessage(),e);
			gestioneErroreDiagnostica(e);
		}

	}

	/**
	 * Il Metodo si occupa di creare un messaggio di livello 'DEBUG-LOW'. 
	 * @deprecated in funzione del metodo logPersonalizzato
	 * @param msg Messaggio da registrare
	 * 
	 */
	@Deprecated
	public void lowDebug(String msg){
		
		String codiceDiagnostico = null; // con questi metodi viene fornito un unico codice diagnostico associato ad ogni messaggio di questo livello
		
		this.setEmitErrorConditionInContext(LogLevels.SEVERITA_DEBUG_LOW);
		
		int severitaLogEmessoPerFiltro = LogLevels.toIntervalloLog4J(LogLevels.SEVERITA_DEBUG_LOW);
		int severitaRichiestaPdD = this.msgDiagPropertiesReader.getFiltroMsgDiagnosticoOpenSPCoop2level7();
		int severitaLog4JRichiestaPdD = this.msgDiagPropertiesReader.getFiltroMsgDiagnosticoOpenSPCoop2level7();
		ConfigurazionePdDManager configurazionePdDReader = getConfigurazionePdDManager();
		if(configurazionePdDReader!=null && configurazionePdDReader.isInitializedConfigurazionePdDReader()){
			severitaRichiestaPdD = this.msgDiagPropertiesReader.getValoreFiltroFromValoreOpenSPCoop2(configurazionePdDReader.getSeveritaMessaggiDiagnostici());
			severitaLog4JRichiestaPdD = this.msgDiagPropertiesReader.getValoreFiltroFromValoreOpenSPCoop2(configurazionePdDReader.getSeveritaLog4JMessaggiDiagnostici());
		}
		if(this.severitaPorta!=null) {
			severitaRichiestaPdD = this.msgDiagPropertiesReader.getValoreFiltroFromValoreOpenSPCoop2(LogLevels.toOpenSPCoop2(this.severitaPorta.getValue()));
		}
		
		try{
			
			boolean emettiLogSuFile = (severitaLogEmessoPerFiltro <= severitaLog4JRichiestaPdD);
			boolean emettiLog = (severitaLogEmessoPerFiltro <= severitaRichiestaPdD);
			if(!emettiLogSuFile && !emettiLog) {
				return;
			}
			
			codiceDiagnostico = this.msgDiagPropertiesReader.getCodiceDiagnosticoDebugLow();
			
			// Data
			Date gdo = DateManager.getDate();
			if(this.openspcoopProperties.generazioneDateCasualiLogAbilitato() && this.pddContext!=null && this.pddContext.getObject(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE)!=null){
				gdo = this.generatoreDateCasuali.getProssimaData((String)this.pddContext.getObject(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE));
			}
			
			org.openspcoop2.protocol.sdk.diagnostica.MsgDiagnostico msgDiag = null;
						
			// Messaggio diagnostici
			// System.out.println("1. ["+severitaLogEmessoPerFiltro+"] <= ["+severitaRichiestaPdD+"]");
			if( emettiLogSuFile){

				if(msgDiag==null){
					msgDiag = this.getMsgDiagnostico(gdo, LogLevels.SEVERITA_DEBUG_LOW, msg, codiceDiagnostico);
				}
				
				if(OpenSPCoop2Logger.loggerMsgDiagnosticoAbilitato){
					try{
						String xml = this.diagnosticoBuilder.toString(msgDiag,TipoSerializzazione.DEFAULT);
						this.loggerMsgDiagnostico.log(LogLevels.LOG_LEVEL_DEBUG_LOW,xml);
					}catch(Exception e){
						logError("Errore durante l'emissione del msg diagnostico su log4j (struttura xml): "+e.getMessage(),e);
						gestioneErroreDiagnostica(e);
					}
				}

			}

			// Messaggio diagnostici
			// System.out.println("1. ["+severitaLogEmessoPerFiltro+"] <= ["+severitaRichiestaPdD+"]");
			if( emettiLog){
					
				if(msgDiag==null){
					msgDiag = this.getMsgDiagnostico(gdo, LogLevels.SEVERITA_DEBUG_LOW, msg, codiceDiagnostico);
				}
				
				if(msgDiag.getIdTransazione()!=null) {
					
					// TransazioneContext
					if(this.openspcoopProperties.isTransazioniSaveDiagnosticiInUniqueTransaction()) {
						this.logMsgDiagnosticoInTransactionContext(msgDiag);
					}
					
					// Msg Diagnostico personalizzato
					for(int i=0; i<this.loggerMsgDiagnosticoOpenSPCoopAppender.size();i++){
						try{
							this.loggerMsgDiagnosticoOpenSPCoopAppender.get(i).log(this.getConnectionFromState(),msgDiag);
						}catch(Exception e){
							logError("Errore durante l'emissione del msg diagnostico personalizzato ["+this.tipoMsgDiagnosticoOpenSPCoopAppender.get(i)+"]: "+e.getMessage(),e);
							gestioneErroreDiagnostica(e);
						}
					}
				}
				else {
					// i diagnostici di sistema comunque non li registro sugli appender personalizzati.
					String message = OpenSPCoop2Logger.humanReadable(msgDiag,this.idCorrelazioneApplicativa,this.idCorrelazioneRisposta,
							this.porta,this.delegata,
							this.fruitore,this.servizio,this.servizioApplicativo,this.protocolFactory);
					try{
						this.loggerResource.log(LogLevels.LOG_LEVEL_DEBUG_LOW,message);

					}catch(Exception e){
						logError("Errore durante l'emissione del msg diagnostico 'human readable' nel log delle risorse (id transazione non fornito): "+e.getMessage(),e);
						gestioneErroreDiagnostica(e);
					}
				}
			}
			
			// Human Readable
			// System.out.println("2. ["+severitaLogEmessoPerFiltro+"] <= ["+severitaOpenSPCoopRichiestaPdD+"]");
			boolean humanReadableAbilitato = false;
			if( IntegrationManager.ID_MODULO.equals(this.idModulo)){ 
				humanReadableAbilitato = OpenSPCoop2Logger.loggerIntegrationManagerAbilitato;
			}else{
				humanReadableAbilitato = OpenSPCoop2Logger.loggerMsgDiagnosticoReadableAbilitato;
			}
			if( humanReadableAbilitato &&  (emettiLogSuFile) ){
				
				if(msgDiag==null){
					msgDiag = this.getMsgDiagnostico(gdo, LogLevels.SEVERITA_DEBUG_LOW, msg, codiceDiagnostico);
				}
				
				//ProtocolFactory protocolFactory = ProtocolFactoryManager.getInstance((String) this.pddContext.getObject(CostantiPdD.PROTOCOLLO));
				String message = OpenSPCoop2Logger.humanReadable(msgDiag,this.idCorrelazioneApplicativa,this.idCorrelazioneRisposta,
						this.porta,this.delegata,
						this.fruitore,this.servizio,this.servizioApplicativo,this.protocolFactory);
				try{
					if( IntegrationManager.ID_MODULO.equals(this.idModulo)){ 
						this.loggerIntegrationManager.log(LogLevels.LOG_LEVEL_DEBUG_LOW,message);
					}else{
						this.loggerOpenSPCoop2.log(LogLevels.LOG_LEVEL_DEBUG_LOW,message);
					}
				}catch(Exception e){
					logError("Errore durante l'emissione del msg diagnostico 'human readable': "+e.getMessage(),e);
					gestioneErroreDiagnostica(e);
				}
			}

		}catch(Exception e){
			logError("MsgDiagnostico.lowDebug error "+e.getMessage(),e);
			gestioneErroreDiagnostica(e);
		}
	}

	/**
	 * Il Metodo si occupa di creare un messaggio di livello 'DEBUG-MEDIUM'. 
	 *
	 * @param msg Messaggio da registrare
	 * 
	 */
	public void mediumDebug(String msg) {
		
		String codiceDiagnostico = null; // con questi metodi viene fornito un unico codice diagnostico associato ad ogni messaggio di questo livello
		
		this.setEmitErrorConditionInContext(LogLevels.SEVERITA_DEBUG_MEDIUM);
		
		int severitaLogEmessoPerFiltro = LogLevels.toIntervalloLog4J(LogLevels.SEVERITA_DEBUG_MEDIUM);
		int severitaRichiestaPdD = this.msgDiagPropertiesReader.getFiltroMsgDiagnosticoOpenSPCoop2level7();
		int severitaLog4JRichiestaPdD = this.msgDiagPropertiesReader.getFiltroMsgDiagnosticoOpenSPCoop2level7();
		ConfigurazionePdDManager configurazionePdDReader = getConfigurazionePdDManager();
		if(configurazionePdDReader!=null && configurazionePdDReader.isInitializedConfigurazionePdDReader()){
			severitaRichiestaPdD = this.msgDiagPropertiesReader.getValoreFiltroFromValoreOpenSPCoop2(configurazionePdDReader.getSeveritaMessaggiDiagnostici());
			severitaLog4JRichiestaPdD = this.msgDiagPropertiesReader.getValoreFiltroFromValoreOpenSPCoop2(configurazionePdDReader.getSeveritaLog4JMessaggiDiagnostici());
		}
		if(this.severitaPorta!=null) {
			severitaRichiestaPdD = this.msgDiagPropertiesReader.getValoreFiltroFromValoreOpenSPCoop2(LogLevels.toOpenSPCoop2(this.severitaPorta.getValue()));
		}
		
		try{
			
			boolean emettiLogSuFile = (severitaLogEmessoPerFiltro <= severitaLog4JRichiestaPdD);
			boolean emettiLog = (severitaLogEmessoPerFiltro <= severitaRichiestaPdD);
			if(!emettiLogSuFile && !emettiLog) {
				return;
			}
			
			codiceDiagnostico = this.msgDiagPropertiesReader.getCodiceDiagnosticoDebugMedium();
			
			// Data
			Date gdo = DateManager.getDate();
			if(this.openspcoopProperties.generazioneDateCasualiLogAbilitato() && this.pddContext!=null && this.pddContext.getObject(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE)!=null){
				gdo = this.generatoreDateCasuali.getProssimaData((String)this.pddContext.getObject(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE));
			}
			
			org.openspcoop2.protocol.sdk.diagnostica.MsgDiagnostico msgDiag = null;
						
			// Messaggio diagnostici
			// System.out.println("1. ["+severitaLogEmessoPerFiltro+"] <= ["+severitaRichiestaPdD+"]");
			if( emettiLogSuFile){

				if(msgDiag==null){
					msgDiag = this.getMsgDiagnostico(gdo, LogLevels.SEVERITA_DEBUG_MEDIUM, msg, codiceDiagnostico);
				}
				
				if(OpenSPCoop2Logger.loggerMsgDiagnosticoAbilitato){
					try{
						String xml = this.diagnosticoBuilder.toString(msgDiag,TipoSerializzazione.DEFAULT);
						this.loggerMsgDiagnostico.log(LogLevels.LOG_LEVEL_DEBUG_MEDIUM,xml);
					}catch(Exception e){
						logError("Errore durante l'emissione del msg diagnostico su log4j (struttura xml): "+e.getMessage(),e);
						gestioneErroreDiagnostica(e);
					}
				}

			}
			
			// Messaggio diagnostici
			// System.out.println("1. ["+severitaLogEmessoPerFiltro+"] <= ["+severitaRichiestaPdD+"]");
			if( emettiLog){
					
				if(msgDiag==null){
					msgDiag = this.getMsgDiagnostico(gdo, LogLevels.SEVERITA_DEBUG_MEDIUM, msg, codiceDiagnostico);
				}
				
				if(msgDiag.getIdTransazione()!=null) {
					
					// TransazioneContext
					if(this.openspcoopProperties.isTransazioniSaveDiagnosticiInUniqueTransaction()) {
						this.logMsgDiagnosticoInTransactionContext(msgDiag);
					}
					
					//	Msg Diagnostico personalizzato
					for(int i=0; i<this.loggerMsgDiagnosticoOpenSPCoopAppender.size();i++){
						try{
							this.loggerMsgDiagnosticoOpenSPCoopAppender.get(i).log(this.getConnectionFromState(),msgDiag);
						}catch(Exception e){
							logError("Errore durante l'emissione del msg diagnostico personalizzato ["+this.tipoMsgDiagnosticoOpenSPCoopAppender.get(i)+"]: "+e.getMessage(),e);
							gestioneErroreDiagnostica(e);
						}
					}
				}
				else {
					// i diagnostici di sistema comunque non li registro sugli appender personalizzati.
					String message = OpenSPCoop2Logger.humanReadable(msgDiag,this.idCorrelazioneApplicativa,this.idCorrelazioneRisposta,
							this.porta,this.delegata,
							this.fruitore,this.servizio,this.servizioApplicativo,this.protocolFactory);
					try{
						this.loggerResource.log(LogLevels.LOG_LEVEL_DEBUG_MEDIUM,message);

					}catch(Exception e){
						logError("Errore durante l'emissione del msg diagnostico 'human readable' nel log delle risorse (id transazione non fornito): "+e.getMessage(),e);
						gestioneErroreDiagnostica(e);
					}
				}
			}

			// Human Readable
			// System.out.println("2. ["+severitaLogEmessoPerFiltro+"] <= ["+severitaOpenSPCoopRichiestaPdD+"]");
			boolean humanReadableAbilitato = false;
			if( IntegrationManager.ID_MODULO.equals(this.idModulo)){ 
				humanReadableAbilitato = OpenSPCoop2Logger.loggerIntegrationManagerAbilitato;
			}else{
				humanReadableAbilitato = OpenSPCoop2Logger.loggerMsgDiagnosticoReadableAbilitato;
			}
			if( humanReadableAbilitato &&  (emettiLogSuFile) ){
				
				if(msgDiag==null){
					msgDiag = this.getMsgDiagnostico(gdo, LogLevels.SEVERITA_DEBUG_MEDIUM, msg, codiceDiagnostico);
				}
				
				//ProtocolFactory protocolFactory = ProtocolFactoryManager.getInstance((String) this.pddContext.getObject(CostantiPdD.PROTOCOLLO));
				String message = OpenSPCoop2Logger.humanReadable(msgDiag,this.idCorrelazioneApplicativa,this.idCorrelazioneRisposta,
						this.porta,this.delegata,
						this.fruitore,this.servizio,this.servizioApplicativo,this.protocolFactory);
				try{
					if( IntegrationManager.ID_MODULO.equals(this.idModulo)){ 
						this.loggerIntegrationManager.log(LogLevels.LOG_LEVEL_DEBUG_MEDIUM,message);
					}else{
						this.loggerOpenSPCoop2.log(LogLevels.LOG_LEVEL_DEBUG_MEDIUM,message);
					}
				}catch(Exception e){
					logError("Errore durante l'emissione del msg diagnostico 'human readable': "+e.getMessage(),e);
					gestioneErroreDiagnostica(e);
				}
			}

		}catch(Exception e){
			logError("MsgDiagnostico.mediumDebug error "+e.getMessage(),e);
			gestioneErroreDiagnostica(e);
		}

	}

	/**
	 * Il Metodo si occupa di creare un messaggio di livello 'DEBUG-HIGH'. 
	 *
	 * @param msg Messaggio da registrare
	 * 
	 */
	public void highDebug(String msg){
		
		String codiceDiagnostico = null; // con questi metodi viene fornito un unico codice diagnostico associato ad ogni messaggio di questo livello
		
		this.setEmitErrorConditionInContext(LogLevels.SEVERITA_DEBUG_HIGH);
		
		int severitaLogEmessoPerFiltro = LogLevels.toIntervalloLog4J(LogLevels.SEVERITA_DEBUG_HIGH);
		int severitaRichiestaPdD = this.msgDiagPropertiesReader.getFiltroMsgDiagnosticoOpenSPCoop2level7();
		int severitaLog4JRichiestaPdD = this.msgDiagPropertiesReader.getFiltroMsgDiagnosticoOpenSPCoop2level7();
		ConfigurazionePdDManager configurazionePdDReader = getConfigurazionePdDManager();
		if(configurazionePdDReader!=null && configurazionePdDReader.isInitializedConfigurazionePdDReader()){
			severitaRichiestaPdD = this.msgDiagPropertiesReader.getValoreFiltroFromValoreOpenSPCoop2(configurazionePdDReader.getSeveritaMessaggiDiagnostici());
			severitaLog4JRichiestaPdD = this.msgDiagPropertiesReader.getValoreFiltroFromValoreOpenSPCoop2(configurazionePdDReader.getSeveritaLog4JMessaggiDiagnostici());
		}
		if(this.severitaPorta!=null) {
			severitaRichiestaPdD = this.msgDiagPropertiesReader.getValoreFiltroFromValoreOpenSPCoop2(LogLevels.toOpenSPCoop2(this.severitaPorta.getValue()));
		}
		
		try{
			
			boolean emettiLogSuFile = (severitaLogEmessoPerFiltro <= severitaLog4JRichiestaPdD);
			boolean emettiLog = (severitaLogEmessoPerFiltro <= severitaRichiestaPdD);
			if(!emettiLogSuFile && !emettiLog) {
				return;
			}
			
			codiceDiagnostico = this.msgDiagPropertiesReader.getCodiceDiagnosticoDebugHigh();
			
			// Data
			Date gdo = DateManager.getDate();
			if(this.openspcoopProperties.generazioneDateCasualiLogAbilitato() && this.pddContext!=null && this.pddContext.getObject(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE)!=null){
				gdo = this.generatoreDateCasuali.getProssimaData((String)this.pddContext.getObject(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE));
			}
			
			org.openspcoop2.protocol.sdk.diagnostica.MsgDiagnostico msgDiag = null;
						
			// Messaggio diagnostici
			// System.out.println("1. ["+severitaLogEmessoPerFiltro+"] <= ["+severitaRichiestaPdD+"]");
			if( emettiLogSuFile){

				if(msgDiag==null){
					msgDiag = this.getMsgDiagnostico(gdo, LogLevels.SEVERITA_DEBUG_HIGH, msg, codiceDiagnostico);
				}
				
				if(OpenSPCoop2Logger.loggerMsgDiagnosticoAbilitato){
					try{
						String xml = this.diagnosticoBuilder.toString(msgDiag,TipoSerializzazione.DEFAULT);
						this.loggerMsgDiagnostico.log(LogLevels.LOG_LEVEL_DEBUG_HIGH,xml);
					}catch(Exception e){
						logError("Errore durante l'emissione del msg diagnostico su log4j (struttura xml): "+e.getMessage(),e);
						gestioneErroreDiagnostica(e);
					}
				}
					
			}
			
			// Messaggio diagnostici
			// System.out.println("1. ["+severitaLogEmessoPerFiltro+"] <= ["+severitaRichiestaPdD+"]");
			if( emettiLog){
				
				if(msgDiag==null){
					msgDiag = this.getMsgDiagnostico(gdo, LogLevels.SEVERITA_DEBUG_HIGH, msg, codiceDiagnostico);
				}
				
				if(msgDiag.getIdTransazione()!=null) {
					
					// TransazioneContext
					if(this.openspcoopProperties.isTransazioniSaveDiagnosticiInUniqueTransaction()) {
						this.logMsgDiagnosticoInTransactionContext(msgDiag);
					}
					
					// Msg Diagnostico personalizzato
					for(int i=0; i<this.loggerMsgDiagnosticoOpenSPCoopAppender.size();i++){
						try{
							this.loggerMsgDiagnosticoOpenSPCoopAppender.get(i).log(this.getConnectionFromState(),msgDiag);
						}catch(Exception e){
							logError("Errore durante l'emissione del msg diagnostico personalizzato ["+this.tipoMsgDiagnosticoOpenSPCoopAppender.get(i)+"]: "+e.getMessage(),e);
							gestioneErroreDiagnostica(e);
						}
					}
				}
				else {
					// i diagnostici di sistema comunque non li registro sugli appender personalizzati.
					String message = OpenSPCoop2Logger.humanReadable(msgDiag,this.idCorrelazioneApplicativa,this.idCorrelazioneRisposta,
							this.porta,this.delegata,
							this.fruitore,this.servizio,this.servizioApplicativo,this.protocolFactory);
					try{
						this.loggerResource.log(LogLevels.LOG_LEVEL_DEBUG_HIGH,message);

					}catch(Exception e){
						logError("Errore durante l'emissione del msg diagnostico 'human readable' nel log delle risorse (id transazione non fornito): "+e.getMessage(),e);
						gestioneErroreDiagnostica(e);
					}
				}
			}

			// Human Readable
			// System.out.println("2. ["+severitaLogEmessoPerFiltro+"] <= ["+severitaOpenSPCoopRichiestaPdD+"]");
			boolean humanReadableAbilitato = false;
			if( IntegrationManager.ID_MODULO.equals(this.idModulo)){ 
				humanReadableAbilitato = OpenSPCoop2Logger.loggerIntegrationManagerAbilitato;
			}else{
				humanReadableAbilitato = OpenSPCoop2Logger.loggerMsgDiagnosticoReadableAbilitato;
			}
			if( humanReadableAbilitato &&  (emettiLogSuFile) ){
				
				if(msgDiag==null){
					msgDiag = this.getMsgDiagnostico(gdo, LogLevels.SEVERITA_DEBUG_HIGH, msg, codiceDiagnostico);
				}
				
				//ProtocolFactory protocolFactory = ProtocolFactoryManager.getInstance((String) this.pddContext.getObject(CostantiPdD.PROTOCOLLO));
				String message = OpenSPCoop2Logger.humanReadable(msgDiag,this.idCorrelazioneApplicativa,this.idCorrelazioneRisposta,
						this.porta,this.delegata,
						this.fruitore,this.servizio,this.servizioApplicativo,this.protocolFactory);
				try{
					if( IntegrationManager.ID_MODULO.equals(this.idModulo)){ 
						this.loggerIntegrationManager.log(LogLevels.LOG_LEVEL_DEBUG_HIGH,message);
					}else{
						this.loggerOpenSPCoop2.log(LogLevels.LOG_LEVEL_DEBUG_HIGH,message);
					}
				}catch(Exception e){
					logError("Errore durante l'emissione del msg diagnostico 'human readable': "+e.getMessage(),e);
					gestioneErroreDiagnostica(e);
				}
			}

		}catch(Exception e){
			logError("MsgDiagnostico.highDebug error "+e.getMessage(),e);
			gestioneErroreDiagnostica(e);
		}
	}

	
	
	private void logMsgDiagnosticoInTransactionContext(org.openspcoop2.protocol.sdk.diagnostica.MsgDiagnostico msgDiag) {
		
		if(this.transazioneApplicativoServer!=null) {
			try {
				// forzo
				msgDiag.setIdTransazione(this.transazioneApplicativoServer.getIdTransazione());
				msgDiag.setApplicativo(this.transazioneApplicativoServer.getServizioApplicativoErogatore());
				GestoreConsegnaMultipla.getInstance().safeSave(msgDiag, this.idPortaApplicativa, this.getValidConnectionState(), this.requestInfo, this.pddContext);
			}catch(Throwable t) {
				logError("Errore durante il salvataggio delle informazioni relative al servizio applicativo: "+t.getMessage(),t);
				gestioneErroreDiagnostica(t);
			}
		}
		else {
		
			Exception exc = null;
			boolean gestioneStateful = false;
			try {
				Transaction tr = TransactionContext.getTransaction(msgDiag.getIdTransazione());
				tr.addMsgDiagnostico(msgDiag);
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
					RepositoryGestioneStateful.addMsgDiagnostico(msgDiag.getIdTransazione(), msgDiag);
				}catch(Exception e){
					if(!IntegrationManager.ID_MODULO.equals(this.idModulo) &&
							!("["+org.openspcoop2.pdd.core.handlers.transazioni.PostOutResponseHandler.class.getName()+"] invocazione terminata").equals(msgDiag.getMessaggio())
						) { //
						exc = e;
					}
				}
			}
			if(exc!=null) {
				logError("Errore durante l'emissione del msg diagnostico nel contesto della transazione: "+exc.getMessage(),exc);
				gestioneErroreDiagnostica(exc);
			}
		}
	}
	
	
	private org.openspcoop2.protocol.sdk.diagnostica.MsgDiagnostico getMsgDiagnostico(Date gdo,int severitaLivelloOpenSPCoop2,String msg,String codiceDiagnostico){
		
		org.openspcoop2.protocol.sdk.diagnostica.MsgDiagnostico msgDiagnostico = new org.openspcoop2.protocol.sdk.diagnostica.MsgDiagnostico();
		
		if(this.pddContext!=null && this.pddContext.containsKey(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE)) {
			msgDiagnostico.setIdTransazione((String) this.pddContext.getObject(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE));
		}
		
		msgDiagnostico.setGdo(gdo);
		
		msgDiagnostico.setIdSoggetto(this.idSoggettoDominio);
		msgDiagnostico.setIdFunzione(this.idModulo);
		msgDiagnostico.setSeverita(severitaLivelloOpenSPCoop2);
		msgDiagnostico.setMessaggio(msg);
		
		msgDiagnostico.setIdBusta(this.idMessaggioRichiesta);
		msgDiagnostico.setIdBustaRisposta(this.idMessaggioRisposta);
		msgDiagnostico.setCodice(codiceDiagnostico);
		
		msgDiagnostico.setApplicativo(this.servizioApplicativo);
		
		msgDiagnostico.setProtocollo(this.protocolFactory.getProtocol());
		
		if(this.pddContext!=null){
			List<MapKey<String>> keys = org.openspcoop2.core.constants.Costanti.CONTEXT_OBJECT;
			if(keys!=null){
				for (int j = 0; j < keys.size(); j++) {
					MapKey<String> mapKey = keys.get(j);
					Object o = this.pddContext.getObject(mapKey);
					if(o!=null && o instanceof String){
						msgDiagnostico.addProperty(mapKey.getValue(), (String)o);
					}
				}
			}
		}
		
		if(this.properties!=null){
			this.properties.forEach( (k,v) -> msgDiagnostico.addProperty(k, v) );
		}
		
		return msgDiagnostico;
	}
	
	private void gestioneErroreDiagnostica(Throwable e) {
		
		if(this.openspcoopProperties.isRegistrazioneDiagnosticaFallita_BloccoServiziPdD()){
			MsgDiagnostico.gestoreDiagnosticaDisponibile=false;
			MsgDiagnostico.motivoMalfunzionamentoDiagnostici=e;
			logError("Il Sistema di gestione della diagnostica ha rilevato un errore durante la registrazione di un messaggio diagnostico,"+
					" tutti i servizi/moduli della porta di dominio sono sospesi. Si richiede un intervento sistemistico per la risoluzione del problema "+
					"e il riavvio di GovWay. Errore rilevato: ",e);
		}
		
	}
	
	private void logError(String msgErrore,Throwable e){
		if(this.loggerOpenSPCoop2Core!=null){
			this.loggerOpenSPCoop2Core.error(msgErrore,e);
		}
		else if(OpenSPCoop2Logger.loggerOpenSPCoopCore!=null){
			OpenSPCoop2Logger.loggerOpenSPCoopCore.error(msgErrore,e);
		}
		
		// Registro l'errore anche in questo logger.
		if(OpenSPCoop2Logger.loggerOpenSPCoopResources!=null){
			OpenSPCoop2Logger.loggerOpenSPCoopResources.error(msgErrore,e);
		}
		
	}
	private void logError(String msgErrore){
		if(this.loggerOpenSPCoop2Core!=null){
			this.loggerOpenSPCoop2Core.error(msgErrore);
		}
		else if(OpenSPCoop2Logger.loggerOpenSPCoopCore!=null){
			OpenSPCoop2Logger.loggerOpenSPCoopCore.error(msgErrore);
		}
		
		// Registro l'errore anche in questo logger.
		if(OpenSPCoop2Logger.loggerOpenSPCoopResources!=null){
			OpenSPCoop2Logger.loggerOpenSPCoopResources.error(msgErrore);
		}
		
	}
}

