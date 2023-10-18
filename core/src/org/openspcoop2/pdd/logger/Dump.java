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




package org.openspcoop2.pdd.logger;

import java.sql.Connection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.openspcoop2.core.config.DumpConfigurazione;
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.Proprieta;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import org.openspcoop2.core.config.driver.DriverConfigurazioneException;
import org.openspcoop2.core.constants.TipoPdD;
import org.openspcoop2.core.id.IDPortaApplicativa;
import org.openspcoop2.core.id.IDPortaDelegata;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.driver.IDServizioFactory;
import org.openspcoop2.core.transazioni.TransazioneApplicativoServer;
import org.openspcoop2.core.transazioni.constants.TipoMessaggio;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.OpenSPCoop2MessageProperties;
import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.message.exception.MessageException;
import org.openspcoop2.message.rest.DumpRestMessageUtils;
import org.openspcoop2.message.soap.DumpSoapMessageUtils;
import org.openspcoop2.message.utils.DumpAttachment;
import org.openspcoop2.message.utils.DumpMessaggio;
import org.openspcoop2.message.utils.DumpMessaggioConfig;
import org.openspcoop2.pdd.config.ConfigurazionePdDManager;
import org.openspcoop2.pdd.config.CostantiProprieta;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.core.CostantiPdD;
import org.openspcoop2.pdd.core.PdDContext;
import org.openspcoop2.pdd.core.connettori.InfoConnettoreUscita;
import org.openspcoop2.pdd.core.transazioni.GestoreConsegnaMultipla;
import org.openspcoop2.pdd.core.transazioni.RepositoryGestioneStateful;
import org.openspcoop2.pdd.core.transazioni.Transaction;
import org.openspcoop2.pdd.core.transazioni.TransactionContext;
import org.openspcoop2.pdd.core.transazioni.TransactionDeletedException;
import org.openspcoop2.pdd.core.transazioni.TransactionNotExistsException;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.dump.Attachment;
import org.openspcoop2.protocol.sdk.dump.BodyMultipartInfo;
import org.openspcoop2.protocol.sdk.dump.DumpException;
import org.openspcoop2.protocol.sdk.dump.IDumpProducer;
import org.openspcoop2.protocol.sdk.dump.Messaggio;
import org.openspcoop2.protocol.sdk.state.IState;
import org.openspcoop2.protocol.sdk.state.RequestInfo;
import org.openspcoop2.protocol.sdk.state.StateMessage;
import org.openspcoop2.protocol.sdk.state.URLProtocolContext;
import org.openspcoop2.protocol.sdk.tracciamento.TracciamentoException;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.io.DumpByteArrayOutputStream;
import org.openspcoop2.utils.transport.TransportUtils;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.slf4j.Logger;



