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

import org.openspcoop2.core.config.ConfigurazioneGeneraleHandler;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model ConfigurazioneGeneraleHandler 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ConfigurazioneGeneraleHandlerModel extends AbstractModel<ConfigurazioneGeneraleHandler> {

	public ConfigurazioneGeneraleHandlerModel(){
	
		super();
	
		this.REQUEST = new org.openspcoop2.core.config.model.ConfigurazioneMessageHandlersModel(new Field("request",org.openspcoop2.core.config.ConfigurazioneMessageHandlers.class,"configurazione-generale-handler",ConfigurazioneGeneraleHandler.class));
		this.RESPONSE = new org.openspcoop2.core.config.model.ConfigurazioneMessageHandlersModel(new Field("response",org.openspcoop2.core.config.ConfigurazioneMessageHandlers.class,"configurazione-generale-handler",ConfigurazioneGeneraleHandler.class));
		this.SERVICE = new org.openspcoop2.core.config.model.ConfigurazioneServiceHandlersModel(new Field("service",org.openspcoop2.core.config.ConfigurazioneServiceHandlers.class,"configurazione-generale-handler",ConfigurazioneGeneraleHandler.class));
	
	}
	
	public ConfigurazioneGeneraleHandlerModel(IField father){
	
		super(father);
	
		this.REQUEST = new org.openspcoop2.core.config.model.ConfigurazioneMessageHandlersModel(new ComplexField(father,"request",org.openspcoop2.core.config.ConfigurazioneMessageHandlers.class,"configurazione-generale-handler",ConfigurazioneGeneraleHandler.class));
		this.RESPONSE = new org.openspcoop2.core.config.model.ConfigurazioneMessageHandlersModel(new ComplexField(father,"response",org.openspcoop2.core.config.ConfigurazioneMessageHandlers.class,"configurazione-generale-handler",ConfigurazioneGeneraleHandler.class));
		this.SERVICE = new org.openspcoop2.core.config.model.ConfigurazioneServiceHandlersModel(new ComplexField(father,"service",org.openspcoop2.core.config.ConfigurazioneServiceHandlers.class,"configurazione-generale-handler",ConfigurazioneGeneraleHandler.class));
	
	}
	
	

	public org.openspcoop2.core.config.model.ConfigurazioneMessageHandlersModel REQUEST = null;
	 
	public org.openspcoop2.core.config.model.ConfigurazioneMessageHandlersModel RESPONSE = null;
	 
	public org.openspcoop2.core.config.model.ConfigurazioneServiceHandlersModel SERVICE = null;
	 

	@Override
	public Class<ConfigurazioneGeneraleHandler> getModeledClass(){
		return ConfigurazioneGeneraleHandler.class;
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