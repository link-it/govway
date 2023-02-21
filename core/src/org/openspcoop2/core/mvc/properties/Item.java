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
package org.openspcoop2.core.mvc.properties;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.openspcoop2.core.mvc.properties.constants.ItemType;
import java.io.Serializable;


/** <p>Java class for item complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="item"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="conditions" type="{http://www.openspcoop2.org/core/mvc/properties}conditions" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="values" type="{http://www.openspcoop2.org/core/mvc/properties}itemValues" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="property" type="{http://www.openspcoop2.org/core/mvc/properties}property" minOccurs="1" maxOccurs="1"/&gt;
 * 		&lt;/sequence&gt;
 * 		&lt;attribute name="type" type="{http://www.openspcoop2.org/core/mvc/properties}itemType" use="required"/&gt;
 * 		&lt;attribute name="name" type="{http://www.w3.org/2001/XMLSchema}string" use="required"/&gt;
 * 		&lt;attribute name="label" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/&gt;
 * 		&lt;attribute name="note" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/&gt;
 * 		&lt;attribute name="required" type="{http://www.w3.org/2001/XMLSchema}boolean" use="optional" default="false"/&gt;
 * 		&lt;attribute name="reloadOnChange" type="{http://www.w3.org/2001/XMLSchema}boolean" use="optional" default="false"/&gt;
 * 		&lt;attribute name="default" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/&gt;
 * 		&lt;attribute name="defaultSelected" type="{http://www.w3.org/2001/XMLSchema}boolean" use="optional"/&gt;
 * 		&lt;attribute name="value" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/&gt;
 * 		&lt;attribute name="min" type="{http://www.w3.org/2001/XMLSchema}integer" use="optional"/&gt;
 * 		&lt;attribute name="max" type="{http://www.w3.org/2001/XMLSchema}integer" use="optional"/&gt;
 * 		&lt;attribute name="validation" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "item", 
  propOrder = {
  	"conditions",
  	"values",
  	"property"
  }
)

@XmlRootElement(name = "item")

public class Item extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public Item() {
    super();
  }

  public Conditions getConditions() {
    return this.conditions;
  }

  public void setConditions(Conditions conditions) {
    this.conditions = conditions;
  }

  public ItemValues getValues() {
    return this.values;
  }

  public void setValues(ItemValues values) {
    this.values = values;
  }

  public Property getProperty() {
    return this.property;
  }

  public void setProperty(Property property) {
    this.property = property;
  }

  public void set_value_type(String value) {
    this.type = (ItemType) ItemType.toEnumConstantFromString(value);
  }

  public String get_value_type() {
    if(this.type == null){
    	return null;
    }else{
    	return this.type.toString();
    }
  }

  public org.openspcoop2.core.mvc.properties.constants.ItemType getType() {
    return this.type;
  }

  public void setType(org.openspcoop2.core.mvc.properties.constants.ItemType type) {
    this.type = type;
  }

  public java.lang.String getName() {
    return this.name;
  }

  public void setName(java.lang.String name) {
    this.name = name;
  }

  public java.lang.String getLabel() {
    return this.label;
  }

  public void setLabel(java.lang.String label) {
    this.label = label;
  }

  public java.lang.String getNote() {
    return this.note;
  }

  public void setNote(java.lang.String note) {
    this.note = note;
  }

  public boolean isRequired() {
    return this.required;
  }

  public boolean getRequired() {
    return this.required;
  }

  public void setRequired(boolean required) {
    this.required = required;
  }

  public boolean isReloadOnChange() {
    return this.reloadOnChange;
  }

  public boolean getReloadOnChange() {
    return this.reloadOnChange;
  }

  public void setReloadOnChange(boolean reloadOnChange) {
    this.reloadOnChange = reloadOnChange;
  }

  public java.lang.String getDefault() {
    return this._default;
  }

  public void setDefault(java.lang.String _default) {
    this._default = _default;
  }

  public boolean isDefaultSelected() {
    return this.defaultSelected;
  }

  public boolean getDefaultSelected() {
    return this.defaultSelected;
  }

  public void setDefaultSelected(boolean defaultSelected) {
    this.defaultSelected = defaultSelected;
  }

  public java.lang.String getValue() {
    return this.value;
  }

  public void setValue(java.lang.String value) {
    this.value = value;
  }

  public java.lang.Integer getMin() {
    return this.min;
  }

  public void setMin(java.lang.Integer min) {
    this.min = min;
  }

  public java.lang.Integer getMax() {
    return this.max;
  }

  public void setMax(java.lang.Integer max) {
    this.max = max;
  }

  public java.lang.String getValidation() {
    return this.validation;
  }

  public void setValidation(java.lang.String validation) {
    this.validation = validation;
  }

  private static final long serialVersionUID = 1L;



  @XmlElement(name="conditions",required=false,nillable=false)
  protected Conditions conditions;

  @XmlElement(name="values",required=false,nillable=false)
  protected ItemValues values;

  @XmlElement(name="property",required=true,nillable=false)
  protected Property property;

  @javax.xml.bind.annotation.XmlTransient
  protected java.lang.String _value_type;

  @XmlAttribute(name="type",required=true)
  protected ItemType type;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="name",required=true)
  protected java.lang.String name;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="label",required=false)
  protected java.lang.String label;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="note",required=false)
  protected java.lang.String note;

  @javax.xml.bind.annotation.XmlSchemaType(name="boolean")
  @XmlAttribute(name="required",required=false)
  protected boolean required = false;

  @javax.xml.bind.annotation.XmlSchemaType(name="boolean")
  @XmlAttribute(name="reloadOnChange",required=false)
  protected boolean reloadOnChange = false;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="default",required=false)
  protected java.lang.String _default;

  @javax.xml.bind.annotation.XmlSchemaType(name="boolean")
  @XmlAttribute(name="defaultSelected",required=false)
  protected boolean defaultSelected;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="value",required=false)
  protected java.lang.String value;

  @javax.xml.bind.annotation.XmlSchemaType(name="integer")
  @XmlAttribute(name="min",required=false)
  protected java.lang.Integer min;

  @javax.xml.bind.annotation.XmlSchemaType(name="integer")
  @XmlAttribute(name="max",required=false)
  protected java.lang.Integer max;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="validation",required=false)
  protected java.lang.String validation;

}
