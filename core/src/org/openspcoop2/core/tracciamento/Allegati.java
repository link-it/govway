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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Java class for allegati complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="allegati"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="allegato" type="{http://www.openspcoop2.org/core/tracciamento}allegato" minOccurs="1" maxOccurs="unbounded"/&gt;
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
@XmlType(name = "allegati", 
  propOrder = {
  	"allegato"
  }
)

@XmlRootElement(name = "allegati")

public class Allegati extends org.openspcoop2.utils.beans.BaseBeanWithId implements Serializable , Cloneable {
  public Allegati() {
    super();
  }

  public void addAllegato(Allegato allegato) {
    this.allegato.add(allegato);
  }

  public Allegato getAllegato(int index) {
    return this.allegato.get( index );
  }

  public Allegato removeAllegato(int index) {
    return this.allegato.remove( index );
  }

  public List<Allegato> getAllegatoList() {
    return this.allegato;
  }

  public void setAllegatoList(List<Allegato> allegato) {
    this.allegato=allegato;
  }

  public int sizeAllegatoList() {
    return this.allegato.size();
  }

  private static final long serialVersionUID = 1L;



  @XmlElement(name="allegato",required=true,nillable=false)
  private List<Allegato> allegato = new ArrayList<>();

  /**
   * Use method getAllegatoList
   * @return List&lt;Allegato&gt;
  */
  public List<Allegato> getAllegato() {
  	return this.getAllegatoList();
  }

  /**
   * Use method setAllegatoList
   * @param allegato List&lt;Allegato&gt;
  */
  public void setAllegato(List<Allegato> allegato) {
  	this.setAllegatoList(allegato);
  }

  /**
   * Use method sizeAllegatoList
   * @return lunghezza della lista
  */
  public int sizeAllegato() {
  	return this.sizeAllegatoList();
  }

}
