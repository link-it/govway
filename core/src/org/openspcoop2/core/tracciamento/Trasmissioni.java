/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2016 Link.it srl (http://link.it).
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Java class for trasmissioni complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="trasmissioni">
 * 		&lt;sequence>
 * 			&lt;element name="trasmissione" type="{http://www.openspcoop2.org/core/tracciamento}trasmissione" minOccurs="1" maxOccurs="unbounded"/>
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
@XmlType(name = "trasmissioni", 
  propOrder = {
  	"trasmissione"
  }
)

@XmlRootElement(name = "trasmissioni")

public class Trasmissioni extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public Trasmissioni() {
  }

  public Long getId() {
    if(this.id!=null)
		return this.id;
	else
		return new Long(-1);
  }

  public void setId(Long id) {
    if(id!=null)
		this.id=id;
	else
		this.id=new Long(-1);
  }

  public void addTrasmissione(Trasmissione trasmissione) {
    this.trasmissione.add(trasmissione);
  }

  public Trasmissione getTrasmissione(int index) {
    return this.trasmissione.get( index );
  }

  public Trasmissione removeTrasmissione(int index) {
    return this.trasmissione.remove( index );
  }

  public List<Trasmissione> getTrasmissioneList() {
    return this.trasmissione;
  }

  public void setTrasmissioneList(List<Trasmissione> trasmissione) {
    this.trasmissione=trasmissione;
  }

  public int sizeTrasmissioneList() {
    return this.trasmissione.size();
  }

  private static final long serialVersionUID = 1L;

  @XmlTransient
  private Long id;



  @XmlElement(name="trasmissione",required=true,nillable=false)
  protected List<Trasmissione> trasmissione = new ArrayList<Trasmissione>();

  /**
   * @deprecated Use method getTrasmissioneList
   * @return List<Trasmissione>
  */
  @Deprecated
  public List<Trasmissione> getTrasmissione() {
  	return this.trasmissione;
  }

  /**
   * @deprecated Use method setTrasmissioneList
   * @param trasmissione List<Trasmissione>
  */
  @Deprecated
  public void setTrasmissione(List<Trasmissione> trasmissione) {
  	this.trasmissione=trasmissione;
  }

  /**
   * @deprecated Use method sizeTrasmissioneList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizeTrasmissione() {
  	return this.trasmissione.size();
  }

}
