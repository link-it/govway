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

package org.openspcoop2.protocol.utils;

import java.util.List;

import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.utils.RegistroServiziUtils;

/**
 * ModISecurityUtils
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ModISecurityUtils {
	
	private ModISecurityUtils() {
	}
	
	public static List<String> getProfiloSicurezzaMessaggio(AccordoServizioParteComune api, String portType) {
		return getPropertySicurezzaMessaggioEngine(CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO, api, portType, false);
	}
	public static List<String> getProfiloSicurezzaMessaggioConSorgenteToken(AccordoServizioParteComune api, String portType) {
		return getPropertySicurezzaMessaggioEngine(CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO, 
				CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SORGENTE_TOKEN_IDAUTH, 
				api, portType, false);
	}
	public static boolean isProfiloSicurezzaMessaggioConIntegrita(AccordoServizioParteComune api, String portType) {
		List<String> tmp = getProfiloSicurezzaMessaggio(api, portType);
		if(tmp!=null && !tmp.isEmpty()) {
			for (String profiloSicurezzaMessaggio : tmp) {
				if(CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM0301.equals(profiloSicurezzaMessaggio) ||
						CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM0302.equals(profiloSicurezzaMessaggio) ||
						CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM0401.equals(profiloSicurezzaMessaggio) ||
						CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM0402.equals(profiloSicurezzaMessaggio)) {
					return true;
				}		
			}
		}
		return false;
	}
	public static boolean isProfiloSicurezzaMessaggioCorniceSicurezza(AccordoServizioParteComune api, String portType) {
		List<String> tmp = getPropertySicurezzaMessaggioEngine(CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA, api, portType, true);
		if(tmp!=null && !tmp.isEmpty()) {
			for (String v : tmp) {
				if(v!=null && "true".equals(v)) {
					return true;
				}
			}
		}
		return false;
	}
	public static String getProfiloSicurezzaMessaggioCorniceSicurezzaPattern(AccordoServizioParteComune api, String portType) {
		List<String> tmp = getPropertySicurezzaMessaggioEngine(CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_PATTERN, api, portType, false);
		if(tmp!=null && !tmp.isEmpty()) {
			return tmp.get(0);
		}
		return null;
	}
	public static String getProfiloSicurezzaMessaggioCorniceSicurezzaSchema(AccordoServizioParteComune api, String portType) {
		List<String> tmp = getPropertySicurezzaMessaggioEngine(CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_SCHEMA, api, portType, false);
		if(tmp!=null && !tmp.isEmpty()) {
			return tmp.get(0);
		}
		return null;
	}
	public static boolean isProfiloSicurezzaMessaggioConHeaderDuplicati(AccordoServizioParteComune api, String portType) {
		List<String> tmp = getPropertySicurezzaMessaggioEngine(CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER, 
				CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SORGENTE_TOKEN_IDAUTH, 
				api, portType, false);
		if(tmp!=null && !tmp.isEmpty()) {
			for (String headerSorgenteToken : tmp) {
				
				List<String> splitValues = RegistroServiziUtils.splitPropertyProtocolloResult(headerSorgenteToken);
				String header = splitValues.get(0);
				String sorgenteToken = splitValues.get(1);
				
				if( 
						(CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_VALUE_AUTHORIZATION_MODIPA.equals(header) ||
						CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_VALUE_AUTHORIZATION_MODIPA_AUTH_IN_RESPONSE.equals(header) ||
						CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_VALUE_AUTHORIZATION_CUSTOM.equals(header) ||
						CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_VALUE_AUTHORIZATION_CUSTOM_AUTH_IN_RESPONSE.equals(header)) 
						&&
					(isSicurezzaMessaggioGenerazioneTokenIdAuthLocale(sorgenteToken)) 
					){
					return true;
				}		
			}
		}
		return false;
	}
	public static boolean isProfiloSicurezzaMessaggioConSorgenteTokenNonLocale(AccordoServizioParteComune api, String portType, boolean rest) {
		if(rest) {
			return isProfiloSicurezzaMessaggioRestConSorgenteTokenNonLocale(api, portType);
		}
		else {
			return isProfiloSicurezzaMessaggioSoapConSorgenteTokenNonLocale(api, portType);
		}
	}
	public static boolean isProfiloSicurezzaMessaggioRestConSorgenteTokenNonLocale(AccordoServizioParteComune api, String portType) {
		List<String> tmp = getPropertySicurezzaMessaggioEngine(CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER, 
				CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SORGENTE_TOKEN_IDAUTH, 
				api, portType, false);
		if(tmp!=null && !tmp.isEmpty()) {
			for (String headerSorgenteToken : tmp) {
				
				List<String> splitValues = RegistroServiziUtils.splitPropertyProtocolloResult(headerSorgenteToken);
				String sorgenteToken = splitValues.get(1);
				
				if(!isSicurezzaMessaggioGenerazioneTokenIdAuthLocale(sorgenteToken) ) {
					return true;
				}		
			}
		}
		return false;
	}
	public static boolean isProfiloSicurezzaMessaggioSoapConSorgenteTokenNonLocale(AccordoServizioParteComune api, String portType) {
		List<String> tmp = getPropertySicurezzaMessaggioEngine(CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SORGENTE_TOKEN_IDAUTH, 
				api, portType, false);
		if(tmp!=null && !tmp.isEmpty()) {
			String sorgenteToken = tmp.get(0);
			if(!isSicurezzaMessaggioGenerazioneTokenIdAuthLocale(sorgenteToken) ) {
				return true;
			}		
		}
		return false;
	}
	private static List<String> getPropertySicurezzaMessaggioEngine(String propertyName, AccordoServizioParteComune api, String portType, boolean booleanValue) {
		return RegistroServiziUtils.fillPropertyProtocollo(propertyName, api, portType, booleanValue);
	}
	private static List<String> getPropertySicurezzaMessaggioEngine(String propertyName, String propertyName2, AccordoServizioParteComune api, String portType, boolean booleanValue) {
		return RegistroServiziUtils.fillPropertyProtocollo(propertyName, propertyName2, api, portType, booleanValue);
	}
	
	public static boolean isSicurezzaMessaggioRequired(AccordoServizioParteComune api, String portType) {
		
		List<String> apiValues = getProfiloSicurezzaMessaggioConSorgenteToken(api, portType);
		if(apiValues!=null && !apiValues.isEmpty()) {
			for (String sicurezzaMessaggioSorgenteToken : apiValues) {
				
				List<String> splitValues = RegistroServiziUtils.splitPropertyProtocolloResult(sicurezzaMessaggioSorgenteToken);
				String sicurezzaMessaggio = splitValues.get(0);
				String sorgenteToken = splitValues.get(1);
				
				if(CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM01.equals(sicurezzaMessaggio) ||
						CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM02.equals(sicurezzaMessaggio)) {
					if(isSicurezzaMessaggioGenerazioneTokenIdAuthLocale(sorgenteToken)) {
						return true;
					}
				}
				else if(CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM0301.equals(sicurezzaMessaggio) ||
						CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM0302.equals(sicurezzaMessaggio) ||
						CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM0401.equals(sicurezzaMessaggio) ||
						CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM0402.equals(sicurezzaMessaggio)) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	// NOTA: riferito dalla classe org.openspcoop2.web.ctrlstat.servlet.pa.PorteApplicativeServizioApplicativoAutorizzatoUtilities tramite reflection
	public static boolean isSicurezzaMessaggioRiferimentoX509Required(AccordoServizioParteComune api, String portType) {
		
		// ne basta uno presente
		
		List<String> apiValues = getProfiloSicurezzaMessaggioConSorgenteToken(api, portType);
		if(apiValues!=null && !apiValues.isEmpty()) {
			for (String sicurezzaMessaggioSorgenteToken : apiValues) {
				
				List<String> splitValues = RegistroServiziUtils.splitPropertyProtocolloResult(sicurezzaMessaggioSorgenteToken);
				String sicurezzaMessaggio = splitValues.get(0);
				String sorgenteToken = splitValues.get(1);
				
				boolean x509 = false;
				if(CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM01.equals(sicurezzaMessaggio) ||
						CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM02.equals(sicurezzaMessaggio)) {
					x509 = isSicurezzaMessaggioGenerazioneTokenIdAuthLocale(sorgenteToken);
				}
				else if(CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM0301.equals(sicurezzaMessaggio) ||
						CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM0302.equals(sicurezzaMessaggio)) {
					x509 = true;
				}
				else if(CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM0401.equals(sicurezzaMessaggio) ||
						CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM0402.equals(sicurezzaMessaggio)) {
					// (per token id-auth)
					x509 =isSicurezzaMessaggioGenerazioneTokenIdAuthLocale(sorgenteToken);
				}
				if(x509) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	public static boolean isSicurezzaMessaggioKidModeSupported(AccordoServizioParteComune api, String portType) {
		
		List<String> apiValues = getProfiloSicurezzaMessaggioConSorgenteToken(api, portType);
		if(apiValues!=null && !apiValues.isEmpty()) {
			for (String sicurezzaMessaggioSorgenteToken : apiValues) {
				
				List<String> splitValues = RegistroServiziUtils.splitPropertyProtocolloResult(sicurezzaMessaggioSorgenteToken);
				String sicurezzaMessaggio = splitValues.get(0);
				String sorgenteToken = splitValues.get(1);
				
				if(CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM01.equals(sicurezzaMessaggio) ||
						CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM02.equals(sicurezzaMessaggio)) {
					if(!isSicurezzaMessaggioGenerazioneTokenIdAuthLocale(sorgenteToken)) {
						return true;
					}
				}
				/**else if(CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM0301.equals(sicurezzaMessaggio) ||
						CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM0302.equals(sicurezzaMessaggio)) {
					// kidMode non permesso
				}*/
				else if(CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM0401.equals(sicurezzaMessaggio) ||
						CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM0402.equals(sicurezzaMessaggio)) {
					return true;
				}
				
			}
		}
		
		return false;
	}
	
	public static boolean existsAlmostOneProfiloSicurezzaMessaggioGenerazioneTokenIdAuthRemoto(AccordoServizioParteComune api, String portType) {
		List<String> tmp = getPropertySicurezzaMessaggioEngine(CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SORGENTE_TOKEN_IDAUTH, api, portType, false);
		if(tmp!=null && !tmp.isEmpty()) {
			for (String sorgenteToken : tmp) {
				if(CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SORGENTE_TOKEN_IDAUTH_VALUE_PDND.equals(sorgenteToken) ||
						CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SORGENTE_TOKEN_IDAUTH_VALUE_OAUTH.equals(sorgenteToken)) {
					return true;
				}
			}
		}
		return false; // default
	}
	public static boolean existsAlmostOneProfiloSicurezzaMessaggioGenerazioneTokenIdAuthLocale(AccordoServizioParteComune api, String portType) {
		List<String> tmp = getPropertySicurezzaMessaggioEngine(CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SORGENTE_TOKEN_IDAUTH, api, portType, false);
		if(tmp!=null && !tmp.isEmpty()) {
			for (String sorgenteToken : tmp) {
				if(! (CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SORGENTE_TOKEN_IDAUTH_VALUE_PDND.equals(sorgenteToken) ||
						CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SORGENTE_TOKEN_IDAUTH_VALUE_OAUTH.equals(sorgenteToken)) ) {
					return true;
				}
			}
		}
		return false;
	}
	private static boolean isSicurezzaMessaggioGenerazioneTokenIdAuthLocale(String sorgenteToken) {
		return !(CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SORGENTE_TOKEN_IDAUTH_VALUE_PDND.equals(sorgenteToken) ||
				 CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SORGENTE_TOKEN_IDAUTH_VALUE_OAUTH.equals(sorgenteToken));
	}
	
	public static boolean isProfiloSicurezzaMessaggioApplicabileRichiesta(AccordoServizioParteComune api, String portType, boolean sicurezzaRequired) {
		List<String> tmp = getPropertySicurezzaMessaggioEngine(CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_MODE, api, portType, false);
		if(tmp!=null && !tmp.isEmpty()) {
			for (String applicabilita : tmp) {
				if(isProfiloSicurezzaMessaggioApplicabile(applicabilita, true) ) {
					return true;
				}
				if(CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_MODE_VALUE_PERSONALIZZATO.equals(applicabilita) &&
					isProfiloSicurezzaMessaggioApplicabileRichiestaCustom(api, portType)) {
					return true;
				}
			}
			return false;
		}
		return sicurezzaRequired; // per backward compatibility con le configurazioni dove l'applicabilità non esisteva ancora
	}
	public static boolean isProfiloSicurezzaMessaggioApplicabileRisposta(AccordoServizioParteComune api, String portType, boolean sicurezzaRequired) {
		List<String> tmp = getPropertySicurezzaMessaggioEngine(CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_MODE, api, portType, false);
		if(tmp!=null && !tmp.isEmpty()) {
			for (String applicabilita : tmp) {
				if(isProfiloSicurezzaMessaggioApplicabile(applicabilita, false) ) {
					return true;
				}
				if(CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_MODE_VALUE_PERSONALIZZATO.equals(applicabilita) &&
					isProfiloSicurezzaMessaggioApplicabileRispostaCustom(api, portType)) {
					return true;
				}
			}
			return false;
		}
		return sicurezzaRequired; // per backward compatibility con le configurazioni dove l'applicabilità non esisteva ancora
	}
	private static boolean isProfiloSicurezzaMessaggioApplicabile(String applicabilita, boolean richiesta) {
		if(richiesta) {
			return CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_MODE_VALUE_ENTRAMBI.equals(applicabilita) ||
					CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_MODE_VALUE_RICHIESTA.equals(applicabilita) ||
					CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_MODE_VALUE_ENTRAMBI_CON_ATTACHMENTS.equals(applicabilita) ||
					CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_MODE_VALUE_RICHIESTA_CON_ATTACHMENTS.equals(applicabilita);
		}	
		else {
			return CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_MODE_VALUE_ENTRAMBI.equals(applicabilita) ||
					CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_MODE_VALUE_RISPOSTA.equals(applicabilita) ||
					CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_MODE_VALUE_ENTRAMBI_CON_ATTACHMENTS.equals(applicabilita) ||
					CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_MODE_VALUE_RISPOSTA_CON_ATTACHMENTS.equals(applicabilita);
		}
	}
	private static boolean isProfiloSicurezzaMessaggioApplicabileRichiestaCustom(AccordoServizioParteComune api, String portType) {
		List<String> tmpCustom = getPropertySicurezzaMessaggioEngine(CostantiDB.MODIPA_PROFILO_SICUREZZA_RICHIESTA_MODE, api, portType, false);
		if(tmpCustom!=null && !tmpCustom.isEmpty()) {
			for (String profiloSicurezzaMessaggioCustom : tmpCustom) {
				if(CostantiDB.MODIPA_PROFILO_SICUREZZA_RICHIESTA_MODE_VALUE_ABILITATO.equals(profiloSicurezzaMessaggioCustom) ||
						CostantiDB.MODIPA_PROFILO_SICUREZZA_RICHIESTA_MODE_VALUE_PERSONALIZZATO.equals(profiloSicurezzaMessaggioCustom) ) {
					return true;
				}	
			}
		}
		return false;
	}
	private static boolean isProfiloSicurezzaMessaggioApplicabileRispostaCustom(AccordoServizioParteComune api, String portType) {
		List<String> tmpCustom = getPropertySicurezzaMessaggioEngine(CostantiDB.MODIPA_PROFILO_SICUREZZA_RISPOSTA_MODE, api, portType, false);
		if(tmpCustom!=null && !tmpCustom.isEmpty()) {
			for (String profiloSicurezzaMessaggioCustom : tmpCustom) {
				if(CostantiDB.MODIPA_PROFILO_SICUREZZA_RISPOSTA_MODE_VALUE_ABILITATO.equals(profiloSicurezzaMessaggioCustom) ||
						CostantiDB.MODIPA_PROFILO_SICUREZZA_RISPOSTA_MODE_VALUE_PERSONALIZZATO.equals(profiloSicurezzaMessaggioCustom) ) {
					return true;
				}	
			}
		}
		return false;
	}
}
