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
package org.openspcoop2.core.config;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Java class for correlazione-applicativa-risposta complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="correlazione-applicativa-risposta"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="elemento" type="{http://www.openspcoop2.org/core/config}correlazione-applicativa-risposta-elemento" minOccurs="1" maxOccurs="unbounded"/&gt;
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
@XmlType(name = "correlazione-applicativa-risposta", 
  propOrder = {
  	"elemento"
  }
)

@XmlRootElement(name = "correlazione-applicativa-risposta")

public class CorrelazioneApplicativaRisposta extends org.openspcoop2.utils.beans.BaseBeanWithId implements Serializable , Cloneable {
  public CorrelazioneApplicativaRisposta() {
    super();
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



  @XmlElement(name="elemento",required=true,nillable=false)
  private List<CorrelazioneApplicativaRispostaElemento> elemento = new ArrayList<>();

}
