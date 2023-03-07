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
package org.openspcoop2.protocol.abstraction;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;


/** <p>Java class for DatiApplicativiErogazione complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DatiApplicativiErogazione"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="endpoint" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="credenziali-basic" type="{http://www.openspcoop2.org/protocol/abstraction}CredenzialiInvocazioneBasic" minOccurs="0" maxOccurs="1"/&gt;
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
@XmlType(name = "DatiApplicativiErogazione", 
  propOrder = {
  	"endpoint",
  	"credenzialiBasic"
  }
)

@XmlRootElement(name = "DatiApplicativiErogazione")

public class DatiApplicativiErogazione extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public DatiApplicativiErogazione() {
    super();
  }

  public java.lang.String getEndpoint() {
    return this.endpoint;
  }

  public void setEndpoint(java.lang.String endpoint) {
    this.endpoint = endpoint;
  }

  public CredenzialiInvocazioneBasic getCredenzialiBasic() {
    return this.credenzialiBasic;
  }

  public void setCredenzialiBasic(CredenzialiInvocazioneBasic credenzialiBasic) {
    this.credenzialiBasic = credenzialiBasic;
  }

  private static final long serialVersionUID = 1L;



  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="endpoint",required=true,nillable=false)
  protected java.lang.String endpoint;

  @XmlElement(name="credenziali-basic",required=false,nillable=false)
  protected CredenzialiInvocazioneBasic credenzialiBasic;

}
