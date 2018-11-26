/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it).
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
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import java.io.Serializable;


/** <p>Java class for response-caching-configurazione complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="response-caching-configurazione">
 * 		&lt;sequence>
 * 			&lt;element name="hash-generator" type="{http://www.openspcoop2.org/core/config}response-caching-configurazione-hash-generator" minOccurs="0" maxOccurs="1"/>
 * 		&lt;/sequence>
 * 		&lt;attribute name="stato" type="{http://www.openspcoop2.org/core/config}StatoFunzionalita" use="optional" default="disabilitato"/>
 * 		&lt;attribute name="cache-timeout-seconds" type="{http://www.w3.org/2001/XMLSchema}unsignedInt" use="optional" default="300"/>
 * 		&lt;attribute name="max-message-size" type="{http://www.w3.org/2001/XMLSchema}unsignedLong" use="optional"/>
 * &lt;/complexType>
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "response-caching-configurazione", 
  propOrder = {
  	"hashGenerator"
  }
)

@XmlRootElement(name = "response-caching-configurazione")

public class ResponseCachingConfigurazione extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public ResponseCachingConfigurazione() {
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

  public ResponseCachingConfigurazioneHashGenerator getHashGenerator() {
    return this.hashGenerator;
  }

  public void setHashGenerator(ResponseCachingConfigurazioneHashGenerator hashGenerator) {
    this.hashGenerator = hashGenerator;
  }

  public void set_value_stato(String value) {
    this.stato = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString(value);
  }

  public String get_value_stato() {
    if(this.stato == null){
    	return null;
    }else{
    	return this.stato.toString();
    }
  }

  public org.openspcoop2.core.config.constants.StatoFunzionalita getStato() {
    return this.stato;
  }

  public void setStato(org.openspcoop2.core.config.constants.StatoFunzionalita stato) {
    this.stato = stato;
  }

  public java.lang.Integer getCacheTimeoutSeconds() {
    return this.cacheTimeoutSeconds;
  }

  public void setCacheTimeoutSeconds(java.lang.Integer cacheTimeoutSeconds) {
    this.cacheTimeoutSeconds = cacheTimeoutSeconds;
  }

  public java.lang.Long getMaxMessageSize() {
    return this.maxMessageSize;
  }

  public void setMaxMessageSize(java.lang.Long maxMessageSize) {
    this.maxMessageSize = maxMessageSize;
  }

  private static final long serialVersionUID = 1L;

  @XmlTransient
  private Long id;



  @XmlElement(name="hash-generator",required=false,nillable=false)
  protected ResponseCachingConfigurazioneHashGenerator hashGenerator;

  @javax.xml.bind.annotation.XmlTransient
  protected java.lang.String _value_stato;

  @XmlAttribute(name="stato",required=false)
  protected StatoFunzionalita stato = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString("disabilitato");

  @javax.xml.bind.annotation.XmlSchemaType(name="unsignedInt")
  @XmlAttribute(name="cache-timeout-seconds",required=false)
  protected java.lang.Integer cacheTimeoutSeconds = java.lang.Integer.valueOf("300");

  @javax.xml.bind.annotation.XmlSchemaType(name="unsignedLong")
  @XmlAttribute(name="max-message-size",required=false)
  protected java.lang.Long maxMessageSize;

}
