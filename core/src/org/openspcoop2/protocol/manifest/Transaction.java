/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it).
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
package org.openspcoop2.protocol.manifest;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;


/** <p>Java class for transaction complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="transaction">
 * 		&lt;attribute name="errorProtocol" type="{http://www.w3.org/2001/XMLSchema}boolean" use="optional" default="false"/>
 * 		&lt;attribute name="labelErrorProtocol" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/>
 * 		&lt;attribute name="externalFault" type="{http://www.w3.org/2001/XMLSchema}boolean" use="optional" default="false"/>
 * 		&lt;attribute name="labelExternalFault" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/>
 * &lt;/complexType>
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "transaction")

@XmlRootElement(name = "transaction")

public class Transaction extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public Transaction() {
  }

  public boolean isErrorProtocol() {
    return this.errorProtocol;
  }

  public boolean getErrorProtocol() {
    return this.errorProtocol;
  }

  public void setErrorProtocol(boolean errorProtocol) {
    this.errorProtocol = errorProtocol;
  }

  public java.lang.String getLabelErrorProtocol() {
    return this.labelErrorProtocol;
  }

  public void setLabelErrorProtocol(java.lang.String labelErrorProtocol) {
    this.labelErrorProtocol = labelErrorProtocol;
  }

  public boolean isExternalFault() {
    return this.externalFault;
  }

  public boolean getExternalFault() {
    return this.externalFault;
  }

  public void setExternalFault(boolean externalFault) {
    this.externalFault = externalFault;
  }

  public java.lang.String getLabelExternalFault() {
    return this.labelExternalFault;
  }

  public void setLabelExternalFault(java.lang.String labelExternalFault) {
    this.labelExternalFault = labelExternalFault;
  }

  private static final long serialVersionUID = 1L;



  @javax.xml.bind.annotation.XmlSchemaType(name="boolean")
  @XmlAttribute(name="errorProtocol",required=false)
  protected boolean errorProtocol = false;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="labelErrorProtocol",required=false)
  protected java.lang.String labelErrorProtocol;

  @javax.xml.bind.annotation.XmlSchemaType(name="boolean")
  @XmlAttribute(name="externalFault",required=false)
  protected boolean externalFault = false;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="labelExternalFault",required=false)
  protected java.lang.String labelExternalFault;

}
