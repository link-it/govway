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

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import java.io.Serializable;


/** <p>Java class for IntegrationConfiguration complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="IntegrationConfiguration"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="name" type="{http://www.openspcoop2.org/protocol/manifest}IntegrationConfigurationName" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="resourceIdentification" type="{http://www.openspcoop2.org/protocol/manifest}IntegrationConfigurationResourceIdentification" minOccurs="1" maxOccurs="1"/&gt;
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
@XmlType(name = "IntegrationConfiguration", 
  propOrder = {
  	"name",
  	"resourceIdentification"
  }
)

@XmlRootElement(name = "IntegrationConfiguration")

public class IntegrationConfiguration extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public IntegrationConfiguration() {
    super();
  }

  public IntegrationConfigurationName getName() {
    return this.name;
  }

  public void setName(IntegrationConfigurationName name) {
    this.name = name;
  }

  public IntegrationConfigurationResourceIdentification getResourceIdentification() {
    return this.resourceIdentification;
  }

  public void setResourceIdentification(IntegrationConfigurationResourceIdentification resourceIdentification) {
    this.resourceIdentification = resourceIdentification;
  }

  private static final long serialVersionUID = 1L;



  @XmlElement(name="name",required=true,nillable=false)
  protected IntegrationConfigurationName name;

  @XmlElement(name="resourceIdentification",required=true,nillable=false)
  protected IntegrationConfigurationResourceIdentification resourceIdentification;

}
