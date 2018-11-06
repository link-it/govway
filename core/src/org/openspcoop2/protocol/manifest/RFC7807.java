/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it).
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


/** <p>Java class for RFC7807 complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="RFC7807">
 * 		&lt;attribute name="useAcceptHeader" type="{http://www.w3.org/2001/XMLSchema}boolean" use="required"/>
 * 		&lt;attribute name="details" type="{http://www.w3.org/2001/XMLSchema}boolean" use="required"/>
 * 		&lt;attribute name="instance" type="{http://www.w3.org/2001/XMLSchema}boolean" use="required"/>
 * 		&lt;attribute name="govwayStatus" type="{http://www.w3.org/2001/XMLSchema}boolean" use="required"/>
 * 		&lt;attribute name="type" type="{http://www.w3.org/2001/XMLSchema}boolean" use="optional" default="true"/>
 * 		&lt;attribute name="typeFormat" type="{http://www.w3.org/2001/XMLSchema}string" use="optional" default="https://httpstatuses.com/%d"/>
 * &lt;/complexType>
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RFC7807")

@XmlRootElement(name = "RFC7807")

public class RFC7807 extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public RFC7807() {
  }

  public boolean isUseAcceptHeader() {
    return this.useAcceptHeader;
  }

  public boolean getUseAcceptHeader() {
    return this.useAcceptHeader;
  }

  public void setUseAcceptHeader(boolean useAcceptHeader) {
    this.useAcceptHeader = useAcceptHeader;
  }

  public boolean isDetails() {
    return this.details;
  }

  public boolean getDetails() {
    return this.details;
  }

  public void setDetails(boolean details) {
    this.details = details;
  }

  public boolean isInstance() {
    return this.instance;
  }

  public boolean getInstance() {
    return this.instance;
  }

  public void setInstance(boolean instance) {
    this.instance = instance;
  }

  public boolean isGovwayStatus() {
    return this.govwayStatus;
  }

  public boolean getGovwayStatus() {
    return this.govwayStatus;
  }

  public void setGovwayStatus(boolean govwayStatus) {
    this.govwayStatus = govwayStatus;
  }

  public boolean isType() {
    return this.type;
  }

  public boolean getType() {
    return this.type;
  }

  public void setType(boolean type) {
    this.type = type;
  }

  public java.lang.String getTypeFormat() {
    return this.typeFormat;
  }

  public void setTypeFormat(java.lang.String typeFormat) {
    this.typeFormat = typeFormat;
  }

  private static final long serialVersionUID = 1L;



  @javax.xml.bind.annotation.XmlSchemaType(name="boolean")
  @XmlAttribute(name="useAcceptHeader",required=true)
  protected boolean useAcceptHeader;

  @javax.xml.bind.annotation.XmlSchemaType(name="boolean")
  @XmlAttribute(name="details",required=true)
  protected boolean details;

  @javax.xml.bind.annotation.XmlSchemaType(name="boolean")
  @XmlAttribute(name="instance",required=true)
  protected boolean instance;

  @javax.xml.bind.annotation.XmlSchemaType(name="boolean")
  @XmlAttribute(name="govwayStatus",required=true)
  protected boolean govwayStatus;

  @javax.xml.bind.annotation.XmlSchemaType(name="boolean")
  @XmlAttribute(name="type",required=false)
  protected boolean type = true;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="typeFormat",required=false)
  protected java.lang.String typeFormat = "https://httpstatuses.com/%d";

}
