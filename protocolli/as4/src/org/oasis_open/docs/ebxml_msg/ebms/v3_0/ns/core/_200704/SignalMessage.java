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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Java class for SignalMessage complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SignalMessage"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="MessageInfo" type="{http://docs.oasis-open.org/ebxml-msg/ebms/v3.0/ns/core/200704/}MessageInfo" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="PullRequest" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="Receipt" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="Error" type="{http://docs.oasis-open.org/ebxml-msg/ebms/v3.0/ns/core/200704/}Error" minOccurs="0" maxOccurs="unbounded"/&gt;
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
@XmlType(name = "SignalMessage", 
  propOrder = {
  	"messageInfo",
  	"pullRequest",
  	"receipt",
  	"error"
  }
)

@XmlRootElement(name = "SignalMessage")

public class SignalMessage extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public SignalMessage() {
    super();
  }

  public MessageInfo getMessageInfo() {
    return this.messageInfo;
  }

  public void setMessageInfo(MessageInfo messageInfo) {
    this.messageInfo = messageInfo;
  }

  public java.lang.String getPullRequest() {
    return this.pullRequest;
  }

  public void setPullRequest(java.lang.String pullRequest) {
    this.pullRequest = pullRequest;
  }

  public java.lang.String getReceipt() {
    return this.receipt;
  }

  public void setReceipt(java.lang.String receipt) {
    this.receipt = receipt;
  }

  public void addError(Error error) {
    this.error.add(error);
  }

  public Error getError(int index) {
    return this.error.get( index );
  }

  public Error removeError(int index) {
    return this.error.remove( index );
  }

  public List<Error> getErrorList() {
    return this.error;
  }

  public void setErrorList(List<Error> error) {
    this.error=error;
  }

  public int sizeErrorList() {
    return this.error.size();
  }

  private static final long serialVersionUID = 1L;



  @XmlElement(name="MessageInfo",required=true,nillable=false)
  protected MessageInfo messageInfo;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="PullRequest",required=false,nillable=false)
  protected java.lang.String pullRequest;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="Receipt",required=false,nillable=false)
  protected java.lang.String receipt;

  @XmlElement(name="Error",required=true,nillable=false)
  private List<Error> error = new ArrayList<>();

  /**
   * Use method getErrorList
   * @return List&lt;Error&gt;
  */
  public List<Error> getError() {
  	return this.getErrorList();
  }

  /**
   * Use method setErrorList
   * @param error List&lt;Error&gt;
  */
  public void setError(List<Error> error) {
  	this.setErrorList(error);
  }

  /**
   * Use method sizeErrorList
   * @return lunghezza della lista
  */
  public int sizeError() {
  	return this.sizeErrorList();
  }

}
