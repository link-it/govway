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
import java.util.ArrayList;
import java.util.List;


/** <p>Java class for eccezioni complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="eccezioni"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="eccezione" type="{http://www.openspcoop2.org/core/tracciamento}eccezione" minOccurs="1" maxOccurs="unbounded"/&gt;
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
@XmlType(name = "eccezioni", 
  propOrder = {
  	"eccezione"
  }
)

@XmlRootElement(name = "eccezioni")

public class Eccezioni extends org.openspcoop2.utils.beans.BaseBeanWithId implements Serializable , Cloneable {
  public Eccezioni() {
    super();
  }

  public void addEccezione(Eccezione eccezione) {
    this.eccezione.add(eccezione);
  }

  public Eccezione getEccezione(int index) {
    return this.eccezione.get( index );
  }

  public Eccezione removeEccezione(int index) {
    return this.eccezione.remove( index );
  }

  public List<Eccezione> getEccezioneList() {
    return this.eccezione;
  }

  public void setEccezioneList(List<Eccezione> eccezione) {
    this.eccezione=eccezione;
  }

  public int sizeEccezioneList() {
    return this.eccezione.size();
  }

  private static final long serialVersionUID = 1L;



  @XmlElement(name="eccezione",required=true,nillable=false)
  private List<Eccezione> eccezione = new ArrayList<>();

  /**
   * Use method getEccezioneList
   * @return List&lt;Eccezione&gt;
  */
  public List<Eccezione> getEccezione() {
  	return this.getEccezioneList();
  }

  /**
   * Use method setEccezioneList
   * @param eccezione List&lt;Eccezione&gt;
  */
  public void setEccezione(List<Eccezione> eccezione) {
  	this.setEccezioneList(eccezione);
  }

  /**
   * Use method sizeEccezioneList
   * @return lunghezza della lista
  */
  public int sizeEccezione() {
  	return this.sizeEccezioneList();
  }

}
