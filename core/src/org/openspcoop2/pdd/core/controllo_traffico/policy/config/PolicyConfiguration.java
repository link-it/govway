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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.config.Proprieta;
import org.openspcoop2.core.controllo_traffico.driver.PolicyGroupByActiveThreadsType;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;

/**
 * Configuration
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class PolicyConfiguration implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	// runtime config
	private boolean gestionePolicyRidefinita = false;
	private PolicyGroupByActiveThreadsType type;
	private Boolean LOCAL_DIVIDED_BY_NODES_remaining_zeroValue = null;
	private Boolean LOCAL_DIVIDED_BY_NODES_limit_roundingDown = null;
	private Boolean LOCAL_DIVIDED_BY_NODES_limit_normalizedQuota = null;
	
	private boolean gestioneHttpHeadersRidefinita = false;
	private boolean disabledHttpHeaders = false;
	private boolean disabledHttpHeaders_limit = false;
	private boolean forceHttpHeaders_limit_no_windows = false;
	private boolean forceHttpHeaders_limit_windows = false;
	private boolean disabledHttpHeaders_remaining = false;
	private boolean disabledHttpHeaders_reset = false;
	private boolean disabledHttpHeaders_retryAfter = false;
	private boolean forceDisabledHttpHeaders_retryAfter_backoff = false;
	private int forceHttpHeaders_retryAfter_backoff = -1;
	
	// console config
	private String syncMode = Constants.VALUE_MODALITA_SINCRONIZZAZIONE_DEFAULT;
	private String impl = null;
	private String count = null;
	private String engineType = null;
	
	private String httpMode = Constants.VALUE_HTTP_HEADER_DEFAULT;
	private String httpMode_limit = null; 
	private String httpMode_remaining = null; 
	private String httpMode_reset = null;
	private String httpMode_retry_after = null;
	private String httpMode_retry_after_backoff = null; 
	
	public PolicyConfiguration(){
		// usato da console
	}
	public PolicyConfiguration(boolean runtime) throws Exception{
		this.initRuntimeInfo(runtime);
	}
	public PolicyConfiguration(List<Proprieta> p) throws Exception{
		this(p, null, true);
	}
	public PolicyConfiguration(List<Proprieta> p, List<PolicyGroupByActiveThreadsType> tipiSupportati, boolean runtime) throws Exception{
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
			
			this.syncMode = getValue(p, Constants.MODALITA_SINCRONIZZAZIONE, Constants.VALUE_MODALITA_SINCRONIZZAZIONE_DEFAULT);
			if(!Constants.getVALUES_MODALITA_SINCRONIZZAZIONE(tipiSupportati).contains(this.syncMode)) {
				throw new Exception("Value '"+this.syncMode+"' unsupported for property '"+Constants.MODALITA_SINCRONIZZAZIONE+"'");
			}
			
			if(Constants.VALUE_MODALITA_SINCRONIZZAZIONE_DISTRIBUITA.equals(this.syncMode)) {
				this.impl = getValue(p, Constants.MODALITA_IMPLEMENTAZIONE, null);
				if(this.impl==null) {
					if(runtime) {
						throw new Exception("Value undefined for property '"+Constants.MODALITA_IMPLEMENTAZIONE+"'");
					}
					else {
						// default
						this.impl = Constants.getVALUES_MODALITA_IMPLEMENTAZIONE(tipiSupportati).get(0);
					}
				}  
				if(!Constants.getVALUES_MODALITA_IMPLEMENTAZIONE(tipiSupportati).contains(this.impl)) {
					throw new Exception("Value '"+this.impl+"' unsupported for property '"+Constants.MODALITA_IMPLEMENTAZIONE+"'");
				}
				
				if(Constants.VALUE_MODALITA_IMPLEMENTAZIONE_HAZELCAST.equals(this.impl)) {
					
					this.count = getValue(p, Constants.MODALITA_CONTATORI, null);
					if(this.count==null) {
						if(runtime) {
							throw new Exception("Value undefined for property '"+Constants.MODALITA_CONTATORI+"'");
						}
						else {
							// default
							this.count = Constants.getVALUES_MODALITA_CONTATORI(tipiSupportati, this.impl).get(0);
						}
					}  
					if(!Constants.getVALUES_MODALITA_CONTATORI(tipiSupportati, this.impl).contains(this.count)) {
						if(runtime) {
							throw new Exception("Value '"+this.count+"' unsupported for property '"+Constants.MODALITA_CONTATORI+"'");
						}
						else {
							// default (e' cambiato impl)
							this.count = Constants.getVALUES_MODALITA_CONTATORI(tipiSupportati, this.impl).get(0);
						}
					}
					
					this.engineType = getValue(p, Constants.MODALITA_TIPOLOGIA, null);
					if(this.engineType==null) {
						if(runtime) {
							throw new Exception("Value undefined for property '"+Constants.MODALITA_TIPOLOGIA+"'");
						}
						else {
							// default
							this.engineType = Constants.getVALUES_MODALITA_TIPOLOGIA(tipiSupportati, this.impl, this.count).get(0);
						}
					}  
					if(!Constants.getVALUES_MODALITA_TIPOLOGIA(tipiSupportati, this.impl, this.count).contains(this.engineType)) {
						if(runtime) {
							throw new Exception("Value '"+this.engineType+"' unsupported for property '"+Constants.MODALITA_TIPOLOGIA+"'");
						}
						else {
							// default (e' cambiato impl o count)
							this.engineType = Constants.getVALUES_MODALITA_TIPOLOGIA(tipiSupportati, this.impl, this.count).get(0);
						}
					}
				}
			}
			
			this.httpMode = getValue(p, Constants.MODALITA_GENERAZIONE_HEADER_HTTP, Constants.VALUE_HTTP_HEADER_DEFAULT);
			if(!Constants.VALUES_MODALITA_GENERAZIONE_HEADER_HTTP.contains(this.httpMode)) {
				throw new Exception("Value '"+this.httpMode+"' unsupported for property '"+Constants.MODALITA_GENERAZIONE_HEADER_HTTP+"'");
			}
			if(Constants.VALUE_HTTP_HEADER_RIDEFINITO.equals(this.httpMode)) {
			
				this.httpMode_limit = getValue(p, Constants.MODALITA_GENERAZIONE_HEADER_HTTP_LIMIT, Constants.VALUE_HTTP_HEADER_DEFAULT);
				if(!Constants.VALUES_MODALITA_GENERAZIONE_HEADER_HTTP_LIMIT.contains(this.httpMode_limit)) {
					throw new Exception("Value '"+this.httpMode_limit+"' unsupported for property '"+Constants.MODALITA_GENERAZIONE_HEADER_HTTP_LIMIT+"'");
				}
				
				this.httpMode_remaining = getValue(p, Constants.MODALITA_GENERAZIONE_HEADER_HTTP_REMAINING, Constants.VALUE_HTTP_HEADER_DEFAULT);
				if(!Constants.VALUES_MODALITA_GENERAZIONE_HEADER_HTTP_REMAINING.contains(this.httpMode_remaining)) {
					throw new Exception("Value '"+this.httpMode_remaining+"' unsupported for property '"+Constants.MODALITA_GENERAZIONE_HEADER_HTTP_REMAINING+"'");
				}
				
				this.httpMode_reset = getValue(p, Constants.MODALITA_GENERAZIONE_HEADER_HTTP_RESET, Constants.VALUE_HTTP_HEADER_DEFAULT);
				if(!Constants.VALUES_MODALITA_GENERAZIONE_HEADER_HTTP_RESET.contains(this.httpMode_reset)) {
					throw new Exception("Value '"+this.httpMode_reset+"' unsupported for property '"+Constants.MODALITA_GENERAZIONE_HEADER_HTTP_RESET+"'");
				}
				
				this.httpMode_retry_after = getValue(p, Constants.MODALITA_GENERAZIONE_HEADER_HTTP_RETRY_AFTER, Constants.VALUE_HTTP_HEADER_DEFAULT);
				if(!Constants.VALUES_MODALITA_GENERAZIONE_HEADER_HTTP_RETRY_AFTER.contains(this.httpMode_retry_after)) {
					throw new Exception("Value '"+this.httpMode_retry_after+"' unsupported for property '"+Constants.MODALITA_GENERAZIONE_HEADER_HTTP_RETRY_AFTER+"'");
				}
				
				if(Constants.VALUE_HTTP_HEADER_ABILITATO_BACKOFF.equals(this.httpMode_retry_after)) {
					this.httpMode_retry_after_backoff = getValue(p, Constants.MODALITA_GENERAZIONE_HEADER_HTTP_RETRY_AFTER_BACKOFF_SECONDS, null);
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
	
	private void initRuntimeInfo(boolean all) throws Exception {
		if(!Constants.VALUE_MODALITA_SINCRONIZZAZIONE_DEFAULT.equals(this.syncMode)) {
			if(Constants.VALUE_MODALITA_SINCRONIZZAZIONE_LOCALE.equals(this.syncMode)) {
				this.type = PolicyGroupByActiveThreadsType.LOCAL;
			}
			else if(Constants.VALUE_MODALITA_SINCRONIZZAZIONE_LOCALE_SUDDIVISA_TRA_NODI.equals(this.syncMode)) {
				this.type = PolicyGroupByActiveThreadsType.LOCAL_DIVIDED_BY_NODES;
			}
			else if(Constants.VALUE_MODALITA_SINCRONIZZAZIONE_DISTRIBUITA.equals(this.syncMode)) {
				if(Constants.VALUE_MODALITA_IMPLEMENTAZIONE_DATABASE.equals(this.impl)) {
					this.type = PolicyGroupByActiveThreadsType.DATABASE;
				}
				else if(Constants.VALUE_MODALITA_IMPLEMENTAZIONE_HAZELCAST.equals(this.impl)) {
					if(Constants.VALUE_MODALITA_CONTATORI_EXACT.equals(this.count)) {
						if(Constants.VALUE_MODALITA_TIPOLOGIA_HAZELCAST_FULL_SYNC.equals(this.engineType)) {
							this.type = PolicyGroupByActiveThreadsType.HAZELCAST;
						}
						else if(Constants.VALUE_MODALITA_TIPOLOGIA_HAZELCAST_NEAR_CACHE.equals(this.engineType)) {
							this.type = PolicyGroupByActiveThreadsType.HAZELCAST_NEAR_CACHE;
						}
						else if(Constants.VALUE_MODALITA_TIPOLOGIA_HAZELCAST_LOCAL_CACHE.equals(this.engineType)) {
							this.type = PolicyGroupByActiveThreadsType.HAZELCAST_LOCAL_CACHE;
						}
						else if(Constants.VALUE_MODALITA_TIPOLOGIA_HAZELCAST_CONTATORI_SINGOLI_PNCOUNTER.equals(this.engineType)) {
							this.type = PolicyGroupByActiveThreadsType.HAZELCAST_PNCOUNTER;
						}
						else if(Constants.VALUE_MODALITA_TIPOLOGIA_HAZELCAST_CONTATORI_SINGOLI_ATOMIC_LONG.equals(this.engineType)) {
							this.type = PolicyGroupByActiveThreadsType.HAZELCAST_ATOMIC_LONG;
						}
						else if(Constants.VALUE_MODALITA_TIPOLOGIA_HAZELCAST_CONTATORI_SINGOLI_ATOMIC_LONG_ASYNC.equals(this.engineType)) {
							this.type = PolicyGroupByActiveThreadsType.HAZELCAST_ATOMIC_LONG_ASYNC;
						}
					}
					else if(Constants.VALUE_MODALITA_CONTATORI_APPROXIMATED.equals(this.count)) {
						if(Constants.VALUE_MODALITA_TIPOLOGIA_HAZELCAST_REMOTE_SYNC.equals(this.engineType)) {
							this.type = PolicyGroupByActiveThreadsType.HAZELCAST_NEAR_CACHE_UNSAFE_SYNC_MAP;
						}
						else if(Constants.VALUE_MODALITA_TIPOLOGIA_HAZELCAST_REMOTE_ASYNC.equals(this.engineType)) {
							this.type = PolicyGroupByActiveThreadsType.HAZELCAST_NEAR_CACHE_UNSAFE_ASYNC_MAP;
						}
					}
				}
				else if(Constants.VALUE_MODALITA_IMPLEMENTAZIONE_REDIS.equals(this.impl)) {
					this.type = PolicyGroupByActiveThreadsType.REDISSON;
				}
			}
		}
		
		if(all) {
			
			OpenSPCoop2Properties op2Properties = OpenSPCoop2Properties.getInstance();
			
			// ** gestione policy **
			
			if(!Constants.VALUE_MODALITA_SINCRONIZZAZIONE_DEFAULT.equals(this.syncMode)) {
				this.gestionePolicyRidefinita = true;
			}
			
			if(this.type==null) {
				this.type = op2Properties.getControlloTrafficoGestorePolicyInMemoryType();
			}
			
			switch (this.type) {
			case LOCAL:
				break;
			case LOCAL_DIVIDED_BY_NODES:
				if(this.LOCAL_DIVIDED_BY_NODES_remaining_zeroValue==null) {
					this.LOCAL_DIVIDED_BY_NODES_remaining_zeroValue = op2Properties.isControlloTrafficoGestorePolicyInMemoryLocalDividedByNodes_remaining_zeroValue();
				}
				if(this.LOCAL_DIVIDED_BY_NODES_limit_roundingDown==null) {
					this.LOCAL_DIVIDED_BY_NODES_limit_roundingDown = op2Properties.isControlloTrafficoGestorePolicyInMemoryLocalDividedByNodes_limit_roundingDown();
				}
				if(this.LOCAL_DIVIDED_BY_NODES_limit_normalizedQuota==null) {
					this.LOCAL_DIVIDED_BY_NODES_limit_normalizedQuota = op2Properties.isControlloTrafficoGestorePolicyInMemoryLocalDividedByNodes_limit_normalizedQuota();
				}
				break;
			case DATABASE:
				break;
			case HAZELCAST:
				break;
			case HAZELCAST_NEAR_CACHE:
				break;
			case HAZELCAST_LOCAL_CACHE:
				break;
			case HAZELCAST_NEAR_CACHE_UNSAFE_SYNC_MAP:
				break;
			case HAZELCAST_NEAR_CACHE_UNSAFE_ASYNC_MAP:
				break;
			case HAZELCAST_PNCOUNTER:
				break;
			case REDISSON:
				break;
			}
			
			// ** gestione http ** 
			
			if(!Constants.VALUE_HTTP_HEADER_DEFAULT.equals(this.httpMode)) {
				this.gestioneHttpHeadersRidefinita = true;
			}
			
			if(Constants.VALUE_HTTP_HEADER_RIDEFINITO.equals(this.httpMode)) {
				
				if(Constants.VALUE_HTTP_HEADER_DISABILITATO.equals(this.httpMode_limit)) {
					this.disabledHttpHeaders_limit = true;
				}
				else if(Constants.VALUE_HTTP_HEADER_ABILITATO_NO_WINDOWS.equals(this.httpMode_limit)) {
					this.forceHttpHeaders_limit_no_windows = true;
				}
				else if(Constants.VALUE_HTTP_HEADER_ABILITATO_WINDOWS.equals(this.httpMode_limit)) {
					this.forceHttpHeaders_limit_windows = true;
				}
				
				if(Constants.VALUE_HTTP_HEADER_DISABILITATO.equals(this.httpMode_remaining)) {
					this.disabledHttpHeaders_remaining = true;
				}
				
				if(Constants.VALUE_HTTP_HEADER_DISABILITATO.equals(this.httpMode_reset)) {
					this.disabledHttpHeaders_reset = true;
				}
				
				if(Constants.VALUE_HTTP_HEADER_DISABILITATO.equals(this.httpMode_retry_after)) {
					this.disabledHttpHeaders_retryAfter = true;
				}
				else if(Constants.VALUE_HTTP_HEADER_ABILITATO_NO_BACKOFF.equals(this.httpMode_retry_after)) {
					this.forceDisabledHttpHeaders_retryAfter_backoff = true;
				}
				else if(Constants.VALUE_HTTP_HEADER_ABILITATO_BACKOFF.equals(this.httpMode_retry_after) && this.httpMode_retry_after_backoff!=null) {
					this.forceHttpHeaders_retryAfter_backoff = Integer.valueOf(this.httpMode_retry_after_backoff);
				}
			}
			else if(Constants.VALUE_HTTP_HEADER_DISABILITATO.equals(this.httpMode)) {
				this.disabledHttpHeaders = true;
			}

		}
	}
	
	public void saveIn(List<Proprieta> list) throws Exception {
		this.initRuntimeInfo(false);
		if(this.type!=null) {
			list.add(newProprieta(Constants.GESTORE, this.type.name()));
		}
		list.add(newProprieta(Constants.MODALITA_SINCRONIZZAZIONE, this.syncMode));
		if(this.impl!=null && StringUtils.isNotEmpty(this.impl)) {
			list.add(newProprieta(Constants.MODALITA_IMPLEMENTAZIONE, this.impl));
		}
		if(this.count!=null && StringUtils.isNotEmpty(this.count)) {
			list.add(newProprieta(Constants.MODALITA_CONTATORI, this.count));
		}
		if(this.engineType!=null && StringUtils.isNotEmpty(this.engineType)) {
			list.add(newProprieta(Constants.MODALITA_TIPOLOGIA, this.engineType));
		}
		
		list.add(newProprieta(Constants.MODALITA_GENERAZIONE_HEADER_HTTP, this.httpMode));
		if(this.httpMode_limit!=null && StringUtils.isNotEmpty(this.httpMode_limit)) {
			list.add(newProprieta(Constants.MODALITA_GENERAZIONE_HEADER_HTTP_LIMIT, this.httpMode_limit));
		}
		if(this.httpMode_remaining!=null && StringUtils.isNotEmpty(this.httpMode_remaining)) {
			list.add(newProprieta(Constants.MODALITA_GENERAZIONE_HEADER_HTTP_REMAINING, this.httpMode_remaining));
		}
		if(this.httpMode_reset!=null && StringUtils.isNotEmpty(this.httpMode_reset)) {
			list.add(newProprieta(Constants.MODALITA_GENERAZIONE_HEADER_HTTP_RESET, this.httpMode_reset));
		}
		if(this.httpMode_retry_after!=null && StringUtils.isNotEmpty(this.httpMode_retry_after)) {
			list.add(newProprieta(Constants.MODALITA_GENERAZIONE_HEADER_HTTP_RETRY_AFTER, this.httpMode_retry_after));
		}
		if(this.httpMode_retry_after_backoff!=null && StringUtils.isNotEmpty(this.httpMode_retry_after_backoff)) {
			list.add(newProprieta(Constants.MODALITA_GENERAZIONE_HEADER_HTTP_RETRY_AFTER_BACKOFF_SECONDS, this.httpMode_retry_after_backoff));
		}
	}
	private Proprieta newProprieta(String nome, String valore) {
		Proprieta p = new Proprieta();
		p.setNome(nome);
		p.setValore(valore);
		return p;
	}
	
	public String getSyncMode() {
		return this.syncMode;
	}
	public void setSyncMode(String syncMode) {
		if(syncMode!=null && StringUtils.isNotEmpty(syncMode)) {
			this.syncMode = syncMode;
		}
		else {
			this.syncMode = Constants.VALUE_MODALITA_SINCRONIZZAZIONE_DEFAULT;
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
			this.httpMode = Constants.VALUE_HTTP_HEADER_DEFAULT;
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
