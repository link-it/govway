/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it). 
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
import org.openspcoop2.pdd.core.CostantiPdD;
import org.openspcoop2.protocol.basic.validator.ValidazioneSintattica;
import org.openspcoop2.protocol.engine.utils.NamingUtils;
import org.openspcoop2.protocol.modipa.AbstractModISecurityToken;
import org.openspcoop2.protocol.modipa.ModIBustaRawContent;
import org.openspcoop2.protocol.modipa.config.ModIProperties;
import org.openspcoop2.protocol.modipa.constants.ModICostanti;
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
import org.openspcoop2.protocol.sdk.validator.ProprietaValidazioneErrori;
import org.openspcoop2.protocol.sdk.validator.ValidazioneSintatticaResult;
import org.openspcoop2.protocol.sdk.validator.ValidazioneUtils;

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
		
		String erroreProcessamento_internalMessage = null;
		try {
					
			IDServizio idServizio = null;
			if(datiRichiesta!=null && 
					datiRichiesta.getTipoDestinatario()!=null && datiRichiesta.getDestinatario()!=null &&
					datiRichiesta.getTipoServizio()!=null && datiRichiesta.getServizio()!=null &&
					datiRichiesta.getVersioneServizio()!=null) {
				idServizio = IDServizioFactory.getInstance().getIDServizioFromValues(datiRichiesta.getTipoServizio(), datiRichiesta.getServizio(), 
						datiRichiesta.getTipoDestinatario(), datiRichiesta.getDestinatario(), 
						datiRichiesta.getVersioneServizio());
				
				IRegistryReader registryReader = this.getProtocolFactory().getCachedRegistryReader(this.state);
				IConfigIntegrationReader configIntegrationReader = this.getProtocolFactory().getCachedConfigIntegrationReader(this.state);
				
				AccordoServizioParteSpecifica asps = registryReader.getAccordoServizioParteSpecifica(idServizio);
				
				IDAccordo idAccordo = IDAccordoFactory.getInstance().getIDAccordoFromUri(asps.getAccordoServizioParteComune());
				AccordoServizioParteComune aspc = registryReader.getAccordoServizioParteComune(idAccordo);
				boolean rest = org.openspcoop2.core.registry.constants.ServiceBinding.REST.equals(aspc.getServiceBinding());
				
				String nomePortType = asps.getPortType();
				String azione = datiRichiesta.getAzione();
								
				IDServizioApplicativo idSA = null;
				ServizioApplicativo sa = null;
				IDSoggetto idSoggettoMittente = null;
				if(!request && datiRichiesta.getServizioApplicativoFruitore()!=null &&
						!CostantiPdD.SERVIZIO_APPLICATIVO_ANONIMO.equals(datiRichiesta.getServizioApplicativoFruitore()) &&
						datiRichiesta.getTipoMittente()!=null &&
						datiRichiesta.getMittente()!=null) {
					
					idSoggettoMittente = new IDSoggetto(datiRichiesta.getTipoMittente(),datiRichiesta.getMittente());
					
					idSA = new IDServizioApplicativo();
					idSA.setIdSoggettoProprietario(idSoggettoMittente);
					idSA.setNome(datiRichiesta.getServizioApplicativoFruitore());
					
					sa = configIntegrationReader.getServizioApplicativo(idSA);
				}
				
				ModIValidazioneSintatticaRest validatoreSintatticoRest = null;
				ModIValidazioneSintatticaSoap validatoreSintatticoSoap = null;
				if(rest) {
					validatoreSintatticoRest = new ModIValidazioneSintatticaRest(this.log, this.state, this.context, this.modiProperties, this.validazioneUtils);
				}
				else {
					validatoreSintatticoSoap = new ModIValidazioneSintatticaSoap(this.log, this.state, this.context, this.modiProperties, this.validazioneUtils);
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
					
					if(!isFault) {
						if(rest) {
							validatoreSintatticoRest.validateSyncInteractionProfile(msg, request, erroriValidazione);
						}
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
							apiContenenteRisorsa = registryReader.getAccordoServizioParteComune(idApiCorrelata, false);
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
					if(ModICostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_RUOLO_VALUE_RICHIESTA.equals(asyncInteractionRole)) {
						if(request &&  ModICostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_VALUE_PUSH.equals(asyncInteractionType)) {
							// devo cercare l'url di invocazione del servizio fruito correlato.
							
							try {
								replyTo = ModIUtilities.getReplyToFruizione(idServizio.getSoggettoErogatore(), idSoggettoMittente, aspc, nomePortType, azione, 
										registryReader, configIntegrationReader, this.protocolFactory, this.state);
							}catch(Exception e) {
								throw new Exception("Configurazione presente nel registro non corretta: "+e.getMessage(),e);
							}
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
				
				String securityMessageProfile = ModIPropertiesUtils.readPropertySecurityMessageProfile(aspc, nomePortType, azione);
				if(securityMessageProfile!=null && !ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_UNDEFINED.equals(securityMessageProfile)) {
					
					boolean processSecurity = ModIPropertiesUtils.processSecurity(aspc, nomePortType, azione, request, 
							msg, rest, this.modiProperties);
																		
					if(processSecurity) {
					
						bustaRitornata.addProperty(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO, 
								ModIPropertiesUtils.convertProfiloSicurezzaToSDKValue(securityMessageProfile, rest));
						
						boolean fruizione = !request;
							
						String headerTokenRest = null;
						String headerTokenRestIntegrity = null; // nel caso di Authorization insieme a Agid-JWT-Signature
						Boolean multipleHeaderAuthorizationConfig = null;
						if(rest) {
							headerTokenRest = ModIPropertiesUtils.readPropertySecurityMessageHeader(aspc, nomePortType, azione, request);
							if(headerTokenRest.contains(" ")) {
								String [] tmp = headerTokenRest.split(" ");
								if(tmp!=null && tmp.length==2 && tmp[0]!=null && tmp[1]!=null) {
									headerTokenRest=tmp[0];
									headerTokenRestIntegrity=tmp[1];
									multipleHeaderAuthorizationConfig = true;
								}
							}
						}
	
						ModISecurityConfig securityConfig = new ModISecurityConfig(msg, this.protocolFactory, this.state, idSoggettoMittente, 
								aspc, asps, sa, rest, fruizione, request,
								multipleHeaderAuthorizationConfig);
						ModITruststoreConfig trustStoreCertificati = new ModITruststoreConfig(fruizione, idSoggettoMittente, asps, false);
						ModITruststoreConfig trustStoreSsl = null;
						if(securityConfig.isX5u()) {
							trustStoreSsl = new ModITruststoreConfig(fruizione, idSoggettoMittente, asps, true);
						}
						
						boolean corniceSicurezza = ModIPropertiesUtils.isPropertySecurityMessageConCorniceSicurezza(aspc, nomePortType, azione);
						
						boolean includiRequestDigest = ModIPropertiesUtils.isPropertySecurityMessageIncludiRequestDigest(aspc, nomePortType, azione);
						
						boolean signAttachments = false;
						if(!rest) {
							signAttachments = ModIPropertiesUtils.isAttachmentsSignature(aspc, nomePortType, azione, request, msg);
						}
						
						boolean buildSecurityTokenInRequest = true;
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
					
						// FIX: va inizializzato dentro il metodo 'validateSecurityProfile' dopo aver identificato l'applicativo
//						boolean bufferMessage_readOnly = this.modiProperties.isReadByPathBufferEnabled();
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
//						}
						Map<String, Object> dynamicMap = new HashMap<String, Object>();
						
						if(rest) {
							boolean headerDuplicati = true;
							boolean securityHeaderObbligatorio = true;
							
							if(headerTokenRestIntegrity==null) {
								
								String token = validatoreSintatticoRest.validateSecurityProfile(msg, request, securityMessageProfile, headerTokenRest, corniceSicurezza, includiRequestDigest, bustaRitornata, 
										erroriValidazione, trustStoreCertificati, trustStoreSsl, securityConfig,
										buildSecurityTokenInRequest, !headerDuplicati, securityHeaderObbligatorio,
										dynamicMap, datiRichiesta);
								
								if(token!=null) {
									
									if(securityConfig.getAudience()!=null) {
										if(request || (securityConfig.isCheckAudience())) {
											String audience = securityConfig.getAudience();
											if(fruizione && !request) {
												try {
													audience = ModIUtilities.getDynamicValue(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO_REST_AUDIENCE, 
															audience, dynamicMap, this.context);
												}catch(Exception e) {
													this.log.error(e.getMessage(),e);
													throw e;
												}
											}
											msg.addContextProperty(ModICostanti.MODIPA_OPENSPCOOP2_MSG_CONTEXT_AUDIENCE_CHECK, audience);
										}
									}
									
									rawContent = new ModIBustaRawContent(headerTokenRest, token);
								}
							}
							else {
								
								// Authorization
								String securityMessageProfileAuthorization = null;
								if(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM0301.equals(securityMessageProfile)) {
									securityMessageProfileAuthorization = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM01; 
								}
								else {
									securityMessageProfileAuthorization = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM02; 
								}
								String tokenAuthorization = validatoreSintatticoRest.validateSecurityProfile(msg, request, securityMessageProfileAuthorization, headerTokenRest, corniceSicurezza, includiRequestDigest, bustaRitornata, 
										erroriValidazione, trustStoreCertificati, trustStoreSsl, securityConfig,
										buildSecurityTokenInRequest, headerDuplicati, securityHeaderObbligatorio,
										dynamicMap, datiRichiesta);
								
								String audAuthorization = null;
								if(tokenAuthorization!=null) {
									
									if(securityConfig.getAudience()!=null) {
										if(request || (securityConfig.isCheckAudience())) {
											audAuthorization = securityConfig.getAudience();
											if(fruizione && !request) {
												try {
													audAuthorization = ModIUtilities.getDynamicValue(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO_REST_AUDIENCE, 
															audAuthorization, dynamicMap, this.context);
												}catch(Exception e) {
													this.log.error(e.getMessage(),e);
													throw e;
												}
											}
											msg.addContextProperty(ModICostanti.MODIPA_OPENSPCOOP2_MSG_CONTEXT_AUDIENCE_CHECK, audAuthorization);
										}
									}
									
								}
								
								// Integrity
								// !! Nel caso di 2 header, quello integrity è obbligatorio solo se c'è un payload, altrimenti se presente viene validato, altrimenti non da errore.
								boolean securityHeaderIntegrityObbligatorio = msg.castAsRest().hasContent();
								ModISecurityConfig securityConfigIntegrity = new ModISecurityConfig(msg, this.protocolFactory, this.state, idSoggettoMittente, 
										aspc, asps, sa, rest, fruizione, request,
										false);
								String tokenIntegrity = validatoreSintatticoRest.validateSecurityProfile(msg, request, securityMessageProfile, headerTokenRestIntegrity, corniceSicurezza, includiRequestDigest, bustaRitornata, 
										erroriValidazione, trustStoreCertificati, trustStoreSsl, securityConfigIntegrity,
										buildSecurityTokenInRequest, headerDuplicati, securityHeaderIntegrityObbligatorio,
										null, null); // gia' inizializzato sopra
								
								if(tokenIntegrity!=null) {
									
									if(securityConfigIntegrity.getAudience()!=null) {
										if(request || (securityConfigIntegrity.isCheckAudience())) {
											String audIntegrity = securityConfigIntegrity.getAudience();
											audIntegrity = securityConfigIntegrity.getAudience();
											if(fruizione && !request) {
												try {
													audIntegrity = ModIUtilities.getDynamicValue(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO_REST_AUDIENCE, 
															audIntegrity, dynamicMap, this.context);
												}catch(Exception e) {
													this.log.error(e.getMessage(),e);
													throw e;
												}
											}
											msg.addContextProperty(ModICostanti.MODIPA_OPENSPCOOP2_MSG_CONTEXT_AUDIENCE_INTEGRITY_CHECK, audIntegrity);
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

							SOAPEnvelope token = validatoreSintatticoSoap.validateSecurityProfile(msg, request, securityMessageProfile, corniceSicurezza, includiRequestDigest, signAttachments, bustaRitornata, 
									erroriValidazione, trustStoreCertificati, securityConfig,
									buildSecurityTokenInRequest,
									dynamicMap, datiRichiesta );
							
							if(token!=null) {
								
								if(securityConfig.getAudience()!=null) {
									if(request || (securityConfig.isCheckAudience())) {
										String audience = securityConfig.getAudience();
										if(fruizione && !request) {
											try {
												audience = ModIUtilities.getDynamicValue(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO_SOAP_WSA_TO, 
														audience, dynamicMap, this.context);
											}catch(Exception e) {
												this.log.error(e.getMessage(),e);
												throw e;
											}
										}
										msg.addContextProperty(ModICostanti.MODIPA_OPENSPCOOP2_MSG_CONTEXT_AUDIENCE_CHECK, audience);
									}
								}
								
								rawContent = new ModIBustaRawContent(token);
							}
							
						}
						
					}
				}
			}
						
		}catch(Exception e) {
			erroreProcessamento_internalMessage = e.getMessage();
			String msgErrore =  "[ErroreInterno]: "+e.getMessage();
			this.log.error(msgErrore,e);
			if(request) {
				ValidazioneSintatticaResult<AbstractModISecurityToken<?>> errorResult = new ValidazioneSintatticaResult<AbstractModISecurityToken<?>>(null, null, null, 
						bustaRitornata, new ErroreCooperazione(msgErrore, CodiceErroreCooperazione.ERRORE_GENERICO_PROCESSAMENTO_MESSAGGIO), null, null, false);
				errorResult.setErrore_integrationFunctionError(IntegrationFunctionError.INTERNAL_REQUEST_ERROR);
				return errorResult;
			}
			else {
				try {
					erroriProcessamento.add(this.validazioneUtils.newEccezioneProcessamento(CodiceErroreCooperazione.ERRORE_GENERICO_PROCESSAMENTO_MESSAGGIO));
				}catch(Exception eInternal) {
					ValidazioneSintatticaResult<AbstractModISecurityToken<?>> errorResult = new ValidazioneSintatticaResult<AbstractModISecurityToken<?>>(null, null, null, 
							bustaRitornata, new ErroreCooperazione(msgErrore, CodiceErroreCooperazione.ERRORE_GENERICO_PROCESSAMENTO_MESSAGGIO), null, null, false);
					errorResult.setErrore_integrationFunctionError(IntegrationFunctionError.INTERNAL_RESPONSE_ERROR);
					return errorResult;
				}
			}
		}
		
		if(erroriValidazione.size()>0 || erroriProcessamento.size()>0) {
			ValidazioneSintatticaResult<AbstractModISecurityToken<?>> resultError = new ValidazioneSintatticaResult<AbstractModISecurityToken<?>>(erroriValidazione, erroriProcessamento, null, 
					bustaRitornata, null, null, rawContent, true);
			resultError.setErroreProcessamento_internalMessage(erroreProcessamento_internalMessage);
			
			if(erroriValidazione.size()>0) {
				if(this.context!=null) {
					this.context.addObject(Costanti.ERRORE_VALIDAZIONE_PROTOCOLLO, Costanti.ERRORE_TRUE);
				}
			}
			
			return resultError;
		}
		
		basicResult.setBustaRaw(rawContent);
		return basicResult;
	}
	
	
	
	
	
	
	
}
