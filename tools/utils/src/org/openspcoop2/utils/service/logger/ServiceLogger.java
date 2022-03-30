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

package org.openspcoop2.utils.service.logger;

import org.openspcoop2.utils.service.context.MD5Constants;
import org.slf4j.Logger;
import org.slf4j.MDC;

/**
 * ServiceLogger
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ServiceLogger {

	private String idOperazione;
	private String methodName;
	private String prefix;
	private Logger log;

	
	public ServiceLogger(String idOperazione, String methodName, String className, Logger log, boolean usePrefix) {
		this.log = log;
		this.idOperazione = idOperazione;
		this.methodName = methodName;
		String classe = className;
		if(className.contains(".")) {
			String [] tmp = className.split("\\.");
			classe = tmp[tmp.length-1];
		}
		if(usePrefix) {
			this.prefix = String.format("<%s> (%s.%s) ", this.idOperazione,classe,this.methodName);
		}
		else {
			this.prefix = "";
		}
		MDC.put(MD5Constants.SERVICE_ID, classe);
		MDC.put(MD5Constants.OPERATION_ID, this.methodName);
	}
	
	private String appendPrefix(String message) {
		StringBuilder bf = new StringBuilder(this.prefix);
		bf.append(message);
		return bf.toString();
	}
	
	public void error(String message, Object ...params) {
		this._error(message, null, params);
	}
	public void error(String message, Throwable t) {
		this._error(message, t, null);
	}
	public void error(String message, Throwable t, Object ...params) {
		this._error(message, t, params);
	}
	private void _error(String message, Throwable t, Object [] params) {
		String msgWithPrefix = this.appendPrefix(message);
		if(params!=null && params.length>0) {
			String msg = String.format(msgWithPrefix,params);
			if(t!=null) {
				this.log.error(msg,t);
			}
			else {
				this.log.error(msg);
			}
		}
		else {
			if(t!=null) {
				this.log.error(msgWithPrefix,t);
			}
			else {
				this.log.error(msgWithPrefix);
			}
		}
	}
	
	public void error_except404(String message, Throwable t) {
		this._error_except404(message, t, null);
	}
	public void error_except404(String message, Throwable t, Object ...params) {
		this._error_except404(message, t, params);
	}
	private void _error_except404(String message, Throwable t, Object [] params) {
		
		boolean error = true;
		if(t!=null && t instanceof javax.ws.rs.WebApplicationException) {
			javax.ws.rs.WebApplicationException we = (javax.ws.rs.WebApplicationException) t;
			if(we.getResponse()!=null && we.getResponse().getStatus()==404) {
				error = false;
			}
		}
		
		String msgWithPrefix = this.appendPrefix(message);
		if(params!=null && params.length>0) {
			String msg = String.format(msgWithPrefix,params);
			if(t!=null) {
				if(error) this.log.error(msg,t); else this.log.debug(msg,t);
			}
			else {
				if(error) this.log.error(msg); else this.log.debug(msg);
			}
		}
		else {
			if(t!=null) {
				if(error) this.log.error(msgWithPrefix,t); else this.log.debug(msgWithPrefix,t);
			}
			else {
				if(error) this.log.error(msgWithPrefix); else this.log.debug(msgWithPrefix,t);
			}
		}
	}
	
	
	public void warn(String message, Object ...params) {
		this._warn(message, null, params);
	}
	public void warn(String message, Throwable t) {
		this._warn(message, t, null);
	}
	public void warn(String message, Throwable t, Object ...params) {
		this._warn(message, t, params);
	}
	private void _warn(String message, Throwable t, Object [] params) {
		String msgWithPrefix = this.appendPrefix(message);
		if(params!=null && params.length>0) {
			String msg = String.format(msgWithPrefix,params);
			if(t!=null) {
				this.log.warn(msg,t);
			}
			else {
				this.log.warn(msg);
			}
		}
		else {
			if(t!=null) {
				this.log.warn(msgWithPrefix,t);
			}
			else {
				this.log.warn(msgWithPrefix);
			}
		}
	}
	
	
	public void info(String message, Object ...params) {
		this._info(message, null, params);
	}
	public void info(String message, Throwable t) {
		this._info(message, t, null);
	}
	public void info(String message, Throwable t, Object ...params) {
		this._info(message, t, params);
	}
	private void _info(String message, Throwable t, Object [] params) {
		String msgWithPrefix = this.appendPrefix(message);
		if(params!=null && params.length>0) {
			String msg = String.format(msgWithPrefix,params);
			if(t!=null) {
				this.log.info(msg,t);
			}
			else {
				this.log.info(msg);
			}
		}
		else {
			if(t!=null) {
				this.log.info(msgWithPrefix,t);
			}
			else {
				this.log.info(msgWithPrefix);
			}
		}
	}
	

	public void debug(String message, Object ...params) {
		this._debug(message, null, params);
	}
	public void debug(String message, Throwable t) {
		this._debug(message, t, null);
	}
	public void debug(String message, Throwable t, Object ...params) {
		this._debug(message, t, params);
	}
	private void _debug(String message, Throwable t, Object [] params) {
		String msgWithPrefix = this.appendPrefix(message);
		if(params!=null && params.length>0) {
			String msg = String.format(msgWithPrefix,params);
			if(t!=null) {
				this.log.debug(msg,t);
			}
			else {
				this.log.debug(msg);
			}
		}
		else {
			if(t!=null) {
				this.log.debug(msgWithPrefix,t);
			}
			else {
				this.log.debug(msgWithPrefix);
			}
		}
	}
	

	public void trace(String message, Object ...params) {
		this._trace(message, null, params);
	}
	public void trace(String message, Throwable t) {
		this._trace(message, t, null);
	}
	public void trace(String message, Throwable t, Object ...params) {
		this._trace(message, t, params);
	}
	private void _trace(String message, Throwable t, Object [] params) {
		String msgWithPrefix = this.appendPrefix(message);
		if(params!=null && params.length>0) {
			String msg = String.format(msgWithPrefix,params);
			if(t!=null) {
				this.log.trace(msg,t);
			}
			else {
				this.log.trace(msg);
			}
		}
		else {
			if(t!=null) {
				this.log.trace(msgWithPrefix,t);
			}
			else {
				this.log.trace(msgWithPrefix);
			}
		}
	}
}
