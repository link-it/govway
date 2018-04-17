/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
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
package org.openspcoop2.core.controllo_congestione.model;

import org.openspcoop2.core.controllo_congestione.ConfigurazioneGenerale;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model ConfigurazioneGenerale 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ConfigurazioneGeneraleModel extends AbstractModel<ConfigurazioneGenerale> {

	public ConfigurazioneGeneraleModel(){
	
		super();
	
		this.CONTROLLO_TRAFFICO = new org.openspcoop2.core.controllo_congestione.model.ConfigurazioneControlloTrafficoModel(new Field("controllo-traffico",org.openspcoop2.core.controllo_congestione.ConfigurazioneControlloTraffico.class,"configurazione-generale",ConfigurazioneGenerale.class));
		this.TEMPI_RISPOSTA_FRUIZIONE = new org.openspcoop2.core.controllo_congestione.model.TempiRispostaFruizioneModel(new Field("tempi-risposta-fruizione",org.openspcoop2.core.controllo_congestione.TempiRispostaFruizione.class,"configurazione-generale",ConfigurazioneGenerale.class));
		this.TEMPI_RISPOSTA_EROGAZIONE = new org.openspcoop2.core.controllo_congestione.model.TempiRispostaErogazioneModel(new Field("tempi-risposta-erogazione",org.openspcoop2.core.controllo_congestione.TempiRispostaErogazione.class,"configurazione-generale",ConfigurazioneGenerale.class));
		this.RATE_LIMITING = new org.openspcoop2.core.controllo_congestione.model.ConfigurazioneRateLimitingModel(new Field("rate-limiting",org.openspcoop2.core.controllo_congestione.ConfigurazioneRateLimiting.class,"configurazione-generale",ConfigurazioneGenerale.class));
		this.CACHE = new org.openspcoop2.core.controllo_congestione.model.CacheModel(new Field("cache",org.openspcoop2.core.controllo_congestione.Cache.class,"configurazione-generale",ConfigurazioneGenerale.class));
	
	}
	
	public ConfigurazioneGeneraleModel(IField father){
	
		super(father);
	
		this.CONTROLLO_TRAFFICO = new org.openspcoop2.core.controllo_congestione.model.ConfigurazioneControlloTrafficoModel(new ComplexField(father,"controllo-traffico",org.openspcoop2.core.controllo_congestione.ConfigurazioneControlloTraffico.class,"configurazione-generale",ConfigurazioneGenerale.class));
		this.TEMPI_RISPOSTA_FRUIZIONE = new org.openspcoop2.core.controllo_congestione.model.TempiRispostaFruizioneModel(new ComplexField(father,"tempi-risposta-fruizione",org.openspcoop2.core.controllo_congestione.TempiRispostaFruizione.class,"configurazione-generale",ConfigurazioneGenerale.class));
		this.TEMPI_RISPOSTA_EROGAZIONE = new org.openspcoop2.core.controllo_congestione.model.TempiRispostaErogazioneModel(new ComplexField(father,"tempi-risposta-erogazione",org.openspcoop2.core.controllo_congestione.TempiRispostaErogazione.class,"configurazione-generale",ConfigurazioneGenerale.class));
		this.RATE_LIMITING = new org.openspcoop2.core.controllo_congestione.model.ConfigurazioneRateLimitingModel(new ComplexField(father,"rate-limiting",org.openspcoop2.core.controllo_congestione.ConfigurazioneRateLimiting.class,"configurazione-generale",ConfigurazioneGenerale.class));
		this.CACHE = new org.openspcoop2.core.controllo_congestione.model.CacheModel(new ComplexField(father,"cache",org.openspcoop2.core.controllo_congestione.Cache.class,"configurazione-generale",ConfigurazioneGenerale.class));
	
	}
	
	

	public org.openspcoop2.core.controllo_congestione.model.ConfigurazioneControlloTrafficoModel CONTROLLO_TRAFFICO = null;
	 
	public org.openspcoop2.core.controllo_congestione.model.TempiRispostaFruizioneModel TEMPI_RISPOSTA_FRUIZIONE = null;
	 
	public org.openspcoop2.core.controllo_congestione.model.TempiRispostaErogazioneModel TEMPI_RISPOSTA_EROGAZIONE = null;
	 
	public org.openspcoop2.core.controllo_congestione.model.ConfigurazioneRateLimitingModel RATE_LIMITING = null;
	 
	public org.openspcoop2.core.controllo_congestione.model.CacheModel CACHE = null;
	 

	@Override
	public Class<ConfigurazioneGenerale> getModeledClass(){
		return ConfigurazioneGenerale.class;
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