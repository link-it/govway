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
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import java.io.Serializable;


/** <p>Java class for response-caching-configurazione-control complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="response-caching-configurazione-control"&gt;
 * 		&lt;attribute name="noCache" type="{http://www.w3.org/2001/XMLSchema}boolean" use="optional" default="true"/&gt;
 * 		&lt;attribute name="maxAge" type="{http://www.w3.org/2001/XMLSchema}boolean" use="optional" default="true"/&gt;
 * 		&lt;attribute name="noStore" type="{http://www.w3.org/2001/XMLSchema}boolean" use="optional" default="true"/&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "response-caching-configurazione-control")

@XmlRootElement(name = "response-caching-configurazione-control")

public class ResponseCachingConfigurazioneControl extends org.openspcoop2.utils.beans.BaseBeanWithId implements Serializable , Cloneable {
  public ResponseCachingConfigurazioneControl() {
    super();
  }

  public boolean isNoCache() {
    return this.noCache;
  }

  public boolean getNoCache() {
    return this.noCache;
  }

  public void setNoCache(boolean noCache) {
    this.noCache = noCache;
  }

  public boolean isMaxAge() {
    return this.maxAge;
  }

  public boolean getMaxAge() {
    return this.maxAge;
  }

  public void setMaxAge(boolean maxAge) {
    this.maxAge = maxAge;
  }

  public boolean isNoStore() {
    return this.noStore;
  }

  public boolean getNoStore() {
    return this.noStore;
  }

  public void setNoStore(boolean noStore) {
    this.noStore = noStore;
  }

  private static final long serialVersionUID = 1L;



  @jakarta.xml.bind.annotation.XmlSchemaType(name="boolean")
  @XmlAttribute(name="noCache",required=false)
  protected boolean noCache = true;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="boolean")
  @XmlAttribute(name="maxAge",required=false)
  protected boolean maxAge = true;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="boolean")
  @XmlAttribute(name="noStore",required=false)
  protected boolean noStore = true;

}
