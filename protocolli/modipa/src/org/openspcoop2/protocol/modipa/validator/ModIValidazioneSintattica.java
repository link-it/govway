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


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.soap.SOAPEnvelope;

import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.constants.Costanti;
import org.openspcoop2.core.constants.TipoPdD;
import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.Resource;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.core.registry.driver.IDServizioFactory;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.core.CostantiPdD;
import org.openspcoop2.pdd.logger.MsgDiagnosticiProperties;
import org.openspcoop2.pdd.logger.MsgDiagnostico;
import org.openspcoop2.protocol.basic.validator.ValidazioneSintattica;
import org.openspcoop2.protocol.engine.utils.NamingUtils;
import org.openspcoop2.protocol.modipa.AbstractModISecurityToken;
import org.openspcoop2.protocol.modipa.ModIBustaRawContent;
import org.openspcoop2.protocol.modipa.config.ModIProperties;
import org.openspcoop2.protocol.modipa.constants.ModICostanti;
import org.openspcoop2.protocol.modipa.constants.ModIHeaderType;
import org.openspcoop2.protocol.modipa.utils.ModIPropertiesUtils;
import org.openspcoop2.protocol.modipa.utils.ModISecurityConfig;
import org.openspcoop2.protocol.modipa.utils.ModITruststoreConfig;
import org.openspcoop2.protocol.modipa.utils.ModIUtilities;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.Eccezione;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.SecurityToken;
import org.openspcoop2.protocol.sdk.constants.CodiceErroreCooperazione;
import org.openspcoop2.protocol.sdk.constants.ErroreCooperazione;
import org.openspcoop2.protocol.sdk.constants.IntegrationFunctionError;
import org.openspcoop2.protocol.sdk.registry.IConfigIntegrationReader;
import org.openspcoop2.protocol.sdk.registry.IRegistryReader;
import org.openspcoop2.protocol.sdk.state.IState;
import org.openspcoop2.protocol.sdk.state.RequestInfo;
import org.openspcoop2.protocol.sdk.validator.ProprietaValidazioneErrori;
import org.openspcoop2.protocol.sdk.validator.ValidazioneSintatticaResult;
import org.openspcoop2.protocol.sdk.validator.ValidazioneUtils;
import org.openspcoop2.utils.transport.http.HttpConstants;

