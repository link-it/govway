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
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import java.io.Serializable;


/** <p>Java class for configurazione-generale-handler complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="configurazione-generale-handler"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="request" type="{http://www.openspcoop2.org/core/config}configurazione-message-handlers" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="response" type="{http://www.openspcoop2.org/core/config}configurazione-message-handlers" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="service" type="{http://www.openspcoop2.org/core/config}configurazione-service-handlers" minOccurs="0" maxOccurs="1"/&gt;
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
@XmlType(name = "configurazione-generale-handler", 
  propOrder = {
  	"request",
  	"response",
  	"service"
  }
)

@XmlRootElement(name = "configurazione-generale-handler")

public class ConfigurazioneGeneraleHandler extends org.openspcoop2.utils.beans.BaseBeanWithId implements Serializable , Cloneable {
  public ConfigurazioneGeneraleHandler() {
    super();
  }

  public ConfigurazioneMessageHandlers getRequest() {
    return this.request;
  }

  public void setRequest(ConfigurazioneMessageHandlers request) {
    this.request = request;
  }

  public ConfigurazioneMessageHandlers getResponse() {
    return this.response;
  }

  public void setResponse(ConfigurazioneMessageHandlers response) {
    this.response = response;
  }

  public ConfigurazioneServiceHandlers getService() {
    return this.service;
  }

  public void setService(ConfigurazioneServiceHandlers service) {
    this.service = service;
  }

  private static final long serialVersionUID = 1L;



  @XmlElement(name="request",required=false,nillable=false)
  protected ConfigurazioneMessageHandlers request;

  @XmlElement(name="response",required=false,nillable=false)
  protected ConfigurazioneMessageHandlers response;

  @XmlElement(name="service",required=false,nillable=false)
  protected ConfigurazioneServiceHandlers service;

}
