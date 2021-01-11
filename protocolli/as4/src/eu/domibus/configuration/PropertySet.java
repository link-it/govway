/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it).
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
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Java class for propertySet complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="propertySet"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="propertyRef" type="{http://www.domibus.eu/configuration}propertyRef" minOccurs="1" maxOccurs="unbounded"/&gt;
 * 		&lt;/sequence&gt;
 * 		&lt;attribute name="name" type="{http://www.domibus.eu/configuration}string" use="required"/&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "propertySet", 
  propOrder = {
  	"propertyRef"
  }
)

@XmlRootElement(name = "propertySet")

public class PropertySet extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public PropertySet() {
  }

  public void addPropertyRef(PropertyRef propertyRef) {
    this.propertyRef.add(propertyRef);
  }

  public PropertyRef getPropertyRef(int index) {
    return this.propertyRef.get( index );
  }

  public PropertyRef removePropertyRef(int index) {
    return this.propertyRef.remove( index );
  }

  public List<PropertyRef> getPropertyRefList() {
    return this.propertyRef;
  }

  public void setPropertyRefList(List<PropertyRef> propertyRef) {
    this.propertyRef=propertyRef;
  }

  public int sizePropertyRefList() {
    return this.propertyRef.size();
  }

  public java.lang.String getName() {
    return this.name;
  }

  public void setName(java.lang.String name) {
    this.name = name;
  }

  private static final long serialVersionUID = 1L;



  @XmlElement(name="propertyRef",required=true,nillable=false)
  protected List<PropertyRef> propertyRef = new ArrayList<PropertyRef>();

  /**
   * @deprecated Use method getPropertyRefList
   * @return List&lt;PropertyRef&gt;
  */
  @Deprecated
  public List<PropertyRef> getPropertyRef() {
  	return this.propertyRef;
  }

  /**
   * @deprecated Use method setPropertyRefList
   * @param propertyRef List&lt;PropertyRef&gt;
  */
  @Deprecated
  public void setPropertyRef(List<PropertyRef> propertyRef) {
  	this.propertyRef=propertyRef;
  }

  /**
   * @deprecated Use method sizePropertyRefList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizePropertyRef() {
  	return this.propertyRef.size();
  }

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="name",required=true)
  protected java.lang.String name;

}
