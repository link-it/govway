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


/** <p>Java class for riscontri complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="riscontri">
 * 		&lt;sequence>
 * 			&lt;element name="riscontro" type="{http://www.openspcoop2.org/core/tracciamento}riscontro" minOccurs="1" maxOccurs="unbounded"/>
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
@XmlType(name = "riscontri", 
  propOrder = {
  	"riscontro"
  }
)

@XmlRootElement(name = "riscontri")

public class Riscontri extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public Riscontri() {
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

  @XmlTransient
  private Long id;



  @XmlElement(name="riscontro",required=true,nillable=false)
  protected List<Riscontro> riscontro = new ArrayList<Riscontro>();

  /**
   * @deprecated Use method getRiscontroList
   * @return List<Riscontro>
  */
  @Deprecated
  public List<Riscontro> getRiscontro() {
  	return this.riscontro;
  }

  /**
   * @deprecated Use method setRiscontroList
   * @param riscontro List<Riscontro>
  */
  @Deprecated
  public void setRiscontro(List<Riscontro> riscontro) {
  	this.riscontro=riscontro;
  }

  /**
   * @deprecated Use method sizeRiscontroList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizeRiscontro() {
  	return this.riscontro.size();
  }

}
