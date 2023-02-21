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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;


/** <p>Java class for registry complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="registry"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="organization" type="{http://www.openspcoop2.org/protocol/manifest}Organization" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="service" type="{http://www.openspcoop2.org/protocol/manifest}Service" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="versions" type="{http://www.openspcoop2.org/protocol/manifest}Versions" minOccurs="1" maxOccurs="1"/&gt;
 * 		&lt;/sequence&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "registry", 
  propOrder = {
  	"organization",
  	"service",
  	"versions"
  }
)

@XmlRootElement(name = "registry")

public class Registry extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public Registry() {
    super();
  }

  public Organization getOrganization() {
    return this.organization;
  }

  public void setOrganization(Organization organization) {
    this.organization = organization;
  }

  public Service getService() {
    return this.service;
  }

  public void setService(Service service) {
    this.service = service;
  }

  public Versions getVersions() {
    return this.versions;
  }

  public void setVersions(Versions versions) {
    this.versions = versions;
  }

  private static final long serialVersionUID = 1L;



  @XmlElement(name="organization",required=true,nillable=false)
  protected Organization organization;

  @XmlElement(name="service",required=true,nillable=false)
  protected Service service;

  @XmlElement(name="versions",required=true,nillable=false)
  protected Versions versions;

}
