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
package org.openspcoop2.monitor.engine.config.base.model;

import org.openspcoop2.monitor.engine.config.base.Plugin;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model Plugin 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class PluginModel extends AbstractModel<Plugin> {

	public PluginModel(){
	
		super();
	
		this.TIPO_PLUGIN = new Field("tipo-plugin",java.lang.String.class,"plugin",Plugin.class);
		this.CLASS_NAME = new Field("class-name",java.lang.String.class,"plugin",Plugin.class);
		this.TIPO = new Field("tipo",java.lang.String.class,"plugin",Plugin.class);
		this.DESCRIZIONE = new Field("descrizione",java.lang.String.class,"plugin",Plugin.class);
		this.LABEL = new Field("label",java.lang.String.class,"plugin",Plugin.class);
		this.STATO = new Field("stato",boolean.class,"plugin",Plugin.class);
		this.PLUGIN_SERVIZIO_COMPATIBILITA = new org.openspcoop2.monitor.engine.config.base.model.PluginServizioCompatibilitaModel(new Field("plugin-servizio-compatibilita",org.openspcoop2.monitor.engine.config.base.PluginServizioCompatibilita.class,"plugin",Plugin.class));
		this.PLUGIN_PROPRIETA_COMPATIBILITA = new org.openspcoop2.monitor.engine.config.base.model.PluginProprietaCompatibilitaModel(new Field("plugin-proprieta-compatibilita",org.openspcoop2.monitor.engine.config.base.PluginProprietaCompatibilita.class,"plugin",Plugin.class));
	
	}
	
	public PluginModel(IField father){
	
		super(father);
	
		this.TIPO_PLUGIN = new ComplexField(father,"tipo-plugin",java.lang.String.class,"plugin",Plugin.class);
		this.CLASS_NAME = new ComplexField(father,"class-name",java.lang.String.class,"plugin",Plugin.class);
		this.TIPO = new ComplexField(father,"tipo",java.lang.String.class,"plugin",Plugin.class);
		this.DESCRIZIONE = new ComplexField(father,"descrizione",java.lang.String.class,"plugin",Plugin.class);
		this.LABEL = new ComplexField(father,"label",java.lang.String.class,"plugin",Plugin.class);
		this.STATO = new ComplexField(father,"stato",boolean.class,"plugin",Plugin.class);
		this.PLUGIN_SERVIZIO_COMPATIBILITA = new org.openspcoop2.monitor.engine.config.base.model.PluginServizioCompatibilitaModel(new ComplexField(father,"plugin-servizio-compatibilita",org.openspcoop2.monitor.engine.config.base.PluginServizioCompatibilita.class,"plugin",Plugin.class));
		this.PLUGIN_PROPRIETA_COMPATIBILITA = new org.openspcoop2.monitor.engine.config.base.model.PluginProprietaCompatibilitaModel(new ComplexField(father,"plugin-proprieta-compatibilita",org.openspcoop2.monitor.engine.config.base.PluginProprietaCompatibilita.class,"plugin",Plugin.class));
	
	}
	
	

	public IField TIPO_PLUGIN = null;
	 
	public IField CLASS_NAME = null;
	 
	public IField TIPO = null;
	 
	public IField DESCRIZIONE = null;
	 
	public IField LABEL = null;
	 
	public IField STATO = null;
	 
	public org.openspcoop2.monitor.engine.config.base.model.PluginServizioCompatibilitaModel PLUGIN_SERVIZIO_COMPATIBILITA = null;
	 
	public org.openspcoop2.monitor.engine.config.base.model.PluginProprietaCompatibilitaModel PLUGIN_PROPRIETA_COMPATIBILITA = null;
	 

	@Override
	public Class<Plugin> getModeledClass(){
		return Plugin.class;
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