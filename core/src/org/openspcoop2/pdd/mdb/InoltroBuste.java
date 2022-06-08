/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it). 
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



package org.openspcoop2.pdd.mdb;

import java.io.ByteArrayInputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPFault;

import org.openspcoop2.core.config.Connettore;
import org.openspcoop2.core.config.DumpConfigurazione;
import org.openspcoop2.core.config.GestioneErrore;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.Proprieta;
import org.openspcoop2.core.config.ResponseCachingConfigurazione;
import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.config.Trasformazioni;
import org.openspcoop2.core.config.ValidazioneContenutiApplicativi;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import org.openspcoop2.core.config.driver.DriverConfigurazioneNotFound;
import org.openspcoop2.core.constants.CostantiConnettori;
import org.openspcoop2.core.constants.TipoPdD;
import org.openspcoop2.core.eccezione.details.DettaglioEccezione;
import org.openspcoop2.core.eccezione.details.utils.XMLUtils;
import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDPortaDelegata;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.OpenSPCoop2MessageFactory;
import org.openspcoop2.message.OpenSPCoop2RestJsonMessage;
import org.openspcoop2.message.OpenSPCoop2RestMessage;
import org.openspcoop2.message.OpenSPCoop2RestXmlMessage;
import org.openspcoop2.message.OpenSPCoop2SoapMessage;
import org.openspcoop2.message.constants.MessageRole;
import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.message.exception.ParseException;
import org.openspcoop2.message.soap.SoapUtils;
import org.openspcoop2.message.soap.mtom.MtomXomReference;
import org.openspcoop2.message.utils.MessageUtilities;
import org.openspcoop2.pdd.config.ClassNameProperties;
import org.openspcoop2.pdd.config.ConfigurazionePdDManager;
import org.openspcoop2.pdd.config.ForwardProxy;
import org.openspcoop2.pdd.config.MTOMProcessorConfig;
import org.openspcoop2.pdd.config.MessageSecurityConfig;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.config.RichiestaDelegata;
import org.openspcoop2.pdd.config.dynamic.PddPluginLoader;
import org.openspcoop2.pdd.core.AbstractCore;
import org.openspcoop2.pdd.core.CostantiPdD;
import org.openspcoop2.pdd.core.EJBUtils;
import org.openspcoop2.pdd.core.GestoreCorrelazioneApplicativa;
import org.openspcoop2.pdd.core.GestoreMessaggi;
import org.openspcoop2.pdd.core.IntegrationContext;
import org.openspcoop2.pdd.core.MTOMProcessor;
import org.openspcoop2.pdd.core.PdDContext;
import org.openspcoop2.pdd.core.ProtocolContext;
import org.openspcoop2.pdd.core.ValidatoreMessaggiApplicativi;
import org.openspcoop2.pdd.core.ValidatoreMessaggiApplicativiException;
import org.openspcoop2.pdd.core.ValidatoreMessaggiApplicativiRest;
import org.openspcoop2.pdd.core.connettori.ConnettoreBase;
import org.openspcoop2.pdd.core.connettori.ConnettoreBaseHTTP;
import org.openspcoop2.pdd.core.connettori.ConnettoreMsg;
import org.openspcoop2.pdd.core.connettori.ConnettoreUtils;
import org.openspcoop2.pdd.core.connettori.GestoreErroreConnettore;
import org.openspcoop2.pdd.core.connettori.IConnettore;
import org.openspcoop2.pdd.core.connettori.InfoConnettoreUscita;
import org.openspcoop2.pdd.core.connettori.RepositoryConnettori;
import org.openspcoop2.pdd.core.handlers.GestoreHandlers;
import org.openspcoop2.pdd.core.handlers.HandlerException;
import org.openspcoop2.pdd.core.handlers.InResponseContext;
import org.openspcoop2.pdd.core.handlers.OutRequestContext;
import org.openspcoop2.pdd.core.integrazione.HeaderIntegrazione;
import org.openspcoop2.pdd.core.integrazione.IGestoreIntegrazionePD;
import org.openspcoop2.pdd.core.integrazione.IGestoreIntegrazionePDSoap;
import org.openspcoop2.pdd.core.integrazione.InResponsePDMessage;
import org.openspcoop2.pdd.core.integrazione.OutRequestPDMessage;
import org.openspcoop2.pdd.core.integrazione.UtilitiesIntegrazione;
import org.openspcoop2.pdd.core.state.IOpenSPCoopState;
import org.openspcoop2.pdd.core.state.OpenSPCoopState;
import org.openspcoop2.pdd.core.state.OpenSPCoopStateException;
import org.openspcoop2.pdd.core.state.OpenSPCoopStateful;
import org.openspcoop2.pdd.core.state.OpenSPCoopStateless;
import org.openspcoop2.pdd.core.token.TokenForward;
import org.openspcoop2.pdd.core.transazioni.Transaction;
import org.openspcoop2.pdd.core.transazioni.TransactionContext;
import org.openspcoop2.pdd.core.trasformazioni.GestoreTrasformazioni;
import org.openspcoop2.pdd.core.trasformazioni.GestoreTrasformazioniException;
import org.openspcoop2.pdd.logger.Dump;
import org.openspcoop2.pdd.logger.MsgDiagnosticiProperties;
import org.openspcoop2.pdd.logger.MsgDiagnostico;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.pdd.logger.Tracciamento;
import org.openspcoop2.pdd.services.DirectVMProtocolInfo;
import org.openspcoop2.pdd.services.ServicesUtils;
import org.openspcoop2.pdd.services.error.AbstractErrorGenerator;
import org.openspcoop2.pdd.services.error.RicezioneBusteExternalErrorGenerator;
import org.openspcoop2.pdd.services.error.RicezioneContenutiApplicativiInternalErrorGenerator;
import org.openspcoop2.pdd.timers.TimerGestoreMessaggi;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.engine.RequestInfo;
import org.openspcoop2.protocol.engine.constants.Costanti;
import org.openspcoop2.protocol.engine.driver.RepositoryBuste;
import org.openspcoop2.protocol.engine.validator.Validatore;
import org.openspcoop2.protocol.registry.RegistroServiziManager;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.BustaRawContent;
import org.openspcoop2.protocol.sdk.Eccezione;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.Integrazione;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.ProtocolMessage;
import org.openspcoop2.protocol.sdk.SecurityInfo;
import org.openspcoop2.protocol.sdk.Trasmissione;
import org.openspcoop2.protocol.sdk.builder.ProprietaErroreApplicativo;
import org.openspcoop2.protocol.sdk.builder.ProprietaManifestAttachments;
import org.openspcoop2.protocol.sdk.config.IProtocolManager;
import org.openspcoop2.protocol.sdk.config.IProtocolVersionManager;
import org.openspcoop2.protocol.sdk.constants.CodiceErroreCooperazione;
import org.openspcoop2.protocol.sdk.constants.CodiceErroreIntegrazione;
import org.openspcoop2.protocol.sdk.constants.ErroreIntegrazione;
import org.openspcoop2.protocol.sdk.constants.ErroriCooperazione;
import org.openspcoop2.protocol.sdk.constants.ErroriIntegrazione;
import org.openspcoop2.protocol.sdk.constants.FaseImbustamento;
import org.openspcoop2.protocol.sdk.constants.FaseSbustamento;
import org.openspcoop2.protocol.sdk.constants.Inoltro;
import org.openspcoop2.protocol.sdk.constants.IntegrationFunctionError;
import org.openspcoop2.protocol.sdk.constants.MessaggiFaultErroreCooperazione;
import org.openspcoop2.protocol.sdk.constants.RuoloMessaggio;
import org.openspcoop2.protocol.sdk.state.StatelessMessage;
import org.openspcoop2.protocol.sdk.tracciamento.EsitoElaborazioneMessaggioTracciato;
import org.openspcoop2.protocol.sdk.validator.IValidatoreErrori;
import org.openspcoop2.protocol.sdk.validator.IValidazioneSemantica;
import org.openspcoop2.protocol.sdk.validator.ProprietaValidazione;
import org.openspcoop2.protocol.sdk.validator.ProprietaValidazioneErrori;
import org.openspcoop2.security.message.MessageSecurityContext;
import org.openspcoop2.security.message.MessageSecurityContextParameters;
import org.openspcoop2.security.message.constants.SecurityConstants;
import org.openspcoop2.security.message.engine.MessageSecurityFactory;
import org.openspcoop2.utils.LimitExceededIOException;
import org.openspcoop2.utils.LimitedInputStream;
import org.openspcoop2.utils.TimeoutIOException;
import org.openspcoop2.utils.TimeoutInputStream;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.digest.IDigestReader;
import org.openspcoop2.utils.io.notifier.NotifierInputStreamParams;
import org.openspcoop2.utils.rest.problem.JsonDeserializer;
import org.openspcoop2.utils.rest.problem.ProblemRFC7807;
import org.openspcoop2.utils.rest.problem.XmlDeserializer;
import org.openspcoop2.utils.transport.TransportResponseContext;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.slf4j.Logger;


