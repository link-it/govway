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
package org.openspcoop2.message.context;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;


/** <p>Java class for content-length complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="content-length"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="outgoing-size" type="{http://www.w3.org/2001/XMLSchema}unsignedLong" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="incoming-size" type="{http://www.w3.org/2001/XMLSchema}unsignedLong" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="incoming-size-forced" type="{http://www.w3.org/2001/XMLSchema}unsignedLong" minOccurs="0" maxOccurs="1"/&gt;
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
@XmlType(name = "content-length", 
  propOrder = {
  	"outgoingSize",
  	"incomingSize",
  	"incomingSizeForced"
  }
)

@XmlRootElement(name = "content-length")

public class ContentLength extends org.openspcoop2.utils.beans.BaseBeanWithId implements Serializable , Cloneable {
  public ContentLength() {
    super();
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
