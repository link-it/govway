/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it). 
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

package org.openspcoop2.pdd.mdb;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.soap.SOAPFault;

import org.openspcoop2.core.config.Connettore;
import org.openspcoop2.core.config.DumpConfigurazione;
import org.openspcoop2.core.config.GestioneErrore;
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.Property;
import org.openspcoop2.core.config.driver.DriverConfigurazioneException;
import org.openspcoop2.core.config.driver.DriverConfigurazioneNotFound;
import org.openspcoop2.core.constants.CostantiConnettori;
import org.openspcoop2.core.constants.TipiConnettore;
import org.openspcoop2.core.constants.TipoPdD;
import org.openspcoop2.core.id.IDPortaDelegata;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.driver.IDServizioFactory;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.OpenSPCoop2MessageFactory;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.message.soap.SoapUtils;
import org.openspcoop2.pdd.config.ConfigurazionePdDManager;
import org.openspcoop2.pdd.config.MessageSecurityConfig;
import org.openspcoop2.pdd.core.AbstractCore;
import org.openspcoop2.pdd.core.CostantiPdD;
import org.openspcoop2.pdd.core.EJBUtils;
import org.openspcoop2.pdd.core.GestoreMessaggi;
import org.openspcoop2.pdd.core.IntegrationContext;
import org.openspcoop2.pdd.core.MTOMProcessor;
import org.openspcoop2.pdd.core.PdDContext;
import org.openspcoop2.pdd.core.ProtocolContext;
import org.openspcoop2.pdd.core.connettori.ConnettoreBase;
import org.openspcoop2.pdd.core.connettori.ConnettoreBaseHTTP;
import org.openspcoop2.pdd.core.connettori.ConnettoreMsg;
import org.openspcoop2.pdd.core.connettori.ConnettoreUtils;
import org.openspcoop2.pdd.core.connettori.GestoreErroreConnettore;
import org.openspcoop2.pdd.core.connettori.IConnettore;
import org.openspcoop2.pdd.core.connettori.InfoConnettoreUscita;
import org.openspcoop2.pdd.core.handlers.GestoreHandlers;
import org.openspcoop2.pdd.core.handlers.HandlerException;
import org.openspcoop2.pdd.core.handlers.OutResponseContext;
import org.openspcoop2.pdd.core.handlers.PostOutResponseContext;
import org.openspcoop2.pdd.core.integrazione.UtilitiesIntegrazione;
import org.openspcoop2.pdd.core.state.IOpenSPCoopState;
import org.openspcoop2.pdd.core.state.OpenSPCoopStateException;
import org.openspcoop2.pdd.core.transazioni.Transaction;
import org.openspcoop2.pdd.core.transazioni.TransactionContext;
import org.openspcoop2.pdd.logger.Dump;
import org.openspcoop2.pdd.logger.MsgDiagnosticiProperties;
import org.openspcoop2.pdd.logger.MsgDiagnostico;
import org.openspcoop2.pdd.logger.Tracciamento;
import org.openspcoop2.pdd.services.core.FlowProperties;
import org.openspcoop2.pdd.timers.TimerGestoreMessaggi;
import org.openspcoop2.protocol.engine.RequestInfo;
import org.openspcoop2.protocol.engine.constants.Costanti;
import org.openspcoop2.protocol.engine.driver.ProfiloDiCollaborazione;
import org.openspcoop2.protocol.engine.driver.RepositoryBuste;
import org.openspcoop2.protocol.engine.validator.Validatore;
import org.openspcoop2.protocol.registry.RegistroServiziManager;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.BustaRawContent;
import org.openspcoop2.protocol.sdk.Eccezione;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.Integrazione;
import org.openspcoop2.protocol.sdk.ProtocolMessage;
import org.openspcoop2.protocol.sdk.SecurityInfo;
import org.openspcoop2.protocol.sdk.Trasmissione;
import org.openspcoop2.protocol.sdk.builder.EsitoTransazione;
import org.openspcoop2.protocol.sdk.builder.ProprietaManifestAttachments;
import org.openspcoop2.protocol.sdk.config.IProtocolVersionManager;
import org.openspcoop2.protocol.sdk.constants.FaseImbustamento;
import org.openspcoop2.protocol.sdk.constants.FaseSbustamento;
import org.openspcoop2.protocol.sdk.constants.LivelloRilevanza;
import org.openspcoop2.protocol.sdk.constants.RuoloMessaggio;
import org.openspcoop2.protocol.sdk.state.IState;
import org.openspcoop2.protocol.sdk.state.StatefulMessage;
import org.openspcoop2.protocol.sdk.tracciamento.EsitoElaborazioneMessaggioTracciato;
import org.openspcoop2.protocol.sdk.validator.IValidatoreErrori;
import org.openspcoop2.protocol.sdk.validator.IValidazioneSemantica;
import org.openspcoop2.protocol.sdk.validator.ProprietaValidazione;
import org.openspcoop2.protocol.sdk.validator.ProprietaValidazioneErrori;
import org.openspcoop2.security.message.MessageSecurityContext;
import org.openspcoop2.security.message.MessageSecurityContextParameters;
import org.openspcoop2.security.message.constants.SecurityConstants;
import org.openspcoop2.security.message.engine.MessageSecurityFactory;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.digest.IDigestReader;
import org.openspcoop2.utils.rest.problem.ProblemRFC7807;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.slf4j.Logger;



