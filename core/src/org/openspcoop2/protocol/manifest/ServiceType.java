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
package org.openspcoop2.protocol.manifest;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import org.openspcoop2.protocol.manifest.constants.MessageType;
import org.openspcoop2.protocol.manifest.constants.ServiceBinding;
import java.io.Serializable;


/** <p>Java class for ServiceType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ServiceType"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="soapMediaTypeCollection" type="{http://www.openspcoop2.org/protocol/manifest}SoapMediaTypeCollection" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="restMediaTypeCollection" type="{http://www.openspcoop2.org/protocol/manifest}RestMediaTypeCollection" minOccurs="0" maxOccurs="1"/&gt;
 * 		&lt;/sequence&gt;
 * 		&lt;attribute name="name" type="{http://www.w3.org/2001/XMLSchema}string" use="required"/&gt;
 * 		&lt;attribute name="protocol" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/&gt;
 * 		&lt;attribute name="messageType" type="{http://www.openspcoop2.org/protocol/manifest}MessageType" use="optional"/&gt;
 * 		&lt;attribute name="binding" type="{http://www.openspcoop2.org/protocol/manifest}ServiceBinding" use="optional"/&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ServiceType", 
  propOrder = {
  	"soapMediaTypeCollection",
  	"restMediaTypeCollection"
  }
)

@XmlRootElement(name = "ServiceType")

public class ServiceType extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public ServiceType() {
    super();
  }

  public SoapMediaTypeCollection getSoapMediaTypeCollection() {
    return this.soapMediaTypeCollection;
  }

  public void setSoapMediaTypeCollection(SoapMediaTypeCollection soapMediaTypeCollection) {
    this.soapMediaTypeCollection = soapMediaTypeCollection;
  }

  public RestMediaTypeCollection getRestMediaTypeCollection() {
    return this.restMediaTypeCollection;
  }

  public void setRestMediaTypeCollection(RestMediaTypeCollection restMediaTypeCollection) {
    this.restMediaTypeCollection = restMediaTypeCollection;
  }

  public java.lang.String getName() {
    return this.name;
  }

  public void setName(java.lang.String name) {
    this.name = name;
  }

  public java.lang.String getProtocol() {
    return this.protocol;
  }

  public void setProtocol(java.lang.String protocol) {
    this.protocol = protocol;
  }

  public void setMessageTypeRawEnumValue(String value) {
    this.messageType = (MessageType) MessageType.toEnumConstantFromString(value);
  }

  public String getMessageTypeRawEnumValue() {
    if(this.messageType == null){
    	return null;
    }else{
    	return this.messageType.toString();
    }
  }

  public org.openspcoop2.protocol.manifest.constants.MessageType getMessageType() {
    return this.messageType;
  }

  public void setMessageType(org.openspcoop2.protocol.manifest.constants.MessageType messageType) {
    this.messageType = messageType;
  }

  public void setBindingRawEnumValue(String value) {
    this.binding = (ServiceBinding) ServiceBinding.toEnumConstantFromString(value);
  }

  public String getBindingRawEnumValue() {
    if(this.binding == null){
    	return null;
    }else{
    	return this.binding.toString();
    }
  }

  public org.openspcoop2.protocol.manifest.constants.ServiceBinding getBinding() {
    return this.binding;
  }

  public void setBinding(org.openspcoop2.protocol.manifest.constants.ServiceBinding binding) {
    this.binding = binding;
  }

  private static final long serialVersionUID = 1L;



  @XmlElement(name="soapMediaTypeCollection",required=false,nillable=false)
  protected SoapMediaTypeCollection soapMediaTypeCollection;

  @XmlElement(name="restMediaTypeCollection",required=false,nillable=false)
  protected RestMediaTypeCollection restMediaTypeCollection;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="name",required=true)
  protected java.lang.String name;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="protocol",required=false)
  protected java.lang.String protocol;

  @jakarta.xml.bind.annotation.XmlTransient
  protected java.lang.String messageTypeRawEnumValue;

  @XmlAttribute(name="messageType",required=false)
  protected MessageType messageType;

  @jakarta.xml.bind.annotation.XmlTransient
  protected java.lang.String bindingRawEnumValue;

  @XmlAttribute(name="binding",required=false)
  protected ServiceBinding binding;

}
