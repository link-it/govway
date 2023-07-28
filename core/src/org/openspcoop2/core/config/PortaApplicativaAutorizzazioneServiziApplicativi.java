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

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Java class for porta-applicativa-autorizzazione-servizi-applicativi complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="porta-applicativa-autorizzazione-servizi-applicativi"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="servizio-applicativo" type="{http://www.openspcoop2.org/core/config}porta-applicativa-autorizzazione-servizio-applicativo" minOccurs="1" maxOccurs="unbounded"/&gt;
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
@XmlType(name = "porta-applicativa-autorizzazione-servizi-applicativi", 
  propOrder = {
  	"servizioApplicativo"
  }
)

@XmlRootElement(name = "porta-applicativa-autorizzazione-servizi-applicativi")

public class PortaApplicativaAutorizzazioneServiziApplicativi extends org.openspcoop2.utils.beans.BaseBeanWithId implements Serializable , Cloneable {
  public PortaApplicativaAutorizzazioneServiziApplicativi() {
    super();
  }

  public void addServizioApplicativo(PortaApplicativaAutorizzazioneServizioApplicativo servizioApplicativo) {
    this.servizioApplicativo.add(servizioApplicativo);
  }

  public PortaApplicativaAutorizzazioneServizioApplicativo getServizioApplicativo(int index) {
    return this.servizioApplicativo.get( index );
  }

  public PortaApplicativaAutorizzazioneServizioApplicativo removeServizioApplicativo(int index) {
    return this.servizioApplicativo.remove( index );
  }

  public List<PortaApplicativaAutorizzazioneServizioApplicativo> getServizioApplicativoList() {
    return this.servizioApplicativo;
  }

  public void setServizioApplicativoList(List<PortaApplicativaAutorizzazioneServizioApplicativo> servizioApplicativo) {
    this.servizioApplicativo=servizioApplicativo;
  }

  public int sizeServizioApplicativoList() {
    return this.servizioApplicativo.size();
  }

  private static final long serialVersionUID = 1L;



  @XmlElement(name="servizio-applicativo",required=true,nillable=false)
  private List<PortaApplicativaAutorizzazioneServizioApplicativo> servizioApplicativo = new ArrayList<>();

  /**
   * Use method getServizioApplicativoList
   * @return List&lt;PortaApplicativaAutorizzazioneServizioApplicativo&gt;
  */
  public List<PortaApplicativaAutorizzazioneServizioApplicativo> getServizioApplicativo() {
  	return this.getServizioApplicativoList();
  }

  /**
   * Use method setServizioApplicativoList
   * @param servizioApplicativo List&lt;PortaApplicativaAutorizzazioneServizioApplicativo&gt;
  */
  public void setServizioApplicativo(List<PortaApplicativaAutorizzazioneServizioApplicativo> servizioApplicativo) {
  	this.setServizioApplicativoList(servizioApplicativo);
  }

  /**
   * Use method sizeServizioApplicativoList
   * @return lunghezza della lista
  */
  public int sizeServizioApplicativo() {
  	return this.sizeServizioApplicativoList();
  }

}
