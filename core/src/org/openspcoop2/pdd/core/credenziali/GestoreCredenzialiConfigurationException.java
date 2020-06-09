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



package org.openspcoop2.pdd.core.credenziali;

import org.openspcoop2.protocol.sdk.constants.IntegrationFunctionError;

/**	
 * Contiene la definizione di una eccezione lanciata dalle classi del package org.openspcoop.pdd.core.handlers
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */


public class GestoreCredenzialiConfigurationException extends Exception {

	private String identitaHandler = null;
	public String getIdentitaHandler() {
		return this.identitaHandler;
	}
	// Impostato dal gestore degli handler
	protected void setIdentitaHandler(String identitaHandler) {
		this.identitaHandler = identitaHandler;
	}

	private IntegrationFunctionError integrationFunctionError;
	public IntegrationFunctionError getIntegrationFunctionError() {
		return this.integrationFunctionError;
	}
	
	public GestoreCredenzialiConfigurationException(IntegrationFunctionError integrationFunctionError, String message, Throwable cause)
	{
		super(message, cause);
		this.integrationFunctionError = integrationFunctionError;
	}
	public GestoreCredenzialiConfigurationException(IntegrationFunctionError integrationFunctionError, Throwable cause)
	{
		super(cause);
		this.integrationFunctionError = integrationFunctionError;
	}
	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	public GestoreCredenzialiConfigurationException(IntegrationFunctionError integrationFunctionError) {
		super();
		this.integrationFunctionError = integrationFunctionError;
	}
	public GestoreCredenzialiConfigurationException(IntegrationFunctionError integrationFunctionError, String msg) {
		super(msg);
		this.integrationFunctionError = integrationFunctionError;
	}

}
