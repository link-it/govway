/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Java class for parties complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="parties">
 * 		&lt;sequence>
 * 			&lt;element name="partyIdTypes" type="{http://www.domibus.eu/configuration}partyIdTypes" minOccurs="1" maxOccurs="1"/>
 * 			&lt;element name="party" type="{http://www.domibus.eu/configuration}party" minOccurs="1" maxOccurs="unbounded"/>
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
@XmlType(name = "parties", 
  propOrder = {
  	"partyIdTypes",
  	"party"
  }
)

@XmlRootElement(name = "parties")

public class Parties extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public Parties() {
  }

  public PartyIdTypes getPartyIdTypes() {
    return this.partyIdTypes;
  }

  public void setPartyIdTypes(PartyIdTypes partyIdTypes) {
    this.partyIdTypes = partyIdTypes;
  }

  public void addParty(Party party) {
    this.party.add(party);
  }

  public Party getParty(int index) {
    return this.party.get( index );
  }

  public Party removeParty(int index) {
    return this.party.remove( index );
  }

  public List<Party> getPartyList() {
    return this.party;
  }

  public void setPartyList(List<Party> party) {
    this.party=party;
  }

  public int sizePartyList() {
    return this.party.size();
  }

  private static final long serialVersionUID = 1L;



  @XmlElement(name="partyIdTypes",required=true,nillable=false)
  protected PartyIdTypes partyIdTypes;

  @XmlElement(name="party",required=true,nillable=false)
  protected List<Party> party = new ArrayList<Party>();

  /**
   * @deprecated Use method getPartyList
   * @return List<Party>
  */
  @Deprecated
  public List<Party> getParty() {
  	return this.party;
  }

  /**
   * @deprecated Use method setPartyList
   * @param party List<Party>
  */
  @Deprecated
  public void setParty(List<Party> party) {
  	this.party=party;
  }

  /**
   * @deprecated Use method sizePartyList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizeParty() {
  	return this.party.size();
  }

}
