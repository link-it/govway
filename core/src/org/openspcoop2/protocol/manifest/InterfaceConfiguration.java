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
package org.openspcoop2.protocol.manifest;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import org.openspcoop2.protocol.manifest.constants.InterfaceType;
import java.io.Serializable;


/** <p>Java class for InterfaceConfiguration complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="InterfaceConfiguration"&gt;
 * 		&lt;attribute name="type" type="{http://www.openspcoop2.org/protocol/manifest}InterfaceType" use="required"/&gt;
 * 		&lt;attribute name="schema" type="{http://www.w3.org/2001/XMLSchema}boolean" use="optional" default="false"/&gt;
 * 		&lt;attribute name="conversations" type="{http://www.w3.org/2001/XMLSchema}boolean" use="optional" default="false"/&gt;
 * 		&lt;attribute name="implementation" type="{http://www.w3.org/2001/XMLSchema}boolean" use="optional" default="false"/&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "InterfaceConfiguration")

@XmlRootElement(name = "InterfaceConfiguration")

public class InterfaceConfiguration extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public InterfaceConfiguration() {
    super();
  }

  public void setTypeRawEnumValue(String value) {
    this.type = (InterfaceType) InterfaceType.toEnumConstantFromString(value);
  }

  public String getTypeRawEnumValue() {
    if(this.type == null){
    	return null;
    }else{
    	return this.type.toString();
    }
  }

  public org.openspcoop2.protocol.manifest.constants.InterfaceType getType() {
    return this.type;
  }

  public void setType(org.openspcoop2.protocol.manifest.constants.InterfaceType type) {
    this.type = type;
  }

  public boolean isSchema() {
    return this.schema;
  }

  public boolean getSchema() {
    return this.schema;
  }

  public void setSchema(boolean schema) {
    this.schema = schema;
  }

  public boolean isConversations() {
    return this.conversations;
  }

  public boolean getConversations() {
    return this.conversations;
  }

  public void setConversations(boolean conversations) {
    this.conversations = conversations;
  }

  public boolean isImplementation() {
    return this.implementation;
  }

  public boolean getImplementation() {
    return this.implementation;
  }

  public void setImplementation(boolean implementation) {
    this.implementation = implementation;
  }

  private static final long serialVersionUID = 1L;



  @jakarta.xml.bind.annotation.XmlTransient
  protected java.lang.String typeRawEnumValue;

  @XmlAttribute(name="type",required=true)
  protected InterfaceType type;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="boolean")
  @XmlAttribute(name="schema",required=false)
  protected boolean schema = false;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="boolean")
  @XmlAttribute(name="conversations",required=false)
  protected boolean conversations = false;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="boolean")
  @XmlAttribute(name="implementation",required=false)
  protected boolean implementation = false;

}
