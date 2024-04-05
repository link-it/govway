/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it).
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
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import org.openspcoop2.protocol.manifest.constants.ResourceIdentificationType;
import java.io.Serializable;


/** <p>Java class for IntegrationConfigurationResourceIdentificationMode complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="IntegrationConfigurationResourceIdentificationMode"&gt;
 * 		&lt;attribute name="name" type="{http://www.openspcoop2.org/protocol/manifest}ResourceIdentificationType" use="required"/&gt;
 * 		&lt;attribute name="onlyAdvancedMode" type="{http://www.w3.org/2001/XMLSchema}boolean" use="optional" default="false"/&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "IntegrationConfigurationResourceIdentificationMode")

@XmlRootElement(name = "IntegrationConfigurationResourceIdentificationMode")

public class IntegrationConfigurationResourceIdentificationMode extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public IntegrationConfigurationResourceIdentificationMode() {
    super();
  }

  public void setNameRawEnumValue(String value) {
    this.name = (ResourceIdentificationType) ResourceIdentificationType.toEnumConstantFromString(value);
  }

  public String getNameRawEnumValue() {
    if(this.name == null){
    	return null;
    }else{
    	return this.name.toString();
    }
  }

  public org.openspcoop2.protocol.manifest.constants.ResourceIdentificationType getName() {
    return this.name;
  }

  public void setName(org.openspcoop2.protocol.manifest.constants.ResourceIdentificationType name) {
    this.name = name;
  }

  public boolean isOnlyAdvancedMode() {
    return this.onlyAdvancedMode;
  }

  public boolean getOnlyAdvancedMode() {
    return this.onlyAdvancedMode;
  }

  public void setOnlyAdvancedMode(boolean onlyAdvancedMode) {
    this.onlyAdvancedMode = onlyAdvancedMode;
  }

  private static final long serialVersionUID = 1L;



  @jakarta.xml.bind.annotation.XmlTransient
  protected java.lang.String nameRawEnumValue;

  @XmlAttribute(name="name",required=true)
  protected ResourceIdentificationType name;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="boolean")
  @XmlAttribute(name="onlyAdvancedMode",required=false)
  protected boolean onlyAdvancedMode = false;

}
