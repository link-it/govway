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
package org.openspcoop2.core.registry;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import org.openspcoop2.core.registry.constants.RepresentationXmlType;
import java.io.Serializable;


/** <p>Java class for resource-representation-xml complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="resource-representation-xml"&gt;
 * 		&lt;attribute name="xml-type" type="{http://www.openspcoop2.org/core/registry}RepresentationXmlType" use="required"/&gt;
 * 		&lt;attribute name="nome" type="{http://www.w3.org/2001/XMLSchema}string" use="required"/&gt;
 * 		&lt;attribute name="namespace" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "resource-representation-xml")

@XmlRootElement(name = "resource-representation-xml")

public class ResourceRepresentationXml extends org.openspcoop2.utils.beans.BaseBeanWithId implements Serializable , Cloneable {
  public ResourceRepresentationXml() {
    super();
  }

  public void setXmlTypeRawEnumValue(String value) {
    this.xmlType = (RepresentationXmlType) RepresentationXmlType.toEnumConstantFromString(value);
  }

  public String getXmlTypeRawEnumValue() {
    if(this.xmlType == null){
    	return null;
    }else{
    	return this.xmlType.toString();
    }
  }

  public org.openspcoop2.core.registry.constants.RepresentationXmlType getXmlType() {
    return this.xmlType;
  }

  public void setXmlType(org.openspcoop2.core.registry.constants.RepresentationXmlType xmlType) {
    this.xmlType = xmlType;
  }

  public java.lang.String getNome() {
    return this.nome;
  }

  public void setNome(java.lang.String nome) {
    this.nome = nome;
  }

  public java.lang.String getNamespace() {
    return this.namespace;
  }

  public void setNamespace(java.lang.String namespace) {
    this.namespace = namespace;
  }

  private static final long serialVersionUID = 1L;



  @jakarta.xml.bind.annotation.XmlTransient
  protected java.lang.String xmlTypeRawEnumValue;

  @XmlAttribute(name="xml-type",required=true)
  protected RepresentationXmlType xmlType;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="nome",required=true)
  protected java.lang.String nome;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="namespace",required=false)
  protected java.lang.String namespace;

}
