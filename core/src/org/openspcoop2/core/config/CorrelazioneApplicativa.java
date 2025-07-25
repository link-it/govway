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
package org.openspcoop2.core.config;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Java class for correlazione-applicativa complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="correlazione-applicativa"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="elemento" type="{http://www.openspcoop2.org/core/config}correlazione-applicativa-elemento" minOccurs="1" maxOccurs="unbounded"/&gt;
 * 		&lt;/sequence&gt;
 * 		&lt;attribute name="scadenza" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "correlazione-applicativa", 
  propOrder = {
  	"elemento"
  }
)

@XmlRootElement(name = "correlazione-applicativa")

public class CorrelazioneApplicativa extends org.openspcoop2.utils.beans.BaseBeanWithId implements Serializable , Cloneable {
  public CorrelazioneApplicativa() {
    super();
  }

  public void addElemento(CorrelazioneApplicativaElemento elemento) {
    this.elemento.add(elemento);
  }

  public CorrelazioneApplicativaElemento getElemento(int index) {
    return this.elemento.get( index );
  }

  public CorrelazioneApplicativaElemento removeElemento(int index) {
    return this.elemento.remove( index );
  }

  public List<CorrelazioneApplicativaElemento> getElementoList() {
    return this.elemento;
  }

  public void setElementoList(List<CorrelazioneApplicativaElemento> elemento) {
    this.elemento=elemento;
  }

  public int sizeElementoList() {
    return this.elemento.size();
  }

  public java.lang.String getScadenza() {
    return this.scadenza;
  }

  public void setScadenza(java.lang.String scadenza) {
    this.scadenza = scadenza;
  }

  private static final long serialVersionUID = 1L;



  @XmlElement(name="elemento",required=true,nillable=false)
  private List<CorrelazioneApplicativaElemento> elemento = new ArrayList<>();

  /**
   * Use method getElementoList
   * @return List&lt;CorrelazioneApplicativaElemento&gt;
  */
  public List<CorrelazioneApplicativaElemento> getElemento() {
  	return this.getElementoList();
  }

  /**
   * Use method setElementoList
   * @param elemento List&lt;CorrelazioneApplicativaElemento&gt;
  */
  public void setElemento(List<CorrelazioneApplicativaElemento> elemento) {
  	this.setElementoList(elemento);
  }

  /**
   * Use method sizeElementoList
   * @return lunghezza della lista
  */
  public int sizeElemento() {
  	return this.sizeElementoList();
  }

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="scadenza",required=false)
  protected java.lang.String scadenza;

}
