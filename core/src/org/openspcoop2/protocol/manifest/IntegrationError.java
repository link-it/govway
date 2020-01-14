/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it).
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
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.openspcoop2.protocol.manifest.constants.IntegrationErrorMessageType;
import java.io.Serializable;


/** <p>Java class for IntegrationError complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="IntegrationError">
 * 		&lt;attribute name="httpReturnCode" type="{http://www.w3.org/2001/XMLSchema}int" use="required"/>
 * 		&lt;attribute name="messageType" type="{http://www.openspcoop2.org/protocol/manifest}IntegrationErrorMessageType" use="required"/>
 * &lt;/complexType>
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "IntegrationError")

@XmlRootElement(name = "IntegrationError")

public class IntegrationError extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public IntegrationError() {
  }

  public int getHttpReturnCode() {
    return this.httpReturnCode;
  }

  public void setHttpReturnCode(int httpReturnCode) {
    this.httpReturnCode = httpReturnCode;
  }

  public void set_value_messageType(String value) {
    this.messageType = (IntegrationErrorMessageType) IntegrationErrorMessageType.toEnumConstantFromString(value);
  }

  public String get_value_messageType() {
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

  private static final long serialVersionUID = 1L;



  @javax.xml.bind.annotation.XmlSchemaType(name="int")
  @XmlAttribute(name="httpReturnCode",required=true)
  protected int httpReturnCode;

  @javax.xml.bind.annotation.XmlTransient
  protected java.lang.String _value_messageType;

  @XmlAttribute(name="messageType",required=true)
  protected IntegrationErrorMessageType messageType;

}
