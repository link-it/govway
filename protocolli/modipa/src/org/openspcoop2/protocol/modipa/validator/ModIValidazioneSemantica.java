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



package org.openspcoop2.protocol.modipa.validator;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaApplicativaAutorizzazioneServizioApplicativo;
import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.config.constants.RuoloTipologia;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import org.openspcoop2.core.constants.Costanti;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.constants.CostantiLabel;
import org.openspcoop2.core.constants.TipoPdD;
import org.openspcoop2.core.id.IDPortaApplicativa;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.pdd.config.ConfigurazionePdDReader;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.core.CostantiPdD;
import org.openspcoop2.pdd.core.keystore.KeystoreException;
import org.openspcoop2.pdd.core.keystore.RemoteStoreProvider;
import org.openspcoop2.pdd.core.token.InformazioniToken;
import org.openspcoop2.pdd.core.token.parser.Claims;
import org.openspcoop2.pdd.logger.MsgDiagnosticiProperties;
import org.openspcoop2.pdd.logger.MsgDiagnostico;
import org.openspcoop2.protocol.basic.validator.ValidazioneSemantica;
import org.openspcoop2.protocol.engine.SecurityTokenUtilities;
import org.openspcoop2.protocol.modipa.config.ModIProperties;
import org.openspcoop2.protocol.modipa.constants.ModICostanti;
import org.openspcoop2.protocol.modipa.utils.ModIPropertiesUtils;
import org.openspcoop2.protocol.modipa.utils.ModISecurityConfig;
import org.openspcoop2.protocol.modipa.utils.ModIUtilities;
import org.openspcoop2.protocol.registry.RegistroServiziManager;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.Eccezione;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.PDNDTokenInfo;
import org.openspcoop2.protocol.sdk.PDNDTokenInfoDetails;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.RestMessageSecurityToken;
import org.openspcoop2.protocol.sdk.SecurityToken;
import org.openspcoop2.protocol.sdk.constants.CodiceErroreCooperazione;
import org.openspcoop2.protocol.sdk.constants.RuoloBusta;
import org.openspcoop2.protocol.sdk.registry.IRegistryReader;
import org.openspcoop2.protocol.sdk.state.IState;
import org.openspcoop2.protocol.sdk.state.RequestInfo;
import org.openspcoop2.protocol.sdk.validator.ProprietaValidazione;
import org.openspcoop2.protocol.sdk.validator.ValidazioneSemanticaResult;
import org.openspcoop2.protocol.sdk.validator.ValidazioneUtils;
import org.openspcoop2.security.SecurityException;
import org.openspcoop2.security.keystore.cache.GestoreKeystoreCache;
import org.openspcoop2.utils.SortedMap;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.certificate.remote.RemoteKeyType;
import org.openspcoop2.utils.certificate.remote.RemoteStoreClientInfo;
import org.openspcoop2.utils.certificate.remote.RemoteStoreConfig;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.date.DateUtils;
import org.openspcoop2.utils.digest.DigestEncoding;
import org.openspcoop2.utils.json.JSONUtils;
import org.openspcoop2.utils.properties.PropertiesUtilities;
import org.openspcoop2.utils.transport.http.HttpConstants;

import com.fasterxml.jackson.databind.JsonNode;



