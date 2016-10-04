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



package org.openspcoop2.pdd.mdb;

import java.io.ByteArrayInputStream;
import java.sql.Timestamp;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPFault;

import org.slf4j.Logger;
import org.openspcoop2.core.config.Connettore;
import org.openspcoop2.core.config.GestioneErrore;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.ServizioApplicativo;
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
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.ParseException;
import org.openspcoop2.message.SOAPVersion;
import org.openspcoop2.message.SoapUtils;
import org.openspcoop2.message.mtom.MtomXomReference;
import org.openspcoop2.pdd.config.ClassNameProperties;
import org.openspcoop2.pdd.config.ConfigurazionePdDManager;
import org.openspcoop2.pdd.config.MTOMProcessorConfig;
import org.openspcoop2.pdd.config.MessageSecurityConfig;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
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
import org.openspcoop2.pdd.core.ValidatoreMessaggiApplicativi;
import org.openspcoop2.pdd.core.ValidatoreMessaggiApplicativiException;
import org.openspcoop2.pdd.core.connettori.ConnettoreHTTP;
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
import org.openspcoop2.pdd.logger.Dump;
import org.openspcoop2.pdd.logger.MsgDiagnosticiProperties;
import org.openspcoop2.pdd.logger.MsgDiagnostico;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.pdd.logger.Tracciamento;
import org.openspcoop2.pdd.services.connector.DirectVMProtocolInfo;
import org.openspcoop2.pdd.timers.TimerGestoreMessaggi;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.engine.builder.ErroreApplicativoBuilder;
import org.openspcoop2.protocol.engine.constants.Costanti;
import org.openspcoop2.protocol.engine.driver.RepositoryBuste;
import org.openspcoop2.protocol.engine.validator.Validatore;
import org.openspcoop2.protocol.registry.RegistroServiziManager;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.Eccezione;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.Integrazione;
import org.openspcoop2.protocol.sdk.SecurityInfo;
import org.openspcoop2.protocol.sdk.Trasmissione;
import org.openspcoop2.protocol.sdk.builder.ProprietaErroreApplicativo;
import org.openspcoop2.protocol.sdk.builder.ProprietaManifestAttachments;
import org.openspcoop2.protocol.sdk.config.IProtocolVersionManager;
import org.openspcoop2.protocol.sdk.constants.CodiceErroreCooperazione;
import org.openspcoop2.protocol.sdk.constants.CodiceErroreIntegrazione;
import org.openspcoop2.protocol.sdk.constants.ErroreIntegrazione;
import org.openspcoop2.protocol.sdk.constants.ErroriCooperazione;
import org.openspcoop2.protocol.sdk.constants.ErroriIntegrazione;
import org.openspcoop2.protocol.sdk.constants.Inoltro;
import org.openspcoop2.protocol.sdk.constants.MessaggiFaultErroreCooperazione;
import org.openspcoop2.protocol.sdk.constants.TipoTraccia;
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
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.io.notifier.NotifierInputStreamParams;
import org.openspcoop2.utils.resources.Loader;
import org.openspcoop2.utils.resources.TransportResponseContext;


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
	protected ErroreApplicativoBuilder erroreApplicativoBuilder;

	
	
	
	
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
	//public static java.util.Hashtable<String,IGestoreIntegrazionePD> gestoriIntegrazionePD = null;
	// E' stato aggiunto lo stato dentro l'oggetto.
	public static String[] defaultGestoriIntegrazionePD = null;
	private static Hashtable<String, String[]> defaultPerProtocolloGestoreIntegrazionePD = null;

	/** Indicazione se sono state inizializzate le variabili del servizio */
	public static boolean initializeService = false;

	/**
	 * Inizializzatore del servizio InoltroBuste
	 * 
	 * @throws Exception
	 */
	private synchronized static void initializeService(ClassNameProperties className,OpenSPCoop2Properties propertiesReader) {

		if(InoltroBuste.initializeService)
			return;

		boolean error = false;
		Logger logCore = OpenSPCoop2Logger.getLoggerOpenSPCoopCore();
		Loader loader = Loader.getInstance();
		
		// Inizializzo IGestoreIntegrazionePD list
		InoltroBuste.defaultGestoriIntegrazionePD = propertiesReader.getTipoIntegrazionePD();
		//InoltroBuste.gestoriIntegrazionePD = new java.util.Hashtable<String,IGestoreIntegrazionePD>();
		for(int i=0; i<InoltroBuste.defaultGestoriIntegrazionePD.length; i++){
			String classType = className.getIntegrazionePortaDelegata(InoltroBuste.defaultGestoriIntegrazionePD[i]);
			try{
				IGestoreIntegrazionePD gestore = (IGestoreIntegrazionePD) loader.newInstance(classType);
				gestore.toString();
				logCore.info("Inizializzazione gestore integrazione PdD->servizioApplicativo di tipo "+
						InoltroBuste.defaultGestoriIntegrazionePD[i]+" effettuata.");
			}catch(Exception e){
				logCore.error("Riscontrato errore durante il caricamento della classe ["+classType+
						"] da utilizzare per la gestione dell'integrazione di tipo ["+
						InoltroBuste.defaultGestoriIntegrazionePD[i]+"]: "+e.getMessage());
				error = true;
			}
		}
		
		// Inizializzo IGestoreIntegrazionePD per protocollo
		InoltroBuste.defaultPerProtocolloGestoreIntegrazionePD = new Hashtable<String, String[]>();
		try{
			Enumeration<String> enumProtocols = ProtocolFactoryManager.getInstance().getProtocolNames();
			while (enumProtocols.hasMoreElements()) {
				String protocol = (String) enumProtocols.nextElement();
				String[]tipiIntegrazionePD = propertiesReader.getTipoIntegrazionePD(protocol);
				if(tipiIntegrazionePD!=null && tipiIntegrazionePD.length>0){
					Vector<String> tipiIntegrazionePerProtocollo = new Vector<String>();
					for (int i = 0; i < tipiIntegrazionePD.length; i++) {
						String classType = className.getIntegrazionePortaDelegata(tipiIntegrazionePD[i]);
						try {
							IGestoreIntegrazionePD test = (IGestoreIntegrazionePD)loader.newInstance(classType);
							test.toString();
							tipiIntegrazionePerProtocollo.add(tipiIntegrazionePD[i]);
							logCore	.info("Inizializzazione gestore per lettura integrazione PD di tipo "
									+ tipiIntegrazionePD[i]	+ " effettuata.");
						} catch (Exception e) {
							logCore.error(
									"Riscontrato errore durante il caricamento della classe ["+ classType
									+ "] da utilizzare per la gestione dell'integrazione di tipo ["
									+ tipiIntegrazionePD[i]+ "]: " + e.getMessage());
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

	/**
	 * Aggiorna la lista dei GestoreIntegrazionePD
	 * 
	 * @throws Exception
	 */
// E' stato aggiunto lo stato dentro l'oggetto.
//	private synchronized static void aggiornaListaGestoreIntegrazione(String newTipo,
//			ClassNameProperties className,OpenSPCoop2Properties propertiesReader) throws Exception{
//		if(InoltroBuste.gestoriIntegrazionePD.contains(newTipo))
//			return; // inizializzato da un altro thread
//
//		// Inizializzo IGestoreIntegrazionePD new Type
//		String classType = className.getIntegrazionePortaDelegata(newTipo);
//		Logger logCore = OpenSPCoop2Logger.getLoggerOpenSPCoopCore();
//		Loader loader = Loader.getInstance();
//		try{
//			InoltroBuste.gestoriIntegrazionePD.put(newTipo,((IGestoreIntegrazionePD) loader.newInstance(classType)));
//			logCore.info("Inizializzazione gestore integrazione PdD->servizioApplicativo di tipo "+newTipo+" effettuata.");
//		}catch(Exception e){
//			throw new Exception("Riscontrato errore durante il caricamento della classe ["+classType+
//					"] da utilizzare per la gestione dell'integrazione di tipo ["+newTipo+"]: "+e.getMessage());
//		}
//	}
		
		
		
		
		
		


	@Override
	public EsitoLib _onMessage(IOpenSPCoopState openspcoopstate,
			RegistroServiziManager registroServiziManager,ConfigurazionePdDManager configurazionePdDManager, 
			MsgDiagnostico msgDiag) throws OpenSPCoopStateException {

		EsitoLib esito = new EsitoLib();
		InoltroBusteMessage inoltroBusteMsg = (InoltroBusteMessage) openspcoopstate.getMessageLib();
		
		/* PddContext */
		PdDContext pddContext = inoltroBusteMsg.getPddContext();
		String idTransazione = PdDContext.getValue(org.openspcoop2.core.constants.Costanti.CLUSTER_ID, pddContext);
				
		/* Protocol Factory */
		IProtocolFactory protocolFactory = null;
		org.openspcoop2.protocol.sdk.config.ITraduttore traduttore = null;
		IProtocolVersionManager protocolManager = null;
		IValidazioneSemantica validazioneSemantica = null;
		try{
			protocolFactory = this.protocolFactoryManager.getProtocolFactoryByName((String) pddContext.getObject(org.openspcoop2.core.constants.Costanti.PROTOCOLLO));
			traduttore = protocolFactory.createTraduttore();
			protocolManager = protocolFactory.createProtocolVersionManager(inoltroBusteMsg.getRichiestaDelegata().getProfiloGestione());
			validazioneSemantica = protocolFactory.createValidazioneSemantica();
		}catch(Exception e){
			msgDiag.logErroreGenerico(e, "ProtocolFactory.instanziazione"); 
			openspcoopstate.releaseResource();
			esito.setEsitoInvocazione(false); 
			esito.setStatoInvocazioneErroreNonGestito(e);
			return esito;
		}
		
		SOAPVersion versioneSoap = (SOAPVersion) pddContext.getObject(org.openspcoop2.core.constants.Costanti.SOAP_VERSION);
		
		msgDiag.setPddContext(pddContext, protocolFactory);	
		
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
		
		Integrazione integrazione = new Integrazione();
		integrazione.setIdModuloInAttesa(richiestaDelegata.getIdModuloInAttesa());
		integrazione.setLocationPD(richiestaDelegata.getLocationPD());
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
		msgDiag.setFruitore(richiestaDelegata.getSoggettoFruitore());
		msgDiag.setServizio(richiestaDelegata.getIdServizio());

		ProprietaValidazioneErrori pValidazioneErrori = new ProprietaValidazioneErrori();
		pValidazioneErrori.setIgnoraEccezioniNonGravi(protocolManager.isIgnoraEccezioniNonGravi());
		
		
		try{
			this.erroreApplicativoBuilder = new ErroreApplicativoBuilder(this.log, protocolFactory, 
					identitaPdD, richiestaDelegata.getSoggettoFruitore(), 
					richiestaDelegata.getIdServizio(), 
					this.idModulo, richiestaDelegata.getFault(), 
					versioneSoap,TipoPdD.DELEGATA,richiestaDelegata.getServizioApplicativo());
		}catch(Exception e){
			msgDiag.logErroreGenerico(e, "ErroreApplicativoBuilder.instanziazione"); 
			openspcoopstate.releaseResource();
			esito.setEsitoInvocazione(false); 
			esito.setStatoInvocazioneErroreNonGestito(e);
			return esito;
		}
		
		
		// VM ProtocolInfo (se siamo arrivati da un canale VM)
		if(pddContext!=null && bustaRichiesta!=null)
			DirectVMProtocolInfo.setInfoFromContext(pddContext, bustaRichiesta);
		

		/* ------------------ Inizializzo stato OpenSPCoop  --------------- */
		msgDiag.mediumDebug("Inizializzo stato per la gestione della richiesta...");
		openspcoopstate.initResource(identitaPdD, InoltroBuste.ID_MODULO, idTransazione);
		registroServiziManager.updateState(openspcoopstate.getStatoRichiesta(),openspcoopstate.getStatoRisposta());
		configurazionePdDManager.updateState(openspcoopstate.getStatoRichiesta(),openspcoopstate.getStatoRisposta());
		msgDiag.updateState(openspcoopstate.getStatoRichiesta(),openspcoopstate.getStatoRisposta());






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
		TipoPdD tipoPdD = TipoPdD.DELEGATA;
		if(functionAsRouter){
			tipoPdD = TipoPdD.ROUTER;
			this.erroreApplicativoBuilder.setTipoPdD(tipoPdD);
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
				sa = configurazionePdDManager.getServizioApplicativo(richiestaDelegata.getIdPortaDelegata(), richiestaDelegata.getServizioApplicativo());
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

		
		// Oneway versione 11
		boolean oneWayVersione11 = inoltroBusteMsg.isOneWayVersione11();
		ejbUtils.setOneWayVersione11(oneWayVersione11);
		
		
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
		
		
		//	GestoriMessaggio
		GestoreMessaggi msgRequest = new GestoreMessaggi(openspcoopstate, true, idMessageRequest,Costanti.OUTBOX,msgDiag, pddContext);
		GestoreMessaggi msgResponse = null;
		msgRequest.setPortaDiTipoStateless(portaDiTipoStateless);
		msgRequest.setRoutingStateless(routingStateless);
		msgRequest.setOneWayVersione11(oneWayVersione11);
		
		
		
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
			tracciamento = new org.openspcoop2.pdd.logger.Tracciamento(identitaPdD,InoltroBuste.ID_MODULO,pddContext,tipoPdD,
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
		switch (protocolManager.getConsegnaAffidabile(bustaRichiesta.getProfiloDiCollaborazione())) {
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

		// ProprietaErroreApplicativo (No function Router active)
		ProprietaErroreApplicativo proprietaErroreAppl = null;
		if(functionAsRouter==false){
			proprietaErroreAppl = richiestaDelegata.getFault();
			proprietaErroreAppl.setIdModulo(InoltroBuste.ID_MODULO);
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
			msgDiag.setDelegata(true);
			msgDiag.setPorta(richiestaDelegata.getLocationPD());
			msgDiag.setServizioApplicativo(richiestaDelegata.getServizioApplicativo());
		}

		// Proprieta Manifest Attachments
		ProprietaManifestAttachments proprietaManifestAttachments = 
			this.propertiesReader.getProprietaManifestAttachments(implementazionePdDDestinatario);
		
		// Read QualifiedAttribute
		boolean readQualifiedAttribute = this.propertiesReader.isReadQualifiedAttribute(implementazionePdDDestinatario);

		// Validazione id completa
		boolean validazioneIDBustaCompleta = this.propertiesReader.isValidazioneIDBustaCompleta(implementazionePdDDestinatario);


		// Risposta
		OpenSPCoop2Message responseMessage = null;


		// Punto di inizio per la transazione.
		Validatore validatore = null;
		SOAPElement headerProtocolloRisposta = null;
		IConnettore connectorSenderForDisconnect = null;
		String location = "";
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
								this.erroreApplicativoBuilder.toMessage(ecc,richiestaDelegata.getSoggettoFruitore(),null);
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
			IDSoggetto soggettoFruitore = richiestaDelegata.getSoggettoFruitore();
			IDServizio idServizio = richiestaDelegata.getIdServizio();
			IDAccordo idAccordoServizio = richiestaDelegata.getIdAccordo();
			msgDiag.logPersonalizzato("routingTable.esaminaInCorso");
			
			// ConnectorProperties (Punto di accesso della porta di identitaPdD.getCodicePorta() a cui spedire la busta)
			Connettore connettore = null;
			String erroreRicercaConnettore = null;
			Exception eForwardRoute = null;
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
							(responseMessage!=null ? responseMessage.getParseException() : null));
					esito.setEsitoInvocazione(true);
					esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,
							msgDiag.getMessaggio_replaceKeywords("routingTable.esaminaInCorsoFallita"));
				}else{
					if(sendRispostaApplicativa){
						OpenSPCoop2Message responseMessageError = 
							this.erroreApplicativoBuilder.toMessage(ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
									get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_514_ROUTING_CONFIGURATION_ERROR),eForwardRoute,
									(responseMessage!=null ? responseMessage.getParseException() : null));
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
			OpenSPCoop2Message requestMessage = null;
			TransportResponseContext transportResponseContext = null;
			try{
				requestMessage = msgRequest.getMessage();
			}catch(Exception e){
				msgDiag.logErroreGenerico(e, "msgRequest.getMessage()");
				if(functionAsRouter){
					ejbUtils.sendAsRispostaBustaErroreProcessamento(richiestaDelegata.getIdModuloInAttesa(),bustaRichiesta,
							ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
								get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_509_READ_REQUEST_MSG),
							idCorrelazioneApplicativa,idCorrelazioneApplicativaRisposta,servizioApplicativoFruitore,e,
							(responseMessage!=null ? responseMessage.getParseException() : null));
					esito.setEsitoInvocazione(true);
					esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,"msgRequest.getMessage()");
				}else{
					if(sendRispostaApplicativa){
						OpenSPCoop2Message responseMessageError = 
							this.erroreApplicativoBuilder.toMessage(ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
									get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_509_READ_REQUEST_MSG),e,
									(responseMessage!=null ? responseMessage.getParseException() : null));
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
			HeaderIntegrazione headerIntegrazione = new HeaderIntegrazione();
			headerIntegrazione.getBusta().setTipoMittente(soggettoFruitore.getTipo());
			headerIntegrazione.getBusta().setMittente(soggettoFruitore.getNome());
			headerIntegrazione.getBusta().setTipoDestinatario(idServizio.getSoggettoErogatore().getTipo());
			headerIntegrazione.getBusta().setDestinatario(idServizio.getSoggettoErogatore().getNome());
			headerIntegrazione.getBusta().setTipoServizio(idServizio.getTipoServizio());
			headerIntegrazione.getBusta().setServizio(idServizio.getServizio());
			headerIntegrazione.getBusta().setAzione(idServizio.getAzione());
			headerIntegrazione.getBusta().setID(bustaRichiesta.getID());
			headerIntegrazione.getBusta().setRiferimentoMessaggio(bustaRichiesta.getRiferimentoMessaggio());
			headerIntegrazione.getBusta().setIdCollaborazione(bustaRichiesta.getCollaborazione());
			headerIntegrazione.getBusta().setProfiloDiCollaborazione(bustaRichiesta.getProfiloDiCollaborazione());
			headerIntegrazione.setIdApplicativo(idCorrelazioneApplicativa);
			headerIntegrazione.setServizioApplicativo(servizioApplicativoFruitore);

			java.util.Properties propertiesTrasporto = new java.util.Properties();
			java.util.Properties propertiesUrlBased = new java.util.Properties();

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
							(responseMessage!=null ? responseMessage.getParseException() : null));
					esito.setEsitoInvocazione(true);
					esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,"getTipiIntegrazione(pd)");
				}else{
					if(sendRispostaApplicativa){
						OpenSPCoop2Message responseMessageError = 
							this.erroreApplicativoBuilder.toMessage(ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
									get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_536_CONFIGURAZIONE_NON_DISPONIBILE),e,
									(responseMessage!=null ? responseMessage.getParseException() : null));
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
			outRequestPDMessage.setMessage(requestMessage);
			outRequestPDMessage.setPortaDelegata(pd);
			outRequestPDMessage.setProprietaTrasporto(propertiesTrasporto);
			outRequestPDMessage.setProprietaUrlBased(propertiesUrlBased);
			outRequestPDMessage.setServizio(idServizio);
			outRequestPDMessage.setSoggettoMittente(soggettoFruitore);
			
			for (int i = 0; i < tipiIntegrazionePD.length; i++) {
				try {
//					if (InoltroBuste.gestoriIntegrazionePD.containsKey(tipiIntegrazionePD_risposta[i]) == false)
//						InoltroBuste.aggiornaListaGestoreIntegrazione(
//								tipiIntegrazionePD_risposta[i], ClassNameProperties.getInstance(),
//										this.propertiesReader);
//					IGestoreIntegrazionePD gestore = InoltroBuste.gestoriIntegrazionePD.get(tipiIntegrazionePD_risposta[i]);
					
					String classType = null;
					IGestoreIntegrazionePD gestore = null;
					try{
						classType = ClassNameProperties.getInstance().getIntegrazionePortaDelegata(tipiIntegrazionePD[i]);
						gestore = (IGestoreIntegrazionePD) this.loader.newInstance(classType);
						AbstractCore.init(gestore, pddContext, protocolFactory);
					}catch(Exception e){
						throw new Exception("Riscontrato errore durante il caricamento della classe ["+classType+
								"] da utilizzare per la gestione dell'integrazione di tipo ["+tipiIntegrazionePD[i]+"]: "+e.getMessage());
					}
					
					if(gestore!=null){
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
						msgDiag.logErroreGenerico("Gestore ["+tipiIntegrazionePD[i]+"] non inizializzato (is null)","gestoriIntegrazionePD.get("+tipiIntegrazionePD[i]+")");
					}
					
				} catch (Exception e) {
					this.log.debug("Errore durante la lettura dell'header di integrazione ["+ tipiIntegrazionePD[i]
									+ "]: "+ e.getMessage(),e);
					msgDiag.addKeyword(CostantiPdD.KEY_TIPO_HEADER_INTEGRAZIONE,tipiIntegrazionePD[i]);
					msgDiag.addKeyword(CostantiPdD.KEY_ERRORE_PROCESSAMENTO, e.toString());
					msgDiag.logPersonalizzato("headerIntegrazione.letturaFallita");
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
			SOAPElement headerBustaRichiesta = null;
			try{
				org.openspcoop2.protocol.engine.builder.Imbustamento imbustatore =  new org.openspcoop2.protocol.engine.builder.Imbustamento(protocolFactory);
				if(functionAsRouter){
					if(this.propertiesReader.isGenerazioneListaTrasmissioni(implementazionePdDDestinatario)){
						headerBustaRichiesta = imbustatore.addTrasmissione(requestMessage, tras, readQualifiedAttribute);
					}
					else{
						Validatore v = new Validatore(requestMessage, openspcoopstate.getStatoRichiesta(),this.log, protocolFactory);
						headerBustaRichiesta = v.getHeaderProtocollo_senzaControlli();
					}
				}else{
					headerBustaRichiesta = imbustatore.imbustamento(openspcoopstate.getStatoRichiesta(),requestMessage,bustaRichiesta,integrazione,gestioneManifest,true,
							scartaBody,proprietaManifestAttachments);
				}
			}catch(Exception e){
				String msgErroreImbusta = null;
				if(functionAsRouter)
					msgErroreImbusta = "imbustatore.addTrasmissione";
				else
					msgErroreImbusta = "imbustatore.imbustamento";
				msgDiag.logErroreGenerico(e, msgErroreImbusta);
				if(functionAsRouter){
					ejbUtils.sendAsRispostaBustaErroreProcessamento(richiestaDelegata.getIdModuloInAttesa(),bustaRichiesta,
							ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
								get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_526_GESTIONE_IMBUSTAMENTO),
							idCorrelazioneApplicativa,idCorrelazioneApplicativaRisposta,servizioApplicativoFruitore,e,
							(responseMessage!=null ? responseMessage.getParseException() : null));
					esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,msgErroreImbusta);
					esito.setEsitoInvocazione(true);
				}else{
					if(sendRispostaApplicativa){
						OpenSPCoop2Message responseMessageError = 
							this.erroreApplicativoBuilder.toMessage(ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
									get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_526_GESTIONE_IMBUSTAMENTO),e,
									(responseMessage!=null ? responseMessage.getParseException() : null));
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
							get5XX_ErroreProcessamento(e.getMessage(),CodiceErroreIntegrazione.CODICE_536_CONFIGURAZIONE_NON_DISPONIBILE);
					configException = e;
				}
				
				if(erroreIntegrazione==null){
					try{
						messageSecurityConfig=configurazionePdDManager.getPD_MessageSecurityForSender(pd);
					}catch(Exception e){
						oggetto = "LetturaConfigurazioneMessageSecurityRoleSender";
						erroreIntegrazione = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
								get5XX_ErroreProcessamento(e.getMessage(),CodiceErroreIntegrazione.CODICE_536_CONFIGURAZIONE_NON_DISPONIBILE);
						configException = e;
					}
				}
				
				if(erroreIntegrazione!=null){
					msgDiag.logErroreGenerico(configException, oggetto);
					if(sendRispostaApplicativa){
						OpenSPCoop2Message responseMessageError = null;
						responseMessageError = this.erroreApplicativoBuilder.toMessage(erroreIntegrazione,configException,
								(responseMessage!=null ? responseMessage.getParseException() : null));
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
					mtomProcessor.mtomBeforeSecurity(requestMessage, TipoTraccia.RICHIESTA);
				}catch(Exception e){
					// L'errore viene registrato dentro il metodo mtomProcessor.mtomBeforeSecurity
					// msgDiag.logErroreGenerico(e,"MTOMProcessor(BeforeSec-"+mtomProcessor.getMTOMProcessorType()+")");
				
					ErroreIntegrazione erroreIntegrazione = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
							get5XX_ErroreProcessamento(e.getMessage(),CodiceErroreIntegrazione.CODICE_557_MTOM_PROCESSOR_ERROR);
					if(sendRispostaApplicativa){
						OpenSPCoop2Message responseMessageError = null;
						responseMessageError = this.erroreApplicativoBuilder.toMessage(erroreIntegrazione,e,
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
						String tipoSicurezza = SecurityConstants.convertActionToString(messageSecurityConfig.getFlowParameters());
						msgDiag.addKeyword(CostantiPdD.KEY_TIPO_SICUREZZA_MESSAGGIO_RICHIESTA, tipoSicurezza);
						pddContext.addObject(CostantiPdD.KEY_TIPO_SICUREZZA_MESSAGGIO_RICHIESTA, tipoSicurezza);
						
						msgDiag.mediumDebug("Inizializzazione contesto di Message Security della richiesta ...");
						
						// Imposto un context di Base (utilizzato per la successiva ricezione)
						MessageSecurityContextParameters contextParameters = new MessageSecurityContextParameters();
						contextParameters.setUseActorDefaultIfNotDefined(this.propertiesReader.isGenerazioneActorDefault(implementazionePdDDestinatario));
						contextParameters.setActorDefault(this.propertiesReader.getActorDefault(implementazionePdDDestinatario));
						contextParameters.setLog(this.log);
						contextParameters.setFunctionAsClient(SecurityConstants.SECURITY_CLIENT);
						contextParameters.setPrefixWsuId(this.propertiesReader.getPrefixWsuId());
						contextParameters.setIdFruitore(soggettoFruitore);
						contextParameters.setIdServizio(idServizio);
						contextParameters.setPddFruitore(registroServiziManager.getIdPortaDominio(soggettoFruitore, null));
						contextParameters.setPddErogatore(registroServiziManager.getIdPortaDominio(idServizio.getSoggettoErogatore(), null));
						
						messageSecurityContext = messageSecurityFactory.getMessageSecurityContext(contextParameters);
						messageSecurityContext.setOutgoingProperties(messageSecurityConfig.getFlowParameters());
						
						msgDiag.mediumDebug("Inizializzazione contesto di Message Security della richiesta completata con successo");
						
						msgDiag.logPersonalizzato("messageSecurity.processamentoRichiestaInCorso");
						if(messageSecurityContext.processOutgoing(requestMessage) == false){
							msgErrore = messageSecurityContext.getMsgErrore();
							codiceErroreCooperazione = messageSecurityContext.getCodiceErrore();
							
							msgDiag.addKeyword(CostantiPdD.KEY_ERRORE_PROCESSAMENTO , "["+codiceErroreCooperazione+"] "+msgErrore );
							msgDiag.logPersonalizzato("messageSecurity.processamentoRichiestaInErrore");
						}
						else{
							msgDiag.logPersonalizzato("messageSecurity.processamentoRichiestaEffettuato");
						}
						
					}catch(Exception e){
						
						msgDiag.addKeyword(CostantiPdD.KEY_ERRORE_PROCESSAMENTO , e.getMessage() );
						msgDiag.logPersonalizzato("messageSecurity.processamentoRichiestaInErrore");
						this.log.error("[MessageSecurityRequest]" + e.getMessage(),e);
						
						erroreIntegrazione = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
								get5XX_ErroreProcessamento(e.getMessage(),CodiceErroreIntegrazione.CODICE_536_CONFIGURAZIONE_NON_DISPONIBILE);
						messageSecurityException = e;
					}
				}
				else{
					msgDiag.logPersonalizzato("messageSecurity.processamentoRichiestaDisabilitato");
				}
				if(erroreIntegrazione==null && codiceErroreCooperazione==null){
					if(messageSecurityContext!=null && messageSecurityContext.getDigestReader()!=null){
						try{
							msgDiag.mediumDebug("Lettura informazioni sulla Sicurezza dal Messaggio di richiesta ...");
							securityInfo = validazioneSemantica.readSecurityInformation(messageSecurityContext.getDigestReader(),requestMessage);
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
							responseMessageError = this.erroreApplicativoBuilder.toMessage(erroreIntegrazione,messageSecurityException,
									(responseMessage!=null ? responseMessage.getParseException() : null));
						}else{
							Eccezione ecc = Eccezione.getEccezioneValidazione(ErroriCooperazione.MESSAGE_SECURITY.getErroreMessageSecurity(msgErrore, codiceErroreCooperazione),protocolFactory);
							responseMessageError = this.erroreApplicativoBuilder.toMessage(ecc,richiestaDelegata.getSoggettoFruitore(),null);
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
					mtomProcessor.mtomAfterSecurity(requestMessage, TipoTraccia.RICHIESTA);
				}catch(Exception e){
					// L'errore viene registrato dentro il metodo mtomProcessor.mtomAfterSecurity
					//msgDiag.logErroreGenerico(e,"MTOMProcessor(AfterSec-"+mtomProcessor.getMTOMProcessorType()+")");
				
					ErroreIntegrazione erroreIntegrazione = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
							get5XX_ErroreProcessamento(e.getMessage(),CodiceErroreIntegrazione.CODICE_557_MTOM_PROCESSOR_ERROR);
					if(sendRispostaApplicativa){
						OpenSPCoop2Message responseMessageError = null;
						responseMessageError = this.erroreApplicativoBuilder.toMessage(erroreIntegrazione,e,
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
			ConnettoreMsg connettoreMsg = new ConnettoreMsg(tipoConnector,requestMessage,cps);
			connettoreMsg.setBusta(bustaRichiesta);
			connettoreMsg.setIdModulo(InoltroBuste.ID_MODULO);
			connettoreMsg.setMsgDiagnostico(msgDiag);
			connettoreMsg.setState(openspcoopstate.getStatoRichiesta());
			connettoreMsg.setProtocolFactory(protocolFactory);
			connettoreMsg.setPropertiesTrasporto(propertiesTrasporto);
			connettoreMsg.setPropertiesUrlBased(propertiesUrlBased);
			IConnettore connectorSender = null;

			// Risposte del connettore
			int codiceRitornato = -1;

			// Stato consegna tramite connettore
			boolean errorConsegna = false;
			boolean riconsegna = false;
			java.sql.Timestamp dataRiconsegna = null;
			String motivoErroreConsegna = null;
			boolean invokerNonSupportato = false;
			SOAPFault fault = null;
			Exception eccezioneProcessamentoConnettore = null;

			// Ricerco connettore
			ClassNameProperties prop = ClassNameProperties.getInstance();
			String connectorClass = prop.getConnettore(tipoConnector);
			if(connectorClass == null){
				msgDiag.logErroreGenerico("Connettore non registrato","ClassNameProperties.getConnettore("+tipoConnector+")");
				invokerNonSupportato = true;
			}

			// Carico connettore richiesto
			Exception eInvokerNonSupportato = null;
			if(invokerNonSupportato==false){
				try{
					connectorSender = (IConnettore) this.loader.newInstance(connectorClass);
					AbstractCore.init(connectorSender, pddContext, protocolFactory);
				}catch(Exception e){
					msgDiag.logErroreGenerico(e,"IConnettore.newInstance(tipo:"+tipoConnector+" class:"+connectorClass+")");
					invokerNonSupportato = true;
					eInvokerNonSupportato = e;
				}
				if( (invokerNonSupportato == false) && (connectorSender == null)){
					msgDiag.logErroreGenerico("ConnectorSender is null","IConnettore.newInstance(tipo:"+tipoConnector+" class:"+connectorClass+")");
					invokerNonSupportato = true;
				}
			}

			// Location
			location = ConnettoreUtils.getAndReplaceLocationWithBustaValues(connettoreMsg, bustaRichiesta, this.log);
			if(location!=null){
				msgDiag.addKeyword(CostantiPdD.KEY_LOCATION, ConnettoreUtils.buildLocationWithURLBasedParameter(connettoreMsg.getPropertiesUrlBased(), location));
			}
			else{
				msgDiag.addKeyword(CostantiPdD.KEY_LOCATION, "N.D.");
			}

			// timeout di default
			if(connettoreMsg.getConnectorProperties()==null){
				java.util.Hashtable<String,String> propCon = new java.util.Hashtable<String,String>();
				connettoreMsg.setConnectorProperties(propCon);
			}
			if(connettoreMsg.getConnectorProperties().get(CostantiConnettori.CONNETTORE_CONNECTION_TIMEOUT)==null){
				connettoreMsg.getConnectorProperties().put(CostantiConnettori.CONNETTORE_CONNECTION_TIMEOUT,""+this.propertiesReader.getConnectionTimeout_inoltroBuste());
			}
			if(connettoreMsg.getConnectorProperties().get(CostantiConnettori.CONNETTORE_READ_CONNECTION_TIMEOUT)==null){
				connettoreMsg.getConnectorProperties().put(CostantiConnettori.CONNETTORE_READ_CONNECTION_TIMEOUT,""+this.propertiesReader.getReadConnectionTimeout_inoltroBuste());
			}

			// User-Agent e X-* header
			UtilitiesIntegrazione httpUtilities = UtilitiesIntegrazione.getInstance(this.log);
			if(connettoreMsg.getPropertiesTrasporto()==null){
				Properties trasporto = new Properties();
				connettoreMsg.setPropertiesTrasporto(trasporto);
			}
			httpUtilities.setRequestTransportProperties(null, connettoreMsg.getPropertiesTrasporto(),null);
			
			
			
			
			
			
			
			
			/* ------------------- OutRequestHandler -----------------------*/
			OutRequestContext outRequestContext = null;
			try{
				outRequestContext = new OutRequestContext(this.log,protocolFactory);
				
				// Informazioni connettore in uscita
				InfoConnettoreUscita infoConnettoreUscita = new InfoConnettoreUscita();
				infoConnettoreUscita.setLocation(location);
				infoConnettoreUscita.setProperties(connettoreMsg.getConnectorProperties());
				infoConnettoreUscita.setPropertiesTrasporto(connettoreMsg.getPropertiesTrasporto());
				infoConnettoreUscita.setPropertiesUrlBased(connettoreMsg.getPropertiesUrlBased());
				infoConnettoreUscita.setSbustamentoSoap(connettoreMsg.isSbustamentoSOAP());
				infoConnettoreUscita.setSbustamentoInformazioniProtocollo(connettoreMsg.isSbustamentoInformazioniProtocollo());
				infoConnettoreUscita.setTipoAutenticazione(connettoreMsg.getAutenticazione());
				infoConnettoreUscita.setCredenziali(connettoreMsg.getCredenziali());
				infoConnettoreUscita.setTipoConnettore(connettoreMsg.getTipoConnettore());
				outRequestContext.setConnettore(infoConnettoreUscita);
				
				// Informazioni messaggio
				outRequestContext.setMessaggio(requestMessage);
				
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
					protocolContext.setTipoServizio(idServizio.getTipoServizio());
					protocolContext.setServizio(idServizio.getServizio());
					protocolContext.setVersioneServizio(idServizio.getVersioneServizioAsInt());
					protocolContext.setAzione(idServizio.getAzione());
				}
				if(idAccordoServizio!=null){
					protocolContext.setIdAccordo(idAccordoServizio);
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
				requestMessage = outRequestContext.getMessaggio();
				
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
				if(e instanceof HandlerException){
					HandlerException he = (HandlerException) e;
					if(he.isEmettiDiagnostico()){
						msgDiag.logErroreGenerico(e, ((HandlerException)e).getIdentitaHandler());
					}
					msgErrore = ((HandlerException)e).getIdentitaHandler()+" error: "+msgErrore;
					if((functionAsRouter || sendRispostaApplicativa) && he.isSetErrorMessageInFault()) {
						erroreIntegrazione = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
								get5XX_ErroreProcessamento(he.getMessage(),CodiceErroreIntegrazione.CODICE_543_HANDLER_OUT_REQUEST);
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
							(responseMessage!=null ? responseMessage.getParseException() : null));
					esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,msgErrore);
					esito.setEsitoInvocazione(true);
				}else{
					if(sendRispostaApplicativa){
						if(erroreIntegrazione==null){
							erroreIntegrazione = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
									get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_543_HANDLER_OUT_REQUEST);
						}
						OpenSPCoop2Message responseMessageError = 
							this.erroreApplicativoBuilder.toMessage(erroreIntegrazione,e,
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
			
			
			
			
			
			
			
			
			
			/* --------------- REFRESH LOCATION ----------------- */
			// L'handler puo' aggiornare le properties che contengono le proprieta' del connettore.
			location = ConnettoreUtils.getAndReplaceLocationWithBustaValues(connettoreMsg, bustaRichiesta, this.log);
			if(location!=null){
				msgDiag.addKeyword(CostantiPdD.KEY_LOCATION, ConnettoreUtils.buildLocationWithURLBasedParameter(connettoreMsg.getPropertiesUrlBased(), location));
			}
			else{
				msgDiag.addKeyword(CostantiPdD.KEY_LOCATION, "N.D.");
			}

			
			
			
			
			
			
			
			
			Utilities.printFreeMemory("InoltroBuste - Dump...");
			
			
			
			
			/* ------------------- Dump -----------------------*/
			if(configurazionePdDManager.dumpMessaggi() ){
				Dump dumpApplicativo = new Dump(identitaPdD,InoltroBuste.ID_MODULO,idMessageRequest,
						soggettoFruitore,idServizio,tipoPdD,pddContext,
						openspcoopstate.getStatoRichiesta(),openspcoopstate.getStatoRisposta());
				dumpApplicativo.dumpRichiestaUscita(requestMessage, outRequestContext.getConnettore());
			}
			
			
			
			
			
			
			
			
			
			
			
			
			
			/* ------------------- 
			   Rilascio Risorsa al DB (La riprendero' dopo aver ottenuto la risposta, se necessario) 
			   Le informazioni nel DB sono state utilizzate fino a questo punto solo in lettura.
			   -----------------------*/
			msgDiag.mediumDebug("Rilascio risorse...");
			openspcoopstate.releaseResource();
			
			
			
			
			
			
			
			Utilities.printFreeMemory("InoltroBuste - spedizione..");
			
			
			

			// --------------------- spedizione --------------------------
			if(invokerNonSupportato==false){
				// utilizzo connettore
				msgDiag.logPersonalizzato("inoltroInCorso");
				ejbUtils.setSpedizioneMsgIngresso(new Timestamp(outRequestContext.getDataElaborazioneMessaggio().getTime()));
				errorConsegna = !connectorSender.send(connettoreMsg);
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
					GestioneErrore gestioneConsegnaConnettore =configurazionePdDManager.getGestioneErroreConnettoreComponenteCooperazione();
					GestoreErroreConnettore gestoreErrore = new GestoreErroreConnettore();
					errorConsegna = !gestoreErrore.verificaConsegna(gestioneConsegnaConnettore,motivoErroreConsegna,eccezioneProcessamentoConnettore,connectorSender.getCodiceTrasporto(),connectorSender.getResponse());
					if(errorConsegna){
						motivoErroreConsegna = gestoreErrore.getErrore();
						riconsegna = gestoreErrore.isRiconsegna();
						dataRiconsegna = gestoreErrore.getDataRispedizione();
					}
					// raccolta risultati del connettore
					fault = gestoreErrore.getFault();
					codiceRitornato = connectorSender.getCodiceTrasporto();
					transportResponseContext = new TransportResponseContext(connectorSender.getHeaderTrasporto(), 
							connectorSender.getCodiceTrasporto()+"", connectorSender.getContentLength(), 
							motivoErroreConsegna, connectorSender.getEccezioneProcessamento());
					responseMessage = connectorSender.getResponse();	
					if(responseMessage!=null){
						responseMessage.setTransportRequestContext(requestMessage.getTransportRequestContext());
						responseMessage.setTransportResponseContext(transportResponseContext);
					}			
					// gestione connessione connettore
					if(functionAsRouter){
						RepositoryConnettori.salvaConnettorePA(idMessageRequest, connectorSender);
					}
					else{
						if(sendRispostaApplicativa ) {
							RepositoryConnettori.salvaConnettorePD(idMessageRequest, connectorSender);
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
						// aggiorno
						location = tmpLocation;
					
						if(connectorSender instanceof ConnettoreHTTP){
							if(((ConnettoreHTTP)connectorSender).isSbustamentoApi()){
								location = org.openspcoop2.core.api.utils.Utilities.updateLocation(((ConnettoreHTTP)connectorSender).getHttpMethod(), location);
							}
						}
						msgDiag.addKeyword(CostantiPdD.KEY_LOCATION, location);
					}
				} catch (Exception e) {
					msgDiag.addKeyword(CostantiPdD.KEY_ERRORE_PROCESSAMENTO , "Analisi risposta fallita, "+e.getMessage() );
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
								this.erroreApplicativoBuilder.toMessage(ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
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



			
			
			

			
			
			
			

			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			/* -------- OpenSPCoop2Message Update ------------- */
			try {
				msgDiag.mediumDebug("Aggiornamento del messaggio");
				// NOTA la versione SOAP capirla da requestMessage, la risposta puo' essere null
				NotifierInputStreamParams nParams = null;
				if(invokerNonSupportato==false){
					nParams = connectorSender.getNotifierInputStreamParamsResponse();
				}
				responseMessage = protocolFactory.createProtocolManager().updateOpenSPCoop2MessageResponse(requestMessage.getVersioneSoap(), responseMessage, 
						bustaRichiesta, nParams,
						requestMessage.getTransportRequestContext(),transportResponseContext);
			} catch (Exception e) {
				msgDiag.addKeyword(CostantiPdD.KEY_ERRORE_PROCESSAMENTO , "Aggiornamento messaggio fallito, "+e.getMessage() );
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
							this.erroreApplicativoBuilder.toMessage(ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
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
					inResponseContext = new InResponseContext(this.log,protocolFactory);
					
					// Informazioni sul messaggio di riposta
					if(responseMessage!=null){
						inResponseContext.setMessaggio(responseMessage);					
					}
					
					// Informazioni sulla consegna
					inResponseContext.setErroreConsegna(motivoErroreConsegna);
					inResponseContext.setPropertiesRispostaTrasporto(connectorSender.getHeaderTrasporto());
					inResponseContext.setReturnCode(codiceRitornato);
					
					// Altre informazioni
					if(outRequestContext.getConnettore()!=null){
						outRequestContext.getConnettore().setLocation(location); // aggiorno location ottenuta dal connettore utilizzato
					}
					inResponseContext.setConnettore(outRequestContext.getConnettore());
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
					if(e instanceof HandlerException){
						HandlerException he = (HandlerException) e;
						if(he.isEmettiDiagnostico()){
							msgDiag.logErroreGenerico(e, ((HandlerException)e).getIdentitaHandler());
						}
						msgErrore = ((HandlerException)e).getIdentitaHandler()+" error: "+msgErrore;
						if((functionAsRouter || sendRispostaApplicativa) && he.isSetErrorMessageInFault()) {
							erroreIntegrazione = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
									get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_544_HANDLER_IN_RESPONSE);
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
							OpenSPCoop2Message responseMessageError = 
								this.erroreApplicativoBuilder.toMessage(erroreIntegrazione,e,
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
			}
			
			
			
			
			
			Utilities.printFreeMemory("InoltroBuste - Messaggio di Risposta + Dump...");
			
			
			
			
			
			// --------------------- Messaggio di Risposta + Dump --------------------------
			if(invokerNonSupportato==false){
				
				// Leggo informazioni di trasporto
				codiceRitornato = inResponseContext.getReturnCode();
				motivoErroreConsegna = inResponseContext.getErroreConsegna();
				responseMessage = inResponseContext.getMessaggio();
			
				// dump applicativo
				if(responseMessage!=null && configurazionePdDManager.dumpMessaggi() ){
					Dump dumpApplicativo = new Dump(identitaPdD,InoltroBuste.ID_MODULO,idMessageRequest,
							soggettoFruitore,idServizio,tipoPdD,pddContext,
							openspcoopstate.getStatoRichiesta(),openspcoopstate.getStatoRisposta());
					dumpApplicativo.dumpRispostaIngresso(responseMessage, inResponseContext.getConnettore(), inResponseContext.getPropertiesRispostaTrasporto());
				}
				
			}

			
			
			
			
			
			




			/* ------------  Tracciamento Richiesta e Messaggio Diagnostico ------------- */
			if(invokerNonSupportato==false){// && errorConsegna==false){

				
				String msgErroreSituazioneAnomale = null;

				// Tracciamento effettuato:

				// - SEMPRE se vi e' da tornare una risposta applicativa (sendRispostaApplicativa=true)

				// - SEMPRE se si ha e' un router: functionAsRouter

				// - SEMPPRE se non vi sono errori (errorConsegna=false)

				// Settings msgDiag
				if(errorConsegna){
					// Una busta contiene per forza di cose un SOAPFault
					// Una busta la si riconosce dal motivo di errore.
					if(fault!=null && fault.getFaultString()!=null && 
							(fault.getFaultString().equals(traduttore.toString(MessaggiFaultErroreCooperazione.FAULT_STRING_VALIDAZIONE)) || fault.getFaultString().equals(traduttore.toString(MessaggiFaultErroreCooperazione.FAULT_STRING_PROCESSAMENTO)))){
						msgDiag.addKeyword(CostantiPdD.KEY_ERRORE_CONSEGNA, fault.getFaultString());
						msgDiag.logPersonalizzato("inoltroConErrore");
					}else{
						msgDiag.addKeyword(CostantiPdD.KEY_ERRORE_CONSEGNA, motivoErroreConsegna);
						msgDiag.logPersonalizzato("inoltroConErrore");
						
						// Controllo Situazione Anomala ISSUE OP-7
						if(responseMessage!=null && responseMessage.getSOAPPart()!=null && 
								responseMessage.getSOAPPart().getEnvelope()!=null &&
								(responseMessage.getSOAPPart().getEnvelope().getBody()==null || (!responseMessage.getSOAPPart().getEnvelope().getBody().hasFault()))){
							msgDiag.logPersonalizzato("comportamentoAnomalo.erroreConsegna.ricezioneMessaggioDiversoFault");
							if(isBlockedTransaction_responseMessageWithTransportCodeError){
								msgErroreSituazioneAnomale = msgDiag.getMessaggio_replaceKeywords("comportamentoAnomalo.erroreConsegna.ricezioneMessaggioDiversoFault");
								this.log.error(msgErroreSituazioneAnomale);
							}
						}
					}
				}else{	
					if(fault!=null && fault.getFaultString()!=null && 
							(fault.getFaultString().equals(traduttore.toString(MessaggiFaultErroreCooperazione.FAULT_STRING_VALIDAZIONE))) ){
						msgDiag.addKeyword(CostantiPdD.KEY_ERRORE_CONSEGNA, fault.getFaultString());
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
						( fault!=null) ){
					msgDiag.mediumDebug("Tracciamento della richiesta...");
					EsitoElaborazioneMessaggioTracciato esitoTraccia = null;
					if(errorConsegna){
						esitoTraccia = EsitoElaborazioneMessaggioTracciato.getEsitoElaborazioneConErrore(msgDiag.getMessaggio_replaceKeywords("inoltroConErrore"));
					}else{
						if(fault!=null && fault.getFaultString()!=null && 
								(fault.getFaultString().equals(traduttore.toString(MessaggiFaultErroreCooperazione.FAULT_STRING_VALIDAZIONE))) ){
							esitoTraccia = EsitoElaborazioneMessaggioTracciato.getEsitoElaborazioneConErrore(msgDiag.getMessaggio_replaceKeywords("inoltroConErrore"));
						}else{
							esitoTraccia = EsitoElaborazioneMessaggioTracciato.getEsitoElaborazioneMessaggioInviato();
						}
					}
					tracciamento.registraRichiesta(requestMessage,securityInfo,headerBustaRichiesta,bustaRichiesta,esitoTraccia,
							Tracciamento.createLocationString(false, location),
							idCorrelazioneApplicativa);
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
								this.erroreApplicativoBuilder.toMessage(ErroriIntegrazione.ERRORE_559_RICEVUTA_RISPOSTA_CON_ERRORE_TRASPORTO.
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
			SOAPElement headerBustaRisposta = null;
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
				validatore = new Validatore(responseMessage,property,openspcoopstate.getStatoRisposta(),readQualifiedAttribute, protocolFactory);
						
				msgDiag.logPersonalizzato("validazioneSintattica");
				presenzaRispostaProtocollo  = validatore.validazioneSintattica(bustaRichiesta, Boolean.FALSE);
				if(presenzaRispostaProtocollo){
					headerBustaRisposta = validatore.getHeaderProtocollo();
					msgDiag.addKeywords(validatore.getBusta(), false);
				}else{
					if(validatore.getErrore()!=null){
						this.log.debug("Messaggio non riconosciuto come busta ("+traduttore.toString(validatore.getErrore().getCodiceErrore())
								+"): "+validatore.getErrore().getDescrizione(protocolFactory));
					}
				}
				
				if(presenzaRispostaProtocollo){
					try{
						// ulteriore controllo per evitare che il protocollo trasparente generi una busta di risposta per il profilo oneway
						presenzaRispostaProtocollo = protocolFactory.createValidazioneSintattica().
								verifyProtocolPresence(tipoPdD,bustaRichiesta.getProfiloDiCollaborazione(),false,responseMessage);
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
								get5XX_ErroreProcessamento(e.getMessage(),CodiceErroreIntegrazione.CODICE_536_CONFIGURAZIONE_NON_DISPONIBILE);
						configException = e;
					}					
					try{
						messageSecurityConfig=configurazionePdDManager.getPD_MessageSecurityForReceiver(pd);
					}catch(Exception e){
						oggetto = "LetturaConfigurazioneMessageSecurityRoleReceiver";
						erroreIntegrazioneConfig = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
								get5XX_ErroreProcessamento(e.getMessage(),CodiceErroreIntegrazione.CODICE_536_CONFIGURAZIONE_NON_DISPONIBILE);
						configException = e;
					}
						
					if(erroreIntegrazioneConfig!=null){
						msgDiag.logErroreGenerico(configException, oggetto);
					
						if(sendRispostaApplicativa){
							OpenSPCoop2Message responseMessageError = 
								this.erroreApplicativoBuilder.toMessage(erroreIntegrazioneConfig,configException,
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
							mtomProcessor.mtomBeforeSecurity(responseMessage, TipoTraccia.RISPOSTA);
						}catch(Exception e){
							// L'errore viene registrato dentro il metodo mtomProcessor.mtomBeforeSecurity
							// msgDiag.logErroreGenerico(e,"MTOMProcessor(BeforeSec-"+mtomProcessor.getMTOMProcessorType()+")");
						
							ErroreIntegrazione erroreIntegrazione = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
									get5XX_ErroreProcessamento(e.getMessage(),CodiceErroreIntegrazione.CODICE_557_MTOM_PROCESSOR_ERROR);
							if(sendRispostaApplicativa){
								OpenSPCoop2Message responseMessageError = this.erroreApplicativoBuilder.toMessage(erroreIntegrazione,e,
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
							String tipoSicurezza = SecurityConstants.convertActionToString(messageSecurityConfig.getFlowParameters());
							msgDiag.addKeyword(CostantiPdD.KEY_TIPO_SICUREZZA_MESSAGGIO_RISPOSTA, tipoSicurezza);
							pddContext.addObject(CostantiPdD.KEY_TIPO_SICUREZZA_MESSAGGIO_RISPOSTA, tipoSicurezza);
							
							msgDiag.mediumDebug("Inizializzazione contesto di Message Security della risposta ...");
														
							if(messageSecurityContext==null){
								// se non vi era la richiesta di MessageSecurity
								MessageSecurityContextParameters contextParameters = new MessageSecurityContextParameters();
								contextParameters.setUseActorDefaultIfNotDefined(this.propertiesReader.isGenerazioneActorDefault(implementazionePdDDestinatario));
								contextParameters.setActorDefault(this.propertiesReader.getActorDefault(implementazionePdDDestinatario));
								contextParameters.setLog(this.log);
								contextParameters.setFunctionAsClient(SecurityConstants.SECURITY_CLIENT);
								contextParameters.setPrefixWsuId(this.propertiesReader.getPrefixWsuId());
								contextParameters.setIdFruitore(soggettoFruitore);
								contextParameters.setIdServizio(idServizio);
								contextParameters.setPddFruitore(registroServiziManager.getIdPortaDominio(soggettoFruitore, null));
								contextParameters.setPddErogatore(registroServiziManager.getIdPortaDominio(idServizio.getSoggettoErogatore(), null));
								messageSecurityContext = new MessageSecurityFactory().getMessageSecurityContext(contextParameters);
							}
							messageSecurityContext.setIncomingProperties(messageSecurityConfig.getFlowParameters());  
							
							msgDiag.mediumDebug("Inizializzazione contesto di Message Security della richiesta completata con successo");
							
						}catch(Exception e){
							msgDiag.logErroreGenerico(e,"InizializzazioneContestoSicurezzaRisposta");
							if(sendRispostaApplicativa){
								OpenSPCoop2Message responseMessageError = 
									this.erroreApplicativoBuilder.toMessage(ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
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
						if(messageSecurityContext!=null && messageSecurityContext.getDigestReader()!=null){
							msgDiag.mediumDebug("Lettura informazioni sulla Sicurezza dal Messaggio di risposta ...");
							securityInfoResponse = validazioneSemantica.readSecurityInformation(messageSecurityContext.getDigestReader(),responseMessage);
							msgDiag.mediumDebug("Lettura informazioni sulla Sicurezza dal Messaggio di risposta completata con successo");
						}
					}catch(Exception e){
						msgDiag.logErroreGenerico(e,"ErroreLetturaInformazioniSicurezza");
						if(sendRispostaApplicativa){
							OpenSPCoop2Message responseMessageError = 
								this.erroreApplicativoBuilder.toMessage(ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
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
					presenzaRispostaProtocollo = validatore.validazioneSemantica_beforeMessageSecurity(true, profiloGestione);
					
					if(validatore.isRilevatiErroriDuranteValidazioneSemantica()==false){
						
						if(messageSecurityContext!= null && messageSecurityContext.getIncomingProperties() != null && messageSecurityContext.getIncomingProperties().size() > 0){
						
							msgDiag.logPersonalizzato("messageSecurity.processamentoRispostaInCorso");
							
							StringBuffer bfErroreSecurity = new StringBuffer();
							presenzaRispostaProtocollo = validatore.validazioneSemantica_messageSecurity_process(messageSecurityContext, bfErroreSecurity);
							
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
							mtomProcessor.mtomAfterSecurity(responseMessage, TipoTraccia.RISPOSTA);
						}catch(Exception e){
							// L'errore viene registrato dentro il metodo mtomProcessor.mtomAfterSecurity
							// msgDiag.logErroreGenerico(e,"MTOMProcessor(AfterSec-"+mtomProcessor.getMTOMProcessorType()+")");
						
							ErroreIntegrazione erroreIntegrazione = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
									get5XX_ErroreProcessamento(e.getMessage(),CodiceErroreIntegrazione.CODICE_557_MTOM_PROCESSOR_ERROR);
							if(sendRispostaApplicativa){
								OpenSPCoop2Message responseMessageError = this.erroreApplicativoBuilder.toMessage(erroreIntegrazione,e,
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
							this.erroreApplicativoBuilder.toMessage(ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
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
						ejbUtils.sendAsRispostaBustaErroreProcessamento(richiestaDelegata.getIdModuloInAttesa(),bustaRichiesta,
								ErroriIntegrazione.ERRORE_516_CONNETTORE_UTILIZZO_CON_ERRORE.
									get516_PortaDiDominioNonDisponibile(bustaRichiesta.getTipoDestinatario()+bustaRichiesta.getDestinatario()),
								idCorrelazioneApplicativa,idCorrelazioneApplicativaRisposta,servizioApplicativoFruitore,eccezioneProcessamentoConnettore,
								(responseMessage!=null ? responseMessage.getParseException() : null));
						openspcoopstate.releaseResource();
						esito.setEsitoInvocazione(true);
						esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,erroreConnettore);
						return esito;
					}
				}else{
					//	Effettuo log dell'eventuale fault (registro anche i fault spcoop, potrebbero contenere dei details aggiunti da una PdD.)
					if( fault!=null && faultLogged==false ){
						msgDiag.addKeyword(CostantiPdD.KEY_SOAP_FAULT, SoapUtils.toString(fault));
						msgDiag.logPersonalizzato("ricezioneSoapFault");
						faultLogged = true;
					}
					if(sendRispostaApplicativa){
						if(responseMessage==null){
							if(requestMessage.getParseException()!=null){
								pddContext.addObject(org.openspcoop2.core.constants.Costanti.CONTENUTO_RICHIESTA_NON_RICONOSCIUTO, true);
								OpenSPCoop2Message responseMessageError = 
									this.erroreApplicativoBuilder.toMessage(
											ErroriIntegrazione.ERRORE_432_PARSING_EXCEPTION_RICHIESTA.getErrore432_MessaggioRichiestaMalformato(requestMessage.getParseException().getParseException()),
											requestMessage.getParseException().getParseException(),
											requestMessage.getParseException());
								ejbUtils.sendRispostaApplicativaErrore(responseMessageError,richiestaDelegata,rollbackRichiesta,pd,sa);
								openspcoopstate.releaseResource();
								esito.setEsitoInvocazione(true);	
								esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,requestMessage.getParseException().getParseException().getMessage());
								return esito;
							}
							else if(pddContext.containsKey(org.openspcoop2.core.constants.Costanti.CONTENUTO_RISPOSTA_NON_RICONOSCIUTO_PARSE_EXCEPTION)){
								ParseException parseException = (ParseException) pddContext.getObject(org.openspcoop2.core.constants.Costanti.CONTENUTO_RISPOSTA_NON_RICONOSCIUTO_PARSE_EXCEPTION);
								pddContext.addObject(org.openspcoop2.core.constants.Costanti.CONTENUTO_RISPOSTA_NON_RICONOSCIUTO, true);
								OpenSPCoop2Message responseMessageError = 
									this.erroreApplicativoBuilder.toMessage(
											ErroriIntegrazione.ERRORE_440_PARSING_EXCEPTION_RISPOSTA.getErrore440_MessaggioRispostaMalformato(parseException.getParseException()),
											parseException.getParseException(),
											parseException);
								ejbUtils.sendRispostaApplicativaErrore(responseMessageError,richiestaDelegata,rollbackRichiesta,pd,sa);
								openspcoopstate.releaseResource();
								esito.setEsitoInvocazione(true);	
								esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,parseException.getParseException().getMessage());
								return esito;
							}
							else {
								OpenSPCoop2Message responseMessageError = 
									this.erroreApplicativoBuilder.toMessage(ErroriIntegrazione.ERRORE_516_CONNETTORE_UTILIZZO_CON_ERRORE.
											get516_PortaDiDominioNonDisponibile(bustaRichiesta.getTipoDestinatario()+bustaRichiesta.getDestinatario()),
														eccezioneProcessamentoConnettore,
														(responseMessage!=null ? responseMessage.getParseException() : null));
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
									this.erroreApplicativoBuilder.toMessage(
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
			
			HeaderIntegrazione headerIntegrazioneRisposta = new HeaderIntegrazione();
			
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
							this.erroreApplicativoBuilder.toMessage(ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
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
				inResponsePDMessage.setProprietaTrasporto(connectorSender.getHeaderTrasporto());
				inResponsePDMessage.setServizio(idServizio);
				inResponsePDMessage.setSoggettoMittente(soggettoFruitore);
				for (int i = 0; i < tipiIntegrazionePD_risposta.length; i++) {
					try {
//						if (InoltroBuste.gestoriIntegrazionePD.containsKey(tipiIntegrazionePD_risposta[i]) == false)
//							InoltroBuste.aggiornaListaGestoreIntegrazione(
//									tipiIntegrazionePD_risposta[i], ClassNameProperties.getInstance(),
//											this.propertiesReader);
//						IGestoreIntegrazionePD gestore = InoltroBuste.gestoriIntegrazionePD.get(tipiIntegrazionePD_risposta[i]);
						
						String classType = null;
						IGestoreIntegrazionePD gestore = null;
						try{
							classType = ClassNameProperties.getInstance().getIntegrazionePortaDelegata(tipiIntegrazionePD_risposta[i]);
							gestore = (IGestoreIntegrazionePD) this.loader.newInstance(classType);
							AbstractCore.init(gestore, pddContext, protocolFactory);
						}catch(Exception e){
							throw new Exception("Riscontrato errore durante il caricamento della classe ["+classType+
									"] da utilizzare per la gestione dell'integrazione di tipo ["+tipiIntegrazionePD_risposta[i]+"]: "+e.getMessage());
						}
						
						if (gestore != null) {
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
						msgDiag.addKeyword(CostantiPdD.KEY_ERRORE_PROCESSAMENTO, e.toString());
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
									this.log,soggettoFruitore,idServizio, servizioApplicativoFruitore,protocolFactory);
					
					SOAPEnvelope soapEnvelopeResponse = null;
					if(responseMessage!=null){
						soapEnvelopeResponse = responseMessage.getSOAPPart().getEnvelope();
					}
					
					gestoreCorrelazione.verificaCorrelazioneRisposta(pd.getCorrelazioneApplicativaRisposta(), soapEnvelopeResponse, headerIntegrazioneRisposta, false);
					
					idCorrelazioneApplicativaRisposta = gestoreCorrelazione.getIdCorrelazione();
					
					msgDiag.setIdCorrelazioneRisposta(idCorrelazioneApplicativaRisposta);
					msgDiag.logCorrelazioneApplicativaRisposta();
					if(richiestaDelegata!=null){
						richiestaDelegata.setIdCorrelazioneApplicativaRisposta(idCorrelazioneApplicativaRisposta);
					}
					
				}catch(Exception e){
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
							this.erroreApplicativoBuilder.toMessage(errore,e,
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









			Utilities.printFreeMemory("InoltroBuste - Gestione Risposta... ");








			/* ---------------- Gestione Risposta  (operazioni comuni per tutti i profili) ------------------- */ 
			boolean isMessaggioErroreProtocollo = false;
			boolean bustaDiServizio = false;
			String idMessageResponse = null;
			java.util.Vector<Eccezione> erroriValidazione = null;
			java.util.Vector<Eccezione> erroriProcessamento = null;
			SbustamentoRisposteMessage sbustamentoRisposteMSG = null;
			Busta bustaRisposta = null;
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
				org.openspcoop2.pdd.core.Utilities.refreshIdentificativiPorta(bustaRisposta, this.propertiesReader.getIdentitaPortaDefault(protocolFactory.getProtocol()), registroServiziManager, protocolFactory);

				isMessaggioErroreProtocollo = validatore.isErroreProtocollo();
				bustaDiServizio = validatore.isBustaDiServizio();
				erroriValidazione = validatore.getEccezioniValidazione();
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
					StringBuffer errore = new StringBuffer();
					Eccezione ecc = null;
					if( erroriProcessamento.size()>1 ){
						ecc = Eccezione.getEccezioneProcessamento(ErroriCooperazione.ERRORE_GENERICO_PROCESSAMENTO_MESSAGGIO.getErroreCooperazione(), protocolFactory);
						for(int k=0; k<erroriProcessamento.size();k++){
							Eccezione eccProcessamento = erroriProcessamento.get(k);
							
							if(eccProcessamento.getDescrizione(protocolFactory)!=null){
								errore.append("["+traduttore.toString(eccProcessamento.getCodiceEccezione(),eccProcessamento.getSubCodiceEccezione())+"] "+eccProcessamento.getDescrizione(protocolFactory)+"\n");
							}
							
						}
						if(errore.length()>0)
							ecc.setDescrizione(errore.toString());		
					}else{
						ecc = erroriProcessamento.get(0);
						if(ecc.getDescrizione(protocolFactory)!=null){
							errore.append("["+traduttore.toString(ecc.getCodiceEccezione(),ecc.getSubCodiceEccezione())+"] "+ecc.getDescrizione(protocolFactory)+"\n");
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
					tracciamento.registraRisposta(responseMessage,securityInfoResponse,headerBustaRisposta,bustaRisposta,esitoTraccia,
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
										this.erroreApplicativoBuilder.toMessage(ecc,richiestaDelegata.getSoggettoFruitore(),null,
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

				// Estrazione header busta
				msgDiag.mediumDebug("Sbustamento della risposta...");
				try{
					boolean gestioneManifestRisposta = false;
					if(functionAsRouter==false && sbustamentoInformazioniProtocolloRisposta){
						gestioneManifestRisposta = configurazionePdDManager.isGestioneManifestAttachments(pd,protocolFactory);
						// GestioneManifest solo se ho ricevuto una busta correttamente formata nel manifest
						boolean sbustamentoManifestRisposta = gestioneManifestRisposta;
						for(int k = 0; k < erroriValidazione.size() ; k++){
							Eccezione er = erroriValidazione.get(k);
							if(CodiceErroreCooperazione.isEccezioneAllegati(er.getCodiceEccezione())){
								sbustamentoManifestRisposta = false;
							}
						}	
						org.openspcoop2.protocol.engine.builder.Sbustamento sbustatore = new org.openspcoop2.protocol.engine.builder.Sbustamento(protocolFactory);
						headerProtocolloRisposta = sbustatore.sbustamento(openspcoopstate.getStatoRichiesta(),responseMessage,bustaRisposta,false,sbustamentoManifestRisposta,proprietaManifestAttachments);
					}else{
						headerProtocolloRisposta = validatore.getHeaderProtocollo();
					}
				}catch(Exception e){
					if(functionAsRouter==false){
						msgDiag.logErroreGenerico(e,"sbustatore.sbustamento("+idMessageResponse+")");
					}else{
						msgDiag.logErroreGenerico(e,"validator.getHeader("+idMessageResponse+")");
					}
					EsitoElaborazioneMessaggioTracciato esitoTraccia = EsitoElaborazioneMessaggioTracciato.getEsitoElaborazioneConErrore("Errore durante lo sbustamento della risposta: "+e.getMessage());
					tracciamento.registraRisposta(responseMessage,securityInfoResponse,headerBustaRisposta,bustaRisposta,esitoTraccia,
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
						if(sendRispostaApplicativa){
							OpenSPCoop2Message responseMessageError = 
								this.erroreApplicativoBuilder.toMessage(ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
										get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_527_GESTIONE_SBUSTAMENTO),e,
										(responseMessage!=null ? responseMessage.getParseException() : null));
							ejbUtils.sendRispostaApplicativaErrore(responseMessageError,richiestaDelegata,rollbackRichiesta,pd,sa);
							esito.setEsitoInvocazione(true);
							esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,"Sbustamento non riuscito");
						}else{
							// Se Non e' attivo una gestione dei riscontri, faccio il rollback sulla coda.
							// Altrimenti verra attivato la gestione dei riscontri che riprovera' dopo un tot.
							if(gestioneBusteNonRiscontrateAttive==false){
								ejbUtils.rollbackMessage("Sbustamento non riuscito", esito);
								esito.setEsitoInvocazione(false);
								esito.setStatoInvocazioneErroreNonGestito(e);
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
					sbustamentoRisposteMSG.setErrors(erroriValidazione);
					sbustamentoRisposteMSG.setMessaggioErroreProtocollo(isMessaggioErroreProtocollo);
					sbustamentoRisposteMSG.setIsBustaDiServizio(bustaDiServizio);
					sbustamentoRisposteMSG.setRuoloBustaRicevuta(validatore.getRuoloBustaRicevuta(true));
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
							//IGestoreIntegrazionePD gestore = InoltroBuste.gestoriIntegrazionePD.get(tipiIntegrazionePD_risposta[i]);
							
							String classType = null;
							IGestoreIntegrazionePD gestore = null;
							try{
								classType = ClassNameProperties.getInstance().getIntegrazionePortaDelegata(tipiIntegrazionePD_risposta[i]);
								gestore = (IGestoreIntegrazionePD) this.loader.newInstance(classType);
								AbstractCore.init(gestore, pddContext, protocolFactory);
							}catch(Exception e){
								throw new Exception("Riscontrato errore durante il caricamento della classe ["+classType+
										"] da utilizzare per la gestione dell'integrazione (Aggiornamento/Eliminazione) di tipo ["+tipiIntegrazionePD_risposta[i]+"]: "+e.getMessage());
							}
							
							if (gestore != null && gestore instanceof IGestoreIntegrazionePDSoap) {
								if(this.propertiesReader.deleteHeaderIntegrazioneResponsePD()){
									((IGestoreIntegrazionePDSoap)gestore).deleteInResponseHeader(inResponsePDMessage);
								}else{
									((IGestoreIntegrazionePDSoap)gestore).updateInResponseHeader(inResponsePDMessage, idMessageRequest, idMessageResponse, 
											servizioApplicativoFruitore, idCorrelazioneApplicativaRisposta, idCorrelazioneApplicativa);
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
									this.erroreApplicativoBuilder.toMessage(ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
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
							msgResponse.deleteMessageWithLock(msg,this.propertiesReader.getMsgGiaInProcessamento_AttesaAttiva(),
									this.propertiesReader.getMsgGiaInProcessamento_CheckInterval());
						}else{
							throw new Exception("Altra copia della Busta ricevuta come risposta con id["+idMessageResponse+"] in elaborazione dal modulo "+proprietarioMessaggio);
						}
					}

					// Aggiungo ErroreApplicativo come details se non sono router.
					if(functionAsRouter==false){
						if(fault!=null){
							if (!isMessaggioErroreProtocollo) {
								if(enrichSoapFaultApplicativo){
									this.erroreApplicativoBuilder.insertInSOAPFault(ErroriIntegrazione.ERRORE_516_CONNETTORE_UTILIZZO_CON_ERRORE.
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
						infoIntegrazione.setLocationPD(richiestaDelegata.getLocationPD());
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
									this.erroreApplicativoBuilder.toMessage(ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
											get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_510_SAVE_RESPONSE_MSG),
											null,
											(responseMessage!=null ? responseMessage.getParseException() : null));
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
			if( fault!=null && faultLogged==false ){
				msgDiag.addKeyword(CostantiPdD.KEY_SOAP_FAULT, SoapUtils.toString(fault));
				msgDiag.logPersonalizzato("ricezioneSoapFault");
				faultLogged = true;
			}






			/* ------- Gestione punto 1 (Carico HTTP Reply vuoto) -------- */
			IValidatoreErrori validatoreErrori = protocolFactory.createValidatoreErrori();
			if ( responseMessage == null ) {
				if(functionAsRouter){

					msgDiag.mediumDebug("Gestione punto 1 (Carico HTTP Reply vuoto) [router]...");
					
					// Scenario RoutingSincrono
					if( (org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.SINCRONO.equals(bustaRichiesta.getProfiloDiCollaborazione())) &&
							(!validatoreErrori.isBustaErrore(bustaRichiesta,requestMessage,pValidazioneErrori)) ){
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
						msgResponse = ejbUtils.sendRispostaApplicativaOK(SoapUtils.build_Soap_Empty(versioneSoap),richiestaDelegata,pd,sa);
					else
						msgResponse = ejbUtils.sendRispostaApplicativaOK(ejbUtils.buildOpenSPCoopOK_soapMsg(versioneSoap, idMessageRequest),richiestaDelegata,pd,sa);
				}
				// Scenario Sincrono
				else if(org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.SINCRONO.equals(bustaRichiesta.getProfiloDiCollaborazione())){
					
					msgDiag.mediumDebug("Gestione punto 1 (Carico HTTP Reply vuoto) [pdd]...");
					if(msgResponse!=null){
						msgResponse.deleteMessageFromFileSystem(); // elimino eventuale risposta salvata su fileSystem
					}

					msgDiag.logPersonalizzato("profiloSincrono.rispostaNonPervenuta");
					OpenSPCoop2Message responseMessageError = 
						this.erroreApplicativoBuilder.toMessage(ErroriIntegrazione.ERRORE_517_RISPOSTA_RICHIESTA_NON_RITORNATA.
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
							this.erroreApplicativoBuilder.toMessage(ErroriIntegrazione.ERRORE_517_RISPOSTA_RICHIESTA_NON_RITORNATA.
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
							msgResponse = ejbUtils.sendRispostaApplicativaOK(SoapUtils.build_Soap_Empty(versioneSoap),richiestaDelegata,pd,sa);
						else
							msgResponse = ejbUtils.sendRispostaApplicativaOK(ejbUtils.buildOpenSPCoopOK_soapMsg(versioneSoap, idMessageRequest),richiestaDelegata,pd,sa);
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
							(!validatoreErrori.isBustaErrore(bustaRichiesta,requestMessage,pValidazioneErrori)) ){
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
						StringBuffer bfDettagliEccezione = new StringBuffer();
						if(bustaRisposta.sizeListaEccezioni()>0)
							msgDiag.addKeyword(CostantiPdD.KEY_ECCEZIONI, bustaRisposta.toStringListaEccezioni(protocolFactory));
						else if(dettaglioEccezione!=null && dettaglioEccezione.getEccezioni()!=null){
							for(int i=0; i<dettaglioEccezione.getEccezioni().sizeEccezioneList(); i++){
								if(i>0)
									bfDettagliEccezione.append("\n");
								org.openspcoop2.core.eccezione.details.Eccezione eccDettaglio = dettaglioEccezione.getEccezioni().getEccezione(i);
								if(eccDettaglio.getRilevanza()!=null && eccDettaglio.getContestoCodifica()!=null){
									bfDettagliEccezione.append("Eccezione ("+eccDettaglio.getTipo()+") "+eccDettaglio.getRilevanza()+" con codice ["+eccDettaglio.getCodice()+"] - "
											+eccDettaglio.getContestoCodifica() +", descrizione errore: "+eccDettaglio.getDescrizione());
								}else{
									bfDettagliEccezione.append("Eccezione ("+eccDettaglio.getTipo()+") con codice ["+eccDettaglio.getCodice()+"], descrizione errore: "+eccDettaglio.getDescrizione());
								}
							}
							if(dettaglioEccezione.getDettagli()!=null){
								for(int i=0; i<dettaglioEccezione.getDettagli().sizeDettaglioList(); i++){
									bfDettagliEccezione.append("\n");
									org.openspcoop2.core.eccezione.details.Dettaglio eccDettaglio = dettaglioEccezione.getDettagli().getDettaglio(i);
									bfDettagliEccezione.append("Dettaglio ["+eccDettaglio.getTipo()+"]: "+eccDettaglio.getBase());
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
						try{ 
							validazioneContenutoApplicativoApplicativo = configurazionePdDManager.getTipoValidazioneContenutoApplicativo(pd,implementazionePdDDestinatario);
						}catch(Exception ex){
							msgDiag.logErroreGenerico(ex,"getTipoValidazioneContenutoApplicativo(pd)");
							if(msgResponse!=null){
								msgResponse.deleteMessageFromFileSystem(); // elimino eventuale risposta salvata su fileSystem
							}
							OpenSPCoop2Message responseMessageError = 
								this.erroreApplicativoBuilder.toMessage(ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
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

							ByteArrayInputStream binXSD = null;
							try{
								if(responseMessage.getSOAPBody()!=null && (responseMessage.getSOAPBody().hasFault()==false) ){
									
									msgDiag.logPersonalizzato("validazioneContenutiApplicativiRispostaInCorso");
									
									// Accept mtom message
									List<MtomXomReference> xomReferences = null;
									if(StatoFunzionalita.ABILITATO.equals(validazioneContenutoApplicativoApplicativo.getAcceptMtomMessage())){
										msgDiag.mediumDebug("Validazione xsd della risposta (mtomFastUnpackagingForXSDConformance)...");
										xomReferences = responseMessage.mtomFastUnpackagingForXSDConformance();
									}
									
									// Init Validatore
									boolean readWSDL = CostantiConfigurazione.VALIDAZIONE_CONTENUTI_APPLICATIVI_WSDL.equals(validazioneContenutoApplicativoApplicativo.getTipo());
									//System.out.println("MESSAGGIO PRIMA DI VALIDARE: "+requestMessage.getAsString(responseMessage.getSOAPPart().getEnvelope(), false));
									msgDiag.mediumDebug("Validazione xsd della risposta (initValidator)...");
									ValidatoreMessaggiApplicativi validatoreMessaggiApplicativi = 
										new ValidatoreMessaggiApplicativi(registroServiziManager,richiestaDelegata.getIdServizio(),
												responseMessage.getVersioneSoap(),responseMessage.getSOAPPart().getEnvelope(),readWSDL);

									// Validazione WSDL 
									if( CostantiConfigurazione.VALIDAZIONE_CONTENUTI_APPLICATIVI_WSDL.equals(validazioneContenutoApplicativoApplicativo.getTipo()) 
											||
											CostantiConfigurazione.VALIDAZIONE_CONTENUTI_APPLICATIVI_OPENSPCOOP.equals(validazioneContenutoApplicativoApplicativo.getTipo())
									){
										msgDiag.mediumDebug("Validazione wsdl della risposta ...");
										validatoreMessaggiApplicativi.validateWithWsdlLogicoImplementativo(false, null);
									}
									
									// Validazione XSD
									msgDiag.mediumDebug("Validazione xsd della risposta (validazione)...");
									validatoreMessaggiApplicativi.validateWithWsdlDefinitorio(false);
									
									// Ripristino struttura messaggio con xom
									if(xomReferences!=null && xomReferences.size()>0){
										msgDiag.mediumDebug("Validazione xsd della risposta (mtomRestoreAfterXSDConformance)...");
										responseMessage.mtomRestoreAfterXSDConformance(xomReferences);
									}
									
									msgDiag.logPersonalizzato("validazioneContenutiApplicativiRispostaEffettuata");
								}
								else{
									if(responseMessage.getSOAPBody()==null){
										msgDiag.addKeyword(CostantiPdD.KEY_DETAILS_VALIDAZIONE_CONTENUTI,CostantiConfigurazione.VALIDAZIONE_CONTENUTI_APPLICATIVI_VALIDAZIONE_SOAP_BODY_NON_PRESENTE);
										msgDiag.logPersonalizzato("validazioneContenutiApplicativiRispostaDisabilitata");
									}
									else if (responseMessage.getSOAPBody().hasFault() ){
										msgDiag.addKeyword(CostantiPdD.KEY_DETAILS_VALIDAZIONE_CONTENUTI,CostantiConfigurazione.VALIDAZIONE_CONTENUTI_APPLICATIVI_VALIDAZIONE_SOAP_FAULT_PRESENTE);
										msgDiag.logPersonalizzato("validazioneContenutiApplicativiRispostaDisabilitata");
									}
								}

							}catch(ValidatoreMessaggiApplicativiException ex){
								msgDiag.addKeyword(CostantiPdD.KEY_ERRORE_PROCESSAMENTO, ex.getMessage());
								msgDiag.logPersonalizzato("validazioneContenutiApplicativiRispostaNonRiuscita");
								this.log.error("[ValidazioneContenutiApplicativi Risposta] "+ex.getMessage(),ex);
								if(CostantiConfigurazione.STATO_CON_WARNING_WARNING_ONLY.equals(validazioneContenutoApplicativoApplicativo.getStato()) == false){
									// validazione abilitata
									if(msgResponse!=null){
										msgResponse.deleteMessageFromFileSystem(); // elimino eventuale risposta salvata su fileSystem
									}
									OpenSPCoop2Message responseMessageError = 
										this.erroreApplicativoBuilder.toMessage(ex.getErrore(),ex,
												(responseMessage!=null ? responseMessage.getParseException() : null));
									ejbUtils.sendRispostaApplicativaErrore(responseMessageError,richiestaDelegata,rollbackRichiesta,pd,sa);
									openspcoopstate.releaseResource();
									esito.setEsitoInvocazione(true);
									esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,
											msgDiag.getMessaggio_replaceKeywords("validazioneContenutiApplicativiNonRiuscita"));
									return esito;
								}
							}catch(Exception ex){
								msgDiag.addKeyword(CostantiPdD.KEY_ERRORE_PROCESSAMENTO, ex.getMessage());
								msgDiag.logPersonalizzato("validazioneContenutiApplicativiRispostaNonRiuscita");
								this.log.error("Riscontrato errore durante la validazione xsd della risposta applicativa",ex);
								if(CostantiConfigurazione.STATO_CON_WARNING_WARNING_ONLY.equals(validazioneContenutoApplicativoApplicativo.getStato()) == false){
									// validazione abilitata
									if(msgResponse!=null){
										msgResponse.deleteMessageFromFileSystem(); // elimino eventuale risposta salvata su fileSystem
									}
									OpenSPCoop2Message responseMessageError = 
										this.erroreApplicativoBuilder.toMessage(ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
												get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_531_VALIDAZIONE_WSDL_FALLITA),ex,
												(responseMessage!=null ? responseMessage.getParseException() : null));
									ejbUtils.sendRispostaApplicativaErrore(responseMessageError,richiestaDelegata,rollbackRichiesta,pd,sa);
									openspcoopstate.releaseResource();
									esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,
											msgDiag.getMessaggio_replaceKeywords("validazioneContenutiApplicativiNonRiuscita"));
									esito.setEsitoInvocazione(true);
									return esito;
								}
							}finally{
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
									this.erroreApplicativoBuilder.toMessage(ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
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

					if(fault!=null){
						if(enrichSoapFaultPdD){
							this.erroreApplicativoBuilder.insertRoutingErrorInSOAPFault(identitaPdD,InoltroBuste.ID_MODULO,
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
								(!validatoreErrori.isBustaErrore(bustaRichiesta,requestMessage,pValidazioneErrori)) ){
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
					if(fault==null){
						
						if(Costanti.SCENARIO_ONEWAY_INVOCAZIONE_SERVIZIO.equals(richiestaDelegata.getScenario())){
							// Altrimenti entro qua per via del sendRispostaApplicativa=true a caso del oneway stateless
							if(responseMessage.getSOAPBody()!=null && responseMessage.getSOAPBody().hasChildNodes()){
								msgDiag.logPersonalizzato("ricezioneSoapMessage.headerProtocolloNonPresente");
							}
							msgDiag.mediumDebug("Invio messaggio 'OK' al modulo di RicezioneContenutiApplicativi (Carico HTTP Reply vuoto)...");
							if(protocolManager.isHttpEmptyResponseOneWay())
								msgResponse = ejbUtils.sendRispostaApplicativaOK(SoapUtils.build_Soap_Empty(versioneSoap),richiestaDelegata,pd,sa);
							else
								msgResponse = ejbUtils.sendRispostaApplicativaOK(ejbUtils.buildOpenSPCoopOK_soapMsg(versioneSoap, idMessageRequest),richiestaDelegata,pd,sa);
							fineGestione = false;
						}else{
							// Se non ho un fault, e non sono in profilo oneway stateless segnalo l'anomalia
							msgDiag.logPersonalizzato("ricezioneSoapMessage.headerProtocolloNonPresente");
							OpenSPCoop2Message responseMessageError = this.erroreApplicativoBuilder.toMessage(ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
											get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_528_RISPOSTA_RICHIESTA_NON_VALIDA),
											null,
											(responseMessage!=null ? responseMessage.getParseException() : null));
							ejbUtils.sendRispostaApplicativaErrore(responseMessageError,richiestaDelegata,rollbackRichiesta,pd,sa);
						 }
					}
					else{
						if(enrichSoapFaultPdD){
							this.erroreApplicativoBuilder.insertInSOAPFault(ErroriIntegrazione.ERRORE_516_CONNETTORE_UTILIZZO_CON_ERRORE.
									get516_PortaDiDominioNonDisponibile(bustaRichiesta.getTipoDestinatario()+bustaRichiesta.getDestinatario()), 
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
					OpenSPCoop2Message responseMessageError = 
						this.erroreApplicativoBuilder.toMessage(ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.getErroreIntegrazione(),e,
								(responseMessage!=null ? responseMessage.getParseException() : null));
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
}
