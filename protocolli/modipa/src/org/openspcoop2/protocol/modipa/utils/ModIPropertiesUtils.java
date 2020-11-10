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

package org.openspcoop2.protocol.modipa.utils;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.Azione;
import org.openspcoop2.core.registry.Fruitore;
import org.openspcoop2.core.registry.Operation;
import org.openspcoop2.core.registry.PortType;
import org.openspcoop2.core.registry.ProtocolProperty;
import org.openspcoop2.core.registry.Resource;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.protocol.modipa.config.ModIProperties;
import org.openspcoop2.protocol.modipa.constants.ModIConsoleCostanti;
import org.openspcoop2.protocol.modipa.constants.ModICostanti;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.properties.ProtocolPropertiesUtils;
import org.openspcoop2.utils.transport.http.ContentTypeUtilities;
import org.openspcoop2.utils.transport.http.HttpConstants;

/**
 * ModIBuilderUtils
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ModIPropertiesUtils {

	public static String readPropertySecurityChannelProfile(AccordoServizioParteComune aspc) throws ProtocolException {
		return ProtocolPropertiesUtils.getRequiredStringValuePropertyRegistry(aspc.getProtocolPropertyList(), 
				ModICostanti.MODIPA_PROFILO_SICUREZZA_CANALE);
	}
	
	
	
	// ACTION
	
	public static String readPropertyInteractionProfile(AccordoServizioParteComune aspc, String nomePortType, String azione) throws ProtocolException {
		return _readProperty(aspc, nomePortType, azione, ModICostanti.MODIPA_PROFILO_INTERAZIONE);
	}
	public static String readPropertyAsyncInteractionProfile(AccordoServizioParteComune aspc, String nomePortType, String azione) throws ProtocolException {
		return _readProperty(aspc, nomePortType, azione, ModICostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA);
	}
	public static String readPropertyAsyncInteractionRole(AccordoServizioParteComune aspc, String nomePortType, String azione) throws ProtocolException {
		return _readProperty(aspc, nomePortType, azione, ModICostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_RUOLO);
	}
	public static String readPropertyAsyncInteractionRequestApi(AccordoServizioParteComune aspc, String nomePortType, String azione) throws ProtocolException {
		return _readProperty(aspc, nomePortType, azione, ModICostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_API_RICHIESTA_CORRELATA);
	}
	public static String readPropertyAsyncInteractionRequestService(AccordoServizioParteComune aspc, String nomePortType, String azione) throws ProtocolException {
		return _readProperty(aspc, nomePortType, azione, ModICostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_SERVIZIO_RICHIESTA_CORRELATA);
	}
	public static String readPropertyAsyncInteractionRequestAction(AccordoServizioParteComune aspc, String nomePortType, String azione) throws ProtocolException {
		return _readProperty(aspc, nomePortType, azione, ModICostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_AZIONE_RICHIESTA_CORRELATA);
	}
	public static String readPropertySecurityMessageProfile(AccordoServizioParteComune aspc, String nomePortType, String azione) throws ProtocolException {
		return _readProperty(aspc, nomePortType, azione, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO);
	}
	public static String readPropertySecurityMessageHeader(AccordoServizioParteComune aspc, String nomePortType, String azione) throws ProtocolException {
		return _readProperty(aspc, nomePortType, azione, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER);
	}
	public static boolean isPropertySecurityMessageConCorniceSicurezza(AccordoServizioParteComune aspc, String nomePortType, String azione) throws ProtocolException {
		String tmp = _readProperty(aspc, nomePortType, azione, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA);
		return tmp!=null ? Boolean.valueOf(tmp) : false;
	}
	public static boolean isPropertySecurityMessageIncludiRequestDigest(AccordoServizioParteComune aspc, String nomePortType, String azione) throws ProtocolException {
		String tmp = _readProperty(aspc, nomePortType, azione, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RISPOSTA_REQUEST_DIGEST);
		return tmp!=null ? Boolean.valueOf(tmp) : false;
	}
	public static String readPropertySecurityMessageApplicabilita(AccordoServizioParteComune aspc, String nomePortType, String azione) throws ProtocolException {
		return _readProperty(aspc, nomePortType, azione, ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE);
	}
	public static String readPropertySecurityMessageApplicabilitaRichiesta(AccordoServizioParteComune aspc, String nomePortType, String azione) throws ProtocolException {
		return _readProperty(aspc, nomePortType, azione, ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_RICHIESTA_MODE);
	}
	public static String readPropertySecurityMessageApplicabilitaRichiestaContentType(AccordoServizioParteComune aspc, String nomePortType, String azione) throws ProtocolException {
		return _readProperty(aspc, nomePortType, azione, ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_RICHIESTA_CONTENT_TYPE_MODE_ID);
	}
	public static String readPropertySecurityMessageApplicabilitaRisposta(AccordoServizioParteComune aspc, String nomePortType, String azione) throws ProtocolException {
		return _readProperty(aspc, nomePortType, azione, ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_RISPOSTA_MODE);
	}
	public static String readPropertySecurityMessageApplicabilitaRispostaContentType(AccordoServizioParteComune aspc, String nomePortType, String azione) throws ProtocolException {
		return _readProperty(aspc, nomePortType, azione, ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_RISPOSTA_CONTENT_TYPE_MODE_ID);
	}
	public static String readPropertySecurityMessageApplicabilitaRispostaReturnCode(AccordoServizioParteComune aspc, String nomePortType, String azione) throws ProtocolException {
		return _readProperty(aspc, nomePortType, azione, ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_RISPOSTA_RETURN_CODE_MODE_ID);
	}
	private static String _readProperty(AccordoServizioParteComune aspc, String nomePortType, String azione,
			String propertyName) throws ProtocolException {
		String interactionProfile = null;
		String asyncInteractionProfile = null;
		String asyncInteractionRole = null;
		String asyncInteractionRequestApi = null;
		String asyncInteractionRequestService = null;
		String asyncInteractionRequestAction = null;
		String securityMessageProfile = ProtocolPropertiesUtils.getOptionalStringValuePropertyRegistry(aspc.getProtocolPropertyList(), 
				ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO);
		String securityMessageProfileHeader = ProtocolPropertiesUtils.getOptionalStringValuePropertyRegistry(aspc.getProtocolPropertyList(), 
				ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER);
		String securityMessageCorniceSicurezza = ProtocolPropertiesUtils.getBooleanValuePropertyRegistry(aspc.getProtocolPropertyList(), 
				ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA, false)+"";
		String securityMessageRequestDigest = ProtocolPropertiesUtils.getBooleanValuePropertyRegistry(aspc.getProtocolPropertyList(), 
				ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RISPOSTA_REQUEST_DIGEST, false)+"";
		String securityMessageApplicabilita = ProtocolPropertiesUtils.getOptionalStringValuePropertyRegistry(aspc.getProtocolPropertyList(), 
				ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE);
		String securityMessageApplicabilitaRichiesta = ProtocolPropertiesUtils.getOptionalStringValuePropertyRegistry(aspc.getProtocolPropertyList(), 
				ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_RICHIESTA_MODE);
		String securityMessageApplicabilitaRichiestaContentType = ProtocolPropertiesUtils.getOptionalStringValuePropertyRegistry(aspc.getProtocolPropertyList(), 
				ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_RICHIESTA_CONTENT_TYPE_MODE_ID);
		String securityMessageApplicabilitaRisposta = ProtocolPropertiesUtils.getOptionalStringValuePropertyRegistry(aspc.getProtocolPropertyList(), 
				ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_RISPOSTA_MODE);
		String securityMessageApplicabilitaRispostaContentType = ProtocolPropertiesUtils.getOptionalStringValuePropertyRegistry(aspc.getProtocolPropertyList(), 
				ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_RISPOSTA_CONTENT_TYPE_MODE_ID);
		String securityMessageApplicabilitaRispostaReturnCode = ProtocolPropertiesUtils.getOptionalStringValuePropertyRegistry(aspc.getProtocolPropertyList(), 
				ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_RISPOSTA_RETURN_CODE_MODE_ID);
		
		if(org.openspcoop2.core.registry.constants.ServiceBinding.REST.equals(aspc.getServiceBinding())) {
			for (Resource resource : aspc.getResourceList()) {
				if(resource.getNome().equals(azione)) {
					interactionProfile = ProtocolPropertiesUtils.getOptionalStringValuePropertyRegistry(resource.getProtocolPropertyList(), 
							ModICostanti.MODIPA_PROFILO_INTERAZIONE);
					if(interactionProfile==null) {
						interactionProfile = ModICostanti.MODIPA_PROFILO_INTERAZIONE_DEFAULT_REST_VALUE;
					}
					if(ModICostanti.MODIPA_PROFILO_INTERAZIONE_VALUE_NON_BLOCCANTE.equals(interactionProfile)) {
						asyncInteractionProfile = ProtocolPropertiesUtils.getRequiredStringValuePropertyRegistry(resource.getProtocolPropertyList(), 
								ModICostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA);
						asyncInteractionRole = ProtocolPropertiesUtils.getRequiredStringValuePropertyRegistry(resource.getProtocolPropertyList(), 
								ModICostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_RUOLO);
						if(ModICostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_VALUE_PUSH.equals(asyncInteractionProfile)) {
							// push
							if(!ModICostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_RUOLO_VALUE_RICHIESTA.equals(asyncInteractionRole)) {
								asyncInteractionRequestApi = ProtocolPropertiesUtils.getRequiredStringValuePropertyRegistry(resource.getProtocolPropertyList(), 
										ModICostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_API_RICHIESTA_CORRELATA);
								asyncInteractionRequestAction = ProtocolPropertiesUtils.getRequiredStringValuePropertyRegistry(resource.getProtocolPropertyList(), 
										ModICostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_AZIONE_RICHIESTA_CORRELATA);
							}
						}
						else {
							// pull
							if(!ModICostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_RUOLO_VALUE_RICHIESTA.equals(asyncInteractionRole)) {
								asyncInteractionRequestAction = ProtocolPropertiesUtils.getRequiredStringValuePropertyRegistry(resource.getProtocolPropertyList(), 
										ModICostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_AZIONE_RICHIESTA_CORRELATA);
							}
						}
						
					}
					String securityMessageProfileMode = ProtocolPropertiesUtils.getOptionalStringValuePropertyRegistry(resource.getProtocolPropertyList(), 
							ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_ACTION_MODE);
					if(securityMessageProfileMode==null) {
						securityMessageProfileMode = ModICostanti.MODIPA_PROFILO_DEFAULT; // default
					}
					if(ModICostanti.MODIPA_PROFILO_RIDEFINISCI.equals(securityMessageProfileMode)) {
						securityMessageProfile = ProtocolPropertiesUtils.getOptionalStringValuePropertyRegistry(resource.getProtocolPropertyList(), 
								ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO);
						securityMessageProfileHeader = ProtocolPropertiesUtils.getOptionalStringValuePropertyRegistry(resource.getProtocolPropertyList(), 
								ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER);
						securityMessageCorniceSicurezza = ProtocolPropertiesUtils.getBooleanValuePropertyRegistry(resource.getProtocolPropertyList(), 
								ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA, false)+"";
						securityMessageRequestDigest = ProtocolPropertiesUtils.getBooleanValuePropertyRegistry(resource.getProtocolPropertyList(), 
								ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RISPOSTA_REQUEST_DIGEST, false)+"";
						securityMessageApplicabilita = ProtocolPropertiesUtils.getOptionalStringValuePropertyRegistry(resource.getProtocolPropertyList(), 
								ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE);
						securityMessageApplicabilitaRichiesta = ProtocolPropertiesUtils.getOptionalStringValuePropertyRegistry(resource.getProtocolPropertyList(), 
								ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_RICHIESTA_MODE);
						securityMessageApplicabilitaRichiestaContentType = ProtocolPropertiesUtils.getOptionalStringValuePropertyRegistry(resource.getProtocolPropertyList(), 
								ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_RICHIESTA_CONTENT_TYPE_MODE_ID);
						securityMessageApplicabilitaRisposta = ProtocolPropertiesUtils.getOptionalStringValuePropertyRegistry(resource.getProtocolPropertyList(), 
								ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_RISPOSTA_MODE);
						securityMessageApplicabilitaRispostaContentType = ProtocolPropertiesUtils.getOptionalStringValuePropertyRegistry(resource.getProtocolPropertyList(), 
								ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_RISPOSTA_CONTENT_TYPE_MODE_ID);
						securityMessageApplicabilitaRispostaReturnCode = ProtocolPropertiesUtils.getOptionalStringValuePropertyRegistry(resource.getProtocolPropertyList(), 
								ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_RISPOSTA_RETURN_CODE_MODE_ID);
					}
					break;
				}
			}
		}
		else {
			if(nomePortType!=null) {
				for (PortType pt : aspc.getPortTypeList()) {
					if(pt.getNome().equals(nomePortType)) {
						for (Operation op : pt.getAzioneList()) {
							if(op.getNome().equals(azione)) {
								interactionProfile = ProtocolPropertiesUtils.getOptionalStringValuePropertyRegistry(op.getProtocolPropertyList(), 
										ModICostanti.MODIPA_PROFILO_INTERAZIONE);
								if(interactionProfile==null) {
									interactionProfile = ModICostanti.MODIPA_PROFILO_INTERAZIONE_DEFAULT_SOAP_VALUE;
								}
								if(ModICostanti.MODIPA_PROFILO_INTERAZIONE_VALUE_NON_BLOCCANTE.equals(interactionProfile)) {
									asyncInteractionProfile = ProtocolPropertiesUtils.getRequiredStringValuePropertyRegistry(op.getProtocolPropertyList(), 
											ModICostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA);
									asyncInteractionRole = ProtocolPropertiesUtils.getRequiredStringValuePropertyRegistry(op.getProtocolPropertyList(), 
											ModICostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_RUOLO);
									if(ModICostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_VALUE_PUSH.equals(asyncInteractionProfile)) {
										// push
										if(!ModICostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_RUOLO_VALUE_RICHIESTA.equals(asyncInteractionRole)) {
											asyncInteractionRequestApi = ProtocolPropertiesUtils.getRequiredStringValuePropertyRegistry(op.getProtocolPropertyList(), 
													ModICostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_API_RICHIESTA_CORRELATA);
											asyncInteractionRequestService = ProtocolPropertiesUtils.getRequiredStringValuePropertyRegistry(op.getProtocolPropertyList(), 
													ModICostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_SERVIZIO_RICHIESTA_CORRELATA);
											asyncInteractionRequestAction = ProtocolPropertiesUtils.getRequiredStringValuePropertyRegistry(op.getProtocolPropertyList(), 
													ModICostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_AZIONE_RICHIESTA_CORRELATA);
										}
									}
									else {
										// pull
										if(!ModICostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_RUOLO_VALUE_RICHIESTA.equals(asyncInteractionRole)) {
											asyncInteractionRequestAction = ProtocolPropertiesUtils.getRequiredStringValuePropertyRegistry(op.getProtocolPropertyList(), 
													ModICostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_AZIONE_RICHIESTA_CORRELATA);
										}
									}
								}
								String securityMessageProfileMode = ProtocolPropertiesUtils.getOptionalStringValuePropertyRegistry(op.getProtocolPropertyList(), 
										ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_ACTION_MODE);
								if(securityMessageProfileMode==null) {
									securityMessageProfileMode = ModICostanti.MODIPA_PROFILO_DEFAULT; // default
								}
								if(ModICostanti.MODIPA_PROFILO_RIDEFINISCI.equals(securityMessageProfileMode)) {
									securityMessageProfile = ProtocolPropertiesUtils.getOptionalStringValuePropertyRegistry(op.getProtocolPropertyList(), 
											ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO);
									securityMessageProfileHeader = ProtocolPropertiesUtils.getOptionalStringValuePropertyRegistry(op.getProtocolPropertyList(), 
											ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER);
									securityMessageCorniceSicurezza = ProtocolPropertiesUtils.getBooleanValuePropertyRegistry(op.getProtocolPropertyList(), 
											ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA, false)+"";
									securityMessageRequestDigest = ProtocolPropertiesUtils.getBooleanValuePropertyRegistry(op.getProtocolPropertyList(), 
											ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RISPOSTA_REQUEST_DIGEST, false)+"";
									securityMessageApplicabilita = ProtocolPropertiesUtils.getOptionalStringValuePropertyRegistry(op.getProtocolPropertyList(), 
											ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE);
									securityMessageApplicabilitaRichiesta = ProtocolPropertiesUtils.getOptionalStringValuePropertyRegistry(op.getProtocolPropertyList(), 
											ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_RICHIESTA_MODE);
									securityMessageApplicabilitaRichiestaContentType = ProtocolPropertiesUtils.getOptionalStringValuePropertyRegistry(op.getProtocolPropertyList(), 
											ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_RICHIESTA_CONTENT_TYPE_MODE_ID);
									securityMessageApplicabilitaRisposta = ProtocolPropertiesUtils.getOptionalStringValuePropertyRegistry(op.getProtocolPropertyList(), 
											ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_RISPOSTA_MODE);
									securityMessageApplicabilitaRispostaContentType = ProtocolPropertiesUtils.getOptionalStringValuePropertyRegistry(op.getProtocolPropertyList(), 
											ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_RISPOSTA_CONTENT_TYPE_MODE_ID);
									securityMessageApplicabilitaRispostaReturnCode = ProtocolPropertiesUtils.getOptionalStringValuePropertyRegistry(op.getProtocolPropertyList(), 
											ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_RISPOSTA_RETURN_CODE_MODE_ID);
								}
								break;
							}
						}
						break;
					}
				}
			}
			else {
				for (Azione azioneAccordo : aspc.getAzioneList()) {
					if(azioneAccordo.getNome().equals(azione)) {
						interactionProfile = ProtocolPropertiesUtils.getOptionalStringValuePropertyRegistry(azioneAccordo.getProtocolPropertyList(), 
								ModICostanti.MODIPA_PROFILO_INTERAZIONE);
						if(interactionProfile==null) {
							interactionProfile = ModICostanti.MODIPA_PROFILO_INTERAZIONE_DEFAULT_SOAP_VALUE;
						}
						if(ModICostanti.MODIPA_PROFILO_INTERAZIONE_VALUE_NON_BLOCCANTE.equals(interactionProfile)) {
							asyncInteractionProfile = ProtocolPropertiesUtils.getRequiredStringValuePropertyRegistry(azioneAccordo.getProtocolPropertyList(), 
									ModICostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA);
							asyncInteractionRole = ProtocolPropertiesUtils.getRequiredStringValuePropertyRegistry(azioneAccordo.getProtocolPropertyList(), 
									ModICostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_RUOLO);
							if(ModICostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_VALUE_PUSH.equals(asyncInteractionProfile)) {
								// push
								if(!ModICostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_RUOLO_VALUE_RICHIESTA.equals(asyncInteractionRole)) {
									asyncInteractionRequestApi = ProtocolPropertiesUtils.getRequiredStringValuePropertyRegistry(azioneAccordo.getProtocolPropertyList(), 
											ModICostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_API_RICHIESTA_CORRELATA);
									asyncInteractionRequestService = ProtocolPropertiesUtils.getRequiredStringValuePropertyRegistry(azioneAccordo.getProtocolPropertyList(), 
											ModICostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_SERVIZIO_RICHIESTA_CORRELATA);
									asyncInteractionRequestAction = ProtocolPropertiesUtils.getRequiredStringValuePropertyRegistry(azioneAccordo.getProtocolPropertyList(), 
											ModICostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_AZIONE_RICHIESTA_CORRELATA);
								}
							}
							else {
								// pull
								if(!ModICostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_RUOLO_VALUE_RICHIESTA.equals(asyncInteractionRole)) {
									asyncInteractionRequestAction = ProtocolPropertiesUtils.getRequiredStringValuePropertyRegistry(azioneAccordo.getProtocolPropertyList(), 
											ModICostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_AZIONE_RICHIESTA_CORRELATA);
								}
							}
						}
						String securityMessageProfileMode = ProtocolPropertiesUtils.getOptionalStringValuePropertyRegistry(azioneAccordo.getProtocolPropertyList(), 
								ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_ACTION_MODE);
						if(securityMessageProfileMode==null) {
							securityMessageProfileMode = ModICostanti.MODIPA_PROFILO_DEFAULT; // default
						}
						if(ModICostanti.MODIPA_PROFILO_RIDEFINISCI.equals(securityMessageProfileMode)) {
							securityMessageProfile = ProtocolPropertiesUtils.getOptionalStringValuePropertyRegistry(azioneAccordo.getProtocolPropertyList(), 
									ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO);
							securityMessageProfileHeader = ProtocolPropertiesUtils.getOptionalStringValuePropertyRegistry(azioneAccordo.getProtocolPropertyList(), 
									ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER);
							securityMessageCorniceSicurezza = ProtocolPropertiesUtils.getBooleanValuePropertyRegistry(azioneAccordo.getProtocolPropertyList(), 
									ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA, false)+"";
							securityMessageRequestDigest = ProtocolPropertiesUtils.getBooleanValuePropertyRegistry(azioneAccordo.getProtocolPropertyList(), 
									ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RISPOSTA_REQUEST_DIGEST, false)+"";
							securityMessageApplicabilita = ProtocolPropertiesUtils.getOptionalStringValuePropertyRegistry(azioneAccordo.getProtocolPropertyList(), 
									ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE);
							securityMessageApplicabilitaRichiesta = ProtocolPropertiesUtils.getOptionalStringValuePropertyRegistry(azioneAccordo.getProtocolPropertyList(), 
									ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_RICHIESTA_MODE);
							securityMessageApplicabilitaRichiestaContentType = ProtocolPropertiesUtils.getOptionalStringValuePropertyRegistry(azioneAccordo.getProtocolPropertyList(), 
									ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_RICHIESTA_CONTENT_TYPE_MODE_ID);
							securityMessageApplicabilitaRisposta = ProtocolPropertiesUtils.getOptionalStringValuePropertyRegistry(azioneAccordo.getProtocolPropertyList(), 
									ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_RISPOSTA_MODE);
							securityMessageApplicabilitaRispostaContentType = ProtocolPropertiesUtils.getOptionalStringValuePropertyRegistry(azioneAccordo.getProtocolPropertyList(), 
									ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_RISPOSTA_CONTENT_TYPE_MODE_ID);
							securityMessageApplicabilitaRispostaReturnCode = ProtocolPropertiesUtils.getOptionalStringValuePropertyRegistry(azioneAccordo.getProtocolPropertyList(), 
									ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_RISPOSTA_RETURN_CODE_MODE_ID);
						}
						break;
					}
				}
			}
		}
		if(ModICostanti.MODIPA_PROFILO_INTERAZIONE.equals(propertyName)) {
			return interactionProfile;
		}
		else if(ModICostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA.equals(propertyName)) {
			return asyncInteractionProfile;
		}
		else if(ModICostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_RUOLO.equals(propertyName)) {
			return asyncInteractionRole;
		}
		else if(ModICostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_API_RICHIESTA_CORRELATA.equals(propertyName)) {
			return asyncInteractionRequestApi;
		}
		else if(ModICostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_SERVIZIO_RICHIESTA_CORRELATA.equals(propertyName)) {
			return asyncInteractionRequestService;
		}
		else if(ModICostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_AZIONE_RICHIESTA_CORRELATA.equals(propertyName)) {
			return asyncInteractionRequestAction;
		}
		else if(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO.equals(propertyName)) {
			return securityMessageProfile;
		}
		else if(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER.equals(propertyName)) {
			
			if(securityMessageProfileHeader==null || StringUtils.isEmpty(securityMessageProfileHeader)) {
				boolean integrita = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM0301.equals(securityMessageProfile) || 
						ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM0302.equals(securityMessageProfile);
				if(integrita) {
					securityMessageProfileHeader = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_IDAM03_DEFAULT_VALUE;
				}
				else {
					securityMessageProfileHeader = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_NOT_IDAM03_DEFAULT_VALUE;
				}
			}
			if(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_VALUE_MODIPA.equals(securityMessageProfileHeader)) {
				return ModIProperties.getInstance().getRestSecurityTokenHeaderModI();
			}
			else if(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_VALUE_AUTHORIZATION.equals(securityMessageProfileHeader)) {
				return HttpConstants.AUTHORIZATION;
			}
			else {
				// caso che non dovrebbe capitare
				return HttpConstants.AUTHORIZATION;
			}
			
		}
		else if(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA.equals(propertyName)) {
			return securityMessageCorniceSicurezza;
		}
		else if(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RISPOSTA_REQUEST_DIGEST.equals(propertyName)) {
			return securityMessageRequestDigest;
		}
		else if(ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE.equals(propertyName)) {
			return securityMessageApplicabilita;
		}
		else if(ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_RICHIESTA_MODE.equals(propertyName)) {
			return securityMessageApplicabilitaRichiesta;
		}
		else if(ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_RICHIESTA_CONTENT_TYPE_MODE_ID.equals(propertyName)) {
			return securityMessageApplicabilitaRichiestaContentType;
		}
		else if(ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_RISPOSTA_MODE.equals(propertyName)) {
			return securityMessageApplicabilitaRisposta;
		}
		else if(ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_RISPOSTA_CONTENT_TYPE_MODE_ID.equals(propertyName)) {
			return securityMessageApplicabilitaRispostaContentType;
		}
		else if(ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_RISPOSTA_RETURN_CODE_MODE_ID.equals(propertyName)) {
			return securityMessageApplicabilitaRispostaReturnCode;
		}
		return null;
	}

	public static List<ProtocolProperty> getProtocolProperties(boolean fruizione, IDSoggetto soggettoFruitore, AccordoServizioParteSpecifica asps) throws ProtocolException {
		List<ProtocolProperty> listProtocolProperties = null;
		Fruitore fruitore = null;
		if(fruizione) {
			fruitore = getFruitore(soggettoFruitore, asps);
			listProtocolProperties = fruitore.getProtocolPropertyList();
		}
		else {
			listProtocolProperties = asps.getProtocolPropertyList();
		}
		return listProtocolProperties;
	}
	
	public static Fruitore getFruitore(IDSoggetto soggettoFruitore, AccordoServizioParteSpecifica asps) throws ProtocolException {
		if(soggettoFruitore==null) {
			throw new ProtocolException("Fruitore non fornito");
		}
		Fruitore fruitore = null;
		boolean find = false;
		for (Fruitore fruitoreCheck : asps.getFruitoreList()) {
			if(fruitoreCheck.getTipo().equals(soggettoFruitore.getTipo()) && fruitoreCheck.getNome().equals(soggettoFruitore.getNome())) {
				fruitore = fruitoreCheck;
				find = true;
				break;
			}
		}
		if(!find) {
			throw new ProtocolException("Fruitore '"+soggettoFruitore+"' non registrato come fruitore dell'accordo parte specifica");
		}
		return fruitore;
	}
	
	public static String convertProfiloSicurezzaToSDKValue(String securityMessageProfile, boolean rest) {
		String profilo = securityMessageProfile.toUpperCase();
		if(rest) {
			profilo = profilo.replace("M", "R");
		}
		else {
			profilo = profilo.replace("M", "S");
		}
		return profilo;
	}
	public static String convertProfiloSicurezzaToConfigurationValue(String securityMessageProfileSDKValue) {
		String securityMessageProfile = securityMessageProfileSDKValue.toLowerCase();
		securityMessageProfile = securityMessageProfile.replace("r", "m");
		securityMessageProfile = securityMessageProfile.replace("s", "m");
		return securityMessageProfile;
	}
	
	
	public static boolean processSecurity(AccordoServizioParteComune aspc, String nomePortType, String azione, boolean isRichiesta, 
			OpenSPCoop2Message message, boolean rest, ModIProperties modiProperties) throws Exception {
		boolean processSecurity = false;
		String securityMessageApplicabilita = ModIPropertiesUtils.readPropertySecurityMessageApplicabilita(aspc, nomePortType, azione);
		if(securityMessageApplicabilita==null || StringUtils.isEmpty(securityMessageApplicabilita)) {
			securityMessageApplicabilita = ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_VALUE_DEFAULT;
		}
		
		boolean configurazionePersonalizzata = false;
		if(ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_VALUE_ENTRAMBI.equals(securityMessageApplicabilita)) {
			processSecurity = true;
		}
		else if(ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_VALUE_RICHIESTA.equals(securityMessageApplicabilita)) {
			processSecurity = isRichiesta;
		}
		else if(ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_VALUE_RISPOSTA.equals(securityMessageApplicabilita)) {
			processSecurity = !isRichiesta;
		}
		else if(ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_VALUE_PERSONALIZZATO.equals(securityMessageApplicabilita)) {
			
			if(isRichiesta) {
				
				String securityMessageApplicabilitaRichiesta = ModIPropertiesUtils.readPropertySecurityMessageApplicabilitaRichiesta(aspc, nomePortType, azione);
				if(securityMessageApplicabilitaRichiesta==null || StringUtils.isEmpty(securityMessageApplicabilitaRichiesta)) {
					securityMessageApplicabilitaRichiesta = ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_RICHIESTA_MODE_VALUE_DEFAULT;
				}
				
				if(ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_RICHIESTA_MODE_VALUE_ABILITATO.equals(securityMessageApplicabilitaRichiesta)) {
					processSecurity = true;
				}
				else if(ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_RICHIESTA_MODE_VALUE_DISABILITATO.equals(securityMessageApplicabilitaRichiesta)) {
					processSecurity = false;
				}
				else if(ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_RICHIESTA_MODE_VALUE_PERSONALIZZATO.equals(securityMessageApplicabilitaRichiesta)) {
			
					configurazionePersonalizzata = true;
					
					String securityMessageApplicabilitaRichiestaContentType = ModIPropertiesUtils.readPropertySecurityMessageApplicabilitaRichiestaContentType(aspc, nomePortType, azione);
					List<String> check = readValues(securityMessageApplicabilitaRichiestaContentType);
					if(check.size()>0) {
						try {
							processSecurity = ContentTypeUtilities.isMatch(message.getContentType(), check);
						}catch(Exception e) {
							throw new ProtocolException(e.getMessage(),e);
						}
					}
				}
				
			}
			else {
				
				String securityMessageApplicabilitaRisposta = ModIPropertiesUtils.readPropertySecurityMessageApplicabilitaRisposta(aspc, nomePortType, azione);
				if(securityMessageApplicabilitaRisposta==null || StringUtils.isEmpty(securityMessageApplicabilitaRisposta)) {
					securityMessageApplicabilitaRisposta = ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_RISPOSTA_MODE_VALUE_DEFAULT;
				}
				
				if(ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_RISPOSTA_MODE_VALUE_ABILITATO.equals(securityMessageApplicabilitaRisposta)) {
					processSecurity = true;
				}
				else if(ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_RISPOSTA_MODE_VALUE_DISABILITATO.equals(securityMessageApplicabilitaRisposta)) {
					processSecurity = false;
				}
				else if(ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_RISPOSTA_MODE_VALUE_PERSONALIZZATO.equals(securityMessageApplicabilitaRisposta)) {
					
					configurazionePersonalizzata = true;
					
					String securityMessageApplicabilitaRispostaContentType = ModIPropertiesUtils.readPropertySecurityMessageApplicabilitaRispostaContentType(aspc, nomePortType, azione);
					List<String> check = readValues(securityMessageApplicabilitaRispostaContentType);
					if(check.size()>0) {
						try {
							processSecurity = ContentTypeUtilities.isMatch(message.getContentType(), check);
						}catch(Exception e) {
							throw new ProtocolException(e.getMessage(),e);
						}
					}
					else {
						processSecurity = false; // non dovrebbe accadere
					}
					
					if(processSecurity) {
						
						// verifico anche codice
						String securityMessageApplicabilitaRispostaReturnCode = ModIPropertiesUtils.readPropertySecurityMessageApplicabilitaRispostaReturnCode(aspc, nomePortType, azione);
						List<String> checkReturnCode = readValues(securityMessageApplicabilitaRispostaReturnCode);
						if(checkReturnCode.size()>0) {
							
							int httpStatus = -1;
							if(message.getTransportResponseContext()!=null) {
								try {
									httpStatus = Integer.parseInt(message.getTransportResponseContext().getCodiceTrasporto());
								}catch(Exception e) {
									throw new ProtocolException("Transport Response Context non contiene un http status valido ("+message.getTransportResponseContext().getCodiceTrasporto()+")");
								}
							}
							else {
								throw new ProtocolException("Transport Response Context non disponibile");
							}
							
							boolean match = false;
							for (String codice : checkReturnCode) {
								if(codice.contains("-")) {
									String [] tmp = codice.split("-");
									if(tmp==null || tmp.length!=2) {
										throw new ProtocolException(ModIConsoleCostanti.MODIPA_API_CONFIGURAZIONE_SICUREZZA_RISPOSTA_RETURN_CODE_MODE_LABEL+
												" '"+codice+"' possiede un formato errato; atteso: codiceMin-codiceMax");
									}
									String codiceMin = tmp[0];
									String codiceMax = tmp[1];
									if(codiceMin==null || StringUtils.isEmpty(codiceMin.trim())) {
										throw new ProtocolException(ModIConsoleCostanti.MODIPA_API_CONFIGURAZIONE_SICUREZZA_RISPOSTA_RETURN_CODE_MODE_LABEL+
												" '"+codice+"' possiede un formato errato (intervallo minimo non definito); atteso: codiceMin-codiceMax");
									}
									if(codiceMax==null || StringUtils.isEmpty(codiceMax.trim())) {
										throw new ProtocolException(ModIConsoleCostanti.MODIPA_API_CONFIGURAZIONE_SICUREZZA_RISPOSTA_RETURN_CODE_MODE_LABEL+
												" '"+codice+"' possiede un formato errato (intervallo massimo non definito); atteso: codiceMin-codiceMax");
									}
									codiceMin = codiceMin.trim();
									codiceMax = codiceMax.trim();
									int codiceMinInt = -1;
									try {
										codiceMinInt = Integer.valueOf(codiceMin);
									}catch(Exception e) {
										throw new ProtocolException(ModIConsoleCostanti.MODIPA_API_CONFIGURAZIONE_SICUREZZA_RISPOSTA_RETURN_CODE_MODE_LABEL+
												" '"+codice+"' contiene un intervallo minimo '"+codiceMin+"' che non è un numero intero");
									}
									int codiceMaxInt = -1;
									try {
										codiceMaxInt = Integer.valueOf(codiceMax);
									}catch(Exception e) {
										throw new ProtocolException(ModIConsoleCostanti.MODIPA_API_CONFIGURAZIONE_SICUREZZA_RISPOSTA_RETURN_CODE_MODE_LABEL+
												" '"+codice+"' contiene un intervallo massimo '"+codiceMax+"' che non è un numero intero");
									}
									if(codiceMaxInt<=codiceMinInt) {
										throw new ProtocolException(ModIConsoleCostanti.MODIPA_API_CONFIGURAZIONE_SICUREZZA_RISPOSTA_RETURN_CODE_MODE_LABEL+
												" '"+codice+"' contiene un intervallo massimo '"+codiceMax+"' minore o uguale all'intervallo minimo '"+codiceMin+"'");
									}
									if( (codiceMinInt <= httpStatus) && (httpStatus <= codiceMaxInt)) {
										match = true;
										break;
									}
								}
								else {
									try {
										int codiceInt = Integer.valueOf(codice);
										if(codiceInt == httpStatus) {
											match = true;
											break;
										}
									}catch(Exception e) {
										throw new ProtocolException(ModIConsoleCostanti.MODIPA_API_CONFIGURAZIONE_SICUREZZA_RISPOSTA_RETURN_CODE_MODE_LABEL+
												" '"+codice+"' non è un numero intero");
									}
								}
							}
							
							processSecurity = match;
						}
						else {
							processSecurity = false; // non dovrebbe accadere
						}
						
					}
				}
				
			}
			
		}
		
		if(processSecurity && !configurazionePersonalizzata) {
			
			// check Fault (Da properties)
			boolean isFault = false;
			boolean processFault = false;
			if(rest) {
				isFault = message.isFault() || message.castAsRest().isProblemDetailsForHttpApis_RFC7807();	
				processFault = modiProperties.isRestSecurityTokenFaultProcessEnabled();
			}
			else {
				isFault = message.isFault() || message.castAsSoap().getSOAPBody().hasFault();
				processFault = modiProperties.isSoapSecurityTokenFaultProcessEnabled();
			}
			if(isFault && !processFault) {
				processSecurity = false;
			}
			
		}
		
		return processSecurity;
		
	}
	
	private static List<String> readValues(String v){
		List<String> codici = new ArrayList<String>();
		if(v!=null && !StringUtils.isEmpty(v)) {
			if(v.contains(",")) {
				String [] tmp = v.split(",");
				for (int i = 0; i < tmp.length; i++) {
					codici.add(tmp[i].trim());
				}
			}
			else {
				codici.add(v.trim());
			}
		}
		return codici;
	}
}
