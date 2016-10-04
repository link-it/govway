/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2016 Link.it srl (http://link.it).
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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


/** <p>Java class for route complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="route">
 * 		&lt;sequence>
 * 			&lt;element name="registro" type="{http://www.openspcoop2.org/core/config}route-registro" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="gateway" type="{http://www.openspcoop2.org/core/config}route-gateway" minOccurs="0" maxOccurs="1"/>
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
@XmlType(name = "route", 
  propOrder = {
  	"registro",
  	"gateway"
  }
)

@XmlRootElement(name = "route")

public class Route extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public Route() {
  }

  public Long getId() {
    if(this.id!=null)
		return this.id;
	else
		return new Long(-1);
  }

  public void setId(Long id) {
    if(id!=null)
		this.id=id;
	else
		this.id=new Long(-1);
  }

  public RouteRegistro getRegistro() {
    return this.registro;
  }

  public void setRegistro(RouteRegistro registro) {
    this.registro = registro;
  }

  public RouteGateway getGateway() {
    return this.gateway;
  }

  public void setGateway(RouteGateway gateway) {
    this.gateway = gateway;
  }

  private static final long serialVersionUID = 1L;

  @XmlTransient
  private Long id;



  @XmlElement(name="registro",required=false,nillable=false)
  protected RouteRegistro registro;

  @XmlElement(name="gateway",required=false,nillable=false)
  protected RouteGateway gateway;

}
