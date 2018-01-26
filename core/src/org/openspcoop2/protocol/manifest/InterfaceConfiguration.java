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
package org.openspcoop2.protocol.manifest;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.openspcoop2.protocol.manifest.constants.InterfaceType;
import java.io.Serializable;


/** <p>Java class for InterfaceConfiguration complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="InterfaceConfiguration">
 * 		&lt;attribute name="type" type="{http://www.openspcoop2.org/protocol/manifest}InterfaceType" use="required"/>
 * 		&lt;attribute name="schema" type="{http://www.w3.org/2001/XMLSchema}boolean" use="optional" default="false"/>
 * 		&lt;attribute name="conversations" type="{http://www.w3.org/2001/XMLSchema}boolean" use="optional" default="false"/>
 * &lt;/complexType>
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
  }

  public void set_value_type(String value) {
    this.type = (InterfaceType) InterfaceType.toEnumConstantFromString(value);
  }

  public String get_value_type() {
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

  private static final long serialVersionUID = 1L;



  @javax.xml.bind.annotation.XmlTransient
  protected java.lang.String _value_type;

  @XmlAttribute(name="type",required=true)
  protected InterfaceType type;

  @javax.xml.bind.annotation.XmlSchemaType(name="boolean")
  @XmlAttribute(name="schema",required=false)
  protected boolean schema = false;

  @javax.xml.bind.annotation.XmlSchemaType(name="boolean")
  @XmlAttribute(name="conversations",required=false)
  protected boolean conversations = false;

}
