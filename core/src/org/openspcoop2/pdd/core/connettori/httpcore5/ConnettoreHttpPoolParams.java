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
package org.openspcoop2.pdd.core.connettori.httpcore5;

import java.net.URL;
import java.util.List;

import org.openspcoop2.core.config.Proprieta;
import org.openspcoop2.pdd.config.CostantiProprieta;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;

/**
 * ConnettoreHttpPoolParams
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ConnettoreHttpPoolParams {

	public ConnettoreHttpPoolParams(OpenSPCoop2Properties openspcoopProperties, URL url, List<Proprieta> proprietaPorta) {
		
		String host = url.getHost();
		int port = url.getPort();
		
		this.maxTotal = openspcoopProperties.getBIOConfigSyncClientMaxTotal(host, port);
		this.maxTotal = CostantiProprieta.getConnettoriConnectionPoolMaxTotal(proprietaPorta, this.maxTotal);
		
		this.defaultMaxPerRoute = openspcoopProperties.getBIOConfigSyncClientMaxPerRoute(host, port);
		this.defaultMaxPerRoute = CostantiProprieta.getConnettoriConnectionPoolMaxPerRoute(proprietaPorta, this.defaultMaxPerRoute);
		
		Integer validateAfterInactivityDefault = openspcoopProperties.getBIOConfigSyncClientValidateAfterInactivity(host, port);
		this.validateAfterInactivity = CostantiProprieta.getConnettoriConnectionPoolValidateAfterInactivity(proprietaPorta, validateAfterInactivityDefault);
	}
	
	private Integer validateAfterInactivity; 
	private int maxTotal;
	private int defaultMaxPerRoute;
	
	public Integer getValidateAfterInactivity() {
		return this.validateAfterInactivity;
	}
	public void setValidateAfterInactivity(Integer validateAfterInactivity) {
		if(validateAfterInactivity!=null && validateAfterInactivity.intValue()>0) {
			this.validateAfterInactivity = validateAfterInactivity;
		}
	}
	public int getMaxTotal() {
		return this.maxTotal;
	}
	public void setMaxTotal(int maxTotal) {
		this.maxTotal = maxTotal;
	}
	public int getDefaultMaxPerRoute() {
		return this.defaultMaxPerRoute;
	}
	public void setDefaultMaxPerRoute(int defaultMaxPerRoute) {
		this.defaultMaxPerRoute = defaultMaxPerRoute;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("maxTotal[").append(this.maxTotal).append("]");
		sb.append(" maxPerRoute[").append(this.defaultMaxPerRoute).append("]");
		if(this.validateAfterInactivity!=null && this.validateAfterInactivity.intValue()>0) {
			sb.append(" validateAfterInactivity[").append(this.validateAfterInactivity.intValue()).append("]");
		}
		return sb.toString();
	}
}
