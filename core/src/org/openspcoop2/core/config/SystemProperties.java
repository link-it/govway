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
package org.openspcoop2.core.config;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Java class for system-properties complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="system-properties"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="system-property" type="{http://www.openspcoop2.org/core/config}Property" minOccurs="1" maxOccurs="unbounded"/&gt;
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
@XmlType(name = "system-properties", 
  propOrder = {
  	"systemProperty"
  }
)

@XmlRootElement(name = "system-properties")

public class SystemProperties extends org.openspcoop2.utils.beans.BaseBeanWithId implements Serializable , Cloneable {
  public SystemProperties() {
    super();
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



  @XmlElement(name="system-property",required=true,nillable=false)
  private List<Property> systemProperty = new ArrayList<>();

  /**
   * Use method getSystemPropertyList
   * @return List&lt;Property&gt;
  */
  public List<Property> getSystemProperty() {
  	return this.getSystemPropertyList();
  }

  /**
   * Use method setSystemPropertyList
   * @param systemProperty List&lt;Property&gt;
  */
  public void setSystemProperty(List<Property> systemProperty) {
  	this.setSystemPropertyList(systemProperty);
  }

  /**
   * Use method sizeSystemPropertyList
   * @return lunghezza della lista
  */
  public int sizeSystemProperty() {
  	return this.sizeSystemPropertyList();
  }

}
