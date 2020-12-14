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


package org.openspcoop2.pdd.services.core;

import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;

import org.openspcoop2.core.config.Connettore;
import org.openspcoop2.core.config.CorrelazioneApplicativa;
import org.openspcoop2.core.config.CorsConfigurazione;
import org.openspcoop2.core.config.DumpConfigurazione;
import org.openspcoop2.core.config.GestioneTokenAutenticazione;
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.Proprieta;
import org.openspcoop2.core.config.ResponseCachingConfigurazione;
import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.config.ValidazioneContenutiApplicativi;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import org.openspcoop2.core.config.constants.TipoGestioneCORS;
import org.openspcoop2.core.config.driver.DriverConfigurazioneNotFound;
import org.openspcoop2.core.constants.CostantiConnettori;
import org.openspcoop2.core.constants.TipoPdD;
import org.openspcoop2.core.id.IDPortaApplicativa;
import org.openspcoop2.core.id.IDPortaDelegata;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.id.IdentificativiFruizione;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.Resource;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziAzioneNotFound;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziCorrelatoNotFound;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziException;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziNotFound;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziPortTypeNotFound;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.core.registry.driver.IDServizioFactory;
import org.openspcoop2.core.transazioni.utils.CredenzialiMittente;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.OpenSPCoop2MessageFactory;
import org.openspcoop2.message.constants.MessageRole;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.message.soap.TunnelSoapUtils;
import org.openspcoop2.message.soap.mtom.MtomXomReference;
import org.openspcoop2.message.utils.MessageUtilities;
import org.openspcoop2.pdd.config.ClassNameProperties;
import org.openspcoop2.pdd.config.ConfigurazioneCanaliNodo;
import org.openspcoop2.pdd.config.ConfigurazionePdDManager;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.config.RichiestaApplicativa;
import org.openspcoop2.pdd.config.RichiestaDelegata;
import org.openspcoop2.pdd.core.AbstractCore;
import org.openspcoop2.pdd.core.CORSFilter;
import org.openspcoop2.pdd.core.CORSWrappedHttpServletResponse;
import org.openspcoop2.pdd.core.CostantiPdD;
import org.openspcoop2.pdd.core.GestoreCorrelazioneApplicativa;
import org.openspcoop2.pdd.core.GestoreMessaggi;
import org.openspcoop2.pdd.core.GestoreMessaggiException;
import org.openspcoop2.pdd.core.IntegrationContext;
import org.openspcoop2.pdd.core.LocalForwardEngine;
import org.openspcoop2.pdd.core.LocalForwardParameter;
import org.openspcoop2.pdd.core.PdDContext;
import org.openspcoop2.pdd.core.ProtocolContext;
import org.openspcoop2.pdd.core.StatoServiziPdD;
import org.openspcoop2.pdd.core.ValidatoreMessaggiApplicativi;
import org.openspcoop2.pdd.core.ValidatoreMessaggiApplicativiException;
import org.openspcoop2.pdd.core.ValidatoreMessaggiApplicativiRest;
import org.openspcoop2.pdd.core.autenticazione.GestoreAutenticazione;
import org.openspcoop2.pdd.core.autenticazione.ParametriAutenticazione;
import org.openspcoop2.pdd.core.autenticazione.pd.EsitoAutenticazionePortaDelegata;
import org.openspcoop2.pdd.core.autorizzazione.GestoreAutorizzazione;
import org.openspcoop2.pdd.core.autorizzazione.container.AutorizzazioneHttpServletRequest;
import org.openspcoop2.pdd.core.autorizzazione.container.IAutorizzazioneSecurityContainer;
import org.openspcoop2.pdd.core.autorizzazione.pd.DatiInvocazionePortaDelegata;
import org.openspcoop2.pdd.core.autorizzazione.pd.EsitoAutorizzazionePortaDelegata;
import org.openspcoop2.pdd.core.connettori.InfoConnettoreIngresso;
import org.openspcoop2.pdd.core.credenziali.Credenziali;
import org.openspcoop2.pdd.core.credenziali.GestoreCredenzialiConfigurationException;
import org.openspcoop2.pdd.core.credenziali.IGestoreCredenziali;
import org.openspcoop2.pdd.core.handlers.GestoreHandlers;
import org.openspcoop2.pdd.core.handlers.HandlerException;
import org.openspcoop2.pdd.core.handlers.InRequestContext;
import org.openspcoop2.pdd.core.handlers.InRequestProtocolContext;
import org.openspcoop2.pdd.core.handlers.OutResponseContext;
import org.openspcoop2.pdd.core.integrazione.HeaderIntegrazione;
import org.openspcoop2.pdd.core.integrazione.IGestoreIntegrazionePD;
import org.openspcoop2.pdd.core.integrazione.IGestoreIntegrazionePDSoap;
import org.openspcoop2.pdd.core.integrazione.InRequestPDMessage;
import org.openspcoop2.pdd.core.integrazione.OutResponsePDMessage;
import org.openspcoop2.pdd.core.node.INodeReceiver;
import org.openspcoop2.pdd.core.node.INodeSender;
import org.openspcoop2.pdd.core.node.NodeTimeoutException;
import org.openspcoop2.pdd.core.response_caching.HashGenerator;
import org.openspcoop2.pdd.core.state.IOpenSPCoopState;
import org.openspcoop2.pdd.core.state.OpenSPCoopState;
import org.openspcoop2.pdd.core.state.OpenSPCoopStateful;
import org.openspcoop2.pdd.core.state.OpenSPCoopStateless;
import org.openspcoop2.pdd.core.token.GestoreToken;
import org.openspcoop2.pdd.core.token.InformazioniToken;
import org.openspcoop2.pdd.core.token.PolicyGestioneToken;
import org.openspcoop2.pdd.core.token.pd.EsitoGestioneTokenPortaDelegata;
import org.openspcoop2.pdd.core.token.pd.EsitoPresenzaTokenPortaDelegata;
import org.openspcoop2.pdd.core.token.pd.GestioneToken;
import org.openspcoop2.pdd.core.transazioni.Transaction;
import org.openspcoop2.pdd.core.transazioni.TransactionContext;
import org.openspcoop2.pdd.logger.Dump;
import org.openspcoop2.pdd.logger.MsgDiagnosticiProperties;
import org.openspcoop2.pdd.logger.MsgDiagnostico;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.pdd.logger.Tracciamento;
import org.openspcoop2.pdd.mdb.ConsegnaContenutiApplicativi;
import org.openspcoop2.pdd.mdb.EsitoLib;
import org.openspcoop2.pdd.mdb.ImbustamentoMessage;
import org.openspcoop2.pdd.mdb.InoltroBuste;
import org.openspcoop2.pdd.mdb.SbustamentoRisposte;
import org.openspcoop2.pdd.services.OpenSPCoop2Startup;
import org.openspcoop2.pdd.services.ServicesUtils;
import org.openspcoop2.pdd.services.connector.messages.ConnectorInMessage;
import org.openspcoop2.pdd.services.error.AbstractErrorGenerator;
import org.openspcoop2.pdd.services.error.RicezioneContenutiApplicativiInternalErrorGenerator;
import org.openspcoop2.pdd.services.skeleton.IntegrationManager;
import org.openspcoop2.pdd.timers.TimerGestoreMessaggi;
import org.openspcoop2.pdd.timers.TimerLock;
import org.openspcoop2.pdd.timers.TimerMonitoraggioRisorseThread;
import org.openspcoop2.pdd.timers.TimerThresholdThread;
import org.openspcoop2.pdd.timers.TipoLock;
import org.openspcoop2.protocol.basic.registry.IdentificazionePortaDelegata;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.engine.RequestInfo;
import org.openspcoop2.protocol.engine.URLProtocolContext;
import org.openspcoop2.protocol.engine.builder.Imbustamento;
import org.openspcoop2.protocol.engine.constants.Costanti;
import org.openspcoop2.protocol.engine.driver.RepositoryBuste;
import org.openspcoop2.protocol.engine.mapping.IdentificazioneDinamicaException;
import org.openspcoop2.protocol.engine.validator.ValidazioneSintattica;
import org.openspcoop2.protocol.registry.RegistroServiziManager;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.Integrazione;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.ProtocolMessage;
import org.openspcoop2.protocol.sdk.Servizio;
import org.openspcoop2.protocol.sdk.builder.IBustaBuilder;
import org.openspcoop2.protocol.sdk.builder.ProprietaErroreApplicativo;
import org.openspcoop2.protocol.sdk.builder.ProprietaManifestAttachments;
import org.openspcoop2.protocol.sdk.config.IProtocolConfiguration;
import org.openspcoop2.protocol.sdk.config.IProtocolManager;
import org.openspcoop2.protocol.sdk.config.IProtocolVersionManager;
import org.openspcoop2.protocol.sdk.config.ITraduttore;
import org.openspcoop2.protocol.sdk.constants.CodiceErroreIntegrazione;
import org.openspcoop2.protocol.sdk.constants.ErroreIntegrazione;
import org.openspcoop2.protocol.sdk.constants.ErroriIntegrazione;
import org.openspcoop2.protocol.sdk.constants.FaseSbustamento;
import org.openspcoop2.protocol.sdk.constants.FunzionalitaProtocollo;
import org.openspcoop2.protocol.sdk.constants.IntegrationFunctionError;
import org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione;
import org.openspcoop2.protocol.sdk.constants.RuoloMessaggio;
import org.openspcoop2.protocol.sdk.constants.StatoFunzionalitaProtocollo;
import org.openspcoop2.protocol.sdk.dump.DumpException;
import org.openspcoop2.protocol.sdk.tracciamento.TracciamentoException;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.resources.Loader;
import org.openspcoop2.utils.transport.http.CORSRequestType;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.slf4j.Logger;

