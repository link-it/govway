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
package org.openspcoop2.protocol.manifest;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Java class for ServiceTypes complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ServiceTypes"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="type" type="{http://www.openspcoop2.org/protocol/manifest}ServiceType" minOccurs="1" maxOccurs="unbounded"/&gt;
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
@XmlType(name = "ServiceTypes", 
  propOrder = {
  	"type"
  }
)

@XmlRootElement(name = "ServiceTypes")

public class ServiceTypes extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public ServiceTypes() {
    super();
  }

  public void addType(ServiceType type) {
    this.type.add(type);
  }

  public ServiceType getType(int index) {
    return this.type.get( index );
  }

  public ServiceType removeType(int index) {
    return this.type.remove( index );
  }

  public List<ServiceType> getTypeList() {
    return this.type;
  }

  public void setTypeList(List<ServiceType> type) {
    this.type=type;
  }

  public int sizeTypeList() {
    return this.type.size();
  }

  private static final long serialVersionUID = 1L;



  @XmlElement(name="type",required=true,nillable=false)
  private List<ServiceType> type = new ArrayList<>();

  /**
   * Use method getTypeList
   * @return List&lt;ServiceType&gt;
  */
  public List<ServiceType> getType() {
  	return this.getTypeList();
  }

  /**
   * Use method setTypeList
   * @param type List&lt;ServiceType&gt;
  */
  public void setType(List<ServiceType> type) {
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
