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

import org.slf4j.Logger;
import org.openspcoop2.core.constants.TipoPdD;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.ParseException;
import org.openspcoop2.pdd.config.ConfigurazionePdDManager;
import org.openspcoop2.pdd.config.RichiestaApplicativa;
import org.openspcoop2.pdd.config.RichiestaDelegata;
import org.openspcoop2.pdd.core.CostantiPdD;
import org.openspcoop2.pdd.core.EJBUtils;
import org.openspcoop2.pdd.core.GestoreMessaggi;
import org.openspcoop2.pdd.core.PdDContext;
import org.openspcoop2.pdd.core.state.IOpenSPCoopState;
import org.openspcoop2.pdd.core.state.OpenSPCoopStateException;
import org.openspcoop2.pdd.core.state.OpenSPCoopStateless;
import org.openspcoop2.pdd.logger.MsgDiagnosticiProperties;
import org.openspcoop2.pdd.logger.MsgDiagnostico;
import org.openspcoop2.pdd.services.connector.DirectVMProtocolInfo;
import org.openspcoop2.protocol.engine.constants.Costanti;
import org.openspcoop2.protocol.engine.driver.ProfiloDiCollaborazione;
import org.openspcoop2.protocol.engine.driver.RepositoryBuste;
import org.openspcoop2.protocol.registry.RegistroServiziManager;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.config.IProtocolVersionManager;
import org.openspcoop2.protocol.sdk.constants.CodiceErroreIntegrazione;
import org.openspcoop2.protocol.sdk.constants.ErroriIntegrazione;
import org.openspcoop2.protocol.sdk.constants.Inoltro;
import org.openspcoop2.protocol.sdk.constants.TipoOraRegistrazione;
import org.openspcoop2.protocol.sdk.state.IState;

