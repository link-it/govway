/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it). 
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
import java.util.Date;
import java.util.List;

import org.openspcoop2.core.id.IDPortaApplicativa;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.driver.IDServizioFactory;
import org.openspcoop2.pdd.config.ConfigurazionePdDManager;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.config.RichiestaApplicativa;
import org.openspcoop2.pdd.core.CostantiPdD;
import org.openspcoop2.pdd.core.GestoreMessaggi;
import org.openspcoop2.pdd.core.MessaggioServizioApplicativo;
import org.openspcoop2.pdd.core.PdDContext;
import org.openspcoop2.pdd.core.behaviour.BehaviourForwardToConfiguration;
import org.openspcoop2.pdd.core.behaviour.StatoFunzionalita;
import org.openspcoop2.pdd.core.state.OpenSPCoopStateful;
import org.openspcoop2.pdd.logger.MsgDiagnostico;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.pdd.mdb.ConsegnaContenutiApplicativi;
import org.openspcoop2.pdd.mdb.ConsegnaContenutiApplicativiMessage;
import org.openspcoop2.pdd.mdb.EsitoLib;
import org.openspcoop2.pdd.services.OpenSPCoop2Startup;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.engine.constants.Costanti;
import org.openspcoop2.protocol.engine.driver.RepositoryBuste;
import org.openspcoop2.protocol.registry.RegistroServiziManager;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.state.StateMessage;
import org.openspcoop2.utils.TipiDatabase;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.id.serial.InfoStatistics;
import org.openspcoop2.utils.semaphore.Semaphore;
import org.openspcoop2.utils.semaphore.SemaphoreConfiguration;
import org.openspcoop2.utils.semaphore.SemaphoreMapping;
import org.slf4j.Logger;



