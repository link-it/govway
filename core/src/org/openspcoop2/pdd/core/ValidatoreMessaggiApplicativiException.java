/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it). 
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



package org.openspcoop2.pdd.core;

import org.openspcoop2.protocol.sdk.constants.ErroreIntegrazione;

/**	
 * Contiene la definizione di una eccezione lanciata dalla classe ValidatoreMessaggiApplicativi
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */


public class ValidatoreMessaggiApplicativiException extends Exception {
	
	/**
	 * SerialUID
	 */
	private static final long serialVersionUID = 1L;

	private ErroreIntegrazione errore;

	public ValidatoreMessaggiApplicativiException() {
	}
	public ValidatoreMessaggiApplicativiException(String msg) {
		super(msg);
	}
	
	public ErroreIntegrazione getErrore() {
		return this.errore;
	}
	public void setErrore(ErroreIntegrazione errore) {
		this.errore = errore;
	}
}
