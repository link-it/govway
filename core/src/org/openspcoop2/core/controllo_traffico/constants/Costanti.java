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

package org.openspcoop2.core.controllo_traffico.constants;

import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.core.controllo_traffico.driver.PolicyGroupByActiveThreadsType;

/**
 * Costanti 
 *
 * @author Andrea Poli (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class Costanti {

	public static final String SEPARATORE_IDPOLICY_RAGGRUPPAMENTO = " - ";
	
	private static String controlloTrafficoImagePrefix = "image";
	private static String controlloTrafficoEventiImagePrefix = "imageEventi";
	private static String controlloTrafficoImageExt = ".bin";
	
	public static String getControlloTrafficoImage(String CT_policyType) {
		return getEngineControlloTrafficoImagePrefix(CT_policyType, controlloTrafficoImagePrefix);
	}
	public static String getControlloTrafficoEventiImage(String CT_policyType) {
		return getEngineControlloTrafficoImagePrefix(CT_policyType, controlloTrafficoEventiImagePrefix);
	}
	private static String getEngineControlloTrafficoImagePrefix(String CT_policyType, String prefix) {
		StringBuilder sb = new StringBuilder(prefix);
		if(CT_policyType!=null && !"LOCAL".equals(CT_policyType)) {
			sb.append("-").append(CT_policyType);
		}
		sb.append(controlloTrafficoImageExt);
		return sb.toString();
	}
	
	public static final String POLICY_GLOBALE = "Globale";
	public static final String POLICY_API = "API";
	
	
	public static final String GESTORE = "ctGestore";
	public static final String GESTORE_HAZELCAST_MAP_BACKWARD_COMPATIBILITY = "HAZELCAST";
	
	public static final String GESTORE_CONFIG_DATE = "ctGestoreConfigDate";
	
	public static final String MODALITA_SINCRONIZZAZIONE = "ctSyncMode";
	
	public static final String VALUE_MODALITA_SINCRONIZZAZIONE_DEFAULT = "default";
	public static final String VALUE_MODALITA_SINCRONIZZAZIONE_LOCALE = "locale";
	public static final String VALUE_MODALITA_SINCRONIZZAZIONE_LOCALE_SUDDIVISA_TRA_NODI = "localePiuNodi";
	public static final String VALUE_MODALITA_SINCRONIZZAZIONE_DISTRIBUITA = "distribuita";
	public static final List<String> getVALUES_MODALITA_SINCRONIZZAZIONE(List<PolicyGroupByActiveThreadsType> tipiSupportati){
		return getEngineMODALITA_SINCRONIZZAZIONE(tipiSupportati, true);
	}
	
	public static final String LABEL_MODALITA_SINCRONIZZAZIONE = "Sincronizzazione";	
	public static final String LABEL_MODALITA_SINCRONIZZAZIONE_DEFAULT = "Default";
	public static final String LABEL_MODALITA_SINCRONIZZAZIONE_LOCALE = "Locale";
	public static final String LABEL_MODALITA_SINCRONIZZAZIONE_LOCALE_SUDDIVISA_TRA_NODI = "Locale - Quota divisa sui nodi";
	public static final String LABEL_MODALITA_SINCRONIZZAZIONE_DISTRIBUITA = "Distribuita";
	
	public static final List<String> getLABELS_MODALITA_SINCRONIZZAZIONE(List<PolicyGroupByActiveThreadsType> tipiSupportati){
		return getEngineMODALITA_SINCRONIZZAZIONE(tipiSupportati, false);
	} 
	
	private static final List<String> getEngineMODALITA_SINCRONIZZAZIONE(List<PolicyGroupByActiveThreadsType> tipiSupportati, boolean values){
		boolean locale = false;
		boolean localeNodi = false;
		boolean distribuita = false;
		for (PolicyGroupByActiveThreadsType tipo : tipiSupportati) {
			if(PolicyGroupByActiveThreadsType.LOCAL.equals(tipo)) {
				locale = true;
			}
			else if(PolicyGroupByActiveThreadsType.LOCAL_DIVIDED_BY_NODES.equals(tipo)) {
				localeNodi = true;
			}
			else {
				distribuita = true;
			}
		}
		List<String> l = new ArrayList<>();
		l.add(values ? VALUE_MODALITA_SINCRONIZZAZIONE_DEFAULT : LABEL_MODALITA_SINCRONIZZAZIONE_DEFAULT);
		if(locale) {
			l.add(values ? VALUE_MODALITA_SINCRONIZZAZIONE_LOCALE : LABEL_MODALITA_SINCRONIZZAZIONE_LOCALE);
		}
		if(localeNodi) {
			l.add(values ? VALUE_MODALITA_SINCRONIZZAZIONE_LOCALE_SUDDIVISA_TRA_NODI : LABEL_MODALITA_SINCRONIZZAZIONE_LOCALE_SUDDIVISA_TRA_NODI);
		}
		if(distribuita) {
			l.add(values ? VALUE_MODALITA_SINCRONIZZAZIONE_DISTRIBUITA : LABEL_MODALITA_SINCRONIZZAZIONE_DISTRIBUITA);
		}
		return l;
	}
	
	
	public static final String MODALITA_IMPLEMENTAZIONE = "ctImpl";
	
	public static final String VALUE_MODALITA_IMPLEMENTAZIONE_DATABASE = "database";
	public static final String VALUE_MODALITA_IMPLEMENTAZIONE_HAZELCAST = "hazelcast";
	public static final String VALUE_MODALITA_IMPLEMENTAZIONE_REDIS = "redis";
	public static final List<String> getVALUES_MODALITA_IMPLEMENTAZIONE(List<PolicyGroupByActiveThreadsType> tipiSupportati){
		return getEngineMODALITA_IMPLEMENTAZIONE(tipiSupportati, true);
	}
	
	public static final String LABEL_MODALITA_IMPLEMENTAZIONE = "Implementazione";
	
	public static final String LABEL_MODALITA_IMPLEMENTAZIONE_DATABASE = "embedded";
	public static final String LABEL_MODALITA_IMPLEMENTAZIONE_HAZELCAST = "hazelcast";
	public static final String LABEL_MODALITA_IMPLEMENTAZIONE_REDIS = "redis";	
	public static final List<String> getLABELS_MODALITA_IMPLEMENTAZIONE(List<PolicyGroupByActiveThreadsType> tipiSupportati){
		return getEngineMODALITA_IMPLEMENTAZIONE(tipiSupportati, false);
	}
	
	private static final List<String> getEngineMODALITA_IMPLEMENTAZIONE(List<PolicyGroupByActiveThreadsType> tipiSupportati, boolean values){
		boolean database = false;
		boolean hazelcast = false;
		boolean redis = false;
		for (PolicyGroupByActiveThreadsType tipo : tipiSupportati) {
			if(PolicyGroupByActiveThreadsType.DATABASE.equals(tipo)) {
				database = true;
			}
			else if(tipo.isHazelcast()) {
				hazelcast = true;
			}
			else if(tipo.isRedis()) {
				redis = true;
			}
		}
		List<String> l = new ArrayList<>();
		if(hazelcast) {
			l.add(values ? VALUE_MODALITA_IMPLEMENTAZIONE_HAZELCAST : LABEL_MODALITA_IMPLEMENTAZIONE_HAZELCAST);
		}
		if(redis) {
			l.add(values ? VALUE_MODALITA_IMPLEMENTAZIONE_REDIS : LABEL_MODALITA_IMPLEMENTAZIONE_REDIS);
		}
		if(database) {
			l.add(values ? VALUE_MODALITA_IMPLEMENTAZIONE_DATABASE : LABEL_MODALITA_IMPLEMENTAZIONE_DATABASE);
		}
		return l;
	}
	
	
	
	public static final String MODALITA_CONTATORI = "ctCount";
	
	public static final String VALUE_MODALITA_CONTATORI_EXACT = "exact";
	public static final String VALUE_MODALITA_CONTATORI_APPROXIMATED = "approximated";
	public static final String VALUE_MODALITA_CONTATORI_INCONSISTENT = "inconsistent";
	public static final List<String> getVALUES_MODALITA_CONTATORI(List<PolicyGroupByActiveThreadsType> tipiSupportati, String impl){
		return getEngineMODALITA_CONTATORI(tipiSupportati, impl, true);
	}
	
	public static final String LABEL_MODALITA_CONTATORI = "Misurazione";
	
	public static final String LABEL_MODALITA_CONTATORI_EXACT = "esatta";
	public static final String LABEL_MODALITA_CONTATORI_APPROXIMATED = "approssimata";
	public static final String LABEL_MODALITA_CONTATORI_INCONSISTENT = "inconsistente";
	public static final List<String> getLABELS_MODALITA_CONTATORI(List<PolicyGroupByActiveThreadsType> tipiSupportati, String impl){
		return getEngineMODALITA_CONTATORI(tipiSupportati, impl, false);
	}
	
	private static final List<String> getEngineMODALITA_CONTATORI(List<PolicyGroupByActiveThreadsType> tipiSupportati, String impl, boolean values){
		boolean exact = false;
		boolean approximated = false;
		boolean inconsistent = false;
		if(VALUE_MODALITA_IMPLEMENTAZIONE_HAZELCAST.equals(impl)) {
			for (PolicyGroupByActiveThreadsType tipo : tipiSupportati) {
				if(!tipo.isHazelcast()) {
					continue;
				}
				
				if(tipo.isExact()) {
					exact = true;
				}
				else if(tipo.isApproximated()) {
					approximated = true;
				}
				else if(tipo.isInconsistent()) {
					inconsistent = true;
				}
			}
		}
		else if(VALUE_MODALITA_IMPLEMENTAZIONE_REDIS.equals(impl)) {
			for (PolicyGroupByActiveThreadsType tipo : tipiSupportati) {
				if(!tipo.isRedis()) {
					continue;
				}
				
				if(tipo.isExact()) {
					exact = true;
				}
				else if(tipo.isApproximated()) {
					approximated = true;
				}
				else if(tipo.isInconsistent()) {
					inconsistent = true;
				}
			}
		}
		List<String> l = new ArrayList<>();
		if(exact) {
			l.add(values ? VALUE_MODALITA_CONTATORI_EXACT : LABEL_MODALITA_CONTATORI_EXACT);
		}
		if(approximated) {
			l.add(values ? VALUE_MODALITA_CONTATORI_APPROXIMATED : LABEL_MODALITA_CONTATORI_APPROXIMATED);
		}
		if(inconsistent) {
			l.add(values ? VALUE_MODALITA_CONTATORI_INCONSISTENT : LABEL_MODALITA_CONTATORI_INCONSISTENT);
		}
		return l;
	}
	
	
	
	
	public static final String MODALITA_TIPOLOGIA = "ctEngineType";
	
	public static final String VALUE_MODALITA_TIPOLOGIA_HAZELCAST_FULL_SYNC = "full-sync";
	public static final String VALUE_MODALITA_TIPOLOGIA_HAZELCAST_NEAR_CACHE = "near-cache";
	public static final String VALUE_MODALITA_TIPOLOGIA_HAZELCAST_LOCAL_CACHE = "local-cache";
	public static final String VALUE_MODALITA_TIPOLOGIA_HAZELCAST_REMOTE_SYNC = "remote-sync";
	public static final String VALUE_MODALITA_TIPOLOGIA_HAZELCAST_REMOTE_ASYNC = "remote-async";
	public static final String VALUE_MODALITA_TIPOLOGIA_HAZELCAST_REPLICATED_MAP = "replicated-map";
	public static final String VALUE_MODALITA_TIPOLOGIA_HAZELCAST_CONTATORI_ATOMIC_LONG = "atomic-long-counters";
	public static final String VALUE_MODALITA_TIPOLOGIA_HAZELCAST_CONTATORI_ATOMIC_LONG_ASYNC = "atomic-long-async-counters";
	public static final String VALUE_MODALITA_TIPOLOGIA_HAZELCAST_CONTATORI_PNCOUNTER = "pn-counters";	
	
	public static final String VALUE_MODALITA_TIPOLOGIA_REDIS_REDDISSON_MAP = "redisson-map";
	public static final String VALUE_MODALITA_TIPOLOGIA_REDIS_CONTATORI_ATOMIC_LONG = "atomic-long-counters";
	public static final String VALUE_MODALITA_TIPOLOGIA_REDIS_CONTATORI_LONGADDER = "longadder-counters";
	
	public static final List<String> getVALUES_MODALITA_TIPOLOGIA(List<PolicyGroupByActiveThreadsType> tipiSupportati, String impl, String counter){
		return getEngineMODALITA_TIPOLOGIA(tipiSupportati, impl, counter, true);
	}
	
	public static final String LABEL_MODALITA_TIPOLOGIA = "Algoritmo";
	
	public static final String LABEL_MODALITA_TIPOLOGIA_HAZELCAST_FULL_SYNC = "map";
	public static final String LABEL_MODALITA_TIPOLOGIA_HAZELCAST_NEAR_CACHE = "near-cache";
	public static final String LABEL_MODALITA_TIPOLOGIA_HAZELCAST_LOCAL_CACHE = "local-cache";
	public static final String LABEL_MODALITA_TIPOLOGIA_HAZELCAST_REMOTE_SYNC = "remote-sync";
	public static final String LABEL_MODALITA_TIPOLOGIA_HAZELCAST_REMOTE_ASYNC = "remote-async";
	public static final String LABEL_MODALITA_TIPOLOGIA_HAZELCAST_REPLICATED_MAP = "replicated-map";
	public static final String LABEL_MODALITA_TIPOLOGIA_HAZELCAST_CONTATORI_ATOMIC_LONG = "atomic-long-counters";
	public static final String LABEL_MODALITA_TIPOLOGIA_HAZELCAST_CONTATORI_ATOMIC_LONG_ASYNC = "atomic-long-async-counters";
	public static final String LABEL_MODALITA_TIPOLOGIA_HAZELCAST_CONTATORI_PNCOUNTER = "pn-counters";
	
	public static final String LABEL_MODALITA_TIPOLOGIA_REDIS_REDDISSON = "map";
	public static final String LABEL_MODALITA_TIPOLOGIA_REDIS_CONTATORI_ATOMIC_LONG = "atomic-long-counters";
	public static final String LABEL_MODALITA_TIPOLOGIA_REDIS_CONTATORI_LONGADDER = "longadder-counters";


	public static final List<String> getLABELS_MODALITA_TIPOLOGIA(List<PolicyGroupByActiveThreadsType> tipiSupportati, String impl, String counter){
		return getEngineMODALITA_TIPOLOGIA(tipiSupportati, impl, counter, false);
	}
	
	private static final List<String> getEngineMODALITA_TIPOLOGIA(List<PolicyGroupByActiveThreadsType> tipiSupportati, String impl, String counter, boolean values){
		List<String> l = new ArrayList<>();
		if(VALUE_MODALITA_IMPLEMENTAZIONE_HAZELCAST.equals(impl)) {
			for (PolicyGroupByActiveThreadsType tipo : tipiSupportati) {
				if(VALUE_MODALITA_CONTATORI_EXACT.equals(counter)) {
					if(PolicyGroupByActiveThreadsType.HAZELCAST_ATOMIC_LONG.equals(tipo)) {
						l.add(values ? VALUE_MODALITA_TIPOLOGIA_HAZELCAST_CONTATORI_ATOMIC_LONG: LABEL_MODALITA_TIPOLOGIA_HAZELCAST_CONTATORI_ATOMIC_LONG);
					}
					else if(PolicyGroupByActiveThreadsType.HAZELCAST_MAP.equals(tipo)) {
						l.add(values ? VALUE_MODALITA_TIPOLOGIA_HAZELCAST_FULL_SYNC : LABEL_MODALITA_TIPOLOGIA_HAZELCAST_FULL_SYNC);
					} 
				}
				else if(VALUE_MODALITA_CONTATORI_APPROXIMATED.equals(counter)) {
					if(PolicyGroupByActiveThreadsType.HAZELCAST_PNCOUNTER.equals(tipo)) {
						l.add(values ? VALUE_MODALITA_TIPOLOGIA_HAZELCAST_CONTATORI_PNCOUNTER: LABEL_MODALITA_TIPOLOGIA_HAZELCAST_CONTATORI_PNCOUNTER);
					}
					else if(PolicyGroupByActiveThreadsType.HAZELCAST_ATOMIC_LONG_ASYNC.equals(tipo)) {
						l.add(values ? VALUE_MODALITA_TIPOLOGIA_HAZELCAST_CONTATORI_ATOMIC_LONG_ASYNC: LABEL_MODALITA_TIPOLOGIA_HAZELCAST_CONTATORI_ATOMIC_LONG_ASYNC);
					}
					else if(PolicyGroupByActiveThreadsType.HAZELCAST_NEAR_CACHE.equals(tipo)) {
						l.add(values ? VALUE_MODALITA_TIPOLOGIA_HAZELCAST_NEAR_CACHE : LABEL_MODALITA_TIPOLOGIA_HAZELCAST_NEAR_CACHE);
					}
					else if(PolicyGroupByActiveThreadsType.HAZELCAST_LOCAL_CACHE.equals(tipo)) {
						l.add(values ? VALUE_MODALITA_TIPOLOGIA_HAZELCAST_LOCAL_CACHE : LABEL_MODALITA_TIPOLOGIA_HAZELCAST_LOCAL_CACHE);
					}
				}
				else if(VALUE_MODALITA_CONTATORI_INCONSISTENT.equals(counter)) {
					if(PolicyGroupByActiveThreadsType.HAZELCAST_NEAR_CACHE_UNSAFE_SYNC_MAP.equals(tipo)) {
						l.add(values ? VALUE_MODALITA_TIPOLOGIA_HAZELCAST_REMOTE_SYNC : LABEL_MODALITA_TIPOLOGIA_HAZELCAST_REMOTE_SYNC);
					}
					else if(PolicyGroupByActiveThreadsType.HAZELCAST_NEAR_CACHE_UNSAFE_ASYNC_MAP.equals(tipo)) {
						l.add(values ? VALUE_MODALITA_TIPOLOGIA_HAZELCAST_REMOTE_ASYNC : LABEL_MODALITA_TIPOLOGIA_HAZELCAST_REMOTE_ASYNC);
					}
					else if(PolicyGroupByActiveThreadsType.HAZELCAST_REPLICATED_MAP.equals(tipo)) {
						l.add(values ? VALUE_MODALITA_TIPOLOGIA_HAZELCAST_REPLICATED_MAP : LABEL_MODALITA_TIPOLOGIA_HAZELCAST_REPLICATED_MAP);
					}
				}
			}
		}
		else if(VALUE_MODALITA_IMPLEMENTAZIONE_REDIS.equals(impl)) {
			for (PolicyGroupByActiveThreadsType tipo : tipiSupportati) {
				if(VALUE_MODALITA_CONTATORI_EXACT.equals(counter)) {
					if(PolicyGroupByActiveThreadsType.REDISSON_ATOMIC_LONG.equals(tipo)) {
						l.add(values ? VALUE_MODALITA_TIPOLOGIA_REDIS_CONTATORI_ATOMIC_LONG : LABEL_MODALITA_TIPOLOGIA_REDIS_CONTATORI_ATOMIC_LONG);
					}
					else if(PolicyGroupByActiveThreadsType.REDISSON_MAP.equals(tipo)) {
						l.add(values ? VALUE_MODALITA_TIPOLOGIA_REDIS_REDDISSON_MAP : LABEL_MODALITA_TIPOLOGIA_REDIS_REDDISSON);
					}
				}
				else if(VALUE_MODALITA_CONTATORI_INCONSISTENT.equals(counter)) {
					/*
					 * Si potrebbe pensare che debba finire in approximatedModes come per il PNCounter 
					 * Non è così poichè per il PNCounter abbiamo le versioni incrementAndGet e addAndGet e in questo caso invece no, si fa prima l'increment e poi si ottiene il valore tramite sum(). 
					 * Una volta che lo si recupera tramite la 'sum()' potrebbero essere tornati anche risultati derivanti da altre richieste in corso.
					 **/
					if(PolicyGroupByActiveThreadsType.REDISSON_LONGADDER.equals(tipo)) {
						l.add(values ? VALUE_MODALITA_TIPOLOGIA_REDIS_CONTATORI_LONGADDER: LABEL_MODALITA_TIPOLOGIA_REDIS_CONTATORI_LONGADDER);
					}
				}
			}
		}
		return l;
	}
	

	
	
	
	// HTTP HEADERS
	
	public static final String VALUE_HTTP_HEADER_DEFAULT = "default";
	public static final String VALUE_HTTP_HEADER_DISABILITATO = "disabilitato";
	public static final String VALUE_HTTP_HEADER_ABILITATO = "abilitato";
	public static final String VALUE_HTTP_HEADER_ABILITATO_NO_WINDOWS = "noWindows";
	public static final String VALUE_HTTP_HEADER_ABILITATO_WINDOWS = "windows";
	public static final String VALUE_HTTP_HEADER_ABILITATO_NO_BACKOFF = "noBackoff";
	public static final String VALUE_HTTP_HEADER_ABILITATO_BACKOFF = "backoff";
	public static final String VALUE_HTTP_HEADER_RIDEFINITO = "ridefinito";
	
	public static final String LABEL_HTTP_HEADER_DEFAULT = "Default";
	public static final String LABEL_HTTP_HEADER_DISABILITATO = "Disabilitato";
	public static final String LABEL_HTTP_HEADER_ABILITATO = "Abilitato";
	public static final String LABEL_HTTP_HEADER_ABILITATO_NO_WINDOWS = "Abilitato (senza finestra temporale)";
	public static final String LABEL_HTTP_HEADER_ABILITATO_WINDOWS = "Abilitato (con finestra temporale)";
	public static final String LABEL_HTTP_HEADER_ABILITATO_NO_BACKOFF = "Abilitato (senza backoff)";
	public static final String LABEL_HTTP_HEADER_ABILITATO_BACKOFF = "Abilitato (con backoff)";
	public static final String LABEL_HTTP_HEADER_RIDEFINITO = "Ridefinito";
	
	
	public static final String MODALITA_GENERAZIONE_HEADER_HTTP = "ctHttpMode";	
	public static final List<String> VALUES_MODALITA_GENERAZIONE_HEADER_HTTP = new ArrayList<>();
	static {
		VALUES_MODALITA_GENERAZIONE_HEADER_HTTP.add(VALUE_HTTP_HEADER_DEFAULT);
		VALUES_MODALITA_GENERAZIONE_HEADER_HTTP.add(VALUE_HTTP_HEADER_DISABILITATO);
		VALUES_MODALITA_GENERAZIONE_HEADER_HTTP.add(VALUE_HTTP_HEADER_RIDEFINITO);
	}
	
	public static final String LABEL_MODALITA_GENERAZIONE_HEADER_HTTP = "HTTP Headers";	
	public static final List<String> LABELS_MODALITA_GENERAZIONE_HEADER_HTTP = new ArrayList<>();
	static {
		LABELS_MODALITA_GENERAZIONE_HEADER_HTTP.add(LABEL_HTTP_HEADER_DEFAULT);
		LABELS_MODALITA_GENERAZIONE_HEADER_HTTP.add(LABEL_HTTP_HEADER_DISABILITATO);
		LABELS_MODALITA_GENERAZIONE_HEADER_HTTP.add(LABEL_HTTP_HEADER_RIDEFINITO);
	}
	
	
	public static final String MODALITA_GENERAZIONE_HEADER_HTTP_LIMIT = "ctHttpQuota";	
	public static final List<String> VALUES_MODALITA_GENERAZIONE_HEADER_HTTP_LIMIT = new ArrayList<>();
	static {
		VALUES_MODALITA_GENERAZIONE_HEADER_HTTP_LIMIT.add(VALUE_HTTP_HEADER_DEFAULT);
		VALUES_MODALITA_GENERAZIONE_HEADER_HTTP_LIMIT.add(VALUE_HTTP_HEADER_ABILITATO_NO_WINDOWS);
		VALUES_MODALITA_GENERAZIONE_HEADER_HTTP_LIMIT.add(VALUE_HTTP_HEADER_ABILITATO_WINDOWS);
		VALUES_MODALITA_GENERAZIONE_HEADER_HTTP_LIMIT.add(VALUE_HTTP_HEADER_DISABILITATO);
	}
	
	public static final String LABEL_MODALITA_GENERAZIONE_HEADER_HTTP_LIMIT = "Limiti di Quota";	
	public static final List<String> LABELS_MODALITA_GENERAZIONE_HEADER_HTTP_LIMIT = new ArrayList<>();
	static {
		LABELS_MODALITA_GENERAZIONE_HEADER_HTTP_LIMIT.add(LABEL_HTTP_HEADER_DEFAULT);
		LABELS_MODALITA_GENERAZIONE_HEADER_HTTP_LIMIT.add(LABEL_HTTP_HEADER_ABILITATO_NO_WINDOWS);
		LABELS_MODALITA_GENERAZIONE_HEADER_HTTP_LIMIT.add(LABEL_HTTP_HEADER_ABILITATO_WINDOWS);
		LABELS_MODALITA_GENERAZIONE_HEADER_HTTP_LIMIT.add(LABEL_HTTP_HEADER_DISABILITATO);
	}
	

	public static final String MODALITA_GENERAZIONE_HEADER_HTTP_REMAINING = "ctHttpRemaining";	
	public static final List<String> VALUES_MODALITA_GENERAZIONE_HEADER_HTTP_REMAINING = new ArrayList<>();
	static {
		VALUES_MODALITA_GENERAZIONE_HEADER_HTTP_REMAINING.add(VALUE_HTTP_HEADER_DEFAULT);
		VALUES_MODALITA_GENERAZIONE_HEADER_HTTP_REMAINING.add(VALUE_HTTP_HEADER_ABILITATO);
		VALUES_MODALITA_GENERAZIONE_HEADER_HTTP_REMAINING.add(VALUE_HTTP_HEADER_DISABILITATO);
	}
	
	public static final String LABEL_MODALITA_GENERAZIONE_HEADER_HTTP_REMAINING = "Rimanenza della Quota";	
	public static final List<String> LABELS_MODALITA_GENERAZIONE_HEADER_HTTP_REMAINING = new ArrayList<>();
	static {
		LABELS_MODALITA_GENERAZIONE_HEADER_HTTP_REMAINING.add(LABEL_HTTP_HEADER_DEFAULT);
		LABELS_MODALITA_GENERAZIONE_HEADER_HTTP_REMAINING.add(LABEL_HTTP_HEADER_ABILITATO);
		LABELS_MODALITA_GENERAZIONE_HEADER_HTTP_REMAINING.add(LABEL_HTTP_HEADER_DISABILITATO);
	}
	
	
	public static final String MODALITA_GENERAZIONE_HEADER_HTTP_RESET = "ctHttpReset";	
	public static final List<String> VALUES_MODALITA_GENERAZIONE_HEADER_HTTP_RESET = new ArrayList<>();
	static {
		VALUES_MODALITA_GENERAZIONE_HEADER_HTTP_RESET.add(VALUE_HTTP_HEADER_DEFAULT);
		VALUES_MODALITA_GENERAZIONE_HEADER_HTTP_RESET.add(VALUE_HTTP_HEADER_ABILITATO);
		VALUES_MODALITA_GENERAZIONE_HEADER_HTTP_RESET.add(VALUE_HTTP_HEADER_DISABILITATO);
	}
	
	public static final String LABEL_MODALITA_GENERAZIONE_HEADER_HTTP_RESET = "Reset della Quota (secondi)";	
	public static final List<String> LABELS_MODALITA_GENERAZIONE_HEADER_HTTP_RESET = new ArrayList<>();
	static {
		LABELS_MODALITA_GENERAZIONE_HEADER_HTTP_RESET.add(LABEL_HTTP_HEADER_DEFAULT);
		LABELS_MODALITA_GENERAZIONE_HEADER_HTTP_RESET.add(LABEL_HTTP_HEADER_ABILITATO);
		LABELS_MODALITA_GENERAZIONE_HEADER_HTTP_RESET.add(LABEL_HTTP_HEADER_DISABILITATO);
	}
	
	
	public static final String MODALITA_GENERAZIONE_HEADER_HTTP_RETRY_AFTER = "ctHttpRetryAfter";	
	public static final List<String> VALUES_MODALITA_GENERAZIONE_HEADER_HTTP_RETRY_AFTER = new ArrayList<>();
	static {
		VALUES_MODALITA_GENERAZIONE_HEADER_HTTP_RETRY_AFTER.add(VALUE_HTTP_HEADER_DEFAULT);
		VALUES_MODALITA_GENERAZIONE_HEADER_HTTP_RETRY_AFTER.add(VALUE_HTTP_HEADER_ABILITATO_NO_BACKOFF);
		VALUES_MODALITA_GENERAZIONE_HEADER_HTTP_RETRY_AFTER.add(VALUE_HTTP_HEADER_ABILITATO_BACKOFF);
		VALUES_MODALITA_GENERAZIONE_HEADER_HTTP_RETRY_AFTER.add(VALUE_HTTP_HEADER_DISABILITATO);
	}
	
	public static final String LABEL_MODALITA_GENERAZIONE_HEADER_HTTP_RETRY_AFTER = "Retry-After";	
	public static final List<String> LABELS_MODALITA_GENERAZIONE_HEADER_HTTP_RETRY_AFTER = new ArrayList<>();
	static {
		LABELS_MODALITA_GENERAZIONE_HEADER_HTTP_RETRY_AFTER.add(LABEL_HTTP_HEADER_DEFAULT);
		LABELS_MODALITA_GENERAZIONE_HEADER_HTTP_RETRY_AFTER.add(LABEL_HTTP_HEADER_ABILITATO_NO_BACKOFF);
		LABELS_MODALITA_GENERAZIONE_HEADER_HTTP_RETRY_AFTER.add(LABEL_HTTP_HEADER_ABILITATO_BACKOFF);
		LABELS_MODALITA_GENERAZIONE_HEADER_HTTP_RETRY_AFTER.add(LABEL_HTTP_HEADER_DISABILITATO);
	}
	
	public static final String MODALITA_GENERAZIONE_HEADER_HTTP_RETRY_AFTER_BACKOFF_SECONDS = "ctHttpRetryAfterBackoff";	
	
	public static final String LABEL_MODALITA_GENERAZIONE_HEADER_HTTP_RETRY_AFTER_BACKOFF_SECONDS = "Backoff (secondi)";	
}
