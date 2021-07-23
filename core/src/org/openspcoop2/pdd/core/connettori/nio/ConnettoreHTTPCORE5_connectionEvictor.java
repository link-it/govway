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

package org.openspcoop2.pdd.core.connettori.nio;

import org.apache.hc.client5.http.HttpRoute;
import org.apache.hc.client5.http.impl.nio.PoolingAsyncClientConnectionManager;
import org.apache.hc.core5.util.TimeValue;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.utils.threads.BaseThread;
import org.slf4j.Logger;

/**
 * ConnettoreHTTPCORE5_connectionEvictor
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ConnettoreHTTPCORE5_connectionEvictor extends BaseThread {

    private int closeIdleConnectionsAfterSeconds;
    private boolean debug;
    private Logger logConnettori;

    public ConnettoreHTTPCORE5_connectionEvictor(boolean debug, int sleepTimeSeconds, int closeIdleConnectionsAfterSeconds) {
        super();
        this.setTimeout(sleepTimeSeconds);
        this.debug = debug;
        this.closeIdleConnectionsAfterSeconds = closeIdleConnectionsAfterSeconds;
        this.logConnettori = OpenSPCoop2Logger.getLoggerOpenSPCoopConnettori();
    }

    @Override
    protected void process() {
    	
    	if(ConnettoreHTTPCORE5_connectionManager.mapPoolingConnectionManager!=null && !ConnettoreHTTPCORE5_connectionManager.mapPoolingConnectionManager.isEmpty()) {
    		
    		//this.logConnettori.debug("[HTTPCore5] IOReactor attivi: "+ConnettoreHTTPCORE_connectionManager.mapIoReactor.size());	
    		this.logConnettori.debug("[HTTPCore5] ConnectionManager attivi: "+ConnettoreHTTPCORE5_connectionManager.mapPoolingConnectionManager.size());	
    		
			for (String key : ConnettoreHTTPCORE5_connectionManager.mapPoolingConnectionManager.keySet()) {
				if(key!=null) {
					
					//ConnectingIOReactor ioReactor = ConnettoreHTTPCORE_connectionManager.mapIoReactor.get(key);
					PoolingAsyncClientConnectionManager connMgr = ConnettoreHTTPCORE5_connectionManager.mapPoolingConnectionManager.get(key);
					ConnettoreHTTPCORE5_connection connection = ConnettoreHTTPCORE5_connectionManager.mapConnection.get(key);
					
			    	// DEBUG
			    	if(this.debug) {
			    		
			    		this.logConnettori.debug("[HTTPCore5] ("+key+") stats: "+connMgr.getTotalStats().toString());	
			    		
			    		if(connMgr.getRoutes()!=null && !connMgr.getRoutes().isEmpty()) {
			    			for (HttpRoute route : connMgr.getRoutes()) {
			    				this.logConnettori.debug("[HTTPCore5] ("+key+") route ["+route.toString()+"]: "+connMgr.getStats(route));	
							}
			    		}

			    		if(connection!=null) {
			    			this.logConnettori.debug("[HTTPCore5] ("+key+"): status: "+connection.getHttpclient().getStatus());
			    		}
			    		
			    	}
			    	
			    	if(connMgr!=null) {
				    	// Close expired connections
				    	connMgr.closeExpired();
				    	// Optionally, close connections
				    	// that have been idle longer than 30 sec
				    	connMgr.closeIdle(TimeValue.ofSeconds(this.closeIdleConnectionsAfterSeconds));
			    	}
					
				}
			}
		}
    	
    }

}