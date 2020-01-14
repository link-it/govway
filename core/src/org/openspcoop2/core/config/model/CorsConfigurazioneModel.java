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
package org.openspcoop2.core.config.model;

import org.openspcoop2.core.config.CorsConfigurazione;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model CorsConfigurazione 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class CorsConfigurazioneModel extends AbstractModel<CorsConfigurazione> {

	public CorsConfigurazioneModel(){
	
		super();
	
		this.ACCESS_CONTROL_ALLOW_ORIGINS = new org.openspcoop2.core.config.model.CorsConfigurazioneOriginModel(new Field("access-control-allow-origins",org.openspcoop2.core.config.CorsConfigurazioneOrigin.class,"cors-configurazione",CorsConfigurazione.class));
		this.ACCESS_CONTROL_ALLOW_HEADERS = new org.openspcoop2.core.config.model.CorsConfigurazioneHeadersModel(new Field("access-control-allow-headers",org.openspcoop2.core.config.CorsConfigurazioneHeaders.class,"cors-configurazione",CorsConfigurazione.class));
		this.ACCESS_CONTROL_ALLOW_METHODS = new org.openspcoop2.core.config.model.CorsConfigurazioneMethodsModel(new Field("access-control-allow-methods",org.openspcoop2.core.config.CorsConfigurazioneMethods.class,"cors-configurazione",CorsConfigurazione.class));
		this.ACCESS_CONTROL_EXPOSE_HEADERS = new org.openspcoop2.core.config.model.CorsConfigurazioneHeadersModel(new Field("access-control-expose-headers",org.openspcoop2.core.config.CorsConfigurazioneHeaders.class,"cors-configurazione",CorsConfigurazione.class));
		this.STATO = new Field("stato",java.lang.String.class,"cors-configurazione",CorsConfigurazione.class);
		this.TIPO = new Field("tipo",java.lang.String.class,"cors-configurazione",CorsConfigurazione.class);
		this.ACCESS_CONTROL_ALL_ALLOW_ORIGINS = new Field("access-control-all-allow-origins",java.lang.String.class,"cors-configurazione",CorsConfigurazione.class);
		this.ACCESS_CONTROL_ALLOW_CREDENTIALS = new Field("access-control-allow-credentials",java.lang.String.class,"cors-configurazione",CorsConfigurazione.class);
		this.ACCESS_CONTROL_MAX_AGE = new Field("access-control-max-age",java.lang.Integer.class,"cors-configurazione",CorsConfigurazione.class);
	
	}
	
	public CorsConfigurazioneModel(IField father){
	
		super(father);
	
		this.ACCESS_CONTROL_ALLOW_ORIGINS = new org.openspcoop2.core.config.model.CorsConfigurazioneOriginModel(new ComplexField(father,"access-control-allow-origins",org.openspcoop2.core.config.CorsConfigurazioneOrigin.class,"cors-configurazione",CorsConfigurazione.class));
		this.ACCESS_CONTROL_ALLOW_HEADERS = new org.openspcoop2.core.config.model.CorsConfigurazioneHeadersModel(new ComplexField(father,"access-control-allow-headers",org.openspcoop2.core.config.CorsConfigurazioneHeaders.class,"cors-configurazione",CorsConfigurazione.class));
		this.ACCESS_CONTROL_ALLOW_METHODS = new org.openspcoop2.core.config.model.CorsConfigurazioneMethodsModel(new ComplexField(father,"access-control-allow-methods",org.openspcoop2.core.config.CorsConfigurazioneMethods.class,"cors-configurazione",CorsConfigurazione.class));
		this.ACCESS_CONTROL_EXPOSE_HEADERS = new org.openspcoop2.core.config.model.CorsConfigurazioneHeadersModel(new ComplexField(father,"access-control-expose-headers",org.openspcoop2.core.config.CorsConfigurazioneHeaders.class,"cors-configurazione",CorsConfigurazione.class));
		this.STATO = new ComplexField(father,"stato",java.lang.String.class,"cors-configurazione",CorsConfigurazione.class);
		this.TIPO = new ComplexField(father,"tipo",java.lang.String.class,"cors-configurazione",CorsConfigurazione.class);
		this.ACCESS_CONTROL_ALL_ALLOW_ORIGINS = new ComplexField(father,"access-control-all-allow-origins",java.lang.String.class,"cors-configurazione",CorsConfigurazione.class);
		this.ACCESS_CONTROL_ALLOW_CREDENTIALS = new ComplexField(father,"access-control-allow-credentials",java.lang.String.class,"cors-configurazione",CorsConfigurazione.class);
		this.ACCESS_CONTROL_MAX_AGE = new ComplexField(father,"access-control-max-age",java.lang.Integer.class,"cors-configurazione",CorsConfigurazione.class);
	
	}
	
	

	public org.openspcoop2.core.config.model.CorsConfigurazioneOriginModel ACCESS_CONTROL_ALLOW_ORIGINS = null;
	 
	public org.openspcoop2.core.config.model.CorsConfigurazioneHeadersModel ACCESS_CONTROL_ALLOW_HEADERS = null;
	 
	public org.openspcoop2.core.config.model.CorsConfigurazioneMethodsModel ACCESS_CONTROL_ALLOW_METHODS = null;
	 
	public org.openspcoop2.core.config.model.CorsConfigurazioneHeadersModel ACCESS_CONTROL_EXPOSE_HEADERS = null;
	 
	public IField STATO = null;
	 
	public IField TIPO = null;
	 
	public IField ACCESS_CONTROL_ALL_ALLOW_ORIGINS = null;
	 
	public IField ACCESS_CONTROL_ALLOW_CREDENTIALS = null;
	 
	public IField ACCESS_CONTROL_MAX_AGE = null;
	 

	@Override
	public Class<CorsConfigurazione> getModeledClass(){
		return CorsConfigurazione.class;
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