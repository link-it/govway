/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it).
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


/** <p>Java class for initiatorParties complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="initiatorParties"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="initiatorParty" type="{http://www.domibus.eu/configuration}initiatorParty" minOccurs="1" maxOccurs="unbounded"/&gt;
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
@XmlType(name = "initiatorParties", 
  propOrder = {
  	"initiatorParty"
  }
)

@XmlRootElement(name = "initiatorParties")

public class InitiatorParties extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public InitiatorParties() {
  }

  public void addInitiatorParty(InitiatorParty initiatorParty) {
    this.initiatorParty.add(initiatorParty);
  }

  public InitiatorParty getInitiatorParty(int index) {
    return this.initiatorParty.get( index );
  }

  public InitiatorParty removeInitiatorParty(int index) {
    return this.initiatorParty.remove( index );
  }

  public List<InitiatorParty> getInitiatorPartyList() {
    return this.initiatorParty;
  }

  public void setInitiatorPartyList(List<InitiatorParty> initiatorParty) {
    this.initiatorParty=initiatorParty;
  }

  public int sizeInitiatorPartyList() {
    return this.initiatorParty.size();
  }

  private static final long serialVersionUID = 1L;



  @XmlElement(name="initiatorParty",required=true,nillable=false)
  protected List<InitiatorParty> initiatorParty = new ArrayList<InitiatorParty>();

  /**
   * @deprecated Use method getInitiatorPartyList
   * @return List&lt;InitiatorParty&gt;
  */
  @Deprecated
  public List<InitiatorParty> getInitiatorParty() {
  	return this.initiatorParty;
  }

  /**
   * @deprecated Use method setInitiatorPartyList
   * @param initiatorParty List&lt;InitiatorParty&gt;
  */
  @Deprecated
  public void setInitiatorParty(List<InitiatorParty> initiatorParty) {
  	this.initiatorParty=initiatorParty;
  }

  /**
   * @deprecated Use method sizeInitiatorPartyList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizeInitiatorParty() {
  	return this.initiatorParty.size();
  }

}
