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
package eu.domibus.configuration;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;


/** <p>Java class for legConfiguration complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="legConfiguration"&gt;
 * 		&lt;attribute name="name" type="{http://www.domibus.eu/configuration}string" use="required"/&gt;
 * 		&lt;attribute name="service" type="{http://www.domibus.eu/configuration}string" use="required"/&gt;
 * 		&lt;attribute name="action" type="{http://www.domibus.eu/configuration}string" use="required"/&gt;
 * 		&lt;attribute name="security" type="{http://www.domibus.eu/configuration}string" use="required"/&gt;
 * 		&lt;attribute name="defaultMpc" type="{http://www.domibus.eu/configuration}string" use="required"/&gt;
 * 		&lt;attribute name="receptionAwareness" type="{http://www.domibus.eu/configuration}string" use="required"/&gt;
 * 		&lt;attribute name="reliability" type="{http://www.domibus.eu/configuration}string" use="required"/&gt;
 * 		&lt;attribute name="propertySet" type="{http://www.domibus.eu/configuration}string" use="optional"/&gt;
 * 		&lt;attribute name="payloadProfile" type="{http://www.domibus.eu/configuration}string" use="optional"/&gt;
 * 		&lt;attribute name="errorHandling" type="{http://www.domibus.eu/configuration}string" use="required"/&gt;
 * 		&lt;attribute name="compressPayloads" type="{http://www.w3.org/2001/XMLSchema}string" use="required"/&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "legConfiguration")

@XmlRootElement(name = "legConfiguration")

public class LegConfiguration extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public LegConfiguration() {
  }

  public java.lang.String getName() {
    return this.name;
  }

  public void setName(java.lang.String name) {
    this.name = name;
  }

  public java.lang.String getService() {
    return this.service;
  }

  public void setService(java.lang.String service) {
    this.service = service;
  }

  public java.lang.String getAction() {
    return this.action;
  }

  public void setAction(java.lang.String action) {
    this.action = action;
  }

  public java.lang.String getSecurity() {
    return this.security;
  }

  public void setSecurity(java.lang.String security) {
    this.security = security;
  }

  public java.lang.String getDefaultMpc() {
    return this.defaultMpc;
  }

  public void setDefaultMpc(java.lang.String defaultMpc) {
    this.defaultMpc = defaultMpc;
  }

  public java.lang.String getReceptionAwareness() {
    return this.receptionAwareness;
  }

  public void setReceptionAwareness(java.lang.String receptionAwareness) {
    this.receptionAwareness = receptionAwareness;
  }

  public java.lang.String getReliability() {
    return this.reliability;
  }

  public void setReliability(java.lang.String reliability) {
    this.reliability = reliability;
  }

  public java.lang.String getPropertySet() {
    return this.propertySet;
  }

  public void setPropertySet(java.lang.String propertySet) {
    this.propertySet = propertySet;
  }

  public java.lang.String getPayloadProfile() {
    return this.payloadProfile;
  }

  public void setPayloadProfile(java.lang.String payloadProfile) {
    this.payloadProfile = payloadProfile;
  }

  public java.lang.String getErrorHandling() {
    return this.errorHandling;
  }

  public void setErrorHandling(java.lang.String errorHandling) {
    this.errorHandling = errorHandling;
  }

  public java.lang.String getCompressPayloads() {
    return this.compressPayloads;
  }

  public void setCompressPayloads(java.lang.String compressPayloads) {
    this.compressPayloads = compressPayloads;
  }

  private static final long serialVersionUID = 1L;



  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="name",required=true)
  protected java.lang.String name;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="service",required=true)
  protected java.lang.String service;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="action",required=true)
  protected java.lang.String action;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="security",required=true)
  protected java.lang.String security;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="defaultMpc",required=true)
  protected java.lang.String defaultMpc;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="receptionAwareness",required=true)
  protected java.lang.String receptionAwareness;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="reliability",required=true)
  protected java.lang.String reliability;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="propertySet",required=false)
  protected java.lang.String propertySet;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="payloadProfile",required=false)
  protected java.lang.String payloadProfile;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="errorHandling",required=true)
  protected java.lang.String errorHandling;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="compressPayloads",required=true)
  protected java.lang.String compressPayloads;

}
