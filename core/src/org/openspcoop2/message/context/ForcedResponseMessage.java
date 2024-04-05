/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it).
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

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import java.io.Serializable;


/** <p>Java class for forced-response-message complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="forced-response-message"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="content" type="{http://www.w3.org/2001/XMLSchema}base64Binary" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="content-type" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="response-code" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="header-parameters" type="{http://www.openspcoop2.org/message/context}header-parameters" minOccurs="0" maxOccurs="1"/&gt;
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
@XmlType(name = "forced-response-message", 
  propOrder = {
  	"content",
  	"contentType",
  	"responseCode",
  	"headerParameters"
  }
)

@XmlRootElement(name = "forced-response-message")

public class ForcedResponseMessage extends org.openspcoop2.utils.beans.BaseBeanWithId implements Serializable , Cloneable {
  public ForcedResponseMessage() {
    super();
  }

  public byte[] getContent() {
    return this.content;
  }

  public void setContent(byte[] content) {
    this.content = content;
  }

  public java.lang.String getContentType() {
    return this.contentType;
  }

  public void setContentType(java.lang.String contentType) {
    this.contentType = contentType;
  }

  public java.lang.String getResponseCode() {
    return this.responseCode;
  }

  public void setResponseCode(java.lang.String responseCode) {
    this.responseCode = responseCode;
  }

  public HeaderParameters getHeaderParameters() {
    return this.headerParameters;
  }

  public void setHeaderParameters(HeaderParameters headerParameters) {
    this.headerParameters = headerParameters;
  }

  private static final long serialVersionUID = 1L;



  @jakarta.xml.bind.annotation.XmlSchemaType(name="base64Binary")
  @XmlElement(name="content",required=false,nillable=false)
  protected byte[] content;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="content-type",required=false,nillable=false)
  protected java.lang.String contentType;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="response-code",required=false,nillable=false)
  protected java.lang.String responseCode;

  @XmlElement(name="header-parameters",required=false,nillable=false)
  protected HeaderParameters headerParameters;

}
