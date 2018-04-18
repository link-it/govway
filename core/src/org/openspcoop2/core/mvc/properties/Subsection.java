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
package org.openspcoop2.core.mvc.properties;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Java class for subsection complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="subsection">
 * 		&lt;sequence>
 * 			&lt;element name="conditions" type="{http://www.openspcoop2.org/core/mvc/properties}conditions" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="item" type="{http://www.openspcoop2.org/core/mvc/properties}item" minOccurs="0" maxOccurs="unbounded"/>
 * 		&lt;/sequence>
 * 		&lt;attribute name="label" type="{http://www.w3.org/2001/XMLSchema}string" use="required"/>
 * &lt;/complexType>
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "subsection", 
  propOrder = {
  	"conditions",
  	"item"
  }
)

@XmlRootElement(name = "subsection")

public class Subsection extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public Subsection() {
  }

  public Conditions getConditions() {
    return this.conditions;
  }

  public void setConditions(Conditions conditions) {
    this.conditions = conditions;
  }

  public void addItem(Item item) {
    this.item.add(item);
  }

  public Item getItem(int index) {
    return this.item.get( index );
  }

  public Item removeItem(int index) {
    return this.item.remove( index );
  }

  public List<Item> getItemList() {
    return this.item;
  }

  public void setItemList(List<Item> item) {
    this.item=item;
  }

  public int sizeItemList() {
    return this.item.size();
  }

  public java.lang.String getLabel() {
    return this.label;
  }

  public void setLabel(java.lang.String label) {
    this.label = label;
  }

  private static final long serialVersionUID = 1L;



  @XmlElement(name="conditions",required=false,nillable=false)
  protected Conditions conditions;

  @XmlElement(name="item",required=true,nillable=false)
  protected List<Item> item = new ArrayList<Item>();

  /**
   * @deprecated Use method getItemList
   * @return List<Item>
  */
  @Deprecated
  public List<Item> getItem() {
  	return this.item;
  }

  /**
   * @deprecated Use method setItemList
   * @param item List<Item>
  */
  @Deprecated
  public void setItem(List<Item> item) {
  	this.item=item;
  }

  /**
   * @deprecated Use method sizeItemList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizeItem() {
  	return this.item.size();
  }

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="label",required=true)
  protected java.lang.String label;

}