/**
 * Implementazione del servizio RicezioneContenutiApplicativi di OpenSPCoop
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class RicezioneContenutiApplicativi {

	/**
	 * Variabile che indica il Nome del modulo dell'architettura di OpenSPCoop
	 * rappresentato da questa classe
	 */
	public final static String ID_MODULO = "RicezioneContenutiApplicativi";

	/** Indicazione se sono state inizializzate le variabili del servizio */
	public static boolean initializeService = false;

	/** IGestoreIntegrazionePD: lista di gestori, ordinati per priorita' minore */
	private static String[] defaultGestoriIntegrazionePD = null;
	private static java.util.concurrent.ConcurrentHashMap<String, String[]> defaultPerProtocolloGestoreIntegrazionePD = null;

	/** IGestoreCredenziali: lista di gestori delle credenziali */
	private static String [] tipiGestoriCredenziali = null;
	
	/**
	 * Inizializzatore del servizio RicezioneContenutiApplicativi
	 * 
	 * @throws Exception
	 */
	public synchronized static void initializeService(
			ConfigurazionePdDManager configReader,
			ClassNameProperties className,
			OpenSPCoop2Properties propertiesReader, Logger logCore)
			throws Exception {
		if (RicezioneContenutiApplicativi.initializeService)
			return; // inizializzato da un altro thread

		Loader loader = Loader.getInstance();
		
		// Inizializzazione NodeSender
		String classTypeNodeSender = className.getNodeSender(propertiesReader.getNodeSender());
		try {
			INodeSender nodeSender = (INodeSender) loader.newInstance(classTypeNodeSender);
			nodeSender.toString();
			logCore.info("Inizializzazione gestore NodeSender di tipo "	+ classTypeNodeSender + " effettuata.");
		} catch (Exception e) {
			throw new Exception(
					"Riscontrato errore durante il caricamento della classe ["+ classTypeNodeSender
							+ "] da utilizzare per la spedizione nell'infrastruttura: "	+ e.getMessage());
		}

		// Inizializzazione NodeReceiver
		String classType = className.getNodeReceiver(propertiesReader.getNodeReceiver());
		try {
			INodeReceiver nodeReceiver = (INodeReceiver) loader.newInstance(classType);
			nodeReceiver.toString();
			logCore.info("Inizializzazione gestore NodeReceiver di tipo "+ classType + " effettuata.");
		} catch (Exception e) {
			throw new Exception(
					"Riscontrato errore durante il caricamento della classe ["+ classType
							+ "] da utilizzare per la ricezione dall'infrastruttura: "+ e.getMessage());
		}

		// Inizializzo IGestoreIntegrazionePD list
		RicezioneContenutiApplicativi.defaultGestoriIntegrazionePD = propertiesReader.getTipoIntegrazionePD();
		for (int i = 0; i < RicezioneContenutiApplicativi.defaultGestoriIntegrazionePD.length; i++) {
			classType = className.getIntegrazionePortaDelegata(RicezioneContenutiApplicativi.defaultGestoriIntegrazionePD[i]);
			try {
				IGestoreIntegrazionePD gestore = (IGestoreIntegrazionePD) loader.newInstance(classType);
				gestore.toString();
				logCore	.info("Inizializzazione gestore integrazione servizioApplicativo->PdD di tipo "
								+ RicezioneContenutiApplicativi.defaultGestoriIntegrazionePD[i]	+ " effettuata.");
			} catch (Exception e) {
				throw new Exception(
						"Riscontrato errore durante il caricamento della classe ["+ classType
								+ "] da utilizzare per la gestione dell'integrazione di tipo ["
								+ RicezioneContenutiApplicativi.defaultGestoriIntegrazionePD[i]+ "]: " + e.getMessage());
			}
		}
		
		// Inizializzo IGestoreIntegrazionePA per protocollo
		RicezioneContenutiApplicativi.defaultPerProtocolloGestoreIntegrazionePD = new java.util.concurrent.ConcurrentHashMap<String, String[]>();
		Enumeration<String> enumProtocols = ProtocolFactoryManager.getInstance().getProtocolNames();
		while (enumProtocols.hasMoreElements()) {
			String protocol = (String) enumProtocols.nextElement();
			String[] tipiIntegrazionePD = propertiesReader.getTipoIntegrazionePD(protocol);
			if(tipiIntegrazionePD!=null && tipiIntegrazionePD.length>0){
				List<String> tipiIntegrazionePerProtocollo = new ArrayList<String>();
				for (int i = 0; i < tipiIntegrazionePD.length; i++) {
					classType = className.getIntegrazionePortaDelegata(tipiIntegrazionePD[i]);
					try {
						IGestoreIntegrazionePD test = (IGestoreIntegrazionePD)loader.newInstance(classType);
						test.toString();
						tipiIntegrazionePerProtocollo.add(tipiIntegrazionePD[i]);
						logCore	.info("Inizializzazione gestore per lettura integrazione PD di tipo "
								+ tipiIntegrazionePD[i]	+ " effettuata.");
					} catch (Exception e) {
						throw new Exception(
								"Riscontrato errore durante il caricamento della classe ["+ classType
								+ "] da utilizzare per la gestione dell'integrazione di tipo ["
								+ tipiIntegrazionePD[i]+ "]: " + e.getMessage());
					}
				}
				if(tipiIntegrazionePerProtocollo.size()>0){
					RicezioneContenutiApplicativi.defaultPerProtocolloGestoreIntegrazionePD.put(protocol, tipiIntegrazionePerProtocollo.toArray(new String[1]));
				}
			}
		}

		// Inizializzo GestoriCredenziali PD
		RicezioneContenutiApplicativi.tipiGestoriCredenziali = propertiesReader.getTipoGestoreCredenzialiPD();
		if(RicezioneContenutiApplicativi.tipiGestoriCredenziali!=null){
			for (int i = 0; i < RicezioneContenutiApplicativi.tipiGestoriCredenziali.length; i++) {
				classType = className.getGestoreCredenziali(RicezioneContenutiApplicativi.tipiGestoriCredenziali[i]);
				try {
					IGestoreCredenziali gestore = (IGestoreCredenziali)loader.newInstance(classType);
					gestore.toString();
					logCore	.info("Inizializzazione gestore credenziali di tipo "
							+ RicezioneContenutiApplicativi.tipiGestoriCredenziali[i]	+ " effettuata.");
				} catch (Exception e) {
					throw new Exception(
							"Riscontrato errore durante il caricamento della classe ["+ classType
							+ "] da utilizzare per la gestione delle credenziali di tipo ["
							+ RicezioneContenutiApplicativi.tipiGestoriCredenziali[i]+ "]: " + e.getMessage());
				}
			}
		}
		
		RicezioneContenutiApplicativi.initializeService = true;
	}



	/** Generatore Errori */
	private RicezioneContenutiApplicativiInternalErrorGenerator generatoreErrore;
	
	/** Contesto della richiesta */
	private RicezioneContenutiApplicativiContext msgContext;
	
	public RicezioneContenutiApplicativi(
			RicezioneContenutiApplicativiContext context,
			RicezioneContenutiApplicativiInternalErrorGenerator generatoreErrore) {
		this.msgContext = context;
		this.generatoreErrore = generatoreErrore;
	}

	public void process(Object ... params) {
				
		
		// ------------- dati generali -----------------------------
		
		// Context
		PdDContext context = this.msgContext.getPddContext();
		
		// Logger
		Logger logCore = OpenSPCoop2Logger.getLoggerOpenSPCoopCore();
		if (logCore == null) {
			logCore = LoggerWrapperFactory.getLogger(RicezioneContenutiApplicativi.ID_MODULO);
		}
		
		// MsgDiagnostico
		MsgDiagnostico msgDiag = this.msgContext.getMsgDiagnostico();
		
		// Messaggio
		OpenSPCoop2Message requestMessage = this.msgContext.getMessageRequest();
		if (requestMessage == null) {
			setSOAPFault(IntegrationFunctionError.INTERNAL_REQUEST_ERROR, logCore, msgDiag, new Exception("Request message is null"), "LetturaMessaggioRichiesta");
			return;
		}
		
		
		
		
		// ------------- in-handler -----------------------------
		IProtocolFactory<?> protocolFactory = null;
		try{
			protocolFactory = ProtocolFactoryManager.getInstance().getProtocolFactoryByName((String)context.getObject(org.openspcoop2.core.constants.Costanti.PROTOCOL_NAME));
		}catch(Exception e){
			setSOAPFault(IntegrationFunctionError.GOVWAY_NOT_INITIALIZED, logCore, msgDiag, e, "ProtocolFactoryInstance");
			return;
		}
		InRequestContext inRequestContext = new InRequestContext(logCore,protocolFactory, null);
		// TipoPorta
		inRequestContext.setTipoPorta(TipoPdD.DELEGATA);
		inRequestContext.setIdModulo(this.msgContext.getIdModulo());
		// Informazioni connettore ingresso
		InfoConnettoreIngresso connettore = new InfoConnettoreIngresso();
		connettore.setCredenziali(this.msgContext.getCredenziali());
		if(this.msgContext.getUrlProtocolContext()!=null && 
				this.msgContext.getUrlProtocolContext().getHttpServletRequest()!=null){
			OpenSPCoop2Properties properties = OpenSPCoop2Properties.getInstance(); // Puo' non essere inizializzato
			if(properties!=null){
				String tipo = properties.getRealContainerCustom();
				if(tipo!=null) {
					try {
						ClassNameProperties className = ClassNameProperties.getInstance();
						Loader loader = Loader.getInstance();
						// Check tipi registrati
						String tipoClass = className.getRealmContainerCustom(tipo);
						IAutorizzazioneSecurityContainer authEngine = (IAutorizzazioneSecurityContainer) loader.newInstance(tipoClass);
						authEngine.init(this.msgContext.getUrlProtocolContext().getHttpServletRequest(), 
								context, protocolFactory);
						AutorizzazioneHttpServletRequest httpServletRequestAuth = new AutorizzazioneHttpServletRequest(this.msgContext.getUrlProtocolContext().getHttpServletRequest(), authEngine);
						this.msgContext.getUrlProtocolContext().updateHttpServletRequest(httpServletRequestAuth);					
					}catch(Exception e){
						setSOAPFault(IntegrationFunctionError.INTERNAL_REQUEST_ERROR, logCore, msgDiag, e, "AutorizzazioneSecurityContainerInstance");
						return;
					}
				}
			}
		}
		connettore.setUrlProtocolContext(this.msgContext.getUrlProtocolContext());
		if(ServiceBinding.SOAP.equals(requestMessage.getServiceBinding())){
			try{
				connettore.setSoapAction(requestMessage.castAsSoap().getSoapAction());
			}catch(Exception e){
				setSOAPFault(IntegrationFunctionError.INTERNAL_REQUEST_ERROR, logCore,msgDiag, e, "LetturaSoapAction");
				return;
			}
		}
		connettore.setFromLocation(this.msgContext.getSourceLocation());
		inRequestContext.setConnettore(connettore);
		// Data accettazione richiesta
		inRequestContext.setDataAccettazioneRichiesta(this.msgContext.getDataAccettazioneRichiesta());
		// Data ingresso richiesta
		inRequestContext.setDataElaborazioneMessaggio(this.msgContext.getDataIngressoRichiesta());
		// PdDContext
		inRequestContext.setPddContext(context);
		// Dati Messaggio
		inRequestContext.setMessaggio(requestMessage);
		// Invoke handler
		try{
			GestoreHandlers.inRequest(inRequestContext, msgDiag, logCore);
		}catch(HandlerException e){
			setSOAPFault(IntegrationFunctionError.INTERNAL_REQUEST_ERROR, logCore,msgDiag, e, e.getIdentitaHandler());
			return;
		}catch(Exception e){
			setSOAPFault(IntegrationFunctionError.INTERNAL_REQUEST_ERROR, logCore,msgDiag, e, "InvocazioneInRequestHandler");
			return;
		}
		
		
		
		
		
			
		// ------------- process -----------------------------
		HashMap<String, Object> internalObjects = new HashMap<>();
		try{
			process_engine(inRequestContext,internalObjects,params);
		} catch(TracciamentoException e){
			setSOAPFault(AbstractErrorGenerator.getIntegrationInternalError(context), logCore,msgDiag, e, "TracciamentoNonRiuscito");
			return;
		} catch(DumpException e){
			setSOAPFault(AbstractErrorGenerator.getIntegrationInternalError(context), logCore,msgDiag, e, "DumpNonRiuscito");
			return;
		} catch(ProtocolException e){
			setSOAPFault(AbstractErrorGenerator.getIntegrationInternalError(context), logCore,msgDiag, e, "InstanziazioneProtocolFactoryNonRiuscita");
			return;
		} 
		
		try{
			if(context!=null  && this.msgContext.getIntegrazione()!=null){
				if(context.containsKey(CostantiPdD.KEY_TIPO_PROCESSAMENTO_MTOM_RICHIESTA)){
					this.msgContext.getIntegrazione().setTipoProcessamentoMtomXopRichiesta(
							(String)context.getObject(CostantiPdD.KEY_TIPO_PROCESSAMENTO_MTOM_RICHIESTA));
				}
				if(context.containsKey(CostantiPdD.KEY_TIPO_PROCESSAMENTO_MTOM_RISPOSTA)){
					this.msgContext.getIntegrazione().setTipoProcessamentoMtomXopRisposta(
							(String)context.getObject(CostantiPdD.KEY_TIPO_PROCESSAMENTO_MTOM_RISPOSTA));
				}
				if(context.containsKey(CostantiPdD.KEY_TIPO_SICUREZZA_MESSAGGIO_RICHIESTA)){
					this.msgContext.getIntegrazione().setTipoMessageSecurityRichiesta(
							(String)context.getObject(CostantiPdD.KEY_TIPO_SICUREZZA_MESSAGGIO_RICHIESTA));
				}
				if(context.containsKey(CostantiPdD.KEY_TIPO_SICUREZZA_MESSAGGIO_RISPOSTA)){
					this.msgContext.getIntegrazione().setTipoMessageSecurityRisposta(
							(String)context.getObject(CostantiPdD.KEY_TIPO_SICUREZZA_MESSAGGIO_RISPOSTA));
				}
			}
		}catch(Exception e){
			setSOAPFault(AbstractErrorGenerator.getIntegrationInternalError(context), logCore,msgDiag, e, "FinalizeIntegrationContextRicezioneContenutiApplicativi");
			return;
		}
		
		
		

		
		
		
		
		// ------------- Dump richiesta in ingresso -----------------------------
		if(internalObjects.containsKey(CostantiPdD.DUMP_RICHIESTA_EFFETTUATO)==false) {
			if(Dump.sistemaDumpDisponibile){
				try{
					ConfigurazionePdDManager configurazionePdDReader = ConfigurazionePdDManager.getInstance();	
					
					if(internalObjects.containsKey(CostantiPdD.DUMP_CONFIG)==false) {
						URLProtocolContext urlProtocolContext = this.msgContext.getUrlProtocolContext();
						if(urlProtocolContext!=null && urlProtocolContext.getInterfaceName()!=null) {
							IDPortaDelegata identificativoPortaDelegata = new IDPortaDelegata();
							identificativoPortaDelegata.setNome(urlProtocolContext.getInterfaceName());
							PortaDelegata portaDelegata = configurazionePdDReader.getPortaDelegata_SafeMethod(identificativoPortaDelegata);
							if(portaDelegata!=null) {
								DumpConfigurazione dumpConfig = configurazionePdDReader.getDumpConfigurazione(portaDelegata);
								internalObjects.put(CostantiPdD.DUMP_CONFIG, dumpConfig);
							}
						}
					}
					
					OpenSPCoop2Message msgRichiesta = inRequestContext.getMessaggio();
					if (msgRichiesta!=null) {
						
						Dump dumpApplicativo = getDump(configurazionePdDReader, protocolFactory, internalObjects, msgDiag.getPorta());
						dumpApplicativo.dumpRichiestaIngresso(msgRichiesta, 
								inRequestContext.getConnettore().getUrlProtocolContext());
					}
				}catch(DumpException e){
					setSOAPFault(AbstractErrorGenerator.getIntegrationInternalError(context), logCore,msgDiag, e, "DumpNonRiuscito");
					return;
				}catch(Exception e){
					// Se non riesco ad accedere alla configurazione sicuramente gia' nel messaggio di risposta e' presente l'errore di PdD non correttamente inizializzata
				}
			}
		}
		
		
		
		
		
		// ------------- out-handler -----------------------------
		OutResponseContext outResponseContext = new OutResponseContext(logCore,protocolFactory, null);
		// TipoPorta
		outResponseContext.setTipoPorta(this.msgContext.getTipoPorta());
		outResponseContext.setIdModulo(this.msgContext.getIdModulo());
		// DataUscitaMessaggio
		outResponseContext.setDataElaborazioneMessaggio(DateManager.getDate());
		// PddContext
		outResponseContext.setPddContext(inRequestContext.getPddContext());
		// Informazioni protocollo e di integrazione
		outResponseContext.setProtocollo(this.msgContext.getProtocol());
		outResponseContext.setIntegrazione(this.msgContext.getIntegrazione());
		// Header di trasporto della risposta
		outResponseContext.setPropertiesRispostaTrasporto(this.msgContext.getHeaderIntegrazioneRisposta());
		// Messaggio
		OpenSPCoop2Message msgResponse = this.msgContext.getMessageResponse();
		outResponseContext.setMessaggio(msgResponse);
		// Invoke handler
		try{
			GestoreHandlers.outResponse(outResponseContext, msgDiag, logCore);
		}catch(HandlerException e){
			setSOAPFault(AbstractErrorGenerator.getIntegrationInternalError(context), logCore,msgDiag, e, e.getIdentitaHandler());
			return;
		}catch(Exception e){
			setSOAPFault(AbstractErrorGenerator.getIntegrationInternalError(context), logCore,msgDiag, e, "InvocazioneOutResponseHandler");
			return;
		}
		

		
		
		
		// ---------------- fine gestione ------------------------------
		OpenSPCoop2Message msgRisposta = null;
		try{
			msgRisposta = outResponseContext.getMessaggio();
			boolean rispostaPresente = true;
			OpenSPCoop2Properties properties = OpenSPCoop2Properties.getInstance(); // Puo' non essere inizializzato
			if(properties!=null){
				rispostaPresente = ServicesUtils.verificaRispostaRelazioneCodiceTrasporto202(protocolFactory,OpenSPCoop2Properties.getInstance(), msgRisposta,true);
			}
			if(rispostaPresente){
				this.msgContext.setMessageResponse(msgRisposta);
			}else{
				this.msgContext.setMessageResponse(null);
				msgRisposta = null;
			}
		}catch(Exception e){
			setSOAPFault(AbstractErrorGenerator.getIntegrationInternalError(context), logCore,msgDiag, e, "FineGestioneRicezioneContenutiApplicativi");
			return;
		}
		
		
		
		
		
		
		
		
		
		// ------------- Dump risposta in uscita-----------------------------
		if(Dump.sistemaDumpDisponibile){
			try{
				ConfigurazionePdDManager configurazionePdDReader = ConfigurazionePdDManager.getInstance();	
				if (msgRisposta!=null) {
					
					Dump dumpApplicativo = getDump(configurazionePdDReader, protocolFactory, internalObjects, msgDiag.getPorta());
					if(outResponseContext.getPropertiesRispostaTrasporto()==null) {
						outResponseContext.setPropertiesRispostaTrasporto(new HashMap<String, String>());
					}
					Map<String, String> propertiesTrasporto = outResponseContext.getPropertiesRispostaTrasporto();
					ServicesUtils.setGovWayHeaderResponse(propertiesTrasporto, logCore, true, outResponseContext.getPddContext(), this.msgContext.getRequestInfo().getProtocolContext());
					dumpApplicativo.dumpRispostaUscita(msgRisposta, 
							inRequestContext.getConnettore().getUrlProtocolContext(), 
							outResponseContext.getPropertiesRispostaTrasporto());
				}
			}catch(DumpException e){
				setSOAPFault(AbstractErrorGenerator.getIntegrationInternalError(context), logCore,msgDiag, e, "DumpNonRiuscito");
				return;
			}catch(Exception e){
				logCore.error(e.getMessage(),e);
				// Se non riesco ad accedere alla configurazione sicuramente gia' nel messaggio di risposta e' presente l'errore di PdD non correttamente inizializzata
			}
		}
		
		
	}
	
	private Dump getDump(ConfigurazionePdDManager configurazionePdDReader,
			IProtocolFactory<?> protocolFactory,
			HashMap<String, Object> internalObjects,
			String nomePorta) throws DumpException, DriverRegistroServiziException {
		
		DumpConfigurazione dumpConfig = null;
		if(internalObjects.containsKey(CostantiPdD.DUMP_CONFIG)) {
			dumpConfig = (DumpConfigurazione) internalObjects.get(CostantiPdD.DUMP_CONFIG); // dovrebbe essere stata impostata per la pd/pa specifica
		}
		else {
			dumpConfig = configurazionePdDReader.getDumpConfigurazione();
		}
		
		ProtocolContext protocolContext = this.msgContext.getProtocol();
		URLProtocolContext urlProtocolContext = this.msgContext.getUrlProtocolContext();
		IDSoggetto soggettoErogatore = null;
		IDServizio idServizio = null;
		IDSoggetto fruitore = null;
		IDSoggetto dominio = null;
		String idRichiesta = null;
		if(protocolContext!=null) {
			if(protocolContext.getTipoServizio()!=null && protocolContext.getServizio()!=null && protocolContext.getVersioneServizio()!=null &&
				protocolContext.getErogatore()!=null && protocolContext.getErogatore().getTipo()!=null && protocolContext.getErogatore().getNome()!=null) {
				idServizio = IDServizioFactory.getInstance().getIDServizioFromValues(protocolContext.getTipoServizio(), protocolContext.getServizio(), 
						protocolContext.getErogatore(), protocolContext.getVersioneServizio());
			}
			dominio = protocolContext.getDominio();
			idRichiesta = protocolContext.getIdRichiesta();
			if(protocolContext.getFruitore()!=null && protocolContext.getFruitore().getTipo()!=null && protocolContext.getFruitore().getNome()!=null) {
				fruitore = protocolContext.getFruitore();
			}
		}
		
		if(dominio == null || fruitore==null || idServizio == null) {
			if(urlProtocolContext!=null && urlProtocolContext.getInterfaceName()!=null) {
				IDPortaDelegata identificativoPortaDelegata = new IDPortaDelegata();
				identificativoPortaDelegata.setNome(urlProtocolContext.getInterfaceName());
				PortaDelegata portaDelegata = null;
				try {
					portaDelegata = configurazionePdDReader.getPortaDelegata_SafeMethod(identificativoPortaDelegata);
				}catch(Exception e) {}
				if(portaDelegata!=null) {
					// Aggiorno tutti
					soggettoErogatore = new IDSoggetto(portaDelegata.getSoggettoErogatore().getTipo(),portaDelegata.getSoggettoErogatore().getNome());
					if(portaDelegata.getServizio()!=null) {
						idServizio = IDServizioFactory.getInstance().getIDServizioFromValues(portaDelegata.getServizio().getTipo(),portaDelegata.getServizio().getNome(), 
									soggettoErogatore, portaDelegata.getServizio().getVersione());
					}
					dominio = new IDSoggetto(portaDelegata.getTipoSoggettoProprietario(), portaDelegata.getNomeSoggettoProprietario());
					fruitore = new IDSoggetto(portaDelegata.getTipoSoggettoProprietario(), portaDelegata.getNomeSoggettoProprietario());
					try {
						dominio.setCodicePorta(RegistroServiziManager.getInstance().getDominio(dominio, null, protocolFactory));
					}catch(Exception e) {
						dominio = OpenSPCoop2Properties.getInstance().getIdentitaPortaDefault(protocolFactory.getProtocol());
					}
				}
			}
		}
		if(idServizio!=null) {
			if(protocolContext!=null && protocolContext.getAzione()!=null) {
				idServizio.setAzione(protocolContext.getAzione());
			}
			else if(this.msgContext.getRequestInfo()!=null && 
					this.msgContext.getRequestInfo().getIdServizio()!=null && this.msgContext.getRequestInfo().getIdServizio().getAzione()!=null) {
				idServizio.setAzione(this.msgContext.getRequestInfo().getIdServizio().getAzione());
			}
		}
		if(dominio==null) {
			dominio = OpenSPCoop2Properties.getInstance().getIdentitaPortaDefault(protocolFactory.getProtocol());
		}

		Dump dumpApplicativo = null;
		if(idServizio!=null){
			dumpApplicativo = new Dump(dominio,
					this.msgContext.getIdModulo(), 
					idRichiesta, fruitore, idServizio,
					this.msgContext.getTipoPorta(), nomePorta, this.msgContext.getPddContext(),
					null,null,
					dumpConfig);
		}else{
			dumpApplicativo = new Dump(dominio,
					this.msgContext.getIdModulo(),this.msgContext.getTipoPorta(), nomePorta,this.msgContext.getPddContext(),
					null,null,
					dumpConfig);
		}
		
		return  dumpApplicativo;
	}
	
	private void setSOAPFault(IntegrationFunctionError integrationFunctionError, Logger logCore, MsgDiagnostico msgDiag, Exception e, String posizione){
		
		HandlerException he = null;
		if(e!=null && (e instanceof HandlerException)){
			he = (HandlerException) e;
		}
		
		if(msgDiag!=null){
			if(he!=null){
				if(he.isEmettiDiagnostico()){
					msgDiag.logErroreGenerico(e, posizione);
				}
			}else{
				msgDiag.logErroreGenerico(e, posizione);
			}
		}
		else {
			logCore.error(posizione+": "+e.getMessage(),e);
		}
		
		IntegrationFunctionError ifError = integrationFunctionError;
		if(he!=null && he.getIntegrationFunctionError()!=null) {
			ifError = he.getIntegrationFunctionError();
		}
		
		if (this.msgContext.isGestioneRisposta()) {
			String posizioneFault = posizione+": "+e.getMessage();
			OpenSPCoop2Message messageFault = this.generatoreErrore.build(this.msgContext.getPddContext(), ifError, 
					ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.get5XX_ErroreProcessamento(posizioneFault),
					e, null);
			this.msgContext.setMessageResponse(messageFault);
		}
	}
	

	private void process_engine(InRequestContext inRequestContext,HashMap<String, Object> internalObjects,Object ... params) 
			throws TracciamentoException, DumpException, ProtocolException {

	
		
		/* ------------ Lettura parametri della richiesta ------------- */

		// Messaggio di ingresso
		OpenSPCoop2Message requestMessage = inRequestContext.getMessaggio();
				
		// Logger
		Logger logCore = inRequestContext.getLogCore();
		
		// Data Ingresso Richiesta
		Date dataIngressoRichiesta = this.msgContext.getDataIngressoRichiesta();
		
		// ID Transazione
		String idTransazione = PdDContext.getValue(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE, inRequestContext.getPddContext());
		
		// RequestInfo
		RequestInfo requestInfo = this.msgContext.getRequestInfo();
		
		// Parametri della porta delegata invocata
		URLProtocolContext urlProtocolContext = this.msgContext.getUrlProtocolContext();

		// Credenziali utilizzate nella richiesta
		Credenziali credenziali = this.msgContext.getCredenziali();

		// Autenticazione/Autorizzazione registrate
		ClassNameProperties className = ClassNameProperties.getInstance();

		// PropertiesReader
		OpenSPCoop2Properties propertiesReader = OpenSPCoop2Properties.getInstance();
		PdDContext pddContext = inRequestContext.getPddContext();
		if (propertiesReader == null) {
			String msg = "Inizializzazione di OpenSPCoop non correttamente effettuata: OpenSPCoopProperties";
			logCore.error(msg);
			if (this.msgContext.isGestioneRisposta()) {
				this.msgContext.setMessageResponse(this.generatoreErrore.build(pddContext, IntegrationFunctionError.GOVWAY_NOT_INITIALIZED, 
						ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
						get5XX_ErroreProcessamento(msg,CodiceErroreIntegrazione.CODICE_501_PDD_NON_INIZIALIZZATA), null, null));
			}
			return;
		}

		// Configurazione PdD Reader
		ConfigurazionePdDManager configurazionePdDReader = ConfigurazionePdDManager.getInstance();

		// RegistroServizi Reader
		RegistroServiziManager registroServiziReader = RegistroServiziManager.getInstance();

		// IdentificativoPdD
		IDSoggetto identitaPdD = requestInfo.getIdentitaPdD();

		// ProtocolFactory
		IProtocolFactory<?> protocolFactory = requestInfo.getProtocolFactory();
		ITraduttore traduttore = protocolFactory.createTraduttore();
		IProtocolManager protocolManager = protocolFactory.createProtocolManager();
		IProtocolConfiguration protocolConfig = protocolFactory.createProtocolConfiguration();
		
		// ProprietaErroreApplicativo
		ProprietaErroreApplicativo proprietaErroreAppl = propertiesReader
				.getProprietaGestioneErrorePD(protocolManager);
		proprietaErroreAppl.setDominio(identitaPdD.getCodicePorta());
		proprietaErroreAppl.setIdModulo(this.msgContext.getIdModulo());
		if(this.msgContext.isForceFaultAsXML()){
			proprietaErroreAppl.setFaultAsXML(true); // es. se siamo in una richiesta http senza SOAP, un SoapFault non ha senso
		}
		this.msgContext.setProprietaErroreAppl(proprietaErroreAppl);
		this.generatoreErrore.updateProprietaErroreApplicativo(proprietaErroreAppl);
				
		// MESSAGGIO DI LIBRERIA
		ImbustamentoMessage imbustamentoMSG = new ImbustamentoMessage();
		
		// Context di risposta
		this.msgContext.setProtocol(new ProtocolContext());
		this.msgContext.getProtocol().setDominio(this.msgContext.getIdentitaPdD());
		this.msgContext.setIntegrazione(new IntegrationContext());

		
		
		
		
		
		/* ------------ Controllo inizializzazione OpenSPCoop ------------------ */
		if (OpenSPCoop2Startup.initialize == false) {
			String msgErrore = "Inizializzazione di OpenSPCoop non correttamente effettuata";
			logCore.error("["+ RicezioneContenutiApplicativi.ID_MODULO+ "]  "+msgErrore);
			try{
				// provo ad emetter un diagnostico
				if(this.msgContext.getMsgDiagnostico()!=null){
					this.msgContext.getMsgDiagnostico().logErroreGenerico(msgErrore,"InizializzazionePdD");
				}
			}catch(Throwable t){logCore.error("Emissione diagnostico per errore inizializzazione non riuscita: "+t.getMessage(),t);}
			if (this.msgContext.isGestioneRisposta()) {
				this.msgContext.setMessageResponse((this.generatoreErrore.build(pddContext, IntegrationFunctionError.GOVWAY_NOT_INITIALIZED, 
						ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
						get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_501_PDD_NON_INIZIALIZZATA),null,null)));
			}
			return;
		}
		if (TimerMonitoraggioRisorseThread.risorseDisponibili == false) {
			String msgErrore = "Risorse di sistema non disponibili: "+ TimerMonitoraggioRisorseThread.risorsaNonDisponibile.getMessage();
			logCore.error("["+ RicezioneContenutiApplicativi.ID_MODULO+ "]  "+msgErrore,TimerMonitoraggioRisorseThread.risorsaNonDisponibile);
			try{
				// provo ad emetter un diagnostico
				if(this.msgContext.getMsgDiagnostico()!=null){
					this.msgContext.getMsgDiagnostico().logErroreGenerico(msgErrore,"InizializzazioneRisorsePdD");
				}
			}catch(Throwable t){logCore.error("Emissione diagnostico per errore inizializzazione non riuscita: "+t.getMessage(),t);}
			if (this.msgContext.isGestioneRisposta()) {
				this.msgContext.setMessageResponse((this.generatoreErrore.build(pddContext, IntegrationFunctionError.GOVWAY_RESOURCES_NOT_AVAILABLE, 
						ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
						get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_532_RISORSE_NON_DISPONIBILI),null,null)));
			}
			return;
		}
		if (TimerThresholdThread.freeSpace == false) {
			String msgErrore = "Non sono disponibili abbastanza risorse per la gestione della richiesta";
			logCore.error("["+ RicezioneContenutiApplicativi.ID_MODULO+ "]  "+msgErrore);
			try{
				// provo ad emetter un diagnostico
				if(this.msgContext.getMsgDiagnostico()!=null){
					this.msgContext.getMsgDiagnostico().logErroreGenerico(msgErrore,"DisponibilitaRisorsePdD");
				}
			}catch(Throwable t){logCore.error("Emissione diagnostico per errore inizializzazione non riuscita: "+t.getMessage(),t);}
			if (this.msgContext.isGestioneRisposta()) {
				this.msgContext.setMessageResponse((this.generatoreErrore.build(pddContext, IntegrationFunctionError.GOVWAY_RESOURCES_NOT_AVAILABLE, 
						ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
						get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_533_RISORSE_DISPONIBILI_LIVELLO_CRITICO),null,null)));
			}
			return;
		}
		if (Tracciamento.tracciamentoDisponibile == false) {
			String msgErrore = "Tracciatura non disponibile: "+ Tracciamento.motivoMalfunzionamentoTracciamento.getMessage();
			logCore.error("["+ RicezioneContenutiApplicativi.ID_MODULO
					+ "]  "+msgErrore,Tracciamento.motivoMalfunzionamentoTracciamento);
			try{
				// provo ad emetter un diagnostico
				if(this.msgContext.getMsgDiagnostico()!=null){
					this.msgContext.getMsgDiagnostico().logErroreGenerico(msgErrore,"Tracciamento");
				}
			}catch(Throwable t){logCore.error("Emissione diagnostico per errore inizializzazione non riuscita: "+t.getMessage(),t);}
			if (this.msgContext.isGestioneRisposta()) {
				this.msgContext.setMessageResponse((this.generatoreErrore.build(pddContext, IntegrationFunctionError.GOVWAY_RESOURCES_NOT_AVAILABLE, 
						ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
						get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_545_TRACCIATURA_NON_FUNZIONANTE),null,null)));
			}
			return;
		}
		if (MsgDiagnostico.gestoreDiagnosticaDisponibile == false) {
			String msgErrore = "Sistema di diagnostica non disponibile: "+ MsgDiagnostico.motivoMalfunzionamentoDiagnostici.getMessage();
			logCore.error("["+ RicezioneContenutiApplicativi.ID_MODULO
					+ "]  "+msgErrore,MsgDiagnostico.motivoMalfunzionamentoDiagnostici);
			try{
				// provo ad emetter un diagnostico lo stesso (molto probabilmente non ci riuscirà essendo proprio la risorsa diagnostica non disponibile)
				if(this.msgContext.getMsgDiagnostico()!=null){
					this.msgContext.getMsgDiagnostico().logErroreGenerico(msgErrore,"Diagnostica");
				}
			}catch(Throwable t){logCore.debug("Emissione diagnostico per errore inizializzazione non riuscita: "+t.getMessage(),t);}
			if (this.msgContext.isGestioneRisposta()) {
				this.msgContext.setMessageResponse((this.generatoreErrore.build(pddContext, IntegrationFunctionError.GOVWAY_RESOURCES_NOT_AVAILABLE, 
						ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
						get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_546_DIAGNOSTICA_NON_FUNZIONANTE),null,null)));
			}
			return;
		}
		if (Dump.sistemaDumpDisponibile == false) {
			String msgErrore = "Sistema di dump dei contenuti applicativi non disponibile: "+ Dump.motivoMalfunzionamentoDump.getMessage();
			logCore.error("["+ RicezioneContenutiApplicativi.ID_MODULO
					+ "]  "+msgErrore,Dump.motivoMalfunzionamentoDump);
			try{
				// provo ad emetter un diagnostico
				if(this.msgContext.getMsgDiagnostico()!=null){
					this.msgContext.getMsgDiagnostico().logErroreGenerico(msgErrore,"Dump");
				}
			}catch(Throwable t){logCore.error("Emissione diagnostico per errore inizializzazione non riuscita: "+t.getMessage(),t);}
			if (this.msgContext.isGestioneRisposta()) {
				this.msgContext.setMessageResponse((this.generatoreErrore.build(pddContext, IntegrationFunctionError.GOVWAY_RESOURCES_NOT_AVAILABLE, 
						ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
						get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_547_DUMP_CONTENUTI_APPLICATIVI_NON_FUNZIONANTE),null,null)));
			}
			return;
		}
		// Check Configurazione (XML)
		try{
			configurazionePdDReader.verificaConsistenzaConfigurazione();	
		}catch(Exception e){
			String msgErrore = "Riscontrato errore durante la verifica della consistenza della configurazione PdD";
			logCore.error("["+ RicezioneContenutiApplicativi.ID_MODULO
					+ "]  "+msgErrore,e);
			try{
				// provo ad emetter un diagnostico
				if(this.msgContext.getMsgDiagnostico()!=null){
					this.msgContext.getMsgDiagnostico().logErroreGenerico(msgErrore,"CheckConfigurazionePdD");
				}
			}catch(Throwable t){logCore.error("Emissione diagnostico per errore inizializzazione non riuscita: "+t.getMessage(),t);}
			if (this.msgContext.isGestioneRisposta()) {
				this.msgContext.setMessageResponse((this.generatoreErrore.build(pddContext, IntegrationFunctionError.GOVWAY_RESOURCES_NOT_AVAILABLE, 
						ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
						get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_536_CONFIGURAZIONE_NON_DISPONIBILE),null,null)));
			}
			return;
		}
		// Check RegistroServizi (XML)
		try{
			registroServiziReader.verificaConsistenzaRegistroServizi();
		}catch(Exception e){
			String msgErrore = "Riscontrato errore durante la verifica del registro dei servizi";
			logCore.error("["+ RicezioneContenutiApplicativi.ID_MODULO
					+ "]  "+msgErrore,e);
			try{
				// provo ad emetter un diagnostico
				if(this.msgContext.getMsgDiagnostico()!=null){
					this.msgContext.getMsgDiagnostico().logErroreGenerico(msgErrore,"CheckRegistroServizi");
				}
			}catch(Throwable t){logCore.error("Emissione diagnostico per errore inizializzazione non riuscita: "+t.getMessage(),t);}
			if (this.msgContext.isGestioneRisposta()) {
				this.msgContext.setMessageResponse((this.generatoreErrore.build(pddContext, IntegrationFunctionError.GOVWAY_RESOURCES_NOT_AVAILABLE, 
						ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
						get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_534_REGISTRO_DEI_SERVIZI_NON_DISPONIBILE),null,null)));
			}
			return;
		}
		
		// Logger dei messaggi diagnostici
		String nomePorta = null;
		if(requestInfo.getProtocolContext().getInterfaceName()!=null){
			nomePorta = requestInfo.getProtocolContext().getInterfaceName();
		}
		else{
			nomePorta = urlProtocolContext.getFunctionParameters() + "_urlInvocazione("+ urlProtocolContext.getUrlInvocazione_formBased() + ")";
		}
		MsgDiagnostico msgDiag = MsgDiagnostico.newInstance(TipoPdD.DELEGATA,identitaPdD, this.msgContext.getIdModulo(),nomePorta);
		this.msgContext.setMsgDiagnostico(msgDiag); // aggiorno msg diagnostico
		msgDiag.setPddContext(inRequestContext.getPddContext(), protocolFactory);
		msgDiag.setPrefixMsgPersonalizzati(MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_CONTENUTI_APPLICATIVI);
		

		// set credenziali
		setCredenziali(credenziali, msgDiag);

		// inizializzazione risorse statiche
		try {
			if (RicezioneContenutiApplicativi.initializeService == false) {
				msgDiag.mediumDebug("Inizializzazione risorse statiche...");
				RicezioneContenutiApplicativi.initializeService(configurazionePdDReader,className,propertiesReader, logCore);
			}
		} catch (Exception e) {
			msgDiag.logErroreGenerico(e,"InizializzazioneRisorseServizioRicezioneContenutiApplicativi");
			if (this.msgContext.isGestioneRisposta()) {
				this.msgContext.setMessageResponse((this.generatoreErrore.build(pddContext, IntegrationFunctionError.GOVWAY_NOT_INITIALIZED, 
						ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
						get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_501_PDD_NON_INIZIALIZZATA),e,null)));
			}
			return;
		}

		// Loader classi dinamiche
		Loader loader = Loader.getInstance();
		
		// ConnectorInMessage
		@SuppressWarnings("unused")
		ConnectorInMessage connectorInMessage = null;
		if(params!=null){
			for (int i = 0; i < params.length; i++) {
				if(params[i]!=null && (params[i] instanceof ConnectorInMessage) ){
					connectorInMessage = (ConnectorInMessage) params[i];
					break;
				}
			}
		}
		
		Transaction transaction = null;
		try{
			transaction = TransactionContext.getTransaction(idTransazione);
		}catch(Exception e){
			msgDiag.logErroreGenerico(e,"getTransaction");
			if (this.msgContext.isGestioneRisposta()) {
				this.msgContext.setMessageResponse((this.generatoreErrore.build(pddContext, IntegrationFunctionError.INTERNAL_REQUEST_ERROR, 
						ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
						get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_500_ERRORE_INTERNO),e,null)));
			}
			return;
		}
		
		
		

		// --------- OPENSPCOOPSTATE ---------
		OpenSPCoopState openspcoopstate = null;
		try{ // finally in fondo, vedi  #try-finally-openspcoopstate#
			
		msgDiag.mediumDebug("Inizializzazione connessione al database...");
		try {
			openspcoopstate = new OpenSPCoopStateful();
			openspcoopstate.initResource(identitaPdD, this.msgContext.getIdModulo(), idTransazione);
		} catch (Exception e) {
			msgDiag.logErroreGenerico(e,"openspcoopstate.initResource()");
			openspcoopstate.releaseResource();
			if (this.msgContext.isGestioneRisposta()) {
				this.msgContext.setMessageResponse((this.generatoreErrore.build(pddContext,IntegrationFunctionError.GOVWAY_RESOURCES_NOT_AVAILABLE, 
						ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
						get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_505_GET_DB_CONNECTION), e,null)));
			}
			return;
		}
		
		// Refresh reader
		registroServiziReader.updateState(openspcoopstate.getStatoRichiesta(),openspcoopstate.getStatoRisposta());
		configurazionePdDReader.updateState(openspcoopstate.getStatoRichiesta(),openspcoopstate.getStatoRisposta());
		msgDiag.updateState(openspcoopstate.getStatoRichiesta(),openspcoopstate.getStatoRisposta());
		
		
		
		
		

		
		

		
		
		Utilities.printFreeMemory("RicezioneContenutiApplicativi - Identificazione porta delegata e soggetto fruitore...");
		
		/* ------------ Identificazione Porta Delegata e SoggettoFruitore */
		msgDiag.mediumDebug("Identificazione porta delegata e soggetto fruitore...");
		IDPortaDelegata identificativoPortaDelegata = null;
		PortaDelegata portaDelegata = null;
		String nomeUtilizzatoPerErrore = null;
		try {
			if(urlProtocolContext.getInterfaceName()!=null) {
				identificativoPortaDelegata = new IDPortaDelegata();
				identificativoPortaDelegata.setNome(urlProtocolContext.getInterfaceName());
				portaDelegata = configurazionePdDReader.getPortaDelegata(identificativoPortaDelegata);
				nomeUtilizzatoPerErrore = identificativoPortaDelegata.getNome();
			}
			else {
				throw new Exception("InterfaceName non presente");
			}
		} catch (Exception e) {
			msgDiag.logErroreGenerico(e,"getPorta");
			openspcoopstate.releaseResource();
			if (this.msgContext.isGestioneRisposta()) {
				this.msgContext.setMessageResponse((this.generatoreErrore.build(pddContext,IntegrationFunctionError.INTERNAL_REQUEST_ERROR, 
						ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
						get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_502_IDENTIFICAZIONE_PORTA), e,null)));
			}
			return;
		}

		// Raccolgo dati
		IDSoggetto soggettoFruitore = null;
		if(portaDelegata!=null) {
			soggettoFruitore = new IDSoggetto(portaDelegata.getTipoSoggettoProprietario(), portaDelegata.getNomeSoggettoProprietario());
		}
		try {
			soggettoFruitore.setCodicePorta(configurazionePdDReader.getIdentificativoPorta(soggettoFruitore, protocolFactory));
		} catch (Exception e) {
			msgDiag.logErroreGenerico(e,"getIdentificativoPorta");
			openspcoopstate.releaseResource();
			if (this.msgContext.isGestioneRisposta()) {
				this.msgContext.setMessageResponse((this.generatoreErrore.build(pddContext,IntegrationFunctionError.INTERNAL_REQUEST_ERROR, 
						ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
						get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_502_IDENTIFICAZIONE_PORTA), e,null)));
			}
			return;
		}
		if(identificativoPortaDelegata.getIdentificativiFruizione()==null) {
			identificativoPortaDelegata.setIdentificativiFruizione(new IdentificativiFruizione());
		}
		identificativoPortaDelegata.getIdentificativiFruizione().setSoggettoFruitore(soggettoFruitore);
		identitaPdD = soggettoFruitore; // la PdD Assume l'identita del soggetto
		this.msgContext.getProtocol().setDominio(identitaPdD);
		this.msgContext.setIdentitaPdD(identitaPdD);
		// che possiede la Porta Delegata ID Porta Delegata
		this.msgContext.getIntegrazione().setIdPD(identificativoPortaDelegata);
		// altri contesti
		msgDiag.setDominio(identitaPdD); // imposto anche il dominio nel msgDiag
		msgDiag.setFruitore(soggettoFruitore);
		msgDiag.addKeyword(CostantiPdD.KEY_PORTA_DELEGATA, identificativoPortaDelegata.getNome());
		msgDiag.addKeywords(soggettoFruitore);
		proprietaErroreAppl.setDominio(identitaPdD.getCodicePorta()); // imposto
		// requestInfo
		requestInfo.setIdentitaPdD(identitaPdD);
		// anche il dominio per gli errori
		this.msgContext.setProprietaErroreAppl(proprietaErroreAppl);
		// GeneratoreErrore
		this.generatoreErrore.updateDominio(identitaPdD);
		this.generatoreErrore.updateProprietaErroreApplicativo(proprietaErroreAppl);
	
		
		
		
		
		

		
		
		
		
		
		
		
		
		
		
		Utilities.printFreeMemory("RicezioneContenutiApplicativi - Lettura header di integrazione...");
		

		/* --------------- Header Integrazione --------------- */
		msgDiag.mediumDebug("Lettura header di integrazione...");
		HeaderIntegrazione headerIntegrazioneRichiesta = null;
		if (this.msgContext.getHeaderIntegrazioneRichiesta() != null)
			headerIntegrazioneRichiesta = this.msgContext.getHeaderIntegrazioneRichiesta(); // prendo quello dell'IntegrationManager
		else
			headerIntegrazioneRichiesta = new HeaderIntegrazione(idTransazione);
		HeaderIntegrazione headerIntegrazioneRisposta = null;
		String[] tipiIntegrazionePD = null;
		try {
			tipiIntegrazionePD = configurazionePdDReader.getTipiIntegrazione(portaDelegata);
		} catch (Exception e) {
			msgDiag.logErroreGenerico(e, "getTipiIntegrazione(pd)");
			openspcoopstate.releaseResource();
			if (this.msgContext.isGestioneRisposta()) {
				this.msgContext.setMessageResponse((this.generatoreErrore.build(pddContext,IntegrationFunctionError.INTERNAL_REQUEST_ERROR, 
						ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
						get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_536_CONFIGURAZIONE_NON_DISPONIBILE), e,null)));
			}
			return;
		}
		if (tipiIntegrazionePD == null){
			if(RicezioneContenutiApplicativi.defaultPerProtocolloGestoreIntegrazionePD.containsKey(protocolFactory.getProtocol()))
				tipiIntegrazionePD = RicezioneContenutiApplicativi.defaultPerProtocolloGestoreIntegrazionePD.get(protocolFactory.getProtocol());
			else
				tipiIntegrazionePD = RicezioneContenutiApplicativi.defaultGestoriIntegrazionePD;
		}

		InRequestPDMessage inRequestPDMessage = new InRequestPDMessage();
		inRequestPDMessage.setBustaRichiesta(null); // non lo si conosce l'aggiorno appena letto le informazioni dal registro
		inRequestPDMessage.setMessage(requestMessage);
		inRequestPDMessage.setUrlProtocolContext(this.msgContext.getUrlProtocolContext());
		inRequestPDMessage.setPortaDelegata(portaDelegata);
		inRequestPDMessage.setSoggettoPropeprietarioPortaDelegata(soggettoFruitore);
		for (int i = 0; i < tipiIntegrazionePD.length; i++) {
			try {
//				if (RicezioneContenutiApplicativi.gestoriIntegrazionePD.containsKey(tipiIntegrazionePD[i]) == false)
//					RicezioneContenutiApplicativi.aggiornaListaGestoreIntegrazione(
//									tipiIntegrazionePD[i], className,
//									propertiesReader, logCore);
//				IGestoreIntegrazionePD gestore = RicezioneContenutiApplicativi.gestoriIntegrazionePD.get(tipiIntegrazionePD[i]);
				
				String classType = null;
				IGestoreIntegrazionePD gestore = null;
				try {
					classType = className.getIntegrazionePortaDelegata(tipiIntegrazionePD[i]);
					gestore = (IGestoreIntegrazionePD) loader.newInstance(classType);
					AbstractCore.init(gestore, pddContext, protocolFactory);
				} catch (Exception e) {
					throw new Exception(
							"Riscontrato errore durante il caricamento della classe ["+ classType
									+ "] da utilizzare per la gestione dell'integrazione di tipo ["+ tipiIntegrazionePD[i] + "]: " + e.getMessage());
				}
				
				if (gestore != null) {
					gestore.readInRequestHeader(headerIntegrazioneRichiesta,inRequestPDMessage);
				}  else {
					msgDiag.logErroreGenerico("Lettura Gestore header di integrazione ["
									+ tipiIntegrazionePD[i]+ "]  non riuscita: non inizializzato", 
									"gestoriIntegrazionePD.get("+tipiIntegrazionePD[i]+")");
				}
			} catch (Exception e) {
				logCore.debug("Errore durante la lettura dell'header di integrazione ["+ tipiIntegrazionePD[i]
								+ "]: "+ e.getMessage(),e);
				msgDiag.addKeyword(CostantiPdD.KEY_TIPO_HEADER_INTEGRAZIONE,tipiIntegrazionePD[i]);
				msgDiag.addKeywordErroreProcessamento(e);
				msgDiag.logPersonalizzato("headerIntegrazione.letturaFallita");
			}
		}


		
		

		
		
		
		Utilities.printFreeMemory("RicezioneContenutiApplicativi - Raccolta dati Gestione CORS ...");
		// NOTA: i dati CORS sono memorizzati solamente nella porta principale e non in quelle di eventuali azioni delegate.
		//       deve quindi essere recuperata prima di sostituire la pd con una più specifica
		CorsConfigurazione cors = null;
		HttpServletRequest httpServletRequest = null;
		boolean effettuareGestioneCORS = false;
		try {
			if(requestInfo!=null && requestInfo.getProtocolContext()!=null) {
				httpServletRequest = requestInfo.getProtocolContext().getHttpServletRequest();	
			}
			
			if(httpServletRequest!=null && HttpRequestMethod.OPTIONS.name().equalsIgnoreCase(httpServletRequest.getMethod())) {
				if(portaDelegata!=null) {
					cors = configurazionePdDReader.getConfigurazioneCORS(portaDelegata);
				}
				else {
					cors = configurazionePdDReader.getConfigurazioneCORS();
				}
			}
			else {
				cors = new CorsConfigurazione();
				cors.setStato(StatoFunzionalita.DISABILITATO);
			}
		} catch (Exception e) {
			msgDiag.logErroreGenerico(e, "configurazionePdDReader.getConfigurazioneCORS(pd)");
			openspcoopstate.releaseResource();
			if (this.msgContext.isGestioneRisposta()) {
				this.msgContext.setMessageResponse((this.generatoreErrore.build(pddContext,IntegrationFunctionError.INTERNAL_REQUEST_ERROR, 
						ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
						get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_536_CONFIGURAZIONE_NON_DISPONIBILE), e,null)));
			}
			return;
		}
		
		
		
		
		
		
		
		
		
		
		
		
		
		Utilities.printFreeMemory("RicezioneContenutiApplicativi - Lettura servizio associato alla PD invocata...");
		
		
		

		/*
		 * ------------- Lettura azione associato alla PD invocata ed eventuale aggiornamento della pd utilizzata ------------
		 */
		msgDiag.mediumDebug("Lettura azione associato alla PD invocata...");
		String idModuloInAttesa = null;
		if (this.msgContext.isGestioneRisposta())
			idModuloInAttesa = this.msgContext.getIdModulo();
		RichiestaDelegata richiestaDelegata = new RichiestaDelegata(
				identificativoPortaDelegata, null,
				idModuloInAttesa, proprietaErroreAppl, identitaPdD);
		IDServizio idServizio = null;
		try {
			IDSoggetto soggettoErogatore = new IDSoggetto(portaDelegata.getSoggettoErogatore().getTipo(),portaDelegata.getSoggettoErogatore().getNome());
			idServizio = IDServizioFactory.getInstance().getIDServizioFromValues(portaDelegata.getServizio().getTipo(),portaDelegata.getServizio().getNome(), 
					soggettoErogatore, portaDelegata.getServizio().getVersione());
			if(requestInfo.getIdServizio()!=null && requestInfo.getIdServizio().getAzione()!=null){
				// gia identificata
				idServizio.setAzione(requestInfo.getIdServizio().getAzione());
				// aggiorno anche codice porta erogatore gia' identificato
				if(requestInfo.getIdServizio().getSoggettoErogatore()!=null) {
					idServizio.getSoggettoErogatore().setCodicePorta(requestInfo.getIdServizio().getSoggettoErogatore().getCodicePorta());
				}
			}
			else{
				idServizio.setAzione(configurazionePdDReader.getAzione(portaDelegata, urlProtocolContext, requestMessage, 
						headerIntegrazioneRichiesta, this.msgContext.getIdModulo().endsWith(IntegrationManager.ID_MODULO), protocolFactory));
			}

		} catch (IdentificazioneDinamicaException e) {
			
			boolean throwFault = true;
			if(StatoFunzionalita.ABILITATO.equals(cors.getStato()) && this.msgContext.isGestioneRisposta()) {
				throwFault = false;
			}
			if(throwFault) {
			
				pddContext.addObject(org.openspcoop2.core.constants.Costanti.OPERAZIONE_NON_INDIVIDUATA, "true");
				
				msgDiag.addKeywordErroreProcessamento(e);
				msgDiag.logPersonalizzato("identificazioneDinamicaAzioneNonRiuscita");
				openspcoopstate.releaseResource();
				if (this.msgContext.isGestioneRisposta()) {
					this.msgContext.setMessageResponse((this.generatoreErrore.build(pddContext,IntegrationFunctionError.OPERATION_UNDEFINED,
							ErroriIntegrazione.ERRORE_403_AZIONE_NON_IDENTIFICATA.getErroreIntegrazione(),e,null)));
				}
				return;
			}
			else {
				effettuareGestioneCORS = true;
			}
				
		} catch (Exception e) {
			msgDiag.addKeywordErroreProcessamento(e);
			msgDiag.logPersonalizzato("identificazioneDinamicaAzioneNonRiuscita");
			logCore.error(msgDiag.getMessaggio_replaceKeywords("identificazioneDinamicaAzioneNonRiuscita"),e);
			openspcoopstate.releaseResource();
			if (this.msgContext.isGestioneRisposta()) {
				this.msgContext.setMessageResponse((this.generatoreErrore.build(pddContext,IntegrationFunctionError.INTERNAL_REQUEST_ERROR,
						ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
						get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_536_CONFIGURAZIONE_NON_DISPONIBILE), e,null)));
			}
			return;
		}
		try {
			if(idServizio.getSoggettoErogatore().getCodicePorta()==null) {
				idServizio.getSoggettoErogatore().setCodicePorta(registroServiziReader.getDominio(idServizio.getSoggettoErogatore(), null, protocolFactory));
			}
						
			// aggiorno idServizio
			richiestaDelegata.setIdServizio(idServizio);
			if(identificativoPortaDelegata.getIdentificativiFruizione()!=null) {
				identificativoPortaDelegata.getIdentificativiFruizione().setIdServizio(idServizio);
			}
		
			// aggiorno informazioni dell'header di integrazione della risposta
			headerIntegrazioneRisposta = new HeaderIntegrazione(idTransazione);
			headerIntegrazioneRisposta.getBusta().setTipoMittente(soggettoFruitore.getTipo());
			headerIntegrazioneRisposta.getBusta().setMittente(soggettoFruitore.getNome());
			headerIntegrazioneRisposta.getBusta().setTipoDestinatario(richiestaDelegata.getIdServizio().getSoggettoErogatore().getTipo());
			headerIntegrazioneRisposta.getBusta().setDestinatario(richiestaDelegata.getIdServizio().getSoggettoErogatore().getNome());
			headerIntegrazioneRisposta.getBusta().setTipoServizio(richiestaDelegata.getIdServizio().getTipo());
			headerIntegrazioneRisposta.getBusta().setServizio(richiestaDelegata.getIdServizio().getNome());
			headerIntegrazioneRisposta.getBusta().setVersioneServizio(richiestaDelegata.getIdServizio().getVersione());
			headerIntegrazioneRisposta.getBusta().setAzione(richiestaDelegata.getIdServizio().getAzione());
			if (headerIntegrazioneRichiesta.getBusta() != null
					&& headerIntegrazioneRichiesta.getBusta().getRiferimentoMessaggio() != null) {
				// per profilo asincrono asimmetrico e simmetrico
				headerIntegrazioneRisposta.getBusta().setRiferimentoMessaggio(headerIntegrazioneRichiesta.getBusta().getRiferimentoMessaggio());
			}
		} catch (Exception e) {
			msgDiag.logErroreGenerico(e, "configurazionePdDReader.letturaDatiServizioServizioNonRiuscita(pd)");
			openspcoopstate.releaseResource();
			if (this.msgContext.isGestioneRisposta()) {
				this.msgContext.setMessageResponse((this.generatoreErrore.build(pddContext,IntegrationFunctionError.INTERNAL_REQUEST_ERROR,
						ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
						get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_536_CONFIGURAZIONE_NON_DISPONIBILE), e,null)));
			}
			return;
		}
		msgDiag.setServizio(richiestaDelegata.getIdServizio());
		msgDiag.addKeywords(richiestaDelegata.getIdServizio());
		
		
		
		
		
		// Gestione CORS
		
		if(!effettuareGestioneCORS && StatoFunzionalita.ABILITATO.equals(cors.getStato()) &&
				richiestaDelegata!=null && richiestaDelegata.getIdServizio()!=null &&
				richiestaDelegata.getIdServizio().getAzione()==null) {
			if(ServiceBinding.REST.equals(requestInfo.getIntegrationServiceBinding())) {
				// in rest una risorsa deve essere riconsociuta
				if(StatoFunzionalita.ABILITATO.equals(cors.getStato()) && this.msgContext.isGestioneRisposta()) {
					effettuareGestioneCORS = true;
				}
			}
			else {
				boolean azioneErrata = false;
				Servizio infoServizio = null;
				try {
					infoServizio = registroServiziReader.getInfoServizio(soggettoFruitore, richiestaDelegata.getIdServizio(),null,false, true);
				} catch (DriverRegistroServiziAzioneNotFound e) {
					azioneErrata = true;
				} catch (Throwable e) {
					// ignore
				}
				if (infoServizio == null) {
					try {
						infoServizio = registroServiziReader.getInfoServizioCorrelato(soggettoFruitore,richiestaDelegata.getIdServizio(),null, true);
					} catch (DriverRegistroServiziAzioneNotFound e) {
						azioneErrata = true;
					} catch (Throwable e) {
						// ignore
					}
				}
				if(azioneErrata) {
					effettuareGestioneCORS = true;
				}
			}
		}
		
		boolean corsTrasparente = false;
				
		if(!effettuareGestioneCORS) {
			if(pddContext.containsKey(CostantiPdD.CORS_PREFLIGHT_REQUEST_SOAP)) {
				effettuareGestioneCORS = true;
			}
			else {
				// devo verificare se si tratta di una azione matched poichè è stato inserito un tipo http method 'qualsiasi'
				if(propertiesReader.isGestioneCORS_resourceHttpMethodQualsiasi_ricezioneContenutiApplicativi()) {
					if(cors!=null && 
							StatoFunzionalita.ABILITATO.equals(cors.getStato()) &&
							TipoGestioneCORS.GATEWAY.equals(cors.getTipo()) &&
							this.msgContext.isGestioneRisposta()) {
						if(idServizio!=null && idServizio.getAzione()!=null) {
							try {
								RegistroServiziManager registroServiziManager = RegistroServiziManager.getInstance();
								AccordoServizioParteSpecifica asps = registroServiziManager.getAccordoServizioParteSpecifica(idServizio, null, false);
								if(asps!=null) {
									AccordoServizioParteComune aspc = registroServiziManager.getAccordoServizioParteComune(IDAccordoFactory.getInstance().getIDAccordoFromUri(asps.getAccordoServizioParteComune()), null, false);
									if(aspc!=null && org.openspcoop2.core.registry.constants.ServiceBinding.REST.equals(aspc.getServiceBinding())) {
										if(aspc.sizeResourceList()>0) {
											for (Resource resource : aspc.getResourceList()) {
												if(idServizio.getAzione().equals(resource.getNome())) {
													if(resource.getMethod()==null){
														effettuareGestioneCORS = true;
													}
													break;
												}
											}
										}
									}
								}
							}catch(Throwable tIgnore) {}
						}
					}
				}
			}
		}
		
		if(effettuareGestioneCORS) {
			
			if(TipoGestioneCORS.GATEWAY.equals(cors.getTipo())) {
				
				CORSFilter corsFilter = new CORSFilter(logCore, cors);
				try {
					CORSWrappedHttpServletResponse res = new CORSWrappedHttpServletResponse(false);
					corsFilter.doCORS(httpServletRequest, res, CORSRequestType.PRE_FLIGHT, true);
					if(this.msgContext.getHeaderIntegrazioneRisposta()==null) {
						this.msgContext.setHeaderIntegrazioneRisposta(new HashMap<String, String>());
					}
					this.msgContext.getHeaderIntegrazioneRisposta().putAll(res.getHeader());
					this.msgContext.setMessageResponse(res.buildMessage());
					pddContext.addObject(org.openspcoop2.core.constants.Costanti.CORS_PREFLIGHT_REQUEST_VIA_GATEWAY, "true");
				}catch(Exception e) {
					// un eccezione non dovrebbe succedere
					msgDiag.logErroreGenerico(e, "gestioneCORS(pd)");
					openspcoopstate.releaseResource();
					if (this.msgContext.isGestioneRisposta()) {
						this.msgContext.setMessageResponse((this.generatoreErrore.build(pddContext,IntegrationFunctionError.INTERNAL_REQUEST_ERROR, 
								ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
								get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_536_CONFIGURAZIONE_NON_DISPONIBILE), e,null)));
					}
					return;
				}
				
				openspcoopstate.releaseResource();
				return;
					
			}
			else {
				
				pddContext.addObject(org.openspcoop2.core.constants.Costanti.CORS_PREFLIGHT_REQUEST_TRASPARENTE, "true");
				corsTrasparente = true;
				
			}
			
		}
		
		
		
		
		

		
		
		Utilities.printFreeMemory("RicezioneContenutiApplicativi - Autorizzazione canale ...");
		ConfigurazioneCanaliNodo configurazioneCanaliNodo = null;
		try {	
			configurazioneCanaliNodo = configurazionePdDReader.getConfigurazioneCanaliNodo(); 
		} catch (Exception e) {
			msgDiag.logErroreGenerico(e, "configurazionePdDReader.getConfigurazioneCanaliNodo()");
			openspcoopstate.releaseResource();
			if (this.msgContext.isGestioneRisposta()) {
				this.msgContext.setMessageResponse((this.generatoreErrore.build(pddContext,IntegrationFunctionError.INTERNAL_REQUEST_ERROR, 
						ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
						get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_536_CONFIGURAZIONE_NON_DISPONIBILE), e,null)));
			}
			return;
		}
		boolean canaleNonAutorizzato = false;
		try {
			if(configurazioneCanaliNodo!=null && configurazioneCanaliNodo.isEnabled()) {
			
				msgDiag.addKeyword(CostantiPdD.KEY_DETAILS, "");
				msgDiag.logPersonalizzato("autorizzazioneCanale.inCorso");
				
				String canaleApiInvocata = null;
				if(portaDelegata!=null) {
					String canalePorta = portaDelegata.getCanale();
					if(canalePorta!=null && !"".equals(canalePorta)) {
						canaleApiInvocata = canalePorta;
					}
					else {
						try {
							AccordoServizioParteSpecifica asps = registroServiziReader.getAccordoServizioParteSpecifica(idServizio, null, false);
							if(asps!=null) {
								AccordoServizioParteComune aspc = registroServiziReader.getAccordoServizioParteComune(IDAccordoFactory.getInstance().getIDAccordoFromUri(asps.getAccordoServizioParteComune()), null, false);
								if(aspc!=null) {
									String canaleApi = aspc.getCanale();
									if(canaleApi!=null && !"".equals(canaleApi)) {
										canaleApiInvocata = canaleApi;
									}
								}
							}
						}catch(DriverRegistroServiziNotFound notFound) {
							// saranno segnalati altri errori dovuti al non riconoscimento del servizio
						}
					}
					
					if(canaleApiInvocata==null || "".equals(canaleApiInvocata)) {
						canaleApiInvocata = configurazioneCanaliNodo.getCanaleDefault();
					}
					
					if(!configurazioneCanaliNodo.getCanaliNodo().contains(canaleApiInvocata)) {
						canaleNonAutorizzato = true;
						String dettaglio=" (nodo '"+configurazioneCanaliNodo.getIdNodo()+"':"+configurazioneCanaliNodo.getCanaliNodo()+" api-invocata:"+canaleApiInvocata+")";
						msgDiag.addKeyword(CostantiPdD.KEY_DETAILS, dettaglio);
						pddContext.addObject(org.openspcoop2.core.constants.Costanti.ERRORE_AUTORIZZAZIONE, "true");
						throw new Exception("L'API invocata richiede un canale differente da quelli associati al nodo; invocazione non autorizzata");
					}
					else {
						msgDiag.logPersonalizzato("autorizzazioneCanale.effettuata");
					}
					
				}
//				else {
//					// saranno segnalati altri errori dovuti al non riconoscimento della porta
//				}
				
			}
		} catch (Exception e) {
			
			String msgErrore = e.getMessage();
			
			msgDiag.addKeyword(CostantiPdD.KEY_ERRORE_PROCESSAMENTO, msgErrore);
			msgDiag.logPersonalizzato("autorizzazioneCanale.fallita");
			
			ErroreIntegrazione errore = canaleNonAutorizzato ? ErroriIntegrazione.ERRORE_404_AUTORIZZAZIONE_FALLITA_SA_ANONIMO.getErrore404_AutorizzazioneFallitaServizioApplicativoAnonimo(msgErrore) : 
				ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_536_CONFIGURAZIONE_NON_DISPONIBILE);
			IntegrationFunctionError integrationFunctionError = canaleNonAutorizzato ? IntegrationFunctionError.AUTHORIZATION_DENY : IntegrationFunctionError.INTERNAL_REQUEST_ERROR;

			openspcoopstate.releaseResource();
			if (this.msgContext.isGestioneRisposta()) {
				this.msgContext.setMessageResponse((this.generatoreErrore.build(pddContext,integrationFunctionError, errore, e,null)));
			}
			return;
		}
		
		
		
		
		
		
		
		
		
		
		Utilities.printFreeMemory("RicezioneContenutiApplicativi - Identificazione PD specifica per azione del servizio ...");
		
		msgDiag.mediumDebug("Lettura azione associato alla PD invocata...");
		if(richiestaDelegata.getIdServizio().getAzione()!=null && portaDelegata!=null) {
			// verifico se esiste una porta delegata piu' specifica
			IdentificazionePortaDelegata identificazione = new IdentificazionePortaDelegata(logCore, protocolFactory, openspcoopstate.getStatoRichiesta(), portaDelegata);
			String action = richiestaDelegata.getIdServizio().getAzione();
			if(identificazione.find(action)) {
				IDPortaDelegata idPD_action = identificazione.getIDPortaDelegata(action);
				if(idPD_action!=null) {
					
					requestMessage.addContextProperty(CostantiPdD.NOME_PORTA_INVOCATA, portaDelegata.getNome()); // prima di aggiornare la porta delegata
					
					identificativoPortaDelegata = idPD_action;
					portaDelegata = identificazione.getPortaDelegata(action);
					nomeUtilizzatoPerErrore = "Configurazione specifica per l'azione '"+action+"', porta '"+ identificativoPortaDelegata.getNome()+"'";
										
					// aggiornao dati che possiede la Porta Delegata ID Porta Delegata
					this.msgContext.getIntegrazione().setIdPD(identificativoPortaDelegata);
					msgDiag.addKeyword(CostantiPdD.KEY_PORTA_DELEGATA, identificativoPortaDelegata.getNome());
					msgDiag.updatePorta(identificativoPortaDelegata.getNome());
					richiestaDelegata.setIdPortaDelegata(identificativoPortaDelegata);
					if(requestMessage.getTransportRequestContext()!=null) {
						requestMessage.getTransportRequestContext().setInterfaceName(identificativoPortaDelegata.getNome());
					}
					
					pddContext.removeObject(org.openspcoop2.core.constants.Costanti.PROPRIETA_CONFIGURAZIONE);
					try {
						Map<String, String> configProperties = configurazionePdDReader.getProprietaConfigurazione(portaDelegata);
			            if (configProperties != null && !configProperties.isEmpty()) {
			               pddContext.addObject(org.openspcoop2.core.constants.Costanti.PROPRIETA_CONFIGURAZIONE, configProperties);
			            }
					}catch(Exception e) {
						logCore.error("Errore durante la lettura delle proprietà di configurazione della porta delegata [" + portaDelegata.getNome() + "]: " + e.getMessage(), e);
					}
				}
			}else {
				pddContext.addObject(org.openspcoop2.core.constants.Costanti.API_NON_INDIVIDUATA, "true");
				msgDiag.addKeyword(CostantiPdD.KEY_ERRORE_PROCESSAMENTO, identificazione.getErroreIntegrazione().getDescrizione(protocolFactory));
				msgDiag.logPersonalizzato("portaDelegataNonEsistente");
				openspcoopstate.releaseResource();
				if (this.msgContext.isGestioneRisposta()) {
					IntegrationFunctionError integrationFunctionError = null;
					if(CodiceErroreIntegrazione.CODICE_401_PORTA_INESISTENTE.equals(identificazione.getErroreIntegrazione().getCodiceErrore())){
						integrationFunctionError = IntegrationFunctionError.API_OUT_UNKNOWN;
					}else{
						integrationFunctionError = IntegrationFunctionError.INTERNAL_REQUEST_ERROR;
					}
					this.msgContext.setMessageResponse((this.generatoreErrore.build(pddContext,integrationFunctionError, identificazione.getErroreIntegrazione(),null,null)));
				}
				return;
			}
		}
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		// ------------- Dump richiesta-----------------------------
		
		msgDiag.mediumDebug("Dump richiesta ...");
		
		DumpConfigurazione dumpConfig = null;
		try {
			dumpConfig = configurazionePdDReader.getDumpConfigurazione(portaDelegata);
			internalObjects.put(CostantiPdD.DUMP_CONFIG, dumpConfig);
		} 
		catch (Exception e) {
			msgDiag.logErroreGenerico(e, "readDumpConfigurazione");
			openspcoopstate.releaseResource();
			if (this.msgContext.isGestioneRisposta()) {
				this.msgContext.setMessageResponse((this.generatoreErrore.build(pddContext,IntegrationFunctionError.INTERNAL_REQUEST_ERROR,
						ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
						get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_536_CONFIGURAZIONE_NON_DISPONIBILE), e,null)));
			}
			return;
		}
		
		Dump dumpApplicativo = new Dump(identitaPdD,
				this.msgContext.getIdModulo(), null,
				soggettoFruitore, richiestaDelegata.getIdServizio(),
				this.msgContext.getTipoPorta(), msgDiag.getPorta(), inRequestContext.getPddContext(),
				openspcoopstate.getStatoRichiesta(),openspcoopstate.getStatoRisposta(),
				dumpConfig);
		dumpApplicativo.dumpRichiestaIngresso(requestMessage,inRequestContext.getConnettore().getUrlProtocolContext());
		internalObjects.put(CostantiPdD.DUMP_RICHIESTA_EFFETTUATO, true);
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		Utilities.printFreeMemory("RicezioneContenutiApplicativi - Autenticazione ...");
		
		

		/* --------------- Gestione credenziali --------------- */
		if(RicezioneContenutiApplicativi.tipiGestoriCredenziali!=null){
			msgDiag.mediumDebug("Gestione personalizzata delle credenziali...");
			
			for (int i = 0; i < RicezioneContenutiApplicativi.tipiGestoriCredenziali.length; i++) {
				try {
								
					IGestoreCredenziali gestore = null;
					String classType = null;
					try {
						classType = className.getGestoreCredenziali(RicezioneContenutiApplicativi.tipiGestoriCredenziali[i]);
						gestore = (IGestoreCredenziali)loader.newInstance(classType);
						AbstractCore.init(gestore, pddContext, protocolFactory);
					} catch (Exception e) {
						throw new Exception(
								"Riscontrato errore durante il caricamento della classe ["+ classType
								+ "] da utilizzare per la gestione delle credenziali di tipo ["
								+ RicezioneContenutiApplicativi.tipiGestoriCredenziali[i]+ "]: " + e.getMessage());
					}
					
					if (gestore != null) {
						Credenziali credenzialiRitornate = gestore.elaborazioneCredenziali(inRequestContext.getConnettore(), requestMessage);
						if(credenzialiRitornate==null){
							throw new Exception("Credenziali non ritornate");
						}
						if(inRequestContext.getConnettore().getCredenziali().equals(credenzialiRitornate) == false){
							String nuoveCredenziali = credenzialiRitornate.toString();
							nuoveCredenziali = nuoveCredenziali.substring(0,(nuoveCredenziali.length()-1));
							msgDiag.addKeyword(CostantiPdD.KEY_NUOVE_CREDENZIALI,nuoveCredenziali);
							String identita = gestore.getIdentitaGestoreCredenziali();
							if(identita==null){
								identita = "Gestore delle credenziali di tipo "+RicezioneContenutiApplicativi.tipiGestoriCredenziali[i];
							}
							msgDiag.addKeyword(CostantiPdD.KEY_IDENTITA_GESTORE_CREDENZIALI, identita);
							pddContext.addObject(CostantiPdD.KEY_IDENTITA_GESTORE_CREDENZIALI, identita);
							msgDiag.logPersonalizzato("gestoreCredenziali.nuoveCredenziali");
							// update credenziali
							inRequestContext.getConnettore().setCredenziali(credenzialiRitornate);
							credenziali = credenzialiRitornate;
							setCredenziali(credenziali, msgDiag);	
						}
					} else {
						throw new Exception("non inizializzato");
					}
				} 
				catch (Exception e) {
					logCore.error("Errore durante l'identificazione delle credenziali ["+ RicezioneContenutiApplicativi.tipiGestoriCredenziali[i]
					         + "]: "+ e.getMessage(),e);
					msgDiag.addKeyword(CostantiPdD.KEY_TIPO_GESTORE_CREDENZIALI,RicezioneContenutiApplicativi.tipiGestoriCredenziali[i]);
					msgDiag.addKeywordErroreProcessamento(e);
					msgDiag.logPersonalizzato("gestoreCredenziali.errore");
					ErroreIntegrazione errore = null;
					IntegrationFunctionError integrationFunctionError = null;
					String wwwAuthenticateErrorHeader = null;
					if(e instanceof GestoreCredenzialiConfigurationException){
						errore = ErroriIntegrazione.ERRORE_431_GESTORE_CREDENZIALI_ERROR.
								getErrore431_ErroreGestoreCredenziali(RicezioneContenutiApplicativi.tipiGestoriCredenziali[i], e);
						GestoreCredenzialiConfigurationException ge = (GestoreCredenzialiConfigurationException) e;
						integrationFunctionError = ge.getIntegrationFunctionError();
						pddContext.addObject(org.openspcoop2.core.constants.Costanti.ERRORE_AUTENTICAZIONE, "true");
						wwwAuthenticateErrorHeader = ge.getWwwAuthenticateErrorHeader();
					}else{
						errore = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
								get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_548_GESTORE_CREDENZIALI_NON_FUNZIONANTE);
						integrationFunctionError = IntegrationFunctionError.INTERNAL_REQUEST_ERROR;
					}
					openspcoopstate.releaseResource();
					if (this.msgContext.isGestioneRisposta()) {
						OpenSPCoop2Message errorMsg = this.generatoreErrore.build(pddContext,integrationFunctionError, errore,e,null);
						if(wwwAuthenticateErrorHeader!=null) {
							errorMsg.forceTransportHeader(HttpConstants.AUTHORIZATION_RESPONSE_WWW_AUTHENTICATE, wwwAuthenticateErrorHeader);
						}
						this.msgContext.setMessageResponse(errorMsg);
					}
					return;
				}
			}
		}
		
		
		
		
		
		
		String tipoAutenticazione = null;
		boolean autenticazioneOpzionale = false;
		String tipoGestioneToken = null;
		GestioneTokenAutenticazione gestioneTokenAutenticazione = null;
		String tipoAutorizzazione = null;
		String tipoAutorizzazioneContenuto = null;
		try {
			tipoAutenticazione = configurazionePdDReader.getAutenticazione(portaDelegata);
			autenticazioneOpzionale = configurazionePdDReader.isAutenticazioneOpzionale(portaDelegata);
			tipoGestioneToken = configurazionePdDReader.getGestioneToken(portaDelegata);
			if(portaDelegata.getGestioneToken()!=null) {
				gestioneTokenAutenticazione = portaDelegata.getGestioneToken().getAutenticazione();
			}
			tipoAutorizzazione = configurazionePdDReader.getAutorizzazione(portaDelegata);
			tipoAutorizzazioneContenuto = configurazionePdDReader.getAutorizzazioneContenuto(portaDelegata);
		} catch (Exception e) {
			msgDiag.logErroreGenerico(e, "letturaAutenticazioneTokenAutorizzazione");
			logCore.error(msgDiag.getMessaggio_replaceKeywords("letturaAutenticazioneTokenAutorizzazione"),e);
			openspcoopstate.releaseResource();
			if (this.msgContext.isGestioneRisposta()) {
				this.msgContext.setMessageResponse((this.generatoreErrore.build(pddContext,IntegrationFunctionError.INTERNAL_REQUEST_ERROR,
						ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
						get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_536_CONFIGURAZIONE_NON_DISPONIBILE), e,null)));
			}
			return;
		}
		
		
		
		
		
		
		
		
		/* ------------ GestioneToken ------------- */
		
		msgDiag.mediumDebug("GestioneToken...");
		this.msgContext.getIntegrazione().setTipoGestioneToken(tipoGestioneToken);
		if (tipoGestioneToken == null) {

			msgDiag.logPersonalizzato("gestioneTokenDisabilitata");
			
		} else {

			transaction.getTempiElaborazione().startToken();
			try {
			
				ErroreIntegrazione errore = null;
				Exception eGestioneToken = null;
				OpenSPCoop2Message errorMessageGestioneToken = null;
				String wwwAuthenticateErrorHeader = null;
				boolean fineGestione = false;
				IntegrationFunctionError integrationFunctionError = null;
				try {
					
					PolicyGestioneToken policyGestioneToken = configurazionePdDReader.getPolicyGestioneToken(portaDelegata);
					
					pddContext.addObject(org.openspcoop2.pdd.core.token.Costanti.PDD_CONTEXT_TOKEN_REALM,
							policyGestioneToken.getRealm());
					pddContext.addObject(org.openspcoop2.pdd.core.token.Costanti.PDD_CONTEXT_TOKEN_MESSAGE_ERROR_BODY_EMPTY,
							policyGestioneToken.isMessageErrorGenerateEmptyMessage());
					pddContext.addObject(org.openspcoop2.pdd.core.token.Costanti.PDD_CONTEXT_TOKEN_MESSAGE_ERROR_GENERIC_MESSAGE,
							policyGestioneToken.isMessageErrorGenerateGenericMessage());
					
					msgDiag.addKeyword(CostantiPdD.KEY_TOKEN_POLICY_GESTIONE, tipoGestioneToken);
					this.msgContext.getIntegrazione().setTokenPolicy(tipoGestioneToken);
					msgDiag.addKeyword(CostantiPdD.KEY_TOKEN_POLICY_AZIONI, policyGestioneToken.getLabelAzioniGestioneToken());
					this.msgContext.getIntegrazione().setTokenPolicy_actions(policyGestioneToken.getAzioniGestioneToken());
					msgDiag.addKeyword(CostantiPdD.KEY_TOKEN_TIPO, policyGestioneToken.getLabelTipoToken());
					msgDiag.logPersonalizzato("gestioneTokenInCorso");
					
					org.openspcoop2.pdd.core.token.pd.DatiInvocazionePortaDelegata datiInvocazione = new org.openspcoop2.pdd.core.token.pd.DatiInvocazionePortaDelegata();
					datiInvocazione.setInfoConnettoreIngresso(inRequestContext.getConnettore());
					datiInvocazione.setState(openspcoopstate.getStatoRichiesta());
					datiInvocazione.setMessage(requestMessage);
					datiInvocazione.setIdPD(identificativoPortaDelegata);
					datiInvocazione.setPd(portaDelegata);		
					datiInvocazione.setPolicyGestioneToken(policyGestioneToken);
					
					GestoreToken.validazioneConfigurazione(datiInvocazione); // assicura che la configurazione sia corretta
					
					GestioneToken gestioneTokenEngine = new GestioneToken(logCore, idTransazione, pddContext, protocolFactory);
					
					// cerco token
					
					msgDiag.addKeyword(CostantiPdD.KEY_TOKEN_POSIZIONE, policyGestioneToken.getLabelPosizioneToken());
					msgDiag.logPersonalizzato("gestioneTokenInCorso.verificaPresenzaToken");
					
					EsitoPresenzaTokenPortaDelegata esitoPresenzaToken = gestioneTokenEngine.verificaPresenzaToken(datiInvocazione);
					EsitoGestioneTokenPortaDelegata esitoValidazioneToken = null;
					EsitoGestioneTokenPortaDelegata esitoIntrospectionToken = null;
					EsitoGestioneTokenPortaDelegata esitoUserInfoToken = null;
					if(esitoPresenzaToken.isPresente()) {
						msgDiag.addKeyword(CostantiPdD.KEY_TOKEN, esitoPresenzaToken.getToken());
						msgDiag.logPersonalizzato("gestioneTokenInCorso.verificaPresenzaToken.trovato"); // stampa del token info
						
						pddContext.addObject(org.openspcoop2.pdd.core.token.Costanti.PDD_CONTEXT_TOKEN_POSIZIONE, esitoPresenzaToken);
						
						msgDiag.logPersonalizzato("gestioneTokenInCorso.verificaPresenzaToken.completataSuccesso");
	
						
						// validazione jwt
						if(!fineGestione) {
							
							if(policyGestioneToken.isValidazioneJWT()) {
							
								msgDiag.logPersonalizzato("gestioneTokenInCorso.validazioneToken");
								
								esitoValidazioneToken = gestioneTokenEngine.validazioneJWTToken(datiInvocazione, esitoPresenzaToken.getToken());
								if(esitoValidazioneToken.isValido()) {
									
									msgDiag.logPersonalizzato("gestioneTokenInCorso.validazioneToken.completataSuccesso");
									
									msgDiag.addKeyword(CostantiPdD.KEY_TOKEN_INFO, esitoValidazioneToken.getInformazioniToken().getRawResponse());
									
									pddContext.addObject(org.openspcoop2.pdd.core.token.Costanti.PDD_CONTEXT_TOKEN_ESITO_VALIDAZIONE, esitoValidazioneToken);
									
									if(esitoValidazioneToken.isInCache()) {
										msgDiag.logPersonalizzato("gestioneTokenInCorso.validazioneToken.inCache");
									}
									else {
										msgDiag.logPersonalizzato("gestioneTokenInCorso.validazioneToken.validato");
									}
								}
								else {
									
									msgDiag.addKeyword(CostantiPdD.KEY_ERRORE_PROCESSAMENTO, esitoValidazioneToken.getDetails());
									if(policyGestioneToken.isValidazioneJWT_warningOnly()) {
										msgDiag.logPersonalizzato("gestioneTokenInCorso.validazioneToken.warningOnly.fallita");
									}
									else {
										msgDiag.logPersonalizzato("gestioneTokenInCorso.validazioneToken.fallita");
										fineGestione = true;
									}
									
									String msgErrore = "processo di gestione token ["+ tipoGestioneToken + "] (validazione JWT) fallito: " + esitoValidazioneToken.getDetails();
									if(esitoValidazioneToken.getEccezioneProcessamento()!=null) {
										logCore.error(msgErrore,esitoValidazioneToken.getEccezioneProcessamento());
									}
									else {
										logCore.error(msgErrore);
									}
								
									errore = esitoValidazioneToken.getErroreIntegrazione();
									eGestioneToken = esitoValidazioneToken.getEccezioneProcessamento();
									errorMessageGestioneToken = esitoValidazioneToken.getErrorMessage();
									wwwAuthenticateErrorHeader = esitoValidazioneToken.getWwwAuthenticateErrorHeader();
									integrationFunctionError = esitoValidazioneToken.getIntegrationFunctionError();
									
								}
							}
							else {
								msgDiag.logPersonalizzato("gestioneTokenInCorso.validazioneToken.disabilitata");
							}
							
						}
						
						
						// introspection
						if(!fineGestione) {
							
							if(policyGestioneToken.isIntrospection()) {
							
								msgDiag.addKeyword(CostantiPdD.KEY_TOKEN_ENDPOINT_SERVIZIO_INTROSPECTION, policyGestioneToken.getIntrospection_endpoint());
								
								msgDiag.logPersonalizzato("gestioneTokenInCorso.introspectionToken");
								
								esitoIntrospectionToken = gestioneTokenEngine.introspectionToken(datiInvocazione, esitoPresenzaToken.getToken());
								if(esitoIntrospectionToken.isValido()) {
									
									msgDiag.logPersonalizzato("gestioneTokenInCorso.introspectionToken.completataSuccesso");
									
									msgDiag.addKeyword(CostantiPdD.KEY_TOKEN_INFO, esitoIntrospectionToken.getInformazioniToken().getRawResponse());
									
									pddContext.addObject(org.openspcoop2.pdd.core.token.Costanti.PDD_CONTEXT_TOKEN_ESITO_INTROSPECTION, esitoIntrospectionToken);
									
									if(esitoIntrospectionToken.isInCache()) {
										msgDiag.logPersonalizzato("gestioneTokenInCorso.introspectionToken.inCache");
									}
									else {
										msgDiag.logPersonalizzato("gestioneTokenInCorso.introspectionToken.validato");
									}
								}
								else {
									
									msgDiag.addKeyword(CostantiPdD.KEY_ERRORE_PROCESSAMENTO, esitoIntrospectionToken.getDetails());
									if(policyGestioneToken.isIntrospection_warningOnly()) {
										msgDiag.logPersonalizzato("gestioneTokenInCorso.introspectionToken.warningOnly.fallita");
									}
									else {
										msgDiag.logPersonalizzato("gestioneTokenInCorso.introspectionToken.fallita");
										fineGestione = true;
									}
									
									String msgErrore = "processo di gestione token ["+ tipoGestioneToken + "] (Introspection) fallito: " + esitoIntrospectionToken.getDetails();
									if(esitoIntrospectionToken.getEccezioneProcessamento()!=null) {
										logCore.error(msgErrore,esitoIntrospectionToken.getEccezioneProcessamento());
									}
									else {
										logCore.error(msgErrore);
									}
								
									errore = esitoIntrospectionToken.getErroreIntegrazione();
									eGestioneToken = esitoIntrospectionToken.getEccezioneProcessamento();
									errorMessageGestioneToken = esitoIntrospectionToken.getErrorMessage();
									wwwAuthenticateErrorHeader = esitoIntrospectionToken.getWwwAuthenticateErrorHeader();
									integrationFunctionError = esitoIntrospectionToken.getIntegrationFunctionError();
									
								}
							}
							else {
								msgDiag.logPersonalizzato("gestioneTokenInCorso.introspectionToken.disabilitata");
							}
							
						}
						
						// userInfo
						if(!fineGestione) {
							
							if(policyGestioneToken.isUserInfo()) {
							
								msgDiag.addKeyword(CostantiPdD.KEY_TOKEN_ENDPOINT_SERVIZIO_USER_INFO, policyGestioneToken.getUserInfo_endpoint());
								
								msgDiag.logPersonalizzato("gestioneTokenInCorso.userInfoToken");
								
								esitoUserInfoToken = gestioneTokenEngine.userInfoToken(datiInvocazione, esitoPresenzaToken.getToken());
								if(esitoUserInfoToken.isValido()) {
									
									msgDiag.logPersonalizzato("gestioneTokenInCorso.userInfoToken.completataSuccesso");
									
									msgDiag.addKeyword(CostantiPdD.KEY_TOKEN_INFO, esitoUserInfoToken.getInformazioniToken().getRawResponse());
									
									pddContext.addObject(org.openspcoop2.pdd.core.token.Costanti.PDD_CONTEXT_TOKEN_ESITO_USER_INFO, esitoUserInfoToken);
									
									if(esitoUserInfoToken.isInCache()) {
										msgDiag.logPersonalizzato("gestioneTokenInCorso.userInfoToken.inCache");
									}
									else {
										msgDiag.logPersonalizzato("gestioneTokenInCorso.userInfoToken.validato");
									}
								}
								else {
									
									msgDiag.addKeyword(CostantiPdD.KEY_ERRORE_PROCESSAMENTO, esitoUserInfoToken.getDetails());
									if(policyGestioneToken.isIntrospection_warningOnly()) {
										msgDiag.logPersonalizzato("gestioneTokenInCorso.userInfoToken.warningOnly.fallita");
									}
									else {
										msgDiag.logPersonalizzato("gestioneTokenInCorso.userInfoToken.fallita");
										fineGestione = true;
									}
									
									String msgErrore = "processo di gestione token ["+ tipoGestioneToken + "] (UserInfo) fallito: " + esitoUserInfoToken.getDetails();
									if(esitoUserInfoToken.getEccezioneProcessamento()!=null) {
										logCore.error(msgErrore,esitoUserInfoToken.getEccezioneProcessamento());
									}
									else {
										logCore.error(msgErrore);
									}
								
									errore = esitoUserInfoToken.getErroreIntegrazione();
									eGestioneToken = esitoUserInfoToken.getEccezioneProcessamento();
									errorMessageGestioneToken = esitoUserInfoToken.getErrorMessage();
									wwwAuthenticateErrorHeader = esitoUserInfoToken.getWwwAuthenticateErrorHeader();
									integrationFunctionError = esitoUserInfoToken.getIntegrationFunctionError();
									
								}
							}
							else {
								msgDiag.logPersonalizzato("gestioneTokenInCorso.userInfoToken.disabilitata");
							}
							
						}
						
						
					}
					else {
						
						if(policyGestioneToken.isTokenOpzionale()==false) {
						
							msgDiag.addKeyword(CostantiPdD.KEY_ERRORE_PROCESSAMENTO, esitoPresenzaToken.getDetails());
							msgDiag.logPersonalizzato("gestioneTokenInCorso.verificaPresenzaToken.fallita");
							
							fineGestione = true;
							
							String msgErrore = "processo di gestione token ["+ tipoGestioneToken + "] fallito: " + esitoPresenzaToken.getDetails();
							if(esitoPresenzaToken.getEccezioneProcessamento()!=null) {
								logCore.error(msgErrore,esitoPresenzaToken.getEccezioneProcessamento());
							}
							else {
								logCore.error(msgErrore);
							}
					
							errore = esitoPresenzaToken.getErroreIntegrazione();
							eGestioneToken = esitoPresenzaToken.getEccezioneProcessamento();
							errorMessageGestioneToken = esitoPresenzaToken.getErrorMessage();
							wwwAuthenticateErrorHeader = esitoPresenzaToken.getWwwAuthenticateErrorHeader();
							integrationFunctionError = IntegrationFunctionError.TOKEN_NOT_FOUND;
						
						}
						
					}
			
					if(fineGestione) {
						if(esitoPresenzaToken.isPresente()) {
							pddContext.addObject(org.openspcoop2.core.constants.Costanti.ERRORE_TOKEN, "true");
						}
						else {
							pddContext.addObject(org.openspcoop2.core.constants.Costanti.TOKEN_NON_PRESENTE, "true");
						}
						msgDiag.logPersonalizzato("gestioneTokenFallita");
						
						List<InformazioniToken> listaEsiti = GestoreToken.getInformazioniTokenNonValide(esitoValidazioneToken, esitoIntrospectionToken, esitoUserInfoToken);
						InformazioniToken informazioniTokenNormalizzate = null;
						if(listaEsiti!=null && listaEsiti.size()>0) {
							informazioniTokenNormalizzate = GestoreToken.normalizeInformazioniToken(listaEsiti);
							informazioniTokenNormalizzate.setValid(true);
						}
						if(informazioniTokenNormalizzate!=null) {
							pddContext.addObject(org.openspcoop2.pdd.core.token.Costanti.PDD_CONTEXT_TOKEN_INFORMAZIONI_NORMALIZZATE, informazioniTokenNormalizzate);
							
							transaction.setInformazioniToken(informazioniTokenNormalizzate);
						}
					}
					else {
						if(esitoPresenzaToken.isPresente()) {
							List<InformazioniToken> listaEsiti = GestoreToken.getInformazioniTokenValide(esitoValidazioneToken, esitoIntrospectionToken, esitoUserInfoToken);
							InformazioniToken informazioniTokenNormalizzate = null;
							if(listaEsiti!=null && listaEsiti.size()>0) {
								informazioniTokenNormalizzate = GestoreToken.normalizeInformazioniToken(listaEsiti);
								informazioniTokenNormalizzate.setValid(true);
							}
							if(informazioniTokenNormalizzate!=null) {
								pddContext.addObject(org.openspcoop2.pdd.core.token.Costanti.PDD_CONTEXT_TOKEN_INFORMAZIONI_NORMALIZZATE, informazioniTokenNormalizzate);
								
								transaction.setInformazioniToken(informazioniTokenNormalizzate);
							}
												
							msgDiag.mediumDebug("Gestione forward token ...");
							gestioneTokenEngine.forwardToken(datiInvocazione,esitoPresenzaToken,
									esitoValidazioneToken, esitoIntrospectionToken, esitoUserInfoToken,
									informazioniTokenNormalizzate);
							msgDiag.mediumDebug("Gestione forward token completata");
							
							msgDiag.logPersonalizzato("gestioneTokenCompletataConSuccesso");
						}
						else {
							msgDiag.logPersonalizzato("gestioneTokenCompletataSenzaRilevazioneToken");
						}				
					}
					
				} catch (Exception e) {
					
					msgDiag.addKeyword(CostantiPdD.KEY_ERRORE_PROCESSAMENTO, e.getMessage());
					msgDiag.logPersonalizzato("gestioneTokenFallita.erroreGenerico");
					logCore.error("processo di gestione token ["+ tipoGestioneToken + "] fallito, " + e.getMessage(),e);
					
					errore = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
							get5XX_ErroreProcessamento("processo di gestione token ["+ tipoGestioneToken + "] fallito, " + e.getMessage(),
									CodiceErroreIntegrazione.CODICE_560_GESTIONE_TOKEN);
					eGestioneToken = e;
					
					integrationFunctionError = IntegrationFunctionError.INTERNAL_REQUEST_ERROR;
					
					fineGestione = true;
					
				}
				if (fineGestione) {
					
					openspcoopstate.releaseResource();
		
					if (this.msgContext.isGestioneRisposta()) {
						if(errorMessageGestioneToken!=null) {
							this.msgContext.setMessageResponse(errorMessageGestioneToken);
						}
						else {
							if(CodiceErroreIntegrazione.CODICE_443_TOKEN_NON_PRESENTE.equals(errore.getCodiceErrore())){
								if(integrationFunctionError==null) {
									integrationFunctionError = IntegrationFunctionError.TOKEN_NOT_FOUND;
								}
							}
							else if(CodiceErroreIntegrazione.CODICE_444_TOKEN_NON_VALIDO.equals(errore.getCodiceErrore())){
								if(integrationFunctionError==null) {
									integrationFunctionError = IntegrationFunctionError.TOKEN_INVALID;
								}
							}
							else{
								if(integrationFunctionError==null) {
									integrationFunctionError = IntegrationFunctionError.INTERNAL_REQUEST_ERROR;
								}
							}
							
							OpenSPCoop2Message errorOpenSPCoopMsg = (this.generatoreErrore.build(pddContext,integrationFunctionError,errore,
									eGestioneToken,null));
							
							if(wwwAuthenticateErrorHeader!=null) {
								errorOpenSPCoopMsg.forceTransportHeader(HttpConstants.AUTHORIZATION_RESPONSE_WWW_AUTHENTICATE, wwwAuthenticateErrorHeader);
							}
							
							this.msgContext.setMessageResponse(errorOpenSPCoopMsg);
						}
					}
					
					return;
					
				}
			}finally {
				transaction.getTempiElaborazione().endToken();
			}
		}
		
		
		
		
		
		
		
		
		
		
		/* ------------ Autenticazione ------------- */
		msgDiag.mediumDebug("Autenticazione del servizio applicativo...");
		this.msgContext.getIntegrazione().setTipoAutenticazione(tipoAutenticazione);
		this.msgContext.getIntegrazione().setAutenticazioneOpzionale(autenticazioneOpzionale);
		if(tipoAutenticazione!=null){
			msgDiag.addKeyword(CostantiPdD.KEY_TIPO_AUTENTICAZIONE, tipoAutenticazione);
		}
		String servizioApplicativo = CostantiPdD.SERVIZIO_APPLICATIVO_ANONIMO;
		String credenzialeTrasporto = null;
		
		org.openspcoop2.pdd.core.autenticazione.pd.DatiInvocazionePortaDelegata datiInvocazioneAutenticazione = new org.openspcoop2.pdd.core.autenticazione.pd.DatiInvocazionePortaDelegata();
		datiInvocazioneAutenticazione.setInfoConnettoreIngresso(inRequestContext.getConnettore());
		datiInvocazioneAutenticazione.setState(openspcoopstate.getStatoRichiesta());
		datiInvocazioneAutenticazione.setIdPD(identificativoPortaDelegata);
		datiInvocazioneAutenticazione.setPd(portaDelegata);	
		
		if (CostantiConfigurazione.AUTENTICAZIONE_NONE.toString().equalsIgnoreCase(tipoAutenticazione)) {

			msgDiag.logPersonalizzato("autenticazioneDisabilitata");
			
			// dichiarazione nell'header di integrazione
			if (headerIntegrazioneRichiesta.getServizioApplicativo() != null) {
				servizioApplicativo = headerIntegrazioneRichiesta.getServizioApplicativo();
				boolean existsServizioApplicativo = false;
				try {
					IDServizioApplicativo idSA = new IDServizioApplicativo();
					idSA.setNome(servizioApplicativo);
					idSA.setIdSoggettoProprietario(soggettoFruitore);
					existsServizioApplicativo = configurazionePdDReader.existsServizioApplicativo(idSA);
				} catch (Exception e) {
					msgDiag.logErroreGenerico(e, "existsServizioApplicativo("+servizioApplicativo+")");
					openspcoopstate.releaseResource();
					if (this.msgContext.isGestioneRisposta()) {
						this.msgContext.setMessageResponse((this.generatoreErrore.build(pddContext,IntegrationFunctionError.INTERNAL_REQUEST_ERROR,
								ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
								get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_536_CONFIGURAZIONE_NON_DISPONIBILE),e,null)));
					}
					return;
				}
				if (existsServizioApplicativo == false) {
					msgDiag.addKeyword(CostantiPdD.KEY_SA_FRUITORE, servizioApplicativo);
					msgDiag.logPersonalizzato("servizioApplicativoFruitore.identificazioneTramiteInfoIntegrazioneNonRiuscita");
					openspcoopstate.releaseResource();
					if (this.msgContext.isGestioneRisposta()) {
						this.msgContext.setMessageResponse((this.generatoreErrore.build(pddContext,IntegrationFunctionError.AUTHENTICATION,
								ErroriIntegrazione.ERRORE_402_AUTENTICAZIONE_FALLITA.
								getErrore402_AutenticazioneFallita("L'identità del servizio applicativo fornita ["+servizioApplicativo+"] non esiste nella configurazione"),
								null,null)));
					}
					return;
				}
			}

		} else {

			transaction.getTempiElaborazione().startAutenticazione();
			try {
			
				msgDiag.logPersonalizzato("autenticazioneInCorso");
				
				ErroreIntegrazione errore = null;
				Exception eAutenticazione = null;
				OpenSPCoop2Message errorMessageAutenticazione = null;
				String wwwAuthenticateErrorHeader = null;
				boolean detailsSet = false;
				IntegrationFunctionError integrationFunctionError = null;
				try {						
						
					EsitoAutenticazionePortaDelegata esito = 
							GestoreAutenticazione.verificaAutenticazionePortaDelegata(tipoAutenticazione, 
									datiInvocazioneAutenticazione, new ParametriAutenticazione(portaDelegata.getProprietaAutenticazioneList()), 
									pddContext, protocolFactory, requestMessage); 
					CostantiPdD.addKeywordInCache(msgDiag, esito.isEsitoPresenteInCache(),
							pddContext, CostantiPdD.KEY_INFO_IN_CACHE_FUNZIONE_AUTENTICAZIONE);
					if(esito.getDetails()==null){
						msgDiag.addKeyword(CostantiPdD.KEY_DETAILS, "");
					}else{
						msgDiag.addKeyword(CostantiPdD.KEY_DETAILS, " ("+esito.getDetails()+")");
					}
					detailsSet = true;
					credenzialeTrasporto = esito.getCredential();
					
					if(credenzialeTrasporto!=null) {
						pddContext.addObject(org.openspcoop2.core.constants.Costanti.IDENTIFICATIVO_AUTENTICATO, credenzialeTrasporto);
					}

					String fullCredential = esito.getFullCredential();
					if(fullCredential!=null && !"".equals(fullCredential)) {
						String c = transaction.getCredenziali();
						if(c!=null && !"".equals(c)) {
							c = c + "\n" + fullCredential;
						}
						else {
							c = fullCredential;
						}
						transaction.setCredenziali(c);
					}
				
					if(esito.isClientAuthenticated() == false) {
						errore = esito.getErroreIntegrazione();
						eAutenticazione = esito.getEccezioneProcessamento();
						errorMessageAutenticazione = esito.getErrorMessage();
						wwwAuthenticateErrorHeader = esito.getWwwAuthenticateErrorHeader();
						integrationFunctionError = esito.getIntegrationFunctionError();
						msgDiag.addKeyword(CostantiPdD.KEY_CREDENZIALI_SA_FRUITORE, credenziali.toString(
								propertiesReader.isAutenticazioneBasicLogPassword() ? Credenziali.SHOW_BASIC_PASSWORD : !Credenziali.SHOW_BASIC_PASSWORD,
								!Credenziali.SHOW_ISSUER,
								!Credenziali.SHOW_DIGEST_CLIENT_CERT,
								!Credenziali.SHOW_SERIAL_NUMBER_CLIENT_CERT)); // Aggiungo la password se presente, evito inoltre di stampare l'issuer e altre info del cert nei diagnostici
					}
					else {
						if(esito.isClientIdentified()) {
							servizioApplicativo = esito.getIdServizioApplicativo().getNome();
							msgDiag.addKeyword(CostantiPdD.KEY_CREDENZIALI_SA_FRUITORE, ""); // per evitare di visualizzarle anche nei successivi diagnostici
						}
						else {
							// l'errore puo' non esserci se l'autenticazione utilizzata non prevede una identificazione obbligatoria
							errore = esito.getErroreIntegrazione();
							eAutenticazione = esito.getEccezioneProcessamento();
							errorMessageAutenticazione = esito.getErrorMessage();
							wwwAuthenticateErrorHeader = esito.getWwwAuthenticateErrorHeader();
							integrationFunctionError = esito.getIntegrationFunctionError();
							
							// evito comunque di ripresentarle nei successivi diagnostici, l'informazione l'ho gia' visualizzata nei diagnostici dell'autenticazione
							msgDiag.addKeyword(CostantiPdD.KEY_CREDENZIALI_SA_FRUITORE, ""); // per evitare di visualizzarle anche nei successivi diagnostici
						}
					}
					
					if(errore!=null) {
						if(autenticazioneOpzionale==false){
							pddContext.addObject(org.openspcoop2.core.constants.Costanti.ERRORE_AUTENTICAZIONE, "true");
						}
					}
					else {
						msgDiag.logPersonalizzato("autenticazioneEffettuata");
					}
					
				} catch (Exception e) {
					errore = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
							get5XX_ErroreProcessamento("processo di autenticazione ["
									+ tipoAutenticazione + "] fallito, " + e.getMessage(),CodiceErroreIntegrazione.CODICE_503_AUTENTICAZIONE);
					eAutenticazione = e;
					logCore.error("processo di autenticazione ["
							+ tipoAutenticazione + "] fallito, " + e.getMessage(),e);
					integrationFunctionError = IntegrationFunctionError.INTERNAL_REQUEST_ERROR;
				}
				if (errore != null) {
					if(!detailsSet) {
						msgDiag.addKeyword(CostantiPdD.KEY_DETAILS, "");
					}
					String descrizioneErrore = null;
					try{
						descrizioneErrore = errore.getDescrizione(protocolFactory);
						msgDiag.addKeyword(CostantiPdD.KEY_ERRORE_PROCESSAMENTO, descrizioneErrore);
					}catch(Exception e){
						logCore.error("getDescrizione Error:"+e.getMessage(),e);
					}
					String errorMsg =  "Riscontrato errore durante il processo di Autenticazione per il messaggio con identificativo di transazione ["+idTransazione+"]: "+descrizioneErrore;
					if(autenticazioneOpzionale){
						msgDiag.logPersonalizzato("servizioApplicativoFruitore.identificazioneTramiteCredenzialiFallita.opzionale");
						if(eAutenticazione!=null){
							logCore.debug(errorMsg,eAutenticazione);
						}
						else{
							logCore.debug(errorMsg);
						}
					}
					else{
						msgDiag.logPersonalizzato("servizioApplicativoFruitore.identificazioneTramiteCredenzialiFallita");
						if(eAutenticazione!=null){
							logCore.error(errorMsg,eAutenticazione);
						}
						else{
							logCore.error(errorMsg);
						}
					}
					if(!autenticazioneOpzionale){
						openspcoopstate.releaseResource();
		
						if (this.msgContext.isGestioneRisposta()) {
							
							if(errorMessageAutenticazione!=null) {
								this.msgContext.setMessageResponse(errorMessageAutenticazione);
							}
							else {
								if(CodiceErroreIntegrazione.CODICE_402_AUTENTICAZIONE_FALLITA.equals(errore.getCodiceErrore())){
									if(integrationFunctionError==null) {
										integrationFunctionError = IntegrationFunctionError.AUTHENTICATION;
									}
								}else{
									if(integrationFunctionError==null) {
										integrationFunctionError = IntegrationFunctionError.INTERNAL_REQUEST_ERROR;
									}
								}
								
								OpenSPCoop2Message errorOpenSPCoopMsg = (this.generatoreErrore.build(pddContext,integrationFunctionError,errore,
										eAutenticazione,null));
								
								if(wwwAuthenticateErrorHeader!=null) {
									errorOpenSPCoopMsg.forceTransportHeader(HttpConstants.AUTHORIZATION_RESPONSE_WWW_AUTHENTICATE, wwwAuthenticateErrorHeader);
								}
								
	//							if(ServiceBinding.REST.equals(requestMessage.getServiceBinding())){
	//								if(wwwAuthenticateErrorHeader!=null && wwwAuthenticateErrorHeader.startsWith(HttpConstants.AUTHORIZATION_PREFIX_BASIC) && 
	//										ServiceBinding.REST.equals(errorOpenSPCoopMsg.getServiceBinding())) {
	//									try {
	//										errorOpenSPCoopMsg.castAsRest().updateContent(null);
	//									}catch(Throwable e) {}
	//								}
	//							}
								
								this.msgContext.setMessageResponse(errorOpenSPCoopMsg);
							}
						}
						
						return;
					}
				}
			}
			finally {
				transaction.getTempiElaborazione().endAutenticazione();
			}
		}
		

		boolean autenticazioneToken = false;
		if(gestioneTokenAutenticazione!=null) {
			autenticazioneToken = GestoreAutenticazione.isAutenticazioneTokenEnabled(gestioneTokenAutenticazione);
		}
		if(autenticazioneToken) {
			
			transaction.getTempiElaborazione().startAutenticazioneToken();
			try {
			
				String checkAuthnToken = GestoreAutenticazione.getLabel(gestioneTokenAutenticazione);
				msgDiag.addKeyword(CostantiPdD.KEY_TOKEN_AUTHN_CHECK, checkAuthnToken);
				msgDiag.logPersonalizzato("autenticazioneTokenInCorso");
				this.msgContext.getIntegrazione().setTokenPolicy_authn(GestoreAutenticazione.getActions(gestioneTokenAutenticazione));
				
				ErroreIntegrazione erroreIntegrazione = null;
				Exception eAutenticazione = null;
				OpenSPCoop2Message errorMessageAutenticazione = null;
				String wwwAuthenticateErrorHeader = null;
				String errorMessage = null;
				IntegrationFunctionError integrationFunctionError = null;
				try {
					EsitoAutenticazionePortaDelegata esito = 
							GestoreAutenticazione.verificaAutenticazioneTokenPortaDelegata(gestioneTokenAutenticazione, datiInvocazioneAutenticazione, pddContext, protocolFactory, requestMessage);
	
					if(esito.isClientAuthenticated() == false) {
						erroreIntegrazione = esito.getErroreIntegrazione();
						eAutenticazione = esito.getEccezioneProcessamento();
						errorMessageAutenticazione = esito.getErrorMessage();
						wwwAuthenticateErrorHeader = esito.getWwwAuthenticateErrorHeader();					
						errorMessage = esito.getDetails();
						integrationFunctionError = esito.getIntegrationFunctionError();
					}
					
					if (erroreIntegrazione != null) {
						pddContext.addObject(org.openspcoop2.core.constants.Costanti.ERRORE_AUTENTICAZIONE_TOKEN, "true");
					}
					else {
						msgDiag.logPersonalizzato("autenticazioneTokenEffettuata");							
					}
					
				} catch (Exception e) {
					erroreIntegrazione = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
							get5XX_ErroreProcessamento("["+ RicezioneBuste.ID_MODULO+ "] processo di autenticazione token ["
									+ checkAuthnToken + "] fallito, " + e.getMessage(),CodiceErroreIntegrazione.CODICE_503_AUTENTICAZIONE);
					eAutenticazione = e;
					logCore.error("processo di autenticazione token ["
							+ checkAuthnToken + "] fallito, " + e.getMessage(),e);
					integrationFunctionError = IntegrationFunctionError.INTERNAL_REQUEST_ERROR;
				}
				if (erroreIntegrazione != null) {
					String descrizioneErrore = null;
					try{
						if(errorMessage!=null) {
							descrizioneErrore = errorMessage;
						}
						else {
							descrizioneErrore = erroreIntegrazione.getDescrizione(protocolFactory);
						}
						msgDiag.addKeyword(CostantiPdD.KEY_ERRORE_PROCESSAMENTO, descrizioneErrore);
					}catch(Exception e){
						logCore.error("getDescrizione Error:"+e.getMessage(),e);
					}
					String errorMsg =  "Riscontrato errore durante il processo di Autenticazione Token per il messaggio con identificativo di transazione ["+idTransazione+"]: "+descrizioneErrore;
					msgDiag.logPersonalizzato("autenticazioneTokenFallita");
					if(eAutenticazione!=null){
						logCore.error(errorMsg,eAutenticazione);
					}
					else{
						logCore.error(errorMsg);
					}
					
					openspcoopstate.releaseResource();
					
					if (this.msgContext.isGestioneRisposta()) {
						
						if(errorMessageAutenticazione!=null) {
							this.msgContext.setMessageResponse(errorMessageAutenticazione);
						}
						else {
							if(CodiceErroreIntegrazione.CODICE_445_TOKEN_AUTORIZZAZIONE_FALLITA.equals(erroreIntegrazione.getCodiceErrore())){
								integrationFunctionError = IntegrationFunctionError.TOKEN_REQUIRED_CLAIMS_NOT_FOUND;
							}else{
								integrationFunctionError = IntegrationFunctionError.INTERNAL_REQUEST_ERROR;
							}
							OpenSPCoop2Message errorOpenSPCoopMsg = (this.generatoreErrore.build(pddContext,integrationFunctionError,erroreIntegrazione,
									eAutenticazione,null));
							
							if(wwwAuthenticateErrorHeader!=null) {
								errorOpenSPCoopMsg.forceTransportHeader(HttpConstants.AUTHORIZATION_RESPONSE_WWW_AUTHENTICATE, wwwAuthenticateErrorHeader);
							}
							
	//						if(ServiceBinding.REST.equals(requestMessage.getServiceBinding())){
	//							if(wwwAuthenticateErrorHeader!=null && wwwAuthenticateErrorHeader.startsWith(HttpConstants.AUTHORIZATION_PREFIX_BASIC) && 
	//									ServiceBinding.REST.equals(errorOpenSPCoopMsg.getServiceBinding())) {
	//								try {
	//									errorOpenSPCoopMsg.castAsRest().updateContent(null);
	//								}catch(Throwable e) {}
	//							}
	//						}
							
							this.msgContext.setMessageResponse(errorOpenSPCoopMsg);
						}
					}
					return;
					
				}
			}finally {
				transaction.getTempiElaborazione().endAutenticazioneToken();
			}
		}
		else {
			
			msgDiag.logPersonalizzato("autenticazioneTokenDisabilitata");
			
		}
		
		
		InformazioniToken informazioniTokenNormalizzate = null;
		if(pddContext.containsKey(org.openspcoop2.pdd.core.token.Costanti.PDD_CONTEXT_TOKEN_INFORMAZIONI_NORMALIZZATE)) {
			informazioniTokenNormalizzate = (InformazioniToken) pddContext.getObject(org.openspcoop2.pdd.core.token.Costanti.PDD_CONTEXT_TOKEN_INFORMAZIONI_NORMALIZZATE);
		}
		
		if(propertiesReader.isTransazioniEnabled() && 
				(credenzialeTrasporto!=null || informazioniTokenNormalizzate!=null)) {
			CredenzialiMittente credenzialiMittente = new CredenzialiMittente();
			
			try{

				if(credenzialeTrasporto!=null) {
					GestoreAutenticazione.updateCredenzialiTrasporto(identitaPdD, ID_MODULO, idTransazione, tipoAutenticazione, credenzialeTrasporto, credenzialiMittente, openspcoopstate);
				}
				
				if(informazioniTokenNormalizzate!=null) {
					GestoreAutenticazione.updateCredenzialiToken(identitaPdD, ID_MODULO, idTransazione, informazioniTokenNormalizzate, credenzialiMittente, openspcoopstate);
				}
				
				transaction.setCredenzialiMittente(credenzialiMittente);
							
			} catch (Exception e) {
				msgDiag.addKeywordErroreProcessamento(e,"Aggiornamento Credenziali Fallito");
				msgDiag.logErroreGenerico(e,"GestoreAutenticazione.updateCredenziali");
				logCore.error("GestoreAutenticazione.updateCredenziali error: "+e.getMessage(),e);

				openspcoopstate.releaseResource();
				
				if (this.msgContext.isGestioneRisposta()) {
					this.msgContext.setMessageResponse((this.generatoreErrore.build(pddContext,IntegrationFunctionError.INTERNAL_REQUEST_ERROR,
							ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
							get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_503_AUTENTICAZIONE),e,null)));
				}
				return;
				
			}
			
		}


		
		
		
		
		
		// *** Aggiorno informazioni aplicativo ***
		
		richiestaDelegata.setServizioApplicativo(servizioApplicativo);
		// Identita' errore
		msgDiag.updatePorta(identificativoPortaDelegata.getNome());
		msgDiag.setServizioApplicativo(servizioApplicativo);
		msgDiag.addKeyword(CostantiPdD.KEY_SA_FRUITORE, servizioApplicativo);
		// identita
		if(CostantiPdD.SERVIZIO_APPLICATIVO_ANONIMO.equals(servizioApplicativo)==false){
			this.msgContext.getIntegrazione().setServizioApplicativoFruitore(servizioApplicativo);
		}
		this.generatoreErrore.updateInformazioniCooperazione(servizioApplicativo);
		
		/*
		 * ------ msgDiagnosito di avvenuta ricezione della richiesta da parte del SIL -----------
		 */
		if(CostantiPdD.SERVIZIO_APPLICATIVO_ANONIMO.equals(servizioApplicativo)){
			msgDiag.logPersonalizzato("ricevutaRichiestaApplicativa.mittenteAnonimo");
		}else{
			msgDiag.logPersonalizzato("ricevutaRichiestaApplicativa");
		}
		
		/*
		 * Get Servizio Applicativo
		 */
		msgDiag.mediumDebug("Get servizio applicativo...");
		ServizioApplicativo sa = null;
		try{
			IDServizioApplicativo idSA = new IDServizioApplicativo();
			idSA.setNome(servizioApplicativo);
			idSA.setIdSoggettoProprietario(soggettoFruitore);
			sa = configurazionePdDReader.getServizioApplicativo(idSA);
		}catch (Exception e) {
			if( !(e instanceof DriverConfigurazioneNotFound) || !(CostantiPdD.SERVIZIO_APPLICATIVO_ANONIMO.equals(servizioApplicativo)) ){
				msgDiag.logErroreGenerico(e, "getServizioApplicativo("+servizioApplicativo+")");
				openspcoopstate.releaseResource();
				if (this.msgContext.isGestioneRisposta()) {
					this.msgContext.setMessageResponse((this.generatoreErrore.build(pddContext,IntegrationFunctionError.INTERNAL_REQUEST_ERROR,
							ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
							get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_536_CONFIGURAZIONE_NON_DISPONIBILE),e,null)));
				}
				return;
			}
		}
		
		/*
		 * ----- Aggiornamento gestione errore specifica per il servizio
		 * applicativo -------
		 */
		msgDiag.mediumDebug("Aggiornamento gestione errore del servizio applicativo...");
		try {
			configurazionePdDReader.aggiornaProprietaGestioneErrorePD(proprietaErroreAppl, sa);
			if(this.msgContext.isForceFaultAsXML()){
				proprietaErroreAppl.setFaultAsXML(true); // es. se siamo in una richiesta http senza SOAP, un SoapFault non ha senso
			}
		} catch (Exception e) {
			msgDiag.logErroreGenerico(e, "aggiornaProprietaGestioneErrorePD(proprietaErroreAppl,"+servizioApplicativo+")");
			openspcoopstate.releaseResource();
			if (this.msgContext.isGestioneRisposta()) {
				this.msgContext.setMessageResponse((this.generatoreErrore.build(pddContext,IntegrationFunctionError.INTERNAL_REQUEST_ERROR,
						ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
						get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_536_CONFIGURAZIONE_NON_DISPONIBILE),e,null)));
			}
			return;
		}
		this.msgContext.setProprietaErroreAppl(proprietaErroreAppl);
		this.generatoreErrore.updateProprietaErroreApplicativo(proprietaErroreAppl);
		
		
		
		

		
		
		
		/* --------------- Verifica tipo soggetto fruitore e tipo servizio rispetto al canale utilizzato --------------- */
		msgDiag.mediumDebug("Verifica canale utilizzato...");
		List<String> tipiSoggettiSupportatiCanale = protocolFactory.createProtocolConfiguration().getTipiSoggetti();
		List<String> tipiServiziSupportatiCanale = protocolFactory.createProtocolConfiguration().getTipiServizi(requestMessage.getServiceBinding());
		// Nota: se qualche informazione e' null verranno segnalati altri errori
		if(soggettoFruitore!=null && soggettoFruitore.getTipo()!=null && 
				tipiSoggettiSupportatiCanale.contains(soggettoFruitore.getTipo())==false){
			msgDiag.logPersonalizzato("protocolli.tipoSoggetto.fruitore.unsupported");
			openspcoopstate.releaseResource();
			if (this.msgContext.isGestioneRisposta()) {
				this.msgContext.setMessageResponse((this.generatoreErrore.build(pddContext,IntegrationFunctionError.NOT_SUPPORTED_BY_PROTOCOL,
						ErroriIntegrazione.ERRORE_436_TIPO_SOGGETTO_FRUITORE_NOT_SUPPORTED_BY_PROTOCOL.
						getErrore436_TipoSoggettoFruitoreNotSupportedByProtocol(soggettoFruitore,protocolFactory),null,null)));
			}
			return;
		}
		if(richiestaDelegata!=null && richiestaDelegata.getIdServizio()!=null &&
				richiestaDelegata.getIdServizio().getSoggettoErogatore()!=null && 
				richiestaDelegata.getIdServizio().getSoggettoErogatore().getTipo()!=null &&
				tipiSoggettiSupportatiCanale.contains(richiestaDelegata.getIdServizio().getSoggettoErogatore().getTipo())==false){
			msgDiag.logPersonalizzato("protocolli.tipoSoggetto.erogatore.unsupported");
			openspcoopstate.releaseResource();
			if (this.msgContext.isGestioneRisposta()) {
				this.msgContext.setMessageResponse((this.generatoreErrore.build(pddContext,IntegrationFunctionError.NOT_SUPPORTED_BY_PROTOCOL,
						ErroriIntegrazione.ERRORE_437_TIPO_SOGGETTO_EROGATORE_NOT_SUPPORTED_BY_PROTOCOL.
						getErrore437_TipoSoggettoErogatoreNotSupportedByProtocol(richiestaDelegata.getIdServizio().getSoggettoErogatore(),protocolFactory),null,null)));
			}
			return;
		}
		if(richiestaDelegata!=null && richiestaDelegata.getIdServizio()!=null &&
				richiestaDelegata.getIdServizio().getTipo()!=null && 
				tipiServiziSupportatiCanale.contains(richiestaDelegata.getIdServizio().getTipo())==false){
			msgDiag.logPersonalizzato("protocolli.tipoServizio.unsupported");
			openspcoopstate.releaseResource();
			if (this.msgContext.isGestioneRisposta()) {
				this.msgContext.setMessageResponse((this.generatoreErrore.build(pddContext,IntegrationFunctionError.NOT_SUPPORTED_BY_PROTOCOL,
						ErroriIntegrazione.ERRORE_438_TIPO_SERVIZIO_NOT_SUPPORTED_BY_PROTOCOL.
						getErrore438_TipoServizioNotSupportedByProtocol(requestMessage.getServiceBinding(),
								richiestaDelegata.getIdServizio(),protocolFactory),null,null)));
			}
			return;
		}
		
		
		
		
		
		
		
		/*
		 * ---------------- Verifico che il servizio di RicezioneContenutiApplicativi sia abilitato ---------------------
		 */
		boolean serviceIsEnabled = false;
		boolean portaEnabled = false;
		Exception serviceIsEnabledExceptionProcessamento = null;
		try{
			serviceIsEnabled = StatoServiziPdD.isEnabledPortaDelegata(soggettoFruitore, richiestaDelegata.getIdServizio());
			if(serviceIsEnabled){
				portaEnabled = configurazionePdDReader.isPortaAbilitata(portaDelegata);
			}
		}catch(Exception e){
			serviceIsEnabledExceptionProcessamento = e;
		}
		if (!serviceIsEnabled || !portaEnabled  || serviceIsEnabledExceptionProcessamento!=null) {
			ErroreIntegrazione errore = null;
			IntegrationFunctionError integrationFunctionError = IntegrationFunctionError.API_SUSPEND;
			if(serviceIsEnabledExceptionProcessamento!=null){
				logCore.error("["+ RicezioneContenutiApplicativi.ID_MODULO+ "] Comprensione stato servizio di ricezione contenuti applicativi non riuscita: "+serviceIsEnabledExceptionProcessamento.getMessage(),serviceIsEnabledExceptionProcessamento);
				msgDiag.logErroreGenerico("Comprensione stato servizio di ricezione contenuti applicativi non riuscita", "PD");
				errore = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.getErroreIntegrazione();
				integrationFunctionError = IntegrationFunctionError.INTERNAL_REQUEST_ERROR;
			}else{
				
				pddContext.addObject(org.openspcoop2.core.constants.Costanti.ERRORE_SOSPENSIONE, "true");
								
				String msg = "Servizio di ricezione contenuti applicativi disabilitato";
				if(serviceIsEnabled){
					msg = "Porta Delegata ["+nomeUtilizzatoPerErrore+"] disabilitata";
					errore = ErroriIntegrazione.ERRORE_446_PORTA_SOSPESA.getErroreIntegrazione();
				}
				else {
					errore = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
							get5XX_ErroreProcessamento(msg,CodiceErroreIntegrazione.CODICE_550_PD_SERVICE_NOT_ACTIVE);
				}
				logCore.error("["+ RicezioneContenutiApplicativi.ID_MODULO+ "] "+msg);
				msgDiag.logErroreGenerico(msg, "PD");
			}
			openspcoopstate.releaseResource();
			if (this.msgContext.isGestioneRisposta()) {
				OpenSPCoop2Message errorOpenSPCoopMsg = this.generatoreErrore.build(pddContext,integrationFunctionError,
						errore,serviceIsEnabledExceptionProcessamento,null);
				if(IntegrationFunctionError.API_SUSPEND.equals(integrationFunctionError) &&
						propertiesReader.isEnabledServiceUnavailableRetryAfter_pd_suspend() && 
						propertiesReader.getServiceUnavailableRetryAfterSeconds_pd_suspend()!=null &&
						propertiesReader.getServiceUnavailableRetryAfterSeconds_pd_suspend()>0) {
					int seconds = propertiesReader.getServiceUnavailableRetryAfterSeconds_pd_suspend();
					if(propertiesReader.getServiceUnavailableRetryAfterSeconds_randomBackoff_pd_suspend()!=null &&
							propertiesReader.getServiceUnavailableRetryAfterSeconds_randomBackoff_pd_suspend()>0) {
						seconds = seconds + new Random().nextInt(propertiesReader.getServiceUnavailableRetryAfterSeconds_randomBackoff_pd_suspend());
					}
					errorOpenSPCoopMsg.forceTransportHeader(HttpConstants.RETRY_AFTER, seconds+"");
				}
				this.msgContext.setMessageResponse(errorOpenSPCoopMsg);
			}
			return;
		}
		
		
		
		
		
		
		
		
		
		
		Utilities.printFreeMemory("RicezioneContenutiApplicativi - Inizializzazione connessione al database...");
		
		
		
		/*
		 * ---------------- Inizializzazione Contesto di gestione della Richiesta ---------------------
		 */
		String idMessageRequest = null;
		
		// Correlazione Applicativa
		msgDiag.mediumDebug("Gestione correlazione applicativa...");
		CorrelazioneApplicativa correlazionePD = null;
		try {
			correlazionePD = configurazionePdDReader.getCorrelazioneApplicativa(portaDelegata);
		} catch (Exception e) {
			msgDiag.logErroreGenerico(e,"getCorrelazioneApplicativa(pd)");
			openspcoopstate.releaseResource();
			if (this.msgContext.isGestioneRisposta()) {
				this.msgContext.setMessageResponse((this.generatoreErrore.build(pddContext,IntegrationFunctionError.INTERNAL_REQUEST_ERROR,
						ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
						get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_536_CONFIGURAZIONE_NON_DISPONIBILE),e,null)));
			}
			return;
		}
		GestoreCorrelazioneApplicativa correlazioneApplicativa = 
			new GestoreCorrelazioneApplicativa(openspcoopstate.getStatoRichiesta(), logCore, richiestaDelegata.getIdSoggettoFruitore(),
					richiestaDelegata.getIdServizio(),servizioApplicativo,protocolFactory,
					transaction);
		boolean correlazioneEsistente = false;
		String idCorrelazioneApplicativa = null;
		if (correlazionePD != null) {
			try {
				correlazioneEsistente = correlazioneApplicativa
						.verificaCorrelazione(correlazionePD, urlProtocolContext,requestMessage,headerIntegrazioneRichiesta, 
								this.msgContext.getIdModulo().endsWith(IntegrationManager.ID_MODULO));
				idCorrelazioneApplicativa = correlazioneApplicativa.getIdCorrelazione();
				
				if (correlazioneEsistente) {
					// Aggiornamento id richiesta
					idMessageRequest = correlazioneApplicativa.getIdBustaCorrelato();

					// Aggiornamento Informazioni Protocollo
					msgDiag.setIdMessaggioRichiesta(idMessageRequest);
					
					// MsgDiagnostico
					msgDiag.addKeyword(CostantiPdD.KEY_ID_CORRELAZIONE_APPLICATIVA, idCorrelazioneApplicativa);
					msgDiag.addKeyword(CostantiPdD.KEY_ID_MESSAGGIO_RICHIESTA, idMessageRequest);
					msgDiag.logPersonalizzato("correlazioneApplicativaEsistente");
				}
			} catch (Exception e) {
				
				pddContext.addObject(org.openspcoop2.core.constants.Costanti.ERRORE_CORRELAZIONE_APPLICATIVA_RICHIESTA, "true");
				
				msgDiag.logErroreGenerico(e,"CorrelazioneApplicativa");
				logCore.error("Riscontrato errore durante il controllo di correlazione applicativa: "+ e.getMessage(),e);
				
				ErroreIntegrazione errore = null;
				if(correlazioneApplicativa!=null){
					errore = correlazioneApplicativa.getErrore();
				}
				if(errore==null){
					errore = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
							get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_529_CORRELAZIONE_APPLICATIVA_RICHIESTA_NON_RIUSCITA);
				}

				openspcoopstate.releaseResource();
				if (this.msgContext.isGestioneRisposta()) {
					IntegrationFunctionError integrationFunctionError = null;
					if(CodiceErroreIntegrazione.CODICE_416_CORRELAZIONE_APPLICATIVA_RICHIESTA_ERRORE.equals(errore.getCodiceErrore())){
						integrationFunctionError = IntegrationFunctionError.APPLICATION_CORRELATION_IDENTIFICATION_REQUEST_FAILED;
					}
					else{
						integrationFunctionError = IntegrationFunctionError.INTERNAL_REQUEST_ERROR;
					}
					this.msgContext.setMessageResponse((this.generatoreErrore.build(pddContext,integrationFunctionError,errore,e,null)));
				}
				return;
			}
		}

		if (correlazioneEsistente == false) {

			// Costruzione ID.
			msgDiag.mediumDebug("Costruzione identificativo...");
			try {
				
				Imbustamento imbustatore = new Imbustamento(logCore, protocolFactory,openspcoopstate.getStatoRichiesta());
				idMessageRequest = 
					imbustatore.buildID(identitaPdD, 
							(String) this.msgContext.getPddContext().getObject(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE), 
							propertiesReader.getGestioneSerializableDB_AttesaAttiva(),
							propertiesReader.getGestioneSerializableDB_CheckInterval(),
							RuoloMessaggio.RICHIESTA);
				if (idMessageRequest == null) {
					throw new Exception("Identificativo non costruito.");
				}
				// Aggiornamento Informazioni Protocollo
				msgDiag.setIdMessaggioRichiesta(idMessageRequest);
				
			} catch (Exception e) {
				msgDiag.logErroreGenerico(e,"imbustatore.buildID(idMessageRequest)");
				openspcoopstate.releaseResource();
				if (this.msgContext.isGestioneRisposta()) {
					this.msgContext.setMessageResponse((this.generatoreErrore.build(pddContext,IntegrationFunctionError.INTERNAL_REQUEST_ERROR,
							ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
							get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_507_COSTRUZIONE_IDENTIFICATIVO), e,null)));
				}
				return;
			}
			msgDiag.addKeyword(CostantiPdD.KEY_ID_MESSAGGIO_RICHIESTA, idMessageRequest);
			
			// Creazione Correlazione Applicativa
			if (correlazionePD!=null && idCorrelazioneApplicativa!=null && correlazioneApplicativa.isRiusoIdentificativo()) {
				msgDiag.mediumDebug("Applicazione correlazione applicativa...");
				try {
					// Applica correlazione
					correlazioneApplicativa.applicaCorrelazione(correlazionePD,idCorrelazioneApplicativa, idMessageRequest);
												
					// MsgDiagnostico
					msgDiag.addKeyword(CostantiPdD.KEY_ID_CORRELAZIONE_APPLICATIVA, idCorrelazioneApplicativa);
					msgDiag.logPersonalizzato("correlazioneApplicativaInstaurata");
				} catch (Exception e) {
					msgDiag.logErroreGenerico(e,"CreazioneCorrelazioneApplicativa");
					
					ErroreIntegrazione errore = null;
					if(correlazioneApplicativa!=null){
						errore = correlazioneApplicativa.getErrore();
					}
					if(errore==null){
						errore = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
								get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_529_CORRELAZIONE_APPLICATIVA_RICHIESTA_NON_RIUSCITA);
					}
					
					openspcoopstate.releaseResource();
					if (this.msgContext.isGestioneRisposta()) {
						IntegrationFunctionError integrationFunctionError = null;
						if(CodiceErroreIntegrazione.CODICE_416_CORRELAZIONE_APPLICATIVA_RICHIESTA_ERRORE.equals(errore.getCodiceErrore())){
							integrationFunctionError = IntegrationFunctionError.APPLICATION_CORRELATION_IDENTIFICATION_REQUEST_FAILED;
						}
						else{
							integrationFunctionError = IntegrationFunctionError.INTERNAL_REQUEST_ERROR;
						}
						this.msgContext.setMessageResponse((this.generatoreErrore.build(pddContext,integrationFunctionError,errore,null,null)));
					}
					return;
				}
			}
		}
		richiestaDelegata.setIdCorrelazioneApplicativa(idCorrelazioneApplicativa);
		msgDiag.setIdCorrelazioneApplicativa(idCorrelazioneApplicativa);
		this.msgContext.getIntegrazione().setIdCorrelazioneApplicativa(idCorrelazioneApplicativa);
		this.msgContext.setIdMessage(idMessageRequest);
		this.msgContext.getProtocol().setIdRichiesta(idMessageRequest);
		
		
		
		Utilities.printFreeMemory("RicezioneContenutiApplicativi - Gestione header integrazione della risposta...");
		
		
		/*
		 * ------- Imposta Risposta dell'Header Trasporto o se l'invocazione e' stata attiva dall'IntegrationManager -------
		 */
		msgDiag.mediumDebug("Gestione header integrazione della risposta...");
		headerIntegrazioneRisposta.getBusta().setID(idMessageRequest);
		boolean containsHeaderIntegrazioneTrasporto = false;
		for (String gestore : defaultGestoriIntegrazionePD) {
			if(CostantiConfigurazione.HEADER_INTEGRAZIONE_TRASPORTO.equals(gestore)){
				containsHeaderIntegrazioneTrasporto = true;
			}
		}
		if (containsHeaderIntegrazioneTrasporto
				|| this.msgContext.getIdModulo().startsWith(RicezioneContenutiApplicativi.ID_MODULO+ IntegrationManager.ID_MODULO)) {
			try {
				Map<String, String> propertiesIntegrazioneRisposta = new HashMap<String, String>();
//				IGestoreIntegrazionePD gestore = 
//					RicezioneContenutiApplicativi.gestoriIntegrazionePD.get(CostantiConfigurazione.HEADER_INTEGRAZIONE_TRASPORTO);
				
				String classType = null;
				IGestoreIntegrazionePD gestore = null;
				try {
					classType = className.getIntegrazionePortaDelegata(CostantiConfigurazione.HEADER_INTEGRAZIONE_TRASPORTO);
					gestore = (IGestoreIntegrazionePD) loader.newInstance(classType);
					AbstractCore.init(gestore, pddContext, protocolFactory);
				} catch (Exception e) {
					throw new Exception(
							"Riscontrato errore durante il caricamento della classe ["+ classType
									+ "] da utilizzare per la gestione dell'integrazione di tipo ["+ CostantiConfigurazione.HEADER_INTEGRAZIONE_TRASPORTO + "]: " + e.getMessage());
				}
				
				if (gestore != null) {
					OutResponsePDMessage outResponsePDMessage = new OutResponsePDMessage();
					outResponsePDMessage.setPortaDelegata(portaDelegata);
					outResponsePDMessage.setProprietaTrasporto(propertiesIntegrazioneRisposta);
					outResponsePDMessage.setServizio(richiestaDelegata.getIdServizio());
					outResponsePDMessage.setSoggettoMittente(soggettoFruitore);
					gestore.setOutResponseHeader(headerIntegrazioneRisposta, outResponsePDMessage);
					this.msgContext.setHeaderIntegrazioneRisposta(propertiesIntegrazioneRisposta);
				}
			} catch (Exception e) {
				msgDiag.logErroreGenerico(e,"setHeaderIntegrazioneRisposta");
			}
		}

		
		
		
		
		

		/*
		  * --------- Informazioni protocollo ----------
		 */
		IBustaBuilder<?> bustaBuilder = protocolFactory.createBustaBuilder(openspcoopstate.getStatoRichiesta());
		idServizio = richiestaDelegata.getIdServizio();	
		this.msgContext.getProtocol().setFruitore(soggettoFruitore);	
		this.msgContext.getProtocol().setErogatore(idServizio.getSoggettoErogatore());		
		this.msgContext.getProtocol().setTipoServizio(idServizio.getTipo());
		this.msgContext.getProtocol().setServizio(idServizio.getNome());
		this.msgContext.getProtocol().setVersioneServizio(idServizio.getVersione());
		this.msgContext.getProtocol().setAzione(idServizio.getAzione());
		this.msgContext.getProtocol().setIdRichiesta(idMessageRequest);
		
		
		
		
		/*
		  * --------- Dati di identificazione ----------
		 */
		
		IDServizioApplicativo idServizioApplicativo = new IDServizioApplicativo();
		idServizioApplicativo.setNome(servizioApplicativo);
		idServizioApplicativo.setIdSoggettoProprietario(soggettoFruitore);
		
		
		
		

		
		/* ------------ Autorizzazione ------------- */
		
		DatiInvocazionePortaDelegata datiInvocazione = new DatiInvocazionePortaDelegata();
		datiInvocazione.setPddContext(pddContext);
		datiInvocazione.setInfoConnettoreIngresso(inRequestContext.getConnettore());
		datiInvocazione.setIdServizio(richiestaDelegata.getIdServizio());
		datiInvocazione.setState(openspcoopstate.getStatoRichiesta());
		datiInvocazione.setIdPD(identificativoPortaDelegata);
		datiInvocazione.setPd(portaDelegata);		
		datiInvocazione.setIdServizioApplicativo(idServizioApplicativo);
		datiInvocazione.setServizioApplicativo(sa);
		
		
		Utilities.printFreeMemory("RicezioneContenutiApplicativi - Autorizzazione del servizio applicativo...");
		

		msgDiag.mediumDebug("Autorizzazione del servizio applicativo...");
		this.msgContext.getIntegrazione().setTipoAutorizzazione(tipoAutorizzazione);
		if(tipoAutorizzazione!=null){
			msgDiag.addKeyword(CostantiPdD.KEY_TIPO_AUTORIZZAZIONE, tipoAutorizzazione);
		}
		if (CostantiConfigurazione.AUTORIZZAZIONE_NONE.equalsIgnoreCase(tipoAutorizzazione) == false) {
			
			transaction.getTempiElaborazione().startAutorizzazione();
			try {
			
				msgDiag.logPersonalizzato("autorizzazioneInCorso");
				
				ErroreIntegrazione errore = null;
				Exception eAutorizzazione = null;
				OpenSPCoop2Message errorMessageAutorizzazione = null;
				String wwwAuthenticateErrorHeader = null;
				boolean detailsSet = false;
				IntegrationFunctionError integrationFunctionError = null;
				try {						
					EsitoAutorizzazionePortaDelegata esito = 
							GestoreAutorizzazione.verificaAutorizzazionePortaDelegata(tipoAutorizzazione, datiInvocazione, pddContext, protocolFactory, requestMessage, logCore); 
					CostantiPdD.addKeywordInCache(msgDiag, esito.isEsitoPresenteInCache(),
							pddContext, CostantiPdD.KEY_INFO_IN_CACHE_FUNZIONE_AUTORIZZAZIONE);
					if(esito.getDetails()==null){
						msgDiag.addKeyword(CostantiPdD.KEY_DETAILS, "");
					}else{
						msgDiag.addKeyword(CostantiPdD.KEY_DETAILS, " ("+esito.getDetails()+")");
					}
					detailsSet = true;
					if (esito.isAutorizzato() == false) {
						errore = esito.getErroreIntegrazione();
						eAutorizzazione = esito.getEccezioneProcessamento();
						errorMessageAutorizzazione = esito.getErrorMessage();
						wwwAuthenticateErrorHeader = esito.getWwwAuthenticateErrorHeader();
						integrationFunctionError = esito.getIntegrationFunctionError();
						pddContext.addObject(org.openspcoop2.core.constants.Costanti.ERRORE_AUTORIZZAZIONE, "true");
					}
					else{
						msgDiag.logPersonalizzato("autorizzazioneEffettuata");
					}
					
				} catch (Exception e) {
					errore = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
							get5XX_ErroreProcessamento("processo di autorizzazione ["
									+ tipoAutorizzazione + "] fallito, " + e.getMessage(),CodiceErroreIntegrazione.CODICE_504_AUTORIZZAZIONE);
					eAutorizzazione = e;
					logCore.error("processo di autorizzazione ["
							+ tipoAutorizzazione + "] fallito, " + e.getMessage(),e);
				}
				if (errore != null) {
					if(!detailsSet) {
						msgDiag.addKeyword(CostantiPdD.KEY_DETAILS, "");
					}
					String descrizioneErrore = null;
					try{
						descrizioneErrore = errore.getDescrizione(protocolFactory);
						msgDiag.addKeyword(CostantiPdD.KEY_ERRORE_PROCESSAMENTO, descrizioneErrore);
					}catch(Exception e){
						logCore.error("getDescrizione Error:"+e.getMessage(),e);
					}
					msgDiag.logPersonalizzato("servizioApplicativoFruitore.nonAutorizzato");
					String errorMsg =  "Riscontrato errore durante il processo di Autorizzazione per il messaggio con identificativo ["+idMessageRequest+"]: "+descrizioneErrore;
					if(eAutorizzazione!=null){
						logCore.error(errorMsg,eAutorizzazione);
					}
					else{
						logCore.error(errorMsg);
					}
					openspcoopstate.releaseResource();
	
					if (this.msgContext.isGestioneRisposta()) {
						
						if(errorMessageAutorizzazione!=null) {
							this.msgContext.setMessageResponse(errorMessageAutorizzazione);
						}
						else {
						
							if(CodiceErroreIntegrazione.CODICE_404_AUTORIZZAZIONE_FALLITA.equals(errore.getCodiceErrore()) ||
									CodiceErroreIntegrazione.CODICE_445_TOKEN_AUTORIZZAZIONE_FALLITA.equals(errore.getCodiceErrore())){
								if(integrationFunctionError==null) {
									integrationFunctionError = IntegrationFunctionError.AUTHORIZATION;
								}
							}else{
								if(integrationFunctionError==null) {
									integrationFunctionError = IntegrationFunctionError.INTERNAL_REQUEST_ERROR;
								}
							}
							
							OpenSPCoop2Message errorOpenSPCoopMsg = (this.generatoreErrore.build(pddContext,integrationFunctionError,errore,
									eAutorizzazione,null));
	
							if(wwwAuthenticateErrorHeader!=null) {
								errorOpenSPCoopMsg.forceTransportHeader(HttpConstants.AUTHORIZATION_RESPONSE_WWW_AUTHENTICATE, wwwAuthenticateErrorHeader);
							}
							
							this.msgContext.setMessageResponse(errorOpenSPCoopMsg);
							
						}
					}
					return;
				}
			}finally {
				transaction.getTempiElaborazione().endAutorizzazione();
			}
		}
		else{
			msgDiag.logPersonalizzato("autorizzazioneDisabilitata");
		}

		
		
		
		
		
		
		
		
		/* -------------- Identificazione servizio ------------------ */
		String infoSearch = null;
		try{
			infoSearch = IDServizioFactory.getInstance().getUriFromIDServizio(idServizio);
		}catch(Exception e){
			infoSearch = idServizio.toString(false);
		}
		if (idServizio.getAzione() != null)
			infoSearch = infoSearch + " azione " + idServizio.getAzione();
		
		// Cerco nome del registro su cui cercare
		msgDiag.addKeyword(CostantiPdD.KEY_INFO_SERVIZIO_BUSTA,infoSearch );
		msgDiag.mediumDebug("Ricerca nome registro ["+infoSearch+"]...");
		String nomeRegistroForSearch = null;
		try {
			nomeRegistroForSearch = configurazionePdDReader.getRegistroForImbustamento(soggettoFruitore, idServizio, false);
		} catch (Exception e) {
			msgDiag.addKeywordErroreProcessamento(e,"connettore associato al servizio non trovato");
			msgDiag.logPersonalizzato(MsgDiagnosticiProperties.MSG_DIAG_IMBUSTAMENTO,"registroServizi.ricercaServizioFallita");
			openspcoopstate.releaseResource();
			if (this.msgContext.isGestioneRisposta()) {
				this.msgContext.setMessageResponse((this.generatoreErrore.build(pddContext,IntegrationFunctionError.INTERNAL_REQUEST_ERROR,
						ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
						get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_514_ROUTING_CONFIGURATION_ERROR), e,null)));
			}
			return;
		}

		
		// Gestisco riferimento asincrono
		String riferimentoServizioCorrelato_ricercaSolamenteServizioCorrelato = null;
		boolean supportoProfiliAsincroni = protocolConfig.isSupportato(requestInfo.getProtocolServiceBinding(),ProfiloDiCollaborazione.ASINCRONO_ASIMMETRICO)
				|| protocolConfig.isSupportato(requestInfo.getProtocolServiceBinding(),ProfiloDiCollaborazione.ASINCRONO_SIMMETRICO);
		if(supportoProfiliAsincroni) {
			if (headerIntegrazioneRichiesta.getBusta() != null && headerIntegrazioneRichiesta.getBusta().getRiferimentoMessaggio() != null)
				riferimentoServizioCorrelato_ricercaSolamenteServizioCorrelato = headerIntegrazioneRichiesta.getBusta().getRiferimentoMessaggio();		
			if(riferimentoServizioCorrelato_ricercaSolamenteServizioCorrelato==null){
				// FIX compatibilita integrazione asincroni con versioni precedente a 1.4
				// L'integrazione era possibile anche tramite info integrazione 'Collaborazione'
				if(propertiesReader.isIntegrazioneAsincroniConIdCollaborazioneEnabled()){
					if (headerIntegrazioneRichiesta.getBusta() != null && headerIntegrazioneRichiesta.getBusta().getIdCollaborazione() != null){
						// utilizzo l'informazione come integrazione asincrona SOLO se il servizio e' correlato.
						Servizio infoServizioTmpVerificaCorrelato = null;
						try{
							infoServizioTmpVerificaCorrelato = registroServiziReader.getInfoServizioCorrelato(soggettoFruitore,idServizio, nomeRegistroForSearch, true);
						}catch(Exception e){
							logCore.debug("Verifica servizio ["+infoSearch+"] se e' correlato, fallita: "+e.getMessage());
							try{
								infoServizioTmpVerificaCorrelato = registroServiziReader.getInfoServizioAzioneCorrelata(soggettoFruitore, idServizio,nomeRegistroForSearch, true);
							}catch(Exception eCorrelato){
								logCore.debug("Verifica servizio ["+infoSearch+"] se e' correlato rispetto all'azione, fallita: "+e.getMessage());
							}
						}
						if(infoServizioTmpVerificaCorrelato!=null){
							// Il servizio e' correlato!
							riferimentoServizioCorrelato_ricercaSolamenteServizioCorrelato = headerIntegrazioneRichiesta.getBusta().getIdCollaborazione();
						}
					}	
				}
			}
		}
		if (riferimentoServizioCorrelato_ricercaSolamenteServizioCorrelato != null) {
			infoSearch = "Servizio correlato " + infoSearch;
		} else {
			infoSearch = "Servizio " + infoSearch;
		}
		infoSearch = "Ricerca nel registro dei servizi di: " + infoSearch;
		if (riferimentoServizioCorrelato_ricercaSolamenteServizioCorrelato != null)
			infoSearch = infoSearch + " (idServizioCorrelato: "+ riferimentoServizioCorrelato_ricercaSolamenteServizioCorrelato + ")";
		msgDiag.addKeyword(CostantiPdD.KEY_INFO_SERVIZIO_BUSTA,infoSearch );
		msgDiag.logPersonalizzato(MsgDiagnosticiProperties.MSG_DIAG_IMBUSTAMENTO,"registroServizi.ricercaServizioInCorso");
		
		// Effettuo ricerca
		boolean isServizioCorrelato = false;
		String invocazioneAzioneErrata = null;
		String portTypeErrato = null;
		boolean ricercaConErrore = false;
		boolean servizioCorrelatoNonTrovato = false;
		boolean servizioNonTrovato = false;
		Servizio infoServizio = null;
		Exception eServiceNotFound = null;
		try {
			// RiferimentoServizioCorrelato presente: cerco solo come servizio
			// correlato.
			if (riferimentoServizioCorrelato_ricercaSolamenteServizioCorrelato != null) {

				String erroreRicerca = null;

				// Ricerca come servizio correlato
				msgDiag.mediumDebug("Ricerca servizio correlato ...");
				try {
					infoServizio = registroServiziReader.getInfoServizioCorrelato(soggettoFruitore,idServizio, nomeRegistroForSearch, true);
					isServizioCorrelato = true;
				} catch (DriverRegistroServiziAzioneNotFound e) {
					boolean throwFault = true;
					if(corsTrasparente) {
						try {
							infoServizio = registroServiziReader.getInfoServizioCorrelato(soggettoFruitore,idServizio, nomeRegistroForSearch, false);
							isServizioCorrelato = true;
							throwFault = false;
						}catch(Throwable ignore) {}
					}
					if(throwFault) {
						invocazioneAzioneErrata = e.getMessage();
						throw e;
					}
				} catch (DriverRegistroServiziNotFound e) {
					erroreRicerca = "\nRicerca come servizio correlato-> "+ e.getMessage();
				}

				// Ricerca come servizio e azione correlata se ho un profilo
				// asincrono asimmetrico (check del profilo interno al metodo)
				if (infoServizio == null && (idServizio.getAzione() != null)) {
					msgDiag.mediumDebug("Ricerca servizio con azione correlata...");
					try {
						infoServizio = registroServiziReader.getInfoServizioAzioneCorrelata(soggettoFruitore, idServizio,nomeRegistroForSearch, true);
						isServizioCorrelato = true;
					} catch (DriverRegistroServiziAzioneNotFound e) {
						boolean throwFault = true;
						if(corsTrasparente) {
							try {
								infoServizio = registroServiziReader.getInfoServizioAzioneCorrelata(soggettoFruitore, idServizio,nomeRegistroForSearch, false);
								isServizioCorrelato = true;
								throwFault = false;
							}catch(Throwable ignore) {}
						}
						if(throwFault) {
							invocazioneAzioneErrata = e.getMessage();
							throw e;
						}
					} catch (DriverRegistroServiziNotFound e) {
						erroreRicerca = erroreRicerca+ "\nRicerca come servizio correlato -> "+ e.getMessage();
					}
				}

				// Se non trovato genero errore
				msgDiag.highDebug("Controllo dati individuati ...");
				if (infoServizio == null && erroreRicerca == null)
					throw new DriverRegistroServiziNotFound(
							"Servizio Correlato non trovato ne tramite la normale ricerca, ne tramite la ricerca per azione correlata (solo se profilo e' asincrono asimmetrico)");
				else if (infoServizio == null)
					throw new DriverRegistroServiziNotFound(erroreRicerca);

			}

			// RiferimentoServizioCorrelato non presente: cerco sia come
			// servizio, che casomai come servizio correlato.
			else {
				String erroreRicerca = null;

				// Ricerca come servizio
				msgDiag.mediumDebug("Ricerca servizio ...");
				try {
					infoServizio = registroServiziReader.getInfoServizio(soggettoFruitore, idServizio,nomeRegistroForSearch,true, true);
				} catch (DriverRegistroServiziAzioneNotFound e) {
					boolean throwFault = true;
					if(corsTrasparente) {
						try {
							infoServizio = registroServiziReader.getInfoServizio(soggettoFruitore, idServizio,nomeRegistroForSearch,true, false);
							throwFault = false;
						}catch(Throwable ignore) {}
					}
					if(throwFault) {
						invocazioneAzioneErrata = e.getMessage();
						throw e;
					}
				} catch (DriverRegistroServiziNotFound e) {
					erroreRicerca = "\nRicerca come servizio -> "+ e.getMessage();
				}

				// Ricerca come servizio correlato
				if (infoServizio == null) {
					msgDiag.mediumDebug("Ricerca servizio correlato...");
					try {
						infoServizio = registroServiziReader.getInfoServizioCorrelato(soggettoFruitore,idServizio, nomeRegistroForSearch, true);
						isServizioCorrelato = true;
					} catch (DriverRegistroServiziAzioneNotFound e) {
						boolean throwFault = true;
						if(corsTrasparente) {
							try {
								infoServizio = registroServiziReader.getInfoServizioCorrelato(soggettoFruitore,idServizio, nomeRegistroForSearch, false);
								isServizioCorrelato = true;
								throwFault = false;
							}catch(Throwable ignore) {}
						}
						if(throwFault) {
							invocazioneAzioneErrata = e.getMessage();
							throw e;
						}
					} catch (DriverRegistroServiziNotFound e) {
						erroreRicerca = erroreRicerca+ "\nRicerca come servizio correlato -> "+ e.getMessage();
					}
				}

				// Se non trovato genero errore
				if (infoServizio == null && erroreRicerca == null)
					throw new DriverRegistroServiziNotFound(
							"Servizio non trovato ne tramite la normale ricerca, ne tramite la ricerca per servizio correlato");
				else if (infoServizio == null)
					throw new DriverRegistroServiziNotFound(erroreRicerca);

			}
		} catch (DriverRegistroServiziNotFound e) {
			eServiceNotFound = e;
			msgDiag.addKeywordErroreProcessamento(e);
			msgDiag.logPersonalizzato(MsgDiagnosticiProperties.MSG_DIAG_IMBUSTAMENTO,"registroServizi.ricercaServizioFallita");
			servizioNonTrovato = true;
		} catch (DriverRegistroServiziAzioneNotFound e) {
			eServiceNotFound = e;
			msgDiag.addKeywordErroreProcessamento(e);
			msgDiag.logPersonalizzato(MsgDiagnosticiProperties.MSG_DIAG_IMBUSTAMENTO,"registroServizi.ricercaServizioFallita");
			// viene impostata la variabile invocazioneAzioneErrata
		} catch (DriverRegistroServiziPortTypeNotFound e) {
			eServiceNotFound = e;
			msgDiag.addKeywordErroreProcessamento(e,"configurazione registro dei servizi errata");
			msgDiag.logPersonalizzato(MsgDiagnosticiProperties.MSG_DIAG_IMBUSTAMENTO,"registroServizi.ricercaServizioFallita");
			portTypeErrato = "Configurazione del registro dei Servizi errata: "+ e.getMessage();
		} catch(DriverRegistroServiziCorrelatoNotFound e){
			eServiceNotFound = e;
			msgDiag.addKeywordErroreProcessamento(e,"correlazione asincrona non rilevata");
			msgDiag.logPersonalizzato(MsgDiagnosticiProperties.MSG_DIAG_IMBUSTAMENTO,"registroServizi.ricercaServizioFallita");
			servizioCorrelatoNonTrovato = true;
		} 
		catch (Exception e) {
			eServiceNotFound = e;
			msgDiag.addKeywordErroreProcessamento(e,"errore generale");
			msgDiag.logPersonalizzato(MsgDiagnosticiProperties.MSG_DIAG_IMBUSTAMENTO,"registroServizi.ricercaServizioFallita");
			logCore.error("Ricerca servizio fallita",e);
			ricercaConErrore = true;
		}

		// Segnalo eventuali errori di servizio non trovato / errato
		if (infoServizio == null) {
			if (servizioNonTrovato == false && ricercaConErrore == false && servizioCorrelatoNonTrovato==false && invocazioneAzioneErrata == null) {
				msgDiag.addKeyword(CostantiPdD.KEY_ERRORE_PROCESSAMENTO , "servizio non esistente" );
				msgDiag.logPersonalizzato(MsgDiagnosticiProperties.MSG_DIAG_IMBUSTAMENTO,"registroServizi.ricercaServizioFallita");
				servizioNonTrovato = true;
			}
			ErroreIntegrazione erroreIntegrazione = null;
			IntegrationFunctionError integrationFunctionError = null;
			if (invocazioneAzioneErrata != null) {
				
				String azione = "";
				if(idServizio.getAzione()!=null) {
					azione = "(azione:"+ idServizio.getAzione()+ ") ";
				}
				
				pddContext.addObject(org.openspcoop2.core.constants.Costanti.OPERAZIONE_NON_INDIVIDUATA, "true");
				
				erroreIntegrazione = ErroriIntegrazione.ERRORE_423_SERVIZIO_CON_AZIONE_SCORRETTA.
						getErrore423_ServizioConAzioneScorretta(azione+ invocazioneAzioneErrata);
				integrationFunctionError = IntegrationFunctionError.OPERATION_UNDEFINED;
			} else if (portTypeErrato != null) {
				erroreIntegrazione = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
						get5XX_ErroreProcessamento(portTypeErrato, CodiceErroreIntegrazione.CODICE_540_REGISTRO_SERVIZI_MAL_CONFIGURATO);
				integrationFunctionError = IntegrationFunctionError.INTERNAL_REQUEST_ERROR;
			} else if (servizioNonTrovato) {
				erroreIntegrazione = ErroriIntegrazione.ERRORE_405_SERVIZIO_NON_TROVATO.getErroreIntegrazione();
				integrationFunctionError = IntegrationFunctionError.NOT_FOUND;
			} else if (ricercaConErrore) {
				erroreIntegrazione = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
						get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_534_REGISTRO_DEI_SERVIZI_NON_DISPONIBILE);
				integrationFunctionError = IntegrationFunctionError.INTERNAL_REQUEST_ERROR;
			} else if (servizioCorrelatoNonTrovato){
				erroreIntegrazione = ErroriIntegrazione.ERRORE_408_SERVIZIO_CORRELATO_NON_TROVATO.getErroreIntegrazione();
				//integrationFunctionError = IntegrationFunctionError.CORRELATION_INFORMATION_NOT_FOUND; in questo caso si tratta di un errore nella configurazione
				integrationFunctionError = IntegrationFunctionError.INTERNAL_REQUEST_ERROR;
			} 
			else {
				erroreIntegrazione = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
						get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_500_ERRORE_INTERNO);
				integrationFunctionError = IntegrationFunctionError.INTERNAL_REQUEST_ERROR;
			}
			openspcoopstate.releaseResource();
			if (this.msgContext.isGestioneRisposta()) {
				this.msgContext.setMessageResponse((this.generatoreErrore.build(pddContext,integrationFunctionError,erroreIntegrazione,eServiceNotFound,null)));
			}
			return;
		}
		msgDiag.highDebug("Ricerca servizio terminata");
		infoServizio.setCorrelato(isServizioCorrelato);
		this.msgContext.getProtocol().setProfiloCollaborazione(infoServizio.getProfiloDiCollaborazione(),null); // il valore verra' serializzato solo successivamente nella busta
		msgDiag.addKeyword(CostantiPdD.KEY_PROFILO_COLLABORAZIONE, traduttore.toString(infoServizio.getProfiloDiCollaborazione()));
		if(infoServizio!=null && infoServizio.getIdAccordo()!=null){
			this.msgContext.getProtocol().setIdAccordo(infoServizio.getIdAccordo());
			richiestaDelegata.setIdAccordo(infoServizio.getIdAccordo());
			try{
				idServizio.setUriAccordoServizioParteComune(IDAccordoFactory.getInstance().getUriFromIDAccordo(infoServizio.getIdAccordo()));
			}catch(Exception e){}
		}
		msgDiag.highDebug("Convert infoServizio to Busta ...");
		Busta bustaRichiesta = infoServizio.convertToBusta(protocolFactory.getProtocol(), soggettoFruitore);
		msgDiag.highDebug("Convert infoServizio to Busta terminata");
		inRequestPDMessage.setBustaRichiesta(bustaRichiesta);
		
		// Aggiorno eventuale valore dipendete dal profilo (PDC)
		if(this.msgContext.getProtocol()!=null && idServizio.getVersione()!=null) {
			if(this.msgContext.getProtocol().getVersioneServizio()==null) {
				this.msgContext.getProtocol().setVersioneServizio(idServizio.getVersione());
			}
			else if(this.msgContext.getProtocol().getVersioneServizio().intValue()!=idServizio.getVersione().intValue()) {
				this.msgContext.getProtocol().setVersioneServizio(idServizio.getVersione());
			}
		}

		
	
		
		
		
		/* -------- Controlli Header di Integrazione ------------- */
		
		if(infoServizio.getIdRiferimentoRichiesta() &&
				protocolConfig.isIntegrationInfoRequired(TipoPdD.DELEGATA, requestInfo.getProtocolServiceBinding(),FunzionalitaProtocollo.RIFERIMENTO_ID_RICHIESTA)) {
			String riferimentoRichiesta = null;
			if (headerIntegrazioneRichiesta!=null &&
					headerIntegrazioneRichiesta.getBusta() != null && headerIntegrazioneRichiesta.getBusta().getRiferimentoMessaggio() != null) {
				riferimentoRichiesta = headerIntegrazioneRichiesta.getBusta().getRiferimentoMessaggio();
			}
			if(riferimentoRichiesta==null) {
				StringBuilder bf = new StringBuilder();
				for (int i = 0; i < tipiIntegrazionePD.length; i++) {
					if(i>0) {
						bf.append(",");
					}
					bf.append(tipiIntegrazionePD[i]);
				}
				msgDiag.addKeyword(CostantiPdD.KEY_TIPI_INTEGRAZIONE ,bf.toString() );
				msgDiag.logPersonalizzato(MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_CONTENUTI_APPLICATIVI,"riferimentoIdRichiesta.nonFornito");
				ErroreIntegrazione erroreIntegrazione = ErroriIntegrazione.ERRORE_442_RIFERIMENTO_ID_MESSAGGIO.getErroreIntegrazione();
				IntegrationFunctionError integrationFunctionError = IntegrationFunctionError.CORRELATION_INFORMATION_NOT_FOUND;
				
				openspcoopstate.releaseResource();
				if (this.msgContext.isGestioneRisposta()) {
					this.msgContext.setMessageResponse((this.generatoreErrore.build(pddContext,integrationFunctionError,erroreIntegrazione,null,null)));
				}
				return;
				
			}
		}
		
		
		
		
		
		/* -------- Profilo di Gestione ------------- */
		try {
			String profiloGestione = registroServiziReader.getProfiloGestioneFruizioneServizio(idServizio,nomeRegistroForSearch);
			richiestaDelegata.setProfiloGestione(profiloGestione);
			msgDiag.mediumDebug("Profilo di gestione ["+ RicezioneContenutiApplicativi.ID_MODULO+ "] della busta: " + profiloGestione);
		} catch (Exception e) {
			msgDiag.addKeywordErroreProcessamento(e,"analisi del profilo di gestione fallita");
			msgDiag.logPersonalizzato(MsgDiagnosticiProperties.MSG_DIAG_IMBUSTAMENTO,"registroServizi.ricercaServizioFallita");
			logCore.error("Comprensione Profilo Gestione fallita",e);
			openspcoopstate.releaseResource();
			if (this.msgContext.isGestioneRisposta()) {
				this.msgContext.setMessageResponse((this.generatoreErrore.build(pddContext,IntegrationFunctionError.INTERNAL_REQUEST_ERROR,
						ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
						get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_525_GESTIONE_FUNZIONALITA_PROTOCOLLO),e,null)));
			}
			return;
		}
		
		
		
		
		/* -------- OpenSPCoop2Message Update ------------- */
		try {
			msgDiag.mediumDebug("Aggiornamento del messaggio");
			requestMessage = protocolFactory.createProtocolManager().updateOpenSPCoop2MessageRequest(requestMessage, bustaRichiesta,
					protocolFactory.getCachedRegistryReader(openspcoopstate.getStatoRichiesta()));
		} catch (Exception e) {
			msgDiag.addKeywordErroreProcessamento(e,"Aggiornamento messaggio fallito");
			msgDiag.logErroreGenerico(e,"ProtocolManager.updateOpenSPCoop2Message");
			logCore.error("ProtocolManager.updateOpenSPCoop2Message error: "+e.getMessage(),e);
			openspcoopstate.releaseResource();
			if (this.msgContext.isGestioneRisposta()) {
				this.msgContext.setMessageResponse((this.generatoreErrore.build(pddContext,IntegrationFunctionError.INTERNAL_REQUEST_ERROR,
						ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
						get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_525_GESTIONE_FUNZIONALITA_PROTOCOLLO),e,null)));
			}
			return;
		}
		
		
		
		
		/* -------- Indirizzi Risposta  ------------- */
		
		String indirizzoFruitore = null;
		String indirizzoErogatore = null;
		IProtocolConfiguration protocolConfiguration = protocolFactory.createProtocolConfiguration();
		if(protocolConfiguration.isSupportoIndirizzoRisposta()){
			try {
				Connettore connettoreFruitore = null;
				try{
					connettoreFruitore = registroServiziReader.getConnettore(soggettoFruitore, nomeRegistroForSearch);
				}catch(org.openspcoop2.core.registry.driver.DriverRegistroServiziNotFound dNotFound){}
				if(connettoreFruitore!=null && !CostantiConfigurazione.DISABILITATO.equals(connettoreFruitore.getTipo())){
					if(connettoreFruitore.getProperties()!=null && connettoreFruitore.getProperties().containsKey(CostantiConnettori.CONNETTORE_LOCATION)){
						indirizzoFruitore = connettoreFruitore.getProperties().get(CostantiConnettori.CONNETTORE_LOCATION);
					}
				}
				msgDiag.mediumDebug("Indirizzo Risposta del soggetto fruitore ["+ soggettoFruitore+ "]: " + indirizzoFruitore);
				
				Connettore connettoreErogatore = null;
				try{
					connettoreErogatore = registroServiziReader.getConnettore(idServizio.getSoggettoErogatore(), nomeRegistroForSearch);
				}catch(org.openspcoop2.core.registry.driver.DriverRegistroServiziNotFound dNotFound){}
				if(connettoreErogatore!=null && !CostantiConfigurazione.DISABILITATO.equals(connettoreErogatore.getTipo())){
					if(connettoreErogatore.getProperties()!=null && connettoreErogatore.getProperties().containsKey(CostantiConnettori.CONNETTORE_LOCATION)){
						indirizzoErogatore = connettoreErogatore.getProperties().get(CostantiConnettori.CONNETTORE_LOCATION);
					}
				}
				msgDiag.mediumDebug("Indirizzo Risposta del soggetto erogatore ["+ idServizio.getSoggettoErogatore()+ "]: " + indirizzoErogatore);
				
			} catch (Exception e) {
				msgDiag.addKeywordErroreProcessamento(e,"recupero degli indirizzi di risposta per i soggetti fallita");
				msgDiag.logPersonalizzato(MsgDiagnosticiProperties.MSG_DIAG_IMBUSTAMENTO,"registroServizi.ricercaServizioFallita");
				logCore.error("Comprensione Indirizzo Risposta fallita",e);
				openspcoopstate.releaseResource();
				if (this.msgContext.isGestioneRisposta()) {
					this.msgContext.setMessageResponse((this.generatoreErrore.build(pddContext,IntegrationFunctionError.INTERNAL_REQUEST_ERROR,
							ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
							get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_525_GESTIONE_FUNZIONALITA_PROTOCOLLO),e,null)));
				}
				return;
			}
			this.msgContext.getProtocol().setIndirizzoFruitore(indirizzoFruitore);
			this.msgContext.getProtocol().setIndirizzoErogatore(indirizzoErogatore);
		}
		
		
		/* ---- Ricerca registro dei servizi terminata con successo --- */
		msgDiag.logPersonalizzato(MsgDiagnosticiProperties.MSG_DIAG_IMBUSTAMENTO,"registroServizi.ricercaServizioEffettuata");
		
		
		

		
		
		
		
		
		
		
		
		
		
		
		
		
		
		// ------------- in-protocol-handler -----------------------------
		try{
			InRequestProtocolContext inRequestProtocolContext = new InRequestProtocolContext(inRequestContext);
			if(inRequestProtocolContext.getStato()==null) {
				inRequestProtocolContext.setStato(openspcoopstate.getStatoRichiesta());
			}
			if(inRequestProtocolContext.getConnettore()!=null){
				inRequestProtocolContext.getConnettore().setCredenziali(credenziali);
			}
			inRequestProtocolContext.setProtocollo(this.msgContext.getProtocol());
			inRequestProtocolContext.setIntegrazione(this.msgContext.getIntegrazione());
			GestoreHandlers.inRequestProtocol(inRequestProtocolContext, msgDiag, logCore);
		}catch(Exception e){		
			ErroreIntegrazione erroreIntegrazione = null;
			IntegrationFunctionError integrationFunctionError = null; 
			if(e instanceof HandlerException){
				HandlerException he = (HandlerException) e;
				if(he.isEmettiDiagnostico()){
					msgDiag.logErroreGenerico(e,he.getIdentitaHandler());
				}
				logCore.error("Gestione InRequestProtocolHandler non riuscita ("+he.getIdentitaHandler()+"): "	+ he);
				if(this.msgContext.isGestioneRisposta()){
					erroreIntegrazione = he.convertToErroreIntegrazione();
					integrationFunctionError = he.getIntegrationFunctionError();
				}
			}else{
				msgDiag.logErroreGenerico(e,"InvocazioneInRequestHandler");
				logCore.error("Gestione InRequestProtocolHandler non riuscita: "	+ e);
			}
			openspcoopstate.releaseResource();
			if (this.msgContext.isGestioneRisposta()) {
				if(erroreIntegrazione==null){
					erroreIntegrazione = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
							get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_558_HANDLER_IN_PROTOCOL_REQUEST);
				}
				if(integrationFunctionError==null) {
					integrationFunctionError = IntegrationFunctionError.INTERNAL_REQUEST_ERROR;
				}
				OpenSPCoop2Message responseMessageError = this.generatoreErrore.build(pddContext,integrationFunctionError,erroreIntegrazione,e,null);
				if(e instanceof HandlerException){
					HandlerException he = (HandlerException) e;
					he.customized(responseMessageError);
				}
				this.msgContext.setMessageResponse(responseMessageError);
			}
			return;
		}
		
		
		
		
		
		
		
		
		
		
		
		
		
		/* -------------------------- Implementazione PdD Soggetti busta -------------------------------*/
		String implementazionePdDMittente = null;
		String implementazionePdDDestinatario = null;
		String idPdDMittente = null;
		String idPdDDestinatario = null;
		msgDiag.mediumDebug("Ricerca implementazione della porta di dominio dei soggetti...");
		try{
			implementazionePdDMittente = registroServiziReader.getImplementazionePdD(soggettoFruitore, null);
			implementazionePdDDestinatario = registroServiziReader.getImplementazionePdD(idServizio.getSoggettoErogatore(), null);
			idPdDMittente = registroServiziReader.getIdPortaDominio(soggettoFruitore, null);
			idPdDDestinatario = registroServiziReader.getIdPortaDominio(idServizio.getSoggettoErogatore(), null);
		}catch(Exception e){
			msgDiag.logErroreGenerico(e,"ricercaImplementazioniPdD");
			openspcoopstate.releaseResource();
			if (this.msgContext.isGestioneRisposta()) {
				this.msgContext.setMessageResponse((this.generatoreErrore.build(pddContext,IntegrationFunctionError.INTERNAL_REQUEST_ERROR,
						ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
						get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_534_REGISTRO_DEI_SERVIZI_NON_DISPONIBILE),e,null)));
			}
			return;
		}
		msgDiag.mediumDebug("ImplementazionePdD soggetto ("+soggettoFruitore.toString()+") e' ["+implementazionePdDMittente+"], soggetto ("
				+idServizio.getSoggettoErogatore().toString()+") e' ["+implementazionePdDDestinatario+"]");
		
		
		
		Utilities.printFreeMemory("RicezioneContenutiApplicativi - Controllo validazione xsd abilitata/disabilitata...");
		
		
		
		/* ------------ Validazione Contenuti Applicativi ------------- */
		msgDiag.mediumDebug("Controllo validazione xsd abilitata/disabilitata...");
		ValidazioneContenutiApplicativi validazioneContenutoApplicativoApplicativo = null;
		List<Proprieta> proprietaValidazioneContenutoApplicativoApplicativo = null;
		try {
			validazioneContenutoApplicativoApplicativo = configurazionePdDReader.getTipoValidazioneContenutoApplicativo(portaDelegata,implementazionePdDDestinatario, true);
			proprietaValidazioneContenutoApplicativoApplicativo = portaDelegata.getProprietaList();
		} catch (Exception e) {
			msgDiag.logErroreGenerico(e,"getTipoValidazioneContenutoApplicativo(pd,"+implementazionePdDDestinatario+")");
			openspcoopstate.releaseResource();
			if (this.msgContext.isGestioneRisposta()) {
				this.msgContext.setMessageResponse((this.generatoreErrore.build(pddContext,IntegrationFunctionError.INTERNAL_REQUEST_ERROR,
						ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
						get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_536_CONFIGURAZIONE_NON_DISPONIBILE),e,null)));
			}
			return;
		}
		if(validazioneContenutoApplicativoApplicativo!=null && validazioneContenutoApplicativoApplicativo.getTipo()!=null){
			String tipo = ValidatoreMessaggiApplicativi.getTipo(validazioneContenutoApplicativoApplicativo);
			this.msgContext.getIntegrazione().setTipoValidazioneContenuti(tipo);
			msgDiag.addKeyword(CostantiPdD.KEY_TIPO_VALIDAZIONE_CONTENUTI, tipo);
			msgDiag.addKeyword(CostantiPdD.KEY_DETAILS_VALIDAZIONE_CONTENUTI,"");
		}
		if (CostantiConfigurazione.STATO_CON_WARNING_ABILITATO.equals(validazioneContenutoApplicativoApplicativo.getStato())
				|| CostantiConfigurazione.STATO_CON_WARNING_WARNING_ONLY.equals(validazioneContenutoApplicativoApplicativo.getStato())) {

			transaction.getTempiElaborazione().startValidazioneRichiesta();
			ByteArrayInputStream binXSD = null;
			try {
				msgDiag.logPersonalizzato("validazioneContenutiApplicativiRichiestaInCorso");
				
				boolean readInterface = CostantiConfigurazione.VALIDAZIONE_CONTENUTI_APPLICATIVI_INTERFACE.equals(validazioneContenutoApplicativoApplicativo.getTipo());
						
				if(ServiceBinding.SOAP.equals(requestMessage.getServiceBinding())){
				
					// Accept mtom message
					List<MtomXomReference> xomReferences = null;
					if(StatoFunzionalita.ABILITATO.equals(validazioneContenutoApplicativoApplicativo.getAcceptMtomMessage())){
						msgDiag.mediumDebug("Validazione xsd della richiesta (mtomFastUnpackagingForXSDConformance)...");
						if(ServiceBinding.SOAP.equals(requestMessage.getServiceBinding())==false){
							throw new Exception("Funzionalita 'AcceptMtomMessage' valida solamente per Service Binding SOAP");
						}
						xomReferences = requestMessage.castAsSoap().mtomFastUnpackagingForXSDConformance();
					}
					
					// Init Validatore
					msgDiag.mediumDebug("Validazione xsd della richiesta (initValidator)...");
					ValidatoreMessaggiApplicativi validatoreMessaggiApplicativi = new ValidatoreMessaggiApplicativi(
							registroServiziReader, richiestaDelegata.getIdServizio(), requestMessage,readInterface,
							propertiesReader.isValidazioneContenutiApplicativi_rpcLiteral_xsiType_gestione(),
							proprietaValidazioneContenutoApplicativoApplicativo);
				
					// Validazione WSDL
					if (CostantiConfigurazione.VALIDAZIONE_CONTENUTI_APPLICATIVI_INTERFACE.equals(validazioneContenutoApplicativoApplicativo.getTipo())
						|| CostantiConfigurazione.VALIDAZIONE_CONTENUTI_APPLICATIVI_OPENSPCOOP.equals(validazioneContenutoApplicativoApplicativo.getTipo())) {
						msgDiag.mediumDebug("Validazione wsdl della richiesta ...");
						validatoreMessaggiApplicativi.validateWithWsdlLogicoImplementativo(true);
					}
					
					// Validazione XSD
					msgDiag.mediumDebug("Validazione xsd della richiesta (validazione)...");
					validatoreMessaggiApplicativi.validateWithWsdlDefinitorio(true);
	
					// Validazione WSDL (Restore Original Document)
					if (CostantiConfigurazione.VALIDAZIONE_CONTENUTI_APPLICATIVI_INTERFACE.equals(validazioneContenutoApplicativoApplicativo.getTipo())
						|| CostantiConfigurazione.VALIDAZIONE_CONTENUTI_APPLICATIVI_OPENSPCOOP.equals(validazioneContenutoApplicativoApplicativo.getTipo())) {
						if(propertiesReader.isValidazioneContenutiApplicativi_rpcLiteral_xsiType_gestione() &&
								propertiesReader.isValidazioneContenutiApplicativi_rpcLiteral_xsiType_ripulituraDopoValidazione()){
							msgDiag.mediumDebug("Ripristino elementi modificati per supportare validazione wsdl della richiesta ...");
							validatoreMessaggiApplicativi.restoreOriginalDocument(true);
						}
					}
					
					// Ripristino struttura messaggio con xom
					if(xomReferences!=null && xomReferences.size()>0){
						msgDiag.mediumDebug("Validazione xsd della richiesta (mtomRestoreAfterXSDConformance)...");
						if(ServiceBinding.SOAP.equals(requestMessage.getServiceBinding())==false){
							throw new Exception("Funzionalita 'AcceptMtomMessage' valida solamente per Service Binding SOAP");
						}
						requestMessage.castAsSoap().mtomRestoreAfterXSDConformance(xomReferences);
					}
					
				}
				else {
					
					// Init Validatore
					msgDiag.mediumDebug("Validazione della richiesta (initValidator)...");
					ValidatoreMessaggiApplicativiRest validatoreMessaggiApplicativi = 
						new ValidatoreMessaggiApplicativiRest(registroServiziReader, richiestaDelegata.getIdServizio(), requestMessage, readInterface, proprietaValidazioneContenutoApplicativoApplicativo,
								protocolFactory, pddContext);
					
					if(CostantiConfigurazione.VALIDAZIONE_CONTENUTI_APPLICATIVI_XSD.equals(validazioneContenutoApplicativoApplicativo.getTipo()) &&
							requestMessage.castAsRest().hasContent()) {
						
						// Validazione XSD
						msgDiag.mediumDebug("Validazione xsd della richiesta ...");
						validatoreMessaggiApplicativi.validateWithSchemiXSD(true);
						
					}
					else {
						
						// Validazione Interface
						validatoreMessaggiApplicativi.validateRequestWithInterface(false);
						
					}
					
				}
				
				msgDiag.logPersonalizzato("validazioneContenutiApplicativiRichiestaEffettuata");
				
			} catch (ValidatoreMessaggiApplicativiException ex) {
				msgDiag.addKeywordErroreProcessamento(ex);
				logCore.error("[ValidazioneContenutiApplicativi Richiesta] "+ex.getMessage(),ex);
				if (CostantiConfigurazione.STATO_CON_WARNING_WARNING_ONLY.equals(validazioneContenutoApplicativoApplicativo.getStato())) {
					msgDiag.logPersonalizzato("validazioneContenutiApplicativiRichiestaNonRiuscita.warningOnly");
				}
				else {
					msgDiag.logPersonalizzato("validazioneContenutiApplicativiRichiestaNonRiuscita");
				}
				if (CostantiConfigurazione.STATO_CON_WARNING_WARNING_ONLY.equals(validazioneContenutoApplicativoApplicativo.getStato()) == false) {
					
					pddContext.addObject(org.openspcoop2.core.constants.Costanti.ERRORE_VALIDAZIONE_RICHIESTA, "true");
					
					// validazione abilitata
					openspcoopstate.releaseResource();
					if (this.msgContext.isGestioneRisposta()) {
						IntegrationFunctionError integrationFunctionError = null;
						if(ex.getErrore()!=null &&
								(
										//CodiceErroreIntegrazione.CODICE_417_COSTRUZIONE_VALIDATORE_TRAMITE_INTERFACCIA_FALLITA.equals(ex.getErrore().getCodiceErrore()) ||
										CodiceErroreIntegrazione.CODICE_418_VALIDAZIONE_RICHIESTA_TRAMITE_INTERFACCIA_FALLITA.equals(ex.getErrore().getCodiceErrore()) 
								)
							){
							integrationFunctionError = IntegrationFunctionError.INVALID_REQUEST_CONTENT;
						}
						else{
							integrationFunctionError = IntegrationFunctionError.INTERNAL_REQUEST_ERROR;
						}
						this.msgContext.setMessageResponse((this.generatoreErrore.build(pddContext,integrationFunctionError, ex.getErrore(),ex,null)));
					}
					return;
				}
			} catch (Exception ex) {
				msgDiag.addKeywordErroreProcessamento(ex);
				logCore.error("Riscontrato errore durante la validazione xsd della richiesta applicativa",ex);
				if (CostantiConfigurazione.STATO_CON_WARNING_WARNING_ONLY.equals(validazioneContenutoApplicativoApplicativo.getStato())) {
					msgDiag.logPersonalizzato("validazioneContenutiApplicativiRichiestaNonRiuscita.warningOnly");
				}
				else {
					msgDiag.logPersonalizzato("validazioneContenutiApplicativiRichiestaNonRiuscita");
				}
				if (CostantiConfigurazione.STATO_CON_WARNING_WARNING_ONLY.equals(validazioneContenutoApplicativoApplicativo.getStato()) == false) {
					
					pddContext.addObject(org.openspcoop2.core.constants.Costanti.ERRORE_VALIDAZIONE_RICHIESTA, "true");
					
					// validazione abilitata
					openspcoopstate.releaseResource();
					if (this.msgContext.isGestioneRisposta()) {
						this.msgContext.setMessageResponse((this.generatoreErrore.build(pddContext,IntegrationFunctionError.INTERNAL_REQUEST_ERROR,
								ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
								get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_531_VALIDAZIONE_TRAMITE_INTERFACCIA_FALLITA),ex,null)));
					}
					return;
				}
			} finally {
				transaction.getTempiElaborazione().endValidazioneRichiesta();
				if (binXSD != null) {
					try {
						binXSD.close();
					} catch (Exception e) {}
				}
			}
		}
		else{
			msgDiag.logPersonalizzato("validazioneContenutiApplicativiRichiestaDisabilitata");
		}

		
		
		
		
		
		
		
		
		
		Utilities.printFreeMemory("RicezioneContenutiApplicativi - Autorizzazione del servizio applicativo...");
		
		
		
		/* ------------ Autorizzazione per Contenuto ------------- */
		msgDiag.mediumDebug("Autorizzazione del servizio applicativo...");
		this.msgContext.getIntegrazione().setTipoAutorizzazioneContenuto(tipoAutorizzazioneContenuto);
		if(tipoAutorizzazioneContenuto!=null){
			msgDiag.addKeyword(CostantiPdD.KEY_TIPO_AUTORIZZAZIONE_CONTENUTO, tipoAutorizzazioneContenuto);
		}
		if (CostantiConfigurazione.AUTORIZZAZIONE_NONE.equalsIgnoreCase(tipoAutorizzazioneContenuto) == false) {
			
			transaction.getTempiElaborazione().startAutorizzazioneContenuti();
			try {
			
				msgDiag.logPersonalizzato("autorizzazioneContenutiApplicativiInCorso");
				
				ErroreIntegrazione errore = null;
				Exception eAutorizzazione = null;
				boolean detailsSet = false;
				IntegrationFunctionError integrationFunctionError = null;
				try {
					// Controllo Autorizzazione
					EsitoAutorizzazionePortaDelegata esito = 
							GestoreAutorizzazione.verificaAutorizzazioneContenutoPortaDelegata(tipoAutorizzazioneContenuto, datiInvocazione, pddContext, protocolFactory, requestMessage, logCore);
					CostantiPdD.addKeywordInCache(msgDiag, esito.isEsitoPresenteInCache(),
							pddContext, CostantiPdD.KEY_INFO_IN_CACHE_FUNZIONE_AUTORIZZAZIONE_CONTENUTI);
					if(esito.getDetails()==null){
						msgDiag.addKeyword(CostantiPdD.KEY_DETAILS, "");
					}else{
						msgDiag.addKeyword(CostantiPdD.KEY_DETAILS, " ("+esito.getDetails()+")");
					}
					detailsSet = true;
					if (esito.isAutorizzato() == false) {
						errore = esito.getErroreIntegrazione();
						eAutorizzazione = esito.getEccezioneProcessamento();
						integrationFunctionError = esito.getIntegrationFunctionError();
						pddContext.addObject(org.openspcoop2.core.constants.Costanti.ERRORE_AUTORIZZAZIONE, "true");
					}
					else{
						msgDiag.logPersonalizzato("autorizzazioneContenutiApplicativiEffettuata");
					}
				} catch (Exception e) {
					String msgErroreAutorizzazione = "processo di autorizzazione ["
							+ tipoAutorizzazioneContenuto + "] fallito, " + e.getMessage();
					errore = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
							get5XX_ErroreProcessamento(msgErroreAutorizzazione, CodiceErroreIntegrazione.CODICE_542_AUTORIZZAZIONE_CONTENUTO);
					eAutorizzazione = e;
					logCore.error(msgErroreAutorizzazione,e);
				}
				if (errore != null) {
					if(!detailsSet) {
						msgDiag.addKeyword(CostantiPdD.KEY_DETAILS, "");
					}
					msgDiag.addKeyword(CostantiPdD.KEY_ERRORE_PROCESSAMENTO, errore.getDescrizione(protocolFactory));
					msgDiag.logPersonalizzato("servizioApplicativoFruitore.contenuto.nonAutorizzato");
					openspcoopstate.releaseResource();
	
					if (this.msgContext.isGestioneRisposta()) {
						if(CodiceErroreIntegrazione.CODICE_404_AUTORIZZAZIONE_FALLITA.equals(errore.getCodiceErrore()) || 
								CodiceErroreIntegrazione.CODICE_428_AUTORIZZAZIONE_CONTENUTO_FALLITA.equals(errore.getCodiceErrore())){
							if(integrationFunctionError==null) {
								integrationFunctionError = IntegrationFunctionError.CONTENT_AUTHORIZATION_DENY;
							}
						}else{
							if(integrationFunctionError==null) {
								integrationFunctionError = IntegrationFunctionError.INTERNAL_REQUEST_ERROR;
							}
						}
						this.msgContext.setMessageResponse((this.generatoreErrore.build(pddContext,integrationFunctionError,errore,
								eAutorizzazione,null)));
					}
					return;
				}
			}finally {
				transaction.getTempiElaborazione().endAutorizzazioneContenuti();
			}
		}
		else{
			msgDiag.logPersonalizzato("autorizzazioneContenutiApplicativiDisabilitata");
		}
		
		
		
		
		
		
		
		
		
		
		
		Utilities.printFreeMemory("RicezioneContenutiApplicativi - Controllo tipo di invocazione (riferimento/normale)...");
		
		
		
		/*
		 * ------------ Check tipo di invocazione PD (x riferimento) -------------
		 */
		msgDiag.mediumDebug("Controllo tipo di invocazione (riferimento/normale)...");
		boolean invocazionePDPerRiferimento = false;
		try {
			invocazionePDPerRiferimento = configurazionePdDReader.invocazionePortaDelegataPerRiferimento(sa);
		} catch (Exception e) {
			msgDiag.logErroreGenerico(e,"invocazionePortaDelegataPerRiferimento(sa)");
			openspcoopstate.releaseResource();
			if (this.msgContext.isGestioneRisposta()) {
				this.msgContext.setMessageResponse((this.generatoreErrore.build(pddContext,IntegrationFunctionError.INTERNAL_REQUEST_ERROR,
						ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
						get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_536_CONFIGURAZIONE_NON_DISPONIBILE),e,null)));
			}
			return;
		}
		if (invocazionePDPerRiferimento) {
			if (this.msgContext.isInvocazionePDPerRiferimento() == false) {
				msgDiag.logPersonalizzato("portaDelegataInvocabilePerRiferimento.riferimentoNonPresente");
				openspcoopstate.releaseResource();
				if (this.msgContext.isGestioneRisposta()) {
					this.msgContext.setMessageResponse((this.generatoreErrore.build(pddContext,IntegrationFunctionError.BAD_REQUEST,
							ErroriIntegrazione.ERRORE_412_PD_INVOCABILE_SOLO_PER_RIFERIMENTO.
							getErroreIntegrazione(),null,null)));
				}
				return;
			}
			
			// eventuale sbustamento delle informazioni di protocollo se richieste dal servizio applicativo
			GestoreMessaggi gestoreMessaggio = new GestoreMessaggi(openspcoopstate, true,this.msgContext.getIdInvocazionePDPerRiferimento(),Costanti.INBOX,msgDiag,this.msgContext.getPddContext());
			try{
				boolean sbustamento_informazioni_protocollo =
						gestoreMessaggio.sbustamentoInformazioniProtocollo(servizioApplicativo,false);
				if(sbustamento_informazioni_protocollo){
					// attachments non gestiti!
					ProprietaManifestAttachments proprietaManifest = propertiesReader.getProprietaManifestAttachments("standard");
					proprietaManifest.setGestioneManifest(false);
					ProtocolMessage protocolMessage = bustaBuilder.sbustamento(requestMessage, pddContext,
							bustaRichiesta, RuoloMessaggio.RICHIESTA, proprietaManifest,
							FaseSbustamento.PRE_INVIO_RICHIESTA_PER_RIFERIMENTO, requestInfo.getIntegrationServiceBinding(), requestInfo.getBindingConfig());
					if(protocolMessage!=null) {
						requestMessage = protocolMessage.getMessage(); // updated
					}
				}
			}catch(Exception e){
				msgDiag.logErroreGenerico(e,"invocazionePortaDelegataPerRiferimento.sbustamentoProtocolHeader()");
				openspcoopstate.releaseResource();
				if (this.msgContext.isGestioneRisposta()) {
					this.msgContext.setMessageResponse((this.generatoreErrore.build(pddContext,IntegrationFunctionError.INTERNAL_REQUEST_ERROR,
							ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
							get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_536_CONFIGURAZIONE_NON_DISPONIBILE),e,null)));
				}
				return;		
			}
			
		} else {
			if (this.msgContext.isInvocazionePDPerRiferimento()) {
				msgDiag.logPersonalizzato("portaDelegataInvocabileNormalmente.riferimentoPresente");
				openspcoopstate.releaseResource();
				if (this.msgContext.isGestioneRisposta()) {
					this.msgContext.setMessageResponse((this.generatoreErrore.build(pddContext,IntegrationFunctionError.BAD_REQUEST,
							ErroriIntegrazione.ERRORE_413_PD_INVOCABILE_SOLO_SENZA_RIFERIMENTO.
							getErroreIntegrazione(),null,null)));
				}
				return;
			}
		}

		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		Utilities.printFreeMemory("RicezioneContenutiApplicativi - Controllo non esistenza di una busta ...");
		
		
		
		
		/* ------------ Controllo che il messaggio non contenga una busta */
		msgDiag.mediumDebug("Controllo non esistenza di una busta ...");
		ValidazioneSintattica validatoreSintattico = new ValidazioneSintattica(pddContext, openspcoopstate.getStatoRichiesta(),requestMessage, protocolFactory);
		boolean esisteProtocolloMsgRichiesta = false;
		try{
			esisteProtocolloMsgRichiesta = validatoreSintattico.
					verifyProtocolPresence(this.msgContext.getTipoPorta(),infoServizio.getProfiloDiCollaborazione(),RuoloMessaggio.RICHIESTA);
		} catch (Exception e){
			msgDiag.logErroreGenerico(e,"controlloEsistenzaBusta");
			openspcoopstate.releaseResource();
			if (this.msgContext.isGestioneRisposta()) {
				this.msgContext.setMessageResponse((this.generatoreErrore.build(pddContext,IntegrationFunctionError.INTERNAL_REQUEST_ERROR,
						ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
						get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_525_GESTIONE_FUNZIONALITA_PROTOCOLLO),e,null)));
			}
			return;
		} finally {
			if(esisteProtocolloMsgRichiesta) {
				msgDiag.logPersonalizzato("richiestaContenenteBusta");
				openspcoopstate.releaseResource();
				if (this.msgContext.isGestioneRisposta()) {
					this.msgContext.setMessageResponse((this.generatoreErrore.build(pddContext,IntegrationFunctionError.INTEROPERABILITY_PROFILE_REQUEST_ALREADY_EXISTS,
							ErroriIntegrazione.ERRORE_420_BUSTA_PRESENTE_RICHIESTA_APPLICATIVA.
							getErroreIntegrazione(),null,null)));
				}
				return;
			}
			// *** GB ***
			if(validatoreSintattico!=null){
				validatoreSintattico.setHeaderSOAP(null);
			}
			validatoreSintattico = null;
			// *** GB ***
		}
		
		
		
		
		
		
		
		
		
		
		
		
		Utilities.printFreeMemory("RicezioneContenutiApplicativi - Recupero configurazione per salvataggio risposta in cache ...");
		msgDiag.mediumDebug("Recupero configurazione per salvataggio risposta in cache ...");
		try{
			ResponseCachingConfigurazione responseCachingConfig = configurazionePdDReader.getConfigurazioneResponseCaching(portaDelegata);
			if(responseCachingConfig!=null && StatoFunzionalita.ABILITATO.equals(responseCachingConfig.getStato())) {
				
				transaction.getTempiElaborazione().startResponseCachingCalcoloDigest();
				try {
				
					msgDiag.mediumDebug("Calcolo digest per salvataggio risposta ...");
					
					HashGenerator hashGenerator = new HashGenerator(propertiesReader.getCachingResponseDigestAlgorithm());
					String digest = hashGenerator.buildKeyCache(requestMessage, requestInfo, responseCachingConfig);
					requestMessage.addContextProperty(CostantiPdD.RESPONSE_CACHE_REQUEST_DIGEST, digest);
				
				}finally {
					transaction.getTempiElaborazione().endResponseCachingCalcoloDigest();
				}
			}
		} catch (Exception e){
			msgDiag.logErroreGenerico(e,"calcoloDigestSalvataggioRisposta");
			openspcoopstate.releaseResource();
			if (this.msgContext.isGestioneRisposta()) {
				this.msgContext.setMessageResponse((this.generatoreErrore.build(pddContext,IntegrationFunctionError.INTERNAL_REQUEST_ERROR,
						ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
						get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_561_DIGEST_REQUEST),e,null)));
			}
			return;
		} 
		
		
		
		
		
		
		
		
		Utilities.printFreeMemory("RicezioneContenutiApplicativi - Update/Delete Header di integrazione se necessario");
		
		
		
		
		
		/*
		 * ------- Update/Delete Header di integrazione se necessario -------
		 */
		// L'header di integrazione SOAP non e' cancellato se presente.
		// Deve allora essere aggiornato ai valori letti
		
		for (int i = 0; i < tipiIntegrazionePD.length; i++) {
			try {
				//IGestoreIntegrazionePD gestore = RicezioneContenutiApplicativi.gestoriIntegrazionePD.get(tipiIntegrazionePD[i]);
				
				String classType = null;
				IGestoreIntegrazionePD gestore = null;
				try {
					classType = className.getIntegrazionePortaDelegata(tipiIntegrazionePD[i]);
					gestore = (IGestoreIntegrazionePD) loader.newInstance(classType);
					AbstractCore.init(gestore, pddContext, protocolFactory);
				} catch (Exception e) {
					throw new Exception(
							"Riscontrato errore durante il caricamento della classe ["+ classType
									+ "] da utilizzare per la gestione dell'integrazione (Update/Delete) di tipo ["+ tipiIntegrazionePD[i] + "]: " + e.getMessage());
				}
				
				if ((gestore != null) && (gestore instanceof IGestoreIntegrazionePDSoap)) {
					if(propertiesReader.deleteHeaderIntegrazioneRequestPD()){
						// delete
						((IGestoreIntegrazionePDSoap)gestore).deleteInRequestHeader(inRequestPDMessage);
					}
					else{
						// update
						String servizioApplicativoDaInserireHeader = null;
						if(CostantiPdD.SERVIZIO_APPLICATIVO_ANONIMO.equals(servizioApplicativo)==false){
							servizioApplicativoDaInserireHeader = servizioApplicativo;
						}
						((IGestoreIntegrazionePDSoap)gestore).updateInRequestHeader(inRequestPDMessage, idServizio, 
								idMessageRequest, servizioApplicativoDaInserireHeader, idCorrelazioneApplicativa);
					}
				} 
			} catch (Exception e) {
				if(propertiesReader.deleteHeaderIntegrazioneRequestPD()){
					msgDiag.logErroreGenerico(e,"deleteHeaderIntegrazione("+ tipiIntegrazionePD[i]+")");
				}else{
					msgDiag.logErroreGenerico(e,"updateHeaderIntegrazione("+ tipiIntegrazionePD[i]+")");
				}
				openspcoopstate.releaseResource();
				if (this.msgContext.isGestioneRisposta()) {
					this.msgContext.setMessageResponse((this.generatoreErrore.build(pddContext,IntegrationFunctionError.INTERNAL_REQUEST_ERROR,
							ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
							get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_541_GESTIONE_HEADER_INTEGRAZIONE),e,null)));
				}
				return;
			}
		}
		
		
		
		
		
		
		
		Utilities.printFreeMemory("RicezioneContenutiApplicativi - Gestione Funzionalita' speciali per Attachments (Manifest)");
		
		
		
		
		
		/*
		 * ------------ Gestione Funzionalita' speciali per Attachments (Manifest) -------------
		 */
		boolean allegaBody = false;
		boolean scartaBody = false;
		try {
			allegaBody = configurazionePdDReader.isAllegaBody(portaDelegata);
			scartaBody = configurazionePdDReader.isScartaBody(portaDelegata);
		} catch (Exception e) {
			msgDiag.logErroreGenerico(e,"configurazionePdDReader.isAllega/ScartaBody(pd)");
			openspcoopstate.releaseResource();
			if (this.msgContext.isGestioneRisposta()) {
				this.msgContext.setMessageResponse((this.generatoreErrore.build(pddContext,IntegrationFunctionError.INTERNAL_REQUEST_ERROR,
						ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
						get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_536_CONFIGURAZIONE_NON_DISPONIBILE),e,null)));
			}
			return;
		}
		if (scartaBody) {
			IntegrationFunctionError integrationFunctionError = null;
			try {
				if(ServiceBinding.SOAP.equals(requestMessage.getServiceBinding())==false){
					integrationFunctionError = IntegrationFunctionError.NOT_SUPPORTED_BY_PROTOCOL;
					throw new Exception("Funzionalita 'ScartaBody' valida solamente per Service Binding SOAP");
				}
				
				// E' permesso SOLO per messaggi con attachment
				if (requestMessage.castAsSoap().countAttachments() <= 0) {
					throw new Exception("La funzionalita' e' permessa solo per messaggi SOAP With Attachments");
				}
			} catch (Exception e) {
				msgDiag.addKeywordErroreProcessamento(e);
				msgDiag.logPersonalizzato("funzionalitaScartaBodyNonEffettuabile");
				openspcoopstate.releaseResource();
				if (this.msgContext.isGestioneRisposta()) {
					if(integrationFunctionError==null) {
						integrationFunctionError = IntegrationFunctionError.BAD_REQUEST;
					}
					this.msgContext.setMessageResponse((this.generatoreErrore.build(pddContext,integrationFunctionError,
							ErroriIntegrazione.ERRORE_425_SCARTA_BODY.
							getErrore425_ScartaBody(e.getMessage()),e,null)));
				}
				return;
			}
		}
		if (allegaBody) {
			try {
				TunnelSoapUtils.allegaBody(requestMessage, propertiesReader.getHeaderSoapActorIntegrazione());
			} catch (Exception e) {
				msgDiag.addKeywordErroreProcessamento(e);
				msgDiag.logPersonalizzato("funzionalitaAllegaBodyNonEffettuabile");
				openspcoopstate.releaseResource();
				if (this.msgContext.isGestioneRisposta()) {
					this.msgContext.setMessageResponse((this.generatoreErrore.build(pddContext,IntegrationFunctionError.BAD_REQUEST,
							ErroriIntegrazione.ERRORE_424_ALLEGA_BODY.
							getErrore424_AllegaBody(e.getMessage()), e,null)));
				}
				return;
			}
		}

		
		
		
		
		
		
		
		
		
		
		/* ------------- Modalita' di gestione ---------------------------- */
		
		// Versione OneWay 
		boolean oneWayStateless = false;
		boolean oneWayVersione11 = false;
		// Versione Sincrono 
		boolean sincronoStateless = false;
		// Asincrono stateless
		boolean asincronoStateless = false;
		
		// Gestione stateless
		boolean portaStateless = false; // vero se almeno uno dei precedenti e' vero

		try {

			if(propertiesReader.isServerJ2EE()==false){
				// Stateless obbligatorio in server di tipo web (non j2ee)
				oneWayStateless = true;
				sincronoStateless = true;
				asincronoStateless = true;
			}
			else if (ProfiloDiCollaborazione.ONEWAY.equals(infoServizio.getProfiloDiCollaborazione())) {
				oneWayStateless = configurazionePdDReader.isModalitaStateless(portaDelegata, infoServizio.getProfiloDiCollaborazione());
			} else if (ProfiloDiCollaborazione.SINCRONO.equals(infoServizio.getProfiloDiCollaborazione())) {
				sincronoStateless = configurazionePdDReader.isModalitaStateless(portaDelegata, infoServizio.getProfiloDiCollaborazione());
			} else if(ProfiloDiCollaborazione.ASINCRONO_SIMMETRICO.equals(infoServizio.getProfiloDiCollaborazione()) ||
					ProfiloDiCollaborazione.ASINCRONO_ASIMMETRICO.equals(infoServizio.getProfiloDiCollaborazione())){
				asincronoStateless = configurazionePdDReader.isModalitaStateless(portaDelegata, infoServizio.getProfiloDiCollaborazione());
			}

			oneWayVersione11 = propertiesReader.isGestioneOnewayStateful_1_1()
				&& ProfiloDiCollaborazione.ONEWAY.equals(infoServizio.getProfiloDiCollaborazione())
					&& !oneWayStateless;

			if (oneWayStateless || sincronoStateless || asincronoStateless || oneWayVersione11) {
				openspcoopstate = OpenSPCoopState.toStateless(((OpenSPCoopStateful)openspcoopstate), true);
				portaStateless = true;
				if(oneWayVersione11==false){
					this.msgContext.getIntegrazione().setGestioneStateless(true);
				}else{
					this.msgContext.getIntegrazione().setGestioneStateless(false);
				}
			}else{
				this.msgContext.getIntegrazione().setGestioneStateless(false);
			}

		} catch (Exception e) {
			msgDiag.logErroreGenerico(e,"GestioneStatelessStateful");
			logCore.error("Analisi modalita di gestione STATEFUL/STATELESS non riuscita: "+ e);
			openspcoopstate.releaseResource();
			if (this.msgContext.isGestioneRisposta()) {
				this.msgContext.setMessageResponse((this.generatoreErrore.build(pddContext,IntegrationFunctionError.INTERNAL_REQUEST_ERROR,
						ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
						get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_536_CONFIGURAZIONE_NON_DISPONIBILE),e,null)));
			}
			return;
		}

		
		
		
		
		

		
		
		
		/* ------------- Modalita' di forward ---------------------------- */
		
		// Versione OneWay 
		boolean localForward = false;
		LocalForwardEngine localForwardEngine = null;
		LocalForwardParameter localForwardParameter = null;
		PortaApplicativa pa = null;
		try {

			localForward = configurazionePdDReader.isLocalForwardMode(portaDelegata);
			
			if(localForward){
								
				String erroreConfigurazione = null;
				
				String prefix = "( Servizio "+IDServizioFactory.getInstance().getUriFromIDServizio(idServizio)+" ) ";
				
				if(ProfiloDiCollaborazione.ASINCRONO_SIMMETRICO.equals(infoServizio.getProfiloDiCollaborazione()) ||
						ProfiloDiCollaborazione.ASINCRONO_ASIMMETRICO.equals(infoServizio.getProfiloDiCollaborazione())){
					erroreConfigurazione = "profilo di collaborazione "+infoServizio.getProfiloDiCollaborazione().getEngineValue()+" non supportato";							
				}
				
				if(erroreConfigurazione==null){
					if (ProfiloDiCollaborazione.SINCRONO.equals(infoServizio.getProfiloDiCollaborazione())) {
						if(sincronoStateless==false){
							erroreConfigurazione = "profilo di collaborazione "+infoServizio.getProfiloDiCollaborazione().getEngineValue()+" non supportato nella modalità stateful";	
						}
					}
				}
				
				if(erroreConfigurazione==null){
					if(configurazionePdDReader.existsSoggetto(idServizio.getSoggettoErogatore())==false){
						erroreConfigurazione = "il soggetto erogatore non risulta essere gestito localmente dalla Porta";
					}
				}
				
				RichiestaApplicativa ra = null;
				IDPortaApplicativa idPA = null;
				if(erroreConfigurazione==null){

					String nomePA = configurazionePdDReader.getLocalForward_NomePortaApplicativa(portaDelegata);
					if(nomePA==null){
						try{
							List<PortaApplicativa> list = configurazionePdDReader.getPorteApplicative(idServizio, false);
							if(list.size()<=0){
								throw new DriverConfigurazioneNotFound("NotFound");
							}
							if(list.size()>1){
								StringBuilder bf = new StringBuilder();
								for (PortaApplicativa portaApplicativa : list) {
									if(bf.length()>0) {
										bf.append(",");
									}
									bf.append(portaApplicativa.getNome());
								}
								throw new Exception("Esiste più di una porta applicativa indirizzabile tramite il servizio ["+idServizio+"] indicato nella porta delegata ["+
										nomeUtilizzatoPerErrore+"]: "+bf.toString());
							}
							idPA = configurazionePdDReader.convertToIDPortaApplicativa(list.get(0));
						}catch(DriverConfigurazioneNotFound n){
							erroreConfigurazione = "Non esiste alcuna porta applicativa indirizzabile tramite il servizio ["+idServizio+"] indicato nella porta delegata ["+
									nomeUtilizzatoPerErrore+"]";
						}catch(Exception e){
							erroreConfigurazione = e.getMessage();
						}
					}
					else{
						try{
							idPA = configurazionePdDReader.getIDPortaApplicativa(nomePA, protocolFactory);
						}catch(Exception e){
							erroreConfigurazione = e.getMessage();
						}
					}
				}
				
				if(erroreConfigurazione==null){
					ra = new RichiestaApplicativa(soggettoFruitore,idServizio.getSoggettoErogatore(), idPA);
					pa = configurazionePdDReader.getPortaApplicativa_SafeMethod(ra.getIdPortaApplicativa());
					if(pa.sizeServizioApplicativoList()<=0){
						erroreConfigurazione = "non risultano registrati servizi applicativi erogatori associati alla porta applicativa ("+pa.getNome()
								+") relativa al servizio richiesto";
					}
				}
				
				
				if(erroreConfigurazione!=null){
					erroreConfigurazione = prefix + erroreConfigurazione;
					msgDiag.addKeyword(CostantiPdD.KEY_ERRORE_PROCESSAMENTO,erroreConfigurazione);
					msgDiag.logPersonalizzato("localForward.configError");
					
					openspcoopstate.releaseResource();
					if (this.msgContext.isGestioneRisposta()) {
						this.msgContext.setMessageResponse((this.generatoreErrore.build(pddContext,IntegrationFunctionError.INTERNAL_REQUEST_ERROR,
								ErroriIntegrazione.ERRORE_435_LOCAL_FORWARD_CONFIG_NON_VALIDA.
								getErrore435_LocalForwardConfigNonValida(erroreConfigurazione),null,null)));
					}
					return;
				}
				
				localForwardParameter = new LocalForwardParameter();
				localForwardParameter.setLog(logCore);
				localForwardParameter.setConfigurazionePdDReader(configurazionePdDReader);
				localForwardParameter.setIdCorrelazioneApplicativa(idCorrelazioneApplicativa);
				localForwardParameter.setIdentitaPdD(identitaPdD);
				localForwardParameter.setIdModulo(this.msgContext.getIdModulo());
				localForwardParameter.setIdRequest(idMessageRequest);
				localForwardParameter.setImplementazionePdDDestinatario(implementazionePdDDestinatario);
				localForwardParameter.setImplementazionePdDMittente(implementazionePdDMittente);
				localForwardParameter.setIdPdDMittente(idPdDMittente);
				localForwardParameter.setIdPdDDestinatario(idPdDDestinatario);
				localForwardParameter.setInfoServizio(infoServizio);
				localForwardParameter.setMsgDiag(msgDiag);
				localForwardParameter.setOpenspcoopstate(openspcoopstate);
				localForwardParameter.setPddContext(inRequestContext.getPddContext());
				localForwardParameter.setProtocolFactory(protocolFactory);
				localForwardParameter.setRichiestaDelegata(richiestaDelegata);
				localForwardParameter.setStateless(portaStateless);
				localForwardParameter.setOneWayVersione11(oneWayVersione11);
				localForwardParameter.setIdPortaApplicativaIndirizzata(idPA);
				
				localForwardEngine = new LocalForwardEngine(localForwardParameter);
							
			}
			
		} catch (Exception e) {
			msgDiag.logErroreGenerico(e,"GestioneLocalForward");
			openspcoopstate.releaseResource();
			if (this.msgContext.isGestioneRisposta()) {
				this.msgContext.setMessageResponse((this.generatoreErrore.build(pddContext,IntegrationFunctionError.INTERNAL_REQUEST_ERROR,
						ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
						get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_554_LOCAL_FORWARD_ERROR),e,null)));
			}
			return;
		}
		
		
		if(localForward){
			try {
				if(localForwardEngine.processRequest(requestMessage)==false){
					openspcoopstate.releaseResource();
					if (this.msgContext.isGestioneRisposta()) {
						this.msgContext.setMessageResponse(localForwardEngine.getResponseMessageError());
					}
					return;
				}
				if(localForwardEngine.getRequestMessageAfterProcess()!=null){
					// Messaggio aggiornato
					requestMessage = localForwardEngine.getRequestMessageAfterProcess();
				}
			} catch (Exception e) {
				msgDiag.logErroreGenerico(e,"GestioneLocalForward.processRequest");
				openspcoopstate.releaseResource();
				if (this.msgContext.isGestioneRisposta()) {
					this.msgContext.setMessageResponse((this.generatoreErrore.build(pddContext,AbstractErrorGenerator.getIntegrationInternalError(pddContext),
							ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
							get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_555_LOCAL_FORWARD_PROCESS_REQUEST_ERROR),e,null)));
				}
				return;
			}
		}
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		Utilities.printFreeMemory("RicezioneContenutiApplicativi - Controllo presenza del messaggio gia' in gestione...");
		
		
		
		
		
		/* ---------------- Controllo esistenza messaggio --------------------- */
		msgDiag.mediumDebug("Controllo presenza del messaggio gia' in gestione...");
		GestoreMessaggi msgRequest = null;
		String tipoMessaggio = Costanti.OUTBOX;
		if(localForward){
			tipoMessaggio = Costanti.INBOX;
		}
		msgRequest = new GestoreMessaggi(openspcoopstate, true,idMessageRequest, tipoMessaggio, msgDiag, inRequestContext.getPddContext());
		msgRequest.setOneWayVersione11(oneWayVersione11);
		RepositoryBuste repositoryBuste = new RepositoryBuste(openspcoopstate.getStatoRichiesta(), true, protocolFactory);
		try {
			if (msgRequest.existsMessage_noCache()) {

				// Se il proprietario attuale e' GestoreMessaggi, forzo
				// l'eliminazione e continuo a processare il messaggio.
				String proprietarioMessaggio = msgRequest.getProprietario(this.msgContext.getIdModulo());
				if (TimerGestoreMessaggi.ID_MODULO.equals(proprietarioMessaggio)) {
					msgDiag.logPersonalizzato("messaggioInGestione.marcatoDaEliminare");
					String msg = msgDiag.getMessaggio_replaceKeywords("messaggioInGestione.marcatoDaEliminare");
					if(propertiesReader.isMsgGiaInProcessamento_useLock()) {
						msgRequest._deleteMessageWithLock(msg,propertiesReader.getMsgGiaInProcessamento_AttesaAttiva(),propertiesReader.getMsgGiaInProcessamento_CheckInterval());
					}
					else {
						msgRequest.deleteMessageByNow();
					}
				}

				// Altrimenti genero errore messaggio precedente ancora in
				// processamento
				else {
					msgDiag.addKeyword(CostantiPdD.KEY_PROPRIETARIO_MESSAGGIO, proprietarioMessaggio);
					msgDiag.logPersonalizzato("messaggioInGestione");
					pddContext.addObject(org.openspcoop2.core.constants.Costanti.RICHIESTA_DUPLICATA, "true");
					openspcoopstate.releaseResource(); 
					if (this.msgContext.isGestioneRisposta()) {
						this.msgContext.setMessageResponse((this.generatoreErrore.build(pddContext,IntegrationFunctionError.CONFLICT_IN_QUEUE,
								ErroriIntegrazione.ERRORE_537_BUSTA_GIA_RICEVUTA.get537_BustaGiaRicevuta(idMessageRequest),null,null)));
					}
					return;
				}
			}
		} catch (Exception e) {
			msgDiag.logErroreGenerico(e,"ControlloPresenzaMessaggioGiaInGestione");
			logCore.error("Controllo/gestione presenza messaggio gia in gestione non riuscito",e);
			openspcoopstate.releaseResource();
			if (this.msgContext.isGestioneRisposta()) {
				this.msgContext.setMessageResponse((this.generatoreErrore.build(pddContext,AbstractErrorGenerator.getIntegrationInternalError(pddContext),
						ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
						get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_537_BUSTA_GIA_RICEVUTA), e,null)));
			}
			return;
		}
		
		
		
		
		
		
		
		
		
		Utilities.printFreeMemory("RicezioneContenutiApplicativi - Registrazione messaggio di richiesta nel RepositoryMessaggi...");
		
		
		
		
		
		/*
		 * ---------------- Creo sessione di gestione del messaggio ricevuto Il
		 * messaggio viene assegnato al modulo 'Imbustamento' (futuro
		 * proprietario) ---------------------
		 */
		msgDiag.mediumDebug("Registrazione messaggio di richiesta nel RepositoryMessaggi...");
		IProtocolVersionManager moduleManager = protocolFactory.createProtocolVersionManager(richiestaDelegata.getProfiloGestione());
		boolean richiestaAsincronaSimmetricaStateless = false;		
		try {
			
			// In caso di richiestaAsincronaSimmetrica e openspcoop stateless,
			// Devo comunque salvare le informazioni sul msg della richiesta.
			// Tali informazioni servono per il check nel modulo RicezioneBuste, per verificare di gestire la risposta
			// solo dopo aver terminato di gestire la richiesta con relativa ricevuta.
			if(ProfiloDiCollaborazione.ASINCRONO_SIMMETRICO.equals(infoServizio.getProfiloDiCollaborazione()) && portaStateless){
				
				if (StatoFunzionalitaProtocollo.ABILITATA.equals(moduleManager.getCollaborazione(infoServizio))) {
					// Se presente riferimentoMessaggio utilizzo quello come riferimento asincrono per la risposta.
					if (headerIntegrazioneRichiesta.getBusta().getRiferimentoMessaggio() != null) {
						richiestaAsincronaSimmetricaStateless = false;
					} else if (headerIntegrazioneRichiesta.getBusta().getIdCollaborazione() != null) {
						// Utilizzo Collaborazione come riferimentoServizioCorrelato
						// Tanto nelle linee guida non possono esistere piu' istanze con la stessa collaborazione, e' stata deprecata.
						// Per igni istanza asincrona (richiesta/risposta) la richiesta genera una collaborazione a capostipite
						richiestaAsincronaSimmetricaStateless = false;
					}else{
						richiestaAsincronaSimmetricaStateless = true;
					}
				} else {
					richiestaAsincronaSimmetricaStateless = headerIntegrazioneRichiesta.getBusta().getRiferimentoMessaggio() == null;
				}

			}
			
			
			// Salvataggio messaggio
			msgRequest.registraMessaggio(requestMessage, dataIngressoRichiesta,
					(oneWayStateless || sincronoStateless || asincronoStateless),
					idCorrelazioneApplicativa);	
			if(localForward){
				msgRequest.aggiornaProprietarioMessaggio(org.openspcoop2.pdd.mdb.ConsegnaContenutiApplicativi.ID_MODULO);
			}else{
				msgRequest.aggiornaProprietarioMessaggio(org.openspcoop2.pdd.mdb.Imbustamento.ID_MODULO);
			}
						
			if(richiestaAsincronaSimmetricaStateless){
				msgRequest.registraInformazioniMessaggio_statelessEngine(dataIngressoRichiesta, org.openspcoop2.pdd.mdb.Imbustamento.ID_MODULO,
						idCorrelazioneApplicativa);
			}
			
		} catch (Exception e) {
			msgDiag.logErroreGenerico(e,"msgRequest.aggiornaProprietarioMessaggio");
			msgRequest.deleteMessageFromFileSystem(); // elimino richiesta salvata su fileSystem
			// Rilascio Connessione DB
			openspcoopstate.releaseResource();
			if (this.msgContext.isGestioneRisposta()) {
				this.msgContext.setMessageResponse((this.generatoreErrore.build(pddContext,AbstractErrorGenerator.getIntegrationInternalError(pddContext),
						ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
						get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_508_SAVE_REQUEST_MSG), e,null)));
			}
			return;
		}

		msgDiag.mediumDebug("Registrazione busta di richiesta nel RepositoryBuste...");
		try {
			if( (!portaStateless) || oneWayVersione11){
				// E' gia registrata se siamo in un contesto di correlazione applicativa				
				if (repositoryBuste.isRegistrata(idMessageRequest,tipoMessaggio)) {
					try{
						if(localForward){
							repositoryBuste.aggiornaBustaIntoInBox(idMessageRequest, soggettoFruitore, richiestaDelegata.getIdServizio(), 
									propertiesReader.getRepositoryIntervalloScadenzaMessaggi(), 
									infoServizio.getProfiloDiCollaborazione(), infoServizio.getConfermaRicezione(), infoServizio.getInoltro());
						}else{
							repositoryBuste.aggiornaBustaIntoOutBox(idMessageRequest, soggettoFruitore, richiestaDelegata.getIdServizio(),
									propertiesReader.getRepositoryIntervalloScadenzaMessaggi(),
									infoServizio.getProfiloDiCollaborazione(), infoServizio.getConfermaRicezione(), infoServizio.getInoltro());
						}
						repositoryBuste.impostaUtilizzoPdD(idMessageRequest,tipoMessaggio);
					}catch(Exception e){
						if(propertiesReader.isMsgGiaInProcessamento_useLock()) {
							String tipo = Costanti.OUTBOX;
							if(localForward){
								tipo = Costanti.INBOX;	
							}
							String causa = "Aggiornamento dati busta con id ["+idMessageRequest+"] tipo["+tipo+"] non riuscito: "+e.getMessage();
							try{
								GestoreMessaggi.acquireLock(msgRequest,TimerLock.newInstance(TipoLock._getLockGestioneRepositoryMessaggi()),msgDiag, causa, propertiesReader.getMsgGiaInProcessamento_AttesaAttiva(), propertiesReader.getMsgGiaInProcessamento_CheckInterval());
								// errore che puo' avvenire a causa del Timer delle Buste (vedi spiegazione in classe GestoreMessaggi.deleteMessageWithLock)
								// Si riesegue tutto il codice isRegistrata e update o create con il lock. Stavolta se avviene un errore non e' dovuto al timer.
								if (repositoryBuste.isRegistrata(idMessageRequest,tipoMessaggio)) {
									if(localForward){
										repositoryBuste.aggiornaBustaIntoInBox(idMessageRequest, soggettoFruitore, richiestaDelegata.getIdServizio(), 
												propertiesReader.getRepositoryIntervalloScadenzaMessaggi(), 
												infoServizio.getProfiloDiCollaborazione(), infoServizio.getConfermaRicezione(), infoServizio.getInoltro());
									}else{
										repositoryBuste.aggiornaBustaIntoOutBox(idMessageRequest, soggettoFruitore, richiestaDelegata.getIdServizio(),
												propertiesReader.getRepositoryIntervalloScadenzaMessaggi(),
												infoServizio.getProfiloDiCollaborazione(), infoServizio.getConfermaRicezione(), infoServizio.getInoltro());
									}
									repositoryBuste.impostaUtilizzoPdD(idMessageRequest,tipoMessaggio);
								}
								else {
									if(localForward){
										repositoryBuste.registraBustaIntoInBox(idMessageRequest, soggettoFruitore, richiestaDelegata.getIdServizio(),
												propertiesReader.getRepositoryIntervalloScadenzaMessaggi(),
												infoServizio.getProfiloDiCollaborazione(), infoServizio.getConfermaRicezione(), infoServizio.getInoltro());
									}
									else{
										repositoryBuste.registraBustaIntoOutBox(idMessageRequest,soggettoFruitore, richiestaDelegata.getIdServizio(),
												propertiesReader.getRepositoryIntervalloScadenzaMessaggi(),
												infoServizio.getProfiloDiCollaborazione(), infoServizio.getConfermaRicezione(), infoServizio.getInoltro());
									}
								}
							}finally{
								try{
									GestoreMessaggi.releaseLock(msgRequest,TimerLock.newInstance(TipoLock._getLockGestioneRepositoryMessaggi()),msgDiag, causa);
								}catch(Exception eUnlock){}
							}
						}
						else {
							throw e;
						}
					}
				} else {
					if(localForward){
						repositoryBuste.registraBustaIntoInBox(idMessageRequest, soggettoFruitore, richiestaDelegata.getIdServizio(),
								propertiesReader.getRepositoryIntervalloScadenzaMessaggi(),
								infoServizio.getProfiloDiCollaborazione(), infoServizio.getConfermaRicezione(), infoServizio.getInoltro());
					}
					else{
						repositoryBuste.registraBustaIntoOutBox(idMessageRequest,soggettoFruitore, richiestaDelegata.getIdServizio(),
								propertiesReader.getRepositoryIntervalloScadenzaMessaggi(),
								infoServizio.getProfiloDiCollaborazione(), infoServizio.getConfermaRicezione(), infoServizio.getInoltro());
					}
				}
				Integrazione infoIntegrazione = new Integrazione();
				infoIntegrazione.setIdModuloInAttesa(this.msgContext.getIdModulo());
				infoIntegrazione.setNomePorta(richiestaDelegata.getIdPortaDelegata().getNome());
				infoIntegrazione.setServizioApplicativo(richiestaDelegata.getServizioApplicativo());
				repositoryBuste.aggiornaInfoIntegrazione(idMessageRequest,tipoMessaggio,infoIntegrazione);
			}
		} catch (Exception e) {
			msgDiag.logErroreGenerico(e,"registrazioneAggiornamentoBusta");
			msgRequest.deleteMessageFromFileSystem(); // elimino richiesta salvata su fileSystem
			openspcoopstate.releaseResource();
			if (this.msgContext.isGestioneRisposta()) {
				this.msgContext.setMessageResponse((this.generatoreErrore.build(pddContext,AbstractErrorGenerator.getIntegrationInternalError(pddContext),
						ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
						get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_525_GESTIONE_FUNZIONALITA_PROTOCOLLO), e,null)));
			}
			return;
		}

		
		
		
		
		
		Utilities.printFreeMemory("RicezioneContenutiApplicativi - Invio messaggio al modulo di Imbustamento...");
		
		
		
		
		
		
		/* ------------ Spedizione a modulo Imbustamento/ConsegnaContenutiApplicativi ------------- */
		String nextModulo = org.openspcoop2.pdd.mdb.Imbustamento.ID_MODULO;
		if(localForward){
			msgDiag.addKeyword(CostantiPdD.KEY_PORTA_APPLICATIVA,pa.getNome());
			if(idServizio.getAzione()==null){
				msgDiag.addKeyword(CostantiPdD.KEY_AZIONE_BUSTA_RICHIESTA,"non presente");
			}
			msgDiag.logPersonalizzato("localForward.logInfo");
			nextModulo = ConsegnaContenutiApplicativi.ID_MODULO;
		}else{
			msgDiag.mediumDebug("Invio messaggio al modulo di Imbustamento...");
		}
		try {
			// Creazione ImbustamentoMessage
			msgDiag.highDebug("Creazione ObjectMessage for send nell'infrastruttura.");

			Serializable msgJMS = null;
			
			if(localForward){
			
				localForwardParameter.setRepositoryBuste(repositoryBuste);
				String portaDelegataAttuale = localForwardParameter.getMsgDiag().getPorta();
				localForwardParameter.getMsgDiag().updatePorta(TipoPdD.APPLICATIVA,localForwardParameter.getIdPortaApplicativaIndirizzata().getNome());
				localForwardEngine.updateLocalForwardParameter(localForwardParameter);
				
				localForwardEngine.sendRequest(msgRequest);
				
				// ripristino
				localForwardParameter.getMsgDiag().updatePorta(TipoPdD.DELEGATA,portaDelegataAttuale);
				
			}
			else{
				imbustamentoMSG.setRichiestaDelegata(richiestaDelegata);
				imbustamentoMSG.setInfoServizio(infoServizio);
				imbustamentoMSG.setOneWayVersione11(oneWayVersione11);
				if (headerIntegrazioneRichiesta.getBusta() != null) {
					// RiferimentoServizioCorrelato
					String riferimentoServizioCorrelato = moduleManager.getIdCorrelazioneAsincrona(
							headerIntegrazioneRichiesta.getBusta().getRiferimentoMessaggio(), headerIntegrazioneRichiesta.getBusta().getIdCollaborazione());
					if (riferimentoServizioCorrelato != null) {
						// Se presente riferimentoMessaggio utilizzo quello.
						imbustamentoMSG.setRiferimentoServizioCorrelato(riferimentoServizioCorrelato);
					}
					// Collaborazione
					if (headerIntegrazioneRichiesta.getBusta().getIdCollaborazione() != null)
						imbustamentoMSG.setIdCollaborazione(headerIntegrazioneRichiesta.getBusta().getIdCollaborazione());
					// RiferimentoMessaggio
					if (headerIntegrazioneRichiesta.getBusta().getRiferimentoMessaggio() != null)
						imbustamentoMSG.setIdRiferimentoMessaggio(headerIntegrazioneRichiesta.getBusta().getRiferimentoMessaggio());
				}
				// Implemnentazione della porta erogatrice
				imbustamentoMSG.setImplementazionePdDSoggettoMittente(implementazionePdDMittente);
				imbustamentoMSG.setImplementazionePdDSoggettoDestinatario(implementazionePdDDestinatario);
				// Indirizzo soggetti
				imbustamentoMSG.setIndirizzoSoggettoMittente(indirizzoFruitore);
				imbustamentoMSG.setIndirizzoSoggettoDestinatario(indirizzoErogatore);
				// PddContext
				imbustamentoMSG.setPddContext(inRequestContext.getPddContext());
			
				msgJMS = imbustamentoMSG;
			}

			if (!portaStateless) {
				logCore.debug(RicezioneContenutiApplicativi.ID_MODULO+ " :eseguo send verso "+nextModulo+"...");
				
				String classTypeNodeSender = null;
				INodeSender nodeSender = null;
				try {
					classTypeNodeSender = className.getNodeSender(propertiesReader.getNodeSender());
					nodeSender = (INodeSender) loader.newInstance(classTypeNodeSender);
				} catch (Exception e) {
					throw new Exception(
							"Riscontrato errore durante il caricamento della classe ["+ classTypeNodeSender
									+ "] da utilizzare per la spedizione nell'infrastruttura: "	+ e.getMessage());
				}
				
				// send JMS solo STATEFUL
				nodeSender.send(msgJMS,nextModulo, msgDiag,
						identitaPdD, this.msgContext.getIdModulo(), idMessageRequest, msgRequest);
				logCore.debug(RicezioneContenutiApplicativi.ID_MODULO+ " :send verso "+nextModulo+" effettuata");
			}

		} catch (Exception e) {
			logCore.error("Spedizione->"+nextModulo+" non riuscita",e);
			msgDiag.logErroreGenerico(e,"GenericLib.nodeSender.send("+nextModulo+")");
			msgRequest.deleteMessageFromFileSystem(); // elimino richiesta salvata precedentemente
			openspcoopstate.releaseResource();
			if (this.msgContext.isGestioneRisposta()) {
				this.msgContext.setMessageResponse((this.generatoreErrore.build(pddContext,AbstractErrorGenerator.getIntegrationInternalError(pddContext),
						ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
						get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_512_SEND),e,null)));
			}
			return;
		}

		
		
		
		
		Utilities.printFreeMemory("RicezioneContenutiApplicativi - Commit delle operazioni per la gestione della richiesta...");
		
		
		
		
		/* ------------ Commit/Rilascia connessione al DB ------------- */
		msgDiag.mediumDebug("Commit delle operazioni per la gestione della richiesta...");
		try {
			// Commit
			openspcoopstate.commit();
			logCore.debug(RicezioneContenutiApplicativi.ID_MODULO+ " :RicezioneContenutiApplicativi commit eseguito");
		} catch (Exception e) {
			msgDiag.logErroreGenerico(e,"openspcoopstate.commit");
			msgRequest.deleteMessageFromFileSystem(); // elimino richiesta salvata precedentemente
			openspcoopstate.releaseResource();
			if (this.msgContext.isGestioneRisposta()) {
				this.msgContext.setMessageResponse((this.generatoreErrore.build(pddContext,AbstractErrorGenerator.getIntegrationInternalError(pddContext),
						ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
						get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_506_COMMIT_JDBC),e,null)));
			}
			return;
		}

		if (!portaStateless) {
			// Aggiornamento cache messaggio
			if (msgRequest != null)
				msgRequest.addMessaggiIntoCache_readFromTable(RicezioneContenutiApplicativi.ID_MODULO, "richiesta");
			// Aggiornamento cache proprietario messaggio
			if (msgRequest != null)
				msgRequest.addProprietariIntoCache_readFromTable(RicezioneContenutiApplicativi.ID_MODULO, "richiesta",null, false);

			// Rilascia connessione al DB
			msgDiag.mediumDebug("Commit delle operazioni per la gestione della richiesta effettuato, rilascio della connessione...");
			openspcoopstate.releaseResource();
		}

		
		
		
		
	
		
		
		
		/* ---------- Parametri Gestione risposta ------------- */
		RicezioneContenutiApplicativiGestioneRisposta parametriGestioneRisposta =
			new RicezioneContenutiApplicativiGestioneRisposta();

		parametriGestioneRisposta.setOpenspcoopstate(openspcoopstate);
		
		parametriGestioneRisposta.setRegistroServiziReader(registroServiziReader);
		parametriGestioneRisposta.setConfigurazionePdDReader(configurazionePdDReader);
		parametriGestioneRisposta.setMsgDiag(msgDiag);
		parametriGestioneRisposta.setLogCore(logCore);
		parametriGestioneRisposta.setPropertiesReader(propertiesReader);
		
		parametriGestioneRisposta.setIdentitaPdD(identitaPdD);
		parametriGestioneRisposta.setIdMessageRequest(idMessageRequest);
		
		parametriGestioneRisposta.setHeaderIntegrazioneRichiesta(headerIntegrazioneRichiesta);
		parametriGestioneRisposta.setHeaderIntegrazioneRisposta(headerIntegrazioneRisposta);
		parametriGestioneRisposta.setTipiIntegrazionePD(tipiIntegrazionePD);
		
		parametriGestioneRisposta.setProprietaErroreAppl(proprietaErroreAppl);
		parametriGestioneRisposta.setServizioApplicativo(servizioApplicativo);
		
		parametriGestioneRisposta.setMsgRequest(msgRequest);
		parametriGestioneRisposta.setRepositoryBuste(repositoryBuste);
		
		parametriGestioneRisposta.setPortaStateless(portaStateless);
		parametriGestioneRisposta.setOneWayVers11(oneWayVersione11);
		parametriGestioneRisposta.setRichiestaAsincronaSimmetricaStateless(richiestaAsincronaSimmetricaStateless);

		parametriGestioneRisposta.setPortaDelegata(portaDelegata);
		parametriGestioneRisposta.setSoggettoMittente(soggettoFruitore);
		parametriGestioneRisposta.setIdServizio(idServizio);
		
		parametriGestioneRisposta.setLocalForward(localForward);
		
		parametriGestioneRisposta.setPddContext(inRequestContext.getPddContext());
		parametriGestioneRisposta.setProtocolFactory(protocolFactory);
		
		parametriGestioneRisposta.setBustaRichiesta(bustaRichiesta);
		
		

		
		
		
		/*
		 * ---------------- STATELESS OR Stateful v11 -------------
		 */
		if (portaStateless) {
			
			//  Durante le invocazioni non deve essere utilizzata la connessione al database 
			((OpenSPCoopStateless)openspcoopstate).setUseConnection(false);
			boolean result = comportamentoStateless(parametriGestioneRisposta, imbustamentoMSG);
			if (!result){
				openspcoopstate.releaseResource();
				return;
			}else{
				// ripristino utilizzo connessione al database
				((OpenSPCoopStateless)openspcoopstate).setUseConnection(true);
			}
		}

		
		// refresh risorse con nuovi stati
		configurazionePdDReader.updateState(openspcoopstate.getStatoRichiesta(),openspcoopstate.getStatoRisposta());
		registroServiziReader.updateState(openspcoopstate.getStatoRichiesta(),openspcoopstate.getStatoRisposta());
		msgDiag.updateState(openspcoopstate.getStatoRichiesta(),openspcoopstate.getStatoRisposta());
		
		
		
		Utilities.printFreeMemory("RicezioneContenutiApplicativi - Gestione risposta...");
		
		
		/* ------------ GestioneRisposta non effettuata ------------- */
		msgDiag.mediumDebug("Gestione risposta...");
		if (this.msgContext.isGestioneRisposta() == false) {
			if(portaStateless)
				openspcoopstate.releaseResource();
			return;
		}

		gestioneRisposta(parametriGestioneRisposta);
		msgDiag.mediumDebug("Lavoro Terminato.");

		}finally{ // try vedi  #try-finally-openspcoopstate#
			try{
				if(openspcoopstate!=null){
					openspcoopstate.forceFinallyReleaseResource();
				}
			}catch(Throwable e){
				if(msgDiag!=null){
					try{
						msgDiag.logErroreGenerico(e, "Rilascio risorsa");
					}catch(Throwable eLog){
						logCore.error("Diagnostico errore per Rilascio risorsa: "+eLog.getMessage(),eLog);
					}
				}
				else{
					logCore.error("Rilascio risorsa: "+e.getMessage(),e);
				}
			}
		}
	}

	
	private void setCredenziali(Credenziali credenziali,MsgDiagnostico msgDiag){
		if (credenziali != null) {
			if (credenziali.getUsername() != null){
				msgDiag.setServizioApplicativo("username("+ credenziali.getUsername() + ")");
			}else if (credenziali.getSubject() != null){
				msgDiag.setServizioApplicativo("subject("+ credenziali.getSubject() + ")");
			}else if (credenziali.getPrincipal() != null){
				msgDiag.setServizioApplicativo("principal("+ credenziali.getPrincipal() + ")");
			}
			else{
				msgDiag.setServizioApplicativo(null);
			}
		}
		
		String credenzialiFornite = "";
		if(credenziali!=null){
			credenzialiFornite = credenziali.toString();
		}
		msgDiag.addKeyword(CostantiPdD.KEY_CREDENZIALI_SA_FRUITORE, credenzialiFornite);
	}
	
	
	/*
	 * Gestione stateless
	 */
	private boolean comportamentoStateless(RicezioneContenutiApplicativiGestioneRisposta parametriGestioneRisposta,
			ImbustamentoMessage imbustamentoMSG)  {

		
		/* ------- Lettura parametri ---------- */
		
		EsitoLib esito;
		
		OpenSPCoopStateless openspcoopstate = (OpenSPCoopStateless) parametriGestioneRisposta.getOpenspcoopstate();
		
		ConfigurazionePdDManager configurazionePdDReader = parametriGestioneRisposta.getConfigurazionePdDReader();
		RegistroServiziManager registroServiziReader = parametriGestioneRisposta.getRegistroServiziReader();
		MsgDiagnostico msgDiag = parametriGestioneRisposta.getMsgDiag();
		Logger logCore = parametriGestioneRisposta.getLogCore();
		OpenSPCoop2Properties propertiesReader = parametriGestioneRisposta.getPropertiesReader();
		
		//SOAPVersion versioneSoap = (SOAPVersion) this.msgContext.getPddContext().getObject(org.openspcoop2.core.constants.Costanti.SOAP_VERSION);
		
		String idMessageRequest = parametriGestioneRisposta.getIdMessageRequest();
		
		//ProprietaErroreApplicativo proprietaErroreAppl = parametriGestioneRisposta.getProprietaErroreAppl();
		
		GestoreMessaggi msgRequest = parametriGestioneRisposta.getMsgRequest();
		
		//IDSoggetto identitaPdD = parametriGestioneRisposta.getIdentitaPdD();
		
		parametriGestioneRisposta.setPortaStateless(true);
		
		boolean rinegoziamentoConnessione = 
			propertiesReader.isRinegoziamentoConnessione(this.msgContext.getProtocol().getProfiloCollaborazione()) && (!parametriGestioneRisposta.isOneWayVers11());

		boolean localForward = parametriGestioneRisposta.isLocalForward();
		
		// ID Transazione
		String idTransazione = PdDContext.getValue(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE, imbustamentoMSG.getPddContext());
		
		PdDContext pddContext = parametriGestioneRisposta.getPddContext();
		
		if(localForward){
			
			
			// E' RicezioneContenutiApplicativi se siamo in oneway11 con presa in carico
			if( ConsegnaContenutiApplicativi.ID_MODULO.equals( ((OpenSPCoopStateless)openspcoopstate).getDestinatarioRequestMsgLib() )
					&&
				 ((OpenSPCoopStateless)openspcoopstate).getDestinatarioResponseMsgLib()==null ){
						
				
				
				/* ------------ Rilascio risorsa se e' presente rinegoziamento delle risorse ------------------ */
				// Rinegozio la connessione SOLO se siamo in oneway o sincrono stateless puro (non oneway11)
				if( rinegoziamentoConnessione ){
					msgDiag.highDebug("ConsegnaContenutiApplicativi stateless (commit) ...");
					openspcoopstate.setUseConnection(true);
					try{
						openspcoopstate.commit();
					}catch(Exception e){}
					openspcoopstate.releaseResource();
					openspcoopstate.setUseConnection(false);
				}
				
				
				
				/*
				 * ---------------------- CONSEGNA CONTENUTI APPLICATIVI ------------------
				 */
		
				ConsegnaContenutiApplicativi consegnaContenutiLib = null;
				try {
					consegnaContenutiLib = new ConsegnaContenutiApplicativi(logCore);
					esito = consegnaContenutiLib.onMessage(openspcoopstate);
					if(esito.getStatoInvocazione()==EsitoLib.ERRORE_NON_GESTITO){
						if(esito.getErroreNonGestito()!=null)
							throw esito.getErroreNonGestito();
						else
							throw new Exception("Errore non gestito");
					}
				} catch (Throwable e) {
					msgDiag.logErroreGenerico(e,"Stateless.ConsegnaContenutiApplicativi");
					logCore.error("Errore Generale durante la gestione stateless: "+e.getMessage(),e);
					msgRequest.deleteMessageFromFileSystem(); // elimino richiesta salvata precedentemente
					openspcoopstate.setUseConnection(true);
					openspcoopstate.releaseResource();
					if (this.msgContext.isGestioneRisposta()) {
						this.msgContext.setMessageResponse((this.generatoreErrore.build(pddContext,AbstractErrorGenerator.getIntegrationInternalError(pddContext),
								ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.getErroreIntegrazione(),e,null)));
					}
					return false;
				}
				
				if (esito.getStatoInvocazione() != EsitoLib.OK) {
					// ripristino utilizzo connessione al database
					openspcoopstate.setUseConnection(true);
					gestioneRisposta(parametriGestioneRisposta);
					return false;
				}
				
			}
	

			
		}
		
		else{ 
			/*------------------------------- IMBUSTAMENTO -------------------------------------------*/
			msgDiag.highDebug("Imbustamento stateless ...");
			org.openspcoop2.pdd.mdb.Imbustamento imbustamentoLib = null;
			openspcoopstate.setMessageLib(imbustamentoMSG);
			openspcoopstate.setIDMessaggioSessione(idMessageRequest);
			try {
				imbustamentoLib = new org.openspcoop2.pdd.mdb.Imbustamento(logCore);
				msgDiag.highDebug("Imbustamento stateless (invoco) ...");
				esito = imbustamentoLib.onMessage(openspcoopstate);
				msgDiag.highDebug("Imbustamento stateless (analizzo esito) ...");
				if(esito.getStatoInvocazione()==EsitoLib.ERRORE_NON_GESTITO){
					if(esito.getErroreNonGestito()!=null)
						throw esito.getErroreNonGestito();
					else
						throw new Exception("Errore non gestito");
				}
			} catch (Throwable e) {
				msgDiag.logErroreGenerico(e,"Stateless.Imbustamento");
				logCore.error("Errore Generale durante la gestione stateless: "+e.getMessage(),e);
				msgRequest.deleteMessageFromFileSystem(); // elimino richiesta salvata precedentemente
				openspcoopstate.setUseConnection(true);
				openspcoopstate.releaseResource();
				if (this.msgContext.isGestioneRisposta()) {
					this.msgContext.setMessageResponse((this.generatoreErrore.build(pddContext,AbstractErrorGenerator.getIntegrationInternalError(pddContext),
							ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.getErroreIntegrazione(),e,null)));
				}
				return false;
			}
	
			// Se la chiamata alla libreria imbustamento non ha dato errori procedo con le altre librerie
			// Altrimenti faccio gestire l'errore prodotto dalla libreria
			if (esito.getStatoInvocazione() != EsitoLib.OK) {
				// ripristino utilizzo connessione al database
				openspcoopstate.setUseConnection(true);
				gestioneRisposta(parametriGestioneRisposta);
				msgDiag.highDebug("Imbustamento stateless (terminato:false)");
				return false;
			}
	
			// Gestione oneway versione 11
			if (openspcoopstate.getDestinatarioResponseMsgLib() != null
					&& openspcoopstate.getDestinatarioResponseMsgLib().startsWith(RicezioneContenutiApplicativi.ID_MODULO)
					&& propertiesReader.isGestioneOnewayStateful_1_1()){
				msgDiag.highDebug("Imbustamento stateless (terminato:true)");
				return true;
			}
	
			/* ------------ Rilascio risorsa se e' presente rinegoziamento delle risorse ------------------ */
			// Rinegozio la connessione SOLO se siamo in oneway o sincrono stateless puro (non oneway11)
			if( rinegoziamentoConnessione ){
				msgDiag.highDebug("Imbustamento stateless (commit) ...");
				openspcoopstate.setUseConnection(true);
				try{
					openspcoopstate.commit();
				}catch(Exception e){}
				openspcoopstate.releaseResource();
				openspcoopstate.setUseConnection(false);
			}
			
			msgDiag.highDebug("Imbustamento stateless terminato");
			
			
			
			
			/*
			 * ---------------------- INOLTRO BUSTE ------------------
			 */
	
			msgDiag.highDebug("InoltroBuste stateless ...");
			InoltroBuste inoltroBusteLib = null;
			try {
				inoltroBusteLib = new InoltroBuste(logCore);
				msgDiag.highDebug("InoltroBuste stateless (invoco) ...");
				esito = inoltroBusteLib.onMessage(openspcoopstate);
				msgDiag.highDebug("InoltroBuste stateless (analizzo esito) ...");
				if(esito.getStatoInvocazione()==EsitoLib.ERRORE_NON_GESTITO){
					if(esito.getErroreNonGestito()!=null)
						throw esito.getErroreNonGestito();
					else
						throw new Exception("Errore non gestito");
				}
			} catch (Throwable e) {
				msgDiag.logErroreGenerico(e,"Stateless.InoltroBuste");
				logCore.error("Errore Generale durante la gestione stateless: "+e.getMessage(),e);
				msgRequest.deleteMessageFromFileSystem(); // elimino richiesta salvata precedentemente
				openspcoopstate.setUseConnection(true);
				openspcoopstate.releaseResource();
				if (this.msgContext.isGestioneRisposta()) {
					this.msgContext.setMessageResponse((this.generatoreErrore.build(pddContext,AbstractErrorGenerator.getIntegrationInternalError(pddContext),
							ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.getErroreIntegrazione(),e,null)));
				}
				return false;
			}
			
			if (esito.getStatoInvocazione() != EsitoLib.OK) {
				// ripristino utilizzo connessione al database
				openspcoopstate.setUseConnection(true);
				gestioneRisposta(parametriGestioneRisposta);
				msgDiag.highDebug("InoltroBuste stateless (terminato:false)");
				return false;
			}
	
			// Gestione oneway versione 11
			if (openspcoopstate.getDestinatarioResponseMsgLib()!=null &&
					openspcoopstate.getDestinatarioResponseMsgLib().startsWith(
					RicezioneContenutiApplicativi.ID_MODULO)){
				msgDiag.highDebug("InoltroBuste stateless (terminato:true)");
				return true;
			}
			
			msgDiag.highDebug("InoltroBuste stateless terminato");
			
			
			
			
	
			/*--------------- SBUSTAMENTO RISPOSTE ---------------- */
	
			msgDiag.highDebug("SbustamentoRisposte stateless ...");
			SbustamentoRisposte sbustamentoRisposteLib = null;
			boolean erroreSbustamentoRisposta = false;
			try {
				sbustamentoRisposteLib = new SbustamentoRisposte(logCore);
				/* Verifico che non abbia rilasciato la connessione, se si la riprendo */
				if( rinegoziamentoConnessione && openspcoopstate.resourceReleased()){
					msgDiag.highDebug("SbustamentoRisposte stateless (initResourceDB) ...");
					openspcoopstate.setUseConnection(true);
					openspcoopstate.initResource(parametriGestioneRisposta.getIdentitaPdD(), SbustamentoRisposte.ID_MODULO, idTransazione);
					openspcoopstate.setUseConnection(false);
					// update states
					registroServiziReader.updateState(openspcoopstate.getStatoRichiesta(),openspcoopstate.getStatoRisposta());
					configurazionePdDReader.updateState(openspcoopstate.getStatoRichiesta(),openspcoopstate.getStatoRisposta());
					msgDiag.updateState(openspcoopstate.getStatoRichiesta(),openspcoopstate.getStatoRisposta());
				}
				msgDiag.highDebug("SbustamentoRisposte stateless (invoco) ...");
				esito = sbustamentoRisposteLib.onMessage(openspcoopstate);
				msgDiag.highDebug("SbustamentoRisposte stateless (analizzo esito) ...");
				if(esito.getStatoInvocazione()==EsitoLib.ERRORE_NON_GESTITO){
					if(esito.getErroreNonGestito()!=null)
						throw esito.getErroreNonGestito();
					else
						throw new Exception("Errore non gestito");
				}
			} catch (Throwable e) {
				erroreSbustamentoRisposta = true;
				msgDiag.logErroreGenerico(e,"Stateless.SbustamentoRisposte");
				logCore.error("Errore Generale durante la gestione stateless: "+e.getMessage(),e);
				msgRequest.deleteMessageFromFileSystem(); // elimino richiesta salvata precedentemente
				openspcoopstate.setUseConnection(true);
				openspcoopstate.releaseResource();
				if (this.msgContext.isGestioneRisposta()) {
					this.msgContext.setMessageResponse((this.generatoreErrore.build(pddContext,AbstractErrorGenerator.getIntegrationInternalError(pddContext),
							ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.getErroreIntegrazione(),e,null)));
				}
				return false;
			}finally{
				/* Se devo rinegoziare la connessione, la rilascio */
				if( (rinegoziamentoConnessione) && (erroreSbustamentoRisposta==false) ){
					msgDiag.highDebug("SbustamentoRisposte stateless (commit) ...");
					openspcoopstate.setUseConnection(true);
					try{
						openspcoopstate.commit();
					}catch(Exception e){}
					openspcoopstate.releaseResource();
					openspcoopstate.setUseConnection(false);
				}
			}
	
			if (esito.getStatoInvocazione() != EsitoLib.OK) {
				// ripristino utilizzo connessione al database
				openspcoopstate.setUseConnection(true);
				gestioneRisposta(parametriGestioneRisposta);
				msgDiag.highDebug("SbustamentoRisposte stateless (terminato:false)");
				return false;
			}

			msgDiag.highDebug("SbustamentoRisposte stateless terminato");
			
		}
		
		

		return true;
	}

	
	
	
	
	
	
	
	
	
	
	/*--------------------------------------- GESTIONE RISPOSTA --------------------------- */

	/* Gestisce le risposte applicative di Ok o di errore */
	private void gestioneRisposta(RicezioneContenutiApplicativiGestioneRisposta parametriGestioneRisposta){
			
			
		/* ------- Lettura parametri ---------- */
		IOpenSPCoopState openspcoopstate = parametriGestioneRisposta.getOpenspcoopstate();
		
		MsgDiagnostico msgDiag = parametriGestioneRisposta.getMsgDiag();
		Logger logCore = parametriGestioneRisposta.getLogCore();
		OpenSPCoop2Properties propertiesReader = parametriGestioneRisposta.getPropertiesReader();
		
		IDSoggetto identitaPdD = parametriGestioneRisposta.getIdentitaPdD();
		String idMessageRequest = parametriGestioneRisposta.getIdMessageRequest();
		
		//ProprietaErroreApplicativo proprietaErroreAppl = parametriGestioneRisposta.getProprietaErroreAppl();
		String servizioApplicativo = parametriGestioneRisposta.getServizioApplicativo();
		
		HeaderIntegrazione headerIntegrazioneRichiesta = parametriGestioneRisposta.getHeaderIntegrazioneRichiesta();
		HeaderIntegrazione headerIntegrazioneRisposta = parametriGestioneRisposta.getHeaderIntegrazioneRisposta();
		String[] tipiIntegrazionePD = parametriGestioneRisposta.getTipiIntegrazionePD();
		
		GestoreMessaggi msgRequest = parametriGestioneRisposta.getMsgRequest();
		//RepositoryBuste repositoryBuste = parametriGestioneRisposta.getRepositoryBuste();
		
		boolean portaStateless = parametriGestioneRisposta.isPortaStateless();
		boolean oneWayVers11 = parametriGestioneRisposta.isOneWayVers11();
		boolean richiestaAsincronaSimmetricaStateless = parametriGestioneRisposta.isRichiestaAsincronaSimmetricaStateless();
		
		PdDContext pddContext = parametriGestioneRisposta.getPddContext();
		String idTransazione = PdDContext.getValue(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE, pddContext);
		RequestInfo requestInfo = (RequestInfo) pddContext.getObject(org.openspcoop2.core.constants.Costanti.REQUEST_INFO);
		
		IProtocolFactory<?> protocolFactory = parametriGestioneRisposta.getProtocolFactory();
		
		Busta bustaRichiesta = parametriGestioneRisposta.getBustaRichiesta();
		
		Loader loader = Loader.getInstance();
		
		boolean errorOccurs_setResponse = false;

		/* ------------ GestioneRisposta ------------- */
		String idMessageResponse = null;
		String idCollaborazioneResponse = null;
		ProfiloDiCollaborazione profiloCollaborazione = null;
		String profiloCollaborazioneValue = null;
		OpenSPCoop2Message responseMessage = null;
		String idCorrelazioneApplicativaRisposta = null;
		//org.openspcoop.pdd.core.TempiAttraversamentoPDD tempiAttraversamentoGestioneMessaggi = null;
		//org.openspcoop.pdd.core.DimensioneMessaggiAttraversamentoPdD dimensioneMessaggiAttraversamentoGestioneMessaggi = null;
		try {

			if (portaStateless || oneWayVers11) {
				
				// Gestione stateless
				
				RicezioneContenutiApplicativiMessage ricezioneContenutiApplicativiMSG = 
					(RicezioneContenutiApplicativiMessage) ((OpenSPCoopStateless) openspcoopstate).getMessageLib();
				idMessageResponse = ricezioneContenutiApplicativiMSG.getIdBustaRisposta();
				idCollaborazioneResponse = ricezioneContenutiApplicativiMSG.getIdCollaborazione();
				profiloCollaborazione = ricezioneContenutiApplicativiMSG.getProfiloCollaborazione();
				profiloCollaborazioneValue = ricezioneContenutiApplicativiMSG.getProfiloCollaborazioneValue();

				responseMessage = ((OpenSPCoopStateless) openspcoopstate).getRispostaMsg();
				
				idCorrelazioneApplicativaRisposta = ((OpenSPCoopStateless) openspcoopstate).getIDCorrelazioneApplicativaRisposta();
				
				if(ProfiloDiCollaborazione.ONEWAY.equals(profiloCollaborazione)==false){
					this.msgContext.getProtocol().setIdRisposta(idMessageResponse);
				}
				this.msgContext.getProtocol().setCollaborazione(idCollaborazioneResponse);

				//tempiAttraversamentoGestioneMessaggi = 
				//	((OpenSPCoopStateless) openspcoopstate).getTempiAttraversamentoPDD();
				//dimensioneMessaggiAttraversamentoGestioneMessaggi = 
				//	((OpenSPCoopStateless) openspcoopstate).getDimensioneMessaggiAttraversamentoPDD();

				// Aggiornamento Informazioni Protocollo
				msgDiag.setIdMessaggioRisposta(idMessageResponse);
				msgDiag.addKeyword(CostantiPdD.KEY_ID_MESSAGGIO_RISPOSTA, idMessageResponse);
			}

			else {

				// Gestione stateful
				
				try {
					responseMessage = MessageUtilities.buildEmptyMessage(OpenSPCoop2MessageFactory.getDefaultMessageFactory(),
							requestInfo.getIntegrationRequestMessageType(), MessageRole.RESPONSE);

					String classType = null;
					INodeReceiver nodeReceiver = null;
					try {
						classType = ClassNameProperties.getInstance().getNodeReceiver(propertiesReader.getNodeReceiver());
						nodeReceiver = (INodeReceiver) loader.newInstance(classType);
					} catch (Exception e) {
						throw new Exception(
								"Riscontrato errore durante il caricamento della classe ["+ classType
										+ "] da utilizzare per la ricezione dall'infrastruttura: "+ e.getMessage());
					}
					
					msgDiag.mediumDebug("Attesa/lettura risposta...");
					RicezioneContenutiApplicativiMessage ricezioneContenutiApplicativiMSG = 
						(RicezioneContenutiApplicativiMessage) nodeReceiver.receive(
								msgDiag, identitaPdD,this.msgContext.getIdModulo(),idMessageRequest,
									propertiesReader.getNodeReceiverTimeoutRicezioneContenutiApplicativi(),
									propertiesReader.getNodeReceiverCheckInterval());
					idMessageResponse = ricezioneContenutiApplicativiMSG.getIdBustaRisposta();
					idCollaborazioneResponse = ricezioneContenutiApplicativiMSG.getIdCollaborazione();
					profiloCollaborazione = ricezioneContenutiApplicativiMSG.getProfiloCollaborazione();
					profiloCollaborazioneValue = ricezioneContenutiApplicativiMSG.getProfiloCollaborazioneValue();

					// aggiorno pddContext
					pddContext = ricezioneContenutiApplicativiMSG.getPddContext();
					if(pddContext!=null){
						Enumeration<String> enumPddContext = pddContext.keys();
						while (enumPddContext.hasMoreElements()) {
							String key = enumPddContext.nextElement();
							//System.out.println("AGGIORNO KEY CONTENTUI ["+key+"]");
							this.msgContext.getPddContext().addObject(key, pddContext.getObject(key));
						}
					}
					
					if(ProfiloDiCollaborazione.ONEWAY.equals(profiloCollaborazione)==false){
						this.msgContext.getProtocol().setIdRisposta(idMessageResponse);
					}
					this.msgContext.getProtocol().setCollaborazione(idCollaborazioneResponse);

				} catch (Exception e) {

					logCore.error("Gestione risposta ("+ this.msgContext.getIdModulo()+ ") con errore", e);
					msgDiag.logErroreGenerico(e,"GestioneRispostaErroreGenerale");
					
					// per la gestione del timeout ho bisogno di una connessione al database
					// In caso di Timeout elimino messaggi di richiesta ancora in processamento.
					if (e instanceof NodeTimeoutException) {

						// Get Connessione al DB
						try {
							openspcoopstate.updateResource(idTransazione);
						} catch (Exception eDB) {
							msgDiag.logErroreGenerico(e,"openspcoopstate.updateResource()");
							this.msgContext.setMessageResponse((this.generatoreErrore.build(pddContext,IntegrationFunctionError.INTERNAL_RESPONSE_ERROR,
									ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
									get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_505_GET_DB_CONNECTION),eDB,
									((responseMessage!=null && responseMessage.getParseException()!=null)?responseMessage.getParseException():null))));
							return;
						}

						// Eliminazione msg di richiesta
						try {
							msgDiag.logPersonalizzato("timeoutRicezioneRisposta");
							msgRequest.aggiornaProprietarioMessaggio(TimerGestoreMessaggi.ID_MODULO);
							openspcoopstate.commit();
						} catch (Exception eDel) {
							msgDiag.logErroreGenerico(eDel,"EliminazioneMessaggioScadutoTimeoutRicezioneRisposta");
						}
						// Rilascio connessione
						openspcoopstate.releaseResource();
					}

					this.msgContext.setMessageResponse((this.generatoreErrore.build(pddContext,IntegrationFunctionError.INTERNAL_RESPONSE_ERROR,
							ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
							get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_513_RECEIVE), e, 
							((responseMessage!=null && responseMessage.getParseException()!=null)?responseMessage.getParseException():null))));
					return;
				}

				// Aggiornamento Informazioni Protocollo
				msgDiag.setIdMessaggioRisposta(idMessageResponse);
				msgDiag.addKeyword(CostantiPdD.KEY_ID_MESSAGGIO_RISPOSTA, idMessageResponse);

				/* ------------ Re-ottengo Connessione al DB -------------- */
				msgDiag.mediumDebug("Richiesta connessione al database per la gestione della risposta...");
				try {
					openspcoopstate.updateResource(idTransazione);
				} catch (Exception e) {
					msgDiag.logErroreGenerico(e,"openspcoopstate.updateResource()");
					this.msgContext.setMessageResponse((this.generatoreErrore.build(pddContext,IntegrationFunctionError.INTERNAL_RESPONSE_ERROR,
							ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
							get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_505_GET_DB_CONNECTION), e,
							((responseMessage!=null && responseMessage.getParseException()!=null)?responseMessage.getParseException():null))));
					return;
				}
				msgRequest.updateOpenSPCoopState(openspcoopstate);

				/*
				 * ------------ Lettura Contenuto Messaggio (mapping in Message) --------------
				 */
				msgDiag.mediumDebug("Lettura messaggio di risposta...");
				GestoreMessaggi msgResponse = new GestoreMessaggi(openspcoopstate, false, idMessageResponse, Costanti.INBOX,msgDiag,this.msgContext.pddContext);
				try {
					responseMessage = msgResponse.getMessage();
					idCorrelazioneApplicativaRisposta = msgResponse.getIDCorrelazioneApplicativaRisposta();
					
					/*tempiAttraversamentoGestioneMessaggi = msgResponse.getTempiAttraversamentoPdD();
					if (tempiAttraversamentoGestioneMessaggi != null
							&& (tempiAttraversamentoGestioneMessaggi.getRicezioneMsgIngresso()==null || tempiAttraversamentoGestioneMessaggi.getSpedizioneMessaggioIngresso()==null)) {
						TempiAttraversamentoPDD dRichiesta = msgRequest.getTempiAttraversamentoPdD();
						if (dRichiesta != null) {
							tempiAttraversamentoGestioneMessaggi.setSpedizioneMessaggioIngresso(dRichiesta.getSpedizioneMessaggioIngresso());
						}
					}
					
					dimensioneMessaggiAttraversamentoGestioneMessaggi = msgResponse.getDimensioneMessaggiAttraversamentoPdD();*/

				} catch (GestoreMessaggiException e) {
					msgDiag.logErroreGenerico(e,"msgResponse.getMessage()");
					openspcoopstate.releaseResource();
					this.msgContext.setMessageResponse((this.generatoreErrore.build(pddContext,IntegrationFunctionError.INTERNAL_RESPONSE_ERROR,
							ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
							get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_511_READ_RESPONSE_MSG), e,
							((responseMessage!=null && responseMessage.getParseException()!=null)?responseMessage.getParseException():null))));
					return;
				}

			}
		} catch (Exception e) {
			logCore.error("ErroreGenerale", e);
			msgDiag.logErroreGenerico(e,"ErroreGenerale");
			openspcoopstate.releaseResource();
			this.msgContext.setMessageResponse((this.generatoreErrore.build(pddContext,IntegrationFunctionError.INTERNAL_RESPONSE_ERROR,
					ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.getErroreIntegrazione(), e,
					((responseMessage!=null && responseMessage.getParseException()!=null)?responseMessage.getParseException():null))
					));
			errorOccurs_setResponse = true;
		}

		
		
		/* ---- Aggiorno informazioni correlazione applicativa risposta ---- */
		msgDiag.setIdCorrelazioneRisposta(idCorrelazioneApplicativaRisposta);
		if(this.msgContext.getProtocol()!=null){
			this.msgContext.getProtocol().setProfiloCollaborazione(profiloCollaborazione, profiloCollaborazioneValue);
		}
		if(this.msgContext.getIntegrazione()!=null)
			this.msgContext.getIntegrazione().setIdCorrelazioneApplicativaRisposta(idCorrelazioneApplicativaRisposta);
			
		
		
		
		/* ----- Header Integrazione ------ */
		msgDiag.mediumDebug("Gestione header di integrazione messaggio di risposta...");
		headerIntegrazioneRisposta.getBusta().setIdCollaborazione(idCollaborazioneResponse);
		headerIntegrazioneRisposta.getBusta().setProfiloDiCollaborazione(profiloCollaborazione);

		// -- REFRESH Impostation Risposta dell'Header Trasporto o se l'invocazione e' stata attiva dall'IntegrationManager --
		// Refresh necessario in seguito alla potenziale impostazione della collaborazione e Profilo di Collaborazione
		// ed eventuali altre future informazioni non subito disponibili
		headerIntegrazioneRisposta.getBusta().setID(idMessageRequest);
		OutResponsePDMessage outResponsePDMessage = new OutResponsePDMessage();
		outResponsePDMessage.setBustaRichiesta(bustaRichiesta);
		Object bustaRispostaObject = pddContext.getObject(CostantiPdD.BUSTA_RISPOSTA);
		if(bustaRispostaObject!=null && bustaRispostaObject instanceof Busta){
			Busta bustaRisposta = (Busta) bustaRispostaObject;
			// aggiungo proprieta' (vengono serializzate negli header di integrazione)
			if(bustaRisposta.sizeProperties()>0){
				String[]propertyNames = bustaRisposta.getPropertiesNames();
				for (int i = 0; i < propertyNames.length; i++) {
					outResponsePDMessage.getBustaRichiesta().addProperty(propertyNames[i], bustaRisposta.getProperty(propertyNames[i]));
				}
			}
		}
		outResponsePDMessage.setMessage(responseMessage);
		outResponsePDMessage.setPortaDelegata(parametriGestioneRisposta.getPortaDelegata());
		Map<String, String> propertiesIntegrazioneRisposta = new HashMap<String, String>();
		outResponsePDMessage.setProprietaTrasporto(propertiesIntegrazioneRisposta);
		outResponsePDMessage.setServizio(parametriGestioneRisposta.getIdServizio());
		outResponsePDMessage.setSoggettoMittente(parametriGestioneRisposta.getSoggettoMittente());
	/*	if (RicezioneContenutiApplicativi.gestoriIntegrazionePD.containsKey(CostantiConfigurazione.HEADER_INTEGRAZIONE_TRASPORTO)
				|| this.msgContext.getIdModulo().startsWith(RicezioneContenutiApplicativi.ID_MODULO+ IntegrationManagerInterface.ID_MODULO)) {*/
		if (this.msgContext.getIdModulo().startsWith(RicezioneContenutiApplicativi.ID_MODULO+ IntegrationManager.ID_MODULO)) {
			try {
				//IGestoreIntegrazionePD gestore = RicezioneContenutiApplicativi.gestoriIntegrazionePD.get(CostantiConfigurazione.HEADER_INTEGRAZIONE_TRASPORTO);
				
				String classType = null;
				IGestoreIntegrazionePD gestore = null;
				try {
					classType = ClassNameProperties.getInstance().getIntegrazionePortaDelegata(CostantiConfigurazione.HEADER_INTEGRAZIONE_TRASPORTO);
					gestore = (IGestoreIntegrazionePD) loader.newInstance(classType);
					AbstractCore.init(gestore, pddContext, protocolFactory);
				} catch (Exception e) {
					throw new Exception(
							"Riscontrato errore durante il caricamento della classe ["+ classType
									+ "] da utilizzare per la gestione dell'integrazione di tipo (Risposta IM) ["+ CostantiConfigurazione.HEADER_INTEGRAZIONE_TRASPORTO + "]: " + e.getMessage());
				}
				
				if (gestore != null) {
					gestore.setOutResponseHeader(headerIntegrazioneRisposta, outResponsePDMessage);
				}
			} catch (Exception e) {
				msgDiag.logErroreGenerico(e,"setHeaderIntegrazioneRisposta");
			}
		}

		// HeaderIntegrazione
		for (int i = 0; i < tipiIntegrazionePD.length; i++) {
			try {
				//IGestoreIntegrazionePD gestore = RicezioneContenutiApplicativi.gestoriIntegrazionePD.get(tipiIntegrazionePD[i]);
				
				String classType = null;
				IGestoreIntegrazionePD gestore = null;
				try {
					classType = ClassNameProperties.getInstance().getIntegrazionePortaDelegata(tipiIntegrazionePD[i]);
					gestore = (IGestoreIntegrazionePD) loader.newInstance(classType);
					AbstractCore.init(gestore, pddContext, protocolFactory);
				} catch (Exception e) {
					throw new Exception(
							"Riscontrato errore durante il caricamento della classe ["+ classType
									+ "] da utilizzare per la gestione dell'integrazione (Risposta) di tipo ["+ tipiIntegrazionePD[i] + "]: " + e.getMessage());
				}
				
				if (gestore != null) {
					if(gestore instanceof IGestoreIntegrazionePDSoap){
						if(propertiesReader.processHeaderIntegrazionePDResponse(false)){
							if(propertiesReader.deleteHeaderIntegrazioneResponsePD()){
								if(responseMessage==null){
									responseMessage = MessageUtilities.buildEmptyMessage(OpenSPCoop2MessageFactory.getDefaultMessageFactory(),
											requestInfo.getIntegrationRequestMessageType(), MessageRole.RESPONSE);
									outResponsePDMessage.setMessage(responseMessage);
								}
								gestore.setOutResponseHeader(headerIntegrazioneRisposta,outResponsePDMessage);
							}else{
								// gia effettuato l'update dell'header in InoltroBuste
							}
						}else{
							if(responseMessage==null){
								responseMessage = MessageUtilities.buildEmptyMessage(OpenSPCoop2MessageFactory.getDefaultMessageFactory(),
										requestInfo.getIntegrationRequestMessageType(), MessageRole.RESPONSE);
								outResponsePDMessage.setMessage(responseMessage);
							}
							gestore.setOutResponseHeader(headerIntegrazioneRisposta,outResponsePDMessage);
						}
					}else{
						gestore.setOutResponseHeader(headerIntegrazioneRisposta,outResponsePDMessage);
					}
				} else {
					throw new Exception("Gestore non inizializzato");
				}
			} catch (Exception e) {
				msgDiag.addKeyword(CostantiPdD.KEY_TIPO_HEADER_INTEGRAZIONE,tipiIntegrazionePD[i]);
				msgDiag.addKeywordErroreProcessamento(e);
				msgDiag.logPersonalizzato("headerIntegrazione.creazioneFallita");
				logCore.error(msgDiag.getMessaggio_replaceKeywords("headerIntegrazione.creazioneFallita"), e);
			}
		}

		// Imposto header di trasporto per la risposta
		this.msgContext.setHeaderIntegrazioneRisposta(propertiesIntegrazioneRisposta);
		
		
		
		
		
		
		
		/* ----- Eliminazione SIL (Stateful) ------ */
		
		if (!portaStateless)
			eliminaSIL((OpenSPCoopStateful) openspcoopstate, msgDiag, idMessageRequest, idMessageResponse,
					servizioApplicativo);

		
		
		
		/* ----- Aggiornamento proprietario (Stateless puro, no gestione oneway) ------ */
		if (portaStateless && !oneWayVers11) {
			msgDiag.mediumDebug("Aggiorno proprietario messaggio richiesta ...");
			try {
				/* Lo stateless che non è onewayVersione11 non salva niente su database */
				// A meno che non siamo in asincrono simmetrico richiesta stateless
				// In caso di richiestaAsincronaSimmetrica e openspcoop stateless,
				// Devo comunque salvare le informazioni sul msg della ricevuta alla richiesta.
				// Tali informazioni servono per il check nel modulo RicezioneBuste, per verificare di gestire la risposta
				// solo dopo aver terminato di gestire la richiesta con relativa ricevuta.
				if(richiestaAsincronaSimmetricaStateless){
					boolean resourceReleased = openspcoopstate.resourceReleased();
					if(resourceReleased){
						((OpenSPCoopStateless)openspcoopstate).setUseConnection(true);
						openspcoopstate.updateResource(idTransazione);
					}
					GestoreMessaggi msgResponse = new GestoreMessaggi(openspcoopstate, false, idMessageResponse, Costanti.INBOX,msgDiag,this.msgContext.pddContext);
					msgResponse.setReadyForDrop(true);
					msgResponse.aggiornaProprietarioMessaggio(TimerGestoreMessaggi.ID_MODULO);
					msgResponse.setReadyForDrop(false);
					
					// Devo eliminare anche la richiesta (nel caso in cui non sia arrivata la validazione della ricevuta)
					msgRequest.updateOpenSPCoopState(openspcoopstate);
					msgRequest.setReadyForDrop(true);
					msgRequest.aggiornaProprietarioMessaggio(TimerGestoreMessaggi.ID_MODULO);
					msgRequest.setReadyForDrop(false);
				}
				
				// Committo modifiche (I commit servono per eventuali modifiche ai duplicati)
				openspcoopstate.commit();
				
			} catch (Exception e) {
				logCore.error("Errore durante l'aggiornamento del proprietario al GestoreMessaggi (Stateless)", e);
				msgDiag.logErroreGenerico(e, "openspcoopstate.commit(stateless risposta)");
				openspcoopstate.releaseResource();
				this.msgContext.setMessageResponse((this.generatoreErrore.build(pddContext,IntegrationFunctionError.INTERNAL_RESPONSE_ERROR,
						ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.getErroreIntegrazione(), e,
						((responseMessage!=null && responseMessage.getParseException()!=null)?responseMessage.getParseException():null))));
				errorOccurs_setResponse = true;
			}
		}

		
		
		/* ----- OneWay stateful/stateless ------ */
		
		if (oneWayVers11) {
			msgDiag.mediumDebug("Commit della gestione oneWay stateful/stateless...");
			try {
				// Committo modifiche
				openspcoopstate.commit();
			} catch (Exception e) {
				logCore.error("Riscontrato errore durante il commit della gestione oneWay stateful/stateless", e);
				msgDiag.logErroreGenerico(e, "openspcoopstate.commit(oneway1.1 risposta)");
				openspcoopstate.releaseResource();
				this.msgContext.setMessageResponse((this.generatoreErrore.build(pddContext,IntegrationFunctionError.INTERNAL_RESPONSE_ERROR,
						ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.getErroreIntegrazione(), e,
						((responseMessage!=null && responseMessage.getParseException()!=null)?responseMessage.getParseException():null))));
				errorOccurs_setResponse = true;
			}
		}

		
		
		
		
		/* ----- Terminazione gestione richiesta ------ */
		
		// Rilascio connessione al DB
		msgDiag.mediumDebug("Rilascio connessione al database...");
		openspcoopstate.releaseResource();

		// Risposta
		if (profiloCollaborazione != null) {
			if (profiloCollaborazione.equals(ProfiloDiCollaborazione.SINCRONO)) {
				msgDiag.addKeyword(CostantiPdD.KEY_TIPOLOGIA_RISPOSTA_APPLICATIVA, "risposta sincrona");
				msgDiag.logPersonalizzato("consegnaRispostaApplicativa");
			} else if (profiloCollaborazione.equals(ProfiloDiCollaborazione.ASINCRONO_SIMMETRICO)) {
				if (headerIntegrazioneRichiesta != null
						&& headerIntegrazioneRichiesta.getBusta() != null
						&& headerIntegrazioneRichiesta.getBusta().getRiferimentoMessaggio() != null) {
					msgDiag.addKeyword(CostantiPdD.KEY_TIPOLOGIA_RISPOSTA_APPLICATIVA, "ricevuta di una risposta asincrona simmetrica");
				} else {
					msgDiag.addKeyword(CostantiPdD.KEY_TIPOLOGIA_RISPOSTA_APPLICATIVA, "ricevuta di una richiesta asincrona simmetrica");
				}
				msgDiag.logPersonalizzato("consegnaRispostaApplicativa");
			} else if (profiloCollaborazione.equals(ProfiloDiCollaborazione.ASINCRONO_ASIMMETRICO)) {
				if (headerIntegrazioneRichiesta != null
						&& headerIntegrazioneRichiesta.getBusta() != null
						&& headerIntegrazioneRichiesta.getBusta().getRiferimentoMessaggio() != null) {
					msgDiag.addKeyword(CostantiPdD.KEY_TIPOLOGIA_RISPOSTA_APPLICATIVA, "ricevuta di una risposta asincrona asimmetrica");
				} else {
					msgDiag.addKeyword(CostantiPdD.KEY_TIPOLOGIA_RISPOSTA_APPLICATIVA, "ricevuta di una richiesta asincrona asimmetrica");
				}
				msgDiag.logPersonalizzato("consegnaRispostaApplicativa");
			}
		}

		
		
		
		msgDiag.mediumDebug("Imposto risposta nel context...");
		if(errorOccurs_setResponse==false){
			this.msgContext.setMessageResponse(responseMessage);
		}

	}

	
	
	
	
	/* -------------- UTILITY -------------- */
	
	private void eliminaSIL(OpenSPCoopStateful openspcoopstate,
			MsgDiagnostico msgDiag, String idMessageRequest, String idMessageResponse,
			String servizioApplicativo) {
		// Elimino SIL destinatario a cui ho consegnato il messaggio
		msgDiag.mediumDebug("Eliminazione SIL destinatario del messaggio nella tabelle MSG_SERVIZI_APPLICATIVI...");
		if (openspcoopstate.getConnectionDB() != null) {
			try {
				// GestoreMessaggi gestoreEliminazioneDestinatario = new
				// GestoreMessaggi(openspcoopstate,
				// false,idMessageResponse,Costanti.INBOX,msgDiag);
				GestoreMessaggi gestoreEliminazioneDestinatario = new GestoreMessaggi(openspcoopstate, false, idMessageResponse, Costanti.INBOX,msgDiag,this.msgContext.pddContext);
				gestoreEliminazioneDestinatario.eliminaDestinatarioMessaggio(servizioApplicativo, idMessageRequest);
			} catch (Exception e) {
				msgDiag.logErroreGenerico(e, "gestoreEliminazioneDestinatario.eliminaDestinatarioMessaggio("+servizioApplicativo+")");
			}
		} else {
			msgDiag.logErroreGenerico("Connessione non disponibile", "gestoreEliminazioneDestinatario.eliminaDestinatarioMessaggio("+servizioApplicativo+")");
		}
	}

}
