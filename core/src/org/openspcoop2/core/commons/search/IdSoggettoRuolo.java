/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it).
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

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import java.io.Serializable;


/** <p>Java class for id-soggetto-ruolo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="id-soggetto-ruolo"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="id-ruolo" type="{http://www.openspcoop2.org/core/commons/search}id-ruolo" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="id-soggetto" type="{http://www.openspcoop2.org/core/commons/search}id-soggetto" minOccurs="1" maxOccurs="1"/&gt;
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
@XmlType(name = "id-soggetto-ruolo", 
  propOrder = {
  	"idRuolo",
  	"idSoggetto"
  }
)

@XmlRootElement(name = "id-soggetto-ruolo")

public class IdSoggettoRuolo extends org.openspcoop2.utils.beans.BaseBeanWithId implements Serializable , Cloneable {
  public IdSoggettoRuolo() {
    super();
  }

  public IdRuolo getIdRuolo() {
    return this.idRuolo;
  }

  public void setIdRuolo(IdRuolo idRuolo) {
    this.idRuolo = idRuolo;
  }

  public IdSoggetto getIdSoggetto() {
    return this.idSoggetto;
  }

  public void setIdSoggetto(IdSoggetto idSoggetto) {
    this.idSoggetto = idSoggetto;
  }

  private static final long serialVersionUID = 1L;



  @XmlElement(name="id-ruolo",required=true,nillable=false)
  protected IdRuolo idRuolo;

  @XmlElement(name="id-soggetto",required=true,nillable=false)
  protected IdSoggetto idSoggetto;

}
