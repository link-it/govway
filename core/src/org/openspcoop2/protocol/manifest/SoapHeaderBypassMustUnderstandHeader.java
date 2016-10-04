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
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;


/** <p>Java class for soapHeaderBypassMustUnderstandHeader complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="soapHeaderBypassMustUnderstandHeader">
 * 		&lt;sequence>
 * 			&lt;element name="localName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="1" maxOccurs="1"/>
 * 			&lt;element name="namespace" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="1" maxOccurs="1"/>
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
@XmlType(name = "soapHeaderBypassMustUnderstandHeader", 
  propOrder = {
  	"localName",
  	"namespace"
  }
)

@XmlRootElement(name = "soapHeaderBypassMustUnderstandHeader")

public class SoapHeaderBypassMustUnderstandHeader extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public SoapHeaderBypassMustUnderstandHeader() {
  }

  public Long getId() {
    if(this.id!=null)
		return this.id;
	else
		return new Long(-1);
  }

  public void setId(Long id) {
    if(id!=null)
		this.id=id;
	else
		this.id=new Long(-1);
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

  @XmlTransient
  private Long id;



  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="localName",required=true,nillable=false)
  protected java.lang.String localName;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="namespace",required=true,nillable=false)
  protected java.lang.String namespace;

}
