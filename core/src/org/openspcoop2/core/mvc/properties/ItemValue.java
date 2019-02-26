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
package org.openspcoop2.core.mvc.properties;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;


/** <p>Java class for itemValue complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="itemValue">
 * 		&lt;attribute name="value" type="{http://www.w3.org/2001/XMLSchema}string" use="required"/>
 * 		&lt;attribute name="label" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/>
 * &lt;/complexType>
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "itemValue")

@XmlRootElement(name = "itemValue")

public class ItemValue extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public ItemValue() {
  }

  public java.lang.String getValue() {
    return this.value;
  }

  public void setValue(java.lang.String value) {
    this.value = value;
  }

  public java.lang.String getLabel() {
    return this.label;
  }

  public void setLabel(java.lang.String label) {
    this.label = label;
  }

  private static final long serialVersionUID = 1L;



  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="value",required=true)
  protected java.lang.String value;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="label",required=false)
  protected java.lang.String label;

}
