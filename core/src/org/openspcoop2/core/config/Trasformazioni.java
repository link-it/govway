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
package org.openspcoop2.core.config;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Java class for trasformazioni complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="trasformazioni"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="regola" type="{http://www.openspcoop2.org/core/config}trasformazione-regola" minOccurs="1" maxOccurs="unbounded"/&gt;
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
@XmlType(name = "trasformazioni", 
  propOrder = {
  	"regola"
  }
)

@XmlRootElement(name = "trasformazioni")

public class Trasformazioni extends org.openspcoop2.utils.beans.BaseBeanWithId implements Serializable , Cloneable {
  public Trasformazioni() {
    super();
  }

  public void addRegola(TrasformazioneRegola regola) {
    this.regola.add(regola);
  }

  public TrasformazioneRegola getRegola(int index) {
    return this.regola.get( index );
  }

  public TrasformazioneRegola removeRegola(int index) {
    return this.regola.remove( index );
  }

  public List<TrasformazioneRegola> getRegolaList() {
    return this.regola;
  }

  public void setRegolaList(List<TrasformazioneRegola> regola) {
    this.regola=regola;
  }

  public int sizeRegolaList() {
    return this.regola.size();
  }

  private static final long serialVersionUID = 1L;



  @XmlElement(name="regola",required=true,nillable=false)
  private List<TrasformazioneRegola> regola = new ArrayList<>();

  /**
   * Use method getRegolaList
   * @return List&lt;TrasformazioneRegola&gt;
  */
  public List<TrasformazioneRegola> getRegola() {
  	return this.getRegolaList();
  }

  /**
   * Use method setRegolaList
   * @param regola List&lt;TrasformazioneRegola&gt;
  */
  public void setRegola(List<TrasformazioneRegola> regola) {
  	this.setRegolaList(regola);
  }

  /**
   * Use method sizeRegolaList
   * @return lunghezza della lista
  */
  public int sizeRegola() {
  	return this.sizeRegolaList();
  }

}
