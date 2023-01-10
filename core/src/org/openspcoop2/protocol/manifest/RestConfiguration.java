/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it).
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
package org.openspcoop2.protocol.manifest;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;


/** <p>Java class for RestConfiguration complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="RestConfiguration"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="integration" type="{http://www.openspcoop2.org/protocol/manifest}Integration" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="integrationError" type="{http://www.openspcoop2.org/protocol/manifest}IntegrationErrorConfiguration" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="mediaTypeCollection" type="{http://www.openspcoop2.org/protocol/manifest}RestMediaTypeCollection" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="interfaces" type="{http://www.openspcoop2.org/protocol/manifest}InterfacesConfiguration" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="profile" type="{http://www.openspcoop2.org/protocol/manifest}RestCollaborationProfile" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="functionality" type="{http://www.openspcoop2.org/protocol/manifest}Functionality" minOccurs="0" maxOccurs="1"/&gt;
 * 		&lt;/sequence&gt;
 * 		&lt;attribute name="xml" type="{http://www.w3.org/2001/XMLSchema}boolean" use="required"/&gt;
 * 		&lt;attribute name="json" type="{http://www.w3.org/2001/XMLSchema}boolean" use="required"/&gt;
 * 		&lt;attribute name="binary" type="{http://www.w3.org/2001/XMLSchema}boolean" use="required"/&gt;
 * 		&lt;attribute name="mimeMultipart" type="{http://www.w3.org/2001/XMLSchema}boolean" use="optional" default="false"/&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RestConfiguration", 
  propOrder = {
  	"integration",
  	"integrationError",
  	"mediaTypeCollection",
  	"interfaces",
  	"profile",
  	"functionality"
  }
)

@XmlRootElement(name = "RestConfiguration")

public class RestConfiguration extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public RestConfiguration() {
  }

  public Integration getIntegration() {
    return this.integration;
  }

  public void setIntegration(Integration integration) {
    this.integration = integration;
  }

  public IntegrationErrorConfiguration getIntegrationError() {
    return this.integrationError;
  }

  public void setIntegrationError(IntegrationErrorConfiguration integrationError) {
    this.integrationError = integrationError;
  }

  public RestMediaTypeCollection getMediaTypeCollection() {
    return this.mediaTypeCollection;
  }

  public void setMediaTypeCollection(RestMediaTypeCollection mediaTypeCollection) {
    this.mediaTypeCollection = mediaTypeCollection;
  }

  public InterfacesConfiguration getInterfaces() {
    return this.interfaces;
  }

  public void setInterfaces(InterfacesConfiguration interfaces) {
    this.interfaces = interfaces;
  }

  public RestCollaborationProfile getProfile() {
    return this.profile;
  }

  public void setProfile(RestCollaborationProfile profile) {
    this.profile = profile;
  }

  public Functionality getFunctionality() {
    return this.functionality;
  }

  public void setFunctionality(Functionality functionality) {
    this.functionality = functionality;
  }

  public boolean isXml() {
    return this.xml;
  }

  public boolean getXml() {
    return this.xml;
  }

  public void setXml(boolean xml) {
    this.xml = xml;
  }

  public boolean isJson() {
    return this.json;
  }

  public boolean getJson() {
    return this.json;
  }

  public void setJson(boolean json) {
    this.json = json;
  }

  public boolean isBinary() {
    return this.binary;
  }

  public boolean getBinary() {
    return this.binary;
  }

  public void setBinary(boolean binary) {
    this.binary = binary;
  }

  public boolean isMimeMultipart() {
    return this.mimeMultipart;
  }

  public boolean getMimeMultipart() {
    return this.mimeMultipart;
  }

  public void setMimeMultipart(boolean mimeMultipart) {
    this.mimeMultipart = mimeMultipart;
  }

  private static final long serialVersionUID = 1L;



  @XmlElement(name="integration",required=true,nillable=false)
  protected Integration integration;

  @XmlElement(name="integrationError",required=true,nillable=false)
  protected IntegrationErrorConfiguration integrationError;

  @XmlElement(name="mediaTypeCollection",required=false,nillable=false)
  protected RestMediaTypeCollection mediaTypeCollection;

  @XmlElement(name="interfaces",required=false,nillable=false)
  protected InterfacesConfiguration interfaces;

  @XmlElement(name="profile",required=false,nillable=false)
  protected RestCollaborationProfile profile;

  @XmlElement(name="functionality",required=false,nillable=false)
  protected Functionality functionality;

  @javax.xml.bind.annotation.XmlSchemaType(name="boolean")
  @XmlAttribute(name="xml",required=true)
  protected boolean xml;

  @javax.xml.bind.annotation.XmlSchemaType(name="boolean")
  @XmlAttribute(name="json",required=true)
  protected boolean json;

  @javax.xml.bind.annotation.XmlSchemaType(name="boolean")
  @XmlAttribute(name="binary",required=true)
  protected boolean binary;

  @javax.xml.bind.annotation.XmlSchemaType(name="boolean")
  @XmlAttribute(name="mimeMultipart",required=false)
  protected boolean mimeMultipart = false;

}
