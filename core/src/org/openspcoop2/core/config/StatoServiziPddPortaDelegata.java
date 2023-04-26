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
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Java class for stato-servizi-pdd-porta-delegata complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="stato-servizi-pdd-porta-delegata"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="filtro-abilitazione" type="{http://www.openspcoop2.org/core/config}tipo-filtro-abilitazione-servizi" minOccurs="0" maxOccurs="unbounded"/&gt;
 * 			&lt;element name="filtro-disabilitazione" type="{http://www.openspcoop2.org/core/config}tipo-filtro-abilitazione-servizi" minOccurs="0" maxOccurs="unbounded"/&gt;
 * 		&lt;/sequence&gt;
 * 		&lt;attribute name="stato" type="{http://www.openspcoop2.org/core/config}StatoFunzionalita" use="optional" default="abilitato"/&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "stato-servizi-pdd-porta-delegata", 
  propOrder = {
  	"filtroAbilitazione",
  	"filtroDisabilitazione"
  }
)

@XmlRootElement(name = "stato-servizi-pdd-porta-delegata")

public class StatoServiziPddPortaDelegata extends org.openspcoop2.utils.beans.BaseBeanWithId implements Serializable , Cloneable {
  public StatoServiziPddPortaDelegata() {
    super();
  }

  public void addFiltroAbilitazione(TipoFiltroAbilitazioneServizi filtroAbilitazione) {
    this.filtroAbilitazione.add(filtroAbilitazione);
  }

  public TipoFiltroAbilitazioneServizi getFiltroAbilitazione(int index) {
    return this.filtroAbilitazione.get( index );
  }

  public TipoFiltroAbilitazioneServizi removeFiltroAbilitazione(int index) {
    return this.filtroAbilitazione.remove( index );
  }

  public List<TipoFiltroAbilitazioneServizi> getFiltroAbilitazioneList() {
    return this.filtroAbilitazione;
  }

  public void setFiltroAbilitazioneList(List<TipoFiltroAbilitazioneServizi> filtroAbilitazione) {
    this.filtroAbilitazione=filtroAbilitazione;
  }

  public int sizeFiltroAbilitazioneList() {
    return this.filtroAbilitazione.size();
  }

  public void addFiltroDisabilitazione(TipoFiltroAbilitazioneServizi filtroDisabilitazione) {
    this.filtroDisabilitazione.add(filtroDisabilitazione);
  }

  public TipoFiltroAbilitazioneServizi getFiltroDisabilitazione(int index) {
    return this.filtroDisabilitazione.get( index );
  }

  public TipoFiltroAbilitazioneServizi removeFiltroDisabilitazione(int index) {
    return this.filtroDisabilitazione.remove( index );
  }

  public List<TipoFiltroAbilitazioneServizi> getFiltroDisabilitazioneList() {
    return this.filtroDisabilitazione;
  }

  public void setFiltroDisabilitazioneList(List<TipoFiltroAbilitazioneServizi> filtroDisabilitazione) {
    this.filtroDisabilitazione=filtroDisabilitazione;
  }

  public int sizeFiltroDisabilitazioneList() {
    return this.filtroDisabilitazione.size();
  }

  public void setStatoRawEnumValue(String value) {
    this.stato = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString(value);
  }

  public String getStatoRawEnumValue() {
    if(this.stato == null){
    	return null;
    }else{
    	return this.stato.toString();
    }
  }

  public org.openspcoop2.core.config.constants.StatoFunzionalita getStato() {
    return this.stato;
  }

  public void setStato(org.openspcoop2.core.config.constants.StatoFunzionalita stato) {
    this.stato = stato;
  }

  private static final long serialVersionUID = 1L;



  @XmlElement(name="filtro-abilitazione",required=true,nillable=false)
  private List<TipoFiltroAbilitazioneServizi> filtroAbilitazione = new ArrayList<>();

  /**
   * Use method getFiltroAbilitazioneList
   * @return List&lt;TipoFiltroAbilitazioneServizi&gt;
  */
  public List<TipoFiltroAbilitazioneServizi> getFiltroAbilitazione() {
  	return this.getFiltroAbilitazioneList();
  }

  /**
   * Use method setFiltroAbilitazioneList
   * @param filtroAbilitazione List&lt;TipoFiltroAbilitazioneServizi&gt;
  */
  public void setFiltroAbilitazione(List<TipoFiltroAbilitazioneServizi> filtroAbilitazione) {
  	this.setFiltroAbilitazioneList(filtroAbilitazione);
  }

  /**
   * Use method sizeFiltroAbilitazioneList
   * @return lunghezza della lista
  */
  public int sizeFiltroAbilitazione() {
  	return this.sizeFiltroAbilitazioneList();
  }

  @XmlElement(name="filtro-disabilitazione",required=true,nillable=false)
  private List<TipoFiltroAbilitazioneServizi> filtroDisabilitazione = new ArrayList<>();

  /**
   * Use method getFiltroDisabilitazioneList
   * @return List&lt;TipoFiltroAbilitazioneServizi&gt;
  */
  public List<TipoFiltroAbilitazioneServizi> getFiltroDisabilitazione() {
  	return this.getFiltroDisabilitazioneList();
  }

  /**
   * Use method setFiltroDisabilitazioneList
   * @param filtroDisabilitazione List&lt;TipoFiltroAbilitazioneServizi&gt;
  */
  public void setFiltroDisabilitazione(List<TipoFiltroAbilitazioneServizi> filtroDisabilitazione) {
  	this.setFiltroDisabilitazioneList(filtroDisabilitazione);
  }

  /**
   * Use method sizeFiltroDisabilitazioneList
   * @return lunghezza della lista
  */
  public int sizeFiltroDisabilitazione() {
  	return this.sizeFiltroDisabilitazioneList();
  }

  @javax.xml.bind.annotation.XmlTransient
  protected java.lang.String statoRawEnumValue;

  @XmlAttribute(name="stato",required=false)
  protected StatoFunzionalita stato = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString("abilitato");

}
