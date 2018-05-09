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
package org.openspcoop2.message.context;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Java class for serialized-context complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="serialized-context">
 * 		&lt;sequence>
 * 			&lt;element name="property" type="{http://www.openspcoop2.org/message/context}serialized-parameter" minOccurs="1" maxOccurs="unbounded"/>
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
@XmlType(name = "serialized-context", 
  propOrder = {
  	"property"
  }
)

@XmlRootElement(name = "serialized-context")

public class SerializedContext extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public SerializedContext() {
  }

  public Long getId() {
    if(this.id!=null)
		return this.id;
	else
		return Long.valueOf(-1);
  }

  public void setId(Long id) {
    if(id!=null)
		this.id=id;
	else
		this.id=Long.valueOf(-1);
  }

  public void addProperty(SerializedParameter property) {
    this.property.add(property);
  }

  public SerializedParameter getProperty(int index) {
    return this.property.get( index );
  }

  public SerializedParameter removeProperty(int index) {
    return this.property.remove( index );
  }

  public List<SerializedParameter> getPropertyList() {
    return this.property;
  }

  public void setPropertyList(List<SerializedParameter> property) {
    this.property=property;
  }

  public int sizePropertyList() {
    return this.property.size();
  }

  private static final long serialVersionUID = 1L;

  @XmlTransient
  private Long id;



  @XmlElement(name="property",required=true,nillable=false)
  protected List<SerializedParameter> property = new ArrayList<SerializedParameter>();

  /**
   * @deprecated Use method getPropertyList
   * @return List<SerializedParameter>
  */
  @Deprecated
  public List<SerializedParameter> getProperty() {
  	return this.property;
  }

  /**
   * @deprecated Use method setPropertyList
   * @param property List<SerializedParameter>
  */
  @Deprecated
  public void setProperty(List<SerializedParameter> property) {
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

}
