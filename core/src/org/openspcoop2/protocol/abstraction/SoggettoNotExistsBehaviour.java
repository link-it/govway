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
package org.openspcoop2.protocol.abstraction;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;


/** <p>Java class for SoggettoNotExistsBehaviour complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SoggettoNotExistsBehaviour"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="endpoint" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="porta-dominio" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/&gt;
 * 		&lt;/sequence&gt;
 * 		&lt;attribute name="create" type="{http://www.w3.org/2001/XMLSchema}boolean" use="required"/&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SoggettoNotExistsBehaviour", 
  propOrder = {
  	"endpoint",
  	"portaDominio"
  }
)

@XmlRootElement(name = "SoggettoNotExistsBehaviour")

public class SoggettoNotExistsBehaviour extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public SoggettoNotExistsBehaviour() {
  }

  public java.lang.String getEndpoint() {
    return this.endpoint;
  }

  public void setEndpoint(java.lang.String endpoint) {
    this.endpoint = endpoint;
  }

  public java.lang.String getPortaDominio() {
    return this.portaDominio;
  }

  public void setPortaDominio(java.lang.String portaDominio) {
    this.portaDominio = portaDominio;
  }

  public boolean isCreate() {
    return this.create;
  }

  public boolean getCreate() {
    return this.create;
  }

  public void setCreate(boolean create) {
    this.create = create;
  }

  private static final long serialVersionUID = 1L;



  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="endpoint",required=false,nillable=false)
  protected java.lang.String endpoint;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="porta-dominio",required=false,nillable=false)
  protected java.lang.String portaDominio;

  @javax.xml.bind.annotation.XmlSchemaType(name="boolean")
  @XmlAttribute(name="create",required=true)
  protected boolean create;

}