/**
 * Classe che implementa, in base al protocollo SdI, l'interfaccia {@link org.openspcoop2.protocol.sdk.validator.IValidazioneSemantica}
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class ModIValidazioneSemantica extends ValidazioneSemantica {

	/** ValidazioneUtils */
	protected ValidazioneUtils validazioneUtils;
	
	/** Properties */
	protected ModIProperties modiProperties;
	
	/** Errori di validazione riscontrati sulla busta */
	protected java.util.List<Eccezione> erroriValidazione = new ArrayList<>();
	/** Errori di processamento riscontrati sulla busta */
	protected java.util.List<Eccezione> erroriProcessamento = new ArrayList<>();
	
	public ModIValidazioneSemantica(IProtocolFactory<?> factory, IState state) throws ProtocolException {
		super(factory, state);
		this.modiProperties = ModIProperties.getInstance();
		this.validazioneUtils = new ValidazioneUtils(factory);
	}

	private static final String DIAGNOSTIC_VALIDATE = "validazioneSemantica";
	private static final String DIAGNOSTIC_IN_CORSO = "inCorso";
	private static final String DIAGNOSTIC_COMPLETATA = "completata";
	private static final String DIAGNOSTIC_FALLITA = "fallita";
	
	private String getErroreClaimNonValido(String claim) {
		return "Token contenente un claim '"+claim+"' non valido";
	}
	private String getErroreClaimNonPresente(String claim) {
		return "Token non contiene il claim '"+claim+"'";
	}
	private Eccezione getErroreMittenteNonAutorizzato(Busta busta, String msgErrore) throws ProtocolException {
		String idApp = busta.getServizioApplicativoFruitore() + " (Soggetto: "+busta.getMittente()+")";
		return this.validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.SICUREZZA_AUTORIZZAZIONE_FALLITA, 
				"Applicativo Mittente "+idApp+" non autorizzato"+(msgErrore!=null ? "; "+msgErrore: ""));
	}
	private void addErroreMittenteNonAutorizzato(Busta busta, String msgErrore) throws ProtocolException {
		this.erroriValidazione.add(getErroreMittenteNonAutorizzato(busta, msgErrore));
	}
	
	private static final String MSG_ERRORE_NON_PRESENTE = "non presente";
	
	private String getPrefixHeader(String hdr) {
		return "[Header '"+hdr+"'] ";
	}
	
	@Override
	public ValidazioneSemanticaResult valida(OpenSPCoop2Message msg, Busta busta, 
			ProprietaValidazione proprietaValidazione, 
			RuoloBusta tipoBusta) throws ProtocolException{
		
		this.valida(msg,busta,tipoBusta, this.protocolFactory, this.state);
		
		java.util.List<Eccezione> erroriValidazioneList = null;
		if(!this.erroriValidazione.isEmpty()){
			erroriValidazioneList = this.erroriValidazione;
			if(this.context!=null) {
				this.context.addObject(Costanti.ERRORE_VALIDAZIONE_PROTOCOLLO, Costanti.ERRORE_TRUE);
			}
		}
		java.util.List<Eccezione> erroriProcessamentoList = null;
		if(!this.erroriProcessamento.isEmpty()){
			erroriValidazioneList = this.erroriProcessamento;
		}
		return new ValidazioneSemanticaResult(erroriValidazioneList, erroriProcessamentoList, null, null, null, null);
		
	}

	private void valida(OpenSPCoop2Message msg,Busta busta, RuoloBusta tipoBusta, IProtocolFactory<?> factory, IState state) throws ProtocolException{
		
		MsgDiagnostico msgDiag = null;
		String tipoDiagnostico = null;
		boolean verifica = false;
		boolean autorizzazione = false;
		int sizeListaErroriValidazionePrimaAutorizzazione = -1;
		int sizeListaProcessamentoValidazionePrimaAutorizzazione = -1;
		try{
		
			String prefixIntegrity = getPrefixHeader(this.modiProperties.getRestSecurityTokenHeaderModI());
			
			RequestInfo requestInfo = null;
			if(this.context!=null && this.context.containsKey(org.openspcoop2.core.constants.Costanti.REQUEST_INFO)) {
				requestInfo = (RequestInfo) this.context.getObject(org.openspcoop2.core.constants.Costanti.REQUEST_INFO);
			}
			
			boolean isRichiesta = RuoloBusta.RICHIESTA.equals(tipoBusta);
			
			boolean rest = msg!=null && ServiceBinding.REST.equals(msg.getServiceBinding());
			
			if(busta==null) {
				throw new ProtocolException("Busta undefined");
			}
			
			TipoPdD tipoPdD = isRichiesta ? TipoPdD.APPLICATIVA : TipoPdD.DELEGATA;
			IDSoggetto idSoggetto = TipoPdD.DELEGATA.equals(tipoPdD) ? 
						new IDSoggetto(busta.getTipoMittente(),busta.getMittente()) : 
						new IDSoggetto(busta.getTipoDestinatario(),busta.getDestinatario());
			if(idSoggetto==null || idSoggetto.getTipo()==null || idSoggetto.getNome()==null) {
				idSoggetto = OpenSPCoop2Properties.getInstance().getIdentitaPortaDefault(this.protocolFactory.getProtocol(), requestInfo);
			}
			else {
				IRegistryReader registryReader = this.getProtocolFactory().getCachedRegistryReader(this.state, requestInfo);
				idSoggetto.setCodicePorta(registryReader.getDominio(idSoggetto));
			}
			msgDiag = MsgDiagnostico.newInstance(TipoPdD.DELEGATA, 
					idSoggetto,
					"ModI", requestInfo!=null && requestInfo.getProtocolContext()!=null ? requestInfo.getProtocolContext().getInterfaceName() : null,
					requestInfo,
					this.state);
			if(TipoPdD.DELEGATA.equals(tipoPdD)){
				msgDiag.setPrefixMsgPersonalizzati(MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_CONTENUTI_APPLICATIVI);
			}
			else {
				msgDiag.setPrefixMsgPersonalizzati(MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_BUSTE);
			}
			msgDiag.setPddContext(this.context, this.protocolFactory);
			tipoDiagnostico = isRichiesta ? ".richiesta." : ".risposta.";
			
			
			Date now = DateManager.getDate();
			
			
			
			String securityMessageProfileNonFiltratoPDND = busta.getProperty(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO);
			if(securityMessageProfileNonFiltratoPDND!=null) {
				securityMessageProfileNonFiltratoPDND = ModIPropertiesUtils.convertProfiloSicurezzaToConfigurationValue(securityMessageProfileNonFiltratoPDND);
			}
			
			String securityMessageProfileSorgenteTokenIdAuth = busta.getProperty(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO_SORGENTE_TOKEN);
			if(securityMessageProfileSorgenteTokenIdAuth!=null) {
				securityMessageProfileSorgenteTokenIdAuth = ModIPropertiesUtils.convertProfiloSicurezzaSorgenteTokenToConfigurationValue(securityMessageProfileSorgenteTokenIdAuth);
			}
			
			String securityMessageProfile = securityMessageProfileNonFiltratoPDND;
			if(securityMessageProfileSorgenteTokenIdAuth!=null &&
				(	
					ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM01.equals(securityMessageProfileNonFiltratoPDND) 
						|| 
					ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM02.equals(securityMessageProfileNonFiltratoPDND)
				)
				&&
				(	
					ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SORGENTE_TOKEN_IDAUTH_VALUE_PDND.equals(securityMessageProfileSorgenteTokenIdAuth) 
						||
					ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SORGENTE_TOKEN_IDAUTH_VALUE_OAUTH.equals(securityMessageProfileSorgenteTokenIdAuth)
				)
			){
				securityMessageProfile = null;
			}
						
			boolean sicurezzaTokenOauth = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SORGENTE_TOKEN_IDAUTH_VALUE_PDND.equals(securityMessageProfileSorgenteTokenIdAuth) 
					||
				ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SORGENTE_TOKEN_IDAUTH_VALUE_OAUTH.equals(securityMessageProfileSorgenteTokenIdAuth);
			
			boolean sicurezzaMessaggio = securityMessageProfile!=null && !ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_UNDEFINED.equals(securityMessageProfile);
			boolean sicurezzaMessaggioIDAR04 = false;
			if(sicurezzaMessaggio) {
				sicurezzaMessaggioIDAR04 = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM0401.equals(securityMessageProfile) 
												|| 
											ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM0402.equals(securityMessageProfile);
			}
			
			String securityAuditPattern = busta.getProperty(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_AUDIT_PATTERN);
			
			if(securityAuditPattern!=null) {
				securityAuditPattern = ModIPropertiesUtils.convertProfiloAuditToConfigurationValue(securityAuditPattern);
			}
			
			boolean sicurezzaAudit = isRichiesta && 
					securityAuditPattern!=null && !ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_PATTERN_VALUE_OLD.equals(securityAuditPattern);
			
			
			if(sicurezzaMessaggio || sicurezzaAudit) {
				msgDiag.logPersonalizzato(DIAGNOSTIC_VALIDATE+tipoDiagnostico+DIAGNOSTIC_IN_CORSO);
				verifica=true;
			}
			
			PortaApplicativa pa = null;
			
			RemoteStoreConfig rsc = null;
			
			if(isRichiesta && sicurezzaTokenOauth) {
				
				String prefixAuthorization = getPrefixHeader(HttpConstants.AUTHORIZATION);
				
				boolean sicurezzaToken = this.context.containsKey(org.openspcoop2.pdd.core.token.Costanti.PDD_CONTEXT_TOKEN_INFORMAZIONI_NORMALIZZATE);
				if(!sicurezzaToken) {
				
					this.erroriValidazione.add(this.validazioneUtils.newEccezioneValidazione(
							CodiceErroreCooperazione.SICUREZZA_TOKEN_NON_PRESENTE, 
								prefixAuthorization+MSG_ERRORE_NON_PRESENTE));
				}
				else {
				
					validateTokenAuthorizationId(msg,
							prefixAuthorization,
							busta, 
							sicurezzaMessaggio);
					
					boolean checkAudienceByModIConfig = sicurezzaMessaggio || sicurezzaAudit;
					pa = validateTokenAuthorizationAudience(msg, factory, state, requestInfo,
							isRichiesta, prefixAuthorization,
							checkAudienceByModIConfig);
					
					rsc = enrichTokenInfo(requestInfo);
				
				}
				
			}
			
			
			if(sicurezzaMessaggio) {
								
				String exp = busta.getProperty(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO_EXP);
				if(exp!=null) {
					checkExp(exp, now, rest, "");
				}
				
				String expIntegrity = busta.getProperty(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO_REST_INTEGRITY_EXP);
				if(expIntegrity!=null) {
					checkExp(expIntegrity, now, rest, prefixIntegrity);
				}
				
				String nbf = busta.getProperty(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO_NBF);
				if(nbf!=null) {
					checkNbf(nbf, now, "");
				}
				
				String nbfIntegrity = busta.getProperty(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO_REST_INTEGRITY_NBF);
				if(nbfIntegrity!=null) {
					checkNbf(nbfIntegrity, now, prefixIntegrity);
				}
				
				String iat = busta.getProperty(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO_IAT);
				if(iat!=null) {
					checkIat(iat, msg, rest, "");
				}
				
				String iatIntegrity = busta.getProperty(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO_REST_INTEGRITY_IAT);
				if(iatIntegrity!=null) {
					checkIat(iatIntegrity, msg, rest, prefixIntegrity);
				}
				
				String audience = busta.getProperty(rest ? 
						ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO_REST_AUDIENCE :
						ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO_SOAP_WSA_TO	);
				
				Object audienceAttesoObject = null;
				if(msg!=null) {
					audienceAttesoObject = msg.getContextProperty(ModICostanti.MODIPA_OPENSPCOOP2_MSG_CONTEXT_AUDIENCE_CHECK);
				}
				String audienceAtteso = null;
				if(audienceAttesoObject!=null) {
					audienceAtteso = (String) audienceAttesoObject;
				}
				
				Object audienceAttesoOAuthObject = null;
				if(msg!=null) {
					audienceAttesoOAuthObject = msg.getContextProperty(ModICostanti.MODIPA_OPENSPCOOP2_MSG_CONTEXT_AUDIENCE_CHECK_OAUTH);
				}
				String audienceOAuthAtteso = null;
				if(audienceAttesoOAuthObject!=null) {
					audienceOAuthAtteso = (String) audienceAttesoOAuthObject;
				}
				
				if(audienceAtteso!=null || audienceOAuthAtteso!=null) {
					
					boolean checkAudience = false;
					if(audienceAtteso!=null) {
						checkAudience = audienceAtteso.equals(audience);
					}
					
					boolean checkAudienceOAuth = false;
					if(audienceOAuthAtteso!=null) {
						checkAudienceOAuth = audienceOAuthAtteso.equals(audience);
					}
					
					if(!checkAudience && !checkAudienceOAuth) {
						
						boolean buildSecurityTokenInRequest = true;
						Object buildSecurityTokenInRequestObject = msg.getContextProperty(ModICostanti.MODIPA_OPENSPCOOP2_MSG_CONTEXT_BUILD_SECURITY_REQUEST_TOKEN);
						if(buildSecurityTokenInRequestObject instanceof Boolean) {
							buildSecurityTokenInRequest = (Boolean) buildSecurityTokenInRequestObject;
						}
						
						if(isRichiesta || buildSecurityTokenInRequest) {
						
							this.erroriValidazione.add(this.validazioneUtils.newEccezioneValidazione(
									isRichiesta ? CodiceErroreCooperazione.SERVIZIO_APPLICATIVO_EROGATORE_NON_VALIDO :
										CodiceErroreCooperazione.SERVIZIO_APPLICATIVO_FRUITORE_NON_VALIDO, 
										getErroreClaimNonValido(Claims.JSON_WEB_TOKEN_RFC_7519_AUDIENCE)));
							
						}
					}
				}
				if(rest) { 
					Object audienceIntegrityAttesoObject = null;
					if(msg!=null) {
						audienceIntegrityAttesoObject = msg.getContextProperty(ModICostanti.MODIPA_OPENSPCOOP2_MSG_CONTEXT_AUDIENCE_INTEGRITY_CHECK);
					}
					String audienceIntegrityAtteso = null;
					if(audienceIntegrityAttesoObject!=null) {
						audienceIntegrityAtteso = (String) audienceIntegrityAttesoObject;
					}
					
					Object audienceIntegrityAttesoOAuthObject = null;
					if(msg!=null) {
						audienceIntegrityAttesoOAuthObject = msg.getContextProperty(ModICostanti.MODIPA_OPENSPCOOP2_MSG_CONTEXT_AUDIENCE_INTEGRITY_CHECK_OAUTH);
					}
					String audienceIntegrityOAuthAtteso = null;
					if(audienceIntegrityAttesoOAuthObject!=null) {
						audienceIntegrityOAuthAtteso = (String) audienceIntegrityAttesoOAuthObject;
					}
					
					if(audienceIntegrityAtteso!=null || audienceIntegrityOAuthAtteso!=null) {
						String audienceIntegrity = busta.getProperty(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO_REST_INTEGRITY_AUDIENCE);
						if(audienceIntegrity==null) {
							// significa che l'audience tra i due token ricevuto e' identico
							audienceIntegrity = busta.getProperty(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO_REST_AUDIENCE);
						}
						
						boolean checkAudience = false;
						if(audienceIntegrityAtteso!=null) {
							checkAudience = audienceIntegrity.equals(audienceIntegrityAtteso);
						}
						
						boolean checkAudienceOAuth = false;
						if(audienceIntegrityAttesoOAuthObject!=null) {
							checkAudienceOAuth = audienceIntegrity.equals(audienceIntegrityAttesoOAuthObject);
						}
						
						if(!checkAudience && !checkAudienceOAuth) {
							this.erroriValidazione.add(this.validazioneUtils.newEccezioneValidazione(
									isRichiesta ? CodiceErroreCooperazione.SERVIZIO_APPLICATIVO_EROGATORE_NON_VALIDO :
										CodiceErroreCooperazione.SERVIZIO_APPLICATIVO_FRUITORE_NON_VALIDO, 
									prefixIntegrity+getErroreClaimNonValido(Claims.JSON_WEB_TOKEN_RFC_7519_AUDIENCE)));
						}
					}
				}
				
			}
			
			
			
			if(sicurezzaAudit) {
			
				String prefixAudit = getPrefixHeader(this.modiProperties.getSecurityTokenHeaderModIAudit());
				
				boolean audit02 = CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_PATTERN_AUDIT_REST_02.equals(securityAuditPattern);
			
				String exp = busta.getProperty(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_AUDIT_EXP);
				if(exp!=null) {
					checkExp(exp, now, rest, prefixAudit);
				}
				
				String nbf = busta.getProperty(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_AUDIT_NBF);
				if(nbf!=null) {
					checkNbf(nbf, now, prefixAudit);
				}
				
				String iat = busta.getProperty(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_AUDIT_IAT);
				if(iat!=null) {
					checkIat(iat, msg, rest, prefixAudit);
				}
				
				Object audienceAuditAttesoObject = null;
				if(msg!=null) {
					audienceAuditAttesoObject = msg.getContextProperty(ModICostanti.MODIPA_OPENSPCOOP2_MSG_CONTEXT_AUDIENCE_AUDIT_CHECK);
				}
				if(audienceAuditAttesoObject!=null) {
					String audienceAuditAtteso = (String) audienceAuditAttesoObject;
					String audienceAudit = busta.getProperty(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_AUDIT_AUDIENCE);
					if(audienceAudit==null) {
						// significa che l'audience tra i due token ricevuto e' identico
						audienceAudit = busta.getProperty(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO_REST_AUDIENCE);
					}
					if(!audienceAuditAtteso.equals(audienceAudit)) {
						this.erroriValidazione.add(this.validazioneUtils.newEccezioneValidazione(
								isRichiesta ? CodiceErroreCooperazione.SERVIZIO_APPLICATIVO_EROGATORE_NON_VALIDO :
									CodiceErroreCooperazione.SERVIZIO_APPLICATIVO_FRUITORE_NON_VALIDO, 
									prefixAudit+getErroreClaimNonValido(Claims.JSON_WEB_TOKEN_RFC_7519_AUDIENCE)));
					}
				}
				
				
				if(rsc!=null) {
					
					validatePurposeId(rsc, prefixAudit, securityAuditPattern);
					
				}
				
				if(audit02) {
					
					validateAudit02(securityMessageProfileSorgenteTokenIdAuth, rest, prefixAudit);
					
				}
				
			}
			
			
			if(verifica) {
				if(this.erroriValidazione.isEmpty() && this.erroriProcessamento.isEmpty()) {
					msgDiag.logPersonalizzato(DIAGNOSTIC_VALIDATE+tipoDiagnostico+DIAGNOSTIC_COMPLETATA);
				}
				else {
					String errore = null;
					if(!this.erroriValidazione.isEmpty()) {
						errore = ModIValidazioneSintattica.buildErrore(this.erroriValidazione, factory);
					}
					else {
						errore = ModIValidazioneSintattica.buildErrore(this.erroriProcessamento, factory);
					}
					msgDiag.addKeyword(CostantiPdD.KEY_ERRORE_PROCESSAMENTO, errore);
					msgDiag.logPersonalizzato(DIAGNOSTIC_VALIDATE+tipoDiagnostico+DIAGNOSTIC_FALLITA);
							
				}
				verifica = false;
			}
			
			
			
			if(isRichiesta) {
				tipoDiagnostico = ".autorizzazione.";
				msgDiag.logPersonalizzato(DIAGNOSTIC_VALIDATE+tipoDiagnostico+DIAGNOSTIC_IN_CORSO);
				autorizzazione = true;
				sizeListaErroriValidazionePrimaAutorizzazione = this.erroriValidazione.size();
				sizeListaProcessamentoValidazionePrimaAutorizzazione = this.erroriProcessamento.size(); 
				
				
				// vale sia per sicurezza messaggio che per token
				// durante l'identificazione viene identificato 1 solo applicativo (non possono essere differenti tra token e trasporto)
				// viene quindi inserito dentro busta e usato per i controlli sottostanti
				
				if(pa==null) {
					String interfaceName = null;
					if(msg!=null && msg.getTransportRequestContext()!=null && msg.getTransportRequestContext().getInterfaceName()!=null) {
						interfaceName = msg.getTransportRequestContext().getInterfaceName();
					}
					else if(requestInfo!=null && requestInfo.getProtocolContext()!=null && requestInfo.getProtocolContext().getInterfaceName()!=null) {
						interfaceName = requestInfo.getProtocolContext().getInterfaceName();
					}
					if(interfaceName==null) {
						throw new ProtocolException("ID Porta non presente");
					}
					IDPortaApplicativa idPA = new IDPortaApplicativa();
					idPA.setNome(interfaceName);
					pa = factory.getCachedConfigIntegrationReader(state, requestInfo).getPortaApplicativa(idPA); // pa invocata
				}
				
				
				/** Identificazione Mittente by LineeGuida e Token */
				boolean saIdentificatoBySecurity = busta.getServizioApplicativoFruitore()!=null && !CostantiPdD.SERVIZIO_APPLICATIVO_ANONIMO.equals(busta.getServizioApplicativoFruitore());
				
				boolean sicurezzaToken = this.context.containsKey(org.openspcoop2.pdd.core.token.Costanti.PDD_CONTEXT_TOKEN_INFORMAZIONI_NORMALIZZATE);
				boolean saIdentificatoByToken = false;
				boolean saVerificatoBySecurity = false;
				if(sicurezzaToken) {
					IDServizioApplicativo idSAbyToken = null;
					StringBuilder sbError = new StringBuilder();
					try {
						idSAbyToken = IdentificazioneApplicativoMittenteUtils.identificazioneApplicativoMittenteByToken(this.log, state, busta, this.context, requestInfo, sbError);
					}catch(Exception e) {
						if(sbError!=null && sbError.length()>0) {
							this.context.addObject(Costanti.ERRORE_AUTORIZZAZIONE, Costanti.ERRORE_TRUE);
							this.erroriValidazione.add(this.validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.SICUREZZA_AUTORIZZAZIONE_FALLITA, 
									sbError.toString()));
							return;
						}
						else {
							throw e;
						}
					}
					saIdentificatoByToken = idSAbyToken!=null;
					
					if(saIdentificatoByToken) {
						/**	&& !saIdentificatoBySecurity) { 
						L'identificazione per sicurezza, se c'è abilitata l'autenticazione per token, non dovrebbe esserci mai poichè la funzionalità è stata disabilitata.
						Con autenticazione token attiva, gli applicativi anche se presente un x509 non vengono identificati dall'autenticazione https effettuata in AbstractModIValidazioneSintatticaCommons e di conseguenza in ModIValidazioneSintatticaRest e ModIValidazioneSintatticaSoap durante il trattamento del token di sicurezza
						bisogna quindi verificare, se è presente un certificato x509 di firma, che corrisponda a quello registrato nell'applicativo di tipo token identificato*/
						
						SecurityToken securityTokenForContext = SecurityTokenUtilities.readSecurityToken(this.context);
						if(securityTokenForContext!=null) {
							try {
								if(securityTokenForContext.getAuthorization()!=null && securityTokenForContext.getAuthorization().getCertificate()!=null) {
									sbError = new StringBuilder();
									String tipoToken = //"Http Header "+
											(securityTokenForContext.getAuthorization().getHttpHeaderName()!=null ? securityTokenForContext.getAuthorization().getHttpHeaderName() : HttpConstants.AUTHORIZATION);
									IdentificazioneApplicativoMittenteUtils.checkApplicativoTokenByX509(this.log, idSAbyToken, 
											state, requestInfo, tipoToken, securityTokenForContext.getAuthorization().getCertificate(), sbError);
									saVerificatoBySecurity = true;
								}
								if(securityTokenForContext.getIntegrity()!=null && securityTokenForContext.getIntegrity().getCertificate()!=null) {
									sbError = new StringBuilder();
									String tipoToken = //"Http Header "+
											(securityTokenForContext.getIntegrity().getHttpHeaderName()!=null ? securityTokenForContext.getIntegrity().getHttpHeaderName() : this.modiProperties.getRestSecurityTokenHeaderModI());
									IdentificazioneApplicativoMittenteUtils.checkApplicativoTokenByX509(this.log, idSAbyToken, 
											state, requestInfo, tipoToken, securityTokenForContext.getIntegrity().getCertificate(), sbError);
									saVerificatoBySecurity = true;
								}
								if(securityTokenForContext.getEnvelope()!=null && securityTokenForContext.getEnvelope().getCertificate()!=null) {
									sbError = new StringBuilder();
									String tipoToken = "WSSecurity";
									IdentificazioneApplicativoMittenteUtils.checkApplicativoTokenByX509(this.log, idSAbyToken, 
											state, requestInfo, tipoToken, securityTokenForContext.getEnvelope().getCertificate(), sbError);
									saVerificatoBySecurity = true;
								}
							}catch(Exception e) {
								if(sbError!=null && sbError.length()>0) {
									this.context.addObject(Costanti.ERRORE_AUTORIZZAZIONE, Costanti.ERRORE_TRUE);
									this.erroriValidazione.add(this.validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.SICUREZZA_AUTORIZZAZIONE_FALLITA, 
											sbError.toString()));
									return;
								}
								else {
									throw e;
								}
							}
						}
						
					}
				}
				
				boolean saFruitoreAnonimo = busta.getServizioApplicativoFruitore()==null || CostantiPdD.SERVIZIO_APPLICATIVO_ANONIMO.equals(busta.getServizioApplicativoFruitore());
								
				/** Tipi di Autorizzazione definiti */
				boolean autorizzazionePerRichiedente = false;
				if(pa.getAutorizzazioneToken()!=null) {
					autorizzazionePerRichiedente = StatoFunzionalita.ABILITATO.equals(pa.getAutorizzazioneToken().getAutorizzazioneApplicativi());
				}
				if(!autorizzazionePerRichiedente && pa.getServiziApplicativiAutorizzati()!=null) {
					// backward compatibility
					autorizzazionePerRichiedente = pa.getServiziApplicativiAutorizzati().sizeServizioApplicativoList()>0;
				}
				
				boolean autorizzazionePerRuolo = false;
				boolean checkRuoloRegistro = false;
				boolean checkRuoloEsterno = false;
				if(pa.getAutorizzazioneToken()!=null) {
	    			autorizzazionePerRuolo = StatoFunzionalita.ABILITATO.equals(pa.getAutorizzazioneToken().getAutorizzazioneRuoli());
				}
				if(autorizzazionePerRuolo) {
    				if( pa.getAutorizzazioneToken().getTipologiaRuoli()==null ||
    					RuoloTipologia.QUALSIASI.equals(pa.getAutorizzazioneToken().getTipologiaRuoli())){
    					checkRuoloRegistro = true;
    					checkRuoloEsterno = true;
    				} 
    				else if( RuoloTipologia.INTERNO.equals(pa.getAutorizzazioneToken().getTipologiaRuoli())){
    					checkRuoloRegistro = true;
    				}
    				else if( RuoloTipologia.ESTERNO.equals(pa.getAutorizzazioneToken().getTipologiaRuoli())){
    					checkRuoloEsterno = true;
    				}
				}
				
				/** Verifica consistenza identificazione del mittente */
				if(
						(
								autorizzazionePerRichiedente 
								|| 
								(autorizzazionePerRuolo && checkRuoloRegistro && !checkRuoloEsterno)
						)
						&&
						saFruitoreAnonimo
				){	
					this.context.addObject(Costanti.ERRORE_AUTORIZZAZIONE, Costanti.ERRORE_TRUE);
					this.erroriValidazione.add(this.validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.SICUREZZA_AUTORIZZAZIONE_FALLITA, 
							"Applicativo Mittente non identificato"));
					return;
				}
				if(!saFruitoreAnonimo &&
					(autorizzazionePerRichiedente || checkRuoloRegistro) 
					){
					// se utilizzo l'informazione dell'applicativo, tale informazione deve essere consistente rispetto a tutti i criteri di sicurezza
					if(sicurezzaMessaggio && !sicurezzaMessaggioIDAR04 &&
						!saIdentificatoBySecurity && !saVerificatoBySecurity) {
						this.context.addObject(Costanti.ERRORE_AUTORIZZAZIONE, Costanti.ERRORE_TRUE);
						addErroreMittenteNonAutorizzato(busta, "il certificato di firma non corrisponde all'applicativo");
						return;
					}
					if(sicurezzaToken &&
						!saIdentificatoByToken) {
						// CASO DEPRECATO: questo caso non puo' succedere poiche' nel caso di sicurezza token l'identificazione avviene SOLO per token
						//                 quindi si rientra nel caso sopra 'Applicativo Mittente non identificato'
						this.context.addObject(Costanti.ERRORE_AUTORIZZAZIONE, Costanti.ERRORE_TRUE);
						addErroreMittenteNonAutorizzato(busta, "il claim 'clientId' presente nel token non corrisponde all'applicativo");
						return;
					}
				}
				
				/** Autorizzazione per Richiedente */
				Eccezione eccezioneAutorizzazionePerRichiedente = null;
				if(autorizzazionePerRichiedente) {					
					boolean autorizzato = false;
					if(pa.getServiziApplicativiAutorizzati()!=null) {
						for (PortaApplicativaAutorizzazioneServizioApplicativo paSA : pa.getServiziApplicativiAutorizzati().getServizioApplicativoList()) {
							if(paSA.getTipoSoggettoProprietario().equals(busta.getTipoMittente()) &&
									paSA.getNomeSoggettoProprietario().equals(busta.getMittente()) &&
									paSA.getNome().equals(busta.getServizioApplicativoFruitore())) {
								autorizzato = true;
							}
						}
					}
					if(!autorizzato) {
						eccezioneAutorizzazionePerRichiedente = getErroreMittenteNonAutorizzato(busta, null);
					}
				}
				
				/** Autorizzazione per Ruolo */
				Eccezione eccezioneAutorizzazionePerRuolo = null;
				if(autorizzazionePerRuolo) {
    				StringBuilder detailsBufferRuoli = new StringBuilder();
    				ServizioApplicativo sa = null;
    				if(!saFruitoreAnonimo) {
    					IDServizioApplicativo idSA = new IDServizioApplicativo();
    					idSA.setIdSoggettoProprietario(new IDSoggetto(busta.getTipoMittente(), busta.getMittente()));
    					idSA.setNome(busta.getServizioApplicativoFruitore());
    					sa = factory.getCachedConfigIntegrationReader(state,requestInfo).getServizioApplicativo(idSA);
    				}
    				boolean authRuoli = ConfigurazionePdDReader._autorizzazioneRoles(
    								RegistroServiziManager.getInstance(state),
    								null, sa, 
    								null, false, 
    								this.context, requestInfo,
    								checkRuoloRegistro, checkRuoloEsterno,
    								detailsBufferRuoli,
    								pa.getAutorizzazioneToken().getRuoli().getMatch(), pa.getAutorizzazioneToken().getRuoli(),
    								true);
    				if(!authRuoli) {
    					String errore = "Applicativo Mittente";
    					if(!saFruitoreAnonimo) {
    						errore = errore + " "+ busta.getServizioApplicativoFruitore() + " (Soggetto: "+busta.getMittente()+")";
    					}
    					errore = errore + " non autorizzato; ";
    					eccezioneAutorizzazionePerRuolo = this.validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.SICUREZZA_AUTORIZZAZIONE_FALLITA, 
    							errore + detailsBufferRuoli.toString());
    				}
	    		}
				
				/** Gestione Eccezioni */
				if(autorizzazionePerRichiedente && autorizzazionePerRuolo) {
					if(eccezioneAutorizzazionePerRichiedente!=null && eccezioneAutorizzazionePerRuolo!=null) {
						this.context.addObject(Costanti.ERRORE_AUTORIZZAZIONE, Costanti.ERRORE_TRUE);
						this.erroriValidazione.add(eccezioneAutorizzazionePerRuolo); // uso eccezione per ruolo che e' più completa come messaggistica
					}
					// se una delle due autorizzazione e' andata a buon fine devo autorizzare
				}
				else {
					if(eccezioneAutorizzazionePerRichiedente!=null || eccezioneAutorizzazionePerRuolo!=null) {
						this.context.addObject(Costanti.ERRORE_AUTORIZZAZIONE, Costanti.ERRORE_TRUE);
						if(eccezioneAutorizzazionePerRichiedente!=null) {
							this.erroriValidazione.add(eccezioneAutorizzazionePerRichiedente); 
						}
						else {
							this.erroriValidazione.add(eccezioneAutorizzazionePerRuolo);
						}
					}
				}
			
				
				int sizeListaEccezioniPrimaAutorizzazione = sizeListaErroriValidazionePrimaAutorizzazione + sizeListaProcessamentoValidazionePrimaAutorizzazione; 
				int sizeListaEccezioniDipoAutorizzazione = this.erroriValidazione.size() + this.erroriProcessamento.size(); 
				if(sizeListaEccezioniPrimaAutorizzazione == sizeListaEccezioniDipoAutorizzazione) {
					msgDiag.logPersonalizzato(DIAGNOSTIC_VALIDATE+tipoDiagnostico+DIAGNOSTIC_COMPLETATA);
				}
				else {
					String errore = null;
					if(sizeListaErroriValidazionePrimaAutorizzazione!=this.erroriValidazione.size()) {
						errore = ModIValidazioneSintattica.buildErrore(this.erroriValidazione, sizeListaErroriValidazionePrimaAutorizzazione, factory);
					}
					else {
						errore = ModIValidazioneSintattica.buildErrore(this.erroriProcessamento, sizeListaProcessamentoValidazionePrimaAutorizzazione, factory);
					}
					msgDiag.addKeyword(CostantiPdD.KEY_ERRORE_PROCESSAMENTO, errore);
					msgDiag.logPersonalizzato(DIAGNOSTIC_VALIDATE+tipoDiagnostico+DIAGNOSTIC_FALLITA);	
				}
				autorizzazione = false;
			
			} // fine autorizzazione
			
			
		}catch(Exception e){
			
			if(msgDiag!=null && (verifica || autorizzazione)) {
				msgDiag.addKeyword(CostantiPdD.KEY_ERRORE_PROCESSAMENTO, e.getMessage());
				msgDiag.logPersonalizzato(DIAGNOSTIC_VALIDATE+tipoDiagnostico+DIAGNOSTIC_FALLITA);
			}
			
			this.erroriProcessamento.add(this.validazioneUtils.newEccezioneProcessamento(CodiceErroreCooperazione.ERRORE_GENERICO_PROCESSAMENTO_MESSAGGIO, 
					e.getMessage(),e));
		}
	}

	private void checkExp(String exp, Date now, boolean rest, String prefix) throws ProtocolException {
		
		boolean enabled = true;
		if(rest) {
			enabled = this.modiProperties.isRestSecurityTokenClaimsExpTimeCheck();
		}
		else {
			enabled = this.modiProperties.isSoapSecurityTokenTimestampExpiresTimeCheck();
		}
		if(!enabled) {
			return;
		}
		
		if(prefix==null) {
			prefix="";
		}
		Date dateExp = null;
		try {
			dateExp = DateUtils.getSimpleDateFormatMs().parse(exp);
		}catch(Exception e) {
			throw new ProtocolException(e.getMessage(),e);
		}
		/*
		 *   The "exp" (expiration time) claim identifies the expiration time on
			 *   or after which the JWT MUST NOT be accepted for processing.  The
			 *   processing of the "exp" claim requires that the current date/time
			 *   MUST be before the expiration date/time listed in the "exp" claim.
		 **/
		Date checkNow = now;
		Long tolerance = null;
		if(rest) {
			tolerance = this.modiProperties.getRestSecurityTokenClaimsExpTimeCheckToleranceMilliseconds();
		}
		else {
			tolerance = this.modiProperties.getSoapSecurityTokenTimestampExpiresTimeCheckToleranceMilliseconds();
		}
		if(tolerance!=null && tolerance.longValue()>0) {
			checkNow = new Date(now.getTime() - tolerance.longValue());
		}
		if(!checkNow.before(dateExp)){
			this.erroriValidazione.add(this.validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.MESSAGGIO_SCADUTO, 
					prefix+"Token scaduto in data '"+exp+"'"));
		}
	}
	
	private void checkNbf(String nbf, Date now, String prefix) throws ProtocolException {
		if(prefix==null) {
			prefix="";
		}
		Date dateNbf = null;
		try {
			dateNbf = DateUtils.getSimpleDateFormatMs().parse(nbf);
		}catch(Exception e) {
			throw new ProtocolException(e.getMessage(),e);
		}
		/*
		 *   The "nbf" (not before) claim identifies the time before which the JWT
		 *   MUST NOT be accepted for processing.  The processing of the "nbf"
		 *   claim requires that the current date/time MUST be after or equal to
		 *   the not-before date/time listed in the "nbf" claim. 
		 **/
		if(!dateNbf.before(now)){
			this.erroriValidazione.add(this.validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.MESSAGGIO_SCADUTO, 
					prefix+"Token non utilizzabile prima della data '"+nbf+"'"));
		}
	}
	
	private void checkIat(String iat, OpenSPCoop2Message msg, boolean rest, String prefix) throws ProtocolException {
		if(prefix==null) {
			prefix="";
		}
		Date dateIat = null;
		try {
			dateIat = DateUtils.getSimpleDateFormatMs().parse(iat);
		}catch(Exception e) {
			throw new ProtocolException(e.getMessage(),e);
		}
		/*
		 *   The "iat" (issued at) claim identifies the time at which the JWT was
			 *   issued.  This claim can be used to determine the age of the JWT.
			 *   The iat Claim can be used to reject tokens that were issued too far away from the current time, 
			 *   limiting the amount of time that nonces need to be stored to prevent attacks. The acceptable range is Client specific. 
		 **/
		Long old = null;
		Object iatObject = null;
		if(msg!=null) {
			iatObject = msg.getContextProperty(ModICostanti.MODIPA_OPENSPCOOP2_MSG_CONTEXT_IAT_TTL_CHECK);
		}
		if(iatObject instanceof Long) {
			old = (Long) iatObject;
		}
		if(old==null) {
			if(rest) {
				old = this.modiProperties.getRestSecurityTokenClaimsIatTimeCheckMilliseconds();
			}
			else {
				old = this.modiProperties.getSoapSecurityTokenTimestampCreatedTimeCheckMilliseconds();
			}
		}
		if(old!=null) {
			Date oldMax = new Date((DateManager.getTimeMillis() - old.longValue()));
			if(dateIat.before(oldMax)) {
				logError(prefix+"Token creato da troppo tempo (data creazione: '"+iat+"', data più vecchia consentita: '"+DateUtils.getSimpleDateFormatMs().format(oldMax)+"', configurazione ms: '"+old.longValue()+"')");
				this.erroriValidazione.add(this.validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.MESSAGGIO_SCADUTO, 
						prefix+"Token creato da troppo tempo (data creazione: '"+iat+"')"));
			}
		}
		
		
		checkIatFuture(dateIat, prefix, iat, rest);
	}
	private void checkIatFuture(Date dateIat, String prefix, String iat, boolean rest) throws ProtocolException {
		Long future = null;
		if(rest) {
			future = this.modiProperties.getRestSecurityTokenClaimsIatTimeCheckFutureToleranceMilliseconds();
		}
		else {
			future = this.modiProperties.getSoapSecurityTokenTimestampCreatedTimeCheckFutureToleranceMilliseconds();
		}
		if(future!=null) {
			Date futureMax = new Date((DateManager.getTimeMillis() + future.longValue()));
			if(dateIat.after(futureMax)) {
				logError(prefix+"Token creato nel futuro (data creazione: '"+iat+"', data massima futura consentita: '"+DateUtils.getSimpleDateFormatMs().format(futureMax)+"', configurazione ms: '"+future.longValue()+"')");
				this.erroriValidazione.add(this.validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.ORA_REGISTRAZIONE_NON_VALIDA, 
						prefix+"Token creato nel futuro (data creazione: '"+iat+"')"));
			}
		}
	}
	
	private void validatePurposeId(RemoteStoreConfig rsc, String prefixAudit, String securityAuditPattern) throws ProtocolException {
		
		try {
			if(rsc!=null &&
					(this.modiProperties.isSecurityTokenAuditExpectedPurposeId() || this.modiProperties.isSecurityTokenAuditCompareAuthorizationPurposeId())) {
			
				SecurityToken securityToken = ModIUtilities.readSecurityToken(this.context);
				if(securityToken==null) {
					this.erroriValidazione.add(this.validazioneUtils.newEccezioneValidazione(
							CodiceErroreCooperazione.SICUREZZA_NON_PRESENTE, 
							"Token di sicurezza non presenti"));
					return;
				}
				
				RestMessageSecurityToken restSecurityToken = securityToken.getAccessToken();

				String prefixAuthorization = getPrefixHeader(HttpConstants.AUTHORIZATION);
				String labelAuditPattern = CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_PATTERN_AUDIT_REST_02.equals(securityAuditPattern) ? 
						CostantiLabel.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_PATTERN_AUDIT_REST_02 : CostantiLabel.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_PATTERN_AUDIT_REST_01;
				String suffixAudit02 = " (richiesto con pattern '"+labelAuditPattern+"')";
				if(restSecurityToken==null) {
					this.erroriValidazione.add(this.validazioneUtils.newEccezioneValidazione(
							CodiceErroreCooperazione.SICUREZZA_NON_PRESENTE, 
							prefixAuthorization+MSG_ERRORE_NON_PRESENTE+suffixAudit02));
					return;
				}
				
				RestMessageSecurityToken auditToken = securityToken.getAudit();
				if(auditToken==null) {
					this.erroriValidazione.add(this.validazioneUtils.newEccezioneValidazione(
							CodiceErroreCooperazione.SICUREZZA_NON_PRESENTE, 
							prefixAudit+MSG_ERRORE_NON_PRESENTE));
					return;
				}
				
				boolean expected = this.modiProperties.isSecurityTokenAuditExpectedPurposeId();
				String purposeIdAuthorization = readPurposeId(restSecurityToken, prefixAuthorization, expected);
				String purposeIdAudit = readPurposeId(auditToken, prefixAudit, expected);
				
				if(this.modiProperties.isSecurityTokenAuditCompareAuthorizationPurposeId() &&
					purposeIdAuthorization!=null && purposeIdAudit!=null && !purposeIdAuthorization.equals(purposeIdAudit)) {
					String claim = org.openspcoop2.pdd.core.token.Costanti.PDND_PURPOSE_ID;
					this.erroriValidazione.add(this.validazioneUtils.newEccezioneValidazione(
							CodiceErroreCooperazione.SICUREZZA_NON_PRESENTE, 
							prefixAudit+"Claim '"+claim+"' presente nel token contiene un valore '"+purposeIdAudit+"' differente da quello presente nel token "+HttpConstants.AUTHORIZATION+" ("+purposeIdAuthorization+")"));
				}
			}
		}catch(Exception e) {
			throw new ProtocolException(e.getMessage(),e);
		}

	}
	private String readPurposeId(RestMessageSecurityToken restSecurityToken, String prefixAuthorization, boolean required) throws ProtocolException {
		String purposeId = null;
		String purposeIdClaim = org.openspcoop2.pdd.core.token.Costanti.PDND_PURPOSE_ID;
		try {
			purposeId = restSecurityToken.getPayloadClaim(purposeIdClaim);
			if( (purposeId==null || StringUtils.isEmpty(purposeId)) && required ) {
				this.erroriValidazione.add(this.validazioneUtils.newEccezioneValidazione(
						CodiceErroreCooperazione.SICUREZZA_TOKEN_NON_VALIDO, 
						prefixAuthorization+getErroreClaimNonPresente(purposeIdClaim)));
			}
		}catch(Exception e) {
			logError("Lettura purpose id token fallita: "+e.getMessage(),e);
			this.erroriValidazione.add(this.validazioneUtils.newEccezioneValidazione(
					CodiceErroreCooperazione.SICUREZZA_TOKEN_NON_VALIDO, 
					prefixAuthorization+getErroreClaimNonValido(purposeIdClaim)));
		}
		return purposeId;
	}
	
	private void validateAudit02(String securityMessageProfileSorgenteTokenIdAuth, boolean rest, String prefixAudit) throws ProtocolException {
		boolean expectedAccessToken = false;
		if(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SORGENTE_TOKEN_IDAUTH_VALUE_PDND.equals(securityMessageProfileSorgenteTokenIdAuth) ||
				ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SORGENTE_TOKEN_IDAUTH_VALUE_OAUTH.equals(securityMessageProfileSorgenteTokenIdAuth)) {
			expectedAccessToken = true;
		}
		
		SecurityToken securityToken = ModIUtilities.readSecurityToken(this.context);
		if(securityToken==null) {
			this.erroriValidazione.add(this.validazioneUtils.newEccezioneValidazione(
					CodiceErroreCooperazione.SICUREZZA_NON_PRESENTE, 
					"Token di sicurezza non presenti"));
			return;
		}
		
		RestMessageSecurityToken restSecurityToken = null;
		if(expectedAccessToken) {
			restSecurityToken = securityToken.getAccessToken();
		}
		else {
			if(rest) {
				restSecurityToken = securityToken.getAuthorization();
			}
			else {
				this.erroriValidazione.add(this.validazioneUtils.newEccezioneValidazione(
						CodiceErroreCooperazione.SICUREZZA_TOKEN_NON_VALIDO, 
						"Token di sicurezza IDAuth Locale su API Soap non è utilizzabile con pattern "+CostantiLabel.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_PATTERN_AUDIT_REST_02));
				return;
			}
		}
		String prefixAuthorization = getPrefixHeader(HttpConstants.AUTHORIZATION);
		String suffixAudit02 = " (richiesto con pattern '"+CostantiLabel.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_PATTERN_AUDIT_REST_02+"')";
		if(restSecurityToken==null) {
			this.erroriValidazione.add(this.validazioneUtils.newEccezioneValidazione(
					CodiceErroreCooperazione.SICUREZZA_NON_PRESENTE, 
					prefixAuthorization+MSG_ERRORE_NON_PRESENTE+suffixAudit02));
			return;
		}
		
		String digestClaimPrefix = org.openspcoop2.pdd.core.token.Costanti.PDND_DIGEST+".";
		
		String digestAlgo = readDigestAlgorithm(digestClaimPrefix, restSecurityToken, prefixAuthorization);
		String digestValue = readDigestValue(digestClaimPrefix, restSecurityToken, prefixAuthorization);
		if(digestAlgo==null || digestValue==null) {
			return; // errori gestiti nei metodi sopra
		}
		
		
		RestMessageSecurityToken auditSecurityToken = securityToken.getAudit();
		if(auditSecurityToken==null || auditSecurityToken.getToken()==null || StringUtils.isEmpty(auditSecurityToken.getToken())) {
			this.erroriValidazione.add(this.validazioneUtils.newEccezioneValidazione(
					CodiceErroreCooperazione.SICUREZZA_TOKEN_NON_PRESENTE, 
					prefixAudit+MSG_ERRORE_NON_PRESENTE));
			return;
		}
		
		// calcolo digest value token audit
		String digestAuditRicalcolato = null;
		try {
			digestAuditRicalcolato = org.openspcoop2.utils.digest.DigestUtils.getDigestValue(auditSecurityToken.getToken().getBytes(), digestAlgo, DigestEncoding.HEX, 
					false); // se rfc3230 true aggiunge prefisso algoritmo=
		}catch(Exception e) {
			logError("Calcolo digest del token di audit fallito: "+e.getMessage(),e);
			this.erroriValidazione.add(this.validazioneUtils.newEccezioneValidazione(
					CodiceErroreCooperazione.SICUREZZA, 
					prefixAudit+"check digest failed"));
			return;
		}
		if(!digestValue.equalsIgnoreCase(digestAuditRicalcolato)) {
			String digestValueClaim = digestClaimPrefix+org.openspcoop2.pdd.core.token.Costanti.PDND_DIGEST_VALUE;
			this.erroriValidazione.add(this.validazioneUtils.newEccezioneValidazione(
					CodiceErroreCooperazione.SICUREZZA_TOKEN_NON_VALIDO, 
					prefixAuthorization+"possiede un valore nel campo '"+digestValueClaim+"' non corrispondente al token di audit previsto dal pattern '"+CostantiLabel.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_PATTERN_AUDIT_REST_02+"'"));
		}
		
	}
	
	private String readDigestAlgorithm(String digestClaimPrefix, RestMessageSecurityToken restSecurityToken, String prefixAuthorization) throws ProtocolException {
		String digestAlgo = null;
		String digestAlgClaim = digestClaimPrefix+org.openspcoop2.pdd.core.token.Costanti.PDND_DIGEST_ALG;
		try {
			digestAlgo = restSecurityToken.getPayloadClaim(digestAlgClaim);
			if(digestAlgo==null || StringUtils.isEmpty(digestAlgo)) {
				this.erroriValidazione.add(this.validazioneUtils.newEccezioneValidazione(
						CodiceErroreCooperazione.SICUREZZA_TOKEN_NON_VALIDO, 
						prefixAuthorization+getErroreClaimNonPresente(digestAlgClaim)));
			}
		}catch(Exception e) {
			logError("Lettura algoritmo di digest in authorization token fallita: "+e.getMessage(),e);
			this.erroriValidazione.add(this.validazioneUtils.newEccezioneValidazione(
					CodiceErroreCooperazione.SICUREZZA_TOKEN_NON_VALIDO, 
					prefixAuthorization+getErroreClaimNonValido(digestAlgClaim)));
		}
		return digestAlgo;
	}
	private String readDigestValue(String digestClaimPrefix, RestMessageSecurityToken restSecurityToken, String prefixAuthorization) throws ProtocolException {
		String digestValue = null;
		String digestValueClaim = digestClaimPrefix+org.openspcoop2.pdd.core.token.Costanti.PDND_DIGEST_VALUE;
		try {
			digestValue = restSecurityToken.getPayloadClaim(digestValueClaim);
			if(digestValue==null || StringUtils.isEmpty(digestValue)) {
				this.erroriValidazione.add(this.validazioneUtils.newEccezioneValidazione(
						CodiceErroreCooperazione.SICUREZZA_TOKEN_NON_VALIDO, 
						prefixAuthorization+getErroreClaimNonPresente(digestValueClaim)));
			}
		}catch(Exception e) {
			logError("Lettura valore del digest audit in authorization token fallita: "+e.getMessage(),e);
			this.erroriValidazione.add(this.validazioneUtils.newEccezioneValidazione(
					CodiceErroreCooperazione.SICUREZZA_TOKEN_NON_VALIDO, 
					prefixAuthorization+getErroreClaimNonValido(digestValueClaim)));
		}
		return digestValue;
	}
	
	private RemoteStoreConfig enrichTokenInfo(RequestInfo requestInfo) throws ProtocolException {
	
		OpenSPCoop2Properties op2Properties = OpenSPCoop2Properties.getInstance();
		RemoteStoreConfig rsc = null;
		try {
			if(op2Properties.isGestoreChiaviPDNDclientInfoEnabled()) {
			
				rsc = getRemoteStoreConfig();
				if(rsc==null) {
					return rsc;
				}
				
				SecurityToken securityTokenForContext = SecurityTokenUtilities.readSecurityToken(this.context);
								
				String kid = readKidFromToken(securityTokenForContext);
				if(kid==null) {
					return rsc;
				}
				
				Object oInformazioniTokenNormalizzate = null;
				if(this.context!=null) {
					oInformazioniTokenNormalizzate = this.context.getObject(org.openspcoop2.pdd.core.token.Costanti.PDD_CONTEXT_TOKEN_INFORMAZIONI_NORMALIZZATE);
				}
				InformazioniToken informazioniTokenNormalizzate = null;
				String clientId = null;
				if(oInformazioniTokenNormalizzate instanceof InformazioniToken) {
					informazioniTokenNormalizzate = (InformazioniToken) oInformazioniTokenNormalizzate;
					clientId = informazioniTokenNormalizzate.getClientId();
				}
				if(clientId==null) {
					return rsc;
				}
				
				enrichTokenInfo(securityTokenForContext, informazioniTokenNormalizzate, requestInfo, rsc,
						kid, clientId);
			}
		}catch(Exception e) {
			throw new ProtocolException(e.getMessage(),e);
		}
		
		return rsc;
	}
	private RemoteStoreConfig getRemoteStoreConfig() throws ProtocolException {
		Object oTokenPolicy = null;
		if(this.context!=null) {
			oTokenPolicy = this.context.getObject(org.openspcoop2.pdd.core.token.Costanti.PDD_CONTEXT_TOKEN_POLICY);
		}
		String tokenPolicy = null;
		if(oTokenPolicy instanceof String) {
			tokenPolicy = (String) oTokenPolicy;
		}
		if(tokenPolicy==null) {
			return null;
		}
		
		return this.modiProperties.getRemoteStoreConfigByTokenPolicy(tokenPolicy);
	}
	private String readKidFromToken(SecurityToken securityTokenForContext) throws UtilsException {
		String kid = null;
		if(securityTokenForContext!=null && securityTokenForContext.getAccessToken()!=null) {
			kid = securityTokenForContext.getAccessToken().getKid();
			if(kid==null) {
				kid = securityTokenForContext.getAccessToken().getHeaderClaim("kid");
			}
		}
		return kid;
	}
	private void enrichTokenInfo(SecurityToken securityTokenForContext, InformazioniToken informazioniTokenNormalizzate, RequestInfo requestInfo, RemoteStoreConfig rsc,
			String kid, String clientId) throws KeystoreException, SecurityException, UtilsException {
		RemoteKeyType keyType = RemoteKeyType.JWK; // ignored
		RemoteStoreProvider remoteStoreProvider = new RemoteStoreProvider(requestInfo, keyType);
		RemoteStoreClientInfo rsci = GestoreKeystoreCache.getRemoteStoreClientInfo(requestInfo, kid, clientId, rsc, remoteStoreProvider);
		if(rsci!=null &&
			(rsci.getClientDetails()!=null || rsci.getOrganizationId()!=null || rsci.getOrganizationDetails()!=null) 
			){
			if(informazioniTokenNormalizzate.getPdnd()==null) {
				informazioniTokenNormalizzate.setPdnd(new HashMap<>());
			}
			if(rsci.getClientDetails()!=null) {
				JSONUtils jsonUtils = JSONUtils.getInstance();
				if(jsonUtils.isJson(rsci.getClientDetails())) {
					JsonNode root = jsonUtils.getAsNode(rsci.getClientDetails());
					PDNDTokenInfoDetails info = new PDNDTokenInfoDetails();
					info.setId(rsci.getClientId());
					info.setDetails(rsci.getClientDetails());
					enrichTokenInfoAddInClaims(jsonUtils, securityTokenForContext, informazioniTokenNormalizzate, root, PDNDTokenInfo.CLIENT_INFO, info);
				}
			}
			if(rsci.getOrganizationDetails()!=null) {
				JSONUtils jsonUtils = JSONUtils.getInstance();
				if(jsonUtils.isJson(rsci.getOrganizationDetails())) {
					JsonNode root = jsonUtils.getAsNode(rsci.getOrganizationDetails());
					PDNDTokenInfoDetails info = new PDNDTokenInfoDetails();
					info.setId(rsci.getOrganizationId());
					info.setDetails(rsci.getOrganizationDetails());
					enrichTokenInfoAddInClaims(jsonUtils, securityTokenForContext, informazioniTokenNormalizzate, root, PDNDTokenInfo.ORGANIZATION_INFO, info);
				}
			}
		}
	}
	private void enrichTokenInfoAddInClaims(JSONUtils jsonUtils, SecurityToken securityTokenForContext, InformazioniToken informazioniTokenNormalizzate, JsonNode root, String type,
			PDNDTokenInfoDetails info) {
		Map<String, Object> readClaims = jsonUtils.convertToSimpleMap(root);
		if(!readClaims.isEmpty()) {
			enrichTokenInfoAddInClaims(securityTokenForContext, informazioniTokenNormalizzate,
					type, info,
					readClaims);
		}
	}
	private void enrichTokenInfoAddInClaims(SecurityToken securityTokenForContext, InformazioniToken informazioniTokenNormalizzate,
			String type, PDNDTokenInfoDetails info,
			Map<String, Object> readClaims) {
		informazioniTokenNormalizzate.getPdnd().put(type,readClaims);
		String prefix = "pdnd."+type+".";
		Map<String, Serializable> readClaimsSerializable = new HashMap<>(); 
		if(informazioniTokenNormalizzate.getClaims()!=null) {
			for (Map.Entry<String,Object> entry : readClaims.entrySet()) {
				String key = prefix+entry.getKey();
				if(!informazioniTokenNormalizzate.getClaims().containsKey(key)) {
					informazioniTokenNormalizzate.getClaims().put(key, entry.getValue());
				}
				if(entry.getValue() instanceof Serializable) {
					readClaimsSerializable.put(entry.getKey(), (Serializable) entry.getValue());
				}
			}
		}
		
		info.setClaims(readClaimsSerializable);
		if(securityTokenForContext.getPdnd()==null) {
			securityTokenForContext.setPdnd(new PDNDTokenInfo());
		}
		securityTokenForContext.getPdnd().setInfo(type, info);
	}
	
	private void validateTokenAuthorizationId(OpenSPCoop2Message msg,
			String prefixAuthorization,
			Busta busta, boolean sicurezzaMessaggio) throws ProtocolException {
	
		Object useJtiAuthorizationObject = msg.getContextProperty(ModICostanti.MODIPA_OPENSPCOOP2_MSG_CONTEXT_USE_JTI_AUTHORIZATION);
		boolean useJtiAuthorization = false;
		if(useJtiAuthorizationObject instanceof Boolean) {
			useJtiAuthorization = (Boolean) useJtiAuthorizationObject;
		}
		
		if(useJtiAuthorization) {
			Object oInformazioniTokenNormalizzate = null;
			if(this.context!=null) {
				oInformazioniTokenNormalizzate = this.context.getObject(org.openspcoop2.pdd.core.token.Costanti.PDD_CONTEXT_TOKEN_INFORMAZIONI_NORMALIZZATE);
			}
			InformazioniToken informazioniTokenNormalizzate = null;
			String jtiClaimReceived = null;
			if(oInformazioniTokenNormalizzate!=null) {
				informazioniTokenNormalizzate = (InformazioniToken) oInformazioniTokenNormalizzate;
				jtiClaimReceived = informazioniTokenNormalizzate.getJti();
			}
			
			if(jtiClaimReceived==null || StringUtils.isEmpty(jtiClaimReceived)) {
				
				this.erroriValidazione.add(this.validazioneUtils.newEccezioneValidazione(
						CodiceErroreCooperazione.IDENTIFICATIVO_MESSAGGIO_NON_PRESENTE, 
							prefixAuthorization+getErroreClaimNonPresente(Claims.JSON_WEB_TOKEN_RFC_7519_JWT_ID)));
				
			}
			else {
				
				String idIntegrity = busta.getID();
				busta.removeProperty(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO_ID);
				if(sicurezzaMessaggio) {
					busta.addProperty(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO_ID, idIntegrity);
				}
				busta.setID(jtiClaimReceived);
				
			}
		}
		
	}
	
	private PortaApplicativa validateTokenAuthorizationAudience(OpenSPCoop2Message msg, IProtocolFactory<?> factory, IState state, RequestInfo requestInfo,
			boolean isRichiesta, String prefixAuthorization,
			boolean checkAudienceByModIConfig) throws ProtocolException {
		
		PortaApplicativa pa = readPortaApplicativa(msg, factory, state, requestInfo);
		
		boolean audienceCheckDefinedInAuthorizationTokenClaims = isAudienceCheckDefinedInAuthorizationTokenClaims(pa);
	
		if(!audienceCheckDefinedInAuthorizationTokenClaims) {
			
			validateTokenAuthorizationAudienceByModIConfig(msg, factory, state, requestInfo,
					isRichiesta, prefixAuthorization,
					checkAudienceByModIConfig);
			
		}
		
		return pa;
	}
	
	private void validateTokenAuthorizationAudienceByModIConfig(OpenSPCoop2Message msg, IProtocolFactory<?> factory, IState state, RequestInfo requestInfo,
			boolean isRichiesta, String prefixAuthorization,
			boolean checkAudienceByModIConfig) throws ProtocolException {
		List<String> audienceClaimReceived = readAudienceFromTokenOAuth();			
		if(audienceClaimReceived==null || audienceClaimReceived.isEmpty()) {
			
			this.erroriValidazione.add(this.validazioneUtils.newEccezioneValidazione(
					isRichiesta ? CodiceErroreCooperazione.SERVIZIO_APPLICATIVO_EROGATORE_NON_PRESENTE :
						CodiceErroreCooperazione.SERVIZIO_APPLICATIVO_FRUITORE_NON_PRESENTE, 
						prefixAuthorization+getErroreClaimNonPresente(Claims.JSON_WEB_TOKEN_RFC_7519_AUDIENCE)));
			
		}
		else {
		
			if(checkAudienceByModIConfig) {
				checkAudienceModIConfig(msg, factory, state, requestInfo,
						isRichiesta, prefixAuthorization,
						audienceClaimReceived);
			}
			else {
				
				this.erroriValidazione.add(this.validazioneUtils.newEccezioneValidazione(
						isRichiesta ? CodiceErroreCooperazione.SERVIZIO_APPLICATIVO_EROGATORE_NON_VALIDO :
							CodiceErroreCooperazione.SERVIZIO_APPLICATIVO_FRUITORE_NON_VALIDO, 
							prefixAuthorization+"Token contenente un claim '"+Claims.JSON_WEB_TOKEN_RFC_7519_AUDIENCE+"' non verificabile; autorizzazione per token claim non definita"));
				
			}
			
		}
	}
	
	private PortaApplicativa readPortaApplicativa(OpenSPCoop2Message msg, IProtocolFactory<?> factory, IState state, RequestInfo requestInfo) throws ProtocolException {
		
		PortaApplicativa pa = null;
		String interfaceName = null;
		if(msg!=null && msg.getTransportRequestContext()!=null && msg.getTransportRequestContext().getInterfaceName()!=null) {
			interfaceName = msg.getTransportRequestContext().getInterfaceName();
		}
		else if(requestInfo!=null && requestInfo.getProtocolContext()!=null && requestInfo.getProtocolContext().getInterfaceName()!=null) {
			interfaceName = requestInfo.getProtocolContext().getInterfaceName();
		}
		if(interfaceName==null) {
			throw new ProtocolException("ID Porta non presente");
		}
		IDPortaApplicativa idPA = new IDPortaApplicativa();
		idPA.setNome(interfaceName);
		try {
			pa = factory.getCachedConfigIntegrationReader(state, requestInfo).getPortaApplicativa(idPA); // pa invocata
		}catch(Exception e) {
			throw new ProtocolException(e.getMessage(),e);
		}
		
		return pa;
		
	}
	private PortaApplicativa readPortaApplicativaDefault(OpenSPCoop2Message msg, IProtocolFactory<?> factory, IState state, RequestInfo requestInfo) throws ProtocolException {
		PortaApplicativa paDefault = null;
		if(msg!=null) {
			Object nomePortaInvocataObject = msg.getContextProperty(CostantiPdD.NOME_PORTA_INVOCATA);
			String nomePorta = null;
			if(nomePortaInvocataObject instanceof String) {
				nomePorta = (String) nomePortaInvocataObject;
			}
			if(nomePorta==null && this.context!=null && this.context.containsKey(CostantiPdD.NOME_PORTA_INVOCATA)) {
				nomePorta = (String) this.context.getObject(CostantiPdD.NOME_PORTA_INVOCATA);
			}
			if(nomePorta==null && requestInfo!=null && requestInfo.getProtocolContext()!=null && requestInfo.getProtocolContext().getInterfaceName()!=null) {
				nomePorta = requestInfo.getProtocolContext().getInterfaceName(); // se non e' presente 'NOME_PORTA_INVOCATA' significa che non e' stato invocato un gruppo specifico
			}
			if(nomePorta!=null) {
				IDPortaApplicativa idPA = new IDPortaApplicativa();
				idPA.setNome(nomePorta);
				try {
					paDefault = factory.getCachedConfigIntegrationReader(state, requestInfo).getPortaApplicativa(idPA);
				}catch(Exception e) {
					throw new ProtocolException(e.getMessage(),e);
				}
			}
			else {
				throw new ProtocolException("ID Porta 'default' non presente");
			}
		}
		return paDefault;
	}
	private boolean isAudienceCheckDefinedInAuthorizationTokenClaims(PortaApplicativa pa) throws ProtocolException {
		
		boolean audienceCheckDefinedInAuthorizationTokenClaims = false;
	
		if(pa.getGestioneToken()!=null && pa.getGestioneToken().getOptions()!=null) {
			SortedMap<List<String>> properties = null;
			try {
				properties = PropertiesUtilities.convertTextToSortedListMap(pa.getGestioneToken().getOptions());
			}catch(Exception e) {
				throw new ProtocolException(e.getMessage(),e);
			}
			if(properties!=null && properties.size()>0 && properties.containsKey(Claims.JSON_WEB_TOKEN_RFC_7519_AUDIENCE)) {
				audienceCheckDefinedInAuthorizationTokenClaims = true; // la verifica viene fatta nell'autorizzazione per token claims					
			}
		}
		
		return audienceCheckDefinedInAuthorizationTokenClaims;
	}
	private List<String> readAudienceFromTokenOAuth() {
		Object oInformazioniTokenNormalizzate = null;
		if(this.context!=null) {
			oInformazioniTokenNormalizzate = this.context.getObject(org.openspcoop2.pdd.core.token.Costanti.PDD_CONTEXT_TOKEN_INFORMAZIONI_NORMALIZZATE);
		}
		InformazioniToken informazioniTokenNormalizzate = null;
		List<String> audienceClaimReceived = null;
		if(oInformazioniTokenNormalizzate!=null) {
			informazioniTokenNormalizzate = (InformazioniToken) oInformazioniTokenNormalizzate;
			audienceClaimReceived = informazioniTokenNormalizzate.getAud();
		}
		return audienceClaimReceived;
	}
	private void checkAudienceModIConfig(OpenSPCoop2Message msg, IProtocolFactory<?> factory, IState state, RequestInfo requestInfo,
			boolean isRichiesta, String prefixAuthorization,
			List<String> audienceClaimReceived) throws ProtocolException {
		PortaApplicativa paDefault = readPortaApplicativaDefault(msg, factory, state, requestInfo);
		
		/**System.out.println("VERIFICO RISPETTO AL VALORE ATTESO '"+modISecurityConfig.getAudience()+"'");*/
		
		checkAudienceModIConfig(msg, state, requestInfo,
				isRichiesta, prefixAuthorization,
				paDefault,
				audienceClaimReceived);
	}
	private void checkAudienceModIConfig(OpenSPCoop2Message msg, IState state, RequestInfo requestInfo,
			boolean isRichiesta, String prefixAuthorization,
			PortaApplicativa paDefault,
			List<String> audienceClaimReceived) throws ProtocolException {
		ModISecurityConfig modISecurityConfig = new ModISecurityConfig(msg, this.protocolFactory, state,requestInfo, 
				!isRichiesta, // fruizione, 
				isRichiesta, // request,
				paDefault);
		
		boolean find = false;
		for (String claim : audienceClaimReceived) {
			if(claim.equalsIgnoreCase(modISecurityConfig.getAudience())) {
				find = true;
				break;
			}	
		}
		if(!find) {
			this.erroriValidazione.add(this.validazioneUtils.newEccezioneValidazione(
					isRichiesta ? CodiceErroreCooperazione.SERVIZIO_APPLICATIVO_EROGATORE_NON_VALIDO :
						CodiceErroreCooperazione.SERVIZIO_APPLICATIVO_FRUITORE_NON_VALIDO, 
						prefixAuthorization+getErroreClaimNonValido(Claims.JSON_WEB_TOKEN_RFC_7519_AUDIENCE)));
		}
	}
	
	private void logError(String msg) {
		if(this.log!=null) {
			this.log.error(msg);
		}
	}
	private void logError(String msg, Exception e) {
		if(this.log!=null) {
			this.log.error(msg,e);
		}
	}
}