/**
 * Contiene la libreria InoltroRisposte
 *
 * @author Poli Andrea (apoli@link.it)
 * @author Tronci Fabio (tronci@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class InoltroRisposte extends GenericLib{

	public final static String ID_MODULO = "InoltroRisposte";

	public InoltroRisposte(Logger log) throws GenericLibException {
		super(InoltroRisposte.ID_MODULO, log);
		inizializza();
	}

	@Override
	public EsitoLib _onMessage(IOpenSPCoopState openspcoopstate,
			RegistroServiziManager registroServiziManager,ConfigurazionePdDManager configurazionePdDManager, 
			MsgDiagnostico msgDiag) throws OpenSPCoopStateException {
		InoltroRisposteMessage inoltroRisposteMsg = (InoltroRisposteMessage) openspcoopstate.getMessageLib();		
		EsitoLib esito = new EsitoLib();
		
		/* PddContext */
		PdDContext pddContext = inoltroRisposteMsg.getPddContext();
		String idTransazione = PdDContext.getValue(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE, pddContext);
		@SuppressWarnings("unused")
		RequestInfo requestInfo = (RequestInfo) pddContext.getObject(org.openspcoop2.core.constants.Costanti.REQUEST_INFO);
		
		/* Busta e tipo di implementazione PdD con cui interoperare */
		// Busta da inviare (tracciamento e Message-Security)
		Busta busta = inoltroRisposteMsg.getBustaRisposta();
		String implementazionePdDDestinatario = inoltroRisposteMsg.getImplementazionePdDSoggettoDestinatario();
		
		IDSoggetto identitaPdD = inoltroRisposteMsg.getDominio();
		msgDiag.setDominio(identitaPdD);  // imposto anche il dominio nel msgDiag
		
		// Tipo di busta da inoltrare
		boolean inoltroSegnalazioneErrore = 
			inoltroRisposteMsg.getInoltroSegnalazioneErrore();
		
		//	Aggiornamento Informazioni
		msgDiag.setPrefixMsgPersonalizzati(MsgDiagnosticiProperties.MSG_DIAG_INOLTRO_BUSTE);
		
		// Gestisco i log come fossero una richiesta, come se fossi nel modulo InoltroBuste
		msgDiag.addKeywords(busta, true);

		// Dati consegna
		IDSoggetto soggettoMittente = new IDSoggetto(busta.getTipoMittente(),busta.getMittente());
		IDSoggetto soggettoDestinatario = new IDSoggetto(busta.getTipoDestinatario(),busta.getDestinatario());
		IDServizio idServizio = null;
		try{
			idServizio = IDServizioFactory.getInstance().getIDServizioFromValues(busta.getTipoServizio(),busta.getServizio(), 
					soggettoDestinatario, busta.getVersioneServizio()); 
		}catch(Exception e){
			msgDiag.logErroreGenerico(e, "IDServizioFactory.getIDServizioFromValues");  
			openspcoopstate.releaseResource();
			esito.setStatoInvocazioneErroreNonGestito(e);
			esito.setEsitoInvocazione(false); 
			return esito;
		}
		idServizio.setAzione(busta.getAzione());

		String idMessageResponse = busta.getID();
		
		msgDiag.setIdMessaggioRichiesta(busta.getRiferimentoMessaggio());
		msgDiag.setIdMessaggioRisposta(busta.getID());
		msgDiag.setFruitore(soggettoMittente);
		msgDiag.setServizio(idServizio);
		msgDiag.setIdCorrelazioneApplicativa(inoltroRisposteMsg.getIdCorrelazioneApplicativa());
		msgDiag.addKeyword(CostantiPdD.KEY_ID_CORRELAZIONE_APPLICATIVA, inoltroRisposteMsg.getIdCorrelazioneApplicativa());


		/* ------------------ Inizializzo stato OpenSPCoop  --------------- */
		msgDiag.mediumDebug("Inizializzo stato per la gestione della richiesta...");
		openspcoopstate.initResource(identitaPdD, InoltroRisposte.ID_MODULO,idTransazione);
		registroServiziManager.updateState(openspcoopstate.getStatoRichiesta(),openspcoopstate.getStatoRisposta());
		configurazionePdDManager.updateState(openspcoopstate.getStatoRichiesta(),openspcoopstate.getStatoRisposta());
		msgDiag.updateState(openspcoopstate.getStatoRichiesta(),openspcoopstate.getStatoRisposta());
		
		/* Protocol Factory */
		IProtocolFactory<?> protocolFactory = null;
		IValidazioneSemantica validazioneSemantica = null;
		org.openspcoop2.protocol.sdk.config.ITraduttore traduttore = null;
		try{
			protocolFactory = this.protocolFactoryManager.getProtocolFactoryByName((String) pddContext.getObject(org.openspcoop2.core.constants.Costanti.PROTOCOL_NAME));
			traduttore = protocolFactory.createTraduttore();
			validazioneSemantica = protocolFactory.createValidazioneSemantica(openspcoopstate.getStatoRichiesta());
		}catch(Exception e){
			msgDiag.logErroreGenerico(e, "ProtocolFactory.instanziazione"); 
			openspcoopstate.releaseResource();
			esito.setEsitoInvocazione(false); 
			esito.setStatoInvocazioneErroreNonGestito(e);
			return esito;
		}
		
		
		// Transaction
		Transaction transactionNullable = null;
		try{
			transactionNullable = TransactionContext.getTransaction(idTransazione);
		}catch(Exception e){
			// La transazione potrebbe essere stata eliminata nelle comunicazioni stateful
//			msgDiag.logErroreGenerico(e, "getTransaction"); 
//			openspcoopstate.releaseResource();
//			esito.setEsitoInvocazione(false); 
//			esito.setStatoInvocazioneErroreNonGestito(e);
//			return esito;
		}
		
		msgDiag.setPddContext(pddContext, protocolFactory);	


		/* ------------- STATO RICHIESTA E STATO RISPOSTA ------------------------------- */
		//IState statoRichiesta = openSPCoopState.getStatoRichiesta();
		//IState statoRisposta = openSPCoopState.getStatoRisposta();

		/* ----------------- ID RICHIESTA ---------------------------------------------- */
		String idMessageRequest = openspcoopstate.getIDMessaggioSessione();

		/* ------------------ Inizializzazione Contesto di gestione della Richiesta --------------- */
		msgDiag.mediumDebug("Inizializzo contesto per la gestione...");

		boolean oneWayVersione11 = inoltroRisposteMsg.isOneWayVersione11();	
		
		// Check FunctionRouting  
		boolean functionAsRouter = false;
		msgDiag.mediumDebug("Esamina modalita' di ricezione (PdD/Router)...");
		boolean existsSoggetto = false;
		try{
			existsSoggetto = configurazionePdDManager.existsSoggetto(soggettoMittente);
		}catch(Exception e){
			msgDiag.logErroreGenerico(e, "existsSoggetto("+soggettoMittente.toString()+")");  
			openspcoopstate.releaseResource();
			esito.setStatoInvocazioneErroreNonGestito(e);
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
				esito.setStatoInvocazioneErroreNonGestito(e);
				esito.setEsitoInvocazione(false); 
				return esito;
			}
			if(routerFunctionActive){
				functionAsRouter = true;
			}else{
				msgDiag.logPersonalizzato("routingTable.soggettoFruitoreNonGestito");
				
				try{
					GestoreMessaggi msgResponse = new GestoreMessaggi(openspcoopstate, false,idMessageResponse,Costanti.OUTBOX,msgDiag,pddContext);
					msgResponse.setOneWayVersione11(oneWayVersione11);
					msgResponse.aggiornaProprietarioMessaggio(TimerGestoreMessaggi.ID_MODULO);
					
					RepositoryBuste repositoryBuste = new RepositoryBuste(openspcoopstate.getStatoRisposta(), false,protocolFactory);
					repositoryBuste.eliminaBustaStatelessFromOutBox(busta.getID());
					
					openspcoopstate.commit();
				}catch(Exception e){
					msgDiag.logErroreGenerico(e,"EliminazioneMessaggioSoggettoFruitoreNonGestito");
					openspcoopstate.releaseResource();
					esito.setStatoInvocazioneErroreNonGestito(e);
					esito.setEsitoInvocazione(false); 
					return esito;
				}
				
				openspcoopstate.releaseResource();
				esito.setStatoInvocazione(EsitoLib.ERRORE_NON_GESTITO,
						"routingTable.soggettoFruitoreNonGestito");
				esito.setEsitoInvocazione(true); 
				return esito;
			}
		}


		// Funzione
		TipoPdD tipoPorta = TipoPdD.APPLICATIVA;
		if(functionAsRouter)
			tipoPorta = TipoPdD.ROUTER;


		// Profilo di gestione
		String versioneProtocollo = inoltroRisposteMsg.getProfiloGestione();
		msgDiag.mediumDebug("Profilo di gestione ["+InoltroRisposte.ID_MODULO+"] della busta: "+versioneProtocollo);


		// EJBUtils (per eventuali errori)
		EJBUtils ejbUtils = null;
		try{
			ejbUtils = new EJBUtils(identitaPdD,tipoPorta,InoltroRisposte.ID_MODULO,idMessageRequest,
					idMessageResponse,Costanti.OUTBOX,openspcoopstate,msgDiag,functionAsRouter,
					inoltroRisposteMsg.getImplementazionePdDSoggettoMittente(),
					inoltroRisposteMsg.getImplementazionePdDSoggettoDestinatario(),
					versioneProtocollo,pddContext);
		}catch(Exception e){
			msgDiag.logErroreGenerico(e, "EJBUtils.new");  
			openspcoopstate.releaseResource(); 
			esito.setStatoInvocazioneErroreNonGestito(e);
			esito.setEsitoInvocazione(false); 
			return esito;
		}
		// Oneway versione 11
		ejbUtils.setOneWayVersione11(oneWayVersione11);


		// GestoriMessaggio
		GestoreMessaggi msgResponse = new GestoreMessaggi(openspcoopstate, false,idMessageResponse,Costanti.OUTBOX,msgDiag,pddContext);
		msgResponse.setOneWayVersione11(oneWayVersione11);
		GestoreMessaggi msgConnectionReply = null; 
		try{
			if(msgResponse.isRiconsegnaMessaggio(null) == false){
				msgDiag.logPersonalizzato("riconsegnaMessaggioPrematura");
				openspcoopstate.releaseResource(); 
				esito.setStatoInvocazione(EsitoLib.ERRORE_NON_GESTITO,
						msgDiag.getMessaggio_replaceKeywords("riconsegnaMessaggioPrematura"));
				esito.setEsitoInvocazione(false); 
				return esito;
			}
		}catch(Exception e){
			msgDiag.logErroreGenerico(e, "msgRequest.isRiconsegnaMessaggio()");
			ejbUtils.rollbackMessage("Errore verifica riconsegna messaggio", esito);
			openspcoopstate.releaseResource(); 
			esito.setStatoInvocazioneErroreNonGestito(e);
			esito.setEsitoInvocazione(true); 
			return esito;
		}
		

		// Repository
		RepositoryBuste repositoryBuste = null;
		RepositoryBuste repositoryConnectionReply = null;

		// Tracciamento
		org.openspcoop2.pdd.logger.Tracciamento tracciamento;
		try {
			tracciamento = new org.openspcoop2.pdd.logger.Tracciamento(identitaPdD,InoltroBuste.ID_MODULO,pddContext,tipoPorta,msgDiag.getPorta(),
					openspcoopstate.getStatoRichiesta(),openspcoopstate.getStatoRisposta());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			msgDiag.logErroreGenerico(e, "ProtocolFactory.instanziazione"); 
			openspcoopstate.releaseResource();
			esito.setEsitoInvocazione(false); 
			esito.setStatoInvocazioneErroreNonGestito(e);
			return esito;
		}
		// Proprieta Manifest Attachments
		ProprietaManifestAttachments proprietaManifestAttachments = 
			this.propertiesReader.getProprietaManifestAttachments(implementazionePdDDestinatario);

		// Read QualifiedAttribute
		boolean readQualifiedAttribute = this.propertiesReader.isReadQualifiedAttribute(implementazionePdDDestinatario);









		// Punto di inizio per la transazione.
		Validatore validatoreProtocolConnectionReply = null;
		IConnettore connectorSender = null;
		String location = "";
		try{

			IProtocolVersionManager protocolManager = protocolFactory.createProtocolVersionManager(versioneProtocollo);



			/* ------------------ Routing --------------- */

			Connettore connettore = null;
			String erroreRicercaConnettore = null;
			if(functionAsRouter){
				// Un router puo' inoltrare solo verso il connettore del soggetto destinatario
				msgDiag.logPersonalizzato("(Router) "+msgDiag.getMessaggio("routingTable.esaminaInCorso"), 
						msgDiag.getLivello("routingTable.esaminaInCorso"),
						msgDiag.getCodice("routingTable.esaminaInCorso"));
				try{
					connettore = configurazionePdDManager.getForwardRoute(soggettoDestinatario,functionAsRouter);
				}catch(Exception e){
					erroreRicercaConnettore = e.getMessage();
				}
				msgDiag.logPersonalizzato("(Router) "+msgDiag.getMessaggio("routingTable.esaminaEffettuata"), 
						msgDiag.getLivello("routingTable.esaminaEffettuata"),
						msgDiag.getCodice("routingTable.esaminaEffettuata"));			
			}else{
				if(configurazionePdDManager.isUtilizzoIndirizzoTelematico() && busta.getIndirizzoDestinatario()!=null){
					msgDiag.logPersonalizzato("routingTable.utilizzoIndirizzoTelematico");
					Property locationIndTel = new Property();
					locationIndTel.setNome(CostantiConnettori.CONNETTORE_LOCATION);
					locationIndTel.setValore(busta.getIndirizzoDestinatario());
					connettore = new Connettore();
					connettore.setTipo(TipiConnettore.HTTP.getNome());
					connettore.addProperty(locationIndTel);
				}else{
					msgDiag.logPersonalizzato("routingTable.esaminaInCorso");
					if(inoltroSegnalazioneErrore){
						try{
							// provo a cercare un connettore specializzato
							//log.info("Cerco busta per Mittente["+soggettoMittente.toString()+"] Destinatario["+soggettoDestinatario.toString()+"] Servizio["+busta.getTipoServizio()+busta.getServizio()+"] Azione["+busta.getAzione()+"]");
							connettore = configurazionePdDManager.getForwardRoute(soggettoMittente,idServizio,functionAsRouter);
						}catch(Exception e){
							erroreRicercaConnettore = "RicercaConnettoreSpecializzato, "+e.getMessage();
						}
						// provo ad inviarlo solo al soggetto
						if(connettore==null){
							try{
								connettore = configurazionePdDManager.getForwardRoute(soggettoDestinatario,functionAsRouter);
							}catch(Exception e){
								erroreRicercaConnettore = "\nRicercaConnettore, "+e.getMessage();
							}
						}
					}else{
						try{
							connettore = configurazionePdDManager.getForwardRoute(soggettoDestinatario,functionAsRouter);
						}catch(Exception e){
							erroreRicercaConnettore = e.getMessage();
						}
					}
					msgDiag.logPersonalizzato("routingTable.esaminaEffettuata");
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
				ejbUtils.rollbackMessage(erroreRicercaConnettore, esito);
				openspcoopstate.releaseResource();
				esito.setStatoInvocazione(EsitoLib.ERRORE_NON_GESTITO,erroreRicercaConnettore);
				esito.setEsitoInvocazione(false); 
				return esito;
			}









			/* ----------- Trasmissione ------------------ */
			String soggettoDestinatarioTrasmissione = "";
			Trasmissione tras = null;
			if(this.propertiesReader.isGenerazioneListaTrasmissioni(implementazionePdDDestinatario)){
				msgDiag.mediumDebug("Aggiunta trasmissione della busta ...");
				// Tracciamento in busta
				tras = new Trasmissione();
				tras.setOrigine(identitaPdD.getNome());
				tras.setTipoOrigine(identitaPdD.getTipo());
				IDSoggetto destTrasm = null;
				if(connettore.getNomeDestinatarioTrasmissioneBusta()!=null && connettore.getTipoDestinatarioTrasmissioneBusta()!=null)
					destTrasm = new IDSoggetto(connettore.getTipoDestinatarioTrasmissioneBusta(),connettore.getNomeDestinatarioTrasmissioneBusta());
				else
					destTrasm = new IDSoggetto(busta.getTipoDestinatario(),
							busta.getDestinatario());	 
				tras.setDestinazione(destTrasm.getNome());
				tras.setTipoDestinazione(destTrasm.getTipo());
				tras.setOraRegistrazione(busta.getOraRegistrazione());
				tras.setTempo(this.propertiesReader.getTipoTempoBusta(implementazionePdDDestinatario));
				busta.addTrasmissione(tras);
				// net hop is Router?	
				if( (soggettoDestinatario.getNome().equals(destTrasm.getNome())==false) ||
						(soggettoDestinatario.getTipo().equals(destTrasm.getTipo())==false)	)
					soggettoDestinatarioTrasmissione = " (tramite router "+destTrasm.getTipo()+"/"+destTrasm.getNome()+")";
			}
			msgDiag.addKeyword(CostantiPdD.KEY_DESTINATARIO_TRASMISSIONE, soggettoDestinatarioTrasmissione);







			/* ------------  Ricostruzione Messaggio Soap da spedire ------------- */
			msgDiag.mediumDebug("Lettura messaggio da spedire...");
			OpenSPCoop2Message responseMessage = null;
			//TempiAttraversamentoPDD tempiAttraversamentoGestioneMessaggi = null;
			//DimensioneMessaggiAttraversamentoPdD dimensioneMessaggiAttraversamentoGestioneMessaggi = null;
			try{
				responseMessage = msgResponse.getMessage();
				//tempiAttraversamentoGestioneMessaggi = msgResponse.getTempiAttraversamentoPdD();
				//dimensioneMessaggiAttraversamentoGestioneMessaggi = msgResponse.getDimensioneMessaggiAttraversamentoPdD();
			}catch(Exception e){
				msgDiag.logErroreGenerico(e, "msgResponse.getMessage()");
				ejbUtils.rollbackMessage("Ricostruzione del messaggio Soap da Spedire non riuscita.", esito);
				openspcoopstate.releaseResource();
				esito.setStatoInvocazioneErroreNonGestito(e);
				esito.setEsitoInvocazione(false); 
				return esito;
			}	



			
			
			
			
			/* ------------ Porta Applicativa ------------ */
			PortaApplicativa pa = null;
			if(functionAsRouter==false){
				try{
					IDServizio idServizioPA = IDServizioFactory.getInstance().getIDServizioFromValues(busta.getTipoServizio(),busta.getServizio(), 
							busta.getTipoMittente(),busta.getMittente(), 
							busta.getVersioneServizio());
					idServizioPA.setAzione(busta.getAzione());
					pa = getPortaApplicativa(configurazionePdDManager, idServizioPA);
				}catch(Exception e){
					if( !(e instanceof DriverConfigurazioneNotFound)  ){
						msgDiag.logErroreGenerico(e, "getPortaApplicativa_SafeMethod");
						ejbUtils.rollbackMessage("Ricostruzione errore durante la lettura della porta applicativa.", esito);
						openspcoopstate.releaseResource();
						esito.setStatoInvocazioneErroreNonGestito(e);
						esito.setEsitoInvocazione(false); 
						return esito;
					}
				}	
			}
			
			
			
			
			
			



			/* ------------  Gestione Funzionalita' speciali per Attachments (Manifest) ------------- */	
			boolean scartaBody = false;
			// viene controllato servizio is not null, poiche' i riscontri non hanno un servizio
			if(functionAsRouter==false && inoltroSegnalazioneErrore==false && busta.getTipoServizio()!=null && busta.getServizio()!=null){
				boolean allegaBody = configurazionePdDManager.isAllegaBody(pa);
				if(allegaBody){
					// E' stato effettuato prima l'inserimento del body come allegato.
					// Forzo lo scartamento.
					scartaBody = true;
				}else{
					scartaBody = configurazionePdDManager.isScartaBody(pa);
				}
			}




			

			/* ------------  Aggiunta eccezioni di livello info riscontrate dalla validazione, se profilo e' lineeGuida1.1 ------------- */	
			if(functionAsRouter==false){
				if(  (inoltroSegnalazioneErrore==false) && (busta.sizeListaEccezioni()==0) && protocolManager.isEccezioniLivelloInfoAbilitato() ){
					RepositoryBuste repositoryBustaRichiesta = new RepositoryBuste(openspcoopstate.getStatoRisposta(), false, protocolFactory);
					List<Eccezione> erroriValidazione = repositoryBustaRichiesta.getErrorsFromInBox(busta.getRiferimentoMessaggio());
					for(int i=0; i<erroriValidazione.size();i++){
						Eccezione ec = erroriValidazione.get(i);
						if(LivelloRilevanza.INFO.equals(ec.getRilevanza())){
							busta.addEccezione(ec);
						}
					}
				}
			}






			/* ------------  Imbustamento ------------- */	
			msgDiag.mediumDebug("Imbustamento ...");
			BustaRawContent<?> headerBusta = null;
			org.openspcoop2.protocol.engine.builder.Imbustamento imbustatore = null;
			boolean gestioneManifest = false;
			RuoloMessaggio ruoloMessaggio = null;
			if(inoltroRisposteMsg.isImbustamento()){
				try{
					ruoloMessaggio = RuoloMessaggio.RISPOSTA;
					if(inoltroSegnalazioneErrore)
						ruoloMessaggio = RuoloMessaggio.RICHIESTA;
					imbustatore = 
							new org.openspcoop2.protocol.engine.builder.Imbustamento(this.log,protocolFactory, openspcoopstate.getStatoRichiesta());
	
					gestioneManifest = configurazionePdDManager.isGestioneManifestAttachments();
					// viene controllato servizio is not null, poiche' i riscontri non hanno un servizio
					if(functionAsRouter==false && inoltroSegnalazioneErrore==false  && busta.getTipoServizio()!=null && busta.getServizio()!=null){
						gestioneManifest = configurazionePdDManager.isGestioneManifestAttachments(pa,protocolFactory);
					}
					if(functionAsRouter && 
							!( identitaPdD.getTipo().equals(busta.getTipoMittente()) && identitaPdD.getNome().equals(busta.getMittente()) ) 
					){
						// Aggiungo trasmissione solo se la busta e' stata generata dalla porta di dominio destinataria della richiesta.
						// Se il mittente e' il router, logicamente la busta sara' un errore generato dal router
						msgDiag.highDebug("Tipo Messaggio Risposta prima dell'imbustamento ["+responseMessage.getClass().getName()+"]");
						ProtocolMessage protocolMessage = imbustatore.addTrasmissione(responseMessage, tras, readQualifiedAttribute,
								FaseImbustamento.PRIMA_SICUREZZA_MESSAGGIO);
						if(protocolMessage!=null && !protocolMessage.isPhaseUnsupported()) {
							headerBusta = protocolMessage.getBustaRawContent();
							responseMessage = protocolMessage.getMessage(); // updated
						}
						msgDiag.highDebug("Tipo Messaggio Risposta dopo l'imbustamento ["+responseMessage.getClass().getName()+"]");
					}
					else{
						Integrazione integrazione = new Integrazione();
						integrazione.setStateless(false);
						msgDiag.highDebug("Tipo Messaggio Risposta prima dell'imbustamento ["+responseMessage.getClass().getName()+"]");
						ProtocolMessage protocolMessage = null;
						if(RuoloMessaggio.RICHIESTA.equals(ruoloMessaggio)) {
							protocolMessage = imbustatore.imbustamentoRichiesta(responseMessage,pddContext,
									busta,
									integrazione,gestioneManifest,scartaBody,proprietaManifestAttachments,
									FaseImbustamento.PRIMA_SICUREZZA_MESSAGGIO);
						}
						else {
							protocolMessage = imbustatore.imbustamentoRisposta(responseMessage,pddContext,
									busta,null,
									integrazione,gestioneManifest,scartaBody,proprietaManifestAttachments,
									FaseImbustamento.DOPO_SICUREZZA_MESSAGGIO);
						}
						if(protocolMessage!=null && !protocolMessage.isPhaseUnsupported()) {
							headerBusta = protocolMessage.getBustaRawContent();
							responseMessage = protocolMessage.getMessage(); // updated
						}
						msgDiag.highDebug("Tipo Messaggio Risposta dopo l'imbustamento ["+responseMessage.getClass().getName()+"]");
					}
				}catch(Exception e){
					if(functionAsRouter)
						msgDiag.logErroreGenerico(e, "imbustatore.before-security.addTrasmissione");
					else
						msgDiag.logErroreGenerico(e, "imbustatore.before-security.imbustamento");
					ejbUtils.rollbackMessage("Imbustamento non riuscito.", esito);
					openspcoopstate.releaseResource();  
					esito.setStatoInvocazioneErroreNonGestito(e);
					esito.setEsitoInvocazione(false); 
					return esito;
				}
			}






			/* ----------- Raccolta FlowParameter MTOM / Security ------------ */
			msgDiag.mediumDebug("Raccolta FlowParameter MTOM / Security proprieta...");
			MTOMProcessor mtomProcessor = null;
			MessageSecurityConfig securityConfig = null;
			FlowProperties flowProperties = null;
			if(functionAsRouter==false){
				
				try{
					flowProperties = this.getFlowProperties(busta,configurazionePdDManager,openspcoopstate.getStatoRisposta(),
							msgDiag,protocolFactory,pa);
				}catch(Exception e){
					msgDiag.logErroreGenerico(e, "RaccoltaFlowParameter_MTOM_Security");
					ejbUtils.rollbackMessage("RaccoltaFlowParameter_MTOM_Security non riuscita:"+e.getMessage(), esito);
					openspcoopstate.releaseResource();
					esito.setStatoInvocazioneErroreNonGestito(e);
					esito.setEsitoInvocazione(false); 
					return esito;
				}
				
				if(flowProperties!=null){
					msgDiag.mediumDebug("init MTOM Processor ...");
					mtomProcessor = new MTOMProcessor(flowProperties.mtom, flowProperties.messageSecurity, 
							tipoPorta, msgDiag, this.log, pddContext);
					securityConfig = flowProperties.messageSecurity;
				}
			}
			
			


			
			
			
			
			
			/* ----------- MTOM Processor BeforeSecurity ------------ */
			msgDiag.mediumDebug("MTOM Processor [BeforeSecurity]...");
			try{
				mtomProcessor.mtomBeforeSecurity(responseMessage, flowProperties.tipoMessaggio);
			}catch(Exception e){
				// L'errore viene registrato dentro il metodo mtomProcessor.mtomBeforeSecurity
				//msgDiag.logErroreGenerico(e,"MTOMProcessor(BeforeSec-"+mtomProcessor.getMTOMProcessorType()+")");
			
				ejbUtils.rollbackMessage("Riscontrato errore durante la gestione MTOM(BeforeSec-"+mtomProcessor.getMTOMProcessorType()+"): "+e.getMessage(), esito);
				openspcoopstate.releaseResource();
				esito.setStatoInvocazioneErroreNonGestito(e);
				esito.setEsitoInvocazione(false); 
				return esito;
			}
			
			
			
			
			
			
			


			/* ------------  Gestione MessageSecurity ------------- */
			SecurityInfo securityInfo = null;
			if(inoltroSegnalazioneErrore == false && functionAsRouter==false){
				msgDiag.mediumDebug("Gestione MessageSecurity del messaggio da inoltrare...");
								
				// Imposto un context di Base 
				MessageSecurityContext messageSecurityContext = null;
				// MessageSecuritySender	
				if(securityConfig!=null && securityConfig.getFlowParameters()!=null && securityConfig.getFlowParameters().size()>0){
					try{
						MessageSecurityContextParameters contextParameters = new MessageSecurityContextParameters();
						contextParameters.setUseActorDefaultIfNotDefined(this.propertiesReader.isGenerazioneActorDefault(implementazionePdDDestinatario));
						contextParameters.setActorDefault(this.propertiesReader.getActorDefault(implementazionePdDDestinatario));
						contextParameters.setLog(this.log);
						contextParameters.setFunctionAsClient(SecurityConstants.SECURITY_SERVER);
						contextParameters.setPrefixWsuId(this.propertiesReader.getPrefixWsuId());
						contextParameters.setRemoveAllWsuIdRef(this.propertiesReader.isRemoveAllWsuIdRef());
						contextParameters.setIdFruitore(soggettoMittente);
						contextParameters.setIdServizio(idServizio);
						contextParameters.setPddFruitore(registroServiziManager.getIdPortaDominio(soggettoMittente, null));
						contextParameters.setPddErogatore(registroServiziManager.getIdPortaDominio(idServizio.getSoggettoErogatore(), null));
						messageSecurityContext = new MessageSecurityFactory().getMessageSecurityContext(contextParameters);
						messageSecurityContext.setOutgoingProperties(securityConfig.getFlowParameters());
						if(messageSecurityContext.processOutgoing(responseMessage,pddContext.getContext(),
								transactionNullable!=null ? transactionNullable.getTempiElaborazione() : null) == false){
							msgDiag.logErroreGenerico(messageSecurityContext.getMsgErrore(), "Costruzione header MessageSecurity");
							String motivazioneErrore = "Applicazione MessageSecurity non riuscita:"+messageSecurityContext.getMsgErrore();
							ejbUtils.rollbackMessage(motivazioneErrore, esito);
							openspcoopstate.releaseResource();
							esito.setStatoInvocazione(EsitoLib.ERRORE_NON_GESTITO, motivazioneErrore);
							esito.setEsitoInvocazione(false); 
							return esito;
						}
					}catch(Exception e){
						msgDiag.logErroreGenerico(e, "set MessageSecurityContext");
						ejbUtils.rollbackMessage("Applicazione MessageSecurity non riuscita:"+e.getMessage(), esito);
						openspcoopstate.releaseResource();
						esito.setStatoInvocazioneErroreNonGestito(e);
						esito.setEsitoInvocazione(false); 
						return esito;
					}
				}

				
				try{
					IDigestReader digestReader = null;
					if(messageSecurityContext != null) {
						digestReader = messageSecurityContext.getDigestReader(responseMessage!=null ? responseMessage.getFactory() : OpenSPCoop2MessageFactory.getDefaultMessageFactory());
					}
					if(digestReader!=null){
						securityInfo = validazioneSemantica.readSecurityInformation(digestReader, responseMessage);
					}
				}catch(Exception e){
					msgDiag.logErroreGenerico(e,"ErroreLetturaInformazioniSicurezza");
					ejbUtils.rollbackMessage("Lettura informazioni di sicurezza:"+e.getMessage(), esito);
					openspcoopstate.releaseResource();
					esito.setStatoInvocazioneErroreNonGestito(e);
					esito.setEsitoInvocazione(false); 
					return esito;
				}
				
			}




			
			
			
			
			/* ----------- MTOM Processor AfterSecurity ------------ */
			msgDiag.mediumDebug("MTOM Processor [AfterSecurity]...");
			try{
				mtomProcessor.mtomAfterSecurity(responseMessage, flowProperties.tipoMessaggio);
			}catch(Exception e){
				// L'errore viene registrato dentro il metodo mtomProcessor.mtomAfterSecurity
				//msgDiag.logErroreGenerico(e,"MTOMProcessor(AfterSec-"+mtomProcessor.getMTOMProcessorType()+")");
			
				ejbUtils.rollbackMessage("Riscontrato errore durante la gestione MTOM(AfterSec-"+mtomProcessor.getMTOMProcessorType()+"): "+e.getMessage(), esito);
				openspcoopstate.releaseResource();
				esito.setStatoInvocazioneErroreNonGestito(e);
				esito.setEsitoInvocazione(false); 
				return esito;
			}
			
			
			
			
			
			

			/* ------------  Imbustamento ------------- */	
			msgDiag.mediumDebug("Imbustamento (after-security) ...");
			if(inoltroRisposteMsg.isImbustamento()){
				try{
					if(functionAsRouter && 
							!( identitaPdD.getTipo().equals(busta.getTipoMittente()) && identitaPdD.getNome().equals(busta.getMittente()) ) 
					){
						// Aggiungo trasmissione solo se la busta e' stata generata dalla porta di dominio destinataria della richiesta.
						// Se il mittente e' il router, logicamente la busta sara' un errore generato dal router
						msgDiag.highDebug("Tipo Messaggio Risposta prima dell'imbustamento (after-security) ["+responseMessage.getClass().getName()+"]");
						ProtocolMessage protocolMessage = imbustatore.addTrasmissione(responseMessage, tras, readQualifiedAttribute,
								FaseImbustamento.DOPO_SICUREZZA_MESSAGGIO);
						if(protocolMessage!=null && !protocolMessage.isPhaseUnsupported()) {
							headerBusta = protocolMessage.getBustaRawContent();
							responseMessage = protocolMessage.getMessage(); // updated
						}
						msgDiag.highDebug("Tipo Messaggio Risposta dopo l'imbustamento (after-security) ["+responseMessage.getClass().getName()+"]");
					}
					else{
						Integrazione integrazione = new Integrazione();
						integrazione.setStateless(false);
						msgDiag.highDebug("Tipo Messaggio Risposta prima dell'imbustamento (after-security) ["+responseMessage.getClass().getName()+"]");
						ProtocolMessage protocolMessage = null;
						if(RuoloMessaggio.RICHIESTA.equals(ruoloMessaggio)) {
							protocolMessage = imbustatore.imbustamentoRichiesta(responseMessage,pddContext,
									busta,
									integrazione,gestioneManifest,scartaBody,proprietaManifestAttachments,
									FaseImbustamento.PRIMA_SICUREZZA_MESSAGGIO);
						}
						else {
							protocolMessage = imbustatore.imbustamentoRisposta(responseMessage,pddContext,
									busta,null,
									integrazione,gestioneManifest,scartaBody,proprietaManifestAttachments,
									FaseImbustamento.DOPO_SICUREZZA_MESSAGGIO);
						}
						if(protocolMessage!=null && !protocolMessage.isPhaseUnsupported()) {
							headerBusta = protocolMessage.getBustaRawContent();
							responseMessage = protocolMessage.getMessage(); // updated
						}
						msgDiag.highDebug("Tipo Messaggio Risposta dopo l'imbustamento (after-security) ["+responseMessage.getClass().getName()+"]");
					}
				}catch(Exception e){
					if(functionAsRouter)
						msgDiag.logErroreGenerico(e, "imbustatore.after-security.addTrasmissione");
					else
						msgDiag.logErroreGenerico(e, "imbustatore.after-security.imbustamento");
					ejbUtils.rollbackMessage("Imbustamento non riuscito (after-security).", esito);
					openspcoopstate.releaseResource();  
					esito.setStatoInvocazioneErroreNonGestito(e);
					esito.setEsitoInvocazione(false); 
					return esito;
				}
			}
			
			
			
			
			
			
			/* ---------------- OutResponseHandler ----------------------*/
			
			
			OutResponseContext outResponseContext = new OutResponseContext(this.log,protocolFactory,openspcoopstate.getStatoRichiesta());
		
			EsitoTransazione esitoHandler = null;
			if(inoltroSegnalazioneErrore==false){
				
				// Informazioni sul messaggio
				outResponseContext.setMessaggio(responseMessage);
				
				// Contesto
				ProtocolContext protocolContext = new ProtocolContext();
				protocolContext.setIdRichiesta(idMessageRequest);
				protocolContext.setFruitore(soggettoMittente);
				if(busta!=null){
					protocolContext.setIndirizzoFruitore(busta.getIndirizzoMittente());
				}
				protocolContext.setErogatore(soggettoDestinatario);
				if(busta!=null){
					protocolContext.setIndirizzoErogatore(busta.getIndirizzoDestinatario()); 
				}
				if(busta!=null){
					protocolContext.setTipoServizio(busta.getTipoServizio());
					protocolContext.setServizio(busta.getServizio());
					protocolContext.setVersioneServizio(busta.getVersioneServizio());
					protocolContext.setAzione(busta.getAzione());
					protocolContext.setProfiloCollaborazione(busta.getProfiloDiCollaborazione(),busta.getProfiloDiCollaborazioneValue());
					protocolContext.setCollaborazione(busta.getCollaborazione());
					protocolContext.setIdRisposta(busta.getID());
				}
				protocolContext.setDominio(msgDiag.getDominio());
				outResponseContext.setProtocollo(protocolContext);
				
				// Integrazione
				IntegrationContext integrationContext = new IntegrationContext();
				integrationContext.setIdCorrelazioneApplicativa(inoltroRisposteMsg.getIdCorrelazioneApplicativa());
				integrationContext.setServizioApplicativoFruitore(inoltroRisposteMsg.getServizioApplicativoFruitore());
				integrationContext.setGestioneStateless(false);
				outResponseContext.setIntegrazione(integrationContext);
				
				// Altre informazioni
				outResponseContext.setDataElaborazioneMessaggio(DateManager.getDate());
				outResponseContext.setPddContext(pddContext);
				outResponseContext.setTipoPorta(tipoPorta);
				outResponseContext.setIdModulo(this.idModulo);
				
				// Invocazione Handler
				try{
					GestoreHandlers.outResponse(outResponseContext, msgDiag, this.log);
				}catch(Exception e){
					if(e instanceof HandlerException){
						HandlerException he = (HandlerException) e;
						if(he.isEmettiDiagnostico()){
							msgDiag.logErroreGenerico(e, ((HandlerException)e).getIdentitaHandler());
						}
						ejbUtils.rollbackMessage(((HandlerException)e).getIdentitaHandler()+" error: "+e.getMessage(), esito);
					}else{
						msgDiag.logErroreGenerico(e, "OutResponseHandler");
						ejbUtils.rollbackMessage("OutResponseHandler error: "+e.getMessage(), esito);
					}
					openspcoopstate.releaseResource();
					esito.setStatoInvocazioneErroreNonGestito(e);
					esito.setEsitoInvocazione(false); 
					return esito;
				}
			
				// Prendo messaggio rielaborato
				responseMessage = outResponseContext.getMessaggio();
				if(responseMessage!=null){
					esitoHandler = protocolFactory.createEsitoBuilder().getEsito(null,
							200, requestInfo.getProtocolServiceBinding(),
							responseMessage, null, 
							(pddContext!=null ? pddContext.getContext() : null));			
					
				}
			}
			
			
			
			
			
			
			
			





			/* ------------------- 
	   Rilascio Risorse (Le riprendero' dopo aver ottenuto la risposta, se necessario) 
	   Le informazioni nel DB sono state utilizzate fino a questo punto solo in lettura.
	   Eventuali spedizioni JMS sono state effettuate e le risorse gia' rilasciate (non arrivero a questo punto)
	   -----------------------*/
			msgDiag.mediumDebug("Rilascio connessione al database...");	
			openspcoopstate.releaseResource();


			
			
			
			
			
			


			/* ------------------- Spedizione Messaggio Soap -----------------------*/
			msgDiag.mediumDebug("Impostazione messaggio del connettore...");

			//	Connettore per consegna
			String tipoConnector = connettore.getTipo();
			msgDiag.addKeyword(CostantiPdD.KEY_TIPO_CONNETTORE, tipoConnector);
			org.openspcoop2.core.config.Property [] cps = null;
			if(connettore.getPropertyList().size()>0){
				cps = connettore.getPropertyList().toArray(new org.openspcoop2.core.config.Property[connettore.getPropertyList().size()]);
			}
			ConnettoreMsg connettoreMsg = new ConnettoreMsg(tipoConnector,responseMessage,cps);
			connettoreMsg.setBusta(busta);
			connettoreMsg.setIdModulo(InoltroRisposte.ID_MODULO);
			connettoreMsg.setProtocolFactory(protocolFactory);
			connettoreMsg.setMsgDiagnostico(msgDiag);
			connettoreMsg.setState(openspcoopstate.getStatoRichiesta());
			connettoreMsg.initPolicyGestioneToken(configurazionePdDManager);
			
			// Risposte del connettore
			int codiceRitornato = -1;
			OpenSPCoop2Message responseHttpReply = null;

			// Stato consegna tramite connettore
			boolean errorConsegna = false;
			boolean riconsegna = false;
			java.sql.Timestamp dataRiconsegna = null;
			String motivoErroreConsegna = null;
			boolean invokerNonSupportato = false;
			SOAPFault soapFaultConnectionReply = null;
			ProblemRFC7807 restProblemConnectionReply = null;
			OpenSPCoop2MessageFactory faultConnectionReplyMessageFactory = null;
			Exception eccezioneProcessamentoConnettore = null;

			// Carico connettore richiesto
			String connectorClass = null;
			@SuppressWarnings("unused")
			Exception eInvokerNonSupportato = null;
			if(invokerNonSupportato==false){
				try{
					connectorSender = (IConnettore) this.pluginLoader.newConnettore(tipoConnector);
				}
				catch(Exception e){
					msgDiag.logErroreGenerico(e,"Inizializzazione Connettore"); // l'errore contiene gia tutte le informazioni
					invokerNonSupportato = true;
					eInvokerNonSupportato = e;
				}
				if(connectorSender!=null) {
					try {
						connectorClass = connectorSender.getClass().getName();
						AbstractCore.init(connectorSender, pddContext, protocolFactory);
					}catch(Exception e){
						msgDiag.logErroreGenerico(e,"IConnettore.newInstance(tipo:"+tipoConnector+" class:"+connectorClass+")");
						invokerNonSupportato = true;
						eInvokerNonSupportato = e;
					}
				}
				if( (invokerNonSupportato == false) && (connectorSender == null)){
					msgDiag.logErroreGenerico("ConnectorSender is null","IConnettore.newInstance(tipo:"+tipoConnector+" class:"+connectorClass+")");
					invokerNonSupportato = true;
				}
			}

			// Imposto tipo di richiesta
			HttpRequestMethod httpRequestMethod = null;
			if(connectorSender!=null){
				try{
					if(connectorSender instanceof ConnettoreBaseHTTP){
						ConnettoreBaseHTTP baseHttp = (ConnettoreBaseHTTP) connectorSender;
						baseHttp.setHttpMethod(responseMessage);
						
						if(ServiceBinding.REST.equals(responseMessage.getServiceBinding())){
							httpRequestMethod = baseHttp.getHttpMethod();
						}
					}
				}catch(Exception e){
					msgDiag.logErroreGenerico(e,"ConnettoreBaseHTTP.setHttpMethod(tipo:"+tipoConnector+" class:"+connectorClass+")");
					invokerNonSupportato = true;
					eInvokerNonSupportato = e;
				}
			}
			
			// Location
			location = ConnettoreUtils.getAndReplaceLocationWithBustaValues(connectorSender, connettoreMsg, busta, pddContext, protocolFactory, this.log);
			if(location!=null){
				String locationWithUrl = ConnettoreUtils.buildLocationWithURLBasedParameter(responseMessage, connettoreMsg.getTipoConnettore(), connettoreMsg.getPropertiesUrlBased(), location,
						protocolFactory, this.idModulo);
				locationWithUrl = ConnettoreUtils.addProxyInfoToLocationForHTTPConnector(connettoreMsg.getTipoConnettore(), connettoreMsg.getConnectorProperties(), locationWithUrl);
				msgDiag.addKeyword(CostantiPdD.KEY_LOCATION, ConnettoreUtils.formatLocation(httpRequestMethod, locationWithUrl));
				
				pddContext.addObject(CostantiPdD.CONNETTORE_REQUEST_METHOD, httpRequestMethod);
				pddContext.addObject(CostantiPdD.CONNETTORE_REQUEST_URL, locationWithUrl);
			}
			else{
				msgDiag.addKeyword(CostantiPdD.KEY_LOCATION, "N.D.");
			}

			// timeout di default
			if(connettoreMsg.getConnectorProperties()==null){
				java.util.Map<String,String> propCon = new java.util.HashMap<String,String>();
				connettoreMsg.setConnectorProperties(propCon);
			}
			if(connettoreMsg.getConnectorProperties().get(CostantiConnettori.CONNETTORE_CONNECTION_TIMEOUT)==null){
				connettoreMsg.getConnectorProperties().put(CostantiConnettori.CONNETTORE_CONNECTION_TIMEOUT,""+this.propertiesReader.getConnectionTimeout_inoltroBuste());
			}
			if(connettoreMsg.getConnectorProperties().get(CostantiConnettori.CONNETTORE_READ_CONNECTION_TIMEOUT)==null){
				connettoreMsg.getConnectorProperties().put(CostantiConnettori.CONNETTORE_READ_CONNECTION_TIMEOUT,""+this.propertiesReader.getReadConnectionTimeout_inoltroBuste());
			}
			
			// User-Agent e X-* header
			UtilitiesIntegrazione httpUtilities = UtilitiesIntegrazione.getInstancePAResponse(this.log);
			if(connettoreMsg.getPropertiesTrasporto()==null){
				Map<String, String>  trasporto = new HashMap<String, String> ();
				connettoreMsg.setPropertiesTrasporto(trasporto);
			}
			httpUtilities.setInfoProductTransportProperties(connettoreMsg.getPropertiesTrasporto());


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
			
			//	Utilizzo Connettore
			if(invokerNonSupportato==false){
				msgDiag.logPersonalizzato("inoltroInCorso");
				// utilizzo connettore
				errorConsegna = !connectorSender.send(null, connettoreMsg);
				motivoErroreConsegna = connectorSender.getErrore();
				eccezioneProcessamentoConnettore = connectorSender.getEccezioneProcessamento();
				if(errorConsegna && motivoErroreConsegna==null){
					motivoErroreConsegna = "Errore durante la consegna";
				}
				//	interpretazione esito consegna
				GestioneErrore gestioneConsegnaConnettore = configurazionePdDManager.getGestioneErroreConnettoreComponenteCooperazione(protocolFactory, responseMessage.getServiceBinding());
				GestoreErroreConnettore  gestoreErrore = new GestoreErroreConnettore();
				errorConsegna = !gestoreErrore.verificaConsegna(gestioneConsegnaConnettore,motivoErroreConsegna,eccezioneProcessamentoConnettore,connectorSender.getCodiceTrasporto(),connectorSender.getResponse());
				if(errorConsegna){
					motivoErroreConsegna = gestoreErrore.getErrore();
					riconsegna = gestoreErrore.isRiconsegna();
					dataRiconsegna = gestoreErrore.getDataRispedizione();
				}
				// raccolta risultati del connettore
				soapFaultConnectionReply = gestoreErrore.getFault();
				restProblemConnectionReply = gestoreErrore.getProblem();
				faultConnectionReplyMessageFactory = connectorSender.getResponse()!=null ? connectorSender.getResponse().getFactory() : OpenSPCoop2MessageFactory.getDefaultMessageFactory();
				codiceRitornato = connectorSender.getCodiceTrasporto();
				responseHttpReply = connectorSender.getResponse();
				Map<String, String>  headerTrasportoReply = connectorSender.getHeaderTrasporto();
				// gestione connessione connettore
				// Sono nella casistica di messaggio preso in carico.
				// Non si deve chiudere immediatamente la connessione, poiche' nel resto del modulo, il messaggio puo' ancora essere utilizzato (es. dump)
				/*
				try{
					connectorSender.disconnect();
				}catch(Exception e){
					msgDiag.logDisconnectError(e, location);
				}
				*/

				msgDiag.addKeyword(CostantiPdD.KEY_CODICE_CONSEGNA, codiceRitornato+"");
				if(motivoErroreConsegna!=null)
					msgDiag.addKeyword(CostantiPdD.KEY_ERRORE_CONSEGNA, motivoErroreConsegna);
				
				// Il Connettore potrebbe aggiungere informazioni alla location.
				String tmpLocation = connectorSender.getLocation();
				if(tmpLocation!=null){
					
					// salvo la request url originale, se la risposta non è letta dalla cache
					boolean responseCached = false;
					if(pddContext.containsKey(ConnettoreBase.RESPONSE_FROM_CACHE)) {
						responseCached = (Boolean) pddContext.getObject(ConnettoreBase.RESPONSE_FROM_CACHE);
					}
					if(!responseCached) {
						pddContext.addObject(CostantiPdD.CONNETTORE_REQUEST_METHOD, httpRequestMethod);
						pddContext.addObject(CostantiPdD.CONNETTORE_REQUEST_URL, tmpLocation);
					}
					
					// aggiorno
					location = tmpLocation;
					if(responseCached) {
						msgDiag.addKeyword(CostantiPdD.KEY_LOCATION, location);
					}
					else {
						msgDiag.addKeyword(CostantiPdD.KEY_LOCATION, ConnettoreUtils.formatLocation(httpRequestMethod, location));
					}
				}
				
				//	dump applicativo
				if(responseHttpReply!=null ){
					DumpConfigurazione dumpConfig = configurazionePdDManager.getDumpConfigurazione(pa);
					Dump dumpApplicativo = new Dump(identitaPdD,InoltroRisposte.ID_MODULO,idMessageRequest,
							soggettoMittente,idServizio,tipoPorta,msgDiag.getPorta(),pddContext,
							openspcoopstate.getStatoRichiesta(),openspcoopstate.getStatoRisposta(),
							dumpConfig);
					dumpApplicativo.dumpRispostaIngresso(responseHttpReply, infoConnettoreUscita, headerTrasportoReply);
				}
			}

			
			
			
			
			
			
			
			
			/* ------------  Re-ottengo Connessione al DB -------------- */
			msgDiag.mediumDebug("Richiesta connessione al database per la gestione della risposta...");
			openspcoopstate.updateResource(idTransazione);







			/* ------------  Tracciamento Richiesta e Messaggio Diagnostico ------------- */
			IValidatoreErrori validatoreErrori = protocolFactory.createValidatoreErrori(openspcoopstate.getStatoRichiesta());
			if(invokerNonSupportato==false){ //&& errorConsegna==false){

				// Tracciamento effettuato sempre
				msgDiag.mediumDebug("Tracciamento della busta...");
				EsitoElaborazioneMessaggioTracciato esitoTraccia = EsitoElaborazioneMessaggioTracciato.getEsitoElaborazioneMessaggioInviato();
				if(inoltroSegnalazioneErrore){
					tracciamento.registraRichiesta(responseMessage,securityInfo,headerBusta,busta,esitoTraccia,
							Tracciamento.createLocationString(false, location),
							inoltroRisposteMsg.getIdCorrelazioneApplicativa());
				}
				else{
					tracciamento.registraRisposta(responseMessage,securityInfo,headerBusta,busta,esitoTraccia,
							Tracciamento.createLocationString(false, location),
							inoltroRisposteMsg.getIdCorrelazioneApplicativa(),inoltroRisposteMsg.getIdCorrelazioneApplicativaRisposta());
				}

				if(errorConsegna){
					msgDiag.logPersonalizzato("inoltroConErrore");
				}else{
					String tipoMsg = "Messaggio Protocollo";
					ProprietaValidazioneErrori pValidazioneErrori = new ProprietaValidazioneErrori();
					pValidazioneErrori.setIgnoraEccezioniNonGravi(protocolManager.isIgnoraEccezioniNonGravi());
					pValidazioneErrori.setVersioneProtocollo(versioneProtocollo);
					if(validatoreErrori.isBustaErrore(busta,responseMessage,pValidazioneErrori))
						tipoMsg = tipoMsg + " Errore";

					if(configurazionePdDManager.isUtilizzoIndirizzoTelematico() && busta.getIndirizzoDestinatario()!=null){
						msgDiag.addKeyword(CostantiPdD.KEY_LOCATION, ("indirizzoTelematico["+busta.getIndirizzoDestinatario()+"]"));
						msgDiag.logPersonalizzato("inoltroEffettuato");
					}else{
						msgDiag.logPersonalizzato("inoltroEffettuato");
					}
				}
			}









		




			/* ------------- Analisi di una risposta ritornata ------------*/
			boolean presenzaRispostaProtocolConnectionReply = false;
			
			if(responseHttpReply != null ){
				msgDiag.mediumDebug("Analisi della risposta (validazione sintattica)...");
				// ValidazioneSintattica: serve a leggere il messaggio
				ProprietaValidazione property = new ProprietaValidazione();
				property.setValidazioneConSchema(configurazionePdDManager.isLivelloValidazioneRigido(implementazionePdDDestinatario));
				property.setValidazioneProfiloCollaborazione(configurazionePdDManager.isValidazioneProfiloCollaborazione(implementazionePdDDestinatario));
				property.setValidazioneManifestAttachments(configurazionePdDManager.isValidazioneManifestAttachments(implementazionePdDDestinatario));
				validatoreProtocolConnectionReply = new Validatore(responseHttpReply,pddContext,property, 
						openspcoopstate.getStatoRisposta(),readQualifiedAttribute, protocolFactory);
				presenzaRispostaProtocolConnectionReply  = validatoreProtocolConnectionReply.validazioneSintattica();
				if(presenzaRispostaProtocolConnectionReply){
					// Gestisco i log come fossero una risposta, come se fossi nel modulo InoltroBuste
					msgDiag.addKeywords(validatoreProtocolConnectionReply.getBusta(), false);
				}
				else{
					if(validatoreProtocolConnectionReply.getErrore()!=null){
						this.log.debug("Messaggio non riconosciuto come busta ("+traduttore.toString(validatoreProtocolConnectionReply.getErrore().getCodiceErrore())
								+"): "+validatoreProtocolConnectionReply.getErrore().getDescrizione(protocolFactory));
					}
				}
			}













			/* ------------------------- Gestione Errori Consegna ---------------------------- */	    
			msgDiag.mediumDebug("Gestione errore consegna della risposta...");
			if(invokerNonSupportato){
				String motivazioneErrore = "Connettore non supportato [tipo:"+tipoConnector+" class:"+connectorClass+"]";
				ejbUtils.rollbackMessage(motivazioneErrore, esito);
				openspcoopstate.releaseResource();
				esito.setStatoInvocazione(EsitoLib.ERRORE_NON_GESTITO, motivazioneErrore);
				esito.setEsitoInvocazione(false); 
				return esito;
			}
			else if(errorConsegna && presenzaRispostaProtocolConnectionReply==false){
				//	Effettuo log dell'eventuale fault
				if(soapFaultConnectionReply!=null){
					msgDiag.addKeyword(CostantiPdD.KEY_SOAP_FAULT, SoapUtils.toString(faultConnectionReplyMessageFactory, soapFaultConnectionReply));
					msgDiag.logPersonalizzato("ricezioneSoapFault");
				}
				else if(restProblemConnectionReply!=null){
					msgDiag.addKeyword(CostantiPdD.KEY_REST_PROBLEM, restProblemConnectionReply.getRaw());
					msgDiag.logPersonalizzato("ricezioneRestProblem");
				}
				String motivazioneErrore = "Errore duranta la spedizione della busta: "+motivoErroreConsegna;
				if(riconsegna){
					ejbUtils.rollbackMessage(motivazioneErrore,dataRiconsegna, esito);
					esito.setEsitoInvocazione(false);
				}else{
					if(inoltroSegnalazioneErrore)
						ejbUtils.releaseOutboxMessage(false); // TODO CHECKME
					else
						ejbUtils.releaseInboxMessage(false);
					esito.setEsitoInvocazione(true);
				}
				openspcoopstate.releaseResource();
				esito.setStatoInvocazione(EsitoLib.ERRORE_NON_GESTITO, motivazioneErrore);
				return esito;
			}




			/* ---------------- Gestione Risposta  (operazioni comuni per tutti i profili) ------------------- */ 
			boolean isMessaggioErroreProtocolloConnectionReply = false;
			Busta bustaConnectionReply = null;

			if(presenzaRispostaProtocolConnectionReply){
				// Gestione Specifica per Buste
				bustaConnectionReply = validatoreProtocolConnectionReply.getBusta();
				isMessaggioErroreProtocolloConnectionReply = validatoreProtocolConnectionReply.isErroreProtocollo();

				// Registrazione Msg
				if(isMessaggioErroreProtocolloConnectionReply){
					msgDiag.logPersonalizzato("ricezioneMessaggioErrore");
				}else{
					msgDiag.logPersonalizzato("ricezioneMessaggio");
				}

				// Estrazione header busta
				msgDiag.mediumDebug("Sbustamento della risposta...");
				BustaRawContent<?> headerProtocolloRispostaConnectionReply = null;
				try{
					boolean gestioneManifestRispostaHttp = false;
					if(functionAsRouter==false)
						gestioneManifestRispostaHttp = configurazionePdDManager.isGestioneManifestAttachments();
					org.openspcoop2.protocol.engine.builder.Sbustamento sbustatore = 
							new org.openspcoop2.protocol.engine.builder.Sbustamento(protocolFactory,openspcoopstate.getStatoRichiesta());
					ProtocolMessage protocolMessage = sbustatore.sbustamento(responseHttpReply,pddContext,
							busta,
							RuoloMessaggio.RISPOSTA,gestioneManifestRispostaHttp,proprietaManifestAttachments,
							FaseSbustamento.POST_CONSEGNA_RISPOSTA_NEW_CONNECTION, requestInfo);
					if(protocolMessage!=null) {
						headerProtocolloRispostaConnectionReply = protocolMessage.getBustaRawContent();
						responseHttpReply = protocolMessage.getMessage(); // updated
					}
				}catch(Exception e){
					EsitoElaborazioneMessaggioTracciato esitoTraccia = EsitoElaborazioneMessaggioTracciato.getEsitoElaborazioneConErrore("Sbustamento busta nella connection Reply non riuscita: "+e.getMessage());
					tracciamento.registraRisposta(responseHttpReply,null,
							validatoreProtocolConnectionReply.getHeaderProtocollo(),bustaConnectionReply,esitoTraccia,
							Tracciamento.createLocationString(true, location),
							inoltroRisposteMsg.getIdCorrelazioneApplicativa(),inoltroRisposteMsg.getIdCorrelazioneApplicativaRisposta()); // non ancora registrata
					msgDiag.logErroreGenerico(e,"sbustatore.sbustamento("+bustaConnectionReply.getID()+")");
					ejbUtils.rollbackMessage("Sbustamento busta nella connection Reply non riuscita.", esito);
					openspcoopstate.releaseResource();
					esito.setStatoInvocazioneErroreNonGestito(e);
					esito.setEsitoInvocazione(false); 
					return esito;
				}

				// Tracciamento Risposta
				msgDiag.mediumDebug("Tracciamento della risposta...");
				EsitoElaborazioneMessaggioTracciato esitoTraccia = EsitoElaborazioneMessaggioTracciato.getEsitoElaborazioneMessaggioRicevuto();
				tracciamento.registraRisposta(responseHttpReply,null,
						headerProtocolloRispostaConnectionReply,bustaConnectionReply,esitoTraccia,
						Tracciamento.createLocationString(true, location),
						inoltroRisposteMsg.getIdCorrelazioneApplicativa(),inoltroRisposteMsg.getIdCorrelazioneApplicativaRisposta());

				// Salvo il messaggio di risposta e re-inoltro l'errore
				if(functionAsRouter){	
					msgDiag.mediumDebug("Registrazione messaggio nel Repository Messaggi/Buste...");
					msgConnectionReply = new GestoreMessaggi(openspcoopstate, false, bustaConnectionReply.getID(),Costanti.OUTBOX,msgDiag, pddContext);
					msgConnectionReply.setOneWayVersione11(oneWayVersione11);
					try{
						repositoryConnectionReply = new RepositoryBuste(openspcoopstate.getStatoRisposta(), false,protocolFactory);
						
						if( msgConnectionReply.existsMessage_noCache() ){
							// Se il proprietario attuale e' GestoreMessaggi, forzo l'eliminazione e continuo a processare il messaggio.
							String proprietarioMessaggio = msgConnectionReply.getProprietario(InoltroRisposte.ID_MODULO);
							if(TimerGestoreMessaggi.ID_MODULO.equals(proprietarioMessaggio)){
								msgDiag.addKeyword(CostantiPdD.KEY_ID_MESSAGGIO_RISPOSTA, bustaConnectionReply.getID());
								msgDiag.logPersonalizzato("(http reply)"+msgDiag.getMessaggio("ricezioneSoapMessage.msgGiaPresente"), 
										msgDiag.getLivello("ricezioneSoapMessage.msgGiaPresente"),
										msgDiag.getCodice("ricezioneSoapMessage.msgGiaPresente"));
								String msg = "(http reply)" + msgDiag.getMessaggio_replaceKeywords("ricezioneSoapMessage.msgGiaPresente");
								if(this.propertiesReader.isMsgGiaInProcessamento_useLock()) {
									msgConnectionReply._deleteMessageWithLock(msg,this.propertiesReader.getMsgGiaInProcessamento_AttesaAttiva(),
											this.propertiesReader.getMsgGiaInProcessamento_CheckInterval());
								}
								else {
									msgConnectionReply.deleteMessageByNow();
								}
							}else{
								throw new Exception("Altra copia della Busta ricevuta come risposta nella http reply con id["+bustaConnectionReply.getID()+"] in elaborazione dal modulo "+proprietarioMessaggio);
							}
						}

						msgConnectionReply.registraMessaggio(responseHttpReply,inoltroRisposteMsg.getIdCorrelazioneApplicativa(),
								inoltroRisposteMsg.getIdCorrelazioneApplicativaRisposta());
						msgConnectionReply.aggiornaProprietarioMessaggio(InoltroRisposte.ID_MODULO);
						msgConnectionReply.aggiornaRiferimentoMessaggio(busta.getID());	

						repositoryConnectionReply.registraBustaIntoOutBox(busta, this.propertiesReader.getRepositoryIntervalloScadenzaMessaggi());
						Integrazione infoIntegrazione = new Integrazione();
						repositoryConnectionReply.aggiornaInfoIntegrazioneIntoOutBox(bustaConnectionReply.getID(),infoIntegrazione);

					}catch(Exception e){
						msgDiag.logErroreGenerico(e,"RegistrazioneRisposta("+bustaConnectionReply.getID()+")");
						ejbUtils.rollbackMessage("Salvataggio messaggio nella connection Reply non riuscita.", esito);
						openspcoopstate.releaseResource();
						esito.setStatoInvocazioneErroreNonGestito(e);
						esito.setEsitoInvocazione(false); 
						return esito;
					}
					msgDiag.mediumDebug("Ridirezione messaggio a InoltroRisposte...");
					try{
						ejbUtils.sendToInoltroRisposte(bustaConnectionReply,true,null,msgConnectionReply,null,null,null,true);
					}catch(Exception e){
						msgDiag.logErroreGenerico(e,"GenericLib.nodeSender.send(InoltroRisposte ID:"+bustaConnectionReply.getID()+")");
						ejbUtils.rollbackMessage("Inoltro messaggio ricevuto nella connection Reply non riuscito.", esito);
						openspcoopstate.releaseResource();
						esito.setStatoInvocazioneErroreNonGestito(e);
						esito.setEsitoInvocazione(false); 
						return esito;
					}

				}				
			}
			else if(responseHttpReply != null ){
				// potenziale Fault (inserito dopo il codice soprastante, per fare decriptare il body al MessageSecurity se presente)
				if(soapFaultConnectionReply!=null){
					msgDiag.addKeyword(CostantiPdD.KEY_SOAP_FAULT, SoapUtils.toString(faultConnectionReplyMessageFactory, soapFaultConnectionReply));
					msgDiag.logPersonalizzato("ricezioneSoapFault");
				}
				else if(restProblemConnectionReply!=null){
					msgDiag.addKeyword(CostantiPdD.KEY_REST_PROBLEM, restProblemConnectionReply.getRaw());
					msgDiag.logPersonalizzato("ricezioneRestProblem");
				}
			}


























			/* ---------- Gestione Transazione Modulo ---------------- */

			// Aggiorno proprietario
			msgDiag.mediumDebug("Aggiorno proprietario messaggio ...");
			msgResponse.aggiornaProprietarioMessaggio(TimerGestoreMessaggi.ID_MODULO);

			/* --------	Elimino accesso daPdD --------- */
			msgDiag.mediumDebug("Elimino utilizzo busta dalla PdD ...");
			repositoryBuste = new RepositoryBuste(openspcoopstate.getStatoRisposta(), false, protocolFactory);
			if(oneWayVersione11)
				repositoryBuste.eliminaBustaStatelessFromOutBox(busta.getID());
			else
				repositoryBuste.eliminaUtilizzoPdDFromOutBox(busta.getID());

			// messaggio finale
			msgDiag.addKeyword(CostantiPdD.KEY_TIPO_CONNETTORE, tipoConnector);
			msgDiag.logPersonalizzato("gestioneConsegnaTerminata");
			
			// Commit JDBC
			msgDiag.mediumDebug("Commit delle operazioni per la gestione della busta...");
			openspcoopstate.commit();

			// Aggiornamento cache Messaggio
			if(msgResponse!=null)
				msgResponse.addMessaggiIntoCache_readFromTable(InoltroRisposte.ID_MODULO, "busta da inoltrare");
			if(msgConnectionReply!=null)
				msgConnectionReply.addMessaggiIntoCache_readFromTable(InoltroRisposte.ID_MODULO, "connection http reply");

			//	Aggiornamento cache proprietario messaggio
			if(msgResponse!=null)
				msgResponse.addProprietariIntoCache_readFromTable(InoltroRisposte.ID_MODULO, "busta da inoltrare",idMessageRequest,functionAsRouter);
			if(msgConnectionReply!=null)
				msgConnectionReply.addProprietariIntoCache_readFromTable(InoltroRisposte.ID_MODULO, "connection http reply",idMessageResponse,functionAsRouter);

			// Rilascio connessione al DB
			msgDiag.mediumDebug("Rilascio connessione al database...");
			openspcoopstate.releaseResource(); // Rilascio Connessione DB

			
			
			
			
			
			
			
			/* --------- PostOutResponse --------------- */
			// Viene fatta qua poiche' ci interessa segnalare la fine della spedizione
			PostOutResponseContext postOutResponseContext = new PostOutResponseContext(this.log,protocolFactory);
			postOutResponseContext.setDataElaborazioneMessaggio(DateManager.getDate());
			if(responseMessage!=null){
				postOutResponseContext.setInputResponseMessageSize(responseMessage.getIncomingMessageContentLength());
				postOutResponseContext.setOutputResponseMessageSize(responseMessage.getOutgoingMessageContentLength());
			}
			postOutResponseContext.setProtocollo(outResponseContext.getProtocollo());
			postOutResponseContext.setEsito(esitoHandler);
			postOutResponseContext.setPddContext(pddContext);
			postOutResponseContext.setIntegrazione(outResponseContext.getIntegrazione());
			postOutResponseContext.setMessaggio(responseMessage);
			postOutResponseContext.setTipoPorta(outResponseContext.getTipoPorta());
			// Invoco handler
			GestoreHandlers.postOutResponse(postOutResponseContext, msgDiag, this.log);
			
			
			
			

			msgDiag.mediumDebug("Lavoro Terminato.");
			esito.setEsitoInvocazione(true);	
			esito.setStatoInvocazione(EsitoLib.OK, null);
			return esito; 

		}catch(Throwable e){
			this.log.error("ErroreGenerale",e);
			msgDiag.logErroreGenerico(e, "Generale");

			ejbUtils.rollbackMessage("ErroreGenerale:"+e.getMessage(), esito);
			esito.setStatoInvocazioneErroreNonGestito(e);
			esito.setEsitoInvocazione(false); 
			
			openspcoopstate.releaseResource();
			
			return esito;
		}
		finally{
			try{
				if(connectorSender!=null)
					connectorSender.disconnect();
			}catch(Exception e){
				try{
					if(msgDiag!=null)
						msgDiag.logDisconnectError(e, location);
				}catch(Exception eDisconnect){
					this.log.error("Errore durante la chiusura delle connessione: "+eDisconnect.getMessage(),e);
				}
			}
			// *** GB ***
			if(validatoreProtocolConnectionReply!=null){
				if(validatoreProtocolConnectionReply.getValidatoreSintattico()!=null){
					validatoreProtocolConnectionReply.getValidatoreSintattico().setHeaderSOAP(null);
				}
				validatoreProtocolConnectionReply.setValidatoreSintattico(null);
			}
			validatoreProtocolConnectionReply = null;
			// *** GB ***
		}

	}



	/**
	 * Ritorna le Proprieta' Message-Security relative alla spedizione della busta
	 * 
	 * @return Proprieta' Message-Security relative alla spedizione della busta
	 */
	private FlowProperties  getFlowProperties(Busta bustaRisposta,
			ConfigurazionePdDManager configurazionePdDManager, IState state,
			MsgDiagnostico msgDiag, 
			org.openspcoop2.protocol.sdk.IProtocolFactory<?> protocolFactory,
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
		flowProperties.tipoMessaggio = RuoloMessaggio.RISPOSTA;
		ProfiloDiCollaborazione profiloCollaborazione = null;

		try{

			// Messaggi AD HOC senza profilo: RISCONTRO
			if(bustaRisposta.getProfiloDiCollaborazione()==null&& bustaRisposta.sizeListaRiscontri()>0){
				if(bustaRisposta.getTipoServizioRichiedenteBustaDiServizio()!=null &&
						bustaRisposta.getServizioRichiedenteBustaDiServizio()!=null){
					PortaApplicativa pa = paFind;
					if(pa==null){
						IDServizio idServizioPA = IDServizioFactory.getInstance().getIDServizioFromValues(bustaRisposta.getTipoServizioRichiedenteBustaDiServizio(),bustaRisposta.getServizioRichiedenteBustaDiServizio(), 
								bustaRisposta.getTipoMittente(),bustaRisposta.getMittente(), 
								bustaRisposta.getVersioneServizioRichiedenteBustaDiServizio());
						idServizioPA.setAzione(bustaRisposta.getAzioneRichiedenteBustaDiServizio());
						pa = getPortaApplicativa(configurazionePdDManager, 
								idServizioPA);
					}
					flowProperties.messageSecurity = configurazionePdDManager.getPA_MessageSecurityForSender(pa);
					flowProperties.mtom = configurazionePdDManager.getPA_MTOMProcessorForSender(pa);
				}
			}
			// Messaggi con profilo OneWay e Sincrono
			else if(org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.ONEWAY.equals(bustaRisposta.getProfiloDiCollaborazione()) || 
					org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.SINCRONO.equals(bustaRisposta.getProfiloDiCollaborazione())
			) {	
				PortaApplicativa pa = paFind;
				if(pa==null){
					IDServizio idServizioPA = IDServizioFactory.getInstance().getIDServizioFromValues(bustaRisposta.getTipoServizio(),bustaRisposta.getServizio(), 
							bustaRisposta.getTipoMittente(),bustaRisposta.getMittente(), 
							bustaRisposta.getVersioneServizio());
					idServizioPA.setAzione(bustaRisposta.getAzione());
					pa = getPortaApplicativa(configurazionePdDManager, idServizioPA);
				}
				flowProperties.messageSecurity = configurazionePdDManager.getPA_MessageSecurityForSender(pa);
				flowProperties.mtom = configurazionePdDManager.getPA_MTOMProcessorForSender(pa);
			}

			// Profilo Asincrono Simmetrico
			else if(org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.ASINCRONO_SIMMETRICO.equals(bustaRisposta.getProfiloDiCollaborazione())) {	

				profiloCollaborazione = new ProfiloDiCollaborazione(state,protocolFactory);

				//	Ricevuta alla richiesta.
				if(profiloCollaborazione.asincrono_isRicevutaRichiesta(bustaRisposta.getRiferimentoMessaggio())){

					PortaApplicativa pa = paFind;
					if(pa==null){
						IDServizio idServizioPA = IDServizioFactory.getInstance().getIDServizioFromValues(bustaRisposta.getTipoServizio(),bustaRisposta.getServizio(), 
								bustaRisposta.getTipoMittente(),bustaRisposta.getMittente(), 
								bustaRisposta.getVersioneServizio());
						idServizioPA.setAzione(bustaRisposta.getAzione());
						pa = getPortaApplicativa(configurazionePdDManager, idServizioPA);
					}
					flowProperties.messageSecurity = configurazionePdDManager.getPA_MessageSecurityForSender(pa);
					flowProperties.mtom = configurazionePdDManager.getPA_MTOMProcessorForSender(pa);

				}

				//	Ricevuta alla risposta.
				else if(profiloCollaborazione.asincrono_isRicevutaRisposta(bustaRisposta.getRiferimentoMessaggio())){

					RepositoryBuste repository = new RepositoryBuste(state, false,protocolFactory);
					Integrazione integrazione = repository.getInfoIntegrazioneFromOutBox(bustaRisposta.getRiferimentoMsgBustaRichiedenteServizio());
					IDPortaDelegata idPD = new IDPortaDelegata();
					idPD.setNome(integrazione.getNomePorta());
					PortaDelegata pd = configurazionePdDManager.getPortaDelegata_SafeMethod(idPD);
					flowProperties.messageSecurity = configurazionePdDManager.getPD_MessageSecurityForSender(pd);
					flowProperties.mtom = configurazionePdDManager.getPD_MTOMProcessorForSender(pd);

				}

			}

			//	Profilo Asincrono Asimmetrico
			else if(org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.ASINCRONO_ASIMMETRICO.equals(bustaRisposta.getProfiloDiCollaborazione())) {	

				profiloCollaborazione = new ProfiloDiCollaborazione(state,protocolFactory);

				//	Ricevuta alla richiesta.
				if(profiloCollaborazione.asincrono_isRicevutaRichiesta(bustaRisposta.getRiferimentoMessaggio())){

					PortaApplicativa pa = paFind;
					if(pa==null){
						IDServizio idServizioPA = IDServizioFactory.getInstance().getIDServizioFromValues(bustaRisposta.getTipoServizio(),bustaRisposta.getServizio(), 
								bustaRisposta.getTipoMittente(),bustaRisposta.getMittente(), 
								bustaRisposta.getVersioneServizio());
						idServizioPA.setAzione(bustaRisposta.getAzione());
						pa = getPortaApplicativa(configurazionePdDManager, idServizioPA);
					}
					flowProperties.messageSecurity = configurazionePdDManager.getPA_MessageSecurityForSender(pa);
					flowProperties.mtom = configurazionePdDManager.getPA_MTOMProcessorForSender(pa);

				}

				//	Ricevuta alla risposta.
				else if(profiloCollaborazione.asincrono_isRicevutaRisposta(bustaRisposta.getRiferimentoMessaggio())){

					PortaApplicativa pa = paFind;
					if(pa==null){
						// ConversioneServizio.
						IDServizio idServizioOriginale = profiloCollaborazione.asincronoAsimmetrico_getDatiConsegnaRisposta(bustaRisposta.getRiferimentoMsgBustaRichiedenteServizio());
						IDServizio idServizioPA = IDServizioFactory.getInstance().getIDServizioFromValues(idServizioOriginale.getTipo(),idServizioOriginale.getNome(), 
								bustaRisposta.getTipoMittente(),bustaRisposta.getMittente(), 
								idServizioOriginale.getVersione());
						idServizioPA.setAzione(idServizioOriginale.getAzione());
						
						pa = getPortaApplicativa(configurazionePdDManager, idServizioPA);
					}
					flowProperties.messageSecurity = configurazionePdDManager.getPA_MessageSecurityForSender(pa);
					flowProperties.mtom = configurazionePdDManager.getPA_MTOMProcessorForSender(pa);

				}

			}

		}catch(Exception e){
			msgDiag.logErroreGenerico(e,"LetturaDatiMessageSecurity");
			this.log.error("Lettura dati MessageSecurity per la spedizione del messaggio di risposta non riuscita",e);
		}finally{
			if(profiloCollaborazione!=null)
				if (state instanceof StatefulMessage )
					((StatefulMessage)state).closePreparedStatement();
				//else ; //TODO CHECKME
		}
		
		return flowProperties;

	}
	
	private PortaApplicativa getPortaApplicativa(ConfigurazionePdDManager configurazionePdDReader, IDServizio idServizio) throws Exception{
		List<PortaApplicativa> listPa = configurazionePdDReader.getPorteApplicative(idServizio, false);
		if(listPa.size()<=0){
			throw new Exception("Non esiste alcuna porta applicativa indirizzabile tramite il servizio ["+idServizio+"]");
		}
		else{
			if(listPa.size()>1)
				throw new Exception("Esiste più di una porta applicativa indirizzabile tramite il servizio ["+idServizio+"]");
			return listPa.get(0);
		}
	}
}
