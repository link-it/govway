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




package org.openspcoop2.core.config.driver;

import java.io.Serializable;

import javax.xml.ws.WebFault;

/**
 * Contiene la definizione di una eccezione lanciata dalle classi del driver che gestisce la configurazione di OpenSPCoop
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

@WebFault(targetNamespace = "http://ws.management.openspcoop.org")
public class DriverConfigurazioneException extends Exception implements Serializable {
	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;
	
	public DriverConfigurazioneException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public DriverConfigurazioneException(Throwable cause) {
		super(cause);
	}

	public DriverConfigurazioneException() {
		super();
    }
	
	public DriverConfigurazioneException(String msg) {
        super(msg);
    }
}

