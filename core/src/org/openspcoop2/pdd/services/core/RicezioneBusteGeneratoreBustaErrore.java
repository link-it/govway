/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it). 
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
package org.openspcoop2.pdd.services.core;

import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.core.commons.CoreException;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.OpenSPCoop2MessageFactory;
import org.openspcoop2.message.constants.MessageRole;
import org.openspcoop2.message.utils.MessageUtilities;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.core.CostantiPdD;
import org.openspcoop2.pdd.core.EJBUtils;
import org.openspcoop2.pdd.core.GestoreMessaggi;
import org.openspcoop2.pdd.core.PdDContext;
import org.openspcoop2.pdd.core.state.IOpenSPCoopState;
import org.openspcoop2.pdd.core.state.OpenSPCoopState;
import org.openspcoop2.pdd.logger.MsgDiagnosticiProperties;
import org.openspcoop2.pdd.logger.MsgDiagnostico;
import org.openspcoop2.pdd.logger.Tracciamento;
import org.openspcoop2.pdd.mdb.InoltroRisposte;
import org.openspcoop2.pdd.mdb.Sbustamento;
import org.openspcoop2.pdd.services.error.AbstractErrorGenerator;
import org.openspcoop2.pdd.services.error.RicezioneBusteExternalErrorGenerator;
import org.openspcoop2.pdd.timers.TimerGestoreMessaggi;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.engine.builder.Imbustamento;
import org.openspcoop2.protocol.engine.constants.Costanti;
import org.openspcoop2.protocol.engine.driver.History;
import org.openspcoop2.protocol.engine.driver.IFiltroDuplicati;
import org.openspcoop2.protocol.engine.driver.RepositoryBuste;
import org.openspcoop2.protocol.engine.validator.Validatore;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.Eccezione;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.Integrazione;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.ProtocolMessage;
import org.openspcoop2.protocol.sdk.Riscontro;
import org.openspcoop2.protocol.sdk.SecurityInfo;
import org.openspcoop2.protocol.sdk.config.IProtocolManager;
import org.openspcoop2.protocol.sdk.config.IProtocolVersionManager;
import org.openspcoop2.protocol.sdk.constants.ErroreCooperazione;
import org.openspcoop2.protocol.sdk.constants.ErroreIntegrazione;
import org.openspcoop2.protocol.sdk.constants.ErroriCooperazione;
import org.openspcoop2.protocol.sdk.constants.ErroriIntegrazione;
import org.openspcoop2.protocol.sdk.constants.FaseImbustamento;
import org.openspcoop2.protocol.sdk.constants.IntegrationFunctionError;
import org.openspcoop2.protocol.sdk.constants.RuoloBusta;
import org.openspcoop2.protocol.sdk.constants.RuoloMessaggio;
import org.openspcoop2.protocol.sdk.constants.TipoOraRegistrazione;
import org.openspcoop2.protocol.sdk.state.RequestInfo;
import org.openspcoop2.protocol.sdk.tracciamento.EsitoElaborazioneMessaggioTracciato;
import org.openspcoop2.protocol.sdk.tracciamento.TracciamentoException;
import org.openspcoop2.protocol.sdk.validator.IValidazioneSemantica;
import org.openspcoop2.security.message.MessageSecurityContext;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.resources.Loader;
import org.slf4j.Logger;

