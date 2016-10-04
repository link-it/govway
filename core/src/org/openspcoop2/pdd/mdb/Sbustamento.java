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
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.core.config.driver.DriverConfigurazioneNotFound;
import org.openspcoop2.core.constants.TipoPdD;
import org.openspcoop2.core.eccezione.details.DettaglioEccezione;
import org.openspcoop2.core.eccezione.details.utils.XMLUtils;
import org.openspcoop2.core.id.IDPortaDelegata;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.SOAPVersion;
import org.openspcoop2.message.SoapUtils;
import org.openspcoop2.pdd.config.ClassNameProperties;
import org.openspcoop2.pdd.config.ConfigurazionePdDManager;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.config.RichiestaApplicativa;
import org.openspcoop2.pdd.config.RichiestaDelegata;
import org.openspcoop2.pdd.core.CostantiPdD;
import org.openspcoop2.pdd.core.EJBUtils;
import org.openspcoop2.pdd.core.EJBUtilsConsegnaException;
import org.openspcoop2.pdd.core.GestoreMessaggi;
import org.openspcoop2.pdd.core.PdDContext;
import org.openspcoop2.pdd.core.behaviour.Behaviour;
import org.openspcoop2.pdd.core.state.IOpenSPCoopState;
import org.openspcoop2.pdd.core.state.OpenSPCoopState;
import org.openspcoop2.pdd.core.state.OpenSPCoopStateException;
import org.openspcoop2.pdd.core.state.OpenSPCoopStateless;
import org.openspcoop2.pdd.logger.MsgDiagnosticiProperties;
import org.openspcoop2.pdd.logger.MsgDiagnostico;
import org.openspcoop2.protocol.engine.builder.ErroreApplicativoBuilder;
import org.openspcoop2.protocol.engine.constants.Costanti;
import org.openspcoop2.protocol.engine.driver.FiltroDuplicati;
import org.openspcoop2.protocol.engine.driver.History;
import org.openspcoop2.protocol.engine.driver.IFiltroDuplicati;
import org.openspcoop2.protocol.engine.driver.ProfiloDiCollaborazione;
import org.openspcoop2.protocol.engine.driver.RepositoryBuste;
import org.openspcoop2.protocol.registry.RegistroServiziManager;
import org.openspcoop2.protocol.registry.RisultatoValidazione;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.Eccezione;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.Integrazione;
import org.openspcoop2.protocol.sdk.Riscontro;
import org.openspcoop2.protocol.sdk.builder.ProprietaErroreApplicativo;
import org.openspcoop2.protocol.sdk.config.IProtocolConfiguration;
import org.openspcoop2.protocol.sdk.config.IProtocolManager;
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
import org.openspcoop2.protocol.sdk.constants.TipoOraRegistrazione;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.resources.Loader;


