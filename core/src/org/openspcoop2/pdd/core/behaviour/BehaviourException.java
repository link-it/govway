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



package org.openspcoop2.pdd.core.behaviour;

import org.openspcoop2.pdd.core.EJBUtilsException;

/**	
 * Contiene la definizione di una eccezione lanciata dalla classe EJBUtils
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */


public class BehaviourException extends EJBUtilsException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public BehaviourException() {
		super();
	}

	public BehaviourException(String message, Throwable cause) {
		super(message, cause);
	}

	public BehaviourException(String msg) {
		super(msg);
	}

	public BehaviourException(Throwable cause) {
		super(cause);
	}
}

