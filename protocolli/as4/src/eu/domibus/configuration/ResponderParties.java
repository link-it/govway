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


/** <p>Java class for responderParties complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="responderParties">
 * 		&lt;sequence>
 * 			&lt;element name="responderParty" type="{http://www.domibus.eu/configuration}responderParty" minOccurs="1" maxOccurs="unbounded"/>
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
@XmlType(name = "responderParties", 
  propOrder = {
  	"responderParty"
  }
)

@XmlRootElement(name = "responderParties")

public class ResponderParties extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public ResponderParties() {
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
  protected List<ResponderParty> responderParty = new ArrayList<ResponderParty>();

  /**
   * @deprecated Use method getResponderPartyList
   * @return List<ResponderParty>
  */
  @Deprecated
  public List<ResponderParty> getResponderParty() {
  	return this.responderParty;
  }

  /**
   * @deprecated Use method setResponderPartyList
   * @param responderParty List<ResponderParty>
  */
  @Deprecated
  public void setResponderParty(List<ResponderParty> responderParty) {
  	this.responderParty=responderParty;
  }

  /**
   * @deprecated Use method sizeResponderPartyList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizeResponderParty() {
  	return this.responderParty.size();
  }

}
