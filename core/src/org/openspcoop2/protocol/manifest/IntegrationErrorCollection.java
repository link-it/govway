/*
 * OpenSPCoop - Customizable API Gateway 
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
package org.openspcoop2.protocol.manifest;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;


/** <p>Java class for IntegrationErrorCollection complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="IntegrationErrorCollection">
 * 		&lt;sequence>
 * 			&lt;element name="authentication" type="{http://www.openspcoop2.org/protocol/manifest}IntegrationError" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="authorization" type="{http://www.openspcoop2.org/protocol/manifest}IntegrationError" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="notFound" type="{http://www.openspcoop2.org/protocol/manifest}IntegrationError" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="badRequest" type="{http://www.openspcoop2.org/protocol/manifest}IntegrationError" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="internalError" type="{http://www.openspcoop2.org/protocol/manifest}IntegrationError" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="default" type="{http://www.openspcoop2.org/protocol/manifest}DefaultIntegrationError" minOccurs="1" maxOccurs="1"/>
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
@XmlType(name = "IntegrationErrorCollection", 
  propOrder = {
  	"authentication",
  	"authorization",
  	"notFound",
  	"badRequest",
  	"internalError",
  	"_default"
  }
)

@XmlRootElement(name = "IntegrationErrorCollection")

public class IntegrationErrorCollection extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public IntegrationErrorCollection() {
  }

  public IntegrationError getAuthentication() {
    return this.authentication;
  }

  public void setAuthentication(IntegrationError authentication) {
    this.authentication = authentication;
  }

  public IntegrationError getAuthorization() {
    return this.authorization;
  }

  public void setAuthorization(IntegrationError authorization) {
    this.authorization = authorization;
  }

  public IntegrationError getNotFound() {
    return this.notFound;
  }

  public void setNotFound(IntegrationError notFound) {
    this.notFound = notFound;
  }

  public IntegrationError getBadRequest() {
    return this.badRequest;
  }

  public void setBadRequest(IntegrationError badRequest) {
    this.badRequest = badRequest;
  }

  public IntegrationError getInternalError() {
    return this.internalError;
  }

  public void setInternalError(IntegrationError internalError) {
    this.internalError = internalError;
  }

  public DefaultIntegrationError getDefault() {
    return this._default;
  }

  public void setDefault(DefaultIntegrationError _default) {
    this._default = _default;
  }

  private static final long serialVersionUID = 1L;



  @XmlElement(name="authentication",required=false,nillable=false)
  protected IntegrationError authentication;

  @XmlElement(name="authorization",required=false,nillable=false)
  protected IntegrationError authorization;

  @XmlElement(name="notFound",required=false,nillable=false)
  protected IntegrationError notFound;

  @XmlElement(name="badRequest",required=false,nillable=false)
  protected IntegrationError badRequest;

  @XmlElement(name="internalError",required=false,nillable=false)
  protected IntegrationError internalError;

  @XmlElement(name="default",required=true,nillable=false)
  protected DefaultIntegrationError _default;

}
