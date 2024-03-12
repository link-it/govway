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

import java.util.List;

import org.openspcoop2.core.config.GestioneTokenAutenticazione;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.id.IDPortaDelegata;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.transazioni.utils.CredenzialiMittente;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.pdd.config.ConfigurazionePdDManager;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.core.CostantiPdD;
import org.openspcoop2.pdd.core.PdDContext;
import org.openspcoop2.pdd.core.autenticazione.GestoreAutenticazione;
import org.openspcoop2.pdd.core.handlers.InRequestContext;
import org.openspcoop2.pdd.core.state.IOpenSPCoopState;
import org.openspcoop2.pdd.core.token.GestoreToken;
import org.openspcoop2.pdd.core.token.InformazioniToken;
import org.openspcoop2.pdd.core.token.PolicyGestioneToken;
import org.openspcoop2.pdd.core.token.pd.EsitoGestioneTokenPortaDelegata;
import org.openspcoop2.pdd.core.token.pd.EsitoPresenzaTokenPortaDelegata;
import org.openspcoop2.pdd.core.token.pd.GestioneToken;
import org.openspcoop2.pdd.core.transazioni.Transaction;
import org.openspcoop2.pdd.logger.MsgDiagnostico;
import org.openspcoop2.pdd.services.error.RicezioneContenutiApplicativiInternalErrorGenerator;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.constants.CodiceErroreIntegrazione;
import org.openspcoop2.protocol.sdk.constants.ErroreIntegrazione;
import org.openspcoop2.protocol.sdk.constants.ErroriIntegrazione;
import org.openspcoop2.protocol.sdk.constants.IntegrationFunctionError;
import org.openspcoop2.protocol.sdk.state.RequestInfo;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.slf4j.Logger;

