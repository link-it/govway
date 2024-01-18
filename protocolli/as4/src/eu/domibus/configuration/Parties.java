/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it).
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
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Java class for parties complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="parties"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="partyIdTypes" type="{http://www.domibus.eu/configuration}partyIdTypes" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="party" type="{http://www.domibus.eu/configuration}party" minOccurs="1" maxOccurs="unbounded"/&gt;
 * 		&lt;/sequence&gt;
 * &lt;/complexType&gt;
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
    super();
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
  private List<Party> party = new ArrayList<>();

  /**
   * Use method getPartyList
   * @return List&lt;Party&gt;
  */
  public List<Party> getParty() {
  	return this.getPartyList();
  }

  /**
   * Use method setPartyList
   * @param party List&lt;Party&gt;
  */
  public void setParty(List<Party> party) {
  	this.setPartyList(party);
  }

  /**
   * Use method sizePartyList
   * @return lunghezza della lista
  */
  public int sizeParty() {
  	return this.sizePartyList();
  }

}
