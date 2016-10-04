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
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.core.config.driver.DriverConfigurazioneNotFound;
import org.openspcoop2.core.constants.TipoPdD;
import org.openspcoop2.core.eccezione.details.DettaglioEccezione;
import org.openspcoop2.core.eccezione.details.utils.XMLUtils;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.ParseException;
import org.openspcoop2.message.SOAPVersion;
import org.openspcoop2.message.SoapUtils;
import org.openspcoop2.pdd.config.ClassNameProperties;
import org.openspcoop2.pdd.config.ConfigurazionePdDManager;
import org.openspcoop2.pdd.config.RichiestaDelegata;
import org.openspcoop2.pdd.core.CostantiPdD;
import org.openspcoop2.pdd.core.EJBUtils;
import org.openspcoop2.pdd.core.GestoreMessaggi;
import org.openspcoop2.pdd.core.PdDContext;
import org.openspcoop2.pdd.core.state.IOpenSPCoopState;
import org.openspcoop2.pdd.core.state.OpenSPCoopState;
import org.openspcoop2.pdd.core.state.OpenSPCoopStateException;
import org.openspcoop2.pdd.core.state.OpenSPCoopStateless;
import org.openspcoop2.pdd.logger.MsgDiagnosticiProperties;
import org.openspcoop2.pdd.logger.MsgDiagnostico;
import org.openspcoop2.pdd.timers.TimerGestoreMessaggi;
import org.openspcoop2.protocol.engine.builder.ErroreApplicativoBuilder;
import org.openspcoop2.protocol.engine.constants.Costanti;
import org.openspcoop2.protocol.engine.driver.FiltroDuplicati;
import org.openspcoop2.protocol.engine.driver.History;
import org.openspcoop2.protocol.engine.driver.IFiltroDuplicati;
import org.openspcoop2.protocol.engine.driver.ProfiloDiCollaborazione;
import org.openspcoop2.protocol.engine.driver.RepositoryBuste;
import org.openspcoop2.protocol.registry.RegistroServiziManager;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.Eccezione;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.Riscontro;
import org.openspcoop2.protocol.sdk.builder.ProprietaErroreApplicativo;
import org.openspcoop2.protocol.sdk.config.IProtocolConfiguration;
import org.openspcoop2.protocol.sdk.config.IProtocolVersionManager;
import org.openspcoop2.protocol.sdk.config.ITraduttore;
import org.openspcoop2.protocol.sdk.constants.CodiceErroreCooperazione;
import org.openspcoop2.protocol.sdk.constants.CodiceErroreIntegrazione;
import org.openspcoop2.protocol.sdk.constants.ErroreIntegrazione;
import org.openspcoop2.protocol.sdk.constants.ErroriIntegrazione;
import org.openspcoop2.protocol.sdk.constants.FunzionalitaProtocollo;
import org.openspcoop2.protocol.sdk.constants.Inoltro;
import org.openspcoop2.protocol.sdk.constants.ErroriCooperazione;
import org.openspcoop2.protocol.sdk.constants.ErroreCooperazione;
import org.openspcoop2.protocol.sdk.constants.RuoloBusta;

