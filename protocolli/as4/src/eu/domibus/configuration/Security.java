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
package eu.domibus.configuration;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;


/** <p>Java class for security complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="security">
 * 		&lt;attribute name="name" type="{http://www.domibus.eu/configuration}string" use="required"/>
 * 		&lt;attribute name="policy" type="{http://www.domibus.eu/configuration}string" use="required"/>
 * 		&lt;attribute name="signatureMethod" type="{http://www.domibus.eu/configuration}string" use="required"/>
 * &lt;/complexType>
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "security")

@XmlRootElement(name = "security")

public class Security extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public Security() {
  }

  public java.lang.String getName() {
    return this.name;
  }

  public void setName(java.lang.String name) {
    this.name = name;
  }

  public java.lang.String getPolicy() {
    return this.policy;
  }

  public void setPolicy(java.lang.String policy) {
    this.policy = policy;
  }

  public java.lang.String getSignatureMethod() {
    return this.signatureMethod;
  }

  public void setSignatureMethod(java.lang.String signatureMethod) {
    this.signatureMethod = signatureMethod;
  }

  private static final long serialVersionUID = 1L;



  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="name",required=true)
  protected java.lang.String name;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="policy",required=true)
  protected java.lang.String policy;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="signatureMethod",required=true)
  protected java.lang.String signatureMethod;

}
