/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it).
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
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;


/** <p>Java class for payload complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="payload"&gt;
 * 		&lt;attribute name="name" type="{http://www.domibus.eu/configuration}string" use="required"/&gt;
 * 		&lt;attribute name="cid" type="{http://www.domibus.eu/configuration}string" use="required"/&gt;
 * 		&lt;attribute name="mimeType" type="{http://www.domibus.eu/configuration}string" use="optional"/&gt;
 * 		&lt;attribute name="inBody" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/&gt;
 * 		&lt;attribute name="schemaFile" type="{http://www.w3.org/2001/XMLSchema}anyURI" use="optional"/&gt;
 * 		&lt;attribute name="maxSize" type="{http://www.w3.org/2001/XMLSchema}integer" use="optional"/&gt;
 * 		&lt;attribute name="required" type="{http://www.w3.org/2001/XMLSchema}boolean" use="required"/&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "payload")

@XmlRootElement(name = "payload")

public class Payload extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public Payload() {
  }

  public java.lang.String getName() {
    return this.name;
  }

  public void setName(java.lang.String name) {
    this.name = name;
  }

  public java.lang.String getCid() {
    return this.cid;
  }

  public void setCid(java.lang.String cid) {
    this.cid = cid;
  }

  public java.lang.String getMimeType() {
    return this.mimeType;
  }

  public void setMimeType(java.lang.String mimeType) {
    this.mimeType = mimeType;
  }

  public java.lang.String getInBody() {
    return this.inBody;
  }

  public void setInBody(java.lang.String inBody) {
    this.inBody = inBody;
  }

  public java.net.URI getSchemaFile() {
    return this.schemaFile;
  }

  public void setSchemaFile(java.net.URI schemaFile) {
    this.schemaFile = schemaFile;
  }

  public java.math.BigInteger getMaxSize() {
    return this.maxSize;
  }

  public void setMaxSize(java.math.BigInteger maxSize) {
    this.maxSize = maxSize;
  }

  public boolean isRequired() {
    return this.required;
  }

  public boolean getRequired() {
    return this.required;
  }

  public void setRequired(boolean required) {
    this.required = required;
  }

  private static final long serialVersionUID = 1L;



  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="name",required=true)
  protected java.lang.String name;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="cid",required=true)
  protected java.lang.String cid;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="mimeType",required=false)
  protected java.lang.String mimeType;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="inBody",required=false)
  protected java.lang.String inBody;

  @javax.xml.bind.annotation.XmlSchemaType(name="anyURI")
  @XmlAttribute(name="schemaFile",required=false)
  protected java.net.URI schemaFile;

  @javax.xml.bind.annotation.XmlSchemaType(name="integer")
  @XmlAttribute(name="maxSize",required=false)
  protected java.math.BigInteger maxSize;

  @javax.xml.bind.annotation.XmlSchemaType(name="boolean")
  @XmlAttribute(name="required",required=true)
  protected boolean required;

}
