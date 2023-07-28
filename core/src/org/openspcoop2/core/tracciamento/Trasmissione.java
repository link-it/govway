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


/** <p>Java class for trasmissione complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="trasmissione"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="origine" type="{http://www.openspcoop2.org/core/tracciamento}soggetto" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="destinazione" type="{http://www.openspcoop2.org/core/tracciamento}soggetto" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="ora-registrazione" type="{http://www.openspcoop2.org/core/tracciamento}data" minOccurs="0" maxOccurs="1"/&gt;
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
@XmlType(name = "trasmissione", 
  propOrder = {
  	"origine",
  	"destinazione",
  	"oraRegistrazione"
  }
)

@XmlRootElement(name = "trasmissione")

public class Trasmissione extends org.openspcoop2.utils.beans.BaseBeanWithId implements Serializable , Cloneable {
  public Trasmissione() {
    super();
  }

  public Soggetto getOrigine() {
    return this.origine;
  }

  public void setOrigine(Soggetto origine) {
    this.origine = origine;
  }

  public Soggetto getDestinazione() {
    return this.destinazione;
  }

  public void setDestinazione(Soggetto destinazione) {
    this.destinazione = destinazione;
  }

  public Data getOraRegistrazione() {
    return this.oraRegistrazione;
  }

  public void setOraRegistrazione(Data oraRegistrazione) {
    this.oraRegistrazione = oraRegistrazione;
  }

  private static final long serialVersionUID = 1L;



  @XmlElement(name="origine",required=false,nillable=false)
  protected Soggetto origine;

  @XmlElement(name="destinazione",required=false,nillable=false)
  protected Soggetto destinazione;

  @XmlElement(name="ora-registrazione",required=false,nillable=false)
  protected Data oraRegistrazione;

}
