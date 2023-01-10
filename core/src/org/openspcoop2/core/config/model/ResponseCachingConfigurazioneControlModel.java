/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it).
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

import org.openspcoop2.core.config.ResponseCachingConfigurazioneControl;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model ResponseCachingConfigurazioneControl 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ResponseCachingConfigurazioneControlModel extends AbstractModel<ResponseCachingConfigurazioneControl> {

	public ResponseCachingConfigurazioneControlModel(){
	
		super();
	
		this.NO_CACHE = new Field("noCache",boolean.class,"response-caching-configurazione-control",ResponseCachingConfigurazioneControl.class);
		this.MAX_AGE = new Field("maxAge",boolean.class,"response-caching-configurazione-control",ResponseCachingConfigurazioneControl.class);
		this.NO_STORE = new Field("noStore",boolean.class,"response-caching-configurazione-control",ResponseCachingConfigurazioneControl.class);
	
	}
	
	public ResponseCachingConfigurazioneControlModel(IField father){
	
		super(father);
	
		this.NO_CACHE = new ComplexField(father,"noCache",boolean.class,"response-caching-configurazione-control",ResponseCachingConfigurazioneControl.class);
		this.MAX_AGE = new ComplexField(father,"maxAge",boolean.class,"response-caching-configurazione-control",ResponseCachingConfigurazioneControl.class);
		this.NO_STORE = new ComplexField(father,"noStore",boolean.class,"response-caching-configurazione-control",ResponseCachingConfigurazioneControl.class);
	
	}
	
	

	public IField NO_CACHE = null;
	 
	public IField MAX_AGE = null;
	 
	public IField NO_STORE = null;
	 

	@Override
	public Class<ResponseCachingConfigurazioneControl> getModeledClass(){
		return ResponseCachingConfigurazioneControl.class;
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