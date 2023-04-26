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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Java class for conditions complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="conditions"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="condition" type="{http://www.openspcoop2.org/core/mvc/properties}condition" minOccurs="1" maxOccurs="unbounded"/&gt;
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
@XmlType(name = "conditions", 
  propOrder = {
  	"condition"
  }
)

@XmlRootElement(name = "conditions")

public class Conditions extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public Conditions() {
    super();
  }

  public void addCondition(Condition condition) {
    this.condition.add(condition);
  }

  public Condition getCondition(int index) {
    return this.condition.get( index );
  }

  public Condition removeCondition(int index) {
    return this.condition.remove( index );
  }

  public List<Condition> getConditionList() {
    return this.condition;
  }

  public void setConditionList(List<Condition> condition) {
    this.condition=condition;
  }

  public int sizeConditionList() {
    return this.condition.size();
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



  @XmlElement(name="condition",required=true,nillable=false)
  private List<Condition> condition = new ArrayList<>();

  /**
   * Use method getConditionList
   * @return List&lt;Condition&gt;
  */
  public List<Condition> getCondition() {
  	return this.getConditionList();
  }

  /**
   * Use method setConditionList
   * @param condition List&lt;Condition&gt;
  */
  public void setCondition(List<Condition> condition) {
  	this.setConditionList(condition);
  }

  /**
   * Use method sizeConditionList
   * @return lunghezza della lista
  */
  public int sizeCondition() {
  	return this.sizeConditionList();
  }

  @javax.xml.bind.annotation.XmlSchemaType(name="boolean")
  @XmlAttribute(name="and",required=false)
  protected boolean and = true;

  @javax.xml.bind.annotation.XmlSchemaType(name="boolean")
  @XmlAttribute(name="not",required=false)
  protected boolean not = false;

}
