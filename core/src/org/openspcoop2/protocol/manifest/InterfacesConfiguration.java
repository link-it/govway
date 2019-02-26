/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it).
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


/** <p>Java class for InterfacesConfiguration complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="InterfacesConfiguration">
 * 		&lt;sequence>
 * 			&lt;element name="specification" type="{http://www.openspcoop2.org/protocol/manifest}InterfaceConfiguration" minOccurs="1" maxOccurs="unbounded"/>
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
@XmlType(name = "InterfacesConfiguration", 
  propOrder = {
  	"specification"
  }
)

@XmlRootElement(name = "InterfacesConfiguration")

public class InterfacesConfiguration extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public InterfacesConfiguration() {
  }

  public void addSpecification(InterfaceConfiguration specification) {
    this.specification.add(specification);
  }

  public InterfaceConfiguration getSpecification(int index) {
    return this.specification.get( index );
  }

  public InterfaceConfiguration removeSpecification(int index) {
    return this.specification.remove( index );
  }

  public List<InterfaceConfiguration> getSpecificationList() {
    return this.specification;
  }

  public void setSpecificationList(List<InterfaceConfiguration> specification) {
    this.specification=specification;
  }

  public int sizeSpecificationList() {
    return this.specification.size();
  }

  private static final long serialVersionUID = 1L;



  @XmlElement(name="specification",required=true,nillable=false)
  protected List<InterfaceConfiguration> specification = new ArrayList<InterfaceConfiguration>();

  /**
   * @deprecated Use method getSpecificationList
   * @return List<InterfaceConfiguration>
  */
  @Deprecated
  public List<InterfaceConfiguration> getSpecification() {
  	return this.specification;
  }

  /**
   * @deprecated Use method setSpecificationList
   * @param specification List<InterfaceConfiguration>
  */
  @Deprecated
  public void setSpecification(List<InterfaceConfiguration> specification) {
  	this.specification=specification;
  }

  /**
   * @deprecated Use method sizeSpecificationList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizeSpecification() {
  	return this.specification.size();
  }

}
