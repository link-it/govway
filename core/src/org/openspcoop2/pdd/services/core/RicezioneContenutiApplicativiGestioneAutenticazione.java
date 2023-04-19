/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it). 
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

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.config.GestioneTokenAutenticazione;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.core.config.driver.DriverConfigurazioneNotFound;
import org.openspcoop2.core.id.IDPortaDelegata;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.PortaDominio;
import org.openspcoop2.core.registry.Soggetto;
import org.openspcoop2.core.transazioni.utils.CredenzialiMittente;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.pdd.config.ConfigurazionePdDManager;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.config.RichiestaDelegata;
import org.openspcoop2.pdd.core.CostantiPdD;
import org.openspcoop2.pdd.core.GestoreRichieste;
import org.openspcoop2.pdd.core.PdDContext;
import org.openspcoop2.pdd.core.autenticazione.GestoreAutenticazione;
import org.openspcoop2.pdd.core.autenticazione.ParametriAutenticazione;
import org.openspcoop2.pdd.core.autenticazione.pd.EsitoAutenticazionePortaDelegata;
import org.openspcoop2.pdd.core.credenziali.Credenziali;
import org.openspcoop2.pdd.core.handlers.InRequestContext;
import org.openspcoop2.pdd.core.integrazione.HeaderIntegrazione;
import org.openspcoop2.pdd.core.state.IOpenSPCoopState;
import org.openspcoop2.pdd.core.token.InformazioniToken;
import org.openspcoop2.pdd.core.transazioni.Transaction;
import org.openspcoop2.pdd.logger.MsgDiagnostico;
import org.openspcoop2.pdd.services.error.RicezioneContenutiApplicativiInternalErrorGenerator;
import org.openspcoop2.protocol.engine.utils.NamingUtils;
import org.openspcoop2.protocol.registry.RegistroServiziManager;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.builder.ProprietaErroreApplicativo;
import org.openspcoop2.protocol.sdk.constants.CodiceErroreIntegrazione;
import org.openspcoop2.protocol.sdk.constants.CostantiProtocollo;
import org.openspcoop2.protocol.sdk.constants.ErroreIntegrazione;
import org.openspcoop2.protocol.sdk.constants.ErroriIntegrazione;
import org.openspcoop2.protocol.sdk.constants.IntegrationFunctionError;
import org.openspcoop2.protocol.sdk.state.RequestFruitore;
import org.openspcoop2.protocol.sdk.state.RequestInfo;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.slf4j.Logger;

