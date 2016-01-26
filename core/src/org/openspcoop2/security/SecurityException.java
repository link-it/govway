/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2016 Link.it srl (http://link.it). 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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




package org.openspcoop2.security;

import org.openspcoop2.protocol.sdk.constants.CodiceErroreCooperazione;


/**
 * Contiene la definizione di una eccezione lanciata dalle classi del driver che gestisce la configurazione di OpenSPCoop
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class SecurityException extends Exception {
	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;
	
	private String msgErrore;
	private CodiceErroreCooperazione codiceErrore;
	
	public SecurityException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public SecurityException(Throwable cause) {
		super(cause);
	}

	public SecurityException() {
		super();
    }
	
	public SecurityException(String msg) {
        super(msg);
    }

	public String getMsgErrore() {
		return this.msgErrore;
	}

	public void setMsgErrore(String msgErrore) {
		this.msgErrore = msgErrore;
	}

	public CodiceErroreCooperazione getCodiceErrore() {
		return this.codiceErrore;
	}

	public void setCodiceErrore(CodiceErroreCooperazione codiceErrore) {
		this.codiceErrore = codiceErrore;
	}
}

