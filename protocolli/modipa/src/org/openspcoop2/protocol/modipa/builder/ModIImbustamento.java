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

package org.openspcoop2.protocol.modipa.builder;

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
import org.openspcoop2.message.constants.MessageRole;
import org.openspcoop2.pdd.core.CostantiPdD;
import org.openspcoop2.protocol.engine.utils.NamingUtils;
import org.openspcoop2.protocol.modipa.ModIBustaRawContent;
import org.openspcoop2.protocol.modipa.config.ModIProperties;
import org.openspcoop2.protocol.modipa.constants.ModICostanti;
import org.openspcoop2.protocol.modipa.utils.ModIKeystoreConfig;
import org.openspcoop2.protocol.modipa.utils.ModIPropertiesUtils;
import org.openspcoop2.protocol.modipa.utils.ModISecurityConfig;
import org.openspcoop2.protocol.modipa.utils.ModIUtilities;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.Context;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.ProtocolMessage;
import org.openspcoop2.protocol.sdk.builder.ProprietaManifestAttachments;
import org.openspcoop2.protocol.sdk.constants.RuoloMessaggio;
import org.openspcoop2.protocol.sdk.registry.IConfigIntegrationReader;
import org.openspcoop2.protocol.sdk.registry.IRegistryReader;
import org.openspcoop2.protocol.sdk.state.IState;
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
	
	public ProtocolMessage buildMessage(OpenSPCoop2Message msg, Context context, Busta busta, Busta bustaRichiesta,
			RuoloMessaggio ruoloMessaggio,
			ProprietaManifestAttachments proprietaManifestAttachments,
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
			if(busta.getServizioApplicativoFruitore()!=null && 
					!CostantiPdD.SERVIZIO_APPLICATIVO_ANONIMO.equals(busta.getServizioApplicativoFruitore())) {
				idSA = new IDServizioApplicativo();
				idSA.setIdSoggettoProprietario(idSoggettoProprietarioSA);
				idSA.setNome(busta.getServizioApplicativoFruitore());
				sa = configIntegrationReader.getServizioApplicativo(idSA);
			}
			
			String azione = busta.getAzione();
			String nomePortType = asps.getPortType();
			
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
						
						try {
							replyTo = ModIUtilities.getReplyToErogazione(idSoggettoMittente, aspc, nomePortType, azione, 
									registryReader, configIntegrationReader, protocolFactory, state);
						}catch(Exception e) {
							throw new Exception("Configurazione presente nel registro non corretta: "+e.getMessage(),e);
						}
					}
					
					apiContenenteRisorsa = aspc;
					
				}
				else {
					
					if(ModICostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_VALUE_PUSH.equals(asyncInteractionType)) {
						String asyncInteractionRequestApi = ModIPropertiesUtils.readPropertyAsyncInteractionRequestApi(aspc, nomePortType, azione);
						IDAccordo idApiCorrelata = IDAccordoFactory.getInstance().getIDAccordoFromUri(asyncInteractionRequestApi);
						String labelApi = NamingUtils.getLabelAccordoServizioParteComune(idApiCorrelata);
						busta.addProperty(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_INTERAZIONE_ASINCRONA_API_RICHIESTA_CORRELATA, labelApi);
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
			String securityMessageProfile = ModIPropertiesUtils.readPropertySecurityMessageProfile(aspc, nomePortType, azione);
			if(securityMessageProfile!=null && !ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_UNDEFINED.equals(securityMessageProfile)) {
				
				// check config
				boolean isRichiesta = MessageRole.REQUEST.equals(messageRole);
				boolean addSecurity = ModIPropertiesUtils.processSecurity(aspc, nomePortType, azione, isRichiesta, 
						msg, rest, this.modiProperties);
												
				if(addSecurity) {
				
					busta.addProperty(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO,
							ModIPropertiesUtils.convertProfiloSicurezzaToSDKValue(securityMessageProfile, rest));
					
					boolean fruizione = MessageRole.REQUEST.equals(messageRole);
					
					String headerTokenRest = null;
					if(rest) {
						headerTokenRest = ModIPropertiesUtils.readPropertySecurityMessageHeader(aspc, nomePortType, azione);
					}
					
					boolean corniceSicurezza = ModIPropertiesUtils.isPropertySecurityMessageConCorniceSicurezza(aspc, nomePortType, azione);
					
					boolean includiRequestDigest = ModIPropertiesUtils.isPropertySecurityMessageIncludiRequestDigest(aspc, nomePortType, azione);
					
					if(MessageRole.REQUEST.equals(messageRole)) {
						if(sa==null) {
							ProtocolException pe = new ProtocolException("Il profilo di sicurezza richiesto '"+securityMessageProfile+"' richiede l'identificazione di un applicativo");
							pe.setInteroperabilityError(true);
							throw pe;
						}
					}
					
					ModIKeystoreConfig keystoreConfig = null;
					ModISecurityConfig securityConfig = new ModISecurityConfig(msg, idSoggettoMittente, asps, sa, 
							rest, fruizione, MessageRole.REQUEST.equals(messageRole), corniceSicurezza,
							busta, bustaRichiesta);
					
					if(MessageRole.REQUEST.equals(messageRole)) {
					
						keystoreConfig = new ModIKeystoreConfig(sa, securityMessageProfile);
						
					}
					else {
						
						keystoreConfig = new ModIKeystoreConfig(fruizione, idSoggettoMittente, asps, securityMessageProfile);
						
					}
					
					if(rest) {
						String token = imbustamentoRest.addToken(msg, context, keystoreConfig, securityConfig, busta, securityMessageProfile, headerTokenRest, corniceSicurezza, ruoloMessaggio, includiRequestDigest);
						protocolMessage.setBustaRawContent(new ModIBustaRawContent(token));
					}
					else {
						SOAPEnvelope env = imbustamentoSoap.addSecurity(msg, context, keystoreConfig, securityConfig, busta, securityMessageProfile, corniceSicurezza, ruoloMessaggio, includiRequestDigest);
						protocolMessage.setBustaRawContent(new ModIBustaRawContent(env));
					}
					
				}

			}
			
			protocolMessage.setMessage(msg);
			return protocolMessage;
		
		}
		catch(ProtocolException pe) {
			if(pe.isInteroperabilityError()) {
				if(context!=null) {
					context.addObject(Costanti.ERRORE_VALIDAZIONE_PROTOCOLLO, Costanti.ERRORE_TRUE);
				}
			}
			throw pe;
		}
		catch(Exception e){
			throw new ProtocolException(e.getMessage(),e);
		}
		
	}
	
}