/**
 * RicezioneContenutiApplicativiGestioneAutenticazione
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class RicezioneContenutiApplicativiGestioneAutenticazione {

	private MsgDiagnostico msgDiag;
	private Logger logCore;
	
	private PortaDelegata portaDelegata;
	private IDPortaDelegata identificativoPortaDelegata;
	
	private IDSoggetto soggettoFruitore;
	private Credenziali credenziali;
	private GestioneTokenAutenticazione gestioneTokenAutenticazione;
	private RichiestaDelegata richiestaDelegata;
	private ProprietaErroreApplicativo proprietaErroreAppl;
	
	private OpenSPCoop2Message requestMessage;
	
	private RicezioneContenutiApplicativiContext msgContext;
	private RicezioneContenutiApplicativiInternalErrorGenerator generatoreErrore;
	private InRequestContext inRequestContext;
	private HeaderIntegrazione headerIntegrazioneRichiesta;
	
	private ConfigurazionePdDManager configurazionePdDReader;
	private RegistroServiziManager registroServiziReader;
	
	private PdDContext pddContext;
	private String idTransazione;
	private IDSoggetto identitaPdD;
	private IOpenSPCoopState openspcoopstate;
	private Transaction transaction;
	private RequestInfo requestInfo;
	
	private IProtocolFactory<?> protocolFactory;
	
	public RicezioneContenutiApplicativiGestioneAutenticazione(MsgDiagnostico msgDiag, Logger logCore,
			PortaDelegata portaDelegata, IDPortaDelegata identificativoPortaDelegata,
			IDSoggetto soggettoFruitore, Credenziali credenziali, GestioneTokenAutenticazione gestioneTokenAutenticazione, RichiestaDelegata richiestaDelegata, ProprietaErroreApplicativo proprietaErroreAppl,
			OpenSPCoop2Message requestMessage,
			RicezioneContenutiApplicativiContext msgContext, RicezioneContenutiApplicativiInternalErrorGenerator generatoreErrore, InRequestContext inRequestContext, HeaderIntegrazione headerIntegrazioneRichiesta,
			ConfigurazionePdDManager configurazionePdDReader, RegistroServiziManager registroServiziReader,
			PdDContext pddContext, String idTransazione, IDSoggetto identitaPdD,
			IOpenSPCoopState openspcoopstate, Transaction transaction, RequestInfo requestInfo,
			IProtocolFactory<?> protocolFactory) {
		this.msgDiag = msgDiag;
		this.logCore = logCore;
				
		this.portaDelegata = portaDelegata;
		this.identificativoPortaDelegata = identificativoPortaDelegata;
		
		this.soggettoFruitore = soggettoFruitore;
		this.credenziali = credenziali;
		this.gestioneTokenAutenticazione = gestioneTokenAutenticazione;
		this.richiestaDelegata = richiestaDelegata;
		this.proprietaErroreAppl = proprietaErroreAppl;
		
		this.requestMessage = requestMessage;
		
		this.msgContext = msgContext;
		this.generatoreErrore = generatoreErrore;
		this.inRequestContext = inRequestContext;
		this.headerIntegrazioneRichiesta = headerIntegrazioneRichiesta;
		
		this.configurazionePdDReader = configurazionePdDReader;
		this.registroServiziReader = registroServiziReader;
		
		this.pddContext = pddContext;
		this.idTransazione = idTransazione;
		this.identitaPdD = identitaPdD;
		this.openspcoopstate = openspcoopstate;
		this.transaction = transaction;
		this.requestInfo = requestInfo; 
		
		this.protocolFactory = protocolFactory;
		
	}
	
	// Result
	private IDServizioApplicativo idApplicativoToken = null;
	private ServizioApplicativo sa = null;
	private String servizioApplicativo = CostantiPdD.SERVIZIO_APPLICATIVO_ANONIMO;
	private InformazioniToken informazioniTokenNormalizzate = null;

	public IDServizioApplicativo getIdApplicativoToken() {
		return this.idApplicativoToken;
	}
	public ServizioApplicativo getSa() {
		return this.sa;
	}
	public String getServizioApplicativo() {
		return this.servizioApplicativo;
	}
	public InformazioniToken getInformazioniTokenNormalizzate() {
		return this.informazioniTokenNormalizzate;
	}
	
	public boolean process() {
		
		String tipoAutenticazione = null;
		boolean autenticazioneOpzionale = false;
		try {
			tipoAutenticazione = this.configurazionePdDReader.getAutenticazione(this.portaDelegata);
			autenticazioneOpzionale = this.configurazionePdDReader.isAutenticazioneOpzionale(this.portaDelegata);
		} catch (Exception e) {
			this.msgDiag.logErroreGenerico(e, "letturaAutenticazioneTokenAutorizzazione");
			this.logCore.error(this.msgDiag.getMessaggio_replaceKeywords("letturaAutenticazioneTokenAutorizzazione"),e);
			this.openspcoopstate.releaseResource();
			if (this.msgContext.isGestioneRisposta()) {
				this.msgContext.setMessageResponse((this.generatoreErrore.build(this.pddContext,IntegrationFunctionError.INTERNAL_REQUEST_ERROR,
						ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
						get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_536_CONFIGURAZIONE_NON_DISPONIBILE), e,null)));
			}
			return false;
		}
		

		this.msgDiag.mediumDebug("Autenticazione del servizio applicativo...");
		this.msgContext.getIntegrazione().setTipoAutenticazione(tipoAutenticazione);
		this.msgContext.getIntegrazione().setAutenticazioneOpzionale(autenticazioneOpzionale);
		if(tipoAutenticazione!=null){
			this.msgDiag.addKeyword(CostantiPdD.KEY_TIPO_AUTENTICAZIONE, tipoAutenticazione);
		}
		String credenzialeTrasporto = null;
		
		org.openspcoop2.pdd.core.autenticazione.pd.DatiInvocazionePortaDelegata datiInvocazioneAutenticazione = new org.openspcoop2.pdd.core.autenticazione.pd.DatiInvocazionePortaDelegata();
		datiInvocazioneAutenticazione.setInfoConnettoreIngresso(this.inRequestContext.getConnettore());
		datiInvocazioneAutenticazione.setState(this.openspcoopstate.getStatoRichiesta());
		datiInvocazioneAutenticazione.setIdPD(this.identificativoPortaDelegata);
		datiInvocazioneAutenticazione.setPd(this.portaDelegata);	
		
		RequestFruitore requestFruitore = null;
		IDServizioApplicativo idSAFruitore = null;
				
		OpenSPCoop2Properties propertiesReader = OpenSPCoop2Properties.getInstance();
		
		if (CostantiConfigurazione.AUTENTICAZIONE_NONE.equalsIgnoreCase(tipoAutenticazione)) {

			this.msgDiag.logPersonalizzato("autenticazioneDisabilitata");
			
			// dichiarazione nell'header di integrazione
			if (this.headerIntegrazioneRichiesta.getServizioApplicativo() != null) {
				this.servizioApplicativo = this.headerIntegrazioneRichiesta.getServizioApplicativo();
				boolean existsServizioApplicativo = false;
				try {
					idSAFruitore = new IDServizioApplicativo();
					idSAFruitore.setNome(this.servizioApplicativo);
					idSAFruitore.setIdSoggettoProprietario(this.soggettoFruitore);
					requestFruitore = GestoreRichieste.readFruitoreTrasporto(this.requestInfo, this.soggettoFruitore, idSAFruitore);
					existsServizioApplicativo = this.configurazionePdDReader.existsServizioApplicativo(idSAFruitore, this.requestInfo);
					if(existsServizioApplicativo) {
						try {
							this.sa = this.configurazionePdDReader.getServizioApplicativo(idSAFruitore, this.requestInfo);
							Map<String, String> configProperties = this.configurazionePdDReader.getProprietaConfigurazione(this.sa);
				            if (configProperties != null && !configProperties.isEmpty()) {
				            	this.pddContext.addObject(org.openspcoop2.core.constants.Costanti.PROPRIETA_APPLICATIVO, configProperties);
							}	
						}catch(Throwable t) {
							// ignore
						}
					}
				} catch (Exception e) {
					this.msgDiag.logErroreGenerico(e, "existsServizioApplicativo("+this.servizioApplicativo+")");
					this.openspcoopstate.releaseResource();
					if (this.msgContext.isGestioneRisposta()) {
						this.msgContext.setMessageResponse((this.generatoreErrore.build(this.pddContext,IntegrationFunctionError.INTERNAL_REQUEST_ERROR,
								ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
								get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_536_CONFIGURAZIONE_NON_DISPONIBILE),e,null)));
					}
					return false;
				}
				if (!existsServizioApplicativo) {
					this.msgDiag.addKeyword(CostantiPdD.KEY_SA_FRUITORE, this.servizioApplicativo);
					this.msgDiag.logPersonalizzato("servizioApplicativoFruitore.identificazioneTramiteInfoIntegrazioneNonRiuscita");
					this.openspcoopstate.releaseResource();
					if (this.msgContext.isGestioneRisposta()) {
						this.msgContext.setMessageResponse((this.generatoreErrore.build(this.pddContext,IntegrationFunctionError.AUTHENTICATION,
								ErroriIntegrazione.ERRORE_402_AUTENTICAZIONE_FALLITA.
								getErrore402_AutenticazioneFallita("L'identità del servizio applicativo fornita ["+this.servizioApplicativo+"] non esiste nella configurazione"),
								null,null)));
					}
					return false;
				}
			}

		} else {

			this.transaction.getTempiElaborazione().startAutenticazione();
			try {
			
				this.msgDiag.logPersonalizzato("autenticazioneInCorso");
				
				ErroreIntegrazione errore = null;
				Exception eAutenticazione = null;
				OpenSPCoop2Message errorMessageAutenticazione = null;
				String wwwAuthenticateErrorHeader = null;
				boolean detailsSet = false;
				IntegrationFunctionError integrationFunctionError = null;
				try {						
						
					EsitoAutenticazionePortaDelegata esito = 
							GestoreAutenticazione.verificaAutenticazionePortaDelegata(tipoAutenticazione, 
									datiInvocazioneAutenticazione, new ParametriAutenticazione(this.portaDelegata.getProprietaAutenticazioneList()), 
									this.pddContext, this.protocolFactory, this.requestMessage); 
					CostantiPdD.addKeywordInCache(this.msgDiag, esito.isEsitoPresenteInCache(),
							this.pddContext, CostantiPdD.KEY_INFO_IN_CACHE_FUNZIONE_AUTENTICAZIONE);
					if(esito.getDetails()==null){
						this.msgDiag.addKeyword(CostantiPdD.KEY_DETAILS, "");
					}else{
						this.msgDiag.addKeyword(CostantiPdD.KEY_DETAILS, " ("+esito.getDetails()+")");
					}
					detailsSet = true;
					credenzialeTrasporto = esito.getCredential();
					if(esito.isClientAuthenticated() && credenzialeTrasporto!=null && esito.isEnrichPrincipal()) {
						// Aggiorno Principal
				    	if(datiInvocazioneAutenticazione.getInfoConnettoreIngresso()!=null && datiInvocazioneAutenticazione.getInfoConnettoreIngresso().getUrlProtocolContext()!=null && 
				    			datiInvocazioneAutenticazione.getInfoConnettoreIngresso().getUrlProtocolContext().getCredential()!=null &&
				    					datiInvocazioneAutenticazione.getInfoConnettoreIngresso().getUrlProtocolContext().getCredential().getPrincipal()==null) {
				    		datiInvocazioneAutenticazione.getInfoConnettoreIngresso().getUrlProtocolContext().getCredential().setPrincipal(credenzialeTrasporto);
				    	}
				    	if(datiInvocazioneAutenticazione.getInfoConnettoreIngresso()!=null && datiInvocazioneAutenticazione.getInfoConnettoreIngresso().getCredenziali()!=null &&
				    			datiInvocazioneAutenticazione.getInfoConnettoreIngresso().getCredenziali().getPrincipal()==null) {
				    		datiInvocazioneAutenticazione.getInfoConnettoreIngresso().getCredenziali().setPrincipal(credenzialeTrasporto);
				    	}
					}
					
					if(credenzialeTrasporto!=null) {
						this.pddContext.addObject(org.openspcoop2.core.constants.Costanti.IDENTIFICATIVO_AUTENTICATO, credenzialeTrasporto);
					}

					String fullCredential = esito.getFullCredential();
					if(fullCredential!=null && !"".equals(fullCredential)) {
						String c = this.transaction.getCredenziali();
						if(c!=null && !"".equals(c)) {
							c = c + "\n" + fullCredential;
						}
						else {
							c = fullCredential;
						}
						this.transaction.setCredenziali(c);
					}
				
					if(!esito.isClientAuthenticated()) {
						errore = esito.getErroreIntegrazione();
						eAutenticazione = esito.getEccezioneProcessamento();
						errorMessageAutenticazione = esito.getErrorMessage();
						wwwAuthenticateErrorHeader = esito.getWwwAuthenticateErrorHeader();
						integrationFunctionError = esito.getIntegrationFunctionError();
						this.msgDiag.addKeyword(CostantiPdD.KEY_CREDENZIALI_SA_FRUITORE, this.credenziali.toString(
								propertiesReader.isAutenticazioneBasicLogPassword() ? Credenziali.SHOW_BASIC_PASSWORD : !Credenziali.SHOW_BASIC_PASSWORD,
								!Credenziali.SHOW_ISSUER,
								!Credenziali.SHOW_DIGEST_CLIENT_CERT,
								!Credenziali.SHOW_SERIAL_NUMBER_CLIENT_CERT)); // Aggiungo la password se presente, evito inoltre di stampare l'issuer e altre info del cert nei diagnostici
					}
					else {
						if(esito.isClientIdentified()) {
							this.servizioApplicativo = esito.getIdServizioApplicativo().getNome();
							this.msgDiag.addKeyword(CostantiPdD.KEY_CREDENZIALI_SA_FRUITORE, ""); // per evitare di visualizzarle anche nei successivi diagnostici
							
							idSAFruitore = new IDServizioApplicativo();
							idSAFruitore.setIdSoggettoProprietario(this.soggettoFruitore);
							idSAFruitore.setNome(this.servizioApplicativo);
							try {
								requestFruitore = GestoreRichieste.readFruitoreTrasporto(this.requestInfo, this.soggettoFruitore, idSAFruitore);
								this.sa = this.configurazionePdDReader.getServizioApplicativo(idSAFruitore, this.requestInfo);
								Map<String, String> configProperties = this.configurazionePdDReader.getProprietaConfigurazione(this.sa);
					            if (configProperties != null && !configProperties.isEmpty()) {
					            	this.pddContext.addObject(org.openspcoop2.core.constants.Costanti.PROPRIETA_APPLICATIVO, configProperties);
								}	
							}catch(Throwable t) {
								// ignore
							}
						}
						else {
							// l'errore puo' non esserci se l'autenticazione utilizzata non prevede una identificazione obbligatoria
							errore = esito.getErroreIntegrazione();
							eAutenticazione = esito.getEccezioneProcessamento();
							errorMessageAutenticazione = esito.getErrorMessage();
							wwwAuthenticateErrorHeader = esito.getWwwAuthenticateErrorHeader();
							integrationFunctionError = esito.getIntegrationFunctionError();
							
							// evito comunque di ripresentarle nei successivi diagnostici, l'informazione l'ho gia' visualizzata nei diagnostici dell'autenticazione
							this.msgDiag.addKeyword(CostantiPdD.KEY_CREDENZIALI_SA_FRUITORE, ""); // per evitare di visualizzarle anche nei successivi diagnostici
						}
					}
					
					if(errore!=null) {
						if(!autenticazioneOpzionale){
							this.pddContext.addObject(org.openspcoop2.core.constants.Costanti.ERRORE_AUTENTICAZIONE, "true");
						}
					}
					else {
						this.msgDiag.logPersonalizzato("autenticazioneEffettuata");
					}
					
				} catch (Exception e) {
					CostantiPdD.addKeywordInCache(this.msgDiag, false,
							this.pddContext, CostantiPdD.KEY_INFO_IN_CACHE_FUNZIONE_AUTENTICAZIONE);
					errore = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
							get5XX_ErroreProcessamento("processo di autenticazione ["
									+ tipoAutenticazione + PROCESSO_FALLITO + e.getMessage(),CodiceErroreIntegrazione.CODICE_503_AUTENTICAZIONE);
					eAutenticazione = e;
					this.logCore.error("processo di autenticazione ["
							+ tipoAutenticazione + PROCESSO_FALLITO + e.getMessage(),e);
					integrationFunctionError = IntegrationFunctionError.INTERNAL_REQUEST_ERROR;
				}
				if (errore != null) {
					if(!detailsSet) {
						this.msgDiag.addKeyword(CostantiPdD.KEY_DETAILS, "");
					}
					String descrizioneErrore = null;
					try{
						descrizioneErrore = errore.getDescrizione(this.protocolFactory);
						if(descrizioneErrore!=null && descrizioneErrore.startsWith(CostantiProtocollo.PREFISSO_AUTENTICAZIONE_FALLITA) &&
								descrizioneErrore.length()>CostantiProtocollo.PREFISSO_AUTENTICAZIONE_FALLITA.length()) {
							String dettaglioErroreInterno = descrizioneErrore.substring(CostantiProtocollo.PREFISSO_AUTENTICAZIONE_FALLITA.length());
							CostantiPdD.addKeywordAutenticazioneFallita(this.msgDiag, dettaglioErroreInterno, this.pddContext, CostantiPdD.KEY_INFO_IN_CACHE_FUNZIONE_AUTENTICAZIONE_FALLITA);
						}
						else {
							this.msgDiag.addKeyword(CostantiPdD.KEY_ERRORE_PROCESSAMENTO, descrizioneErrore);
						}
					}catch(Exception e){
						this.logCore.error(DESCRIZIONE_ERROR+e.getMessage(),e);
					}
					String errorMsg =  "Riscontrato errore durante il processo di Autenticazione per il messaggio con identificativo di transazione ["+this.idTransazione+"]: "+descrizioneErrore;
					if(autenticazioneOpzionale){
						this.msgDiag.logPersonalizzato("servizioApplicativoFruitore.identificazioneTramiteCredenzialiFallita.opzionale");
						if(eAutenticazione!=null){
							this.logCore.debug(errorMsg,eAutenticazione);
						}
						else{
							this.logCore.debug(errorMsg);
						}
					}
					else{
						this.msgDiag.logPersonalizzato("servizioApplicativoFruitore.identificazioneTramiteCredenzialiFallita");
						if(eAutenticazione!=null){
							this.logCore.error(errorMsg,eAutenticazione);
						}
						else{
							this.logCore.error(errorMsg);
						}
					}
					if(!autenticazioneOpzionale){
						this.openspcoopstate.releaseResource();
		
						if (this.msgContext.isGestioneRisposta()) {
							
							if(errorMessageAutenticazione!=null) {
								this.msgContext.setMessageResponse(errorMessageAutenticazione);
							}
							else {
								if(CodiceErroreIntegrazione.CODICE_402_AUTENTICAZIONE_FALLITA.equals(errore.getCodiceErrore())){
									if(integrationFunctionError==null) {
										integrationFunctionError = IntegrationFunctionError.AUTHENTICATION;
									}
								}else{
									if(integrationFunctionError==null) {
										integrationFunctionError = IntegrationFunctionError.INTERNAL_REQUEST_ERROR;
									}
								}
								
								OpenSPCoop2Message errorOpenSPCoopMsg = (this.generatoreErrore.build(this.pddContext,integrationFunctionError,errore,
										eAutenticazione,null));
								
								if(wwwAuthenticateErrorHeader!=null) {
									errorOpenSPCoopMsg.forceTransportHeader(HttpConstants.AUTHORIZATION_RESPONSE_WWW_AUTHENTICATE, wwwAuthenticateErrorHeader);
								}
								
								/**if(ServiceBinding.REST.equals(this.requestMessage.getServiceBinding())){
									if(wwwAuthenticateErrorHeader!=null && wwwAuthenticateErrorHeader.startsWith(HttpConstants.AUTHORIZATION_PREFIX_BASIC) && 
											ServiceBinding.REST.equals(errorOpenSPCoopMsg.getServiceBinding())) {
										try {
											errorOpenSPCoopMsg.castAsRest().updateContent(null);
										}catch(Throwable e) {}
									}
								}*/
								
								this.msgContext.setMessageResponse(errorOpenSPCoopMsg);
							}
						}
						
						return false;
					}
				}
			}
			finally {
				this.transaction.getTempiElaborazione().endAutenticazione();
			}
		}
		

		boolean autenticazioneToken = false;
		if(this.gestioneTokenAutenticazione!=null) {
			autenticazioneToken = GestoreAutenticazione.isAutenticazioneTokenEnabled(this.gestioneTokenAutenticazione);
		}
		if(autenticazioneToken) {
			
			this.transaction.getTempiElaborazione().startAutenticazioneToken();
			try {
			
				String checkAuthnToken = GestoreAutenticazione.getLabel(this.gestioneTokenAutenticazione);
				this.msgDiag.addKeyword(CostantiPdD.KEY_TOKEN_AUTHN_CHECK, checkAuthnToken);
				this.msgDiag.logPersonalizzato("autenticazioneTokenInCorso");
				this.msgContext.getIntegrazione().setTokenPolicy_authn(GestoreAutenticazione.getActions(this.gestioneTokenAutenticazione));
				
				ErroreIntegrazione erroreIntegrazione = null;
				Exception eAutenticazione = null;
				OpenSPCoop2Message errorMessageAutenticazione = null;
				String wwwAuthenticateErrorHeader = null;
				String errorMessage = null;
				IntegrationFunctionError integrationFunctionError = null;
				try {
					EsitoAutenticazionePortaDelegata esito = 
							GestoreAutenticazione.verificaAutenticazioneTokenPortaDelegata(this.gestioneTokenAutenticazione, datiInvocazioneAutenticazione, this.pddContext, this.protocolFactory, this.requestMessage);
	
					if(!esito.isClientAuthenticated()) {
						erroreIntegrazione = esito.getErroreIntegrazione();
						eAutenticazione = esito.getEccezioneProcessamento();
						errorMessageAutenticazione = esito.getErrorMessage();
						wwwAuthenticateErrorHeader = esito.getWwwAuthenticateErrorHeader();					
						errorMessage = esito.getDetails();
						integrationFunctionError = esito.getIntegrationFunctionError();
					}
					
					if (erroreIntegrazione != null) {
						this.pddContext.addObject(org.openspcoop2.core.constants.Costanti.ERRORE_AUTENTICAZIONE_TOKEN, "true");
					}
					else {
						this.msgDiag.logPersonalizzato("autenticazioneTokenEffettuata");							
					}
					
				} catch (Exception e) {
					erroreIntegrazione = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
							get5XX_ErroreProcessamento("["+ RicezioneContenutiApplicativi.ID_MODULO+ "] processo di autenticazione token ["
									+ checkAuthnToken + PROCESSO_FALLITO + e.getMessage(),CodiceErroreIntegrazione.CODICE_503_AUTENTICAZIONE);
					eAutenticazione = e;
					this.logCore.error("processo di autenticazione token ["
							+ checkAuthnToken + PROCESSO_FALLITO + e.getMessage(),e);
					integrationFunctionError = IntegrationFunctionError.INTERNAL_REQUEST_ERROR;
				}
				if (erroreIntegrazione != null) {
					String descrizioneErrore = null;
					try{
						if(errorMessage!=null) {
							descrizioneErrore = errorMessage;
						}
						else {
							descrizioneErrore = erroreIntegrazione.getDescrizione(this.protocolFactory);
						}
						this.msgDiag.addKeyword(CostantiPdD.KEY_ERRORE_PROCESSAMENTO, descrizioneErrore);
					}catch(Exception e){
						this.logCore.error(DESCRIZIONE_ERROR+e.getMessage(),e);
					}
					String errorMsg =  "Riscontrato errore durante il processo di Autenticazione Token per il messaggio con identificativo di transazione ["+this.idTransazione+"]: "+descrizioneErrore;
					this.msgDiag.logPersonalizzato("autenticazioneTokenFallita");
					if(eAutenticazione!=null){
						this.logCore.error(errorMsg,eAutenticazione);
					}
					else{
						this.logCore.error(errorMsg);
					}
					
					this.openspcoopstate.releaseResource();
					
					if (this.msgContext.isGestioneRisposta()) {
						
						if(errorMessageAutenticazione!=null) {
							this.msgContext.setMessageResponse(errorMessageAutenticazione);
						}
						else {
							if(CodiceErroreIntegrazione.CODICE_445_TOKEN_AUTORIZZAZIONE_FALLITA.equals(erroreIntegrazione.getCodiceErrore())){
								integrationFunctionError = IntegrationFunctionError.TOKEN_REQUIRED_CLAIMS_NOT_FOUND;
							}else{
								integrationFunctionError = IntegrationFunctionError.INTERNAL_REQUEST_ERROR;
							}
							OpenSPCoop2Message errorOpenSPCoopMsg = (this.generatoreErrore.build(this.pddContext,integrationFunctionError,erroreIntegrazione,
									eAutenticazione,null));
							
							if(wwwAuthenticateErrorHeader!=null) {
								errorOpenSPCoopMsg.forceTransportHeader(HttpConstants.AUTHORIZATION_RESPONSE_WWW_AUTHENTICATE, wwwAuthenticateErrorHeader);
							}
							
							/**if(ServiceBinding.REST.equals(this.requestMessage.getServiceBinding())){
								if(wwwAuthenticateErrorHeader!=null && wwwAuthenticateErrorHeader.startsWith(HttpConstants.AUTHORIZATION_PREFIX_BASIC) && 
										ServiceBinding.REST.equals(errorOpenSPCoopMsg.getServiceBinding())) {
									try {
										errorOpenSPCoopMsg.castAsRest().updateContent(null);
									}catch(Throwable e) {}
								}
							}*/
							
							this.msgContext.setMessageResponse(errorOpenSPCoopMsg);
						}
					}
					return false;
					
				}
			}finally {
				this.transaction.getTempiElaborazione().endAutenticazioneToken();
			}
		}
		else {
			
			this.msgDiag.logPersonalizzato("autenticazioneTokenDisabilitata");
			
		}
		
		
		if(this.pddContext.containsKey(org.openspcoop2.pdd.core.token.Costanti.PDD_CONTEXT_TOKEN_INFORMAZIONI_NORMALIZZATE)) {
			this.informazioniTokenNormalizzate = (InformazioniToken) this.pddContext.getObject(org.openspcoop2.pdd.core.token.Costanti.PDD_CONTEXT_TOKEN_INFORMAZIONI_NORMALIZZATE);
		}
		
		ServizioApplicativo saApplicativoToken = null;
		Soggetto soggettoToken = null;
		RequestFruitore requestFruitoreToken = null;
		if(this.informazioniTokenNormalizzate!=null && 
				this.informazioniTokenNormalizzate.getClientId()!=null && StringUtils.isNotEmpty(this.informazioniTokenNormalizzate.getClientId())) {
			this.transaction.getTempiElaborazione().startAutenticazioneApplicativoToken();
			try {
			
				this.msgDiag.addKeyword(CostantiPdD.KEY_TOKEN_CLIENT_ID, this.informazioniTokenNormalizzate.getClientId());
				this.msgDiag.logPersonalizzato("autenticazioneApplicativoTokenInCorso");
				
				ErroreIntegrazione errore = null;
				Exception eAutenticazione = null;
				IntegrationFunctionError integrationFunctionError = null;
				try {						
						
					EsitoAutenticazionePortaDelegata esito = 
							GestoreAutenticazione.verificaAutenticazionePortaDelegata(CostantiConfigurazione.AUTENTICAZIONE_TOKEN, 
									datiInvocazioneAutenticazione, new ParametriAutenticazione(this.portaDelegata.getProprietaAutenticazioneList()), 
									this.pddContext, this.protocolFactory, this.requestMessage); 
					CostantiPdD.addKeywordInCache(this.msgDiag, esito.isEsitoPresenteInCache(),
							this.pddContext, CostantiPdD.KEY_INFO_IN_CACHE_FUNZIONE_AUTENTICAZIONE_TOKEN);
					if(!esito.isClientAuthenticated()) {
						errore = esito.getErroreIntegrazione();
						eAutenticazione = esito.getEccezioneProcessamento();
						integrationFunctionError = esito.getIntegrationFunctionError();
						this.pddContext.addObject(org.openspcoop2.core.constants.Costanti.ERRORE_AUTENTICAZIONE_TOKEN, "true");
					}
					else {
						if(esito.isClientIdentified()) {
							this.idApplicativoToken = esito.getIdServizioApplicativo();
							this.msgDiag.addKeyword(CostantiPdD.KEY_TOKEN_SERVIZIO_APPLICATIVO, this.idApplicativoToken.getNome()+NamingUtils.LABEL_DOMINIO+this.idApplicativoToken.getIdSoggettoProprietario().toString());
							this.msgDiag.logPersonalizzato("autenticazioneApplicativoTokenEffettuata.identificazioneRiuscita");
							
							this.pddContext.addObject(org.openspcoop2.core.constants.Costanti.ID_APPLICATIVO_TOKEN, this.idApplicativoToken);
							try {
								requestFruitoreToken = GestoreRichieste.readFruitoreToken(this.requestInfo, this.idApplicativoToken.getIdSoggettoProprietario(), this.idApplicativoToken);
								saApplicativoToken = this.configurazionePdDReader.getServizioApplicativo(this.idApplicativoToken, this.requestInfo);
								Map<String, String> configProperties = this.configurazionePdDReader.getProprietaConfigurazione(saApplicativoToken);
					            if (configProperties != null && !configProperties.isEmpty()) {
					            	this.pddContext.addObject(org.openspcoop2.core.constants.Costanti.PROPRIETA_APPLICATIVO_TOKEN, configProperties);
								}	
							}catch(Throwable t) {
								// ignore
							}
							try {
								soggettoToken = this.registroServiziReader.getSoggetto(this.idApplicativoToken.getIdSoggettoProprietario(), null, this.requestInfo);
								Map<String, String> configProperties = this.registroServiziReader.getProprietaConfigurazione(soggettoToken);
					            if (configProperties != null && !configProperties.isEmpty()) {
					            	this.pddContext.addObject(org.openspcoop2.core.constants.Costanti.PROPRIETA_SOGGETTO_PROPRIETARIO_APPLICATIVO_TOKEN, configProperties);
								}	
							}catch(Throwable t) {
								// ignore
							}
						}
						else {
							errore = esito.getErroreIntegrazione();
							eAutenticazione = esito.getEccezioneProcessamento();
							integrationFunctionError = esito.getIntegrationFunctionError();
							if(integrationFunctionError!=null && IntegrationFunctionError.AUTHENTICATION_INVALID_CREDENTIALS.equals(integrationFunctionError)) {
								this.pddContext.addObject(org.openspcoop2.core.constants.Costanti.ERRORE_AUTENTICAZIONE_TOKEN, "true");
							}
							if(errore==null) {
								this.msgDiag.logPersonalizzato("autenticazioneApplicativoTokenEffettuata.identificazioneFallita");
							}
						}
					}
				} catch (Exception e) {
					CostantiPdD.addKeywordInCache(this.msgDiag, false,
							this.pddContext, CostantiPdD.KEY_INFO_IN_CACHE_FUNZIONE_AUTENTICAZIONE_TOKEN);
					errore = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
							get5XX_ErroreProcessamento("processo di autenticazione applicativo token fallito, " + e.getMessage(),CodiceErroreIntegrazione.CODICE_503_AUTENTICAZIONE);
					eAutenticazione = e;
					this.logCore.error("processo di autenticazione applicativo token fallito, " + e.getMessage(),e);
					integrationFunctionError = IntegrationFunctionError.INTERNAL_REQUEST_ERROR;
				}
				if (errore != null) {
					String descrizioneErrore = null;
					try{
						descrizioneErrore = errore.getDescrizione(this.protocolFactory);
						this.msgDiag.addKeyword(CostantiPdD.KEY_ERRORE_PROCESSAMENTO, descrizioneErrore);
					}catch(Exception e){
						this.logCore.error(DESCRIZIONE_ERROR+e.getMessage(),e);
					}
					String errorMsg =  "Riscontrato errore durante il processo di autenticazione dell'applicativo token per il messaggio con identificativo di transazione ["+this.idTransazione+"]: "+descrizioneErrore;
					this.msgDiag.logPersonalizzato("autenticazioneApplicativoTokenFallita");
					if(eAutenticazione!=null){
						this.logCore.error(errorMsg,eAutenticazione);
					}
					else{
						this.logCore.error(errorMsg);
					}
					this.openspcoopstate.releaseResource();
	
					if (this.msgContext.isGestioneRisposta()) {
						
						if(integrationFunctionError==null) {
							integrationFunctionError = IntegrationFunctionError.INTERNAL_REQUEST_ERROR;
						}
												
						OpenSPCoop2Message errorOpenSPCoopMsg = (this.generatoreErrore.build(this.pddContext,integrationFunctionError,errore,
								eAutenticazione,null));
						
						this.msgContext.setMessageResponse(errorOpenSPCoopMsg);
						
					}
					
					return false;
				}
			}
			finally {
				this.transaction.getTempiElaborazione().endAutenticazioneApplicativoToken();
			}
		}
		
		if(propertiesReader.isTransazioniEnabled() && 
				(credenzialeTrasporto!=null || this.informazioniTokenNormalizzate!=null)) {
			CredenzialiMittente credenzialiMittente = new CredenzialiMittente();
			
			try{

				if(credenzialeTrasporto!=null) {
					GestoreAutenticazione.updateCredenzialiTrasporto(this.identitaPdD, RicezioneContenutiApplicativi.ID_MODULO, this.idTransazione, tipoAutenticazione, credenzialeTrasporto, credenzialiMittente, 
							this.openspcoopstate, "RicezioneContenutiApplicativi.credenzialiTrasporto", this.requestInfo);
				}
				
				if(this.informazioniTokenNormalizzate!=null) {
					GestoreAutenticazione.updateCredenzialiToken(this.identitaPdD, RicezioneContenutiApplicativi.ID_MODULO, this.idTransazione, this.informazioniTokenNormalizzate, this.idApplicativoToken, credenzialiMittente, 
							this.openspcoopstate, "RicezioneContenutiApplicativi.credenzialiToken", this.requestInfo);
				}
				
				this.transaction.setCredenzialiMittente(credenzialiMittente);
							
			} catch (Exception e) {
				this.msgDiag.addKeywordErroreProcessamento(e,"Aggiornamento Credenziali Fallito");
				this.msgDiag.logErroreGenerico(e,"GestoreAutenticazione.updateCredenziali");
				this.logCore.error("GestoreAutenticazione.updateCredenziali error: "+e.getMessage(),e);

				this.openspcoopstate.releaseResource();
				
				if (this.msgContext.isGestioneRisposta()) {
					this.msgContext.setMessageResponse((this.generatoreErrore.build(this.pddContext,IntegrationFunctionError.INTERNAL_REQUEST_ERROR,
							ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
							get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_503_AUTENTICAZIONE),e,null)));
				}
				return false;
				
			}
			
		}
		
		
		
		
		

		
		
		
		
		
		// *** Aggiorno informazioni aplicativo ***
		
		this.richiestaDelegata.setServizioApplicativo(this.servizioApplicativo);
		// Identita' errore
		this.msgDiag.updatePorta(this.identificativoPortaDelegata.getNome(), this.requestInfo);
		this.msgDiag.setServizioApplicativo(this.servizioApplicativo);
		this.msgDiag.addKeyword(CostantiPdD.KEY_SA_FRUITORE, this.servizioApplicativo);
		// identita
		if(!CostantiPdD.SERVIZIO_APPLICATIVO_ANONIMO.equals(this.servizioApplicativo)){
			this.msgContext.getIntegrazione().setServizioApplicativoFruitore(this.servizioApplicativo);
		}
		this.generatoreErrore.updateInformazioniCooperazione(this.servizioApplicativo);
		
		/*
		 * ------ msgDiagnosito di avvenuta ricezione della richiesta da parte del SIL -----------
		 */
		if(CostantiPdD.SERVIZIO_APPLICATIVO_ANONIMO.equals(this.servizioApplicativo)){
			this.msgDiag.logPersonalizzato("ricevutaRichiestaApplicativa.mittenteAnonimo");
		}else{
			this.msgDiag.logPersonalizzato("ricevutaRichiestaApplicativa");
		}
		
		/*
		 * Get Servizio Applicativo trasporto
		 */
		this.msgDiag.mediumDebug("Get servizio applicativo...");
		boolean anonimo = (this.sa==null);
		try{
			if(requestFruitore==null) {
				requestFruitore = GestoreRichieste.readFruitoreTrasporto(this.requestInfo, this.soggettoFruitore, idSAFruitore);
			}
			if(this.sa==null) {
				if(requestFruitore!=null) {
					if(	requestFruitore.getServizioApplicativoFruitoreAnonimo()!=null &&
						requestFruitore.getServizioApplicativoFruitoreAnonimo().equals(CostantiPdD.SERVIZIO_APPLICATIVO_ANONIMO)) {
						anonimo = true;
					}
					else {
						this.sa = requestFruitore.getServizioApplicativoFruitore();
						anonimo = (this.sa==null);
					}
				}
				else if(idSAFruitore!=null) {
					this.sa = this.configurazionePdDReader.getServizioApplicativo(idSAFruitore, this.requestInfo);
				}
			}
		}catch (Exception e) {
			if( !(e instanceof DriverConfigurazioneNotFound) || !(CostantiPdD.SERVIZIO_APPLICATIVO_ANONIMO.equals(this.servizioApplicativo)) ){
				this.msgDiag.logErroreGenerico(e, "getServizioApplicativo("+this.servizioApplicativo+")");
				this.openspcoopstate.releaseResource();
				if (this.msgContext.isGestioneRisposta()) {
					this.msgContext.setMessageResponse((this.generatoreErrore.build(this.pddContext,IntegrationFunctionError.INTERNAL_REQUEST_ERROR,
							ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
							get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_536_CONFIGURAZIONE_NON_DISPONIBILE),e,null)));
				}
				return false;
			}
			else {
				anonimo = true;
			}
		}
		if(requestFruitore==null) {
			RequestFruitore rf = new RequestFruitore(); 
			
			rf.setIdSoggettoFruitore(this.soggettoFruitore);
			try {
				org.openspcoop2.core.registry.Soggetto soggettoRegistry = this.registroServiziReader.getSoggetto(this.soggettoFruitore, null, this.requestInfo); 
				rf.setSoggettoFruitoreRegistry(soggettoRegistry);
			}catch(Throwable t) {
				this.logCore.error(ERRORE_LETTURA_SOGGETTO+this.soggettoFruitore+DAL_REGISTRO+t.getMessage(),t);
			}	
			try {
				org.openspcoop2.core.config.Soggetto soggettoConfig = this.configurazionePdDReader.getSoggetto(this.soggettoFruitore, this.requestInfo); 
				rf.setSoggettoFruitoreConfig(soggettoConfig);
			}catch(Throwable t) {
				this.logCore.error(ERRORE_LETTURA_SOGGETTO+this.soggettoFruitore+"' dalla configurazione: "+t.getMessage(),t);
			}	
			try {
				String idPorta = this.configurazionePdDReader.getIdentificativoPorta(this.soggettoFruitore, this.protocolFactory, this.requestInfo);
				rf.setSoggettoFruitoreIdentificativoPorta(idPorta);
			}catch(Throwable t) {
				this.logCore.error("Errore durante la lettura dell'identificativo porta del soggetto '"+this.soggettoFruitore+DAL_REGISTRO+t.getMessage(),t);
			}	
			try {
				boolean soggettoVirtualeFRU = this.configurazionePdDReader.isSoggettoVirtuale(this.soggettoFruitore, this.requestInfo);
				rf.setSoggettoFruitoreSoggettoVirtuale(soggettoVirtualeFRU);
			}catch(Throwable t) {
				this.logCore.error("Errore durante la lettura dell'indicazione sul soggetto virtuale associato al soggetto '"+this.soggettoFruitore+DAL_REGISTRO+t.getMessage(),t);
			}	
			try {
				if(rf!=null) {
					if(rf.getSoggettoFruitoreRegistry().getPortaDominio()!=null &&
							StringUtils.isNotEmpty(rf.getSoggettoFruitoreRegistry().getPortaDominio())) {
						PortaDominio pdd = this.registroServiziReader.getPortaDominio(rf.getSoggettoFruitoreRegistry().getPortaDominio(), null, this.requestInfo);
						rf.setSoggettoFruitorePddReaded(true);
						rf.setSoggettoFruitorePdd(pdd);
					}
					else {
						rf.setSoggettoFruitorePddReaded(true);
					}
				}
			}catch(Throwable t) {
				this.logCore.error("Errore durante la lettura dei dati della pdd del soggetto '"+this.soggettoFruitore+DAL_REGISTRO+t.getMessage(),t);
			}	
			try {
				String impl = this.registroServiziReader.getImplementazionePdD(this.soggettoFruitore, null, this.requestInfo);
				rf.setSoggettoFruitoreImplementazionePdd(impl);
			}catch(Throwable t) {
				this.logCore.error("Errore durante la lettura dell'implementazione pdd del soggetto '"+this.soggettoFruitore+DAL_REGISTRO+t.getMessage(),t);
			}	
				
			rf.setIdServizioApplicativoFruitore(idSAFruitore);
			rf.setServizioApplicativoFruitore(this.sa);
			if(anonimo) {
				rf.setServizioApplicativoFruitoreAnonimo(CostantiPdD.SERVIZIO_APPLICATIVO_ANONIMO);
			}
			
			try {
				GestoreRichieste.saveRequestFruitoreTrasporto(this.requestInfo, rf);
			} catch (Throwable e) {
				this.logCore.error("Errore durante il salvataggio nella cache e nel thread context delle informazioni sul fruitore trasporto: "+e.getMessage(),e);
			}
		}
		
		
		/*
		 * ----- Aggiornamento gestione errore specifica per il servizio
		 * applicativo -------
		 */
		this.msgDiag.mediumDebug("Aggiornamento gestione errore del servizio applicativo...");
		try {
			this.configurazionePdDReader.aggiornaProprietaGestioneErrorePD(this.proprietaErroreAppl, this.sa);
			if(this.msgContext.isForceFaultAsXML()){
				this.proprietaErroreAppl.setFaultAsXML(true); // es. se siamo in una richiesta http senza SOAP, un SoapFault non ha senso
			}
		} catch (Exception e) {
			this.msgDiag.logErroreGenerico(e, "aggiornaProprietaGestioneErrorePD(this.proprietaErroreAppl,"+this.servizioApplicativo+")");
			this.openspcoopstate.releaseResource();
			if (this.msgContext.isGestioneRisposta()) {
				this.msgContext.setMessageResponse((this.generatoreErrore.build(this.pddContext,IntegrationFunctionError.INTERNAL_REQUEST_ERROR,
						ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
						get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_536_CONFIGURAZIONE_NON_DISPONIBILE),e,null)));
			}
			return false;
		}
		this.msgContext.setProprietaErroreAppl(this.proprietaErroreAppl);
		this.generatoreErrore.updateProprietaErroreApplicativo(this.proprietaErroreAppl);
		
		
		
		// ** token **
		if(this.idApplicativoToken!=null) {
			try {
				if(requestFruitoreToken==null) {
					requestFruitoreToken = GestoreRichieste.readFruitoreToken(this.requestInfo, this.idApplicativoToken.getIdSoggettoProprietario(), this.idApplicativoToken);
				}
			}catch(Throwable t) {
				this.logCore.error("Errore durante la lettura del fruitore '"+this.idApplicativoToken+"' dall'oggetto request info: "+t.getMessage(),t);
			}
			
			if(saApplicativoToken==null) {
				try {
					saApplicativoToken = this.configurazionePdDReader.getServizioApplicativo(this.idApplicativoToken, this.requestInfo);
				}catch(Throwable t) {
					this.logCore.error("Errore durante la lettura dell'applicativo '"+this.idApplicativoToken+DAL_REGISTRO+t.getMessage(),t);
				}
				if(saApplicativoToken!=null) {
					try {
						Map<String, String> configProperties = this.configurazionePdDReader.getProprietaConfigurazione(saApplicativoToken);
						if (configProperties != null && !configProperties.isEmpty()) {
							this.pddContext.addObject(org.openspcoop2.core.constants.Costanti.PROPRIETA_APPLICATIVO_TOKEN, configProperties);
						}	
					}catch(Throwable t) {
						this.logCore.error("Errore durante la lettura delle proprietà dell'applicativo '"+this.idApplicativoToken+DAL_REGISTRO+t.getMessage(),t);
					}
				}
			}
			
			if(soggettoToken==null) {
				try {
					soggettoToken = this.registroServiziReader.getSoggetto(this.idApplicativoToken.getIdSoggettoProprietario(), null, this.requestInfo);
				}catch(Throwable t) {
					this.logCore.error(ERRORE_LETTURA_SOGGETTO+this.idApplicativoToken.getIdSoggettoProprietario()+DAL_REGISTRO+t.getMessage(),t);
				}
				if(soggettoToken!=null) {
					try {
						Map<String, String> configProperties = this.registroServiziReader.getProprietaConfigurazione(soggettoToken);
			            if (configProperties != null && !configProperties.isEmpty()) {
			            	this.pddContext.addObject(org.openspcoop2.core.constants.Costanti.PROPRIETA_SOGGETTO_PROPRIETARIO_APPLICATIVO_TOKEN, configProperties);
						}	
					}catch(Throwable t) {
						this.logCore.error("Errore durante la lettura delle proprietà del soggetto '"+this.idApplicativoToken.getIdSoggettoProprietario()+DAL_REGISTRO+t.getMessage(),t);
					}	
				}
			}
		}
		if(requestFruitoreToken==null && this.idApplicativoToken!=null && soggettoToken!=null && saApplicativoToken!=null) {
			RequestFruitore rf = new RequestFruitore(); 
			
			rf.setIdSoggettoFruitore(this.idApplicativoToken.getIdSoggettoProprietario());
			rf.setSoggettoFruitoreRegistry(soggettoToken);	
			try {
				org.openspcoop2.core.config.Soggetto soggettoConfig = this.configurazionePdDReader.getSoggetto(this.idApplicativoToken.getIdSoggettoProprietario(), this.requestInfo); 
				rf.setSoggettoFruitoreConfig(soggettoConfig);
			}catch(Throwable t) {
				this.logCore.error(ERRORE_LETTURA_SOGGETTO+this.idApplicativoToken.getIdSoggettoProprietario()+"' dalla configurazione: "+t.getMessage(),t);
			}	
			try {
				String idPorta = this.configurazionePdDReader.getIdentificativoPorta(this.idApplicativoToken.getIdSoggettoProprietario(), this.protocolFactory, this.requestInfo);
				rf.setSoggettoFruitoreIdentificativoPorta(idPorta);
			}catch(Throwable t) {
				this.logCore.error("Errore durante la lettura dell'identificativo porta del soggetto '"+this.idApplicativoToken.getIdSoggettoProprietario()+DAL_REGISTRO+t.getMessage(),t);
			}	
			try {
				boolean soggettoVirtualeFRU = this.configurazionePdDReader.isSoggettoVirtuale(this.idApplicativoToken.getIdSoggettoProprietario(), this.requestInfo);
				rf.setSoggettoFruitoreSoggettoVirtuale(soggettoVirtualeFRU);
			}catch(Throwable t) {
				this.logCore.error("Errore durante la lettura dell'indicazione sul soggetto virtuale associato al soggetto '"+this.idApplicativoToken.getIdSoggettoProprietario()+DAL_REGISTRO+t.getMessage(),t);
			}	
			try {
				if(rf!=null) {
					if(rf.getSoggettoFruitoreRegistry().getPortaDominio()!=null &&
							StringUtils.isNotEmpty(rf.getSoggettoFruitoreRegistry().getPortaDominio())) {
						PortaDominio pdd = this.registroServiziReader.getPortaDominio(rf.getSoggettoFruitoreRegistry().getPortaDominio(), null, this.requestInfo);
						rf.setSoggettoFruitorePddReaded(true);
						rf.setSoggettoFruitorePdd(pdd);
					}
					else {
						rf.setSoggettoFruitorePddReaded(true);
					}
				}
			}catch(Throwable t) {
				this.logCore.error("Errore durante la lettura dei dati della pdd del soggetto '"+this.idApplicativoToken.getIdSoggettoProprietario()+DAL_REGISTRO+t.getMessage(),t);
			}	
			try {
				String impl = this.registroServiziReader.getImplementazionePdD(this.idApplicativoToken.getIdSoggettoProprietario(), null, this.requestInfo);
				rf.setSoggettoFruitoreImplementazionePdd(impl);
			}catch(Throwable t) {
				this.logCore.error("Errore durante la lettura dell'implementazione pdd del soggetto '"+this.idApplicativoToken.getIdSoggettoProprietario()+DAL_REGISTRO+t.getMessage(),t);
			}	
				
			rf.setIdServizioApplicativoFruitore(this.idApplicativoToken);
			rf.setServizioApplicativoFruitore(saApplicativoToken);
			/** nel token non ha senso rf.setServizioApplicativoFruitoreAnonimo(saApplicativoToken==null); */
			
			try {
				GestoreRichieste.saveRequestFruitoreToken(this.requestInfo, rf);
			} catch (Throwable e) {
				this.logCore.error("Errore durante il salvataggio nella cache e nel thread context delle informazioni sul fruitore token: "+e.getMessage(),e);
			}
		}
		
		return true;
		
	}

	private static final String ERRORE_LETTURA_SOGGETTO = "Errore durante la lettura del soggetto '";
	private static final String DAL_REGISTRO = "' dal registro: ";
	private static final String PROCESSO_FALLITO = "] fallito, ";
	private static final String DESCRIZIONE_ERROR = "getDescrizione Error:";
}
