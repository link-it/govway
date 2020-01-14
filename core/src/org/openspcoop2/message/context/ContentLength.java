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
package org.openspcoop2.message.context;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;


/** <p>Java class for content-length complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="content-length">
 * 		&lt;sequence>
 * 			&lt;element name="outgoing-size" type="{http://www.w3.org/2001/XMLSchema}unsignedLong" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="incoming-size" type="{http://www.w3.org/2001/XMLSchema}unsignedLong" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="incoming-size-forced" type="{http://www.w3.org/2001/XMLSchema}unsignedLong" minOccurs="0" maxOccurs="1"/>
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
@XmlType(name = "content-length", 
  propOrder = {
  	"outgoingSize",
  	"incomingSize",
  	"incomingSizeForced"
  }
)

@XmlRootElement(name = "content-length")

public class ContentLength extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public ContentLength() {
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

  public java.lang.Long getOutgoingSize() {
    return this.outgoingSize;
  }

  public void setOutgoingSize(java.lang.Long outgoingSize) {
    this.outgoingSize = outgoingSize;
  }

  public java.lang.Long getIncomingSize() {
    return this.incomingSize;
  }

  public void setIncomingSize(java.lang.Long incomingSize) {
    this.incomingSize = incomingSize;
  }

  public java.lang.Long getIncomingSizeForced() {
    return this.incomingSizeForced;
  }

  public void setIncomingSizeForced(java.lang.Long incomingSizeForced) {
    this.incomingSizeForced = incomingSizeForced;
  }

  private static final long serialVersionUID = 1L;

  @XmlTransient
  private Long id;



  @javax.xml.bind.annotation.XmlSchemaType(name="unsignedLong")
  @XmlElement(name="outgoing-size",required=false,nillable=false)
  protected java.lang.Long outgoingSize;

  @javax.xml.bind.annotation.XmlSchemaType(name="unsignedLong")
  @XmlElement(name="incoming-size",required=false,nillable=false)
  protected java.lang.Long incomingSize;

  @javax.xml.bind.annotation.XmlSchemaType(name="unsignedLong")
  @XmlElement(name="incoming-size-forced",required=false,nillable=false)
  protected java.lang.Long incomingSizeForced;

}
