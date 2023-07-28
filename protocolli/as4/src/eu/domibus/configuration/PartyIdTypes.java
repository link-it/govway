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
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Java class for partyIdTypes complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="partyIdTypes"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="partyIdType" type="{http://www.domibus.eu/configuration}partyIdType" minOccurs="1" maxOccurs="unbounded"/&gt;
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
@XmlType(name = "partyIdTypes", 
  propOrder = {
  	"partyIdType"
  }
)

@XmlRootElement(name = "partyIdTypes")

public class PartyIdTypes extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public PartyIdTypes() {
    super();
  }

  public void addPartyIdType(PartyIdType partyIdType) {
    this.partyIdType.add(partyIdType);
  }

  public PartyIdType getPartyIdType(int index) {
    return this.partyIdType.get( index );
  }

  public PartyIdType removePartyIdType(int index) {
    return this.partyIdType.remove( index );
  }

  public List<PartyIdType> getPartyIdTypeList() {
    return this.partyIdType;
  }

  public void setPartyIdTypeList(List<PartyIdType> partyIdType) {
    this.partyIdType=partyIdType;
  }

  public int sizePartyIdTypeList() {
    return this.partyIdType.size();
  }

  private static final long serialVersionUID = 1L;



  @XmlElement(name="partyIdType",required=true,nillable=false)
  private List<PartyIdType> partyIdType = new ArrayList<>();

  /**
   * Use method getPartyIdTypeList
   * @return List&lt;PartyIdType&gt;
  */
  public List<PartyIdType> getPartyIdType() {
  	return this.getPartyIdTypeList();
  }

  /**
   * Use method setPartyIdTypeList
   * @param partyIdType List&lt;PartyIdType&gt;
  */
  public void setPartyIdType(List<PartyIdType> partyIdType) {
  	this.setPartyIdTypeList(partyIdType);
  }

  /**
   * Use method sizePartyIdTypeList
   * @return lunghezza della lista
  */
  public int sizePartyIdType() {
  	return this.sizePartyIdTypeList();
  }

}
