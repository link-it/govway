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

import org.openspcoop2.core.config.TrasformazioneRest;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model TrasformazioneRest 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class TrasformazioneRestModel extends AbstractModel<TrasformazioneRest> {

	public TrasformazioneRestModel(){
	
		super();
	
		this.METODO = new Field("metodo",java.lang.String.class,"trasformazione-rest",TrasformazioneRest.class);
		this.PATH = new Field("path",java.lang.String.class,"trasformazione-rest",TrasformazioneRest.class);
	
	}
	
	public TrasformazioneRestModel(IField father){
	
		super(father);
	
		this.METODO = new ComplexField(father,"metodo",java.lang.String.class,"trasformazione-rest",TrasformazioneRest.class);
		this.PATH = new ComplexField(father,"path",java.lang.String.class,"trasformazione-rest",TrasformazioneRest.class);
	
	}
	
	

	public IField METODO = null;
	 
	public IField PATH = null;
	 

	@Override
	public Class<TrasformazioneRest> getModeledClass(){
		return TrasformazioneRest.class;
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