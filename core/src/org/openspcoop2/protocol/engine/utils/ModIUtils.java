/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it).
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

package org.openspcoop2.protocol.engine.utils;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.constants.CostantiLabel;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.Fruitore;
import org.openspcoop2.core.registry.ProtocolProperty;
import org.openspcoop2.core.registry.constants.ServiceBinding;
import org.openspcoop2.core.registry.utils.RegistroServiziUtils;
import org.openspcoop2.security.message.constants.SecurityConstants;
import org.openspcoop2.security.message.constants.SignatureC14NAlgorithm;
import org.openspcoop2.utils.certificate.hsm.HSMUtils;

/**
 * ModIUtils
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ModIUtils {

	// COSTANTI boolean
	public static final String INTEGRITY = "integrity";
	public static final String DIGEST_RICHIESTA = "request-digest";
	public static final String CORNICE_SICUREZZA = "user-info";
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
	public static final String API_SICUREZZA_MESSAGGIO_HTTP_HEADER = API_SICUREZZA_MESSAGGIO_PREFIX+"-http-header";
	public static final String API_SICUREZZA_MESSAGGIO_APPLICABILITA = API_SICUREZZA_MESSAGGIO_PREFIX+"-applicability";
	public static final String API_SICUREZZA_MESSAGGIO_REQUEST_DIGEST = API_SICUREZZA_MESSAGGIO_PREFIX+"-request-digest";
	public static final String API_SICUREZZA_MESSAGGIO_USER_INFO = API_SICUREZZA_MESSAGGIO_PREFIX+"-user-info";
	
	private static final String REQUEST_PREFIX = "request-";
	private static final String RESPONSE_PREFIX = "response-";
	
	public static final String API_IMPL_SICUREZZA_MESSAGGIO_SIGNATURE_ALGORITHM = "signature-algorithm";
	public static final String API_IMPL_SICUREZZA_MESSAGGIO_CANONICALIZATION_ALGORITHM = "canonicalization-algorithm";
	public static final String API_IMPL_SICUREZZA_MESSAGGIO_HTTP_HEADER_FIRMATI = "signed-http-headers";
	public static final String API_IMPL_SICUREZZA_MESSAGGIO_SOAP_HEADER_FIRMATI = "signed-soap-headers";
	public static final String API_IMPL_SICUREZZA_MESSAGGIO_RIFERIMENTO_X509 = "x509-reference";
	public static final String API_IMPL_SICUREZZA_MESSAGGIO_CATENA_CERTIFICATI_X509 = "x509-certificate-chain";
	public static final String API_IMPL_SICUREZZA_MESSAGGIO_INCLUDE_SIGNATURE_TOKEN = "include-signature-token";
	public static final String API_IMPL_SICUREZZA_MESSAGGIO_TTL = "ttl";
	public static final String API_IMPL_SICUREZZA_MESSAGGIO_AUDIENCE = "audience";
	public static final String API_IMPL_SICUREZZA_MESSAGGIO_VERIFICA_AUDIENCE = "audience-verify";
	public static final String API_IMPL_SICUREZZA_MESSAGGIO_INTEGRITY_AUDIENCE = "integrity-audience";
	
	public static final String API_IMPL_SICUREZZA_MESSAGGIO_STORE_TYPE = "type";
	public static final String API_IMPL_SICUREZZA_MESSAGGIO_STORE_PATH = "path";
	public static final String API_IMPL_SICUREZZA_MESSAGGIO_STORE_CRLS = "crls";
	public static final String API_IMPL_SICUREZZA_MESSAGGIO_KEY_ALIAS = "key-alias";
	
	// COSTANTI SERVIZIO
	public static final String HSM = "hsm";
	
	public static Map<String, String> configToMap(AccordoServizioParteComune aspc, AccordoServizioParteSpecifica asps,
			String urlInvocazione, 
			Fruitore fruitore, String urlConnettoreFruitoreModI
			) throws Exception{
		
		boolean gestioneErogatori = (fruitore==null);
		boolean gestioneFruitori = !gestioneErogatori;
		
		Map<String, String> map = new HashMap<String, String>();
				
		List<ProtocolProperty> protocolPropertyList = gestioneErogatori ? asps.getProtocolPropertyList() : fruitore.getProtocolPropertyList();
		
		boolean rest = ServiceBinding.REST.equals(aspc.getServiceBinding());
		boolean digest = isProfiloSicurezzaMessaggioConIntegrita(aspc, asps.getPortType());
		boolean digestRichiesta = isProfiloSicurezzaMessaggioRequestDigest(aspc, asps.getPortType());
		boolean corniceSicurezza = isProfiloSicurezzaMessaggioCorniceSicurezza(aspc, asps.getPortType());
		boolean headerDuplicati = false;
		if(rest) {
			headerDuplicati = isProfiloSicurezzaMessaggioConHeaderDuplicati(aspc, asps.getPortType());
		}
		map.put(INTEGRITY, digest+"");
		map.put(DIGEST_RICHIESTA, digestRichiesta+"");
		map.put(CORNICE_SICUREZZA, corniceSicurezza+"");
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
			}
			if(sbMes.length()>0) {
				sicurezzaMessaggio = true;
				map.put(API_SICUREZZA_MESSAGGIO_PATTERN, sbMes.toString());
			}
		}
		
		if(sicurezzaMessaggio) {
			
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
			map.put(API_SICUREZZA_MESSAGGIO_USER_INFO,
					corniceSicurezza ? StatoFunzionalita.ABILITATO.getValue() : StatoFunzionalita.DISABILITATO.getValue());
							
			boolean request = true;
			addProfiloModISicurezza(map,
					protocolPropertyList,
					rest, gestioneFruitori, request, 
					digest, corniceSicurezza, headerDuplicati,
					urlInvocazione, urlConnettoreFruitoreModI);
			addProfiloModISicurezza(map,
					protocolPropertyList,
					rest, gestioneFruitori, !request, 
					digest, corniceSicurezza, headerDuplicati,
					urlInvocazione, urlConnettoreFruitoreModI);
		}
		
		return map;
		
	}
	
	public static String getPrefixKey(boolean fruizione, boolean request) {
		String prefixKey = null;
		/*if(request) {
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
			boolean digest, boolean corniceSicurezza, boolean headerDuplicati,
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
			
			if(digest) {
				// header firmati
				if( (request && fruizione) || (!request && !fruizione) ) {
					map.put(prefixKey+API_IMPL_SICUREZZA_MESSAGGIO_HTTP_HEADER_FIRMATI, 
							getStringValue(protocolPropertyList, CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HTTP_HEADERS_REST));
				}
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
				if(algoLabel.contains("#") && !algoLabel.endsWith("#")) {
					algoLabel = algoLabel.substring(algoLabel.indexOf("#")+1, algoLabel.length()).toUpperCase();
				}
				map.put(prefixKey+API_IMPL_SICUREZZA_MESSAGGIO_SIGNATURE_ALGORITHM, algoLabel);
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
				if(SignatureC14NAlgorithm.INCLUSIVE_C14N_10_OMITS_COMMENTS.getUri().equals(algoLabel)) {
					algoLabel = CostantiLabel.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_CANONICALIZATION_ALG_LABEL_INCLUSIVE_C14N_10;
				}
				else if(SignatureC14NAlgorithm.INCLUSIVE_C14N_11_OMITS_COMMENTS.getUri().equals(algoLabel)) {
					algoLabel = CostantiLabel.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_CANONICALIZATION_ALG_LABEL_INCLUSIVE_C14N_11;
				}
				else if(SignatureC14NAlgorithm.EXCLUSIVE_C14N_10_OMITS_COMMENTS.getUri().equals(algoLabel)) {
					algoLabel = CostantiLabel.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_CANONICALIZATION_ALG_LABEL_EXCLUSIVE_C14N_10;
				}
				map.put(prefixKey+API_IMPL_SICUREZZA_MESSAGGIO_CANONICALIZATION_ALGORITHM,algoLabel);
			}
			
			if(digest) {
				// header firmati
				if( (request && fruizione) || (!request && !fruizione) ) {
					String v = getStringValue(protocolPropertyList, CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SOAP_HEADERS_SOAP);
					if(v!=null) {
						v = v.replaceAll("\n", ", ");
						map.put(prefixKey+API_IMPL_SICUREZZA_MESSAGGIO_SOAP_HEADER_FIRMATI, v);
					}
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
				if(SecurityConstants.KEY_IDENTIFIER_BST_DIRECT_REFERENCE.equals(refLabel)){
					refLabel = CostantiLabel.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_RIFERIMENTO_X509_LABEL_BINARY_SECURITY_TOKEN;
					useCertificateChain = true;
				}
				else if(SecurityConstants.KEY_IDENTIFIER_ISSUER_SERIAL.equals(refLabel)){
					refLabel = CostantiLabel.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_RIFERIMENTO_X509_LABEL_SECURITY_TOKEN_REFERENCE;
					includeSignatureToken = true;
				}
				else if(SecurityConstants.KEY_IDENTIFIER_X509.equals(refLabel)){
					refLabel = CostantiLabel.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_RIFERIMENTO_X509_LABEL_KEY_IDENTIFIER_X509;
				}
				else if(SecurityConstants.KEY_IDENTIFIER_THUMBPRINT.equals(refLabel)){
					refLabel = CostantiLabel.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_RIFERIMENTO_X509_LABEL_KEY_IDENTIFIER_THUMBPRINT;
					includeSignatureToken = true;
				}
				else if(SecurityConstants.KEY_IDENTIFIER_SKI.equals(refLabel)){
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
			String aud = (v!=null ? 
					v : 
					fruizione ? urlConnettoreFruitoreModI : urlInvocazione);
			map.put(prefixKey+API_IMPL_SICUREZZA_MESSAGGIO_AUDIENCE, 
					aud);
			audit = true;
		}
		else {
			if(fruizione) {
				String stato = getBooleanValueAsStato(protocolPropertyList, CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RISPOSTA_AUDIENCE);
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
		
		// Header Duplicati
		if(rest && headerDuplicati && 
				( 
						(!request && fruizione)
						||
						(request && !fruizione)
				)
			) {
			
			if(audit) {
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
			String aliasKey = null;
			@SuppressWarnings("unused")
			boolean keystoreModePath = false;
			boolean keystoreModeArchive = false;
			boolean keystoreModeHsm = false;
			if(ssl) {
				type = CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SSL_TRUSTSTORE_TYPE;
				path = CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SSL_TRUSTSTORE_PATH;
				crl = CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SSL_TRUSTSTORE_CRLS;
			}
			else if(truststore) {
				type = CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_TYPE;
				path = CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_PATH;
				crl = CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_CRLS;
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
				map.put(prefixKey+ API_IMPL_SICUREZZA_MESSAGGIO_STORE_PATH,"HSM");
			}
			else if(path!=null) {
				String vPath = getStringValue(protocolPropertyList, path);
				map.put(prefixKey+ API_IMPL_SICUREZZA_MESSAGGIO_STORE_PATH,vPath);
			}
			else if(keystoreModeArchive) {
				map.put(prefixKey+ API_IMPL_SICUREZZA_MESSAGGIO_STORE_PATH,"Archivio caricato");
			}
			
			if(crl!=null) {
				String vCrl = getStringValue(protocolPropertyList, crl);
				map.put(prefixKey+ API_IMPL_SICUREZZA_MESSAGGIO_STORE_CRLS,vCrl);
			}
			
			if(aliasKey!=null) {
				String vAliasKey = getStringValue(protocolPropertyList, aliasKey);
				map.put(prefixKey+ API_IMPL_SICUREZZA_MESSAGGIO_KEY_ALIAS,vAliasKey);
			}
						
		}
		
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
	
	private static boolean isProfiloSicurezzaMessaggioConIntegrita(AccordoServizioParteComune api, String portType) {
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
						CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_VALUE_AUTHORIZATION_MODIPA_AUTH_IN_RESPONSE.equals(profiloSicurezzaMessaggio)) {
					return true;
				}		
			}
		}
		return false;
	}
	
	private static String getHeaderModI() throws Exception {
		Class<?> modiPropertiesClass = Class.forName("org.openspcoop2.protocol.modipa.config.ModIProperties");
		Method mGetInstance = modiPropertiesClass.getMethod("getInstance");
		Object instance = mGetInstance.invoke(null);
		Method mGetRestSecurityTokenHeaderModI = instance.getClass().getMethod("getRestSecurityTokenHeaderModI");
		return (String) mGetRestSecurityTokenHeaderModI.invoke(instance);
	}

}
