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

import org.openspcoop2.core.config.ResponseCachingConfigurazioneRegola;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model ResponseCachingConfigurazioneRegola 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ResponseCachingConfigurazioneRegolaModel extends AbstractModel<ResponseCachingConfigurazioneRegola> {

	public ResponseCachingConfigurazioneRegolaModel(){
	
		super();
	
		this.RETURN_CODE_MIN = new Field("return-code-min",java.lang.Integer.class,"response-caching-configurazione-regola",ResponseCachingConfigurazioneRegola.class);
		this.RETURN_CODE_MAX = new Field("return-code-max",java.lang.Integer.class,"response-caching-configurazione-regola",ResponseCachingConfigurazioneRegola.class);
		this.FAULT = new Field("fault",boolean.class,"response-caching-configurazione-regola",ResponseCachingConfigurazioneRegola.class);
		this.CACHE_TIMEOUT_SECONDS = new Field("cache-timeout-seconds",java.lang.Integer.class,"response-caching-configurazione-regola",ResponseCachingConfigurazioneRegola.class);
	
	}
	
	public ResponseCachingConfigurazioneRegolaModel(IField father){
	
		super(father);
	
		this.RETURN_CODE_MIN = new ComplexField(father,"return-code-min",java.lang.Integer.class,"response-caching-configurazione-regola",ResponseCachingConfigurazioneRegola.class);
		this.RETURN_CODE_MAX = new ComplexField(father,"return-code-max",java.lang.Integer.class,"response-caching-configurazione-regola",ResponseCachingConfigurazioneRegola.class);
		this.FAULT = new ComplexField(father,"fault",boolean.class,"response-caching-configurazione-regola",ResponseCachingConfigurazioneRegola.class);
		this.CACHE_TIMEOUT_SECONDS = new ComplexField(father,"cache-timeout-seconds",java.lang.Integer.class,"response-caching-configurazione-regola",ResponseCachingConfigurazioneRegola.class);
	
	}
	
	

	public IField RETURN_CODE_MIN = null;
	 
	public IField RETURN_CODE_MAX = null;
	 
	public IField FAULT = null;
	 
	public IField CACHE_TIMEOUT_SECONDS = null;
	 

	@Override
	public Class<ResponseCachingConfigurazioneRegola> getModeledClass(){
		return ResponseCachingConfigurazioneRegola.class;
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