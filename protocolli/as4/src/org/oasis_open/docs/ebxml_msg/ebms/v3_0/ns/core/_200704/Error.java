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
package org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;


/** <p>Java class for Error complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Error">
 * 		&lt;sequence>
 * 			&lt;element name="Description" type="{http://docs.oasis-open.org/ebxml-msg/ebms/v3.0/ns/core/200704/}Description" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="ErrorDetail" type="{http://www.w3.org/2001/XMLSchema}token" minOccurs="0" maxOccurs="1"/>
 * 		&lt;/sequence>
 * 		&lt;attribute name="category" type="{http://www.w3.org/2001/XMLSchema}token" use="optional"/>
 * 		&lt;attribute name="refToMessageInError" type="{http://www.w3.org/2001/XMLSchema}token" use="optional"/>
 * 		&lt;attribute name="errorCode" type="{http://www.w3.org/2001/XMLSchema}token" use="required"/>
 * 		&lt;attribute name="origin" type="{http://www.w3.org/2001/XMLSchema}token" use="optional"/>
 * 		&lt;attribute name="severity" type="{http://www.w3.org/2001/XMLSchema}token" use="required"/>
 * 		&lt;attribute name="shortDescription" type="{http://www.w3.org/2001/XMLSchema}token" use="optional"/>
 * &lt;/complexType>
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Error", 
  propOrder = {
  	"description",
  	"errorDetail"
  }
)

@XmlRootElement(name = "Error")

public class Error extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public Error() {
  }

  public Description getDescription() {
    return this.description;
  }

  public void setDescription(Description description) {
    this.description = description;
  }

  public java.lang.String getErrorDetail() {
    return this.errorDetail;
  }

  public void setErrorDetail(java.lang.String errorDetail) {
    this.errorDetail = errorDetail;
  }

  public java.lang.String getCategory() {
    return this.category;
  }

  public void setCategory(java.lang.String category) {
    this.category = category;
  }

  public java.lang.String getRefToMessageInError() {
    return this.refToMessageInError;
  }

  public void setRefToMessageInError(java.lang.String refToMessageInError) {
    this.refToMessageInError = refToMessageInError;
  }

  public java.lang.String getErrorCode() {
    return this.errorCode;
  }

  public void setErrorCode(java.lang.String errorCode) {
    this.errorCode = errorCode;
  }

  public java.lang.String getOrigin() {
    return this.origin;
  }

  public void setOrigin(java.lang.String origin) {
    this.origin = origin;
  }

  public java.lang.String getSeverity() {
    return this.severity;
  }

  public void setSeverity(java.lang.String severity) {
    this.severity = severity;
  }

  public java.lang.String getShortDescription() {
    return this.shortDescription;
  }

  public void setShortDescription(java.lang.String shortDescription) {
    this.shortDescription = shortDescription;
  }

  private static final long serialVersionUID = 1L;



  @XmlElement(name="Description",required=false,nillable=false)
  protected Description description;

  @javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(javax.xml.bind.annotation.adapters.CollapsedStringAdapter.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="token")
  @XmlElement(name="ErrorDetail",required=false,nillable=false)
  protected java.lang.String errorDetail;

  @javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(javax.xml.bind.annotation.adapters.CollapsedStringAdapter.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="token")
  @XmlAttribute(name="category",required=false)
  protected java.lang.String category;

  @javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(javax.xml.bind.annotation.adapters.CollapsedStringAdapter.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="token")
  @XmlAttribute(name="refToMessageInError",required=false)
  protected java.lang.String refToMessageInError;

  @javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(javax.xml.bind.annotation.adapters.CollapsedStringAdapter.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="token")
  @XmlAttribute(name="errorCode",required=true)
  protected java.lang.String errorCode;

  @javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(javax.xml.bind.annotation.adapters.CollapsedStringAdapter.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="token")
  @XmlAttribute(name="origin",required=false)
  protected java.lang.String origin;

  @javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(javax.xml.bind.annotation.adapters.CollapsedStringAdapter.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="token")
  @XmlAttribute(name="severity",required=true)
  protected java.lang.String severity;

  @javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(javax.xml.bind.annotation.adapters.CollapsedStringAdapter.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="token")
  @XmlAttribute(name="shortDescription",required=false)
  protected java.lang.String shortDescription;

}
