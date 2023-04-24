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


package org.openspcoop2.monitor.sdk.exceptions;

import java.util.HashMap;
import java.util.Map;



/**
 * ParameterException
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ParameterException extends Exception {
    
	private Map<String, String> errors;
	
	public void addFieldErrorMessage(String paramId, String errorMessage){
		if(this.errors==null)
			this.errors = new HashMap<>();
		
		this.errors.put(paramId, errorMessage);
	}
	
	public ParameterException(String message, Throwable cause)
	{
		super(message, cause);
	}
	public ParameterException(Throwable cause)
	{
		super(cause);
	}
	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;
	
	public ParameterException() {
		super();
    }
	public ParameterException(String msg) {
        super(msg);
    }
	
	public Map<String, String> getErrors() {
		return this.errors;
	}
}

