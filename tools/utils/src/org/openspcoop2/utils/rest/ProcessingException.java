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



package org.openspcoop2.utils.rest;


/**	
 * Contiene la definizione di una eccezione lanciata dalla classe ValidatoreMessaggiApplicativi
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author: apoli $
 * @version $Rev: 12564 $, $Date: 2017-01-11 14:31:31 +0100 (Wed, 11 Jan 2017) $
 */


public class ProcessingException extends Exception {
	
	/**
	 * SerialUID
	 */
	private static final long serialVersionUID = 1L;

	public ProcessingException(String message, Throwable cause)
	{
		super(message, cause);
		// TODO Auto-generated constructor stub
	}
	public ProcessingException(Throwable cause)
	{
		super(cause);
		// TODO Auto-generated constructor stub
	}

	public ProcessingException() {
		super();
	}
	public ProcessingException(String msg) {
		super(msg);
	}

}
