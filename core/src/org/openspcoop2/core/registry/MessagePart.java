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
package org.openspcoop2.core.registry;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;


/** <p>Java class for message-part complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="message-part">
 * 		&lt;attribute name="name" type="{http://www.w3.org/2001/XMLSchema}string" use="required"/>
 * 		&lt;attribute name="element-name" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/>
 * 		&lt;attribute name="element-namespace" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/>
 * 		&lt;attribute name="type-name" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/>
 * 		&lt;attribute name="type-namespace" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/>
 * &lt;/complexType>
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "message-part")

@XmlRootElement(name = "message-part")

public class MessagePart extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public MessagePart() {
  }

  public Long getId() {
    if(this.id!=null)
		return this.id;
	else
		return new Long(-1);
  }

  public void setId(Long id) {
    if(id!=null)
		this.id=id;
	else
		this.id=new Long(-1);
  }

  public java.lang.String getName() {
    return this.name;
  }

  public void setName(java.lang.String name) {
    this.name = name;
  }

  public java.lang.String getElementName() {
    return this.elementName;
  }

  public void setElementName(java.lang.String elementName) {
    this.elementName = elementName;
  }

  public java.lang.String getElementNamespace() {
    return this.elementNamespace;
  }

  public void setElementNamespace(java.lang.String elementNamespace) {
    this.elementNamespace = elementNamespace;
  }

  public java.lang.String getTypeName() {
    return this.typeName;
  }

  public void setTypeName(java.lang.String typeName) {
    this.typeName = typeName;
  }

  public java.lang.String getTypeNamespace() {
    return this.typeNamespace;
  }

  public void setTypeNamespace(java.lang.String typeNamespace) {
    this.typeNamespace = typeNamespace;
  }

  private static final long serialVersionUID = 1L;

  @XmlTransient
  private Long id;



  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="name",required=true)
  protected java.lang.String name;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="element-name",required=false)
  protected java.lang.String elementName;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="element-namespace",required=false)
  protected java.lang.String elementNamespace;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="type-name",required=false)
  protected java.lang.String typeName;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="type-namespace",required=false)
  protected java.lang.String typeNamespace;

}
