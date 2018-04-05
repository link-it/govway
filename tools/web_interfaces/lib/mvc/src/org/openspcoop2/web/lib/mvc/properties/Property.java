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
package org.openspcoop2.web.lib.mvc.properties;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;


/** <p>Java class for property complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="property">
 * 		&lt;attribute name="name" type="{http://www.w3.org/2001/XMLSchema}string" use="required"/>
 * 		&lt;attribute name="selectedValue" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/>
 * 		&lt;attribute name="unselectedValue" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/>
 * 		&lt;attribute name="append" type="{http://www.w3.org/2001/XMLSchema}boolean" use="optional" default="false"/>
 * 		&lt;attribute name="appendSeparator" type="{http://www.w3.org/2001/XMLSchema}string" use="optional" default=" "/>
 * 		&lt;attribute name="properties" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/>
 * &lt;/complexType>
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "property")

@XmlRootElement(name = "property")

public class Property extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public Property() {
  }

  public java.lang.String getName() {
    return this.name;
  }

  public void setName(java.lang.String name) {
    this.name = name;
  }

  public java.lang.String getSelectedValue() {
    return this.selectedValue;
  }

  public void setSelectedValue(java.lang.String selectedValue) {
    this.selectedValue = selectedValue;
  }

  public java.lang.String getUnselectedValue() {
    return this.unselectedValue;
  }

  public void setUnselectedValue(java.lang.String unselectedValue) {
    this.unselectedValue = unselectedValue;
  }

  public boolean isAppend() {
    return this.append;
  }

  public boolean getAppend() {
    return this.append;
  }

  public void setAppend(boolean append) {
    this.append = append;
  }

  public java.lang.String getAppendSeparator() {
    return this.appendSeparator;
  }

  public void setAppendSeparator(java.lang.String appendSeparator) {
    this.appendSeparator = appendSeparator;
  }

  public java.lang.String getProperties() {
    return this.properties;
  }

  public void setProperties(java.lang.String properties) {
    this.properties = properties;
  }

  private static final long serialVersionUID = 1L;



  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="name",required=true)
  protected java.lang.String name;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="selectedValue",required=false)
  protected java.lang.String selectedValue;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="unselectedValue",required=false)
  protected java.lang.String unselectedValue;

  @javax.xml.bind.annotation.XmlSchemaType(name="boolean")
  @XmlAttribute(name="append",required=false)
  protected boolean append = false;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="appendSeparator",required=false)
  protected java.lang.String appendSeparator = " ";

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="properties",required=false)
  protected java.lang.String properties;

}
