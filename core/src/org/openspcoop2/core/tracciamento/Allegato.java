/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it).
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
package org.openspcoop2.core.tracciamento;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import java.io.Serializable;


/** <p>Java class for allegato complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="allegato"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="content-id" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="content-location" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="content-type" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="digest" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/&gt;
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
@XmlType(name = "allegato", 
  propOrder = {
  	"contentId",
  	"contentLocation",
  	"contentType",
  	"digest"
  }
)

@XmlRootElement(name = "allegato")

public class Allegato extends org.openspcoop2.utils.beans.BaseBeanWithId implements Serializable , Cloneable {
  public Allegato() {
    super();
  }

  public java.lang.String getContentId() {
    return this.contentId;
  }

  public void setContentId(java.lang.String contentId) {
    this.contentId = contentId;
  }

  public java.lang.String getContentLocation() {
    return this.contentLocation;
  }

  public void setContentLocation(java.lang.String contentLocation) {
    this.contentLocation = contentLocation;
  }

  public java.lang.String getContentType() {
    return this.contentType;
  }

  public void setContentType(java.lang.String contentType) {
    this.contentType = contentType;
  }

  public java.lang.String getDigest() {
    return this.digest;
  }

  public void setDigest(java.lang.String digest) {
    this.digest = digest;
  }

  private static final long serialVersionUID = 1L;



  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="content-id",required=false,nillable=false)
  protected java.lang.String contentId;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="content-location",required=false,nillable=false)
  protected java.lang.String contentLocation;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="content-type",required=true,nillable=false)
  protected java.lang.String contentType;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="digest",required=false,nillable=false)
  protected java.lang.String digest;

}
