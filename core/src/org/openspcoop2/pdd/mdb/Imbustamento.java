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


package org.openspcoop2.pdd.mdb;

import java.util.Date;

import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.config.driver.DriverConfigurazioneNotFound;
import org.openspcoop2.core.constants.TipoPdD;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.OpenSPCoop2MessageFactory;
import org.openspcoop2.message.constants.MessageRole;
import org.openspcoop2.message.utils.MessageUtilities;
import org.openspcoop2.pdd.config.ConfigurazionePdDManager;
import org.openspcoop2.pdd.config.RichiestaDelegata;
import org.openspcoop2.pdd.core.CostantiPdD;
import org.openspcoop2.pdd.core.EJBUtils;
import org.openspcoop2.pdd.core.GestoreMessaggi;
import org.openspcoop2.pdd.core.PdDContext;
import org.openspcoop2.pdd.core.state.IOpenSPCoopState;
import org.openspcoop2.pdd.core.state.OpenSPCoopState;
import org.openspcoop2.pdd.core.state.OpenSPCoopStateException;
import org.openspcoop2.pdd.core.state.OpenSPCoopStateful;
import org.openspcoop2.pdd.core.state.OpenSPCoopStateless;
import org.openspcoop2.pdd.logger.MsgDiagnosticiProperties;
import org.openspcoop2.pdd.logger.MsgDiagnostico;
import org.openspcoop2.pdd.services.error.RicezioneContenutiApplicativiInternalErrorGenerator;
import org.openspcoop2.protocol.engine.RequestInfo;
import org.openspcoop2.protocol.engine.constants.Costanti;
import org.openspcoop2.protocol.engine.driver.ConsegnaInOrdine;
import org.openspcoop2.protocol.engine.driver.ProfiloDiCollaborazione;
import org.openspcoop2.protocol.engine.driver.RepositoryBuste;
import org.openspcoop2.protocol.engine.driver.Riscontri;
import org.openspcoop2.protocol.registry.RegistroServiziManager;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.Integrazione;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.Servizio;
import org.openspcoop2.protocol.sdk.builder.ProprietaErroreApplicativo;
import org.openspcoop2.protocol.sdk.config.IProtocolConfiguration;
import org.openspcoop2.protocol.sdk.config.IProtocolVersionManager;
import org.openspcoop2.protocol.sdk.config.ITraduttore;
import org.openspcoop2.protocol.sdk.constants.CodiceErroreIntegrazione;
import org.openspcoop2.protocol.sdk.constants.ErroreIntegrazione;
import org.openspcoop2.protocol.sdk.constants.ErroriIntegrazione;
import org.openspcoop2.protocol.sdk.constants.FunzionalitaProtocollo;
import org.openspcoop2.protocol.sdk.constants.InitialIdConversationType;
import org.openspcoop2.protocol.sdk.constants.Inoltro;
import org.openspcoop2.protocol.sdk.constants.IntegrationFunctionError;
import org.openspcoop2.protocol.sdk.constants.TipoOraRegistrazione;
import org.openspcoop2.protocol.sdk.state.StatelessMessage;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.date.DateUtils;
import org.slf4j.Logger;

