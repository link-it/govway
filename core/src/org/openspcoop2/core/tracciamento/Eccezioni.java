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


/** <p>Java class for eccezioni complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="eccezioni">
 * 		&lt;sequence>
 * 			&lt;element name="eccezione" type="{http://www.openspcoop2.org/core/tracciamento}eccezione" minOccurs="1" maxOccurs="unbounded"/>
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
@XmlType(name = "eccezioni", 
  propOrder = {
  	"eccezione"
  }
)

@XmlRootElement(name = "eccezioni")

public class Eccezioni extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public Eccezioni() {
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

  @XmlTransient
  private Long id;



  @XmlElement(name="eccezione",required=true,nillable=false)
  protected List<Eccezione> eccezione = new ArrayList<Eccezione>();

  /**
   * @deprecated Use method getEccezioneList
   * @return List<Eccezione>
  */
  @Deprecated
  public List<Eccezione> getEccezione() {
  	return this.eccezione;
  }

  /**
   * @deprecated Use method setEccezioneList
   * @param eccezione List<Eccezione>
  */
  @Deprecated
  public void setEccezione(List<Eccezione> eccezione) {
  	this.eccezione=eccezione;
  }

  /**
   * @deprecated Use method sizeEccezioneList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizeEccezione() {
  	return this.eccezione.size();
  }

}
