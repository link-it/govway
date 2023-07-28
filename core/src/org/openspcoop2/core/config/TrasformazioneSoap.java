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
package org.openspcoop2.core.config;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import org.openspcoop2.core.config.constants.VersioneSOAP;
import java.io.Serializable;


/** <p>Java class for trasformazione-soap complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="trasformazione-soap"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="envelope-body-conversione-template" type="{http://www.w3.org/2001/XMLSchema}base64Binary" minOccurs="0" maxOccurs="1"/&gt;
 * 		&lt;/sequence&gt;
 * 		&lt;attribute name="envelope" type="{http://www.w3.org/2001/XMLSchema}boolean" use="optional" default="true"/&gt;
 * 		&lt;attribute name="versione" type="{http://www.openspcoop2.org/core/config}VersioneSOAP" use="optional"/&gt;
 * 		&lt;attribute name="soap-action" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/&gt;
 * 		&lt;attribute name="envelope-as-attachment" type="{http://www.w3.org/2001/XMLSchema}boolean" use="optional" default="false"/&gt;
 * 		&lt;attribute name="envelope-body-conversione-tipo" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "trasformazione-soap", 
  propOrder = {
  	"envelopeBodyConversioneTemplate"
  }
)

@XmlRootElement(name = "trasformazione-soap")

public class TrasformazioneSoap extends org.openspcoop2.utils.beans.BaseBeanWithId implements Serializable , Cloneable {
  public TrasformazioneSoap() {
    super();
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

  public void setVersioneRawEnumValue(String value) {
    this.versione = (VersioneSOAP) VersioneSOAP.toEnumConstantFromString(value);
  }

  public String getVersioneRawEnumValue() {
    if(this.versione == null){
    	return null;
    }else{
    	return this.versione.toString();
    }
  }

  public org.openspcoop2.core.config.constants.VersioneSOAP getVersione() {
    return this.versione;
  }

  public void setVersione(org.openspcoop2.core.config.constants.VersioneSOAP versione) {
    this.versione = versione;
  }

  public java.lang.String getSoapAction() {
    return this.soapAction;
  }

  public void setSoapAction(java.lang.String soapAction) {
    this.soapAction = soapAction;
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



  @jakarta.xml.bind.annotation.XmlSchemaType(name="base64Binary")
  @XmlElement(name="envelope-body-conversione-template",required=false,nillable=false)
  protected byte[] envelopeBodyConversioneTemplate;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="boolean")
  @XmlAttribute(name="envelope",required=false)
  protected boolean envelope = true;

  @jakarta.xml.bind.annotation.XmlTransient
  protected java.lang.String versioneRawEnumValue;

  @XmlAttribute(name="versione",required=false)
  protected VersioneSOAP versione;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="soap-action",required=false)
  protected java.lang.String soapAction;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="boolean")
  @XmlAttribute(name="envelope-as-attachment",required=false)
  protected boolean envelopeAsAttachment = false;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="envelope-body-conversione-tipo",required=false)
  protected java.lang.String envelopeBodyConversioneTipo;

}
