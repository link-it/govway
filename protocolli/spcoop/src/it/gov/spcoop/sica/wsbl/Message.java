/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it).
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
package it.gov.spcoop.sica.wsbl;

import it.gov.spcoop.sica.wsbl.constants.MessageOrientationType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;


/** <p>Java class for message complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="message"&gt;
 * 		&lt;attribute name="name" type="{http://spcoop.gov.it/sica/wsbl}string" use="required"/&gt;
 * 		&lt;attribute name="type" type="{http://spcoop.gov.it/sica/wsbl}messageOrientationType" use="required"/&gt;
 * 		&lt;attribute name="source" type="{http://spcoop.gov.it/sica/wsbl}string" use="required"/&gt;
 * 		&lt;attribute name="target" type="{http://spcoop.gov.it/sica/wsbl}string" use="required"/&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "message")

@XmlRootElement(name = "message")

public class Message extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public Message() {
  }

  public java.lang.String getName() {
    return this.name;
  }

  public void setName(java.lang.String name) {
    this.name = name;
  }

  public void set_value_type(String value) {
    this.type = (MessageOrientationType) MessageOrientationType.toEnumConstantFromString(value);
  }

  public String get_value_type() {
    if(this.type == null){
    	return null;
    }else{
    	return this.type.toString();
    }
  }

  public it.gov.spcoop.sica.wsbl.constants.MessageOrientationType getType() {
    return this.type;
  }

  public void setType(it.gov.spcoop.sica.wsbl.constants.MessageOrientationType type) {
    this.type = type;
  }

  public java.lang.String getSource() {
    return this.source;
  }

  public void setSource(java.lang.String source) {
    this.source = source;
  }

  public java.lang.String getTarget() {
    return this.target;
  }

  public void setTarget(java.lang.String target) {
    this.target = target;
  }

  private static final long serialVersionUID = 1L;



  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="name",required=true)
  protected java.lang.String name;

  @javax.xml.bind.annotation.XmlTransient
  protected java.lang.String _value_type;

  @XmlAttribute(name="type",required=true)
  protected MessageOrientationType type;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="source",required=true)
  protected java.lang.String source;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="target",required=true)
  protected java.lang.String target;

}
