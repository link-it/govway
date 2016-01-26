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
package org.openspcoop2.core.registry;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Java class for accordo-cooperazione-partecipanti complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="accordo-cooperazione-partecipanti">
 * 		&lt;sequence>
 * 			&lt;element name="soggetto-partecipante" type="{http://www.openspcoop2.org/core/registry}id-soggetto" minOccurs="0" maxOccurs="unbounded"/>
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
@XmlType(name = "accordo-cooperazione-partecipanti", 
  propOrder = {
  	"soggettoPartecipante"
  }
)

@XmlRootElement(name = "accordo-cooperazione-partecipanti")

public class AccordoCooperazionePartecipanti extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public AccordoCooperazionePartecipanti() {
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

  public void addSoggettoPartecipante(IdSoggetto soggettoPartecipante) {
    this.soggettoPartecipante.add(soggettoPartecipante);
  }

  public IdSoggetto getSoggettoPartecipante(int index) {
    return this.soggettoPartecipante.get( index );
  }

  public IdSoggetto removeSoggettoPartecipante(int index) {
    return this.soggettoPartecipante.remove( index );
  }

  public List<IdSoggetto> getSoggettoPartecipanteList() {
    return this.soggettoPartecipante;
  }

  public void setSoggettoPartecipanteList(List<IdSoggetto> soggettoPartecipante) {
    this.soggettoPartecipante=soggettoPartecipante;
  }

  public int sizeSoggettoPartecipanteList() {
    return this.soggettoPartecipante.size();
  }

  private static final long serialVersionUID = 1L;

  @XmlTransient
  private Long id;



  @XmlElement(name="soggetto-partecipante",required=true,nillable=false)
  protected List<IdSoggetto> soggettoPartecipante = new ArrayList<IdSoggetto>();

  /**
   * @deprecated Use method getSoggettoPartecipanteList
   * @return List<IdSoggetto>
  */
  @Deprecated
  public List<IdSoggetto> getSoggettoPartecipante() {
  	return this.soggettoPartecipante;
  }

  /**
   * @deprecated Use method setSoggettoPartecipanteList
   * @param soggettoPartecipante List<IdSoggetto>
  */
  @Deprecated
  public void setSoggettoPartecipante(List<IdSoggetto> soggettoPartecipante) {
  	this.soggettoPartecipante=soggettoPartecipante;
  }

  /**
   * @deprecated Use method sizeSoggettoPartecipanteList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizeSoggettoPartecipante() {
  	return this.soggettoPartecipante.size();
  }

}
