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


/** <p>Java class for condition complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="condition">
 * 		&lt;sequence>
 * 			&lt;element name="selected" type="{http://www.openspcoop2.org/core/mvc/properties}selected" minOccurs="0" maxOccurs="unbounded"/>
 * 			&lt;element name="equals" type="{http://www.openspcoop2.org/core/mvc/properties}equals" minOccurs="0" maxOccurs="unbounded"/>
 * 			&lt;element name="defined" type="{http://www.openspcoop2.org/core/mvc/properties}defined" minOccurs="0" maxOccurs="unbounded"/>
 * 		&lt;/sequence>
 * 		&lt;attribute name="and" type="{http://www.w3.org/2001/XMLSchema}boolean" use="optional" default="true"/>
 * 		&lt;attribute name="not" type="{http://www.w3.org/2001/XMLSchema}boolean" use="optional" default="false"/>
 * &lt;/complexType>
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "condition", 
  propOrder = {
  	"selected",
  	"equals",
  	"defined"
  }
)

@XmlRootElement(name = "condition")

public class Condition extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public Condition() {
  }

  public void addSelected(Selected selected) {
    this.selected.add(selected);
  }

  public Selected getSelected(int index) {
    return this.selected.get( index );
  }

  public Selected removeSelected(int index) {
    return this.selected.remove( index );
  }

  public List<Selected> getSelectedList() {
    return this.selected;
  }

  public void setSelectedList(List<Selected> selected) {
    this.selected=selected;
  }

  public int sizeSelectedList() {
    return this.selected.size();
  }

  public void addEquals(Equals equals) {
    this.equals.add(equals);
  }

  public Equals getEquals(int index) {
    return this.equals.get( index );
  }

  public Equals removeEquals(int index) {
    return this.equals.remove( index );
  }

  public List<Equals> getEqualsList() {
    return this.equals;
  }

  public void setEqualsList(List<Equals> equals) {
    this.equals=equals;
  }

  public int sizeEqualsList() {
    return this.equals.size();
  }

  public void addDefined(Defined defined) {
    this.defined.add(defined);
  }

  public Defined getDefined(int index) {
    return this.defined.get( index );
  }

  public Defined removeDefined(int index) {
    return this.defined.remove( index );
  }

  public List<Defined> getDefinedList() {
    return this.defined;
  }

  public void setDefinedList(List<Defined> defined) {
    this.defined=defined;
  }

  public int sizeDefinedList() {
    return this.defined.size();
  }

  public boolean isAnd() {
    return this.and;
  }

  public boolean getAnd() {
    return this.and;
  }

  public void setAnd(boolean and) {
    this.and = and;
  }

  public boolean isNot() {
    return this.not;
  }

  public boolean getNot() {
    return this.not;
  }

  public void setNot(boolean not) {
    this.not = not;
  }

  private static final long serialVersionUID = 1L;



  @XmlElement(name="selected",required=true,nillable=false)
  protected List<Selected> selected = new ArrayList<Selected>();

  /**
   * @deprecated Use method getSelectedList
   * @return List<Selected>
  */
  @Deprecated
  public List<Selected> getSelected() {
  	return this.selected;
  }

  /**
   * @deprecated Use method setSelectedList
   * @param selected List<Selected>
  */
  @Deprecated
  public void setSelected(List<Selected> selected) {
  	this.selected=selected;
  }

  /**
   * @deprecated Use method sizeSelectedList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizeSelected() {
  	return this.selected.size();
  }

  @XmlElement(name="equals",required=true,nillable=false)
  protected List<Equals> equals = new ArrayList<Equals>();

  /**
   * @deprecated Use method getEqualsList
   * @return List<Equals>
  */
  @Deprecated
  public List<Equals> getEquals() {
  	return this.equals;
  }

  /**
   * @deprecated Use method setEqualsList
   * @param equals List<Equals>
  */
  @Deprecated
  public void setEquals(List<Equals> equals) {
  	this.equals=equals;
  }

  /**
   * @deprecated Use method sizeEqualsList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizeEquals() {
  	return this.equals.size();
  }

  @XmlElement(name="defined",required=true,nillable=false)
  protected List<Defined> defined = new ArrayList<Defined>();

  /**
   * @deprecated Use method getDefinedList
   * @return List<Defined>
  */
  @Deprecated
  public List<Defined> getDefined() {
  	return this.defined;
  }

  /**
   * @deprecated Use method setDefinedList
   * @param defined List<Defined>
  */
  @Deprecated
  public void setDefined(List<Defined> defined) {
  	this.defined=defined;
  }

  /**
   * @deprecated Use method sizeDefinedList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizeDefined() {
  	return this.defined.size();
  }

  @javax.xml.bind.annotation.XmlSchemaType(name="boolean")
  @XmlAttribute(name="and",required=false)
  protected boolean and = true;

  @javax.xml.bind.annotation.XmlSchemaType(name="boolean")
  @XmlAttribute(name="not",required=false)
  protected boolean not = false;

}
