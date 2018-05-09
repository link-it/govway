/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it).
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
package org.openspcoop2.message.context;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;


/** <p>Java class for forced-response complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="forced-response">
 * 		&lt;sequence>
 * 			&lt;element name="response-code" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="empty-response" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="response-message" type="{http://www.openspcoop2.org/message/context}forced-response-message" minOccurs="0" maxOccurs="1"/>
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
@XmlType(name = "forced-response", 
  propOrder = {
  	"responseCode",
  	"emptyResponse",
  	"responseMessage"
  }
)

@XmlRootElement(name = "forced-response")

public class ForcedResponse extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public ForcedResponse() {
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

  public java.lang.String getResponseCode() {
    return this.responseCode;
  }

  public void setResponseCode(java.lang.String responseCode) {
    this.responseCode = responseCode;
  }

  public boolean isEmptyResponse() {
    return this.emptyResponse;
  }

  public boolean getEmptyResponse() {
    return this.emptyResponse;
  }

  public void setEmptyResponse(boolean emptyResponse) {
    this.emptyResponse = emptyResponse;
  }

  public ForcedResponseMessage getResponseMessage() {
    return this.responseMessage;
  }

  public void setResponseMessage(ForcedResponseMessage responseMessage) {
    this.responseMessage = responseMessage;
  }

  private static final long serialVersionUID = 1L;

  @XmlTransient
  private Long id;



  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="response-code",required=false,nillable=false)
  protected java.lang.String responseCode;

  @javax.xml.bind.annotation.XmlSchemaType(name="boolean")
  @XmlElement(name="empty-response",required=false,nillable=false)
  protected boolean emptyResponse;

  @XmlElement(name="response-message",required=false,nillable=false)
  protected ForcedResponseMessage responseMessage;

}
