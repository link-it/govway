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


/** <p>Java class for allegati complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="allegati">
 * 		&lt;sequence>
 * 			&lt;element name="allegato" type="{http://www.openspcoop2.org/core/tracciamento}allegato" minOccurs="1" maxOccurs="unbounded"/>
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
@XmlType(name = "allegati", 
  propOrder = {
  	"allegato"
  }
)

@XmlRootElement(name = "allegati")

public class Allegati extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public Allegati() {
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

  @XmlTransient
  private Long id;



  @XmlElement(name="allegato",required=true,nillable=false)
  protected List<Allegato> allegato = new ArrayList<Allegato>();

  /**
   * @deprecated Use method getAllegatoList
   * @return List<Allegato>
  */
  @Deprecated
  public List<Allegato> getAllegato() {
  	return this.allegato;
  }

  /**
   * @deprecated Use method setAllegatoList
   * @param allegato List<Allegato>
  */
  @Deprecated
  public void setAllegato(List<Allegato> allegato) {
  	this.allegato=allegato;
  }

  /**
   * @deprecated Use method sizeAllegatoList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizeAllegato() {
  	return this.allegato.size();
  }

}
