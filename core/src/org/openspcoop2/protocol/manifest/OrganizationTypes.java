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
package org.openspcoop2.protocol.manifest;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Java class for OrganizationTypes complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="OrganizationTypes"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="type" type="{http://www.openspcoop2.org/protocol/manifest}OrganizationType" minOccurs="1" maxOccurs="unbounded"/&gt;
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
@XmlType(name = "OrganizationTypes", 
  propOrder = {
  	"type"
  }
)

@XmlRootElement(name = "OrganizationTypes")

public class OrganizationTypes extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public OrganizationTypes() {
    super();
  }

  public void addType(OrganizationType type) {
    this.type.add(type);
  }

  public OrganizationType getType(int index) {
    return this.type.get( index );
  }

  public OrganizationType removeType(int index) {
    return this.type.remove( index );
  }

  public List<OrganizationType> getTypeList() {
    return this.type;
  }

  public void setTypeList(List<OrganizationType> type) {
    this.type=type;
  }

  public int sizeTypeList() {
    return this.type.size();
  }

  private static final long serialVersionUID = 1L;



  @XmlElement(name="type",required=true,nillable=false)
  private List<OrganizationType> type = new ArrayList<>();

  /**
   * Use method getTypeList
   * @return List&lt;OrganizationType&gt;
  */
  public List<OrganizationType> getType() {
  	return this.getTypeList();
  }

  /**
   * Use method setTypeList
   * @param type List&lt;OrganizationType&gt;
  */
  public void setType(List<OrganizationType> type) {
  	this.setTypeList(type);
  }

  /**
   * Use method sizeTypeList
   * @return lunghezza della lista
  */
  public int sizeType() {
  	return this.sizeTypeList();
  }

}
