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
package org.openspcoop2.core.commons.search;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;


/** <p>Java class for servizio-applicativo-proprieta-protocollo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="servizio-applicativo-proprieta-protocollo"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="value_string" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="value_number" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="value_boolean" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0" maxOccurs="1"/&gt;
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
@XmlType(name = "servizio-applicativo-proprieta-protocollo", 
  propOrder = {
  	"name",
  	"valueString",
  	"valueNumber",
  	"valueBoolean"
  }
)

@XmlRootElement(name = "servizio-applicativo-proprieta-protocollo")

public class ServizioApplicativoProprietaProtocollo extends org.openspcoop2.utils.beans.BaseBeanWithId implements Serializable , Cloneable {
  public ServizioApplicativoProprietaProtocollo() {
    super();
  }

  public java.lang.String getName() {
    return this.name;
  }

  public void setName(java.lang.String name) {
    this.name = name;
  }

  public java.lang.String getValueString() {
    return this.valueString;
  }

  public void setValueString(java.lang.String valueString) {
    this.valueString = valueString;
  }

  public long getValueNumber() {
    return this.valueNumber;
  }

  public void setValueNumber(long valueNumber) {
    this.valueNumber = valueNumber;
  }

  public int getValueBoolean() {
    return this.valueBoolean;
  }

  public void setValueBoolean(int valueBoolean) {
    this.valueBoolean = valueBoolean;
  }

  private static final long serialVersionUID = 1L;



  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="name",required=true,nillable=false)
  protected java.lang.String name;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="value_string",required=false,nillable=false)
  protected java.lang.String valueString;

  @javax.xml.bind.annotation.XmlSchemaType(name="long")
  @XmlElement(name="value_number",required=false,nillable=false)
  protected long valueNumber;

  @javax.xml.bind.annotation.XmlSchemaType(name="int")
  @XmlElement(name="value_boolean",required=false,nillable=false)
  protected int valueBoolean;

}
