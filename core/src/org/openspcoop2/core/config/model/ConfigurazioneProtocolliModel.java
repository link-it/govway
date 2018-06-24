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
package org.openspcoop2.core.config.model;

import org.openspcoop2.core.config.ConfigurazioneProtocolli;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model ConfigurazioneProtocolli 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ConfigurazioneProtocolliModel extends AbstractModel<ConfigurazioneProtocolli> {

	public ConfigurazioneProtocolliModel(){
	
		super();
	
		this.PROTOCOLLO = new org.openspcoop2.core.config.model.ConfigurazioneProtocolloModel(new Field("protocollo",org.openspcoop2.core.config.ConfigurazioneProtocollo.class,"configurazione-protocolli",ConfigurazioneProtocolli.class));
	
	}
	
	public ConfigurazioneProtocolliModel(IField father){
	
		super(father);
	
		this.PROTOCOLLO = new org.openspcoop2.core.config.model.ConfigurazioneProtocolloModel(new ComplexField(father,"protocollo",org.openspcoop2.core.config.ConfigurazioneProtocollo.class,"configurazione-protocolli",ConfigurazioneProtocolli.class));
	
	}
	
	

	public org.openspcoop2.core.config.model.ConfigurazioneProtocolloModel PROTOCOLLO = null;
	 

	@Override
	public Class<ConfigurazioneProtocolli> getModeledClass(){
		return ConfigurazioneProtocolli.class;
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