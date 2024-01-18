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
import java.io.Serializable;


/** <p>Java class for RiferimentoSoggetto complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="RiferimentoSoggetto"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="id-soggetto" type="{http://www.openspcoop2.org/protocol/abstraction}Soggetto" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="not-exists-behaviour" type="{http://www.openspcoop2.org/protocol/abstraction}SoggettoNotExistsBehaviour" minOccurs="0" maxOccurs="1"/&gt;
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
@XmlType(name = "RiferimentoSoggetto", 
  propOrder = {
  	"idSoggetto",
  	"notExistsBehaviour"
  }
)

@XmlRootElement(name = "RiferimentoSoggetto")

public class RiferimentoSoggetto extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public RiferimentoSoggetto() {
    super();
  }

  public Soggetto getIdSoggetto() {
    return this.idSoggetto;
  }

  public void setIdSoggetto(Soggetto idSoggetto) {
    this.idSoggetto = idSoggetto;
  }

  public SoggettoNotExistsBehaviour getNotExistsBehaviour() {
    return this.notExistsBehaviour;
  }

  public void setNotExistsBehaviour(SoggettoNotExistsBehaviour notExistsBehaviour) {
    this.notExistsBehaviour = notExistsBehaviour;
  }

  private static final long serialVersionUID = 1L;



  @XmlElement(name="id-soggetto",required=true,nillable=false)
  protected Soggetto idSoggetto;

  @XmlElement(name="not-exists-behaviour",required=false,nillable=false)
  protected SoggettoNotExistsBehaviour notExistsBehaviour;

}
