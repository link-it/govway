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
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;

import org.openspcoop2.core.config.CorsConfigurazione;
import org.openspcoop2.core.config.DumpConfigurazione;
import org.openspcoop2.core.config.GestioneTokenAutenticazione;
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.Proprieta;
import org.openspcoop2.core.config.ResponseCachingConfigurazione;
import org.openspcoop2.core.config.ValidazioneContenutiApplicativi;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import org.openspcoop2.core.config.constants.TipoGestioneCORS;
import org.openspcoop2.core.config.driver.DriverConfigurazioneException;
import org.openspcoop2.core.config.driver.DriverConfigurazioneNotFound;
import org.openspcoop2.core.constants.TipoPdD;
import org.openspcoop2.core.eccezione.details.DettaglioEccezione;
import org.openspcoop2.core.eccezione.details.utils.XMLUtils;
import org.openspcoop2.core.id.IDPortaApplicativa;
import org.openspcoop2.core.id.IDPortaDelegata;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.id.IdentificativiErogazione;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.Resource;
import org.openspcoop2.core.registry.Soggetto;
import org.openspcoop2.core.registry.constants.CostantiRegistroServizi;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziAzioneNotFound;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziException;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziNotFound;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.core.registry.driver.IDServizioFactory;
import org.openspcoop2.core.transazioni.utils.CredenzialiMittente;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.OpenSPCoop2MessageFactory;
import org.openspcoop2.message.config.ServiceBindingConfiguration;
import org.openspcoop2.message.constants.MessageRole;
import org.openspcoop2.message.constants.ServiceBinding;
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
import org.openspcoop2.pdd.core.EJBUtils;
import org.openspcoop2.pdd.core.GestoreCorrelazioneApplicativa;
import org.openspcoop2.pdd.core.GestoreMessaggi;
import org.openspcoop2.pdd.core.IntegrationContext;
import org.openspcoop2.pdd.core.MTOMProcessor;
import org.openspcoop2.pdd.core.PdDContext;
import org.openspcoop2.pdd.core.ProtocolContext;
import org.openspcoop2.pdd.core.StatoServiziPdD;
import org.openspcoop2.pdd.core.ValidatoreMessaggiApplicativi;
import org.openspcoop2.pdd.core.ValidatoreMessaggiApplicativiException;
import org.openspcoop2.pdd.core.ValidatoreMessaggiApplicativiRest;
import org.openspcoop2.pdd.core.autenticazione.GestoreAutenticazione;
import org.openspcoop2.pdd.core.autenticazione.ParametriAutenticazione;
import org.openspcoop2.pdd.core.autenticazione.pa.EsitoAutenticazionePortaApplicativa;
import org.openspcoop2.pdd.core.autorizzazione.GestoreAutorizzazione;
import org.openspcoop2.pdd.core.autorizzazione.container.AutorizzazioneHttpServletRequest;
import org.openspcoop2.pdd.core.autorizzazione.container.IAutorizzazioneSecurityContainer;
import org.openspcoop2.pdd.core.autorizzazione.pa.DatiInvocazionePortaApplicativa;
import org.openspcoop2.pdd.core.autorizzazione.pa.EsitoAutorizzazionePortaApplicativa;
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
import org.openspcoop2.pdd.core.integrazione.HeaderIntegrazioneBusta;
import org.openspcoop2.pdd.core.integrazione.IGestoreIntegrazionePA;
import org.openspcoop2.pdd.core.integrazione.IGestoreIntegrazionePASoap;
import org.openspcoop2.pdd.core.integrazione.InRequestPAMessage;
import org.openspcoop2.pdd.core.integrazione.OutResponsePAMessage;
import org.openspcoop2.pdd.core.integrazione.UtilitiesIntegrazione;
import org.openspcoop2.pdd.core.node.INodeReceiver;
import org.openspcoop2.pdd.core.node.INodeSender;
import org.openspcoop2.pdd.core.node.NodeTimeoutException;
import org.openspcoop2.pdd.core.response_caching.HashGenerator;
import org.openspcoop2.pdd.core.state.IOpenSPCoopState;
import org.openspcoop2.pdd.core.state.OpenSPCoopState;
import org.openspcoop2.pdd.core.state.OpenSPCoopStateException;
import org.openspcoop2.pdd.core.state.OpenSPCoopStateful;
import org.openspcoop2.pdd.core.state.OpenSPCoopStateless;
import org.openspcoop2.pdd.core.token.GestoreToken;
import org.openspcoop2.pdd.core.token.InformazioniToken;
import org.openspcoop2.pdd.core.token.PolicyGestioneToken;
import org.openspcoop2.pdd.core.token.pa.EsitoGestioneTokenPortaApplicativa;
import org.openspcoop2.pdd.core.token.pa.EsitoPresenzaTokenPortaApplicativa;
import org.openspcoop2.pdd.core.token.pa.GestioneToken;
import org.openspcoop2.pdd.core.transazioni.Transaction;
import org.openspcoop2.pdd.core.transazioni.TransactionContext;
import org.openspcoop2.pdd.logger.Dump;
import org.openspcoop2.pdd.logger.MsgDiagnosticiProperties;
import org.openspcoop2.pdd.logger.MsgDiagnostico;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.pdd.logger.Tracciamento;
import org.openspcoop2.pdd.mdb.ConsegnaContenutiApplicativi;
import org.openspcoop2.pdd.mdb.EsitoLib;
import org.openspcoop2.pdd.mdb.GenericLibException;
import org.openspcoop2.pdd.mdb.ImbustamentoRisposte;
import org.openspcoop2.pdd.mdb.InoltroBuste;
import org.openspcoop2.pdd.mdb.InoltroBusteMessage;
import org.openspcoop2.pdd.mdb.InoltroRisposte;
import org.openspcoop2.pdd.mdb.Sbustamento;
import org.openspcoop2.pdd.mdb.SbustamentoMessage;
import org.openspcoop2.pdd.services.DirectVMProtocolInfo;
import org.openspcoop2.pdd.services.OpenSPCoop2Startup;
import org.openspcoop2.pdd.services.ServicesUtils;
import org.openspcoop2.pdd.services.connector.messages.ConnectorInMessage;
import org.openspcoop2.pdd.services.error.AbstractErrorGenerator;
import org.openspcoop2.pdd.services.error.RicezioneBusteExternalErrorGenerator;
import org.openspcoop2.pdd.services.service.RicezioneBusteServiceUtils;
import org.openspcoop2.pdd.services.skeleton.IntegrationManager;
import org.openspcoop2.pdd.timers.TimerGestoreMessaggi;
import org.openspcoop2.pdd.timers.TimerLock;
import org.openspcoop2.pdd.timers.TimerMonitoraggioRisorseThread;
import org.openspcoop2.pdd.timers.TimerThresholdThread;
import org.openspcoop2.pdd.timers.TipoLock;
import org.openspcoop2.protocol.basic.registry.IdentificazionePortaApplicativa;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.engine.RequestInfo;
import org.openspcoop2.protocol.engine.URLProtocolContext;
import org.openspcoop2.protocol.engine.builder.Imbustamento;
import org.openspcoop2.protocol.engine.constants.Costanti;
import org.openspcoop2.protocol.engine.driver.History;
import org.openspcoop2.protocol.engine.driver.IFiltroDuplicati;
import org.openspcoop2.protocol.engine.driver.ProfiloDiCollaborazione;
import org.openspcoop2.protocol.engine.driver.RepositoryBuste;
import org.openspcoop2.protocol.engine.mapping.IdentificazioneDinamicaException;
import org.openspcoop2.protocol.engine.mapping.InformazioniServizioURLMapping;
import org.openspcoop2.protocol.engine.validator.Validatore;
import org.openspcoop2.protocol.registry.RegistroServiziManager;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.BustaRawContent;
import org.openspcoop2.protocol.sdk.Context;
import org.openspcoop2.protocol.sdk.Eccezione;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.Integrazione;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.ProtocolMessage;
import org.openspcoop2.protocol.sdk.Riscontro;
import org.openspcoop2.protocol.sdk.SecurityInfo;
import org.openspcoop2.protocol.sdk.Servizio;
import org.openspcoop2.protocol.sdk.Trasmissione;
import org.openspcoop2.protocol.sdk.builder.ProprietaManifestAttachments;
import org.openspcoop2.protocol.sdk.config.IProtocolConfiguration;
import org.openspcoop2.protocol.sdk.config.IProtocolManager;
import org.openspcoop2.protocol.sdk.config.IProtocolVersionManager;
import org.openspcoop2.protocol.sdk.config.ITraduttore;
import org.openspcoop2.protocol.sdk.constants.CodiceErroreCooperazione;
import org.openspcoop2.protocol.sdk.constants.CodiceErroreIntegrazione;
import org.openspcoop2.protocol.sdk.constants.ErroreCooperazione;
import org.openspcoop2.protocol.sdk.constants.ErroreIntegrazione;
import org.openspcoop2.protocol.sdk.constants.ErroriCooperazione;
import org.openspcoop2.protocol.sdk.constants.ErroriIntegrazione;
import org.openspcoop2.protocol.sdk.constants.FaseImbustamento;
import org.openspcoop2.protocol.sdk.constants.FaseSbustamento;
import org.openspcoop2.protocol.sdk.constants.FunzionalitaProtocollo;
import org.openspcoop2.protocol.sdk.constants.Inoltro;
import org.openspcoop2.protocol.sdk.constants.IntegrationFunctionError;
import org.openspcoop2.protocol.sdk.constants.LivelloRilevanza;
import org.openspcoop2.protocol.sdk.constants.RuoloBusta;
import org.openspcoop2.protocol.sdk.constants.RuoloMessaggio;
import org.openspcoop2.protocol.sdk.constants.StatoFunzionalitaProtocollo;
import org.openspcoop2.protocol.sdk.constants.TipoOraRegistrazione;
import org.openspcoop2.protocol.sdk.dump.DumpException;
import org.openspcoop2.protocol.sdk.state.StateMessage;
import org.openspcoop2.protocol.sdk.state.StatelessMessage;
import org.openspcoop2.protocol.sdk.tracciamento.EsitoElaborazioneMessaggioTracciato;
import org.openspcoop2.protocol.sdk.tracciamento.TracciamentoException;
import org.openspcoop2.protocol.sdk.validator.IValidatoreErrori;
import org.openspcoop2.protocol.sdk.validator.IValidazioneSemantica;
import org.openspcoop2.protocol.sdk.validator.ProprietaValidazione;
import org.openspcoop2.protocol.sdk.validator.ProprietaValidazioneErrori;
import org.openspcoop2.security.message.MessageSecurityContext;
import org.openspcoop2.security.message.MessageSecurityContextParameters;
import org.openspcoop2.security.message.constants.SecurityConstants;
import org.openspcoop2.security.message.engine.MessageSecurityFactory;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.resources.Loader;
import org.openspcoop2.utils.transport.Credential;
import org.openspcoop2.utils.transport.http.CORSRequestType;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.slf4j.Logger;


/**
 * Contiene la definizione del modulo 'RicezioneBuste', il quale e' un
 * modulo dell'infrastruttura OpenSPCoop.
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class RicezioneBuste {

	/** Variabile che indica il Nome del modulo dell'architettura di OpenSPCoop rappresentato da questa classe */
	public final static String ID_MODULO = "RicezioneBuste";

	/** Indicazione se sono state inizializzate le variabili del servizio */
	public static boolean initializeService = false;

	/** IGestoreIntegrazionePA: lista di gestori, ordinati per priorita' minore */
	public static String[] defaultGestoriIntegrazionePA = null;
	public static java.util.concurrent.ConcurrentHashMap<String, String[]> defaultPerProtocolloGestoreIntegrazionePA = null;
	
	/** IGestoreCredenziali: lista di gestori delle credenziali */
	private static String [] tipiGestoriCredenziali = null;
	
	/**
	 * Inizializzatore del servizio RicezioneBuste
	 * 
	 * @throws Exception
	 */
	public synchronized static void initializeService(ConfigurazionePdDManager configReader,ClassNameProperties className,OpenSPCoop2Properties propertiesReader,Logger logCore) throws Exception{
		if(RicezioneBuste.initializeService)
			return; // inizializzato da un altro thread

		Loader loader = Loader.getInstance();
		
		// Inizializzazione NodeSender
		String classTypeNodeSender = className.getNodeSender(propertiesReader.getNodeSender());
		try{
			INodeSender nodeSender = (INodeSender) loader.newInstance(classTypeNodeSender);
			nodeSender.toString();
			logCore.info("Inizializzazione gestore NodeSender di tipo "+classTypeNodeSender+" effettuata.");
		}catch(Exception e){
			throw new Exception("Riscontrato errore durante il caricamento della classe ["+classTypeNodeSender+
					"] da utilizzare per la spedizione nell'infrastruttura: "+e.getMessage());
		}

		// Inizializzazione NodeReceiver
		String classType = className.getNodeReceiver(propertiesReader.getNodeReceiver());
		try{
			INodeReceiver nodeReceiver = (INodeReceiver) loader.newInstance(classType);
			nodeReceiver.toString();
			logCore.info("Inizializzazione gestore NodeReceiver di tipo "+classType+" effettuata.");
		}catch(Exception e){
			throw new Exception("Riscontrato errore durante il caricamento della classe ["+classType+
					"] da utilizzare per la ricezione dall'infrastruttura: "+e.getMessage());
		}

		// Inizializzo IGestoreIntegrazionePA list
		String [] tipiIntegrazioneDefault = propertiesReader.getTipoIntegrazionePA();
		//List<IGestoreIntegrazionePA> v = new ArrayList<IGestoreIntegrazionePA>();
		List<String> s = new ArrayList<String>();
		for (int i = 0; i < tipiIntegrazioneDefault.length; i++) {
			classType = className.getIntegrazionePortaApplicativa(tipiIntegrazioneDefault[i]);
			try {
				IGestoreIntegrazionePA test = (IGestoreIntegrazionePA)loader.newInstance(classType);
				test.toString();
				//v.add(((IGestoreIntegrazionePA)test));
				s.add(tipiIntegrazioneDefault[i]);
				logCore	.info("Inizializzazione gestore per lettura integrazione PA di tipo "
						+ tipiIntegrazioneDefault[i]	+ " effettuata.");
			} catch (Exception e) {
				throw new Exception(
						"Riscontrato errore durante il caricamento della classe ["+ classType
						+ "] da utilizzare per la gestione dell'integrazione di tipo ["
						+ tipiIntegrazioneDefault[i]+ "]: " + e.getMessage());
			}
		}
		if(s.size()>0){
			RicezioneBuste.defaultGestoriIntegrazionePA = s.toArray(new String[1]);
//			RicezioneBuste.gestoriIntegrazionePA = new java.util.concurrent.ConcurrentHashMap<String, IGestoreIntegrazionePA>();
//			while(s.size()>0){
//				RicezioneBuste.gestoriIntegrazionePA.put(s.remove(0), v.remove(0));
//			}
		}
		
		// Inizializzo IGestoreIntegrazionePA per protocollo
		RicezioneBuste.defaultPerProtocolloGestoreIntegrazionePA = new java.util.concurrent.ConcurrentHashMap<String, String[]>();
		Enumeration<String> enumProtocols = ProtocolFactoryManager.getInstance().getProtocolNames();
		while (enumProtocols.hasMoreElements()) {
			String protocol = (String) enumProtocols.nextElement();
			String [] tipiIntegrazionePA = propertiesReader.getTipoIntegrazionePA(protocol);
			if(tipiIntegrazionePA!=null && tipiIntegrazionePA.length>0){
				List<String> tipiIntegrazionePerProtocollo = new ArrayList<String>();
				for (int i = 0; i < tipiIntegrazionePA.length; i++) {
					classType = className.getIntegrazionePortaApplicativa(tipiIntegrazionePA[i]);
					try {
						IGestoreIntegrazionePA test = (IGestoreIntegrazionePA)loader.newInstance(classType);
						test.toString();
						tipiIntegrazionePerProtocollo.add(tipiIntegrazionePA[i]);
						logCore	.info("Inizializzazione gestore per lettura integrazione PA di tipo "
								+ tipiIntegrazionePA[i]	+ " effettuata.");
					} catch (Exception e) {
						throw new Exception(
								"Riscontrato errore durante il caricamento della classe ["+ classType
								+ "] da utilizzare per la gestione dell'integrazione di tipo ["
								+ tipiIntegrazionePA[i]+ "]: " + e.getMessage());
					}
				}
				if(tipiIntegrazionePerProtocollo.size()>0){
					RicezioneBuste.defaultPerProtocolloGestoreIntegrazionePA.put(protocol, tipiIntegrazionePerProtocollo.toArray(new String[1]));
				}
			}
		}
		
		// Inizializzo GestoriCredenziali PA
		RicezioneBuste.tipiGestoriCredenziali = propertiesReader.getTipoGestoreCredenzialiPA();
		if(RicezioneBuste.tipiGestoriCredenziali!=null){
			for (int i = 0; i < RicezioneBuste.tipiGestoriCredenziali.length; i++) {
				classType = className.getGestoreCredenziali(RicezioneBuste.tipiGestoriCredenziali[i]);
				try {
					IGestoreCredenziali gestore = (IGestoreCredenziali)loader.newInstance(classType);
					gestore.toString();
					logCore	.info("Inizializzazione gestore credenziali di tipo "
							+ RicezioneBuste.tipiGestoriCredenziali[i]	+ " effettuata.");
				} catch (Exception e) {
					throw new Exception(
							"Riscontrato errore durante il caricamento della classe ["+ classType
							+ "] da utilizzare per la gestione delle credenziali di tipo ["
							+ RicezioneBuste.tipiGestoriCredenziali[i]+ "]: " + e.getMessage());
				}
			}
		}

		RicezioneBuste.initializeService = true;
	}


	/** Generatore Errori */
	private RicezioneBusteExternalErrorGenerator generatoreErrore;
	
	/** Contesto della richiesta */
	private RicezioneBusteContext msgContext;
	
	public RicezioneBuste(RicezioneBusteContext context,
			RicezioneBusteExternalErrorGenerator generatoreErrore){
		this.msgContext = context;
		this.generatoreErrore = generatoreErrore;
	}


	public void process(Object ... params){

		
		

		// ------------- dati generali -----------------------------

		// Context
		PdDContext context = this.msgContext.getPddContext();
		
		// Logger
		Logger logCore = OpenSPCoop2Logger.getLoggerOpenSPCoopCore();
		if(logCore==null){
			logCore = LoggerWrapperFactory.getLogger(RicezioneBuste.ID_MODULO);
		}
	
		// MsgDiagnostico
		MsgDiagnostico msgDiag = this.msgContext.getMsgDiagnostico();
		
		// Messaggio
		OpenSPCoop2Message requestMessage = this.msgContext.getMessageRequest();
		if(requestMessage==null){
			setSOAPFault_processamento(IntegrationFunctionError.INTERNAL_REQUEST_ERROR, logCore, msgDiag, new Exception("Request message is null"), "LetturaMessaggioRichiesta");
		}

		
		
		
		
		
		// ------------- in-handler -----------------------------
		
		IProtocolFactory<?> protocolFactory = null;
		try{
			protocolFactory = ProtocolFactoryManager.getInstance().getProtocolFactoryByName((String)context.getObject(org.openspcoop2.core.constants.Costanti.PROTOCOL_NAME));
		}catch(Exception e){
			setSOAPFault_processamento(IntegrationFunctionError.GOVWAY_NOT_INITIALIZED, logCore, msgDiag, e, "ProtocolFactoryInstance");
			return;
		}
		InRequestContext inRequestContext = new InRequestContext(logCore,protocolFactory,null);
		// TipoPorta
		inRequestContext.setTipoPorta(TipoPdD.APPLICATIVA);
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
						setSOAPFault_processamento(IntegrationFunctionError.INTERNAL_REQUEST_ERROR, logCore, msgDiag, e, "AutorizzazioneSecurityContainerInstance");
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
				setSOAPFault_processamento(IntegrationFunctionError.INTERNAL_REQUEST_ERROR, logCore,msgDiag, e, "LetturaSoapAction");
				return;
			}
		}
		connettore.setFromLocation(this.msgContext.getSourceLocation());
		inRequestContext.setConnettore(connettore);
		// Data accettazione richiesta
		inRequestContext.setDataAccettazioneRichiesta(this.msgContext.getDataAccettazioneRichiesta());
		// Data ingresso richiesta
		inRequestContext.setDataElaborazioneMessaggio(this.msgContext.getDataIngressoRichiesta());
		// PddContext
		inRequestContext.setPddContext(context);
		// Dati Messaggio
		inRequestContext.setMessaggio(requestMessage);
		// Invoke handler
		try{
			GestoreHandlers.inRequest(inRequestContext, msgDiag, logCore);
		}catch(HandlerException e){
			setSOAPFault_processamento(IntegrationFunctionError.INTERNAL_REQUEST_ERROR, logCore,msgDiag, e, e.getIdentitaHandler());
			return;
		}catch(Exception e){
			setSOAPFault_processamento(IntegrationFunctionError.INTERNAL_REQUEST_ERROR, logCore,msgDiag, e, "InvocazioneInRequestHandler");
			return;
		}

		
		
		
		
		
		
		// ------------- process -----------------------------
		HashMap<String, Object> internalObjects = new HashMap<>();
		try{
			process_engine(inRequestContext,internalObjects,params);
		} catch(TracciamentoException tracciamentoException){
			setSOAPFault_processamento(AbstractErrorGenerator.getIntegrationInternalError(context), logCore,msgDiag, tracciamentoException, "TracciamentoNonRiuscito");
			return;
		} catch(DumpException e){
			setSOAPFault_processamento(AbstractErrorGenerator.getIntegrationInternalError(context), logCore,msgDiag, e, "DumpNonRiuscito");
			return;
		} catch(ProtocolException protocolException){
			setSOAPFault_processamento(AbstractErrorGenerator.getIntegrationInternalError(context), logCore,msgDiag, protocolException, "ProtocolFactoryNonInstanziata");
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
			setSOAPFault_processamento(AbstractErrorGenerator.getIntegrationInternalError(context), logCore,msgDiag, e, "FinalizeIntegrationContextRicezioneBuste");
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
							IDPortaApplicativa identificativoPortaApplicativa = new IDPortaApplicativa();
							identificativoPortaApplicativa.setNome(urlProtocolContext.getInterfaceName());
							PortaApplicativa portaApplicativa = configurazionePdDReader.getPortaApplicativa_SafeMethod(identificativoPortaApplicativa);
							if(portaApplicativa!=null) {
								DumpConfigurazione dumpConfig = configurazionePdDReader.getDumpConfigurazione(portaApplicativa);
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
				}catch(DumpException dumpException){
					setSOAPFault_processamento(AbstractErrorGenerator.getIntegrationInternalError(context), logCore,msgDiag, dumpException, "DumpNonRiuscito");
					return;
				}catch(Exception e){
					// Se non riesco ad accedere alla configurazione sicuramente gia' nel messaggio di risposta e' presente l'errore di PdD non correttamente inizializzata
				}
			}
		}
		
		
		
		
		
		
		
		
		// ------------- out-handler -----------------------------
		OutResponseContext outResponseContext = new OutResponseContext(logCore,protocolFactory,null);
		// TipoPorta
		outResponseContext.setTipoPorta(this.msgContext.getTipoPorta());
		outResponseContext.setIdModulo(this.msgContext.getIdModulo());
		// DataUscitaMessaggio
		outResponseContext.setDataElaborazioneMessaggio(DateManager.getDate());
		// PddContext
		outResponseContext.setPddContext(inRequestContext.getPddContext());
		// Informazioni busta e di integrazione
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
			setSOAPFault_processamento(AbstractErrorGenerator.getIntegrationInternalError(context), logCore,msgDiag, e, e.getIdentitaHandler());
			return;
		}catch(Exception e){
			setSOAPFault_processamento(AbstractErrorGenerator.getIntegrationInternalError(context), logCore,msgDiag, e, "InvocazioneOutResponseHandler");
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
			setSOAPFault_processamento(AbstractErrorGenerator.getIntegrationInternalError(context), logCore,msgDiag, e, "FineGestioneRicezioneBuste");
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
					ServicesUtils.setGovWayHeaderResponse(propertiesTrasporto, logCore, false, outResponseContext.getPddContext(), this.msgContext.getRequestInfo().getProtocolContext());
					dumpApplicativo.dumpRispostaUscita(msgRisposta, 
							inRequestContext.getConnettore().getUrlProtocolContext(), 
							outResponseContext.getPropertiesRispostaTrasporto());
				}
			}catch(DumpException dumpException){
				setSOAPFault_processamento(AbstractErrorGenerator.getIntegrationInternalError(context), logCore,msgDiag, dumpException, "DumpNonRiuscito");
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
				IDPortaApplicativa identificativoPortaApplicativa = new IDPortaApplicativa();
				identificativoPortaApplicativa.setNome(urlProtocolContext.getInterfaceName());
				PortaApplicativa portaApplicativa = null;
				try {
					portaApplicativa = configurazionePdDReader.getPortaApplicativa_SafeMethod(identificativoPortaApplicativa);
				}catch(Exception e) {}
				if(portaApplicativa!=null) {
					// Aggiorno tutti
					soggettoErogatore = new IDSoggetto(portaApplicativa.getTipoSoggettoProprietario(), portaApplicativa.getNomeSoggettoProprietario());
					if(portaApplicativa.getServizio()!=null) {
						idServizio = IDServizioFactory.getInstance().getIDServizioFromValues(portaApplicativa.getServizio().getTipo(),portaApplicativa.getServizio().getNome(), 
									soggettoErogatore, portaApplicativa.getServizio().getVersione());
					}
					dominio = new IDSoggetto(portaApplicativa.getTipoSoggettoProprietario(), portaApplicativa.getNomeSoggettoProprietario());
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
		if(protocolContext!=null){
			dumpApplicativo = new Dump(dominio,
					this.msgContext.getIdModulo(), 
					idRichiesta, fruitore, idServizio,
					this.msgContext.getTipoPorta(),nomePorta,this.msgContext.getPddContext(),
					null,null,
					dumpConfig);
		}else{
			dumpApplicativo = new Dump(dominio,
					this.msgContext.getIdModulo(),this.msgContext.getTipoPorta(),nomePorta,this.msgContext.getPddContext(),
					null,null,
					dumpConfig);
		}
		return dumpApplicativo;
	}
	
	// processamento quando non sono disponibili le risorse (es. MsgDiagnostico)
	private void setSOAPFault_processamento(IntegrationFunctionError integrationFunctionError, ErroreIntegrazione erroreIntegrazione){
		setSOAPFault_engine(integrationFunctionError, null, null, null, null, erroreIntegrazione, null, false);
	}
	private void setSOAPFault_processamento(IntegrationFunctionError integrationFunctionError, ErroreIntegrazione erroreIntegrazione, Exception e){
		setSOAPFault_engine(integrationFunctionError, null, null, e, null, erroreIntegrazione, null, false);
	}
	// processamento
	private void setSOAPFault_processamento(IntegrationFunctionError integrationFunctionError, Logger logCore, MsgDiagnostico msgDiag, Exception e, String posizione){
		setSOAPFault_engine(integrationFunctionError, logCore, msgDiag, e, null, null, posizione, false);
	}
	private void setSOAPFault_processamento(IntegrationFunctionError integrationFunctionError, Logger logCore, MsgDiagnostico msgDiag, ErroreIntegrazione erroreIntegrazione, Exception e, String posizione){
		setSOAPFault_engine(integrationFunctionError, logCore, msgDiag, e, null, erroreIntegrazione, posizione, false);
	}
	// intestazione
	private void setSOAPFault_intestazione(IntegrationFunctionError integrationFunctionError, ErroreCooperazione erroreCooperazione){
		setSOAPFault_engine(integrationFunctionError, null, null, null, erroreCooperazione, null, null, true);
	}
	@SuppressWarnings("unused")
	private void setSOAPFault_intestazione(IntegrationFunctionError integrationFunctionError, ErroreIntegrazione erroreIntegrazione){
		setSOAPFault_engine(integrationFunctionError, null, null, null, null, erroreIntegrazione, null, true);
	}
	
	private void setSOAPFault_engine(IntegrationFunctionError integrationFunctionError, Logger logCore, MsgDiagnostico msgDiag, Exception e, 
			ErroreCooperazione erroreCooperazione, ErroreIntegrazione erroreIntegrazione, String posizioneErrore, 
			boolean validazione) {
				
		HandlerException he = null;
		if(e!=null && (e instanceof HandlerException)){
			he = (HandlerException) e;
		}
		
		Context context = this.msgContext.getPddContext();
		
		if(msgDiag!=null){
			if(he!=null){
				if(he.isEmettiDiagnostico()){
					msgDiag.logErroreGenerico(e, posizioneErrore);
				}
			}else{
				String descrizioneErrore = null;
				if(erroreCooperazione != null){
					try{
						descrizioneErrore = erroreCooperazione.getDescrizione(this.generatoreErrore.getProtocolFactory());
					}catch(Throwable t){}
				}
				if(descrizioneErrore==null && erroreIntegrazione!=null) {
					try{
						descrizioneErrore = erroreIntegrazione.getDescrizione(this.generatoreErrore.getProtocolFactory());
					}catch(Throwable t){}
				}
				if(descrizioneErrore==null) {
					descrizioneErrore = posizioneErrore;
				}
				msgDiag.logErroreGenerico(descrizioneErrore, posizioneErrore); // nota: non emette informazioni sul core
				if(logCore!=null){
					if(e!=null) {
						logCore.error(descrizioneErrore+": "+e.getMessage(),e);
					}
					else {
						logCore.error(descrizioneErrore);
					}
				}
			}
		}
		else if(logCore!=null){
			if(e!=null) {
				logCore.error(posizioneErrore+": "+e.getMessage(),e);
			}
			else {
				logCore.error(posizioneErrore);
			}
		}
		if (this.msgContext.isGestioneRisposta()) {
			OpenSPCoop2Message messageFault = null;
			if(validazione){
				if(erroreCooperazione != null){
					String descrizioneErrore = null;
					try{
						descrizioneErrore = erroreCooperazione.getDescrizione(this.generatoreErrore.getProtocolFactory());
						messageFault = this.generatoreErrore.buildErroreIntestazione(context, integrationFunctionError, 
								erroreCooperazione.getCodiceErrore(), descrizioneErrore);
					}catch(Exception eP){
						messageFault = this.generatoreErrore.buildFault(eP, context);
					}
				}else{
					messageFault = this.generatoreErrore.buildErroreIntestazione(context,integrationFunctionError, 
							erroreIntegrazione);
				}
			}else{
				if(erroreIntegrazione!=null){
					messageFault = this.generatoreErrore.buildErroreProcessamento(context,integrationFunctionError, 
							erroreIntegrazione,e);
				}
				else if(e!=null){
					IntegrationFunctionError ifError = integrationFunctionError;
					ErroreIntegrazione erroreIntegrazioneGenerato = null;
					if(he!=null){
						erroreIntegrazioneGenerato = he.convertToErroreIntegrazione();
						if(he.getIntegrationFunctionError()!=null) {
							ifError = he.getIntegrationFunctionError();
						}
					}
					if(erroreIntegrazioneGenerato==null) {
						erroreIntegrazioneGenerato = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.get5XX_ErroreProcessamento(posizioneErrore);
					}
					messageFault = this.generatoreErrore.buildErroreProcessamento(context,ifError, 
							erroreIntegrazioneGenerato,e);
					if(he!=null){
						he.customized(messageFault);
					}
				}else{
					messageFault = this.generatoreErrore.buildErroreProcessamento(context,integrationFunctionError, 
							ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.get5XX_ErroreProcessamento(posizioneErrore));
				}
			}
			// Lo imposto sempre, perche' puo' servire nell'handler
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
		
		// Loader
		Loader loader = Loader.getInstance();
		
		// RequestInfo
		RequestInfo requestInfo = this.msgContext.getRequestInfo();
		
		
		/* ------------ Controllo inizializzazione OpenSPCoop  ------------------ */
		if( OpenSPCoop2Startup.initialize == false){
			String msgErrore = "Inizializzazione di OpenSPCoop non correttamente effettuata";
			logCore.error("["+RicezioneBuste.ID_MODULO+"]  "+msgErrore);
			try{
				// provo ad emetter un diagnostico
				if(this.msgContext.getMsgDiagnostico()!=null){
					this.msgContext.getMsgDiagnostico().logErroreGenerico(msgErrore,"InizializzazionePdD");
				}
			}catch(Throwable t){logCore.error("Emissione diagnostico per errore inizializzazione non riuscita: "+t.getMessage(),t);}
			setSOAPFault_processamento(IntegrationFunctionError.GOVWAY_NOT_INITIALIZED,
					ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
					get5XX_ErroreProcessamento(msgErrore,CodiceErroreIntegrazione.CODICE_501_PDD_NON_INIZIALIZZATA));
			return;
		}
		if( TimerMonitoraggioRisorseThread.risorseDisponibili == false){
			String msgErrore = "Risorse di sistema non disponibili: "+ TimerMonitoraggioRisorseThread.risorsaNonDisponibile.getMessage();
			logCore.error("["+RicezioneBuste.ID_MODULO+"]  "+msgErrore,TimerMonitoraggioRisorseThread.risorsaNonDisponibile);
			try{
				// provo ad emetter un diagnostico
				if(this.msgContext.getMsgDiagnostico()!=null){
					this.msgContext.getMsgDiagnostico().logErroreGenerico(msgErrore,"InizializzazioneRisorsePdD");
				}
			}catch(Throwable t){logCore.error("Emissione diagnostico per errore inizializzazione non riuscita: "+t.getMessage(),t);}
			setSOAPFault_processamento(IntegrationFunctionError.GOVWAY_RESOURCES_NOT_AVAILABLE,
					ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
					get5XX_ErroreProcessamento(msgErrore,CodiceErroreIntegrazione.CODICE_532_RISORSE_NON_DISPONIBILI));
			return;
		}
		if( TimerThresholdThread.freeSpace == false){
			String msgErrore = "Non sono disponibili abbastanza risorse per la gestione della richiesta";
			logCore.error("["+RicezioneBuste.ID_MODULO+"]  "+msgErrore);
			try{
				// provo ad emetter un diagnostico
				if(this.msgContext.getMsgDiagnostico()!=null){
					this.msgContext.getMsgDiagnostico().logErroreGenerico(msgErrore,"DisponibilitaRisorsePdD");
				}
			}catch(Throwable t){logCore.error("Emissione diagnostico per errore inizializzazione non riuscita: "+t.getMessage(),t);}
			setSOAPFault_processamento(IntegrationFunctionError.GOVWAY_RESOURCES_NOT_AVAILABLE,
					ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
					get5XX_ErroreProcessamento(msgErrore,CodiceErroreIntegrazione.CODICE_533_RISORSE_DISPONIBILI_LIVELLO_CRITICO));
			return;
		}
		if( Tracciamento.tracciamentoDisponibile == false){
			String msgErrore = "Tracciatura non disponibile: "+ Tracciamento.motivoMalfunzionamentoTracciamento.getMessage();
			logCore.error("["+RicezioneBuste.ID_MODULO+"]  "+msgErrore,Tracciamento.motivoMalfunzionamentoTracciamento);
			try{
				// provo ad emetter un diagnostico
				if(this.msgContext.getMsgDiagnostico()!=null){
					this.msgContext.getMsgDiagnostico().logErroreGenerico(msgErrore,"Tracciamento");
				}
			}catch(Throwable t){logCore.error("Emissione diagnostico per errore inizializzazione non riuscita: "+t.getMessage(),t);}
			setSOAPFault_processamento(IntegrationFunctionError.GOVWAY_RESOURCES_NOT_AVAILABLE,
					ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
					get5XX_ErroreProcessamento(msgErrore,CodiceErroreIntegrazione.CODICE_545_TRACCIATURA_NON_FUNZIONANTE));
			return;
		}
		if( MsgDiagnostico.gestoreDiagnosticaDisponibile == false){
			String msgErrore = "Sistema di diagnostica non disponibile: "+ MsgDiagnostico.motivoMalfunzionamentoDiagnostici.getMessage();
			logCore.error("["+RicezioneBuste.ID_MODULO+"]  "+msgErrore,MsgDiagnostico.motivoMalfunzionamentoDiagnostici);
			try{
				// provo ad emetter un diagnostico lo stesso (molto probabilmente non ci riuscirà essendo proprio la risorsa diagnostica non disponibile)
				if(this.msgContext.getMsgDiagnostico()!=null){
					this.msgContext.getMsgDiagnostico().logErroreGenerico(msgErrore,"Diagnostica");
				}
			}catch(Throwable t){logCore.debug("Emissione diagnostico per errore inizializzazione non riuscita: "+t.getMessage(),t);}
			setSOAPFault_processamento(IntegrationFunctionError.GOVWAY_RESOURCES_NOT_AVAILABLE,
					ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
					get5XX_ErroreProcessamento(msgErrore,CodiceErroreIntegrazione.CODICE_546_DIAGNOSTICA_NON_FUNZIONANTE));
			return;
		}
		if( Dump.sistemaDumpDisponibile == false){
			String msgErrore = "Sistema di dump dei contenuti applicativi non disponibile: "+ Dump.motivoMalfunzionamentoDump.getMessage();
			logCore.error("["+RicezioneBuste.ID_MODULO+"]  "+msgErrore,Dump.motivoMalfunzionamentoDump);
			try{
				// provo ad emetter un diagnostico
				if(this.msgContext.getMsgDiagnostico()!=null){
					this.msgContext.getMsgDiagnostico().logErroreGenerico(msgErrore,"Dump");
				}
			}catch(Throwable t){logCore.error("Emissione diagnostico per errore inizializzazione non riuscita: "+t.getMessage(),t);}
			setSOAPFault_processamento(IntegrationFunctionError.GOVWAY_RESOURCES_NOT_AVAILABLE,
					ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
					get5XX_ErroreProcessamento(msgErrore,CodiceErroreIntegrazione.CODICE_547_DUMP_CONTENUTI_APPLICATIVI_NON_FUNZIONANTE));
			return;
		}
		// Check Configurazione (XML)
		ConfigurazionePdDManager configurazionePdDReader = ConfigurazionePdDManager.getInstance();
		try{
			configurazionePdDReader.verificaConsistenzaConfigurazione();
		}catch(Exception e){
			String msgErrore = "Riscontrato errore durante la verifica della consistenza della configurazione PdD";
			logCore.error("["+RicezioneBuste.ID_MODULO+"]  "+msgErrore,e);
			try{
				// provo ad emetter un diagnostico
				if(this.msgContext.getMsgDiagnostico()!=null){
					this.msgContext.getMsgDiagnostico().logErroreGenerico(msgErrore,"CheckConfigurazionePdD");
				}
			}catch(Throwable t){logCore.error("Emissione diagnostico per errore inizializzazione non riuscita: "+t.getMessage(),t);}
			setSOAPFault_processamento(IntegrationFunctionError.GOVWAY_RESOURCES_NOT_AVAILABLE,
					ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
					get5XX_ErroreProcessamento(msgErrore,CodiceErroreIntegrazione.CODICE_536_CONFIGURAZIONE_NON_DISPONIBILE),e);
			return;
		}
		// Check RegistroServizi (XML)
		RegistroServiziManager registroServiziReader = RegistroServiziManager.getInstance();
		try{
			registroServiziReader.verificaConsistenzaRegistroServizi();
		}catch(Exception e){
			String msgErrore = "Riscontrato errore durante la verifica del registro dei servizi";
			logCore.error("["+ RicezioneBuste.ID_MODULO+ "]  "+msgErrore,e);
			try{
				// provo ad emetter un diagnostico
				if(this.msgContext.getMsgDiagnostico()!=null){
					this.msgContext.getMsgDiagnostico().logErroreGenerico(msgErrore,"CheckRegistroServizi");
				}
			}catch(Throwable t){logCore.error("Emissione diagnostico per errore inizializzazione non riuscita: "+t.getMessage(),t);}
			setSOAPFault_processamento(IntegrationFunctionError.GOVWAY_RESOURCES_NOT_AVAILABLE,
					ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
					get5XX_ErroreProcessamento(msgErrore,CodiceErroreIntegrazione.CODICE_534_REGISTRO_DEI_SERVIZI_NON_DISPONIBILE),e);
			return;
		}





		/* ------------- Inizializzazione Risorse  ------------------- */

		// Credenziali utilizzate nella richiesta
		Credenziali credenziali = this.msgContext.getCredenziali();


		// OpenSPCoop Properties
		OpenSPCoop2Properties propertiesReader = OpenSPCoop2Properties.getInstance();
		
		// Classi registrate
		ClassNameProperties className = ClassNameProperties.getInstance();

		//	IdentificativoPdD
		IDSoggetto identitaPdD = requestInfo.getIdentitaPdD();

		
		// ConnectorInMessage
		ConnectorInMessage connectorInMessage = null;
		if(params!=null){
			for (int i = 0; i < params.length; i++) {
				if(params[i]!=null && (params[i] instanceof ConnectorInMessage) ){
					connectorInMessage = (ConnectorInMessage) params[i];
					break;
				}
			}
		}
		
		// ProtocolFactory
		IProtocolFactory<?> protocolFactory = requestInfo.getProtocolFactory();
		PdDContext pddContext = inRequestContext.getPddContext();
		ITraduttore traduttore = protocolFactory.createTraduttore();
		
		//	Logger dei messaggi diagnostici
		String nomePorta = null;
		if(requestInfo.getProtocolContext().getInterfaceName()!=null){
			nomePorta = requestInfo.getProtocolContext().getInterfaceName();
		}
		else{
			nomePorta = inRequestContext.getConnettore().getUrlProtocolContext().getFunctionParameters() + "_urlInvocazione("+ inRequestContext.getConnettore().getUrlProtocolContext().getUrlInvocazione_formBased() + ")";
		}
		MsgDiagnostico msgDiag = MsgDiagnostico.newInstance(TipoPdD.APPLICATIVA,identitaPdD,this.msgContext.getIdModulo(),nomePorta);
		this.msgContext.setMsgDiagnostico(msgDiag); // aggiorno msg diagnostico
		msgDiag.setPddContext(inRequestContext.getPddContext(), protocolFactory);
		msgDiag.setPrefixMsgPersonalizzati(MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_BUSTE);
			
		// Parametri della porta applicativa invocata
		URLProtocolContext urlProtocolContext = this.msgContext.getUrlProtocolContext();
		
		// setCredenziali
		setCredenziali(credenziali, msgDiag);
		
		// inizializzazione risorse statiche
		try{
			if(RicezioneBuste.initializeService==false){
				msgDiag.mediumDebug("Inizializzazione risorse statiche...");
				RicezioneBuste.initializeService(configurazionePdDReader, className, propertiesReader,logCore);
			}
		}catch(Exception e){
			setSOAPFault_processamento(IntegrationFunctionError.GOVWAY_NOT_INITIALIZED,logCore,msgDiag,
					ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
					get5XX_ErroreProcessamento(e,CodiceErroreIntegrazione.CODICE_501_PDD_NON_INIZIALIZZATA),e,
					"InizializzazioneRisorseServizioRicezioneBuste");
			return;
		}
		
		// Imposto header di risposta
		Map<String, String> headerRisposta = new HashMap<String, String>();
		UtilitiesIntegrazione utilitiesHttpRisposta = UtilitiesIntegrazione.getInstancePAResponse(logCore);
		try{
			utilitiesHttpRisposta.setInfoProductTransportProperties(headerRisposta);
		}catch(Exception e){
			setSOAPFault_processamento(IntegrationFunctionError.INTERNAL_REQUEST_ERROR,logCore,msgDiag,
					ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
					get5XX_ErroreProcessamento(e),e,
					"InizializzazioneHeaderRisposta");
			return;
		}
		this.msgContext.setHeaderIntegrazioneRisposta(headerRisposta);

		// OPENSPCOOPSTATE 
		OpenSPCoopState openspcoopstate = null;
		try{ // finally in fondo, vedi  #try-finally-openspcoopstate#

		// Messaggio di libreria 
		SbustamentoMessage sbustamentoMSG = new SbustamentoMessage();
		InoltroBusteMessage inoltroMSG = new InoltroBusteMessage();

		// Context di risposta
		this.msgContext.setProtocol(new ProtocolContext());
		this.msgContext.getProtocol().setDominio(this.msgContext.getIdentitaPdD());
		this.msgContext.setIntegrazione(new IntegrationContext());
		

		// DBManager 
		msgDiag.mediumDebug("Richiesta connessione al database...");
		try{
			openspcoopstate = new OpenSPCoopStateful();
			openspcoopstate.initResource(identitaPdD, this.msgContext.getIdModulo(),idTransazione);
		}catch(Exception e){
			setSOAPFault_processamento(IntegrationFunctionError.GOVWAY_RESOURCES_NOT_AVAILABLE,logCore,msgDiag,
					ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
					get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_505_GET_DB_CONNECTION),e,
					"initDatabaseResource");
			return;
		}
		
		// Refresh reader
		registroServiziReader.updateState(openspcoopstate.getStatoRichiesta(),openspcoopstate.getStatoRisposta());
		configurazionePdDReader.updateState(openspcoopstate.getStatoRichiesta(),openspcoopstate.getStatoRisposta());
		msgDiag.updateState(openspcoopstate.getStatoRichiesta(),openspcoopstate.getStatoRisposta());

		// Dati precedentemente raccolti
		String servizioApplicativoFruitore = null;
		String correlazioneApplicativa = null;
		if(this.msgContext.isTracciamentoAbilitato()==false){
			// e' gia stata effettuata un'analisi precedentemente
			servizioApplicativoFruitore = this.msgContext.getIdentitaServizioApplicativoFruitore();
			this.msgContext.getIntegrazione().setServizioApplicativoFruitore(servizioApplicativoFruitore);
			correlazioneApplicativa = this.msgContext.getIdCorrelazioneApplicativa();
			msgDiag.addKeyword(CostantiPdD.KEY_SA_FRUITORE, servizioApplicativoFruitore);
			msgDiag.addKeyword(CostantiPdD.KEY_ID_CORRELAZIONE_APPLICATIVA, correlazioneApplicativa);
			this.generatoreErrore.updateInformazioniCooperazione(servizioApplicativoFruitore);
		}

		// Transaction
		Transaction transaction = null;
		try{
			transaction = TransactionContext.getTransaction(idTransazione);
		}catch(Exception e){
			setSOAPFault_processamento(IntegrationFunctionError.INTERNAL_REQUEST_ERROR,logCore,msgDiag,
					ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
					get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_500_ERRORE_INTERNO),e,
					"getTransaction");
			return;
		}
		
		
		
		
		// Messaggio di generazione Errore Protocollo
		RicezioneBusteParametriGenerazioneBustaErrore parametriGenerazioneBustaErrore = new RicezioneBusteParametriGenerazioneBustaErrore();
		parametriGenerazioneBustaErrore.setOpenspcoop(openspcoopstate);
		parametriGenerazioneBustaErrore.setIdentitaPdD(identitaPdD);
		parametriGenerazioneBustaErrore.setMsgDiag(msgDiag);
		parametriGenerazioneBustaErrore.setPropertiesReader(propertiesReader);
		parametriGenerazioneBustaErrore.setLogCore(logCore);
		parametriGenerazioneBustaErrore.setCorrelazioneApplicativa(correlazioneApplicativa);
		parametriGenerazioneBustaErrore.setServizioApplicativoFruitore(servizioApplicativoFruitore);
		parametriGenerazioneBustaErrore.setImplementazionePdDMittente(CostantiRegistroServizi.IMPLEMENTAZIONE_STANDARD);
		parametriGenerazioneBustaErrore.setImplementazionePdDDestinatario(CostantiRegistroServizi.IMPLEMENTAZIONE_STANDARD);
		
		
		// Messaggio di invio Errore Protocollo
		RicezioneBusteParametriInvioBustaErrore parametriInvioBustaErrore = new RicezioneBusteParametriInvioBustaErrore();
		parametriInvioBustaErrore.setOpenspcoop(openspcoopstate);
		parametriInvioBustaErrore.setIdentitaPdD(identitaPdD);
		parametriInvioBustaErrore.setMsgDiag(msgDiag);
		parametriInvioBustaErrore.setPropertiesReader(propertiesReader);
		parametriInvioBustaErrore.setLogCore(logCore);
		parametriInvioBustaErrore.setCorrelazioneApplicativa(correlazioneApplicativa);
		parametriInvioBustaErrore.setServizioApplicativoFruitore(servizioApplicativoFruitore);
		parametriInvioBustaErrore.setImplementazionePdDMittente(CostantiRegistroServizi.IMPLEMENTAZIONE_STANDARD);
		parametriInvioBustaErrore.setImplementazionePdDDestinatario(CostantiRegistroServizi.IMPLEMENTAZIONE_STANDARD);
		
		parametriInvioBustaErrore.setNewConnectionForResponse(false);
		parametriInvioBustaErrore.setUtilizzoIndirizzoTelematico(false);
		parametriInvioBustaErrore.setFunctionAsRouter(false);
		parametriInvioBustaErrore.setOnewayVersione11(false);
		parametriInvioBustaErrore.setPddContext(inRequestContext.getPddContext());
		
		

		
		

		
		
		
		
		
		
		
		
		
		/* ------------ Comprensione IDServizio  ------------- */	
		
		msgDiag.mediumDebug("Comprensione IDServizio ...");
		
		IDServizio idServizio = requestInfo.getIdServizio();
		PortaApplicativa pa = null;
		if(idServizio==null){
			// avviene solamente se abbiamo invocazioni speciali con contextURL
			// provo ad individuarlo con il protocollo
			String idBusta = null;
			String profiloBusta = null;
			try{
				Busta busta = protocolFactory.createValidazioneSintattica(openspcoopstate.getStatoRichiesta()).getBusta_senzaControlli(requestMessage);
				if(busta==null){
					throw new Exception("Protocollo non individuato nel messaggio");
				}
				idBusta = busta.getID();
				profiloBusta = busta.getProfiloDiCollaborazioneValue();
				
				if(busta.getTipoDestinatario()==null){
					throw new Exception("TipoDestinatario non individuato nel messaggio");
				}
				if(busta.getDestinatario()==null){
					throw new Exception("Destinatario non individuato nel messaggio");
				}
				if(busta.getTipoServizio()==null){
					throw new Exception("TipoServizio non individuato nel messaggio");
				}
				if(busta.getServizio()==null){
					throw new Exception("Servizio non individuato nel messaggio");
				}
				if(busta.getVersioneServizio()==null){
					throw new Exception("VersioneServizio non individuato nel messaggio");
				}
				idServizio = IDServizioFactory.getInstance().getIDServizioFromValues(busta.getTipoServizio(), busta.getServizio(), 
						busta.getTipoDestinatario(), busta.getDestinatario(), 
						busta.getVersioneServizio());
				idServizio.getSoggettoErogatore().setCodicePorta(registroServiziReader.getDominio(idServizio.getSoggettoErogatore(), null, protocolFactory));
				idServizio.setAzione(busta.getAzione());
				requestInfo.setIdServizio(idServizio);
				
				List<PortaApplicativa> listPa = configurazionePdDReader.getPorteApplicative(idServizio, false);
//				if(listPa.size()<=0){
//					throw new Exception("Non esiste alcuna porta applicativa indirizzabile tramite il servizio ["+idServizio+"]");
//				}
				// NOTA: la pa potra' essere null nei casi di profili asincroni
				if(listPa.size()>0){
					if(listPa.size()>1)
						throw new Exception("Esiste più di una porta applicativa indirizzabile tramite il servizio ["+idServizio+"]");
					pa = listPa.get(0);
					
					IDPortaApplicativa idPA = new IDPortaApplicativa();
					idPA.setNome(pa.getNome());
					idPA.setIdentificativiErogazione(new IdentificativiErogazione());
					idPA.getIdentificativiErogazione().setIdServizio(idServizio);
					RicezioneBusteServiceUtils.updatePortaApplicativaRequestInfo(requestInfo, logCore, this.generatoreErrore, 
							ServicesUtils.getServiceIdentificationReader(logCore, requestInfo), msgDiag, 
							urlProtocolContext, idPA,
							pddContext);
					//requestInfo.getProtocolContext().setInterfaceName(pa.getNome());
				}
				
			}catch(Exception e){
				
				logCore.debug(e.getMessage(),e); // lascio come debug puo' essere utile
				
//				boolean checkAsSecondaFaseAsincrono = false;
//				try{
//					if(idServizio!=null){
//						IProtocolConfiguration config = protocolFactory.createProtocolConfiguration();
//						if(config.isSupportato(org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.ASINCRONO_ASIMMETRICO) || 
//								config.isSupportato(org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.ASINCRONO_SIMMETRICO)	) {
//							Busta busta = protocolFactory.createValidazioneSintattica().getBusta_senzaControlli(requestMessage);
//							if(busta!=null && busta.getProfiloDiCollaborazione()!=null) {
//								if(org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.ASINCRONO_ASIMMETRICO.equals(busta.getProfiloDiCollaborazione()) ||
//										org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.ASINCRONO_SIMMETRICO.equals(busta.getProfiloDiCollaborazione())
//									) {
//									if(busta.getRiferimentoMessaggio()!=null) {
//										checkAsSecondaFaseAsincrono = true;
//									}
//								}
//							}
//						}
//					}
//				}catch(Exception eAsincronoCheck){
//					logCore.error("Errore durante il controllo della presenza di un profilo asincrono: "+eAsincronoCheck.getMessage(),eAsincronoCheck);
//				}
//				
//				if(checkAsSecondaFaseAsincrono==false) {
				
				ServiceBindingConfiguration bindingConfig = requestInfo.getBindingConfig();
				if(bindingConfig.existsContextUrlMapping()==false){
					IntegrationFunctionError integrationFunctionError = null;
					if(idServizio!=null){
						msgDiag.addKeywords(idServizio);
						if(idServizio.getAzione()==null){
							msgDiag.addKeyword(CostantiPdD.KEY_AZIONE_BUSTA_RICHIESTA, "-");
						}
						msgDiag.addKeyword(CostantiPdD.KEY_ID_MESSAGGIO_RICHIESTA, idBusta!=null ? idBusta : "-");
						msgDiag.addKeyword(CostantiPdD.KEY_PROFILO_COLLABORAZIONE, profiloBusta!=null ? profiloBusta : "-");
						msgDiag.logPersonalizzato(MsgDiagnosticiProperties.MSG_DIAG_SBUSTAMENTO,"portaApplicativaNonEsistente.identificazionePerServizio");
						integrationFunctionError = IntegrationFunctionError.NOT_FOUND;
					}
					else{
						msgDiag.addKeywordErroreProcessamento(e);
						msgDiag.logPersonalizzato(MsgDiagnosticiProperties.MSG_DIAG_SBUSTAMENTO,"portaApplicativaNonEsistente");
						integrationFunctionError = IntegrationFunctionError.API_IN_UNKNOWN;
					}
					
					// passo volutamente null come msgDiag poichè ho generato prima il diagnostico
					setSOAPFault_processamento(integrationFunctionError,logCore,null,
							ErroriIntegrazione.ERRORE_450_PA_INESISTENTE.getErroreIntegrazione(),e,
							"comprensioneIDServizio");
					openspcoopstate.releaseResource();
					return;
				}
			}
		}
		else{
			// L'interface name DEVE essere presente in questo caso
			try{
				IDPortaApplicativa idPA = new IDPortaApplicativa();
				idPA.setNome(requestInfo.getProtocolContext().getInterfaceName());
				pa = configurazionePdDReader.getPortaApplicativa_SafeMethod(idPA);
				// NOTA: la pa potra' essere null nei casi di profili asincroni
			}catch(Exception e){
				setSOAPFault_processamento(IntegrationFunctionError.INTERNAL_REQUEST_ERROR,logCore,msgDiag,
						ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
						get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_536_CONFIGURAZIONE_NON_DISPONIBILE),e,
							"getPA");
				openspcoopstate.releaseResource();
				return;
			}
		}
		if(pa!=null){
			msgDiag.updatePorta(pa.getNome());
		}

		
		
		
		
		
		
		
		/* ------------ Comprensione Router Function  ------------- */	
		
		// PdD Function: router o normale PdD
		boolean functionAsRouter = false;
		boolean soggettoVirtuale = false;
		msgDiag.mediumDebug("Esamina modalita' di ricezione (PdD/Router/SoggettoVirtuale)...");
		boolean existsSoggetto = false;
		try{
			if(idServizio!=null && idServizio.getSoggettoErogatore()!=null) {
				existsSoggetto = configurazionePdDReader.existsSoggetto(idServizio.getSoggettoErogatore());
			}
		}catch(Exception e){
			setSOAPFault_processamento(IntegrationFunctionError.INTERNAL_REQUEST_ERROR,logCore,msgDiag,
					ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
					get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_536_CONFIGURAZIONE_NON_DISPONIBILE),e,
					"existsSoggetto("+idServizio.getSoggettoErogatore().toString()+")");
			openspcoopstate.releaseResource();
			return;
		}
		if(existsSoggetto==false){
			// La PdD non gestisce il soggetto destinatario della busta.
			// Controllo adesso che sia abilitata la funzione di Router per la PdD, altrimenti nel successivo
			// modulo verra' generato un errore di soggetto non gestito.
			msgDiag.mediumDebug("Raccolta identita router...");
			boolean routerFunctionActive = false;
			try{
				routerFunctionActive = configurazionePdDReader.routerFunctionActive();
			}catch(Exception e){
				setSOAPFault_processamento(IntegrationFunctionError.INTERNAL_REQUEST_ERROR,logCore,msgDiag,
						ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
						get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_514_ROUTING_CONFIGURATION_ERROR),e,
						"ErroreCheckFunzionalitaRouterAttiva");
				openspcoopstate.releaseResource();
				return;
			}		
			if(routerFunctionActive){
				functionAsRouter = true;	
				try{
					identitaPdD = configurazionePdDReader.getRouterIdentity(protocolFactory);
				}catch(Exception e){
					setSOAPFault_processamento(IntegrationFunctionError.INTERNAL_REQUEST_ERROR,logCore,msgDiag,
							ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
							get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_514_ROUTING_CONFIGURATION_ERROR),e,
							"ErroreRiconoscimentoIdentitaRouter");
					openspcoopstate.releaseResource();
					return;
				}
			}
			// else: assume identita di default.
		}else{
			// identita assume quella dell'erogatore
			msgDiag.mediumDebug("Raccolta identita porta di dominio...");
			String dominio = null;
			try{
				dominio = configurazionePdDReader.getIdentificativoPorta(idServizio.getSoggettoErogatore(),protocolFactory);
				if(dominio==null){
					throw new Exception("Dominio is null");
				}
			}catch(Exception e){
				setSOAPFault_processamento(IntegrationFunctionError.INTERNAL_REQUEST_ERROR,logCore,msgDiag,
						ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
						get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_536_CONFIGURAZIONE_NON_DISPONIBILE),e,
						"ErroreRiconoscimentoIdentitaPdD");
				openspcoopstate.releaseResource();
				return;
			}
			identitaPdD = new IDSoggetto(idServizio.getSoggettoErogatore().getTipo(),
					idServizio.getSoggettoErogatore().getNome(),dominio);
		}
		if(functionAsRouter){
			this.msgContext.setTipoPorta(TipoPdD.ROUTER);
		}
		
		// Context
		this.msgContext.setIdentitaPdD(identitaPdD);
		// GeneratoreErrore
		this.generatoreErrore.updateDominio(identitaPdD);
		// Raccolta dati Busta Ricevuta
		parametriGenerazioneBustaErrore.setIdentitaPdD(identitaPdD);
		parametriInvioBustaErrore.setIdentitaPdD(identitaPdD);
		parametriInvioBustaErrore.setFunctionAsRouter(functionAsRouter);
		// requestInfo
		requestInfo.setIdentitaPdD(identitaPdD);
		// altri
		msgDiag.setDominio(identitaPdD); // imposto anche il dominio nel msgDiag
		parametriGenerazioneBustaErrore.setMsgDiag(msgDiag);
		parametriInvioBustaErrore.setMsgDiag(msgDiag);
		
		
		
		
		
		
		
		
		
		
		
		/* --------------- Header Integrazione (viene letto solo se tracciamento e' abilitato altrimenti significa che il punto di ingresso ha gia gestito la lettura) --------------- */
		String[] tipiIntegrazionePA = null;
		msgDiag.mediumDebug("Header integrazione...");
		if(propertiesReader.processHeaderIntegrazionePARequest(functionAsRouter)){
			if(functionAsRouter ){
				msgDiag.highDebug("Header integrazione (Default gestori integrazione Router)");
				if(RicezioneBuste.defaultPerProtocolloGestoreIntegrazionePA.containsKey(protocolFactory.getProtocol()))
					tipiIntegrazionePA = RicezioneBuste.defaultPerProtocolloGestoreIntegrazionePA.get(protocolFactory.getProtocol());
				else
					tipiIntegrazionePA = RicezioneBuste.defaultGestoriIntegrazionePA;
			}else{
				msgDiag.highDebug("Header integrazione (Gestori integrazione...)");
				if(pa!=null && this.msgContext.isTracciamentoAbilitato()){
					msgDiag.mediumDebug("Lettura header di integrazione...");
					try {
						tipiIntegrazionePA = configurazionePdDReader.getTipiIntegrazione(pa);
					} catch (Exception e) {
						setSOAPFault_processamento(IntegrationFunctionError.INTERNAL_REQUEST_ERROR,logCore,msgDiag,
								ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
								get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_536_CONFIGURAZIONE_NON_DISPONIBILE),e,
								"configurazionePdDReader.getTipiIntegrazione(pa)");
						openspcoopstate.releaseResource();
						return;
					}
				}
				msgDiag.highDebug("Header integrazione (Gestori integrazione terminato)");
				if (tipiIntegrazionePA == null){
					if(RicezioneBuste.defaultPerProtocolloGestoreIntegrazionePA.containsKey(protocolFactory.getProtocol()))
						tipiIntegrazionePA = RicezioneBuste.defaultPerProtocolloGestoreIntegrazionePA.get(protocolFactory.getProtocol());
					else
						tipiIntegrazionePA = RicezioneBuste.defaultGestoriIntegrazionePA;
				}
			}
		}
		HeaderIntegrazione headerIntegrazioneRichiesta = new HeaderIntegrazione(idTransazione);
		InRequestPAMessage inRequestPAMessage = null;
		if(tipiIntegrazionePA!=null){
			msgDiag.highDebug("Header integrazione (Impostazione ...)");
			inRequestPAMessage = new InRequestPAMessage();
			inRequestPAMessage.setMessage(requestMessage);
			inRequestPAMessage.setUrlProtocolContext(this.msgContext.getUrlProtocolContext());
			inRequestPAMessage.setPortaApplicativa(pa);
			inRequestPAMessage.setServizio(idServizio);
			for (int i = 0; i < tipiIntegrazionePA.length; i++) {
				try {
					
					String classType = null;
					IGestoreIntegrazionePA gestore = null;
					try {
						classType = className.getIntegrazionePortaApplicativa(tipiIntegrazionePA[i]);
						gestore = (IGestoreIntegrazionePA)loader.newInstance(classType);
						AbstractCore.init(gestore, pddContext, protocolFactory);
					} catch (Exception e) {
						throw new Exception(
								"Riscontrato errore durante il caricamento della classe ["+ classType
								+ "] da utilizzare per la gestione dell'integrazione di tipo ["+ tipiIntegrazionePA[i] + "]: " + e.getMessage());
					}
					
					if(gestore==null){
						msgDiag.logErroreGenerico("Gestore ["
								+ tipiIntegrazionePA[i]+ "], per la lettura dell'header di integrazione, non inizializzato",
								"gestoriIntegrazionePASoap.get("+tipiIntegrazionePA[i]+")");
					}
					//else if (gestore instanceof IGestoreIntegrazionePASoap) {
					//	((IGestoreIntegrazionePASoap)gestore).readInRequestHeader(headerIntegrazioneRichiesta, inRequestPAMessage);
					else {
						gestore.readInRequestHeader(headerIntegrazioneRichiesta, inRequestPAMessage);
					}		
				} catch (Exception e) {
					msgDiag.addKeyword(CostantiPdD.KEY_TIPO_HEADER_INTEGRAZIONE,tipiIntegrazionePA[i]);
					msgDiag.addKeywordErroreProcessamento(e);
					msgDiag.logPersonalizzato("headerIntegrazione.letturaFallita");
				}
			}
			if(headerIntegrazioneRichiesta!=null){
				msgDiag.highDebug("Header integrazione (set context...)");
				if(this.msgContext.getIdentitaServizioApplicativoFruitore()!=null)
					servizioApplicativoFruitore = this.msgContext.getIdentitaServizioApplicativoFruitore();
				else
					servizioApplicativoFruitore = headerIntegrazioneRichiesta.getServizioApplicativo();

				parametriGenerazioneBustaErrore.setServizioApplicativoFruitore(servizioApplicativoFruitore);
				parametriInvioBustaErrore.setServizioApplicativoFruitore(servizioApplicativoFruitore);
				this.generatoreErrore.updateInformazioniCooperazione(servizioApplicativoFruitore);
				
				if(this.msgContext.getIdCorrelazioneApplicativa()!=null)
					correlazioneApplicativa = this.msgContext.getIdCorrelazioneApplicativa();
				else
					correlazioneApplicativa = headerIntegrazioneRichiesta.getIdApplicativo();
				parametriGenerazioneBustaErrore.setCorrelazioneApplicativa(correlazioneApplicativa);
				parametriInvioBustaErrore.setCorrelazioneApplicativa(correlazioneApplicativa);
				
				msgDiag.addKeyword(CostantiPdD.KEY_SA_FRUITORE, servizioApplicativoFruitore);
				msgDiag.addKeyword(CostantiPdD.KEY_ID_CORRELAZIONE_APPLICATIVA, correlazioneApplicativa);
				
				this.msgContext.getIntegrazione().setIdCorrelazioneApplicativa(correlazioneApplicativa);
				this.msgContext.getIntegrazione().setServizioApplicativoFruitore(servizioApplicativoFruitore);
				
				msgDiag.highDebug("Header integrazione (set context ok)");
			}
		}
		
		
		
		
		
		
		Utilities.printFreeMemory("RicezioneBuste - Autorizzazione canale ...");
		ConfigurazioneCanaliNodo configurazioneCanaliNodo = null;
		try {
			if(!functionAsRouter) {
				configurazioneCanaliNodo = configurazionePdDReader.getConfigurazioneCanaliNodo();
			}
		} catch (Exception e) {
			setSOAPFault_processamento(IntegrationFunctionError.INTERNAL_REQUEST_ERROR,logCore,msgDiag,
					ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
					get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_536_CONFIGURAZIONE_NON_DISPONIBILE),e,
					"configurazionePdDReader.getConfigurazioneCanaliNodo()");
			openspcoopstate.releaseResource();
			return;
		}
		boolean canaleNonAutorizzato = false;
		try {
			if(configurazioneCanaliNodo!=null && configurazioneCanaliNodo.isEnabled()) {
			
				msgDiag.addKeyword(CostantiPdD.KEY_DETAILS, "");
				msgDiag.logPersonalizzato("autorizzazioneCanale.inCorso");
				
				String canaleApiInvocata = null;
				if(pa!=null) {
					String canalePorta = pa.getCanale();
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
				else {
					// saranno segnalati altri errori dovuti al non riconoscimento della porta
				}
				
			}
		} catch (Exception e) {
			
			String msgErrore = e.getMessage();
			
			msgDiag.addKeyword(CostantiPdD.KEY_ERRORE_PROCESSAMENTO, msgErrore);
			msgDiag.logPersonalizzato("autorizzazioneCanale.fallita");
			
			if(canaleNonAutorizzato) {
				logCore.error(e.getMessage(),e);
				setSOAPFault_intestazione(IntegrationFunctionError.AUTHORIZATION_DENY,
						ErroriCooperazione.AUTORIZZAZIONE_FALLITA.getErroreAutorizzazione(msgErrore, CodiceErroreCooperazione.SICUREZZA_AUTORIZZAZIONE_FALLITA));
			}
			else {
				setSOAPFault_processamento(IntegrationFunctionError.INTERNAL_REQUEST_ERROR,logCore,null,
						ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_536_CONFIGURAZIONE_NON_DISPONIBILE),e,
						"autorizzazioneCanale");
			}
			openspcoopstate.releaseResource();
			return;
		}
		
		
		
		
		
		
		Utilities.printFreeMemory("RicezioneBuste - Raccolta dati Gestione CORS ...");
		// NOTA: i dati CORS sono memorizzati solamente nella porta principale e non in quelle di eventuali azioni delegate.
		//       deve quindi essere recuperata prima di sostituire la pa con una più specifica
		CorsConfigurazione cors = null;
		HttpServletRequest httpServletRequest = null;
		boolean effettuareGestioneCORS = false;
		try {
			if(requestInfo!=null && requestInfo.getProtocolContext()!=null) {
				httpServletRequest = requestInfo.getProtocolContext().getHttpServletRequest();	
			}
			
			if(httpServletRequest!=null && HttpRequestMethod.OPTIONS.name().equalsIgnoreCase(httpServletRequest.getMethod())) {
				if(pa!=null) {
					cors = configurazionePdDReader.getConfigurazioneCORS(pa);
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
			setSOAPFault_processamento(IntegrationFunctionError.INTERNAL_REQUEST_ERROR,logCore,msgDiag,
					ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
					get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_536_CONFIGURAZIONE_NON_DISPONIBILE),e,
					"configurazionePdDReader.getConfigurazioneCORS(pa)");
			openspcoopstate.releaseResource();
			return;
		}
		
		
		
		
		
		
		
		/* ------------ Comprensione Azione  ------------- */	
		
		try{
			if(pa!=null){
				if(requestInfo.getIdServizio()!=null && requestInfo.getIdServizio().getAzione()!=null){
					// gia identificata
					idServizio.setAzione(requestInfo.getIdServizio().getAzione());
				}
				else{
					idServizio.setAzione(configurazionePdDReader.getAzione(pa, urlProtocolContext, requestMessage, 
							headerIntegrazioneRichiesta, this.msgContext.getIdModulo().endsWith(IntegrationManager.ID_MODULO), protocolFactory));
				}
				requestInfo.setIdServizio(idServizio);
			}
		}catch(Exception e){
			
			boolean throwFault = true;
			if(StatoFunzionalita.ABILITATO.equals(cors.getStato()) && this.msgContext.isGestioneRisposta()) {
				throwFault = false;
			}
			if(throwFault) {
			
				IntegrationFunctionError integrationFunctinError = IntegrationFunctionError.INTERNAL_REQUEST_ERROR;
				if(e instanceof IdentificazioneDinamicaException) {
					
					pddContext.addObject(org.openspcoop2.core.constants.Costanti.OPERAZIONE_NON_INDIVIDUATA, "true");
										
					integrationFunctinError = IntegrationFunctionError.OPERATION_UNDEFINED;
				}
				
				setSOAPFault_processamento(integrationFunctinError,logCore,msgDiag,
						ErroriIntegrazione.ERRORE_403_AZIONE_NON_IDENTIFICATA.getErroreIntegrazione(),e,
						"identificazioneDinamicaAzionePortaAplicativa");
				openspcoopstate.releaseResource();
				return;
				
			}
			else {
				effettuareGestioneCORS = true;
			}
			
		}
		
		
		
		
		

		
		
		
		Utilities.printFreeMemory("RicezioneBuste - Identificazione PA specifica per azione del servizio ...");
		
		msgDiag.mediumDebug("Lettura azione associato alla PA invocata...");
		if(idServizio!=null && idServizio.getAzione()!=null && pa!=null) {
			// verifico se esiste una porta applicativa piu' specifica
			IdentificazionePortaApplicativa identificazione = new IdentificazionePortaApplicativa(logCore, protocolFactory, openspcoopstate.getStatoRichiesta(), pa);
			String action = idServizio.getAzione();
			if(identificazione.find(action)) {
				IDPortaApplicativa idPA_action = identificazione.getIDPortaApplicativa(action);
				if(idPA_action!=null) {
					
					requestMessage.addContextProperty(CostantiPdD.NOME_PORTA_INVOCATA, pa.getNome()); // prima di aggiornare la porta applicativa
										
					pa = identificazione.getPortaApplicativa(action);
					msgDiag.addKeyword(CostantiPdD.KEY_PORTA_APPLICATIVA, pa.getNome());
					msgDiag.updatePorta(pa.getNome());
					if(requestMessage.getTransportRequestContext()!=null) {
						requestMessage.getTransportRequestContext().setInterfaceName(pa.getNome());
					}
					
					pddContext.removeObject(org.openspcoop2.core.constants.Costanti.PROPRIETA_CONFIGURAZIONE);
					try {
						Map<String, String> configProperties = configurazionePdDReader.getProprietaConfigurazione(pa);
			            if (configProperties != null && !configProperties.isEmpty()) {
			               pddContext.addObject(org.openspcoop2.core.constants.Costanti.PROPRIETA_CONFIGURAZIONE, configProperties);
			            }
					}catch(Exception e) {
						logCore.error("Errore durante la lettura delle proprietà di configurazione della porta applicativa [" + pa.getNome() + "]: " + e.getMessage(), e);
					}
				}
			}else {
				pddContext.addObject(org.openspcoop2.core.constants.Costanti.API_NON_INDIVIDUATA, "true");
				
				msgDiag.addKeyword(CostantiPdD.KEY_ERRORE_PROCESSAMENTO, identificazione.getErroreIntegrazione().getDescrizione(protocolFactory));
				msgDiag.logPersonalizzato(MsgDiagnosticiProperties.MSG_DIAG_SBUSTAMENTO,"portaApplicativaNonEsistente");	
				
				// passo volutamente null come msgDiag poichè ho generato prima il diagnostico
				IntegrationFunctionError integrationFunctionError = null;
				if(CodiceErroreIntegrazione.CODICE_401_PORTA_INESISTENTE.equals(identificazione.getErroreIntegrazione().getCodiceErrore())){
					integrationFunctionError = IntegrationFunctionError.API_IN_UNKNOWN;
				}else{
					integrationFunctionError = IntegrationFunctionError.INTERNAL_REQUEST_ERROR;
				}
				setSOAPFault_processamento(integrationFunctionError,logCore,null,
						identificazione.getErroreIntegrazione(),null,
						"comprensionePASpecificaPerAzione");
				openspcoopstate.releaseResource();
				return;
			}
		}
		
		
		

		
		
		
		
		
		
		
		
		
		
		// ------------- Dump richiesta-----------------------------
			
		msgDiag.mediumDebug("Dump richiesta ...");
		
		DumpConfigurazione dumpConfig = null;
		try {
			if(pa!=null) {
				dumpConfig = configurazionePdDReader.getDumpConfigurazione(pa);
			}
			else {
				dumpConfig = configurazionePdDReader.getDumpConfigurazione();
			}
			internalObjects.put(CostantiPdD.DUMP_CONFIG, dumpConfig);
		} 
		catch (Exception e) {
			setSOAPFault_processamento(IntegrationFunctionError.INTERNAL_REQUEST_ERROR,logCore,msgDiag,
					ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
					get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_536_CONFIGURAZIONE_NON_DISPONIBILE),e,
					"readDumpConfigurazione");
			openspcoopstate.releaseResource();
			return;
		}
		
		Dump dumpApplicativo = new Dump(identitaPdD,
				this.msgContext.getIdModulo(),  null,
				null, idServizio,
				this.msgContext.getTipoPorta(),msgDiag.getPorta(),inRequestContext.getPddContext(),
				openspcoopstate.getStatoRichiesta(),openspcoopstate.getStatoRisposta(),
				dumpConfig);
		dumpApplicativo.dumpRichiestaIngresso(requestMessage,inRequestContext.getConnettore().getUrlProtocolContext());
		internalObjects.put(CostantiPdD.DUMP_RICHIESTA_EFFETTUATO, true);
		
		
		
		
		
		
		
		
		
		
		
		
		


		/* ------------  
		 * URL Mapping 
		 * (Comprensione dati sul soggetto fruitore e sulle funzionalità di protocollo ed identificativo di protocollo)
		 * In questo punto l'idServizio contiene tutte le informazioni necessarie per accedere al registro se le funzionalità di protocollo sono statiche
		 * Deve contenere anche l'azione, visto che quest'ultima influenza il profili di collaborazione 
		 * ------------- */	
		
		Servizio infoServizio = null;
		IDSoggetto soggettoFruitore = null;
		String id = null;
		boolean generazioneListaTrasmissioni = false;
		InformazioniServizioURLMapping is = null;
		boolean identitaServizioValida = false;
		String nomeRegistroForSearch = null; // qualsiasi registro
		try{
			is = new InformazioniServizioURLMapping(requestMessage,protocolFactory,urlProtocolContext,
					logCore, this.msgContext.getIdModuloAsIDService(),
					propertiesReader.getCustomContexts());
			logCore.debug("InformazioniServizioTramiteURLMapping: "+is.toString());		
			
		
			Credential identity = null;
						
			// Read Identity
			if(is.existsIdentityBasedIdentificationMode()){
				if(connectorInMessage!=null)
					identity = connectorInMessage.getCredential();
			}
			
			// Refresh dati su mittente
			soggettoFruitore = new IDSoggetto();
			IDSoggetto headerIntegrazioneRichiestaSoggettoMittente = null;
			if(headerIntegrazioneRichiesta!=null && headerIntegrazioneRichiesta.getBusta()!=null){
				headerIntegrazioneRichiestaSoggettoMittente = new IDSoggetto(headerIntegrazioneRichiesta.getBusta().getTipoMittente(), 
						headerIntegrazioneRichiesta.getBusta().getMittente());
			}
			is.refreshDati(soggettoFruitore, identity, headerIntegrazioneRichiestaSoggettoMittente);
			
			// Reimposto a null se il refresh non ha trovato dati.
			if(soggettoFruitore.getTipo()==null && soggettoFruitore.getNome()==null){
				soggettoFruitore = null;
			}			
			
			// Aggiorno domini dei soggetti se completamente ricostruiti tramite url mapping differente da plugin based
			if(soggettoFruitore!=null && soggettoFruitore.getTipo()!=null && soggettoFruitore.getNome()!=null){
				try {
					soggettoFruitore.setCodicePorta(registroServiziReader.getDominio(soggettoFruitore, nomeRegistroForSearch, protocolFactory));
				} catch (Exception e) {
					//e.printStackTrace();
				}
			}
			requestInfo.setFruitore(soggettoFruitore);
			
			// Check id
			boolean identitaSoggettoErogatoreValida = idServizio!=null &&
					idServizio.getSoggettoErogatore()!=null && 
					idServizio.getSoggettoErogatore().getTipo()!=null &&
					idServizio.getSoggettoErogatore().getNome()!=null;
			identitaServizioValida = identitaSoggettoErogatoreValida && 
					idServizio!=null && idServizio.getNome()!=null && idServizio.getTipo()!=null && idServizio.getVersione()!=null;
						
			// ID Protocollo
			id = null;
			if(is.isStaticBasedIdentificationMode_IdProtocol()){
				Imbustamento imbustamento = new Imbustamento(logCore, protocolFactory, openspcoopstate.getStatoRichiesta());
				IDSoggetto idSoggetto = null;
				if(identitaSoggettoErogatoreValida) {
					idSoggetto = idServizio.getSoggettoErogatore();
				}
				else {
					idSoggetto = propertiesReader.getIdentitaPortaDefault(protocolFactory.getProtocol());
				}
				id = 
					imbustamento.buildID(idSoggetto, 
							(String) this.msgContext.getPddContext().getObject(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE), 
							propertiesReader.getGestioneSerializableDB_AttesaAttiva(),
							propertiesReader.getGestioneSerializableDB_CheckInterval(),
							RuoloMessaggio.RICHIESTA);
			}
			
			// Lista trasmissioni
			generazioneListaTrasmissioni = is.isGenerateListaTrasmissione();
			
			// InfoServizio (NOTA: lasciare per ultimo)
			if(is.isStaticBasedIdentificationMode_InfoProtocol()){
				if(identitaServizioValida) {
					infoServizio = registroServiziReader.getInfoServizio(soggettoFruitore, idServizio,nomeRegistroForSearch,true, true);
				}
				else {
					infoServizio = new Servizio(); // se l'id servizio non e' valido poi viene segnalato dal motore della validazione
				}
			}
			else{
				infoServizio = new Servizio();
			}
		}
		catch(DriverRegistroServiziAzioneNotFound e){
			
			boolean throwFault = true;
			if(StatoFunzionalita.ABILITATO.equals(cors.getStato()) && this.msgContext.isGestioneRisposta()) {
				throwFault = false;
				if(TipoGestioneCORS.TRASPARENTE.equals(cors.getTipo())) {
					// per poter continuare l'elaborazione ho bisogno dell'id servizio
					try {
						if(is.isStaticBasedIdentificationMode_InfoProtocol()){
							if(identitaServizioValida) {
								infoServizio = registroServiziReader.getInfoServizio(soggettoFruitore, idServizio,nomeRegistroForSearch,true, false);
							}
						}
					}catch(Exception eGetInfoServizio) {
						throwFault = true;
					}
				}
			}
			if(throwFault) {
			
				String azione = "";
				if(idServizio.getAzione()!=null) {
					azione = "(azione:"+ idServizio.getAzione()+ ") ";
				}
				
				pddContext.addObject(org.openspcoop2.core.constants.Costanti.OPERAZIONE_NON_INDIVIDUATA, "true");
				
				setSOAPFault_processamento(IntegrationFunctionError.OPERATION_UNDEFINED,logCore,msgDiag,
						ErroriIntegrazione.ERRORE_423_SERVIZIO_CON_AZIONE_SCORRETTA.
						getErrore423_ServizioConAzioneScorretta(azione+ e.getMessage()),e,
						"readProtocolInfo");
				openspcoopstate.releaseResource();
				return;
				
			}
			else {
				effettuareGestioneCORS = true;
			}
			
		}
		catch(Exception e){
			setSOAPFault_processamento(IntegrationFunctionError.INTERNAL_REQUEST_ERROR,logCore,msgDiag,
					ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
					get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_536_CONFIGURAZIONE_NON_DISPONIBILE),e,
					"readProtocolInfo");
			openspcoopstate.releaseResource();
			return;
		}
		
		
		
		// Gestione CORS
		
		if(!effettuareGestioneCORS) {
			if(pddContext.containsKey(CostantiPdD.CORS_PREFLIGHT_REQUEST_SOAP)) {
				effettuareGestioneCORS = true;
			}
			else {
				// devo verificare se si tratta di una azione matched poichè è stato inserito un tipo http method 'qualsiasi'
				if(propertiesReader.isGestioneCORS_resourceHttpMethodQualsiasi_ricezioneBuste()) {
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
					CORSWrappedHttpServletResponse res = new CORSWrappedHttpServletResponse(true);
					corsFilter.doCORS(httpServletRequest, res, CORSRequestType.PRE_FLIGHT, true);
					if(this.msgContext.getHeaderIntegrazioneRisposta()==null) {
						this.msgContext.setHeaderIntegrazioneRisposta(new HashMap<String, String>());
					}
					this.msgContext.getHeaderIntegrazioneRisposta().putAll(res.getHeader());
					this.msgContext.setMessageResponse(res.buildMessage());
					pddContext.addObject(org.openspcoop2.core.constants.Costanti.CORS_PREFLIGHT_REQUEST_VIA_GATEWAY, "true");
				}catch(Exception e) {
					// un eccezione non dovrebbe succedere
					setSOAPFault_processamento(IntegrationFunctionError.INTERNAL_REQUEST_ERROR,logCore,msgDiag,
							ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
							get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_536_CONFIGURAZIONE_NON_DISPONIBILE),e,
							"gestioneCORS");
					openspcoopstate.releaseResource();
					return;
				}
				
				openspcoopstate.releaseResource();
				return;
					
			}
			else {
				
				pddContext.addObject(org.openspcoop2.core.constants.Costanti.CORS_PREFLIGHT_REQUEST_TRASPARENTE, "true");
				
			}
			
		}
	

		
		
		
		

		
		
		
		/* ------------  Busta con i dati identificati tramite PA e URLMapping ------------- */	
		
		Busta bustaURLMapping = null;
		try{
			// Build Busta
			bustaURLMapping = new Busta(protocolFactory,infoServizio, soggettoFruitore, 
					idServizio!=null ? idServizio.getSoggettoErogatore() : null, 
					id, generazioneListaTrasmissioni);
			if(infoServizio.getCollaborazione()) {
				// in questo punto sar' true solo se infoServizio è stato letto dal registro E siamo in 'isStaticBasedIdentificationMode_InfoProtocol'
				if(headerIntegrazioneRichiesta!=null && headerIntegrazioneRichiesta.getBusta()!=null 
						&& headerIntegrazioneRichiesta.getBusta().getIdCollaborazione()!=null) {
					bustaURLMapping.setCollaborazione(headerIntegrazioneRichiesta.getBusta().getIdCollaborazione());
				}
			}
			if(infoServizio.getIdRiferimentoRichiesta()) {
				// in questo punto sar' true solo se infoServizio è stato letto dal registro E siamo in 'isStaticBasedIdentificationMode_InfoProtocol'
				if(headerIntegrazioneRichiesta!=null && headerIntegrazioneRichiesta.getBusta()!=null 
						&& headerIntegrazioneRichiesta.getBusta().getRiferimentoMessaggio()!=null) {
					bustaURLMapping.setRiferimentoMessaggio(headerIntegrazioneRichiesta.getBusta().getRiferimentoMessaggio());
				}
			}
			TipoOraRegistrazione tipoOraRegistrazione = propertiesReader.getTipoTempoBusta(null);
			bustaURLMapping.setTipoOraRegistrazione(tipoOraRegistrazione, traduttore.toString(tipoOraRegistrazione));
			if(bustaURLMapping.sizeListaTrasmissioni()>0){
				for (Trasmissione trasmissione : bustaURLMapping.getListaTrasmissioni()) {
					trasmissione.setTempo(tipoOraRegistrazione, traduttore.toString(tipoOraRegistrazione));
				}
			}
			bustaURLMapping.setServizioApplicativoFruitore(servizioApplicativoFruitore);
		}catch(Exception e){
			setSOAPFault_processamento(IntegrationFunctionError.INTERNAL_REQUEST_ERROR,logCore,msgDiag,
					ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
					get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_536_CONFIGURAZIONE_NON_DISPONIBILE),e,
					"bustaURLMapping");
			openspcoopstate.releaseResource();
			return;
		}
		
		
		
		
		
		

		
		
		
		
		
		
		
		

		
				
		/* ------------  Processamento Busta Ricevuta ------------- */	
		
		// ValidazioneSintattica
		msgDiag.mediumDebug("Validazione busta ricevuta in corso...");
		ProprietaValidazione properties = new ProprietaValidazione();
		boolean readQualifiedAttribute = propertiesReader.isReadQualifiedAttribute(CostantiRegistroServizi.IMPLEMENTAZIONE_STANDARD);
		
		Validatore validatore = new Validatore(requestMessage,pddContext,properties, openspcoopstate.getStatoRichiesta(),readQualifiedAttribute, protocolFactory);
		
		
		msgDiag.logPersonalizzato("validazioneSintattica");
		if(validatore.validazioneSintattica(bustaURLMapping, true) == false){

			// Provo a reperire il dominio se ho l'informazione sul destinatario valida
			Busta erroreIntestazione = null;
			boolean setDestinatarioDefault = true;
			if( validatore.getBustaErroreHeaderIntestazione()!=null){
				erroreIntestazione = validatore.getBustaErroreHeaderIntestazione();
				
				// Imposto Identificativo Richiesta
				if(erroreIntestazione.getID()!=null){
					msgDiag.setIdMessaggioRichiesta(erroreIntestazione.getID());
					this.msgContext.getProtocol().setIdRichiesta(erroreIntestazione.getID());
				}
				
				IProtocolManager protocolManager = protocolFactory.createProtocolManager();
				if( (protocolManager.getKeywordMittenteSconosciuto().equals(erroreIntestazione.getDestinatario())==false) &&
						(protocolManager.getKeywordTipoMittenteSconosciuto().equals(erroreIntestazione.getTipoDestinatario())==false)
				){
					try{
						String dominioTmp = configurazionePdDReader.getIdentificativoPorta(new IDSoggetto(erroreIntestazione.getTipoDestinatario(),
								erroreIntestazione.getDestinatario()),protocolFactory);
						if(dominioTmp!=null){
							identitaPdD.setCodicePorta(dominioTmp);
							identitaPdD.setTipo(erroreIntestazione.getTipoDestinatario());
							identitaPdD.setNome(erroreIntestazione.getDestinatario());
							setDestinatarioDefault = false;
							parametriGenerazioneBustaErrore.setIdentitaPdD(identitaPdD);
							parametriInvioBustaErrore.setIdentitaPdD(identitaPdD);
						}
					}catch(Exception e){}	
				}
				
				// Imposto i domini corretti, se sono stati impostati dei mittenti e tipi mittenti esistenti
				if(erroreIntestazione.getMittente()!=null && erroreIntestazione.getTipoMittente()!=null){
					try{
						String dominio = registroServiziReader.getDominio(new IDSoggetto(erroreIntestazione.getTipoMittente(), erroreIntestazione.getMittente()), null, protocolFactory);
						if(dominio!=null)
							erroreIntestazione.setIdentificativoPortaMittente(dominio);
					}catch(Exception e){}
				}
				if(erroreIntestazione.getDestinatario()!=null && erroreIntestazione.getTipoDestinatario()!=null){
					try{
						String dominio = registroServiziReader.getDominio(new IDSoggetto(erroreIntestazione.getTipoDestinatario(), erroreIntestazione.getDestinatario()), null, protocolFactory);
						if(dominio!=null)
							erroreIntestazione.setIdentificativoPortaDestinatario(dominio);
					}catch(Exception e){}
				}
			}


			// Provo a tracciare/dumpare la busta di richiesta arrivata malformata
			try{
				if(erroreIntestazione!=null){
					msgDiag.addKeywords(erroreIntestazione,true);
					if(erroreIntestazione.getMittente()!=null || erroreIntestazione.getTipoMittente()!=null){
						msgDiag.logPersonalizzato("ricezioneMessaggio");
					}
					else{
						msgDiag.logPersonalizzato("ricezioneMessaggio.mittenteAnonimo");
					}
				}

				if(this.msgContext.isTracciamentoAbilitato() && erroreIntestazione!=null){
					msgDiag.mediumDebug("Tracciamento busta di richiesta...");

					// Tracciamento richiesta
					Tracciamento tracciamento = new Tracciamento(identitaPdD,
							this.msgContext.getIdModulo(),
							inRequestContext.getPddContext(),
							this.msgContext.getTipoPorta(),msgDiag.getPorta(),
							openspcoopstate.getStatoRichiesta(),openspcoopstate.getStatoRisposta());
					
					erroreIntestazione.setServizioApplicativoFruitore(servizioApplicativoFruitore);
					String dettaglioErrore = null;
					if(erroreIntestazione!=null){
						msgDiag.addKeyword(CostantiPdD.KEY_ECCEZIONI, erroreIntestazione.toStringListaEccezioni(protocolFactory));
						msgDiag.addKeyword(CostantiPdD.KEY_TIPO_VALIDAZIONE_BUSTA, "sintattica");
						dettaglioErrore = msgDiag.getMessaggio_replaceKeywords(MsgDiagnosticiProperties.MSG_DIAG_SBUSTAMENTO,"validazioneNonRiuscita");
					}
					EsitoElaborazioneMessaggioTracciato esitoTraccia = 
							EsitoElaborazioneMessaggioTracciato.getEsitoElaborazioneConErrore(dettaglioErrore);
					
					// Prima di tracciare elimino la lista di eccezioni
					List<Eccezione> eccezioniRiscontrate = erroreIntestazione.cloneListaEccezioni();
					while(erroreIntestazione.sizeListaEccezioni()>0){
						erroreIntestazione.removeEccezione(0);
					}
					
					// Tracciamento Busta Ricevuta
					tracciamento.registraRichiesta(requestMessage,null,validatore.getHeaderProtocollo_senzaControlli(),erroreIntestazione,esitoTraccia,
							Tracciamento.createLocationString(true,this.msgContext.getSourceLocation()),
							correlazioneApplicativa);
					
					// Riaggiungo eccezioni riscontrate per tracciare risposta
					while(eccezioniRiscontrate.size()>0){
						erroreIntestazione.addEccezione(eccezioniRiscontrate.remove(0));
					}
				}
			}catch(Exception e){
				msgDiag.logErroreGenerico(e,"TracciamentoBustaMalformata");
				logCore.error("Riscontrato errore durante il tracciamento della busta malformata ricevuta",e);
			}

			if(erroreIntestazione!=null){
				msgDiag.addKeyword(CostantiPdD.KEY_ECCEZIONI, erroreIntestazione.toStringListaEccezioni(protocolFactory));
				msgDiag.addKeyword(CostantiPdD.KEY_TIPO_VALIDAZIONE_BUSTA, "sintattica");
				msgDiag.logPersonalizzato(MsgDiagnosticiProperties.MSG_DIAG_SBUSTAMENTO,"validazioneNonRiuscita");
				
				if(this.msgContext.isGestioneRisposta()){

					if(setDestinatarioDefault){
						// Non sono riuscito a prendere il dominio del destinatario.
						// Usero' come mittente della busta quella della porta di dominio di default
						erroreIntestazione.setDestinatario(identitaPdD.getNome());
						erroreIntestazione.setTipoDestinatario(identitaPdD.getTipo());
						erroreIntestazione.setIdentificativoPortaDestinatario(identitaPdD.getCodicePorta());
					}

					Tracciamento tracciamento = new Tracciamento(identitaPdD,
							this.msgContext.getIdModulo(),
							inRequestContext.getPddContext(),
							this.msgContext.getTipoPorta(),msgDiag.getPorta(),
							openspcoopstate.getStatoRichiesta(),openspcoopstate.getStatoRisposta());
					
					parametriGenerazioneBustaErrore.setTracciamento(tracciamento);
					parametriGenerazioneBustaErrore.setBusta(erroreIntestazione);
					parametriGenerazioneBustaErrore.setError(erroreIntestazione.cloneListaEccezioni());
					
					IntegrationFunctionError integrationFunctionError = IntegrationFunctionError.BAD_REQUEST;
					if(validatore.getErrore_integrationFunctionError()!=null) {
						integrationFunctionError = validatore.getErrore_integrationFunctionError();
					}
					parametriGenerazioneBustaErrore.setIntegrationFunctionError(integrationFunctionError);
					
					OpenSPCoop2Message errorOpenSPCoopMsg = generaBustaErroreValidazione(parametriGenerazioneBustaErrore);
					
					// Nota: la bustaRichiesta e' stata trasformata da generaErroreValidazione
					
					parametriInvioBustaErrore.setOpenspcoopMsg(errorOpenSPCoopMsg);
					parametriInvioBustaErrore.setBusta(parametriGenerazioneBustaErrore.getBusta());
					sendRispostaBustaErrore(parametriInvioBustaErrore);
				}
			}else{
				
				IntegrationFunctionError integrationFunctionError = IntegrationFunctionError.BAD_REQUEST;
				if(validatore.getErrore_integrationFunctionError()!=null) {
					integrationFunctionError = validatore.getErrore_integrationFunctionError();
				}
				try{
					msgDiag.addKeyword(CostantiPdD.KEY_ECCEZIONI, validatore.getErrore().getDescrizione(protocolFactory));
				}catch(Exception e){
					logCore.error("getDescrizione Error:"+e.getMessage(),e);
				}
				msgDiag.addKeyword(CostantiPdD.KEY_TIPO_VALIDAZIONE_BUSTA, "sintattica");
				msgDiag.logPersonalizzato(MsgDiagnosticiProperties.MSG_DIAG_SBUSTAMENTO,"validazioneNonRiuscita");
				setSOAPFault_intestazione(integrationFunctionError, validatore.getErrore());
			}
			openspcoopstate.releaseResource();
			return;
		}
				
		idServizio = validatore.getIDServizio();
		Busta bustaRichiesta = validatore.getBusta();
		BustaRawContent<?> soapHeaderElement = validatore.getHeaderProtocollo();

		
		
		
		
		
		
		
		/* ------------  Controllo che i dati ottenuti dal validatore ed i dati ottenuti dalla RequestInfo coincidano ------------- */	
		
		
		// Controllo che i dati ottenuti dal validatore ed i dati ottenuti dalla RequestInfo coincidano
		if(requestInfo.getIdServizio()!=null){
			// Altrimenti i dati l'ho letti dalla busta e coincidono per forza
			String oggetto = null;
			String datoBusta = null;
			String datoPA = null;
			String locationPA = requestInfo.getProtocolContext().getInterfaceName();
			if(requestInfo.getIdServizio().getTipo().equals(idServizio.getTipo())==false){
				oggetto = "Tipo del servizio";
				datoBusta = idServizio.getTipo();
				datoPA = requestInfo.getIdServizio().getTipo();
			}
			else if(requestInfo.getIdServizio().getNome().equals(idServizio.getNome())==false){
				oggetto = "Servizio";
				datoBusta = idServizio.getNome();
				datoPA = requestInfo.getIdServizio().getNome();
			}
			else if(requestInfo.getIdServizio().getVersione().intValue() != idServizio.getVersione().intValue()){
				oggetto = "VersioneServizio";
				datoBusta = idServizio.getVersione().intValue()+"";
				datoPA = requestInfo.getIdServizio().getVersione().intValue()+"";
			}
			else if(requestInfo.getIdServizio().getAzione()!=null && requestInfo.getIdServizio().getAzione().equals(idServizio.getAzione())==false){
				oggetto = "Azione";
				datoBusta = idServizio.getAzione();
				datoPA = requestInfo.getIdServizio().getAzione();
			}
			else if(requestInfo.getIdServizio().getSoggettoErogatore().getTipo().equals(idServizio.getSoggettoErogatore().getTipo())==false){
				oggetto = "Tipo del soggetto erogatore";
				datoBusta = idServizio.getSoggettoErogatore().getTipo();
				datoPA = requestInfo.getIdServizio().getSoggettoErogatore().getTipo();
			}
			else if(requestInfo.getIdServizio().getSoggettoErogatore().getNome().equals(idServizio.getSoggettoErogatore().getNome())==false){
				oggetto = "Soggetto erogatore";
				datoBusta = idServizio.getSoggettoErogatore().getNome();
				datoPA = requestInfo.getIdServizio().getSoggettoErogatore().getNome();
			}
			if(oggetto!=null){
				setSOAPFault_processamento(IntegrationFunctionError.BAD_REQUEST,logCore,msgDiag,
						ErroriIntegrazione.ERRORE_455_DATI_BUSTA_DIFFERENTI_PA_INVOCATA.
							getErrore455DatiBustaDifferentiDatiPAInvocata(oggetto, datoBusta, datoPA, locationPA),null,
						"ConfrontoDatiBustaConDatiInvocazionePortaApplicativa");
				openspcoopstate.releaseResource();
				return;
			}
		}
		
		
		
		
		
		
		
		/* ----------- Ruolo Busta Ricevuta ------------ */
		
		RuoloBusta ruoloBustaRicevuta = null;
		if(functionAsRouter==false){
			msgDiag.mediumDebug("Lettura Ruolo Busta...");
			try{
				ruoloBustaRicevuta = validatore.getRuoloBustaRicevuta(requestInfo.getProtocolServiceBinding(),false);
			}catch(Exception e){
				msgDiag.logErroreGenerico(e,"validator.getRuoloBustaRicevuta(false)");
			}
		}
		
		
		
		
		
		
		
		
		/* -------- Lettura Porta Applicativa 
		 * (Il vero controllo sull'esistenza della Porta Applicativa viene effettuato in Sbustamento, poiche' dipende dal profilo) ------------- */
		// per profili asincroni
		PortaDelegata pd = null;
		IDPortaDelegata idPD = null;
		IDPortaApplicativa idPA = null;
		String servizioApplicativoErogatoreAsincronoSimmetricoRisposta = null;
		boolean asincronoSimmetricoRisposta = false;
		if(functionAsRouter==false && idServizio!=null){
			msgDiag.mediumDebug("Lettura porta applicativa/delegata...");
			try{

				/* ----------- Comprensione profilo -------------- */
				if(     (org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.ASINCRONO_SIMMETRICO.equals(bustaRichiesta.getProfiloDiCollaborazione()) ||
						org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.ASINCRONO_ASIMMETRICO.equals(bustaRichiesta.getProfiloDiCollaborazione())) &&
						!(RuoloBusta.RICHIESTA.equals(ruoloBustaRicevuta.toString())) )
				{
					msgDiag.highDebug("Lettura porta applicativa/delegata (Asincrono)...");
					// La validazione non deve essere effettuata se abbiamo una ricevuta asincrona, 'modalita' asincrona'
					ProfiloDiCollaborazione profiloCollaborazione = new ProfiloDiCollaborazione(openspcoopstate.getStatoRichiesta(),protocolFactory);

					if(org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.ASINCRONO_SIMMETRICO.equals(bustaRichiesta.getProfiloDiCollaborazione())) {	

						asincronoSimmetricoRisposta = true;
						
						//	Risposta Asincrona
						RepositoryBuste repository = new RepositoryBuste(openspcoopstate.getStatoRichiesta(), true, protocolFactory);
						Integrazione integrazione = null;
						if(bustaRichiesta.getRiferimentoMessaggio()!=null){
							integrazione = repository.getInfoIntegrazioneFromOutBox(bustaRichiesta.getRiferimentoMessaggio());
						}else{
							// LineeGuida (Collaborazione)
							integrazione = repository.getInfoIntegrazioneFromOutBox(bustaRichiesta.getCollaborazione());
						}
						servizioApplicativoErogatoreAsincronoSimmetricoRisposta = integrazione.getServizioApplicativo();
						idPD = new IDPortaDelegata();
						idPD.setNome(integrazione.getNomePorta());
						pd = configurazionePdDReader.getPortaDelegata_SafeMethod(idPD);
						idPD = configurazionePdDReader.convertToIDPortaDelegata(pd); // per aggiungere informazioni sugli identificativi
						
					}
					// Profilo Asincrono Asimmetrico
					else if(org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.ASINCRONO_ASIMMETRICO.equals(bustaRichiesta.getProfiloDiCollaborazione())) {	

						///	Risposta Asincrona
						if(RuoloBusta.RISPOSTA.equals(ruoloBustaRicevuta.toString())){
							// ConversioneServizio.
							IDServizio idServizioOriginale = null;
							if(bustaRichiesta.getRiferimentoMessaggio()!=null){
								idServizioOriginale = profiloCollaborazione.asincronoAsimmetrico_getDatiConsegnaRisposta(bustaRichiesta.getRiferimentoMessaggio());
							}else{
								// LineeGuida (Collaborazione)
								idServizioOriginale = profiloCollaborazione.asincronoAsimmetrico_getDatiConsegnaRisposta(bustaRichiesta.getCollaborazione());
							}
							this.overwriteIdSoggetto(idServizioOriginale, idServizio.getSoggettoErogatore());
								
							if(pa==null){
								pa = this.getPortaApplicativa(configurazionePdDReader, idServizioOriginale);
							}
							
						}
						// Ricevuta alla richiesta/risposta.
						else if(RuoloBusta.RICEVUTA_RICHIESTA.equals(ruoloBustaRicevuta.toString()) || 
								RuoloBusta.RICEVUTA_RISPOSTA.equals(ruoloBustaRicevuta.toString()) ){	
							RepositoryBuste repository = new RepositoryBuste(openspcoopstate.getStatoRichiesta(), true, protocolFactory);
							Integrazione integrazione = null;
							if(bustaRichiesta.getRiferimentoMessaggio()!=null){
								integrazione = repository.getInfoIntegrazioneFromOutBox(bustaRichiesta.getRiferimentoMessaggio());
							}else{
								// LineeGuida (Collaborazione)
								integrazione = repository.getInfoIntegrazioneFromOutBox(bustaRichiesta.getCollaborazione());
							}
							idPD = new IDPortaDelegata();
							idPD.setNome(integrazione.getNomePorta());
							pd = configurazionePdDReader.getPortaDelegata_SafeMethod(idPD);
							idPD = configurazionePdDReader.convertToIDPortaDelegata(pd); // per aggiungere informazioni sugli identificativi
						}
						
					}
					// Chiudo eventuali prepared statement, che non voglio eseguire.
					((StateMessage)openspcoopstate.getStatoRichiesta()).closePreparedStatement();
				}else{
					msgDiag.highDebug("Lettura porta applicativa/delegata (Normale)...");
					if(pa==null){
						pa = this.getPortaApplicativa(configurazionePdDReader, idServizio);
					}
				}
				
				// Aggiungo identita servizio applicativi
				if(pa!=null){
					idPA = configurazionePdDReader.convertToIDPortaApplicativa(pa);
					msgDiag.updatePorta(pa.getNome());
					for(int i=0; i<pa.sizeServizioApplicativoList();i++){
						this.msgContext.getIntegrazione().addServizioApplicativoErogatore(pa.getServizioApplicativo(i).getNome());
					}
				}
				else if(servizioApplicativoErogatoreAsincronoSimmetricoRisposta!=null){
					// E' l'erogatore della risposta asincrona!
					this.msgContext.getIntegrazione().addServizioApplicativoErogatore(servizioApplicativoErogatoreAsincronoSimmetricoRisposta);
				}
				
				// Aggiungo identificativo porta applicativa/delegata
				if(idPA!=null){
					this.msgContext.getIntegrazione().setIdPA(idPA);
				}
				else if(idPD!=null){
					this.msgContext.getIntegrazione().setIdPD(idPD);
				}
				
				msgDiag.highDebug("Lettura porta applicativa/delegata terminato");
				
			}catch(Exception e){
				if(  !(e instanceof DriverConfigurazioneNotFound) ) {
					msgDiag.logErroreGenerico(e,"letturaPorta");
					
					setSOAPFault_processamento(IntegrationFunctionError.INTERNAL_REQUEST_ERROR,
							ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
							get5XX_ErroreProcessamento("LetturaPorta",CodiceErroreIntegrazione.CODICE_525_GESTIONE_FUNZIONALITA_PROTOCOLLO),e);
					return;

				}
			}
		}

		msgDiag.highDebug("Lettura porta applicativa/delegata terminato impostazione context");

		
		
		
		
		
		
		
		
		
		/*
		 * ---------------- Aggiornamento dati raccolti (PreAutenticazione) ---------------------
		 */
		
		//		 Aggiornamento Informazioni protocollo
		msgDiag.setIdMessaggioRichiesta(validatore.getBusta().getID());
		this.msgContext.setIdMessage(validatore.getBusta().getID());
		msgDiag.setServizio(idServizio);
		msgDiag.addKeywords(validatore.getBusta(), true);
		parametriGenerazioneBustaErrore.setMsgDiag(msgDiag);
		parametriInvioBustaErrore.setMsgDiag(msgDiag);

		
		Tracciamento tracciamento = new Tracciamento(identitaPdD,
				this.msgContext.getIdModulo(),
				inRequestContext.getPddContext(),
				this.msgContext.getTipoPorta(),msgDiag.getPorta(),
				openspcoopstate.getStatoRichiesta(),openspcoopstate.getStatoRisposta());
		parametriGenerazioneBustaErrore.setTracciamento(tracciamento);
		
		
		
		
		
		
		
		/* --------------- Gestione credenziali --------------- */
		if(RicezioneBuste.tipiGestoriCredenziali!=null){
			msgDiag.mediumDebug("Gestione personalizzata delle credenziali...");
			
			for (int i = 0; i < RicezioneBuste.tipiGestoriCredenziali.length; i++) {
				try {
					
					IGestoreCredenziali gestore = null;
					String classType = null;
					try {
						classType = className.getGestoreCredenziali(RicezioneBuste.tipiGestoriCredenziali[i]);
						gestore = (IGestoreCredenziali)loader.newInstance(classType);
						AbstractCore.init(gestore, pddContext, protocolFactory);
					} catch (Exception e) {
						throw new Exception(
								"Riscontrato errore durante il caricamento della classe ["+ classType
								+ "] da utilizzare per la gestione delle credenziali di tipo ["
								+ RicezioneBuste.tipiGestoriCredenziali[i]+ "]: " + e.getMessage());
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
								identita = "Gestore delle credenziali di tipo "+RicezioneBuste.tipiGestoriCredenziali[i];
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
					logCore.error("Errore durante l'identificazione delle credenziali ["+ RicezioneBuste.tipiGestoriCredenziali[i]
					         + "]: "+ e.getMessage(),e);
					msgDiag.addKeyword(CostantiPdD.KEY_TIPO_GESTORE_CREDENZIALI,RicezioneBuste.tipiGestoriCredenziali[i]);
					msgDiag.addKeywordErroreProcessamento(e);
					msgDiag.logPersonalizzato("gestoreCredenziali.errore");
					ErroreIntegrazione msgErroreIntegrazione = null;
					String wwwAuthenticateErrorHeader = null;
					if(e instanceof GestoreCredenzialiConfigurationException){
						GestoreCredenzialiConfigurationException ge = (GestoreCredenzialiConfigurationException) e;
						parametriGenerazioneBustaErrore.setIntegrationFunctionError(ge.getIntegrationFunctionError());
						msgErroreIntegrazione = 
								ErroriIntegrazione.ERRORE_431_GESTORE_CREDENZIALI_ERROR.
									getErrore431_ErroreGestoreCredenziali(RicezioneBuste.tipiGestoriCredenziali[i],e);
						pddContext.addObject(org.openspcoop2.core.constants.Costanti.ERRORE_AUTENTICAZIONE, "true");
						wwwAuthenticateErrorHeader = ge.getWwwAuthenticateErrorHeader();
					}else{
						msgErroreIntegrazione = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
								get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_548_GESTORE_CREDENZIALI_NON_FUNZIONANTE);
					}
					
					// Tracciamento richiesta: non ancora registrata
					if(this.msgContext.isTracciamentoAbilitato()){
						EsitoElaborazioneMessaggioTracciato esitoTraccia = 
								EsitoElaborazioneMessaggioTracciato.getEsitoElaborazioneConErrore(msgDiag.getMessaggio_replaceKeywords("gestoreCredenziali.errore"));
						tracciamento.registraRichiesta(requestMessage,null,soapHeaderElement,bustaRichiesta,esitoTraccia,
								Tracciamento.createLocationString(true,this.msgContext.getSourceLocation()),
								correlazioneApplicativa);
					}
					if(this.msgContext.isGestioneRisposta()){
						
						parametriGenerazioneBustaErrore.setBusta(bustaRichiesta);
						parametriGenerazioneBustaErrore.setErroreIntegrazione(msgErroreIntegrazione);
						OpenSPCoop2Message errorMsg = generaBustaErroreProcessamento(parametriGenerazioneBustaErrore,e);
						if(wwwAuthenticateErrorHeader!=null) {
							errorMsg.forceTransportHeader(HttpConstants.AUTHORIZATION_RESPONSE_WWW_AUTHENTICATE, wwwAuthenticateErrorHeader);
						}
						
						// Nota: la bustaRichiesta e' stata trasformata da generaErroreProcessamento
						parametriInvioBustaErrore.setOpenspcoopMsg(errorMsg);
						parametriInvioBustaErrore.setBusta(parametriGenerazioneBustaErrore.getBusta());
						sendRispostaBustaErrore(parametriInvioBustaErrore);
					}
					openspcoopstate.releaseResource();
					return;
					
				}
			}
		}
		
		
		
		
		
		
		
		
		
		
		/* ------------ GestioneToken ------------- */
		
		msgDiag.mediumDebug("GestioneToken...");
		String tipoGestioneToken = null;
		GestioneTokenAutenticazione gestioneTokenAutenticazione = null;
		try {
			if(pa!=null){
				tipoGestioneToken = configurazionePdDReader.getGestioneToken(pa);
				if(pa.getGestioneToken()!=null) {
					gestioneTokenAutenticazione = pa.getGestioneToken().getAutenticazione();
				}
			}
			else{
				tipoGestioneToken = configurazionePdDReader.getGestioneToken(pd);
				if(pd.getGestioneToken()!=null) {
					gestioneTokenAutenticazione = pd.getGestioneToken().getAutenticazione();
				}
			}
		}catch(Exception exception){}
		this.msgContext.getIntegrazione().setTipoGestioneToken(tipoGestioneToken);
		if (tipoGestioneToken == null || asincronoSimmetricoRisposta) {

			if(!asincronoSimmetricoRisposta) {
				msgDiag.logPersonalizzato("gestioneTokenDisabilitata");
			}
			
		} else {

			transaction.getTempiElaborazione().startToken();
			try {
			
				ErroreCooperazione erroreCooperazione = null;
				ErroreIntegrazione erroreIntegrazione = null;
				Exception eGestioneToken = null;
				OpenSPCoop2Message errorMessageGestioneToken = null;
				String wwwAuthenticateErrorHeader = null;
				boolean fineGestione = false;
				IntegrationFunctionError integrationFunctionError = null;
				try {
					
					PolicyGestioneToken policyGestioneToken = null;
					if(pa!=null){
						policyGestioneToken = configurazionePdDReader.getPolicyGestioneToken(pa);
					}
					else {
						policyGestioneToken = configurazionePdDReader.getPolicyGestioneToken(pd);
					}
					
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
					
					org.openspcoop2.pdd.core.token.pa.DatiInvocazionePortaApplicativa datiInvocazione = new org.openspcoop2.pdd.core.token.pa.DatiInvocazionePortaApplicativa();
					datiInvocazione.setInfoConnettoreIngresso(inRequestContext.getConnettore());
					datiInvocazione.setState(openspcoopstate.getStatoRichiesta());
					datiInvocazione.setMessage(requestMessage);
					datiInvocazione.setIdPA(idPA);
					datiInvocazione.setPa(pa);	
					datiInvocazione.setIdPD(idPD);
					datiInvocazione.setPd(pd);		
					datiInvocazione.setPolicyGestioneToken(policyGestioneToken);
					
					GestoreToken.validazioneConfigurazione(datiInvocazione); // assicura che la configurazione sia corretta
					
					GestioneToken gestioneTokenEngine = new GestioneToken(logCore, idTransazione, pddContext, protocolFactory);
					
					// cerco token
					
					msgDiag.addKeyword(CostantiPdD.KEY_TOKEN_POSIZIONE, policyGestioneToken.getLabelPosizioneToken());
					msgDiag.logPersonalizzato("gestioneTokenInCorso.verificaPresenzaToken");
					
					EsitoPresenzaTokenPortaApplicativa esitoPresenzaToken = gestioneTokenEngine.verificaPresenzaToken(datiInvocazione);
					EsitoGestioneTokenPortaApplicativa esitoValidazioneToken = null;
					EsitoGestioneTokenPortaApplicativa esitoIntrospectionToken = null;
					EsitoGestioneTokenPortaApplicativa esitoUserInfoToken = null;
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
								
									erroreCooperazione = esitoValidazioneToken.getErroreCooperazione();
									erroreIntegrazione = esitoValidazioneToken.getErroreIntegrazione();
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
								
									erroreCooperazione = esitoIntrospectionToken.getErroreCooperazione();
									erroreIntegrazione = esitoIntrospectionToken.getErroreIntegrazione();
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
								
									erroreCooperazione = esitoUserInfoToken.getErroreCooperazione();
									erroreIntegrazione = esitoUserInfoToken.getErroreIntegrazione();
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
						
							erroreCooperazione = esitoPresenzaToken.getErroreCooperazione();
							erroreIntegrazione = esitoPresenzaToken.getErroreIntegrazione();
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
					
					erroreIntegrazione = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
							get5XX_ErroreProcessamento("processo di gestione token ["+ tipoGestioneToken + "] fallito, " + e.getMessage(),
									CodiceErroreIntegrazione.CODICE_560_GESTIONE_TOKEN);
					erroreCooperazione = null;
					eGestioneToken = e;
					
					integrationFunctionError = IntegrationFunctionError.INTERNAL_REQUEST_ERROR;
					
					fineGestione = true;
					
				}
				if (fineGestione) {
									
					// Tracciamento richiesta: non ancora registrata
					if(this.msgContext.isTracciamentoAbilitato()){
						EsitoElaborazioneMessaggioTracciato esitoTraccia = 
								EsitoElaborazioneMessaggioTracciato.getEsitoElaborazioneConErrore("["+ RicezioneBuste.ID_MODULO+ "] processo di gestione token ["
									+ tipoGestioneToken + "] fallito");
						tracciamento.registraRichiesta(requestMessage,null,soapHeaderElement,bustaRichiesta,esitoTraccia,
								Tracciamento.createLocationString(true,this.msgContext.getSourceLocation()),
								correlazioneApplicativa);
					}
					
					if(this.msgContext.isGestioneRisposta()){
	
						if(errorMessageGestioneToken!=null) {
							this.msgContext.setMessageResponse(errorMessageGestioneToken);
						}
						else {
						
							parametriGenerazioneBustaErrore.setBusta(bustaRichiesta);	
							if(erroreIntegrazione != null){
								parametriGenerazioneBustaErrore.setErroreIntegrazione(erroreIntegrazione);
							}
							else{
								parametriGenerazioneBustaErrore.setErroreCooperazione(erroreCooperazione);
							}
		
							OpenSPCoop2Message errorOpenSPCoopMsg = null;
														
							if(erroreCooperazione!=null){
								if(CodiceErroreCooperazione.SICUREZZA_TOKEN_NON_PRESENTE.equals(erroreCooperazione.getCodiceErrore())) {
									if(integrationFunctionError==null) {
										integrationFunctionError = IntegrationFunctionError.TOKEN_NOT_FOUND;
									}
									parametriGenerazioneBustaErrore.setIntegrationFunctionError(integrationFunctionError);
									errorOpenSPCoopMsg = generaBustaErroreValidazione(parametriGenerazioneBustaErrore);
								}
								else if(CodiceErroreCooperazione.SICUREZZA_TOKEN_NON_VALIDO.equals(erroreCooperazione.getCodiceErrore())) {
									if(integrationFunctionError==null) {
										integrationFunctionError = IntegrationFunctionError.TOKEN_INVALID;
									}
									parametriGenerazioneBustaErrore.setIntegrationFunctionError(integrationFunctionError);
									errorOpenSPCoopMsg = generaBustaErroreValidazione(parametriGenerazioneBustaErrore);
								}
								else {
									if(integrationFunctionError==null) {
										integrationFunctionError = IntegrationFunctionError.INTERNAL_REQUEST_ERROR;
									}
									parametriGenerazioneBustaErrore.setIntegrationFunctionError(integrationFunctionError);
									errorOpenSPCoopMsg = generaBustaErroreProcessamento(parametriGenerazioneBustaErrore,eGestioneToken);
								}
							}
							else {
								if(integrationFunctionError==null) {
									integrationFunctionError = IntegrationFunctionError.INTERNAL_REQUEST_ERROR;
								}
								parametriGenerazioneBustaErrore.setIntegrationFunctionError(integrationFunctionError);
								errorOpenSPCoopMsg = generaBustaErroreProcessamento(parametriGenerazioneBustaErrore,eGestioneToken);
							}	
							
							if(wwwAuthenticateErrorHeader!=null) {
								errorOpenSPCoopMsg.forceTransportHeader(HttpConstants.AUTHORIZATION_RESPONSE_WWW_AUTHENTICATE, wwwAuthenticateErrorHeader);
							}
							
							// Nota: la bustaRichiesta e' stata trasformata da generaErroreProcessamento
							parametriInvioBustaErrore.setOpenspcoopMsg(errorOpenSPCoopMsg);
							parametriInvioBustaErrore.setBusta(parametriGenerazioneBustaErrore.getBusta());
							sendRispostaBustaErrore(parametriInvioBustaErrore);
						}
	
					}
										
					openspcoopstate.releaseResource();
					return;
					
				}
			}finally {
				transaction.getTempiElaborazione().endToken();
			}

		}
		
		
		
		
		
		
		
		
		
		
		
		
		
		/*
		 * ---------------- Mittente / Autenticazione ---------------------
		 */
		boolean soggettoFruitoreIdentificatoTramiteProtocollo = false;
		if(soggettoFruitore==null && validatore.getSoggettoMittente()!=null) {
			soggettoFruitoreIdentificatoTramiteProtocollo = true;
		}
		soggettoFruitore = validatore.getSoggettoMittente();
		boolean soggettoAutenticato = false;
		boolean supportatoAutenticazioneSoggetti = false;
		if(functionAsRouter==false){
			supportatoAutenticazioneSoggetti = protocolFactory.createProtocolConfiguration().isSupportoAutenticazioneSoggetti();
			String credenzialeTrasporto = null;
			String tipoAutenticazione = null;
			
			org.openspcoop2.pdd.core.autenticazione.pa.DatiInvocazionePortaApplicativa datiInvocazioneAutenticazione = new org.openspcoop2.pdd.core.autenticazione.pa.DatiInvocazionePortaApplicativa();
			datiInvocazioneAutenticazione.setInfoConnettoreIngresso(inRequestContext.getConnettore());
			datiInvocazioneAutenticazione.setState(openspcoopstate.getStatoRichiesta());
			datiInvocazioneAutenticazione.setIdPA(idPA);
			datiInvocazioneAutenticazione.setPa(pa);	
			datiInvocazioneAutenticazione.setIdPD(idPD);
			datiInvocazioneAutenticazione.setPd(pd);		
			
			if(supportatoAutenticazioneSoggetti && !asincronoSimmetricoRisposta && (pa!=null || pd!=null)){
			
				msgDiag.mediumDebug("Autenticazione del soggetto...");
				boolean autenticazioneOpzionale = false;
				ParametriAutenticazione parametriAutenticazione = null;
				try{
					if(pa!=null){
						tipoAutenticazione = configurazionePdDReader.getAutenticazione(pa);
						autenticazioneOpzionale = configurazionePdDReader.isAutenticazioneOpzionale(pa);
						parametriAutenticazione = new ParametriAutenticazione(pa.getProprietaAutenticazioneList());
					}
					else{
						tipoAutenticazione = configurazionePdDReader.getAutenticazione(pd);
						autenticazioneOpzionale = configurazionePdDReader.isAutenticazioneOpzionale(pd);
						parametriAutenticazione = new ParametriAutenticazione(pd.getProprietaAutenticazioneList());
					}
				}catch(Exception exception){}
				this.msgContext.getIntegrazione().setTipoAutenticazione(tipoAutenticazione);
				this.msgContext.getIntegrazione().setAutenticazioneOpzionale(autenticazioneOpzionale);
				if(tipoAutenticazione!=null){
					msgDiag.addKeyword(CostantiPdD.KEY_TIPO_AUTENTICAZIONE, tipoAutenticazione);
				}
				//String soggettoFruitore = CostantiPdD.SOGGETTO_ANONIMO;
				if (CostantiConfigurazione.AUTENTICAZIONE_NONE.toString().equalsIgnoreCase(tipoAutenticazione)) {
					msgDiag.logPersonalizzato("autenticazioneDisabilitata");
				}	
				else{
					
					transaction.getTempiElaborazione().startAutenticazione();
					try {
					
						msgDiag.addKeyword(CostantiPdD.KEY_CREDENZIALI_MITTENTE_MSG, credenziali.toString());
						msgDiag.logPersonalizzato("autenticazioneInCorso");
						
						ErroreCooperazione erroreCooperazione = null;
						ErroreIntegrazione erroreIntegrazione = null;
						Exception eAutenticazione = null;
						OpenSPCoop2Message errorMessageAutenticazione = null;
						String wwwAuthenticateErrorHeader = null;
						boolean detailsSet = false;
						IntegrationFunctionError integrationFunctionError = null;
						try {						
							EsitoAutenticazionePortaApplicativa esito = 
									GestoreAutenticazione.verificaAutenticazionePortaApplicativa(tipoAutenticazione,
											datiInvocazioneAutenticazione, parametriAutenticazione,
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
								erroreCooperazione = esito.getErroreCooperazione();
								erroreIntegrazione = esito.getErroreIntegrazione();
								eAutenticazione = esito.getEccezioneProcessamento();
								errorMessageAutenticazione = esito.getErrorMessage();
								wwwAuthenticateErrorHeader = esito.getWwwAuthenticateErrorHeader();
								integrationFunctionError = esito.getIntegrationFunctionError();
								msgDiag.addKeyword(CostantiPdD.KEY_CREDENZIALI_MITTENTE_MSG, credenziali.toString(
										propertiesReader.isAutenticazioneBasicLogPassword() ? Credenziali.SHOW_BASIC_PASSWORD : !Credenziali.SHOW_BASIC_PASSWORD,
										!Credenziali.SHOW_ISSUER,
										!Credenziali.SHOW_DIGEST_CLIENT_CERT,
										!Credenziali.SHOW_SERIAL_NUMBER_CLIENT_CERT)); // Aggiungo la password se presente, evito inoltre di stampare l'issuer e altre info del cert nei diagnostici
							}
							else {
								if(esito.isClientIdentified()) {
									soggettoAutenticato = true;
									IDSoggetto idSoggettoFruitoreIdentificato = esito.getIdSoggetto();
									if(soggettoFruitore!=null &&  soggettoFruitore.getNome()!=null && soggettoFruitore.getTipo()!=null) {
										if(!soggettoFruitore.equals(idSoggettoFruitoreIdentificato)) {
											throw new Exception("Identificato un soggetto (tramite profilo di interoperabilità) '"+soggettoFruitore
													+"' differente da quello identificato tramite il processo di autenticazione '"+idSoggettoFruitoreIdentificato+"'");
										}
									}
									soggettoFruitore = 	idSoggettoFruitoreIdentificato;
									if(esito.getIdServizioApplicativo()!=null) {
										servizioApplicativoFruitore = esito.getIdServizioApplicativo().getNome();
										parametriGenerazioneBustaErrore.setServizioApplicativoFruitore(servizioApplicativoFruitore);
										parametriInvioBustaErrore.setServizioApplicativoFruitore(servizioApplicativoFruitore);
										this.generatoreErrore.updateInformazioniCooperazione(servizioApplicativoFruitore);
										msgDiag.addKeyword(CostantiPdD.KEY_SA_FRUITORE, servizioApplicativoFruitore);
										this.msgContext.getIntegrazione().setServizioApplicativoFruitore(servizioApplicativoFruitore);
									}
									msgDiag.addKeyword(CostantiPdD.KEY_CREDENZIALI_MITTENTE_MSG, ""); // per evitare di visualizzarle anche nei successivi diagnostici
									msgDiag.addKeyword(CostantiPdD.KEY_CREDENZIALI, "");
								}
								else {
									// l'errore puo' non esserci se l'autenticazione utilizzata non prevede una identificazione obbligatoria
									erroreCooperazione = esito.getErroreCooperazione();
									erroreIntegrazione = esito.getErroreIntegrazione();
									eAutenticazione = esito.getEccezioneProcessamento();
									errorMessageAutenticazione = esito.getErrorMessage();
									wwwAuthenticateErrorHeader = esito.getWwwAuthenticateErrorHeader();
									integrationFunctionError = esito.getIntegrationFunctionError();
									
									// evito comunque di ripresentarle nei successivi diagnostici, l'informazione l'ho gia' visualizzata nei diagnostici dell'autenticazione
									msgDiag.addKeyword(CostantiPdD.KEY_CREDENZIALI_MITTENTE_MSG, ""); // per evitare di visualizzarle anche nei successivi diagnostici
									msgDiag.addKeyword(CostantiPdD.KEY_CREDENZIALI, "");
								}
							}
							
							if (erroreIntegrazione != null || erroreCooperazione!=null) {
								if(autenticazioneOpzionale==false){
									pddContext.addObject(org.openspcoop2.core.constants.Costanti.ERRORE_AUTENTICAZIONE, "true");
								}
							}
							else {
								msgDiag.logPersonalizzato("autenticazioneEffettuata");							
							}
							
						} catch (Exception e) {
							erroreIntegrazione = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
									get5XX_ErroreProcessamento("["+ RicezioneBuste.ID_MODULO+ "] processo di autenticazione ["
											+ tipoAutenticazione + "] fallito, " + e.getMessage(),CodiceErroreIntegrazione.CODICE_503_AUTENTICAZIONE);
							erroreCooperazione = null;
							eAutenticazione = e;
							logCore.error("processo di autenticazione ["
									+ tipoAutenticazione + "] fallito, " + e.getMessage(),e);
							integrationFunctionError = IntegrationFunctionError.INTERNAL_REQUEST_ERROR;
						}
						if (erroreIntegrazione != null || erroreCooperazione!=null) {
							if(!detailsSet) {
								msgDiag.addKeyword(CostantiPdD.KEY_DETAILS, "");
							}
							String descrizioneErrore = null;
							try{
								if(erroreCooperazione != null)
									descrizioneErrore = erroreCooperazione.getDescrizione(protocolFactory);
								else
									descrizioneErrore = erroreIntegrazione.getDescrizione(protocolFactory);
								msgDiag.addKeyword(CostantiPdD.KEY_ERRORE_PROCESSAMENTO, descrizioneErrore);
							}catch(Exception e){
								logCore.error("getDescrizione Error:"+e.getMessage(),e);
							}
							String errorMsg =  "Riscontrato errore durante il processo di Autenticazione per il messaggio con identificativo di transazione ["+idTransazione+"]: "+descrizioneErrore;
							if(autenticazioneOpzionale){
								msgDiag.logPersonalizzato("autenticazioneFallita.opzionale");
								if(eAutenticazione!=null){
									logCore.debug(errorMsg,eAutenticazione);
								}
								else{
									logCore.debug(errorMsg);
								}
							}
							else{
								msgDiag.logPersonalizzato("autenticazioneFallita");
								if(eAutenticazione!=null){
									logCore.error(errorMsg,eAutenticazione);
								}
								else{
									logCore.error(errorMsg);
								}
							}
							
							if(!autenticazioneOpzionale){
							
								// Tracciamento richiesta: non ancora registrata
								if(this.msgContext.isTracciamentoAbilitato()){
									EsitoElaborazioneMessaggioTracciato esitoTraccia = 
											EsitoElaborazioneMessaggioTracciato.getEsitoElaborazioneConErrore("["+ RicezioneBuste.ID_MODULO+ "] processo di autenticazione ["
												+ tipoAutenticazione + "] fallito");
									tracciamento.registraRichiesta(requestMessage,null,soapHeaderElement,bustaRichiesta,esitoTraccia,
											Tracciamento.createLocationString(true,this.msgContext.getSourceLocation()),
											correlazioneApplicativa);
								}
								
								if(this.msgContext.isGestioneRisposta()){
		
									if(errorMessageAutenticazione!=null) {
										this.msgContext.setMessageResponse(errorMessageAutenticazione);
									}
									else {
									
										parametriGenerazioneBustaErrore.setBusta(bustaRichiesta);	
										if(erroreIntegrazione != null){
											parametriGenerazioneBustaErrore.setErroreIntegrazione(erroreIntegrazione);
										}
										else{
											parametriGenerazioneBustaErrore.setErroreCooperazione(erroreCooperazione);
										}
			
										OpenSPCoop2Message errorOpenSPCoopMsg = null;
										if(erroreCooperazione!=null){
											if(CodiceErroreCooperazione.MITTENTE_NON_PRESENTE.equals(erroreCooperazione.getCodiceErrore()) ||
													CodiceErroreCooperazione.MITTENTE_NON_VALORIZZATO.equals(erroreCooperazione.getCodiceErrore()) ||
													CodiceErroreCooperazione.MITTENTE.equals(erroreCooperazione.getCodiceErrore())) {
												if(integrationFunctionError==null) {
													integrationFunctionError = IntegrationFunctionError.AUTHENTICATION_CREDENTIALS_NOT_FOUND;
												}
												parametriGenerazioneBustaErrore.setIntegrationFunctionError(integrationFunctionError);
												errorOpenSPCoopMsg = generaBustaErroreValidazione(parametriGenerazioneBustaErrore);
											}
											else if(CodiceErroreCooperazione.MITTENTE_SCONOSCIUTO.equals(erroreCooperazione.getCodiceErrore()) ||
													CodiceErroreCooperazione.MITTENTE_NON_VALIDO.equals(erroreCooperazione.getCodiceErrore())) {
												if(integrationFunctionError==null) {
													integrationFunctionError = IntegrationFunctionError.AUTHENTICATION_INVALID_CREDENTIALS;
												}
												parametriGenerazioneBustaErrore.setIntegrationFunctionError(integrationFunctionError);
												errorOpenSPCoopMsg = generaBustaErroreValidazione(parametriGenerazioneBustaErrore);
											}
											else {
												if(integrationFunctionError==null) {
													integrationFunctionError = IntegrationFunctionError.INTERNAL_REQUEST_ERROR;
												}
												parametriGenerazioneBustaErrore.setIntegrationFunctionError(integrationFunctionError);
												errorOpenSPCoopMsg = generaBustaErroreProcessamento(parametriGenerazioneBustaErrore,eAutenticazione);
											}
										}
										else {
											if(integrationFunctionError==null) {
												integrationFunctionError = IntegrationFunctionError.INTERNAL_REQUEST_ERROR;
											}
											parametriGenerazioneBustaErrore.setIntegrationFunctionError(integrationFunctionError);
											errorOpenSPCoopMsg = generaBustaErroreProcessamento(parametriGenerazioneBustaErrore,eAutenticazione);
										}	
										
										if(wwwAuthenticateErrorHeader!=null) {
											errorOpenSPCoopMsg.forceTransportHeader(HttpConstants.AUTHORIZATION_RESPONSE_WWW_AUTHENTICATE, wwwAuthenticateErrorHeader);
										}
										
	//									if(ServiceBinding.REST.equals(requestMessage.getServiceBinding())){
	//										if(wwwAuthenticateErrorHeader!=null && wwwAuthenticateErrorHeader.startsWith(HttpConstants.AUTHORIZATION_PREFIX_BASIC) && 
	//												ServiceBinding.REST.equals(errorOpenSPCoopMsg.getServiceBinding())) {
	//											try {
	//												errorOpenSPCoopMsg.castAsRest().updateContent(null);
	//											}catch(Throwable e) {}
	//										}
	//									}
										
										// Nota: la bustaRichiesta e' stata trasformata da generaErroreProcessamento
										parametriInvioBustaErrore.setOpenspcoopMsg(errorOpenSPCoopMsg);
										parametriInvioBustaErrore.setBusta(parametriGenerazioneBustaErrore.getBusta());
										sendRispostaBustaErrore(parametriInvioBustaErrore);
									}
		
								}
								
								openspcoopstate.releaseResource();
								return;
								
							}
						}
					}
					finally {
						transaction.getTempiElaborazione().endAutenticazione();
					}
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
					
					ErroreCooperazione erroreCooperazione = null;
					ErroreIntegrazione erroreIntegrazione = null;
					Exception eAutenticazione = null;
					OpenSPCoop2Message errorMessageAutenticazione = null;
					String wwwAuthenticateErrorHeader = null;
					String errorMessage = null;
					IntegrationFunctionError integrationFunctionError = null;
					try {
						EsitoAutenticazionePortaApplicativa	esito = 
								GestoreAutenticazione.verificaAutenticazioneTokenPortaApplicativa(gestioneTokenAutenticazione, datiInvocazioneAutenticazione, pddContext, protocolFactory, requestMessage);
	
						if(esito.isClientAuthenticated() == false) {
							erroreCooperazione = esito.getErroreCooperazione();
							erroreIntegrazione = esito.getErroreIntegrazione();
							eAutenticazione = esito.getEccezioneProcessamento();
							errorMessageAutenticazione = esito.getErrorMessage();
							wwwAuthenticateErrorHeader = esito.getWwwAuthenticateErrorHeader();
							errorMessage = esito.getDetails();
							integrationFunctionError = esito.getIntegrationFunctionError();
						}
						
						if (erroreIntegrazione != null || erroreCooperazione!=null) {
							pddContext.addObject(org.openspcoop2.core.constants.Costanti.ERRORE_AUTENTICAZIONE_TOKEN, "true");
						}
						else {
							msgDiag.logPersonalizzato("autenticazioneTokenEffettuata");							
						}
						
					} catch (Exception e) {
						erroreIntegrazione = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
								get5XX_ErroreProcessamento("["+ RicezioneBuste.ID_MODULO+ "] processo di autenticazione token ["
										+ checkAuthnToken + "] fallito, " + e.getMessage(),CodiceErroreIntegrazione.CODICE_503_AUTENTICAZIONE);
						erroreCooperazione = null;
						eAutenticazione = e;
						logCore.error("processo di autenticazione token ["
								+ checkAuthnToken + "] fallito, " + e.getMessage(),e);
						integrationFunctionError = IntegrationFunctionError.INTERNAL_REQUEST_ERROR;
					}
					if (erroreIntegrazione != null || erroreCooperazione!=null) {
						String descrizioneErrore = null;
						try{
							if(errorMessage!=null) {
								descrizioneErrore = errorMessage;
							}
							else {
								if(erroreCooperazione != null)
									descrizioneErrore = erroreCooperazione.getDescrizione(protocolFactory);
								else
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
						
						// Tracciamento richiesta: non ancora registrata
						if(this.msgContext.isTracciamentoAbilitato()){
							EsitoElaborazioneMessaggioTracciato esitoTraccia = 
									EsitoElaborazioneMessaggioTracciato.getEsitoElaborazioneConErrore("["+ RicezioneBuste.ID_MODULO+ "] processo di autenticazione ["
										+ tipoAutenticazione + "] fallito");
							tracciamento.registraRichiesta(requestMessage,null,soapHeaderElement,bustaRichiesta,esitoTraccia,
									Tracciamento.createLocationString(true,this.msgContext.getSourceLocation()),
									correlazioneApplicativa);
						}
						
						if(this.msgContext.isGestioneRisposta()){
	
							if(errorMessageAutenticazione!=null) {
								this.msgContext.setMessageResponse(errorMessageAutenticazione);
							}
							else {
							
								parametriGenerazioneBustaErrore.setBusta(bustaRichiesta);	
								if(erroreIntegrazione != null){
									parametriGenerazioneBustaErrore.setErroreIntegrazione(erroreIntegrazione);
								}
								else{
									parametriGenerazioneBustaErrore.setErroreCooperazione(erroreCooperazione);
								}
	
								OpenSPCoop2Message errorOpenSPCoopMsg = null;
								if(erroreCooperazione!=null){
									if(CodiceErroreCooperazione.SICUREZZA_TOKEN_AUTORIZZAZIONE_FALLITA.equals(erroreCooperazione.getCodiceErrore())) {
										if(integrationFunctionError==null) {
											integrationFunctionError = IntegrationFunctionError.TOKEN_REQUIRED_CLAIMS_NOT_FOUND;
										}
										parametriGenerazioneBustaErrore.setIntegrationFunctionError(integrationFunctionError);
										errorOpenSPCoopMsg = generaBustaErroreValidazione(parametriGenerazioneBustaErrore);
									}
									else {
										if(integrationFunctionError==null) {
											integrationFunctionError = IntegrationFunctionError.INTERNAL_REQUEST_ERROR;
										}
										parametriGenerazioneBustaErrore.setIntegrationFunctionError(integrationFunctionError);
										errorOpenSPCoopMsg = generaBustaErroreProcessamento(parametriGenerazioneBustaErrore,eAutenticazione);
									}
								}
								else {
									if(integrationFunctionError==null) {
										integrationFunctionError = IntegrationFunctionError.INTERNAL_REQUEST_ERROR;
									}
									parametriGenerazioneBustaErrore.setIntegrationFunctionError(integrationFunctionError);
									errorOpenSPCoopMsg = generaBustaErroreProcessamento(parametriGenerazioneBustaErrore,eAutenticazione);
								}	
								
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
								
								// Nota: la bustaRichiesta e' stata trasformata da generaErroreProcessamento
								parametriInvioBustaErrore.setOpenspcoopMsg(errorOpenSPCoopMsg);
								parametriInvioBustaErrore.setBusta(parametriGenerazioneBustaErrore.getBusta());
								sendRispostaBustaErrore(parametriInvioBustaErrore);
							}
	
						}
						
						openspcoopstate.releaseResource();
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
					
					// Tracciamento richiesta: non ancora registrata
					if(this.msgContext.isTracciamentoAbilitato()){
						EsitoElaborazioneMessaggioTracciato esitoTraccia = 
								EsitoElaborazioneMessaggioTracciato.getEsitoElaborazioneConErrore("GestoreAutenticazione.updateCredenziali, non riuscito: "+e.getMessage());
						tracciamento.registraRichiesta(requestMessage,null,soapHeaderElement,bustaRichiesta,esitoTraccia,
								Tracciamento.createLocationString(true,this.msgContext.getSourceLocation()),
								correlazioneApplicativa);
					}
					if(this.msgContext.isGestioneRisposta()){

						parametriGenerazioneBustaErrore.setBusta(bustaRichiesta);
						parametriGenerazioneBustaErrore.
							setErroreIntegrazione(ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
									get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_503_AUTENTICAZIONE));
						OpenSPCoop2Message errorOpenSPCoopMsg = generaBustaErroreProcessamento(parametriGenerazioneBustaErrore,e);
						
						// Nota: la bustaRichiesta e' stata trasformata da generaErroreProcessamento

						parametriInvioBustaErrore.setOpenspcoopMsg(errorOpenSPCoopMsg);
						parametriInvioBustaErrore.setBusta(parametriGenerazioneBustaErrore.getBusta());
						sendRispostaBustaErrore(parametriInvioBustaErrore);

					}
					openspcoopstate.releaseResource();
					return;
					
				}
				
			}


		}
		
		
		
		
		
		
		
		
	
		
		
		/*
		 * ---------------- Aggiornamento dati raccolti (PostAutenticazione) ---------------------
		 */
			
		Trasmissione trasmissioneSoggettoAutenticato = null; 
		if(soggettoAutenticato || soggettoFruitoreIdentificatoTramiteProtocollo){
			validatore.getBusta().setTipoMittente(soggettoFruitore.getTipo());
			validatore.getBusta().setMittente(soggettoFruitore.getNome());
			if(validatore.getBusta().sizeListaTrasmissioni()>0){
				for (int i = 0; i < validatore.getBusta().sizeListaTrasmissioni(); i++) {
					Trasmissione trasmissione = validatore.getBusta().getTrasmissione(i);
					if(trasmissione.getOrigine()==null && trasmissione.getTipoOrigine()==null && trasmissione.getIdentificativoPortaOrigine()==null){
						trasmissioneSoggettoAutenticato = trasmissione;
						trasmissione.setTipoOrigine(soggettoFruitore.getTipo());
						trasmissione.setOrigine(soggettoFruitore.getNome());
					}
				}
			}
			validatore.setMittente(soggettoFruitore);
			bustaRichiesta = validatore.getBusta();
			msgDiag.addKeywords(validatore.getBusta(), true);
		}
				
		// VM ProtocolInfo (se siamo arrivati da un canale VM)
		if(pddContext!=null && bustaRichiesta!=null)
			DirectVMProtocolInfo.setInfoFromContext(pddContext, bustaRichiesta);
			
		// Se non impostati, imposto i domini
		org.openspcoop2.pdd.core.Utilities.refreshIdentificativiPorta(bustaRichiesta, requestInfo.getIdentitaPdD(), registroServiziReader, protocolFactory);
		if(soggettoFruitore != null){
			if(soggettoFruitore.getCodicePorta()==null){
				soggettoFruitore.setCodicePorta(bustaRichiesta.getIdentificativoPortaMittente());
			}
			if(trasmissioneSoggettoAutenticato!=null && trasmissioneSoggettoAutenticato.getIdentificativoPortaOrigine()==null){
				trasmissioneSoggettoAutenticato.setIdentificativoPortaOrigine(bustaRichiesta.getIdentificativoPortaMittente());
			}
			msgDiag.setFruitore(soggettoFruitore);
		}
		if(idServizio!=null && idServizio.getSoggettoErogatore()!=null){
			if(idServizio.getSoggettoErogatore().getCodicePorta()==null){
				idServizio.getSoggettoErogatore().setCodicePorta(bustaRichiesta.getIdentificativoPortaDestinatario());
			}
		}
		
		if(servizioApplicativoFruitore!=null){
			// overriding busta
			bustaRichiesta.setServizioApplicativoFruitore(servizioApplicativoFruitore);
		}
		else{
			// altrimenti se è valorizzato internamente alla busta (poichè previsto dal protocollo (es. pdc)) e non capito tramite informazioni di integrazione uso quello
			if(bustaRichiesta.getServizioApplicativoFruitore()!=null){
				servizioApplicativoFruitore = bustaRichiesta.getServizioApplicativoFruitore();
			}
		}
		String idMessageRequest = bustaRichiesta.getID();

		TipoPdD tipoPorta = TipoPdD.APPLICATIVA;
		if(functionAsRouter)
			tipoPorta = TipoPdD.ROUTER;
		this.msgContext.getProtocol().setDominio(identitaPdD);
		this.msgContext.setIdentitaPdD(identitaPdD);
		this.msgContext.setTipoPorta(tipoPorta);
		this.msgContext.getProtocol().setFruitore(soggettoFruitore);
		if(bustaRichiesta!=null){
			this.msgContext.getProtocol().setIndirizzoFruitore(bustaRichiesta.getIndirizzoMittente());
		}
		if(idServizio!=null && idServizio.getSoggettoErogatore()!=null){
			this.msgContext.getProtocol().setErogatore(idServizio.getSoggettoErogatore());
		}
		if(bustaRichiesta!=null){
			this.msgContext.getProtocol().setIndirizzoErogatore(bustaRichiesta.getIndirizzoDestinatario());
		}
		if(idServizio!=null) {
			this.msgContext.getProtocol().setTipoServizio(idServizio.getTipo());
			this.msgContext.getProtocol().setServizio(idServizio.getNome());
			this.msgContext.getProtocol().setVersioneServizio(idServizio.getVersione());
			this.msgContext.getProtocol().setAzione(idServizio.getAzione());
		}
		this.msgContext.getProtocol().setIdRichiesta(idMessageRequest);
		this.msgContext.getProtocol().setProfiloCollaborazione(bustaRichiesta.getProfiloDiCollaborazione(),bustaRichiesta.getProfiloDiCollaborazioneValue());
		this.msgContext.getProtocol().setCollaborazione(bustaRichiesta.getCollaborazione());




		
		
		
		
		
		

		
		/*
		 * ---------------- Verifico che il servizio di RicezioneBuste sia abilitato ---------------------
		 */
		boolean serviceIsEnabled = false;
		boolean portaEnabled = false;
		Exception serviceIsEnabledExceptionProcessamento = null;
		try{
			serviceIsEnabled = StatoServiziPdD.isEnabledPortaApplicativa(soggettoFruitore, idServizio);
			if(serviceIsEnabled){
				// verifico la singola porta
				if(pa!=null){
					portaEnabled = configurazionePdDReader.isPortaAbilitata(pa);
				}
				else{
					portaEnabled = configurazionePdDReader.isPortaAbilitata(pd);
				}
			}
		}catch(Exception e){
			serviceIsEnabledExceptionProcessamento = e;
		}
		if (!serviceIsEnabled || !portaEnabled || serviceIsEnabledExceptionProcessamento!=null) {
			ErroreIntegrazione errore = null;
			String esito = null;
			IntegrationFunctionError integrationFunctionError = IntegrationFunctionError.API_SUSPEND;
			if(serviceIsEnabledExceptionProcessamento!=null){
				logCore.error("["+ RicezioneBuste.ID_MODULO+ "] Comprensione stato servizio di ricezione buste non riuscita: "+serviceIsEnabledExceptionProcessamento.getMessage(),serviceIsEnabledExceptionProcessamento);
				msgDiag.logErroreGenerico("Comprensione stato servizio di ricezione buste non riuscita", "PA");
				esito = "["+ RicezioneBuste.ID_MODULO+ "] Comprensione stato servizio di ricezione buste non riuscita";
				errore = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.getErroreIntegrazione();
				integrationFunctionError = IntegrationFunctionError.INTERNAL_REQUEST_ERROR;
			}else{
				
				pddContext.addObject(org.openspcoop2.core.constants.Costanti.ERRORE_SOSPENSIONE, "true");
				
				String msg = "Servizio di ricezione buste disabilitato";
				if(serviceIsEnabled){
					if(pa!=null){
						msg = "Porta Applicativa ["+pa.getNome()+"] disabilitata";
					}
					else{
						msg = "Porta Delegata ["+pd.getNome()+"] disabilitata";
					}
					errore = ErroriIntegrazione.ERRORE_446_PORTA_SOSPESA.getErroreIntegrazione();
				}
				else {
					errore = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
							get5XX_ErroreProcessamento(msg,CodiceErroreIntegrazione.CODICE_551_PA_SERVICE_NOT_ACTIVE);
				}
				logCore.error("["+ RicezioneBuste.ID_MODULO+ "] "+msg);
				msgDiag.logErroreGenerico(msg, "PA");
				esito = "["+ RicezioneBuste.ID_MODULO+ "] "+msg;
			}
			// Tracciamento richiesta: non ancora registrata
			if(this.msgContext.isTracciamentoAbilitato()){
				EsitoElaborazioneMessaggioTracciato esitoTraccia = 
						EsitoElaborazioneMessaggioTracciato.getEsitoElaborazioneConErrore(esito);
				tracciamento.registraRichiesta(requestMessage,null,soapHeaderElement,bustaRichiesta,esitoTraccia,
						Tracciamento.createLocationString(true,this.msgContext.getSourceLocation()),
						correlazioneApplicativa);
			}
			if(this.msgContext.isGestioneRisposta()){

				parametriGenerazioneBustaErrore.setBusta(bustaRichiesta);							
				parametriGenerazioneBustaErrore.setErroreIntegrazione(errore);
				parametriGenerazioneBustaErrore.setIntegrationFunctionError(integrationFunctionError);

				OpenSPCoop2Message errorOpenSPCoopMsg = generaBustaErroreProcessamento(parametriGenerazioneBustaErrore,serviceIsEnabledExceptionProcessamento);
				
				if(IntegrationFunctionError.API_SUSPEND.equals(integrationFunctionError) &&
						propertiesReader.isEnabledServiceUnavailableRetryAfter_pa_suspend() && 
						propertiesReader.getServiceUnavailableRetryAfterSeconds_pa_suspend()!=null &&
						propertiesReader.getServiceUnavailableRetryAfterSeconds_pa_suspend()>0) {
					int seconds = propertiesReader.getServiceUnavailableRetryAfterSeconds_pa_suspend();
					if(propertiesReader.getServiceUnavailableRetryAfterSeconds_randomBackoff_pa_suspend()!=null &&
							propertiesReader.getServiceUnavailableRetryAfterSeconds_randomBackoff_pa_suspend()>0) {
						seconds = seconds + new Random().nextInt(propertiesReader.getServiceUnavailableRetryAfterSeconds_randomBackoff_pa_suspend());
					}
					errorOpenSPCoopMsg.forceTransportHeader(HttpConstants.RETRY_AFTER, seconds+"");
				}
				
				// Nota: la bustaRichiesta e' stata trasformata da generaErroreProcessamento
				parametriInvioBustaErrore.setOpenspcoopMsg(errorOpenSPCoopMsg);
				parametriInvioBustaErrore.setBusta(parametriGenerazioneBustaErrore.getBusta());
				sendRispostaBustaErrore(parametriInvioBustaErrore);

			}
			openspcoopstate.releaseResource();
			return;
		}
		
		
		
		
		
		
		/* -------- OpenSPCoop2Message Update ------------- */
		try {
			msgDiag.mediumDebug("Aggiornamento del messaggio");
			requestMessage = protocolFactory.createProtocolManager().updateOpenSPCoop2MessageRequest(requestMessage, bustaRichiesta,
					protocolFactory.getCachedRegistryReader(openspcoopstate.getStatoRichiesta()));
		} catch (Exception e) {
			// Emetto log, non ancora emesso
			if(validatore.getBusta()!=null && 
					(validatore.getBusta().getMittente()!=null || validatore.getBusta().getTipoMittente()!=null)){
				msgDiag.logPersonalizzato("ricezioneMessaggio");
			}
			else{
				msgDiag.logPersonalizzato("ricezioneMessaggio.mittenteAnonimo");
			}
			
			msgDiag.addKeywordErroreProcessamento(e,"Aggiornamento messaggio fallito");
			msgDiag.logErroreGenerico(e,"ProtocolManager.updateOpenSPCoop2Message");
			logCore.error("ProtocolManager.updateOpenSPCoop2Message error: "+e.getMessage(),e);
			
			// Tracciamento richiesta: non ancora registrata
			if(this.msgContext.isTracciamentoAbilitato()){
				EsitoElaborazioneMessaggioTracciato esitoTraccia = 
						EsitoElaborazioneMessaggioTracciato.getEsitoElaborazioneConErrore("ProtocolManager.updateOpenSPCoop2Message, non riuscito: "+e.getMessage());
				tracciamento.registraRichiesta(requestMessage,null,soapHeaderElement,bustaRichiesta,esitoTraccia,
						Tracciamento.createLocationString(true,this.msgContext.getSourceLocation()),
						correlazioneApplicativa);
			}
			if(this.msgContext.isGestioneRisposta()){

				parametriGenerazioneBustaErrore.setBusta(bustaRichiesta);
				parametriGenerazioneBustaErrore.
					setErroreIntegrazione(ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
							get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_525_GESTIONE_FUNZIONALITA_PROTOCOLLO));
				OpenSPCoop2Message errorOpenSPCoopMsg = generaBustaErroreProcessamento(parametriGenerazioneBustaErrore,e);
				
				// Nota: la bustaRichiesta e' stata trasformata da generaErroreProcessamento

				parametriInvioBustaErrore.setOpenspcoopMsg(errorOpenSPCoopMsg);
				parametriInvioBustaErrore.setBusta(parametriGenerazioneBustaErrore.getBusta());
				sendRispostaBustaErrore(parametriInvioBustaErrore);

			}
			openspcoopstate.releaseResource();
			return;
		}
		
		
		
		
		
		
		

		
		// Imposto un context di Base (utilizzato anche  per la successiva spedizione della risposta)
		MessageSecurityContext messageSecurityContext = null;
		// Proprieta' FlowParameter MTOM / Security relative alla ricezione della busta
		FlowProperties flowPropertiesRequest = null;
		// Proprieta' FlowParameter MTOM / Security relative alla spedizione della risposta
		FlowProperties flowPropertiesResponse = null;

		
		// Modalita' gestione risposta (Sincrona/Fault/Ricevute...)
		// Per i profili diversi dal sincrono e' possibile impostare dove far ritornare l'errore
		boolean newConnectionForResponse = false; 
		boolean utilizzoIndirizzoTelematico = false;		
		if(functionAsRouter==false){

			// Calcolo newConnectionForResponse in caso di asincroni
			if( org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.ASINCRONO_ASIMMETRICO.equals(bustaRichiesta.getProfiloDiCollaborazione()) ||
					org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.ASINCRONO_SIMMETRICO.equals(bustaRichiesta.getProfiloDiCollaborazione())	){
				newConnectionForResponse = configurazionePdDReader.newConnectionForResponse();
				utilizzoIndirizzoTelematico = configurazionePdDReader.isUtilizzoIndirizzoTelematico();
				parametriInvioBustaErrore.setNewConnectionForResponse(newConnectionForResponse);
				parametriInvioBustaErrore.setUtilizzoIndirizzoTelematico(utilizzoIndirizzoTelematico);
			}


			/* ------------
			   Controllo effettuato in caso di ricezione di una risposta Asincrona Simmetrica, o richiesta-stato Asincrona Asimmetrica
			   In questo caso, se e' ancora in corso la gestione della richiesta o della ricevuta alla richiesta, 
			   prima di procedere con la gestione della risposta/richiesta-stato, devono termiare le precedenti richieste/ricevute.
			   ------------
			 */
			if(validatore.isErroreProtocollo()==false && validatore.isBustaDiServizio()==false){
				try{
					long scadenzaControllo = DateManager.getTimeMillis() + propertiesReader.getTimeoutBustaRispostaAsincrona();
					int checkIntervalControllo = propertiesReader.getCheckIntervalBustaRispostaAsincrona();
					boolean attendiTerminazioneRichiesta = 
						this.gestioneRispostaAsincrona_checkPresenzaRichiesta(scadenzaControllo,checkIntervalControllo, bustaRichiesta, 
								openspcoopstate, msgDiag, newConnectionForResponse, inRequestContext.getPddContext());
					boolean attendiTerminazioneRicevutaRichiesta = 
						this.gestioneRispostaAsincrona_checkPresenzaRicevutaRichiesta(scadenzaControllo,checkIntervalControllo, bustaRichiesta, 
								openspcoopstate, msgDiag, newConnectionForResponse, inRequestContext.getPddContext());

					ErroreIntegrazione msgErroreIntegrazione = null;
					String motivoErrore = null;
					if(attendiTerminazioneRichiesta){
						msgDiag.logPersonalizzato("attesaFineProcessamento.richiestaAsincrona.timeoutScaduto");
						msgErroreIntegrazione = ErroriIntegrazione.ERRORE_538_RICHIESTA_ASINCRONA_ANCORA_IN_PROCESSAMENTO.getErroreIntegrazione();
					}else if(attendiTerminazioneRicevutaRichiesta){
						msgDiag.logPersonalizzato("attesaFineProcessamento.ricevutaRichiestaAsincrona.timeoutScaduto");
						msgErroreIntegrazione = ErroriIntegrazione.ERRORE_539_RICEVUTA_RICHIESTA_ASINCRONA_ANCORA_IN_PROCESSAMENTO.getErroreIntegrazione();
					}
					if(attendiTerminazioneRichiesta || attendiTerminazioneRicevutaRichiesta){
						// Tracciamento richiesta: non ancora registrata
						if(this.msgContext.isTracciamentoAbilitato()){
							EsitoElaborazioneMessaggioTracciato esitoTraccia = 
									EsitoElaborazioneMessaggioTracciato.getEsitoElaborazioneConErrore(motivoErrore);
							tracciamento.registraRichiesta(requestMessage,null,soapHeaderElement,bustaRichiesta,esitoTraccia,
									Tracciamento.createLocationString(true,this.msgContext.getSourceLocation()),
									correlazioneApplicativa);
						}
						if(this.msgContext.isGestioneRisposta()){

							parametriGenerazioneBustaErrore.setBusta(bustaRichiesta);
							parametriGenerazioneBustaErrore.setErroreIntegrazione(msgErroreIntegrazione);

							OpenSPCoop2Message errorOpenSPCoopMsg = generaBustaErroreProcessamento(parametriGenerazioneBustaErrore,null);
							
							// Nota: la bustaRichiesta e' stata trasformata da generaErroreProcessamento
							parametriInvioBustaErrore.setOpenspcoopMsg(errorOpenSPCoopMsg);
							parametriInvioBustaErrore.setBusta(parametriGenerazioneBustaErrore.getBusta());
							sendRispostaBustaErrore(parametriInvioBustaErrore);

						}
						openspcoopstate.releaseResource();
						return;
					}

				}catch(Exception e){

					// Emetto log, non ancora emesso
					if(validatore.getBusta()!=null && 
							(validatore.getBusta().getMittente()!=null || validatore.getBusta().getTipoMittente()!=null)){
						msgDiag.logPersonalizzato("ricezioneMessaggio");
					}
					else{
						msgDiag.logPersonalizzato("ricezioneMessaggio.mittenteAnonimo");
					}
					
					msgDiag.logErroreGenerico(e,"checkPresenzaRichiestaRicevutaAsincronaAncoraInGestione");
					logCore.error("Controllo presenza richieste/ricevuteRichieste ancora in gestione " +
							"correlate alla risposta/richiesta-stato asincrona simmetrica/asimmetrica arrivata, non riuscito",e);
					// Tracciamento richiesta: non ancora registrata
					if(this.msgContext.isTracciamentoAbilitato()){
						EsitoElaborazioneMessaggioTracciato esitoTraccia = 
								EsitoElaborazioneMessaggioTracciato.getEsitoElaborazioneConErrore("Controllo presenza richieste/ricevuteRichieste ancora in gestione " +
										"correlate alla risposta/richiesta-stato asincrona simmetrica/asimmetrica arrivata, non riuscito: "+e.getMessage());
						tracciamento.registraRichiesta(requestMessage,null,soapHeaderElement,bustaRichiesta,esitoTraccia,
								Tracciamento.createLocationString(true,this.msgContext.getSourceLocation()),
								correlazioneApplicativa);
					}
					if(this.msgContext.isGestioneRisposta()){

						parametriGenerazioneBustaErrore.setBusta(bustaRichiesta);
						parametriGenerazioneBustaErrore.
							setErroreIntegrazione(ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
									get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_536_CONFIGURAZIONE_NON_DISPONIBILE));
						OpenSPCoop2Message errorOpenSPCoopMsg = generaBustaErroreProcessamento(parametriGenerazioneBustaErrore,e);
						
						// Nota: la bustaRichiesta e' stata trasformata da generaErroreProcessamento

						parametriInvioBustaErrore.setOpenspcoopMsg(errorOpenSPCoopMsg);
						parametriInvioBustaErrore.setBusta(parametriGenerazioneBustaErrore.getBusta());
						sendRispostaBustaErrore(parametriInvioBustaErrore);

					}
					openspcoopstate.releaseResource();
					return;
				}
			}
			
			/* ----------- Scenario Cooperazione ------------ */
			if(ruoloBustaRicevuta!=null){
				try{
					String scenarioCooperazione = null;
					if(org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.ONEWAY.equals(bustaRichiesta.getProfiloDiCollaborazione())) {	
						scenarioCooperazione = Costanti.SCENARIO_ONEWAY_INVOCAZIONE_SERVIZIO;
					}
					else if(org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.SINCRONO.equals(bustaRichiesta.getProfiloDiCollaborazione())) {	
						scenarioCooperazione = Costanti.SCENARIO_SINCRONO_INVOCAZIONE_SERVIZIO;
					}
					else if(org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.ASINCRONO_SIMMETRICO.equals(bustaRichiesta.getProfiloDiCollaborazione())) {	
						if(RuoloBusta.RICHIESTA.equals(ruoloBustaRicevuta.toString())){
							scenarioCooperazione = Costanti.SCENARIO_ASINCRONO_SIMMETRICO_INVOCAZIONE_SERVIZIO;
						}
						else if(RuoloBusta.RISPOSTA.equals(ruoloBustaRicevuta.toString())){
							scenarioCooperazione = Costanti.SCENARIO_ASINCRONO_SIMMETRICO_CONSEGNA_RISPOSTA;
						}
						else{
							// sono ricevute asincrone
						}
					}
					else if(org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.ASINCRONO_ASIMMETRICO.equals(bustaRichiesta.getProfiloDiCollaborazione())) {	
						if(RuoloBusta.RICHIESTA.equals(ruoloBustaRicevuta.toString())){
							scenarioCooperazione = Costanti.SCENARIO_ASINCRONO_ASIMMETRICO_INVOCAZIONE_SERVIZIO;
						}
						else if(RuoloBusta.RISPOSTA.equals(ruoloBustaRicevuta.toString())){
							scenarioCooperazione = Costanti.SCENARIO_ASINCRONO_ASIMMETRICO_POLLING;
						}
						else{
							// sono ricevute asincrone
						}
					}
					this.msgContext.getProtocol().setScenarioCooperazione(scenarioCooperazione);
				}catch(Exception e){
					msgDiag.logErroreGenerico(e,"setScenarioCooperazione");
				}
			}

		}



		
		
		
		
		
		
		
		
		/* -------------------------- Implementazione PdD Soggetti busta -------------------------------*/
		String implementazionePdDMittente = null;
		String implementazionePdDDestinatario = null;
		String idPdDMittente = null;
		String idPdDDestinatario = null;
		msgDiag.mediumDebug("Ricerca implementazione della porta di dominio dei soggetti...");
		ProprietaManifestAttachments proprietaManifestAttachments = null;
		boolean validazioneIDBustaCompleta = true;
		try{
			if(soggettoFruitore!=null){
				implementazionePdDMittente = registroServiziReader.getImplementazionePdD(soggettoFruitore, null);
			}
			if(idServizio!=null && idServizio.getSoggettoErogatore()!=null){
				implementazionePdDDestinatario = registroServiziReader.getImplementazionePdD(idServizio.getSoggettoErogatore(), null);
			}
			if(soggettoFruitore!=null){
				idPdDMittente = registroServiziReader.getIdPortaDominio(soggettoFruitore, null);
			}
			if(idServizio!=null && idServizio.getSoggettoErogatore()!=null){
				idPdDDestinatario = registroServiziReader.getIdPortaDominio(idServizio.getSoggettoErogatore(), null);
			}
			parametriGenerazioneBustaErrore.setImplementazionePdDMittente(implementazionePdDMittente);
			parametriGenerazioneBustaErrore.setImplementazionePdDDestinatario(implementazionePdDDestinatario);
			parametriInvioBustaErrore.setImplementazionePdDMittente(implementazionePdDMittente);
			parametriInvioBustaErrore.setImplementazionePdDDestinatario(implementazionePdDDestinatario);
			
			properties.setValidazioneConSchema(configurazionePdDReader.isLivelloValidazioneRigido(implementazionePdDMittente));
			properties.setValidazioneProfiloCollaborazione(configurazionePdDReader.isValidazioneProfiloCollaborazione(implementazionePdDMittente));
			if(openspcoopstate!=null) {
				properties.setRuntimeState(openspcoopstate.getStatoRichiesta());
				if(propertiesReader.isTransazioniUsePddRuntimeDatasource()) {
					properties.setTracceState(openspcoopstate.getStatoRichiesta());
				}
			}
			validatore.setProprietaValidazione(properties); // update
			
			MessageSecurityContextParameters contextParameters = new MessageSecurityContextParameters();
			contextParameters.setUseActorDefaultIfNotDefined(propertiesReader.isGenerazioneActorDefault(implementazionePdDMittente));
			contextParameters.setActorDefault(propertiesReader.getActorDefault(implementazionePdDMittente));
			contextParameters.setLog(logCore);
			contextParameters.setFunctionAsClient(SecurityConstants.SECURITY_SERVER);
			contextParameters.setPrefixWsuId(propertiesReader.getPrefixWsuId());
			contextParameters.setRemoveAllWsuIdRef(propertiesReader.isRemoveAllWsuIdRef());
			contextParameters.setIdFruitore(soggettoFruitore);
			contextParameters.setPddFruitore(idPdDMittente);
			contextParameters.setIdServizio(idServizio);
			contextParameters.setPddErogatore(idPdDDestinatario);
			messageSecurityContext = new MessageSecurityFactory().getMessageSecurityContext(contextParameters);
			parametriGenerazioneBustaErrore.setMessageSecurityContext(messageSecurityContext);
			
			proprietaManifestAttachments = propertiesReader.getProprietaManifestAttachments(implementazionePdDMittente);
			readQualifiedAttribute = propertiesReader.isReadQualifiedAttribute(implementazionePdDMittente);
			validazioneIDBustaCompleta = propertiesReader.isValidazioneIDBustaCompleta(implementazionePdDMittente);
			
		}catch(Exception e){
			msgDiag.logErroreGenerico(e,"ricercaImplementazionePdDSoggettiBusta");
			// Tracciamento richiesta: non ancora registrata
			if(this.msgContext.isTracciamentoAbilitato()){
				EsitoElaborazioneMessaggioTracciato esitoTraccia = 
						EsitoElaborazioneMessaggioTracciato.getEsitoElaborazioneConErrore("Errore durante la ricerca dell'implementazione della porta di dominio dei soggetti: "+e.getMessage());
				tracciamento.registraRichiesta(requestMessage,null,soapHeaderElement,bustaRichiesta,esitoTraccia,
						Tracciamento.createLocationString(true,this.msgContext.getSourceLocation()),
						correlazioneApplicativa);
			}
			if(this.msgContext.isGestioneRisposta()){
				
				parametriGenerazioneBustaErrore.setBusta(bustaRichiesta);
				parametriGenerazioneBustaErrore.
				setErroreIntegrazione(ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
						get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_536_CONFIGURAZIONE_NON_DISPONIBILE));
				OpenSPCoop2Message errorOpenSPCoopMsg = generaBustaErroreProcessamento(parametriGenerazioneBustaErrore,e);
				
				// Nota: la bustaRichiesta e' stata trasformata da generaErroreProcessamento
				parametriInvioBustaErrore.setOpenspcoopMsg(errorOpenSPCoopMsg);
				parametriInvioBustaErrore.setBusta(parametriGenerazioneBustaErrore.getBusta());
				sendRispostaBustaErrore(parametriInvioBustaErrore);
			}
			openspcoopstate.releaseResource();
			return;
		}
		if(soggettoFruitore!=null){
			msgDiag.mediumDebug("ImplementazionePdD soggetto fruitore ("+soggettoFruitore.toString()+"): ["+implementazionePdDMittente+"]");
		}
		if(idServizio!=null && idServizio.getSoggettoErogatore()!=null){
			msgDiag.mediumDebug("ImplementazionePdD soggetto erogatore ("+idServizio.getSoggettoErogatore().toString()+"): ["+implementazionePdDDestinatario+"]");
		}
		
		
		
		
		
		
		




		
		/* -------- Manifest attachments ------------- */
		// per profili asincroni
		if(functionAsRouter==false){
			msgDiag.mediumDebug("Lettura manifest attachments impostato in porta applicativa/delegata...");
			try{

				// Validazione manifest attachments
				msgDiag.highDebug("Lettura porta applicativa/delegata (Set)...");
				if(pa!=null){
					properties.setValidazioneManifestAttachments(
							configurazionePdDReader.isValidazioneManifestAttachments(implementazionePdDMittente) &&
							configurazionePdDReader.isGestioneManifestAttachments(pa,protocolFactory));
				}
				else if(pd!=null){
					properties.setValidazioneManifestAttachments(
							configurazionePdDReader.isValidazioneManifestAttachments(implementazionePdDDestinatario) &&
							configurazionePdDReader.isGestioneManifestAttachments(pd,protocolFactory));
				}
				else{
					properties.setValidazioneManifestAttachments(
							configurazionePdDReader.isValidazioneManifestAttachments(implementazionePdDMittente));
				}
				
				msgDiag.highDebug("Lettura porta applicativa/delegata terminato");
				
			}catch(Exception e){
				if(  !(e instanceof DriverConfigurazioneNotFound) ) {
					msgDiag.logErroreGenerico(e,"letturaPortaManifestAttachments");
					
					// Tracciamento richiesta: non ancora registrata
					if(this.msgContext.isTracciamentoAbilitato()){
						EsitoElaborazioneMessaggioTracciato esitoTraccia = 
								EsitoElaborazioneMessaggioTracciato.getEsitoElaborazioneConErrore("Errore durante la lettura della porta applicativa/delegata per la gestione ManifestAttachments: "+e.getMessage());
						tracciamento.registraRichiesta(requestMessage,null,soapHeaderElement,bustaRichiesta,esitoTraccia,
								Tracciamento.createLocationString(true,this.msgContext.getSourceLocation()),
								correlazioneApplicativa);
					}
					if(this.msgContext.isGestioneRisposta()){
						
						parametriGenerazioneBustaErrore.setBusta(bustaRichiesta);
						parametriGenerazioneBustaErrore.
						setErroreIntegrazione(ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
								get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_536_CONFIGURAZIONE_NON_DISPONIBILE));
						OpenSPCoop2Message errorOpenSPCoopMsg = generaBustaErroreProcessamento(parametriGenerazioneBustaErrore,e);
						
						// Nota: la bustaRichiesta e' stata trasformata da generaErroreProcessamento

						parametriInvioBustaErrore.setOpenspcoopMsg(errorOpenSPCoopMsg);
						parametriInvioBustaErrore.setBusta(parametriGenerazioneBustaErrore.getBusta());
						sendRispostaBustaErrore(parametriInvioBustaErrore);

					}
					openspcoopstate.releaseResource();
					return;
				}
			}
			msgDiag.highDebug("Lettura manifest attachments impostato in porta applicativa/delegata terminato");
		}

		






		
		
		
		// Configurazione Richiesta Applicativa
		String idModuloInAttesa = null;
		if(this.msgContext.isGestioneRisposta())
			idModuloInAttesa = this.msgContext.getIdModulo();
		RichiestaApplicativa richiestaApplicativa = null;
		if(idPA!=null) {
			richiestaApplicativa = new RichiestaApplicativa(soggettoFruitore,
					idModuloInAttesa,identitaPdD,idPA); 
		}
		else {
			// Scenario Asincrono Simmetrico - Risposta
			richiestaApplicativa = new RichiestaApplicativa(soggettoFruitore,
					idModuloInAttesa,identitaPdD,idServizio); 
		}
		richiestaApplicativa.setFiltroProprietaPorteApplicative(this.msgContext.getProprietaFiltroPortaApplicativa());
		
		
		


		
		
		
		
		
		// ------------- Controllo funzionalita di protocollo richieste siano compatibili con il protocollo -----------------------------
		try{
			// NOTA: Usare getIntegrationServiceBinding poichè le funzionalità si riferiscono al tipo di integrazione scelta
			
			IProtocolConfiguration protocolConfiguration = protocolFactory.createProtocolConfiguration();
			if(bustaRichiesta.getProfiloDiCollaborazione()!=null && 
					!org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.UNKNOWN.equals(bustaRichiesta.getProfiloDiCollaborazione()) ){
				if(protocolConfiguration.isSupportato(requestInfo.getIntegrationServiceBinding(),bustaRichiesta.getProfiloDiCollaborazione())==false){
					throw new Exception("Profilo di Collaborazione ["+bustaRichiesta.getProfiloDiCollaborazione().getEngineValue()+"]");
				}
			}
			// NOTA:  FiltroDuplicati, consegnaAffidabile, idCollaborazione, consegnaInOrdine verificato in sbustamento.
			if(bustaRichiesta.getScadenza()!=null){
				if(protocolConfiguration.isSupportato(requestInfo.getIntegrationServiceBinding(),FunzionalitaProtocollo.SCADENZA)==false){
					throw new Exception(FunzionalitaProtocollo.SCADENZA.getEngineValue());
				}
			}
			
			if(configurazionePdDReader.isGestioneManifestAttachments(pa,protocolFactory)){
				if(protocolConfiguration.isSupportato(requestInfo.getIntegrationServiceBinding(),FunzionalitaProtocollo.MANIFEST_ATTACHMENTS)==false){
					throw new Exception(FunzionalitaProtocollo.MANIFEST_ATTACHMENTS.getEngineValue());
				}
			}			
		}catch(Exception e){	
			msgDiag.addKeywordErroreProcessamento(e);
			msgDiag.logPersonalizzato(MsgDiagnosticiProperties.MSG_DIAG_SBUSTAMENTO,"protocolli.funzionalita.unsupported");
			// Tracciamento richiesta: non ancora registrata
			if(this.msgContext.isTracciamentoAbilitato()){
				EsitoElaborazioneMessaggioTracciato esitoTraccia = 
						EsitoElaborazioneMessaggioTracciato.getEsitoElaborazioneConErrore(msgDiag.getMessaggio_replaceKeywords("protocolli.funzionalita.unsupported"));
				tracciamento.registraRichiesta(requestMessage,null,soapHeaderElement,bustaRichiesta,esitoTraccia,
						Tracciamento.createLocationString(true,this.msgContext.getSourceLocation()),
						correlazioneApplicativa);
			}
			if(this.msgContext.isGestioneRisposta()){
				
				parametriGenerazioneBustaErrore.setIntegrationFunctionError(IntegrationFunctionError.NOT_SUPPORTED_BY_PROTOCOL);
				parametriGenerazioneBustaErrore.setBusta(bustaRichiesta);
				parametriGenerazioneBustaErrore.
				setErroreIntegrazione(ErroriIntegrazione.ERRORE_439_FUNZIONALITA_NOT_SUPPORTED_BY_PROTOCOL.
						getErrore439_FunzionalitaNotSupportedByProtocol(e.getMessage(), protocolFactory));
				OpenSPCoop2Message errorOpenSPCoopMsg = generaBustaErroreProcessamento(parametriGenerazioneBustaErrore,e);
				
				// Nota: la bustaRichiesta e' stata trasformata da generaErroreProcessamento

				parametriInvioBustaErrore.setOpenspcoopMsg(errorOpenSPCoopMsg);
				parametriInvioBustaErrore.setBusta(parametriGenerazioneBustaErrore.getBusta());
				sendRispostaBustaErrore(parametriInvioBustaErrore);

			}
			openspcoopstate.releaseResource();
			return;
		}
		
		


	
		




		/* ------------- Modalita' di gestione ---------------------------- */
		//Versione OneWay 
		boolean oneWayStateless = false;
		boolean oneWayVersione11 = false;
		// Versione Sincrono 
		boolean sincronoStateless = false;
		// Asincrono stateless
		boolean asincronoStateless = false;

		// Gestione stateless
		boolean portaStateless = false; // vero se almeno uno dei 3 precedenti e' vero

		// Routing stateless
		boolean routingStateless = false;

		try {
			
			if(functionAsRouter == false){
				
				if(propertiesReader.isServerJ2EE()==false){
					// Stateless obbligatorio in server di tipo web (non j2ee)
					oneWayStateless = true;
					sincronoStateless = true;
					asincronoStateless = true;
				}
				else if(org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.ONEWAY.equals(bustaRichiesta.getProfiloDiCollaborazione())){
					if(soggettoVirtuale==false) {
						oneWayStateless = configurazionePdDReader.isModalitaStateless(pa,bustaRichiesta.getProfiloDiCollaborazione());
					}
				}
				else if(org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.SINCRONO.equals(bustaRichiesta.getProfiloDiCollaborazione())){	
					sincronoStateless = configurazionePdDReader.isModalitaStateless(pa,bustaRichiesta.getProfiloDiCollaborazione());
				}else if(org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.ASINCRONO_SIMMETRICO.equals(bustaRichiesta.getProfiloDiCollaborazione()) ||
						org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.ASINCRONO_ASIMMETRICO.equals(bustaRichiesta.getProfiloDiCollaborazione())){
					// Le ricevute arrivano solo con connection-new (possibile solo con stateful)
					if( (RuoloBusta.RICEVUTA_RICHIESTA.equals(ruoloBustaRicevuta.toString())==false) &&
							(RuoloBusta.RICEVUTA_RISPOSTA.equals(ruoloBustaRicevuta.toString())==false)){
						if(org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.ASINCRONO_SIMMETRICO.equals(bustaRichiesta.getProfiloDiCollaborazione()) && 
								pd!=null){
							// Risposta
							asincronoStateless = configurazionePdDReader.isModalitaStateless(pd, bustaRichiesta.getProfiloDiCollaborazione());
						}else{
							asincronoStateless = configurazionePdDReader.isModalitaStateless(pa, bustaRichiesta.getProfiloDiCollaborazione());
						}
					}
				}

				oneWayVersione11 = propertiesReader.isGestioneOnewayStateful_1_1() &&  
				org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.ONEWAY.equals(bustaRichiesta.getProfiloDiCollaborazione())&& !oneWayStateless ; 

				if ( oneWayStateless || sincronoStateless || asincronoStateless || oneWayVersione11 ) {
					portaStateless = true;		
					if(oneWayVersione11==false){
						this.msgContext.getIntegrazione().setGestioneStateless(true);
					}else{
						this.msgContext.getIntegrazione().setGestioneStateless(false);
					}
				}else{
					this.msgContext.getIntegrazione().setGestioneStateless(false);
				}
			}else{
				routingStateless = CostantiConfigurazione.ABILITATO.equals(propertiesReader.getStatelessRouting());
				this.msgContext.getIntegrazione().setGestioneStateless(routingStateless);
			}
			
			parametriInvioBustaErrore.setOnewayVersione11(oneWayVersione11);
			
		} catch (Exception e) {
			msgDiag.logErroreGenerico(e,"AnalisiModalitaGestioneStatefulStateless");
			logCore.error("Analisi modalita di gestione STATEFUL/STATELESS non riuscita: "	+ e);
			if (this.msgContext.isGestioneRisposta()) {
				
				parametriGenerazioneBustaErrore.setBusta(bustaRichiesta);
				parametriGenerazioneBustaErrore.setErroreIntegrazione(ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
						get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_536_CONFIGURAZIONE_NON_DISPONIBILE));
				OpenSPCoop2Message errorOpenSPCoopMsg = generaBustaErroreProcessamento(parametriGenerazioneBustaErrore,e);
				
				// Nota: la bustaRichiesta e' stata trasformata da generaErroreProcessamento

				parametriInvioBustaErrore.setOpenspcoopMsg(errorOpenSPCoopMsg);
				parametriInvioBustaErrore.setBusta(parametriGenerazioneBustaErrore.getBusta());
				sendRispostaBustaErrore(parametriInvioBustaErrore);

			}
			openspcoopstate.releaseResource();
			return;
		}


		// UPDATE: Modalita' gestione risposta (Sincrona/Fault/Ricevute...)
		// Per i profili diversi dal sincrono e' possibile impostare dove far ritornare l'errore
		if((org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.SINCRONO.equals(bustaRichiesta.getProfiloDiCollaborazione())==false) &&
				(oneWayStateless==false) && (sincronoStateless==false) && (asincronoStateless==false) && (routingStateless==false) ){
			// New connection puo' essere usata solo se non siamo in modalita stateless
			newConnectionForResponse = configurazionePdDReader.newConnectionForResponse();
		}
		parametriInvioBustaErrore.setNewConnectionForResponse(newConnectionForResponse);
		if( (oneWayStateless==false) && (sincronoStateless==false) && (asincronoStateless==false) && (routingStateless==false)){
			// New connection puo' essere usata solo se non siamo in modalita stateless
			utilizzoIndirizzoTelematico = configurazionePdDReader.isUtilizzoIndirizzoTelematico();
		}
		parametriInvioBustaErrore.setUtilizzoIndirizzoTelematico(utilizzoIndirizzoTelematico);

		/* --------- Rilascio connessione se agisco come router stateless --------------- */
		if(routingStateless){
			openspcoopstate.releaseResource();
		} 

		Integrazione infoIntegrazione = new Integrazione();
		infoIntegrazione.setIdModuloInAttesa(this.msgContext.getIdModulo());
		infoIntegrazione.setStateless(portaStateless);
		parametriGenerazioneBustaErrore.setIntegrazione(infoIntegrazione);




		


		

		
		
		
		
		
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
			if (this.msgContext.isGestioneRisposta()) {
				
				if(erroreIntegrazione==null){
					erroreIntegrazione = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
							get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_558_HANDLER_IN_PROTOCOL_REQUEST);
				}
				if(integrationFunctionError==null) {
					integrationFunctionError = IntegrationFunctionError.INTERNAL_REQUEST_ERROR;
				}
				
				parametriGenerazioneBustaErrore.setIntegrationFunctionError(integrationFunctionError);
				parametriGenerazioneBustaErrore.setBusta(bustaRichiesta);
				parametriGenerazioneBustaErrore.setErroreIntegrazione(erroreIntegrazione);
				OpenSPCoop2Message errorMsg = generaBustaErroreProcessamento(parametriGenerazioneBustaErrore,e);
				if(e instanceof HandlerException){
					HandlerException he = (HandlerException) e;
					he.customized(errorMsg);
				}
				
				// Nota: la bustaRichiesta e' stata trasformata da generaErroreProcessamento
				parametriInvioBustaErrore.setOpenspcoopMsg(errorMsg);
				parametriInvioBustaErrore.setBusta(parametriGenerazioneBustaErrore.getBusta());
				sendRispostaBustaErrore(parametriInvioBustaErrore);
			}
			openspcoopstate.releaseResource();
			return;
		}
		
		
		
		
		
		
		
		
		

		// Validazione Semantica (se modalita router non attiva)
		if(functionAsRouter==false){


			/* ----------- Raccolta FlowParameter MTOM / Security ------------ */
			msgDiag.mediumDebug("Raccolta FlowParameter MTOM / Security proprieta...");
			MTOMProcessor mtomProcessor = null;
			try{
				
				// read flow Properties
				flowPropertiesRequest = this.getFlowPropertiesRequest(requestMessage, bustaRichiesta, configurazionePdDReader, 
						((StateMessage)openspcoopstate.getStatoRichiesta()), msgDiag,logCore,propertiesReader,
						ruoloBustaRicevuta,implementazionePdDMittente,requestInfo, inRequestContext.getPddContext(),pa);				
				flowPropertiesResponse = this.getFlowPropertiesResponse(requestMessage, bustaRichiesta, configurazionePdDReader, 
						((StateMessage)openspcoopstate.getStatoRichiesta()), msgDiag,logCore,propertiesReader,
						ruoloBustaRicevuta,implementazionePdDMittente,requestInfo, inRequestContext.getPddContext(),pa);
				parametriGenerazioneBustaErrore.setFlowPropertiesResponse(flowPropertiesResponse);
				
				// init message security context
				if(flowPropertiesRequest!=null && flowPropertiesRequest.messageSecurity!=null && 
						flowPropertiesRequest.messageSecurity.getFlowParameters()!=null){
					if(flowPropertiesRequest.messageSecurity.getFlowParameters().size() > 0){
						messageSecurityContext.setIncomingProperties(flowPropertiesRequest.messageSecurity.getFlowParameters());
					}
				}
				
				// init mtom processor
				mtomProcessor = new MTOMProcessor(flowPropertiesRequest.mtom, flowPropertiesRequest.messageSecurity, 
						tipoPorta, msgDiag, logCore, pddContext);
				
				msgDiag.mediumDebug("Raccolta FlowParameter MTOM / Security completata con successo");
				
			}catch(Exception e){

				// Emetto log, non ancora emesso
				if(validatore.getBusta()!=null && 
						(validatore.getBusta().getMittente()!=null || validatore.getBusta().getTipoMittente()!=null)){
					msgDiag.logPersonalizzato("ricezioneMessaggio");
				}
				else{
					msgDiag.logPersonalizzato("ricezioneMessaggio.mittenteAnonimo");
				}
			
				msgDiag.logErroreGenerico(e,"RaccoltaFlowParameter_MTOM_Security");
				
				// Tracciamento richiesta: non ancora registrata
				if(this.msgContext.isTracciamentoAbilitato()){
					EsitoElaborazioneMessaggioTracciato esitoTraccia = 
							EsitoElaborazioneMessaggioTracciato.getEsitoElaborazioneConErrore("Riscontrato errore durante la lettura delle proprieta' di MTOM / SecurityMessage: "+e.getMessage());
					tracciamento.registraRichiesta(requestMessage,null,soapHeaderElement,bustaRichiesta,esitoTraccia,
							Tracciamento.createLocationString(true,this.msgContext.getSourceLocation()),
							correlazioneApplicativa);
				}
				if(this.msgContext.isGestioneRisposta()){
					
					parametriGenerazioneBustaErrore.setBusta(bustaRichiesta);
					parametriGenerazioneBustaErrore.setErroreIntegrazione(ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
							get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_536_CONFIGURAZIONE_NON_DISPONIBILE));
					OpenSPCoop2Message errorOpenSPCoopMsg = generaBustaErroreProcessamento(parametriGenerazioneBustaErrore,e);
					
					// Nota: la bustaRichiesta e' stata trasformata da generaErroreProcessamento

					parametriInvioBustaErrore.setOpenspcoopMsg(errorOpenSPCoopMsg);
					parametriInvioBustaErrore.setBusta(parametriGenerazioneBustaErrore.getBusta());
					sendRispostaBustaErrore(parametriInvioBustaErrore);

				}
				openspcoopstate.releaseResource();
				return;
			}

			
			
			
			/* ----------- MTOM Processor BeforeSecurity ------------ */
			msgDiag.mediumDebug("MTOM Processor [BeforeSecurity]...");
			try{
				mtomProcessor.mtomBeforeSecurity(requestMessage, flowPropertiesRequest.tipoMessaggio);
			}catch(Exception e){
				// L'errore viene registrato dentro il metodo mtomProcessor.mtomBeforeSecurity
				//msgDiag.logErroreGenerico(e,"MTOMProcessor(BeforeSec-"+mtomProcessor.getMTOMProcessorType()+")");
				
				// Tracciamento richiesta: non ancora registrata
				if(this.msgContext.isTracciamentoAbilitato()){
					EsitoElaborazioneMessaggioTracciato esitoTraccia = 
							EsitoElaborazioneMessaggioTracciato.getEsitoElaborazioneConErrore("Riscontrato errore durante la gestione MTOM(BeforeSec-"+mtomProcessor.getMTOMProcessorType()+"): "+e.getMessage());
					tracciamento.registraRichiesta(requestMessage,null,soapHeaderElement,bustaRichiesta,esitoTraccia,
							Tracciamento.createLocationString(true,this.msgContext.getSourceLocation()),
							correlazioneApplicativa);
				}
				if(this.msgContext.isGestioneRisposta()){
					
					parametriGenerazioneBustaErrore.setBusta(bustaRichiesta);
					parametriGenerazioneBustaErrore.setErroreIntegrazione(ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
							get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_557_MTOM_PROCESSOR_ERROR));
					parametriGenerazioneBustaErrore.setIntegrationFunctionError(IntegrationFunctionError.ATTACHMENTS_PROCESSING_REQUEST_FAILED);
					OpenSPCoop2Message errorOpenSPCoopMsg = generaBustaErroreProcessamento(parametriGenerazioneBustaErrore,e);
					
					// Nota: la bustaRichiesta e' stata trasformata da generaErroreProcessamento

					parametriInvioBustaErrore.setOpenspcoopMsg(errorOpenSPCoopMsg);
					parametriInvioBustaErrore.setBusta(parametriGenerazioneBustaErrore.getBusta());
					sendRispostaBustaErrore(parametriInvioBustaErrore);

				}
				openspcoopstate.releaseResource();
				return;
			}
			

			/* ----------- Validazione Semantica (viene anche applicata la sicurezza) ------------ */
			
			msgDiag.logPersonalizzato("validazioneSemantica.beforeSecurity");
			boolean presenzaRichiestaProtocollo = validatore.validazioneSemantica_beforeMessageSecurity(requestInfo.getProtocolServiceBinding(),false, null);
			
			if(validatore.isRilevatiErroriDuranteValidazioneSemantica()==false){
				
				try{
					if(org.openspcoop2.security.message.engine.WSSUtilities.isNormalizeToSaajImpl(messageSecurityContext)){
						msgDiag.mediumDebug("Normalize to saajImpl");
						//System.out.println("RicezioneBuste.request.normalize");
						requestMessage = requestMessage.normalizeToSaajImpl();
						
						validatore.updateMsg(requestMessage);
					}
				}catch(Exception e){
					msgDiag.logErroreGenerico(e,"NormalizeRequestToSaajImpl");
					
					// Tracciamento richiesta: non ancora registrata
					if(this.msgContext.isTracciamentoAbilitato()){
						EsitoElaborazioneMessaggioTracciato esitoTraccia = 
								EsitoElaborazioneMessaggioTracciato.getEsitoElaborazioneConErrore("Riscontrato errore durante la gestione NormalizeRequestToSaajImpl: "+e.getMessage());
						tracciamento.registraRichiesta(requestMessage,null,soapHeaderElement,bustaRichiesta,esitoTraccia,
								Tracciamento.createLocationString(true,this.msgContext.getSourceLocation()),
								correlazioneApplicativa);
					}
					if(this.msgContext.isGestioneRisposta()){
						
						parametriGenerazioneBustaErrore.setBusta(bustaRichiesta);
						parametriGenerazioneBustaErrore.setErroreIntegrazione(ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
								get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_536_CONFIGURAZIONE_NON_DISPONIBILE));
						if(validatore.getErrore_integrationFunctionError()!=null) {
							parametriGenerazioneBustaErrore.setIntegrationFunctionError(validatore.getErrore_integrationFunctionError());
						}
						OpenSPCoop2Message errorOpenSPCoopMsg = generaBustaErroreProcessamento(parametriGenerazioneBustaErrore,e);
						
						// Nota: la bustaRichiesta e' stata trasformata da generaErroreProcessamento

						parametriInvioBustaErrore.setOpenspcoopMsg(errorOpenSPCoopMsg);
						parametriInvioBustaErrore.setBusta(parametriGenerazioneBustaErrore.getBusta());
						sendRispostaBustaErrore(parametriInvioBustaErrore);

					}
					openspcoopstate.releaseResource();
					return;
				}
				
				msgDiag.mediumDebug("Lettura informazioni sulla Sicurezza dal Messaggio di richiesta ...");
				presenzaRichiestaProtocollo = validatore.validazioneSemantica_messageSecurity_readSecurityInfo(messageSecurityContext);
				msgDiag.mediumDebug("Lettura informazioni sulla Sicurezza dal Messaggio di richiesta completata con successo");
			
				if(validatore.isRilevatiErroriDuranteValidazioneSemantica()==false){				
					if(messageSecurityContext!= null && messageSecurityContext.getIncomingProperties() != null && messageSecurityContext.getIncomingProperties().size() > 0){
					
						String tipoSicurezza = SecurityConstants.convertActionToString(messageSecurityContext.getIncomingProperties());
						msgDiag.addKeyword(CostantiPdD.KEY_TIPO_SICUREZZA_MESSAGGIO_RICHIESTA, tipoSicurezza);
						pddContext.addObject(CostantiPdD.KEY_TIPO_SICUREZZA_MESSAGGIO_RICHIESTA, tipoSicurezza);
						
						msgDiag.logPersonalizzato("messageSecurity.processamentoRichiestaInCorso");
						
						StringBuilder bfErroreSecurity = new StringBuilder();
						presenzaRichiestaProtocollo = validatore.validazioneSemantica_messageSecurity_process(messageSecurityContext, bfErroreSecurity,
								transaction!=null ? transaction.getTempiElaborazione() : null,
										true);
						
						if(bfErroreSecurity.length()>0){
							msgDiag.addKeyword(CostantiPdD.KEY_ERRORE_PROCESSAMENTO , bfErroreSecurity.toString() );
							msgDiag.logPersonalizzato("messageSecurity.processamentoRichiestaInErrore");
						}
						else{
							msgDiag.logPersonalizzato("messageSecurity.processamentoRichiestaEffettuato");
						}
					}	
					else{
						msgDiag.logPersonalizzato("messageSecurity.processamentoRichiestaDisabilitato");
					}
				}
			
				if(validatore.isRilevatiErroriDuranteValidazioneSemantica()==false){
					
					msgDiag.logPersonalizzato("validazioneSemantica.afterSecurity");
					presenzaRichiestaProtocollo = validatore.validazioneSemantica_afterMessageSecurity(proprietaManifestAttachments, validazioneIDBustaCompleta);
			
				}
			}

			if(presenzaRichiestaProtocollo == false){

				// Emetto log, non ancora emesso
				if(validatore.getBusta()!=null && 
						(validatore.getBusta().getMittente()!=null || validatore.getBusta().getTipoMittente()!=null)){
					msgDiag.logPersonalizzato("ricezioneMessaggio");
				}
				else{
					msgDiag.logPersonalizzato("ricezioneMessaggio.mittenteAnonimo");
				}
				
				try{
					msgDiag.addKeyword(CostantiPdD.KEY_ECCEZIONI, validatore.getErrore().getDescrizione(protocolFactory));
				}catch(Exception e){
					logCore.error("getDescrizione Error:"+e.getMessage(),e);
				}
				msgDiag.addKeyword(CostantiPdD.KEY_TIPO_VALIDAZIONE_BUSTA, "semantica");
				msgDiag.logPersonalizzato(MsgDiagnosticiProperties.MSG_DIAG_SBUSTAMENTO,"validazioneNonRiuscita");
				
				// Tracciamento richiesta: non ancora registrata
				if(this.msgContext.isTracciamentoAbilitato()){
					EsitoElaborazioneMessaggioTracciato esitoTraccia = 
							EsitoElaborazioneMessaggioTracciato.getEsitoElaborazioneConErrore(msgDiag.getMessaggio_replaceKeywords(MsgDiagnosticiProperties.MSG_DIAG_SBUSTAMENTO,"validazioneNonRiuscita"));
					tracciamento.registraRichiesta(requestMessage,null,soapHeaderElement,bustaRichiesta,esitoTraccia,
							Tracciamento.createLocationString(true,this.msgContext.getSourceLocation()),
							correlazioneApplicativa);
				}
				if(this.msgContext.isGestioneRisposta()){
					
					parametriGenerazioneBustaErrore.setBusta(bustaRichiesta);
					parametriGenerazioneBustaErrore.setErroreIntegrazione(ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
							get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_525_GESTIONE_FUNZIONALITA_PROTOCOLLO));
					if(validatore.getErrore_integrationFunctionError()!=null) {
						parametriGenerazioneBustaErrore.setIntegrationFunctionError(validatore.getErrore_integrationFunctionError());
					}
					OpenSPCoop2Message errorOpenSPCoopMsg = generaBustaErroreProcessamento(parametriGenerazioneBustaErrore,validatore.getEccezioneProcessamentoValidazioneSemantica());
					
					// Nota: la bustaRichiesta e' stata trasformata da generaErroreProcessamento

					parametriInvioBustaErrore.setOpenspcoopMsg(errorOpenSPCoopMsg);
					parametriInvioBustaErrore.setBusta(parametriGenerazioneBustaErrore.getBusta());
					sendRispostaBustaErrore(parametriInvioBustaErrore);

				}
				openspcoopstate.releaseResource();
				return;
			}
			
			
			/* ----------- MTOM Processor AfterSecurity ------------ */
			msgDiag.mediumDebug("MTOM Processor [AfterSecurity]...");
			try{
				mtomProcessor.mtomAfterSecurity(requestMessage, flowPropertiesRequest.tipoMessaggio);
			}catch(Exception e){
				// L'errore viene registrato dentro il metodo mtomProcessor.mtomAfterSecurity
				//msgDiag.logErroreGenerico(e,"MTOMProcessor(AfterSec-"+mtomProcessor.getMTOMProcessorType()+")");
				
				// Tracciamento richiesta: non ancora registrata
				if(this.msgContext.isTracciamentoAbilitato()){
					EsitoElaborazioneMessaggioTracciato esitoTraccia = 
							EsitoElaborazioneMessaggioTracciato.getEsitoElaborazioneConErrore("Riscontrato errore durante la gestione MTOM(AfterSec-"+mtomProcessor.getMTOMProcessorType()+"): "+e.getMessage());
					tracciamento.registraRichiesta(requestMessage,null,soapHeaderElement,bustaRichiesta,esitoTraccia,
							Tracciamento.createLocationString(true,this.msgContext.getSourceLocation()),
							correlazioneApplicativa);
				}
				if(this.msgContext.isGestioneRisposta()){
					
					parametriGenerazioneBustaErrore.setBusta(bustaRichiesta);
					parametriGenerazioneBustaErrore.setErroreIntegrazione(ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
							get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_557_MTOM_PROCESSOR_ERROR));
					parametriGenerazioneBustaErrore.setIntegrationFunctionError(IntegrationFunctionError.ATTACHMENTS_PROCESSING_REQUEST_FAILED);
					OpenSPCoop2Message errorOpenSPCoopMsg = generaBustaErroreProcessamento(parametriGenerazioneBustaErrore,e);
					
					// Nota: la bustaRichiesta e' stata trasformata da generaErroreProcessamento

					parametriInvioBustaErrore.setOpenspcoopMsg(errorOpenSPCoopMsg);
					parametriInvioBustaErrore.setBusta(parametriGenerazioneBustaErrore.getBusta());
					sendRispostaBustaErrore(parametriInvioBustaErrore);

				}
				openspcoopstate.releaseResource();
				return;
			}
			
		}
		java.util.List<Eccezione> erroriValidazione = validatore.getEccezioniValidazione();
		java.util.List<Eccezione> erroriProcessamento =validatore.getEccezioniProcessamento();
		IntegrationFunctionError integrationFunctionErrorValidazione = validatore.getErrore_integrationFunctionError(); 
		SecurityInfo securityInfoRequest = validatore.getSecurityInfo();
		
		boolean isMessaggioErroreProtocollo = validatore.isErroreProtocollo();
		// Se ho un msg Errore e' interessante solo utilizzare mittente/destinatario poiche' il servizio non esistera'
		// La busta e' stata invertita tra mittente e destinatario
		if(isMessaggioErroreProtocollo && idServizio!=null){
			this.cleanDatiServizio(idServizio);
			idServizio.setAzione(null);
		}
		boolean bustaDiServizio = validatore.isBustaDiServizio();
		if(validatore.getInfoServizio()!=null && validatore.getInfoServizio().getIdAccordo()!=null){
			this.msgContext.getProtocol().setIdAccordo(validatore.getInfoServizio().getIdAccordo());
			richiestaApplicativa.setIdAccordo(validatore.getInfoServizio().getIdAccordo());
		}
		else if(infoServizio!=null && infoServizio.getIdAccordo()!=null){
			this.msgContext.getProtocol().setIdAccordo(infoServizio.getIdAccordo());
			richiestaApplicativa.setIdAccordo(infoServizio.getIdAccordo());
		}
		
		// Aggiorno eventuale valore ProfiloCollaborazione dipendete dal profilo (PDC)
		if(bustaRichiesta!=null && this.msgContext.getProtocol()!=null) {
			this.msgContext.getProtocol().setProfiloCollaborazione(bustaRichiesta.getProfiloDiCollaborazione(),bustaRichiesta.getProfiloDiCollaborazioneValue());
			if(bustaRichiesta.getVersioneServizio()>0 && bustaRichiesta.getVersioneServizio()!=this.msgContext.getProtocol().getVersioneServizio()) {
				this.msgContext.getProtocol().setVersioneServizio(bustaRichiesta.getVersioneServizio());
			}
		}

		
		
		
		
		/* -------- Controlli Header di Integrazione ------------- */
		
		IProtocolConfiguration protocolConfig = protocolFactory.createProtocolConfiguration();
		if(infoServizio.getIdRiferimentoRichiesta() &&
				protocolConfig.isIntegrationInfoRequired(TipoPdD.APPLICATIVA, requestInfo.getProtocolServiceBinding(),FunzionalitaProtocollo.RIFERIMENTO_ID_RICHIESTA)) {
			String riferimentoRichiesta = null;
			if (headerIntegrazioneRichiesta!=null &&
					headerIntegrazioneRichiesta.getBusta() != null && headerIntegrazioneRichiesta.getBusta().getRiferimentoMessaggio() != null) {
				riferimentoRichiesta = headerIntegrazioneRichiesta.getBusta().getRiferimentoMessaggio();
			}
			if(riferimentoRichiesta==null) {
				StringBuilder bf = new StringBuilder();
				for (int i = 0; i < tipiIntegrazionePA.length; i++) {
					if(i>0) {
						bf.append(",");
					}
					bf.append(tipiIntegrazionePA[i]);
				}
				msgDiag.addKeyword(CostantiPdD.KEY_TIPI_INTEGRAZIONE ,bf.toString() );
				msgDiag.logPersonalizzato(MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_BUSTE,"riferimentoIdRichiesta.nonFornito");
				
				// Tracciamento richiesta: non ancora registrata
				if(this.msgContext.isTracciamentoAbilitato()){
					EsitoElaborazioneMessaggioTracciato esitoTraccia = 
							EsitoElaborazioneMessaggioTracciato.getEsitoElaborazioneConErrore(msgDiag.getMessaggio(MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_BUSTE,"riferimentoIdRichiesta.nonFornito"));
					tracciamento.registraRichiesta(requestMessage,null,soapHeaderElement,bustaRichiesta,esitoTraccia,
							Tracciamento.createLocationString(true,this.msgContext.getSourceLocation()),
							correlazioneApplicativa);
				}
				
				if(this.msgContext.isGestioneRisposta()){
					
					ErroreIntegrazione erroreIntegrazione = ErroriIntegrazione.ERRORE_442_RIFERIMENTO_ID_MESSAGGIO.getErroreIntegrazione();
					IntegrationFunctionError integrationFunctionError = IntegrationFunctionError.CORRELATION_INFORMATION_NOT_FOUND;
					
					parametriGenerazioneBustaErrore.setBusta(bustaRichiesta);
					parametriGenerazioneBustaErrore.setErroreIntegrazione(erroreIntegrazione);
					parametriGenerazioneBustaErrore.setIntegrationFunctionError(integrationFunctionError);
					OpenSPCoop2Message errorOpenSPCoopMsg = generaBustaErroreProcessamento(parametriGenerazioneBustaErrore, null);

					// Nota: la bustaRichiesta e' stata trasformata da generaErroreProcessamento
					parametriInvioBustaErrore.setOpenspcoopMsg(errorOpenSPCoopMsg);
					parametriInvioBustaErrore.setBusta(parametriGenerazioneBustaErrore.getBusta());
					sendRispostaBustaErrore(parametriInvioBustaErrore);
				}
				openspcoopstate.releaseResource();
				return;

				
			}
		}
		
		
		
		
		
		
		/* ----------- Sbustamento ------------ */
		BustaRawContent<?> headerProtocolloRichiesta = null;
		if(functionAsRouter == false && 
				isMessaggioErroreProtocollo==false && 
				erroriProcessamento.size()==0 && erroriValidazione.size()==0 &&
				bustaDiServizio==false){
			msgDiag.highDebug("Tipo Messaggio Richiesta prima dello sbustamento ["+FaseSbustamento.POST_VALIDAZIONE_SEMANTICA_RISPOSTA
					+"] ["+requestMessage.getClass().getName()+"]");
			org.openspcoop2.protocol.engine.builder.Sbustamento sbustatore = new org.openspcoop2.protocol.engine.builder.Sbustamento(protocolFactory,openspcoopstate.getStatoRichiesta());
			ProtocolMessage protocolMessage = sbustatore.sbustamento(requestMessage,pddContext,
					bustaRichiesta,
					RuoloMessaggio.RICHIESTA,properties.isValidazioneManifestAttachments(),proprietaManifestAttachments,
					FaseSbustamento.POST_VALIDAZIONE_SEMANTICA_RICHIESTA, requestInfo);
			if(protocolMessage!=null) {
				if(protocolMessage.isUseBustaRawContentReadByValidation()) {
					headerProtocolloRichiesta = soapHeaderElement;
				}
				else {
					headerProtocolloRichiesta = protocolMessage.getBustaRawContent();
				}
				requestMessage = protocolMessage.getMessage(); // updated
			}
			msgDiag.highDebug("Tipo Messaggio Richiesta dopo lo sbustamento ["+FaseSbustamento.POST_VALIDAZIONE_SEMANTICA_RISPOSTA
					+"] ["+requestMessage.getClass().getName()+"]");
		}
		else{
			headerProtocolloRichiesta = soapHeaderElement;
		}
		
		
		
		
		
		/* --------------- CorrelazioneApplicativa (Lato PA se definita) ------------------- 
		 * NOTA: Vi e' l'overwrite dell'informazione sulla correlazione applicativa eventualmente letta prima dall'header di trasporto
		 *        opzione comunuque per default disabitata.
		 * --------------- */
		if(functionAsRouter == false && 
				isMessaggioErroreProtocollo==false && 
				erroriProcessamento.size()==0 && erroriValidazione.size()==0 &&
				bustaDiServizio==false &&
				pa!=null && pa.getCorrelazioneApplicativa()!=null){
		
			GestoreCorrelazioneApplicativa gestoreCorrelazioneApplicativa = null;
			try {
				gestoreCorrelazioneApplicativa = 
					new GestoreCorrelazioneApplicativa(openspcoopstate.getStatoRichiesta(), logCore, soggettoFruitore,
							idServizio,servizioApplicativoFruitore,protocolFactory,
							transaction);
				
				gestoreCorrelazioneApplicativa.verificaCorrelazione(pa.getCorrelazioneApplicativa(), urlProtocolContext, requestMessage, 
						headerIntegrazioneRichiesta, false);
				
				if(gestoreCorrelazioneApplicativa.getIdCorrelazione()!=null)
					correlazioneApplicativa = gestoreCorrelazioneApplicativa.getIdCorrelazione();
				
			}catch(Exception e){
				
				pddContext.addObject(org.openspcoop2.core.constants.Costanti.ERRORE_CORRELAZIONE_APPLICATIVA_RICHIESTA, "true");
				
				msgDiag.logErroreGenerico(e,"CorrelazioneApplicativa("+bustaRichiesta.getID()+")");
				logCore.error("Riscontrato errore durante la correlazione applicativa ["+bustaRichiesta.getID()+"]",e);
				
				// Tracciamento richiesta: non ancora registrata
				if(this.msgContext.isTracciamentoAbilitato()){
					EsitoElaborazioneMessaggioTracciato esitoTraccia = 
							EsitoElaborazioneMessaggioTracciato.getEsitoElaborazioneConErrore("Riscontrato errore durante la correlazione applicativa ["+bustaRichiesta.getID()+"]: "+e.getMessage());
					tracciamento.registraRichiesta(requestMessage,null,soapHeaderElement,bustaRichiesta,esitoTraccia,
							Tracciamento.createLocationString(true,this.msgContext.getSourceLocation()),
							correlazioneApplicativa);
				}
				
				if(this.msgContext.isGestioneRisposta()){
					
					ErroreIntegrazione errore = null;
					if(gestoreCorrelazioneApplicativa!=null){
						errore = gestoreCorrelazioneApplicativa.getErrore();
					}
					if(errore==null){
						errore = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
								get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_529_CORRELAZIONE_APPLICATIVA_RICHIESTA_NON_RIUSCITA);
					}
					
					parametriGenerazioneBustaErrore.setBusta(bustaRichiesta);
					parametriGenerazioneBustaErrore.setErroreIntegrazione(errore);
					IntegrationFunctionError integrationFunctionError = null;
					if(CodiceErroreIntegrazione.CODICE_416_CORRELAZIONE_APPLICATIVA_RICHIESTA_ERRORE.equals(errore.getCodiceErrore())){
						integrationFunctionError = IntegrationFunctionError.APPLICATION_CORRELATION_IDENTIFICATION_REQUEST_FAILED;
					}
					else{
						integrationFunctionError = IntegrationFunctionError.INTERNAL_REQUEST_ERROR;
					}
					parametriGenerazioneBustaErrore.setIntegrationFunctionError(integrationFunctionError);
					OpenSPCoop2Message errorOpenSPCoopMsg = generaBustaErroreProcessamento(parametriGenerazioneBustaErrore,e);

					// Nota: la bustaRichiesta e' stata trasformata da generaErroreProcessamento
					parametriInvioBustaErrore.setOpenspcoopMsg(errorOpenSPCoopMsg);
					parametriInvioBustaErrore.setBusta(parametriGenerazioneBustaErrore.getBusta());
					sendRispostaBustaErrore(parametriInvioBustaErrore);
				}
				openspcoopstate.releaseResource();
				return;
			}
		}
		else{
			// L'informazione nel caso di functionAsRouter o isMessaggioErrore o erroriProcessamento.size()>0 o erroriValidazione.size()>0 o bustaDiServizio non e' significativa
			// Invece nel caso di pa==null o di pa.getCorrelazioneApplicativa==null l'informazione eventualmente cmq letta prima non deve essere usata:
			correlazioneApplicativa = null;
		}
		
		
		
		
		
		
		
		
		
		
		
		/* --------------- Header Integrazione SOAP (aggiornamento/eliminazione) -------------------  */
		if(tipiIntegrazionePA!=null){
			
			if(inRequestPAMessage!=null){
				inRequestPAMessage.setBustaRichiesta(bustaRichiesta);
				inRequestPAMessage.setSoggettoMittente(soggettoFruitore);
			}
			
			for (int i = 0; i < tipiIntegrazionePA.length; i++) {
				try {
					//IGestoreIntegrazionePA gestore = RicezioneBuste.gestoriIntegrazionePA.get(tipiIntegrazionePA[i]);
					
					String classType = null;
					IGestoreIntegrazionePA gestore = null;
					try {
						classType = className.getIntegrazionePortaApplicativa(tipiIntegrazionePA[i]);
						gestore = (IGestoreIntegrazionePA)loader.newInstance(classType);
						AbstractCore.init(gestore, pddContext, protocolFactory);
					} catch (Exception e) {
						throw new Exception(
								"Riscontrato errore durante il caricamento della classe ["+ classType
								+ "] da utilizzare per la gestione (aggiornamento/eliminazione) dell'integrazione di tipo ["+ tipiIntegrazionePA[i] + "]: " + e.getMessage());
					}
					
					if (gestore instanceof IGestoreIntegrazionePASoap) {
						if(propertiesReader.deleteHeaderIntegrazioneRequestPA()){
							((IGestoreIntegrazionePASoap)gestore).deleteInRequestHeader(inRequestPAMessage);
						}
						else{
							((IGestoreIntegrazionePASoap)gestore).updateInRequestHeader(inRequestPAMessage, idMessageRequest, servizioApplicativoFruitore, correlazioneApplicativa);
						}
					} 					
				} catch (Exception e) {
					msgDiag.logErroreGenerico(e,"HeaderIntegrazione("+tipiIntegrazionePA[i]+")");
					
					// Tracciamento richiesta: non ancora registrata
					if(this.msgContext.isTracciamentoAbilitato()){
						EsitoElaborazioneMessaggioTracciato esitoTraccia = 
								EsitoElaborazioneMessaggioTracciato.getEsitoElaborazioneConErrore("Riscontrato errore durante la gestione dell'Header di Integrazione("+tipiIntegrazionePA[i]+"): "+e.getMessage());
						tracciamento.registraRichiesta(requestMessage,null,soapHeaderElement,bustaRichiesta,esitoTraccia,
								Tracciamento.createLocationString(true,this.msgContext.getSourceLocation()),
								correlazioneApplicativa);
					}
					
					if(this.msgContext.isGestioneRisposta()){
						
						parametriGenerazioneBustaErrore.setBusta(bustaRichiesta);
						parametriGenerazioneBustaErrore.setErroreIntegrazione(ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
								get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_541_GESTIONE_HEADER_INTEGRAZIONE));
						OpenSPCoop2Message errorOpenSPCoopMsg = generaBustaErroreProcessamento(parametriGenerazioneBustaErrore,e);

						// Nota: la bustaRichiesta e' stata trasformata da generaErroreProcessamento
						parametriInvioBustaErrore.setOpenspcoopMsg(errorOpenSPCoopMsg);
						parametriInvioBustaErrore.setBusta(parametriGenerazioneBustaErrore.getBusta());
						sendRispostaBustaErrore(parametriInvioBustaErrore);
					}
					openspcoopstate.releaseResource();
					return;
				}
			}
		}
		
		
		
		
		
		
		
		
		

		

		/* ----------- Correlazione nei messaggi diagnostici ---------------*/
		msgDiag.setIdCorrelazioneApplicativa(correlazioneApplicativa);
		if(this.msgContext.getIntegrazione()!=null)
			this.msgContext.getIntegrazione().setIdCorrelazioneApplicativa(correlazioneApplicativa);
		parametriGenerazioneBustaErrore.setMsgDiag(msgDiag);
		parametriInvioBustaErrore.setMsgDiag(msgDiag);
		
		
		
		
		
		
		
		
		
		
		
		/* ----------- Emetto msg diagnostico di ricezione Busta ----------------------*/
		DettaglioEccezione dettaglioEccezione = null;
		boolean mittenteAnonimo = false;
		if(validatore.getBusta()!=null && 
				(validatore.getBusta().getMittente()!=null || validatore.getBusta().getTipoMittente()!=null)){
			mittenteAnonimo = false;
		}
		else{
			mittenteAnonimo = true;
		}
		if(isMessaggioErroreProtocollo){
			if(validatore.isMessaggioErroreIntestazione()){
				msgDiag.addKeyword(CostantiPdD.KEY_TIPO_MESSAGGIO_BUSTA_ERRORE, CostantiPdD.TIPO_MESSAGGIO_BUSTA_ERRORE_INTESTAZIONE);
			}else{
				msgDiag.addKeyword(CostantiPdD.KEY_TIPO_MESSAGGIO_BUSTA_ERRORE, CostantiPdD.TIPO_MESSAGGIO_BUSTA_ERRORE_PROCESSAMENTO);
			}
			msgDiag.logPersonalizzato("ricezioneMessaggioErrore");
			
			// Esamino se e' presente un elemento DettaglioEccezione
			try{
				dettaglioEccezione = XMLUtils.getDettaglioEccezione(logCore,requestMessage);
			}catch(Exception e){
				logCore.error("Errore durante l'analisi del dettaglio dell'eccezione",e);
			}
			
		}else{
			if(mittenteAnonimo){
				msgDiag.logPersonalizzato("ricezioneMessaggio.mittenteAnonimo");
			}
			else{
				msgDiag.logPersonalizzato("ricezioneMessaggio");
			}
		}












		/* -------- Profilo di Gestione ------------- */
		String versioneProtocollo = validatore.getProfiloGestione();
		msgDiag.mediumDebug("Profilo di gestione ["+RicezioneBuste.ID_MODULO+"] della busta: "+versioneProtocollo);
		richiestaApplicativa.setProfiloGestione(versioneProtocollo);
		parametriGenerazioneBustaErrore.setProfiloGestione(versioneProtocollo);
		IProtocolVersionManager moduleManager = protocolFactory.createProtocolVersionManager(versioneProtocollo);
		if( functionAsRouter==false ){

			// Riferimento messaggio con un profilo sincrono non può essere ricevuto in questo contesto.
			if(org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.SINCRONO.equals(bustaRichiesta.getProfiloDiCollaborazione()) && 
					bustaRichiesta.getRiferimentoMessaggio()!=null &&
					!isMessaggioErroreProtocollo &&// aggiunto !isMessaggioErrore poiche' una busta di eccezione puo' contenenere in effetti il rifMsg
					(ruoloBustaRicevuta==null || !ruoloBustaRicevuta.equals(RuoloBusta.RICHIESTA)) // aggiunto questo controllo perchè i protocolli con id riferimento richiesta hanno il rif messaggio, ma il ruolo rimane RICHIESTA
					){ 
				Eccezione ecc = Eccezione.getEccezioneValidazione(ErroriCooperazione.RIFERIMENTO_MESSAGGIO_NON_VALIDO.getErroreCooperazione(), protocolFactory);
				erroriValidazione.add(ecc);
			}
		}

















		/* -------- Gestione errori ------------- */
		// Se sono in modalita router: se vi sono errori di processamento/validazione, ritorno subito l'errore.
		// altrimenti: se sono presenti errore di processamento, ritorno subito l'errore.
		msgDiag.mediumDebug("Gestione errori...");
		if( (erroriProcessamento.size()>0) || (functionAsRouter && (erroriValidazione.size()>0)) ){
			StringBuilder errore = new StringBuilder();
			for(int k=0; k<erroriProcessamento.size();k++){
				Eccezione erroreProcessamento = erroriProcessamento.get(k);
				try{
					errore.append("Processamento["+traduttore.toString(erroreProcessamento.getCodiceEccezione(),erroreProcessamento.getSubCodiceEccezione())+"] "+erroriProcessamento.get(k).getDescrizione(protocolFactory)+"\n");
				}catch(Exception e){
					logCore.error("getDescrizione Error:"+e.getMessage(),e);
				}
			}
			for(int k=0; k<erroriValidazione.size();k++){
				Eccezione erroreValidazione = erroriValidazione.get(k);
				try{
					errore.append("Validazione["+traduttore.toString(erroreValidazione.getCodiceEccezione(),erroreValidazione.getSubCodiceEccezione())+"] "+erroriValidazione.get(k).getDescrizione(protocolFactory)+"\n");
				}catch(Exception e){
					logCore.error("getDescrizione Error:"+e.getMessage(),e);
				}
			}
			msgDiag.addKeyword(CostantiPdD.KEY_ECCEZIONI, errore.toString());
			msgDiag.addKeyword(CostantiPdD.KEY_NUMERO_ECCEZIONI, (erroriProcessamento.size()+erroriValidazione.size())+"" );
			msgDiag.logPersonalizzato(MsgDiagnosticiProperties.MSG_DIAG_SBUSTAMENTO,"validazioneBusta.bustaNonCorretta");
			// Tracciamento richiesta: non ancora registrata
			if(this.msgContext.isTracciamentoAbilitato()){
				EsitoElaborazioneMessaggioTracciato esitoTraccia = 
						EsitoElaborazioneMessaggioTracciato.getEsitoElaborazioneConErrore(msgDiag.getMessaggio_replaceKeywords(MsgDiagnosticiProperties.MSG_DIAG_SBUSTAMENTO,"validazioneBusta.bustaNonCorretta"));
				tracciamento.registraRichiesta(requestMessage,securityInfoRequest,soapHeaderElement,bustaRichiesta,esitoTraccia,
						Tracciamento.createLocationString(true,this.msgContext.getSourceLocation()),
						correlazioneApplicativa);
			}
			if(this.msgContext.isGestioneRisposta()){
				OpenSPCoop2Message errorOpenSPCoopMsg = null;
				parametriGenerazioneBustaErrore.setBusta(bustaRichiesta);
				if(erroriProcessamento.size()>0){
					// inserisco in erroriProcessamento anche erroriValidazione
					for(int j=0; j<erroriValidazione.size(); j++){
						erroriProcessamento.add(erroriValidazione.get(j));
					}

					parametriGenerazioneBustaErrore.setError(erroriProcessamento);
					errorOpenSPCoopMsg = generaBustaErroreProcessamento(parametriGenerazioneBustaErrore,null);

				}else{

					parametriGenerazioneBustaErrore.setError(erroriValidazione);
					if(integrationFunctionErrorValidazione!=null) {
						parametriGenerazioneBustaErrore.setIntegrationFunctionError(integrationFunctionErrorValidazione);
					}
					errorOpenSPCoopMsg = generaBustaErroreValidazione(parametriGenerazioneBustaErrore);

				}
				
				// Nota: la bustaRichiesta e' stata trasformata da generaErroreProcessamento

				parametriInvioBustaErrore.setOpenspcoopMsg(errorOpenSPCoopMsg);
				parametriInvioBustaErrore.setBusta(parametriGenerazioneBustaErrore.getBusta());
				sendRispostaBustaErrore(parametriInvioBustaErrore);

			}
			openspcoopstate.releaseResource();
			return;
		}











		
		
		
		/* -------- Tracciamento ------------- */
		if(this.msgContext.isTracciamentoAbilitato()){
			msgDiag.mediumDebug("Tracciamento busta di richiesta...");
			
			EsitoElaborazioneMessaggioTracciato esitoTraccia = null;
			if( (erroriProcessamento.size()>0) || (erroriValidazione.size()>0) ){
				
				boolean foundErroriGravi = false;
				
				String dettaglioErrore = null;
				StringBuilder eccBuffer = new StringBuilder();
				for(int k = 0; k < erroriProcessamento.size() ; k++){
					Eccezione er = erroriProcessamento.get(k);
					if(k>0)
						eccBuffer.append(" ");
					eccBuffer.append(er.toString(protocolFactory));
					eccBuffer.append(";");
					
					if(moduleManager.isIgnoraEccezioniLivelloNonGrave()){
						if(er.getRilevanza()!=null){
							if(LivelloRilevanza.isEccezioneLivelloGrave(er.getRilevanza())){
								foundErroriGravi = true;
							}
						}else{
							foundErroriGravi = true;
						}
					}else{
						foundErroriGravi = true;
					}
				}
				for(int k = 0; k < erroriValidazione.size() ; k++){
					Eccezione er = erroriValidazione.get(k);
					if(eccBuffer.length()>0)
						eccBuffer.append(" ");
					eccBuffer.append(er.toString(protocolFactory));
					eccBuffer.append(";");
					
					if(moduleManager.isIgnoraEccezioniLivelloNonGrave()){
						if(er.getRilevanza()!=null){
							if(LivelloRilevanza.isEccezioneLivelloGrave(er.getRilevanza())){
								foundErroriGravi = true;
							}
						}else{
							foundErroriGravi = true;
						}
					}else{
						foundErroriGravi = true;
					}
				}
				msgDiag.addKeyword(CostantiPdD.KEY_NUMERO_ECCEZIONI, erroriProcessamento.size()+erroriValidazione.size()+"");
				msgDiag.addKeyword(CostantiPdD.KEY_ECCEZIONI, eccBuffer.toString());
				if(isMessaggioErroreProtocollo){
					dettaglioErrore = msgDiag.getMessaggio_replaceKeywords(MsgDiagnosticiProperties.MSG_DIAG_SBUSTAMENTO,"validazioneBustaErrore.listaEccezioniMalformata");
				}else{
					dettaglioErrore = msgDiag.getMessaggio_replaceKeywords(MsgDiagnosticiProperties.MSG_DIAG_SBUSTAMENTO,"validazioneBusta.bustaNonCorretta");
				}
				if(foundErroriGravi){
					esitoTraccia = 
						EsitoElaborazioneMessaggioTracciato.getEsitoElaborazioneConErrore(dettaglioErrore);
				}
				else{
					esitoTraccia = 
						EsitoElaborazioneMessaggioTracciato.getEsitoElaborazioneMessaggioRicevuto(dettaglioErrore);
				}
			}
			else{
				esitoTraccia = 
						EsitoElaborazioneMessaggioTracciato.getEsitoElaborazioneMessaggioRicevuto();
			}
			
			// Tracciamento Busta Ricevuta
			tracciamento.registraRichiesta(requestMessage,securityInfoRequest,headerProtocolloRichiesta,bustaRichiesta,esitoTraccia,
					Tracciamento.createLocationString(true,this.msgContext.getSourceLocation()),
					correlazioneApplicativa);
		}













		/* -------- Check Mittente o Destinatario o Servizio conosciuto o identificativo errato ---------------- */
		msgDiag.mediumDebug("Controllo mittente/destinatario/servizio (se sconosciuto, risposta immediata sulla connessione)...");
		for(int k = 0; k < erroriValidazione.size() ; k++){
			Eccezione er = erroriValidazione.get(k);
			if( CodiceErroreCooperazione.isEccezioneMittente(er.getCodiceEccezione()) ||			
				CodiceErroreCooperazione.isEccezioneDestinatario(er.getCodiceEccezione()) ||	
				CodiceErroreCooperazione.isEccezioneServizio(er.getCodiceEccezione()) ||	
				CodiceErroreCooperazione.isEccezioneIdentificativoMessaggio(er.getCodiceEccezione()) ||	
				CodiceErroreCooperazione.isEccezioneSicurezzaAutorizzazione(er.getCodiceEccezione()) ){

				if(functionAsRouter==false){ 
					// Può esistere un errore mittente, che non è altro che una segnalazione sull'indirizzo telematico
					if(moduleManager.isEccezioniLivelloInfoAbilitato()){
						if(LivelloRilevanza.INFO.equals(er.getRilevanza()))
							continue;
					}
				}

				StringBuilder eccBuffer = new StringBuilder();
				for(int j = 0; j < erroriValidazione.size() ; j++){
					if(j>0)
						eccBuffer.append("\n");
					eccBuffer.append(erroriValidazione.get(j).toString(protocolFactory));
				}
				msgDiag.addKeyword(CostantiPdD.KEY_ECCEZIONI, eccBuffer.toString());
				msgDiag.addKeyword(CostantiPdD.KEY_NUMERO_ECCEZIONI, erroriValidazione.size()+"" );
				msgDiag.logPersonalizzato(MsgDiagnosticiProperties.MSG_DIAG_SBUSTAMENTO,"validazioneBusta.bustaNonCorretta");
				if(this.msgContext.isGestioneRisposta()){
					
					parametriGenerazioneBustaErrore.setBusta(bustaRichiesta);
					List<Eccezione> errs = new ArrayList<Eccezione>();
					for(int add = 0; add < erroriValidazione.size() ; add++){
						errs.add(erroriValidazione.get(add));
					}
					parametriGenerazioneBustaErrore.setError(errs);
					
					if(integrationFunctionErrorValidazione!=null) {
						parametriGenerazioneBustaErrore.setIntegrationFunctionError(integrationFunctionErrorValidazione);
					}
					else if(CodiceErroreCooperazione.isEccezioneSicurezzaAutorizzazione(er.getCodiceEccezione())) {
						parametriGenerazioneBustaErrore.setIntegrationFunctionError(IntegrationFunctionError.AUTHORIZATION);
					}
					else {
						parametriGenerazioneBustaErrore.setIntegrationFunctionError(IntegrationFunctionError.INVALID_INTEROPERABILITY_PROFILE_REQUEST);
					}
					
					OpenSPCoop2Message errorOpenSPCoopMsg = generaBustaErroreValidazione(parametriGenerazioneBustaErrore);
					
					// Nota: la bustaRichiesta e' stata trasformata da generaErroreProcessamento
					// se il mittente e' sconosciuto non e' possibile utilizzare l'invio su di una nuova connessione,
					// a meno della presenza dell'indirizzo telematico, poiche' non esistera un connettore su cui spedire

					parametriInvioBustaErrore.setUtilizzoIndirizzoTelematico(false);
					parametriInvioBustaErrore.setNewConnectionForResponse(false);
					parametriInvioBustaErrore.setOpenspcoopMsg(errorOpenSPCoopMsg);
					parametriInvioBustaErrore.setBusta(parametriGenerazioneBustaErrore.getBusta());
					sendRispostaBustaErrore(parametriInvioBustaErrore);

				}
				openspcoopstate.releaseResource();
				return;
			}
		}





		
		







		/* -------- Check Autorizzazione (Se non ho ricevuto un msg Errore Protocollo)---------------- */
		
		// Guardo se vi sono eccezioni di validazione
		// Se ci sono eccezioni di processamento vengono ritornate prima
		boolean eccezioniValidazioni = false;
		for(int k = 0; k < erroriValidazione.size() ; k++){
			Eccezione er = erroriValidazione.get(k);
			if(functionAsRouter==false){ 
				// Check profilo linee guida 1.0
				// Può esistere un errore, che non è altro che una segnalazione sull'indirizzo telematico
				if(moduleManager.isEccezioniLivelloInfoAbilitato()){
					if(LivelloRilevanza.INFO.equals(er.getRilevanza()))
						continue;
				}
			}
			eccezioniValidazioni = true;
			break;
		}
		
		msgDiag.mediumDebug("Autorizzazione ...");
		String tipoAutorizzazione = null;
		try{
			if(functionAsRouter){
				tipoAutorizzazione = propertiesReader.getTipoAutorizzazioneBuste();
			}
			else{
				if(pa!=null){
					tipoAutorizzazione = configurazionePdDReader.getAutorizzazione(pa);
				}
				// Non ha senso effettuare l'autorizzazione basato sulla PD
//				else{
//					tipoAutorizzazione = configurazionePdDReader.getAutorizzazione(pd);
//				}
			}
		}catch(Exception notFound){}
		boolean isAttivoAutorizzazioneBuste = tipoAutorizzazione!=null && !CostantiConfigurazione.AUTORIZZAZIONE_NONE.equalsIgnoreCase(tipoAutorizzazione);
		this.msgContext.getIntegrazione().setTipoAutorizzazione(tipoAutorizzazione);
		if(tipoAutorizzazione!=null){
			msgDiag.addKeyword(CostantiPdD.KEY_TIPO_AUTORIZZAZIONE, tipoAutorizzazione);
			msgDiag.addKeyword(CostantiPdD.KEY_TIPO_AUTORIZZAZIONE_BUSTE, tipoAutorizzazione);
		}
		if(!isAttivoAutorizzazioneBuste){
			msgDiag.logPersonalizzato("autorizzazioneBusteDisabilitata");
		}

		DatiInvocazionePortaApplicativa datiInvocazione = null;
		
		if(isAttivoAutorizzazioneBuste &&
				isMessaggioErroreProtocollo==false &&
				bustaDiServizio==false &&
				eccezioniValidazioni==false){	
			
			transaction.getTempiElaborazione().startAutorizzazione();
			try{

				msgDiag.mediumDebug("Autorizzazione di tipo ["+tipoAutorizzazione+"]...");

				//	Controllo Autorizzazione
				String identitaMittente = null;
				if(credenziali!=null && !soggettoAutenticato){
					if(!"".equals(credenziali.toString())){
						identitaMittente = credenziali.toString();
						if(identitaMittente.endsWith(" ")){
							identitaMittente = identitaMittente.substring(0, identitaMittente.length()-1);	
						}
					}
				}
				String subjectMessageSecurity = null;
				if(messageSecurityContext!=null){
					subjectMessageSecurity = messageSecurityContext.getSubject();
				}



				/* --- Calcolo idServizio e fruitore ---- */

				IDServizio idServizioPerAutorizzazione = getIdServizioPerAutorizzazione(idServizio, soggettoFruitore, functionAsRouter, bustaRichiesta, ruoloBustaRicevuta); 
				IDSoggetto idSoggettoMittentePerAutorizzazione = getIDSoggettoMittentePerAutorizzazione(idServizio, soggettoFruitore, functionAsRouter, bustaRichiesta, ruoloBustaRicevuta, supportatoAutenticazioneSoggetti);
				Soggetto soggettoMittentePerAutorizzazione = null;
				if(idSoggettoMittentePerAutorizzazione!=null){
					soggettoMittentePerAutorizzazione = registroServiziReader.getSoggetto(idSoggettoMittentePerAutorizzazione, null);
				}

				String tipoMessaggio = "messaggio";
				if(ruoloBustaRicevuta!=null){
					if(RuoloBusta.RICEVUTA_RICHIESTA.equals(ruoloBustaRicevuta.toString()) || RuoloBusta.RICEVUTA_RISPOSTA.equals(ruoloBustaRicevuta.toString())){
						tipoMessaggio = "ricevuta asincrona";
					}
				}
				msgDiag.addKeyword(CostantiPdD.KEY_TIPO_MESSAGGIO_BUSTA, tipoMessaggio);
				if(idSoggettoMittentePerAutorizzazione!=null){
					msgDiag.addKeyword(CostantiPdD.KEY_MITTENTE_E_SERVIZIO_DA_AUTORIZZARE, "fruitore ["+idSoggettoMittentePerAutorizzazione.toString()+"] -> servizio ["+idServizioPerAutorizzazione.toString()+"]");
				}
				else{
					msgDiag.addKeyword(CostantiPdD.KEY_MITTENTE_E_SERVIZIO_DA_AUTORIZZARE, "servizio ["+idServizioPerAutorizzazione.toString()+"]");
				}
				if(identitaMittente!=null)
					msgDiag.addKeyword(CostantiPdD.KEY_CREDENZIALI_MITTENTE_MSG, " credenzialiMittente "+identitaMittente);
				else
					msgDiag.addKeyword(CostantiPdD.KEY_CREDENZIALI_MITTENTE_MSG, "");
				if(servizioApplicativoFruitore!=null)
					msgDiag.addKeyword(CostantiPdD.KEY_SA_FRUITORE_MSG, " identitaServizioApplicativoFruitore ["+servizioApplicativoFruitore+"]");
				else
					msgDiag.addKeyword(CostantiPdD.KEY_SA_FRUITORE_MSG, "");
				if(subjectMessageSecurity!=null)
					msgDiag.addKeyword(CostantiPdD.KEY_SUBJECT_MESSAGE_SECURITY_MSG, " subjectMessageSecurity ["+subjectMessageSecurity+"]");
				else
					msgDiag.addKeyword(CostantiPdD.KEY_SUBJECT_MESSAGE_SECURITY_MSG, "");
				msgDiag.logPersonalizzato("autorizzazioneBusteInCorso");
				
				IDServizioApplicativo identitaServizioApplicativoFruitore = new IDServizioApplicativo();
				identitaServizioApplicativoFruitore.setNome(servizioApplicativoFruitore);
				identitaServizioApplicativoFruitore.setIdSoggettoProprietario(idSoggettoMittentePerAutorizzazione);
				
				datiInvocazione = new DatiInvocazionePortaApplicativa();
				datiInvocazione.setPddContext(pddContext);
				datiInvocazione.setInfoConnettoreIngresso(inRequestContext.getConnettore());
				datiInvocazione.setIdServizio(idServizioPerAutorizzazione);
				datiInvocazione.setState(openspcoopstate.getStatoRichiesta());
				datiInvocazione.setCredenzialiPdDMittente(credenziali);
				datiInvocazione.setIdentitaServizioApplicativoFruitore(identitaServizioApplicativoFruitore);
				datiInvocazione.setSubjectServizioApplicativoFruitoreFromMessageSecurityHeader(subjectMessageSecurity);
				datiInvocazione.setIdPA(idPA);
				datiInvocazione.setPa(pa);
				datiInvocazione.setIdPD(idPD);
				datiInvocazione.setPd(pd);
				datiInvocazione.setIdSoggettoFruitore(idSoggettoMittentePerAutorizzazione);
				datiInvocazione.setSoggettoFruitore(soggettoMittentePerAutorizzazione);
				datiInvocazione.setRuoloBusta(ruoloBustaRicevuta);
				
				EsitoAutorizzazionePortaApplicativa esito = 
						GestoreAutorizzazione.verificaAutorizzazionePortaApplicativa(tipoAutorizzazione, 
								datiInvocazione, pddContext, protocolFactory, requestMessage, logCore);
				CostantiPdD.addKeywordInCache(msgDiag, esito.isEsitoPresenteInCache(),
						pddContext, CostantiPdD.KEY_INFO_IN_CACHE_FUNZIONE_AUTORIZZAZIONE);
				if(esito.getDetails()==null){
					msgDiag.addKeyword(CostantiPdD.KEY_DETAILS, "");
				}else{
					msgDiag.addKeyword(CostantiPdD.KEY_DETAILS, " ("+esito.getDetails()+")");
				}
				if(esito.isAutorizzato()==false){
					pddContext.addObject(org.openspcoop2.core.constants.Costanti.ERRORE_AUTORIZZAZIONE, "true");
					String descrizioneErrore = null;
					try{
						if(esito.getErroreCooperazione()!=null){
							descrizioneErrore = esito.getErroreCooperazione().getDescrizione(protocolFactory);
						}
						else{
							descrizioneErrore = esito.getErroreIntegrazione().getDescrizione(protocolFactory);
						}
						msgDiag.addKeyword(CostantiPdD.KEY_ERRORE_PROCESSAMENTO, descrizioneErrore);
					}catch(Exception e){
						logCore.error("getDescrizione Error:"+e.getMessage(),e);
					}
					msgDiag.addKeyword(CostantiPdD.KEY_POSIZIONE_ERRORE, traduttore.toString(esito.getErroreCooperazione().getCodiceErrore()));
					msgDiag.logPersonalizzato("autorizzazioneBusteFallita");
					String errorMsg =  "Riscontrato errore durante il processo di Autorizzazione per il messaggio con identificativo ["+bustaRichiesta.getID()+"]: "+descrizioneErrore;
					if(esito.getEccezioneProcessamento()!=null){
						logCore.error(errorMsg,esito.getEccezioneProcessamento());
					}
					else{
						logCore.error(errorMsg);
					}
					if(this.msgContext.isGestioneRisposta()){
						
						if(esito.getErrorMessage()!=null) {
							this.msgContext.setMessageResponse(esito.getErrorMessage());
						}
						else {
						
							OpenSPCoop2Message errorOpenSPCoopMsg = null;
							parametriGenerazioneBustaErrore.setBusta(bustaRichiesta);
							IntegrationFunctionError integrationFunctionError = esito.getIntegrationFunctionError();
							if(esito.getErroreCooperazione()!=null){
								parametriGenerazioneBustaErrore.setErroreCooperazione(esito.getErroreCooperazione());
								if(CodiceErroreCooperazione.SICUREZZA_AUTORIZZAZIONE_FALLITA.equals(esito.getErroreCooperazione().getCodiceErrore()) || 
										CodiceErroreCooperazione.SICUREZZA_FALSIFICAZIONE_MITTENTE.equals(esito.getErroreCooperazione().getCodiceErrore()) ||
										CodiceErroreCooperazione.SICUREZZA_TOKEN_AUTORIZZAZIONE_FALLITA.equals(esito.getErroreCooperazione().getCodiceErrore())){
									if(integrationFunctionError==null) {
										integrationFunctionError = IntegrationFunctionError.AUTHORIZATION;
									}
									parametriGenerazioneBustaErrore.setIntegrationFunctionError(integrationFunctionError);
									errorOpenSPCoopMsg = generaBustaErroreValidazione(parametriGenerazioneBustaErrore);
								}
								// Else necessario per Certificazione DigitPA tramite Router
								else if(CodiceErroreCooperazione.SERVIZIO_SCONOSCIUTO.equals(esito.getErroreCooperazione().getCodiceErrore())){
									parametriGenerazioneBustaErrore.setErroreCooperazione(
											ErroriCooperazione.SERVIZIO_SCONOSCIUTO.getErroreCooperazione()); // in modo da utilizzare la posizione standard.
									if(integrationFunctionError==null) {
										integrationFunctionError = IntegrationFunctionError.NOT_FOUND;
									}
									parametriGenerazioneBustaErrore.setIntegrationFunctionError(integrationFunctionError);
									errorOpenSPCoopMsg = generaBustaErroreValidazione(parametriGenerazioneBustaErrore);
								}
								else{
									if(integrationFunctionError==null) {
										integrationFunctionError = IntegrationFunctionError.INTERNAL_REQUEST_ERROR;
									}
									parametriGenerazioneBustaErrore.setIntegrationFunctionError(integrationFunctionError);
									errorOpenSPCoopMsg = generaBustaErroreProcessamento(parametriGenerazioneBustaErrore, esito.getEccezioneProcessamento());
								}
							}
							else{
								parametriGenerazioneBustaErrore.setErroreIntegrazione(esito.getErroreIntegrazione());
								if(integrationFunctionError==null) {
									integrationFunctionError = IntegrationFunctionError.INTERNAL_REQUEST_ERROR;
								}
								parametriGenerazioneBustaErrore.setIntegrationFunctionError(integrationFunctionError);
								errorOpenSPCoopMsg = generaBustaErroreProcessamento(parametriGenerazioneBustaErrore, esito.getEccezioneProcessamento());
							}
							
							if(esito.getWwwAuthenticateErrorHeader()!=null) {
								errorOpenSPCoopMsg.forceTransportHeader(HttpConstants.AUTHORIZATION_RESPONSE_WWW_AUTHENTICATE, esito.getWwwAuthenticateErrorHeader());
							}
	
							// Nota: la bustaRichiesta e' stata trasformata da generaErroreProcessamento
							parametriInvioBustaErrore.setOpenspcoopMsg(errorOpenSPCoopMsg);
							parametriInvioBustaErrore.setBusta(parametriGenerazioneBustaErrore.getBusta());
							sendRispostaBustaErrore(parametriInvioBustaErrore);
							
						}
					}
					openspcoopstate.releaseResource();
					return;
				}else{
					msgDiag.logPersonalizzato("autorizzazioneBusteEffettuata");
				}
			}catch(Exception e){
				msgDiag.logErroreGenerico(e,"AutorizzazioneMessaggio("+bustaRichiesta.getID()+")");
				logCore.error("Riscontrato errore durante il processo di Autorizzazione per il messaggio con identificativo ["+bustaRichiesta.getID()+"]",e);
				if(this.msgContext.isGestioneRisposta()){
					
					parametriGenerazioneBustaErrore.setBusta(bustaRichiesta);
					parametriGenerazioneBustaErrore.setErroreIntegrazione(ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
							get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_504_AUTORIZZAZIONE));
					
					OpenSPCoop2Message errorOpenSPCoopMsg = generaBustaErroreProcessamento(parametriGenerazioneBustaErrore,e);

					// Nota: la bustaRichiesta e' stata trasformata da generaErroreProcessamento
					parametriInvioBustaErrore.setOpenspcoopMsg(errorOpenSPCoopMsg);
					parametriInvioBustaErrore.setBusta(parametriGenerazioneBustaErrore.getBusta());
					sendRispostaBustaErrore(parametriInvioBustaErrore);
				}
				openspcoopstate.releaseResource();
				return;
			}
			finally {
				transaction.getTempiElaborazione().endAutorizzazione();
			}
		}







		
		
		
		
		
		
		
		
		/* --------------- Verifica tipo soggetto fruitore e tipo servizio rispetto al canale utilizzato 
		 * (NOTA!: non sportarlo prima senno non si supera eventuali certificazioni) --------------- */
		msgDiag.mediumDebug("Verifica canale utilizzato...");
		List<String> tipiSoggettiSupportatiCanale = protocolFactory.createProtocolConfiguration().getTipiSoggetti();
		List<String> tipiServiziSupportatiCanale = protocolFactory.createProtocolConfiguration().getTipiServizi(requestMessage.getServiceBinding());
		ErroreCooperazione erroreVerificaTipoByProtocol = null;
		// Nota: se qualche informazione e' null verranno segnalati altri errori
		if(soggettoFruitore!=null && soggettoFruitore.getTipo()!=null && 
				tipiSoggettiSupportatiCanale.contains(soggettoFruitore.getTipo())==false){
			parametriGenerazioneBustaErrore.setIntegrationFunctionError(IntegrationFunctionError.NOT_SUPPORTED_BY_PROTOCOL);
			msgDiag.logPersonalizzato("protocolli.tipoSoggetto.fruitore.unsupported");
			erroreVerificaTipoByProtocol = ErroriCooperazione.TIPO_MITTENTE_NON_VALIDO.getErroreCooperazione();
		}
		else if(idServizio!=null && idServizio.getSoggettoErogatore()!=null && idServizio.getSoggettoErogatore().getTipo()!=null &&
				tipiSoggettiSupportatiCanale.contains(idServizio.getSoggettoErogatore().getTipo())==false){
			parametriGenerazioneBustaErrore.setIntegrationFunctionError(IntegrationFunctionError.NOT_SUPPORTED_BY_PROTOCOL);
			msgDiag.logPersonalizzato("protocolli.tipoSoggetto.erogatore.unsupported");
			erroreVerificaTipoByProtocol = ErroriCooperazione.TIPO_DESTINATARIO_NON_VALIDO.getErroreCooperazione();
		}
		else if(idServizio!=null && idServizio.getTipo()!=null && 
				tipiServiziSupportatiCanale.contains(idServizio.getTipo())==false){
			parametriGenerazioneBustaErrore.setIntegrationFunctionError(IntegrationFunctionError.NOT_SUPPORTED_BY_PROTOCOL);
			msgDiag.logPersonalizzato("protocolli.tipoServizio.unsupported");
			erroreVerificaTipoByProtocol = ErroriCooperazione.TIPO_SERVIZIO_NON_VALIDO.getErroreCooperazione();
		}
			
		if(erroreVerificaTipoByProtocol!=null){
			if(this.msgContext.isGestioneRisposta()){

				parametriGenerazioneBustaErrore.setBusta(bustaRichiesta);							
				parametriGenerazioneBustaErrore.setErroreCooperazione(erroreVerificaTipoByProtocol);

				OpenSPCoop2Message errorOpenSPCoopMsg = generaBustaErroreValidazione(parametriGenerazioneBustaErrore);
				
				// Nota: la bustaRichiesta e' stata trasformata da generaErroreProcessamento
				parametriInvioBustaErrore.setOpenspcoopMsg(errorOpenSPCoopMsg);
				parametriInvioBustaErrore.setBusta(parametriGenerazioneBustaErrore.getBusta());
				sendRispostaBustaErrore(parametriInvioBustaErrore);

			}
			openspcoopstate.releaseResource();
			return;
		}
		
		
		
		
		
		
		
		
		
		




		/* ------------ Validazione Contenuti Applicativi e Autorizzazione per Contenuto  ------------- */
		ValidazioneContenutiApplicativi validazioneContenutoApplicativoApplicativo = null;
		List<Proprieta> proprietaValidazioneContenutoApplicativoApplicativo = null;
		String tipoAutorizzazionePerContenuto = null;
		if(functionAsRouter == false && 
				isMessaggioErroreProtocollo==false && 
				erroriProcessamento.size()==0 && erroriValidazione.size()==0 &&
				bustaDiServizio==false &&
				pa!=null){
			msgDiag.mediumDebug("Controllo abilitazione validazione XSD della richiesta...");
			boolean isRicevutaAsincrona_modalitaAsincrona = false;
			try{


				/* ----------- Comprensione profilo -------------- */
				if(     (org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.ASINCRONO_SIMMETRICO.equals(bustaRichiesta.getProfiloDiCollaborazione()) ||
						org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.ASINCRONO_ASIMMETRICO.equals(bustaRichiesta.getProfiloDiCollaborazione())) &&
						(bustaRichiesta.getRiferimentoMessaggio()!=null) )
				{
					msgDiag.mediumDebug("Controllo abilitazione validazione XSD della richiesta (check asincrono)...");
					// La validazione non deve essere effettuata se abbiamo una ricevuta asincrona, 'modalita' asincrona'

					if(org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.ASINCRONO_SIMMETRICO.equals(bustaRichiesta.getProfiloDiCollaborazione())) {	

						//	Richiesta Asincrona
						if(bustaRichiesta.getRiferimentoMessaggio()==null){
							validazioneContenutoApplicativoApplicativo = configurazionePdDReader.getTipoValidazioneContenutoApplicativo(pa,implementazionePdDMittente, true);
							proprietaValidazioneContenutoApplicativoApplicativo = pa.getProprietaList();
							tipoAutorizzazionePerContenuto = configurazionePdDReader.getAutorizzazioneContenuto(pa);
						}else{
							//	Risposta Asincrona
							if(RuoloBusta.RISPOSTA.equals(ruoloBustaRicevuta.toString())){
								validazioneContenutoApplicativoApplicativo = configurazionePdDReader.getTipoValidazioneContenutoApplicativo(pd,implementazionePdDMittente, true);
								proprietaValidazioneContenutoApplicativoApplicativo = pd.getProprietaList();
								// NOTA: deve essere registrato un tipo di autorizzazione per contenuto busta uguale al tipo di autorizzazione utilizzato lato servizi applicativi.
								tipoAutorizzazionePerContenuto = configurazionePdDReader.getAutorizzazioneContenuto(pd);
							}
							//	Ricevuta alla richiesta/risposta.
							else if(RuoloBusta.RICEVUTA_RICHIESTA.equals(ruoloBustaRicevuta.toString()) || 
									RuoloBusta.RICEVUTA_RISPOSTA.equals(ruoloBustaRicevuta.toString()) ){	
								if( configurazionePdDReader.ricevutaAsincronaSimmetricaAbilitata(pd))	{
									isRicevutaAsincrona_modalitaAsincrona = true;	
								}else{
									validazioneContenutoApplicativoApplicativo = configurazionePdDReader.getTipoValidazioneContenutoApplicativo(pd,implementazionePdDMittente, true);
									proprietaValidazioneContenutoApplicativoApplicativo = pd.getProprietaList();
									// NOTA: deve essere registrato un tipo di autorizzazione per contenuto busta uguale al tipo di autorizzazione utilizzato lato servizi applicativi.
									tipoAutorizzazionePerContenuto = configurazionePdDReader.getAutorizzazioneContenuto(pd);
								}
							}
						}
					}
					// Profilo Asincrono Asimmetrico
					else if(org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.ASINCRONO_ASIMMETRICO.equals(bustaRichiesta.getProfiloDiCollaborazione())) {	

						//	Richiesta Asincrona
						if(bustaRichiesta.getRiferimentoMessaggio()==null){
							validazioneContenutoApplicativoApplicativo = configurazionePdDReader.getTipoValidazioneContenutoApplicativo(pa,implementazionePdDMittente, true);
							proprietaValidazioneContenutoApplicativoApplicativo = pa.getProprietaList();
							tipoAutorizzazionePerContenuto = configurazionePdDReader.getAutorizzazioneContenuto(pa);
						}else{
							//	Risposta Asincrona
							if(RuoloBusta.RISPOSTA.equals(ruoloBustaRicevuta.toString())){
								validazioneContenutoApplicativoApplicativo = configurazionePdDReader.getTipoValidazioneContenutoApplicativo(pa,implementazionePdDMittente, true);
								proprietaValidazioneContenutoApplicativoApplicativo = pa.getProprietaList();
								tipoAutorizzazionePerContenuto = configurazionePdDReader.getAutorizzazioneContenuto(pa);
							}
							// Ricevuta alla richiesta/risposta.
							else if(RuoloBusta.RICEVUTA_RICHIESTA.equals(ruoloBustaRicevuta.toString()) || 
									RuoloBusta.RICEVUTA_RISPOSTA.equals(ruoloBustaRicevuta.toString()) ){	
								if( configurazionePdDReader.ricevutaAsincronaAsimmetricaAbilitata(pd))	{
									isRicevutaAsincrona_modalitaAsincrona = true;	
								}else{
									validazioneContenutoApplicativoApplicativo = configurazionePdDReader.getTipoValidazioneContenutoApplicativo(pd,implementazionePdDMittente, true);
									proprietaValidazioneContenutoApplicativoApplicativo = pd.getProprietaList();
									// NOTA: deve essere registrato un tipo di autorizzazione per contenuto busta uguale al tipo di autorizzazione utilizzato lato servizi applicativi.
									tipoAutorizzazionePerContenuto = configurazionePdDReader.getAutorizzazioneContenuto(pd);
								}
							}
						}
					}
					// Chiudo eventuali prepared statement, che non voglio eseguire.
					((StateMessage)openspcoopstate.getStatoRichiesta()).closePreparedStatement();
				}else{
					msgDiag.mediumDebug("Controllo abilitazione validazione dei contenuti applicativi della richiesta...");
					validazioneContenutoApplicativoApplicativo = configurazionePdDReader.getTipoValidazioneContenutoApplicativo(pa,implementazionePdDMittente, true);
					proprietaValidazioneContenutoApplicativoApplicativo = pa.getProprietaList();
					tipoAutorizzazionePerContenuto = configurazionePdDReader.getAutorizzazioneContenuto(pa);
				}
			}catch(Exception e){
				msgDiag.logErroreGenerico(e,"getTipoValidazione/Autorizzazione ContenutoApplicativo");
				if(this.msgContext.isGestioneRisposta()){
					
					parametriGenerazioneBustaErrore.setBusta(bustaRichiesta);
					parametriGenerazioneBustaErrore.setErroreIntegrazione(ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
							get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_536_CONFIGURAZIONE_NON_DISPONIBILE));
					OpenSPCoop2Message errorOpenSPCoopMsg = generaBustaErroreProcessamento(parametriGenerazioneBustaErrore,e);
					
					// Nota: la bustaRichiesta e' stata trasformata da generaErroreProcessamento
					parametriInvioBustaErrore.setOpenspcoopMsg(errorOpenSPCoopMsg);
					parametriInvioBustaErrore.setBusta(parametriGenerazioneBustaErrore.getBusta());
					sendRispostaBustaErrore(parametriInvioBustaErrore);
				}
				openspcoopstate.releaseResource();
				return;
			}
			if(isRicevutaAsincrona_modalitaAsincrona==false){

				if(validazioneContenutoApplicativoApplicativo!=null && validazioneContenutoApplicativoApplicativo.getTipo()!=null){
					String tipo = ValidatoreMessaggiApplicativi.getTipo(validazioneContenutoApplicativoApplicativo);
					this.msgContext.getIntegrazione().setTipoValidazioneContenuti(tipo);
					msgDiag.addKeyword(CostantiPdD.KEY_TIPO_VALIDAZIONE_CONTENUTI, tipo);
					msgDiag.addKeyword(CostantiPdD.KEY_DETAILS_VALIDAZIONE_CONTENUTI,"");
				}
				
				// VALIDAZIONE CONTENUTI APPLICATIVI
				
				if(CostantiConfigurazione.STATO_CON_WARNING_ABILITATO.equals(validazioneContenutoApplicativoApplicativo.getStato())||
						CostantiConfigurazione.STATO_CON_WARNING_WARNING_ONLY.equals(validazioneContenutoApplicativoApplicativo.getStato())){
				
					transaction.getTempiElaborazione().startValidazioneRichiesta();
					ByteArrayInputStream binXSD = null;
					try{
												
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
							msgDiag.mediumDebug("Validazione della richiesta (initValidator)...");
							ValidatoreMessaggiApplicativi validatoreMessaggiApplicativi = 
								new ValidatoreMessaggiApplicativi(registroServiziReader,idServizio,requestMessage,readInterface,
										propertiesReader.isValidazioneContenutiApplicativi_rpcLiteral_xsiType_gestione(),
										proprietaValidazioneContenutoApplicativoApplicativo);
	
							// Validazione WSDL 
							if( CostantiConfigurazione.VALIDAZIONE_CONTENUTI_APPLICATIVI_INTERFACE.equals(validazioneContenutoApplicativoApplicativo.getTipo()) 
									||
									CostantiConfigurazione.VALIDAZIONE_CONTENUTI_APPLICATIVI_OPENSPCOOP.equals(validazioneContenutoApplicativoApplicativo.getTipo())
							){
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
								new ValidatoreMessaggiApplicativiRest(registroServiziReader, idServizio, requestMessage, readInterface, proprietaValidazioneContenutoApplicativoApplicativo,
										protocolFactory, pddContext);
							
							if(CostantiConfigurazione.VALIDAZIONE_CONTENUTI_APPLICATIVI_XSD.equals(validazioneContenutoApplicativoApplicativo.getTipo()) &&
									requestMessage.castAsRest().hasContent()) {
								
								// Validazione XSD
								msgDiag.mediumDebug("Validazione xsd della richiesta ...");
								validatoreMessaggiApplicativi.validateWithSchemiXSD(true);
								
							}
							else {
								
								// Validazione Interface
								validatoreMessaggiApplicativi.validateRequestWithInterface(true);
								
							}
							
						}
						
						msgDiag.logPersonalizzato("validazioneContenutiApplicativiRichiestaEffettuata");
	
					}catch(ValidatoreMessaggiApplicativiException ex){
						msgDiag.addKeywordErroreProcessamento(ex);
						logCore.error("[ValidazioneContenutiApplicativi Richiesta] "+ex.getMessage(),ex);
						if (CostantiConfigurazione.STATO_CON_WARNING_WARNING_ONLY.equals(validazioneContenutoApplicativoApplicativo.getStato())) {
							msgDiag.logPersonalizzato("validazioneContenutiApplicativiRichiestaNonRiuscita.warningOnly");
						}
						else {
							msgDiag.logPersonalizzato("validazioneContenutiApplicativiRichiestaNonRiuscita");
						}
						if(CostantiConfigurazione.STATO_CON_WARNING_WARNING_ONLY.equals(validazioneContenutoApplicativoApplicativo.getStato()) == false){
							
							pddContext.addObject(org.openspcoop2.core.constants.Costanti.ERRORE_VALIDAZIONE_RICHIESTA, "true");
							
							// validazione abilitata
							if(this.msgContext.isGestioneRisposta()){
								
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
								parametriGenerazioneBustaErrore.setIntegrationFunctionError(integrationFunctionError);
								parametriGenerazioneBustaErrore.setBusta(bustaRichiesta);
								parametriGenerazioneBustaErrore.setErroreIntegrazione(ex.getErrore());
								OpenSPCoop2Message errorOpenSPCoopMsg = generaBustaErroreProcessamento(parametriGenerazioneBustaErrore,ex);
								
								// Nota: la bustaRichiesta e' stata trasformata da generaErroreProcessamento
								parametriInvioBustaErrore.setOpenspcoopMsg(errorOpenSPCoopMsg);
								parametriInvioBustaErrore.setBusta(parametriGenerazioneBustaErrore.getBusta());
								sendRispostaBustaErrore(parametriInvioBustaErrore);
							}
							openspcoopstate.releaseResource();
							return;
						}
					}catch(Exception ex){
						msgDiag.addKeywordErroreProcessamento(ex);
						logCore.error("Riscontrato errore durante la validazione dei contenuti applicativi (richiesta applicativa)",ex);
						if (CostantiConfigurazione.STATO_CON_WARNING_WARNING_ONLY.equals(validazioneContenutoApplicativoApplicativo.getStato())) {
							msgDiag.logPersonalizzato("validazioneContenutiApplicativiRichiestaNonRiuscita.warningOnly");
						}
						else {
							msgDiag.logPersonalizzato("validazioneContenutiApplicativiRichiestaNonRiuscita");
						}
						if(CostantiConfigurazione.STATO_CON_WARNING_WARNING_ONLY.equals(validazioneContenutoApplicativoApplicativo.getStato()) == false){
							
							pddContext.addObject(org.openspcoop2.core.constants.Costanti.ERRORE_VALIDAZIONE_RICHIESTA, "true");
							
							// validazione abilitata
							if(this.msgContext.isGestioneRisposta()){
								
								parametriGenerazioneBustaErrore.setBusta(bustaRichiesta);
								parametriGenerazioneBustaErrore.setErroreIntegrazione(ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
										get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_531_VALIDAZIONE_TRAMITE_INTERFACCIA_FALLITA));
	
								OpenSPCoop2Message errorOpenSPCoopMsg = generaBustaErroreProcessamento(parametriGenerazioneBustaErrore,ex);
								
								// Nota: la bustaRichiesta e' stata trasformata da generaErroreProcessamento
								parametriInvioBustaErrore.setOpenspcoopMsg(errorOpenSPCoopMsg);
								parametriInvioBustaErrore.setBusta(parametriGenerazioneBustaErrore.getBusta());
								sendRispostaBustaErrore(parametriInvioBustaErrore);
							}
							openspcoopstate.releaseResource();
							return;
						}
					}finally{
						transaction.getTempiElaborazione().endValidazioneRichiesta();
						if(binXSD!=null){
							try{
								binXSD.close();
							}catch(Exception e){}
						}
					}
				}
				else{
					msgDiag.logPersonalizzato("validazioneContenutiApplicativiRichiestaDisabilitata");
				}
			}
			
			
			
			// AUTORIZZAZIONE PER CONTENUTO
			this.msgContext.getIntegrazione().setTipoAutorizzazioneContenuto(tipoAutorizzazionePerContenuto);
			if(tipoAutorizzazionePerContenuto!=null){
				msgDiag.addKeyword(CostantiPdD.KEY_TIPO_AUTORIZZAZIONE_CONTENUTO, tipoAutorizzazionePerContenuto);
			}
			if (CostantiConfigurazione.AUTORIZZAZIONE_NONE.equalsIgnoreCase(tipoAutorizzazionePerContenuto) == false) {
				
				transaction.getTempiElaborazione().startAutorizzazioneContenuti();
				try {
					
					String identitaMittente = null;
					if(credenziali!=null && !soggettoAutenticato){
						if(!"".equals(credenziali.toString())){
							identitaMittente = credenziali.toString();
							if(identitaMittente.endsWith(" ")){
								identitaMittente = identitaMittente.substring(0, identitaMittente.length()-1);	
							}
						}
					}
					String subjectMessageSecurity = null;
					if(messageSecurityContext!=null){
						subjectMessageSecurity = messageSecurityContext.getSubject();
					}
					
					IDServizio idServizioPerAutorizzazione = getIdServizioPerAutorizzazione(idServizio, soggettoFruitore, functionAsRouter, bustaRichiesta, ruoloBustaRicevuta); 
					IDSoggetto idSoggettoMittentePerAutorizzazione = getIDSoggettoMittentePerAutorizzazione(idServizio, soggettoFruitore, functionAsRouter, bustaRichiesta, ruoloBustaRicevuta, supportatoAutenticazioneSoggetti);
					Soggetto soggettoMittentePerAutorizzazione = null;
					if(idSoggettoMittentePerAutorizzazione!=null){
						soggettoMittentePerAutorizzazione = registroServiziReader.getSoggetto(idSoggettoMittentePerAutorizzazione, null);
					}
					
					String tipoMessaggio = "messaggio";
					if(RuoloBusta.RICEVUTA_RICHIESTA.equals(ruoloBustaRicevuta.toString()) || RuoloBusta.RICEVUTA_RISPOSTA.equals(ruoloBustaRicevuta.toString())){
						tipoMessaggio = "ricevuta asincrona";
					}
					msgDiag.addKeyword(CostantiPdD.KEY_TIPO_MESSAGGIO_BUSTA, tipoMessaggio);
					if(idSoggettoMittentePerAutorizzazione!=null){
						msgDiag.addKeyword(CostantiPdD.KEY_MITTENTE_E_SERVIZIO_DA_AUTORIZZARE, "fruitore ["+idSoggettoMittentePerAutorizzazione.toString()+"] -> servizio ["+idServizioPerAutorizzazione.toString()+"]");
					}
					else{
						msgDiag.addKeyword(CostantiPdD.KEY_MITTENTE_E_SERVIZIO_DA_AUTORIZZARE, "servizio ["+idServizioPerAutorizzazione.toString()+"]");
					}
					if(identitaMittente!=null)
						msgDiag.addKeyword(CostantiPdD.KEY_CREDENZIALI_MITTENTE_MSG, " credenzialiMittente "+identitaMittente);
					else
						msgDiag.addKeyword(CostantiPdD.KEY_CREDENZIALI_MITTENTE_MSG, "");
					if(servizioApplicativoFruitore!=null)
						msgDiag.addKeyword(CostantiPdD.KEY_SA_FRUITORE_MSG, " identitaServizioApplicativoFruitore ["+servizioApplicativoFruitore+"]");
					else
						msgDiag.addKeyword(CostantiPdD.KEY_SA_FRUITORE_MSG, "");
					if(subjectMessageSecurity!=null)
						msgDiag.addKeyword(CostantiPdD.KEY_SUBJECT_MESSAGE_SECURITY_MSG, " subjectMessageSecurity ["+subjectMessageSecurity+"]");
					else
						msgDiag.addKeyword(CostantiPdD.KEY_SUBJECT_MESSAGE_SECURITY_MSG, "");
					msgDiag.logPersonalizzato("autorizzazioneContenutiBusteInCorso");
					
					if(datiInvocazione==null){
						
						IDServizioApplicativo identitaServizioApplicativoFruitore = new IDServizioApplicativo();
						identitaServizioApplicativoFruitore.setNome(servizioApplicativoFruitore);
						identitaServizioApplicativoFruitore.setIdSoggettoProprietario(idSoggettoMittentePerAutorizzazione);
						
						datiInvocazione = new DatiInvocazionePortaApplicativa();
						datiInvocazione.setInfoConnettoreIngresso(inRequestContext.getConnettore());
						datiInvocazione.setIdServizio(idServizioPerAutorizzazione);
						datiInvocazione.setState(openspcoopstate.getStatoRichiesta());
						datiInvocazione.setCredenzialiPdDMittente(credenziali);
						datiInvocazione.setIdentitaServizioApplicativoFruitore(identitaServizioApplicativoFruitore);
						datiInvocazione.setSubjectServizioApplicativoFruitoreFromMessageSecurityHeader(subjectMessageSecurity);
						datiInvocazione.setIdPA(idPA);
						datiInvocazione.setPa(pa);
						datiInvocazione.setIdPD(idPD);
						datiInvocazione.setPd(pd);
						datiInvocazione.setIdSoggettoFruitore(idSoggettoMittentePerAutorizzazione);
						datiInvocazione.setSoggettoFruitore(soggettoMittentePerAutorizzazione);
						
						datiInvocazione.setRuoloBusta(ruoloBustaRicevuta);
					}
					
					// Controllo Autorizzazione
					EsitoAutorizzazionePortaApplicativa esito = 
							GestoreAutorizzazione.verificaAutorizzazioneContenutoPortaApplicativa(tipoAutorizzazionePerContenuto, datiInvocazione, pddContext, protocolFactory, requestMessage, logCore);
					CostantiPdD.addKeywordInCache(msgDiag, esito.isEsitoPresenteInCache(),
							pddContext, CostantiPdD.KEY_INFO_IN_CACHE_FUNZIONE_AUTORIZZAZIONE_CONTENUTI);
					if(esito.getDetails()==null){
						msgDiag.addKeyword(CostantiPdD.KEY_DETAILS, "");
					}else{
						msgDiag.addKeyword(CostantiPdD.KEY_DETAILS, " ("+esito.getDetails()+")");
					}
					if(esito.isAutorizzato()==false){
						pddContext.addObject(org.openspcoop2.core.constants.Costanti.ERRORE_AUTORIZZAZIONE, "true");
						try{
							msgDiag.addKeyword(CostantiPdD.KEY_ERRORE_PROCESSAMENTO, esito.getErroreCooperazione().getDescrizione(protocolFactory));
						}catch(Exception e){
							logCore.error("getDescrizione Error:"+e.getMessage(),e);
						}
						msgDiag.addKeyword(CostantiPdD.KEY_POSIZIONE_ERRORE, traduttore.toString(esito.getErroreCooperazione().getCodiceErrore()));
						msgDiag.logPersonalizzato("autorizzazioneContenutiBusteFallita");
						if(this.msgContext.isGestioneRisposta()){
							OpenSPCoop2Message errorMsg = null;
							parametriGenerazioneBustaErrore.setBusta(bustaRichiesta);
							parametriGenerazioneBustaErrore.setErroreCooperazione(esito.getErroreCooperazione());
							IntegrationFunctionError integrationFunctionError = esito.getIntegrationFunctionError();
							if(CodiceErroreCooperazione.SICUREZZA_AUTORIZZAZIONE_FALLITA.equals(esito.getErroreCooperazione().getCodiceErrore()) 
									|| CodiceErroreCooperazione.SICUREZZA_FALSIFICAZIONE_MITTENTE.equals(esito.getErroreCooperazione().getCodiceErrore())){
								if(integrationFunctionError==null) {
									integrationFunctionError = IntegrationFunctionError.CONTENT_AUTHORIZATION_DENY;
								}
								parametriGenerazioneBustaErrore.setIntegrationFunctionError(integrationFunctionError);
								errorMsg = generaBustaErroreValidazione(parametriGenerazioneBustaErrore);
							}
							else{
								if(integrationFunctionError==null) {
									integrationFunctionError = IntegrationFunctionError.INTERNAL_REQUEST_ERROR;
								}
								parametriGenerazioneBustaErrore.setIntegrationFunctionError(integrationFunctionError);
								errorMsg = generaBustaErroreProcessamento(parametriGenerazioneBustaErrore,esito.getEccezioneProcessamento());
							}

							// Nota: la bustaRichiesta e' stata trasformata da generaErroreProcessamento
							parametriInvioBustaErrore.setOpenspcoopMsg(errorMsg);
							parametriInvioBustaErrore.setBusta(parametriGenerazioneBustaErrore.getBusta());
							sendRispostaBustaErrore(parametriInvioBustaErrore);
						}
						
						openspcoopstate.releaseResource();
						return;
					}else{
						msgDiag.logPersonalizzato("autorizzazioneContenutiBusteEffettuata");
					}

				}catch(Exception ex){
					msgDiag.logErroreGenerico(ex,"AutorizzazioneContenuto Messaggio("+bustaRichiesta.getID()+")");
					logCore.error("Riscontrato errore durante il processo di Autorizzazione del Contenuto per il messaggio con identificativo ["+bustaRichiesta.getID()+"]",ex);
					if(this.msgContext.isGestioneRisposta()){
						
						parametriGenerazioneBustaErrore.setBusta(bustaRichiesta);
						parametriGenerazioneBustaErrore.setErroreIntegrazione(ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
								get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_542_AUTORIZZAZIONE_CONTENUTO));
						OpenSPCoop2Message errorMsg = generaBustaErroreProcessamento(parametriGenerazioneBustaErrore,ex);

						// Nota: la bustaRichiesta e' stata trasformata da generaErroreProcessamento
						parametriInvioBustaErrore.setOpenspcoopMsg(errorMsg);
						parametriInvioBustaErrore.setBusta(parametriGenerazioneBustaErrore.getBusta());
						sendRispostaBustaErrore(parametriInvioBustaErrore);
					}
					openspcoopstate.releaseResource();
					return;
				}
				finally {
					transaction.getTempiElaborazione().endAutorizzazioneContenuti();
				}
			}
			else{
				msgDiag.logPersonalizzato("autorizzazioneContenutiBusteDisabilitata");
			}
		}



		







		Utilities.printFreeMemory("RicezioneBuste - Recupero configurazione per salvataggio risposta in cache ...");
		msgDiag.mediumDebug("Recupero configurazione per salvataggio risposta in cache ...");
		try{
			ResponseCachingConfigurazione responseCachingConfig = null;
			if(pa!=null) {
				responseCachingConfig = configurazionePdDReader.getConfigurazioneResponseCaching(pa);
			}
			else {
				responseCachingConfig = configurazionePdDReader.getConfigurazioneResponseCaching();
			}
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
			logCore.error("Calcolo Digest Salvataggio Risposta non riuscito: "	+ e);
			if (this.msgContext.isGestioneRisposta()) {
				
				parametriGenerazioneBustaErrore.setBusta(bustaRichiesta);
				parametriGenerazioneBustaErrore.setErroreIntegrazione(ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
						get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_561_DIGEST_REQUEST));

				OpenSPCoop2Message errorOpenSPCoopMsg = generaBustaErroreProcessamento(parametriGenerazioneBustaErrore,e);
				
				// Nota: la bustaRichiesta e' stata trasformata da generaErroreProcessamento
				parametriInvioBustaErrore.setOpenspcoopMsg(errorOpenSPCoopMsg);
				parametriInvioBustaErrore.setBusta(parametriGenerazioneBustaErrore.getBusta());
				sendRispostaBustaErrore(parametriInvioBustaErrore);
			}
			openspcoopstate.releaseResource();
			return;
		}
		
		
		
		





		/* ------------- Modalita' di gestione ---------------------------- */
		try {
			if ( oneWayStateless || sincronoStateless || asincronoStateless || oneWayVersione11 || routingStateless ) {
				openspcoopstate = OpenSPCoopState.toStateless(((OpenSPCoopStateful)openspcoopstate), true);
				parametriGenerazioneBustaErrore.setOpenspcoop(openspcoopstate);
				parametriInvioBustaErrore.setOpenspcoop(openspcoopstate);
			}
		} catch (Exception e) {
			msgDiag.logErroreGenerico(e,"OpenSPCoopState.toStateless");
			logCore.error("Creazione stato STATEFUL/STATELESS non riuscita: "	+ e);
			if (this.msgContext.isGestioneRisposta()) {
				
				parametriGenerazioneBustaErrore.setBusta(bustaRichiesta);
				parametriGenerazioneBustaErrore.setErroreIntegrazione(ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
						get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_536_CONFIGURAZIONE_NON_DISPONIBILE));

				OpenSPCoop2Message errorOpenSPCoopMsg = generaBustaErroreProcessamento(parametriGenerazioneBustaErrore,e);
				
				// Nota: la bustaRichiesta e' stata trasformata da generaErroreProcessamento
				parametriInvioBustaErrore.setOpenspcoopMsg(errorOpenSPCoopMsg);
				parametriInvioBustaErrore.setBusta(parametriGenerazioneBustaErrore.getBusta());
				sendRispostaBustaErrore(parametriInvioBustaErrore);
			}
			openspcoopstate.releaseResource();
			return;
		}
















		/* -------- Gestione Richiesta ---------- */
		String tipoMsg = Costanti.INBOX;
		if(functionAsRouter)
			tipoMsg = Costanti.OUTBOX;
		GestoreMessaggi msgRequest = new GestoreMessaggi(openspcoopstate, true, idMessageRequest, tipoMsg,msgDiag, inRequestContext.getPddContext());
		msgRequest.setOneWayVersione11(oneWayVersione11);
		msgRequest.setRoutingStateless(routingStateless);
		RepositoryBuste repositoryBuste = new RepositoryBuste(openspcoopstate.getStatoRichiesta(), true, protocolFactory);














		/* ------------
	   Controllo di non aver gia' registrato la richiesta 
	   (se non l'ho registrata, non significa che non l'abbia gia' ricevuta, puo' darsi che l'abbia ricevuta
	   e gia' completata di elaborare. 
	   Il controllo dell'history in Sbustamento effettuera' poi questo controllo e generera' un eventuale riscontro)
	   L'unico caso in cui posso ricevere piu' di una copia di una busta dovrebbe avvenire con profili asincroni in modalita asincrona
	   e con profili oneWay. In questo caso se ricevo una busta che sto gia' processando gestisco il caso particolare.
	   Negli altri casi emetto anche un messaggio Errore di msg gia ricevuto.
	   ------------
		 */
		msgDiag.mediumDebug("Controllo presenza del messaggio gia' in gestione...");
		if( (functionAsRouter==false) || (routingStateless==false) ){
			try{
				if(  msgRequest.existsMessage_noCache() ){

					// Se il proprietario attuale e' GestoreMessaggi, forzo l'eliminazione e continuo a processare il messaggio.
					String proprietarioMessaggio = msgRequest.getProprietario(this.msgContext.getIdModulo());
					if(TimerGestoreMessaggi.ID_MODULO.equals(proprietarioMessaggio)){
						msgDiag.logPersonalizzato("messaggioInGestione.marcatoDaEliminare");
						String msg = msgDiag.getMessaggio_replaceKeywords("messaggioInGestione.marcatoDaEliminare");
						if(propertiesReader.isMsgGiaInProcessamento_useLock()) {
							msgRequest._deleteMessageWithLock(msg,propertiesReader.getMsgGiaInProcessamento_AttesaAttiva(),
								propertiesReader.getMsgGiaInProcessamento_CheckInterval());
						}
						else {
							msgRequest.deleteMessageByNow();
						}
					}

					// Altrimenti gestisco il duplicato
					else{

						boolean rispostaModalitaSincrona = org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.SINCRONO.equals(bustaRichiesta.getProfiloDiCollaborazione()) ||
						(org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.ASINCRONO_SIMMETRICO.equals(bustaRichiesta.getProfiloDiCollaborazione()) && configurazionePdDReader.ricevutaAsincronaSimmetricaAbilitata(pa)) ||
						(org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.ASINCRONO_ASIMMETRICO.equals(bustaRichiesta.getProfiloDiCollaborazione()) && configurazionePdDReader.ricevutaAsincronaAsimmetricaAbilitata(pa));

						// Se la modalita e' sincrona non deve essere possibile ricevere una busta due volte con lo stesso identificativo
						if(rispostaModalitaSincrona){
							msgDiag.addKeyword(CostantiPdD.KEY_PROPRIETARIO_MESSAGGIO, proprietarioMessaggio);
							msgDiag.logPersonalizzato("messaggioInGestione.gestioneSincrona");
							pddContext.addObject(org.openspcoop2.core.constants.Costanti.RICHIESTA_DUPLICATA, "true");
							if(this.msgContext.isGestioneRisposta()){
								
								parametriGenerazioneBustaErrore.setBusta(bustaRichiesta);
								parametriGenerazioneBustaErrore.setErroreIntegrazione(ErroriIntegrazione.ERRORE_537_BUSTA_GIA_RICEVUTA.get537_BustaGiaRicevuta(idMessageRequest));
								parametriGenerazioneBustaErrore.setIntegrationFunctionError(IntegrationFunctionError.CONFLICT_IN_QUEUE);
								OpenSPCoop2Message errorOpenSPCoopMsg = generaBustaErroreProcessamento(parametriGenerazioneBustaErrore,null);
								
								// Nota: la bustaRichiesta e' stata trasformata da generaErroreProcessamento
								parametriInvioBustaErrore.setOpenspcoopMsg(errorOpenSPCoopMsg);
								parametriInvioBustaErrore.setBusta(parametriGenerazioneBustaErrore.getBusta());
								sendRispostaBustaErrore(parametriInvioBustaErrore);
							}
							openspcoopstate.releaseResource();
							return;
						}

						else{
							// gestione risposta gia presente
							// se profiloTrasmissione=ALPIUUNAVOLTA
							//     Se il proprietario attuale e' il modulo consegna contenuti applicativi posso ritornare un soap vuoto, (o riscontro/ricevuta a seconda del profilo)
							//     Altrimenti provo ad aspettare un tempo ragionevole e ogni tanto rileggo il proprietario. Non appena raggiunge il modulo consegna contenuti applicativi
							//     posso ritornare un soap vuoto (o riscontro/ricevuta a seconda del profilo)
							// altrimenti provo ad aspettare che il messaggio non esista piu' o abbia cmq come proprietario GestoreMessaggi per poi eliminarlo.
							// 
							// Se scade il timeout cmq genero un errore di busta gia' in gestione.
							msgDiag.addKeyword(CostantiPdD.KEY_PROPRIETARIO_MESSAGGIO, proprietarioMessaggio);
							msgDiag.logPersonalizzato("messaggioInGestione.gestioneAsincrona");
							long scadenzaWhile = DateManager.getTimeMillis() + propertiesReader.getMsgGiaInProcessamento_AttesaAttiva();
							boolean isErrore_MsgGiaRicevuto = true;
							boolean msgAttesaFineProcessamento = false;
							int millisecondiTrascorsi = 0;
							while( DateManager.getTimeMillis() < scadenzaWhile  ){

								proprietarioMessaggio = msgRequest.getProprietario(this.msgContext.getIdModulo());

								if( (Inoltro.SENZA_DUPLICATI.equals(bustaRichiesta.getInoltro())) || 
										(this.msgContext.isForzaFiltroDuplicati_msgGiaInProcessamento())  ){

									// Se sono entrato in questo controllo vuole dire che prima esisteva un msg che aveva come proprietario Sbustamento o RicezioneBuste.
									// Quindi se ho raggiunto ConsegnaContenutiApplicativi, GestoreMessaggi o non esiste piu' il messaggio, vuole dire che e' stato elaborato.
									if(ConsegnaContenutiApplicativi.ID_MODULO.equals(proprietarioMessaggio) || 
											TimerGestoreMessaggi.ID_MODULO.equals(proprietarioMessaggio) ||
											(msgRequest.existsMessage_noCache()==false) ){
										
										pddContext.addObject(org.openspcoop2.core.constants.Costanti.RICHIESTA_DUPLICATA, "true");
										
										if(this.msgContext.isGestioneRisposta()){
											this.msgContext.setMessageResponse(this.generaRisposta_msgGiaRicevuto(!this.msgContext.isForzaFiltroDuplicati_msgGiaInProcessamento(),
													bustaRichiesta,infoIntegrazione, msgDiag, openspcoopstate, logCore, configurazionePdDReader,propertiesReader,
													versioneProtocollo,ruoloBustaRicevuta,implementazionePdDMittente,protocolFactory,
													identitaPdD,idTransazione,loader,oneWayVersione11,implementazionePdDMittente,
													tracciamento,messageSecurityContext,
													correlazioneApplicativa,
													pddContext, IntegrationFunctionError.CONFLICT));
										}
										openspcoopstate.releaseResource();
										return;
									}

									if(msgAttesaFineProcessamento==false){
										msgDiag.addKeyword(CostantiPdD.KEY_TIMEOUT, ""+(propertiesReader.getMsgGiaInProcessamento_AttesaAttiva()/1000));
										msgDiag.logPersonalizzato("messaggioInGestione.attesaFineProcessamento.filtroDuplicatiAbilitato");
										msgAttesaFineProcessamento = true;
									}

								}else{

									if(msgAttesaFineProcessamento==false){
										msgDiag.addKeyword(CostantiPdD.KEY_TIMEOUT, ""+(propertiesReader.getMsgGiaInProcessamento_AttesaAttiva()/1000));
										msgDiag.logPersonalizzato("messaggioInGestione.attesaFineProcessamento.filtroDuplicatiDisabilitato");
										msgAttesaFineProcessamento = true;
									}

									boolean existsMessage = msgRequest.existsMessage_noCache();
									if(existsMessage==false){
										msgDiag.logPersonalizzato("messaggioInGestione.attesaFineProcessamento.filtroDuplicatiDisabilitato.attesaTerminata");
										isErrore_MsgGiaRicevuto = false;
										break;
									}

									if(TimerGestoreMessaggi.ID_MODULO.equals(proprietarioMessaggio)){
										msgDiag.logPersonalizzato("messaggioInGestione.attesaFineProcessamento.filtroDuplicatiDisabilitato.forzoEliminazione");
										String msg = msgDiag.getMessaggio_replaceKeywords("messaggioInGestione.attesaFineProcessamento.filtroDuplicatiDisabilitato.forzoEliminazione");
										if(propertiesReader.isMsgGiaInProcessamento_useLock()) {
											msgRequest._deleteMessageWithLock(msg,propertiesReader.getMsgGiaInProcessamento_AttesaAttiva()-millisecondiTrascorsi,
													propertiesReader.getMsgGiaInProcessamento_CheckInterval());
										}
										else {
											msgRequest.deleteMessageByNow();
										}
										isErrore_MsgGiaRicevuto = false;
										break;
									}

								}

								Utilities.sleep(propertiesReader.getMsgGiaInProcessamento_CheckInterval());
								millisecondiTrascorsi = millisecondiTrascorsi + propertiesReader.getMsgGiaInProcessamento_CheckInterval();

							}
							if(isErrore_MsgGiaRicevuto){
								msgDiag.logPersonalizzato("messaggioInGestione.attesaFineProcessamento.timeoutScaduto");
								pddContext.addObject(org.openspcoop2.core.constants.Costanti.RICHIESTA_DUPLICATA, "true");
								if(this.msgContext.isGestioneRisposta()){
									
									parametriGenerazioneBustaErrore.setBusta(bustaRichiesta);
									parametriGenerazioneBustaErrore.setErroreIntegrazione(ErroriIntegrazione.ERRORE_537_BUSTA_GIA_RICEVUTA.get537_BustaGiaRicevuta(idMessageRequest));
									parametriGenerazioneBustaErrore.setIntegrationFunctionError(IntegrationFunctionError.CONFLICT_IN_QUEUE);
									OpenSPCoop2Message errorOpenSPCoopMsg = generaBustaErroreProcessamento(parametriGenerazioneBustaErrore,null);
									
									// Nota: la bustaRichiesta e' stata trasformata da generaErroreProcessamento
									parametriInvioBustaErrore.setOpenspcoopMsg(errorOpenSPCoopMsg);
									parametriInvioBustaErrore.setBusta(parametriGenerazioneBustaErrore.getBusta());
									sendRispostaBustaErrore(parametriInvioBustaErrore,false);
								}
								openspcoopstate.releaseResource();
								return;
							}
						}
					}
				}
			}catch(Exception e){
				msgDiag.logErroreGenerico(e,"ControlloPresenzaMessaggioGiaInGestione");
				logCore.error("Controllo/gestione presenza messaggio gia in gestione non riuscito",e);
				if(this.msgContext.isGestioneRisposta()){
					
					parametriGenerazioneBustaErrore.setBusta(bustaRichiesta);
					parametriGenerazioneBustaErrore.setErroreIntegrazione(ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
							get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_536_CONFIGURAZIONE_NON_DISPONIBILE));

					OpenSPCoop2Message errorOpenSPCoopMsg = generaBustaErroreProcessamento(parametriGenerazioneBustaErrore,e);
					
					// Nota: la bustaRichiesta e' stata trasformata da generaErroreProcessamento
					parametriInvioBustaErrore.setOpenspcoopMsg(errorOpenSPCoopMsg);
					parametriInvioBustaErrore.setBusta(parametriGenerazioneBustaErrore.getBusta());
					sendRispostaBustaErrore(parametriInvioBustaErrore);
				}
				openspcoopstate.releaseResource();
				return;
			}
		}





















		/* ----------------   Creo sessione di gestione del messaggio ricevuto  --------------------- */
		msgDiag.mediumDebug("Registrazione messaggio di richiesta nel RepositoryMessaggi...");
		try{
			msgRequest.registraMessaggio(requestMessage,dataIngressoRichiesta, 
					(oneWayStateless || sincronoStateless || asincronoStateless || routingStateless),
					correlazioneApplicativa);
		}catch(Exception e){
			msgDiag.logErroreGenerico(e,"msgRequest.registraMessaggio");
			if(this.msgContext.isGestioneRisposta()){
				parametriGenerazioneBustaErrore.setBusta(bustaRichiesta);
				parametriGenerazioneBustaErrore.setErroreIntegrazione(ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
					get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_508_SAVE_REQUEST_MSG));

				OpenSPCoop2Message errorOpenSPCoopMsg = generaBustaErroreProcessamento(parametriGenerazioneBustaErrore,e);
				
				// Nota: la bustaRichiesta e' stata trasformata da generaErroreProcessamento
				parametriInvioBustaErrore.setOpenspcoopMsg(errorOpenSPCoopMsg);
				parametriInvioBustaErrore.setBusta(parametriGenerazioneBustaErrore.getBusta());
				sendRispostaBustaErrore(parametriInvioBustaErrore);
			}
			msgRequest.deleteMessageFromFileSystem(); // elimino richiesta salvata su fileSystem
			openspcoopstate.releaseResource();
			return;
		}












		/* ----------------   Salvo busta ricevuta (se non l'ho gia' ricevuta)   --------------------- */
		msgDiag.mediumDebug("Registrazione busta di richiesta nel RepositoryBuste...");
		try{
			if( (oneWayStateless==false) && (sincronoStateless==false) && (asincronoStateless==false)  && (routingStateless==false) ){
				if(repositoryBuste.isRegistrata(bustaRichiesta.getID(),tipoMsg)){
					try{
						repositoryBuste.aggiornaBusta(bustaRichiesta,tipoMsg,propertiesReader.getRepositoryIntervalloScadenzaMessaggi(),erroriValidazione);
						repositoryBuste.impostaUtilizzoPdD(bustaRichiesta.getID(),tipoMsg);
					}catch(Exception e){
						if(propertiesReader.isMsgGiaInProcessamento_useLock()) {
							String causa = "Aggiornamento dati busta con id ["+bustaRichiesta.getID()+"] tipo["+tipoMsg+"] non riuscito: "+e.getMessage();
							try{
								GestoreMessaggi.acquireLock(msgRequest,TimerLock.newInstance(TipoLock._getLockGestioneRepositoryMessaggi()), msgDiag, causa, propertiesReader.getMsgGiaInProcessamento_AttesaAttiva(), propertiesReader.getMsgGiaInProcessamento_CheckInterval());
								// errore che puo' avvenire a causa del Timer delle Buste (vedi spiegazione in classe GestoreMessaggi.deleteMessageWithLock)
								// Si riesegue tutto il codice isRegistrata e update o create con il lock. Stavolta se avviene un errore non e' dovuto al timer.
								if(repositoryBuste.isRegistrata(bustaRichiesta.getID(),tipoMsg)){
									repositoryBuste.aggiornaBusta(bustaRichiesta,tipoMsg,propertiesReader.getRepositoryIntervalloScadenzaMessaggi(),erroriValidazione);
									repositoryBuste.impostaUtilizzoPdD(bustaRichiesta.getID(),tipoMsg);
								}
								else{
									repositoryBuste.registraBusta(bustaRichiesta, tipoMsg, erroriValidazione, propertiesReader.getRepositoryIntervalloScadenzaMessaggi());
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

				}
				else{
					repositoryBuste.registraBusta(bustaRichiesta, tipoMsg, erroriValidazione, propertiesReader.getRepositoryIntervalloScadenzaMessaggi());
				}
				repositoryBuste.aggiornaInfoIntegrazione(bustaRichiesta.getID(),tipoMsg,infoIntegrazione);
			}else{
				((StatelessMessage)openspcoopstate.getStatoRichiesta()).setBusta(bustaRichiesta);
			}
		}catch(Exception e){
			msgDiag.logErroreGenerico(e,"repositoryBuste.registraBusta");
			if(this.msgContext.isGestioneRisposta()){
				
				parametriGenerazioneBustaErrore.setBusta(bustaRichiesta);
				parametriGenerazioneBustaErrore.setErroreIntegrazione(ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
						get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_525_GESTIONE_FUNZIONALITA_PROTOCOLLO));

				OpenSPCoop2Message errorOpenSPCoopMsg = generaBustaErroreProcessamento(parametriGenerazioneBustaErrore,e);
				
				// Nota: la bustaRichiesta e' stata trasformata da generaErroreProcessamento
				parametriInvioBustaErrore.setOpenspcoopMsg(errorOpenSPCoopMsg);	
				parametriInvioBustaErrore.setBusta(parametriGenerazioneBustaErrore.getBusta());
				sendRispostaBustaErrore(parametriInvioBustaErrore);
			}
			msgRequest.deleteMessageFromFileSystem(); // elimino richiesta salvata su fileSystem
			openspcoopstate.releaseResource();
			return;
		}




		

		
		
		
		






		/* ------------  Forward a Sbustamento o InoltroBuste (a seconda se il modulo assume funzione di Router) ------------- */
		try{
			if(functionAsRouter){ // solo stateful
				msgDiag.mediumDebug("Invio messaggio al modulo di InoltroBuste (router)...");
				msgRequest.aggiornaProprietarioMessaggio(org.openspcoop2.pdd.mdb.InoltroBuste.ID_MODULO);
				// Creazione InoltroBuste
				msgDiag.highDebug("Creazione ObjectMessage for send nell'infrastruttura.");
				inoltroMSG.setBusta(bustaRichiesta);
				RichiestaDelegata rd = new RichiestaDelegata(soggettoFruitore);
				rd.setDominio(identitaPdD);
				rd.setIdModuloInAttesa(this.msgContext.getIdModulo());
				rd.setIdServizio(idServizio);
				rd.setIdCorrelazioneApplicativa(correlazioneApplicativa);
				rd.setServizioApplicativo(servizioApplicativoFruitore);
				inoltroMSG.setRichiestaDelegata(rd);
				inoltroMSG.setImplementazionePdDSoggettoMittente(implementazionePdDMittente);
				inoltroMSG.setImplementazionePdDSoggettoDestinatario(implementazionePdDDestinatario);
				inoltroMSG.setPddContext(inRequestContext.getPddContext());

				// send jms solo x il comportamento stateful
				if (!routingStateless) {
					
					String classTypeNodeSender = null;
					INodeSender nodeSender = null;
					try{
						classTypeNodeSender = className.getNodeSender(propertiesReader.getNodeSender());
						nodeSender = (INodeSender) loader.newInstance(classTypeNodeSender);
						AbstractCore.init(nodeSender, pddContext, protocolFactory);
					}catch(Exception e){
						throw new Exception("Riscontrato errore durante il caricamento della classe ["+classTypeNodeSender+
								"] da utilizzare per la spedizione nell'infrastruttura: "+e.getMessage());
					}
					
					nodeSender.send(inoltroMSG, org.openspcoop2.pdd.mdb.InoltroBuste.ID_MODULO, msgDiag, 
							identitaPdD,this.msgContext.getIdModulo(), idMessageRequest, msgRequest);
				}
			}else{
				msgDiag.mediumDebug("Invio messaggio al modulo di Sbustamento...");
				msgRequest.aggiornaProprietarioMessaggio(org.openspcoop2.pdd.mdb.Sbustamento.ID_MODULO);

				// setto parametri SbustamentoMessage
				msgDiag.highDebug("Creazione ObjectMessage for send nell'infrastruttura.");
				richiestaApplicativa.setIdCorrelazioneApplicativa(correlazioneApplicativa);
				richiestaApplicativa.setIdentitaServizioApplicativoFruitore(servizioApplicativoFruitore);
				sbustamentoMSG.setRichiestaApplicativa(richiestaApplicativa);
				sbustamentoMSG.setBusta(bustaRichiesta);
				sbustamentoMSG.setErrors(erroriValidazione, integrationFunctionErrorValidazione);
				sbustamentoMSG.setMessaggioErroreProtocollo(isMessaggioErroreProtocollo);
				sbustamentoMSG.setIsBustaDiServizio(bustaDiServizio);
				sbustamentoMSG.setServizioCorrelato(validatore.getServizioCorrelato());
				sbustamentoMSG.setTipoServizioCorrelato(validatore.getTipoServizioCorrelato());
				sbustamentoMSG.setVersioneServizioCorrelato(validatore.getVersioneServizioCorrelato());
				sbustamentoMSG.setRuoloBustaRicevuta(ruoloBustaRicevuta);
				sbustamentoMSG.setOneWayVersione11(oneWayVersione11);
				sbustamentoMSG.setStateless((oneWayStateless || sincronoStateless || asincronoStateless));
				sbustamentoMSG.setImplementazionePdDSoggettoMittente(implementazionePdDMittente);
				sbustamentoMSG.setImplementazionePdDSoggettoDestinatario(implementazionePdDDestinatario);
				sbustamentoMSG.setPddContext(inRequestContext.getPddContext());
				sbustamentoMSG.setDettaglioEccezione(dettaglioEccezione);
				
				if(validatore.getInfoServizio()!=null){
					sbustamentoMSG.setFiltroDuplicatiRichiestoAccordo(Inoltro.SENZA_DUPLICATI.equals(validatore.getInfoServizio().getInoltro()));
					if(StatoFunzionalitaProtocollo.REGISTRO.equals(moduleManager.getConsegnaAffidabile(bustaRichiesta)))
						sbustamentoMSG.setConfermaRicezioneRichiestoAccordo(validatore.getInfoServizio().getConfermaRicezione());
					if(StatoFunzionalitaProtocollo.REGISTRO.equals(moduleManager.getConsegnaInOrdine(bustaRichiesta)))	
						sbustamentoMSG.setConsegnaOrdineRichiestoAccordo(validatore.getInfoServizio().getOrdineConsegna());
				}

				// send jms solo x il comportamento stateful
				if (!portaStateless) {
					logCore.debug(RicezioneBuste.ID_MODULO + " :eseguo send a sbustamento");
					
					String classTypeNodeSender = null;
					INodeSender nodeSender = null;
					try{
						classTypeNodeSender = className.getNodeSender(propertiesReader.getNodeSender());
						nodeSender = (INodeSender) loader.newInstance(classTypeNodeSender);
						AbstractCore.init(nodeSender, pddContext, protocolFactory);
					}catch(Exception e){
						throw new Exception("Riscontrato errore durante il caricamento della classe ["+classTypeNodeSender+
								"] da utilizzare per la spedizione nell'infrastruttura: "+e.getMessage());
					}
					
					nodeSender.send(sbustamentoMSG, org.openspcoop2.pdd.mdb.Sbustamento.ID_MODULO, msgDiag, 
							identitaPdD,this.msgContext.getIdModulo(), idMessageRequest, msgRequest);
					logCore.debug(RicezioneBuste.ID_MODULO + " :send a sbustamento eseguita");
				}
			}

		} catch (Exception e) {	
			if(functionAsRouter){
				logCore.error("Spedizione->InoltroBuste(router) non riuscita",e);
				msgDiag.logErroreGenerico(e,"GenericLib.nodeSender.send(InoltroBuste)");
			}else{
				logCore.error("Spedizione->Sbustamento non riuscita",e);
				msgDiag.logErroreGenerico(e,"GenericLib.nodeSender.send(Sbustamento)");
			}
			if(this.msgContext.isGestioneRisposta()){
				
				parametriGenerazioneBustaErrore.setBusta(bustaRichiesta);
				parametriGenerazioneBustaErrore.setErroreIntegrazione(ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
						get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_512_SEND));
				
				OpenSPCoop2Message errorOpenSPCoopMsg = generaBustaErroreProcessamento(parametriGenerazioneBustaErrore,e);
				
				// Nota: la bustaRichiesta e' stata trasformata da generaErroreProcessamento
				parametriInvioBustaErrore.setOpenspcoopMsg(errorOpenSPCoopMsg);
				parametriInvioBustaErrore.setBusta(parametriGenerazioneBustaErrore.getBusta());
				sendRispostaBustaErrore(parametriInvioBustaErrore);
			}
			msgRequest.deleteMessageFromFileSystem(); // elimino richiesta salvata su fileSystem
			openspcoopstate.releaseResource();
			return;
		}






		/* ------------  Commit connessione al DB (RichiestaSalvata) ------------- */
		msgDiag.mediumDebug("Commit delle operazioni per la gestione della richiesta...");
		try{
			openspcoopstate.commit();
			logCore.debug(RicezioneBuste.ID_MODULO+ " :RicezioneBuste commit eseguito");
		}catch (Exception e) {	
			msgDiag.logErroreGenerico(e,"openspcoopstate.commit()");
			if(this.msgContext.isGestioneRisposta()){
				
				parametriGenerazioneBustaErrore.setBusta(bustaRichiesta);
				parametriGenerazioneBustaErrore.setErroreIntegrazione(ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
						get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_506_COMMIT_JDBC));

				OpenSPCoop2Message errorOpenSPCoopMsg = generaBustaErroreProcessamento(parametriGenerazioneBustaErrore,e);
				
				// Nota: la bustaRichiesta e' stata trasformata da generaErroreProcessamento
				parametriInvioBustaErrore.setOpenspcoopMsg(errorOpenSPCoopMsg);
				parametriInvioBustaErrore.setBusta(parametriGenerazioneBustaErrore.getBusta());
				sendRispostaBustaErrore(parametriInvioBustaErrore);
			}
			msgRequest.deleteMessageFromFileSystem(); // elimino richiesta salvata 
			openspcoopstate.releaseResource();
			return;
		}

		// *** GB ***
		if(validatore!=null){
			if(validatore.getValidatoreSintattico()!=null){
				validatore.getValidatoreSintattico().setHeaderSOAP(null);
			}
			validatore.setValidatoreSintattico(null);
		}
		validatore = null;
		// *** GB ***
		
		if ( portaStateless==false && routingStateless== false  ) {

			// Aggiornamento cache messaggio
			if(msgRequest!=null)
				msgRequest.addMessaggiIntoCache_readFromTable(RicezioneBuste.ID_MODULO, "richiesta");

			// Aggiornamento cache proprietario messaggio
			if(msgRequest!=null)
				msgRequest.addProprietariIntoCache_readFromTable(RicezioneBuste.ID_MODULO, "richiesta",null,functionAsRouter);

			// Rilascia connessione al DB 
			msgDiag.mediumDebug("Commit delle operazioni per la gestione della richiesta effettuato, rilascio della connessione...");
			openspcoopstate.releaseResource();
		}




		
		
		
		/* FIX bug riferimentoMessaggio errato, contiene un id generato nel flusso di risposta (e poi non usato in seguito a errori) invece di quello della richiesta */
		bustaRichiesta = bustaRichiesta.clone(); // Per evitare che mi venga modificato da ImbustamentoRisposte (TODO: Non ho capito il motivo)
		
		
		
		
		


		/* ------------------------------ STATELESS  ROUTING -------------------------------- */
		if(routingStateless){
			((OpenSPCoopStateless)openspcoopstate).setMessageLib(inoltroMSG);
			((OpenSPCoopStateless)openspcoopstate).setIDMessaggioSessione(idMessageRequest);

			// Durante le invocazioni non deve essere utilizzata la connessione al database 
			((OpenSPCoopStateless)openspcoopstate).setUseConnection(false);

			// InoltroBuste 
			InoltroBuste lib = null;
			try{
				lib = new InoltroBuste(logCore);
				EsitoLib esito = lib.onMessage(openspcoopstate);

				if ( esito.getStatoInvocazione() == EsitoLib.OK ||
						esito.getStatoInvocazione() == EsitoLib.ERRORE_GESTITO ){
					msgDiag.mediumDebug("Invocazione libreria InoltroBuste riuscito con esito: "+esito.getStatoInvocazione());
				}
				else if (esito.getStatoInvocazione() == EsitoLib.ERRORE_NON_GESTITO ) {
					throw new Exception("Errore non gestito dalla libreria");
				}
				//else {
				//	throw new Exception("Esito libreria sconosciuto");
				//}
			}catch(Exception e){
				msgDiag.logErroreGenerico(e,"GestioneRoutingStateless");
				logCore.error("Errore Generale durante la gestione del routing stateless: "+e.getMessage(),e);

				parametriGenerazioneBustaErrore.setBusta(bustaRichiesta);
				parametriGenerazioneBustaErrore.setErroreIntegrazione(ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
						getErroreIntegrazione());

				OpenSPCoop2Message errorOpenSPCoopMsg = generaBustaErroreProcessamento(parametriGenerazioneBustaErrore,e);
				
				// Nota: la bustaRichiesta e' stata trasformata da generaErroreProcessamento
				((OpenSPCoopStateless)openspcoopstate).setUseConnection(true);
				parametriInvioBustaErrore.setOpenspcoopMsg(errorOpenSPCoopMsg);
				parametriInvioBustaErrore.setBusta(parametriGenerazioneBustaErrore.getBusta());
				sendRispostaBustaErrore(parametriInvioBustaErrore);
				openspcoopstate.releaseResource();
				return;
			}

			// ripristino utilizzo connessione al database
			((OpenSPCoopStateless)openspcoopstate).setUseConnection(true);
		}




		/* ------------------------------ STATELESS  NO-ROUTING -------------------------------- */
		/* Il ciclo seguente chiama tutte le librerie della gestione MDB di openspcoop, fino a quando non viene restituito un messaggio
		 * di libreria destinato a questo modulo oppure fino a che non si verifica un errore.
		 * Se la chiamata di libreria ci restituisce uno stato d'errore ed e' stata settata una risposta da inviare al mittente
		 * usciamo dal ciclo e gestiamo la risposta esattamente come facciamo se tutte le librerie sono andate a buon fine. Se ci 
		 * viene restituito un errore senza messaggio di risposta, ne creiamo uno noi (generico) e lo inviamo al mittente
		 */ 

		if (portaStateless) {
						
			((OpenSPCoopStateless)openspcoopstate).setMessageLib(sbustamentoMSG);
			((OpenSPCoopStateless)openspcoopstate).setIDMessaggioSessione(idMessageRequest);

			// Durante le invocazioni non deve essere utilizzata la connessione al database 
			((OpenSPCoopStateless)openspcoopstate).setUseConnection(false);

			EsitoLib esito = new EsitoLib();
			try {
				((OpenSPCoopStateless)openspcoopstate).setDestinatarioResponseMsgLib(""); // Verra aggiornato dalle librerie
				while (((OpenSPCoopStateless)openspcoopstate).getDestinatarioResponseMsgLib().startsWith(RicezioneBuste.ID_MODULO) == false) {

					boolean libreriaSbustamento = false;
					if (  ((OpenSPCoopStateless)openspcoopstate).getDestinatarioRequestMsgLib().startsWith(Sbustamento.ID_MODULO) ) {
						libreriaSbustamento = true;
					}
					
					if (  ((OpenSPCoopStateless)openspcoopstate).getDestinatarioResponseMsgLib().startsWith(ImbustamentoRisposte.ID_MODULO) ) {
						/* Verifico che non abbia rilasciato la connessione, se si la riprendo */
						if( propertiesReader.isRinegoziamentoConnessione(bustaRichiesta.getProfiloDiCollaborazione()) && (oneWayStateless || sincronoStateless || asincronoStateless)
								&& openspcoopstate.resourceReleased()){
							((OpenSPCoopStateless)openspcoopstate).setUseConnection(true);
							((OpenSPCoopStateless)openspcoopstate).initResource(identitaPdD, ImbustamentoRisposte.ID_MODULO, idTransazione);
							((OpenSPCoopStateless)openspcoopstate).setUseConnection(false);
							// update states
							registroServiziReader.updateState(openspcoopstate.getStatoRichiesta(),openspcoopstate.getStatoRisposta());
							configurazionePdDReader.updateState(openspcoopstate.getStatoRichiesta(),openspcoopstate.getStatoRisposta());
							tracciamento.updateState(openspcoopstate.getStatoRichiesta(),openspcoopstate.getStatoRisposta());
							msgDiag.updateState(openspcoopstate.getStatoRichiesta(),openspcoopstate.getStatoRisposta());
						}
					}

					esito = chiamaLibreria(((OpenSPCoopStateless)openspcoopstate), logCore);

					if ( oneWayVersione11 && newConnectionForResponse && 
							esito.getStatoInvocazione() == EsitoLib.OK &&
							libreriaSbustamento) {

						((OpenSPCoopStateless)openspcoopstate).setDestinatarioResponseMsgLib(RicezioneBuste.ID_MODULO);

						// Ho finito di gestire la richiesta in questo caso.
						((OpenSPCoopStateless)openspcoopstate).setUseConnection(true);
						openspcoopstate.commit();
						((OpenSPCoopStateless)openspcoopstate).setUseConnection(false);
					}

					if ( esito.getStatoInvocazione() == EsitoLib.ERRORE_GESTITO ) {

						if(oneWayVersione11 && newConnectionForResponse &&
								libreriaSbustamento ){

							// Ho finito di gestire la richiesta in questo caso.
							((OpenSPCoopStateless)openspcoopstate).setUseConnection(true);
							openspcoopstate.commit();
							((OpenSPCoopStateless)openspcoopstate).setUseConnection(false);
						}

						break;
					} 
					
					else if (esito.getStatoInvocazione() == EsitoLib.ERRORE_NON_GESTITO ) {
						throw new Exception("Errore non gestito dalla libreria");
					}
					
					if(libreriaSbustamento==true){
						// invocazione della libreriaSbustamento terminata, posso rilasciare la connessione se sono in stateless puro
						if( propertiesReader.isRinegoziamentoConnessione(bustaRichiesta.getProfiloDiCollaborazione()) && (oneWayStateless || sincronoStateless || asincronoStateless) ){
							((OpenSPCoopStateless)openspcoopstate).setUseConnection(true);
							openspcoopstate.commit();
							openspcoopstate.releaseResource();
							((OpenSPCoopStateless)openspcoopstate).setUseConnection(false);
						}
					}
					
					//else {
					//	throw new Exception("Esito libreria sconosciuto");
					//}
				}
			}catch (Exception e) {
				msgDiag.logErroreGenerico(e,"GestioneStateless");
				logCore.error("Errore Generale durante la gestione stateless: "+e.getMessage(),e);

				parametriGenerazioneBustaErrore.setBusta(bustaRichiesta);
				parametriGenerazioneBustaErrore.setErroreIntegrazione(ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
						getErroreIntegrazione());
				
				OpenSPCoop2Message errorOpenSPCoopMsg = generaBustaErroreProcessamento(parametriGenerazioneBustaErrore,e);
				
				// Nota: la bustaRichiesta e' stata trasformata da generaErroreProcessamento
				((OpenSPCoopStateless)openspcoopstate).setUseConnection(true);
				parametriInvioBustaErrore.setOpenspcoopMsg(errorOpenSPCoopMsg);
				parametriInvioBustaErrore.setBusta(parametriGenerazioneBustaErrore.getBusta());
				sendRispostaBustaErrore(parametriInvioBustaErrore);
				openspcoopstate.releaseResource();
				return;
			}

			// ripristino utilizzo connessione al database
			((OpenSPCoopStateless)openspcoopstate).setUseConnection(true);
		}




		// refresh risorse con nuovi stati
		configurazionePdDReader.updateState(openspcoopstate.getStatoRichiesta(),openspcoopstate.getStatoRisposta());
		registroServiziReader.updateState(openspcoopstate.getStatoRichiesta(),openspcoopstate.getStatoRisposta());
		tracciamento.updateState(openspcoopstate.getStatoRichiesta(),openspcoopstate.getStatoRisposta());
		msgDiag.updateState(openspcoopstate.getStatoRichiesta(),openspcoopstate.getStatoRisposta());

		
		

		/* ------------  GestioneRisposta non effettuata ------------- */
		msgDiag.mediumDebug("Gestione risposta...");
		if(this.msgContext.isGestioneRisposta()==false){
			if(portaStateless)
				openspcoopstate.releaseResource();
			return;
		}
		
		boolean richiestaRispostaProtocollo = true;
		/* -------Scenario Routing -------------
		 * Una busta e 'potenzialmente ricevibile da questo modulo se:
		 * - profilo sincrono
		 * - impostazione della newConnectionForResponse = false
		 * Se ricevo una busta non sincrona e newConnectionForResponse=true ho quindi terminato.
		 * 
		 * Una busta NON DEVE cmq esssre ricevuta.
		 * Es. puo' darsi che la PdD finale mandi il sincrono su di una nuova connessione.
		 */
		
		if(functionAsRouter){
			if( (org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.SINCRONO.equals(bustaRichiesta.getProfiloDiCollaborazione())==false) &&
					newConnectionForResponse ) {
				this.msgContext.setMessageResponse(MessageUtilities.buildEmptyMessage(requestMessage.getFactory(),requestMessage.getMessageType(),MessageRole.RESPONSE));
				return;
			}
			richiestaRispostaProtocollo = false;
		}
		/* -------Scenario Normale -------------
		 * Una busta e' potenzialmente ricevibile da questo modulo se:
		 * - indTelematicoAbilitato && indTelematicoMittente==null
		 * - indTelematicoNonAbilitato
		 * - profilo sincrono (e valgono i punti sopra degli indirizzi telematici)
		 * - profilo non sincrono && newConnectionForReponse = false
		 * Se ricevuo una busta che possiede un indTelMittente e la gestione e' abilitata ho quindi terminato.
		 * Se ricevo una busta non sincrona e newConnectionForResponse=true ho quindi terminato.
		 *  
		 * Una busta DEVE essere ricevuta quindi, superati i controlli sopra se:
		 * - profilo oneway && confermaRicezione  (poiche' se vado su newConnection o indTelematico sono gia' uscito)
		 * - profilo sincrono (poiche' se vi e' un indTelematico sono gia' uscito)
		 * Quindi una risposta dopo i controlli sopra e' sempre aspettata, a meno di eccezioni:
		 * Se ricevo una busta Errore 
		 * Se ricevo una busta di Servizio
		 * Se viene richiesta un profilo OneWay senza confermaRicezione
		 */
		else{
			if ( utilizzoIndirizzoTelematico && bustaRichiesta.getIndirizzoMittente()!=null &&
					moduleManager.isUtilizzoIndirizzoSoggettoPresenteBusta() &&
					(!oneWayStateless) && (!sincronoStateless) && (!asincronoStateless)  ){
				this.msgContext.setMessageResponse(MessageUtilities.buildEmptyMessage(requestMessage.getFactory(),requestMessage.getMessageType(),MessageRole.RESPONSE));
				if(portaStateless){
					openspcoopstate.releaseResource();
				}
				return;
			} else if( (org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.SINCRONO.equals(bustaRichiesta.getProfiloDiCollaborazione())==false) &&
					newConnectionForResponse &&
					(!oneWayStateless) && (!sincronoStateless) && (!asincronoStateless) ) {
				this.msgContext.setMessageResponse(MessageUtilities.buildEmptyMessage(requestMessage.getFactory(),requestMessage.getMessageType(),MessageRole.RESPONSE));
				if(portaStateless){
					openspcoopstate.releaseResource();
				}
				return;
			} else {
				if ( isMessaggioErroreProtocollo  ) {
					richiestaRispostaProtocollo = false;
				} else if ( bustaDiServizio  ) {
					richiestaRispostaProtocollo = false;
				} else if( StatoFunzionalitaProtocollo.DISABILITATA.equals(moduleManager.getConsegnaAffidabile(bustaRichiesta)) ||
						(propertiesReader.isGestioneRiscontri(implementazionePdDMittente)==false) ||
						(org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.ONEWAY.equals(bustaRichiesta.getProfiloDiCollaborazione()) &&
								(bustaRichiesta.isConfermaRicezione()==false))
				){
					richiestaRispostaProtocollo = false;
				}
				else if( org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.ASINCRONO_SIMMETRICO.equals(bustaRichiesta.getProfiloDiCollaborazione()) ||
						org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.ASINCRONO_ASIMMETRICO.equals(bustaRichiesta.getProfiloDiCollaborazione()) ){
					richiestaRispostaProtocollo = false;
				}
			}
		}









		GestoreMessaggi msgResponse = null;
		OpenSPCoop2Message responseMessage = MessageUtilities.buildEmptyMessage(requestMessage.getFactory(),requestMessage.getMessageType(),MessageRole.RESPONSE);
		//TempiAttraversamentoPDD tempiAttraversamentoGestioneMessaggi = null;
		//DimensioneMessaggiAttraversamentoPdD dimensioneMessaggiAttraversamentoGestioneMessaggi = null;

		/* ------------  Lettura parametri del messaggio ricevuto e ValidityCheck -------------- */
		Busta bustaRisposta = null;
		String idMessaggioSblocco = null;

		boolean contenutoRispostaPresente = false;

		try {

			RicezioneBusteMessage ricezioneBusteMSG = null;
			String idCorrelazioneApplicativaRisposta = null;
			try{
				msgDiag.mediumDebug("Attesa/lettura risposta...");

				if ( portaStateless==false && routingStateless== false  ) {
					
					String classType = null;
					INodeReceiver nodeReceiver = null;
					try{
						classType = className.getNodeReceiver(propertiesReader.getNodeReceiver());
						nodeReceiver = (INodeReceiver) loader.newInstance(classType);
						AbstractCore.init(nodeReceiver, pddContext, protocolFactory);
					}catch(Exception e){
						throw new Exception("Riscontrato errore durante il caricamento della classe ["+classType+
								"] da utilizzare per la ricezione dall'infrastruttura: "+e.getMessage());
					}
					
					ricezioneBusteMSG =	(RicezioneBusteMessage) nodeReceiver.receive(msgDiag,identitaPdD,this.msgContext.getIdModulo(),
							idMessageRequest,propertiesReader.getNodeReceiverTimeoutRicezioneBuste(),
							propertiesReader.getNodeReceiverCheckInterval());
					
					// aggiorno pddContext
					pddContext = ricezioneBusteMSG.getPddContext();
					if(pddContext!=null){
						Enumeration<String> enumPddContext = pddContext.keys();
						while (enumPddContext.hasMoreElements()) {
							String key = enumPddContext.nextElement();
							//System.out.println("AGGIORNO KEY BUSTE ["+key+"]");
							this.msgContext.getPddContext().addObject(key, pddContext.getObject(key));
						}
					}
				}
				else{
					ricezioneBusteMSG = (RicezioneBusteMessage) openspcoopstate.getMessageLib();
				}


				contenutoRispostaPresente = ricezioneBusteMSG.getBustaRisposta()!=null;
				if( richiestaRispostaProtocollo && (contenutoRispostaPresente==false)  ){
					throw new Exception("Risposta attesa e non ritornata: ErroreInterno");
				}
				// Leggo il contenuto
				if(contenutoRispostaPresente){
					bustaRisposta = ricezioneBusteMSG.getBustaRisposta();

					this.msgContext.getProtocol().setIdRisposta(bustaRisposta.getID());
					this.msgContext.getProtocol().setCollaborazione(bustaRisposta.getCollaborazione());

				}else{
					idMessaggioSblocco = ricezioneBusteMSG.getIdMessaggioSblocco();
				}

			} catch (Exception e) {
				msgDiag.logErroreGenerico(e,"GestioneRisposta("+this.msgContext.getIdModulo()+")");
				logCore.error("Gestione risposta ("+this.msgContext.getIdModulo()+") con errore",e);
				
				// per la costruzione dell'errore ho bisogno di una connessione al database
				if ( portaStateless==false && routingStateless== false  ) {
					try{
						msgDiag.mediumDebug("Richiesta connessione al database per la gestione della risposta...");
						openspcoopstate.updateResource(idTransazione);
					}catch(Exception eDB){
						setSOAPFault_processamento(IntegrationFunctionError.INTERNAL_RESPONSE_ERROR,logCore,msgDiag,
								ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
								get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_505_GET_DB_CONNECTION),eDB,
								"openspcoopstate.updateDatabaseResource");
						return;
					}
				}

				// In caso di Timeout elimino messaggi di richiesta ancora in processamento.
				if(e instanceof NodeTimeoutException) {
					try{
						msgDiag.logPersonalizzato("timeoutRicezioneRisposta");
						msgRequest.aggiornaProprietarioMessaggio(TimerGestoreMessaggi.ID_MODULO);
						openspcoopstate.commit();
					}catch(Exception eDel){
						msgDiag.logErroreGenerico(eDel.getMessage(),"EliminazioneMessaggioScadutoTimeoutRicezioneRisposta");
					}
				}

				// Spedisco errore
				parametriGenerazioneBustaErrore.setBusta(bustaRichiesta);
				parametriGenerazioneBustaErrore.setErroreIntegrazione(ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
						get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_513_RECEIVE));

				OpenSPCoop2Message errorOpenSPCoopMsg = generaBustaErroreProcessamento(parametriGenerazioneBustaErrore,e);
				
				// Nota: la bustaRichiesta e' stata trasformata da generaErroreProcessamento
				parametriInvioBustaErrore.setOpenspcoopMsg(errorOpenSPCoopMsg);
				parametriInvioBustaErrore.setBusta(parametriGenerazioneBustaErrore.getBusta());
				sendRispostaBustaErrore(parametriInvioBustaErrore);

				// rilascio connessione e ritorno messaggio
				openspcoopstate.releaseResource();
				return;
			}



			//	Aggiornamento Informazioni
			if(contenutoRispostaPresente){
				msgDiag.setIdMessaggioRisposta(bustaRisposta.getID());
				msgDiag.addKeywords(bustaRisposta, false);
			}else{
				msgDiag.setIdMessaggioRisposta(idMessaggioSblocco);
			}
			parametriGenerazioneBustaErrore.setMsgDiag(msgDiag);
			parametriInvioBustaErrore.setMsgDiag(msgDiag);
			
			if ( portaStateless==false && routingStateless== false  ) {

				/* ------------  Re-ottengo Connessione al DB -------------- */
				try{
					msgDiag.mediumDebug("Richiesta connessione al database per la gestione della risposta...");
					openspcoopstate.updateResource(idTransazione);
				}catch(Exception e){
					setSOAPFault_processamento(IntegrationFunctionError.INTERNAL_RESPONSE_ERROR,logCore,msgDiag,
							ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
							get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_505_GET_DB_CONNECTION),e,
							"openspcoopstate.updateDatabaseResource");
					return;
				}

				// MsgResponse
				if(contenutoRispostaPresente){
					msgResponse = new GestoreMessaggi(openspcoopstate, false, bustaRisposta.getID(),Costanti.OUTBOX,msgDiag,inRequestContext.getPddContext());
				}else{
					msgResponse = new GestoreMessaggi(openspcoopstate, false,idMessaggioSblocco,Costanti.OUTBOX,msgDiag,inRequestContext.getPddContext());
				}
				//tempiAttraversamentoGestioneMessaggi = msgResponse.getTempiAttraversamentoPdD();
				//dimensioneMessaggiAttraversamentoGestioneMessaggi = msgResponse.getDimensioneMessaggiAttraversamentoPdD();


				/* ------------  Lettura Contenuto Messaggio (mapping in Message)  -------------- */
				if(contenutoRispostaPresente || functionAsRouter){
					msgDiag.mediumDebug("Lettura messaggio di risposta...");
					try{
						responseMessage = msgResponse.getMessage();
						idCorrelazioneApplicativaRisposta = msgResponse.getIDCorrelazioneApplicativaRisposta();
					}catch(Exception e){
						// Il router potrebbe ricevere il SOAPFault da reinoltrare
						if( (functionAsRouter==false) || (contenutoRispostaPresente==true) ){
							msgDiag.logErroreGenerico(e,"msgResponse.getMessage()");
							
							parametriGenerazioneBustaErrore.setBusta(bustaRichiesta);
							parametriGenerazioneBustaErrore.setErroreIntegrazione(ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
									get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_511_READ_RESPONSE_MSG));

							OpenSPCoop2Message errorOpenSPCoopMsg = generaBustaErroreProcessamento(parametriGenerazioneBustaErrore,e);
							
							// Nota: la bustaRichiesta e' stata trasformata da generaErroreProcessamento
							parametriInvioBustaErrore.setOpenspcoopMsg(errorOpenSPCoopMsg);
							parametriInvioBustaErrore.setBusta(parametriGenerazioneBustaErrore.getBusta());
							sendRispostaBustaErrore(parametriInvioBustaErrore);
							openspcoopstate.releaseResource();
							return;
						}
					}
				}
			}

			else {
				responseMessage = ((OpenSPCoopStateless)openspcoopstate).getRispostaMsg();
				idCorrelazioneApplicativaRisposta = ((OpenSPCoopStateless)openspcoopstate).getIDCorrelazioneApplicativaRisposta();
				if(responseMessage!=null){
					parametriGenerazioneBustaErrore.setParseException(responseMessage.getParseException());
				}
				/*tempiAttraversamentoGestioneMessaggi = 
					((OpenSPCoopStateless) openspcoopstate).getTempiAttraversamentoPDD();
				dimensioneMessaggiAttraversamentoGestioneMessaggi = 
					((OpenSPCoopStateless) openspcoopstate).getDimensioneMessaggiAttraversamentoPDD();*/
			}

			/* ---- Aggiorno informazioni correlazione applicativa risposta ---- */
			parametriGenerazioneBustaErrore.setCorrelazioneApplicativaRisposta(idCorrelazioneApplicativaRisposta);
			parametriInvioBustaErrore.setCorrelazioneApplicativaRisposta(idCorrelazioneApplicativaRisposta);
			msgDiag.setIdCorrelazioneRisposta(idCorrelazioneApplicativaRisposta);
			if(this.msgContext.getIntegrazione()!=null)
				this.msgContext.getIntegrazione().setIdCorrelazioneApplicativaRisposta(idCorrelazioneApplicativaRisposta);

			// Gestione Busta di Risposta
			if(contenutoRispostaPresente){

				/* ----------- Trasmissione ------------------ */
				Trasmissione tras = null;
				if( propertiesReader.isGenerazioneListaTrasmissioni(implementazionePdDMittente)){
					msgDiag.mediumDebug("Impostazione trasmissione nella busta di risposta...");
					// Tracciamento in busta
					tras = new Trasmissione();
					
					// origine
					tras.setOrigine(identitaPdD.getNome());
					tras.setTipoOrigine(identitaPdD.getTipo());
					tras.setIdentificativoPortaOrigine(identitaPdD.getCodicePorta());
					
					// trasmissione
					// Cerco destinatario con identita che sto assumendo (l'origine di quella trasmissione e' la destinazione di questa!)
					// che come mittente non possieda il mittente attuale della busta (senno potrebbe essere il potenziale
					// precedente hop che ha aggiunto una trasmissione da lui a questo hop)
					for(int i=0;i<bustaRichiesta.sizeListaTrasmissioni();i++){
						if( identitaPdD.getTipo().equals(bustaRichiesta.getTrasmissione(i).getTipoDestinazione()) &&
								identitaPdD.getNome().equals(bustaRichiesta.getTrasmissione(i).getDestinazione()) ){
							boolean tipoOrigineValido = true;
							try {
								traduttore.toProtocolOrganizationType(bustaRichiesta.getTrasmissione(i).getTipoOrigine());
							}catch(Exception e) {
								tipoOrigineValido = false;
							}
							if(tipoOrigineValido) {
								tras.setDestinazione(bustaRichiesta.getTrasmissione(i).getOrigine());
								tras.setTipoDestinazione(bustaRichiesta.getTrasmissione(i).getTipoOrigine());
							}
						}
					}
					if(tras.getDestinazione()==null || tras.getTipoDestinazione()==null){
						tras.setDestinazione(bustaRisposta.getDestinatario());
						tras.setTipoDestinazione(bustaRisposta.getTipoDestinatario());
					}
					try{
						String dominio = registroServiziReader.getDominio(new IDSoggetto(tras.getTipoDestinazione(),tras.getDestinazione()), null, protocolFactory);
						tras.setIdentificativoPortaDestinazione(dominio);
					}catch(Exception e){}
					
					// oraRegistrazione
					tras.setOraRegistrazione(bustaRisposta.getOraRegistrazione());
					tras.setTempo(propertiesReader.getTipoTempoBusta(implementazionePdDMittente));
					
					bustaRisposta.addTrasmissione(tras);
				}



				/* ------------  Gestione Funzionalita' speciali per Attachments (Manifest) ------------- */	
				boolean scartaBody = false;
				if(functionAsRouter==false){
					boolean allegaBody = configurazionePdDReader.isAllegaBody(pa);
					if(allegaBody){
						// E' stato effettuato prima l'inserimento del body come allegato.
						// Forzo lo scartamento.
						scartaBody = true;
					}else{
						scartaBody = configurazionePdDReader.isScartaBody(pa);
					}
				}








				/* ------------  Aggiunta eccezioni di livello info riscontrate dalla validazione, se profilo e' lineeGuida1.1 ------------- */	
				if(functionAsRouter==false){
					if( bustaRisposta.sizeListaEccezioni()==0 && moduleManager.isIgnoraEccezioniLivelloNonGrave()){
						for(int i=0; i<erroriValidazione.size();i++){
							Eccezione ec = erroriValidazione.get(i);
							if(LivelloRilevanza.INFO.equals(ec.getRilevanza())){
								bustaRisposta.addEccezione(ec);
							}
						}
					}
				}








				/* ------------  Imbustamento (Prima della Sicurezza)  ------------- */	
				
				msgDiag.mediumDebug("Imbustamento della risposta...");
				BustaRawContent<?> headerBustaRisposta = null;
				boolean gestioneManifestRisposta = false;
				Imbustamento imbustatore = null;
				try{
					if(functionAsRouter){
						gestioneManifestRisposta = configurazionePdDReader.isGestioneManifestAttachments();
					}else{
						if( org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.ASINCRONO_SIMMETRICO.equals(bustaRichiesta.getProfiloDiCollaborazione()) &&
								RuoloBusta.RISPOSTA.equals(ruoloBustaRicevuta.toString()) &&
								pd!=null){ // devo generare la ricevuta alla risposta
							gestioneManifestRisposta = configurazionePdDReader.isGestioneManifestAttachments(pd,protocolFactory);
						}else{
							gestioneManifestRisposta = configurazionePdDReader.isGestioneManifestAttachments(pa,protocolFactory);
						}
					}
					imbustatore = new Imbustamento(logCore, protocolFactory,openspcoopstate.getStatoRichiesta());
					if(functionAsRouter && 
							!( identitaPdD.getTipo().equals(bustaRisposta.getTipoMittente()) && identitaPdD.getNome().equals(bustaRisposta.getMittente()) ) 
					){
						// Aggiungo trasmissione solo se la busta e' stata generata dalla porta di dominio destinataria della richiesta.
						// Se il mittente e' il router, logicamente la busta sara' un errore generato dal router
						if( propertiesReader.isGenerazioneListaTrasmissioni(implementazionePdDMittente)){
							msgDiag.highDebug("Tipo Messaggio Risposta prima dell'imbustamento ["+responseMessage.getClass().getName()+"]");
							ProtocolMessage protocolMessage = imbustatore.addTrasmissione(responseMessage, tras, readQualifiedAttribute, 
									FaseImbustamento.PRIMA_SICUREZZA_MESSAGGIO);
							if(protocolMessage!=null && !protocolMessage.isPhaseUnsupported()) {
								headerBustaRisposta = protocolMessage.getBustaRawContent();
								responseMessage = protocolMessage.getMessage(); // updated
							}
							msgDiag.highDebug("Tipo Messaggio Risposta dopo l'imbustamento ["+responseMessage.getClass().getName()+"]");
						}
						else{
							Validatore v = new Validatore(responseMessage,pddContext,openspcoopstate.getStatoRichiesta(), logCore, protocolFactory);
							headerBustaRisposta = v.getHeaderProtocollo_senzaControlli();
						}
					}else{
						msgDiag.highDebug("Tipo Messaggio Risposta prima dell'imbustamento ["+responseMessage.getClass().getName()+"]");
						ProtocolMessage protocolMessage = imbustatore.imbustamentoRisposta(responseMessage,pddContext,
								bustaRisposta,bustaRichiesta,
								infoIntegrazione,gestioneManifestRisposta,scartaBody,proprietaManifestAttachments,
								FaseImbustamento.PRIMA_SICUREZZA_MESSAGGIO);
						if(protocolMessage!=null && !protocolMessage.isPhaseUnsupported()) {
							headerBustaRisposta = protocolMessage.getBustaRawContent();
							responseMessage = protocolMessage.getMessage(); // updated
						}
						msgDiag.highDebug("Tipo Messaggio Risposta dopo l'imbustamento ["+responseMessage.getClass().getName()+"]");
					}
				}catch(Exception e){
					if(functionAsRouter && 
							!( identitaPdD.getTipo().equals(bustaRisposta.getTipoMittente()) && identitaPdD.getNome().equals(bustaRisposta.getMittente()) ) 
					){
						msgDiag.logErroreGenerico(e,"imbustatore.pre-security.addTrasmissione(risposta)");
					}else{
						msgDiag.logErroreGenerico(e,"imbustatore.pre-security-imbustamento(risposta)");
					}
	
					parametriGenerazioneBustaErrore.setBusta(bustaRichiesta);
					parametriGenerazioneBustaErrore.setErroreIntegrazione(ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
							get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_526_GESTIONE_IMBUSTAMENTO));
					parametriGenerazioneBustaErrore.setIntegrationFunctionError(IntegrationFunctionError.INTEROPERABILITY_PROFILE_ENVELOPING_RESPONSE_FAILED);
					
					OpenSPCoop2Message errorOpenSPCoopMsg = generaBustaErroreProcessamento(parametriGenerazioneBustaErrore,e);
					
					// Nota: la bustaRichiesta e' stata trasformata da generaErroreProcessamento
					parametriInvioBustaErrore.setOpenspcoopMsg(errorOpenSPCoopMsg);
					parametriInvioBustaErrore.setBusta(parametriGenerazioneBustaErrore.getBusta());
					sendRispostaBustaErrore(parametriInvioBustaErrore);
					openspcoopstate.releaseResource();
					return;
				}




				
				
				
				
				/* ------------ Init MTOM Processor  -------------- */
				MTOMProcessor mtomProcessor = null;
				if(functionAsRouter==false){
					if(flowPropertiesResponse!=null){
						msgDiag.mediumDebug("init MTOM Processor ...");
						mtomProcessor = new MTOMProcessor(flowPropertiesResponse.mtom, flowPropertiesResponse.messageSecurity, 
								tipoPorta, msgDiag, logCore, pddContext);
					}
				}
				
				
				
				
				
				/* ------------ MTOM Processor BeforeSecurity  -------------- */
				if(mtomProcessor!=null){
					try{
						mtomProcessor.mtomBeforeSecurity(responseMessage, RuoloMessaggio.RISPOSTA);
					}catch(Exception e){
						// L'errore viene registrato dentro il metodo mtomProcessor.mtomBeforeSecurity
						//msgDiag.logErroreGenerico(e,"MTOMProcessor(BeforeSec-"+mtomProcessor.getMTOMProcessorType()+")");
						
						parametriGenerazioneBustaErrore.setBusta(bustaRichiesta);
						parametriGenerazioneBustaErrore.setErroreIntegrazione(ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
								get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_557_MTOM_PROCESSOR_ERROR));
						parametriGenerazioneBustaErrore.setIntegrationFunctionError(IntegrationFunctionError.ATTACHMENTS_PROCESSING_RESPONSE_FAILED);
						
						OpenSPCoop2Message errorOpenSPCoopMsg = generaBustaErroreProcessamento(parametriGenerazioneBustaErrore,e);
						
						// Nota: la bustaRichiesta e' stata trasformata da generaErroreProcessamento
						parametriInvioBustaErrore.setOpenspcoopMsg(errorOpenSPCoopMsg);
						parametriInvioBustaErrore.setBusta(parametriGenerazioneBustaErrore.getBusta());
						sendRispostaBustaErrore(parametriInvioBustaErrore);
						openspcoopstate.releaseResource();
						return;
					}
				}




				/* ------------ Message-Security -------------- */
				if(functionAsRouter==false){
					if(flowPropertiesResponse != null && flowPropertiesResponse.messageSecurity!=null && 
						flowPropertiesResponse.messageSecurity.getFlowParameters() !=null &&
						flowPropertiesResponse.messageSecurity.getFlowParameters().size() > 0){
						try{
							messageSecurityContext.setFunctionAsClient(SecurityConstants.SECURITY_CLIENT);
							messageSecurityContext.setOutgoingProperties(flowPropertiesResponse.messageSecurity.getFlowParameters());
							
							String tipoSicurezza = SecurityConstants.convertActionToString(messageSecurityContext.getOutgoingProperties());
							msgDiag.addKeyword(CostantiPdD.KEY_TIPO_SICUREZZA_MESSAGGIO_RISPOSTA, tipoSicurezza);
							pddContext.addObject(CostantiPdD.KEY_TIPO_SICUREZZA_MESSAGGIO_RISPOSTA, tipoSicurezza);
							
							msgDiag.logPersonalizzato("messageSecurity.processamentoRispostaInCorso");
							
							if(org.openspcoop2.security.message.engine.WSSUtilities.isNormalizeToSaajImpl(messageSecurityContext)){
								msgDiag.mediumDebug("Normalize to saajImpl");
								//System.out.println("RicezioneBuste.response.normalize");
								responseMessage = responseMessage.normalizeToSaajImpl();
							}
							
							if(messageSecurityContext.processOutgoing(responseMessage,pddContext.getContext(),
									transaction!=null ? transaction.getTempiElaborazione() : null) == false){
								
								msgDiag.addKeyword(CostantiPdD.KEY_ERRORE_PROCESSAMENTO , messageSecurityContext.getMsgErrore() );
								msgDiag.logPersonalizzato("messageSecurity.processamentoRispostaInErrore");
								
								parametriGenerazioneBustaErrore.setBusta(bustaRichiesta);
								parametriGenerazioneBustaErrore.setErroreCooperazione(ErroriCooperazione.MESSAGE_SECURITY.
										getErroreMessageSecurity(messageSecurityContext.getMsgErrore(), messageSecurityContext.getCodiceErrore()));
								parametriGenerazioneBustaErrore.setIntegrationFunctionError(IntegrationFunctionError.MESSAGE_SECURITY_RESPONSE_FAILED);
								
								OpenSPCoop2Message errorOpenSPCoopMsg = generaBustaErroreValidazione(parametriGenerazioneBustaErrore);
								
								// Nota: la bustaRichiesta e' stata trasformata da generaErroreProcessamento
								parametriInvioBustaErrore.setOpenspcoopMsg(errorOpenSPCoopMsg);
								parametriInvioBustaErrore.setBusta(parametriGenerazioneBustaErrore.getBusta());
								sendRispostaBustaErrore(parametriInvioBustaErrore);
								openspcoopstate.releaseResource();
								return;
							}
							else{
								msgDiag.logPersonalizzato("messageSecurity.processamentoRispostaEffettuato");
							}
							
						}catch(Exception e){
							msgDiag.addKeywordErroreProcessamento(e);
							msgDiag.logPersonalizzato("messageSecurity.processamentoRispostaInErrore");
							logCore.error("[MessageSecurityResponse]" + e.getMessage(),e);
							
							parametriGenerazioneBustaErrore.setBusta(bustaRichiesta);
							parametriGenerazioneBustaErrore.setErroreIntegrazione(ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
									get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_536_CONFIGURAZIONE_NON_DISPONIBILE));

							OpenSPCoop2Message errorOpenSPCoopMsg = generaBustaErroreProcessamento(parametriGenerazioneBustaErrore,e);
							
							// Nota: la bustaRichiesta e' stata trasformata da generaErroreProcessamento
							parametriInvioBustaErrore.setOpenspcoopMsg(errorOpenSPCoopMsg);
							parametriInvioBustaErrore.setBusta(parametriGenerazioneBustaErrore.getBusta());
							sendRispostaBustaErrore(parametriInvioBustaErrore);
							openspcoopstate.releaseResource();
							return;
						}
					}
					else{
						msgDiag.logPersonalizzato("messageSecurity.processamentoRispostaDisabilitato");
					}
				}

				
				
				
				/* ------------ MTOM Processor AfterSecurity  -------------- */
				if(mtomProcessor!=null){
					try{
						mtomProcessor.mtomAfterSecurity(responseMessage, RuoloMessaggio.RISPOSTA);
					}catch(Exception e){
						// L'errore viene registrato dentro il metodo mtomProcessor.mtomAfterSecurity
						//msgDiag.logErroreGenerico(e,"MTOMProcessor(AfterSec-"+mtomProcessor.getMTOMProcessorType()+")");
						
						parametriGenerazioneBustaErrore.setBusta(bustaRichiesta);
						parametriGenerazioneBustaErrore.setErroreIntegrazione(ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
								get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_557_MTOM_PROCESSOR_ERROR));
						parametriGenerazioneBustaErrore.setIntegrationFunctionError(IntegrationFunctionError.ATTACHMENTS_PROCESSING_RESPONSE_FAILED);
						
						OpenSPCoop2Message errorOpenSPCoopMsg = generaBustaErroreProcessamento(parametriGenerazioneBustaErrore,e);
						
						// Nota: la bustaRichiesta e' stata trasformata da generaErroreProcessamento
						parametriInvioBustaErrore.setOpenspcoopMsg(errorOpenSPCoopMsg);
						parametriInvioBustaErrore.setBusta(parametriGenerazioneBustaErrore.getBusta());
						sendRispostaBustaErrore(parametriInvioBustaErrore);
						openspcoopstate.releaseResource();
						return;
					}
				}
				
				
				
				
				
				/* ------------  Imbustamento (Dopo della Sicurezza)  ------------- */	
				
				msgDiag.mediumDebug("Imbustamento della risposta dopo la sicurezza...");
				try{
					if(functionAsRouter && 
							!( identitaPdD.getTipo().equals(bustaRisposta.getTipoMittente()) && identitaPdD.getNome().equals(bustaRisposta.getMittente()) ) 
					){
						// Aggiungo trasmissione solo se la busta e' stata generata dalla porta di dominio destinataria della richiesta.
						// Se il mittente e' il router, logicamente la busta sara' un errore generato dal router
						if( propertiesReader.isGenerazioneListaTrasmissioni(implementazionePdDMittente)){
							msgDiag.highDebug("Tipo Messaggio Risposta prima dell'imbustamento (after-security) ["+responseMessage.getClass().getName()+"]");
							ProtocolMessage protocolMessage = imbustatore.addTrasmissione(responseMessage, tras, readQualifiedAttribute, 
									FaseImbustamento.DOPO_SICUREZZA_MESSAGGIO);
							if(protocolMessage!=null && !protocolMessage.isPhaseUnsupported()) {
								headerBustaRisposta = protocolMessage.getBustaRawContent();
								responseMessage = protocolMessage.getMessage(); // updated
							}
							msgDiag.highDebug("Tipo Messaggio Risposta dopo l'imbustamento (after-security) ["+responseMessage.getClass().getName()+"]");
						}
						// else gia' effettuato nella precedente fase pre-sicurezza
					}else{
						msgDiag.highDebug("Tipo Messaggio Risposta prima dell'imbustamento (after-security) ["+responseMessage.getClass().getName()+"]");
						ProtocolMessage protocolMessage = imbustatore.imbustamentoRisposta(responseMessage,pddContext,
								bustaRisposta,bustaRichiesta,
								infoIntegrazione,gestioneManifestRisposta,scartaBody,proprietaManifestAttachments,
								FaseImbustamento.DOPO_SICUREZZA_MESSAGGIO);
						if(protocolMessage!=null && !protocolMessage.isPhaseUnsupported()) {
							headerBustaRisposta = protocolMessage.getBustaRawContent();
							responseMessage = protocolMessage.getMessage(); // updated
						}
						msgDiag.highDebug("Tipo Messaggio Risposta dopo l'imbustamento (after-security) ["+responseMessage.getClass().getName()+"]");
					}
				}catch(Exception e){
					if(functionAsRouter && 
							!( identitaPdD.getTipo().equals(bustaRisposta.getTipoMittente()) && identitaPdD.getNome().equals(bustaRisposta.getMittente()) ) 
					){
						msgDiag.logErroreGenerico(e,"imbustatore.after-security.addTrasmissione(risposta)");
					}else{
						msgDiag.logErroreGenerico(e,"imbustatore.after-security-imbustamento(risposta)");
					}
	
					OpenSPCoop2Message errorOpenSPCoopMsg = null;
					
					parametriGenerazioneBustaErrore.setBusta(bustaRichiesta);
					parametriGenerazioneBustaErrore.setIntegrationFunctionError(IntegrationFunctionError.INTEROPERABILITY_PROFILE_ENVELOPING_RESPONSE_FAILED);
					if(e!=null && e instanceof ProtocolException && ((ProtocolException)e).isInteroperabilityError() ) {
						parametriGenerazioneBustaErrore.setErroreCooperazione(ErroriCooperazione.ERRORE_GENERICO_PROTOCOLLO_NON_CORRETTO.
								getErroreCooperazione(e.getMessage()));
						errorOpenSPCoopMsg = generaBustaErroreValidazione(parametriGenerazioneBustaErrore);
					}
					else {
						parametriGenerazioneBustaErrore.setErroreIntegrazione(ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
							get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_526_GESTIONE_IMBUSTAMENTO));
						errorOpenSPCoopMsg = generaBustaErroreProcessamento(parametriGenerazioneBustaErrore,e);
					}

					// Nota: la bustaRichiesta e' stata trasformata da generaErroreProcessamento
					parametriInvioBustaErrore.setOpenspcoopMsg(errorOpenSPCoopMsg);
					parametriInvioBustaErrore.setBusta(parametriGenerazioneBustaErrore.getBusta());
					sendRispostaBustaErrore(parametriInvioBustaErrore);
					openspcoopstate.releaseResource();
					return;
				}
				
				
				

				/* ---------- Tracciamento Busta Ricevuta ------------- */
				msgDiag.mediumDebug("Tracciamento busta di risposta...");
				if(this.msgContext.isTracciamentoAbilitato()){
					EsitoElaborazioneMessaggioTracciato esitoTraccia = 
							EsitoElaborazioneMessaggioTracciato.getEsitoElaborazioneMessaggioInviato();
					SecurityInfo securityInfoResponse  = null;
					if(functionAsRouter==false){
						if(messageSecurityContext!=null && messageSecurityContext.getDigestReader(responseMessage.getFactory())!=null){
							IValidazioneSemantica validazioneSemantica = protocolFactory.createValidazioneSemantica(openspcoopstate.getStatoRichiesta());
							securityInfoResponse = validazioneSemantica.readSecurityInformation(messageSecurityContext.getDigestReader(responseMessage.getFactory()),responseMessage);
						}
					}
					tracciamento.registraRisposta(responseMessage,securityInfoResponse,headerBustaRisposta,bustaRisposta,esitoTraccia,
							Tracciamento.createLocationString(false,this.msgContext.getSourceLocation()),
							correlazioneApplicativa,idCorrelazioneApplicativaRisposta);
				}
				IValidatoreErrori validatoreErrori = protocolFactory.createValidatoreErrori(openspcoopstate.getStatoRichiesta());
				IProtocolManager protocolManager = protocolFactory.createProtocolManager();
				ProprietaValidazioneErrori pValidazioneErrori = new ProprietaValidazioneErrori();
				pValidazioneErrori.setIgnoraEccezioniNonGravi(protocolManager.isIgnoraEccezioniNonGravi());
				pValidazioneErrori.setVersioneProtocollo(versioneProtocollo);
				if( validatoreErrori.isBustaErrore(bustaRisposta,responseMessage,pValidazioneErrori) ) {
					if(mittenteAnonimo){
						msgDiag.logPersonalizzato("generazioneMessaggioErroreRisposta.mittenteAnonimo");
					}
					else{
						msgDiag.logPersonalizzato("generazioneMessaggioErroreRisposta");
					}
				}else{
					if(mittenteAnonimo){
						msgDiag.logPersonalizzato("generazioneMessaggioRisposta.mittenteAnonimo");
					}
					else{
						msgDiag.logPersonalizzato("generazioneMessaggioRisposta");
					}
				}
					

				/* --------	Elimino accesso daPdD --------- */
				msgDiag.mediumDebug("Eliminazione accesso da PdD...");
				repositoryBuste.eliminaUtilizzoPdDFromOutBox(bustaRisposta.getID());

			}

			
			
			
			
			
			
			
			/* ----- Header Integrazione ------ */
			if(pa!=null){
				msgDiag.mediumDebug("Gestione header di integrazione messaggio di risposta...");
				HeaderIntegrazione headerIntegrazioneRisposta = new HeaderIntegrazione(idTransazione);
				headerIntegrazioneRisposta.setBusta(new HeaderIntegrazioneBusta());
				headerIntegrazioneRisposta.getBusta().setTipoMittente(bustaRichiesta.getTipoMittente());
				headerIntegrazioneRisposta.getBusta().setMittente(bustaRichiesta.getMittente());
				headerIntegrazioneRisposta.getBusta().setTipoDestinatario(bustaRichiesta.getTipoDestinatario());
				headerIntegrazioneRisposta.getBusta().setDestinatario(bustaRichiesta.getDestinatario());
				headerIntegrazioneRisposta.getBusta().setTipoServizio(bustaRichiesta.getTipoServizio());
				headerIntegrazioneRisposta.getBusta().setServizio(bustaRichiesta.getServizio());
				headerIntegrazioneRisposta.getBusta().setVersioneServizio(bustaRichiesta.getVersioneServizio());
				headerIntegrazioneRisposta.getBusta().setAzione(bustaRichiesta.getAzione());
				if(bustaRichiesta.getCollaborazione()!=null) {
					headerIntegrazioneRisposta.getBusta().setIdCollaborazione(bustaRichiesta.getCollaborazione());
				}
				else if(bustaRisposta!=null && bustaRisposta.getCollaborazione()!=null) {
					headerIntegrazioneRisposta.getBusta().setIdCollaborazione(bustaRisposta.getCollaborazione());
				}
				headerIntegrazioneRisposta.getBusta().setID(bustaRichiesta.getID());
				headerIntegrazioneRisposta.getBusta().setProfiloDiCollaborazione(bustaRichiesta.getProfiloDiCollaborazione());
				headerIntegrazioneRisposta.setIdApplicativo(correlazioneApplicativa);
					
				String[] tipiIntegrazionePA_response = null;
				msgDiag.mediumDebug("Header integrazione...");
				if(functionAsRouter ){
					msgDiag.highDebug("Header integrazione (Default gestori integrazione Router)");
					if(RicezioneBuste.defaultPerProtocolloGestoreIntegrazionePA.containsKey(protocolFactory.getProtocol()))
						tipiIntegrazionePA_response = RicezioneBuste.defaultPerProtocolloGestoreIntegrazionePA.get(protocolFactory.getProtocol());
					else
						tipiIntegrazionePA_response = RicezioneBuste.defaultGestoriIntegrazionePA;
				}else{
					msgDiag.highDebug("Header integrazione (Gestori integrazione...)");
					if(pa!=null && this.msgContext.isTracciamentoAbilitato()){
						msgDiag.mediumDebug("Lettura header di integrazione...");
						try {
							tipiIntegrazionePA_response = configurazionePdDReader.getTipiIntegrazione(pa);
						} catch (Exception e) {
							msgDiag.logErroreGenerico(e,"configurazionePdDReader.getTipiIntegrazione(pa)");
							
							parametriGenerazioneBustaErrore.setBusta(bustaRichiesta);
							parametriGenerazioneBustaErrore.setErroreIntegrazione(ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
									get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_536_CONFIGURAZIONE_NON_DISPONIBILE));
	
							OpenSPCoop2Message errorOpenSPCoopMsg = generaBustaErroreProcessamento(parametriGenerazioneBustaErrore,e);
							
							// Nota: la bustaRichiesta e' stata trasformata da generaErroreProcessamento
							parametriInvioBustaErrore.setOpenspcoopMsg(errorOpenSPCoopMsg);
							parametriInvioBustaErrore.setBusta(parametriGenerazioneBustaErrore.getBusta());
							sendRispostaBustaErrore(parametriInvioBustaErrore);
							openspcoopstate.releaseResource();
							return;
						}
						msgDiag.highDebug("Header integrazione (Gestori integrazione terminato)");
						if (tipiIntegrazionePA_response == null){
							if(RicezioneBuste.defaultPerProtocolloGestoreIntegrazionePA.containsKey(protocolFactory.getProtocol()))
								tipiIntegrazionePA_response = RicezioneBuste.defaultPerProtocolloGestoreIntegrazionePA.get(protocolFactory.getProtocol());
							else
								tipiIntegrazionePA_response = RicezioneBuste.defaultGestoriIntegrazionePA;
						}
					}
				}
				
				OutResponsePAMessage outResponsePAMessage = new OutResponsePAMessage();
				outResponsePAMessage.setBustaRichiesta(bustaRichiesta);
				outResponsePAMessage.setMessage(responseMessage);
				Map<String, String> propertiesIntegrazioneRisposta = new HashMap<String, String>();
				outResponsePAMessage.setProprietaTrasporto(propertiesIntegrazioneRisposta);
				outResponsePAMessage.setPortaDelegata(pd);
				outResponsePAMessage.setPortaApplicativa(pa);
				outResponsePAMessage.setSoggettoMittente(soggettoFruitore);
				outResponsePAMessage.setServizio(idServizio);
				
				for (int i = 0; i < tipiIntegrazionePA_response.length; i++) {
					try {
//						if(RicezioneBuste.gestoriIntegrazionePA.containsKey(tipiIntegrazionePA[i])==false){
//							RicezioneBuste.aggiornaListaGestoreIntegrazione(
//									tipiIntegrazionePA[i], className,
//									propertiesReader, logCore);
//						}
//						IGestoreIntegrazionePA gestore = RicezioneBuste.gestoriIntegrazionePA.get(tipiIntegrazionePA[i]);
						
						String classType = null;
						IGestoreIntegrazionePA gestore = null;
						try {
							classType = className.getIntegrazionePortaApplicativa(tipiIntegrazionePA_response[i]);
							gestore = (IGestoreIntegrazionePA)loader.newInstance(classType);
							AbstractCore.init(gestore, pddContext, protocolFactory);
						} catch (Exception e) {
							throw new Exception(
									"Riscontrato errore durante il caricamento della classe ["+ classType
									+ "] da utilizzare per la gestione dell'integrazione di tipo ["+ tipiIntegrazionePA_response[i] + "]: " + e.getMessage());
						}
						
						if (gestore != null) {
							if(gestore instanceof IGestoreIntegrazionePASoap){
								if(propertiesReader.deleteHeaderIntegrazioneResponsePA()){
									if(responseMessage==null){
										responseMessage = MessageUtilities.buildEmptyMessage(requestMessage.getFactory(),requestMessage.getMessageType(), MessageRole.RESPONSE);
										outResponsePAMessage.setMessage(responseMessage);
									}
									gestore.setOutResponseHeader(headerIntegrazioneRisposta,outResponsePAMessage);
								}else{
									// gia effettuato l'update dell'header in InoltroBuste
								}
							}else{
								if(responseMessage==null){
									responseMessage = MessageUtilities.buildEmptyMessage(requestMessage.getFactory(),requestMessage.getMessageType(), MessageRole.RESPONSE);
									outResponsePAMessage.setMessage(responseMessage);
								}
								gestore.setOutResponseHeader(headerIntegrazioneRisposta,outResponsePAMessage);
							}
						} else {
							throw new Exception("Gestore non inizializzato");
						}
							
					} catch (Exception e) {
						msgDiag.addKeyword(CostantiPdD.KEY_TIPO_HEADER_INTEGRAZIONE,tipiIntegrazionePA_response[i]);
						msgDiag.addKeywordErroreProcessamento(e);
						msgDiag.logPersonalizzato("headerIntegrazione.creazioneFallita");
						logCore.error(msgDiag.getMessaggio_replaceKeywords("headerIntegrazione.creazioneFallita"), e);
					}
				}
				
				// Imposto header di trasporto per la risposta
				this.msgContext.setHeaderIntegrazioneRisposta(propertiesIntegrazioneRisposta);
				
			}			
				
				
	


			// STATELESS
			if (oneWayStateless || sincronoStateless || asincronoStateless) {
				msgDiag.mediumDebug("Aggiorno proprietario messaggio richiesta ...");
				try {
					/* Lo stateless che non è onewayVersione11 non salva niente su database */
					//msgRequest.setReadyForDrop(true);
					//msgRequest.aggiornaProprietarioMessaggio(TimerGestoreMessaggi.ID_MODULO);
					//repositoryBuste.eliminaBustaStatelessFromInBox(idMessageRequest);

					// Committo modifiche (I commit servono per eventuali modifiche ai duplicati)
					openspcoopstate.commit();
				} catch (Exception e) {
					logCore.error("Riscontrato errore durante l'aggiornamento proprietario messaggio", e);
					msgDiag.logErroreGenerico(e, "openspcoopstate.commit(stateless risposta)");
					
					parametriGenerazioneBustaErrore.setBusta(bustaRichiesta);
					parametriGenerazioneBustaErrore.setErroreIntegrazione(ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
							getErroreIntegrazione());
					
					OpenSPCoop2Message errorOpenSPCoopMsg = generaBustaErroreProcessamento(parametriGenerazioneBustaErrore,e);
					
					// Nota: la bustaRichiesta e' stata trasformata da generaErroreProcessamento
					parametriInvioBustaErrore.setOpenspcoopMsg(errorOpenSPCoopMsg);
					parametriInvioBustaErrore.setBusta(parametriGenerazioneBustaErrore.getBusta());
					sendRispostaBustaErrore(parametriInvioBustaErrore);
					openspcoopstate.releaseResource();
					return;
				}
			}



			/* ----- OneWay stateful/stateless ibrido ------ */
			if (oneWayVersione11) {
				msgDiag.mediumDebug("Commit della gestione oneWay stateful/stateless ibrido ...");
				try {
					// Committo modifiche
					openspcoopstate.commit();
				} catch (Exception e) {
					logCore.error("Riscontrato errore durante il commit della gestione oneWay stateful/stateless ibrido", e);
					msgDiag.logErroreGenerico(e, "openspcoopstate.commit(oneway1.1 risposta)");
					
					parametriGenerazioneBustaErrore.setBusta(bustaRichiesta);
					parametriGenerazioneBustaErrore.setErroreIntegrazione(ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
							getErroreIntegrazione());
					
					OpenSPCoop2Message errorOpenSPCoopMsg = generaBustaErroreProcessamento(parametriGenerazioneBustaErrore,e);
					
					// Nota: la bustaRichiesta e' stata trasformata da generaErroreProcessamento
					parametriInvioBustaErrore.setOpenspcoopMsg(errorOpenSPCoopMsg);
					parametriInvioBustaErrore.setBusta(parametriGenerazioneBustaErrore.getBusta());
					sendRispostaBustaErrore(parametriInvioBustaErrore);
					openspcoopstate.releaseResource();
					return;
				}
			}


			// STATEFUL
			if (!portaStateless && !routingStateless){ 

				/* ---------- Gestione Transazione Modulo ---------------- */	    
				// Aggiorno proprietario Messaggio
				msgDiag.mediumDebug("Aggiornamento proprietario messaggio...");
				msgResponse.aggiornaProprietarioMessaggio(TimerGestoreMessaggi.ID_MODULO);

				// Commit JDBC
				msgDiag.mediumDebug("Commit delle operazioni per la gestione della risposta...");
				openspcoopstate.commit();

				// Aggiornamento cache messaggio
				if(msgResponse!=null)
					msgResponse.addMessaggiIntoCache_readFromTable(RicezioneBuste.ID_MODULO, "risposta");

				// Aggiornamento cache proprietario messaggio
				if(msgResponse!=null)
					msgResponse.addProprietariIntoCache_readFromTable(RicezioneBuste.ID_MODULO, "risposta",idMessageRequest,functionAsRouter);

			}





			// Rilascio connessione al DB
			msgDiag.mediumDebug("Commit delle operazioni per la gestione della richiesta effettuato, rilascio della connessione...");
			openspcoopstate.releaseResource();

			// Risposta 
			msgDiag.mediumDebug("Imposto risposta nel context...");
			this.msgContext.setMessageResponse(responseMessage);
			msgDiag.mediumDebug("Lavoro Terminato.");



		} catch (Exception e) {
			logCore.error("ErroreGenerale",e);
			msgDiag.logErroreGenerico(e, "Generale");
			
			parametriGenerazioneBustaErrore.setBusta(bustaRichiesta);
			parametriGenerazioneBustaErrore.setErroreIntegrazione(ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
					getErroreIntegrazione());
			
			OpenSPCoop2Message errorOpenSPCoopMsg = generaBustaErroreProcessamento(parametriGenerazioneBustaErrore,e);
			
			// Nota: la bustaRichiesta e' stata trasformata da generaErroreProcessamento
			parametriInvioBustaErrore.setOpenspcoopMsg(errorOpenSPCoopMsg);
			parametriInvioBustaErrore.setBusta(parametriGenerazioneBustaErrore.getBusta());
			sendRispostaBustaErrore(parametriInvioBustaErrore);
			openspcoopstate.releaseResource();
			return;
		}
		
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
		String credenzialiFornite = "";
		if(credenziali!=null){
			credenzialiFornite = credenziali.toString();
		}
		msgDiag.addKeyword(CostantiPdD.KEY_CREDENZIALI, credenzialiFornite);
	}


	private OpenSPCoop2Message generaBustaErroreProcessamento(RicezioneBusteParametriGenerazioneBustaErrore parametriGenerazioneBustaErrore,Exception e){
		parametriGenerazioneBustaErrore.setErroreProcessamento(true);
		parametriGenerazioneBustaErrore.setEccezioneProcessamento(e);
		return generaBustaErrore(parametriGenerazioneBustaErrore, false);
	}

	private OpenSPCoop2Message generaBustaErroreValidazione(RicezioneBusteParametriGenerazioneBustaErrore parametriGenerazioneBustaErrore){
		parametriGenerazioneBustaErrore.setErroreProcessamento(false);
		return generaBustaErrore(parametriGenerazioneBustaErrore, true);
	}

	private OpenSPCoop2Message generaBustaErrore(RicezioneBusteParametriGenerazioneBustaErrore parametriGenerazioneBustaErrore, boolean erroreValidazione){
		PdDContext pddContext = this.msgContext.getPddContext();
		String idTransazione = (String) pddContext.getObject(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE);
		
		try{
			IntegrationFunctionError integrationFunctionError = parametriGenerazioneBustaErrore.getIntegrationFunctionError(pddContext, erroreValidazione);
			
			OpenSPCoop2Message responseErrorMessage = null;
		
			ErroreCooperazione erroreCooperazione = parametriGenerazioneBustaErrore.getErroreCooperazione();
			ErroreIntegrazione erroreIntegrazione = parametriGenerazioneBustaErrore.getErroreIntegrazione();
			
			List<Eccezione> error = parametriGenerazioneBustaErrore.getError();
			
			//IOpenSPCoopState state = parametriGenerazioneBustaErrore.getOpenspcoop();
			//IDSoggetto identitaPdD = parametriGenerazioneBustaErrore.getIdentitaPdD();
			
			java.util.Hashtable<String,Object> securityPropertiesResponse = null;
			if(parametriGenerazioneBustaErrore.getFlowPropertiesResponse()!=null && parametriGenerazioneBustaErrore.getFlowPropertiesResponse().messageSecurity!=null)
				securityPropertiesResponse = parametriGenerazioneBustaErrore.getFlowPropertiesResponse().messageSecurity.getFlowParameters();
			MessageSecurityContext messageSecurityContext = parametriGenerazioneBustaErrore.getMessageSecurityContext();
			OpenSPCoop2Properties propertiesReader = parametriGenerazioneBustaErrore.getPropertiesReader();
			String profiloGestione = parametriGenerazioneBustaErrore.getProfiloGestione();
			String implementazionePdDMittente = parametriGenerazioneBustaErrore.getImplementazionePdDMittente();
			
			Tracciamento tracciamento = parametriGenerazioneBustaErrore.getTracciamento();
			MsgDiagnostico msgDiag = parametriGenerazioneBustaErrore.getMsgDiag();
			
			Integrazione integrazione = parametriGenerazioneBustaErrore.getIntegrazione();
			if(integrazione==null){
				integrazione = new Integrazione();
				integrazione.setIdModuloInAttesa(this.msgContext.getIdModulo());
			}
			
			IOpenSPCoopState openspcoopState = parametriGenerazioneBustaErrore.getOpenspcoop();
			

			// Genero messaggio di errore
			this.generatoreErrore.updateState(openspcoopState.getStatoRichiesta());
			if(parametriGenerazioneBustaErrore.isErroreProcessamento()){
				if(erroreCooperazione!=null){
					responseErrorMessage = this.generatoreErrore.buildErroreProtocollo_Processamento(
							integrationFunctionError,
							parametriGenerazioneBustaErrore.getBusta(),integrazione, idTransazione, erroreCooperazione,
							securityPropertiesResponse,messageSecurityContext,
							propertiesReader.getGestioneSerializableDB_AttesaAttiva(),
							propertiesReader.getGestioneSerializableDB_CheckInterval(),profiloGestione,
							propertiesReader.getTipoTempoBusta(implementazionePdDMittente),
							propertiesReader.isGenerazioneListaTrasmissioni(implementazionePdDMittente),
							parametriGenerazioneBustaErrore.getEccezioneProcessamento(),
							this.msgContext.getPddContext());
				}
				else if(erroreIntegrazione!=null){
					responseErrorMessage = this.generatoreErrore.buildErroreProtocollo_Processamento(
							integrationFunctionError,
							parametriGenerazioneBustaErrore.getBusta(),integrazione, idTransazione, erroreIntegrazione,
							securityPropertiesResponse,messageSecurityContext,
							propertiesReader.getGestioneSerializableDB_AttesaAttiva(),
							propertiesReader.getGestioneSerializableDB_CheckInterval(),profiloGestione,
							propertiesReader.getTipoTempoBusta(implementazionePdDMittente),
							propertiesReader.isGenerazioneListaTrasmissioni(implementazionePdDMittente),
							parametriGenerazioneBustaErrore.getEccezioneProcessamento(),
							this.msgContext.getPddContext());
				}else{
					responseErrorMessage = this.generatoreErrore.buildErroreProtocollo_Processamento(
							integrationFunctionError,
							parametriGenerazioneBustaErrore.getBusta(),integrazione, idTransazione, error,
							securityPropertiesResponse,messageSecurityContext,
							propertiesReader.getGestioneSerializableDB_AttesaAttiva(),
							propertiesReader.getGestioneSerializableDB_CheckInterval(),profiloGestione,
							propertiesReader.getTipoTempoBusta(implementazionePdDMittente),
							propertiesReader.isGenerazioneListaTrasmissioni(implementazionePdDMittente),
							parametriGenerazioneBustaErrore.getEccezioneProcessamento(),
							this.msgContext.getPddContext());
				}
			}else{
				if(erroreCooperazione!=null){
					responseErrorMessage = this.generatoreErrore.buildErroreProtocollo_Intestazione(
							integrationFunctionError,
							parametriGenerazioneBustaErrore.getBusta(),integrazione, idTransazione,erroreCooperazione,
							securityPropertiesResponse,messageSecurityContext,
							propertiesReader.getGestioneSerializableDB_AttesaAttiva(),
							propertiesReader.getGestioneSerializableDB_CheckInterval(),profiloGestione,
							propertiesReader.getTipoTempoBusta(implementazionePdDMittente),
							propertiesReader.isGenerazioneListaTrasmissioni(implementazionePdDMittente),
							this.msgContext.getPddContext());
				}
				else if(erroreIntegrazione!=null){
					throw new Exception("Method 'generaBustaErroreValidazione' not supported for MessaggioErroreIntegrazione");
				}
				else {
					responseErrorMessage = this.generatoreErrore.buildErroreProtocollo_Intestazione(
							integrationFunctionError,
							parametriGenerazioneBustaErrore.getBusta(),integrazione, idTransazione,error,
							securityPropertiesResponse,messageSecurityContext,
							propertiesReader.getGestioneSerializableDB_AttesaAttiva(),
							propertiesReader.getGestioneSerializableDB_CheckInterval(),profiloGestione,
							propertiesReader.getTipoTempoBusta(implementazionePdDMittente),
							propertiesReader.isGenerazioneListaTrasmissioni(implementazionePdDMittente),
							this.msgContext.getPddContext());
				}
			}

			// ProtocolFactory
			IProtocolFactory<?> protocolFactory = ProtocolFactoryManager.getInstance().getProtocolFactoryByName((String) this.msgContext.getPddContext().getObject(org.openspcoop2.core.constants.Costanti.PROTOCOL_NAME));
						
			//	Tracciamento Busta Ritornata: cambiata nel metodo msgErroreProcessamento
			if(this.msgContext.isTracciamentoAbilitato()){
				EsitoElaborazioneMessaggioTracciato esitoTraccia = 
						EsitoElaborazioneMessaggioTracciato.getEsitoElaborazioneMessaggioInviato();
				SecurityInfo securityInfoResponse  = null;
				boolean functionAsRouter = false; // In questo caso dovrebbe essere sempre false?
				if(functionAsRouter){
					if(messageSecurityContext!=null && messageSecurityContext.getDigestReader(responseErrorMessage.getFactory())!=null){
						IValidazioneSemantica validazioneSemantica = protocolFactory.createValidazioneSemantica(openspcoopState.getStatoRichiesta());
						securityInfoResponse = validazioneSemantica.readSecurityInformation(messageSecurityContext.getDigestReader(responseErrorMessage.getFactory()),responseErrorMessage);
					}
				}
				Validatore v = new Validatore(responseErrorMessage,this.msgContext.getPddContext(),openspcoopState.getStatoRichiesta(),
						parametriGenerazioneBustaErrore.getLogCore(), protocolFactory);
				tracciamento.registraRisposta(responseErrorMessage,securityInfoResponse,
						v.getHeaderProtocollo_senzaControlli(), parametriGenerazioneBustaErrore.getBusta(),esitoTraccia,
						Tracciamento.createLocationString(false,this.msgContext.getSourceLocation()),
						parametriGenerazioneBustaErrore.getCorrelazioneApplicativa(),
						parametriGenerazioneBustaErrore.getCorrelazioneApplicativaRisposta());
			}

			// Messaggio diagnostico
			msgDiag.addKeywords(parametriGenerazioneBustaErrore.getBusta(),false);
			IProtocolManager protocolManager = protocolFactory.createProtocolManager();
			if( (protocolManager.getKeywordMittenteSconosciuto().equals(parametriGenerazioneBustaErrore.getBusta().getDestinatario())==false) &&
					(protocolManager.getKeywordTipoMittenteSconosciuto().equals(parametriGenerazioneBustaErrore.getBusta().getTipoDestinatario())==false)	){
				if(parametriGenerazioneBustaErrore.getBusta().getDestinatario()==null && parametriGenerazioneBustaErrore.getBusta().getTipoDestinatario()==null){
					msgDiag.logPersonalizzato("generazioneMessaggioErroreRisposta.mittenteAnonimo");
				}
				else{
					msgDiag.logPersonalizzato("generazioneMessaggioErroreRisposta");
				}
			} 
			else{
				msgDiag.logPersonalizzato("generazioneMessaggioErroreRisposta.destinatarioSconosciuto");
			}
			
			// Imposto Identificativo Risposta
			// Nota: la bustaRichiesta e' stata trasformata (invertita)
			msgDiag.setIdMessaggioRisposta(parametriGenerazioneBustaErrore.getBusta().getID());
			this.msgContext.getProtocol().setIdRisposta(parametriGenerazioneBustaErrore.getBusta().getID());
			
			if(parametriGenerazioneBustaErrore.getParseException()!=null){
				responseErrorMessage.setParseException(parametriGenerazioneBustaErrore.getParseException());
			}
			
			//responseErrorMessage.writeTo(System.out, false);
			return responseErrorMessage;

		}catch(Exception e){
			return this.generatoreErrore.buildFault(e,pddContext);
		}
	}


	private void sendRispostaBustaErrore(RicezioneBusteParametriInvioBustaErrore parametriInvioBustaErrore){
		sendRispostaBustaErrore(parametriInvioBustaErrore, true);
	}
	private void sendRispostaBustaErrore(RicezioneBusteParametriInvioBustaErrore parametriInvioBustaErrore,boolean eliminaMessaggioRicevuto){


		GestoreMessaggi msgResponse = null;
		RepositoryBuste repositoryBuste = null;
		boolean httpReply = true;
		
		IOpenSPCoopState openspcoop = parametriInvioBustaErrore.getOpenspcoop();
		Busta bustaRisposta = parametriInvioBustaErrore.getBusta();
		MsgDiagnostico msgDiag = parametriInvioBustaErrore.getMsgDiag();
		Logger logCore = parametriInvioBustaErrore.getLogCore();
		PdDContext pddContext = parametriInvioBustaErrore.getPddContext();
		RequestInfo requestInfo = (RequestInfo) pddContext.getObject(org.openspcoop2.core.constants.Costanti.REQUEST_INFO);
		
		try{
			
			IProtocolFactory<?> protocolFactory = ProtocolFactoryManager.getInstance().getProtocolFactoryByName((String) pddContext.getObject(org.openspcoop2.core.constants.Costanti.PROTOCOL_NAME));
						

			/* ------- Gestione messaggi di richiesta per stateless --------*/
			if(eliminaMessaggioRicevuto){
				if(parametriInvioBustaErrore.isOnewayVersione11()){
					GestoreMessaggi msgRichiesta = new GestoreMessaggi(openspcoop,true,
							bustaRisposta.getRiferimentoMessaggio(),Costanti.INBOX,logCore,msgDiag,pddContext);
					msgRichiesta.setReadyForDrop(true);
					msgRichiesta.aggiornaProprietarioMessaggio(TimerGestoreMessaggi.ID_MODULO);
					RepositoryBuste repositoryBustaRichiesta = new RepositoryBuste(openspcoop.getStatoRichiesta(),true,protocolFactory);
					repositoryBustaRichiesta.eliminaBustaStatelessFromInBox(bustaRisposta.getRiferimentoMessaggio());
				}
			}
			
			
			/* ------- Gestione MsgRisposta --------------- */

			// Esamina tipo di risposta (indirizzo di spedizione)
			if( parametriInvioBustaErrore.isNewConnectionForResponse() )
				httpReply = false;
			if( bustaRisposta.getIndirizzoDestinatario()!=null &&
					parametriInvioBustaErrore.isUtilizzoIndirizzoTelematico() )
				httpReply = false;

			// --- Genero una risposta se devo spedirla su di una nuova connessione.
			if(httpReply==false){

				// Salvataggio busta da inviare
				repositoryBuste = new RepositoryBuste(openspcoop.getStatoRisposta(), false, protocolFactory);
				repositoryBuste.registraBustaIntoOutBox(bustaRisposta, parametriInvioBustaErrore.getPropertiesReader().getRepositoryIntervalloScadenzaMessaggi());
				Integrazione infoIntegrazione = new Integrazione();
				repositoryBuste.aggiornaInfoIntegrazioneIntoOutBox(bustaRisposta.getID(),infoIntegrazione);

				msgResponse = new GestoreMessaggi(openspcoop, true, bustaRisposta.getID(),Costanti.OUTBOX,msgDiag,pddContext);
				msgResponse.setOneWayVersione11(parametriInvioBustaErrore.isOnewayVersione11());
				// --- creazione nuovo stato
				msgResponse.registraMessaggio(parametriInvioBustaErrore.getOpenspcoopMsg(),parametriInvioBustaErrore.getCorrelazioneApplicativa(),parametriInvioBustaErrore.getCorrelazioneApplicativaRisposta());
				// ---- aggiorno riferimentoMessaggio
				msgResponse.aggiornaRiferimentoMessaggio(bustaRisposta.getRiferimentoMessaggio());
				// --- Aggiornamento Proprietario messaggio
				// Aggiorno proprietario Messaggio: INOLTRO RISPOSTE	
				msgResponse.aggiornaProprietarioMessaggio(InoltroRisposte.ID_MODULO);

				// --- Spedizione al successivo modulo
				try{
					// spedizione al modulo InoltroRisposte
					EJBUtils ejb = new EJBUtils(parametriInvioBustaErrore.getIdentitaPdD(),this.msgContext.getTipoPorta(),this.msgContext.getIdModulo(),
							bustaRisposta.getRiferimentoMessaggio(),bustaRisposta.getID(),
							Costanti.OUTBOX,openspcoop,msgDiag,parametriInvioBustaErrore.isFunctionAsRouter(),
							parametriInvioBustaErrore.getImplementazionePdDMittente(),
							parametriInvioBustaErrore.getImplementazionePdDDestinatario(),
							parametriInvioBustaErrore.getProfiloGestione(),
							parametriInvioBustaErrore.getPddContext());

					// Il messaggio gia' possiede la busta, non deve essere effettuato imbustamento da InoltroRisposte
					ejb.sendToInoltroRisposte(bustaRisposta,false,null, msgResponse,
							parametriInvioBustaErrore.getCorrelazioneApplicativa(),parametriInvioBustaErrore.getCorrelazioneApplicativaRisposta(),
							parametriInvioBustaErrore.getServizioApplicativoFruitore(),false);		
				}catch(Exception e){
					throw e; // rilancio eccezione;
				}

				// Imposto messaggio ritornato nella connection-reply
				OpenSPCoop2Message soapBodyEmpty = MessageUtilities.buildEmptyMessage(OpenSPCoop2MessageFactory.getDefaultMessageFactory(),
						requestInfo.getProtocolRequestMessageType(), MessageRole.RESPONSE);
				this.msgContext.setMessageResponse(soapBodyEmpty);
				
			}else{
				// Imposto messaggio ritornato nella connection-reply
				OpenSPCoop2Message risposta = parametriInvioBustaErrore.getOpenspcoopMsg();
				this.msgContext.setMessageResponse(risposta);
				
			}

			/* ------- Commit/Close Connessione DB --------------- */
			openspcoop.commit();

			// Aggiornamento cache messaggio
			if(msgResponse!=null)
				msgResponse.addMessaggiIntoCache_readFromTable(RicezioneBuste.ID_MODULO, "risposta verso nuova connessione");

			// Aggiornamento cache proprietario messaggio
			if(msgResponse!=null)
				msgResponse.addProprietariIntoCache_readFromTable(RicezioneBuste.ID_MODULO, "risposta verso nuova connessione",
						bustaRisposta.getRiferimentoMessaggio(),parametriInvioBustaErrore.isFunctionAsRouter());


		}catch (Exception e) {
			IntegrationFunctionError integrationFunctionError = AbstractErrorGenerator.getIntegrationInternalError(pddContext); // default
			msgDiag.logErroreGenerico(e, "sendRispostaBustaErrore");
			this.msgContext.setMessageResponse(this.generatoreErrore.buildErroreProcessamento(pddContext,
					integrationFunctionError,
					ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.getErroreIntegrazione(), e));
		}  	

	}


	/* Utility per la gestione del Message-Security e MTOM */
	/**
	 * Ritorna le Proprieta' Message-Security relative alla ricezione della busta
	 * 
	 * @return Proprieta' Message-Security relative alla ricezione della busta
	 */
	private FlowProperties getFlowPropertiesRequest(OpenSPCoop2Message requestMessage, Busta bustaRichiesta,
			ConfigurazionePdDManager configurazionePdDReader,StateMessage state,
			MsgDiagnostico msgDiag,Logger logCore,OpenSPCoop2Properties properties,
			RuoloBusta ruoloBustaRicevuta,String implementazionePdDMittente,
			RequestInfo requestInfo, PdDContext pddContext,
			PortaApplicativa paFind)throws DriverConfigurazioneException{

		// Proprieta' Message-Security relative alla ricezione della busta

		// Messaggi AD HOC (riscontro) responseFlow della porta delegata, utilizzo l'id del riscontro

		// RichiestaOneWay requestFlow della PortaApplicativa
		// RispostaOneWay (integrazione) responseFlow della porta delegata

		// RichiestaSincrona requestFlow della porta applicativa
		// RispostaSincrona su new Connection NON SUPPORTATA, cmq: (integrazione) responseFlow della porta delegata

		// RichiestaAsincronaSimmetrica requestFlow della PortaApplicativa
		// RicevutaRichiestaAsincronaSimmetrica (integrazione) responseFlow della porta delegata che ha effettuato la richiesta
		// RispostaAsincronaSimmetrica (integrazione) responseFlow della porta delegata che ha effettuato la richiesta
		// RicevutaRispostaAsincronaSimmetrica (integrazione) responseFlow della porta delegata che ha effettuato la risposta

		// RichiestaAsincronaAsimmetrica requestFlow della PortaApplicativa
		// RicevutaRichiestaAsincronaAsimmetrica (integrazione) responseFlow della porta delegata che ha effettuato la richiesta
		// RispostaAsincronaAsimmetrica (conversioneServizio) requestFlow della porta applicativa
		// RicevutaRispostaAsincronaAsimmetrica (integrazione) responseFlow della porta delegata che ha effettuato la risposta

		FlowProperties flowProperties = new FlowProperties();
		flowProperties.tipoMessaggio = RuoloMessaggio.RICHIESTA;
		
		ProfiloDiCollaborazione profiloCollaborazione = null;
		try{
			
			IProtocolFactory<?> protocolFactory = ProtocolFactoryManager.getInstance().getProtocolFactoryByName((String) pddContext.getObject(org.openspcoop2.core.constants.Costanti.PROTOCOL_NAME));
						
			// Messaggi AD HOC senza profilo: RISCONTRO
			if(bustaRichiesta.getProfiloDiCollaborazione()==null && bustaRichiesta.sizeListaRiscontri()>0 &&
					properties.isGestioneRiscontri(implementazionePdDMittente)){
				RepositoryBuste repository = new RepositoryBuste(state, true,protocolFactory);
				Integrazione integrazione = repository.getInfoIntegrazioneFromOutBox(bustaRichiesta.getRiscontro(0).getID());
				if(integrazione.getNomePorta()!=null){
					IDPortaDelegata idPD = new IDPortaDelegata();
					idPD.setNome(integrazione.getNomePorta());
					PortaDelegata pd = configurazionePdDReader.getPortaDelegata_SafeMethod(idPD);
					flowProperties.messageSecurity = configurazionePdDReader.getPD_MessageSecurityForReceiver(pd);
					flowProperties.mtom = configurazionePdDReader.getPD_MTOMProcessorForReceiver(pd);
				}

			}

			// Profilo OneWay e Sincrono
			if(org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.ONEWAY.equals(bustaRichiesta.getProfiloDiCollaborazione()) || 
					org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.SINCRONO.equals(bustaRichiesta.getProfiloDiCollaborazione())
			) {	
				// Richiesta
				if(bustaRichiesta.getRiferimentoMessaggio()==null){
					PortaApplicativa pa = paFind;
					if(pa==null){
						IDServizio idServizioPA = null;
						try {
							idServizioPA = IDServizioFactory.getInstance().getIDServizioFromValues(bustaRichiesta.getTipoServizio(),bustaRichiesta.getServizio(), 
									bustaRichiesta.getTipoDestinatario(),bustaRichiesta.getDestinatario(), 
									bustaRichiesta.getVersioneServizio());
						}catch(Exception e) {
							// se non sono presenti dati identificativi del servizio non potro poi localizzare la PA
						}
						if(idServizioPA!=null) {
							idServizioPA.setAzione(bustaRichiesta.getAzione());
							try {
								pa = this.getPortaApplicativa(configurazionePdDReader, idServizioPA);
							}catch(DriverConfigurazioneNotFound notFound) {}
						}
					}
					if(pa!=null) {
						flowProperties.messageSecurity = configurazionePdDReader.getPA_MessageSecurityForReceiver(pa, logCore, requestMessage, bustaRichiesta, requestInfo, pddContext);
						flowProperties.mtom = configurazionePdDReader.getPA_MTOMProcessorForReceiver(pa);
					}
				}
				// Risposta
				else{
					RepositoryBuste repository = new RepositoryBuste(state, false,protocolFactory);
					Integrazione integrazione = repository.getInfoIntegrazioneFromOutBox(bustaRichiesta.getRiferimentoMessaggio());
					IDPortaDelegata idPD = new IDPortaDelegata();
					idPD.setNome(integrazione.getNomePorta());
					PortaDelegata pd = configurazionePdDReader.getPortaDelegata_SafeMethod(idPD);
					flowProperties.messageSecurity = configurazionePdDReader.getPD_MessageSecurityForReceiver(pd);
					flowProperties.mtom = configurazionePdDReader.getPD_MTOMProcessorForReceiver(pd);
					flowProperties.tipoMessaggio = RuoloMessaggio.RISPOSTA;
				}
			}

			// Profilo Asincrono Simmetrico
			else if(org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.ASINCRONO_SIMMETRICO.equals(bustaRichiesta.getProfiloDiCollaborazione())) {	

				//	Richiesta Asincrona
				if(bustaRichiesta.getRiferimentoMessaggio()==null){

					PortaApplicativa pa = paFind;
					if(pa==null){
						IDServizio idServizioPA = null;
						try {
							idServizioPA = IDServizioFactory.getInstance().getIDServizioFromValues(bustaRichiesta.getTipoServizio(),bustaRichiesta.getServizio(), 
									bustaRichiesta.getTipoDestinatario(),bustaRichiesta.getDestinatario(), 
									bustaRichiesta.getVersioneServizio());
						}catch(Exception e) {
							// se non sono presenti dati identificativi del servizio non potro poi localizzare la PA
						}
						if(idServizioPA!=null) {
							idServizioPA.setAzione(bustaRichiesta.getAzione());
							try {
								pa = this.getPortaApplicativa(configurazionePdDReader, idServizioPA);
							}catch(DriverConfigurazioneNotFound notFound) {}
						}
					}
					if(pa!=null) {
						flowProperties.messageSecurity = configurazionePdDReader.getPA_MessageSecurityForReceiver(pa, logCore, requestMessage, bustaRichiesta, requestInfo, pddContext);
						flowProperties.mtom = configurazionePdDReader.getPA_MTOMProcessorForReceiver(pa);
					}

				}else{

					//	Risposta Asincrona
					if(RuoloBusta.RISPOSTA.equals(ruoloBustaRicevuta.toString())){

						RepositoryBuste repository = new RepositoryBuste(state, false,protocolFactory);
						Integrazione integrazione = repository.getInfoIntegrazioneFromOutBox(bustaRichiesta.getRiferimentoMessaggio());
						IDPortaDelegata idPD = new IDPortaDelegata();
						idPD.setNome(integrazione.getNomePorta());
						PortaDelegata pd = configurazionePdDReader.getPortaDelegata_SafeMethod(idPD);
						flowProperties.messageSecurity = configurazionePdDReader.getPD_MessageSecurityForSender(pd, logCore, requestMessage, bustaRichiesta, requestInfo, pddContext);
						flowProperties.mtom = configurazionePdDReader.getPD_MTOMProcessorForSender(pd);

					}
					//	Ricevuta alla richiesta.
					else if(RuoloBusta.RICEVUTA_RICHIESTA.equals(ruoloBustaRicevuta.toString())){

						RepositoryBuste repository = new RepositoryBuste(state, true, protocolFactory);
						Integrazione integrazione = repository.getInfoIntegrazioneFromOutBox(bustaRichiesta.getRiferimentoMessaggio());
						IDPortaDelegata idPD = new IDPortaDelegata();
						idPD.setNome(integrazione.getNomePorta());
						PortaDelegata pd = configurazionePdDReader.getPortaDelegata_SafeMethod(idPD);
						flowProperties.messageSecurity = configurazionePdDReader.getPD_MessageSecurityForReceiver(pd);
						flowProperties.mtom = configurazionePdDReader.getPD_MTOMProcessorForReceiver(pd);
						flowProperties.tipoMessaggio = RuoloMessaggio.RISPOSTA;

					}
					//	Ricevuta alla risposta.
					else if(RuoloBusta.RICEVUTA_RISPOSTA.equals(ruoloBustaRicevuta.toString())){

						RepositoryBuste repository = new RepositoryBuste(state, false, protocolFactory);
						Integrazione integrazione = repository.getInfoIntegrazioneFromOutBox(bustaRichiesta.getRiferimentoMessaggio());
						IDPortaDelegata idPD = new IDPortaDelegata();
						idPD.setNome(integrazione.getNomePorta());
						PortaDelegata pd = configurazionePdDReader.getPortaDelegata_SafeMethod(idPD);
						flowProperties.messageSecurity = configurazionePdDReader.getPD_MessageSecurityForReceiver(pd);
						flowProperties.mtom = configurazionePdDReader.getPD_MTOMProcessorForReceiver(pd);
						flowProperties.tipoMessaggio = RuoloMessaggio.RISPOSTA;

					}

				}

			}

			// Profilo Asincrono Asimmetrico
			else if(org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.ASINCRONO_ASIMMETRICO.equals(bustaRichiesta.getProfiloDiCollaborazione())) {	

				//	Richiesta Asincrona
				if(bustaRichiesta.getRiferimentoMessaggio()==null){

					PortaApplicativa pa = paFind;
					if(pa==null){
						IDServizio idServizioPA = null;
						try {
							idServizioPA = IDServizioFactory.getInstance().getIDServizioFromValues(bustaRichiesta.getTipoServizio(),bustaRichiesta.getServizio(), 
									bustaRichiesta.getTipoDestinatario(),bustaRichiesta.getDestinatario(), 
									bustaRichiesta.getVersioneServizio());
						}catch(Exception e) {
							// se non sono presenti dati identificativi del servizio non potro poi localizzare la PA
						}
						if(idServizioPA!=null) {
							idServizioPA.setAzione(bustaRichiesta.getAzione());
							try {
								pa = this.getPortaApplicativa(configurazionePdDReader, idServizioPA);
							}catch(DriverConfigurazioneNotFound notFound) {}
						}
					}
					if(pa!=null) {
						flowProperties.messageSecurity = configurazionePdDReader.getPA_MessageSecurityForReceiver(pa, logCore, requestMessage, bustaRichiesta, requestInfo, pddContext);
						flowProperties.mtom = configurazionePdDReader.getPA_MTOMProcessorForReceiver(pa);
					}

				}else{

					profiloCollaborazione = new ProfiloDiCollaborazione(state,protocolFactory);

					//	Risposta Asincrona
					if(RuoloBusta.RISPOSTA.equals(ruoloBustaRicevuta.toString())){

						// ConversioneServizio.
						IDServizio idServizioOriginale = profiloCollaborazione.asincronoAsimmetrico_getDatiConsegnaRisposta(bustaRichiesta.getRiferimentoMessaggio());
						PortaApplicativa pa = paFind;
						if(pa==null){
							IDServizio idServizioPA = null;
							try {
								idServizioPA = IDServizioFactory.getInstance().getIDServizioFromValues(idServizioOriginale.getTipo(),idServizioOriginale.getNome(), 
										bustaRichiesta.getTipoDestinatario(),bustaRichiesta.getDestinatario(), 
										idServizioOriginale.getVersione());
							}catch(Exception e) {
								// se non sono presenti dati identificativi del servizio non potro poi localizzare la PA
							}
							if(idServizioPA!=null) {
								idServizioPA.setAzione(idServizioOriginale.getAzione());
								try {
									pa = this.getPortaApplicativa(configurazionePdDReader,idServizioPA);
								}catch(DriverConfigurazioneNotFound notFound) {}
							}
						}
						if(pa!=null) {
							flowProperties.messageSecurity = configurazionePdDReader.getPA_MessageSecurityForReceiver(pa, logCore, requestMessage, bustaRichiesta, requestInfo, pddContext);
							flowProperties.mtom = configurazionePdDReader.getPA_MTOMProcessorForReceiver(pa);
						}

					}
					//	Ricevuta alla richiesta.
					else if(RuoloBusta.RICEVUTA_RICHIESTA.equals(ruoloBustaRicevuta.toString())){

						RepositoryBuste repository = new RepositoryBuste(state, true,protocolFactory);
						Integrazione integrazione = repository.getInfoIntegrazioneFromOutBox(bustaRichiesta.getRiferimentoMessaggio());
						IDPortaDelegata idPD = new IDPortaDelegata();
						idPD.setNome(integrazione.getNomePorta());
						PortaDelegata pd = configurazionePdDReader.getPortaDelegata_SafeMethod(idPD);
						flowProperties.messageSecurity = configurazionePdDReader.getPD_MessageSecurityForReceiver(pd);
						flowProperties.mtom = configurazionePdDReader.getPD_MTOMProcessorForReceiver(pd);
						flowProperties.tipoMessaggio = RuoloMessaggio.RISPOSTA;

					}
					//	Ricevuta alla risposta.
					else if(RuoloBusta.RICEVUTA_RISPOSTA.equals(ruoloBustaRicevuta.toString())){

						RepositoryBuste repository = new RepositoryBuste(state, false,protocolFactory);
						Integrazione integrazione = repository.getInfoIntegrazioneFromOutBox(bustaRichiesta.getRiferimentoMessaggio());
						IDPortaDelegata idPD = new IDPortaDelegata();
						idPD.setNome(integrazione.getNomePorta());
						PortaDelegata pd = configurazionePdDReader.getPortaDelegata_SafeMethod(idPD);
						flowProperties.messageSecurity = configurazionePdDReader.getPD_MessageSecurityForReceiver(pd);
						flowProperties.mtom = configurazionePdDReader.getPD_MTOMProcessorForReceiver(pd);
						flowProperties.tipoMessaggio = RuoloMessaggio.RISPOSTA;

					}

				}

			}

		}catch(Exception e){
			msgDiag.logErroreGenerico(e, "lettura_MessageSecurity_MTOM_RequestProperties");
			logCore.error("Lettura dati Message-Security / MTOM per la ricezione del messaggio non riuscita",e);
		}finally{
			if(profiloCollaborazione!=null)
				state.closePreparedStatement();
		}

		return flowProperties;
	}


	/**
	 * Ritorna le Proprieta' Message-Security relative alla spedizione della busta
	 * 
	 * @return Proprieta' Message-Security relative alla spedizione della busta
	 */
	private FlowProperties getFlowPropertiesResponse(OpenSPCoop2Message requestMessage, Busta bustaRichiesta,
			ConfigurazionePdDManager configurazionePdDReader,StateMessage state,
			MsgDiagnostico msgDiag,Logger logCore,OpenSPCoop2Properties properties,
			RuoloBusta ruoloBustaRicevuta,String implementazionePdDMittente,
			RequestInfo requestInfo, PdDContext pddContext,
			PortaApplicativa paFind)throws DriverConfigurazioneException{

		//	Proprieta' Message-Security relative alla spedizione della busta

		// Messaggi AD HOC senza profilo (riscontro) responseFlow della porta applicativa

		// RispostaOneWay responseFlow della porta applicativa

		// RispostaSincrona responseFlow della porta applicativa

		// RicevutaRichiestaAsincronaSimmetrica responseFlow della porta applicativa
		// RicevutaRispostaAsincronaSimmetrica (integrazione) requestFlow della porta delegata che ha effettuato la richiesta

		// RicevutaRichiestaAsincronaAsimmetrica responseFlow della porta applicativa
		// RicevutaRispostaAsincronaAsimmetrica (conversioneServizio) responseFlow della porta applicativa

		FlowProperties flowProperties = new FlowProperties();
		flowProperties.tipoMessaggio = RuoloMessaggio.RISPOSTA;

		// NOTA: La busta che sto gestendo e' la busta che ho ricevuto, non quella che sto inviando!!

		ProfiloDiCollaborazione profiloCollaborazione = null;
		try{

			IProtocolFactory<?> protocolFactory = ProtocolFactoryManager.getInstance().getProtocolFactoryByName((String) pddContext.getObject(org.openspcoop2.core.constants.Costanti.PROTOCOL_NAME));
						
			// Messaggi AD HOC senza profilo: RISCONTRO
			if(bustaRichiesta.getProfiloDiCollaborazione()==null && bustaRichiesta.sizeListaRiscontri()>0 &&
					properties.isGestioneRiscontri(implementazionePdDMittente)){
				if(bustaRichiesta.getTipoServizio()!=null &&
						bustaRichiesta.getServizio()!=null){
					PortaApplicativa pa = paFind;
					if(pa==null){
						IDServizio idServizioPA = null;
						try {
							idServizioPA = IDServizioFactory.getInstance().getIDServizioFromValues(bustaRichiesta.getTipoServizio(),bustaRichiesta.getServizio(), 
									bustaRichiesta.getTipoDestinatario(),bustaRichiesta.getDestinatario(), 
									bustaRichiesta.getVersioneServizio());
						}catch(Exception e) {
							// se non sono presenti dati identificativi del servizio non potro poi localizzare la PA
						}
						if(idServizioPA!=null) {
							idServizioPA.setAzione(bustaRichiesta.getAzione());
							try {
								pa = this.getPortaApplicativa(configurazionePdDReader, idServizioPA);
							}catch(DriverConfigurazioneNotFound notFound) {}
						}
					}
					if(pa!=null) {
						flowProperties.messageSecurity = configurazionePdDReader.getPA_MessageSecurityForSender(pa);
						flowProperties.mtom = configurazionePdDReader.getPA_MTOMProcessorForSender(pa);
					}
				}
			}
			// Messaggi con profilo
			else if(org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.ONEWAY.equals(bustaRichiesta.getProfiloDiCollaborazione()) || 
					org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.SINCRONO.equals(bustaRichiesta.getProfiloDiCollaborazione())
			) {	
				PortaApplicativa pa = paFind;
				if(pa==null){
					IDServizio idServizioPA = null;
					try {
						idServizioPA = IDServizioFactory.getInstance().getIDServizioFromValues(bustaRichiesta.getTipoServizio(),bustaRichiesta.getServizio(), 
								bustaRichiesta.getTipoDestinatario(),bustaRichiesta.getDestinatario(), 
								bustaRichiesta.getVersioneServizio());
					}catch(Exception e) {
						// se non sono presenti dati identificativi del servizio non potro poi localizzare la PA
					}
					if(idServizioPA!=null) {
						idServizioPA.setAzione(bustaRichiesta.getAzione());
						try {
							pa = this.getPortaApplicativa(configurazionePdDReader, idServizioPA);
						}catch(DriverConfigurazioneNotFound notFound) {}
					}
				}
				if(pa!=null) {
					flowProperties.messageSecurity = configurazionePdDReader.getPA_MessageSecurityForSender(pa);
					flowProperties.mtom = configurazionePdDReader.getPA_MTOMProcessorForSender(pa);
				}
			}

			//	 Profilo Asincrono Simmetrico
			else if(org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.ASINCRONO_SIMMETRICO.equals(bustaRichiesta.getProfiloDiCollaborazione())) {	

				//	Ricevuta alla richiesta.
				if(bustaRichiesta.getRiferimentoMessaggio()==null){

					PortaApplicativa pa = paFind;
					if(pa==null){
						IDServizio idServizioPA = null;
						try {
							idServizioPA = IDServizioFactory.getInstance().getIDServizioFromValues(bustaRichiesta.getTipoServizio(),bustaRichiesta.getServizio(), 
									bustaRichiesta.getTipoDestinatario(),bustaRichiesta.getDestinatario(), 
									bustaRichiesta.getVersioneServizio());
						}catch(Exception e) {
							// se non sono presenti dati identificativi del servizio non potro poi localizzare la PA
						}
						if(idServizioPA!=null) {
							idServizioPA.setAzione(bustaRichiesta.getAzione());
							try {
								pa = this.getPortaApplicativa(configurazionePdDReader, idServizioPA);
							}catch(DriverConfigurazioneNotFound notFound) {}
						}
					}
					if(pa!=null) {
						flowProperties.messageSecurity = configurazionePdDReader.getPA_MessageSecurityForSender(pa);
						flowProperties.mtom = configurazionePdDReader.getPA_MTOMProcessorForSender(pa);
					}

				}

				//	Ricevuta alla risposta.
				else if(RuoloBusta.RISPOSTA.equals(ruoloBustaRicevuta.toString())){

					RepositoryBuste repository = new RepositoryBuste(state, false, protocolFactory);
					Integrazione integrazione = repository.getInfoIntegrazioneFromOutBox(bustaRichiesta.getRiferimentoMessaggio());
					IDPortaDelegata idPD = new IDPortaDelegata();
					idPD.setNome(integrazione.getNomePorta());
					PortaDelegata pd = configurazionePdDReader.getPortaDelegata_SafeMethod(idPD);
					flowProperties.messageSecurity = configurazionePdDReader.getPD_MessageSecurityForSender(pd, logCore, requestMessage, bustaRichiesta, requestInfo, pddContext);
					flowProperties.mtom = configurazionePdDReader.getPD_MTOMProcessorForSender(pd);

				}

			}

			//	Profilo Asincrono Asimmetrico
			else if(org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.ASINCRONO_ASIMMETRICO.equals(bustaRichiesta.getProfiloDiCollaborazione())) {	

				profiloCollaborazione = new ProfiloDiCollaborazione(state, protocolFactory);

				//	Ricevuta alla richiesta.
				if(bustaRichiesta.getRiferimentoMessaggio()==null){

					PortaApplicativa pa = paFind;
					if(pa==null){
						IDServizio idServizioPA = null;
						try {
							idServizioPA = IDServizioFactory.getInstance().getIDServizioFromValues(bustaRichiesta.getTipoServizio(),bustaRichiesta.getServizio(), 
									bustaRichiesta.getTipoDestinatario(),bustaRichiesta.getDestinatario(), 
									bustaRichiesta.getVersioneServizio());
						}catch(Exception e) {
							// se non sono presenti dati identificativi del servizio non potro poi localizzare la PA
						}
						if(idServizioPA!=null) {
							idServizioPA.setAzione(bustaRichiesta.getAzione());
							try {
								pa = this.getPortaApplicativa(configurazionePdDReader, idServizioPA);
							}catch(DriverConfigurazioneNotFound notFound) {}
						}
					}
					if(pa!=null) {
						flowProperties.messageSecurity = configurazionePdDReader.getPA_MessageSecurityForSender(pa);
						flowProperties.mtom = configurazionePdDReader.getPA_MTOMProcessorForSender(pa);
					}

				}

				//	Ricevuta alla risposta.
				else if(RuoloBusta.RISPOSTA.equals(ruoloBustaRicevuta.toString())){

					// ConversioneServizio.
					IDServizio idServizioOriginale = profiloCollaborazione.asincronoAsimmetrico_getDatiConsegnaRisposta(bustaRichiesta.getRiferimentoMessaggio());
					PortaApplicativa pa = paFind;
					if(pa==null){
						IDServizio idServizioPA = null;
						try {
							idServizioPA = IDServizioFactory.getInstance().getIDServizioFromValues(idServizioOriginale.getTipo(),idServizioOriginale.getNome(), 
									bustaRichiesta.getTipoDestinatario(),bustaRichiesta.getDestinatario(), 
									idServizioOriginale.getVersione());
						}catch(Exception e) {
							// se non sono presenti dati identificativi del servizio non potro poi localizzare la PA
						}
						if(idServizioPA!=null) {
							idServizioPA.setAzione(idServizioOriginale.getAzione());
							try {
								pa = this.getPortaApplicativa(configurazionePdDReader, idServizioPA);
							}catch(DriverConfigurazioneNotFound notFound) {}
						}
					}
					if(pa!=null) {
						flowProperties.messageSecurity = configurazionePdDReader.getPA_MessageSecurityForSender(pa);
						flowProperties.mtom = configurazionePdDReader.getPA_MTOMProcessorForSender(pa);
					}

				}

			}



		}catch(Exception e){
			msgDiag.logErroreGenerico(e, "lettura_MessageSecurity_MTOM_ResponseProperties");
			logCore.error("Lettura dati Message-Security / MTOM per la spedizione del messaggio di risposta non riuscita",e);
		}finally{
			if(profiloCollaborazione!=null)
				state.closePreparedStatement();
		}

		return flowProperties;
	}




	private boolean gestioneRispostaAsincrona_checkPresenzaRichiesta(long scadenzaControllo,int checkInterval,Busta bustaRichiesta,
			IOpenSPCoopState openspcoopstate,MsgDiagnostico msgDiag,boolean newConnectionForResponse,
			PdDContext pddContext) throws Exception{
		boolean attendiTerminazioneRichiesta = false;

		IProtocolFactory<?> protocolFactory = ProtocolFactoryManager.getInstance().getProtocolFactoryByName((String) pddContext.getObject(org.openspcoop2.core.constants.Costanti.PROTOCOL_NAME));
				
		// Puo' potenzialmente essere una ricevuta alla richiesta (new connection for response)
		// o alla risposta (e cmq la risposta l'ho generata io e quindi e' chiaro che ho finito di gestire la richiesta)
		boolean isRicevutaRichiesta = false;
		boolean isRicevutaRisposta = false;
		if(newConnectionForResponse){
			if( ((org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.ASINCRONO_SIMMETRICO.equals(bustaRichiesta.getProfiloDiCollaborazione())) 
					||
					(org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.ASINCRONO_ASIMMETRICO.equals(bustaRichiesta.getProfiloDiCollaborazione()))) &&
					bustaRichiesta.getRiferimentoMessaggio()!=null) {	
				// Le ricevute hanno il riferimento messaggio anche nelle linee guida!
				ProfiloDiCollaborazione profiloCollaborazione = new ProfiloDiCollaborazione(openspcoopstate.getStatoRichiesta(),protocolFactory);
				isRicevutaRichiesta = profiloCollaborazione.asincrono_isRicevutaRichiesta(bustaRichiesta.getRiferimentoMessaggio());
				isRicevutaRisposta =  profiloCollaborazione.asincrono_isRicevutaRisposta(bustaRichiesta.getRiferimentoMessaggio());
			}
		}


		while( DateManager.getTimeMillis() < scadenzaControllo ){

			msgDiag.mediumDebug("RICHIESTA  NOW["+DateManager.getTimeMillis()+"] < SCADENZA["+scadenzaControllo+"]");

			if(org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.ASINCRONO_SIMMETRICO.equals(bustaRichiesta.getProfiloDiCollaborazione())) {	
				if( (bustaRichiesta.getRiferimentoMessaggio()!=null) || 
						( bustaRichiesta.getCollaborazione()!=null && (bustaRichiesta.getCollaborazione().equals(bustaRichiesta.getID())==false) ) 
				){

					if( ( newConnectionForResponse==false ) || 
							( newConnectionForResponse && (!isRicevutaRichiesta) && (!isRicevutaRisposta) ) 
					){	
						String idRichiesta = bustaRichiesta.getRiferimentoMessaggio();
						if(idRichiesta==null){
							// LineeGuida non sara' presente il riferimentoMessaggio ma l'elemento collaborazione
							idRichiesta = bustaRichiesta.getCollaborazione();
						}
						GestoreMessaggi checkRichiesta = new GestoreMessaggi(openspcoopstate, false, idRichiesta,Costanti.OUTBOX,msgDiag,pddContext);
						attendiTerminazioneRichiesta =  checkRichiesta.existsMessageInProcessamento();
						if(!attendiTerminazioneRichiesta)
							break;
						/*else{
							String proprietario = checkRichiesta.getProprietario(RicezioneBuste.ID_MODULO);
							msgDiag.infoOpenSPCoop("RICHIESTA  ID["+bustaRichiesta.getRiferimentoMessaggio()+"] OUTBOX attendiTerminazioneRichiesta["+attendiTerminazioneRichiesta+"] PROPRIETARIO["+proprietario+"]");
						}*/
					}else{
						break; // si tratta di una ricevuta alla richiesta o alla risposta asincrona simmetrica in un caso di newConnectionForResponse
					}
				}else{
					break; // non si tratta di una risposta asincrona simmetrica, ma di una richiesta
				}
			}else if(org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.ASINCRONO_ASIMMETRICO.equals(bustaRichiesta.getProfiloDiCollaborazione())) {
				if( (bustaRichiesta.getRiferimentoMessaggio()!=null) || 
						( bustaRichiesta.getCollaborazione()!=null && (bustaRichiesta.getCollaborazione().equals(bustaRichiesta.getID())==false) ) 
				){

					if( ( newConnectionForResponse==false ) || 
							( newConnectionForResponse && (!isRicevutaRichiesta) && (!isRicevutaRisposta) ) 
					){	
						String idRichiesta = bustaRichiesta.getRiferimentoMessaggio();
						if(idRichiesta==null){
							// LineeGuida non sara' presente il riferimentoMessaggio ma l'elemento collaborazione
							idRichiesta = bustaRichiesta.getCollaborazione();
						}
						GestoreMessaggi checkRichiesta = 
							new GestoreMessaggi(openspcoopstate, false, idRichiesta,Costanti.INBOX,msgDiag,pddContext);
						attendiTerminazioneRichiesta =  checkRichiesta.existsMessageInProcessamento();
						if(!attendiTerminazioneRichiesta)
							break;
						/*else{
							String proprietario = checkRichiesta.getProprietario(RicezioneBuste.ID_MODULO);
							msgDiag.infoOpenSPCoop("RICHIESTA  ID["+bustaRichiesta.getRiferimentoMessaggio()+"] INBOX attendiTerminazioneRichiesta["+attendiTerminazioneRichiesta+"] PROPRIETARIO["+proprietario+"]");
						}*/
					}else{
						break; // si tratta di una ricevuta alla richiesta o risposta asincrona asimmetrica in un caso di newConnectionForResponse
					}
				}else{
					break; // non si tratta di una richiesta-stato asincrona asimmetrica, ma di una richiesta
				}
			}else{
				break;
			}

			if(bustaRichiesta.getProfiloDiCollaborazione()!=null){
				msgDiag.mediumDebug("Busta di risposta con profilo ["+bustaRichiesta.getProfiloDiCollaborazione().getEngineValue()+"] non gestibile, si attende il completamento" +
						" della gestione della richiesta");
			}
			else{
				msgDiag.mediumDebug("Busta di risposta con profilo null?? non gestibile, si attende il completamento" +
						" della gestione della richiesta");
			}

			Utilities.sleep(checkInterval);
		}
		return attendiTerminazioneRichiesta;
	}

	private boolean gestioneRispostaAsincrona_checkPresenzaRicevutaRichiesta(long scadenzaControllo,int checkInterval,Busta bustaRichiesta,
			IOpenSPCoopState openspcoopstate,MsgDiagnostico msgDiag,boolean newConnectionForResponse,
			PdDContext pddContext)throws Exception{
		boolean attendiTerminazioneRicevutaRichiesta = false;

		IProtocolFactory<?> protocolFactory = ProtocolFactoryManager.getInstance().getProtocolFactoryByName((String) pddContext.getObject(org.openspcoop2.core.constants.Costanti.PROTOCOL_NAME));
		
		// Puo' potenzialmente essere una ricevuta alla richiesta (new connection for response)
		// o alla risposta (e cmq la risposta l'ho generata io e quindi e' chiaro che ho finito di gestire la richiesta)
		boolean isRicevutaRichiesta = false;
		boolean isRicevutaRisposta = false;
		if(newConnectionForResponse){
			if( ((org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.ASINCRONO_SIMMETRICO.equals(bustaRichiesta.getProfiloDiCollaborazione())) 
					||
					(org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.ASINCRONO_ASIMMETRICO.equals(bustaRichiesta.getProfiloDiCollaborazione()))) &&
					bustaRichiesta.getRiferimentoMessaggio()!=null) {
				// Le ricevute hanno il riferimento messaggio anche nelle linee guida!
				ProfiloDiCollaborazione profiloCollaborazione = new ProfiloDiCollaborazione(openspcoopstate.getStatoRichiesta(),protocolFactory);
				isRicevutaRichiesta = profiloCollaborazione.asincrono_isRicevutaRichiesta(bustaRichiesta.getRiferimentoMessaggio());
				isRicevutaRisposta =  profiloCollaborazione.asincrono_isRicevutaRisposta(bustaRichiesta.getRiferimentoMessaggio());
			}
		}

		while( DateManager.getTimeMillis() < scadenzaControllo ){

			msgDiag.mediumDebug("RICEVUTA RICHIESTA  NOW["+DateManager.getTimeMillis()+"] < SCADENZA["+scadenzaControllo+"]");

			if(org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.ASINCRONO_SIMMETRICO.equals(bustaRichiesta.getProfiloDiCollaborazione())) {	
				if( (bustaRichiesta.getRiferimentoMessaggio()!=null) || 
						( bustaRichiesta.getCollaborazione()!=null && (bustaRichiesta.getCollaborazione().equals(bustaRichiesta.getID())==false) ) 
				){

					if( ( newConnectionForResponse==false ) || 
							( newConnectionForResponse && (!isRicevutaRichiesta) && (!isRicevutaRisposta) ) 
					){
						String idRicevutaRichiesta = bustaRichiesta.getRiferimentoMessaggio();
						if(idRicevutaRichiesta==null){
							// LineeGuida non sara' presente il riferimentoMessaggio ma l'elemento collaborazione
							idRicevutaRichiesta = bustaRichiesta.getCollaborazione();
						}
						GestoreMessaggi checkRicevutaRichiesta = new GestoreMessaggi(openspcoopstate, false, idRicevutaRichiesta,Costanti.INBOX,msgDiag,pddContext);
						attendiTerminazioneRicevutaRichiesta =  checkRicevutaRichiesta.existsMessageInProcessamentoByReference();
						if(!attendiTerminazioneRicevutaRichiesta)
							break;
						/*else{
							String proprietario = checkRicevutaRichiesta.getProprietario(RicezioneBuste.ID_MODULO);
							msgDiag.infoOpenSPCoop("RICHIESTA  ID["+bustaRichiesta.getRiferimentoMessaggio()+"] INBOX attendiTerminazioneRichiesta["+attendiTerminazioneRicevutaRichiesta+"] PROPRIETARIO["+proprietario+"]");
						}*/
					}else{
						break; // si tratta di una ricevuta alla richiesta o risposta asincrona simmetrica in un caso di newConnectionForResponse
					}
				}else{
					break; // non si tratta di una risposta asincrona simmetrica, ma di una richiesa
				}
			}else if(org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.ASINCRONO_ASIMMETRICO.equals(bustaRichiesta.getProfiloDiCollaborazione())) {
				if( (bustaRichiesta.getRiferimentoMessaggio()!=null) || 
						( bustaRichiesta.getCollaborazione()!=null && (bustaRichiesta.getCollaborazione().equals(bustaRichiesta.getID())==false) ) 
				){

					if( ( newConnectionForResponse==false ) || 
							( newConnectionForResponse && (!isRicevutaRichiesta) && (!isRicevutaRisposta) ) 
					){
						String idRicevutaRichiesta = bustaRichiesta.getRiferimentoMessaggio();
						if(idRicevutaRichiesta==null){
							// LineeGuida non sara' presente il riferimentoMessaggio ma l'elemento collaborazione
							idRicevutaRichiesta = bustaRichiesta.getCollaborazione();
						}
						GestoreMessaggi checkRicevutaRichiesta = new GestoreMessaggi(openspcoopstate, false, idRicevutaRichiesta,Costanti.OUTBOX,msgDiag,pddContext);
						attendiTerminazioneRicevutaRichiesta =  checkRicevutaRichiesta.existsMessageInProcessamentoByReference();
						if(!attendiTerminazioneRicevutaRichiesta)
							break;
						/*else{
							String proprietario = checkRicevutaRichiesta.getProprietario(RicezioneBuste.ID_MODULO);
							msgDiag.infoOpenSPCoop("RICHIESTA  ID["+bustaRichiesta.getRiferimentoMessaggio()+"] OUTBOX attendiTerminazioneRichiesta["+attendiTerminazioneRicevutaRichiesta+"] PROPRIETARIO["+proprietario+"]");
						}*/
					}else{
						break; // si tratta di una ricevuta alla richiesta o risposta asincrona asimmetrica in un caso di newConnectionForResponse
					}
				}else{
					break; // non si tratta di una richiesta-stato asincrona asimmetrica, ma di una richiesta
				}
			}else{
				break;
			}

			if(bustaRichiesta.getProfiloDiCollaborazione()!=null)
				msgDiag.mediumDebug("Busta di risposta con profilo ["+bustaRichiesta.getProfiloDiCollaborazione().getEngineValue()+"] non gestibile, si attende il completamento" +
				" della gestione della ricevuta alla richiesta");
			else{
				msgDiag.mediumDebug("Busta di risposta con profilo null??? non gestibile, si attende il completamento" +
						" della gestione della ricevuta alla richiesta");
			}

			Utilities.sleep(checkInterval);
		}

		return attendiTerminazioneRicevutaRichiesta;
	}


	private OpenSPCoop2Message generaRisposta_msgGiaRicevuto(boolean printMsg,Busta bustaRichiesta,Integrazione integrazione,MsgDiagnostico msgDiag,
			OpenSPCoopState openspcoopstate,Logger log,ConfigurazionePdDManager config,OpenSPCoop2Properties properties, String profiloGestione,
			RuoloBusta ruoloBustaRicevuta,String implementazionePdDMittente,IProtocolFactory<?> protocolFactory,
			IDSoggetto identitaPdD,String idTransazione,Loader loader, boolean oneWayVersione11,
			String implementazionePdDSoggettoMittente,
			Tracciamento tracciamento,MessageSecurityContext messageSecurityContext,
			String idCorrelazioneApplicativa,
			PdDContext pddContext, IntegrationFunctionError integrationFunctionError) throws ProtocolException, TracciamentoException{

		RepositoryBuste repositoryBuste = new RepositoryBuste(openspcoopstate.getStatoRichiesta(), true,protocolFactory);
		History historyBuste = new History(openspcoopstate.getStatoRichiesta(), log);
		Busta bustaHTTPReply = null;
		
		IProtocolVersionManager protocolManager = 
				ProtocolFactoryManager.getInstance().getProtocolFactoryByName((String) this.msgContext.getPddContext().getObject(org.openspcoop2.core.constants.Costanti.PROTOCOL_NAME)).createProtocolVersionManager(profiloGestione);
		
		RequestInfo requestInfo = (RequestInfo) this.msgContext.getPddContext().getObject(org.openspcoop2.core.constants.Costanti.REQUEST_INFO);
		
		boolean consegnaAffidabile = false;
		switch (protocolManager.getConsegnaAffidabile(bustaRichiesta)) {
		case ABILITATA:
			consegnaAffidabile = true;
			break;
		case DISABILITATA:
			consegnaAffidabile = false;
			break;
		default:
			consegnaAffidabile = properties.isGestioneRiscontri(implementazionePdDMittente) && bustaRichiesta.isConfermaRicezione();
			break;
		}
		
		// Aggiorno duplicati
		try{
			IFiltroDuplicati gestoreFiltroDuplicati = Sbustamento.getGestoreFiltroDuplicati(properties, loader, 
					openspcoopstate, this.msgContext.getPddContext(), historyBuste, repositoryBuste, oneWayVersione11);
			gestoreFiltroDuplicati.isDuplicata(protocolFactory, bustaRichiesta.getID()); // lo invoco lo stesso per eventuali implementazioni che utilzzano il worflow
			// Aggiorno duplicati
			if(printMsg){
				msgDiag.logPersonalizzato(MsgDiagnosticiProperties.MSG_DIAG_SBUSTAMENTO,"ricezioneBustaDuplicata.count");
			}
			gestoreFiltroDuplicati.incrementaNumeroDuplicati(protocolFactory,bustaRichiesta.getID());
			openspcoopstate.commit();
		}catch(Exception e){
			log.error("Aggiornamento numero duplicati per busta ["+bustaRichiesta.getID()+"] non riuscito: "+e.getMessage(),e);
		}
		if(printMsg){
			msgDiag.logPersonalizzato(MsgDiagnosticiProperties.MSG_DIAG_SBUSTAMENTO,"ricezioneBustaDuplicata");
		}
		
		Imbustamento imbustatore = new Imbustamento(log, protocolFactory, openspcoopstate.getStatoRichiesta());
		
		/* 
		 * 1) duplicato in caso di oneWay: se confermaRicezione=true e la gestione dei riscontri e' attiva, re-invio un riscontro
		 * 1b) duplicato in caso di oneWay: se confermaRicezione=false o cmq la gestione dei riscontri non è attiva, genero Errore se indicato da file property, altrimenti ritorno http 202
		 * 2) duplicati in caso sincrono: genero un msg Errore
		 * 3) duplicati in caso asincrono: rigenero la ricevuta
		 */
		
		boolean http200 = true;
		
		// 1) 
		if( org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.ONEWAY.equals(bustaRichiesta.getProfiloDiCollaborazione()) &&
				consegnaAffidabile){
			msgDiag.logPersonalizzato("generazioneRiscontro");

			// Costruisco riscontro da re-inviare
			TipoOraRegistrazione tipoOraRegistrazione = properties.getTipoTempoBusta(implementazionePdDMittente);
			bustaHTTPReply = bustaRichiesta.invertiBusta(tipoOraRegistrazione,protocolFactory.createTraduttore().toString(tipoOraRegistrazione));

			String id_busta_risposta = 
					imbustatore.buildID(identitaPdD, idTransazione, 
							properties.getGestioneSerializableDB_AttesaAttiva(),
							properties.getGestioneSerializableDB_CheckInterval(),
							RuoloMessaggio.RISPOSTA);
			bustaHTTPReply.setID(id_busta_risposta);
			
			Riscontro r = new Riscontro();
			r.setID(bustaRichiesta.getID());
			r.setOraRegistrazione(DateManager.getDate());
			r.setTipoOraRegistrazione(properties.getTipoTempoBusta(implementazionePdDMittente));
			bustaHTTPReply.addRiscontro(r);

		}
		// 1b) 
		else if( org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.ONEWAY.equals(bustaRichiesta.getProfiloDiCollaborazione()) &&
				( (properties.isGestioneRiscontri(implementazionePdDMittente)==false) || (bustaRichiesta.isConfermaRicezione()==false) )  && 
				( protocolManager.isGenerazioneErroreMessaggioOnewayDuplicato() || properties.isGenerazioneErroreProtocolloFiltroDuplicati(implementazionePdDMittente))){
			http200 = false;
			
			String id_busta_risposta = 
					imbustatore.buildID(identitaPdD, idTransazione, 
							properties.getGestioneSerializableDB_AttesaAttiva(),
							properties.getGestioneSerializableDB_CheckInterval(),
							RuoloMessaggio.RISPOSTA);
			List<Eccezione> v = new ArrayList<Eccezione>();
			v.add(Eccezione.getEccezioneValidazione(ErroriCooperazione.IDENTIFICATIVO_MESSAGGIO_GIA_PROCESSATO.getErroreCooperazione(),protocolFactory));
			bustaHTTPReply = this.generatoreErrore.getImbustamentoErrore().buildMessaggioErroreProtocollo_Validazione(v,bustaRichiesta,id_busta_risposta,
					properties.getTipoTempoBusta(implementazionePdDSoggettoMittente));	
			if( !( identitaPdD.getNome().equals(bustaHTTPReply.getMittente()) && 
					identitaPdD.getTipo().equals(bustaHTTPReply.getTipoMittente()) ) ){
				// Il mittente della busta che sara' spedita e' il router
				bustaHTTPReply.setMittente(identitaPdD.getNome());
				bustaHTTPReply.setTipoMittente(identitaPdD.getTipo());
				bustaHTTPReply.setIdentificativoPortaMittente(identitaPdD.getCodicePorta());
				bustaHTTPReply.setIndirizzoMittente(null);
			}		
			
		}
		// 2)
		//else if(org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.SINCRONO.equals(bustaRichiesta.getProfiloDiCollaborazione())){
		// non ci si entra mai in questo caso. Il metodo non viene mai chiamato nel caso di sincrono
		//}
		
		// 3
		else if(org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.ASINCRONO_SIMMETRICO.equals(bustaRichiesta.getProfiloDiCollaborazione()) ||
				org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.ASINCRONO_ASIMMETRICO.equals(bustaRichiesta.getProfiloDiCollaborazione())){

			String ricevuta = null;
			if(org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.ASINCRONO_SIMMETRICO.equals(bustaRichiesta.getProfiloDiCollaborazione())){
				// Asincrono Simmetrico
				if(bustaRichiesta.getRiferimentoMessaggio()==null)
					ricevuta = "ricevuta di una richiesta asincrona simmetrica";
				else if(ruoloBustaRicevuta!=null && RuoloBusta.RISPOSTA.equals(ruoloBustaRicevuta.toString()) ){
					ricevuta = "ricevuta di una risposta asincrona simmetrica";
				}
			}else{
				//	Asincrono Asimmetrico
				if(bustaRichiesta.getRiferimentoMessaggio()==null){
					ricevuta = "ricevuta di una richiesta asincrona asimmetrica";
					// TODO ServizioCorrelato da impostare nella ricevuta asincrona
				}else if(ruoloBustaRicevuta!=null && RuoloBusta.RISPOSTA.equals(ruoloBustaRicevuta.toString()) ){
					ricevuta = "ricevuta di una risposta asincrona asimmetrica";
				}
			}

			if(ricevuta!=null){
				msgDiag.addKeyword(CostantiPdD.KEY_TIPO_RICEVUTA_ASINCRONA, ricevuta);
				msgDiag.logPersonalizzato("generazioneRicevutaAsincrona");

				// Costruisco ricevuta da re-inviare
				TipoOraRegistrazione tipoOraRegistrazione = properties.getTipoTempoBusta(implementazionePdDMittente);
				bustaHTTPReply = bustaRichiesta.invertiBusta(tipoOraRegistrazione,protocolFactory.createTraduttore().toString(tipoOraRegistrazione));
				bustaHTTPReply.setVersioneServizio(bustaRichiesta.getVersioneServizio());
				bustaHTTPReply.setServizio(bustaRichiesta.getServizio());
				bustaHTTPReply.setTipoServizio(bustaRichiesta.getTipoServizio());
				bustaHTTPReply.setAzione(bustaRichiesta.getAzione());
				bustaHTTPReply.setProfiloDiCollaborazione(org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.ASINCRONO_SIMMETRICO);
				bustaHTTPReply.setRiferimentoMessaggio(bustaRichiesta.getID());
				// (per gli asincroni devono sempre essere presenti)
				if( properties.isGestioneElementoCollaborazione(implementazionePdDMittente))
					bustaHTTPReply.setCollaborazione(bustaRichiesta.getCollaborazione());		
				
				String id_busta_risposta = 
						imbustatore.buildID(identitaPdD, idTransazione, 
								properties.getGestioneSerializableDB_AttesaAttiva(),
								properties.getGestioneSerializableDB_CheckInterval(),
								RuoloMessaggio.RISPOSTA);
				bustaHTTPReply.setID(id_busta_risposta);
			}
		}
		// 4
		else{
			if( protocolManager.isGenerazioneErroreMessaggioOnewayDuplicato() || properties.isGenerazioneErroreProtocolloFiltroDuplicati(implementazionePdDMittente)){
				http200 = false;
				
				String id_busta_risposta = 
						imbustatore.buildID(identitaPdD, idTransazione, 
								properties.getGestioneSerializableDB_AttesaAttiva(),
								properties.getGestioneSerializableDB_CheckInterval(),
								RuoloMessaggio.RISPOSTA);
				List<Eccezione> v = new ArrayList<Eccezione>();
				v.add(Eccezione.getEccezioneValidazione(ErroriCooperazione.IDENTIFICATIVO_MESSAGGIO_GIA_PROCESSATO.getErroreCooperazione(),protocolFactory));
				bustaHTTPReply = this.generatoreErrore.getImbustamentoErrore().buildMessaggioErroreProtocollo_Validazione(v,bustaRichiesta,id_busta_risposta,
						properties.getTipoTempoBusta(implementazionePdDSoggettoMittente));	
				if( !( identitaPdD.getNome().equals(bustaHTTPReply.getMittente()) && 
						identitaPdD.getTipo().equals(bustaHTTPReply.getTipoMittente()) ) ){
					// Il mittente della busta che sara' spedita e' il router
					bustaHTTPReply.setMittente(identitaPdD.getNome());
					bustaHTTPReply.setTipoMittente(identitaPdD.getTipo());
					bustaHTTPReply.setIdentificativoPortaMittente(identitaPdD.getCodicePorta());
					bustaHTTPReply.setIndirizzoMittente(null);
				}		
			}
		}

		if(bustaHTTPReply==null){
			return MessageUtilities.buildEmptyMessage(OpenSPCoop2MessageFactory.getDefaultMessageFactory(),
					requestInfo.getProtocolRequestMessageType(), MessageRole.RESPONSE);
		}else{
						
			OpenSPCoop2Message msg = null;
			if(http200){
				msg = MessageUtilities.buildEmptyMessage(OpenSPCoop2MessageFactory.getDefaultMessageFactory(),
						requestInfo.getProtocolRequestMessageType(), MessageRole.RESPONSE);
			}
			else{
				msg = this.generatoreErrore.buildErroreIntestazione(pddContext,integrationFunctionError);
			}
			
			
			ProtocolMessage protocolMessage = imbustatore.imbustamentoRisposta(msg,pddContext,
					bustaHTTPReply,bustaRichiesta,
					integrazione,false,false,null,
					FaseImbustamento.PRIMA_SICUREZZA_MESSAGGIO);
			if(protocolMessage!=null && !protocolMessage.isPhaseUnsupported()) {
				msg = protocolMessage.getMessage(); // updated
			}
			
			protocolMessage = imbustatore.imbustamentoRisposta(msg,pddContext,
					bustaHTTPReply,bustaRichiesta,
					integrazione,false,false,null,
					FaseImbustamento.DOPO_SICUREZZA_MESSAGGIO);
			if(protocolMessage!=null && !protocolMessage.isPhaseUnsupported()) {
				msg = protocolMessage.getMessage(); // updated
			}
			
			
			//			Tracciamento Busta Ritornata: cambiata nel metodo msgErroreProcessamento
			if(this.msgContext.isTracciamentoAbilitato()){
				EsitoElaborazioneMessaggioTracciato esitoTraccia = 
						EsitoElaborazioneMessaggioTracciato.getEsitoElaborazioneMessaggioInviato();
				SecurityInfo securityInfoResponse  = null;
//				boolean functionAsRouter = false; // In questo caso dovrebbe essere sempre false?
//				if(functionAsRouter){
//					if(messageSecurityContext!=null && messageSecurityContext.getDigestReader()!=null){
//						IValidazioneSemantica validazioneSemantica = protocolFactory.createValidazioneSemantica();
//						securityInfoResponse = validazioneSemantica.readSecurityInformation(messageSecurityContext.getDigestReader(),msg);
//					}
//				}
				Validatore v = new Validatore(msg,this.msgContext.getPddContext(),openspcoopstate.getStatoRichiesta(),
						log, protocolFactory);
				tracciamento.registraRisposta(msg,securityInfoResponse,
						v.getHeaderProtocollo_senzaControlli(), bustaHTTPReply,esitoTraccia,
						Tracciamento.createLocationString(false,this.msgContext.getSourceLocation()),
						idCorrelazioneApplicativa,
						null);
			}
			
			return msg;
		}
	}

	private EsitoLib chiamaLibreria(OpenSPCoopStateless openspcoopstate, Logger log) throws OpenSPCoopStateException, GenericLibException{

		/* --------------------------- SBUSTAMENTO ------------------------ */
		if (openspcoopstate.getDestinatarioRequestMsgLib().startsWith(Sbustamento.ID_MODULO)) {
			Sbustamento lib = new Sbustamento(log);
			return lib.onMessage(openspcoopstate);	
		}

		/* ---------------------------ConsegnaContenutiApplicativi----------------------------- */
		else if (openspcoopstate.getDestinatarioRequestMsgLib().startsWith(ConsegnaContenutiApplicativi.ID_MODULO)) {
			ConsegnaContenutiApplicativi lib = new ConsegnaContenutiApplicativi(log);
			EsitoLib result = lib.onMessage(openspcoopstate);
			if (result.getStatoInvocazione()==EsitoLib.OK) openspcoopstate.setDestinatarioRequestMsgLib("");
			return result;
		}

		/* ----------------------        -ImbustamentoRisposte         ------------------------------- */
		else if (openspcoopstate.getDestinatarioResponseMsgLib().startsWith(ImbustamentoRisposte.ID_MODULO)) {
			ImbustamentoRisposte lib = new ImbustamentoRisposte(log);
			return lib.onMessage(openspcoopstate);
		}

		/* ---------------------       -InoltroRisposte----------------------------- */
		else if (openspcoopstate.getDestinatarioResponseMsgLib().startsWith(InoltroRisposte.ID_MODULO)) {
			InoltroRisposte lib = new InoltroRisposte(log);
			return lib.onMessage(openspcoopstate);
		}

		else throw new OpenSPCoopStateException(RicezioneBuste.ID_MODULO + ".chiamaLibreria: nome libreria non valido");

	}
	
	
	private IDServizio getIdServizioPerAutorizzazione(IDServizio idServizio,IDSoggetto soggettoFruitore,
			boolean functionAsRouter,Busta bustaRichiesta,RuoloBusta ruoloBustaRicevuta) throws Exception{


		IDSoggetto soggettoDestinatarioPerAutorizzazione = null;
		if(functionAsRouter){
			soggettoDestinatarioPerAutorizzazione = new IDSoggetto(idServizio.getSoggettoErogatore().getTipo(),idServizio.getSoggettoErogatore().getNome(),
					idServizio.getSoggettoErogatore().getCodicePorta());
		}
		else if(org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.ONEWAY.equals(bustaRichiesta.getProfiloDiCollaborazione()) ||
				org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.SINCRONO.equals(bustaRichiesta.getProfiloDiCollaborazione())){
			soggettoDestinatarioPerAutorizzazione = new IDSoggetto(idServizio.getSoggettoErogatore().getTipo(),idServizio.getSoggettoErogatore().getNome(),
					idServizio.getSoggettoErogatore().getCodicePorta());
		}else if (org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.ASINCRONO_SIMMETRICO.equals(bustaRichiesta.getProfiloDiCollaborazione()) ){
			if(ruoloBustaRicevuta==null){
				// router
				soggettoDestinatarioPerAutorizzazione = new IDSoggetto(idServizio.getSoggettoErogatore().getTipo(),idServizio.getSoggettoErogatore().getNome(),
						idServizio.getSoggettoErogatore().getCodicePorta());
			}
			else{
				if(RuoloBusta.RICHIESTA.equals(ruoloBustaRicevuta.toString()) || RuoloBusta.RISPOSTA.equals(ruoloBustaRicevuta.toString())){
					soggettoDestinatarioPerAutorizzazione = new IDSoggetto(idServizio.getSoggettoErogatore().getTipo(),idServizio.getSoggettoErogatore().getNome(),
							idServizio.getSoggettoErogatore().getCodicePorta());
				}else{
					if(soggettoFruitore==null){
						throw new Exception("Soggetto fruitore non identificato");
					}
					soggettoDestinatarioPerAutorizzazione = new IDSoggetto(soggettoFruitore.getTipo(),soggettoFruitore.getNome(),
							soggettoFruitore.getCodicePorta());
				}	
			}
		}else if (org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.ASINCRONO_ASIMMETRICO.equals(bustaRichiesta.getProfiloDiCollaborazione()) ){
			if(ruoloBustaRicevuta==null){
				// router
				soggettoDestinatarioPerAutorizzazione = new IDSoggetto(idServizio.getSoggettoErogatore().getTipo(),idServizio.getSoggettoErogatore().getNome(),
						idServizio.getSoggettoErogatore().getCodicePorta());
			}
			else{
				if(RuoloBusta.RICHIESTA.equals(ruoloBustaRicevuta.toString()) || RuoloBusta.RISPOSTA.equals(ruoloBustaRicevuta.toString())){
					soggettoDestinatarioPerAutorizzazione = new IDSoggetto(idServizio.getSoggettoErogatore().getTipo(),idServizio.getSoggettoErogatore().getNome(),
							idServizio.getSoggettoErogatore().getCodicePorta());
				}else{
					if(soggettoFruitore==null){
						throw new Exception("Soggetto fruitore non identificato");
					}
					soggettoDestinatarioPerAutorizzazione = new IDSoggetto(soggettoFruitore.getTipo(),soggettoFruitore.getNome(),
							soggettoFruitore.getCodicePorta());
				}
			}
		}else{
			soggettoDestinatarioPerAutorizzazione = new IDSoggetto(idServizio.getSoggettoErogatore().getTipo(),idServizio.getSoggettoErogatore().getNome(),
					idServizio.getSoggettoErogatore().getCodicePorta());
		}

		IDServizio idServizioPerAutorizzazione = IDServizioFactory.getInstance().
				getIDServizioFromValues(idServizio.getTipo(), idServizio.getNome(), soggettoDestinatarioPerAutorizzazione, idServizio.getVersione());
		idServizioPerAutorizzazione.setUriAccordoServizioParteComune(idServizio.getUriAccordoServizioParteComune());
		idServizioPerAutorizzazione.setAzione(idServizio.getAzione());
		idServizioPerAutorizzazione.setTipologia(idServizio.getTipologia());
		
		return idServizioPerAutorizzazione;
	}
	
	private IDSoggetto getIDSoggettoMittentePerAutorizzazione(IDServizio idServizio,IDSoggetto soggettoFruitore,
			boolean functionAsRouter,Busta bustaRichiesta,RuoloBusta ruoloBustaRicevuta, boolean supportatoAutenticazioneSoggetti) throws Exception{
		IDSoggetto soggettoMittentePerAutorizzazione = null;
		if(functionAsRouter){
			if(soggettoFruitore==null){
				throw new Exception("Soggetto fruitore non identificato");
			}
			soggettoMittentePerAutorizzazione = new IDSoggetto(soggettoFruitore.getTipo(),soggettoFruitore.getNome(),
					soggettoFruitore.getCodicePorta());
		}
		else if(org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.ONEWAY.equals(bustaRichiesta.getProfiloDiCollaborazione()) ||
				org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.SINCRONO.equals(bustaRichiesta.getProfiloDiCollaborazione())){
			if(soggettoFruitore==null){
				if(supportatoAutenticazioneSoggetti){
					return null; // invocazione anonima
				}else{
					throw new Exception("Soggetto fruitore non identificato");		
				}
			}
			soggettoMittentePerAutorizzazione = new IDSoggetto(soggettoFruitore.getTipo(),soggettoFruitore.getNome(),
					soggettoFruitore.getCodicePorta());
		}else if (org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.ASINCRONO_SIMMETRICO.equals(bustaRichiesta.getProfiloDiCollaborazione()) ){
			if(ruoloBustaRicevuta==null){
				if(soggettoFruitore==null){
					if(supportatoAutenticazioneSoggetti){
						return null; // invocazione anonima
					}else{
						throw new Exception("Soggetto fruitore non identificato");		
					}
				}
				soggettoMittentePerAutorizzazione = new IDSoggetto(soggettoFruitore.getTipo(),soggettoFruitore.getNome(),
						soggettoFruitore.getCodicePorta());
			}else{
				if(RuoloBusta.RICHIESTA.equals(ruoloBustaRicevuta.toString()) || RuoloBusta.RISPOSTA.equals(ruoloBustaRicevuta.toString())){
					if(soggettoFruitore==null){
						if(supportatoAutenticazioneSoggetti){
							return null; // invocazione anonima
						}else{
							throw new Exception("Soggetto fruitore non identificato");		
						}
					}
					soggettoMittentePerAutorizzazione = new IDSoggetto(soggettoFruitore.getTipo(),soggettoFruitore.getNome(),
							soggettoFruitore.getCodicePorta());
				}else{
					soggettoMittentePerAutorizzazione = new IDSoggetto(idServizio.getSoggettoErogatore().getTipo(),idServizio.getSoggettoErogatore().getNome(),
							idServizio.getSoggettoErogatore().getCodicePorta());
				}		
			}
		}else if (org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.ASINCRONO_ASIMMETRICO.equals(bustaRichiesta.getProfiloDiCollaborazione()) ){
			if(ruoloBustaRicevuta==null){
				if(soggettoFruitore==null){
					if(supportatoAutenticazioneSoggetti){
						return null; // invocazione anonima
					}else{
						throw new Exception("Soggetto fruitore non identificato");		
					}
				}
				soggettoMittentePerAutorizzazione = new IDSoggetto(soggettoFruitore.getTipo(),soggettoFruitore.getNome(),
						soggettoFruitore.getCodicePorta());
			}else{
				if(RuoloBusta.RICHIESTA.equals(ruoloBustaRicevuta.toString()) || RuoloBusta.RISPOSTA.equals(ruoloBustaRicevuta.toString())){
					if(soggettoFruitore==null){
						if(supportatoAutenticazioneSoggetti){
							return null; // invocazione anonima
						}else{
							throw new Exception("Soggetto fruitore non identificato");		
						}
					}
					soggettoMittentePerAutorizzazione = new IDSoggetto(soggettoFruitore.getTipo(),soggettoFruitore.getNome(),
							soggettoFruitore.getCodicePorta());
				}else{
					soggettoMittentePerAutorizzazione = new IDSoggetto(idServizio.getSoggettoErogatore().getTipo(),idServizio.getSoggettoErogatore().getNome(),
							idServizio.getSoggettoErogatore().getCodicePorta());
				}
			}
		}else{
			if(soggettoFruitore==null){
				if(supportatoAutenticazioneSoggetti){
					return null; // invocazione anonima
				}else{
					throw new Exception("Soggetto fruitore non identificato");		
				}
			}
			soggettoMittentePerAutorizzazione = new IDSoggetto(soggettoFruitore.getTipo(),soggettoFruitore.getNome(),
					soggettoFruitore.getCodicePorta());
		}
		return soggettoMittentePerAutorizzazione;
	}
	
	private PortaApplicativa getPortaApplicativa(ConfigurazionePdDManager configurazionePdDReader, IDServizio idServizio) throws Exception{
		List<PortaApplicativa> listPa = configurazionePdDReader.getPorteApplicative(idServizio, false);
		if(listPa.size()<=0){
			throw new Exception("Non esiste alcuna porta applicativa indirizzabile tramite il servizio ["+idServizio+"]");
		}
		else{
			if(listPa.size()>1)
				throw new Exception("Esiste più di una porta applicativa indirizzabile tramite il servizio ["+idServizio+"]");
			return listPa.get(0);
		}
	}
	
	@SuppressWarnings("deprecation")
	private void overwriteIdSoggetto(IDServizio idServizio, IDSoggetto idSoggetto){
		idServizio.setSoggettoErogatore(idSoggetto);
	}
	@SuppressWarnings("deprecation")
	private void cleanDatiServizio(IDServizio idServizio){
		idServizio.setVersione(null);
		idServizio.setNome(null);
		idServizio.setTipo(null);
	}
}


