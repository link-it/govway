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
package org.openspcoop2.core.eccezione.router_details;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import java.io.Serializable;


/** <p>Java class for dettaglio-routing complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="dettaglio-routing"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="domain" type="{http://govway.org/integration/fault/router_details}dominio" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="timestamp" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="detail" type="{http://govway.org/integration/fault/router_details}dettaglio" minOccurs="1" maxOccurs="1"/&gt;
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
@XmlType(name = "dettaglio-routing", 
  propOrder = {
  	"domain",
  	"timestamp",
  	"detail"
  }
)

@XmlRootElement(name = "router-details")

public class DettaglioRouting extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public DettaglioRouting() {
    super();
  }

  public Dominio getDomain() {
    return this.domain;
  }

  public void setDomain(Dominio domain) {
    this.domain = domain;
  }

  public java.util.Date getTimestamp() {
    return this.timestamp;
  }

  public void setTimestamp(java.util.Date timestamp) {
    this.timestamp = timestamp;
  }

  public Dettaglio getDetail() {
    return this.detail;
  }

  public void setDetail(Dettaglio detail) {
    this.detail = detail;
  }

  private static final long serialVersionUID = 1L;

  private static org.openspcoop2.core.eccezione.router_details.model.DettaglioRoutingModel modelStaticInstance = null;
  private static synchronized void initModelStaticInstance(){
	  if(org.openspcoop2.core.eccezione.router_details.DettaglioRouting.modelStaticInstance==null){
  			org.openspcoop2.core.eccezione.router_details.DettaglioRouting.modelStaticInstance = new org.openspcoop2.core.eccezione.router_details.model.DettaglioRoutingModel();
	  }
  }
  public static org.openspcoop2.core.eccezione.router_details.model.DettaglioRoutingModel model(){
	  if(org.openspcoop2.core.eccezione.router_details.DettaglioRouting.modelStaticInstance==null){
	  		initModelStaticInstance();
	  }
	  return org.openspcoop2.core.eccezione.router_details.DettaglioRouting.modelStaticInstance;
  }


  @XmlElement(name="domain",required=true,nillable=false)
  protected Dominio domain;

  @jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter(org.openspcoop2.utils.jaxb.DateTime2String.class)
  @jakarta.xml.bind.annotation.XmlSchemaType(name="dateTime")
  @XmlElement(name="timestamp",required=true,nillable=false,type=java.lang.String.class)
  protected java.util.Date timestamp;

  @XmlElement(name="detail",required=true,nillable=false)
  protected Dettaglio detail;

}
