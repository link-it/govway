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
package org.openspcoop2.monitor.engine.config.transazioni.model;

import org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneTransazionePlugin;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model ConfigurazioneTransazionePlugin 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ConfigurazioneTransazionePluginModel extends AbstractModel<ConfigurazioneTransazionePlugin> {

	public ConfigurazioneTransazionePluginModel(){
	
		super();
	
		this.ID_CONFIGURAZIONE_TRANSAZIONE_PLUGIN = new Field("id-configurazione-transazione-plugin",java.lang.String.class,"configurazione-transazione-plugin",ConfigurazioneTransazionePlugin.class);
		this.ENABLED = new Field("enabled",boolean.class,"configurazione-transazione-plugin",ConfigurazioneTransazionePlugin.class);
		this.PLUGIN = new org.openspcoop2.monitor.engine.config.transazioni.model.InfoPluginModel(new Field("plugin",org.openspcoop2.monitor.engine.config.transazioni.InfoPlugin.class,"configurazione-transazione-plugin",ConfigurazioneTransazionePlugin.class));
	
	}
	
	public ConfigurazioneTransazionePluginModel(IField father){
	
		super(father);
	
		this.ID_CONFIGURAZIONE_TRANSAZIONE_PLUGIN = new ComplexField(father,"id-configurazione-transazione-plugin",java.lang.String.class,"configurazione-transazione-plugin",ConfigurazioneTransazionePlugin.class);
		this.ENABLED = new ComplexField(father,"enabled",boolean.class,"configurazione-transazione-plugin",ConfigurazioneTransazionePlugin.class);
		this.PLUGIN = new org.openspcoop2.monitor.engine.config.transazioni.model.InfoPluginModel(new ComplexField(father,"plugin",org.openspcoop2.monitor.engine.config.transazioni.InfoPlugin.class,"configurazione-transazione-plugin",ConfigurazioneTransazionePlugin.class));
	
	}
	
	

	public IField ID_CONFIGURAZIONE_TRANSAZIONE_PLUGIN = null;
	 
	public IField ENABLED = null;
	 
	public org.openspcoop2.monitor.engine.config.transazioni.model.InfoPluginModel PLUGIN = null;
	 

	@Override
	public Class<ConfigurazioneTransazionePlugin> getModeledClass(){
		return ConfigurazioneTransazionePlugin.class;
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