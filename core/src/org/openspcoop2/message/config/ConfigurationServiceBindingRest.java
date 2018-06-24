/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it). 
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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.message.exception.MessageException;

/**
 * ConfigurationServiceBindingRest
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ConfigurationServiceBindingRest extends AbstractConfigurationServiceBinding implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private RestBinding binding;
	
	public ConfigurationServiceBindingRest(boolean enabled,RestBinding binding,
			IntegrationErrorCollection internalIntegrationErrorConfiguration,
			IntegrationErrorCollection externalIntegrationErrorConfiguration) throws MessageException{
		super(ServiceBinding.REST, enabled,internalIntegrationErrorConfiguration,externalIntegrationErrorConfiguration);
		if(this.enabled){
			if(binding==null){
				throw new MessageException("RestBinding not defined");
			}
			this.binding = binding;
		}
	}
	
	@Override
	public void init(){
		if(this.enabled){
			this.request = new RestMediaTypeCollection(this.binding);
			this.response = new RestMediaTypeCollection(this.binding);
		}
	}
	
	public RestBinding getBinding() {
		return this.binding;
	}
	
	@Override
	public List<MessageType> getMessageTypeSupported(){
		 List<MessageType> list = new ArrayList<MessageType>();
		 if(this.binding!=null) {
			 if(this.binding.isBinding_xml()) {
				 list.add(MessageType.XML);
			 }
			 if(this.binding.isBinding_json()) {
				 list.add(MessageType.JSON);
			 }
			 if(this.binding.isBinding_binary()) {
				 list.add(MessageType.BINARY);
			 }
			 if(this.binding.isBinding_mimeMultipart()) {
				 list.add(MessageType.MIME_MULTIPART);
			 }
		 }
		 return list;
	}
	
}
