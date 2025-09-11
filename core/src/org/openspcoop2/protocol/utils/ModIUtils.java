/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it).
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

package org.openspcoop2.protocol.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.constants.CostantiLabel;
import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.Fruitore;
import org.openspcoop2.core.registry.Proprieta;
import org.openspcoop2.core.registry.ProtocolProperty;
import org.openspcoop2.core.registry.Soggetto;
import org.openspcoop2.core.registry.constants.ServiceBinding;
import org.openspcoop2.core.registry.utils.RegistroServiziUtils;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.ModIPDNDClientConfig;
import org.openspcoop2.protocol.sdk.ModIPDNDOrganizationConfig;
import org.openspcoop2.protocol.sdk.ModIPropertiesUtils;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.utils.MapKey;
import org.openspcoop2.utils.certificate.KeystoreParams;
import org.openspcoop2.utils.certificate.KeystoreType;
import org.openspcoop2.utils.certificate.hsm.HSMUtils;
import org.openspcoop2.utils.certificate.remote.RemoteKeyType;
import org.openspcoop2.utils.certificate.remote.RemoteStoreConfig;
import org.openspcoop2.utils.digest.DigestEncoding;
import org.openspcoop2.utils.io.Base64Utilities;
import org.openspcoop2.utils.json.JsonPathExpressionEngine;
import org.slf4j.Logger;

