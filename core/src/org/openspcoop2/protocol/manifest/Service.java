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


/** <p>Java class for Service complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Service">
 * 		&lt;sequence>
 * 			&lt;element name="types" type="{http://www.openspcoop2.org/protocol/manifest}ServiceTypes" minOccurs="1" maxOccurs="1"/>
 * 			&lt;element name="profile" type="{http://www.openspcoop2.org/protocol/manifest}CollaborationProfile" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="functionality" type="{http://www.openspcoop2.org/protocol/manifest}Functionality" minOccurs="0" maxOccurs="1"/>
 * 		&lt;/sequence>
 * 		&lt;attribute name="wsdlSchema" type="{http://www.w3.org/2001/XMLSchema}boolean" use="optional" default="false"/>
 * 		&lt;attribute name="conversations" type="{http://www.w3.org/2001/XMLSchema}boolean" use="optional" default="false"/>
 * &lt;/complexType>
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Service", 
  propOrder = {
  	"types",
  	"profile",
  	"functionality"
  }
)

@XmlRootElement(name = "Service")

public class Service extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public Service() {
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

  public ServiceTypes getTypes() {
    return this.types;
  }

  public void setTypes(ServiceTypes types) {
    this.types = types;
  }

  public CollaborationProfile getProfile() {
    return this.profile;
  }

  public void setProfile(CollaborationProfile profile) {
    this.profile = profile;
  }

  public Functionality getFunctionality() {
    return this.functionality;
  }

  public void setFunctionality(Functionality functionality) {
    this.functionality = functionality;
  }

  public boolean isWsdlSchema() {
    return this.wsdlSchema;
  }

  public boolean getWsdlSchema() {
    return this.wsdlSchema;
  }

  public void setWsdlSchema(boolean wsdlSchema) {
    this.wsdlSchema = wsdlSchema;
  }

  public boolean isConversations() {
    return this.conversations;
  }

  public boolean getConversations() {
    return this.conversations;
  }

  public void setConversations(boolean conversations) {
    this.conversations = conversations;
  }

  private static final long serialVersionUID = 1L;

  @XmlTransient
  private Long id;



  @XmlElement(name="types",required=true,nillable=false)
  protected ServiceTypes types;

  @XmlElement(name="profile",required=false,nillable=false)
  protected CollaborationProfile profile;

  @XmlElement(name="functionality",required=false,nillable=false)
  protected Functionality functionality;

  @javax.xml.bind.annotation.XmlSchemaType(name="boolean")
  @XmlAttribute(name="wsdlSchema",required=false)
  protected boolean wsdlSchema = false;

  @javax.xml.bind.annotation.XmlSchemaType(name="boolean")
  @XmlAttribute(name="conversations",required=false)
  protected boolean conversations = false;

}
