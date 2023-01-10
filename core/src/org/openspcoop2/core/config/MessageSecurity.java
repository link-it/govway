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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;


/** <p>Java class for message-security complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="message-security"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="request-flow" type="{http://www.openspcoop2.org/core/config}message-security-flow" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="response-flow" type="{http://www.openspcoop2.org/core/config}message-security-flow" minOccurs="0" maxOccurs="1"/&gt;
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
@XmlType(name = "message-security", 
  propOrder = {
  	"requestFlow",
  	"responseFlow"
  }
)

@XmlRootElement(name = "message-security")

public class MessageSecurity extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public MessageSecurity() {
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

  public MessageSecurityFlow getRequestFlow() {
    return this.requestFlow;
  }

  public void setRequestFlow(MessageSecurityFlow requestFlow) {
    this.requestFlow = requestFlow;
  }

  public MessageSecurityFlow getResponseFlow() {
    return this.responseFlow;
  }

  public void setResponseFlow(MessageSecurityFlow responseFlow) {
    this.responseFlow = responseFlow;
  }

  private static final long serialVersionUID = 1L;

  @XmlTransient
  private Long id;



  @XmlElement(name="request-flow",required=false,nillable=false)
  protected MessageSecurityFlow requestFlow;

  @XmlElement(name="response-flow",required=false,nillable=false)
  protected MessageSecurityFlow responseFlow;

}
