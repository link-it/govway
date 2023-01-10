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

package org.openspcoop2.pdd.timers;


import org.openspcoop2.pdd.config.DynamicClusterManager;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.core.controllo_traffico.policy.PolicyVerifier;
import org.openspcoop2.utils.threads.BaseThread;
import org.slf4j.Logger;


/**     
 * TimerClusterDinamicoThread
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class TimerClusterDinamicoThread extends BaseThread{

	private static TimerState STATE = TimerState.OFF; // abilitato in OpenSPCoop2Startup al momento dell'avvio
	
	public static TimerState getSTATE() {
		return STATE;
	}
	public static void setSTATE(TimerState sTATE) {
		STATE = sTATE;
	}

	public static final String ID_MODULO = "TimerClusterDinamico";
		
	/** Logger utilizzato per debug. */
	private Logger log = null;
	
	/** Variables */	
	private OpenSPCoop2Properties properties;
	private DynamicClusterManager manager;
	
	
	/** Costruttore */
	public TimerClusterDinamicoThread(Logger log) throws Exception{
	
		this.log = log;
	
		this.properties = OpenSPCoop2Properties.getInstance();
		this.setTimeout(this.properties.getClusterDinamicoRefreshSecondsInterval());

		this.manager = DynamicClusterManager.getInstance();
	}

	
	@Override
	public void process(){
	
		if(TimerState.ENABLED.equals(STATE)) {
		
	    	try{
	    		this.manager.refresh(this.log);
			}catch(Exception e){
				this.log.error("Errore durante l'aggiornamento del cluster id dinamico: "+e.getMessage(),e);
			}
	    	
	    	try {
	    		if(this.manager.isRateLimitingGestioneCluster()) {
	    			PolicyVerifier.setListClusterNodes(this.manager.getHostnames(this.log));
	    		}
	    	}catch(Exception e){
				this.log.error("Errore durante l'aggiornamento della lista dei nodi per il rate limiting: "+e.getMessage(),e);
			}
		}
		else {
			this.log.info("Timer "+ID_MODULO+" disabilitato");
		}
				
	}
	
	@Override
	public void close(){
		this.log.info("Thread per il refresh del cluster id dinamico terminato");
	}
		
}
