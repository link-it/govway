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
package eu.domibus.configuration;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Java class for services complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="services"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="service" type="{http://www.domibus.eu/configuration}service" minOccurs="1" maxOccurs="unbounded"/&gt;
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
@XmlType(name = "services", 
  propOrder = {
  	"service"
  }
)

@XmlRootElement(name = "services")

public class Services extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public Services() {
    super();
  }

  public void addService(Service service) {
    this.service.add(service);
  }

  public Service getService(int index) {
    return this.service.get( index );
  }

  public Service removeService(int index) {
    return this.service.remove( index );
  }

  public List<Service> getServiceList() {
    return this.service;
  }

  public void setServiceList(List<Service> service) {
    this.service=service;
  }

  public int sizeServiceList() {
    return this.service.size();
  }

  private static final long serialVersionUID = 1L;



  @XmlElement(name="service",required=true,nillable=false)
  protected List<Service> service = new ArrayList<Service>();

  /**
   * @deprecated Use method getServiceList
   * @return List&lt;Service&gt;
  */
  @Deprecated
  public List<Service> getService() {
  	return this.service;
  }

  /**
   * @deprecated Use method setServiceList
   * @param service List&lt;Service&gt;
  */
  @Deprecated
  public void setService(List<Service> service) {
  	this.service=service;
  }

  /**
   * @deprecated Use method sizeServiceList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizeService() {
  	return this.service.size();
  }

}
