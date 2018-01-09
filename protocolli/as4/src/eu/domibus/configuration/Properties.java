/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2017 Link.it srl (http://link.it).
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
 * &lt;complexType name="properties">
 * 		&lt;sequence>
 * 			&lt;element name="property" type="{http://www.domibus.eu/configuration}property" minOccurs="1" maxOccurs="unbounded"/>
 * 			&lt;element name="propertySet" type="{http://www.domibus.eu/configuration}propertySet" minOccurs="1" maxOccurs="unbounded"/>
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
@XmlType(name = "properties", 
  propOrder = {
  	"property",
  	"propertySet"
  }
)

@XmlRootElement(name = "properties")

public class Properties extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public Properties() {
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
  protected List<Property> property = new ArrayList<Property>();

  /**
   * @deprecated Use method getPropertyList
   * @return List<Property>
  */
  @Deprecated
  public List<Property> getProperty() {
  	return this.property;
  }

  /**
   * @deprecated Use method setPropertyList
   * @param property List<Property>
  */
  @Deprecated
  public void setProperty(List<Property> property) {
  	this.property=property;
  }

  /**
   * @deprecated Use method sizePropertyList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizeProperty() {
  	return this.property.size();
  }

  @XmlElement(name="propertySet",required=true,nillable=false)
  protected List<PropertySet> propertySet = new ArrayList<PropertySet>();

  /**
   * @deprecated Use method getPropertySetList
   * @return List<PropertySet>
  */
  @Deprecated
  public List<PropertySet> getPropertySet() {
  	return this.propertySet;
  }

  /**
   * @deprecated Use method setPropertySetList
   * @param propertySet List<PropertySet>
  */
  @Deprecated
  public void setPropertySet(List<PropertySet> propertySet) {
  	this.propertySet=propertySet;
  }

  /**
   * @deprecated Use method sizePropertySetList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizePropertySet() {
  	return this.propertySet.size();
  }

}
