/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it).
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
package org.openspcoop2.core.eccezione.router_details;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import java.io.Serializable;


/** <p>Java class for dominio complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="dominio"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="id" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="organization" type="{http://govway.org/integration/fault/router_details}dominio-soggetto" minOccurs="1" maxOccurs="1"/&gt;
 * 		&lt;/sequence&gt;
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
    super();
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

  public java.lang.String getModule() {
    return this.module;
  }

  public void setModule(java.lang.String module) {
    this.module = module;
  }

  private static final long serialVersionUID = 1L;



  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="id",required=true,nillable=false)
  protected java.lang.String id;

  @XmlElement(name="organization",required=true,nillable=false)
  protected DominioSoggetto organization;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="module",required=false)
  protected java.lang.String module;

}
