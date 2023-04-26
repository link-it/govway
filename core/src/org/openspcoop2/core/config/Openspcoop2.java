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
package org.openspcoop2.core.config;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Java class for openspcoop2 complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="openspcoop2"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="soggetto" type="{http://www.openspcoop2.org/core/config}soggetto" minOccurs="1" maxOccurs="unbounded"/&gt;
 * 			&lt;element name="configurazione" type="{http://www.openspcoop2.org/core/config}configurazione" minOccurs="1" maxOccurs="1"/&gt;
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
@XmlType(name = "openspcoop2", 
  propOrder = {
  	"soggetto",
  	"configurazione"
  }
)

@XmlRootElement(name = "openspcoop2")

public class Openspcoop2 extends org.openspcoop2.utils.beans.BaseBeanWithId implements Serializable , Cloneable {
  public Openspcoop2() {
    super();
  }

  public void addSoggetto(Soggetto soggetto) {
    this.soggetto.add(soggetto);
  }

  public Soggetto getSoggetto(int index) {
    return this.soggetto.get( index );
  }

  public Soggetto removeSoggetto(int index) {
    return this.soggetto.remove( index );
  }

  public List<Soggetto> getSoggettoList() {
    return this.soggetto;
  }

  public void setSoggettoList(List<Soggetto> soggetto) {
    this.soggetto=soggetto;
  }

  public int sizeSoggettoList() {
    return this.soggetto.size();
  }

  public Configurazione getConfigurazione() {
    return this.configurazione;
  }

  public void setConfigurazione(Configurazione configurazione) {
    this.configurazione = configurazione;
  }

  private static final long serialVersionUID = 1L;



  @XmlElement(name="soggetto",required=true,nillable=false)
  private List<Soggetto> soggetto = new ArrayList<>();

  @XmlElement(name="configurazione",required=true,nillable=false)
  protected Configurazione configurazione;

}
