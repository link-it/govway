/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it).
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
package eu.domibus.configuration;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Java class for party complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="party"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="identifier" type="{http://www.domibus.eu/configuration}identifier" minOccurs="1" maxOccurs="unbounded"/&gt;
 * 		&lt;/sequence&gt;
 * 		&lt;attribute name="name" type="{http://www.w3.org/2001/XMLSchema}string" use="required"/&gt;
 * 		&lt;attribute name="userName" type="{http://www.domibus.eu/configuration}string" use="optional"/&gt;
 * 		&lt;attribute name="password" type="{http://www.domibus.eu/configuration}string" use="optional"/&gt;
 * 		&lt;attribute name="endpoint" type="{http://www.w3.org/2001/XMLSchema}anyURI" use="required"/&gt;
 * 		&lt;attribute name="allowChunking" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "party", 
  propOrder = {
  	"identifier"
  }
)

@XmlRootElement(name = "party")

public class Party extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public Party() {
  }

  public void addIdentifier(Identifier identifier) {
    this.identifier.add(identifier);
  }

  public Identifier getIdentifier(int index) {
    return this.identifier.get( index );
  }

  public Identifier removeIdentifier(int index) {
    return this.identifier.remove( index );
  }

  public List<Identifier> getIdentifierList() {
    return this.identifier;
  }

  public void setIdentifierList(List<Identifier> identifier) {
    this.identifier=identifier;
  }

  public int sizeIdentifierList() {
    return this.identifier.size();
  }

  public java.lang.String getName() {
    return this.name;
  }

  public void setName(java.lang.String name) {
    this.name = name;
  }

  public java.lang.String getUserName() {
    return this.userName;
  }

  public void setUserName(java.lang.String userName) {
    this.userName = userName;
  }

  public java.lang.String getPassword() {
    return this.password;
  }

  public void setPassword(java.lang.String password) {
    this.password = password;
  }

  public java.net.URI getEndpoint() {
    return this.endpoint;
  }

  public void setEndpoint(java.net.URI endpoint) {
    this.endpoint = endpoint;
  }

  public java.lang.String getAllowChunking() {
    return this.allowChunking;
  }

  public void setAllowChunking(java.lang.String allowChunking) {
    this.allowChunking = allowChunking;
  }

  private static final long serialVersionUID = 1L;



  @XmlElement(name="identifier",required=true,nillable=false)
  protected List<Identifier> identifier = new ArrayList<Identifier>();

  /**
   * @deprecated Use method getIdentifierList
   * @return List&lt;Identifier&gt;
  */
  @Deprecated
  public List<Identifier> getIdentifier() {
  	return this.identifier;
  }

  /**
   * @deprecated Use method setIdentifierList
   * @param identifier List&lt;Identifier&gt;
  */
  @Deprecated
  public void setIdentifier(List<Identifier> identifier) {
  	this.identifier=identifier;
  }

  /**
   * @deprecated Use method sizeIdentifierList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizeIdentifier() {
  	return this.identifier.size();
  }

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="name",required=true)
  protected java.lang.String name;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="userName",required=false)
  protected java.lang.String userName;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="password",required=false)
  protected java.lang.String password;

  @javax.xml.bind.annotation.XmlSchemaType(name="anyURI")
  @XmlAttribute(name="endpoint",required=true)
  protected java.net.URI endpoint;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="allowChunking",required=false)
  protected java.lang.String allowChunking;

}
