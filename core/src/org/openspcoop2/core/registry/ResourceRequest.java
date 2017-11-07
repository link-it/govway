/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2017 Link.it srl (http://link.it).
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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import org.openspcoop2.core.registry.constants.MessageType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Java class for resource-request complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="resource-request">
 * 		&lt;sequence>
 * 			&lt;element name="representation" type="{http://www.openspcoop2.org/core/registry}resource-representation" minOccurs="0" maxOccurs="unbounded"/>
 * 		&lt;/sequence>
 * 		&lt;attribute name="id-resource" type="{http://www.w3.org/2001/XMLSchema}unsignedLong" use="optional"/>
 * 		&lt;attribute name="descrizione" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/>
 * 		&lt;attribute name="message-type" type="{http://www.openspcoop2.org/core/registry}MessageType" use="optional"/>
 * &lt;/complexType>
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "resource-request", 
  propOrder = {
  	"representation"
  }
)

@XmlRootElement(name = "resource-request")

public class ResourceRequest extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public ResourceRequest() {
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

  public void addRepresentation(ResourceRepresentation representation) {
    this.representation.add(representation);
  }

  public ResourceRepresentation getRepresentation(int index) {
    return this.representation.get( index );
  }

  public ResourceRepresentation removeRepresentation(int index) {
    return this.representation.remove( index );
  }

  public List<ResourceRepresentation> getRepresentationList() {
    return this.representation;
  }

  public void setRepresentationList(List<ResourceRepresentation> representation) {
    this.representation=representation;
  }

  public int sizeRepresentationList() {
    return this.representation.size();
  }

  public java.lang.Long getIdResource() {
    return this.idResource;
  }

  public void setIdResource(java.lang.Long idResource) {
    this.idResource = idResource;
  }

  public java.lang.String getDescrizione() {
    return this.descrizione;
  }

  public void setDescrizione(java.lang.String descrizione) {
    this.descrizione = descrizione;
  }

  public void set_value_messageType(String value) {
    this.messageType = (MessageType) MessageType.toEnumConstantFromString(value);
  }

  public String get_value_messageType() {
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

  private static final long serialVersionUID = 1L;

  @XmlTransient
  private Long id;



  @XmlElement(name="representation",required=true,nillable=false)
  protected List<ResourceRepresentation> representation = new ArrayList<ResourceRepresentation>();

  /**
   * @deprecated Use method getRepresentationList
   * @return List<ResourceRepresentation>
  */
  @Deprecated
  public List<ResourceRepresentation> getRepresentation() {
  	return this.representation;
  }

  /**
   * @deprecated Use method setRepresentationList
   * @param representation List<ResourceRepresentation>
  */
  @Deprecated
  public void setRepresentation(List<ResourceRepresentation> representation) {
  	this.representation=representation;
  }

  /**
   * @deprecated Use method sizeRepresentationList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizeRepresentation() {
  	return this.representation.size();
  }

  @javax.xml.bind.annotation.XmlTransient
  protected java.lang.Long idResource;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="descrizione",required=false)
  protected java.lang.String descrizione;

  @javax.xml.bind.annotation.XmlTransient
  protected java.lang.String _value_messageType;

  @XmlAttribute(name="message-type",required=false)
  protected MessageType messageType;

}
