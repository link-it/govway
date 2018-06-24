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
package eu.domibus.configuration;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;


/** <p>Java class for process complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="process">
 * 		&lt;sequence>
 * 			&lt;element name="initiatorParties" type="{http://www.domibus.eu/configuration}initiatorParties" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="responderParties" type="{http://www.domibus.eu/configuration}responderParties" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="legs" type="{http://www.domibus.eu/configuration}legs" minOccurs="1" maxOccurs="1"/>
 * 		&lt;/sequence>
 * 		&lt;attribute name="name" type="{http://www.domibus.eu/configuration}string" use="required"/>
 * 		&lt;attribute name="responderRole" type="{http://www.w3.org/2001/XMLSchema}string" use="required"/>
 * 		&lt;attribute name="agreement" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/>
 * 		&lt;attribute name="binding" type="{http://www.w3.org/2001/XMLSchema}string" use="required"/>
 * 		&lt;attribute name="mep" type="{http://www.w3.org/2001/XMLSchema}string" use="required"/>
 * 		&lt;attribute name="initiatorRole" type="{http://www.w3.org/2001/XMLSchema}string" use="required"/>
 * &lt;/complexType>
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "process", 
  propOrder = {
  	"initiatorParties",
  	"responderParties",
  	"legs"
  }
)

@XmlRootElement(name = "process")

public class Process extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public Process() {
  }

  public InitiatorParties getInitiatorParties() {
    return this.initiatorParties;
  }

  public void setInitiatorParties(InitiatorParties initiatorParties) {
    this.initiatorParties = initiatorParties;
  }

  public ResponderParties getResponderParties() {
    return this.responderParties;
  }

  public void setResponderParties(ResponderParties responderParties) {
    this.responderParties = responderParties;
  }

  public Legs getLegs() {
    return this.legs;
  }

  public void setLegs(Legs legs) {
    this.legs = legs;
  }

  public java.lang.String getName() {
    return this.name;
  }

  public void setName(java.lang.String name) {
    this.name = name;
  }

  public java.lang.String getResponderRole() {
    return this.responderRole;
  }

  public void setResponderRole(java.lang.String responderRole) {
    this.responderRole = responderRole;
  }

  public java.lang.String getAgreement() {
    return this.agreement;
  }

  public void setAgreement(java.lang.String agreement) {
    this.agreement = agreement;
  }

  public java.lang.String getBinding() {
    return this.binding;
  }

  public void setBinding(java.lang.String binding) {
    this.binding = binding;
  }

  public java.lang.String getMep() {
    return this.mep;
  }

  public void setMep(java.lang.String mep) {
    this.mep = mep;
  }

  public java.lang.String getInitiatorRole() {
    return this.initiatorRole;
  }

  public void setInitiatorRole(java.lang.String initiatorRole) {
    this.initiatorRole = initiatorRole;
  }

  private static final long serialVersionUID = 1L;



  @XmlElement(name="initiatorParties",required=false,nillable=false)
  protected InitiatorParties initiatorParties;

  @XmlElement(name="responderParties",required=false,nillable=false)
  protected ResponderParties responderParties;

  @XmlElement(name="legs",required=true,nillable=false)
  protected Legs legs;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="name",required=true)
  protected java.lang.String name;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="responderRole",required=true)
  protected java.lang.String responderRole;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="agreement",required=false)
  protected java.lang.String agreement;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="binding",required=true)
  protected java.lang.String binding;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="mep",required=true)
  protected java.lang.String mep;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="initiatorRole",required=true)
  protected java.lang.String initiatorRole;

}
