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


/** <p>Java class for responderParties complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="responderParties"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="responderParty" type="{http://www.domibus.eu/configuration}responderParty" minOccurs="1" maxOccurs="unbounded"/&gt;
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
@XmlType(name = "responderParties", 
  propOrder = {
  	"responderParty"
  }
)

@XmlRootElement(name = "responderParties")

public class ResponderParties extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public ResponderParties() {
    super();
  }

  public void addResponderParty(ResponderParty responderParty) {
    this.responderParty.add(responderParty);
  }

  public ResponderParty getResponderParty(int index) {
    return this.responderParty.get( index );
  }

  public ResponderParty removeResponderParty(int index) {
    return this.responderParty.remove( index );
  }

  public List<ResponderParty> getResponderPartyList() {
    return this.responderParty;
  }

  public void setResponderPartyList(List<ResponderParty> responderParty) {
    this.responderParty=responderParty;
  }

  public int sizeResponderPartyList() {
    return this.responderParty.size();
  }

  private static final long serialVersionUID = 1L;



  @XmlElement(name="responderParty",required=true,nillable=false)
  private List<ResponderParty> responderParty = new ArrayList<>();

  /**
   * Use method getResponderPartyList
   * @return List&lt;ResponderParty&gt;
  */
  public List<ResponderParty> getResponderParty() {
  	return this.getResponderPartyList();
  }

  /**
   * Use method setResponderPartyList
   * @param responderParty List&lt;ResponderParty&gt;
  */
  public void setResponderParty(List<ResponderParty> responderParty) {
  	this.setResponderPartyList(responderParty);
  }

  /**
   * Use method sizeResponderPartyList
   * @return lunghezza della lista
  */
  public int sizeResponderParty() {
  	return this.sizeResponderPartyList();
  }

}
