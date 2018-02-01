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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;


/** <p>Java class for Service complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Service">
 * 		&lt;sequence>
 * 			&lt;element name="types" type="{http://www.openspcoop2.org/protocol/manifest}ServiceTypes" minOccurs="1" maxOccurs="1"/>
 * 		&lt;/sequence>
 * 		&lt;attribute name="apiReferent" type="{http://www.w3.org/2001/XMLSchema}boolean" use="optional" default="false"/>
 * 		&lt;attribute name="version" type="{http://www.w3.org/2001/XMLSchema}boolean" use="optional" default="true"/>
 * &lt;/complexType>
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Service", 
  propOrder = {
  	"types"
  }
)

@XmlRootElement(name = "Service")

public class Service extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public Service() {
  }

  public ServiceTypes getTypes() {
    return this.types;
  }

  public void setTypes(ServiceTypes types) {
    this.types = types;
  }

  public boolean isApiReferent() {
    return this.apiReferent;
  }

  public boolean getApiReferent() {
    return this.apiReferent;
  }

  public void setApiReferent(boolean apiReferent) {
    this.apiReferent = apiReferent;
  }

  public boolean isVersion() {
    return this.version;
  }

  public boolean getVersion() {
    return this.version;
  }

  public void setVersion(boolean version) {
    this.version = version;
  }

  private static final long serialVersionUID = 1L;



  @XmlElement(name="types",required=true,nillable=false)
  protected ServiceTypes types;

  @javax.xml.bind.annotation.XmlSchemaType(name="boolean")
  @XmlAttribute(name="apiReferent",required=false)
  protected boolean apiReferent = false;

  @javax.xml.bind.annotation.XmlSchemaType(name="boolean")
  @XmlAttribute(name="version",required=false)
  protected boolean version = true;

}
