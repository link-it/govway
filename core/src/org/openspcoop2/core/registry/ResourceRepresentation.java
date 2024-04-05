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
package org.openspcoop2.core.registry;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import org.openspcoop2.core.registry.constants.MessageType;
import org.openspcoop2.core.registry.constants.RepresentationType;
import java.io.Serializable;


/** <p>Java class for resource-representation complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="resource-representation"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="xml" type="{http://www.openspcoop2.org/core/registry}resource-representation-xml" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="json" type="{http://www.openspcoop2.org/core/registry}resource-representation-json" minOccurs="0" maxOccurs="1"/&gt;
 * 		&lt;/sequence&gt;
 * 		&lt;attribute name="media-type" type="{http://www.w3.org/2001/XMLSchema}string" use="required"/&gt;
 * 		&lt;attribute name="nome" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/&gt;
 * 		&lt;attribute name="descrizione" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/&gt;
 * 		&lt;attribute name="message-type" type="{http://www.openspcoop2.org/core/registry}MessageType" use="optional"/&gt;
 * 		&lt;attribute name="representation-type" type="{http://www.openspcoop2.org/core/registry}RepresentationType" use="optional"/&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "resource-representation", 
  propOrder = {
  	"xml",
  	"json"
  }
)

@XmlRootElement(name = "resource-representation")

public class ResourceRepresentation extends org.openspcoop2.utils.beans.BaseBeanWithId implements Serializable , Cloneable {
  public ResourceRepresentation() {
    super();
  }

  public ResourceRepresentationXml getXml() {
    return this.xml;
  }

  public void setXml(ResourceRepresentationXml xml) {
    this.xml = xml;
  }

  public ResourceRepresentationJson getJson() {
    return this.json;
  }

  public void setJson(ResourceRepresentationJson json) {
    this.json = json;
  }

  public java.lang.String getMediaType() {
    return this.mediaType;
  }

  public void setMediaType(java.lang.String mediaType) {
    this.mediaType = mediaType;
  }

  public java.lang.String getNome() {
    return this.nome;
  }

  public void setNome(java.lang.String nome) {
    this.nome = nome;
  }

  public java.lang.String getDescrizione() {
    return this.descrizione;
  }

  public void setDescrizione(java.lang.String descrizione) {
    this.descrizione = descrizione;
  }

  public void setMessageTypeRawEnumValue(String value) {
    this.messageType = (MessageType) MessageType.toEnumConstantFromString(value);
  }

  public String getMessageTypeRawEnumValue() {
    if(this.messageType == null){
    	return null;
    }else{
    	return this.messageType.toString();
    }
  }

  public org.openspcoop2.core.registry.constants.MessageType getMessageType() {
    return this.messageType;
  }

  public void setMessageType(org.openspcoop2.core.registry.constants.MessageType messageType) {
    this.messageType = messageType;
  }

  public void setRepresentationTypeRawEnumValue(String value) {
    this.representationType = (RepresentationType) RepresentationType.toEnumConstantFromString(value);
  }

  public String getRepresentationTypeRawEnumValue() {
    if(this.representationType == null){
    	return null;
    }else{
    	return this.representationType.toString();
    }
  }

  public org.openspcoop2.core.registry.constants.RepresentationType getRepresentationType() {
    return this.representationType;
  }

  public void setRepresentationType(org.openspcoop2.core.registry.constants.RepresentationType representationType) {
    this.representationType = representationType;
  }

  private static final long serialVersionUID = 1L;



  @XmlElement(name="xml",required=false,nillable=false)
  protected ResourceRepresentationXml xml;

  @XmlElement(name="json",required=false,nillable=false)
  protected ResourceRepresentationJson json;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="media-type",required=true)
  protected java.lang.String mediaType;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="nome",required=false)
  protected java.lang.String nome;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="descrizione",required=false)
  protected java.lang.String descrizione;

  @jakarta.xml.bind.annotation.XmlTransient
  protected java.lang.String messageTypeRawEnumValue;

  @XmlAttribute(name="message-type",required=false)
  protected MessageType messageType;

  @jakarta.xml.bind.annotation.XmlTransient
  protected java.lang.String representationTypeRawEnumValue;

  @XmlAttribute(name="representation-type",required=false)
  protected RepresentationType representationType;

}
