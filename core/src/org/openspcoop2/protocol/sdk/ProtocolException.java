/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it). 
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

package org.openspcoop2.protocol.sdk;

/**
 * ProtocolException
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ProtocolException extends Exception {

	private boolean interoperabilityError = false; // indicazione che si tratta di un errore di validazione del protocollo
	public boolean isInteroperabilityError() {
		return this.interoperabilityError;
	}
	public void setInteroperabilityError(boolean interoperabilityError) {
		this.interoperabilityError = interoperabilityError;
	}

	private boolean forceTrace; // indicazione se deve essere forzato il tracciamento
	public boolean isForceTrace() {
		return this.forceTrace;
	}
	public void setForceTrace(boolean forceTrace) {
		this.forceTrace = forceTrace;
	}
	
	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;
	
	public ProtocolException() {
	}
	public ProtocolException(String msg) {
		super(msg);
	}
	public ProtocolException(String message, Throwable cause)
	{
		super(message, cause);
		// TODO Auto-generated constructor stub
	}
	public ProtocolException(Throwable cause)
	{
		super(cause);
		// TODO Auto-generated constructor stub
	}
}
