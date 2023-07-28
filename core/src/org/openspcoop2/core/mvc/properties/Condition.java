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


/** <p>Java class for condition complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="condition"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="defined" type="{http://www.openspcoop2.org/core/mvc/properties}defined" minOccurs="0" maxOccurs="unbounded"/&gt;
 * 			&lt;element name="selected" type="{http://www.openspcoop2.org/core/mvc/properties}selected" minOccurs="0" maxOccurs="unbounded"/&gt;
 * 			&lt;element name="equals" type="{http://www.openspcoop2.org/core/mvc/properties}equals" minOccurs="0" maxOccurs="unbounded"/&gt;
 * 			&lt;element name="lessThen" type="{http://www.openspcoop2.org/core/mvc/properties}equals" minOccurs="0" maxOccurs="unbounded"/&gt;
 * 			&lt;element name="lessEquals" type="{http://www.openspcoop2.org/core/mvc/properties}equals" minOccurs="0" maxOccurs="unbounded"/&gt;
 * 			&lt;element name="greaterThen" type="{http://www.openspcoop2.org/core/mvc/properties}equals" minOccurs="0" maxOccurs="unbounded"/&gt;
 * 			&lt;element name="greaterEquals" type="{http://www.openspcoop2.org/core/mvc/properties}equals" minOccurs="0" maxOccurs="unbounded"/&gt;
 * 			&lt;element name="startsWith" type="{http://www.openspcoop2.org/core/mvc/properties}equals" minOccurs="0" maxOccurs="unbounded"/&gt;
 * 			&lt;element name="endsWith" type="{http://www.openspcoop2.org/core/mvc/properties}equals" minOccurs="0" maxOccurs="unbounded"/&gt;
 * 		&lt;/sequence&gt;
 * 		&lt;attribute name="and" type="{http://www.w3.org/2001/XMLSchema}boolean" use="optional" default="true"/&gt;
 * 		&lt;attribute name="not" type="{http://www.w3.org/2001/XMLSchema}boolean" use="optional" default="false"/&gt;
 * &lt;/complexType&gt;
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
  	"defined",
  	"selected",
  	"equals",
  	"lessThen",
  	"lessEquals",
  	"greaterThen",
  	"greaterEquals",
  	"startsWith",
  	"endsWith"
  }
)

@XmlRootElement(name = "condition")

public class Condition extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public Condition() {
    super();
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

  public void addLessThen(Equals lessThen) {
    this.lessThen.add(lessThen);
  }

  public Equals getLessThen(int index) {
    return this.lessThen.get( index );
  }

  public Equals removeLessThen(int index) {
    return this.lessThen.remove( index );
  }

  public List<Equals> getLessThenList() {
    return this.lessThen;
  }

  public void setLessThenList(List<Equals> lessThen) {
    this.lessThen=lessThen;
  }

  public int sizeLessThenList() {
    return this.lessThen.size();
  }

  public void addLessEquals(Equals lessEquals) {
    this.lessEquals.add(lessEquals);
  }

  public Equals getLessEquals(int index) {
    return this.lessEquals.get( index );
  }

  public Equals removeLessEquals(int index) {
    return this.lessEquals.remove( index );
  }

  public List<Equals> getLessEqualsList() {
    return this.lessEquals;
  }

  public void setLessEqualsList(List<Equals> lessEquals) {
    this.lessEquals=lessEquals;
  }

  public int sizeLessEqualsList() {
    return this.lessEquals.size();
  }

  public void addGreaterThen(Equals greaterThen) {
    this.greaterThen.add(greaterThen);
  }

  public Equals getGreaterThen(int index) {
    return this.greaterThen.get( index );
  }

  public Equals removeGreaterThen(int index) {
    return this.greaterThen.remove( index );
  }

  public List<Equals> getGreaterThenList() {
    return this.greaterThen;
  }

  public void setGreaterThenList(List<Equals> greaterThen) {
    this.greaterThen=greaterThen;
  }

  public int sizeGreaterThenList() {
    return this.greaterThen.size();
  }

  public void addGreaterEquals(Equals greaterEquals) {
    this.greaterEquals.add(greaterEquals);
  }

  public Equals getGreaterEquals(int index) {
    return this.greaterEquals.get( index );
  }

  public Equals removeGreaterEquals(int index) {
    return this.greaterEquals.remove( index );
  }

  public List<Equals> getGreaterEqualsList() {
    return this.greaterEquals;
  }

  public void setGreaterEqualsList(List<Equals> greaterEquals) {
    this.greaterEquals=greaterEquals;
  }

  public int sizeGreaterEqualsList() {
    return this.greaterEquals.size();
  }

  public void addStartsWith(Equals startsWith) {
    this.startsWith.add(startsWith);
  }

  public Equals getStartsWith(int index) {
    return this.startsWith.get( index );
  }

  public Equals removeStartsWith(int index) {
    return this.startsWith.remove( index );
  }

  public List<Equals> getStartsWithList() {
    return this.startsWith;
  }

  public void setStartsWithList(List<Equals> startsWith) {
    this.startsWith=startsWith;
  }

  public int sizeStartsWithList() {
    return this.startsWith.size();
  }

  public void addEndsWith(Equals endsWith) {
    this.endsWith.add(endsWith);
  }

  public Equals getEndsWith(int index) {
    return this.endsWith.get( index );
  }

  public Equals removeEndsWith(int index) {
    return this.endsWith.remove( index );
  }

  public List<Equals> getEndsWithList() {
    return this.endsWith;
  }

  public void setEndsWithList(List<Equals> endsWith) {
    this.endsWith=endsWith;
  }

  public int sizeEndsWithList() {
    return this.endsWith.size();
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



  @XmlElement(name="defined",required=true,nillable=false)
  private List<Defined> defined = new ArrayList<>();

  /**
   * Use method getDefinedList
   * @return List&lt;Defined&gt;
  */
  public List<Defined> getDefined() {
  	return this.getDefinedList();
  }

  /**
   * Use method setDefinedList
   * @param defined List&lt;Defined&gt;
  */
  public void setDefined(List<Defined> defined) {
  	this.setDefinedList(defined);
  }

  /**
   * Use method sizeDefinedList
   * @return lunghezza della lista
  */
  public int sizeDefined() {
  	return this.sizeDefinedList();
  }

  @XmlElement(name="selected",required=true,nillable=false)
  private List<Selected> selected = new ArrayList<>();

  /**
   * Use method getSelectedList
   * @return List&lt;Selected&gt;
  */
  public List<Selected> getSelected() {
  	return this.getSelectedList();
  }

  /**
   * Use method setSelectedList
   * @param selected List&lt;Selected&gt;
  */
  public void setSelected(List<Selected> selected) {
  	this.setSelectedList(selected);
  }

  /**
   * Use method sizeSelectedList
   * @return lunghezza della lista
  */
  public int sizeSelected() {
  	return this.sizeSelectedList();
  }

  @XmlElement(name="equals",required=true,nillable=false)
  private List<Equals> equals = new ArrayList<>();

  /**
   * Use method getEqualsList
   * @return List&lt;Equals&gt;
  */
  public List<Equals> getEquals() {
  	return this.getEqualsList();
  }

  /**
   * Use method setEqualsList
   * @param equals List&lt;Equals&gt;
  */
  public void setEquals(List<Equals> equals) {
  	this.setEqualsList(equals);
  }

  /**
   * Use method sizeEqualsList
   * @return lunghezza della lista
  */
  public int sizeEquals() {
  	return this.sizeEqualsList();
  }

  @XmlElement(name="lessThen",required=true,nillable=false)
  private List<Equals> lessThen = new ArrayList<>();

  /**
   * Use method getLessThenList
   * @return List&lt;Equals&gt;
  */
  public List<Equals> getLessThen() {
  	return this.getLessThenList();
  }

  /**
   * Use method setLessThenList
   * @param lessThen List&lt;Equals&gt;
  */
  public void setLessThen(List<Equals> lessThen) {
  	this.setLessThenList(lessThen);
  }

  /**
   * Use method sizeLessThenList
   * @return lunghezza della lista
  */
  public int sizeLessThen() {
  	return this.sizeLessThenList();
  }

  @XmlElement(name="lessEquals",required=true,nillable=false)
  private List<Equals> lessEquals = new ArrayList<>();

  /**
   * Use method getLessEqualsList
   * @return List&lt;Equals&gt;
  */
  public List<Equals> getLessEquals() {
  	return this.getLessEqualsList();
  }

  /**
   * Use method setLessEqualsList
   * @param lessEquals List&lt;Equals&gt;
  */
  public void setLessEquals(List<Equals> lessEquals) {
  	this.setLessEqualsList(lessEquals);
  }

  /**
   * Use method sizeLessEqualsList
   * @return lunghezza della lista
  */
  public int sizeLessEquals() {
  	return this.sizeLessEqualsList();
  }

  @XmlElement(name="greaterThen",required=true,nillable=false)
  private List<Equals> greaterThen = new ArrayList<>();

  /**
   * Use method getGreaterThenList
   * @return List&lt;Equals&gt;
  */
  public List<Equals> getGreaterThen() {
  	return this.getGreaterThenList();
  }

  /**
   * Use method setGreaterThenList
   * @param greaterThen List&lt;Equals&gt;
  */
  public void setGreaterThen(List<Equals> greaterThen) {
  	this.setGreaterThenList(greaterThen);
  }

  /**
   * Use method sizeGreaterThenList
   * @return lunghezza della lista
  */
  public int sizeGreaterThen() {
  	return this.sizeGreaterThenList();
  }

  @XmlElement(name="greaterEquals",required=true,nillable=false)
  private List<Equals> greaterEquals = new ArrayList<>();

  /**
   * Use method getGreaterEqualsList
   * @return List&lt;Equals&gt;
  */
  public List<Equals> getGreaterEquals() {
  	return this.getGreaterEqualsList();
  }

  /**
   * Use method setGreaterEqualsList
   * @param greaterEquals List&lt;Equals&gt;
  */
  public void setGreaterEquals(List<Equals> greaterEquals) {
  	this.setGreaterEqualsList(greaterEquals);
  }

  /**
   * Use method sizeGreaterEqualsList
   * @return lunghezza della lista
  */
  public int sizeGreaterEquals() {
  	return this.sizeGreaterEqualsList();
  }

  @XmlElement(name="startsWith",required=true,nillable=false)
  private List<Equals> startsWith = new ArrayList<>();

  /**
   * Use method getStartsWithList
   * @return List&lt;Equals&gt;
  */
  public List<Equals> getStartsWith() {
  	return this.getStartsWithList();
  }

  /**
   * Use method setStartsWithList
   * @param startsWith List&lt;Equals&gt;
  */
  public void setStartsWith(List<Equals> startsWith) {
  	this.setStartsWithList(startsWith);
  }

  /**
   * Use method sizeStartsWithList
   * @return lunghezza della lista
  */
  public int sizeStartsWith() {
  	return this.sizeStartsWithList();
  }

  @XmlElement(name="endsWith",required=true,nillable=false)
  private List<Equals> endsWith = new ArrayList<>();

  /**
   * Use method getEndsWithList
   * @return List&lt;Equals&gt;
  */
  public List<Equals> getEndsWith() {
  	return this.getEndsWithList();
  }

  /**
   * Use method setEndsWithList
   * @param endsWith List&lt;Equals&gt;
  */
  public void setEndsWith(List<Equals> endsWith) {
  	this.setEndsWithList(endsWith);
  }

  /**
   * Use method sizeEndsWithList
   * @return lunghezza della lista
  */
  public int sizeEndsWith() {
  	return this.sizeEndsWithList();
  }

  @jakarta.xml.bind.annotation.XmlSchemaType(name="boolean")
  @XmlAttribute(name="and",required=false)
  protected boolean and = true;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="boolean")
  @XmlAttribute(name="not",required=false)
  protected boolean not = false;

}
