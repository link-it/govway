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

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Java class for response-caching-configurazione complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="response-caching-configurazione"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="hash-generator" type="{http://www.openspcoop2.org/core/config}response-caching-configurazione-hash-generator" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="control" type="{http://www.openspcoop2.org/core/config}response-caching-configurazione-control" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="regola" type="{http://www.openspcoop2.org/core/config}response-caching-configurazione-regola" minOccurs="0" maxOccurs="unbounded"/&gt;
 * 		&lt;/sequence&gt;
 * 		&lt;attribute name="stato" type="{http://www.openspcoop2.org/core/config}StatoFunzionalita" use="optional" default="disabilitato"/&gt;
 * 		&lt;attribute name="cache-timeout-seconds" type="{http://www.w3.org/2001/XMLSchema}unsignedInt" use="optional" default="300"/&gt;
 * 		&lt;attribute name="max-message-size" type="{http://www.w3.org/2001/XMLSchema}unsignedLong" use="optional"/&gt;
 * &lt;/complexType&gt;
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
  	"hashGenerator",
  	"control",
  	"regola"
  }
)

@XmlRootElement(name = "response-caching-configurazione")

public class ResponseCachingConfigurazione extends org.openspcoop2.utils.beans.BaseBeanWithId implements Serializable , Cloneable {
  public ResponseCachingConfigurazione() {
    super();
  }

  public ResponseCachingConfigurazioneHashGenerator getHashGenerator() {
    return this.hashGenerator;
  }

  public void setHashGenerator(ResponseCachingConfigurazioneHashGenerator hashGenerator) {
    this.hashGenerator = hashGenerator;
  }

  public ResponseCachingConfigurazioneControl getControl() {
    return this.control;
  }

  public void setControl(ResponseCachingConfigurazioneControl control) {
    this.control = control;
  }

  public void addRegola(ResponseCachingConfigurazioneRegola regola) {
    this.regola.add(regola);
  }

  public ResponseCachingConfigurazioneRegola getRegola(int index) {
    return this.regola.get( index );
  }

  public ResponseCachingConfigurazioneRegola removeRegola(int index) {
    return this.regola.remove( index );
  }

  public List<ResponseCachingConfigurazioneRegola> getRegolaList() {
    return this.regola;
  }

  public void setRegolaList(List<ResponseCachingConfigurazioneRegola> regola) {
    this.regola=regola;
  }

  public int sizeRegolaList() {
    return this.regola.size();
  }

  public void setStatoRawEnumValue(String value) {
    this.stato = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString(value);
  }

  public String getStatoRawEnumValue() {
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



  @XmlElement(name="hash-generator",required=false,nillable=false)
  protected ResponseCachingConfigurazioneHashGenerator hashGenerator;

  @XmlElement(name="control",required=false,nillable=false)
  protected ResponseCachingConfigurazioneControl control;

  @XmlElement(name="regola",required=true,nillable=false)
  private List<ResponseCachingConfigurazioneRegola> regola = new ArrayList<>();

  /**
   * Use method getRegolaList
   * @return List&lt;ResponseCachingConfigurazioneRegola&gt;
  */
  public List<ResponseCachingConfigurazioneRegola> getRegola() {
  	return this.getRegolaList();
  }

  /**
   * Use method setRegolaList
   * @param regola List&lt;ResponseCachingConfigurazioneRegola&gt;
  */
  public void setRegola(List<ResponseCachingConfigurazioneRegola> regola) {
  	this.setRegolaList(regola);
  }

  /**
   * Use method sizeRegolaList
   * @return lunghezza della lista
  */
  public int sizeRegola() {
  	return this.sizeRegolaList();
  }

  @jakarta.xml.bind.annotation.XmlTransient
  protected java.lang.String statoRawEnumValue;

  @XmlAttribute(name="stato",required=false)
  protected StatoFunzionalita stato = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString("disabilitato");

  @jakarta.xml.bind.annotation.XmlSchemaType(name="unsignedInt")
  @XmlAttribute(name="cache-timeout-seconds",required=false)
  protected java.lang.Integer cacheTimeoutSeconds = java.lang.Integer.valueOf("300");

  @jakarta.xml.bind.annotation.XmlSchemaType(name="unsignedLong")
  @XmlAttribute(name="max-message-size",required=false)
  protected java.lang.Long maxMessageSize;

}
