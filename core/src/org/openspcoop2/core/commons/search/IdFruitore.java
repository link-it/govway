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
package org.openspcoop2.core.commons.search;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import java.io.Serializable;


/** <p>Java class for id-fruitore complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="id-fruitore"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="id-fruitore" type="{http://www.openspcoop2.org/core/commons/search}id-soggetto" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="id-accordo-servizio-parte-specifica" type="{http://www.openspcoop2.org/core/commons/search}id-accordo-servizio-parte-specifica" minOccurs="1" maxOccurs="1"/&gt;
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
@XmlType(name = "id-fruitore", 
  propOrder = {
  	"idFruitore",
  	"idAccordoServizioParteSpecifica"
  }
)

@XmlRootElement(name = "id-fruitore")

public class IdFruitore extends org.openspcoop2.utils.beans.BaseBeanWithId implements Serializable , Cloneable {
  public IdFruitore() {
    super();
  }

  public IdSoggetto getIdFruitore() {
    return this.idFruitore;
  }

  public void setIdFruitore(IdSoggetto idFruitore) {
    this.idFruitore = idFruitore;
  }

  public IdAccordoServizioParteSpecifica getIdAccordoServizioParteSpecifica() {
    return this.idAccordoServizioParteSpecifica;
  }

  public void setIdAccordoServizioParteSpecifica(IdAccordoServizioParteSpecifica idAccordoServizioParteSpecifica) {
    this.idAccordoServizioParteSpecifica = idAccordoServizioParteSpecifica;
  }

  private static final long serialVersionUID = 1L;



  @XmlElement(name="id-fruitore",required=true,nillable=false)
  protected IdSoggetto idFruitore;

  @XmlElement(name="id-accordo-servizio-parte-specifica",required=true,nillable=false)
  protected IdAccordoServizioParteSpecifica idAccordoServizioParteSpecifica;

}
