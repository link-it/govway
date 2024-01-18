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
package backend.ecodex.org._1_1;

import backend.ecodex.org._1_1.constants.ErrorCode;
import backend.ecodex.org._1_1.constants.MshRole;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;


/** <p>Java class for errorResultImpl complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="errorResultImpl"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="errorCode" type="{http://org.ecodex.backend/1_1/}errorCode" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="errorDetail" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="messageInErrorId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="mshRole" type="{http://org.ecodex.backend/1_1/}mshRole" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="notified" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="timestamp" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0" maxOccurs="1"/&gt;
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
@XmlType(name = "errorResultImpl", 
  propOrder = {
  	"errorCode",
  	"errorDetail",
  	"messageInErrorId",
  	"mshRole",
  	"notified",
  	"timestamp"
  }
)

@XmlRootElement(name = "errorResultImpl")

public class ErrorResultImpl extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public ErrorResultImpl() {
    super();
  }

  public void setErrorCodeRawEnumValue(String value) {
    this.errorCode = (ErrorCode) ErrorCode.toEnumConstantFromString(value);
  }

  public String getErrorCodeRawEnumValue() {
    if(this.errorCode == null){
    	return null;
    }else{
    	return this.errorCode.toString();
    }
  }

  public backend.ecodex.org._1_1.constants.ErrorCode getErrorCode() {
    return this.errorCode;
  }

  public void setErrorCode(backend.ecodex.org._1_1.constants.ErrorCode errorCode) {
    this.errorCode = errorCode;
  }

  public java.lang.String getErrorDetail() {
    return this.errorDetail;
  }

  public void setErrorDetail(java.lang.String errorDetail) {
    this.errorDetail = errorDetail;
  }

  public java.lang.String getMessageInErrorId() {
    return this.messageInErrorId;
  }

  public void setMessageInErrorId(java.lang.String messageInErrorId) {
    this.messageInErrorId = messageInErrorId;
  }

  public void setMshRoleRawEnumValue(String value) {
    this.mshRole = (MshRole) MshRole.toEnumConstantFromString(value);
  }

  public String getMshRoleRawEnumValue() {
    if(this.mshRole == null){
    	return null;
    }else{
    	return this.mshRole.toString();
    }
  }

  public backend.ecodex.org._1_1.constants.MshRole getMshRole() {
    return this.mshRole;
  }

  public void setMshRole(backend.ecodex.org._1_1.constants.MshRole mshRole) {
    this.mshRole = mshRole;
  }

  public java.util.Date getNotified() {
    return this.notified;
  }

  public void setNotified(java.util.Date notified) {
    this.notified = notified;
  }

  public java.util.Date getTimestamp() {
    return this.timestamp;
  }

  public void setTimestamp(java.util.Date timestamp) {
    this.timestamp = timestamp;
  }

  private static final long serialVersionUID = 1L;



  @javax.xml.bind.annotation.XmlTransient
  protected java.lang.String errorCodeRawEnumValue;

  @XmlElement(name="errorCode",required=false,nillable=false)
  protected ErrorCode errorCode;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="errorDetail",required=false,nillable=false)
  protected java.lang.String errorDetail;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="messageInErrorId",required=false,nillable=false)
  protected java.lang.String messageInErrorId;

  @javax.xml.bind.annotation.XmlTransient
  protected java.lang.String mshRoleRawEnumValue;

  @XmlElement(name="mshRole",required=false,nillable=false)
  protected MshRole mshRole;

  @javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(org.openspcoop2.utils.jaxb.DateTime2String.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="dateTime")
  @XmlElement(name="notified",required=false,nillable=false,type=java.lang.String.class)
  protected java.util.Date notified;

  @javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(org.openspcoop2.utils.jaxb.DateTime2String.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="dateTime")
  @XmlElement(name="timestamp",required=false,nillable=false,type=java.lang.String.class)
  protected java.util.Date timestamp;

}
