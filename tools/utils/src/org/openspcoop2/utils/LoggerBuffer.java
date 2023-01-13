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
package org.openspcoop2.utils;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.slf4j.Logger;

/**
 * LoggerBuffer
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class LoggerBuffer {

	private StringBuilder sbError;
	private StringBuilder sbDebug;
	private boolean logErrorInDebug = true;
	private boolean addSeverityPrefix = false;
	private Logger logDebug;
	private Logger logError;
	
	public Logger getLogDebug() {
		return this.logDebug;
	}
	public void setLogDebug(Logger logDebug) {
		this.logDebug = logDebug;
	}
	public Logger getLogError() {
		return this.logError;
	}
	public void setLogError(Logger logError) {
		this.logError = logError;
	}
	public StringBuilder getSbError() {
		return this.sbError;
	}
	public void setSbError(StringBuilder sbError) {
		this.sbError = sbError;
	}
	public StringBuilder getSbDebug() {
		return this.sbDebug;
	}
	public void setSbDebug(StringBuilder sbDebug) {
		this.sbDebug = sbDebug;
	}
	public boolean isLogErrorInDebug() {
		return this.logErrorInDebug;
	}
	public void setLogErrorInDebug(boolean logErrorInDebug) {
		this.logErrorInDebug = logErrorInDebug;
	}
	public boolean isAddSeverityPrefix() {
		return this.addSeverityPrefix;
	}
	public void setAddSeverityPrefix(boolean addSeverityPrefix) {
		this.addSeverityPrefix = addSeverityPrefix;
	}
	public void addSeverityPrefix(boolean addSeverityPrefix) {
		this.addSeverityPrefix = addSeverityPrefix;
	}
	
	public void debug(String msgError) {
		this.debug(msgError, null);
	}
	public void debug(String msgError, Throwable e) {
		
		if(this.logDebug!=null) {
			this.logDebug.debug(msgError,e);
		}
		
		if(this.sbDebug!=null) {
			if(this.sbDebug.length()>0) {
				this.sbDebug.append("\n");
			}
			if(this.addSeverityPrefix) {
				this.sbDebug.append("DEBUG ");
			}
			this.sbDebug.append(msgError);
			try( ByteArrayOutputStream out = new ByteArrayOutputStream(); PrintStream ps = new PrintStream(out);){
				e.printStackTrace(ps);
				ps.flush();
				out.flush();
				if(this.sbDebug.length()>0) {
					this.sbDebug.append("\n");
				}
				if(this.addSeverityPrefix) {
					this.sbDebug.append("DEBUG ");
				}
				this.sbDebug.append(out.toString());
			}catch(Throwable t) {
				// ignore
			}
		}
	}
	
	public void error(String msgError) {
		this.error(msgError, null);
	}
	public void error(String msgError, Throwable e) {
		
		if(this.logError!=null) {
			this.logError.error(msgError,e);
		}
		if(this.logDebug!=null && this.logErrorInDebug) {
			if(this.logError==null || this.logError.getName()==null || !this.logError.getName().equals(this.logDebug.getName()) ) {
				this.logDebug.error(msgError,e);
			}
		}
		
		if(this.sbError!=null || this.sbDebug!=null) {
			if(this.sbError!=null) {
				if(this.sbError.length()>0) {
					this.sbError.append("\n");
				}
				if(this.addSeverityPrefix) {
					this.sbError.append("ERROR ");
				}
				this.sbError.append(msgError);
			}
			if(this.sbDebug!=null && this.logErrorInDebug) {
				if(this.sbDebug.length()>0) {
					this.sbDebug.append("\n");
				}
				if(this.addSeverityPrefix) {
					this.sbDebug.append("ERROR ");
				}
				this.sbDebug.append(msgError);
			}
			try( ByteArrayOutputStream out = new ByteArrayOutputStream(); PrintStream ps = new PrintStream(out);){
				e.printStackTrace(ps);
				ps.flush();
				out.flush();
				if(this.sbError!=null) {
					if(this.sbError.length()>0) {
						this.sbError.append("\n");
					}
					if(this.addSeverityPrefix) {
						this.sbError.append("ERROR ");
					}
					this.sbError.append(out.toString());
				}
				if(this.sbDebug!=null && this.logErrorInDebug) {
					if(this.sbDebug.length()>0) {
						this.sbDebug.append("\n");
					}
					if(this.addSeverityPrefix) {
						this.sbDebug.append("ERROR ");
					}
					this.sbDebug.append(out.toString());
				}
			}catch(Throwable t) {
				// ignore
			}
		}
	}
}
