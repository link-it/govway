/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it).
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

import org.openspcoop2.core.mvc.properties.Section;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model Section 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SectionModel extends AbstractModel<Section> {

	public SectionModel(){
	
		super();
	
		this.CONDITIONS = new org.openspcoop2.core.mvc.properties.model.ConditionsModel(new Field("conditions",org.openspcoop2.core.mvc.properties.Conditions.class,"section",Section.class));
		this.ITEM = new org.openspcoop2.core.mvc.properties.model.ItemModel(new Field("item",org.openspcoop2.core.mvc.properties.Item.class,"section",Section.class));
		this.SUBSECTION = new org.openspcoop2.core.mvc.properties.model.SubsectionModel(new Field("subsection",org.openspcoop2.core.mvc.properties.Subsection.class,"section",Section.class));
		this.LABEL = new Field("label",java.lang.String.class,"section",Section.class);
		this.HIDDEN = new Field("hidden",boolean.class,"section",Section.class);
	
	}
	
	public SectionModel(IField father){
	
		super(father);
	
		this.CONDITIONS = new org.openspcoop2.core.mvc.properties.model.ConditionsModel(new ComplexField(father,"conditions",org.openspcoop2.core.mvc.properties.Conditions.class,"section",Section.class));
		this.ITEM = new org.openspcoop2.core.mvc.properties.model.ItemModel(new ComplexField(father,"item",org.openspcoop2.core.mvc.properties.Item.class,"section",Section.class));
		this.SUBSECTION = new org.openspcoop2.core.mvc.properties.model.SubsectionModel(new ComplexField(father,"subsection",org.openspcoop2.core.mvc.properties.Subsection.class,"section",Section.class));
		this.LABEL = new ComplexField(father,"label",java.lang.String.class,"section",Section.class);
		this.HIDDEN = new ComplexField(father,"hidden",boolean.class,"section",Section.class);
	
	}
	
	

	public org.openspcoop2.core.mvc.properties.model.ConditionsModel CONDITIONS = null;
	 
	public org.openspcoop2.core.mvc.properties.model.ItemModel ITEM = null;
	 
	public org.openspcoop2.core.mvc.properties.model.SubsectionModel SUBSECTION = null;
	 
	public IField LABEL = null;
	 
	public IField HIDDEN = null;
	 

	@Override
	public Class<Section> getModeledClass(){
		return Section.class;
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