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

package org.openspcoop2.monitor.sdk.exceptions;

/**
 * AlarmException
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class AlarmNotifyException extends Exception {
    
	public String pluginInvocationError;
	public String sendMailError;
	public String scriptInvocationError;
	
	public AlarmNotifyException(String message, Throwable cause)
	{
		super(message, cause);
	}
	public AlarmNotifyException(Throwable cause)
	{
		super(cause);
	}
	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;
	
	public AlarmNotifyException() {
		super();
    }
	public AlarmNotifyException(String msg) {
        super(msg);
    }
	
	public String getPluginInvocationError() {
		return this.pluginInvocationError;
	}
	public void setPluginInvocationError(String pluginInvocationError) {
		this.pluginInvocationError = pluginInvocationError;
	}
	public String getSendMailError() {
		return this.sendMailError;
	}
	public void setSendMailError(String sendMailError) {
		this.sendMailError = sendMailError;
	}
	public String getScriptInvocationError() {
		return this.scriptInvocationError;
	}
	public void setScriptInvocationError(String scriptInvocationError) {
		this.scriptInvocationError = scriptInvocationError;
	}
}

