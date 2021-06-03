/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it). 
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



package org.openspcoop2.pdd.timers;

import java.sql.Connection;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.core.config.driver.DriverConfigurazioneNotFound;
import org.openspcoop2.core.id.IDPortaDelegata;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.driver.IDServizioFactory;
import org.openspcoop2.pdd.config.ClassNameProperties;
import org.openspcoop2.pdd.config.ConfigurazionePdDManager;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.config.RichiestaDelegata;
import org.openspcoop2.pdd.core.AbstractCore;
import org.openspcoop2.pdd.core.CostantiPdD;
import org.openspcoop2.pdd.core.EJBUtilsException;
import org.openspcoop2.pdd.core.GestoreMessaggi;
import org.openspcoop2.pdd.core.PdDContext;
import org.openspcoop2.pdd.core.node.INodeSender;
import org.openspcoop2.pdd.core.state.OpenSPCoopStateful;
import org.openspcoop2.pdd.core.state.OpenSPCoopStateless;
import org.openspcoop2.pdd.logger.MsgDiagnostico;
import org.openspcoop2.pdd.mdb.InoltroBuste;
import org.openspcoop2.pdd.mdb.InoltroBusteMessage;
import org.openspcoop2.pdd.services.OpenSPCoop2Startup;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.engine.constants.Costanti;
import org.openspcoop2.protocol.engine.driver.BustaNonRiscontrata;
import org.openspcoop2.protocol.engine.driver.ProfiloDiCollaborazione;
import org.openspcoop2.protocol.engine.driver.RepositoryBuste;
import org.openspcoop2.protocol.engine.driver.Riscontri;
import org.openspcoop2.protocol.engine.driver.RollbackRepositoryBuste;
import org.openspcoop2.protocol.registry.RegistroServiziManager;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.Integrazione;
import org.openspcoop2.protocol.sdk.builder.ProprietaErroreApplicativo;
import org.openspcoop2.protocol.sdk.state.StateMessage;
import org.openspcoop2.protocol.sdk.state.StatelessMessage;
import org.openspcoop2.utils.TipiDatabase;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.id.serial.InfoStatistics;
import org.openspcoop2.utils.resources.Loader;
import org.openspcoop2.utils.semaphore.Semaphore;
import org.openspcoop2.utils.semaphore.SemaphoreConfiguration;
import org.openspcoop2.utils.semaphore.SemaphoreMapping;