/**
 * Classe che implementa, in base al protocollo ModI, l'interfaccia {@link org.openspcoop2.protocol.sdk.validator.IValidazioneSintattica}
 *
 * @author Poli Andrea (apoli@link.it)
 * @author Lorenzo Nardi (nardi@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ModIValidazioneSintattica extends ValidazioneSintattica<AbstractModISecurityToken<?>>{

	/** ValidazioneUtils */
	protected ValidazioneUtils validazioneUtils;
	private ModIProperties modiProperties;
	
	public ModIValidazioneSintattica(IProtocolFactory<AbstractModISecurityToken<?>> factory, IState state) throws ProtocolException {
		super(factory, state);
		this.validazioneUtils = new ValidazioneUtils(factory);
		this.modiProperties = ModIProperties.getInstance();
	}
	
	private static final String DIAGNOSTIC_VALIDATE_TOKEN_ID_AUTH = "validateTokenIdAuth";
	private static final String DIAGNOSTIC_VALIDATE_TOKEN_INTEGRITY = "validateTokenIntegrity";
	private static final String DIAGNOSTIC_IN_CORSO = "inCorso";
	private static final String DIAGNOSTIC_COMPLETATA = "completata";
	private static final String DIAGNOSTIC_FALLITA = "fallita";

	
	@Override
	public ValidazioneSintatticaResult<AbstractModISecurityToken<?>> validaRichiesta(OpenSPCoop2Message msg,
			Busta datiBustaLettiURLMappingProperties, ProprietaValidazioneErrori proprietaValidazioneErrori)
			throws ProtocolException {
		
		ValidazioneSintatticaResult<AbstractModISecurityToken<?>> basicResult = super.validaRichiesta(msg, datiBustaLettiURLMappingProperties, proprietaValidazioneErrori);
		return this.validate(basicResult, true, msg, datiBustaLettiURLMappingProperties);

	}
	
	@Override
	public ValidazioneSintatticaResult<AbstractModISecurityToken<?>> validaRisposta(OpenSPCoop2Message msg,
			Busta bustaRichiesta, ProprietaValidazioneErrori proprietaValidazioneErrori) throws ProtocolException {
		
		ValidazioneSintatticaResult<AbstractModISecurityToken<?>> basicResult = super.validaRisposta(msg, bustaRichiesta, proprietaValidazioneErrori);
		return this.validate(basicResult, false, msg, bustaRichiesta);
		
	}
	
	private ValidazioneSintatticaResult<AbstractModISecurityToken<?>> validate(ValidazioneSintatticaResult<AbstractModISecurityToken<?>> basicResult, 
			boolean request, OpenSPCoop2Message msg, Busta datiRichiesta) {
		List<Eccezione> erroriValidazione = new ArrayList<>();
		List<Eccezione> erroriProcessamento = new ArrayList<>();
		
		ModIBustaRawContent rawContent = null;
		
		Busta bustaRitornata = basicResult.getBusta();
		
		String erroreProcessamentoInternalMessage = null;
		try {
					
			IDServizio idServizio = null;
			if(datiRichiesta!=null && 
					datiRichiesta.getTipoDestinatario()!=null && datiRichiesta.getDestinatario()!=null &&
					datiRichiesta.getTipoServizio()!=null && datiRichiesta.getServizio()!=null &&
					datiRichiesta.getVersioneServizio()!=null) {
				idServizio = IDServizioFactory.getInstance().getIDServizioFromValues(datiRichiesta.getTipoServizio(), datiRichiesta.getServizio(), 
						datiRichiesta.getTipoDestinatario(), datiRichiesta.getDestinatario(), 
						datiRichiesta.getVersioneServizio());
				
				RequestInfo requestInfo = null;
				if(this.context!=null && this.context.containsKey(org.openspcoop2.core.constants.Costanti.REQUEST_INFO)) {
					requestInfo = (RequestInfo) this.context.getObject(org.openspcoop2.core.constants.Costanti.REQUEST_INFO);
				}
				
				IRegistryReader registryReader = this.getProtocolFactory().getCachedRegistryReader(this.state, requestInfo);
				IConfigIntegrationReader configIntegrationReader = this.getProtocolFactory().getCachedConfigIntegrationReader(this.state, requestInfo);
				
				AccordoServizioParteSpecifica asps = registryReader.getAccordoServizioParteSpecifica(idServizio);
				
				IDAccordo idAccordo = IDAccordoFactory.getInstance().getIDAccordoFromUri(asps.getAccordoServizioParteComune());
				AccordoServizioParteComune aspc = registryReader.getAccordoServizioParteComune(idAccordo);
				boolean rest = org.openspcoop2.core.registry.constants.ServiceBinding.REST.equals(aspc.getServiceBinding());
				
				String nomePortType = asps.getPortType();
				String azione = datiRichiesta.getAzione();
				
				IDSoggetto idSoggettoMittente = null;
				if(!request && 	datiRichiesta.getTipoMittente()!=null &&
						datiRichiesta.getMittente()!=null) {
					
					idSoggettoMittente = new IDSoggetto(datiRichiesta.getTipoMittente(),datiRichiesta.getMittente());
					
				}
				
				IDServizioApplicativo idSA = null;
				ServizioApplicativo sa = null;
				if(!request && datiRichiesta.getServizioApplicativoFruitore()!=null &&
						!CostantiPdD.SERVIZIO_APPLICATIVO_ANONIMO.equals(datiRichiesta.getServizioApplicativoFruitore()) &&
						idSoggettoMittente!=null) {
					
					idSA = new IDServizioApplicativo();
					idSA.setIdSoggettoProprietario(idSoggettoMittente);
					idSA.setNome(datiRichiesta.getServizioApplicativoFruitore());
					
					sa = configIntegrationReader.getServizioApplicativo(idSA);
				}
				
				ModIValidazioneSintatticaRest validatoreSintatticoRest = null;
				ModIValidazioneSintatticaSoap validatoreSintatticoSoap = null;
				if(rest) {
					validatoreSintatticoRest = new ModIValidazioneSintatticaRest(this.log, this.state, this.context, this.protocolFactory, requestInfo, this.modiProperties, this.validazioneUtils);
				}
				else {
					validatoreSintatticoSoap = new ModIValidazioneSintatticaSoap(this.log, this.state, this.context, this.protocolFactory, requestInfo, this.modiProperties, this.validazioneUtils);
				}
				
				
				// check Fault
				boolean isFault = false;
				if(rest) {
					isFault = msg.isFault() || msg.castAsRest().isProblemDetailsForHttpApis_RFC7807();	
				}
				else {
					isFault = msg.isFault() || msg.castAsSoap().hasSOAPFault();
				}
				
				
				
				// Lettura profili
				
				
				/* *** PROFILO INTERAZIONE *** */
				
				String interactionProfile = ModIPropertiesUtils.readPropertyInteractionProfile(aspc, nomePortType, azione);
				bustaRitornata.addProperty(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_INTERAZIONE, interactionProfile);
				
				if(ModICostanti.MODIPA_PROFILO_INTERAZIONE_VALUE_BLOCCANTE.equals(interactionProfile)) {
					
					if(!isFault &&
						rest) {
						validatoreSintatticoRest.validateSyncInteractionProfile(msg, request, erroriValidazione);
					}
					
				}
				else if(ModICostanti.MODIPA_PROFILO_INTERAZIONE_VALUE_NON_BLOCCANTE.equals(interactionProfile)) {
					
					String asyncInteractionType = ModIPropertiesUtils.readPropertyAsyncInteractionProfile(aspc, nomePortType, azione);
					bustaRitornata.addProperty(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_INTERAZIONE_ASINCRONA_TIPO, asyncInteractionType);
					
					String asyncInteractionRole = ModIPropertiesUtils.readPropertyAsyncInteractionRole(aspc, nomePortType, azione);
					bustaRitornata.addProperty(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_INTERAZIONE_ASINCRONA_RUOLO, asyncInteractionRole);
					
					AccordoServizioParteComune apiContenenteRisorsa = null; 
					if(!ModICostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_RUOLO_VALUE_RICHIESTA.equals(asyncInteractionRole)) {
						
						if(ModICostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_VALUE_PUSH.equals(asyncInteractionType)) {
							String asyncInteractionRequestApi = ModIPropertiesUtils.readPropertyAsyncInteractionRequestApi(aspc, nomePortType, azione);
							IDAccordo idApiCorrelata = IDAccordoFactory.getInstance().getIDAccordoFromUri(asyncInteractionRequestApi);
							String labelApi = NamingUtils.getLabelAccordoServizioParteComune(idApiCorrelata);
							bustaRitornata.addProperty(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_INTERAZIONE_ASINCRONA_API_RICHIESTA_CORRELATA, labelApi);
							apiContenenteRisorsa = registryReader.getAccordoServizioParteComune(idApiCorrelata, false, false);
						}
						else {
							apiContenenteRisorsa = aspc;
						}
						
						if(rest) {
							
							String asyncInteractionRequestResource = ModIPropertiesUtils.readPropertyAsyncInteractionRequestAction(aspc, nomePortType, azione);
							
							String labelResourceCorrelata = asyncInteractionRequestResource;
							for (Resource r : apiContenenteRisorsa.getResourceList()) {
								if(r.getNome().equals(asyncInteractionRequestResource)) {
									labelResourceCorrelata = NamingUtils.getLabelResource(r);
									break;
								}
							}
							
							bustaRitornata.addProperty(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_INTERAZIONE_ASINCRONA_RISORSA_RICHIESTA_CORRELATA, labelResourceCorrelata);
							
						}
						else {
							
							String asyncInteractionRequestService = ModIPropertiesUtils.readPropertyAsyncInteractionRequestService(aspc, nomePortType, azione);
							bustaRitornata.addProperty(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_INTERAZIONE_ASINCRONA_SERVIZIO_RICHIESTA_CORRELATA, asyncInteractionRequestService);
							
							String asyncInteractionRequestAction = ModIPropertiesUtils.readPropertyAsyncInteractionRequestAction(aspc, nomePortType, azione);
							bustaRitornata.addProperty(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_INTERAZIONE_ASINCRONA_AZIONE_RICHIESTA_CORRELATA, asyncInteractionRequestAction);
							
						}
						
					}
					else {
						apiContenenteRisorsa = aspc;
					}
					
					String replyTo = null;
					if(ModICostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_RUOLO_VALUE_RICHIESTA.equals(asyncInteractionRole) &&
						request &&  ModICostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_VALUE_PUSH.equals(asyncInteractionType)) {
						// devo cercare l'url di invocazione del servizio fruito correlato.
						
						try {
							replyTo = ModIUtilities.getReplyToFruizione(idServizio.getSoggettoErogatore(), idSoggettoMittente, aspc, nomePortType, azione, 
									registryReader, configIntegrationReader, this.protocolFactory, this.state, requestInfo);
						}catch(Exception e) {
							throw new ProtocolException("Configurazione presente nel registro non corretta: "+e.getMessage(),e);
						}
					}
					
					if(!isFault) {
						if(rest) {
							validatoreSintatticoRest.validateAsyncInteractionProfile(msg, request, asyncInteractionType, asyncInteractionRole, 
									apiContenenteRisorsa, azione,
									bustaRitornata, erroriValidazione,
									replyTo);
						}
						else {
							validatoreSintatticoSoap.validateAsyncInteractionProfile(msg, request, asyncInteractionType, asyncInteractionRole, 
									bustaRitornata, erroriValidazione,
									replyTo);
						}
					}
				}
				
				
				/* *** SICUREZZA CANALE *** */
				
				String securityChannelProfile = ModIPropertiesUtils.readPropertySecurityChannelProfile(aspc);
				bustaRitornata.addProperty(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_CANALE, securityChannelProfile.toUpperCase());
				
				if(request && this.context!=null) {
					
					@SuppressWarnings("unused")
					SecurityToken securityTokenForContext = ModIUtilities.newSecurityToken(this.context); // creo per averlo a disposizione anche se non e' presente la sicurezza messaggio
					
				}
				
				
				/* *** SICUREZZA MESSAGGIO *** */
				
				boolean filterPDND = true;
				String securityMessageProfileNonFiltratoPDND = ModIPropertiesUtils.readPropertySecurityMessageProfile(aspc, nomePortType, azione, !filterPDND);
				
				boolean existsSecurityFlusso =  false;
				if(securityMessageProfileNonFiltratoPDND!=null && !ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_UNDEFINED.equals(securityMessageProfileNonFiltratoPDND)) {
					existsSecurityFlusso = ModIPropertiesUtils.processSecurity(aspc, nomePortType, azione, request, 
								msg, rest, this.modiProperties);
				}
					
				if(existsSecurityFlusso) {
					bustaRitornata.addProperty(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO, 
							ModIPropertiesUtils.convertProfiloSicurezzaToSDKValue(securityMessageProfileNonFiltratoPDND, rest));
					
					String sorgenteToken = ModIPropertiesUtils.readPropertySecurityMessageSorgenteToken(aspc, nomePortType, azione, true);
					bustaRitornata.addProperty(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO_SORGENTE_TOKEN,
							ModIPropertiesUtils.convertProfiloSicurezzaSorgenteTokenToSDKValue(sorgenteToken));
					boolean sorgenteLocale = true;
					if(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SORGENTE_TOKEN_IDAUTH_VALUE_PDND.equals(sorgenteToken) ||
							ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SORGENTE_TOKEN_IDAUTH_VALUE_OAUTH.equals(sorgenteToken)) {
						sorgenteLocale = false;
					}
					
					String securityMessageProfile = ModIPropertiesUtils.readPropertySecurityMessageProfile(aspc, nomePortType, azione, filterPDND);
										
					MsgDiagnostico msgDiag = null;
					TipoPdD tipoPdD = request ? TipoPdD.APPLICATIVA : TipoPdD.DELEGATA;
					IDSoggetto idSoggetto = TipoPdD.DELEGATA.equals(tipoPdD) ? idSoggettoMittente : idServizio.getSoggettoErogatore();
					if(idSoggetto==null || idSoggetto.getTipo()==null || idSoggetto.getNome()==null) {
						idSoggetto = OpenSPCoop2Properties.getInstance().getIdentitaPortaDefault(this.protocolFactory.getProtocol(), requestInfo);
					}
					else {
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
					String tipoDiagnostico = request ? ".richiesta." : ".risposta.";
					
					boolean processSecurity = false;
					boolean securityMessageProfilePrevedeTokenLocale = securityMessageProfile!=null && !ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_UNDEFINED.equals(securityMessageProfile);
					if(securityMessageProfilePrevedeTokenLocale) {
						processSecurity = existsSecurityFlusso;
					}
					
					boolean corniceSicurezza = ModIPropertiesUtils.isPropertySecurityMessageConCorniceSicurezza(aspc, nomePortType, azione);
					String patternCorniceSicurezza = null;
					String schemaCorniceSicurezza = null;
					if(corniceSicurezza) {
						patternCorniceSicurezza = ModIPropertiesUtils.readPropertySecurityMessageCorniceSicurezzaPattern(aspc, nomePortType, azione);
						if(patternCorniceSicurezza==null) {
							// backward compatibility
							patternCorniceSicurezza = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_PATTERN_VALUE_OLD;
						}
						if(!ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_PATTERN_VALUE_OLD.equals(patternCorniceSicurezza)) {
							schemaCorniceSicurezza = ModIPropertiesUtils.readPropertySecurityMessageCorniceSicurezzaSchema(aspc, nomePortType, azione);
						}
					}
					
					boolean fruizione = !request;
					
					boolean processAudit = !fruizione && corniceSicurezza && 
							patternCorniceSicurezza!=null && !ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_PATTERN_VALUE_OLD.equals(patternCorniceSicurezza);
												
					boolean keystoreKidMode = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM0401.equals(securityMessageProfile)
							||
							ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM0402.equals(securityMessageProfile)
							||
							!sorgenteLocale;
					
					Map<String, Object> dynamicMap = null;
					
					ModISecurityConfig securityConfig = null;
					ModITruststoreConfig trustStoreCertificati = null;
					ModITruststoreConfig trustStoreSsl = null;
					
					String headerTokenRest = null;
					String headerTokenRestIntegrity = null; // nel caso di Authorization insieme a Agid-JWT-Signature
					boolean integritaCustom = false;
					
					boolean buildSecurityTokenInRequest = true;
										
					if(processSecurity || processAudit) {
						
						// dynamicMap
						// FIX: va inizializzato dentro il metodo 'validateSecurityProfile' dopo aver identificato l'applicativo
/**						boolean bufferMessage_readOnly = this.modiProperties.isReadByPathBufferEnabled();
//						Map<String, Object> dynamicMap = null;
//						Map<String, Object> dynamicMapRequest = null;
//						if(!request) {
//							dynamicMapRequest = ModIUtilities.removeDynamicMapRequest(this.context);
//						}
//						if(dynamicMapRequest!=null) {
//							dynamicMap = DynamicUtils.buildDynamicMapResponse(msg, this.context, null, this.log, bufferMessage_readOnly, dynamicMapRequest);
//						}
//						else {
//							dynamicMap = DynamicUtils.buildDynamicMap(msg, this.context, datiRichiesta, this.log, bufferMessage_readOnly);
//							ModIUtilities.saveDynamicMapRequest(this.context, dynamicMap);
//						}*/
						dynamicMap = new HashMap<>();
						
						// header
						Boolean multipleHeaderAuthorizationConfig = null;
						if(rest) {
							headerTokenRest = ModIPropertiesUtils.readPropertySecurityMessageHeader(aspc, nomePortType, azione, request, filterPDND);
							if(headerTokenRest.contains(" ")) {
								String [] tmp = headerTokenRest.split(" ");
								if(tmp!=null && tmp.length==2 && tmp[0]!=null && tmp[1]!=null) {
									headerTokenRest=tmp[0];
									headerTokenRestIntegrity=tmp[1];
									multipleHeaderAuthorizationConfig = true;
								}
							}
							integritaCustom = ModIPropertiesUtils.isPropertySecurityMessageHeaderCustom(aspc, nomePortType, azione, request);
							if(integritaCustom) {
								String hdrCustom = headerTokenRest;
								if(multipleHeaderAuthorizationConfig!=null && multipleHeaderAuthorizationConfig && headerTokenRestIntegrity!=null) {
									hdrCustom = headerTokenRestIntegrity;
								}
								bustaRitornata.addProperty(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO_CUSTOM_HEADER,hdrCustom);
							}
						}
						
						// truststore
						securityConfig = new ModISecurityConfig(msg, this.context, this.protocolFactory, this.state, requestInfo, idSoggettoMittente, 
								aspc, asps, sa, rest, fruizione, request,
								multipleHeaderAuthorizationConfig,
								keystoreKidMode,
								patternCorniceSicurezza, schemaCorniceSicurezza);
						trustStoreCertificati = new ModITruststoreConfig(fruizione, idSoggettoMittente, asps, false);
						if(securityConfig.isX5u()) {
							trustStoreSsl = new ModITruststoreConfig(fruizione, idSoggettoMittente, asps, true);
						}
						
						// securityToken
						if(!request) {
							String securityMessageRequest = null;
							if(datiRichiesta!=null) {
								securityMessageRequest = datiRichiesta.getProperty(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO);
							}
							if(securityMessageRequest==null) {
								buildSecurityTokenInRequest = false;
							}
							msg.addContextProperty(ModICostanti.MODIPA_OPENSPCOOP2_MSG_CONTEXT_BUILD_SECURITY_REQUEST_TOKEN, buildSecurityTokenInRequest);
						}
						else {
							if(this.context!=null) {
								this.context.addObject(ModICostanti.MODIPA_OPENSPCOOP2_MSG_CONTEXT_BUILD_SECURITY_REQUEST_TOKEN, processSecurity);
							}
						}
					}
					
					// security (ID_AUTH e INTEGRITY)
					boolean errorOccurs = false;
					if(processSecurity) {
																						
						boolean includiRequestDigest = ModIPropertiesUtils.isPropertySecurityMessageIncludiRequestDigest(aspc, nomePortType, azione);
						
						boolean signAttachments = false;
						if(!rest) {
							signAttachments = ModIPropertiesUtils.isAttachmentsSignature(aspc, nomePortType, azione, request, msg);
						}
							
						boolean integritaX509 = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM0301.equals(securityMessageProfile) || 
								ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM0302.equals(securityMessageProfile);
						boolean integritaKid = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM0401.equals(securityMessageProfile) || 
								ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM0402.equals(securityMessageProfile);
						boolean integrita = integritaX509 || integritaKid;
												
						if(rest) {
							boolean securityHeaderObbligatorio = true;

							if(headerTokenRestIntegrity==null) {
								
								String token = null;
								String prefixMsgDiag = null;
								if(HttpConstants.AUTHORIZATION.equalsIgnoreCase(headerTokenRest)) {
									prefixMsgDiag = DIAGNOSTIC_VALIDATE_TOKEN_ID_AUTH;
								}
								else {
									prefixMsgDiag = DIAGNOSTIC_VALIDATE_TOKEN_INTEGRITY;
								}
								try {
									msgDiag.logPersonalizzato(prefixMsgDiag+tipoDiagnostico+DIAGNOSTIC_IN_CORSO);
									
									// Nel caso di 1 solo header da generare localmente, l'integrity, e l'authorization prodotto tramite token policy, l'integrity è obbligatorio solo se c'è un payload, altrimenti se presente viene validato, altrimenti non da errore.
									if(!sorgenteLocale) {
										securityHeaderObbligatorio = msg.castAsRest().hasContent();
									}
									token = validatoreSintatticoRest.validateSecurityProfile(msg, request, securityMessageProfile, false, headerTokenRest, 
											corniceSicurezza, patternCorniceSicurezza, 
											null, // devo fornirlo solo durante la validazione dell'Audit Token 
											includiRequestDigest, bustaRitornata, 
											erroriValidazione, trustStoreCertificati, trustStoreSsl, securityConfig,
											buildSecurityTokenInRequest, ModIHeaderType.SINGLE, integritaCustom, securityHeaderObbligatorio,
											dynamicMap, datiRichiesta);
									
									if(erroriValidazione.isEmpty()) {
										msgDiag.logPersonalizzato(prefixMsgDiag+tipoDiagnostico+DIAGNOSTIC_COMPLETATA);
									}
									else {
										String errore = buildErrore(erroriValidazione, this.protocolFactory);
										msgDiag.addKeyword(CostantiPdD.KEY_ERRORE_PROCESSAMENTO, errore);
										msgDiag.logPersonalizzato(prefixMsgDiag+tipoDiagnostico+DIAGNOSTIC_FALLITA);
										errorOccurs = true;
									}
								}
								catch(ProtocolException pe) {
									msgDiag.addKeyword(CostantiPdD.KEY_ERRORE_PROCESSAMENTO, pe.getMessage());
									msgDiag.logPersonalizzato(prefixMsgDiag+tipoDiagnostico+DIAGNOSTIC_FALLITA);
									throw pe;
								}
									
									
								if(token!=null) {
									
									if(securityConfig.getAudience()!=null &&
										(request || (securityConfig.isCheckAudience())) 
									){
										String audience = securityConfig.getAudience();
										if(fruizione && !request) {
											try {
												audience = ModIUtilities.getDynamicValue(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO_REST_AUDIENCE, 
														audience, dynamicMap, this.context);
											}catch(Exception e) {
												this.logError(e.getMessage(),e);
												throw e;
											}
										}
										msg.addContextProperty(ModICostanti.MODIPA_OPENSPCOOP2_MSG_CONTEXT_AUDIENCE_CHECK, audience);
									}
									
									if(!request && securityConfig.isCheckAudience() && integritaKid && securityConfig.getTokenClientId()!=null) {
										String audienceClientId = null;
										try {
											audienceClientId = ModIUtilities.getDynamicValue(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO_REST_AUDIENCE+"-OAuth", 
													securityConfig.getTokenClientId(), dynamicMap, this.context);
										}catch(Exception e) {
											this.logError(e.getMessage(),e);
											throw e;
										}
										if(audienceClientId!=null) {
											msg.addContextProperty(ModICostanti.MODIPA_OPENSPCOOP2_MSG_CONTEXT_AUDIENCE_CHECK_OAUTH, audienceClientId);
										}
									}
									
									rawContent = new ModIBustaRawContent(headerTokenRest, token);
								}
							}
							else {
								
								boolean useKIDforIdAUTH = false; // normalmente il token authorization sarà PDND, se cmq è locale lo genero uguale per il kid
								if(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM0401.equals(securityMessageProfile)
										||
										ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM0402.equals(securityMessageProfile)) {
									useKIDforIdAUTH = true;
								}
								
								// Authorization
								String tokenAuthorization = null;
								try {
									msgDiag.logPersonalizzato(DIAGNOSTIC_VALIDATE_TOKEN_ID_AUTH+tipoDiagnostico+DIAGNOSTIC_IN_CORSO);
									
									String securityMessageProfileAuthorization = null;
									if(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM0301.equals(securityMessageProfile) ||
											ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM0401.equals(securityMessageProfile)) {
										securityMessageProfileAuthorization = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM01; 
									}
									else {
										securityMessageProfileAuthorization = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM02; 
									}
									tokenAuthorization = validatoreSintatticoRest.validateSecurityProfile(msg, request, securityMessageProfileAuthorization, useKIDforIdAUTH, headerTokenRest, 
											corniceSicurezza, patternCorniceSicurezza, 
											null, // devo fornirlo solo durante la validazione dell'Audit Token   
											includiRequestDigest, bustaRitornata, 
											erroriValidazione, trustStoreCertificati, trustStoreSsl, securityConfig,
											buildSecurityTokenInRequest, ModIHeaderType.BOTH_AUTH, integritaCustom, securityHeaderObbligatorio,
											dynamicMap, datiRichiesta);
									
									String audAuthorization = null;
									if(tokenAuthorization!=null &&
										securityConfig.getAudience()!=null &&
										(request || (securityConfig.isCheckAudience()))
										){
										audAuthorization = securityConfig.getAudience();
										if(fruizione && !request) {
											try {
												audAuthorization = ModIUtilities.getDynamicValue(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO_REST_AUDIENCE, 
														audAuthorization, dynamicMap, this.context);
											}catch(Exception e) {
												this.logError(e.getMessage(),e);
												throw e;
											}
										}
										msg.addContextProperty(ModICostanti.MODIPA_OPENSPCOOP2_MSG_CONTEXT_AUDIENCE_CHECK, audAuthorization);				
									}
									
									if(erroriValidazione.isEmpty()) {
										msgDiag.logPersonalizzato(DIAGNOSTIC_VALIDATE_TOKEN_ID_AUTH+tipoDiagnostico+DIAGNOSTIC_COMPLETATA);
									}
									else {
										String errore = buildErrore(erroriValidazione, this.protocolFactory);
										msgDiag.addKeyword(CostantiPdD.KEY_ERRORE_PROCESSAMENTO, errore);
										msgDiag.logPersonalizzato(DIAGNOSTIC_VALIDATE_TOKEN_ID_AUTH+tipoDiagnostico+DIAGNOSTIC_FALLITA);
										errorOccurs = true;
									}
								}
								catch(ProtocolException pe) {
									msgDiag.addKeyword(CostantiPdD.KEY_ERRORE_PROCESSAMENTO, pe.getMessage());
									msgDiag.logPersonalizzato(DIAGNOSTIC_VALIDATE_TOKEN_ID_AUTH+tipoDiagnostico+DIAGNOSTIC_FALLITA);
									throw pe;
								}
								
								// Integrity
								String tokenIntegrity = null;
								ModISecurityConfig securityConfigIntegrity = null;
								try {
									msgDiag.logPersonalizzato(DIAGNOSTIC_VALIDATE_TOKEN_INTEGRITY+tipoDiagnostico+DIAGNOSTIC_IN_CORSO);
									
									// !! Nel caso di 2 header, quello integrity è obbligatorio solo se c'è un payload, altrimenti se presente viene validato, altrimenti non da errore.
									boolean securityHeaderIntegrityObbligatorio = msg.castAsRest().hasContent();
									securityConfigIntegrity = new ModISecurityConfig(msg, this.context, this.protocolFactory, this.state, requestInfo, idSoggettoMittente, 
											aspc, asps, sa, rest, fruizione, request,
											false,
											keystoreKidMode,
											patternCorniceSicurezza, schemaCorniceSicurezza);
									tokenIntegrity = validatoreSintatticoRest.validateSecurityProfile(msg, request, securityMessageProfile, useKIDforIdAUTH, headerTokenRestIntegrity, 
											corniceSicurezza, patternCorniceSicurezza, 
											null, // devo fornirlo solo durante la validazione dell'Audit Token 
											includiRequestDigest, bustaRitornata, 
											erroriValidazione, trustStoreCertificati, trustStoreSsl, securityConfigIntegrity,
											buildSecurityTokenInRequest, ModIHeaderType.BOTH_INTEGRITY, integritaCustom, securityHeaderIntegrityObbligatorio,
											null, null); // gia' inizializzato sopra
									
									if(erroriValidazione.isEmpty()) {
										msgDiag.logPersonalizzato(DIAGNOSTIC_VALIDATE_TOKEN_INTEGRITY+tipoDiagnostico+DIAGNOSTIC_COMPLETATA);
									}
									else {
										String errore = buildErrore(erroriValidazione, this.protocolFactory);
										msgDiag.addKeyword(CostantiPdD.KEY_ERRORE_PROCESSAMENTO, errore);
										msgDiag.logPersonalizzato(DIAGNOSTIC_VALIDATE_TOKEN_INTEGRITY+tipoDiagnostico+DIAGNOSTIC_FALLITA);
										errorOccurs = true;
									}
								}
								catch(ProtocolException pe) {
									msgDiag.addKeyword(CostantiPdD.KEY_ERRORE_PROCESSAMENTO, pe.getMessage());
									msgDiag.logPersonalizzato(DIAGNOSTIC_VALIDATE_TOKEN_INTEGRITY+tipoDiagnostico+DIAGNOSTIC_FALLITA);
									throw pe;
								}
								
								if(tokenIntegrity!=null &&
									securityConfigIntegrity.getAudience()!=null &&
									(request || (securityConfigIntegrity.isCheckAudience())) 
									){
									String audIntegrity = securityConfigIntegrity.getAudience();
									if(fruizione && !request) {
										try {
											audIntegrity = ModIUtilities.getDynamicValue(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO_REST_AUDIENCE, 
													audIntegrity, dynamicMap, this.context);
										}catch(Exception e) {
											this.logError(e.getMessage(),e);
											throw e;
										}
									}
									msg.addContextProperty(ModICostanti.MODIPA_OPENSPCOOP2_MSG_CONTEXT_AUDIENCE_INTEGRITY_CHECK, audIntegrity);
									
									if(!request && securityConfig.isCheckAudience() && integritaKid && securityConfig.getTokenClientId()!=null) {
										String audienceClientId = null;
										try {
											audienceClientId = ModIUtilities.getDynamicValue(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO_REST_AUDIENCE+"-OAuth", 
													securityConfig.getTokenClientId(), dynamicMap, this.context);
										}catch(Exception e) {
											this.logError(e.getMessage(),e);
											throw e;
										}
										if(audienceClientId!=null) {
											msg.addContextProperty(ModICostanti.MODIPA_OPENSPCOOP2_MSG_CONTEXT_AUDIENCE_INTEGRITY_CHECK_OAUTH, audienceClientId);
										}
									}
								}
								
								// Finalizzo
								
								if(tokenAuthorization!=null && tokenIntegrity!=null) {
									rawContent = new ModIBustaRawContent(tokenAuthorization, headerTokenRestIntegrity, tokenIntegrity);
								}
								else if(tokenAuthorization!=null) {
									rawContent = new ModIBustaRawContent(headerTokenRest, tokenAuthorization);
								}
								else if(tokenIntegrity!=null) {
									rawContent = new ModIBustaRawContent(headerTokenRestIntegrity, tokenIntegrity);
								}
								
							}
							
						}
						else {

							// soap
							
							String prefixMsgDiag = null;
							if(integrita) {
								prefixMsgDiag = DIAGNOSTIC_VALIDATE_TOKEN_INTEGRITY;
							}
							else {
								prefixMsgDiag = DIAGNOSTIC_VALIDATE_TOKEN_ID_AUTH;
							}
							try {
								msgDiag.logPersonalizzato(prefixMsgDiag+tipoDiagnostico+DIAGNOSTIC_IN_CORSO);
							
								boolean corniceSicurezzaLegacySoap = corniceSicurezza && !processAudit;
								
								SOAPEnvelope token = validatoreSintatticoSoap.validateSecurityProfile(msg, request, securityMessageProfile, corniceSicurezzaLegacySoap, includiRequestDigest, signAttachments, bustaRitornata, 
										erroriValidazione, trustStoreCertificati, securityConfig,
										buildSecurityTokenInRequest,
										dynamicMap, datiRichiesta, requestInfo );
								
								if(token!=null) {
									
									if(securityConfig.getAudience()!=null &&
										(request || (securityConfig.isCheckAudience())) 
										){
										String audience = securityConfig.getAudience();
										if(fruizione && !request) {
											try {
												audience = ModIUtilities.getDynamicValue(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO_SOAP_WSA_TO, 
														audience, dynamicMap, this.context);
											}catch(Exception e) {
												this.logError(e.getMessage(),e);
												throw e;
											}
										}
										msg.addContextProperty(ModICostanti.MODIPA_OPENSPCOOP2_MSG_CONTEXT_AUDIENCE_CHECK, audience);
									}
									
									rawContent = new ModIBustaRawContent(token);
								}

								if(erroriValidazione.isEmpty()) {
									msgDiag.logPersonalizzato(prefixMsgDiag+tipoDiagnostico+DIAGNOSTIC_COMPLETATA);
								}
								else {
									String errore = buildErrore(erroriValidazione, this.protocolFactory);
									msgDiag.addKeyword(CostantiPdD.KEY_ERRORE_PROCESSAMENTO, errore);
									msgDiag.logPersonalizzato(prefixMsgDiag+tipoDiagnostico+DIAGNOSTIC_FALLITA);
									errorOccurs = true;
								}
							}
							catch(ProtocolException pe) {
								msgDiag.addKeyword(CostantiPdD.KEY_ERRORE_PROCESSAMENTO, pe.getMessage());
								msgDiag.logPersonalizzato(prefixMsgDiag+tipoDiagnostico+DIAGNOSTIC_FALLITA);
								throw pe;
							}
							
						}
					
					}
					
					// audit (ID_AUDIT)
					if(!errorOccurs && processAudit) {
						
						bustaRitornata.addProperty(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_AUDIT_PATTERN, 
								ModIPropertiesUtils.convertProfiloAuditToSDKValue(patternCorniceSicurezza));
						bustaRitornata.addProperty(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_AUDIT_SCHEMA, 
								ModIPropertiesUtils.convertSchemaAuditToSDKValue(schemaCorniceSicurezza, this.modiProperties));
						
						if(validatoreSintatticoRest==null) {
							// audit su api soap
							validatoreSintatticoRest = new ModIValidazioneSintatticaRest(this.log, this.state, this.context, this.protocolFactory, requestInfo, this.modiProperties, this.validazioneUtils);
						}
						
						String securityMessageProfileAudit = null;
						if(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM0301.equals(securityMessageProfile)
								||
								ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM0401.equals(securityMessageProfile)) {
							securityMessageProfileAudit = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM01; 
						}
						else {
							securityMessageProfileAudit = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM02; 
						}
												
						boolean useKIDforAudit = false;
						if(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM0401.equals(securityMessageProfile)
								||
								ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM0402.equals(securityMessageProfile)
								||
								!sorgenteLocale) {
							useKIDforAudit = true;
						}
						
						String headerTokenAudit = this.modiProperties.getSecurityTokenHeaderModIAudit();
						
						try {
							msgDiag.logPersonalizzato("validateTokenAudit.richiesta.inCorso");
						
							String tokenAudit = validatoreSintatticoRest.validateSecurityProfile(msg, request, securityMessageProfileAudit, useKIDforAudit, headerTokenAudit, 
									corniceSicurezza, patternCorniceSicurezza, schemaCorniceSicurezza,
									false, bustaRitornata, 
									erroriValidazione, trustStoreCertificati, trustStoreSsl, securityConfig,
									buildSecurityTokenInRequest, ModIHeaderType.SINGLE, integritaCustom, true,
									dynamicMap, datiRichiesta);
							
							if(tokenAudit!=null){
								String audExpected = securityConfig.getCorniceSicurezzaAudience();
								try {
									audExpected = ModIUtilities.getDynamicValue(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_AUDIT_AUDIENCE, 
											audExpected, dynamicMap, this.context);
								}catch(Exception e) {
									this.logError(e.getMessage(),e);
									throw e;
								}
								msg.addContextProperty(ModICostanti.MODIPA_OPENSPCOOP2_MSG_CONTEXT_AUDIENCE_AUDIT_CHECK, audExpected);				
							}
							
							if(rawContent!=null) {
								rawContent.getElement().setTokenAudit(headerTokenAudit, tokenAudit);
							}
							
							if(erroriValidazione.isEmpty()) {
								msgDiag.logPersonalizzato("validateTokenAudit.richiesta.completata");
							}
							else {
								String errore = buildErrore(erroriValidazione, this.protocolFactory);
								msgDiag.addKeyword(CostantiPdD.KEY_ERRORE_PROCESSAMENTO, errore);
								msgDiag.logPersonalizzato("validateTokenAudit.richiesta.fallita");
							}
						}
						catch(ProtocolException pe) {
							msgDiag.addKeyword(CostantiPdD.KEY_ERRORE_PROCESSAMENTO, pe.getMessage());
							msgDiag.logPersonalizzato("validateTokenAudit.richiesta.fallita");
							throw pe;
						}
						
					}
				}

			}
						
		}catch(Exception e) {
			erroreProcessamentoInternalMessage = e.getMessage();
			String msgErrore =  "[ErroreInterno]: "+e.getMessage();
			this.logError(msgErrore,e);
			if(request) {
				ValidazioneSintatticaResult<AbstractModISecurityToken<?>> errorResult = new ValidazioneSintatticaResult<>(null, null, null, 
						bustaRitornata, new ErroreCooperazione(msgErrore, CodiceErroreCooperazione.ERRORE_GENERICO_PROCESSAMENTO_MESSAGGIO), null, null, false);
				errorResult.setErrore_integrationFunctionError(IntegrationFunctionError.INTERNAL_REQUEST_ERROR);
				return errorResult;
			}
			else {
				try {
					erroriProcessamento.add(this.validazioneUtils.newEccezioneProcessamento(CodiceErroreCooperazione.ERRORE_GENERICO_PROCESSAMENTO_MESSAGGIO));
				}catch(Exception eInternal) {
					ValidazioneSintatticaResult<AbstractModISecurityToken<?>> errorResult = new ValidazioneSintatticaResult<>(null, null, null, 
							bustaRitornata, new ErroreCooperazione(msgErrore, CodiceErroreCooperazione.ERRORE_GENERICO_PROCESSAMENTO_MESSAGGIO), null, null, false);
					errorResult.setErrore_integrationFunctionError(IntegrationFunctionError.INTERNAL_RESPONSE_ERROR);
					return errorResult;
				}
			}
		}
		
		if(!erroriValidazione.isEmpty() || !erroriProcessamento.isEmpty()) {
			ValidazioneSintatticaResult<AbstractModISecurityToken<?>> resultError = new ValidazioneSintatticaResult<>(erroriValidazione, erroriProcessamento, null, 
					bustaRitornata, null, null, rawContent, true);
			resultError.setErroreProcessamento_internalMessage(erroreProcessamentoInternalMessage);
			
			if(!erroriValidazione.isEmpty() &&
				this.context!=null) {
				this.context.addObject(Costanti.ERRORE_VALIDAZIONE_PROTOCOLLO, Costanti.ERRORE_TRUE);
			}
			
			return resultError;
		}
		
		basicResult.setBustaRaw(rawContent);
		return basicResult;
	}
	
	private void logError(String msg, Exception e) {
		this.log.error(msg,e);
	}

	static String buildErrore(List<Eccezione> list, IProtocolFactory<?> protocolFactory) throws ProtocolException {
		return buildErrore(list, 0, protocolFactory);
	}
	static String buildErrore(List<Eccezione> list, int offset, IProtocolFactory<?> protocolFactory) throws ProtocolException {
		StringBuilder sb = new StringBuilder();
		if(list!=null && !list.isEmpty()) {
			if(list.size()==1) {
				if(offset>0) {
					throw new ProtocolException("Offset > 0 con lista di dimensione '"+list.size()+"'");
				}
				Eccezione eccezione = list.get(0);
				sb.append("[");
				sb.append(eccezione.getCodiceEccezioneValue(protocolFactory));
				sb.append("] ");
				sb.append(eccezione.getDescrizione(protocolFactory));
			}
			else {
				if(offset>list.size()) {
					throw new ProtocolException("Offset maggiore della dimensione della lista di errori '"+list.size()+"'");
				}
				int size = list.size() - offset;
				sb.append("Riscontrate "+size+" eccezioni.");
				int index = 0;
				for (Eccezione eccezione : list) {
					if(index>=offset) {
						sb.append("\n[");
						sb.append(eccezione.getCodiceEccezioneValue(protocolFactory));
						sb.append("] ");
						sb.append(eccezione.getDescrizione(protocolFactory));
					}
					index++;
				}
			}
		}
		return sb.toString();
	}
}