/**
 * RicezioneContenutiApplicativiGestioneToken
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class RicezioneContenutiApplicativiGestioneToken {

	private MsgDiagnostico msgDiag;
	private Logger logCore;
	
	private PortaDelegata portaDelegata;
	private IDPortaDelegata identificativoPortaDelegata;
	
	private OpenSPCoop2Message requestMessage;
	
	private RicezioneContenutiApplicativiContext msgContext;
	private RicezioneContenutiApplicativiInternalErrorGenerator generatoreErrore;
	private InRequestContext inRequestContext;
	
	private ConfigurazionePdDManager configurazionePdDReader;
	
	private PdDContext pddContext;
	private String idTransazione;
	private IOpenSPCoopState openspcoopstate;
	private Transaction transaction;
	private RequestInfo requestInfo;
	
	private IProtocolFactory<?> protocolFactory;
	
	private IDSoggetto identitaPdD;
	
	public RicezioneContenutiApplicativiGestioneToken(MsgDiagnostico msgDiag, Logger logCore,
			PortaDelegata portaDelegata, IDPortaDelegata identificativoPortaDelegata,
			OpenSPCoop2Message requestMessage,
			RicezioneContenutiApplicativiContext msgContext, RicezioneContenutiApplicativiInternalErrorGenerator generatoreErrore, InRequestContext inRequestContext,
			ConfigurazionePdDManager configurazionePdDReader,
			PdDContext pddContext, String idTransazione,
			IOpenSPCoopState openspcoopstate, Transaction transaction, RequestInfo requestInfo,
			IProtocolFactory<?> protocolFactory,
			IDSoggetto identitaPdD) {
		this.msgDiag = msgDiag;
		this.logCore = logCore;
				
		this.portaDelegata = portaDelegata;
		this.identificativoPortaDelegata = identificativoPortaDelegata;
		
		this.requestMessage = requestMessage;
		
		this.msgContext = msgContext;
		this.generatoreErrore = generatoreErrore;
		this.inRequestContext = inRequestContext;
		
		this.configurazionePdDReader = configurazionePdDReader;
		
		this.pddContext = pddContext;
		this.idTransazione = idTransazione;
		this.openspcoopstate = openspcoopstate;
		this.transaction = transaction;
		this.requestInfo = requestInfo; 
		
		this.protocolFactory = protocolFactory;
		
		this.identitaPdD = identitaPdD;
		
	}
	
	// Result
	private GestioneTokenAutenticazione gestioneTokenAutenticazione = null;
	private String token = null;
	private String tipoGestioneToken = null;
	
	public GestioneTokenAutenticazione getGestioneTokenAutenticazione() {
		return this.gestioneTokenAutenticazione;
	}
	public String getToken() {
		return this.token;
	}
	public String getTipoGestioneToken() {
		return this.tipoGestioneToken;
	}
	
	public boolean process() {
		
		try {
			this.tipoGestioneToken = this.configurazionePdDReader.getGestioneToken(this.portaDelegata);
			if(this.portaDelegata!=null && this.portaDelegata.getGestioneToken()!=null) {
				this.gestioneTokenAutenticazione = this.portaDelegata.getGestioneToken().getAutenticazione();
			}
		} catch (Exception e) {
			this.msgDiag.logErroreGenerico(e, "letturaToken");
			this.logCore.error(this.msgDiag.getMessaggio_replaceKeywords("letturaToken"),e);
			this.openspcoopstate.releaseResource();
			if (this.msgContext.isGestioneRisposta()) {
				this.msgContext.setMessageResponse((this.generatoreErrore.build(this.pddContext,IntegrationFunctionError.INTERNAL_REQUEST_ERROR,
						ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
						get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_536_CONFIGURAZIONE_NON_DISPONIBILE), e,null)));
			}
			return false;
		}
		
		String gestioneTokenPrefix = "processo di gestione token ["+ this.tipoGestioneToken + "] ";
		
		this.msgDiag.mediumDebug("GestioneToken...");
		this.msgContext.getIntegrazione().setTipoGestioneToken(this.tipoGestioneToken);
		if (this.tipoGestioneToken == null) {

			this.msgDiag.logPersonalizzato("gestioneTokenDisabilitata");
			
		} else {

			this.transaction.getTempiElaborazione().startToken();
			try {
			
				ErroreIntegrazione errore = null;
				Exception eGestioneToken = null;
				OpenSPCoop2Message errorMessageGestioneToken = null;
				String wwwAuthenticateErrorHeader = null;
				boolean fineGestione = false;
				IntegrationFunctionError integrationFunctionError = null;
				try {
					
					PolicyGestioneToken policyGestioneToken = this.configurazionePdDReader.getPolicyGestioneToken(this.portaDelegata, this.requestInfo);
					
					this.pddContext.addObject(org.openspcoop2.pdd.core.token.Costanti.PDD_CONTEXT_TOKEN_POLICY, 
							policyGestioneToken.getName());
					this.pddContext.addObject(org.openspcoop2.pdd.core.token.Costanti.PDD_CONTEXT_TOKEN_REALM,
							policyGestioneToken.getRealm());
					this.pddContext.addObject(org.openspcoop2.pdd.core.token.Costanti.PDD_CONTEXT_TOKEN_MESSAGE_ERROR_BODY_EMPTY,
							policyGestioneToken.isMessageErrorGenerateEmptyMessage());
					this.pddContext.addObject(org.openspcoop2.pdd.core.token.Costanti.PDD_CONTEXT_TOKEN_MESSAGE_ERROR_GENERIC_MESSAGE,
							policyGestioneToken.isMessageErrorGenerateGenericMessage());
					
					this.msgDiag.addKeyword(CostantiPdD.KEY_TOKEN_POLICY_GESTIONE, this.tipoGestioneToken);
					this.msgContext.getIntegrazione().setTokenPolicy(this.tipoGestioneToken);
					this.msgDiag.addKeyword(CostantiPdD.KEY_TOKEN_POLICY_AZIONI, policyGestioneToken.getLabelAzioniGestioneToken());
					this.msgContext.getIntegrazione().setTokenPolicyActions(policyGestioneToken.getAzioniGestioneToken());
					this.msgDiag.addKeyword(CostantiPdD.KEY_TOKEN_TIPO, policyGestioneToken.getLabelTipoToken());
					this.msgDiag.logPersonalizzato("gestioneTokenInCorso");
					
					org.openspcoop2.pdd.core.token.pd.DatiInvocazionePortaDelegata datiInvocazione = new org.openspcoop2.pdd.core.token.pd.DatiInvocazionePortaDelegata();
					datiInvocazione.setInfoConnettoreIngresso(this.inRequestContext.getConnettore());
					datiInvocazione.setState(this.openspcoopstate.getStatoRichiesta());
					datiInvocazione.setIdModulo(this.msgContext.getIdModulo());
					datiInvocazione.setMessage(this.requestMessage);
					datiInvocazione.setIdPD(this.identificativoPortaDelegata);
					datiInvocazione.setPd(this.portaDelegata);		
					datiInvocazione.setPolicyGestioneToken(policyGestioneToken);
					datiInvocazione.setRequestInfo(this.requestInfo);
					
					GestoreToken.validazioneConfigurazione(datiInvocazione); // assicura che la configurazione sia corretta
					
					GestioneToken gestioneTokenEngine = new GestioneToken(this.logCore, this.idTransazione, this.pddContext, this.protocolFactory);
					
					// cerco token
					
					this.msgDiag.addKeyword(CostantiPdD.KEY_TOKEN_POSIZIONE, policyGestioneToken.getLabelPosizioneToken());
					this.msgDiag.logPersonalizzato("gestioneTokenInCorso.verificaPresenzaToken");
					
					EsitoPresenzaTokenPortaDelegata esitoPresenzaToken = gestioneTokenEngine.verificaPresenzaToken(datiInvocazione);
					EsitoGestioneTokenPortaDelegata esitoValidazioneToken = null;
					EsitoGestioneTokenPortaDelegata esitoIntrospectionToken = null;
					EsitoGestioneTokenPortaDelegata esitoUserInfoToken = null;
					if(esitoPresenzaToken.isPresente()) {
						this.msgDiag.addKeyword(CostantiPdD.KEY_TOKEN, esitoPresenzaToken.getToken());
						this.msgDiag.logPersonalizzato("gestioneTokenInCorso.verificaPresenzaToken.trovato"); // stampa del token info
						
						this.token = esitoPresenzaToken.getToken();
						this.pddContext.addObject(org.openspcoop2.pdd.core.token.Costanti.PDD_CONTEXT_TOKEN_POSIZIONE, esitoPresenzaToken);
						
						this.msgDiag.logPersonalizzato("gestioneTokenInCorso.verificaPresenzaToken.completataSuccesso");
	
						
						// validazione jwt
						if(!fineGestione) {
							
							if(policyGestioneToken.isValidazioneJWT()) {
							
								this.msgDiag.logPersonalizzato("gestioneTokenInCorso.validazioneToken");
								
								esitoValidazioneToken = gestioneTokenEngine.validazioneJWTToken(datiInvocazione, esitoPresenzaToken);
								if(esitoValidazioneToken.isValido()) {
									
									this.msgDiag.logPersonalizzato("gestioneTokenInCorso.validazioneToken.completataSuccesso");
									
									this.msgDiag.addKeyword(CostantiPdD.KEY_TOKEN_INFO, esitoValidazioneToken.getInformazioniToken().getRawResponse());
									
									this.pddContext.addObject(org.openspcoop2.pdd.core.token.Costanti.PDD_CONTEXT_TOKEN_ESITO_VALIDAZIONE, esitoValidazioneToken);
									
									if(esitoValidazioneToken.isInCache()) {
										this.msgDiag.logPersonalizzato("gestioneTokenInCorso.validazioneToken.inCache");
									}
									else {
										this.msgDiag.logPersonalizzato("gestioneTokenInCorso.validazioneToken.validato");
									}
								}
								else {
									
									this.msgDiag.addKeyword(CostantiPdD.KEY_ERRORE_PROCESSAMENTO, esitoValidazioneToken.getDetails());
									if(policyGestioneToken.isValidazioneJWTWarningOnly()) {
										this.msgDiag.logPersonalizzato("gestioneTokenInCorso.validazioneToken.warningOnly.fallita");
									}
									else {
										this.msgDiag.logPersonalizzato("gestioneTokenInCorso.validazioneToken.fallita");
										fineGestione = true;
									}
									
									String msgErrore = gestioneTokenPrefix+"(validazione JWT) fallito: " + esitoValidazioneToken.getDetails();
									if(esitoValidazioneToken.getEccezioneProcessamento()!=null) {
										this.logCore.error(msgErrore,esitoValidazioneToken.getEccezioneProcessamento());
									}
									else {
										this.logCore.error(msgErrore);
									}
								
									errore = esitoValidazioneToken.getErroreIntegrazione();
									eGestioneToken = esitoValidazioneToken.getEccezioneProcessamento();
									errorMessageGestioneToken = esitoValidazioneToken.getErrorMessage();
									wwwAuthenticateErrorHeader = esitoValidazioneToken.getWwwAuthenticateErrorHeader();
									integrationFunctionError = esitoValidazioneToken.getIntegrationFunctionError();
									
								}
							}
							else {
								this.msgDiag.logPersonalizzato("gestioneTokenInCorso.validazioneToken.disabilitata");
							}
							
						}
						
						
						// introspection
						if(!fineGestione) {
							
							if(policyGestioneToken.isIntrospection()) {
							
								this.msgDiag.addKeyword(CostantiPdD.KEY_TOKEN_ENDPOINT_SERVIZIO_INTROSPECTION, policyGestioneToken.getIntrospectionEndpoint());
								
								this.msgDiag.logPersonalizzato("gestioneTokenInCorso.introspectionToken");
								
								esitoIntrospectionToken = gestioneTokenEngine.introspectionToken(datiInvocazione, esitoPresenzaToken);
								if(esitoIntrospectionToken.isValido()) {
									
									this.msgDiag.logPersonalizzato("gestioneTokenInCorso.introspectionToken.completataSuccesso");
									
									this.msgDiag.addKeyword(CostantiPdD.KEY_TOKEN_INFO, esitoIntrospectionToken.getInformazioniToken().getRawResponse());
									
									this.pddContext.addObject(org.openspcoop2.pdd.core.token.Costanti.PDD_CONTEXT_TOKEN_ESITO_INTROSPECTION, esitoIntrospectionToken);
									
									if(esitoIntrospectionToken.isInCache()) {
										this.msgDiag.logPersonalizzato("gestioneTokenInCorso.introspectionToken.inCache");
									}
									else {
										this.msgDiag.logPersonalizzato("gestioneTokenInCorso.introspectionToken.validato");
									}
								}
								else {
									
									this.msgDiag.addKeyword(CostantiPdD.KEY_ERRORE_PROCESSAMENTO, esitoIntrospectionToken.getDetails());
									if(policyGestioneToken.isIntrospectionWarningOnly()) {
										this.msgDiag.logPersonalizzato("gestioneTokenInCorso.introspectionToken.warningOnly.fallita");
									}
									else {
										this.msgDiag.logPersonalizzato("gestioneTokenInCorso.introspectionToken.fallita");
										fineGestione = true;
									}
									
									String msgErrore = gestioneTokenPrefix+"(Introspection) fallito: " + esitoIntrospectionToken.getDetails();
									if(esitoIntrospectionToken.getEccezioneProcessamento()!=null) {
										this.logCore.error(msgErrore,esitoIntrospectionToken.getEccezioneProcessamento());
									}
									else {
										this.logCore.error(msgErrore);
									}
								
									errore = esitoIntrospectionToken.getErroreIntegrazione();
									eGestioneToken = esitoIntrospectionToken.getEccezioneProcessamento();
									errorMessageGestioneToken = esitoIntrospectionToken.getErrorMessage();
									wwwAuthenticateErrorHeader = esitoIntrospectionToken.getWwwAuthenticateErrorHeader();
									integrationFunctionError = esitoIntrospectionToken.getIntegrationFunctionError();
									
								}
							}
							else {
								this.msgDiag.logPersonalizzato("gestioneTokenInCorso.introspectionToken.disabilitata");
							}
							
						}
						
						// userInfo
						if(!fineGestione) {
							
							if(policyGestioneToken.isUserInfo()) {
							
								this.msgDiag.addKeyword(CostantiPdD.KEY_TOKEN_ENDPOINT_SERVIZIO_USER_INFO, policyGestioneToken.getUserInfoEndpoint());
								
								this.msgDiag.logPersonalizzato("gestioneTokenInCorso.userInfoToken");
								
								esitoUserInfoToken = gestioneTokenEngine.userInfoToken(datiInvocazione, esitoPresenzaToken);
								if(esitoUserInfoToken.isValido()) {
									
									this.msgDiag.logPersonalizzato("gestioneTokenInCorso.userInfoToken.completataSuccesso");
									
									this.msgDiag.addKeyword(CostantiPdD.KEY_TOKEN_INFO, esitoUserInfoToken.getInformazioniToken().getRawResponse());
									
									this.pddContext.addObject(org.openspcoop2.pdd.core.token.Costanti.PDD_CONTEXT_TOKEN_ESITO_USER_INFO, esitoUserInfoToken);
									
									if(esitoUserInfoToken.isInCache()) {
										this.msgDiag.logPersonalizzato("gestioneTokenInCorso.userInfoToken.inCache");
									}
									else {
										this.msgDiag.logPersonalizzato("gestioneTokenInCorso.userInfoToken.validato");
									}
								}
								else {
									
									this.msgDiag.addKeyword(CostantiPdD.KEY_ERRORE_PROCESSAMENTO, esitoUserInfoToken.getDetails());
									if(policyGestioneToken.isIntrospectionWarningOnly()) {
										this.msgDiag.logPersonalizzato("gestioneTokenInCorso.userInfoToken.warningOnly.fallita");
									}
									else {
										this.msgDiag.logPersonalizzato("gestioneTokenInCorso.userInfoToken.fallita");
										fineGestione = true;
									}
									
									String msgErrore = gestioneTokenPrefix+"(UserInfo) fallito: " + esitoUserInfoToken.getDetails();
									if(esitoUserInfoToken.getEccezioneProcessamento()!=null) {
										this.logCore.error(msgErrore,esitoUserInfoToken.getEccezioneProcessamento());
									}
									else {
										this.logCore.error(msgErrore);
									}
								
									errore = esitoUserInfoToken.getErroreIntegrazione();
									eGestioneToken = esitoUserInfoToken.getEccezioneProcessamento();
									errorMessageGestioneToken = esitoUserInfoToken.getErrorMessage();
									wwwAuthenticateErrorHeader = esitoUserInfoToken.getWwwAuthenticateErrorHeader();
									integrationFunctionError = esitoUserInfoToken.getIntegrationFunctionError();
									
								}
							}
							else {
								this.msgDiag.logPersonalizzato("gestioneTokenInCorso.userInfoToken.disabilitata");
							}
							
						}
						
						
					}
					else {
						
						if(!policyGestioneToken.isTokenOpzionale()) {
						
							this.msgDiag.addKeyword(CostantiPdD.KEY_ERRORE_PROCESSAMENTO, esitoPresenzaToken.getDetails());
							this.msgDiag.logPersonalizzato("gestioneTokenInCorso.verificaPresenzaToken.fallita");
							
							fineGestione = true;
							
							String msgErrore = gestioneTokenPrefix+"fallito: " + esitoPresenzaToken.getDetails();
							if(esitoPresenzaToken.getEccezioneProcessamento()!=null) {
								this.logCore.error(msgErrore,esitoPresenzaToken.getEccezioneProcessamento());
							}
							else {
								this.logCore.error(msgErrore);
							}
					
							errore = esitoPresenzaToken.getErroreIntegrazione();
							eGestioneToken = esitoPresenzaToken.getEccezioneProcessamento();
							errorMessageGestioneToken = esitoPresenzaToken.getErrorMessage();
							wwwAuthenticateErrorHeader = esitoPresenzaToken.getWwwAuthenticateErrorHeader();
							integrationFunctionError = IntegrationFunctionError.TOKEN_NOT_FOUND;
						
						}
						
					}
			
					if(fineGestione) {
						if(esitoPresenzaToken.isPresente()) {
							this.pddContext.addObject(org.openspcoop2.core.constants.Costanti.ERRORE_TOKEN, "true");
						}
						else {
							this.pddContext.addObject(org.openspcoop2.core.constants.Costanti.TOKEN_NON_PRESENTE, "true");
						}
						this.msgDiag.logPersonalizzato("gestioneTokenFallita");
						
						List<InformazioniToken> listaEsiti = GestoreToken.getInformazioniTokenNonValide(esitoValidazioneToken, esitoIntrospectionToken, esitoUserInfoToken);
						InformazioniToken informazioniTokenNormalizzate = null;
						if(listaEsiti!=null && !listaEsiti.isEmpty()) {
							informazioniTokenNormalizzate = GestoreToken.normalizeInformazioniToken(listaEsiti);
							informazioniTokenNormalizzate.setValid(false);
						}
						if(informazioniTokenNormalizzate!=null) {
							this.pddContext.addObject(org.openspcoop2.pdd.core.token.Costanti.PDD_CONTEXT_TOKEN_INFORMAZIONI_NORMALIZZATE, informazioniTokenNormalizzate);
							
							this.transaction.setInformazioniToken(informazioniTokenNormalizzate);
						}
					}
					else {
						if(esitoPresenzaToken.isPresente()) {
							List<InformazioniToken> listaEsiti = GestoreToken.getInformazioniTokenValide(esitoValidazioneToken, esitoIntrospectionToken, esitoUserInfoToken);
							InformazioniToken informazioniTokenNormalizzate = null;
							if(listaEsiti!=null && !listaEsiti.isEmpty()) {
								informazioniTokenNormalizzate = GestoreToken.normalizeInformazioniToken(listaEsiti);
								informazioniTokenNormalizzate.setValid(true);
							}
							if(informazioniTokenNormalizzate!=null) {
								this.pddContext.addObject(org.openspcoop2.pdd.core.token.Costanti.PDD_CONTEXT_TOKEN_INFORMAZIONI_NORMALIZZATE, informazioniTokenNormalizzate);
								
								this.transaction.setInformazioniToken(informazioniTokenNormalizzate);
							}
												
							this.msgDiag.mediumDebug("Gestione forward token ...");
							gestioneTokenEngine.forwardToken(datiInvocazione,esitoPresenzaToken,
									esitoValidazioneToken, esitoIntrospectionToken, esitoUserInfoToken,
									informazioniTokenNormalizzate);
							this.msgDiag.mediumDebug("Gestione forward token completata");
							
							this.msgDiag.logPersonalizzato("gestioneTokenCompletataConSuccesso");
						}
						else {
							this.msgDiag.logPersonalizzato("gestioneTokenCompletataSenzaRilevazioneToken");
						}				
					}
					
				} catch (Exception e) {
					
					this.msgDiag.addKeyword(CostantiPdD.KEY_ERRORE_PROCESSAMENTO, e.getMessage());
					this.msgDiag.logPersonalizzato("gestioneTokenFallita.erroreGenerico");
					this.logCore.error(gestioneTokenPrefix+"fallito, " + e.getMessage(),e);
					
					errore = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
							get5XX_ErroreProcessamento(gestioneTokenPrefix+"fallito, " + e.getMessage(),
									CodiceErroreIntegrazione.CODICE_560_GESTIONE_TOKEN);
					eGestioneToken = e;
					
					integrationFunctionError = IntegrationFunctionError.INTERNAL_REQUEST_ERROR;
					
					fineGestione = true;
					
				}
				if (fineGestione) {
					
					this.openspcoopstate.releaseResource();
		
					if (this.msgContext.isGestioneRisposta()) {
						if(errorMessageGestioneToken!=null) {
							this.msgContext.setMessageResponse(errorMessageGestioneToken);
						}
						else {
							if(CodiceErroreIntegrazione.CODICE_443_TOKEN_NON_PRESENTE.equals(errore.getCodiceErrore())){
								if(integrationFunctionError==null) {
									integrationFunctionError = IntegrationFunctionError.TOKEN_NOT_FOUND;
								}
							}
							else if(CodiceErroreIntegrazione.CODICE_444_TOKEN_NON_VALIDO.equals(errore.getCodiceErrore())){
								if(integrationFunctionError==null) {
									integrationFunctionError = IntegrationFunctionError.TOKEN_INVALID;
								}
							}
							else{
								if(integrationFunctionError==null) {
									integrationFunctionError = IntegrationFunctionError.INTERNAL_REQUEST_ERROR;
								}
							}
							
							OpenSPCoop2Message errorOpenSPCoopMsg = (this.generatoreErrore.build(this.pddContext,integrationFunctionError,errore,
									eGestioneToken,null));
							
							if(wwwAuthenticateErrorHeader!=null) {
								errorOpenSPCoopMsg.forceTransportHeader(HttpConstants.AUTHORIZATION_RESPONSE_WWW_AUTHENTICATE, wwwAuthenticateErrorHeader);
							}
							
							this.msgContext.setMessageResponse(errorOpenSPCoopMsg);
						}
					}
					
					updateCredenzialiToken();
					
					return false;
					
				}
			}finally {
				this.transaction.getTempiElaborazione().endToken();
			}
		}
		
		return true;
		
	}
	
	private void updateCredenzialiToken() {
		
		// Viene chiamato se la validazione fallisce
		
		if(OpenSPCoop2Properties.getInstance().isGestioneTokenSaveTokenAuthenticationInfoValidationFailed()) {
			CredenzialiMittente credenzialiMittente = this.transaction.getCredenzialiMittente();
			if(credenzialiMittente==null) {
				credenzialiMittente = new CredenzialiMittente();
				try {
					this.transaction.setCredenzialiMittente(credenzialiMittente);
				}catch(Exception e) {
					this.logCore.error("SetCredenzialiMittente error: "+e.getMessage(),e);
				}
			}
			InformazioniToken informazioniTokenNormalizzate = null;
			if(this.pddContext.containsKey(org.openspcoop2.pdd.core.token.Costanti.PDD_CONTEXT_TOKEN_INFORMAZIONI_NORMALIZZATE)) {
				informazioniTokenNormalizzate = (InformazioniToken) this.pddContext.getObject(org.openspcoop2.pdd.core.token.Costanti.PDD_CONTEXT_TOKEN_INFORMAZIONI_NORMALIZZATE);
			}
			if(informazioniTokenNormalizzate!=null) {
				try {
					GestoreAutenticazione.updateCredenzialiToken(
							this.identitaPdD,
							RicezioneContenutiApplicativi.ID_MODULO, this.idTransazione, informazioniTokenNormalizzate, null, credenzialiMittente, 
							this.openspcoopstate, "RicezioneContenutiApplicativi.credenzialiToken", this.requestInfo,
							this.pddContext);
				}catch(Exception e) {
					this.logCore.error("updateCredenzialiToken error: "+e.getMessage(),e);
				}
			}
		}
	}
}