/**
 * Timer che si occupa di re-inoltrare i messaggi in riconsegna
 * 
 *  
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class TimerConsegnaContenutiApplicativi  {


	private MsgDiagnostico msgDiag = null;
	private Logger logTimer = null;
	private OpenSPCoop2Properties propertiesReader = null;
	private boolean logQuery = false;
	private int limit = CostantiPdD.LIMIT_MESSAGGI_GESTORI;
	private boolean orderByQuery;
	private long timeout;

	private RegistroServiziManager registroServiziReader;
	private ConfigurazionePdDManager configurazionePdDReader;

	private TimerLock timerLock = null;

	/** Semaforo */
	private Semaphore semaphore = null;
	private InfoStatistics semaphore_statistics;

	public TimerConsegnaContenutiApplicativi(MsgDiagnostico msgDiag,Logger log,OpenSPCoop2Properties p,
			boolean logQuery,
			int limit,boolean orderByQuery,ConfigurazionePdDManager configurazionePdDReader,RegistroServiziManager registroServiziReader,
			long timeout) throws TimerException{
		this.msgDiag = msgDiag;
		this.logTimer = log;
		this.propertiesReader = p;
		this.logQuery = logQuery;
		this.limit = limit;
		this.orderByQuery = orderByQuery;
		this.timeout = timeout;

		this.configurazionePdDReader = configurazionePdDReader;
		this.registroServiziReader = registroServiziReader;

		// deve essere utilizzato lo stesso lock per GestoreMessaggi, ConsegnaContenuti, GestoreBuste per risolvere problema di eliminazione descritto in GestoreMessaggi metodo deleteMessageWithLock 
		this.timerLock = new TimerLock(TipoLock.GESTIONE_REPOSITORY_MESSAGGI);

		if(this.propertiesReader.isTimerLockByDatabase()) {
			this.semaphore_statistics = new InfoStatistics();

			SemaphoreConfiguration config = GestoreMessaggi.newSemaphoreConfiguration(this.propertiesReader.getTimerConsegnaContenutiApplicativi_lockMaxLife(), 
					this.propertiesReader.getTimerConsegnaContenutiApplicativi_lockIdleTime());

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
			this.logTimer.error("["+TimerConsegnaContenutiApplicativiThread.ID_MODULO+"] Rilevato sistema in shutdown");
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
			this.logTimer.error("["+TimerConsegnaContenutiApplicativiThread.ID_MODULO+"] Risorse di sistema non disponibili: "+TimerMonitoraggioRisorseThread.risorsaNonDisponibile.getMessage(),TimerMonitoraggioRisorseThread.risorsaNonDisponibile);
			return;
		}
		if( MsgDiagnostico.gestoreDiagnosticaDisponibile == false){
			this.logTimer.error("["+TimerConsegnaContenutiApplicativiThread.ID_MODULO+"] Sistema di diagnostica non disponibile: "+MsgDiagnostico.motivoMalfunzionamentoDiagnostici.getMessage(),MsgDiagnostico.motivoMalfunzionamentoDiagnostici);
			return;
		}

		// Prendo la gestione
		this.msgDiag.logPersonalizzato("controlloInCorso");
		this.logTimer.info(this.msgDiag.getMessaggio_replaceKeywords("controlloInCorso"));
		long startControlloRepositoryMessaggi = DateManager.getTimeMillis();



		OpenSPCoopStateful openspcoopstateGestore = new OpenSPCoopStateful();
		try {

			openspcoopstateGestore.initResource(this.propertiesReader.getIdentitaPortaDefault(null),TimerConsegnaContenutiApplicativiThread.ID_MODULO, null);
			Connection connectionDB = ((StateMessage)openspcoopstateGestore.getStatoRichiesta()).getConnectionDB();

			// Messaggi da eliminare 
			GestoreMessaggi gestoreMsgSearch = new GestoreMessaggi(openspcoopstateGestore, true,this.logTimer,this.msgDiag, null);
			RepositoryBuste repositoryBuste = new RepositoryBuste(openspcoopstateGestore.getStatoRichiesta(), true,null);
			boolean trovatiMessaggi = true;

			Date now = DateManager.getDate();

			while(trovatiMessaggi){

				trovatiMessaggi = false;


				/* --- Messaggi da re-inoltrare --- */ 

				// ReInoltro Messaggi from INBOX
				String causaMessaggiINBOXDaRiconsegnare = "Messaggi da riconsegnare verso il modulo ConsegnaContenutiApplicativi";
				List<MessaggioServizioApplicativo> msgDaRiconsegnareINBOX = null;
				try{
					GestoreMessaggi.acquireLock(
							this.semaphore, connectionDB, this.timerLock,
							this.msgDiag, causaMessaggiINBOXDaRiconsegnare, 
							this.propertiesReader.getMsgGiaInProcessamento_AttesaAttiva(), 
							this.propertiesReader.getMsgGiaInProcessamento_CheckInterval());

					msgDaRiconsegnareINBOX = gestoreMsgSearch.readMessaggiDaRiconsegnareIntoBox(this.limit,this.logQuery,this.orderByQuery,now);
					int riconsegnati = 0;
					if(msgDaRiconsegnareINBOX.size()>0){
						if(this.logQuery)
							this.logTimer.info("Trovati "+msgDaRiconsegnareINBOX.size()+" messaggi da inoltrare al modulo ConsegnaContenutiApplicativi (INBOX) ...");
						trovatiMessaggi = true;

						this.msgDiag.addKeyword(CostantiPdD.KEY_TIPO_MESSAGGIO,Costanti.INBOX);
						for(int i=0; i<msgDaRiconsegnareINBOX.size(); i++){

							MessaggioServizioApplicativo msgServizioApplicativo = msgDaRiconsegnareINBOX.get(i);
							String servizioApplicativo = msgServizioApplicativo.getServizioApplicativo();
							String idMsgDaInoltrare = msgServizioApplicativo.getIdMessaggio();
							this.msgDiag.addKeyword(CostantiPdD.KEY_ID_MESSAGGIO_DA_INOLTRARE,idMsgDaInoltrare);
							this.msgDiag.addKeyword(CostantiPdD.KEY_SA_EROGATORE,servizioApplicativo);

							GestoreMessaggi messaggioDaInviare = null;
							OpenSPCoopStateful openspcoopstateMesssaggio = null;
							try{
								try{
									GestoreMessaggi.updateLock(
											this.semaphore, connectionDB, this.timerLock,
											this.msgDiag, "Gestione riconsegna messaggio con id ["+idMsgDaInoltrare+"] ...");
								}catch(Throwable e){
									this.msgDiag.logErroreGenerico(e,"InoltroMessaggioInbox("+idMsgDaInoltrare+")-UpdateLock");
									this.logTimer.error("ErroreInoltroMessaggioInbox("+idMsgDaInoltrare+")-UpdateLock: "+e.getMessage(),e);
									break;
								}

								Busta bustaToSend = repositoryBuste.getBustaFromInBox(idMsgDaInoltrare);
								this.msgDiag.addKeywords(bustaToSend, true);

								IProtocolFactory<?> protocolFactory = ProtocolFactoryManager.getInstance().getProtocolFactoryByName(bustaToSend.getProtocollo());

								String implementazioneMittente = this.registroServiziReader.getImplementazionePdD(new IDSoggetto(bustaToSend.getTipoMittente(),bustaToSend.getMittente()), null);
								String implementazioneDestinatario = this.registroServiziReader.getImplementazionePdD(new IDSoggetto(bustaToSend.getTipoDestinatario(),bustaToSend.getDestinatario()), null);

								messaggioDaInviare = new GestoreMessaggi(openspcoopstateGestore,true,idMsgDaInoltrare,Costanti.INBOX,
										this.logTimer,this.msgDiag,null);
								PdDContext pddContext = messaggioDaInviare.getPdDContext();

								IDSoggetto soggettoFruitore = new IDSoggetto(bustaToSend.getTipoMittente(),
										bustaToSend.getMittente());

								IDServizio servizioBusta = IDServizioFactory.getInstance().getIDServizioFromValues(bustaToSend.getTipoServizio(),
										bustaToSend.getServizio(),
										bustaToSend.getTipoDestinatario(), 
										bustaToSend.getDestinatario(), 
										bustaToSend.getVersioneServizio()); 
								servizioBusta.setAzione(bustaToSend.getAzione());	

								IDSoggetto identitaPdD = null;
								String dominioRD = null;
								try{
									dominioRD = this.configurazionePdDReader.getIdentificativoPorta(servizioBusta.getSoggettoErogatore(),protocolFactory);
									if(dominioRD==null){
										throw new Exception("Dominio is null");
									}
								}catch(Exception e){
									this.msgDiag.logErroreGenerico(e,"BustaNonRiscontrata getDominio("+servizioBusta.getSoggettoErogatore()+")");
								}
								if(dominioRD==null){
									identitaPdD = this.propertiesReader.getIdentitaPortaDefault(null);
								}else{
									identitaPdD = new IDSoggetto(bustaToSend.getTipoDestinatario(),
											bustaToSend.getDestinatario(),dominioRD);
								}

								IDPortaApplicativa idPA = this.configurazionePdDReader.getIDPortaApplicativa(msgServizioApplicativo.getNomePorta(), protocolFactory);
								RichiestaApplicativa richiestaApplicativa = new RichiestaApplicativa(soggettoFruitore, identitaPdD, idPA);
								richiestaApplicativa.setServizioApplicativo(servizioApplicativo);

								ConsegnaContenutiApplicativiMessage consegnaMSG = new ConsegnaContenutiApplicativiMessage();
								consegnaMSG.setBusta(bustaToSend);
								consegnaMSG.setOneWayVersione11(false);
								consegnaMSG.setStateless(true);
								consegnaMSG.setImplementazionePdDSoggettoMittente(implementazioneMittente);
								consegnaMSG.setImplementazionePdDSoggettoDestinatario(implementazioneDestinatario);
								consegnaMSG.setPddContext(pddContext);
								consegnaMSG.setIdMessaggioPreBehaviour(bustaToSend.getRiferimentoMessaggio());
								BehaviourForwardToConfiguration behaviourForwardToConfiguration = new BehaviourForwardToConfiguration();
								if(msgServizioApplicativo.isSbustamentoSoap())
									behaviourForwardToConfiguration.setSbustamentoSoap(StatoFunzionalita.ABILITATA);
								else 
									behaviourForwardToConfiguration.setSbustamentoSoap(StatoFunzionalita.DISABILITATA);
								if(msgServizioApplicativo.isSbustamentoInformazioniProtocollo())
									behaviourForwardToConfiguration.setSbustamentoInformazioniProtocollo(StatoFunzionalita.ABILITATA);
								else 
									behaviourForwardToConfiguration.setSbustamentoInformazioniProtocollo(StatoFunzionalita.DISABILITATA);
								consegnaMSG.setBehaviourForwardToConfiguration(behaviourForwardToConfiguration);

								consegnaMSG.setRichiestaApplicativa(richiestaApplicativa);

								ConsegnaContenutiApplicativi lib = new ConsegnaContenutiApplicativi(OpenSPCoop2Logger.getLoggerOpenSPCoopCore());
								openspcoopstateMesssaggio = new OpenSPCoopStateful();
								// viene inizializzata dentro il modulo ConsegnaContenutiApplicativi
								//openspcoopstateMesssaggio.initResource(identitaPdD,TimerConsegnaContenutiApplicativiThread.ID_MODULO, bustaToSend.getID());
								openspcoopstateMesssaggio.setMessageLib(consegnaMSG);
								EsitoLib result = lib.onMessage(openspcoopstateMesssaggio);
								if(this.logQuery)
									this.logTimer.debug("Invocato ConsegnaContenutiApplicativi per ["+bustaToSend.getID()+
											"] con esito: "+result.getStatoInvocazione(),result.getErroreNonGestito());
								if(EsitoLib.ERRORE_NON_GESTITO==result.getStatoInvocazione()){
									// per evitare i loop infinito
									if(result.isDataRispedizioneAggiornata()==false){
										messaggioDaInviare.aggiornaDataRispedizione(new Timestamp(DateManager.getTimeMillis()+(this.timeout*1000)), servizioApplicativo);
									}
									if(result.isErroreProcessamentoMessaggioAggiornato()==false){
										if(result.getErroreNonGestito()!=null){
											messaggioDaInviare.aggiornaErroreProcessamentoMessaggio("["+TimerConsegnaContenutiApplicativiThread.ID_MODULO+"] "+
													result.getErroreNonGestito().getMessage(), servizioApplicativo);
										}
										else if(result.getMotivazioneErroreNonGestito()!=null){
											messaggioDaInviare.aggiornaErroreProcessamentoMessaggio("["+TimerConsegnaContenutiApplicativiThread.ID_MODULO+"] "+
													result.getMotivazioneErroreNonGestito(), servizioApplicativo);
										}
										else{
											messaggioDaInviare.aggiornaErroreProcessamentoMessaggio("["+TimerConsegnaContenutiApplicativiThread.ID_MODULO+"] "+
													"Invocato ConsegnaContenutiApplicativi per ["+bustaToSend.getID()+
													"] con esito: "+result.getStatoInvocazione(), servizioApplicativo);
										}
									}
								}
								else{

									this.msgDiag.logPersonalizzato("inoltroMessaggio");
									if(this.logQuery)
										this.logTimer.debug(this.msgDiag.getMessaggio_replaceKeywords("inoltroMessaggio"));

								}

								riconsegnati++;

							}catch(Exception e){
								this.msgDiag.logErroreGenerico(e,"InoltroMessaggioInbox("+idMsgDaInoltrare+")");
								this.logTimer.error("ErroreInoltroMessaggioInbox("+idMsgDaInoltrare+"): "+e.getMessage(),e);
								// per evitare i loop infinito
								messaggioDaInviare.aggiornaDataRispedizione(new Timestamp(DateManager.getTimeMillis()+(this.timeout*1000)), servizioApplicativo);
								messaggioDaInviare.aggiornaErroreProcessamentoMessaggio("["+TimerConsegnaContenutiApplicativiThread.ID_MODULO+"] "+e.getMessage(), servizioApplicativo);
							}finally{
								try{
									if(openspcoopstateMesssaggio!=null && !openspcoopstateMesssaggio.resourceReleased()){
										openspcoopstateMesssaggio.releaseResource();
									}
								}catch(Exception e){}
							}
						}

						if(this.logQuery)
							this.logTimer.info("Inoltrati "+riconsegnati+" messaggi letti dal repository (INBOX)");
					}
					else{
						if(this.logQuery)
							this.logTimer.info("Non sono stati trovati messaggi da re-inoltrare verso il modulo ConsegnaContenutiApplicativi nel repository (INBOX)");
					}
				}finally{
					try{
						GestoreMessaggi.releaseLock(
								this.semaphore, connectionDB, this.timerLock,
								this.msgDiag, causaMessaggiINBOXDaRiconsegnare);
					}catch(Exception e){}
				}








				// log finale  
				if(trovatiMessaggi){
					this.msgDiag.addKeyword(CostantiPdD.KEY_TIMER_CONSEGNA_CONTENUTI_APPLICATIVI_NUMERO_MESSAGGI_INOLTRATI, msgDaRiconsegnareINBOX.size()+"");
					this.msgDiag.logPersonalizzato("ricercaMessaggiDaInoltrare");
				}

			}



			// end
			long endControlloRepositoryMessaggi = DateManager.getTimeMillis();
			long diff = (endControlloRepositoryMessaggi-startControlloRepositoryMessaggi);
			this.logTimer.info("Controllo Repository Messaggi (Riconsegna verso ConsegnaContenutiApplicativi) terminato in "+Utilities.convertSystemTimeIntoString_millisecondi(diff, true));

		} 
		catch(TimerLockNotAvailableException t) {
			// msg diagnostico emesso durante l'emissione dell'eccezione
			this.logTimer.info(t.getMessage(),t);
		}
		catch (Exception e) {
			this.msgDiag.logErroreGenerico(e,"GestioneMessaggiRiconsegnaConsegnaContenutiApplicativi");
			this.logTimer.error("Riscontrato errore durante la gestione del repository dei messaggi (Riconsegna verso ConsegnaContenutiApplicativi): "+ e.getMessage(),e);
		}finally{
			if(openspcoopstateGestore!=null)
				openspcoopstateGestore.releaseResource();
		}
	}

}
