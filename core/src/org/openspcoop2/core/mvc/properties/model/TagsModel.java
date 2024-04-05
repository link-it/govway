/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it).
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

import org.openspcoop2.core.mvc.properties.Tags;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model Tags 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class TagsModel extends AbstractModel<Tags> {

	public TagsModel(){
	
		super();
	
		this.TAG = new Field("tag",java.lang.String.class,"tags",Tags.class);
		this.AND = new Field("and",boolean.class,"tags",Tags.class);
		this.NOT = new Field("not",boolean.class,"tags",Tags.class);
	
	}
	
	public TagsModel(IField father){
	
		super(father);
	
		this.TAG = new ComplexField(father,"tag",java.lang.String.class,"tags",Tags.class);
		this.AND = new ComplexField(father,"and",boolean.class,"tags",Tags.class);
		this.NOT = new ComplexField(father,"not",boolean.class,"tags",Tags.class);
	
	}
	
	

	public IField TAG = null;
	 
	public IField AND = null;
	 
	public IField NOT = null;
	 

	@Override
	public Class<Tags> getModeledClass(){
		return Tags.class;
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