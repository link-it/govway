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
package eu.domibus.configuration;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import java.io.Serializable;


/** <p>Java class for mep complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="mep"&gt;
 * 		&lt;attribute name="name" type="{http://www.domibus.eu/configuration}string" use="required"/&gt;
 * 		&lt;attribute name="value" type="{http://www.w3.org/2001/XMLSchema}anyURI" use="required"/&gt;
 * 		&lt;attribute name="legs" type="{http://www.w3.org/2001/XMLSchema}integer" use="optional" default="1"/&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "mep")

@XmlRootElement(name = "mep")

public class Mep extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public Mep() {
    super();
  }

  public java.lang.String getName() {
    return this.name;
  }

  public void setName(java.lang.String name) {
    this.name = name;
  }

  public java.net.URI getValue() {
    return this.value;
  }

  public void setValue(java.net.URI value) {
    this.value = value;
  }

  public java.math.BigInteger getLegs() {
    return this.legs;
  }

  public void setLegs(java.math.BigInteger legs) {
    this.legs = legs;
  }

  private static final long serialVersionUID = 1L;



  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="name",required=true)
  protected java.lang.String name;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="anyURI")
  @XmlAttribute(name="value",required=true)
  protected java.net.URI value;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="integer")
  @XmlAttribute(name="legs",required=false)
  protected java.math.BigInteger legs = new java.math.BigInteger("1");

}
