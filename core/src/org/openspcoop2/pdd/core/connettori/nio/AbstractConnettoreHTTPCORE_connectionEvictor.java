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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
public abstract class AbstractConnettoreHTTPCORE_connectionEvictor extends BaseThread {

    protected int closeIdleConnectionsAfterSeconds;
    protected boolean debug;
    protected Logger logConnettori;
    private Map<String, Object> mapConnection;
    private String id;

    @SuppressWarnings("unchecked")
	public AbstractConnettoreHTTPCORE_connectionEvictor(boolean debug, int sleepTimeSeconds, int closeIdleConnectionsAfterSeconds,
    		Object mapConnection, String id) {
        this.setTimeout(sleepTimeSeconds);
        this.debug = debug;
        this.closeIdleConnectionsAfterSeconds = closeIdleConnectionsAfterSeconds;
        this.logConnettori = OpenSPCoop2Logger.getLoggerOpenSPCoopConnettori();
        this.mapConnection = (Map<String, Object>) mapConnection;
        this.id = id;
    }

    protected abstract boolean check();
    
    @Override
    protected void process() {
    	
    	boolean check = this.check();
    	
    	if(check) {
	    	List<String> removedKeys = new ArrayList<String>();
	    				
	    	if(this.mapConnection!=null) {
	    	
				this.logConnettori.debug("["+this.id+"] AsyncClient attivi: "+this.mapConnection.size());	
	    		
				for (String key : this.mapConnection.keySet()) {
					if(key!=null) {
						
						Object o = this.mapConnection.get(key);
						AbstractConnettoreHTTPCORE_connection<?> connection = null;
						if(o instanceof AbstractConnettoreHTTPCORE_connection) {
							connection = (AbstractConnettoreHTTPCORE_connection<?>) o;
						}
						else {
							this.logConnettori.error("["+this.id+"] AsyncClient ("+key+") Incompatible type '"+o.getClass().getName()+"'");
							continue;
						}
				    	if(!connection.isExpired()) {
				    		connection.checkExpire();
				    	}
						
				    	// DEBUG
				    	if(this.debug) {
				    		
				    		if(connection!=null) {
				    			this.logConnettori.debug("["+this.id+"] AsyncClient ("+key+") (expired:"+connection.isExpired()+"): status: "+connection.getStatus());
				    		}
				    		
				    	}
					
				    	if(connection.isExpired()) {
				    		if(connection.isReadyForClose()) {
				    			removedKeys.add(key);
				    		}
				    	}
				    	
					}
				}
				
				if(!removedKeys.isEmpty()) {
					while(!removedKeys.isEmpty()) {
						String key = removedKeys.remove(0);
						this.logConnettori.debug("["+this.id+"] AsyncClient ("+key+") close unused connection ...");
						Object o = this.mapConnection.get(key);
						AbstractConnettoreHTTPCORE_connection<?> connection = null;
						if(o instanceof AbstractConnettoreHTTPCORE_connection) {
							connection = (AbstractConnettoreHTTPCORE_connection<?>) o;
						}
						else {
							this.logConnettori.debug("["+this.id+"] AsyncClient ("+key+") close unused connection failed: incompatible type '"+o.getClass().getName()+"'");
							continue;
						}
						try {
							connection.close();
							this.logConnettori.debug("["+this.id+"] AsyncClient ("+key+") close unused connection ok");
							this.mapConnection.remove(key);
						}catch(Throwable e) {
							this.logConnettori.debug("["+this.id+"] AsyncClient ("+key+") close unused connection failed: "+e.getMessage(),e);
						}
					}
				}
			}
    	}
    	
    }

}