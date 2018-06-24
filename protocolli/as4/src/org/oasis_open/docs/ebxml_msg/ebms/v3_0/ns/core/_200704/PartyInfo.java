/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
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
package org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;


/** <p>Java class for PartyInfo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PartyInfo">
 * 		&lt;sequence>
 * 			&lt;element name="From" type="{http://docs.oasis-open.org/ebxml-msg/ebms/v3.0/ns/core/200704/}From" minOccurs="1" maxOccurs="1"/>
 * 			&lt;element name="To" type="{http://docs.oasis-open.org/ebxml-msg/ebms/v3.0/ns/core/200704/}To" minOccurs="1" maxOccurs="1"/>
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
@XmlType(name = "PartyInfo", 
  propOrder = {
  	"from",
  	"to"
  }
)

@XmlRootElement(name = "PartyInfo")

public class PartyInfo extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public PartyInfo() {
  }

  public From getFrom() {
    return this.from;
  }

  public void setFrom(From from) {
    this.from = from;
  }

  public To getTo() {
    return this.to;
  }

  public void setTo(To to) {
    this.to = to;
  }

  private static final long serialVersionUID = 1L;



  @XmlElement(name="From",required=true,nillable=false)
  protected From from;

  @XmlElement(name="To",required=true,nillable=false)
  protected To to;

}
