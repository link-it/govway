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

package org.openspcoop2.protocol.modipa.builder;

import java.util.List;
import java.util.Map;

import jakarta.xml.soap.SOAPEnvelope;

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
import org.openspcoop2.message.constants.MessageRole;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.core.CostantiPdD;
import org.openspcoop2.pdd.core.dynamic.DynamicUtils;
import org.openspcoop2.pdd.logger.MsgDiagnosticiProperties;
import org.openspcoop2.pdd.logger.MsgDiagnostico;
import org.openspcoop2.protocol.engine.utils.NamingUtils;
import org.openspcoop2.protocol.modipa.ModIBustaRawContent;
import org.openspcoop2.protocol.modipa.config.ModIProperties;
import org.openspcoop2.protocol.modipa.constants.ModICostanti;
import org.openspcoop2.protocol.modipa.constants.ModIHeaderType;
import org.openspcoop2.protocol.modipa.utils.ModIKeystoreConfig;
import org.openspcoop2.protocol.modipa.utils.ModIPropertiesUtils;
import org.openspcoop2.protocol.modipa.utils.ModISecurityConfig;
import org.openspcoop2.protocol.modipa.utils.ModIUtilities;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.Context;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.ProtocolMessage;
import org.openspcoop2.protocol.sdk.constants.RuoloMessaggio;
import org.openspcoop2.protocol.sdk.registry.IConfigIntegrationReader;
import org.openspcoop2.protocol.sdk.registry.IRegistryReader;
import org.openspcoop2.protocol.sdk.state.IState;
import org.openspcoop2.protocol.sdk.state.RequestInfo;
import org.openspcoop2.utils.MapKey;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.id.UniqueIdentifierManager;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.slf4j.Logger;

