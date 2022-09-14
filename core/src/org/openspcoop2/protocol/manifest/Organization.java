/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it).
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


/** <p>Java class for Organization complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Organization"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="types" type="{http://www.openspcoop2.org/protocol/manifest}OrganizationTypes" minOccurs="1" maxOccurs="1"/&gt;
 * 		&lt;/sequence&gt;
 * 		&lt;attribute name="authentication" type="{http://www.w3.org/2001/XMLSchema}boolean" use="optional" default="true"/&gt;
 * 		&lt;attribute name="httpsWithTokenAuthentication" type="{http://www.w3.org/2001/XMLSchema}boolean" use="optional" default="false"/&gt;
 * 		&lt;attribute name="inboundApplicativeAuthentication" type="{http://www.w3.org/2001/XMLSchema}boolean" use="optional" default="true"/&gt;
 * 		&lt;attribute name="inboundOrganizationAuthorizationWithoutAuthentication" type="{http://www.w3.org/2001/XMLSchema}boolean" use="optional" default="false"/&gt;
 * 		&lt;attribute name="inboundExternalApplicationAuthentication" type="{http://www.w3.org/2001/XMLSchema}boolean" use="optional" default="false"/&gt;
 * 		&lt;attribute name="codeDomain" type="{http://www.w3.org/2001/XMLSchema}boolean" use="optional" default="false"/&gt;
 * 		&lt;attribute name="codeIPA" type="{http://www.w3.org/2001/XMLSchema}boolean" use="optional" default="false"/&gt;
 * 		&lt;attribute name="replyToAddress" type="{http://www.w3.org/2001/XMLSchema}boolean" use="optional" default="false"/&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Organization", 
  propOrder = {
  	"types"
  }
)

@XmlRootElement(name = "Organization")

public class Organization extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public Organization() {
  }

  public OrganizationTypes getTypes() {
    return this.types;
  }

  public void setTypes(OrganizationTypes types) {
    this.types = types;
  }

  public boolean isAuthentication() {
    return this.authentication;
  }

  public boolean getAuthentication() {
    return this.authentication;
  }

  public void setAuthentication(boolean authentication) {
    this.authentication = authentication;
  }

  public boolean isHttpsWithTokenAuthentication() {
    return this.httpsWithTokenAuthentication;
  }

  public boolean getHttpsWithTokenAuthentication() {
    return this.httpsWithTokenAuthentication;
  }

  public void setHttpsWithTokenAuthentication(boolean httpsWithTokenAuthentication) {
    this.httpsWithTokenAuthentication = httpsWithTokenAuthentication;
  }

  public boolean isInboundApplicativeAuthentication() {
    return this.inboundApplicativeAuthentication;
  }

  public boolean getInboundApplicativeAuthentication() {
    return this.inboundApplicativeAuthentication;
  }

  public void setInboundApplicativeAuthentication(boolean inboundApplicativeAuthentication) {
    this.inboundApplicativeAuthentication = inboundApplicativeAuthentication;
  }

  public boolean isInboundOrganizationAuthorizationWithoutAuthentication() {
    return this.inboundOrganizationAuthorizationWithoutAuthentication;
  }

  public boolean getInboundOrganizationAuthorizationWithoutAuthentication() {
    return this.inboundOrganizationAuthorizationWithoutAuthentication;
  }

  public void setInboundOrganizationAuthorizationWithoutAuthentication(boolean inboundOrganizationAuthorizationWithoutAuthentication) {
    this.inboundOrganizationAuthorizationWithoutAuthentication = inboundOrganizationAuthorizationWithoutAuthentication;
  }

  public boolean isInboundExternalApplicationAuthentication() {
    return this.inboundExternalApplicationAuthentication;
  }

  public boolean getInboundExternalApplicationAuthentication() {
    return this.inboundExternalApplicationAuthentication;
  }

  public void setInboundExternalApplicationAuthentication(boolean inboundExternalApplicationAuthentication) {
    this.inboundExternalApplicationAuthentication = inboundExternalApplicationAuthentication;
  }

  public boolean isCodeDomain() {
    return this.codeDomain;
  }

  public boolean getCodeDomain() {
    return this.codeDomain;
  }

  public void setCodeDomain(boolean codeDomain) {
    this.codeDomain = codeDomain;
  }

  public boolean isCodeIPA() {
    return this.codeIPA;
  }

  public boolean getCodeIPA() {
    return this.codeIPA;
  }

  public void setCodeIPA(boolean codeIPA) {
    this.codeIPA = codeIPA;
  }

  public boolean isReplyToAddress() {
    return this.replyToAddress;
  }

  public boolean getReplyToAddress() {
    return this.replyToAddress;
  }

  public void setReplyToAddress(boolean replyToAddress) {
    this.replyToAddress = replyToAddress;
  }

  private static final long serialVersionUID = 1L;



  @XmlElement(name="types",required=true,nillable=false)
  protected OrganizationTypes types;

  @javax.xml.bind.annotation.XmlSchemaType(name="boolean")
  @XmlAttribute(name="authentication",required=false)
  protected boolean authentication = true;

  @javax.xml.bind.annotation.XmlSchemaType(name="boolean")
  @XmlAttribute(name="httpsWithTokenAuthentication",required=false)
  protected boolean httpsWithTokenAuthentication = false;

  @javax.xml.bind.annotation.XmlSchemaType(name="boolean")
  @XmlAttribute(name="inboundApplicativeAuthentication",required=false)
  protected boolean inboundApplicativeAuthentication = true;

  @javax.xml.bind.annotation.XmlSchemaType(name="boolean")
  @XmlAttribute(name="inboundOrganizationAuthorizationWithoutAuthentication",required=false)
  protected boolean inboundOrganizationAuthorizationWithoutAuthentication = false;

  @javax.xml.bind.annotation.XmlSchemaType(name="boolean")
  @XmlAttribute(name="inboundExternalApplicationAuthentication",required=false)
  protected boolean inboundExternalApplicationAuthentication = false;

  @javax.xml.bind.annotation.XmlSchemaType(name="boolean")
  @XmlAttribute(name="codeDomain",required=false)
  protected boolean codeDomain = false;

  @javax.xml.bind.annotation.XmlSchemaType(name="boolean")
  @XmlAttribute(name="codeIPA",required=false)
  protected boolean codeIPA = false;

  @javax.xml.bind.annotation.XmlSchemaType(name="boolean")
  @XmlAttribute(name="replyToAddress",required=false)
  protected boolean replyToAddress = false;

}
