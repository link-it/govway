/*
 * OpenSPCoop - Customizable API Gateway 
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
package org.openspcoop2.core.tracciamento;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;


/** <p>Java class for trasmissione complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="trasmissione">
 * 		&lt;sequence>
 * 			&lt;element name="origine" type="{http://www.openspcoop2.org/core/tracciamento}soggetto" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="destinazione" type="{http://www.openspcoop2.org/core/tracciamento}soggetto" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="ora-registrazione" type="{http://www.openspcoop2.org/core/tracciamento}data" minOccurs="0" maxOccurs="1"/>
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
@XmlType(name = "trasmissione", 
  propOrder = {
  	"origine",
  	"destinazione",
  	"oraRegistrazione"
  }
)

@XmlRootElement(name = "trasmissione")

public class Trasmissione extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public Trasmissione() {
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

  @XmlTransient
  private Long id;



  @XmlElement(name="origine",required=false,nillable=false)
  protected Soggetto origine;

  @XmlElement(name="destinazione",required=false,nillable=false)
  protected Soggetto destinazione;

  @XmlElement(name="ora-registrazione",required=false,nillable=false)
  protected Data oraRegistrazione;

}
