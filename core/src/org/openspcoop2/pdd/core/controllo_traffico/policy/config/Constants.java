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

package org.openspcoop2.pdd.core.controllo_traffico.policy.config;

import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.core.controllo_traffico.driver.CostantiControlloTraffico;
import org.openspcoop2.core.controllo_traffico.driver.PolicyGroupByActiveThreadsType;

/**
 * Constants
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class Constants {

	public final static String GESTORE = "ctGestore";
	
	public final static String MODALITA_SINCRONIZZAZIONE = "ctSyncMode";
	
	public final static String VALUE_MODALITA_SINCRONIZZAZIONE_DEFAULT = "default";
	public final static String VALUE_MODALITA_SINCRONIZZAZIONE_LOCALE = "locale";
	public final static String VALUE_MODALITA_SINCRONIZZAZIONE_LOCALE_SUDDIVISA_TRA_NODI = "localePiuNodi";
	public final static String VALUE_MODALITA_SINCRONIZZAZIONE_DISTRIBUITA = "distribuita";
	public final static List<String> getVALUES_MODALITA_SINCRONIZZAZIONE(List<PolicyGroupByActiveThreadsType> tipiSupportati){
		return _getMODALITA_SINCRONIZZAZIONE(tipiSupportati, true);
	}
	
	public final static String LABEL_MODALITA_SINCRONIZZAZIONE = CostantiControlloTraffico.LABEL_MODALITA_SINCRONIZZAZIONE;	
	public final static String LABEL_MODALITA_SINCRONIZZAZIONE_DEFAULT = CostantiControlloTraffico.LABEL_MODALITA_SINCRONIZZAZIONE_DEFAULT;	
	public final static String LABEL_MODALITA_SINCRONIZZAZIONE_LOCALE = CostantiControlloTraffico.LABEL_MODALITA_SINCRONIZZAZIONE_LOCALE;	
	public final static String LABEL_MODALITA_SINCRONIZZAZIONE_LOCALE_SUDDIVISA_TRA_NODI = CostantiControlloTraffico.LABEL_MODALITA_SINCRONIZZAZIONE_LOCALE_SUDDIVISA_TRA_NODI;	
	public final static String LABEL_MODALITA_SINCRONIZZAZIONE_DISTRIBUITA = CostantiControlloTraffico.LABEL_MODALITA_SINCRONIZZAZIONE_DISTRIBUITA;	
	public final static List<String> getLABELS_MODALITA_SINCRONIZZAZIONE(List<PolicyGroupByActiveThreadsType> tipiSupportati){
		return _getMODALITA_SINCRONIZZAZIONE(tipiSupportati, false);
	} 
	
	private final static List<String> _getMODALITA_SINCRONIZZAZIONE(List<PolicyGroupByActiveThreadsType> tipiSupportati, boolean values){
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
		List<String> l = new ArrayList<String>();
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
	
	
	public final static String MODALITA_IMPLEMENTAZIONE = "ctImpl";
	
	public final static String VALUE_MODALITA_IMPLEMENTAZIONE_DATABASE = "database";
	public final static String VALUE_MODALITA_IMPLEMENTAZIONE_HAZELCAST = "hazelcast";
	public final static String VALUE_MODALITA_IMPLEMENTAZIONE_REDIS = "redis";
	public final static List<String> getVALUES_MODALITA_IMPLEMENTAZIONE(List<PolicyGroupByActiveThreadsType> tipiSupportati){
		return _getMODALITA_IMPLEMENTAZIONE(tipiSupportati, true);
	}
	
	public final static String LABEL_MODALITA_IMPLEMENTAZIONE = CostantiControlloTraffico.LABEL_MODALITA_IMPLEMENTAZIONE;	
	
	public final static String LABEL_MODALITA_IMPLEMENTAZIONE_DATABASE = CostantiControlloTraffico.LABEL_MODALITA_IMPLEMENTAZIONE_DATABASE;	
	public final static String LABEL_MODALITA_IMPLEMENTAZIONE_HAZELCAST = CostantiControlloTraffico.LABEL_MODALITA_IMPLEMENTAZIONE_HAZELCAST;	
	public final static String LABEL_MODALITA_IMPLEMENTAZIONE_REDIS = CostantiControlloTraffico.LABEL_MODALITA_IMPLEMENTAZIONE_REDIS;	
	public final static List<String> getLABELS_MODALITA_IMPLEMENTAZIONE(List<PolicyGroupByActiveThreadsType> tipiSupportati){
		return _getMODALITA_IMPLEMENTAZIONE(tipiSupportati, false);
	}
	
	private final static List<String> _getMODALITA_IMPLEMENTAZIONE(List<PolicyGroupByActiveThreadsType> tipiSupportati, boolean values){
		boolean database = false;
		boolean hazelcast = false;
		boolean redis = false;
		for (PolicyGroupByActiveThreadsType tipo : tipiSupportati) {
			if(PolicyGroupByActiveThreadsType.DATABASE.equals(tipo)) {
				database = true;
			}
			else if(PolicyGroupByActiveThreadsType.HAZELCAST.equals(tipo) ||
					PolicyGroupByActiveThreadsType.HAZELCAST_NEAR_CACHE.equals(tipo) ||
					PolicyGroupByActiveThreadsType.HAZELCAST_LOCAL_CACHE.equals(tipo) ||
					PolicyGroupByActiveThreadsType.HAZELCAST_NEAR_CACHE_UNSAFE_SYNC_MAP.equals(tipo) ||
					PolicyGroupByActiveThreadsType.HAZELCAST_NEAR_CACHE_UNSAFE_ASYNC_MAP.equals(tipo)) {
				hazelcast = true;
			}
			else if(PolicyGroupByActiveThreadsType.REDISSON.equals(tipo)) {
				redis = true;
			}
		}
		List<String> l = new ArrayList<String>();
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
	
	
	
	public final static String MODALITA_CONTATORI = "ctCount";
	
	public final static String VALUE_MODALITA_CONTATORI_EXACT = "exact";
	public final static String VALUE_MODALITA_CONTATORI_APPROXIMATED = "approximated";
	public final static List<String> getVALUES_MODALITA_CONTATORI(List<PolicyGroupByActiveThreadsType> tipiSupportati, String impl){
		return _getMODALITA_CONTATORI(tipiSupportati, impl, true);
	}
	
	public final static String LABEL_MODALITA_CONTATORI = CostantiControlloTraffico.LABEL_MODALITA_CONTATORI;	
	
	public final static String LABEL_MODALITA_CONTATORI_EXACT = CostantiControlloTraffico.LABEL_MODALITA_CONTATORI_EXACT;	
	public final static String LABEL_MODALITA_CONTATORI_APPROXIMATED = CostantiControlloTraffico.LABEL_MODALITA_CONTATORI_APPROXIMATED;	
	public final static List<String> getLABELS_MODALITA_CONTATORI(List<PolicyGroupByActiveThreadsType> tipiSupportati, String impl){
		return _getMODALITA_CONTATORI(tipiSupportati, impl, false);
	}
	
	private final static List<String> _getMODALITA_CONTATORI(List<PolicyGroupByActiveThreadsType> tipiSupportati, String impl, boolean values){
		boolean exact = false;
		boolean approximated = false;
		if(VALUE_MODALITA_IMPLEMENTAZIONE_HAZELCAST.equals(impl)) {
			for (PolicyGroupByActiveThreadsType tipo : tipiSupportati) {
				if(PolicyGroupByActiveThreadsType.HAZELCAST.equals(tipo) ||
						PolicyGroupByActiveThreadsType.HAZELCAST_NEAR_CACHE.equals(tipo) ||
						PolicyGroupByActiveThreadsType.HAZELCAST_LOCAL_CACHE.equals(tipo)) {
					exact = true;
				}
				else if(PolicyGroupByActiveThreadsType.HAZELCAST_NEAR_CACHE_UNSAFE_SYNC_MAP.equals(tipo) ||
						PolicyGroupByActiveThreadsType.HAZELCAST_NEAR_CACHE_UNSAFE_ASYNC_MAP.equals(tipo)) {
					approximated = true;
				}
			}
		}
		List<String> l = new ArrayList<String>();
		if(exact) {
			l.add(values ? VALUE_MODALITA_CONTATORI_EXACT : LABEL_MODALITA_CONTATORI_EXACT);
		}
		if(approximated) {
			l.add(values ? VALUE_MODALITA_CONTATORI_APPROXIMATED : LABEL_MODALITA_CONTATORI_APPROXIMATED);
		}
		return l;
	}
	
	
	
	
	public final static String MODALITA_TIPOLOGIA = "ctEngineType";
	
	public final static String VALUE_MODALITA_TIPOLOGIA_HAZELCAST_FULL_SYNC = "full-sync";
	public final static String VALUE_MODALITA_TIPOLOGIA_HAZELCAST_NEAR_CACHE = "near-cache";
	public final static String VALUE_MODALITA_TIPOLOGIA_HAZELCAST_LOCAL_CACHE = "local-cache";
	public final static String VALUE_MODALITA_TIPOLOGIA_HAZELCAST_REMOTE_SYNC = "remote-sync";
	public final static String VALUE_MODALITA_TIPOLOGIA_HAZELCAST_REMOTE_ASYNC = "remote-async";
	public final static String VALUE_MODALITA_TIPOLOGIA_HAZELCAST_CONTATORI_SINGOLI= "contatori-singoli";

	
	public final static String VALUE_MODALITA_TIPOLOGIA_REDIS_REDDISSON = "redisson-map";
	
	public final static List<String> getVALUES_MODALITA_TIPOLOGIA(List<PolicyGroupByActiveThreadsType> tipiSupportati, String impl, String counter){
		return _getMODALITA_TIPOLOGIA(tipiSupportati, impl, counter, true);
	}
	
	public final static String LABEL_MODALITA_TIPOLOGIA = CostantiControlloTraffico.LABEL_MODALITA_TIPOLOGIA;	
	
	public final static String LABEL_MODALITA_TIPOLOGIA_HAZELCAST_FULL_SYNC = CostantiControlloTraffico.LABEL_MODALITA_TIPOLOGIA_HAZELCAST_FULL_SYNC;	
	public final static String LABEL_MODALITA_TIPOLOGIA_HAZELCAST_NEAR_CACHE = CostantiControlloTraffico.LABEL_MODALITA_TIPOLOGIA_HAZELCAST_NEAR_CACHE;	
	public final static String LABEL_MODALITA_TIPOLOGIA_HAZELCAST_LOCAL_CACHE = CostantiControlloTraffico.LABEL_MODALITA_TIPOLOGIA_HAZELCAST_LOCAL_CACHE;	
	public final static String LABEL_MODALITA_TIPOLOGIA_HAZELCAST_REMOTE_SYNC = CostantiControlloTraffico.LABEL_MODALITA_TIPOLOGIA_HAZELCAST_REMOTE_SYNC;	
	public final static String LABEL_MODALITA_TIPOLOGIA_HAZELCAST_REMOTE_ASYNC = CostantiControlloTraffico.LABEL_MODALITA_TIPOLOGIA_HAZELCAST_REMOTE_ASYNC;	

	public final static String LABEL_MODALITA_TIPOLOGIA_REDIS_REDDISSON = CostantiControlloTraffico.LABEL_MODALITA_TIPOLOGIA_REDIS_REDDISSON;

	public final static List<String> getLABELS_MODALITA_TIPOLOGIA(List<PolicyGroupByActiveThreadsType> tipiSupportati, String impl, String counter){
		return _getMODALITA_TIPOLOGIA(tipiSupportati, impl, counter, false);
	}
	
	private final static List<String> _getMODALITA_TIPOLOGIA(List<PolicyGroupByActiveThreadsType> tipiSupportati, String impl, String counter, boolean values){
		List<String> l = new ArrayList<String>();
		if(VALUE_MODALITA_IMPLEMENTAZIONE_HAZELCAST.equals(impl)) {
			for (PolicyGroupByActiveThreadsType tipo : tipiSupportati) {
				if(VALUE_MODALITA_CONTATORI_EXACT.equals(counter)) {
					if(PolicyGroupByActiveThreadsType.HAZELCAST.equals(tipo)) {
						l.add(values ? VALUE_MODALITA_TIPOLOGIA_HAZELCAST_FULL_SYNC : LABEL_MODALITA_TIPOLOGIA_HAZELCAST_FULL_SYNC);
					}
					else if(PolicyGroupByActiveThreadsType.HAZELCAST_NEAR_CACHE.equals(tipo)) {
						l.add(values ? VALUE_MODALITA_TIPOLOGIA_HAZELCAST_NEAR_CACHE : LABEL_MODALITA_TIPOLOGIA_HAZELCAST_NEAR_CACHE);
					}
					else if(PolicyGroupByActiveThreadsType.HAZELCAST_LOCAL_CACHE.equals(tipo)) {
						l.add(values ? VALUE_MODALITA_TIPOLOGIA_HAZELCAST_LOCAL_CACHE : LABEL_MODALITA_TIPOLOGIA_HAZELCAST_LOCAL_CACHE);
					}
				}
				else if(VALUE_MODALITA_CONTATORI_APPROXIMATED.equals(counter)) {
					if(PolicyGroupByActiveThreadsType.HAZELCAST_NEAR_CACHE_UNSAFE_SYNC_MAP.equals(tipo)) {
						l.add(values ? VALUE_MODALITA_TIPOLOGIA_HAZELCAST_REMOTE_SYNC : LABEL_MODALITA_TIPOLOGIA_HAZELCAST_REMOTE_SYNC);
					}
					else if(PolicyGroupByActiveThreadsType.HAZELCAST_NEAR_CACHE_UNSAFE_ASYNC_MAP.equals(tipo)) {
						l.add(values ? VALUE_MODALITA_TIPOLOGIA_HAZELCAST_REMOTE_ASYNC : LABEL_MODALITA_TIPOLOGIA_HAZELCAST_REMOTE_ASYNC);
					}
				}
			}
		}
		else if(VALUE_MODALITA_IMPLEMENTAZIONE_REDIS.equals(impl)) {
			for (PolicyGroupByActiveThreadsType tipo : tipiSupportati) {
				if(PolicyGroupByActiveThreadsType.REDISSON.equals(tipo)) {
					l.add(values ? VALUE_MODALITA_TIPOLOGIA_REDIS_REDDISSON : LABEL_MODALITA_TIPOLOGIA_REDIS_REDDISSON);
				}
			}
		}
		return l;
	}
	

	
	
	
	// HTTP HEADERS
	
	public final static String VALUE_HTTP_HEADER_DEFAULT = "default";
	public final static String VALUE_HTTP_HEADER_DISABILITATO = "disabilitato";
	public final static String VALUE_HTTP_HEADER_ABILITATO = "abilitato";
	public final static String VALUE_HTTP_HEADER_ABILITATO_NO_WINDOWS = "noWindows";
	public final static String VALUE_HTTP_HEADER_ABILITATO_WINDOWS = "windows";
	public final static String VALUE_HTTP_HEADER_ABILITATO_NO_BACKOFF = "noBackoff";
	public final static String VALUE_HTTP_HEADER_ABILITATO_BACKOFF = "backoff";
	public final static String VALUE_HTTP_HEADER_RIDEFINITO = "ridefinito";
	
	public final static String LABEL_HTTP_HEADER_DEFAULT = "Default";
	public final static String LABEL_HTTP_HEADER_DISABILITATO = "Disabilitato";
	public final static String LABEL_HTTP_HEADER_ABILITATO = "Abilitato";
	public final static String LABEL_HTTP_HEADER_ABILITATO_NO_WINDOWS = "Abilitato (senza finestra temporale)";
	public final static String LABEL_HTTP_HEADER_ABILITATO_WINDOWS = "Abilitato (con finestra temporale)";
	public final static String LABEL_HTTP_HEADER_ABILITATO_NO_BACKOFF = "Abilitato (senza backoff)";
	public final static String LABEL_HTTP_HEADER_ABILITATO_BACKOFF = "Abilitato (con backoff)";
	public final static String LABEL_HTTP_HEADER_RIDEFINITO = "Ridefinito";
	
	
	public final static String MODALITA_GENERAZIONE_HEADER_HTTP = "ctHttpMode";	
	public final static List<String> VALUES_MODALITA_GENERAZIONE_HEADER_HTTP = new ArrayList<String>();
	static {
		VALUES_MODALITA_GENERAZIONE_HEADER_HTTP.add(VALUE_HTTP_HEADER_DEFAULT);
		VALUES_MODALITA_GENERAZIONE_HEADER_HTTP.add(VALUE_HTTP_HEADER_DISABILITATO);
		VALUES_MODALITA_GENERAZIONE_HEADER_HTTP.add(VALUE_HTTP_HEADER_RIDEFINITO);
	}
	
	public final static String LABEL_MODALITA_GENERAZIONE_HEADER_HTTP = "HTTP Headers";	
	public final static List<String> LABELS_MODALITA_GENERAZIONE_HEADER_HTTP = new ArrayList<String>();
	static {
		LABELS_MODALITA_GENERAZIONE_HEADER_HTTP.add(LABEL_HTTP_HEADER_DEFAULT);
		LABELS_MODALITA_GENERAZIONE_HEADER_HTTP.add(LABEL_HTTP_HEADER_DISABILITATO);
		LABELS_MODALITA_GENERAZIONE_HEADER_HTTP.add(LABEL_HTTP_HEADER_RIDEFINITO);
	}
	
	
	public final static String MODALITA_GENERAZIONE_HEADER_HTTP_LIMIT = "ctHttpQuota";	
	public final static List<String> VALUES_MODALITA_GENERAZIONE_HEADER_HTTP_LIMIT = new ArrayList<String>();
	static {
		VALUES_MODALITA_GENERAZIONE_HEADER_HTTP_LIMIT.add(VALUE_HTTP_HEADER_DEFAULT);
		VALUES_MODALITA_GENERAZIONE_HEADER_HTTP_LIMIT.add(VALUE_HTTP_HEADER_ABILITATO_NO_WINDOWS);
		VALUES_MODALITA_GENERAZIONE_HEADER_HTTP_LIMIT.add(VALUE_HTTP_HEADER_ABILITATO_WINDOWS);
		VALUES_MODALITA_GENERAZIONE_HEADER_HTTP_LIMIT.add(VALUE_HTTP_HEADER_DISABILITATO);
	}
	
	public final static String LABEL_MODALITA_GENERAZIONE_HEADER_HTTP_LIMIT = "Limiti di Quota";	
	public final static List<String> LABELS_MODALITA_GENERAZIONE_HEADER_HTTP_LIMIT = new ArrayList<String>();
	static {
		LABELS_MODALITA_GENERAZIONE_HEADER_HTTP_LIMIT.add(LABEL_HTTP_HEADER_DEFAULT);
		LABELS_MODALITA_GENERAZIONE_HEADER_HTTP_LIMIT.add(LABEL_HTTP_HEADER_ABILITATO_NO_WINDOWS);
		LABELS_MODALITA_GENERAZIONE_HEADER_HTTP_LIMIT.add(LABEL_HTTP_HEADER_ABILITATO_WINDOWS);
		LABELS_MODALITA_GENERAZIONE_HEADER_HTTP_LIMIT.add(LABEL_HTTP_HEADER_DISABILITATO);
	}
	

	public final static String MODALITA_GENERAZIONE_HEADER_HTTP_REMAINING = "ctHttpRemaining";	
	public final static List<String> VALUES_MODALITA_GENERAZIONE_HEADER_HTTP_REMAINING = new ArrayList<String>();
	static {
		VALUES_MODALITA_GENERAZIONE_HEADER_HTTP_REMAINING.add(VALUE_HTTP_HEADER_DEFAULT);
		VALUES_MODALITA_GENERAZIONE_HEADER_HTTP_REMAINING.add(VALUE_HTTP_HEADER_ABILITATO);
		VALUES_MODALITA_GENERAZIONE_HEADER_HTTP_REMAINING.add(VALUE_HTTP_HEADER_DISABILITATO);
	}
	
	public final static String LABEL_MODALITA_GENERAZIONE_HEADER_HTTP_REMAINING = "Rimanenza della Quota";	
	public final static List<String> LABELS_MODALITA_GENERAZIONE_HEADER_HTTP_REMAINING = new ArrayList<String>();
	static {
		LABELS_MODALITA_GENERAZIONE_HEADER_HTTP_REMAINING.add(LABEL_HTTP_HEADER_DEFAULT);
		LABELS_MODALITA_GENERAZIONE_HEADER_HTTP_REMAINING.add(LABEL_HTTP_HEADER_ABILITATO);
		LABELS_MODALITA_GENERAZIONE_HEADER_HTTP_REMAINING.add(LABEL_HTTP_HEADER_DISABILITATO);
	}
	
	
	public final static String MODALITA_GENERAZIONE_HEADER_HTTP_RESET = "ctHttpReset";	
	public final static List<String> VALUES_MODALITA_GENERAZIONE_HEADER_HTTP_RESET = new ArrayList<String>();
	static {
		VALUES_MODALITA_GENERAZIONE_HEADER_HTTP_RESET.add(VALUE_HTTP_HEADER_DEFAULT);
		VALUES_MODALITA_GENERAZIONE_HEADER_HTTP_RESET.add(VALUE_HTTP_HEADER_ABILITATO);
		VALUES_MODALITA_GENERAZIONE_HEADER_HTTP_RESET.add(VALUE_HTTP_HEADER_DISABILITATO);
	}
	
	public final static String LABEL_MODALITA_GENERAZIONE_HEADER_HTTP_RESET = "Reset della Quota (secondi)";	
	public final static List<String> LABELS_MODALITA_GENERAZIONE_HEADER_HTTP_RESET = new ArrayList<String>();
	static {
		LABELS_MODALITA_GENERAZIONE_HEADER_HTTP_RESET.add(LABEL_HTTP_HEADER_DEFAULT);
		LABELS_MODALITA_GENERAZIONE_HEADER_HTTP_RESET.add(LABEL_HTTP_HEADER_ABILITATO);
		LABELS_MODALITA_GENERAZIONE_HEADER_HTTP_RESET.add(LABEL_HTTP_HEADER_DISABILITATO);
	}
	
	
	public final static String MODALITA_GENERAZIONE_HEADER_HTTP_RETRY_AFTER = "ctHttpRetryAfter";	
	public final static List<String> VALUES_MODALITA_GENERAZIONE_HEADER_HTTP_RETRY_AFTER = new ArrayList<String>();
	static {
		VALUES_MODALITA_GENERAZIONE_HEADER_HTTP_RETRY_AFTER.add(VALUE_HTTP_HEADER_DEFAULT);
		VALUES_MODALITA_GENERAZIONE_HEADER_HTTP_RETRY_AFTER.add(VALUE_HTTP_HEADER_ABILITATO_NO_BACKOFF);
		VALUES_MODALITA_GENERAZIONE_HEADER_HTTP_RETRY_AFTER.add(VALUE_HTTP_HEADER_ABILITATO_BACKOFF);
		VALUES_MODALITA_GENERAZIONE_HEADER_HTTP_RETRY_AFTER.add(VALUE_HTTP_HEADER_DISABILITATO);
	}
	
	public final static String LABEL_MODALITA_GENERAZIONE_HEADER_HTTP_RETRY_AFTER = "Retry-After";	
	public final static List<String> LABELS_MODALITA_GENERAZIONE_HEADER_HTTP_RETRY_AFTER = new ArrayList<String>();
	static {
		LABELS_MODALITA_GENERAZIONE_HEADER_HTTP_RETRY_AFTER.add(LABEL_HTTP_HEADER_DEFAULT);
		LABELS_MODALITA_GENERAZIONE_HEADER_HTTP_RETRY_AFTER.add(LABEL_HTTP_HEADER_ABILITATO_NO_BACKOFF);
		LABELS_MODALITA_GENERAZIONE_HEADER_HTTP_RETRY_AFTER.add(LABEL_HTTP_HEADER_ABILITATO_BACKOFF);
		LABELS_MODALITA_GENERAZIONE_HEADER_HTTP_RETRY_AFTER.add(LABEL_HTTP_HEADER_DISABILITATO);
	}
	
	public final static String MODALITA_GENERAZIONE_HEADER_HTTP_RETRY_AFTER_BACKOFF_SECONDS = "ctHttpRetryAfterBackoff";	
	
	public final static String LABEL_MODALITA_GENERAZIONE_HEADER_HTTP_RETRY_AFTER_BACKOFF_SECONDS = "Backoff (secondi)";	
	
}
