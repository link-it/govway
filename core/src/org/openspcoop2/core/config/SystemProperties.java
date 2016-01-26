/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2016 Link.it srl (http://link.it).
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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
package org.openspcoop2.core.config;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Java class for system-properties complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="system-properties">
 * 		&lt;sequence>
 * 			&lt;element name="system-property" type="{http://www.openspcoop2.org/core/config}Property" minOccurs="1" maxOccurs="unbounded"/>
 * 		&lt;/sequence>
 * &lt;/complexType>
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "system-properties", 
  propOrder = {
  	"systemProperty"
  }
)

@XmlRootElement(name = "system-properties")

public class SystemProperties extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public SystemProperties() {
  }

  public Long getId() {
    if(this.id!=null)
		return this.id;
	else
		return new Long(-1);
  }

  public void setId(Long id) {
    if(id!=null)
		this.id=id;
	else
		this.id=new Long(-1);
  }

  public void addSystemProperty(Property systemProperty) {
    this.systemProperty.add(systemProperty);
  }

  public Property getSystemProperty(int index) {
    return this.systemProperty.get( index );
  }

  public Property removeSystemProperty(int index) {
    return this.systemProperty.remove( index );
  }

  public List<Property> getSystemPropertyList() {
    return this.systemProperty;
  }

  public void setSystemPropertyList(List<Property> systemProperty) {
    this.systemProperty=systemProperty;
  }

  public int sizeSystemPropertyList() {
    return this.systemProperty.size();
  }

  private static final long serialVersionUID = 1L;

  @XmlTransient
  private Long id;



  @XmlElement(name="system-property",required=true,nillable=false)
  protected List<Property> systemProperty = new ArrayList<Property>();

  /**
   * @deprecated Use method getSystemPropertyList
   * @return List<Property>
  */
  @Deprecated
  public List<Property> getSystemProperty() {
  	return this.systemProperty;
  }

  /**
   * @deprecated Use method setSystemPropertyList
   * @param systemProperty List<Property>
  */
  @Deprecated
  public void setSystemProperty(List<Property> systemProperty) {
  	this.systemProperty=systemProperty;
  }

  /**
   * @deprecated Use method sizeSystemPropertyList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizeSystemProperty() {
  	return this.systemProperty.size();
  }

}
