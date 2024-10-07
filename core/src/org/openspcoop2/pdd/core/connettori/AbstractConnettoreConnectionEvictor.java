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

package org.openspcoop2.pdd.core.connettori;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.utils.threads.BaseThread;
import org.slf4j.Logger;

/**
 * AbstractConnettoreConnectionEvictor
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public abstract class AbstractConnettoreConnectionEvictor extends BaseThread {

    protected int closeIdleConnectionsAfterSeconds;
    protected boolean debug;
    protected Logger logConnettori;
    private Map<String, Object> mapConnection;
    private String id;

    @SuppressWarnings("unchecked")
	protected AbstractConnettoreConnectionEvictor(boolean debug, int sleepTimeSeconds, int closeIdleConnectionsAfterSeconds,
    		Object mapConnection, String id) {
        this.setTimeout(sleepTimeSeconds);
        this.debug = debug;
        this.closeIdleConnectionsAfterSeconds = closeIdleConnectionsAfterSeconds;
        this.logConnettori = OpenSPCoop2Logger.getLoggerOpenSPCoopConnettori();
        this.mapConnection = (Map<String, Object>) mapConnection;
        this.id = id;
    }

    protected void logDebug(String msg) {
    	if(this.logConnettori!=null) {
    		this.logConnettori.debug(msg);
    	}
    }
    protected void logDebug(String msg, Exception e) {
    	if(this.logConnettori!=null) {
    		this.logConnettori.debug(msg,e);
    	}
    }
    protected void logError(String msg) {
    	if(this.logConnettori!=null) {
    		this.logConnettori.error(msg);
    	}
    }
    protected void logError(String msg, Exception e) {
    	if(this.logConnettori!=null) {
    		this.logConnettori.error(msg,e);
    	}
    }
    
    protected abstract boolean check();
    
    @Override
    protected void process() {
    	
    	boolean check = this.check();
    	
    	if(check) {
	    	List<String> removedKeys = new ArrayList<>();
	    				
	    	if(this.mapConnection!=null) {
	    	
				this.logDebug("["+this.id+"] Client attivi: "+this.mapConnection.size());	
	    		
				for (Map.Entry<String,Object> entry : this.mapConnection.entrySet()) {
					String key = entry.getKey();
					if(key!=null) {
						
						String prefix = "["+this.id+"] Client ("+key+") ";
						
						Object o = this.mapConnection.get(key);
						AbstractConnettoreConnection<?> connection = null;
						if(o instanceof AbstractConnettoreConnection) {
							connection = (AbstractConnettoreConnection<?>) o;
						}
						else {
							this.logError(prefix+"Incompatible type '"+o.getClass().getName()+"'");
							continue;
						}
				    	if(!connection.isExpired()) {
				    		connection.checkExpire();
				    	}
						
				    	// DEBUG
				    	if(this.debug &&
				    		connection!=null) {
				    		String status = connection.getStatus();
				    		if(status!=null && StringUtils.isNotEmpty(status)) {
				    			this.logDebug(prefix+"(expired:"+connection.isExpired()+"): status: "+connection.getStatus());
				    		}
				    		else {
				    			this.logDebug(prefix+"(expired:"+connection.isExpired()+")");
				    		}
				    		
				    	}
					
				    	if(connection.isExpired() &&
				    		connection.isReadyForClose()) {
				    		removedKeys.add(key);
				    	}
				    	
					}
				}
				
				if(!removedKeys.isEmpty()) {
					while(!removedKeys.isEmpty()) {
						String key = removedKeys.remove(0);
						
						String prefix = "["+this.id+"] Client ("+key+") ";
						
						this.logDebug(prefix+"close unused connection ...");
						Object o = this.mapConnection.get(key);
						AbstractConnettoreConnection<?> connection = null;
						if(o instanceof AbstractConnettoreConnection) {
							connection = (AbstractConnettoreConnection<?>) o;
						}
						else {
							this.logDebug(prefix+"close unused connection failed: incompatible type '"+o.getClass().getName()+"'");
							continue;
						}
						try {
							connection.close();
							this.logDebug(prefix+"close unused connection ok");
							this.mapConnection.remove(key);
						}catch(Exception e) {
							this.logDebug(prefix+"close unused connection failed: "+e.getMessage(),e);
						}
					}
				}
			}
    	}
    	
    }

}