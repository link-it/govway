/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it). 
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



package org.openspcoop2.pools.core.commons;

/**
 * Contiene la definizione di una eccezione lanciata da OpenSPCoopConnectionFactory
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */


public class OpenSPCoopFactoryException extends Exception {
    
	 /**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;
	
	public OpenSPCoopFactoryException() {
    }
	public OpenSPCoopFactoryException(String msg) {
        super(msg);
    }
	 public OpenSPCoopFactoryException(String message, Throwable cause)
		{
			super(message, cause);
			// TODO Auto-generated constructor stub
		}
		public OpenSPCoopFactoryException(Throwable cause)
		{
			super(cause);
			// TODO Auto-generated constructor stub
		}
}

