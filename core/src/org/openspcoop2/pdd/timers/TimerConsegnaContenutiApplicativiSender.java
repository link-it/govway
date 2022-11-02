/*
 * GovWay - A customizable API Gateway
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it). 
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

import java.sql.Timestamp;
import java.util.Date;

import org.openspcoop2.core.config.GestioneErrore;
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaApplicativaServizioApplicativo;
import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDPortaApplicativa;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.core.registry.driver.IDServizioFactory;
import org.openspcoop2.core.transazioni.IdTransazioneApplicativoServer;
import org.openspcoop2.pdd.config.ConfigurazioneCoda;
import org.openspcoop2.pdd.config.ConfigurazionePdDManager;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.config.RichiestaApplicativa;
import org.openspcoop2.pdd.core.CostantiPdD;
import org.openspcoop2.pdd.core.GestoreMessaggi;
import org.openspcoop2.pdd.core.MessaggioServizioApplicativo;
import org.openspcoop2.pdd.core.PdDContext;
import org.openspcoop2.pdd.core.behaviour.BehaviourForwardToConfiguration;
import org.openspcoop2.pdd.core.behaviour.StatoFunzionalita;
import org.openspcoop2.pdd.core.behaviour.built_in.multi_deliver.ConfigurazioneGestioneConsegnaNotifiche;
import org.openspcoop2.pdd.core.behaviour.built_in.multi_deliver.GestioneConsegnaNotificheUtils;
import org.openspcoop2.pdd.core.behaviour.built_in.multi_deliver.MultiDeliverUtils;
import org.openspcoop2.pdd.core.state.OpenSPCoopStateDBManager;
import org.openspcoop2.pdd.core.state.OpenSPCoopStateful;
import org.openspcoop2.pdd.logger.MsgDiagnosticiProperties;
import org.openspcoop2.pdd.logger.MsgDiagnostico;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.pdd.mdb.ConsegnaContenutiApplicativi;
import org.openspcoop2.pdd.mdb.ConsegnaContenutiApplicativiBehaviourMessage;
import org.openspcoop2.pdd.mdb.ConsegnaContenutiApplicativiMessage;
import org.openspcoop2.pdd.mdb.EsitoLib;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.engine.constants.Costanti;
import org.openspcoop2.protocol.engine.driver.RepositoryBuste;
import org.openspcoop2.protocol.registry.RegistroServiziManager;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.state.RequestInfo;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.threads.IRunnableInstance;
import org.openspcoop2.utils.threads.RunnableLogger;

/**
 * Timer che si occupa di re-inoltrare i messaggi in riconsegna
 * 
 *  
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class TimerConsegnaContenutiApplicativiSender implements IRunnableInstance{

	private OpenSPCoop2Properties propertiesReader = null;
	private RegistroServiziManager registroServiziReader;
	private ConfigurazionePdDManager configurazionePdDReader;
	
	private MessaggioServizioApplicativo messaggioServizioApplicativo;
	
	private RunnableLogger log;
	private RunnableLogger logSql;
	private boolean debug;
	
	private String clusterId;
	
	private int minTimeoutResend;
	
	public TimerConsegnaContenutiApplicativiSender(MessaggioServizioApplicativo messaggioServizioApplicativo,
			RegistroServiziManager registroServiziReader,
			ConfigurazionePdDManager configurazionePdDReader,
			String clusterId, ConfigurazioneCoda configurazioneCoda) {
		this.messaggioServizioApplicativo = messaggioServizioApplicativo;
		this.propertiesReader = OpenSPCoop2Properties.getInstance();
		this.registroServiziReader = registroServiziReader;
		this.configurazionePdDReader = configurazionePdDReader;
		this.debug = configurazioneCoda.isDebug();
		this.clusterId = clusterId;
		this.minTimeoutResend = configurazioneCoda.getConsegnaFallita_intervalloMinimoRiconsegna();
	}
	
	@Override
	public String getIdentifier() {
		return "("+this.messaggioServizioApplicativo.getIdTransazione()+"_"+this.messaggioServizioApplicativo.getServizioApplicativo()+"_"+this.messaggioServizioApplicativo.getIdMessaggio()+")";
	}
	
	@Override
	public boolean isContinuousRunning() {
		return false; // deve effettuare solamente una consegna
	}
	
	@Override
	public void initialize(RunnableLogger log) throws UtilsException{
		this.log = log;
		this.logSql = new RunnableLogger(log.getThreadName(),  
				OpenSPCoop2Logger.getLoggerOpenSPCoopConsegnaContenutiSql(this.debug));
	}
	
	
	@Override
	public void check() throws UtilsException {
		
		String idTransazione = this.messaggioServizioApplicativo.getIdTransazione();
		String servizioApplicativo = this.messaggioServizioApplicativo.getServizioApplicativo();
		String idMsgDaInoltrare = this.messaggioServizioApplicativo.getIdMessaggio();
		String identificativo = "redelivery_"+idTransazione+"_"+servizioApplicativo+"_"+idMsgDaInoltrare;
		Date oraRegistrazione = this.messaggioServizioApplicativo.getOraRegistrazione();
		
		MsgDiagnostico msgDiag = MsgDiagnostico.newInstance(TimerConsegnaContenutiApplicativiThread.ID_MODULO);
		PdDContext pddContext = new PdDContext();
		pddContext.addObject(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE, idTransazione);
		msgDiag.setPrefixMsgPersonalizzati(MsgDiagnosticiProperties.MSG_DIAG_TIMER_CONSEGNA_CONTENUTI_APPLICATIVI);
		msgDiag.addKeyword(CostantiPdD.KEY_TIPO_MESSAGGIO,Costanti.INBOX);
		msgDiag.addKeyword(CostantiPdD.KEY_ID_MESSAGGIO_DA_INOLTRARE,idMsgDaInoltrare);
		msgDiag.addKeyword(CostantiPdD.KEY_SA_EROGATORE,servizioApplicativo);
		
		OpenSPCoopStateful openspcoopstateGestore = new OpenSPCoopStateful();
		try {

			this.log.debug("Riconsegna in corso del messaggio '"+idMsgDaInoltrare+"' per l'applicativo '"+servizioApplicativo+"' ...");
			
			openspcoopstateGestore.initResource(this.propertiesReader.getIdentitaPortaDefaultWithoutProtocol(),TimerConsegnaContenutiApplicativiThread.ID_MODULO, identificativo,
					OpenSPCoopStateDBManager.consegnePreseInCarico);
				
			GestoreMessaggi messaggioDaInviare = null;
			try{
	
				// FASE 1 PREPARAZIONE
				
				RepositoryBuste repositoryBuste = new RepositoryBuste(openspcoopstateGestore.getStatoRichiesta(), true,null);
				Busta bustaToSend = repositoryBuste.getBustaFromInBox(idMsgDaInoltrare, true);
				msgDiag.addKeywords(bustaToSend, true);
	
				IProtocolFactory<?> protocolFactory = ProtocolFactoryManager.getInstance().getProtocolFactoryByName(bustaToSend.getProtocollo());
	
				// Per ottimizzare le chiamate
				RequestInfo requestInfo = null;
				// TODO: ottimizzare questa chiamata. Fare una struttura ad hoc poi da riportare in consegna contenuti quando si legge la requestInfo dal Messaggio.
				//RequestInfo requestInfoForMemoryOptimization = new RequestInfo();
				//GestoreRichieste.setRequestConfigInMemory(requestInfoForMemoryOptimization);
				
				String implementazioneMittente = null;
				if(bustaToSend.getTipoMittente()!=null && bustaToSend.getMittente()!=null) {
					implementazioneMittente = this.registroServiziReader.getImplementazionePdD(new IDSoggetto(bustaToSend.getTipoMittente(),bustaToSend.getMittente()), null, requestInfo);
				}
				String implementazioneDestinatario = this.registroServiziReader.getImplementazionePdD(new IDSoggetto(bustaToSend.getTipoDestinatario(),bustaToSend.getDestinatario()), null, requestInfo);
	
				messaggioDaInviare = new GestoreMessaggi(openspcoopstateGestore,true,idMsgDaInoltrare,Costanti.INBOX,
						this.logSql.getLog(),msgDiag,null);
				pddContext = messaggioDaInviare.getPdDContext(true); // aggiorno anche l'istanza dentro l'oggetto messaggioDaInviare stesso.
	
				IDSoggetto soggettoFruitore = null;
				if(bustaToSend.getMittente()!=null) {
					soggettoFruitore = new IDSoggetto(bustaToSend.getTipoMittente(),
							bustaToSend.getMittente());
				}
	
				IDServizio servizioBusta = IDServizioFactory.getInstance().getIDServizioFromValues(bustaToSend.getTipoServizio(),
						bustaToSend.getServizio(),
						bustaToSend.getTipoDestinatario(), 
						bustaToSend.getDestinatario(), 
						bustaToSend.getVersioneServizio()); 
				servizioBusta.setAzione(bustaToSend.getAzione());	
	
				IDAccordo idAccordo = null;
				AccordoServizioParteSpecifica asps = null;
				try{
					asps = this.registroServiziReader.getAccordoServizioParteSpecifica(servizioBusta, null, false, requestInfo);
					idAccordo = IDAccordoFactory.getInstance().getIDAccordoFromUri(asps.getAccordoServizioParteComune());
				}catch(Exception e){
					msgDiag.logErroreGenerico(e,"ConsegnaAsincrona getAccordoServizioParteSpecifica("+servizioBusta+")");
				}
					
				IDSoggetto identitaPdD = null;
				String dominioRD = null;
				try{
					dominioRD = this.configurazionePdDReader.getIdentificativoPorta(servizioBusta.getSoggettoErogatore(),protocolFactory, requestInfo);
					if(dominioRD==null){
						throw new Exception("Dominio is null");
					}
				}catch(Exception e){
					msgDiag.logErroreGenerico(e,"ConsegnaAsincrona getDominio("+servizioBusta.getSoggettoErogatore()+")");
				}
				if(dominioRD==null){
					identitaPdD = this.propertiesReader.getIdentitaPortaDefault(null, requestInfo);
				}else{
					identitaPdD = new IDSoggetto(bustaToSend.getTipoDestinatario(),
							bustaToSend.getDestinatario(),dominioRD);
				}
	
				IDPortaApplicativa idPA = this.configurazionePdDReader.getIDPortaApplicativa(this.messaggioServizioApplicativo.getNomePorta(), requestInfo, protocolFactory);
				RichiestaApplicativa richiestaApplicativa = new RichiestaApplicativa(soggettoFruitore, identitaPdD, idPA);
				richiestaApplicativa.setServizioApplicativo(servizioApplicativo);
				richiestaApplicativa.setIdentitaServizioApplicativoFruitore(bustaToSend.getServizioApplicativoFruitore());
				richiestaApplicativa.setIdAccordo(idAccordo);
	
				ConsegnaContenutiApplicativiMessage consegnaMSG = new ConsegnaContenutiApplicativiMessage();
				consegnaMSG.setBusta(bustaToSend);
				consegnaMSG.setOneWayVersione11(false);
				consegnaMSG.setStateless(true);
				consegnaMSG.setImplementazionePdDSoggettoMittente(implementazioneMittente);
				consegnaMSG.setImplementazionePdDSoggettoDestinatario(implementazioneDestinatario);
				consegnaMSG.setPddContext(pddContext);
				consegnaMSG.setRichiestaApplicativa(richiestaApplicativa);
				
				BehaviourForwardToConfiguration behaviourForwardToConfiguration = new BehaviourForwardToConfiguration();
				if(this.messaggioServizioApplicativo.isSbustamentoSoap())
					behaviourForwardToConfiguration.setSbustamentoSoap(StatoFunzionalita.ABILITATA);
				else 
					behaviourForwardToConfiguration.setSbustamentoSoap(StatoFunzionalita.DISABILITATA);
				if(this.messaggioServizioApplicativo.isSbustamentoInformazioniProtocollo())
					behaviourForwardToConfiguration.setSbustamentoInformazioniProtocollo(StatoFunzionalita.ABILITATA);
				else 
					behaviourForwardToConfiguration.setSbustamentoInformazioniProtocollo(StatoFunzionalita.DISABILITATA);
				
				ConsegnaContenutiApplicativiBehaviourMessage behaviourMsg = new ConsegnaContenutiApplicativiBehaviourMessage();
				behaviourMsg.setIdMessaggioPreBehaviour(bustaToSend.getRiferimentoMessaggio());
				behaviourMsg.setBehaviourForwardToConfiguration(behaviourForwardToConfiguration);
				IdTransazioneApplicativoServer idTransazioneApplicativoServer = new IdTransazioneApplicativoServer();
				idTransazioneApplicativoServer.setIdTransazione(PdDContext.getValue(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE, pddContext));
				idTransazioneApplicativoServer.setServizioApplicativoErogatore(servizioApplicativo);
				PortaApplicativa pa = this.configurazionePdDReader.getPortaApplicativa_SafeMethod(idPA, requestInfo);
				if(pa!=null && pa.getServizioApplicativoList()!=null) {
					for (PortaApplicativaServizioApplicativo pasa : pa.getServizioApplicativoList()) {
						if(pasa.getNome().equals(servizioApplicativo)) {
							if(pasa.getDatiConnettore()!=null) {
								idTransazioneApplicativoServer.setConnettoreNome(pasa.getDatiConnettore().getNome());
							}
							ConfigurazioneGestioneConsegnaNotifiche configGestioneConsegna = MultiDeliverUtils.read(pasa, this.log.getLog()); 
							GestioneErrore gestioneErroreBehaviour = GestioneConsegnaNotificheUtils.toGestioneErrore(configGestioneConsegna);		
							behaviourMsg.setGestioneErrore(gestioneErroreBehaviour);
							break;
						}
					}
				}
				behaviourMsg.setIdTransazioneApplicativoServer(idTransazioneApplicativoServer);
				behaviourMsg.setOraRegistrazioneTransazioneApplicativoServer(oraRegistrazione);
				consegnaMSG.setBehaviour(behaviourMsg);
	
				
				
				// FASE 2 SPEDIZIONE
				
				// rilasco connessione gestore
				openspcoopstateGestore.releaseResource();
				
				EsitoLib result = null;
				OpenSPCoopStateful openspcoopstateMessaggio = null;
				try {
					ConsegnaContenutiApplicativi lib = new ConsegnaContenutiApplicativi(OpenSPCoop2Logger.getLoggerOpenSPCoopCore());
					openspcoopstateMessaggio = new OpenSPCoopStateful();
					// viene inizializzata dentro il modulo ConsegnaContenutiApplicativi
					//openspcoopstateMessaggio.initResource(identitaPdD,TimerConsegnaContenutiApplicativiThread.ID_MODULO, bustaToSend.getID());
					openspcoopstateMessaggio.setMessageLib(consegnaMSG);
					result = lib.onMessage(openspcoopstateMessaggio);
				}finally {
					try{
						if(openspcoopstateMessaggio!=null && !openspcoopstateMessaggio.resourceReleased()){
							openspcoopstateMessaggio.releaseResource();
						}
					}catch(Exception e){}
					
					// riprendo connessione gestore
					openspcoopstateGestore.updateResource(identificativo);
					messaggioDaInviare.updateOpenSPCoopState(openspcoopstateGestore);
				}
				
				
				
				if(this.debug)
					this.log.debug("Invocato ConsegnaContenutiApplicativi per ["+bustaToSend.getID()+
							"] con esito: "+result.getStatoInvocazione(),result.getErroreNonGestito());
				if(EsitoLib.ERRORE_NON_GESTITO==result.getStatoInvocazione()){
					// per evitare i loop infinito
					Timestamp tMinTimeoutResend = new Timestamp(DateManager.getTimeMillis()+(this.minTimeoutResend*1000));
					if(result.getDataRispedizioneAggiornata()==null || result.getDataRispedizioneAggiornata().before(tMinTimeoutResend)){
						messaggioDaInviare.aggiornaDataRispedizione(tMinTimeoutResend, servizioApplicativo);
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
	
					msgDiag.logPersonalizzato("inoltroMessaggio");
					if(this.debug)
						this.log.debug(msgDiag.getMessaggio_replaceKeywords("inoltroMessaggio"));
	
				}
	
			}catch(Exception e){
				msgDiag.logErroreGenerico(e,"InoltroMessaggioInbox("+idMsgDaInoltrare+")");
				this.log.error("ErroreInoltroMessaggioInbox("+idMsgDaInoltrare+"): "+e.getMessage(),e);
				// per evitare i loop infinito
				if(openspcoopstateGestore!=null && !openspcoopstateGestore.resourceReleased()){
					messaggioDaInviare.aggiornaDataRispedizione(new Timestamp(DateManager.getTimeMillis()+(this.minTimeoutResend*1000)), servizioApplicativo);
					messaggioDaInviare.aggiornaErroreProcessamentoMessaggio("["+TimerConsegnaContenutiApplicativiThread.ID_MODULO+"] "+e.getMessage(), servizioApplicativo);
				}
			}finally{
				if(openspcoopstateGestore!=null && !openspcoopstateGestore.resourceReleased()){
					messaggioDaInviare.releaseMessaggioPresaInCosegna(servizioApplicativo, this.clusterId, this.debug, this.logSql); // può già essere stato eliminato
				}
			}
			
			this.log.debug("Riconsegna in corso del messaggio '"+idMsgDaInoltrare+"' per l'applicativo '"+servizioApplicativo+"' terminata");
		}
		catch (Exception e) {
			msgDiag.logErroreGenerico(e,"GestioneRiconsegnaMessaggio");
			this.log.error("Riscontrato errore durante la consegna del messaggio '"+idMsgDaInoltrare+"' per l'applicativo '"+servizioApplicativo+"': "+ e.getMessage(),e);
		}finally{
			if(openspcoopstateGestore!=null)
				openspcoopstateGestore.releaseResource();
		}
	}

}
