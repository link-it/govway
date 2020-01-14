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
package org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;


/** <p>Java class for MessageInfo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="MessageInfo">
 * 		&lt;sequence>
 * 			&lt;element name="Timestamp" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="1" maxOccurs="1"/>
 * 			&lt;element name="MessageId" type="{http://docs.oasis-open.org/ebxml-msg/ebms/v3.0/ns/core/200704/}string" minOccurs="1" maxOccurs="1"/>
 * 			&lt;element name="RefToMessageId" type="{http://docs.oasis-open.org/ebxml-msg/ebms/v3.0/ns/core/200704/}string" minOccurs="0" maxOccurs="1"/>
 * 		&lt;/sequence>
 * &lt;/complexType>
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MessageInfo", 
  propOrder = {
  	"timestamp",
  	"messageId",
  	"refToMessageId"
  }
)

@XmlRootElement(name = "MessageInfo")

public class MessageInfo extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public MessageInfo() {
  }

  public java.util.Date getTimestamp() {
    return this.timestamp;
  }

  public void setTimestamp(java.util.Date timestamp) {
    this.timestamp = timestamp;
  }

  public java.lang.String getMessageId() {
    return this.messageId;
  }

  public void setMessageId(java.lang.String messageId) {
    this.messageId = messageId;
  }

  public java.lang.String getRefToMessageId() {
    return this.refToMessageId;
  }

  public void setRefToMessageId(java.lang.String refToMessageId) {
    this.refToMessageId = refToMessageId;
  }

  private static final long serialVersionUID = 1L;



  @javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(org.openspcoop2.utils.jaxb.DateTime2String.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="dateTime")
  @XmlElement(name="Timestamp",required=true,nillable=false,type=java.lang.String.class)
  protected java.util.Date timestamp;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="MessageId",required=true,nillable=false)
  protected java.lang.String messageId;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="RefToMessageId",required=false,nillable=false)
  protected java.lang.String refToMessageId;

}
