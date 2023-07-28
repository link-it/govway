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
package org.openspcoop2.core.mvc.properties;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Java class for section complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="section"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="conditions" type="{http://www.openspcoop2.org/core/mvc/properties}conditions" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="item" type="{http://www.openspcoop2.org/core/mvc/properties}item" minOccurs="0" maxOccurs="unbounded"/&gt;
 * 			&lt;element name="subsection" type="{http://www.openspcoop2.org/core/mvc/properties}subsection" minOccurs="0" maxOccurs="unbounded"/&gt;
 * 		&lt;/sequence&gt;
 * 		&lt;attribute name="label" type="{http://www.w3.org/2001/XMLSchema}string" use="required"/&gt;
 * 		&lt;attribute name="hidden" type="{http://www.w3.org/2001/XMLSchema}boolean" use="optional" default="false"/&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "section", 
  propOrder = {
  	"conditions",
  	"item",
  	"subsection"
  }
)

@XmlRootElement(name = "section")

public class Section extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public Section() {
    super();
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

  public void addSubsection(Subsection subsection) {
    this.subsection.add(subsection);
  }

  public Subsection getSubsection(int index) {
    return this.subsection.get( index );
  }

  public Subsection removeSubsection(int index) {
    return this.subsection.remove( index );
  }

  public List<Subsection> getSubsectionList() {
    return this.subsection;
  }

  public void setSubsectionList(List<Subsection> subsection) {
    this.subsection=subsection;
  }

  public int sizeSubsectionList() {
    return this.subsection.size();
  }

  public java.lang.String getLabel() {
    return this.label;
  }

  public void setLabel(java.lang.String label) {
    this.label = label;
  }

  public boolean isHidden() {
    return this.hidden;
  }

  public boolean getHidden() {
    return this.hidden;
  }

  public void setHidden(boolean hidden) {
    this.hidden = hidden;
  }

  private static final long serialVersionUID = 1L;



  @XmlElement(name="conditions",required=false,nillable=false)
  protected Conditions conditions;

  @XmlElement(name="item",required=true,nillable=false)
  private List<Item> item = new ArrayList<>();

  /**
   * Use method getItemList
   * @return List&lt;Item&gt;
  */
  public List<Item> getItem() {
  	return this.getItemList();
  }

  /**
   * Use method setItemList
   * @param item List&lt;Item&gt;
  */
  public void setItem(List<Item> item) {
  	this.setItemList(item);
  }

  /**
   * Use method sizeItemList
   * @return lunghezza della lista
  */
  public int sizeItem() {
  	return this.sizeItemList();
  }

  @XmlElement(name="subsection",required=true,nillable=false)
  private List<Subsection> subsection = new ArrayList<>();

  /**
   * Use method getSubsectionList
   * @return List&lt;Subsection&gt;
  */
  public List<Subsection> getSubsection() {
  	return this.getSubsectionList();
  }

  /**
   * Use method setSubsectionList
   * @param subsection List&lt;Subsection&gt;
  */
  public void setSubsection(List<Subsection> subsection) {
  	this.setSubsectionList(subsection);
  }

  /**
   * Use method sizeSubsectionList
   * @return lunghezza della lista
  */
  public int sizeSubsection() {
  	return this.sizeSubsectionList();
  }

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="label",required=true)
  protected java.lang.String label;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="boolean")
  @XmlAttribute(name="hidden",required=false)
  protected boolean hidden = false;

}
