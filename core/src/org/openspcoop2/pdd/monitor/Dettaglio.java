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
package org.openspcoop2.pdd.monitor;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Java class for dettaglio complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="dettaglio"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="errore-processamento" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="id-correlazione-applicativa" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="id-modulo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="tipo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="servizio-applicativo-consegna" type="{http://www.openspcoop2.org/pdd/monitor}servizio-applicativo-consegna" minOccurs="0" maxOccurs="unbounded"/&gt;
 * 			&lt;element name="proprieta" type="{http://www.openspcoop2.org/pdd/monitor}proprieta" minOccurs="0" maxOccurs="unbounded"/&gt;
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
@XmlType(name = "dettaglio", 
  propOrder = {
  	"erroreProcessamento",
  	"idCorrelazioneApplicativa",
  	"idModulo",
  	"tipo",
  	"servizioApplicativoConsegna",
  	"proprieta"
  }
)

@XmlRootElement(name = "dettaglio")

public class Dettaglio extends org.openspcoop2.utils.beans.BaseBeanWithId implements Serializable , Cloneable {
  public Dettaglio() {
    super();
  }

  public java.lang.String getErroreProcessamento() {
    return this.erroreProcessamento;
  }

  public void setErroreProcessamento(java.lang.String erroreProcessamento) {
    this.erroreProcessamento = erroreProcessamento;
  }

  public java.lang.String getIdCorrelazioneApplicativa() {
    return this.idCorrelazioneApplicativa;
  }

  public void setIdCorrelazioneApplicativa(java.lang.String idCorrelazioneApplicativa) {
    this.idCorrelazioneApplicativa = idCorrelazioneApplicativa;
  }

  public java.lang.String getIdModulo() {
    return this.idModulo;
  }

  public void setIdModulo(java.lang.String idModulo) {
    this.idModulo = idModulo;
  }

  public java.lang.String getTipo() {
    return this.tipo;
  }

  public void setTipo(java.lang.String tipo) {
    this.tipo = tipo;
  }

  public void addServizioApplicativoConsegna(ServizioApplicativoConsegna servizioApplicativoConsegna) {
    this.servizioApplicativoConsegna.add(servizioApplicativoConsegna);
  }

  public ServizioApplicativoConsegna getServizioApplicativoConsegna(int index) {
    return this.servizioApplicativoConsegna.get( index );
  }

  public ServizioApplicativoConsegna removeServizioApplicativoConsegna(int index) {
    return this.servizioApplicativoConsegna.remove( index );
  }

  public List<ServizioApplicativoConsegna> getServizioApplicativoConsegnaList() {
    return this.servizioApplicativoConsegna;
  }

  public void setServizioApplicativoConsegnaList(List<ServizioApplicativoConsegna> servizioApplicativoConsegna) {
    this.servizioApplicativoConsegna=servizioApplicativoConsegna;
  }

  public int sizeServizioApplicativoConsegnaList() {
    return this.servizioApplicativoConsegna.size();
  }

  public void addProprieta(Proprieta proprieta) {
    this.proprieta.add(proprieta);
  }

  public Proprieta getProprieta(int index) {
    return this.proprieta.get( index );
  }

  public Proprieta removeProprieta(int index) {
    return this.proprieta.remove( index );
  }

  public List<Proprieta> getProprietaList() {
    return this.proprieta;
  }

  public void setProprietaList(List<Proprieta> proprieta) {
    this.proprieta=proprieta;
  }

  public int sizeProprietaList() {
    return this.proprieta.size();
  }

  private static final long serialVersionUID = 1L;



  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="errore-processamento",required=false,nillable=false)
  protected java.lang.String erroreProcessamento;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="id-correlazione-applicativa",required=false,nillable=false)
  protected java.lang.String idCorrelazioneApplicativa;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="id-modulo",required=true,nillable=false)
  protected java.lang.String idModulo;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="tipo",required=true,nillable=false)
  protected java.lang.String tipo;

  @XmlElement(name="servizio-applicativo-consegna",required=true,nillable=false)
  protected List<ServizioApplicativoConsegna> servizioApplicativoConsegna = new ArrayList<ServizioApplicativoConsegna>();

  /**
   * @deprecated Use method getServizioApplicativoConsegnaList
   * @return List&lt;ServizioApplicativoConsegna&gt;
  */
  @Deprecated
  public List<ServizioApplicativoConsegna> getServizioApplicativoConsegna() {
  	return this.servizioApplicativoConsegna;
  }

  /**
   * @deprecated Use method setServizioApplicativoConsegnaList
   * @param servizioApplicativoConsegna List&lt;ServizioApplicativoConsegna&gt;
  */
  @Deprecated
  public void setServizioApplicativoConsegna(List<ServizioApplicativoConsegna> servizioApplicativoConsegna) {
  	this.servizioApplicativoConsegna=servizioApplicativoConsegna;
  }

  /**
   * @deprecated Use method sizeServizioApplicativoConsegnaList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizeServizioApplicativoConsegna() {
  	return this.servizioApplicativoConsegna.size();
  }

  @XmlElement(name="proprieta",required=true,nillable=false)
  protected List<Proprieta> proprieta = new ArrayList<Proprieta>();

  /**
   * @deprecated Use method getProprietaList
   * @return List&lt;Proprieta&gt;
  */
  @Deprecated
  public List<Proprieta> getProprieta() {
  	return this.proprieta;
  }

  /**
   * @deprecated Use method setProprietaList
   * @param proprieta List&lt;Proprieta&gt;
  */
  @Deprecated
  public void setProprieta(List<Proprieta> proprieta) {
  	this.proprieta=proprieta;
  }

  /**
   * @deprecated Use method sizeProprietaList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizeProprieta() {
  	return this.proprieta.size();
  }

}
