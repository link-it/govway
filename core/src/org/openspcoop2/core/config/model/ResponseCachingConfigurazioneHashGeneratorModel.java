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

import org.openspcoop2.core.config.ResponseCachingConfigurazioneHashGenerator;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model ResponseCachingConfigurazioneHashGenerator 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ResponseCachingConfigurazioneHashGeneratorModel extends AbstractModel<ResponseCachingConfigurazioneHashGenerator> {

	public ResponseCachingConfigurazioneHashGeneratorModel(){
	
		super();
	
		this.HEADER = new Field("header",java.lang.String.class,"response-caching-configurazione-hash-generator",ResponseCachingConfigurazioneHashGenerator.class);
		this.REQUEST_URI = new Field("request-uri",java.lang.String.class,"response-caching-configurazione-hash-generator",ResponseCachingConfigurazioneHashGenerator.class);
		this.HEADERS = new Field("headers",java.lang.String.class,"response-caching-configurazione-hash-generator",ResponseCachingConfigurazioneHashGenerator.class);
		this.PAYLOAD = new Field("payload",java.lang.String.class,"response-caching-configurazione-hash-generator",ResponseCachingConfigurazioneHashGenerator.class);
	
	}
	
	public ResponseCachingConfigurazioneHashGeneratorModel(IField father){
	
		super(father);
	
		this.HEADER = new ComplexField(father,"header",java.lang.String.class,"response-caching-configurazione-hash-generator",ResponseCachingConfigurazioneHashGenerator.class);
		this.REQUEST_URI = new ComplexField(father,"request-uri",java.lang.String.class,"response-caching-configurazione-hash-generator",ResponseCachingConfigurazioneHashGenerator.class);
		this.HEADERS = new ComplexField(father,"headers",java.lang.String.class,"response-caching-configurazione-hash-generator",ResponseCachingConfigurazioneHashGenerator.class);
		this.PAYLOAD = new ComplexField(father,"payload",java.lang.String.class,"response-caching-configurazione-hash-generator",ResponseCachingConfigurazioneHashGenerator.class);
	
	}
	
	

	public IField HEADER = null;
	 
	public IField REQUEST_URI = null;
	 
	public IField HEADERS = null;
	 
	public IField PAYLOAD = null;
	 

	@Override
	public Class<ResponseCachingConfigurazioneHashGenerator> getModeledClass(){
		return ResponseCachingConfigurazioneHashGenerator.class;
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
