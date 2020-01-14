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
package org.openspcoop2.protocol.manifest;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.openspcoop2.protocol.manifest.constants.ResourceIdentificationType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Java class for IntegrationConfigurationResourceIdentificationModes complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="IntegrationConfigurationResourceIdentificationModes">
 * 		&lt;sequence>
 * 			&lt;element name="mode" type="{http://www.openspcoop2.org/protocol/manifest}IntegrationConfigurationResourceIdentificationMode" minOccurs="1" maxOccurs="unbounded"/>
 * 		&lt;/sequence>
 * 		&lt;attribute name="default" type="{http://www.openspcoop2.org/protocol/manifest}ResourceIdentificationType" use="optional"/>
 * 		&lt;attribute name="forceInterfaceMode" type="{http://www.w3.org/2001/XMLSchema}boolean" use="required"/>
 * &lt;/complexType>
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "IntegrationConfigurationResourceIdentificationModes", 
  propOrder = {
  	"mode"
  }
)

@XmlRootElement(name = "IntegrationConfigurationResourceIdentificationModes")

public class IntegrationConfigurationResourceIdentificationModes extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public IntegrationConfigurationResourceIdentificationModes() {
  }

  public void addMode(IntegrationConfigurationResourceIdentificationMode mode) {
    this.mode.add(mode);
  }

  public IntegrationConfigurationResourceIdentificationMode getMode(int index) {
    return this.mode.get( index );
  }

  public IntegrationConfigurationResourceIdentificationMode removeMode(int index) {
    return this.mode.remove( index );
  }

  public List<IntegrationConfigurationResourceIdentificationMode> getModeList() {
    return this.mode;
  }

  public void setModeList(List<IntegrationConfigurationResourceIdentificationMode> mode) {
    this.mode=mode;
  }

  public int sizeModeList() {
    return this.mode.size();
  }

  public void set_value__default(String value) {
    this._default = (ResourceIdentificationType) ResourceIdentificationType.toEnumConstantFromString(value);
  }

  public String get_value__default() {
    if(this._default == null){
    	return null;
    }else{
    	return this._default.toString();
    }
  }

  public org.openspcoop2.protocol.manifest.constants.ResourceIdentificationType getDefault() {
    return this._default;
  }

  public void setDefault(org.openspcoop2.protocol.manifest.constants.ResourceIdentificationType _default) {
    this._default = _default;
  }

  public boolean isForceInterfaceMode() {
    return this.forceInterfaceMode;
  }

  public boolean getForceInterfaceMode() {
    return this.forceInterfaceMode;
  }

  public void setForceInterfaceMode(boolean forceInterfaceMode) {
    this.forceInterfaceMode = forceInterfaceMode;
  }

  private static final long serialVersionUID = 1L;



  @XmlElement(name="mode",required=true,nillable=false)
  protected List<IntegrationConfigurationResourceIdentificationMode> mode = new ArrayList<IntegrationConfigurationResourceIdentificationMode>();

  /**
   * @deprecated Use method getModeList
   * @return List<IntegrationConfigurationResourceIdentificationMode>
  */
  @Deprecated
  public List<IntegrationConfigurationResourceIdentificationMode> getMode() {
  	return this.mode;
  }

  /**
   * @deprecated Use method setModeList
   * @param mode List<IntegrationConfigurationResourceIdentificationMode>
  */
  @Deprecated
  public void setMode(List<IntegrationConfigurationResourceIdentificationMode> mode) {
  	this.mode=mode;
  }

  /**
   * @deprecated Use method sizeModeList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizeMode() {
  	return this.mode.size();
  }

  @javax.xml.bind.annotation.XmlTransient
  protected java.lang.String _value__default;

  @XmlAttribute(name="default",required=false)
  protected ResourceIdentificationType _default;

  @javax.xml.bind.annotation.XmlSchemaType(name="boolean")
  @XmlAttribute(name="forceInterfaceMode",required=true)
  protected boolean forceInterfaceMode;

}
