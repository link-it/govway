/*
 * OpenSPCoop - Customizable API Gateway 
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



package org.openspcoop2.pdd.core;

/**	
 * Contiene la definizione di una eccezione lanciata dalla classe GestoreMessaggi
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */


public class GestoreMessaggiException extends Exception {

	 /**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;
	
	public GestoreMessaggiException() {
	}
	public GestoreMessaggiException(String msg) {
		super(msg);
	}
	 public GestoreMessaggiException(String message, Throwable cause)
		{
			super(message, cause);
			// TODO Auto-generated constructor stub
		}
		public GestoreMessaggiException(Throwable cause)
		{
			super(cause);
			// TODO Auto-generated constructor stub
		}
}
