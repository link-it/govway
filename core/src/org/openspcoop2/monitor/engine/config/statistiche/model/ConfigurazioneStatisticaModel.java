/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it).
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
package org.openspcoop2.monitor.engine.config.statistiche.model;

import org.openspcoop2.monitor.engine.config.statistiche.ConfigurazioneStatistica;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model ConfigurazioneStatistica 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ConfigurazioneStatisticaModel extends AbstractModel<ConfigurazioneStatistica> {

	public ConfigurazioneStatisticaModel(){
	
		super();
	
		this.ID_CONFIGURAZIONE_STATISTICA = new Field("id-configurazione-statistica",java.lang.String.class,"configurazione-statistica",ConfigurazioneStatistica.class);
		this.ID_CONFIGURAZIONE_SERVIZIO_AZIONE = new org.openspcoop2.monitor.engine.config.statistiche.model.IdConfigurazioneServizioAzioneModel(new Field("id-configurazione-servizio-azione",org.openspcoop2.monitor.engine.config.statistiche.IdConfigurazioneServizioAzione.class,"configurazione-statistica",ConfigurazioneStatistica.class));
		this.ENABLED = new Field("enabled",boolean.class,"configurazione-statistica",ConfigurazioneStatistica.class);
		this.PLUGIN = new org.openspcoop2.monitor.engine.config.statistiche.model.InfoPluginModel(new Field("plugin",org.openspcoop2.monitor.engine.config.statistiche.InfoPlugin.class,"configurazione-statistica",ConfigurazioneStatistica.class));
		this.LABEL = new Field("label",java.lang.String.class,"configurazione-statistica",ConfigurazioneStatistica.class);
	
	}
	
	public ConfigurazioneStatisticaModel(IField father){
	
		super(father);
	
		this.ID_CONFIGURAZIONE_STATISTICA = new ComplexField(father,"id-configurazione-statistica",java.lang.String.class,"configurazione-statistica",ConfigurazioneStatistica.class);
		this.ID_CONFIGURAZIONE_SERVIZIO_AZIONE = new org.openspcoop2.monitor.engine.config.statistiche.model.IdConfigurazioneServizioAzioneModel(new ComplexField(father,"id-configurazione-servizio-azione",org.openspcoop2.monitor.engine.config.statistiche.IdConfigurazioneServizioAzione.class,"configurazione-statistica",ConfigurazioneStatistica.class));
		this.ENABLED = new ComplexField(father,"enabled",boolean.class,"configurazione-statistica",ConfigurazioneStatistica.class);
		this.PLUGIN = new org.openspcoop2.monitor.engine.config.statistiche.model.InfoPluginModel(new ComplexField(father,"plugin",org.openspcoop2.monitor.engine.config.statistiche.InfoPlugin.class,"configurazione-statistica",ConfigurazioneStatistica.class));
		this.LABEL = new ComplexField(father,"label",java.lang.String.class,"configurazione-statistica",ConfigurazioneStatistica.class);
	
	}
	
	

	public IField ID_CONFIGURAZIONE_STATISTICA = null;
	 
	public org.openspcoop2.monitor.engine.config.statistiche.model.IdConfigurazioneServizioAzioneModel ID_CONFIGURAZIONE_SERVIZIO_AZIONE = null;
	 
	public IField ENABLED = null;
	 
	public org.openspcoop2.monitor.engine.config.statistiche.model.InfoPluginModel PLUGIN = null;
	 
	public IField LABEL = null;
	 

	@Override
	public Class<ConfigurazioneStatistica> getModeledClass(){
		return ConfigurazioneStatistica.class;
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