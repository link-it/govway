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
package org.openspcoop2.protocol.manifest;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.openspcoop2.protocol.manifest.constants.ActorType;
import java.io.Serializable;


/** <p>Java class for IntegrationConfigurationElementName complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="IntegrationConfigurationElementName"&gt;
 * 		&lt;attribute name="prefix" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/&gt;
 * 		&lt;attribute name="actor" type="{http://www.openspcoop2.org/protocol/manifest}ActorType" use="optional"/&gt;
 * 		&lt;attribute name="suffix" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "IntegrationConfigurationElementName")

@XmlRootElement(name = "IntegrationConfigurationElementName")

public class IntegrationConfigurationElementName extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public IntegrationConfigurationElementName() {
    super();
  }

  public java.lang.String getPrefix() {
    return this.prefix;
  }

  public void setPrefix(java.lang.String prefix) {
    this.prefix = prefix;
  }

  public void set_value_actor(String value) {
    this.actor = (ActorType) ActorType.toEnumConstantFromString(value);
  }

  public String get_value_actor() {
    if(this.actor == null){
    	return null;
    }else{
    	return this.actor.toString();
    }
  }

  public org.openspcoop2.protocol.manifest.constants.ActorType getActor() {
    return this.actor;
  }

  public void setActor(org.openspcoop2.protocol.manifest.constants.ActorType actor) {
    this.actor = actor;
  }

  public java.lang.String getSuffix() {
    return this.suffix;
  }

  public void setSuffix(java.lang.String suffix) {
    this.suffix = suffix;
  }

  private static final long serialVersionUID = 1L;



  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="prefix",required=false)
  protected java.lang.String prefix;

  @javax.xml.bind.annotation.XmlTransient
  protected java.lang.String _value_actor;

  @XmlAttribute(name="actor",required=false)
  protected ActorType actor;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="suffix",required=false)
  protected java.lang.String suffix;

}
