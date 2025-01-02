/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it).
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
package org.openspcoop2.protocol.information_missing;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Java class for Requisiti complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Requisiti"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="protocollo" type="{http://www.openspcoop2.org/protocol/information_missing}RequisitoProtocollo" minOccurs="0" maxOccurs="unbounded"/&gt;
 * 			&lt;element name="input" type="{http://www.openspcoop2.org/protocol/information_missing}RequisitoInput" minOccurs="0" maxOccurs="1"/&gt;
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
@XmlType(name = "Requisiti", 
  propOrder = {
  	"protocollo",
  	"input"
  }
)

@XmlRootElement(name = "Requisiti")

public class Requisiti extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public Requisiti() {
    super();
  }

  public void addProtocollo(RequisitoProtocollo protocollo) {
    this.protocollo.add(protocollo);
  }

  public RequisitoProtocollo getProtocollo(int index) {
    return this.protocollo.get( index );
  }

  public RequisitoProtocollo removeProtocollo(int index) {
    return this.protocollo.remove( index );
  }

  public List<RequisitoProtocollo> getProtocolloList() {
    return this.protocollo;
  }

  public void setProtocolloList(List<RequisitoProtocollo> protocollo) {
    this.protocollo=protocollo;
  }

  public int sizeProtocolloList() {
    return this.protocollo.size();
  }

  public RequisitoInput getInput() {
    return this.input;
  }

  public void setInput(RequisitoInput input) {
    this.input = input;
  }

  private static final long serialVersionUID = 1L;



  @XmlElement(name="protocollo",required=true,nillable=false)
  private List<RequisitoProtocollo> protocollo = new ArrayList<>();

  /**
   * Use method getProtocolloList
   * @return List&lt;RequisitoProtocollo&gt;
  */
  public List<RequisitoProtocollo> getProtocollo() {
  	return this.getProtocolloList();
  }

  /**
   * Use method setProtocolloList
   * @param protocollo List&lt;RequisitoProtocollo&gt;
  */
  public void setProtocollo(List<RequisitoProtocollo> protocollo) {
  	this.setProtocolloList(protocollo);
  }

  /**
   * Use method sizeProtocolloList
   * @return lunghezza della lista
  */
  public int sizeProtocollo() {
  	return this.sizeProtocolloList();
  }

  @XmlElement(name="input",required=false,nillable=false)
  protected RequisitoInput input;

}