/**
 * Contiene la libreria ImbustamentoRisposte
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author Tronci Fabio (tronci@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ImbustamentoRisposte extends GenericLib {

	public final static String ID_MODULO = "ImbustamentoRisposte";

	public ImbustamentoRisposte(Logger log) throws GenericLibException {
		super(ImbustamentoRisposte.ID_MODULO, log);
		inizializza();
	}

	@Override
	public EsitoLib _onMessage(IOpenSPCoopState openspcoopstate,
			RegistroServiziManager registroServiziManager,ConfigurazionePdDManager configurazionePdDManager, 
			MsgDiagnostico msgDiag) throws OpenSPCoopStateException {
		EsitoLib esito = new EsitoLib(); 
		ImbustamentoRisposteMessage imbustamentoRisposteMsg = (ImbustamentoRisposteMessage) openspcoopstate.getMessageLib();
		
		/* Regupero eventuali errori di parsing. */
		ParseException parseException = null;
		if(openspcoopstate instanceof OpenSPCoopStateless) {
			OpenSPCoop2Message msgRisposta = ((OpenSPCoopStateless) openspcoopstate).getRispostaMsg();
			if(msgRisposta!=null){
				parseException = msgRisposta.getParseException();
			}
		}
		
		/* PddContext */
		PdDContext pddContext = imbustamentoRisposteMsg.getPddContext();
		String idTransazione = PdDContext.getValue(org.openspcoop2.core.constants.Costanti.CLUSTER_ID, pddContext);
				
		/* Protocol Factory */
		IProtocolFactory protocolFactory = null;
		try{
			protocolFactory = this.protocolFactoryManager.getProtocolFactoryByName((String) pddContext.getObject(org.openspcoop2.core.constants.Costanti.PROTOCOLLO));
		}catch(Exception e){
			msgDiag.logErroreGenerico(e, "ProtocolFactory.instanziazione"); 
			openspcoopstate.releaseResource();
			esito.setEsitoInvocazione(false); 
			esito.setStatoInvocazioneErroreNonGestito(e);
			return esito;
		}
		msgDiag.setPddContext(pddContext, protocolFactory);
		
		/* ID e tipo di implementazione PdD con cui interoperare */
		String idMessageRequest = openspcoopstate.getIDMessaggioSessione();
		String implementazionePdDMittente = imbustamentoRisposteMsg.getImplementazionePdDSoggettoMittente();
		
		//	Busta che ha causato l'invocazione di servizio (utile per costruire eventuali messaggi Errore)
		Busta bustaRichiesta = imbustamentoRisposteMsg.getBusta();
		//	Identificatore Porta Applicativa
		RichiestaApplicativa richiestaApplicativa = imbustamentoRisposteMsg.getRichiestaApplicativa();
		//	Identificatore Porta Delegata
		RichiestaDelegata richiestaDelegata = imbustamentoRisposteMsg.getRichiestaDelegata();
		// ID della risposta
		String idMessageResponse = imbustamentoRisposteMsg.getIDMessageResponse(); // ID associato a questa risposta
		// Altri parametri:
		TipoPdD tipoPdD = TipoPdD.APPLICATIVA;
		String idModuloInAttesa = null; 
		IDSoggetto identitaPdD = null;
		String scenarioCooperazione = null; 
		IDSoggetto soggettoFruitore = null;
		IDServizio idServizio = null;
		String servizioApplicativo = null;
		IDServizio servizioHeaderIntegrazione = null;
		if(bustaRichiesta!=null){
			servizioHeaderIntegrazione = new IDServizio();
			// Per ricambiare il servizio in correlato per AsincronoAsimmetrico, richiestaStato
			servizioHeaderIntegrazione.setTipoServizio(bustaRichiesta.getTipoServizio());
			servizioHeaderIntegrazione.setServizio(bustaRichiesta.getServizio());
			servizioHeaderIntegrazione.setAzione(bustaRichiesta.getAzione());
		}
		String profiloGestione = null;
		String servizioApplicativoFruitore = null;
		String servizioApplicativoErogatore = null;
		String idCorrelazioneApplicativa = null;
		String idCorrelazioneApplicativaRisposta = null;

		if(richiestaApplicativa!=null){
			identitaPdD = richiestaApplicativa.getDominio();
			idModuloInAttesa = richiestaApplicativa.getIdModuloInAttesa();
			scenarioCooperazione = richiestaApplicativa.getScenario();
			soggettoFruitore = richiestaApplicativa.getSoggettoFruitore();
			idServizio = richiestaApplicativa.getIDServizio();
			servizioApplicativo = richiestaApplicativa.getServizioApplicativo();
			if(servizioHeaderIntegrazione!=null){
				servizioHeaderIntegrazione.setSoggettoErogatore(idServizio.getSoggettoErogatore());
			}
			profiloGestione = richiestaApplicativa.getProfiloGestione();
			servizioApplicativoFruitore = richiestaApplicativa.getIdentitaServizioApplicativoFruitore();
			servizioApplicativoErogatore = richiestaApplicativa.getServizioApplicativo();
			idCorrelazioneApplicativa = richiestaApplicativa.getIdCorrelazioneApplicativa();
			idCorrelazioneApplicativaRisposta = richiestaApplicativa.getIdCorrelazioneApplicativaRisposta();
		}else{
			identitaPdD = richiestaDelegata.getDominio();
			idModuloInAttesa = richiestaDelegata.getIdModuloInAttesa();
			scenarioCooperazione = richiestaDelegata.getScenario();
			soggettoFruitore = richiestaDelegata.getSoggettoFruitore();
			idServizio = richiestaDelegata.getIdServizio();
			servizioApplicativo = richiestaDelegata.getServizioApplicativo();
			if(servizioHeaderIntegrazione!=null){
				servizioHeaderIntegrazione.setSoggettoErogatore(idServizio.getSoggettoErogatore());
			}
			profiloGestione = richiestaDelegata.getProfiloGestione();
			servizioApplicativoFruitore = richiestaDelegata.getServizioApplicativo();
			idCorrelazioneApplicativa = richiestaDelegata.getIdCorrelazioneApplicativa();
			idCorrelazioneApplicativaRisposta = richiestaDelegata.getIdCorrelazioneApplicativaRisposta();
		}
		msgDiag.mediumDebug("Profilo di gestione ["+ImbustamentoRisposte.ID_MODULO+"] della busta: "+profiloGestione);
		msgDiag.setDominio(identitaPdD);  // imposto anche il dominio nel msgDiag


		//			 Aggiornamento Informazioni
		msgDiag.setIdMessaggioRichiesta(idMessageRequest);
		msgDiag.setIdMessaggioRisposta(idMessageResponse);
		msgDiag.setDelegata(false);
		msgDiag.setFruitore(soggettoFruitore);
		if(servizioHeaderIntegrazione!=null){
			msgDiag.setServizio(servizioHeaderIntegrazione);
		}else{
			msgDiag.setServizio(idServizio);
		}
		msgDiag.setServizioApplicativo(servizioApplicativo);
		msgDiag.setIdCorrelazioneApplicativa(idCorrelazioneApplicativa);
		msgDiag.setIdCorrelazioneRisposta(idCorrelazioneApplicativaRisposta);
		msgDiag.setPrefixMsgPersonalizzati(MsgDiagnosticiProperties.MSG_DIAG_IMBUSTAMENTO_RISPOSTE);
		msgDiag.addKeywords(bustaRichiesta, true);
		msgDiag.addKeyword(CostantiPdD.KEY_ID_MESSAGGIO_RICHIESTA, idMessageRequest);
		msgDiag.addKeyword(CostantiPdD.KEY_ID_MESSAGGIO_RISPOSTA, idMessageResponse);
		msgDiag.addKeyword(CostantiPdD.KEY_SA_EROGATORE, servizioApplicativo);
		if(servizioApplicativoFruitore!=null){
			msgDiag.addKeyword(CostantiPdD.KEY_SA_FRUITORE, servizioApplicativoFruitore);
		}
		msgDiag.addKeyword(CostantiPdD.KEY_ID_CORRELAZIONE_APPLICATIVA, idCorrelazioneApplicativa);
		
		


		/* ------------------ Inizializzo stato OpenSPCoop  --------------- */
		msgDiag.mediumDebug("Inizializzo stato per la gestione della richiesta...");
		openspcoopstate.initResource(identitaPdD, ImbustamentoRisposte.ID_MODULO,idTransazione);
		registroServiziManager.updateState(openspcoopstate.getStatoRichiesta(),openspcoopstate.getStatoRisposta());
		configurazionePdDManager.updateState(openspcoopstate.getStatoRichiesta(),openspcoopstate.getStatoRisposta());
		msgDiag.updateState(openspcoopstate.getStatoRichiesta(),openspcoopstate.getStatoRisposta());




		/* ------------------ Inizializzazione Contesto di gestione della Richiesta --------------- */
		msgDiag.mediumDebug("Inizializzo contesto per la gestione...");
		// EJBUtils (per eventuali errori)
		EJBUtils ejbUtils = null;
		try{
			ejbUtils = new EJBUtils(identitaPdD,tipoPdD,ImbustamentoRisposte.ID_MODULO,idMessageRequest,
					idMessageResponse,Costanti.OUTBOX, openspcoopstate ,msgDiag,false,
					imbustamentoRisposteMsg.getImplementazionePdDSoggettoMittente(),
					imbustamentoRisposteMsg.getImplementazionePdDSoggettoDestinatario(),
					profiloGestione,pddContext);
			ejbUtils.setSpedizioneMsgIngresso(imbustamentoRisposteMsg.getSpedizioneMsgIngresso());
			ejbUtils.setRicezioneMsgRisposta(imbustamentoRisposteMsg.getRicezioneMsgRisposta());
			ejbUtils.setServizioApplicativoErogatore(servizioApplicativo);
		}catch(Exception e){
			msgDiag.logErroreGenerico(e, "EJBUtils.new"); 
			openspcoopstate.releaseResource(); 
			esito.setEsitoInvocazione(false); 
			esito.setStatoInvocazioneErroreNonGestito(e); 
			return esito;
		}
		
		// Oneway versione 11
		boolean oneWayVersione11 = imbustamentoRisposteMsg.isOneWayVersione11();
		ejbUtils.setOneWayVersione11(oneWayVersione11);

		// Gestori funzionalita'
		IState statoRichiesta = openspcoopstate.getStatoRichiesta();
		ProfiloDiCollaborazione profiloCollaborazione = null;
		try{
			profiloCollaborazione = new ProfiloDiCollaborazione(statoRichiesta,protocolFactory);
		}catch(Exception e){
			msgDiag.logErroreGenerico(e, "ProfiloDiCollaborazione.new");
			openspcoopstate.releaseResource();
			esito.setEsitoInvocazione(false); 
			esito.setStatoInvocazioneErroreNonGestito(e);
			return esito;
		}
		RepositoryBuste repositoryBuste = new RepositoryBuste(statoRichiesta, true, protocolFactory);

		// Gestore Messaggi
		GestoreMessaggi msgResponse = null; // Risposta inviata

		//	Modalita' gestione risposta (Sincrona/Fault/Ricevute...)
		// Per i profili diversi dal sincrono e' possibile impostare dove far ritornare l'errore
		boolean newConnectionForResponse = false; 
		if( (org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.SINCRONO.equals(bustaRichiesta.getProfiloDiCollaborazione())==false) &&
				(imbustamentoRisposteMsg.isStateless()==false) ) {
			newConnectionForResponse = configurazionePdDManager.newConnectionForResponse();
		}
		ejbUtils.setReplyOnNewConnection(newConnectionForResponse);
		
		
		// Gestione indirizzo telematico
		boolean gestioneIndirizzoTelematico = false;
		if(imbustamentoRisposteMsg.isStateless()==false){
			gestioneIndirizzoTelematico = configurazionePdDManager.isUtilizzoIndirizzoTelematico();
		}
		ejbUtils.setUtilizzoIndirizzoTelematico(gestioneIndirizzoTelematico);



		// Punto di inizio per la transazione.
		try {

			IProtocolVersionManager protocolManager = protocolFactory.createProtocolVersionManager(profiloGestione);

			Busta bustaRisposta = null;
			TipoOraRegistrazione tipoTempo = this.propertiesReader.getTipoTempoBusta(implementazionePdDMittente);
			boolean asincronoStateless = false;
			
			/* ------------   Risposta Profilo Sincrono  ------------- */
			if(Costanti.SCENARIO_SINCRONO_INVOCAZIONE_SERVIZIO.equals(scenarioCooperazione)){				
				try{
					msgDiag.mediumDebug("Generazione busta per il profilo di Collaborazione Sincrono...");
					bustaRisposta = profiloCollaborazione.sincrono_generaBustaRisposta(idMessageRequest,tipoTempo);
				}catch(Exception e){
					msgDiag.logErroreGenerico(e, "profiloCollaborazione.sincrono_generaBustaRisposta"); 
					ejbUtils.sendAsRispostaBustaErroreProcessamento(idModuloInAttesa,bustaRichiesta,
							ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
								get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_525_GESTIONE_FUNZIONALITA_PROTOCOLLO),
							idCorrelazioneApplicativa,idCorrelazioneApplicativaRisposta,servizioApplicativoFruitore,e,
							parseException);
					openspcoopstate.releaseResource();
					esito.setEsitoInvocazione(true); 
					esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,
							"profiloCollaborazione.sincrono_generaBustaRisposta");
					return esito;
				}
			}
			/* ------------   Invocazione Asincrono Simmetrico ------------- */
			else if(Costanti.SCENARIO_ASINCRONO_SIMMETRICO_INVOCAZIONE_SERVIZIO.equals(scenarioCooperazione) ){
				try{
					msgDiag.mediumDebug("Generazione busta per il profilo di Collaborazione Asincrono Simmetrico (richiesta)...");
					bustaRisposta = profiloCollaborazione.asincronoSimmetrico_getBustaRicevuta(idMessageRequest,idMessageRequest,true,tipoTempo);
					asincronoStateless = imbustamentoRisposteMsg.isStateless();
				}catch(Exception e){
					msgDiag.logErroreGenerico(e, "profiloCollaborazione.asincronoSimmetrico_getBustaRicevuta(Richiesta)"); 
					ejbUtils.sendAsRispostaBustaErroreProcessamento(idModuloInAttesa,bustaRichiesta,
							ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
								get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_525_GESTIONE_FUNZIONALITA_PROTOCOLLO),
							idCorrelazioneApplicativa,idCorrelazioneApplicativaRisposta,servizioApplicativoFruitore,e,
							parseException);
					openspcoopstate.releaseResource();
					esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,
							"profiloCollaborazione.asincronoSimmetrico_getBustaRicevuta(Richiesta)");
					esito.setEsitoInvocazione(true); return esito;
				}
			}
			/* ------------  Risposta Asincrono Simmetrico ------------- */
			else if(Costanti.SCENARIO_ASINCRONO_SIMMETRICO_CONSEGNA_RISPOSTA.equals(scenarioCooperazione) ){
				try{
					msgDiag.mediumDebug("Generazione busta per il profilo di Collaborazione Asincrono Simmetrico (risposta)...");
					bustaRisposta = profiloCollaborazione.asincronoSimmetrico_getBustaRicevuta(protocolManager.getIdCorrelazioneAsincrona(bustaRichiesta),idMessageRequest,false,tipoTempo);
					if(bustaRisposta!=null){
						bustaRisposta.setRiferimentoMsgBustaRichiedenteServizio(bustaRichiesta.getRiferimentoMessaggio());
						// La ricevuta deve avere l'azione della risposta
						bustaRisposta.setAzione(bustaRichiesta.getAzione());
					}
					asincronoStateless = imbustamentoRisposteMsg.isStateless();
				}catch(Exception e){
					msgDiag.logErroreGenerico(e, "profiloCollaborazione.asincronoSimmetrico_getBustaRicevuta(Risposta)"); 
					ejbUtils.sendAsRispostaBustaErroreProcessamento(idModuloInAttesa,bustaRichiesta,
							ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
								get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_525_GESTIONE_FUNZIONALITA_PROTOCOLLO),
							idCorrelazioneApplicativa,idCorrelazioneApplicativaRisposta,servizioApplicativoFruitore,e,
							parseException);
					openspcoopstate.releaseResource();
					esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,
							"profiloCollaborazione.asincronoSimmetrico_getBustaRicevuta(Risposta)");
					esito.setEsitoInvocazione(true); return esito;
				}
			}
			/* ------------   Invocazione Asincrono Asimmetrico ------------- */
			else if(Costanti.SCENARIO_ASINCRONO_ASIMMETRICO_INVOCAZIONE_SERVIZIO.equals(scenarioCooperazione) ){
				try{
					boolean generazionAttributiAsincroni = protocolManager.isGenerazioneInformazioniServizioCorrelatoAsincronoAsimmetrico() && 
						this.propertiesReader.isGenerazioneAttributiAsincroni(implementazionePdDMittente);
					msgDiag.mediumDebug("Generazione busta per il profilo di Collaborazione Asincrono Asimmetrico (richiesta)...");
					bustaRisposta = profiloCollaborazione.asincronoAsimmetrico_getBustaRicevuta(idMessageRequest,idMessageRequest,true,generazionAttributiAsincroni,tipoTempo);
					asincronoStateless = imbustamentoRisposteMsg.isStateless();
				}catch(Exception e){
					msgDiag.logErroreGenerico(e, "profiloCollaborazione.asincronoAsimmetrico_getBustaRicevuta(Richiesta)"); 
					ejbUtils.sendAsRispostaBustaErroreProcessamento(idModuloInAttesa,bustaRichiesta,
							ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
								get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_525_GESTIONE_FUNZIONALITA_PROTOCOLLO),
							idCorrelazioneApplicativa,idCorrelazioneApplicativaRisposta,servizioApplicativoFruitore,e,
							parseException);
					openspcoopstate.releaseResource();
					esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,
							"profiloCollaborazione.asincronoAsimmetrico_getBustaRicevuta(Richiesta)");
					esito.setEsitoInvocazione(true); return esito;
				}
			}
			/* ------------  Risposta Asincrono Asimmetrico ------------- */
			else if(Costanti.SCENARIO_ASINCRONO_ASIMMETRICO_POLLING.equals(scenarioCooperazione) ){
				try{
					msgDiag.mediumDebug("Generazione busta per il profilo di Collaborazione Asincrono Asimmetrico (richiestaStato)...");
					boolean generazionAttributiAsincroni = protocolManager.isGenerazioneInformazioniServizioCorrelatoAsincronoAsimmetrico() && 
						this.propertiesReader.isGenerazioneAttributiAsincroni(implementazionePdDMittente);
					bustaRisposta = profiloCollaborazione.asincronoAsimmetrico_getBustaRicevuta(protocolManager.getIdCorrelazioneAsincrona(bustaRichiesta),idMessageRequest,false,generazionAttributiAsincroni,tipoTempo);
					if(bustaRisposta!=null){
						bustaRisposta.setRiferimentoMsgBustaRichiedenteServizio(bustaRichiesta.getRiferimentoMessaggio());
						// La ricevuta deve avere l'azione della richiesta-stato
						bustaRisposta.setAzione(bustaRichiesta.getAzione());
					}
					asincronoStateless = imbustamentoRisposteMsg.isStateless();
				}catch(Exception e){
					msgDiag.logErroreGenerico(e, "profiloCollaborazione.asincronoAsimmetrico_getBustaRicevuta(Risposta)"); 
					ejbUtils.sendAsRispostaBustaErroreProcessamento(idModuloInAttesa,bustaRichiesta,
							ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
								get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_525_GESTIONE_FUNZIONALITA_PROTOCOLLO),
							idCorrelazioneApplicativa,idCorrelazioneApplicativaRisposta,servizioApplicativoFruitore,e,
							parseException);
					openspcoopstate.releaseResource(); 
					esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,
							"profiloCollaborazione.asincronoAsimmetrico_getBustaRicevuta(Risposta)");
					esito.setEsitoInvocazione(true); return esito;
				}
			}

			/* ------ Check Busta di Risposta ------- */
			msgDiag.mediumDebug("Check busta...");
			if(bustaRisposta==null){
				throw new Exception("Busta di risposta ["+scenarioCooperazione+"] non generata.");
			}

			switch (protocolManager.getConsegnaAffidabile(bustaRisposta.getProfiloDiCollaborazione())) {
			case ABILITATA:
				bustaRisposta.setConfermaRicezione(true);
				break;
			case DISABILITATA:
				bustaRisposta.setConfermaRicezione(false);
				break;
			default:
				// valore della busta di risposta
				break;
			}
			
			switch (protocolManager.getFiltroDuplicati(bustaRisposta.getProfiloDiCollaborazione())) {
			case ABILITATA:
				bustaRisposta.setInoltro(Inoltro.SENZA_DUPLICATI,protocolFactory.createTraduttore().toString(Inoltro.SENZA_DUPLICATI));
				break;
			case DISABILITATA:
				bustaRisposta.setInoltro(Inoltro.CON_DUPLICATI,protocolFactory.createTraduttore().toString(Inoltro.CON_DUPLICATI));
				break;
			default:
				// valore della busta di risposta
			}
			

			/* -------------- Aggiungo ID Risposta ------------------- */
			bustaRisposta.setID(idMessageResponse);
			msgDiag.addKeywords(bustaRisposta, false);
			
			
			/* -------------- Aggiungo SA Applicativo Erogatore ------------------- */
			bustaRisposta.setServizioApplicativoErogatore(servizioApplicativoErogatore);			


			/* -------------- Imposto eventuali informazioni DirectVM ------------------- */
			DirectVMProtocolInfo.setInfoFromContext(pddContext, bustaRisposta);
			
			
			/* ------------  Spedizione Risposta   ------------- */
			msgDiag.mediumDebug("Spedizione busta di risposta al modulo in uscita (RicezioneBuste/InoltroRisposte)...");
			msgResponse = 
				ejbUtils.sendBustaRisposta(idModuloInAttesa,bustaRisposta,profiloGestione,
						idCorrelazioneApplicativa,idCorrelazioneApplicativaRisposta,servizioApplicativoFruitore);

			// elimino utilizzo PdD della richiesta
			msgDiag.mediumDebug("Elimino utilizzo busta dalla PdD...");
			repositoryBuste.eliminaUtilizzoPdDFromInBox(idMessageRequest,asincronoStateless);


			// Commit JDBC
			openspcoopstate.commit();

			// Aggiornamento cache messaggio
			if(msgResponse!=null)
				msgResponse.addMessaggiIntoCache_readFromTable(ImbustamentoRisposte.ID_MODULO, "risposta");

			// Aggiornamento cache proprietario messaggio
			if(msgResponse!=null)
				msgResponse.addProprietariIntoCache_readFromTable(ImbustamentoRisposte.ID_MODULO, "risposta",idMessageRequest,false);

			// Rilascio connessione
			msgDiag.mediumDebug("Rilascio connessione al database...");
			openspcoopstate.releaseResource();

			msgDiag.mediumDebug("Lavoro Terminato.");
			esito.setStatoInvocazione(EsitoLib.OK, null);
			esito.setEsitoInvocazione(true); return esito; 



		}catch(Throwable e){	
			this.log.error("ErroreGenerale",e);
			msgDiag.logErroreGenerico(e, "Generale");
			
			try{
				ejbUtils.sendAsRispostaBustaErroreProcessamento(idModuloInAttesa,bustaRichiesta,
						ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.getErroreIntegrazione(),
						idCorrelazioneApplicativa,idCorrelazioneApplicativaRisposta,servizioApplicativoFruitore,e,
						parseException);
				esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,"ErroreGenerale");
				esito.setEsitoInvocazione(true);
			}catch(Exception er){
				msgDiag.logErroreGenerico(er,"ejbUtils.sendErroreGenerale(profiloConRisposta)");
				ejbUtils.rollbackMessage("Spedizione Errore al Mittente durante una richiesta con gestione della risposta non riuscita", esito);
				esito.setStatoInvocazioneErroreNonGestito(er);
				esito.setEsitoInvocazione(false); 
			}
			openspcoopstate.releaseResource(); 
			return esito;
		}

	}
}
