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
package org.openspcoop2.core.eccezione.errore_applicativo;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import java.io.Serializable;


/** <p>Java class for dati-cooperazione complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="dati-cooperazione"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="sender" type="{http://govway.org/integration/fault}soggetto" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="provider" type="{http://govway.org/integration/fault}soggetto" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="service" type="{http://govway.org/integration/fault}servizio" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="action" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="application" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/&gt;
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
@XmlType(name = "dati-cooperazione", 
  propOrder = {
  	"sender",
  	"provider",
  	"service",
  	"action",
  	"application"
  }
)

@XmlRootElement(name = "dati-cooperazione")

public class DatiCooperazione extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public DatiCooperazione() {
    super();
  }

  public Soggetto getSender() {
    return this.sender;
  }

  public void setSender(Soggetto sender) {
    this.sender = sender;
  }

  public Soggetto getProvider() {
    return this.provider;
  }

  public void setProvider(Soggetto provider) {
    this.provider = provider;
  }

  public Servizio getService() {
    return this.service;
  }

  public void setService(Servizio service) {
    this.service = service;
  }

  public java.lang.String getAction() {
    return this.action;
  }

  public void setAction(java.lang.String action) {
    this.action = action;
  }

  public java.lang.String getApplication() {
    return this.application;
  }

  public void setApplication(java.lang.String application) {
    this.application = application;
  }

  private static final long serialVersionUID = 1L;



  @XmlElement(name="sender",required=false,nillable=false)
  protected Soggetto sender;

  @XmlElement(name="provider",required=false,nillable=false)
  protected Soggetto provider;

  @XmlElement(name="service",required=false,nillable=false)
  protected Servizio service;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="action",required=false,nillable=false)
  protected java.lang.String action;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="application",required=false,nillable=false)
  protected java.lang.String application;

}
