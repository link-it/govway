/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it).
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

import org.openspcoop2.core.config.CorsConfigurazioneHeaders;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model CorsConfigurazioneHeaders 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class CorsConfigurazioneHeadersModel extends AbstractModel<CorsConfigurazioneHeaders> {

	public CorsConfigurazioneHeadersModel(){
	
		super();
	
		this.HEADER = new Field("header",java.lang.String.class,"cors-configurazione-headers",CorsConfigurazioneHeaders.class);
	
	}
	
	public CorsConfigurazioneHeadersModel(IField father){
	
		super(father);
	
		this.HEADER = new ComplexField(father,"header",java.lang.String.class,"cors-configurazione-headers",CorsConfigurazioneHeaders.class);
	
	}
	
	

	public IField HEADER = null;
	 

	@Override
	public Class<CorsConfigurazioneHeaders> getModeledClass(){
		return CorsConfigurazioneHeaders.class;
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