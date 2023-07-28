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
package org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Java class for Messaging complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Messaging"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="SignalMessage" type="{http://docs.oasis-open.org/ebxml-msg/ebms/v3.0/ns/core/200704/}SignalMessage" minOccurs="0" maxOccurs="unbounded"/&gt;
 * 			&lt;element name="UserMessage" type="{http://docs.oasis-open.org/ebxml-msg/ebms/v3.0/ns/core/200704/}UserMessage" minOccurs="0" maxOccurs="unbounded"/&gt;
 * 		&lt;/sequence&gt;
 * 		&lt;attribute name="id" type="{http://www.w3.org/2001/XMLSchema}ID" use="optional"/&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Messaging", 
  propOrder = {
  	"signalMessage",
  	"userMessage"
  }
)

@XmlRootElement(name = "Messaging")

public class Messaging extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public Messaging() {
    super();
  }

  public void addSignalMessage(SignalMessage signalMessage) {
    this.signalMessage.add(signalMessage);
  }

  public SignalMessage getSignalMessage(int index) {
    return this.signalMessage.get( index );
  }

  public SignalMessage removeSignalMessage(int index) {
    return this.signalMessage.remove( index );
  }

  public List<SignalMessage> getSignalMessageList() {
    return this.signalMessage;
  }

  public void setSignalMessageList(List<SignalMessage> signalMessage) {
    this.signalMessage=signalMessage;
  }

  public int sizeSignalMessageList() {
    return this.signalMessage.size();
  }

  public void addUserMessage(UserMessage userMessage) {
    this.userMessage.add(userMessage);
  }

  public UserMessage getUserMessage(int index) {
    return this.userMessage.get( index );
  }

  public UserMessage removeUserMessage(int index) {
    return this.userMessage.remove( index );
  }

  public List<UserMessage> getUserMessageList() {
    return this.userMessage;
  }

  public void setUserMessageList(List<UserMessage> userMessage) {
    this.userMessage=userMessage;
  }

  public int sizeUserMessageList() {
    return this.userMessage.size();
  }

  public java.lang.String getId() {
    return this.id;
  }

  public void setId(java.lang.String id) {
    this.id = id;
  }

  private static final long serialVersionUID = 1L;

  private static org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.model.MessagingModel modelStaticInstance = null;
  private static synchronized void initModelStaticInstance(){
	  if(org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Messaging.modelStaticInstance==null){
  			org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Messaging.modelStaticInstance = new org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.model.MessagingModel();
	  }
  }
  public static org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.model.MessagingModel model(){
	  if(org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Messaging.modelStaticInstance==null){
	  		initModelStaticInstance();
	  }
	  return org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Messaging.modelStaticInstance;
  }


  @XmlElement(name="SignalMessage",required=true,nillable=false)
  private List<SignalMessage> signalMessage = new ArrayList<>();

  /**
   * Use method getSignalMessageList
   * @return List&lt;SignalMessage&gt;
  */
  public List<SignalMessage> getSignalMessage() {
  	return this.getSignalMessageList();
  }

  /**
   * Use method setSignalMessageList
   * @param signalMessage List&lt;SignalMessage&gt;
  */
  public void setSignalMessage(List<SignalMessage> signalMessage) {
  	this.setSignalMessageList(signalMessage);
  }

  /**
   * Use method sizeSignalMessageList
   * @return lunghezza della lista
  */
  public int sizeSignalMessage() {
  	return this.sizeSignalMessageList();
  }

  @XmlElement(name="UserMessage",required=true,nillable=false)
  private List<UserMessage> userMessage = new ArrayList<>();

  /**
   * Use method getUserMessageList
   * @return List&lt;UserMessage&gt;
  */
  public List<UserMessage> getUserMessage() {
  	return this.getUserMessageList();
  }

  /**
   * Use method setUserMessageList
   * @param userMessage List&lt;UserMessage&gt;
  */
  public void setUserMessage(List<UserMessage> userMessage) {
  	this.setUserMessageList(userMessage);
  }

  /**
   * Use method sizeUserMessageList
   * @return lunghezza della lista
  */
  public int sizeUserMessage() {
  	return this.sizeUserMessageList();
  }

  @jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter(jakarta.xml.bind.annotation.adapters.CollapsedStringAdapter.class)
  @jakarta.xml.bind.annotation.XmlID
  @jakarta.xml.bind.annotation.XmlSchemaType(name="ID")
  @XmlAttribute(name="id",required=false)
  protected java.lang.String id;

}
