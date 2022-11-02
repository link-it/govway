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
import java.util.List;

import org.openspcoop2.core.config.Proprieta;
import org.openspcoop2.core.controllo_traffico.beans.AbstractPolicyConfiguration;
import org.openspcoop2.core.controllo_traffico.constants.Costanti;
import org.openspcoop2.core.controllo_traffico.driver.PolicyGroupByActiveThreadsType;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;

/**
 * Configuration
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class PolicyConfiguration extends AbstractPolicyConfiguration implements Serializable {

	private static final long serialVersionUID = 1L;

	public PolicyConfiguration() {
		super();
	}

	public PolicyConfiguration(boolean runtime) throws Exception {
		super(runtime);
	}

	public PolicyConfiguration(List<Proprieta> p, List<PolicyGroupByActiveThreadsType> tipiSupportati, boolean runtime)
			throws Exception {
		super(p, tipiSupportati, runtime);
	}

	public PolicyConfiguration(List<Proprieta> p) throws Exception {
		super(p);
	}
	
	@Override
	protected void initRuntimeInfoAll() throws Exception{
			
		OpenSPCoop2Properties op2Properties = OpenSPCoop2Properties.getInstance();
		
		// ** gestione policy **
		
		if(!Costanti.VALUE_MODALITA_SINCRONIZZAZIONE_DEFAULT.equals(this.syncMode)) {
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
		case HAZELCAST_MAP:
		case HAZELCAST_NEAR_CACHE:
		case HAZELCAST_LOCAL_CACHE:
		case HAZELCAST_NEAR_CACHE_UNSAFE_SYNC_MAP:
		case HAZELCAST_NEAR_CACHE_UNSAFE_ASYNC_MAP:
		case HAZELCAST_REPLICATED_MAP:
		case HAZELCAST_PNCOUNTER:
		case REDISSON_MAP:
		case HAZELCAST_ATOMIC_LONG:
		case HAZELCAST_ATOMIC_LONG_ASYNC:
		case REDISSON_ATOMIC_LONG:
		case REDISSON_LONGADDER:
			break;
		}
		
		// ** gestione http ** 
		
		if(!Costanti.VALUE_HTTP_HEADER_DEFAULT.equals(this.httpMode)) {
			this.gestioneHttpHeadersRidefinita = true;
		}
		
		if(Costanti.VALUE_HTTP_HEADER_RIDEFINITO.equals(this.httpMode)) {
			
			if(Costanti.VALUE_HTTP_HEADER_DISABILITATO.equals(this.httpMode_limit)) {
				this.disabledHttpHeaders_limit = true;
			}
			else if(Costanti.VALUE_HTTP_HEADER_ABILITATO_NO_WINDOWS.equals(this.httpMode_limit)) {
				this.forceHttpHeaders_limit_no_windows = true;
			}
			else if(Costanti.VALUE_HTTP_HEADER_ABILITATO_WINDOWS.equals(this.httpMode_limit)) {
				this.forceHttpHeaders_limit_windows = true;
			}
			
			if(Costanti.VALUE_HTTP_HEADER_DISABILITATO.equals(this.httpMode_remaining)) {
				this.disabledHttpHeaders_remaining = true;
			}
			
			if(Costanti.VALUE_HTTP_HEADER_DISABILITATO.equals(this.httpMode_reset)) {
				this.disabledHttpHeaders_reset = true;
			}
			
			if(Costanti.VALUE_HTTP_HEADER_DISABILITATO.equals(this.httpMode_retry_after)) {
				this.disabledHttpHeaders_retryAfter = true;
			}
			else if(Costanti.VALUE_HTTP_HEADER_ABILITATO_NO_BACKOFF.equals(this.httpMode_retry_after)) {
				this.forceDisabledHttpHeaders_retryAfter_backoff = true;
			}
			else if(Costanti.VALUE_HTTP_HEADER_ABILITATO_BACKOFF.equals(this.httpMode_retry_after) && this.httpMode_retry_after_backoff!=null) {
				this.forceHttpHeaders_retryAfter_backoff = Integer.valueOf(this.httpMode_retry_after_backoff);
			}
		}
		else if(Costanti.VALUE_HTTP_HEADER_DISABILITATO.equals(this.httpMode)) {
			this.disabledHttpHeaders = true;
		}

	}

}
