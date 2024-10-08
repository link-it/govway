/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it). 
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

import org.apache.commons.lang.StringUtils;
import org.apache.hc.client5.http.HttpRoute;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.core5.pool.PoolStats;
import org.apache.hc.core5.util.TimeValue;
import org.openspcoop2.pdd.core.connettori.AbstractConnettoreConnectionEvictor;

/**
 * ConnettoreHTTPCORE5_connectionEvictor
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ConnettoreHTTPCOREConnectionEvictor extends AbstractConnettoreConnectionEvictor {

	private static final String HTTPCORE_PREFIX = "["+ConnettoreHTTPCORE.ID_HTTPCORE+"] ";
	
    public ConnettoreHTTPCOREConnectionEvictor(boolean debug, int sleepTimeSeconds, int closeIdleConnectionsAfterSeconds) {
        super(debug, sleepTimeSeconds, closeIdleConnectionsAfterSeconds,
        		ConnettoreHTTPCOREConnectionManager.mapConnection, ConnettoreHTTPCORE.ID_HTTPCORE);
    }

    @Override
    protected boolean check() {
    	
    	if(ConnettoreHTTPCOREConnectionManager.mapPoolingConnectionManager!=null && !ConnettoreHTTPCOREConnectionManager.mapPoolingConnectionManager.isEmpty()) {
    		
    		this.logDebug(HTTPCORE_PREFIX+"ConnectionManager attivi: "+ConnettoreHTTPCOREConnectionManager.mapPoolingConnectionManager.size());	
    		
			for (String key : ConnettoreHTTPCOREConnectionManager.mapPoolingConnectionManager.keySet()) {
				if(key!=null) {
					check(key);
				}
			}
			
			return ConnettoreHTTPCOREConnectionManager.USE_POOL_CONNECTION; // devo continuare l'analisi delle connessione solo se uso un pool delle connessioni; ha senso solo per il nio
		}
    	
    	return false;
    	
    }
    private void check(String key) {
    	PoolingHttpClientConnectionManager connMgr = ConnettoreHTTPCOREConnectionManager.mapPoolingConnectionManager.get(key);
		ConnettoreHTTPCOREConnection connection = ConnettoreHTTPCOREConnectionManager.mapConnection.get(key);
		
    	// DEBUG
    	if(this.debug) {
    		
    		PoolStats totalStats = connMgr.getTotalStats();
    	    this.logDebug(HTTPCORE_PREFIX+"("+key+") stats: "+totalStats.toString());	
    		/**this.logDebug("  Totali - In uso: " + totalStats.getLeased() + "; Disponibili: " +
                       totalStats.getAvailable() + "; In attesa: " + totalStats.getPending());*/
    		
    		if(connMgr.getRoutes()!=null && !connMgr.getRoutes().isEmpty()) {
    			for (HttpRoute route : connMgr.getRoutes()) {
    				PoolStats routeStats = connMgr.getStats(route);
    				this.logDebug(HTTPCORE_PREFIX+"("+key+") route ["+route.toString()+"]: "+routeStats.toString());	
    				/**this.logDebug("  Rotta: " + route + " - In uso: " + routeStats.getLeased() +
	                           "; Disponibili: " + routeStats.getAvailable() +
	                           "; In attesa: " + routeStats.getPending());*/
				}
    		}

    		if(connection!=null) {
    			String status = connection.getStatus();
    			if(status!=null && StringUtils.isNotEmpty(status)) {
    				this.logDebug(HTTPCORE_PREFIX+"("+key+"): status: "+connection.getStatus());
    			}
    		}
    		
    	}
    	
    	if(connMgr!=null) {
	    	// Close expired connections
	    	connMgr.closeExpired();
	    	// Optionally, close connections
	    	// that have been idle longer than x sec
	    	connMgr.closeIdle(TimeValue.ofSeconds(this.closeIdleConnectionsAfterSeconds));
    	}
    }

}