/**
 * ModIImbustamento
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ModIImbustamento {

	private Logger log;
	private ModIProperties modiProperties;
	public ModIImbustamento(Logger log) throws ProtocolException {
		this.log = log;
		this.modiProperties = ModIProperties.getInstance();
	}
	
	private static final String DIAGNOSTIC_ADD_TOKEN_ID_AUTH = "addTokenIdAuth";
	private static final String DIAGNOSTIC_ADD_TOKEN_INTEGRITY = "addTokenIntegrity";
	private static final String DIAGNOSTIC_IN_CORSO = "inCorso";
	private static final String DIAGNOSTIC_COMPLETATA = "completata";
	private static final String DIAGNOSTIC_FALLITA = "fallita";
	
	private static String getReplyToErogazione(IDSoggetto idSoggettoMittente, AccordoServizioParteComune aspc, String nomePortType, String azione,
			IRegistryReader registryReader, IConfigIntegrationReader configIntegrationReader, 
			IProtocolFactory<?> protocolFactory, IState state,
			RequestInfo requestInfo) throws ProtocolException {
		try {
			return ModIUtilities.getReplyToErogazione(idSoggettoMittente, aspc, nomePortType, azione, 
					registryReader, configIntegrationReader, protocolFactory, state, requestInfo);
		}catch(Exception e) {
			throw new ProtocolException("Configurazione presente nel registro non corretta: "+e.getMessage(),e);
		}
	}
	
	private static String newUniqueIdentifier() throws ProtocolException {
		try{
			return UniqueIdentifierManager.newUniqueIdentifier().getAsString();
		}catch(Exception e){
			throw new ProtocolException(e.getMessage());
		}
	}
	
	public ProtocolMessage buildMessage(OpenSPCoop2Message msg, Context context, Busta busta, Busta bustaRichiesta,
			RuoloMessaggio ruoloMessaggio,
			IRegistryReader registryReader, IConfigIntegrationReader configIntegrationReader, 
			IProtocolFactory<?> protocolFactory, IState state) throws ProtocolException{
		
		try{
			ProtocolMessage protocolMessage = new ProtocolMessage();
			
			MessageRole messageRole = MessageRole.REQUEST;
			if(RuoloMessaggio.RISPOSTA.equals(ruoloMessaggio)){
				messageRole = MessageRole.RESPONSE; 
			}
			
			
			// **** Read object from Registry *****
			
			IDSoggetto idSoggettoMittente = new IDSoggetto(busta.getTipoMittente(),busta.getMittente());
			IDSoggetto idSoggettoDestinatario = new IDSoggetto(busta.getTipoDestinatario(),busta.getDestinatario());
			IDSoggetto idSoggettoErogatore = null;
			IDSoggetto idSoggettoProprietarioSA = null;
			if(RuoloMessaggio.RICHIESTA.equals(ruoloMessaggio)){ 
				idSoggettoErogatore = idSoggettoDestinatario;
				idSoggettoProprietarioSA = idSoggettoMittente;
			}
			else{
				idSoggettoErogatore = idSoggettoMittente;
				idSoggettoProprietarioSA = idSoggettoDestinatario;
			}
			IDServizio idServizio = IDServizioFactory.getInstance().getIDServizioFromValues(busta.getTipoServizio(), busta.getServizio(), 
					idSoggettoErogatore, busta.getVersioneServizio());
			AccordoServizioParteSpecifica asps = registryReader.getAccordoServizioParteSpecifica(idServizio);
			
			IDAccordo idAccordo = IDAccordoFactory.getInstance().getIDAccordoFromUri(asps.getAccordoServizioParteComune());
			AccordoServizioParteComune aspc = registryReader.getAccordoServizioParteComune(idAccordo);
			boolean rest = org.openspcoop2.core.registry.constants.ServiceBinding.REST.equals(aspc.getServiceBinding());
			
			IDServizioApplicativo idSA = null;
			ServizioApplicativo sa = null;
			if(MessageRole.REQUEST.equals(messageRole) &&
					busta.getServizioApplicativoFruitore()!=null && 
					!CostantiPdD.SERVIZIO_APPLICATIVO_ANONIMO.equals(busta.getServizioApplicativoFruitore())) {
				idSA = new IDServizioApplicativo();
				idSA.setIdSoggettoProprietario(idSoggettoProprietarioSA);
				idSA.setNome(busta.getServizioApplicativoFruitore());
				sa = configIntegrationReader.getServizioApplicativo(idSA);
			}
			
			String azione = busta.getAzione();
			String nomePortType = asps.getPortType();
			
			RequestInfo requestInfo = null;
			if(context!=null && context.containsKey(org.openspcoop2.core.constants.Costanti.REQUEST_INFO)) {
				requestInfo = (RequestInfo) context.getObject(org.openspcoop2.core.constants.Costanti.REQUEST_INFO);
			}
			
			ModIImbustamentoRest imbustamentoRest = null;
			ModIImbustamentoSoap imbustamentoSoap = null;
			if(rest) {
				imbustamentoRest = new ModIImbustamentoRest(this.log);
			}
			else {
				imbustamentoSoap = new ModIImbustamentoSoap(this.log);
			}
			
			
			
			/* *** PROFILO INTERAZIONE *** */
			
			String interactionProfile = ModIPropertiesUtils.readPropertyInteractionProfile(aspc, nomePortType, azione);
			busta.addProperty(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_INTERAZIONE, interactionProfile);
			
			if(ModICostanti.MODIPA_PROFILO_INTERAZIONE_VALUE_BLOCCANTE.equals(interactionProfile)) {
			
				if(rest) {
					imbustamentoRest.addSyncInteractionProfile(msg, ruoloMessaggio);
				}
				
			}
			else if(ModICostanti.MODIPA_PROFILO_INTERAZIONE_VALUE_NON_BLOCCANTE.equals(interactionProfile)) {
				
				String asyncInteractionType = ModIPropertiesUtils.readPropertyAsyncInteractionProfile(aspc, nomePortType, azione);
				busta.addProperty(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_INTERAZIONE_ASINCRONA_TIPO, asyncInteractionType);
				
				String asyncInteractionRole = ModIPropertiesUtils.readPropertyAsyncInteractionRole(aspc, nomePortType, azione);
				busta.addProperty(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_INTERAZIONE_ASINCRONA_RUOLO, asyncInteractionRole);
				
				String replyTo = null;
				
				AccordoServizioParteComune apiContenenteRisorsa = null; 
								
				if(ModICostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_RUOLO_VALUE_RICHIESTA.equals(asyncInteractionRole)) {
				
					if(RuoloMessaggio.RICHIESTA.equals(ruoloMessaggio) && ModICostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_VALUE_PUSH.equals(asyncInteractionType)) {
						// devo cercare l'url di invocazione del servizio erogato correlato.
						
						replyTo = getReplyToErogazione(idSoggettoMittente, aspc, nomePortType, azione,
								registryReader, configIntegrationReader, 
								protocolFactory, state,
								requestInfo);
					}
					
					apiContenenteRisorsa = aspc;
					
				}
				else {
					
					if(ModICostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_VALUE_PUSH.equals(asyncInteractionType)) {
						String asyncInteractionRequestApi = ModIPropertiesUtils.readPropertyAsyncInteractionRequestApi(aspc, nomePortType, azione);
						IDAccordo idApiCorrelata = IDAccordoFactory.getInstance().getIDAccordoFromUri(asyncInteractionRequestApi);
						String labelApi = NamingUtils.getLabelAccordoServizioParteComune(idApiCorrelata);
						busta.addProperty(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_INTERAZIONE_ASINCRONA_API_RICHIESTA_CORRELATA, labelApi);
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
						
						busta.addProperty(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_INTERAZIONE_ASINCRONA_RISORSA_RICHIESTA_CORRELATA, labelResourceCorrelata);
						
					}
					else {
						
						String asyncInteractionRequestService = ModIPropertiesUtils.readPropertyAsyncInteractionRequestService(aspc, nomePortType, azione);
						busta.addProperty(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_INTERAZIONE_ASINCRONA_SERVIZIO_RICHIESTA_CORRELATA, asyncInteractionRequestService);
						
						String asyncInteractionRequestAction = ModIPropertiesUtils.readPropertyAsyncInteractionRequestAction(aspc, nomePortType, azione);
						busta.addProperty(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_INTERAZIONE_ASINCRONA_AZIONE_RICHIESTA_CORRELATA, asyncInteractionRequestAction);
						
					}
					
				}
				
				if(rest) {
					imbustamentoRest.addAsyncInteractionProfile(msg, busta, ruoloMessaggio, 
							asyncInteractionType, asyncInteractionRole,
							replyTo,
							apiContenenteRisorsa, azione);
				}
				else {
					imbustamentoSoap.addAsyncInteractionProfile(msg, busta, ruoloMessaggio, 
							asyncInteractionType, asyncInteractionRole,
							replyTo,
							apiContenenteRisorsa, azione);
				}
				
			}
			
			
			/* *** SICUREZZA CANALE *** */
			
			String securityChannelProfile = ModIPropertiesUtils.readPropertySecurityChannelProfile(aspc);
			busta.addProperty(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_CANALE, securityChannelProfile.toUpperCase());
			
			
			/* *** SICUREZZA MESSAGGIO *** */
			
			boolean isRichiesta = MessageRole.REQUEST.equals(messageRole);
			
			boolean filterPDND = true;
			String securityMessageProfileNonFiltratoPDND = ModIPropertiesUtils.readPropertySecurityMessageProfile(aspc, nomePortType, azione, !filterPDND);
			
			boolean existsSecurityFlusso =  false;
			if(securityMessageProfileNonFiltratoPDND!=null && !ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_UNDEFINED.equals(securityMessageProfileNonFiltratoPDND)) {
				existsSecurityFlusso = ModIPropertiesUtils.processSecurity(aspc, nomePortType, azione, isRichiesta, 
						msg, rest, this.modiProperties);
			}
			
			if(existsSecurityFlusso) {
				
				busta.addProperty(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO,
						ModIPropertiesUtils.convertProfiloSicurezzaToSDKValue(securityMessageProfileNonFiltratoPDND, rest));
				
				String sorgenteToken = ModIPropertiesUtils.readPropertySecurityMessageSorgenteToken(aspc, nomePortType, azione, true);
				busta.addProperty(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO_SORGENTE_TOKEN,
						ModIPropertiesUtils.convertProfiloSicurezzaSorgenteTokenToSDKValue(sorgenteToken));
				boolean sorgenteLocale = true;
				if(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SORGENTE_TOKEN_IDAUTH_VALUE_PDND.equals(sorgenteToken) ||
						ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SORGENTE_TOKEN_IDAUTH_VALUE_OAUTH.equals(sorgenteToken)) {
					sorgenteLocale = false;
				}
				
				String securityMessageProfile = ModIPropertiesUtils.readPropertySecurityMessageProfile(aspc, nomePortType, azione, filterPDND);
				
				boolean sicurezzaRidefinitaOperazione = ModIPropertiesUtils.readSicurezzaMessaggioRidefinitaOperazioneEngine(aspc, nomePortType, azione);
								
				MsgDiagnostico msgDiag = null;
				TipoPdD tipoPdD = RuoloMessaggio.RICHIESTA.equals(ruoloMessaggio) ? TipoPdD.DELEGATA : TipoPdD.APPLICATIVA;
				IDSoggetto idSoggetto = TipoPdD.DELEGATA.equals(tipoPdD) ? idSoggettoMittente : idSoggettoDestinatario;
				if(idSoggetto==null || idSoggetto.getTipo()==null || idSoggetto.getNome()==null) {
					idSoggetto = OpenSPCoop2Properties.getInstance().getIdentitaPortaDefault(protocolFactory.getProtocol(), requestInfo);
				}
				else {
					idSoggetto.setCodicePorta(registryReader.getDominio(idSoggetto));
				}
				msgDiag = MsgDiagnostico.newInstance(TipoPdD.DELEGATA, 
						idSoggetto,
						"ModI", requestInfo!=null && requestInfo.getProtocolContext()!=null ? requestInfo.getProtocolContext().getInterfaceName() : null,
						requestInfo,
						state);
				if(TipoPdD.DELEGATA.equals(tipoPdD)){
					msgDiag.setPrefixMsgPersonalizzati(MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_CONTENUTI_APPLICATIVI);
				}
				else {
					msgDiag.setPrefixMsgPersonalizzati(MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_BUSTE);
				}
				msgDiag.setPddContext(context, protocolFactory);
				String tipoDiagnostico = RuoloMessaggio.RICHIESTA.equals(ruoloMessaggio) ? ".richiesta." : ".risposta.";
				
				// check config
				
				boolean addSecurity = false;
				boolean securityMessageProfilePrevedeTokenLocale = securityMessageProfile!=null && !ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_UNDEFINED.equals(securityMessageProfile);
				if(securityMessageProfilePrevedeTokenLocale) {
					addSecurity = existsSecurityFlusso;
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
				
				boolean fruizione = isRichiesta;
				
				boolean addAudit = fruizione && corniceSicurezza && 
						patternCorniceSicurezza!=null && !ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_PATTERN_VALUE_OLD.equals(patternCorniceSicurezza);
								
				boolean keystoreKidMode = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM0401.equals(securityMessageProfile)
						||
						ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM0402.equals(securityMessageProfile)
						||
						!sorgenteLocale;
				
				Map<String, Object> dynamicMap = null;
				
				ModISecurityConfig securityConfig = null;
				ModISecurityConfig securityConfigAudit = null;
				ModIKeystoreConfig keystoreConfig = null;
				boolean keystoreDefinitoInFruizione = false;
				
				String headerTokenRest = null;
				String headerTokenRestIntegrity = null; // nel caso di Authorization insieme a Agid-JWT-Signature
				boolean integritaCustom = false;
				
				if(addSecurity || addAudit) {
				
					// dynamicMap
					boolean bufferMessageReadOnly = this.modiProperties.isReadByPathBufferEnabled();
					Map<String, Object> dynamicMapRequest = null;
					if(RuoloMessaggio.RISPOSTA.equals(ruoloMessaggio)) {
						dynamicMapRequest = ModIUtilities.removeDynamicMapRequest(context);
					}
					if(dynamicMapRequest!=null) {
						dynamicMap = DynamicUtils.buildDynamicMapResponse(msg, context, busta, this.log, bufferMessageReadOnly, dynamicMapRequest);
					}
					else {
						dynamicMap = DynamicUtils.buildDynamicMap(msg, context, busta, this.log, bufferMessageReadOnly);
						ModIUtilities.saveDynamicMapRequest(context, dynamicMap);
					}
					
					// header
					Boolean multipleHeaderAuthorizationConfig = null;
					if(rest) {
						headerTokenRest = ModIPropertiesUtils.readPropertySecurityMessageHeader(aspc, nomePortType, azione, isRichiesta, filterPDND);
						if(headerTokenRest.contains(" ")) {
							String [] tmp = headerTokenRest.split(" ");
							if(tmp!=null && tmp.length==2 && tmp[0]!=null && tmp[1]!=null) {
								headerTokenRest=tmp[0];
								headerTokenRestIntegrity=tmp[1];
								multipleHeaderAuthorizationConfig = true;
							}
						}
						integritaCustom = ModIPropertiesUtils.isPropertySecurityMessageHeaderCustom(aspc, nomePortType, azione, isRichiesta);
						if(integritaCustom) {
							String hdrCustom = headerTokenRest;
							if(multipleHeaderAuthorizationConfig!=null && multipleHeaderAuthorizationConfig && headerTokenRestIntegrity!=null) {
								hdrCustom = headerTokenRestIntegrity;
							}
							busta.addProperty(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO_CUSTOM_HEADER,hdrCustom);
						}
					}
					
					// modalita Keystore
					if(isRichiesta) {
						
						keystoreDefinitoInFruizione = ModIKeystoreConfig.isKeystoreDefinitoInFruizione(idSoggettoMittente, asps);
						
						if((!keystoreDefinitoInFruizione) && sa==null) {
							ProtocolException pe = new ProtocolException("Il profilo di sicurezza richiesto '"+securityMessageProfile+"' richiede l'identificazione di un applicativo");
							pe.setInteroperabilityError(true);
							throw pe;
						}
					}
					
					// security config
					
					if(addAudit) {
						securityConfigAudit = new ModISecurityConfig(msg, context, protocolFactory, state, requestInfo, 
								idSoggettoMittente, asps, sa, 
								rest, fruizione, isRichiesta, 
								patternCorniceSicurezza, schemaCorniceSicurezza,
								busta, bustaRichiesta, 
								multipleHeaderAuthorizationConfig,
								keystoreDefinitoInFruizione, keystoreKidMode,
								addSecurity, addAudit);
					}
					
					boolean keystoreKidModeSecurityToken = keystoreKidMode;
					if(rest && headerTokenRestIntegrity==null) {
						// un solo header
						if(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM0301.equals(securityMessageProfile)
								||
								ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM0302.equals(securityMessageProfile)) {
							keystoreKidModeSecurityToken = false;
						}
					}
					
					securityConfig = new ModISecurityConfig(msg, context, protocolFactory, state, requestInfo, 
							idSoggettoMittente, asps, sa, 
							rest, fruizione, isRichiesta, 
							patternCorniceSicurezza, schemaCorniceSicurezza,
							busta, bustaRichiesta, 
							multipleHeaderAuthorizationConfig,
							keystoreDefinitoInFruizione, keystoreKidModeSecurityToken,
							addSecurity, addAudit);
					
					// keystore
					
					if(isRichiesta) {
					
						if(keystoreDefinitoInFruizione) {
							keystoreConfig = new ModIKeystoreConfig(fruizione, idSoggettoMittente, asps, securityMessageProfile);
						}
						else {
							keystoreConfig = new ModIKeystoreConfig(sa, securityMessageProfile);
						}
						
					}
					else {
						
						keystoreConfig = new ModIKeystoreConfig(fruizione, idSoggettoMittente, asps, securityMessageProfile);
						
					}
				}
								
				String purposeId = null;
				if(addSecurity || addAudit) {
					purposeId =  ModIPropertiesUtils.readPurposeId(configIntegrationReader, asps, idSoggettoMittente, azione);
				}
				
				long now = DateManager.getTimeMillis();
				
				
				// security (ID_AUTH e INTEGRITY)
				if(addSecurity) {
																		
					boolean includiRequestDigest = ModIPropertiesUtils.isPropertySecurityMessageIncludiRequestDigest(aspc, nomePortType, azione);
					
					boolean signAttachments = false;
					if(!rest) {
						signAttachments = ModIPropertiesUtils.isAttachmentsSignature(aspc, nomePortType, azione, isRichiesta, msg);
					}
						
					if(rest) {
						
						// Integrity verrà generato solo se:
						// Nel caso di 2 header da generare localmente, quello integrity viene prodotto solo se c'è un payload o uno degli header indicati da firmare.
						// Nel caso di 1 solo header da generare localmente, l'integrity, e l'authorization prodotto tramite token policy lo stesso verrà generato solo se c'è un payload o uno degli header indicati da firmare.
						boolean addIntegrity = false;
						boolean signedHeaders = false;
						if(securityConfig.getHttpHeaders()!=null && !securityConfig.getHttpHeaders().isEmpty()) {
							Map<String, List<String>> mapForceTransportHeaders = msg.getForceTransportHeaders();
							for (String httpHeader : securityConfig.getHttpHeaders()) {
								if(!httpHeader.equalsIgnoreCase(HttpConstants.DIGEST)) {
									List<String> values = ModIImbustamentoRest.getHeaderValues(ruoloMessaggio, msg, mapForceTransportHeaders, httpHeader);
									if(values!=null && !values.isEmpty()) {
										signedHeaders = true;
										break;
									}
								}
							}
						}
						if(msg.castAsRest().hasContent() || signedHeaders) {
							addIntegrity = true;
						}
						
						
						if(headerTokenRestIntegrity==null) {
							
							// Nel caso di 1 solo header da generare localmente, l'integrity, e l'authorization prodotto tramite token policy lo stesso verrà generato solo se c'è un payload o uno degli header indicati da firmare.
							if(sorgenteLocale || addIntegrity) {
							
								String prefixMsgDiag = null;
								if(HttpConstants.AUTHORIZATION.equalsIgnoreCase(headerTokenRest)) {
									prefixMsgDiag = DIAGNOSTIC_ADD_TOKEN_ID_AUTH;
								}
								else {
									prefixMsgDiag = DIAGNOSTIC_ADD_TOKEN_INTEGRITY;
								}
								try {
									msgDiag.logPersonalizzato(prefixMsgDiag+tipoDiagnostico+DIAGNOSTIC_IN_CORSO);
								
									ModIJWTToken modiToken = imbustamentoRest.addToken(msg, isRichiesta, context, keystoreConfig, securityConfig, busta, 
											securityMessageProfile, false, headerTokenRest, 
											corniceSicurezza, patternCorniceSicurezza, 
											null, // devo fornirlo solo durante la generazione dell'Audit Token 
											ruoloMessaggio, includiRequestDigest,
											now, busta.getID(), ModIHeaderType.SINGLE, integritaCustom,
											dynamicMap, requestInfo,
											purposeId, sicurezzaRidefinitaOperazione);
									protocolMessage.setBustaRawContent(new ModIBustaRawContent(headerTokenRest, modiToken.getToken()));
									
									CostantiPdD.addKeywordInCache(msgDiag, modiToken.isInCache(), context, CostantiPdD.KEY_INFO_IN_CACHE_FUNZIONE_MODI_TOKEN_AUTHORIZATION); 
									msgDiag.logPersonalizzato(prefixMsgDiag+tipoDiagnostico+DIAGNOSTIC_COMPLETATA);
								}
								catch(ProtocolException pe) {
									msgDiag.addKeyword(CostantiPdD.KEY_ERRORE_PROCESSAMENTO, pe.getMessage());
									msgDiag.logPersonalizzato(prefixMsgDiag+tipoDiagnostico+DIAGNOSTIC_FALLITA);
									throw pe;
								}
							}
							
						}
						else {
							
							String jtiAuthorization = busta.getID();
							String jtiIntegrity = busta.getID();
							if(!securityConfig.isMultipleHeaderUseSameJti()) {
								if(securityConfig.isMultipleHeaderUseJtiAuthorizationAsIdMessaggio()) {
									jtiIntegrity = newUniqueIdentifier();
								}else {
									jtiAuthorization = newUniqueIdentifier();
								}
							}
							
							// Authorization
							String securityMessageProfileAuthorization = null;
							if(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM0301.equals(securityMessageProfile)
									||
									ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM0401.equals(securityMessageProfile)) {
								securityMessageProfileAuthorization = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM01; 
							}
							else {
								securityMessageProfileAuthorization = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM02; 
							}
							
							boolean useKIDforIdAUTH = false; // normalmente il token authorization sarà PDND, se cmq è locale lo genero uguale per il kid
							if(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM0401.equals(securityMessageProfile)
									||
									ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM0402.equals(securityMessageProfile)) {
								useKIDforIdAUTH = true;
							}

							ModIJWTToken modiTokenAuthorization = null;
							try {
								msgDiag.logPersonalizzato(DIAGNOSTIC_ADD_TOKEN_ID_AUTH+tipoDiagnostico+DIAGNOSTIC_IN_CORSO);
							
								modiTokenAuthorization = imbustamentoRest.addToken(msg, isRichiesta, context, keystoreConfig, securityConfig, busta, 
										securityMessageProfileAuthorization, useKIDforIdAUTH, headerTokenRest, 
										corniceSicurezza, patternCorniceSicurezza, 
										null, // devo fornirlo solo durante la generazione dell'Audit Token 
										ruoloMessaggio, includiRequestDigest,
										now, jtiAuthorization, ModIHeaderType.BOTH_AUTH, integritaCustom,
										dynamicMap, requestInfo,
										purposeId, sicurezzaRidefinitaOperazione);
								
								CostantiPdD.addKeywordInCache(msgDiag, modiTokenAuthorization.isInCache(), context, CostantiPdD.KEY_INFO_IN_CACHE_FUNZIONE_MODI_TOKEN_AUTHORIZATION); 
								msgDiag.logPersonalizzato(DIAGNOSTIC_ADD_TOKEN_ID_AUTH+tipoDiagnostico+DIAGNOSTIC_COMPLETATA);
							}
							catch(ProtocolException pe) {
								msgDiag.addKeyword(CostantiPdD.KEY_ERRORE_PROCESSAMENTO, pe.getMessage());
								msgDiag.logPersonalizzato(DIAGNOSTIC_ADD_TOKEN_ID_AUTH+tipoDiagnostico+DIAGNOSTIC_FALLITA);
								throw pe;
							}
							
							// Integrity
							// !! Nel caso di 2 header, quello integrity viene prodotto solo se c'è un payload o uno degli header indicati da firmare.
							ModIJWTToken modiTokenIntegrity = null;
							if(addIntegrity) {
								try {
									msgDiag.logPersonalizzato(DIAGNOSTIC_ADD_TOKEN_INTEGRITY+tipoDiagnostico+DIAGNOSTIC_IN_CORSO);
									
									boolean keystoreKidModeIntegrity = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM0401.equals(securityMessageProfile)
											||
											ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM0402.equals(securityMessageProfile);									
									ModISecurityConfig securityConfigIntegrity = new ModISecurityConfig(msg, context, protocolFactory, state, requestInfo,  
											idSoggettoMittente, asps, sa, 
											rest, fruizione, isRichiesta, 
											patternCorniceSicurezza, schemaCorniceSicurezza,
											busta, bustaRichiesta, 
											false,
											keystoreDefinitoInFruizione, keystoreKidModeIntegrity,
											addSecurity, addAudit);
									modiTokenIntegrity = imbustamentoRest.addToken(msg, isRichiesta, context, keystoreConfig, securityConfigIntegrity, busta, 
											securityMessageProfile, false, headerTokenRestIntegrity, 
											corniceSicurezza, patternCorniceSicurezza, 
											null, // devo fornirlo solo durante la generazione dell'Audit Token 
											ruoloMessaggio, includiRequestDigest,
											now, jtiIntegrity, ModIHeaderType.BOTH_INTEGRITY, integritaCustom,
											dynamicMap, requestInfo,
											purposeId, sicurezzaRidefinitaOperazione);
									
									// l'integrity non sarà mai in cache, ma il diagnostico e' condiviso
									CostantiPdD.addKeywordInCache(msgDiag, modiTokenIntegrity.isInCache(), context, CostantiPdD.KEY_INFO_IN_CACHE_FUNZIONE_MODI_TOKEN_INTEGRITY); 
									msgDiag.logPersonalizzato(DIAGNOSTIC_ADD_TOKEN_INTEGRITY+tipoDiagnostico+DIAGNOSTIC_COMPLETATA);
								}
								catch(ProtocolException pe) {
									msgDiag.addKeyword(CostantiPdD.KEY_ERRORE_PROCESSAMENTO, pe.getMessage());
									msgDiag.logPersonalizzato(DIAGNOSTIC_ADD_TOKEN_INTEGRITY+tipoDiagnostico+DIAGNOSTIC_FALLITA);
									throw pe;
								}
							}
							
							if(modiTokenIntegrity!=null) {
								protocolMessage.setBustaRawContent(new ModIBustaRawContent(modiTokenAuthorization.getToken(), headerTokenRestIntegrity, modiTokenIntegrity.getToken()));
							}
							else {
								protocolMessage.setBustaRawContent(new ModIBustaRawContent(headerTokenRest, modiTokenAuthorization.getToken()));
							}
														
						}
					}
					else {
						// soap
						boolean integritaX509 = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM0301.equals(securityMessageProfile) || 
								ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM0302.equals(securityMessageProfile);
						boolean integritaKid = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM0401.equals(securityMessageProfile) || 
								ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM0402.equals(securityMessageProfile);
						boolean integrita = integritaX509 || integritaKid;
						
						String prefixMsgDiag = null;
						MapKey<String> mapKey = null;
						if(integrita) {
							prefixMsgDiag = DIAGNOSTIC_ADD_TOKEN_INTEGRITY;
							mapKey = CostantiPdD.KEY_INFO_IN_CACHE_FUNZIONE_MODI_TOKEN_INTEGRITY;
						}
						else {
							prefixMsgDiag = DIAGNOSTIC_ADD_TOKEN_ID_AUTH;
							mapKey = CostantiPdD.KEY_INFO_IN_CACHE_FUNZIONE_MODI_TOKEN_AUTHORIZATION;
						}
						try {
							msgDiag.logPersonalizzato(prefixMsgDiag+tipoDiagnostico+DIAGNOSTIC_IN_CORSO);
							
							boolean corniceSicurezzaLegacySoap = corniceSicurezza && !addAudit;
							
							SOAPEnvelope env = imbustamentoSoap.addSecurity(msg, isRichiesta, context, keystoreConfig, securityConfig, busta, 
									securityMessageProfile, corniceSicurezzaLegacySoap, ruoloMessaggio, includiRequestDigest, signAttachments,
									dynamicMap, requestInfo);
							protocolMessage.setBustaRawContent(new ModIBustaRawContent(env));
							
							// il token SOAP non è mai in cache
							CostantiPdD.addKeywordInCache(msgDiag, false, context, mapKey);
							msgDiag.logPersonalizzato(prefixMsgDiag+tipoDiagnostico+DIAGNOSTIC_COMPLETATA);
						}
						catch(ProtocolException pe) {
							msgDiag.addKeyword(CostantiPdD.KEY_ERRORE_PROCESSAMENTO, pe.getMessage());
							msgDiag.logPersonalizzato(prefixMsgDiag+tipoDiagnostico+DIAGNOSTIC_FALLITA);
							throw pe;
						}
					}
					
				}
				
				// audit (ID_AUDIT)
				if(addAudit) {
					
					busta.addProperty(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_AUDIT_PATTERN, 
							ModIPropertiesUtils.convertProfiloAuditToSDKValue(patternCorniceSicurezza));
					busta.addProperty(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_AUDIT_SCHEMA, 
							ModIPropertiesUtils.convertSchemaAuditToSDKValue(schemaCorniceSicurezza, this.modiProperties));
					
					if(imbustamentoRest==null) {
						// audit su api soap
						imbustamentoRest = new ModIImbustamentoRest(this.log);
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
					
					String jtiAudit = newUniqueIdentifier();
					
					try {
						msgDiag.logPersonalizzato("addTokenAudit.richiesta.inCorso");
					
						ModIJWTToken modiTokenAudit = imbustamentoRest.addToken(msg, isRichiesta, context, keystoreConfig, securityConfigAudit, busta, 
								securityMessageProfileAudit, useKIDforAudit, headerTokenAudit, 
								corniceSicurezza, patternCorniceSicurezza, schemaCorniceSicurezza,
								ruoloMessaggio, false,
								now, jtiAudit, ModIHeaderType.SINGLE, false,
								dynamicMap, requestInfo,
								purposeId, sicurezzaRidefinitaOperazione);
						if(protocolMessage.getBustaRawContent() instanceof ModIBustaRawContent) {
							ModIBustaRawContent raw = (ModIBustaRawContent) protocolMessage.getBustaRawContent();
							raw.getElement().setTokenAudit(headerTokenAudit, modiTokenAudit.getToken());
						}
						
						CostantiPdD.addKeywordInCache(msgDiag, modiTokenAudit.isInCache(), context, CostantiPdD.KEY_INFO_IN_CACHE_FUNZIONE_MODI_TOKEN_AUDIT); 
						msgDiag.logPersonalizzato("addTokenAudit.richiesta.completata");
					}
					catch(ProtocolException pe) {
						msgDiag.addKeyword(CostantiPdD.KEY_ERRORE_PROCESSAMENTO, pe.getMessage());
						msgDiag.logPersonalizzato("addTokenAudit.richiesta.fallita");
						throw pe;
					}
				}

			}
			
			protocolMessage.setMessage(msg);
			return protocolMessage;
		
		}
		catch(ProtocolException pe) {
			if(pe.isInteroperabilityError() &&
				context!=null) {
				context.addObject(Costanti.ERRORE_VALIDAZIONE_PROTOCOLLO, Costanti.ERRORE_TRUE);
			}
			throw pe;
		}
		catch(Exception e){
			throw new ProtocolException(e.getMessage(),e);
		}
		
	}
	
}
