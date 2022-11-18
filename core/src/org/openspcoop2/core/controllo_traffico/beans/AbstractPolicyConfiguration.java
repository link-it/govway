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

package org.openspcoop2.core.controllo_traffico.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.config.Proprieta;
import org.openspcoop2.core.controllo_traffico.constants.Costanti;
import org.openspcoop2.core.controllo_traffico.driver.PolicyGroupByActiveThreadsType;

/**
 * PolicyConfiguration
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public abstract class AbstractPolicyConfiguration implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	// runtime config
	protected boolean gestionePolicyRidefinita = false;
	protected PolicyGroupByActiveThreadsType type;
	private Boolean LOCAL_DIVIDED_BY_NODES_remaining_zeroValue = null;
	private Boolean LOCAL_DIVIDED_BY_NODES_limit_roundingDown = null;
	private Boolean LOCAL_DIVIDED_BY_NODES_limit_normalizedQuota = null;
	protected Boolean _getLOCAL_DIVIDED_BY_NODES_remaining_zeroValue() {
		return this.LOCAL_DIVIDED_BY_NODES_remaining_zeroValue;
	}
	public void setLOCAL_DIVIDED_BY_NODES_remaining_zeroValue(Boolean lOCAL_DIVIDED_BY_NODES_remaining_zeroValue) {
		this.LOCAL_DIVIDED_BY_NODES_remaining_zeroValue = lOCAL_DIVIDED_BY_NODES_remaining_zeroValue;
	}
	protected Boolean _getLOCAL_DIVIDED_BY_NODES_limit_roundingDown() {
		return this.LOCAL_DIVIDED_BY_NODES_limit_roundingDown;
	}
	public void setLOCAL_DIVIDED_BY_NODES_limit_roundingDown(Boolean lOCAL_DIVIDED_BY_NODES_limit_roundingDown) {
		this.LOCAL_DIVIDED_BY_NODES_limit_roundingDown = lOCAL_DIVIDED_BY_NODES_limit_roundingDown;
	}
	protected Boolean _getLOCAL_DIVIDED_BY_NODES_limit_normalizedQuota() {
		return this.LOCAL_DIVIDED_BY_NODES_limit_normalizedQuota;
	}
	public void setLOCAL_DIVIDED_BY_NODES_limit_normalizedQuota(Boolean lOCAL_DIVIDED_BY_NODES_limit_normalizedQuota) {
		this.LOCAL_DIVIDED_BY_NODES_limit_normalizedQuota = lOCAL_DIVIDED_BY_NODES_limit_normalizedQuota;
	}

	protected boolean gestioneHttpHeadersRidefinita = false;
	protected boolean disabledHttpHeaders = false;
	protected boolean disabledHttpHeaders_limit = false;
	protected boolean forceHttpHeaders_limit_no_windows = false;
	protected boolean forceHttpHeaders_limit_windows = false;
	protected boolean disabledHttpHeaders_remaining = false;
	protected boolean disabledHttpHeaders_reset = false;
	protected boolean disabledHttpHeaders_retryAfter = false;
	protected boolean forceDisabledHttpHeaders_retryAfter_backoff = false;
	protected int forceHttpHeaders_retryAfter_backoff = -1;
	
	// console config
	protected String syncMode = Costanti.VALUE_MODALITA_SINCRONIZZAZIONE_DEFAULT;
	protected String impl = null;
	protected String count = null;
	protected String engineType = null;
	protected Long gestorePolicyConfigDate = null;
	
	protected String httpMode = Costanti.VALUE_HTTP_HEADER_DEFAULT;
	protected String httpMode_limit = null; 
	protected String httpMode_remaining = null; 
	protected String httpMode_reset = null;
	protected String httpMode_retry_after = null;
	protected String httpMode_retry_after_backoff = null; 
		
	public AbstractPolicyConfiguration(){
		// usato da console
	}
	public AbstractPolicyConfiguration(boolean runtime) throws Exception{
		this.initRuntimeInfo(runtime);
	}
	public AbstractPolicyConfiguration(List<Proprieta> p) throws Exception{
		this(p, null, true);
	}
	public AbstractPolicyConfiguration(List<Proprieta> p, List<PolicyGroupByActiveThreadsType> tipiSupportati, boolean runtime) throws Exception{
		if(p!=null && !p.isEmpty()) {
			
			if(tipiSupportati==null) {
				if(runtime) {
					tipiSupportati = new ArrayList<PolicyGroupByActiveThreadsType>();
					for (PolicyGroupByActiveThreadsType type : PolicyGroupByActiveThreadsType.values()) {
						tipiSupportati.add(type);
					}
				}
				else {
					throw new Exception("Tipi supportati non indicati");
				}
			}
			
			String gestorePolicyConfigDate = getValue(p, Costanti.GESTORE_CONFIG_DATE, null);
			if(gestorePolicyConfigDate!=null) {
				this.gestorePolicyConfigDate = Long.valueOf(gestorePolicyConfigDate);
			}
			
			this.syncMode = getValue(p, Costanti.MODALITA_SINCRONIZZAZIONE, Costanti.VALUE_MODALITA_SINCRONIZZAZIONE_DEFAULT);
			if(!Costanti.getVALUES_MODALITA_SINCRONIZZAZIONE(tipiSupportati).contains(this.syncMode)) {
				throw new Exception("Value '"+this.syncMode+"' unsupported for property '"+Costanti.MODALITA_SINCRONIZZAZIONE+"'");
			}
			
			if(Costanti.VALUE_MODALITA_SINCRONIZZAZIONE_DISTRIBUITA.equals(this.syncMode)) {
				this.impl = getValue(p, Costanti.MODALITA_IMPLEMENTAZIONE, null);
				if(this.impl==null) {
					if(runtime) {
						throw new Exception("Value undefined for property '"+Costanti.MODALITA_IMPLEMENTAZIONE+"'");
					}
					else {
						// default
						this.impl = Costanti.getVALUES_MODALITA_IMPLEMENTAZIONE(tipiSupportati).get(0);
					}
				}  
				if(!Costanti.getVALUES_MODALITA_IMPLEMENTAZIONE(tipiSupportati).contains(this.impl)) {
					throw new Exception("Value '"+this.impl+"' unsupported for property '"+Costanti.MODALITA_IMPLEMENTAZIONE+"'");
				}
				
				if(Costanti.VALUE_MODALITA_IMPLEMENTAZIONE_HAZELCAST.equals(this.impl) ||
						Costanti.VALUE_MODALITA_IMPLEMENTAZIONE_REDIS.equals(this.impl)) {
					
					this.count = getValue(p, Costanti.MODALITA_CONTATORI, null);
					if(this.count==null) {
						if(runtime) {
							throw new Exception("Value undefined for property '"+Costanti.MODALITA_CONTATORI+"'");
						}
						else {
							// default
							this.count = Costanti.getVALUES_MODALITA_CONTATORI(tipiSupportati, this.impl).get(0);
						}
					}  
					if(!Costanti.getVALUES_MODALITA_CONTATORI(tipiSupportati, this.impl).contains(this.count)) {
						if(runtime) {
							throw new Exception("Value '"+this.count+"' unsupported for property '"+Costanti.MODALITA_CONTATORI+"'");
						}
						else {
							// default (e' cambiato impl)
							this.count = Costanti.getVALUES_MODALITA_CONTATORI(tipiSupportati, this.impl).get(0);
						}
					}
					
					this.engineType = getValue(p, Costanti.MODALITA_TIPOLOGIA, null);
					if(this.engineType==null) {
						if(runtime) {
							throw new Exception("Value undefined for property '"+Costanti.MODALITA_TIPOLOGIA+"'");
						}
						else {
							// default
							this.engineType = Costanti.getVALUES_MODALITA_TIPOLOGIA(tipiSupportati, this.impl, this.count).get(0);
						}
					}  
					if(!Costanti.getVALUES_MODALITA_TIPOLOGIA(tipiSupportati, this.impl, this.count).contains(this.engineType)) {
						if(runtime) {
							throw new Exception("Value '"+this.engineType+"' unsupported for property '"+Costanti.MODALITA_TIPOLOGIA+"'");
						}
						else {
							// default (e' cambiato impl o count)
							this.engineType = Costanti.getVALUES_MODALITA_TIPOLOGIA(tipiSupportati, this.impl, this.count).get(0);
						}
					}
				}
			}
			
			this.httpMode = getValue(p, Costanti.MODALITA_GENERAZIONE_HEADER_HTTP, Costanti.VALUE_HTTP_HEADER_DEFAULT);
			if(!Costanti.VALUES_MODALITA_GENERAZIONE_HEADER_HTTP.contains(this.httpMode)) {
				throw new Exception("Value '"+this.httpMode+"' unsupported for property '"+Costanti.MODALITA_GENERAZIONE_HEADER_HTTP+"'");
			}
			if(Costanti.VALUE_HTTP_HEADER_RIDEFINITO.equals(this.httpMode)) {
			
				this.httpMode_limit = getValue(p, Costanti.MODALITA_GENERAZIONE_HEADER_HTTP_LIMIT, Costanti.VALUE_HTTP_HEADER_DEFAULT);
				if(!Costanti.VALUES_MODALITA_GENERAZIONE_HEADER_HTTP_LIMIT.contains(this.httpMode_limit)) {
					throw new Exception("Value '"+this.httpMode_limit+"' unsupported for property '"+Costanti.MODALITA_GENERAZIONE_HEADER_HTTP_LIMIT+"'");
				}
				
				this.httpMode_remaining = getValue(p, Costanti.MODALITA_GENERAZIONE_HEADER_HTTP_REMAINING, Costanti.VALUE_HTTP_HEADER_DEFAULT);
				if(!Costanti.VALUES_MODALITA_GENERAZIONE_HEADER_HTTP_REMAINING.contains(this.httpMode_remaining)) {
					throw new Exception("Value '"+this.httpMode_remaining+"' unsupported for property '"+Costanti.MODALITA_GENERAZIONE_HEADER_HTTP_REMAINING+"'");
				}
				
				this.httpMode_reset = getValue(p, Costanti.MODALITA_GENERAZIONE_HEADER_HTTP_RESET, Costanti.VALUE_HTTP_HEADER_DEFAULT);
				if(!Costanti.VALUES_MODALITA_GENERAZIONE_HEADER_HTTP_RESET.contains(this.httpMode_reset)) {
					throw new Exception("Value '"+this.httpMode_reset+"' unsupported for property '"+Costanti.MODALITA_GENERAZIONE_HEADER_HTTP_RESET+"'");
				}
				
				this.httpMode_retry_after = getValue(p, Costanti.MODALITA_GENERAZIONE_HEADER_HTTP_RETRY_AFTER, Costanti.VALUE_HTTP_HEADER_DEFAULT);
				if(!Costanti.VALUES_MODALITA_GENERAZIONE_HEADER_HTTP_RETRY_AFTER.contains(this.httpMode_retry_after)) {
					throw new Exception("Value '"+this.httpMode_retry_after+"' unsupported for property '"+Costanti.MODALITA_GENERAZIONE_HEADER_HTTP_RETRY_AFTER+"'");
				}
				
				if(Costanti.VALUE_HTTP_HEADER_ABILITATO_BACKOFF.equals(this.httpMode_retry_after)) {
					this.httpMode_retry_after_backoff = getValue(p, Costanti.MODALITA_GENERAZIONE_HEADER_HTTP_RETRY_AFTER_BACKOFF_SECONDS, null);
				}
			}
		}
		this.initRuntimeInfo(runtime);
	}
	
	private static String getValue(List<Proprieta> p, String name, String defaultV) {
		String tmp = null;
		if(p!=null && !p.isEmpty()) {
			for (Proprieta proprieta : p) {
				if(proprieta.getNome().equals(name)) {
					tmp = proprieta.getValore();
					break;
				}
			}
		}
		if(tmp!=null) {
			tmp = tmp.trim();
		}
		if(tmp!=null && StringUtils.isNotEmpty(tmp)) {
			return tmp;
		}
		else {
			return defaultV;
		}
	}
	
	
	private static Map<String, PolicyGroupByActiveThreadsType> mappingCostantiToHazelcastExact = Map.of(
			Costanti.VALUE_MODALITA_TIPOLOGIA_HAZELCAST_CONTATORI_ATOMIC_LONG, PolicyGroupByActiveThreadsType.HAZELCAST_ATOMIC_LONG,
			Costanti.VALUE_MODALITA_TIPOLOGIA_HAZELCAST_FULL_SYNC, PolicyGroupByActiveThreadsType.HAZELCAST_MAP
			);
	
	
	private static Map<String, PolicyGroupByActiveThreadsType> mappingCostantiToHazelcastApprox = Map.of(	
			Costanti.VALUE_MODALITA_TIPOLOGIA_HAZELCAST_CONTATORI_PNCOUNTER,  PolicyGroupByActiveThreadsType.HAZELCAST_PNCOUNTER,
			Costanti.VALUE_MODALITA_TIPOLOGIA_HAZELCAST_CONTATORI_ATOMIC_LONG_ASYNC, PolicyGroupByActiveThreadsType.HAZELCAST_ATOMIC_LONG_ASYNC,
			Costanti.VALUE_MODALITA_TIPOLOGIA_HAZELCAST_NEAR_CACHE, PolicyGroupByActiveThreadsType.HAZELCAST_NEAR_CACHE,
			Costanti.VALUE_MODALITA_TIPOLOGIA_HAZELCAST_LOCAL_CACHE, PolicyGroupByActiveThreadsType.HAZELCAST_LOCAL_CACHE
		);
	
	private static Map<String, PolicyGroupByActiveThreadsType> mappingCostantiToHazelcastInconsistent = Map.of(	
			Costanti.VALUE_MODALITA_TIPOLOGIA_HAZELCAST_REMOTE_SYNC, PolicyGroupByActiveThreadsType.HAZELCAST_NEAR_CACHE_UNSAFE_SYNC_MAP,
			Costanti.VALUE_MODALITA_TIPOLOGIA_HAZELCAST_REMOTE_ASYNC, PolicyGroupByActiveThreadsType.HAZELCAST_NEAR_CACHE_UNSAFE_ASYNC_MAP,
			Costanti.VALUE_MODALITA_TIPOLOGIA_HAZELCAST_REPLICATED_MAP, PolicyGroupByActiveThreadsType.HAZELCAST_REPLICATED_MAP
		);
	
	
	private static Map<String, PolicyGroupByActiveThreadsType> mappingCostantiToRedisExact = Map.of(
			Costanti.VALUE_MODALITA_TIPOLOGIA_REDIS_CONTATORI_ATOMIC_LONG, PolicyGroupByActiveThreadsType.REDISSON_ATOMIC_LONG,
			Costanti.VALUE_MODALITA_TIPOLOGIA_REDIS_REDDISSON_MAP, PolicyGroupByActiveThreadsType.REDISSON_MAP
		);
			
	
	private static Map<String, PolicyGroupByActiveThreadsType> mappingCostantiToRedisInconsistent= Map.of(
			Costanti.VALUE_MODALITA_TIPOLOGIA_REDIS_CONTATORI_LONGADDER, PolicyGroupByActiveThreadsType.REDISSON_LONGADDER
			);
			
	
	private void initRuntimeInfo(boolean all) throws Exception {
		if(!Costanti.VALUE_MODALITA_SINCRONIZZAZIONE_DEFAULT.equals(this.syncMode)) {
			if(Costanti.VALUE_MODALITA_SINCRONIZZAZIONE_LOCALE.equals(this.syncMode)) {
				this.type = PolicyGroupByActiveThreadsType.LOCAL;
			}
			else if(Costanti.VALUE_MODALITA_SINCRONIZZAZIONE_LOCALE_SUDDIVISA_TRA_NODI.equals(this.syncMode)) {
				this.type = PolicyGroupByActiveThreadsType.LOCAL_DIVIDED_BY_NODES;
			}
			else if(Costanti.VALUE_MODALITA_SINCRONIZZAZIONE_DISTRIBUITA.equals(this.syncMode)) {
				if(Costanti.VALUE_MODALITA_IMPLEMENTAZIONE_DATABASE.equals(this.impl)) {
					this.type = PolicyGroupByActiveThreadsType.DATABASE;
				}
				else if(Costanti.VALUE_MODALITA_IMPLEMENTAZIONE_HAZELCAST.equals(this.impl)) {
					if(Costanti.VALUE_MODALITA_CONTATORI_EXACT.equals(this.count)) {
						this.type = mappingCostantiToHazelcastExact.get(this.engineType);
					}
					else if(Costanti.VALUE_MODALITA_CONTATORI_APPROXIMATED.equals(this.count)) {
						this.type = mappingCostantiToHazelcastApprox.get(this.engineType);	
					}
					else if(Costanti.VALUE_MODALITA_CONTATORI_INCONSISTENT.equals(this.count)) {
						this.type = mappingCostantiToHazelcastInconsistent.get(this.engineType);	
					}
				}
				else if(Costanti.VALUE_MODALITA_IMPLEMENTAZIONE_REDIS.equals(this.impl)) {
					if(Costanti.VALUE_MODALITA_CONTATORI_EXACT.equals(this.count)) {
						this.type = mappingCostantiToRedisExact.get(this.engineType);
					}
//					else if(Costanti.VALUE_MODALITA_CONTATORI_APPROXIMATED.equals(this.count)) {
//						this.type = mappingCostantiToRedisApprox.get(this.engineType);	
//					}
					else if(Costanti.VALUE_MODALITA_CONTATORI_INCONSISTENT.equals(this.count)) {
						this.type = mappingCostantiToRedisInconsistent.get(this.engineType);	
					}
				}
			}
		}
		
		if(all) {
			initRuntimeInfoAll();
		}
	
	}
	protected abstract void initRuntimeInfoAll() throws Exception;
	
	public void saveIn(List<Proprieta> list) throws Exception {
		this.initRuntimeInfo(false);
		if(this.type!=null) {
			list.add(newProprieta(Costanti.GESTORE, this.type.name()));
		}
		if(this.gestorePolicyConfigDate!=null) {
			list.add(newProprieta(Costanti.GESTORE_CONFIG_DATE, this.gestorePolicyConfigDate.longValue()+""));
		}
		list.add(newProprieta(Costanti.MODALITA_SINCRONIZZAZIONE, this.syncMode));
		if(this.impl!=null && StringUtils.isNotEmpty(this.impl)) {
			list.add(newProprieta(Costanti.MODALITA_IMPLEMENTAZIONE, this.impl));
		}
		if(this.count!=null && StringUtils.isNotEmpty(this.count)) {
			list.add(newProprieta(Costanti.MODALITA_CONTATORI, this.count));
		}
		if(this.engineType!=null && StringUtils.isNotEmpty(this.engineType)) {
			list.add(newProprieta(Costanti.MODALITA_TIPOLOGIA, this.engineType));
		}
		
		list.add(newProprieta(Costanti.MODALITA_GENERAZIONE_HEADER_HTTP, this.httpMode));
		if(this.httpMode_limit!=null && StringUtils.isNotEmpty(this.httpMode_limit)) {
			list.add(newProprieta(Costanti.MODALITA_GENERAZIONE_HEADER_HTTP_LIMIT, this.httpMode_limit));
		}
		if(this.httpMode_remaining!=null && StringUtils.isNotEmpty(this.httpMode_remaining)) {
			list.add(newProprieta(Costanti.MODALITA_GENERAZIONE_HEADER_HTTP_REMAINING, this.httpMode_remaining));
		}
		if(this.httpMode_reset!=null && StringUtils.isNotEmpty(this.httpMode_reset)) {
			list.add(newProprieta(Costanti.MODALITA_GENERAZIONE_HEADER_HTTP_RESET, this.httpMode_reset));
		}
		if(this.httpMode_retry_after!=null && StringUtils.isNotEmpty(this.httpMode_retry_after)) {
			list.add(newProprieta(Costanti.MODALITA_GENERAZIONE_HEADER_HTTP_RETRY_AFTER, this.httpMode_retry_after));
		}
		if(this.httpMode_retry_after_backoff!=null && StringUtils.isNotEmpty(this.httpMode_retry_after_backoff)) {
			list.add(newProprieta(Costanti.MODALITA_GENERAZIONE_HEADER_HTTP_RETRY_AFTER_BACKOFF_SECONDS, this.httpMode_retry_after_backoff));
		}
	}
	private Proprieta newProprieta(String nome, String valore) {
		Proprieta p = new Proprieta();
		p.setNome(nome);
		p.setValore(valore);
		return p;
	}
	
	public Long getGestorePolicyConfigDate() {
		return this.gestorePolicyConfigDate;
	}
	public void setGestorePolicyConfigDate(Long gestorePolicyConfigDate) {
		this.gestorePolicyConfigDate = gestorePolicyConfigDate;
	}
	public String getSyncMode() {
		return this.syncMode;
	}
	public void setSyncMode(String syncMode) {
		if(syncMode!=null && StringUtils.isNotEmpty(syncMode)) {
			this.syncMode = syncMode;
		}
		else {
			this.syncMode = Costanti.VALUE_MODALITA_SINCRONIZZAZIONE_DEFAULT;
		}
	}
	public String getImpl() {
		return this.impl;
	}
	public void setImpl(String impl) {
		if(impl!=null && StringUtils.isNotEmpty(impl)) {
			this.impl = impl;
		}
		else {
			this.impl = null;
		}
	}
	public String getCount() {
		return this.count;
	}
	public void setCount(String count) {
		if(count!=null && StringUtils.isNotEmpty(count)) {
			this.count = count;
		}
		else {
			this.count = null;
		}
	}
	public String getEngineType() {
		return this.engineType;
	}
	public void setEngineType(String engineType) {
		if(engineType!=null && StringUtils.isNotEmpty(engineType)) {
			this.engineType = engineType;
		}
		else {
			this.engineType = null;
		}
	}
	public String getHttpMode() {
		return this.httpMode;
	}
	public void setHttpMode(String httpMode) {
		if(httpMode!=null && StringUtils.isNotEmpty(httpMode)) {
			this.httpMode = httpMode;
		}
		else {
			this.httpMode = Costanti.VALUE_HTTP_HEADER_DEFAULT;
		}
	}
	public String getHttpMode_limit() {
		return this.httpMode_limit;
	}
	public void setHttpMode_limit(String httpMode_limit) {
		if(httpMode_limit!=null && StringUtils.isNotEmpty(httpMode_limit)) {
			this.httpMode_limit = httpMode_limit;
		}
		else {
			this.httpMode_limit = null;
		}
	}
	public String getHttpMode_remaining() {
		return this.httpMode_remaining;
	}
	public void setHttpMode_remaining(String httpMode_remaining) {
		if(httpMode_remaining!=null && StringUtils.isNotEmpty(httpMode_remaining)) {
			this.httpMode_remaining = httpMode_remaining;
		}
		else {
			this.httpMode_remaining = null;
		}
	}
	public String getHttpMode_reset() {
		return this.httpMode_reset;
	}
	public void setHttpMode_reset(String httpMode_reset) {
		if(httpMode_reset!=null && StringUtils.isNotEmpty(httpMode_reset)) {
			this.httpMode_reset = httpMode_reset;
		}
		else {
			this.httpMode_reset = null;
		}
	}
	public String getHttpMode_retry_after() {
		return this.httpMode_retry_after;
	}
	public void setHttpMode_retry_after(String httpMode_retry_after) {
		if(httpMode_retry_after!=null && StringUtils.isNotEmpty(httpMode_retry_after)) {
			this.httpMode_retry_after = httpMode_retry_after;
		}
		else {
			this.httpMode_retry_after = null;
		}
	}
	public String getHttpMode_retry_after_backoff() {
		return this.httpMode_retry_after_backoff;
	}
	public void setHttpMode_retry_after_backoff(String httpMode_retry_after_backoff) {
		if(httpMode_retry_after_backoff!=null && StringUtils.isNotEmpty(httpMode_retry_after_backoff)) {
			this.httpMode_retry_after_backoff = httpMode_retry_after_backoff;
		}
		else {
			this.httpMode_retry_after_backoff = null;
		}
	}
	
	public boolean isGestionePolicyRidefinita() {
		return this.gestionePolicyRidefinita;
	}
	public PolicyGroupByActiveThreadsType getType() {
		return this.type;
	}
	public boolean isLOCAL_DIVIDED_BY_NODES_remaining_zeroValue() {
		return this.LOCAL_DIVIDED_BY_NODES_remaining_zeroValue;
	}
	public boolean isLOCAL_DIVIDED_BY_NODES_limit_roundingDown() {
		return this.LOCAL_DIVIDED_BY_NODES_limit_roundingDown;
	}
	public boolean isLOCAL_DIVIDED_BY_NODES_limit_normalizedQuota() {
		return this.LOCAL_DIVIDED_BY_NODES_limit_normalizedQuota;
	}

	public boolean isGestioneHttpHeadersRidefinita() {
		return this.gestioneHttpHeadersRidefinita;
	}
	public boolean isDisabledHttpHeaders() {
		return this.disabledHttpHeaders;
	}
	public boolean isDisabledHttpHeaders_limit() {
		return this.disabledHttpHeaders_limit;
	}
	public boolean isForceHttpHeaders_limit_no_windows() {
		return this.forceHttpHeaders_limit_no_windows;
	}
	public boolean isForceHttpHeaders_limit_windows() {
		return this.forceHttpHeaders_limit_windows;
	}
	public boolean isDisabledHttpHeaders_remaining() {
		return this.disabledHttpHeaders_remaining;
	}
	public boolean isDisabledHttpHeaders_reset() {
		return this.disabledHttpHeaders_reset;
	}
	public boolean isDisabledHttpHeaders_retryAfter() {
		return this.disabledHttpHeaders_retryAfter;
	}
	public boolean isForceDisabledHttpHeaders_retryAfter_backoff() {
		return this.forceDisabledHttpHeaders_retryAfter_backoff;
	}
	public int getForceHttpHeaders_retryAfter_backoff() {
		return this.forceHttpHeaders_retryAfter_backoff;
	}
}
