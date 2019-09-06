/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it).
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

import java.util.List;

import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.Azione;
import org.openspcoop2.core.registry.Fruitore;
import org.openspcoop2.core.registry.Operation;
import org.openspcoop2.core.registry.PortType;
import org.openspcoop2.core.registry.ProtocolProperty;
import org.openspcoop2.core.registry.Resource;
import org.openspcoop2.protocol.modipa.constants.ModICostanti;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.properties.ProtocolPropertiesUtils;

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
		
		if(org.openspcoop2.core.registry.constants.ServiceBinding.REST.equals(aspc.getServiceBinding())) {
			for (Resource resource : aspc.getResourceList()) {
				if(resource.getNome().equals(azione)) {
					interactionProfile = ProtocolPropertiesUtils.getOptionalStringValuePropertyRegistry(resource.getProtocolPropertyList(), 
							ModICostanti.MODIPA_PROFILO_INTERAZIONE);
					if(interactionProfile==null) {
						interactionProfile = ModICostanti.MODIPA_PROFILO_INTERAZIONE_VALUE_BLOCCANTE; // default
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
									interactionProfile = ModICostanti.MODIPA_PROFILO_INTERAZIONE_VALUE_BLOCCANTE; // default
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
							interactionProfile = ModICostanti.MODIPA_PROFILO_INTERAZIONE_VALUE_BLOCCANTE; // default
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
}
