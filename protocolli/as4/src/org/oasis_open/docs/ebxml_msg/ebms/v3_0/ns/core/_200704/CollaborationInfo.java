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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;


/** <p>Java class for CollaborationInfo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CollaborationInfo">
 * 		&lt;sequence>
 * 			&lt;element name="AgreementRef" type="{http://docs.oasis-open.org/ebxml-msg/ebms/v3.0/ns/core/200704/}AgreementRef" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="Service" type="{http://docs.oasis-open.org/ebxml-msg/ebms/v3.0/ns/core/200704/}Service" minOccurs="1" maxOccurs="1"/>
 * 			&lt;element name="Action" type="{http://www.w3.org/2001/XMLSchema}token" minOccurs="1" maxOccurs="1"/>
 * 			&lt;element name="ConversationId" type="{http://www.w3.org/2001/XMLSchema}token" minOccurs="1" maxOccurs="1"/>
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
@XmlType(name = "CollaborationInfo", 
  propOrder = {
  	"agreementRef",
  	"service",
  	"action",
  	"conversationId"
  }
)

@XmlRootElement(name = "CollaborationInfo")

public class CollaborationInfo extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public CollaborationInfo() {
  }

  public AgreementRef getAgreementRef() {
    return this.agreementRef;
  }

  public void setAgreementRef(AgreementRef agreementRef) {
    this.agreementRef = agreementRef;
  }

  public Service getService() {
    return this.service;
  }

  public void setService(Service service) {
    this.service = service;
  }

  public java.lang.String getAction() {
    return this.action;
  }

  public void setAction(java.lang.String action) {
    this.action = action;
  }

  public java.lang.String getConversationId() {
    return this.conversationId;
  }

  public void setConversationId(java.lang.String conversationId) {
    this.conversationId = conversationId;
  }

  private static final long serialVersionUID = 1L;



  @XmlElement(name="AgreementRef",required=false,nillable=false)
  protected AgreementRef agreementRef;

  @XmlElement(name="Service",required=true,nillable=false)
  protected Service service;

  @javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(javax.xml.bind.annotation.adapters.CollapsedStringAdapter.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="token")
  @XmlElement(name="Action",required=true,nillable=false)
  protected java.lang.String action;

  @javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(javax.xml.bind.annotation.adapters.CollapsedStringAdapter.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="token")
  @XmlElement(name="ConversationId",required=true,nillable=false)
  protected java.lang.String conversationId;

}
