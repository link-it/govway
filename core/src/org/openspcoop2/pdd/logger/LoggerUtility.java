/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it).
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

package org.openspcoop2.pdd.logger;

import org.slf4j.Logger;

/**     
 * LoggerUtility
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class LoggerUtility {

	private Logger log = null;
	private boolean debug = false;
	
	public boolean isDebug() {
		return this.debug;
	}
	public Logger getLog() {
		return this.log;
	}

	public LoggerUtility(Logger log,boolean debug){
		this.log = log;
		this.debug = debug;
	}

	public void error(String msg){
		this.log.error(msg);
	}
	public void error(String msg,Exception e){
		this.log.error(msg,e);
	}
	public void error(String msg,Throwable e){
		this.log.error(msg,e);
	}
	
	public void info(String msg){
		this.log.info(msg);
	}
	public void info(String msg,Exception e){
		this.log.info(msg,e);
	}
	public void info(String msg,Throwable e){
		this.log.info(msg,e);
	}
	
	public void debug(String msg){
		if(this.debug){
			this.log.debug(msg);
		}
	}
	public void debug(String msg,Exception e){
		if(this.debug){
			this.log.debug(msg,e);
		}
	}
	public void debug(String msg,Throwable e){
		if(this.debug){
			this.log.debug(msg,e);
		}
	}
}
