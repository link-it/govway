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
package org.openspcoop2.protocol.manifest;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.openspcoop2.protocol.manifest.constants.IntegrationErrorMessageDetailType;
import org.openspcoop2.protocol.manifest.constants.IntegrationErrorMessageType;
import java.io.Serializable;


/** <p>Java class for IntegrationError complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="IntegrationError"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="errorCode" type="{http://www.openspcoop2.org/protocol/manifest}IntegrationErrorCode" minOccurs="1" maxOccurs="1"/&gt;
 * 		&lt;/sequence&gt;
 * 		&lt;attribute name="messageType" type="{http://www.openspcoop2.org/protocol/manifest}IntegrationErrorMessageType" use="required"/&gt;
 * 		&lt;attribute name="retry" type="{http://www.w3.org/2001/XMLSchema}boolean" use="optional" default="false"/&gt;
 * 		&lt;attribute name="errorMessage" type="{http://www.openspcoop2.org/protocol/manifest}IntegrationErrorMessageDetailType" use="optional" default="generic"/&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "IntegrationError", 
  propOrder = {
  	"errorCode"
  }
)

@XmlRootElement(name = "IntegrationError")

public class IntegrationError extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public IntegrationError() {
    super();
  }

  public IntegrationErrorCode getErrorCode() {
    return this.errorCode;
  }

  public void setErrorCode(IntegrationErrorCode errorCode) {
    this.errorCode = errorCode;
  }

  public void setMessageTypeRawEnumValue(String value) {
    this.messageType = (IntegrationErrorMessageType) IntegrationErrorMessageType.toEnumConstantFromString(value);
  }

  public String getMessageTypeRawEnumValue() {
    if(this.messageType == null){
    	return null;
    }else{
    	return this.messageType.toString();
    }
  }

  public org.openspcoop2.protocol.manifest.constants.IntegrationErrorMessageType getMessageType() {
    return this.messageType;
  }

  public void setMessageType(org.openspcoop2.protocol.manifest.constants.IntegrationErrorMessageType messageType) {
    this.messageType = messageType;
  }

  public boolean isRetry() {
    return this.retry;
  }

  public boolean getRetry() {
    return this.retry;
  }

  public void setRetry(boolean retry) {
    this.retry = retry;
  }

  public void setErrorMessageRawEnumValue(String value) {
    this.errorMessage = (IntegrationErrorMessageDetailType) IntegrationErrorMessageDetailType.toEnumConstantFromString(value);
  }

  public String getErrorMessageRawEnumValue() {
    if(this.errorMessage == null){
    	return null;
    }else{
    	return this.errorMessage.toString();
    }
  }

  public org.openspcoop2.protocol.manifest.constants.IntegrationErrorMessageDetailType getErrorMessage() {
    return this.errorMessage;
  }

  public void setErrorMessage(org.openspcoop2.protocol.manifest.constants.IntegrationErrorMessageDetailType errorMessage) {
    this.errorMessage = errorMessage;
  }

  private static final long serialVersionUID = 1L;



  @XmlElement(name="errorCode",required=true,nillable=false)
  protected IntegrationErrorCode errorCode;

  @javax.xml.bind.annotation.XmlTransient
  protected java.lang.String messageTypeRawEnumValue;

  @XmlAttribute(name="messageType",required=true)
  protected IntegrationErrorMessageType messageType;

  @javax.xml.bind.annotation.XmlSchemaType(name="boolean")
  @XmlAttribute(name="retry",required=false)
  protected boolean retry = false;

  @javax.xml.bind.annotation.XmlTransient
  protected java.lang.String errorMessageRawEnumValue;

  @XmlAttribute(name="errorMessage",required=false)
  protected IntegrationErrorMessageDetailType errorMessage = (IntegrationErrorMessageDetailType) IntegrationErrorMessageDetailType.toEnumConstantFromString("generic");

}
