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
package org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Java class for MessageProperties complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="MessageProperties"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="Property" type="{http://docs.oasis-open.org/ebxml-msg/ebms/v3.0/ns/core/200704/}Property" minOccurs="1" maxOccurs="unbounded"/&gt;
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
@XmlType(name = "MessageProperties", 
  propOrder = {
  	"property"
  }
)

@XmlRootElement(name = "MessageProperties")

public class MessageProperties extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public MessageProperties() {
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

  private static final long serialVersionUID = 1L;



  @XmlElement(name="Property",required=true,nillable=false)
  private List<Property> property = new ArrayList<>();

  /**
   * Use method getPropertyList
   * @return List&lt;Property&gt;
  */
  public List<Property> getProperty() {
  	return this.getPropertyList();
  }

  /**
   * Use method setPropertyList
   * @param property List&lt;Property&gt;
  */
  public void setProperty(List<Property> property) {
  	this.setPropertyList(property);
  }

  /**
   * Use method sizePropertyList
   * @return lunghezza della lista
  */
  public int sizeProperty() {
  	return this.sizePropertyList();
  }

}
