/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it).
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
package org.openspcoop2.core.eccezione.details;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.openspcoop2.core.eccezione.details.constants.TipoPdD;
import java.io.Serializable;


/** <p>Java class for dominio complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="dominio"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="id" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="organization" type="{http://govway.org/integration/fault/details}dominio-soggetto" minOccurs="1" maxOccurs="1"/&gt;
 * 		&lt;/sequence&gt;
 * 		&lt;attribute name="role" type="{http://govway.org/integration/fault/details}TipoPdD" use="optional"/&gt;
 * 		&lt;attribute name="module" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "dominio", 
  propOrder = {
  	"id",
  	"organization"
  }
)

@XmlRootElement(name = "dominio")

public class Dominio extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public Dominio() {
  }

  public java.lang.String getId() {
    return this.id;
  }

  public void setId(java.lang.String id) {
    this.id = id;
  }

  public DominioSoggetto getOrganization() {
    return this.organization;
  }

  public void setOrganization(DominioSoggetto organization) {
    this.organization = organization;
  }

  public void set_value_role(String value) {
    this.role = (TipoPdD) TipoPdD.toEnumConstantFromString(value);
  }

  public String get_value_role() {
    if(this.role == null){
    	return null;
    }else{
    	return this.role.toString();
    }
  }

  public org.openspcoop2.core.eccezione.details.constants.TipoPdD getRole() {
    return this.role;
  }

  public void setRole(org.openspcoop2.core.eccezione.details.constants.TipoPdD role) {
    this.role = role;
  }

  public java.lang.String getModule() {
    return this.module;
  }

  public void setModule(java.lang.String module) {
    this.module = module;
  }

  private static final long serialVersionUID = 1L;



  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="id",required=true,nillable=false)
  protected java.lang.String id;

  @XmlElement(name="organization",required=true,nillable=false)
  protected DominioSoggetto organization;

  @javax.xml.bind.annotation.XmlTransient
  protected java.lang.String _value_role;

  @XmlAttribute(name="role",required=false)
  protected TipoPdD role;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="module",required=false)
  protected java.lang.String module;

}
