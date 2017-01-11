/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2017 Link.it srl (http://link.it).
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
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;


/** <p>Java class for UserMessage complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="UserMessage">
 * 		&lt;sequence>
 * 			&lt;element name="MessageInfo" type="{http://docs.oasis-open.org/ebxml-msg/ebms/v3.0/ns/core/200704/}MessageInfo" minOccurs="1" maxOccurs="1"/>
 * 			&lt;element name="PartyInfo" type="{http://docs.oasis-open.org/ebxml-msg/ebms/v3.0/ns/core/200704/}PartyInfo" minOccurs="1" maxOccurs="1"/>
 * 			&lt;element name="CollaborationInfo" type="{http://docs.oasis-open.org/ebxml-msg/ebms/v3.0/ns/core/200704/}CollaborationInfo" minOccurs="1" maxOccurs="1"/>
 * 			&lt;element name="MessageProperties" type="{http://docs.oasis-open.org/ebxml-msg/ebms/v3.0/ns/core/200704/}MessageProperties" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="PayloadInfo" type="{http://docs.oasis-open.org/ebxml-msg/ebms/v3.0/ns/core/200704/}PayloadInfo" minOccurs="0" maxOccurs="1"/>
 * 		&lt;/sequence>
 * 		&lt;attribute name="mpc" type="{http://www.w3.org/2001/XMLSchema}anyURI" use="optional"/>
 * &lt;/complexType>
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UserMessage", 
  propOrder = {
  	"messageInfo",
  	"partyInfo",
  	"collaborationInfo",
  	"messageProperties",
  	"payloadInfo"
  }
)

@XmlRootElement(name = "UserMessage")

public class UserMessage extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public UserMessage() {
  }

  public MessageInfo getMessageInfo() {
    return this.messageInfo;
  }

  public void setMessageInfo(MessageInfo messageInfo) {
    this.messageInfo = messageInfo;
  }

  public PartyInfo getPartyInfo() {
    return this.partyInfo;
  }

  public void setPartyInfo(PartyInfo partyInfo) {
    this.partyInfo = partyInfo;
  }

  public CollaborationInfo getCollaborationInfo() {
    return this.collaborationInfo;
  }

  public void setCollaborationInfo(CollaborationInfo collaborationInfo) {
    this.collaborationInfo = collaborationInfo;
  }

  public MessageProperties getMessageProperties() {
    return this.messageProperties;
  }

  public void setMessageProperties(MessageProperties messageProperties) {
    this.messageProperties = messageProperties;
  }

  public PayloadInfo getPayloadInfo() {
    return this.payloadInfo;
  }

  public void setPayloadInfo(PayloadInfo payloadInfo) {
    this.payloadInfo = payloadInfo;
  }

  public java.net.URI getMpc() {
    return this.mpc;
  }

  public void setMpc(java.net.URI mpc) {
    this.mpc = mpc;
  }

  private static final long serialVersionUID = 1L;



  @XmlElement(name="MessageInfo",required=true,nillable=false)
  protected MessageInfo messageInfo;

  @XmlElement(name="PartyInfo",required=true,nillable=false)
  protected PartyInfo partyInfo;

  @XmlElement(name="CollaborationInfo",required=true,nillable=false)
  protected CollaborationInfo collaborationInfo;

  @XmlElement(name="MessageProperties",required=false,nillable=false)
  protected MessageProperties messageProperties;

  @XmlElement(name="PayloadInfo",required=false,nillable=false)
  protected PayloadInfo payloadInfo;

  @javax.xml.bind.annotation.XmlSchemaType(name="anyURI")
  @XmlAttribute(name="mpc",required=false)
  protected java.net.URI mpc;

}
