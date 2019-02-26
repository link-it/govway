/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it).
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
package org.openspcoop2.monitor.engine.config.ricerche.model;

import org.openspcoop2.monitor.engine.config.ricerche.ConfigurazioneRicerca;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model ConfigurazioneRicerca 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ConfigurazioneRicercaModel extends AbstractModel<ConfigurazioneRicerca> {

	public ConfigurazioneRicercaModel(){
	
		super();
	
		this.ID_CONFIGURAZIONE_RICERCA = new Field("id-configurazione-ricerca",java.lang.String.class,"configurazione-ricerca",ConfigurazioneRicerca.class);
		this.ID_CONFIGURAZIONE_SERVIZIO_AZIONE = new org.openspcoop2.monitor.engine.config.ricerche.model.IdConfigurazioneServizioAzioneModel(new Field("id-configurazione-servizio-azione",org.openspcoop2.monitor.engine.config.ricerche.IdConfigurazioneServizioAzione.class,"configurazione-ricerca",ConfigurazioneRicerca.class));
		this.ENABLED = new Field("enabled",boolean.class,"configurazione-ricerca",ConfigurazioneRicerca.class);
		this.PLUGIN = new org.openspcoop2.monitor.engine.config.ricerche.model.InfoPluginModel(new Field("plugin",org.openspcoop2.monitor.engine.config.ricerche.InfoPlugin.class,"configurazione-ricerca",ConfigurazioneRicerca.class));
		this.LABEL = new Field("label",java.lang.String.class,"configurazione-ricerca",ConfigurazioneRicerca.class);
	
	}
	
	public ConfigurazioneRicercaModel(IField father){
	
		super(father);
	
		this.ID_CONFIGURAZIONE_RICERCA = new ComplexField(father,"id-configurazione-ricerca",java.lang.String.class,"configurazione-ricerca",ConfigurazioneRicerca.class);
		this.ID_CONFIGURAZIONE_SERVIZIO_AZIONE = new org.openspcoop2.monitor.engine.config.ricerche.model.IdConfigurazioneServizioAzioneModel(new ComplexField(father,"id-configurazione-servizio-azione",org.openspcoop2.monitor.engine.config.ricerche.IdConfigurazioneServizioAzione.class,"configurazione-ricerca",ConfigurazioneRicerca.class));
		this.ENABLED = new ComplexField(father,"enabled",boolean.class,"configurazione-ricerca",ConfigurazioneRicerca.class);
		this.PLUGIN = new org.openspcoop2.monitor.engine.config.ricerche.model.InfoPluginModel(new ComplexField(father,"plugin",org.openspcoop2.monitor.engine.config.ricerche.InfoPlugin.class,"configurazione-ricerca",ConfigurazioneRicerca.class));
		this.LABEL = new ComplexField(father,"label",java.lang.String.class,"configurazione-ricerca",ConfigurazioneRicerca.class);
	
	}
	
	

	public IField ID_CONFIGURAZIONE_RICERCA = null;
	 
	public org.openspcoop2.monitor.engine.config.ricerche.model.IdConfigurazioneServizioAzioneModel ID_CONFIGURAZIONE_SERVIZIO_AZIONE = null;
	 
	public IField ENABLED = null;
	 
	public org.openspcoop2.monitor.engine.config.ricerche.model.InfoPluginModel PLUGIN = null;
	 
	public IField LABEL = null;
	 

	@Override
	public Class<ConfigurazioneRicerca> getModeledClass(){
		return ConfigurazioneRicerca.class;
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