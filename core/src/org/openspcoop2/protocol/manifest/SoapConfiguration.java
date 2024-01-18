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
package org.openspcoop2.protocol.manifest;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import java.io.Serializable;


/** <p>Java class for SoapConfiguration complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SoapConfiguration"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="integration" type="{http://www.openspcoop2.org/protocol/manifest}Integration" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="integrationError" type="{http://www.openspcoop2.org/protocol/manifest}IntegrationErrorConfiguration" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="mediaTypeCollection" type="{http://www.openspcoop2.org/protocol/manifest}SoapMediaTypeCollection" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="interfaces" type="{http://www.openspcoop2.org/protocol/manifest}InterfacesConfiguration" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="profile" type="{http://www.openspcoop2.org/protocol/manifest}CollaborationProfile" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="functionality" type="{http://www.openspcoop2.org/protocol/manifest}Functionality" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="soapHeaderBypassMustUnderstand" type="{http://www.openspcoop2.org/protocol/manifest}SoapHeaderBypassMustUnderstand" minOccurs="0" maxOccurs="1"/&gt;
 * 		&lt;/sequence&gt;
 * 		&lt;attribute name="soap11" type="{http://www.w3.org/2001/XMLSchema}boolean" use="required"/&gt;
 * 		&lt;attribute name="soap11_withAttachments" type="{http://www.w3.org/2001/XMLSchema}boolean" use="optional" default="true"/&gt;
 * 		&lt;attribute name="soap11_mtom" type="{http://www.w3.org/2001/XMLSchema}boolean" use="optional" default="true"/&gt;
 * 		&lt;attribute name="soap12" type="{http://www.w3.org/2001/XMLSchema}boolean" use="required"/&gt;
 * 		&lt;attribute name="soap12_withAttachments" type="{http://www.w3.org/2001/XMLSchema}boolean" use="optional" default="true"/&gt;
 * 		&lt;attribute name="soap12_mtom" type="{http://www.w3.org/2001/XMLSchema}boolean" use="optional" default="true"/&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SoapConfiguration", 
  propOrder = {
  	"integration",
  	"integrationError",
  	"mediaTypeCollection",
  	"interfaces",
  	"profile",
  	"functionality",
  	"soapHeaderBypassMustUnderstand"
  }
)

@XmlRootElement(name = "SoapConfiguration")

public class SoapConfiguration extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public SoapConfiguration() {
    super();
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

  public SoapMediaTypeCollection getMediaTypeCollection() {
    return this.mediaTypeCollection;
  }

  public void setMediaTypeCollection(SoapMediaTypeCollection mediaTypeCollection) {
    this.mediaTypeCollection = mediaTypeCollection;
  }

  public InterfacesConfiguration getInterfaces() {
    return this.interfaces;
  }

  public void setInterfaces(InterfacesConfiguration interfaces) {
    this.interfaces = interfaces;
  }

  public CollaborationProfile getProfile() {
    return this.profile;
  }

  public void setProfile(CollaborationProfile profile) {
    this.profile = profile;
  }

  public Functionality getFunctionality() {
    return this.functionality;
  }

  public void setFunctionality(Functionality functionality) {
    this.functionality = functionality;
  }

  public SoapHeaderBypassMustUnderstand getSoapHeaderBypassMustUnderstand() {
    return this.soapHeaderBypassMustUnderstand;
  }

  public void setSoapHeaderBypassMustUnderstand(SoapHeaderBypassMustUnderstand soapHeaderBypassMustUnderstand) {
    this.soapHeaderBypassMustUnderstand = soapHeaderBypassMustUnderstand;
  }

  public boolean isSoap11() {
    return this.soap11;
  }

  public boolean getSoap11() {
    return this.soap11;
  }

  public void setSoap11(boolean soap11) {
    this.soap11 = soap11;
  }

  public boolean isSoap11WithAttachments() {
    return this.soap11WithAttachments;
  }

  public boolean getSoap11WithAttachments() {
    return this.soap11WithAttachments;
  }

  public void setSoap11WithAttachments(boolean soap11WithAttachments) {
    this.soap11WithAttachments = soap11WithAttachments;
  }

  public boolean isSoap11Mtom() {
    return this.soap11Mtom;
  }

  public boolean getSoap11Mtom() {
    return this.soap11Mtom;
  }

  public void setSoap11Mtom(boolean soap11Mtom) {
    this.soap11Mtom = soap11Mtom;
  }

  public boolean isSoap12() {
    return this.soap12;
  }

  public boolean getSoap12() {
    return this.soap12;
  }

  public void setSoap12(boolean soap12) {
    this.soap12 = soap12;
  }

  public boolean isSoap12WithAttachments() {
    return this.soap12WithAttachments;
  }

  public boolean getSoap12WithAttachments() {
    return this.soap12WithAttachments;
  }

  public void setSoap12WithAttachments(boolean soap12WithAttachments) {
    this.soap12WithAttachments = soap12WithAttachments;
  }

  public boolean isSoap12Mtom() {
    return this.soap12Mtom;
  }

  public boolean getSoap12Mtom() {
    return this.soap12Mtom;
  }

  public void setSoap12Mtom(boolean soap12Mtom) {
    this.soap12Mtom = soap12Mtom;
  }

  private static final long serialVersionUID = 1L;



  @XmlElement(name="integration",required=true,nillable=false)
  protected Integration integration;

  @XmlElement(name="integrationError",required=true,nillable=false)
  protected IntegrationErrorConfiguration integrationError;

  @XmlElement(name="mediaTypeCollection",required=false,nillable=false)
  protected SoapMediaTypeCollection mediaTypeCollection;

  @XmlElement(name="interfaces",required=false,nillable=false)
  protected InterfacesConfiguration interfaces;

  @XmlElement(name="profile",required=false,nillable=false)
  protected CollaborationProfile profile;

  @XmlElement(name="functionality",required=false,nillable=false)
  protected Functionality functionality;

  @XmlElement(name="soapHeaderBypassMustUnderstand",required=false,nillable=false)
  protected SoapHeaderBypassMustUnderstand soapHeaderBypassMustUnderstand;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="boolean")
  @XmlAttribute(name="soap11",required=true)
  protected boolean soap11;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="boolean")
  @XmlAttribute(name="soap11_withAttachments",required=false)
  protected boolean soap11WithAttachments = true;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="boolean")
  @XmlAttribute(name="soap11_mtom",required=false)
  protected boolean soap11Mtom = true;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="boolean")
  @XmlAttribute(name="soap12",required=true)
  protected boolean soap12;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="boolean")
  @XmlAttribute(name="soap12_withAttachments",required=false)
  protected boolean soap12WithAttachments = true;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="boolean")
  @XmlAttribute(name="soap12_mtom",required=false)
  protected boolean soap12Mtom = true;

}