/**
 * RicezioneBusteGeneratoreBustaErrore
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class RicezioneBusteGeneratoreBustaErrore {

	private RicezioneBusteContext msgContext;
	private RicezioneBusteExternalErrorGenerator generatoreErrore; 
	
	public RicezioneBusteGeneratoreBustaErrore(RicezioneBusteContext msgContext, RicezioneBusteExternalErrorGenerator generatoreErrore) {
		this.msgContext = msgContext;
		this.generatoreErrore = generatoreErrore;
	}
	
	public OpenSPCoop2Message generaBustaErroreProcessamento(RicezioneBusteParametriGenerazioneBustaErrore parametriGenerazioneBustaErrore,Exception e){
		parametriGenerazioneBustaErrore.setErroreProcessamento(true);
		parametriGenerazioneBustaErrore.setEccezioneProcessamento(e);
		return generaBustaErrore(parametriGenerazioneBustaErrore, false);
	}

	public OpenSPCoop2Message generaBustaErroreValidazione(RicezioneBusteParametriGenerazioneBustaErrore parametriGenerazioneBustaErrore){
		parametriGenerazioneBustaErrore.setErroreProcessamento(false);
		return generaBustaErrore(parametriGenerazioneBustaErrore, true);
	}

	public OpenSPCoop2Message generaBustaErrore(RicezioneBusteParametriGenerazioneBustaErrore parametriGenerazioneBustaErrore, boolean erroreValidazione){
		PdDContext pddContext = this.msgContext.getPddContext();
		String idTransazione = (String) pddContext.getObject(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE);
		
		try{
			IntegrationFunctionError integrationFunctionError = parametriGenerazioneBustaErrore.getIntegrationFunctionError(pddContext, erroreValidazione);
			
			OpenSPCoop2Message responseErrorMessage = null;
		
			ErroreCooperazione erroreCooperazione = parametriGenerazioneBustaErrore.getErroreCooperazione();
			ErroreIntegrazione erroreIntegrazione = parametriGenerazioneBustaErrore.getErroreIntegrazione();
			
			List<Eccezione> error = parametriGenerazioneBustaErrore.getError();
			
			
			java.util.Map<String,Object> securityPropertiesResponse = null;
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
			this.generatoreErrore.updateState(openspcoopState.getStatoRichiesta());
			if(parametriGenerazioneBustaErrore.isErroreProcessamento()){
				if(erroreCooperazione!=null){
					responseErrorMessage = this.generatoreErrore.buildErroreProtocollo_Processamento(
							integrationFunctionError,
							parametriGenerazioneBustaErrore.getBusta(),integrazione, idTransazione, erroreCooperazione,
							securityPropertiesResponse,messageSecurityContext,
							propertiesReader.getGestioneSerializableDB_AttesaAttiva(),
							propertiesReader.getGestioneSerializableDB_CheckInterval(),profiloGestione,
							propertiesReader.getTipoTempoBusta(implementazionePdDMittente),
							propertiesReader.isGenerazioneListaTrasmissioni(implementazionePdDMittente),
							parametriGenerazioneBustaErrore.getEccezioneProcessamento(),
							this.msgContext.getPddContext());
				}
				else if(erroreIntegrazione!=null){
					responseErrorMessage = this.generatoreErrore.buildErroreProtocollo_Processamento(
							integrationFunctionError,
							parametriGenerazioneBustaErrore.getBusta(),integrazione, idTransazione, erroreIntegrazione,
							securityPropertiesResponse,messageSecurityContext,
							propertiesReader.getGestioneSerializableDB_AttesaAttiva(),
							propertiesReader.getGestioneSerializableDB_CheckInterval(),profiloGestione,
							propertiesReader.getTipoTempoBusta(implementazionePdDMittente),
							propertiesReader.isGenerazioneListaTrasmissioni(implementazionePdDMittente),
							parametriGenerazioneBustaErrore.getEccezioneProcessamento(),
							this.msgContext.getPddContext());
				}else{
					responseErrorMessage = this.generatoreErrore.buildErroreProtocollo_Processamento(
							integrationFunctionError,
							parametriGenerazioneBustaErrore.getBusta(),integrazione, idTransazione, error,
							securityPropertiesResponse,messageSecurityContext,
							propertiesReader.getGestioneSerializableDB_AttesaAttiva(),
							propertiesReader.getGestioneSerializableDB_CheckInterval(),profiloGestione,
							propertiesReader.getTipoTempoBusta(implementazionePdDMittente),
							propertiesReader.isGenerazioneListaTrasmissioni(implementazionePdDMittente),
							parametriGenerazioneBustaErrore.getEccezioneProcessamento(),
							this.msgContext.getPddContext());
				}
			}else{
				if(erroreCooperazione!=null){
					responseErrorMessage = this.generatoreErrore.buildErroreProtocollo_Intestazione(
							integrationFunctionError,
							parametriGenerazioneBustaErrore.getBusta(),integrazione, idTransazione,erroreCooperazione,
							securityPropertiesResponse,messageSecurityContext,
							propertiesReader.getGestioneSerializableDB_AttesaAttiva(),
							propertiesReader.getGestioneSerializableDB_CheckInterval(),profiloGestione,
							propertiesReader.getTipoTempoBusta(implementazionePdDMittente),
							propertiesReader.isGenerazioneListaTrasmissioni(implementazionePdDMittente),
							this.msgContext.getPddContext());
				}
				else if(erroreIntegrazione!=null){
					throw new CoreException("Method 'generaBustaErroreValidazione' not supported for MessaggioErroreIntegrazione");
				}
				else {
					responseErrorMessage = this.generatoreErrore.buildErroreProtocollo_Intestazione(
							integrationFunctionError,
							parametriGenerazioneBustaErrore.getBusta(),integrazione, idTransazione,error,
							securityPropertiesResponse,messageSecurityContext,
							propertiesReader.getGestioneSerializableDB_AttesaAttiva(),
							propertiesReader.getGestioneSerializableDB_CheckInterval(),profiloGestione,
							propertiesReader.getTipoTempoBusta(implementazionePdDMittente),
							propertiesReader.isGenerazioneListaTrasmissioni(implementazionePdDMittente),
							this.msgContext.getPddContext());
				}
			}

			// ProtocolFactory
			IProtocolFactory<?> protocolFactory = ProtocolFactoryManager.getInstance().getProtocolFactoryByName((String) this.msgContext.getPddContext().getObject(org.openspcoop2.core.constants.Costanti.PROTOCOL_NAME));
						
			//	Tracciamento Busta Ritornata: cambiata nel metodo msgErroreProcessamento
			if(this.msgContext.isTracciamentoAbilitato()){
				EsitoElaborazioneMessaggioTracciato esitoTraccia = 
						EsitoElaborazioneMessaggioTracciato.getEsitoElaborazioneMessaggioInviato();
				SecurityInfo securityInfoResponse  = null;
				boolean functionAsRouter = false; // In questo caso dovrebbe essere sempre false?
				if(functionAsRouter && messageSecurityContext!=null && messageSecurityContext.getDigestReader(responseErrorMessage.getFactory())!=null){
					IValidazioneSemantica validazioneSemantica = protocolFactory.createValidazioneSemantica(openspcoopState.getStatoRichiesta());
					securityInfoResponse = validazioneSemantica.readSecurityInformation(messageSecurityContext.getDigestReader(responseErrorMessage.getFactory()),responseErrorMessage);
				}
				Validatore v = new Validatore(responseErrorMessage,this.msgContext.getPddContext(),openspcoopState.getStatoRichiesta(),
						parametriGenerazioneBustaErrore.getLogCore(), protocolFactory);
				tracciamento.registraRisposta(responseErrorMessage,securityInfoResponse,
						v.getHeaderProtocollo_senzaControlli(), parametriGenerazioneBustaErrore.getBusta(),esitoTraccia,
						Tracciamento.createLocationString(false,this.msgContext.getSourceLocation()),
						parametriGenerazioneBustaErrore.getCorrelazioneApplicativa(),
						parametriGenerazioneBustaErrore.getCorrelazioneApplicativaRisposta());
			}

			// Messaggio diagnostico
			msgDiag.addKeywords(parametriGenerazioneBustaErrore.getBusta(),false);
			IProtocolManager protocolManager = protocolFactory.createProtocolManager();
			if( (!protocolManager.getKeywordMittenteSconosciuto().equals(parametriGenerazioneBustaErrore.getBusta().getDestinatario())) &&
					(!protocolManager.getKeywordTipoMittenteSconosciuto().equals(parametriGenerazioneBustaErrore.getBusta().getTipoDestinatario()))	){
				if(parametriGenerazioneBustaErrore.getBusta().getDestinatario()==null && parametriGenerazioneBustaErrore.getBusta().getTipoDestinatario()==null){
					msgDiag.logPersonalizzato("generazioneMessaggioErroreRisposta.mittenteAnonimo");
				}
				else{
					msgDiag.logPersonalizzato("generazioneMessaggioErroreRisposta");
				}
			} 
			else{
				msgDiag.logPersonalizzato("generazioneMessaggioErroreRisposta.destinatarioSconosciuto");
			}
			
			// Imposto Identificativo Risposta
			// Nota: la bustaRichiesta e' stata trasformata (invertita)
			msgDiag.setIdMessaggioRisposta(parametriGenerazioneBustaErrore.getBusta().getID());
			this.msgContext.getProtocol().setIdRisposta(parametriGenerazioneBustaErrore.getBusta().getID());
			
			if(parametriGenerazioneBustaErrore.getParseException()!=null){
				responseErrorMessage.setParseException(parametriGenerazioneBustaErrore.getParseException());
			}
			
			return responseErrorMessage;

		}catch(Exception e){
			return this.generatoreErrore.buildFault(e,pddContext);
		}
	}


	public void sendRispostaBustaErrore(RicezioneBusteParametriInvioBustaErrore parametriInvioBustaErrore){
		sendRispostaBustaErrore(parametriInvioBustaErrore, true);
	}
	public void sendRispostaBustaErrore(RicezioneBusteParametriInvioBustaErrore parametriInvioBustaErrore,boolean eliminaMessaggioRicevuto){


		GestoreMessaggi msgResponse = null;
		RepositoryBuste repositoryBuste = null;
		boolean httpReply = true;
		
		IOpenSPCoopState openspcoop = parametriInvioBustaErrore.getOpenspcoop();
		Busta bustaRisposta = parametriInvioBustaErrore.getBusta();
		MsgDiagnostico msgDiag = parametriInvioBustaErrore.getMsgDiag();
		Logger logCore = parametriInvioBustaErrore.getLogCore();
		PdDContext pddContext = parametriInvioBustaErrore.getPddContext();
		RequestInfo requestInfo = (RequestInfo) pddContext.getObject(org.openspcoop2.core.constants.Costanti.REQUEST_INFO);
		
		try{
			
			IProtocolFactory<?> protocolFactory = ProtocolFactoryManager.getInstance().getProtocolFactoryByName((String) pddContext.getObject(org.openspcoop2.core.constants.Costanti.PROTOCOL_NAME));
						

			/* ------- Gestione messaggi di richiesta per stateless --------*/
			if(eliminaMessaggioRicevuto && parametriInvioBustaErrore.isOnewayVersione11()){
				GestoreMessaggi msgRichiesta = new GestoreMessaggi(openspcoop,true,
						bustaRisposta.getRiferimentoMessaggio(),Costanti.INBOX,logCore,msgDiag,pddContext);
				msgRichiesta.setReadyForDrop(true);
				msgRichiesta.aggiornaProprietarioMessaggio(TimerGestoreMessaggi.ID_MODULO);
				RepositoryBuste repositoryBustaRichiesta = new RepositoryBuste(openspcoop.getStatoRichiesta(),true,protocolFactory);
				repositoryBustaRichiesta.eliminaBustaStatelessFromInBox(bustaRisposta.getRiferimentoMessaggio());
			}
			
			
			/* ------- Gestione MsgRisposta --------------- */

			// Esamina tipo di risposta (indirizzo di spedizione)
			if( parametriInvioBustaErrore.isNewConnectionForResponse() )
				httpReply = false;
			if( bustaRisposta.getIndirizzoDestinatario()!=null &&
					parametriInvioBustaErrore.isUtilizzoIndirizzoTelematico() )
				httpReply = false;

			// --- Genero una risposta se devo spedirla su di una nuova connessione.
			if(!httpReply){

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


				// Imposto messaggio ritornato nella connection-reply
				OpenSPCoop2Message soapBodyEmpty = MessageUtilities.buildEmptyMessage(OpenSPCoop2MessageFactory.getDefaultMessageFactory(),
						requestInfo.getProtocolRequestMessageType(), MessageRole.RESPONSE);
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
			IntegrationFunctionError integrationFunctionError = AbstractErrorGenerator.getIntegrationInternalError(pddContext); // default
			msgDiag.logErroreGenerico(e, "sendRispostaBustaErrore");
			this.msgContext.setMessageResponse(this.generatoreErrore.buildErroreProcessamento(pddContext,
					integrationFunctionError,
					ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.getErroreIntegrazione(), e));
		}  	

	}

	
	public OpenSPCoop2Message generaRispostaMsgGiaRicevuto(boolean printMsg,Busta bustaRichiesta,Integrazione integrazione,MsgDiagnostico msgDiag,
			OpenSPCoopState openspcoopstate,Logger log,OpenSPCoop2Properties properties, String profiloGestione,
			RuoloBusta ruoloBustaRicevuta,String implementazionePdDMittente,IProtocolFactory<?> protocolFactory,
			IDSoggetto identitaPdD,String idTransazione,Loader loader, boolean oneWayVersione11,
			String implementazionePdDSoggettoMittente,
			Tracciamento tracciamento,
			String idCorrelazioneApplicativa,
			PdDContext pddContext, IntegrationFunctionError integrationFunctionError) throws ProtocolException, TracciamentoException{

		RepositoryBuste repositoryBuste = new RepositoryBuste(openspcoopstate.getStatoRichiesta(), true,protocolFactory);
		History historyBuste = new History(openspcoopstate.getStatoRichiesta(), log);
		Busta bustaHTTPReply = null;
		
		IProtocolVersionManager protocolManager = 
				ProtocolFactoryManager.getInstance().getProtocolFactoryByName((String) this.msgContext.getPddContext().getObject(org.openspcoop2.core.constants.Costanti.PROTOCOL_NAME)).createProtocolVersionManager(profiloGestione);
		
		RequestInfo requestInfo = (RequestInfo) this.msgContext.getPddContext().getObject(org.openspcoop2.core.constants.Costanti.REQUEST_INFO);
		
		boolean consegnaAffidabile = false;
		switch (protocolManager.getConsegnaAffidabile(bustaRichiesta)) {
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
			
			boolean oldGestioneConnessione = false;
			boolean rinegozia = false;
			if(gestoreFiltroDuplicati.releaseRuntimeResourceBeforeCheck() && !openspcoopstate.resourceReleased()) {
				rinegozia = true;
				msgDiag.mediumDebug("Rilascio connessione al database prima di verificare se la richiesta è duplicata ...");
				oldGestioneConnessione = openspcoopstate.isUseConnection();
				openspcoopstate.setUseConnection(true);
				openspcoopstate.commit();
				openspcoopstate.releaseResource();
			}
			
			gestoreFiltroDuplicati.isDuplicata(protocolFactory, bustaRichiesta.getID()); // lo invoco lo stesso per eventuali implementazioni che utilzzano il worflow
			// Aggiorno duplicati
			if(printMsg){
				msgDiag.logPersonalizzato(MsgDiagnosticiProperties.MSG_DIAG_SBUSTAMENTO,"ricezioneBustaDuplicata.count");
			}
			gestoreFiltroDuplicati.incrementaNumeroDuplicati(protocolFactory,bustaRichiesta.getID());
			
			if(gestoreFiltroDuplicati.releaseRuntimeResourceBeforeCheck() && rinegozia) {
				msgDiag.mediumDebug("Rinegozio connessione dopo la verifica di richiesta duplicata ...");
				rinegoziazioneConnessione(openspcoopstate, idTransazione, oldGestioneConnessione);
			}
			
			openspcoopstate.commit();
		}catch(Exception e){
			log.error("Aggiornamento numero duplicati per busta ["+bustaRichiesta.getID()+"] non riuscito: "+e.getMessage(),e);
		}
		if(printMsg){
			msgDiag.logPersonalizzato(MsgDiagnosticiProperties.MSG_DIAG_SBUSTAMENTO,"ricezioneBustaDuplicata");
		}
		
		Imbustamento imbustatore = new Imbustamento(log, protocolFactory, openspcoopstate.getStatoRichiesta());
		
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

			String idBustaRisposta = 
					imbustatore.buildID(identitaPdD, idTransazione, 
							properties.getGestioneSerializableDB_AttesaAttiva(),
							properties.getGestioneSerializableDB_CheckInterval(),
							RuoloMessaggio.RISPOSTA);
			bustaHTTPReply.setID(idBustaRisposta);
			
			Riscontro r = new Riscontro();
			r.setID(bustaRichiesta.getID());
			r.setOraRegistrazione(DateManager.getDate());
			r.setTipoOraRegistrazione(properties.getTipoTempoBusta(implementazionePdDMittente));
			bustaHTTPReply.addRiscontro(r);

		}
		// 1b) 
		else if( org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.ONEWAY.equals(bustaRichiesta.getProfiloDiCollaborazione()) &&
				( (!properties.isGestioneRiscontri(implementazionePdDMittente)) || (!bustaRichiesta.isConfermaRicezione()) )  && 
				( protocolManager.isGenerazioneErroreMessaggioOnewayDuplicato() || properties.isGenerazioneErroreProtocolloFiltroDuplicati(implementazionePdDMittente))){
			http200 = false;
			
			String idBustaRisposta = 
					imbustatore.buildID(identitaPdD, idTransazione, 
							properties.getGestioneSerializableDB_AttesaAttiva(),
							properties.getGestioneSerializableDB_CheckInterval(),
							RuoloMessaggio.RISPOSTA);
			List<Eccezione> v = new ArrayList<>();
			v.add(Eccezione.getEccezioneValidazione(ErroriCooperazione.IDENTIFICATIVO_MESSAGGIO_GIA_PROCESSATO.getErroreCooperazione(),protocolFactory));
			bustaHTTPReply = this.generatoreErrore.getImbustamentoErrore().buildMessaggioErroreProtocollo_Validazione(v,bustaRichiesta,idBustaRisposta,
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
		/** else if(org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.SINCRONO.equals(bustaRichiesta.getProfiloDiCollaborazione())){
		 non ci si entra mai in questo caso. Il metodo non viene mai chiamato nel caso di sincrono
		}*/
		
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
					// ServizioCorrelato da impostare nella ricevuta asincrona
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
				
				String idBustaRisposta = 
						imbustatore.buildID(identitaPdD, idTransazione, 
								properties.getGestioneSerializableDB_AttesaAttiva(),
								properties.getGestioneSerializableDB_CheckInterval(),
								RuoloMessaggio.RISPOSTA);
				bustaHTTPReply.setID(idBustaRisposta);
			}
		}
		// 4
		else{
			if( protocolManager.isGenerazioneErroreMessaggioOnewayDuplicato() || properties.isGenerazioneErroreProtocolloFiltroDuplicati(implementazionePdDMittente)){
				http200 = false;
				
				String idBustaRisposta = 
						imbustatore.buildID(identitaPdD, idTransazione, 
								properties.getGestioneSerializableDB_AttesaAttiva(),
								properties.getGestioneSerializableDB_CheckInterval(),
								RuoloMessaggio.RISPOSTA);
				List<Eccezione> v = new ArrayList<>();
				v.add(Eccezione.getEccezioneValidazione(ErroriCooperazione.IDENTIFICATIVO_MESSAGGIO_GIA_PROCESSATO.getErroreCooperazione(),protocolFactory));
				bustaHTTPReply = this.generatoreErrore.getImbustamentoErrore().buildMessaggioErroreProtocollo_Validazione(v,bustaRichiesta,idBustaRisposta,
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
			return MessageUtilities.buildEmptyMessage(OpenSPCoop2MessageFactory.getDefaultMessageFactory(),
					requestInfo.getProtocolRequestMessageType(), MessageRole.RESPONSE);
		}else{
						
			OpenSPCoop2Message msg = null;
			if(http200){
				msg = MessageUtilities.buildEmptyMessage(OpenSPCoop2MessageFactory.getDefaultMessageFactory(),
						requestInfo.getProtocolRequestMessageType(), MessageRole.RESPONSE);
			}
			else{
				msg = this.generatoreErrore.buildErroreIntestazione(pddContext,integrationFunctionError);
			}
			
			
			ProtocolMessage protocolMessage = imbustatore.imbustamentoRisposta(msg,pddContext,
					bustaHTTPReply,bustaRichiesta,
					integrazione,false,false,null,
					FaseImbustamento.PRIMA_SICUREZZA_MESSAGGIO);
			if(protocolMessage!=null && !protocolMessage.isPhaseUnsupported()) {
				msg = protocolMessage.getMessage(); // updated
			}
			
			protocolMessage = imbustatore.imbustamentoRisposta(msg,pddContext,
					bustaHTTPReply,bustaRichiesta,
					integrazione,false,false,null,
					FaseImbustamento.DOPO_SICUREZZA_MESSAGGIO);
			if(protocolMessage!=null && !protocolMessage.isPhaseUnsupported()) {
				msg = protocolMessage.getMessage(); // updated
			}
			
			
			//			Tracciamento Busta Ritornata: cambiata nel metodo msgErroreProcessamento
			if(this.msgContext.isTracciamentoAbilitato()){
				EsitoElaborazioneMessaggioTracciato esitoTraccia = 
						EsitoElaborazioneMessaggioTracciato.getEsitoElaborazioneMessaggioInviato();
				SecurityInfo securityInfoResponse  = null;
				/**boolean functionAsRouter = false; // In questo caso dovrebbe essere sempre false?
				if(functionAsRouter){
					if(messageSecurityContext!=null && messageSecurityContext.getDigestReader()!=null){
						IValidazioneSemantica validazioneSemantica = protocolFactory.createValidazioneSemantica();
						securityInfoResponse = validazioneSemantica.readSecurityInformation(messageSecurityContext.getDigestReader(),msg);
					}
				}*/
				Validatore v = new Validatore(msg,this.msgContext.getPddContext(),openspcoopstate.getStatoRichiesta(),
						log, protocolFactory);
				tracciamento.registraRisposta(msg,securityInfoResponse,
						v.getHeaderProtocollo_senzaControlli(), bustaHTTPReply,esitoTraccia,
						Tracciamento.createLocationString(false,this.msgContext.getSourceLocation()),
						idCorrelazioneApplicativa,
						null);
			}
			
			return msg;
		}
	}
	
	private void rinegoziazioneConnessione(OpenSPCoopState openspcoopstate, String idTransazione, boolean oldGestioneConnessione) throws ProtocolException {
		try{
			openspcoopstate.updateResource(idTransazione);
			openspcoopstate.setUseConnection(oldGestioneConnessione);
		}catch(Exception e){
			throw new ProtocolException("Rinegoziazione connessione dopo la verifica di richiesta duplicata fallita: "+e.getMessage(),e);
		} 
	}
}
