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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;


/** <p>Java class for RiferimentoAccordoServizioParteSpecifica complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="RiferimentoAccordoServizioParteSpecifica"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="uri" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="id-servizio" type="{http://www.openspcoop2.org/protocol/abstraction}IdentificatoreServizio" minOccurs="0" maxOccurs="1"/&gt;
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
@XmlType(name = "RiferimentoAccordoServizioParteSpecifica", 
  propOrder = {
  	"uri",
  	"idServizio"
  }
)

@XmlRootElement(name = "RiferimentoAccordoServizioParteSpecifica")

public class RiferimentoAccordoServizioParteSpecifica extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public RiferimentoAccordoServizioParteSpecifica() {
    super();
  }

  public java.lang.String getUri() {
    return this.uri;
  }

  public void setUri(java.lang.String uri) {
    this.uri = uri;
  }

  public IdentificatoreServizio getIdServizio() {
    return this.idServizio;
  }

  public void setIdServizio(IdentificatoreServizio idServizio) {
    this.idServizio = idServizio;
  }

  private static final long serialVersionUID = 1L;



  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="uri",required=false,nillable=false)
  protected java.lang.String uri;

  @XmlElement(name="id-servizio",required=false,nillable=false)
  protected IdentificatoreServizio idServizio;

}
