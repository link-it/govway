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
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import java.io.Serializable;


/** <p>Java class for RiferimentoAccordoServizioParteComune complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="RiferimentoAccordoServizioParteComune"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="uri" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="id-accordo" type="{http://www.openspcoop2.org/protocol/abstraction}IdentificatoreAccordo" minOccurs="0" maxOccurs="1"/&gt;
 * 		&lt;/sequence&gt;
 * 		&lt;attribute name="servizio" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RiferimentoAccordoServizioParteComune", 
  propOrder = {
  	"uri",
  	"idAccordo"
  }
)

@XmlRootElement(name = "RiferimentoAccordoServizioParteComune")

public class RiferimentoAccordoServizioParteComune extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public RiferimentoAccordoServizioParteComune() {
    super();
  }

  public java.lang.String getUri() {
    return this.uri;
  }

  public void setUri(java.lang.String uri) {
    this.uri = uri;
  }

  public IdentificatoreAccordo getIdAccordo() {
    return this.idAccordo;
  }

  public void setIdAccordo(IdentificatoreAccordo idAccordo) {
    this.idAccordo = idAccordo;
  }

  public java.lang.String getServizio() {
    return this.servizio;
  }

  public void setServizio(java.lang.String servizio) {
    this.servizio = servizio;
  }

  private static final long serialVersionUID = 1L;



  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="uri",required=false,nillable=false)
  protected java.lang.String uri;

  @XmlElement(name="id-accordo",required=false,nillable=false)
  protected IdentificatoreAccordo idAccordo;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="servizio",required=false)
  protected java.lang.String servizio;

}
