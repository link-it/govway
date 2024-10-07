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
import org.openspcoop2.pdd.core.connettori.ConnettoreHttpPoolParams;

/**
 * ConnettoreHttpPoolParams
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ConnettoreHttpPoolParamsBuilder {

	private ConnettoreHttpPoolParamsBuilder() {}
	
	public static ConnettoreHttpPoolParams newConnettoreHttpPoolParams(OpenSPCoop2Properties openspcoopProperties, URL url, List<Proprieta> proprietaPorta) {
		
		ConnettoreHttpPoolParams c= new ConnettoreHttpPoolParams();
		
		String host = url.getHost();
		int port = url.getPort();
		
		Integer maxTotal = openspcoopProperties.getBIOConfigSyncClientMaxTotal(host, port);
		maxTotal = CostantiProprieta.getConnettoriConnectionPoolMaxTotal(proprietaPorta, maxTotal);
		c.setMaxTotal(maxTotal);
		
		Integer defaultMaxPerRoute = openspcoopProperties.getBIOConfigSyncClientMaxPerRoute(host, port);
		defaultMaxPerRoute = CostantiProprieta.getConnettoriConnectionPoolMaxPerRoute(proprietaPorta, defaultMaxPerRoute);
		c.setDefaultMaxPerRoute(defaultMaxPerRoute);
		
		Integer validateAfterInactivityDefault = openspcoopProperties.getBIOConfigSyncClientValidateAfterInactivity(host, port);
		Integer validateAfterInactivity = CostantiProprieta.getConnettoriConnectionPoolValidateAfterInactivity(proprietaPorta, validateAfterInactivityDefault);
		c.setValidateAfterInactivity(validateAfterInactivity);
		
		return c;
	}
	
}
