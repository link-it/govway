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
package org.openspcoop2.core.registry;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import org.openspcoop2.core.registry.constants.CredenzialeTipo;
import java.io.Serializable;


/** <p>Java class for credenziali-soggetto complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="credenziali-soggetto"&gt;
 * 		&lt;attribute name="tipo" type="{http://www.openspcoop2.org/core/registry}CredenzialeTipo" use="optional" default="ssl"/&gt;
 * 		&lt;attribute name="user" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/&gt;
 * 		&lt;attribute name="password" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/&gt;
 * 		&lt;attribute name="app-id" type="{http://www.w3.org/2001/XMLSchema}boolean" use="optional" default="false"/&gt;
 * 		&lt;attribute name="subject" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/&gt;
 * 		&lt;attribute name="cn-subject" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/&gt;
 * 		&lt;attribute name="issuer" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/&gt;
 * 		&lt;attribute name="cn-issuer" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/&gt;
 * 		&lt;attribute name="certificate" type="{http://www.w3.org/2001/XMLSchema}base64Binary" use="optional"/&gt;
 * 		&lt;attribute name="certificate-strict-verification" type="{http://www.w3.org/2001/XMLSchema}boolean" use="optional" default="false"/&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "credenziali-soggetto")

@XmlRootElement(name = "credenziali-soggetto")

public class CredenzialiSoggetto extends org.openspcoop2.utils.beans.BaseBeanWithId implements Serializable , Cloneable {
  public CredenzialiSoggetto() {
    super();
  }

  public void setTipoRawEnumValue(String value) {
    this.tipo = (CredenzialeTipo) CredenzialeTipo.toEnumConstantFromString(value);
  }

  public String getTipoRawEnumValue() {
    if(this.tipo == null){
    	return null;
    }else{
    	return this.tipo.toString();
    }
  }

  public org.openspcoop2.core.registry.constants.CredenzialeTipo getTipo() {
    return this.tipo;
  }

  public void setTipo(org.openspcoop2.core.registry.constants.CredenzialeTipo tipo) {
    this.tipo = tipo;
  }

  public java.lang.String getUser() {
    return this.user;
  }

  public void setUser(java.lang.String user) {
    this.user = user;
  }

  public java.lang.String getPassword() {
    return this.password;
  }

  public void setPassword(java.lang.String password) {
    this.password = password;
  }

  public boolean isAppId() {
    return this.appId;
  }

  public boolean getAppId() {
    return this.appId;
  }

  public void setAppId(boolean appId) {
    this.appId = appId;
  }

  public java.lang.String getSubject() {
    return this.subject;
  }

  public void setSubject(java.lang.String subject) {
    this.subject = subject;
  }

  public java.lang.String getCnSubject() {
    return this.cnSubject;
  }

  public void setCnSubject(java.lang.String cnSubject) {
    this.cnSubject = cnSubject;
  }

  public java.lang.String getIssuer() {
    return this.issuer;
  }

  public void setIssuer(java.lang.String issuer) {
    this.issuer = issuer;
  }

  public java.lang.String getCnIssuer() {
    return this.cnIssuer;
  }

  public void setCnIssuer(java.lang.String cnIssuer) {
    this.cnIssuer = cnIssuer;
  }

  public byte[] getCertificate() {
    return this.certificate;
  }

  public void setCertificate(byte[] certificate) {
    this.certificate = certificate;
  }

  public boolean isCertificateStrictVerification() {
    return this.certificateStrictVerification;
  }

  public boolean getCertificateStrictVerification() {
    return this.certificateStrictVerification;
  }

  public void setCertificateStrictVerification(boolean certificateStrictVerification) {
    this.certificateStrictVerification = certificateStrictVerification;
  }

  private static final long serialVersionUID = 1L;



  @jakarta.xml.bind.annotation.XmlTransient
  protected java.lang.String tipoRawEnumValue;

  @XmlAttribute(name="tipo",required=false)
  protected CredenzialeTipo tipo = (CredenzialeTipo) CredenzialeTipo.toEnumConstantFromString("ssl");

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="user",required=false)
  protected java.lang.String user;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="password",required=false)
  protected java.lang.String password;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="boolean")
  @XmlAttribute(name="app-id",required=false)
  protected boolean appId = false;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="subject",required=false)
  protected java.lang.String subject;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="cn-subject",required=false)
  protected java.lang.String cnSubject;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="issuer",required=false)
  protected java.lang.String issuer;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="cn-issuer",required=false)
  protected java.lang.String cnIssuer;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="base64Binary")
  @XmlAttribute(name="certificate",required=false)
  protected byte[] certificate;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="boolean")
  @XmlAttribute(name="certificate-strict-verification",required=false)
  protected boolean certificateStrictVerification = false;

}