/**
 * Contiene la definizione un Logger utilizzato dai nodi dell'infrastruttura di OpenSPCoop
 * per la registrazione di messaggi diagnostici.
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class Dump {

	/** Indicazione di un dump funzionante */
	private static boolean sistemaDumpDisponibile = true;
	public static boolean isSistemaDumpDisponibile() {
		return sistemaDumpDisponibile;
	}
	public static void setSistemaDumpDisponibile(boolean sistemaDumpDisponibile) {
		Dump.sistemaDumpDisponibile = sistemaDumpDisponibile;
	}

	/** Primo errore avvenuto nel momento in cui è stato rilevato un malfunzionamento nel sistema di dump */
	private static Throwable motivoMalfunzionamentoDump = null;
	public static Throwable getMotivoMalfunzionamentoDump() {
		return motivoMalfunzionamentoDump;
	}
	public static void setMotivoMalfunzionamentoDump(Throwable motivoMalfunzionamentoDump) {
		Dump.motivoMalfunzionamentoDump = motivoMalfunzionamentoDump;
	}
	
	private static final String DIAGNOSTICO_REGISTRAZIONE_NON_RIUSCITA = "dumpContenutiApplicativi.registrazioneNonRiuscita";
	

	/**  Logger log4j utilizzato per effettuare un dump dei messaggi applicativi */
	private Logger loggerDump = null;
	private void loggerDumpError(String msg) {
		if(this.loggerDump!=null) {
			this.loggerDump.error(msg);
		}
	}
	private void loggerDumpError(String msg, Throwable e) {
		if(this.loggerDump!=null) {
			this.loggerDump.error(msg,e);
		}
	}
	
	private void loggerOpenSPCoopResourcesError(String msg, Throwable e) {
		if(OpenSPCoop2Logger.loggerOpenSPCoopResources!=null) {
			OpenSPCoop2Logger.loggerOpenSPCoopResources.error(msg,e);
		}
	}
	
	
	
	/** Soggetto che richiede il logger */
	private IDSoggetto dominio;
	/** Modulo Funzionale */
	private String idModulo;
	/** Identificativo del Messaggio */
	private String idMessaggio;
	/** Fruitore */
	private IDSoggetto fruitore;
	/** Servizio */
	private IDServizio servizio;
	/** Signature */
	private String signature;
	/** GDO */
	private Date gdo;
	/** TipoPdD */
	private TipoPdD tipoPdD;
	private String nomePorta;
	/** PdDContext */
	private PdDContext pddContext;
	/** RequestInfo */
	private RequestInfo requestInfo = null;
	
	/** OpenSPCoopProperties */
	private OpenSPCoop2Properties properties = null;

	/** MsgDiagnostico per eventuali errori di tracciamento (viene usato anche per registrare il dump in corso e completato) */
	private MsgDiagnostico msgDiagErroreDump = null;
	private boolean emitDiagnosticDump = false;
	
	/** Appender personalizzati per i dump applicativi di OpenSPCoop */
	private List<IDumpProducer> loggerDumpOpenSPCoopAppender = null; 
	private List<String> tipoDumpOpenSPCoopAppender = null; 
	
	private DumpConfigurazione dumpConfigurazione;
	
	private IProtocolFactory<?> protocolFactory = null;
	private String idTransazione = null;
	
	/** Stati */
	private StateMessage statoRichiesta;
	private StateMessage statoRisposta;
	
	/** Transaction */
	private Transaction transactionNullable = null;
	
	/** -----------------Impostazione TransazioneApplicativoServer ---------------- */
	private TransazioneApplicativoServer transazioneApplicativoServer;
	private IDPortaApplicativa idPortaApplicativa;
	private Date dataConsegnaTransazioneApplicativoServer;
	public void setTransazioneApplicativoServer(TransazioneApplicativoServer transazioneApplicativoServer, IDPortaApplicativa idPortaApplicativa, Date dataConsegnaTransazioneApplicativoServer) {
		this.transazioneApplicativoServer = transazioneApplicativoServer;
		this.idPortaApplicativa = idPortaApplicativa;
		this.dataConsegnaTransazioneApplicativoServer = dataConsegnaTransazioneApplicativoServer;
	}
	
	/**
	 * Costruttore. 
	 *
	 * @param dominio Soggetto che richiede il logger
	 * @param modulo Funzione che richiede il logger
	 * @param pddContext pddContext
	 * 
	 */
	public Dump(IDSoggetto dominio, String modulo, TipoPdD tipoPdD, String nomePorta, PdDContext pddContext, DumpConfigurazione dumpConfigurazione) throws DumpException{ // dump binario
		this(dominio, modulo, null, null, null, tipoPdD, nomePorta, pddContext,null,null,dumpConfigurazione);
	}
	public Dump(IDSoggetto dominio, String modulo, TipoPdD tipoPdD, String nomePorta, PdDContext pddContext,IState statoRichiesta,IState statoRisposta,
			DumpConfigurazione dumpConfigurazione) throws DumpException{
		this(dominio, modulo, null, null, null, tipoPdD, nomePorta, pddContext,statoRichiesta,statoRisposta,dumpConfigurazione);
	}
	public Dump(IDSoggetto dominio, String modulo, String idMessaggio, IDSoggetto fruitore, IDServizio servizio, 
			TipoPdD tipoPdD, String nomePorta, PdDContext pddContext,IState stateParam,IState responseStateParam,
			DumpConfigurazione dumpConfigurazione) throws DumpException{
		this.dominio = dominio;
		this.idModulo = modulo;
		this.idMessaggio = idMessaggio;
		this.fruitore = fruitore;
		this.servizio = servizio;
		this.loggerDump = OpenSPCoop2Logger.loggerDump;
		this.loggerDumpOpenSPCoopAppender = OpenSPCoop2Logger.loggerDumpOpenSPCoopAppender;
		this.tipoDumpOpenSPCoopAppender = OpenSPCoop2Logger.tipoDumpOpenSPCoopAppender;
		this.gdo = DateManager.getDate();
		if(this.dominio!=null)
			this.signature = this.dominio.getCodicePorta()+" <"+this.gdo+"> "+this.idModulo+"\n";
		else
			this.signature = "<"+this.gdo+"> "+this.idModulo+"\n";
		this.tipoPdD = tipoPdD;
		this.nomePorta = nomePorta;
		this.pddContext = pddContext;
		if(this.pddContext==null) {
			throw new DumpException("PdDContext is null");
		}
		
		this.properties = OpenSPCoop2Properties.getInstance();
		if(stateParam instanceof StateMessage){
			this.statoRichiesta = (StateMessage) stateParam;
		}
		if(responseStateParam instanceof StateMessage){
			this.statoRisposta = (StateMessage) responseStateParam;
		}
		
		OpenSPCoop2Properties op2Properties = OpenSPCoop2Properties.getInstance();
		
		this.dumpConfigurazione = dumpConfigurazione;
		
		if(this.pddContext.containsKey(org.openspcoop2.core.constants.Costanti.REQUEST_INFO)) {
			this.requestInfo = (RequestInfo) this.pddContext.getObject(org.openspcoop2.core.constants.Costanti.REQUEST_INFO);
		}
		
		this.msgDiagErroreDump = MsgDiagnostico.newInstance(this.tipoPdD,dominio,modulo,nomePorta,this.requestInfo,this.statoRichiesta,this.statoRisposta);
		this.msgDiagErroreDump.setPrefixMsgPersonalizzati(MsgDiagnosticiProperties.MSG_DIAG_TRACCIAMENTO);
		this.emitDiagnosticDump = op2Properties.isDumpEmitDiagnostic();
		
		this.idTransazione = (String) this.pddContext.getObject(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE);
		
		// Protocol Factory Manager
		String protocol = null;
		try{
			this.protocolFactory = ProtocolFactoryManager.getInstance().getProtocolFactoryByName((String) this.pddContext.getObject(org.openspcoop2.core.constants.Costanti.PROTOCOL_NAME));
			protocol = this.protocolFactory.getProtocol();
			this.msgDiagErroreDump.setPddContext(this.pddContext, this.protocolFactory);
		}catch (Exception e) {
			throw new DumpException("Errore durante l'inizializzazione del ProtocolFactoryManager...",e);
		}
		if(this.dominio==null){
			this.dominio=op2Properties.getIdentitaPortaDefault(protocol, this.requestInfo);
		}
		
		try{
			if(this.idTransazione!=null) {
				this.transactionNullable = TransactionContext.getTransaction(this.idTransazione);
			}
		}catch(Exception e){
			// La transazione potrebbe essere stata eliminata nelle comunicazioni stateful
		}
	}
	
	private Connection getConnectionFromState(boolean richiesta){
		if(richiesta){
			Connection c = StateMessage.getConnection(this.statoRichiesta);
			if(c!=null) {
				return c;
			}
		}
		else{
			Connection c = StateMessage.getConnection(this.statoRisposta);
			if(c!=null) {
				return c;
			}
		}
		return null;
	}






	/** ----------------- METODI DI LOGGING  ---------------- */
	public void emitDiagnosticStartDumpBinarioRichiestaIngresso(boolean onlyFileTrace) {
		emitDiagnosticDumpStart(TipoMessaggio.RICHIESTA_INGRESSO_DUMP_BINARIO, onlyFileTrace);
	}
	public void dumpBinarioRichiestaIngresso(boolean dumpBinarioRegistrazioneDatabase, boolean onlyLogFileTraceHeaders, boolean onlyLogFileTraceBody, 
			DumpByteArrayOutputStream msg, MessageType messageType, 
			URLProtocolContext protocolContext) throws DumpException {
		if(this.transactionNullable!=null) {
			this.transactionNullable.getTempiElaborazione().startDumpBinarioRichiestaIngresso();
		}
		try {
			dump(dumpBinarioRegistrazioneDatabase, onlyLogFileTraceHeaders, onlyLogFileTraceBody, TipoMessaggio.RICHIESTA_INGRESSO_DUMP_BINARIO,
					null,msg,messageType,
					protocolContext!=null ? protocolContext.getSource() : null,
					protocolContext!=null ? protocolContext.getHeaders() : null);
		}
		finally {
			if(this.transactionNullable!=null) {
				this.transactionNullable.getTempiElaborazione().endDumpBinarioRichiestaIngresso();
			}
		}
	}
	
	public void dumpRichiestaIngresso(OpenSPCoop2Message msg, URLProtocolContext protocolContext) throws DumpException {
		if(this.transactionNullable!=null) {
			this.transactionNullable.getTempiElaborazione().startDumpRichiestaIngresso();
		}
		try {
			dump(false, false, false, TipoMessaggio.RICHIESTA_INGRESSO,
					msg,null,null,
					protocolContext!=null ? protocolContext.getSource() : null,
					protocolContext!=null ? protocolContext.getHeaders() : null);
		}
		finally {
			if(this.transactionNullable!=null) {
				this.transactionNullable.getTempiElaborazione().endDumpRichiestaIngresso();
			}
		}
	}
	public void dumpRichiestaIngressoByIntegrationManagerError(byte[] msg, URLProtocolContext protocolContext) throws DumpException {
		if(this.transactionNullable!=null) {
			this.transactionNullable.getTempiElaborazione().startDumpRichiestaIngresso();
		}
		try {
			dump(false, false, false, TipoMessaggio.RICHIESTA_INGRESSO,
					null,DumpByteArrayOutputStream.newInstance(msg),null,
					protocolContext!=null ? protocolContext.getSource() : null,
					protocolContext!=null ? protocolContext.getHeaders() : null);
		}
		finally {
			if(this.transactionNullable!=null) {
				this.transactionNullable.getTempiElaborazione().endDumpRichiestaIngresso();
			}
		}
	}
	
	public void emitDiagnosticStartDumpBinarioRichiestaUscita(boolean onlyFileTrace) {
		emitDiagnosticDumpStart(TipoMessaggio.RICHIESTA_USCITA_DUMP_BINARIO, onlyFileTrace);
	}
	public void dumpBinarioRichiestaUscita(boolean dumpBinarioRegistrazioneDatabase, boolean onlyLogFileTraceHeaders, boolean onlyLogFileTraceBody, 
			DumpByteArrayOutputStream msg, MessageType messageType, 
			InfoConnettoreUscita infoConnettore) throws DumpException {
		if(this.transactionNullable!=null) {
			this.transactionNullable.getTempiElaborazione().startDumpBinarioRichiestaUscita();
		}
		try {
			dump(dumpBinarioRegistrazioneDatabase, onlyLogFileTraceHeaders, onlyLogFileTraceBody, TipoMessaggio.RICHIESTA_USCITA_DUMP_BINARIO,
					null,msg,messageType,
					(infoConnettore!=null ? infoConnettore.getLocation() : null),
					(infoConnettore!=null ? infoConnettore.getHeaders() : null));
		}
		finally {
			if(this.transactionNullable!=null) {
				this.transactionNullable.getTempiElaborazione().endDumpBinarioRichiestaUscita();
			}
		}
	}
	public void dumpRichiestaUscita(OpenSPCoop2Message msg, InfoConnettoreUscita infoConnettore) throws DumpException {
		if(this.transactionNullable!=null) {
			this.transactionNullable.getTempiElaborazione().startDumpRichiestaUscita();
		}
		try {
			dump(false, false, false, TipoMessaggio.RICHIESTA_USCITA,
					msg,null,null,
					(infoConnettore!=null ? infoConnettore.getLocation() : null),
					(infoConnettore!=null ? infoConnettore.getHeaders() : null));
		}
		finally {
			if(this.transactionNullable!=null) {
				this.transactionNullable.getTempiElaborazione().endDumpRichiestaUscita();
			}
		}
	}

	public void emitDiagnosticStartDumpBinarioRispostaIngresso(boolean onlyFileTrace) {
		emitDiagnosticDumpStart(TipoMessaggio.RISPOSTA_INGRESSO_DUMP_BINARIO, onlyFileTrace);
	}
	public void dumpBinarioRispostaIngresso(boolean dumpBinarioRegistrazioneDatabase, boolean onlyLogFileTraceHeaders, boolean onlyLogFileTraceBody, 
			DumpByteArrayOutputStream msg, MessageType messageType, 
			InfoConnettoreUscita infoConnettore, Map<String, List<String>> transportHeaderRisposta) throws DumpException {
		if(this.transactionNullable!=null) {
			this.transactionNullable.getTempiElaborazione().startDumpBinarioRispostaIngresso();
		}
		try {
			dump(dumpBinarioRegistrazioneDatabase, onlyLogFileTraceHeaders, onlyLogFileTraceBody, TipoMessaggio.RISPOSTA_INGRESSO_DUMP_BINARIO,
					null,msg,messageType,
					(infoConnettore!=null ? infoConnettore.getLocation() : null),transportHeaderRisposta);
		}
		finally {
			if(this.transactionNullable!=null) {
				this.transactionNullable.getTempiElaborazione().endDumpBinarioRispostaIngresso();
			}
		}
	}
	
	public void dumpRispostaIngresso(OpenSPCoop2Message msg, InfoConnettoreUscita infoConnettore, Map<String, List<String>> transportHeaderRisposta) throws DumpException {
		if(this.transactionNullable!=null) {
			this.transactionNullable.getTempiElaborazione().startDumpRispostaIngresso();
		}
		try {
			dump(false, false, false, TipoMessaggio.RISPOSTA_INGRESSO,
					msg,null,null,
					(infoConnettore!=null ? infoConnettore.getLocation() : null),transportHeaderRisposta);
		}
		finally {
			if(this.transactionNullable!=null) {
				this.transactionNullable.getTempiElaborazione().endDumpRispostaIngresso();
			}
		}
	}
	

	public void emitDiagnosticStartDumpBinarioRispostaUscita(boolean onlyFileTrace) {
		emitDiagnosticDumpStart(TipoMessaggio.RISPOSTA_USCITA_DUMP_BINARIO, onlyFileTrace);
	}
	public void dumpBinarioRispostaUscita(boolean dumpBinarioRegistrazioneDatabase, boolean onlyLogFileTraceHeaders, boolean onlyLogFileTraceBody, 
			DumpByteArrayOutputStream msg, MessageType messageType, 
			URLProtocolContext protocolContext, Map<String, List<String>> transportHeaderRisposta) throws DumpException {
		if(this.transactionNullable!=null) {
			this.transactionNullable.getTempiElaborazione().startDumpBinarioRispostaUscita();
		}
		try {
			dump(dumpBinarioRegistrazioneDatabase, onlyLogFileTraceHeaders, onlyLogFileTraceBody, TipoMessaggio.RISPOSTA_USCITA_DUMP_BINARIO,
					null,msg,messageType,
					protocolContext!=null ? protocolContext.getSource() : null,transportHeaderRisposta);
		}
		finally {
			if(this.transactionNullable!=null) {
				this.transactionNullable.getTempiElaborazione().endDumpBinarioRispostaUscita();
			}
		}
	}
	
	public void dumpRispostaUscita(OpenSPCoop2Message msg, URLProtocolContext protocolContext, Map<String, List<String>> transportHeaderRisposta) throws DumpException {
		if(this.transactionNullable!=null) {
			this.transactionNullable.getTempiElaborazione().startDumpRispostaUscita();
		}
		try {
			dump(false, false, false, TipoMessaggio.RISPOSTA_USCITA,
					msg,null,null,
					protocolContext!=null ? protocolContext.getSource() : null,transportHeaderRisposta);
		}
		finally {
			if(this.transactionNullable!=null) {
				this.transactionNullable.getTempiElaborazione().endDumpRispostaUscita();
			}
		}
	}
	

	public void dumpIntegrationManagerGetMessage(OpenSPCoop2Message msg) throws DumpException {
		if(this.transactionNullable!=null) {
			this.transactionNullable.getTempiElaborazione().startDumpIntegrationManager();
		}
		try {
			dump(false, false, false, TipoMessaggio.INTEGRATION_MANAGER,
					msg,null,null,
					"IntegrationManager.getMessage()",null);
		}
		finally {
			if(this.transactionNullable!=null) {
				this.transactionNullable.getTempiElaborazione().endDumpIntegrationManager();
			}
		}
	}
	
	/**
	 * Il Metodo si occupa di creare un effettuare un dump del messaggio applicativo. 
	 *
	 * @param msg Messaggio da registrare
	 * @throws TracciamentoException 
	 * 
	 */
	private void dump(boolean dumpBinarioRegistrazioneDatabase, boolean onlyLogFileTraceHeaders, boolean onlyLogFileTraceBody, 
			TipoMessaggio tipoMessaggio,OpenSPCoop2Message msg,DumpByteArrayOutputStream msgBytes, MessageType messageType,
			String location,Map<String, List<String>> transportHeaderParam) throws DumpException {

		boolean dumpNormale = TipoMessaggio.RICHIESTA_INGRESSO.equals(tipoMessaggio) ||
				TipoMessaggio.RICHIESTA_USCITA.equals(tipoMessaggio) ||
				TipoMessaggio.RISPOSTA_INGRESSO.equals(tipoMessaggio) ||
				TipoMessaggio.RISPOSTA_USCITA.equals(tipoMessaggio);
		
		boolean dumpBinario = TipoMessaggio.RICHIESTA_INGRESSO_DUMP_BINARIO.equals(tipoMessaggio) ||
				TipoMessaggio.RICHIESTA_USCITA_DUMP_BINARIO.equals(tipoMessaggio) ||
				TipoMessaggio.RISPOSTA_INGRESSO_DUMP_BINARIO.equals(tipoMessaggio) ||
				TipoMessaggio.RISPOSTA_USCITA_DUMP_BINARIO.equals(tipoMessaggio);
		
		boolean dumpIntegrationManager = TipoMessaggio.INTEGRATION_MANAGER.equals(tipoMessaggio); 
		
		
		if(dumpNormale) {
			if(this.dumpConfigurazione==null) {
				return; // disabilitato
			}
			if(this.dumpConfigurazione!=null && StatoFunzionalita.DISABILITATO.equals(this.dumpConfigurazione.getRealtime())) {
				return; // viene gestito tramite l'handler notify
			}
		}
				
		boolean dumpHeaders = true; 
		boolean dumpBody = true;
		boolean dumpAttachments = true;
		if(dumpBinario) {
			dumpAttachments = false;
		}
		boolean dumpBinarioAttivatoTramiteRegolaConfigurazione = false;
		
		if(dumpNormale) {
			// Il dump non binario si utilizza solo se viene richiesto un payload parsing, altrimenti quello binario è più preciso (anche se sono abilitati solo gli header) 
			if(TipoMessaggio.RICHIESTA_INGRESSO.equals(tipoMessaggio)) {
				if(this.dumpConfigurazione!=null && this.dumpConfigurazione.getRichiestaIngresso()!=null) {
					if(StatoFunzionalita.ABILITATO.equals(this.dumpConfigurazione.getRichiestaIngresso().getPayload()) &&
							StatoFunzionalita.ABILITATO.equals(this.dumpConfigurazione.getRichiestaIngresso().getPayloadParsing())){
						dumpHeaders = StatoFunzionalita.ABILITATO.equals(this.dumpConfigurazione.getRichiestaIngresso().getHeaders());
						dumpBody = StatoFunzionalita.ABILITATO.equals(this.dumpConfigurazione.getRichiestaIngresso().getBody());
						dumpAttachments = StatoFunzionalita.ABILITATO.equals(this.dumpConfigurazione.getRichiestaIngresso().getAttachments());		
					}
					else {
						dumpHeaders = false;
						dumpBody = false;
						dumpAttachments = false;
					}
				}
			}
			else if(TipoMessaggio.RICHIESTA_USCITA.equals(tipoMessaggio)) {
				if(this.dumpConfigurazione!=null && this.dumpConfigurazione.getRichiestaUscita()!=null) {
					if(StatoFunzionalita.ABILITATO.equals(this.dumpConfigurazione.getRichiestaUscita().getPayload()) &&
							StatoFunzionalita.ABILITATO.equals(this.dumpConfigurazione.getRichiestaUscita().getPayloadParsing())){
						dumpHeaders = StatoFunzionalita.ABILITATO.equals(this.dumpConfigurazione.getRichiestaUscita().getHeaders());
						dumpBody = StatoFunzionalita.ABILITATO.equals(this.dumpConfigurazione.getRichiestaUscita().getBody());
						dumpAttachments = StatoFunzionalita.ABILITATO.equals(this.dumpConfigurazione.getRichiestaUscita().getAttachments());
					}
					else {
						dumpHeaders = false;
						dumpBody = false;
						dumpAttachments = false;
					}
				}
			}
			else if(TipoMessaggio.RISPOSTA_INGRESSO.equals(tipoMessaggio)) {
				if(this.dumpConfigurazione!=null && this.dumpConfigurazione.getRispostaIngresso()!=null) {
					if(StatoFunzionalita.ABILITATO.equals(this.dumpConfigurazione.getRispostaIngresso().getPayload()) &&
							StatoFunzionalita.ABILITATO.equals(this.dumpConfigurazione.getRispostaIngresso().getPayloadParsing())){
						dumpHeaders = StatoFunzionalita.ABILITATO.equals(this.dumpConfigurazione.getRispostaIngresso().getHeaders());
						dumpBody = StatoFunzionalita.ABILITATO.equals(this.dumpConfigurazione.getRispostaIngresso().getBody());
						dumpAttachments = StatoFunzionalita.ABILITATO.equals(this.dumpConfigurazione.getRispostaIngresso().getAttachments());
					}
					else {
						dumpHeaders = false;
						dumpBody = false;
						dumpAttachments = false;
					}
				}
			}
			else if(TipoMessaggio.RISPOSTA_USCITA.equals(tipoMessaggio) &&
				this.dumpConfigurazione!=null && this.dumpConfigurazione.getRispostaUscita()!=null) {
				if(StatoFunzionalita.ABILITATO.equals(this.dumpConfigurazione.getRispostaUscita().getPayload()) &&
						StatoFunzionalita.ABILITATO.equals(this.dumpConfigurazione.getRispostaUscita().getPayloadParsing())){
					dumpHeaders = StatoFunzionalita.ABILITATO.equals(this.dumpConfigurazione.getRispostaUscita().getHeaders());
					dumpBody = StatoFunzionalita.ABILITATO.equals(this.dumpConfigurazione.getRispostaUscita().getBody());
					dumpAttachments = StatoFunzionalita.ABILITATO.equals(this.dumpConfigurazione.getRispostaUscita().getAttachments());
				}
				else {
					dumpHeaders = false;
					dumpBody = false;
					dumpAttachments = false;
				}
			}
			if(!dumpHeaders && !dumpBody && !dumpAttachments) {
				return; // disabilitato
			}
		}
		else if(dumpBinario) {
			
			if(dumpBinarioRegistrazioneDatabase) {
				// registro tutto
			}
			else {
					
				// imposto a false; verifico poi regola specifica e faccio successivamente verifica per file trace
				dumpHeaders = false;
				dumpBody = false;
				
				// Il dump non binario si utilizza solo se viene richiesto un payload parsing, altrimenti quello binario è più preciso (anche se sono abilitati solo gli header) 
				if(TipoMessaggio.RICHIESTA_INGRESSO_DUMP_BINARIO.equals(tipoMessaggio)) {
					if(this.dumpConfigurazione!=null && this.dumpConfigurazione.getRichiestaIngresso()!=null) {
						boolean payloadParsing = StatoFunzionalita.ABILITATO.equals(this.dumpConfigurazione.getRichiestaIngresso().getPayload()) &&
								StatoFunzionalita.ABILITATO.equals(this.dumpConfigurazione.getRichiestaIngresso().getPayloadParsing());
						if(!payloadParsing) {
							dumpHeaders = StatoFunzionalita.ABILITATO.equals(this.dumpConfigurazione.getRichiestaIngresso().getHeaders());
							dumpBody = StatoFunzionalita.ABILITATO.equals(this.dumpConfigurazione.getRichiestaIngresso().getPayload());
							dumpBinarioAttivatoTramiteRegolaConfigurazione = dumpHeaders || dumpBody;
						}
					}
				}
				else if(TipoMessaggio.RICHIESTA_USCITA_DUMP_BINARIO.equals(tipoMessaggio)) {
					if(this.dumpConfigurazione!=null && this.dumpConfigurazione.getRichiestaUscita()!=null) {
						boolean payloadParsing = StatoFunzionalita.ABILITATO.equals(this.dumpConfigurazione.getRichiestaUscita().getPayload()) &&
								StatoFunzionalita.ABILITATO.equals(this.dumpConfigurazione.getRichiestaUscita().getPayloadParsing());
						if(!payloadParsing) {
							dumpHeaders = StatoFunzionalita.ABILITATO.equals(this.dumpConfigurazione.getRichiestaUscita().getHeaders());
							dumpBody = StatoFunzionalita.ABILITATO.equals(this.dumpConfigurazione.getRichiestaUscita().getPayload());
							dumpBinarioAttivatoTramiteRegolaConfigurazione = dumpHeaders || dumpBody;
						}
					}
				}
				else if(TipoMessaggio.RISPOSTA_INGRESSO_DUMP_BINARIO.equals(tipoMessaggio)) {
					if(this.dumpConfigurazione!=null && this.dumpConfigurazione.getRispostaIngresso()!=null) {
						boolean payloadParsing = StatoFunzionalita.ABILITATO.equals(this.dumpConfigurazione.getRispostaIngresso().getPayload()) &&
								StatoFunzionalita.ABILITATO.equals(this.dumpConfigurazione.getRispostaIngresso().getPayloadParsing());
						if(!payloadParsing) {
							dumpHeaders = StatoFunzionalita.ABILITATO.equals(this.dumpConfigurazione.getRispostaIngresso().getHeaders());
							dumpBody = StatoFunzionalita.ABILITATO.equals(this.dumpConfigurazione.getRispostaIngresso().getPayload());
							dumpBinarioAttivatoTramiteRegolaConfigurazione = dumpHeaders || dumpBody;
						}
					}
				}
				else if(TipoMessaggio.RISPOSTA_USCITA_DUMP_BINARIO.equals(tipoMessaggio) &&
					this.dumpConfigurazione!=null && this.dumpConfigurazione.getRispostaUscita()!=null) {
					boolean payloadParsing = StatoFunzionalita.ABILITATO.equals(this.dumpConfigurazione.getRispostaUscita().getPayload()) &&
							StatoFunzionalita.ABILITATO.equals(this.dumpConfigurazione.getRispostaUscita().getPayloadParsing());
					if(!payloadParsing) {
						dumpHeaders = StatoFunzionalita.ABILITATO.equals(this.dumpConfigurazione.getRispostaUscita().getHeaders());
						dumpBody = StatoFunzionalita.ABILITATO.equals(this.dumpConfigurazione.getRispostaUscita().getPayload());
						dumpBinarioAttivatoTramiteRegolaConfigurazione = dumpHeaders || dumpBody;
					}
				}
			}
			
			if(!dumpHeaders && !dumpBody && !dumpAttachments && !onlyLogFileTraceHeaders && !onlyLogFileTraceBody) {
				return; // disabilitato
			}
		}
		boolean dumpMultipartHeaders = dumpHeaders;
		
		String identificativoDiagnostico = null;
		if(dumpNormale) {
			identificativoDiagnostico = emitDiagnosticDumpStart(tipoMessaggio, false);
		}
		else {
			boolean onlyFileTrace = !dumpHeaders && !dumpBody && !dumpAttachments;
			
			identificativoDiagnostico = getIdentificativoDiagnostico(tipoMessaggio, onlyFileTrace);
		}
		
		try {
			dumpEngine(onlyLogFileTraceHeaders, onlyLogFileTraceBody, 
					tipoMessaggio, msg, msgBytes, messageType,
					location, transportHeaderParam,
					dumpHeaders, dumpBody, dumpAttachments, dumpMultipartHeaders, 
					dumpIntegrationManager,
					dumpBinario, dumpBinarioAttivatoTramiteRegolaConfigurazione);
		}finally {
			if(identificativoDiagnostico!=null && this.emitDiagnosticDump) {
				try {
					if(this.transazioneApplicativoServer!=null && this.idPortaApplicativa!=null) {
						this.msgDiagErroreDump.setTransazioneApplicativoServer(this.transazioneApplicativoServer, this.idPortaApplicativa);
					}
					this.msgDiagErroreDump.logPersonalizzato(identificativoDiagnostico+"completato");
				}catch(Exception t) {
					if(this.loggerDump!=null) {
						this.loggerDumpError("Riscontrato errore durante l'emissione del diagnostico per il dump del contenuto applicativo in corso del messaggio ("+tipoMessaggio+
								") "+getLogIdTransazione()+":"+t.getMessage(),t);
					}
				}finally {
					this.msgDiagErroreDump.setTransazioneApplicativoServer(null, null);
				}
			}
		}
	}
	
	private String getIdentificativoDiagnostico(TipoMessaggio tipoMessaggio, boolean onlyFileTrace) {
		String identificativoDiagnostico = null;
		if(tipoMessaggio!=null) {
			switch (tipoMessaggio) {
			case RICHIESTA_INGRESSO:
			case RICHIESTA_INGRESSO_DUMP_BINARIO:
				if(onlyFileTrace) {
					identificativoDiagnostico = "dumpContenutiApplicativiFileTrace.richiestaIngresso.";
				}
				else {
					identificativoDiagnostico = "dumpContenutiApplicativi.richiestaIngresso.";
				}
				break;
			case RICHIESTA_USCITA:
			case RICHIESTA_USCITA_DUMP_BINARIO:
				if(onlyFileTrace) {
					identificativoDiagnostico = "dumpContenutiApplicativiFileTrace.richiestaUscita.";
				}
				else {
					identificativoDiagnostico = "dumpContenutiApplicativi.richiestaUscita.";
				}
				break;
			case RISPOSTA_INGRESSO:
			case RISPOSTA_INGRESSO_DUMP_BINARIO:
				if(onlyFileTrace) {
					identificativoDiagnostico = "dumpContenutiApplicativiFileTrace.rispostaIngresso.";
				}
				else {
					identificativoDiagnostico = "dumpContenutiApplicativi.rispostaIngresso.";
				}
				break;
			case RISPOSTA_USCITA:
			case RISPOSTA_USCITA_DUMP_BINARIO:
				if(onlyFileTrace) {
					identificativoDiagnostico = "dumpContenutiApplicativiFileTrace.rispostaUscita.";
				}
				else {
					identificativoDiagnostico = "dumpContenutiApplicativi.rispostaUscita.";
				}
				break;
			default:
				break;
			}
		}
		return identificativoDiagnostico;
	}
	
	private String emitDiagnosticDumpStart(TipoMessaggio tipoMessaggio, boolean onlyFileTrace) {
		String identificativoDiagnostico = getIdentificativoDiagnostico(tipoMessaggio, onlyFileTrace);
		emitDiagnosticDumpStart(tipoMessaggio, identificativoDiagnostico);
		return identificativoDiagnostico;
	}
	private void emitDiagnosticDumpStart(TipoMessaggio tipoMessaggio, String identificativoDiagnostico) {
		if(identificativoDiagnostico!=null && this.emitDiagnosticDump) {
			try {
				if(this.transazioneApplicativoServer!=null && this.idPortaApplicativa!=null) {
					this.msgDiagErroreDump.setTransazioneApplicativoServer(this.transazioneApplicativoServer, this.idPortaApplicativa);
				}
				this.msgDiagErroreDump.logPersonalizzato(identificativoDiagnostico+"inCorso");
			}catch(Exception t) {
				if(this.loggerDump!=null) {
					this.loggerDumpError("Riscontrato errore durante l'emissione del diagnostico per il dump del contenuto applicativo in corso del messaggio ("+tipoMessaggio+
							") "+getLogIdTransazione()+":"+t.getMessage(),t);
				}
			}finally {
				this.msgDiagErroreDump.setTransazioneApplicativoServer(null, null);
			}
		}
	}
	
	
	private List<String> initDumpWhiteHeaderList(TipoMessaggio tipoMessaggio) {
		return initDumpHeaderList(true, tipoMessaggio);
	}
	private List<String> initDumpBlackHeaderList(TipoMessaggio tipoMessaggio) {
		return initDumpHeaderList(false, tipoMessaggio);
	}
	private List<String> initDumpHeaderList(boolean whiteList, TipoMessaggio tipoMessaggio) {
		
		List<String> l = initDumpHeaderPorta(whiteList, tipoMessaggio);
		if(l!=null && !l.isEmpty()) {
			return l;
		}
				
		if(this.tipoPdD!=null && tipoMessaggio!=null) {
			l = initDumpHeaderListRequestResponse(whiteList, tipoMessaggio);
			if(l!=null && !l.isEmpty()) {
				return l;
			}
		}
		
		if(this.tipoPdD!=null) {
			l = initDumpHeaderList(whiteList, 
					this.properties.getDumpHeaderErogazioniWhiteList(), 
					this.properties.getDumpHeaderErogazioniBlackList(), 
					this.properties.getDumpHeaderFruizioniWhiteList(), 
					this.properties.getDumpHeaderFruizioniBlackList());
			if(l!=null && !l.isEmpty()) {
				return l;
			}
		}
		
		return whiteList ? this.properties.getDumpHeaderWhiteList() : this.properties.getDumpHeaderBlackList();
	}
	private List<Proprieta> getProprietaPortaList() throws DriverConfigurazioneException{
		List<Proprieta> lReturn = null;
		if(this.tipoPdD!=null && this.nomePorta!=null) {
			
			ConfigurazionePdDManager configPdDManager = ConfigurazionePdDManager.getInstance(this.statoRichiesta!=null ? this.statoRichiesta : this.statoRisposta);
			
			if(TipoPdD.APPLICATIVA.equals(this.tipoPdD)) {
				IDPortaApplicativa idPA = new IDPortaApplicativa();
				idPA.setNome(this.nomePorta);
				PortaApplicativa pa = configPdDManager.getPortaApplicativa_SafeMethod(idPA, this.requestInfo);
				if(pa!=null) {
					return pa.getProprieta();
				}
			}
			else if(TipoPdD.DELEGATA.equals(this.tipoPdD)) {
				IDPortaDelegata idPD = new IDPortaDelegata();
				idPD.setNome(this.nomePorta);
				PortaDelegata pd = configPdDManager.getPortaDelegata_SafeMethod(idPD, this.requestInfo);
				if(pd!=null) {
					return pd.getProprieta();
				}
			}
			
		}
		return lReturn;
	}
	private List<String> initDumpHeaderPorta(boolean whiteList, TipoMessaggio tipoMessaggio) {
		
		List<String> l = null;
		
		List<Proprieta> portaProps = null;
		try {
			portaProps = getProprietaPortaList();
		}catch(Exception e) {
			this.loggerDumpError("Recupero proprietà dalla porta '"+this.nomePorta+"' (tipo: "+this.tipoPdD+") fallito: "+e.getMessage(),e);
		}
		
		if(portaProps!=null && !portaProps.isEmpty()) {
			
			l = initDumpHeaderListRequestResponsePorta(whiteList, tipoMessaggio, portaProps);
			if(l!=null && !l.isEmpty()) {
				return l;
			}
			
			List<String> white = CostantiProprieta.getRegistrazioneMessaggiWhiteList(portaProps);
			List<String> black = CostantiProprieta.getRegistrazioneMessaggiBlackList(portaProps);
			l = initDumpHeaderList(whiteList, 
					white, 
					black, 
					white, 
					black);
			if(l!=null && !l.isEmpty()) {
				return l;
			}
			
		}
		
		return l;
	}
	private List<String> initDumpHeaderListRequestResponsePorta(boolean whiteList, TipoMessaggio tipoMessaggio, List<Proprieta> proprieta) {
		List<String> l = null;
		
		if(this.tipoPdD!=null && tipoMessaggio!=null) {
			switch (tipoMessaggio) {
			
			case RICHIESTA_INGRESSO:
			case RICHIESTA_INGRESSO_DUMP_BINARIO:{
				List<String> white = CostantiProprieta.getRegistrazioneMessaggiRichiestaIngressoWhiteList(proprieta);
				List<String> black = CostantiProprieta.getRegistrazioneMessaggiRichiestaIngressoBlackList(proprieta);
				l = initDumpHeaderList(whiteList, 
						white, 
						black, 
						white, 
						black);
				break;
			}
			case RICHIESTA_USCITA:
			case RICHIESTA_USCITA_DUMP_BINARIO:{
				List<String> white = CostantiProprieta.getRegistrazioneMessaggiRichiestaUscitaWhiteList(proprieta);
				List<String> black = CostantiProprieta.getRegistrazioneMessaggiRichiestaUscitaBlackList(proprieta);
				l = initDumpHeaderList(whiteList, 
						white, 
						black, 
						white, 
						black);
				break;
			}
			case RISPOSTA_INGRESSO:
			case RISPOSTA_INGRESSO_DUMP_BINARIO:{
				List<String> white = CostantiProprieta.getRegistrazioneMessaggiRispostaIngressoWhiteList(proprieta);
				List<String> black = CostantiProprieta.getRegistrazioneMessaggiRispostaIngressoBlackList(proprieta);
				l = initDumpHeaderList(whiteList, 
						white, 
						black, 
						white, 
						black);
				break;
			}
	
			case RISPOSTA_USCITA:
			case RISPOSTA_USCITA_DUMP_BINARIO:{
				List<String> white = CostantiProprieta.getRegistrazioneMessaggiRispostaUscitaWhiteList(proprieta);
				List<String> black = CostantiProprieta.getRegistrazioneMessaggiRispostaUscitaBlackList(proprieta);
				l = initDumpHeaderList(whiteList, 
						white, 
						black, 
						white, 
						black);
				break;
			}
				
			default:
				break;
			}
		}
		return l;
	}
	private List<String> initDumpHeaderListRequestResponse(boolean whiteList, TipoMessaggio tipoMessaggio) {
		
		List<String> l = null;
		
		if(this.tipoPdD!=null && tipoMessaggio!=null) {
			switch (tipoMessaggio) {
			
			case RICHIESTA_INGRESSO:
			case RICHIESTA_INGRESSO_DUMP_BINARIO:
				l = initDumpHeaderList(whiteList, 
						this.properties.getDumpHeaderErogazioniRichiestaIngressoWhiteList(), 
						this.properties.getDumpHeaderErogazioniRichiestaIngressoBlackList(), 
						this.properties.getDumpHeaderFruizioniRichiestaIngressoWhiteList(), 
						this.properties.getDumpHeaderFruizioniRichiestaIngressoBlackList());
				break;
	
			case RICHIESTA_USCITA:
			case RICHIESTA_USCITA_DUMP_BINARIO:
				l = initDumpHeaderList(whiteList, 
						this.properties.getDumpHeaderErogazioniRichiestaUscitaWhiteList(), 
						this.properties.getDumpHeaderErogazioniRichiestaUscitaBlackList(), 
						this.properties.getDumpHeaderFruizioniRichiestaUscitaWhiteList(), 
						this.properties.getDumpHeaderFruizioniRichiestaUscitaBlackList());
				break;
				
			case RISPOSTA_INGRESSO:
			case RISPOSTA_INGRESSO_DUMP_BINARIO:
				l = initDumpHeaderList(whiteList, 
						this.properties.getDumpHeaderErogazioniRispostaIngressoWhiteList(), 
						this.properties.getDumpHeaderErogazioniRispostaIngressoBlackList(), 
						this.properties.getDumpHeaderFruizioniRispostaIngressoWhiteList(), 
						this.properties.getDumpHeaderFruizioniRispostaIngressoBlackList());
				break;
	
			case RISPOSTA_USCITA:
			case RISPOSTA_USCITA_DUMP_BINARIO:
				l = initDumpHeaderList(whiteList, 
						this.properties.getDumpHeaderErogazioniRispostaUscitaWhiteList(), 
						this.properties.getDumpHeaderErogazioniRispostaUscitaBlackList(), 
						this.properties.getDumpHeaderFruizioniRispostaUscitaWhiteList(), 
						this.properties.getDumpHeaderFruizioniRispostaUscitaBlackList());
				break;
				
			default:
				break;
			}
		}
		return l;
	}
	private List<String> initDumpHeaderList(boolean whiteList, List<String> erogazioniWhiteList, List<String> erogazioniBlackList, List<String> fruizioniWhiteList, List<String> fruizioniBlackList){
		List<String> l = null;
		if(TipoPdD.APPLICATIVA.equals(this.tipoPdD)) {
			l = whiteList ? erogazioniWhiteList : erogazioniBlackList;
		}
		else if(TipoPdD.DELEGATA.equals(this.tipoPdD)) {
			l = whiteList ? fruizioniWhiteList : fruizioniBlackList;
		}
		return l;
	}
	
	
	private void dumpEngine(boolean onlyLogFileTraceHeaders, boolean onlyLogFileTraceBody, 
			TipoMessaggio tipoMessaggio,OpenSPCoop2Message msg,DumpByteArrayOutputStream msgBytes, MessageType messageType,
			String location,Map<String, List<String>> transportHeaderParam,
			boolean dumpHeaders, boolean dumpBody, boolean dumpAttachments, boolean dumpMultipartHeaders, 
			boolean dumpIntegrationManager,
			boolean dumpBinario, boolean dumpBinarioAttivatoTramiteRegolaConfigurazione) throws DumpException {
		
		
		Messaggio messaggio = new Messaggio();
		if(this.protocolFactory!=null) {
			messaggio.setProtocollo(this.protocolFactory.getProtocol());
		}
		messaggio.setTipoMessaggio(tipoMessaggio);
		if(msg!=null) {
			messaggio.setFormatoMessaggio(msg.getMessageType());
		}
		else if(messageType!=null) {
			messaggio.setFormatoMessaggio(messageType);
		}
		
		messaggio.setGdo(DateManager.getDate());
		
		messaggio.setDominio(this.dominio);
		messaggio.setTipoPdD(this.tipoPdD);
		messaggio.setIdFunzione(this.idModulo);
		
		messaggio.setIdTransazione(this.idTransazione);
		messaggio.setIdBusta(this.idMessaggio);
		messaggio.setFruitore(this.fruitore);
		messaggio.setServizio(this.servizio);
		
		if(this.protocolFactory!=null) {
			messaggio.setProtocollo(this.protocolFactory.getProtocol());
		}
		
		
		
		// HEADERS
		Map<String, List<String>> transportHeader = new HashMap<>(); // uso anche sotto per content type in caso msg bytes
		try{
			if(transportHeaderParam!=null && transportHeaderParam.size()>0){
				transportHeader.putAll(transportHeaderParam);
			}
			if(msg!=null){
				OpenSPCoop2MessageProperties forwardHeader = null;
				if(TipoMessaggio.RICHIESTA_USCITA.equals(tipoMessaggio)){
					if(ServiceBinding.REST.equals(msg.getServiceBinding())) {
						forwardHeader = msg.getForwardTransportHeader(this.properties.getRESTServicesHeadersForwardConfig(true));
					}
					else {
						forwardHeader = msg.getForwardTransportHeader(this.properties.getSOAPServicesHeadersForwardConfig(true));
					}
				}
				else if(TipoMessaggio.RISPOSTA_USCITA.equals(tipoMessaggio)){
					if(ServiceBinding.REST.equals(msg.getServiceBinding())) {
						forwardHeader = msg.getForwardTransportHeader(this.properties.getRESTServicesHeadersForwardConfig(false));
					}
					else {
						forwardHeader = msg.getForwardTransportHeader(this.properties.getSOAPServicesHeadersForwardConfig(false));
					}
				}
				if(forwardHeader!=null && forwardHeader.size()>0){
					Iterator<String> enHdr = forwardHeader.getKeys();
					while (enHdr.hasNext()) {
						String key = enHdr.next();
						if(key!=null){
							List<String> values = forwardHeader.getPropertyValues(key);
							if(values!=null && !values.isEmpty()){
								transportHeader.put(key, values);
							}
						}
					}
				}
			}
			if(msg!=null &&
					!TransportUtils.containsKey(transportHeader, HttpConstants.CONTENT_TYPE)) {
				String contentType = msg.getContentType();
				if(contentType!=null) {
					TransportUtils.setHeader(transportHeader, HttpConstants.CONTENT_TYPE, contentType);
				}
			}
		}catch(Exception e){
			// Registro solo l'errore sul file dump.log
			// Si tratta di un errore che non dovrebbe mai avvenire
			String messaggioErrore = "Riscontrato errore durante la lettura degli header di trasporto del contenuto applicativo presente nel messaggio ("+tipoMessaggio+
					") "+getLogIdTransazione()+":"+e.getMessage();
			this.loggerDumpError(messaggioErrore);
			OpenSPCoop2Logger.getLoggerOpenSPCoopCore().error(messaggioErrore);
		}
		if( (dumpHeaders || onlyLogFileTraceHeaders) &&
			(transportHeader!=null && transportHeader.size()>0)
			){
				
			List<String> filterList = null;
			boolean white = false;
			
			List<String> dumpHeaderWhiteList = initDumpWhiteHeaderList(tipoMessaggio);
			List<String> dumpHeaderBlackList = initDumpBlackHeaderList(tipoMessaggio);
			
			if(dumpHeaderWhiteList!=null && !dumpHeaderWhiteList.isEmpty()) {
				filterList = dumpHeaderWhiteList;
				white = true;
			}
			else if(dumpHeaderBlackList!=null && !dumpHeaderBlackList.isEmpty()) {
				filterList = dumpHeaderBlackList;
				white = false;
			}
			
			Iterator<String> keys = transportHeader.keySet().iterator();
			while (keys.hasNext()) {
				String key = keys.next();					
				if(key!=null){
					
					boolean add = false;
					if(filterList!=null) {
						boolean find = false;
						for (String filterHdr : filterList) {
							if(filterHdr.equalsIgnoreCase(key)) {
								find = true;
								break;
							}
						}
						if(white) {
							if(find) {
								add = true;
							}
						}
						else {
							if(!find) {
								add = true;
							}
						}
					}
					else {
						add = true;
					}
					
					if(add) {
						List<String> values = transportHeader.get(key);
						messaggio.getHeaders().put(key, values);
					}
				}
			}
		}
		
		
		
		
		// BODY e ATTACHMENTS
		DumpMessaggio dumpMessaggio = null;
		DumpMessaggioConfig dumpMessaggioConfig = null;
		try {
			if( (dumpBody || onlyLogFileTraceBody) 
					|| 
				dumpAttachments
				) {
				if(msg!=null){
					dumpMessaggioConfig = new DumpMessaggioConfig();
					dumpMessaggio = fillMessaggio(msg, dumpMessaggioConfig,
							dumpBody, dumpAttachments, dumpMultipartHeaders,
							messaggio);
				}
				else {
					
					if( dumpBody || onlyLogFileTraceBody ) {
					
						if(transportHeader!=null && !transportHeader.isEmpty()) {
							Iterator<String> keys = transportHeader.keySet().iterator();
							while (keys.hasNext()) {
								String key = keys.next();
								if(HttpConstants.CONTENT_TYPE.equalsIgnoreCase(key)){
									List<String> values = transportHeader.get(key);	
									if(values!=null && !values.isEmpty()) {
										messaggio.setContentType(values.get(0));
									}
									break;
								}
							}
						}
						
						messaggio.setBody(msgBytes);
					}
				}
			}
		}catch(Exception e){
			try{
				// Registro l'errore sul file dump.log
				this.loggerDumpError("Riscontrato errore durante la preparazione al dump del contenuto applicativo presente nel messaggio ("+tipoMessaggio+
						") "+getLogIdTransazione()+":"+e.getMessage());
			}catch(Exception eLog){
				// ignore
			}
			loggerOpenSPCoopResourcesError("Errore durante la preparazione al dump del contenuto applicativo presente nel messaggio ("+tipoMessaggio+
					") "+getLogIdTransazione()+": "+e.getMessage(),e);
			try{
				this.msgDiagErroreDump.addKeyword(CostantiPdD.KEY_TRACCIA_TIPO, tipoMessaggio.getValue());
				this.msgDiagErroreDump.addKeyword(CostantiPdD.KEY_TRACCIAMENTO_ERRORE,e.getMessage());
				this.msgDiagErroreDump.logPersonalizzato(DIAGNOSTICO_REGISTRAZIONE_NON_RIUSCITA);
			}catch(Exception eMsg){
				// ignore
			}
			gestioneErroreDump(e);
		}
		
		
		
		
		
		// TransazioneContext
		if(this.properties.isTransazioniSaveDumpInUniqueTransaction() || onlyLogFileTraceBody || onlyLogFileTraceHeaders) {
			
			
			if(this.transazioneApplicativoServer!=null) {
				try {
					// forzo
					messaggio.setIdTransazione(this.transazioneApplicativoServer.getIdTransazione());
					messaggio.setServizioApplicativoErogatore(this.transazioneApplicativoServer.getServizioApplicativoErogatore());
					messaggio.setDataConsegna(this.dataConsegnaTransazioneApplicativoServer);
					GestoreConsegnaMultipla.getInstance().safeSave(messaggio, this.idPortaApplicativa, this.statoRichiesta!=null ? this.statoRichiesta : this.statoRisposta, this.requestInfo, this.pddContext);
				}catch(Exception t) {
					String msgError = "Errore durante il salvataggio delle informazioni relative al servizio applicativo: "+t.getMessage();
					this.loggerDumpError(msgError,t);
					loggerOpenSPCoopResourcesError(msgError,t);
					try{
						this.msgDiagErroreDump.addKeyword(CostantiPdD.KEY_TRACCIA_TIPO, tipoMessaggio.getValue());
						this.msgDiagErroreDump.addKeyword(CostantiPdD.KEY_TRACCIAMENTO_ERRORE,t.getMessage());
						this.msgDiagErroreDump.logPersonalizzato(DIAGNOSTICO_REGISTRAZIONE_NON_RIUSCITA);
					}catch(Exception eMsg){
						// ignore
					}
					gestioneErroreDump(t);
				}
			}
			else {
			
				Exception exc = null;
				boolean gestioneStateful = false;
				try {
					Transaction tr = TransactionContext.getTransaction(this.idTransazione);
					if(messaggio.getBody()!=null) {
						messaggio.getBody().lock();
					}
					tr.addMessaggio(messaggio, onlyLogFileTraceHeaders, onlyLogFileTraceBody);
				}catch(TransactionDeletedException | TransactionNotExistsException e){
					gestioneStateful = true;
				}catch(Exception e){
					exc = e;
				}
				if(gestioneStateful && !dumpIntegrationManager){
					try{
						/**System.out.println("@@@@@REPOSITORY@@@@@ LOG DUMP ID TRANSAZIONE ["+idTransazione+"] ADD");*/
						RepositoryGestioneStateful.addMessaggio(this.idTransazione, messaggio);
					}catch(Exception e){
						exc = e;
					}
				}
				if(exc!=null) {
					try{
						// Registro l'errore sul file dump.log
						this.loggerDumpError("Riscontrato errore durante la registrazione, nel contesto della transazione, del dump del contenuto applicativo presente nel messaggio ("+tipoMessaggio+
								") "+getLogIdTransazione()+":"+exc.getMessage());
					}catch(Exception eLog){
						// ignore
					}
					loggerOpenSPCoopResourcesError("Errore durante la registrazione, nel contesto della transazione, del contenuto applicativo presente nel messaggio ("+tipoMessaggio+
							") "+getLogIdTransazione()+": "+exc.getMessage(),exc);
					try{
						this.msgDiagErroreDump.addKeyword(CostantiPdD.KEY_TRACCIA_TIPO, tipoMessaggio.getValue());
						this.msgDiagErroreDump.addKeyword(CostantiPdD.KEY_TRACCIAMENTO_ERRORE,exc.getMessage());
						this.msgDiagErroreDump.logPersonalizzato(DIAGNOSTICO_REGISTRAZIONE_NON_RIUSCITA);
					}catch(Exception eMsg){
						// ignore
					}
					gestioneErroreDump(exc);
				}
				
			}
		}
		
		// Log4J
		if(OpenSPCoop2Logger.loggerDumpAbilitato && 
				(!dumpBinario || dumpBinarioAttivatoTramiteRegolaConfigurazione) // Se attivato da console deve finire in dump.log
				){
			// Su file di log, il log binario viene registrato in altra maniera sul file openspcoop2_connettori.log
			try{
				StringBuilder out = new StringBuilder();
				out.append(this.signature);
				out.append("TipoMessaggio:"+tipoMessaggio.getValue());
				
				if(this.idTransazione!=null){
					out.append(" idTransazione:");
					out.append(this.idTransazione);
				}
				
				if(this.idMessaggio!=null){
					out.append(" idMessaggio:");
					out.append(this.idMessaggio);
				}
				
				if(location!=null){
					if(TipoMessaggio.RICHIESTA_INGRESSO.equals(tipoMessaggio) || TipoMessaggio.RISPOSTA_USCITA.equals(tipoMessaggio)){
						out.append(" source:");
					}else{
						out.append(" location:");
					}
					out.append(location);
				}
				
				if( this.fruitore!=null ){
					out.append(" FR:");
					out.append(this.fruitore.toString());
				}
				if( this.fruitore!=null && this.servizio!=null)
					out.append(" -> ");
				if( this.servizio!=null ){
					if(this.servizio.getNome()!=null){
						out.append(" S:");
						try{
							out.append(IDServizioFactory.getInstance().getUriFromIDServizio(this.servizio));
						}catch(Exception e){
							out.append(this.servizio.toString(false));
						}
					}else if(this.servizio.getSoggettoErogatore()!=null){
						out.append(" ER:");
						out.append(this.servizio.getSoggettoErogatore().toString());
					}
				}
				
				out.append(" \n");
				
				// HEADER
				
				if(dumpHeaders) {
					out.append("------ Header di trasporto ------\n");
					if(messaggio.getHeaders()!=null && messaggio.getHeaders().size()>0) {
						Iterator<?> it = messaggio.getHeaders().keySet().iterator();
						while (it.hasNext()) {
							String key = (String) it.next();
							if(key!=null){
								List<String> values = messaggio.getHeaders().get(key);
								if(values!=null && !values.isEmpty()) {
									for (String value : values) {
										out.append("- "+key+": "+value+"\n");
									}
								}
							}
						}
					}
					else {
						out.append("Non presenti\n");
					}
				}
				
				// BODY e ATTACHMENTS
				
				if(dumpBody || dumpAttachments) {
					if(msg!=null){
	
						if(dumpAttachments && !this.properties.isDumpAllAttachments()) {
							// Ricalcolo gli attachments prendendo solo quelli stampabili
							if(ServiceBinding.SOAP.equals(msg.getServiceBinding())){
								dumpMessaggio = DumpSoapMessageUtils.dumpMessage(msg.castAsSoap(), dumpMessaggioConfig, false);
							}
							else{
								dumpMessaggio = DumpRestMessageUtils.dumpMessage(msg.castAsRest(), dumpMessaggioConfig, false);
							}
						}
						
						if(dumpMessaggio!=null) {
							out.append(dumpMessaggio.toString(dumpMessaggioConfig,this.properties.isDumpAllAttachments()));
						}
	
					}else{
						if(msgBytes!=null && msgBytes.size()>0) {
							if(org.openspcoop2.utils.mime.MultipartUtils.messageWithAttachment(msgBytes.toByteArray())){
								out.append("------ MessageWithAttachments ------\n");
							}else{
								out.append("------ Message ------\n");
							}
							out.append(msgBytes.toString());
						}
						else {
							out.append("------ Message ------\n");
							out.append("Non presente\n");
						}
					}
				
				}
				
				String msgDump = out.toString();
				this.loggerDump.info(msgDump); 
				
			}catch(Exception e){
				try{
					// Registro l'errore sul file dump.log
					this.loggerDumpError("Riscontrato errore durante il dump su file di log del contenuto applicativo presente nel messaggio ("+tipoMessaggio+
							") "+getLogIdTransazione()+":"+e.getMessage());
				}catch(Exception eLog){
					// ignore
				}
				loggerOpenSPCoopResourcesError("Errore durante il dump su file di log del contenuto applicativo presente nel messaggio ("+tipoMessaggio+
						") "+getLogIdTransazione()+": "+e.getMessage(),e);
				try{
					this.msgDiagErroreDump.addKeyword(CostantiPdD.KEY_TRACCIA_TIPO, tipoMessaggio.getValue());
					this.msgDiagErroreDump.addKeyword(CostantiPdD.KEY_TRACCIAMENTO_ERRORE,e.getMessage());
					this.msgDiagErroreDump.logPersonalizzato(DIAGNOSTICO_REGISTRAZIONE_NON_RIUSCITA);
				}catch(Exception eMsg){
					// ignore
				}
				gestioneErroreDump(e);
			}
		}
		
		// Il dump via API lo effettuo solamente se ho davvero un messaggio OpenSPCoop
		// Senno ottengo errori all'interno delle implementazioni degli appender dump
		boolean onlyLogFileTrace = false;
		if( (onlyLogFileTraceHeaders && !dumpHeaders)
				||
			(onlyLogFileTraceBody && !dumpBody) ) {
			onlyLogFileTrace=true;
		}
		if(!onlyLogFileTrace && !dumpIntegrationManager) {
			for(int i=0; i<this.loggerDumpOpenSPCoopAppender.size();i++){
				try{
					boolean headersCompact = false; //non supportato nei plugins
					this.loggerDumpOpenSPCoopAppender.get(i).dump(getConnectionFromState(false),messaggio,headersCompact);
				}catch(Exception e){
					loggerOpenSPCoopResourcesError("Errore durante il dump personalizzato ["+this.tipoDumpOpenSPCoopAppender.get(i)+
							"] del contenuto applicativo presente nel messaggio ("+tipoMessaggio+") "+getLogIdTransazione()+": "+e.getMessage(),e);
					try{
						this.msgDiagErroreDump.addKeyword(CostantiPdD.KEY_TRACCIA_TIPO, tipoMessaggio.getValue());
						this.msgDiagErroreDump.addKeyword(CostantiPdD.KEY_TRACCIAMENTO_ERRORE,e.getMessage());
						this.msgDiagErroreDump.addKeyword(CostantiPdD.KEY_TRACCIAMENTO_PERSONALIZZATO,this.tipoDumpOpenSPCoopAppender.get(i));
						this.msgDiagErroreDump.logPersonalizzato("dumpContenutiApplicativi.registrazioneNonRiuscita.openspcoopAppender");
					}catch(Exception eMsg){
						// ignore
					}
					gestioneErroreDump(e);
				}
			}
		}

	}
	
	public static DumpMessaggio fillMessaggio(OpenSPCoop2Message msg, DumpMessaggioConfig dumpMessaggioConfig,
			boolean dumpBody, boolean dumpAttachments, boolean dumpMultipartHeaders,
			Messaggio messaggio) throws MessageException  {
			
		DumpMessaggio dumpMessaggio = null;
		
		dumpMessaggioConfig.setDumpBody(dumpBody);
		dumpMessaggioConfig.setDumpHeaders(false); // utilizzo sempre quello fornito poiche' per il raw il msg openspcoop2 non e' disponibile, altrimenti perche' devo considerare i forward header
		dumpMessaggioConfig.setDumpAttachments(dumpAttachments);
		dumpMessaggioConfig.setDumpMultipartHeaders(dumpMultipartHeaders);
		
		if(ServiceBinding.SOAP.equals(msg.getServiceBinding())){
			dumpMessaggio = DumpSoapMessageUtils.dumpMessage(msg.castAsSoap(), dumpMessaggioConfig, true); /**!Devo leggere tutti gli attachments! this.properties.isDumpAllAttachments());*/
		}
		else{
			dumpMessaggio = DumpRestMessageUtils.dumpMessage(msg.castAsRest(), dumpMessaggioConfig, true); /**!Devo leggere tutti gli attachments! this.properties.isDumpAllAttachments());*/
		}
		
		messaggio.setContentType(dumpMessaggio.getContentType());
		
		if(dumpBody) {
			messaggio.setBody(DumpByteArrayOutputStream.newInstance(dumpMessaggio.getBody()));
			if(dumpMessaggio.getMultipartInfoBody()!=null) {
				BodyMultipartInfo bodyMultipartInfo = new BodyMultipartInfo();
				
				bodyMultipartInfo.setContentId(dumpMessaggio.getMultipartInfoBody().getContentId());
				bodyMultipartInfo.setContentLocation(dumpMessaggio.getMultipartInfoBody().getContentLocation());
				bodyMultipartInfo.setContentType(dumpMessaggio.getMultipartInfoBody().getContentType());
				
				if(dumpMultipartHeaders &&
					dumpMessaggio.getMultipartInfoBody().getHeadersValues()!=null &&
					dumpMessaggio.getMultipartInfoBody().getHeadersValues().size()>0) {
					Iterator<?> it = dumpMessaggio.getMultipartInfoBody().getHeadersValues().keySet().iterator();
					while (it.hasNext()) {
						String key = (String) it.next();
						List<String> values = dumpMessaggio.getMultipartInfoBody().getHeadersValues().get(key);
						bodyMultipartInfo.getHeaders().put(key, values);
					}
				}
				
				messaggio.setBodyMultipartInfo(bodyMultipartInfo);
			}
		}
		
		if(dumpAttachments && dumpMessaggio.getAttachments()!=null &&
				!dumpMessaggio.getAttachments().isEmpty()) {
			for (DumpAttachment dumpAttach : dumpMessaggio.getAttachments()) {
				
				Attachment attachment = new Attachment();
				
				attachment.setContentId(dumpAttach.getContentId());
				attachment.setContentLocation(dumpAttach.getContentLocation());
				attachment.setContentType(dumpAttach.getContentType());
				
				if(dumpMultipartHeaders &&
					dumpAttach.getHeadersValues()!=null &&
					dumpAttach.getHeadersValues().size()>0) {
					Iterator<?> it = dumpAttach.getHeadersValues().keySet().iterator();
					while (it.hasNext()) {
						String key = (String) it.next();
						List<String> values = dumpAttach.getHeadersValues().get(key);
						attachment.getHeaders().put(key, values);
					}
				}
				
				if(dumpAttach.getContent()!=null)
					attachment.setContent(dumpAttach.getContent());
				else if(dumpAttach.getErrorContentNotSerializable()!=null)
					attachment.setContent(dumpAttach.getErrorContentNotSerializable().getBytes());
				else
					throw new MessageException("Contenuto dell'attachment con id '"+attachment.getContentId()+"' non presente ?");
				
				messaggio.getAttachments().add(attachment);
			}
		}
		
		return dumpMessaggio;
	}

	
	private void gestioneErroreDump(Throwable e) throws DumpException{
		
		if(this.properties.isDumpFallitoBloccoServiziPdD()){
			Dump.setSistemaDumpDisponibile(false);
			Dump.setMotivoMalfunzionamentoDump(e);
			try{
				this.msgDiagErroreDump.logPersonalizzato("dumpContenutiApplicativi.errore.bloccoServizi");
			}catch(Exception eMsg){
				// ignore
			}
			loggerOpenSPCoopResourcesError("Il Sistema di dump dei contenuti applicativi ha rilevato un errore "+
					"durante la registrazione di un contenuto applicativo, tutti i servizi/moduli della porta di dominio sono sospesi."+
					" Si richiede un intervento sistemistico per la risoluzione del problema e il riavvio di GovWay. "+
					"Errore rilevato: ",e);
		}
		
		if(this.properties.isDumpFallitoBloccaCooperazioneInCorso()){
			throw new DumpException(e);
		}
	}
	
	private String getLogIdTransazione() {
		return "con identificativo di transazione ["+this.idTransazione+"]";
	}
}

