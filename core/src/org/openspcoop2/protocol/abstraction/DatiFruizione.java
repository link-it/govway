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
package org.openspcoop2.protocol.abstraction;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import org.openspcoop2.protocol.abstraction.constants.Stato;
import java.io.Serializable;


/** <p>Java class for DatiFruizione complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DatiFruizione"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="endpoint" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="client-auth" type="{http://www.openspcoop2.org/protocol/abstraction}Stato" minOccurs="0" maxOccurs="1"/&gt;
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
@XmlType(name = "DatiFruizione", 
  propOrder = {
  	"endpoint",
  	"clientAuth"
  }
)

@XmlRootElement(name = "DatiFruizione")

public class DatiFruizione extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public DatiFruizione() {
    super();
  }

  public java.lang.String getEndpoint() {
    return this.endpoint;
  }

  public void setEndpoint(java.lang.String endpoint) {
    this.endpoint = endpoint;
  }

  public void setClientAuthRawEnumValue(String value) {
    this.clientAuth = (Stato) Stato.toEnumConstantFromString(value);
  }

  public String getClientAuthRawEnumValue() {
    if(this.clientAuth == null){
    	return null;
    }else{
    	return this.clientAuth.toString();
    }
  }

  public org.openspcoop2.protocol.abstraction.constants.Stato getClientAuth() {
    return this.clientAuth;
  }

  public void setClientAuth(org.openspcoop2.protocol.abstraction.constants.Stato clientAuth) {
    this.clientAuth = clientAuth;
  }

  private static final long serialVersionUID = 1L;



  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="endpoint",required=false,nillable=false)
  protected java.lang.String endpoint;

  @jakarta.xml.bind.annotation.XmlTransient
  protected java.lang.String clientAuthRawEnumValue;

  @XmlElement(name="client-auth",required=false,nillable=false)
  protected Stato clientAuth;

}
