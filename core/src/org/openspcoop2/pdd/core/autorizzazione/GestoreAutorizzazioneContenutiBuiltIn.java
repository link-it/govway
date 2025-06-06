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

package org.openspcoop2.pdd.core.autorizzazione;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.config.Proprieta;
import org.openspcoop2.core.config.utils.ConfigUtils;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.core.PdDContext;
import org.openspcoop2.pdd.core.dynamic.DynamicUtils;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.utils.SortedMap;
import org.openspcoop2.utils.regexp.RegularExpressionEngine;
import org.slf4j.Logger;

/**
 * GestoreAutorizzazioneContenutiBuiltIn
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class GestoreAutorizzazioneContenutiBuiltIn {

	private boolean autorizzato = true;
	private String errorMessage = null;
	public boolean isAutorizzato() {
		return this.autorizzato;
	}

	public String getErrorMessage() {
		return this.errorMessage;
	}
	
	public void process(OpenSPCoop2Message msg, AbstractDatiInvocazione datiInvocazione, PdDContext pddContext,
			List<Proprieta> regole) throws Exception {
		
		Logger log = OpenSPCoop2Logger.getLoggerOpenSPCoopCore();
		
		/* Costruisco dynamic Map */
		boolean bufferMessage_readOnly =  OpenSPCoop2Properties.getInstance().isReadByPathBufferEnabled();
		Map<String, Object> dynamicMap = DynamicUtils.buildDynamicMap(msg, pddContext, datiInvocazione.getBusta(), log, bufferMessage_readOnly);
		
		
		/* Analisi regole di autorizzazione */
		
		if(regole==null || regole.size()<=0) {
			throw new Exception("Non sono state definite regole di autorizzazione contenuto");
		}
		
		SortedMap<List<String>> map = ConfigUtils.toSortedListMap(regole);
		if(map==null || map.isEmpty()) {
			throw new Exception("Non sono state definite regole di autorizzazione contenuto");
		}
		
		//System.out.println("\n\n@ ===== AUTH CONTENUTI =====");
		
		List<String> keys = map.keys();
		for (String risorsa : keys) {
			
			List<String> expectedValues = map.get(risorsa);
			
			if(expectedValues!=null && !expectedValues.isEmpty()) {
				
				for (String expectedValue : expectedValues) {
			
					//System.out.println("check '"+risorsa+"'='"+expectedValue+"'");
					
					if(risorsa==null) {
						throw new Exception("Trovata una regola di autorizzazione senza risorsa");
					}
					risorsa=risorsa.trim();
					
					if(risorsa.startsWith("#")) {
						// commento
						continue;
					}
					
					if(expectedValue==null) {
						throw new Exception("Trovata una regola di autorizzazione senza valore atteso");
					}	
					expectedValue = expectedValue.trim();
					
					// Risolvo valore della risorsa
					String valoreRisorsa = null;
					try {
						valoreRisorsa = DynamicUtils.convertDynamicPropertyValue(risorsa, risorsa, dynamicMap, pddContext);
					}catch(Exception e) {
						String msgError = "Conversione valore della risorsa '"+risorsa+"' non riuscita: "+e.getMessage();
						if(CostantiAutorizzazione.AUTHZ_UNDEFINED.equalsIgnoreCase(expectedValue)) {
							// NOP: se non l'ho risolto significa che non esiste.... sotto nel controllo undefined quindi autorizzo
							//if(!e.getMessage().contains("not exists as key in map")) {
							//	
							//}
						}
						else {
							log.error(msgError, e);
							this.autorizzato = false;
							this.errorMessage = "Resource '"+risorsa+"' not verifiable; unprocessable dynamic value '"+expectedValue+"': "+e.getMessage();
							break;
							//throw new Exception(msgError,e);
						}
					}
					
		
					
					if(CostantiAutorizzazione.AUTHZ_ANY_VALUE.equalsIgnoreCase(expectedValue)) {
						
						/** ANY VALUE */
						
						log.debug("Verifico valore della risorsa '"+risorsa+"' non sia null e non sia vuoto ...");
						
						// basta che abbia un valore not null
						if(valoreRisorsa==null || "".equals(valoreRisorsa)) {
							this.autorizzato = false;
							this.errorMessage = "Resource '"+risorsa+"' with unexpected empty value";
							break;
						}
					}
					else if(CostantiAutorizzazione.AUTHZ_UNDEFINED.equalsIgnoreCase(expectedValue)) {
						
						/** NOT PRESENT */
						
						log.debug("Verifico valore della risorsa '"+risorsa+"' sia null o sia vuoto ...");
						
						// basta che abbia un valore
						if(valoreRisorsa!=null && !"".equals(valoreRisorsa)) {
							this.autorizzato = false;
							this.errorMessage = "Unexpected resource '"+risorsa+"'";
							break;
						}
						
					}
					else if(
							(
									expectedValue.toLowerCase().startsWith(CostantiAutorizzazione.AUTHZ_REGEXP_MATCH_PREFIX.toLowerCase())
									||
									expectedValue.toLowerCase().startsWith(CostantiAutorizzazione.AUTHZ_REGEXP_FIND_PREFIX.toLowerCase())
									||
									expectedValue.toLowerCase().startsWith(CostantiAutorizzazione.AUTHZ_REGEXP_NOT_MATCH_PREFIX.toLowerCase())
									||
									expectedValue.toLowerCase().startsWith(CostantiAutorizzazione.AUTHZ_REGEXP_NOT_FIND_PREFIX.toLowerCase())
							) 
							&&
							expectedValue.toLowerCase().endsWith(CostantiAutorizzazione.AUTHZ_REGEXP_SUFFIX.toLowerCase())) {
						
						/** REGULAR EXPRESSION MATCH/FIND */
						
						boolean match = expectedValue.toLowerCase().startsWith(CostantiAutorizzazione.AUTHZ_REGEXP_MATCH_PREFIX.toLowerCase())
								||
								expectedValue.toLowerCase().startsWith(CostantiAutorizzazione.AUTHZ_REGEXP_NOT_MATCH_PREFIX.toLowerCase());
						boolean not = expectedValue.toLowerCase().startsWith(CostantiAutorizzazione.AUTHZ_REGEXP_NOT_MATCH_PREFIX.toLowerCase())
								||
								expectedValue.toLowerCase().startsWith(CostantiAutorizzazione.AUTHZ_REGEXP_NOT_FIND_PREFIX.toLowerCase());
						String regexpPattern = null;
						if(match) {
							int length = -1;
							if(expectedValue.toLowerCase().startsWith(CostantiAutorizzazione.AUTHZ_REGEXP_MATCH_PREFIX.toLowerCase())) {
								length = CostantiAutorizzazione.AUTHZ_REGEXP_MATCH_PREFIX.length();
							}
							else {
								length = CostantiAutorizzazione.AUTHZ_REGEXP_NOT_MATCH_PREFIX.length();
							}
							if(expectedValue.length()<= (length+CostantiAutorizzazione.AUTHZ_REGEXP_SUFFIX.length()) ) {
								throw new Exception("Resource '"+risorsa+"' configuration without expected regexp match");
							}
							regexpPattern = expectedValue.substring(length, (expectedValue.length()-CostantiAutorizzazione.AUTHZ_REGEXP_SUFFIX.length()));
						}
						else {
							int length = -1;
							if(expectedValue.toLowerCase().startsWith(CostantiAutorizzazione.AUTHZ_REGEXP_FIND_PREFIX.toLowerCase())) {
								length = CostantiAutorizzazione.AUTHZ_REGEXP_FIND_PREFIX.length();
							}
							else {
								length = CostantiAutorizzazione.AUTHZ_REGEXP_NOT_FIND_PREFIX.length();
							}
							if(expectedValue.length()<= (length+CostantiAutorizzazione.AUTHZ_REGEXP_SUFFIX.length()) ) {
								throw new Exception("Resource '"+risorsa+"' configuration without expected regexp find");
							}
							regexpPattern = expectedValue.substring(length, (expectedValue.length()-CostantiAutorizzazione.AUTHZ_REGEXP_SUFFIX.length()));
						}
						regexpPattern = regexpPattern.trim();
						log.debug("Verifico valore dela risorsa '"+risorsa+"' tramite espressione regolare (match:"+match+") '"+regexpPattern+"' ...");
						
						// basta che un valore abbia match
						if(valoreRisorsa==null || StringUtils.isEmpty(valoreRisorsa)) {
							// questo controllo serve per evitare che venga lanciata l'eccezione dentro RegularExpressionEngine del contenuto vuoto
							this.autorizzato = false;
							this.errorMessage = "Unable to locate the resource '"+risorsa+"' or the resource is empty.";
							break;
						}
						boolean ok = match ? RegularExpressionEngine.isMatch(valoreRisorsa, regexpPattern) : RegularExpressionEngine.isFind(valoreRisorsa, regexpPattern);
						if(not) {
							if(ok) {
								String tipo = match ? "match" : "find";
								this.autorizzato = false;
								this.errorMessage = "Resource '"+risorsa+"' with unexpected value '"+valoreRisorsa+"' (regExpr not "+tipo+" failed)";
								break;
							}
						}
						else {
							if(!ok) {
								String tipo = match ? "match" : "find";
								this.autorizzato = false;
								this.errorMessage = "Resource '"+risorsa+"' with unexpected value '"+valoreRisorsa+"' (regExpr "+tipo+" failed)";
								break;
							}
						}
						
					}
					else {
					
						/** VALUE (con PLACEHOLDERS) */
						
						boolean not = false;
						if(
								expectedValue.toLowerCase().startsWith(CostantiAutorizzazione.AUTHZ_NOT_PREFIX.toLowerCase())
								&&
								expectedValue.toLowerCase().endsWith(CostantiAutorizzazione.AUTHZ_NOT_SUFFIX.toLowerCase())) {
							not = true;
							if(expectedValue.length()<= (CostantiAutorizzazione.AUTHZ_NOT_PREFIX.length()+CostantiAutorizzazione.AUTHZ_NOT_SUFFIX.length()) ) {
								throw new Exception("Resource '"+risorsa+"' configuration without value in not condition");
							}
							expectedValue = expectedValue.substring(CostantiAutorizzazione.AUTHZ_NOT_PREFIX.length(), (expectedValue.length()-CostantiAutorizzazione.AUTHZ_NOT_SUFFIX.length()));
						}
						
						boolean ignoreCase = false;
						if(
								expectedValue.toLowerCase().startsWith(CostantiAutorizzazione.AUTHZ_IGNORE_CASE_PREFIX.toLowerCase())
								&&
								expectedValue.toLowerCase().endsWith(CostantiAutorizzazione.AUTHZ_IGNORE_CASE_SUFFIX.toLowerCase())) {
							ignoreCase = true;
							if(expectedValue.length()<= (CostantiAutorizzazione.AUTHZ_IGNORE_CASE_PREFIX.length()+CostantiAutorizzazione.AUTHZ_IGNORE_CASE_SUFFIX.length()) ) {
								throw new Exception("Resource '"+risorsa+"' configuration without value in ignore case condition");
							}
							expectedValue = expectedValue.substring(CostantiAutorizzazione.AUTHZ_IGNORE_CASE_PREFIX.length(), (expectedValue.length()-CostantiAutorizzazione.AUTHZ_IGNORE_CASE_SUFFIX.length()));
						}
						
						try {
							expectedValue = DynamicUtils.convertDynamicPropertyValue(risorsa, expectedValue, dynamicMap, pddContext);
						}catch(Exception e) {
							String msgErrore = "Conversione valore per risorsa '"+risorsa+"' non riuscita (valore: "+expectedValue+"): "+e.getMessage();
							//throw new Exception(msgErrore,e);
							log.error(msgErrore, e);
							this.autorizzato = false;
							this.errorMessage = "Resource '"+risorsa+"' not verifiable; unprocessable dynamic value '"+expectedValue+"': "+e.getMessage();
							break;
						}
						
						if(expectedValue!=null) {
							boolean ok = false;
							if(expectedValue.contains(",")) {
								String [] values = expectedValue.split(",");
								for (int i = 0; i < values.length; i++) {
									String v = values[i].trim();
									if(v!=null) {
										if(ignoreCase) {
											if(v.equalsIgnoreCase(valoreRisorsa)) {
												ok = true;
												break;
											}
										}
										else {
											if(v.equals(valoreRisorsa)) {
												ok = true;
												break;
											}
										}
									}
								}
							}
							else {
								if(ignoreCase) {
									ok = expectedValue.equalsIgnoreCase(valoreRisorsa);
								}
								else {
									ok = expectedValue.equals(valoreRisorsa);
								}
							}
							
							if(not) {
								if(ok) {
									this.autorizzato = false;
									this.errorMessage = "Resource '"+risorsa+"' with unauthorized value '"+valoreRisorsa+"'";
									break;
								}
							}
							else {
								if(!ok) {
									this.autorizzato = false;
									this.errorMessage = "Resource '"+risorsa+"' with unexpected value '"+valoreRisorsa+"'";
									break;
								}
							}
						}
						else {
							if(valoreRisorsa!=null) {
								this.autorizzato = false;
								this.errorMessage = "Resource '"+risorsa+"' with unexpected value; expected null value";
								break;
							}
						}
					}
				}
				
			}
			
		}
		
	}

}