/**
 * Contiene la libreria Sbustamento
 *
 * @author Poli Andrea (apoli@link.it)
 * @author Tronci Fabio (tronci@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SbustamentoRisposte extends GenericLib {

	public static final String ID_MODULO = "SbustamentoRisposte";

	/** XMLBuilder */
	private ErroreApplicativoBuilder erroreApplicativoBuilder;


	public SbustamentoRisposte(Logger log) throws GenericLibException {
		super(SbustamentoRisposte.ID_MODULO, log);
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

		SbustamentoRisposteMessage sbustamentoRisposteMsg = (SbustamentoRisposteMessage) openspcoopstate.getMessageLib();
		
		/* Regupero eventuali errori di parsing. */
		ParseException parseException = null;
		if(openspcoopstate instanceof OpenSPCoopStateless) {
			OpenSPCoop2Message msgRisposta = ((OpenSPCoopStateless) openspcoopstate).getRispostaMsg();
			if(msgRisposta!=null){
				parseException = msgRisposta.getParseException();
			}
		}
		
		/* PddContext */
		PdDContext pddContext = sbustamentoRisposteMsg.getPddContext();
		String idTransazione = PdDContext.getValue(org.openspcoop2.core.constants.Costanti.CLUSTER_ID, pddContext);
		
		/* Protocol Factory */
		IProtocolFactory protocolFactory = null;
		IProtocolVersionManager protocolManager = null;
		ITraduttore traduttore = null;
		try{
			protocolFactory = this.protocolFactoryManager.getProtocolFactoryByName((String) pddContext.getObject(org.openspcoop2.core.constants.Costanti.PROTOCOLLO));
			protocolManager = protocolFactory.createProtocolVersionManager(sbustamentoRisposteMsg.getRichiestaDelegata().getProfiloGestione());
			traduttore = protocolFactory.createTraduttore();
		}catch(Exception e){
			msgDiag.logErroreGenerico(e, "ProtocolFactory.instanziazione"); 
			openspcoopstate.releaseResource();
			esito.setEsitoInvocazione(false); 
			esito.setStatoInvocazioneErroreNonGestito(e);
			return esito;
		}
		
		SOAPVersion versioneSoap = (SOAPVersion) pddContext.getObject(org.openspcoop2.core.constants.Costanti.SOAP_VERSION);
		
		msgDiag.setPddContext(pddContext, protocolFactory);
		
		/* Busta e ID e tipo di implementazione PdD con cui interoperare */
		Busta bustaRisposta = sbustamentoRisposteMsg.getBusta();
		String idResponse = bustaRisposta.getID();
		String implementazionePdDDestinatario = sbustamentoRisposteMsg.getImplementazionePdDSoggettoDestinatario();
		
		/* Processamento informazioni */
		RichiestaDelegata richiestaDelegata = sbustamentoRisposteMsg.getRichiestaDelegata();
		richiestaDelegata.setProfiloCollaborazione(bustaRisposta.getProfiloDiCollaborazione(),bustaRisposta.getProfiloDiCollaborazioneValue()); // update value
		java.util.Vector<Eccezione> errors = sbustamentoRisposteMsg.getErrors();
		boolean isMessaggioErroreProtocollo = sbustamentoRisposteMsg.isMessaggioErroreProtocollo();
		boolean bustaDiServizio = sbustamentoRisposteMsg.getIsBustaDiServizio();
		TipoPdD tipoPdD = TipoPdD.DELEGATA;
		IDSoggetto identitaPdD = sbustamentoRisposteMsg.getRichiestaDelegata().getDominio();
		
		msgDiag.setDominio(identitaPdD);  // imposto anche il dominio nel msgDiag
		msgDiag.setPrefixMsgPersonalizzati(MsgDiagnosticiProperties.MSG_DIAG_SBUSTAMENTO_RISPOSTE);
		msgDiag.addKeywords(richiestaDelegata.getSoggettoFruitore(),richiestaDelegata.getIdServizio(),null);
		msgDiag.addKeywords(bustaRisposta, false);
		msgDiag.addKeyword(CostantiPdD.KEY_SA_FRUITORE, richiestaDelegata.getServizioApplicativo());
		msgDiag.setIdCorrelazioneApplicativa(richiestaDelegata.getIdCorrelazioneApplicativa());
		msgDiag.setIdCorrelazioneRisposta(richiestaDelegata.getIdCorrelazioneApplicativaRisposta());
		msgDiag.addKeyword(CostantiPdD.KEY_ID_CORRELAZIONE_APPLICATIVA, richiestaDelegata.getIdCorrelazioneApplicativa());
		
		String profiloGestione = richiestaDelegata.getProfiloGestione();
		msgDiag.mediumDebug("Profilo di gestione ["+SbustamentoRisposte.ID_MODULO+"] della busta: "+profiloGestione);
		
		try{
			this.erroreApplicativoBuilder = new ErroreApplicativoBuilder(this.log, protocolFactory, 
					identitaPdD, richiestaDelegata.getSoggettoFruitore(), richiestaDelegata.getIdServizio(), 
					this.idModulo, richiestaDelegata.getFault(),versioneSoap,
					TipoPdD.DELEGATA,richiestaDelegata.getServizioApplicativo());
		}catch(Exception e){
			msgDiag.logErroreGenerico(e, "ErroreApplicativoBuilder.instanziazione"); 
			openspcoopstate.releaseResource();
			esito.setEsitoInvocazione(false); 
			esito.setStatoInvocazioneErroreNonGestito(e);
			return esito;
		}
		

		/* ------------------ Inizializzo stato OpenSPCoop  --------------- */
		msgDiag.mediumDebug("Inizializzo stato per la gestione della richiesta...");
		openspcoopstate.initResource(identitaPdD, SbustamentoRisposte.ID_MODULO,idTransazione);
		registroServiziManager.updateState(openspcoopstate.getStatoRichiesta(),openspcoopstate.getStatoRisposta());
		configurazionePdDManager.updateState(openspcoopstate.getStatoRichiesta(),openspcoopstate.getStatoRisposta());
		msgDiag.updateState(openspcoopstate.getStatoRichiesta(),openspcoopstate.getStatoRisposta());

		/* ----------------- ID RICHIESTA ---------------------------------------------- */
		String idMessageRequest = openspcoopstate.getIDMessaggioSessione();

		// Aggiornamento Informazioni messaggio diagnostico
		msgDiag.setIdMessaggioRichiesta(idMessageRequest);
		msgDiag.setIdMessaggioRisposta(idResponse);
		msgDiag.addKeyword(CostantiPdD.KEY_ID_MESSAGGIO_RICHIESTA, idMessageRequest);
		msgDiag.setDelegata(true);
		msgDiag.setPorta(richiestaDelegata.getLocationPD());
		msgDiag.setFruitore(richiestaDelegata.getSoggettoFruitore());
		msgDiag.setServizio(richiestaDelegata.getIdServizio());
		msgDiag.setServizioApplicativo(richiestaDelegata.getServizioApplicativo());
		msgDiag.setIdCorrelazioneApplicativa(richiestaDelegata.getIdCorrelazioneApplicativa());
		msgDiag.setIdCorrelazioneRisposta(richiestaDelegata.getIdCorrelazioneApplicativaRisposta());


		/* ------------------ Inizializzazione Contesto di gestione  --------------- */
		msgDiag.mediumDebug("Inizializzo contesto per la gestione...");

		//	Gestore della risposta
		GestoreMessaggi msgResponse = null;

		// Gestore funzionalita
		ProfiloDiCollaborazione profiloCollaborazione = null;
		try{
			profiloCollaborazione = new ProfiloDiCollaborazione(openspcoopstate.getStatoRisposta(),protocolFactory);
		}catch(Exception e){
			msgDiag.logErroreGenerico(e, "ProfiloDiCollaborazione.new");
			openspcoopstate.releaseResource();
			esito.setEsitoInvocazione(false); 
			esito.setStatoInvocazioneErroreNonGestito(e);
			return esito;
		}
		History historyBuste = new History(openspcoopstate.getStatoRisposta());
		RepositoryBuste repositoryBuste = new RepositoryBuste(openspcoopstate.getStatoRisposta(), false, protocolFactory);
		String ricevutaAsincrona = null;

		// EJBUtils (per eventuali errori)
		EJBUtils ejbUtils = null;
		try{
			ejbUtils = new EJBUtils(identitaPdD,tipoPdD,SbustamentoRisposte.ID_MODULO,idMessageRequest,
					idResponse,Costanti.INBOX, openspcoopstate, msgDiag,false,
					sbustamentoRisposteMsg.getImplementazionePdDSoggettoMittente(),
					sbustamentoRisposteMsg.getImplementazionePdDSoggettoDestinatario(),
					profiloGestione,pddContext);
			ejbUtils.setSpedizioneMsgIngresso(sbustamentoRisposteMsg.getSpedizioneMsgIngresso());
			ejbUtils.setRicezioneMsgRisposta(sbustamentoRisposteMsg.getRicezioneMsgRisposta());
			ejbUtils.setScenarioCooperazione(richiestaDelegata.getScenario());
		}catch(Exception e){
			msgDiag.logErroreGenerico(e, "EJBUtils.new");
			openspcoopstate.releaseResource();
			esito.setEsitoInvocazione(false); 
			esito.setStatoInvocazioneErroreNonGestito(e);
			return esito;
		}
		
		// Oneway versione 11
		boolean oneWayVersione11 = sbustamentoRisposteMsg.isOneWayVersione11();
		ejbUtils.setOneWayVersione11(oneWayVersione11);

		
		/* ----------- Lettura PortaDelegata e Servizio Applicativo ------------- */
		PortaDelegata pd = null;
		ServizioApplicativo sa = null;
		try{
			pd = configurazionePdDManager.getPortaDelegata(richiestaDelegata.getIdPortaDelegata());
		}catch(Exception e){
			msgDiag.logErroreGenerico(e,"getPortaDelegata()");
			openspcoopstate.releaseResource();
			esito.setEsitoInvocazione(false);	
			esito.setStatoInvocazioneErroreNonGestito(e);
			return esito;
		}
		try{
			sa = configurazionePdDManager.getServizioApplicativo(richiestaDelegata.getIdPortaDelegata(), richiestaDelegata.getServizioApplicativo());
		}catch(Exception e){
			if( !(e instanceof DriverConfigurazioneNotFound) || 
					!(CostantiPdD.SERVIZIO_APPLICATIVO_ANONIMO.equals(richiestaDelegata.getServizioApplicativo())) ){
				msgDiag.logErroreGenerico(e,"getServizioApplicativo()");
				openspcoopstate.releaseResource();
				esito.setEsitoInvocazione(false);	
				esito.setStatoInvocazioneErroreNonGestito(e);
				return esito;
			}
		}

		
		
		//	Modalita' gestione risposta Applicativa (No function Router active)
		// -a se il profilo e' sincrono
		// -b se il profilo e' asincrono e ricevutaAsincrona e' abilitata.
		// -c se il profilo e' asincrono asimmetrico di richiesta stato e ricevuta disabilitata
		// -d se il profilo e' oneway e la modalita di trasmissione e' sincrona.
		boolean sendRispostaApplicativa = false;
		boolean statelessAsincrono = false;
		// a)
		if(Costanti.SCENARIO_SINCRONO_INVOCAZIONE_SERVIZIO.equals(richiestaDelegata.getScenario()) ){
			sendRispostaApplicativa = true;
		}
		// c) [e b) per la richiestaStato AsincronaAsimmetrica]
		else if(Costanti.SCENARIO_ASINCRONO_ASIMMETRICO_POLLING.equals(richiestaDelegata.getScenario())){
			try{
				statelessAsincrono = configurazionePdDManager.isModalitaStateless(pd, bustaRisposta.getProfiloDiCollaborazione());
			}catch(Exception e){
				msgDiag.logErroreGenerico(e,"AsincronoAsimmetricoPolling.isModalitaStateless(pd)");
				openspcoopstate.releaseResource();
				esito.setEsitoInvocazione(false);	
				esito.setStatoInvocazioneErroreNonGestito(e);
				return esito;
			}
			sendRispostaApplicativa = true;
		}
		// b)
		else if (Costanti.SCENARIO_ASINCRONO_ASIMMETRICO_INVOCAZIONE_SERVIZIO.equals(richiestaDelegata.getScenario()) ||
				Costanti.SCENARIO_ASINCRONO_SIMMETRICO_INVOCAZIONE_SERVIZIO.equals(richiestaDelegata.getScenario()) ||
				Costanti.SCENARIO_ASINCRONO_SIMMETRICO_CONSEGNA_RISPOSTA.equals(richiestaDelegata.getScenario()) ){
			try{
				statelessAsincrono = configurazionePdDManager.isModalitaStateless(pd, bustaRisposta.getProfiloDiCollaborazione());
				sendRispostaApplicativa =  statelessAsincrono || richiestaDelegata.isRicevutaAsincrona();
			}catch(Exception e){
				msgDiag.logErroreGenerico(e,"Asincrono.isModalitaStateless(pd)");
				openspcoopstate.releaseResource();
				esito.setEsitoInvocazione(false);	
				esito.setStatoInvocazioneErroreNonGestito(e);
				return esito;
			}
		}
		// d)
		else if(Costanti.SCENARIO_ONEWAY_INVOCAZIONE_SERVIZIO.equals(richiestaDelegata.getScenario())){
			try{
				sendRispostaApplicativa = (configurazionePdDManager.isModalitaStateless(pd, org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.ONEWAY));
			}catch(Exception e){
				msgDiag.logErroreGenerico(e,"OnewayInvocazioneServizio.isModalitaStateless(pd)");
				openspcoopstate.releaseResource();
				esito.setEsitoInvocazione(false);	
				esito.setStatoInvocazioneErroreNonGestito(e);
				return esito;
			}
		}

		//	rollback RiferimentoMessaggio
		boolean rollbackRiferimentoMessaggio = false;
		if(sendRispostaApplicativa){
			if(Costanti.SCENARIO_SINCRONO_INVOCAZIONE_SERVIZIO.equals(richiestaDelegata.getScenario()) ){
				rollbackRiferimentoMessaggio = true;
			}
			// Profilo Asincrono con gestione ricevuta applicativa (siamo in sendRispostaApplicativa)
			else if(Costanti.SCENARIO_ASINCRONO_ASIMMETRICO_INVOCAZIONE_SERVIZIO.equals(richiestaDelegata.getScenario()) ||
					Costanti.SCENARIO_ASINCRONO_SIMMETRICO_INVOCAZIONE_SERVIZIO.equals(richiestaDelegata.getScenario()) ){
				rollbackRiferimentoMessaggio = true;
			}
		}

		//	ProprietaErroreApplicativo
		ProprietaErroreApplicativo proprietaErroreAppl = richiestaDelegata.getFault();
		proprietaErroreAppl.setIdModulo(SbustamentoRisposte.ID_MODULO);

		// Ruolo Busta ricevuta
		RuoloBusta ruoloBustaRicevuta = sbustamentoRisposteMsg.getRuoloBustaRicevuta();









		// Punto di inizio per la transazione.
		try{

			boolean ricezioneRiscontri = false;
			switch (protocolManager.getConsegnaAffidabile(bustaRisposta.getProfiloDiCollaborazione())) {
			case ABILITATA:
				ricezioneRiscontri = true;
				break;
			case DISABILITATA:
				ricezioneRiscontri = false;
				break;
			default:
				ricezioneRiscontri = this.propertiesReader.isGestioneRiscontri(implementazionePdDDestinatario);
				break;
			}
			
			boolean imbustamentoFiltroDuplicatiAbilitato = false;
			switch (protocolManager.getFiltroDuplicati(bustaRisposta.getProfiloDiCollaborazione())) {
			case ABILITATA:
				imbustamentoFiltroDuplicatiAbilitato = true;
				break;
			case DISABILITATA:
				imbustamentoFiltroDuplicatiAbilitato = false;
				break;
			default:
				imbustamentoFiltroDuplicatiAbilitato = (Inoltro.SENZA_DUPLICATI.equals(bustaRisposta.getInoltro())) ||
				(this.propertiesReader.isCheckFromRegistroFiltroDuplicatiAbilitato(implementazionePdDDestinatario) && sbustamentoRisposteMsg.isFiltroDuplicati());
				break;
			}
			
			// ------------- Controllo funzionalita di protocollo richieste siano compatibili con il protocollo -----------------------------
			try{
				IProtocolConfiguration protocolConfiguration = protocolFactory.createProtocolConfiguration();
				if(imbustamentoFiltroDuplicatiAbilitato){
					if(protocolConfiguration.isSupportato(FunzionalitaProtocollo.FILTRO_DUPLICATI)==false){
						throw new Exception(FunzionalitaProtocollo.FILTRO_DUPLICATI.getEngineValue());
					}
				}				
			}catch(Exception e){	
				msgDiag.addKeyword(CostantiPdD.KEY_ERRORE_PROCESSAMENTO , e.getMessage() );
				msgDiag.logPersonalizzato("protocolli.funzionalita.unsupported");
				if(sendRispostaApplicativa){
					OpenSPCoop2Message responseMessageError =
						this.erroreApplicativoBuilder.toMessage(ErroriIntegrazione.ERRORE_439_FUNZIONALITA_NOT_SUPPORTED_BY_PROTOCOL.
								getErrore439_FunzionalitaNotSupportedByProtocol(e.getMessage(), protocolFactory),e,
								parseException);
					ejbUtils.sendRispostaApplicativaErrore(responseMessageError,richiestaDelegata,rollbackRiferimentoMessaggio,pd,sa);
					esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,
							msgDiag.getMessaggio_replaceKeywords("protocolli.funzionalita.unsupported"));
				}else{
					ejbUtils.releaseInboxMessage(false); 
					esito.setStatoInvocazioneErroreNonGestito(e);
				}
				openspcoopstate.releaseResource();
				esito.setEsitoInvocazione(true);
				return esito;
			}
			
			
			
			
			
			
			
			
			/*   -------------- Validazione: Gestione messaggio Errore Protocollo -----------------  */
			if(isMessaggioErroreProtocollo){

				msgDiag.mediumDebug("Gestione messaggio errore protocollo...");
				boolean msgErroreProtocolloValido = true;

				if(configurazionePdDManager.getTipoValidazione(implementazionePdDDestinatario).equals(CostantiConfigurazione.STATO_CON_WARNING_DISABILITATO) == false){

					if(errors.size() != 0){

						// BUSTA ERRORE MALFORMATA :
						// Il seguente codice viene eseguito solo se la modalita' di validazione
						// della busta e' "Warning Only" o "active"

						// Registrazione eccezioni riscontrate dalla validazione della busta
						StringBuffer eccBuffer = new StringBuffer();
						for(int k = 0; k < errors.size() ; k++){
							Eccezione er = errors.get(k);
							if(k>0)
								eccBuffer.append("\n");
							eccBuffer.append(er.toString(protocolFactory));
						}
						msgDiag.addKeyword(CostantiPdD.KEY_NUMERO_ECCEZIONI, errors.size()+"");
						msgDiag.addKeyword(CostantiPdD.KEY_ECCEZIONI, eccBuffer.toString());
						msgDiag.logPersonalizzato("validazioneBustaErrore.listaEccezioniMalformata");

						// Il lavoro deve essere terminato solo in caso di Modalita Active
						if(configurazionePdDManager.getTipoValidazione(implementazionePdDDestinatario).equals(CostantiConfigurazione.STATO_CON_WARNING_ABILITATO) == true){
							msgErroreProtocolloValido=false;
						}
					}
				} // -- end if not active

				if(msgErroreProtocolloValido){
					//	 Registrazione eccezioni portate nella busta
					if(bustaRisposta.sizeListaEccezioni()>0){
						msgDiag.addKeyword(CostantiPdD.KEY_ECCEZIONI, bustaRisposta.toStringListaEccezioni(protocolFactory));
						msgDiag.addKeyword(CostantiPdD.KEY_NUMERO_ECCEZIONI, bustaRisposta.sizeListaEccezioni()+"");
						msgDiag.logPersonalizzato("ricezioneBustaErrore");
					}
				}

				DettaglioEccezione dettaglioEccezione = sbustamentoRisposteMsg.getDettaglioEccezione();
				if(dettaglioEccezione!=null){
					msgDiag.addKeyword(CostantiPdD.KEY_OPENSPCOOP2_DETAILS, XMLUtils.toString(dettaglioEccezione));
					msgDiag.logPersonalizzato("ricezioneBustaErroreDetails");
				}
				
				// Gestione ERRORE
				msgDiag.mediumDebug("Invio eventuale messaggio di errore al servizio applicativo (gestione errore)...");
				if(sendRispostaApplicativa){
					Eccezione eccezioneDaInviareServizioApplicativo = 
							Eccezione.getEccezioneValidazione(ErroriCooperazione.ERRORE_GENERICO_PROTOCOLLO_NON_CORRETTO.getErroreCooperazione(), protocolFactory);
					ErroreIntegrazione erroreIntegrazioneDaInviareServizioApplicativo = null;
					if(msgErroreProtocolloValido){
						if(bustaRisposta.sizeListaEccezioni()>1){
							StringBuffer bfDescrizione = new StringBuffer();
							for(int k=0; k<bustaRisposta.sizeListaEccezioni();k++){
								Eccezione eccListaEccezioni = bustaRisposta.getEccezione(k);
								if(eccListaEccezioni.getDescrizione(protocolFactory)!=null)
									bfDescrizione.append("["+traduttore.toString(eccListaEccezioni.getCodiceEccezione(),eccListaEccezioni.getSubCodiceEccezione())+"] "+eccListaEccezioni.getDescrizione(protocolFactory)+"\n");
							}
							if(bfDescrizione.length()>0)
								eccezioneDaInviareServizioApplicativo.setDescrizione(bfDescrizione.toString());
						}else{
							if(bustaRisposta.sizeListaEccezioni()==1){
								eccezioneDaInviareServizioApplicativo = bustaRisposta.getEccezione(0);
							}
							else{
								if(dettaglioEccezione!=null && dettaglioEccezione.getEccezioni()!=null && dettaglioEccezione.getEccezioni().sizeEccezioneList()>0){
									org.openspcoop2.core.eccezione.details.Eccezione e = dettaglioEccezione.getEccezioni().getEccezione(0);
									if(org.openspcoop2.core.eccezione.details.constants.Costanti.TIPO_ECCEZIONE_PROTOCOLLO.equals(e.getTipo())){
										ErroreCooperazione msgErroreCooperazione =
												new ErroreCooperazione(e.getDescrizione(), traduttore.toCodiceErroreCooperazione(e.getCodice()));
										eccezioneDaInviareServizioApplicativo = new Eccezione(msgErroreCooperazione, false, SbustamentoRisposte.ID_MODULO ,protocolFactory);
									}else{
//										erroreIntegrazioneDaInviareServizioApplicativo = 
//												new ErroreIntegrazione(e.getDescrizione(), traduttore.toCodiceErroreIntegrazione(e.getCodiceAAA(), propertiesReader.getProprietaGestioneErrorePD(protocolManager).getFaultPrefixCode()));
										// Se e' arrivato un details di errore di integrazione, comunque genero una busta di errore di processamento, visto che di fatto ho ricevuto questa.
										eccezioneDaInviareServizioApplicativo = Eccezione.
												getEccezioneProcessamento(ErroriCooperazione.ERRORE_GENERICO_PROCESSAMENTO_MESSAGGIO.getErroreProcessamento(e.getDescrizione()), protocolFactory);
									}
								}else{
									eccezioneDaInviareServizioApplicativo = new Eccezione(ErroriCooperazione.ERRORE_GENERICO_PROCESSAMENTO_MESSAGGIO.getErroreCooperazione(), false, SbustamentoRisposte.ID_MODULO, protocolFactory);
								}
							}
						}
					}
					else{
						if(errors.size()>1){
							StringBuffer bfDescrizione = new StringBuffer();
							for(int k=0; k<errors.size();k++){
								Eccezione error = errors.get(k);
								if(error.getDescrizione(protocolFactory)!=null)
									bfDescrizione.append("["+traduttore.toString(error.getCodiceEccezione(),error.getSubCodiceEccezione())+"] "+error.getDescrizione(protocolFactory)+"\n");
							}
							if(bfDescrizione.length()>0)
								eccezioneDaInviareServizioApplicativo.setDescrizione(bfDescrizione.toString());
						}else{
							eccezioneDaInviareServizioApplicativo = errors.get(0);
						}
					}
					OpenSPCoop2Message responseMessageError = null;
					if(eccezioneDaInviareServizioApplicativo!=null){
						responseMessageError = this.erroreApplicativoBuilder.toMessage(eccezioneDaInviareServizioApplicativo,
								richiestaDelegata.getIdServizio().getSoggettoErogatore(),dettaglioEccezione,
								parseException);
					}else{
						responseMessageError = this.erroreApplicativoBuilder.toMessage(erroreIntegrazioneDaInviareServizioApplicativo,null,
								parseException);
					}
					ejbUtils.sendRispostaApplicativaErrore(responseMessageError,richiestaDelegata,rollbackRiferimentoMessaggio,pd,sa);
					esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,"BustaErrore");
				}else{
					ejbUtils.releaseInboxMessage(false); 
					esito.setStatoInvocazione(EsitoLib.ERRORE_NON_GESTITO,"BustaErrore");
				}
				msgDiag.mediumDebug("Rilascio connessione...");
				openspcoopstate.releaseResource();
				esito.setEsitoInvocazione(true); 
				return esito; // Non devo utilizzare una busta malformata
			}








			/*   -------------- Validazione: Gestione messaggio di protocollo -----------------  */
			if(isMessaggioErroreProtocollo == false){
				if(configurazionePdDManager.getTipoValidazione(implementazionePdDDestinatario).equals(CostantiConfigurazione.STATO_CON_WARNING_DISABILITATO) == false){

					// Redirigo Errori presenti nella Busta
					boolean validazioneConErrori = (errors.size() > 0);
					if( protocolManager.isIgnoraEccezioniLivelloNonGrave() || (this.propertiesReader.ignoraEccezioniNonGravi_Validazione())){
						validazioneConErrori = Busta.containsEccezioniGravi(errors);
					}

					if(validazioneConErrori){

						msgDiag.mediumDebug("Gestione messaggio di protocollo che non ha superato la validazione...");

						// Registrazione eccezioni riscontrate dalla validazione della busta
						boolean mittenteRegistrato = true;
						StringBuffer eccBuffer = new StringBuffer();
						java.util.Vector<Eccezione> errorsClone =  new java.util.Vector<Eccezione>();
						for(int k = 0; k < errors.size() ; k++){
							Eccezione er = errors.get(k);
							errorsClone.add(er);
							if(k>0)
								eccBuffer.append("\n");
							eccBuffer.append(er.toString(protocolFactory));
							if(CodiceErroreCooperazione.isEccezioneMittente(er.getCodiceEccezione())){
								mittenteRegistrato = false;
							}
						}

						// Registrazione degli errori effettuata solo in caso la modalita di Validazione
						// della busta sia "Warning Only" o "active"
						msgDiag.addKeyword(CostantiPdD.KEY_ECCEZIONI, eccBuffer.toString());
						msgDiag.addKeyword(CostantiPdD.KEY_NUMERO_ECCEZIONI, errors.size()+"");
						msgDiag.logPersonalizzato("validazioneBusta.bustaNonCorretta");

						// Spedizione BustaErrore contenente gli errori riscontrati, solo se
						// la modalita di validazione della busta e' "active"
						// e il Mittente e' comunque conosciuto.
						if( configurazionePdDManager.getTipoValidazione(implementazionePdDDestinatario).equals(CostantiConfigurazione.STATO_CON_WARNING_ABILITATO) == true) {

							// Spedisco un Errore ad un mittente conosciuto...
							if(mittenteRegistrato){
								// SPEDIZIONE BUSTA ERRORE AL MITTENTE DELLA BUSTA :
								msgDiag.mediumDebug("Invio segnalazione di errore ...");
								ejbUtils.sendAsRispostaBustaErrore_inoltroSegnalazioneErrore(bustaRisposta,errorsClone);

							}

							// GESTIONE INTERNA DI QUESTA BUSTA ERRORE :
							// spedisco prima eccezione riscontrata al SIL.
							msgDiag.mediumDebug("Invio eventuale messaggio di errore al servizio applicativo (gestione errore)...");
							if(sendRispostaApplicativa){
								Eccezione eccezioneDaInviareServizioApplicativo = null;
								if(errors.size()>1){
									eccezioneDaInviareServizioApplicativo = 
											Eccezione.getEccezioneValidazione(ErroriCooperazione.ERRORE_GENERICO_PROTOCOLLO_NON_CORRETTO.getErroreCooperazione(), protocolFactory);
									StringBuffer bfDescrizione = new StringBuffer();
									for(int k=0; k<errors.size();k++){
										Eccezione error = errors.get(k);
										if(error.getDescrizione(protocolFactory)!=null)
											bfDescrizione.append("["+traduttore.toString(error.getCodiceEccezione(),error.getSubCodiceEccezione())+"] "+error.getDescrizione(protocolFactory)+"\n");
									}
									if(bfDescrizione.length()>0)
										eccezioneDaInviareServizioApplicativo.setDescrizione(bfDescrizione.toString());
								}else{
									eccezioneDaInviareServizioApplicativo = errors.get(0);
								}

								// profilo sincrono asincronoPolling, aspetta una risposta
								OpenSPCoop2Message responseMessageError =
									this.erroreApplicativoBuilder.toMessage(eccezioneDaInviareServizioApplicativo,
											richiestaDelegata.getSoggettoFruitore(),null,
											parseException);
								ejbUtils.sendRispostaApplicativaErrore(responseMessageError,richiestaDelegata,rollbackRiferimentoMessaggio,pd,sa);
								esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,
										msgDiag.getMessaggio_replaceKeywords("validazioneBusta.bustaNonCorretta"));
							}else{
								// profilo OneWay, non aspetta una risposta
								ejbUtils.releaseInboxMessage(false); //TODO CHECKME
								esito.setStatoInvocazione(EsitoLib.ERRORE_NON_GESTITO,
										msgDiag.getMessaggio_replaceKeywords("validazioneBusta.bustaNonCorretta"));
							}
							msgDiag.mediumDebug("Rilascio connessione...");
							openspcoopstate.releaseResource();
							esito.setEsitoInvocazione(true); 
							return esito;
						}
					}
				}
			}





			/* -----------
			 *
			 * Stampa dei messaggi di errore non GRAVI
			 * (frammento di codice raggiunto SOLO se profilo=LineeGuida1.1)
			 *
			 * ------------------ */
			if( (bustaRisposta.containsEccezioniGravi()==false) && (bustaRisposta.sizeListaEccezioni()>0) ){
				msgDiag.addKeyword(CostantiPdD.KEY_ECCEZIONI, bustaRisposta.toStringListaEccezioni(protocolFactory));
				msgDiag.logPersonalizzato("ricezioneBusta.eccezioniNonGravi");
			}
			// Stampa di eventuali eccezioni NON GRAVI venute fuori durante la validazione della busta
			if( (Busta.containsEccezioniGravi(errors)==false) && (errors.size()>0) ){
				msgDiag.addKeyword(CostantiPdD.KEY_ECCEZIONI, Busta.toStringListaEccezioni(errors, protocolFactory));
				msgDiag.addKeyword(CostantiPdD.KEY_NUMERO_ECCEZIONI, errors.size()+"" );
				msgDiag.logPersonalizzato("validazioneBusta.eccezioniNonGravi");
			}





			/* ------  Ricezione riscontri per il NAL (deve essere la prima attivita', poiche' effettua commit JDBC) -------- */
			if(ricezioneRiscontri){
				if(bustaRisposta.sizeListaRiscontri() > 0){
					for(int i=0;i<bustaRisposta.sizeListaRiscontri();i++){
						Riscontro r = bustaRisposta.getRiscontro(i);
						
						msgDiag.addKeyword(CostantiPdD.KEY_ID_BUSTA_RISCONTRATA, r.getID());
						msgDiag.addKeyword(CostantiPdD.KEY_DATA_RISCONTRO, r.getOraRegistrazione().toString());
						msgDiag.logPersonalizzato("ricezioneRiscontro");
						try{
							GestoreMessaggi msgRiscontrato = new GestoreMessaggi(openspcoopstate, true, r.getID(),Costanti.OUTBOX,msgDiag, pddContext);
							msgRiscontrato.validateAndDeleteMsgOneWayRiscontrato();
						}catch(Exception e){
							msgDiag.logErroreGenerico(e, "msgRiscontrato.validateAndDeleteMsgOneWayRiscontrato("+r.getID()+")"); 
						}
					}
				}
			}



			




			/*   -------------- Controllo esistenza soggetto destinatario  ------------------ */
			msgDiag.mediumDebug("Controllo appartenenza Destinazione Busta (controllo esistenza soggetto)...");
			boolean existsSoggetto = false;
			try{
				existsSoggetto = configurazionePdDManager.existsSoggetto(new IDSoggetto(bustaRisposta.getTipoDestinatario(),bustaRisposta.getDestinatario()));
			}catch(Exception e){
				msgDiag.logErroreGenerico(e,"existsSoggetto("+bustaRisposta.getTipoDestinatario()+"/"+bustaRisposta.getDestinatario()+")");
				if(sendRispostaApplicativa){
					OpenSPCoop2Message responseMessageError =
						this.erroreApplicativoBuilder.toMessage(ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
								get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_536_CONFIGURAZIONE_NON_DISPONIBILE),e,
								parseException);
					ejbUtils.sendRispostaApplicativaErrore(responseMessageError,richiestaDelegata,rollbackRiferimentoMessaggio,pd,sa);
					esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,
							"existsSoggetto("+bustaRisposta.getTipoDestinatario()+"/"+bustaRisposta.getDestinatario()+")");
				}else{
					ejbUtils.releaseInboxMessage(false); 
					esito.setStatoInvocazioneErroreNonGestito(e);
				}
				openspcoopstate.releaseResource();
				esito.setEsitoInvocazione(true);
				return esito;
			}
			if(!existsSoggetto){
				msgDiag.logPersonalizzato("soggettoDestinatarioNonGestito");
				if(sendRispostaApplicativa){
					OpenSPCoop2Message responseMessageError =
						this.erroreApplicativoBuilder.toMessage(ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
								get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_528_RISPOSTA_RICHIESTA_NON_VALIDA),null,
								parseException);
					ejbUtils.sendRispostaApplicativaErrore(responseMessageError,richiestaDelegata,rollbackRiferimentoMessaggio,pd,sa);
					esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,
							msgDiag.getMessaggio_replaceKeywords("soggettoDestinatarioNonGestito"));
				}else{
					ejbUtils.releaseInboxMessage(false);
					esito.setStatoInvocazione(EsitoLib.ERRORE_NON_GESTITO,
							msgDiag.getMessaggio_replaceKeywords("soggettoDestinatarioNonGestito"));
				}
				openspcoopstate.releaseResource();
				esito.setEsitoInvocazione(true);
				return esito;
			}










			/* -------------------
			   Controllo se la busta porta Dati .
			   In caso ad es. di riscontri ... arrivera' una busta senza dati, e senza profilo di collaborazione.
			   o comunque non devono essere considerati
			   --------------------- */
			if(bustaDiServizio == true) {

				msgDiag.logPersonalizzato("ricezioneBustaServizio");

				// Scenario OneWay in modalita sincrona
				if(Costanti.SCENARIO_ONEWAY_INVOCAZIONE_SERVIZIO.equals(richiestaDelegata.getScenario()) &&
						sendRispostaApplicativa){
					msgDiag.mediumDebug("Gestione riscontro per oneway in modalita sincrona...");
				}else{
					if(sendRispostaApplicativa){
						OpenSPCoop2Message responseMessageError =
							this.erroreApplicativoBuilder.toMessage(ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
									get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_517_RISPOSTA_RICHIESTA_NON_RITORNATA),null,
									parseException);
						ejbUtils.sendRispostaApplicativaErrore(responseMessageError,richiestaDelegata,rollbackRiferimentoMessaggio,pd,sa);
						esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,
								msgDiag.getMessaggio_replaceKeywords("ricezioneBustaServizio"));
					}else{
						ejbUtils.releaseInboxMessage(true);
						esito.setStatoInvocazione(EsitoLib.ERRORE_NON_GESTITO,
								msgDiag.getMessaggio_replaceKeywords("ricezioneBustaServizio"));
					}
					openspcoopstate.releaseResource();
					esito.setEsitoInvocazione(true); 
					return esito;
				}
			}







			/* ------------  Gestione Duplicati  ------------- */
						
			boolean checkDuplicato = imbustamentoFiltroDuplicatiAbilitato;
			if(checkDuplicato){
				
				try{
					
					// Istanzio gestore filtro duplicati
					String gestoreFiltroDuplicatiType = this.propertiesReader.getGestoreFiltroDuplicatiRepositoryBuste();
					ClassNameProperties prop = ClassNameProperties.getInstance();
					String gestoreFiltroDuplicatiClass = prop.getFiltroDuplicati(gestoreFiltroDuplicatiType);
					if(gestoreFiltroDuplicatiClass == null){
						throw new Exception("GestoreFiltroDuplicati non registrato ("+gestoreFiltroDuplicatiType+")");
					}
					IFiltroDuplicati gestoreFiltroDuplicati = (IFiltroDuplicati) this.loader.newInstance(gestoreFiltroDuplicatiClass);
					pddContext.addObject(org.openspcoop2.core.constants.Costanti.CONNECTION_PDD, ((OpenSPCoopState)openspcoopstate).getConnectionDB() );
					gestoreFiltroDuplicati.init(pddContext);
					if(gestoreFiltroDuplicati instanceof FiltroDuplicati){
						((FiltroDuplicati)gestoreFiltroDuplicati).setHistoryBuste(historyBuste);
						((FiltroDuplicati)gestoreFiltroDuplicati).setRepositoryBuste(repositoryBuste);
						((FiltroDuplicati)gestoreFiltroDuplicati).setGestioneStateless(openspcoopstate instanceof OpenSPCoopStateless);
						((FiltroDuplicati)gestoreFiltroDuplicati).setRepositoryIntervalloScadenzaMessaggi(this.propertiesReader.getRepositoryIntervalloScadenzaMessaggi());
					}
					boolean bustaDuplicata = gestoreFiltroDuplicati.isDuplicata(protocolFactory, bustaRisposta.getID());
				
					// BUSTA GIA' PRECEDENTEMENTE RICEVUTA
					if (bustaDuplicata){
						
						// Aggiorno duplicati
						msgDiag.logPersonalizzato("ricezioneBustaDuplicata.count");
						gestoreFiltroDuplicati.incrementaNumeroDuplicati(protocolFactory, bustaRisposta.getID());
						
						msgDiag.logPersonalizzato("ricezioneBustaDuplicata");
		
						if(sendRispostaApplicativa){
							/*this.xmlBuilder.msgErroreApplicativo_Processamento(proprietaErroreAppl,
										CostantiPdD.CODICE_528_RISPOSTA_RICHIESTA_NON_VALIDA,
										CostantiPdD.MSG_5XX_SISTEMA_NON_DISPONIBILE);*/
							OpenSPCoop2Message responseMessageError =
								this.erroreApplicativoBuilder.toMessage(Eccezione.getEccezioneValidazione(ErroriCooperazione.IDENTIFICATIVO_MESSAGGIO_GIA_PROCESSATO.getErroreCooperazione(),
										protocolFactory),
										identitaPdD,null,
										parseException);
							
							esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,
									msgDiag.getMessaggio_replaceKeywords("ricezioneBustaDuplicata"));
							ejbUtils.setRollbackRichiestaInCasoErrore_rollbackHistory(false); // non devo cancellare la busta per la gestione dei duplicati
							ejbUtils.sendRispostaApplicativaErrore(responseMessageError,richiestaDelegata,rollbackRiferimentoMessaggio,pd,sa);
						}else{
							// Devo rilasciare il messaggio, senza cancellarlo dalla history.
							ejbUtils.releaseInboxMessage(false,true);
							esito.setStatoInvocazione(EsitoLib.ERRORE_NON_GESTITO,
									msgDiag.getMessaggio_replaceKeywords("ricezioneBustaDuplicata"));
						}
		
						openspcoopstate.commit();
						openspcoopstate.releaseResource();
						esito.setEsitoInvocazione(true); 
						return esito;
		
					}else {
		
						// REGISTRAZIONE BUSTA RICEVUTA NELL'HISTORY
						gestoreFiltroDuplicati.registraBusta(protocolFactory, bustaRisposta);
						msgDiag.logPersonalizzato("ricezioneBusta.registrazionePerFiltroDuplicati");
					}
					
				}catch(Exception e) {
					msgDiag.logErroreGenerico(e, "GestioneHistoryBusteRicevute");
					if(sendRispostaApplicativa){
						OpenSPCoop2Message responseMessageError =
							this.erroreApplicativoBuilder.toMessage(ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
									get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_525_GESTIONE_FUNZIONALITA_PROTOCOLLO),e,
									parseException);
						ejbUtils.sendRispostaApplicativaErrore(responseMessageError,richiestaDelegata,rollbackRiferimentoMessaggio,pd,sa);
						esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,"GestioneHistoryBusteRicevute");
					}else{
						ejbUtils.releaseInboxMessage(false); 
						esito.setStatoInvocazioneErroreNonGestito(e);
					}
					openspcoopstate.releaseResource();
					esito.setEsitoInvocazione(true); 
					return esito;
				}
				
				pddContext.removeObject(org.openspcoop2.core.constants.Costanti.CONNECTION_PDD);
			}
			











			/* ------------  Invio Riscontri per altri NAL  ------------- */
			// TODO: da implementare




			/* ------------  Collaborazione e Sequenza  ------------- */
			// TODO: da implementare




			/* ------------  Validazione profilo di collaborazione Atteso  ------------- */
			Eccezione ecc = null;
			msgDiag.addKeyword(CostantiPdD.KEY_SCENARIO_COOPERAZIONE_GESTITO, richiestaDelegata.getScenario());
			if(Costanti.SCENARIO_ONEWAY_INVOCAZIONE_SERVIZIO.equals(richiestaDelegata.getScenario())){
				msgDiag.mediumDebug("Gestione profilo di collaborazione OneWay...");
				// Deve arrivare al massimo una busta con profilo null, o oneWay per compatibilita' con altre PdD
				if(bustaRisposta.getProfiloDiCollaborazione()!=null &&
						org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.ONEWAY.equals(bustaRisposta.getProfiloDiCollaborazione()) == false){
					
					msgDiag.logPersonalizzato("profiloCollaborazioneRisposta.diversoScenarioGestito");
					ecc = Eccezione.getEccezioneValidazione(ErroriCooperazione.PROFILO_COLLABORAZIONE_NON_VALIDO.getErroreProfiloCollaborazioneNonValido(
							"Profilo di collaborazione diverso dallo scenario di cooperazione (atteso OneWay)."),
					protocolFactory);
				}
				// Se arriva un riferimento msg deve comunque essere quello della richiesta
				// La validazione non valida il rifMsg per OneWay poiche' e' solo per compatibilita' con HPPdD
				else if(bustaRisposta.getRiferimentoMessaggio()!=null &&
						idMessageRequest.equals(bustaRisposta.getRiferimentoMessaggio()) == false){
					msgDiag.logPersonalizzato("riferimentoMessaggioNonValido");
					ecc = Eccezione.getEccezioneValidazione(ErroriCooperazione.RIFERIMENTO_MESSAGGIO_NON_VALIDO.getErroreCooperazione(),protocolFactory);
				}
			}
			if(Costanti.SCENARIO_SINCRONO_INVOCAZIONE_SERVIZIO.equals(richiestaDelegata.getScenario())){
				msgDiag.mediumDebug("Gestione profilo di collaborazione Sincrono...");
				if(org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.SINCRONO.equals(bustaRisposta.getProfiloDiCollaborazione()) == false){
					msgDiag.logPersonalizzato("profiloCollaborazioneRisposta.diversoScenarioGestito");
					ecc = Eccezione.getEccezioneValidazione(ErroriCooperazione.PROFILO_COLLABORAZIONE_NON_VALIDO.getErroreProfiloCollaborazioneNonValido(
					"Profilo di collaborazione diverso dallo scenario di cooperazione (atteso Sincrono)."),
					protocolFactory);
				}else if(bustaRisposta.getRiferimentoMessaggio()==null){
					msgDiag.logPersonalizzato("riferimentoMessaggioNonPresente");
					ecc = Eccezione.getEccezioneValidazione(ErroriCooperazione.RIFERIMENTO_MESSAGGIO_NON_PRESENTE.getErroreCooperazione(),protocolFactory);
				}else if(bustaRisposta.getRiferimentoMessaggio()!=null &&
						idMessageRequest.equals(bustaRisposta.getRiferimentoMessaggio()) == false){
					msgDiag.logPersonalizzato("riferimentoMessaggioNonValido");
					ecc = Eccezione.getEccezioneValidazione(ErroriCooperazione.RIFERIMENTO_MESSAGGIO_NON_VALIDO.getErroreCooperazione(),protocolFactory);
				}
			}else if(Costanti.SCENARIO_ASINCRONO_SIMMETRICO_INVOCAZIONE_SERVIZIO.equals(richiestaDelegata.getScenario()) ||
					Costanti.SCENARIO_ASINCRONO_SIMMETRICO_CONSEGNA_RISPOSTA.equals(richiestaDelegata.getScenario()) ){
				msgDiag.mediumDebug("Gestione profilo di collaborazione AsincronoSimmetrico...");
				if(org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.ASINCRONO_SIMMETRICO.equals(bustaRisposta.getProfiloDiCollaborazione()) == false){
					msgDiag.logPersonalizzato("profiloCollaborazioneRisposta.diversoScenarioGestito");
					ecc = Eccezione.getEccezioneValidazione(ErroriCooperazione.PROFILO_COLLABORAZIONE_NON_VALIDO.getErroreProfiloCollaborazioneNonValido(
					"Profilo di collaborazione diverso dallo scenario di cooperazione (atteso AsincronoSimmetrico)."),
					protocolFactory);
				}else if(bustaRisposta.getRiferimentoMessaggio()==null){
					msgDiag.logPersonalizzato("riferimentoMessaggioNonPresente");
					ecc = Eccezione.getEccezioneValidazione(ErroriCooperazione.RIFERIMENTO_MESSAGGIO_NON_PRESENTE.getErroreCooperazione(),protocolFactory);
				}else if(bustaRisposta.getRiferimentoMessaggio()!=null &&
						idMessageRequest.equals(bustaRisposta.getRiferimentoMessaggio()) == false){
					msgDiag.logPersonalizzato("riferimentoMessaggioNonValido");
					ecc = Eccezione.getEccezioneValidazione(ErroriCooperazione.RIFERIMENTO_MESSAGGIO_NON_VALIDO.getErroreCooperazione(),protocolFactory);
				}
			}
			else if(Costanti.SCENARIO_ASINCRONO_ASIMMETRICO_INVOCAZIONE_SERVIZIO.equals(richiestaDelegata.getScenario()) ||
					Costanti.SCENARIO_ASINCRONO_ASIMMETRICO_POLLING.equals(richiestaDelegata.getScenario()) ){
				msgDiag.mediumDebug("Gestione profilo di collaborazione AsincronoAsimmetrico...");
				if(org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.ASINCRONO_ASIMMETRICO.equals(bustaRisposta.getProfiloDiCollaborazione()) == false){
					msgDiag.logPersonalizzato("profiloCollaborazioneRisposta.diversoScenarioGestito");
					ecc = Eccezione.getEccezioneValidazione(ErroriCooperazione.PROFILO_COLLABORAZIONE_NON_VALIDO.getErroreProfiloCollaborazioneNonValido(
					"Profilo di collaborazione diverso dallo scenario di cooperazione (atteso AsincronoAsimmetrico)."),
					protocolFactory);
				}else if(bustaRisposta.getRiferimentoMessaggio()==null){
					msgDiag.logPersonalizzato("riferimentoMessaggioNonPresente");
					ecc = Eccezione.getEccezioneValidazione(ErroriCooperazione.RIFERIMENTO_MESSAGGIO_NON_PRESENTE.getErroreCooperazione(),protocolFactory);
				}else if(bustaRisposta.getRiferimentoMessaggio()!=null &&
						idMessageRequest.equals(bustaRisposta.getRiferimentoMessaggio()) == false){
					msgDiag.logPersonalizzato("riferimentoMessaggioNonValido");
					ecc = Eccezione.getEccezioneValidazione(ErroriCooperazione.RIFERIMENTO_MESSAGGIO_NON_VALIDO.getErroreCooperazione(),protocolFactory);
				}
			}
			if(ecc!=null){
				if(sendRispostaApplicativa){
					OpenSPCoop2Message responseMessageError =
						this.erroreApplicativoBuilder.toMessage(ecc,
								richiestaDelegata.getSoggettoFruitore(),null,
								parseException);
					ejbUtils.sendRispostaApplicativaErrore(responseMessageError,richiestaDelegata,rollbackRiferimentoMessaggio,pd,sa);
					esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,ecc.toString(protocolFactory));
				}else{
					ejbUtils.releaseInboxMessage(false); 
					esito.setStatoInvocazione(EsitoLib.ERRORE_NON_GESTITO,ecc.toString(protocolFactory));
				}
				openspcoopstate.releaseResource();
				esito.setEsitoInvocazione(true); 
				return esito;
			}














			/* ------------  Profilo di collaborazione  ------------- */
			msgDiag.mediumDebug("Gestione profilo...");
			if(org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.ONEWAY.equals(bustaRisposta.getProfiloDiCollaborazione())){
				msgDiag.logPersonalizzato("rispostaOneway");
			}
			else if(org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.SINCRONO.equals(bustaRisposta.getProfiloDiCollaborazione())){

				// Eliminazione informazioni sincrone
				msgDiag.mediumDebug("Gestione profilo di collaborazione Sincrono (elimina richiesta in OutBox)...");
				try{
					profiloCollaborazione.sincrono_eliminaRichiestaInOutBox(bustaRisposta.getRiferimentoMessaggio());
				}catch(Exception e){
					msgDiag.logErroreGenerico(e, "profiloCollaborazione.sincrono_eliminaRichiestaInOutBox");
					OpenSPCoop2Message responseMessageError =
						this.erroreApplicativoBuilder.toMessage(ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
								get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_525_GESTIONE_FUNZIONALITA_PROTOCOLLO),e,
								parseException);
					ejbUtils.sendRispostaApplicativaErrore(responseMessageError,richiestaDelegata,rollbackRiferimentoMessaggio,pd,sa);
					openspcoopstate.releaseResource();
					esito.setEsitoInvocazione(true);
					esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO, "profiloCollaborazione.sincrono_eliminaRichiestaInOutBox");
					return esito;
				}
			}
			else if(org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.ASINCRONO_SIMMETRICO.equals(bustaRisposta.getProfiloDiCollaborazione())) {

				// un riferimento msg vi e' per forza, altrimenti non supera il controllo precedente sullo scenario

				//	Ricevuta alla richiesta.
				if(RuoloBusta.RICEVUTA_RICHIESTA.equals(ruoloBustaRicevuta.toString())){
					ricevutaAsincrona = "ricevuta di una richiesta asincrona simmetrica";
				}
				//	Ricevuta alla risposta.
				else if(RuoloBusta.RICEVUTA_RISPOSTA.equals(ruoloBustaRicevuta.toString())){
					ricevutaAsincrona = "ricevuta di una risposta asincrona simmetrica";
				}
				else{
					String motivazioneErrore = "Gestione busta di risposta asincrona simmetrica non permessa (Busta "+bustaRisposta.getID()+"); il modulo attendeva una ricevuta asincrona";
					msgDiag.logErroreGenerico(motivazioneErrore,"GestioneProfiloAsincronoSimmetrico");
					ejbUtils.releaseInboxMessage(false);
					openspcoopstate.releaseResource();
					esito.setEsitoInvocazione(true); 
					esito.setStatoInvocazione(EsitoLib.ERRORE_NON_GESTITO,motivazioneErrore);
					return esito;
				}
			}else if(org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.ASINCRONO_ASIMMETRICO.equals(bustaRisposta.getProfiloDiCollaborazione())) {	

				// un riferimento msg vi e' per forza, altrimenti non supera il controllo precedente sullo scenario 
				
				//	Ricevuta alla richiesta.
				if(RuoloBusta.RICEVUTA_RICHIESTA.equals(ruoloBustaRicevuta.toString())){
						
					ricevutaAsincrona = "ricevuta di una richiesta asincrona asimmetrica";
						
				}
				//	Ricevuta alla risposta.
				else if(RuoloBusta.RICEVUTA_RISPOSTA.equals(ruoloBustaRicevuta.toString())){
					
					ricevutaAsincrona = "ricevuta di una risposta asincrona asimmetrica contenente l'esito della richiesta stato";
													
				}
				else{
					String motivazioneErrore = "Gestione busta di risposta asincrona asimmetrica non permessa (Busta "+bustaRisposta.getID()+"); il modulo attendeva una ricevuta asincrona";
					msgDiag.logErroreGenerico(motivazioneErrore, "GestioneProfiloAsincronoAsimmetrico");
					ejbUtils.releaseInboxMessage(false);
					openspcoopstate.releaseResource();
					esito.setEsitoInvocazione(true); 
					esito.setStatoInvocazione(EsitoLib.ERRORE_NON_GESTITO,motivazioneErrore);
					return esito;
						
				}
			}	  











			/* ----------------- GestioneBustaArrivata ----------------- */
			if(sendRispostaApplicativa){

				// Spedisco risposta applicativa

				if(Costanti.SCENARIO_ONEWAY_INVOCAZIONE_SERVIZIO.equals(richiestaDelegata.getScenario())){


					msgResponse = new GestoreMessaggi(openspcoopstate, false, bustaRisposta.getID(), Costanti.INBOX, msgDiag,pddContext);
					msgDiag.mediumDebug("Aggiorno proprietario messaggio...");
					// Aggiorno proprietario messaggio
					msgResponse.aggiornaProprietarioMessaggio(TimerGestoreMessaggi.ID_MODULO);
					msgDiag.mediumDebug("Elimino accesso da PdD...");
					// Elimino accesso daPdD
					repositoryBuste.eliminaUtilizzoPdDFromInBox(idResponse);

					msgDiag.mediumDebug("Invio messaggio 'OK' al modulo di RicezioneContenutiApplicativi, oneway con scenario sincrono...");

					if(protocolManager.isHttpEmptyResponseOneWay())
						msgResponse = ejbUtils.sendRispostaApplicativaOK(SoapUtils.build_Soap_Empty(versioneSoap),richiestaDelegata,pd,sa);
					else
						msgResponse = ejbUtils.sendRispostaApplicativaOK(ejbUtils.buildOpenSPCoopOK_soapMsg(versioneSoap, idMessageRequest),richiestaDelegata,pd,sa);
					
				} else {
					msgDiag.mediumDebug("Send risposta applicativa...");
					msgResponse = ejbUtils.sendRispostaApplicativa(richiestaDelegata,pd,sa);
				}

			} else {

				// Elimino la risposta
				msgResponse = new GestoreMessaggi(openspcoopstate, false, bustaRisposta.getID(),Costanti.INBOX,msgDiag,pddContext);
				msgDiag.mediumDebug("Aggiorno proprietario messaggio...");
				// Aggiorno proprietario messaggio
				msgResponse.aggiornaProprietarioMessaggio(TimerGestoreMessaggi.ID_MODULO);
				msgDiag.mediumDebug("Elimino accesso da PdD...");
				// Elimino accesso daPdD
				repositoryBuste.eliminaUtilizzoPdDFromInBox(idResponse,statelessAsincrono);
			}

			msgDiag.mediumDebug("Commit delle operazioni per la gestione della busta...");
			openspcoopstate.commit();

		}catch(Throwable e){
			this.log.error("ErroreGenerale",e);
			msgDiag.logErroreGenerico(e, "Generale");
			
			if(sendRispostaApplicativa){
				OpenSPCoop2Message responseMessageError =
					this.erroreApplicativoBuilder.toMessage(ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.getErroreIntegrazione(),e,
							parseException);
				try{
					ejbUtils.sendRispostaApplicativaErrore(responseMessageError,richiestaDelegata,rollbackRiferimentoMessaggio,pd,sa);
					esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,"ErroreGenerale");
					esito.setEsitoInvocazione(true);
				}catch(Exception er){
					msgDiag.logErroreGenerico(er,"ejbUtils.sendErroreGenerale(profiloConRisposta)");
					ejbUtils.rollbackMessage("Spedizione Errore al Mittente durante una richiesta sincrona non riuscita", esito);
					esito.setStatoInvocazioneErroreNonGestito(er);
					esito.setEsitoInvocazione(false);
				}
			}else{
				ejbUtils.rollbackMessage("ErroreGenerale:"+e.getMessage(), esito);
				esito.setEsitoInvocazione(false);
				esito.setStatoInvocazioneErroreNonGestito(e);
			}
			openspcoopstate.releaseResource();
			return esito;
		}

		// Aggiornamento cache messaggio
		if(msgResponse!=null)
			msgResponse.addMessaggiIntoCache_readFromTable(SbustamentoRisposte.ID_MODULO, "risposte");

		// Aggiornamento cache proprietario messaggio
		if(msgResponse!=null)
			msgResponse.addProprietariIntoCache_readFromTable(SbustamentoRisposte.ID_MODULO, "risposte",idMessageRequest,false);

		//	Gestione ricevute
		if(ricevutaAsincrona!=null){
			msgDiag.mediumDebug("Gestione ricevute asincrone...");
			try{

				boolean forzaEliminazioneMessaggio = false;
				if( (openspcoopstate instanceof OpenSPCoopStateless) &&  
						("ricevuta di una richiesta asincrona simmetrica".equals(ricevutaAsincrona)) ){
					forzaEliminazioneMessaggio = true;
				}
				
				GestoreMessaggi msgRiscontrato = new GestoreMessaggi(openspcoopstate, false ,bustaRisposta.getRiferimentoMessaggio(),Costanti.OUTBOX,msgDiag,pddContext);
				msgRiscontrato.setReadyForDrop(forzaEliminazioneMessaggio);
				msgRiscontrato.validateAndDeleteMsgAsincronoRiscontrato(bustaRisposta);
				msgDiag.addKeyword(CostantiPdD.KEY_TIPO_RICEVUTA_ASINCRONA, ricevutaAsincrona);
				msgDiag.logPersonalizzato("validazioneRicevutaAsincrona");
			}catch(Exception e){
				msgDiag.logErroreGenerico(e,"Validazione("+ricevutaAsincrona+")");
			}
		}

		//	Rilascio connessione al DB
		msgDiag.mediumDebug("Rilascio connessione al database...");
		openspcoopstate.releaseResource();

		msgDiag.mediumDebug("Lavoro Terminato.");
		esito.setStatoInvocazione(EsitoLib.OK, null);

		esito.setEsitoInvocazione(true);
		return esito;

	}
}
