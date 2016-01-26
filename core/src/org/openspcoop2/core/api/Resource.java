/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2016 Link.it srl (http://link.it).
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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
package org.openspcoop2.core.api;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import org.openspcoop2.core.api.constants.MessageType;
import org.openspcoop2.core.api.constants.MethodType;
import java.io.Serializable;


/** <p>Java class for resource complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="resource">
 * 		&lt;sequence>
 * 			&lt;element name="path" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="method" type="{http://www.openspcoop2.org/core/api}MethodType" minOccurs="1" maxOccurs="1"/>
 * 			&lt;element name="type" type="{http://www.openspcoop2.org/core/api}MessageType" minOccurs="1" maxOccurs="1"/>
 * 			&lt;element name="media-type" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="response-status" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="response-message" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/>
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
@XmlType(name = "resource", 
  propOrder = {
  	"path",
  	"method",
  	"type",
  	"mediaType",
  	"responseStatus",
  	"responseMessage"
  }
)

@XmlRootElement(name = "resource")

public class Resource extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public Resource() {
  }

  public Long getId() {
    if(this.id!=null)
		return this.id;
	else
		return new Long(-1);
  }

  public void setId(Long id) {
    if(id!=null)
		this.id=id;
	else
		this.id=new Long(-1);
  }

  public java.lang.String getPath() {
    return this.path;
  }

  public void setPath(java.lang.String path) {
    this.path = path;
  }

  public void set_value_method(String value) {
    this.method = (MethodType) MethodType.toEnumConstantFromString(value);
  }

  public String get_value_method() {
    if(this.method == null){
    	return null;
    }else{
    	return this.method.toString();
    }
  }

  public org.openspcoop2.core.api.constants.MethodType getMethod() {
    return this.method;
  }

  public void setMethod(org.openspcoop2.core.api.constants.MethodType method) {
    this.method = method;
  }

  public void set_value_type(String value) {
    this.type = (MessageType) MessageType.toEnumConstantFromString(value);
  }

  public String get_value_type() {
    if(this.type == null){
    	return null;
    }else{
    	return this.type.toString();
    }
  }

  public org.openspcoop2.core.api.constants.MessageType getType() {
    return this.type;
  }

  public void setType(org.openspcoop2.core.api.constants.MessageType type) {
    this.type = type;
  }

  public java.lang.String getMediaType() {
    return this.mediaType;
  }

  public void setMediaType(java.lang.String mediaType) {
    this.mediaType = mediaType;
  }

  public java.lang.Integer getResponseStatus() {
    return this.responseStatus;
  }

  public void setResponseStatus(java.lang.Integer responseStatus) {
    this.responseStatus = responseStatus;
  }

  public java.lang.String getResponseMessage() {
    return this.responseMessage;
  }

  public void setResponseMessage(java.lang.String responseMessage) {
    this.responseMessage = responseMessage;
  }

  private static final long serialVersionUID = 1L;

  @XmlTransient
  private Long id;



  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="path",required=false,nillable=false)
  protected java.lang.String path;

  @XmlTransient
  protected java.lang.String _value_method;

  @XmlElement(name="method",required=true,nillable=false)
  protected MethodType method;

  @XmlTransient
  protected java.lang.String _value_type;

  @XmlElement(name="type",required=true,nillable=false)
  protected MessageType type;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="media-type",required=false,nillable=false)
  protected java.lang.String mediaType;

  @javax.xml.bind.annotation.XmlSchemaType(name="integer")
  @XmlElement(name="response-status",required=false,nillable=false)
  protected java.lang.Integer responseStatus;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="response-message",required=false,nillable=false)
  protected java.lang.String responseMessage;

}
