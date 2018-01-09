/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2017 Link.it srl (http://link.it).
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


/** <p>Java class for identifier complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="identifier">
 * 		&lt;attribute name="partyId" type="{http://www.domibus.eu/configuration}string" use="required"/>
 * 		&lt;attribute name="partyIdType" type="{http://www.domibus.eu/configuration}string" use="required"/>
 * &lt;/complexType>
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "identifier")

@XmlRootElement(name = "identifier")

public class Identifier extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public Identifier() {
  }

  public java.lang.String getPartyId() {
    return this.partyId;
  }

  public void setPartyId(java.lang.String partyId) {
    this.partyId = partyId;
  }

  public java.lang.String getPartyIdType() {
    return this.partyIdType;
  }

  public void setPartyIdType(java.lang.String partyIdType) {
    this.partyIdType = partyIdType;
  }

  private static final long serialVersionUID = 1L;



  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="partyId",required=true)
  protected java.lang.String partyId;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="partyIdType",required=true)
  protected java.lang.String partyIdType;

}
