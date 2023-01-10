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
import org.openspcoop2.protocol.manifest.constants.IntegrationErrorProblemType;
import java.io.Serializable;


/** <p>Java class for IntegrationErrorCollection complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="IntegrationErrorCollection"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="rfc7807" type="{http://www.openspcoop2.org/protocol/manifest}RFC7807" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="authentication" type="{http://www.openspcoop2.org/protocol/manifest}IntegrationError" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="authorization" type="{http://www.openspcoop2.org/protocol/manifest}IntegrationError" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="notFound" type="{http://www.openspcoop2.org/protocol/manifest}IntegrationError" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="badRequest" type="{http://www.openspcoop2.org/protocol/manifest}IntegrationError" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="conflict" type="{http://www.openspcoop2.org/protocol/manifest}IntegrationError" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="requestTooLarge" type="{http://www.openspcoop2.org/protocol/manifest}IntegrationError" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="limitExceeded" type="{http://www.openspcoop2.org/protocol/manifest}IntegrationError" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="tooManyRequests" type="{http://www.openspcoop2.org/protocol/manifest}IntegrationError" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="serviceUnavailable" type="{http://www.openspcoop2.org/protocol/manifest}IntegrationError" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="endpointRequestTimedOut" type="{http://www.openspcoop2.org/protocol/manifest}IntegrationError" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="badResponse" type="{http://www.openspcoop2.org/protocol/manifest}IntegrationError" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="internalRequestError" type="{http://www.openspcoop2.org/protocol/manifest}IntegrationError" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="internalResponseError" type="{http://www.openspcoop2.org/protocol/manifest}IntegrationError" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="default" type="{http://www.openspcoop2.org/protocol/manifest}DefaultIntegrationError" minOccurs="1" maxOccurs="1"/&gt;
 * 		&lt;/sequence&gt;
 * 		&lt;attribute name="problemType" type="{http://www.openspcoop2.org/protocol/manifest}IntegrationErrorProblemType" use="required"/&gt;
 * 		&lt;attribute name="useInternalFault" type="{http://www.w3.org/2001/XMLSchema}boolean" use="optional" default="false"/&gt;
 * &lt;/complexType&gt;
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
  	"rfc7807",
  	"authentication",
  	"authorization",
  	"notFound",
  	"badRequest",
  	"conflict",
  	"requestTooLarge",
  	"limitExceeded",
  	"tooManyRequests",
  	"serviceUnavailable",
  	"endpointRequestTimedOut",
  	"badResponse",
  	"internalRequestError",
  	"internalResponseError",
  	"_default"
  }
)

@XmlRootElement(name = "IntegrationErrorCollection")

public class IntegrationErrorCollection extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public IntegrationErrorCollection() {
  }

  public RFC7807 getRfc7807() {
    return this.rfc7807;
  }

  public void setRfc7807(RFC7807 rfc7807) {
    this.rfc7807 = rfc7807;
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

  public IntegrationError getConflict() {
    return this.conflict;
  }

  public void setConflict(IntegrationError conflict) {
    this.conflict = conflict;
  }

  public IntegrationError getRequestTooLarge() {
    return this.requestTooLarge;
  }

  public void setRequestTooLarge(IntegrationError requestTooLarge) {
    this.requestTooLarge = requestTooLarge;
  }

  public IntegrationError getLimitExceeded() {
    return this.limitExceeded;
  }

  public void setLimitExceeded(IntegrationError limitExceeded) {
    this.limitExceeded = limitExceeded;
  }

  public IntegrationError getTooManyRequests() {
    return this.tooManyRequests;
  }

  public void setTooManyRequests(IntegrationError tooManyRequests) {
    this.tooManyRequests = tooManyRequests;
  }

  public IntegrationError getServiceUnavailable() {
    return this.serviceUnavailable;
  }

  public void setServiceUnavailable(IntegrationError serviceUnavailable) {
    this.serviceUnavailable = serviceUnavailable;
  }

  public IntegrationError getEndpointRequestTimedOut() {
    return this.endpointRequestTimedOut;
  }

  public void setEndpointRequestTimedOut(IntegrationError endpointRequestTimedOut) {
    this.endpointRequestTimedOut = endpointRequestTimedOut;
  }

  public IntegrationError getBadResponse() {
    return this.badResponse;
  }

  public void setBadResponse(IntegrationError badResponse) {
    this.badResponse = badResponse;
  }

  public IntegrationError getInternalRequestError() {
    return this.internalRequestError;
  }

  public void setInternalRequestError(IntegrationError internalRequestError) {
    this.internalRequestError = internalRequestError;
  }

  public IntegrationError getInternalResponseError() {
    return this.internalResponseError;
  }

  public void setInternalResponseError(IntegrationError internalResponseError) {
    this.internalResponseError = internalResponseError;
  }

  public DefaultIntegrationError getDefault() {
    return this._default;
  }

  public void setDefault(DefaultIntegrationError _default) {
    this._default = _default;
  }

  public void set_value_problemType(String value) {
    this.problemType = (IntegrationErrorProblemType) IntegrationErrorProblemType.toEnumConstantFromString(value);
  }

  public String get_value_problemType() {
    if(this.problemType == null){
    	return null;
    }else{
    	return this.problemType.toString();
    }
  }

  public org.openspcoop2.protocol.manifest.constants.IntegrationErrorProblemType getProblemType() {
    return this.problemType;
  }

  public void setProblemType(org.openspcoop2.protocol.manifest.constants.IntegrationErrorProblemType problemType) {
    this.problemType = problemType;
  }

  public boolean isUseInternalFault() {
    return this.useInternalFault;
  }

  public boolean getUseInternalFault() {
    return this.useInternalFault;
  }

  public void setUseInternalFault(boolean useInternalFault) {
    this.useInternalFault = useInternalFault;
  }

  private static final long serialVersionUID = 1L;



  @XmlElement(name="rfc7807",required=false,nillable=false)
  protected RFC7807 rfc7807;

  @XmlElement(name="authentication",required=false,nillable=false)
  protected IntegrationError authentication;

  @XmlElement(name="authorization",required=false,nillable=false)
  protected IntegrationError authorization;

  @XmlElement(name="notFound",required=false,nillable=false)
  protected IntegrationError notFound;

  @XmlElement(name="badRequest",required=false,nillable=false)
  protected IntegrationError badRequest;

  @XmlElement(name="conflict",required=false,nillable=false)
  protected IntegrationError conflict;

  @XmlElement(name="requestTooLarge",required=false,nillable=false)
  protected IntegrationError requestTooLarge;

  @XmlElement(name="limitExceeded",required=false,nillable=false)
  protected IntegrationError limitExceeded;

  @XmlElement(name="tooManyRequests",required=false,nillable=false)
  protected IntegrationError tooManyRequests;

  @XmlElement(name="serviceUnavailable",required=false,nillable=false)
  protected IntegrationError serviceUnavailable;

  @XmlElement(name="endpointRequestTimedOut",required=false,nillable=false)
  protected IntegrationError endpointRequestTimedOut;

  @XmlElement(name="badResponse",required=false,nillable=false)
  protected IntegrationError badResponse;

  @XmlElement(name="internalRequestError",required=false,nillable=false)
  protected IntegrationError internalRequestError;

  @XmlElement(name="internalResponseError",required=false,nillable=false)
  protected IntegrationError internalResponseError;

  @XmlElement(name="default",required=true,nillable=false)
  protected DefaultIntegrationError _default;

  @javax.xml.bind.annotation.XmlTransient
  protected java.lang.String _value_problemType;

  @XmlAttribute(name="problemType",required=true)
  protected IntegrationErrorProblemType problemType;

  @javax.xml.bind.annotation.XmlSchemaType(name="boolean")
  @XmlAttribute(name="useInternalFault",required=false)
  protected boolean useInternalFault = false;

}
