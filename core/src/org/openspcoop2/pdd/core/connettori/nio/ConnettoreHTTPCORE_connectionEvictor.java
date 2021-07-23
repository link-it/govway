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

import java.util.concurrent.TimeUnit;

import org.apache.http.impl.nio.conn.PoolingNHttpClientConnectionManager;
import org.apache.http.impl.nio.reactor.DefaultConnectingIOReactor;
import org.apache.http.impl.nio.reactor.ExceptionEvent;
import org.apache.http.nio.reactor.ConnectingIOReactor;
import org.openspcoop2.utils.date.DateUtils;

/**
 * ConnettoreHTTPCORE_connectionEvictor
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ConnettoreHTTPCORE_connectionEvictor extends AbstractConnettoreHTTPCORE_connectionEvictor {

    public ConnettoreHTTPCORE_connectionEvictor(boolean debug, int sleepTimeSeconds, int closeIdleConnectionsAfterSeconds) {
        super(debug, sleepTimeSeconds, closeIdleConnectionsAfterSeconds,
        		ConnettoreHTTPCORE_connectionManager.mapConnection, "HTTPCore");
    }

    @Override
    protected boolean check() {
    	
    	if(ConnettoreHTTPCORE_connectionManager.mapIoReactor!=null && !ConnettoreHTTPCORE_connectionManager.mapIoReactor.isEmpty()) {
    		
    		this.logConnettori.debug("[HTTPCore] IOReactor attivi: "+ConnettoreHTTPCORE_connectionManager.mapIoReactor.size());		
    		
			for (String key : ConnettoreHTTPCORE_connectionManager.mapIoReactor.keySet()) {
				if(key!=null) {
					
					ConnectingIOReactor ioReactor = ConnettoreHTTPCORE_connectionManager.mapIoReactor.get(key);
					PoolingNHttpClientConnectionManager connMgr = ConnettoreHTTPCORE_connectionManager.mapPoolingConnectionManager.get(key);
					ConnettoreHTTPCORE_connection connection = ConnettoreHTTPCORE_connectionManager.mapConnection.get(key);
					
			    	// DEBUG
			    	if(this.debug) {
			    		
			    		if(ioReactor!=null && ioReactor instanceof DefaultConnectingIOReactor) {
			    			DefaultConnectingIOReactor defaultConnectingIOReactor = (DefaultConnectingIOReactor) ioReactor;
			    			this.logConnettori.debug("[HTTPCore] ("+key+") IOReactor status: "+defaultConnectingIOReactor.getStatus());
			    			if(defaultConnectingIOReactor.getAuditLog()!=null && defaultConnectingIOReactor.getAuditLog().size()>0) {
			    				for (int i = 0; i < defaultConnectingIOReactor.getAuditLog().size(); i++) {
			    					ExceptionEvent event = defaultConnectingIOReactor.getAuditLog().get(i);
			    					String sDate = "";
			    					if(event.getTimestamp()!=null) {
			    						sDate = "("+DateUtils.getSimpleDateFormatMs().format(event.getTimestamp())+")";
			    					}
			    					this.logConnettori.error("[HTTPCore] ("+key+") IOReactor exceptionEvent "+sDate+": "+event.getCause().getMessage(),event.getCause());
								}
			    			}
			    		}
			    		
			    		if(connection!=null) {
			    			this.logConnettori.debug("[HTTPCore] ("+key+"): isRunning: "+connection.getHttpclient().isRunning());
			    		}
			    		
			    	}
			    	
			    	if(connMgr!=null) {
				    	// Close expired connections
				    	connMgr.closeExpiredConnections();
				    	// Optionally, close connections
				    	// that have been idle longer than 30 sec
				    	connMgr.closeIdleConnections(this.closeIdleConnectionsAfterSeconds, TimeUnit.SECONDS);
			    	}
					
				}
			}
		
			// fix
			//return true;
			return false; // non dove controllare le connessioni con httpcore 4.x poiche' non e' possibile associare il connection manager a piu' istanze client
		}
    	
    	return false;
    	
    }

}