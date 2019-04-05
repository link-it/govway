/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it).
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
import java.util.ArrayList;
import java.util.List;


/** <p>Java class for response-caching-configurazione complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="response-caching-configurazione">
 * 		&lt;sequence>
 * 			&lt;element name="hash-generator" type="{http://www.openspcoop2.org/core/config}response-caching-configurazione-hash-generator" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="control" type="{http://www.openspcoop2.org/core/config}response-caching-configurazione-control" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="regola" type="{http://www.openspcoop2.org/core/config}response-caching-configurazione-regola" minOccurs="0" maxOccurs="unbounded"/>
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
  	"hashGenerator",
  	"control",
  	"regola"
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

  @XmlElement(name="control",required=false,nillable=false)
  protected ResponseCachingConfigurazioneControl control;

  @XmlElement(name="regola",required=true,nillable=false)
  protected List<ResponseCachingConfigurazioneRegola> regola = new ArrayList<ResponseCachingConfigurazioneRegola>();

  /**
   * @deprecated Use method getRegolaList
   * @return List<ResponseCachingConfigurazioneRegola>
  */
  @Deprecated
  public List<ResponseCachingConfigurazioneRegola> getRegola() {
  	return this.regola;
  }

  /**
   * @deprecated Use method setRegolaList
   * @param regola List<ResponseCachingConfigurazioneRegola>
  */
  @Deprecated
  public void setRegola(List<ResponseCachingConfigurazioneRegola> regola) {
  	this.regola=regola;
  }

  /**
   * @deprecated Use method sizeRegolaList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizeRegola() {
  	return this.regola.size();
  }

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
