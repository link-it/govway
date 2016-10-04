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



package org.openspcoop2.pdd.services.skeleton;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.soap.SOAPFault;

import org.slf4j.Logger;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.core.constants.TipoPdD;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.message.MessageUtils;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.OpenSPCoop2MessageParseResult;
import org.openspcoop2.message.ParseException;
import org.openspcoop2.message.SOAPVersion;
import org.openspcoop2.message.SoapUtils;
import org.openspcoop2.message.SoapUtilsBuildParameter;
import org.openspcoop2.pdd.config.ClassNameProperties;
import org.openspcoop2.pdd.config.ConfigurazionePdDManager;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.core.AbstractCore;
import org.openspcoop2.pdd.core.CostantiPdD;
import org.openspcoop2.pdd.core.GestoreMessaggi;
import org.openspcoop2.pdd.core.PdDContext;
import org.openspcoop2.pdd.core.autenticazione.Credenziali;
import org.openspcoop2.pdd.core.autenticazione.GestoreCredenzialiConfigurationException;
import org.openspcoop2.pdd.core.autenticazione.IAutenticazione;
import org.openspcoop2.pdd.core.autenticazione.IGestoreCredenzialiIM;
import org.openspcoop2.pdd.core.connettori.IConnettore;
import org.openspcoop2.pdd.core.connettori.InfoConnettoreIngresso;
import org.openspcoop2.pdd.core.connettori.RepositoryConnettori;
import org.openspcoop2.pdd.core.handlers.GestoreHandlers;
import org.openspcoop2.pdd.core.handlers.HandlerException;
import org.openspcoop2.pdd.core.handlers.IntegrationManagerRequestContext;
import org.openspcoop2.pdd.core.handlers.IntegrationManagerResponseContext;
import org.openspcoop2.pdd.core.handlers.PostOutResponseContext;
import org.openspcoop2.pdd.core.handlers.PreInRequestContext;
import org.openspcoop2.pdd.core.integrazione.HeaderIntegrazione;
import org.openspcoop2.pdd.core.state.OpenSPCoopStateful;
import org.openspcoop2.pdd.logger.Dump;
import org.openspcoop2.pdd.logger.MsgDiagnosticiProperties;
import org.openspcoop2.pdd.logger.MsgDiagnostico;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.pdd.logger.Tracciamento;
import org.openspcoop2.pdd.services.DumpRaw;
import org.openspcoop2.pdd.services.OpenSPCoop2Startup;
import org.openspcoop2.pdd.services.RicezioneContenutiApplicativi;
import org.openspcoop2.pdd.services.RicezioneContenutiApplicativiContext;
import org.openspcoop2.pdd.services.ServicesUtils;
import org.openspcoop2.pdd.services.ServletUtils;
import org.openspcoop2.pdd.services.connector.ConnectorUtils;
import org.openspcoop2.pdd.services.connector.HttpServletConnectorInMessage;
import org.openspcoop2.pdd.services.connector.HttpServletConnectorOutMessage;
import org.openspcoop2.pdd.timers.TimerMonitoraggioRisorse;
import org.openspcoop2.pdd.timers.TimerThreshold;
import org.openspcoop2.protocol.engine.LetturaParametriBusta;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.engine.URLProtocolContext;
import org.openspcoop2.protocol.engine.constants.Costanti;
import org.openspcoop2.protocol.engine.constants.IDService;
import org.openspcoop2.protocol.engine.driver.RepositoryBuste;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.builder.EsitoTransazione;
import org.openspcoop2.protocol.sdk.builder.IBustaBuilder;
import org.openspcoop2.protocol.sdk.builder.InformazioniErroriInfrastrutturali;
import org.openspcoop2.protocol.sdk.builder.ProprietaErroreApplicativo;
import org.openspcoop2.protocol.sdk.builder.ProprietaManifestAttachments;
import org.openspcoop2.protocol.sdk.constants.CodiceErroreIntegrazione;
import org.openspcoop2.protocol.sdk.constants.ErroreIntegrazione;
import org.openspcoop2.protocol.sdk.constants.ErroriIntegrazione;
import org.openspcoop2.protocol.sdk.constants.EsitoTransazioneName;
import org.openspcoop2.utils.Identity;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.id.IUniqueIdentifier;
import org.openspcoop2.utils.id.UniqueIdentifierManager;
import org.openspcoop2.utils.io.notifier.NotifierInputStreamParams;
import org.openspcoop2.utils.resources.Loader;
import org.openspcoop2.utils.resources.TransportRequestContext;

		
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

	public final static IDService ID_SERVICE = IDService.INTEGRATION_MANAGER_SOAP;
	public final static String ID_MODULO = ID_SERVICE.getValue();
	
	private OpenSPCoop2Properties propertiesReader;
	private ClassNameProperties className;


	private boolean inizializzato = false;

	/** Indicazione se il servizio PD risulta attivo */
	private static Boolean staticInitialized = false;
	public static Boolean isActiveIMService = true;
	
	private void init(){
		if(!this.inizializzato){
			this.propertiesReader = OpenSPCoop2Properties.getInstance();
			this.className = ClassNameProperties.getInstance();
			this.inizializzato = true;
			synchronized (IntegrationManager.staticInitialized) {
				if(IntegrationManager.staticInitialized==false){
					IntegrationManager.staticInitialized = true;
					IntegrationManager.isActiveIMService = ConfigurazionePdDManager.getInstance().isIMServiceActive();
				}
			}
		}
	}


	protected abstract HttpServletRequest getHttpServletRequest() throws IntegrationManagerException;
	protected abstract HttpServletResponse getHttpServletResponse() throws IntegrationManagerException;
	
	private IProtocolFactory getProtocolFactory(Logger log) throws IntegrationManagerException{
		try {
			String protocolName = (String) getHttpServletRequest().getAttribute(org.openspcoop2.core.constants.Costanti.PROTOCOLLO);
			return ProtocolFactoryManager.getInstance().getProtocolFactoryByName(protocolName);
		} catch (Exception e) {
			log.error("Errore durante il recupero della ProtocolFactory: "+e.getMessage(),e);
			throw new IntegrationManagerException(null,ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.getErroreIntegrazione());
		} 
	}
	
	private void checkIMAuthorization(Logger log) throws IntegrationManagerException{
		try {
			Object o = getHttpServletRequest().getAttribute(org.openspcoop2.core.constants.Costanti.INTEGRATION_MANAGER_ENGINE_AUTHORIZED);
			if(o == null || !(o instanceof Boolean)){
				throw new Exception("Invocazione del Servizio non autorizzata");
			}
			Boolean b = (Boolean) o;
			if(!b){
				throw new Exception("Invocazione del Servizio non autorizzata");
			}
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			throw new IntegrationManagerException(null,ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.getErroreIntegrazione());
		} 
	}
	
	
	private InfoConnettoreIngresso buildInfoConnettoreIngresso(javax.servlet.http.HttpServletRequest req,Credenziali credenziali,URLProtocolContext urlProtocolContext){
		// Informazioni connettore ingresso
		InfoConnettoreIngresso connettoreIngresso = new InfoConnettoreIngresso();
		connettoreIngresso.setCredenziali(credenziali);
		connettoreIngresso.setFromLocation(ServletUtils.getLocation(req, credenziali));
		connettoreIngresso.setUrlProtocolContext(urlProtocolContext);
		return connettoreIngresso;
	}
	
	private IntegrationManagerRequestContext buildIMRequestContext(Timestamp dataRichiestaOperazione,
			Operazione tipoOperazione, PdDContext pddContext, Logger logCore,IProtocolFactory protocolFactory) throws IntegrationManagerException, ProtocolException, UtilsException{
		
		IntegrationManagerRequestContext imRequestContext = 
			new IntegrationManagerRequestContext(dataRichiestaOperazione, tipoOperazione, pddContext, logCore,protocolFactory);
		
		// Raccolta oggetti da contesto
		Credenziali credenziali = null;
		javax.servlet.http.HttpServletRequest req = getHttpServletRequest();
		URLProtocolContext urlProtocolContext = ServletUtils.getParametriInvocazionePorta(req,logCore);
		try {
			credenziali = ServletUtils.getCredenziali(req,logCore);
			if(credenziali==null){
				credenziali = new Credenziali();
			}
		}catch(Exception e){
			credenziali = new Credenziali();
		}
		imRequestContext.setConnettore(buildInfoConnettoreIngresso(req, credenziali, urlProtocolContext));
		
		return imRequestContext;
	}

	/** IGestoreCredenziali: lista di gestori delle credenziali */
	//private java.util.Hashtable<String, IGestoreCredenzialiIM> gestoriCredenziali = null;
	// E' stato aggiunto lo stato dentro l'oggetto.
	private String [] tipiGestoriCredenziali = null;
	private synchronized void initializeGestoreCredenziali(IProtocolFactory protocolFactory,MsgDiagnostico msgDiag) throws IntegrationManagerException{
		if(this.tipiGestoriCredenziali==null){
			
			Loader loader = Loader.getInstance();
			
			//this.gestoriCredenziali = new java.util.Hashtable<String, IGestoreCredenzialiIM>();
			this.tipiGestoriCredenziali = this.propertiesReader.getTipoGestoreCredenzialiIM();
			if(this.tipiGestoriCredenziali!=null){
				for (int i = 0; i < this.tipiGestoriCredenziali.length; i++) {
					String classType = this.className.getGestoreCredenzialiIM(this.tipiGestoriCredenziali[i]);
					try {
						IGestoreCredenzialiIM gestore = (IGestoreCredenzialiIM)loader.newInstance(classType);
						gestore.toString();
						//this.gestoriCredenziali.put(this.tipiGestoriCredenziali[i],gestore);
					} catch (Exception e) {
						msgDiag.logErroreGenerico(e, "InizializzazioneGestoreCredenziali("+this.tipiGestoriCredenziali[i]+")");
						throw new IntegrationManagerException(protocolFactory,
								ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
									get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_548_GESTORE_CREDENZIALI_NON_FUNZIONANTE));
					}
				}
			}
		}
	}
	private void gestioneCredenziali(IProtocolFactory protocolFactory,MsgDiagnostico msgDiag,
			InfoConnettoreIngresso infoConnettoreIngresso,PdDContext pddContext) throws IntegrationManagerException {
		if(this.tipiGestoriCredenziali==null){
			initializeGestoreCredenziali(protocolFactory,msgDiag);
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
									get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_548_GESTORE_CREDENZIALI_NON_FUNZIONANTE));
					}
					
					if (gestore != null) {
						Credenziali credenzialiRitornate = gestore.elaborazioneCredenziali(infoConnettoreIngresso);
						if(credenzialiRitornate==null){
							throw new Exception("Credenziali non ritornate");
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
							msgDiag.logPersonalizzato("gestoreCredenziali.nuoveCredenziali");
							// update credenziali
							infoConnettoreIngresso.setCredenziali(credenzialiRitornate);	
						}
					} else {
						throw new Exception("non inizializzato");
					}
				} 
				catch (Exception e) {
					OpenSPCoop2Logger.getLoggerOpenSPCoopCore().error("Errore durante l'identificazione delle credenziali ["+ this.tipiGestoriCredenziali[i]
					         + "]: "+ e.getMessage(),e);
					msgDiag.addKeyword(CostantiPdD.KEY_TIPO_GESTORE_CREDENZIALI,this.tipiGestoriCredenziali[i]);
					msgDiag.addKeyword(CostantiPdD.KEY_ERRORE_PROCESSAMENTO, e.getMessage());
					msgDiag.logPersonalizzato("gestoreCredenziali.errore");
					ErroreIntegrazione errore = null;
					if(e instanceof GestoreCredenzialiConfigurationException){
						errore = ErroriIntegrazione.ERRORE_431_GESTORE_CREDENZIALI_ERROR.
								getErrore431_ErroreGestoreCredenziali(this.tipiGestoriCredenziali[i], e);
					}else{
						errore = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
								get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_548_GESTORE_CREDENZIALI_NON_FUNZIONANTE);
					}
					
					throw new IntegrationManagerException(protocolFactory,errore);
					
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
	private IDServizioApplicativo autenticazione(IProtocolFactory protocolFactory,MsgDiagnostico msgDiag,InfoConnettoreIngresso infoConnettoreIngresso,
			ConfigurazionePdDManager configPdDManager,PdDContext pddContext) throws IntegrationManagerException {
		
		Credenziali credenziali = infoConnettoreIngresso.getCredenziali();
		
		// Autenticazione
		String credenzialiFornite = "";
		if (credenziali.getUsername() != null || credenziali.getSubject() != null) {
			credenzialiFornite = "(";
			if (credenziali.getUsername() != null){
				if(credenziali.getPassword()==null || "".equals(credenziali.getPassword()) )
					credenzialiFornite = credenzialiFornite + " Basic Username: "+ credenziali.getUsername() + "  Basic Password: non definita";
				else
					credenzialiFornite = credenzialiFornite + " Basic Username: "+ credenziali.getUsername() + " ";
			}
			if (credenziali.getSubject() != null)
				credenzialiFornite = credenzialiFornite + " SSL Subject: "+ credenziali.getSubject() + " ";
			credenzialiFornite = credenzialiFornite + ") ";
		}
		msgDiag.addKeyword(CostantiPdD.KEY_CREDENZIALI_SA_FRUITORE, credenzialiFornite);

		IDServizioApplicativo servizio_applicativo = null;	    
		String [] tipoAutenticazione = configPdDManager.getIntegrationManagerAuthentication();
		if(tipoAutenticazione==null || tipoAutenticazione.length<1){
			msgDiag.logPersonalizzato("autenticazioneNonImpostata");
			throw new IntegrationManagerException(protocolFactory,ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
					get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_519_INTEGRATION_MANAGER_CONFIGURATION_ERROR));
		}
		for(int i=0; i<tipoAutenticazione.length; i++){
			if(CostantiConfigurazione.INVOCAZIONE_SERVIZIO_AUTENTICAZIONE_NONE.toString().equalsIgnoreCase(tipoAutenticazione[i]) == true){
				msgDiag.logPersonalizzato("autenticazioneNonImpostata");
				throw new IntegrationManagerException(protocolFactory,ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
						get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_519_INTEGRATION_MANAGER_CONFIGURATION_ERROR));
			}
		}
		
		Loader loader = Loader.getInstance();
		
		StringBuffer errori = new StringBuffer();
		for(int i=0; i<tipoAutenticazione.length; i++){
			
			// Autenticazione: Caricamento classe
			String authClass = this.className.getAutenticazione(tipoAutenticazione[i]);
			IAutenticazione auth = null;
			try{
				auth = (IAutenticazione) loader.newInstance(authClass);
				AbstractCore.init(auth, pddContext, protocolFactory);
			}catch(Exception e){
				msgDiag.logErroreGenerico(e,"Autenticazione("+tipoAutenticazione+") Class.forName("+authClass+")");
				throw new IntegrationManagerException(protocolFactory,ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
						get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_503_AUTENTICAZIONE));
			}	 
			
			// Autenticazione: Controllo
			if(auth.process(infoConnettoreIngresso,null)==false){
				if(errori.length()>0)
					errori.append("\n");
				try{
					errori.append("(Autenticazione " +tipoAutenticazione[i]+") "+ auth.getErrore().getDescrizione(protocolFactory));
				}catch(Exception e){
					OpenSPCoop2Logger.getLoggerOpenSPCoopCore().error("Errore durante la comprensione dell'errore: "+e.getMessage(),e);
					throw new IntegrationManagerException(protocolFactory,ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
							get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_519_INTEGRATION_MANAGER_CONFIGURATION_ERROR));
				}
			}
			else{
				servizio_applicativo = auth.getServizioApplicativo();
				break; //sa individuato
			}
		}
		
		if(servizio_applicativo == null){
			if(errori.length()>0){
				msgDiag.addKeyword(CostantiPdD.KEY_ERRORE_PROCESSAMENTO, errori.toString());
				msgDiag.logPersonalizzato("servizioApplicativo.identificazioneTramiteCredenziali");
				throw new IntegrationManagerException(protocolFactory,ErroriIntegrazione.ERRORE_402_AUTENTICAZIONE_FALLITA.
						getErrore402_AutenticazioneFallita(errori.toString()));
			}
			else{
				msgDiag.addKeyword(CostantiPdD.KEY_ERRORE_PROCESSAMENTO, "servizio applicativo non autenticato");
				msgDiag.logPersonalizzato("servizioApplicativo.identificazioneTramiteCredenziali");
				throw new IntegrationManagerException(protocolFactory,ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
						get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_503_AUTENTICAZIONE));
			}
		}
		
		msgDiag.addKeyword(CostantiPdD.KEY_SA_FRUITORE, servizio_applicativo.getNome());
		msgDiag.addKeyword(CostantiPdD.KEY_SA_EROGATORE, servizio_applicativo.getNome());
		msgDiag.setServizioApplicativo(servizio_applicativo.getNome());
		if(servizio_applicativo.getIdSoggettoProprietario()!=null){
			msgDiag.addKeyword(CostantiPdD.KEY_TIPO_MITTENTE_BUSTA_RICHIESTA, servizio_applicativo.getIdSoggettoProprietario().getTipo());
			msgDiag.addKeyword(CostantiPdD.KEY_MITTENTE_BUSTA_RICHIESTA, servizio_applicativo.getIdSoggettoProprietario().getNome());
			msgDiag.setFruitore(servizio_applicativo.getIdSoggettoProprietario());
		}
		return  servizio_applicativo;
	}


	/* ----------- Init -------------- */
	private void verificaRisorseSistema(IProtocolFactory protocolFactory,Logger logCore,String tipoOperazione) throws IntegrationManagerException {
		
		if( OpenSPCoop2Startup.initialize == false){
			logCore.error("["+IntegrationManager.ID_MODULO+"]["+tipoOperazione+"] Inizializzazione di OpenSPCoop non correttamente effettuata");
			throw new IntegrationManagerException(protocolFactory,ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
					get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_501_PDD_NON_INIZIALIZZATA));
		}
		if( TimerMonitoraggioRisorse.risorseDisponibili == false){
			logCore.error("["+IntegrationManager.ID_MODULO+"]["+tipoOperazione+"] Risorse di sistema non disponibili: "+TimerMonitoraggioRisorse.risorsaNonDisponibile.getMessage(),TimerMonitoraggioRisorse.risorsaNonDisponibile);
			throw new IntegrationManagerException(protocolFactory,ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
					get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_532_RISORSE_NON_DISPONIBILI));
		}
		if (TimerThreshold.freeSpace == false) {
			logCore.error("["+IntegrationManager.ID_MODULO+"]["+tipoOperazione+"] Non sono disponibili abbastanza risorse per la gestione della richiesta");
			throw new IntegrationManagerException(protocolFactory,ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
					get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_533_RISORSE_DISPONIBILI_LIVELLO_CRITICO));
		}
		if( Tracciamento.tracciamentoDisponibile == false){
			logCore.error("["+IntegrationManager.ID_MODULO+"]["+tipoOperazione+"] Tracciatura non disponibile: "+Tracciamento.motivoMalfunzionamentoTracciamento.getMessage(),Tracciamento.motivoMalfunzionamentoTracciamento);
			throw new IntegrationManagerException(protocolFactory,ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
					get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_545_TRACCIATURA_NON_FUNZIONANTE));
		}
		if( MsgDiagnostico.gestoreDiagnosticaDisponibile == false){
			logCore.error("["+IntegrationManager.ID_MODULO+"]["+tipoOperazione+"] Sistema di diagnostica non disponibile: "+MsgDiagnostico.motivoMalfunzionamentoDiagnostici.getMessage(),MsgDiagnostico.motivoMalfunzionamentoDiagnostici);
			throw new IntegrationManagerException(protocolFactory,ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
					get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_546_DIAGNOSTICA_NON_FUNZIONANTE));
		}
		if( Dump.sistemaDumpDisponibile == false){
			logCore.error("["+IntegrationManager.ID_MODULO+"]["+tipoOperazione+"] Sistema di dump dei contenuti applicativi non disponibile: "+Dump.motivoMalfunzionamentoDump.getMessage(),Dump.motivoMalfunzionamentoDump);
			throw new IntegrationManagerException(protocolFactory,ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
					get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_547_DUMP_CONTENUTI_APPLICATIVI_NON_FUNZIONANTE));
		}
		// Check Configurazione (XML)
		try{
			ConfigurazionePdDManager.getInstance().verificaConsistenzaConfigurazione();
		}catch(Exception e){
			logCore.error("["+IntegrationManager.ID_MODULO+"]["+tipoOperazione+"] Riscontrato errore durante la verifica della consistenza della configurazione PdD",e);
			throw new IntegrationManagerException(protocolFactory,ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
					get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_536_CONFIGURAZIONE_NON_DISPONIBILE));
		}
		if( IntegrationManager.isActiveIMService == false){
			logCore.error("["+IntegrationManager.ID_MODULO+"]["+tipoOperazione+"] Servizio IntegrationManager disabilitato");
			throw new IntegrationManagerException(protocolFactory,ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
					get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_552_IM_SERVICE_NOT_ACTIVE));
		}
	}
	
	/* -------- Utility -------------- */
	private IUniqueIdentifier getUniqueIdentifier(IProtocolFactory protocolFactory,MsgDiagnostico msgDiag,String tipoOperazione) throws IntegrationManagerException {
		try{
			return UniqueIdentifierManager.newUniqueIdentifier();
		}catch(Exception e){
			msgDiag.logErroreGenerico(e,"getAllMessagesId("+tipoOperazione+").getUniqueIdentifier()");
			throw new IntegrationManagerException(protocolFactory,ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.getErroreIntegrazione());
		}
	}
	private MsgDiagnostico getMsgDiagnostico(){
		MsgDiagnostico msgDiag = new MsgDiagnostico(IntegrationManager.ID_MODULO);
		msgDiag.setPrefixMsgPersonalizzati(MsgDiagnosticiProperties.MSG_DIAG_INTEGRATION_MANAGER);
		return msgDiag;
	}
	
	private EsitoTransazione getEsitoTransazione(IProtocolFactory pf,IntegrationManagerRequestContext imRequestContext, EsitoTransazioneName name) throws ProtocolException{
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
	private List<String> getAllMessagesId_engine(Operazione tipoOperazione,String tipoServizio,String servizio,String azione,int counter, int offset) throws IntegrationManagerException {

		// Timestamp
		Timestamp dataRichiestaOperazione = DateManager.getTimestamp();
		
		// Logger
		Logger logCore = OpenSPCoop2Logger.getLoggerOpenSPCoopCore();
		if(logCore==null)
			logCore = LoggerWrapperFactory.getLogger(IntegrationManager.ID_MODULO);
		
		// check Autorizzazione
		checkIMAuthorization(logCore);
		
		// ProtocolFactoyr
		IProtocolFactory protocolFactory = getProtocolFactory(logCore);
		
		// Verifica risorse sistema
		this.verificaRisorseSistema(protocolFactory,logCore, tipoOperazione.toString());

		MsgDiagnostico msgDiag = getMsgDiagnostico();
		msgDiag.addKeyword(CostantiPdD.KEY_TIPO_OPERAZIONE_IM, tipoOperazione.toString());
		
		IDServizioApplicativo id_servizio_applicativo = null;
		String servizio_applicativo = null;
		OpenSPCoopStateful stato = null;
		
		// PddContext
		PdDContext pddContext = new PdDContext();
		String idTransazione = this.getUniqueIdentifier(protocolFactory,msgDiag,tipoOperazione.toString()).getAsString();
		pddContext.addObject(org.openspcoop2.core.constants.Costanti.CLUSTER_ID, idTransazione);
		pddContext.addObject(org.openspcoop2.core.constants.Costanti.PROTOCOLLO, protocolFactory.getProtocol());
		IDServizio idServizio = new IDServizio();
		idServizio.setTipoServizio(tipoServizio);
		idServizio.setServizio(servizio);
		idServizio.setAzione(azione);
		pddContext.addObject(org.openspcoop2.core.constants.Costanti.ID_SERVIZIO,idServizio);
		msgDiag.setPddContext(pddContext, protocolFactory);
		
		
		
		/* ------------  IntegrationManagerRequesCHandler ------------- */
		IntegrationManagerRequestContext imRequestContext = null;
		try {
			imRequestContext = buildIMRequestContext(dataRichiestaOperazione, tipoOperazione, pddContext,logCore,protocolFactory);
			GestoreHandlers.integrationManagerRequest(imRequestContext, msgDiag, logCore);
		} catch(Exception e) {
			ErroreIntegrazione erroreIntegrazione = null;
			if(e instanceof HandlerException){
				HandlerException he = (HandlerException) e;
				if(he.isEmettiDiagnostico()){
					msgDiag.logErroreGenerico(e, ((HandlerException)e).getIdentitaHandler());
				}
				if(he.isSetErrorMessageInFault()){
					erroreIntegrazione = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.get5XX_ErroreProcessamento(e.getMessage());
				}
			} else {
				msgDiag.logErroreGenerico(e, "IntegrationManagerRequestHandler");
			} 
			if(erroreIntegrazione==null){
				erroreIntegrazione = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.getErroreIntegrazione();
			}
			throw new IntegrationManagerException(protocolFactory,erroreIntegrazione);
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
			stato.initResource(this.propertiesReader.getIdentitaPortaDefault(protocolFactory.getProtocol()),IntegrationManager.ID_MODULO, idTransazione);
			ConfigurazionePdDManager configPdDManager = ConfigurazionePdDManager.getInstance(stato.getStatoRichiesta(),stato.getStatoRisposta());
			msgDiag.updateState(stato.getStatoRichiesta(),stato.getStatoRisposta());
			
			// gestione credenziali
			this.gestioneCredenziali(protocolFactory,msgDiag, imRequestContext.getConnettore(),pddContext);
						
			// Autenticazione Servizio Applicativo
			id_servizio_applicativo = autenticazione(protocolFactory,msgDiag,imRequestContext.getConnettore(),configPdDManager,pddContext);
			servizio_applicativo = id_servizio_applicativo.getNome();
			imResponseContext.setServizioApplicativo(id_servizio_applicativo);
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
			String param = "ServizioApplicativo["+servizio_applicativo+"]"+tipoServizioLog+servizioLog+azioneLog+counterLog;
			msgDiag.addKeyword(CostantiPdD.KEY_PARAMETRI_OPERAZIONE_IM, param);
			msgDiag.logPersonalizzato("logInvocazioneOperazione");

			// Ricerca Messaggi
			Vector<String> ids = null;

			GestoreMessaggi gestoreMessaggi = new GestoreMessaggi(stato, true, msgDiag,pddContext);

			if(counter<0 && offset<0){
				ids = gestoreMessaggi.getIDMessaggi_ServizioApplicativo(servizio_applicativo,tipoServizio,servizio,azione);
			}else if(offset<0){
				ids = gestoreMessaggi.getIDMessaggi_ServizioApplicativo(servizio_applicativo,tipoServizio,servizio,azione,counter);
			}else{
				ids = gestoreMessaggi.getIDMessaggi_ServizioApplicativo(servizio_applicativo,tipoServizio,servizio,azione,counter,offset);
			}
			if(ids.size() == 0){
				msgDiag.logPersonalizzato("messaggiNonPresenti");
				throw new IntegrationManagerException(protocolFactory,ErroriIntegrazione.ERRORE_406_INTEGRATION_MANAGER_MESSAGGI_FOR_SIL_NON_TROVATI.
						getErroreIntegrazione(),servizio_applicativo);
			}
			List<String> listResponse = new ArrayList<String>();
			if(ids!=null && ids.size()>0){
				listResponse.addAll(ids);
			}

			imResponseContext.setEsito(this.getEsitoTransazione(protocolFactory, imRequestContext, EsitoTransazioneName.OK));
			
			return listResponse;

		}catch(Exception e){
			
			try{
				imResponseContext.setEsito(this.getEsitoTransazione(protocolFactory, imRequestContext, EsitoTransazioneName.ERRORE_GENERICO));
	
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
				logCore.error("Errore durante la generazione dell'esito: "+eInteral.getMessage(),eInteral);
				imResponseContext.setEsito(EsitoTransazione.ESITO_TRANSAZIONE_ERROR);
			}
			
			// Controllo Eccezioni
			if(e instanceof IntegrationManagerException){
				// Lanciata all'interno del metodo
				throw (IntegrationManagerException)e;
			}else{
				msgDiag.logErroreGenerico(e,"getAllMessagesId("+tipoOperazione+")");
				throw new IntegrationManagerException(protocolFactory,ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.getErroreIntegrazione(),servizio_applicativo);
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
		return getAllMessagesId_engine(Operazione.getAllMessagesId,null,null,null,-1,-1);
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
		return getAllMessagesId_engine(Operazione.getAllMessagesIdByService,tipoServizio,servizio,azione,-1,-1);
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
		return getAllMessagesId_engine(Operazione.getNextMessagesId,null,null,null,counter,-1);	
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
		return getAllMessagesId_engine(Operazione.getNextMessagesIdByService,tipoServizio,servizio,azione,counter,-1);	
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
		return getAllMessagesId_engine(Operazione.getMessagesIdArray,null,null,null,counter,offset);	
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
		return getAllMessagesId_engine(Operazione.getMessagesIdArrayByService,tipoServizio,servizio,azione,counter,offset);	
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
	private IntegrationManagerMessage getMessage_engine(Operazione tipoOperazione, String idMessaggio, boolean isRiferimentoMessaggio) throws IntegrationManagerException {

		// Timestamp
		Timestamp dataRichiestaOperazione = DateManager.getTimestamp();
		
		// Logger
		Logger logCore = OpenSPCoop2Logger.getLoggerOpenSPCoopCore();
		if(logCore==null)
			logCore = LoggerWrapperFactory.getLogger(IntegrationManager.ID_MODULO);

		// check Autorizzazione
		checkIMAuthorization(logCore);
		
		// ProtocolFactoyr
		IProtocolFactory protocolFactory = getProtocolFactory(logCore);
		
		// Verifica risorse sistema
		this.verificaRisorseSistema(protocolFactory,logCore, tipoOperazione.toString());

		MsgDiagnostico msgDiag = getMsgDiagnostico();
		msgDiag.addKeyword(CostantiPdD.KEY_TIPO_OPERAZIONE_IM, tipoOperazione.toString());

		if(isRiferimentoMessaggio){
			msgDiag.addKeyword(CostantiPdD.KEY_RIFERIMENTO_MESSAGGIO_RICHIESTA, idMessaggio);
			msgDiag.addKeyword(CostantiPdD.KEY_RIFERIMENTO_MESSAGGIO_RISPOSTA, idMessaggio);
		}else{
			msgDiag.addKeyword(CostantiPdD.KEY_ID_MESSAGGIO_RICHIESTA, idMessaggio);
			msgDiag.addKeyword(CostantiPdD.KEY_ID_MESSAGGIO_RISPOSTA, idMessaggio);
		}
		
		IDServizioApplicativo id_servizio_applicativo = null;
		String servizio_applicativo = null;
		OpenSPCoopStateful stato = null;
		// PddContext
		PdDContext pddContext = new PdDContext();
		String idTransazione = this.getUniqueIdentifier(protocolFactory,msgDiag,tipoOperazione.toString()).getAsString();
		pddContext.addObject(org.openspcoop2.core.constants.Costanti.CLUSTER_ID, idTransazione);
		pddContext.addObject(org.openspcoop2.core.constants.Costanti.ID_MESSAGGIO,idMessaggio);
		pddContext.addObject(org.openspcoop2.core.constants.Costanti.PROTOCOLLO,protocolFactory.getProtocol());
		msgDiag.setPddContext(pddContext, protocolFactory);
		
		
		/* ------------  IntegrationManagerRequesCHandler ------------- */
		IntegrationManagerRequestContext imRequestContext = null;
		try{
			imRequestContext = buildIMRequestContext(dataRichiestaOperazione, tipoOperazione, pddContext,logCore,protocolFactory);
			imRequestContext.setIdMessaggio(idMessaggio);
			GestoreHandlers.integrationManagerRequest(imRequestContext, msgDiag, logCore);
		}catch(Exception e){
			ErroreIntegrazione erroreIntegrazione = null;
			if(e instanceof HandlerException){
				HandlerException he = (HandlerException) e;
				if(he.isEmettiDiagnostico()){
					msgDiag.logErroreGenerico(e, ((HandlerException)e).getIdentitaHandler());
				}
				if(he.isSetErrorMessageInFault()){
					erroreIntegrazione = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.get5XX_ErroreProcessamento(e.getMessage());
				}
			}else{
				msgDiag.logErroreGenerico(e, "IntegrationManagerRequestHandler");
			}
			if(erroreIntegrazione==null){
				erroreIntegrazione = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.getErroreIntegrazione();
			}
			throw new IntegrationManagerException(protocolFactory,erroreIntegrazione);
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
			stato.initResource(this.propertiesReader.getIdentitaPortaDefault(protocolFactory.getProtocol()),IntegrationManager.ID_MODULO, idTransazione);
			ConfigurazionePdDManager configPdDManager = ConfigurazionePdDManager.getInstance(stato.getStatoRichiesta(),stato.getStatoRisposta());
			msgDiag.updateState(stato.getStatoRichiesta(),stato.getStatoRisposta());
			
			// gestione credenziali
			this.gestioneCredenziali(protocolFactory,msgDiag, imRequestContext.getConnettore(),pddContext);
			
			// Autenticazione Servizio Applicativo
			id_servizio_applicativo = autenticazione(protocolFactory,msgDiag,imRequestContext.getConnettore(),configPdDManager,pddContext);
			servizio_applicativo = id_servizio_applicativo.getNome();
			imResponseContext.setServizioApplicativo(id_servizio_applicativo);
			String param = "ServizioApplicativo["+servizio_applicativo+"] ID["+idMessaggio+"]";
			msgDiag.addKeyword(CostantiPdD.KEY_PARAMETRI_OPERAZIONE_IM, param);
			msgDiag.logPersonalizzato("logInvocazioneOperazione");


			// Check Esistenza Messaggio

			GestoreMessaggi gestoreMessaggi = new GestoreMessaggi(stato, true, idMessaggio, Costanti.INBOX, msgDiag, pddContext);
			if(gestoreMessaggi.existsMessageForSIL(servizio_applicativo,isRiferimentoMessaggio) == false){
				msgDiag.addKeyword(CostantiPdD.KEY_ID_MESSAGGIO_TRANSACTION_MANAGER, idMessaggio);
				msgDiag.logPersonalizzato("messaggioNonTrovato");
				throw new IntegrationManagerException(protocolFactory,ErroriIntegrazione.ERRORE_407_INTEGRATION_MANAGER_MSG_RICHIESTO_NON_TROVATO.
						getErroreIntegrazione(),servizio_applicativo);
			}

			// Check Autorizzazione all'utilizzo di IntegrationManager
			boolean authorized = gestoreMessaggi.checkAutorizzazione(servizio_applicativo,isRiferimentoMessaggio);
			if(authorized==false){
				msgDiag.logPersonalizzato("servizioApplicativo.nonAutorizzato");
				throw new IntegrationManagerException(protocolFactory,ErroriIntegrazione.ERRORE_404_AUTORIZZAZIONE_FALLITA.getErrore404_AutorizzazioneFallita(servizio_applicativo),
						servizio_applicativo);
			}

			// GetIDMessaggio Richiesto
			String idMessaggioRichiesto = idMessaggio;
			try{
				if(isRiferimentoMessaggio)
					idMessaggioRichiesto = gestoreMessaggi.mapRiferimentoIntoIDBusta();
			}catch(Exception e){
				msgDiag.addKeyword(CostantiPdD.KEY_ERRORE_PROCESSAMENTO, e.getMessage());
				msgDiag.logPersonalizzato("mappingRifMsgToId.nonRiuscito");
				throw new IntegrationManagerException(protocolFactory,ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
						get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_520_READ_MSG_FROM_INTEGRATION_MANAGER),servizio_applicativo);
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
				busta = repository.getSomeValuesFromInBox(idMessaggioRichiesto,parametri);
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
					idServizio = new IDServizio(busta.getTipoDestinatario(),busta.getDestinatario(),
							busta.getTipoServizio(),busta.getServizio());
				}
			}catch(Exception e){
				msgDiag.logErroreGenerico(e,"ReadInformazioniProtocollo("+tipoOperazione+","+idMessaggioRichiesto+")");
				if(e.getMessage()==null || (e.getMessage().indexOf("Busta non trovata")<0) ){
					throw new IntegrationManagerException(protocolFactory,ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
							get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_520_READ_MSG_FROM_INTEGRATION_MANAGER),servizio_applicativo);
				}// else
				//{
				//busta non presente, il msg puo' cmq essere consumato
				//}
			}


			// Indicazione se il messaggio deve essere sbustato
			boolean sbustamento_soap = gestoreMessaggi.sbustamentoSOAP(servizio_applicativo,isRiferimentoMessaggio);
			boolean sbustamento_informazioni_protocollo =
				gestoreMessaggi.sbustamentoInformazioniProtocollo(servizio_applicativo,isRiferimentoMessaggio);

			// Messaggio
			OpenSPCoop2Message consegnaMessage = null;
			try{
				consegnaMessage = gestoreMessaggi.getMessage(isRiferimentoMessaggio);	
			}catch(Exception e){
				msgDiag.logErroreGenerico(e,"gestoreMessaggi.getMessage("+isRiferimentoMessaggio+","+tipoOperazione+")");
				throw new IntegrationManagerException(protocolFactory,ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
						get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_520_READ_MSG_FROM_INTEGRATION_MANAGER),servizio_applicativo);
			}
			
			// TipoMessaggio
			boolean isRichiesta = true;
			try{
				GestoreMessaggi gestoreMessaggiComprensione = new GestoreMessaggi(stato, true, idMessaggioRichiesto, Costanti.INBOX, msgDiag, pddContext);
				isRichiesta = (gestoreMessaggiComprensione.getRiferimentoMessaggio()==null);
			}catch(Exception e){
				logCore.error("Comprensione tipo messaggio non riuscita: "+e.getMessage(),e);
			}

			// Eventuale sbustamento
			if(sbustamento_informazioni_protocollo){
				try{
					IBustaBuilder bustaBuilder = protocolFactory.createBustaBuilder();
					// attachments non gestiti!
					ProprietaManifestAttachments proprietaManifest = this.propertiesReader.getProprietaManifestAttachments("standard");
					proprietaManifest.setGestioneManifest(false);
					bustaBuilder.sbustamento(stato.getStatoRichiesta(),consegnaMessage, busta, isRichiesta, proprietaManifest);
				}catch(Exception e){
					msgDiag.logErroreGenerico(e,"gestoreMessaggi.getMessage("+isRiferimentoMessaggio+","+tipoOperazione+")");
					throw new IntegrationManagerException(protocolFactory,ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
							get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_520_READ_MSG_FROM_INTEGRATION_MANAGER),servizio_applicativo);				
				}
			}
			
			//	dump applicativo
			if(ConfigurazionePdDManager.getInstance(stato.getStatoRichiesta(),stato.getStatoRisposta()).dumpMessaggi()){
				msgDiag.mediumDebug("Dump applicativo messaggio ritornato...");
				Dump dumpApplicativo = new Dump(this.propertiesReader.getIdentitaPortaDefault(protocolFactory.getProtocol()),IntegrationManager.ID_MODULO,idMessaggio,
						fruitore,idServizio,TipoPdD.INTEGRATION_MANAGER,pddContext,
						stato.getStatoRichiesta(),stato.getStatoRisposta());
				dumpApplicativo.dumpIntegrationManagerGetMessage(consegnaMessage);
			}

			// Costruzione Message da ritornare
			IntegrationManagerMessage msgReturn = null;
			try{
				if(sbustamento_soap==false){
					msgReturn = new IntegrationManagerMessage(consegnaMessage,false,protocolHeaderInfo);
				}else{
					byte [] sbustato = SoapUtils.sbustamentoMessaggio(consegnaMessage);
					msgReturn = new IntegrationManagerMessage(sbustato,false,protocolHeaderInfo);
				}
			}catch(Exception e){
				msgDiag.logErroreGenerico(e,"buildMsgReturn("+idMessaggio+","+tipoOperazione+")");
				throw new IntegrationManagerException(protocolFactory,ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
						get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_523_CREAZIONE_PROTOCOL_MESSAGE),servizio_applicativo);
			}

			imResponseContext.setEsito(this.getEsitoTransazione(protocolFactory, imRequestContext, EsitoTransazioneName.OK));
			imResponseContext.setDimensioneMessaggioBytes(new Long(msgReturn.getMessage().length));
			
			return msgReturn;

		}catch(Exception e){
			
			try{
				imResponseContext.setEsito(this.getEsitoTransazione(protocolFactory, imRequestContext, EsitoTransazioneName.ERRORE_GENERICO));
				
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
				logCore.error("Errore durante la generazione dell'esito: "+eInteral.getMessage(),eInteral);
				imResponseContext.setEsito(EsitoTransazione.ESITO_TRANSAZIONE_ERROR);
			}
			
			// Controllo Eccezioni
			if(e instanceof IntegrationManagerException){
				// Lanciata all'interno del metodo
				throw (IntegrationManagerException)e;
			}else{	
				msgDiag.logErroreGenerico(e,"getMessage("+tipoOperazione+")");
				throw new IntegrationManagerException(protocolFactory,ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.getErroreIntegrazione(),servizio_applicativo);
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
	private void deleteMessage_engine(Operazione tipoOperazione,String idMessaggio,boolean isRiferimentoMessaggio) throws IntegrationManagerException {

		// Timestamp
		Timestamp dataRichiestaOperazione = DateManager.getTimestamp();
		
		// Logger
		Logger logCore = OpenSPCoop2Logger.getLoggerOpenSPCoopCore();
		if(logCore==null)
			logCore = LoggerWrapperFactory.getLogger(IntegrationManager.ID_MODULO);

		// check Autorizzazione
		checkIMAuthorization(logCore);
		
		// ProtocolFactoyr
		IProtocolFactory protocolFactory = getProtocolFactory(logCore);
		
		// Verifica risorse sistema
		this.verificaRisorseSistema(protocolFactory,logCore, tipoOperazione.toString());

		MsgDiagnostico msgDiag = getMsgDiagnostico();
		msgDiag.addKeyword(CostantiPdD.KEY_TIPO_OPERAZIONE_IM, tipoOperazione.toString());
		
		if(isRiferimentoMessaggio){
			msgDiag.addKeyword(CostantiPdD.KEY_RIFERIMENTO_MESSAGGIO_RICHIESTA, idMessaggio);
			msgDiag.addKeyword(CostantiPdD.KEY_RIFERIMENTO_MESSAGGIO_RISPOSTA, idMessaggio);
		}else{
			msgDiag.addKeyword(CostantiPdD.KEY_ID_MESSAGGIO_RICHIESTA, idMessaggio);
			msgDiag.addKeyword(CostantiPdD.KEY_ID_MESSAGGIO_RISPOSTA, idMessaggio);
		}
		
		GestoreMessaggi gestoreMessaggi = null;
		IDServizioApplicativo id_servizio_applicativo = null;
		String servizio_applicativo = null;
		OpenSPCoopStateful stato = null;
		// PddContext
		PdDContext pddContext = new PdDContext();
		String idTransazione = this.getUniqueIdentifier(protocolFactory,msgDiag,tipoOperazione.toString()).getAsString();
		pddContext.addObject(org.openspcoop2.core.constants.Costanti.CLUSTER_ID, idTransazione);
		pddContext.addObject(org.openspcoop2.core.constants.Costanti.ID_MESSAGGIO,idMessaggio);
		pddContext.addObject(org.openspcoop2.core.constants.Costanti.PROTOCOLLO, protocolFactory.getProtocol());
		msgDiag.setPddContext(pddContext, protocolFactory);
		
		
		
		/* ------------  IntegrationManagerRequesCHandler ------------- */
		IntegrationManagerRequestContext imRequestContext = null;
		try{
			imRequestContext = buildIMRequestContext(dataRichiestaOperazione, tipoOperazione, pddContext,logCore,protocolFactory);
			imRequestContext.setIdMessaggio(idMessaggio);
			GestoreHandlers.integrationManagerRequest(imRequestContext, msgDiag, logCore);
		}catch(Exception e){
			ErroreIntegrazione erroreIntegrazione = null;
			if(e instanceof HandlerException){
				HandlerException he = (HandlerException) e;
				if(he.isEmettiDiagnostico()){
					msgDiag.logErroreGenerico(e, ((HandlerException)e).getIdentitaHandler());
				}
				if(he.isSetErrorMessageInFault()){
					erroreIntegrazione = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.get5XX_ErroreProcessamento(e.getMessage());
				}
			}else{
				msgDiag.logErroreGenerico(e, "IntegrationManagerRequestHandler");
			}
			if(erroreIntegrazione==null){
				erroreIntegrazione = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.getErroreIntegrazione();
			}
			throw new IntegrationManagerException(protocolFactory,erroreIntegrazione);
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
			stato.initResource(this.propertiesReader.getIdentitaPortaDefault(protocolFactory.getProtocol()),IntegrationManager.ID_MODULO, idTransazione);
			ConfigurazionePdDManager configPdDManager = ConfigurazionePdDManager.getInstance(stato.getStatoRichiesta(),stato.getStatoRisposta());
			msgDiag.updateState(stato.getStatoRichiesta(),stato.getStatoRisposta());	
			
			// gestione credenziali
			this.gestioneCredenziali(protocolFactory,msgDiag, imRequestContext.getConnettore(),pddContext);
			
			// Autenticazione Servizio Applicativo
			id_servizio_applicativo = autenticazione(protocolFactory,msgDiag,imRequestContext.getConnettore(),configPdDManager,pddContext);
			servizio_applicativo = id_servizio_applicativo.getNome();
			imResponseContext.setServizioApplicativo(id_servizio_applicativo);
			String param = "ServizioApplicativo["+servizio_applicativo+"] ID["+idMessaggio+"]";
			msgDiag.addKeyword(CostantiPdD.KEY_PARAMETRI_OPERAZIONE_IM, param);
			msgDiag.logPersonalizzato("logInvocazioneOperazione");

			// Check Esistenza Messaggio
			gestoreMessaggi = new GestoreMessaggi(stato, true, idMessaggio, Costanti.INBOX, msgDiag, pddContext);

			if(gestoreMessaggi.existsMessageForSIL(servizio_applicativo,isRiferimentoMessaggio) == false){
				msgDiag.addKeyword(CostantiPdD.KEY_ID_MESSAGGIO_TRANSACTION_MANAGER, idMessaggio);
				msgDiag.logPersonalizzato("messaggioNonTrovato");
				throw new IntegrationManagerException(protocolFactory,ErroriIntegrazione.ERRORE_407_INTEGRATION_MANAGER_MSG_RICHIESTO_NON_TROVATO.
						getErroreIntegrazione(),servizio_applicativo);
			}

			// Check Autorizzazione all'utilizzo di IntegrationManager
			boolean authorized = gestoreMessaggi.checkAutorizzazione(servizio_applicativo,isRiferimentoMessaggio);
			if(authorized==false){
				msgDiag.logPersonalizzato("servizioApplicativo.nonAutorizzato");
				throw new IntegrationManagerException(protocolFactory,ErroriIntegrazione.ERRORE_404_AUTORIZZAZIONE_FALLITA.getErrore404_AutorizzazioneFallita(servizio_applicativo),
						servizio_applicativo);
			}

			//	GetIDMessaggio Richiesto
			String idMessaggioRichiesto = idMessaggio;
			try{
				if(isRiferimentoMessaggio)
					idMessaggioRichiesto = gestoreMessaggi.mapRiferimentoIntoIDBusta();
			}catch(Exception e){
				msgDiag.addKeyword(CostantiPdD.KEY_ERRORE_PROCESSAMENTO, e.getMessage());
				msgDiag.logPersonalizzato("mappingRifMsgToId.nonRiuscito");
				throw new IntegrationManagerException(protocolFactory,ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
						get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_520_READ_MSG_FROM_INTEGRATION_MANAGER),servizio_applicativo);
			}

			//	Elimino Messaggio
			try{
				GestoreMessaggi gestoreEliminazione = new GestoreMessaggi(stato, true, idMessaggioRichiesto,Costanti.INBOX,msgDiag,pddContext);
				gestoreEliminazione.eliminaDestinatarioMessaggio(servizio_applicativo, gestoreEliminazione.getRiferimentoMessaggio());
			}catch(Exception e){
				msgDiag.logErroreGenerico(e,"gestoreMessaggi.eliminaDestinatarioMessaggio("+tipoOperazione+","+servizio_applicativo+","+idMessaggio+")");
				throw new IntegrationManagerException(protocolFactory,ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
						get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_522_DELETE_MSG_FROM_INTEGRATION_MANAGER),servizio_applicativo);
			}

			imResponseContext.setEsito(this.getEsitoTransazione(protocolFactory, imRequestContext, EsitoTransazioneName.OK));
			
		}catch(Exception e){
			
			try{
				imResponseContext.setEsito(this.getEsitoTransazione(protocolFactory, imRequestContext, EsitoTransazioneName.ERRORE_GENERICO));
				
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
				logCore.error("Errore durante la generazione dell'esito: "+eInteral.getMessage(),eInteral);
				imResponseContext.setEsito(EsitoTransazione.ESITO_TRANSAZIONE_ERROR);
			}
			
			// Controllo Eccezioni
			if(e instanceof IntegrationManagerException){
				// Lanciata all'interno del metodo
				throw (IntegrationManagerException)e;
			}else{
				msgDiag.logErroreGenerico(e,"deleteMessage("+tipoOperazione+")");
				throw new IntegrationManagerException(protocolFactory,ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.getErroreIntegrazione(),servizio_applicativo);
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
		Timestamp dataRichiestaOperazione = DateManager.getTimestamp();
		
		// Logger
		Logger logCore = OpenSPCoop2Logger.getLoggerOpenSPCoopCore();
		if(logCore==null)
			logCore = LoggerWrapperFactory.getLogger(IntegrationManager.ID_MODULO);

		// check Autorizzazione
		checkIMAuthorization(logCore);
		
		// ProtocolFactoyr
		IProtocolFactory protocolFactory = getProtocolFactory(logCore);
		
		// Verifica risorse sistema
		this.verificaRisorseSistema(protocolFactory,logCore, tipoOperazione.toString());

		MsgDiagnostico msgDiag = getMsgDiagnostico();
		msgDiag.addKeyword(CostantiPdD.KEY_TIPO_OPERAZIONE_IM, tipoOperazione.toString());
		
		IDServizioApplicativo id_servizio_applicativo = null;
		String servizio_applicativo = null;
		OpenSPCoopStateful stato = null;
		// PddContext
		PdDContext pddContext = new PdDContext();
		String idTransazione = this.getUniqueIdentifier(protocolFactory,msgDiag,tipoOperazione.toString()).getAsString();
		pddContext.addObject(org.openspcoop2.core.constants.Costanti.CLUSTER_ID, idTransazione);
		pddContext.addObject(org.openspcoop2.core.constants.Costanti.PROTOCOLLO, protocolFactory.getProtocol());
		msgDiag.setPddContext(pddContext, protocolFactory);
		
		
		
		
		/* ------------  IntegrationManagerRequesCHandler ------------- */
		IntegrationManagerRequestContext imRequestContext = null;
		try{
			imRequestContext = buildIMRequestContext(dataRichiestaOperazione, tipoOperazione, pddContext,logCore,protocolFactory);
			GestoreHandlers.integrationManagerRequest(imRequestContext, msgDiag, logCore);
		}catch(Exception e){
			ErroreIntegrazione erroreIntegrazione = null;
			if(e instanceof HandlerException){
				HandlerException he = (HandlerException) e;
				if(he.isEmettiDiagnostico()){
					msgDiag.logErroreGenerico(e, ((HandlerException)e).getIdentitaHandler());
				}
				if(he.isSetErrorMessageInFault()){
					erroreIntegrazione = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.get5XX_ErroreProcessamento(e.getMessage());
				}
			}else{
				msgDiag.logErroreGenerico(e, "IntegrationManagerRequestHandler");
			}
			if(erroreIntegrazione==null){
				erroreIntegrazione = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.getErroreIntegrazione();
			}
			throw new IntegrationManagerException(protocolFactory,erroreIntegrazione);
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
			stato.initResource(this.propertiesReader.getIdentitaPortaDefault(protocolFactory.getProtocol()),IntegrationManager.ID_MODULO, idTransazione);
			ConfigurazionePdDManager configPdDManager = ConfigurazionePdDManager.getInstance(stato.getStatoRichiesta(),stato.getStatoRisposta());
			msgDiag.updateState(stato.getStatoRichiesta(),stato.getStatoRisposta());	
			
			// gestione credenziali
			this.gestioneCredenziali(protocolFactory,msgDiag, imRequestContext.getConnettore(),pddContext);
			
			// Autenticazione Servizio Applicativo
			id_servizio_applicativo = autenticazione(protocolFactory,msgDiag,imRequestContext.getConnettore(),configPdDManager,pddContext);
			servizio_applicativo = id_servizio_applicativo.getNome();
			imResponseContext.setServizioApplicativo(id_servizio_applicativo);
			String param = "ServizioApplicativo["+servizio_applicativo+"]";
			msgDiag.addKeyword(CostantiPdD.KEY_PARAMETRI_OPERAZIONE_IM, param);
			msgDiag.logPersonalizzato("logInvocazioneOperazione");

			// Effettuo ricerca ID DEL SERVIZIO APPLICATIVO
			GestoreMessaggi gestoreSearchID = new GestoreMessaggi(stato, true,msgDiag,pddContext);

			Vector<String> ids =  gestoreSearchID.getIDMessaggi_ServizioApplicativo(servizio_applicativo);      
			if(ids.size() == 0){
				msgDiag.logPersonalizzato("messaggiNonPresenti");
				throw new IntegrationManagerException(protocolFactory,ErroriIntegrazione.ERRORE_406_INTEGRATION_MANAGER_MESSAGGI_FOR_SIL_NON_TROVATI.
						getErroreIntegrazione(),servizio_applicativo);
			}

			// Creo i vari gestori di messaggi
			for(int i=0; i<ids.size(); i++){
				String idMessaggio = ids.get(i);
				GestoreMessaggi gestoreMessaggi = new GestoreMessaggi(stato, true,idMessaggio,Costanti.INBOX,msgDiag,pddContext);

				// Check Esistenza Messaggio
				if(gestoreMessaggi.existsMessageForSIL(servizio_applicativo) == false){
					msgDiag.addKeyword(CostantiPdD.KEY_ID_MESSAGGIO_TRANSACTION_MANAGER, idMessaggio);
					msgDiag.logPersonalizzato("messaggioNonTrovato");
					throw new IntegrationManagerException(protocolFactory,ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
							get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_520_READ_MSG_FROM_INTEGRATION_MANAGER),servizio_applicativo);
				}

				// Check Autorizzazione all'utilizzo di IntegrationManager
				boolean authorized = gestoreMessaggi.checkAutorizzazione(servizio_applicativo);
				if(authorized==false){
					msgDiag.logPersonalizzato("servizioApplicativo.nonAutorizzato");
					throw new IntegrationManagerException(protocolFactory,ErroriIntegrazione.ERRORE_404_AUTORIZZAZIONE_FALLITA.getErrore404_AutorizzazioneFallita(servizio_applicativo),
							servizio_applicativo);
				}
			}

			// Elimino i messaggi
			while(ids.size() > 0){
				// Elimino Messaggio
				String idMessaggio = ids.remove(0);
				try{
					//	Elimino accesso daPdD
					//	GestoreMessaggi gestoreEliminazione = new GestoreMessaggi(stato, true,idMessaggio,Costanti.INBOX,this.msgDiag);
					GestoreMessaggi gestoreEliminazione = new GestoreMessaggi(stato, true,idMessaggio,Costanti.INBOX,msgDiag,pddContext);
					gestoreEliminazione.eliminaDestinatarioMessaggio(servizio_applicativo, gestoreEliminazione.getRiferimentoMessaggio());
				}catch(Exception e){
					msgDiag.logErroreGenerico(e,"gestoreMessaggi.eliminaDestinatarioMessaggio("+tipoOperazione+","+servizio_applicativo+","+idMessaggio+")");
					throw new IntegrationManagerException(protocolFactory,ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
							get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_522_DELETE_MSG_FROM_INTEGRATION_MANAGER),servizio_applicativo);
				}
			}
			
			imResponseContext.setEsito(this.getEsitoTransazione(protocolFactory, imRequestContext, EsitoTransazioneName.OK));


		}catch(Exception e){
			
			try{
				imResponseContext.setEsito(this.getEsitoTransazione(protocolFactory, imRequestContext, EsitoTransazioneName.ERRORE_GENERICO));
				
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
				logCore.error("Errore durante la generazione dell'esito: "+eInteral.getMessage(),eInteral);
				imResponseContext.setEsito(EsitoTransazione.ESITO_TRANSAZIONE_ERROR);
			}
			
			// Controllo Eccezioni
			if(e instanceof IntegrationManagerException){
				// Lanciata all'interno del metodo
				throw (IntegrationManagerException)e;
			}else{
				msgDiag.logErroreGenerico(e,"deleteAllMessages("+tipoOperazione+")");
				throw new IntegrationManagerException(protocolFactory,ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.getErroreIntegrazione(),servizio_applicativo);
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
	 * @param idInvocazionePerRiferimento Indicazione se l'invocazione avviene per riferimento
	 * @return un Message contenente il messaggio di risposta 
	 * 
	 */
	private IntegrationManagerMessage invocaPortaDelegata_engine(String tipoOperazione, String portaDelegata, IntegrationManagerMessage msg,
			String idInvocazionePerRiferimento) throws IntegrationManagerException {

		String idModulo = RicezioneContenutiApplicativi.ID_MODULO+IntegrationManager.ID_MODULO;
		
		// Timestamp
		Timestamp dataIngressoMessaggio = DateManager.getTimestamp();
		
		// Logger
		Logger logCore = OpenSPCoop2Logger.getLoggerOpenSPCoopCore();
		if(logCore==null)
			logCore = LoggerWrapperFactory.getLogger(IntegrationManager.ID_MODULO);

		// check Autorizzazione
		checkIMAuthorization(logCore);
		
		// ProtocolFactoyr
		IProtocolFactory protocolFactory = getProtocolFactory(logCore);
		
		// Verifica risorse sistema
		this.verificaRisorseSistema(protocolFactory,logCore, tipoOperazione);
		
		// DumpRaw
		DumpRaw dumpRaw = null;
		try{
			ConfigurazionePdDManager configPdDManager = ConfigurazionePdDManager.getInstance();
			if(configPdDManager==null || configPdDManager.isInitializedConfigurazionePdDReader()==false){
				throw new Exception("ConfigurazionePdDManager not initialized");
			}
			if(configPdDManager.dumpBinarioPD()){
				dumpRaw = new DumpRaw(logCore,true);
			}
		}catch(Throwable e){
			logCore.error("Inizializzazione di OpenSPCoop non correttamente effettuata: ConfigurazionePdDManager");
			try{
				throw new IntegrationManagerException(protocolFactory,ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.getErroreIntegrazione());
			}catch(Throwable eError){
				logCore.error("Errore generazione SOAPFault",e);
			}
		}
		
		MsgDiagnostico msgDiag = getMsgDiagnostico();
		msgDiag.addKeyword(CostantiPdD.KEY_TIPO_OPERAZIONE_IM, tipoOperazione);
		
		

		
		
		
		
		
		/* ------------  Gestione ------------- */
		
		OpenSPCoopStateful stato = null;
		
		RicezioneContenutiApplicativiContext context = null;
		try{
			// viene generato l'UUID
			context = new RicezioneContenutiApplicativiContext(IDService.PORTA_DELEGATA_INTEGRATION_MANAGER, dataIngressoMessaggio,this.propertiesReader.getIdentitaPortaDefault(protocolFactory.getProtocol()));
		}catch(Exception e){
			msgDiag.logErroreGenerico(e,"invocaPortaDelegata_engine("+tipoOperazione+").newRicezioneContenutiApplicativiContext()");
			throw new IntegrationManagerException(protocolFactory,ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.getErroreIntegrazione());
		}
		context.getPddContext().addObject(CostantiPdD.KEY_TIPO_OPERAZIONE_IM, tipoOperazione);
		context.setTipoPorta(TipoPdD.DELEGATA);
		msgDiag.setPddContext(context.getPddContext(), protocolFactory);
		
		try{
			msgDiag.logPersonalizzato("ricezioneRichiesta.firstLog");
		}catch(Exception e){
			logCore.error("Errore generazione diagnostico di ingresso",e);
		}
		
		if(dumpRaw!=null){
			dumpRaw.serializeContext(context, protocolFactory.getProtocol());
		}
		
		// PddContext
		String idTransazione = PdDContext.getValue(org.openspcoop2.core.constants.Costanti.CLUSTER_ID,context.getPddContext()); 
		context.getPddContext().addObject(org.openspcoop2.core.constants.Costanti.CLUSTER_ID, idTransazione);
		context.getPddContext().addObject(org.openspcoop2.core.constants.Costanti.TIPO_OPERAZIONE_IM, tipoOperazione.toString());
		context.getPddContext().addObject(org.openspcoop2.core.constants.Costanti.ID_MESSAGGIO, idInvocazionePerRiferimento);
		context.getPddContext().addObject(org.openspcoop2.core.constants.Costanti.PORTA_DELEGATA, portaDelegata);
		context.getPddContext().addObject(org.openspcoop2.core.constants.Costanti.PROTOCOLLO, protocolFactory.getProtocol());
		
		EsitoTransazione esito = null;
		String errore = null;
		OpenSPCoop2Message msgRequest = null;
		OpenSPCoop2Message msgResponse = null;
		IntegrationManagerMessage msgReturn = null;
		String descrizioneSoapFault = "";
		URLProtocolContext urlProtocolContext = null;
		try{

			
			/* ------------  PreInHandler ------------- */
			
			// build context
			PreInRequestContext preInRequestContext = new PreInRequestContext(context.getPddContext());
			preInRequestContext.setTipoPorta(TipoPdD.DELEGATA);
			preInRequestContext.setIdModulo(idModulo);
			preInRequestContext.setProtocolFactory(protocolFactory);
			Hashtable<String, Object> transportContext = new Hashtable<String, Object>();
			HttpServletConnectorInMessage httpIn = null;
			try{
				httpIn = new HttpServletConnectorInMessage(getHttpServletRequest(), ID_SERVICE, ID_MODULO);
				transportContext.put(PreInRequestContext.SERVLET_REQUEST, httpIn);
			}catch(Exception e){
				ConnectorUtils.getErrorLog().error("HttpServletConnectorInMessage init error: "+e.getMessage(),e);
				//throw new ServletException(e.getMessage(),e);
			}
			HttpServletConnectorOutMessage httpOut = null;
			try{
				httpOut = new HttpServletConnectorOutMessage(protocolFactory, getHttpServletResponse(), ID_SERVICE, ID_MODULO);
				transportContext.put(PreInRequestContext.SERVLET_RESPONSE, httpOut);
			}catch(Exception e){
				ConnectorUtils.getErrorLog().error("HttpServletConnectorOutMessage init error: "+e.getMessage(),e);
				//throw new ServletException(e.getMessage(),e);
			}
			preInRequestContext.setTransportContext(transportContext);	
			preInRequestContext.setLogCore(logCore);
			
			// invocazione handler
			GestoreHandlers.preInRequest(preInRequestContext, msgDiag, logCore);
			
			// aggiungo eventuali info inserite nel preInHandler
			context.getPddContext().addAll(preInRequestContext.getPddContext(), false);
			
			// Lettura risposta parametri NotifierInputStream
			NotifierInputStreamParams notifierInputStreamParams = preInRequestContext.getNotifierInputStreamParams();
			context.setNotifierInputStreamParams(notifierInputStreamParams);
			
			msgDiag.logPersonalizzato("ricezioneRichiesta.elaborazioneDati.tipologiaMessaggio");
			
			
			
			

			/* ------------  Validazione INPUT in base all'operaizone invocata ------------- */
			if("invocaPortaDelegata".equals(tipoOperazione)){
				// check presenza body applicativo
				if(msg==null || msg.getMessage()==null){
					msgDiag.logPersonalizzato("invocazionePortaDelegata.contenutoApplicativoNonPresente");
					throw new IntegrationManagerException(protocolFactory,ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
							get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_520_READ_MSG_FROM_INTEGRATION_MANAGER));
				}
			}
			else if("invocaPortaDelegataPerRiferimento".equals(tipoOperazione)){
				//	 check presenza riferimento
				if(idInvocazionePerRiferimento==null){
					msgDiag.logPersonalizzato("invocazionePortaDelegataPerRiferimento.riferimentoMessaggioNonPresente");
					throw new IntegrationManagerException(protocolFactory,ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
							get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_520_READ_MSG_FROM_INTEGRATION_MANAGER));
				}
			}
			else if("sendRispostaAsincronaSimmetrica".equals(tipoOperazione)){
				// check presenza body applicativo
				if(msg==null || msg.getMessage()==null){
					msgDiag.logPersonalizzato("invocazionePortaDelegata.contenutoApplicativoNonPresente");
					throw new IntegrationManagerException(protocolFactory,ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
							get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_520_READ_MSG_FROM_INTEGRATION_MANAGER));
				}
				//	check presenza id di correlazione asincrona
				if(msg.getProtocolHeaderInfo()==null || msg.getProtocolHeaderInfo().getRiferimentoMessaggio()==null){
					msgDiag.logPersonalizzato("invocazionePortaDelegata.profiloAsincrono.riferimentoMessaggioNonPresente");
					throw new IntegrationManagerException(protocolFactory,ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
							get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_520_READ_MSG_FROM_INTEGRATION_MANAGER));
				}
			}
			else if("sendRichiestaStatoAsincronaAsimmetrica".equals(tipoOperazione)){
				//	check presenza body applicativo
				if(msg==null || msg.getMessage()==null){
					msgDiag.logPersonalizzato("invocazionePortaDelegata.contenutoApplicativoNonPresente");
					throw new IntegrationManagerException(protocolFactory,ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
							get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_520_READ_MSG_FROM_INTEGRATION_MANAGER));
				}
				//	check presenza id di correlazione asincrona
				if(msg.getProtocolHeaderInfo()==null || msg.getProtocolHeaderInfo().getRiferimentoMessaggio()==null){
					msgDiag.logPersonalizzato("invocazionePortaDelegata.profiloAsincrono.riferimentoMessaggioNonPresente");
					throw new IntegrationManagerException(protocolFactory,ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
							get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_520_READ_MSG_FROM_INTEGRATION_MANAGER));
				}
			}
			else{
				throw new Exception("Tipo operazione ["+tipoOperazione+"] non gestita");
			}
			
			
			
			/* ------------  Lettura parametri della richiesta ------------- */
			javax.servlet.http.HttpServletRequest req = this.getHttpServletRequest();
		
			// Credenziali utilizzate nella richiesta
			Credenziali credenziali = ServletUtils.getCredenziali(req,logCore); 
			// Credenziali credenziali = (Credenziali) msgContext.get("openspcoop.credenziali");

			String credenzialiFornite = "";
			if(credenziali!=null){
				if (credenziali.getUsername() != null || credenziali.getSubject() != null) {
//					credenzialiFornite = "(";
//					if (credenziali.getUsername() != null)
//						credenzialiFornite = credenzialiFornite + " Basic Username: "+ credenziali.getUsername() + " ";
//					if (credenziali.getSubject() != null)
//						credenzialiFornite = credenzialiFornite + " SSL Subject: "+ credenziali.getSubject() + " ";
//					credenzialiFornite = credenzialiFornite + ") ";
					credenzialiFornite = credenziali.toString();
				}
				msgDiag.addKeyword(CostantiPdD.KEY_CREDENZIALI_SA_FRUITORE, credenzialiFornite);
			}

			String invPerRiferimento = "";
			if(idInvocazionePerRiferimento!=null)
				invPerRiferimento = " idInvocazionePerRiferimento["+idInvocazionePerRiferimento+"]";

			String riferimentoMessaggio = "";
			if(msg.getProtocolHeaderInfo()!=null && msg.getProtocolHeaderInfo().getRiferimentoMessaggio()!=null){
				riferimentoMessaggio = " riferimentoMessaggio["+msg.getProtocolHeaderInfo().getRiferimentoMessaggio()+"]";
			}

			String param = "PD["+portaDelegata+"]"+credenzialiFornite+invPerRiferimento+riferimentoMessaggio;
			msgDiag.addKeyword(CostantiPdD.KEY_PARAMETRI_OPERAZIONE_IM, param);
			msgDiag.logPersonalizzato("logInvocazioneOperazione");

			
			
			// Properties Trasporto
			java.util.Properties headerTrasporto = null;
			if(this.propertiesReader.integrationManager_readInformazioniTrasporto()){
				headerTrasporto = 
					new java.util.Properties();	    
				java.util.Enumeration<?> enTrasporto = req.getHeaderNames();
				while(enTrasporto.hasMoreElements()){
					String nomeProperty = (String)enTrasporto.nextElement();
					headerTrasporto.setProperty(nomeProperty,req.getHeader(nomeProperty));
					//log.info("Proprieta' Trasporto: nome["+nomeProperty+"] valore["+req.getHeader(nomeProperty)+"]");
				} 
			}
			
			
			// Parametri della porta delegata invocata
			urlProtocolContext = new URLProtocolContext();
			urlProtocolContext.setFunctionParameters(portaDelegata);
			urlProtocolContext.setRequestURI(portaDelegata);
			urlProtocolContext.setFunction(URLProtocolContext.IntegrationManager_FUNCTION);
			urlProtocolContext.setProtocol(protocolFactory.getProtocol());
			if(this.propertiesReader.integrationManager_readInformazioniTrasporto()){
				urlProtocolContext.setParametersTrasporto(headerTrasporto);
			}
			

			//	Messaggio di richiesta
			msgDiag.logPersonalizzato("ricezioneRichiesta.elaborazioneDati.inCorso");
			Utilities.printFreeMemory("IntegrationManager - Pre costruzione richiesta");
			if(idInvocazionePerRiferimento!=null){

				if(dumpRaw!=null){
					String contentTypeRichiesta = null; // idInvocazionePerRiferimento
					Integer contentLengthRichiesta = null;
					String rawMessage = idInvocazionePerRiferimento;
					Identity identity = null;
					try{
						identity = new Identity(req);
					}catch(Throwable t){
						logCore.error("Dump Identity error: "+t.getMessage(),t);
					}
					dumpRaw.serializeRequest(contentTypeRichiesta, contentLengthRichiesta, identity, urlProtocolContext, rawMessage, null);
				}
				
				stato = new OpenSPCoopStateful();
				stato.initResource(this.propertiesReader.getIdentitaPortaDefault(protocolFactory.getProtocol()),IntegrationManager.ID_MODULO,idTransazione);
				msgDiag.updateState(stato.getStatoRichiesta(),stato.getStatoRisposta());
				
				// Leggo Messaggio
				try{
					GestoreMessaggi gestoreMessaggio = new GestoreMessaggi(stato, true,idInvocazionePerRiferimento,Costanti.INBOX,msgDiag,context.getPddContext());
					msgRequest = gestoreMessaggio.getMessage();	
				}catch(Exception e){
					msgDiag.logErroreGenerico(e,"gestoreMessaggio.getMessagePerRiferimento("+idInvocazionePerRiferimento+")");
					throw new IntegrationManagerException(protocolFactory,ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
							get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_520_READ_MSG_FROM_INTEGRATION_MANAGER));
				}

				// Rilascio Connessione DB
				stato.releaseResource();
				stato = null;

			}else{

				if(dumpRaw!=null){
					String contentTypeRichiesta = "text/xml"; // per ora e' cablato.
					Integer contentLengthRichiesta = null;
					String rawMessage = null;
					Identity identity = null;
					if(msg.getMessage()!=null){
						try{
							contentLengthRichiesta = msg.getMessage().length;
							rawMessage = new String(msg.getMessage());
						}catch(Throwable t){
							logCore.error("Dump error: "+t.getMessage(),t);
						}
					}
					try{
						identity = new Identity(req);
					}catch(Throwable t){
						logCore.error("Dump Identity error: "+t.getMessage(),t);
					}
					dumpRaw.serializeRequest(contentTypeRichiesta, contentLengthRichiesta, identity, urlProtocolContext, rawMessage, null);
				}
				
				// Ricostruzione Messaggio
				try{
					OpenSPCoop2MessageParseResult pr = SoapUtils.build(new SoapUtilsBuildParameter(msg.getMessage(), msg.getImbustamento(), 
							this.propertiesReader.isDeleteInstructionTargetMachineXml(), 
							this.propertiesReader.isFileCacheEnable(), 
							this.propertiesReader.getAttachmentRepoDir(), 
							this.propertiesReader.getFileThreshold()),
							notifierInputStreamParams);
					if(pr.getParseException()!=null){
						context.getPddContext().addObject(org.openspcoop2.core.constants.Costanti.CONTENUTO_RICHIESTA_NON_RICONOSCIUTO_PARSE_EXCEPTION, pr.getParseException());
					}
					msgRequest = pr.getMessage_throwParseException();
					msgRequest.setTransportRequestContext(urlProtocolContext);
				}catch(Exception e){
					
					Throwable tParsing = null;
					if(context.getPddContext().containsKey(org.openspcoop2.core.constants.Costanti.CONTENUTO_RICHIESTA_NON_RICONOSCIUTO_PARSE_EXCEPTION)){
						tParsing = ((ParseException) context.getPddContext().removeObject(org.openspcoop2.core.constants.Costanti.CONTENUTO_RICHIESTA_NON_RICONOSCIUTO_PARSE_EXCEPTION)).getParseException();
					}
					if(tParsing==null){
						tParsing = MessageUtils.getParseException(e);
					}
					if(tParsing==null){
						tParsing = e;
					}
					
					String msgErrore = tParsing.getMessage();
					if(msgErrore==null){
						msgErrore = tParsing.toString();
					}
					
					context.getPddContext().addObject(org.openspcoop2.core.constants.Costanti.CONTENUTO_RICHIESTA_NON_RICONOSCIUTO, true);
					if(ConfigurazionePdDManager.getInstance().dumpMessaggi()){
						Dump dumpApplicativo = new Dump(this.propertiesReader.getIdentitaPortaDefault(protocolFactory.getProtocol()),
								IntegrationManager.ID_MODULO,TipoPdD.DELEGATA,context.getPddContext(),null,null);
						dumpApplicativo.dumpRichiestaIngresso(msg.getMessage(),buildInfoConnettoreIngresso(req, credenziali, urlProtocolContext));
					}
					if(msg.getImbustamento()==false){
						msgDiag.addKeyword(CostantiPdD.KEY_ERRORE_PROCESSAMENTO, msgErrore);
						msgDiag.logPersonalizzato("buildMsg.nonRiuscito");
						throw new IntegrationManagerException(protocolFactory,ErroriIntegrazione.ERRORE_421_MSG_SOAP_NON_COSTRUIBILE_TRAMITE_RICHIESTA_APPLICATIVA.
								getErrore421_MessaggioSOAPNonGenerabile(msgErrore));
					} else {
						msgDiag.addKeyword(CostantiPdD.KEY_ERRORE_PROCESSAMENTO, msgErrore);
						msgDiag.logPersonalizzato("buildMsg.imbustamentoSOAP.nonRiuscito");
						throw new IntegrationManagerException(protocolFactory,ErroriIntegrazione.ERRORE_422_IMBUSTAMENTO_SOAP_NON_RIUSCITO_RICHIESTA_APPLICATIVA.
								getErrore422_MessaggioSOAPNonGenerabileTramiteImbustamentoSOAP(msgErrore));
					}
				}
			}
			Utilities.printFreeMemory("IntegrationManager - Post costruzione richiesta");
			msgRequest.setProtocolName(protocolFactory.getProtocol());

			


			//	Costruisco HeaderIntegrazione IntegrationManager
			HeaderIntegrazione headerIntegrazioneRichiesta = new HeaderIntegrazione();


			// Identificazione Porta Delegata INPUT-BASED
			ProtocolHeaderInfo protocolHeaderInfo = msg.getProtocolHeaderInfo();
			if(protocolHeaderInfo!=null){

				if(protocolHeaderInfo.getTipoMittente()!=null && (!"".equals(protocolHeaderInfo.getTipoMittente())))
					headerIntegrazioneRichiesta.getBusta().setTipoMittente(protocolHeaderInfo.getTipoMittente());
				if(protocolHeaderInfo.getMittente()!=null && (!"".equals(protocolHeaderInfo.getMittente())))
					headerIntegrazioneRichiesta.getBusta().setMittente(protocolHeaderInfo.getMittente());

				if(protocolHeaderInfo.getTipoDestinatario()!=null && (!"".equals(protocolHeaderInfo.getTipoDestinatario())))
					headerIntegrazioneRichiesta.getBusta().setTipoDestinatario(protocolHeaderInfo.getTipoDestinatario());
				if(protocolHeaderInfo.getDestinatario()!=null && (!"".equals(protocolHeaderInfo.getDestinatario())))
					headerIntegrazioneRichiesta.getBusta().setDestinatario(protocolHeaderInfo.getDestinatario());

				if(protocolHeaderInfo.getTipoServizio()!=null && (!"".equals(protocolHeaderInfo.getTipoServizio())))
					headerIntegrazioneRichiesta.getBusta().setTipoServizio(protocolHeaderInfo.getTipoServizio());
				if(protocolHeaderInfo.getServizio()!=null && (!"".equals(protocolHeaderInfo.getServizio())))
					headerIntegrazioneRichiesta.getBusta().setServizio(protocolHeaderInfo.getServizio());

				if(protocolHeaderInfo.getAzione()!=null && (!"".equals(protocolHeaderInfo.getAzione())))
					headerIntegrazioneRichiesta.getBusta().setAzione(protocolHeaderInfo.getAzione());

				if(protocolHeaderInfo.getID()!=null && (!"".equals(protocolHeaderInfo.getID())))
					headerIntegrazioneRichiesta.getBusta().setID(protocolHeaderInfo.getID());

				if(protocolHeaderInfo.getIdCollaborazione()!=null && (!"".equals(protocolHeaderInfo.getIdCollaborazione())))
					headerIntegrazioneRichiesta.getBusta().setIdCollaborazione(protocolHeaderInfo.getIdCollaborazione());

				if(protocolHeaderInfo.getRiferimentoMessaggio()!=null && (!"".equals(protocolHeaderInfo.getRiferimentoMessaggio())))
					headerIntegrazioneRichiesta.getBusta().setRiferimentoMessaggio(protocolHeaderInfo.getRiferimentoMessaggio());
			}
			if(msg.getIdApplicativo()!=null && (!"".equals(msg.getIdApplicativo())))
				headerIntegrazioneRichiesta.setIdApplicativo(msg.getIdApplicativo());
			if(msg.getServizioApplicativo()!=null && (!"".equals(msg.getServizioApplicativo())))
				headerIntegrazioneRichiesta.setServizioApplicativo(msg.getServizioApplicativo());

			//	Contesto di Richiesta
			context.setTipoPorta(TipoPdD.DELEGATA);
			context.setCredenziali(credenziali);
			context.setIdModulo(idModulo);
			context.setGestioneRisposta(true); // siamo in un webServices, la risposta deve essere aspettata
			context.setInvocazionePDPerRiferimento(idInvocazionePerRiferimento!=null);
			context.setIdInvocazionePDPerRiferimento(idInvocazionePerRiferimento);
			context.setMessageRequest(msgRequest);
			context.setUrlProtocolContext(urlProtocolContext);
			context.setHeaderIntegrazioneRichiesta(headerIntegrazioneRichiesta);
			context.getPddContext().addObject(org.openspcoop2.core.constants.Costanti.PROTOCOLLO, protocolFactory.getProtocol());
			context.getPddContext().addObject(org.openspcoop2.core.constants.Costanti.SOAP_VERSION, SOAPVersion.SOAP11);
			// Location
			context.setFromLocation(ServletUtils.getLocation(req, credenziali));
			
			// Log elaborazione dati completata
			msgDiag.logPersonalizzato("ricezioneRichiesta.elaborazioneDati.completata");
			
			// Invocazione...
			RicezioneContenutiApplicativi gestoreRichiesta = new RicezioneContenutiApplicativi(context);
			gestoreRichiesta.process(req);
			msgResponse = context.getMessageResponse();
			if(context.getMsgDiagnostico()!=null){
				msgDiag = context.getMsgDiagnostico();
				// Aggiorno informazioni
				msgDiag.addKeyword(CostantiPdD.KEY_TIPO_OPERAZIONE_IM, tipoOperazione);
			}

			// Check parsing request
			if((msgRequest!=null && msgRequest.getParseException() != null) || 
					(context.getPddContext().containsKey(org.openspcoop2.core.constants.Costanti.CONTENUTO_RICHIESTA_NON_RICONOSCIUTO_PARSE_EXCEPTION))){
			
				// Senno l'esito viene forzato essere 5XX
				try{
					esito = protocolFactory.createEsitoBuilder().getEsito(urlProtocolContext,EsitoTransazioneName.CONTENUTO_RICHIESTA_NON_RICONOSCIUTO);
				}catch(Exception eBuildError){
					esito = EsitoTransazione.ESITO_TRANSAZIONE_ERROR;
				}
				
				context.getPddContext().addObject(org.openspcoop2.core.constants.Costanti.CONTENUTO_RICHIESTA_NON_RICONOSCIUTO, true);
				ParseException parseException = null;
				if( msgRequest!=null && msgRequest.getParseException() != null ){
					parseException = msgRequest.getParseException();
				}
				else{
					parseException = (ParseException) context.getPddContext().getObject(org.openspcoop2.core.constants.Costanti.CONTENUTO_RICHIESTA_NON_RICONOSCIUTO_PARSE_EXCEPTION);
				}
				String msgErrore = parseException.getParseException().getMessage();
				if(msgErrore==null){
					msgErrore = parseException.getParseException().toString();
				}	
				msgDiag.addKeyword(CostantiPdD.KEY_ERRORE_PROCESSAMENTO, msgErrore);
				logCore.error("parsingExceptionRichiesta",parseException.getSourceException());
//				msgDiag.logPersonalizzato("parsingExceptionRichiesta");
//				throw new IntegrationManagerException(protocolFactory,ErroriIntegrazione.ERRORE_432_PARSING_EXCEPTION_RICHIESTA.
//						getErrore432_MessaggioRichiestaMalformato(parseException.getParseException()));
				// Per l'IntegrationManager esiste un codice specifico
				msgDiag.logPersonalizzato(MsgDiagnosticiProperties.MSG_DIAG_INTEGRATION_MANAGER,"buildMsg.nonRiuscito");
				throw new IntegrationManagerException(protocolFactory,ErroriIntegrazione.ERRORE_421_MSG_SOAP_NON_COSTRUIBILE_TRAMITE_RICHIESTA_APPLICATIVA.
						getErrore421_MessaggioSOAPNonGenerabile(msgErrore));
			}
			
			// Raccolgo l'eventuale header di integrazione
			java.util.Properties headerIntegrazioneRisposta = context.getHeaderIntegrazioneRisposta();
			ProtocolHeaderInfo protocolHeaderInfoResponse = null; 
			if(headerIntegrazioneRisposta!=null){
				java.util.Properties keyValue = this.propertiesReader.getKeyValue_HeaderIntegrazioneTrasporto();
				protocolHeaderInfoResponse = new ProtocolHeaderInfo();

				protocolHeaderInfoResponse.setID(headerIntegrazioneRisposta.getProperty(keyValue.getProperty(CostantiPdD.HEADER_INTEGRAZIONE_ID_MESSAGGIO)));
				protocolHeaderInfoResponse.setRiferimentoMessaggio(headerIntegrazioneRisposta.getProperty(keyValue.getProperty(CostantiPdD.HEADER_INTEGRAZIONE_RIFERIMENTO_MESSAGGIO)));
				protocolHeaderInfoResponse.setIdCollaborazione(headerIntegrazioneRisposta.getProperty(keyValue.getProperty(CostantiPdD.HEADER_INTEGRAZIONE_COLLABORAZIONE)));

				protocolHeaderInfoResponse.setMittente(headerIntegrazioneRisposta.getProperty(keyValue.getProperty(CostantiPdD.HEADER_INTEGRAZIONE_MITTENTE)));
				protocolHeaderInfoResponse.setTipoMittente(headerIntegrazioneRisposta.getProperty(keyValue.getProperty(CostantiPdD.HEADER_INTEGRAZIONE_TIPO_MITTENTE)));

				protocolHeaderInfoResponse.setDestinatario(headerIntegrazioneRisposta.getProperty(keyValue.getProperty(CostantiPdD.HEADER_INTEGRAZIONE_DESTINATARIO)));
				protocolHeaderInfoResponse.setTipoDestinatario(headerIntegrazioneRisposta.getProperty(keyValue.getProperty(CostantiPdD.HEADER_INTEGRAZIONE_TIPO_DESTINATARIO)));

				protocolHeaderInfoResponse.setServizio(headerIntegrazioneRisposta.getProperty(keyValue.getProperty(CostantiPdD.HEADER_INTEGRAZIONE_SERVIZIO)));
				protocolHeaderInfoResponse.setTipoServizio(headerIntegrazioneRisposta.getProperty(keyValue.getProperty(CostantiPdD.HEADER_INTEGRAZIONE_TIPO_SERVIZIO)));

				protocolHeaderInfoResponse.setAzione(headerIntegrazioneRisposta.getProperty(keyValue.getProperty(CostantiPdD.HEADER_INTEGRAZIONE_AZIONE)));
			}

			InformazioniErroriInfrastrutturali informazioniErrori = ServletUtils.readInformazioniErroriInfrastrutturali(context.getPddContext());
			
			//	IntepretazioneRisposta
			if(msgResponse!=null){
				
				esito = protocolFactory.createEsitoBuilder().getEsito(urlProtocolContext, msgResponse, 
						context.getProprietaErroreAppl(), informazioniErrori, 
						(context.getPddContext()!=null ? context.getPddContext().getContext() : null));			
												
				IntegrationManagerException exc = null;
				try{
					if(msgResponse.getSOAPBody().hasFault()){

						descrizioneSoapFault = " ("+SoapUtils.toString(msgResponse.getSOAPBody().getFault(), false)+")";
						
						// Potenziale MsgErroreApplicativo
						SOAPFault fault = msgResponse.getSOAPBody().getFault();
						ProprietaErroreApplicativo pea = context.getProprietaErroreAppl();
						if(pea!=null && fault.getFaultActor()!=null && fault.getFaultActor().equals(pea.getFaultActor())){
							String prefix = org.openspcoop2.protocol.basic.Costanti.ERRORE_INTEGRAZIONE_PREFIX_CODE;
							if(context.getProprietaErroreAppl().getFaultPrefixCode()!=null){
								prefix = context.getProprietaErroreAppl().getFaultPrefixCode();
							}
							if(this.propertiesReader.isErroreApplicativoIntoDetails() && fault.getDetail()!=null){
								exc = ServicesUtils.mapXMLIntoProtocolException(protocolFactory,fault.getDetail().getFirstChild(),
										prefix);
							}else{
								exc = ServicesUtils.mapXMLIntoProtocolException(protocolFactory,fault.getFaultString(),
										prefix);
							}
							if(exc== null)
								throw new Exception("Costruzione Eccezione fallita: null");
						}
					}
				}catch(Exception e){
					try{
						if( (msgResponse!=null && msgResponse.getParseException() != null) ||
								(context.getPddContext().containsKey(org.openspcoop2.core.constants.Costanti.CONTENUTO_RISPOSTA_NON_RICONOSCIUTO_PARSE_EXCEPTION))){
							informazioniErrori.setContenutoRispostaNonRiconosciuto(true);
							ParseException parseException = null;
							if( msgResponse!=null && msgResponse.getParseException() != null ){
								parseException = msgResponse.getParseException();
							}
							else{
								parseException = (ParseException) context.getPddContext().getObject(org.openspcoop2.core.constants.Costanti.CONTENUTO_RISPOSTA_NON_RICONOSCIUTO_PARSE_EXCEPTION);
							}
							String msgErrore = parseException.getParseException().getMessage();
							if(msgErrore==null){
								msgErrore = parseException.getParseException().toString();
							}
							msgDiag.addKeyword(CostantiPdD.KEY_ERRORE_PROCESSAMENTO, msgErrore);
							logCore.error("parsingExceptionRisposta",parseException.getSourceException());
							msgDiag.logPersonalizzato("parsingExceptionRisposta");
							throw new IntegrationManagerException(protocolFactory,ErroriIntegrazione.ERRORE_440_PARSING_EXCEPTION_RISPOSTA.
									getErrore440_MessaggioRispostaMalformato(parseException.getParseException()));
						}
						else{
							msgDiag.logErroreGenerico(e,"buildProtocolException("+tipoOperazione+")");
							throw new IntegrationManagerException(protocolFactory,ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
									get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_524_CREAZIONE_PROTOCOL_EXCEPTION));
						}
					}finally{
						// ricalcolo esito
						esito = protocolFactory.createEsitoBuilder().getEsito(urlProtocolContext, msgResponse, 
								context.getProprietaErroreAppl(), informazioniErrori, 
								(context.getPddContext()!=null ? context.getPddContext().getContext() : null));		
					}
				}	
				if(exc!=null){
					throw exc;
				}

				// Se non ho lanciato l'eccezione, costruisco una risposta
				try{
					msgReturn = new IntegrationManagerMessage(msgResponse,false);
				}catch(Exception e){
					try{
						if( (msgResponse!=null && msgResponse.getParseException() != null) ||
								(context.getPddContext().containsKey(org.openspcoop2.core.constants.Costanti.CONTENUTO_RISPOSTA_NON_RICONOSCIUTO_PARSE_EXCEPTION))){
							informazioniErrori.setContenutoRispostaNonRiconosciuto(true);
							ParseException parseException = null;
							if( msgResponse!=null && msgResponse.getParseException() != null ){
								parseException = msgResponse.getParseException();
							}
							else{
								parseException = (ParseException) context.getPddContext().getObject(org.openspcoop2.core.constants.Costanti.CONTENUTO_RISPOSTA_NON_RICONOSCIUTO_PARSE_EXCEPTION);
							}
							String msgErrore = parseException.getParseException().getMessage();
							if(msgErrore==null){
								msgErrore = parseException.getParseException().toString();
							}
							msgDiag.addKeyword(CostantiPdD.KEY_ERRORE_PROCESSAMENTO, msgErrore);
							logCore.error("parsingExceptionRisposta",parseException.getSourceException());
							msgDiag.logPersonalizzato("parsingExceptionRisposta");
							throw new IntegrationManagerException(protocolFactory,ErroriIntegrazione.ERRORE_440_PARSING_EXCEPTION_RISPOSTA.
									getErrore440_MessaggioRispostaMalformato(parseException.getParseException()));
						}
						else{
							msgDiag.logErroreGenerico(e,"buildMessage_response("+tipoOperazione+")");
							throw new IntegrationManagerException(protocolFactory,ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
									get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_523_CREAZIONE_PROTOCOL_MESSAGE));
						}
					}finally{
						// ricalcolo esito
						esito = protocolFactory.createEsitoBuilder().getEsito(urlProtocolContext, msgResponse, 
								context.getProprietaErroreAppl(), informazioniErrori, 
								(context.getPddContext()!=null ?context.getPddContext().getContext() : null));		
					}
				}
				
				msgDiag.addKeyword(CostantiPdD.KEY_SOAP_FAULT, descrizioneSoapFault);
				msgDiag.logPersonalizzato(MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_CONTENUTI_APPLICATIVI,"integrationManager.consegnaRispostaApplicativaEffettuata");
				
			}else{
				/*
				// Con la nuova versione di RicezioneContenutiApplicativi, in caso di oneway non viene ritornato un messaggio SOAP empty, ma proprio null
				// Una risposta deve esistere per forza.
				msgDiag.logErroreGenerico("Risposta non presente","gestioneRisposta("+tipoOperazione+")");
				throw new IntegrationManagerException(CostantiPdD.CODICE_511_READ_RESPONSE_MSG,
						CostantiPdD.MSG_5XX_SISTEMA_NON_DISPONIBILE);
				*/
				msgReturn = new IntegrationManagerMessage();
				
				esito = protocolFactory.createEsitoBuilder().getEsito(urlProtocolContext, msgResponse, 
						context.getProprietaErroreAppl(), informazioniErrori, 
						(context.getPddContext()!=null ? context.getPddContext().getContext() : null));	
				// ok oneway
				
				msgDiag.logPersonalizzato(MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_CONTENUTI_APPLICATIVI,"integrationManager.consegnaRispostaApplicativaVuota");
			}	    

			msgReturn.setProtocolHeaderInfo(protocolHeaderInfoResponse);
			
			if(dumpRaw!=null){
				String contentTypeRisposta = "text/xml"; // per ora e' cablato.
				String rawMessage = null;
				Integer contentLengthRisposta = null;
				if(msgReturn.getMessage()!=null){
					try{
						contentLengthRisposta = msgReturn.getMessage().length;
						rawMessage = new String(msgReturn.getMessage());
					}catch(Throwable t){
						logCore.error("Dump error: "+t.getMessage(),t);
					}
				}
				dumpRaw.serializeResponse(rawMessage, null, null, contentLengthRisposta, contentTypeRisposta, 200);
			}
			
			return msgReturn;
			
		}catch(Exception e){
			
			if(dumpRaw!=null){
				String contentTypeRisposta = "text/xml"; // per ora e' cablato.
				String rawMessage = "[Exception "+e.getClass().getName()+"]: "+ e.getMessage(); // Non riesco ad avere il marshal xml della risposta ritornata
				Integer contentLengthRisposta = null;
				dumpRaw.serializeResponse(rawMessage, null,null, contentLengthRisposta, contentTypeRisposta, 500);
			}
			
			errore = e.getMessage();
			msgDiag.addKeyword(CostantiPdD.KEY_SOAP_FAULT, descrizioneSoapFault);
			
			// Controllo Eccezioni
			if(e instanceof IntegrationManagerException){
				// Lanciata all'interno del metodo
				
				if(esito==null){
					// Altrimenti l'esito era settato
					try{
						esito = protocolFactory.createEsitoBuilder().getEsito(urlProtocolContext,EsitoTransazioneName.ERRORE_PROCESSAMENTO_PDD_5XX);
					}catch(Exception eBuildError){
						esito = EsitoTransazione.ESITO_TRANSAZIONE_ERROR;
					}
				}
				
				IntegrationManagerException eSPC = (IntegrationManagerException)e;
				//String prefix = eSPC.getProprietaErroreApplicativo().getFaultPrefixCode();
				//boolean isFaultAsGenericCode = eSPC.getProprietaErroreApplicativo().isFaultAsGenericCode();
				if(descrizioneSoapFault!=null && !"".equals(descrizioneSoapFault)){
					// stesse informazioni presenti gia nel fault
					msgDiag.addKeyword(CostantiPdD.KEY_ERRORE_CONSEGNA,"ProtocolException/"+eSPC.getCodiceEccezione());
				}else{
					msgDiag.addKeyword(CostantiPdD.KEY_ERRORE_CONSEGNA,"ProtocolException/"+eSPC.getCodiceEccezione() + " " + eSPC.getDescrizioneEccezione());
				}
				msgDiag.logPersonalizzato(MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_CONTENUTI_APPLICATIVI,"integrationManager.consegnaRispostaApplicativaFallita");
				
				throw eSPC;
			}else{
				msgDiag.logErroreGenerico(e,"invocaPortaDelegata("+tipoOperazione+")");
				try{
					esito = protocolFactory.createEsitoBuilder().getEsito(urlProtocolContext,EsitoTransazioneName.ERRORE_PROCESSAMENTO_PDD_5XX);
				}catch(Exception eBuildError){
					esito = EsitoTransazione.ESITO_TRANSAZIONE_ERROR;
				}
				
				msgDiag.addKeyword(CostantiPdD.KEY_ERRORE_CONSEGNA,errore);
				msgDiag.logPersonalizzato(MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_CONTENUTI_APPLICATIVI,"integrationManager.consegnaRispostaApplicativaFallita");
				
				throw new IntegrationManagerException(protocolFactory,ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.getErroreIntegrazione());
			}
			
		}finally{
			
			try{
				if(stato!=null)
					stato.releaseResource();
			}catch(Exception eClose){}
			
			
			
			
			// *** Chiudo connessione verso PdD Destinazione per casi stateless ***
			String location = "...";
			try{
				IConnettore c = null;
				if(context!=null && context.getIdMessage()!=null){
					c = RepositoryConnettori.removeConnettorePD(context.getIdMessage());
				}
				if(c!=null){
					location = c.getLocation();
					c.disconnect();
				}
			}catch(Exception e){
				msgDiag.logDisconnectError(e, location);
			}
			
			
			
			
			/* ------------  PostOutResponseHandler ------------- */
			PostOutResponseContext postOutResponseContext = new PostOutResponseContext(logCore, protocolFactory);
			try{
				context.getPddContext().addObject(CostantiPdD.DATA_INGRESSO_MESSAGGIO_RICHIESTA, dataIngressoMessaggio);
				postOutResponseContext.setPddContext(context.getPddContext());
				postOutResponseContext.setDataElaborazioneMessaggio(DateManager.getDate());
				postOutResponseContext.setEsito(esito);
				postOutResponseContext.setMessaggio(msgResponse);
				
				if(msgRequest!=null){
					postOutResponseContext.setInputRequestMessageSize(msgRequest.getIncomingMessageContentLength());
					postOutResponseContext.setOutputRequestMessageSize(msgRequest.getOutgoingMessageContentLength());
				}else{
					if(msg!=null && msg.getMessage()!=null)
						postOutResponseContext.setInputRequestMessageSize(new Long(msg.getMessage().length));
				}
				if(msgResponse!=null){
					postOutResponseContext.setInputResponseMessageSize(msgResponse.getIncomingMessageContentLength());
					if(msgReturn!=null && msgReturn.getMessage()!=null)
						postOutResponseContext.setOutputResponseMessageSize(new Long(msgReturn.getMessage().length));
				}
				
				if(errore!=null){
					postOutResponseContext.setReturnCode(500);
				}else{
					postOutResponseContext.setReturnCode(200);
				}
//				if(context!=null){
				if(context.getTipoPorta()!=null)
					postOutResponseContext.setTipoPorta(context.getTipoPorta());
				else
					postOutResponseContext.setTipoPorta(TipoPdD.DELEGATA);
				postOutResponseContext.setProtocollo(context.getProtocol());
				postOutResponseContext.setIntegrazione(context.getIntegrazione());
//				}else{
//					postOutResponseContext.setTipoPorta(TipoPdD.DELEGATA);
//				}
				postOutResponseContext.setIdModulo(idModulo);
								
			}catch(Exception e){
				msgDiag.logErroreGenerico(e,"postOutResponse, preparazione contesto");
			}
			
			GestoreHandlers.postOutResponse(postOutResponseContext, msgDiag, logCore);
		}
	}





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



}
