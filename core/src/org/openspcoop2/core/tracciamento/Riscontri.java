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


/** <p>Java class for riscontri complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="riscontri"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="riscontro" type="{http://www.openspcoop2.org/core/tracciamento}riscontro" minOccurs="1" maxOccurs="unbounded"/&gt;
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
@XmlType(name = "riscontri", 
  propOrder = {
  	"riscontro"
  }
)

@XmlRootElement(name = "riscontri")

public class Riscontri extends org.openspcoop2.utils.beans.BaseBeanWithId implements Serializable , Cloneable {
  public Riscontri() {
    super();
  }

  public void addRiscontro(Riscontro riscontro) {
    this.riscontro.add(riscontro);
  }

  public Riscontro getRiscontro(int index) {
    return this.riscontro.get( index );
  }

  public Riscontro removeRiscontro(int index) {
    return this.riscontro.remove( index );
  }

  public List<Riscontro> getRiscontroList() {
    return this.riscontro;
  }

  public void setRiscontroList(List<Riscontro> riscontro) {
    this.riscontro=riscontro;
  }

  public int sizeRiscontroList() {
    return this.riscontro.size();
  }

  private static final long serialVersionUID = 1L;



  @XmlElement(name="riscontro",required=true,nillable=false)
  private List<Riscontro> riscontro = new ArrayList<>();

  /**
   * Use method getRiscontroList
   * @return List&lt;Riscontro&gt;
  */
  public List<Riscontro> getRiscontro() {
  	return this.getRiscontroList();
  }

  /**
   * Use method setRiscontroList
   * @param riscontro List&lt;Riscontro&gt;
  */
  public void setRiscontro(List<Riscontro> riscontro) {
  	this.setRiscontroList(riscontro);
  }

  /**
   * Use method sizeRiscontroList
   * @return lunghezza della lista
  */
  public int sizeRiscontro() {
  	return this.sizeRiscontroList();
  }

}
