/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
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
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
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
 * &lt;complexType name="stato-servizi-pdd-porta-delegata">
 * 		&lt;sequence>
 * 			&lt;element name="filtro-abilitazione" type="{http://www.openspcoop2.org/core/config}tipo-filtro-abilitazione-servizi" minOccurs="0" maxOccurs="unbounded"/>
 * 			&lt;element name="filtro-disabilitazione" type="{http://www.openspcoop2.org/core/config}tipo-filtro-abilitazione-servizi" minOccurs="0" maxOccurs="unbounded"/>
 * 		&lt;/sequence>
 * 		&lt;attribute name="stato" type="{http://www.openspcoop2.org/core/config}StatoFunzionalita" use="optional" default="abilitato"/>
 * &lt;/complexType>
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

public class StatoServiziPddPortaDelegata extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public StatoServiziPddPortaDelegata() {
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

  public void set_value_stato(String value) {
    this.stato = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString(value);
  }

  public String get_value_stato() {
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

  @XmlTransient
  private Long id;



  @XmlElement(name="filtro-abilitazione",required=true,nillable=false)
  protected List<TipoFiltroAbilitazioneServizi> filtroAbilitazione = new ArrayList<TipoFiltroAbilitazioneServizi>();

  /**
   * @deprecated Use method getFiltroAbilitazioneList
   * @return List<TipoFiltroAbilitazioneServizi>
  */
  @Deprecated
  public List<TipoFiltroAbilitazioneServizi> getFiltroAbilitazione() {
  	return this.filtroAbilitazione;
  }

  /**
   * @deprecated Use method setFiltroAbilitazioneList
   * @param filtroAbilitazione List<TipoFiltroAbilitazioneServizi>
  */
  @Deprecated
  public void setFiltroAbilitazione(List<TipoFiltroAbilitazioneServizi> filtroAbilitazione) {
  	this.filtroAbilitazione=filtroAbilitazione;
  }

  /**
   * @deprecated Use method sizeFiltroAbilitazioneList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizeFiltroAbilitazione() {
  	return this.filtroAbilitazione.size();
  }

  @XmlElement(name="filtro-disabilitazione",required=true,nillable=false)
  protected List<TipoFiltroAbilitazioneServizi> filtroDisabilitazione = new ArrayList<TipoFiltroAbilitazioneServizi>();

  /**
   * @deprecated Use method getFiltroDisabilitazioneList
   * @return List<TipoFiltroAbilitazioneServizi>
  */
  @Deprecated
  public List<TipoFiltroAbilitazioneServizi> getFiltroDisabilitazione() {
  	return this.filtroDisabilitazione;
  }

  /**
   * @deprecated Use method setFiltroDisabilitazioneList
   * @param filtroDisabilitazione List<TipoFiltroAbilitazioneServizi>
  */
  @Deprecated
  public void setFiltroDisabilitazione(List<TipoFiltroAbilitazioneServizi> filtroDisabilitazione) {
  	this.filtroDisabilitazione=filtroDisabilitazione;
  }

  /**
   * @deprecated Use method sizeFiltroDisabilitazioneList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizeFiltroDisabilitazione() {
  	return this.filtroDisabilitazione.size();
  }

  @XmlTransient
  protected java.lang.String _value_stato;

  @XmlAttribute(name="stato",required=false)
  protected StatoFunzionalita stato = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString("abilitato");

}
