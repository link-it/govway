/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
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


/** <p>Java class for transport-response-context complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="transport-response-context">
 * 		&lt;sequence>
 * 			&lt;element name="header-parameters" type="{http://www.openspcoop2.org/message/context}header-parameters" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="transport-code" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="content-length" type="{http://www.w3.org/2001/XMLSchema}unsignedLong" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="error" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/>
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
@XmlType(name = "transport-response-context", 
  propOrder = {
  	"headerParameters",
  	"transportCode",
  	"contentLength",
  	"error"
  }
)

@XmlRootElement(name = "transport-response-context")

public class TransportResponseContext extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public TransportResponseContext() {
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

  public HeaderParameters getHeaderParameters() {
    return this.headerParameters;
  }

  public void setHeaderParameters(HeaderParameters headerParameters) {
    this.headerParameters = headerParameters;
  }

  public java.lang.String getTransportCode() {
    return this.transportCode;
  }

  public void setTransportCode(java.lang.String transportCode) {
    this.transportCode = transportCode;
  }

  public java.lang.Long getContentLength() {
    return this.contentLength;
  }

  public void setContentLength(java.lang.Long contentLength) {
    this.contentLength = contentLength;
  }

  public java.lang.String getError() {
    return this.error;
  }

  public void setError(java.lang.String error) {
    this.error = error;
  }

  private static final long serialVersionUID = 1L;

  @XmlTransient
  private Long id;



  @XmlElement(name="header-parameters",required=false,nillable=false)
  protected HeaderParameters headerParameters;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="transport-code",required=false,nillable=false)
  protected java.lang.String transportCode;

  @javax.xml.bind.annotation.XmlSchemaType(name="unsignedLong")
  @XmlElement(name="content-length",required=false,nillable=false)
  protected java.lang.Long contentLength;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="error",required=false,nillable=false)
  protected java.lang.String error;

}
