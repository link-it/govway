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



package org.openspcoop2.pdd.timers;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Vector;

import org.slf4j.Logger;
import org.openspcoop2.core.id.IDPortaApplicativaByNome;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
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
import org.openspcoop2.utils.date.DateManager;


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
	
	public TimerConsegnaContenutiApplicativi(MsgDiagnostico msgDiag,Logger log,OpenSPCoop2Properties p,
			boolean logQuery,
			int limit,boolean orderByQuery,ConfigurazionePdDManager configurazionePdDReader,RegistroServiziManager registroServiziReader,
			long timeout){
		 this.msgDiag = msgDiag;
		 this.logTimer = log;
		 this.propertiesReader = p;
		 this.logQuery = logQuery;
		 this.limit = limit;
		 this.orderByQuery = orderByQuery;
		 this.timeout = timeout;
		 
		 this.configurazionePdDReader = configurazionePdDReader;
		 this.registroServiziReader = registroServiziReader;
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
		if( TimerMonitoraggioRisorse.risorseDisponibili == false){
			this.logTimer.error("["+TimerConsegnaContenutiApplicativiThread.ID_MODULO+"] Risorse di sistema non disponibili: "+TimerMonitoraggioRisorse.risorsaNonDisponibile.getMessage(),TimerMonitoraggioRisorse.risorsaNonDisponibile);
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
				Vector<MessaggioServizioApplicativo> msgDaRiconsegnareINBOX = null;
                try{
                	GestoreMessaggi.acquireLock(this.msgDiag, causaMessaggiINBOXDaRiconsegnare, 
                			this.propertiesReader.getMsgGiaInProcessamento_AttesaAttiva(), 
                			this.propertiesReader.getMsgGiaInProcessamento_CheckInterval());
                	
                	msgDaRiconsegnareINBOX = gestoreMsgSearch.readMessaggiDaRiconsegnareIntoBox(this.limit,this.logQuery,this.orderByQuery,now);
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
								Busta bustaToSend = repositoryBuste.getBustaFromInBox(idMsgDaInoltrare);
								this.msgDiag.addKeywords(bustaToSend, true);
								
								IProtocolFactory protocolFactory = ProtocolFactoryManager.getInstance().getProtocolFactoryByName(bustaToSend.getProtocollo());
								
								String implementazioneMittente = this.registroServiziReader.getImplementazionePdD(new IDSoggetto(bustaToSend.getTipoMittente(),bustaToSend.getMittente()), null);
								String implementazioneDestinatario = this.registroServiziReader.getImplementazionePdD(new IDSoggetto(bustaToSend.getTipoDestinatario(),bustaToSend.getDestinatario()), null);
										
								messaggioDaInviare = new GestoreMessaggi(openspcoopstateGestore,true,idMsgDaInoltrare,Costanti.INBOX,
										this.logTimer,this.msgDiag,null);
								PdDContext pddContext = messaggioDaInviare.getPdDContext();
								
								IDSoggetto soggettoFruitore = new IDSoggetto(bustaToSend.getTipoMittente(),
										bustaToSend.getMittente());
								
								IDServizio servizioBusta = new IDServizio(bustaToSend.getTipoDestinatario(),
										bustaToSend.getDestinatario(),
										bustaToSend.getTipoServizio(),
										bustaToSend.getServizio(),
										bustaToSend.getAzione());
								
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
								
								IDPortaApplicativaByNome idPAbyNome = new IDPortaApplicativaByNome();
								idPAbyNome.setNome(msgServizioApplicativo.getNomePorta());
								idPAbyNome.setSoggetto(servizioBusta.getSoggettoErogatore());
								RichiestaApplicativa richiestaApplicativa = new RichiestaApplicativa(soggettoFruitore, servizioBusta, identitaPdD, idPAbyNome);
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
		                	this.logTimer.info("Inoltrati "+msgDaRiconsegnareINBOX.size()+" messaggi letti dal repository (INBOX)");
					}
					else{
						if(this.logQuery)
							this.logTimer.info("Non sono stati trovati messaggi da re-inoltrare verso il modulo ConsegnaContenutiApplicativi nel repository (INBOX)");
					}
                }finally{
    				try{
    					GestoreMessaggi.releaseLock(this.msgDiag, causaMessaggiINBOXDaRiconsegnare);
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
            this.logTimer.info("Controllo Repository Messaggi (Riconsegna verso ConsegnaContenutiApplicativi) terminata in "+((endControlloRepositoryMessaggi-startControlloRepositoryMessaggi)/1000) +" secondi");

			
		} catch (Exception e) {
			this.msgDiag.logErroreGenerico(e,"GestioneMessaggiRiconsegnaConsegnaContenutiApplicativi");
			this.logTimer.error("Riscontrato errore durante la gestione del repository dei messaggi (Riconsegna verso ConsegnaContenutiApplicativi): "+ e.getMessage(),e);
		}finally{
			if(openspcoopstateGestore!=null)
				openspcoopstateGestore.releaseResource();
		}
	}

}
