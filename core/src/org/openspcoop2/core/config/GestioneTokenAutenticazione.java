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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
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

  public void set_value_issuer(String value) {
    this.issuer = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString(value);
  }

  public String get_value_issuer() {
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

  public void set_value_clientId(String value) {
    this.clientId = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString(value);
  }

  public String get_value_clientId() {
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

  public void set_value_subject(String value) {
    this.subject = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString(value);
  }

  public String get_value_subject() {
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

  public void set_value_username(String value) {
    this.username = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString(value);
  }

  public String get_value_username() {
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

  public void set_value_email(String value) {
    this.email = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString(value);
  }

  public String get_value_email() {
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



  @javax.xml.bind.annotation.XmlTransient
  protected java.lang.String _value_issuer;

  @XmlAttribute(name="issuer",required=false)
  protected StatoFunzionalita issuer = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString("disabilitato");

  @javax.xml.bind.annotation.XmlTransient
  protected java.lang.String _value_clientId;

  @XmlAttribute(name="client-id",required=false)
  protected StatoFunzionalita clientId = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString("disabilitato");

  @javax.xml.bind.annotation.XmlTransient
  protected java.lang.String _value_subject;

  @XmlAttribute(name="subject",required=false)
  protected StatoFunzionalita subject = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString("disabilitato");

  @javax.xml.bind.annotation.XmlTransient
  protected java.lang.String _value_username;

  @XmlAttribute(name="username",required=false)
  protected StatoFunzionalita username = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString("disabilitato");

  @javax.xml.bind.annotation.XmlTransient
  protected java.lang.String _value_email;

  @XmlAttribute(name="email",required=false)
  protected StatoFunzionalita email = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString("disabilitato");

}
