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


package org.openspcoop2.pdd.services;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import javax.xml.soap.AttachmentPart;
import javax.xml.soap.SOAPEnvelope;

import org.slf4j.Logger;
import org.openspcoop2.core.api.constants.CostantiApi;
import org.openspcoop2.core.config.Connettore;
import org.openspcoop2.core.config.CorrelazioneApplicativa;
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.config.TipoFiltroAbilitazioneServizi;
import org.openspcoop2.core.config.ValidazioneContenutiApplicativi;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import org.openspcoop2.core.config.driver.DriverConfigurazioneNotFound;
import org.openspcoop2.core.constants.CostantiConnettori;
import org.openspcoop2.core.constants.TipoPdD;
import org.openspcoop2.core.id.IDPortaApplicativaByNome;
import org.openspcoop2.core.id.IDPortaDelegata;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziAzioneNotFound;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziCorrelatoNotFound;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziNotFound;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziPortTypeNotFound;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.OpenSPCoop2MessageFactory;
import org.openspcoop2.message.SOAPVersion;
import org.openspcoop2.message.SoapUtils;
import org.openspcoop2.message.mtom.MtomXomReference;
import org.openspcoop2.pdd.config.ClassNameProperties;
import org.openspcoop2.pdd.config.ConfigurazionePdDManager;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.config.PDIdentificazioneDinamica;
import org.openspcoop2.pdd.config.PDIdentificazioneDinamicaException;
import org.openspcoop2.pdd.config.RichiestaApplicativa;
import org.openspcoop2.pdd.config.RichiestaDelegata;
import org.openspcoop2.pdd.core.AbstractCore;
import org.openspcoop2.pdd.core.CostantiPdD;
import org.openspcoop2.pdd.core.GestoreCorrelazioneApplicativa;
import org.openspcoop2.pdd.core.GestoreMessaggi;
import org.openspcoop2.pdd.core.GestoreMessaggiException;
import org.openspcoop2.pdd.core.IdentificazionePortaDelegata;
import org.openspcoop2.pdd.core.IntegrationContext;
import org.openspcoop2.pdd.core.LocalForwardEngine;
import org.openspcoop2.pdd.core.LocalForwardParameter;
import org.openspcoop2.pdd.core.PdDContext;
import org.openspcoop2.pdd.core.ProtocolContext;
import org.openspcoop2.pdd.core.StatoServiziPdD;
import org.openspcoop2.pdd.core.ValidatoreMessaggiApplicativi;
import org.openspcoop2.pdd.core.ValidatoreMessaggiApplicativiException;
import org.openspcoop2.pdd.core.autenticazione.Credenziali;
import org.openspcoop2.pdd.core.autenticazione.GestoreCredenzialiConfigurationException;
import org.openspcoop2.pdd.core.autenticazione.IAutenticazione;
import org.openspcoop2.pdd.core.autenticazione.IGestoreCredenziali;
import org.openspcoop2.pdd.core.autorizzazione.GestoreAutorizzazione;
import org.openspcoop2.pdd.core.autorizzazione.pd.DatiInvocazionePortaDelegata;
import org.openspcoop2.pdd.core.autorizzazione.pd.EsitoAutorizzazioneIntegrazione;
import org.openspcoop2.pdd.core.autorizzazione.pd.IAutorizzazioneContenutoPortaDelegata;
import org.openspcoop2.pdd.core.connettori.InfoConnettoreIngresso;
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
import org.openspcoop2.pdd.core.state.IOpenSPCoopState;
import org.openspcoop2.pdd.core.state.OpenSPCoopState;
import org.openspcoop2.pdd.core.state.OpenSPCoopStateful;
import org.openspcoop2.pdd.core.state.OpenSPCoopStateless;
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
import org.openspcoop2.pdd.services.connector.ConnectorInMessage;
import org.openspcoop2.pdd.services.skeleton.IntegrationManager;
import org.openspcoop2.pdd.timers.TimerGestoreMessaggi;
import org.openspcoop2.pdd.timers.TimerMonitoraggioRisorse;
import org.openspcoop2.pdd.timers.TimerThreshold;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.engine.URLProtocolContext;
import org.openspcoop2.protocol.engine.builder.ErroreApplicativoBuilder;
import org.openspcoop2.protocol.engine.builder.Imbustamento;
import org.openspcoop2.protocol.engine.constants.Costanti;
import org.openspcoop2.protocol.engine.driver.RepositoryBuste;
import org.openspcoop2.protocol.engine.validator.ValidazioneSintattica;
import org.openspcoop2.protocol.registry.RegistroServiziManager;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.Integrazione;
import org.openspcoop2.protocol.sdk.ProtocolException;
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
import org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione;
import org.openspcoop2.protocol.sdk.constants.StatoFunzionalitaProtocollo;
import org.openspcoop2.protocol.sdk.tracciamento.TracciamentoException;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.resources.Loader;

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
	
	/** Indicazione se il servizio PD risulta attivo */
	public static Boolean isActivePDService = true;
	public static List<TipoFiltroAbilitazioneServizi> listaAbilitazioniPDService = null;
	public static List<TipoFiltroAbilitazioneServizi> listaDisabilitazioniPDService = null;

	/** INodeSender */
	//private static INodeSender nodeSender = null;
	// E' stato aggiunto lo stato dentro l'oggetto.

	/** INodeReceiver */
	//private static INodeReceiver nodeReceiver = null;
	// E' stato aggiunto lo stato dentro l'oggetto.

	/** IGestoreIntegrazionePD: lista di gestori, ordinati per priorita' minore */
	//private static java.util.Hashtable<String, IGestoreIntegrazionePD> gestoriIntegrazionePD = new java.util.Hashtable<String, IGestoreIntegrazionePD>();
	// E' stato aggiunto lo stato dentro l'oggetto.
	private static String[] defaultGestoriIntegrazionePD = null;
	private static Hashtable<String, String[]> defaultPerProtocolloGestoreIntegrazionePD = null;

	/** IAutorizzazione: lista di gestori per l'autorizzazione */
	//private static java.util.Hashtable<String, IAutorizzazione> gestoriAutorizzazione = new java.util.Hashtable<String, IAutorizzazione>();
	// E' stato aggiunto lo stato dentro l'oggetto.
	
	/** IAutorizzazione: lista di gestori per l'autorizzazione per contenuto */
	//private static java.util.Hashtable<String, IAutorizzazioneContenuto> gestoriAutorizzazioneContenuto = new java.util.Hashtable<String, IAutorizzazioneContenuto>();
	// E' stato aggiunto lo stato dentro l'oggetto.

	/** IGestoreCredenziali: lista di gestori delle credenziali */
	//private static java.util.Hashtable<String, IGestoreCredenziali> gestoriCredenziali = new java.util.Hashtable<String, IGestoreCredenziali>();
	// E' stato aggiunto lo stato dentro l'oggetto.
	private static String [] tipiGestoriCredenziali = null;
	
	/**
	 * Inizializzatore del servizio RicezioneContenutiApplicativi
	 * 
	 * @throws Exception
	 */
	protected synchronized static void initializeService(
			ConfigurazionePdDManager configReader,
			ClassNameProperties className,
			OpenSPCoop2Properties propertiesReader, Logger logCore)
			throws Exception {
		if (RicezioneContenutiApplicativi.initializeService)
			return; // inizializzato da un altro thread

		Loader loader = Loader.getInstance();
		
		// Inizializzo stato del servizio PA
		RicezioneContenutiApplicativi.isActivePDService = configReader.isPDServiceActive();
		RicezioneContenutiApplicativi.listaAbilitazioniPDService = configReader.getFiltriAbilitazionePDService();
		RicezioneContenutiApplicativi.listaDisabilitazioniPDService = configReader.getFiltriDisabilitazionePDService();
		
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
		RicezioneContenutiApplicativi.defaultPerProtocolloGestoreIntegrazionePD = new Hashtable<String, String[]>();
		Enumeration<String> enumProtocols = ProtocolFactoryManager.getInstance().getProtocolNames();
		while (enumProtocols.hasMoreElements()) {
			String protocol = (String) enumProtocols.nextElement();
			String[] tipiIntegrazionePD = propertiesReader.getTipoIntegrazionePD(protocol);
			if(tipiIntegrazionePD!=null && tipiIntegrazionePD.length>0){
				Vector<String> tipiIntegrazionePerProtocollo = new Vector<String>();
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

	/**
	 * Aggiorna la lista dei GestoreIntegrazionePD
	 * 
	 * @throws Exception
	 */
// E' stato aggiunto lo stato dentro l'oggetto.
//	private synchronized static void aggiornaListaGestoreIntegrazione(
//			String newTipo, ClassNameProperties className,
//			OpenSPCoop2Properties propertiesReader, Logger logCore)
//			throws Exception {
//		if (RicezioneContenutiApplicativi.gestoriIntegrazionePD.contains(newTipo))
//			return; // inizializzato da un altro thread
//
//		Loader loader = Loader.getInstance();
//		
//		// Inizializzo IGestoreIntegrazionePD new Type
//		String classType = className.getIntegrazionePortaDelegata(newTipo);
//		try {
//			RicezioneContenutiApplicativi.gestoriIntegrazionePD.put(newTipo,((IGestoreIntegrazionePD) loader.newInstance(classType)));
//			logCore.info("Inizializzazione gestore integrazione servizioApplicativo->PdD di tipo "+ newTipo + " effettuata.");
//		} catch (Exception e) {
//			throw new Exception(
//					"Riscontrato errore durante il caricamento della classe ["+ classType
//							+ "] da utilizzare per la gestione dell'integrazione di tipo ["+ newTipo + "]: " + e.getMessage());
//		}
//	}

	/**
	 * Aggiorna la lista dei Gestore per l'autorizzazione
	 * 
	 * @throws Exception
	 */
// // E' stato aggiunto lo stato dentro l'oggetto.
//	private synchronized static void aggiornaListaGestoreAutorizzazione(
//			String newTipo, ClassNameProperties className,
//			OpenSPCoop2Properties propertiesReader, Logger logCore)
//			throws Exception {
//		if (RicezioneContenutiApplicativi.gestoriAutorizzazione.contains(newTipo))
//			return; // inizializzato da un altro thread
//
//		Loader loader = Loader.getInstance();
//		
//		// Inizializzo IGestoreAutorizzazione new Type
//		String classType = className.getAutorizzazione(newTipo);
//		try {
//			RicezioneContenutiApplicativi.gestoriAutorizzazione.put(newTipo,((IAutorizzazione) loader.newInstance(classType)));
//			logCore.info("Inizializzazione gestore autorizzazione di tipo "+ newTipo + " effettuata.");
//		} catch (Exception e) {
//			throw new Exception(
//					"Riscontrato errore durante il caricamento della classe ["+ classType
//							+ "] da utilizzare per la gestione dell'autorizzazione di tipo ["+ newTipo + "]: " + e.getMessage());
//		}
//	}
	
	/**
	 * Aggiorna la lista dei Gestore per l'autorizzazione per contenuto
	 * 
	 * @throws Exception
	 */
// E' stato aggiunto lo stato dentro l'oggetto.
//	private synchronized static void aggiornaListaGestoreAutorizzazioneContenuto(
//			String newTipo, ClassNameProperties className,
//			OpenSPCoop2Properties propertiesReader, Logger logCore)
//			throws Exception {
//		if (RicezioneContenutiApplicativi.gestoriAutorizzazioneContenuto.contains(newTipo))
//			return; // inizializzato da un altro thread
//
//		Loader loader = Loader.getInstance();
//		
//		// Inizializzo IGestoreAutorizzazione new Type
//		String classType = className.getAutorizzazioneContenuto(newTipo);
//		try {
//			RicezioneContenutiApplicativi.gestoriAutorizzazioneContenuto.put(newTipo,((IAutorizzazioneContenuto) loader.newInstance(classType)));
//			logCore.info("Inizializzazione gestore autorizzazione contenuto di tipo "+ newTipo + " effettuata.");
//		} catch (Exception e) {
//			throw new Exception(
//					"Riscontrato errore durante il caricamento della classe ["+ classType
//							+ "] da utilizzare per la gestione dell'autorizzazione contenuto di tipo ["+ newTipo + "]: " + e.getMessage());
//		}
//	}

	/** Contesto della richiesta */
	private RicezioneContenutiApplicativiContext msgContext;

	/** Modalita' di gestione del Messaggio */
	boolean responseAsByte = false;

	/** Factory per la creazione di messaggi SOAP */
	private  OpenSPCoop2MessageFactory fac = OpenSPCoop2MessageFactory.getMessageFactory();
	
	public RicezioneContenutiApplicativi(
			RicezioneContenutiApplicativiContext context) {
		this.msgContext = context;
	}

	public void process(Object ... params) {
				
		
		// ------------- dati generali -----------------------------
		
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
			try {
				requestMessage = this.msgContext.getMessageRequestFromByte();
				this.responseAsByte = true;
			} catch (Exception e) {
				setSOAPFault(logCore, msgDiag, e, "LetturaMessaggioRichiesta");
				return;
			}
		}
		
		
		
		
		// ------------- in-handler -----------------------------
		IProtocolFactory protocolFactory = null;
		try{
			protocolFactory = ProtocolFactoryManager.getInstance().getProtocolFactoryByName((String)this.msgContext.getPddContext().getObject(org.openspcoop2.core.constants.Costanti.PROTOCOLLO));
		}catch(Exception e){
			setSOAPFault(logCore, msgDiag, e, "ProtocolFactoryInstance");
			return;
		}
		InRequestContext inRequestContext = new InRequestContext(logCore,protocolFactory);
		// TipoPorta
		inRequestContext.setTipoPorta(TipoPdD.DELEGATA);
		inRequestContext.setIdModulo(this.msgContext.getIdModulo());
		// Informazioni connettore ingresso
		InfoConnettoreIngresso connettore = new InfoConnettoreIngresso();
		connettore.setCredenziali(this.msgContext.getCredenziali());
		if(this.msgContext.getUrlProtocolContext()!=null){
			connettore.setUrlProtocolContext(this.msgContext.getUrlProtocolContext());
		}
		connettore.setSoapAction(this.msgContext.getSoapAction());
		connettore.setFromLocation(this.msgContext.getFromLocation());
		inRequestContext.setConnettore(connettore);
		// Data ingresso messaggio
		inRequestContext.setDataElaborazioneMessaggio(this.msgContext.getDataIngressoRichiesta());
		// PdDContext
		inRequestContext.setPddContext(this.msgContext.getPddContext());
		// Dati Messaggio
		inRequestContext.setMessaggio(requestMessage);
		// Invoke handler
		try{
			GestoreHandlers.inRequest(inRequestContext, msgDiag, logCore);
		}catch(HandlerException e){
			setSOAPFault(logCore,msgDiag, e, e.getIdentitaHandler());
			return;
		}catch(Exception e){
			setSOAPFault(logCore,msgDiag, e, "InvocazioneInRequestHandler");
			return;
		}
		
		
		
		
		
			
		// ------------- process -----------------------------
		try{
			process_engine(inRequestContext,params);
		}catch(TracciamentoException e){
			setSOAPFault(logCore,msgDiag, e, "TracciamentoNonRiuscito");
			return;
		} catch(ProtocolException e){
			setSOAPFault(logCore,msgDiag, e, "InstanziazioneProtocolFactoryNonRiuscita");
			return;
		} 
		
		try{
			if(this.msgContext.getPddContext()!=null  && this.msgContext.getIntegrazione()!=null){
				if(this.msgContext.getPddContext().containsKey(CostantiPdD.KEY_TIPO_PROCESSAMENTO_MTOM_RICHIESTA)){
					this.msgContext.getIntegrazione().setTipoProcessamentoMtomXopRichiesta(
							(String)this.msgContext.getPddContext().getObject(CostantiPdD.KEY_TIPO_PROCESSAMENTO_MTOM_RICHIESTA));
				}
				if(this.msgContext.getPddContext().containsKey(CostantiPdD.KEY_TIPO_PROCESSAMENTO_MTOM_RISPOSTA)){
					this.msgContext.getIntegrazione().setTipoProcessamentoMtomXopRisposta(
							(String)this.msgContext.getPddContext().getObject(CostantiPdD.KEY_TIPO_PROCESSAMENTO_MTOM_RISPOSTA));
				}
				if(this.msgContext.getPddContext().containsKey(CostantiPdD.KEY_TIPO_SICUREZZA_MESSAGGIO_RICHIESTA)){
					this.msgContext.getIntegrazione().setTipoMessageSecurityRichiesta(
							(String)this.msgContext.getPddContext().getObject(CostantiPdD.KEY_TIPO_SICUREZZA_MESSAGGIO_RICHIESTA));
				}
				if(this.msgContext.getPddContext().containsKey(CostantiPdD.KEY_TIPO_SICUREZZA_MESSAGGIO_RISPOSTA)){
					this.msgContext.getIntegrazione().setTipoMessageSecurityRisposta(
							(String)this.msgContext.getPddContext().getObject(CostantiPdD.KEY_TIPO_SICUREZZA_MESSAGGIO_RISPOSTA));
				}
			}
		}catch(Exception e){
			setSOAPFault(logCore,msgDiag, e, "FinalizeIntegrationContextRicezioneContenutiApplicativi");
			return;
		}
		
		
		
		
		
		// ------------- out-handler -----------------------------
		OutResponseContext outResponseContext = new OutResponseContext(logCore,protocolFactory);
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
			setSOAPFault(logCore,msgDiag, e, e.getIdentitaHandler());
			return;
		}catch(Exception e){
			setSOAPFault(logCore,msgDiag, e, "InvocazioneOutResponseHandler");
			return;
		}
		

		
		
		
		// ---------------- fine gestione ------------------------------
		OpenSPCoop2Message msgRisposta = null;
		try{
			msgRisposta = outResponseContext.getMessaggio();
			boolean rispostaPresente = true;
			OpenSPCoop2Properties properties = OpenSPCoop2Properties.getInstance(); // Puo' non essere inizializzato
			if(properties!=null){
				rispostaPresente = ServletUtils.verificaRispostaRelazioneCodiceTrasporto202(protocolFactory,OpenSPCoop2Properties.getInstance(), msgRisposta,true);
			}
			if(rispostaPresente){
				if (this.responseAsByte) {
					this.msgContext.setMessageResponseAsByte(msgRisposta);
				} else {
					this.msgContext.setMessageResponse(msgRisposta);
				}
			}else{
				this.msgContext.setMessageResponse(null);
				msgRisposta = null;
			}
		}catch(Exception e){
			setSOAPFault(logCore,msgDiag, e, "FineGestioneRicezioneContenutiApplicativi");
			return;
		}
		
		
		
		
		
		
		
		
		
		// ------------- Dump risposta in uscita-----------------------------
		if(Dump.sistemaDumpDisponibile){
			try{
				ConfigurazionePdDManager configurazionePdDReader = ConfigurazionePdDManager.getInstance();	
				if (configurazionePdDReader.dumpMessaggi() && msgRisposta!=null) {
					ProtocolContext protocolContext = this.msgContext.getProtocol();
					Dump dumpApplicativo = null;
					if(protocolContext!=null){
						dumpApplicativo = new Dump(protocolContext.getDominio(),
								this.msgContext.getIdModulo(), protocolContext.getIdRichiesta(),
								protocolContext.getFruitore(),new IDServizio(protocolContext.getErogatore(), protocolContext.getTipoServizio(), protocolContext.getServizio(), protocolContext.getAzione()),
								this.msgContext.getTipoPorta(),this.msgContext.getPddContext(),
								null,null);
					}else{
						dumpApplicativo = new Dump(null,this.msgContext.getIdModulo(),this.msgContext.getTipoPorta(),this.msgContext.getPddContext(),
								null,null);
					}
					dumpApplicativo.dumpRispostaUscita(msgRisposta, inRequestContext.getConnettore(), outResponseContext.getPropertiesRispostaTrasporto());
				}
			}catch(TracciamentoException e){
				setSOAPFault(logCore,msgDiag, e, "TracciamentoNonRiuscito");
				return;
			}catch(Exception e){
				// Se non riesco ad accedere alla configurazione sicuramente gia' nel messaggio di risposta e' presente l'errore di PdD non correttamente inizializzata
			}
		}
		
		
	}
	
	private void setSOAPFault(Logger logCore, MsgDiagnostico msgDiag, Exception e, String posizione){
		
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
		else
			logCore.error(posizione+": "+e.getMessage(),e);
		if (this.msgContext.isGestioneRisposta()) {
			String posizioneFault = posizione+": "+e.getMessage();
			OpenSPCoop2Message messageFault = this.fac.createFaultMessage(this.msgContext.getMessageRequest().getVersioneSoap(), posizioneFault);
			if (this.responseAsByte) {
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				try{
					messageFault.writeTo(out,false);
				}catch(Exception eWriteTo){
					msgDiag.logErroreGenerico(e, "GenerazioneFaultAsBytes");
				}
				this.msgContext.setMessageRequestAsByte(out.toByteArray());
			}else{
				this.msgContext.setMessageResponse(messageFault);
			}
		}
	}
	

	private void process_engine(InRequestContext inRequestContext,Object ... params) throws TracciamentoException, ProtocolException {

	
		
		/* ------------ Lettura parametri della richiesta ------------- */

		// Messaggio di ingresso
		OpenSPCoop2Message requestMessage = inRequestContext.getMessaggio();
		
		SOAPVersion versioneSoap = requestMessage.getVersioneSoap();
		
		// Logger
		Logger logCore = inRequestContext.getLogCore();
		
		// Data Ingresso Messaggio
		Timestamp dataIngressoMessaggio = new Timestamp(this.msgContext.getDataIngressoRichiesta().getTime());
		
		// ID Transazione
		String idTransazione = PdDContext.getValue(org.openspcoop2.core.constants.Costanti.CLUSTER_ID, inRequestContext.getPddContext());
		
		// Parametri della porta delegata invocata
		URLProtocolContext urlProtocolContext = this.msgContext.getUrlProtocolContext();

		// Credenziali utilizzate nella richiesta
		Credenziali credenziali = this.msgContext.getCredenziali();

		// Autenticazione/Autorizzazione registrate
		ClassNameProperties className = ClassNameProperties.getInstance();

		// PropertiesReader
		OpenSPCoop2Properties propertiesReader = OpenSPCoop2Properties.getInstance();
		if (propertiesReader == null) {
			logCore.error("Inizializzazione di OpenSPCoop non correttamente effettuata: OpenSPCoopProperties");
			if (this.msgContext.isGestioneRisposta()) {
				this.msgContext.setMessageResponse(this.fac.createFaultMessage(versioneSoap, "ErroreInizializzazioneOpenSPCoop"));
			}
			return;
		}

		// Configurazione PdD Reader
		ConfigurazionePdDManager configurazionePdDReader = ConfigurazionePdDManager.getInstance();

		// RegistroServizi Reader
		RegistroServiziManager registroServiziReader = RegistroServiziManager.getInstance();

		// Protocollo
		String protocol = (String) this.msgContext.getPddContext().getObject(org.openspcoop2.core.constants.Costanti.PROTOCOLLO);
		
		// IdentificativoPdD
		IDSoggetto identitaPdD = propertiesReader.getIdentitaPortaDefault(protocol);

		// ProtocolFactory
		IProtocolFactory protocolFactory = ProtocolFactoryManager.getInstance().getProtocolFactoryByName(protocol);
		PdDContext pddContext = inRequestContext.getPddContext();
		ITraduttore traduttore = protocolFactory.createTraduttore();
		IProtocolManager protocolManager = protocolFactory.createProtocolManager();
		
		// ProprietaErroreApplicativo
		ProprietaErroreApplicativo proprietaErroreAppl = propertiesReader
				.getProprietaGestioneErrorePD(protocolManager);
		proprietaErroreAppl.setDominio(identitaPdD.getCodicePorta());
		proprietaErroreAppl.setIdModulo(this.msgContext.getIdModulo());
		if(this.msgContext.isForceFaultAsXML()){
			proprietaErroreAppl.setFaultAsXML(true); // es. se siamo in una richiesta http senza SOAP, un SoapFault non ha senso
		}
		this.msgContext.setProprietaErroreAppl(proprietaErroreAppl);

		// ErroreApplicativoBuilder
		ErroreApplicativoBuilder erroreApplicativoBuilder = 
			new ErroreApplicativoBuilder(logCore, protocolFactory, 
					identitaPdD, null, null, this.msgContext.getIdModulo(), proprietaErroreAppl, versioneSoap,
					this.msgContext.getTipoPorta(),null);	
				
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
				this.msgContext.setMessageResponse((erroreApplicativoBuilder.toMessage(ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
						get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_501_PDD_NON_INIZIALIZZATA),null,null)));
			}
			return;
		}
		if (TimerMonitoraggioRisorse.risorseDisponibili == false) {
			String msgErrore = "Risorse di sistema non disponibili: "+ TimerMonitoraggioRisorse.risorsaNonDisponibile.getMessage();
			logCore.error("["+ RicezioneContenutiApplicativi.ID_MODULO+ "]  "+msgErrore,TimerMonitoraggioRisorse.risorsaNonDisponibile);
			try{
				// provo ad emetter un diagnostico
				if(this.msgContext.getMsgDiagnostico()!=null){
					this.msgContext.getMsgDiagnostico().logErroreGenerico(msgErrore,"InizializzazioneRisorsePdD");
				}
			}catch(Throwable t){logCore.error("Emissione diagnostico per errore inizializzazione non riuscita: "+t.getMessage(),t);}
			if (this.msgContext.isGestioneRisposta()) {
				this.msgContext.setMessageResponse((erroreApplicativoBuilder.toMessage(ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
						get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_532_RISORSE_NON_DISPONIBILI),null,null)));
			}
			return;
		}
		if (TimerThreshold.freeSpace == false) {
			String msgErrore = "Non sono disponibili abbastanza risorse per la gestione della richiesta";
			logCore.error("["+ RicezioneContenutiApplicativi.ID_MODULO+ "]  "+msgErrore);
			try{
				// provo ad emetter un diagnostico
				if(this.msgContext.getMsgDiagnostico()!=null){
					this.msgContext.getMsgDiagnostico().logErroreGenerico(msgErrore,"DisponibilitaRisorsePdD");
				}
			}catch(Throwable t){logCore.error("Emissione diagnostico per errore inizializzazione non riuscita: "+t.getMessage(),t);}
			if (this.msgContext.isGestioneRisposta()) {
				this.msgContext.setMessageResponse((erroreApplicativoBuilder.toMessage(ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
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
				this.msgContext.setMessageResponse((erroreApplicativoBuilder.toMessage(ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
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
				this.msgContext.setMessageResponse((erroreApplicativoBuilder.toMessage(ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
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
				this.msgContext.setMessageResponse((erroreApplicativoBuilder.toMessage(ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
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
				this.msgContext.setMessageResponse((erroreApplicativoBuilder.toMessage(ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
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
				this.msgContext.setMessageResponse((erroreApplicativoBuilder.toMessage(ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
						get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_534_REGISTRO_DEI_SERVIZI_NON_DISPONIBILE),null,null)));
			}
			return;
		}
		
		// Logger dei messaggi diagnostici
		MsgDiagnostico msgDiag = new MsgDiagnostico(identitaPdD, this.msgContext.getIdModulo());
		this.msgContext.setMsgDiagnostico(msgDiag); // aggiorno msg diagnostico
		msgDiag.setPddContext(inRequestContext.getPddContext(), protocolFactory);
		msgDiag.setPrefixMsgPersonalizzati(MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_CONTENUTI_APPLICATIVI);
		msgDiag.setDelegata(true);
		msgDiag.setPorta(urlProtocolContext.getFunctionParameters() + "_urlInvocazione("+ urlProtocolContext.getUrlInvocazione_formBased() + ")");

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
				this.msgContext.setMessageResponse((erroreApplicativoBuilder.toMessage(ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
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
				this.msgContext.setMessageResponse((erroreApplicativoBuilder.toMessage(ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
						get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_505_GET_DB_CONNECTION), e,null)));
			}
			return;
		}
		
		// Refresh reader
		registroServiziReader.updateState(openspcoopstate.getStatoRichiesta(),openspcoopstate.getStatoRisposta());
		configurazionePdDReader.updateState(openspcoopstate.getStatoRichiesta(),openspcoopstate.getStatoRisposta());
		msgDiag.updateState(openspcoopstate.getStatoRichiesta(),openspcoopstate.getStatoRisposta());
		
		
		
		
		
		// ------------- Dump richiesta-----------------------------
		if (configurazionePdDReader.dumpMessaggi()) {
			Dump dumpApplicativo = new Dump(identitaPdD,this.msgContext.getIdModulo(), this.msgContext.getTipoPorta(), inRequestContext.getPddContext(),
					openspcoopstate.getStatoRichiesta(),openspcoopstate.getStatoRisposta());
			dumpApplicativo.dumpRichiestaIngresso(requestMessage,inRequestContext.getConnettore());
		}
		
		
		
		
		
		
		
		
		Utilities.printFreeMemory("RicezioneContenutiApplicativi - Identificazione porta delegata e soggetto fruitore...");
		
		/* ------------ Identificazione Porta Delegata e SoggettoFruitore */
		msgDiag.mediumDebug("Identificazione porta delegata e soggetto fruitore...");
		IdentificazionePortaDelegata identificazione = new IdentificazionePortaDelegata(urlProtocolContext,protocolFactory);
		if (identificazione.process(openspcoopstate.getStatoRichiesta()) == false) {
			msgDiag.addKeyword(CostantiPdD.KEY_ERRORE_PROCESSAMENTO, identificazione.getErroreIntegrazione().getDescrizione(protocolFactory));
			msgDiag.logPersonalizzato("portaDelegataNonEsistente");
			openspcoopstate.releaseResource();
			if (this.msgContext.isGestioneRisposta()) {
				this.msgContext.setMessageResponse((erroreApplicativoBuilder.toMessage(identificazione.getErroreIntegrazione(),null,null)));
			}
			return;
		}
		// Raccolgo dati
		IDSoggetto soggettoFruitore = identificazione.getSoggetto();
		PortaDelegata pd = identificazione.getPd();
		requestMessage.addContextProperty(CostantiApi.NOME_PORTA, pd.getNome());
		identitaPdD = soggettoFruitore; // la PdD Assume l'identita del soggetto
		this.msgContext.getProtocol().setDominio(identitaPdD);
		this.msgContext.setIdentitaPdD(identitaPdD);
		// che possiede la Porta Delegata ID Porta Delegata
		IDPortaDelegata idPD = new IDPortaDelegata();
		idPD.setLocationPD(identificazione.getNomePDIndivituata());
		idPD.setSoggettoFruitore(soggettoFruitore);
		this.msgContext.getIntegrazione().setIdPD(idPD);
		// altri contesti
		msgDiag.setDominio(identitaPdD); // imposto anche il dominio nel msgDiag
		msgDiag.setFruitore(soggettoFruitore);
		msgDiag.addKeyword(CostantiPdD.KEY_PORTA_DELEGATA, identificazione.getNomePDIndivituata());
		msgDiag.addKeywords(soggettoFruitore);
		proprietaErroreAppl.setDominio(identitaPdD.getCodicePorta()); // imposto
		erroreApplicativoBuilder.setDominio(identitaPdD);
		erroreApplicativoBuilder.setMittente(soggettoFruitore);
		erroreApplicativoBuilder.setProprietaErroreApplicato(proprietaErroreAppl);
		// anche il dominio per gli errori
		this.msgContext.setProprietaErroreAppl(proprietaErroreAppl);
	
		
		
		
		
		Utilities.printFreeMemory("RicezioneContenutiApplicativi - Lettura header di integrazione...");
		

		/* --------------- Header Integrazione --------------- */
		msgDiag.mediumDebug("Lettura header di integrazione...");
		HeaderIntegrazione headerIntegrazioneRichiesta = null;
		if (this.msgContext.getHeaderIntegrazioneRichiesta() != null)
			headerIntegrazioneRichiesta = this.msgContext.getHeaderIntegrazioneRichiesta(); // prendo quello dell'IntegrationManager
		else
			headerIntegrazioneRichiesta = new HeaderIntegrazione();
		HeaderIntegrazione headerIntegrazioneRisposta = null;
		String[] tipiIntegrazionePD = null;
		try {
			tipiIntegrazionePD = configurazionePdDReader.getTipiIntegrazione(pd);
		} catch (Exception e) {
			msgDiag.logErroreGenerico(e, "getTipiIntegrazione(pd)");
			openspcoopstate.releaseResource();
			if (this.msgContext.isGestioneRisposta()) {
				this.msgContext.setMessageResponse((erroreApplicativoBuilder.toMessage(ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
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
		inRequestPDMessage.setPortaDelegata(pd);
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
				msgDiag.addKeyword(CostantiPdD.KEY_ERRORE_PROCESSAMENTO, e.toString());
				msgDiag.logPersonalizzato("headerIntegrazione.letturaFallita");
			}
		}

		
		
		
		
		Utilities.printFreeMemory("RicezioneContenutiApplicativi - Autenticazione del servizio applicativo...");
		
		
		
		
		
		
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
					msgDiag.addKeyword(CostantiPdD.KEY_ERRORE_PROCESSAMENTO, e.getMessage());
					msgDiag.logPersonalizzato("gestoreCredenziali.errore");
					ErroreIntegrazione errore = null;
					if(e instanceof GestoreCredenzialiConfigurationException){
						errore = ErroriIntegrazione.ERRORE_431_GESTORE_CREDENZIALI_ERROR.
								getErrore431_ErroreGestoreCredenziali(RicezioneContenutiApplicativi.tipiGestoriCredenziali[i], e);
					}else{
						errore = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
								get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_548_GESTORE_CREDENZIALI_NON_FUNZIONANTE);
					}
					openspcoopstate.releaseResource();
					if (this.msgContext.isGestioneRisposta()) {
						this.msgContext.setMessageResponse((erroreApplicativoBuilder.toMessage(errore,e,null)));
					}
					return;
				}
			}
		}
		
		
		
		
		
		
		
		
		
		
		
		/* ------------ Autenticazione ------------- */
		msgDiag.mediumDebug("Autenticazione del servizio applicativo...");
		String tipoAutenticazione = identificazione.getTipoAutenticazione();
		this.msgContext.getIntegrazione().setTipoAutenticazione(tipoAutenticazione);
		if(tipoAutenticazione!=null){
			msgDiag.addKeyword(CostantiPdD.KEY_TIPO_AUTENTICAZIONE, tipoAutenticazione);
		}
		String servizioApplicativo = CostantiPdD.SERVIZIO_APPLICATIVO_ANONIMO;
		if (CostantiConfigurazione.INVOCAZIONE_SERVIZIO_AUTENTICAZIONE_NONE.toString().equalsIgnoreCase(tipoAutenticazione)) {

			msgDiag.logPersonalizzato("autenticazioneDisabilitata");
			
			// dichiarazione nell'header di integrazione
			if (headerIntegrazioneRichiesta.getServizioApplicativo() != null) {
				servizioApplicativo = headerIntegrazioneRichiesta.getServizioApplicativo();
				boolean existsServizioApplicativo = false;
				try {
					existsServizioApplicativo = configurazionePdDReader.existsServizioApplicativo(idPD,servizioApplicativo);
				} catch (Exception e) {
					msgDiag.logErroreGenerico(e, "existsServizioApplicativo(idPD,"+servizioApplicativo+")");
					openspcoopstate.releaseResource();
					if (this.msgContext.isGestioneRisposta()) {
						this.msgContext.setMessageResponse((erroreApplicativoBuilder.toMessage(ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
								get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_536_CONFIGURAZIONE_NON_DISPONIBILE),e,null)));
					}
					return;
				}
				if (existsServizioApplicativo == false) {
					msgDiag.addKeyword(CostantiPdD.KEY_SA_FRUITORE, servizioApplicativo);
					msgDiag.logPersonalizzato("servizioApplicativoFruitore.identificazioneTramiteInfoIntegrazioneNonRiuscita");
					openspcoopstate.releaseResource();
					if (this.msgContext.isGestioneRisposta()) {
						this.msgContext.setMessageResponse((erroreApplicativoBuilder.toMessage(ErroriIntegrazione.ERRORE_402_AUTENTICAZIONE_FALLITA.
								getErrore402_AutenticazioneFallita("L'identità del servizio applicativo fornita ["+servizioApplicativo+"] non esiste nella configurazione della Porta di Dominio"),
								null,null)));
					}
					return;
				}
			}

		} else {

			msgDiag.logPersonalizzato("autenticazioneInCorso");
			
			// Caricamento classe
			String authClass = className.getAutenticazione(tipoAutenticazione);
			IAutenticazione auth = null;
			try {
				auth = (IAutenticazione) loader.newInstance(authClass);
				AbstractCore.init(auth, pddContext, protocolFactory);
			} catch (Exception e) {
				msgDiag.logErroreGenerico(e,"Autenticazione("+tipoAutenticazione+") Class.forName("+authClass+")");
				openspcoopstate.releaseResource();
				if (this.msgContext.isGestioneRisposta()) {
					this.msgContext.setMessageResponse((erroreApplicativoBuilder.toMessage(ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
							get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_503_AUTENTICAZIONE),e,null)));
				}
				return;
			}

			// Controllo Autenticazione
			if (auth.process(inRequestContext.getConnettore(), idPD, openspcoopstate.getStatoRichiesta()) == false) {
				msgDiag.addKeyword(CostantiPdD.KEY_ERRORE_PROCESSAMENTO, auth.getErrore().getDescrizione(protocolFactory));
				
				// Ridefinisco in caso di Basic, per aggiungere la password se presente
				if (credenziali.getUsername() != null) {
					if (credenziali.getUsername() != null){
						if(credenziali.getPassword()!=null && !"".equals(credenziali.getPassword()) ){
							String credenzialiFornite = "(";
							credenzialiFornite = credenzialiFornite + " Basic Username: ["+ credenziali.getUsername() + "]  Basic Password: ["+credenziali.getPassword()+"]";
							credenzialiFornite = credenzialiFornite + ") ";
							msgDiag.addKeyword(CostantiPdD.KEY_CREDENZIALI_SA_FRUITORE, credenzialiFornite);
						}
					}
				}
				
				msgDiag.logPersonalizzato("servizioApplicativoFruitore.identificazioneTramiteCredenziali");
				openspcoopstate.releaseResource();
				if (this.msgContext.isGestioneRisposta()) {
					this.msgContext.setMessageResponse((erroreApplicativoBuilder.toMessage(auth.getErrore(),
							auth.getException(),null)));
				}
				return;
			}

			servizioApplicativo = auth.getServizioApplicativo().getNome();
		}
		// Identita' errore
		msgDiag.setPorta(idPD.getLocationPD());
		msgDiag.setServizioApplicativo(servizioApplicativo);
		msgDiag.addKeyword(CostantiPdD.KEY_SA_FRUITORE, servizioApplicativo);
		// identita
		if(CostantiPdD.SERVIZIO_APPLICATIVO_ANONIMO.equals(servizioApplicativo)==false){
			this.msgContext.getIntegrazione().setServizioApplicativoFruitore(servizioApplicativo);
		}
		erroreApplicativoBuilder.setServizioApplicativo(servizioApplicativo);
		
		/*
		 * ------ msgDiagnosito di avvenuta ricezione della richiesta da parte del SIL -----------
		 */
		msgDiag.logPersonalizzato("ricevutaRichiestaApplicativa");
		
		/*
		 * Get Servizio Applicativo
		 */
		msgDiag.mediumDebug("Get servizio applicativo...");
		ServizioApplicativo sa = null;
		try{
			sa = configurazionePdDReader.getServizioApplicativo(idPD, servizioApplicativo);
		}catch (Exception e) {
			if( !(e instanceof DriverConfigurazioneNotFound) || !(CostantiPdD.SERVIZIO_APPLICATIVO_ANONIMO.equals(servizioApplicativo)) ){
				msgDiag.logErroreGenerico(e, "getServizioApplicativo(idPD,"+servizioApplicativo+")");
				openspcoopstate.releaseResource();
				if (this.msgContext.isGestioneRisposta()) {
					this.msgContext.setMessageResponse((erroreApplicativoBuilder.toMessage(ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
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
				this.msgContext.setMessageResponse((erroreApplicativoBuilder.toMessage(ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
						get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_536_CONFIGURAZIONE_NON_DISPONIBILE),e,null)));
			}
			return;
		}
		this.msgContext.setProprietaErroreAppl(proprietaErroreAppl);
		
		
		
		
		
		
		
		Utilities.printFreeMemory("RicezioneContenutiApplicativi - Lettura servizio associato alla PD invocata...");
		
		
		

		/*
		 * ------------- Lettura servizio associato alla PD invocata ------------
		 */
		msgDiag.mediumDebug("Lettura servizio associato alla PD invocata...");
		String idModuloInAttesa = null;
		if (this.msgContext.isGestioneRisposta())
			idModuloInAttesa = this.msgContext.getIdModulo();
		RichiestaDelegata richiestaDelegata = new RichiestaDelegata(
				soggettoFruitore, idPD.getLocationPD(), servizioApplicativo,
				idModuloInAttesa, proprietaErroreAppl, identitaPdD);
		try {
			if (configurazionePdDReader.identificazioneContentBased(pd)) {
				SOAPEnvelope soapEnvelopeRequest = requestMessage.getSOAPPart().getEnvelope();
				richiestaDelegata.setIdServizio(configurazionePdDReader.getIDServizio(pd, urlProtocolContext,
								requestMessage, soapEnvelopeRequest, 
								headerIntegrazioneRichiesta, 
								this.msgContext.getIdModulo().endsWith(IntegrationManager.ID_MODULO),
								this.msgContext.getSoapAction(),protocolFactory));
			} else {
				richiestaDelegata.setIdServizio(configurazionePdDReader.getIDServizio(pd, urlProtocolContext, 
								requestMessage, null,
								headerIntegrazioneRichiesta, 
								this.msgContext.getIdModulo().endsWith(IntegrationManager.ID_MODULO),
								this.msgContext.getSoapAction(),protocolFactory));
			}
			if (richiestaDelegata.getIdServizio() == null)
				throw new PDIdentificazioneDinamicaException(PDIdentificazioneDinamica.SERVIZIO,"Identificazione Servizio non riuscita");
			if (richiestaDelegata.getIdServizio().getSoggettoErogatore() == null
					|| richiestaDelegata.getIdServizio().getSoggettoErogatore().getNome() == null
					|| richiestaDelegata.getIdServizio().getSoggettoErogatore().getTipo() == null)
				throw new PDIdentificazioneDinamicaException(PDIdentificazioneDinamica.SOGGETTO_EROGATORE,"Identificazione SoggettoErogatore non riuscita");
			if (richiestaDelegata.getIdServizio().getTipoServizio() == null
					|| richiestaDelegata.getIdServizio().getServizio() == null)
				throw new PDIdentificazioneDinamicaException(PDIdentificazioneDinamica.SERVIZIO,"Identificazione Servizio (tipo/nome) non riuscita");

			// aggiorno informazioni dell'header di integrazione della
			// risposta
			headerIntegrazioneRisposta = new HeaderIntegrazione();
			headerIntegrazioneRisposta.getBusta().setTipoMittente(soggettoFruitore.getTipo());
			headerIntegrazioneRisposta.getBusta().setMittente(soggettoFruitore.getNome());
			headerIntegrazioneRisposta.getBusta().setTipoDestinatario(richiestaDelegata.getIdServizio().getSoggettoErogatore().getTipo());
			headerIntegrazioneRisposta.getBusta().setDestinatario(richiestaDelegata.getIdServizio().getSoggettoErogatore().getNome());
			headerIntegrazioneRisposta.getBusta().setTipoServizio(richiestaDelegata.getIdServizio().getTipoServizio());
			headerIntegrazioneRisposta.getBusta().setServizio(richiestaDelegata.getIdServizio().getServizio());
			headerIntegrazioneRisposta.getBusta().setAzione(richiestaDelegata.getIdServizio().getAzione());
			if (headerIntegrazioneRichiesta.getBusta() != null
					&& headerIntegrazioneRichiesta.getBusta().getRiferimentoMessaggio() != null) {
				// per profilo asincrono asimmetrico e simmetrico
				headerIntegrazioneRisposta.getBusta().setRiferimentoMessaggio(headerIntegrazioneRichiesta.getBusta().getRiferimentoMessaggio());
			}

		} catch (PDIdentificazioneDinamicaException e) {
			if(e.getPosizione()!=null)
				msgDiag.addKeyword(CostantiPdD.KEY_ERRORE_PROCESSAMENTO, "["+e.getPosizione().name()+"] "+ e.getMessage());
			else
				msgDiag.addKeyword(CostantiPdD.KEY_ERRORE_PROCESSAMENTO, e.getMessage());
			msgDiag.logPersonalizzato("identificazioneDinamicaServizioNonRiuscita");
			openspcoopstate.releaseResource();
			if (this.msgContext.isGestioneRisposta()) {
				this.msgContext.setMessageResponse((erroreApplicativoBuilder.toMessage(ErroriIntegrazione.ERRORE_403_PD_PATTERN_NON_VALIDO.
						getErrore403_IdentificazioneDinamicaPortaDelgata(e.getPosizione().name()),e,null)));
			}
			return;
		} catch (Exception e) {
			msgDiag.addKeyword(CostantiPdD.KEY_ERRORE_PROCESSAMENTO, e.getMessage());
			msgDiag.logPersonalizzato("identificazioneDinamicaServizioNonRiuscita");
			logCore.error(msgDiag.getMessaggio_replaceKeywords("identificazioneDinamicaServizioNonRiuscita"),e);
			openspcoopstate.releaseResource();
			if (this.msgContext.isGestioneRisposta()) {
				this.msgContext.setMessageResponse((erroreApplicativoBuilder.toMessage(ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
						get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_536_CONFIGURAZIONE_NON_DISPONIBILE), e,null)));
			}
			return;
		}
		msgDiag.setServizio(richiestaDelegata.getIdServizio());
		msgDiag.addKeywords(richiestaDelegata.getIdServizio());
		erroreApplicativoBuilder.setServizio(richiestaDelegata.getIdServizio());
		
		
		
		
		
		
		
		
		
		
		
		/* --------------- Verifica tipo soggetto fruitore e tipo servizio rispetto al canale utilizzato --------------- */
		msgDiag.mediumDebug("Verifica canale utilizzato...");
		List<String> tipiSoggettiSupportatiCanale = protocolFactory.createProtocolConfiguration().getTipiSoggetti();
		List<String> tipiServiziSupportatiCanale = protocolFactory.createProtocolConfiguration().getTipiServizi();
		// Nota: se qualche informazione e' null verranno segnalati altri errori
		if(soggettoFruitore!=null && soggettoFruitore.getTipo()!=null && 
				tipiSoggettiSupportatiCanale.contains(soggettoFruitore.getTipo())==false){
			msgDiag.logPersonalizzato("protocolli.tipoSoggetto.fruitore.unsupported");
			openspcoopstate.releaseResource();
			if (this.msgContext.isGestioneRisposta()) {
				this.msgContext.setMessageResponse((erroreApplicativoBuilder.toMessage(ErroriIntegrazione.ERRORE_436_TIPO_SOGGETTO_FRUITORE_NOT_SUPPORTED_BY_PROTOCOL.
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
				this.msgContext.setMessageResponse((erroreApplicativoBuilder.toMessage(ErroriIntegrazione.ERRORE_437_TIPO_SOGGETTO_EROGATORE_NOT_SUPPORTED_BY_PROTOCOL.
						getErrore437_TipoSoggettoErogatoreNotSupportedByProtocol(richiestaDelegata.getIdServizio().getSoggettoErogatore(),protocolFactory),null,null)));
			}
			return;
		}
		if(richiestaDelegata!=null && richiestaDelegata.getIdServizio()!=null &&
				richiestaDelegata.getIdServizio().getTipoServizio()!=null && 
				tipiServiziSupportatiCanale.contains(richiestaDelegata.getIdServizio().getTipoServizio())==false){
			msgDiag.logPersonalizzato("protocolli.tipoServizio.unsupported");
			openspcoopstate.releaseResource();
			if (this.msgContext.isGestioneRisposta()) {
				this.msgContext.setMessageResponse((erroreApplicativoBuilder.toMessage(ErroriIntegrazione.ERRORE_438_TIPO_SERVIZIO_NOT_SUPPORTED_BY_PROTOCOL.
						getErrore438_TipoServizioNotSupportedByProtocol(richiestaDelegata.getIdServizio(),protocolFactory),null,null)));
			}
			return;
		}
		
		
		
		
		
		
		
		/*
		 * ---------------- Verifico che il servizio di RicezioneContenutiApplicativi sia abilitato ---------------------
		 */
		if (StatoServiziPdD.isEnabled(RicezioneContenutiApplicativi.isActivePDService,
				RicezioneContenutiApplicativi.listaAbilitazioniPDService, 
				RicezioneContenutiApplicativi.listaDisabilitazioniPDService, 
				soggettoFruitore, richiestaDelegata.getIdServizio()) == false) {
			logCore.error("["+ RicezioneContenutiApplicativi.ID_MODULO+ "]  Servizio di ricezione contenuti applicativi disabilitato");
			msgDiag.logErroreGenerico("Servizio di ricezione contenuti applicativi disabilitato", "PD");
			openspcoopstate.releaseResource();
			if (this.msgContext.isGestioneRisposta()) {
				this.msgContext.setMessageResponse((erroreApplicativoBuilder.toMessage(ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
						get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_550_PD_SERVICE_NOT_ACTIVE),null,null)));
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
			correlazionePD = configurazionePdDReader.getCorrelazioneApplicativa(pd);
		} catch (Exception e) {
			msgDiag.logErroreGenerico(e,"getCorrelazioneApplicativa(pd)");
			openspcoopstate.releaseResource();
			if (this.msgContext.isGestioneRisposta()) {
				this.msgContext.setMessageResponse((erroreApplicativoBuilder.toMessage(ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
						get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_536_CONFIGURAZIONE_NON_DISPONIBILE),e,null)));
			}
			return;
		}
		GestoreCorrelazioneApplicativa correlazioneApplicativa = 
			new GestoreCorrelazioneApplicativa(openspcoopstate.getStatoRichiesta(), logCore, richiestaDelegata.getSoggettoFruitore(),
					richiestaDelegata.getIdServizio(),servizioApplicativo,protocolFactory);
		boolean correlazioneEsistente = false;
		String idCorrelazioneApplicativa = null;
		if (correlazionePD != null) {
			try {
				SOAPEnvelope soapEnvelopeRequest = requestMessage.getSOAPPart().getEnvelope();

				correlazioneEsistente = correlazioneApplicativa
						.verificaCorrelazione(correlazionePD, urlProtocolContext,soapEnvelopeRequest,headerIntegrazioneRichiesta, 
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
					this.msgContext.setMessageResponse((erroreApplicativoBuilder.toMessage(errore,e,null)));
				}
				return;
			}
		}

		if (correlazioneEsistente == false) {

			// Costruzione ID.
			msgDiag.mediumDebug("Costruzione identificativo...");
			try {
				
				Imbustamento imbustatore = new Imbustamento(protocolFactory);
				idMessageRequest = 
					imbustatore.buildID(openspcoopstate.getStatoRichiesta(),identitaPdD, 
							(String) this.msgContext.getPddContext().getObject(org.openspcoop2.core.constants.Costanti.CLUSTER_ID), 
							propertiesReader.getGestioneSerializableDB_AttesaAttiva(),
							propertiesReader.getGestioneSerializableDB_CheckInterval(),
							Boolean.TRUE);
				if (idMessageRequest == null) {
					throw new Exception("Identificativo non costruito.");
				}
				// Aggiornamento Informazioni Protocollo
				msgDiag.setIdMessaggioRichiesta(idMessageRequest);
				
			} catch (Exception e) {
				msgDiag.logErroreGenerico(e,"imbustatore.buildID(idMessageRequest)");
				openspcoopstate.releaseResource();
				if (this.msgContext.isGestioneRisposta()) {
					this.msgContext.setMessageResponse((erroreApplicativoBuilder.toMessage(ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
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
						this.msgContext.setMessageResponse((erroreApplicativoBuilder.toMessage(errore,null,null)));
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
				java.util.Properties propertiesIntegrazioneRisposta = new java.util.Properties();
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
					outResponsePDMessage.setPortaDelegata(pd);
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

		// emetto correlazione
		msgDiag.logCorrelazione();
		msgDiag.logCorrelazioneServizioApplicativo();
		
		
		
		
		

		/*
		  * --------- Informazioni protocollo ----------
		 */
		IBustaBuilder bustaBuilder = protocolFactory.createBustaBuilder();
		IDServizio idServizio = richiestaDelegata.getIdServizio();	
		this.msgContext.getProtocol().setFruitore(soggettoFruitore);	
		this.msgContext.getProtocol().setErogatore(idServizio.getSoggettoErogatore());		
		this.msgContext.getProtocol().setTipoServizio(idServizio.getTipoServizio());
		this.msgContext.getProtocol().setServizio(idServizio.getServizio());
		this.msgContext.getProtocol().setVersioneServizio(idServizio.getVersioneServizioAsInt());
		this.msgContext.getProtocol().setAzione(idServizio.getAzione());
		this.msgContext.getProtocol().setIdRichiesta(idMessageRequest);
		
		
		
		
		/*
		  * --------- Dati di identificazione ----------
		 */
		
		IDServizioApplicativo idServizioApplicativo = new IDServizioApplicativo();
		idServizioApplicativo.setNome(servizioApplicativo);
		idServizioApplicativo.setIdSoggettoProprietario(idPD.getSoggettoFruitore());
		
		DatiInvocazionePortaDelegata datiInvocazione = new DatiInvocazionePortaDelegata();
		datiInvocazione.setInfoConnettoreIngresso(inRequestContext.getConnettore());
		datiInvocazione.setIdServizio(richiestaDelegata.getIdServizio());
		datiInvocazione.setState(openspcoopstate.getStatoRichiesta());
		datiInvocazione.setIdPD(idPD);
		datiInvocazione.setPd(pd);		
		datiInvocazione.setIdServizioApplicativo(idServizioApplicativo);
		
		
		

		
		
		
		
		Utilities.printFreeMemory("RicezioneContenutiApplicativi - Autorizzazione del servizio applicativo...");
		
		/* ------------ Autorizzazione ------------- */
		msgDiag.mediumDebug("Autorizzazione del servizio applicativo...");
		String tipoAutorizzazione = identificazione.getTipoAutorizzazione();
		this.msgContext.getIntegrazione().setTipoAutorizzazione(tipoAutorizzazione);
		if(tipoAutorizzazione!=null){
			msgDiag.addKeyword(CostantiPdD.KEY_TIPO_AUTORIZZAZIONE, tipoAutorizzazione);
		}
		if (CostantiConfigurazione.AUTORIZZAZIONE_NONE.equalsIgnoreCase(tipoAutorizzazione) == false) {
			
			msgDiag.logPersonalizzato("autorizzazioneInCorso");
			
			ErroreIntegrazione errore = null;
			Exception eAutorizzazione = null;
			try {
//				if (RicezioneContenutiApplicativi.gestoriAutorizzazione.containsKey(tipoAutorizzazione) == false)
//					RicezioneContenutiApplicativi.aggiornaListaGestoreAutorizzazione(
//									tipoAutorizzazione, className,propertiesReader, logCore);
//				IAutorizzazione auth = RicezioneContenutiApplicativi.gestoriAutorizzazione.get(tipoAutorizzazione);
												
				EsitoAutorizzazioneIntegrazione esito = 
						GestoreAutorizzazione.verificaAutorizzazionePortaDelegata(tipoAutorizzazione, datiInvocazione, pddContext, protocolFactory); 
				if (esito.isServizioAutorizzato() == false) {
					errore = esito.getErroreIntegrazione();
					eAutorizzazione = esito.getEccezioneProcessamento();
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
				msgDiag.addKeyword(CostantiPdD.KEY_ERRORE_PROCESSAMENTO, errore.getDescrizione(protocolFactory));
				msgDiag.logPersonalizzato("servizioApplicativoFruitore.nonAutorizzato");
				openspcoopstate.releaseResource();

				if (this.msgContext.isGestioneRisposta()) {
					this.msgContext.setMessageResponse((erroreApplicativoBuilder.toMessage(errore,
							eAutorizzazione,null)));
				}
				return;
			}
			else{
				msgDiag.logPersonalizzato("autorizzazioneEffettuata");
			}
		}
		else{
			msgDiag.logPersonalizzato("autorizzazioneDisabilitata");
		}

		
		
		
		
		
		
		
		
		/* -------------- Identificazione servizio ------------------ */
		String infoSearch = idServizio.getTipoServizio() + "/"
				+ idServizio.getServizio() + " erogato dal Soggetto "
				+ idServizio.getSoggettoErogatore().getTipo() + "/"
				+ idServizio.getSoggettoErogatore().getNome();
		if (idServizio.getAzione() != null)
			infoSearch = infoSearch + " azione " + idServizio.getAzione();
		
		// Cerco nome del registro su cui cercare
		msgDiag.addKeyword(CostantiPdD.KEY_INFO_SERVIZIO_BUSTA,infoSearch );
		msgDiag.mediumDebug("Ricerca nome registro ["+infoSearch+"]...");
		String nomeRegistroForSearch = null;
		try {
			nomeRegistroForSearch = configurazionePdDReader.getRegistroForImbustamento(soggettoFruitore, idServizio, false);
		} catch (Exception e) {
			msgDiag.addKeyword(CostantiPdD.KEY_ERRORE_PROCESSAMENTO , "connettore associato al servizio non trovato, "+e.getMessage() );
			msgDiag.logPersonalizzato(MsgDiagnosticiProperties.MSG_DIAG_IMBUSTAMENTO,"registroServizi.ricercaServizioFallita");
			openspcoopstate.releaseResource();
			if (this.msgContext.isGestioneRisposta()) {
				this.msgContext.setMessageResponse((erroreApplicativoBuilder.toMessage(ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
						get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_514_ROUTING_CONFIGURATION_ERROR), e,null)));
			}
			return;
		}

		
		// Gestisco riferimento asincrono
		String riferimentoServizioCorrelato = null;
		if (headerIntegrazioneRichiesta.getBusta() != null && headerIntegrazioneRichiesta.getBusta().getRiferimentoMessaggio() != null)
			riferimentoServizioCorrelato = headerIntegrazioneRichiesta.getBusta().getRiferimentoMessaggio();		
		if(riferimentoServizioCorrelato==null){
			// FIX compatibilita integrazione asincroni con versioni precedente a 1.4
			// L'integrazione era possibile anche tramite info integrazione 'Collaborazione'
			if(propertiesReader.isIntegrazioneAsincroniConIdCollaborazioneEnabled()){
				if (headerIntegrazioneRichiesta.getBusta() != null && headerIntegrazioneRichiesta.getBusta().getIdCollaborazione() != null){
					// utilizzo l'informazione come integrazione asincrona SOLO se il servizio e' correlato.
					Servizio infoServizioTmpVerificaCorrelato = null;
					try{
						infoServizioTmpVerificaCorrelato = registroServiziReader.getInfoServizioCorrelato(soggettoFruitore,idServizio, nomeRegistroForSearch);
					}catch(Exception e){
						logCore.debug("Verifica servizio ["+infoSearch+"] se e' correlato, fallita: "+e.getMessage());
						try{
							infoServizioTmpVerificaCorrelato = registroServiziReader.getInfoServizioAzioneCorrelata(soggettoFruitore, idServizio,nomeRegistroForSearch);
						}catch(Exception eCorrelato){
							logCore.debug("Verifica servizio ["+infoSearch+"] se e' correlato rispetto all'azione, fallita: "+e.getMessage());
						}
					}
					if(infoServizioTmpVerificaCorrelato!=null){
						// Il servizio e' correlato!
						riferimentoServizioCorrelato = headerIntegrazioneRichiesta.getBusta().getIdCollaborazione();
					}
				}	
			}
		}
		if (riferimentoServizioCorrelato != null) {
			infoSearch = "Servizio correlato " + infoSearch;
		} else {
			infoSearch = "Servizio " + infoSearch;
		}
		infoSearch = "Ricerca nel registro dei servizi di: " + infoSearch;
		if (riferimentoServizioCorrelato != null)
			infoSearch = infoSearch + " (idServizioCorrelato: "+ riferimentoServizioCorrelato + ")";
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
			if (riferimentoServizioCorrelato != null) {

				String erroreRicerca = null;

				// Ricerca come servizio correlato
				msgDiag.mediumDebug("Ricerca servizio correlato ...");
				try {
					infoServizio = registroServiziReader.getInfoServizioCorrelato(soggettoFruitore,idServizio, nomeRegistroForSearch);
					isServizioCorrelato = true;
				} catch (DriverRegistroServiziAzioneNotFound e) {
					invocazioneAzioneErrata = e.getMessage();
					throw e;
				} catch (DriverRegistroServiziNotFound e) {
					erroreRicerca = "\nRicerca come servizio correlato-> "+ e.getMessage();
				}

				// Ricerca come servizio e azione correlata se ho un profilo
				// asincrono asimmetrico (check del profilo interno al metodo)
				if (infoServizio == null && (idServizio.getAzione() != null)) {
					msgDiag.mediumDebug("Ricerca servizio con azione correlata...");
					try {
						infoServizio = registroServiziReader.getInfoServizioAzioneCorrelata(soggettoFruitore, idServizio,nomeRegistroForSearch);
						isServizioCorrelato = true;
					} catch (DriverRegistroServiziAzioneNotFound e) {
						invocazioneAzioneErrata = e.getMessage();
						throw e;
					} catch (DriverRegistroServiziNotFound e) {
						erroreRicerca = erroreRicerca+ "\nRicerca come servizio correlato -> "+ e.getMessage();
					}
				}

				// Se non trovato genero errore
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
					infoServizio = registroServiziReader.getInfoServizio(soggettoFruitore, idServizio,nomeRegistroForSearch,true);
				} catch (DriverRegistroServiziAzioneNotFound e) {
					invocazioneAzioneErrata = e.getMessage();
					throw e;
				} catch (DriverRegistroServiziNotFound e) {
					erroreRicerca = "\nRicerca come servizio -> "+ e.getMessage();
				}

				// Ricerca come servizio correlato
				if (infoServizio == null) {
					msgDiag.mediumDebug("Ricerca servizio correlato...");
					try {
						infoServizio = registroServiziReader.getInfoServizioCorrelato(soggettoFruitore,idServizio, nomeRegistroForSearch);
						isServizioCorrelato = true;
					} catch (DriverRegistroServiziAzioneNotFound e) {
						invocazioneAzioneErrata = e.getMessage();
						throw e;
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
			msgDiag.addKeyword(CostantiPdD.KEY_ERRORE_PROCESSAMENTO , e.getMessage() );
			msgDiag.logPersonalizzato(MsgDiagnosticiProperties.MSG_DIAG_IMBUSTAMENTO,"registroServizi.ricercaServizioFallita");
			servizioNonTrovato = true;
		} catch (DriverRegistroServiziAzioneNotFound e) {
			eServiceNotFound = e;
			msgDiag.addKeyword(CostantiPdD.KEY_ERRORE_PROCESSAMENTO , e.getMessage() );
			msgDiag.logPersonalizzato(MsgDiagnosticiProperties.MSG_DIAG_IMBUSTAMENTO,"registroServizi.ricercaServizioFallita");
			// viene impostata la variabile invocazioneAzioneErrata
		} catch (DriverRegistroServiziPortTypeNotFound e) {
			eServiceNotFound = e;
			msgDiag.addKeyword(CostantiPdD.KEY_ERRORE_PROCESSAMENTO , "configurazione registro dei servizi errata, "+e.getMessage() );
			msgDiag.logPersonalizzato(MsgDiagnosticiProperties.MSG_DIAG_IMBUSTAMENTO,"registroServizi.ricercaServizioFallita");
			portTypeErrato = "Configurazione del registro dei Servizi errata: "+ e.getMessage();
		} catch(DriverRegistroServiziCorrelatoNotFound e){
			eServiceNotFound = e;
			msgDiag.addKeyword(CostantiPdD.KEY_ERRORE_PROCESSAMENTO , "correlazione asincrona non rilevata, "+e.getMessage() );
			msgDiag.logPersonalizzato(MsgDiagnosticiProperties.MSG_DIAG_IMBUSTAMENTO,"registroServizi.ricercaServizioFallita");
			servizioCorrelatoNonTrovato = true;
		} 
		catch (Exception e) {
			eServiceNotFound = e;
			msgDiag.addKeyword(CostantiPdD.KEY_ERRORE_PROCESSAMENTO , "errore generale, "+e.getMessage() );
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
			if (invocazioneAzioneErrata != null) {
				erroreIntegrazione = ErroriIntegrazione.ERRORE_423_SERVIZIO_CON_AZIONE_SCORRETTA.
						getErrore423_ServizioConAzioneScorretta("(azione:"+ idServizio.getAzione()+ ") "+ invocazioneAzioneErrata);
			} else if (portTypeErrato != null) {
				erroreIntegrazione = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
						get5XX_ErroreProcessamento(portTypeErrato, CodiceErroreIntegrazione.CODICE_540_REGISTRO_SERVIZI_MAL_CONFIGURATO);
			} else if (servizioNonTrovato) {
				erroreIntegrazione = ErroriIntegrazione.ERRORE_405_SERVIZIO_NON_TROVATO.getErroreIntegrazione();
			} else if (ricercaConErrore) {
				erroreIntegrazione = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
						get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_534_REGISTRO_DEI_SERVIZI_NON_DISPONIBILE);
			} else if (servizioCorrelatoNonTrovato){
				erroreIntegrazione = ErroriIntegrazione.ERRORE_408_SERVIZIO_CORRELATO_NON_TROVATO.getErroreIntegrazione();
			} 
			else {
				erroreIntegrazione = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
						get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_533_RISORSE_DISPONIBILI_LIVELLO_CRITICO);
			}
			openspcoopstate.releaseResource();
			if (this.msgContext.isGestioneRisposta()) {
				this.msgContext.setMessageResponse((erroreApplicativoBuilder.toMessage(erroreIntegrazione,eServiceNotFound,null)));
			}
			return;
		}
		infoServizio.setCorrelato(isServizioCorrelato);
		this.msgContext.getProtocol().setProfiloCollaborazione(infoServizio.getProfiloDiCollaborazione(),null); // il valore verra' serializzato solo successivamente nella busta
		msgDiag.addKeyword(CostantiPdD.KEY_PROFILO_COLLABORAZIONE, traduttore.toString(infoServizio.getProfiloDiCollaborazione()));
		if(infoServizio!=null){
			this.msgContext.getProtocol().setIdAccordo(infoServizio.getIdAccordo());
			richiestaDelegata.setIdAccordo(infoServizio.getIdAccordo());
			try{
				idServizio.setUriAccordo(IDAccordoFactory.getInstance().getUriFromIDAccordo(infoServizio.getIdAccordo()));
			}catch(Exception e){}
		}
		Busta bustaRichiesta = infoServizio.convertToBusta(protocol, soggettoFruitore);
		inRequestPDMessage.setBustaRichiesta(bustaRichiesta);
		
	
		/* -------- Profilo di Gestione ------------- */
		try {
			String profiloGestione = registroServiziReader.getProfiloGestioneFruizioneServizio(idServizio,nomeRegistroForSearch);
			richiestaDelegata.setProfiloGestione(profiloGestione);
			msgDiag.mediumDebug("Profilo di gestione ["+ RicezioneContenutiApplicativi.ID_MODULO+ "] della busta: " + profiloGestione);
		} catch (Exception e) {
			msgDiag.addKeyword(CostantiPdD.KEY_ERRORE_PROCESSAMENTO , "analisi del profilo di gestione fallita, "+e.getMessage() );
			msgDiag.logPersonalizzato(MsgDiagnosticiProperties.MSG_DIAG_IMBUSTAMENTO,"registroServizi.ricercaServizioFallita");
			logCore.error("Comprensione Profilo Gestione fallita",e);
			openspcoopstate.releaseResource();
			if (this.msgContext.isGestioneRisposta()) {
				this.msgContext.setMessageResponse((erroreApplicativoBuilder.toMessage(ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
						get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_525_GESTIONE_FUNZIONALITA_PROTOCOLLO),e,null)));
			}
			return;
		}
		
		
		
		
		/* -------- OpenSPCoop2Message Update ------------- */
		try {
			msgDiag.mediumDebug("Aggiornamento del messaggio");
			requestMessage = protocolFactory.createProtocolManager().updateOpenSPCoop2MessageRequest(requestMessage.getVersioneSoap(),requestMessage, bustaRichiesta);
		} catch (Exception e) {
			msgDiag.addKeyword(CostantiPdD.KEY_ERRORE_PROCESSAMENTO , "Aggiornamento messaggio fallito, "+e.getMessage() );
			msgDiag.logErroreGenerico(e,"ProtocolManager.updateOpenSPCoop2Message");
			logCore.error("ProtocolManager.updateOpenSPCoop2Message error: "+e.getMessage(),e);
			openspcoopstate.releaseResource();
			if (this.msgContext.isGestioneRisposta()) {
				this.msgContext.setMessageResponse((erroreApplicativoBuilder.toMessage(ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
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
				msgDiag.addKeyword(CostantiPdD.KEY_ERRORE_PROCESSAMENTO , "recupero degli indirizzi di risposta per i soggetti fallita, "+e.getMessage() );
				msgDiag.logPersonalizzato(MsgDiagnosticiProperties.MSG_DIAG_IMBUSTAMENTO,"registroServizi.ricercaServizioFallita");
				logCore.error("Comprensione Indirizzo Risposta fallita",e);
				openspcoopstate.releaseResource();
				if (this.msgContext.isGestioneRisposta()) {
					this.msgContext.setMessageResponse((erroreApplicativoBuilder.toMessage(ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
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
			if(inRequestProtocolContext.getConnettore()!=null){
				inRequestProtocolContext.getConnettore().setCredenziali(credenziali);
			}
			inRequestProtocolContext.setProtocollo(this.msgContext.getProtocol());
			inRequestProtocolContext.setIntegrazione(this.msgContext.getIntegrazione());
			GestoreHandlers.inRequestProtocol(inRequestProtocolContext, msgDiag, logCore);
		}catch(Exception e){		
			ErroreIntegrazione erroreIntegrazione = null;
			if(e instanceof HandlerException){
				HandlerException he = (HandlerException) e;
				if(he.isEmettiDiagnostico()){
					msgDiag.logErroreGenerico(e,he.getIdentitaHandler());
				}
				logCore.error("Gestione InRequestProtocolHandler non riuscita ("+he.getIdentitaHandler()+"): "	+ he);
				if(this.msgContext.isGestioneRisposta() && he.isSetErrorMessageInFault()){
					erroreIntegrazione = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
							get5XX_ErroreProcessamento(e.getMessage(),CodiceErroreIntegrazione.CODICE_558_HANDLER_IN_PROTOCOL_REQUEST);
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
				this.msgContext.setMessageResponse((erroreApplicativoBuilder.toMessage(erroreIntegrazione,e,null)));
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
				this.msgContext.setMessageResponse((erroreApplicativoBuilder.toMessage(ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
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
		try {
			validazioneContenutoApplicativoApplicativo = configurazionePdDReader.getTipoValidazioneContenutoApplicativo(pd,implementazionePdDDestinatario);
		} catch (Exception e) {
			msgDiag.logErroreGenerico(e,"getTipoValidazioneContenutoApplicativo(pd,"+implementazionePdDDestinatario+")");
			openspcoopstate.releaseResource();
			if (this.msgContext.isGestioneRisposta()) {
				this.msgContext.setMessageResponse((erroreApplicativoBuilder.toMessage(ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
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

			ByteArrayInputStream binXSD = null;
			try {
				msgDiag.logPersonalizzato("validazioneContenutiApplicativiRichiestaInCorso");
				
				// Accept mtom message
				List<MtomXomReference> xomReferences = null;
				if(StatoFunzionalita.ABILITATO.equals(validazioneContenutoApplicativoApplicativo.getAcceptMtomMessage())){
					msgDiag.mediumDebug("Validazione xsd della richiesta (mtomFastUnpackagingForXSDConformance)...");
					xomReferences = requestMessage.mtomFastUnpackagingForXSDConformance();
				}
				
				// Init Validatore
				boolean readWSDL = 
					CostantiConfigurazione.VALIDAZIONE_CONTENUTI_APPLICATIVI_WSDL.equals(validazioneContenutoApplicativoApplicativo.getTipo());
				msgDiag.mediumDebug("Validazione xsd della richiesta (initValidator)...");
				ValidatoreMessaggiApplicativi validatoreMessaggiApplicativi = new ValidatoreMessaggiApplicativi(
						registroServiziReader, richiestaDelegata.getIdServizio(), 
						requestMessage.getVersioneSoap(),requestMessage.getSOAPPart().getEnvelope(),readWSDL);
			
				// Validazione WSDL
				if (CostantiConfigurazione.VALIDAZIONE_CONTENUTI_APPLICATIVI_WSDL.equals(validazioneContenutoApplicativoApplicativo.getTipo())
					|| CostantiConfigurazione.VALIDAZIONE_CONTENUTI_APPLICATIVI_OPENSPCOOP.equals(validazioneContenutoApplicativoApplicativo.getTipo())) {
					msgDiag.mediumDebug("Validazione wsdl della richiesta ...");
					validatoreMessaggiApplicativi.validateWithWsdlLogicoImplementativo(true,this.msgContext.getSoapAction());
				}
				
				// Validazione XSD
				msgDiag.mediumDebug("Validazione xsd della richiesta (validazione)...");
				validatoreMessaggiApplicativi.validateWithWsdlDefinitorio(true);

				// Ripristino struttura messaggio con xom
				if(xomReferences!=null && xomReferences.size()>0){
					msgDiag.mediumDebug("Validazione xsd della richiesta (mtomRestoreAfterXSDConformance)...");
					requestMessage.mtomRestoreAfterXSDConformance(xomReferences);
				}
				
				msgDiag.logPersonalizzato("validazioneContenutiApplicativiRichiestaEffettuata");
				
			} catch (ValidatoreMessaggiApplicativiException ex) {
				msgDiag.addKeyword(CostantiPdD.KEY_ERRORE_PROCESSAMENTO, ex.getMessage());
				msgDiag.logPersonalizzato("validazioneContenutiApplicativiRichiestaNonRiuscita");
				logCore.error("[ValidazioneContenutiApplicativi Richiesta] "+ex.getMessage(),ex);
				if (CostantiConfigurazione.STATO_CON_WARNING_WARNING_ONLY.equals(validazioneContenutoApplicativoApplicativo.getStato()) == false) {
					// validazione abilitata
					openspcoopstate.releaseResource();
					if (this.msgContext.isGestioneRisposta()) {
						this.msgContext.setMessageResponse((erroreApplicativoBuilder.toMessage(ex.getErrore(),ex,null)));
					}
					return;
				}
			} catch (Exception ex) {
				msgDiag.addKeyword(CostantiPdD.KEY_ERRORE_PROCESSAMENTO, ex.getMessage());
				msgDiag.logPersonalizzato("validazioneContenutiApplicativiRichiestaNonRiuscita");
				logCore.error("Riscontrato errore durante la validazione xsd della richiesta applicativa",ex);
				if (CostantiConfigurazione.STATO_CON_WARNING_WARNING_ONLY.equals(validazioneContenutoApplicativoApplicativo.getStato()) == false) {
					// validazione abilitata
					openspcoopstate.releaseResource();
					if (this.msgContext.isGestioneRisposta()) {
						this.msgContext.setMessageResponse((erroreApplicativoBuilder.toMessage(ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
								get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_531_VALIDAZIONE_WSDL_FALLITA),ex,null)));
					}
					return;
				}
			} finally {
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
		String tipoAutorizzazioneContenuto = identificazione.getTipoAutorizzazioneContenuto();
		this.msgContext.getIntegrazione().setTipoAutorizzazioneContenuto(tipoAutorizzazioneContenuto);
		if(tipoAutorizzazioneContenuto!=null){
			msgDiag.addKeyword(CostantiPdD.KEY_TIPO_AUTORIZZAZIONE_CONTENUTO, tipoAutorizzazioneContenuto);
		}
		if (CostantiConfigurazione.AUTORIZZAZIONE_NONE.equalsIgnoreCase(tipoAutorizzazioneContenuto) == false) {
			
			msgDiag.logPersonalizzato("autorizzazioneContenutiApplicativiInCorso");
			
			ErroreIntegrazione errore = null;
			Exception eAutorizzazione = null;
			try {
//				if (RicezioneContenutiApplicativi.gestoriAutorizzazioneContenuto.containsKey(tipoAutorizzazioneContenuto) == false)
//					RicezioneContenutiApplicativi.aggiornaListaGestoreAutorizzazioneContenuto(
//							tipoAutorizzazioneContenuto, className,propertiesReader, logCore);
//				IAutorizzazioneContenuto auth = RicezioneContenutiApplicativi.gestoriAutorizzazioneContenuto.get(tipoAutorizzazioneContenuto);
				
				// Inizializzo IGestoreAutorizzazione new Type
				String classType = null;
				IAutorizzazioneContenutoPortaDelegata auth = null;
				try {
					classType = className.getAutorizzazioneContenuto(tipoAutorizzazioneContenuto);
					auth = ((IAutorizzazioneContenutoPortaDelegata) loader.newInstance(classType));
					AbstractCore.init(auth, pddContext, protocolFactory);
				} catch (Exception e) {
					throw new Exception(
							"Riscontrato errore durante il caricamento della classe ["+ classType
									+ "] da utilizzare per la gestione dell'autorizzazione contenuto di tipo ["+ tipoAutorizzazioneContenuto + "]: " + e.getMessage());
				}
				
				if (auth != null) {
					// Controllo Autorizzazione
					EsitoAutorizzazioneIntegrazione esito = auth.process(datiInvocazione,requestMessage);
					if (esito.isServizioAutorizzato() == false) {
						errore = esito.getErroreIntegrazione();
						eAutorizzazione = esito.getEccezioneProcessamento();
					}
				} else {
					errore = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
							get5XX_ErroreProcessamento("gestore ["
									+ tipoAutorizzazioneContenuto + "] non inizializzato", CodiceErroreIntegrazione.CODICE_542_AUTORIZZAZIONE_CONTENUTO);
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
				msgDiag.addKeyword(CostantiPdD.KEY_ERRORE_PROCESSAMENTO, errore.getDescrizione(protocolFactory));
				msgDiag.logPersonalizzato("servizioApplicativoFruitore.contenuto.nonAutorizzato");
				openspcoopstate.releaseResource();

				if (this.msgContext.isGestioneRisposta()) {
					this.msgContext.setMessageResponse((erroreApplicativoBuilder.toMessage(errore,
							eAutorizzazione,null)));
				}
				return;
			}
			else{
				msgDiag.logPersonalizzato("autorizzazioneContenutiApplicativiEffettuata");
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
				this.msgContext.setMessageResponse((erroreApplicativoBuilder.toMessage(ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
						get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_536_CONFIGURAZIONE_NON_DISPONIBILE),e,null)));
			}
			return;
		}
		if (invocazionePDPerRiferimento) {
			if (this.msgContext.isInvocazionePDPerRiferimento() == false) {
				msgDiag.logPersonalizzato("portaDelegataInvocabilePerRiferimento.riferimentoNonPresente");
				openspcoopstate.releaseResource();
				if (this.msgContext.isGestioneRisposta()) {
					this.msgContext.setMessageResponse((erroreApplicativoBuilder.toMessage(ErroriIntegrazione.ERRORE_412_PD_INVOCABILE_SOLO_PER_RIFERIMENTO.
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
					bustaBuilder.sbustamento(openspcoopstate.getStatoRichiesta(),requestMessage, bustaRichiesta, true, proprietaManifest);
				}
			}catch(Exception e){
				msgDiag.logErroreGenerico(e,"invocazionePortaDelegataPerRiferimento.sbustamentoProtocolHeader()");
				openspcoopstate.releaseResource();
				if (this.msgContext.isGestioneRisposta()) {
					this.msgContext.setMessageResponse((erroreApplicativoBuilder.toMessage(ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
							get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_536_CONFIGURAZIONE_NON_DISPONIBILE),e,null)));
				}
				return;		
			}
			
		} else {
			if (this.msgContext.isInvocazionePDPerRiferimento()) {
				msgDiag.logPersonalizzato("portaDelegataInvocabileNormalmente.riferimentoPresente");
				openspcoopstate.releaseResource();
				if (this.msgContext.isGestioneRisposta()) {
					this.msgContext.setMessageResponse((erroreApplicativoBuilder.toMessage(ErroriIntegrazione.ERRORE_413_PD_INVOCABILE_SOLO_SENZA_RIFERIMENTO.
							getErroreIntegrazione(),null,null)));
				}
				return;
			}
		}

		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		Utilities.printFreeMemory("RicezioneContenutiApplicativi - Controllo non esistenza di una busta ...");
		
		
		
		
		/* ------------ Controllo che il messaggio non contenga una busta */
		msgDiag.mediumDebug("Controllo non esistenza di una busta ...");
		ValidazioneSintattica validatoreSintattico = new ValidazioneSintattica(openspcoopstate.getStatoRichiesta(),requestMessage, protocolFactory);
		boolean esisteProtocolloMsgRichiesta = false;
		try{
			esisteProtocolloMsgRichiesta = validatoreSintattico.
					verifyProtocolPresence(this.msgContext.getTipoPorta(),infoServizio.getProfiloDiCollaborazione(),true);
		} catch (Exception e){
			msgDiag.logErroreGenerico(e,"controlloEsistenzaBusta");
			openspcoopstate.releaseResource();
			if (this.msgContext.isGestioneRisposta()) {
				this.msgContext.setMessageResponse((erroreApplicativoBuilder.toMessage(ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
						get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_525_GESTIONE_FUNZIONALITA_PROTOCOLLO),e,null)));
			}
			return;
		} finally {
			if(esisteProtocolloMsgRichiesta) {
				msgDiag.logPersonalizzato("richiestaContenenteBusta");
				openspcoopstate.releaseResource();
				if (this.msgContext.isGestioneRisposta()) {
					this.msgContext.setMessageResponse((erroreApplicativoBuilder.toMessage(ErroriIntegrazione.ERRORE_420_BUSTA_PRESENTE_RICHIESTA_APPLICATIVA.
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
					this.msgContext.setMessageResponse((erroreApplicativoBuilder.toMessage(ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
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
			allegaBody = configurazionePdDReader.isAllegaBody(pd);
			scartaBody = configurazionePdDReader.isScartaBody(pd);
		} catch (Exception e) {
			msgDiag.logErroreGenerico(e,"configurazionePdDReader.isAllega/ScartaBody(pd)");
			openspcoopstate.releaseResource();
			if (this.msgContext.isGestioneRisposta()) {
				this.msgContext.setMessageResponse((erroreApplicativoBuilder.toMessage(ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
						get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_536_CONFIGURAZIONE_NON_DISPONIBILE),e,null)));
			}
			return;
		}
		if (scartaBody) {
			try {
				// E' permesso SOLO per messaggi con attachment
				if (requestMessage.countAttachments() <= 0) {
					throw new Exception("La funzionalita' e' permessa solo per messaggi SOAP With Attachments");
				}
			} catch (Exception e) {
				msgDiag.addKeyword(CostantiPdD.KEY_ERRORE_PROCESSAMENTO, e.getMessage());
				msgDiag.logPersonalizzato("funzionalitaScartaBodyNonEffettuabile");
				openspcoopstate.releaseResource();
				if (this.msgContext.isGestioneRisposta()) {
					this.msgContext.setMessageResponse((erroreApplicativoBuilder.toMessage(ErroriIntegrazione.ERRORE_425_SCARTA_BODY.
							getErrore425_ScartaBody(e.getMessage()),e,null)));
				}
				return;
			}
		}
		if (allegaBody) {
			try {
				// E' permesso SOLO per messaggi senza attachment
				if (requestMessage.countAttachments() > 0) {
					throw new Exception("La funzionalita' non e' permessa per messaggi SOAP With Attachments");
				}

				// Metto il contenuto del body in un attachments.
				byte[] body = SoapUtils.sbustamentoSOAPEnvelope(requestMessage.getSOAPPart().getEnvelope());
				AttachmentPart ap =  requestMessage.createAttachmentPart();
				ap.setRawContentBytes(body, 0, body.length, "text/xml");
				ap.setContentId(requestMessage.createContentID(propertiesReader.getHeaderSoapActorIntegrazione()));
				requestMessage.addAttachmentPart(ap);
				
				// Aggiungo contentID all'attachmet contenente la SOAPEnvelope
				// Necessario per essere compatibile con alcune implementazioni, es axis14, 
				// altrimenti essendo il ContentType senza Start element, Axis14 utilizza come xml per costruire la SOAPEnvelope 
				// il primo attachment nel messaggio MIME che contiene il ContentID.
				requestMessage.getSOAPPart().addMimeHeader("Content-Id", requestMessage.createContentID(propertiesReader.getHeaderSoapActorIntegrazione()));
				
				// Rimuovo contenuti del body
				requestMessage.getSOAPBody().removeContents();

			} catch (Exception e) {
				msgDiag.addKeyword(CostantiPdD.KEY_ERRORE_PROCESSAMENTO, e.getMessage());
				msgDiag.logPersonalizzato("funzionalitaAllegaBodyNonEffettuabile");
				openspcoopstate.releaseResource();
				if (this.msgContext.isGestioneRisposta()) {
					this.msgContext.setMessageResponse((erroreApplicativoBuilder.toMessage(ErroriIntegrazione.ERRORE_424_ALLEGA_BODY.
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
				oneWayStateless = configurazionePdDReader.isModalitaStateless(pd, infoServizio.getProfiloDiCollaborazione());
			} else if (ProfiloDiCollaborazione.SINCRONO.equals(infoServizio.getProfiloDiCollaborazione())) {
				sincronoStateless = configurazionePdDReader.isModalitaStateless(pd, infoServizio.getProfiloDiCollaborazione());
			} else if(ProfiloDiCollaborazione.ASINCRONO_SIMMETRICO.equals(infoServizio.getProfiloDiCollaborazione()) ||
					ProfiloDiCollaborazione.ASINCRONO_ASIMMETRICO.equals(infoServizio.getProfiloDiCollaborazione())){
				asincronoStateless = configurazionePdDReader.isModalitaStateless(pd, infoServizio.getProfiloDiCollaborazione());
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
				this.msgContext.setMessageResponse((erroreApplicativoBuilder.toMessage(ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
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

			localForward = configurazionePdDReader.isLocalForwardMode(pd);
			
			if(localForward){
				
				String erroreConfigurazione = null;
				
				String prefix = "( Servizio v"+idServizio.getVersioneServizio()+" "+idServizio.getTipoServizio()+"/"+idServizio.getServizio();
				if(idServizio.getAzione()!=null){
					prefix = prefix+" Azione "+idServizio.getAzione();
				}
				prefix = prefix+" Erogatore "+idServizio.getSoggettoErogatore().toString()+" ) ";
				
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
				if(erroreConfigurazione==null){
					IDPortaApplicativaByNome idPaByNome = configurazionePdDReader.convertTo_SafeMethod(idServizio, null);
					ra = new RichiestaApplicativa(soggettoFruitore, idServizio, identitaPdD, idPaByNome);
					if(configurazionePdDReader.existsPA(ra)==false){
						erroreConfigurazione = "non risulta esistere una porta applicativa associata al servizio richiesto";
					}
				}
				
				if(erroreConfigurazione==null){
					pa = configurazionePdDReader.getPortaApplicativa_SafeMethod(ra.getIdPAbyNome());
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
						this.msgContext.setMessageResponse((erroreApplicativoBuilder.toMessage(ErroriIntegrazione.ERRORE_435_LOCAL_FORWARD_CONFIG_NON_VALIDA.
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
				
				localForwardEngine = new LocalForwardEngine(localForwardParameter);
							
			}
			
		} catch (Exception e) {
			msgDiag.logErroreGenerico(e,"GestioneLocalForward");
			openspcoopstate.releaseResource();
			if (this.msgContext.isGestioneRisposta()) {
				this.msgContext.setMessageResponse((erroreApplicativoBuilder.toMessage(ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
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
			} catch (Exception e) {
				msgDiag.logErroreGenerico(e,"GestioneLocalForward.processRequest");
				openspcoopstate.releaseResource();
				if (this.msgContext.isGestioneRisposta()) {
					this.msgContext.setMessageResponse((erroreApplicativoBuilder.toMessage(ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
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
					msgRequest.deleteMessageWithLock(msg,propertiesReader.getMsgGiaInProcessamento_AttesaAttiva(),propertiesReader.getMsgGiaInProcessamento_CheckInterval());
				}

				// Altrimenti genero errore messaggio precedente ancora in
				// processamento
				else {
					msgDiag.addKeyword(CostantiPdD.KEY_PROPRIETARIO_MESSAGGIO, proprietarioMessaggio);
					msgDiag.logPersonalizzato("messaggioInGestione");
					openspcoopstate.releaseResource(); 
					if (this.msgContext.isGestioneRisposta()) {
						this.msgContext.setMessageResponse((erroreApplicativoBuilder.toMessage(
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
				this.msgContext.setMessageResponse((erroreApplicativoBuilder.toMessage(ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
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
				
				if (StatoFunzionalitaProtocollo.ABILITATA.equals(moduleManager.getCollaborazione(infoServizio.getProfiloDiCollaborazione()))) {
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
			msgRequest.registraMessaggio(requestMessage, dataIngressoMessaggio,
					(oneWayStateless || sincronoStateless || asincronoStateless),
					idCorrelazioneApplicativa);	
			if(localForward){
				msgRequest.aggiornaProprietarioMessaggio(org.openspcoop2.pdd.mdb.ConsegnaContenutiApplicativi.ID_MODULO);
			}else{
				msgRequest.aggiornaProprietarioMessaggio(org.openspcoop2.pdd.mdb.Imbustamento.ID_MODULO);
			}
						
			if(richiestaAsincronaSimmetricaStateless){
				msgRequest.registraInformazioniMessaggio_statelessEngine(dataIngressoMessaggio, org.openspcoop2.pdd.mdb.Imbustamento.ID_MODULO,
						idCorrelazioneApplicativa);
			}
			
		} catch (Exception e) {
			msgDiag.logErroreGenerico(e,"msgRequest.aggiornaProprietarioMessaggio");
			msgRequest.deleteMessageFromFileSystem(); // elimino richiesta salvata su fileSystem
			// Rilascio Connessione DB
			openspcoopstate.releaseResource();
			if (this.msgContext.isGestioneRisposta()) {
				this.msgContext.setMessageResponse((erroreApplicativoBuilder.toMessage(ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
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
						String tipo = Costanti.OUTBOX;
						if(localForward){
							tipo = Costanti.INBOX;	
						}
						String causa = "Aggiornamento dati busta con id ["+idMessageRequest+"] tipo["+tipo+"] non riuscito: "+e.getMessage();
						try{
							GestoreMessaggi.acquireLock(msgDiag, causa, propertiesReader.getMsgGiaInProcessamento_AttesaAttiva(), propertiesReader.getMsgGiaInProcessamento_CheckInterval());
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
								GestoreMessaggi.releaseLock(msgDiag, causa);
							}catch(Exception eUnlock){}
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
				infoIntegrazione.setLocationPD(richiestaDelegata.getLocationPD());
				infoIntegrazione.setServizioApplicativo(richiestaDelegata.getServizioApplicativo());
				repositoryBuste.aggiornaInfoIntegrazione(idMessageRequest,tipoMessaggio,infoIntegrazione);
			}
		} catch (Exception e) {
			msgDiag.logErroreGenerico(e,"registrazioneAggiornamentoBusta");
			msgRequest.deleteMessageFromFileSystem(); // elimino richiesta salvata su fileSystem
			openspcoopstate.releaseResource();
			if (this.msgContext.isGestioneRisposta()) {
				this.msgContext.setMessageResponse((erroreApplicativoBuilder.toMessage(ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
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
				localForwardParameter.getMsgDiag().setDelegata(false);
				localForwardParameter.getMsgDiag().logCorrelazione(); // emetto correlazione per la PA
				localForwardEngine.updateLocalForwardParameter(localForwardParameter);
				
				localForwardEngine.sendRequest(msgRequest);
				
				localForwardParameter.getMsgDiag().setDelegata(true);
				
			}
			else{
				imbustamentoMSG.setRichiestaDelegata(richiestaDelegata);
				imbustamentoMSG.setInfoServizio(infoServizio);
				imbustamentoMSG.setOneWayVersione11(oneWayVersione11);
				if (headerIntegrazioneRichiesta.getBusta() != null) {
					// RiferimentoServizioCorrelato
					riferimentoServizioCorrelato = moduleManager.getIdCorrelazioneAsincrona(
							headerIntegrazioneRichiesta.getBusta().getRiferimentoMessaggio(), headerIntegrazioneRichiesta.getBusta().getIdCollaborazione());
					if (riferimentoServizioCorrelato != null) {
						// Se presente riferimentoMessaggio utilizzo quello.
						imbustamentoMSG.setRiferimentoServizioCorrelato(riferimentoServizioCorrelato);
					}
					// Collaborazione
					if (headerIntegrazioneRichiesta.getBusta().getIdCollaborazione() != null)
						imbustamentoMSG.setIdCollaborazione(headerIntegrazioneRichiesta.getBusta().getIdCollaborazione());
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
				this.msgContext.setMessageResponse((erroreApplicativoBuilder.toMessage(ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
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
				this.msgContext.setMessageResponse((erroreApplicativoBuilder.toMessage(ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
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
		parametriGestioneRisposta.setXmlBuilder(erroreApplicativoBuilder);
		
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

		parametriGestioneRisposta.setPortaDelegata(pd);
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
		ErroreApplicativoBuilder xmlBuilder = parametriGestioneRisposta.getXmlBuilder();
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
		String idTransazione = PdDContext.getValue(org.openspcoop2.core.constants.Costanti.CLUSTER_ID, imbustamentoMSG.getPddContext());
		
		
		if(localForward){
			
			
			// E' RicezioneContenutiApplicativi se siamo in oneway11 con presa in carico
			if( ConsegnaContenutiApplicativi.ID_MODULO.equals( ((OpenSPCoopStateless)openspcoopstate).getDestinatarioRequestMsgLib() )
					&&
				 ((OpenSPCoopStateless)openspcoopstate).getDestinatarioResponseMsgLib()==null ){
						
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
						this.msgContext.setMessageResponse((xmlBuilder.toMessage(ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.getErroreIntegrazione(),e,null)));
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
			org.openspcoop2.pdd.mdb.Imbustamento imbustamentoLib = null;
			openspcoopstate.setMessageLib(imbustamentoMSG);
			openspcoopstate.setIDMessaggioSessione(idMessageRequest);
			try {
				imbustamentoLib = new org.openspcoop2.pdd.mdb.Imbustamento(logCore);
				esito = imbustamentoLib.onMessage(openspcoopstate);
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
					this.msgContext.setMessageResponse((xmlBuilder.toMessage(ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.getErroreIntegrazione(),e,null)));
				}
				return false;
			}
	
			// Se la chiamata alla libreria imbustamento non ha dato errori procedo con le altre librerie
			// Altrimenti faccio gestire l'errore prodotto dalla libreria
			if (esito.getStatoInvocazione() != EsitoLib.OK) {
				// ripristino utilizzo connessione al database
				openspcoopstate.setUseConnection(true);
				gestioneRisposta(parametriGestioneRisposta);
				return false;
			}
	
			// Gestione oneway versione 11
			if (openspcoopstate.getDestinatarioResponseMsgLib() != null
					&& openspcoopstate.getDestinatarioResponseMsgLib().startsWith(RicezioneContenutiApplicativi.ID_MODULO)
					&& propertiesReader.isGestioneOnewayStateful_1_1())
				return true;
	
			/* ------------ Rilascio risorsa se e' presente rinegoziamento delle risorse ------------------ */
			// Rinegozio la connessione SOLO se siamo in oneway o sincrono stateless puro (non oneway11)
			if( rinegoziamentoConnessione ){
				openspcoopstate.setUseConnection(true);
				try{
					openspcoopstate.commit();
				}catch(Exception e){}
				openspcoopstate.releaseResource();
				openspcoopstate.setUseConnection(false);
			}
			
			
			
			
			
			/*
			 * ---------------------- INOLTRO BUSTE ------------------
			 */
	
			InoltroBuste inoltroBusteLib = null;
			try {
				inoltroBusteLib = new InoltroBuste(logCore);
				esito = inoltroBusteLib.onMessage(openspcoopstate);
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
					this.msgContext.setMessageResponse((xmlBuilder.toMessage(ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.getErroreIntegrazione(),e,null)));
				}
				return false;
			}
			
			if (esito.getStatoInvocazione() != EsitoLib.OK) {
				// ripristino utilizzo connessione al database
				openspcoopstate.setUseConnection(true);
				gestioneRisposta(parametriGestioneRisposta);
				return false;
			}
	
			// Gestione oneway versione 11
			if (openspcoopstate.getDestinatarioResponseMsgLib()!=null &&
					openspcoopstate.getDestinatarioResponseMsgLib().startsWith(
					RicezioneContenutiApplicativi.ID_MODULO))
				return true;
			
			
			
			
			
	
			/*--------------- SBUSTAMENTO RISPOSTE ---------------- */
	
			SbustamentoRisposte sbustamentoRisposteLib = null;
			boolean erroreSbustamentoRisposta = false;
			try {
				sbustamentoRisposteLib = new SbustamentoRisposte(logCore);
				/* Verifico che non abbia rilasciato la connessione, se si la riprendo */
				if( rinegoziamentoConnessione && openspcoopstate.resourceReleased()){
					openspcoopstate.setUseConnection(true);
					openspcoopstate.initResource(parametriGestioneRisposta.getIdentitaPdD(), SbustamentoRisposte.ID_MODULO, idTransazione);
					openspcoopstate.setUseConnection(false);
					// update states
					registroServiziReader.updateState(openspcoopstate.getStatoRichiesta(),openspcoopstate.getStatoRisposta());
					configurazionePdDReader.updateState(openspcoopstate.getStatoRichiesta(),openspcoopstate.getStatoRisposta());
					msgDiag.updateState(openspcoopstate.getStatoRichiesta(),openspcoopstate.getStatoRisposta());
				}
				esito = sbustamentoRisposteLib.onMessage(openspcoopstate);
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
					this.msgContext.setMessageResponse((xmlBuilder.toMessage(ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.getErroreIntegrazione(),e,null)));
				}
				return false;
			}finally{
				/* Se devo rinegoziare la connessione, la rilascio */
				if( (rinegoziamentoConnessione) && (erroreSbustamentoRisposta==false) ){
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
				return false;
			}
			
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
		ErroreApplicativoBuilder xmlBuilder = parametriGestioneRisposta.getXmlBuilder();
		
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
		String idTransazione = PdDContext.getValue(org.openspcoop2.core.constants.Costanti.CLUSTER_ID, pddContext);
		
		IProtocolFactory protocolFactory = parametriGestioneRisposta.getProtocolFactory();
		
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
		SOAPVersion versioneSoap = (SOAPVersion) this.msgContext.pddContext.getObject(org.openspcoop2.core.constants.Costanti.SOAP_VERSION);
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
				if(responseMessage!=null)
					versioneSoap = responseMessage.getVersioneSoap();
				
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
					responseMessage = SoapUtils.build_Soap_Empty(versioneSoap);

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
							this.msgContext.setMessageResponse((xmlBuilder.toMessage(ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
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

					this.msgContext.setMessageResponse((xmlBuilder.toMessage(ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
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
					this.msgContext.setMessageResponse((xmlBuilder.toMessage(ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
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
					this.msgContext.setMessageResponse((xmlBuilder.toMessage(ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
							get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_511_READ_RESPONSE_MSG), e,
							((responseMessage!=null && responseMessage.getParseException()!=null)?responseMessage.getParseException():null))));
					return;
				}

			}
		} catch (Exception e) {
			logCore.error("ErroreGenerale", e);
			msgDiag.logErroreGenerico(e,"ErroreGenerale");
			openspcoopstate.releaseResource();
			this.msgContext.setMessageResponse((xmlBuilder.toMessage(ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.getErroreIntegrazione(), e,
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
		java.util.Properties propertiesIntegrazioneRisposta = new java.util.Properties();
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
									responseMessage = this.fac.createEmptySOAPMessage(versioneSoap);
									outResponsePDMessage.setMessage(responseMessage);
								}
								gestore.setOutResponseHeader(headerIntegrazioneRisposta,outResponsePDMessage);
							}else{
								// gia effettuato l'update dell'header in InoltroBuste
							}
						}else{
							if(responseMessage==null){
								responseMessage = this.fac.createEmptySOAPMessage(versioneSoap);
								outResponsePDMessage.setMessage(responseMessage);
							}
							gestore.setOutResponseHeader(headerIntegrazioneRisposta,outResponsePDMessage);
						}
					}else{
						gestore.setOutResponseHeader(headerIntegrazioneRisposta,outResponsePDMessage);
					}
				} else {
					msgDiag.logErroreGenerico("Creazione header di integrazione ["+ tipiIntegrazionePD[i]+ "] non riuscito, gestore non inzializzato","setHeaderIntegrazioneRisposta");
				}
			} catch (Exception e) {
				logCore.error("Errore durante la creazione dell'header di integrazione ["+ tipiIntegrazionePD[i] + "]: "+ e.getMessage(), e);
				msgDiag.logErroreGenerico(e,"setHeaderIntegrazioneRisposta("+tipiIntegrazionePD[i]+")");
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
				this.msgContext.setMessageResponse((xmlBuilder.toMessage(ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.getErroreIntegrazione(), e,
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
				this.msgContext.setMessageResponse((xmlBuilder.toMessage(ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.getErroreIntegrazione(), e,
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
