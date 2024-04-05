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
package eu.domibus.configuration;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import java.io.Serializable;


/** <p>Java class for property complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="property"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="value" type="{http://www.domibus.eu/configuration}PropertyValue" minOccurs="0" maxOccurs="1"/&gt;
 * 		&lt;/sequence&gt;
 * 		&lt;attribute name="name" type="{http://www.domibus.eu/configuration}string" use="required"/&gt;
 * 		&lt;attribute name="key" type="{http://www.domibus.eu/configuration}string" use="required"/&gt;
 * 		&lt;attribute name="datatype" type="{http://www.domibus.eu/configuration}string" use="required"/&gt;
 * 		&lt;attribute name="required" type="{http://www.w3.org/2001/XMLSchema}boolean" use="required"/&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "property", 
  propOrder = {
  	"value"
  }
)

@XmlRootElement(name = "property")

public class Property extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public Property() {
    super();
  }

  public PropertyValue getValue() {
    return this.value;
  }

  public void setValue(PropertyValue value) {
    this.value = value;
  }

  public java.lang.String getName() {
    return this.name;
  }

  public void setName(java.lang.String name) {
    this.name = name;
  }

  public java.lang.String getKey() {
    return this.key;
  }

  public void setKey(java.lang.String key) {
    this.key = key;
  }

  public java.lang.String getDatatype() {
    return this.datatype;
  }

  public void setDatatype(java.lang.String datatype) {
    this.datatype = datatype;
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

  private static final long serialVersionUID = 1L;



  @XmlElement(name="value",required=false,nillable=false)
  protected PropertyValue value;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="name",required=true)
  protected java.lang.String name;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="key",required=true)
  protected java.lang.String key;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="datatype",required=true)
  protected java.lang.String datatype;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="boolean")
  @XmlAttribute(name="required",required=true)
  protected boolean required;

}
