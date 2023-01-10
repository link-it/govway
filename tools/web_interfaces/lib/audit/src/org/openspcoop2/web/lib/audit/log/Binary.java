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
package org.openspcoop2.web.lib.audit.log;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;


/** <p>Java class for binary complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="binary"&gt;
 * 		&lt;attribute name="binary-id" type="{http://www.w3.org/2001/XMLSchema}string" use="required"/&gt;
 * 		&lt;attribute name="checksum" type="{http://www.w3.org/2001/XMLSchema}long" use="required"/&gt;
 * 		&lt;attribute name="id-operation" type="{http://www.w3.org/2001/XMLSchema}unsignedLong" use="optional"/&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "binary")

@XmlRootElement(name = "binary")

public class Binary extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public Binary() {
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

  public java.lang.String getBinaryId() {
    return this.binaryId;
  }

  public void setBinaryId(java.lang.String binaryId) {
    this.binaryId = binaryId;
  }

  public long getChecksum() {
    return this.checksum;
  }

  public void setChecksum(long checksum) {
    this.checksum = checksum;
  }

  public java.lang.Long getIdOperation() {
    return this.idOperation;
  }

  public void setIdOperation(java.lang.Long idOperation) {
    this.idOperation = idOperation;
  }

  private static final long serialVersionUID = 1L;

  @XmlTransient
  private Long id;



  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="binary-id",required=true)
  protected java.lang.String binaryId;

  @javax.xml.bind.annotation.XmlSchemaType(name="long")
  @XmlAttribute(name="checksum",required=true)
  protected long checksum;

  @javax.xml.bind.annotation.XmlSchemaType(name="unsignedLong")
  @XmlAttribute(name="id-operation",required=false)
  protected java.lang.Long idOperation;

}
