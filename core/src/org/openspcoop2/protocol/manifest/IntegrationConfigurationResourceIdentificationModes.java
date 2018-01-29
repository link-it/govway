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
import org.openspcoop2.protocol.manifest.constants.ResourceIdentificationType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/** <p>Java class for IntegrationConfigurationResourceIdentificationModes complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="IntegrationConfigurationResourceIdentificationModes">
 * 		&lt;sequence>
 * 			&lt;element name="mode" type="{http://www.openspcoop2.org/protocol/manifest}ResourceIdentificationType" minOccurs="1" maxOccurs="unbounded"/>
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

  public void set_value_mode(List<String> list) {
    if(list!=null){
    	this.mode = new ArrayList<ResourceIdentificationType>();
    	for (Iterator<String> iterator = list.iterator(); iterator.hasNext();) {
    		String value = iterator.next();
    		this.mode.add((ResourceIdentificationType) ResourceIdentificationType.toEnumConstantFromString(value));
    	}
    }
  }

  public List<String> get_value_mode() {
    if(this.mode==null){
    	return null;
    }else{
    	List<String> list = new ArrayList<java.lang.String>();
    	for (Iterator<ResourceIdentificationType> iterator = this.mode.iterator(); iterator.hasNext();) {
    		ResourceIdentificationType enumObject = iterator.next();
    		list.add(enumObject.toString());
    	}
    	return list;
    }
  }

  public void addMode(ResourceIdentificationType mode) {
    this.mode.add(mode);
  }

  public ResourceIdentificationType getMode(int index) {
    return this.mode.get( index );
  }

  public ResourceIdentificationType removeMode(int index) {
    return this.mode.remove( index );
  }

  public List<ResourceIdentificationType> getModeList() {
    return this.mode;
  }

  public void setModeList(List<ResourceIdentificationType> mode) {
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



  @javax.xml.bind.annotation.XmlTransient
  protected List<java.lang.String> _value_mode;

  @XmlElement(name="mode",required=true,nillable=false)
  protected List<ResourceIdentificationType> mode = new ArrayList<ResourceIdentificationType>();

  /**
   * @deprecated Use method getModeList
   * @return List<ResourceIdentificationType>
  */
  @Deprecated
  public List<ResourceIdentificationType> getMode() {
  	return this.mode;
  }

  /**
   * @deprecated Use method setModeList
   * @param mode List<ResourceIdentificationType>
  */
  @Deprecated
  public void setMode(List<ResourceIdentificationType> mode) {
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
