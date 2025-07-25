/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it).
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

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Java class for routing-table-destinazione complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="routing-table-destinazione"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="route" type="{http://www.openspcoop2.org/core/config}route" minOccurs="1" maxOccurs="unbounded"/&gt;
 * 		&lt;/sequence&gt;
 * 		&lt;attribute name="tipo" type="{http://www.w3.org/2001/XMLSchema}string" use="required"/&gt;
 * 		&lt;attribute name="nome" type="{http://www.w3.org/2001/XMLSchema}string" use="required"/&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "routing-table-destinazione", 
  propOrder = {
  	"route"
  }
)

@XmlRootElement(name = "routing-table-destinazione")

public class RoutingTableDestinazione extends org.openspcoop2.utils.beans.BaseBeanWithId implements Serializable , Cloneable {
  public RoutingTableDestinazione() {
    super();
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

  public java.lang.String getTipo() {
    return this.tipo;
  }

  public void setTipo(java.lang.String tipo) {
    this.tipo = tipo;
  }

  public java.lang.String getNome() {
    return this.nome;
  }

  public void setNome(java.lang.String nome) {
    this.nome = nome;
  }

  private static final long serialVersionUID = 1L;



  @XmlElement(name="route",required=true,nillable=false)
  private List<Route> route = new ArrayList<>();

  /**
   * Use method getRouteList
   * @return List&lt;Route&gt;
  */
  public List<Route> getRoute() {
  	return this.getRouteList();
  }

  /**
   * Use method setRouteList
   * @param route List&lt;Route&gt;
  */
  public void setRoute(List<Route> route) {
  	this.setRouteList(route);
  }

  /**
   * Use method sizeRouteList
   * @return lunghezza della lista
  */
  public int sizeRoute() {
  	return this.sizeRouteList();
  }

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="tipo",required=true)
  protected java.lang.String tipo;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="nome",required=true)
  protected java.lang.String nome;

}
