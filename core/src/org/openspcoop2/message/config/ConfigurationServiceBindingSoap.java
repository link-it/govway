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

package org.openspcoop2.message.config;

import java.io.Serializable;

import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.message.exception.MessageException;

/**
 * ConfigurationServiceBindingSoap
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author: apoli $
 * @version $Rev: 12237 $, $Date: 2016-10-04 11:41:45 +0200 (Tue, 04 Oct 2016) $
 */
public class ConfigurationServiceBindingSoap extends AbstractConfigurationServiceBinding implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private SoapBinding binding;
	
	public ConfigurationServiceBindingSoap(boolean enabled,SoapBinding binding,
			IntegrationErrorCollection internalIntegrationErrorConfiguration,
			IntegrationErrorCollection externalIntegrationErrorConfiguration) throws MessageException{
		super(ServiceBinding.SOAP, enabled, internalIntegrationErrorConfiguration, externalIntegrationErrorConfiguration);
		if(this.enabled){
			if(binding==null){
				throw new MessageException("SoapBinding not defined");
			}
			this.binding = binding;
		}
	}
	
	@Override
	protected void initMediaTypeCollection(){
		this.request = new SoapMediaTypeCollection(this.binding);
		this.response = new SoapMediaTypeCollection(this.binding);
	}
	
	public SoapBinding getBinding() {
		return this.binding;
	}
	
}
