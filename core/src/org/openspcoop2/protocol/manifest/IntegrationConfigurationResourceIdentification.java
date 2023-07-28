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


/** <p>Java class for IntegrationConfigurationResourceIdentification complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="IntegrationConfigurationResourceIdentification"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="identificationModes" type="{http://www.openspcoop2.org/protocol/manifest}IntegrationConfigurationResourceIdentificationModes" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="identificationParameter" type="{http://www.openspcoop2.org/protocol/manifest}IntegrationConfigurationName" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="specificResource" type="{http://www.openspcoop2.org/protocol/manifest}IntegrationConfigurationResourceIdentificationSpecificResource" minOccurs="1" maxOccurs="1"/&gt;
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
@XmlType(name = "IntegrationConfigurationResourceIdentification", 
  propOrder = {
  	"identificationModes",
  	"identificationParameter",
  	"specificResource"
  }
)

@XmlRootElement(name = "IntegrationConfigurationResourceIdentification")

public class IntegrationConfigurationResourceIdentification extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public IntegrationConfigurationResourceIdentification() {
    super();
  }

  public IntegrationConfigurationResourceIdentificationModes getIdentificationModes() {
    return this.identificationModes;
  }

  public void setIdentificationModes(IntegrationConfigurationResourceIdentificationModes identificationModes) {
    this.identificationModes = identificationModes;
  }

  public IntegrationConfigurationName getIdentificationParameter() {
    return this.identificationParameter;
  }

  public void setIdentificationParameter(IntegrationConfigurationName identificationParameter) {
    this.identificationParameter = identificationParameter;
  }

  public IntegrationConfigurationResourceIdentificationSpecificResource getSpecificResource() {
    return this.specificResource;
  }

  public void setSpecificResource(IntegrationConfigurationResourceIdentificationSpecificResource specificResource) {
    this.specificResource = specificResource;
  }

  private static final long serialVersionUID = 1L;



  @XmlElement(name="identificationModes",required=true,nillable=false)
  protected IntegrationConfigurationResourceIdentificationModes identificationModes;

  @XmlElement(name="identificationParameter",required=false,nillable=false)
  protected IntegrationConfigurationName identificationParameter;

  @XmlElement(name="specificResource",required=true,nillable=false)
  protected IntegrationConfigurationResourceIdentificationSpecificResource specificResource;

}
