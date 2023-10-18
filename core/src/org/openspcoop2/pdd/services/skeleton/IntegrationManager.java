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



package org.openspcoop2.pdd.services.skeleton;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.openspcoop2.core.commons.CoreException;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.core.constants.TipoPdD;
import org.openspcoop2.core.id.IDPortaApplicativa;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.driver.IDServizioFactory;
import org.openspcoop2.core.transazioni.TransazioneApplicativoServer;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.soap.TunnelSoapUtils;
import org.openspcoop2.pdd.config.ClassNameProperties;
import org.openspcoop2.pdd.config.ConfigurazionePdDManager;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.core.AbstractCore;
import org.openspcoop2.pdd.core.CostantiPdD;
import org.openspcoop2.pdd.core.GestoreMessaggi;
import org.openspcoop2.pdd.core.MessaggioServizioApplicativo;
import org.openspcoop2.pdd.core.PdDContext;
import org.openspcoop2.pdd.core.StatoServiziPdD;
import org.openspcoop2.pdd.core.autenticazione.GestoreAutenticazione;
import org.openspcoop2.pdd.core.autenticazione.pd.EsitoAutenticazionePortaDelegata;
import org.openspcoop2.pdd.core.connettori.InfoConnettoreIngresso;
import org.openspcoop2.pdd.core.credenziali.Credenziali;
import org.openspcoop2.pdd.core.credenziali.GestoreCredenzialiConfigurationException;
import org.openspcoop2.pdd.core.credenziali.IGestoreCredenzialiIM;
import org.openspcoop2.pdd.core.handlers.GestoreHandlers;
import org.openspcoop2.pdd.core.handlers.HandlerException;
import org.openspcoop2.pdd.core.handlers.IntegrationManagerRequestContext;
import org.openspcoop2.pdd.core.handlers.IntegrationManagerResponseContext;
import org.openspcoop2.pdd.core.state.OpenSPCoopStateDBManager;
import org.openspcoop2.pdd.core.state.OpenSPCoopStateful;
import org.openspcoop2.pdd.core.transazioni.GestoreConsegnaMultipla;
import org.openspcoop2.pdd.logger.Dump;
import org.openspcoop2.pdd.logger.MsgDiagnosticiProperties;
import org.openspcoop2.pdd.logger.MsgDiagnostico;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.pdd.logger.Tracciamento;
import org.openspcoop2.pdd.services.OpenSPCoop2Startup;
import org.openspcoop2.pdd.services.service.RicezioneContenutiApplicativiIntegrationManagerService;
import org.openspcoop2.pdd.timers.TimerMonitoraggioRisorseThread;
import org.openspcoop2.pdd.timers.TimerThresholdThread;
import org.openspcoop2.protocol.engine.LetturaParametriBusta;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.engine.URLProtocolContextImpl;
import org.openspcoop2.protocol.engine.constants.Costanti;
import org.openspcoop2.protocol.engine.driver.RepositoryBuste;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.ProtocolMessage;
import org.openspcoop2.protocol.sdk.builder.EsitoTransazione;
import org.openspcoop2.protocol.sdk.builder.IBustaBuilder;
import org.openspcoop2.protocol.sdk.builder.ProprietaManifestAttachments;
import org.openspcoop2.protocol.sdk.constants.CodiceErroreIntegrazione;
import org.openspcoop2.protocol.sdk.constants.ErroreIntegrazione;
import org.openspcoop2.protocol.sdk.constants.ErroriIntegrazione;
import org.openspcoop2.protocol.sdk.constants.EsitoTransazioneName;
import org.openspcoop2.protocol.sdk.constants.FaseSbustamento;
import org.openspcoop2.protocol.sdk.constants.IDService;
import org.openspcoop2.protocol.sdk.constants.IntegrationFunctionError;
import org.openspcoop2.protocol.sdk.constants.RuoloMessaggio;
import org.openspcoop2.protocol.sdk.state.URLProtocolContext;
import org.openspcoop2.protocol.utils.ErroriProperties;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.Semaphore;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.id.IUniqueIdentifier;
import org.openspcoop2.utils.id.UniqueIdentifierManager;
import org.openspcoop2.utils.resources.Loader;
import org.openspcoop2.utils.transport.TransportRequestContext;
import org.slf4j.Logger;

		
/**
 * IntegrationManager service
 *
 *
 * @author Lo Votrico Fabio (fabio@link.it)
 * @author Poli Andrea (apoli@link.it)
 * @author Tronci Fabio (tronci@link.it)
 * @author Nardi Lorenzo (nardi@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
		
public abstract class IntegrationManager implements IntegrationManagerMessageBoxInterface,IntegrationManagerPDInterface {

	public static final IDService ID_SERVICE = IDService.INTEGRATION_MANAGER_SOAP;
	public static final String ID_MODULO = ID_SERVICE.getValue();
	
	private static void logError(Logger logCore, String msg) {
		logCore.error(msg);
	}
	private static void logError(Logger logCore, String msg, Throwable e) {
		logCore.error(msg,e);
	}
	
	private OpenSPCoop2Properties propertiesReader;
	private ClassNameProperties className;

	private static final boolean useNewMethod = true; // compatta diverse query e usa le date

	private boolean inizializzato = false;

	/** Indicazione se il servizio PD risulta attivo */
	private static Semaphore semaphore = new Semaphore(ID_MODULO);
	private static Boolean staticInitialized = false;
	
	private void init(){
		if(!this.inizializzato){
			this.propertiesReader = OpenSPCoop2Properties.getInstance();
			this.className = ClassNameProperties.getInstance();
			this.inizializzato = true;
			semaphore.acquireThrowRuntime("init");
			try {
				if(!IntegrationManager.staticInitialized){
					IntegrationManager.staticInitialized = true;
				}
			}finally {
				semaphore.release("init");
			}
		}
	}


	protected abstract HttpServletRequest getHttpServletRequest() throws IntegrationManagerException;
	protected abstract HttpServletResponse getHttpServletResponse() throws IntegrationManagerException;

	private ErroriProperties getErroriProperties(Logger log) {
		ErroriProperties erroriProperties = null;
		try {
			erroriProperties = ErroriProperties.getInstance(log);
		}catch(Exception ignore) {
			// non succede
		}
		return erroriProperties;
	}
	
	private IProtocolFactory<?> getProtocolFactory(Logger log) throws IntegrationManagerException{
		try {
			String protocolName = (String) getHttpServletRequest().getAttribute(org.openspcoop2.core.constants.Costanti.PROTOCOL_NAME.getValue());
			return ProtocolFactoryManager.getInstance().getProtocolFactoryByName(protocolName);
		} catch (Exception e) {
			log.error("Errore durante il recupero della ProtocolFactory: "+e.getMessage(),e);
			throw new IntegrationManagerException(null,ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.getErroreIntegrazione(),
					IntegrationFunctionError.INTERNAL_REQUEST_ERROR, this.getErroriProperties(log));
		} 
	}
	
	private void checkIMAuthorization(Logger log) throws IntegrationManagerException{
		try {
			Object o = getHttpServletRequest().getAttribute(org.openspcoop2.core.constants.Costanti.INTEGRATION_MANAGER_ENGINE_AUTHORIZED.getValue());
			if(!(o instanceof Boolean)){
				throw new CoreException("Invocazione del Servizio non autorizzata");
			}
			Boolean b = (Boolean) o;
			if(!b.booleanValue()){
				throw new CoreException("Invocazione del Servizio non autorizzata");
			}
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			throw new IntegrationManagerException(null,ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.getErroreIntegrazione(),
					IntegrationFunctionError.AUTHORIZATION, this.getErroriProperties(log));
		} 
	}
	private void setNomePortaDelegata(Logger log,String nomePorta) throws IntegrationManagerException{
		try {
			getHttpServletRequest().setAttribute(org.openspcoop2.core.constants.Costanti.PORTA_DELEGATA.getValue(), nomePorta);
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			throw new IntegrationManagerException(null,ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.getErroreIntegrazione(),
					IntegrationFunctionError.INTERNAL_REQUEST_ERROR, this.getErroriProperties(log));
		} 
	}
	
	
	public static InfoConnettoreIngresso buildInfoConnettoreIngresso(jakarta.servlet.http.HttpServletRequest req,Credenziali credenziali,URLProtocolContext urlProtocolContext){
		// Informazioni connettore ingresso
		InfoConnettoreIngresso connettoreIngresso = new InfoConnettoreIngresso();
		connettoreIngresso.setCredenziali(credenziali);
		connettoreIngresso.setFromLocation(urlProtocolContext.getSource());
		connettoreIngresso.setUrlProtocolContext(urlProtocolContext);
		return connettoreIngresso;
	}
	
	private IntegrationManagerRequestContext buildIMRequestContext(Date dataRichiestaOperazione,
			Operazione tipoOperazione, PdDContext pddContext, Logger logCore,IProtocolFactory<?> protocolFactory) throws IntegrationManagerException, ProtocolException, UtilsException{
		
		IntegrationManagerRequestContext imRequestContext = 
			new IntegrationManagerRequestContext(dataRichiestaOperazione, tipoOperazione, pddContext, logCore,protocolFactory);
		
		// Raccolta oggetti da contesto
		Credenziali credenziali = null;
		jakarta.servlet.http.HttpServletRequest req = getHttpServletRequest();
		URLProtocolContext urlProtocolContext = new URLProtocolContextImpl(req,logCore,true,true,this.propertiesReader.getCustomContexts());
		try {
			credenziali = new Credenziali(urlProtocolContext.getCredential());
		}catch(Exception e){
			credenziali = new Credenziali();
		}
		imRequestContext.setConnettore(buildInfoConnettoreIngresso(req, credenziali, urlProtocolContext));
		
		return imRequestContext;
	}

	/** IGestoreCredenziali: lista di gestori delle credenziali */
	// E' stato aggiunto lo stato dentro l'oggetto.
	private String [] tipiGestoriCredenziali = null;
	private synchronized void initializeGestoreCredenziali(Logger logCore,IProtocolFactory<?> protocolFactory,MsgDiagnostico msgDiag) throws IntegrationManagerException{
		if(this.tipiGestoriCredenziali==null){
			
			Loader loader = Loader.getInstance();
			
			this.tipiGestoriCredenziali = this.propertiesReader.getTipoGestoreCredenzialiIM();
			if(this.tipiGestoriCredenziali!=null){
				for (int i = 0; i < this.tipiGestoriCredenziali.length; i++) {
					String classType = this.className.getGestoreCredenzialiIM(this.tipiGestoriCredenziali[i]);
					try {
						IGestoreCredenzialiIM gestore = (IGestoreCredenzialiIM)loader.newInstance(classType);
						gestore.toString();
					} catch (Exception e) {
						msgDiag.logErroreGenerico(e, "InizializzazioneGestoreCredenziali("+this.tipiGestoriCredenziali[i]+")");
						throw new IntegrationManagerException(protocolFactory,
								ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
									get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_548_GESTORE_CREDENZIALI_NON_FUNZIONANTE),
									IntegrationFunctionError.INTERNAL_REQUEST_ERROR, this.getErroriProperties(logCore));
					}
				}
			}
		}
	}
	private void gestioneCredenziali(Logger logCore,IProtocolFactory<?> protocolFactory,MsgDiagnostico msgDiag,
			InfoConnettoreIngresso infoConnettoreIngresso,PdDContext pddContext) throws IntegrationManagerException {
		if(this.tipiGestoriCredenziali==null){
			initializeGestoreCredenziali(logCore,protocolFactory,msgDiag);
		}
		if(this.tipiGestoriCredenziali!=null){
			msgDiag.mediumDebug("Gestione personalizzata delle credenziali...");
			
			Loader loader = Loader.getInstance();
			
			for (int i = 0; i < this.tipiGestoriCredenziali.length; i++) {
				try {
									
					String classType = null;
					IGestoreCredenzialiIM gestore = null;
					try {
						classType = this.className.getGestoreCredenzialiIM(this.tipiGestoriCredenziali[i]);
						gestore = (IGestoreCredenzialiIM)loader.newInstance(classType);
						AbstractCore.init(gestore, pddContext, protocolFactory);
					} catch (Exception e) {
						msgDiag.logErroreGenerico(e, "InizializzazioneGestoreCredenziali("+this.tipiGestoriCredenziali[i]+")");
						throw new IntegrationManagerException(protocolFactory,
								ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
									get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_548_GESTORE_CREDENZIALI_NON_FUNZIONANTE),
									IntegrationFunctionError.INTERNAL_REQUEST_ERROR, this.getErroriProperties(logCore));
					}
					
					if (gestore != null) {
						Credenziali credenzialiRitornate = gestore.elaborazioneCredenziali(infoConnettoreIngresso);
						if(credenzialiRitornate==null){
							throw new CoreException("Credenziali non ritornate");
						}
						if(infoConnettoreIngresso.getCredenziali().equals(credenzialiRitornate) == false){
							String nuoveCredenziali = credenzialiRitornate.toString();
							nuoveCredenziali = nuoveCredenziali.substring(0,(nuoveCredenziali.length()-1));
							msgDiag.addKeyword(CostantiPdD.KEY_NUOVE_CREDENZIALI,nuoveCredenziali);
							String identita = gestore.getIdentitaGestoreCredenziali();
							if(identita==null){
								identita = "Gestore delle credenziali di tipo "+this.tipiGestoriCredenziali[i];
							}
							msgDiag.addKeyword(CostantiPdD.KEY_IDENTITA_GESTORE_CREDENZIALI, identita);
							pddContext.addObject(org.openspcoop2.core.constants.Costanti.IDENTITA_GESTORE_CREDENZIALI, identita);
							msgDiag.logPersonalizzato("gestoreCredenziali.nuoveCredenziali");
							// update credenziali
							infoConnettoreIngresso.setCredenziali(credenzialiRitornate);	
						}
					} else {
						throw new CoreException("non inizializzato");
					}
				} 
				catch (Exception e) {
					OpenSPCoop2Logger.getLoggerOpenSPCoopCore().error("Errore durante l'identificazione delle credenziali ["+ this.tipiGestoriCredenziali[i]
					         + "]: "+ e.getMessage(),e);
					msgDiag.addKeyword(CostantiPdD.KEY_TIPO_GESTORE_CREDENZIALI,this.tipiGestoriCredenziali[i]);
					msgDiag.addKeyword(CostantiPdD.KEY_ERRORE_PROCESSAMENTO, e.getMessage());
					msgDiag.logPersonalizzato("gestoreCredenziali.errore");
					ErroreIntegrazione errore = null;
					IntegrationFunctionError integrationFunctionError = null;
					if(e instanceof GestoreCredenzialiConfigurationException){
						GestoreCredenzialiConfigurationException ge = (GestoreCredenzialiConfigurationException) e;
						integrationFunctionError = ge.getIntegrationFunctionError();
						errore = ErroriIntegrazione.ERRORE_431_GESTORE_CREDENZIALI_ERROR.
								getErrore431_ErroreGestoreCredenziali(this.tipiGestoriCredenziali[i], e);
					}else{
						errore = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
								get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_548_GESTORE_CREDENZIALI_NON_FUNZIONANTE);
					}
					if(integrationFunctionError==null) {
						integrationFunctionError = IntegrationFunctionError.INTERNAL_REQUEST_ERROR;
					}
					
					throw new IntegrationManagerException(protocolFactory,errore,
							integrationFunctionError, this.getErroriProperties(logCore));
					
				}
			}
		}
	}

	/**
	 * Effettua il processo di autenticazione
	 *
	 * @return il nome del Servizio Applicativo invocante
	 * 
	 */
	private IDServizioApplicativo autenticazione(Logger logCore, IProtocolFactory<?> protocolFactory,MsgDiagnostico msgDiag,InfoConnettoreIngresso infoConnettoreIngresso,
			ConfigurazionePdDManager configPdDManager,PdDContext pddContext) throws IntegrationManagerException {
		
		Credenziali credenziali = infoConnettoreIngresso.getCredenziali();
		
		// Autenticazione
		String credenzialiFornite = "";
		if(credenziali!=null){
			credenzialiFornite = credenziali.toString();
		}		
		msgDiag.addKeyword(CostantiPdD.KEY_CREDENZIALI_SA_FRUITORE, credenzialiFornite);

		IDServizioApplicativo servizioApplicativo = null;	    
		String [] tipoAutenticazione = configPdDManager.getIntegrationManagerAuthentication();
		if(tipoAutenticazione==null || tipoAutenticazione.length<1){
			msgDiag.logPersonalizzato("autenticazioneNonImpostata");
			throw new IntegrationManagerException(protocolFactory,ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
					get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_519_INTEGRATION_MANAGER_CONFIGURATION_ERROR),
					IntegrationFunctionError.INTERNAL_REQUEST_ERROR, this.getErroriProperties(logCore));
		}
		for(int i=0; i<tipoAutenticazione.length; i++){
			if(CostantiConfigurazione.INVOCAZIONE_SERVIZIO_AUTENTICAZIONE_NONE.toString().equalsIgnoreCase(tipoAutenticazione[i]) == true){
				msgDiag.logPersonalizzato("autenticazioneNonImpostata");
				throw new IntegrationManagerException(protocolFactory,ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
						get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_519_INTEGRATION_MANAGER_CONFIGURATION_ERROR),
						IntegrationFunctionError.INTERNAL_REQUEST_ERROR, this.getErroriProperties(logCore));
			}
		}
		
		StringBuilder errori = new StringBuilder();
		for(int i=0; i<tipoAutenticazione.length; i++){
			
			org.openspcoop2.pdd.core.autenticazione.pd.DatiInvocazionePortaDelegata datiInvocazione = new org.openspcoop2.pdd.core.autenticazione.pd.DatiInvocazionePortaDelegata();
			datiInvocazione.setInfoConnettoreIngresso(infoConnettoreIngresso);
			
			EsitoAutenticazionePortaDelegata esito = null;
			try{
				esito = GestoreAutenticazione.verificaAutenticazioneMessageBox(tipoAutenticazione[i], 
						datiInvocazione, null, 
						pddContext, protocolFactory); 
			}catch(Exception e){
				msgDiag.logErroreGenerico(e,"Autenticazione("+tipoAutenticazione[i]+")");
				throw new IntegrationManagerException(protocolFactory,ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
						get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_503_AUTENTICAZIONE),
						IntegrationFunctionError.INTERNAL_REQUEST_ERROR, this.getErroriProperties(logCore));
			}	
			if(esito.getDetails()==null){
				msgDiag.addKeyword(CostantiPdD.KEY_DETAILS, "");
			}else{
				msgDiag.addKeyword(CostantiPdD.KEY_DETAILS, " ("+esito.getDetails()+")");
			}
			if (esito.isClientAuthenticated()==false || esito.isClientIdentified() == false) {
				if(errori.length()>0 || tipoAutenticazione.length>1)
					errori.append("\n");
				try{
					if(esito.getErroreIntegrazione()!=null) {
						errori.append("(Autenticazione " +tipoAutenticazione[i]+") "+ esito.getErroreIntegrazione().getDescrizione(protocolFactory));
					}
					else {
						// l'errore puo' non esserci se l'identificazione non e' obbligatoria nella modalita' scelta.
						errori.append("(Autenticazione " +tipoAutenticazione[i]+") non ha identificato alcun servizio applicativo");
					}
				}catch(Exception e){
					OpenSPCoop2Logger.getLoggerOpenSPCoopCore().error("Errore durante l'identificazione dell'errore: "+e.getMessage(),e);
					throw new IntegrationManagerException(protocolFactory,ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
							get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_519_INTEGRATION_MANAGER_CONFIGURATION_ERROR),
							IntegrationFunctionError.INTERNAL_REQUEST_ERROR, this.getErroriProperties(logCore));
				}
			}
			else{
				servizioApplicativo = esito.getIdServizioApplicativo();
				break; //sa individuato
			}
		}
		
		if(servizioApplicativo == null){
			if(errori.length()>0){
				msgDiag.addKeyword(CostantiPdD.KEY_ERRORE_PROCESSAMENTO, errori.toString());
				msgDiag.logPersonalizzato("servizioApplicativo.identificazioneTramiteCredenziali");
				throw new IntegrationManagerException(protocolFactory,ErroriIntegrazione.ERRORE_402_AUTENTICAZIONE_FALLITA.
						getErrore402_AutenticazioneFallita(errori.toString()),
						IntegrationFunctionError.AUTHENTICATION, this.getErroriProperties(logCore));
			}
			else{
				msgDiag.addKeyword(CostantiPdD.KEY_ERRORE_PROCESSAMENTO, "servizio applicativo non autenticato");
				msgDiag.logPersonalizzato("servizioApplicativo.identificazioneTramiteCredenziali");
				throw new IntegrationManagerException(protocolFactory,ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
						get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_503_AUTENTICAZIONE),
						IntegrationFunctionError.INTERNAL_REQUEST_ERROR, this.getErroriProperties(logCore));
			}
		}
		
		msgDiag.addKeyword(CostantiPdD.KEY_SA_FRUITORE, servizioApplicativo.getNome());
		msgDiag.addKeyword(CostantiPdD.KEY_SA_EROGATORE, servizioApplicativo.getNome());
		msgDiag.setServizioApplicativo(servizioApplicativo.getNome());
		if(servizioApplicativo.getIdSoggettoProprietario()!=null){
			msgDiag.addKeyword(CostantiPdD.KEY_TIPO_MITTENTE_BUSTA_RICHIESTA, servizioApplicativo.getIdSoggettoProprietario().getTipo());
			msgDiag.addKeyword(CostantiPdD.KEY_MITTENTE_BUSTA_RICHIESTA, servizioApplicativo.getIdSoggettoProprietario().getNome());
			msgDiag.setFruitore(servizioApplicativo.getIdSoggettoProprietario());
		}
		return  servizioApplicativo;
	}


	/* ----------- Init -------------- */
	private void verificaRisorseSistema(IProtocolFactory<?> protocolFactory,Logger logCore,String tipoOperazione) throws IntegrationManagerException {
		
		if( !OpenSPCoop2Startup.initialize){
			logError(logCore, "["+IntegrationManager.ID_MODULO+"]["+tipoOperazione+"] Inizializzazione di GovWay non correttamente effettuata");
			throw new IntegrationManagerException(protocolFactory,ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
					get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_501_PDD_NON_INIZIALIZZATA),
					IntegrationFunctionError.GOVWAY_NOT_INITIALIZED, this.getErroriProperties(logCore));
		}
		if( !TimerMonitoraggioRisorseThread.isRisorseDisponibili()){
			logError(logCore, "["+IntegrationManager.ID_MODULO+"]["+tipoOperazione+"] Risorse di sistema non disponibili: "+TimerMonitoraggioRisorseThread.getRisorsaNonDisponibile().getMessage(),TimerMonitoraggioRisorseThread.getRisorsaNonDisponibile());
			throw new IntegrationManagerException(protocolFactory,ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
					get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_532_RISORSE_NON_DISPONIBILI),
					IntegrationFunctionError.GOVWAY_RESOURCES_NOT_AVAILABLE, this.getErroriProperties(logCore));
		}
		if (!TimerThresholdThread.freeSpace) {
			logError(logCore, "["+IntegrationManager.ID_MODULO+"]["+tipoOperazione+"] Non sono disponibili abbastanza risorse per la gestione della richiesta");
			throw new IntegrationManagerException(protocolFactory,ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
					get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_533_RISORSE_DISPONIBILI_LIVELLO_CRITICO),
					IntegrationFunctionError.GOVWAY_RESOURCES_NOT_AVAILABLE, this.getErroriProperties(logCore));
		}
		if( !Tracciamento.tracciamentoDisponibile){
			logError(logCore, "["+IntegrationManager.ID_MODULO+"]["+tipoOperazione+"] Tracciatura non disponibile: "+Tracciamento.motivoMalfunzionamentoTracciamento.getMessage(),Tracciamento.motivoMalfunzionamentoTracciamento);
			throw new IntegrationManagerException(protocolFactory,ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
					get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_545_TRACCIATURA_NON_FUNZIONANTE),
					IntegrationFunctionError.GOVWAY_RESOURCES_NOT_AVAILABLE, this.getErroriProperties(logCore));
		}
		if( !MsgDiagnostico.gestoreDiagnosticaDisponibile){
			logError(logCore, "["+IntegrationManager.ID_MODULO+"]["+tipoOperazione+"] Sistema di diagnostica non disponibile: "+MsgDiagnostico.motivoMalfunzionamentoDiagnostici.getMessage(),MsgDiagnostico.motivoMalfunzionamentoDiagnostici);
			throw new IntegrationManagerException(protocolFactory,ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
					get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_546_DIAGNOSTICA_NON_FUNZIONANTE),
					IntegrationFunctionError.GOVWAY_RESOURCES_NOT_AVAILABLE, this.getErroriProperties(logCore));
		}
		if( !Dump.isSistemaDumpDisponibile()){
			logError(logCore, "["+IntegrationManager.ID_MODULO+"]["+tipoOperazione+"] Sistema di dump dei contenuti applicativi non disponibile: "+Dump.getMotivoMalfunzionamentoDump().getMessage(),Dump.getMotivoMalfunzionamentoDump());
			throw new IntegrationManagerException(protocolFactory,ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
					get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_547_DUMP_CONTENUTI_APPLICATIVI_NON_FUNZIONANTE),
					IntegrationFunctionError.GOVWAY_RESOURCES_NOT_AVAILABLE, this.getErroriProperties(logCore));
		}
		// Check Configurazione (XML)
		try{
			ConfigurazionePdDManager.getInstance().verificaConsistenzaConfigurazione();
		}catch(Exception e){
			logError(logCore, "["+IntegrationManager.ID_MODULO+"]["+tipoOperazione+"] Riscontrato errore durante la verifica della consistenza della configurazione PdD",e);
			throw new IntegrationManagerException(protocolFactory,ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
					get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_536_CONFIGURAZIONE_NON_DISPONIBILE),
					IntegrationFunctionError.GOVWAY_RESOURCES_NOT_AVAILABLE, this.getErroriProperties(logCore));
		}
		
		boolean serviceIsEnabled = false;
		Exception serviceIsEnabledExceptionProcessamento = null;
		try{
			serviceIsEnabled = StatoServiziPdD.isEnabledIntegrationManager();
		}catch(Exception e){
			serviceIsEnabledExceptionProcessamento = e;
		}
		if (!serviceIsEnabled || serviceIsEnabledExceptionProcessamento!=null) {
			if(serviceIsEnabledExceptionProcessamento!=null){
				logError(logCore, "["+ IntegrationManager.ID_MODULO+ "]["+tipoOperazione+"] Identificazione stato servizio IntegrationManager non riuscita: "+serviceIsEnabledExceptionProcessamento.getMessage(),serviceIsEnabledExceptionProcessamento);
			}else{
				logError(logCore, "["+ IntegrationManager.ID_MODULO+ "]["+tipoOperazione+"] Servizio IntegrationManager disabilitato");
			}
			throw new IntegrationManagerException(protocolFactory,ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
					get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_552_IM_SERVICE_NOT_ACTIVE),
					IntegrationFunctionError.API_SUSPEND, this.getErroriProperties(logCore));
		}

	}
	
	/* -------- Utility -------------- */
	private IUniqueIdentifier getUniqueIdentifier(Logger logCore, IProtocolFactory<?> protocolFactory,MsgDiagnostico msgDiag,String tipoOperazione) throws IntegrationManagerException {
		try{
			return UniqueIdentifierManager.newUniqueIdentifier();
		}catch(Exception e){
			msgDiag.logErroreGenerico(e,"("+tipoOperazione+").getUniqueIdentifier()");
			throw new IntegrationManagerException(protocolFactory,ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.getErroreIntegrazione(),
					IntegrationFunctionError.INTERNAL_REQUEST_ERROR, this.getErroriProperties(logCore));
		}
	}
	private MsgDiagnostico getMsgDiagnostico(){
		MsgDiagnostico msgDiag = MsgDiagnostico.newInstance(IntegrationManager.ID_MODULO);
		msgDiag.setPrefixMsgPersonalizzati(MsgDiagnosticiProperties.MSG_DIAG_INTEGRATION_MANAGER);
		return msgDiag;
	}
	
	private EsitoTransazione getEsitoTransazione(IProtocolFactory<?> pf,IntegrationManagerRequestContext imRequestContext, EsitoTransazioneName name) throws ProtocolException{
		TransportRequestContext t = null;
		if(imRequestContext!=null && imRequestContext.getConnettore()!=null){
			t = imRequestContext.getConnettore().getUrlProtocolContext();
		}
		return pf.createEsitoBuilder().getEsito(t,name);
	}
	


	/*-------- getAllMessagesID ----*/

	/**
	 * Restituisce gli id di tutti i messaggi in base al servizio_applicativo
	 * Al servizio e all'azione se presenti
	 *
	 * @param tipoServizio Filtro per Servizio
	 * @param servizio Filtro per Servizio
	 * @param azione Filtro per Azione
	 * @param counter Indica il numero di id da ritornare
	 * @return una Collezione di ID di risposte applicative destinate al servizio applicativo che ha invocato il servizio
	 * 
	 */
	private List<String> getAllMessagesId_engine(Operazione tipoOperazione,String tipoServizio,String servizio, Integer versioneServizio,
			String azione,int counter, int offset) throws IntegrationManagerException {

		// Timestamp
		Date dataRichiestaOperazione = DateManager.getTimestamp();
		
		// Logger
		Logger logCore = OpenSPCoop2Logger.getLoggerOpenSPCoopCore();
		if(logCore==null)
			logCore = LoggerWrapperFactory.getLogger(IntegrationManager.ID_MODULO);
		
		// check Autorizzazione
		checkIMAuthorization(logCore);
		
		// ProtocolFactoyr
		IProtocolFactory<?> protocolFactory = getProtocolFactory(logCore);
		
		// Verifica risorse sistema
		this.verificaRisorseSistema(protocolFactory,logCore, tipoOperazione.toString());

		MsgDiagnostico msgDiag = getMsgDiagnostico();
		msgDiag.addKeyword(CostantiPdD.KEY_TIPO_OPERAZIONE_IM, tipoOperazione.toString());
		
		IDServizioApplicativo idServizioApplicativo = null;
		OpenSPCoopStateful stato = null;
		
		// PddContext
		PdDContext pddContext = new PdDContext();
		String idTransazione = this.getUniqueIdentifier(logCore, protocolFactory,msgDiag,tipoOperazione.toString()).getAsString();
		pddContext.addObject(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE, idTransazione);
		pddContext.addObject(org.openspcoop2.core.constants.Costanti.PROTOCOL_NAME, protocolFactory.getProtocol());
		
		IDServizio idServizio = null;
		try {
			int ver = -1;
			if(versioneServizio!=null) {
				ver = versioneServizio;
			}
			idServizio = IDServizioFactory.getInstance().getIDServizioFromValuesWithoutCheck(tipoServizio,servizio, 
				OpenSPCoop2Properties.getInstance().getIdentitaPortaDefault(protocolFactory.getProtocol(), null).getTipo(), 
				OpenSPCoop2Properties.getInstance().getIdentitaPortaDefault(protocolFactory.getProtocol(), null).getNome(), 
				ver);
		} catch(Exception e) {
			msgDiag.logErroreGenerico(e, "IDServizioFactory.getIDServizioFromValues");
			ErroreIntegrazione erroreIntegrazione = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.getErroreIntegrazione();
			throw new IntegrationManagerException(protocolFactory,erroreIntegrazione,
					IntegrationFunctionError.INTERNAL_REQUEST_ERROR, this.getErroriProperties(logCore));
		}
		idServizio.setAzione(azione);
		pddContext.addObject(org.openspcoop2.core.constants.Costanti.ID_SERVIZIO,idServizio);
		msgDiag.setPddContext(pddContext, protocolFactory);
		
		
		
		/* ------------  IntegrationManagerRequestHandler ------------- */
		IntegrationManagerRequestContext imRequestContext = null;
		try {
			imRequestContext = buildIMRequestContext(dataRichiestaOperazione, tipoOperazione, pddContext,logCore,protocolFactory);
			GestoreHandlers.integrationManagerRequest(imRequestContext, msgDiag, logCore);
		} catch(Exception e) {
			ErroreIntegrazione erroreIntegrazione = null;
			IntegrationFunctionError integrationFunctionError = null;
			if(e instanceof HandlerException){
				HandlerException he = (HandlerException) e;
				integrationFunctionError = he.getIntegrationFunctionError();
				if(he.isEmettiDiagnostico()){
					msgDiag.logErroreGenerico(e, ((HandlerException)e).getIdentitaHandler());
				}
				erroreIntegrazione = he.convertToErroreIntegrazione();
			} else {
				msgDiag.logErroreGenerico(e, "IntegrationManagerRequestHandler");
			} 
			if(erroreIntegrazione==null){
				erroreIntegrazione = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.getErroreIntegrazione();
			}
			if(integrationFunctionError==null) {
				integrationFunctionError = IntegrationFunctionError.INTERNAL_REQUEST_ERROR;
			}
			throw new IntegrationManagerException(protocolFactory,erroreIntegrazione,
					integrationFunctionError, this.getErroriProperties(logCore));
		}

		
		
		/* ------------  Gestione Operazione ------------- */
		IntegrationManagerResponseContext imResponseContext = 
			new IntegrationManagerResponseContext(dataRichiestaOperazione,tipoOperazione,pddContext,logCore,protocolFactory);
		if(imRequestContext!=null){
			imResponseContext.setConnettore(imRequestContext.getConnettore());
		}
		
		try{
			
			// init stato
			stato = new OpenSPCoopStateful();
			stato.initResource(this.propertiesReader.getIdentitaPortaDefault(protocolFactory.getProtocol(), null),IntegrationManager.ID_MODULO, idTransazione, 
					OpenSPCoopStateDBManager.messageBox);
			ConfigurazionePdDManager configPdDManager = ConfigurazionePdDManager.getInstance(stato.getStatoRichiesta(),stato.getStatoRisposta());
			msgDiag.updateState(configPdDManager);
			
			// gestione credenziali
			this.gestioneCredenziali(logCore, protocolFactory,msgDiag, imRequestContext.getConnettore(),pddContext);
						
			// Autenticazione Servizio Applicativo
			idServizioApplicativo = autenticazione(logCore, protocolFactory,msgDiag,imRequestContext.getConnettore(),configPdDManager,pddContext);
			imResponseContext.setServizioApplicativo(idServizioApplicativo);
			String tipoServizioLog = "";
			String servizioLog = "";
			String azioneLog = "";
			String counterLog = "";
			if(tipoServizio!=null)
				tipoServizioLog = " tipoServizio["+tipoServizio+"]";
			if(servizio!=null)
				servizioLog = " servizio["+servizio+"]";
			if(azione!=null)
				azioneLog = " azione["+azione+"]";
			if(counter>=0)
				counterLog = " counter["+counter+"]";
			String param = "ServizioApplicativo["+idServizioApplicativo.getNome()+"]"+tipoServizioLog+servizioLog+azioneLog+counterLog;
			msgDiag.addKeyword(CostantiPdD.KEY_PARAMETRI_OPERAZIONE_IM, param);
			msgDiag.logPersonalizzato("logInvocazioneOperazione");

			// Ricerca Messaggi
			List<IdentificativoIM> ids = null;

			GestoreMessaggi gestoreMessaggi = new GestoreMessaggi(stato, true, msgDiag,pddContext);

			if(counter<0 && offset<0){
				ids = gestoreMessaggi.getIDMessaggi_ServizioApplicativo(idServizioApplicativo.getNome(),tipoServizio,servizio,azione);
			}else if(offset<0){
				ids = gestoreMessaggi.getIDMessaggi_ServizioApplicativo(idServizioApplicativo.getNome(),tipoServizio,servizio,azione,counter);
			}else{
				ids = gestoreMessaggi.getIDMessaggi_ServizioApplicativo(idServizioApplicativo.getNome(),tipoServizio,servizio,azione,counter,offset);
			}
			if(ids==null || ids.isEmpty()){
				msgDiag.logPersonalizzato("messaggiNonPresenti");
				throw new IntegrationManagerException(protocolFactory,ErroriIntegrazione.ERRORE_406_INTEGRATION_MANAGER_MESSAGGI_FOR_SIL_NON_TROVATI.
						getErroreIntegrazione(),idServizioApplicativo,
						IntegrationFunctionError.IM_MESSAGES_NOT_FOUND, this.getErroriProperties(logCore));
			}
			List<String> listResponse = new ArrayList<>();
			if(ids!=null && !ids.isEmpty()){
				for (IdentificativoIM id : ids) {
					if(this.propertiesReader.isIntegrationManagerIdWithDate()) {
						listResponse.add(id.getIdWithDate());
					}
					else {
						listResponse.add(id.getId());
					}	
				}
			}

			imResponseContext.setEsito(this.getEsitoTransazione(protocolFactory, imRequestContext, EsitoTransazioneName.OK));
			
			return listResponse;

		}catch(Exception e){
			
			try{
				imResponseContext.setEsito(this.getEsitoTransazione(protocolFactory, imRequestContext, EsitoTransazioneName.ERRORE_PROCESSAMENTO_PDD_5XX));
	
				if(e instanceof IntegrationManagerException){
					IntegrationManagerException exc = (IntegrationManagerException) e;
					if(CodiceErroreIntegrazione.CODICE_402_AUTENTICAZIONE_FALLITA.equals(exc.getCodiceErroreIntegrazione()) ||
							CodiceErroreIntegrazione.CODICE_431_GESTORE_CREDENZIALI_ERROR.equals(exc.getCodiceErroreIntegrazione())){
						imResponseContext.setEsito(this.getEsitoTransazione(protocolFactory, imRequestContext, EsitoTransazioneName.AUTENTICAZIONE_FALLITA));
					}else if(CodiceErroreIntegrazione.CODICE_406_INTEGRATION_MANAGER_MESSAGGI_FOR_SIL_NON_TROVATI.equals(exc.getCodiceErroreIntegrazione())){
						imResponseContext.setEsito(this.getEsitoTransazione(protocolFactory, imRequestContext, EsitoTransazioneName.MESSAGGI_NON_PRESENTI));
					}
				}
			}catch(Exception eInteral){
				logError(logCore, "Errore durante la generazione dell'esito: "+eInteral.getMessage(),eInteral);
				imResponseContext.setEsito(EsitoTransazione.ESITO_TRANSAZIONE_ERROR);
			}
			
			// Controllo Eccezioni
			if(e instanceof IntegrationManagerException){
				// Lanciata all'interno del metodo
				throw (IntegrationManagerException)e;
			}else{
				msgDiag.logErroreGenerico(e,"getAllMessagesId("+tipoOperazione+")");
				throw new IntegrationManagerException(protocolFactory,ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.getErroreIntegrazione(),idServizioApplicativo,
						IntegrationFunctionError.INTERNAL_REQUEST_ERROR, this.getErroriProperties(logCore));
			}
		}finally{
			try{
				if(stato!=null)
					stato.releaseResource();
			}catch(Exception eClose){}
			
			
			/* ------------  IntegrationManagerResponseHandler ------------- */
			imResponseContext.setDataCompletamentoOperazione(DateManager.getDate());
			try{
				GestoreHandlers.integrationManagerResponse(imResponseContext, msgDiag, logCore);
			}catch(Exception e){
				if(e instanceof HandlerException){
					HandlerException he = (HandlerException) e;
					if(he.isEmettiDiagnostico()){
						msgDiag.logErroreGenerico(e, ((HandlerException)e).getIdentitaHandler());
					}
				}else{
					msgDiag.logErroreGenerico(e, "IntegrationManagerResponseHandler");
				}
			}
		}
	}
	/**
	 * Restituisce gli id di tutti i messaggi in base al servizio_applicativo
	 *
	 * @return una Collezione di ID di risposte applicative destinate al servizio applicativo che ha invocato il servizio
	 * 
	 */
	@Override
	public List<String> getAllMessagesId() throws IntegrationManagerException {
		init();
		return getAllMessagesId_engine(Operazione.getAllMessagesId,null,null,null,null,-1,-1);
	}


	/**
	 * Restituisce gli id di tutti i messaggi in base a servizio_applicativo, servizio
	 *
	 * @param tipoServizio Filtro per Servizio
	 * @param servizio Filtro per Servizio
	 * @param azione Filtro per Azione
	 * @return una Collezione di ID di risposte applicative destinate al servizio applicativo che ha invocato il servizio
	 * 
	 */
	@Override
	public List<String> getAllMessagesIdByService(String tipoServizio, String servizio, String azione) throws IntegrationManagerException {
		init();
		return getAllMessagesId_engine(Operazione.getAllMessagesIdByService,tipoServizio,servizio,null,azione,-1,-1);
	}


	/**
	 * Restituisce gli id dei primi <var>counter</var> i messaggi, in base al servizio_applicativo,
	 *
	 * @param counter Indica il numero di id da ritornare
	 * @return una Collezione di ID di risposte applicative destinate al servizio applicativo che ha invocato il servizio
	 * 
	 */
	@Override
	@Deprecated
	public List<String> getNextMessagesId(int counter) throws IntegrationManagerException {
		init();
		return getAllMessagesId_engine(Operazione.getNextMessagesId,null,null,null,null,counter,-1);	
	}

	/**
	 *  Restituisce gli id dei primi <var>counter</var> i messaggi, in base a servizio_applicativo, servizio
	 *
	 * @param counter Indica il numero di id da ritornare
	 * @param tipoServizio Filtro per Servizio
	 * @param servizio Filtro per Servizio
	 * @param azione Filtro per Azione
	 * @return una Collezione di ID di risposte applicative destinate al servizio applicativo che ha invocato il servizio
	 * 
	 */
	@Override
	@Deprecated
	public List<String> getNextMessagesIdByService(int counter,String tipoServizio,String servizio, String azione) throws IntegrationManagerException {
		init();
		return getAllMessagesId_engine(Operazione.getNextMessagesIdByService,tipoServizio,servizio,null,azione,counter,-1);	
	}
	
	/**
	 * Restituisce gli id dei primi <var>counter</var> i messaggi, in base al servizio_applicativo,
	 *
	 * @param counter Indica il numero di id da ritornare
	 * @return una Collezione di ID di risposte applicative destinate al servizio applicativo che ha invocato il servizio
	 * 
	 */
	@Override
	public List<String> getMessagesIdArray(int offset,int counter) throws IntegrationManagerException {
		init();
		return getAllMessagesId_engine(Operazione.getMessagesIdArray,null,null,null,null,counter,offset);	
	}

	/**
	 *  Restituisce gli id dei primi <var>counter</var> i messaggi, in base a servizio_applicativo, servizio
	 *
	 * @param counter Indica il numero di id da ritornare
	 * @param tipoServizio Filtro per Servizio
	 * @param servizio Filtro per Servizio
	 * @param azione Filtro per Azione
	 * @return una Collezione di ID di risposte applicative destinate al servizio applicativo che ha invocato il servizio
	 * 
	 */
	@Override
	public List<String> getMessagesIdArrayByService(int offset,int counter,String tipoServizio,String servizio, String azione) throws IntegrationManagerException {
		init();
		return getAllMessagesId_engine(Operazione.getMessagesIdArrayByService,tipoServizio,servizio,null,azione,counter,offset);	
	}









	/* ------ Get ----- */

	/**
	 * Recupera e restituisce un messaggio
	 *
	 * @param idMessaggio ID del Messaggio da recuperare
	 * @param isRiferimentoMessaggio Indicazione se l'id e' un riferimento messaggio
	 * @return un Message contenente il messaggio recuperato (e informazioni supplementari)
	 * 
	 */
	private IntegrationManagerMessage getMessage_engine(Operazione tipoOperazione, String idMessaggioParam, boolean isRiferimentoMessaggio) throws IntegrationManagerException {

		// Timestamp
		Date dataRichiestaOperazione = DateManager.getTimestamp();
		
		// Logger
		Logger logCore = OpenSPCoop2Logger.getLoggerOpenSPCoopCore();
		if(logCore==null)
			logCore = LoggerWrapperFactory.getLogger(IntegrationManager.ID_MODULO);

		// check Autorizzazione
		checkIMAuthorization(logCore);
		
		// ProtocolFactoyr
		IProtocolFactory<?> protocolFactory = getProtocolFactory(logCore);
		
		// Verifica risorse sistema
		this.verificaRisorseSistema(protocolFactory,logCore, tipoOperazione.toString());

		MsgDiagnostico msgDiag = getMsgDiagnostico();
		msgDiag.addKeyword(CostantiPdD.KEY_TIPO_OPERAZIONE_IM, tipoOperazione.toString());

		// Parse identificativo
		String idMessaggio = null;
		Date dataMessaggio = null;
		if(this.propertiesReader.isIntegrationManagerIdWithDate()) {
			IdentificativoIM idIM = IdentificativoIM.getIdentificativoIM(idMessaggioParam, logCore);
			idMessaggio = idIM.getId();
			dataMessaggio = idIM.getData();
		}
		else {
			idMessaggio = idMessaggioParam;
		}
		
		// Context
		if(isRiferimentoMessaggio){
			msgDiag.addKeyword(CostantiPdD.KEY_RIFERIMENTO_MESSAGGIO_RICHIESTA, idMessaggio);
			msgDiag.addKeyword(CostantiPdD.KEY_RIFERIMENTO_MESSAGGIO_RISPOSTA, idMessaggio);
		}else{
			msgDiag.addKeyword(CostantiPdD.KEY_ID_MESSAGGIO_RICHIESTA, idMessaggio);
			msgDiag.addKeyword(CostantiPdD.KEY_ID_MESSAGGIO_RISPOSTA, idMessaggio);
		}
		
		IDServizioApplicativo idServizioApplicativo = null;
		OpenSPCoopStateful stato = null;
		// PddContext
		PdDContext pddContext = new PdDContext();
		String idTransazione = this.getUniqueIdentifier(logCore, protocolFactory,msgDiag,tipoOperazione.toString()).getAsString();
		pddContext.addObject(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE, idTransazione);
		pddContext.addObject(org.openspcoop2.core.constants.Costanti.ID_MESSAGGIO,idMessaggio);
		pddContext.addObject(org.openspcoop2.core.constants.Costanti.PROTOCOL_NAME,protocolFactory.getProtocol());
		msgDiag.setPddContext(pddContext, protocolFactory);
		
		
		/* ------------  IntegrationManagerRequesCHandler ------------- */
		IntegrationManagerRequestContext imRequestContext = null;
		try{
			imRequestContext = buildIMRequestContext(dataRichiestaOperazione, tipoOperazione, pddContext,logCore,protocolFactory);
			imRequestContext.setIdMessaggio(idMessaggio);
			GestoreHandlers.integrationManagerRequest(imRequestContext, msgDiag, logCore);
		}catch(Exception e){
			ErroreIntegrazione erroreIntegrazione = null;
			IntegrationFunctionError integrationFunctionError = null;
			if(e instanceof HandlerException){
				HandlerException he = (HandlerException) e;
				integrationFunctionError = he.getIntegrationFunctionError();
				if(he.isEmettiDiagnostico()){
					msgDiag.logErroreGenerico(e, ((HandlerException)e).getIdentitaHandler());
				}
				erroreIntegrazione = he.convertToErroreIntegrazione();
			}else{
				msgDiag.logErroreGenerico(e, "IntegrationManagerRequestHandler");
			}
			if(erroreIntegrazione==null){
				erroreIntegrazione = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.getErroreIntegrazione();
			}
			if(integrationFunctionError==null) {
				integrationFunctionError = IntegrationFunctionError.INTERNAL_REQUEST_ERROR;
			}
			throw new IntegrationManagerException(protocolFactory,erroreIntegrazione,
					integrationFunctionError, this.getErroriProperties(logCore));
		}
		
		
		/* ------------  Gestione Operazione ------------- */
		IntegrationManagerResponseContext imResponseContext = 
			new IntegrationManagerResponseContext(dataRichiestaOperazione,tipoOperazione,pddContext,logCore,protocolFactory);
		if(imRequestContext!=null){
			imResponseContext.setConnettore(imRequestContext.getConnettore());
		}
		imResponseContext.setIdMessaggio(idMessaggio);
		try{

			// init stato
			stato = new OpenSPCoopStateful();
			stato.initResource(this.propertiesReader.getIdentitaPortaDefault(protocolFactory.getProtocol(), null),IntegrationManager.ID_MODULO, idTransazione, 
					OpenSPCoopStateDBManager.messageBox);
			ConfigurazionePdDManager configPdDManager = ConfigurazionePdDManager.getInstance(stato.getStatoRichiesta(),stato.getStatoRisposta());
			msgDiag.updateState(configPdDManager);
			
			// gestione credenziali
			this.gestioneCredenziali(logCore, protocolFactory,msgDiag, imRequestContext.getConnettore(),pddContext);
			
			// Autenticazione Servizio Applicativo
			idServizioApplicativo = autenticazione(logCore, protocolFactory,msgDiag,imRequestContext.getConnettore(),configPdDManager,pddContext);
			imResponseContext.setServizioApplicativo(idServizioApplicativo);
			String param = "ServizioApplicativo["+idServizioApplicativo.getNome()+"] ID["+idMessaggio+"]";
			msgDiag.addKeyword(CostantiPdD.KEY_PARAMETRI_OPERAZIONE_IM, param);
			msgDiag.logPersonalizzato("logInvocazioneOperazione");

			// Gestore Messaggio
			GestoreMessaggi gestoreMessaggi = new GestoreMessaggi(stato, true, idMessaggio, Costanti.INBOX, msgDiag, pddContext);
			MessaggioIM messaggioIM = null;
			if(useNewMethod) {
				messaggioIM = gestoreMessaggi.readMessageForSIL(idServizioApplicativo.getNome(),isRiferimentoMessaggio, dataMessaggio);
			}
			
			// Check Esistenza Messaggio
			boolean exists = false;
			if(useNewMethod) {
				exists = messaggioIM!=null;
			}
			else { 
				exists = gestoreMessaggi.existsMessageForSIL(idServizioApplicativo.getNome(),isRiferimentoMessaggio); 
			}
			if(!exists){
				msgDiag.addKeyword(CostantiPdD.KEY_ID_MESSAGGIO_TRANSACTION_MANAGER, idMessaggio);
				msgDiag.logPersonalizzato("messaggioNonTrovato");
				throw new IntegrationManagerException(protocolFactory,ErroriIntegrazione.ERRORE_407_INTEGRATION_MANAGER_MSG_RICHIESTO_NON_TROVATO.
						getErroreIntegrazione(),idServizioApplicativo,
						IntegrationFunctionError.IM_MESSAGE_NOT_FOUND, this.getErroriProperties(logCore));
			}

			// Check Autorizzazione all'utilizzo di IntegrationManager
			boolean authorized = false;
			if(useNewMethod) {
				authorized = messaggioIM.isAuthorized();
			}
			else { 
				authorized = gestoreMessaggi.checkAutorizzazione(idServizioApplicativo.getNome(),isRiferimentoMessaggio);
			}
			if(!authorized){
				msgDiag.logPersonalizzato("servizioApplicativo.nonAutorizzato");
				throw new IntegrationManagerException(protocolFactory,ErroriIntegrazione.ERRORE_404_AUTORIZZAZIONE_FALLITA_SA.
						getErrore404_AutorizzazioneFallitaServizioApplicativo(idServizioApplicativo.getNome()),
						idServizioApplicativo,
						IntegrationFunctionError.AUTHORIZATION, this.getErroriProperties(logCore));
			}

			// GetIDMessaggio Richiesto
			String idMessaggioRichiesto = idMessaggio;
			try{
				if(isRiferimentoMessaggio) {
					if(useNewMethod) {
						idMessaggioRichiesto = messaggioIM.getIdentificativoRichiesta();
					}
					else {
						idMessaggioRichiesto = gestoreMessaggi.mapRiferimentoIntoIDBusta();
					}
				}
			}catch(Exception e){
				msgDiag.addKeywordErroreProcessamento(e);
				msgDiag.logPersonalizzato("mappingRifMsgToId.nonRiuscito");
				throw new IntegrationManagerException(protocolFactory,ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
						get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_520_READ_MSG_FROM_INTEGRATION_MANAGER),idServizioApplicativo,
						IntegrationFunctionError.INTERNAL_REQUEST_ERROR, this.getErroriProperties(logCore));
			}

			// Proprieta' Protocollo del Messaggio da ritornare
			RepositoryBuste repository = new RepositoryBuste(stato.getStatoRichiesta(), true, protocolFactory);
			ProtocolHeaderInfo protocolHeaderInfo = null;
			IDSoggetto fruitore = null;
			IDServizio idServizio = null;
			Busta busta = null;
			try{
				LetturaParametriBusta parametri = new LetturaParametriBusta();
				parametri.setMittente(true);
				parametri.setDestinatario(true);
				parametri.setRiferimentoMessaggio(true);
				parametri.setServizio(true);
				parametri.setAzione(true);
				parametri.setCollaborazione(true);
				busta = repository.getSomeValuesFromInBox(idMessaggioRichiesto,parametri,dataMessaggio);
				if(busta!=null){
					protocolHeaderInfo = new ProtocolHeaderInfo();
					protocolHeaderInfo.setID(idMessaggioRichiesto);
					protocolHeaderInfo.setRiferimentoMessaggio(busta.getRiferimentoMessaggio());
					protocolHeaderInfo.setTipoMittente(busta.getTipoMittente());
					protocolHeaderInfo.setMittente(busta.getMittente());
					protocolHeaderInfo.setTipoDestinatario(busta.getTipoDestinatario());
					protocolHeaderInfo.setDestinatario(busta.getDestinatario());
					protocolHeaderInfo.setTipoServizio(busta.getTipoServizio());
					protocolHeaderInfo.setServizio(busta.getServizio());
					protocolHeaderInfo.setAzione(busta.getAzione());
					protocolHeaderInfo.setIdCollaborazione(busta.getCollaborazione());
					busta.setID(idMessaggioRichiesto);
					msgDiag.addKeywords(busta, true);
					
					fruitore = new IDSoggetto(busta.getTipoMittente(),busta.getMittente());
					idServizio = IDServizioFactory.getInstance().getIDServizioFromValues(busta.getTipoServizio(),busta.getServizio(), 
							busta.getTipoDestinatario(),busta.getDestinatario(), 
							busta.getVersioneServizio());

				}
			}catch(Exception e){
				msgDiag.logErroreGenerico(e,"ReadInformazioniProtocollo("+tipoOperazione+","+idMessaggioRichiesto+")");
				if(e.getMessage()==null || (e.getMessage().indexOf("Busta non trovata")<0) ){
					throw new IntegrationManagerException(protocolFactory,ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
							get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_520_READ_MSG_FROM_INTEGRATION_MANAGER),idServizioApplicativo,
							IntegrationFunctionError.INTERNAL_REQUEST_ERROR, this.getErroriProperties(logCore));
				}// else
				//{
				//busta non presente, il msg puo' cmq essere consumato
				//}
			}


			// Indicazione se il messaggio deve essere sbustato
			boolean sbustamentoSoap = false;
			if(useNewMethod) {
				sbustamentoSoap = messaggioIM.isSbustamentoSoap();
			}
			else {
				sbustamentoSoap = gestoreMessaggi.sbustamentoSOAP(idServizioApplicativo.getNome(),isRiferimentoMessaggio);
			}
			boolean sbustamentoInformazioniProtocollo = false;
			if(useNewMethod) {
				sbustamentoInformazioniProtocollo = messaggioIM.isSbustamentoInformazioniProtocollo();
			}
			else {
				sbustamentoInformazioniProtocollo = gestoreMessaggi.sbustamentoInformazioniProtocollo(idServizioApplicativo.getNome(),isRiferimentoMessaggio);
			}

			// TipoMessaggio
			GestoreMessaggi gestoreMessaggiIdentificativoReale = null;
			RuoloMessaggio ruoloMessaggio = RuoloMessaggio.RICHIESTA;
			try{
				gestoreMessaggiIdentificativoReale = new GestoreMessaggi(stato, true, idMessaggioRichiesto, Costanti.INBOX, msgDiag, pddContext);
				if(useNewMethod) {
					ruoloMessaggio = messaggioIM.getRiferimentoMessaggio()==null ? RuoloMessaggio.RICHIESTA : RuoloMessaggio.RISPOSTA;
				}
				else {
					ruoloMessaggio = gestoreMessaggiIdentificativoReale.getRiferimentoMessaggio()==null ? RuoloMessaggio.RICHIESTA : RuoloMessaggio.RISPOSTA;
				}
			}catch(Exception e){
				logError(logCore, "Identificazione tipo messaggio non riuscita: "+e.getMessage(),e);
			}
			
			// Messaggio
			OpenSPCoop2Message consegnaMessage = null;
			try{
				if(useNewMethod && gestoreMessaggiIdentificativoReale!=null) {
					consegnaMessage = gestoreMessaggiIdentificativoReale.getMessage(dataMessaggio); // risoluzione riferimento messaggio gia' effettuata
				}
				else {
					consegnaMessage = gestoreMessaggi.getMessage(isRiferimentoMessaggio);
				}
			}catch(Exception e){
				msgDiag.logErroreGenerico(e,"gestoreMessaggi.getMessage("+isRiferimentoMessaggio+","+tipoOperazione+")");
				throw new IntegrationManagerException(protocolFactory,ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
						get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_520_READ_MSG_FROM_INTEGRATION_MANAGER),idServizioApplicativo,
						IntegrationFunctionError.INTERNAL_REQUEST_ERROR, this.getErroriProperties(logCore));
			}
			
			// Eventuale sbustamento
			if(sbustamentoInformazioniProtocollo){
				try{
					IBustaBuilder<?> bustaBuilder = protocolFactory.createBustaBuilder(stato.getStatoRichiesta());
					
					FaseSbustamento fase = null;
					if(RuoloMessaggio.RICHIESTA.equals(ruoloMessaggio)){
						fase = FaseSbustamento.PRE_CONSEGNA_RICHIESTA;
					}else{
						fase = FaseSbustamento.PRE_CONSEGNA_RISPOSTA;
					}
					
					// attachments non gestiti!
					ProprietaManifestAttachments proprietaManifest = this.propertiesReader.getProprietaManifestAttachments("standard");
					proprietaManifest.setGestioneManifest(false);
					ProtocolMessage  protocolMessage = bustaBuilder.sbustamento(consegnaMessage, pddContext,
							busta, ruoloMessaggio, proprietaManifest, fase, null, null); 
					if(protocolMessage!=null) {
						consegnaMessage = protocolMessage.getMessage(); // updated
					}
				}catch(Exception e){
					msgDiag.logErroreGenerico(e,"gestoreMessaggi.getMessage("+isRiferimentoMessaggio+","+tipoOperazione+")");
					throw new IntegrationManagerException(protocolFactory,ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
							get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_520_READ_MSG_FROM_INTEGRATION_MANAGER),idServizioApplicativo,
							IntegrationFunctionError.INTERNAL_REQUEST_ERROR, this.getErroriProperties(logCore));				
				}
			}
			
			//	dump applicativo
			msgDiag.mediumDebug("Dump applicativo messaggio ritornato...");
			Dump dumpApplicativo = new Dump(this.propertiesReader.getIdentitaPortaDefault(protocolFactory.getProtocol(), null),
					IntegrationManager.ID_MODULO,idMessaggio,
					fruitore,idServizio,
					TipoPdD.INTEGRATION_MANAGER,null,pddContext,
					stato.getStatoRichiesta(),stato.getStatoRisposta(),
					null);
			dumpApplicativo.dumpIntegrationManagerGetMessage(consegnaMessage);

			// Costruzione Message da ritornare
			IntegrationManagerMessage msgReturn = null;
			try{
				if(!sbustamentoSoap){
					msgReturn = new IntegrationManagerMessage(consegnaMessage,false,protocolHeaderInfo);
				}else{
					byte [] sbustato = TunnelSoapUtils.sbustamentoMessaggio(consegnaMessage);
					msgReturn = new IntegrationManagerMessage(sbustato,false,protocolHeaderInfo);
				}
			}catch(Exception e){
				msgDiag.logErroreGenerico(e,"buildMsgReturn("+idMessaggio+","+tipoOperazione+")");
				throw new IntegrationManagerException(protocolFactory,ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
						get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_523_CREAZIONE_PROTOCOL_MESSAGE),idServizioApplicativo,
						IntegrationFunctionError.INTERNAL_REQUEST_ERROR, this.getErroriProperties(logCore));
			}

			imResponseContext.setEsito(this.getEsitoTransazione(protocolFactory, imRequestContext, EsitoTransazioneName.OK));
			imResponseContext.setDimensioneMessaggioBytes(Long.valueOf(msgReturn.getMessage().length));
			
			// Informazione da salvare
			try {
				TransazioneApplicativoServer transazioneApplicativoServer = null;
				String nomePorta = null;
				if(useNewMethod) {
					transazioneApplicativoServer = new TransazioneApplicativoServer();
					transazioneApplicativoServer.setIdTransazione(messaggioIM.getIdTransazione());
					transazioneApplicativoServer.setServizioApplicativoErogatore(messaggioIM.getServizioApplicativo());
					transazioneApplicativoServer.setDataRegistrazione(messaggioIM.getOraRegistrazione());
					transazioneApplicativoServer.setDataPrelievoIm(DateManager.getDate());
					transazioneApplicativoServer.setProtocollo(protocolFactory.getProtocol());
					nomePorta = messaggioIM.getNomePorta();
				}
				else {
					MessaggioServizioApplicativo info = gestoreMessaggiIdentificativoReale.readInfoDestinatario(idServizioApplicativo.getNome(), true, logCore);
					if(info!=null) {
						transazioneApplicativoServer = new TransazioneApplicativoServer();
						transazioneApplicativoServer.setIdTransazione(info.getIdTransazione());
						transazioneApplicativoServer.setServizioApplicativoErogatore(info.getServizioApplicativo());
						transazioneApplicativoServer.setDataRegistrazione(info.getOraRegistrazione());
						transazioneApplicativoServer.setDataPrelievoIm(DateManager.getDate());
						transazioneApplicativoServer.setProtocollo(protocolFactory.getProtocol());
						nomePorta = info.getNomePorta();
					}
				}
				if(transazioneApplicativoServer!=null) {
					IDPortaApplicativa idPA = new IDPortaApplicativa();
					idPA.setNome(nomePorta);
					try {
						GestoreConsegnaMultipla.getInstance().safeUpdatePrelievoIM(transazioneApplicativoServer, idPA, stato, pddContext);
					}catch(Throwable t) {
						logError(logCore, "["+transazioneApplicativoServer.getIdTransazione()+"]["+transazioneApplicativoServer.getServizioApplicativoErogatore()+"] Errore durante il salvataggio delle informazioni relative al servizio applicativo: "+t.getMessage(),t);
					}
				}
			}catch(Exception e){
				logError(logCore, "Salvataggio informazioni sulla transazione non riuscita: "+e.getMessage(),e);
			}
			
			return msgReturn;

		}catch(Exception e){
			
			try{
				imResponseContext.setEsito(this.getEsitoTransazione(protocolFactory, imRequestContext, EsitoTransazioneName.ERRORE_PROCESSAMENTO_PDD_5XX));
				
				if(e instanceof IntegrationManagerException){
					IntegrationManagerException exc = (IntegrationManagerException) e;
					if(CodiceErroreIntegrazione.CODICE_402_AUTENTICAZIONE_FALLITA.equals(exc.getCodiceErroreIntegrazione()) ||
							CodiceErroreIntegrazione.CODICE_431_GESTORE_CREDENZIALI_ERROR.equals(exc.getCodiceErroreIntegrazione())){
						imResponseContext.setEsito(this.getEsitoTransazione(protocolFactory, imRequestContext, EsitoTransazioneName.AUTENTICAZIONE_FALLITA));
					}else if(CodiceErroreIntegrazione.CODICE_407_INTEGRATION_MANAGER_MSG_RICHIESTO_NON_TROVATO.equals(exc.getCodiceErroreIntegrazione())){
						imResponseContext.setEsito(this.getEsitoTransazione(protocolFactory, imRequestContext, EsitoTransazioneName.MESSAGGIO_NON_TROVATO));
					}else if(CodiceErroreIntegrazione.CODICE_404_AUTORIZZAZIONE_FALLITA.equals(exc.getCodiceErroreIntegrazione())){
						imResponseContext.setEsito(this.getEsitoTransazione(protocolFactory, imRequestContext, EsitoTransazioneName.AUTORIZZAZIONE_FALLITA));
					}
				}
			}catch(Exception eInteral){
				logError(logCore, "Errore durante la generazione dell'esito: "+eInteral.getMessage(),eInteral);
				imResponseContext.setEsito(EsitoTransazione.ESITO_TRANSAZIONE_ERROR);
			}
			
			// Controllo Eccezioni
			if(e instanceof IntegrationManagerException){
				// Lanciata all'interno del metodo
				throw (IntegrationManagerException)e;
			}else{	
				msgDiag.logErroreGenerico(e,"getMessage("+tipoOperazione+")");
				throw new IntegrationManagerException(protocolFactory,ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.getErroreIntegrazione(),idServizioApplicativo,
						IntegrationFunctionError.INTERNAL_REQUEST_ERROR, this.getErroriProperties(logCore));
			}
		}finally{
			try{
				if(stato!=null)
					stato.releaseResource();
			}catch(Exception eClose){}
			
			
			
			/* ------------  IntegrationManagerResponseHandler ------------- */
			imResponseContext.setDataCompletamentoOperazione(DateManager.getDate());
			try{
				GestoreHandlers.integrationManagerResponse(imResponseContext, msgDiag, logCore);
			}catch(Exception e){
				if(e instanceof HandlerException){
					HandlerException he = (HandlerException) e;
					if(he.isEmettiDiagnostico()){
						msgDiag.logErroreGenerico(e, ((HandlerException)e).getIdentitaHandler());
					}
				}else{
					msgDiag.logErroreGenerico(e, "IntegrationManagerResponseHandler");
				}
			}
		}
	}

	/**
	 * Recupera e restituisce un messaggio
	 *
	 * @param idMessaggio ID del Messaggio da recuperare
	 * @return un Message contenente il messaggio recuperato (e informazioni supplementari)
	 * 
	 */
	@Override
	public IntegrationManagerMessage getMessage(String idMessaggio) throws IntegrationManagerException {
		init();
		return getMessage_engine(Operazione.getMessage,idMessaggio,false);
	}

	/**
	 * Recupera e restituisce un messaggio cercandolo per riferimentoMessaggio
	 *
	 * @param riferimentoMsg del Messaggio da recuperare
	 * @return un Message contenente il messaggio recuperato (e informazioni supplementari)
	 * 
	 */
	@Override
	public IntegrationManagerMessage getMessageByReference(String riferimentoMsg) throws IntegrationManagerException {
		init();
		return getMessage_engine(Operazione.getMessageByReference,riferimentoMsg,true);
	}





















	/* -------- delete ------ */
	/**
	 * Cancella un messaggio
	 *
	 * @param idMessaggio ID del Messaggio da recuperare
	 * @param isRiferimentoMessaggio Indicazione se l'id e' un riferimento messaggio
	 * 
	 */
	private void deleteMessage_engine(Operazione tipoOperazione,String idMessaggioParam,boolean isRiferimentoMessaggio) throws IntegrationManagerException {

		// Timestamp
		Date dataRichiestaOperazione = DateManager.getTimestamp();
		
		// Logger
		Logger logCore = OpenSPCoop2Logger.getLoggerOpenSPCoopCore();
		if(logCore==null)
			logCore = LoggerWrapperFactory.getLogger(IntegrationManager.ID_MODULO);

		// check Autorizzazione
		checkIMAuthorization(logCore);
		
		// ProtocolFactoyr
		IProtocolFactory<?> protocolFactory = getProtocolFactory(logCore);
		
		// Verifica risorse sistema
		this.verificaRisorseSistema(protocolFactory,logCore, tipoOperazione.toString());

		MsgDiagnostico msgDiag = getMsgDiagnostico();
		msgDiag.addKeyword(CostantiPdD.KEY_TIPO_OPERAZIONE_IM, tipoOperazione.toString());
		
		// Parse identificativo
		String idMessaggio = null;
		Date dataMessaggio = null;
		if(this.propertiesReader.isIntegrationManagerIdWithDate()) {
			IdentificativoIM idIM = IdentificativoIM.getIdentificativoIM(idMessaggioParam, logCore);
			idMessaggio = idIM.getId();
			dataMessaggio = idIM.getData();
		}
		else {
			idMessaggio = idMessaggioParam;
		}
		
		// Context
		if(isRiferimentoMessaggio){
			msgDiag.addKeyword(CostantiPdD.KEY_RIFERIMENTO_MESSAGGIO_RICHIESTA, idMessaggio);
			msgDiag.addKeyword(CostantiPdD.KEY_RIFERIMENTO_MESSAGGIO_RISPOSTA, idMessaggio);
		}else{
			msgDiag.addKeyword(CostantiPdD.KEY_ID_MESSAGGIO_RICHIESTA, idMessaggio);
			msgDiag.addKeyword(CostantiPdD.KEY_ID_MESSAGGIO_RISPOSTA, idMessaggio);
		}
		
		GestoreMessaggi gestoreMessaggi = null;
		IDServizioApplicativo idServizioApplicativo = null;
		String servizioApplicativo = null;
		OpenSPCoopStateful stato = null;
		// PddContext
		PdDContext pddContext = new PdDContext();
		String idTransazione = this.getUniqueIdentifier(logCore, protocolFactory,msgDiag,tipoOperazione.toString()).getAsString();
		pddContext.addObject(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE, idTransazione);
		pddContext.addObject(org.openspcoop2.core.constants.Costanti.ID_MESSAGGIO,idMessaggio);
		pddContext.addObject(org.openspcoop2.core.constants.Costanti.PROTOCOL_NAME, protocolFactory.getProtocol());
		msgDiag.setPddContext(pddContext, protocolFactory);
		
		
		
		/* ------------  IntegrationManagerRequesCHandler ------------- */
		IntegrationManagerRequestContext imRequestContext = null;
		try{
			imRequestContext = buildIMRequestContext(dataRichiestaOperazione, tipoOperazione, pddContext,logCore,protocolFactory);
			imRequestContext.setIdMessaggio(idMessaggio);
			GestoreHandlers.integrationManagerRequest(imRequestContext, msgDiag, logCore);
		}catch(Exception e){
			ErroreIntegrazione erroreIntegrazione = null;
			IntegrationFunctionError integrationFunctionError = null;
			if(e instanceof HandlerException){
				HandlerException he = (HandlerException) e;
				integrationFunctionError = he.getIntegrationFunctionError();
				if(he.isEmettiDiagnostico()){
					msgDiag.logErroreGenerico(e, ((HandlerException)e).getIdentitaHandler());
				}
				erroreIntegrazione = he.convertToErroreIntegrazione();
			}else{
				msgDiag.logErroreGenerico(e, "IntegrationManagerRequestHandler");
			}
			if(erroreIntegrazione==null){
				erroreIntegrazione = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.getErroreIntegrazione();
			}
			if(integrationFunctionError==null) {
				integrationFunctionError = IntegrationFunctionError.INTERNAL_REQUEST_ERROR;
			}
			throw new IntegrationManagerException(protocolFactory,erroreIntegrazione,
					integrationFunctionError, this.getErroriProperties(logCore));
		}
		
		
		/* ------------  Gestione Operazione ------------- */
		IntegrationManagerResponseContext imResponseContext = 
			new IntegrationManagerResponseContext(dataRichiestaOperazione,tipoOperazione,pddContext,logCore,protocolFactory);
		if(imRequestContext!=null){
			imResponseContext.setConnettore(imRequestContext.getConnettore());
		}
		imResponseContext.setIdMessaggio(idMessaggio);
		try{	

			// init stato
			stato = new OpenSPCoopStateful();
			stato.initResource(this.propertiesReader.getIdentitaPortaDefault(protocolFactory.getProtocol(), null),IntegrationManager.ID_MODULO, idTransazione, 
					OpenSPCoopStateDBManager.messageBox);
			ConfigurazionePdDManager configPdDManager = ConfigurazionePdDManager.getInstance(stato.getStatoRichiesta(),stato.getStatoRisposta());
			msgDiag.updateState(configPdDManager);	
			
			// gestione credenziali
			this.gestioneCredenziali(logCore, protocolFactory,msgDiag, imRequestContext.getConnettore(),pddContext);
			
			// Autenticazione Servizio Applicativo
			idServizioApplicativo = autenticazione(logCore, protocolFactory,msgDiag,imRequestContext.getConnettore(),configPdDManager,pddContext);
			servizioApplicativo = idServizioApplicativo.getNome();
			imResponseContext.setServizioApplicativo(idServizioApplicativo);
			String param = "ServizioApplicativo["+servizioApplicativo+"] ID["+idMessaggio+"]";
			msgDiag.addKeyword(CostantiPdD.KEY_PARAMETRI_OPERAZIONE_IM, param);
			msgDiag.logPersonalizzato("logInvocazioneOperazione");

			// Gestore Messaggio
			gestoreMessaggi = new GestoreMessaggi(stato, true, idMessaggio, Costanti.INBOX, msgDiag, pddContext);
			MessaggioIM messaggioIM = null;
			if(useNewMethod) {
				messaggioIM = gestoreMessaggi.readMessageForSIL(idServizioApplicativo.getNome(),isRiferimentoMessaggio, dataMessaggio);
			}
			
			// Check Esistenza Messaggio
			boolean exists = false;
			if(useNewMethod) {
				exists = messaggioIM!=null;
			}
			else { 
				exists = gestoreMessaggi.existsMessageForSIL(servizioApplicativo,isRiferimentoMessaggio); 
			}
			if(!exists){
				msgDiag.addKeyword(CostantiPdD.KEY_ID_MESSAGGIO_TRANSACTION_MANAGER, idMessaggio);
				msgDiag.logPersonalizzato("messaggioNonTrovato");
				throw new IntegrationManagerException(protocolFactory,ErroriIntegrazione.ERRORE_407_INTEGRATION_MANAGER_MSG_RICHIESTO_NON_TROVATO.
						getErroreIntegrazione(),idServizioApplicativo,
						IntegrationFunctionError.IM_MESSAGE_NOT_FOUND, this.getErroriProperties(logCore));
			}

			// Check Autorizzazione all'utilizzo di IntegrationManager
			boolean authorized = false;
			if(useNewMethod) {
				authorized = messaggioIM.isAuthorized();
			}
			else { 
				authorized = gestoreMessaggi.checkAutorizzazione(servizioApplicativo,isRiferimentoMessaggio);
			}
			if(!authorized){
				msgDiag.logPersonalizzato("servizioApplicativo.nonAutorizzato");
				throw new IntegrationManagerException(protocolFactory,ErroriIntegrazione.ERRORE_404_AUTORIZZAZIONE_FALLITA_SA.
						getErrore404_AutorizzazioneFallitaServizioApplicativo(servizioApplicativo),
						idServizioApplicativo,
						IntegrationFunctionError.AUTHORIZATION, this.getErroriProperties(logCore));
			}

			//	GetIDMessaggio Richiesto
			String idMessaggioRichiesto = idMessaggio;
			try{
				if(isRiferimentoMessaggio) {
					if(useNewMethod) {
						idMessaggioRichiesto = messaggioIM.getIdentificativoRichiesta();
					}
					else {
						idMessaggioRichiesto = gestoreMessaggi.mapRiferimentoIntoIDBusta();
					}
				}
			}catch(Exception e){
				msgDiag.addKeywordErroreProcessamento(e);
				msgDiag.logPersonalizzato("mappingRifMsgToId.nonRiuscito");
				throw new IntegrationManagerException(protocolFactory,ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
						get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_520_READ_MSG_FROM_INTEGRATION_MANAGER),idServizioApplicativo,
						IntegrationFunctionError.INTERNAL_REQUEST_ERROR, this.getErroriProperties(logCore));
			}

			//	Gestore Messaggio da eliminare
			GestoreMessaggi gestoreEliminazione = null;
			try{
				gestoreEliminazione = new GestoreMessaggi(stato, true, idMessaggioRichiesto,Costanti.INBOX,msgDiag,pddContext);
			}catch(Exception e){
				msgDiag.logErroreGenerico(e,"gestoreMessaggi.eliminaDestinatarioMessaggio("+tipoOperazione+","+servizioApplicativo+","+idMessaggio+")");
				throw new IntegrationManagerException(protocolFactory,ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
						get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_522_DELETE_MSG_FROM_INTEGRATION_MANAGER),idServizioApplicativo,
						IntegrationFunctionError.INTERNAL_REQUEST_ERROR, this.getErroriProperties(logCore));
			}
			
			// Aggiorno la transazione prima di eliminare il messaggio
			try {
				TransazioneApplicativoServer transazioneApplicativoServer = null;
				String nomePorta = null;
				if(useNewMethod) {
					transazioneApplicativoServer = new TransazioneApplicativoServer();
					transazioneApplicativoServer.setIdTransazione(messaggioIM.getIdTransazione());
					transazioneApplicativoServer.setServizioApplicativoErogatore(messaggioIM.getServizioApplicativo());
					transazioneApplicativoServer.setDataRegistrazione(messaggioIM.getOraRegistrazione());
					transazioneApplicativoServer.setDataEliminazioneIm(DateManager.getDate());
					transazioneApplicativoServer.setProtocollo(protocolFactory.getProtocol());
					nomePorta = messaggioIM.getNomePorta();
				}
				else {
					MessaggioServizioApplicativo info = gestoreEliminazione.readInfoDestinatario(idServizioApplicativo.getNome(), true, logCore);
					if(info!=null) {
						transazioneApplicativoServer = new TransazioneApplicativoServer();
						transazioneApplicativoServer.setIdTransazione(info.getIdTransazione());
						transazioneApplicativoServer.setServizioApplicativoErogatore(info.getServizioApplicativo());
						transazioneApplicativoServer.setDataRegistrazione(info.getOraRegistrazione());
						transazioneApplicativoServer.setDataEliminazioneIm(DateManager.getDate());
						transazioneApplicativoServer.setProtocollo(protocolFactory.getProtocol());
						nomePorta = info.getNomePorta();
					}
				}
				if(transazioneApplicativoServer!=null) {
					IDPortaApplicativa idPA = new IDPortaApplicativa();
					idPA.setNome(nomePorta);
					try {
						GestoreConsegnaMultipla.getInstance().safeUpdateEliminazioneIM(transazioneApplicativoServer, idPA, stato, pddContext);
					}catch(Throwable t) {
						logError(logCore, "["+transazioneApplicativoServer.getIdTransazione()+"]["+transazioneApplicativoServer.getServizioApplicativoErogatore()+"] Errore durante il salvataggio delle informazioni relative al servizio applicativo: "+t.getMessage(),t);
					}
				}
			}catch(Exception e){
				logError(logCore, "Salvataggio informazioni sulla transazione non riuscita: "+e.getMessage(),e);
			}
			
			// Elimino Messaggio
			try{
				gestoreEliminazione.eliminaDestinatarioMessaggio(servizioApplicativo, gestoreEliminazione.getRiferimentoMessaggio(), dataMessaggio);
			}catch(Exception e){
				msgDiag.logErroreGenerico(e,"gestoreMessaggi.eliminaDestinatarioMessaggio("+tipoOperazione+","+servizioApplicativo+","+idMessaggio+")");
				throw new IntegrationManagerException(protocolFactory,ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
						get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_522_DELETE_MSG_FROM_INTEGRATION_MANAGER),idServizioApplicativo,
						IntegrationFunctionError.INTERNAL_REQUEST_ERROR, this.getErroriProperties(logCore));
			}

			imResponseContext.setEsito(this.getEsitoTransazione(protocolFactory, imRequestContext, EsitoTransazioneName.OK));
			
		}catch(Exception e){
			
			try{
				imResponseContext.setEsito(this.getEsitoTransazione(protocolFactory, imRequestContext, EsitoTransazioneName.ERRORE_PROCESSAMENTO_PDD_5XX));
				
				if(e instanceof IntegrationManagerException){
					IntegrationManagerException exc = (IntegrationManagerException) e;
					if(CodiceErroreIntegrazione.CODICE_402_AUTENTICAZIONE_FALLITA.equals(exc.getCodiceErroreIntegrazione()) ||
							CodiceErroreIntegrazione.CODICE_431_GESTORE_CREDENZIALI_ERROR.equals(exc.getCodiceErroreIntegrazione())){
						imResponseContext.setEsito(this.getEsitoTransazione(protocolFactory, imRequestContext, EsitoTransazioneName.AUTENTICAZIONE_FALLITA));
					}else if(CodiceErroreIntegrazione.CODICE_407_INTEGRATION_MANAGER_MSG_RICHIESTO_NON_TROVATO.equals(exc.getCodiceErroreIntegrazione())){
						imResponseContext.setEsito(this.getEsitoTransazione(protocolFactory, imRequestContext, EsitoTransazioneName.MESSAGGIO_NON_TROVATO));
					}else if(CodiceErroreIntegrazione.CODICE_404_AUTORIZZAZIONE_FALLITA.equals(exc.getCodiceErroreIntegrazione())){
						imResponseContext.setEsito(this.getEsitoTransazione(protocolFactory, imRequestContext, EsitoTransazioneName.AUTORIZZAZIONE_FALLITA));
					}
				}
			}catch(Exception eInteral){
				logError(logCore, "Errore durante la generazione dell'esito: "+eInteral.getMessage(),eInteral);
				imResponseContext.setEsito(EsitoTransazione.ESITO_TRANSAZIONE_ERROR);
			}
			
			// Controllo Eccezioni
			if(e instanceof IntegrationManagerException){
				// Lanciata all'interno del metodo
				throw (IntegrationManagerException)e;
			}else{
				msgDiag.logErroreGenerico(e,"deleteMessage("+tipoOperazione+")");
				throw new IntegrationManagerException(protocolFactory,ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.getErroreIntegrazione(),idServizioApplicativo,
						IntegrationFunctionError.INTERNAL_REQUEST_ERROR, this.getErroriProperties(logCore));
			}
		}finally{
			try{
				if(stato!=null)
					stato.releaseResource();
			}catch(Exception eClose){}
			
			
			/* ------------  IntegrationManagerResponseHandler ------------- */
			imResponseContext.setDataCompletamentoOperazione(DateManager.getDate());
			try{
				GestoreHandlers.integrationManagerResponse(imResponseContext, msgDiag, logCore);
			}catch(Exception e){
				if(e instanceof HandlerException){
					HandlerException he = (HandlerException) e;
					if(he.isEmettiDiagnostico()){
						msgDiag.logErroreGenerico(e, ((HandlerException)e).getIdentitaHandler());
					}
				}else{
					msgDiag.logErroreGenerico(e, "IntegrationManagerResponseHandler");
				}
			}
		}
	}

	/**
	 * Cancella un messaggio
	 *
	 * @param idMessaggio ID del Messaggio da recuperare
	 * 
	 */
	@Override
	public void deleteMessage(String idMessaggio) throws IntegrationManagerException {
		init();
		deleteMessage_engine(Operazione.deleteMessage,idMessaggio,false);
	}

	/**
	 * Cancella un messaggio cercandolo per riferimentoMessaggio
	 *
	 * @param riferimentoMsg del Messaggio da recuperare
	 * 
	 */
	@Override
	public void deleteMessageByReference(String riferimentoMsg) throws IntegrationManagerException {
		init();
		deleteMessage_engine(Operazione.deleteMessageByReference,riferimentoMsg,true);
	}






















	/* --------- delete All Messages ----------- */

	/**
	 * Cancella tutti i messaggi di un servizio applicativo
	 *
	 * 
	 */
	@Override
	public void deleteAllMessages() throws IntegrationManagerException {

		init();
		
		Operazione tipoOperazione = Operazione.deleteAllMessages;

		// Timestamp
		Date dataRichiestaOperazione = DateManager.getTimestamp();
		
		// Logger
		Logger logCore = OpenSPCoop2Logger.getLoggerOpenSPCoopCore();
		if(logCore==null)
			logCore = LoggerWrapperFactory.getLogger(IntegrationManager.ID_MODULO);

		// check Autorizzazione
		checkIMAuthorization(logCore);
		
		// ProtocolFactoyr
		IProtocolFactory<?> protocolFactory = getProtocolFactory(logCore);
		
		// Verifica risorse sistema
		this.verificaRisorseSistema(protocolFactory,logCore, tipoOperazione.toString());

		MsgDiagnostico msgDiag = getMsgDiagnostico();
		msgDiag.addKeyword(CostantiPdD.KEY_TIPO_OPERAZIONE_IM, tipoOperazione.toString());
		
		IDServizioApplicativo idServizioApplicativo = null;
		OpenSPCoopStateful stato = null;
		// PddContext
		PdDContext pddContext = new PdDContext();
		String idTransazione = this.getUniqueIdentifier(logCore, protocolFactory,msgDiag,tipoOperazione.toString()).getAsString();
		pddContext.addObject(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE, idTransazione);
		pddContext.addObject(org.openspcoop2.core.constants.Costanti.PROTOCOL_NAME, protocolFactory.getProtocol());
		msgDiag.setPddContext(pddContext, protocolFactory);
		
		
		
		
		/* ------------  IntegrationManagerRequesCHandler ------------- */
		IntegrationManagerRequestContext imRequestContext = null;
		try{
			imRequestContext = buildIMRequestContext(dataRichiestaOperazione, tipoOperazione, pddContext,logCore,protocolFactory);
			GestoreHandlers.integrationManagerRequest(imRequestContext, msgDiag, logCore);
		}catch(Exception e){
			ErroreIntegrazione erroreIntegrazione = null;
			IntegrationFunctionError integrationFunctionError = null;
			if(e instanceof HandlerException){
				HandlerException he = (HandlerException) e;
				integrationFunctionError = he.getIntegrationFunctionError();
				if(he.isEmettiDiagnostico()){
					msgDiag.logErroreGenerico(e, ((HandlerException)e).getIdentitaHandler());
				}
				erroreIntegrazione = he.convertToErroreIntegrazione();
			}else{
				msgDiag.logErroreGenerico(e, "IntegrationManagerRequestHandler");
			}
			if(erroreIntegrazione==null){
				erroreIntegrazione = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.getErroreIntegrazione();
			}
			if(integrationFunctionError==null) {
				integrationFunctionError = IntegrationFunctionError.INTERNAL_REQUEST_ERROR;
			}
			throw new IntegrationManagerException(protocolFactory,erroreIntegrazione,
					integrationFunctionError, getErroriProperties(logCore));
		}
		
		
		/* ------------  Gestione Operazione ------------- */
		IntegrationManagerResponseContext imResponseContext = 
			new IntegrationManagerResponseContext(dataRichiestaOperazione,tipoOperazione,pddContext,logCore,protocolFactory);
		if(imRequestContext!=null){
			imResponseContext.setConnettore(imRequestContext.getConnettore());
		}
		try{

			// init stato
			stato = new OpenSPCoopStateful();
			stato.initResource(this.propertiesReader.getIdentitaPortaDefault(protocolFactory.getProtocol(), null),IntegrationManager.ID_MODULO, idTransazione, 
					OpenSPCoopStateDBManager.messageBox);
			ConfigurazionePdDManager configPdDManager = ConfigurazionePdDManager.getInstance(stato.getStatoRichiesta(),stato.getStatoRisposta());
			msgDiag.updateState(configPdDManager);	
			
			// gestione credenziali
			this.gestioneCredenziali(logCore, protocolFactory,msgDiag, imRequestContext.getConnettore(),pddContext);
			
			// Autenticazione Servizio Applicativo
			idServizioApplicativo = autenticazione(logCore, protocolFactory,msgDiag,imRequestContext.getConnettore(),configPdDManager,pddContext);
			imResponseContext.setServizioApplicativo(idServizioApplicativo);
			String param = "ServizioApplicativo["+idServizioApplicativo.getNome()+"]";
			msgDiag.addKeyword(CostantiPdD.KEY_PARAMETRI_OPERAZIONE_IM, param);
			msgDiag.logPersonalizzato("logInvocazioneOperazione");

			// Effettuo ricerca ID DEL SERVIZIO APPLICATIVO
			GestoreMessaggi gestoreSearchID = new GestoreMessaggi(stato, true,msgDiag,pddContext);

			List<IdentificativoIM> ids =  gestoreSearchID.getIDMessaggi_ServizioApplicativo(idServizioApplicativo.getNome());      
			if(ids.isEmpty()){
				msgDiag.logPersonalizzato("messaggiNonPresenti");
				throw new IntegrationManagerException(protocolFactory,ErroriIntegrazione.ERRORE_406_INTEGRATION_MANAGER_MESSAGGI_FOR_SIL_NON_TROVATI.
						getErroreIntegrazione(),idServizioApplicativo,
						IntegrationFunctionError.IM_MESSAGES_NOT_FOUND, this.getErroriProperties(logCore));
			}

			// Creo i vari gestori di messaggi
			List<MessaggioIM> messaggiIM = new ArrayList<MessaggioIM>();
			for(int i=0; i<ids.size(); i++){
				IdentificativoIM idIM = ids.get(i);
				String idMessaggio = idIM.getId();
				Date dataMessaggio = idIM.getData();
				
				GestoreMessaggi gestoreMessaggi = new GestoreMessaggi(stato, true,idMessaggio,Costanti.INBOX,msgDiag,pddContext);
				MessaggioIM messaggioIM = null;
				if(useNewMethod) {
					messaggioIM = gestoreMessaggi.readMessageForSIL(idServizioApplicativo.getNome(), dataMessaggio);
					if(messaggioIM!=null) {
						messaggiIM.add(messaggioIM);
					}
				}
				
				// Check Esistenza Messaggio
				boolean exists = false;
				if(useNewMethod) {
					exists = messaggioIM!=null;
				}
				else { 
					exists = gestoreMessaggi.existsMessageForSIL(idServizioApplicativo.getNome()); 
				}
				if(!exists){
					msgDiag.addKeyword(CostantiPdD.KEY_ID_MESSAGGIO_TRANSACTION_MANAGER, idMessaggio);
					msgDiag.logPersonalizzato("messaggioNonTrovato");
					throw new IntegrationManagerException(protocolFactory,ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
							get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_520_READ_MSG_FROM_INTEGRATION_MANAGER),idServizioApplicativo,
							IntegrationFunctionError.INTERNAL_REQUEST_ERROR, this.getErroriProperties(logCore));
				}

				// Check Autorizzazione all'utilizzo di IntegrationManager
				boolean authorized = false;
				if(useNewMethod) {
					authorized = messaggioIM.isAuthorized();
				}
				else { 
					authorized = gestoreMessaggi.checkAutorizzazione(idServizioApplicativo.getNome());
				}
				if(!authorized){
					msgDiag.logPersonalizzato("servizioApplicativo.nonAutorizzato");
					throw new IntegrationManagerException(protocolFactory,ErroriIntegrazione.ERRORE_404_AUTORIZZAZIONE_FALLITA_SA
							.getErrore404_AutorizzazioneFallitaServizioApplicativo(idServizioApplicativo.getNome()),
							idServizioApplicativo,
							IntegrationFunctionError.AUTHORIZATION, this.getErroriProperties(logCore));
				}
			}

			// Elimino i messaggi
			while(!ids.isEmpty()){
				// Elimino Messaggio
				IdentificativoIM idIM = ids.remove(0);
				String idMessaggio = idIM.getId();
				Date dataMessaggio = idIM.getData();
				MessaggioIM messaggioIM = null;
				if(useNewMethod) {
					messaggioIM = messaggiIM.remove(0);
				}
				
				// Gestore Messaggio da eliminare
				GestoreMessaggi gestoreEliminazione = null;
				try{
					//	Elimino accesso daPdD
					gestoreEliminazione = new GestoreMessaggi(stato, true,idMessaggio,Costanti.INBOX,msgDiag,pddContext);
				}catch(Exception e){
					msgDiag.logErroreGenerico(e,"gestoreMessaggi.eliminaDestinatarioMessaggio("+tipoOperazione+","+idServizioApplicativo.getNome()+","+idMessaggio+")");
					throw new IntegrationManagerException(protocolFactory,ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
							get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_522_DELETE_MSG_FROM_INTEGRATION_MANAGER),idServizioApplicativo,
							IntegrationFunctionError.INTERNAL_REQUEST_ERROR, this.getErroriProperties(logCore));
				}
				
				// Aggiorno la transazione prima di eliminare il messaggio
				try {
					TransazioneApplicativoServer transazioneApplicativoServer = null;
					String nomePorta = null;
					if(useNewMethod) {
						transazioneApplicativoServer = new TransazioneApplicativoServer();
						transazioneApplicativoServer.setIdTransazione(messaggioIM.getIdTransazione());
						transazioneApplicativoServer.setServizioApplicativoErogatore(messaggioIM.getServizioApplicativo());
						transazioneApplicativoServer.setDataRegistrazione(messaggioIM.getOraRegistrazione());
						transazioneApplicativoServer.setDataEliminazioneIm(DateManager.getDate());
						transazioneApplicativoServer.setProtocollo(protocolFactory.getProtocol());
						nomePorta = messaggioIM.getNomePorta();
					}
					else {				
						MessaggioServizioApplicativo info = gestoreEliminazione.readInfoDestinatario(idServizioApplicativo.getNome(), true, logCore);
						if(info!=null) {
							transazioneApplicativoServer = new TransazioneApplicativoServer();
							transazioneApplicativoServer.setIdTransazione(info.getIdTransazione());
							transazioneApplicativoServer.setServizioApplicativoErogatore(info.getServizioApplicativo());
							transazioneApplicativoServer.setDataRegistrazione(info.getOraRegistrazione());
							transazioneApplicativoServer.setDataEliminazioneIm(DateManager.getDate());
							transazioneApplicativoServer.setProtocollo(protocolFactory.getProtocol());
							nomePorta = info.getNomePorta();
						}
					}
					if(transazioneApplicativoServer!=null) {
						IDPortaApplicativa idPA = new IDPortaApplicativa();
						idPA.setNome(nomePorta);
						try {
							GestoreConsegnaMultipla.getInstance().safeUpdateEliminazioneIM(transazioneApplicativoServer, idPA, stato, pddContext);
						}catch(Throwable t) {
							logError(logCore, "["+transazioneApplicativoServer.getIdTransazione()+"]["+transazioneApplicativoServer.getServizioApplicativoErogatore()+"] Errore durante il salvataggio delle informazioni relative al servizio applicativo: "+t.getMessage(),t);
						}
					}
				}catch(Exception e){
					logError(logCore, "Salvataggio informazioni sulla transazione non riuscita: "+e.getMessage(),e);
				}
				
				// Elimino Messaggio
				try{
					gestoreEliminazione.eliminaDestinatarioMessaggio(idServizioApplicativo.getNome(), gestoreEliminazione.getRiferimentoMessaggio(), dataMessaggio);
				}catch(Exception e){
					msgDiag.logErroreGenerico(e,"gestoreMessaggi.eliminaDestinatarioMessaggio("+tipoOperazione+","+idServizioApplicativo.getNome()+","+idMessaggio+")");
					throw new IntegrationManagerException(protocolFactory,ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
							get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_522_DELETE_MSG_FROM_INTEGRATION_MANAGER),idServizioApplicativo,
							IntegrationFunctionError.INTERNAL_REQUEST_ERROR, this.getErroriProperties(logCore));
				}
			}
			
			imResponseContext.setEsito(this.getEsitoTransazione(protocolFactory, imRequestContext, EsitoTransazioneName.OK));


		}catch(Exception e){
			
			try{
				imResponseContext.setEsito(this.getEsitoTransazione(protocolFactory, imRequestContext, EsitoTransazioneName.ERRORE_PROCESSAMENTO_PDD_5XX));
				
				if(e instanceof IntegrationManagerException){
					IntegrationManagerException exc = (IntegrationManagerException) e;
					if(CodiceErroreIntegrazione.CODICE_402_AUTENTICAZIONE_FALLITA.equals(exc.getCodiceErroreIntegrazione()) ||
							CodiceErroreIntegrazione.CODICE_431_GESTORE_CREDENZIALI_ERROR.equals(exc.getCodiceErroreIntegrazione())){
						imResponseContext.setEsito(this.getEsitoTransazione(protocolFactory, imRequestContext, EsitoTransazioneName.AUTENTICAZIONE_FALLITA));
					}else if(CodiceErroreIntegrazione.CODICE_406_INTEGRATION_MANAGER_MESSAGGI_FOR_SIL_NON_TROVATI.equals(exc.getCodiceErroreIntegrazione())){
						imResponseContext.setEsito(this.getEsitoTransazione(protocolFactory, imRequestContext, EsitoTransazioneName.MESSAGGI_NON_PRESENTI));
					}else if(CodiceErroreIntegrazione.CODICE_404_AUTORIZZAZIONE_FALLITA.equals(exc.getCodiceErroreIntegrazione())){
						imResponseContext.setEsito(this.getEsitoTransazione(protocolFactory, imRequestContext, EsitoTransazioneName.AUTORIZZAZIONE_FALLITA));
					}
				}
			}catch(Exception eInteral){
				logError(logCore, "Errore durante la generazione dell'esito: "+eInteral.getMessage(),eInteral);
				imResponseContext.setEsito(EsitoTransazione.ESITO_TRANSAZIONE_ERROR);
			}
			
			// Controllo Eccezioni
			if(e instanceof IntegrationManagerException){
				// Lanciata all'interno del metodo
				throw (IntegrationManagerException)e;
			}else{
				msgDiag.logErroreGenerico(e,"deleteAllMessages("+tipoOperazione+")");
				throw new IntegrationManagerException(protocolFactory,ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.getErroreIntegrazione(),idServizioApplicativo,
						IntegrationFunctionError.INTERNAL_REQUEST_ERROR, this.getErroriProperties(logCore));
			}
		}finally{
			try{
				if(stato!=null)
					stato.releaseResource();
			}catch(Exception eClose){}
			
			
			
			/* ------------  IntegrationManagerResponseHandler ------------- */
			imResponseContext.setDataCompletamentoOperazione(DateManager.getDate());
			try{
				GestoreHandlers.integrationManagerResponse(imResponseContext, msgDiag, logCore);
			}catch(Exception e){
				if(e instanceof HandlerException){
					HandlerException he = (HandlerException) e;
					if(he.isEmettiDiagnostico()){
						msgDiag.logErroreGenerico(e, ((HandlerException)e).getIdentitaHandler());
					}
				}else{
					msgDiag.logErroreGenerico(e, "IntegrationManagerResponseHandler");
				}
			}
		}

	}























	/* ------- Invocazione Porta Delegata ---------------*/

	/**
	 * Invoca una porta delegata
	 *
	 * @param portaDelegata Porta Delegata da invocare
	 * @param msg Message da utilizza come messaggio da invocare (contiene anche il servizio che si desidera invocare)
	 * @return un Message contenente il messaggio di risposta 
	 * 
	 */
	@Override
	public IntegrationManagerMessage invocaPortaDelegata(String portaDelegata,IntegrationManagerMessage msg) throws IntegrationManagerException {
		init();
		String tipoOperazione = "invocaPortaDelegata";
		return invocaPortaDelegata_engine(tipoOperazione,portaDelegata,msg,null);

	}


	/**
	 * Invoca una porta delegata per riferimento
	 *
	 * @param portaDelegata Porta Delegata da invocare
	 * @param msg Message da utilizza come messaggio da invocare (contiene anche il servizio che si desidera invocare)
	 * @param riferimentoMessaggio ID che identifica un Messaggio da utilizzare
	 * 
	 * @return un Message contenente il messaggio di risposta 
	 * 
	 */
	@Override
	public IntegrationManagerMessage invocaPortaDelegataPerRiferimento(String portaDelegata,
			IntegrationManagerMessage msg,String riferimentoMessaggio) throws IntegrationManagerException {
		init();
		String tipoOperazione = "invocaPortaDelegataPerRiferimento";
		return invocaPortaDelegata_engine(tipoOperazione,portaDelegata,msg,riferimentoMessaggio);

	}


	/**
	 * Invia una risposta asicrona a OpenSPCoop
	 * @param portaDelegata Porta Delegata da invocare
	 * @param msg Message da utilizza come messaggio da invocare (contiene anche il servizio che si desidera invocare)
	 * @return un Message contenente il messaggio di risposta 
	 * 
	 */
	@Override
	public IntegrationManagerMessage sendRispostaAsincronaSimmetrica(String portaDelegata,IntegrationManagerMessage msg) throws IntegrationManagerException {
		init();
		String tipoOperazione = "sendRispostaAsincronaSimmetrica";		
		return invocaPortaDelegata_engine(tipoOperazione,portaDelegata,msg,null);

	}

	/**
	 * Invia una risposta asicrona a OpenSPCoop
	 * @param portaDelegata Porta Delegata da invocare
	 * @param msg Message da utilizza come messaggio da invocare (contiene anche il servizio che si desidera invocare)
	 * @return un Message contenente il messaggio di risposta 
	 * 
	 */
	@Override
	public IntegrationManagerMessage sendRichiestaStatoAsincronaAsimmetrica(String portaDelegata,IntegrationManagerMessage msg) throws IntegrationManagerException {
		init();
		String tipoOperazione = "sendRichiestaStatoAsincronaAsimmetrica";
		return invocaPortaDelegata_engine(tipoOperazione,portaDelegata,msg,null);

	}


	private IntegrationManagerMessage invocaPortaDelegata_engine(String tipoOperazione, String portaDelegata, IntegrationManagerMessage msg,
			String idInvocazionePerRiferimento) throws IntegrationManagerException {

		// Timestamp
		Date dataAccettazioneRichiesta = DateManager.getDate();
		Date dataIngressoRichiesta = dataAccettazioneRichiesta;
		
		// Logger
		Logger logCore = OpenSPCoop2Logger.getLoggerOpenSPCoopCore();
		if(logCore==null)
			logCore = LoggerWrapperFactory.getLogger(IntegrationManager.ID_MODULO);
		
		// check Autorizzazione
		checkIMAuthorization(logCore);
		
		// ProtocolFactory
		IProtocolFactory<?> protocolFactory = getProtocolFactory(logCore);
		
		// Verifica risorse sistema
		this.verificaRisorseSistema(protocolFactory,logCore, tipoOperazione);
		
		// SetNomePD
		this.setNomePortaDelegata(logCore, portaDelegata);
		
		RicezioneContenutiApplicativiIntegrationManagerService service = new RicezioneContenutiApplicativiIntegrationManagerService();
		return service.process(tipoOperazione, portaDelegata, msg, idInvocazionePerRiferimento, 
				logCore, getHttpServletRequest(), getHttpServletResponse(), 
				protocolFactory, dataAccettazioneRichiesta, dataIngressoRichiesta);

	}

}
