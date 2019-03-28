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

import org.openspcoop2.core.config.ResponseCachingConfigurazione;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model ResponseCachingConfigurazione 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ResponseCachingConfigurazioneModel extends AbstractModel<ResponseCachingConfigurazione> {

	public ResponseCachingConfigurazioneModel(){
	
		super();
	
		this.HASH_GENERATOR = new org.openspcoop2.core.config.model.ResponseCachingConfigurazioneHashGeneratorModel(new Field("hash-generator",org.openspcoop2.core.config.ResponseCachingConfigurazioneHashGenerator.class,"response-caching-configurazione",ResponseCachingConfigurazione.class));
		this.CONTROL = new org.openspcoop2.core.config.model.ResponseCachingConfigurazioneControlModel(new Field("control",org.openspcoop2.core.config.ResponseCachingConfigurazioneControl.class,"response-caching-configurazione",ResponseCachingConfigurazione.class));
		this.REGOLA = new org.openspcoop2.core.config.model.ResponseCachingConfigurazioneRegolaModel(new Field("regola",org.openspcoop2.core.config.ResponseCachingConfigurazioneRegola.class,"response-caching-configurazione",ResponseCachingConfigurazione.class));
		this.STATO = new Field("stato",java.lang.String.class,"response-caching-configurazione",ResponseCachingConfigurazione.class);
		this.CACHE_TIMEOUT_SECONDS = new Field("cache-timeout-seconds",java.lang.Integer.class,"response-caching-configurazione",ResponseCachingConfigurazione.class);
		this.MAX_MESSAGE_SIZE = new Field("max-message-size",java.lang.Long.class,"response-caching-configurazione",ResponseCachingConfigurazione.class);
	
	}
	
	public ResponseCachingConfigurazioneModel(IField father){
	
		super(father);
	
		this.HASH_GENERATOR = new org.openspcoop2.core.config.model.ResponseCachingConfigurazioneHashGeneratorModel(new ComplexField(father,"hash-generator",org.openspcoop2.core.config.ResponseCachingConfigurazioneHashGenerator.class,"response-caching-configurazione",ResponseCachingConfigurazione.class));
		this.CONTROL = new org.openspcoop2.core.config.model.ResponseCachingConfigurazioneControlModel(new ComplexField(father,"control",org.openspcoop2.core.config.ResponseCachingConfigurazioneControl.class,"response-caching-configurazione",ResponseCachingConfigurazione.class));
		this.REGOLA = new org.openspcoop2.core.config.model.ResponseCachingConfigurazioneRegolaModel(new ComplexField(father,"regola",org.openspcoop2.core.config.ResponseCachingConfigurazioneRegola.class,"response-caching-configurazione",ResponseCachingConfigurazione.class));
		this.STATO = new ComplexField(father,"stato",java.lang.String.class,"response-caching-configurazione",ResponseCachingConfigurazione.class);
		this.CACHE_TIMEOUT_SECONDS = new ComplexField(father,"cache-timeout-seconds",java.lang.Integer.class,"response-caching-configurazione",ResponseCachingConfigurazione.class);
		this.MAX_MESSAGE_SIZE = new ComplexField(father,"max-message-size",java.lang.Long.class,"response-caching-configurazione",ResponseCachingConfigurazione.class);
	
	}
	
	

	public org.openspcoop2.core.config.model.ResponseCachingConfigurazioneHashGeneratorModel HASH_GENERATOR = null;
	 
	public org.openspcoop2.core.config.model.ResponseCachingConfigurazioneControlModel CONTROL = null;
	 
	public org.openspcoop2.core.config.model.ResponseCachingConfigurazioneRegolaModel REGOLA = null;
	 
	public IField STATO = null;
	 
	public IField CACHE_TIMEOUT_SECONDS = null;
	 
	public IField MAX_MESSAGE_SIZE = null;
	 

	@Override
	public Class<ResponseCachingConfigurazione> getModeledClass(){
		return ResponseCachingConfigurazione.class;
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
