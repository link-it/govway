/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it).
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
import org.openspcoop2.core.config.constants.CredenzialeTipo;
import java.io.Serializable;


/** <p>Java class for credenziali complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="credenziali"&gt;
 * 		&lt;attribute name="tipo" type="{http://www.openspcoop2.org/core/config}CredenzialeTipo" use="optional" default="ssl"/&gt;
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
@XmlType(name = "credenziali")

@XmlRootElement(name = "credenziali")

public class Credenziali extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public Credenziali() {
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

  public void set_value_tipo(String value) {
    this.tipo = (CredenzialeTipo) CredenzialeTipo.toEnumConstantFromString(value);
  }

  public String get_value_tipo() {
    if(this.tipo == null){
    	return null;
    }else{
    	return this.tipo.toString();
    }
  }

  public org.openspcoop2.core.config.constants.CredenzialeTipo getTipo() {
    return this.tipo;
  }

  public void setTipo(org.openspcoop2.core.config.constants.CredenzialeTipo tipo) {
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

  @XmlTransient
  private Long id;



  @javax.xml.bind.annotation.XmlTransient
  protected java.lang.String _value_tipo;

  @XmlAttribute(name="tipo",required=false)
  protected CredenzialeTipo tipo = (CredenzialeTipo) CredenzialeTipo.toEnumConstantFromString("ssl");

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="user",required=false)
  protected java.lang.String user;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="password",required=false)
  protected java.lang.String password;

  @javax.xml.bind.annotation.XmlSchemaType(name="boolean")
  @XmlAttribute(name="app-id",required=false)
  protected boolean appId = false;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="subject",required=false)
  protected java.lang.String subject;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="cn-subject",required=false)
  protected java.lang.String cnSubject;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="issuer",required=false)
  protected java.lang.String issuer;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="cn-issuer",required=false)
  protected java.lang.String cnIssuer;

  @javax.xml.bind.annotation.XmlSchemaType(name="base64Binary")
  @XmlAttribute(name="certificate",required=false)
  protected byte[] certificate;

  @javax.xml.bind.annotation.XmlSchemaType(name="boolean")
  @XmlAttribute(name="certificate-strict-verification",required=false)
  protected boolean certificateStrictVerification = false;

}
