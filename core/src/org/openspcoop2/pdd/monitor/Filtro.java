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
package org.openspcoop2.pdd.monitor;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import org.openspcoop2.pdd.monitor.constants.StatoMessaggio;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Java class for filtro complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="filtro">
 * 		&lt;sequence>
 * 			&lt;element name="correlazione-applicativa" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="busta" type="{http://www.openspcoop2.org/pdd/monitor}busta" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="id-messaggio" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="message-pattern" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="soglia" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="stato" type="{http://www.openspcoop2.org/pdd/monitor}StatoMessaggio" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="tipo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="soggetto" type="{http://www.openspcoop2.org/pdd/monitor}busta-soggetto" minOccurs="0" maxOccurs="unbounded"/>
 * 			&lt;element name="proprieta" type="{http://www.openspcoop2.org/pdd/monitor}proprieta" minOccurs="0" maxOccurs="unbounded"/>
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
@XmlType(name = "filtro", 
  propOrder = {
  	"correlazioneApplicativa",
  	"busta",
  	"idMessaggio",
  	"messagePattern",
  	"soglia",
  	"stato",
  	"tipo",
  	"soggetto",
  	"proprieta"
  }
)

@XmlRootElement(name = "filtro")

public class Filtro extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public Filtro() {
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

  public java.lang.String getCorrelazioneApplicativa() {
    return this.correlazioneApplicativa;
  }

  public void setCorrelazioneApplicativa(java.lang.String correlazioneApplicativa) {
    this.correlazioneApplicativa = correlazioneApplicativa;
  }

  public Busta getBusta() {
    return this.busta;
  }

  public void setBusta(Busta busta) {
    this.busta = busta;
  }

  public java.lang.String getIdMessaggio() {
    return this.idMessaggio;
  }

  public void setIdMessaggio(java.lang.String idMessaggio) {
    this.idMessaggio = idMessaggio;
  }

  public java.lang.String getMessagePattern() {
    return this.messagePattern;
  }

  public void setMessagePattern(java.lang.String messagePattern) {
    this.messagePattern = messagePattern;
  }

  public long getSoglia() {
    return this.soglia;
  }

  public void setSoglia(long soglia) {
    this.soglia = soglia;
  }

  public void set_value_stato(String value) {
    this.stato = (StatoMessaggio) StatoMessaggio.toEnumConstantFromString(value);
  }

  public String get_value_stato() {
    if(this.stato == null){
    	return null;
    }else{
    	return this.stato.toString();
    }
  }

  public org.openspcoop2.pdd.monitor.constants.StatoMessaggio getStato() {
    return this.stato;
  }

  public void setStato(org.openspcoop2.pdd.monitor.constants.StatoMessaggio stato) {
    this.stato = stato;
  }

  public java.lang.String getTipo() {
    return this.tipo;
  }

  public void setTipo(java.lang.String tipo) {
    this.tipo = tipo;
  }

  public void addSoggetto(BustaSoggetto soggetto) {
    this.soggetto.add(soggetto);
  }

  public BustaSoggetto getSoggetto(int index) {
    return this.soggetto.get( index );
  }

  public BustaSoggetto removeSoggetto(int index) {
    return this.soggetto.remove( index );
  }

  public List<BustaSoggetto> getSoggettoList() {
    return this.soggetto;
  }

  public void setSoggettoList(List<BustaSoggetto> soggetto) {
    this.soggetto=soggetto;
  }

  public int sizeSoggettoList() {
    return this.soggetto.size();
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

  @XmlTransient
  private Long id;



  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="correlazione-applicativa",required=false,nillable=false)
  protected java.lang.String correlazioneApplicativa;

  @XmlElement(name="busta",required=false,nillable=false)
  protected Busta busta;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="id-messaggio",required=false,nillable=false)
  protected java.lang.String idMessaggio;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="message-pattern",required=false,nillable=false)
  protected java.lang.String messagePattern;

  @javax.xml.bind.annotation.XmlSchemaType(name="long")
  @XmlElement(name="soglia",required=false,nillable=false)
  protected long soglia = -1; // default utilizzato dal driver

  @XmlTransient
  protected java.lang.String _value_stato;

  @XmlElement(name="stato",required=false,nillable=false)
  protected StatoMessaggio stato;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="tipo",required=false,nillable=false)
  protected java.lang.String tipo;

  @XmlElement(name="soggetto",required=true,nillable=false)
  protected List<BustaSoggetto> soggetto = new ArrayList<BustaSoggetto>();

  /**
   * @deprecated Use method getSoggettoList
   * @return List<BustaSoggetto>
  */
  @Deprecated
  public List<BustaSoggetto> getSoggetto() {
  	return this.soggetto;
  }

  /**
   * @deprecated Use method setSoggettoList
   * @param soggetto List<BustaSoggetto>
  */
  @Deprecated
  public void setSoggetto(List<BustaSoggetto> soggetto) {
  	this.soggetto=soggetto;
  }

  /**
   * @deprecated Use method sizeSoggettoList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizeSoggetto() {
  	return this.soggetto.size();
  }

  @XmlElement(name="proprieta",required=true,nillable=false)
  protected List<Proprieta> proprieta = new ArrayList<Proprieta>();

  /**
   * @deprecated Use method getProprietaList
   * @return List<Proprieta>
  */
  @Deprecated
  public List<Proprieta> getProprieta() {
  	return this.proprieta;
  }

  /**
   * @deprecated Use method setProprietaList
   * @param proprieta List<Proprieta>
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
