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
  protected List<PartyIdType> partyIdType = new ArrayList<PartyIdType>();

  /**
   * @deprecated Use method getPartyIdTypeList
   * @return List&lt;PartyIdType&gt;
  */
  @Deprecated
  public List<PartyIdType> getPartyIdType() {
  	return this.partyIdType;
  }

  /**
   * @deprecated Use method setPartyIdTypeList
   * @param partyIdType List&lt;PartyIdType&gt;
  */
  @Deprecated
  public void setPartyIdType(List<PartyIdType> partyIdType) {
  	this.partyIdType=partyIdType;
  }

  /**
   * @deprecated Use method sizePartyIdTypeList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizePartyIdType() {
  	return this.partyIdType.size();
  }

}
