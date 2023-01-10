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
package org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;


/** <p>Java class for PartInfo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PartInfo"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="Schema" type="{http://docs.oasis-open.org/ebxml-msg/ebms/v3.0/ns/core/200704/}Schema" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="Description" type="{http://docs.oasis-open.org/ebxml-msg/ebms/v3.0/ns/core/200704/}Description" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="PartProperties" type="{http://docs.oasis-open.org/ebxml-msg/ebms/v3.0/ns/core/200704/}PartProperties" minOccurs="0" maxOccurs="1"/&gt;
 * 		&lt;/sequence&gt;
 * 		&lt;attribute name="href" type="{http://www.w3.org/2001/XMLSchema}token" use="optional"/&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PartInfo", 
  propOrder = {
  	"schema",
  	"description",
  	"partProperties"
  }
)

@XmlRootElement(name = "PartInfo")

public class PartInfo extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public PartInfo() {
  }

  public Schema getSchema() {
    return this.schema;
  }

  public void setSchema(Schema schema) {
    this.schema = schema;
  }

  public Description getDescription() {
    return this.description;
  }

  public void setDescription(Description description) {
    this.description = description;
  }

  public PartProperties getPartProperties() {
    return this.partProperties;
  }

  public void setPartProperties(PartProperties partProperties) {
    this.partProperties = partProperties;
  }

  public java.lang.String getHref() {
    return this.href;
  }

  public void setHref(java.lang.String href) {
    this.href = href;
  }

  private static final long serialVersionUID = 1L;



  @XmlElement(name="Schema",required=false,nillable=false)
  protected Schema schema;

  @XmlElement(name="Description",required=false,nillable=false)
  protected Description description;

  @XmlElement(name="PartProperties",required=false,nillable=false)
  protected PartProperties partProperties;

  @javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(javax.xml.bind.annotation.adapters.CollapsedStringAdapter.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="token")
  @XmlAttribute(name="href",required=false)
  protected java.lang.String href;

}
