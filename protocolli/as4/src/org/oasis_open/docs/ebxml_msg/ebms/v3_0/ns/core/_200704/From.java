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
package org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Java class for From complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="From">
 * 		&lt;sequence>
 * 			&lt;element name="PartyId" type="{http://docs.oasis-open.org/ebxml-msg/ebms/v3.0/ns/core/200704/}PartyId" minOccurs="1" maxOccurs="unbounded"/>
 * 			&lt;element name="Role" type="{http://docs.oasis-open.org/ebxml-msg/ebms/v3.0/ns/core/200704/}string" minOccurs="1" maxOccurs="1"/>
 * 		&lt;/sequence>
 * &lt;/complexType>
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "From", 
  propOrder = {
  	"partyId",
  	"role"
  }
)

@XmlRootElement(name = "From")

public class From extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public From() {
  }

  public void addPartyId(PartyId partyId) {
    this.partyId.add(partyId);
  }

  public PartyId getPartyId(int index) {
    return this.partyId.get( index );
  }

  public PartyId removePartyId(int index) {
    return this.partyId.remove( index );
  }

  public List<PartyId> getPartyIdList() {
    return this.partyId;
  }

  public void setPartyIdList(List<PartyId> partyId) {
    this.partyId=partyId;
  }

  public int sizePartyIdList() {
    return this.partyId.size();
  }

  public java.lang.String getRole() {
    return this.role;
  }

  public void setRole(java.lang.String role) {
    this.role = role;
  }

  private static final long serialVersionUID = 1L;



  @XmlElement(name="PartyId",required=true,nillable=false)
  protected List<PartyId> partyId = new ArrayList<PartyId>();

  /**
   * @deprecated Use method getPartyIdList
   * @return List<PartyId>
  */
  @Deprecated
  public List<PartyId> getPartyId() {
  	return this.partyId;
  }

  /**
   * @deprecated Use method setPartyIdList
   * @param partyId List<PartyId>
  */
  @Deprecated
  public void setPartyId(List<PartyId> partyId) {
  	this.partyId=partyId;
  }

  /**
   * @deprecated Use method sizePartyIdList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizePartyId() {
  	return this.partyId.size();
  }

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="Role",required=true,nillable=false)
  protected java.lang.String role;

}
