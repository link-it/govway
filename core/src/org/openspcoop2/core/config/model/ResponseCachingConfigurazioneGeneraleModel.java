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
package org.openspcoop2.core.config.model;

import org.openspcoop2.core.config.ResponseCachingConfigurazioneGenerale;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model ResponseCachingConfigurazioneGenerale 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ResponseCachingConfigurazioneGeneraleModel extends AbstractModel<ResponseCachingConfigurazioneGenerale> {

	public ResponseCachingConfigurazioneGeneraleModel(){
	
		super();
	
		this.CONFIGURAZIONE = new org.openspcoop2.core.config.model.ResponseCachingConfigurazioneModel(new Field("configurazione",org.openspcoop2.core.config.ResponseCachingConfigurazione.class,"response-caching-configurazione-generale",ResponseCachingConfigurazioneGenerale.class));
		this.CACHE = new org.openspcoop2.core.config.model.CacheModel(new Field("cache",org.openspcoop2.core.config.Cache.class,"response-caching-configurazione-generale",ResponseCachingConfigurazioneGenerale.class));
	
	}
	
	public ResponseCachingConfigurazioneGeneraleModel(IField father){
	
		super(father);
	
		this.CONFIGURAZIONE = new org.openspcoop2.core.config.model.ResponseCachingConfigurazioneModel(new ComplexField(father,"configurazione",org.openspcoop2.core.config.ResponseCachingConfigurazione.class,"response-caching-configurazione-generale",ResponseCachingConfigurazioneGenerale.class));
		this.CACHE = new org.openspcoop2.core.config.model.CacheModel(new ComplexField(father,"cache",org.openspcoop2.core.config.Cache.class,"response-caching-configurazione-generale",ResponseCachingConfigurazioneGenerale.class));
	
	}
	
	

	public org.openspcoop2.core.config.model.ResponseCachingConfigurazioneModel CONFIGURAZIONE = null;
	 
	public org.openspcoop2.core.config.model.CacheModel CACHE = null;
	 

	@Override
	public Class<ResponseCachingConfigurazioneGenerale> getModeledClass(){
		return ResponseCachingConfigurazioneGenerale.class;
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