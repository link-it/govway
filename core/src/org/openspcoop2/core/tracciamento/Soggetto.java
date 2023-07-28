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
package org.openspcoop2.core.tracciamento;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import java.io.Serializable;


/** <p>Java class for soggetto complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="soggetto"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="identificativo" type="{http://www.openspcoop2.org/core/tracciamento}soggetto-identificativo" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="identificativo-porta" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="indirizzo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/&gt;
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
@XmlType(name = "soggetto", 
  propOrder = {
  	"identificativo",
  	"identificativoPorta",
  	"indirizzo"
  }
)

@XmlRootElement(name = "soggetto")

public class Soggetto extends org.openspcoop2.utils.beans.BaseBeanWithId implements Serializable , Cloneable {
  public Soggetto() {
    super();
  }

  public SoggettoIdentificativo getIdentificativo() {
    return this.identificativo;
  }

  public void setIdentificativo(SoggettoIdentificativo identificativo) {
    this.identificativo = identificativo;
  }

  public java.lang.String getIdentificativoPorta() {
    return this.identificativoPorta;
  }

  public void setIdentificativoPorta(java.lang.String identificativoPorta) {
    this.identificativoPorta = identificativoPorta;
  }

  public java.lang.String getIndirizzo() {
    return this.indirizzo;
  }

  public void setIndirizzo(java.lang.String indirizzo) {
    this.indirizzo = indirizzo;
  }

  private static final long serialVersionUID = 1L;



  @XmlElement(name="identificativo",required=true,nillable=false)
  protected SoggettoIdentificativo identificativo;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="identificativo-porta",required=false,nillable=false)
  protected java.lang.String identificativoPorta;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="indirizzo",required=false,nillable=false)
  protected java.lang.String indirizzo;

}
