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
package org.openspcoop2.core.config;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import java.io.Serializable;


/** <p>Java class for gestione-token-autenticazione complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="gestione-token-autenticazione"&gt;
 * 		&lt;attribute name="issuer" type="{http://www.openspcoop2.org/core/config}StatoFunzionalita" use="optional" default="disabilitato"/&gt;
 * 		&lt;attribute name="client-id" type="{http://www.openspcoop2.org/core/config}StatoFunzionalita" use="optional" default="disabilitato"/&gt;
 * 		&lt;attribute name="subject" type="{http://www.openspcoop2.org/core/config}StatoFunzionalita" use="optional" default="disabilitato"/&gt;
 * 		&lt;attribute name="username" type="{http://www.openspcoop2.org/core/config}StatoFunzionalita" use="optional" default="disabilitato"/&gt;
 * 		&lt;attribute name="email" type="{http://www.openspcoop2.org/core/config}StatoFunzionalita" use="optional" default="disabilitato"/&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "gestione-token-autenticazione")

@XmlRootElement(name = "gestione-token-autenticazione")

public class GestioneTokenAutenticazione extends org.openspcoop2.utils.beans.BaseBeanWithId implements Serializable , Cloneable {
  public GestioneTokenAutenticazione() {
    super();
  }

  public void setIssuerRawEnumValue(String value) {
    this.issuer = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString(value);
  }

  public String getIssuerRawEnumValue() {
    if(this.issuer == null){
    	return null;
    }else{
    	return this.issuer.toString();
    }
  }

  public org.openspcoop2.core.config.constants.StatoFunzionalita getIssuer() {
    return this.issuer;
  }

  public void setIssuer(org.openspcoop2.core.config.constants.StatoFunzionalita issuer) {
    this.issuer = issuer;
  }

  public void setClientIdRawEnumValue(String value) {
    this.clientId = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString(value);
  }

  public String getClientIdRawEnumValue() {
    if(this.clientId == null){
    	return null;
    }else{
    	return this.clientId.toString();
    }
  }

  public org.openspcoop2.core.config.constants.StatoFunzionalita getClientId() {
    return this.clientId;
  }

  public void setClientId(org.openspcoop2.core.config.constants.StatoFunzionalita clientId) {
    this.clientId = clientId;
  }

  public void setSubjectRawEnumValue(String value) {
    this.subject = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString(value);
  }

  public String getSubjectRawEnumValue() {
    if(this.subject == null){
    	return null;
    }else{
    	return this.subject.toString();
    }
  }

  public org.openspcoop2.core.config.constants.StatoFunzionalita getSubject() {
    return this.subject;
  }

  public void setSubject(org.openspcoop2.core.config.constants.StatoFunzionalita subject) {
    this.subject = subject;
  }

  public void setUsernameRawEnumValue(String value) {
    this.username = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString(value);
  }

  public String getUsernameRawEnumValue() {
    if(this.username == null){
    	return null;
    }else{
    	return this.username.toString();
    }
  }

  public org.openspcoop2.core.config.constants.StatoFunzionalita getUsername() {
    return this.username;
  }

  public void setUsername(org.openspcoop2.core.config.constants.StatoFunzionalita username) {
    this.username = username;
  }

  public void setEmailRawEnumValue(String value) {
    this.email = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString(value);
  }

  public String getEmailRawEnumValue() {
    if(this.email == null){
    	return null;
    }else{
    	return this.email.toString();
    }
  }

  public org.openspcoop2.core.config.constants.StatoFunzionalita getEmail() {
    return this.email;
  }

  public void setEmail(org.openspcoop2.core.config.constants.StatoFunzionalita email) {
    this.email = email;
  }

  private static final long serialVersionUID = 1L;



  @jakarta.xml.bind.annotation.XmlTransient
  protected java.lang.String issuerRawEnumValue;

  @XmlAttribute(name="issuer",required=false)
  protected StatoFunzionalita issuer = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString("disabilitato");

  @jakarta.xml.bind.annotation.XmlTransient
  protected java.lang.String clientIdRawEnumValue;

  @XmlAttribute(name="client-id",required=false)
  protected StatoFunzionalita clientId = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString("disabilitato");

  @jakarta.xml.bind.annotation.XmlTransient
  protected java.lang.String subjectRawEnumValue;

  @XmlAttribute(name="subject",required=false)
  protected StatoFunzionalita subject = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString("disabilitato");

  @jakarta.xml.bind.annotation.XmlTransient
  protected java.lang.String usernameRawEnumValue;

  @XmlAttribute(name="username",required=false)
  protected StatoFunzionalita username = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString("disabilitato");

  @jakarta.xml.bind.annotation.XmlTransient
  protected java.lang.String emailRawEnumValue;

  @XmlAttribute(name="email",required=false)
  protected StatoFunzionalita email = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString("disabilitato");

}
