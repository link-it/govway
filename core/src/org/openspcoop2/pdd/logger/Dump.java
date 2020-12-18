/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it). 
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
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import org.openspcoop2.core.constants.TipoPdD;
import org.openspcoop2.core.id.IDPortaApplicativa;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.driver.IDServizioFactory;
import org.openspcoop2.core.transazioni.TransazioneApplicativoServer;
import org.openspcoop2.core.transazioni.constants.TipoMessaggio;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.OpenSPCoop2MessageProperties;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.message.rest.DumpRestMessageUtils;
import org.openspcoop2.message.soap.DumpSoapMessageUtils;
import org.openspcoop2.message.utils.DumpAttachment;
import org.openspcoop2.message.utils.DumpMessaggio;
import org.openspcoop2.message.utils.DumpMessaggioConfig;
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
import org.openspcoop2.protocol.engine.URLProtocolContext;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.dump.Attachment;
import org.openspcoop2.protocol.sdk.dump.BodyMultipartInfo;
import org.openspcoop2.protocol.sdk.dump.DumpException;
import org.openspcoop2.protocol.sdk.dump.IDumpProducer;
import org.openspcoop2.protocol.sdk.dump.Messaggio;
import org.openspcoop2.protocol.sdk.state.IState;
import org.openspcoop2.protocol.sdk.state.StateMessage;
import org.openspcoop2.protocol.sdk.tracciamento.TracciamentoException;
import org.openspcoop2.utils.date.DateManager;
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
	public static boolean sistemaDumpDisponibile = true;
	/** Primo errore avvenuto nel momento in cui è stato rilevato un malfunzionamento nel sistema di dump */
	public static Throwable motivoMalfunzionamentoDump = null;


	/**  Logger log4j utilizzato per effettuare un dump dei messaggi applicativi */
	private Logger loggerDump = null;
	
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
	/** PdDContext */
	private PdDContext pddContext;

	/** OpenSPCoopProperties */
	private OpenSPCoop2Properties properties = null;

	/** MsgDiagnostico per eventuali errori di tracciamento */
	private MsgDiagnostico msgDiagErroreDump = null;
	
	/** Appender personalizzati per i dump applicativi di OpenSPCoop */
	private List<IDumpProducer> loggerDumpOpenSPCoopAppender = null; 
	private List<String> tipoDumpOpenSPCoopAppender = null; 
	
	private DumpConfigurazione dumpConfigurazione;
	
	private IProtocolFactory<?> protocolFactory = null;
	private String idTransazione = null;
	
	/** Stati */
	private IState statoRichiesta;
	private IState statoRisposta;
	
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
//	public Dump(IDSoggetto dominio, String modulo, TipoPdD tipoPdD, PdDContext pddContext,
//			DumpConfigurazione dumpConfigurazione) throws DumpException{
//		this(dominio, modulo, null, null, null, tipoPdD, pddContext,null,null,dumpConfigurazione);
//	}
	public Dump(IDSoggetto dominio, String modulo, TipoPdD tipoPdD, String nomePorta, PdDContext pddContext) throws DumpException{ // dump binario
		this(dominio, modulo, null, null, null, tipoPdD, nomePorta, pddContext,null,null,null);
	}
	public Dump(IDSoggetto dominio, String modulo, TipoPdD tipoPdD, String nomePorta, PdDContext pddContext,IState statoRichiesta,IState statoRisposta,
			DumpConfigurazione dumpConfigurazione) throws DumpException{
		this(dominio, modulo, null, null, null, tipoPdD, nomePorta, pddContext,statoRichiesta,statoRisposta,dumpConfigurazione);
	}
	public Dump(IDSoggetto dominio, String modulo, String idMessaggio, IDSoggetto fruitore, IDServizio servizio, 
			TipoPdD tipoPdD, String nomePorta, PdDContext pddContext,IState statoRichiesta,IState statoRisposta,
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
		this.pddContext = pddContext;
		this.properties = OpenSPCoop2Properties.getInstance();
		this.statoRichiesta = statoRichiesta;
		this.statoRisposta = statoRisposta;
		this.dumpConfigurazione = dumpConfigurazione;
		
		this.msgDiagErroreDump = MsgDiagnostico.newInstance(this.tipoPdD,dominio,modulo,nomePorta,this.statoRichiesta,this.statoRisposta);
		this.msgDiagErroreDump.setPrefixMsgPersonalizzati(MsgDiagnosticiProperties.MSG_DIAG_TRACCIAMENTO);
		
		this.idTransazione = (String) this.pddContext.getObject(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE);
		
		// Protocol Factory Manager
		String protocol = null;
		try{
			this.protocolFactory = ProtocolFactoryManager.getInstance().getProtocolFactoryByName((String) pddContext.getObject(org.openspcoop2.core.constants.Costanti.PROTOCOL_NAME));
			protocol = this.protocolFactory.getProtocol();
			this.msgDiagErroreDump.setPddContext(pddContext, this.protocolFactory);
		}catch (Exception e) {
			throw new DumpException("Errore durante l'inizializzazione del ProtocolFactoryManager...",e);
		}
		if(this.dominio==null){
			this.dominio=OpenSPCoop2Properties.getInstance().getIdentitaPortaDefault(protocol);
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

	private Connection getConnectionFromState(boolean richiesta){
		if(richiesta){
			if(this.statoRichiesta!=null && this.statoRichiesta instanceof StateMessage){
				boolean validConnection = false;
				try{
					validConnection = !((StateMessage)this.statoRichiesta).getConnectionDB().isClosed();
				}catch(Exception e){}
				if(validConnection)
					return ((StateMessage)this.statoRichiesta).getConnectionDB();
			}
		}
		else{
			if(this.statoRisposta!=null && this.statoRisposta instanceof StateMessage){
				boolean validConnection = false;
				try{
					validConnection = !((StateMessage)this.statoRisposta).getConnectionDB().isClosed();
				}catch(Exception e){}
				if(validConnection)
					return ((StateMessage)this.statoRisposta).getConnectionDB();
			}
		}
		return null;
	}






	/** ----------------- METODI DI LOGGING  ---------------- */

	public void dumpBinarioRichiestaIngresso(boolean onlyLogFileTrace, byte[] msg, URLProtocolContext protocolContext) throws DumpException {
		if(this.transactionNullable!=null) {
			this.transactionNullable.getTempiElaborazione().startDumpBinarioRichiestaIngresso();
		}
		try {
			dump(onlyLogFileTrace, TipoMessaggio.RICHIESTA_INGRESSO_DUMP_BINARIO,null,msg,protocolContext.getSource(),protocolContext.getParametersTrasporto());
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
			dump(false, TipoMessaggio.RICHIESTA_INGRESSO,msg,null,protocolContext.getSource(),protocolContext.getParametersTrasporto());
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
			dump(false, TipoMessaggio.RICHIESTA_INGRESSO,null,msg,protocolContext.getSource(),protocolContext.getParametersTrasporto());
		}
		finally {
			if(this.transactionNullable!=null) {
				this.transactionNullable.getTempiElaborazione().endDumpRichiestaIngresso();
			}
		}
	}
	

	public void dumpBinarioRichiestaUscita(boolean onlyLogFileTrace, byte[] msg, InfoConnettoreUscita infoConnettore) throws DumpException {
		if(this.transactionNullable!=null) {
			this.transactionNullable.getTempiElaborazione().startDumpBinarioRichiestaUscita();
		}
		try {
			dump(onlyLogFileTrace, TipoMessaggio.RICHIESTA_USCITA_DUMP_BINARIO,null,msg,(infoConnettore!=null ? infoConnettore.getLocation() : null),infoConnettore.getPropertiesTrasporto());
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
			dump(false, TipoMessaggio.RICHIESTA_USCITA,msg,null,(infoConnettore!=null ? infoConnettore.getLocation() : null),infoConnettore.getPropertiesTrasporto());
		}
		finally {
			if(this.transactionNullable!=null) {
				this.transactionNullable.getTempiElaborazione().endDumpRichiestaUscita();
			}
		}
	}

	
	public void dumpBinarioRispostaIngresso(boolean onlyLogFileTrace, byte[] msg, InfoConnettoreUscita infoConnettore, Map<String, String> transportHeaderRisposta) throws DumpException {
		if(this.transactionNullable!=null) {
			this.transactionNullable.getTempiElaborazione().startDumpBinarioRispostaIngresso();
		}
		try {
			dump(onlyLogFileTrace, TipoMessaggio.RISPOSTA_INGRESSO_DUMP_BINARIO,null,msg,(infoConnettore!=null ? infoConnettore.getLocation() : null),transportHeaderRisposta);
		}
		finally {
			if(this.transactionNullable!=null) {
				this.transactionNullable.getTempiElaborazione().endDumpBinarioRispostaIngresso();
			}
		}
	}
	
	public void dumpRispostaIngresso(OpenSPCoop2Message msg, InfoConnettoreUscita infoConnettore, Map<String, String> transportHeaderRisposta) throws DumpException {
		if(this.transactionNullable!=null) {
			this.transactionNullable.getTempiElaborazione().startDumpRispostaIngresso();
		}
		try {
			dump(false, TipoMessaggio.RISPOSTA_INGRESSO,msg,null,(infoConnettore!=null ? infoConnettore.getLocation() : null),transportHeaderRisposta);
		}
		finally {
			if(this.transactionNullable!=null) {
				this.transactionNullable.getTempiElaborazione().endDumpRispostaIngresso();
			}
		}
	}
	

	
	public void dumpBinarioRispostaUscita(boolean onlyLogFileTrace, byte[] msg, URLProtocolContext protocolContext, Map<String, String> transportHeaderRisposta) throws DumpException {
		if(this.transactionNullable!=null) {
			this.transactionNullable.getTempiElaborazione().startDumpBinarioRispostaUscita();
		}
		try {
			dump(onlyLogFileTrace, TipoMessaggio.RISPOSTA_USCITA_DUMP_BINARIO,null,msg,protocolContext.getSource(),transportHeaderRisposta);
		}
		finally {
			if(this.transactionNullable!=null) {
				this.transactionNullable.getTempiElaborazione().endDumpBinarioRispostaUscita();
			}
		}
	}
	
	public void dumpRispostaUscita(OpenSPCoop2Message msg, URLProtocolContext protocolContext, Map<String, String> transportHeaderRisposta) throws DumpException {
		if(this.transactionNullable!=null) {
			this.transactionNullable.getTempiElaborazione().startDumpRispostaUscita();
		}
		try {
			dump(false, TipoMessaggio.RISPOSTA_USCITA,msg,null,protocolContext.getSource(),transportHeaderRisposta);
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
			dump(false, TipoMessaggio.INTEGRATION_MANAGER,msg,null,"IntegrationManager.getMessage()",null);
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
	private void dump(boolean onlyLogFileTrace, TipoMessaggio tipoMessaggio,OpenSPCoop2Message msg,byte[] msgBytes, 
			String location,Map<String, String> transportHeaderParam) throws DumpException {

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
		if(dumpNormale) {
			if(TipoMessaggio.RICHIESTA_INGRESSO.equals(tipoMessaggio)) {
				if(this.dumpConfigurazione!=null && this.dumpConfigurazione.getRichiestaIngresso()!=null) {
					dumpHeaders = StatoFunzionalita.ABILITATO.equals(this.dumpConfigurazione.getRichiestaIngresso().getHeaders());
					dumpBody = StatoFunzionalita.ABILITATO.equals(this.dumpConfigurazione.getRichiestaIngresso().getBody());
					dumpAttachments = StatoFunzionalita.ABILITATO.equals(this.dumpConfigurazione.getRichiestaIngresso().getAttachments());
				}
			}
			else if(TipoMessaggio.RICHIESTA_USCITA.equals(tipoMessaggio)) {
				if(this.dumpConfigurazione!=null && this.dumpConfigurazione.getRichiestaUscita()!=null) {
					dumpHeaders = StatoFunzionalita.ABILITATO.equals(this.dumpConfigurazione.getRichiestaUscita().getHeaders());
					dumpBody = StatoFunzionalita.ABILITATO.equals(this.dumpConfigurazione.getRichiestaUscita().getBody());
					dumpAttachments = StatoFunzionalita.ABILITATO.equals(this.dumpConfigurazione.getRichiestaUscita().getAttachments());
				}
			}
			else if(TipoMessaggio.RISPOSTA_INGRESSO.equals(tipoMessaggio)) {
				if(this.dumpConfigurazione!=null && this.dumpConfigurazione.getRispostaIngresso()!=null) {
					dumpHeaders = StatoFunzionalita.ABILITATO.equals(this.dumpConfigurazione.getRispostaIngresso().getHeaders());
					dumpBody = StatoFunzionalita.ABILITATO.equals(this.dumpConfigurazione.getRispostaIngresso().getBody());
					dumpAttachments = StatoFunzionalita.ABILITATO.equals(this.dumpConfigurazione.getRispostaIngresso().getAttachments());
				}
			}
			else if(TipoMessaggio.RISPOSTA_USCITA.equals(tipoMessaggio)) {
				if(this.dumpConfigurazione!=null && this.dumpConfigurazione.getRispostaUscita()!=null) {
					dumpHeaders = StatoFunzionalita.ABILITATO.equals(this.dumpConfigurazione.getRispostaUscita().getHeaders());
					dumpBody = StatoFunzionalita.ABILITATO.equals(this.dumpConfigurazione.getRispostaUscita().getBody());
					dumpAttachments = StatoFunzionalita.ABILITATO.equals(this.dumpConfigurazione.getRispostaUscita().getAttachments());
				}
			}
			if(!dumpHeaders && !dumpBody && !dumpAttachments) {
				return; // disabilitato
			}
		}
		boolean dumpMultipartHeaders = dumpHeaders;
		
		
		
		
		
		Messaggio messaggio = new Messaggio();
		messaggio.setProtocollo(this.protocolFactory.getProtocol());
		messaggio.setTipoMessaggio(tipoMessaggio);
		if(msg!=null) {
			messaggio.setFormatoMessaggio(msg.getMessageType());
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
		Map<String, String> transportHeader = new HashMap<String, String>(); // uso anche sotto per content type in caso msg bytes
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
						String key = (String) enHdr.next();
						if(key!=null){
							String value = forwardHeader.getProperty(key);
							if(value!=null){
								transportHeader.put(key, value);
							}
						}
					}
				}
			}
			if(msg!=null &&
					!TransportUtils.hasKey(transportHeader, HttpConstants.CONTENT_TYPE)) {
				String contentType = msg.getContentType();
				if(contentType!=null) {
					transportHeader.put(HttpConstants.CONTENT_TYPE, contentType);
				}
			}
		}catch(Exception e){
			// Registro solo l'errore sul file dump.log
			// Si tratta di un errore che non dovrebbe mai avvenire
			String messaggioErrore = "Riscontrato errore durante la lettura degli header di trasporto del contenuto applicativo presente nel messaggio ("+tipoMessaggio+
					") con identificativo di transazione ["+this.idTransazione+"]:"+e.getMessage();
			this.loggerDump.error(messaggioErrore);
			OpenSPCoop2Logger.getLoggerOpenSPCoopCore().error(messaggioErrore);
		}
		if(dumpHeaders) {
			if(transportHeader!=null && transportHeader.size()>0){
				Iterator<String> keys = transportHeader.keySet().iterator();
				while (keys.hasNext()) {
					String key = (String) keys.next();
					if(key!=null){
						String value = transportHeader.get(key);
						messaggio.getHeaders().put(key, value);
					}
				}
			}
		}
		
		
		
		
		// BODY e ATTACHMENTS
		DumpMessaggio dumpMessaggio = null;
		DumpMessaggioConfig dumpMessaggioConfig = null;
		try {
			if(dumpBody || dumpAttachments) {
				if(msg!=null){
					
					dumpMessaggioConfig = new DumpMessaggioConfig();
					dumpMessaggioConfig.setDumpBody(dumpBody);
					dumpMessaggioConfig.setDumpHeaders(false); // utilizzo sempre quello fornito poiche' per il raw il msg openspcoop2 non e' disponibile, altrimenti perche' devo considerare i forward header
					dumpMessaggioConfig.setDumpAttachments(dumpAttachments);
					dumpMessaggioConfig.setDumpMultipartHeaders(dumpMultipartHeaders);
					
					if(ServiceBinding.SOAP.equals(msg.getServiceBinding())){
						dumpMessaggio = DumpSoapMessageUtils.dumpMessage(msg.castAsSoap(), dumpMessaggioConfig, true); //!Devo leggere tutti gli attachments! this.properties.isDumpAllAttachments());
					}
					else{
						dumpMessaggio = DumpRestMessageUtils.dumpMessage(msg.castAsRest(), dumpMessaggioConfig, true); //!Devo leggere tutti gli attachments! this.properties.isDumpAllAttachments());
					}
					
					messaggio.setContentType(dumpMessaggio.getContentType());
					
					if(dumpBody) {
						messaggio.setBody(dumpMessaggio.getBody());
						if(dumpMessaggio.getMultipartInfoBody()!=null) {
							BodyMultipartInfo bodyMultipartInfo = new BodyMultipartInfo();
							
							bodyMultipartInfo.setContentId(dumpMessaggio.getMultipartInfoBody().getContentId());
							bodyMultipartInfo.setContentLocation(dumpMessaggio.getMultipartInfoBody().getContentLocation());
							bodyMultipartInfo.setContentType(dumpMessaggio.getMultipartInfoBody().getContentType());
							
							if(dumpMultipartHeaders) {
								if(dumpMessaggio.getMultipartInfoBody().getHeaders()!=null &&
										dumpMessaggio.getMultipartInfoBody().getHeaders().size()>0) {
									Iterator<?> it = dumpMessaggio.getMultipartInfoBody().getHeaders().keySet().iterator();
									while (it.hasNext()) {
										String key = (String) it.next();
										String value = dumpMessaggio.getMultipartInfoBody().getHeaders().get(key);
										bodyMultipartInfo.getHeaders().put(key, value);
									}
								}
							}
							
							messaggio.setBodyMultipartInfo(bodyMultipartInfo);
						}
					}
					
					if(dumpAttachments && dumpMessaggio.getAttachments()!=null &&
							dumpMessaggio.getAttachments().size()>0) {
						for (DumpAttachment dumpAttach : dumpMessaggio.getAttachments()) {
							
							Attachment attachment = new Attachment();
							
							attachment.setContentId(dumpAttach.getContentId());
							attachment.setContentLocation(dumpAttach.getContentLocation());
							attachment.setContentType(dumpAttach.getContentType());
							
							if(dumpMultipartHeaders) {
								if(dumpAttach.getHeaders()!=null &&
										dumpAttach.getHeaders().size()>0) {
									Iterator<?> it = dumpAttach.getHeaders().keySet().iterator();
									while (it.hasNext()) {
										String key = (String) it.next();
										String value = dumpAttach.getHeaders().get(key);
										attachment.getHeaders().put(key, value);
									}
								}
							}
							
							if(dumpAttach.getContent()!=null)
								attachment.setContent(dumpAttach.getContent());
							else if(dumpAttach.getErrorContentNotSerializable()!=null)
								attachment.setContent(dumpAttach.getErrorContentNotSerializable().getBytes());
							else
								throw new Exception("Contenuto dell'attachment con id '"+attachment.getContentId()+"' non presente ?");
							
							messaggio.getAttachments().add(attachment);
						}
					}
				}
				else {
					
					if(dumpBody) {
					
						if(transportHeader!=null && !transportHeader.isEmpty()) {
							Iterator<String> keys = transportHeader.keySet().iterator();
							while (keys.hasNext()) {
								String key = (String) keys.next();
								String value = null;
								if(HttpConstants.CONTENT_TYPE.equalsIgnoreCase(key)){
									value = transportHeader.get(key);	
									messaggio.setContentType(value);
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
				this.loggerDump.error("Riscontrato errore durante la preparazione al dump del contenuto applicativo presente nel messaggio ("+tipoMessaggio+
						") con identificativo di transazione ["+this.idTransazione+"]:"+e.getMessage());
			}catch(Exception eLog){}
			OpenSPCoop2Logger.loggerOpenSPCoopResources.error("Errore durante la preparazione al dump del contenuto applicativo presente nel messaggio ("+tipoMessaggio+
					") con identificativo di transazione ["+this.idTransazione+"]: "+e.getMessage(),e);
			try{
				this.msgDiagErroreDump.addKeyword(CostantiPdD.KEY_TRACCIA_TIPO, tipoMessaggio.getValue());
				this.msgDiagErroreDump.addKeyword(CostantiPdD.KEY_TRACCIAMENTO_ERRORE,e.getMessage());
				this.msgDiagErroreDump.logPersonalizzato("dumpContenutiApplicativi.registrazioneNonRiuscita");
			}catch(Exception eMsg){}
			gestioneErroreDump(e);
		}
		
		
		
		
		
		// TransazioneContext
		if(this.properties.isTransazioniSaveDumpInUniqueTransaction() || onlyLogFileTrace) {
			
			
			if(this.transazioneApplicativoServer!=null) {
				try {
					// forzo
					messaggio.setIdTransazione(this.transazioneApplicativoServer.getIdTransazione());
					messaggio.setServizioApplicativoErogatore(this.transazioneApplicativoServer.getServizioApplicativoErogatore());
					messaggio.setDataConsegna(this.dataConsegnaTransazioneApplicativoServer);
					GestoreConsegnaMultipla.getInstance().safeSave(messaggio, this.idPortaApplicativa, this.statoRichiesta!=null ? this.statoRichiesta : this.statoRisposta);
				}catch(Throwable t) {
					String msgError = "Errore durante il salvataggio delle informazioni relative al servizio applicativo: "+t.getMessage();
					this.loggerDump.error(msgError,t);
					OpenSPCoop2Logger.loggerOpenSPCoopResources.error(msgError,t);
					try{
						this.msgDiagErroreDump.addKeyword(CostantiPdD.KEY_TRACCIA_TIPO, tipoMessaggio.getValue());
						this.msgDiagErroreDump.addKeyword(CostantiPdD.KEY_TRACCIAMENTO_ERRORE,t.getMessage());
						this.msgDiagErroreDump.logPersonalizzato("dumpContenutiApplicativi.registrazioneNonRiuscita");
					}catch(Exception eMsg){}
					gestioneErroreDump(t);
				}
			}
			else {
			
				Exception exc = null;
				boolean gestioneStateful = false;
				try {
					Transaction tr = TransactionContext.getTransaction(this.idTransazione);
					tr.addMessaggio(messaggio, onlyLogFileTrace);
				}catch(TransactionDeletedException e){
					gestioneStateful = true;
				}catch(TransactionNotExistsException e){
					gestioneStateful = true;
				}catch(Exception e){
					exc = e;
				}
				if(gestioneStateful && !dumpIntegrationManager){
					try{
						//System.out.println("@@@@@REPOSITORY@@@@@ LOG DUMP ID TRANSAZIONE ["+idTransazione+"] ADD");
						RepositoryGestioneStateful.addMessaggio(location, messaggio);
					}catch(Exception e){
						exc = e;
					}
				}
				if(exc!=null) {
					try{
						// Registro l'errore sul file dump.log
						this.loggerDump.error("Riscontrato errore durante la registrazione, nel contesto della transazione, del dump del contenuto applicativo presente nel messaggio ("+tipoMessaggio+
								") con identificativo di transazione ["+this.idTransazione+"]:"+exc.getMessage());
					}catch(Exception eLog){}
					OpenSPCoop2Logger.loggerOpenSPCoopResources.error("Errore durante la registrazione, nel contesto della transazione, del contenuto applicativo presente nel messaggio ("+tipoMessaggio+
							") con identificativo di transazione ["+this.idTransazione+"]: "+exc.getMessage(),exc);
					try{
						this.msgDiagErroreDump.addKeyword(CostantiPdD.KEY_TRACCIA_TIPO, tipoMessaggio.getValue());
						this.msgDiagErroreDump.addKeyword(CostantiPdD.KEY_TRACCIAMENTO_ERRORE,exc.getMessage());
						this.msgDiagErroreDump.logPersonalizzato("dumpContenutiApplicativi.registrazioneNonRiuscita");
					}catch(Exception eMsg){}
					gestioneErroreDump(exc);
				}
				
			}
		}
		
		// Log4J
		if(OpenSPCoop2Logger.loggerDumpAbilitato && !dumpBinario){
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
								String value = messaggio.getHeaders().get(key);
								out.append("- "+key+": "+value+"\n");
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
						
						out.append(dumpMessaggio.toString(dumpMessaggioConfig,this.properties.isDumpAllAttachments()));
	
					}else{
						if(org.openspcoop2.utils.mime.MultipartUtils.messageWithAttachment(msgBytes)){
							out.append("------ MessageWithAttachments ------\n");
						}else{
							out.append("------ Message ------\n");
						}
						out.append(new String(msgBytes));
					}
				
				}
				
				this.loggerDump.info(out.toString()); 
				
			}catch(Exception e){
				try{
					// Registro l'errore sul file dump.log
					this.loggerDump.error("Riscontrato errore durante il dump su file di log del contenuto applicativo presente nel messaggio ("+tipoMessaggio+
							") con identificativo di transazione ["+this.idTransazione+"]:"+e.getMessage());
				}catch(Exception eLog){}
				OpenSPCoop2Logger.loggerOpenSPCoopResources.error("Errore durante il dump su file di log del contenuto applicativo presente nel messaggio ("+tipoMessaggio+
						") con identificativo di transazione ["+this.idTransazione+"]: "+e.getMessage(),e);
				try{
					this.msgDiagErroreDump.addKeyword(CostantiPdD.KEY_TRACCIA_TIPO, tipoMessaggio.getValue());
					this.msgDiagErroreDump.addKeyword(CostantiPdD.KEY_TRACCIAMENTO_ERRORE,e.getMessage());
					this.msgDiagErroreDump.logPersonalizzato("dumpContenutiApplicativi.registrazioneNonRiuscita");
				}catch(Exception eMsg){}
				gestioneErroreDump(e);
			}
		}
		
		// Il dump via API lo effettuo solamente se ho davvero un messaggio OpenSPCoop
		// Senno ottengo errori all'interno delle implementazioni degli appender dump
		if(!onlyLogFileTrace && !dumpIntegrationManager) {
			for(int i=0; i<this.loggerDumpOpenSPCoopAppender.size();i++){
				try{
					this.loggerDumpOpenSPCoopAppender.get(i).dump(getConnectionFromState(false),messaggio);
				}catch(Exception e){
					OpenSPCoop2Logger.loggerOpenSPCoopResources.error("Errore durante il dump personalizzato ["+this.tipoDumpOpenSPCoopAppender.get(i)+
							"] del contenuto applicativo presente nel messaggio ("+tipoMessaggio+") con identificativo di transazione ["+this.idTransazione+"]: "+e.getMessage(),e);
					try{
						this.msgDiagErroreDump.addKeyword(CostantiPdD.KEY_TRACCIA_TIPO, tipoMessaggio.getValue());
						this.msgDiagErroreDump.addKeyword(CostantiPdD.KEY_TRACCIAMENTO_ERRORE,e.getMessage());
						this.msgDiagErroreDump.addKeyword(CostantiPdD.KEY_TRACCIAMENTO_PERSONALIZZATO,this.tipoDumpOpenSPCoopAppender.get(i));
						this.msgDiagErroreDump.logPersonalizzato("dumpContenutiApplicativi.registrazioneNonRiuscita.openspcoopAppender");
					}catch(Exception eMsg){}
					gestioneErroreDump(e);
				}
			}
		}

	}
	

	
	private void gestioneErroreDump(Throwable e) throws DumpException{
		
		if(this.properties.isDumpFallito_BloccoServiziPdD()){
			Dump.sistemaDumpDisponibile=false;
			Dump.motivoMalfunzionamentoDump=e;
			try{
				this.msgDiagErroreDump.logPersonalizzato("dumpContenutiApplicativi.errore.bloccoServizi");
			}catch(Exception eMsg){}
			OpenSPCoop2Logger.loggerOpenSPCoopResources.error("Il Sistema di dump dei contenuti applicativi ha rilevato un errore "+
					"durante la registrazione di un contenuto applicativo, tutti i servizi/moduli della porta di dominio sono sospesi."+
					" Si richiede un intervento sistemistico per la risoluzione del problema e il riavvio di GovWay. "+
					"Errore rilevato: ",e);
		}
		
		if(this.properties.isDumpFallito_BloccaCooperazioneInCorso()){
			throw new DumpException(e);
		}
	}
}

