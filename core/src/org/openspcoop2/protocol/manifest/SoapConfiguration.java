/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2016 Link.it srl (http://link.it).
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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


/** <p>Java class for SoapConfiguration complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SoapConfiguration">
 * 		&lt;sequence>
 * 			&lt;element name="integrationError" type="{http://www.openspcoop2.org/protocol/manifest}IntegrationErrorConfiguration" minOccurs="1" maxOccurs="1"/>
 * 			&lt;element name="mediaTypeCollection" type="{http://www.openspcoop2.org/protocol/manifest}SoapMediaTypeCollection" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="soapHeaderBypassMustUnderstand" type="{http://www.openspcoop2.org/protocol/manifest}SoapHeaderBypassMustUnderstand" minOccurs="0" maxOccurs="1"/>
 * 		&lt;/sequence>
 * 		&lt;attribute name="soap11" type="{http://www.w3.org/2001/XMLSchema}boolean" use="required"/>
 * 		&lt;attribute name="soap11_withAttachments" type="{http://www.w3.org/2001/XMLSchema}boolean" use="optional" default="true"/>
 * 		&lt;attribute name="soap11_mtom" type="{http://www.w3.org/2001/XMLSchema}boolean" use="optional" default="true"/>
 * 		&lt;attribute name="soap12" type="{http://www.w3.org/2001/XMLSchema}boolean" use="required"/>
 * 		&lt;attribute name="soap12_withAttachments" type="{http://www.w3.org/2001/XMLSchema}boolean" use="optional" default="true"/>
 * 		&lt;attribute name="soap12_mtom" type="{http://www.w3.org/2001/XMLSchema}boolean" use="optional" default="true"/>
 * &lt;/complexType>
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
  	"integrationError",
  	"mediaTypeCollection",
  	"soapHeaderBypassMustUnderstand"
  }
)

@XmlRootElement(name = "SoapConfiguration")

public class SoapConfiguration extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public SoapConfiguration() {
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



  @XmlElement(name="integrationError",required=true,nillable=false)
  protected IntegrationErrorConfiguration integrationError;

  @XmlElement(name="mediaTypeCollection",required=false,nillable=false)
  protected SoapMediaTypeCollection mediaTypeCollection;

  @XmlElement(name="soapHeaderBypassMustUnderstand",required=false,nillable=false)
  protected SoapHeaderBypassMustUnderstand soapHeaderBypassMustUnderstand;

  @javax.xml.bind.annotation.XmlSchemaType(name="boolean")
  @XmlAttribute(name="soap11",required=true)
  protected boolean soap11;

  @javax.xml.bind.annotation.XmlSchemaType(name="boolean")
  @XmlAttribute(name="soap11_withAttachments",required=false)
  protected boolean soap11WithAttachments = true;

  @javax.xml.bind.annotation.XmlSchemaType(name="boolean")
  @XmlAttribute(name="soap11_mtom",required=false)
  protected boolean soap11Mtom = true;

  @javax.xml.bind.annotation.XmlSchemaType(name="boolean")
  @XmlAttribute(name="soap12",required=true)
  protected boolean soap12;

  @javax.xml.bind.annotation.XmlSchemaType(name="boolean")
  @XmlAttribute(name="soap12_withAttachments",required=false)
  protected boolean soap12WithAttachments = true;

  @javax.xml.bind.annotation.XmlSchemaType(name="boolean")
  @XmlAttribute(name="soap12_mtom",required=false)
  protected boolean soap12Mtom = true;

}
