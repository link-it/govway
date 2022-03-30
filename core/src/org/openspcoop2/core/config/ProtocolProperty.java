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
package org.openspcoop2.core.config;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;


/** <p>Java class for protocol-property complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="protocol-property"&gt;
 * 		&lt;attribute name="name" type="{http://www.w3.org/2001/XMLSchema}string" use="required"/&gt;
 * 		&lt;attribute name="value" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/&gt;
 * 		&lt;attribute name="number-value" type="{http://www.w3.org/2001/XMLSchema}unsignedLong" use="optional"/&gt;
 * 		&lt;attribute name="boolean-value" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/&gt;
 * 		&lt;attribute name="file" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/&gt;
 * 		&lt;attribute name="byte-file" type="{http://www.w3.org/2001/XMLSchema}base64Binary" use="optional"/&gt;
 * 		&lt;attribute name="tipo-proprietario" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/&gt;
 * 		&lt;attribute name="id-proprietario" type="{http://www.w3.org/2001/XMLSchema}unsignedLong" use="optional"/&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "protocol-property")

@XmlRootElement(name = "protocol-property")

public class ProtocolProperty extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public ProtocolProperty() {
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

  public java.lang.String getName() {
    return this.name;
  }

  public void setName(java.lang.String name) {
    this.name = name;
  }

  public java.lang.String getValue() {
    return this.value;
  }

  public void setValue(java.lang.String value) {
    this.value = value;
  }

  public java.lang.Long getNumberValue() {
    return this.numberValue;
  }

  public void setNumberValue(java.lang.Long numberValue) {
    this.numberValue = numberValue;
  }

  public Boolean getBooleanValue() {
    return this.booleanValue;
  }

  public void setBooleanValue(Boolean booleanValue) {
    this.booleanValue = booleanValue;
  }

  public java.lang.String getFile() {
    return this.file;
  }

  public void setFile(java.lang.String file) {
    this.file = file;
  }

  public byte[] getByteFile() {
    return this.byteFile;
  }

  public void setByteFile(byte[] byteFile) {
    this.byteFile = byteFile;
  }

  public java.lang.String getTipoProprietario() {
    return this.tipoProprietario;
  }

  public void setTipoProprietario(java.lang.String tipoProprietario) {
    this.tipoProprietario = tipoProprietario;
  }

  public java.lang.Long getIdProprietario() {
    return this.idProprietario;
  }

  public void setIdProprietario(java.lang.Long idProprietario) {
    this.idProprietario = idProprietario;
  }

  private static final long serialVersionUID = 1L;

  @XmlTransient
  private Long id;



  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="name",required=true)
  protected java.lang.String name;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="value",required=false)
  protected java.lang.String value;

  @javax.xml.bind.annotation.XmlSchemaType(name="unsignedLong")
  @XmlAttribute(name="number-value",required=false)
  protected java.lang.Long numberValue;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="boolean-value",required=false)
  protected Boolean booleanValue;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="file",required=false)
  protected java.lang.String file;

  @javax.xml.bind.annotation.XmlSchemaType(name="base64Binary")
  @XmlAttribute(name="byte-file",required=false)
  protected byte[] byteFile;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="tipo-proprietario",required=false)
  protected java.lang.String tipoProprietario;

  @javax.xml.bind.annotation.XmlTransient
  protected java.lang.Long idProprietario;

}
