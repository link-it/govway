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
package org.openspcoop2.core.config;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Java class for routing-table-default complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="routing-table-default"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="route" type="{http://www.openspcoop2.org/core/config}route" minOccurs="1" maxOccurs="unbounded"/&gt;
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
@XmlType(name = "routing-table-default", 
  propOrder = {
  	"route"
  }
)

@XmlRootElement(name = "routing-table-default")

public class RoutingTableDefault extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public RoutingTableDefault() {
  }

  public Long getId() {
    if(this.id!=null)
		return this.id;
	else
		return Long.valueOf(-1);
  }

  public void setId(Long id) {
    if(id!=null)
		this.id=id;
	else
		this.id=Long.valueOf(-1);
  }

  public void addRoute(Route route) {
    this.route.add(route);
  }

  public Route getRoute(int index) {
    return this.route.get( index );
  }

  public Route removeRoute(int index) {
    return this.route.remove( index );
  }

  public List<Route> getRouteList() {
    return this.route;
  }

  public void setRouteList(List<Route> route) {
    this.route=route;
  }

  public int sizeRouteList() {
    return this.route.size();
  }

  private static final long serialVersionUID = 1L;

  @XmlTransient
  private Long id;



  @XmlElement(name="route",required=true,nillable=false)
  protected List<Route> route = new ArrayList<Route>();

  /**
   * @deprecated Use method getRouteList
   * @return List&lt;Route&gt;
  */
  @Deprecated
  public List<Route> getRoute() {
  	return this.route;
  }

  /**
   * @deprecated Use method setRouteList
   * @param route List&lt;Route&gt;
  */
  @Deprecated
  public void setRoute(List<Route> route) {
  	this.route=route;
  }

  /**
   * @deprecated Use method sizeRouteList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizeRoute() {
  	return this.route.size();
  }

}
