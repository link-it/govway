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
package org.openspcoop2.core.mvc.properties.model;

import org.openspcoop2.core.mvc.properties.Config;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model Config 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ConfigModel extends AbstractModel<Config> {

	public ConfigModel(){
	
		super();
	
		this.COMPATIBILITY = new org.openspcoop2.core.mvc.properties.model.CompatibilityModel(new Field("compatibility",org.openspcoop2.core.mvc.properties.Compatibility.class,"config",Config.class));
		this.PROPERTIES = new org.openspcoop2.core.mvc.properties.model.PropertiesModel(new Field("properties",org.openspcoop2.core.mvc.properties.Properties.class,"config",Config.class));
		this.SECTION = new org.openspcoop2.core.mvc.properties.model.SectionModel(new Field("section",org.openspcoop2.core.mvc.properties.Section.class,"config",Config.class));
		this.ID = new Field("id",java.lang.String.class,"config",Config.class);
		this.LABEL = new Field("label",java.lang.String.class,"config",Config.class);
		this.SORT_LABEL = new Field("sortLabel",java.lang.String.class,"config",Config.class);
		this.DESCRIZIONE = new Field("descrizione",java.lang.String.class,"config",Config.class);
		this.PROVIDER = new Field("provider",java.lang.String.class,"config",Config.class);
	
	}
	
	public ConfigModel(IField father){
	
		super(father);
	
		this.COMPATIBILITY = new org.openspcoop2.core.mvc.properties.model.CompatibilityModel(new ComplexField(father,"compatibility",org.openspcoop2.core.mvc.properties.Compatibility.class,"config",Config.class));
		this.PROPERTIES = new org.openspcoop2.core.mvc.properties.model.PropertiesModel(new ComplexField(father,"properties",org.openspcoop2.core.mvc.properties.Properties.class,"config",Config.class));
		this.SECTION = new org.openspcoop2.core.mvc.properties.model.SectionModel(new ComplexField(father,"section",org.openspcoop2.core.mvc.properties.Section.class,"config",Config.class));
		this.ID = new ComplexField(father,"id",java.lang.String.class,"config",Config.class);
		this.LABEL = new ComplexField(father,"label",java.lang.String.class,"config",Config.class);
		this.SORT_LABEL = new ComplexField(father,"sortLabel",java.lang.String.class,"config",Config.class);
		this.DESCRIZIONE = new ComplexField(father,"descrizione",java.lang.String.class,"config",Config.class);
		this.PROVIDER = new ComplexField(father,"provider",java.lang.String.class,"config",Config.class);
	
	}
	
	

	public org.openspcoop2.core.mvc.properties.model.CompatibilityModel COMPATIBILITY = null;
	 
	public org.openspcoop2.core.mvc.properties.model.PropertiesModel PROPERTIES = null;
	 
	public org.openspcoop2.core.mvc.properties.model.SectionModel SECTION = null;
	 
	public IField ID = null;
	 
	public IField LABEL = null;
	 
	public IField SORT_LABEL = null;
	 
	public IField DESCRIZIONE = null;
	 
	public IField PROVIDER = null;
	 

	@Override
	public Class<Config> getModeledClass(){
		return Config.class;
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