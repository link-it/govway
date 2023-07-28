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
package org.openspcoop2.core.allarmi;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import java.io.Serializable;


/** <p>Java class for allarme-mail complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="allarme-mail"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="invia" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="invia-warning" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="destinatari" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="subject" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="body" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/&gt;
 * 		&lt;/sequence&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "allarme-mail", 
  propOrder = {
  	"invia",
  	"inviaWarning",
  	"destinatari",
  	"subject",
  	"body"
  }
)

@XmlRootElement(name = "allarme-mail")

public class AllarmeMail extends org.openspcoop2.utils.beans.BaseBeanWithId implements Serializable , Cloneable {
  public AllarmeMail() {
    super();
  }

  public java.lang.Integer getInvia() {
    return this.invia;
  }

  public void setInvia(java.lang.Integer invia) {
    this.invia = invia;
  }

  public java.lang.Integer getInviaWarning() {
    return this.inviaWarning;
  }

  public void setInviaWarning(java.lang.Integer inviaWarning) {
    this.inviaWarning = inviaWarning;
  }

  public java.lang.String getDestinatari() {
    return this.destinatari;
  }

  public void setDestinatari(java.lang.String destinatari) {
    this.destinatari = destinatari;
  }

  public java.lang.String getSubject() {
    return this.subject;
  }

  public void setSubject(java.lang.String subject) {
    this.subject = subject;
  }

  public java.lang.String getBody() {
    return this.body;
  }

  public void setBody(java.lang.String body) {
    this.body = body;
  }

  private static final long serialVersionUID = 1L;



  @jakarta.xml.bind.annotation.XmlSchemaType(name="integer")
  @XmlElement(name="invia",required=false,nillable=false)
  protected java.lang.Integer invia;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="integer")
  @XmlElement(name="invia-warning",required=false,nillable=false)
  protected java.lang.Integer inviaWarning;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="destinatari",required=false,nillable=false)
  protected java.lang.String destinatari;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="subject",required=false,nillable=false)
  protected java.lang.String subject;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="body",required=false,nillable=false)
  protected java.lang.String body;

}
