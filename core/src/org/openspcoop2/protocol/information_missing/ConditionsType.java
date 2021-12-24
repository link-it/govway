/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it).
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
package org.openspcoop2.protocol.information_missing;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Java class for ConditionsType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ConditionsType"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="proprieta" type="{http://www.openspcoop2.org/protocol/information_missing}ConditionType" minOccurs="1" maxOccurs="unbounded"/&gt;
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
@XmlType(name = "ConditionsType", 
  propOrder = {
  	"proprieta"
  }
)

@XmlRootElement(name = "ConditionsType")

public class ConditionsType extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public ConditionsType() {
  }

  public void addProprieta(ConditionType proprieta) {
    this.proprieta.add(proprieta);
  }

  public ConditionType getProprieta(int index) {
    return this.proprieta.get( index );
  }

  public ConditionType removeProprieta(int index) {
    return this.proprieta.remove( index );
  }

  public List<ConditionType> getProprietaList() {
    return this.proprieta;
  }

  public void setProprietaList(List<ConditionType> proprieta) {
    this.proprieta=proprieta;
  }

  public int sizeProprietaList() {
    return this.proprieta.size();
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



  @XmlElement(name="proprieta",required=true,nillable=false)
  protected List<ConditionType> proprieta = new ArrayList<ConditionType>();

  /**
   * @deprecated Use method getProprietaList
   * @return List&lt;ConditionType&gt;
  */
  @Deprecated
  public List<ConditionType> getProprieta() {
  	return this.proprieta;
  }

  /**
   * @deprecated Use method setProprietaList
   * @param proprieta List&lt;ConditionType&gt;
  */
  @Deprecated
  public void setProprieta(List<ConditionType> proprieta) {
  	this.proprieta=proprieta;
  }

  /**
   * @deprecated Use method sizeProprietaList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizeProprieta() {
  	return this.proprieta.size();
  }

  @javax.xml.bind.annotation.XmlSchemaType(name="boolean")
  @XmlAttribute(name="and",required=false)
  protected boolean and = true;

  @javax.xml.bind.annotation.XmlSchemaType(name="boolean")
  @XmlAttribute(name="not",required=false)
  protected boolean not = false;

}
