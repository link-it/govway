/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2016 Link.it srl (http://link.it).
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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
package org.openspcoop2.core.tracciamento.model;

import org.openspcoop2.core.tracciamento.Allegato;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model Allegato 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class AllegatoModel extends AbstractModel<Allegato> {

	public AllegatoModel(){
	
		super();
	
		this.CONTENT_ID = new Field("content-id",java.lang.String.class,"allegato",Allegato.class);
		this.CONTENT_LOCATION = new Field("content-location",java.lang.String.class,"allegato",Allegato.class);
		this.CONTENT_TYPE = new Field("content-type",java.lang.String.class,"allegato",Allegato.class);
		this.DIGEST = new Field("digest",java.lang.String.class,"allegato",Allegato.class);
	
	}
	
	public AllegatoModel(IField father){
	
		super(father);
	
		this.CONTENT_ID = new ComplexField(father,"content-id",java.lang.String.class,"allegato",Allegato.class);
		this.CONTENT_LOCATION = new ComplexField(father,"content-location",java.lang.String.class,"allegato",Allegato.class);
		this.CONTENT_TYPE = new ComplexField(father,"content-type",java.lang.String.class,"allegato",Allegato.class);
		this.DIGEST = new ComplexField(father,"digest",java.lang.String.class,"allegato",Allegato.class);
	
	}
	
	

	public IField CONTENT_ID = null;
	 
	public IField CONTENT_LOCATION = null;
	 
	public IField CONTENT_TYPE = null;
	 
	public IField DIGEST = null;
	 

	@Override
	public Class<Allegato> getModeledClass(){
		return Allegato.class;
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