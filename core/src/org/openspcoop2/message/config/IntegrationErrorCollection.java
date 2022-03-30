/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it). 
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



package org.openspcoop2.message.config;

import java.util.HashMap;
import java.util.Map;

import org.openspcoop2.message.constants.IntegrationError;
import org.openspcoop2.message.constants.IntegrationErrorMessageType;

/**
 * IntegrationErrorConfiguration
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class IntegrationErrorCollection implements java.io.Serializable {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	
	private Map<String, org.openspcoop2.message.config.IntegrationErrorConfiguration> map = new HashMap<String, org.openspcoop2.message.config.IntegrationErrorConfiguration>();

	private IntegrationErrorMessageType defaultError;
	
	public void addIntegrationError(ConfigurationRFC7807 rfc7807, IntegrationError errorType, 
			IntegrationErrorMessageType integrationErrorMessageType, 
			IntegrationErrorReturnConfiguration errorReturnConfig, 
			boolean useInternalFault){
		if(this.map.containsKey(errorType.name())){
			this.map.remove(errorType.name());
		}
		org.openspcoop2.message.config.IntegrationErrorConfiguration error = 
				new org.openspcoop2.message.config.IntegrationErrorConfiguration(rfc7807, integrationErrorMessageType, errorReturnConfig, useInternalFault);
		this.map.put(errorType.name(), error);
		if(IntegrationError.DEFAULT.equals(errorType)){
			this.defaultError = integrationErrorMessageType;
		}
	}
	
	public org.openspcoop2.message.config.IntegrationErrorConfiguration getIntegrationError(IntegrationError errorType){
		if(this.map.containsKey(errorType.name())){
			org.openspcoop2.message.config.IntegrationErrorConfiguration error = this.map.get(errorType.name());
			error.setDefaultErrorType(this.defaultError);
			return error;
		}
		return null;
	}
}





