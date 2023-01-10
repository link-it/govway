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



package org.openspcoop2.protocol.spcoop.archive;

import java.io.Serializable;


/**
 * Contiene la definizione di una eccezione che indica il mapping non trovato
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */


public class SPCoopConvertToPackageCNIPAException extends Exception implements Serializable {
    
	 public SPCoopConvertToPackageCNIPAException(String message, Throwable cause)
	{
		super(message, cause);
		// TODO Auto-generated constructor stub
	}
	public SPCoopConvertToPackageCNIPAException(Throwable cause)
	{
		super(cause);
		// TODO Auto-generated constructor stub
	}
	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;
	
	public SPCoopConvertToPackageCNIPAException() {
		super();
    }
	public SPCoopConvertToPackageCNIPAException(String msg) {
        super(msg);
    }
}

