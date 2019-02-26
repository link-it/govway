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



package org.openspcoop2.protocol.spcoop.sica;


/**
 * Contiene la definizione di una eccezione lanciata dalla utiliti di trasformazione tra oggetti SICA e oggetti OpenSPCoop
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */


public class SICAToOpenSPCoopUtilitiesException extends Exception {
    
	 public SICAToOpenSPCoopUtilitiesException(String message, Throwable cause)
	{
		super(message, cause);
		// TODO Auto-generated constructor stub
	}
	public SICAToOpenSPCoopUtilitiesException(Throwable cause)
	{
		super(cause);
		// TODO Auto-generated constructor stub
	}
	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;
	
	public SICAToOpenSPCoopUtilitiesException() {
		super();
    }
	public SICAToOpenSPCoopUtilitiesException(String msg) {
        super(msg);
    }
}

