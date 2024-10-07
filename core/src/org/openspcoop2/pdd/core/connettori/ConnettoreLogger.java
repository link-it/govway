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

import org.slf4j.Logger;
import org.openspcoop2.core.constants.Costanti;
import org.openspcoop2.pdd.core.PdDContext;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;

/**
 * ConnettoreLogger
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ConnettoreLogger {

	private boolean debug;
	private Logger loggerConnettore;
	private Logger loggerCore;
	private String idMessaggio;
	private PdDContext pddContext;
	private String idTransazione;
	
	public ConnettoreLogger(boolean debug,String idMessaggio,PdDContext pddContext){
		this.debug = debug;
		this.idMessaggio = idMessaggio;
		this.pddContext = pddContext;
		
		Object oIdTransazione = this.pddContext.getObject(Costanti.ID_TRANSAZIONE);
		if(oIdTransazione instanceof String id){
			this.idTransazione = id;
		}
		
		this.loggerConnettore = OpenSPCoop2Logger.getLoggerOpenSPCoopConnettori();
		
		this.loggerCore = OpenSPCoop2Logger.getLoggerOpenSPCoopCore();
	}
	
	public String getIdTransazione() {
		return this.idTransazione;
	}
	
	public Logger getLogger(){
		if(this.debug){
			return this.loggerConnettore;
		}
		else{
			return this.loggerCore;
		}
	}
	
	public String buildMsg(String msg){
		StringBuilder bf = new StringBuilder();
		if(this.idTransazione!=null){
			bf.append("id:").append(this.idTransazione);
		}
		if(this.idMessaggio!=null){
			if(bf.length()>0){
				bf.append(" ");
			}
			bf.append("(id-busta:").append(this.idMessaggio);
		}
		if(bf.length()>0){
			return "<"+bf.toString()+"> "+msg;
		}
		else{
			return  msg;
		}
	}
	
	public void error(String msg){
		String errorMsg = this.buildMsg(msg);
		this.loggerCore.error(errorMsg);
		if(this.debug){
			this.loggerConnettore.error(errorMsg);
		}
	}
	
	public void error(String msg, Throwable t){
		String errorMsg = this.buildMsg(msg);
		this.loggerCore.error(errorMsg,t);
		if(this.debug){
			this.loggerConnettore.error(errorMsg,t);
		}
	}
	
	public void warn(String msg){
		String warnMsg = this.buildMsg(msg);
		this.loggerCore.warn(warnMsg);
		if(this.debug){
			this.loggerConnettore.warn(warnMsg);
		}
	}
	public void warn(String msg, Throwable t){
		String warnMsg = this.buildMsg(msg);
		this.loggerCore.warn(warnMsg,t);
		if(this.debug){
			this.loggerConnettore.warn(warnMsg,t);
		}
	}

	public void info(String msg, boolean logInCore){
		String infoMsg = this.buildMsg(msg);
		if(logInCore){
			this.loggerCore.info(infoMsg);
		}
		if(this.debug){
			this.loggerConnettore.info(infoMsg);
		}
	}
	
	public void debug(String msg){
		if(this.debug){
			String debugMsg = this.buildMsg(msg);
			this.loggerConnettore.debug(debugMsg);
		}
	}
	public void debug(String msg, Throwable t){
		if(this.debug){
			String debugMsg = this.buildMsg(msg);
			this.loggerConnettore.debug(debugMsg, t);
		}
	}

	
	
}
