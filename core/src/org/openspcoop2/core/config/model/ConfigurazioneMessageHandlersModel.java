/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it).
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
package org.openspcoop2.core.config.model;

import org.openspcoop2.core.config.ConfigurazioneMessageHandlers;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model ConfigurazioneMessageHandlers 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ConfigurazioneMessageHandlersModel extends AbstractModel<ConfigurazioneMessageHandlers> {

	public ConfigurazioneMessageHandlersModel(){
	
		super();
	
		this.PRE_IN = new org.openspcoop2.core.config.model.ConfigurazioneHandlerModel(new Field("pre-in",org.openspcoop2.core.config.ConfigurazioneHandler.class,"configurazione-message-handlers",ConfigurazioneMessageHandlers.class));
		this.IN = new org.openspcoop2.core.config.model.ConfigurazioneHandlerModel(new Field("in",org.openspcoop2.core.config.ConfigurazioneHandler.class,"configurazione-message-handlers",ConfigurazioneMessageHandlers.class));
		this.IN_PROTOCOL_INFO = new org.openspcoop2.core.config.model.ConfigurazioneHandlerModel(new Field("inProtocolInfo",org.openspcoop2.core.config.ConfigurazioneHandler.class,"configurazione-message-handlers",ConfigurazioneMessageHandlers.class));
		this.OUT = new org.openspcoop2.core.config.model.ConfigurazioneHandlerModel(new Field("out",org.openspcoop2.core.config.ConfigurazioneHandler.class,"configurazione-message-handlers",ConfigurazioneMessageHandlers.class));
		this.POST_OUT = new org.openspcoop2.core.config.model.ConfigurazioneHandlerModel(new Field("postOut",org.openspcoop2.core.config.ConfigurazioneHandler.class,"configurazione-message-handlers",ConfigurazioneMessageHandlers.class));
	
	}
	
	public ConfigurazioneMessageHandlersModel(IField father){
	
		super(father);
	
		this.PRE_IN = new org.openspcoop2.core.config.model.ConfigurazioneHandlerModel(new ComplexField(father,"pre-in",org.openspcoop2.core.config.ConfigurazioneHandler.class,"configurazione-message-handlers",ConfigurazioneMessageHandlers.class));
		this.IN = new org.openspcoop2.core.config.model.ConfigurazioneHandlerModel(new ComplexField(father,"in",org.openspcoop2.core.config.ConfigurazioneHandler.class,"configurazione-message-handlers",ConfigurazioneMessageHandlers.class));
		this.IN_PROTOCOL_INFO = new org.openspcoop2.core.config.model.ConfigurazioneHandlerModel(new ComplexField(father,"inProtocolInfo",org.openspcoop2.core.config.ConfigurazioneHandler.class,"configurazione-message-handlers",ConfigurazioneMessageHandlers.class));
		this.OUT = new org.openspcoop2.core.config.model.ConfigurazioneHandlerModel(new ComplexField(father,"out",org.openspcoop2.core.config.ConfigurazioneHandler.class,"configurazione-message-handlers",ConfigurazioneMessageHandlers.class));
		this.POST_OUT = new org.openspcoop2.core.config.model.ConfigurazioneHandlerModel(new ComplexField(father,"postOut",org.openspcoop2.core.config.ConfigurazioneHandler.class,"configurazione-message-handlers",ConfigurazioneMessageHandlers.class));
	
	}
	
	

	public org.openspcoop2.core.config.model.ConfigurazioneHandlerModel PRE_IN = null;
	 
	public org.openspcoop2.core.config.model.ConfigurazioneHandlerModel IN = null;
	 
	public org.openspcoop2.core.config.model.ConfigurazioneHandlerModel IN_PROTOCOL_INFO = null;
	 
	public org.openspcoop2.core.config.model.ConfigurazioneHandlerModel OUT = null;
	 
	public org.openspcoop2.core.config.model.ConfigurazioneHandlerModel POST_OUT = null;
	 

	@Override
	public Class<ConfigurazioneMessageHandlers> getModeledClass(){
		return ConfigurazioneMessageHandlers.class;
	}
	
	@Override
	public String toString(){
		if(this.getModeledClass()!=null){
			return this.getModeledClass().getName();
		}else{
			return "N.D.";
		}
	}

}