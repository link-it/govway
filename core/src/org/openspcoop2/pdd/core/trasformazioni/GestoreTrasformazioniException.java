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



package org.openspcoop2.pdd.core.trasformazioni;

/**	
 * Contiene la definizione di una eccezione lanciata dalla classe GestoreTrasformazioni
 *
 * @author Andrea Poli <apoli@link.it>
 * @author $Author$
 * @version $Rev$, $Date$
 */


public class GestoreTrasformazioniException extends Exception  {
	
	/**
	 * SerialUID
	 */
	private static final long serialVersionUID = 1L;

	public GestoreTrasformazioniException(String message, Throwable cause)
	{
		super(message, cause);
		// TODO Auto-generated constructor stub
	}
	public GestoreTrasformazioniException(Throwable cause)
	{
		super(cause);
		// TODO Auto-generated constructor stub
	}

	public GestoreTrasformazioniException() {
		super();
    }
	public GestoreTrasformazioniException(String msg) {
        super(msg);
    }
}