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
package org.openspcoop2.core.config;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;


/** <p>Java class for response-caching-configurazione-generale complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="response-caching-configurazione-generale"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="configurazione" type="{http://www.openspcoop2.org/core/config}response-caching-configurazione" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="cache" type="{http://www.openspcoop2.org/core/config}cache" minOccurs="0" maxOccurs="1"/&gt;
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
@XmlType(name = "response-caching-configurazione-generale", 
  propOrder = {
  	"configurazione",
  	"cache"
  }
)

@XmlRootElement(name = "response-caching-configurazione-generale")

public class ResponseCachingConfigurazioneGenerale extends org.openspcoop2.utils.beans.BaseBeanWithId implements Serializable , Cloneable {
  public ResponseCachingConfigurazioneGenerale() {
    super();
  }

  public ResponseCachingConfigurazione getConfigurazione() {
    return this.configurazione;
  }

  public void setConfigurazione(ResponseCachingConfigurazione configurazione) {
    this.configurazione = configurazione;
  }

  public Cache getCache() {
    return this.cache;
  }

  public void setCache(Cache cache) {
    this.cache = cache;
  }

  private static final long serialVersionUID = 1L;



  @XmlElement(name="configurazione",required=true,nillable=false)
  protected ResponseCachingConfigurazione configurazione;

  @XmlElement(name="cache",required=false,nillable=false)
  protected Cache cache;

}
