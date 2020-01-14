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
package org.openspcoop2.monitor.engine.config.transazioni.model;

import org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneTransazione;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model ConfigurazioneTransazione 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ConfigurazioneTransazioneModel extends AbstractModel<ConfigurazioneTransazione> {

	public ConfigurazioneTransazioneModel(){
	
		super();
	
		this.ID_CONFIGURAZIONE_SERVIZIO_AZIONE = new org.openspcoop2.monitor.engine.config.transazioni.model.IdConfigurazioneServizioAzioneModel(new Field("id-configurazione-servizio-azione",org.openspcoop2.monitor.engine.config.transazioni.IdConfigurazioneServizioAzione.class,"configurazione-transazione",ConfigurazioneTransazione.class));
		this.ENABLED = new Field("enabled",boolean.class,"configurazione-transazione",ConfigurazioneTransazione.class);
		this.CONFIGURAZIONE_TRANSAZIONE_PLUGIN = new org.openspcoop2.monitor.engine.config.transazioni.model.ConfigurazioneTransazionePluginModel(new Field("configurazione-transazione-plugin",org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneTransazionePlugin.class,"configurazione-transazione",ConfigurazioneTransazione.class));
		this.CONFIGURAZIONE_TRANSAZIONE_STATO = new org.openspcoop2.monitor.engine.config.transazioni.model.ConfigurazioneTransazioneStatoModel(new Field("configurazione-transazione-stato",org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneTransazioneStato.class,"configurazione-transazione",ConfigurazioneTransazione.class));
		this.CONFIGURAZIONE_TRANSAZIONE_RISORSA_CONTENUTO = new org.openspcoop2.monitor.engine.config.transazioni.model.ConfigurazioneTransazioneRisorsaContenutoModel(new Field("configurazione-transazione-risorsa-contenuto",org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneTransazioneRisorsaContenuto.class,"configurazione-transazione",ConfigurazioneTransazione.class));
	
	}
	
	public ConfigurazioneTransazioneModel(IField father){
	
		super(father);
	
		this.ID_CONFIGURAZIONE_SERVIZIO_AZIONE = new org.openspcoop2.monitor.engine.config.transazioni.model.IdConfigurazioneServizioAzioneModel(new ComplexField(father,"id-configurazione-servizio-azione",org.openspcoop2.monitor.engine.config.transazioni.IdConfigurazioneServizioAzione.class,"configurazione-transazione",ConfigurazioneTransazione.class));
		this.ENABLED = new ComplexField(father,"enabled",boolean.class,"configurazione-transazione",ConfigurazioneTransazione.class);
		this.CONFIGURAZIONE_TRANSAZIONE_PLUGIN = new org.openspcoop2.monitor.engine.config.transazioni.model.ConfigurazioneTransazionePluginModel(new ComplexField(father,"configurazione-transazione-plugin",org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneTransazionePlugin.class,"configurazione-transazione",ConfigurazioneTransazione.class));
		this.CONFIGURAZIONE_TRANSAZIONE_STATO = new org.openspcoop2.monitor.engine.config.transazioni.model.ConfigurazioneTransazioneStatoModel(new ComplexField(father,"configurazione-transazione-stato",org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneTransazioneStato.class,"configurazione-transazione",ConfigurazioneTransazione.class));
		this.CONFIGURAZIONE_TRANSAZIONE_RISORSA_CONTENUTO = new org.openspcoop2.monitor.engine.config.transazioni.model.ConfigurazioneTransazioneRisorsaContenutoModel(new ComplexField(father,"configurazione-transazione-risorsa-contenuto",org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneTransazioneRisorsaContenuto.class,"configurazione-transazione",ConfigurazioneTransazione.class));
	
	}
	
	

	public org.openspcoop2.monitor.engine.config.transazioni.model.IdConfigurazioneServizioAzioneModel ID_CONFIGURAZIONE_SERVIZIO_AZIONE = null;
	 
	public IField ENABLED = null;
	 
	public org.openspcoop2.monitor.engine.config.transazioni.model.ConfigurazioneTransazionePluginModel CONFIGURAZIONE_TRANSAZIONE_PLUGIN = null;
	 
	public org.openspcoop2.monitor.engine.config.transazioni.model.ConfigurazioneTransazioneStatoModel CONFIGURAZIONE_TRANSAZIONE_STATO = null;
	 
	public org.openspcoop2.monitor.engine.config.transazioni.model.ConfigurazioneTransazioneRisorsaContenutoModel CONFIGURAZIONE_TRANSAZIONE_RISORSA_CONTENUTO = null;
	 

	@Override
	public Class<ConfigurazioneTransazione> getModeledClass(){
		return ConfigurazioneTransazione.class;
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