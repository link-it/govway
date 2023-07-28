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

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import java.io.Serializable;


/** <p>Java class for SoapHeaderBypassMustUnderstandHeader complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SoapHeaderBypassMustUnderstandHeader"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="localName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="namespace" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="1" maxOccurs="1"/&gt;
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
@XmlType(name = "SoapHeaderBypassMustUnderstandHeader", 
  propOrder = {
  	"localName",
  	"namespace"
  }
)

@XmlRootElement(name = "SoapHeaderBypassMustUnderstandHeader")

public class SoapHeaderBypassMustUnderstandHeader extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public SoapHeaderBypassMustUnderstandHeader() {
    super();
  }

  public java.lang.String getLocalName() {
    return this.localName;
  }

  public void setLocalName(java.lang.String localName) {
    this.localName = localName;
  }

  public java.lang.String getNamespace() {
    return this.namespace;
  }

  public void setNamespace(java.lang.String namespace) {
    this.namespace = namespace;
  }

  private static final long serialVersionUID = 1L;



  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="localName",required=true,nillable=false)
  protected java.lang.String localName;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="namespace",required=true,nillable=false)
  protected java.lang.String namespace;

}
