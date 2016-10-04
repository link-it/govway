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
package org.openspcoop2.core.config;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import org.openspcoop2.core.config.constants.CredenzialeTipo;
import java.io.Serializable;


/** <p>Java class for credenziali complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="credenziali">
 * 		&lt;attribute name="tipo" type="{http://www.openspcoop2.org/core/config}CredenzialeTipo" use="optional" default="ssl"/>
 * 		&lt;attribute name="user" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/>
 * 		&lt;attribute name="password" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/>
 * 		&lt;attribute name="subject" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/>
 * &lt;/complexType>
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "credenziali")

@XmlRootElement(name = "credenziali")

public class Credenziali extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public Credenziali() {
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

  public void set_value_tipo(String value) {
    this.tipo = (CredenzialeTipo) CredenzialeTipo.toEnumConstantFromString(value);
  }

  public String get_value_tipo() {
    if(this.tipo == null){
    	return null;
    }else{
    	return this.tipo.toString();
    }
  }

  public org.openspcoop2.core.config.constants.CredenzialeTipo getTipo() {
    return this.tipo;
  }

  public void setTipo(org.openspcoop2.core.config.constants.CredenzialeTipo tipo) {
    this.tipo = tipo;
  }

  public java.lang.String getUser() {
    return this.user;
  }

  public void setUser(java.lang.String user) {
    this.user = user;
  }

  public java.lang.String getPassword() {
    return this.password;
  }

  public void setPassword(java.lang.String password) {
    this.password = password;
  }

  public java.lang.String getSubject() {
    return this.subject;
  }

  public void setSubject(java.lang.String subject) {
    this.subject = subject;
  }

  private static final long serialVersionUID = 1L;

  @XmlTransient
  private Long id;



  @XmlTransient
  protected java.lang.String _value_tipo;

  @XmlAttribute(name="tipo",required=false)
  protected CredenzialeTipo tipo = (CredenzialeTipo) CredenzialeTipo.toEnumConstantFromString("ssl");

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="user",required=false)
  protected java.lang.String user;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="password",required=false)
  protected java.lang.String password;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="subject",required=false)
  protected java.lang.String subject;

}