/**
 * Contiene la libreria InoltroBuste
 *
 * @author Poli Andrea (apoli@link.it)
 * @author Tronci Fabio (tronci@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class InoltroBuste extends GenericLib{

	public final static String ID_MODULO = "InoltroBuste";

	/** XMLBuilder */
	protected RicezioneContenutiApplicativiInternalErrorGenerator generatoreErrore;

	
	
	
	
	public InoltroBuste(Logger log) throws GenericLibException {
		super(InoltroBuste.ID_MODULO,log);
		inizializza();
	}

	@Override
	protected synchronized void inizializza() throws GenericLibException {	
		super.inizializza();
		InoltroBuste.initializeService(ClassNameProperties.getInstance(), this.propertiesReader);
	}
	
	
	/** IGestoreIntegrazionePA: lista di gestori, ordinati per priorita' minore */
	public static String[] defaultGestoriIntegrazionePD = null;
	private static java.util.concurrent.ConcurrentHashMap<String, String[]> defaultPerProtocolloGestoreIntegrazionePD = null;

	/** Indicazione se sono state inizializzate le variabili del servizio */
	public static boolean initializeService = false;

	/**
	 * Inizializzatore del servizio InoltroBuste
	 * 
	 * @throws Exception
	 */
	public synchronized static void initializeService(ClassNameProperties className,OpenSPCoop2Properties propertiesReader) {

		if(InoltroBuste.initializeService)
			return;

		boolean error = false;
		Logger logCore = OpenSPCoop2Logger.getLoggerOpenSPCoopCore();
		PddPluginLoader pluginLoader = PddPluginLoader.getInstance();
		
		// Inizializzo IGestoreIntegrazionePD list
		InoltroBuste.defaultGestoriIntegrazionePD = propertiesReader.getTipoIntegrazionePD();
		for(int i=0; i<InoltroBuste.defaultGestoriIntegrazionePD.length; i++){
			try{
				IGestoreIntegrazionePD gestore = pluginLoader.newIntegrazionePortaDelegata(InoltroBuste.defaultGestoriIntegrazionePD[i]);
				gestore.toString();
				logCore.info("Inizializzazione gestore dati di integrazione per le fruizioni di tipo "+
						InoltroBuste.defaultGestoriIntegrazionePD[i]+" effettuata.");
			}catch(Exception e){
				logCore.error(e.getMessage(),e);
				error = true;
			}
		}
		
		// Inizializzo IGestoreIntegrazionePD per protocollo
		InoltroBuste.defaultPerProtocolloGestoreIntegrazionePD = new java.util.concurrent.ConcurrentHashMap<String, String[]>();
		try{
			Enumeration<String> enumProtocols = ProtocolFactoryManager.getInstance().getProtocolNames();
			while (enumProtocols.hasMoreElements()) {
				String protocol = (String) enumProtocols.nextElement();
				String[]tipiIntegrazionePD = propertiesReader.getTipoIntegrazionePD(protocol);
				if(tipiIntegrazionePD!=null && tipiIntegrazionePD.length>0){
					List<String> tipiIntegrazionePerProtocollo = new ArrayList<String>();
					for (int i = 0; i < tipiIntegrazionePD.length; i++) {
						try {
							IGestoreIntegrazionePD gestore = pluginLoader.newIntegrazionePortaDelegata(tipiIntegrazionePD[i]);
							gestore.toString();
							tipiIntegrazionePerProtocollo.add(tipiIntegrazionePD[i]);
							logCore.info("Inizializzazione gestore dati di integrazione (protocollo: "+protocol+") per le fruizioni di tipo "
									+ tipiIntegrazionePD[i]	+ " effettuata.");
						} catch (Exception e) {
							logCore.error(e.getMessage(),e);
							error = true;
						}
					}
					if(tipiIntegrazionePerProtocollo.size()>0){
						InoltroBuste.defaultPerProtocolloGestoreIntegrazionePD.put(protocol, tipiIntegrazionePerProtocollo.toArray(new String[1]));
					}
				}
			}
		}catch(Exception e){
			logCore.error(
					"Riscontrato errore durante l'inizializzazione dei tipi di integrazione per protocollo: "+e.getMessage(),e);
			error = true;
		}
		

		InoltroBuste.initializeService = !error;
	}
		
		
		
		
		


	@Override
	public EsitoLib _onMessage(IOpenSPCoopState openspcoopstate,
			RegistroServiziManager registroServiziManager,ConfigurazionePdDManager configurazionePdDManager, 
			MsgDiagnostico msgDiag) throws OpenSPCoopStateException {

		EsitoLib esito = new EsitoLib();
		InoltroBusteMessage inoltroBusteMsg = (InoltroBusteMessage) openspcoopstate.getMessageLib();
		
		/* PddContext */
		PdDContext pddContext = inoltroBusteMsg.getPddContext();
		String idTransazione = PdDContext.getValue(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE, pddContext);
						
		/* ID e tipo di implementazione PdD con cui interoperare */
		String idMessageRequest = openspcoopstate.getIDMessaggioSessione();
		String implementazionePdDDestinatario = inoltroBusteMsg.getImplementazionePdDSoggettoDestinatario();
		
		RichiestaDelegata richiestaDelegata = inoltroBusteMsg.getRichiestaDelegata();
		Busta bustaRichiesta = inoltroBusteMsg.getBusta();
		IDSoggetto identitaPdD = inoltroBusteMsg.getRichiestaDelegata().getDominio();
		String profiloGestione = richiestaDelegata.getProfiloGestione();
		msgDiag.setDominio(identitaPdD);  // imposto anche il dominio nel msgDiag
		msgDiag.setPrefixMsgPersonalizzati(MsgDiagnosticiProperties.MSG_DIAG_INOLTRO_BUSTE);
		msgDiag.addKeywords(bustaRichiesta, true);
		msgDiag.mediumDebug("Profilo di gestione ["+InoltroBuste.ID_MODULO+"] della busta: "+profiloGestione);
		
		IDPortaDelegata idPD = richiestaDelegata.getIdPortaDelegata();
		
		TipoPdD tipoPdD = TipoPdD.DELEGATA;
		if(idPD!=null) {
			msgDiag.updatePorta(tipoPdD, idPD.getNome());
		}
		
		Integrazione integrazione = new Integrazione();
		integrazione.setIdModuloInAttesa(richiestaDelegata.getIdModuloInAttesa());
		integrazione.setNomePorta(richiestaDelegata.getIdPortaDelegata().getNome());
		integrazione.setScenario(richiestaDelegata.getScenario());
		integrazione.setServizioApplicativo(richiestaDelegata.getServizioApplicativo());

		String servizioApplicativoFruitore = null;
		if(CostantiPdD.SERVIZIO_APPLICATIVO_ANONIMO.equals(richiestaDelegata.getServizioApplicativo())==false){
			servizioApplicativoFruitore = richiestaDelegata.getServizioApplicativo();
			msgDiag.addKeyword(CostantiPdD.KEY_SA_FRUITORE, servizioApplicativoFruitore);
		}else{
			msgDiag.addKeyword(CostantiPdD.KEY_SA_FRUITORE, CostantiPdD.SERVIZIO_APPLICATIVO_ANONIMO);
		}
		String idCorrelazioneApplicativa = richiestaDelegata.getIdCorrelazioneApplicativa();
		String idCorrelazioneApplicativaRisposta = null;
		msgDiag.setIdCorrelazioneApplicativa(idCorrelazioneApplicativa);
		msgDiag.addKeyword(CostantiPdD.KEY_ID_CORRELAZIONE_APPLICATIVA,idCorrelazioneApplicativa);
		
		// Aggiornamento Informazioni messaggio diagnostico
		msgDiag.setIdMessaggioRichiesta(idMessageRequest);
		msgDiag.setFruitore(richiestaDelegata.getIdSoggettoFruitore());
		msgDiag.setServizio(richiestaDelegata.getIdServizio());
		

		
		
		// VM ProtocolInfo (se siamo arrivati da un canale VM)
		if(pddContext!=null && bustaRichiesta!=null)
			DirectVMProtocolInfo.setInfoFromContext(pddContext, bustaRichiesta);
		

		/* ------------------ Inizializzo stato OpenSPCoop  --------------- */
		msgDiag.mediumDebug("Inizializzo stato per la gestione della richiesta...");
		openspcoopstate.initResource(identitaPdD, InoltroBuste.ID_MODULO, idTransazione);
		registroServiziManager.updateState(openspcoopstate.getStatoRichiesta(),openspcoopstate.getStatoRisposta());
		configurazionePdDManager.updateState(openspcoopstate.getStatoRichiesta(),openspcoopstate.getStatoRisposta());
		msgDiag.updateState(openspcoopstate.getStatoRichiesta(),openspcoopstate.getStatoRisposta());



		/* Protocol Factory */
		IProtocolFactory<?> protocolFactory = null;
		org.openspcoop2.protocol.sdk.config.ITraduttore traduttore = null;
		IProtocolVersionManager protocolManager = null;
		IValidazioneSemantica validazioneSemantica = null;
		try{
			protocolFactory = this.protocolFactoryManager.getProtocolFactoryByName((String) pddContext.getObject(org.openspcoop2.core.constants.Costanti.PROTOCOL_NAME));
			traduttore = protocolFactory.createTraduttore();
			protocolManager = protocolFactory.createProtocolVersionManager(inoltroBusteMsg.getRichiestaDelegata().getProfiloGestione());
			validazioneSemantica = protocolFactory.createValidazioneSemantica(openspcoopstate.getStatoRichiesta());
		}catch(Exception e){
			msgDiag.logErroreGenerico(e, "ProtocolFactory.instanziazione"); 
			openspcoopstate.releaseResource();
			esito.setEsitoInvocazione(false); 
			esito.setStatoInvocazioneErroreNonGestito(e);
			return esito;
		}
		
		// Transaction
		Transaction transactionNullable = null;
		try{
			transactionNullable = TransactionContext.getTransaction(idTransazione);
		}catch(Exception e){
			// La transazione potrebbe essere stata eliminata nelle comunicazioni stateful
//			msgDiag.logErroreGenerico(e, "getTransaction"); 
//			openspcoopstate.releaseResource();
//			esito.setEsitoInvocazione(false); 
//			esito.setStatoInvocazioneErroreNonGestito(e);
//			return esito;
		}
		
		msgDiag.setPddContext(pddContext, protocolFactory);	
		
		ProprietaValidazioneErrori pValidazioneErrori = new ProprietaValidazioneErrori();
		pValidazioneErrori.setIgnoraEccezioniNonGravi(protocolManager.isIgnoraEccezioniNonGravi());
		



		/* ------------------ Inizializzazione Contesto di gestione  --------------- */
		msgDiag.mediumDebug("Inizializzo contesto per la gestione...");

		// Check FunctionRouting  
		boolean functionAsRouter = false;
		msgDiag.mediumDebug("Esamina modalita' di ricezione (PdD/Router)...");
		boolean existsSoggetto = false;
		try{
			existsSoggetto = configurazionePdDManager.existsSoggetto(new IDSoggetto(bustaRichiesta.getTipoMittente(),bustaRichiesta.getMittente()));
		}catch(Exception e){
			msgDiag.logErroreGenerico(e, "existsSoggetto("+bustaRichiesta.getTipoMittente()+"/"+bustaRichiesta.getMittente()+")");  
			openspcoopstate.releaseResource();
			esito.setEsitoInvocazione(false);	
			return esito;
		}
		if(existsSoggetto==false){
			// La PdD non gestisce il soggetto mittente della busta.
			// Controllo adesso che sia abilitata la funzione di Router per la PdD, poiche'
			// mi dovrebbe essere arrivata per forza dal modulo di ricezione buste in modalita' Router.
			boolean routerFunctionActive = false;
			try{
				routerFunctionActive = configurazionePdDManager.routerFunctionActive();
			}catch(Exception e){
				msgDiag.logErroreGenerico(e,"routerFunctionActive()");
				openspcoopstate.releaseResource();
				esito.setEsitoInvocazione(false);	
				esito.setStatoInvocazioneErroreNonGestito(e);
				return esito;
			}
			if(routerFunctionActive){
				functionAsRouter = true;
			}else{
				msgDiag.logPersonalizzato("routingTable.soggettoFruitoreNonGestito");
				openspcoopstate.releaseResource();
				esito.setEsitoInvocazione(true);	
				esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,
						msgDiag.getMessaggio_replaceKeywords("routingTable.soggettoFruitoreNonGestito"));
				return esito;
			}
		}
		if(functionAsRouter){
			tipoPdD = TipoPdD.ROUTER;
		}

		
		//	GestoriMessaggio
		GestoreMessaggi msgRequest = new GestoreMessaggi(openspcoopstate, true, idMessageRequest,Costanti.OUTBOX,msgDiag, pddContext);
		OpenSPCoop2Message requestMessagePrimaTrasformazione = null;
		GestoreMessaggi msgResponse = null;
		
		
		// RequestInfo
		RequestInfo requestInfo = (RequestInfo) pddContext.getObject(org.openspcoop2.core.constants.Costanti.REQUEST_INFO);
		if(requestInfo==null || idTransazione==null) {
			// devo leggerlo dal messaggio
			try {
				requestMessagePrimaTrasformazione = msgRequest.getMessage();
				if(requestInfo==null) {
					Object o = requestMessagePrimaTrasformazione.getContextProperty(org.openspcoop2.core.constants.Costanti.REQUEST_INFO);
					if(o==null) {
						throw new Exception("RequestInfo non presente nel contesto");
					}
					requestInfo = (RequestInfo) o;
					pddContext.addObject(org.openspcoop2.core.constants.Costanti.REQUEST_INFO,requestInfo);
				}
				if(idTransazione==null) {
					Object o = requestMessagePrimaTrasformazione.getContextProperty(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE);
					if(o==null) {
						throw new Exception("IdTransazione non presente nel contesto");
					}
					idTransazione = (String) o;
					pddContext.addObject(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE,idTransazione);
				}
			}catch(Exception e) {
				msgDiag.logErroreGenerico(e, "LetturaMessaggioErrore (Recupero Dati)"); 
				openspcoopstate.releaseResource();
				esito.setEsitoInvocazione(false); 
				esito.setStatoInvocazioneErroreNonGestito(e);
				return esito;
			}
		}
		
		
		
		// ProprietaErroreApplicativo (No function Router active)
		ProprietaErroreApplicativo proprietaErroreAppl = null;
		if(functionAsRouter==false){
			proprietaErroreAppl = richiestaDelegata.getFault();
			proprietaErroreAppl.setIdModulo(InoltroBuste.ID_MODULO);
		}
		
		try{
			this.generatoreErrore = new RicezioneContenutiApplicativiInternalErrorGenerator(this.log, this.idModulo, requestInfo);
			this.generatoreErrore.updateInformazioniCooperazione(richiestaDelegata.getIdSoggettoFruitore(), richiestaDelegata.getIdServizio());
			this.generatoreErrore.updateInformazioniCooperazione(richiestaDelegata.getServizioApplicativo());
			if(proprietaErroreAppl!=null){
				this.generatoreErrore.updateProprietaErroreApplicativo(proprietaErroreAppl);
			}
			this.generatoreErrore.updateTipoPdD(tipoPdD);
		}catch(Exception e){
			msgDiag.logErroreGenerico(e, "RicezioneContenutiApplicativiGeneratoreErrore.instanziazione"); 
			openspcoopstate.releaseResource();
			esito.setEsitoInvocazione(false); 
			esito.setStatoInvocazioneErroreNonGestito(e);
			return esito;
		}
		
		
		
		
		
		
		
		
		/* ----------- Lettura PortaDelegata e Servizio Applicativo ------------- */
		PortaDelegata pd = null;
		ServizioApplicativo sa = null;
		if(functionAsRouter==false){
			try{
				pd = configurazionePdDManager.getPortaDelegata(richiestaDelegata.getIdPortaDelegata());
			}catch(Exception e){
				msgDiag.logErroreGenerico(e,"getPortaDelegata()");
				openspcoopstate.releaseResource();
				esito.setEsitoInvocazione(false);	
				esito.setStatoInvocazioneErroreNonGestito(e);
				return esito;
			}
			try{
				IDServizioApplicativo idSA = new IDServizioApplicativo();
				idSA.setNome(richiestaDelegata.getServizioApplicativo());
				idSA.setIdSoggettoProprietario(richiestaDelegata.getIdSoggettoFruitore());
				sa = configurazionePdDManager.getServizioApplicativo(idSA);
			}catch(Exception e){
				if( !(e instanceof DriverConfigurazioneNotFound) || 
						!(CostantiPdD.SERVIZIO_APPLICATIVO_ANONIMO.equals(richiestaDelegata.getServizioApplicativo())) ){
					msgDiag.logErroreGenerico(e,"getServizioApplicativo()");
					openspcoopstate.releaseResource();
					esito.setEsitoInvocazione(false);	
					esito.setStatoInvocazioneErroreNonGestito(e);
					return esito;
				}
			}
		}
		
		
		Utilities.printFreeMemory("InoltroBuste - Esamina contesto per la gestione...");
		
		msgDiag.mediumDebug("Esamina contesto per la gestione...");
		// Modalita' gestione risposta Applicativa (No function Router active) 
		// -a immediata se il profilo e' sincrono 
		// -b immediata se il profilo e' asincrono e ricevutaAsincrona e' abilitata.
		// -c immediata se il profilo e' asincrono asimmetrico di richiesta stato e ricevuta disabilitata
		// -d immediata se il profilo e' oneway e la modalita di trasmissione e' sincrona.
		// -e rollback negli altri casi
		boolean sendRispostaApplicativa = false;
		boolean statelessAsincrono = false;
		boolean richiestaAsincronaSimmetricaStateless = false;
		if(functionAsRouter==false){
			// a)
			if(Costanti.SCENARIO_SINCRONO_INVOCAZIONE_SERVIZIO.equals(richiestaDelegata.getScenario()) ){
				sendRispostaApplicativa = true; 
				integrazione.setStateless(true);
			}
			// c) [e b) per la richiestaStato AsincronaAsimmetrica]
			else if(Costanti.SCENARIO_ASINCRONO_ASIMMETRICO_POLLING.equals(richiestaDelegata.getScenario())){
				try{
					statelessAsincrono = configurazionePdDManager.isModalitaStateless(pd, bustaRichiesta.getProfiloDiCollaborazione());
				}catch(Exception e){
					msgDiag.logErroreGenerico(e,"AsincronoAsimmetricoPolling.isModalitaStateless(pd)");
					openspcoopstate.releaseResource();      
					esito.setEsitoInvocazione(false);	
					esito.setStatoInvocazioneErroreNonGestito(e);
					return esito;
				}
				sendRispostaApplicativa = true; 
				integrazione.setStateless(statelessAsincrono);
			}
			// b)
			else if (Costanti.SCENARIO_ASINCRONO_ASIMMETRICO_INVOCAZIONE_SERVIZIO.equals(richiestaDelegata.getScenario()) ||
					Costanti.SCENARIO_ASINCRONO_SIMMETRICO_INVOCAZIONE_SERVIZIO.equals(richiestaDelegata.getScenario()) ||
					Costanti.SCENARIO_ASINCRONO_SIMMETRICO_CONSEGNA_RISPOSTA.equals(richiestaDelegata.getScenario()) ){
				try{
					statelessAsincrono = configurazionePdDManager.isModalitaStateless(pd, bustaRichiesta.getProfiloDiCollaborazione());
					sendRispostaApplicativa =  statelessAsincrono || richiestaDelegata.isRicevutaAsincrona();
					richiestaAsincronaSimmetricaStateless = Costanti.SCENARIO_ASINCRONO_SIMMETRICO_INVOCAZIONE_SERVIZIO.equals(richiestaDelegata.getScenario())
						&& (openspcoopstate instanceof OpenSPCoopStateless);
				}catch(Exception e){
					msgDiag.logErroreGenerico(e,"Asincrono.isModalitaStateless(pd)");
					openspcoopstate.releaseResource();      
					esito.setEsitoInvocazione(false);	
					esito.setStatoInvocazioneErroreNonGestito(e);
					return esito;
				}	
				integrazione.setStateless(statelessAsincrono);
			}
			// d)
			else if(Costanti.SCENARIO_ONEWAY_INVOCAZIONE_SERVIZIO.equals(richiestaDelegata.getScenario())){
				try{
					sendRispostaApplicativa = configurazionePdDManager.isModalitaStateless(pd, bustaRichiesta.getProfiloDiCollaborazione());
				}catch(Exception e){
					msgDiag.logErroreGenerico(e,"OnewayInvocazioneServizio.isModalitaStateless(pd)");
					openspcoopstate.releaseResource();      
					esito.setEsitoInvocazione(false);	
					esito.setStatoInvocazioneErroreNonGestito(e);
					return esito;
				}	
				integrazione.setStateless(sendRispostaApplicativa);
			}
		}
		
		pddContext.addObject(org.openspcoop2.core.constants.Costanti.STATELESS, integrazione.isStateless()+"");

		// EJBUtils (per eventuali errori)
		EJBUtils ejbUtils = null;
		try{
			ejbUtils = new EJBUtils(identitaPdD,tipoPdD,InoltroBuste.ID_MODULO,idMessageRequest,
					idMessageRequest,Costanti.OUTBOX,openspcoopstate, msgDiag,functionAsRouter,
					inoltroBusteMsg.getImplementazionePdDSoggettoMittente(),
					inoltroBusteMsg.getImplementazionePdDSoggettoDestinatario(),
					profiloGestione,pddContext);
		}catch(Exception e){
			msgDiag.logErroreGenerico(e, "EJBUtils.new");  
			openspcoopstate.releaseResource();
			esito.setEsitoInvocazione(false);	return esito;
		}
		ejbUtils.setRouting(functionAsRouter);
		
		if(functionAsRouter){
			try{
				RicezioneBusteExternalErrorGenerator generatoreErrorePA = new RicezioneBusteExternalErrorGenerator(this.log, this.idModulo, requestInfo, openspcoopstate.getStatoRichiesta());
				generatoreErrorePA.updateInformazioniCooperazione(richiestaDelegata.getIdSoggettoFruitore(), richiestaDelegata.getIdServizio());
				generatoreErrorePA.updateTipoPdD(TipoPdD.ROUTER);
				ejbUtils.setGeneratoreErrorePortaApplicativa(generatoreErrorePA);
			}catch(Exception e){
				msgDiag.logErroreGenerico(e, "RicezioneBusteExternalErrorGenerator.instanziazione"); 
				openspcoopstate.releaseResource();
				esito.setEsitoInvocazione(false); 
				esito.setStatoInvocazioneErroreNonGestito(e);
				return esito;
			}
		}

		
		// Oneway versione 11
		boolean oneWayVersione11 = inoltroBusteMsg.isOneWayVersione11();
		ejbUtils.setOneWayVersione11(oneWayVersione11);
		msgRequest.setOneWayVersione11(oneWayVersione11);
		
		// ResponseCaching 
		ResponseCachingConfigurazione responseCachingConfig = null;
		if(functionAsRouter==false){
			try{		
				responseCachingConfig = configurazionePdDManager.getConfigurazioneResponseCaching(pd);	
			}catch(Exception e){
				msgDiag.logErroreGenerico(e, "getConfigurazioneResponseCaching(pd)");
				ejbUtils.rollbackMessage("Errore nella lettura della configurazione per il salvataggio della risposta in cache", esito);
				openspcoopstate.releaseResource();
				esito.setEsitoInvocazione(false);	
				esito.setStatoInvocazioneErroreNonGestito(e);
				return esito;
			}
		}
		
		// Trasformazioni
		Trasformazioni trasformazioni = null;
		if(functionAsRouter==false){
			try {
				trasformazioni = configurazionePdDManager.getTrasformazioni(pd);
			}catch(Exception e){
				msgDiag.logErroreGenerico(e, "getTrasformazioni(pd)");
				ejbUtils.rollbackMessage("Errore nella lettura della configurazione delle trasformazioni", esito);
				openspcoopstate.releaseResource();
				esito.setEsitoInvocazione(false);	
				esito.setStatoInvocazioneErroreNonGestito(e);
				return esito;
			}
		}
		
		//Identificazione del tipo di porta
		boolean portaDiTipoStateless= false;
		boolean routingStateless = false;
		if(functionAsRouter==false){
			try{		
				portaDiTipoStateless = configurazionePdDManager.isModalitaStateless(pd, bustaRichiesta.getProfiloDiCollaborazione());		
			}catch(Exception e){
				msgDiag.logErroreGenerico(e, "isModalitaStateless("+bustaRichiesta.getProfiloDiCollaborazione()+")");
				ejbUtils.rollbackMessage("Errore nella creazione dei gestori messaggio", esito);
				openspcoopstate.releaseResource();
				esito.setEsitoInvocazione(false);	
				esito.setStatoInvocazioneErroreNonGestito(e);
				return esito;
			}
		}else{
			routingStateless = CostantiConfigurazione.ABILITATO.equals(this.propertiesReader.getStatelessRouting());
			ejbUtils.setRollbackRichiestaInCasoErrore((routingStateless==false));
		}
		msgRequest.setPortaDiTipoStateless(portaDiTipoStateless);
		msgRequest.setRoutingStateless(routingStateless);

		boolean sbustamentoInformazioniProtocolloRisposta = false;
		if(functionAsRouter==false){
			try{		
				sbustamentoInformazioniProtocolloRisposta = configurazionePdDManager.invocazionePortaDelegataSbustamentoInformazioniProtocollo(sa);
			}catch(Exception e){
				msgDiag.logErroreGenerico(e, "isGestioneManifestAttachments(pd)");
				ejbUtils.rollbackMessage("Errore durante l'invocazione del metodo isGestioneManifestAttachments", esito);
				openspcoopstate.releaseResource();
				esito.setEsitoInvocazione(false);	
				esito.setStatoInvocazioneErroreNonGestito(e);
				return esito;
			}
		}
		
		boolean isBlockedTransaction_responseMessageWithTransportCodeError = false;
		try{		
			isBlockedTransaction_responseMessageWithTransportCodeError = 
						protocolManager.isBlockedTransaction_responseMessageWithTransportCodeError();
		}catch(Exception e){
			msgDiag.logErroreGenerico(e, "isBlockedTransaction_responseMessageWithTransportCodeError)");
			ejbUtils.rollbackMessage("Errore durante l'invocazione del metodo isBlockedTransaction_responseMessageWithTransportCodeError", esito);
			openspcoopstate.releaseResource();
			esito.setEsitoInvocazione(false);	
			esito.setStatoInvocazioneErroreNonGestito(e);
			return esito;
		}
		
		



		
		
		
		try{
			if(msgRequest.isRiconsegnaMessaggio(null) == false){
				openspcoopstate.releaseResource();
				// Per i profili 'sincroni' dove vi e' un modulo in attesa non puo' sussistere una riconsegna del messaggio.
				if(sendRispostaApplicativa==false){
					msgDiag.logPersonalizzato("riconsegnaMessaggioPrematura");
					esito.setEsitoInvocazione(false);
					esito.setStatoInvocazione(EsitoLib.ERRORE_NON_GESTITO,msgDiag.getMessaggio_replaceKeywords("riconsegnaMessaggioPrematura"));
				}else{
					String message = null;
					String posizione = null;
					if(Costanti.SCENARIO_ONEWAY_INVOCAZIONE_SERVIZIO.equals(richiestaDelegata.getScenario())){
						message = "Messaggio eliminato durante il controllo di ri-consegna ("+richiestaDelegata.getScenario()+")";
						posizione = "msgRequest.isRiconsegnaMessaggio("+richiestaDelegata.getScenario()+")";
					}else{
						message = "Messaggio eliminato durante il controllo di ri-consegna ("+richiestaDelegata.getScenario()+",STATELESS)";
						posizione = "msgRequest.isRiconsegnaMessaggio("+richiestaDelegata.getScenario()+",STATELESS)";
					}
					msgDiag.logErroreGenerico(message,posizione);
					esito.setEsitoInvocazione(true);
					esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,message);
				}
				return esito;
			}
		}catch(Exception e){
			msgDiag.logErroreGenerico(e, "msgRequest.isRiconsegnaMessaggio()");
			ejbUtils.rollbackMessage("Errore verifica riconsegna messaggio", esito);
			openspcoopstate.releaseResource();
			esito.setEsitoInvocazione(false);	
			esito.setStatoInvocazioneErroreNonGestito(e);
			return esito;
		}

		// Gestore Funzionalita'
		RepositoryBuste repositoryBuste = new RepositoryBuste(openspcoopstate.getStatoRichiesta(), true, protocolFactory);
	
		// Tracciamento
		org.openspcoop2.pdd.logger.Tracciamento tracciamento;
		try {
			tracciamento = new org.openspcoop2.pdd.logger.Tracciamento(identitaPdD,InoltroBuste.ID_MODULO,pddContext,tipoPdD,msgDiag.getPorta(),
					openspcoopstate.getStatoRichiesta(),openspcoopstate.getStatoRisposta());
		} catch (Exception e) {
			msgDiag.logErroreGenerico(e, "ProtocolFactory.instanziazione Imbustamento/Tracciamento"); 
			openspcoopstate.releaseResource();
			esito.setEsitoInvocazione(false); 
			esito.setStatoInvocazioneErroreNonGestito(e);
			return esito;
		}
		// rollback Richiesta non deve essere effettuato se ho una risposta/richiesta stato asincrona simmetrica/asimmetrica
		boolean rollbackRichiesta = true;
		if(sendRispostaApplicativa){
			if(Costanti.SCENARIO_ASINCRONO_ASIMMETRICO_POLLING.equals(richiestaDelegata.getScenario()) ||
					Costanti.SCENARIO_ASINCRONO_SIMMETRICO_CONSEGNA_RISPOSTA.equals(richiestaDelegata.getScenario())){
				rollbackRichiesta = false;
			}
		}

		boolean consegnaAffidabile = false;
		switch (protocolManager.getConsegnaAffidabile(bustaRichiesta)) {
		case ABILITATA:
			consegnaAffidabile = true;
			break;
		case DISABILITATA:
			consegnaAffidabile = false;
			break;
		default:
			consegnaAffidabile = this.propertiesReader.isGestioneRiscontri(implementazionePdDDestinatario) && bustaRichiesta.isConfermaRicezione();
			break;
		}
		
		// Gestione riscontri per profilo oneway (No function Router active)
		boolean gestioneBusteNonRiscontrateAttive = false;
		if(functionAsRouter==false){
			if(Costanti.SCENARIO_ONEWAY_INVOCAZIONE_SERVIZIO.equals(richiestaDelegata.getScenario()) && (sendRispostaApplicativa==false) ){
				gestioneBusteNonRiscontrateAttive =consegnaAffidabile;
			}else if (Costanti.SCENARIO_ASINCRONO_ASIMMETRICO_INVOCAZIONE_SERVIZIO.equals(richiestaDelegata.getScenario()) ||
					Costanti.SCENARIO_ASINCRONO_SIMMETRICO_INVOCAZIONE_SERVIZIO.equals(richiestaDelegata.getScenario()) ||
					Costanti.SCENARIO_ASINCRONO_SIMMETRICO_CONSEGNA_RISPOSTA.equals(richiestaDelegata.getScenario()) ){
				if(richiestaDelegata.isRicevutaAsincrona()==false)
					gestioneBusteNonRiscontrateAttive = true;
			}
		}

		// Enrich SOAPFault
		boolean enrichSoapFaultApplicativo = this.propertiesReader.isAggiungiDetailErroreApplicativo_SoapFaultApplicativo();
		boolean enrichSoapFaultPdD = this.propertiesReader.isAggiungiDetailErroreApplicativo_SoapFaultPdD();
		if(proprietaErroreAppl!=null){
			enrichSoapFaultApplicativo = proprietaErroreAppl.isAggiungiDetailErroreApplicativo_SoapFaultApplicativo();
			enrichSoapFaultPdD = proprietaErroreAppl.isAggiungiDetailErroreApplicativo_SoapFaultPdD();
		}
		
		// Modalita' gestione risposta (Sia per PdD normale che per Router)
		// - connectionReply per profilo sincrono
		// - parametro configurabile per altri profili
		boolean newConnectionForResponse = false; 
		if(functionAsRouter){
			if(org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.SINCRONO.equals(bustaRichiesta.getProfiloDiCollaborazione())==false
					&& (portaDiTipoStateless==false) && (routingStateless==false) ){
				// New connection puo' essere usata solo se non siamo in modalita stateless){
				newConnectionForResponse = configurazionePdDManager.newConnectionForResponse();
			}
		}
		
		boolean utilizzoIndirizzoTelematico = false;
		if( (portaDiTipoStateless==false) && (routingStateless==false)){
			// New connection puo' essere usata solo se non siamo in modalita stateless
			 utilizzoIndirizzoTelematico = configurazionePdDManager.isUtilizzoIndirizzoTelematico();
		}

		// Aggiornamento EJBUtils
		ejbUtils.setReplyOnNewConnection(newConnectionForResponse);
		ejbUtils.setUtilizzoIndirizzoTelematico(utilizzoIndirizzoTelematico);
		ejbUtils.setScenarioCooperazione(richiestaDelegata.getScenario());

		// Identita' errore
		if(functionAsRouter == false){
			msgDiag.setServizioApplicativo(richiestaDelegata.getServizioApplicativo());
		}

		// Proprieta Manifest Attachments
		ProprietaManifestAttachments proprietaManifestAttachments = 
			this.propertiesReader.getProprietaManifestAttachments(implementazionePdDDestinatario);
		
		// Read QualifiedAttribute
		boolean readQualifiedAttribute = this.propertiesReader.isReadQualifiedAttribute(implementazionePdDDestinatario);

		// Validazione id completa
		boolean validazioneIDBustaCompleta = this.propertiesReader.isValidazioneIDBustaCompleta(implementazionePdDDestinatario);

		
		// Esamina informazioni
		IDSoggetto soggettoFruitore = richiestaDelegata.getIdSoggettoFruitore();
		IDServizio idServizio = richiestaDelegata.getIdServizio();
		IDAccordo idAccordoServizio = richiestaDelegata.getIdAccordo();
		ForwardProxy forwardProxy = null;
		if(functionAsRouter==false && configurazionePdDManager.isForwardProxyEnabled()) {
			try {
				forwardProxy = configurazionePdDManager.getForwardProxyConfigFruizione(identitaPdD, idServizio, null);
			}catch(Exception e) {
				msgDiag.logErroreGenerico(e, "Configurazione del connettore errata per la funzionalità govway-proxy"); 
				openspcoopstate.releaseResource();
				esito.setEsitoInvocazione(false); 
				esito.setStatoInvocazioneErroreNonGestito(e);
				return esito;
			}
		}
		
		

		// Risposta
		OpenSPCoop2Message responseMessage = null;
		

		// Punto di inizio per la transazione.
		Validatore validatore = null;
		BustaRawContent<?> headerProtocolloRisposta = null;
		IConnettore connectorSenderForDisconnect = null;
		String location = "";
		boolean useResponseForParseException = false;
		try{




			Utilities.printFreeMemory("InoltroBuste - Controllo scadenza busta...");

			/* ------------  Controllo Scadenza Busta  ------------- */
			if(bustaRichiesta.getScadenza() != null){
				msgDiag.mediumDebug("Controllo scadenza busta...");

				Timestamp now = DateManager.getTimestamp();
				if (bustaRichiesta.getScadenza().before(now)) {
					// Busta scaduta
					Eccezione ecc = Eccezione.getEccezioneValidazione(ErroriCooperazione.MESSAGGIO_SCADUTO.getErroreCooperazione(), protocolFactory);
					msgDiag.logPersonalizzato("inoltroBustaScaduta");
					if(functionAsRouter){
						ejbUtils.sendAsRispostaBustaErroreValidazione(richiestaDelegata.getIdModuloInAttesa(),bustaRichiesta,ecc,
								idCorrelazioneApplicativa,idCorrelazioneApplicativaRisposta,servizioApplicativoFruitore);
					}else{
						if(sendRispostaApplicativa){
							OpenSPCoop2Message responseMessageError = 
									this.generatoreErrore.build(pddContext, IntegrationFunctionError.INTERNAL_REQUEST_ERROR,
											ecc,richiestaDelegata.getIdSoggettoFruitore(),null);
							ejbUtils.sendRispostaApplicativaErrore(responseMessageError,richiestaDelegata,rollbackRichiesta,pd,sa);	
						}else{
							ejbUtils.releaseOutboxMessage(true);
						}
					}
					openspcoopstate.releaseResource();
					esito.setEsitoInvocazione(true);	
					esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,
							msgDiag.getMessaggio_replaceKeywords("inoltroBustaScaduta"));
					return esito;
				}
			}









			Utilities.printFreeMemory("InoltroBuste - Routing... ");




			/* ------------------ Routing --------------- */
			msgDiag.logPersonalizzato("routingTable.esaminaInCorso");
			
			// ConnectorProperties (Punto di accesso della porta di identitaPdD.getCodicePorta() a cui spedire la busta)
			Connettore connettore = null;
			String erroreRicercaConnettore = null;
			Exception eForwardRoute = null;
			if(functionAsRouter==false){
				try{
					IProtocolManager pm = protocolFactory.createProtocolManager();
					if(pm.isStaticRoute()) {
						org.openspcoop2.core.registry.Connettore connettoreProtocol = 
								pm.getStaticRoute(soggettoFruitore,idServizio,
										protocolFactory.getCachedRegistryReader(openspcoopstate.getStatoRichiesta()));
						if(connettoreProtocol!=null) {
							connettore = connettoreProtocol.mappingIntoConnettoreConfigurazione();
						}
					}
				}catch(Exception e){
					eForwardRoute = e;
					erroreRicercaConnettore = e.getMessage();
				}
			}
			if(connettore==null && eForwardRoute==null){ // in pratica se non e' stato trovato un connettore via protocol
				try{
					connettore = configurazionePdDManager.getForwardRoute(soggettoFruitore,idServizio,functionAsRouter);
				}catch(Exception e){
					eForwardRoute = e;
					erroreRicercaConnettore = e.getMessage();
				}
				if(functionAsRouter){
					if(connettore==null){
						try{
							connettore = configurazionePdDManager.getForwardRoute(idServizio.getSoggettoErogatore(),functionAsRouter);
						}catch(Exception e){
							eForwardRoute = e;
							erroreRicercaConnettore = erroreRicercaConnettore+ "\nRicerca in base al solo soggetto destinatario:\n"+ e.getMessage();
						}
					}
				}
			}
			if (connettore == null) {
				if(erroreRicercaConnettore!=null){
					msgDiag.addKeyword(CostantiPdD.KEY_ERRORE_PROCESSAMENTO, erroreRicercaConnettore);
					erroreRicercaConnettore = "Riscontrato errore durante la ricerca del connettore a cui inoltrare la busta: "+erroreRicercaConnettore;
				}else{
					msgDiag.addKeyword(CostantiPdD.KEY_ERRORE_PROCESSAMENTO, "non definito");
					erroreRicercaConnettore = "Riscontrato errore durante la ricerca del connettore a cui inoltrare la busta: non definito";
				}
				msgDiag.logPersonalizzato("routingTable.esaminaInCorsoFallita");
				if(functionAsRouter){
					ejbUtils.sendAsRispostaBustaErroreProcessamento(richiestaDelegata.getIdModuloInAttesa(),bustaRichiesta,
							ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
								get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_514_ROUTING_CONFIGURATION_ERROR),
							idCorrelazioneApplicativa,idCorrelazioneApplicativaRisposta,servizioApplicativoFruitore,eForwardRoute,
							(requestMessagePrimaTrasformazione!=null ? requestMessagePrimaTrasformazione.getParseException() : null));
					esito.setEsitoInvocazione(true);
					esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,
							msgDiag.getMessaggio_replaceKeywords("routingTable.esaminaInCorsoFallita"));
				}else{
					if(sendRispostaApplicativa){
						OpenSPCoop2Message responseMessageError = 
								this.generatoreErrore.build(pddContext,IntegrationFunctionError.INTERNAL_REQUEST_ERROR,
										ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
										get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_514_ROUTING_CONFIGURATION_ERROR),eForwardRoute,
											(requestMessagePrimaTrasformazione!=null ? requestMessagePrimaTrasformazione.getParseException() : null));
						ejbUtils.sendRispostaApplicativaErrore(responseMessageError,richiestaDelegata,rollbackRichiesta,pd,sa);
						esito.setEsitoInvocazione(true);
						esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,
								msgDiag.getMessaggio_replaceKeywords("routingTable.esaminaInCorsoFallita"));
					}else{
						// Se Non e' attivo una gestione dei riscontri, faccio il rollback sulla coda.
						// Altrimenti verra attivato la gestione dei riscontri che riprovera' dopo un tot.
						if(gestioneBusteNonRiscontrateAttive==false){
							ejbUtils.rollbackMessage(erroreRicercaConnettore, esito);
							esito.setEsitoInvocazione(false);
							esito.setStatoInvocazione(EsitoLib.ERRORE_NON_GESTITO,
									msgDiag.getMessaggio_replaceKeywords("routingTable.esaminaInCorsoFallita"));
						}else{
							ejbUtils.updateErroreProcessamentoMessage(erroreRicercaConnettore, esito);
							esito.setEsitoInvocazione(true);
							esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,
									msgDiag.getMessaggio_replaceKeywords("routingTable.esaminaInCorsoFallita"));
						}
					}
				}
				openspcoopstate.releaseResource();
				return esito;
			}
			msgDiag.logPersonalizzato("routingTable.esaminaEffettuata");







			Utilities.printFreeMemory("InoltroBuste - Trasmissione...");







			/* ----------- Trasmissione ------------------ */
			String soggettoDestinatarioTrasmissione = "";
			Trasmissione tras = null;
			if(this.propertiesReader.isGenerazioneListaTrasmissioni(implementazionePdDDestinatario)){
				msgDiag.mediumDebug("Gestione trasmissione della busta ...");
				// Tracciamento in busta
				tras = new Trasmissione();
				
				// origine
				tras.setOrigine(identitaPdD.getNome());
				tras.setTipoOrigine(identitaPdD.getTipo());
				tras.setIdentificativoPortaOrigine(identitaPdD.getCodicePorta());
				
				// destinazione
				IDSoggetto destTrasm = null;
				if(connettore.getNomeDestinatarioTrasmissioneBusta()!=null && connettore.getTipoDestinatarioTrasmissioneBusta()!=null)
					destTrasm = new IDSoggetto(connettore.getTipoDestinatarioTrasmissioneBusta(),connettore.getNomeDestinatarioTrasmissioneBusta());
				else
					destTrasm = new IDSoggetto(bustaRichiesta.getTipoDestinatario(),
							bustaRichiesta.getDestinatario());	 
				tras.setDestinazione(destTrasm.getNome());
				tras.setTipoDestinazione(destTrasm.getTipo());
				try{
					String dominio = registroServiziManager.getDominio(destTrasm, null, protocolFactory);
					tras.setIdentificativoPortaDestinazione(dominio);
				}catch(Exception e){}
				
				// oraRegistrazione
				tras.setOraRegistrazione(bustaRichiesta.getOraRegistrazione());
				tras.setTempo(this.propertiesReader.getTipoTempoBusta(implementazionePdDDestinatario));
				
				bustaRichiesta.addTrasmissione(tras);
				// net hop is Router?	
				if( (idServizio.getSoggettoErogatore().getNome().equals(destTrasm.getNome())==false) ||
						(idServizio.getSoggettoErogatore().getTipo().equals(destTrasm.getTipo())==false)	)
					soggettoDestinatarioTrasmissione = " (tramite router "+destTrasm.getTipo()+"/"+destTrasm.getNome()+")";
			}
			msgDiag.addKeyword(CostantiPdD.KEY_DESTINATARIO_TRASMISSIONE, soggettoDestinatarioTrasmissione);







			Utilities.printFreeMemory("InoltroBuste - Lettura messaggio soap della richiesta da spedire...");







			/* ------------  Ricostruzione Messaggio Soap da spedire ------------- */	
			msgDiag.mediumDebug("Lettura messaggio soap della richiesta da spedire...");
			TransportResponseContext transportResponseContext = null;
			try{
				if(requestMessagePrimaTrasformazione==null) {
					requestMessagePrimaTrasformazione = msgRequest.getMessage();
				}
			}catch(Exception e){
				msgDiag.logErroreGenerico(e, "msgRequest.getMessage()");
				if(functionAsRouter){
					ejbUtils.sendAsRispostaBustaErroreProcessamento(richiestaDelegata.getIdModuloInAttesa(),bustaRichiesta,
							ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
								get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_509_READ_REQUEST_MSG),
							idCorrelazioneApplicativa,idCorrelazioneApplicativaRisposta,servizioApplicativoFruitore,e,
							(requestMessagePrimaTrasformazione!=null ? requestMessagePrimaTrasformazione.getParseException() : null));
					esito.setEsitoInvocazione(true);
					esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,"msgRequest.getMessage()");
				}else{
					if(sendRispostaApplicativa){
						OpenSPCoop2Message responseMessageError = 
								this.generatoreErrore.build(pddContext,IntegrationFunctionError.INTERNAL_REQUEST_ERROR,
										ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
											get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_509_READ_REQUEST_MSG),e,
												(requestMessagePrimaTrasformazione!=null ? requestMessagePrimaTrasformazione.getParseException() : null));
						ejbUtils.sendRispostaApplicativaErrore(responseMessageError,richiestaDelegata,rollbackRichiesta,pd,sa);
						esito.setEsitoInvocazione(true);
						esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,"msgRequest.getMessage()");
					}else{
						// Se Non e' attivo una gestione dei riscontri, faccio il rollback sulla coda.
						// Altrimenti verra attivato la gestione dei riscontri che riprovera' dopo un tot.
						if(gestioneBusteNonRiscontrateAttive==false){
							ejbUtils.rollbackMessage("Ricostruzione del messaggio Soap da Spedire non riuscita.", esito);
							esito.setStatoInvocazioneErroreNonGestito(e);
							esito.setEsitoInvocazione(false);
						}else{
							ejbUtils.updateErroreProcessamentoMessage("Ricostruzione del messaggio Soap da Spedire non riuscita.", esito);
							esito.setEsitoInvocazione(true);
							esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,"msgRequest.getMessage()");
						}
					}
				}
				openspcoopstate.releaseResource();
				return esito;
			}	


			
			
			
			
			
			
			
			/* -----  Header Integrazione ------ */
			msgDiag.mediumDebug("Gestione header di integrazione per la richiesta...");
			HeaderIntegrazione headerIntegrazione = new HeaderIntegrazione(idTransazione);
			headerIntegrazione.getBusta().setTipoMittente(soggettoFruitore.getTipo());
			headerIntegrazione.getBusta().setMittente(soggettoFruitore.getNome());
			headerIntegrazione.getBusta().setTipoDestinatario(idServizio.getSoggettoErogatore().getTipo());
			headerIntegrazione.getBusta().setDestinatario(idServizio.getSoggettoErogatore().getNome());
			headerIntegrazione.getBusta().setTipoServizio(idServizio.getTipo());
			headerIntegrazione.getBusta().setServizio(idServizio.getNome());
			headerIntegrazione.getBusta().setVersioneServizio(idServizio.getVersione());
			headerIntegrazione.getBusta().setAzione(idServizio.getAzione());
			headerIntegrazione.getBusta().setID(bustaRichiesta.getID());
			headerIntegrazione.getBusta().setRiferimentoMessaggio(bustaRichiesta.getRiferimentoMessaggio());
			headerIntegrazione.getBusta().setIdCollaborazione(bustaRichiesta.getCollaborazione());
			headerIntegrazione.getBusta().setProfiloDiCollaborazione(bustaRichiesta.getProfiloDiCollaborazione());
			headerIntegrazione.setIdApplicativo(idCorrelazioneApplicativa);
			headerIntegrazione.setServizioApplicativo(servizioApplicativoFruitore);

			Map<String, List<String>>  propertiesTrasporto = new HashMap<String, List<String>> ();
			Map<String, List<String>>  propertiesUrlBased = new HashMap<String, List<String>> ();

			String [] tipiIntegrazionePD = null;
			try {
				if(pd!=null)
					tipiIntegrazionePD = configurazionePdDManager.getTipiIntegrazione(pd);
			} catch (Exception e) {
				msgDiag.logErroreGenerico(e, "getTipiIntegrazione(pd)");
				if(functionAsRouter){
					ejbUtils.sendAsRispostaBustaErroreProcessamento(richiestaDelegata.getIdModuloInAttesa(),bustaRichiesta,
							ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
								get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_536_CONFIGURAZIONE_NON_DISPONIBILE),
							idCorrelazioneApplicativa,idCorrelazioneApplicativaRisposta,servizioApplicativoFruitore,e,
							(requestMessagePrimaTrasformazione!=null ? requestMessagePrimaTrasformazione.getParseException() : null));
					esito.setEsitoInvocazione(true);
					esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,"getTipiIntegrazione(pd)");
				}else{
					if(sendRispostaApplicativa){
						OpenSPCoop2Message responseMessageError = 
								this.generatoreErrore.build(pddContext,IntegrationFunctionError.INTERNAL_REQUEST_ERROR,
										ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
											get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_536_CONFIGURAZIONE_NON_DISPONIBILE),e,
												(requestMessagePrimaTrasformazione!=null ? requestMessagePrimaTrasformazione.getParseException() : null));
						ejbUtils.sendRispostaApplicativaErrore(responseMessageError,richiestaDelegata,rollbackRichiesta,pd,sa);
						esito.setEsitoInvocazione(true);
						esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,"getTipiIntegrazione(pd)");
					}else{
						// Se Non e' attivo una gestione dei riscontri, faccio il rollback sulla coda.
						// Altrimenti verra attivato la gestione dei riscontri che riprovera' dopo un tot.
						if(gestioneBusteNonRiscontrateAttive==false){
							ejbUtils.rollbackMessage("Gestione header Integrazione Richiesta non riuscita.", esito);
							esito.setStatoInvocazioneErroreNonGestito(e);
							esito.setEsitoInvocazione(false);
						}else{
							ejbUtils.updateErroreProcessamentoMessage("Ricostruzione del messaggio Soap da Spedire non riuscita.", esito);
							esito.setEsitoInvocazione(true);
							esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,"getTipiIntegrazione(pd)");
						}
					}
				}
				openspcoopstate.releaseResource();
				return esito;
			}
			
			if (tipiIntegrazionePD == null){
				if(InoltroBuste.defaultPerProtocolloGestoreIntegrazionePD.containsKey(protocolFactory.getProtocol())){
					tipiIntegrazionePD = InoltroBuste.defaultPerProtocolloGestoreIntegrazionePD.get(protocolFactory.getProtocol());
				}else{
					tipiIntegrazionePD = InoltroBuste.defaultGestoriIntegrazionePD;
				}
			}
			
			OutRequestPDMessage outRequestPDMessage = new OutRequestPDMessage();
			outRequestPDMessage.setBustaRichiesta(bustaRichiesta);
			outRequestPDMessage.setMessage(requestMessagePrimaTrasformazione);
			outRequestPDMessage.setPortaDelegata(pd);
			outRequestPDMessage.setHeaders(propertiesTrasporto);
			outRequestPDMessage.setParameters(propertiesUrlBased);
			outRequestPDMessage.setServizio(idServizio);
			outRequestPDMessage.setSoggettoMittente(soggettoFruitore);
			
			for (int i = 0; i < tipiIntegrazionePD.length; i++) {
				try {					
					IGestoreIntegrazionePD gestore = null;
					try{
						gestore = (IGestoreIntegrazionePD) this.pluginLoader.newIntegrazionePortaDelegata(tipiIntegrazionePD[i]);
					}catch(Exception e){
						throw e;
					}
					if(gestore!=null){
						String classType = null;
						try {
							classType = gestore.getClass().getName();
							AbstractCore.init(gestore, pddContext, protocolFactory);
						}catch(Exception e){
							throw new Exception("Riscontrato errore durante l'inizializzazione della classe ["+classType+
									"] da utilizzare per la gestione dell'integrazione delle fruizioni di tipo ["+tipiIntegrazionePD[i]+"]: "+e.getMessage());
						}
						if(gestore instanceof IGestoreIntegrazionePDSoap){
							if(this.propertiesReader.deleteHeaderIntegrazioneRequestPD()){
								gestore.setOutRequestHeader(headerIntegrazione,outRequestPDMessage);
							}
							else{
								// gia effettuato l'update dell'header in RicezioneBuste
							}
						}else{
							gestore.setOutRequestHeader(headerIntegrazione,outRequestPDMessage);
						}
					}else{
						throw new Exception("Gestore non inizializzato");
					}
					
				} catch (Exception e) {
					msgDiag.addKeyword(CostantiPdD.KEY_TIPO_HEADER_INTEGRAZIONE,tipiIntegrazionePD[i]);
					msgDiag.addKeywordErroreProcessamento(e);
					msgDiag.logPersonalizzato("headerIntegrazione.creazioneFallita");
					this.log.error(msgDiag.getMessaggio_replaceKeywords("headerIntegrazione.creazioneFallita"), e);
				}
			}
			
			
			
			
			
			
			
			/* ------------ Trasformazione Richiesta  -------------- */
			
			GestoreTrasformazioni gestoreTrasformazioni = null;
			OpenSPCoop2Message requestMessageTrasformato = requestMessagePrimaTrasformazione;
			if(trasformazioni!=null) {
				try {
					gestoreTrasformazioni = new GestoreTrasformazioni(this.log, msgDiag, idServizio, soggettoFruitore, servizioApplicativoFruitore, 
							trasformazioni, transactionNullable, pddContext, requestInfo, tipoPdD,
							this.generatoreErrore);
					requestMessageTrasformato = gestoreTrasformazioni.trasformazioneRichiesta(requestMessagePrimaTrasformazione, bustaRichiesta);
				}
				catch(GestoreTrasformazioniException e) {
					
					msgDiag.addKeywordErroreProcessamento(e);
					msgDiag.logPersonalizzato("trasformazione.processamentoRichiestaInErrore");
					
					pddContext.addObject(org.openspcoop2.core.constants.Costanti.ERRORE_TRASFORMAZIONE_RICHIESTA, "true");
					
					ErroreIntegrazione erroreIntegrazione = gestoreTrasformazioni.getErrore();
					if(erroreIntegrazione==null) {
						erroreIntegrazione = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
								get5XX_ErroreProcessamento(e,CodiceErroreIntegrazione.CODICE_562_TRASFORMAZIONE);
					}
					
					if(sendRispostaApplicativa){
						
						IntegrationFunctionError integrationFunctionError = IntegrationFunctionError.TRANSFORMATION_RULE_REQUEST_FAILED;
						if(e.getOp2IntegrationFunctionError()!=null) {
							integrationFunctionError = e.getOp2IntegrationFunctionError();
						}
						OpenSPCoop2Message responseMessageError = null;
						if(e.getOpenSPCoop2ErrorMessage()!=null) {
							responseMessageError = e.getOpenSPCoop2ErrorMessage();
						}
						else {
							responseMessageError = this.generatoreErrore.build(pddContext,integrationFunctionError,
									erroreIntegrazione,e,
										(requestMessagePrimaTrasformazione!=null ? requestMessagePrimaTrasformazione.getParseException() : null));
						}
						
						ejbUtils.sendRispostaApplicativaErrore(responseMessageError,richiestaDelegata,rollbackRichiesta,pd,sa);
						esito.setEsitoInvocazione(true);
						esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,
								"Trasformazione-Richiesta");
						
					}else{
						// Se Non e' attivo una gestione dei riscontri, faccio il rollback sulla coda.
						// Altrimenti verra attivato la gestione dei riscontri che riprovera' dopo un tot.
						if(gestioneBusteNonRiscontrateAttive==false){
							ejbUtils.rollbackMessage("Trasformazione della richiesta non riuscita: "+e.getMessage(), esito);
							esito.setEsitoInvocazione(false);
							esito.setStatoInvocazione(EsitoLib.ERRORE_NON_GESTITO,
									"Trasformazione-Richiesta");
						}else{
							ejbUtils.updateErroreProcessamentoMessage("Trasformazione della richiesta non riuscita: "+e.getMessage(), esito);
							esito.setEsitoInvocazione(true);
							esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,
									"Trasformazione-Richiesta");
						}
					}
					openspcoopstate.releaseResource();
					return esito;
				}		
			}
			
			
			





			/* ------------  Gestione Funzionalita' speciali per Attachments (Manifest) ------------- */
			boolean scartaBody = false;
			if(functionAsRouter==false){
				boolean allegaBody = 
					configurazionePdDManager.isAllegaBody(pd);
				if(allegaBody){
					// E' stato effettuato prima l'inserimento del body come allegato.
					// Forzo lo scartamento.
					scartaBody = true;
				}else{
					scartaBody = configurazionePdDManager.isScartaBody(pd);
				}
			}


			Utilities.printFreeMemory("InoltroBuste - Imbustamento...");
			
			
			
			
			
			
			
			
			/* ------------  Imbustamento ------------- */	
			msgDiag.mediumDebug("Imbustamento ...");
			boolean gestioneManifest = false;
			if(functionAsRouter){
				gestioneManifest = configurazionePdDManager.isGestioneManifestAttachments();
			}else{
				gestioneManifest = configurazionePdDManager.isGestioneManifestAttachments(pd,protocolFactory);
			}
			BustaRawContent<?> headerBustaRichiesta = null;
			org.openspcoop2.protocol.engine.builder.Imbustamento imbustatore = null;
			try{
				msgDiag.highDebug("Imbustamento (creoImbustamentoUtils) ...");
				imbustatore = 
						new org.openspcoop2.protocol.engine.builder.Imbustamento(this.log, protocolFactory, openspcoopstate.getStatoRichiesta());
				msgDiag.highDebug("Imbustamento (invokeSdk) ...");
				if(functionAsRouter){
					if(this.propertiesReader.isGenerazioneListaTrasmissioni(implementazionePdDDestinatario)){
						msgDiag.highDebug("Tipo Messaggio Richiesta prima dell'imbustamento ["+requestMessageTrasformato.getClass().getName()+"]");
						ProtocolMessage protocolMessage = imbustatore.addTrasmissione(requestMessageTrasformato, tras, readQualifiedAttribute,
								FaseImbustamento.PRIMA_SICUREZZA_MESSAGGIO);
						if(protocolMessage!=null && !protocolMessage.isPhaseUnsupported()) {
							headerBustaRichiesta = protocolMessage.getBustaRawContent();
							requestMessageTrasformato = protocolMessage.getMessage(); // updated
						}
						msgDiag.highDebug("Tipo Messaggio Richiesta dopo l'imbustamento ["+requestMessageTrasformato.getClass().getName()+"]");
					}
					else{
						Validatore v = new Validatore(requestMessageTrasformato, pddContext, openspcoopstate.getStatoRichiesta(),this.log, protocolFactory);
						headerBustaRichiesta = v.getHeaderProtocollo_senzaControlli();
					}
				}else{
					msgDiag.highDebug("Tipo Messaggio Richiesta prima dell'imbustamento ["+requestMessageTrasformato.getClass().getName()+"]");
					ProtocolMessage protocolMessage = imbustatore.imbustamentoRichiesta(requestMessageTrasformato,pddContext,
							bustaRichiesta,
							integrazione,gestioneManifest,scartaBody,proprietaManifestAttachments,
							FaseImbustamento.PRIMA_SICUREZZA_MESSAGGIO);
					if(protocolMessage!=null && !protocolMessage.isPhaseUnsupported()) {
						headerBustaRichiesta = protocolMessage.getBustaRawContent();
						requestMessageTrasformato = protocolMessage.getMessage(); // updated
					}
					msgDiag.highDebug("Tipo Messaggio Richiesta dopo l'imbustamento ["+requestMessageTrasformato.getClass().getName()+"]");
				}
				msgDiag.highDebug("Imbustamento (invokeSdk) terminato");
			}catch(Exception e){
				String msgErroreImbusta = null;
				if(functionAsRouter)
					msgErroreImbusta = "imbustatore.before-sec.addTrasmissione";
				else
					msgErroreImbusta = "imbustatore.before-sec.imbustamento";
				msgDiag.logErroreGenerico(e, msgErroreImbusta);
				if(functionAsRouter){
					ejbUtils.sendAsRispostaBustaErroreProcessamento(richiestaDelegata.getIdModuloInAttesa(),bustaRichiesta,
							ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
								get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_526_GESTIONE_IMBUSTAMENTO),
							idCorrelazioneApplicativa,idCorrelazioneApplicativaRisposta,servizioApplicativoFruitore,e,
							(requestMessagePrimaTrasformazione!=null ? requestMessagePrimaTrasformazione.getParseException() : null));
					esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,msgErroreImbusta);
					esito.setEsitoInvocazione(true);
				}else{
					if(sendRispostaApplicativa){
						OpenSPCoop2Message responseMessageError = 
								this.generatoreErrore.build(pddContext,IntegrationFunctionError.INTEROPERABILITY_PROFILE_ENVELOPING_REQUEST_FAILED,
										ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
											get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_526_GESTIONE_IMBUSTAMENTO),e,
												(requestMessagePrimaTrasformazione!=null ? requestMessagePrimaTrasformazione.getParseException() : null));
						ejbUtils.sendRispostaApplicativaErrore(responseMessageError,richiestaDelegata,rollbackRichiesta,pd,sa);
						esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,msgErroreImbusta);
						esito.setEsitoInvocazione(true);
					}else{
						// Se Non e' attivo una gestione dei riscontri, faccio il rollback sulla coda.
						// Altrimenti verra attivato la gestione dei riscontri che riprovera' dopo un tot.
						if(gestioneBusteNonRiscontrateAttive==false){
							ejbUtils.rollbackMessage("Imbustamento non riuscito.", esito);
							esito.setEsitoInvocazione(false);
							esito.setStatoInvocazioneErroreNonGestito(e);
						}else{
							ejbUtils.updateErroreProcessamentoMessage("Imbustamento non riuscito.", esito);
							esito.setEsitoInvocazione(true);
							esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,msgErroreImbusta);
						}
					}
				}
				openspcoopstate.releaseResource();
				return esito;
			}


			
			
			
			/* ------------ Init MTOM Processor / SecurityContext -------------- */
			MTOMProcessor mtomProcessor = null;
			MessageSecurityConfig messageSecurityConfig = null;
			if(functionAsRouter==false){
			
				msgDiag.mediumDebug("init MTOM Processor / SecurityContext ...");
				ErroreIntegrazione erroreIntegrazione = null;
				Exception configException = null;
				String oggetto = null;
				
				MTOMProcessorConfig mtomConfig = null;
				try{
					mtomConfig=configurazionePdDManager.getPD_MTOMProcessorForSender(pd);
				}catch(Exception e){
					oggetto = "LetturaConfigurazioneMTOMProcessorRoleSender";
					erroreIntegrazione = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
							get5XX_ErroreProcessamento(e,CodiceErroreIntegrazione.CODICE_536_CONFIGURAZIONE_NON_DISPONIBILE);
					configException = e;
				}
				
				if(erroreIntegrazione==null){
					try{
						messageSecurityConfig=configurazionePdDManager.getPD_MessageSecurityForSender(pd, this.log, requestMessageTrasformato, bustaRichiesta, requestInfo, pddContext);
					}catch(Exception e){
						oggetto = "LetturaConfigurazioneMessageSecurityRoleSender";
						erroreIntegrazione = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
								get5XX_ErroreProcessamento(e,CodiceErroreIntegrazione.CODICE_536_CONFIGURAZIONE_NON_DISPONIBILE);
						configException = e;
					}
				}
				
				if(erroreIntegrazione!=null){
					msgDiag.logErroreGenerico(configException, oggetto);
					if(sendRispostaApplicativa){
						OpenSPCoop2Message responseMessageError = null;
						responseMessageError = this.generatoreErrore.build(pddContext,IntegrationFunctionError.INTERNAL_REQUEST_ERROR,
								erroreIntegrazione,configException,
								(requestMessagePrimaTrasformazione!=null ? requestMessagePrimaTrasformazione.getParseException() : null));
						ejbUtils.sendRispostaApplicativaErrore(responseMessageError,richiestaDelegata,rollbackRichiesta,pd,sa);
						esito.setEsitoInvocazione(true);
						esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO, configException.getMessage());
					}else{
						// Se Non e' attivo una gestione dei riscontri, faccio il rollback sulla coda.
						// Altrimenti verra attivato la gestione dei riscontri che riprovera' dopo un tot.
						if(gestioneBusteNonRiscontrateAttive==false){
							ejbUtils.rollbackMessage(oggetto+" non riuscita: "+configException.getMessage(), esito);
							esito.setEsitoInvocazione(false);
							esito.setStatoInvocazione(EsitoLib.ERRORE_NON_GESTITO, configException.getMessage());
						}else{
							ejbUtils.updateErroreProcessamentoMessage(oggetto+" non riuscita: "+configException.getMessage(), esito);
							esito.setEsitoInvocazione(true);
							esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO, configException.getMessage());
						}
					}
					openspcoopstate.releaseResource();
					return esito;
				}
				else{
					mtomProcessor = new MTOMProcessor(mtomConfig, messageSecurityConfig, 
							tipoPdD, msgDiag, this.log, pddContext);
				}
								
			}
			
			
			
			
			
			/* ------------ MTOM Processor BeforeSecurity  -------------- */
			if(mtomProcessor!=null){
				try{
					mtomProcessor.mtomBeforeSecurity(requestMessageTrasformato, RuoloMessaggio.RICHIESTA);
				}catch(Exception e){
					// L'errore viene registrato dentro il metodo mtomProcessor.mtomBeforeSecurity
					// msgDiag.logErroreGenerico(e,"MTOMProcessor(BeforeSec-"+mtomProcessor.getMTOMProcessorType()+")");
				
					ErroreIntegrazione erroreIntegrazione = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
							get5XX_ErroreProcessamento(e,CodiceErroreIntegrazione.CODICE_557_MTOM_PROCESSOR_ERROR);
					if(sendRispostaApplicativa){
						OpenSPCoop2Message responseMessageError = null;
						responseMessageError = this.generatoreErrore.build(pddContext,IntegrationFunctionError.ATTACHMENTS_PROCESSING_REQUEST_FAILED,
								erroreIntegrazione,e,
									(requestMessagePrimaTrasformazione!=null ? requestMessagePrimaTrasformazione.getParseException() : null));
						ejbUtils.sendRispostaApplicativaErrore(responseMessageError,richiestaDelegata,rollbackRichiesta,pd,sa);
						esito.setEsitoInvocazione(true);
						esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,
								"MTOMProcessor(BeforeSec-"+mtomProcessor.getMTOMProcessorType()+")");
					}else{
						// Se Non e' attivo una gestione dei riscontri, faccio il rollback sulla coda.
						// Altrimenti verra attivato la gestione dei riscontri che riprovera' dopo un tot.
						if(gestioneBusteNonRiscontrateAttive==false){
							ejbUtils.rollbackMessage("MTOMProcessor(BeforeSec-"+mtomProcessor.getMTOMProcessorType()+") non riuscita: "+e.getMessage(), esito);
							esito.setEsitoInvocazione(false);
							esito.setStatoInvocazione(EsitoLib.ERRORE_NON_GESTITO,
									"MTOMProcessor(BeforeSec-"+mtomProcessor.getMTOMProcessorType()+")");
						}else{
							ejbUtils.updateErroreProcessamentoMessage("MTOMProcessor(BeforeSec-"+mtomProcessor.getMTOMProcessorType()+") non riuscita: "+e.getMessage(), esito);
							esito.setEsitoInvocazione(true);
							esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,
									"MTOMProcessor(BeforeSec-"+mtomProcessor.getMTOMProcessorType()+")");
						}
					}
					openspcoopstate.releaseResource();
					return esito;
				}
			}
			else{
				if(functionAsRouter==false){
					msgDiag.logPersonalizzato("mtom.beforeSecurity.processamentoRichiestaDisabilitato");
				}
			}
			
			
			
			



			Utilities.printFreeMemory("InoltroBuste - Gestione MessageSecurity della richiesta ...");


			/* ------------  Gestione Message-Security ------------- */
			MessageSecurityFactory messageSecurityFactory = new MessageSecurityFactory();
			MessageSecurityContext messageSecurityContext = null;
			SecurityInfo securityInfo = null;
			if(functionAsRouter==false){

				// ottiene le proprieta' Message-Security relative alla porta delegata:
				// OneWay -> RequestFlow
				// Sincrono --> RequestFlow
				// Asincrono --> RequestFlow
				String msgErrore = null;
				ErroreIntegrazione erroreIntegrazione = null;
				CodiceErroreCooperazione codiceErroreCooperazione = null;
				Exception messageSecurityException = null;
				if(messageSecurityConfig!=null && messageSecurityConfig.getFlowParameters()!=null && 
						messageSecurityConfig.getFlowParameters().size() > 0){
					try{
						
						msgDiag.mediumDebug("Inizializzazione contesto di Message Security della richiesta ...");
						
						// Imposto un context di Base (utilizzato per la successiva ricezione)
						MessageSecurityContextParameters contextParameters = new MessageSecurityContextParameters();
						contextParameters.setUseActorDefaultIfNotDefined(this.propertiesReader.isGenerazioneActorDefault(implementazionePdDDestinatario));
						contextParameters.setActorDefault(this.propertiesReader.getActorDefault(implementazionePdDDestinatario));
						contextParameters.setLog(this.log);
						contextParameters.setFunctionAsClient(SecurityConstants.SECURITY_CLIENT);
						contextParameters.setPrefixWsuId(this.propertiesReader.getPrefixWsuId());
						contextParameters.setRemoveAllWsuIdRef(this.propertiesReader.isRemoveAllWsuIdRef());
						contextParameters.setIdFruitore(soggettoFruitore);
						contextParameters.setIdServizio(idServizio);
						contextParameters.setPddFruitore(registroServiziManager.getIdPortaDominio(soggettoFruitore, null));
						contextParameters.setPddErogatore(registroServiziManager.getIdPortaDominio(idServizio.getSoggettoErogatore(), null));
						
						messageSecurityContext = messageSecurityFactory.getMessageSecurityContext(contextParameters);
						messageSecurityContext.setOutgoingProperties(messageSecurityConfig.getFlowParameters());
						
						String tipoSicurezza = SecurityConstants.convertActionToString(messageSecurityContext.getOutgoingProperties());
						msgDiag.addKeyword(CostantiPdD.KEY_TIPO_SICUREZZA_MESSAGGIO_RICHIESTA, tipoSicurezza);
						pddContext.addObject(CostantiPdD.KEY_TIPO_SICUREZZA_MESSAGGIO_RICHIESTA, tipoSicurezza);
						
						msgDiag.mediumDebug("Inizializzazione contesto di Message Security della richiesta completata con successo");
						
						if(org.openspcoop2.security.message.engine.WSSUtilities.isNormalizeToSaajImpl(messageSecurityContext)){
							msgDiag.mediumDebug("Normalize to saajImpl");
							//System.out.println("InoltroBusteEgov.request.normalize");
							requestMessageTrasformato = requestMessageTrasformato.normalizeToSaajImpl();
						}
						
						msgDiag.logPersonalizzato("messageSecurity.processamentoRichiestaInCorso");
						if(messageSecurityContext.processOutgoing(requestMessageTrasformato,pddContext.getContext(),
								transactionNullable!=null ? transactionNullable.getTempiElaborazione() : null) == false){
							msgErrore = messageSecurityContext.getMsgErrore();
							codiceErroreCooperazione = messageSecurityContext.getCodiceErrore();
							
							msgDiag.addKeyword(CostantiPdD.KEY_ERRORE_PROCESSAMENTO , "["+codiceErroreCooperazione+"] "+msgErrore );
							msgDiag.logPersonalizzato("messageSecurity.processamentoRichiestaInErrore");
						}
						else{
							msgDiag.logPersonalizzato("messageSecurity.processamentoRichiestaEffettuato");
						}
						
					}catch(Exception e){
						
						msgDiag.addKeywordErroreProcessamento(e);
						msgDiag.logPersonalizzato("messageSecurity.processamentoRichiestaInErrore");
						this.log.error("[MessageSecurityRequest]" + e.getMessage(),e);
						
						erroreIntegrazione = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
								get5XX_ErroreProcessamento(e,CodiceErroreIntegrazione.CODICE_536_CONFIGURAZIONE_NON_DISPONIBILE);
						messageSecurityException = e;
					}
				}
				else{
					msgDiag.logPersonalizzato("messageSecurity.processamentoRichiestaDisabilitato");
				}
				if(erroreIntegrazione==null && codiceErroreCooperazione==null){
					IDigestReader digestReader = null;
					if(messageSecurityContext != null) {
						digestReader = messageSecurityContext.getDigestReader(requestMessageTrasformato!=null ? requestMessageTrasformato.getFactory() : OpenSPCoop2MessageFactory.getDefaultMessageFactory());
					}
					if(digestReader!=null){
						try{
							msgDiag.mediumDebug("Lettura informazioni sulla Sicurezza dal Messaggio di richiesta ...");
							securityInfo = validazioneSemantica.readSecurityInformation(digestReader,requestMessageTrasformato);
							msgDiag.mediumDebug("Lettura informazioni sulla Sicurezza dal Messaggio di richiesta completata con successo");
						}catch(Exception e){
							msgDiag.logErroreGenerico(e,"ErroreLetturaInformazioniSicurezza");
							erroreIntegrazione = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
								get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_549_SECURITY_INFO_READER_ERROR);
						}
					}
				}
				if(erroreIntegrazione!=null || codiceErroreCooperazione!= null){
					if(sendRispostaApplicativa){
						OpenSPCoop2Message responseMessageError = null;
						if(erroreIntegrazione!=null){
							responseMessageError = this.generatoreErrore.build(pddContext,IntegrationFunctionError.MESSAGE_SECURITY_REQUEST_FAILED,
									erroreIntegrazione,messageSecurityException,
										(requestMessagePrimaTrasformazione!=null ? requestMessagePrimaTrasformazione.getParseException() : null));
						}else{
							Eccezione ecc = Eccezione.getEccezioneValidazione(ErroriCooperazione.MESSAGE_SECURITY.getErroreMessageSecurity(msgErrore, codiceErroreCooperazione),protocolFactory);
							responseMessageError = this.generatoreErrore.build(pddContext,IntegrationFunctionError.MESSAGE_SECURITY_REQUEST_FAILED,
									ecc,richiestaDelegata.getIdSoggettoFruitore(),null);
						}
						ejbUtils.sendRispostaApplicativaErrore(responseMessageError,richiestaDelegata,rollbackRichiesta,pd,sa);
						esito.setEsitoInvocazione(true);
						esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,msgErrore);
					}else{
						// Se Non e' attivo una gestione dei riscontri, faccio il rollback sulla coda.
						// Altrimenti verra attivato la gestione dei riscontri che riprovera' dopo un tot.
						if(gestioneBusteNonRiscontrateAttive==false){
							ejbUtils.rollbackMessage("Applicazione MessageSecurity-Sender non riuscita: "+msgErrore, esito);
							esito.setEsitoInvocazione(false);
							esito.setStatoInvocazione(EsitoLib.ERRORE_NON_GESTITO,msgErrore);
						}else{
							ejbUtils.updateErroreProcessamentoMessage("Applicazione MessageSecurity-Sender non riuscita: "+msgErrore, esito);
							esito.setEsitoInvocazione(true);
							esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,msgErrore);
						}
					}
					openspcoopstate.releaseResource();
					return esito;
				}
			}  
			
			
			
			
			
			
			/* ------------ MTOM Processor AfterSecurity  -------------- */
			if(mtomProcessor!=null){
				try{
					mtomProcessor.mtomAfterSecurity(requestMessageTrasformato, RuoloMessaggio.RICHIESTA);
				}catch(Exception e){
					// L'errore viene registrato dentro il metodo mtomProcessor.mtomAfterSecurity
					//msgDiag.logErroreGenerico(e,"MTOMProcessor(AfterSec-"+mtomProcessor.getMTOMProcessorType()+")");
				
					ErroreIntegrazione erroreIntegrazione = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
							get5XX_ErroreProcessamento(e,CodiceErroreIntegrazione.CODICE_557_MTOM_PROCESSOR_ERROR);
					if(sendRispostaApplicativa){
						OpenSPCoop2Message responseMessageError = null;
						responseMessageError = this.generatoreErrore.build(pddContext,IntegrationFunctionError.ATTACHMENTS_PROCESSING_REQUEST_FAILED,
								erroreIntegrazione,e,
									(requestMessagePrimaTrasformazione!=null ? requestMessagePrimaTrasformazione.getParseException() : null));
						ejbUtils.sendRispostaApplicativaErrore(responseMessageError,richiestaDelegata,rollbackRichiesta,pd,sa);
						esito.setEsitoInvocazione(true);
						esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,
								"MTOMProcessor(AfterSec-"+mtomProcessor.getMTOMProcessorType()+")");
					}else{
						// Se Non e' attivo una gestione dei riscontri, faccio il rollback sulla coda.
						// Altrimenti verra attivato la gestione dei riscontri che riprovera' dopo un tot.
						if(gestioneBusteNonRiscontrateAttive==false){
							ejbUtils.rollbackMessage("MTOMProcessor(AfterSec-"+mtomProcessor.getMTOMProcessorType()+") non riuscita: "+e.getMessage(), esito);
							esito.setEsitoInvocazione(false);
							esito.setStatoInvocazione(EsitoLib.ERRORE_NON_GESTITO,
									"MTOMProcessor(AfterSec-"+mtomProcessor.getMTOMProcessorType()+")");
						}else{
							ejbUtils.updateErroreProcessamentoMessage("MTOMProcessor(AfterSec-"+mtomProcessor.getMTOMProcessorType()+") non riuscita: "+e.getMessage(), esito);
							esito.setEsitoInvocazione(true);
							esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,
									"MTOMProcessor(AfterSec-"+mtomProcessor.getMTOMProcessorType()+")");
						}
					}
					openspcoopstate.releaseResource();
					return esito;
				}
			}
			
			
			
			
			
			
			/* ------------  Imbustamento ------------- */	
			msgDiag.mediumDebug("Imbustamento (after security) ...");
			try{
				msgDiag.highDebug("Imbustamento (after security) (invokeSdk) ...");
				if(functionAsRouter){
					if(this.propertiesReader.isGenerazioneListaTrasmissioni(implementazionePdDDestinatario)){
						msgDiag.highDebug("Tipo Messaggio Richiesta prima dell'imbustamento (after security) ["+requestMessageTrasformato.getClass().getName()+"]");
						ProtocolMessage protocolMessage = imbustatore.addTrasmissione(requestMessageTrasformato, tras, readQualifiedAttribute,
								FaseImbustamento.DOPO_SICUREZZA_MESSAGGIO);
						if(protocolMessage!=null && !protocolMessage.isPhaseUnsupported()) {
							headerBustaRichiesta = protocolMessage.getBustaRawContent();
							requestMessageTrasformato = protocolMessage.getMessage(); // updated
						}
						msgDiag.highDebug("Tipo Messaggio Richiesta dopo l'imbustamento (after security) ["+requestMessageTrasformato.getClass().getName()+"]");
					}
					else{
						// gia' effettuata in fase PRIMA_SICUREZZA_MESSAGGIO
					}
				}else{
					msgDiag.highDebug("Tipo Messaggio Richiesta prima dell'imbustamento (after security) ["+requestMessageTrasformato.getClass().getName()+"]");
					ProtocolMessage protocolMessage = imbustatore.imbustamentoRichiesta(requestMessageTrasformato,pddContext,
							bustaRichiesta,
							integrazione,gestioneManifest,scartaBody,proprietaManifestAttachments,
							FaseImbustamento.DOPO_SICUREZZA_MESSAGGIO);
					if(protocolMessage!=null && !protocolMessage.isPhaseUnsupported()) {
						headerBustaRichiesta = protocolMessage.getBustaRawContent();
						requestMessageTrasformato = protocolMessage.getMessage(); // updated
					}
					msgDiag.highDebug("Tipo Messaggio Richiesta dopo l'imbustamento (after security) ["+requestMessageTrasformato.getClass().getName()+"]");
				}
				msgDiag.highDebug("Imbustamento (after security) (invokeSdk) terminato");
			}catch(Exception e){
				String msgErroreImbusta = null;
				if(functionAsRouter)
					msgErroreImbusta = "imbustatore.after-sec.addTrasmissione";
				else
					msgErroreImbusta = "imbustatore.after-sec.imbustamento";
				msgDiag.logErroreGenerico(e, msgErroreImbusta);
				if(functionAsRouter){
					ejbUtils.sendAsRispostaBustaErroreProcessamento(richiestaDelegata.getIdModuloInAttesa(),bustaRichiesta,
							ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
								get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_526_GESTIONE_IMBUSTAMENTO),
							idCorrelazioneApplicativa,idCorrelazioneApplicativaRisposta,servizioApplicativoFruitore,e,
							(requestMessagePrimaTrasformazione!=null ? requestMessagePrimaTrasformazione.getParseException() : null));
					esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,msgErroreImbusta);
					esito.setEsitoInvocazione(true);
				}else{
					if(sendRispostaApplicativa){
						
						ErroreIntegrazione erroreIntegrazione = null;
						if(e!=null && e instanceof ProtocolException && ((ProtocolException)e).isInteroperabilityError() ) {
							erroreIntegrazione = ErroriIntegrazione.ERRORE_439_FUNZIONALITA_NOT_SUPPORTED_BY_PROTOCOL.
									getErrore439_FunzionalitaNotSupportedByProtocol(e.getMessage(), protocolFactory);
						}
						else {
							erroreIntegrazione = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
									get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_526_GESTIONE_IMBUSTAMENTO);
						}
						
						OpenSPCoop2Message responseMessageError = 
								this.generatoreErrore.build(pddContext,IntegrationFunctionError.INTEROPERABILITY_PROFILE_ENVELOPING_REQUEST_FAILED,
										erroreIntegrazione,e,
												(requestMessagePrimaTrasformazione!=null ? requestMessagePrimaTrasformazione.getParseException() : null));
						ejbUtils.sendRispostaApplicativaErrore(responseMessageError,richiestaDelegata,rollbackRichiesta,pd,sa);
						esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,msgErroreImbusta);
						esito.setEsitoInvocazione(true);
					}else{
						// Se Non e' attivo una gestione dei riscontri, faccio il rollback sulla coda.
						// Altrimenti verra attivato la gestione dei riscontri che riprovera' dopo un tot.
						if(gestioneBusteNonRiscontrateAttive==false){
							ejbUtils.rollbackMessage("Imbustamento non riuscito (after-security).", esito);
							esito.setEsitoInvocazione(false);
							esito.setStatoInvocazioneErroreNonGestito(e);
						}else{
							ejbUtils.updateErroreProcessamentoMessage("Imbustamento non riuscito (after-security).", esito);
							esito.setEsitoInvocazione(true);
							esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,msgErroreImbusta);
						}
					}
				}
				openspcoopstate.releaseResource();
				return esito;
			}
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			

			Utilities.printFreeMemory("InoltroBuste - Impostazione messaggio del connettore...");





			/* ------------------- Preparo Spedizione -----------------------*/
			
			
			msgDiag.mediumDebug("Impostazione messaggio del connettore...");
			// Connettore per consegna
			String tipoConnector = connettore.getTipo();
			msgDiag.addKeyword(CostantiPdD.KEY_TIPO_CONNETTORE, tipoConnector);
			org.openspcoop2.core.config.Property [] cps = null;
			if(connettore.getPropertyList().size()>0){
				cps = connettore.getPropertyList().toArray(new org.openspcoop2.core.config.Property[connettore.getPropertyList().size()]);
			}
			ConnettoreMsg connettoreMsg = new ConnettoreMsg(tipoConnector,requestMessageTrasformato,cps);
			connettoreMsg.setBusta(bustaRichiesta);
			connettoreMsg.setIdModulo(InoltroBuste.ID_MODULO);
			connettoreMsg.setMsgDiagnostico(msgDiag);
			connettoreMsg.setState(openspcoopstate.getStatoRichiesta());
			connettoreMsg.setProtocolFactory(protocolFactory);
			connettoreMsg.setPropertiesTrasporto(propertiesTrasporto);
			connettoreMsg.setPropertiesUrlBased(propertiesUrlBased);
			connettoreMsg.initPolicyGestioneToken(configurazionePdDManager);
			connettoreMsg.setForwardProxy(forwardProxy);
			connettoreMsg.setIdAccordo(idAccordoServizio);
			if(requestMessagePrimaTrasformazione!=null && requestMessagePrimaTrasformazione.getTransportRequestContext()!=null) {
				connettoreMsg.setUrlInvocazionePorta(requestMessagePrimaTrasformazione.getTransportRequestContext().getUrlInvocazione_formBased());
			}
			IConnettore connectorSender = null;

			// mapping per forward token
			TokenForward tokenForward = null;
			Object oTokenForward = requestMessageTrasformato.getContextProperty(org.openspcoop2.pdd.core.token.Costanti.MSG_CONTEXT_TOKEN_FORWARD);
			if(oTokenForward!=null) {
				tokenForward = (TokenForward) oTokenForward;
			}
			if(tokenForward!=null) {
				if(tokenForward.getTrasporto()!=null && tokenForward.getTrasporto().size()>0) {
					propertiesTrasporto.putAll(tokenForward.getTrasporto());
				}
				if(tokenForward.getUrl()!=null && tokenForward.getUrl().size()>0) {
					propertiesUrlBased.putAll(tokenForward.getUrl());
				}
			}
			
			// Risposte del connettore
			int codiceRitornato = -1;

			// Stato consegna tramite connettore
			boolean errorConsegna = false;
			boolean riconsegna = false;
			java.sql.Timestamp dataRiconsegna = null;
			String motivoErroreConsegna = null;
			boolean invokerNonSupportato = false;
			SOAPFault soapFault = null;
			ProblemRFC7807 restProblem = null;
			OpenSPCoop2MessageFactory faultMessageFactory = null;
			Exception eccezioneProcessamentoConnettore = null;

			// Carico connettore richiesto
			String connectorClass = null;
			Exception eInvokerNonSupportato = null;
			if(invokerNonSupportato==false){
				try{
					connectorSender = (IConnettore) this.pluginLoader.newConnettore(tipoConnector);
				}
				catch(Exception e){
					msgDiag.logErroreGenerico(e,"Inizializzazione Connettore"); // l'errore contiene gia tutte le informazioni
					invokerNonSupportato = true;
					eInvokerNonSupportato = e;
				}
				if(connectorSender!=null) {
					try {
						connectorClass = connectorSender.getClass().getName();
						AbstractCore.init(connectorSender, pddContext, protocolFactory);
					}catch(Exception e){
						msgDiag.logErroreGenerico(e,"IConnettore.newInstance(tipo:"+tipoConnector+" class:"+connectorClass+")");
						invokerNonSupportato = true;
						eInvokerNonSupportato = e;
					}
				}
				if( (invokerNonSupportato == false) && (connectorSender == null)){
					msgDiag.logErroreGenerico("ConnectorSender is null","IConnettore.newInstance(tipo:"+tipoConnector+" class:"+connectorClass+")");
					invokerNonSupportato = true;
				}
			}
			
			// Imposto tipo di richiesta
			HttpRequestMethod httpRequestMethod = null;
			if(connectorSender!=null){
				try{
					if(connectorSender instanceof ConnettoreBaseHTTP){
						ConnettoreBaseHTTP baseHttp = (ConnettoreBaseHTTP) connectorSender;
						baseHttp.setHttpMethod(requestMessageTrasformato);
						
						if(ServiceBinding.REST.equals(requestMessageTrasformato.getServiceBinding())){
							httpRequestMethod = baseHttp.getHttpMethod();
						}
					}
				}catch(Exception e){
					msgDiag.logErroreGenerico(e,"ConnettoreBaseHTTP.setHttpMethod(tipo:"+tipoConnector+" class:"+connectorClass+")");
					invokerNonSupportato = true;
					eInvokerNonSupportato = e;
				}
			}

			// Location
			location = ConnettoreUtils.getAndReplaceLocationWithBustaValues(connectorSender, connettoreMsg, bustaRichiesta, pddContext, protocolFactory, this.log);
			if(location!=null){
				String locationWithUrl = ConnettoreUtils.buildLocationWithURLBasedParameter(this.log, requestMessageTrasformato, connettoreMsg.getTipoConnettore(), connettoreMsg.getPropertiesUrlBased(), location,
						protocolFactory, this.idModulo);
				locationWithUrl = ConnettoreUtils.addProxyInfoToLocationForHTTPConnector(connettoreMsg.getTipoConnettore(), connettoreMsg.getConnectorProperties(), locationWithUrl);
				locationWithUrl = ConnettoreUtils.addGovWayProxyInfoToLocationForHTTPConnector(connettoreMsg.getForwardProxy(),connectorSender, locationWithUrl);
				msgDiag.addKeyword(CostantiPdD.KEY_LOCATION, ConnettoreUtils.formatLocation(httpRequestMethod, locationWithUrl));
				
				pddContext.addObject(CostantiPdD.CONNETTORE_REQUEST_METHOD, httpRequestMethod);
				pddContext.addObject(CostantiPdD.CONNETTORE_REQUEST_URL, locationWithUrl);
			}
			else{
				msgDiag.addKeyword(CostantiPdD.KEY_LOCATION, "N.D.");
			}

			// timeout di default
			if(connettoreMsg.getConnectorProperties()==null){
				java.util.Map<String,String> propCon = new java.util.HashMap<String,String>();
				connettoreMsg.setConnectorProperties(propCon);
			}
			if(connettoreMsg.getConnectorProperties().get(CostantiConnettori.CONNETTORE_CONNECTION_TIMEOUT)==null){
				connettoreMsg.getConnectorProperties().put(CostantiConnettori.CONNETTORE_CONNECTION_TIMEOUT,""+this.propertiesReader.getConnectionTimeout_inoltroBuste());
			}
			if(connettoreMsg.getConnectorProperties().get(CostantiConnettori.CONNETTORE_READ_CONNECTION_TIMEOUT)==null){
				connettoreMsg.getConnectorProperties().put(CostantiConnettori.CONNETTORE_READ_CONNECTION_TIMEOUT,""+this.propertiesReader.getReadConnectionTimeout_inoltroBuste());
			}

			// User-Agent e X-* header
			UtilitiesIntegrazione httpUtilities = UtilitiesIntegrazione.getInstancePDRequest(this.log);
			if(connettoreMsg.getPropertiesTrasporto()==null){
				Map<String, List<String>> trasporto = new HashMap<String, List<String>>();
				connettoreMsg.setPropertiesTrasporto(trasporto);
			}
			httpUtilities.setInfoProductTransportProperties(connettoreMsg.getPropertiesTrasporto());
			
			
			
			
			
			
			
			
			/* ------------------- OutRequestHandler -----------------------*/
			OutRequestContext outRequestContext = null;
			try{
				outRequestContext = new OutRequestContext(this.log,protocolFactory,openspcoopstate.getStatoRichiesta());
				
				// Informazioni connettore in uscita
				InfoConnettoreUscita infoConnettoreUscita = new InfoConnettoreUscita();
				infoConnettoreUscita.setLocation(location);
				infoConnettoreUscita.setProperties(connettoreMsg.getConnectorProperties());
				infoConnettoreUscita.setHeaders(connettoreMsg.getPropertiesTrasporto());
				infoConnettoreUscita.setParameters(connettoreMsg.getPropertiesUrlBased());
				infoConnettoreUscita.setSbustamentoSoap(connettoreMsg.isSbustamentoSOAP());
				infoConnettoreUscita.setSbustamentoInformazioniProtocollo(connettoreMsg.isSbustamentoInformazioniProtocollo());
				infoConnettoreUscita.setTipoAutenticazione(connettoreMsg.getAutenticazione());
				infoConnettoreUscita.setCredenziali(connettoreMsg.getCredenziali());
				infoConnettoreUscita.setTipoConnettore(connettoreMsg.getTipoConnettore());
				outRequestContext.setConnettore(infoConnettoreUscita);
				
				// Informazioni messaggio
				outRequestContext.setMessaggio(requestMessageTrasformato);
				
				// Contesto
				ProtocolContext protocolContext = new ProtocolContext();
				protocolContext.setFruitore(soggettoFruitore);
				if(bustaRichiesta!=null){
					protocolContext.setIndirizzoFruitore(bustaRichiesta.getIndirizzoMittente());
				}
				protocolContext.setIdRichiesta(idMessageRequest);
				if(idServizio!=null){
					protocolContext.setErogatore(idServizio.getSoggettoErogatore());
					if(bustaRichiesta!=null){
						protocolContext.setIndirizzoErogatore(bustaRichiesta.getIndirizzoDestinatario());
					}
					protocolContext.setTipoServizio(idServizio.getTipo());
					protocolContext.setServizio(idServizio.getNome());
					protocolContext.setVersioneServizio(idServizio.getVersione());
					protocolContext.setAzione(idServizio.getAzione());
				}
				if(idAccordoServizio!=null){
					protocolContext.setIdAccordo(idAccordoServizio);
				}
				else if(idServizio!=null && idServizio.getUriAccordoServizioParteComune()!=null){
					protocolContext.setIdAccordo(IDAccordoFactory.getInstance().getIDAccordoFromUri(idServizio.getUriAccordoServizioParteComune()));
				}
				if(bustaRichiesta!=null){
					protocolContext.setProfiloCollaborazione(bustaRichiesta.getProfiloDiCollaborazione(),bustaRichiesta.getProfiloDiCollaborazioneValue());
					protocolContext.setCollaborazione(bustaRichiesta.getCollaborazione());
				}
				protocolContext.setDominio(msgDiag.getDominio());
				protocolContext.setScenarioCooperazione(richiestaDelegata.getScenario());
				outRequestContext.setProtocollo(protocolContext);
				
				// Integrazione
				IntegrationContext integrationContext = new IntegrationContext();
				integrationContext.setIdCorrelazioneApplicativa(idCorrelazioneApplicativa);
				integrationContext.setServizioApplicativoFruitore(servizioApplicativoFruitore);
				integrationContext.setGestioneStateless(portaDiTipoStateless);
				integrationContext.setIdPD(idPD);
				outRequestContext.setIntegrazione(integrationContext);
				
				// Altre informazioni
				outRequestContext.setDataElaborazioneMessaggio(DateManager.getDate());
				outRequestContext.setPddContext(pddContext);
				if(functionAsRouter)
					outRequestContext.setTipoPorta(TipoPdD.ROUTER);
				else
					outRequestContext.setTipoPorta(TipoPdD.DELEGATA);
				outRequestContext.setIdModulo(this.idModulo);
								
				// Invocazione handler
				GestoreHandlers.outRequest(outRequestContext, msgDiag, this.log);
				
				// Riporto messaggio
				requestMessageTrasformato = outRequestContext.getMessaggio();
				
				// Salvo handler
				connettoreMsg.setOutRequestContext(outRequestContext);
				
			}
			catch(Exception e){
				String msgErrore = null;
				if(e!=null && e.getMessage()!=null){
					if(e.getMessage().length()>150){
						msgErrore = e.getMessage().substring(0, 150);
					}else{
						msgErrore = e.getMessage();
					}
				}
				ErroreIntegrazione erroreIntegrazione = null;
				IntegrationFunctionError integrationFunctionError = null;
				if(e instanceof HandlerException){
					HandlerException he = (HandlerException) e;
					if(he.isEmettiDiagnostico()){
						msgDiag.logErroreGenerico(e, ((HandlerException)e).getIdentitaHandler());
					}
					msgErrore = ((HandlerException)e).getIdentitaHandler()+" error: "+msgErrore;
					if((functionAsRouter || sendRispostaApplicativa)) {
						erroreIntegrazione = he.convertToErroreIntegrazione();
						integrationFunctionError = he.getIntegrationFunctionError();
					}
				}else{
					msgDiag.logErroreGenerico(e, "OutRequestHandler");
					msgErrore = "OutRequestHandler error: "+msgErrore;
				}
				if(functionAsRouter){
					if(erroreIntegrazione==null){
						erroreIntegrazione = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
								get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_543_HANDLER_OUT_REQUEST);
					}
					ejbUtils.sendAsRispostaBustaErroreProcessamento(richiestaDelegata.getIdModuloInAttesa(),bustaRichiesta,
							erroreIntegrazione,
							idCorrelazioneApplicativa,idCorrelazioneApplicativaRisposta,servizioApplicativoFruitore,e,
							(requestMessagePrimaTrasformazione!=null ? requestMessagePrimaTrasformazione.getParseException() : null));
					esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,msgErrore);
					esito.setEsitoInvocazione(true);
				}else{
					if(sendRispostaApplicativa){
						if(erroreIntegrazione==null){
							erroreIntegrazione = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
									get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_543_HANDLER_OUT_REQUEST);
						}
						if(integrationFunctionError==null) {
							integrationFunctionError = IntegrationFunctionError.INTERNAL_REQUEST_ERROR;
						}
						OpenSPCoop2Message responseMessageError = 
								this.generatoreErrore.build(pddContext,integrationFunctionError,erroreIntegrazione,e,
									(requestMessagePrimaTrasformazione!=null ? requestMessagePrimaTrasformazione.getParseException() : null));
						if(e instanceof HandlerException){
							HandlerException he = (HandlerException) e;
							he.customized(responseMessageError);
						}
						ejbUtils.sendRispostaApplicativaErrore(responseMessageError,richiestaDelegata,rollbackRichiesta,pd,sa);
						esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,msgErrore);
						esito.setEsitoInvocazione(true);
					}else{
						// Se Non e' attivo una gestione dei riscontri, faccio il rollback sulla coda.
						// Altrimenti verra attivato la gestione dei riscontri che riprovera' dopo un tot.
						if(gestioneBusteNonRiscontrateAttive==false){
							ejbUtils.rollbackMessage(msgErrore, esito);
							esito.setEsitoInvocazione(false);
							esito.setStatoInvocazioneErroreNonGestito(e);
						}else{
							ejbUtils.updateErroreProcessamentoMessage(msgErrore, esito);
							esito.setEsitoInvocazione(true);
							esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,msgErrore);
						}
					}
				}
				openspcoopstate.releaseResource();
				return esito;
			}
			
			
			
			
			
			
			
			
			
			/* --------------- REFRESH LOCATION ----------------- */
			// L'handler puo' aggiornare le properties che contengono le proprieta' del connettore.
			location = ConnettoreUtils.getAndReplaceLocationWithBustaValues(connectorSender, connettoreMsg, bustaRichiesta, pddContext, protocolFactory, this.log);
			if(location!=null){
				String locationWithUrl = ConnettoreUtils.buildLocationWithURLBasedParameter(this.log, requestMessageTrasformato, connettoreMsg.getTipoConnettore(), connettoreMsg.getPropertiesUrlBased(), location,
						protocolFactory, this.idModulo);
				locationWithUrl = ConnettoreUtils.addProxyInfoToLocationForHTTPConnector(connettoreMsg.getTipoConnettore(), connettoreMsg.getConnectorProperties(), locationWithUrl);
				locationWithUrl = ConnettoreUtils.addGovWayProxyInfoToLocationForHTTPConnector(connettoreMsg.getForwardProxy(),connectorSender, locationWithUrl);
				msgDiag.addKeyword(CostantiPdD.KEY_LOCATION, ConnettoreUtils.formatLocation(httpRequestMethod, locationWithUrl));
				
				pddContext.addObject(CostantiPdD.CONNETTORE_REQUEST_METHOD, httpRequestMethod);
				pddContext.addObject(CostantiPdD.CONNETTORE_REQUEST_URL, locationWithUrl);
			}
			else{
				msgDiag.addKeyword(CostantiPdD.KEY_LOCATION, "N.D.");
			}

			
			
			
			
			
			
			
			
			Utilities.printFreeMemory("InoltroBuste - Dump...");
			
			
			
			
			/* ------------------- Dump -----------------------*/
			DumpConfigurazione dumpConfig = configurazionePdDManager.getDumpConfigurazione(pd);
			Dump dumpApplicativoRichiesta = new Dump(identitaPdD,InoltroBuste.ID_MODULO,idMessageRequest,
					soggettoFruitore,idServizio,tipoPdD,msgDiag.getPorta(),pddContext,
					openspcoopstate.getStatoRichiesta(),openspcoopstate.getStatoRisposta(),
					dumpConfig);
			dumpApplicativoRichiesta.dumpRichiestaUscita(requestMessageTrasformato, outRequestContext.getConnettore());
			
			
			
			
			
			
			
			
			
			
			
			
			
			/* ------------------- 
			   Rilascio Risorsa al DB (La riprendero' dopo aver ottenuto la risposta, se necessario) 
			   Le informazioni nel DB sono state utilizzate fino a questo punto solo in lettura.
			   -----------------------*/
			msgDiag.mediumDebug("Rilascio risorse...");
			openspcoopstate.releaseResource();
			
			
			
			
			
			
			
			Utilities.printFreeMemory("InoltroBuste - spedizione..");
			
			
			

			// --------------------- spedizione --------------------------
			Date dataPrimaInvocazioneConnettore = null;
			Date dataTerminataInvocazioneConnettore = null;
			if(invokerNonSupportato==false){
				// utilizzo connettore
				msgDiag.logPersonalizzato("inoltroInCorso");
				ejbUtils.setSpedizioneMsgIngresso(new Timestamp(outRequestContext.getDataElaborazioneMessaggio().getTime()));
				dataPrimaInvocazioneConnettore = DateManager.getDate();
				errorConsegna = !connectorSender.send(responseCachingConfig, connettoreMsg);
				dataTerminataInvocazioneConnettore = DateManager.getDate();
			}
			
			
			Utilities.printFreeMemory("InoltroBuste - Richiesta risorsa per la gestione della risposta...");
			
			
			
					
			/* ------------  Re-ottengo Connessione al DB -------------- */
			msgDiag.mediumDebug("Richiesta risorsa per la gestione della risposta...");
			try{
				
				boolean gestioneAsincroniStateless = 
					(org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.ASINCRONO_ASIMMETRICO.equals(bustaRichiesta.getProfiloDiCollaborazione()) ||
							org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.ASINCRONO_SIMMETRICO.equals(bustaRichiesta.getProfiloDiCollaborazione()))
					&& configurazionePdDManager.isModalitaStateless(pd, bustaRichiesta.getProfiloDiCollaborazione());
				boolean oldGestioneConnessione = false;
				if(gestioneAsincroniStateless){
					oldGestioneConnessione = ((OpenSPCoopStateless)openspcoopstate).isUseConnection();
					((OpenSPCoopStateless)openspcoopstate).setUseConnection(true);
				}
				openspcoopstate.updateResource(idTransazione);
				if(gestioneAsincroniStateless){
					((OpenSPCoopStateless)openspcoopstate).setUseConnection(oldGestioneConnessione);
				}
				
				// Aggiorno risorse
				ejbUtils.updateOpenSPCoopState(openspcoopstate);
				msgRequest.updateOpenSPCoopState(openspcoopstate);
				repositoryBuste.updateState(openspcoopstate.getStatoRichiesta());
				
				// POOL,TRANSACTIONISOLATION:
				//connectionDB.setTransactionIsolation(DBManager.getTransactionIsolationLevel());
			}catch(Exception e){
				msgDiag.logErroreGenerico(e,"openspcoopstate.updateResource()");
				openspcoopstate.releaseResource();
				esito.setEsitoInvocazione(false);	
				return esito;
			}
			
			
			
			
			
			
			
			

			
			
		
			
			
			
			
			/* ------------  Analisi Risposta -------------- */
			if(invokerNonSupportato==false){
				
				try {
					msgDiag.mediumDebug("Analisi Risposta");
					
					// nota per lo stato si intende un esito di errore connettore quando è proprio il connettore a restituire errore.
					// se invece il connettore esce "bene" e restituisce poi un codice http e/o una risposta, si rientra nei casi sottostanti
					pddContext.addObject(org.openspcoop2.core.constants.Costanti.ERRORE_UTILIZZO_CONNETTORE, errorConsegna);
					
					ejbUtils.setRicezioneMsgRisposta(DateManager.getTimestamp());
					motivoErroreConsegna = connectorSender.getErrore();
					eccezioneProcessamentoConnettore = connectorSender.getEccezioneProcessamento();
					if(errorConsegna && motivoErroreConsegna==null){
						motivoErroreConsegna = "Errore durante la consegna";
					}
					//	interpretazione esito consegna
					GestioneErrore gestioneConsegnaConnettore =configurazionePdDManager.getGestioneErroreConnettoreComponenteCooperazione(protocolFactory, requestMessageTrasformato.getServiceBinding());
					GestoreErroreConnettore gestoreErrore = new GestoreErroreConnettore();
					errorConsegna = !gestoreErrore.verificaConsegna(gestioneConsegnaConnettore,motivoErroreConsegna,eccezioneProcessamentoConnettore,connectorSender);
					if(errorConsegna){
						motivoErroreConsegna = gestoreErrore.getErrore();
						riconsegna = gestoreErrore.isRiconsegna();
						dataRiconsegna = gestoreErrore.getDataRispedizione();
					}
					// dopo aver verificato se siamo in un caso di errore, vediamo se l'errore è dovuto al codice di trasporto
					// in tal caso rientriamo in un utilizzo del connettore con errore.
					if(errorConsegna) {
						if(connectorSender.getResponse()==null) {
							pddContext.addObject(org.openspcoop2.core.constants.Costanti.ERRORE_UTILIZZO_CONNETTORE, errorConsegna);
						}
					}
					// raccolta risultati del connettore
					soapFault = gestoreErrore.getFault();
					restProblem = gestoreErrore.getProblem();
					faultMessageFactory = connectorSender.getResponse()!=null ? connectorSender.getResponse().getFactory() : OpenSPCoop2MessageFactory.getDefaultMessageFactory();
					codiceRitornato = connectorSender.getCodiceTrasporto();
					transportResponseContext = new TransportResponseContext(this.log, connectorSender.getCodiceTrasporto()+"", 
							connectorSender.getHeaderTrasporto(), 
							connectorSender.getContentLength(), 
							motivoErroreConsegna, connectorSender.getEccezioneProcessamento());
					responseMessage = connectorSender.getResponse();	
					useResponseForParseException = true;
					if(responseMessage!=null){
						responseMessage.setTransportRequestContext(requestMessagePrimaTrasformazione.getTransportRequestContext());
						responseMessage.setTransportResponseContext(transportResponseContext);
					}			
					// gestione connessione connettore
					if(functionAsRouter){
						RepositoryConnettori.salvaConnettorePA(
								//idMessageRequest, 
								idTransazione,
								connectorSender);
					}
					else{
						if(sendRispostaApplicativa ) {
							RepositoryConnettori.salvaConnettorePD(
									//idMessageRequest, 
									idTransazione,
									connectorSender);
						}
						else{
							// Sono nella casistica di messaggio preso in carico.
							// Non si deve chiudere immediatamente la connessione, poiche' nel resto del modulo, il messaggio puo' ancora essere utilizzato (es. dump)
							connectorSenderForDisconnect = connectorSender;
						}
					}
					
					msgDiag.addKeyword(CostantiPdD.KEY_CODICE_CONSEGNA, codiceRitornato+"");
					
					// Il Connettore potrebbe aggiungere informazioni alla location.
					String tmpLocation = connectorSender.getLocation();
					if(tmpLocation!=null){
						
						// salvo la request url originale, se la risposta non è letta dalla cache
						boolean responseCached = false;
						if(pddContext.containsKey(ConnettoreBase.RESPONSE_FROM_CACHE)) {
							responseCached = (Boolean) pddContext.getObject(ConnettoreBase.RESPONSE_FROM_CACHE);
						}
						if(!responseCached) {
							pddContext.addObject(CostantiPdD.CONNETTORE_REQUEST_METHOD, httpRequestMethod);
							pddContext.addObject(CostantiPdD.CONNETTORE_REQUEST_URL, tmpLocation);
						}
						
						// aggiorno
						location = tmpLocation;
						if(responseCached) {
							msgDiag.addKeyword(CostantiPdD.KEY_LOCATION, location);
						}
						else {
							msgDiag.addKeyword(CostantiPdD.KEY_LOCATION, ConnettoreUtils.formatLocation(httpRequestMethod, location));
						}
					}
					
					/* ------------ Check Charset ------------- */
					try {
						if(transportResponseContext!=null) {
							boolean checkEnabled = false;
							List<String> ctDefault = null;
							if(requestMessageTrasformato!=null && ServiceBinding.SOAP.equals(requestMessageTrasformato.getServiceBinding())){
								if(this.propertiesReader.isControlloCharsetContentTypeAbilitatoRicezioneContenutiApplicativiSoap()) {
									checkEnabled = true;
									ctDefault = this.propertiesReader.getControlloCharsetContentTypeAbilitatoRicezioneContenutiApplicativiSoap();
								}
							}
							else {
								if(this.propertiesReader.isControlloCharsetContentTypeAbilitatoRicezioneContenutiApplicativiRest()) {
									checkEnabled = true;
									ctDefault = this.propertiesReader.getControlloCharsetContentTypeAbilitatoRicezioneContenutiApplicativiRest();
								}
							}
							if(checkEnabled) {
								if(transportResponseContext.getContentType()!=null) {
									ServicesUtils.checkCharset(transportResponseContext.getContentType(), ctDefault, msgDiag, false, TipoPdD.DELEGATA);
								}
							}
						}
					}catch(Throwable t) {
						String ct = null;
						try {
							if(transportResponseContext!=null) {
								ct = transportResponseContext.getContentType();
							}
						}catch(Throwable tRead) {}	
						this.log.error("Avvenuto errore durante il controllo del charset della risposta (Content-Type: "+ct+"): "+t.getMessage(),t);
					}
					
				} catch (Exception e) {
					msgDiag.addKeywordErroreProcessamento(e, "Analisi risposta fallita");
					msgDiag.logErroreGenerico(e,"AnalisiRispostaConnettore");
					String msgErrore = "Analisi risposta del connettore ha provocato un errore: "+e.getMessage();
					this.log.error(msgErrore,e);
					if(functionAsRouter){
						ejbUtils.sendAsRispostaBustaErroreProcessamento(richiestaDelegata.getIdModuloInAttesa(),bustaRichiesta,
								ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
									get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_500_ERRORE_INTERNO),
								idCorrelazioneApplicativa,idCorrelazioneApplicativaRisposta,servizioApplicativoFruitore,e,
								(connectorSender!=null && connectorSender.getResponse()!=null ? connectorSender.getResponse().getParseException() : null));
						esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,msgErrore);
						esito.setEsitoInvocazione(true);
					}else{
						if(sendRispostaApplicativa){
							OpenSPCoop2Message responseMessageError = 
									this.generatoreErrore.build(pddContext,IntegrationFunctionError.INTERNAL_RESPONSE_ERROR,
											ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
												get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_500_ERRORE_INTERNO),e,
													(connectorSender!=null && connectorSender.getResponse()!=null ? connectorSender.getResponse().getParseException() : null));
							ejbUtils.sendRispostaApplicativaErrore(responseMessageError,richiestaDelegata,rollbackRichiesta,pd,sa);
							esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,msgErrore);
							esito.setEsitoInvocazione(true);
						}else{
							// Se Non e' attivo una gestione dei riscontri, faccio il rollback sulla coda.
							// Altrimenti verra attivato la gestione dei riscontri che riprovera' dopo un tot.
							if(gestioneBusteNonRiscontrateAttive==false){
								ejbUtils.rollbackMessage(msgErrore, esito);
								esito.setEsitoInvocazione(false);
								esito.setStatoInvocazioneErroreNonGestito(e);
							}else{
								ejbUtils.updateErroreProcessamentoMessage(msgErrore, esito);
								esito.setEsitoInvocazione(true);
								esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,msgErrore);
							}
						}
					}
					openspcoopstate.releaseResource();
					return esito;
				}
			}



			
			
			

			
			
			
			

			
			
			/* ------------ Trasformazione Risposta  -------------- */
			
			boolean dumpRispostaEffettuato = false;
			if(trasformazioni!=null && responseMessage!=null) {
				try {
					
					// prima effettuo dump applicativo
					if(responseMessage!=null ){
						Dump dumpApplicativo = new Dump(identitaPdD,InoltroBuste.ID_MODULO,idMessageRequest,
								soggettoFruitore,idServizio,tipoPdD,msgDiag.getPorta(),pddContext,
								openspcoopstate.getStatoRichiesta(),openspcoopstate.getStatoRisposta(),
								dumpConfig);
						InfoConnettoreUscita infoConnettoreUscita = outRequestContext.getConnettore();
						if(infoConnettoreUscita!=null){
							infoConnettoreUscita.setLocation(location); // aggiorno location ottenuta dal connettore utilizzato
						}
						dumpApplicativo.dumpRispostaIngresso(responseMessage, infoConnettoreUscita, connectorSender.getHeaderTrasporto());
						dumpRispostaEffettuato = true;
					}
					
					MessageType messageTypePrimaTrasformazione = responseMessage.getMessageType();
					
					responseMessage = gestoreTrasformazioni.trasformazioneRisposta(responseMessage, bustaRichiesta);
					
					MessageType messageTypeDopoTrasformazione = (responseMessage!=null)  ? responseMessage.getMessageType() : null;
					if(messageTypeDopoTrasformazione==null || messageTypePrimaTrasformazione.equals(messageTypeDopoTrasformazione)==false) {
						soapFault = null;
						restProblem = null;
						if(messageTypeDopoTrasformazione!=null) {
							if(responseMessage instanceof OpenSPCoop2SoapMessage){
								if(responseMessage.castAsSoap().hasSOAPFault()){
									SOAPBody body = responseMessage.castAsSoap().getSOAPBody();
									soapFault = body.getFault();
								}
							}
							else {
								if(responseMessage instanceof OpenSPCoop2RestJsonMessage ){
									OpenSPCoop2RestJsonMessage msg = responseMessage.castAsRestJson();
									if(msg.hasContent() && msg.isProblemDetailsForHttpApis_RFC7807()) {
										JsonDeserializer deserializer = new JsonDeserializer();
										restProblem = deserializer.fromString(msg.getContent(), false);
									}
								}
								else if(responseMessage instanceof OpenSPCoop2RestXmlMessage ){
									OpenSPCoop2RestXmlMessage msg = responseMessage.castAsRestXml();
									if(msg.hasContent() && msg.isProblemDetailsForHttpApis_RFC7807()) {
										XmlDeserializer deserializer = new XmlDeserializer();
										restProblem = deserializer.fromNode(msg.getContent(), false);
									}
								}
							}
						}
					}

				}
				catch(Throwable e) {
					
					pddContext.addObject(org.openspcoop2.core.constants.Costanti.ERRORE_TRASFORMAZIONE_RISPOSTA, "true");
					
					// prima emetto diagnostico di fine connettore
					StringBuilder bfMsgErroreSituazioneAnomale = new StringBuilder();
					EsitoElaborazioneMessaggioTracciato esitoTraccia = gestioneTracciamentoFineConnettore(errorConsegna, 
							soapFault, restProblem, 
							traduttore, msgDiag, motivoErroreConsegna, 
							responseMessage, isBlockedTransaction_responseMessageWithTransportCodeError,
							functionAsRouter, sendRispostaApplicativa, bfMsgErroreSituazioneAnomale);
					if(esitoTraccia!=null) {
						tracciamento.registraRichiesta(requestMessageTrasformato,securityInfo,headerBustaRichiesta,bustaRichiesta,esitoTraccia,
								Tracciamento.createLocationString(false, location),
								idCorrelazioneApplicativa);
					}
					
					msgDiag.addKeywordErroreProcessamento(e);
					msgDiag.logPersonalizzato("trasformazione.processamentoRispostaInErrore");
					
					ErroreIntegrazione erroreIntegrazione = gestoreTrasformazioni.getErrore();
					if(erroreIntegrazione==null) {
						erroreIntegrazione = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
								get5XX_ErroreProcessamento(e,CodiceErroreIntegrazione.CODICE_562_TRASFORMAZIONE);
					}
					
					if(sendRispostaApplicativa){
						
						IntegrationFunctionError integrationFunctionError = IntegrationFunctionError.TRANSFORMATION_RULE_RESPONSE_FAILED;
						if(e instanceof GestoreTrasformazioniException && (((GestoreTrasformazioniException)e).getOp2IntegrationFunctionError()!=null)) {
							integrationFunctionError = ((GestoreTrasformazioniException)e).getOp2IntegrationFunctionError();
						}
						
						OpenSPCoop2Message responseMessageError = null;
						if(e instanceof GestoreTrasformazioniException && (((GestoreTrasformazioniException)e).getOpenSPCoop2ErrorMessage()!=null)) {
							responseMessageError = ((GestoreTrasformazioniException)e).getOpenSPCoop2ErrorMessage();
						}
						else {
							responseMessageError = this.generatoreErrore.build(pddContext,integrationFunctionError,
									erroreIntegrazione,e,
										(responseMessage!=null ? responseMessage.getParseException() : null));
						}
						ejbUtils.sendRispostaApplicativaErrore(responseMessageError,richiestaDelegata,rollbackRichiesta,pd,sa);
						esito.setEsitoInvocazione(true);
						esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,
								"Trasformazione-Risposta");
					}else{
						// Se Non e' attivo una gestione dei riscontri, faccio il rollback sulla coda.
						// Altrimenti verra attivato la gestione dei riscontri che riprovera' dopo un tot.
						if(gestioneBusteNonRiscontrateAttive==false){
							ejbUtils.rollbackMessage("Trasformazione della risposta non riuscita: "+e.getMessage(), esito);
							esito.setEsitoInvocazione(false);
							esito.setStatoInvocazione(EsitoLib.ERRORE_NON_GESTITO,
									"Trasformazione-Risposta");
						}else{
							ejbUtils.updateErroreProcessamentoMessage("Trasformazione della risposta non riuscita: "+e.getMessage(), esito);
							esito.setEsitoInvocazione(true);
							esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,
									"Trasformazione-Risposta");
						}
					}
					openspcoopstate.releaseResource();
					return esito;
				}		
			}
			
			
			
			
			
			
			
			
			
			
			
			
			
			/* -------- OpenSPCoop2Message Update ------------- */
			try {
				msgDiag.mediumDebug("Aggiornamento del messaggio");
				// NOTA la versione SOAP capirla da requestMessage, la risposta puo' essere null
				NotifierInputStreamParams nParams = null;
				if(invokerNonSupportato==false){
					nParams = connectorSender.getNotifierInputStreamParamsResponse();
				}
				responseMessage = protocolFactory.createProtocolManager().updateOpenSPCoop2MessageResponse(responseMessage, 
						bustaRichiesta, nParams,
						requestMessagePrimaTrasformazione.getTransportRequestContext(),transportResponseContext,
						protocolFactory.getCachedRegistryReader(openspcoopstate.getStatoRichiesta()),
						false);
			} catch (Exception e) {
				
				if(e instanceof ProtocolException) {
					ProtocolException pe = (ProtocolException) e;
					if(pe.isForceTrace()) {
						msgDiag.mediumDebug("Tracciamento della richiesta...");
						EsitoElaborazioneMessaggioTracciato esitoTraccia = EsitoElaborazioneMessaggioTracciato.getEsitoElaborazioneConErrore(e.getMessage());
						tracciamento.registraRichiesta(requestMessageTrasformato,securityInfo,headerBustaRichiesta,bustaRichiesta,esitoTraccia,
								Tracciamento.createLocationString(false, location),
								idCorrelazioneApplicativa);
					}
				}
				
				
				msgDiag.addKeywordErroreProcessamento(e, "Aggiornamento messaggio fallito");
				msgDiag.logErroreGenerico(e,"ProtocolManager.updateOpenSPCoop2Message");
				String msgErrore = "ProtocolManager.updateOpenSPCoop2Message error: "+e.getMessage();
				this.log.error(msgErrore,e);
				if(functionAsRouter){
					ejbUtils.sendAsRispostaBustaErroreProcessamento(richiestaDelegata.getIdModuloInAttesa(),bustaRichiesta,
							ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
								get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_525_GESTIONE_FUNZIONALITA_PROTOCOLLO),
							idCorrelazioneApplicativa,idCorrelazioneApplicativaRisposta,servizioApplicativoFruitore,e,
							(responseMessage!=null ? responseMessage.getParseException() : null));
					esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,msgErrore);
					esito.setEsitoInvocazione(true);
				}else{
					if(sendRispostaApplicativa){
						OpenSPCoop2Message responseMessageError = 
								this.generatoreErrore.build(pddContext,IntegrationFunctionError.INTERNAL_RESPONSE_ERROR,
										ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
											get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_525_GESTIONE_FUNZIONALITA_PROTOCOLLO),e,
												(responseMessage!=null ? responseMessage.getParseException() : null));
						ejbUtils.sendRispostaApplicativaErrore(responseMessageError,richiestaDelegata,rollbackRichiesta,pd,sa);
						esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,msgErrore);
						esito.setEsitoInvocazione(true);
					}else{
						// Se Non e' attivo una gestione dei riscontri, faccio il rollback sulla coda.
						// Altrimenti verra attivato la gestione dei riscontri che riprovera' dopo un tot.
						if(gestioneBusteNonRiscontrateAttive==false){
							ejbUtils.rollbackMessage(msgErrore, esito);
							esito.setEsitoInvocazione(false);
							esito.setStatoInvocazioneErroreNonGestito(e);
						}else{
							ejbUtils.updateErroreProcessamentoMessage(msgErrore, esito);
							esito.setEsitoInvocazione(true);
							esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,msgErrore);
						}
					}
				}
				openspcoopstate.releaseResource();
				return esito;
			}
			
			
			
			
			
			
			
			
			
			
			
			
			Utilities.printFreeMemory("InoltroBuste - InResponseHandler...");
			
			
			
			
			/* ------------------- InResponseHandler -----------------------*/
			InResponseContext inResponseContext = null;
			if(invokerNonSupportato==false){
				try{
					inResponseContext = new InResponseContext(this.log,protocolFactory,openspcoopstate.getStatoRisposta());
					
					// Informazioni sul messaggio di riposta
					if(responseMessage!=null){
						inResponseContext.setMessaggio(responseMessage);					
					}
					
					// Informazioni sulla consegna
					inResponseContext.setErroreConsegna(motivoErroreConsegna);
					inResponseContext.setResponseHeaders(connectorSender.getHeaderTrasporto());
					inResponseContext.setReturnCode(codiceRitornato);
					
					// Altre informazioni
					if(outRequestContext.getConnettore()!=null){
						outRequestContext.getConnettore().setLocation(location); // aggiorno location ottenuta dal connettore utilizzato
					}
					inResponseContext.setConnettore(outRequestContext.getConnettore());
					inResponseContext.setDataPrimaInvocazioneConnettore(dataPrimaInvocazioneConnettore);
					inResponseContext.setDataTerminataInvocazioneConnettore(dataTerminataInvocazioneConnettore);
					inResponseContext.setDataAccettazioneRisposta(connectorSender.getDataAccettazioneRisposta());
					inResponseContext.setDataElaborazioneMessaggio(ejbUtils.getRicezioneMsgRisposta());
					inResponseContext.setProtocollo(outRequestContext.getProtocollo());
					inResponseContext.setPddContext(pddContext);
					inResponseContext.setIntegrazione(outRequestContext.getIntegrazione());
					inResponseContext.setTipoPorta(outRequestContext.getTipoPorta());
					
					// Invocazione handler
					GestoreHandlers.inResponse(inResponseContext, msgDiag, this.log);
										
				}				
				catch(Exception e){
					String msgErrore = null;
					if(e!=null && e.getMessage()!=null){
						if(e.getMessage().length()>150){
							msgErrore = e.getMessage().substring(0, 150);
						}else{
							msgErrore = e.getMessage();
						}
					}
					ErroreIntegrazione erroreIntegrazione = null;
					IntegrationFunctionError integrationFunctionError = null;
					if(e instanceof HandlerException){
						HandlerException he = (HandlerException) e;
						if(he.isEmettiDiagnostico()){
							msgDiag.logErroreGenerico(e, ((HandlerException)e).getIdentitaHandler());
						}
						msgErrore = ((HandlerException)e).getIdentitaHandler()+" error: "+msgErrore;
						if((functionAsRouter || sendRispostaApplicativa)) {
							erroreIntegrazione = he.convertToErroreIntegrazione();
							integrationFunctionError = he.getIntegrationFunctionError();
						}
					}else{
						msgDiag.logErroreGenerico(e, "InResponseHandler");
						msgErrore = "InResponseHandler error: "+msgErrore;
					}
					if(functionAsRouter){
						if(erroreIntegrazione==null){
							erroreIntegrazione = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
									get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_544_HANDLER_IN_RESPONSE);
						}
						ejbUtils.sendAsRispostaBustaErroreProcessamento(richiestaDelegata.getIdModuloInAttesa(),bustaRichiesta,
								erroreIntegrazione,
								idCorrelazioneApplicativa,idCorrelazioneApplicativaRisposta,servizioApplicativoFruitore,e,
								(responseMessage!=null ? responseMessage.getParseException() : null));
						esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,msgErrore);
						esito.setEsitoInvocazione(true);
					}else{
						if(sendRispostaApplicativa){
							if(erroreIntegrazione==null){
								erroreIntegrazione = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
										get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_544_HANDLER_IN_RESPONSE);
							}
							if(integrationFunctionError==null) {
								integrationFunctionError = IntegrationFunctionError.INTERNAL_RESPONSE_ERROR;
							}
							OpenSPCoop2Message responseMessageError = 
									this.generatoreErrore.build(pddContext,integrationFunctionError,
											erroreIntegrazione,e,
												(responseMessage!=null ? responseMessage.getParseException() : null));
							if(e instanceof HandlerException){
								HandlerException he = (HandlerException) e;
								he.customized(responseMessageError);
							}
							ejbUtils.sendRispostaApplicativaErrore(responseMessageError,richiestaDelegata,rollbackRichiesta,pd,sa);
							esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,msgErrore);
							esito.setEsitoInvocazione(true);
						}else{
							// Se Non e' attivo una gestione dei riscontri, faccio il rollback sulla coda.
							// Altrimenti verra attivato la gestione dei riscontri che riprovera' dopo un tot.
							if(gestioneBusteNonRiscontrateAttive==false){
								ejbUtils.rollbackMessage(msgErrore, esito);
								esito.setEsitoInvocazione(false);
								esito.setStatoInvocazioneErroreNonGestito(e);
							}else{
								ejbUtils.updateErroreProcessamentoMessage(msgErrore, esito);
								esito.setEsitoInvocazione(true);
								esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,msgErrore);
							}
						}
					}
					openspcoopstate.releaseResource();
					return esito;
				}
			}
			
			
			
			
			
			Utilities.printFreeMemory("InoltroBuste - Messaggio di Risposta + Dump...");
			
			
			
			
			
			// --------------------- Messaggio di Risposta + Dump --------------------------
			if(invokerNonSupportato==false){
				
				// Leggo informazioni di trasporto
				codiceRitornato = inResponseContext.getReturnCode();
				motivoErroreConsegna = inResponseContext.getErroreConsegna();
				responseMessage = inResponseContext.getMessaggio();
			
				// dump applicativo
				if(!dumpRispostaEffettuato && responseMessage!=null ){
					Dump dumpApplicativo = new Dump(identitaPdD,InoltroBuste.ID_MODULO,idMessageRequest,
							soggettoFruitore,idServizio,tipoPdD,msgDiag.getPorta(),pddContext,
							openspcoopstate.getStatoRichiesta(),openspcoopstate.getStatoRisposta(),
							dumpConfig);
					dumpApplicativo.dumpRispostaIngresso(responseMessage, inResponseContext.getConnettore(), inResponseContext.getResponseHeaders());
				}
				
			}

			
			
			
			
			
			




			/* ------------  Tracciamento Richiesta e Messaggio Diagnostico ------------- */
			if(invokerNonSupportato==false){// && errorConsegna==false){

				StringBuilder bfMsgErroreSituazioneAnomale = new StringBuilder();
				EsitoElaborazioneMessaggioTracciato esitoTraccia = gestioneTracciamentoFineConnettore(errorConsegna, 
						soapFault, restProblem,
						traduttore, msgDiag, motivoErroreConsegna, 
						responseMessage, isBlockedTransaction_responseMessageWithTransportCodeError,
						functionAsRouter, sendRispostaApplicativa, bfMsgErroreSituazioneAnomale);
				if(esitoTraccia!=null) {
					tracciamento.registraRichiesta(requestMessageTrasformato,securityInfo,headerBustaRichiesta,bustaRichiesta,esitoTraccia,
							Tracciamento.createLocationString(false, location),
							idCorrelazioneApplicativa);
				}
				String msgErroreSituazioneAnomale = null;
				if(bfMsgErroreSituazioneAnomale.length()>0) {
					msgErroreSituazioneAnomale = bfMsgErroreSituazioneAnomale.toString();
				}

				// Dopo che ho effettuata la tracciatura ritorno errore se necessario
				if(msgErroreSituazioneAnomale!=null){
					if(functionAsRouter){		
						ejbUtils.sendAsRispostaBustaErroreProcessamento(richiestaDelegata.getIdModuloInAttesa(),bustaRichiesta,
								ErroriIntegrazione.ERRORE_559_RICEVUTA_RISPOSTA_CON_ERRORE_TRASPORTO.
									get559_RicevutaRispostaConErroreTrasporto(msgErroreSituazioneAnomale),
								idCorrelazioneApplicativa,idCorrelazioneApplicativaRisposta,servizioApplicativoFruitore,null,
								(responseMessage!=null ? responseMessage.getParseException() : null));
						esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,msgErroreSituazioneAnomale);
						esito.setEsitoInvocazione(true);
					}else{
						if(sendRispostaApplicativa){
							OpenSPCoop2Message responseMessageError = 
									this.generatoreErrore.build(pddContext,IntegrationFunctionError.BAD_RESPONSE,
											ErroriIntegrazione.ERRORE_559_RICEVUTA_RISPOSTA_CON_ERRORE_TRASPORTO.
												get559_RicevutaRispostaConErroreTrasporto(msgErroreSituazioneAnomale),null,
													(responseMessage!=null ? responseMessage.getParseException() : null));
							ejbUtils.sendRispostaApplicativaErrore(responseMessageError,richiestaDelegata,rollbackRichiesta,pd,sa);
							esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,msgErroreSituazioneAnomale);
							esito.setEsitoInvocazione(true);
						}else{
							// Se Non e' attivo una gestione dei riscontri, faccio il rollback sulla coda.
							// Altrimenti verra attivato la gestione dei riscontri che riprovera' dopo un tot.
							if(gestioneBusteNonRiscontrateAttive==false){
								ejbUtils.rollbackMessage(msgErroreSituazioneAnomale, esito);
								esito.setEsitoInvocazione(false);
								esito.setStatoInvocazione(EsitoLib.ERRORE_NON_GESTITO,msgErroreSituazioneAnomale);
							}else{
								ejbUtils.updateErroreProcessamentoMessage(msgErroreSituazioneAnomale, esito);
								esito.setEsitoInvocazione(true);
								esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,msgErroreSituazioneAnomale);
							}
						}
					}
					openspcoopstate.releaseResource();
					return esito;
				}
			}

			
			
			
			
			
			
			
			
		
			



			Utilities.printFreeMemory("InoltroBuste - Analisi della risposta (validazione sintattica/semantica)...");





			/* ------------- Analisi di una risposta ritornata ------------*/
			boolean presenzaRispostaProtocollo = false;
			SecurityInfo securityInfoResponse = null;
			
			boolean sbustamentoInformazioniProtocolloRispostaDopoCorrelazione = false;
			org.openspcoop2.protocol.engine.builder.Sbustamento sbustatore = null;
			Busta bustaRisposta = null;
			boolean sbustamentoManifestRisposta = false;
			
			if(responseMessage != null ){
				msgDiag.mediumDebug("Analisi della risposta (validazione sintattica)...");

				// ValidazioneSintattica
				ProprietaValidazione property = new ProprietaValidazione();
				property.setValidazioneConSchema(configurazionePdDManager.isLivelloValidazioneRigido(implementazionePdDDestinatario));
				property.setValidazioneProfiloCollaborazione(configurazionePdDManager.isValidazioneProfiloCollaborazione(implementazionePdDDestinatario));
				property.setValidazioneManifestAttachments(
						configurazionePdDManager.isValidazioneManifestAttachments(implementazionePdDDestinatario) &&
						gestioneManifest);
				//validator = new ValidatoreSPCoop(responseMessage,property,openspcoopstate.getStatoRichiesta(),readQualifiedAttribute);
				if(openspcoopstate.getStatoRisposta() instanceof StatelessMessage){
					((StatelessMessage) openspcoopstate.getStatoRisposta()).setBustaCorrelata(bustaRichiesta);
				}
				if(openspcoopstate!=null) {
					property.setRuntimeState(openspcoopstate.getStatoRisposta());
					if(this.propertiesReader.isTransazioniUsePddRuntimeDatasource()) {
						property.setTracceState(openspcoopstate.getStatoRisposta());
					}
				}
				validatore = new Validatore(responseMessage,pddContext,property,openspcoopstate.getStatoRisposta(),readQualifiedAttribute, protocolFactory);
						
				msgDiag.logPersonalizzato("validazioneSintattica");
				presenzaRispostaProtocollo  = validatore.validazioneSintattica(bustaRichiesta, Boolean.FALSE);
				if(presenzaRispostaProtocollo){
					headerProtocolloRisposta = validatore.getHeaderProtocollo();
					msgDiag.addKeywords(validatore.getBusta(), false);
					if(validatore.getErroreProcessamento_internalMessage()!=null) {
						msgDiag.logErroreGenerico(validatore.getErroreProcessamento_internalMessage(), "Validazione Risposta");
					}
				}else{
					if(validatore.getErrore()!=null){
						this.log.debug("Messaggio non riconosciuto come busta ("+traduttore.toString(validatore.getErrore().getCodiceErrore())
								+"): "+validatore.getErrore().getDescrizione(protocolFactory));
					}
				}
				
				if(presenzaRispostaProtocollo){
					try{
						// ulteriore controllo per evitare che il protocollo trasparente generi una busta di risposta per il profilo oneway
						presenzaRispostaProtocollo = protocolFactory.createValidazioneSintattica(openspcoopstate.getStatoRisposta()).
								verifyProtocolPresence(tipoPdD,bustaRichiesta.getProfiloDiCollaborazione(),RuoloMessaggio.RISPOSTA,responseMessage);
					} catch (Exception e){
						this.log.debug("Messaggio non riconosciuto come busta: "+e.getMessage());
						presenzaRispostaProtocollo = false;
					} 
				}
								
				if(functionAsRouter==false && presenzaRispostaProtocollo ){		

					
					
					/* *** Init MTOM Processor / SecurityContext *** */ 
					mtomProcessor = null;
					messageSecurityConfig = null;
					msgDiag.mediumDebug("init MTOM Processor / SecurityContext ...");
					ErroreIntegrazione erroreIntegrazioneConfig = null;
					Exception configException = null;
					String oggetto = null;
						
					MTOMProcessorConfig mtomConfig = null;
					try{
						mtomConfig=configurazionePdDManager.getPD_MTOMProcessorForReceiver(pd);
					}catch(Exception e){
						oggetto = "LetturaConfigurazioneMTOMProcessorRoleReceiver";
						erroreIntegrazioneConfig = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
								get5XX_ErroreProcessamento(e,CodiceErroreIntegrazione.CODICE_536_CONFIGURAZIONE_NON_DISPONIBILE);
						configException = e;
					}					
					try{
						messageSecurityConfig=configurazionePdDManager.getPD_MessageSecurityForReceiver(pd);
					}catch(Exception e){
						oggetto = "LetturaConfigurazioneMessageSecurityRoleReceiver";
						erroreIntegrazioneConfig = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
								get5XX_ErroreProcessamento(e,CodiceErroreIntegrazione.CODICE_536_CONFIGURAZIONE_NON_DISPONIBILE);
						configException = e;
					}
						
					if(erroreIntegrazioneConfig!=null){
						msgDiag.logErroreGenerico(configException, oggetto);
					
						if(sendRispostaApplicativa){
							OpenSPCoop2Message responseMessageError = 
									this.generatoreErrore.build(pddContext,IntegrationFunctionError.INTERNAL_RESPONSE_ERROR,
											erroreIntegrazioneConfig,configException,
												(responseMessage!=null ? responseMessage.getParseException() : null));
							ejbUtils.sendRispostaApplicativaErrore(responseMessageError,richiestaDelegata,rollbackRichiesta,pd,sa);
							esito.setEsitoInvocazione(true);
							esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,configException.getMessage());
						} else{
							// Se Non e' attivo una gestione dei riscontri, faccio il rollback sulla coda.
							// Altrimenti verra attivato la gestione dei riscontri che riprovera' dopo un tot.
							if(gestioneBusteNonRiscontrateAttive==false){
								ejbUtils.rollbackMessage(oggetto+" non riuscita: "+configException.getMessage(), esito);
								esito.setStatoInvocazioneErroreNonGestito(configException);
								esito.setEsitoInvocazione(false);
							}else{
								ejbUtils.updateErroreProcessamentoMessage(oggetto+" non riuscita: "+configException.getMessage(), esito);
								esito.setEsitoInvocazione(true);
								esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,configException.getMessage());
							}
						}
						openspcoopstate.releaseResource();
						return esito;
					}
					else{
						mtomProcessor = new MTOMProcessor(mtomConfig, messageSecurityConfig, 
								tipoPdD, msgDiag, this.log, pddContext);
					}
					
					
					
					
					/* *** MTOM Processor BeforeSecurity  *** */
					if(mtomProcessor!=null){
						try{
							mtomProcessor.mtomBeforeSecurity(responseMessage, RuoloMessaggio.RISPOSTA);
						}catch(Exception e){
							// L'errore viene registrato dentro il metodo mtomProcessor.mtomBeforeSecurity
							// msgDiag.logErroreGenerico(e,"MTOMProcessor(BeforeSec-"+mtomProcessor.getMTOMProcessorType()+")");
						
							ErroreIntegrazione erroreIntegrazione = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
									get5XX_ErroreProcessamento(e,CodiceErroreIntegrazione.CODICE_557_MTOM_PROCESSOR_ERROR);
							if(sendRispostaApplicativa){
								OpenSPCoop2Message responseMessageError = this.generatoreErrore.build(pddContext,IntegrationFunctionError.ATTACHMENTS_PROCESSING_RESPONSE_FAILED,
										erroreIntegrazione,e,
											(responseMessage!=null ? responseMessage.getParseException() : null));
								ejbUtils.sendRispostaApplicativaErrore(responseMessageError,richiestaDelegata,rollbackRichiesta,pd,sa);
								esito.setEsitoInvocazione(true);
								esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,
										"MTOMProcessor(BeforeSec-"+mtomProcessor.getMTOMProcessorType()+")");
							}else{
								// Se Non e' attivo una gestione dei riscontri, faccio il rollback sulla coda.
								// Altrimenti verra attivato la gestione dei riscontri che riprovera' dopo un tot.
								if(gestioneBusteNonRiscontrateAttive==false){
									ejbUtils.rollbackMessage("MTOMProcessor(BeforeSec-"+mtomProcessor.getMTOMProcessorType()+") non riuscita: "+e.getMessage(), esito);
									esito.setStatoInvocazioneErroreNonGestito(e);
									esito.setEsitoInvocazione(false);
								}else{
									ejbUtils.updateErroreProcessamentoMessage("MTOMProcessor(BeforeSec-"+mtomProcessor.getMTOMProcessorType()+") non riuscita: "+e.getMessage(), esito);
									esito.setEsitoInvocazione(true);
									esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,
											"MTOMProcessor(BeforeSec-"+mtomProcessor.getMTOMProcessorType()+")");
								}
							}
							openspcoopstate.releaseResource();
							return esito;
						}
					}
					
					
										

					
										
					/* *** Init context sicurezza *** */
					if(messageSecurityConfig!=null && messageSecurityConfig.getFlowParameters()!=null
							&& messageSecurityConfig.getFlowParameters().size()>0){
						
						try{							
							msgDiag.mediumDebug("Inizializzazione contesto di Message Security della risposta ...");
														
							if(messageSecurityContext==null){
								// se non vi era la richiesta di MessageSecurity
								MessageSecurityContextParameters contextParameters = new MessageSecurityContextParameters();
								contextParameters.setUseActorDefaultIfNotDefined(this.propertiesReader.isGenerazioneActorDefault(implementazionePdDDestinatario));
								contextParameters.setActorDefault(this.propertiesReader.getActorDefault(implementazionePdDDestinatario));
								contextParameters.setLog(this.log);
								contextParameters.setFunctionAsClient(SecurityConstants.SECURITY_SERVER);
								contextParameters.setPrefixWsuId(this.propertiesReader.getPrefixWsuId());
								contextParameters.setRemoveAllWsuIdRef(this.propertiesReader.isRemoveAllWsuIdRef());
								contextParameters.setIdFruitore(soggettoFruitore);
								contextParameters.setIdServizio(idServizio);
								contextParameters.setPddFruitore(registroServiziManager.getIdPortaDominio(soggettoFruitore, null));
								contextParameters.setPddErogatore(registroServiziManager.getIdPortaDominio(idServizio.getSoggettoErogatore(), null));
								messageSecurityContext = new MessageSecurityFactory().getMessageSecurityContext(contextParameters);
							}
							messageSecurityContext.setIncomingProperties(messageSecurityConfig.getFlowParameters());  
							messageSecurityContext.setFunctionAsClient(SecurityConstants.SECURITY_SERVER);
							
							String tipoSicurezza = SecurityConstants.convertActionToString(messageSecurityContext.getIncomingProperties());
							msgDiag.addKeyword(CostantiPdD.KEY_TIPO_SICUREZZA_MESSAGGIO_RISPOSTA, tipoSicurezza);
							pddContext.addObject(CostantiPdD.KEY_TIPO_SICUREZZA_MESSAGGIO_RISPOSTA, tipoSicurezza);
							
							msgDiag.mediumDebug("Inizializzazione contesto di Message Security della richiesta completata con successo");
							
							if(responseMessage!=null && org.openspcoop2.security.message.engine.WSSUtilities.isNormalizeToSaajImpl(messageSecurityContext)){
								msgDiag.mediumDebug("Normalize Response to saajImpl");
								//System.out.println("InoltroBusteEgov.response.normalize");
								responseMessage = responseMessage.normalizeToSaajImpl();
								
								validatore.updateMsg(responseMessage);
							}
							
						}catch(Exception e){
							msgDiag.logErroreGenerico(e,"InizializzazioneContestoSicurezzaRisposta");
							if(sendRispostaApplicativa){
								OpenSPCoop2Message responseMessageError = 
										this.generatoreErrore.build(pddContext,IntegrationFunctionError.INTERNAL_RESPONSE_ERROR,
												ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
													get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_536_CONFIGURAZIONE_NON_DISPONIBILE),e,
														(responseMessage!=null ? responseMessage.getParseException() : null));
								ejbUtils.sendRispostaApplicativaErrore(responseMessageError,richiestaDelegata,rollbackRichiesta,pd,sa);
								esito.setEsitoInvocazione(true);
								esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,
										"InizializzazioneContestoSicurezzaRisposta");
							} else{
								// Se Non e' attivo una gestione dei riscontri, faccio il rollback sulla coda.
								// Altrimenti verra attivato la gestione dei riscontri che riprovera' dopo un tot.
								if(gestioneBusteNonRiscontrateAttive==false){
									ejbUtils.rollbackMessage("Errore durante la lettura delle proprieta' di MessageSecurity per la risposta: "+e.getMessage(), esito);
									esito.setStatoInvocazioneErroreNonGestito(e);
									esito.setEsitoInvocazione(false);
								}else{
									ejbUtils.updateErroreProcessamentoMessage("Errore durante la lettura delle proprieta' di MessageSecurity per la risposta: "+e.getMessage(), esito);
									esito.setEsitoInvocazione(true);
									esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,
											"InizializzazioneContestoSicurezzaRisposta");
								}
							}
							openspcoopstate.releaseResource();
							return esito;
						}
					}
					else{
						msgDiag.logPersonalizzato("messageSecurity.processamentoRispostaDisabilitato");
					}
					
					
					
					/* *** ReadSecurityInformation *** */
					try{
						IDigestReader digestReader = null;
						if(messageSecurityContext != null) {
							digestReader = messageSecurityContext.getDigestReader(responseMessage!=null ? responseMessage.getFactory() : OpenSPCoop2MessageFactory.getDefaultMessageFactory());
						}
						if(digestReader!=null){
							msgDiag.mediumDebug("Lettura informazioni sulla Sicurezza dal Messaggio di risposta ...");
							securityInfoResponse = validazioneSemantica.readSecurityInformation(digestReader,responseMessage);
							msgDiag.mediumDebug("Lettura informazioni sulla Sicurezza dal Messaggio di risposta completata con successo");
						}
					}catch(Exception e){
						msgDiag.logErroreGenerico(e,"ErroreLetturaInformazioniSicurezza");
						if(sendRispostaApplicativa){
							OpenSPCoop2Message responseMessageError = 
									this.generatoreErrore.build(pddContext,IntegrationFunctionError.MESSAGE_SECURITY_RESPONSE_FAILED,
											ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
												get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_549_SECURITY_INFO_READER_ERROR),e,
													(responseMessage!=null ? responseMessage.getParseException() : null));
							ejbUtils.sendRispostaApplicativaErrore(responseMessageError,richiestaDelegata,rollbackRichiesta,pd,sa);
							esito.setEsitoInvocazione(true);
							esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,
									"ErroreLetturaInformazioniSicurezza");
						} else{
							// Se Non e' attivo una gestione dei riscontri, faccio il rollback sulla coda.
							// Altrimenti verra attivato la gestione dei riscontri che riprovera' dopo un tot.
							if(gestioneBusteNonRiscontrateAttive==false){
								ejbUtils.rollbackMessage("Errore durante la lettura dele informazioni di sicurezza per la risposta: "+e.getMessage(), esito);
								esito.setStatoInvocazioneErroreNonGestito(e);
								esito.setEsitoInvocazione(false);
							}else{
								ejbUtils.updateErroreProcessamentoMessage("Errore durante la lettura dele informazioni di sicurezza per la risposta: "+e.getMessage(), esito);
								esito.setEsitoInvocazione(true);
								esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,
										"ErroreLetturaInformazioniSicurezza");
							}
						}
						openspcoopstate.releaseResource();
						return esito;
					}
					
					
					
					
					/* *** ValidazioneSemantica (e applicazione sicurezza del messaggio) *** */
					
					msgDiag.logPersonalizzato("validazioneSemantica.beforeSecurity");
					presenzaRispostaProtocollo = validatore.validazioneSemantica_beforeMessageSecurity(requestInfo.getProtocolServiceBinding(),true, profiloGestione);
					
					if(validatore.isRilevatiErroriDuranteValidazioneSemantica()==false){
						
						if(messageSecurityContext!= null && messageSecurityContext.getIncomingProperties() != null && messageSecurityContext.getIncomingProperties().size() > 0){
						
							msgDiag.logPersonalizzato("messageSecurity.processamentoRispostaInCorso");
							
							StringBuilder bfErroreSecurity = new StringBuilder();
							presenzaRispostaProtocollo = validatore.validazioneSemantica_messageSecurity_process(messageSecurityContext, bfErroreSecurity,
									transactionNullable!=null ? transactionNullable.getTempiElaborazione() : null,
											false);
							
							if(bfErroreSecurity.length()>0){
								msgDiag.addKeyword(CostantiPdD.KEY_ERRORE_PROCESSAMENTO , bfErroreSecurity.toString() );
								msgDiag.logPersonalizzato("messageSecurity.processamentoRispostaInErrore");
							}
							else{
								msgDiag.logPersonalizzato("messageSecurity.processamentoRispostaEffettuato");
							}
						}
					
						if(validatore.isRilevatiErroriDuranteValidazioneSemantica()==false){
							
							msgDiag.logPersonalizzato("validazioneSemantica.afterSecurity");
							presenzaRispostaProtocollo = validatore.validazioneSemantica_afterMessageSecurity(proprietaManifestAttachments, validazioneIDBustaCompleta);
					
						}
					}
					
					
					
					/* *** MTOM Processor AfterSecurity  *** */
					if(mtomProcessor!=null){
						try{
							mtomProcessor.mtomAfterSecurity(responseMessage, RuoloMessaggio.RISPOSTA);
						}catch(Exception e){
							// L'errore viene registrato dentro il metodo mtomProcessor.mtomAfterSecurity
							// msgDiag.logErroreGenerico(e,"MTOMProcessor(AfterSec-"+mtomProcessor.getMTOMProcessorType()+")");
						
							ErroreIntegrazione erroreIntegrazione = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
									get5XX_ErroreProcessamento(e,CodiceErroreIntegrazione.CODICE_557_MTOM_PROCESSOR_ERROR);
							if(sendRispostaApplicativa){
								OpenSPCoop2Message responseMessageError = this.generatoreErrore.build(pddContext,IntegrationFunctionError.ATTACHMENTS_PROCESSING_RESPONSE_FAILED,
										erroreIntegrazione,e,
											(responseMessage!=null ? responseMessage.getParseException() : null));
								ejbUtils.sendRispostaApplicativaErrore(responseMessageError,richiestaDelegata,rollbackRichiesta,pd,sa);
								esito.setEsitoInvocazione(true);
								esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,
										"MTOMProcessor(AfterSec-"+mtomProcessor.getMTOMProcessorType()+")");
							}else{
								// Se Non e' attivo una gestione dei riscontri, faccio il rollback sulla coda.
								// Altrimenti verra attivato la gestione dei riscontri che riprovera' dopo un tot.
								if(gestioneBusteNonRiscontrateAttive==false){
									ejbUtils.rollbackMessage("MTOMProcessor(AfterSec-"+mtomProcessor.getMTOMProcessorType()+") non riuscita: "+e.getMessage(), esito);
									esito.setStatoInvocazioneErroreNonGestito(e);
									esito.setEsitoInvocazione(false);
								}else{
									ejbUtils.updateErroreProcessamentoMessage("MTOMProcessor(AfterSec-"+mtomProcessor.getMTOMProcessorType()+") non riuscita: "+e.getMessage(), esito);
									esito.setEsitoInvocazione(true);
									esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,
											"MTOMProcessor(AfterSec-"+mtomProcessor.getMTOMProcessorType()+")");
								}
							}
							openspcoopstate.releaseResource();
							return esito;
						}
					}
					
				}
					
				if(presenzaRispostaProtocollo ){		
					
					// Qua dentro ci entro sia se sono router, che se è ancora rilevata la presenza di un protocollo dopo la validazione semantica
					
					// Estrazione header busta
					msgDiag.mediumDebug("Sbustamento della risposta...");
					bustaRisposta = validatore.getBusta();
					try{
						boolean isMessaggioErroreProtocollo = validatore.isErroreProtocollo();
						
						boolean gestioneManifestRisposta = false;
						if(functionAsRouter==false && !isMessaggioErroreProtocollo){
							gestioneManifestRisposta = configurazionePdDManager.isGestioneManifestAttachments(pd,protocolFactory);
							
							List<Eccezione> erroriValidazione = validatore.getEccezioniValidazione();
							
							sbustatore = 
									new org.openspcoop2.protocol.engine.builder.Sbustamento(protocolFactory, openspcoopstate.getStatoRisposta());
							
							// GestioneManifest solo se ho ricevuto una busta correttamente formata nel manifest
							sbustamentoManifestRisposta = gestioneManifestRisposta;
							for(int k = 0; k < erroriValidazione.size() ; k++){
								Eccezione er = erroriValidazione.get(k);
								if(CodiceErroreCooperazione.isEccezioneAllegati(er.getCodiceEccezione())){
									sbustamentoManifestRisposta = false;
								}
							}	
							msgDiag.highDebug("Tipo Messaggio Risposta prima dello sbustamento ["+FaseSbustamento.POST_VALIDAZIONE_SEMANTICA_RISPOSTA
									+"] ["+responseMessage.getClass().getName()+"]");
							ProtocolMessage protocolMessage = sbustatore.sbustamento(responseMessage,pddContext,
									bustaRisposta,
									RuoloMessaggio.RISPOSTA,sbustamentoManifestRisposta,proprietaManifestAttachments,
									FaseSbustamento.POST_VALIDAZIONE_SEMANTICA_RISPOSTA, requestInfo);
							if(protocolMessage!=null) {
								if(!protocolMessage.isUseBustaRawContentReadByValidation()) {
									headerProtocolloRisposta = protocolMessage.getBustaRawContent();
								}
								responseMessage = protocolMessage.getMessage(); // updated
							}
							msgDiag.highDebug("Tipo Messaggio Risposta dopo lo sbustamento ["+FaseSbustamento.POST_VALIDAZIONE_SEMANTICA_RISPOSTA
									+"] ["+responseMessage.getClass().getName()+"]");		
							
		
							// Spostato dopo correlazione applicativa
							if(sbustamentoInformazioniProtocolloRisposta){
								// effettuo lo stesso sbustamento invocandolo con la nuova fase
								// Questa invocazione andrebbe implementata su ricezionecontenutiApplicativi teoricamente
								sbustamentoInformazioniProtocolloRispostaDopoCorrelazione = true;
//								msgDiag.highDebug("Tipo Messaggio Risposta prima dello sbustamento ["+FaseSbustamento.PRE_CONSEGNA_RISPOSTA
//										+"] ["+responseMessage.getClass().getName()+"]");
//								protocolMessage = sbustatore.sbustamento(responseMessage,bustaRisposta,
//										RuoloMessaggio.RISPOSTA,sbustamentoManifestRisposta,proprietaManifestAttachments,
//										FaseSbustamento.PRE_CONSEGNA_RISPOSTA, requestInfo);
//								headerProtocolloRisposta = protocolMessage.getBustaRawContent();
//								responseMessage = protocolMessage.getMessage(); // updated
//								msgDiag.highDebug("Tipo Messaggio Risposta dopo lo sbustamento ["+FaseSbustamento.PRE_CONSEGNA_RISPOSTA
//										+"] ["+responseMessage.getClass().getName()+"]");	
							}
							
						}else{
							headerProtocolloRisposta = validatore.getHeaderProtocollo();
						}
					}catch(Exception e){
						if(functionAsRouter==false){
							msgDiag.logErroreGenerico(e,"sbustatore.sbustamento("+bustaRisposta.getID()+")");
						}else{
							msgDiag.logErroreGenerico(e,"validator.getHeader("+bustaRisposta.getID()+")");
						}
												
						EsitoElaborazioneMessaggioTracciato esitoTraccia = EsitoElaborazioneMessaggioTracciato.getEsitoElaborazioneConErrore("Errore durante lo sbustamento della risposta: "+e.getMessage());
						tracciamento.registraRisposta(responseMessage,securityInfoResponse,headerProtocolloRisposta,bustaRisposta,esitoTraccia,
								Tracciamento.createLocationString(true, location),
								idCorrelazioneApplicativa,idCorrelazioneApplicativaRisposta); // non ancora registrata
						
						if(functionAsRouter){
							ejbUtils.sendAsRispostaBustaErroreProcessamento(richiestaDelegata.getIdModuloInAttesa(),
									bustaRichiesta,
									ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
										get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_527_GESTIONE_SBUSTAMENTO),
									idCorrelazioneApplicativa,idCorrelazioneApplicativaRisposta,servizioApplicativoFruitore,e,
									(responseMessage!=null ? responseMessage.getParseException() : null));
							esito.setEsitoInvocazione(true);
							esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,
									"Sbustamento non riuscito");
						}else{
													
							ErroreIntegrazione erroreIntegrazione = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
									get5XX_ErroreProcessamento(e,CodiceErroreIntegrazione.CODICE_527_GESTIONE_SBUSTAMENTO);
							if(sendRispostaApplicativa){
								OpenSPCoop2Message responseMessageError = this.generatoreErrore.build(pddContext,IntegrationFunctionError.INTERNAL_RESPONSE_ERROR,
										erroreIntegrazione,e,
											(responseMessage!=null ? responseMessage.getParseException() : null));
								ejbUtils.sendRispostaApplicativaErrore(responseMessageError,richiestaDelegata,rollbackRichiesta,pd,sa);
								esito.setEsitoInvocazione(true);
								esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,"Sbustamento non riuscito");
							}else{
								// Se Non e' attivo una gestione dei riscontri, faccio il rollback sulla coda.
								// Altrimenti verra attivato la gestione dei riscontri che riprovera' dopo un tot.
								if(gestioneBusteNonRiscontrateAttive==false){
									ejbUtils.rollbackMessage("Sbustamento non riuscito", esito);
									esito.setStatoInvocazioneErroreNonGestito(e);
									esito.setEsitoInvocazione(false);
								}else{
									ejbUtils.updateErroreProcessamentoMessage("Sbustamento non riuscito", esito);
									esito.setEsitoInvocazione(true);
									esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,"Sbustamento non riuscito");
								}
							}
							
						}
						openspcoopstate.releaseResource();
						return esito;

					}
				}
			}





			Utilities.printFreeMemory("InoltroBuste - Gestione errore consegna della risposta...");
			boolean faultLogged = false;


			/* ------------------------- Gestione Errori Consegna ---------------------------- */
			msgDiag.mediumDebug("Gestione errore consegna della risposta...");
			// Invoker Non Supportato
			if(invokerNonSupportato == true){
				String connettoreNonSupportato = "Connettore non supportato [tipo:"+tipoConnector+" class:"+connectorClass+"]";
				if(functionAsRouter){
					ejbUtils.sendAsRispostaBustaErroreProcessamento(richiestaDelegata.getIdModuloInAttesa(),bustaRichiesta,
							ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
								get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_515_CONNETTORE_NON_REGISTRATO),
							idCorrelazioneApplicativa,idCorrelazioneApplicativaRisposta,servizioApplicativoFruitore,eInvokerNonSupportato,
							(responseMessage!=null ? responseMessage.getParseException() : null));
					esito.setEsitoInvocazione(true);
					esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,connettoreNonSupportato);
				}else{
					if(sendRispostaApplicativa){
						OpenSPCoop2Message responseMessageError = 
								this.generatoreErrore.build(pddContext,IntegrationFunctionError.INTERNAL_REQUEST_ERROR,
										ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
											get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_515_CONNETTORE_NON_REGISTRATO),eInvokerNonSupportato,
												(responseMessage!=null ? responseMessage.getParseException() : null));
						ejbUtils.sendRispostaApplicativaErrore(responseMessageError,richiestaDelegata,rollbackRichiesta,pd,sa);
						esito.setEsitoInvocazione(true);
						esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,connettoreNonSupportato);
					} else{
						// Se Non e' attivo una gestione dei riscontri, faccio il rollback sulla coda.
						// Altrimenti verra attivato la gestione dei riscontri che riprovera' dopo un tot.
						if(gestioneBusteNonRiscontrateAttive==false){
							ejbUtils.rollbackMessage(connettoreNonSupportato, esito);
							esito.setEsitoInvocazione(false);
							esito.setStatoInvocazione(EsitoLib.ERRORE_NON_GESTITO,connettoreNonSupportato);
						}else{
							ejbUtils.updateErroreProcessamentoMessage("Connettore non supportato [tipo:"+tipoConnector+" class:"+connectorClass+"].", esito);
							esito.setEsitoInvocazione(true);
							esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,connettoreNonSupportato);
						}
					}
				}
				openspcoopstate.releaseResource();
				return esito;
			}

			// Errori avvenuti durante la consegna (senza che cmq sia pervenuta una busta)
			else if(errorConsegna && presenzaRispostaProtocollo==false){
				String erroreConnettore = "Consegna ["+tipoConnector+"] con errore: "+motivoErroreConsegna;
				if(functionAsRouter){
					if(responseMessage==null){
						//	Genero una risposta di errore, poiche' non presente
						IntegrationFunctionError integrationFunctionError = getIntegrationFunctionErroreConnectionError(eccezioneProcessamentoConnettore, motivoErroreConsegna, pddContext);
						ejbUtils.setIntegrationFunctionErrorPortaApplicativa(integrationFunctionError);
						ejbUtils.sendAsRispostaBustaErroreProcessamento(richiestaDelegata.getIdModuloInAttesa(),bustaRichiesta,
								ErroriIntegrazione.ERRORE_516_CONNETTORE_UTILIZZO_CON_ERRORE.
									get516_PortaDiDominioNonDisponibile(bustaRichiesta.getTipoDestinatario()+"-"+bustaRichiesta.getDestinatario()),
								idCorrelazioneApplicativa,idCorrelazioneApplicativaRisposta,servizioApplicativoFruitore,eccezioneProcessamentoConnettore,
								(responseMessage!=null ? responseMessage.getParseException() : null));
						openspcoopstate.releaseResource();
						esito.setEsitoInvocazione(true);
						esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,erroreConnettore);
						return esito;
					}
				}else{
					//	Effettuo log dell'eventuale fault (registro anche i fault spcoop, potrebbero contenere dei details aggiunti da una PdD.)
					if( soapFault!=null && faultLogged==false ){
						msgDiag.addKeyword(CostantiPdD.KEY_SOAP_FAULT, SoapUtils.safe_toString(faultMessageFactory, soapFault, this.log));
						msgDiag.logPersonalizzato("ricezioneSoapFault");
						faultLogged = true;
					}
					else if( restProblem!=null && faultLogged==false ){
						msgDiag.addKeyword(CostantiPdD.KEY_REST_PROBLEM, restProblem.getRaw());
						msgDiag.logPersonalizzato("ricezioneRestProblem");
						faultLogged = true;
					}
					if(sendRispostaApplicativa){
						if(responseMessage==null){
							
							String requestReadTimeout = null;
							String responseReadTimeout = null;
							if(pddContext!=null && pddContext.containsKey(TimeoutInputStream.ERROR_MSG_KEY)) {
								String timeoutMessage = PdDContext.getValue(TimeoutInputStream.ERROR_MSG_KEY, pddContext);
								if(timeoutMessage!=null && timeoutMessage.startsWith(CostantiPdD.PREFIX_TIMEOUT_REQUEST)) {
									requestReadTimeout = timeoutMessage;
								}
								else if(timeoutMessage!=null && timeoutMessage.startsWith(CostantiPdD.PREFIX_TIMEOUT_RESPONSE)) {
									responseReadTimeout = timeoutMessage;
								}
							}
							String requestLimitExceeded = null;
							String responseLimitExceeded = null;
							if(pddContext!=null && pddContext.containsKey(LimitedInputStream.ERROR_MSG_KEY)) {
								String limitedExceededMessage = PdDContext.getValue(LimitedInputStream.ERROR_MSG_KEY, pddContext);
								if(limitedExceededMessage!=null && limitedExceededMessage.startsWith(CostantiPdD.PREFIX_LIMITED_REQUEST)) {
									requestLimitExceeded = limitedExceededMessage;
								}
								else if(limitedExceededMessage!=null && limitedExceededMessage.startsWith(CostantiPdD.PREFIX_LIMITED_RESPONSE)) {
									responseLimitExceeded = limitedExceededMessage;
								}
							}
							
							if(requestMessagePrimaTrasformazione.getParseException()!=null || 
									requestReadTimeout!=null || 
									requestLimitExceeded!=null){
								pddContext.addObject(org.openspcoop2.core.constants.Costanti.CONTENUTO_RICHIESTA_NON_RICONOSCIUTO, true);
								
								ParseException parseException = null;
								Throwable tParsing = null;
								String errorMsg = null;
								if(requestReadTimeout != null) {
									tParsing = (TimeoutIOException) pddContext.getObject(TimeoutInputStream.EXCEPTION_KEY);
									errorMsg = tParsing.getMessage();
								}
								else if(requestLimitExceeded != null) {
									tParsing = (LimitExceededIOException) pddContext.getObject(LimitedInputStream.EXCEPTION_KEY);
									errorMsg = tParsing.getMessage();
								}
								else {
									parseException = requestMessagePrimaTrasformazione.getParseException();
									tParsing = parseException.getParseException();
									errorMsg = tParsing.getMessage();
								}
								
								IntegrationFunctionError integrationFunctionError = IntegrationFunctionError.UNPROCESSABLE_REQUEST_CONTENT;
								if(requestReadTimeout!=null) {
									integrationFunctionError = IntegrationFunctionError.REQUEST_TIMED_OUT;
								}
								else if(requestLimitExceeded!=null) {
									integrationFunctionError = IntegrationFunctionError.REQUEST_SIZE_EXCEEDED;
								}
								else if(requestMessagePrimaTrasformazione.getParseException().getSourceException()!=null &&
										TimeoutIOException.isTimeoutIOException(requestMessagePrimaTrasformazione.getParseException().getSourceException())) {
									integrationFunctionError = IntegrationFunctionError.REQUEST_TIMED_OUT;
								}
								else if(requestMessagePrimaTrasformazione.getParseException().getSourceException()!=null &&
										LimitExceededIOException.isLimitExceededIOException(requestMessagePrimaTrasformazione.getParseException().getSourceException())) {
									integrationFunctionError = IntegrationFunctionError.REQUEST_SIZE_EXCEEDED;
								}
								
								OpenSPCoop2Message responseMessageError = 
										this.generatoreErrore.build(pddContext, integrationFunctionError,
											ErroriIntegrazione.ERRORE_432_PARSING_EXCEPTION_RICHIESTA.getErrore432_MessaggioRichiestaMalformato(tParsing),
											tParsing,
											parseException);
								ejbUtils.sendRispostaApplicativaErrore(responseMessageError,richiestaDelegata,rollbackRichiesta,pd,sa);
								openspcoopstate.releaseResource();
								esito.setEsitoInvocazione(true);	
								esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,errorMsg);
								return esito;
							}
							else if(pddContext.containsKey(org.openspcoop2.core.constants.Costanti.CONTENUTO_RISPOSTA_NON_RICONOSCIUTO_PARSE_EXCEPTION)
									//|| responseReadTimeout!=null deve essere gestito dopo per generare un errore di connessione. Viene gestito all'interno del metodo 'getIntegrationFunctionErroreConnectionError' dell'else sottostante
									|| responseLimitExceeded!=null
									){
								
								ParseException parseException = null;
								Throwable tParsing = null;
								String errorMsg = null;
								if(responseReadTimeout != null) {
									tParsing = (TimeoutIOException) pddContext.getObject(TimeoutInputStream.EXCEPTION_KEY);
									errorMsg = tParsing.getMessage();
								}
								else if(responseLimitExceeded != null) {
									tParsing = (LimitExceededIOException) pddContext.getObject(LimitedInputStream.EXCEPTION_KEY);
									errorMsg = tParsing.getMessage();
								}
								else if(pddContext.containsKey(org.openspcoop2.core.constants.Costanti.CONTENUTO_RISPOSTA_NON_RICONOSCIUTO_PARSE_EXCEPTION)){
									parseException = (ParseException) pddContext.getObject(org.openspcoop2.core.constants.Costanti.CONTENUTO_RISPOSTA_NON_RICONOSCIUTO_PARSE_EXCEPTION);
									tParsing = parseException.getParseException();
									errorMsg = tParsing.getMessage();
								}
								
								pddContext.addObject(org.openspcoop2.core.constants.Costanti.CONTENUTO_RISPOSTA_NON_RICONOSCIUTO, true);
								IntegrationFunctionError integrationFunctionError = IntegrationFunctionError.UNPROCESSABLE_RESPONSE_CONTENT;
								if(responseReadTimeout!=null) {
									integrationFunctionError = IntegrationFunctionError.ENDPOINT_REQUEST_TIMED_OUT;
								}
								else if(responseLimitExceeded!=null) {
									integrationFunctionError = IntegrationFunctionError.RESPONSE_SIZE_EXCEEDED;
								}
								else if(parseException.getSourceException()!=null &&
										TimeoutIOException.isTimeoutIOException(parseException.getSourceException())) {
									integrationFunctionError = IntegrationFunctionError.ENDPOINT_REQUEST_TIMED_OUT;
								}
								else if(parseException.getSourceException()!=null &&
										LimitExceededIOException.isLimitExceededIOException(parseException.getSourceException())) {
									integrationFunctionError = IntegrationFunctionError.RESPONSE_SIZE_EXCEEDED;
								}
								OpenSPCoop2Message responseMessageError = 
										this.generatoreErrore.build(pddContext,integrationFunctionError,
											ErroriIntegrazione.ERRORE_440_PARSING_EXCEPTION_RISPOSTA.getErrore440_MessaggioRispostaMalformato(tParsing),
											tParsing,
											parseException);
								ejbUtils.sendRispostaApplicativaErrore(responseMessageError,richiestaDelegata,rollbackRichiesta,pd,sa);
								openspcoopstate.releaseResource();
								esito.setEsitoInvocazione(true);	
								esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,errorMsg);
								return esito;
							}
							else {
								IntegrationFunctionError integrationFunctionError = getIntegrationFunctionErroreConnectionError(eccezioneProcessamentoConnettore, motivoErroreConsegna, pddContext);
								OpenSPCoop2Message responseMessageError = 
										this.generatoreErrore.build(pddContext,integrationFunctionError,
												ErroriIntegrazione.ERRORE_516_CONNETTORE_UTILIZZO_CON_ERRORE.
													get516_PortaDiDominioNonDisponibile(bustaRichiesta.getTipoDestinatario()+"-"+bustaRichiesta.getDestinatario()),
																eccezioneProcessamentoConnettore,
																(responseMessage!=null ? responseMessage.getParseException() : null));
								
								// Retry-After
								boolean isEnabled = this.propertiesReader.isEnabledServiceUnavailableRetryAfter_pd_connectionFailed();
								Integer retryAfterSeconds = this.propertiesReader.getServiceUnavailableRetryAfterSeconds_pd_connectionFailed();
								Integer retryAfterBackOffSeconds = this.propertiesReader.getServiceUnavailableRetryAfterSeconds_randomBackoff_pd_connectionFailed();
								if(	isEnabled &&
									retryAfterSeconds!=null && retryAfterSeconds>0) {
									int seconds = retryAfterSeconds;
									if(retryAfterBackOffSeconds!=null && retryAfterBackOffSeconds>0) {
										seconds = seconds + new Random().nextInt(retryAfterBackOffSeconds);
									}
									responseMessageError.forceTransportHeader(HttpConstants.RETRY_AFTER, seconds+"");
								}
								
								ejbUtils.sendRispostaApplicativaErrore(responseMessageError,richiestaDelegata,rollbackRichiesta,pd,sa);
								openspcoopstate.releaseResource();
								esito.setEsitoInvocazione(true);	
								esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,erroreConnettore);
								return esito;
							}
						}
						else{
							if(responseMessage.getParseException()!=null){
								pddContext.addObject(org.openspcoop2.core.constants.Costanti.CONTENUTO_RISPOSTA_NON_RICONOSCIUTO, true);
								OpenSPCoop2Message responseMessageError = 
										this.generatoreErrore.build(pddContext,IntegrationFunctionError.UNPROCESSABLE_RESPONSE_CONTENT,
											ErroriIntegrazione.ERRORE_440_PARSING_EXCEPTION_RISPOSTA.getErrore440_MessaggioRispostaMalformato(responseMessage.getParseException().getParseException()),
											responseMessage.getParseException().getParseException(),
											responseMessage.getParseException());
								ejbUtils.sendRispostaApplicativaErrore(responseMessageError,richiestaDelegata,rollbackRichiesta,pd,sa);
								openspcoopstate.releaseResource();
								esito.setEsitoInvocazione(true);	
								esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,responseMessage.getParseException().getParseException().getMessage());
								return esito;
							}
						}
					}else{
						// Se Non e' attivo una gestione dei riscontri, faccio il rollback sulla coda.
						// Altrimenti verra attivato la gestione dei riscontri che riprovera' dopo un tot.
						if(gestioneBusteNonRiscontrateAttive==false){
							if(riconsegna){
								ejbUtils.rollbackMessage(erroreConnettore,dataRiconsegna, esito);
								esito.setEsitoInvocazione(false);
								esito.setStatoInvocazione(EsitoLib.ERRORE_NON_GESTITO,erroreConnettore);
							}else{
								ejbUtils.releaseOutboxMessage(true);
								esito.setEsitoInvocazione(true);
								esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,erroreConnettore);
							}
						}else{
							ejbUtils.updateErroreProcessamentoMessage(erroreConnettore,dataRiconsegna, esito);
							esito.setEsitoInvocazione(true);
							esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,erroreConnettore);
						}
						openspcoopstate.releaseResource();
						return esito;
					}
				}
			}





			

			
			
			/* ------------------------- Gestione Header Integrazione / Correlazione Applicativa  ---------------------------- */
			
			HeaderIntegrazione headerIntegrazioneRisposta = new HeaderIntegrazione(idTransazione);
			
			Utilities.printFreeMemory("InoltroBuste - Gestione Header Integrazione / Correlazione Applicativa... ");
			
				
			// *** Header Integrazione Risposta ***
			msgDiag.mediumDebug("Gestione Header Integrazione... ");
			String[] tipiIntegrazionePD_risposta = null;
			InResponsePDMessage inResponsePDMessage = null;
			if(this.propertiesReader.processHeaderIntegrazionePDResponse(functionAsRouter)){
				try {
					if(pd!=null)
						tipiIntegrazionePD_risposta = configurazionePdDManager.getTipiIntegrazione(pd);
				} catch (Exception e) {
					msgDiag.logErroreGenerico(e, "getTipiIntegrazione(pd)");
					if(sendRispostaApplicativa){
						OpenSPCoop2Message responseMessageError = 
								this.generatoreErrore.build(pddContext,IntegrationFunctionError.INTERNAL_RESPONSE_ERROR,
										ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
											get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_536_CONFIGURAZIONE_NON_DISPONIBILE),e,
											(responseMessage!=null ? responseMessage.getParseException() : null));
						ejbUtils.sendRispostaApplicativaErrore(responseMessageError,richiestaDelegata,rollbackRichiesta,pd,sa);
						esito.setEsitoInvocazione(true);
						esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,"getTipiIntegrazione(pd)");
					}else{
						// Se Non e' attivo una gestione dei riscontri, faccio il rollback sulla coda.
						// Altrimenti verra attivato la gestione dei riscontri che riprovera' dopo un tot.
						if(gestioneBusteNonRiscontrateAttive==false){
							ejbUtils.rollbackMessage("Gestione header Integrazione Risposta non riuscita.", esito);
							esito.setStatoInvocazioneErroreNonGestito(e);
							esito.setEsitoInvocazione(false);
						}else{
							ejbUtils.updateErroreProcessamentoMessage("Gestione header Integrazione Risposta non riuscita.", esito);
							esito.setEsitoInvocazione(true);
							esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,"getTipiIntegrazione(pd)");
						}
					}
					openspcoopstate.releaseResource();
					return esito;
				}
				
				if (tipiIntegrazionePD_risposta == null){
					if(InoltroBuste.defaultPerProtocolloGestoreIntegrazionePD.containsKey(protocolFactory.getProtocol())){
						tipiIntegrazionePD_risposta = InoltroBuste.defaultPerProtocolloGestoreIntegrazionePD.get(protocolFactory.getProtocol());
					}else{
						tipiIntegrazionePD_risposta = InoltroBuste.defaultGestoriIntegrazionePD;
					}
				}
				
				inResponsePDMessage = new InResponsePDMessage();
				inResponsePDMessage.setBustaRichiesta(bustaRichiesta);
				inResponsePDMessage.setMessage(responseMessage);
				inResponsePDMessage.setPortaDelegata(pd);
				inResponsePDMessage.setHeaders(connectorSender.getHeaderTrasporto());
				inResponsePDMessage.setServizio(idServizio);
				inResponsePDMessage.setSoggettoMittente(soggettoFruitore);
				for (int i = 0; i < tipiIntegrazionePD_risposta.length; i++) {
					try {
						IGestoreIntegrazionePD gestore = null;
						try{
							gestore = (IGestoreIntegrazionePD) this.pluginLoader.newIntegrazionePortaDelegata(tipiIntegrazionePD_risposta[i]);
						}catch(Exception e){
							throw e;
						}
						if(gestore!=null){
							String classType = null;
							try {
								classType = gestore.getClass().getName();
								AbstractCore.init(gestore, pddContext, protocolFactory);
							}catch(Exception e){
								throw new Exception("Riscontrato errore durante l'inizializzazione della classe ["+classType+
										"] da utilizzare per la gestione dell'integrazione (Risposta) delle fruizioni di tipo ["+tipiIntegrazionePD_risposta[i]+"]: "+e.getMessage());
							}
							if(responseMessage!=null){
								gestore.readInResponseHeader(headerIntegrazioneRisposta,inResponsePDMessage);
							}else if( ! (gestore instanceof IGestoreIntegrazionePDSoap) ){
								gestore.readInResponseHeader(headerIntegrazioneRisposta,inResponsePDMessage);
							}
						} else {
							msgDiag.logErroreGenerico("Lettura Gestore header di integrazione ["
											+ tipiIntegrazionePD_risposta[i]+ "]  non riuscita: non inizializzato", 
											"gestoriIntegrazionePD.get("+tipiIntegrazionePD_risposta[i]+")");
						}
					} catch (Exception e) {
						this.log.debug("Errore durante la lettura dell'header di integrazione ["+ tipiIntegrazionePD_risposta[i]
										+ "]: "+ e.getMessage(),e);
						msgDiag.addKeyword(CostantiPdD.KEY_TIPO_HEADER_INTEGRAZIONE,tipiIntegrazionePD_risposta[i]);
						msgDiag.addKeywordErroreProcessamento(e);
						msgDiag.logPersonalizzato("headerIntegrazione.letturaFallita");
					}
				}
			}
		
			// *** Correlazione Applicativa ***
			msgDiag.mediumDebug("Gestione Correlazione Applicativa... ");
			
			// Correlazione Applicativa
			if(functionAsRouter==false && pd!=null && pd.getCorrelazioneApplicativaRisposta()!=null){
				GestoreCorrelazioneApplicativa gestoreCorrelazione = null;
				try{
					
					gestoreCorrelazione = 
							new GestoreCorrelazioneApplicativa(openspcoopstate.getStatoRisposta(),
									this.log,soggettoFruitore,idServizio, servizioApplicativoFruitore,protocolFactory,
									transactionNullable, pddContext);

					gestoreCorrelazione.verificaCorrelazioneRisposta(pd.getCorrelazioneApplicativaRisposta(), responseMessage, headerIntegrazioneRisposta, false);
					
					idCorrelazioneApplicativaRisposta = gestoreCorrelazione.getIdCorrelazione();
					
					if(idCorrelazioneApplicativaRisposta!=null) {
						if(transactionNullable!=null) {
							transactionNullable.setCorrelazioneApplicativaRisposta(idCorrelazioneApplicativaRisposta);
						}
					}
					
					msgDiag.setIdCorrelazioneRisposta(idCorrelazioneApplicativaRisposta);

					if(richiestaDelegata!=null){
						richiestaDelegata.setIdCorrelazioneApplicativaRisposta(idCorrelazioneApplicativaRisposta);
					}
					
				}catch(Exception e){
					
					pddContext.addObject(org.openspcoop2.core.constants.Costanti.ERRORE_CORRELAZIONE_APPLICATIVA_RISPOSTA, "true");
					
					msgDiag.logErroreGenerico(e, "gestioneCorrelazioneApplicativaRisposta");
					this.log.error("Riscontrato errore durante il controllo di correlazione applicativa della risposta: "+ e.getMessage(),e);
					
					ErroreIntegrazione errore = null;
					if(gestoreCorrelazione!=null){
						errore = gestoreCorrelazione.getErrore();
					}
					if(errore==null){
						errore = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
								get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_553_CORRELAZIONE_APPLICATIVA_RISPOSTA_NON_RIUSCITA);
					}
					if(sendRispostaApplicativa){
						OpenSPCoop2Message responseMessageError = 
								this.generatoreErrore.build(pddContext,IntegrationFunctionError.APPLICATION_CORRELATION_IDENTIFICATION_RESPONSE_FAILED,
										errore,e,
											(responseMessage!=null ? responseMessage.getParseException() : null));
						ejbUtils.sendRispostaApplicativaErrore(responseMessageError,richiestaDelegata,rollbackRichiesta,pd,sa);
						esito.setEsitoInvocazione(true);
						esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,"gestioneCorrelazioneApplicativaRisposta");
					}else{
						// Se Non e' attivo una gestione dei riscontri, faccio il rollback sulla coda.
						// Altrimenti verra attivato la gestione dei riscontri che riprovera' dopo un tot.
						if(gestioneBusteNonRiscontrateAttive==false){
							ejbUtils.rollbackMessage("Gestione Correlazione Applicativa Risposta non riuscita.", esito);
							esito.setStatoInvocazioneErroreNonGestito(e);
							esito.setEsitoInvocazione(false);
						}else{
							ejbUtils.updateErroreProcessamentoMessage("Gestione Correlazione Applicativa Risposta non riuscita.", esito);
							esito.setEsitoInvocazione(true);
							esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,"gestioneCorrelazioneApplicativaRisposta");
						}
					}
					openspcoopstate.releaseResource();
					return esito;
				}
			}







			
			
			
			// Sbustamento Protocollo Dopo Correlazione Applicativa
			
			if( sbustamentoInformazioniProtocolloRispostaDopoCorrelazione ){		
				
				// Qua dentro ci entro sia se sono router, che se è ancora rilevata la presenza di un protocollo dopo la validazione semantica
				
				// Estrazione header busta
				msgDiag.mediumDebug("Sbustamento della risposta (PostCorrelazione PreConsegna)...");
				try{
									
					if(sbustamentoInformazioniProtocolloRisposta){
						// effettuo lo stesso sbustamento invocandolo con la nuova fase
						// Questa invocazione andrebbe implementata su ricezionecontenutiApplicativi teoricamente
						msgDiag.highDebug("Tipo Messaggio Risposta prima dello sbustamento ["+FaseSbustamento.PRE_CONSEGNA_RISPOSTA
								+"] ["+responseMessage.getClass().getName()+"]");
						ProtocolMessage protocolMessage = sbustatore.sbustamento(responseMessage,pddContext,
								bustaRisposta,
								RuoloMessaggio.RISPOSTA,sbustamentoManifestRisposta,proprietaManifestAttachments,
								FaseSbustamento.PRE_CONSEGNA_RISPOSTA, requestInfo);
						if(protocolMessage!=null ) {
							if(!protocolMessage.isUseBustaRawContentReadByValidation()) {
								headerProtocolloRisposta = protocolMessage.getBustaRawContent();
							}
							responseMessage = protocolMessage.getMessage(); // updated
						}
						msgDiag.highDebug("Tipo Messaggio Risposta dopo lo sbustamento ["+FaseSbustamento.PRE_CONSEGNA_RISPOSTA
								+"] ["+responseMessage.getClass().getName()+"]");	
					}
						
				}catch(Exception e){
					msgDiag.logErroreGenerico(e,"sbustatore.sbustamento("+bustaRisposta.getID()+")");
					
					EsitoElaborazioneMessaggioTracciato esitoTraccia = EsitoElaborazioneMessaggioTracciato.getEsitoElaborazioneConErrore("Errore durante lo sbustamento della risposta: "+e.getMessage());
					tracciamento.registraRisposta(responseMessage,securityInfoResponse,headerProtocolloRisposta,bustaRisposta,esitoTraccia,
							Tracciamento.createLocationString(true, location),
							idCorrelazioneApplicativa,idCorrelazioneApplicativaRisposta); // non ancora registrata
					
					if(functionAsRouter){
						ejbUtils.sendAsRispostaBustaErroreProcessamento(richiestaDelegata.getIdModuloInAttesa(),
								bustaRichiesta,
								ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
									get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_527_GESTIONE_SBUSTAMENTO),
								idCorrelazioneApplicativa,idCorrelazioneApplicativaRisposta,servizioApplicativoFruitore,e,
								(responseMessage!=null ? responseMessage.getParseException() : null));
						esito.setEsitoInvocazione(true);
						esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,
								"Sbustamento non riuscito");
					}else{
												
						ErroreIntegrazione erroreIntegrazione = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
								get5XX_ErroreProcessamento(e,CodiceErroreIntegrazione.CODICE_527_GESTIONE_SBUSTAMENTO);
						if(sendRispostaApplicativa){
							OpenSPCoop2Message responseMessageError = this.generatoreErrore.build(pddContext,IntegrationFunctionError.INTERNAL_RESPONSE_ERROR,
									erroreIntegrazione,e,
										(responseMessage!=null ? responseMessage.getParseException() : null));
							ejbUtils.sendRispostaApplicativaErrore(responseMessageError,richiestaDelegata,rollbackRichiesta,pd,sa);
							esito.setEsitoInvocazione(true);
							esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,"Sbustamento non riuscito");
						}else{
							// Se Non e' attivo una gestione dei riscontri, faccio il rollback sulla coda.
							// Altrimenti verra attivato la gestione dei riscontri che riprovera' dopo un tot.
							if(gestioneBusteNonRiscontrateAttive==false){
								ejbUtils.rollbackMessage("Sbustamento non riuscito", esito);
								esito.setStatoInvocazioneErroreNonGestito(e);
								esito.setEsitoInvocazione(false);
							}else{
								ejbUtils.updateErroreProcessamentoMessage("Sbustamento non riuscito", esito);
								esito.setEsitoInvocazione(true);
								esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,"Sbustamento non riuscito");
							}
						}
						
					}
					openspcoopstate.releaseResource();
					return esito;

				}
			}

			
			
			
			
			
			
			
			
			
			
			


			Utilities.printFreeMemory("InoltroBuste - Gestione Risposta... ");








			/* ---------------- Gestione Risposta  (operazioni comuni per tutti i profili) ------------------- */ 
			boolean isMessaggioErroreProtocollo = false;
			boolean bustaDiServizio = false;
			String idMessageResponse = null;
			java.util.List<Eccezione> erroriValidazione = null;
			IntegrationFunctionError validazione_integrationFunctionError = null;
			java.util.List<Eccezione> erroriProcessamento = null;
			SbustamentoRisposteMessage sbustamentoRisposteMSG = null;
			DettaglioEccezione dettaglioEccezione = null;

			if(presenzaRispostaProtocollo){

				// Aggiornamento Informazioni
				msgDiag.setIdMessaggioRisposta(idMessageResponse);

				// Gestione Specifica per Buste
				bustaRisposta = validatore.getBusta();
				idMessageResponse = bustaRisposta.getID();
				
				// Imposto eventuali informazioni DirectVM
				if(bustaRisposta!=null && pddContext!=null){
					DirectVMProtocolInfo.setInfoFromContext(pddContext, bustaRisposta);
				}
				
				// Aggiunto risposta a pddContext
				if(bustaRisposta!=null){
					pddContext.addObject(CostantiPdD.BUSTA_RISPOSTA, bustaRisposta);
				}

				// Se non impostati, imposto i domini
				org.openspcoop2.pdd.core.Utilities.refreshIdentificativiPorta(bustaRisposta, requestInfo.getIdentitaPdD(), registroServiziManager, protocolFactory);

				// aggiunto dal protocollo
				if(richiestaDelegata!=null && richiestaDelegata.getIdCollaborazione()==null && bustaRisposta.getCollaborazione()!=null) {
					richiestaDelegata.setIdCollaborazione(bustaRisposta.getCollaborazione());
				}
				
				isMessaggioErroreProtocollo = validatore.isErroreProtocollo();
				bustaDiServizio = validatore.isBustaDiServizio();
				erroriValidazione = validatore.getEccezioniValidazione();
				validazione_integrationFunctionError = validatore.getErrore_integrationFunctionError();
				erroriProcessamento =validatore.getEccezioniProcessamento();

				// Registrazione Msg
				if(isMessaggioErroreProtocollo){
					if(validatore.isMessaggioErroreIntestazione()){
						msgDiag.addKeyword(CostantiPdD.KEY_TIPO_MESSAGGIO_BUSTA_ERRORE, CostantiPdD.TIPO_MESSAGGIO_BUSTA_ERRORE_INTESTAZIONE);
					}else{
						msgDiag.addKeyword(CostantiPdD.KEY_TIPO_MESSAGGIO_BUSTA_ERRORE, CostantiPdD.TIPO_MESSAGGIO_BUSTA_ERRORE_PROCESSAMENTO);
					}
					msgDiag.logPersonalizzato("ricezioneMessaggioErrore");
					
					// Esamino se e' presente un elemento DettaglioEccezione
					dettaglioEccezione = XMLUtils.getDettaglioEccezione(this.log,responseMessage);
					
				}else{
					msgDiag.logPersonalizzato("ricezioneMessaggio");
				}

				// se ci sono errori di processamento gestisco errore.
				if(erroriProcessamento.size()>0){
					StringBuilder errore = new StringBuilder();
					Eccezione ecc = null;
					if( erroriProcessamento.size()>1 ){
						ecc = Eccezione.getEccezioneProcessamento(ErroriCooperazione.ERRORE_GENERICO_PROCESSAMENTO_MESSAGGIO.getErroreCooperazione(), protocolFactory);
						for(int k=0; k<erroriProcessamento.size();k++){
							Eccezione eccProcessamento = erroriProcessamento.get(k);
							
							if(k>0) {
								errore.append("\n");
							}
							errore.append("["+traduttore.toString(eccProcessamento.getCodiceEccezione(),eccProcessamento.getSubCodiceEccezione())+"]");
							if(eccProcessamento.getDescrizione(protocolFactory)!=null){
								errore.append(" "+eccProcessamento.getDescrizione(protocolFactory));
							}
							
						}
						if(errore.length()>0)
							ecc.setDescrizione(errore.toString());		
					}else{
						ecc = erroriProcessamento.get(0);
						errore.append("["+traduttore.toString(ecc.getCodiceEccezione(),ecc.getSubCodiceEccezione())+"]");
						if(ecc.getDescrizione(protocolFactory)!=null){
							errore.append(" "+ecc.getDescrizione(protocolFactory));
						}
					}
					EsitoElaborazioneMessaggioTracciato esitoTraccia = null;
					if(isMessaggioErroreProtocollo){
						msgDiag.addKeyword(CostantiPdD.KEY_ECCEZIONI, errore.toString());
						msgDiag.logPersonalizzato(MsgDiagnosticiProperties.MSG_DIAG_SBUSTAMENTO_RISPOSTE,"validazioneBustaErrore.listaEccezioniMalformata");
						esitoTraccia = EsitoElaborazioneMessaggioTracciato.getEsitoElaborazioneConErrore(msgDiag.getMessaggio_replaceKeywords(MsgDiagnosticiProperties.MSG_DIAG_SBUSTAMENTO_RISPOSTE,"validazioneBustaErrore.listaEccezioniMalformata"));
					}else{
						msgDiag.addKeyword(CostantiPdD.KEY_ECCEZIONI, errore.toString());
						msgDiag.addKeyword(CostantiPdD.KEY_NUMERO_ECCEZIONI, erroriProcessamento.size()+"");
						msgDiag.logPersonalizzato(MsgDiagnosticiProperties.MSG_DIAG_SBUSTAMENTO_RISPOSTE,"validazioneBusta.bustaNonCorretta");
						esitoTraccia = EsitoElaborazioneMessaggioTracciato.getEsitoElaborazioneConErrore(msgDiag.getMessaggio_replaceKeywords(MsgDiagnosticiProperties.MSG_DIAG_SBUSTAMENTO_RISPOSTE,"validazioneBusta.bustaNonCorretta"));
					}
					tracciamento.registraRisposta(responseMessage,securityInfoResponse,headerProtocolloRisposta,bustaRisposta,esitoTraccia,
							Tracciamento.createLocationString(true, location),
							idCorrelazioneApplicativa,idCorrelazioneApplicativaRisposta); // non ancora registrata
					if(functionAsRouter){
						ejbUtils.sendAsRispostaBustaErroreProcessamento(richiestaDelegata.getIdModuloInAttesa(),
								bustaRichiesta,erroriProcessamento,
								idCorrelazioneApplicativa,idCorrelazioneApplicativaRisposta,servizioApplicativoFruitore,null,
								(responseMessage!=null ? responseMessage.getParseException() : null));
					}else{
						String erroreRilevato = "Riscontrato errore durante la validazione della busta di risposta con id["+idMessageResponse+"]:\n"+errore.toString();
						if(sendRispostaApplicativa){
							OpenSPCoop2Message responseMessageError =  
									this.generatoreErrore.build(pddContext,IntegrationFunctionError.INTERNAL_RESPONSE_ERROR,ecc,richiestaDelegata.getIdSoggettoFruitore(),
												(responseMessage!=null ? responseMessage.getParseException() : null));
							ejbUtils.sendRispostaApplicativaErrore(responseMessageError,richiestaDelegata,rollbackRichiesta,pd,sa);
							esito.setEsitoInvocazione(true);
							esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,erroreRilevato);
						}else{
							// Se Non e' attivo una gestione dei riscontri, faccio il rollback sulla coda.
							// Altrimenti verra attivato la gestione dei riscontri che riprovera' dopo un tot.
							if(gestioneBusteNonRiscontrateAttive==false){
								ejbUtils.rollbackMessage(erroreRilevato, esito);
								esito.setEsitoInvocazione(false);
								esito.setStatoInvocazione(EsitoLib.ERRORE_NON_GESTITO,erroreRilevato);
							}else{
								ejbUtils.updateErroreProcessamentoMessage(erroreRilevato, esito);
								esito.setEsitoInvocazione(true);
								esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,erroreRilevato);
							}
						}
					}
					openspcoopstate.releaseResource();
					return esito;
				}

				// Tracciamento Risposta
				msgDiag.mediumDebug("Tracciamento della busta di risposta...");
				EsitoElaborazioneMessaggioTracciato esitoTraccia = EsitoElaborazioneMessaggioTracciato.getEsitoElaborazioneMessaggioRicevuto();
				tracciamento.registraRisposta(responseMessage,securityInfoResponse,headerProtocolloRisposta,bustaRisposta,esitoTraccia,
						Tracciamento.createLocationString(true, location),
						idCorrelazioneApplicativa,idCorrelazioneApplicativaRisposta);

				// Costruzione oggetto da spedire al modulo 'SbustamentoRisposte' (se routing non attivo)
				if(functionAsRouter==false){
					sbustamentoRisposteMSG = new SbustamentoRisposteMessage();
					sbustamentoRisposteMSG.setRichiestaDelegata(richiestaDelegata);
					sbustamentoRisposteMSG.setBusta(bustaRisposta);
					sbustamentoRisposteMSG.setErrors(erroriValidazione, validazione_integrationFunctionError);
					sbustamentoRisposteMSG.setMessaggioErroreProtocollo(isMessaggioErroreProtocollo);
					sbustamentoRisposteMSG.setIsBustaDiServizio(bustaDiServizio);
					sbustamentoRisposteMSG.setRuoloBustaRicevuta(validatore.getRuoloBustaRicevuta(requestInfo.getProtocolServiceBinding(),true));
					sbustamentoRisposteMSG.setSpedizioneMsgIngresso(ejbUtils.getSpedizioneMsgIngresso());
					sbustamentoRisposteMSG.setRicezioneMsgRisposta(ejbUtils.getRicezioneMsgRisposta());
					sbustamentoRisposteMSG.setOneWayVersione11(oneWayVersione11);
					sbustamentoRisposteMSG.setImplementazionePdDSoggettoMittente(inoltroBusteMsg.getImplementazionePdDSoggettoMittente());
					sbustamentoRisposteMSG.setImplementazionePdDSoggettoDestinatario(inoltroBusteMsg.getImplementazionePdDSoggettoDestinatario());
					sbustamentoRisposteMSG.setPddContext(pddContext);
					sbustamentoRisposteMSG.setDettaglioEccezione(dettaglioEccezione);
					if(validatore.getInfoServizio()!=null){
						sbustamentoRisposteMSG.setFiltroDuplicati(Inoltro.SENZA_DUPLICATI.equals(validatore.getInfoServizio().getInoltro()));
					}
				}				
				// Costruzione sessione messaggio di risposta
				String tipoMsg = Costanti.INBOX;
				if(functionAsRouter)
					tipoMsg = Costanti.OUTBOX;
				msgDiag.mediumDebug("Registrazione messaggio di risposta nel RepositoryMessaggi/Buste...");
				msgResponse = new GestoreMessaggi(openspcoopstate, false, idMessageResponse,tipoMsg,msgDiag, pddContext);
				msgResponse.setPortaDiTipoStateless(portaDiTipoStateless);
				msgResponse.setOneWayVersione11(oneWayVersione11);
				

				
				// *** Aggiornamento/Eliminazione header integrazione ***
				msgDiag.mediumDebug("Aggiornamento/Eliminazione Header Integrazione... ");
				if(responseMessage!=null && tipiIntegrazionePD_risposta!=null){
					for (int i = 0; i < tipiIntegrazionePD_risposta.length; i++) {
						try {
							
							IGestoreIntegrazionePD gestore = null;
							try{
								gestore = (IGestoreIntegrazionePD) this.pluginLoader.newIntegrazionePortaDelegata(tipiIntegrazionePD_risposta[i]);
							}catch(Exception e){
								throw e;
							}
							if(gestore!=null){
								String classType = null;
								try {
									classType = gestore.getClass().getName();
									AbstractCore.init(gestore, pddContext, protocolFactory);
								}catch(Exception e){
									throw new Exception("Riscontrato errore durante l'inizializzazione della classe ["+classType+
											"] da utilizzare per la gestione dell'integrazione (Risposta Update/Delete) delle fruizioni di tipo ["+tipiIntegrazionePD_risposta[i]+"]: "+e.getMessage());
								}
								if (gestore instanceof IGestoreIntegrazionePDSoap) {
									if(this.propertiesReader.deleteHeaderIntegrazioneResponsePD()){
										((IGestoreIntegrazionePDSoap)gestore).deleteInResponseHeader(inResponsePDMessage);
									}else{
										((IGestoreIntegrazionePDSoap)gestore).updateInResponseHeader(inResponsePDMessage, idMessageRequest, idMessageResponse, 
												servizioApplicativoFruitore, idCorrelazioneApplicativaRisposta, idCorrelazioneApplicativa);
									}
								}
							}
						} catch (Exception e) {
							String motivoErrore = null;
							if(this.propertiesReader.deleteHeaderIntegrazioneResponsePD()){
								motivoErrore = "deleteHeaderIntegrazione("+ tipiIntegrazionePD_risposta[i]+")";
							}else{
								motivoErrore = "updateHeaderIntegrazione("+ tipiIntegrazionePD_risposta[i]+")";
							}
							msgDiag.logErroreGenerico(e,motivoErrore);
							if(sendRispostaApplicativa){
								OpenSPCoop2Message responseMessageError = 
										this.generatoreErrore.build(pddContext,IntegrationFunctionError.INTERNAL_RESPONSE_ERROR,
												ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
													get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_541_GESTIONE_HEADER_INTEGRAZIONE),e,
														(responseMessage!=null ? responseMessage.getParseException() : null));
								ejbUtils.sendRispostaApplicativaErrore(responseMessageError,richiestaDelegata,rollbackRichiesta,pd,sa);
								esito.setEsitoInvocazione(true);
								esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,motivoErrore);
							}else{
								// Se Non e' attivo una gestione dei riscontri, faccio il rollback sulla coda.
								// Altrimenti verra attivato la gestione dei riscontri che riprovera' dopo un tot.
								if(gestioneBusteNonRiscontrateAttive==false){
									ejbUtils.rollbackMessage(motivoErrore, esito);
									esito.setStatoInvocazioneErroreNonGestito(e);
									esito.setEsitoInvocazione(false);
								}else{
									ejbUtils.updateErroreProcessamentoMessage(motivoErrore, esito);
									esito.setEsitoInvocazione(true);
									esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,motivoErrore);
								}
							}
							openspcoopstate.releaseResource();
							return esito;
						}
					}
				}
				
				
				
				try{
					RepositoryBuste repositoryResponse = null;
					if(functionAsRouter==false){
						repositoryResponse = new RepositoryBuste(openspcoopstate.getStatoRisposta(), false, protocolFactory);
					}

					if( msgResponse.existsMessage_noCache() ){
						// Se il proprietario attuale e' GestoreMessaggi, forzo l'eliminazione e continuo a processare il messaggio.
						String proprietarioMessaggio = msgResponse.getProprietario(InoltroBuste.ID_MODULO);
						if(TimerGestoreMessaggi.ID_MODULO.equals(proprietarioMessaggio)){
							msgDiag.logPersonalizzato("ricezioneSoapMessage.msgGiaPresente");
							String msg = msgDiag.getMessaggio_replaceKeywords("ricezioneSoapMessage.msgGiaPresente");
							if(this.propertiesReader.isMsgGiaInProcessamento_useLock()) {
								msgResponse._deleteMessageWithLock(msg,this.propertiesReader.getMsgGiaInProcessamento_AttesaAttiva(),
										this.propertiesReader.getMsgGiaInProcessamento_CheckInterval());
							}
							else {
								msgResponse.deleteMessageByNow();
							}
						}else{
							throw new Exception("Altra copia della Busta ricevuta come risposta con id["+idMessageResponse+"] in elaborazione dal modulo "+proprietarioMessaggio);
						}
					}

					// Aggiungo ErroreApplicativo come details se non sono router.
					if(functionAsRouter==false){
						if(soapFault!=null){
							if (!isMessaggioErroreProtocollo) {
								if(enrichSoapFaultApplicativo){
									IntegrationFunctionError integrationFunctionError = getIntegrationFunctionErroreConnectionError(eccezioneProcessamentoConnettore, motivoErroreConsegna, pddContext);
									this.generatoreErrore.getErroreApplicativoBuilderForAddDetailInSoapFault(pddContext, responseMessage.getMessageType(), integrationFunctionError).
										insertInSOAPFault(ErroriIntegrazione.ERRORE_516_CONNETTORE_UTILIZZO_CON_ERRORE.
												get516_ServizioApplicativoNonDisponibile(), 
												responseMessage);
								}
							}
						}
					}

					msgResponse.registraMessaggio(responseMessage,idCorrelazioneApplicativa, idCorrelazioneApplicativaRisposta);
					if(richiestaAsincronaSimmetricaStateless && (!isMessaggioErroreProtocollo)){
						// In caso di richiestaAsincronaSimmetrica e openspcoop stateless,
						// Devo comunque salvare le informazioni sul msg della ricevuta alla richiesta.
						// Tali informazioni servono per il check nel modulo RicezioneBuste, per verificare di gestire la risposta
						// solo dopo aver terminato di gestire la richiesta con relativa ricevuta.
						msgResponse.registraInformazioniMessaggio_statelessEngine(ejbUtils.getRicezioneMsgRisposta(), 
								org.openspcoop2.pdd.mdb.SbustamentoRisposte.ID_MODULO,
								idMessageRequest,
								idCorrelazioneApplicativa, idCorrelazioneApplicativaRisposta);
					}

					// registrazione risposta 
					if(functionAsRouter==false){
						
						//Se functionAsRouter viene registrato in EJBUtils.sendRispostaProtocollo
						
						msgResponse.aggiornaRiferimentoMessaggio(idMessageRequest);
						
						boolean bustaRispostaGiaRegistrata = false;
						if( (openspcoopstate instanceof OpenSPCoopStateful) || (oneWayVersione11)  ){
							bustaRispostaGiaRegistrata =repositoryResponse.isRegistrata(bustaRisposta.getID(),tipoMsg);
						}
						if(bustaRispostaGiaRegistrata){
							repositoryResponse.aggiornaBusta(bustaRisposta,tipoMsg,this.propertiesReader.getRepositoryIntervalloScadenzaMessaggi(),erroriValidazione);
							repositoryResponse.impostaUtilizzoPdD(bustaRisposta.getID(),tipoMsg);
						}
						else{
							repositoryResponse.registraBusta(bustaRisposta,tipoMsg, erroriValidazione ,this.propertiesReader.getRepositoryIntervalloScadenzaMessaggi());
						}
						Integrazione infoIntegrazione = new Integrazione();
						infoIntegrazione.setIdModuloInAttesa(richiestaDelegata.getIdModuloInAttesa());
						infoIntegrazione.setNomePorta(richiestaDelegata.getIdPortaDelegata().getNome());
						infoIntegrazione.setServizioApplicativo(richiestaDelegata.getServizioApplicativo());
						infoIntegrazione.setScenario(richiestaDelegata.getScenario());
						repositoryResponse.aggiornaInfoIntegrazione(bustaRisposta.getID(),tipoMsg,infoIntegrazione);
					}

				}catch(Exception e){
					if(msgResponse!=null){
						msgResponse.deleteMessageFromFileSystem(); // elimino eventuale risposta salvata su fileSystem
					}
					msgDiag.logErroreGenerico(e,"RegistrazioneRisposta("+idMessageResponse+")");
					if(functionAsRouter){
						ejbUtils.sendAsRispostaBustaErroreProcessamento(richiestaDelegata.getIdModuloInAttesa(),
								bustaRichiesta,
								ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
									get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_510_SAVE_RESPONSE_MSG),
								idCorrelazioneApplicativa,idCorrelazioneApplicativaRisposta,servizioApplicativoFruitore,e,
								(responseMessage!=null ? responseMessage.getParseException() : null));
						esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,"RegistrazioneRisposta("+idMessageResponse+")");
						esito.setEsitoInvocazione(true);
					}else{
						if(sendRispostaApplicativa){
							OpenSPCoop2Message responseMessageError = null;
							responseMessageError = 
									this.generatoreErrore.build(pddContext,IntegrationFunctionError.INTERNAL_RESPONSE_ERROR,
											ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
												get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_510_SAVE_RESPONSE_MSG),
												null, (responseMessage!=null ? responseMessage.getParseException() : null));
							ejbUtils.sendRispostaApplicativaErrore(responseMessageError,richiestaDelegata,rollbackRichiesta,pd,sa);
							esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,"RegistrazioneRisposta("+idMessageResponse+")");
							esito.setEsitoInvocazione(true);
						}else{
							//Se Non e' attivo una gestione dei riscontri, faccio il rollback sulla coda.
							// Altrimenti verra attivato la gestione dei riscontri che riprovera' dopo un tot.
							if(gestioneBusteNonRiscontrateAttive==false){
								ejbUtils.rollbackMessage("Salvataggio risposta non riuscito", esito);
								esito.setStatoInvocazioneErroreNonGestito(e);
								esito.setEsitoInvocazione(false);
							}else{
								ejbUtils.updateErroreProcessamentoMessage("Salvataggio risposta non riuscito", esito);
								esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,"RegistrazioneRisposta("+idMessageResponse+")");
								esito.setEsitoInvocazione(true);
							}
						}
					}
					openspcoopstate.releaseResource();
					return esito;
				}
			}








			/* -------Scenario RoutingSincrono -------------
			 * Invio di una busta con profilo sincrono (che non e' un msg di errore).
			 * In questo contesto la risposta deve essere sempre inviata sulla stessa connessione.
			 * Casi di fault:
			 * 1) Ricevere niente, carico HTTP Reply vuoto: 
			 *               E' tutto OK, invio lo sblocco verso RicezioneBuste            
			 * 2) e 3)  Ricevere un MessaggioProtocollo Errore o Normale: 
			 *               Forward verso RicezioneBuste  		   
			 * 4) Ricevere un MessaggioSoap (con o senza Fault) senza una busta associata: 
			 *               E' tutto OK, invio lo sblocco verso RicezioneBuste
			 *               In presenza di un Fault, si effettua la sua registrazione.
			 */ 
			/* -------- Scenario RoutingNotSincrono ------------
			 * Scenario alternativo al RoutingSincrono, include i msg:
			 * - profilo non sincrono
			 * - busta di servizio (servizio non presente)
			 * - busta Errore
			 * Casi di fault:
			 * 1) Ricevere niente, carico HTTP Reply vuoto: 
			 *          E' tutto OK,
			 *          se attiva la gestione sulla stessa connessione invio lo sblocco verso RicezioneBuste.
			 * 2) e 3)  Ricevere un MessaggioProtocollo Errore o Normale: 
			 *          Se attiva la gestione sulla stessa connessione invio la busta verso RicezioneBuste,
			 *          altrimenti la invio al modulo InoltroRisposte		   
			 * 4) Ricevere un MessaggioSoap (con o senza Fault) senza una busta associata: 
			 *           E' tutto OK,
			 *           se attiva la gestione sulla stessa connessione invio lo sblocco verso RicezioneBuste.
			 *           In presenza di un Fault, si effettua la sua registrazione.
			 */ 
			/* --------Scenario Sincrono------------
			 * Invio di una busta con profilo sincrono.
			 * In questo contesto la risposta deve essere sempre inviata sulla stessa connessione.
			 * Casi di fault:
			 * 1) Ricevere niente, carico HTTP Reply vuoto: 
			 *                segnalazione di errore al modulo SbustamentoRisposte
			 * 2) e 3)  Ricevere un MessaggioProtocollo Errore o Normale: forward verso SbustamentoRisposte 		   
			 * 4) Ricevere un MessaggioSoap (con o senza Fault) senza una busta associata: 
			 *               segnalazione di errore al modulo SbustamentoRisposte. 
			 *               In presenza di un Fault, si effettua la sua registrazione.
			 */
			/* -------- Scenario NotSincrono ------------
			 * Scenario alternativo al Sincrono, include i msg:
			 * - profilo non sincrono
			 * - busta Errore
			 * Casi di fault:
			 * 1) Ricevere niente, carico HTTP Reply vuoto: 
			 *          E' tutto OK, non devo fare altro.
			 * 2) e 3)  Ricevere un MessaggioProtocollo Errore o Normale: forward verso SbustamentoRisposte 
			 * 4) Ricevere un MessaggioSoap (con o senza Fault) senza una busta associata: 
			 *              e' tutto OK, non devo fare altro.   
			 *             In presenza di un Fault, si effettua la sua registrazione.
			 */ 


			msgDiag.mediumDebug("Registrazione eventuale fault...");
			//	Effettuo log dell'eventuale fault (registro anche i fault spcoop, potrebbero contenere dei details aggiunti da una PdD.)
			if( soapFault!=null && faultLogged==false ){
				msgDiag.addKeyword(CostantiPdD.KEY_SOAP_FAULT, SoapUtils.safe_toString(faultMessageFactory, soapFault, this.log));
				msgDiag.logPersonalizzato("ricezioneSoapFault");
				faultLogged = true;
			}
			else if( restProblem!=null && faultLogged==false ){
				msgDiag.addKeyword(CostantiPdD.KEY_REST_PROBLEM, restProblem.getRaw());
				msgDiag.logPersonalizzato("ricezioneRestProblem");
				faultLogged = true;
			}






			/* ------- Gestione punto 1 (Carico HTTP Reply vuoto) -------- */
			IValidatoreErrori validatoreErrori = protocolFactory.createValidatoreErrori(openspcoopstate.getStatoRisposta());
			if ( responseMessage == null ) {
				if(functionAsRouter){

					msgDiag.mediumDebug("Gestione punto 1 (Carico HTTP Reply vuoto) [router]...");
					
					// Scenario RoutingSincrono
					if( (org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.SINCRONO.equals(bustaRichiesta.getProfiloDiCollaborazione())) &&
							(!validatoreErrori.isBustaErrore(bustaRichiesta,requestMessageTrasformato,pValidazioneErrori)) ){
						msgDiag.mediumDebug("Spedizione msg di sblocco al modulo RicezioneBuste...");
						ejbUtils.sendSbloccoRicezioneBuste(richiestaDelegata.getIdModuloInAttesa());
					}
					// Scenario RoutingNotSincrono
					else{
						if(newConnectionForResponse==false){
							msgDiag.mediumDebug("Spedizione msg di sblocco al modulo RicezioneBuste...");
							msgResponse = ejbUtils.sendSbloccoRicezioneBuste(richiestaDelegata.getIdModuloInAttesa());
						}
					}

				}
				// Scenario OneWay in modalita sincrona
				else if(org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.ONEWAY.equals(bustaRichiesta.getProfiloDiCollaborazione()) &&
						sendRispostaApplicativa){
					msgDiag.mediumDebug("Invio messaggio 'OK' al modulo di RicezioneContenutiApplicativi (Carico HTTP Reply vuoto)...");
					if(protocolManager.isHttpEmptyResponseOneWay())
						msgResponse = ejbUtils.sendRispostaApplicativaOK(MessageUtilities.buildEmptyMessage(OpenSPCoop2MessageFactory.getDefaultMessageFactory(),
								requestInfo.getIntegrationRequestMessageType(),MessageRole.RESPONSE),
								richiestaDelegata,pd,sa);
					else
						msgResponse = ejbUtils.sendRispostaApplicativaOK(ejbUtils.buildOpenSPCoopOK(requestInfo.getIntegrationRequestMessageType(), idMessageRequest),richiestaDelegata,pd,sa);
				}
				// Scenario Sincrono
				else if(org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.SINCRONO.equals(bustaRichiesta.getProfiloDiCollaborazione())){
					
					msgDiag.mediumDebug("Gestione punto 1 (Carico HTTP Reply vuoto) [pdd]...");
					if(msgResponse!=null){
						msgResponse.deleteMessageFromFileSystem(); // elimino eventuale risposta salvata su fileSystem
					}

					msgDiag.logPersonalizzato("profiloSincrono.rispostaNonPervenuta");
					OpenSPCoop2Message responseMessageError = 
							this.generatoreErrore.build(pddContext,IntegrationFunctionError.EXPECTED_RESPONSE_NOT_FOUND,
									ErroriIntegrazione.ERRORE_517_RISPOSTA_RICHIESTA_NON_RITORNATA.
										get517_RispostaRichiestaNonRitornata(bustaRichiesta.getTipoDestinatario()+bustaRichiesta.getDestinatario()),null,
											(responseMessage!=null ? responseMessage.getParseException() : null));							
					ejbUtils.sendRispostaApplicativaErrore(responseMessageError,richiestaDelegata,rollbackRichiesta,pd,sa);
					openspcoopstate.releaseResource();
					esito.setEsitoInvocazione(true);
					esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,
							msgDiag.getMessaggio_replaceKeywords("profiloSincrono.rispostaNonPervenuta"));
					return esito;
				}
				// Scenario NotSincrono
				if (org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.ASINCRONO_SIMMETRICO.equals(bustaRichiesta.getProfiloDiCollaborazione()) ||
						org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.ASINCRONO_ASIMMETRICO.equals(bustaRichiesta.getProfiloDiCollaborazione()) ) {
				
					boolean attesaRispostaSullaConnessioneHttp = true;
					if(!Costanti.SCENARIO_ASINCRONO_ASIMMETRICO_POLLING.equals(richiestaDelegata.getScenario())){
						if(newConnectionForResponse){
							attesaRispostaSullaConnessioneHttp = false;
						}
					}
					
					if(attesaRispostaSullaConnessioneHttp){
						
						msgDiag.mediumDebug("Gestione punto 1 (Carico HTTP Reply vuoto) (Asincroni) [pdd]...");
						if(msgResponse!=null){
							msgResponse.deleteMessageFromFileSystem(); // elimino eventuale risposta salvata su fileSystem
						}

						msgDiag.logPersonalizzato("profiloAsincrono.rispostaNonPervenuta");
						OpenSPCoop2Message responseMessageError = 
								this.generatoreErrore.build(pddContext,IntegrationFunctionError.EXPECTED_RESPONSE_NOT_FOUND,
										ErroriIntegrazione.ERRORE_517_RISPOSTA_RICHIESTA_NON_RITORNATA.
											get517_RispostaRichiestaNonRitornata(bustaRichiesta.getTipoDestinatario()+bustaRichiesta.getDestinatario()),null,
												(responseMessage!=null ? responseMessage.getParseException() : null));							
						ejbUtils.sendRispostaApplicativaErrore(responseMessageError,richiestaDelegata,rollbackRichiesta,pd,sa);
						openspcoopstate.releaseResource();
						esito.setEsitoInvocazione(true);
						esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,
								msgDiag.getMessaggio_replaceKeywords("profiloAsincrono.rispostaNonPervenuta"));
						return esito;
						
					}
					else{
					
						if(protocolManager.isHttpEmptyResponseOneWay())
							msgResponse = ejbUtils.sendRispostaApplicativaOK(MessageUtilities.buildEmptyMessage(OpenSPCoop2MessageFactory.getDefaultMessageFactory(),
									requestInfo.getIntegrationRequestMessageType(),MessageRole.RESPONSE),richiestaDelegata,pd,sa);
						else
							msgResponse = ejbUtils.sendRispostaApplicativaOK(ejbUtils.buildOpenSPCoopOK(requestInfo.getIntegrationRequestMessageType(), idMessageRequest),richiestaDelegata,pd,sa);
					}
					
					
				}
			}








			/* ------- Gestione punto 2 o 3 (MessaggioErroreProtocollo e MessaggioProtocollo) -------- */
			else if(presenzaRispostaProtocollo){
				if(isMessaggioErroreProtocollo){	
					// 2) Ricevuto Messaggio Errore Protocollo da spedire al modulo Sbustamento.
					msgDiag.logPersonalizzato("rispostaRicevuta.messaggioErrore");
				}else{   
					// 3) Ricevuto Messaggio Protocollo (Puo' contenere un riscontro o qualcos'altro...) 
					msgDiag.logPersonalizzato("rispostaRicevuta.messaggio");
				}


				// Consegna al modulo opportuno
				if(functionAsRouter){ 

					msgDiag.mediumDebug("Gestione punto 2/3 (MessaggioErroreProtocollo e MessaggioProtocollo) [router]...");

					// Scenario Sincrono
					if( (org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.SINCRONO.equals(bustaRichiesta.getProfiloDiCollaborazione())) &&
							(!validatoreErrori.isBustaErrore(bustaRichiesta,requestMessageTrasformato,pValidazioneErrori)) ){
						msgDiag.mediumDebug("Gestione punto 2 (MessaggioErroreProtocollo e MessaggioProtocollo) [router] (sincrono)...");
						ejbUtils.sendBustaRisposta(richiestaDelegata.getIdModuloInAttesa(),bustaRisposta,null,
								idCorrelazioneApplicativa,idCorrelazioneApplicativaRisposta,servizioApplicativoFruitore);
					}
					// Scenario NotSincrono
					else{
						msgDiag.mediumDebug("Gestione punto 2 (MessaggioErroreProtocollo e MessaggioProtocollo) [router] (nonSincrono)...");
						ejbUtils.sendBustaRisposta(richiestaDelegata.getIdModuloInAttesa(),bustaRisposta,null,
								idCorrelazioneApplicativa,idCorrelazioneApplicativaRisposta,servizioApplicativoFruitore);
					}

				}

				else{	

					msgDiag.mediumDebug("Gestione punto 2/3 (MessaggioErroreProtocollo e MessaggioProtocollo) [pdd]...");

					// Rollback se la gestione dell'errore mi indica di effettuarlo (se non attesa risposta).
					if(sendRispostaApplicativa==false && errorConsegna && gestioneBusteNonRiscontrateAttive==false){

						// Comunicazione Asincrona (OneWay e Asincroni senza ricevuta applicativa)
						StringBuilder bfDettagliEccezione = new StringBuilder();
						if(bustaRisposta.sizeListaEccezioni()>0)
							msgDiag.addKeyword(CostantiPdD.KEY_ECCEZIONI, bustaRisposta.toStringListaEccezioni(protocolFactory));
						else if(dettaglioEccezione!=null && dettaglioEccezione.getExceptions()!=null){
							for(int i=0; i<dettaglioEccezione.getExceptions().sizeExceptionList(); i++){
								if(i>0)
									bfDettagliEccezione.append("\n");
								org.openspcoop2.core.eccezione.details.Eccezione eccDettaglio = dettaglioEccezione.getExceptions().getException(i);
								if(eccDettaglio.getSeverity()!=null && eccDettaglio.getContext()!=null){
									bfDettagliEccezione.append("Eccezione ("+eccDettaglio.getType()+") "+eccDettaglio.getSeverity()+" con codice ["+eccDettaglio.getCode()+"] - "
											+eccDettaglio.getContext() +", descrizione errore: "+eccDettaglio.getDescription());
								}else{
									bfDettagliEccezione.append("Eccezione ("+eccDettaglio.getType()+") con codice ["+eccDettaglio.getCode()+"], descrizione errore: "+eccDettaglio.getDescription());
								}
							}
							if(dettaglioEccezione.getDetails()!=null){
								for(int i=0; i<dettaglioEccezione.getDetails().sizeDetailList(); i++){
									bfDettagliEccezione.append("\n");
									org.openspcoop2.core.eccezione.details.Dettaglio eccDettaglio = dettaglioEccezione.getDetails().getDetail(i);
									bfDettagliEccezione.append("Dettaglio ["+eccDettaglio.getType()+"]: "+eccDettaglio.getBase());
								}
							}
							msgDiag.addKeyword(CostantiPdD.KEY_ECCEZIONI, bfDettagliEccezione.toString());
						}
						else{
							msgDiag.addKeyword(CostantiPdD.KEY_ECCEZIONI, motivoErroreConsegna);
						}
						msgDiag.logPersonalizzato("ricezioneMessaggioErrore.rollback");
						
						String motivoErroreRiconsegna = null;
						if(bustaRisposta.sizeListaEccezioni()>0)
							motivoErroreRiconsegna = "Consegna ["+tipoConnector+"] con errore: "+bustaRisposta.toStringListaEccezioni(protocolFactory);
						else if(dettaglioEccezione!=null){
							motivoErroreRiconsegna = "Consegna ["+tipoConnector+"] con errore: "+bfDettagliEccezione.toString();
						}
						else{
							motivoErroreRiconsegna = "Consegna ["+tipoConnector+"] con errore: "+motivoErroreConsegna;
						}
						
						if(riconsegna){
							ejbUtils.rollbackMessage(motivoErroreRiconsegna,dataRiconsegna , esito);
							esito.setStatoInvocazione(EsitoLib.ERRORE_NON_GESTITO,motivoErroreRiconsegna);
							esito.setEsitoInvocazione(false);
						}else{
							//TODO CHECKME relaseoutbox con il messaggio di risposta o di richiesta ?
							ejbUtils.releaseOutboxMessage(false);
							esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,motivoErroreRiconsegna);
							esito.setEsitoInvocazione(true);
						}

						if(msgResponse!=null){
							msgResponse.deleteMessageFromFileSystem(); // elimino eventuale risposta salvata su fileSystem
						}
						openspcoopstate.releaseResource();
						return esito;

					}




					//	Verifica xsd  (Se siamo in un caso di risposta applicativa presente)
					if(sendRispostaApplicativa && isMessaggioErroreProtocollo==false && 
							erroriValidazione.size()<=0 &&
							erroriProcessamento.size()<=0){
						msgDiag.mediumDebug("Controllo validazione xsd abilitata/disabilitata...");
						ValidazioneContenutiApplicativi validazioneContenutoApplicativoApplicativo =  null;
						List<Proprieta> proprietaValidazioneContenutoApplicativoApplicativo = null;
						try{ 
							validazioneContenutoApplicativoApplicativo = configurazionePdDManager.getTipoValidazioneContenutoApplicativo(pd,implementazionePdDDestinatario, false);
							proprietaValidazioneContenutoApplicativoApplicativo = pd.getProprietaList();
						}catch(Exception ex){
							msgDiag.logErroreGenerico(ex,"getTipoValidazioneContenutoApplicativo(pd)");
							if(msgResponse!=null){
								msgResponse.deleteMessageFromFileSystem(); // elimino eventuale risposta salvata su fileSystem
							}
							OpenSPCoop2Message responseMessageError = 
									this.generatoreErrore.build(pddContext,IntegrationFunctionError.INTERNAL_RESPONSE_ERROR,
											ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
												get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_536_CONFIGURAZIONE_NON_DISPONIBILE),ex,
													(responseMessage!=null ? responseMessage.getParseException() : null));
							ejbUtils.sendRispostaApplicativaErrore(responseMessageError,richiestaDelegata,rollbackRichiesta,pd,sa);
							openspcoopstate.releaseResource();
							esito.setEsitoInvocazione(true);
							esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,
									"getTipoValidazioneContenutoApplicativo(pd)");
							return esito;
						}
						if(validazioneContenutoApplicativoApplicativo!=null && validazioneContenutoApplicativoApplicativo.getTipo()!=null){
							String tipo = ValidatoreMessaggiApplicativi.getTipo(validazioneContenutoApplicativoApplicativo);
							//this.msgContext.getIntegrazione().setTipoValidazioneContenuti(tipo);
							msgDiag.addKeyword(CostantiPdD.KEY_TIPO_VALIDAZIONE_CONTENUTI, tipo);
							msgDiag.addKeyword(CostantiPdD.KEY_DETAILS_VALIDAZIONE_CONTENUTI,"");
						}
						if(CostantiConfigurazione.STATO_CON_WARNING_ABILITATO.equals(validazioneContenutoApplicativoApplicativo.getStato())||
								CostantiConfigurazione.STATO_CON_WARNING_WARNING_ONLY.equals(validazioneContenutoApplicativoApplicativo.getStato())){

							if(transactionNullable!=null) {
								transactionNullable.getTempiElaborazione().startValidazioneRisposta();
							}
							ByteArrayInputStream binXSD = null;
							try{
								boolean hasContent = false;
								boolean isFault = false;
								if(ServiceBinding.SOAP.equals(responseMessage.getServiceBinding())){
									OpenSPCoop2SoapMessage soapMsg = responseMessage.castAsSoap();
									hasContent = !soapMsg.isSOAPBodyEmpty();
									isFault = soapMsg.isFault();
								}
								else{
									//OpenSPCoop2RestMessage<?> restMsg = responseMessage.castAsRest();
									//hasContent = restMsg.hasContent();
									hasContent = true; // devo controllare gli header etc...
									//isFault = restMsg.isProblemDetailsForHttpApis_RFC7807() || MessageRole.FAULT.equals(responseMessage.getMessageRole());
									// fix: i problem detail devono far parte dell'interfaccia openapi
									isFault = MessageRole.FAULT.equals(responseMessage.getMessageRole());
								}
								
								boolean validazioneAbilitata = true;
								if(ServiceBinding.SOAP.equals(responseMessage.getServiceBinding())){
									validazioneAbilitata = hasContent && (isFault==false);
								}
								else {
									validazioneAbilitata = ValidatoreMessaggiApplicativiRest.isValidazioneAbilitata(proprietaValidazioneContenutoApplicativoApplicativo, responseMessage, codiceRitornato);
								}
								if( validazioneAbilitata ){
									
									msgDiag.logPersonalizzato("validazioneContenutiApplicativiRispostaInCorso");
									
									boolean readInterface = CostantiConfigurazione.VALIDAZIONE_CONTENUTI_APPLICATIVI_INTERFACE.equals(validazioneContenutoApplicativoApplicativo.getTipo());
											
									if(ServiceBinding.SOAP.equals(responseMessage.getServiceBinding())){
									
										// Accept mtom message
										List<MtomXomReference> xomReferences = null;
										if(StatoFunzionalita.ABILITATO.equals(validazioneContenutoApplicativoApplicativo.getAcceptMtomMessage())){
											msgDiag.mediumDebug("Validazione xsd della risposta (mtomFastUnpackagingForXSDConformance)...");
											if(ServiceBinding.SOAP.equals(responseMessage.getServiceBinding())==false){
												throw new Exception("Funzionalita 'AcceptMtomMessage' valida solamente per Service Binding SOAP");
											}
											xomReferences = responseMessage.castAsSoap().mtomFastUnpackagingForXSDConformance();
										}
										
										// Init Validatore
										//System.out.println("MESSAGGIO PRIMA DI VALIDARE: "+requestMessage.getAsString(responseMessage.getSOAPPart().getEnvelope(), false));
										msgDiag.mediumDebug("Validazione della risposta (initValidator)...");
										ValidatoreMessaggiApplicativi validatoreMessaggiApplicativi = 
											new ValidatoreMessaggiApplicativi(registroServiziManager,richiestaDelegata.getIdServizio(),
													responseMessage,readInterface,
													this.propertiesReader.isValidazioneContenutiApplicativi_rpcLiteral_xsiType_gestione(),
													proprietaValidazioneContenutoApplicativoApplicativo,
													pddContext);
	
										// Validazione WSDL 
										if( CostantiConfigurazione.VALIDAZIONE_CONTENUTI_APPLICATIVI_INTERFACE.equals(validazioneContenutoApplicativoApplicativo.getTipo()) 
												||
												CostantiConfigurazione.VALIDAZIONE_CONTENUTI_APPLICATIVI_OPENSPCOOP.equals(validazioneContenutoApplicativoApplicativo.getTipo())
										){
											msgDiag.mediumDebug("Validazione wsdl della risposta ...");
											validatoreMessaggiApplicativi.validateWithWsdlLogicoImplementativo(false);
										}
										
										// Validazione XSD
										msgDiag.mediumDebug("Validazione xsd della risposta (validazione)...");
										validatoreMessaggiApplicativi.validateWithWsdlDefinitorio(false);
										
										// Validazione WSDL (Restore Original Document)
										if (CostantiConfigurazione.VALIDAZIONE_CONTENUTI_APPLICATIVI_INTERFACE.equals(validazioneContenutoApplicativoApplicativo.getTipo())
											|| CostantiConfigurazione.VALIDAZIONE_CONTENUTI_APPLICATIVI_OPENSPCOOP.equals(validazioneContenutoApplicativoApplicativo.getTipo())) {
											if(this.propertiesReader.isValidazioneContenutiApplicativi_rpcLiteral_xsiType_gestione() &&
													this.propertiesReader.isValidazioneContenutiApplicativi_rpcLiteral_xsiType_ripulituraDopoValidazione()){
												msgDiag.mediumDebug("Ripristino elementi modificati per supportare validazione wsdl della risposta ...");
												validatoreMessaggiApplicativi.restoreOriginalDocument(false);
											}
										}
										
										// Ripristino struttura messaggio con xom
										if(xomReferences!=null && xomReferences.size()>0){
											msgDiag.mediumDebug("Validazione xsd della risposta (mtomRestoreAfterXSDConformance)...");
											if(ServiceBinding.SOAP.equals(responseMessage.getServiceBinding())==false){
												throw new Exception("Funzionalita 'AcceptMtomMessage' valida solamente per Service Binding SOAP");
											}
											responseMessage.castAsSoap().mtomRestoreAfterXSDConformance(xomReferences);
										}
										
									}
									else {
										
										// Init Validatore
										msgDiag.mediumDebug("Validazione della risposta (initValidator)...");
										ValidatoreMessaggiApplicativiRest validatoreMessaggiApplicativi = 
											new ValidatoreMessaggiApplicativiRest(registroServiziManager, richiestaDelegata.getIdServizio(), responseMessage, readInterface, proprietaValidazioneContenutoApplicativoApplicativo, 
													protocolFactory, pddContext);
										
										if(CostantiConfigurazione.VALIDAZIONE_CONTENUTI_APPLICATIVI_XSD.equals(validazioneContenutoApplicativoApplicativo.getTipo()) &&
												responseMessage.castAsRest().hasContent()) {
											
											// Validazione XSD
											msgDiag.mediumDebug("Validazione xsd della risposta ...");
											validatoreMessaggiApplicativi.validateWithSchemiXSD(false);
											
										}
										else {
											
											// Validazione Interface
											validatoreMessaggiApplicativi.validateResponseWithInterface(requestMessagePrimaTrasformazione, false);
											
										}
										
									}
									
									msgDiag.logPersonalizzato("validazioneContenutiApplicativiRispostaEffettuata");
								}
								else{
									if(hasContent==false){
										msgDiag.addKeyword(CostantiPdD.KEY_DETAILS_VALIDAZIONE_CONTENUTI,CostantiConfigurazione.VALIDAZIONE_CONTENUTI_APPLICATIVI_VALIDAZIONE_SOAP_BODY_NON_PRESENTE);
										msgDiag.logPersonalizzato("validazioneContenutiApplicativiRispostaDisabilitata");
									}
									else if (isFault){
										msgDiag.addKeyword(CostantiPdD.KEY_DETAILS_VALIDAZIONE_CONTENUTI,CostantiConfigurazione.VALIDAZIONE_CONTENUTI_APPLICATIVI_VALIDAZIONE_SOAP_FAULT_PRESENTE);
										msgDiag.logPersonalizzato("validazioneContenutiApplicativiRispostaDisabilitata");
									}
								}

							}catch(ValidatoreMessaggiApplicativiException ex){
								msgDiag.addKeywordErroreProcessamento(ex);
								this.log.error("[ValidazioneContenutiApplicativi Risposta] "+ex.getMessage(),ex);
								if (CostantiConfigurazione.STATO_CON_WARNING_WARNING_ONLY.equals(validazioneContenutoApplicativoApplicativo.getStato())) {
									msgDiag.logPersonalizzato("validazioneContenutiApplicativiRispostaNonRiuscita.warningOnly");
								}
								else {
									msgDiag.logPersonalizzato("validazioneContenutiApplicativiRispostaNonRiuscita");
								}
								if(CostantiConfigurazione.STATO_CON_WARNING_WARNING_ONLY.equals(validazioneContenutoApplicativoApplicativo.getStato()) == false){
									
									pddContext.addObject(org.openspcoop2.core.constants.Costanti.ERRORE_VALIDAZIONE_RISPOSTA, "true");
									
									IntegrationFunctionError integrationFunctionError = null;
									if(ex.getErrore()!=null &&
											(
													//CodiceErroreIntegrazione.CODICE_417_COSTRUZIONE_VALIDATORE_TRAMITE_INTERFACCIA_FALLITA.equals(ex.getErrore().getCodiceErrore()) ||
													CodiceErroreIntegrazione.CODICE_419_VALIDAZIONE_RISPOSTA_TRAMITE_INTERFACCIA_FALLITA.equals(ex.getErrore().getCodiceErrore()) 
											)
										){
										integrationFunctionError = IntegrationFunctionError.INVALID_RESPONSE_CONTENT;
									}
									else{
										integrationFunctionError = IntegrationFunctionError.INTERNAL_RESPONSE_ERROR;
									}
									
									// validazione abilitata
									if(msgResponse!=null){
										msgResponse.deleteMessageFromFileSystem(); // elimino eventuale risposta salvata su fileSystem
									}
									OpenSPCoop2Message responseMessageError = 
											this.generatoreErrore.build(pddContext,integrationFunctionError,
													ex.getErrore(),ex,
														(responseMessage!=null ? responseMessage.getParseException() : null));
									ejbUtils.sendRispostaApplicativaErrore(responseMessageError,richiestaDelegata,rollbackRichiesta,pd,sa);
									openspcoopstate.releaseResource();
									esito.setEsitoInvocazione(true);
									esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,
											msgDiag.getMessaggio_replaceKeywords("validazioneContenutiApplicativiNonRiuscita"));
									return esito;
								}
							}catch(Exception ex){
								msgDiag.addKeywordErroreProcessamento(ex);
								this.log.error("Riscontrato errore durante la validazione xsd della risposta applicativa",ex);
								if (CostantiConfigurazione.STATO_CON_WARNING_WARNING_ONLY.equals(validazioneContenutoApplicativoApplicativo.getStato())) {
									msgDiag.logPersonalizzato("validazioneContenutiApplicativiRispostaNonRiuscita.warningOnly");
								}
								else {
									msgDiag.logPersonalizzato("validazioneContenutiApplicativiRispostaNonRiuscita");
								}
								if(CostantiConfigurazione.STATO_CON_WARNING_WARNING_ONLY.equals(validazioneContenutoApplicativoApplicativo.getStato()) == false){
									
									pddContext.addObject(org.openspcoop2.core.constants.Costanti.ERRORE_VALIDAZIONE_RISPOSTA, "true");
									
									// validazione abilitata
									if(msgResponse!=null){
										msgResponse.deleteMessageFromFileSystem(); // elimino eventuale risposta salvata su fileSystem
									}
									OpenSPCoop2Message responseMessageError = 
											this.generatoreErrore.build(pddContext,IntegrationFunctionError.INTERNAL_RESPONSE_ERROR,
													ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
														get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_531_VALIDAZIONE_TRAMITE_INTERFACCIA_FALLITA),ex,
															(responseMessage!=null ? responseMessage.getParseException() : null));
									ejbUtils.sendRispostaApplicativaErrore(responseMessageError,richiestaDelegata,rollbackRichiesta,pd,sa);
									openspcoopstate.releaseResource();
									esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,
											msgDiag.getMessaggio_replaceKeywords("validazioneContenutiApplicativiNonRiuscita"));
									esito.setEsitoInvocazione(true);
									return esito;
								}
							}finally{
								if(transactionNullable!=null) {
									transactionNullable.getTempiElaborazione().endValidazioneRisposta();
								}
								if(binXSD!=null){
									try{
										binXSD.close();
									}catch(Exception e){}
								}
							}
						}
						else{
							msgDiag.logPersonalizzato("validazioneContenutiApplicativiRispostaDisabilitata");
						}
					}





					//msgResponse.aggiornaInformazioni(bustaRisposta.getServizio(),bustaRisposta.getAzione());
					msgDiag.mediumDebug("Aggiorna proprietario messaggio risposta...");
					msgResponse.aggiornaProprietarioMessaggio(SbustamentoRisposte.ID_MODULO);
					((OpenSPCoopState)openspcoopstate).setMessageLib(sbustamentoRisposteMSG);
					
					if (openspcoopstate instanceof OpenSPCoopStateful) {

						msgDiag.mediumDebug("Spedizione messaggio verso SbustamentoRisposte...");
						try{
							ejbUtils.getNodeSender(this.propertiesReader, this.log).send(sbustamentoRisposteMSG, SbustamentoRisposte.ID_MODULO, msgDiag, 
									identitaPdD,InoltroBuste.ID_MODULO, idMessageRequest,msgResponse);
						} catch (Exception e) {
							this.log.error("Spedizione->SbustamentoRisposte non riuscita",e);
							if(msgResponse!=null){
								msgResponse.deleteMessageFromFileSystem(); // elimino eventuale risposta salvata su fileSystem
							}
							msgDiag.logErroreGenerico(e,"GenericLib.nodeSender.send(SbustamentoRisposte)");
							if(sendRispostaApplicativa){
								OpenSPCoop2Message responseMessageError = 
										this.generatoreErrore.build(pddContext,IntegrationFunctionError.INTERNAL_RESPONSE_ERROR,
												ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
													get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_512_SEND),e,
														(responseMessage!=null ? responseMessage.getParseException() : null));
								ejbUtils.sendRispostaApplicativaErrore(responseMessageError,richiestaDelegata,rollbackRichiesta,pd,sa);
								esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,
										"Spedizione->SbustamentoRisposte non riuscita");
								esito.setEsitoInvocazione(true);
							}else{
								//Se Non e' attivo una gestione dei riscontri, faccio il rollback sulla coda.
								// Altrimenti verra attivato la gestione dei riscontri che riprovera' dopo un tot.
								if(gestioneBusteNonRiscontrateAttive==false){
									ejbUtils.rollbackMessage("Spedizione risposta al modulo di Sbustamento non riuscito", esito);
									esito.setStatoInvocazione(EsitoLib.ERRORE_NON_GESTITO,
											"Spedizione->SbustamentoRisposte non riuscita");
									esito.setEsitoInvocazione(false);
								}else{
									ejbUtils.updateErroreProcessamentoMessage("Spedizione risposta al modulo di Sbustamento non riuscito", esito);
									esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,
											"Spedizione->SbustamentoRisposte non riuscita");
									esito.setEsitoInvocazione(true);
								}
							}
							openspcoopstate.releaseResource();
							return esito;
						}
					}		

				}
			} 







			/* ------- Gestione punto 4 (SoapFault senza una busta associata) -------- */
			else{

				// Nei casi di soapFault con una busta non rientreremo in questo caso.
				// Nel protocollo trasparente si rientra cmq di sopra poichè viene sempre ritornato 'true' in presenzaBustaRisposta
				pddContext.addObject(org.openspcoop2.core.constants.Costanti.ERRORE_SOAP_FAULT_SERVER, true);
				
				// Fault registrato prima comunque
//				if(fault!=null && faultLogged==false){	
//					msgDiag.addKeyword(CostantiPdD.KEY_SOAP_FAULT, SoapUtils.toString(fault));
//					msgDiag.logPersonalizzato("ricezioneSoapFault");
//					faultLogged = true;
//				}
				if(functionAsRouter){
					msgDiag.mediumDebug("Gestione punto 4 (SoapFault senza una busta associata) [router] ...");

					if(soapFault!=null || restProblem!=null){
						if(soapFault!=null && enrichSoapFaultPdD){
							IntegrationFunctionError integrationFunctionError = IntegrationFunctionError.BAD_RESPONSE;
							this.generatoreErrore.getErroreApplicativoBuilderForAddDetailInSoapFault(pddContext, responseMessage.getMessageType(),integrationFunctionError).
								insertRoutingErrorInSOAPFault(identitaPdD,InoltroBuste.ID_MODULO,
									msgDiag.getMessaggio_replaceKeywords("ricezioneSoapFault"), responseMessage);
						}
						
						if( (org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.SINCRONO.equals(bustaRichiesta.getProfiloDiCollaborazione())==false) &&
								newConnectionForResponse ) {
							// ritorno l'errore su di una nuova connessione.
							ejbUtils.sendAsRispostaBustaErroreProcessamento(richiestaDelegata.getIdModuloInAttesa(), bustaRichiesta, 
									ErroriIntegrazione.ERRORE_518_RISPOSTA_RICHIESTA_RITORNATA_COME_FAULT.
										get518_RispostaRichiestaRitornataComeFault(bustaRichiesta.getTipoDestinatario()+bustaRichiesta.getDestinatario()),
									idCorrelazioneApplicativa,idCorrelazioneApplicativaRisposta,servizioApplicativoFruitore,null,
									(responseMessage!=null ? responseMessage.getParseException() : null));
						}else{
							ejbUtils.sendSOAPFault(richiestaDelegata.getIdModuloInAttesa(), responseMessage);
						}
						
					}else{
					
						// Scenario RoutingSincrono
						if( (org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.SINCRONO.equals(bustaRichiesta.getProfiloDiCollaborazione())) &&
								(!validatoreErrori.isBustaErrore(bustaRichiesta,requestMessageTrasformato,pValidazioneErrori)) ){
							msgDiag.mediumDebug("Gestione punto 4 (SoapFault senza una busta associata) [router] (sincrono)...");
							ejbUtils.sendSbloccoRicezioneBuste(richiestaDelegata.getIdModuloInAttesa());
						}
	
						// Scenario RoutingNotSincrono
						else{
							if(newConnectionForResponse==false){
								msgDiag.logPersonalizzato("ricezioneSoapMessage.headerProtocolloNonPresente");
								ejbUtils.sendSbloccoRicezioneBuste(richiestaDelegata.getIdModuloInAttesa());
							}
						}
					}

				}
				// Scenario Sincrono
				else if(sendRispostaApplicativa){

					msgDiag.mediumDebug("Gestione punto 4 (SoapFault senza una busta associata) [pdd] ...");

					if(msgResponse!=null){
						msgResponse.deleteMessageFromFileSystem(); // elimino eventuale risposta salvata su fileSystem
					}
					boolean fineGestione = true;
					if(soapFault==null && restProblem==null){
						
						if(Costanti.SCENARIO_ONEWAY_INVOCAZIONE_SERVIZIO.equals(richiestaDelegata.getScenario())){
							// Altrimenti entro qua per via del sendRispostaApplicativa=true a caso del oneway stateless
							
							boolean hasContent = false;
							if(ServiceBinding.SOAP.equals(responseMessage.getServiceBinding())){
								OpenSPCoop2SoapMessage soapMsg = responseMessage.castAsSoap();
								hasContent = soapMsg.getSOAPBody()!=null;
								if(hasContent){
									hasContent = SoapUtils.getFirstNotEmptyChildNode(soapMsg.getFactory(), soapMsg.getSOAPBody(), false)!=null;
								}
							}
							else{
								OpenSPCoop2RestMessage<?> restMsg = responseMessage.castAsRest();
								hasContent = restMsg.hasContent();
							}
							
							if(hasContent){
								msgDiag.logPersonalizzato("ricezioneSoapMessage.headerProtocolloNonPresente");
							}
							msgDiag.mediumDebug("Invio messaggio 'OK' al modulo di RicezioneContenutiApplicativi (Carico HTTP Reply vuoto)...");
							if(protocolManager.isHttpEmptyResponseOneWay())
								msgResponse = ejbUtils.sendRispostaApplicativaOK(MessageUtilities.buildEmptyMessage(OpenSPCoop2MessageFactory.getDefaultMessageFactory(),
										requestInfo.getIntegrationRequestMessageType(),MessageRole.RESPONSE),richiestaDelegata,pd,sa);
							else
								msgResponse = ejbUtils.sendRispostaApplicativaOK(ejbUtils.buildOpenSPCoopOK(requestInfo.getIntegrationRequestMessageType(), idMessageRequest),richiestaDelegata,pd,sa);
							fineGestione = false;
						}else{
							// Se non ho un fault, e non sono in profilo oneway stateless segnalo l'anomalia
							msgDiag.logPersonalizzato("ricezioneSoapMessage.headerProtocolloNonPresente");
							OpenSPCoop2Message responseMessageError = this.generatoreErrore.build(pddContext,IntegrationFunctionError.BAD_RESPONSE,
									ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
											get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_528_RISPOSTA_RICHIESTA_NON_VALIDA),
											null,
											(responseMessage!=null ? responseMessage.getParseException() : null));
							ejbUtils.sendRispostaApplicativaErrore(responseMessageError,richiestaDelegata,rollbackRichiesta,pd,sa);
						 }
					}
					else{
						if(enrichSoapFaultPdD){
							IntegrationFunctionError integrationFunctionError = getIntegrationFunctionErroreConnectionError(eccezioneProcessamentoConnettore, motivoErroreConsegna, pddContext);
							this.generatoreErrore.getErroreApplicativoBuilderForAddDetailInSoapFault(pddContext, responseMessage.getMessageType(), integrationFunctionError).
								insertInSOAPFault(ErroriIntegrazione.ERRORE_516_CONNETTORE_UTILIZZO_CON_ERRORE.
										get516_PortaDiDominioNonDisponibile(bustaRichiesta.getTipoDestinatario()+"-"+bustaRichiesta.getDestinatario()), 
										responseMessage);
						}
						ejbUtils.sendRispostaApplicativaErrore(responseMessage,richiestaDelegata,rollbackRichiesta,pd,sa);
					}
					
					if(fineGestione){
						openspcoopstate.releaseResource();
						esito.setEsitoInvocazione(true);
						esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,
								"Gestione punto 4 (SoapFault senza una busta associata) [pdd]");
						return esito;
					}
				}
				// Scenario NotSincrono
				// OK
			}







			Utilities.printFreeMemory("InoltroBuste - Elimino utilizzo busta dalla PdD ...");






			/* ---------- Gestione Transazione Modulo ---------------- */
			msgDiag.mediumDebug("Elimino utilizzo busta dalla PdD ...");
			repositoryBuste.eliminaUtilizzoPdDFromOutBox(idMessageRequest,statelessAsincrono);
			
			if (oneWayVersione11) {
				repositoryBuste.eliminaBustaStatelessFromOutBox(idMessageRequest);
			}
			// Se e' indicato di non eliminare il messaggio di RICHIESTA
			// non deve essere fatto poiche' sara' riutilizzato dal Thread che re-invia la busta
			if(gestioneBusteNonRiscontrateAttive==false){
				msgDiag.mediumDebug("Aggiorno proprietario messaggio richiesta ...");
				msgRequest.aggiornaProprietarioMessaggio(TimerGestoreMessaggi.ID_MODULO);
			}
			else {
				if (oneWayVersione11 && sbustamentoRisposteMSG!=null) {
					// Se sono in onewayVersione11, ho diritto ad utilizzare la connessione.
					// Non devo farla utilizzare pero' per SbustamentoRisposte
					((OpenSPCoopStateless)openspcoopstate).setUseConnection(false); 
					EsitoLib esitoLib = null;
					try{
						SbustamentoRisposte sbustamentoLib = new SbustamentoRisposte(this.log);
						esitoLib = sbustamentoLib.onMessage(openspcoopstate);
					}finally{
						((OpenSPCoopStateless)openspcoopstate).setUseConnection(true);
					}
					if ( ! esitoLib.isEsitoInvocazione() ) 
						throw new Exception(InoltroBuste.ID_MODULO + " chiamata alla libreria SbustamentoRisposte fallita " + esito.getStatoInvocazione());
					else{
						msgDiag.mediumDebug("Aggiorno proprietario messaggio richiesta ...");
						msgRequest.aggiornaProprietarioMessaggio(TimerGestoreMessaggi.ID_MODULO);
					}					
				}
			}

			// messaggio finale
			msgDiag.addKeyword(CostantiPdD.KEY_TIPO_CONNETTORE, tipoConnector);
			msgDiag.logPersonalizzato("gestioneConsegnaTerminata");

			// Commit JDBC
			msgDiag.mediumDebug("Commit delle operazioni per la gestione della richiesta...");
			openspcoopstate.commit();

			// Rilascio connessione al DB
			msgDiag.mediumDebug("Rilascio connessione al database...");
			openspcoopstate.releaseResource();

			// Aggiornamento cache messaggio
			if(msgRequest!=null)
				msgRequest.addMessaggiIntoCache_readFromTable(InoltroBuste.ID_MODULO, "richiesta");
			if(msgResponse!=null)
				msgResponse.addMessaggiIntoCache_readFromTable(InoltroBuste.ID_MODULO, "risposta");

			//	Aggiornamento cache proprietario messaggio
			if(msgRequest!=null)
				msgRequest.addProprietariIntoCache_readFromTable(InoltroBuste.ID_MODULO, "richiesta",null,functionAsRouter);
			if(msgResponse!=null)
				msgResponse.addProprietariIntoCache_readFromTable(InoltroBuste.ID_MODULO, "risposta",idMessageRequest,functionAsRouter);


			msgDiag.mediumDebug("Lavoro Terminato.");
			esito.setEsitoInvocazione(true);	
			esito.setStatoInvocazione(EsitoLib.OK, null);
			return esito; 


		}catch(Throwable e){
			this.log.error("ErroreGenerale",e);
			msgDiag.logErroreGenerico(e, "Generale");

			if(openspcoopstate.resourceReleased()){
				msgDiag.logErroreGenerico(e, "ErroreGeneraleNonGestibile");
				esito.setEsitoInvocazione(false);
				esito.setStatoInvocazioneErroreNonGestito(e);
				return esito;
			}else{
				if ( msgResponse!=null ){
					msgResponse.deleteMessageFromFileSystem(); // elimino eventuale risposta salvata su fileSystem
				}

				if( sendRispostaApplicativa ){
					
					ParseException parseException = null;
					if(useResponseForParseException) {
						parseException = (responseMessage!=null ? responseMessage.getParseException() : null);
					}
					else {
						parseException = (requestMessagePrimaTrasformazione!=null ? requestMessagePrimaTrasformazione.getParseException() : null);	
					}
					
					OpenSPCoop2Message responseMessageError = 
							this.generatoreErrore.build(pddContext,AbstractErrorGenerator.getIntegrationInternalError(pddContext),
									ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.getErroreIntegrazione(),e,
									parseException);
					try{
						ejbUtils.sendRispostaApplicativaErrore(responseMessageError,richiestaDelegata,rollbackRichiesta,pd,sa);
						esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO, "ErroreGenerale");
						esito.setEsitoInvocazione(true);
					}catch(Exception er){
						msgDiag.logErroreGenerico(er,"ejbUtils.sendErroreGenerale(profiloConRisposta)");
						ejbUtils.rollbackMessage("Spedizione Errore al Mittente durante una richiesta sincrona non riuscita", esito);
						esito.setStatoInvocazioneErroreNonGestito(er);
						esito.setEsitoInvocazione(false);
					}
				}else{
					ejbUtils.rollbackMessage("ErroreGenerale:"+e.getMessage(), esito);
					esito.setStatoInvocazioneErroreNonGestito(e);
					esito.setEsitoInvocazione(false);
				}
				openspcoopstate.releaseResource();
				return esito;
			}
		}finally{
			try{
				if(connectorSenderForDisconnect!=null)
					connectorSenderForDisconnect.disconnect();
			}catch(Exception e){
				try{
					if(msgDiag!=null)
						msgDiag.logDisconnectError(e, location);
				}catch(Exception eDisconnect){
					this.log.error("Errore durante la chiusura delle connessione: "+eDisconnect.getMessage(),e);
				}
			}
			// *** GB ***
			if(validatore!=null){
				if(validatore.getValidatoreSintattico()!=null){
					validatore.getValidatoreSintattico().setHeaderSOAP(null);
				}
				validatore.setValidatoreSintattico(null);
			}
			validatore = null;
			headerProtocolloRisposta = null;
			// *** GB ***
		}
	}
	
	private EsitoElaborazioneMessaggioTracciato gestioneTracciamentoFineConnettore(boolean errorConsegna, 
			SOAPFault soapfault, ProblemRFC7807 restProblem,
			org.openspcoop2.protocol.sdk.config.ITraduttore traduttore,
			MsgDiagnostico msgDiag, String motivoErroreConsegna, OpenSPCoop2Message responseMessage,
			boolean isBlockedTransaction_responseMessageWithTransportCodeError,
			boolean functionAsRouter, boolean sendRispostaApplicativa,
			StringBuilder bfMsgErroreSituazioneAnomale) throws Exception {
		
		String msgErroreSituazioneAnomale = null;

		// Tracciamento effettuato:

		// - SEMPRE se vi e' da tornare una risposta applicativa (sendRispostaApplicativa=true)

		// - SEMPRE se si ha e' un router: functionAsRouter

		// - SEMPPRE se non vi sono errori (errorConsegna=false)
		
		// Settings msgDiag
		if(errorConsegna){
			// Una busta contiene per forza di cose un SOAPFault
			// Una busta la si riconosce dal motivo di errore.
			if(soapfault!=null && soapfault.getFaultString()!=null && 
					(soapfault.getFaultString().equals(traduttore.toString(MessaggiFaultErroreCooperazione.FAULT_STRING_VALIDAZIONE)) || soapfault.getFaultString().equals(traduttore.toString(MessaggiFaultErroreCooperazione.FAULT_STRING_PROCESSAMENTO)))){
				msgDiag.addKeyword(CostantiPdD.KEY_ERRORE_CONSEGNA, soapfault.getFaultString());
				msgDiag.logPersonalizzato("inoltroConErrore");
			}else{
				msgDiag.addKeyword(CostantiPdD.KEY_ERRORE_CONSEGNA, motivoErroreConsegna);
				msgDiag.logPersonalizzato("inoltroConErrore");
				
				// Controllo Situazione Anomala ISSUE OP-7
				if(responseMessage!=null && ServiceBinding.SOAP.equals(responseMessage.getServiceBinding())){
					OpenSPCoop2SoapMessage soapMessageResponse = responseMessage.castAsSoap();
					if(soapMessageResponse.getSOAPPart()!=null && 
							soapMessageResponse.getSOAPPart().getEnvelope()!=null &&
							(soapMessageResponse.isSOAPBodyEmpty() || (!soapMessageResponse.hasSOAPFault()))
							){
						msgDiag.logPersonalizzato("comportamentoAnomalo.erroreConsegna.ricezioneMessaggioDiversoFault");
						if(isBlockedTransaction_responseMessageWithTransportCodeError){
							msgErroreSituazioneAnomale = msgDiag.getMessaggio_replaceKeywords("comportamentoAnomalo.erroreConsegna.ricezioneMessaggioDiversoFault");
							this.log.error(msgErroreSituazioneAnomale);
							bfMsgErroreSituazioneAnomale.append(msgErroreSituazioneAnomale);
						}
					}
				}
			}
		}else{	
			if(soapfault!=null && soapfault.getFaultString()!=null && 
					(soapfault.getFaultString().equals(traduttore.toString(MessaggiFaultErroreCooperazione.FAULT_STRING_VALIDAZIONE))) ){
				msgDiag.addKeyword(CostantiPdD.KEY_ERRORE_CONSEGNA, soapfault.getFaultString());
				msgDiag.logPersonalizzato("inoltroConErrore");
			}
			else{
				msgDiag.logPersonalizzato("inoltroEffettuato");
			}
		}
		
		// Se non vi e' da tornare una risposta applicativa
		// - Se vi e' una consegna con errore, e non vi e' un soap fault, non viene tracciato (verra' rispedito).
		// - Altrimenti viene tracciato anche se:
		//       - messaggio errore protocollo
		//       - messaggio protocollo con fault applicativo
		//       - FaultServer
		if( (errorConsegna==false) || (functionAsRouter) || (sendRispostaApplicativa) || 
				( soapfault!=null) ){
			msgDiag.mediumDebug("Tracciamento della richiesta...");
			EsitoElaborazioneMessaggioTracciato esitoTraccia = null;
			if(errorConsegna){
				esitoTraccia = EsitoElaborazioneMessaggioTracciato.getEsitoElaborazioneConErrore(msgDiag.getMessaggio_replaceKeywords("inoltroConErrore"));
			}else{
				if(soapfault!=null && soapfault.getFaultString()!=null && 
						(soapfault.getFaultString().equals(traduttore.toString(MessaggiFaultErroreCooperazione.FAULT_STRING_VALIDAZIONE))) ){
					esitoTraccia = EsitoElaborazioneMessaggioTracciato.getEsitoElaborazioneConErrore(msgDiag.getMessaggio_replaceKeywords("inoltroConErrore"));
				}else{
					esitoTraccia = EsitoElaborazioneMessaggioTracciato.getEsitoElaborazioneMessaggioInviato();
				}
			}
			return esitoTraccia;
		}
		
		return null;

	}
	
	private IntegrationFunctionError getIntegrationFunctionErroreConnectionError(Exception eccezioneProcessamentoConnettore, String motivoErroreConsegna, PdDContext pddContext) {
		IntegrationFunctionError integrationFunctionError = IntegrationFunctionError.SERVICE_UNAVAILABLE;
		
		String responseReadTimeout = null;
		if(pddContext!=null && pddContext.containsKey(TimeoutInputStream.ERROR_MSG_KEY)) {
			String timeoutMessage = PdDContext.getValue(TimeoutInputStream.ERROR_MSG_KEY, pddContext);
			if(timeoutMessage!=null && timeoutMessage.startsWith(CostantiPdD.PREFIX_TIMEOUT_RESPONSE)) {
				responseReadTimeout = timeoutMessage;
			}
		}
		if(responseReadTimeout!=null) {
			integrationFunctionError = IntegrationFunctionError.ENDPOINT_REQUEST_TIMED_OUT;
			pddContext.removeObject(TimeoutInputStream.ERROR_MSG_KEY);
			pddContext.removeObject(TimeoutInputStream.EXCEPTION_KEY);
		}
		else if(eccezioneProcessamentoConnettore!=null && motivoErroreConsegna!=null) {
			if(this.propertiesReader.isServiceUnavailable_ReadTimedOut(motivoErroreConsegna)){
				integrationFunctionError = IntegrationFunctionError.ENDPOINT_REQUEST_TIMED_OUT;
			}
		}
		return integrationFunctionError;
	}
}
