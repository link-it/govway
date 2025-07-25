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
package org.openspcoop2.protocol.abstraction;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Java class for Fruitori complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Fruitori"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="fruitore" type="{http://www.openspcoop2.org/protocol/abstraction}Soggetto" minOccurs="1" maxOccurs="unbounded"/&gt;
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
@XmlType(name = "Fruitori", 
  propOrder = {
  	"fruitore"
  }
)

@XmlRootElement(name = "Fruitori")

public class Fruitori extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public Fruitori() {
    super();
  }

  public void addFruitore(Soggetto fruitore) {
    this.fruitore.add(fruitore);
  }

  public Soggetto getFruitore(int index) {
    return this.fruitore.get( index );
  }

  public Soggetto removeFruitore(int index) {
    return this.fruitore.remove( index );
  }

  public List<Soggetto> getFruitoreList() {
    return this.fruitore;
  }

  public void setFruitoreList(List<Soggetto> fruitore) {
    this.fruitore=fruitore;
  }

  public int sizeFruitoreList() {
    return this.fruitore.size();
  }

  private static final long serialVersionUID = 1L;



  @XmlElement(name="fruitore",required=true,nillable=false)
  private List<Soggetto> fruitore = new ArrayList<>();

  /**
   * Use method getFruitoreList
   * @return List&lt;Soggetto&gt;
  */
  public List<Soggetto> getFruitore() {
  	return this.getFruitoreList();
  }

  /**
   * Use method setFruitoreList
   * @param fruitore List&lt;Soggetto&gt;
  */
  public void setFruitore(List<Soggetto> fruitore) {
  	this.setFruitoreList(fruitore);
  }

  /**
   * Use method sizeFruitoreList
   * @return lunghezza della lista
  */
  public int sizeFruitore() {
  	return this.sizeFruitoreList();
  }

}
