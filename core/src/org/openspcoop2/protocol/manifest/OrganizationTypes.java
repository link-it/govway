/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it).
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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Java class for OrganizationTypes complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="OrganizationTypes">
 * 		&lt;sequence>
 * 			&lt;element name="type" type="{http://www.openspcoop2.org/protocol/manifest}OrganizationType" minOccurs="1" maxOccurs="unbounded"/>
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
@XmlType(name = "OrganizationTypes", 
  propOrder = {
  	"type"
  }
)

@XmlRootElement(name = "OrganizationTypes")

public class OrganizationTypes extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public OrganizationTypes() {
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
  protected List<OrganizationType> type = new ArrayList<OrganizationType>();

  /**
   * @deprecated Use method getTypeList
   * @return List<OrganizationType>
  */
  @Deprecated
  public List<OrganizationType> getType() {
  	return this.type;
  }

  /**
   * @deprecated Use method setTypeList
   * @param type List<OrganizationType>
  */
  @Deprecated
  public void setType(List<OrganizationType> type) {
  	this.type=type;
  }

  /**
   * @deprecated Use method sizeTypeList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizeType() {
  	return this.type.size();
  }

}