/**
 * ModIUtils
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ModIUtils {
	
	private ModIUtils() {}

	// COSTANTI boolean
	public static final String INTEGRITY = "integrity";
	public static final String DIGEST_RICHIESTA = "request-digest";
	public static final String CORNICE_SICUREZZA = "user-info";
	public static final String CORNICE_SICUREZZA_PATTERN = "user-info-pattern";
	public static final String CORNICE_SICUREZZA_SCHEMA = "user-info-schema";
	public static final String HEADER_DUPLICATI = "contemporary-headers";
	public static boolean isBooleanIndicator(String key) {
		return ModIUtils.INTEGRITY.equals(key) || 
				ModIUtils.DIGEST_RICHIESTA.equals(key) ||
				ModIUtils.CORNICE_SICUREZZA.equals(key) ||
				ModIUtils.HEADER_DUPLICATI.equals(key);
	}
	
	// COSTANTI string
	private static final String API_PREFIX = "api-";
	public static final String API_SICUREZZA_CANALE_PATTERN = API_PREFIX+"channel-security-pattern";
	public static final String API_SICUREZZA_MESSAGGIO_PREFIX = API_PREFIX+"message-security";
	public static final String API_SICUREZZA_MESSAGGIO_PATTERN = API_SICUREZZA_MESSAGGIO_PREFIX+"-pattern";
	public static final String API_SICUREZZA_MESSAGGIO_SORGENTE_TOKEN_ID_AUTH = API_SICUREZZA_MESSAGGIO_PREFIX+"-sorgente-token-id-auth";
	public static final String API_SICUREZZA_MESSAGGIO_HTTP_HEADER = API_SICUREZZA_MESSAGGIO_PREFIX+"-http-header";
	public static final String API_SICUREZZA_MESSAGGIO_APPLICABILITA = API_SICUREZZA_MESSAGGIO_PREFIX+"-applicability";
	public static final String API_SICUREZZA_MESSAGGIO_REQUEST_DIGEST = API_SICUREZZA_MESSAGGIO_PREFIX+"-request-digest";
	public static final String API_SICUREZZA_MESSAGGIO_USER_INFO = API_SICUREZZA_MESSAGGIO_PREFIX+"-user-info";
	
	private static final String REQUEST_PREFIX = "request-";
	private static final String RESPONSE_PREFIX = "response-";
	
	public static final String API_IMPL_SICUREZZA_MESSAGGIO_SIGNATURE_ALGORITHM = "signature-algorithm";
	public static final String API_IMPL_SICUREZZA_MESSAGGIO_CANONICALIZATION_ALGORITHM = "canonicalization-algorithm";
	public static final String API_IMPL_SICUREZZA_MESSAGGIO_DIGEST_ENCODING = "digest-encoding";
	public static final String API_IMPL_SICUREZZA_MESSAGGIO_HTTP_HEADER_FIRMATI = "signed-http-headers";
	public static final String API_IMPL_SICUREZZA_MESSAGGIO_SOAP_HEADER_FIRMATI = "signed-soap-headers";
	public static final String API_IMPL_SICUREZZA_MESSAGGIO_RIFERIMENTO_X509 = "x509-reference";
	public static final String API_IMPL_SICUREZZA_MESSAGGIO_CATENA_CERTIFICATI_X509 = "x509-certificate-chain";
	public static final String API_IMPL_SICUREZZA_MESSAGGIO_INCLUDE_SIGNATURE_TOKEN = "include-signature-token";
	public static final String API_IMPL_SICUREZZA_MESSAGGIO_TTL = "ttl";
	public static final String API_IMPL_SICUREZZA_MESSAGGIO_AUDIENCE = "audience";
	public static final String API_IMPL_SICUREZZA_MESSAGGIO_VERIFICA_AUDIENCE = "audience-verify";
	public static final String API_IMPL_SICUREZZA_MESSAGGIO_INTEGRITY_AUDIENCE = "integrity-audience";
	public static final String API_IMPL_SICUREZZA_MESSAGGIO_FRUIZIONE_KEYSTORE_MODE = "fruizione-keystore-mode";
	public static final String API_IMPL_SICUREZZA_MESSAGGIO_X5U_CERTIFICATE_URL = "x5u-certificate-url";
	public static final String API_IMPL_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_PREFIX = "audit-info-";
	public static final String API_IMPL_SICUREZZA_MESSAGGIO_AUDIT_AUDIENCE = "audit-audience";
	
	public static final String API_IMPL_SICUREZZA_MESSAGGIO_STORE_TYPE = "type";
	public static final String API_IMPL_SICUREZZA_MESSAGGIO_STORE_PATH = "path";
	public static final String API_IMPL_SICUREZZA_MESSAGGIO_STORE_CRLS = "crls";
	public static final String API_IMPL_SICUREZZA_MESSAGGIO_STORE_OCSP_POLICY = "ocsp";
	public static final String API_IMPL_SICUREZZA_MESSAGGIO_KEY_ALIAS = "key-alias";
	
	public static final String API_IMPL_SICUREZZA_OAUTH_IDENTIFICATIVO = "oauth-id";
	public static final String API_IMPL_SICUREZZA_OAUTH_KID = "oauth-kid";
	
	public static final String API_IMPL_INFO_ESERVICE_ID = "info-eservice-id";
	public static final String API_IMPL_INFO_DESCRIPTOR_ID = "info-descriptor-id";
	public static final String API_IMPL_INFO_SIGNAL_HUB = "info-signal-hub";
	
	// COSTANTI SERVIZIO
	public static final String HSM = "hsm";
	
	// COSTANTI API PDND
	public static final String API_PDND_EVENTS_KEYS_PATH = "api-pdnd-eventsKeys-path";
	public static final String API_PDND_EVENTS_KEYS_PARAMETER_LASTEVENTID = "api-pdnd-eventsKeys-parameterLastEventId";
	public static final String API_PDND_EVENTS_KEYS_PARAMETER_LIMIT = "api-pdnd-eventsKeys-parameterLimit";
	public static final String API_PDND_CLIENTS_PATH = "api-pdnd-clients-path";
	public static final String API_PDND_CLIENTS_ORGANIZATION_JSON_PATH = "api-pdnd-clients-organizationJsonPath";
	public static final String API_PDND_ORGANIZATIONS_PATH = "api-pdnd-organizations-path";
	public static String extractInfoFromMetadati(Map<String, String> metadati, String key, String description) throws ProtocolException{
		if(metadati==null || metadati.isEmpty() || !metadati.containsKey(key)) {
			throw new ProtocolException(description+" not found"); 
		}
		String v = metadati.get(key);
		if(v==null || StringUtils.isEmpty(v.trim())) {
			throw new ProtocolException(description+" undefined"); 
		}
		return v;
	}
	
	
	
	public static Map<String, String> configToMap(AccordoServizioParteComune aspc, AccordoServizioParteSpecifica asps,
			String urlInvocazione, 
			Fruitore fruitore, String urlConnettoreFruitoreModI
			) throws ProtocolException{
		
		boolean gestioneErogatori = (fruitore==null);
		boolean gestioneFruitori = !gestioneErogatori;
		
		Map<String, String> map = new HashMap<>();
				
		List<ProtocolProperty> protocolPropertyList = gestioneErogatori ? asps.getProtocolPropertyList() : fruitore.getProtocolPropertyList();
		
		boolean rest = ServiceBinding.REST.equals(aspc.getServiceBinding());
		boolean digest = isProfiloSicurezzaMessaggioConIntegritaX509(aspc, asps.getPortType()) || isProfiloSicurezzaMessaggioConIntegritaKid(aspc, asps.getPortType());
		boolean digestRichiesta = isProfiloSicurezzaMessaggioRequestDigest(aspc, asps.getPortType());
		boolean corniceSicurezza = isProfiloSicurezzaMessaggioCorniceSicurezza(aspc, asps.getPortType());
		String patternDatiCorniceSicurezza = null;
		String schemaDatiCorniceSicurezza = null;
		if(corniceSicurezza) {
			patternDatiCorniceSicurezza = getProfiloSicurezzaMessaggioCorniceSicurezzaPattern(aspc, asps.getPortType());
			if(patternDatiCorniceSicurezza==null) {
				// backward compatibility
				patternDatiCorniceSicurezza = CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_PATTERN_OLD;
			}
			if(!CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_PATTERN_OLD.equals(patternDatiCorniceSicurezza)) {
				schemaDatiCorniceSicurezza = getProfiloSicurezzaMessaggioCorniceSicurezzaSchema(aspc, asps.getPortType());
			}
		}
		boolean headerDuplicati = false;
		if(rest) {
			headerDuplicati = isProfiloSicurezzaMessaggioConHeaderDuplicati(aspc, asps.getPortType());
		}
		map.put(INTEGRITY, digest+"");
		map.put(DIGEST_RICHIESTA, digestRichiesta+"");
		map.put(CORNICE_SICUREZZA, corniceSicurezza+"");
		if(corniceSicurezza) {
			map.put(CORNICE_SICUREZZA_PATTERN, patternDatiCorniceSicurezza);
			if(schemaDatiCorniceSicurezza!=null) {
				map.put(CORNICE_SICUREZZA_SCHEMA, schemaDatiCorniceSicurezza);
			}
		}
		map.put(HEADER_DUPLICATI, headerDuplicati+"");
		
		// sicurezza canale
		String v = getStringValue(aspc.getProtocolPropertyList(), CostantiDB.MODIPA_PROFILO_SICUREZZA_CANALE);
		String canale = null;
		if(CostantiDB.MODIPA_PROFILO_SICUREZZA_CANALE_VALUE_IDAC01.equals(v)) {
			canale = CostantiLabel.MODIPA_PROFILO_SICUREZZA_CANALE_LABEL_IDAC01;
		}
		else if(CostantiDB.MODIPA_PROFILO_SICUREZZA_CANALE_VALUE_IDAC02.equals(v)) {
			canale = CostantiLabel.MODIPA_PROFILO_SICUREZZA_CANALE_LABEL_IDAC02;
		}
		if(canale!=null) {
			map.put(API_SICUREZZA_CANALE_PATTERN, canale);
		}
		
		// sicurezza messaggio
		boolean sicurezzaMessaggio = false;
		List<String> tmp = RegistroServiziUtils.fillPropertyProtocollo(CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO, aspc, asps.getPortType(), false);
		if(tmp!=null && !tmp.isEmpty()) {
			StringBuilder sbMes = new StringBuilder();
			for (String pattern : tmp) {
				if(CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM01.equals(pattern)) {
					if(sbMes.length()>0) {
						sbMes.append(", ");
					}
					sbMes.append(rest ? CostantiLabel.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_LABEL_IDAM01_REST : CostantiLabel.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_LABEL_IDAM01_SOAP );
				}
				else if(CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM02.equals(pattern)) {
					if(sbMes.length()>0) {
						sbMes.append(", ");
					}
					sbMes.append(rest ? CostantiLabel.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_LABEL_IDAM02_REST : CostantiLabel.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_LABEL_IDAM02_SOAP );
				}
				else if(CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM0301.equals(pattern)) {
					if(sbMes.length()>0) {
						sbMes.append(", ");
					}
					sbMes.append(rest ? CostantiLabel.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_LABEL_IDAM0301_REST : CostantiLabel.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_LABEL_IDAM0301_SOAP );
				}
				else if(CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM0302.equals(pattern)) {
					if(sbMes.length()>0) {
						sbMes.append(", ");
					}
					sbMes.append(rest ? CostantiLabel.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_LABEL_IDAM0302_REST : CostantiLabel.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_LABEL_IDAM0302_SOAP );
				}
				else if(CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM0401.equals(pattern) && rest) {
					if(sbMes.length()>0) {
						sbMes.append(", ");
					}
					sbMes.append(CostantiLabel.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_LABEL_IDAM0401_REST);
				}
				else if(CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM0402.equals(pattern) && rest) {
					if(sbMes.length()>0) {
						sbMes.append(", ");
					}
					sbMes.append(CostantiLabel.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_LABEL_IDAM0402_REST);
				}
			}
			if(sbMes.length()>0) {
				sicurezzaMessaggio = true;
				map.put(API_SICUREZZA_MESSAGGIO_PATTERN, sbMes.toString());
			}
		}
		
		boolean sicurezzaMessaggioPdnd = false;
		if(sicurezzaMessaggio) {
			
			// Sorgente Token ID AUTH
			tmp = RegistroServiziUtils.fillPropertyProtocollo(CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SORGENTE_TOKEN_IDAUTH, aspc, asps.getPortType(), false);
			if(tmp!=null && !tmp.isEmpty()) {
				StringBuilder sbMes = new StringBuilder();
				for (String mode : tmp) {
					if(CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SORGENTE_TOKEN_IDAUTH_VALUE_LOCALE.equals(mode)) {
						if(sbMes.length()>0) {
							sbMes.append(", ");
						}
						sbMes.append(CostantiLabel.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SORGENTE_TOKEN_IDAUTH_LOCALE);
					}
					else if(CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SORGENTE_TOKEN_IDAUTH_VALUE_PDND.equals(mode)) {
						if(sbMes.length()>0) {
							sbMes.append(", ");
						}
						sbMes.append(CostantiLabel.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SORGENTE_TOKEN_IDAUTH_PDND);
						sicurezzaMessaggioPdnd = true;
					}
					else if(CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SORGENTE_TOKEN_IDAUTH_VALUE_OAUTH.equals(mode)) {
						if(sbMes.length()>0) {
							sbMes.append(", ");
						}
						sbMes.append(CostantiLabel.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SORGENTE_TOKEN_IDAUTH_OAUTH);
					}
				}
				if(sbMes.length()>0) {
					map.put(API_SICUREZZA_MESSAGGIO_SORGENTE_TOKEN_ID_AUTH, sbMes.toString());
				}
			}
			
			if(rest) {
				// Header (duplicati)
				tmp = RegistroServiziUtils.fillPropertyProtocollo(CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER, aspc, asps.getPortType(), false);
				if(tmp!=null && !tmp.isEmpty()) {
					StringBuilder sbMes = new StringBuilder();
					String headerModI = getHeaderModI();
					for (String mode : tmp) {
						if(CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_VALUE_MODIPA.equals(mode)) {
							if(sbMes.length()>0) {
								sbMes.append(", ");
							}
							sbMes.append(CostantiLabel.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_LABEL_MODIPA.
									replace(CostantiLabel.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_LABEL_MODIPA, headerModI));
						}
						else if(CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_VALUE_AUTHORIZATION.equals(mode)) {
							if(sbMes.length()>0) {
								sbMes.append(", ");
							}
							sbMes.append(CostantiLabel.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_LABEL_AUTHORIZATION);
						}
						else if(CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_VALUE_AUTHORIZATION_MODIPA.equals(mode)) {
							if(sbMes.length()>0) {
								sbMes.append(", ");
							}
							sbMes.append(CostantiLabel.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_LABEL_AUTHORIZATION_MODIPA.
									replace(CostantiLabel.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_LABEL_MODIPA, headerModI));
						}
						else if(CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_VALUE_AUTHORIZATION_MODIPA_AUTH_IN_RESPONSE.equals(mode)) {
							if(sbMes.length()>0) {
								sbMes.append(", ");
							}
							sbMes.append(CostantiLabel.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_LABEL_AUTHORIZATION_MODIPA_AUTH_IN_RESPONSE.
									replace(CostantiLabel.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_LABEL_MODIPA, headerModI));
						}
						else if(CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_VALUE_CUSTOM.equals(mode)) {
							if(sbMes.length()>0) {
								sbMes.append(", ");
							}
							sbMes.append(CostantiLabel.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_LABEL_CUSTOM);
						}
						else if(CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_VALUE_AUTHORIZATION_CUSTOM.equals(mode)) {
							if(sbMes.length()>0) {
								sbMes.append(", ");
							}
							sbMes.append(CostantiLabel.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_LABEL_AUTHORIZATION_CUSTOM);
						}
						else if(CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_VALUE_AUTHORIZATION_CUSTOM_AUTH_IN_RESPONSE.equals(mode)) {
							if(sbMes.length()>0) {
								sbMes.append(", ");
							}
							sbMes.append(CostantiLabel.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_LABEL_AUTHORIZATION_CUSTOM_AUTH_IN_RESPONSE);
						}
					}
					if(sbMes.length()>0) {
						map.put(API_SICUREZZA_MESSAGGIO_HTTP_HEADER, sbMes.toString());
					}
				}
			}
			
			// Applicabilita
			tmp = RegistroServiziUtils.fillPropertyProtocollo(CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_MODE, aspc, asps.getPortType(), false);
			if(tmp!=null && !tmp.isEmpty()) {
				StringBuilder sbMes = new StringBuilder();
				for (String mode : tmp) {
					if(CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_MODE_VALUE_ENTRAMBI.equals(mode)) {
						if(sbMes.length()>0) {
							sbMes.append(", ");
						}
						sbMes.append(CostantiLabel.MODIPA_API_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_LABEL_ENTRAMBI);
					}
					else if(CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_MODE_VALUE_RICHIESTA.equals(mode)) {
						if(sbMes.length()>0) {
							sbMes.append(", ");
						}
						sbMes.append(CostantiLabel.MODIPA_API_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_LABEL_RICHIESTA);
					}
					else if(CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_MODE_VALUE_RISPOSTA.equals(mode)) {
						if(sbMes.length()>0) {
							sbMes.append(", ");
						}
						sbMes.append(CostantiLabel.MODIPA_API_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_LABEL_RISPOSTA);
					}
					else if(CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_MODE_VALUE_ENTRAMBI_CON_ATTACHMENTS.equals(mode)) {
						if(sbMes.length()>0) {
							sbMes.append(", ");
						}
						sbMes.append(CostantiLabel.MODIPA_API_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_LABEL_ENTRAMBI_CON_ATTACHMENTS);
					}
					else if(CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_MODE_VALUE_RICHIESTA_CON_ATTACHMENTS.equals(mode)) {
						if(sbMes.length()>0) {
							sbMes.append(", ");
						}
						sbMes.append(CostantiLabel.MODIPA_API_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_LABEL_RICHIESTA_CON_ATTACHMENTS);
					}
					else if(CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_MODE_VALUE_RISPOSTA_CON_ATTACHMENTS.equals(mode)) {
						if(sbMes.length()>0) {
							sbMes.append(", ");
						}
						sbMes.append(CostantiLabel.MODIPA_API_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_LABEL_RISPOSTA_CON_ATTACHMENTS);
					}
					else if(CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_MODE_VALUE_PERSONALIZZATO.equals(mode)) {
						if(sbMes.length()>0) {
							sbMes.append(", ");
						}
						sbMes.append(CostantiLabel.MODIPA_API_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_LABEL_PERSONALIZZATO);
					}
				}
				if(sbMes.length()>0) {
					map.put(API_SICUREZZA_MESSAGGIO_APPLICABILITA, sbMes.toString());
				}
			}
			
			// Request Digest
			map.put(API_SICUREZZA_MESSAGGIO_REQUEST_DIGEST,
					digestRichiesta ? StatoFunzionalita.ABILITATO.getValue() : StatoFunzionalita.DISABILITATO.getValue());
			
			// Cornice Sicurezza
			if(corniceSicurezza) {
				if(CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_PATTERN_OLD.equals(patternDatiCorniceSicurezza)) {
					map.put(API_SICUREZZA_MESSAGGIO_USER_INFO,
							CostantiLabel.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_PATTERN_OLD);
				}
				else if(CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_PATTERN_AUDIT_REST_01.equals(patternDatiCorniceSicurezza)) {
					map.put(API_SICUREZZA_MESSAGGIO_USER_INFO,
							CostantiLabel.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_PATTERN_AUDIT_REST_01+" (schema:"+schemaDatiCorniceSicurezza+")");
				}
				else if(CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_PATTERN_AUDIT_REST_02.equals(patternDatiCorniceSicurezza)) {
					map.put(API_SICUREZZA_MESSAGGIO_USER_INFO,
							CostantiLabel.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_PATTERN_AUDIT_REST_02+" (schema:"+schemaDatiCorniceSicurezza+")");
				}
			}
			else {
				map.put(API_SICUREZZA_MESSAGGIO_USER_INFO,
					StatoFunzionalita.DISABILITATO.getValue());
			}
							
			boolean request = true;
			addProfiloModISicurezza(map,
					protocolPropertyList,
					rest, gestioneFruitori, request, 
					digest, 
					patternDatiCorniceSicurezza, schemaDatiCorniceSicurezza, 
					headerDuplicati,
					urlInvocazione, urlConnettoreFruitoreModI);
			addProfiloModISicurezza(map,
					protocolPropertyList,
					rest, gestioneFruitori, !request, 
					digest, 
					patternDatiCorniceSicurezza, schemaDatiCorniceSicurezza, 
					headerDuplicati,
					urlInvocazione, urlConnettoreFruitoreModI);
		}
		else {
			
			// Sicurezza OAuth
			
			if( gestioneFruitori ) {
				
				v = getStringValue(protocolPropertyList, CostantiDB.MODIPA_PROFILO_SICUREZZA_OAUTH_IDENTIFICATIVO);
				if(v!=null && StringUtils.isNotEmpty(v)) {
					map.put(API_IMPL_SICUREZZA_OAUTH_IDENTIFICATIVO, v);
				}
				v = getStringValue(protocolPropertyList, CostantiDB.MODIPA_PROFILO_SICUREZZA_OAUTH_KID);
				if(v!=null && StringUtils.isNotEmpty(v)) {
					map.put(API_IMPL_SICUREZZA_OAUTH_KID, v);
				}
				
				addStore(map, protocolPropertyList, "", false, false);
			}
			
		}
		
		if(sicurezzaMessaggioPdnd && gestioneErogatori) {
			
			v = getStringValue(protocolPropertyList, CostantiDB.MODIPA_API_IMPL_INFO_ESERVICE_ID);
			if(v!=null && StringUtils.isNotEmpty(v)) {
				map.put(API_IMPL_INFO_ESERVICE_ID, v);
			}
			
			v = getStringValue(protocolPropertyList, CostantiDB.MODIPA_API_IMPL_INFO_DESCRIPTOR_ID);
			if(v!=null && StringUtils.isNotEmpty(v)) {
				map.put(API_IMPL_INFO_DESCRIPTOR_ID, v);
			}
			
			
			v = getStringValue(protocolPropertyList, CostantiDB.MODIPA_API_IMPL_INFO_SIGNAL_HUB_ID);
			if(v!=null && StringUtils.isNotEmpty(v)) {
				map.put(API_IMPL_INFO_SIGNAL_HUB, "true".equals(v) ? StatoFunzionalita.ABILITATO.getValue() : StatoFunzionalita.DISABILITATO.getValue());
			}
			else if(ModIUtils.isSignalHubEnabled()) {
				map.put(API_IMPL_INFO_SIGNAL_HUB, StatoFunzionalita.DISABILITATO.getValue());
			}
		}
		
		return map;
		
	}
	
	public static String getPrefixKey(boolean fruizione, boolean request) {
		String prefixKey = null;
		if(fruizione) {
			// nop
		}
		/**if(request) {
			prefixKey = fruizione ? "out-request-" : "in-request-"; 
		}
		else {
			prefixKey = fruizione ? "in-response-" : "out-response-"; 
		}*/
		prefixKey = request ? REQUEST_PREFIX : RESPONSE_PREFIX; 
		return prefixKey;
	}
	
	private static void addProfiloModISicurezza(Map<String, String> map,
			List<ProtocolProperty> protocolPropertyList,
			boolean rest, boolean fruizione, boolean request, 
			boolean digest, 
			String patternDatiCorniceSicurezza, String schemaDatiCorniceSicurezza, 
			boolean headerDuplicati,
			String urlInvocazione, String urlConnettoreFruitoreModI) {
		
		String prefixKey = getPrefixKey(fruizione, request);
		
		// Firma
		boolean x5u = false;
		if(rest) {
			
			// Algoritmo
			String idProfiloSicurezzaMessaggioAlgItem = null;
			if(fruizione && request) {
				idProfiloSicurezzaMessaggioAlgItem = CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_RICHIESTA_ALG;
			}
			else if(!fruizione && !request) {
				idProfiloSicurezzaMessaggioAlgItem = CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_RISPOSTA_ALG;
			}
			if(idProfiloSicurezzaMessaggioAlgItem!=null) {
				String v = getStringValue(protocolPropertyList, idProfiloSicurezzaMessaggioAlgItem);
				map.put(prefixKey+API_IMPL_SICUREZZA_MESSAGGIO_SIGNATURE_ALGORITHM, v);
			}
			
			// Codifica Digest
			String idProfiloSicurezzaMessaggioDigestEncodingItem = null;
			if(fruizione && request) {
				idProfiloSicurezzaMessaggioDigestEncodingItem = CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_RICHIESTA_DIGEST_ENCODING;
			}
			else if(!fruizione && !request) {
				idProfiloSicurezzaMessaggioDigestEncodingItem = CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_RISPOSTA_DIGEST_ENCODING;
			}
			if(idProfiloSicurezzaMessaggioDigestEncodingItem!=null) {
				String v = getStringValue(protocolPropertyList, idProfiloSicurezzaMessaggioDigestEncodingItem);
				if(v!=null) {
					try {
						DigestEncoding de = DigestEncoding.valueOf(v);
						if(DigestEncoding.BASE64.equals(de)) {
							map.put(prefixKey+API_IMPL_SICUREZZA_MESSAGGIO_DIGEST_ENCODING, CostantiLabel.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_DIGEST_ENCODING_LABEL_BASE64);
						}else{ //HEX:
							map.put(prefixKey+API_IMPL_SICUREZZA_MESSAGGIO_DIGEST_ENCODING, CostantiLabel.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_DIGEST_ENCODING_LABEL_HEX);
						}
					}catch(Exception t) {
						map.put(prefixKey+API_IMPL_SICUREZZA_MESSAGGIO_DIGEST_ENCODING, v);
					}
				}
				else {
					map.put(prefixKey+API_IMPL_SICUREZZA_MESSAGGIO_DIGEST_ENCODING, v);
				}
			}
			
			if(digest &&
				// header firmati
				( (request && fruizione) || (!request && !fruizione) ) 
				){
				map.put(prefixKey+API_IMPL_SICUREZZA_MESSAGGIO_HTTP_HEADER_FIRMATI, 
						getStringValue(protocolPropertyList, CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HTTP_HEADERS_REST));
			}
			
			// Posizione Certificato
			String rifX509Id = request ? CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_RICHIESTA_RIFERIMENTO_X509 :
				CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_RISPOSTA_RIFERIMENTO_X509;
			String v = getStringValue(protocolPropertyList, rifX509Id);
			map.put(prefixKey+API_IMPL_SICUREZZA_MESSAGGIO_RIFERIMENTO_X509, v);
			x5u = (v!=null) && v.contains("x5u");
			
			// Certificate Chain
			String rifX509Xc5ChainId = null;
			if(fruizione && request) {
				rifX509Xc5ChainId = CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_RICHIESTA_RIFERIMENTO_X509_X5C_USE_CERTIFICATE_CHAIN;
			}
			else if(!fruizione && !request) {
				rifX509Xc5ChainId = CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_RISPOSTA_RIFERIMENTO_X509_X5C_USE_CERTIFICATE_CHAIN;
			}
			if(rifX509Xc5ChainId!=null) {
				map.put(prefixKey+API_IMPL_SICUREZZA_MESSAGGIO_CATENA_CERTIFICATI_X509, 
						getBooleanValueAsStato(protocolPropertyList, rifX509Xc5ChainId));
			}
		}
		else {
			
			// Algoritmo
			String idProfiloSicurezzaMessaggioAlgItem = null;
			if(fruizione && request) {
				idProfiloSicurezzaMessaggioAlgItem = CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SOAP_RICHIESTA_ALG;
			}
			else if(!fruizione && !request) {
				idProfiloSicurezzaMessaggioAlgItem = CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SOAP_RISPOSTA_ALG;
			}
			if(idProfiloSicurezzaMessaggioAlgItem!=null) {
				String algoLabel = getStringValue(protocolPropertyList, idProfiloSicurezzaMessaggioAlgItem);
				if(algoLabel!=null &&
					algoLabel.contains("#") && !algoLabel.endsWith("#")) {
					algoLabel = algoLabel.substring(algoLabel.indexOf("#")+1, algoLabel.length()).toUpperCase();
				}
				if(algoLabel!=null) {
					map.put(prefixKey+API_IMPL_SICUREZZA_MESSAGGIO_SIGNATURE_ALGORITHM, algoLabel);
				}
			}
			
			// Algoritmo C14N
			String idProfiloSicurezzaMessaggioAlgC14NItem = null;
			if(fruizione && request) {
				idProfiloSicurezzaMessaggioAlgC14NItem = CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SOAP_RICHIESTA_CANONICALIZATION_ALG;
			}
			else if(!fruizione && !request) {
				idProfiloSicurezzaMessaggioAlgC14NItem = CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SOAP_RISPOSTA_CANONICALIZATION_ALG;
			}
			if(idProfiloSicurezzaMessaggioAlgC14NItem!=null) {
				String algoLabel = getStringValue(protocolPropertyList, idProfiloSicurezzaMessaggioAlgC14NItem);
				if(CostantiDB.INCLUSIVE_C14N_10_OMITS_COMMENTS_URI.equals(algoLabel)) {
					algoLabel = CostantiLabel.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_CANONICALIZATION_ALG_LABEL_INCLUSIVE_C14N_10;
				}
				else if(CostantiDB.INCLUSIVE_C14N_11_OMITS_COMMENTS_URI.equals(algoLabel)) {
					algoLabel = CostantiLabel.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_CANONICALIZATION_ALG_LABEL_INCLUSIVE_C14N_11;
				}
				else if(CostantiDB.EXCLUSIVE_C14N_10_OMITS_COMMENTS_URI.equals(algoLabel)) {
					algoLabel = CostantiLabel.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_CANONICALIZATION_ALG_LABEL_EXCLUSIVE_C14N_10;
				}
				map.put(prefixKey+API_IMPL_SICUREZZA_MESSAGGIO_CANONICALIZATION_ALGORITHM,algoLabel);
			}
			
			if(digest &&
				// header firmati
				( (request && fruizione) || (!request && !fruizione) ) 
			){
				String v = getStringValue(protocolPropertyList, CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SOAP_HEADERS_SOAP);
				if(v!=null) {
					int index = 0;
					while(v.contains("\n") && index<1000) {
						v = v.replace("\n", ", ");
						index++;
					}
					map.put(prefixKey+API_IMPL_SICUREZZA_MESSAGGIO_SOAP_HEADER_FIRMATI, v);
				}
			}
			
			// Posizione Certificato
			String rifX509Id = null;
			if(fruizione && request) {
				rifX509Id = CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SOAP_RICHIESTA_RIFERIMENTO_X509;
			}
			else if(!fruizione && !request) {
				rifX509Id = CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SOAP_RISPOSTA_RIFERIMENTO_X509;
			}
			boolean useCertificateChain = false;
			boolean includeSignatureToken = false;
			if(rifX509Id!=null) {
				String refLabel = getStringValue(protocolPropertyList, rifX509Id);
				if(CostantiDB.KEY_IDENTIFIER_BST_DIRECT_REFERENCE.equals(refLabel)){
					refLabel = CostantiLabel.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_RIFERIMENTO_X509_LABEL_BINARY_SECURITY_TOKEN;
					useCertificateChain = true;
				}
				else if(CostantiDB.KEY_IDENTIFIER_ISSUER_SERIAL.equals(refLabel)){
					refLabel = CostantiLabel.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_RIFERIMENTO_X509_LABEL_SECURITY_TOKEN_REFERENCE;
					includeSignatureToken = true;
				}
				else if(CostantiDB.KEY_IDENTIFIER_X509.equals(refLabel)){
					refLabel = CostantiLabel.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_RIFERIMENTO_X509_LABEL_KEY_IDENTIFIER_X509;
				}
				else if(CostantiDB.KEY_IDENTIFIER_THUMBPRINT.equals(refLabel)){
					refLabel = CostantiLabel.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_RIFERIMENTO_X509_LABEL_KEY_IDENTIFIER_THUMBPRINT;
					includeSignatureToken = true;
				}
				else if(CostantiDB.KEY_IDENTIFIER_SKI.equals(refLabel)){
					refLabel = CostantiLabel.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_RIFERIMENTO_X509_LABEL_KEY_IDENTIFIER_SKI;
					includeSignatureToken = true;
				}
				map.put(prefixKey+API_IMPL_SICUREZZA_MESSAGGIO_RIFERIMENTO_X509, refLabel);
			}
			
			if(useCertificateChain) {
				// Certificate Chain
				String certId = null;
				if(fruizione && request) {
					certId = CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SOAP_RICHIESTA_RIFERIMENTO_X509_BINARY_SECURITY_TOKEN_USE_CERTIFICATE_CHAIN;
				}
				else if(!fruizione && !request) {
					certId = CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SOAP_RISPOSTA_RIFERIMENTO_X509_BINARY_SECURITY_TOKEN_USE_CERTIFICATE_CHAIN;
				}
				if(certId!=null) {
					map.put(prefixKey+API_IMPL_SICUREZZA_MESSAGGIO_CATENA_CERTIFICATI_X509,
							getBooleanValueAsStato(protocolPropertyList, certId));
				}
			}
			
			if(includeSignatureToken) {
				// Signature Token
				String certId = null;
				if(fruizione && request) {
					certId = CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SOAP_RICHIESTA_RIFERIMENTO_X509_BINARY_SECURITY_TOKEN_INCLUDE_SIGNATURE_TOKEN;
				}
				else if(!fruizione && !request) {
					certId = CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SOAP_RISPOSTA_RIFERIMENTO_X509_BINARY_SECURITY_TOKEN_INCLUDE_SIGNATURE_TOKEN;
				}
				if(certId!=null) {
					map.put(prefixKey+API_IMPL_SICUREZZA_MESSAGGIO_INCLUDE_SIGNATURE_TOKEN, 
							getBooleanValueAsStato(protocolPropertyList, certId));
				}
			}
			
		}
		
		// Ttl
		String idProfiloSicurezzaMessaggioIatTtlItem = null;
		String idProfiloSicurezzaMessaggioIatTtlSecondsItem = null;
		if(fruizione && !request) {
			idProfiloSicurezzaMessaggioIatTtlItem = CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RISPOSTA_IAT;
			idProfiloSicurezzaMessaggioIatTtlSecondsItem = CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RISPOSTA_IAT_SECONDS;
		}
		else if(!fruizione && request) {
			idProfiloSicurezzaMessaggioIatTtlItem = CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RICHIESTA_IAT;
			idProfiloSicurezzaMessaggioIatTtlSecondsItem = CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RICHIESTA_IAT_SECONDS;
		}
		if(idProfiloSicurezzaMessaggioIatTtlItem!=null && idProfiloSicurezzaMessaggioIatTtlSecondsItem!=null) {
			String v = getStringValue(protocolPropertyList, idProfiloSicurezzaMessaggioIatTtlItem);
			if(v==null || StringUtils.isEmpty(v) || CostantiDB.MODIPA_PROFILO_DEFAULT.equals(v)) {
				map.put(prefixKey+API_IMPL_SICUREZZA_MESSAGGIO_TTL, CostantiLabel.MODIPA_LABEL_DEFAULT);
			}
			else {
				v = getStringValue(protocolPropertyList, idProfiloSicurezzaMessaggioIatTtlSecondsItem);
				map.put(prefixKey+API_IMPL_SICUREZZA_MESSAGGIO_TTL, v);
			}
		}
		
		// Expiration Time
		String idProfiloSicurezzaMessaggioExpItem = null;
		if(fruizione && request) {
			idProfiloSicurezzaMessaggioExpItem = CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RICHIESTA_EXPIRED;
		}
		else if(!fruizione && !request) {
			idProfiloSicurezzaMessaggioExpItem = CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RISPOSTA_EXPIRED;
		}
		if(idProfiloSicurezzaMessaggioExpItem!=null) {
			String v = getStringValue(protocolPropertyList, idProfiloSicurezzaMessaggioExpItem);
			if(v!=null && StringUtils.isNotEmpty(v)) {
				map.put(prefixKey+API_IMPL_SICUREZZA_MESSAGGIO_TTL, v);
			}
		}
		
		// Audit
		boolean audit = false;
		if(request) {
			String v = getStringValue(protocolPropertyList, CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RICHIESTA_AUDIENCE);
			String aud = null;
			if(v!=null) {
				aud = v;
			}else {
				aud = fruizione ? urlConnettoreFruitoreModI : urlInvocazione;
			}
			map.put(prefixKey+API_IMPL_SICUREZZA_MESSAGGIO_AUDIENCE, 
					aud);
			audit = true;
		}
		else {
			if(fruizione) {
				String stato = getBooleanValueAsStato(protocolPropertyList, CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RISPOSTA_AUDIENCE);
				if(stato!=null && StringUtils.isNotEmpty(stato)) {
					map.put(prefixKey+API_IMPL_SICUREZZA_MESSAGGIO_VERIFICA_AUDIENCE, stato);
					if(StatoFunzionalita.ABILITATO.getValue().equals(stato)) {
						
						audit = true;
						
						String v = getStringValue(protocolPropertyList, CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RISPOSTA_AUDIENCE_VALORE);
						if(v!=null && StringUtils.isNotEmpty(v)) {
							map.put(prefixKey+API_IMPL_SICUREZZA_MESSAGGIO_AUDIENCE, v);
						}
					}
				}
			}
		}
		
		// X5U URL
		if(x5u) {
			String id = null;
			if(request && fruizione) {
				id = CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_RICHIESTA_X509_VALUE_X5URL;
			}
			if(!request && !fruizione) {
				id = CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_RISPOSTA_X509_VALUE_X5URL;
			}
			if(id!=null) {
				String v = getStringValue(protocolPropertyList, id);
				if(v!=null && StringUtils.isNotEmpty(v)) {
					map.put(prefixKey+API_IMPL_SICUREZZA_MESSAGGIO_X5U_CERTIFICATE_URL, v);
				}
			}
		}
		
		// CorniceSicurezza
		if(patternDatiCorniceSicurezza!=null) {
			if(CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_PATTERN_OLD.equals(patternDatiCorniceSicurezza)) {
				
				String claimCodiceEnte = CostantiLabel.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_CODICE_ENTE;
				String idCodiceEnteMode = CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_CODICE_ENTE_MODE;
				String vCodiceEnteMode = getStringValue(protocolPropertyList, idCodiceEnteMode);
				if(vCodiceEnteMode!=null && CostantiDB.MODIPA_PROFILO_RIDEFINISCI.equals(vCodiceEnteMode)) {
					String idValue = CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_CODICE_ENTE;
					String value = getStringValue(protocolPropertyList, idValue);
					if(value!=null) {
						map.put(prefixKey+ API_IMPL_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_PREFIX+claimCodiceEnte, value);
					}
				}
				else {
					map.put(prefixKey+ API_IMPL_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_PREFIX+claimCodiceEnte, CostantiDB.MODIPA_PROFILO_DEFAULT);
				}
				
				String claimUser = CostantiLabel.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_USER;
				String idUserMode = CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_USER_MODE;
				String vUserMode = getStringValue(protocolPropertyList, idUserMode);
				if(vUserMode!=null && CostantiDB.MODIPA_PROFILO_RIDEFINISCI.equals(vUserMode)) {
					String idValue = CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_USER;
					String value = getStringValue(protocolPropertyList, idValue);
					if(value!=null) {
						map.put(prefixKey+ API_IMPL_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_PREFIX+claimUser, value);
					}
				}
				else {
					map.put(prefixKey+ API_IMPL_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_PREFIX+claimUser, CostantiDB.MODIPA_PROFILO_DEFAULT);
				}
				
				String claimUserIP = CostantiLabel.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_IP_USER;
				String idUserIPMode = CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_IP_USER_MODE;
				String vUserIPMode = getStringValue(protocolPropertyList, idUserIPMode);
				if(vUserIPMode!=null && CostantiDB.MODIPA_PROFILO_RIDEFINISCI.equals(vUserIPMode)) {
					String idValue = CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_IP_USER;
					String value = getStringValue(protocolPropertyList, idValue);
					if(value!=null) {
						map.put(prefixKey+ API_IMPL_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_PREFIX+claimUserIP, value);
					}
				}
				else {
					map.put(prefixKey+ API_IMPL_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_PREFIX+claimUserIP, CostantiDB.MODIPA_PROFILO_DEFAULT);
				}
				
			}
			else if(schemaDatiCorniceSicurezza!=null){
				
				String idAuditDifferent = CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RICHIESTA_AUDIT_AUDIENCE ;
				String v = getStringValue(protocolPropertyList, idAuditDifferent);
				if(CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RICHIESTA_AUDIT_AUDIENCE_VALUE_DIFFERENT.equals(v)) {
					String idAudit = CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RICHIESTA_AUDIENCE_CUSTOM_AUDIT;					
					v = getStringValue(protocolPropertyList, idAudit);
					if(v!=null && StringUtils.isNotEmpty(v)) {
						map.put(prefixKey+ API_IMPL_SICUREZZA_MESSAGGIO_AUDIT_AUDIENCE, v);
					}
				}	
				
				List<String> l = new ArrayList<>();
				for (ProtocolProperty pp : protocolPropertyList) {
					if(pp.getName().startsWith(CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_SCHEMA_MODE_PREFIX)) {
						String claim = pp.getName().substring(CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_SCHEMA_MODE_PREFIX.length());
						l.add(claim);
					}
				}
				if(l!=null && !l.isEmpty()) {
					for (String c : l) {
						String idMode = CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_SCHEMA_MODE_PREFIX+c;
						String vMode = getStringValue(protocolPropertyList, idMode);
						if(vMode!=null && CostantiDB.MODIPA_PROFILO_RIDEFINISCI.equals(vMode)) {
							String idValue = CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_SCHEMA_PREFIX+c;
							String value = getStringValue(protocolPropertyList, idValue);
							if(value!=null) {
								map.put(prefixKey+ API_IMPL_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_PREFIX+c, value);
							}
						}
						else {
							map.put(prefixKey+ API_IMPL_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_PREFIX+c, CostantiDB.MODIPA_PROFILO_DEFAULT);
						}
					}
				}
			}
		}
		
		// Header Duplicati
		if(rest && headerDuplicati && 
				( 
						(!request && fruizione)
						||
						(request && !fruizione)
				)
				&&
				audit) {
			String idAuditDifferent = request ? CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RICHIESTA_REST_DOPPI_HEADER_AUDIENCE  :
				CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RISPOSTA_REST_DOPPI_HEADER_AUDIENCE;
			String v = getStringValue(protocolPropertyList, idAuditDifferent);
			if(CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_DOPPI_HEADER_AUDIENCE_VALUE_DIFFERENT.equals(v)) {
				String idAudit = request ? CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RICHIESTA_REST_DOPPI_HEADER_AUDIENCE_INTEGRITY :
					CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RISPOSTA_REST_DOPPI_HEADER_AUDIENCE_INTEGRITY;					
				v = getStringValue(protocolPropertyList, idAudit);
				if(v!=null && StringUtils.isNotEmpty(v)) {
					map.put(prefixKey+ API_IMPL_SICUREZZA_MESSAGGIO_INTEGRITY_AUDIENCE, v);
				}
			}	
		}
		
		
		// Sicurezza OAuth
		
		if( fruizione && request ) {
			String v = getStringValue(protocolPropertyList, CostantiDB.MODIPA_PROFILO_SICUREZZA_OAUTH_IDENTIFICATIVO);
			if(v!=null && StringUtils.isNotEmpty(v)) {
				map.put(prefixKey+ API_IMPL_SICUREZZA_OAUTH_IDENTIFICATIVO, v);
			}
			v = getStringValue(protocolPropertyList, CostantiDB.MODIPA_PROFILO_SICUREZZA_OAUTH_KID);
			if(v!=null && StringUtils.isNotEmpty(v)) {
				map.put(prefixKey+ API_IMPL_SICUREZZA_OAUTH_KID, v);
			}
		}
		
		// TrustStore
		if( (fruizione && !request) || (!fruizione && request) ) {
			
			// truststore per i certificati
			addStore(map, protocolPropertyList, prefixKey, false, true);
			
			if(rest && x5u) {
				// ssl per le url (x5u)
				addStore(map, protocolPropertyList, prefixKey, true, false);
			}
			
		}
		
		// KeyStore
		if(!fruizione && !request) {
			addStore(map, protocolPropertyList, prefixKey, false, false);
		}
		
		if(fruizione && request) {
			
			boolean keystoreDefinitoInFruizione = false;
			String idFruizioneKeystoreFruizioneMode = CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_FRUIZIONE_KEYSTORE_MODE;
			String v = getStringValue(protocolPropertyList, idFruizioneKeystoreFruizioneMode);
			if(CostantiDB.MODIPA_KEYSTORE_FRUIZIONE.equals(v)) {
				keystoreDefinitoInFruizione = true;
				map.put(prefixKey+ API_IMPL_SICUREZZA_MESSAGGIO_FRUIZIONE_KEYSTORE_MODE, 
						CostantiLabel.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_FRUIZIONE_KEYSTORE_MODE_LABEL_FRUIZIONE);
			}
			else if(CostantiDB.MODIPA_KEYSTORE_FRUIZIONE_TOKEN_POLICY.equals(v)) {
				map.put(prefixKey+ API_IMPL_SICUREZZA_MESSAGGIO_FRUIZIONE_KEYSTORE_MODE, 
						CostantiLabel.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_FRUIZIONE_KEYSTORE_MODE_LABEL_TOKEN_POLICY);
			}
			else {
				// verifico di non essere nel caso di API con pattern ID_AUTH_REST_01 via PDND dove la modalità di scelta non viene indicata.
				if(v==null || StringUtils.isEmpty(v)) {
					String idFruizioneKeystoreMode = CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_KEYSTORE_MODE;
					String vMode = getStringValue(protocolPropertyList, idFruizioneKeystoreMode);
					if(CostantiDB.MODIPA_PROFILO_RIDEFINISCI.equals(vMode)) {
						keystoreDefinitoInFruizione = true;
					}
				}
				if(keystoreDefinitoInFruizione) {
					map.put(prefixKey+ API_IMPL_SICUREZZA_MESSAGGIO_FRUIZIONE_KEYSTORE_MODE, 
							CostantiLabel.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_FRUIZIONE_KEYSTORE_MODE_LABEL_FRUIZIONE);
				}
				else {
					map.put(prefixKey+ API_IMPL_SICUREZZA_MESSAGGIO_FRUIZIONE_KEYSTORE_MODE, 
							CostantiLabel.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_FRUIZIONE_KEYSTORE_MODE_LABEL_APPLICATIVO);
				}
			}
			
			if(keystoreDefinitoInFruizione) {
				addStore(map, protocolPropertyList, prefixKey, false, false);
			}
			
		}
	}
	
	public static String getPrefixKeyStore(boolean prefix, String prefixKey, boolean ssl, boolean truststore) {
		String s = null;
		if(ssl) {
			s = prefixKey+"truststore-ssl";
		}
		else if(truststore) {
			s = prefixKey+"truststore";
		}
		else {
			s = prefixKey+"keystore";
		}
		return prefix ? s+"-" : s;
	}
	
	private static void addStore(Map<String, String> map, List<ProtocolProperty> protocolPropertyList,
			String prefixKeyParam, boolean ssl, boolean truststore) {
		
		String id = null;
		if(ssl) {
			id = CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SSL_TRUSTSTORE_MODE; 
		}
		else if(truststore) {
			id = CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_MODE;
		}
		else {
			id = CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_KEYSTORE_MODE;
		}
		
		String v = getStringValue(protocolPropertyList, id);
		if(v==null  || StringUtils.isEmpty(v) || CostantiDB.MODIPA_PROFILO_DEFAULT.equals(v)) {
			// aggiungo informazione solo se presente
			String store = getPrefixKeyStore(false, prefixKeyParam, ssl, truststore);
			if(CostantiDB.MODIPA_PROFILO_DEFAULT.equals(v)) {
				map.put(store,CostantiLabel.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_STORE_MODE_LABEL_DEFAULT);
			}
		}
		else {
			
			String prefixKey = getPrefixKeyStore(true, prefixKeyParam, ssl, truststore);
						
			String type = null;
			String path = null;
			String crl = null;
			String ocsp = null;
			String aliasKey = null;
			boolean keystoreModePath = false;
			boolean keystoreModeArchive = false;
			boolean keystoreModeHsm = false;
			if(ssl) {
				type = CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SSL_TRUSTSTORE_TYPE;
				path = CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SSL_TRUSTSTORE_PATH;
				crl = CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SSL_TRUSTSTORE_CRLS;
				ocsp = CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SSL_TRUSTSTORE_OCSP_POLICY;
			}
			else if(truststore) {
				type = CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_TYPE;
				path = CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_PATH;
				crl = CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_CRLS;
				ocsp = CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_OCSP_POLICY;
			}
			else {
				type = CostantiDB.MODIPA_KEYSTORE_TYPE;
				aliasKey = CostantiDB.MODIPA_KEY_ALIAS;
				String mode = getStringValue(protocolPropertyList, CostantiDB.MODIPA_KEYSTORE_MODE);
				if(CostantiDB.MODIPA_KEYSTORE_MODE_VALUE_ARCHIVE.equals(mode)) {
					keystoreModeArchive = true;
				}
				else if(CostantiDB.MODIPA_KEYSTORE_MODE_VALUE_PATH.equals(mode)) {
					keystoreModePath = true;
					path = CostantiDB.MODIPA_KEYSTORE_PATH;
				}
				else if(CostantiDB.MODIPA_KEYSTORE_MODE_VALUE_HSM.equals(mode)) {
					keystoreModeHsm = true;
				}
			}
			
			String vType = null;
			if(type!=null) {
				vType = getStringValue(protocolPropertyList, type);
				map.put(prefixKey+ API_IMPL_SICUREZZA_MESSAGGIO_STORE_TYPE,vType);
			}
			
			if(keystoreModePath) {
				// nop
			}
			
			boolean hsm = false;
			if(ssl || truststore) {
				if(vType!=null) {
					hsm = HSMUtils.isKeystoreHSM(vType);
				}
			}
			else {
				hsm = keystoreModeHsm;
			}
			map.put(prefixKey+HSM,hsm+"");
			
			if(hsm) {
				map.put(prefixKey+ API_IMPL_SICUREZZA_MESSAGGIO_STORE_PATH,CostantiLabel.STORE_HSM);
			}
			else if(path!=null) {
				String vPath = getStringValue(protocolPropertyList, path);
				map.put(prefixKey+ API_IMPL_SICUREZZA_MESSAGGIO_STORE_PATH,vPath);
			}
			else if(keystoreModeArchive) {
				map.put(prefixKey+ API_IMPL_SICUREZZA_MESSAGGIO_STORE_PATH,CostantiLabel.STORE_CARICATO_BASEDATI);
			}
			
			if(crl!=null) {
				String vCrl = getStringValue(protocolPropertyList, crl);
				map.put(prefixKey+ API_IMPL_SICUREZZA_MESSAGGIO_STORE_CRLS,vCrl);
			}
			
			if(ocsp!=null) {
				String vOcsp = getStringValue(protocolPropertyList, ocsp);
				map.put(prefixKey+ API_IMPL_SICUREZZA_MESSAGGIO_STORE_OCSP_POLICY,vOcsp);
			}
			
			if(aliasKey!=null) {
				String vAliasKey = getStringValue(protocolPropertyList, aliasKey);
				map.put(prefixKey+ API_IMPL_SICUREZZA_MESSAGGIO_KEY_ALIAS,vAliasKey);
			}
						
		}
		
	}
		
	private static boolean isProfiloSicurezzaMessaggioConIntegritaX509(AccordoServizioParteComune api, String portType) {
		List<String> tmp = RegistroServiziUtils.fillPropertyProtocollo(CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO, api, portType, false);
		if(tmp!=null && !tmp.isEmpty()) {
			for (String profiloSicurezzaMessaggio : tmp) {
				if(CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM0301.equals(profiloSicurezzaMessaggio) ||
						CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM0302.equals(profiloSicurezzaMessaggio)) {
					return true;
				}		
			}
		}
		return false;
	}
	private static boolean isProfiloSicurezzaMessaggioConIntegritaKid(AccordoServizioParteComune api, String portType) {
		List<String> tmp = RegistroServiziUtils.fillPropertyProtocollo(CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO, api, portType, false);
		if(tmp!=null && !tmp.isEmpty()) {
			for (String profiloSicurezzaMessaggio : tmp) {
				if(CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM0401.equals(profiloSicurezzaMessaggio) ||
						CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM0402.equals(profiloSicurezzaMessaggio)) {
					return true;
				}		
			}
		}
		return false;
	}
	private static boolean isProfiloSicurezzaMessaggioCorniceSicurezza(AccordoServizioParteComune api, String portType) {
		List<String> tmp = RegistroServiziUtils.fillPropertyProtocollo(CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA, api, portType, true);
		if(tmp!=null && !tmp.isEmpty()) {
			for (String v : tmp) {
				if(v!=null && "true".equals(v)) {
					return true;
				}
			}
		}
		return false;
	}
	private static String getProfiloSicurezzaMessaggioCorniceSicurezzaPattern(AccordoServizioParteComune api, String portType) {
		List<String> tmp = RegistroServiziUtils.fillPropertyProtocollo(CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_PATTERN, api, portType, false);
		if(tmp!=null && !tmp.isEmpty()) {
			return tmp.get(0);
		}
		return null;
	}
	private static String getProfiloSicurezzaMessaggioCorniceSicurezzaSchema(AccordoServizioParteComune api, String portType) {
		List<String> tmp = RegistroServiziUtils.fillPropertyProtocollo(CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_SCHEMA, api, portType, false);
		if(tmp!=null && !tmp.isEmpty()) {
			return tmp.get(0);
		}
		return null;
	}
	private static boolean isProfiloSicurezzaMessaggioRequestDigest(AccordoServizioParteComune api, String portType) {
		List<String> tmp = RegistroServiziUtils.fillPropertyProtocollo(CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RISPOSTA_REQUEST_DIGEST, api, portType, true);
		if(tmp!=null && !tmp.isEmpty()) {
			for (String v : tmp) {
				if(v!=null && "true".equals(v)) {
					return true;
				}
			}
		}
		return false;
	}
	private static boolean isProfiloSicurezzaMessaggioConHeaderDuplicati(AccordoServizioParteComune api, String portType) {
		List<String> tmp = RegistroServiziUtils.fillPropertyProtocollo(CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER, api, portType, false);
		if(tmp!=null && !tmp.isEmpty()) {
			for (String profiloSicurezzaMessaggio : tmp) {
				if(CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_VALUE_AUTHORIZATION_MODIPA.equals(profiloSicurezzaMessaggio) ||
						CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_VALUE_AUTHORIZATION_MODIPA_AUTH_IN_RESPONSE.equals(profiloSicurezzaMessaggio) ||
						CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_VALUE_AUTHORIZATION_CUSTOM.equals(profiloSicurezzaMessaggio) ||
						CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_VALUE_AUTHORIZATION_CUSTOM_AUTH_IN_RESPONSE.equals(profiloSicurezzaMessaggio)) {
					return true;
				}		
			}
		}
		return false;
	}
	
	// NOTA: tutti i metodi seguenti devono essere riportati in org.openspcoop2.protocol.engine.utils.ModITestUtils
	public static final String CLASS_MODIPA_PROPERTIES = ModIPropertiesUtils.CLASS_MODIPA_PROPERTIES;
	
	public static Object getModiProperties() throws ProtocolException {
		return ModIPropertiesUtils.getModiProperties();
	}
	
	public static String getHeaderModI() throws ProtocolException {
		return ModIPropertiesUtils.getHeaderModI();
	}
	
	public static boolean isTokenOAuthUseJtiIntegrityAsMessageId() throws ProtocolException {
		return ModIPropertiesUtils.isTokenOAuthUseJtiIntegrityAsMessageId();
	}
	
	
	public static ModIPDNDClientConfig getAPIPDNDClientConfig() throws ProtocolException {
		return ModIPropertiesUtils.getAPIPDNDClientConfig();
	}
	public static ModIPDNDClientConfig getAPIPDNDClientConfig(Logger log) throws ProtocolException {
		return ModIPropertiesUtils.getAPIPDNDClientConfig(log);
	}
	public static ModIPDNDClientConfig getAPIPDNDClientConfig(String details) throws ProtocolException {
		return ModIPropertiesUtils.getAPIPDNDClientConfig(details);
	}
	public static ModIPDNDClientConfig getAPIPDNDClientConfig(String details, Logger log) throws ProtocolException {
		return ModIPropertiesUtils.getAPIPDNDClientConfig(details, log);
	}
	
	public static ModIPDNDOrganizationConfig getAPIPDNDOrganizationConfig() throws ProtocolException {
		return ModIPropertiesUtils.getAPIPDNDOrganizationConfig();
	}
	public static ModIPDNDOrganizationConfig getAPIPDNDOrganizationConfig(Logger log) throws ProtocolException {
		return ModIPropertiesUtils.getAPIPDNDOrganizationConfig(log);
	}
	public static ModIPDNDOrganizationConfig getAPIPDNDOrganizationConfig(String details) throws ProtocolException {
		return ModIPropertiesUtils.getAPIPDNDOrganizationConfig(details);
	}
	public static ModIPDNDOrganizationConfig getAPIPDNDOrganizationConfig(String details, Logger log) throws ProtocolException {
		return ModIPropertiesUtils.getAPIPDNDOrganizationConfig(details, log);
	}

	
	
	public static IDAccordo buildSignalHubPushIdAPI(IDSoggetto idSoggettoDefault) throws ProtocolException {
		return ModIPropertiesUtils.buildSignalHubPushIdAPI(idSoggettoDefault);
	}
	
	public static boolean isSignalHubEnabled() throws ProtocolException {
		return ModIPropertiesUtils.isSignalHubEnabled();
	}
	
	public static boolean isTracingPDNDEnabled() throws ProtocolException {
		return ModIPropertiesUtils.isTracingPDNDEnabled();
	}
	public static boolean isTracingPDNDEnabledSafe() {
		try {
			return isTracingPDNDEnabled();
		}catch(Exception e) {
			return false;
		}
	}
		
	public static List<RemoteStoreConfig> getRemoteStoreConfig() throws ProtocolException {
		return ModIPropertiesUtils.getRemoteStoreConfig();
	}
	
	public static RemoteKeyType getRemoteKeyType(String name) throws ProtocolException {
		return ModIPropertiesUtils.getRemoteKeyType(name);
	}
	
	public static KeystoreParams getSicurezzaMessaggioCertificatiTrustStore() throws ProtocolException {
		return ModIPropertiesUtils.getSicurezzaMessaggioCertificatiTrustStore();
	}
	
	public static KeystoreParams getSicurezzaMessaggioSslTrustStore() throws ProtocolException {
		return ModIPropertiesUtils.getSicurezzaMessaggioSslTrustStore();
	}
	
	public static KeystoreParams getSicurezzaMessaggioCertificatiKeyStore() throws ProtocolException {
		return ModIPropertiesUtils.getSicurezzaMessaggioCertificatiKeyStore();
	}
	
	public static KeystoreParams getApplicativoKeystoreParams(List<org.openspcoop2.core.config.ProtocolProperty> protocolPropertyList) {
		
		if(protocolPropertyList==null || protocolPropertyList.isEmpty()) {
			return null;
		}
		
		KeystoreParams keystoreParams = null;
		
		String sicurezza = getStringValueConfig(protocolPropertyList,CostantiDB.MODIPA_SICUREZZA_MESSAGGIO);
		if("true".equals(sicurezza)) {
			
			boolean keystoreModePath = false;
			boolean keystoreModeArchive = false;
			boolean keystoreModeHsm = false;
			String mode = getStringValueConfig(protocolPropertyList, CostantiDB.MODIPA_KEYSTORE_MODE);
			String path =null;
			String type = CostantiDB.MODIPA_KEYSTORE_TYPE;
			if(CostantiDB.MODIPA_KEYSTORE_MODE_VALUE_ARCHIVE.equals(mode)) {
				keystoreModeArchive = true;
			}
			else if(CostantiDB.MODIPA_KEYSTORE_MODE_VALUE_PATH.equals(mode)) {
				keystoreModePath = true;
				path = CostantiDB.MODIPA_KEYSTORE_PATH;
			}
			else if(CostantiDB.MODIPA_KEYSTORE_MODE_VALUE_HSM.equals(mode)) {
				keystoreModeHsm = true;
			}
			
			String vType = null;
			if(type!=null) {
				vType = getStringValueConfig(protocolPropertyList, type);
			}
			
			if(keystoreModePath) {
				// nop
			}
			
			String vPath = null;
			byte[] vStore = null;
			if(keystoreModeHsm) {
				vPath = CostantiLabel.STORE_HSM;
			}
			else if(path!=null) {
				vPath = getStringValueConfig(protocolPropertyList, path);
			}
			else if(keystoreModeArchive) {
				vPath = CostantiLabel.STORE_CARICATO_BASEDATI;
				vStore = getBinaryValueConfig(protocolPropertyList, CostantiDB.MODIPA_KEYSTORE_ARCHIVE);
			}
			
			String pw = CostantiDB.MODIPA_KEYSTORE_PASSWORD;
			String vPassword = getStringValueConfig(protocolPropertyList, pw);
						
			String aliasKey = CostantiDB.MODIPA_KEY_ALIAS;
			String vAliasKey = getStringValueConfig(protocolPropertyList, aliasKey);
			
			String passwordKey = CostantiDB.MODIPA_KEY_PASSWORD;
			String vPassordKey = getStringValueConfig(protocolPropertyList, passwordKey);
			
			String vPathPublicKey = null;
			if(KeystoreType.KEY_PAIR.getNome().equals(vType)) {
				vPathPublicKey = getStringValueConfig(protocolPropertyList, CostantiDB.MODIPA_KEYSTORE_PATH_PUBLIC_KEY);
			}
			
			String vKeyPairAlgorithm = null;
			if(KeystoreType.KEY_PAIR.getNome().equals(vType) || KeystoreType.PUBLIC_KEY.getNome().equals(vType)) {
				vKeyPairAlgorithm = getStringValueConfig(protocolPropertyList, CostantiDB.MODIPA_KEYSTORE_KEY_ALGORITHM);
			}
			
			String byok = CostantiDB.MODIPA_KEYSTORE_BYOK_POLICY;
			String vByok = null;
			if(!keystoreModeArchive && !keystoreModeHsm) {
				vByok = getStringValueConfig(protocolPropertyList, byok);
			}
			
			keystoreParams = new KeystoreParams();
			keystoreParams.setType(vType);
			keystoreParams.setPath(vPath);
			keystoreParams.setStore(vStore);
			keystoreParams.setPassword(vPassword);
			keystoreParams.setKeyAlias(vAliasKey);
			keystoreParams.setKeyPassword(vPassordKey);
			keystoreParams.setKeyPairPublicKeyPath(vPathPublicKey);
			keystoreParams.setKeyPairAlgorithm(vKeyPairAlgorithm);
			keystoreParams.setByokPolicy(vByok);
			
		}
	
		return keystoreParams;
	}
	
	public static byte[] getApplicativoKeystoreCertificate(List<org.openspcoop2.core.config.ProtocolProperty> protocolPropertyList) {
		
		byte[] b = null;
		
		if(protocolPropertyList==null || protocolPropertyList.isEmpty()) {
			return b;
		}
		
		String sicurezza = getStringValueConfig(protocolPropertyList,CostantiDB.MODIPA_SICUREZZA_MESSAGGIO);
		if("true".equals(sicurezza)) {
			
			return getBinaryValueConfig(protocolPropertyList, CostantiDB.MODIPA_KEYSTORE_CERTIFICATE);
			
		}
	
		return b;
	}
	
	public static boolean existsStoreConfig(List<org.openspcoop2.core.registry.ProtocolProperty> protocolPropertyList, boolean includeRemoteStore,
			boolean checkModeFruizioneKeystoreId) throws ProtocolException {
		KeystoreParams keystoreParams = null;
		try { 
			keystoreParams = ModIUtils.getKeyStoreParams(protocolPropertyList,
					checkModeFruizioneKeystoreId);
		}catch(Exception e) {
			throw new ProtocolException(e.getMessage(),e);
		}
		KeystoreParams truststoreParams = null;
		try { 
			truststoreParams = ModIUtils.getTrustStoreParams(protocolPropertyList);
		}catch(Exception e) {
			throw new ProtocolException(e.getMessage(),e);
		}
		KeystoreParams truststoreSslParams = null;
		try { 
			truststoreSslParams = ModIUtils.getTrustStoreSSLParams(protocolPropertyList);
		}catch(Exception e) {
			throw new ProtocolException(e.getMessage(),e);
		}
		
		boolean existsStore = keystoreParams!=null || truststoreParams!=null || truststoreSslParams!=null;
		if(!includeRemoteStore) {
			return existsStore;
		}
		
		List<RemoteStoreConfig> trustStoreRemoteConfig = null;
		try {
			trustStoreRemoteConfig = ModIUtils.getRemoteStoreConfig();
		}catch(Exception e) {
			throw new ProtocolException(e.getMessage(),e);
		}
		
		return existsStore || (trustStoreRemoteConfig!=null && !trustStoreRemoteConfig.isEmpty());
	}
	
	public static KeystoreParams getKeyStoreParams(List<org.openspcoop2.core.registry.ProtocolProperty> protocolPropertyList,
			boolean checkModeFruizioneKeystoreId) throws ProtocolException {
		return getKeystoreParamsEngine(protocolPropertyList, false, false,
				checkModeFruizioneKeystoreId);
	}
	public static KeystoreParams getTrustStoreParams(List<org.openspcoop2.core.registry.ProtocolProperty> protocolPropertyList) throws ProtocolException {
		return getKeystoreParamsEngine(protocolPropertyList, false, true,
				false);
	}
	public static KeystoreParams getTrustStoreSSLParams(List<org.openspcoop2.core.registry.ProtocolProperty> protocolPropertyList) throws ProtocolException {
		return getKeystoreParamsEngine(protocolPropertyList, true, false,
				false);
	}
	private static KeystoreParams getKeystoreParamsEngine(List<org.openspcoop2.core.registry.ProtocolProperty> protocolPropertyList,
			boolean ssl, boolean truststore,
			boolean checkModeFruizioneKeystoreId) throws ProtocolException {
				
		if(protocolPropertyList==null || protocolPropertyList.isEmpty()) {
			return null;
		}
		
		if(checkModeFruizioneKeystoreId) {
			String v = getStringValue(protocolPropertyList, CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_FRUIZIONE_KEYSTORE_MODE);
			if(
					v!=null && !StringUtils.isEmpty(v) && // aggiungo questo controllo per evitare che nel caso di API con solo pattern ID_AUTH_REST_01 via PDND dove non viene visualizzato 'MODIPA_PROFILO_SICUREZZA_MESSAGGIO_FRUIZIONE_KEYSTORE_MODE' si esca e non si controlli i certificati
					!CostantiDB.MODIPA_KEYSTORE_FRUIZIONE.equals(v)) {
				return null;
			}
		}
		
		KeystoreParams keystoreParams = null;
		
		String id = null;
		if(ssl) {
			id = CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SSL_TRUSTSTORE_MODE; 
		}
		else if(truststore) {
			id = CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_MODE;
		}
		else {
			id = CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_KEYSTORE_MODE;
		}
		
		String v = getStringValue(protocolPropertyList, id);
		if(v!=null && !StringUtils.isEmpty(v) && !CostantiDB.MODIPA_PROFILO_UNDEFINED.equals(v)) {
			if(CostantiDB.MODIPA_PROFILO_DEFAULT.equals(v)) {
				if(ssl) {
					keystoreParams = ModIUtils.getSicurezzaMessaggioSslTrustStore();
				}
				else if(truststore) {
					keystoreParams = ModIUtils.getSicurezzaMessaggioCertificatiTrustStore();
				}
				else {
					keystoreParams = ModIUtils.getSicurezzaMessaggioCertificatiKeyStore();
				}
			}
			else if(CostantiDB.MODIPA_PROFILO_RIDEFINISCI.equals(v)) {
				
				String type = null;
				String path = null;
				String archive = null;
				String pw = null;
				String crl = null;
				String ocsp = null;
				String aliasKey = null;
				String pwKey = null;
				String byok = null;
				boolean keystoreModePath = false;
				boolean keystoreModeArchive = false;
				boolean keystoreModeHsm = false;
				if(ssl) {
					type = CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SSL_TRUSTSTORE_TYPE;
					path = CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SSL_TRUSTSTORE_PATH;
					pw = CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SSL_TRUSTSTORE_PASSWORD;
					crl = CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SSL_TRUSTSTORE_CRLS;
					ocsp = CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SSL_TRUSTSTORE_OCSP_POLICY;
				}
				else if(truststore) {
					type = CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_TYPE;
					path = CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_PATH;
					pw = CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_PASSWORD;
					crl = CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_CRLS;
					ocsp = CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_OCSP_POLICY;
				}
				else {
					type = CostantiDB.MODIPA_KEYSTORE_TYPE;
					pw = CostantiDB.MODIPA_KEYSTORE_PASSWORD;
					aliasKey = CostantiDB.MODIPA_KEY_ALIAS;
					pwKey = CostantiDB.MODIPA_KEY_PASSWORD;
					String mode = getStringValue(protocolPropertyList, CostantiDB.MODIPA_KEYSTORE_MODE);
					if(CostantiDB.MODIPA_KEYSTORE_MODE_VALUE_ARCHIVE.equals(mode)) {
						keystoreModeArchive = true;
						archive = CostantiDB.MODIPA_KEYSTORE_ARCHIVE;
					}
					else if(CostantiDB.MODIPA_KEYSTORE_MODE_VALUE_PATH.equals(mode)) {
						keystoreModePath = true;
						path = CostantiDB.MODIPA_KEYSTORE_PATH;
						byok = CostantiDB.MODIPA_KEYSTORE_BYOK_POLICY;
					}
					else if(CostantiDB.MODIPA_KEYSTORE_MODE_VALUE_HSM.equals(mode)) {
						keystoreModeHsm = true;
					}
				}
				
				String vType = null;
				if(type!=null) {
					vType = getStringValue(protocolPropertyList, type);
				}
				
				if(keystoreModePath) {
					// nop
				}
				
				String vPassword = null;
				if(pw!=null) {
					vPassword = getStringValue(protocolPropertyList, pw);
				}
				
				boolean hsm = false;
				if(ssl || truststore) {
					if(vType!=null) {
						hsm = HSMUtils.isKeystoreHSM(vType);
					}
				}
				else {
					hsm = keystoreModeHsm;
				}
				
				String vPath = null;
				byte[] vStore = null;
				if(hsm) {
					vPath = CostantiLabel.STORE_HSM;
				}
				else if(path!=null) {
					vPath = getStringValue(protocolPropertyList, path);
				}
				else if(keystoreModeArchive) {
					vPath = CostantiLabel.STORE_CARICATO_BASEDATI;
					vStore = getBinaryValue(protocolPropertyList, archive);
				}
				
				String vCrl = null;
				if(crl!=null) {
					vCrl = getStringValue(protocolPropertyList, crl);
				}
				
				String vOcsp = null;
				if(ocsp!=null) {
					vOcsp = getStringValue(protocolPropertyList, ocsp);
				}
				
				String vAliasKey = null;
				if(aliasKey!=null) {
					vAliasKey = getStringValue(protocolPropertyList, aliasKey);
				}
				
				String vPasswordKey = null;
				if(pwKey!=null) {
					vPasswordKey = getStringValue(protocolPropertyList, pwKey);
				}
				
				String vPathPublicKey = null;
				if(KeystoreType.KEY_PAIR.getNome().equals(vType)) {
					vPathPublicKey = getStringValue(protocolPropertyList, CostantiDB.MODIPA_KEYSTORE_PATH_PUBLIC_KEY);
				}
				
				String vKeyPairAlgorithm = null;
				if(KeystoreType.KEY_PAIR.getNome().equals(vType) || KeystoreType.PUBLIC_KEY.getNome().equals(vType)) {
					vKeyPairAlgorithm = getStringValue(protocolPropertyList, CostantiDB.MODIPA_KEYSTORE_KEY_ALGORITHM);
				}
				
				String vByok = null;
				if(byok!=null) {
					vByok = getStringValue(protocolPropertyList, byok);
				}
				
				keystoreParams = new KeystoreParams();
				keystoreParams.setType(vType);
				keystoreParams.setPath(vPath);
				keystoreParams.setStore(vStore);
				keystoreParams.setPassword(vPassword);
				keystoreParams.setCrls(vCrl);
				keystoreParams.setOcspPolicy(vOcsp);
				keystoreParams.setKeyAlias(vAliasKey);
				keystoreParams.setKeyPassword(vPasswordKey);
				keystoreParams.setKeyPairPublicKeyPath(vPathPublicKey);
				keystoreParams.setKeyPairAlgorithm(vKeyPairAlgorithm);
				keystoreParams.setByokPolicy(vByok);
			}
		}
	
		return keystoreParams;
	}

	
	private static String getStringValue(List<ProtocolProperty> protocolPropertyList, String id) {
		for (ProtocolProperty protocolProperty : protocolPropertyList) {
			if(protocolProperty.getName().equals(id)) {
				if(StringUtils.isNotEmpty(protocolProperty.getValue())) {
					return protocolProperty.getValue();
				}
				else if(protocolProperty.getNumberValue()!=null) {
					return protocolProperty.getNumberValue().toString();
				}
				else if(protocolProperty.getBooleanValue()!=null) {
					return protocolProperty.getBooleanValue().toString();
				}
				else if(protocolProperty.getByteFile()!=null) {
					return "Archivio binario";
				}
				else {
					return null;
				}
			}
		}
		return null;
	}
	private static String getBooleanValueAsStato(List<ProtocolProperty> protocolPropertyList, String id) {
		for (ProtocolProperty protocolProperty : protocolPropertyList) {
			if(protocolProperty.getName().equals(id)) {
				return protocolProperty.getBooleanValue()!=null && protocolProperty.getBooleanValue() ? 
						StatoFunzionalita.ABILITATO.getValue() : StatoFunzionalita.DISABILITATO.getValue();
			}
		}
		return null;
	}
	static byte[] getBinaryValue(List<ProtocolProperty> protocolPropertyList, String id) {
		byte[] b = null;
		for (ProtocolProperty protocolProperty : protocolPropertyList) {
			if(protocolProperty.getName().equals(id)) {
				return protocolProperty.getByteFile();
			}
		}
		return b;
	}
	
	private static String getStringValueConfig(List<org.openspcoop2.core.config.ProtocolProperty> protocolPropertyList, String id) {
		for (org.openspcoop2.core.config.ProtocolProperty protocolProperty : protocolPropertyList) {
			if(protocolProperty.getName().equals(id)) {
				if(StringUtils.isNotEmpty(protocolProperty.getValue())) {
					return protocolProperty.getValue();
				}
				else if(protocolProperty.getNumberValue()!=null) {
					return protocolProperty.getNumberValue().toString();
				}
				else if(protocolProperty.getBooleanValue()!=null) {
					return protocolProperty.getBooleanValue().toString();
				}
				else if(protocolProperty.getByteFile()!=null) {
					return "Archivio binario";
				}
				else {
					return null;
				}
			}
		}
		return null;
	}
	static String getBooleanValueAsStatoConfig(List<org.openspcoop2.core.config.ProtocolProperty> protocolPropertyList, String id) {
		for (org.openspcoop2.core.config.ProtocolProperty protocolProperty : protocolPropertyList) {
			if(protocolProperty.getName().equals(id)) {
				return protocolProperty.getBooleanValue()!=null && protocolProperty.getBooleanValue() ? 
						StatoFunzionalita.ABILITATO.getValue() : StatoFunzionalita.DISABILITATO.getValue();
			}
		}
		return null;
	}
	private static byte[] getBinaryValueConfig(List<org.openspcoop2.core.config.ProtocolProperty> protocolPropertyList, String id) {
		byte[] b = null;
		for (org.openspcoop2.core.config.ProtocolProperty protocolProperty : protocolPropertyList) {
			if(protocolProperty.getName().equals(id)) {
				return protocolProperty.getByteFile();
			}
		}
		return b;
	}
	
	public static final String PROPRIETA_INTERMEDIARIO = "intermediario";
	public static boolean isSoggettoCanaleIntermediario(Soggetto soggettoCanale, Logger log) {
		try {
			if(soggettoCanale.sizeProprietaList()>0) {
				for (Proprieta proprieta :soggettoCanale.getProprieta()) {
					if(PROPRIETA_INTERMEDIARIO.equals(proprieta.getNome())) {
						return "true".equalsIgnoreCase(proprieta.getValore());
					}
				}
			}
		}catch(Exception e) {
			log.error("[isSoggettoCanaleIntermediario] Process failed: "+e.getMessage(),e);
		}
		return false;
	}
	
	public static String getMessaggioErroreDominioCanaleDifferenteDominioApplicativo(IDServizioApplicativo idServizioApplicativoMessaggio, IDSoggetto idSoggettoMittenteCanale) {
		return "Token di sicurezza firmato da un applicativo '"+idServizioApplicativoMessaggio.getNome()+
				"' risiedente nel dominio del soggetto '"+idServizioApplicativoMessaggio.getIdSoggettoProprietario().toString()+"'; il dominio differisce dal soggetto identificato sul canale di trasporto ("+idSoggettoMittenteCanale+
				")";
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
	
	public static String convertProfiloSicurezzaSorgenteTokenToSDKValue(String sorgenteToken) {
		if(CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SORGENTE_TOKEN_IDAUTH_VALUE_LOCALE.equals(sorgenteToken)) {
			return CostantiLabel.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SORGENTE_TOKEN_IDAUTH_LOCALE;
		}
		else if(CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SORGENTE_TOKEN_IDAUTH_VALUE_PDND.equals(sorgenteToken)) {
			return CostantiLabel.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SORGENTE_TOKEN_IDAUTH_PDND;
		}
		else if(CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SORGENTE_TOKEN_IDAUTH_VALUE_OAUTH.equals(sorgenteToken)) {
			return CostantiLabel.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SORGENTE_TOKEN_IDAUTH_OAUTH;
		}
		else {
			// backward compatibility
			return CostantiLabel.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SORGENTE_TOKEN_IDAUTH_LOCALE;
		}
	}
	public static String convertProfiloSicurezzaSorgenteTokenToConfigurationValue(String sorgenteTokenSDKValue) throws ProtocolException {
		if(CostantiLabel.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SORGENTE_TOKEN_IDAUTH_LOCALE.equals(sorgenteTokenSDKValue)) {
			return CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SORGENTE_TOKEN_IDAUTH_VALUE_LOCALE;
		}
		else if(CostantiLabel.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SORGENTE_TOKEN_IDAUTH_PDND.equals(sorgenteTokenSDKValue)) {
			return CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SORGENTE_TOKEN_IDAUTH_VALUE_PDND;
		}
		else if(CostantiLabel.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SORGENTE_TOKEN_IDAUTH_OAUTH.equals(sorgenteTokenSDKValue)) {
			return CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SORGENTE_TOKEN_IDAUTH_VALUE_OAUTH;
		}
		throw newProtocolExceptionUnknown("Source label",sorgenteTokenSDKValue);
	}
	
	
	public static String convertProfiloAuditToSDKValue(String patternCorniceSicurezza) throws ProtocolException {
		if(CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_PATTERN_OLD.equals(patternCorniceSicurezza)) {
			return CostantiLabel.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_PATTERN_OLD;
		}
		else if(CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_PATTERN_AUDIT_REST_01.equals(patternCorniceSicurezza)) {
			return CostantiLabel.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_PATTERN_AUDIT_REST_01;
		}
		else if(CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_PATTERN_AUDIT_REST_02.equals(patternCorniceSicurezza)) {
			return CostantiLabel.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_PATTERN_AUDIT_REST_02;
		}
		throw newProtocolExceptionUnknown("Pattern value",patternCorniceSicurezza);
	}
	public static String convertProfiloAuditToConfigurationValue(String patternCorniceSicurezzaSDKValue) throws ProtocolException {
		if(CostantiLabel.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_PATTERN_OLD.equals(patternCorniceSicurezzaSDKValue)) {
			return CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_PATTERN_OLD;
		}
		else if(CostantiLabel.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_PATTERN_AUDIT_REST_01.equals(patternCorniceSicurezzaSDKValue)) {
			return CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_PATTERN_AUDIT_REST_01;
		}
		else if(CostantiLabel.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_PATTERN_AUDIT_REST_02.equals(patternCorniceSicurezzaSDKValue)) {
			return CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_PATTERN_AUDIT_REST_02;
		}
		throw newProtocolExceptionUnknown("Pattern label",patternCorniceSicurezzaSDKValue);
	}
	
	public static ProtocolException newProtocolExceptionUnknown(String prefix, String value) {
		return new ProtocolException(prefix+" '"+value+"' unknown");
	}
	
	
	public static final MapKey<String> MODIPA_OPENSPCOOP2_MSG_CONTEXT_USE_JTI_AUTHORIZATION = org.openspcoop2.utils.Map.newMapKey("MODIPA_USE_JTI_AUTHORIZATION");
	public static boolean useJtiAuthorizationObject(OpenSPCoop2Message msg) {
		Object useJtiAuthorizationObject = msg.getContextProperty(MODIPA_OPENSPCOOP2_MSG_CONTEXT_USE_JTI_AUTHORIZATION);
		boolean useJtiAuthorization = false;
		if(useJtiAuthorizationObject instanceof Boolean) {
			useJtiAuthorization = (Boolean) useJtiAuthorizationObject;
		}
		return useJtiAuthorization;
	}
	public static void replaceBustaIdWithJtiTokenId(ModIValidazioneSemanticaProfiloSicurezza modIValidazioneSemanticaProfiloSicurezza, String jtiClaimReceived) {
		if(jtiClaimReceived!=null && !StringUtils.isEmpty(jtiClaimReceived)) {
			Busta busta = modIValidazioneSemanticaProfiloSicurezza.getBusta();
			String idIntegrity = busta.getID();
			busta.removeProperty(CostantiDB.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO_ID);
			if(modIValidazioneSemanticaProfiloSicurezza.isSicurezzaMessaggio()) {
				busta.addProperty(CostantiDB.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO_ID, idIntegrity);
			}
			busta.setID(jtiClaimReceived);
		}
	}
	
	public static String readJti(String token, Logger log) {
		try {
			if(token!=null && token.contains(".")) {
				String [] tmp = token.split("\\.");
				if(tmp!=null && tmp.length>=3) {
					String jsonBase64 = tmp[1];
					String json = new String(Base64Utilities.decode(jsonBase64.getBytes()));
					return JsonPathExpressionEngine.extractAndConvertResultAsString(json, "$.jti", log);
				}
			}
		}catch(Exception e) {
			// ignore
		}
		return null;
	}

}
