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
package org.openspcoop2.pdd.core.behaviour.built_in.load_balance;

import java.io.Serializable;

/**
 * ConfigurazioneLoadBalancer
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class LoadBalancerInstance  implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private LoadBalancerPool loadBalancerPool;
	private String connectorSelected;
	
	public String getConnectorSelected() {
		return this.connectorSelected;
	}
	public void setConnectorSelected(String connectorSelected) {
		this.connectorSelected = connectorSelected;
	}
	
	public LoadBalancerPool getLoadBalancerPool() {
		return this.loadBalancerPool;
	}
	public void setLoadBalancerPool(LoadBalancerPool loadBalancerPool) {
		this.loadBalancerPool = loadBalancerPool;
	}

	
}
