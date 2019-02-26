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
package org.openspcoop2.core.mvc.properties.model;

import org.openspcoop2.core.mvc.properties.Compatibility;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model Compatibility 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class CompatibilityModel extends AbstractModel<Compatibility> {

	public CompatibilityModel(){
	
		super();
	
		this.TAGS = new org.openspcoop2.core.mvc.properties.model.TagsModel(new Field("tags",org.openspcoop2.core.mvc.properties.Tags.class,"compatibility",Compatibility.class));
		this.AND = new Field("and",boolean.class,"compatibility",Compatibility.class);
		this.NOT = new Field("not",boolean.class,"compatibility",Compatibility.class);
	
	}
	
	public CompatibilityModel(IField father){
	
		super(father);
	
		this.TAGS = new org.openspcoop2.core.mvc.properties.model.TagsModel(new ComplexField(father,"tags",org.openspcoop2.core.mvc.properties.Tags.class,"compatibility",Compatibility.class));
		this.AND = new ComplexField(father,"and",boolean.class,"compatibility",Compatibility.class);
		this.NOT = new ComplexField(father,"not",boolean.class,"compatibility",Compatibility.class);
	
	}
	
	

	public org.openspcoop2.core.mvc.properties.model.TagsModel TAGS = null;
	 
	public IField AND = null;
	 
	public IField NOT = null;
	 

	@Override
	public Class<Compatibility> getModeledClass(){
		return Compatibility.class;
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