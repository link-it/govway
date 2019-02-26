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
package org.openspcoop2.core.controllo_traffico.model;

import org.openspcoop2.core.controllo_traffico.ConfigurazioneRateLimiting;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model ConfigurazioneRateLimiting 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ConfigurazioneRateLimitingModel extends AbstractModel<ConfigurazioneRateLimiting> {

	public ConfigurazioneRateLimitingModel(){
	
		super();
	
		this.TIPO_ERRORE = new Field("tipo-errore",java.lang.String.class,"configurazione-rate-limiting",ConfigurazioneRateLimiting.class);
		this.TIPO_ERRORE_INCLUDI_DESCRIZIONE = new Field("tipo-errore-includi-descrizione",boolean.class,"configurazione-rate-limiting",ConfigurazioneRateLimiting.class);
	
	}
	
	public ConfigurazioneRateLimitingModel(IField father){
	
		super(father);
	
		this.TIPO_ERRORE = new ComplexField(father,"tipo-errore",java.lang.String.class,"configurazione-rate-limiting",ConfigurazioneRateLimiting.class);
		this.TIPO_ERRORE_INCLUDI_DESCRIZIONE = new ComplexField(father,"tipo-errore-includi-descrizione",boolean.class,"configurazione-rate-limiting",ConfigurazioneRateLimiting.class);
	
	}
	
	

	public IField TIPO_ERRORE = null;
	 
	public IField TIPO_ERRORE_INCLUDI_DESCRIZIONE = null;
	 

	@Override
	public Class<ConfigurazioneRateLimiting> getModeledClass(){
		return ConfigurazioneRateLimiting.class;
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