/**
 * 
 * Contiene la libreria Sbustamento
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author Tronci Fabio (tronci@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class Sbustamento extends GenericLib{

	public final static String ID_MODULO = "Sbustamento";

	/** XMLBuilder */
	private ErroreApplicativoBuilder erroreApplicativoBuilder;


	public Sbustamento(Logger log) throws GenericLibException {
		super(Sbustamento.ID_MODULO, log);
		inizializza();
	}

	@Override
	protected void inizializza() throws GenericLibException {
		super.inizializza();
	}

	@Override
	public EsitoLib _onMessage(IOpenSPCoopState openspcoopstate,
			RegistroServiziManager registroServiziManager,ConfigurazionePdDManager configurazionePdDManager, 
			MsgDiagnostico msgDiag) throws OpenSPCoopStateException {

		SbustamentoMessage sbustamentoMsg = (SbustamentoMessage) openspcoopstate.getMessageLib();
		
		EsitoLib esito = new EsitoLib();

		/* PddContext */
		PdDContext pddContext = sbustamentoMsg.getPddContext();
		String idTransazione = PdDContext.getValue(org.openspcoop2.core.constants.Costanti.CLUSTER_ID, pddContext);
		
		/* Protocol Factory */
		IProtocolFactory protocolFactory = null;
		IProtocolVersionManager protocolManager = null;
		ITraduttore traduttore = null;
		try{
			protocolFactory = this.protocolFactoryManager.getProtocolFactoryByName((String) pddContext.getObject(org.openspcoop2.core.constants.Costanti.PROTOCOLLO));
			protocolManager = protocolFactory.createProtocolVersionManager(sbustamentoMsg.getRichiestaApplicativa().getProfiloGestione());
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
		
		/* Busta e tipo di implementazione PdD con cui interoperare */
		Busta bustaRichiesta = sbustamentoMsg.getBusta();
		String implementazionePdDMittente = sbustamentoMsg.getImplementazionePdDSoggettoMittente();
		
		/* Intrepretazione informazioni */
		RichiestaApplicativa richiestaApplicativa = sbustamentoMsg.getRichiestaApplicativa();
	
		IDServizio idServizio = richiestaApplicativa.getIDServizio();
		IDSoggetto idSoggettoFruitore = richiestaApplicativa.getSoggettoFruitore();
		java.util.Vector<Eccezione> errors = sbustamentoMsg.getErrors();
		boolean isMessaggioErroreProtocollo = sbustamentoMsg.isMessaggioErroreProtocollo();
		boolean bustaDiServizio = sbustamentoMsg.getIsBustaDiServizio();
		TipoPdD tipoPdD = TipoPdD.APPLICATIVA;
		IDSoggetto identitaPdD = sbustamentoMsg.getRichiestaApplicativa().getDominio();
		
		msgDiag.setDominio(identitaPdD);  // imposto anche il dominio nel msgDiag
		msgDiag.setPrefixMsgPersonalizzati(MsgDiagnosticiProperties.MSG_DIAG_SBUSTAMENTO);
		msgDiag.addKeywords(bustaRichiesta, true);
		if(richiestaApplicativa.getIdentitaServizioApplicativoFruitore()!=null){
			msgDiag.addKeyword(CostantiPdD.KEY_SA_FRUITORE, richiestaApplicativa.getIdentitaServizioApplicativoFruitore());
		}
		msgDiag.setIdCorrelazioneApplicativa(richiestaApplicativa.getIdCorrelazioneApplicativa());
		msgDiag.addKeyword(CostantiPdD.KEY_ID_CORRELAZIONE_APPLICATIVA, richiestaApplicativa.getIdCorrelazioneApplicativa());
		
		String profiloGestione = richiestaApplicativa.getProfiloGestione();
		msgDiag.mediumDebug("Profilo di gestione ["+Sbustamento.ID_MODULO+"] della busta: "+profiloGestione);
		
		try{
			this.erroreApplicativoBuilder = new ErroreApplicativoBuilder(this.log, protocolFactory, 
					identitaPdD, idSoggettoFruitore, idServizio, this.idModulo, 
					this.propertiesReader.getProprietaGestioneErrorePD(protocolManager), versioneSoap,
					TipoPdD.APPLICATIVA,null);
		}catch(Exception e){
			msgDiag.logErroreGenerico(e, "ErroreApplicativoBuilder.instanziazione"); 
			openspcoopstate.releaseResource();
			esito.setEsitoInvocazione(false); 
			esito.setStatoInvocazioneErroreNonGestito(e);
			return esito;
		}
		

		/* ------------------ Inizializzo stato OpenSPCoop  --------------- */
		msgDiag.mediumDebug("Inizializzo stato per la gestione della richiesta...");
		openspcoopstate.initResource(identitaPdD, Sbustamento.ID_MODULO,idTransazione);
		registroServiziManager.updateState(openspcoopstate.getStatoRichiesta(),openspcoopstate.getStatoRisposta());
		configurazionePdDManager.updateState(openspcoopstate.getStatoRichiesta(),openspcoopstate.getStatoRisposta());
		msgDiag.updateState(openspcoopstate.getStatoRichiesta(),openspcoopstate.getStatoRisposta());


		/* ------------- STATO RICHIESTA E STATO RISPOSTA ------------------------------- */
		//	IState statoRichiesta = openSPCoopState.getStatoRichiesta();
		//	IState statoRisposta = openSPCoopState.getStatoRisposta();

		/* ----------------- ID RICHIESTA ---------------------------------------------- */
		String idMessageRequest = openspcoopstate.getIDMessaggioSessione();


		// Aggiornamento Informazioni
		msgDiag.setIdMessaggioRichiesta(idMessageRequest);
		msgDiag.setFruitore(idSoggettoFruitore);
		msgDiag.setServizio(idServizio);
		msgDiag.setDelegata(false);



		/* ------------------ Inizializzazione Contesto di gestione  --------------- */
		msgDiag.mediumDebug("Inizializzo contesto per la gestione...");

		// EJBUtils (per eventuali errori)
		EJBUtils ejbUtils = null;
		try{
			ejbUtils = new EJBUtils(identitaPdD,tipoPdD,Sbustamento.ID_MODULO,idMessageRequest, idMessageRequest,Costanti.INBOX,openspcoopstate,msgDiag,false,
					sbustamentoMsg.getImplementazionePdDSoggettoMittente(),
					sbustamentoMsg.getImplementazionePdDSoggettoDestinatario(),
					profiloGestione,pddContext);
		}catch(Exception e){
			msgDiag.logErroreGenerico(e, "EJBUtils.new");
			openspcoopstate.releaseResource(); 
			esito.setEsitoInvocazione(false); 
			esito.setStatoInvocazioneErroreNonGestito(e);
			return esito;
		}

		// Oneway versione 11
		boolean oneWayVersione11 = sbustamentoMsg.isOneWayVersione11();
		ejbUtils.setOneWayVersione11(oneWayVersione11);
		ejbUtils.setPortaDiTipoStateless_esclusoOneWay11(sbustamentoMsg.isStateless());
		
		// Gestori funzionalita'
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
		History historyBuste = new History(openspcoopstate.getStatoRichiesta());
		RepositoryBuste repositoryBuste = new RepositoryBuste(openspcoopstate.getStatoRichiesta(), true,protocolFactory);
		String ricevutaAsincrona = null;

		
		// Gestore Messaggi
		GestoreMessaggi msgRequest = new GestoreMessaggi(openspcoopstate,true, idMessageRequest,Costanti.INBOX,msgDiag,pddContext);
		GestoreMessaggi msgResponse = null; // Evenutuale busta spedita indietro contenente riscontri...
		msgRequest.setOneWayVersione11(oneWayVersione11);
		
		// Informazioni di integrazione
		String servizioApplicativoFruitore = richiestaApplicativa.getIdentitaServizioApplicativoFruitore();
		String idCorrelazioneApplicativa = richiestaApplicativa.getIdCorrelazioneApplicativa();

				
		// Modalita' gestione risposta (Sincrona/Fault/Ricevute...)
		// Per i profili diversi dal sincrono e' possibile impostare dove far ritornare l'errore
		boolean newConnectionForResponse = false; 
		if( (org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.SINCRONO.equals(bustaRichiesta.getProfiloDiCollaborazione())==false) &&
				(sbustamentoMsg.isStateless()==false) ) {
			newConnectionForResponse = configurazionePdDManager.newConnectionForResponse();
		}
		ejbUtils.setReplyOnNewConnection(newConnectionForResponse);
		
		// Gestione indirizzo telematico
		boolean gestioneIndirizzoTelematico = false;
		if(sbustamentoMsg.isStateless()==false){
			gestioneIndirizzoTelematico = configurazionePdDManager.isUtilizzoIndirizzoTelematico();
		}
		ejbUtils.setUtilizzoIndirizzoTelematico(gestioneIndirizzoTelematico);
	
		

		// Indicazione se deve essere spedito lo sblocco al moduloRicezione
		// non deve essere spedito SOLO se il modulo ha gia' terminato di gestire.
		// Succede se:
		// - e' abilitata la gestione dell'indirizzo telematico e la busta lo possiede nel mittente
		// - profilo della busta non è sincrono e le risposte vengono mandate su di una nuova connessione
		boolean sendSbloccoRicezioneBuste = true;
		if ( configurazionePdDManager.isUtilizzoIndirizzoTelematico() && bustaRichiesta.getIndirizzoMittente()!=null &&
				(sbustamentoMsg.isStateless()==false) ){
			sendSbloccoRicezioneBuste = false;
		}else if( newConnectionForResponse ) {
			sendSbloccoRicezioneBuste = false;
		}

		// Ruolo Busta ricevuta
		RuoloBusta ruoloBustaRicevuta = sbustamentoMsg.getRuoloBustaRicevuta();



		



		// Punto di inizio per la transazione.
		Busta bustaHTTPReply = null;
		try{

			boolean ricezioneRiscontri = false;
			switch (protocolManager.getConsegnaAffidabile(bustaRichiesta.getProfiloDiCollaborazione())) {
			case ABILITATA:
				ricezioneRiscontri = true;
				break;
			case DISABILITATA:
				ricezioneRiscontri = false;
				break;
			default:
				ricezioneRiscontri = this.propertiesReader.isGestioneRiscontri(implementazionePdDMittente);
				break;
			}

			boolean consegnaAffidabile = false;
			switch (protocolManager.getConsegnaAffidabile(bustaRichiesta.getProfiloDiCollaborazione())) {
			case ABILITATA:
				consegnaAffidabile = true;
				break;
			case DISABILITATA:
				consegnaAffidabile = false;
				break;
			default:
				consegnaAffidabile = this.propertiesReader.isGestioneRiscontri(implementazionePdDMittente) && bustaRichiesta.isConfermaRicezione();
				break;
			}
			
			boolean idCollaborazione = false;
			switch (protocolManager.getCollaborazione(bustaRichiesta.getProfiloDiCollaborazione())) {
			case ABILITATA:
				idCollaborazione = true;
				break;
			case DISABILITATA:
				idCollaborazione = false;
				break;
			default:
				idCollaborazione = this.propertiesReader.isGestioneElementoCollaborazione(implementazionePdDMittente) && (bustaRichiesta.getCollaborazione()!=null);
				break;
			}
					
			boolean consegnaInOrdine = false;
			switch (protocolManager.getConsegnaInOrdine(bustaRichiesta.getProfiloDiCollaborazione())) {
				case ABILITATA:
					consegnaInOrdine = true;
					break;
				case DISABILITATA:
					consegnaInOrdine = false;
					break;
				default:
					boolean gestioneConsegnaInOrdineAbilitata =  this.propertiesReader.isGestioneRiscontri(implementazionePdDMittente) && 
					this.propertiesReader.isGestioneElementoCollaborazione(implementazionePdDMittente) && 
					this.propertiesReader.isGestioneConsegnaInOrdine(implementazionePdDMittente);	
					consegnaInOrdine = gestioneConsegnaInOrdineAbilitata &&
						bustaRichiesta.getSequenza()!=-1;
					break;
			}
			
			boolean imbustamentoFiltroDuplicatiAbilitato = false;
			switch (protocolManager.getFiltroDuplicati(bustaRichiesta.getProfiloDiCollaborazione())) {
			case ABILITATA:
				imbustamentoFiltroDuplicatiAbilitato = true;
				break;
			case DISABILITATA:
				imbustamentoFiltroDuplicatiAbilitato = false;
				break;
			default:
				imbustamentoFiltroDuplicatiAbilitato = (Inoltro.SENZA_DUPLICATI.equals(bustaRichiesta.getInoltro())) ||
				(this.propertiesReader.isCheckFromRegistroFiltroDuplicatiAbilitato(implementazionePdDMittente) && sbustamentoMsg.isFiltroDuplicatiRichiestoAccordo());
				break;
			}
			
			
			
			
			
			// ------------- Controllo funzionalita di protocollo richieste siano compatibili con il protocollo -----------------------------
			try{
				IProtocolConfiguration protocolConfiguration = protocolFactory.createProtocolConfiguration();
				if(bustaRichiesta.getProfiloDiCollaborazione()!=null && 
						!org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.UNKNOWN.equals(bustaRichiesta.getProfiloDiCollaborazione())){
					if(protocolConfiguration.isSupportato(bustaRichiesta.getProfiloDiCollaborazione())==false){
						throw new Exception("Profilo di Collaborazione ["+bustaRichiesta.getProfiloDiCollaborazione().getEngineValue()+"]");
					}
				}
				if(imbustamentoFiltroDuplicatiAbilitato){
					if(protocolConfiguration.isSupportato(FunzionalitaProtocollo.FILTRO_DUPLICATI)==false){
						throw new Exception(FunzionalitaProtocollo.FILTRO_DUPLICATI.getEngineValue());
					}
				}
				if(consegnaAffidabile){
					if(protocolConfiguration.isSupportato(FunzionalitaProtocollo.CONFERMA_RICEZIONE)==false){
						throw new Exception(FunzionalitaProtocollo.CONFERMA_RICEZIONE.getEngineValue());
					}
				}
				if(idCollaborazione){
					if(protocolConfiguration.isSupportato(FunzionalitaProtocollo.COLLABORAZIONE)==false){
						throw new Exception(FunzionalitaProtocollo.COLLABORAZIONE.getEngineValue());
					}
				}
				if(consegnaInOrdine){
					if(protocolConfiguration.isSupportato(FunzionalitaProtocollo.CONSEGNA_IN_ORDINE)==false){
						throw new Exception(FunzionalitaProtocollo.CONSEGNA_IN_ORDINE.getEngineValue());
					}
				}				
			}catch(Exception e){	
				msgDiag.addKeyword(CostantiPdD.KEY_ERRORE_PROCESSAMENTO , e.getMessage() );
				msgDiag.logPersonalizzato("protocolli.funzionalita.unsupported");
				ejbUtils.sendAsRispostaBustaErroreProcessamento(richiestaApplicativa.getIdModuloInAttesa(),bustaRichiesta,
						ErroriIntegrazione.ERRORE_439_FUNZIONALITA_NOT_SUPPORTED_BY_PROTOCOL.
						getErrore439_FunzionalitaNotSupportedByProtocol(e.getMessage(), protocolFactory),
						idCorrelazioneApplicativa,servizioApplicativoFruitore,null,
						null);
				openspcoopstate.releaseResource();
				esito.setEsitoInvocazione(true);    
				esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,
						msgDiag.getMessaggio_replaceKeywords("protocolli.funzionalita.unsupported")); 
				return esito;
			}
			
			
			
			
			
			
			
			




			/*   -------------- Validazione: Gestione messaggio Errore Protocollo -----------------  */ 
			if(isMessaggioErroreProtocollo){

				msgDiag.mediumDebug("Gestione messaggio errore protocollo...");
				boolean msgErroreProtocolloValido = true;

				// validazione
				if(configurazionePdDManager.getTipoValidazione(implementazionePdDMittente).equals(CostantiConfigurazione.STATO_CON_WARNING_DISABILITATO) == false){

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
						if(configurazionePdDManager.getTipoValidazione(implementazionePdDMittente).equals(CostantiConfigurazione.STATO_CON_WARNING_ABILITATO) == true){
							msgErroreProtocolloValido=false;
						}  
					}

				}// -- end if not active

				if(msgErroreProtocolloValido){
					//	 Registrazione eccezioni portate nella busta
					if(bustaRichiesta.sizeListaEccezioni()>0){
						msgDiag.addKeyword(CostantiPdD.KEY_ECCEZIONI, bustaRichiesta.toStringListaEccezioni(protocolFactory));
						msgDiag.addKeyword(CostantiPdD.KEY_NUMERO_ECCEZIONI, bustaRichiesta.sizeListaEccezioni()+"");
						msgDiag.logPersonalizzato("ricezioneBustaErrore");
					}
				}

				DettaglioEccezione dettaglioEccezione = sbustamentoMsg.getDettaglioEccezione();
				if(dettaglioEccezione!=null){
					msgDiag.addKeyword(CostantiPdD.KEY_OPENSPCOOP2_DETAILS, XMLUtils.toString(dettaglioEccezione));
					msgDiag.logPersonalizzato("ricezioneBustaErroreDetails");
				}
				
				if(sendSbloccoRicezioneBuste){
					msgDiag.mediumDebug("Invio messaggio di sblocco a RicezioneBuste...");
					msgResponse = ejbUtils.sendSbloccoRicezioneBuste(richiestaApplicativa.getIdModuloInAttesa());
				}

				// spedizioneErrore  (se la validazione e' disattivata)
				msgDiag.mediumDebug("Invio eventuale messaggio di errore al servizio applicativo (gestione errore)...");
				Eccezione eccezioneDaInviareServizioApplicativo = 
						Eccezione.getEccezioneValidazione(ErroriCooperazione.ERRORE_GENERICO_PROTOCOLLO_NON_CORRETTO.getErroreCooperazione(), protocolFactory);
				ErroreIntegrazione erroreIntegrazioneDaInviareServizioApplicativo = null;
				if(msgErroreProtocolloValido){
					if(bustaRichiesta.sizeListaEccezioni()>1){
						StringBuffer bfDescrizione = new StringBuffer();
						for(int k=0; k<bustaRichiesta.sizeListaEccezioni();k++){
							Eccezione eccLista = bustaRichiesta.getEccezione(k);
							if(eccLista.getDescrizione(protocolFactory)!=null)
								bfDescrizione.append("["+traduttore.toString(eccLista.getCodiceEccezione(),eccLista.getSubCodiceEccezione())+"] "+eccLista.getDescrizione(protocolFactory)+"\n");
						}
						if(bfDescrizione.length()>0)
							eccezioneDaInviareServizioApplicativo.setDescrizione(bfDescrizione.toString());
					}	else {
						if(bustaRichiesta.sizeListaEccezioni()==1){
							eccezioneDaInviareServizioApplicativo = bustaRichiesta.getEccezione(0);
						}else{
							if(dettaglioEccezione!=null && dettaglioEccezione.getEccezioni()!=null && dettaglioEccezione.getEccezioni().sizeEccezioneList()>0){
								org.openspcoop2.core.eccezione.details.Eccezione e = dettaglioEccezione.getEccezioni().getEccezione(0);
								if(org.openspcoop2.core.eccezione.details.constants.Costanti.TIPO_ECCEZIONE_PROTOCOLLO.equals(e.getTipo())){
									ErroreCooperazione msgErroreCooperazione =
											new ErroreCooperazione(e.getDescrizione(), traduttore.toCodiceErroreCooperazione(e.getCodice()));
									eccezioneDaInviareServizioApplicativo = new Eccezione(msgErroreCooperazione, false, Sbustamento.ID_MODULO, protocolFactory);
								}else{
//									erroreIntegrazioneDaInviareServizioApplicativo = 
//											new ErroreIntegrazione(e.getDescrizione(), traduttore.toCodiceErroreIntegrazione(e.getCodiceAAA(), propertiesReader.getProprietaGestioneErrorePD(protocolManager).getFaultPrefixCode()));
									// Se e' arrivato un details di errore di integrazione, comunque genero una busta di errore di processamento, visto che di fatto ho ricevuto questa.
									eccezioneDaInviareServizioApplicativo = Eccezione.
											getEccezioneProcessamento(ErroriCooperazione.ERRORE_GENERICO_PROCESSAMENTO_MESSAGGIO.getErroreProcessamento(e.getDescrizione()), protocolFactory);
								}
							}else{
								eccezioneDaInviareServizioApplicativo = 
										new Eccezione(ErroriCooperazione.ERRORE_GENERICO_PROCESSAMENTO_MESSAGGIO.getErroreCooperazione(), 
												false, Sbustamento.ID_MODULO, protocolFactory);
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

				gestioneErroreProtocollo(configurazionePdDManager,ejbUtils,profiloCollaborazione,repositoryBuste,
						bustaRichiesta,identitaPdD,eccezioneDaInviareServizioApplicativo,erroreIntegrazioneDaInviareServizioApplicativo,
						new IDSoggetto(bustaRichiesta.getTipoMittente(),bustaRichiesta.getMittente()),
						dettaglioEccezione, versioneSoap,protocolManager);

				// Commit modifiche
				openspcoopstate.commit();
				
				msgDiag.mediumDebug("Rilascio connessione...");
				openspcoopstate.releaseResource();
				esito.setEsitoInvocazione(true);
				esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,"RicevutoMsgBustaErrore");
				return esito;
			}













			/*   -------------- Validazione: Gestione messaggio di protocollo  -----------------  */ 
			else if(isMessaggioErroreProtocollo == false){
				if(configurazionePdDManager.getTipoValidazione(implementazionePdDMittente).equals(CostantiConfigurazione.STATO_CON_WARNING_DISABILITATO) == false){

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
						if( configurazionePdDManager.getTipoValidazione(implementazionePdDMittente).equals(CostantiConfigurazione.STATO_CON_WARNING_ABILITATO) == true) {

							// bustaOriginale per gestione errore
							Busta bustaNonValida = bustaRichiesta.clone();

							// Spedisco un Errore ad un mittente conosciuto...
							ejbUtils.setRollbackRichiestaInCasoErrore(false); // viene effettuato da gestioneErrore
							if(mittenteRegistrato){
								msgDiag.mediumDebug("Invio segnalazione di errore ...");
								ejbUtils.sendAsRispostaBustaErroreValidazione(richiestaApplicativa.getIdModuloInAttesa(),bustaRichiesta,errorsClone,
										idCorrelazioneApplicativa,null,servizioApplicativoFruitore);
							}
							else{
								if(sendSbloccoRicezioneBuste){
									msgDiag.mediumDebug("Invio messaggio di sblocco a RicezioneBuste...");
									// se il mittente non e' conosciuto sblocco solo Ricezione
									msgResponse = ejbUtils.sendSbloccoRicezioneBuste(richiestaApplicativa.getIdModuloInAttesa());
								}
							}
							ejbUtils.setRollbackRichiestaInCasoErrore(true); // viene effettuato da gestioneErrore

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

							// GESTIONE ERRORE INTERNO
							msgDiag.mediumDebug("Invio eventuale messaggio di errore al servizio applicativo (gestione errore)...");
							gestioneErroreProtocollo(configurazionePdDManager, ejbUtils, profiloCollaborazione, repositoryBuste,
									bustaNonValida, identitaPdD, eccezioneDaInviareServizioApplicativo,null,
									identitaPdD, null, versioneSoap,protocolManager);

							// Commit modifiche
							openspcoopstate.commit();
							
							msgDiag.mediumDebug("Rilascio connessione...");
							openspcoopstate.releaseResource();
							esito.setEsitoInvocazione(true); 
							esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,
									msgDiag.getMessaggio_replaceKeywords("validazioneBusta.bustaNonCorretta"));
							return esito;
						}
					}
				}
			}







			/*  Stampa dei messaggi di errore non GRAVI (frammento di codice raggiunto SOLO se profilo=LineeGuida1.1) ---------------- */
			// Stampa di eventuali eccezioni NON GRAVI presenti nella busta
			if( (bustaRichiesta.containsEccezioniGravi()==false) && (bustaRichiesta.sizeListaEccezioni()>0) ){
				msgDiag.addKeyword(CostantiPdD.KEY_ECCEZIONI, bustaRichiesta.toStringListaEccezioni(protocolFactory));
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
				if(bustaRichiesta.sizeListaRiscontri() > 0){ 
					for(int i=0;i<bustaRichiesta.sizeListaRiscontri();i++){  
						Riscontro r = bustaRichiesta.getRiscontro(i);
						
						msgDiag.addKeyword(CostantiPdD.KEY_ID_BUSTA_RISCONTRATA, r.getID());
						msgDiag.addKeyword(CostantiPdD.KEY_DATA_RISCONTRO, r.getOraRegistrazione().toString());
						msgDiag.logPersonalizzato("ricezioneRiscontro");
						try{
							GestoreMessaggi msgRiscontrato = new GestoreMessaggi(openspcoopstate,true, r.getID(),Costanti.OUTBOX,msgDiag,pddContext);
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
				existsSoggetto = configurazionePdDManager.existsSoggetto(idServizio.getSoggettoErogatore());
			}catch(Exception e){
				if(idServizio!=null && idServizio.getSoggettoErogatore()!=null)
					msgDiag.logErroreGenerico(e,"existsSoggetto("+idServizio.getSoggettoErogatore().toString()+")");
				else
					msgDiag.logErroreGenerico(e,"existsSoggetto()");
				ejbUtils.sendAsRispostaBustaErroreProcessamento(richiestaApplicativa.getIdModuloInAttesa(),bustaRichiesta,
						ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
							get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_536_CONFIGURAZIONE_NON_DISPONIBILE),
						idCorrelazioneApplicativa,servizioApplicativoFruitore,e,
						null);
				openspcoopstate.releaseResource();
				esito.setEsitoInvocazione(true);    
				esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,
						"existsSoggetto()"); 
				return esito;
			}
			boolean isSoggettoVirtuale = configurazionePdDManager.isSoggettoVirtuale(idServizio.getSoggettoErogatore());
			if(!existsSoggetto && !isSoggettoVirtuale){
				msgDiag.logPersonalizzato("soggettoDestinatarioNonGestito");
				ejbUtils.sendAsRispostaBustaErroreProcessamento(richiestaApplicativa.getIdModuloInAttesa(),bustaRichiesta,
						ErroriIntegrazione.ERRORE_451_SOGGETTO_INESISTENTE.getErroreIntegrazione(),
						idCorrelazioneApplicativa,servizioApplicativoFruitore,null,
						null);
				openspcoopstate.releaseResource();
				esito.setEsitoInvocazione(true);    
				esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,
						msgDiag.getMessaggio_replaceKeywords("soggettoDestinatarioNonGestito")); 
				return esito;
			}






			/* ------------------- 
			 *	Controllo se la busta porta Dati in caso ad es. di riscontri ... arrivera' una busta senza dati, 
                         e senza profilo di collaborazione.  o comunque non devono essere considerati	--------------------- */
			if(bustaDiServizio == true) { 

				msgDiag.logPersonalizzato("ricezioneBustaServizio");

				// Send Sblocco a RicezioneBuste
				if(sendSbloccoRicezioneBuste){
					msgResponse = ejbUtils.sendSbloccoRicezioneBuste(richiestaApplicativa.getIdModuloInAttesa());
				}
				ejbUtils.releaseInboxMessage(false);
				openspcoopstate.releaseResource();
				esito.setEsitoInvocazione(true);    
				esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,
						msgDiag.getMessaggio_replaceKeywords("ricezioneBustaServizio")); 
				return esito;
			} 





			/* ------------ 
			 * Dichiarazione Busta per HTTP Reply, utilizzata SOLO se il profilo e' asincrono  o oneway.
			 * Contiene la dichiarazione di eventuali:
			 * 1) Riscontri da ritornare
			 * 2) Ricevute da ritornare
			 * ------------- */
			// Variabile che sara' settata a true se si verifica una situazione in cui nella busta HTTPReply 
			// vengono impostati parametri di ritorno (ricevute,riscontri.....)
			boolean returnProtocolReply = false;		
			TipoOraRegistrazione tipoOraRegistrazione = this.propertiesReader.getTipoTempoBusta(implementazionePdDMittente);
			bustaHTTPReply = bustaRichiesta.invertiBusta(tipoOraRegistrazione,protocolFactory.createTraduttore().toString(tipoOraRegistrazione));






			
			
			
			
			
			
			
			/* ------------  Check funzionalita' richieste nel registro dei servizi che combacino con quelle presente nella busta ------------- */
			// Profilo di collaborazione: controllato durante la validazione
			// Filtro duplicati: controllato dal blocco di codice seguente, e casomai forzato
			// Consegna in ordine: se la busta non la possiede, ma e' richiesta viene sollevata una eccezione
			// Conferma ricezione: se la busta non la possiede, ma e' richiesta viene sollevata una eccezione
			
			if( org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.ONEWAY.equals(bustaRichiesta.getProfiloDiCollaborazione()) ){
				
				// ConfermaRicezione check
				if((this.propertiesReader.isCheckFromRegistroConfermaRicezioneAbilitato(implementazionePdDMittente) && sbustamentoMsg.isConfermaRicezioneRichiestoAccordo() &&
						(bustaRichiesta.isConfermaRicezione()==false)) && !consegnaAffidabile){
					msgDiag.logPersonalizzato("funzionalitaRichiestaAccordo.confermaRicezioneNonPresente");
					Eccezione e = Eccezione.getEccezioneValidazione(ErroriCooperazione.PROFILO_TRASMISSIONE_CONFERMA_RICEZIONE_NON_PRESENTE.getErroreCooperazione(),protocolFactory);
					ejbUtils.sendAsRispostaBustaErroreValidazione(richiestaApplicativa.getIdModuloInAttesa(),bustaRichiesta,e,
							idCorrelazioneApplicativa,servizioApplicativoFruitore);
					openspcoopstate.releaseResource();
					esito.setEsitoInvocazione(true); 
					esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,
							msgDiag.getMessaggio_replaceKeywords("funzionalitaRichiestaAccordo.confermaRicezioneNonPresente"));
					return esito;
				}
				
				if((this.propertiesReader.isCheckFromRegistroConsegnaInOrdineAbilitato(implementazionePdDMittente) && sbustamentoMsg.isConsegnaOrdineRichiestoAccordo()) &&
						(bustaRichiesta.getSequenza()<0) ){
					msgDiag.logPersonalizzato("funzionalitaRichiestaAccordo.consegnaInOrdineNonPresente");
					Eccezione e = Eccezione.getEccezioneValidazione(ErroriCooperazione.CONSEGNA_IN_ORDINE_NON_GESTIBILE.getErroreCooperazione(),protocolFactory);
					ejbUtils.sendAsRispostaBustaErroreValidazione(richiestaApplicativa.getIdModuloInAttesa(),bustaRichiesta,e,
							idCorrelazioneApplicativa,servizioApplicativoFruitore);
					openspcoopstate.releaseResource();
					esito.setEsitoInvocazione(true); 
					esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,
							msgDiag.getMessaggio_replaceKeywords("funzionalitaRichiestaAccordo.consegnaInOrdineNonPresente"));
					return esito;
				}
			}
			
			
			
			
			
			
			









			/* ------------  Gestione Duplicati ------------- */
						
			boolean checkDuplicato = imbustamentoFiltroDuplicatiAbilitato ;			
			if(checkDuplicato){
										
				/* 
				 * 1) duplicato in caso di oneWay: se confermaRicezione=true e la gestione dei riscontri e' attiva, re-invio un riscontro
				 * 1b) duplicato in caso di oneWay: se confermaRicezione=false o cmq la gestione dei riscontri non è attiva, genero Errore se indicato da file property, altrimenti ritorno http 202
				 * 2) duplicati in caso sincrono: genero un msg Errore
				 * 3) duplicati in caso asincrono: rigenero la ricevuta
				 */
				try{
					
					// Istanzio gestore filtro duplicati
					IFiltroDuplicati gestoreFiltroDuplicati = getGestoreFiltroDuplicati(this.propertiesReader, this.loader, 
							openspcoopstate, pddContext, historyBuste, repositoryBuste, oneWayVersione11);
					boolean bustaDuplicata = gestoreFiltroDuplicati.isDuplicata(protocolFactory, bustaRichiesta.getID());
					
					// BUSTA GIA' PRECEDENTEMENTE RICEVUTA
					if (bustaDuplicata){
	
						// Aggiorno duplicati
						msgDiag.logPersonalizzato("ricezioneBustaDuplicata.count");
						gestoreFiltroDuplicati.incrementaNumeroDuplicati(protocolFactory,bustaRichiesta.getID());
							
						msgDiag.logPersonalizzato("ricezioneBustaDuplicata");
	
						// 1) 
						if( org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.ONEWAY.equals(bustaRichiesta.getProfiloDiCollaborazione()) && consegnaAffidabile){
							//System.out.println("CASO 1");
							msgDiag.logPersonalizzato(MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_BUSTE,"generazioneRiscontro");
	
							// Costruisco riscontro da re-inviare
							Riscontro r = new Riscontro();
							r.setID(bustaRichiesta.getID());
							r.setOraRegistrazione(bustaHTTPReply.getOraRegistrazione());
							r.setTipoOraRegistrazione(this.propertiesReader.getTipoTempoBusta(implementazionePdDMittente));
							bustaHTTPReply.setTipoServizioRichiedenteBustaDiServizio(bustaRichiesta.getTipoServizio());
							bustaHTTPReply.setServizioRichiedenteBustaDiServizio(bustaRichiesta.getServizio());
							bustaHTTPReply.setAzioneRichiedenteBustaDiServizio(bustaRichiesta.getAzione());
							bustaHTTPReply.addRiscontro(r);
	
							// Re-invio Riscontro
							msgResponse = ejbUtils.buildAndSendBustaRisposta(richiestaApplicativa.getIdModuloInAttesa(),bustaHTTPReply,
									SoapUtils.build_Soap_Empty(versioneSoap),profiloGestione,
									idCorrelazioneApplicativa,servizioApplicativoFruitore);
						}
						// 1b) 
						else if( org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.ONEWAY.equals(bustaRichiesta.getProfiloDiCollaborazione()) &&
								( (this.propertiesReader.isGestioneRiscontri(implementazionePdDMittente)==false) || (bustaRichiesta.isConfermaRicezione()==false) )  && 
								( protocolManager.isGenerazioneErroreMessaggioOnewayDuplicato() || this.propertiesReader.isGenerazioneErroreProtocolloFiltroDuplicati(implementazionePdDMittente))){
							//System.out.println("CASO 1b");
							ejbUtils.setRollbackRichiestaInCasoErrore_rollbackHistory(false); // non devo cancellare l'history
							ejbUtils.sendAsRispostaBustaErroreValidazione(richiestaApplicativa.getIdModuloInAttesa(),bustaRichiesta,
									Eccezione.getEccezioneValidazione(ErroriCooperazione.IDENTIFICATIVO_MESSAGGIO_GIA_PROCESSATO.getErroreCooperazione(),protocolFactory),
									idCorrelazioneApplicativa,servizioApplicativoFruitore);
							openspcoopstate.releaseResource();
							esito.setEsitoInvocazione(true); 
							esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,
									msgDiag.getMessaggio_replaceKeywords("ricezioneBustaDuplicata"));
							return esito;
						}
	
						// 2)
						else if(org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.SINCRONO.equals(bustaRichiesta.getProfiloDiCollaborazione())){
							//System.out.println("CASO 2");
							ejbUtils.setRollbackRichiestaInCasoErrore_rollbackHistory(false); // non devo cancellare l'history
							ejbUtils.sendAsRispostaBustaErroreValidazione(richiestaApplicativa.getIdModuloInAttesa(),bustaRichiesta,
									Eccezione.getEccezioneValidazione(ErroriCooperazione.IDENTIFICATIVO_MESSAGGIO_GIA_PROCESSATO.getErroreCooperazione(),protocolFactory),
									idCorrelazioneApplicativa,servizioApplicativoFruitore);
							openspcoopstate.releaseResource();
							esito.setEsitoInvocazione(true); 
							esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,
									msgDiag.getMessaggio_replaceKeywords("ricezioneBustaDuplicata"));
							return esito;
						}
	
						// 3
						else if(org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.ASINCRONO_SIMMETRICO.equals(bustaRichiesta.getProfiloDiCollaborazione()) ||
								org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.ASINCRONO_ASIMMETRICO.equals(bustaRichiesta.getProfiloDiCollaborazione())){
							String ricevuta = null;
							if(org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.ASINCRONO_SIMMETRICO.equals(bustaRichiesta.getProfiloDiCollaborazione())){
								//System.out.println("CASO 3 Asincrono Simmetrico");
								// Asincrono Simmetrico
						
								boolean ricevutaAbilitata = true; // Default SICA
								try{
									if( RuoloBusta.RISPOSTA.equals(ruoloBustaRicevuta.toString()) ){
										msgDiag.mediumDebug("Lettura Porta Delegata ...");
										String idAsincronoCorrelazioneRichiesta = bustaRichiesta.getRiferimentoMessaggio();
										if(idAsincronoCorrelazioneRichiesta==null){
											idAsincronoCorrelazioneRichiesta = bustaRichiesta.getCollaborazione();
										}
										Integrazione integrazione = repositoryBuste.getInfoIntegrazioneFromOutBox(idAsincronoCorrelazioneRichiesta);
										IDPortaDelegata idPD = new IDPortaDelegata();
										idPD.setLocationPD(integrazione.getLocationPD());
										idPD.setSoggettoFruitore(new IDSoggetto(bustaRichiesta.getTipoDestinatario(),bustaRichiesta.getDestinatario()));
										PortaDelegata pd = configurazionePdDManager.getPortaDelegata_SafeMethod(idPD);
										ricevutaAbilitata = configurazionePdDManager.ricevutaAsincronaSimmetricaAbilitata(pd);
									}else{
										msgDiag.mediumDebug("Lettura Porta Applicativa ...");
										PortaApplicativa pa = configurazionePdDManager.getPortaApplicativa_SafeMethod(richiestaApplicativa.getIdPAbyNome());
										ricevutaAbilitata = configurazionePdDManager.ricevutaAsincronaSimmetricaAbilitata(pa);
									}
								}catch(Exception e){
									this.log.error("Errore durante la comprensione della Porta (Delegata/Applicativa) associata alla busta: "+e.getMessage(),e);
								}
								
								if(ricevutaAbilitata==false){
									if(bustaRichiesta.getRiferimentoMessaggio()==null)
										ricevuta = "ricevuta di una richiesta asincrona simmetrica";
									else if( RuoloBusta.RISPOSTA.equals(ruoloBustaRicevuta.toString()) ){
										ricevuta = "ricevuta di una risposta asincrona simmetrica";
									}
								}else{
									//System.out.println("CASO 3 Asincrono Simmetrico con ricevuta sincrona");
									ejbUtils.setRollbackRichiestaInCasoErrore_rollbackHistory(false); // non devo cancellare l'history
									ejbUtils.sendAsRispostaBustaErroreValidazione(richiestaApplicativa.getIdModuloInAttesa(),bustaRichiesta,
											Eccezione.getEccezioneValidazione(ErroriCooperazione.IDENTIFICATIVO_MESSAGGIO_GIA_PROCESSATO.getErroreCooperazione(),protocolFactory),
											idCorrelazioneApplicativa,servizioApplicativoFruitore);
									openspcoopstate.releaseResource();
									esito.setEsitoInvocazione(true); 
									esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,
											msgDiag.getMessaggio_replaceKeywords("ricezioneBustaDuplicata")); 
									return esito;
								}
	
							}else{
								//System.out.println("CASO 3 Asincrono Asimmetrico");
								//	Asincrono Asimmetrico
								
								msgDiag.mediumDebug("Lettura Porta Applicativa ...");
								PortaApplicativa pa = configurazionePdDManager.getPortaApplicativa_SafeMethod(richiestaApplicativa.getIdPAbyNome());
								
								if(configurazionePdDManager.ricevutaAsincronaAsimmetricaAbilitata(pa)==false){
									if(bustaRichiesta.getRiferimentoMessaggio()==null){
										ricevuta = "ricevuta di una richiesta asincrona asimmetrica";
										// TODO ServizioCorrelato da impostare nella ricevuta asincrona
									}else if( RuoloBusta.RISPOSTA.equals(ruoloBustaRicevuta.toString()) ){
										ricevuta = "ricevuta di una risposta asincrona asimmetrica";
									}
								}else{
									//System.out.println("CASO 3 Asincrono Asimmetrico con ricevuta sincrona");
									ejbUtils.setRollbackRichiestaInCasoErrore_rollbackHistory(false); // non devo cancellare l'history
									ejbUtils.sendAsRispostaBustaErroreValidazione(richiestaApplicativa.getIdModuloInAttesa(),bustaRichiesta,
											Eccezione.getEccezioneValidazione(ErroriCooperazione.IDENTIFICATIVO_MESSAGGIO_GIA_PROCESSATO.getErroreCooperazione(),protocolFactory),
											idCorrelazioneApplicativa,servizioApplicativoFruitore);
									openspcoopstate.releaseResource();
									esito.setEsitoInvocazione(true); 
									esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,
											msgDiag.getMessaggio_replaceKeywords("ricezioneBustaDuplicata")); 
									return esito;
								}
							}
	
							if(ricevuta!=null){
								//System.out.println("CASO 3 creazione ricevuta");
								msgDiag.addKeyword(CostantiPdD.KEY_TIPO_RICEVUTA_ASINCRONA, ricevuta);
								msgDiag.logPersonalizzato(MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_BUSTE,"generazioneRicevutaAsincrona");
								
								// Costruisco ricevuta da re-inviare
								bustaHTTPReply.setVersioneServizio(bustaRichiesta.getVersioneServizio());
								bustaHTTPReply.setServizio(bustaRichiesta.getServizio());
								bustaHTTPReply.setTipoServizio(bustaRichiesta.getTipoServizio());
								bustaHTTPReply.setAzione(bustaRichiesta.getAzione());
								bustaHTTPReply.setProfiloDiCollaborazione(org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.ASINCRONO_SIMMETRICO);
								bustaHTTPReply.setRiferimentoMessaggio(bustaRichiesta.getID());
								// (per gli asincroni devono sempre essere presenti la collaborazione)
								if( this.propertiesReader.isGestioneElementoCollaborazione(implementazionePdDMittente))
									bustaHTTPReply.setCollaborazione(bustaRichiesta.getCollaborazione());	
								if(!consegnaAffidabile){
									bustaHTTPReply.setConfermaRicezione(false);
								}
								if(imbustamentoFiltroDuplicatiAbilitato){
									bustaHTTPReply.setInoltro(Inoltro.SENZA_DUPLICATI,protocolFactory.createTraduttore().toString(Inoltro.SENZA_DUPLICATI));
								}
								//	Re-invio Ricevuta
								msgResponse = ejbUtils.buildAndSendBustaRisposta(richiestaApplicativa.getIdModuloInAttesa(),bustaHTTPReply,
										SoapUtils.build_Soap_Empty(versioneSoap),profiloGestione,
										idCorrelazioneApplicativa,servizioApplicativoFruitore);	
							}
	
						}
	
						// 4
						else{
							//System.out.println("Caso 4");
							if( protocolManager.isGenerazioneErroreMessaggioOnewayDuplicato() || this.propertiesReader.isGenerazioneErroreProtocolloFiltroDuplicati(implementazionePdDMittente)){
								//System.out.println("Caso 4, spedizione errore di segnalazione id duplicato");
								ejbUtils.setRollbackRichiestaInCasoErrore_rollbackHistory(false); // non devo cancellare l'history
								ejbUtils.sendAsRispostaBustaErroreValidazione(richiestaApplicativa.getIdModuloInAttesa(),bustaRichiesta,
										Eccezione.getEccezioneValidazione(ErroriCooperazione.IDENTIFICATIVO_MESSAGGIO_GIA_PROCESSATO.getErroreCooperazione(),protocolFactory),
										idCorrelazioneApplicativa,servizioApplicativoFruitore);
								openspcoopstate.releaseResource();
								esito.setEsitoInvocazione(true); 
								esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,
										msgDiag.getMessaggio_replaceKeywords("ricezioneBustaDuplicata")); 
								return esito;
							}
	
							// Msg di Sblocco per il modulo 'RicezioneBuste'
							if(sendSbloccoRicezioneBuste){
								//System.out.println("Caso 4, solo segnalazione errore");
								msgResponse = ejbUtils.sendSbloccoRicezioneBuste(richiestaApplicativa.getIdModuloInAttesa());
							}
	
						} 
						//System.out.println("RELEASE");
						// Devo rilasciare il messaggio, senza cancellarlo dalla history.
						ejbUtils.releaseInboxMessage(false,true);
	
						// Aggiornamento cache messaggio risposta
						if(msgResponse!=null)
							msgResponse.addMessaggiIntoCache_readFromTable(Sbustamento.ID_MODULO, "risposta in seguito a busta duplicata");
	
						// Aggiornamento cache proprietario messaggio risposta
						if(msgResponse!=null)
							msgResponse.addProprietariIntoCache_readFromTable(Sbustamento.ID_MODULO, "risposta in seguito a busta duplicata",idMessageRequest,false);
	
						openspcoopstate.releaseResource();
						esito.setEsitoInvocazione(true);  
						esito.setStatoInvocazione(EsitoLib.OK,null); 
						return esito;
	
					}else { 
	
						// REGISTRAZIONE BUSTA RICEVUTA NELL'HISTORY
						gestoreFiltroDuplicati.registraBusta(protocolFactory, bustaRichiesta);
						msgDiag.logPersonalizzato("ricezioneBusta.registrazionePerFiltroDuplicati");
						
					}  
					
				}catch(Exception e) {
					msgDiag.logErroreGenerico(e, "GestioneHistoryBusteRicevute");
					ejbUtils.rollbackMessage("Gestione dell'history delle buste ricevute non riuscita", esito);
					openspcoopstate.releaseResource(); 
					esito.setEsitoInvocazione(false); 
					esito.setStatoInvocazioneErroreNonGestito(e); 
					return esito;
				} 
				
				pddContext.removeObject(org.openspcoop2.core.constants.Costanti.CONNECTION_PDD);
			}
			
			








			/* ------------  Invio Riscontri (solo per profilo OneWay) ------------- */
			if(consegnaAffidabile){
				msgDiag.mediumDebug("Gestione eventuali riscontri da inviare...");
				boolean senza_piggy_backing = true;
				if(org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.ONEWAY.equals(bustaRichiesta.getProfiloDiCollaborazione()) ) {	
					if ( (senza_piggy_backing) && (bustaRichiesta.isConfermaRicezione())){

						// Riscontro ad hoc
						Riscontro r = new Riscontro();
						r.setID(bustaRichiesta.getID());
						r.setOraRegistrazione(bustaHTTPReply.getOraRegistrazione());
						r.setTipoOraRegistrazione(this.propertiesReader.getTipoTempoBusta(implementazionePdDMittente));
						bustaHTTPReply.addRiscontro(r);
						bustaHTTPReply.setTipoServizioRichiedenteBustaDiServizio(bustaRichiesta.getTipoServizio());
						bustaHTTPReply.setServizioRichiedenteBustaDiServizio(bustaRichiesta.getServizio());
						bustaHTTPReply.setAzioneRichiedenteBustaDiServizio(bustaRichiesta.getAzione());

						returnProtocolReply = true;
					}
				}
			}









			/* ------------  Collaborazione e Sequenza  ------------- */
			// check Funzionalita' fornita solo con OneWay
			// Sequenza: deve essere abilitata la consegna affidabile + la collaborazione e infine la consegna in ordine
			if( consegnaInOrdine &&
					(org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.ONEWAY.equals(bustaRichiesta.getProfiloDiCollaborazione())==false)	){

				msgDiag.mediumDebug("Gestione vincoli consegna in ordine...");

				msgDiag.logPersonalizzato("consegnaInOrdine.profiloDiversoOneway");
				Eccezione e = Eccezione.getEccezioneValidazione(ErroriCooperazione.CONSEGNA_IN_ORDINE_NON_SUPPORTATA.getErroreCooperazione(),protocolFactory);
				ejbUtils.sendAsRispostaBustaErroreValidazione(richiestaApplicativa.getIdModuloInAttesa(),bustaRichiesta,e,
						idCorrelazioneApplicativa,servizioApplicativoFruitore);
				openspcoopstate.releaseResource();
				esito.setEsitoInvocazione(true); 
				esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,
						msgDiag.getMessaggio_replaceKeywords("consegnaInOrdine.profiloDiversoOneway"));
				return esito;
			}








			/* ------------  Profilo di Collaborazione  ------------- */
			String scenarioCooperazione = null;
			boolean generazioneMsgOK = false;
			boolean existsPA = true;
			PortaApplicativa pa = null;
			ServizioApplicativo sa = null;
			Integrazione integrazioneAsincrona = null;
			if(org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.ONEWAY.equals(bustaRichiesta.getProfiloDiCollaborazione())) {	

				msgDiag.mediumDebug("Gestione profilo di collaborazione OneWay...");

				scenarioCooperazione = Costanti.SCENARIO_ONEWAY_INVOCAZIONE_SERVIZIO;

				//	check esistenza porta applicativa
				msgDiag.mediumDebug("Gestione profilo di collaborazione OneWay (existsPA)...");
				try{
					existsPA=configurazionePdDManager.existsPA(richiestaApplicativa);
				}catch(Exception e){
					msgDiag.logErroreGenerico(e, "existsPA(richiestaApplicativa,oneway)");
					ejbUtils.sendAsRispostaBustaErroreProcessamento(richiestaApplicativa.getIdModuloInAttesa(),bustaRichiesta,
							ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
								get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_536_CONFIGURAZIONE_NON_DISPONIBILE),
							idCorrelazioneApplicativa,servizioApplicativoFruitore,e,
							null);
					openspcoopstate.releaseResource();
					esito.setEsitoInvocazione(true); 
					esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,"existsPA(richiestaApplicativa,oneway)");
					return esito;
				}
				if(!existsPA){
					msgDiag.logPersonalizzato("portaApplicativaNonEsistente");
					ejbUtils.sendAsRispostaBustaErroreProcessamento(richiestaApplicativa.getIdModuloInAttesa(),bustaRichiesta,
							ErroriIntegrazione.ERRORE_450_PA_INESISTENTE.getErroreIntegrazione(),
							idCorrelazioneApplicativa,servizioApplicativoFruitore,null,
							null);
					openspcoopstate.releaseResource();
					esito.setEsitoInvocazione(true);
					esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,
							msgDiag.getMessaggio_replaceKeywords("portaApplicativaNonEsistente"));
					return esito;
				}
				
				// Lettura Porta Applicativa
				msgDiag.mediumDebug("Lettura Porta Applicativa ...");
				pa = configurazionePdDManager.getPortaApplicativa_SafeMethod(richiestaApplicativa.getIdPAbyNome());
				
				// Modalita di trasmissione sincrona/asincrona
				if(configurazionePdDManager.isModalitaStateless(pa, bustaRichiesta.getProfiloDiCollaborazione())==false){
					generazioneMsgOK = true;
				}else{
					generazioneMsgOK = false;
				}

			}else if(org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.SINCRONO.equals(bustaRichiesta.getProfiloDiCollaborazione())) {	

				msgDiag.mediumDebug("Gestione profilo di collaborazione Sincrono...");

				if(bustaRichiesta.getRiferimentoMessaggio()!=null){
					msgDiag.logPersonalizzato("ricezioneRispostaSincrona");
					ejbUtils.sendAsRispostaBustaErroreProcessamento(richiestaApplicativa.getIdModuloInAttesa(),bustaRichiesta,
							ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
								get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_525_GESTIONE_FUNZIONALITA_PROTOCOLLO),
							idCorrelazioneApplicativa,servizioApplicativoFruitore,null,
							null);
					openspcoopstate.releaseResource();
					esito.setEsitoInvocazione(true); 
					esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,
							msgDiag.getMessaggio_replaceKeywords("ricezioneRispostaSincrona"));
					return esito;
				}

				scenarioCooperazione = Costanti.SCENARIO_SINCRONO_INVOCAZIONE_SERVIZIO;

				//	check esistenza porta applicativa
				msgDiag.mediumDebug("Gestione profilo di collaborazione Sincrono (existsPA)...");
				try{
					existsPA=configurazionePdDManager.existsPA(richiestaApplicativa);
				}catch(Exception e){
					msgDiag.logErroreGenerico(e, "existsPA(richiestaApplicativa,sincrono)");
					ejbUtils.sendAsRispostaBustaErroreProcessamento(richiestaApplicativa.getIdModuloInAttesa(),bustaRichiesta,
							ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
								get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_536_CONFIGURAZIONE_NON_DISPONIBILE),
							idCorrelazioneApplicativa,servizioApplicativoFruitore,e,
							null);
					openspcoopstate.releaseResource();
					esito.setEsitoInvocazione(true); 
					esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,"existsPA(richiestaApplicativa,sincrono)");
					return esito;
				}
				if(!existsPA){
					msgDiag.logPersonalizzato("portaApplicativaNonEsistente");
					ejbUtils.sendAsRispostaBustaErroreProcessamento(richiestaApplicativa.getIdModuloInAttesa(),bustaRichiesta,
							ErroriIntegrazione.ERRORE_450_PA_INESISTENTE.getErroreIntegrazione(),
							idCorrelazioneApplicativa,servizioApplicativoFruitore,null,
							null);
					openspcoopstate.releaseResource();
					esito.setEsitoInvocazione(true); 
					esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,
							msgDiag.getMessaggio_replaceKeywords("portaApplicativaNonEsistente"));
					return esito;
				}

				// Lettura Porta Applicativa
				msgDiag.mediumDebug("Lettura Porta Applicativa ...");
				pa = configurazionePdDManager.getPortaApplicativa_SafeMethod(richiestaApplicativa.getIdPAbyNome());
				
				msgDiag.mediumDebug("Gestione profilo di collaborazione Sincrono (registra busta ricevuta)...");
				try{
					profiloCollaborazione.registraBustaRicevuta(bustaRichiesta.getID());
				}catch(Exception e){
					msgDiag.logErroreGenerico(e, "profiloCollaborazione.registraBustaRicevuta("+bustaRichiesta.getID()+")");
					ejbUtils.sendAsRispostaBustaErroreProcessamento(richiestaApplicativa.getIdModuloInAttesa(),bustaRichiesta,
							ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
								get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_525_GESTIONE_FUNZIONALITA_PROTOCOLLO),
							idCorrelazioneApplicativa,servizioApplicativoFruitore,e,
							null);
					openspcoopstate.releaseResource();
					esito.setEsitoInvocazione(true); 
					esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,"profiloCollaborazione.registraBustaRicevuta("+bustaRichiesta.getID()+")");
					return esito;
				} 

			}else if(org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.ASINCRONO_SIMMETRICO.equals(bustaRichiesta.getProfiloDiCollaborazione())) {	

				// Richiesta Asincrona
				if(RuoloBusta.RICHIESTA.equals(ruoloBustaRicevuta.toString())){

					msgDiag.mediumDebug("Gestione profilo di collaborazione AsincronoSimmetrico richiesta...");

					scenarioCooperazione = Costanti.SCENARIO_ASINCRONO_SIMMETRICO_INVOCAZIONE_SERVIZIO;

					//	check esistenza porta applicativa
					msgDiag.mediumDebug("Gestione profilo di collaborazione AsincronoSimmetrico richiesta (existsPA)...");
					try{
						existsPA=configurazionePdDManager.existsPA(richiestaApplicativa);
					}catch(Exception e){
						msgDiag.logErroreGenerico(e, "existsPA(richiestaApplicativa,asincronoSimmetricoRichiesta)");
						ejbUtils.sendAsRispostaBustaErroreProcessamento(richiestaApplicativa.getIdModuloInAttesa(),bustaRichiesta,
								ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
									get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_536_CONFIGURAZIONE_NON_DISPONIBILE),
								idCorrelazioneApplicativa,servizioApplicativoFruitore,e,
								null);
						openspcoopstate.releaseResource();
						esito.setEsitoInvocazione(true); 
						esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,"existsPA(richiestaApplicativa,asincronoSimmetricoRichiesta)"); 
						return esito;
					}
					if(!existsPA){
						msgDiag.logPersonalizzato("portaApplicativaNonEsistente");
						ejbUtils.sendAsRispostaBustaErroreProcessamento(richiestaApplicativa.getIdModuloInAttesa(),bustaRichiesta,
								ErroriIntegrazione.ERRORE_450_PA_INESISTENTE.getErroreIntegrazione(),
								idCorrelazioneApplicativa,servizioApplicativoFruitore,null,
								null);
						openspcoopstate.releaseResource();
						esito.setEsitoInvocazione(true); 
						esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,
								msgDiag.getMessaggio_replaceKeywords("portaApplicativaNonEsistente"));
						return esito;
					}

					
					// Lettura Porta Applicativa
					msgDiag.mediumDebug("Lettura Porta Applicativa ...");
					pa = configurazionePdDManager.getPortaApplicativa_SafeMethod(richiestaApplicativa.getIdPAbyNome());
					
					
					//	gestione ricevute asincrone
					msgDiag.mediumDebug("Gestione profilo di collaborazione AsincronoSimmetrico richiesta (controllo ricevuta abilitata)...");
					try{
						richiestaApplicativa.setRicevutaAsincrona(configurazionePdDManager.ricevutaAsincronaSimmetricaAbilitata(pa));
					}catch(Exception e){
						msgDiag.logErroreGenerico(e, "ricevutaAsincronaSimmetricaAbilitata(pa)");
						ejbUtils.sendAsRispostaBustaErroreProcessamento(richiestaApplicativa.getIdModuloInAttesa(),bustaRichiesta,
								ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
									get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_536_CONFIGURAZIONE_NON_DISPONIBILE),
								idCorrelazioneApplicativa,servizioApplicativoFruitore,e,
								null);
						openspcoopstate.releaseResource();
						esito.setEsitoInvocazione(true); 
						esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,"ricevutaAsincronaSimmetricaAbilitata(pa)"); 
						return esito;
					}

					//	deve essere generato un msg OK se non e' abilitata la funzionalita' di ricevuta asincrona
					// Modalita di trasmissione stateless/stateful
					if(configurazionePdDManager.isModalitaStateless(pa,bustaRichiesta.getProfiloDiCollaborazione())==false){
						generazioneMsgOK = !richiestaApplicativa.isRicevutaAsincrona();
					}
					else{
						generazioneMsgOK = false;
					}

					// registrazione
					msgDiag.mediumDebug("Gestione profilo di collaborazione AsincronoSimmetrico richiesta (registrazione busta ricevuta)...");
					try{
						String tipoServizioCorrelato = null;
						String servizioCorrelato = null;
						if(bustaRichiesta.getTipoServizioCorrelato()!=null &&
								bustaRichiesta.getServizioCorrelato() != null){
							tipoServizioCorrelato = bustaRichiesta.getTipoServizioCorrelato();
							servizioCorrelato = bustaRichiesta.getServizioCorrelato();
						}else{
							RisultatoValidazione validazione = registroServiziManager.validaServizio(idSoggettoFruitore,idServizio,null);
							if( (validazione==null) || (validazione.getServizioRegistrato()==false))
								throw new Exception("Servizio ["+idServizio.toString()+"] non esiste nel registro dei servizi");
							if( (validazione.getServizioCorrelato()==null) || (validazione.getTipoServizioCorrelato()==null) )
								throw new Exception("Servizio ["+idServizio.toString()+"] non possiede un servizio correlato associato");
							tipoServizioCorrelato = validazione.getTipoServizioCorrelato();
							servizioCorrelato = validazione.getServizioCorrelato();
						}

						String collaborazione = null;
						// (per gli asincroni devono sempre essere presenti)
						if(this.propertiesReader.isGestioneElementoCollaborazione(implementazionePdDMittente))
							collaborazione = bustaRichiesta.getCollaborazione();
						profiloCollaborazione.asincronoSimmetrico_registraRichiestaRicevuta(bustaRichiesta.getID(),collaborazione,
								tipoServizioCorrelato,servizioCorrelato,richiestaApplicativa.isRicevutaAsincrona(),
								this.propertiesReader.getRepositoryIntervalloScadenzaMessaggi());
					}catch(Exception e){
						msgDiag.logErroreGenerico(e, "SalvataggioInformazioniProfiloAsincronoSimmetrico");
						ejbUtils.sendAsRispostaBustaErroreProcessamento(richiestaApplicativa.getIdModuloInAttesa(),bustaRichiesta,
								ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
									get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_525_GESTIONE_FUNZIONALITA_PROTOCOLLO),
								idCorrelazioneApplicativa,servizioApplicativoFruitore,e,
								null);
						openspcoopstate.releaseResource();
						esito.setEsitoInvocazione(true); 
						esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,"SalvataggioInformazioniProfiloAsincronoSimmetrico"); 
						return esito;
					} 

					// RicevutaAsincrona immediata
					if(generazioneMsgOK){
						returnProtocolReply = true;
						bustaHTTPReply.setServizio(bustaRichiesta.getServizio());
						bustaHTTPReply.setTipoServizio(bustaRichiesta.getTipoServizio());
						bustaHTTPReply.setAzione(bustaRichiesta.getAzione());
						bustaHTTPReply.setProfiloDiCollaborazione(org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.ASINCRONO_SIMMETRICO);
						bustaHTTPReply.setRiferimentoMessaggio(bustaRichiesta.getID());
						// (per gli asincroni devono sempre essere presenti)
						if(this.propertiesReader.isGestioneElementoCollaborazione(implementazionePdDMittente))
							bustaHTTPReply.setCollaborazione(bustaRichiesta.getCollaborazione());
						if(!consegnaAffidabile){
							bustaHTTPReply.setConfermaRicezione(false);
						}
						if(imbustamentoFiltroDuplicatiAbilitato){
							bustaHTTPReply.setInoltro(Inoltro.SENZA_DUPLICATI,protocolFactory.createTraduttore().toString(Inoltro.SENZA_DUPLICATI));
						}
					}
				}else{

					// Risposta Asincrona
					if(RuoloBusta.RISPOSTA.equals(ruoloBustaRicevuta.toString())){

						msgDiag.mediumDebug("Gestione profilo di collaborazione AsincronoSimmetrico risposta...");
						scenarioCooperazione = Costanti.SCENARIO_ASINCRONO_SIMMETRICO_CONSEGNA_RISPOSTA;

					}
					//	Ricevuta alla richiesta.
					else if(RuoloBusta.RICEVUTA_RICHIESTA.equals(ruoloBustaRicevuta.toString())){

						msgDiag.mediumDebug("Gestione profilo di collaborazione AsincronoSimmetrico ricevuta richiesta asincrona...");
						ricevutaAsincrona = "ricevuta di una richiesta asincrona simmetrica";

					}
					//	Ricevuta alla risposta.
					else if(RuoloBusta.RICEVUTA_RISPOSTA.equals(ruoloBustaRicevuta.toString())){

						msgDiag.mediumDebug("Gestione profilo di collaborazione AsincronoSimmetrico ricevuta risposta asincrona ...");
						ricevutaAsincrona = "ricevuta di una risposta asincrona simmetrica";

					}
					else{

						msgDiag.logPersonalizzato("profiloAsincrono.flussoRicevutaRichiestaRispostaNonCorretto");
						ejbUtils.sendAsRispostaBustaErroreProcessamento(richiestaApplicativa.getIdModuloInAttesa(),bustaRichiesta,
								ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
									get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_525_GESTIONE_FUNZIONALITA_PROTOCOLLO),
								idCorrelazioneApplicativa,servizioApplicativoFruitore,null,
								null);
						openspcoopstate.releaseResource();
						esito.setEsitoInvocazione(true); 
						esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,
								msgDiag.getMessaggio_replaceKeywords("profiloAsincrono.flussoRicevutaRichiestaRispostaNonCorretto")); 
						return esito;

					}

					//	Raccolgo dati consegna per Risposta o Ricevute
					msgDiag.mediumDebug("Gestione profilo di collaborazione AsincronoSimmetrico risposta (raccolta dati per consegna risposta)...");
					try{
						if(ricevutaAsincrona==null){
							integrazioneAsincrona = profiloCollaborazione.asincronoSimmetrico_getDatiConsegnaRisposta(protocolManager.getIdCorrelazioneAsincrona(bustaRichiesta));
						}else{
							integrazioneAsincrona = profiloCollaborazione.asincronoSimmetrico_getDatiConsegnaRicevuta(bustaRichiesta.getRiferimentoMessaggio());
						}
					}catch(Exception e){
						msgDiag.logErroreGenerico(e, "RaccoltaDatiIntegrazioneProfiloAsincronoSimmetrico");
						ejbUtils.sendAsRispostaBustaErroreProcessamento(richiestaApplicativa.getIdModuloInAttesa(),bustaRichiesta,
								ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
									get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_525_GESTIONE_FUNZIONALITA_PROTOCOLLO),
								idCorrelazioneApplicativa,servizioApplicativoFruitore,e,
								null);
						openspcoopstate.releaseResource();
						esito.setEsitoInvocazione(true); 
						esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,"RaccoltaDatiIntegrazioneProfiloAsincronoSimmetrico"); 
						return esito;
					} 

				}	

			} else if(org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.ASINCRONO_ASIMMETRICO.equals(bustaRichiesta.getProfiloDiCollaborazione())) {	
				//	Richiesta Asincrona
				if(RuoloBusta.RICHIESTA.equals(ruoloBustaRicevuta.toString())){
					msgDiag.mediumDebug("Gestione profilo di collaborazione AsincronoAsimmetrico richiesta...");

					scenarioCooperazione = Costanti.SCENARIO_ASINCRONO_ASIMMETRICO_INVOCAZIONE_SERVIZIO;

					//	check esistenza porta applicativa
					msgDiag.mediumDebug("Gestione profilo di collaborazione AsincronoAsimmetrico richiesta (existsPA)...");
					try{
						existsPA=configurazionePdDManager.existsPA(richiestaApplicativa);
					}catch(Exception e){
						msgDiag.logErroreGenerico(e, "existsPA(richiestaApplicativa,asincronoAsimmetricoRichiesta)");
						ejbUtils.sendAsRispostaBustaErroreProcessamento(richiestaApplicativa.getIdModuloInAttesa(),bustaRichiesta,
								ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
									get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_536_CONFIGURAZIONE_NON_DISPONIBILE),
								idCorrelazioneApplicativa,servizioApplicativoFruitore,e,
								null);
						openspcoopstate.releaseResource();
						esito.setEsitoInvocazione(true); 
						esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,"existsPA(richiestaApplicativa,asincronoAsimmetricoRichiesta)"); 
						return esito;
					}
					if(!existsPA){
						msgDiag.logPersonalizzato("(Richiesta) "+msgDiag.getMessaggio("portaApplicativaNonEsistente"), 
								msgDiag.getLivello("portaApplicativaNonEsistente"),
								msgDiag.getCodice("portaApplicativaNonEsistente"));
						ejbUtils.sendAsRispostaBustaErroreProcessamento(richiestaApplicativa.getIdModuloInAttesa(),bustaRichiesta,
								ErroriIntegrazione.ERRORE_450_PA_INESISTENTE.getErroreIntegrazione(),
								idCorrelazioneApplicativa,servizioApplicativoFruitore,null,
								null);
						openspcoopstate.releaseResource();
						esito.setEsitoInvocazione(true);
						esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,
								"(Richiesta) "+msgDiag.getMessaggio_replaceKeywords("portaApplicativaNonEsistente")); 
						return esito;
					}

					// Lettura Porta Applicativa
					msgDiag.mediumDebug("Lettura Porta Applicativa ...");
					pa = configurazionePdDManager.getPortaApplicativa_SafeMethod(richiestaApplicativa.getIdPAbyNome());
					
					// assegnamento servizioApplicativo
					msgDiag.mediumDebug("Gestione profilo di collaborazione AsincronoAsimmetrico richiesta (lettura servizio applicativo)...");
					String [] servizioApplicativo = null;
					try{
						servizioApplicativo = configurazionePdDManager.getServiziApplicativi(pa);
						if(servizioApplicativo==null)
							throw new Exception("Servizio applicativo non esistente");
						if(servizioApplicativo.length>1)
							throw new Exception("profilo non utilizzabile con una porta applicativa a cui sono stati associati piu' servizi applicativi");
						if(servizioApplicativo.length<=0)
							throw new Exception("Non sono stati associati servizi applicativi alla porta applicativa");
					}
					catch(Exception e){
						msgDiag.logErroreGenerico(e, "LetturaServizioApplicatiProfiloAsincronoAsimmetrico");
						ejbUtils.sendAsRispostaBustaErroreProcessamento(richiestaApplicativa.getIdModuloInAttesa(),bustaRichiesta,
								ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
									get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_525_GESTIONE_FUNZIONALITA_PROTOCOLLO),
								idCorrelazioneApplicativa,servizioApplicativoFruitore,e,
								null);
						openspcoopstate.releaseResource();
						esito.setEsitoInvocazione(true);  
						esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,"LetturaServizioApplicatiProfiloAsincronoAsimmetrico"); 
						return esito;
					}
					richiestaApplicativa.setServizioApplicativo(servizioApplicativo[0]);


					//	Lettura Servizio Applicativo
					msgDiag.mediumDebug("Lettura Servizio Applicativo ...");
					try{
						sa = configurazionePdDManager.getServizioApplicativo(richiestaApplicativa.getIdPA(), servizioApplicativo[0]);
					}catch(DriverConfigurazioneNotFound e){
						msgDiag.logErroreGenerico("Servizio applicativo ["+servizioApplicativo[0]+"] non esistente", "getServizioApplicativoProfiloAsincronoAsimmetrico");
						ejbUtils.sendAsRispostaBustaErroreProcessamento(richiestaApplicativa.getIdModuloInAttesa(),bustaRichiesta,
								ErroriIntegrazione.ERRORE_453_SA_INESISTENTE.getErroreIntegrazione(),
								idCorrelazioneApplicativa,servizioApplicativoFruitore,e,
								null);
						openspcoopstate.releaseResource();
						esito.setEsitoInvocazione(true); 
						esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,"Servizio applicativo ["+servizioApplicativo[0]+"] non esistente"); 
						return esito;
					}
					catch(Exception e){
						msgDiag.logErroreGenerico(e, "getServizioApplicativoProfiloAsincronoAsimmetrico");
						ejbUtils.sendAsRispostaBustaErroreProcessamento(richiestaApplicativa.getIdModuloInAttesa(),bustaRichiesta,
								ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
									get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_536_CONFIGURAZIONE_NON_DISPONIBILE),
								idCorrelazioneApplicativa,servizioApplicativoFruitore,e,
								null);
						openspcoopstate.releaseResource();
						esito.setEsitoInvocazione(true); 
						esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,"getServizioApplicativoProfiloAsincronoAsimmetrico"); 
						return esito;
					}
					
					
					// check esistenza ConsegnaRispostaAsincrona
					msgDiag.mediumDebug("Gestione profilo di collaborazione AsincronoAsimmetrico richiesta (exists ConsegnaRispostAsincrona)...");
					boolean existsConsegnaRispostaAsincrona = false;
					msgDiag.addKeyword(CostantiPdD.KEY_SA_EROGATORE, sa.getNome());
					try{
						existsConsegnaRispostaAsincrona = configurazionePdDManager.existsConsegnaRispostaAsincrona(sa);
					}catch(Exception e){
						msgDiag.logErroreGenerico(e, "existsConsegnaRispostaAsincrona_ProfiloAsincronoAsimmetrico");
						ejbUtils.sendAsRispostaBustaErroreProcessamento(richiestaApplicativa.getIdModuloInAttesa(),bustaRichiesta,
								ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_536_CONFIGURAZIONE_NON_DISPONIBILE),
								idCorrelazioneApplicativa,servizioApplicativoFruitore,e,
								null);
						openspcoopstate.releaseResource();
						esito.setEsitoInvocazione(true); 
						esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,"existsConsegnaRispostaAsincrona_ProfiloAsincronoAsimmetrico"); 
						return esito;
					}
					if (existsConsegnaRispostaAsincrona == false){
						msgDiag.logPersonalizzato("profiloAsincronoAsimmetrico.saSenzaRispostaAsincrona");
						ejbUtils.sendAsRispostaBustaErroreProcessamento(richiestaApplicativa.getIdModuloInAttesa(),bustaRichiesta,
								ErroriIntegrazione.ERRORE_411_RICEZIONE_CONTENUTI_ASINCRONA_RICHIESTA.getErroreIntegrazione(),
								idCorrelazioneApplicativa,servizioApplicativoFruitore,null,
								null);
						openspcoopstate.releaseResource();
						esito.setEsitoInvocazione(true); 
						esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,
								msgDiag.getMessaggio_replaceKeywords("profiloAsincronoAsimmetrico.saSenzaRispostaAsincrona")); 
						return esito;
					}

					// check esistenza ServizioCorrelato associato al Servizio
					msgDiag.mediumDebug("Gestione profilo di collaborazione AsincronoAsimmetrico richiesta (servizio correlato)...");
					if(sbustamentoMsg.getServizioCorrelato()==null || sbustamentoMsg.getTipoServizioCorrelato()==null){
						msgDiag.logPersonalizzato("profiloAsincronoAsimmetrico.servizioCorrelatoNonEsistente");
						ejbUtils.sendAsRispostaBustaErroreProcessamento(richiestaApplicativa.getIdModuloInAttesa(),bustaRichiesta,
								ErroriIntegrazione.ERRORE_408_SERVIZIO_CORRELATO_NON_TROVATO.getErroreIntegrazione(),
								idCorrelazioneApplicativa,servizioApplicativoFruitore,null,
								null);
						openspcoopstate.releaseResource();
						esito.setEsitoInvocazione(true); 
						esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,
								msgDiag.getMessaggio_replaceKeywords("profiloAsincronoAsimmetrico.servizioCorrelatoNonEsistente")); 
						return esito;
					}

					//	gestione ricevute asincrone
					msgDiag.mediumDebug("Gestione profilo di collaborazione AsincronoAsimmetrico richiesta (ricevuta abilitata)...");
					try{
						richiestaApplicativa.setRicevutaAsincrona(configurazionePdDManager.ricevutaAsincronaAsimmetricaAbilitata(pa));
					}catch(Exception e){
						msgDiag.logErroreGenerico(e, "ricevutaAsincronaAsimmetricaAbilitata(pa)");
						ejbUtils.sendAsRispostaBustaErroreProcessamento(richiestaApplicativa.getIdModuloInAttesa(),bustaRichiesta,
								ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_536_CONFIGURAZIONE_NON_DISPONIBILE),
								idCorrelazioneApplicativa,servizioApplicativoFruitore,e,
								null);
						openspcoopstate.releaseResource();
						esito.setEsitoInvocazione(true); 
						esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,"ricevutaAsincronaAsimmetricaAbilitata(pa)"); 
						return esito;
					}

					// deve essere generato un msg OK se non e' abilitata la funzionalita' di ricevuta asincrona
					// Modalita di trasmissione stateless/stateful
					if(configurazionePdDManager.isModalitaStateless(pa, bustaRichiesta.getProfiloDiCollaborazione())==false){
						generazioneMsgOK = !richiestaApplicativa.isRicevutaAsincrona();
					}
					else{
						generazioneMsgOK = false;
					}

					// registrazione
					msgDiag.mediumDebug("Gestione profilo di collaborazione AsincronoAsimmetrico richiesta (registrazione busta ricevuta)...");
					try{
						String collaborazione = null;
						// (per gli asincroni devono sempre essere presenti)
						if(this.propertiesReader.isGestioneElementoCollaborazione(implementazionePdDMittente))
							collaborazione = bustaRichiesta.getCollaborazione();
						profiloCollaborazione.asincronoAsimmetrico_registraRichiestaRicevuta(bustaRichiesta.getID(),collaborazione,
								sbustamentoMsg.getTipoServizioCorrelato(),sbustamentoMsg.getServizioCorrelato(),
								richiestaApplicativa.isRicevutaAsincrona(),
								this.propertiesReader.getRepositoryIntervalloScadenzaMessaggi());
					}catch(Exception e){
						msgDiag.logErroreGenerico(e, "SalvataggioInformazioniProfiloAsincronoAsimmetrico");
						ejbUtils.sendAsRispostaBustaErroreProcessamento(richiestaApplicativa.getIdModuloInAttesa(),bustaRichiesta,
								ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
									get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_525_GESTIONE_FUNZIONALITA_PROTOCOLLO),
								idCorrelazioneApplicativa,servizioApplicativoFruitore,e,
								null);
						openspcoopstate.releaseResource();
						esito.setEsitoInvocazione(true); 
						esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,"SalvataggioInformazioniProfiloAsincronoAsimmetrico"); 
						return esito;
					} 

					// RicevutaAsincrona immediata
					if(generazioneMsgOK){
						returnProtocolReply = true;
						bustaHTTPReply.setServizio(bustaRichiesta.getServizio());
						bustaHTTPReply.setTipoServizio(bustaRichiesta.getTipoServizio());

						// PRODUZIONE tipo e nome Servizio Correlato
						if( protocolManager.isGenerazioneInformazioniServizioCorrelatoAsincronoAsimmetrico() && 
								this.propertiesReader.isGenerazioneAttributiAsincroni(implementazionePdDMittente)){
							bustaHTTPReply.setServizioCorrelato(sbustamentoMsg.getServizioCorrelato());
							bustaHTTPReply.setTipoServizioCorrelato(sbustamentoMsg.getTipoServizioCorrelato());
						}

						bustaHTTPReply.setAzione(bustaRichiesta.getAzione());
						bustaHTTPReply.setProfiloDiCollaborazione(org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.ASINCRONO_ASIMMETRICO);
						bustaHTTPReply.setRiferimentoMessaggio(bustaRichiesta.getID());

						// (per gli asincroni devono sempre essere presenti)
						if(this.propertiesReader.isGestioneElementoCollaborazione(implementazionePdDMittente))
							bustaHTTPReply.setCollaborazione(bustaRichiesta.getCollaborazione());

						if(!consegnaAffidabile){
							bustaHTTPReply.setConfermaRicezione(false);
						}
						if(imbustamentoFiltroDuplicatiAbilitato){
							bustaHTTPReply.setInoltro(Inoltro.SENZA_DUPLICATI,protocolFactory.createTraduttore().toString(Inoltro.SENZA_DUPLICATI));	
						}
					}
				}else{

					// Risposta Asincrona
					if(RuoloBusta.RISPOSTA.equals(ruoloBustaRicevuta.toString())){

						msgDiag.mediumDebug("Gestione profilo di collaborazione AsincronoAsimmetrico richiesta Stato...");
						scenarioCooperazione = Costanti.SCENARIO_ASINCRONO_ASIMMETRICO_POLLING;

						IDServizio idServizioOriginale = null;
						msgDiag.mediumDebug("Gestione profilo di collaborazione AsincronoAsimmetrico richiesta Stato (lettura servizio originale)...");
						try{
							idServizioOriginale = profiloCollaborazione.asincronoAsimmetrico_getDatiConsegnaRisposta(protocolManager.getIdCorrelazioneAsincrona(bustaRichiesta));
						}catch(Exception e){
							msgDiag.logErroreGenerico(e, "profiloCollaborazione.asincronoAsimmetrico_getDatiConsegnaRisposta");
							ejbUtils.sendAsRispostaBustaErroreProcessamento(richiestaApplicativa.getIdModuloInAttesa(),bustaRichiesta,
									ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
										get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_525_GESTIONE_FUNZIONALITA_PROTOCOLLO),
									idCorrelazioneApplicativa,servizioApplicativoFruitore,e,
									null);
							openspcoopstate.releaseResource();
							esito.setEsitoInvocazione(true); 
							esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,"profiloCollaborazione.asincronoAsimmetrico_getDatiConsegnaRisposta"); 
							return esito;
						} 

						// ripristino richiestaApplicativa con valori del servizio di richiesta (sovrascrivendo i valori del servizio correlato)
						richiestaApplicativa.getIDServizio().setServizio(idServizioOriginale.getServizio());
						richiestaApplicativa.getIDServizio().setTipoServizio(idServizioOriginale.getTipoServizio());
						richiestaApplicativa.getIDServizio().setAzione(idServizioOriginale.getAzione());

						// check esistenza porta applicativa
						msgDiag.mediumDebug("Gestione profilo di collaborazione AsincronoSimmetrico richiesta Stato (existsPA)...");
						try{
							existsPA=configurazionePdDManager.existsPA(richiestaApplicativa);
						}catch(Exception e){
							msgDiag.logErroreGenerico(e, "existsPA(richiestaApplicativa,asincronoAsimmetricoRichiestaStato)");
							ejbUtils.sendAsRispostaBustaErroreProcessamento(richiestaApplicativa.getIdModuloInAttesa(),bustaRichiesta,
									ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
										get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_536_CONFIGURAZIONE_NON_DISPONIBILE),
									idCorrelazioneApplicativa,servizioApplicativoFruitore,e,
									null);
							openspcoopstate.releaseResource();
							esito.setEsitoInvocazione(true);  
							esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,"existsPA(richiestaApplicativa,asincronoAsimmetricoRichiestaStato)"); 
							return esito;
						}
						if(!existsPA){
							msgDiag.logPersonalizzato("(RichiestaStato) "+msgDiag.getMessaggio("portaApplicativaNonEsistente"), 
									msgDiag.getLivello("portaApplicativaNonEsistente"),
									msgDiag.getCodice("portaApplicativaNonEsistente"));
							ejbUtils.sendAsRispostaBustaErroreProcessamento(richiestaApplicativa.getIdModuloInAttesa(),bustaRichiesta,
									ErroriIntegrazione.ERRORE_450_PA_INESISTENTE.getErroreIntegrazione(),
									idCorrelazioneApplicativa,servizioApplicativoFruitore,null,
									null);
							openspcoopstate.releaseResource();
							esito.setEsitoInvocazione(true);  
							esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,
									"(RichiestaStato) "+msgDiag.getMessaggio_replaceKeywords("portaApplicativaNonEsistente")); 
							return esito;
						}
						
						
						// Lettura Porta Applicativa
						msgDiag.mediumDebug("Lettura Porta Applicativa ...");
						pa = configurazionePdDManager.getPortaApplicativa_SafeMethod(richiestaApplicativa.getIdPAbyNome());
						

					}
					//	Ricevuta alla richiesta.
					else if(RuoloBusta.RICEVUTA_RICHIESTA.equals(ruoloBustaRicevuta.toString())){

						msgDiag.mediumDebug("Gestione profilo di collaborazione AsincronoSimmetrico ricevuta richiesta...");
						ricevutaAsincrona = "ricevuta di una richiesta asincrona asimmetrica";

					}
					//	Ricevuta alla risposta.
					else if(RuoloBusta.RICEVUTA_RISPOSTA.equals(ruoloBustaRicevuta.toString())){

						msgDiag.mediumDebug("Gestione profilo di collaborazione AsincronoSimmetrico ricevuta risposta...");
						ricevutaAsincrona = "ricevuta di una risposta asincrona asimmetrica contenente l'esito della richiesta stato";

					}
					else{

						msgDiag.logPersonalizzato("profiloAsincrono.flussoRicevutaRichiestaRispostaNonCorretto");
						ejbUtils.sendAsRispostaBustaErroreProcessamento(richiestaApplicativa.getIdModuloInAttesa(),bustaRichiesta,
								ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
									get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_525_GESTIONE_FUNZIONALITA_PROTOCOLLO),
								idCorrelazioneApplicativa,servizioApplicativoFruitore,null,
								null);
						openspcoopstate.releaseResource();
						esito.setEsitoInvocazione(true);  
						esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,
								msgDiag.getMessaggio_replaceKeywords("profiloAsincrono.flussoRicevutaRichiestaRispostaNonCorretto")); 
						return esito;

					}


					//	Raccolgo dati consegna per Ricevute
					try{
						if(ricevutaAsincrona!=null){
							msgDiag.mediumDebug("Gestione profilo di collaborazione AsincronoAsimmetrico ricevuta (raccolta dati consegna per ricevuta)...");
							integrazioneAsincrona = profiloCollaborazione.asincronoAsimmetrico_getDatiConsegnaRicevuta(bustaRichiesta.getRiferimentoMessaggio());
						}
					}catch(Exception e){
						msgDiag.logErroreGenerico(e, "RaccoltaDatiIntegrazioneProfiloAsincronoAsimmetrico");
						ejbUtils.sendAsRispostaBustaErroreProcessamento(richiestaApplicativa.getIdModuloInAttesa(),bustaRichiesta,
								ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
									get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_525_GESTIONE_FUNZIONALITA_PROTOCOLLO),
								idCorrelazioneApplicativa,servizioApplicativoFruitore,e,
								null);
						openspcoopstate.releaseResource();
						esito.setEsitoInvocazione(true); 
						esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,"RaccoltaDatiIntegrazioneProfiloAsincronoAsimmetrico"); 
						return esito;
					} 

				}	

			}else{

				// profilo non conosciuto
				Eccezione ecc = 
						Eccezione.getEccezioneValidazione(ErroriCooperazione.PROFILO_COLLABORAZIONE_SCONOSCIUTO.
								getErroreProfiloCollaborazioneSconosciuto(bustaRichiesta.getProfiloDiCollaborazioneValue()),
									protocolFactory);
				msgDiag.addKeyword(CostantiPdD.KEY_ECCEZIONI, ecc.toString(protocolFactory));
				msgDiag.addKeyword(CostantiPdD.KEY_NUMERO_ECCEZIONI, "1");
				msgDiag.logPersonalizzato("validazioneBusta.bustaNonCorretta");
				ejbUtils.sendAsRispostaBustaErroreValidazione(richiestaApplicativa.getIdModuloInAttesa(),bustaRichiesta,ecc,
						idCorrelazioneApplicativa,servizioApplicativoFruitore);
				openspcoopstate.releaseResource();
				esito.setEsitoInvocazione(true); 
				esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,
						msgDiag.getMessaggio_replaceKeywords("validazioneBusta.bustaNonCorretta")); 
				return esito;

			}	











			/*   -------------- Creazione dati consegna applicativa per profili asincroni  ------------------ */
			msgDiag.mediumDebug("Gestione profilo di collaborazione Asincrono, raccolta dati consegna...");
			RichiestaDelegata consegnaApplicativaAsincrona = null;
			PortaDelegata pdConsegnaApplicativaAsincrona = null;
			ServizioApplicativo saConsegnaApplicativaAsincrona = null;
			try{
				if(integrazioneAsincrona!=null){
					IDSoggetto soggettoFruitoreRichiestaAsincrona = new IDSoggetto(bustaRichiesta.getTipoDestinatario(),bustaRichiesta.getDestinatario());
					IDSoggetto soggettoErogatoreRichiestaAsincrona = new IDSoggetto(bustaRichiesta.getTipoMittente(),bustaRichiesta.getMittente());
					IDServizio servizioRichiestaAsincrona = new IDServizio(soggettoErogatoreRichiestaAsincrona,bustaRichiesta.getTipoServizio(),bustaRichiesta.getServizio(),bustaRichiesta.getAzione());
					ProprietaErroreApplicativo proprietaErroreApplAsincrono = this.propertiesReader.getProprietaGestioneErrorePD(protocolManager);
					proprietaErroreApplAsincrono.setDominio(identitaPdD.getCodicePorta());
					proprietaErroreApplAsincrono.setIdModulo(Sbustamento.ID_MODULO);
					consegnaApplicativaAsincrona = new RichiestaDelegata(soggettoFruitoreRichiestaAsincrona,integrazioneAsincrona.getLocationPD(),
							integrazioneAsincrona.getServizioApplicativo(),servizioRichiestaAsincrona,null,proprietaErroreApplAsincrono,identitaPdD);
					consegnaApplicativaAsincrona.setScenario(scenarioCooperazione);
					consegnaApplicativaAsincrona.setProfiloGestione(profiloGestione);
					
					pdConsegnaApplicativaAsincrona = configurazionePdDManager.getPortaDelegata(consegnaApplicativaAsincrona.getIdPortaDelegata());
					try{
						saConsegnaApplicativaAsincrona = configurazionePdDManager.getServizioApplicativo(consegnaApplicativaAsincrona.getIdPortaDelegata(), 
								consegnaApplicativaAsincrona.getServizioApplicativo());
					}catch(Exception e){
						if( !(e instanceof DriverConfigurazioneNotFound) || 
								!(CostantiPdD.SERVIZIO_APPLICATIVO_ANONIMO.equals(consegnaApplicativaAsincrona.getServizioApplicativo())) ){
							throw e;
						}
					}
					
					configurazionePdDManager.aggiornaProprietaGestioneErrorePD(proprietaErroreApplAsincrono,saConsegnaApplicativaAsincrona);
					

					// gestione ricevute asincrone
					if(org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.ASINCRONO_SIMMETRICO.equals(bustaRichiesta.getProfiloDiCollaborazione())){
						// asincrono simmetrico
						consegnaApplicativaAsincrona.setRicevutaAsincrona(configurazionePdDManager.ricevutaAsincronaSimmetricaAbilitata(pdConsegnaApplicativaAsincrona));
					} else {
						// asincrono asimmetrico, se sono in un contesto di richiestaStato pooling, devo sempre ritornare il contenuto applicativo nella richiesta
						if(RuoloBusta.RICEVUTA_RISPOSTA.equals(ruoloBustaRicevuta.toString())){
							consegnaApplicativaAsincrona.setRicevutaAsincrona(true);
						}else{
							consegnaApplicativaAsincrona.setRicevutaAsincrona(configurazionePdDManager.ricevutaAsincronaAsimmetricaAbilitata(pdConsegnaApplicativaAsincrona));
						}
					}

					// Ricevute Asincrone Simmetriche e Asimmetriche 
					if(ricevutaAsincrona!=null){
						//	di default la ricezioneAsincrona viene utilizzata.
						//	Non deve essere utilizzata solo nel caso di richiesta con profilo AsincronoSimmetrico 
						consegnaApplicativaAsincrona.setUtilizzoConsegnaAsincrona(true);
						if(profiloCollaborazione.asincrono_spedizioneRichiestaInCorso(bustaRichiesta.getRiferimentoMessaggio()) 
								&& org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.ASINCRONO_SIMMETRICO.equals(bustaRichiesta.getProfiloDiCollaborazione())){
							consegnaApplicativaAsincrona.setUtilizzoConsegnaAsincrona(false);
						}

						//	Il modulo in attesa e' il servizio di ricezioneContenutiApplicativi
						consegnaApplicativaAsincrona.setIdModuloInAttesa(integrazioneAsincrona.getIdModuloInAttesa());
					}

					// Risposta Asincrona Simmetrica
					else{
						// Il modulo in attesa e' il servizio di ricezioneBuste
						consegnaApplicativaAsincrona.setIdModuloInAttesa(richiestaApplicativa.getIdModuloInAttesa());

						//	elimino busta per profilo se non devo attendere una risposta applicativa (altrimenti la elimino quando prendo la busta per la ricevuta )
						if(consegnaApplicativaAsincrona.isRicevutaAsincrona() == false){
							profiloCollaborazione.eliminaBustaInviata(protocolManager.getIdCorrelazioneAsincrona(bustaRichiesta),true);
						}

						//	di default la ricezioneAsincrona viene utilizzata.
						//	Non deve essere utilizzata solo nel caso di richiesta con profilo AsincronoSimmetrico 
						// qua siamo nella risposta asincrona
						consegnaApplicativaAsincrona.setUtilizzoConsegnaAsincrona(true);

						//	deve essere generato un msg OK se non e' abilitata la funzionalita' di ricevuta asincrona
						if(configurazionePdDManager.isModalitaStateless(pdConsegnaApplicativaAsincrona, bustaRichiesta.getProfiloDiCollaborazione())==false){
							generazioneMsgOK = !consegnaApplicativaAsincrona.isRicevutaAsincrona();
						}
						else{
							generazioneMsgOK = false;
						}
						
						//	RicevutaAsincrona immediata
						if(generazioneMsgOK){
							returnProtocolReply = true;
							bustaHTTPReply.setServizio(bustaRichiesta.getServizio());
							bustaHTTPReply.setTipoServizio(bustaRichiesta.getTipoServizio());
							bustaHTTPReply.setAzione(bustaRichiesta.getAzione());
							bustaHTTPReply.setProfiloDiCollaborazione(org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.ASINCRONO_SIMMETRICO);
							bustaHTTPReply.setRiferimentoMessaggio(bustaRichiesta.getID());
							// (per gli asincroni devono sempre essere presenti)
							if(this.propertiesReader.isGestioneElementoCollaborazione(implementazionePdDMittente))
								bustaHTTPReply.setCollaborazione(bustaRichiesta.getCollaborazione());
							bustaHTTPReply.setRiferimentoMsgBustaRichiedenteServizio(bustaRichiesta.getRiferimentoMessaggio());

							if(!consegnaAffidabile){
								bustaHTTPReply.setConfermaRicezione(false);
							}
							if(imbustamentoFiltroDuplicatiAbilitato){
								bustaHTTPReply.setInoltro(Inoltro.SENZA_DUPLICATI,protocolFactory.createTraduttore().toString(Inoltro.SENZA_DUPLICATI));	
							}
						}


					}
				}	
			}catch(Exception e){
				msgDiag.logErroreGenerico(e, "GestioneConsegnaProfiloAsincrono");
				ejbUtils.sendAsRispostaBustaErroreProcessamento(richiestaApplicativa.getIdModuloInAttesa(),bustaRichiesta,
						ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
							get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_525_GESTIONE_FUNZIONALITA_PROTOCOLLO),
						idCorrelazioneApplicativa,servizioApplicativoFruitore,e,
						null);
				openspcoopstate.releaseResource();	
				esito.setEsitoInvocazione(true); 
				esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,"GestioneConsegnaProfiloAsincrono"); 
				return esito;
			} 
















			/* ------------  Scenario di Cooperazione  ------------- */
			msgDiag.mediumDebug("Impostazione scenario di cooperazione...");
			try{
				richiestaApplicativa.setScenario(scenarioCooperazione);
				repositoryBuste.aggiornaInfoIntegrazioneIntoInBox_Scenario(bustaRichiesta.getID(), scenarioCooperazione);
				ejbUtils.setScenarioCooperazione(scenarioCooperazione);
			}catch(Exception e){
				msgDiag.logErroreGenerico(e, "repository.aggiornaInfoIntegrazioneIntoInBox_Scenario");
				ejbUtils.sendAsRispostaBustaErroreProcessamento(richiestaApplicativa.getIdModuloInAttesa(),bustaRichiesta,
						ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
							get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_525_GESTIONE_FUNZIONALITA_PROTOCOLLO),
						idCorrelazioneApplicativa,servizioApplicativoFruitore,e,
						null);
				openspcoopstate.releaseResource();
				esito.setEsitoInvocazione(true); 
				esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,"repository.aggiornaInfoIntegrazioneIntoInBox_Scenario"); 
				return esito;
			} 




			/* ---------  
			 * Spedizione contenuto applicativi con
			 * - ConsegnaContenutiApplicativiMessage al modulo ConsegnaContenutiApplicativi
			 * - RicezioneContenutiApplicativiMessage al modulo RicezioneContenutiApplicativi
			 * -------- */
			Behaviour behaviour = null;
			try{
				if(ricevutaAsincrona==null){
					if(consegnaApplicativaAsincrona==null){
						msgDiag.mediumDebug("Invio messaggio a ConsegnaContenutiApplicativi...");
						behaviour = ejbUtils.sendToConsegnaContenutiApplicativi(richiestaApplicativa,	bustaRichiesta,msgRequest,pa,repositoryBuste);
						if(ejbUtils.isGestioneStatelessConIntegrationManager()){
							generazioneMsgOK = true;
							sendSbloccoRicezioneBuste = true;
						}
					}else{
						msgDiag.mediumDebug("Invio messaggio a ConsegnaContenutiApplicativi (consegnaAsincrona)...");
						ejbUtils.sendToConsegnaContenutiApplicativi_gestioneMessaggio(consegnaApplicativaAsincrona,bustaRichiesta,msgRequest,saConsegnaApplicativaAsincrona);
					}	
				}else{
					if(consegnaApplicativaAsincrona.isRicevutaAsincrona()){
						// Aggiorno l'id di sessione al riferimento Messaggio, id utilizzato dal servizio
						// RicezioneContenutiApplicativi per l'attesa di una risposta
						msgDiag.mediumDebug("Invio messaggio asincrono a Ricezione/Consegna ContenutiApplicativi...");
						ejbUtils.updateIdSessione(bustaRichiesta.getRiferimentoMessaggio());

						// Spedizione contenuto ricevuta applicativa
						msgRequest = ejbUtils.sendRispostaApplicativa(consegnaApplicativaAsincrona,pdConsegnaApplicativaAsincrona,saConsegnaApplicativaAsincrona);

						// ripristino old id sessione
						ejbUtils.updateIdSessione(idMessageRequest);
					}
				}
				// La gestione puo' avvenire per diversi servizi applicativi di una stessa PA.
				// Quindi il servizio applicativo non e' identificavile in questo modulo. Lo sara' in Consegna contenuti applicativi.
				// Viene effettuato il log di correlazione in EJBUtils
				msgDiag.setServizioApplicativo(null);

			}catch(EJBUtilsConsegnaException e){
				msgDiag.logPersonalizzato(e.getMessaggio(), e.getLivello(), e.getCodice());
				
				ejbUtils.sendAsRispostaBustaErroreProcessamento(richiestaApplicativa.getIdModuloInAttesa(),bustaRichiesta,
						ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
							get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_512_SEND),
						idCorrelazioneApplicativa,servizioApplicativoFruitore,e,
						null);
				openspcoopstate.releaseResource();
				esito.setEsitoInvocazione(true); 
				esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,e.getMessaggio()); 
				return esito;
			}
			catch(Exception e){
				//viene loggato dentro ejbUtils. msgDiag.errorOpenSPCoop("Abilitazione modulo ConsegnaMessaggio non riuscita (Busta "+bustaRichiesta.getID()+"): "+e.getMessage());

				ejbUtils.sendAsRispostaBustaErroreProcessamento(richiestaApplicativa.getIdModuloInAttesa(),bustaRichiesta,
						ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
							get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_512_SEND),
						idCorrelazioneApplicativa,servizioApplicativoFruitore,e,
						null);
				openspcoopstate.releaseResource();
				esito.setEsitoInvocazione(true); 
				esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,"Abilitazione modulo ConsegnaMessaggio non riuscita (Busta "+bustaRichiesta.getID()+"): "+e.getMessage()); 
				return esito;
			}








			
			
			







			/* ------------  Gestione Risposta verso RicezioneBuste ------------- */
			// se il profilo e' oneWay, 
			// AsincronoSimmetrico o AsincronoAsimmetricoRichiesta senza gestione della ricevuta applicativa 
			// viene generata una risposta inviata al modulo.
			boolean behaviourResponseTo = behaviour!=null && behaviour.isResponseTo();
			if(  generazioneMsgOK || behaviourResponseTo ) {
				try{
					pddContext.addObject(org.openspcoop2.core.constants.Costanti.DATA_PRESA_IN_CARICO, 
							org.openspcoop2.core.constants.Costanti.newSimpleDateFormat().format(DateManager.getDate()));
					
					if(behaviourResponseTo){
					
						// Invio risposta richiesta dalla configurazione di behaviour
						OpenSPCoop2Message msgReplyTo = null;
						Busta bustaReplyTo = null;
						if(behaviour.getResponseTo()!=null){
							msgReplyTo = behaviour.getResponseTo().getMessage();
							bustaReplyTo = behaviour.getResponseTo().getBusta();
						}
						if(msgReplyTo==null){
							msgReplyTo = SoapUtils.build_Soap_Empty(versioneSoap);
						}
						msgDiag.mediumDebug("Invio messaggio a Ricezione/Consegna ContenutiApplicativi (Behaviour)...");
						msgResponse = ejbUtils.buildAndSendBustaRisposta(richiestaApplicativa.getIdModuloInAttesa(),
								bustaReplyTo,
								msgReplyTo,profiloGestione,
								idCorrelazioneApplicativa,servizioApplicativoFruitore);
						
					}else if( returnProtocolReply == false){
						// Invio sblocco se e' attesa una risposta dal modulo
						// Se non e' abilitato la risposta su di una nuova connessione, e l'indirizzo telematico 
						// non e' abilitato o cmq non presente, allora devo inviare lo sblocco
						if(sendSbloccoRicezioneBuste){
							msgDiag.mediumDebug("Invio messaggio di sblocco a RicezioneBuste...");
							msgResponse = ejbUtils.sendSbloccoRicezioneBuste(richiestaApplicativa.getIdModuloInAttesa());
						}
					}
					else{
						// Invio risposta immediata in seguito alla richiesta ricevuta
						msgDiag.mediumDebug("Invio messaggio a Ricezione/Consegna ContenutiApplicativi...");
						msgResponse = ejbUtils.buildAndSendBustaRisposta(richiestaApplicativa.getIdModuloInAttesa(),bustaHTTPReply,
								SoapUtils.build_Soap_Empty(versioneSoap),profiloGestione,
								idCorrelazioneApplicativa,servizioApplicativoFruitore);
					}
				}catch(Exception e){
					msgDiag.logErroreGenerico(e, "GenerazioneMsgOK(Riscontro/Ricevuta/Sblocco)");
					ejbUtils.sendAsRispostaBustaErroreProcessamento(richiestaApplicativa.getIdModuloInAttesa(),bustaRichiesta,
							ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
								get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_512_SEND),
							idCorrelazioneApplicativa,servizioApplicativoFruitore,e,
							null);
					openspcoopstate.releaseResource();
					esito.setEsitoInvocazione(true); 
					esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,"GenerazioneMsgOK(Riscontro/Ricevuta/Sblocco)"); 
					return esito;
				}
			}else if( ricevutaAsincrona !=null ){
				try{
					pddContext.addObject(org.openspcoop2.core.constants.Costanti.DATA_PRESA_IN_CARICO, 
							org.openspcoop2.core.constants.Costanti.newSimpleDateFormat().format(DateManager.getDate()));
					
					//	Invio sblocco
					// Se non e' abilitato la risposta su di una nuova connessione, e l'indirizzo telematico 
					// non e' abilitato o cmq non presente, allora devo inviare lo sblocco
					if(sendSbloccoRicezioneBuste){
						msgDiag.mediumDebug("Invio messaggio di sblocco a RicezioneBuste (ricevuta asincrona)...");
						msgResponse = ejbUtils.sendSbloccoRicezioneBuste(richiestaApplicativa.getIdModuloInAttesa());
					}
				}catch(Exception e){
					msgDiag.logErroreGenerico(e, "SendSbloccoDopoRicezione("+ricevutaAsincrona+")");
					ejbUtils.sendAsRispostaBustaErroreProcessamento(richiestaApplicativa.getIdModuloInAttesa(),bustaRichiesta,
							ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
								get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_512_SEND),
							idCorrelazioneApplicativa,servizioApplicativoFruitore,e,
							null);
					openspcoopstate.releaseResource();
					esito.setEsitoInvocazione(true); 
					esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,"SendSbloccoDopoRicezione("+ricevutaAsincrona+")"); 
					return esito;
				}
			}
			
			







			/* ---------- Gestione Transazione Modulo ---------------- */
			// se non ho gestito una ricevuta asincrona, sono andato verso ConsegnaContenutiApplicativi
			msgDiag.mediumDebug("Aggiornamento proprietario messaggio...");

			if(ricevutaAsincrona==null){
				msgRequest.aggiornaProprietarioMessaggio(org.openspcoop2.pdd.mdb.ConsegnaContenutiApplicativi.ID_MODULO);
			}
			// se ho gestito una ricevuta asincrona, guardo se e' abilitata la gestione del contenuto applicativo nella risposta
			// se non e' abilitato devo cancellare il messaggio. 
			else if(consegnaApplicativaAsincrona!=null){
				if(consegnaApplicativaAsincrona.isRicevutaAsincrona()==false){
					repositoryBuste.eliminaUtilizzoPdDFromInBox(idMessageRequest,true);
					msgRequest.aggiornaProprietarioMessaggio(org.openspcoop2.pdd.timers.TimerGestoreMessaggi.ID_MODULO);
				}
			}
			// else l'ho gia inviato al modulo di ricezione contenuti applicativi il contenuto della ricevuta asincrona

			// Commit JDBC
			msgDiag.mediumDebug("Commit delle operazioni per la gestione della busta...");
			openspcoopstate.commit();


		}catch(Throwable e){
			this.log.error("ErroreGenerale",e);
			msgDiag.logErroreGenerico(e, "Generale");
			
			if(org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.SINCRONO.equals(bustaRichiesta.getProfiloDiCollaborazione()) == true  ) {
				try{
					ejbUtils.sendAsRispostaBustaErroreProcessamento(richiestaApplicativa.getIdModuloInAttesa(),bustaRichiesta,
							ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.getErroreIntegrazione(),
							idCorrelazioneApplicativa,servizioApplicativoFruitore,e,
							null);
					esito.setEsitoInvocazione(true); 
					esito.setStatoInvocazione(EsitoLib.ERRORE_GESTITO,"ErroreGenerale");
				}catch(Exception er){
					msgDiag.logErroreGenerico(er,"ejbUtils.sendErroreGenerale(profiloConRisposta)");
					ejbUtils.rollbackMessage("Spedizione Errore al Mittente durante una richiesta sincrona non riuscita", esito);
					esito.setEsitoInvocazione(false); 
					esito.setStatoInvocazioneErroreNonGestito(er);
				}	 
			}else{
				ejbUtils.rollbackMessage("ErroreGenerale:"+e.getMessage(), esito); 
				esito.setStatoInvocazioneErroreNonGestito(e);
				esito.setEsitoInvocazione(false);
			}
			openspcoopstate.releaseResource();
			return esito;
		}

		// Aggiornamento cache messaggio
		if(msgRequest!=null)
			msgRequest.addMessaggiIntoCache_readFromTable(Sbustamento.ID_MODULO, "richiesta");
		if(msgResponse!=null)
			msgResponse.addMessaggiIntoCache_readFromTable(Sbustamento.ID_MODULO, "risposta");

		// Aggiornamento cache proprietario messaggio
		if(msgRequest!=null)
			msgRequest.addProprietariIntoCache_readFromTable(Sbustamento.ID_MODULO, "richiesta",null,false);
		if(msgResponse!=null)
			msgResponse.addProprietariIntoCache_readFromTable(Sbustamento.ID_MODULO, "risposta",idMessageRequest,false);

		// Gestione ricevute
		if(ricevutaAsincrona!=null){
			msgDiag.mediumDebug("Gestione ricevute asincrone...");
			try{
				
				boolean forzaEliminazioneMessaggio = false;
				if( (openspcoopstate instanceof OpenSPCoopStateless) &&  
						("ricevuta di una richiesta asincrona simmetrica".equals(ricevutaAsincrona)) ){
					forzaEliminazioneMessaggio = true;
				}
				
				GestoreMessaggi msgRiscontrato = new GestoreMessaggi(openspcoopstate, true, bustaRichiesta.getRiferimentoMessaggio(),Costanti.OUTBOX,msgDiag,pddContext);
				msgRiscontrato.setReadyForDrop(forzaEliminazioneMessaggio);
				msgRiscontrato.validateAndDeleteMsgAsincronoRiscontrato(bustaRichiesta);
				msgDiag.addKeyword(CostantiPdD.KEY_TIPO_RICEVUTA_ASINCRONA, ricevutaAsincrona);
				msgDiag.logPersonalizzato("validazioneRicevutaAsincrona");
			}catch(Exception e){
				msgDiag.logErroreGenerico(e,"Validazione("+ricevutaAsincrona+")");
			}
		}

		// Rilascio connessione al DB
		msgDiag.mediumDebug("Rilascio connessione al database...");
		openspcoopstate.releaseResource();

		msgDiag.mediumDebug("Lavoro Terminato.");

		esito.setEsitoInvocazione(true); 
		esito.setStatoInvocazione(EsitoLib.OK, null); 
		return esito;

	}



	/**
	 * Gestione errore.
	 *
	 * 
	 */
	private void gestioneErroreProtocollo(ConfigurazionePdDManager configurazionePdDManager, EJBUtils ejbUtils, ProfiloDiCollaborazione profiloCollaborazione, RepositoryBuste repositoryBuste,
			Busta busta, IDSoggetto identitaPdD, Eccezione eccezioneProtocollo, ErroreIntegrazione erroreIntegrazione, IDSoggetto soggettoProduttoreEccezione, DettaglioEccezione dettaglioEccezione, 
			SOAPVersion versioneSoap,IProtocolManager protocolManager) throws Exception{

		// Gestione ERRORE
		if(org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.SINCRONO.equals(busta.getProfiloDiCollaborazione()) && busta.getRiferimentoMessaggio()!=null){
			// oltre al messaggio in ingresso, viene effettuato il rollback sul riferimento messaggio.
			// una risposta al SIL in attesa verra' ritornata quando scadra il timeout sulla connessione JMS della coda 'RicezioneContenutiApplicativi'
			ejbUtils.releaseInboxMessage(Costanti.OUTBOX,busta.getRiferimentoMessaggio(), true);
		}else if(org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.ASINCRONO_SIMMETRICO.equals(busta.getProfiloDiCollaborazione()) ||
				org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.ASINCRONO_ASIMMETRICO.equals(busta.getProfiloDiCollaborazione()) ){
			Integrazione integrazioneRispostaErrore = null;
			boolean spedizioneRichiestaInCorso = profiloCollaborazione.asincrono_spedizioneRichiestaInCorso(busta.getRiferimentoMessaggio());
			boolean spedizioneRispostaInCorso = profiloCollaborazione.asincrono_spedizioneRispostaInCorso(busta.getRiferimentoMessaggio());
			boolean ricevutaApplicativaAbilitata=true;
			boolean gestioneEffettuata = false;
			if(spedizioneRichiestaInCorso || spedizioneRispostaInCorso){
				ricevutaApplicativaAbilitata = profiloCollaborazione.asincrono_ricevutaApplicativaAbilitata(busta.getRiferimentoMessaggio());
				if(ricevutaApplicativaAbilitata){
					try{
						integrazioneRispostaErrore = repositoryBuste.getInfoIntegrazioneFromOutBox(busta.getRiferimentoMessaggio());
					}catch(Exception e){
						throw new Exception("Riscontrato errore durante l'esamina dei dati di integrazione per la generazione di una risposta applicativa asincrona, in seguito alla ricezione di un Messaggio Errore Protocollo: "+e.getMessage());
					}
					if(integrazioneRispostaErrore!=null){
						// Costruzione Richiesta Delegata
						IDSoggetto soggettoFruitoreAsincrono = new IDSoggetto(busta.getTipoDestinatario(),busta.getDestinatario());
						IDSoggetto soggettoErogatoreAsincrono = new IDSoggetto(busta.getTipoMittente(),busta.getMittente());
						IDServizio servizioAsincrono = new IDServizio(soggettoErogatoreAsincrono,busta.getTipoServizio(),busta.getServizio(),busta.getAzione());
						ProprietaErroreApplicativo proprietaErroreApplAsincrono = this.propertiesReader.getProprietaGestioneErrorePD(protocolManager);
						proprietaErroreApplAsincrono.setDominio(identitaPdD.getCodicePorta());
						proprietaErroreApplAsincrono.setIdModulo(Sbustamento.ID_MODULO);
						
						RichiestaDelegata consegnaApplicativaAsincrona = new RichiestaDelegata(soggettoFruitoreAsincrono,integrazioneRispostaErrore.getLocationPD(),
								integrazioneRispostaErrore.getServizioApplicativo(),servizioAsincrono,null,proprietaErroreApplAsincrono,identitaPdD);
						
						PortaDelegata pd = configurazionePdDManager.getPortaDelegata(consegnaApplicativaAsincrona.getIdPortaDelegata());
						ServizioApplicativo sappl = null;
						try{
							sappl = configurazionePdDManager.getServizioApplicativo(consegnaApplicativaAsincrona.getIdPortaDelegata(), 
								consegnaApplicativaAsincrona.getServizioApplicativo());
						}catch(Exception e){
							if( !(e instanceof DriverConfigurazioneNotFound) || !(CostantiPdD.SERVIZIO_APPLICATIVO_ANONIMO.equals(consegnaApplicativaAsincrona.getServizioApplicativo())) ){
								throw e;
							}
						}
						configurazionePdDManager.aggiornaProprietaGestioneErrorePD(proprietaErroreApplAsincrono,sappl);
					
						
						//	di default la ricezioneAsincrona viene utilizzata.
						//	Non deve essere utilizzata solo nel caso di richiesta con profilo AsincronoSimmetrico 
						consegnaApplicativaAsincrona.setUtilizzoConsegnaAsincrona(true);
						if(spedizioneRichiestaInCorso && org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.ASINCRONO_SIMMETRICO.equals(busta.getProfiloDiCollaborazione())){
							consegnaApplicativaAsincrona.setUtilizzoConsegnaAsincrona(false);
						}
						//	Il modulo in attesa e' il servizio di ricezioneContenutiApplicativi
						consegnaApplicativaAsincrona.setIdModuloInAttesa(integrazioneRispostaErrore.getIdModuloInAttesa());

						this.erroreApplicativoBuilder.setProprietaErroreApplicato(proprietaErroreApplAsincrono);
						this.erroreApplicativoBuilder.setTipoPdD(TipoPdD.DELEGATA);
						this.erroreApplicativoBuilder.setServizioApplicativo(consegnaApplicativaAsincrona.getServizioApplicativo());
						this.erroreApplicativoBuilder.setMittente(consegnaApplicativaAsincrona.getSoggettoFruitore());
						this.erroreApplicativoBuilder.setServizio(consegnaApplicativaAsincrona.getIdServizio());
						this.erroreApplicativoBuilder.setDominio(consegnaApplicativaAsincrona.getDominio());
						OpenSPCoop2Message responseMessageError = null;
						if(eccezioneProtocollo!=null){
							responseMessageError = this.erroreApplicativoBuilder.toMessage(eccezioneProtocollo,soggettoProduttoreEccezione,dettaglioEccezione,
									null);
						}else{
							responseMessageError = this.erroreApplicativoBuilder.toMessage(erroreIntegrazione,null,null);
						}
						//	Aggiorno l'id di sessione al riferimento Messaggio, id utilizzato dal servizio
						// RicezioneContenutiApplicativi per l'attesa di una risposta
						ejbUtils.updateIdSessione(busta.getRiferimentoMessaggio());
						ejbUtils.sendRispostaApplicativaErrore(responseMessageError,consegnaApplicativaAsincrona,spedizioneRichiestaInCorso,pd,sappl);
						// ripristino old id sessione
						ejbUtils.updateIdSessione(busta.getID());

						gestioneEffettuata = true;
					}
					else{
						// una risposta al SIL in attesa verra' ritornata quando scadra il timeout sulla connessione JMS della coda 'RicezioneContenutiApplicativi'
						if(spedizioneRichiestaInCorso){
							//	oltre al messaggio in ingresso, viene effettuato il rollback sul riferimento messaggio.
							ejbUtils.releaseInboxMessage(Costanti.OUTBOX,busta.getRiferimentoMessaggio(), true);

							gestioneEffettuata = true;
						}// else se vi e' una risposta in corso, basta rilasciare la busta di errore.
					}
				}// else se non e' attiva la gestione della ricevuta applicativa, basta rilasciare la busta di errore.
			}// else se non e' in corso ne una richiesta ne una risposta, basta rilasciare la busta di errore.
			if(gestioneEffettuata == false){
				ejbUtils.releaseInboxMessage(true);
			}					
		}else{
			ejbUtils.releaseInboxMessage(true);
		}

	}

	
	public static IFiltroDuplicati getGestoreFiltroDuplicati(OpenSPCoop2Properties propertiesReader,Loader loader, 
			IOpenSPCoopState openspcoopstate,PdDContext pddContext, History historyBuste, RepositoryBuste repositoryBuste, boolean oneWayVersione11) throws Exception{
		// Istanzio gestore filtro duplicati
		String gestoreFiltroDuplicatiType = propertiesReader.getGestoreFiltroDuplicatiRepositoryBuste();
		ClassNameProperties prop = ClassNameProperties.getInstance();
		String gestoreFiltroDuplicatiClass = prop.getFiltroDuplicati(gestoreFiltroDuplicatiType);
		if(gestoreFiltroDuplicatiClass == null){
			throw new Exception("GestoreFiltroDuplicati non registrato ("+gestoreFiltroDuplicatiType+")");
		}
		IFiltroDuplicati gestoreFiltroDuplicati = (IFiltroDuplicati) loader.newInstance(gestoreFiltroDuplicatiClass);
		pddContext.addObject(org.openspcoop2.core.constants.Costanti.CONNECTION_PDD, ((OpenSPCoopState)openspcoopstate).getConnectionDB() );
		gestoreFiltroDuplicati.init(pddContext);
		if(gestoreFiltroDuplicati instanceof FiltroDuplicati){
			((FiltroDuplicati)gestoreFiltroDuplicati).setHistoryBuste(historyBuste);
			((FiltroDuplicati)gestoreFiltroDuplicati).setRepositoryBuste(repositoryBuste);
			((FiltroDuplicati)gestoreFiltroDuplicati).setGestioneStateless((openspcoopstate instanceof OpenSPCoopStateless) && (oneWayVersione11==false));
			((FiltroDuplicati)gestoreFiltroDuplicati).setRepositoryIntervalloScadenzaMessaggi(propertiesReader.getRepositoryIntervalloScadenzaMessaggi());
		}
		return gestoreFiltroDuplicati;
	}
}
