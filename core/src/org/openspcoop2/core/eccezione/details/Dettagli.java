/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2017 Link.it srl (http://link.it).
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
package org.openspcoop2.core.eccezione.details;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Java class for dettagli complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="dettagli">
 * 		&lt;sequence>
 * 			&lt;element name="dettaglio" type="{http://www.openspcoop2.org/core/eccezione/details}dettaglio" minOccurs="0" maxOccurs="unbounded"/>
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
@XmlType(name = "dettagli", 
  propOrder = {
  	"dettaglio"
  }
)

@XmlRootElement(name = "dettagli")

public class Dettagli extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public Dettagli() {
  }

  public void addDettaglio(Dettaglio dettaglio) {
    this.dettaglio.add(dettaglio);
  }

  public Dettaglio getDettaglio(int index) {
    return this.dettaglio.get( index );
  }

  public Dettaglio removeDettaglio(int index) {
    return this.dettaglio.remove( index );
  }

  public List<Dettaglio> getDettaglioList() {
    return this.dettaglio;
  }

  public void setDettaglioList(List<Dettaglio> dettaglio) {
    this.dettaglio=dettaglio;
  }

  public int sizeDettaglioList() {
    return this.dettaglio.size();
  }

  private static final long serialVersionUID = 1L;



  @XmlElement(name="dettaglio",required=true,nillable=false)
  protected List<Dettaglio> dettaglio = new ArrayList<Dettaglio>();

  /**
   * @deprecated Use method getDettaglioList
   * @return List<Dettaglio>
  */
  @Deprecated
  public List<Dettaglio> getDettaglio() {
  	return this.dettaglio;
  }

  /**
   * @deprecated Use method setDettaglioList
   * @param dettaglio List<Dettaglio>
  */
  @Deprecated
  public void setDettaglio(List<Dettaglio> dettaglio) {
  	this.dettaglio=dettaglio;
  }

  /**
   * @deprecated Use method sizeDettaglioList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizeDettaglio() {
  	return this.dettaglio.size();
  }

}
