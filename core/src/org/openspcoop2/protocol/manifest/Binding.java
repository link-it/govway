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
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;


/** <p>Java class for binding complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="binding">
 * 		&lt;sequence>
 * 			&lt;element name="soapHeaderBypassMustUnderstand" type="{http://www.openspcoop2.org/protocol/manifest}soapHeaderBypassMustUnderstand" minOccurs="0" maxOccurs="1"/>
 * 		&lt;/sequence>
 * 		&lt;attribute name="soap11" type="{http://www.w3.org/2001/XMLSchema}boolean" use="required"/>
 * 		&lt;attribute name="soap12" type="{http://www.w3.org/2001/XMLSchema}boolean" use="required"/>
 * &lt;/complexType>
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "binding", 
  propOrder = {
  	"soapHeaderBypassMustUnderstand"
  }
)

@XmlRootElement(name = "binding")

public class Binding extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public Binding() {
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

  public SoapHeaderBypassMustUnderstand getSoapHeaderBypassMustUnderstand() {
    return this.soapHeaderBypassMustUnderstand;
  }

  public void setSoapHeaderBypassMustUnderstand(SoapHeaderBypassMustUnderstand soapHeaderBypassMustUnderstand) {
    this.soapHeaderBypassMustUnderstand = soapHeaderBypassMustUnderstand;
  }

  public boolean isSoap11() {
    return this.soap11;
  }

  public boolean getSoap11() {
    return this.soap11;
  }

  public void setSoap11(boolean soap11) {
    this.soap11 = soap11;
  }

  public boolean isSoap12() {
    return this.soap12;
  }

  public boolean getSoap12() {
    return this.soap12;
  }

  public void setSoap12(boolean soap12) {
    this.soap12 = soap12;
  }

  private static final long serialVersionUID = 1L;

  @XmlTransient
  private Long id;



  @XmlElement(name="soapHeaderBypassMustUnderstand",required=false,nillable=false)
  protected SoapHeaderBypassMustUnderstand soapHeaderBypassMustUnderstand;

  @javax.xml.bind.annotation.XmlSchemaType(name="boolean")
  @XmlAttribute(name="soap11",required=true)
  protected boolean soap11;

  @javax.xml.bind.annotation.XmlSchemaType(name="boolean")
  @XmlAttribute(name="soap12",required=true)
  protected boolean soap12;

}
