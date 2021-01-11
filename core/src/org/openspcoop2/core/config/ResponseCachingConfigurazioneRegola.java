/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it).
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
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;


/** <p>Java class for response-caching-configurazione-regola complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="response-caching-configurazione-regola"&gt;
 * 		&lt;attribute name="return-code-min" type="{http://www.w3.org/2001/XMLSchema}unsignedInt" use="optional"/&gt;
 * 		&lt;attribute name="return-code-max" type="{http://www.w3.org/2001/XMLSchema}unsignedInt" use="optional"/&gt;
 * 		&lt;attribute name="fault" type="{http://www.w3.org/2001/XMLSchema}boolean" use="optional" default="false"/&gt;
 * 		&lt;attribute name="cache-timeout-seconds" type="{http://www.w3.org/2001/XMLSchema}unsignedInt" use="optional"/&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "response-caching-configurazione-regola")

@XmlRootElement(name = "response-caching-configurazione-regola")

public class ResponseCachingConfigurazioneRegola extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public ResponseCachingConfigurazioneRegola() {
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

  public java.lang.Integer getReturnCodeMin() {
    return this.returnCodeMin;
  }

  public void setReturnCodeMin(java.lang.Integer returnCodeMin) {
    this.returnCodeMin = returnCodeMin;
  }

  public java.lang.Integer getReturnCodeMax() {
    return this.returnCodeMax;
  }

  public void setReturnCodeMax(java.lang.Integer returnCodeMax) {
    this.returnCodeMax = returnCodeMax;
  }

  public boolean isFault() {
    return this.fault;
  }

  public boolean getFault() {
    return this.fault;
  }

  public void setFault(boolean fault) {
    this.fault = fault;
  }

  public java.lang.Integer getCacheTimeoutSeconds() {
    return this.cacheTimeoutSeconds;
  }

  public void setCacheTimeoutSeconds(java.lang.Integer cacheTimeoutSeconds) {
    this.cacheTimeoutSeconds = cacheTimeoutSeconds;
  }

  private static final long serialVersionUID = 1L;

  @XmlTransient
  private Long id;



  @javax.xml.bind.annotation.XmlSchemaType(name="unsignedInt")
  @XmlAttribute(name="return-code-min",required=false)
  protected java.lang.Integer returnCodeMin;

  @javax.xml.bind.annotation.XmlSchemaType(name="unsignedInt")
  @XmlAttribute(name="return-code-max",required=false)
  protected java.lang.Integer returnCodeMax;

  @javax.xml.bind.annotation.XmlSchemaType(name="boolean")
  @XmlAttribute(name="fault",required=false)
  protected boolean fault = false;

  @javax.xml.bind.annotation.XmlSchemaType(name="unsignedInt")
  @XmlAttribute(name="cache-timeout-seconds",required=false)
  protected java.lang.Integer cacheTimeoutSeconds;

}
