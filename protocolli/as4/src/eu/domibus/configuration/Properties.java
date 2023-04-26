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
package eu.domibus.configuration;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Java class for properties complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="properties"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="property" type="{http://www.domibus.eu/configuration}property" minOccurs="1" maxOccurs="unbounded"/&gt;
 * 			&lt;element name="propertySet" type="{http://www.domibus.eu/configuration}propertySet" minOccurs="1" maxOccurs="unbounded"/&gt;
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
@XmlType(name = "properties", 
  propOrder = {
  	"property",
  	"propertySet"
  }
)

@XmlRootElement(name = "properties")

public class Properties extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public Properties() {
    super();
  }

  public void addProperty(Property property) {
    this.property.add(property);
  }

  public Property getProperty(int index) {
    return this.property.get( index );
  }

  public Property removeProperty(int index) {
    return this.property.remove( index );
  }

  public List<Property> getPropertyList() {
    return this.property;
  }

  public void setPropertyList(List<Property> property) {
    this.property=property;
  }

  public int sizePropertyList() {
    return this.property.size();
  }

  public void addPropertySet(PropertySet propertySet) {
    this.propertySet.add(propertySet);
  }

  public PropertySet getPropertySet(int index) {
    return this.propertySet.get( index );
  }

  public PropertySet removePropertySet(int index) {
    return this.propertySet.remove( index );
  }

  public List<PropertySet> getPropertySetList() {
    return this.propertySet;
  }

  public void setPropertySetList(List<PropertySet> propertySet) {
    this.propertySet=propertySet;
  }

  public int sizePropertySetList() {
    return this.propertySet.size();
  }

  private static final long serialVersionUID = 1L;



  @XmlElement(name="property",required=true,nillable=false)
  private List<Property> property = new ArrayList<>();

  @XmlElement(name="propertySet",required=true,nillable=false)
  private List<PropertySet> propertySet = new ArrayList<>();

}
