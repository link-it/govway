/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it).
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
package org.openspcoop2.core.config;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;


/** <p>Java class for trasformazione-soap-risposta complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="trasformazione-soap-risposta">
 * 		&lt;sequence>
 * 			&lt;element name="envelope-body-conversione-template" type="{http://www.w3.org/2001/XMLSchema}base64Binary" minOccurs="0" maxOccurs="1"/>
 * 		&lt;/sequence>
 * 		&lt;attribute name="envelope" type="{http://www.w3.org/2001/XMLSchema}boolean" use="optional" default="true"/>
 * 		&lt;attribute name="envelope-as-attachment" type="{http://www.w3.org/2001/XMLSchema}boolean" use="optional" default="false"/>
 * 		&lt;attribute name="envelope-body-conversione-tipo" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/>
 * &lt;/complexType>
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "trasformazione-soap-risposta", 
  propOrder = {
  	"envelopeBodyConversioneTemplate"
  }
)

@XmlRootElement(name = "trasformazione-soap-risposta")

public class TrasformazioneSoapRisposta extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public TrasformazioneSoapRisposta() {
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

  public byte[] getEnvelopeBodyConversioneTemplate() {
    return this.envelopeBodyConversioneTemplate;
  }

  public void setEnvelopeBodyConversioneTemplate(byte[] envelopeBodyConversioneTemplate) {
    this.envelopeBodyConversioneTemplate = envelopeBodyConversioneTemplate;
  }

  public boolean isEnvelope() {
    return this.envelope;
  }

  public boolean getEnvelope() {
    return this.envelope;
  }

  public void setEnvelope(boolean envelope) {
    this.envelope = envelope;
  }

  public boolean isEnvelopeAsAttachment() {
    return this.envelopeAsAttachment;
  }

  public boolean getEnvelopeAsAttachment() {
    return this.envelopeAsAttachment;
  }

  public void setEnvelopeAsAttachment(boolean envelopeAsAttachment) {
    this.envelopeAsAttachment = envelopeAsAttachment;
  }

  public java.lang.String getEnvelopeBodyConversioneTipo() {
    return this.envelopeBodyConversioneTipo;
  }

  public void setEnvelopeBodyConversioneTipo(java.lang.String envelopeBodyConversioneTipo) {
    this.envelopeBodyConversioneTipo = envelopeBodyConversioneTipo;
  }

  private static final long serialVersionUID = 1L;

  @XmlTransient
  private Long id;



  @javax.xml.bind.annotation.XmlSchemaType(name="base64Binary")
  @XmlElement(name="envelope-body-conversione-template",required=false,nillable=false)
  protected byte[] envelopeBodyConversioneTemplate;

  @javax.xml.bind.annotation.XmlSchemaType(name="boolean")
  @XmlAttribute(name="envelope",required=false)
  protected boolean envelope = true;

  @javax.xml.bind.annotation.XmlSchemaType(name="boolean")
  @XmlAttribute(name="envelope-as-attachment",required=false)
  protected boolean envelopeAsAttachment = false;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="envelope-body-conversione-tipo",required=false)
  protected java.lang.String envelopeBodyConversioneTipo;

}