/**
 * Contiene la libreria Imbustamento
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author Tronci Fabio (tronci@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class Imbustamento extends GenericLib{

	public final static String ID_MODULO = "Imbustamento";

	public Imbustamento(Logger log) throws GenericLibException {
		super(Imbustamento.ID_MODULO, log);
		inizializza();
	}	

	@Override
	protected synchronized void inizializza() throws GenericLibException {
		super.inizializza();
	}

	@Override
	public EsitoLib _onMessage(IOpenSPCoopState openspcoopstate,
			RegistroServiziManager registroServiziManager,ConfigurazionePdDManager configurazionePdDManager, 
			MsgDiagnostico msgDiag) throws OpenSPCoopStateException {

		EsitoLib esito = new EsitoLib();

		ImbustamentoMessage imbustamentoMsg = (ImbustamentoMessage) openspcoopstate.getMessageLib();
		
		/* PddContext */
		PdDContext pddContext = imbustamentoMsg.getPddContext();
		String idTransazione = PdDContext.getValue(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE, pddContext);
		RequestInfo requestInfo = (RequestInfo) pddContext.getObject(org.openspcoop2.core.constants.Costanti.REQUEST_INFO);
		
		/* Protocol Factory */
		IProtocolFactory<?> protocolFactory = null;
		IProtocolVersionManager protocolManager = null;
		ITraduttore traduttore = null;
		try{
			protocolFactory = this.protocolFactoryManager.getProtocolFactoryByName((String) pddContext.getObject(org.openspcoop2.core.constants.Costanti.PROTOCOL_NAME));
			protocolManager = protocolFactory.createProtocolVersionManager(imbustamentoMsg.getRichiestaDelegata().getProfiloGestione());
			traduttore = protocolFactory.createTraduttore();
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
		String implementazionePdDDestinatario = imbustamentoMsg.getImplementazionePdDSoggettoDestinatario();
		
		RichiestaDelegata richiestaDelegata = imbustamentoMsg.getRichiestaDelegata();
		IDSoggetto identitaPdD = imbustamentoMsg.getRichiestaDelegata().getDominio();
		TipoPdD tipoPdD = TipoPdD.DELEGATA;
		msgDiag.setDominio(identitaPdD);  // imposto anche il dominio nel msgDiag

		// Aggiornamento Informazioni messaggio diagnostico
		msgDiag.setPrefixMsgPersonalizzati(MsgDiagnosticiProperties.MSG_DIAG_IMBUSTAMENTO);
		msgDiag.setIdMessaggioRichiesta(idMessageRequest);
		msgDiag.addKeyword(CostantiPdD.KEY_ID_MESSAGGIO_RICHIESTA, idMessageRequest);
		if(msgDiag.getPorta()==null) {
			if(richiestaDelegata!=null && richiestaDelegata.getIdPortaDelegata()!=null) {
				msgDiag.updatePorta(tipoPdD, richiestaDelegata.getIdPortaDelegata().getNome());
			}
		}
		msgDiag.setFruitore(richiestaDelegata.getIdSoggettoFruitore());
		msgDiag.addKeywords(richiestaDelegata.getIdSoggettoFruitore());
		msgDiag.setServizio(richiestaDelegata.getIdServizio());
		msgDiag.addKeywords(richiestaDelegata.getIdServizio());
		msgDiag.setServizioApplicativo(richiestaDelegata.getServizioApplicativo());
		msgDiag.addKeyword(CostantiPdD.KEY_SA_FRUITORE, richiestaDelegata.getServizioApplicativo());
		if(richiestaDelegata.getProfiloCollaborazione()!=null)
			msgDiag.addKeyword(CostantiPdD.KEY_PROFILO_COLLABORAZIONE,traduttore.toString(richiestaDelegata.getProfiloCollaborazione()));
		msgDiag.setIdCorrelazioneApplicativa(richiestaDelegata.getIdCorrelazioneApplicativa());
		msgDiag.addKeyword(CostantiPdD.KEY_ID_CORRELAZIONE_APPLICATIVA, richiestaDelegata.getIdCorrelazioneApplicativa());
		msgDiag.addKeyword(CostantiPdD.KEY_FUNZIONALITA_COLLABORAZIONE, imbustamentoMsg.getInfoServizio().getCollaborazione()+"");
		msgDiag.addKeyword(CostantiPdD.KEY_FUNZIONALITA_CONFERMA_RICEZIONE, imbustamentoMsg.getInfoServizio().getConfermaRicezione()+"");
		msgDiag.addKeyword(CostantiPdD.KEY_FUNZIONALITA_FILTRO_DUPLICATI, Inoltro.SENZA_DUPLICATI.equals(imbustamentoMsg.getInfoServizio().getInoltro()) + "");
	
				
		//	ProprietaErroreApplicativo	
		ProprietaErroreApplicativo proprietaErroreAppl = richiestaDelegata.getFault();
		proprietaErroreAppl.setIdModulo(Imbustamento.ID_MODULO);
		
		// GeneratoreErrore
		RicezioneContenutiApplicativiInternalErrorGenerator generatoreErrore = null;
		try{
			generatoreErrore = new RicezioneContenutiApplicativiInternalErrorGenerator(this.log, this.idModulo, requestInfo);
			generatoreErrore.updateInformazioniCooperazione(richiestaDelegata.getIdSoggettoFruitore(), richiestaDelegata.getIdServizio());
			generatoreErrore.updateInformazioniCooperazione(richiestaDelegata.getServizioApplicativo());
			generatoreErrore.updateProprietaErroreApplicativo(proprietaErroreAppl);
		}catch(Exception e){
			msgDiag.logErroreGenerico(e, "RicezioneContenutiApplicativiGeneratoreErrore.instanziazione"); 
			openspcoopstate.releaseResource();
			esito.setEsitoInvocazione(false); 
			esito.setStatoInvocazioneErroreNonGestito(e);
			return esito;
		}
		

		/* ------------------ Inizializzo stato OpenSPCoop  --------------- */
		msgDiag.mediumDebug("Inizializzo stato per la gestione della richiesta...");
		openspcoopstate.initResource(identitaPdD, Imbustamento.ID_MODULO,idTransazione);
		registroServiziManager.updateState(openspcoopstate.getStatoRichiesta(),openspcoopstate.getStatoRisposta());
		configurazionePdDManager.updateState(openspcoopstate.getStatoRichiesta(),openspcoopstate.getStatoRisposta());
		msgDiag.updateState(openspcoopstate.getStatoRichiesta(),openspcoopstate.getStatoRisposta());


		/* ------------------ Inizializzazione Contesto di gestione  --------------- */
		msgDiag.mediumDebug("Inizializzo contesto per la gestione...");
		// EJBUtils (per eventuali errori)
		EJBUtils ejbUtils = null;
		try{
			ejbUtils = new EJBUtils(identitaPdD,tipoPdD,Imbustamento.ID_MODULO,idMessageRequest,
					idMessageRequest,Costanti.OUTBOX,openspcoopstate,msgDiag,false,
					imbustamentoMsg.getImplementazionePdDSoggettoMittente(),
					imbustamentoMsg.getImplementazionePdDSoggettoDestinatario(),
					imbustamentoMsg.getRichiestaDelegata().getProfiloGestione(),pddContext);
		}catch(Exception e){
			msgDiag.logErroreGenerico(e, "EJBUtils.new"); 
			openspcoopstate.releaseResource();
			esito.setEsitoInvocazione(false); 
			esito.setStatoInvocazioneErroreNonGestito(e);
			return esito;
		}

		// Oneway versione 11
		boolean oneWayVersione11 = imbustamentoMsg.isOneWayVersione11();
		ejbUtils.setOneWayVersione11(oneWayVersione11);

		// Gestori funzionalita'
		RepositoryBuste repositoryBuste = new RepositoryBuste(openspcoopstate.getStatoRichiesta(), true,protocolFactory);
		Riscontri gestoreRiscontri = new Riscontri(openspcoopstate.getStatoRichiesta());
		ProfiloDiCollaborazione profiloCollaborazione = null;
		try{
			profiloCollaborazione = new ProfiloDiCollaborazione(openspcoopstate.getStatoRichiesta(),protocolFactory);
		}catch(Exception e){
			msgDiag.logErroreGenerico(e, "ProfiloDiCollaborazione.new");
			openspcoopstate.releaseResource();
			esito.setEsitoInvocazione(false); 
			esito.setStatoInvocazioneErroreNonGestito(e);
			return esito;
		}
		ConsegnaInOrdine ordineConsegna = new ConsegnaInOrdine(openspcoopstate.getStatoRichiesta(),protocolFactory);
		GestoreMessaggi msgRequest = null;
		GestoreMessaggi msgOK = null; // Evenutuale busta OK (rigirata a ID_MODULO o a RICEZIONE_CONTENUTI_APPLICATIVI)

		Servizio infoServizio = imbustamentoMsg.getInfoServizio();
		String riferimentoServizioCorrelato = imbustamentoMsg.getRiferimentoServizioCorrelato();
		boolean isServizioCorrelato = infoServizio.isCorrelato();
		IDServizio idServizio = richiestaDelegata.getIdServizio();
		IDSoggetto soggettoFruitore = richiestaDelegata.getIdSoggettoFruitore();
		

		// Punto di inizio per la transazione.
		PortaDelegata pd = null;
		ServizioApplicativo sa = null;
		try{
			

			/**---- Check Utilizzo Consegna Risposta Asincrona */
			msgDiag.mediumDebug("Check utilizzo consegna risposta asincrona...");
			// Non deve essere utilizzata solo nel caso di richiesta con profilo AsincronoSimmetrico 
			if( !(infoServizio.getProfiloDiCollaborazione().equals(org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.ASINCRONO_SIMMETRICO) &&
					riferimentoServizioCorrelato==null 
					&& isServizioCorrelato==false) ){
				// di default la ricezioneAsincrona viene utilizzata.
				// Per i casi di :
				// - profiloOneWay
				// - profiloSincrono
				// - profiloAsincronoSimmetrico solo in consegna risposta
				// - profiloAsincronoAsimmetrico 
				richiestaDelegata.setUtilizzoConsegnaAsincrona(true); 
			}


			Utilities.printFreeMemory("Imbustamento - Costruzione busta ...");


			/* ---  Costruzione/Registrazione Busta  ----- */
			msgDiag.mediumDebug("Costruzione busta ...");
			Busta busta = new Busta(protocolFactory.getProtocol());

			// identificatore del servizio richiesto
			busta.setTipoDestinatario(idServizio.getSoggettoErogatore().getTipo());
			busta.setDestinatario(idServizio.getSoggettoErogatore().getNome());
			busta.setIdentificativoPortaDestinatario(idServizio.getSoggettoErogatore().getCodicePorta());
			busta.setIndirizzoDestinatario(imbustamentoMsg.getIndirizzoSoggettoDestinatario());
			
			busta.setVersioneServizio(idServizio.getVersione());
			busta.setServizio(idServizio.getNome());
			busta.setTipoServizio(idServizio.getTipo());
			busta.setAzione(idServizio.getAzione());

			// mittente della richiesta
			busta.setTipoMittente(soggettoFruitore.getTipo());
			busta.setMittente(soggettoFruitore.getNome());
			busta.setIdentificativoPortaMittente(soggettoFruitore.getCodicePorta());
			busta.setIndirizzoMittente(imbustamentoMsg.getIndirizzoSoggettoMittente());
			
			busta.setServizioApplicativoFruitore(richiestaDelegata.getServizioApplicativo());

			// Informazioni protocollo
			msgDiag.highDebug("Costruzione Busta con ID = "+idMessageRequest);
			busta.setID(idMessageRequest);

			Date oraRegistrazione = DateManager.getDate();
			msgDiag.highDebug("Costruzione Busta in data : ["+oraRegistrazione+"]");
			busta.setOraRegistrazione(oraRegistrazione);
			TipoOraRegistrazione tipoOraRegistrazione = this.propertiesReader.getTipoTempoBusta(implementazionePdDDestinatario);
			busta.setTipoOraRegistrazione(tipoOraRegistrazione,protocolFactory.createTraduttore().toString(tipoOraRegistrazione));
			busta.setScadenza(infoServizio.getScadenza());
			long scadenzaBusta = infoServizio.getScadenzaMinuti();
			if(scadenzaBusta<=0){
				scadenzaBusta = this.propertiesReader.getRepositoryIntervalloScadenzaMessaggi();
			}
			
			switch (protocolManager.getFiltroDuplicati(infoServizio)) {
			case ABILITATA:
				busta.setInoltro(Inoltro.SENZA_DUPLICATI,protocolFactory.createTraduttore().toString(Inoltro.SENZA_DUPLICATI));
				break;
			case DISABILITATA:
				busta.setInoltro(Inoltro.CON_DUPLICATI,protocolFactory.createTraduttore().toString(Inoltro.CON_DUPLICATI));
				break;
			default:
				busta.setInoltro(infoServizio.getInoltro(),protocolFactory.createTraduttore().toString(infoServizio.getInoltro()));
				break;
			}
				
			boolean consegnaAffidabile = false;
			switch (protocolManager.getConsegnaAffidabile(infoServizio)) {
			case ABILITATA:
				consegnaAffidabile = true;
				break;
			case DISABILITATA:
				consegnaAffidabile = false;
				break;
			default:
				consegnaAffidabile = this.propertiesReader.isGestioneRiscontri(implementazionePdDDestinatario) && infoServizio.getConfermaRicezione();
				break;
			}
			
			boolean idCollaborazione = false;
			switch (protocolManager.getCollaborazione(infoServizio)) {
			case ABILITATA:
				idCollaborazione = true;
				break;
			case DISABILITATA:
				idCollaborazione = false;
				break;
			default:
				idCollaborazione = this.propertiesReader.isGestioneElementoCollaborazione(implementazionePdDDestinatario) && infoServizio.getCollaborazione();
				break;
			}
			String idCollaborazioneCapostipite = null;
			if(idCollaborazione) {
				InitialIdConversationType initial = protocolFactory.createProtocolConfiguration().isGenerateInitialIdConversation(TipoPdD.DELEGATA, FunzionalitaProtocollo.COLLABORAZIONE);
				if(InitialIdConversationType.ID_TRANSAZIONE.equals(initial)) {
					idCollaborazioneCapostipite = idTransazione;
				}
				else if(InitialIdConversationType.ID_MESSAGGIO.equals(initial)) {
					idCollaborazioneCapostipite = idMessageRequest;
				}
			}
			
			boolean idRiferimentoMessaggioRichiesta = false;
			switch (protocolManager.getIdRiferimentoRichiesta(infoServizio)) {
			case ABILITATA:
				idRiferimentoMessaggioRichiesta = true;
				break;
			case DISABILITATA:
				idRiferimentoMessaggioRichiesta = false;
				break;
			default:
				idRiferimentoMessaggioRichiesta = this.propertiesReader.isGestioneElementoIdRiferimentoRichiesta(implementazionePdDDestinatario) && infoServizio.getIdRiferimentoRichiesta();
				break;
			}
			
			boolean consegnaInOrdine = false;
			switch (protocolManager.getConsegnaInOrdine(infoServizio)) {
			case ABILITATA:
				consegnaInOrdine = true;
				break;
			case DISABILITATA:
				consegnaInOrdine = false;
				break;
			default:
				consegnaInOrdine = this.propertiesReader.isGestioneConsegnaInOrdine(implementazionePdDDestinatario) && infoServizio.getOrdineConsegna();
				break;
			}
			
			
						
			/* ----------- ConsegnaAffidabile ------------- */
			if( consegnaAffidabile )
				busta.setConfermaRicezione(true);
			else
				busta.setConfermaRicezione(false);

						

			/* ----------- Lettura PortaDelegata e Servizio Applicativo ------------- */
			pd = configurazionePdDManager.getPortaDelegata(richiestaDelegata.getIdPortaDelegata());
			try{
				IDServizioApplicativo idSA = new IDServizioApplicativo();
				idSA.setNome(richiestaDelegata.getServizioApplicativo());
				idSA.setIdSoggettoProprietario(richiestaDelegata.getIdSoggettoFruitore());
				sa = configurazionePdDManager.getServizioApplicativo(idSA);
			}catch(DriverConfigurazioneNotFound e){
				if(CostantiPdD.SERVIZIO_APPLICATIVO_ANONIMO.equals(richiestaDelegata.getServizioApplicativo())==false)
					throw e;
			}
			
					
			// ------------- Controllo funzionalita di protocollo richieste siano compatibili con il protocollo -----------------------------
			try{
				
				// NOTA: Usare getIntegrationServiceBinding poichè le funzionalità si riferiscono al tipo di integrazione scelta
				
				IProtocolConfiguration protocolConfiguration = protocolFactory.createProtocolConfiguration();
				if(protocolConfiguration.isSupportato(requestInfo.getIntegrationServiceBinding(),infoServizio.getProfiloDiCollaborazione())==false){
					throw new Exception("Profilo di Collaborazione ["+infoServizio.getProfiloDiCollaborazione().getEngineValue()+"]");
				}
				if(busta.getInoltro()!=null && Inoltro.SENZA_DUPLICATI.equals(busta.getInoltro())){
					if(protocolConfiguration.isSupportato(requestInfo.getIntegrationServiceBinding(),FunzionalitaProtocollo.FILTRO_DUPLICATI)==false){
						throw new Exception(FunzionalitaProtocollo.FILTRO_DUPLICATI.getEngineValue());
					}
				}
				if(consegnaAffidabile){
					if(protocolConfiguration.isSupportato(requestInfo.getIntegrationServiceBinding(),FunzionalitaProtocollo.CONFERMA_RICEZIONE)==false){
						throw new Exception(FunzionalitaProtocollo.CONFERMA_RICEZIONE.getEngineValue());
					}
				}
				if(idCollaborazione){
					if(protocolConfiguration.isSupportato(requestInfo.getIntegrationServiceBinding(),FunzionalitaProtocollo.COLLABORAZIONE)==false){
						throw new Exception(FunzionalitaProtocollo.COLLABORAZIONE.getEngineValue());
					}
				}
				if(idRiferimentoMessaggioRichiesta){
					if(protocolConfiguration.isSupportato(requestInfo.getIntegrationServiceBinding(),FunzionalitaProtocollo.RIFERIMENTO_ID_RICHIESTA)==false){
						throw new Exception(FunzionalitaProtocollo.RIFERIMENTO_ID_RICHIESTA.getEngineValue());
					}
				}
				if(consegnaInOrdine){
					if(protocolConfiguration.isSupportato(requestInfo.getIntegrationServiceBinding(),FunzionalitaProtocollo.CONSEGNA_IN_ORDINE)==false){
						throw new Exception(FunzionalitaProtocollo.CONSEGNA_IN_ORDINE.getEngineValue());
					}
				}
				if(infoServizio.getScadenza()!=null){
					if(protocolConfiguration.isSupportato(requestInfo.getIntegrationServiceBinding(),FunzionalitaProtocollo.SCADENZA)==false){
						throw new Exception(FunzionalitaProtocollo.SCADENZA.getEngineValue());
					}
				}
				if(configurazionePdDManager.isGestioneManifestAttachments(pd,protocolFactory)){
					if(protocolConfiguration.isSupportato(requestInfo.getIntegrationServiceBinding(),FunzionalitaProtocollo.MANIFEST_ATTACHMENTS)==false){
						throw new Exception(FunzionalitaProtocollo.MANIFEST_ATTACHMENTS.getEngineValue());
					}
				}			
			}catch(Exception e){	
				msgDiag.addKeywordErroreProcessamento(e);
				msgDiag.logPersonalizzato("protocolli.funzionalita.unsupported");
				OpenSPCoop2Message responseMessageError = 
						generatoreErrore.build(pddContext,IntegrationFunctionError.NOT_SUPPORTED_BY_PROTOCOL, 
								ErroriIntegrazione.ERRORE_439_FUNZIONALITA_NOT_SUPPORTED_BY_PROTOCOL.
								getErrore439_FunzionalitaNotSupportedByProtocol(e.getMessage(), protocolFactory),e,
								null);
				ejbUtils.sendRispostaApplicativaErrore(responseMessageError,richiestaDelegata,pd,sa);
				openspcoopstate.releaseResource();
				esito.setEsitoInvocazione(true); 
				esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,
						msgDiag.getMessaggio_replaceKeywords("protocolli.funzionalita.unsupported"));
				return esito;
			}
			
			
			
			
			
			
			
			/* ------------  Check Consegna in Ordine che sia con profilo OneWay 
			                 e che rispetti vincoli  ------------- */	
			// Sequenza: deve essere abilitata la consegna affidabile + la collaborazione e infine la consegna in ordine
						
			if( consegnaInOrdine ){
				msgDiag.mediumDebug("Check vincoli consegna in ordine...");
				if ( infoServizio.getProfiloDiCollaborazione().equals(org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.ONEWAY)==false) {
					msgDiag.addKeyword(CostantiPdD.KEY_PROFILO_COLLABORAZIONE, traduttore.toString(infoServizio.getProfiloDiCollaborazione()));
					msgDiag.logPersonalizzato("consegnaInOrdine.profiloNonOneway");
					OpenSPCoop2Message responseMessageError = 
							generatoreErrore.build(pddContext,IntegrationFunctionError.NOT_SUPPORTED_BY_PROTOCOL, 
									ErroriIntegrazione.ERRORE_414_CONSEGNA_IN_ORDINE_CON_PROFILO_NO_ONEWAY.getErroreIntegrazione(),null,
								null);
					ejbUtils.sendRispostaApplicativaErrore(responseMessageError,richiestaDelegata,pd,sa);
					openspcoopstate.releaseResource();
					esito.setEsitoInvocazione(true); 
					esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,
							msgDiag.getMessaggio_replaceKeywords("consegnaInOrdine.profiloNonOneway"));
					return esito;
				}
				if(this.propertiesReader.isGestioneRiscontri(implementazionePdDDestinatario)==false){
					msgDiag.logPersonalizzato("consegnaInOrdine.confermaRicezioneNonRichiesta");
					OpenSPCoop2Message responseMessageError = 
							generatoreErrore.build(pddContext,IntegrationFunctionError.NOT_SUPPORTED_BY_PROTOCOL, 
									ErroriIntegrazione.ERRORE_415_CONSEGNA_IN_ORDINE_SENZA_VINCOLI_RICHIESTI.getErroreIntegrazione(),null,
								null);
					ejbUtils.sendRispostaApplicativaErrore(responseMessageError,richiestaDelegata,pd,sa);
					openspcoopstate.releaseResource();
					esito.setEsitoInvocazione(true); 
					esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,
							msgDiag.getMessaggio_replaceKeywords("consegnaInOrdine.confermaRicezioneNonRichiesta"));
					return esito;
				}
				if(this.propertiesReader.isGestioneElementoCollaborazione(implementazionePdDDestinatario)==false){
					msgDiag.logPersonalizzato("consegnaInOrdine.idCollaborazioneNonRichiesto");
					OpenSPCoop2Message responseMessageError = 
							generatoreErrore.build(pddContext,IntegrationFunctionError.NOT_SUPPORTED_BY_PROTOCOL, 
									ErroriIntegrazione.ERRORE_415_CONSEGNA_IN_ORDINE_SENZA_VINCOLI_RICHIESTI.getErroreIntegrazione(),null,
									null);
					ejbUtils.sendRispostaApplicativaErrore(responseMessageError,richiestaDelegata,pd,sa);
					openspcoopstate.releaseResource();
					esito.setEsitoInvocazione(true); 
					esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,
							msgDiag.getMessaggio_replaceKeywords("consegnaInOrdine.idCollaborazioneNonRichiesto"));
					return esito;
				}
				if(infoServizio.getCollaborazione()==false || infoServizio.getConfermaRicezione()==false || Inoltro.SENZA_DUPLICATI.equals(infoServizio.getInoltro())==false){
					msgDiag.logPersonalizzato("consegnaInOrdine.funzionalitaMancanti");
					OpenSPCoop2Message responseMessageError = 
							generatoreErrore.build(pddContext,IntegrationFunctionError.NOT_SUPPORTED_BY_PROTOCOL,  
									ErroriIntegrazione.ERRORE_415_CONSEGNA_IN_ORDINE_SENZA_VINCOLI_RICHIESTI.getErroreIntegrazione(),null,
									null);
					ejbUtils.sendRispostaApplicativaErrore(responseMessageError,richiestaDelegata,pd,sa);
					openspcoopstate.releaseResource();
					esito.setEsitoInvocazione(true); 
					esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,
							msgDiag.getMessaggio_replaceKeywords("consegnaInOrdine.funzionalitaMancanti"));
					return esito;
				}
			}
			boolean gestioneConsegnaInOrdineAbilitata = consegnaInOrdine &&
				this.propertiesReader.isGestioneRiscontri(implementazionePdDDestinatario) && 
				this.propertiesReader.isGestioneElementoCollaborazione(implementazionePdDDestinatario) && 
				this.propertiesReader.isGestioneConsegnaInOrdine(implementazionePdDDestinatario);



			
			
			
			
			

			
			
			

			Utilities.printFreeMemory("Imbustamento - Gestione profilo di Collaborazione...");






			/* ------------  Profilo di Collaborazione  ------------- */	
			boolean generazioneMsgOK = false;
			String scenarioCooperazione = null;
			if(infoServizio.getProfiloDiCollaborazione().equals(org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.ONEWAY)) {	

				msgDiag.mediumDebug("Gestione profilo di Collaborazione OneWay ...");

				// MessaggioSingoloOneWay		
				busta.setProfiloDiCollaborazione(org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.ONEWAY);
				scenarioCooperazione = Costanti.SCENARIO_ONEWAY_INVOCAZIONE_SERVIZIO;

				// Collaborazione
				if( idCollaborazione ){
					if(imbustamentoMsg.getIdCollaborazione()!=null)
						busta.setCollaborazione(imbustamentoMsg.getIdCollaborazione());
					else
						busta.setCollaborazione(idCollaborazioneCapostipite);
				}
				
				// RiferimentoMessaggio
				if( idRiferimentoMessaggioRichiesta ){
					if(imbustamentoMsg.getIdRiferimentoMessaggio()!=null)
						busta.setRiferimentoMessaggio(imbustamentoMsg.getIdRiferimentoMessaggio());
				}

				if(gestioneConsegnaInOrdineAbilitata && infoServizio.getOrdineConsegna()){
					if(oneWayVersione11 || openspcoopstate instanceof OpenSPCoopStateful){
						msgDiag.mediumDebug("Gestione profilo di Collaborazione OneWay (consegna in ordine)...");
						try{
							ordineConsegna.setNextSequenza_daInviare(busta);
						}catch(Exception e){
							msgDiag.logErroreGenerico(e, "ordineConsegna.setNextSequenza_daInviare");
							openspcoopstate.releaseResource();
							esito.setEsitoInvocazione(true); 
							esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO, "ordineConsegna.setNextSequenza_daInviare");
							return esito;
						}
					}

				}

				// Modalita di trasmissione sincrona/asincrona
				if(configurazionePdDManager.isModalitaStateless(pd, infoServizio.getProfiloDiCollaborazione())==false){
					generazioneMsgOK = true;
				}
				else{
					generazioneMsgOK = false;
				}


			} else if(infoServizio.getProfiloDiCollaborazione().equals(org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.SINCRONO)) {	

				msgDiag.mediumDebug("Gestione profilo di Collaborazione Sincrono ...");

				// Profilo Sincrono		
				busta.setProfiloDiCollaborazione(org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.SINCRONO);
				scenarioCooperazione = Costanti.SCENARIO_SINCRONO_INVOCAZIONE_SERVIZIO;

				try {
					msgDiag.mediumDebug("Gestione profilo di Collaborazione Sincrono (Registrazione Busta da inviare)...");
					profiloCollaborazione.registraBustaInviata(idMessageRequest);
				} catch(ProtocolException e){
					msgDiag.logErroreGenerico(e, "profiloDiCollaborazione.registraBustaInviata");
					OpenSPCoop2Message responseMessageError = 
							generatoreErrore.build(pddContext,IntegrationFunctionError.INTERNAL_REQUEST_ERROR, 
									ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
								get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_525_GESTIONE_FUNZIONALITA_PROTOCOLLO),e,
								null);
					ejbUtils.sendRispostaApplicativaErrore(responseMessageError,richiestaDelegata,pd,sa);
					openspcoopstate.releaseResource();
					esito.setEsitoInvocazione(true); 
					esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO, "profiloDiCollaborazione.registraBustaInviata");
					return esito;
				}

				//	Collaborazione
				if( idCollaborazione ){
					if(imbustamentoMsg.getIdCollaborazione()!=null)
						busta.setCollaborazione(imbustamentoMsg.getIdCollaborazione());
					else
						busta.setCollaborazione(idCollaborazioneCapostipite);
				}
				
				// RiferimentoMessaggio
				if( idRiferimentoMessaggioRichiesta ){
					if(imbustamentoMsg.getIdRiferimentoMessaggio()!=null)
						busta.setRiferimentoMessaggio(imbustamentoMsg.getIdRiferimentoMessaggio());
				}

			}else if(infoServizio.getProfiloDiCollaborazione().equals(org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.ASINCRONO_SIMMETRICO)) {	

				msgDiag.mediumDebug("Gestione profilo di Collaborazione AsincronoSimmetrico ...");

				// Profilo Asincrono Simmetrico		  
				busta.setProfiloDiCollaborazione(org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.ASINCRONO_SIMMETRICO); 
				// deve essere generato un msg OK se non e' abilitata la funzionalita' di ricevuta asincrona
				msgDiag.mediumDebug("Gestione profilo di Collaborazione AsincronoSimmetrico (check ricevuta abilitata)...");
				richiestaDelegata.setRicevutaAsincrona(configurazionePdDManager.ricevutaAsincronaSimmetricaAbilitata(pd));
				
				// Modalita di trasmissione stateless/stateful
				if(configurazionePdDManager.isModalitaStateless(pd, infoServizio.getProfiloDiCollaborazione())==false){
					generazioneMsgOK = !richiestaDelegata.isRicevutaAsincrona();
				}
				else{
					generazioneMsgOK = false;
				}

				//	Richiesta Asincrona
				if(riferimentoServizioCorrelato==null && isServizioCorrelato==false){

					msgDiag.mediumDebug("Gestione profilo di Collaborazione AsincronoSimmetrico richiesta...");

					// check autenticazione richiesta
					if(CostantiPdD.SERVIZIO_APPLICATIVO_ANONIMO.equals(richiestaDelegata.getServizioApplicativo())){
						msgDiag.logPersonalizzato("profiloAsincronoSimmetrico.saAnonimo");
						OpenSPCoop2Message responseMessageError = 
								generatoreErrore.build(pddContext,IntegrationFunctionError.AUTHENTICATION, 
										ErroriIntegrazione.ERRORE_410_AUTENTICAZIONE_RICHIESTA.getErroreIntegrazione(),null,
									null);
						ejbUtils.sendRispostaApplicativaErrore(responseMessageError,richiestaDelegata,pd,sa);
						openspcoopstate.releaseResource();

						esito.setEsitoInvocazione(true); 
						esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,
								msgDiag.getMessaggio_replaceKeywords("profiloAsincronoSimmetrico.saAnonimo"));
						return esito;
					}

					// check esistenza consegna risposta asincrona
					msgDiag.mediumDebug("Gestione profilo di Collaborazione AsincronoSimmetrico richiesta (consegna asincrona)...");
					boolean existsConsegnaRispostaAsincrona = false;
					try{
						existsConsegnaRispostaAsincrona = configurazionePdDManager.existsConsegnaRispostaAsincrona(sa);
					}catch(Exception e){
						msgDiag.logErroreGenerico(e, "existsConsegnaRispostaAsincrona("+sa+")");
						OpenSPCoop2Message responseMessageError = 
								generatoreErrore.build(pddContext,IntegrationFunctionError.INTERNAL_REQUEST_ERROR, 
										ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
											get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_536_CONFIGURAZIONE_NON_DISPONIBILE),e,null);
						ejbUtils.sendRispostaApplicativaErrore(responseMessageError,richiestaDelegata,pd,sa);
						openspcoopstate.releaseResource();	
						esito.setEsitoInvocazione(true); 
						esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,
								"existsConsegnaRispostaAsincrona("+sa+")");
						return esito;
					}
					if (existsConsegnaRispostaAsincrona == false){
						msgDiag.logPersonalizzato("profiloAsincronoSimmetrico.saSenzaRispostaAsincrona");
						OpenSPCoop2Message responseMessageError = 
								generatoreErrore.build(pddContext,IntegrationFunctionError.INTERNAL_REQUEST_ERROR, 
									ErroriIntegrazione.ERRORE_411_RICEZIONE_CONTENUTI_ASINCRONA_RICHIESTA.getErroreIntegrazione(),null,null);
						ejbUtils.sendRispostaApplicativaErrore(responseMessageError,richiestaDelegata,pd,sa);
						openspcoopstate.releaseResource();
						esito.setEsitoInvocazione(true); 
						esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,
								msgDiag.getMessaggio_replaceKeywords("profiloAsincronoSimmetrico.saSenzaRispostaAsincrona"));
						return esito;
					}

					scenarioCooperazione = Costanti.SCENARIO_ASINCRONO_SIMMETRICO_INVOCAZIONE_SERVIZIO;

					// impostazione dati servizio correlato
					if(infoServizio.getServizioCorrelato()==null || infoServizio.getTipoServizioCorrelato()==null){
						msgDiag.logPersonalizzato("profiloAsincronoSimmetrico.servizioCorrelatoNonEsistente");
						OpenSPCoop2Message responseMessageError = 
								generatoreErrore.build(pddContext,IntegrationFunctionError.INTERNAL_REQUEST_ERROR, 
									ErroriIntegrazione.ERRORE_408_SERVIZIO_CORRELATO_NON_TROVATO.getErroreIntegrazione(),null,null);
						ejbUtils.sendRispostaApplicativaErrore(responseMessageError,richiestaDelegata,pd,sa);
						openspcoopstate.releaseResource();
						esito.setEsitoInvocazione(true); 
						esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,
								msgDiag.getMessaggio_replaceKeywords("profiloAsincronoSimmetrico.servizioCorrelatoNonEsistente"));
						return esito;
					}
					// PRODUZIONE tipo e nome Servizio Correlato
					if( protocolManager.isGenerazioneInformazioniServizioCorrelatoAsincronoSimmetrico() && 
							this.propertiesReader.isGenerazioneAttributiAsincroni(implementazionePdDDestinatario)){
						busta.setServizioCorrelato(infoServizio.getServizioCorrelato());
						busta.setTipoServizioCorrelato(infoServizio.getTipoServizioCorrelato());
					}

					//	Collaborazione (per gli asincroni devono sempre essere presenti)
					if( idCollaborazione ){
						if(imbustamentoMsg.getIdCollaborazione()!=null)
							busta.setCollaborazione(imbustamentoMsg.getIdCollaborazione());
						else
							busta.setCollaborazione(idCollaborazioneCapostipite);
					}

					// registrazione richiesta asincrona simmetrica
					try{
						msgDiag.mediumDebug("Gestione profilo di Collaborazione AsincronoSimmetrico richiesta (registrazione busta da inviare)...");
						
						Integrazione infoIntegrazione = null;
						if(configurazionePdDManager.isModalitaStateless(pd, infoServizio.getProfiloDiCollaborazione())){
							infoIntegrazione = new Integrazione();
							infoIntegrazione.setIdModuloInAttesa(this.idModulo);
							infoIntegrazione.setNomePorta(richiestaDelegata.getIdPortaDelegata().getNome());
							infoIntegrazione.setServizioApplicativo(richiestaDelegata.getServizioApplicativo());
							
							if(busta.getScadenza()==null){
								busta.setScadenza(new Date(DateManager.getTimeMillis()+(scadenzaBusta*60*1000)));
							}
							if(openspcoopstate instanceof OpenSPCoopStateless){
								((StatelessMessage)openspcoopstate.getStatoRichiesta()).setBusta(busta);
							}
						}
						profiloCollaborazione.asincronoSimmetrico_registraRichiestaInviata(idMessageRequest,busta.getCollaborazione(),
								infoServizio.getTipoServizioCorrelato(),
								infoServizio.getServizioCorrelato(),
								infoServizio.getVersioneServizioCorrelato(),
								richiestaDelegata.isRicevutaAsincrona(),infoIntegrazione,scadenzaBusta);
					}catch(ProtocolException e){
						msgDiag.logErroreGenerico(e, "profiloDiCollaborazione.asincronoSimmetrico_registraRichiestaInviata");
						openspcoopstate.releaseResource();
						OpenSPCoop2Message responseMessageError = 
								generatoreErrore.build(pddContext,IntegrationFunctionError.INTERNAL_REQUEST_ERROR, 
										ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
											get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_525_GESTIONE_FUNZIONALITA_PROTOCOLLO),e,null);
						ejbUtils.sendRispostaApplicativaErrore(responseMessageError,richiestaDelegata,pd,sa);
						esito.setEsitoInvocazione(true); 
						esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,
								"profiloDiCollaborazione.asincronoSimmetrico_registraRichiestaInviata");
						return esito;
					}

					// Non deve essere utilizzato la definizione dell'elemento 'ricezioneRispostaAsincrona'
					// per ritornare la ricevuta o l'ok.
					// L'elemento 'ricezioneRispostaAsincrona' sara' utilizzato per ritornare la risposta.
					richiestaDelegata.setUtilizzoConsegnaAsincrona(false); 


				}
				// Risposta Asincrona
				else{
					msgDiag.mediumDebug("Gestione profilo di Collaborazione AsincronoSimmetrico risposta...");
					
					scenarioCooperazione = Costanti.SCENARIO_ASINCRONO_SIMMETRICO_CONSEGNA_RISPOSTA;

					msgDiag.addKeyword(CostantiPdD.KEY_ID_CORRELAZIONE_ASINCRONA, riferimentoServizioCorrelato);
					msgDiag.logPersonalizzato("profiloAsincronoSimmetrico.risposta.correlazioneRichiesta");
					
					if(openspcoopstate instanceof OpenSPCoopStateless){
						
						if(busta.getScadenza()==null){
							busta.setScadenza(new Date(DateManager.getTimeMillis()+(scadenzaBusta*60*1000)));
						}
						
						((StatelessMessage)openspcoopstate.getStatoRichiesta()).setBusta(busta);
					}
					
					// imposto dati risposta
					try{
						msgDiag.mediumDebug("Gestione profilo di Collaborazione AsincronoSimmetrico risposta (lettura busta risposta)...");
						Busta rispostaAsincrona = profiloCollaborazione.asincronoSimmetrico_getBustaRisposta(riferimentoServizioCorrelato,idMessageRequest,scadenzaBusta);

						// Check di coerenza
						msgDiag.mediumDebug("Gestione profilo di Collaborazione AsincronoSimmetrico risposta (check coerenza)...");
						if( busta.getTipoMittente().equals(rispostaAsincrona.getTipoDestinatario())==false){
							throw new ProtocolException("Il tipo di mittente della risposta non rispetta quello atteso nella gestione del profilo asincrono simmetrico.");
						}
						if( busta.getMittente().equals(rispostaAsincrona.getDestinatario())==false){
							throw new ProtocolException("Il mittente della risposta non rispetta quello atteso nella gestione del profilo asincrono simmetrico.");
						}
						if( busta.getTipoDestinatario().equals(rispostaAsincrona.getTipoMittente())==false){
							throw new ProtocolException("Il tipo di destinatario della risposta non rispetta quello atteso nella gestione del profilo asincrono simmetrico.");
						}
						if( busta.getDestinatario().equals(rispostaAsincrona.getMittente())==false){
							throw new ProtocolException("Il destinatario della risposta non rispetta quello atteso nella gestione del profilo asincrono simmetrico.");
						}
						if( busta.getTipoServizio().equals(rispostaAsincrona.getTipoServizioCorrelato())==false){
							throw new ProtocolException("Il tipo del servizio correlato invocato non rispetta quello atteso nella gestione del profilo asincrono simmetrico.");
						}
						if( busta.getServizio().equals(rispostaAsincrona.getServizioCorrelato())==false){
							throw new ProtocolException("Il servizio correlato invocato non rispetta quello atteso nella gestione del profilo asincrono simmetrico.");
						}
						/* L'azione della risposta puo' essere diversa da quella della richiesta.
						if( (busta.getAzione()!=null) &&
							(busta.getAzione().equals(rispostaAsincrona.getAzione())==false)){
							throw new ProtocolException("L'azione invocata non rispetta quella attesa nella gestione del profilo asincrono simmetrico.");
						}
						 */

						if(protocolManager.isCorrelazioneRichiestaPresenteRispostaAsincronaSimmetrica()){
							busta.setRiferimentoMessaggio(riferimentoServizioCorrelato);
						}

					}catch(ProtocolException e){
						ErroreIntegrazione erroreIntegrazione = null;
						IntegrationFunctionError integrationFunctionError = null;
						this.log.error("Busta per correlazione asincrona simmetrica ("+riferimentoServizioCorrelato+") non trovata",e);
						if(e.getMessage()!=null && (e.getMessage().indexOf("Busta non trovata")!=-1) || (e.getMessage().indexOf("Tipo/Nome servizio correlato non trovato")!=-1)){
							msgDiag.logPersonalizzato("profiloAsincronoSimmetrico.rispostaNonCorrelataRichiesta");
							erroreIntegrazione = ErroriIntegrazione.ERRORE_409_RISPOSTA_ASINCRONA_NON_CORRELATA_ALLA_RICHIESTA.getErroreIntegrazione();
							integrationFunctionError = IntegrationFunctionError.CORRELATION_INFORMATION_NOT_FOUND;
						}else{
							msgDiag.logErroreGenerico(e, "profiloDiCollaborazione.asincronoSimmetrico_getBustaRisposta");
							erroreIntegrazione = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
									get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_525_GESTIONE_FUNZIONALITA_PROTOCOLLO);
							integrationFunctionError = IntegrationFunctionError.INTERNAL_REQUEST_ERROR;
						}

						OpenSPCoop2Message responseMessageError = 
								generatoreErrore.build(pddContext,integrationFunctionError, erroreIntegrazione,e,null);
						ejbUtils.sendRispostaApplicativaErrore(responseMessageError,richiestaDelegata,pd,sa);
						openspcoopstate.releaseResource();
						esito.setEsitoInvocazione(true); 
						esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,
								"Busta per correlazione asincrona simmetrica ("+riferimentoServizioCorrelato+") non trovata");
						return esito;
					}

					//	Collaborazione (per gli asincroni devono sempre essere presenti)
					if( idCollaborazione ){
						if(imbustamentoMsg.getIdCollaborazione()!=null)
							busta.setCollaborazione(imbustamentoMsg.getIdCollaborazione());
						else
							busta.setCollaborazione(riferimentoServizioCorrelato);
					}
					
				}

			}else if(infoServizio.getProfiloDiCollaborazione().equals(org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.ASINCRONO_ASIMMETRICO)) {	

				msgDiag.mediumDebug("Gestione profilo di Collaborazione AsincronoAsimmetrico ...");

				//	Profilo Asincrono Asimmetrico		  
				busta.setProfiloDiCollaborazione(org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.ASINCRONO_ASIMMETRICO); 

				//	Richiesta Asincrona
				if(riferimentoServizioCorrelato==null && isServizioCorrelato==false){

					msgDiag.mediumDebug("Gestione profilo di Collaborazione AsincronoAsimmetrico richiesta...");

					//	deve essere generato un msg OK se non e' abilitata la funzionalita' di ricevuta asincrona
					msgDiag.mediumDebug("Gestione profilo di Collaborazione AsincronoAsimmetrico richiesta (ricevuta abilitata)...");
					richiestaDelegata.setRicevutaAsincrona(configurazionePdDManager.ricevutaAsincronaAsimmetricaAbilitata(pd));
					
					// Modalita di trasmissione stateless/stateful
					if(configurazionePdDManager.isModalitaStateless(pd, infoServizio.getProfiloDiCollaborazione())==false){
						generazioneMsgOK = !richiestaDelegata.isRicevutaAsincrona();
					}
					else{
						generazioneMsgOK = false;
					}

					scenarioCooperazione = Costanti.SCENARIO_ASINCRONO_ASIMMETRICO_INVOCAZIONE_SERVIZIO;

					// Collaborazione (per gli asincroni devono sempre essere presenti)
					if( idCollaborazione ){
						if(imbustamentoMsg.getIdCollaborazione()!=null)
							busta.setCollaborazione(imbustamentoMsg.getIdCollaborazione());
						else
							busta.setCollaborazione(idCollaborazioneCapostipite);
					}

					// registrazione richiesta asincrona asimmetrica
					try{
						msgDiag.mediumDebug("Gestione profilo di Collaborazione AsincronoAsimmetrico richiesta (registrazione busta da inviare)...");
						
						if(configurazionePdDManager.isModalitaStateless(pd, infoServizio.getProfiloDiCollaborazione())){
							if(busta.getScadenza()==null){
								busta.setScadenza(new Date(DateManager.getTimeMillis()+(scadenzaBusta*60*1000)));
							}
							if(openspcoopstate instanceof OpenSPCoopStateless){
								((StatelessMessage)openspcoopstate.getStatoRichiesta()).setBusta(busta);
							}
						}
						
						profiloCollaborazione.asincronoAsimmetrico_registraRichiestaInviata(idMessageRequest,busta.getCollaborazione(),richiestaDelegata.isRicevutaAsincrona(),scadenzaBusta);
					}catch(ProtocolException e){
						msgDiag.logErroreGenerico(e, "profiloDiCollaborazione.asincronoAsimmetrico_registraRichiestaInviata");
						OpenSPCoop2Message responseMessageError = 
								generatoreErrore.build(pddContext,IntegrationFunctionError.INTERNAL_REQUEST_ERROR,
										ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
									get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_525_GESTIONE_FUNZIONALITA_PROTOCOLLO),e,
									null);
						ejbUtils.sendRispostaApplicativaErrore(responseMessageError,richiestaDelegata,pd,sa);
						openspcoopstate.releaseResource();
						esito.setEsitoInvocazione(true); 
						esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,
								"profiloDiCollaborazione.asincronoAsimmetrico_registraRichiestaInviata");
						return esito;
					}


				}
				// Risposta Asincrona
				else{
					msgDiag.mediumDebug("Gestione profilo di Collaborazione AsincronoAsimmetrico richiestaStato...");

					scenarioCooperazione = Costanti.SCENARIO_ASINCRONO_ASIMMETRICO_POLLING;

					msgDiag.addKeyword(CostantiPdD.KEY_ID_CORRELAZIONE_ASINCRONA, riferimentoServizioCorrelato);
					msgDiag.logPersonalizzato("profiloAsincronoAsimmetrico.richiestaStato.correlazioneRichiesta");
					
					// il polling e' sempre con gestione della ricevuta
					richiestaDelegata.setRicevutaAsincrona(true);

					if(openspcoopstate instanceof OpenSPCoopStateless){
						
						if(busta.getScadenza()==null){
							busta.setScadenza(new Date(DateManager.getTimeMillis()+(scadenzaBusta*60*1000)));
						}
						
						((StatelessMessage)openspcoopstate.getStatoRichiesta()).setBusta(busta);
					}
					
					// imposto dati risposta
					try{
						msgDiag.mediumDebug("Gestione profilo di Collaborazione AsincronoAsimmetrico richiestaStato (lettura busta)...");
						Busta richiestaStatoAsincrona = profiloCollaborazione.asincronoAsimmetrico_getBustaRisposta(riferimentoServizioCorrelato,idMessageRequest,scadenzaBusta);

						// Check di coerenza
						msgDiag.mediumDebug("Gestione profilo di Collaborazione AsincronoAsimmetrico richiestaStato (check coerenza)...");
						if( busta.getTipoMittente().equals(richiestaStatoAsincrona.getTipoMittente())==false){
							throw new ProtocolException("Il tipo di mittente della risposta non rispetta quello atteso nella gestione del profilo asincrono asimmetrico.");
						}
						if( busta.getMittente().equals(richiestaStatoAsincrona.getMittente())==false){
							throw new ProtocolException("Il mittente della risposta non rispetta quello atteso nella gestione del profilo asincrono asimmetrico.");
						}
						if( busta.getTipoDestinatario().equals(richiestaStatoAsincrona.getTipoDestinatario())==false){
							throw new ProtocolException("Il tipo di destinatario della risposta non rispetta quello atteso nella gestione del profilo asincrono asimmetrico.");
						}
						if( busta.getDestinatario().equals(richiestaStatoAsincrona.getDestinatario())==false){
							throw new ProtocolException("Il destinatario della risposta non rispetta quello atteso nella gestione del profilo asincrono asimmetrico.");
						}
						if( busta.getTipoServizio().equals(richiestaStatoAsincrona.getTipoServizioCorrelato())==false){
							throw new ProtocolException("Il tipo del servizio correlato invocato non rispetta quello atteso nella gestione del profilo asincrono asimmetrico.");
						}
						if( busta.getServizio().equals(richiestaStatoAsincrona.getServizioCorrelato())==false){
							throw new ProtocolException("Il servizio correlato invocato non rispetta quello atteso nella gestione del profilo asincrono asimmetrico.");
						}
						/* L'azione della richiestaStato puo' essere diversa da quella della richiesta.
						if( (busta.getAzione()!=null) &&
							(busta.getAzione().equals(richiestaStatoAsincrona.getAzione())==false)){
							throw new ProtocolException("L'azione invocata non rispetta quella attesa nella gestione del profilo asincrono asimmetrico.");
						}
						 */

						if(protocolManager.isCorrelazioneRichiestaPresenteRichiestaStatoAsincronaAsimmetrica()){
							busta.setRiferimentoMessaggio(riferimentoServizioCorrelato);
						}

					}catch(ProtocolException e){
						ErroreIntegrazione erroreIntegrazione = null;
						IntegrationFunctionError integrationFunctionError = null;
						this.log.error("Busta per correlazione asincrona asimmetrica ("+riferimentoServizioCorrelato+") non trovata",e);
						if(e.getMessage()!=null && e.getMessage().indexOf("Busta non trovata")!=-1){
							msgDiag.logPersonalizzato("profiloAsincronoAsimmetrico.richiestaStatoNonCorrelataRichiesta");
							erroreIntegrazione = ErroriIntegrazione.ERRORE_409_RISPOSTA_ASINCRONA_NON_CORRELATA_ALLA_RICHIESTA.getErroreIntegrazione();
							integrationFunctionError = IntegrationFunctionError.CORRELATION_INFORMATION_NOT_FOUND;
						}else{
							msgDiag.logErroreGenerico(e, "profiloDiCollaborazione.asincronoAsimmetrico_getBustaRisposta");
							erroreIntegrazione = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
									get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_525_GESTIONE_FUNZIONALITA_PROTOCOLLO);
							integrationFunctionError = IntegrationFunctionError.INTERNAL_REQUEST_ERROR;
						}
						OpenSPCoop2Message responseMessageError = 
								generatoreErrore.build(pddContext,integrationFunctionError,erroreIntegrazione,e,null);
						ejbUtils.sendRispostaApplicativaErrore(responseMessageError,richiestaDelegata,pd,sa);
						openspcoopstate.releaseResource(); 
						esito.setEsitoInvocazione(true); 
						esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,
								"Busta per correlazione asincrona asimmetrica ("+riferimentoServizioCorrelato+") non trovata");
						return esito;
					}

					//	Collaborazione (per gli asincroni devono sempre essere presenti)
					if( idCollaborazione ){
						if(imbustamentoMsg.getIdCollaborazione()!=null)
							busta.setCollaborazione(imbustamentoMsg.getIdCollaborazione());
						else
							busta.setCollaborazione(riferimentoServizioCorrelato);
					}

				}

			}	else{

				// Profilo non conosciuto??
				msgDiag.logErroreGenerico("Profilo di Collaborazione ["+infoServizio.getProfiloDiCollaborazione()+"] non gestito", "gestioneProfiloCollaborazione");
				OpenSPCoop2Message responseMessageError = 
						generatoreErrore.build(pddContext,IntegrationFunctionError.INTERNAL_REQUEST_ERROR,
								ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
									get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_525_GESTIONE_FUNZIONALITA_PROTOCOLLO),null,null);
				ejbUtils.sendRispostaApplicativaErrore(responseMessageError,richiestaDelegata,pd,sa);
				openspcoopstate.releaseResource();
				esito.setEsitoInvocazione(true); 
				esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,
						"Profilo di Collaborazione ["+infoServizio.getProfiloDiCollaborazione()+"] non gestito");
				return esito;
			}

			richiestaDelegata.setScenario(scenarioCooperazione);
			ejbUtils.setScenarioCooperazione(scenarioCooperazione);
			richiestaDelegata.setIdCollaborazione(busta.getCollaborazione());
			richiestaDelegata.setProfiloCollaborazione(busta.getProfiloDiCollaborazione(),busta.getProfiloDiCollaborazioneValue());


			boolean stateless = configurazionePdDManager.isModalitaStateless(pd, infoServizio.getProfiloDiCollaborazione());
						
			if (!oneWayVersione11 && !stateless) {
				
				/* ------------  Registro busta nel Repository Buste ------------- */
				msgDiag.mediumDebug("Aggiornamento busta nel RepositoryBuste...");
				try{
					repositoryBuste.aggiornaBustaIntoOutBox(busta,this.propertiesReader.getRepositoryIntervalloScadenzaMessaggi());
					repositoryBuste.aggiornaInfoIntegrazioneIntoOutBox_Scenario(busta.getID(),scenarioCooperazione);
				}catch(ProtocolException e){
					msgDiag.logErroreGenerico(e, "repositoryBuste.aggiornaBusta/InfoIntegrazione_IntoOutBox");
					OpenSPCoop2Message responseMessageError = 
							generatoreErrore.build(pddContext,IntegrationFunctionError.INTERNAL_REQUEST_ERROR,
									ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
										get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_525_GESTIONE_FUNZIONALITA_PROTOCOLLO),e,	null);
					ejbUtils.sendRispostaApplicativaErrore(responseMessageError,richiestaDelegata,pd,sa);
					openspcoopstate.releaseResource();
					esito.setEsitoInvocazione(true); 
					esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,
							"repositoryBuste.aggiornaBusta/InfoIntegrazione_IntoOutBox");
					return esito;
				}

			}




			/* ------------  Ricezione riscontri per il NAL 
	   	              (vengono gestiti solo se la busta contiene un profilo oneWay)  ------------- */
			if( consegnaAffidabile ){
				if(org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.ONEWAY.equals(infoServizio.getProfiloDiCollaborazione())) {

					msgDiag.logPersonalizzato("consegnaAffidabile.salvataggioInformazioni");
					try{
						gestoreRiscontri.registraRiscontroDaRicevere( busta.getID() , DateManager.getDate());
					}catch(ProtocolException e){
						msgDiag.logErroreGenerico(e, "gestoreRiscontri.registraRiscontroDaRicevere");
						OpenSPCoop2Message responseMessageError = 
								generatoreErrore.build(pddContext,IntegrationFunctionError.INTERNAL_REQUEST_ERROR,
										ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
											get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_525_GESTIONE_FUNZIONALITA_PROTOCOLLO),e,	null);
						ejbUtils.sendRispostaApplicativaErrore(responseMessageError,richiestaDelegata,pd,sa);
						openspcoopstate.releaseResource();
						esito.setEsitoInvocazione(true); 
						esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,
								"gestoreRiscontri.registraRiscontroDaRicevere");
						return esito;
					}
				}else{
					msgDiag.logPersonalizzato("consegnaAffidabile.profiloNonOneway");
				}
			}



			Utilities.printFreeMemory("Imbustamento - Invio messaggio al modulo di InoltroBuste...");



			/* ------------  Spedizione InoltroBusteMessage al modulo InoltroBuste   ------------- */

			// Creazione InoltroBusteMessage
			// (infoBusta da spedire)
			msgDiag.mediumDebug("Invio messaggio al modulo di InoltroBuste...");
			InoltroBusteMessage inoltroMSG = new InoltroBusteMessage();
			inoltroMSG.setRichiestaDelegata(richiestaDelegata);
			inoltroMSG.setBusta(busta);
			inoltroMSG.setOneWayVersione11(oneWayVersione11);
			inoltroMSG.setImplementazionePdDSoggettoMittente(imbustamentoMsg.getImplementazionePdDSoggettoMittente());
			inoltroMSG.setImplementazionePdDSoggettoDestinatario(imbustamentoMsg.getImplementazionePdDSoggettoDestinatario());
			inoltroMSG.setPddContext(pddContext);
			
			msgRequest = new GestoreMessaggi(openspcoopstate, true, idMessageRequest,Costanti.OUTBOX,msgDiag,pddContext);

			// send. Se la porta applicativa funziona in modalita' stateful v11 il messaggio JMS viene arricchito con
			//       un oggetto di tipo OpenSPCoopStateless

			if ( !stateless  ) {
				if ( oneWayVersione11 ) {
					OpenSPCoopStateless statelessSerializzabile = ((OpenSPCoopStateless) openspcoopstate).rendiSerializzabile();
					inoltroMSG.setOpenspcoopstate(statelessSerializzabile);
					msgRequest.setOneWayVersione11(true);
				}
		
				try{
					ejbUtils.getNodeSender(this.propertiesReader, this.log).send(inoltroMSG, InoltroBuste.ID_MODULO, msgDiag, 
							identitaPdD,Imbustamento.ID_MODULO, idMessageRequest, msgRequest);
				} catch (Exception e) {
					this.log.error("Spedizione->InoltroBuste non riuscita",e);
					msgDiag.logErroreGenerico(e,"GenericLib.nodeSender.send(InoltroBuste)");
					OpenSPCoop2Message responseMessageError = 
							generatoreErrore.build(pddContext,IntegrationFunctionError.INTERNAL_REQUEST_ERROR,
									ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
										get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_512_SEND),e,	null);
					ejbUtils.sendRispostaApplicativaErrore(responseMessageError,richiestaDelegata,pd,sa);
					openspcoopstate.releaseResource(); 
					esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,
							"Spedizione->InoltroBuste non riuscita");
					esito.setEsitoInvocazione(true); 
					return esito;
				}
			}


			Utilities.printFreeMemory("Imbustamento - Gestione Risposta");

			/* ------------  Gestione Risposta  ------------- */
			// se il profilo e' oneWay, AsincronoSimmetrico o AsincronoAsimmetricoRichiesta  viene generata una risposta SOAP 'OK'
			// inizializzo richiesta
			if(  generazioneMsgOK ) {
				
				pddContext.addObject(org.openspcoop2.core.constants.Costanti.DATA_PRESA_IN_CARICO, 
						DateUtils.getSimpleDateFormatMs().format(DateManager.getDate()));
				
				msgDiag.mediumDebug("Invio messaggio 'OK' al modulo di RicezioneContenutiApplicativi...");
				if(protocolManager.isHttpEmptyResponseOneWay())
					msgOK = ejbUtils.sendRispostaApplicativaOK(MessageUtilities.buildEmptyMessage(OpenSPCoop2MessageFactory.getDefaultMessageFactory(),
							requestInfo.getIntegrationRequestMessageType(), MessageRole.RESPONSE),richiestaDelegata,pd,sa);
				else
					msgOK = ejbUtils.sendRispostaApplicativaOK(ejbUtils.buildOpenSPCoopOK(requestInfo.getIntegrationRequestMessageType(), idMessageRequest),richiestaDelegata,pd,sa);
			}















			/* ---------- Gestione Transazione Modulo ---------------- */

			// Aggiorno proprietario Messaggio e Scenario di cooperazione
			msgDiag.mediumDebug("Aggiorno proprietario messaggio...");
			msgRequest.aggiornaProprietarioMessaggio(org.openspcoop2.pdd.mdb.InoltroBuste.ID_MODULO);

			// Commit JDBC

			msgDiag.mediumDebug("Commit delle operazioni per la gestione della richiesta...");
			openspcoopstate.commit();

			// Aggiornamento cache messaggi
			if(msgRequest!=null)
				msgRequest.addMessaggiIntoCache_readFromTable(Imbustamento.ID_MODULO, "richiesta");
			if(msgOK!=null)
				msgOK.addMessaggiIntoCache_readFromTable(Imbustamento.ID_MODULO, "messaggio OK");

			//	Aggiornamento cache proprietario messaggio
			if(msgRequest!=null)
				msgRequest.addProprietariIntoCache_readFromTable(Imbustamento.ID_MODULO, "richiesta",null,false);
			if(msgOK!=null)
				msgOK.addProprietariIntoCache_readFromTable(Imbustamento.ID_MODULO, "messaggio OK",idMessageRequest,false);


			// Rilascio connessione al DB
			msgDiag.mediumDebug("Rilascio connessione al database...");
			openspcoopstate.releaseResource();

			msgDiag.mediumDebug("Lavoro Terminato.");
			esito.setEsitoInvocazione(true);
			esito.setStatoInvocazione(EsitoLib.OK,null);
			if (stateless) ((OpenSPCoopState)openspcoopstate).setMessageLib(inoltroMSG);
			return esito; 


		}catch(Throwable e){
			this.log.error("ErroreGenerale",e);
			msgDiag.logErroreGenerico(e, "Generale");
			
			OpenSPCoop2Message responseMessageError = 
					generatoreErrore.build(pddContext,IntegrationFunctionError.INTERNAL_REQUEST_ERROR,
							ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.getErroreIntegrazione(),e,null);
			try{
				ejbUtils.sendRispostaApplicativaErrore(responseMessageError,richiestaDelegata,pd,sa);
				esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,"ErroreGenerale");
				esito.setEsitoInvocazione(true);
			}catch(Exception er){
				msgDiag.logErroreGenerico(er,"ejbUtils.sendErroreGenerale");
				ejbUtils.rollbackMessage("Spedizione Errore al Mittente durante una richiesta non riuscita", esito);
				esito.setEsitoInvocazione(false);
				esito.setStatoInvocazioneErroreNonGestito(e);
			}	 
			openspcoopstate.releaseResource();
			return esito;
		}

	}
}
