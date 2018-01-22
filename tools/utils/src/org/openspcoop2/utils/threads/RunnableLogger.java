/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2017 Link.it srl (http://link.it). 
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

package org.openspcoop2.utils.threads;

import org.slf4j.Logger;

/**
 * RunnableLogger
 *  
 * @author Poli Andrea (apoli@link.it)
 * @author $Author: apoli $
 * @version $Rev: 13384 $, $Date: 2017-10-26 12:24:53 +0200 (Thu, 26 Oct 2017) $
 */
public class RunnableLogger {

	/** Logger utilizzato per debug. */
	private Logger log = null;
	
	/** Nome */
	private String threadName;
	private String prefix = null;
	
	public RunnableLogger(String threadName, Logger log) {
		this.log = log;
		this.threadName = threadName;
		this.prefix = "["+this.threadName+"] ";
	}
	
	public Logger getLog() {
		return this.log;
	}

	public String getThreadName() {
		return this.threadName;
	}

	public String getPrefix() {
		return this.prefix;
	}
	
	
	public void warn(String message,Throwable t) {
		this.log.warn(this.prefix+message,t);
	}
	public void warn(String message) {
		this.log.warn(this.prefix+message);
	}
	
	public void error(String message,Throwable t) {
		this.log.error(this.prefix+message,t);
	}
	public void error(String message) {
		this.log.error(this.prefix+message);
	}
	
	public void info(String message,Throwable t) {
		this.log.info(this.prefix+message,t);
	}
	public void info(String message) {
		this.log.info(this.prefix+message);
	}
	
	public void debug(String message,Throwable t) {
		this.log.debug(this.prefix+message,t);
	}
	public void debug(String message) {
		this.log.debug(this.prefix+message);
	}
	
	public void trace(String message,Throwable t) {
		this.log.trace(this.prefix+message,t);
	}
	public void trace(String message) {
		this.log.trace(this.prefix+message);
	}
}
