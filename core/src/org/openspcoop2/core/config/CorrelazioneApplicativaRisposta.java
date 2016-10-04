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
package org.openspcoop2.core.config;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Java class for correlazione-applicativa-risposta complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="correlazione-applicativa-risposta">
 * 		&lt;sequence>
 * 			&lt;element name="elemento" type="{http://www.openspcoop2.org/core/config}correlazione-applicativa-risposta-elemento" minOccurs="1" maxOccurs="unbounded"/>
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
@XmlType(name = "correlazione-applicativa-risposta", 
  propOrder = {
  	"elemento"
  }
)

@XmlRootElement(name = "correlazione-applicativa-risposta")

public class CorrelazioneApplicativaRisposta extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public CorrelazioneApplicativaRisposta() {
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

  public void addElemento(CorrelazioneApplicativaRispostaElemento elemento) {
    this.elemento.add(elemento);
  }

  public CorrelazioneApplicativaRispostaElemento getElemento(int index) {
    return this.elemento.get( index );
  }

  public CorrelazioneApplicativaRispostaElemento removeElemento(int index) {
    return this.elemento.remove( index );
  }

  public List<CorrelazioneApplicativaRispostaElemento> getElementoList() {
    return this.elemento;
  }

  public void setElementoList(List<CorrelazioneApplicativaRispostaElemento> elemento) {
    this.elemento=elemento;
  }

  public int sizeElementoList() {
    return this.elemento.size();
  }

  private static final long serialVersionUID = 1L;

  @XmlTransient
  private Long id;



  @XmlElement(name="elemento",required=true,nillable=false)
  protected List<CorrelazioneApplicativaRispostaElemento> elemento = new ArrayList<CorrelazioneApplicativaRispostaElemento>();

  /**
   * @deprecated Use method getElementoList
   * @return List<CorrelazioneApplicativaRispostaElemento>
  */
  @Deprecated
  public List<CorrelazioneApplicativaRispostaElemento> getElemento() {
  	return this.elemento;
  }

  /**
   * @deprecated Use method setElementoList
   * @param elemento List<CorrelazioneApplicativaRispostaElemento>
  */
  @Deprecated
  public void setElemento(List<CorrelazioneApplicativaRispostaElemento> elemento) {
  	this.elemento=elemento;
  }

  /**
   * @deprecated Use method sizeElementoList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizeElemento() {
  	return this.elemento.size();
  }

}