/**
 * Implementazione dell'interfaccia {@link TimerGestoreBusteNonRiscontrate} del Gestore
 * dei threads di servizio di OpenSPCoop.
 * 
 *  
 * @author Poli Andrea (apoli@link.it)
 * @author Marcello Spadafora (ma.spadafora@finsiel.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class TimerGestoreBusteNonRiscontrateLib {

	public static TimerState STATE_ONEWAY = TimerState.OFF; // abilitato in OpenSPCoop2Startup al momento dell'avvio
	public static TimerState STATE_ASINCRONI = TimerState.OFF; // abilitato in OpenSPCoop2Startup al momento dell'avvio
	
	private MsgDiagnostico msgDiag = null;
	private Logger logTimer = null;
	private OpenSPCoop2Properties propertiesReader = null;
	private boolean logQuery = false;
	private int limit = CostantiPdD.LIMIT_MESSAGGI_GESTORI;
	private long timeout = 10;
	private ConfigurazionePdDManager configurazionePdDReader;
	private RegistroServiziManager registroServiziReader;

	/** INodeSender */
	//private static INodeSender nodeSender = null;
	// C'e' lo stato
	private static String tipoNodeSender = null;

	private ProtocolFactoryManager protocolFactoryManager = null;

	private TimerLock timerLock = null;

	/** Semaforo */
	private Semaphore semaphore = null;
	private InfoStatistics semaphore_statistics;

	public TimerGestoreBusteNonRiscontrateLib(MsgDiagnostico msgDiag,Logger log,OpenSPCoop2Properties p,boolean logQuery,int limit,
			long timeout,ConfigurazionePdDManager configurazionePdDReader,RegistroServiziManager registroServiziReader) throws TimerException{
		this.msgDiag = msgDiag;
		this.logTimer = log;
		this.propertiesReader = p;
		this.logQuery = logQuery;
		this.limit = limit;
		this.timeout = timeout;
		this.configurazionePdDReader = configurazionePdDReader;
		this.registroServiziReader = registroServiziReader;
		try{
			this.protocolFactoryManager = ProtocolFactoryManager.getInstance();
		}catch(Exception e){
			// log dell'eventuale errore e' dentro il metodo
			throw new TimerException("Riscontrato errore durante l'inizializzazione del ProtocolFactoryManager: "+e.getMessage(),e);
		}

		if(TimerGestoreBusteNonRiscontrateLib.tipoNodeSender==null){
			try{
				TimerGestoreBusteNonRiscontrateLib.tipoNodeSender = this.propertiesReader.getNodeSender();
			}catch(Exception e){
				// log dell'eventuale errore e' dentro il metodo
				throw new TimerException("Riscontrato errore durante l'inizializzazione del Thread GestoreBusteNonRiscontrate (NodeSender): "+e.getMessage(),e);
			}
		}
		
		this.timerLock = new TimerLock(TipoLock.GESTIONE_BUSTE_NON_RISCONTRATE);

		if(this.propertiesReader.isTimerLockByDatabase()) {
			this.semaphore_statistics = new InfoStatistics();

			SemaphoreConfiguration config = GestoreMessaggi.newSemaphoreConfiguration(this.propertiesReader.getTimerGestoreRiscontriRicevute_lockMaxLife(), 
					this.propertiesReader.getTimerGestoreRiscontriRicevute_lockIdleTime());

			TipiDatabase databaseType = TipiDatabase.toEnumConstant(this.propertiesReader.getDatabaseType());
			try {
				this.semaphore = new Semaphore(this.semaphore_statistics, SemaphoreMapping.newInstance(this.timerLock.getIdLock()), 
						config, databaseType, this.logTimer);
			}catch(Exception e) {
				throw new TimerException(e.getMessage(),e);
			}
		}
	}


	public void check() throws TimerException {

		// Controllo che il sistema non sia andando in shutdown
		if(OpenSPCoop2Startup.contextDestroyed){
			this.logTimer.error("["+TimerGestoreBusteNonRiscontrate.ID_MODULO+"] Rilevato sistema in shutdown");
			return;
		}
		
		// Controllo che l'inizializzazione corretta delle risorse sia effettuata
		if(OpenSPCoop2Startup.initialize==false){
			this.msgDiag.logFatalError("inizializzazione di OpenSPCoop non effettuata", "Check Inizializzazione");
			String msgErrore = "Riscontrato errore: inizializzazione del Timer o di OpenSPCoop non effettuata";
			this.logTimer.error(msgErrore);
			throw new TimerException(msgErrore);
		}

		// Controllo risorse di sistema disponibili
		if( TimerMonitoraggioRisorseThread.risorseDisponibili == false){
			this.logTimer.error("["+TimerGestoreBusteNonRiscontrate.ID_MODULO+"] Risorse di sistema non disponibili: "+TimerMonitoraggioRisorseThread.risorsaNonDisponibile.getMessage(),TimerMonitoraggioRisorseThread.risorsaNonDisponibile);
			return;
		}
		if( MsgDiagnostico.gestoreDiagnosticaDisponibile == false){
			this.logTimer.error("["+TimerGestoreBusteNonRiscontrate.ID_MODULO+"] Sistema di diagnostica non disponibile: "+MsgDiagnostico.motivoMalfunzionamentoDiagnostici.getMessage(),MsgDiagnostico.motivoMalfunzionamentoDiagnostici);
			return;
		}

		// Controllo che il timer non sia stato momentaneamente disabilitato
		if(!TimerState.ENABLED.equals(STATE_ONEWAY) && !TimerState.ENABLED.equals(STATE_ASINCRONI)) {
			this.msgDiag.logPersonalizzato("disabilitato");
			this.logTimer.info(this.msgDiag.getMessaggio_replaceKeywords("disabilitato"));
			return;
		}
		
		// refresh
		if( this.configurazionePdDReader.getTimeoutRiscontro() < 1  )	{
			String msgErrore = "Riscontrato errore durante la lettura del timeout per le buste non riscontrate (<=0 ??)";
			this.msgDiag.logErroreGenerico(msgErrore,"getTimeoutRiscontro()");
			this.logTimer.error(msgErrore);
			return;
		}

		this.timeout = this.configurazionePdDReader.getTimeoutRiscontro();
		String minuti = "minuti";
		if(this.timeout == 1)
			minuti = "minuto";
		this.msgDiag.addKeyword(CostantiPdD.KEY_TIMEOUT, this.timeout+" "+minuti);
		this.msgDiag.addKeyword(CostantiPdD.KEY_LIMIT, this.limit+"");
		this.msgDiag.logPersonalizzato("controlloInCorso");
		this.logTimer.info(this.msgDiag.getMessaggio_replaceKeywords("controlloInCorso"));
		long startControlloBusteNonRiscontrate = DateManager.getTimeMillis();


		// Gestione Riscontri...
		GestoreMessaggi gestoreMsg= null;
		GestoreMessaggi messaggioDaInviare= null;
		RepositoryBuste repositoryBuste= null;
		Riscontri gestoreRiscontri = null;
		RollbackRepositoryBuste rollbackRepositoryBuste= null;

		OpenSPCoopStateful openspcoopState = new OpenSPCoopStateful();

		try {

			openspcoopState.initResource(this.propertiesReader.getIdentitaPortaDefault(null),TimerGestoreBusteNonRiscontrate.ID_MODULO, null);
			Connection connectionDB = ((StateMessage)openspcoopState.getStatoRichiesta()).getConnectionDB();


			/* ----- Gestione Riscontri per profilo OneWay ----- */
			if(TimerState.ENABLED.equals(STATE_ONEWAY)) {
				Riscontri rBuste = new Riscontri(openspcoopState.getStatoRichiesta(),this.logTimer);
				int offsetRiscontri = 0;
				String causaBusteOneWayToSend = "Rispedizione buste con profilo oneway non riscontrate";
				List<BustaNonRiscontrata> busteOneWayToSend = null;
				try{
					GestoreMessaggi.acquireLock(
							this.semaphore, connectionDB, this.timerLock,
							this.msgDiag, causaBusteOneWayToSend, 
							this.propertiesReader.getTimerGestoreRiscontriRicevute_getLockAttesaAttiva(), 
							this.propertiesReader.getTimerGestoreRiscontriRicevute_getLockCheckInterval());
					
					busteOneWayToSend = rBuste.getBustePerUlterioreInoltro(this.timeout,this.limit,offsetRiscontri,this.logQuery);
					if(this.logQuery){
						if( (busteOneWayToSend != null) && (busteOneWayToSend.size()<=0)){
							this.logTimer.info("Non sono state trovate buste con profilo oneway non riscontrate da rispedire");
						}
					}
		
					// Gestione buste da re-spedire
					while ( (busteOneWayToSend != null) && (busteOneWayToSend.size()>0) ) {
		
						if(this.logQuery)
							this.logTimer.info("Trovate "+busteOneWayToSend.size()+" buste con profilo oneway non riscontrate da rispedire ...");
		
						int gestiti = 0;
						for (int i = 0; i < busteOneWayToSend.size(); i++) {
		
							BustaNonRiscontrata bustaNonRiscontrata = busteOneWayToSend.get(i);
							String idBustaDaRispedire = bustaNonRiscontrata.getIdentificativo();
							this.msgDiag.addKeyword(CostantiPdD.KEY_ID_MESSAGGIO_RICHIESTA, idBustaDaRispedire);
							this.msgDiag.addKeyword(CostantiPdD.KEY_PROFILO_COLLABORAZIONE, bustaNonRiscontrata.getProfiloCollaborazione().name());
		
							// Busta da rispedire
							this.msgDiag.logPersonalizzato("bustaNonRiscontrata");
							if(this.logQuery){
								this.logTimer.debug(this.msgDiag.getMessaggio_replaceKeywords("bustaNonRiscontrata"));
							}
							
							try{
								GestoreMessaggi.updateLock(
										this.semaphore, connectionDB, this.timerLock,
										this.msgDiag, "Rispedizione busta con profilo oneway con id ["+idBustaDaRispedire+"] ...");
							}catch(Throwable e){
								this.msgDiag.logErroreGenerico(e,"RispedizioneBustaNonRiscontrataOneWay("+idBustaDaRispedire+")-UpdateLock");
								this.logTimer.error("ErroreRispedizioneBustaNonRiscontrataOneWay("+idBustaDaRispedire+")-UpdateLock: "+e.getMessage(),e);
								break;
							}
		
							// Controllo che al riscontro corrisponda effettivamente un messaggio nel repository
							messaggioDaInviare = new GestoreMessaggi(openspcoopState,true,idBustaDaRispedire,Costanti.OUTBOX,
									this.logTimer,this.msgDiag,null);
							if(messaggioDaInviare.existsMessage_noCache()==false){
								this.msgDiag.logPersonalizzato("bustaNonRiscontrata.messaggioNonEsistente");
								this.logTimer.error(this.msgDiag.getMessaggio_replaceKeywords("bustaNonRiscontrata.messaggioNonEsistente"));
								try{
									repositoryBuste = new RepositoryBuste(openspcoopState.getStatoRichiesta(), true,null);
									repositoryBuste.eliminaUtilizzoPdDFromOutBox(idBustaDaRispedire);
		
									gestoreRiscontri = new Riscontri(openspcoopState.getStatoRichiesta(),null);
									gestoreRiscontri.validazioneRiscontroRicevuto(idBustaDaRispedire);
		
									((StateMessage)openspcoopState.getStatoRichiesta()).executePreparedStatement();
									
									gestiti++;
									
								}catch(Exception e){
									this.msgDiag.logErroreGenerico(e,"EliminazioneBustaNonRiscontrataNonEsistente("+idBustaDaRispedire+")");
									this.logTimer.error("ErroreEliminazioneBustaNonRiscontrataNonEsistente("+idBustaDaRispedire+"): "+e.getMessage(),e);
								}
							}
							else{
		
								try{
		
									// PdDContext
									PdDContext pddContext = messaggioDaInviare.getPdDContext(true); // aggiorno anche l'istanza dentro l'oggetto messaggioDaInviare stesso.
									IProtocolFactory<?> protocolFactory = this.protocolFactoryManager.getProtocolFactoryByName((String) pddContext.getObject(org.openspcoop2.core.constants.Costanti.PROTOCOL_NAME));
		
									// Recupero busta
									repositoryBuste = new RepositoryBuste(openspcoopState.getStatoRichiesta(),this.logTimer, true,protocolFactory);
									Busta bustaToSend = repositoryBuste.getBustaFromOutBox(idBustaDaRispedire);
									this.msgDiag.addKeywords(bustaToSend, true);	
		
									// Controllo se la busta e' scaduta
									Date scadenza = bustaToSend.getScadenza();
									Timestamp now = DateManager.getTimestamp();
		
									// Eventuale msg Scaduto.
									if (scadenza.before(now)) {
										// Busta scaduta
		
										this.msgDiag.logPersonalizzato("bustaNonRiscontrataScaduta");
										if(this.logQuery)
											this.logTimer.info(this.msgDiag.getMessaggio_replaceKeywords("bustaNonRiscontrataScaduta"));
		
										try{
											repositoryBuste = new RepositoryBuste(openspcoopState.getStatoRichiesta(), true,protocolFactory);
											repositoryBuste.eliminaUtilizzoPdDFromOutBox(bustaToSend.getID());
											((StateMessage)openspcoopState.getStatoRichiesta()).executePreparedStatement();
											gestoreMsg = new GestoreMessaggi(openspcoopState, true, bustaToSend.getID(),Costanti.OUTBOX,this.msgDiag,pddContext);
											gestoreMsg.validateAndDeleteMsgOneWayRiscontrato();
										}catch(Exception e){
											this.msgDiag.logErroreGenerico(e,"EliminazioneBustaNonRiscontrataScaduta("+bustaToSend.getID()+")");
											this.logTimer.error("ErroreEliminazioneBustaNonRiscontrataScaduta("+bustaToSend.getID()+"): "+e.getMessage(),e);
										}
		
									}
									else {
		
										// Dati
										IDSoggetto soggettoBustaNonRiscontrata = new IDSoggetto(bustaToSend.getTipoMittente(),
												bustaToSend.getMittente());
										IDSoggetto identitaPdD = null;
										String dominioRD = null;
										try{
											dominioRD = this.configurazionePdDReader.getIdentificativoPorta(soggettoBustaNonRiscontrata,protocolFactory);
											if(dominioRD==null){
												throw new Exception("Dominio is null");
											}
										}catch(Exception e){
											this.msgDiag.logErroreGenerico(e,"BustaNonRiscontrata getDominio("+soggettoBustaNonRiscontrata+")");
										}
										if(dominioRD==null){
											identitaPdD = this.propertiesReader.getIdentitaPortaDefault(null);
										}else{
											identitaPdD = new IDSoggetto(bustaToSend.getTipoMittente(),
													bustaToSend.getMittente(),dominioRD);
										}
	                                    IDServizio servizioBusta = IDServizioFactory.getInstance().getIDServizioFromValues(bustaToSend.getTipoServizio(),
	                                            bustaToSend.getServizio(),
	                                            bustaToSend.getTipoDestinatario(), 
	                                            bustaToSend.getDestinatario(), 
	                                            bustaToSend.getVersioneServizio()); 
	                                    servizioBusta.setAzione(bustaToSend.getAzione()); 
		
										// Dati integrazione
										repositoryBuste = new RepositoryBuste(openspcoopState.getStatoRichiesta(), true,protocolFactory);
										Integrazione infoIntegrazione = repositoryBuste.getInfoIntegrazioneFromOutBox(bustaToSend.getID());
										ProprietaErroreApplicativo erroreAppl = this.propertiesReader.getProprietaGestioneErrorePD(protocolFactory.createProtocolManager());
										erroreAppl.setDominio(identitaPdD.getCodicePorta());
		
										// RichiestaDelegata
										IDPortaDelegata idPD = this.configurazionePdDReader.getIDPortaDelegata(infoIntegrazione.getNomePorta(), protocolFactory);
	                                    RichiestaDelegata richiestaDelegata = new RichiestaDelegata(
	                                            idPD,infoIntegrazione.getServizioApplicativo(),
	                                            infoIntegrazione.getIdModuloInAttesa(),erroreAppl,identitaPdD);
										richiestaDelegata.setScenario(infoIntegrazione.getScenario());
										richiestaDelegata.setUtilizzoConsegnaAsincrona(true); // i riscontri sono utilizzati solo con il profilo oneway
										richiestaDelegata.setIdCollaborazione(bustaToSend.getCollaborazione());
										richiestaDelegata.setProfiloCollaborazione(bustaToSend.getProfiloDiCollaborazione(),bustaToSend.getProfiloDiCollaborazioneValue());
										try{
											richiestaDelegata.setIdCorrelazioneApplicativa(messaggioDaInviare.getIDCorrelazioneApplicativa());
										}catch(Exception e){}
		
										// Lettura servizio applicativo
										ServizioApplicativo sa = null;
										String servizioApplicativo = richiestaDelegata.getServizioApplicativo();
										try{
	                                        if(servizioApplicativo!=null){
	                                            IDServizioApplicativo idSA = new IDServizioApplicativo();
	                                            idSA.setNome(servizioApplicativo);
	                                            idSA.setIdSoggettoProprietario(richiestaDelegata.getIdSoggettoFruitore());
	                                            sa = this.configurazionePdDReader.getServizioApplicativo(idSA);
	                                        }
										}catch (Exception e) {
											if( !(e instanceof DriverConfigurazioneNotFound) || !(CostantiPdD.SERVIZIO_APPLICATIVO_ANONIMO.equals(servizioApplicativo)) ){
												throw e;
											}
										}
		
										String implementazioneMittente = this.registroServiziReader.getImplementazionePdD(new IDSoggetto(bustaToSend.getTipoMittente(),bustaToSend.getMittente()), null);
										String implementazioneDestinatario = this.registroServiziReader.getImplementazionePdD(new IDSoggetto(bustaToSend.getTipoDestinatario(),bustaToSend.getDestinatario()), null);
		
										// Gestione errore del servizio applicativo
										this.configurazionePdDReader.aggiornaProprietaGestioneErrorePD(erroreAppl,sa);
		
										// Profilo Gestione
										String profiloGestione = this.registroServiziReader.getProfiloGestioneFruizioneServizio(servizioBusta, null);
										richiestaDelegata.setProfiloGestione(profiloGestione);
		
										// Identificazione modalita di gestione (oneway 11 o 10)
										PortaDelegata pd = this.configurazionePdDReader.getPortaDelegata_SafeMethod(idPD);
										boolean oneWayStateless = this.configurazionePdDReader.isModalitaStateless(pd, bustaToSend.getProfiloDiCollaborazione());
										boolean oneWayVersione11 = this.propertiesReader.isGestioneOnewayStateful_1_1() && !oneWayStateless;
		
										// costruzione InoltroBuste_MEssage
										InoltroBusteMessage inoltroMSG = new InoltroBusteMessage();
										inoltroMSG.setRichiestaDelegata(richiestaDelegata);
										inoltroMSG.setBusta(bustaToSend);
										inoltroMSG.setOneWayVersione11(oneWayVersione11);
										if(oneWayVersione11){
											OpenSPCoopStateless stateless = new OpenSPCoopStateless();
											StatelessMessage statelessMessage = new StatelessMessage();
											statelessMessage.setBusta(bustaToSend);
											stateless.setStatoRichiesta(statelessMessage);
											inoltroMSG.setOpenspcoopstate(stateless);
										}
										inoltroMSG.setImplementazionePdDSoggettoMittente(implementazioneMittente);
										inoltroMSG.setImplementazionePdDSoggettoDestinatario(implementazioneDestinatario);
										inoltroMSG.setPddContext(pddContext);
		
										gestoreMsg = new GestoreMessaggi(openspcoopState, true, bustaToSend.getID(),Costanti.OUTBOX,this.msgDiag,pddContext);
		
										// Send Message on Queue
										if(CostantiConfigurazione.COMUNICAZIONE_INFRASTRUTTURALE_DB.equals(TimerGestoreBusteNonRiscontrateLib.tipoNodeSender)){
											gestoreMsg.ripristinaMessaggio();
										}else{
											try{
		
												String classTypeNodeSender = null;
												INodeSender nodeSender = null;
												try{
													classTypeNodeSender = ClassNameProperties.getInstance().getNodeSender(TimerGestoreBusteNonRiscontrateLib.tipoNodeSender);
													nodeSender = (INodeSender) Loader.getInstance().newInstance(classTypeNodeSender);
													AbstractCore.init(nodeSender, pddContext, protocolFactory);
												}catch(Exception e){
													throw new EJBUtilsException("Riscontrato errore durante il caricamento della classe ["+classTypeNodeSender+
															"] da utilizzare per la spedizione nell'infrastruttura: "+e.getMessage(),e);
												}
		
												nodeSender.send(inoltroMSG, InoltroBuste.ID_MODULO, this.msgDiag, 
														this.propertiesReader.getIdentitaPortaDefault(null),TimerGestoreBusteNonRiscontrate.ID_MODULO, bustaToSend.getID(), gestoreMsg);
											}catch(Exception e){
												this.msgDiag.logErroreGenerico(e,"GenericLib.nodeSender.send(InoltroBuste)");
												this.logTimer.error("Spedizione->InoltroBuste non riuscita",e);
												return;
											}
										}
		
									}
		
									if(this.logQuery)
										this.logTimer.debug("Gestita/Reinviata busta OneWay non riscontrata con ID ["+ bustaToSend.getID() + "]");
		
									gestiti++;
									
								}catch(Exception e){
									String msgErrore = "RespedizioneBustaNonRiscontrata ErroreGenerale("+idBustaDaRispedire+")";
									this.msgDiag.logErroreGenerico(e,msgErrore);
									this.logTimer.error(msgErrore+": "+e.getMessage(),e);
								}		
		
		
							}
		
						}
		
						if(this.logQuery)
							this.logTimer.info("Gestite "+gestiti+" buste con profilo oneway non riscontrate da rispedire");
		
						boolean cerca = true;
						try{
							GestoreMessaggi.updateLock(
									this.semaphore, connectionDB, this.timerLock,
									this.msgDiag, "Ricerca nuovo buste con profilo oneway non riscontrate ...");						
						}catch(Throwable e){
							this.msgDiag.logErroreGenerico(e,"RicercaNuoveBusteOneWayNonRiscontrate-UpdateLock");
							this.logTimer.error("ErroreRicercaNuoveBusteOneWayNonRiscontrate-UpdateLock: "+e.getMessage(),e);
							cerca = false;
						}
						
						// Check altri riscontri da inviare
						if(cerca) {
							offsetRiscontri = offsetRiscontri + busteOneWayToSend.size(); 
							busteOneWayToSend = 
									rBuste.getBustePerUlterioreInoltro(this.timeout, this.limit, offsetRiscontri, this.logQuery);
						}
						else {
							busteOneWayToSend = new ArrayList<BustaNonRiscontrata>(); // per uscire dal while
						}
						
					}
				}finally{
					try{
						GestoreMessaggi.releaseLock(
								this.semaphore, connectionDB, this.timerLock,
								this.msgDiag, causaBusteOneWayToSend);
					}catch(Exception e){}
				}
			}
			else {
				if(this.logQuery) {
					this.logTimer.info("Gestione buste, con profilo oneway, disabilitata");
				}	
			}





			/* ----- Gestione RicevuteAsincrone per profili Asincroni ----- */
			if(TimerState.ENABLED.equals(STATE_ASINCRONI)) {
				ProfiloDiCollaborazione pBuste = new ProfiloDiCollaborazione(openspcoopState.getStatoRichiesta(),this.logTimer,null);
				int offsetBusteAsincrone = 0;
				String causaBusteAsincroneToSend = "Rispedizione buste con profilo asincrono non riscontrate";
				try{
					GestoreMessaggi.acquireLock(
							this.semaphore, connectionDB, this.timerLock,
							this.msgDiag, causaBusteAsincroneToSend, 
							this.propertiesReader.getTimerGestoreRiscontriRicevute_getLockAttesaAttiva(), 
							this.propertiesReader.getTimerGestoreRiscontriRicevute_getLockCheckInterval());
					
					List<BustaNonRiscontrata> busteAsincroneToSend = null;
					busteAsincroneToSend = pBuste.asincrono_getBusteAsincronePerUlterioreInoltro(this.timeout,this.limit,offsetBusteAsincrone,this.logQuery);
					if(this.logQuery){
						if( (busteAsincroneToSend != null) && (busteAsincroneToSend.size()<=0)){
							this.logTimer.info("Non sono state trovate buste con profilo asincrono non riscontrate da rispedire");
						}
					}
		
					// Gestione buste da re-spedire
					while ( (busteAsincroneToSend != null) && (busteAsincroneToSend.size()>0) ) {
		
						if(this.logQuery)
							this.logTimer.info("Trovate "+busteAsincroneToSend.size()+" buste con profilo asincrono non riscontrate da rispedire ...");
		
						int gestiti = 0;
						for (int i = 0; i < busteAsincroneToSend.size(); i++) {
		
							BustaNonRiscontrata bustaNonRiscontrata = busteAsincroneToSend.get(i);
							String idBustaDaRispedire = bustaNonRiscontrata.getIdentificativo();
							this.msgDiag.addKeyword(CostantiPdD.KEY_ID_MESSAGGIO_RICHIESTA, idBustaDaRispedire);
							this.msgDiag.addKeyword(CostantiPdD.KEY_PROFILO_COLLABORAZIONE, bustaNonRiscontrata.getProfiloCollaborazione().name()); 
		
							// Busta da rispedire
							this.msgDiag.logPersonalizzato("ricevutaAsincronaNonRicevuta");
							if(this.logQuery){
								this.logTimer.debug(this.msgDiag.getMessaggio_replaceKeywords("ricevutaAsincronaNonRicevuta"));
							}
							
							try{
								GestoreMessaggi.updateLock(
										this.semaphore, connectionDB, this.timerLock,
										this.msgDiag, "Rispedizione busta con profilo asincrono con id ["+idBustaDaRispedire+"] ...");
							}catch(Throwable e){
								this.msgDiag.logErroreGenerico(e,"RispedizioneBustaNonRiscontrataAsincrona("+idBustaDaRispedire+")-UpdateLock");
								this.logTimer.error("ErroreRispedizioneBustaNonRiscontrataAsincrona("+idBustaDaRispedire+")-UpdateLock: "+e.getMessage(),e);
								break;
							}
		
							// Controllo che alla busta corrisponda effettivamente un messaggio nel repository
							messaggioDaInviare = new GestoreMessaggi(openspcoopState,true,idBustaDaRispedire,Costanti.OUTBOX,
									this.logTimer,this.msgDiag,null);
							if(messaggioDaInviare.existsMessage_noCache()==false){
								this.msgDiag.logPersonalizzato("ricevutaAsincronaNonRicevuta.messaggioNonEsistente");
								this.logTimer.error(this.msgDiag.getMessaggio_replaceKeywords("ricevutaAsincronaNonRicevuta.messaggioNonEsistente"));
								try{
									repositoryBuste = new RepositoryBuste(openspcoopState.getStatoRichiesta(), true,null);
									repositoryBuste.eliminaUtilizzoPdDFromOutBox(idBustaDaRispedire);
		
									//if(bustaToSend.getRiferimentoMessaggio()==null){
									// richiesta
									rollbackRepositoryBuste = new RollbackRepositoryBuste(idBustaDaRispedire,openspcoopState.getStatoRichiesta(),true);
									rollbackRepositoryBuste.rollbackBustaIntoOutBox();
									((StateMessage)openspcoopState.getStatoRichiesta()).executePreparedStatement();
									//}
		
									gestiti++;
									
								}catch(Exception e){
									this.msgDiag.logErroreGenerico(e,"EliminazioneBustaAsincronaNonEsistente("+idBustaDaRispedire+")");
									this.logTimer.error("ErroreEliminazioneBustaAsincronaNonEsistente("+idBustaDaRispedire+"): "+e.getMessage(),e);
								}
							}else{
		
								try{
		
									// PdDContext
									PdDContext pddContext = messaggioDaInviare.getPdDContext(true); // aggiorno anche l'istanza dentro l'oggetto messaggioDaInviare stesso.;
									IProtocolFactory<?> protocolFactory = this.protocolFactoryManager.getProtocolFactoryByName((String) pddContext.getObject(org.openspcoop2.core.constants.Costanti.PROTOCOL_NAME));
		
									// Recupero busta
									repositoryBuste = new RepositoryBuste(openspcoopState.getStatoRichiesta(),this.logTimer, true,protocolFactory);
									Busta bustaToSend = repositoryBuste.getBustaFromOutBox(idBustaDaRispedire);
									this.msgDiag.addKeywords(bustaToSend, true);	
		
									// Controllo se la busta e' scaduta
									Date scadenza = bustaToSend.getScadenza();
									Timestamp now = DateManager.getTimestamp();
		
									// Eventuale msg Scaduto.
									if (scadenza.before(now)) {
										// Busta scaduta
		
										this.msgDiag.logPersonalizzato("ricevutaAsincronaNonRicevuta.bustaScaduta");
										if(this.logQuery){
											this.logTimer.info(this.msgDiag.getMessaggio_replaceKeywords("ricevutaAsincronaNonRicevuta.bustaScaduta"));
										}
										try{
											//if(bustaToSend.getRiferimentoMessaggio()==null){
											// richiesta
											rollbackRepositoryBuste = new RollbackRepositoryBuste(bustaToSend.getID(),openspcoopState.getStatoRichiesta(),true);
											rollbackRepositoryBuste.rollbackBustaIntoOutBox();
											((StateMessage)openspcoopState.getStatoRichiesta()).executePreparedStatement();
											//}
											gestoreMsg = new GestoreMessaggi(openspcoopState, true, bustaToSend.getID(),Costanti.OUTBOX,this.msgDiag,pddContext);
											gestoreMsg.logicDeleteMessage();
										}catch(Exception e){
											this.msgDiag.logErroreGenerico(e,"EliminazioneBustaAsincronaScaduta("+bustaToSend.getID()+")");
											this.logTimer.error("ErroreEliminazioneBustaAsincronaScaduta("+bustaToSend.getID()+"): "+e.getMessage(),e);
										}
		
									}
									else {
										// Busta da rispedire
		
										// Dati
										IDSoggetto soggettoBustaNonRiscontrata = new IDSoggetto(bustaToSend.getTipoMittente(),
												bustaToSend.getMittente());
										IDSoggetto identitaPdD = null;
										String dominioRD = null;
										try{
											dominioRD = this.configurazionePdDReader.getIdentificativoPorta(soggettoBustaNonRiscontrata,protocolFactory);
											if(dominioRD==null){
												throw new Exception("Dominio is null");
											}
										}catch(Exception e){
											this.msgDiag.logErroreGenerico(e,"BustaAsincrona getDominio("+soggettoBustaNonRiscontrata+")");
											this.logTimer.error("ErroreBustaAsincrona getDominio("+soggettoBustaNonRiscontrata+"): "+e.getMessage(),e);
										}
										if(dominioRD==null){
											identitaPdD = this.propertiesReader.getIdentitaPortaDefault(null);
										}else{
											identitaPdD = new IDSoggetto(bustaToSend.getTipoMittente(),
													bustaToSend.getMittente(),dominioRD);
										}
	                                    IDServizio servizioBusta = IDServizioFactory.getInstance().getIDServizioFromValues(bustaToSend.getTipoServizio(),
	                                            bustaToSend.getServizio(),
	                                            bustaToSend.getTipoDestinatario(), 
	                                            bustaToSend.getDestinatario(), 
	                                            bustaToSend.getVersioneServizio()); 
	                                    servizioBusta.setAzione(bustaToSend.getAzione());
		
										// Dati integrazione
										repositoryBuste = new RepositoryBuste(openspcoopState.getStatoRichiesta(), true,protocolFactory);
										Integrazione infoIntegrazione = repositoryBuste.getInfoIntegrazioneFromOutBox(bustaToSend.getID());
										ProprietaErroreApplicativo erroreAppl = this.propertiesReader.getProprietaGestioneErrorePD(protocolFactory.createProtocolManager());
										erroreAppl.setDominio(identitaPdD.getCodicePorta());
		
										// RichiestaDelegata
	                                    IDPortaDelegata idPD = this.configurazionePdDReader.getIDPortaDelegata(infoIntegrazione.getNomePorta(), protocolFactory);
	                                    RichiestaDelegata richiestaDelegata = new RichiestaDelegata(idPD,infoIntegrazione.getServizioApplicativo(),
	                                                    infoIntegrazione.getIdModuloInAttesa(),erroreAppl,identitaPdD);
										richiestaDelegata.setScenario(infoIntegrazione.getScenario());
										if(org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.ASINCRONO_SIMMETRICO.equals(bustaToSend.getProfiloDiCollaborazione()) &&
												bustaToSend.getRiferimentoMessaggio() ==null)
											richiestaDelegata.setUtilizzoConsegnaAsincrona(false); // una richiesta asincrona simmetrica non deve utilizzare la consegna asincrona della ricevuta applicativa/ok
										else
											richiestaDelegata.setUtilizzoConsegnaAsincrona(true); 
										richiestaDelegata.setRicevutaAsincrona(false); // solo in caso di non-gestione della ricevuta applicativa, viene attivato il timer
		
										// Lettura servizio applicativo
										ServizioApplicativo sa = null;
										String servizioApplicativo = richiestaDelegata.getServizioApplicativo();
										try{
	                                        if(servizioApplicativo!=null){
	                                            IDServizioApplicativo idSA = new IDServizioApplicativo();
	                                            idSA.setNome(servizioApplicativo);
	                                            idSA.setIdSoggettoProprietario(richiestaDelegata.getIdSoggettoFruitore());
	                                            sa = this.configurazionePdDReader.getServizioApplicativo(idSA);
	                                        }
										}catch (Exception e) {
											if( !(e instanceof DriverConfigurazioneNotFound) || !(CostantiPdD.SERVIZIO_APPLICATIVO_ANONIMO.equals(servizioApplicativo)) ){
												throw e;
											}
										}
		
										// Aggiornamento informazioni integrazioni servizio applicativo
										this.configurazionePdDReader.aggiornaProprietaGestioneErrorePD(erroreAppl,sa);
		
										// costruzione InoltroBuste_MEssage
										InoltroBusteMessage inoltroMSG = new InoltroBusteMessage();
										inoltroMSG.setRichiestaDelegata(richiestaDelegata);
										inoltroMSG.setBusta(bustaToSend);
		
										gestoreMsg = new GestoreMessaggi(openspcoopState, true, bustaToSend.getID(),Costanti.OUTBOX,this.msgDiag,pddContext);
										inoltroMSG.setPddContext(pddContext);
		
										// Send Message on Queue
										if(CostantiConfigurazione.COMUNICAZIONE_INFRASTRUTTURALE_DB.equals(TimerGestoreBusteNonRiscontrateLib.tipoNodeSender)){
											gestoreMsg.ripristinaMessaggio();
										}else{
											try{
		
												String classTypeNodeSender = null;
												INodeSender nodeSender = null;
												try{
													classTypeNodeSender = ClassNameProperties.getInstance().getNodeSender(TimerGestoreBusteNonRiscontrateLib.tipoNodeSender);
													nodeSender = (INodeSender) Loader.getInstance().newInstance(classTypeNodeSender);
													AbstractCore.init(nodeSender, pddContext, protocolFactory);
												}catch(Exception e){
													throw new EJBUtilsException("Riscontrato errore durante il caricamento della classe ["+classTypeNodeSender+
															"] da utilizzare per la spedizione nell'infrastruttura: "+e.getMessage(),e);
												}
		
												nodeSender.send(inoltroMSG, InoltroBuste.ID_MODULO, this.msgDiag, 
														this.propertiesReader.getIdentitaPortaDefault(null),TimerGestoreBusteNonRiscontrate.ID_MODULO, bustaToSend.getID(), gestoreMsg);
											}catch(Exception e){
												this.msgDiag.logErroreGenerico(e,"Asincrono GenericLib.nodeSender.send(InoltroBuste)");
												this.logTimer.error("Spedizione->InoltroBuste non riuscita",e);
												return;
											}
										}
									}
		
									if(this.logQuery){
										this.logTimer.info("Gestita/Reinviata busta asincrona, la cui ricevuta non e' pervenuta, con ID ["+ bustaToSend.getID() + "]");
									}
		
									gestiti++;
		
								}catch(Exception e){
									String msgErrore = "RespedizioneBustaAsincrona ErroreGenerale("+idBustaDaRispedire+")";
									this.msgDiag.logErroreGenerico(e,msgErrore);
									this.logTimer.error(msgErrore+": "+e.getMessage(),e);
								}
							}
		
						}
		
						if(this.logQuery)
							this.logTimer.info("Gestite "+gestiti+" buste con profilo asincrono non riscontrate da rispedire");
		
						boolean cerca = true;
						try{
							GestoreMessaggi.updateLock(
									this.semaphore, connectionDB, this.timerLock,
									this.msgDiag, "Ricerca nuovo buste con profilo asincrono non riscontrate ...");						
						}catch(Throwable e){
							this.msgDiag.logErroreGenerico(e,"RicercaNuoveBusteAsincroneNonRiscontrate-UpdateLock");
							this.logTimer.error("ErroreRicercaNuoveBusteAsincroneNonRiscontrate-UpdateLock: "+e.getMessage(),e);
							cerca = false;
						}
						
						// Check altri riscontri da inviare
						if(cerca) {					
							// Check altre buste da inviare
							offsetBusteAsincrone = offsetBusteAsincrone + busteAsincroneToSend.size();
							busteAsincroneToSend = 
									pBuste.asincrono_getBusteAsincronePerUlterioreInoltro(this.timeout,this.limit,offsetBusteAsincrone,this.logQuery);
						}
						else {
							busteAsincroneToSend = new ArrayList<BustaNonRiscontrata>(); // per uscire dal while
						}
					}
				}finally{
					try{
						GestoreMessaggi.releaseLock(
								this.semaphore, connectionDB, this.timerLock,
								this.msgDiag, causaBusteAsincroneToSend);
					}catch(Exception e){}
				}
			}
			else {
				if(this.logQuery) {
					this.logTimer.info("Gestione buste, con profilo asincrono, disabilitata");
				}	
			}
			

			// end
			long endControlloBusteNonRiscontrate = DateManager.getTimeMillis();
			long diff = (endControlloBusteNonRiscontrate-startControlloBusteNonRiscontrate);
			this.logTimer.info("Controllo Buste in attesa di riscontro terminato in "+Utilities.convertSystemTimeIntoString_millisecondi(diff, true));

		} 
		catch(TimerLockNotAvailableException t) {
			// msg diagnostico emesso durante l'emissione dell'eccezione
			this.logTimer.info(t.getMessage(),t);
		}
		catch (Exception e) {
			this.msgDiag.logErroreGenerico(e,"GestioneBusteNonRiscontrate");
			this.logTimer.error("Riscontrato errore durante la gestione delle buste non riscontrate: "+ e.getMessage(),e);
			return;
		}finally{
			if(openspcoopState!=null)
				openspcoopState.releaseResource();
		}
	}

}
