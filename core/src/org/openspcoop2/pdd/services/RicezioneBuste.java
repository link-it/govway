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
import java.sql.Timestamp;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;

import org.slf4j.Logger;
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.TipoFiltroAbilitazioneServizi;
import org.openspcoop2.core.config.ValidazioneContenutiApplicativi;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import org.openspcoop2.core.config.driver.DriverConfigurazioneException;
import org.openspcoop2.core.config.driver.DriverConfigurazioneNotFound;
import org.openspcoop2.core.constants.TipoPdD;
import org.openspcoop2.core.eccezione.details.DettaglioEccezione;
import org.openspcoop2.core.eccezione.details.utils.XMLUtils;
import org.openspcoop2.core.id.IDPortaApplicativa;
import org.openspcoop2.core.id.IDPortaApplicativaByNome;
import org.openspcoop2.core.id.IDPortaDelegata;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.constants.CostantiRegistroServizi;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.OpenSPCoop2MessageFactory;
import org.openspcoop2.message.SOAPVersion;
import org.openspcoop2.message.SoapUtils;
import org.openspcoop2.message.mtom.MtomXomReference;
import org.openspcoop2.pdd.config.ClassNameProperties;
import org.openspcoop2.pdd.config.ConfigurazionePdDManager;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.config.RichiestaApplicativa;
import org.openspcoop2.pdd.config.RichiestaDelegata;
import org.openspcoop2.pdd.core.AbstractCore;
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
import org.openspcoop2.pdd.core.autenticazione.Credenziali;
import org.openspcoop2.pdd.core.autenticazione.GestoreCredenzialiConfigurationException;
import org.openspcoop2.pdd.core.autenticazione.IGestoreCredenziali;
import org.openspcoop2.pdd.core.autorizzazione.GestoreAutorizzazione;
import org.openspcoop2.pdd.core.autorizzazione.pa.DatiInvocazionePortaApplicativa;
import org.openspcoop2.pdd.core.autorizzazione.pa.EsitoAutorizzazioneCooperazione;
import org.openspcoop2.pdd.core.autorizzazione.pa.IAutorizzazioneContenutoPortaApplicativa;
import org.openspcoop2.pdd.core.connettori.InfoConnettoreIngresso;
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
import org.openspcoop2.pdd.core.state.IOpenSPCoopState;
import org.openspcoop2.pdd.core.state.OpenSPCoopState;
import org.openspcoop2.pdd.core.state.OpenSPCoopStateException;
import org.openspcoop2.pdd.core.state.OpenSPCoopStateful;
import org.openspcoop2.pdd.core.state.OpenSPCoopStateless;
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
import org.openspcoop2.pdd.services.connector.ConnectorInMessage;
import org.openspcoop2.pdd.services.connector.DirectVMProtocolInfo;
import org.openspcoop2.pdd.timers.TimerGestoreMessaggi;
import org.openspcoop2.pdd.timers.TimerMonitoraggioRisorse;
import org.openspcoop2.pdd.timers.TimerThreshold;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.engine.URLProtocolContext;
import org.openspcoop2.protocol.engine.builder.Imbustamento;
import org.openspcoop2.protocol.engine.constants.Costanti;
import org.openspcoop2.protocol.engine.constants.IDService;
import org.openspcoop2.protocol.engine.driver.History;
import org.openspcoop2.protocol.engine.driver.IFiltroDuplicati;
import org.openspcoop2.protocol.engine.driver.ProfiloDiCollaborazione;
import org.openspcoop2.protocol.engine.driver.RepositoryBuste;
import org.openspcoop2.protocol.engine.mapping.InformazioniServizioURLMapping;
import org.openspcoop2.protocol.engine.validator.Validatore;
import org.openspcoop2.protocol.registry.RegistroServiziManager;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.Eccezione;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.Integrazione;
import org.openspcoop2.protocol.sdk.ProtocolException;
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
import org.openspcoop2.protocol.sdk.constants.FunzionalitaProtocollo;
import org.openspcoop2.protocol.sdk.constants.Inoltro;
import org.openspcoop2.protocol.sdk.constants.LivelloRilevanza;
import org.openspcoop2.protocol.sdk.constants.RuoloBusta;
import org.openspcoop2.protocol.sdk.constants.StatoFunzionalitaProtocollo;
import org.openspcoop2.protocol.sdk.constants.TipoOraRegistrazione;
import org.openspcoop2.protocol.sdk.constants.TipoTraccia;
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
import org.openspcoop2.utils.Identity;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.resources.Loader;


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
	
	/** Indicazione se il servizio PA risulta attivo */
	public static Boolean isActivePAService = true;
	public static List<TipoFiltroAbilitazioneServizi> listaAbilitazioniPAService = null;
	public static List<TipoFiltroAbilitazioneServizi> listaDisabilitazioniPAService = null;

	/** INodeSender */
	//private static INodeSender nodeSender = null;
	// E' stato aggiunto lo stato dentro l'oggetto.

	/** INodeReceiver */
	//private static INodeReceiver nodeReceiver = null;
	// E' stato aggiunto lo stato dentro l'oggetto.

	/** IGestoreIntegrazionePA: lista di gestori, ordinati per priorita' minore */
	//private static java.util.Hashtable<String, IGestoreIntegrazionePA> gestoriIntegrazionePA = new java.util.Hashtable<String, IGestoreIntegrazionePA>();
	// E' stato aggiunto lo stato dentro l'oggetto.
	// Sono public perchè utilizzati da altre implementazioni come ad es. AdapterJMS 
	public static String[] defaultGestoriIntegrazionePA = null;
	public static Hashtable<String, String[]> defaultPerProtocolloGestoreIntegrazionePA = null;
	
	
	/** Factory per la creazione di messaggi SOAP */
	private  OpenSPCoop2MessageFactory fac = OpenSPCoop2MessageFactory.getMessageFactory();
	

	/** IAutorizzazione: lista di gestori per l'autorizzazione per contenuto delle buste */
	//private static java.util.Hashtable<String, IAutorizzazioneContenutoBuste> gestoriAutorizzazioneContenutoBuste = new java.util.Hashtable<String, IAutorizzazioneContenutoBuste>();
	// E' stato aggiunto lo stato dentro l'oggetto.
	
	/** IGestoreCredenziali: lista di gestori delle credenziali */
	//private static java.util.Hashtable<String, IGestoreCredenziali> gestoriCredenziali = new java.util.Hashtable<String, IGestoreCredenziali>();
	// E' stato aggiunto lo stato dentro l'oggetto.
	private static String [] tipiGestoriCredenziali = null;
	
	/**
	 * Inizializzatore del servizio RicezioneBuste
	 * 
	 * @throws Exception
	 */
	protected synchronized static void initializeService(ConfigurazionePdDManager configReader,ClassNameProperties className,OpenSPCoop2Properties propertiesReader,Logger logCore) throws Exception{
		if(RicezioneBuste.initializeService)
			return; // inizializzato da un altro thread

		Loader loader = Loader.getInstance();

		// Inizializzo stato del servizio PA
		RicezioneBuste.isActivePAService = configReader.isPAServiceActive();
		RicezioneBuste.listaAbilitazioniPAService = configReader.getFiltriAbilitazionePAService();
		RicezioneBuste.listaDisabilitazioniPAService = configReader.getFiltriDisabilitazionePAService();
		
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
		//Vector<IGestoreIntegrazionePA> v = new Vector<IGestoreIntegrazionePA>();
		Vector<String> s = new Vector<String>();
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
//			RicezioneBuste.gestoriIntegrazionePA = new Hashtable<String, IGestoreIntegrazionePA>();
//			while(s.size()>0){
//				RicezioneBuste.gestoriIntegrazionePA.put(s.remove(0), v.remove(0));
//			}
		}
		
		// Inizializzo IGestoreIntegrazionePA per protocollo
		RicezioneBuste.defaultPerProtocolloGestoreIntegrazionePA = new Hashtable<String, String[]>();
		Enumeration<String> enumProtocols = ProtocolFactoryManager.getInstance().getProtocolNames();
		while (enumProtocols.hasMoreElements()) {
			String protocol = (String) enumProtocols.nextElement();
			String [] tipiIntegrazionePA = propertiesReader.getTipoIntegrazionePA(protocol);
			if(tipiIntegrazionePA!=null && tipiIntegrazionePA.length>0){
				Vector<String> tipiIntegrazionePerProtocollo = new Vector<String>();
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


	/**
	 * Aggiorna la lista dei GestoreIntegrazionePA
	 * 
	 * @throws Exception
	 */
// Aggiunto lo stato
//	private synchronized static boolean aggiornaListaGestoreIntegrazione(
//			String newTipo, ClassNameProperties className,
//			OpenSPCoop2Properties propertiesReader, Logger logCore)
//	throws Exception {
//		if (RicezioneBuste.gestoriIntegrazionePA.contains(newTipo) )
//			return true; // inizializzato da un altro thread
//
//		Loader loader = Loader.getInstance();
//		
//		// Inizializzo IGestoreIntegrazionePD new Type
//		String classType = className.getIntegrazionePortaApplicativa(newTipo);
//		try {
//			IGestoreIntegrazionePA test = (IGestoreIntegrazionePA)loader.newInstance(classType);
//			RicezioneBuste.gestoriIntegrazionePA.put(newTipo,((IGestoreIntegrazionePASoap)test));
//			logCore	.info("Inizializzazione gestore per lettura integrazione PA di tipo "
//					+ newTipo	+ " effettuata.");
//			return true;
//		} catch (Exception e) {
//			throw new Exception(
//					"Riscontrato errore durante il caricamento della classe ["+ classType
//					+ "] da utilizzare per la gestione dell'integrazione di tipo ["+ newTipo + "]: " + e.getMessage());
//		}
//	}


	/**
	 * Aggiorna la lista dei Gestore per l'autorizzazione per contenuto buste
	 * 
	 * @throws Exception
	 */
// Aggiunto lo stato
//	private synchronized static void aggiornaListaGestoreAutorizzazioneContenutoBuste(
//			String newTipo, ClassNameProperties className,
//			OpenSPCoop2Properties propertiesReader, Logger logCore)
//			throws Exception {
//		if (RicezioneBuste.gestoriAutorizzazioneContenutoBuste.contains(newTipo))
//			return; // inizializzato da un altro thread
//
//		Loader loader = Loader.getInstance();
//		
//		// Inizializzo IGestoreAutorizzazione new Type
//		String classType = className.getAutorizzazioneContenutoBuste(newTipo);
//		try {
//			RicezioneBuste.gestoriAutorizzazioneContenutoBuste.put(newTipo,((IAutorizzazioneContenutoBuste) loader.newInstance(classType)));
//			logCore.info("Inizializzazione gestore autorizzazione contenuto buste di tipo "+ newTipo + " effettuata.");
//		} catch (Exception e) {
//			throw new Exception(
//					"Riscontrato errore durante il caricamento della classe ["+ classType
//							+ "] da utilizzare per la gestione dell'autorizzazione contenuto buste di tipo ["+ newTipo + "]: " + e.getMessage());
//		}
//	}
	


	/** Contesto della richiesta */
	private RicezioneBusteContext msgContext;

	/** Modalita' di gestione del Messaggio */
	boolean responseAsByte = false;

	public RicezioneBuste(RicezioneBusteContext context){
		this.msgContext = context;
	}


	public void process(Object ... params){

		
		

		// ------------- dati generali -----------------------------

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
			try{
				requestMessage = this.msgContext.getMessageRequestFromByte();
				this.responseAsByte = true;
			}catch(Exception e){
				setSOAPFault_processamento(logCore, msgDiag, e, "LetturaMessaggioRichiesta");
				return;
			}
		}
		
		
		
		
		
		// ------------- in-handler -----------------------------
		
		IProtocolFactory protocolFactory = null;
		try{
			protocolFactory = ProtocolFactoryManager.getInstance().getProtocolFactoryByName((String)this.msgContext.getPddContext().getObject(org.openspcoop2.core.constants.Costanti.PROTOCOLLO));
		}catch(Exception e){
			setSOAPFault_processamento(logCore, msgDiag, e, "ProtocolFactoryInstance");
			return;
		}
		InRequestContext inRequestContext = new InRequestContext(logCore,protocolFactory);
		// TipoPorta
		inRequestContext.setTipoPorta(TipoPdD.APPLICATIVA);
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
		// PddContext
		inRequestContext.setPddContext(this.msgContext.getPddContext());
		// Dati Messaggio
		inRequestContext.setMessaggio(requestMessage);
		// Invoke handler
		try{
			GestoreHandlers.inRequest(inRequestContext, msgDiag, logCore);
		}catch(HandlerException e){
			setSOAPFault_processamento(logCore,msgDiag, e, e.getIdentitaHandler());
			return;
		}catch(Exception e){
			setSOAPFault_processamento(logCore,msgDiag, e, "InvocazioneInRequestHandler");
			return;
		}

		
		
		
		
		
		
		// ------------- process -----------------------------
		try{
			process_engine(inRequestContext,params);
		} catch(TracciamentoException tracciamentoException){
			setSOAPFault_processamento(logCore,msgDiag, tracciamentoException, "TracciamentoNonRiuscito");
			return;
		} catch(ProtocolException protocolException){
			setSOAPFault_processamento(logCore,msgDiag, protocolException, "ProtocolFactoryNonInstanziata");
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
			setSOAPFault_processamento(logCore,msgDiag, e, "FinalizeIntegrationContextRicezioneBuste");
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
			setSOAPFault_processamento(logCore,msgDiag, e, e.getIdentitaHandler());
			return;
		}catch(Exception e){
			setSOAPFault_processamento(logCore,msgDiag, e, "InvocazioneOutResponseHandler");
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
			setSOAPFault_processamento(logCore,msgDiag, e, "FineGestioneRicezioneBuste");
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
			}catch(TracciamentoException tracciamentoException){
				setSOAPFault_processamento(logCore,msgDiag, tracciamentoException, "TracciamentoNonRiuscito");
				return;
			}catch(Exception e){
				// Se non riesco ad accedere alla configurazione sicuramente gia' nel messaggio di risposta e' presente l'errore di PdD non correttamente inizializzata
			}
		}

	}
	private void setSOAPFault_processamento(Logger logCore, MsgDiagnostico msgDiag, Exception e, String posizione){
		setSOAPFault_engine(logCore, msgDiag, e, null, null, posizione, false);
	}
	private void setSOAPFault_processamento(String posizione){
		setSOAPFault_engine(null, null, null, null, null, posizione, false);
	}
	private void setSOAPFault_intestazione(ErroreCooperazione erroreCooperazione){
		setSOAPFault_engine(null, null, null, erroreCooperazione, null, null, true);
	}
	private void setSOAPFault_intestazione(ErroreIntegrazione erroreIntegrazione){
		setSOAPFault_engine(null, null, null, null, erroreIntegrazione, null, true);
	}
	
	private void setSOAPFault_engine(Logger logCore, MsgDiagnostico msgDiag, Exception e, 
			ErroreCooperazione erroreCooperazione, ErroreIntegrazione erroreIntegrazione, String posizioneErrore, 
			boolean validazione) {
		
		boolean setSoapPrefixBackwardCompatibilityOpenSPCoop1 = true;
		try{
			setSoapPrefixBackwardCompatibilityOpenSPCoop1 = OpenSPCoop2Properties.getInstance().isForceSoapPrefixCompatibilitaOpenSPCoopV1(); 
		}catch(Exception eReader){}
		
		HandlerException he = null;
		if(e!=null && (e instanceof HandlerException)){
			he = (HandlerException) e;
		}
		
		SOAPVersion versioneSoap = (SOAPVersion) this.msgContext.getPddContext().getObject(org.openspcoop2.core.constants.Costanti.SOAP_VERSION);
		if(msgDiag!=null){
			if(he!=null){
				if(he.isEmettiDiagnostico()){
					msgDiag.logErroreGenerico(e, posizioneErrore);
				}
			}else{
				msgDiag.logErroreGenerico(e, posizioneErrore);
			}
		}
		else if(logCore!=null){
			logCore.error(posizioneErrore+e.getMessage(),e);
		}
		if (this.msgContext.isGestioneRisposta()) {
			OpenSPCoop2Message messageFault = null;
			try{
				IProtocolFactory protocolFactory = ProtocolFactoryManager.getInstance().getProtocolFactoryByName((String) this.msgContext.getPddContext().getObject(org.openspcoop2.core.constants.Costanti.PROTOCOLLO));
				
				Imbustamento imbustamentoErroreBuilder = new Imbustamento(logCore, protocolFactory);
				if(validazione){
					if(erroreCooperazione != null)
						messageFault = imbustamentoErroreBuilder.buildSoapFaultProtocollo_intestazione(this.msgContext.getIdentitaPdD(), this.msgContext.getTipoPorta(),
								this.msgContext.getIdModulo(), 
								erroreCooperazione.getCodiceErrore(), erroreCooperazione.getDescrizione(protocolFactory), 
								versioneSoap, setSoapPrefixBackwardCompatibilityOpenSPCoop1);
					else
						messageFault = imbustamentoErroreBuilder.buildSoapFaultProtocollo_intestazione(this.msgContext.getIdentitaPdD(), this.msgContext.getTipoPorta(),
								this.msgContext.getIdModulo(), 
								erroreIntegrazione, 
								versioneSoap, setSoapPrefixBackwardCompatibilityOpenSPCoop1);
				}else{
					if(e!=null){
						ErroreIntegrazione erroreIntegrazioneGenerato = null;
						if(he!=null && he.isSetErrorMessageInFault()){
							erroreIntegrazioneGenerato = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.get5XX_ErroreProcessamento(he.getMessage());
						}else{
							erroreIntegrazioneGenerato = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.get5XX_ErroreProcessamento(posizioneErrore);
						}
						messageFault = imbustamentoErroreBuilder.buildSoapFaultProtocollo_processamento(this.msgContext.getIdentitaPdD(), this.msgContext.getTipoPorta(),
								this.msgContext.getIdModulo(), 
								erroreIntegrazioneGenerato,
								e, versioneSoap, setSoapPrefixBackwardCompatibilityOpenSPCoop1);
					}else{
						messageFault = imbustamentoErroreBuilder.buildSoapFaultProtocollo_processamento(this.msgContext.getIdentitaPdD(), this.msgContext.getTipoPorta(),
								this.msgContext.getIdModulo(), 
								ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.get5XX_ErroreProcessamento(posizioneErrore),
								versioneSoap, setSoapPrefixBackwardCompatibilityOpenSPCoop1);
					}
				}
			} catch(ProtocolException protocolException){
				//TODO cosa fare se non recuperla il protocollo?
				protocolException.printStackTrace();
			}
			if (this.responseAsByte) {
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				try{
					messageFault.writeTo(out,false);
				}catch(Exception eWriteTo){
					msgDiag.logErroreGenerico(e, "GenerazioneFaultAsBytes");
				}
				this.msgContext.setMessageRequestAsByte(out.toByteArray());
			}
			// Lo imposto sempre, perche' puo' servire nell'handler
			this.msgContext.setMessageResponse(messageFault);
		}
	}
	
	
	
	private void process_engine(InRequestContext inRequestContext,Object ... params) throws TracciamentoException, ProtocolException {

		/* ------------ Lettura parametri della richiesta ------------- */
	
		// Messaggio di ingresso
		OpenSPCoop2Message requestMessage = inRequestContext.getMessaggio();
		
		// Logger
		Logger logCore = inRequestContext.getLogCore();
		
		// Data Ingresso Messaggio
		Timestamp dataIngressoMessaggio = new Timestamp(this.msgContext.getDataIngressoRichiesta().getTime());
		
		// ID Transazione
		String idTransazione = PdDContext.getValue(org.openspcoop2.core.constants.Costanti.CLUSTER_ID, inRequestContext.getPddContext());
		
		// Loader
		Loader loader = Loader.getInstance();
		
		
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
			setSOAPFault_processamento("ErroreInizializzazioneOpenSPCoop");
			return;
		}
		if( TimerMonitoraggioRisorse.risorseDisponibili == false){
			String msgErrore = "Risorse di sistema non disponibili: "+ TimerMonitoraggioRisorse.risorsaNonDisponibile.getMessage();
			logCore.error("["+RicezioneBuste.ID_MODULO+"]  "+msgErrore,TimerMonitoraggioRisorse.risorsaNonDisponibile);
			try{
				// provo ad emetter un diagnostico
				if(this.msgContext.getMsgDiagnostico()!=null){
					this.msgContext.getMsgDiagnostico().logErroreGenerico(msgErrore,"InizializzazioneRisorsePdD");
				}
			}catch(Throwable t){logCore.error("Emissione diagnostico per errore inizializzazione non riuscita: "+t.getMessage(),t);}
			setSOAPFault_processamento("RisorseSistemaNonDisponibili");
			return;
		}
		if( TimerThreshold.freeSpace == false){
			String msgErrore = "Non sono disponibili abbastanza risorse per la gestione della richiesta";
			logCore.error("["+RicezioneBuste.ID_MODULO+"]  "+msgErrore);
			try{
				// provo ad emetter un diagnostico
				if(this.msgContext.getMsgDiagnostico()!=null){
					this.msgContext.getMsgDiagnostico().logErroreGenerico(msgErrore,"DisponibilitaRisorsePdD");
				}
			}catch(Throwable t){logCore.error("Emissione diagnostico per errore inizializzazione non riuscita: "+t.getMessage(),t);}
			setSOAPFault_processamento("RisorseSistemaLivelloCritico");
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
			setSOAPFault_processamento("TracciaturaNonDisponibile");
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
			setSOAPFault_processamento("DiagnosticaNonDisponibile");
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
			setSOAPFault_processamento("SistemaDumpNonDisponibile");
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
			setSOAPFault_processamento("RefreshConfigurazioneNonRiuscito");
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
			setSOAPFault_processamento("RefreshRegistroServiziNonRiuscito");
			return;
		}





		/* ------------- Inizializzazione Risorse  ------------------- */

		// Credenziali utilizzate nella richiesta
		Credenziali credenziali = this.msgContext.getCredenziali();


		// OpenSPCoop Properties
		OpenSPCoop2Properties propertiesReader = OpenSPCoop2Properties.getInstance();
		
		// Classi registrate
		ClassNameProperties className = ClassNameProperties.getInstance();

		// protocollo
		String protocollo = (String) this.msgContext.getPddContext().getObject(org.openspcoop2.core.constants.Costanti.PROTOCOLLO);
		
		//	IdentificativoPdD
		IDSoggetto identitaPdD = propertiesReader.getIdentitaPortaDefault(protocollo);

		
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
		IProtocolFactory protocolFactory = ProtocolFactoryManager.getInstance().getProtocolFactoryByName(protocollo);
		PdDContext pddContext = inRequestContext.getPddContext();
		ITraduttore traduttore = protocolFactory.createTraduttore();
		
		//	Logger dei messaggi diagnostici
		MsgDiagnostico msgDiag = new MsgDiagnostico(identitaPdD,this.msgContext.getIdModulo());
		this.msgContext.setMsgDiagnostico(msgDiag); // aggiorno msg diagnostico
		msgDiag.setPddContext(inRequestContext.getPddContext(), protocolFactory);
		msgDiag.setPrefixMsgPersonalizzati(MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_BUSTE);
		if(inRequestContext.getConnettore()!=null && inRequestContext.getConnettore().getUrlProtocolContext()!=null){
			msgDiag.setPorta(inRequestContext.getConnettore().getUrlProtocolContext().getFunctionParameters() + "_urlInvocazione("+ inRequestContext.getConnettore().getUrlProtocolContext().getUrlInvocazione_formBased() + ")");
		}
			
		// Parametri della porta applicativa invocata
		URLProtocolContext urlProtocolContext = this.msgContext.getUrlProtocolContext();
		
		// setCredenziali
		setCredenziali(credenziali, msgDiag);
		
		// Imbustatore
		org.openspcoop2.protocol.engine.builder.Imbustamento imbustatore = new Imbustamento(protocolFactory);

		// inizializzazione risorse statiche
		try{
			if(RicezioneBuste.initializeService==false){
				msgDiag.mediumDebug("Inizializzazione risorse statiche...");
				RicezioneBuste.initializeService(configurazionePdDReader, className, propertiesReader,logCore);
			}
		}catch(Exception e){
			setSOAPFault_processamento(logCore,msgDiag,e,"InizializzazioneRisorseServizioRicezioneBuste");
			return;
		}
		
		// Imposto header di risposta
		Properties headerRisposta = new Properties();
		UtilitiesIntegrazione utilitiesHttp = UtilitiesIntegrazione.getInstance(logCore);
		try{
			utilitiesHttp.setResponseTransportProperties(null, headerRisposta, null);
		}catch(Exception e){
			setSOAPFault_processamento(logCore,msgDiag,e,"InizializzazioneHeaderRisposta");
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
			setSOAPFault_processamento(logCore,msgDiag,e,"initDatabaseResource");
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
		
		parametriGenerazioneBustaErrore.setImbustatore(imbustatore);

		
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
		
		

		
		
		// ------------- Dump richiesta-----------------------------
		if (configurazionePdDReader.dumpMessaggi()) {
			Dump dumpApplicativo = new Dump(identitaPdD,this.msgContext.getIdModulo(), this.msgContext.getTipoPorta(),inRequestContext.getPddContext(),
					openspcoopstate.getStatoRichiesta(),openspcoopstate.getStatoRisposta());
			dumpApplicativo.dumpRichiestaIngresso(requestMessage,inRequestContext.getConnettore());
		}
		
		
		
		
		
		
		
		
		
		
		
		


		/* ------------  URL Mapping ------------- */	

		InformazioniServizioURLMapping is = null;
		PortaApplicativa pa = null;
		IDServizio idServizio = null;
		Busta bustaURLMapping = null;
		try {
			is = new InformazioniServizioURLMapping(requestMessage,protocolFactory,urlProtocolContext,
					registroServiziReader,logCore, this.msgContext.getIdModuloAsIDService());
			logCore.debug("InformazioniServizioTramiteURLMapping: "+is.toString());		
		} catch (ProtocolException e) {
			msgDiag.addKeyword(CostantiPdD.KEY_ERRORE_PROCESSAMENTO, e.getMessage());
			logCore.error(msgDiag.getMessaggio_replaceKeywords(MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_BUSTE,"identificazionePAErrore"),e);
			msgDiag.logPersonalizzato(MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_BUSTE,"identificazionePAErrore");
			setSOAPFault_intestazione(ErroriIntegrazione.ERRORE_450_PA_INESISTENTE.getErroreIntegrazione());
			openspcoopstate.releaseResource();
			return;
		}
		
		try{
		
			Identity identity = null;
			
			// Read Porta Applicativa
			if(is.existsPABasedIdentificationMode()){
				
				String nomePA = null;
				String tipoProprietario = null;
				String nomeProprietario = null;
				List<PortaApplicativa> listaPA = null;
				boolean nomePAEsattamenteUgualeURLInvocata = false;
				try {
					nomePA = is.getPaInfo().getNomePA();
					tipoProprietario = is.getPaInfo().getTipoSoggetto();
					nomeProprietario = is.getPaInfo().getNomeSoggetto();
					if(is.getPaInfo().getAzione()!=null){
						try{
							// prima provo a cercare la PA con anche l'azione finale.
							listaPA = configurazionePdDReader.getPorteApplicative(nomePA+"/"+is.getPaInfo().getAzione(), tipoProprietario, nomeProprietario);
							nomePAEsattamenteUgualeURLInvocata = true;
						}catch(DriverConfigurazioneNotFound dNotFoud){}
					}
					if(listaPA==null){
						listaPA = configurazionePdDReader.getPorteApplicative(nomePA, tipoProprietario, nomeProprietario);
					}
				} catch (Exception e) {
					
					// Nel caso di API provo ad utilizzare lo stesso algoritmo della PD scendendo a ritroso con gli '/'
					if(IDService.PORTA_APPLICATIVA_API.equals(this.msgContext.getIdModuloAsIDService())){
						if(nomePA.contains("/")){
							String tmpNome = new String(nomePA);
							while(tmpNome.contains("/")){
								
								int indexCut = -1;
								for(int i=(tmpNome.length()-1); i>=0 ;i--){
									if(tmpNome.charAt(i) == '/'){
										indexCut = i;
										break;
									}
								}
								tmpNome = tmpNome.substring(0,indexCut);
								
								try{
									listaPA = configurazionePdDReader.getPorteApplicative(tmpNome, tipoProprietario, nomeProprietario);
								}catch(DriverConfigurazioneNotFound dNotFoud){}
								if(listaPA!=null && listaPA.size()>0){
									break;
								}
								
							}
						}
					}
					if(listaPA==null || listaPA.size()<=0){
						String nomeError = nomePA;
						if(is.getPaInfo().getAzione()!=null){
							nomeError = nomeError +"[/"+is.getPaInfo().getAzione()+"]";
						}
						throw new ProtocolException("Impossibile trovare la Porta Applicativa ( "+nomeError+" ) nella configurazione: "+e.getMessage(), e);
					}
				}
				
				if(listaPA.size()>1){
					if(tipoProprietario==null){
						tipoProprietario="*";
					}
					if(nomeProprietario==null){
						nomeProprietario="*";
					}
					String nomeError = nomePA;
					if(is.getPaInfo().getAzione()!=null){
						nomeError = nomeError +"[/"+is.getPaInfo().getAzione()+"]";
					}
					throw new ProtocolException("Identificate piu' di una Porta Applicativa con i seguenti filtri: nomePA( "+nomeError+" ) tipoSoggetto( "+tipoProprietario+" ) nomeSoggetto( "+nomeProprietario+" )");
				}
				pa = listaPA.get(0);
				if(nomePAEsattamenteUgualeURLInvocata){
					is.getPaInfo().setNomePA(pa.getNome());
					is.getPaInfo().setAzione(null);
				}
			}
			
			// Read Identity
			if(is.existsIdentityBasedIdentificationMode()){
				if(connectorInMessage!=null)
					identity = connectorInMessage.getIdentity();
			}
			
			// Refresh dati su mittente e destinatario
			IDSoggetto idSoggettoFruitore = new IDSoggetto();
			IDSoggetto idSoggettoErogatore = new IDSoggetto();
			idServizio = new IDServizio();
			idServizio.setSoggettoErogatore(idSoggettoErogatore);
			is.refreshDati(idSoggettoFruitore, idServizio, this.msgContext.getSoapAction(), identity, pa);
			
			// Reimposto a null se il refresh non ha trovato dati.
			if(idSoggettoFruitore.getTipo()==null && idSoggettoFruitore.getNome()==null){
				idSoggettoFruitore = null;
			}			
			
			// Aggiorno domini dei soggetti se completamente ricostruiti tramite url mapping differente da plugin based
			String nomeRegistroForSearch = null; // qualsiasi registro
			if(idSoggettoFruitore!=null && idSoggettoFruitore.getTipo()!=null && idSoggettoFruitore.getNome()!=null){
				try {
					idSoggettoFruitore.setCodicePorta(registroServiziReader.getDominio(idSoggettoFruitore, nomeRegistroForSearch, protocolFactory));
				} catch (Exception e) {
					//e.printStackTrace();
				}
			}
			if(idSoggettoErogatore.getTipo()!=null && idSoggettoErogatore.getNome()!=null){
				try {
					idSoggettoErogatore.setCodicePorta(registroServiziReader.getDominio(idSoggettoErogatore, nomeRegistroForSearch, protocolFactory));
				} catch (Exception e) {
					//e.printStackTrace();
				}
			}
			
			// InfoServizio
			Servizio infoServizio = null;
			if(is.isStaticBasedIdentificationMode_InfoProtocol()){
				// devono essere stati definiti almeno i parametri necessari ad identificare il servizio.
				String motivoErrore = null;
				if(idServizio==null || idServizio.getServizio()==null){
					motivoErrore = "servizio";
				}
				else if(idServizio.getTipoServizio()==null){
					motivoErrore = "tipoServizio";
				}
				else if(idServizio.getSoggettoErogatore()==null || idServizio.getSoggettoErogatore().getNome()==null){
					motivoErrore = "destinatario";
				}
				else if(idServizio.getSoggettoErogatore().getTipo()==null){
					motivoErrore = "tipoDestinatario";
				}
				if(motivoErrore!=null){
					throw new ProtocolException("L'utilizzo della modalita' 'static' per l'identificazione delle funzionalita' di protocollo richiede che i dati su servizio e destinatario (tipo e nome) vengano identificati tramite una modalita' differente da pluginBased");
				}
				
				infoServizio = registroServiziReader.getInfoServizio(idSoggettoFruitore, idServizio,nomeRegistroForSearch,true);

			}
			else{
				infoServizio = new Servizio();
			}
			
			// ID Protocollo
			String id = null;
			if(is.isStaticBasedIdentificationMode_IdProtocol()){
				
				// devono essere stati definiti almeno i parametri necessari ad identificare l'erogatore.
				String motivoErrore = null;
				if(idServizio.getSoggettoErogatore()==null || idServizio.getSoggettoErogatore().getNome()==null){
					motivoErrore = "destinatario";
				}
				else if(idServizio.getSoggettoErogatore().getTipo()==null){
					motivoErrore = "tipoDestinatario";
				}
				if(motivoErrore!=null){
					throw new ProtocolException("L'utilizzo della modalita' 'static' per la generazione dell'id di protocollo richiede che i dati sul destinatario (tipo e nome) vengano identificati tramite una modalita' differente da pluginBased");
				}
				
				Imbustamento imbustamento = new Imbustamento(protocolFactory);
				id = 
					imbustamento.buildID(openspcoopstate.getStatoRichiesta(),idSoggettoErogatore, 
							(String) this.msgContext.getPddContext().getObject(org.openspcoop2.core.constants.Costanti.CLUSTER_ID), 
							propertiesReader.getGestioneSerializableDB_AttesaAttiva(),
							propertiesReader.getGestioneSerializableDB_CheckInterval(),
							true);
			}
			
			// Build Busta
			bustaURLMapping = new Busta(protocolFactory,infoServizio, idSoggettoFruitore, idSoggettoErogatore, id, is.isGenerateListaTrasmissione());
			TipoOraRegistrazione tipoOraRegistrazione = propertiesReader.getTipoTempoBusta(null);
			bustaURLMapping.setTipoOraRegistrazione(tipoOraRegistrazione, traduttore.toString(tipoOraRegistrazione));
			if(bustaURLMapping.sizeListaTrasmissioni()>0){
				for (Trasmissione trasmissione : bustaURLMapping.getListaTrasmissioni()) {
					trasmissione.setTempo(tipoOraRegistrazione, traduttore.toString(tipoOraRegistrazione));
				}
			}
			
		}catch(Exception e){
			openspcoopstate.releaseResource();
			setSOAPFault_processamento(logCore,msgDiag,e,"readProtocolInfo");
			return;
		}
		if(pa!=null){
			msgDiag.setPorta(pa.getNome());
		}
		
		
		
		

		
		
		
		
		
		
		
		/* ------------  Processamento Busta Ricevuta ------------- */	
		
		// ValidazioneSintattica
		msgDiag.mediumDebug("Validazione busta ricevuta in corso...");
		ProprietaValidazione properties = new ProprietaValidazione();
		boolean readQualifiedAttribute = propertiesReader.isReadQualifiedAttribute(CostantiRegistroServizi.IMPLEMENTAZIONE_STANDARD);
		
		Validatore validatore = new Validatore(requestMessage,properties, openspcoopstate.getStatoRichiesta(),readQualifiedAttribute, protocolFactory);
		
		
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
					msgDiag.logPersonalizzato("ricezioneMessaggio");
				}

				if(this.msgContext.isTracciamentoAbilitato() && erroreIntestazione!=null){
					msgDiag.mediumDebug("Tracciamento busta di richiesta...");

					// Tracciamento richiesta
					Tracciamento tracciamento = new Tracciamento(identitaPdD,
							this.msgContext.getIdModulo(),
							inRequestContext.getPddContext(),
							this.msgContext.getTipoPorta(),
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
							Tracciamento.createLocationString(true,this.msgContext.getFromLocation()),
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
							this.msgContext.getTipoPorta(),
							openspcoopstate.getStatoRichiesta(),openspcoopstate.getStatoRisposta());
					
					parametriGenerazioneBustaErrore.setTracciamento(tracciamento);
					parametriGenerazioneBustaErrore.setBusta(erroreIntestazione);
					parametriGenerazioneBustaErrore.setError(erroreIntestazione.cloneListaEccezioni());
					OpenSPCoop2Message errorOpenSPCoopMsg = generaBustaErroreValidazione(parametriGenerazioneBustaErrore);
					
					// Nota: la bustaRichiesta e' stata trasformata da generaErroreValidazione
					
					parametriInvioBustaErrore.setOpenspcoopMsg(errorOpenSPCoopMsg);
					parametriInvioBustaErrore.setBusta(parametriGenerazioneBustaErrore.getBusta());
					sendRispostaBustaErrore(parametriInvioBustaErrore);
				}
			}else{
				try{
					msgDiag.addKeyword(CostantiPdD.KEY_ECCEZIONI, validatore.getErrore().getDescrizione(protocolFactory));
				}catch(Exception e){
					logCore.error("getDescrizione Error:"+e.getMessage(),e);
				}
				msgDiag.addKeyword(CostantiPdD.KEY_TIPO_VALIDAZIONE_BUSTA, "sintattica");
				msgDiag.logPersonalizzato(MsgDiagnosticiProperties.MSG_DIAG_SBUSTAMENTO,"validazioneNonRiuscita");
				setSOAPFault_intestazione(validatore.getErrore());
			}
			openspcoopstate.releaseResource();
			return;
		}
		IDSoggetto soggettoFruitore = validatore.getSoggettoMittente();
		idServizio = validatore.getIDServizio();
		SOAPElement soapHeaderElement = validatore.getHeaderProtocollo();
		
		//		 Aggiornamento Informazioni protocollo
		msgDiag.setIdMessaggioRichiesta(validatore.getBusta().getID());
		this.msgContext.setIdMessage(validatore.getBusta().getID());
		msgDiag.setFruitore(soggettoFruitore);
		msgDiag.setServizio(idServizio);
		msgDiag.setDelegata(false);
		msgDiag.addKeywords(validatore.getBusta(), true);
		parametriGenerazioneBustaErrore.setMsgDiag(msgDiag);
		parametriInvioBustaErrore.setMsgDiag(msgDiag);

		// PdD Function: router o normale PdD
		boolean functionAsRouter = false;
		msgDiag.mediumDebug("Esamina modalita' di ricezione (PdD/Router)...");
		boolean existsSoggetto = false;
		try{
			existsSoggetto = configurazionePdDReader.existsSoggetto(idServizio.getSoggettoErogatore());
		}catch(Exception e){
			openspcoopstate.releaseResource();
			setSOAPFault_processamento(logCore, msgDiag, e, "existsSoggetto("+idServizio.getSoggettoErogatore().toString()+")");
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
				openspcoopstate.releaseResource();
				setSOAPFault_processamento(logCore, msgDiag, e, "ErroreCheckFunzionalitaRouterAttiva");
				return;
			}		
			if(routerFunctionActive){
				functionAsRouter = true;	
				try{
					identitaPdD = configurazionePdDReader.getRouterIdentity(protocolFactory);
				}catch(Exception e){
					openspcoopstate.releaseResource();
					setSOAPFault_processamento(logCore, msgDiag, e, "ErroreRiconoscimentoIdentitaRouter");
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
				openspcoopstate.releaseResource();
				setSOAPFault_processamento(logCore, msgDiag, e, "ErroreRiconoscimentoIdentitaPdD");
				return;
			}
			identitaPdD = new IDSoggetto(idServizio.getSoggettoErogatore().getTipo(),
					idServizio.getSoggettoErogatore().getNome(),dominio);
		}
		if(functionAsRouter){
			this.msgContext.setTipoPorta(TipoPdD.ROUTER);
		}
		

		// Raccolta dati Busta Ricevuta
		parametriGenerazioneBustaErrore.setIdentitaPdD(identitaPdD);
		parametriInvioBustaErrore.setIdentitaPdD(identitaPdD);
		parametriInvioBustaErrore.setFunctionAsRouter(functionAsRouter);
		
		msgDiag.setDominio(identitaPdD); // imposto anche il dominio nel msgDiag
		parametriGenerazioneBustaErrore.setMsgDiag(msgDiag);
		parametriInvioBustaErrore.setMsgDiag(msgDiag);
		
		Tracciamento tracciamento = new Tracciamento(identitaPdD,
				this.msgContext.getIdModulo(),
				inRequestContext.getPddContext(),
				this.msgContext.getTipoPorta(),
				openspcoopstate.getStatoRichiesta(),openspcoopstate.getStatoRisposta());
		parametriGenerazioneBustaErrore.setTracciamento(tracciamento);
		
		Busta bustaRichiesta = validatore.getBusta();
		
		// VM ProtocolInfo (se siamo arrivati da un canale VM)
		if(pddContext!=null && bustaRichiesta!=null)
			DirectVMProtocolInfo.setInfoFromContext(pddContext, bustaRichiesta);
			
		// Se non impostati, imposto i domini
		org.openspcoop2.pdd.core.Utilities.refreshIdentificativiPorta(bustaRichiesta, propertiesReader.getIdentitaPortaDefault(protocollo), registroServiziReader, protocolFactory);
		if(soggettoFruitore != null){
			if(soggettoFruitore.getCodicePorta()==null){
				soggettoFruitore.setCodicePorta(bustaRichiesta.getIdentificativoPortaMittente());
			}
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
		this.msgContext.getProtocol().setErogatore(idServizio.getSoggettoErogatore());
		if(bustaRichiesta!=null){
			this.msgContext.getProtocol().setIndirizzoErogatore(bustaRichiesta.getIndirizzoDestinatario());
		}
		this.msgContext.getProtocol().setTipoServizio(idServizio.getTipoServizio());
		this.msgContext.getProtocol().setServizio(idServizio.getServizio());
		this.msgContext.getProtocol().setVersioneServizio(idServizio.getVersioneServizioAsInt());
		this.msgContext.getProtocol().setAzione(idServizio.getAzione());
		this.msgContext.getProtocol().setIdRichiesta(idMessageRequest);
		this.msgContext.getProtocol().setProfiloCollaborazione(bustaRichiesta.getProfiloDiCollaborazione(),bustaRichiesta.getProfiloDiCollaborazioneValue());
		this.msgContext.getProtocol().setCollaborazione(bustaRichiesta.getCollaborazione());




		
		
		
		
		
		

		
		/*
		 * ---------------- Verifico che il servizio di RicezioneBuste sia abilitato ---------------------
		 */
		if(StatoServiziPdD.isEnabled(RicezioneBuste.isActivePAService, 
				RicezioneBuste.listaAbilitazioniPAService, 
				RicezioneBuste.listaDisabilitazioniPAService, 
				soggettoFruitore, idServizio) == false){
			logCore.error("["+ RicezioneBuste.ID_MODULO+ "]  Servizio di ricezione buste disabilitato");
			msgDiag.logErroreGenerico("Servizio di ricezione buste disabilitato", "PA");
			// Tracciamento richiesta: non ancora registrata
			if(this.msgContext.isTracciamentoAbilitato()){
				EsitoElaborazioneMessaggioTracciato esitoTraccia = 
						EsitoElaborazioneMessaggioTracciato.getEsitoElaborazioneConErrore("["+ RicezioneBuste.ID_MODULO+ "]  Servizio di ricezione buste disabilitato");
				tracciamento.registraRichiesta(requestMessage,null,soapHeaderElement,bustaRichiesta,esitoTraccia,
						Tracciamento.createLocationString(true,this.msgContext.getFromLocation()),
						correlazioneApplicativa);
			}
			if(this.msgContext.isGestioneRisposta()){

				parametriGenerazioneBustaErrore.setBusta(bustaRichiesta);							
				parametriGenerazioneBustaErrore.setErroreIntegrazione(ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
						get5XX_ErroreProcessamento("["+ RicezioneBuste.ID_MODULO+ "]  Servizio di ricezione buste disabilitato", CodiceErroreIntegrazione.CODICE_551_PA_SERVICE_NOT_ACTIVE));

				OpenSPCoop2Message errorOpenSPCoopMsg = generaBustaErroreProcessamento(parametriGenerazioneBustaErrore,null);
				
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
			requestMessage = protocolFactory.createProtocolManager().updateOpenSPCoop2MessageRequest(requestMessage.getVersioneSoap(),requestMessage, bustaRichiesta);
		} catch (Exception e) {
			// Emetto log, non ancora emesso
			msgDiag.logPersonalizzato("ricezioneMessaggio");
			
			msgDiag.addKeyword(CostantiPdD.KEY_ERRORE_PROCESSAMENTO , "Aggiornamento messaggio fallito, "+e.getMessage() );
			msgDiag.logErroreGenerico(e,"ProtocolManager.updateOpenSPCoop2Message");
			logCore.error("ProtocolManager.updateOpenSPCoop2Message error: "+e.getMessage(),e);
			
			// Tracciamento richiesta: non ancora registrata
			if(this.msgContext.isTracciamentoAbilitato()){
				EsitoElaborazioneMessaggioTracciato esitoTraccia = 
						EsitoElaborazioneMessaggioTracciato.getEsitoElaborazioneConErrore("ProtocolManager.updateOpenSPCoop2Message, non riuscito: "+e.getMessage());
				tracciamento.registraRichiesta(requestMessage,null,soapHeaderElement,bustaRichiesta,esitoTraccia,
						Tracciamento.createLocationString(true,this.msgContext.getFromLocation()),
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
		RuoloBusta ruoloBustaRicevuta = null;
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
									Tracciamento.createLocationString(true,this.msgContext.getFromLocation()),
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
					msgDiag.logPersonalizzato("ricezioneMessaggio");
					
					msgDiag.logErroreGenerico(e,"checkPresenzaRichiestaRicevutaAsincronaAncoraInGestione");
					logCore.error("Controllo presenza richieste/ricevuteRichieste ancora in gestione " +
							"correlate alla risposta/richiesta-stato asincrona simmetrica/asimmetrica arrivata, non riuscito",e);
					// Tracciamento richiesta: non ancora registrata
					if(this.msgContext.isTracciamentoAbilitato()){
						EsitoElaborazioneMessaggioTracciato esitoTraccia = 
								EsitoElaborazioneMessaggioTracciato.getEsitoElaborazioneConErrore("Controllo presenza richieste/ricevuteRichieste ancora in gestione " +
										"correlate alla risposta/richiesta-stato asincrona simmetrica/asimmetrica arrivata, non riuscito: "+e.getMessage());
						tracciamento.registraRichiesta(requestMessage,null,soapHeaderElement,bustaRichiesta,esitoTraccia,
								Tracciamento.createLocationString(true,this.msgContext.getFromLocation()),
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



			/* ----------- Ruolo Busta Ricevuta ------------ */
			try{
				ruoloBustaRicevuta = validatore.getRuoloBustaRicevuta(false);
			}catch(Exception e){
				msgDiag.logErroreGenerico(e,"validator.getRuoloBustaRicevuta(false)");
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
			implementazionePdDMittente = registroServiziReader.getImplementazionePdD(soggettoFruitore, null);
			implementazionePdDDestinatario = registroServiziReader.getImplementazionePdD(idServizio.getSoggettoErogatore(), null);
			idPdDMittente = registroServiziReader.getIdPortaDominio(soggettoFruitore, null);
			idPdDDestinatario = registroServiziReader.getIdPortaDominio(idServizio.getSoggettoErogatore(), null);
			parametriGenerazioneBustaErrore.setImplementazionePdDMittente(implementazionePdDMittente);
			parametriGenerazioneBustaErrore.setImplementazionePdDDestinatario(implementazionePdDDestinatario);
			parametriInvioBustaErrore.setImplementazionePdDMittente(implementazionePdDMittente);
			parametriInvioBustaErrore.setImplementazionePdDDestinatario(implementazionePdDDestinatario);
			
			properties.setValidazioneConSchema(configurazionePdDReader.isLivelloValidazioneRigido(implementazionePdDMittente));
			properties.setValidazioneProfiloCollaborazione(configurazionePdDReader.isValidazioneProfiloCollaborazione(implementazionePdDMittente));
			validatore.setProprietaValidazione(properties); // update
			
			MessageSecurityContextParameters contextParameters = new MessageSecurityContextParameters();
			contextParameters.setUseActorDefaultIfNotDefined(propertiesReader.isGenerazioneActorDefault(implementazionePdDMittente));
			contextParameters.setActorDefault(propertiesReader.getActorDefault(implementazionePdDMittente));
			contextParameters.setLog(logCore);
			contextParameters.setFunctionAsClient(SecurityConstants.SECUIRYT_SERVER);
			contextParameters.setPrefixWsuId(propertiesReader.getPrefixWsuId());
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
						Tracciamento.createLocationString(true,this.msgContext.getFromLocation()),
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
		msgDiag.mediumDebug("ImplementazionePdD soggetto ("+soggettoFruitore.toString()+") e' ["+implementazionePdDMittente+"], soggetto ("
				+idServizio.getSoggettoErogatore().toString()+") e' ["+implementazionePdDDestinatario+"]");
		
		
		
		
		
		



		

		/* -------- Lettura Porta Applicativa 
		 * (Il vero controllo sull'esistenza della Porta Applicativa viene effettuato in Sbustamento, poiche' dipende dal profilo) ------------- */
		// per profili asincroni
		PortaDelegata pd = null;
		IDPortaDelegata idPD = null;
		String servizioApplicativoErogatoreAsincronoSimmetricoRisposta = null;
		if(functionAsRouter==false){
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
						idPD.setLocationPD(integrazione.getLocationPD());
						idPD.setSoggettoFruitore(new IDSoggetto(bustaRichiesta.getTipoDestinatario(),bustaRichiesta.getDestinatario()));
						pd = configurazionePdDReader.getPortaDelegata_SafeMethod(idPD);
						
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
							idServizioOriginale.setSoggettoErogatore(idServizio.getSoggettoErogatore());
							if(pa==null){ // potrebbe essere compreso tramite paBased url Mapping
								IDPortaApplicativaByNome idByNome = configurazionePdDReader.convertTo_SafeMethod(idServizioOriginale, this.msgContext.getProprietaFiltroPortaApplicativa());
								if(idByNome!=null)
									pa = configurazionePdDReader.getPortaApplicativa_SafeMethod(idByNome);
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
							idPD.setLocationPD(integrazione.getLocationPD());
							idPD.setSoggettoFruitore(new IDSoggetto(bustaRichiesta.getTipoDestinatario(),bustaRichiesta.getDestinatario()));
							pd = configurazionePdDReader.getPortaDelegata_SafeMethod(idPD);
						}
						
					}
					// Chiudo eventuali prepared statement, che non voglio eseguire.
					((StateMessage)openspcoopstate.getStatoRichiesta()).closePreparedStatement();
				}else{
					msgDiag.highDebug("Lettura porta applicativa/delegata (Normale)...");
					if(pa==null){ // potrebbe essere compreso tramite paBased url Mapping
						IDPortaApplicativaByNome idByNome = configurazionePdDReader.convertTo_SafeMethod(idServizio, this.msgContext.getProprietaFiltroPortaApplicativa());
						if(idByNome!=null)
							pa = configurazionePdDReader.getPortaApplicativa_SafeMethod(idByNome);
					}
				}

				// Validazione manifest attachments
				msgDiag.highDebug("Lettura porta applicativa/delegata (Set)...");
				properties.setValidazioneManifestAttachments(
						configurazionePdDReader.isValidazioneManifestAttachments(implementazionePdDMittente) &&
						configurazionePdDReader.isGestioneManifestAttachments(pa,protocolFactory));
				
				msgDiag.highDebug("Lettura porta applicativa/delegata terminato");
				
			}catch(Exception e){
				if(  !(e instanceof DriverConfigurazioneNotFound) ) {
					msgDiag.logErroreGenerico(e,"letturaPorta");
					
					// Tracciamento richiesta: non ancora registrata
					if(this.msgContext.isTracciamentoAbilitato()){
						EsitoElaborazioneMessaggioTracciato esitoTraccia = 
								EsitoElaborazioneMessaggioTracciato.getEsitoElaborazioneConErrore("Errore durante la lettura della porta applicativa/delegata: "+e.getMessage());
						tracciamento.registraRichiesta(requestMessage,null,soapHeaderElement,bustaRichiesta,esitoTraccia,
								Tracciamento.createLocationString(true,this.msgContext.getFromLocation()),
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
		}
		// Aggiungo identita servizio applicativi
		IDPortaApplicativaByNome idPAbyNome = null;
		if(pa!=null){
			idPAbyNome = new IDPortaApplicativaByNome();
			idPAbyNome.setNome(pa.getNome());
			idPAbyNome.setSoggetto(idServizio.getSoggettoErogatore());
			msgDiag.setPorta(pa.getNome());
			for(int i=0; i<pa.sizeServizioApplicativoList();i++){
				this.msgContext.getIntegrazione().addServizioApplicativoErogatore(pa.getServizioApplicativo(i).getNome());
			}
		}
		else if(servizioApplicativoErogatoreAsincronoSimmetricoRisposta!=null){
			// E' l'erogatore della risposta asincrona!
			this.msgContext.getIntegrazione().addServizioApplicativoErogatore(servizioApplicativoErogatoreAsincronoSimmetricoRisposta);
		}
		// Aggiungo identificativo porta applicativa/delegata
		if(idPAbyNome!=null){
			this.msgContext.getIntegrazione().setIdPA(idPAbyNome);
		}
		else if(idPD!=null){
			this.msgContext.getIntegrazione().setIdPD(idPD);
		}
		msgDiag.highDebug("Lettura porta applicativa/delegata terminato impostazione context");





		
		
		
		// Configurazione Richiesta Applicativa
		String idModuloInAttesa = null;
		if(this.msgContext.isGestioneRisposta())
			idModuloInAttesa = this.msgContext.getIdModulo();
		RichiestaApplicativa richiestaApplicativa = new RichiestaApplicativa(soggettoFruitore,idServizio,
				idModuloInAttesa,identitaPdD,idPAbyNome); 
		richiestaApplicativa.setFiltroProprietaPorteApplicative(this.msgContext.getProprietaFiltroPortaApplicativa());
		
		
		


		
		
		
		
		
		// ------------- Controllo funzionalita di protocollo richieste siano compatibili con il protocollo -----------------------------
		try{
			IProtocolConfiguration protocolConfiguration = protocolFactory.createProtocolConfiguration();
			if(bustaRichiesta.getProfiloDiCollaborazione()!=null && 
					!org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.UNKNOWN.equals(bustaRichiesta.getProfiloDiCollaborazione()) ){
				if(protocolConfiguration.isSupportato(bustaRichiesta.getProfiloDiCollaborazione())==false){
					throw new Exception("Profilo di Collaborazione ["+bustaRichiesta.getProfiloDiCollaborazione().getEngineValue()+"]");
				}
			}
			// NOTA:  FiltroDuplicati, consegnaAffidabile, idCollaborazione, consegnaInOrdine verificato in sbustamento.
			if(bustaRichiesta.getScadenza()!=null){
				if(protocolConfiguration.isSupportato(FunzionalitaProtocollo.SCADENZA)==false){
					throw new Exception(FunzionalitaProtocollo.SCADENZA.getEngineValue());
				}
			}
			
			if(configurazionePdDReader.isGestioneManifestAttachments(pa,protocolFactory)){
				if(protocolConfiguration.isSupportato(FunzionalitaProtocollo.MANIFEST_ATTACHMENTS)==false){
					throw new Exception(FunzionalitaProtocollo.MANIFEST_ATTACHMENTS.getEngineValue());
				}
			}			
		}catch(Exception e){	
			msgDiag.addKeyword(CostantiPdD.KEY_ERRORE_PROCESSAMENTO , e.getMessage() );
			msgDiag.logPersonalizzato(MsgDiagnosticiProperties.MSG_DIAG_SBUSTAMENTO,"protocolli.funzionalita.unsupported");
			// Tracciamento richiesta: non ancora registrata
			if(this.msgContext.isTracciamentoAbilitato()){
				EsitoElaborazioneMessaggioTracciato esitoTraccia = 
						EsitoElaborazioneMessaggioTracciato.getEsitoElaborazioneConErrore(msgDiag.getMessaggio_replaceKeywords("protocolli.funzionalita.unsupported"));
				tracciamento.registraRichiesta(requestMessage,null,soapHeaderElement,bustaRichiesta,esitoTraccia,
						Tracciamento.createLocationString(true,this.msgContext.getFromLocation()),
						correlazioneApplicativa);
			}
			if(this.msgContext.isGestioneRisposta()){
				
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
						msgDiag.logErroreGenerico(e,"configurazionePdDReader.getTipiIntegrazione(pa)");
						// Tracciamento richiesta: non ancora registrata
						if(this.msgContext.isTracciamentoAbilitato()){
							EsitoElaborazioneMessaggioTracciato esitoTraccia = 
									EsitoElaborazioneMessaggioTracciato.getEsitoElaborazioneConErrore("Errore durante la lettura delle configurazioni dei tipi di integrazione con la  porta applicativa/delegata: "+e.getMessage());
							tracciamento.registraRichiesta(requestMessage,null,soapHeaderElement,bustaRichiesta,esitoTraccia,
									Tracciamento.createLocationString(true,this.msgContext.getFromLocation()),
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
				msgDiag.highDebug("Header integrazione (Gestori integrazione terminato)");
				if (tipiIntegrazionePA == null){
					if(RicezioneBuste.defaultPerProtocolloGestoreIntegrazionePA.containsKey(protocolFactory.getProtocol()))
						tipiIntegrazionePA = RicezioneBuste.defaultPerProtocolloGestoreIntegrazionePA.get(protocolFactory.getProtocol());
					else
						tipiIntegrazionePA = RicezioneBuste.defaultGestoriIntegrazionePA;
				}
			}
		}
		HeaderIntegrazione headerIntegrazioneRichiesta = new HeaderIntegrazione();
		InRequestPAMessage inRequestPAMessage = null;
		if(tipiIntegrazionePA!=null){
			msgDiag.highDebug("Header integrazione (Impostazione ...)");
			inRequestPAMessage = new InRequestPAMessage();
			inRequestPAMessage.setBustaRichiesta(bustaRichiesta);
			inRequestPAMessage.setMessage(requestMessage);
			inRequestPAMessage.setUrlProtocolContext(this.msgContext.getUrlProtocolContext());
			inRequestPAMessage.setPortaApplicativa(pa);
			inRequestPAMessage.setServizio(idServizio);
			inRequestPAMessage.setSoggettoMittente(soggettoFruitore);
			for (int i = 0; i < tipiIntegrazionePA.length; i++) {
				try {
//					if(RicezioneBuste.gestoriIntegrazionePA.containsKey(tipiIntegrazionePA[i])==false){
//						RicezioneBuste.aggiornaListaGestoreIntegrazione(
//								tipiIntegrazionePA[i], className,
//								propertiesReader, logCore);
//					}
//					IGestoreIntegrazionePA gestore = RicezioneBuste.gestoriIntegrazionePA.get(tipiIntegrazionePA[i]);
					
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
					else if (gestore instanceof IGestoreIntegrazionePASoap) {
						((IGestoreIntegrazionePASoap)gestore).readInRequestHeader(headerIntegrazioneRichiesta, inRequestPAMessage);
					}		
				} catch (Exception e) {
					msgDiag.addKeyword(CostantiPdD.KEY_TIPO_HEADER_INTEGRAZIONE,tipiIntegrazionePA[i]);
					msgDiag.addKeyword(CostantiPdD.KEY_ERRORE_PROCESSAMENTO, e.toString());
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
				
				// overwrite info sulla busta
				bustaRichiesta.setServizioApplicativoFruitore(servizioApplicativoFruitore);
				msgDiag.highDebug("Header integrazione (set context ok)");
			}
		}
		

		
		
		
		
		
		
		
		
		
		
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
					msgDiag.addKeyword(CostantiPdD.KEY_ERRORE_PROCESSAMENTO, e.getMessage());
					msgDiag.logPersonalizzato("gestoreCredenziali.errore");
					ErroreIntegrazione msgErroreIntegrazione = null;
					if(e instanceof GestoreCredenzialiConfigurationException){
						msgErroreIntegrazione = 
								ErroriIntegrazione.ERRORE_431_GESTORE_CREDENZIALI_ERROR.
									getErrore431_ErroreGestoreCredenziali(RicezioneBuste.tipiGestoriCredenziali[i],e);
					}else{
						msgErroreIntegrazione = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
								get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_548_GESTORE_CREDENZIALI_NON_FUNZIONANTE);
					}
					
					// Tracciamento richiesta: non ancora registrata
					if(this.msgContext.isTracciamentoAbilitato()){
						EsitoElaborazioneMessaggioTracciato esitoTraccia = 
								EsitoElaborazioneMessaggioTracciato.getEsitoElaborazioneConErrore(msgDiag.getMessaggio_replaceKeywords("gestoreCredenziali.errore"));
						tracciamento.registraRichiesta(requestMessage,null,soapHeaderElement,bustaRichiesta,esitoTraccia,
								Tracciamento.createLocationString(true,this.msgContext.getFromLocation()),
								correlazioneApplicativa);
					}
					if(this.msgContext.isGestioneRisposta()){
						
						parametriGenerazioneBustaErrore.setBusta(bustaRichiesta);
						parametriGenerazioneBustaErrore.setErroreIntegrazione(msgErroreIntegrazione);
						OpenSPCoop2Message errorMsg = generaBustaErroreProcessamento(parametriGenerazioneBustaErrore,e);
						
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
					oneWayStateless = configurazionePdDReader.isModalitaStateless(pa,bustaRichiesta.getProfiloDiCollaborazione());
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
			if (this.msgContext.isGestioneRisposta()) {
				
				if(erroreIntegrazione==null){
					erroreIntegrazione = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
							get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_558_HANDLER_IN_PROTOCOL_REQUEST);
				}
				
				parametriGenerazioneBustaErrore.setBusta(bustaRichiesta);
				parametriGenerazioneBustaErrore.setErroreIntegrazione(erroreIntegrazione);
				OpenSPCoop2Message errorMsg = generaBustaErroreProcessamento(parametriGenerazioneBustaErrore,e);
				
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
				flowPropertiesRequest = this.getFlowPropertiesRequest(bustaRichiesta, configurazionePdDReader, 
						((StateMessage)openspcoopstate.getStatoRichiesta()), msgDiag,logCore,propertiesReader,
						ruoloBustaRicevuta,implementazionePdDMittente,inRequestContext.getPddContext(),pa);				
				flowPropertiesResponse = this.getFlowPropertiesResponse(bustaRichiesta, configurazionePdDReader, 
						((StateMessage)openspcoopstate.getStatoRichiesta()), msgDiag,logCore,propertiesReader,
						ruoloBustaRicevuta,implementazionePdDMittente,inRequestContext.getPddContext(),pa);
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
				msgDiag.logPersonalizzato("ricezioneMessaggio");
			
				msgDiag.logErroreGenerico(e,"RaccoltaFlowParameter_MTOM_Security");
				
				// Tracciamento richiesta: non ancora registrata
				if(this.msgContext.isTracciamentoAbilitato()){
					EsitoElaborazioneMessaggioTracciato esitoTraccia = 
							EsitoElaborazioneMessaggioTracciato.getEsitoElaborazioneConErrore("Riscontrato errore durante la lettura delle proprieta' di MTOM / SecurityMessage: "+e.getMessage());
					tracciamento.registraRichiesta(requestMessage,null,soapHeaderElement,bustaRichiesta,esitoTraccia,
							Tracciamento.createLocationString(true,this.msgContext.getFromLocation()),
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
							Tracciamento.createLocationString(true,this.msgContext.getFromLocation()),
							correlazioneApplicativa);
				}
				if(this.msgContext.isGestioneRisposta()){
					
					parametriGenerazioneBustaErrore.setBusta(bustaRichiesta);
					parametriGenerazioneBustaErrore.setErroreIntegrazione(ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
							get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_557_MTOM_PROCESSOR_ERROR));
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
			boolean presenzaRichiestaProtocollo = validatore.validazioneSemantica_beforeMessageSecurity(false, null);
			
			if(validatore.isRilevatiErroriDuranteValidazioneSemantica()==false){
				
				msgDiag.mediumDebug("Lettura informazioni sulla Sicurezza dal Messaggio di richiesta ...");
				presenzaRichiestaProtocollo = validatore.validazioneSemantica_messageSecurity_readSecurityInfo(messageSecurityContext);
				msgDiag.mediumDebug("Lettura informazioni sulla Sicurezza dal Messaggio di richiesta completata con successo");
			
				if(validatore.isRilevatiErroriDuranteValidazioneSemantica()==false){				
					if(messageSecurityContext!= null && messageSecurityContext.getIncomingProperties() != null && messageSecurityContext.getIncomingProperties().size() > 0){
					
						String tipoSicurezza = SecurityConstants.convertActionToString(messageSecurityContext.getIncomingProperties());
						msgDiag.addKeyword(CostantiPdD.KEY_TIPO_SICUREZZA_MESSAGGIO_RICHIESTA, tipoSicurezza);
						pddContext.addObject(CostantiPdD.KEY_TIPO_SICUREZZA_MESSAGGIO_RICHIESTA, tipoSicurezza);
						
						msgDiag.logPersonalizzato("messageSecurity.processamentoRichiestaInCorso");
						
						StringBuffer bfErroreSecurity = new StringBuffer();
						presenzaRichiestaProtocollo = validatore.validazioneSemantica_messageSecurity_process(messageSecurityContext, bfErroreSecurity);
						
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
				msgDiag.logPersonalizzato("ricezioneMessaggio");
				
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
							Tracciamento.createLocationString(true,this.msgContext.getFromLocation()),
							correlazioneApplicativa);
				}
				if(this.msgContext.isGestioneRisposta()){
					
					parametriGenerazioneBustaErrore.setBusta(bustaRichiesta);
					parametriGenerazioneBustaErrore.setErroreIntegrazione(ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
							get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_525_GESTIONE_FUNZIONALITA_PROTOCOLLO));
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
							Tracciamento.createLocationString(true,this.msgContext.getFromLocation()),
							correlazioneApplicativa);
				}
				if(this.msgContext.isGestioneRisposta()){
					
					parametriGenerazioneBustaErrore.setBusta(bustaRichiesta);
					parametriGenerazioneBustaErrore.setErroreIntegrazione(ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
							get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_557_MTOM_PROCESSOR_ERROR));
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
		java.util.Vector<Eccezione> erroriValidazione = validatore.getEccezioniValidazione();
		java.util.Vector<Eccezione> erroriProcessamento =validatore.getEccezioniProcessamento();
		SecurityInfo securityInfoRequest = validatore.getSecurityInfo();
		
		boolean isMessaggioErroreProtocollo = validatore.isErroreProtocollo();
		// Se ho un msg Errore e' interessante solo utilizzare mittente/destinatario poiche' il servizio non esistera'
		// La busta e' stata invertita tra mittente e destinatario
		if(isMessaggioErroreProtocollo){
			idServizio.setVersioneServizio(null);
			idServizio.setServizio(null);
			idServizio.setTipoServizio(null);
			idServizio.setAzione(null);
		}
		boolean bustaDiServizio = validatore.isBustaDiServizio();
		if(validatore.getInfoServizio()!=null){
			this.msgContext.getProtocol().setIdAccordo(validatore.getInfoServizio().getIdAccordo());
			richiestaApplicativa.setIdAccordo(validatore.getInfoServizio().getIdAccordo());
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
		
			try {
				SOAPEnvelope soapEnvelopeRequest = requestMessage.getSOAPPart().getEnvelope();
				
				GestoreCorrelazioneApplicativa gestoreCorrelazioneApplicativa = 
					new GestoreCorrelazioneApplicativa(openspcoopstate.getStatoRichiesta(), logCore, soggettoFruitore,
							idServizio,servizioApplicativoFruitore,protocolFactory);
				
				gestoreCorrelazioneApplicativa.verificaCorrelazione(pa.getCorrelazioneApplicativa(), null, soapEnvelopeRequest, 
						headerIntegrazioneRichiesta, false);
				
				if(gestoreCorrelazioneApplicativa.getIdCorrelazione()!=null)
					correlazioneApplicativa = gestoreCorrelazioneApplicativa.getIdCorrelazione();
				
			}catch(Exception e){
				msgDiag.logErroreGenerico(e,"CorrelazioneApplicativa("+bustaRichiesta.getID()+")");
				logCore.error("Riscontrato errore durante la correlazione applicativa ["+bustaRichiesta.getID()+"]",e);
				
				// Tracciamento richiesta: non ancora registrata
				if(this.msgContext.isTracciamentoAbilitato()){
					EsitoElaborazioneMessaggioTracciato esitoTraccia = 
							EsitoElaborazioneMessaggioTracciato.getEsitoElaborazioneConErrore("Riscontrato errore durante la correlazione applicativa ["+bustaRichiesta.getID()+"]: "+e.getMessage());
					tracciamento.registraRichiesta(requestMessage,null,soapHeaderElement,bustaRichiesta,esitoTraccia,
							Tracciamento.createLocationString(true,this.msgContext.getFromLocation()),
							correlazioneApplicativa);
				}
				
				if(this.msgContext.isGestioneRisposta()){
					
					parametriGenerazioneBustaErrore.setBusta(bustaRichiesta);
					parametriGenerazioneBustaErrore.setErroreIntegrazione(ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
							get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_529_CORRELAZIONE_APPLICATIVA_RICHIESTA_NON_RIUSCITA));
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
								Tracciamento.createLocationString(true,this.msgContext.getFromLocation()),
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
		if(!isMessaggioErroreProtocollo && 
				idServizio.getServizio()!=null && idServizio.getTipoServizio()!=null && idServizio.getVersioneServizio()!=null ){
			
			// Viene impostato a null se sopra e' un errore di protocollo, oppure se nella busta ci sono errori relativi al servizio.
			
			msgDiag.logCorrelazione();
		}
		
		
		
		
		
		
		
		
		
		
		
		/* ----------- Emetto msg diagnostico di ricezione Busta ----------------------*/
		DettaglioEccezione dettaglioEccezione = null;
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
			
		}else
			msgDiag.logPersonalizzato("ricezioneMessaggio");












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
					!isMessaggioErroreProtocollo){ // aggiunto !isMessaggioErrore poiche' una busta di eccezione puo' contenenere in effetti il rifMsg
				Eccezione ecc = Eccezione.getEccezioneValidazione(ErroriCooperazione.RIFERIMENTO_MESSAGGIO_NON_VALIDO.getErroreCooperazione(), protocolFactory);
				erroriValidazione.add(ecc);
			}
		}

















		/* -------- Gestione errori ------------- */
		// Se sono in modalita router: se vi sono errori di processamento/validazione, ritorno subito l'errore.
		// altrimenti: se sono presenti errore di processamento, ritorno subito l'errore.
		msgDiag.mediumDebug("Gestione errori...");
		if( (erroriProcessamento.size()>0) || (functionAsRouter && (erroriValidazione.size()>0)) ){
			StringBuffer errore = new StringBuffer();
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
						Tracciamento.createLocationString(true,this.msgContext.getFromLocation()),
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
		SOAPElement headerProtocolloRichiesta = validatore.getHeaderProtocollo();
		if(this.msgContext.isTracciamentoAbilitato()){
			msgDiag.mediumDebug("Tracciamento busta di richiesta...");
			
			EsitoElaborazioneMessaggioTracciato esitoTraccia = null;
			if( (erroriProcessamento.size()>0) || (erroriValidazione.size()>0) ){
				
				boolean foundErroriGravi = false;
				
				String dettaglioErrore = null;
				StringBuffer eccBuffer = new StringBuffer();
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
					Tracciamento.createLocationString(true,this.msgContext.getFromLocation()),
					correlazioneApplicativa);
		}













		/* -------- Check Mittente o Destinatario o Servizio conosciuto o identificativo errato ---------------- */
		msgDiag.mediumDebug("Controllo mittente/destinatario/servizio (se sconosciuto, risposta immediata sulla connessione)...");
		for(int k = 0; k < erroriValidazione.size() ; k++){
			Eccezione er = erroriValidazione.get(k);
			if( CodiceErroreCooperazione.isEccezioneMittente(er.getCodiceEccezione()) ||			
				CodiceErroreCooperazione.isEccezioneDestinatario(er.getCodiceEccezione()) ||	
				CodiceErroreCooperazione.isEccezioneServizio(er.getCodiceEccezione()) ||	
				CodiceErroreCooperazione.isEccezioneIdentificativoMessaggio(er.getCodiceEccezione()) ){

				if(functionAsRouter==false){ 
					// Può esistere un errore mittente, che non è altro che una segnalazione sull'indirizzo telematico
					if(moduleManager.isEccezioniLivelloInfoAbilitato()){
						if(LivelloRilevanza.INFO.equals(er.getRilevanza()))
							continue;
					}
				}

				StringBuffer eccBuffer = new StringBuffer();
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
					Vector<Eccezione> errs = new Vector<Eccezione>();
					for(int add = 0; add < erroriValidazione.size() ; add++){
						errs.add(erroriValidazione.get(add));
					}
					parametriGenerazioneBustaErrore.setError(errs);
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
		
		DatiInvocazionePortaApplicativa datiInvocazione = null;
		
		// NOTA: TODO: la porta applicativa non c'e' sempre, ad esempio non c'e' per il profilo asincrono simmetrico per la risposta.
		// In questi casi c'e' invece la porta delegata. Aggiungere un campo ad hoc sulla porta delegata???
		// Inoltre gestire i casi in cui la busta arrivata e' di servizio (es. riscontri) e quindi non corrisponde ne ad una porta delegata ne ad una porta applicativa
		
		String tipoAutorizzazione_TODO_SpostareSuPortaApplicativa = propertiesReader.getTipoAutorizzazioneBuste();
		boolean isAttivoAutorizzazioneBuste_TODO_SpostareSuPortaApplicativa = !CostantiConfigurazione.AUTORIZZAZIONE_NONE.equalsIgnoreCase(tipoAutorizzazione_TODO_SpostareSuPortaApplicativa);
		this.msgContext.getIntegrazione().setTipoAutorizzazione(tipoAutorizzazione_TODO_SpostareSuPortaApplicativa);
		if(tipoAutorizzazione_TODO_SpostareSuPortaApplicativa!=null){
			msgDiag.addKeyword(CostantiPdD.KEY_TIPO_AUTORIZZAZIONE_BUSTE, tipoAutorizzazione_TODO_SpostareSuPortaApplicativa);
		}
		if(!isAttivoAutorizzazioneBuste_TODO_SpostareSuPortaApplicativa){
			msgDiag.logPersonalizzato("autorizzazioneBusteDisabilitata");
		}
		if(isAttivoAutorizzazioneBuste_TODO_SpostareSuPortaApplicativa &&
				isMessaggioErroreProtocollo==false &&
				bustaDiServizio==false &&
				eccezioniValidazioni==false){	
			try{

				msgDiag.mediumDebug("Autorizzazione Protocollo di tipo ["+tipoAutorizzazione_TODO_SpostareSuPortaApplicativa+"]...");

				//	Controllo Autorizzazione
				String identitaMittente = null;
				if(credenziali!=null){
					if(!"".equals(credenziali.toString()))
						identitaMittente = credenziali.toString();
				}
				String subjectMessageSecurity = null;
				if(messageSecurityContext!=null){
					subjectMessageSecurity = messageSecurityContext.getSubject();
				}



				/* --- Calcolo idServizio e fruitore ---- */

				IDServizio idServizioPerAutorizzazione = getIdServizioPerAutorizzazione(idServizio, soggettoFruitore, functionAsRouter, bustaRichiesta, ruoloBustaRicevuta); 
				IDSoggetto soggettoMittentePerAutorizzazione = getIDSoggettoMittentePerAutorizzazione(idServizio, soggettoFruitore, functionAsRouter, bustaRichiesta, ruoloBustaRicevuta);
				

				String tipoMessaggio = "messaggio";
				if(ruoloBustaRicevuta!=null){
					if(RuoloBusta.RICEVUTA_RICHIESTA.equals(ruoloBustaRicevuta.toString()) || RuoloBusta.RICEVUTA_RISPOSTA.equals(ruoloBustaRicevuta.toString())){
						tipoMessaggio = "ricevuta asincrona";
					}
				}
				msgDiag.addKeyword(CostantiPdD.KEY_TIPO_MESSAGGIO_BUSTA, tipoMessaggio);
				msgDiag.addKeyword(CostantiPdD.KEY_MITTENTE_E_SERVIZIO_DA_AUTORIZZARE, "FR["+soggettoMittentePerAutorizzazione.toString()+"]->ER["+idServizioPerAutorizzazione.toString()+"]");
				if(identitaMittente!=null)
					msgDiag.addKeyword(CostantiPdD.KEY_CREDENZIALI_MITTENTE_MSG, " inviato da un mittente ["+identitaMittente+"]");
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
				identitaServizioApplicativoFruitore.setIdSoggettoProprietario(soggettoMittentePerAutorizzazione);
				
				datiInvocazione = new DatiInvocazionePortaApplicativa();
				datiInvocazione.setInfoConnettoreIngresso(inRequestContext.getConnettore());
				datiInvocazione.setIdServizio(idServizioPerAutorizzazione);
				datiInvocazione.setState(openspcoopstate.getStatoRichiesta());
				datiInvocazione.setCredenzialiPdDMittente(credenziali);
				datiInvocazione.setIdentitaServizioApplicativoFruitore(identitaServizioApplicativoFruitore);
				datiInvocazione.setSubjectServizioApplicativoFruitoreFromMessageSecurityHeader(subjectMessageSecurity);
				datiInvocazione.setIdPA(idPAbyNome);
				datiInvocazione.setPa(pa);
				datiInvocazione.setIdPD(idPD);
				datiInvocazione.setPd(pd);
				datiInvocazione.setIdSoggettoFruitore(soggettoMittentePerAutorizzazione);
				datiInvocazione.setRuoloBusta(ruoloBustaRicevuta);
				
				EsitoAutorizzazioneCooperazione esito = 
						GestoreAutorizzazione.verificaAutorizzazionePortaApplicativa(tipoAutorizzazione_TODO_SpostareSuPortaApplicativa, 
								datiInvocazione, pddContext, protocolFactory);
				if(esito.isServizioAutorizzato()==false){
					try{
						msgDiag.addKeyword(CostantiPdD.KEY_ERRORE_PROCESSAMENTO, esito.getErroreCooperazione().getDescrizione(protocolFactory));
					}catch(Exception e){
						logCore.error("getDescrizione Error:"+e.getMessage(),e);
					}
					msgDiag.addKeyword(CostantiPdD.KEY_POSIZIONE_ERRORE, traduttore.toString(esito.getErroreCooperazione().getCodiceErrore()));
					msgDiag.logPersonalizzato("autorizzazioneBusteFallita");
					if(this.msgContext.isGestioneRisposta()){
						OpenSPCoop2Message errorOpenSPCoopMsg = null;
						parametriGenerazioneBustaErrore.setBusta(bustaRichiesta);
						parametriGenerazioneBustaErrore.setErroreCooperazione(esito.getErroreCooperazione());
						if(CodiceErroreCooperazione.SICUREZZA_AUTORIZZAZIONE_FALLITA.equals(esito.getErroreCooperazione().getCodiceErrore()) || 
								CodiceErroreCooperazione.SICUREZZA_FALSIFICAZIONE_MITTENTE.equals(esito.getErroreCooperazione().getCodiceErrore())){
							errorOpenSPCoopMsg = generaBustaErroreValidazione(parametriGenerazioneBustaErrore);
						}
						// Else necessario per Certificazione DigitPA tramite Router
						else if(CodiceErroreCooperazione.SERVIZIO_SCONOSCIUTO.equals(esito.getErroreCooperazione().getCodiceErrore())){
							parametriGenerazioneBustaErrore.setErroreCooperazione(
									ErroriCooperazione.SERVIZIO_SCONOSCIUTO.getErroreCooperazione()); // in modo da utilizzare la posizione standard.
							errorOpenSPCoopMsg = generaBustaErroreValidazione(parametriGenerazioneBustaErrore);
						}
						else{
							errorOpenSPCoopMsg = generaBustaErroreProcessamento(parametriGenerazioneBustaErrore, esito.getEccezioneProcessamento());
						}

						// Nota: la bustaRichiesta e' stata trasformata da generaErroreProcessamento
						parametriInvioBustaErrore.setOpenspcoopMsg(errorOpenSPCoopMsg);
						parametriInvioBustaErrore.setBusta(parametriGenerazioneBustaErrore.getBusta());
						sendRispostaBustaErrore(parametriInvioBustaErrore);
					}
					openspcoopstate.releaseResource();
					return;
				}else{
					if(esito.getDetails()==null){
						msgDiag.addKeyword(CostantiPdD.KEY_DETAILS, "");
					}else{
						msgDiag.addKeyword(CostantiPdD.KEY_DETAILS, " ("+esito.getDetails()+")");
					}
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
		}







		
		
		
		
		
		
		
		
		/* --------------- Verifica tipo soggetto fruitore e tipo servizio rispetto al canale utilizzato 
		 * (NOTA!: non sportarlo prima senno non si supera eventuali certificazioni) --------------- */
		msgDiag.mediumDebug("Verifica canale utilizzato...");
		List<String> tipiSoggettiSupportatiCanale = protocolFactory.createProtocolConfiguration().getTipiSoggetti();
		List<String> tipiServiziSupportatiCanale = protocolFactory.createProtocolConfiguration().getTipiServizi();
		ErroreIntegrazione erroreVerificaTipoByProtocol = null;
		// Nota: se qualche informazione e' null verranno segnalati altri errori
		if(soggettoFruitore!=null && soggettoFruitore.getTipo()!=null && 
				tipiSoggettiSupportatiCanale.contains(soggettoFruitore.getTipo())==false){
			msgDiag.logPersonalizzato("protocolli.tipoSoggetto.fruitore.unsupported");
			erroreVerificaTipoByProtocol = ErroriIntegrazione.ERRORE_436_TIPO_SOGGETTO_FRUITORE_NOT_SUPPORTED_BY_PROTOCOL.
					getErrore436_TipoSoggettoFruitoreNotSupportedByProtocol(soggettoFruitore,protocolFactory);
		}
		else if(idServizio!=null && idServizio.getSoggettoErogatore()!=null && idServizio.getSoggettoErogatore().getTipo()!=null &&
				tipiSoggettiSupportatiCanale.contains(idServizio.getSoggettoErogatore().getTipo())==false){
			msgDiag.logPersonalizzato("protocolli.tipoSoggetto.erogatore.unsupported");
			erroreVerificaTipoByProtocol = ErroriIntegrazione.ERRORE_437_TIPO_SOGGETTO_EROGATORE_NOT_SUPPORTED_BY_PROTOCOL.
					getErrore437_TipoSoggettoErogatoreNotSupportedByProtocol(idServizio.getSoggettoErogatore(),protocolFactory);
		}
		else if(idServizio!=null && idServizio.getTipoServizio()!=null && 
				tipiServiziSupportatiCanale.contains(idServizio.getTipoServizio())==false){
			msgDiag.logPersonalizzato("protocolli.tipoServizio.unsupported");
			erroreVerificaTipoByProtocol = ErroriIntegrazione.ERRORE_438_TIPO_SERVIZIO_NOT_SUPPORTED_BY_PROTOCOL.
				getErrore438_TipoServizioNotSupportedByProtocol(idServizio,protocolFactory);
		}
			
		if(erroreVerificaTipoByProtocol!=null){
			if(this.msgContext.isGestioneRisposta()){

				parametriGenerazioneBustaErrore.setBusta(bustaRichiesta);							
				parametriGenerazioneBustaErrore.setErroreIntegrazione(erroreVerificaTipoByProtocol);

				OpenSPCoop2Message errorOpenSPCoopMsg = generaBustaErroreProcessamento(parametriGenerazioneBustaErrore,null);
				
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
							validazioneContenutoApplicativoApplicativo = configurazionePdDReader.getTipoValidazioneContenutoApplicativo(pa,implementazionePdDMittente);
							tipoAutorizzazionePerContenuto = configurazionePdDReader.getAutorizzazioneContenuto(pa);
						}else{
							//	Risposta Asincrona
							if(RuoloBusta.RISPOSTA.equals(ruoloBustaRicevuta.toString())){
								validazioneContenutoApplicativoApplicativo = configurazionePdDReader.getTipoValidazioneContenutoApplicativo(pd,implementazionePdDMittente);
								// NOTA: deve essere registrato un tipo di autorizzazione per contenuto busta uguale al tipo di autorizzazione utilizzato lato servizi applicativi.
								tipoAutorizzazionePerContenuto = configurazionePdDReader.getAutorizzazioneContenuto(pd);
							}
							//	Ricevuta alla richiesta/risposta.
							else if(RuoloBusta.RICEVUTA_RICHIESTA.equals(ruoloBustaRicevuta.toString()) || 
									RuoloBusta.RICEVUTA_RISPOSTA.equals(ruoloBustaRicevuta.toString()) ){	
								if( configurazionePdDReader.ricevutaAsincronaSimmetricaAbilitata(pd))	{
									isRicevutaAsincrona_modalitaAsincrona = true;	
								}else{
									validazioneContenutoApplicativoApplicativo = configurazionePdDReader.getTipoValidazioneContenutoApplicativo(pd,implementazionePdDMittente);
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
							validazioneContenutoApplicativoApplicativo = configurazionePdDReader.getTipoValidazioneContenutoApplicativo(pa,implementazionePdDMittente);
							tipoAutorizzazionePerContenuto = configurazionePdDReader.getAutorizzazioneContenuto(pa);
						}else{
							//	Risposta Asincrona
							if(RuoloBusta.RISPOSTA.equals(ruoloBustaRicevuta.toString())){
								validazioneContenutoApplicativoApplicativo = configurazionePdDReader.getTipoValidazioneContenutoApplicativo(pa,implementazionePdDMittente);
								tipoAutorizzazionePerContenuto = configurazionePdDReader.getAutorizzazioneContenuto(pa);
							}
							// Ricevuta alla richiesta/risposta.
							else if(RuoloBusta.RICEVUTA_RICHIESTA.equals(ruoloBustaRicevuta.toString()) || 
									RuoloBusta.RICEVUTA_RISPOSTA.equals(ruoloBustaRicevuta.toString()) ){	
								if( configurazionePdDReader.ricevutaAsincronaAsimmetricaAbilitata(pd))	{
									isRicevutaAsincrona_modalitaAsincrona = true;	
								}else{
									validazioneContenutoApplicativoApplicativo = configurazionePdDReader.getTipoValidazioneContenutoApplicativo(pd,implementazionePdDMittente);
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
					validazioneContenutoApplicativoApplicativo = configurazionePdDReader.getTipoValidazioneContenutoApplicativo(pa,implementazionePdDMittente);
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
				ByteArrayInputStream binXSD = null;
				try{
					if(CostantiConfigurazione.STATO_CON_WARNING_ABILITATO.equals(validazioneContenutoApplicativoApplicativo.getStato())||
							CostantiConfigurazione.STATO_CON_WARNING_WARNING_ONLY.equals(validazioneContenutoApplicativoApplicativo.getStato())){
						
						msgDiag.logPersonalizzato("validazioneContenutiApplicativiRichiestaInCorso");
						
						// Accept mtom message
						List<MtomXomReference> xomReferences = null;
						if(StatoFunzionalita.ABILITATO.equals(validazioneContenutoApplicativoApplicativo.getAcceptMtomMessage())){
							msgDiag.mediumDebug("Validazione xsd della richiesta (mtomFastUnpackagingForXSDConformance)...");
							xomReferences = requestMessage.mtomFastUnpackagingForXSDConformance();
						}
						
						// Init Validatore
						boolean readWSDL = CostantiConfigurazione.VALIDAZIONE_CONTENUTI_APPLICATIVI_WSDL.equals(validazioneContenutoApplicativoApplicativo.getTipo());
						msgDiag.mediumDebug("Validazione xsd della richiesta (initValidator)...");
						ValidatoreMessaggiApplicativi validatoreMessaggiApplicativi = 
							new ValidatoreMessaggiApplicativi(registroServiziReader,idServizio,
									requestMessage.getVersioneSoap(),requestMessage.getSOAPPart().getEnvelope(),readWSDL);

						// Validazione WSDL 
						if( CostantiConfigurazione.VALIDAZIONE_CONTENUTI_APPLICATIVI_WSDL.equals(validazioneContenutoApplicativoApplicativo.getTipo()) 
								||
								CostantiConfigurazione.VALIDAZIONE_CONTENUTI_APPLICATIVI_OPENSPCOOP.equals(validazioneContenutoApplicativoApplicativo.getTipo())
						){
							msgDiag.mediumDebug("Validazione wsdl della richiesta ...");
							validatoreMessaggiApplicativi.validateWithWsdlLogicoImplementativo(true, this.msgContext.getSoapAction());
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
					}
					else{
						msgDiag.logPersonalizzato("validazioneContenutiApplicativiRichiestaDisabilitata");
					}
				}catch(ValidatoreMessaggiApplicativiException ex){
					msgDiag.addKeyword(CostantiPdD.KEY_ERRORE_PROCESSAMENTO, ex.getMessage());
					msgDiag.logPersonalizzato("validazioneContenutiApplicativiRichiestaNonRiuscita");
					logCore.error("[ValidazioneContenutiApplicativi Richiesta] "+ex.getMessage(),ex);
					if(CostantiConfigurazione.STATO_CON_WARNING_WARNING_ONLY.equals(validazioneContenutoApplicativoApplicativo.getStato()) == false){
						// validazione abilitata
						if(this.msgContext.isGestioneRisposta()){
							
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
					msgDiag.addKeyword(CostantiPdD.KEY_ERRORE_PROCESSAMENTO, ex.getMessage());
					msgDiag.logPersonalizzato("validazioneContenutiApplicativiRichiestaNonRiuscita");
					logCore.error("Riscontrato errore durante la validazione dei contenuti applicativi (richiesta applicativa)",ex);
					if(CostantiConfigurazione.STATO_CON_WARNING_WARNING_ONLY.equals(validazioneContenutoApplicativoApplicativo.getStato()) == false){
						// validazione abilitata
						if(this.msgContext.isGestioneRisposta()){
							
							parametriGenerazioneBustaErrore.setBusta(bustaRichiesta);
							parametriGenerazioneBustaErrore.setErroreIntegrazione(ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
									get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_531_VALIDAZIONE_WSDL_FALLITA));

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
					if(binXSD!=null){
						try{
							binXSD.close();
						}catch(Exception e){}
					}
				}
			}
			
			
			
			// AUTORIZZAZIONE PER CONTENUTO
			this.msgContext.getIntegrazione().setTipoAutorizzazioneContenuto(tipoAutorizzazionePerContenuto);
			if(tipoAutorizzazionePerContenuto!=null){
				msgDiag.addKeyword(CostantiPdD.KEY_TIPO_AUTORIZZAZIONE_CONTENUTO, tipoAutorizzazionePerContenuto);
			}
			if (CostantiConfigurazione.AUTORIZZAZIONE_NONE.equalsIgnoreCase(tipoAutorizzazionePerContenuto) == false) {
				try{
//					if (RicezioneBuste.gestoriAutorizzazioneContenutoBuste.containsKey(tipoAutorizzazionePerContenuto) == false)
//						RicezioneBuste.aggiornaListaGestoreAutorizzazioneContenutoBuste(
//								tipoAutorizzazionePerContenuto, className,propertiesReader, logCore);
//					IAutorizzazioneContenutoBuste auth = RicezioneBuste.gestoriAutorizzazioneContenutoBuste.get(tipoAutorizzazionePerContenuto);
					
					String classType = null;
					IAutorizzazioneContenutoPortaApplicativa auth = null;
					try {
						classType = className.getAutorizzazioneContenutoBuste(tipoAutorizzazionePerContenuto);
						auth = (IAutorizzazioneContenutoPortaApplicativa) loader.newInstance(classType);
						AbstractCore.init(auth, pddContext, protocolFactory);
					} catch (Exception e) {
						throw new Exception(
								"Riscontrato errore durante il caricamento della classe ["+ classType
										+ "] da utilizzare per la gestione dell'autorizzazione contenuto buste di tipo ["+ tipoAutorizzazionePerContenuto + "]: " + e.getMessage());
					}
					
					if (auth != null) {
						
						String identitaMittente = null;
						if(credenziali!=null){
							if(!"".equals(credenziali.toString()))
								identitaMittente = credenziali.toString();
						}
						String subjectMessageSecurity = null;
						if(messageSecurityContext!=null){
							subjectMessageSecurity = messageSecurityContext.getSubject();
						}
						
						IDServizio idServizioPerAutorizzazione = getIdServizioPerAutorizzazione(idServizio, soggettoFruitore, functionAsRouter, bustaRichiesta, ruoloBustaRicevuta); 
						IDSoggetto soggettoMittentePerAutorizzazione = getIDSoggettoMittentePerAutorizzazione(idServizio, soggettoFruitore, functionAsRouter, bustaRichiesta, ruoloBustaRicevuta);
												
						String tipoMessaggio = "messaggio";
						if(RuoloBusta.RICEVUTA_RICHIESTA.equals(ruoloBustaRicevuta.toString()) || RuoloBusta.RICEVUTA_RISPOSTA.equals(ruoloBustaRicevuta.toString())){
							tipoMessaggio = "ricevuta asincrona";
						}
						msgDiag.addKeyword(CostantiPdD.KEY_TIPO_MESSAGGIO_BUSTA, tipoMessaggio);
						msgDiag.addKeyword(CostantiPdD.KEY_MITTENTE_E_SERVIZIO_DA_AUTORIZZARE, "FR["+soggettoMittentePerAutorizzazione.toString()+"]->ER["+idServizioPerAutorizzazione.toString()+"]");
						if(identitaMittente!=null)
							msgDiag.addKeyword(CostantiPdD.KEY_CREDENZIALI_MITTENTE_MSG, " inviato da un mittente ["+identitaMittente+"]");
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
							identitaServizioApplicativoFruitore.setIdSoggettoProprietario(soggettoMittentePerAutorizzazione);
							
							datiInvocazione = new DatiInvocazionePortaApplicativa();
							datiInvocazione.setInfoConnettoreIngresso(inRequestContext.getConnettore());
							datiInvocazione.setIdServizio(idServizioPerAutorizzazione);
							datiInvocazione.setState(openspcoopstate.getStatoRichiesta());
							datiInvocazione.setCredenzialiPdDMittente(credenziali);
							datiInvocazione.setIdentitaServizioApplicativoFruitore(identitaServizioApplicativoFruitore);
							datiInvocazione.setSubjectServizioApplicativoFruitoreFromMessageSecurityHeader(subjectMessageSecurity);
							datiInvocazione.setIdPA(idPAbyNome);
							datiInvocazione.setPa(pa);
							datiInvocazione.setIdPD(idPD);
							datiInvocazione.setPd(pd);
							datiInvocazione.setIdSoggettoFruitore(soggettoMittentePerAutorizzazione);
							datiInvocazione.setRuoloBusta(ruoloBustaRicevuta);
						}
						
						// Controllo Autorizzazione
						EsitoAutorizzazioneCooperazione esito = auth.process(datiInvocazione, requestMessage);
						
						if(esito.isServizioAutorizzato()==false){
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
								if(CodiceErroreCooperazione.SICUREZZA_AUTORIZZAZIONE_FALLITA.equals(esito.getErroreCooperazione().getCodiceErrore()) 
										|| CodiceErroreCooperazione.SICUREZZA_FALSIFICAZIONE_MITTENTE.equals(esito.getErroreCooperazione().getCodiceErrore())){
									errorMsg = generaBustaErroreValidazione(parametriGenerazioneBustaErrore);
								}
								else{
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
							if(esito.getDetails()==null){
								msgDiag.addKeyword(CostantiPdD.KEY_DETAILS, "");
							}else{
								msgDiag.addKeyword(CostantiPdD.KEY_DETAILS, " ("+esito.getDetails()+")");
							}
							msgDiag.logPersonalizzato("autorizzazioneContenutiBusteEffettuata");
						}
						
					} else {
						throw new Exception("gestore ["
								+ tipoAutorizzazionePerContenuto + "] non inizializzato");
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
			}
			else{
				msgDiag.logPersonalizzato("autorizzazioneContenutiBusteDisabilitata");
			}
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
						msgRequest.deleteMessageWithLock(msg,propertiesReader.getMsgGiaInProcessamento_AttesaAttiva(),
								propertiesReader.getMsgGiaInProcessamento_CheckInterval());
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
							if(this.msgContext.isGestioneRisposta()){
								
								parametriGenerazioneBustaErrore.setBusta(bustaRichiesta);
								parametriGenerazioneBustaErrore.setErroreIntegrazione(ErroriIntegrazione.ERRORE_537_BUSTA_GIA_RICEVUTA.get537_BustaGiaRicevuta(idMessageRequest));
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
										if(this.msgContext.isGestioneRisposta()){
											this.msgContext.setMessageResponse(this.generaRisposta_msgGiaRicevuto(!this.msgContext.isForzaFiltroDuplicati_msgGiaInProcessamento(),
													bustaRichiesta,infoIntegrazione, msgDiag, openspcoopstate, logCore, configurazionePdDReader,propertiesReader,
													versioneProtocollo,ruoloBustaRicevuta,implementazionePdDMittente,protocolFactory,
													identitaPdD,idTransazione,loader,oneWayVersione11,implementazionePdDMittente,
													tracciamento,messageSecurityContext,
													correlazioneApplicativa));
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
										msgRequest.deleteMessageWithLock(msg,propertiesReader.getMsgGiaInProcessamento_AttesaAttiva()-millisecondiTrascorsi,
												propertiesReader.getMsgGiaInProcessamento_CheckInterval());
										isErrore_MsgGiaRicevuto = false;
										break;
									}

								}

								try{
									Thread.sleep(propertiesReader.getMsgGiaInProcessamento_CheckInterval());
								}catch(Exception eRandom){}
								millisecondiTrascorsi = millisecondiTrascorsi + propertiesReader.getMsgGiaInProcessamento_CheckInterval();

							}
							if(isErrore_MsgGiaRicevuto){
								msgDiag.logPersonalizzato("messaggioInGestione.attesaFineProcessamento.timeoutScaduto");
								if(this.msgContext.isGestioneRisposta()){
									
									parametriGenerazioneBustaErrore.setBusta(bustaRichiesta);
									parametriGenerazioneBustaErrore.setErroreIntegrazione(ErroriIntegrazione.ERRORE_537_BUSTA_GIA_RICEVUTA.get537_BustaGiaRicevuta(idMessageRequest));
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
			msgRequest.registraMessaggio(requestMessage,dataIngressoMessaggio, 
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
						String causa = "Aggiornamento dati busta con id ["+bustaRichiesta.getID()+"] tipo["+tipoMsg+"] non riuscito: "+e.getMessage();
						try{
							GestoreMessaggi.acquireLock(msgDiag, causa, propertiesReader.getMsgGiaInProcessamento_AttesaAttiva(), propertiesReader.getMsgGiaInProcessamento_CheckInterval());
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
								GestoreMessaggi.releaseLock(msgDiag, causa);
							}catch(Exception eUnlock){}
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
				sbustamentoMSG.setErrors(erroriValidazione);
				sbustamentoMSG.setMessaggioErroreProtocollo(isMessaggioErroreProtocollo);
				sbustamentoMSG.setIsBustaDiServizio(bustaDiServizio);
				sbustamentoMSG.setServizioCorrelato(validatore.getServizioCorrelato());
				sbustamentoMSG.setTipoServizioCorrelato(validatore.getTipoServizioCorrelato());
				sbustamentoMSG.setRuoloBustaRicevuta(ruoloBustaRicevuta);
				sbustamentoMSG.setOneWayVersione11(oneWayVersione11);
				sbustamentoMSG.setStateless((oneWayStateless || sincronoStateless || asincronoStateless));
				sbustamentoMSG.setImplementazionePdDSoggettoMittente(implementazionePdDMittente);
				sbustamentoMSG.setImplementazionePdDSoggettoDestinatario(implementazionePdDDestinatario);
				sbustamentoMSG.setPddContext(inRequestContext.getPddContext());
				sbustamentoMSG.setDettaglioEccezione(dettaglioEccezione);
				
				if(validatore.getInfoServizio()!=null){
					sbustamentoMSG.setFiltroDuplicatiRichiestoAccordo(Inoltro.SENZA_DUPLICATI.equals(validatore.getInfoServizio().getInoltro()));
					if(StatoFunzionalitaProtocollo.REGISTRO.equals(moduleManager.getConsegnaAffidabile(bustaRichiesta.getProfiloDiCollaborazione())))
						sbustamentoMSG.setConfermaRicezioneRichiestoAccordo(validatore.getInfoServizio().getConfermaRicezione());
					if(StatoFunzionalitaProtocollo.REGISTRO.equals(moduleManager.getConsegnaInOrdine(bustaRichiesta.getProfiloDiCollaborazione())))	
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
		SOAPVersion versioneSoap = (SOAPVersion) this.msgContext.getPddContext().getObject(org.openspcoop2.core.constants.Costanti.SOAP_VERSION);
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
				this.msgContext.setMessageResponse(SoapUtils.build_Soap_Empty(versioneSoap));
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
				this.msgContext.setMessageResponse(SoapUtils.build_Soap_Empty(versioneSoap));
				if(portaStateless){
					openspcoopstate.releaseResource();
				}
				return;
			} else if( (org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.SINCRONO.equals(bustaRichiesta.getProfiloDiCollaborazione())==false) &&
					newConnectionForResponse &&
					(!oneWayStateless) && (!sincronoStateless) && (!asincronoStateless) ) {
				this.msgContext.setMessageResponse(SoapUtils.build_Soap_Empty(versioneSoap));
				if(portaStateless){
					openspcoopstate.releaseResource();
				}
				return;
			} else {
				if ( isMessaggioErroreProtocollo  ) {
					richiestaRispostaProtocollo = false;
				} else if ( bustaDiServizio  ) {
					richiestaRispostaProtocollo = false;
				} else if( StatoFunzionalitaProtocollo.DISABILITATA.equals(moduleManager.getConsegnaAffidabile(bustaRichiesta.getProfiloDiCollaborazione())) ||
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
		OpenSPCoop2Message responseMessage = SoapUtils.build_Soap_Empty(versioneSoap);
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
						setSOAPFault_processamento(logCore, msgDiag, eDB, "openspcoopstate.updateDatabaseResource");
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
					setSOAPFault_processamento(logCore, msgDiag, e, "openspcoopstate.updateDatabaseResource");
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
							//if( !(bustaRichiesta.getTrasmissione(i).getTipoOrigine().equals(bustaRisposta.getTipoMittente()) &&
							//     bustaRichiesta.getTrasmissione(i).getOrigine().equals(bustaRisposta.getMittente())) ){
							tras.setDestinazione(bustaRichiesta.getTrasmissione(i).getOrigine());
							tras.setTipoDestinazione(bustaRichiesta.getTrasmissione(i).getTipoOrigine());
							//}
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








				/* ------------  Imbustamento  ------------- */	
				msgDiag.mediumDebug("Imbustamento della risposta...");
				SOAPElement headerBustaRisposta = null;
				try{
					boolean gestioneManifestRisposta = false;
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
					if(functionAsRouter && 
							!( identitaPdD.getTipo().equals(bustaRisposta.getTipoMittente()) && identitaPdD.getNome().equals(bustaRisposta.getMittente()) ) 
					){
						// Aggiungo trasmissione solo se la busta e' stata generata dalla porta di dominio destinataria della richiesta.
						// Se il mittente e' il router, logicamente la busta sara' un errore generato dal router
						if( propertiesReader.isGenerazioneListaTrasmissioni(implementazionePdDMittente)){
							headerBustaRisposta = imbustatore.addTrasmissione(responseMessage, tras, readQualifiedAttribute);
						}
						else{
							Validatore v = new Validatore(responseMessage,openspcoopstate.getStatoRichiesta(), logCore, protocolFactory);
							headerBustaRisposta = v.getHeaderProtocollo_senzaControlli();
						}
					}else{
						headerBustaRisposta = imbustatore.imbustamento(openspcoopstate.getStatoRichiesta(),responseMessage,bustaRisposta,infoIntegrazione,
								gestioneManifestRisposta,false,scartaBody,proprietaManifestAttachments);
					}
				}catch(Exception e){
					if(functionAsRouter && 
							!( identitaPdD.getTipo().equals(bustaRisposta.getTipoMittente()) && identitaPdD.getNome().equals(bustaRisposta.getMittente()) ) 
					){
						msgDiag.logErroreGenerico(e,"imbustatore.addTrasmissione(risposta)");
					}else{
						msgDiag.logErroreGenerico(e,"imbustatore.imbustamento(risposta)");
					}
	
					parametriGenerazioneBustaErrore.setBusta(bustaRichiesta);
					parametriGenerazioneBustaErrore.setErroreIntegrazione(ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
							get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_526_GESTIONE_IMBUSTAMENTO));

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
						mtomProcessor.mtomBeforeSecurity(responseMessage, TipoTraccia.RISPOSTA);
					}catch(Exception e){
						// L'errore viene registrato dentro il metodo mtomProcessor.mtomBeforeSecurity
						//msgDiag.logErroreGenerico(e,"MTOMProcessor(BeforeSec-"+mtomProcessor.getMTOMProcessorType()+")");
						
						parametriGenerazioneBustaErrore.setBusta(bustaRichiesta);
						parametriGenerazioneBustaErrore.setErroreIntegrazione(ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
								get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_557_MTOM_PROCESSOR_ERROR));

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
							String tipoSicurezza = SecurityConstants.convertActionToString(flowPropertiesResponse.messageSecurity.getFlowParameters());
							msgDiag.addKeyword(CostantiPdD.KEY_TIPO_SICUREZZA_MESSAGGIO_RISPOSTA, tipoSicurezza);
							pddContext.addObject(CostantiPdD.KEY_TIPO_SICUREZZA_MESSAGGIO_RISPOSTA, tipoSicurezza);
							
							msgDiag.logPersonalizzato("messageSecurity.processamentoRispostaInCorso");
							
							messageSecurityContext.setOutgoingProperties(flowPropertiesResponse.messageSecurity.getFlowParameters());
							if(messageSecurityContext.processOutgoing(responseMessage) == false){
								
								msgDiag.addKeyword(CostantiPdD.KEY_ERRORE_PROCESSAMENTO , messageSecurityContext.getMsgErrore() );
								msgDiag.logPersonalizzato("messageSecurity.processamentoRispostaInErrore");
								
								parametriGenerazioneBustaErrore.setBusta(bustaRichiesta);
								parametriGenerazioneBustaErrore.setErroreCooperazione(ErroriCooperazione.MESSAGE_SECURITY.
										getErroreMessageSecurity(messageSecurityContext.getMsgErrore(), messageSecurityContext.getCodiceErrore()));
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
							msgDiag.addKeyword(CostantiPdD.KEY_ERRORE_PROCESSAMENTO , e.getMessage() );
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
						mtomProcessor.mtomAfterSecurity(responseMessage, TipoTraccia.RISPOSTA);
					}catch(Exception e){
						// L'errore viene registrato dentro il metodo mtomProcessor.mtomAfterSecurity
						//msgDiag.logErroreGenerico(e,"MTOMProcessor(AfterSec-"+mtomProcessor.getMTOMProcessorType()+")");
						
						parametriGenerazioneBustaErrore.setBusta(bustaRichiesta);
						parametriGenerazioneBustaErrore.setErroreIntegrazione(ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
								get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_557_MTOM_PROCESSOR_ERROR));

						OpenSPCoop2Message errorOpenSPCoopMsg = generaBustaErroreProcessamento(parametriGenerazioneBustaErrore,e);
						
						// Nota: la bustaRichiesta e' stata trasformata da generaErroreProcessamento
						parametriInvioBustaErrore.setOpenspcoopMsg(errorOpenSPCoopMsg);
						parametriInvioBustaErrore.setBusta(parametriGenerazioneBustaErrore.getBusta());
						sendRispostaBustaErrore(parametriInvioBustaErrore);
						openspcoopstate.releaseResource();
						return;
					}
				}
				
				
				
				

				/* ---------- Tracciamento Busta Ricevuta ------------- */
				msgDiag.mediumDebug("Tracciamento busta di risposta...");
				if(this.msgContext.isTracciamentoAbilitato()){
					EsitoElaborazioneMessaggioTracciato esitoTraccia = 
							EsitoElaborazioneMessaggioTracciato.getEsitoElaborazioneMessaggioInviato();
					SecurityInfo securityInfoResponse  = null;
					if(functionAsRouter==false){
						if(messageSecurityContext!=null && messageSecurityContext.getDigestReader()!=null){
							IValidazioneSemantica validazioneSemantica = protocolFactory.createValidazioneSemantica();
							securityInfoResponse = validazioneSemantica.readSecurityInformation(messageSecurityContext.getDigestReader(),responseMessage);
						}
					}
					tracciamento.registraRisposta(responseMessage,securityInfoResponse,headerBustaRisposta,bustaRisposta,esitoTraccia,
							Tracciamento.createLocationString(false,this.msgContext.getFromLocation()),
							correlazioneApplicativa,idCorrelazioneApplicativaRisposta);
				}
				IValidatoreErrori validatoreErrori = protocolFactory.createValidatoreErrori();
				IProtocolManager protocolManager = protocolFactory.createProtocolManager();
				ProprietaValidazioneErrori pValidazioneErrori = new ProprietaValidazioneErrori();
				pValidazioneErrori.setIgnoraEccezioniNonGravi(protocolManager.isIgnoraEccezioniNonGravi());
				pValidazioneErrori.setVersioneProtocollo(versioneProtocollo);
				if( validatoreErrori.isBustaErrore(bustaRisposta,responseMessage,pValidazioneErrori) ) 
					msgDiag.logPersonalizzato("generazioneMessaggioErroreRisposta");
				else
					msgDiag.logPersonalizzato("generazioneMessaggioRisposta");
					

				/* --------	Elimino accesso daPdD --------- */
				msgDiag.mediumDebug("Eliminazione accesso da PdD...");
				repositoryBuste.eliminaUtilizzoPdDFromOutBox(bustaRisposta.getID());

			}

			
			
			
			
			
			
			
			/* ----- Header Integrazione ------ */
			if(pa!=null){
				msgDiag.mediumDebug("Gestione header di integrazione messaggio di risposta...");
				HeaderIntegrazione headerIntegrazioneRisposta = new HeaderIntegrazione();
				headerIntegrazioneRisposta.setBusta(new HeaderIntegrazioneBusta());
				headerIntegrazioneRisposta.getBusta().setTipoMittente(bustaRichiesta.getTipoMittente());
				headerIntegrazioneRisposta.getBusta().setMittente(bustaRichiesta.getMittente());
				headerIntegrazioneRisposta.getBusta().setTipoDestinatario(bustaRichiesta.getTipoDestinatario());
				headerIntegrazioneRisposta.getBusta().setDestinatario(bustaRichiesta.getDestinatario());
				headerIntegrazioneRisposta.getBusta().setTipoServizio(bustaRichiesta.getTipoServizio());
				headerIntegrazioneRisposta.getBusta().setServizio(bustaRichiesta.getServizio());
				headerIntegrazioneRisposta.getBusta().setAzione(bustaRichiesta.getAzione());
				headerIntegrazioneRisposta.getBusta().setIdCollaborazione(bustaRichiesta.getCollaborazione());
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
				java.util.Properties propertiesIntegrazioneRisposta = new java.util.Properties();
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
										responseMessage = this.fac.createEmptySOAPMessage(versioneSoap);
										outResponsePAMessage.setMessage(responseMessage);
									}
									gestore.setOutResponseHeader(headerIntegrazioneRisposta,outResponsePAMessage);
								}else{
									// gia effettuato l'update dell'header in InoltroBuste
								}
							}else{
								if(responseMessage==null){
									responseMessage = this.fac.createEmptySOAPMessage(versioneSoap);
									outResponsePAMessage.setMessage(responseMessage);
								}
								gestore.setOutResponseHeader(headerIntegrazioneRisposta,outResponsePAMessage);
							}
						} else {
							msgDiag.logErroreGenerico("Creazione header di integrazione ["+ tipiIntegrazionePA_response[i]+ "] non riuscito, gestore non inizializzato","setHeaderIntegrazioneRisposta");
						}
							
					} catch (Exception e) {
						msgDiag.addKeyword(CostantiPdD.KEY_TIPO_HEADER_INTEGRAZIONE,tipiIntegrazionePA_response[i]);
						msgDiag.addKeyword(CostantiPdD.KEY_ERRORE_PROCESSAMENTO, e.toString());
						msgDiag.logPersonalizzato("headerIntegrazione.letturaFallita");
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
		return generaBustaErrore(parametriGenerazioneBustaErrore);
	}

	private OpenSPCoop2Message generaBustaErroreValidazione(RicezioneBusteParametriGenerazioneBustaErrore parametriGenerazioneBustaErrore){
		parametriGenerazioneBustaErrore.setErroreProcessamento(false);
		return generaBustaErrore(parametriGenerazioneBustaErrore);
	}

	private OpenSPCoop2Message generaBustaErrore(RicezioneBusteParametriGenerazioneBustaErrore parametriGenerazioneBustaErrore){
		String idTransazione = (String) this.msgContext.getPddContext().getObject(org.openspcoop2.core.constants.Costanti.CLUSTER_ID);
		SOAPVersion versioneSoap = (SOAPVersion) this.msgContext.getPddContext().getObject(org.openspcoop2.core.constants.Costanti.SOAP_VERSION);
		
		try{
			OpenSPCoop2Message responseErrorMessage = null;
		
			ErroreCooperazione erroreCooperazione = parametriGenerazioneBustaErrore.getErroreCooperazione();
			ErroreIntegrazione erroreIntegrazione = parametriGenerazioneBustaErrore.getErroreIntegrazione();
			
			List<Eccezione> error = parametriGenerazioneBustaErrore.getError();
			
			Imbustamento imbustatore = parametriGenerazioneBustaErrore.getImbustatore();
			//IOpenSPCoopState state = parametriGenerazioneBustaErrore.getOpenspcoop();
			IDSoggetto identitaPdD = parametriGenerazioneBustaErrore.getIdentitaPdD();
			
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
			if(parametriGenerazioneBustaErrore.isErroreProcessamento()){
				if(erroreCooperazione!=null){
					responseErrorMessage = imbustatore.msgErroreProtocollo_Processamento(openspcoopState.getStatoRichiesta(),
							identitaPdD,this.msgContext.getTipoPorta(),this.msgContext.getIdModulo(),
							parametriGenerazioneBustaErrore.getBusta(),integrazione, idTransazione, erroreCooperazione,
							securityPropertiesResponse,messageSecurityContext,
							propertiesReader.getGestioneSerializableDB_AttesaAttiva(),
							propertiesReader.getGestioneSerializableDB_CheckInterval(),profiloGestione,
							propertiesReader.getTipoTempoBusta(implementazionePdDMittente),
							propertiesReader.isGenerazioneListaTrasmissioni(implementazionePdDMittente),
							parametriGenerazioneBustaErrore.getEccezioneProcessamento(),
							versioneSoap, propertiesReader.isForceSoapPrefixCompatibilitaOpenSPCoopV1());
				}
				else if(erroreIntegrazione!=null){
					responseErrorMessage = imbustatore.msgErroreProtocollo_Processamento(openspcoopState.getStatoRichiesta(),
							identitaPdD,this.msgContext.getTipoPorta(),this.msgContext.getIdModulo(),
							parametriGenerazioneBustaErrore.getBusta(),integrazione, idTransazione, erroreIntegrazione,
							securityPropertiesResponse,messageSecurityContext,
							propertiesReader.getGestioneSerializableDB_AttesaAttiva(),
							propertiesReader.getGestioneSerializableDB_CheckInterval(),profiloGestione,
							propertiesReader.getTipoTempoBusta(implementazionePdDMittente),
							propertiesReader.isGenerazioneListaTrasmissioni(implementazionePdDMittente),
							parametriGenerazioneBustaErrore.getEccezioneProcessamento(),
							versioneSoap, propertiesReader.isForceSoapPrefixCompatibilitaOpenSPCoopV1());
				}else{
					responseErrorMessage = imbustatore.msgErroreProtocollo_Processamento(openspcoopState.getStatoRichiesta(),
							identitaPdD,this.msgContext.getTipoPorta(),this.msgContext.getIdModulo(),
							parametriGenerazioneBustaErrore.getBusta(),integrazione, idTransazione, error,
							securityPropertiesResponse,messageSecurityContext,
							propertiesReader.getGestioneSerializableDB_AttesaAttiva(),
							propertiesReader.getGestioneSerializableDB_CheckInterval(),profiloGestione,
							propertiesReader.getTipoTempoBusta(implementazionePdDMittente),
							propertiesReader.isGenerazioneListaTrasmissioni(implementazionePdDMittente),
							parametriGenerazioneBustaErrore.getEccezioneProcessamento(),
							versioneSoap, propertiesReader.isForceSoapPrefixCompatibilitaOpenSPCoopV1());
				}
			}else{
				if(erroreCooperazione!=null){
					responseErrorMessage = imbustatore.msgErroreProtocollo_Validazione(openspcoopState.getStatoRichiesta(),
							identitaPdD,this.msgContext.getIdModulo(),
							parametriGenerazioneBustaErrore.getBusta(),integrazione, idTransazione,erroreCooperazione,
							securityPropertiesResponse,messageSecurityContext,
							propertiesReader.getGestioneSerializableDB_AttesaAttiva(),
							propertiesReader.getGestioneSerializableDB_CheckInterval(),profiloGestione,
							propertiesReader.getTipoTempoBusta(implementazionePdDMittente),
							propertiesReader.isGenerazioneListaTrasmissioni(implementazionePdDMittente),
							versioneSoap, propertiesReader.isForceSoapPrefixCompatibilitaOpenSPCoopV1());
				}
				else if(erroreIntegrazione!=null){
					throw new Exception("Method 'generaBustaErroreValidazione' not supported for MessaggioErroreIntegrazione");
				}
				else {
					responseErrorMessage = imbustatore.msgErroreProtocollo_Validazione(openspcoopState.getStatoRichiesta(),
							identitaPdD,this.msgContext.getIdModulo(),
							parametriGenerazioneBustaErrore.getBusta(),integrazione, idTransazione,error,
							securityPropertiesResponse,messageSecurityContext,
							propertiesReader.getGestioneSerializableDB_AttesaAttiva(),
							propertiesReader.getGestioneSerializableDB_CheckInterval(),profiloGestione,
							propertiesReader.getTipoTempoBusta(implementazionePdDMittente),
							propertiesReader.isGenerazioneListaTrasmissioni(implementazionePdDMittente),
							versioneSoap, propertiesReader.isForceSoapPrefixCompatibilitaOpenSPCoopV1());
				}
			}

			// ProtocolFactory
			IProtocolFactory protocolFactory = ProtocolFactoryManager.getInstance().getProtocolFactoryByName((String) this.msgContext.getPddContext().getObject(org.openspcoop2.core.constants.Costanti.PROTOCOLLO));
						
			//	Tracciamento Busta Ritornata: cambiata nel metodo msgErroreProcessamento
			if(this.msgContext.isTracciamentoAbilitato()){
				EsitoElaborazioneMessaggioTracciato esitoTraccia = 
						EsitoElaborazioneMessaggioTracciato.getEsitoElaborazioneMessaggioInviato();
				SecurityInfo securityInfoResponse  = null;
				boolean functionAsRouter = false; // In questo caso dovrebbe essere sempre false?
				if(functionAsRouter){
					if(messageSecurityContext!=null && messageSecurityContext.getDigestReader()!=null){
						IValidazioneSemantica validazioneSemantica = protocolFactory.createValidazioneSemantica();
						securityInfoResponse = validazioneSemantica.readSecurityInformation(messageSecurityContext.getDigestReader(),responseErrorMessage);
					}
				}
				Validatore v = new Validatore(responseErrorMessage,openspcoopState.getStatoRichiesta(),
						parametriGenerazioneBustaErrore.getLogCore(), protocolFactory);
				tracciamento.registraRisposta(responseErrorMessage,securityInfoResponse,
						v.getHeaderProtocollo_senzaControlli(), parametriGenerazioneBustaErrore.getBusta(),esitoTraccia,
						Tracciamento.createLocationString(false,this.msgContext.getFromLocation()),
						parametriGenerazioneBustaErrore.getCorrelazioneApplicativa(),
						parametriGenerazioneBustaErrore.getCorrelazioneApplicativaRisposta());
			}

			// Messaggio diagnostico
			msgDiag.addKeywords(parametriGenerazioneBustaErrore.getBusta(),false);
			IProtocolManager protocolManager = protocolFactory.createProtocolManager();
			if( (protocolManager.getKeywordMittenteSconosciuto().equals(parametriGenerazioneBustaErrore.getBusta().getDestinatario())==false) &&
					(protocolManager.getKeywordTipoMittenteSconosciuto().equals(parametriGenerazioneBustaErrore.getBusta().getTipoDestinatario())==false)	){
				msgDiag.logPersonalizzato("generazioneMessaggioErroreRisposta");
			}else{
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
			return this.fac.createFaultMessage(versioneSoap, "ErroreGenerazioneMessaggioRispostaErrore: "+e.getMessage());
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
		
		SOAPVersion versioneSoap = (SOAPVersion) this.msgContext.getPddContext().getObject(org.openspcoop2.core.constants.Costanti.SOAP_VERSION);
		try{
			
			IProtocolFactory protocolFactory = ProtocolFactoryManager.getInstance().getProtocolFactoryByName((String) pddContext.getObject(org.openspcoop2.core.constants.Costanti.PROTOCOLLO));
						

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
				OpenSPCoop2Message soapBodyEmpty = SoapUtils.build_Soap_Empty(versioneSoap);
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
			msgDiag.logErroreGenerico(e, "sendRispostaBustaErrore");
			try{
				IProtocolFactory protocolFactory = ProtocolFactoryManager.getInstance().getProtocolFactoryByName((String) this.msgContext.getPddContext().getObject(org.openspcoop2.core.constants.Costanti.PROTOCOLLO));
				Imbustamento protocolErroreBuilder = new Imbustamento(protocolFactory);
				this.msgContext.setMessageResponse(protocolErroreBuilder.buildSoapFaultProtocollo_processamento(this.msgContext.getIdentitaPdD(), 
						this.msgContext.getTipoPorta(),this.msgContext.getIdModulo(),
						ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.getErroreIntegrazione(), e, 
						versioneSoap, parametriInvioBustaErrore.getPropertiesReader().isForceSoapPrefixCompatibilitaOpenSPCoopV1()));
			}catch(Exception eBustaErrore){
				this.msgContext.setMessageResponse(this.fac.createFaultMessage(versioneSoap, "ErroreSendBustaErrore: "+e.getMessage()));		
			}
		}  	

	}


	/* Utility per la gestione del Message-Security e MTOM */
	/**
	 * Ritorna le Proprieta' Message-Security relative alla ricezione della busta
	 * 
	 * @return Proprieta' Message-Security relative alla ricezione della busta
	 */
	private FlowProperties getFlowPropertiesRequest(Busta bustaRichiesta,
			ConfigurazionePdDManager configurazionePdDReader,StateMessage state,
			MsgDiagnostico msgDiag,Logger logCore,OpenSPCoop2Properties properties,
			RuoloBusta ruoloBustaRicevuta,String implementazionePdDMittente,
			PdDContext pddContext,
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
		flowProperties.tipoMessaggio = TipoTraccia.RICHIESTA;
		
		ProfiloDiCollaborazione profiloCollaborazione = null;
		try{
			
			IProtocolFactory protocolFactory = ProtocolFactoryManager.getInstance().getProtocolFactoryByName((String) pddContext.getObject(org.openspcoop2.core.constants.Costanti.PROTOCOLLO));
						
			// Messaggi AD HOC senza profilo: RISCONTRO
			if(bustaRichiesta.getProfiloDiCollaborazione()==null && bustaRichiesta.sizeListaRiscontri()>0 &&
					properties.isGestioneRiscontri(implementazionePdDMittente)){
				RepositoryBuste repository = new RepositoryBuste(state, true,protocolFactory);
				Integrazione integrazione = repository.getInfoIntegrazioneFromOutBox(bustaRichiesta.getRiscontro(0).getID());
				if(integrazione.getLocationPD()!=null){
					IDPortaDelegata idPD = new IDPortaDelegata();
					idPD.setLocationPD(integrazione.getLocationPD());
					idPD.setSoggettoFruitore(new IDSoggetto(bustaRichiesta.getTipoDestinatario(),bustaRichiesta.getDestinatario()));
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
					IDPortaApplicativa idPA = new IDPortaApplicativa();
					idPA.setIDServizio(new IDServizio(bustaRichiesta.getTipoDestinatario(),bustaRichiesta.getDestinatario(),bustaRichiesta.getTipoServizio(),bustaRichiesta.getServizio(),bustaRichiesta.getAzione()));
					PortaApplicativa pa = paFind;
					if(pa==null){
						IDPortaApplicativaByNome idPAbyNome = configurazionePdDReader.convertTo_SafeMethod(idPA.getIDServizio(), this.msgContext.getProprietaFiltroPortaApplicativa());
						if(idPAbyNome!=null){
							pa = configurazionePdDReader.getPortaApplicativa(idPAbyNome);
						}
					}
					flowProperties.messageSecurity = configurazionePdDReader.getPA_MessageSecurityForReceiver(pa);
					flowProperties.mtom = configurazionePdDReader.getPA_MTOMProcessorForReceiver(pa);
				}
				// Risposta
				else{
					RepositoryBuste repository = new RepositoryBuste(state, false,protocolFactory);
					Integrazione integrazione = repository.getInfoIntegrazioneFromOutBox(bustaRichiesta.getRiferimentoMessaggio());
					IDPortaDelegata idPD = new IDPortaDelegata();
					idPD.setLocationPD(integrazione.getLocationPD());
					idPD.setSoggettoFruitore(new IDSoggetto(bustaRichiesta.getTipoDestinatario(),bustaRichiesta.getDestinatario()));
					PortaDelegata pd = configurazionePdDReader.getPortaDelegata_SafeMethod(idPD);
					flowProperties.messageSecurity = configurazionePdDReader.getPD_MessageSecurityForReceiver(pd);
					flowProperties.mtom = configurazionePdDReader.getPD_MTOMProcessorForReceiver(pd);
					flowProperties.tipoMessaggio = TipoTraccia.RISPOSTA;
				}
			}

			// Profilo Asincrono Simmetrico
			else if(org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.ASINCRONO_SIMMETRICO.equals(bustaRichiesta.getProfiloDiCollaborazione())) {	

				//	Richiesta Asincrona
				if(bustaRichiesta.getRiferimentoMessaggio()==null){

					IDPortaApplicativa idPA = new IDPortaApplicativa();
					idPA.setIDServizio(new IDServizio(bustaRichiesta.getTipoDestinatario(),bustaRichiesta.getDestinatario(),bustaRichiesta.getTipoServizio(),bustaRichiesta.getServizio(),bustaRichiesta.getAzione()));
					PortaApplicativa pa = paFind;
					if(pa==null){
						IDPortaApplicativaByNome idPAbyNome = configurazionePdDReader.convertTo_SafeMethod(idPA.getIDServizio(), this.msgContext.getProprietaFiltroPortaApplicativa());
						if(idPAbyNome!=null){
							pa = configurazionePdDReader.getPortaApplicativa(idPAbyNome);
						}
					}
					flowProperties.messageSecurity = configurazionePdDReader.getPA_MessageSecurityForReceiver(pa);
					flowProperties.mtom = configurazionePdDReader.getPA_MTOMProcessorForReceiver(pa);

				}else{

					//	Risposta Asincrona
					if(RuoloBusta.RISPOSTA.equals(ruoloBustaRicevuta.toString())){

						RepositoryBuste repository = new RepositoryBuste(state, false,protocolFactory);
						Integrazione integrazione = repository.getInfoIntegrazioneFromOutBox(bustaRichiesta.getRiferimentoMessaggio());
						IDPortaDelegata idPD = new IDPortaDelegata();
						idPD.setLocationPD(integrazione.getLocationPD());
						idPD.setSoggettoFruitore(new IDSoggetto(bustaRichiesta.getTipoDestinatario(),bustaRichiesta.getDestinatario()));
						PortaDelegata pd = configurazionePdDReader.getPortaDelegata_SafeMethod(idPD);
						flowProperties.messageSecurity = configurazionePdDReader.getPD_MessageSecurityForSender(pd);
						flowProperties.mtom = configurazionePdDReader.getPD_MTOMProcessorForSender(pd);

					}
					//	Ricevuta alla richiesta.
					else if(RuoloBusta.RICEVUTA_RICHIESTA.equals(ruoloBustaRicevuta.toString())){

						RepositoryBuste repository = new RepositoryBuste(state, true, protocolFactory);
						Integrazione integrazione = repository.getInfoIntegrazioneFromOutBox(bustaRichiesta.getRiferimentoMessaggio());
						IDPortaDelegata idPD = new IDPortaDelegata();
						idPD.setLocationPD(integrazione.getLocationPD());
						idPD.setSoggettoFruitore(new IDSoggetto(bustaRichiesta.getTipoDestinatario(),bustaRichiesta.getDestinatario()));
						PortaDelegata pd = configurazionePdDReader.getPortaDelegata_SafeMethod(idPD);
						flowProperties.messageSecurity = configurazionePdDReader.getPD_MessageSecurityForReceiver(pd);
						flowProperties.mtom = configurazionePdDReader.getPD_MTOMProcessorForReceiver(pd);
						flowProperties.tipoMessaggio = TipoTraccia.RISPOSTA;

					}
					//	Ricevuta alla risposta.
					else if(RuoloBusta.RICEVUTA_RISPOSTA.equals(ruoloBustaRicevuta.toString())){

						RepositoryBuste repository = new RepositoryBuste(state, false, protocolFactory);
						Integrazione integrazione = repository.getInfoIntegrazioneFromOutBox(bustaRichiesta.getRiferimentoMessaggio());
						IDPortaDelegata idPD = new IDPortaDelegata();
						idPD.setLocationPD(integrazione.getLocationPD());
						idPD.setSoggettoFruitore(new IDSoggetto(bustaRichiesta.getTipoDestinatario(),bustaRichiesta.getDestinatario()));
						PortaDelegata pd = configurazionePdDReader.getPortaDelegata_SafeMethod(idPD);
						flowProperties.messageSecurity = configurazionePdDReader.getPD_MessageSecurityForReceiver(pd);
						flowProperties.mtom = configurazionePdDReader.getPD_MTOMProcessorForReceiver(pd);
						flowProperties.tipoMessaggio = TipoTraccia.RISPOSTA;

					}

				}

			}

			// Profilo Asincrono Asimmetrico
			else if(org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.ASINCRONO_ASIMMETRICO.equals(bustaRichiesta.getProfiloDiCollaborazione())) {	

				//	Richiesta Asincrona
				if(bustaRichiesta.getRiferimentoMessaggio()==null){

					IDPortaApplicativa idPA = new IDPortaApplicativa();
					idPA.setIDServizio(new IDServizio(bustaRichiesta.getTipoDestinatario(),bustaRichiesta.getDestinatario(),bustaRichiesta.getTipoServizio(),bustaRichiesta.getServizio(),bustaRichiesta.getAzione()));
					PortaApplicativa pa = paFind;
					if(pa==null){
						IDPortaApplicativaByNome idPAbyNome = configurazionePdDReader.convertTo_SafeMethod(idPA.getIDServizio(), this.msgContext.getProprietaFiltroPortaApplicativa());
						if(idPAbyNome!=null){
							pa = configurazionePdDReader.getPortaApplicativa(idPAbyNome);
						}
					}
					flowProperties.messageSecurity = configurazionePdDReader.getPA_MessageSecurityForReceiver(pa);
					flowProperties.mtom = configurazionePdDReader.getPA_MTOMProcessorForReceiver(pa);

				}else{

					profiloCollaborazione = new ProfiloDiCollaborazione(state,protocolFactory);

					//	Risposta Asincrona
					if(RuoloBusta.RISPOSTA.equals(ruoloBustaRicevuta.toString())){

						IDPortaApplicativa idPA = new IDPortaApplicativa();
						// ConversioneServizio.
						IDServizio idServizioOriginale = profiloCollaborazione.asincronoAsimmetrico_getDatiConsegnaRisposta(bustaRichiesta.getRiferimentoMessaggio());
						idPA.setIDServizio(new IDServizio(bustaRichiesta.getTipoDestinatario(),bustaRichiesta.getDestinatario(),idServizioOriginale.getTipoServizio(),
								idServizioOriginale.getServizio(),idServizioOriginale.getAzione()));
						PortaApplicativa pa = paFind;
						if(pa==null){
							IDPortaApplicativaByNome idPAbyNome = configurazionePdDReader.convertTo_SafeMethod(idPA.getIDServizio(), this.msgContext.getProprietaFiltroPortaApplicativa());
							if(idPAbyNome!=null){
								pa = configurazionePdDReader.getPortaApplicativa(idPAbyNome);
							}
						}
						flowProperties.messageSecurity = configurazionePdDReader.getPA_MessageSecurityForReceiver(pa);
						flowProperties.mtom = configurazionePdDReader.getPA_MTOMProcessorForReceiver(pa);

					}
					//	Ricevuta alla richiesta.
					else if(RuoloBusta.RICEVUTA_RICHIESTA.equals(ruoloBustaRicevuta.toString())){

						RepositoryBuste repository = new RepositoryBuste(state, true,protocolFactory);
						Integrazione integrazione = repository.getInfoIntegrazioneFromOutBox(bustaRichiesta.getRiferimentoMessaggio());
						IDPortaDelegata idPD = new IDPortaDelegata();
						idPD.setLocationPD(integrazione.getLocationPD());
						idPD.setSoggettoFruitore(new IDSoggetto(bustaRichiesta.getTipoDestinatario(),bustaRichiesta.getDestinatario()));
						PortaDelegata pd = configurazionePdDReader.getPortaDelegata_SafeMethod(idPD);
						flowProperties.messageSecurity = configurazionePdDReader.getPD_MessageSecurityForReceiver(pd);
						flowProperties.mtom = configurazionePdDReader.getPD_MTOMProcessorForReceiver(pd);
						flowProperties.tipoMessaggio = TipoTraccia.RISPOSTA;

					}
					//	Ricevuta alla risposta.
					else if(RuoloBusta.RICEVUTA_RISPOSTA.equals(ruoloBustaRicevuta.toString())){

						RepositoryBuste repository = new RepositoryBuste(state, false,protocolFactory);
						Integrazione integrazione = repository.getInfoIntegrazioneFromOutBox(bustaRichiesta.getRiferimentoMessaggio());
						IDPortaDelegata idPD = new IDPortaDelegata();
						idPD.setLocationPD(integrazione.getLocationPD());
						idPD.setSoggettoFruitore(new IDSoggetto(bustaRichiesta.getTipoDestinatario(),bustaRichiesta.getDestinatario()));
						PortaDelegata pd = configurazionePdDReader.getPortaDelegata_SafeMethod(idPD);
						flowProperties.messageSecurity = configurazionePdDReader.getPD_MessageSecurityForReceiver(pd);
						flowProperties.mtom = configurazionePdDReader.getPD_MTOMProcessorForReceiver(pd);
						flowProperties.tipoMessaggio = TipoTraccia.RISPOSTA;

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
	private FlowProperties getFlowPropertiesResponse(Busta bustaRichiesta,
			ConfigurazionePdDManager configurazionePdDReader,StateMessage state,
			MsgDiagnostico msgDiag,Logger logCore,OpenSPCoop2Properties properties,
			RuoloBusta ruoloBustaRicevuta,String implementazionePdDMittente,
			PdDContext pddContext,
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
		flowProperties.tipoMessaggio = TipoTraccia.RISPOSTA;

		// NOTA: La busta che sto gestendo e' la busta che ho ricevuto, non quella che sto inviando!!

		ProfiloDiCollaborazione profiloCollaborazione = null;
		try{

			IProtocolFactory protocolFactory = ProtocolFactoryManager.getInstance().getProtocolFactoryByName((String) pddContext.getObject(org.openspcoop2.core.constants.Costanti.PROTOCOLLO));
						
			// Messaggi AD HOC senza profilo: RISCONTRO
			if(bustaRichiesta.getProfiloDiCollaborazione()==null && bustaRichiesta.sizeListaRiscontri()>0 &&
					properties.isGestioneRiscontri(implementazionePdDMittente)){
				if(bustaRichiesta.getTipoServizio()!=null &&
						bustaRichiesta.getServizio()!=null){
					IDPortaApplicativa idPA = new IDPortaApplicativa();
					idPA.setIDServizio(new IDServizio(bustaRichiesta.getTipoDestinatario(),bustaRichiesta.getDestinatario(),bustaRichiesta.getTipoServizio(),bustaRichiesta.getServizio(),bustaRichiesta.getAzione()));
					PortaApplicativa pa = paFind;
					if(pa==null){
						IDPortaApplicativaByNome idPAbyNome = configurazionePdDReader.convertTo_SafeMethod(idPA.getIDServizio(), this.msgContext.getProprietaFiltroPortaApplicativa());
						if(idPAbyNome!=null){
							pa = configurazionePdDReader.getPortaApplicativa(idPAbyNome);
						}
					}
					flowProperties.messageSecurity = configurazionePdDReader.getPA_MessageSecurityForSender(pa);
					flowProperties.mtom = configurazionePdDReader.getPA_MTOMProcessorForSender(pa);
				}
			}
			// Messaggi con profilo
			else if(org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.ONEWAY.equals(bustaRichiesta.getProfiloDiCollaborazione()) || 
					org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.SINCRONO.equals(bustaRichiesta.getProfiloDiCollaborazione())
			) {	
				IDPortaApplicativa idPA = new IDPortaApplicativa();
				idPA.setIDServizio(new IDServizio(bustaRichiesta.getTipoDestinatario(),bustaRichiesta.getDestinatario(),bustaRichiesta.getTipoServizio(),bustaRichiesta.getServizio(),bustaRichiesta.getAzione()));
				PortaApplicativa pa = paFind;
				if(pa==null){
					IDPortaApplicativaByNome idPAbyNome = configurazionePdDReader.convertTo_SafeMethod(idPA.getIDServizio(), this.msgContext.getProprietaFiltroPortaApplicativa());
					if(idPAbyNome!=null){
						pa = configurazionePdDReader.getPortaApplicativa(idPAbyNome);
					}
				}
				flowProperties.messageSecurity = configurazionePdDReader.getPA_MessageSecurityForSender(pa);
				flowProperties.mtom = configurazionePdDReader.getPA_MTOMProcessorForSender(pa);
			}

			//	 Profilo Asincrono Simmetrico
			else if(org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.ASINCRONO_SIMMETRICO.equals(bustaRichiesta.getProfiloDiCollaborazione())) {	

				//	Ricevuta alla richiesta.
				if(bustaRichiesta.getRiferimentoMessaggio()==null){

					IDPortaApplicativa idPA = new IDPortaApplicativa();
					idPA.setIDServizio(new IDServizio(bustaRichiesta.getTipoDestinatario(),bustaRichiesta.getDestinatario(),bustaRichiesta.getTipoServizio(),bustaRichiesta.getServizio(),bustaRichiesta.getAzione()));
					PortaApplicativa pa = paFind;
					if(pa==null){
						IDPortaApplicativaByNome idPAbyNome = configurazionePdDReader.convertTo_SafeMethod(idPA.getIDServizio(), this.msgContext.getProprietaFiltroPortaApplicativa());
						if(idPAbyNome!=null){
							pa = configurazionePdDReader.getPortaApplicativa(idPAbyNome);
						}
					}
					flowProperties.messageSecurity = configurazionePdDReader.getPA_MessageSecurityForSender(pa);
					flowProperties.mtom = configurazionePdDReader.getPA_MTOMProcessorForSender(pa);

				}

				//	Ricevuta alla risposta.
				else if(RuoloBusta.RISPOSTA.equals(ruoloBustaRicevuta.toString())){

					RepositoryBuste repository = new RepositoryBuste(state, false, protocolFactory);
					Integrazione integrazione = repository.getInfoIntegrazioneFromOutBox(bustaRichiesta.getRiferimentoMessaggio());
					IDPortaDelegata idPD = new IDPortaDelegata();
					idPD.setLocationPD(integrazione.getLocationPD());
					idPD.setSoggettoFruitore(new IDSoggetto(bustaRichiesta.getTipoDestinatario(),bustaRichiesta.getDestinatario()));
					PortaDelegata pd = configurazionePdDReader.getPortaDelegata_SafeMethod(idPD);
					flowProperties.messageSecurity = configurazionePdDReader.getPD_MessageSecurityForSender(pd);
					flowProperties.mtom = configurazionePdDReader.getPD_MTOMProcessorForSender(pd);

				}

			}

			//	Profilo Asincrono Asimmetrico
			else if(org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.ASINCRONO_ASIMMETRICO.equals(bustaRichiesta.getProfiloDiCollaborazione())) {	

				profiloCollaborazione = new ProfiloDiCollaborazione(state, protocolFactory);

				//	Ricevuta alla richiesta.
				if(bustaRichiesta.getRiferimentoMessaggio()==null){

					IDPortaApplicativa idPA = new IDPortaApplicativa();
					idPA.setIDServizio(new IDServizio(bustaRichiesta.getTipoDestinatario(),bustaRichiesta.getDestinatario(),bustaRichiesta.getTipoServizio(),bustaRichiesta.getServizio(),bustaRichiesta.getAzione()));
					PortaApplicativa pa = paFind;
					if(pa==null){
						IDPortaApplicativaByNome idPAbyNome = configurazionePdDReader.convertTo_SafeMethod(idPA.getIDServizio(), this.msgContext.getProprietaFiltroPortaApplicativa());
						if(idPAbyNome!=null){
							pa = configurazionePdDReader.getPortaApplicativa(idPAbyNome);
						}
					}
					flowProperties.messageSecurity = configurazionePdDReader.getPA_MessageSecurityForSender(pa);
					flowProperties.mtom = configurazionePdDReader.getPA_MTOMProcessorForSender(pa);

				}

				//	Ricevuta alla risposta.
				else if(RuoloBusta.RISPOSTA.equals(ruoloBustaRicevuta.toString())){

					IDPortaApplicativa idPA = new IDPortaApplicativa();
					// ConversioneServizio.
					IDServizio idServizioOriginale = profiloCollaborazione.asincronoAsimmetrico_getDatiConsegnaRisposta(bustaRichiesta.getRiferimentoMessaggio());
					idPA.setIDServizio(new IDServizio(bustaRichiesta.getTipoDestinatario(),bustaRichiesta.getDestinatario(),
							idServizioOriginale.getTipoServizio(),idServizioOriginale.getServizio(),idServizioOriginale.getAzione()));
					PortaApplicativa pa = paFind;
					if(pa==null){
						IDPortaApplicativaByNome idPAbyNome = configurazionePdDReader.convertTo_SafeMethod(idPA.getIDServizio(), this.msgContext.getProprietaFiltroPortaApplicativa());
						if(idPAbyNome!=null){
							pa = configurazionePdDReader.getPortaApplicativa(idPAbyNome);
						}
					}
					flowProperties.messageSecurity = configurazionePdDReader.getPA_MessageSecurityForSender(pa);
					flowProperties.mtom = configurazionePdDReader.getPA_MTOMProcessorForSender(pa);

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

		IProtocolFactory protocolFactory = ProtocolFactoryManager.getInstance().getProtocolFactoryByName((String) pddContext.getObject(org.openspcoop2.core.constants.Costanti.PROTOCOLLO));
				
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

			try{
				Thread.sleep(checkInterval);
			}catch(Exception eRandom){}
		}
		return attendiTerminazioneRichiesta;
	}

	private boolean gestioneRispostaAsincrona_checkPresenzaRicevutaRichiesta(long scadenzaControllo,int checkInterval,Busta bustaRichiesta,
			IOpenSPCoopState openspcoopstate,MsgDiagnostico msgDiag,boolean newConnectionForResponse,
			PdDContext pddContext)throws Exception{
		boolean attendiTerminazioneRicevutaRichiesta = false;

		IProtocolFactory protocolFactory = ProtocolFactoryManager.getInstance().getProtocolFactoryByName((String) pddContext.getObject(org.openspcoop2.core.constants.Costanti.PROTOCOLLO));
		
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

			try{
				Thread.sleep(checkInterval);
			}catch(Exception eRandom){}
		}

		return attendiTerminazioneRicevutaRichiesta;
	}


	private OpenSPCoop2Message generaRisposta_msgGiaRicevuto(boolean printMsg,Busta bustaRichiesta,Integrazione integrazione,MsgDiagnostico msgDiag,
			OpenSPCoopState openspcoopstate,Logger log,ConfigurazionePdDManager config,OpenSPCoop2Properties properties, String profiloGestione,
			RuoloBusta ruoloBustaRicevuta,String implementazionePdDMittente,IProtocolFactory protocolFactory,
			IDSoggetto identitaPdD,String idTransazione,Loader loader, boolean oneWayVersione11,
			String implementazionePdDSoggettoMittente,
			Tracciamento tracciamento,MessageSecurityContext messageSecurityContext,
			String idCorrelazioneApplicativa) throws ProtocolException, TracciamentoException{

		RepositoryBuste repositoryBuste = new RepositoryBuste(openspcoopstate.getStatoRichiesta(), true,protocolFactory);
		History historyBuste = new History(openspcoopstate.getStatoRichiesta(), log);
		SOAPVersion versioneSoap = (SOAPVersion) this.msgContext.getPddContext().getObject(org.openspcoop2.core.constants.Costanti.SOAP_VERSION);
		Busta bustaHTTPReply = null;
		
		IProtocolVersionManager protocolManager = 
				ProtocolFactoryManager.getInstance().getProtocolFactoryByName((String) this.msgContext.getPddContext().getObject(org.openspcoop2.core.constants.Costanti.PROTOCOLLO)).createProtocolVersionManager(profiloGestione);
		
		boolean consegnaAffidabile = false;
		switch (protocolManager.getConsegnaAffidabile(bustaRichiesta.getProfiloDiCollaborazione())) {
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
		
		Imbustamento imbustatore = new Imbustamento(log, protocolFactory);
		
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
					imbustatore.buildID(openspcoopstate.getStatoRichiesta(),identitaPdD, idTransazione, 
							properties.getGestioneSerializableDB_AttesaAttiva(),
							properties.getGestioneSerializableDB_CheckInterval(),
							Boolean.FALSE);
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
					imbustatore.buildID(openspcoopstate.getStatoRichiesta(),identitaPdD, idTransazione, 
							properties.getGestioneSerializableDB_AttesaAttiva(),
							properties.getGestioneSerializableDB_CheckInterval(),
							Boolean.FALSE);
			Vector<Eccezione> v = new Vector<Eccezione>();
			v.add(Eccezione.getEccezioneValidazione(ErroriCooperazione.IDENTIFICATIVO_MESSAGGIO_GIA_PROCESSATO.getErroreCooperazione(),protocolFactory));
			bustaHTTPReply = imbustatore.buildMessaggioErroreProtocollo_Validazione(v,bustaRichiesta,id_busta_risposta,
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
						imbustatore.buildID(openspcoopstate.getStatoRichiesta(),identitaPdD, idTransazione, 
								properties.getGestioneSerializableDB_AttesaAttiva(),
								properties.getGestioneSerializableDB_CheckInterval(),
								Boolean.FALSE);
				bustaHTTPReply.setID(id_busta_risposta);
			}
		}
		// 4
		else{
			if( protocolManager.isGenerazioneErroreMessaggioOnewayDuplicato() || properties.isGenerazioneErroreProtocolloFiltroDuplicati(implementazionePdDMittente)){
				http200 = false;
				
				String id_busta_risposta = 
						imbustatore.buildID(openspcoopstate.getStatoRichiesta(),identitaPdD, idTransazione, 
								properties.getGestioneSerializableDB_AttesaAttiva(),
								properties.getGestioneSerializableDB_CheckInterval(),
								Boolean.FALSE);
				Vector<Eccezione> v = new Vector<Eccezione>();
				v.add(Eccezione.getEccezioneValidazione(ErroriCooperazione.IDENTIFICATIVO_MESSAGGIO_GIA_PROCESSATO.getErroreCooperazione(),protocolFactory));
				bustaHTTPReply = imbustatore.buildMessaggioErroreProtocollo_Validazione(v,bustaRichiesta,id_busta_risposta,
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
			return SoapUtils.build_Soap_Empty(versioneSoap);
		}else{
						
			OpenSPCoop2Message msg = null;
			if(http200){
				msg = SoapUtils.build_Soap_Empty(versioneSoap);
			}
			else{
				msg = imbustatore.buildSoapMsgErroreProtocollo_Validazione(versioneSoap, properties.isForceSoapPrefixCompatibilitaOpenSPCoopV1());
			}
			imbustatore.imbustamento(openspcoopstate.getStatoRichiesta(),msg,bustaHTTPReply,integrazione,
					false,false,false,null);
			
			
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
				Validatore v = new Validatore(msg,openspcoopstate.getStatoRichiesta(),
						log, protocolFactory);
				tracciamento.registraRisposta(msg,securityInfoResponse,
						v.getHeaderProtocollo_senzaControlli(), bustaHTTPReply,esitoTraccia,
						Tracciamento.createLocationString(false,this.msgContext.getFromLocation()),
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
			boolean functionAsRouter,Busta bustaRichiesta,RuoloBusta ruoloBustaRicevuta){
		IDServizio idServizioPerAutorizzazione = new IDServizio();
		idServizioPerAutorizzazione.setServizio(idServizio.getServizio());
		idServizioPerAutorizzazione.setTipoServizio(idServizio.getTipoServizio());
		idServizioPerAutorizzazione.setUriAccordo(idServizio.getUriAccordo());
		idServizioPerAutorizzazione.setAzione(idServizio.getAzione());
		idServizioPerAutorizzazione.setTipologiaServizio(idServizio.getTipologiaServizio());

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
					soggettoDestinatarioPerAutorizzazione = new IDSoggetto(soggettoFruitore.getTipo(),soggettoFruitore.getNome(),
							soggettoFruitore.getCodicePorta());
				}
			}
		}else{
			soggettoDestinatarioPerAutorizzazione = new IDSoggetto(idServizio.getSoggettoErogatore().getTipo(),idServizio.getSoggettoErogatore().getNome(),
					idServizio.getSoggettoErogatore().getCodicePorta());
		}

		idServizioPerAutorizzazione.setSoggettoErogatore(soggettoDestinatarioPerAutorizzazione);
		return idServizioPerAutorizzazione;
	}
	
	private IDSoggetto getIDSoggettoMittentePerAutorizzazione(IDServizio idServizio,IDSoggetto soggettoFruitore,
			boolean functionAsRouter,Busta bustaRichiesta,RuoloBusta ruoloBustaRicevuta){
		IDSoggetto soggettoMittentePerAutorizzazione = null;
		if(functionAsRouter){
			soggettoMittentePerAutorizzazione = new IDSoggetto(soggettoFruitore.getTipo(),soggettoFruitore.getNome(),
					soggettoFruitore.getCodicePorta());
		}
		else if(org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.ONEWAY.equals(bustaRichiesta.getProfiloDiCollaborazione()) ||
				org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.SINCRONO.equals(bustaRichiesta.getProfiloDiCollaborazione())){
			soggettoMittentePerAutorizzazione = new IDSoggetto(soggettoFruitore.getTipo(),soggettoFruitore.getNome(),
					soggettoFruitore.getCodicePorta());
		}else if (org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.ASINCRONO_SIMMETRICO.equals(bustaRichiesta.getProfiloDiCollaborazione()) ){
			if(ruoloBustaRicevuta==null){
				// router
				soggettoMittentePerAutorizzazione = new IDSoggetto(soggettoFruitore.getTipo(),soggettoFruitore.getNome(),
						soggettoFruitore.getCodicePorta());
			}else{
				if(RuoloBusta.RICHIESTA.equals(ruoloBustaRicevuta.toString()) || RuoloBusta.RISPOSTA.equals(ruoloBustaRicevuta.toString())){
					soggettoMittentePerAutorizzazione = new IDSoggetto(soggettoFruitore.getTipo(),soggettoFruitore.getNome(),
							soggettoFruitore.getCodicePorta());
				}else{
					soggettoMittentePerAutorizzazione = new IDSoggetto(idServizio.getSoggettoErogatore().getTipo(),idServizio.getSoggettoErogatore().getNome(),
							idServizio.getSoggettoErogatore().getCodicePorta());
				}		
			}
		}else if (org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.ASINCRONO_ASIMMETRICO.equals(bustaRichiesta.getProfiloDiCollaborazione()) ){
			if(ruoloBustaRicevuta==null){
				// router
				soggettoMittentePerAutorizzazione = new IDSoggetto(soggettoFruitore.getTipo(),soggettoFruitore.getNome(),
						soggettoFruitore.getCodicePorta());
			}else{
				if(RuoloBusta.RICHIESTA.equals(ruoloBustaRicevuta.toString()) || RuoloBusta.RISPOSTA.equals(ruoloBustaRicevuta.toString())){
					soggettoMittentePerAutorizzazione = new IDSoggetto(soggettoFruitore.getTipo(),soggettoFruitore.getNome(),
							soggettoFruitore.getCodicePorta());
				}else{
					soggettoMittentePerAutorizzazione = new IDSoggetto(idServizio.getSoggettoErogatore().getTipo(),idServizio.getSoggettoErogatore().getNome(),
							idServizio.getSoggettoErogatore().getCodicePorta());
				}
			}
		}else{
			soggettoMittentePerAutorizzazione = new IDSoggetto(soggettoFruitore.getTipo(),soggettoFruitore.getNome(),
					soggettoFruitore.getCodicePorta());
		}
		return soggettoMittentePerAutorizzazione;
	}
}


