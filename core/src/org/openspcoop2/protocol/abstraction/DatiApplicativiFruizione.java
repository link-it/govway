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
package org.openspcoop2.protocol.abstraction;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import org.openspcoop2.protocol.abstraction.constants.Autenticazione;
import java.io.Serializable;


/** <p>Java class for DatiApplicativiFruizione complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DatiApplicativiFruizione"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="basic-username" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="basic-password" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="ssl-subject" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/&gt;
 * 		&lt;/sequence&gt;
 * 		&lt;attribute name="autenticazione" type="{http://www.openspcoop2.org/protocol/abstraction}Autenticazione" use="required"/&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DatiApplicativiFruizione", 
  propOrder = {
  	"basicUsername",
  	"basicPassword",
  	"sslSubject"
  }
)

@XmlRootElement(name = "DatiApplicativiFruizione")

public class DatiApplicativiFruizione extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public DatiApplicativiFruizione() {
    super();
  }

  public java.lang.String getBasicUsername() {
    return this.basicUsername;
  }

  public void setBasicUsername(java.lang.String basicUsername) {
    this.basicUsername = basicUsername;
  }

  public java.lang.String getBasicPassword() {
    return this.basicPassword;
  }

  public void setBasicPassword(java.lang.String basicPassword) {
    this.basicPassword = basicPassword;
  }

  public java.lang.String getSslSubject() {
    return this.sslSubject;
  }

  public void setSslSubject(java.lang.String sslSubject) {
    this.sslSubject = sslSubject;
  }

  public void setAutenticazioneRawEnumValue(String value) {
    this.autenticazione = (Autenticazione) Autenticazione.toEnumConstantFromString(value);
  }

  public String getAutenticazioneRawEnumValue() {
    if(this.autenticazione == null){
    	return null;
    }else{
    	return this.autenticazione.toString();
    }
  }

  public org.openspcoop2.protocol.abstraction.constants.Autenticazione getAutenticazione() {
    return this.autenticazione;
  }

  public void setAutenticazione(org.openspcoop2.protocol.abstraction.constants.Autenticazione autenticazione) {
    this.autenticazione = autenticazione;
  }

  private static final long serialVersionUID = 1L;



  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="basic-username",required=false,nillable=false)
  protected java.lang.String basicUsername;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="basic-password",required=false,nillable=false)
  protected java.lang.String basicPassword;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="ssl-subject",required=false,nillable=false)
  protected java.lang.String sslSubject;

  @jakarta.xml.bind.annotation.XmlTransient
  protected java.lang.String autenticazioneRawEnumValue;

  @XmlAttribute(name="autenticazione",required=true)
  protected Autenticazione autenticazione;

}
