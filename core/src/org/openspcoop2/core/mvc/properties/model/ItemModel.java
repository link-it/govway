/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it).
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

import org.openspcoop2.core.mvc.properties.Item;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model Item 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ItemModel extends AbstractModel<Item> {

	public ItemModel(){
	
		super();
	
		this.CONDITIONS = new org.openspcoop2.core.mvc.properties.model.ConditionsModel(new Field("conditions",org.openspcoop2.core.mvc.properties.Conditions.class,"item",Item.class));
		this.VALUES = new org.openspcoop2.core.mvc.properties.model.ItemValuesModel(new Field("values",org.openspcoop2.core.mvc.properties.ItemValues.class,"item",Item.class));
		this.PROPERTY = new org.openspcoop2.core.mvc.properties.model.PropertyModel(new Field("property",org.openspcoop2.core.mvc.properties.Property.class,"item",Item.class));
		this.TYPE = new Field("type",java.lang.String.class,"item",Item.class);
		this.NAME = new Field("name",java.lang.String.class,"item",Item.class);
		this.LABEL = new Field("label",java.lang.String.class,"item",Item.class);
		this.REQUIRED = new Field("required",boolean.class,"item",Item.class);
		this.RELOAD_ON_CHANGE = new Field("reloadOnChange",boolean.class,"item",Item.class);
		this.DEFAULT = new Field("default",java.lang.String.class,"item",Item.class);
		this.DEFAULT_SELECTED = new Field("defaultSelected",boolean.class,"item",Item.class);
		this.VALUE = new Field("value",java.lang.String.class,"item",Item.class);
		this.MIN = new Field("min",java.lang.Integer.class,"item",Item.class);
		this.MAX = new Field("max",java.lang.Integer.class,"item",Item.class);
		this.VALIDATION = new Field("validation",java.lang.String.class,"item",Item.class);
	
	}
	
	public ItemModel(IField father){
	
		super(father);
	
		this.CONDITIONS = new org.openspcoop2.core.mvc.properties.model.ConditionsModel(new ComplexField(father,"conditions",org.openspcoop2.core.mvc.properties.Conditions.class,"item",Item.class));
		this.VALUES = new org.openspcoop2.core.mvc.properties.model.ItemValuesModel(new ComplexField(father,"values",org.openspcoop2.core.mvc.properties.ItemValues.class,"item",Item.class));
		this.PROPERTY = new org.openspcoop2.core.mvc.properties.model.PropertyModel(new ComplexField(father,"property",org.openspcoop2.core.mvc.properties.Property.class,"item",Item.class));
		this.TYPE = new ComplexField(father,"type",java.lang.String.class,"item",Item.class);
		this.NAME = new ComplexField(father,"name",java.lang.String.class,"item",Item.class);
		this.LABEL = new ComplexField(father,"label",java.lang.String.class,"item",Item.class);
		this.REQUIRED = new ComplexField(father,"required",boolean.class,"item",Item.class);
		this.RELOAD_ON_CHANGE = new ComplexField(father,"reloadOnChange",boolean.class,"item",Item.class);
		this.DEFAULT = new ComplexField(father,"default",java.lang.String.class,"item",Item.class);
		this.DEFAULT_SELECTED = new ComplexField(father,"defaultSelected",boolean.class,"item",Item.class);
		this.VALUE = new ComplexField(father,"value",java.lang.String.class,"item",Item.class);
		this.MIN = new ComplexField(father,"min",java.lang.Integer.class,"item",Item.class);
		this.MAX = new ComplexField(father,"max",java.lang.Integer.class,"item",Item.class);
		this.VALIDATION = new ComplexField(father,"validation",java.lang.String.class,"item",Item.class);
	
	}
	
	

	public org.openspcoop2.core.mvc.properties.model.ConditionsModel CONDITIONS = null;
	 
	public org.openspcoop2.core.mvc.properties.model.ItemValuesModel VALUES = null;
	 
	public org.openspcoop2.core.mvc.properties.model.PropertyModel PROPERTY = null;
	 
	public IField TYPE = null;
	 
	public IField NAME = null;
	 
	public IField LABEL = null;
	 
	public IField REQUIRED = null;
	 
	public IField RELOAD_ON_CHANGE = null;
	 
	public IField DEFAULT = null;
	 
	public IField DEFAULT_SELECTED = null;
	 
	public IField VALUE = null;
	 
	public IField MIN = null;
	 
	public IField MAX = null;
	 
	public IField VALIDATION = null;
	 

	@Override
	public Class<Item> getModeledClass(){
		return Item.class;
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