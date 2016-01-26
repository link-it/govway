/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2016 Link.it srl (http://link.it).
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Java class for Requisiti complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Requisiti">
 * 		&lt;sequence>
 * 			&lt;element name="protocollo" type="{http://www.openspcoop2.org/protocol/information_missing}RequisitoProtocollo" minOccurs="0" maxOccurs="unbounded"/>
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
@XmlType(name = "Requisiti", 
  propOrder = {
  	"protocollo"
  }
)

@XmlRootElement(name = "Requisiti")

public class Requisiti extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public Requisiti() {
  }

  public Long getId() {
    if(this.id!=null)
		return this.id;
	else
		return new Long(-1);
  }

  public void setId(Long id) {
    if(id!=null)
		this.id=id;
	else
		this.id=new Long(-1);
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

  private static final long serialVersionUID = 1L;

  @XmlTransient
  private Long id;



  @XmlElement(name="protocollo",required=true,nillable=false)
  protected List<RequisitoProtocollo> protocollo = new ArrayList<RequisitoProtocollo>();

  /**
   * @deprecated Use method getProtocolloList
   * @return List<RequisitoProtocollo>
  */
  @Deprecated
  public List<RequisitoProtocollo> getProtocollo() {
  	return this.protocollo;
  }

  /**
   * @deprecated Use method setProtocolloList
   * @param protocollo List<RequisitoProtocollo>
  */
  @Deprecated
  public void setProtocollo(List<RequisitoProtocollo> protocollo) {
  	this.protocollo=protocollo;
  }

  /**
   * @deprecated Use method sizeProtocolloList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizeProtocollo() {
  	return this.protocollo.size();
  }

}
