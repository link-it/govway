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
package org.openspcoop2.pdd.core.behaviour;

import org.openspcoop2.pdd.core.behaviour.built_in.load_balance.LoadBalancerPool;

/**
 * Behaviour
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class BehaviourLoadBalancer {

	private LoadBalancerPool loadBalancerPool;
	private String connectorName;
	
	public LoadBalancerPool getLoadBalancerPool() {
		return this.loadBalancerPool;
	}
	public void setLoadBalancerPool(LoadBalancerPool loadBalancerPool) {
		this.loadBalancerPool = loadBalancerPool;
	}
	public String getConnectorName() {
		return this.connectorName;
	}
	public void setConnectorName(String connectorName) {
		this.connectorName = connectorName;
	}
}
