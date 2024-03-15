/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it).
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
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Java class for tracciamento complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="tracciamento"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="openspcoop-appender" type="{http://www.openspcoop2.org/core/config}openspcoop-appender" minOccurs="0" maxOccurs="unbounded"/&gt;
 * 			&lt;element name="openspcoop-sorgente-dati" type="{http://www.openspcoop2.org/core/config}openspcoop-sorgente-dati" minOccurs="0" maxOccurs="unbounded"/&gt;
 * 			&lt;element name="porta-delegata" type="{http://www.openspcoop2.org/core/config}configurazione-tracciamento-porta" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="porta-applicativa" type="{http://www.openspcoop2.org/core/config}configurazione-tracciamento-porta" minOccurs="0" maxOccurs="1"/&gt;
 * 		&lt;/sequence&gt;
 * 		&lt;attribute name="stato" type="{http://www.openspcoop2.org/core/config}StatoFunzionalita" use="optional" default="abilitato"/&gt;
 * 		&lt;attribute name="esiti" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tracciamento", 
  propOrder = {
  	"openspcoopAppender",
  	"openspcoopSorgenteDati",
  	"portaDelegata",
  	"portaApplicativa"
  }
)

@XmlRootElement(name = "tracciamento")

public class Tracciamento extends org.openspcoop2.utils.beans.BaseBeanWithId implements Serializable , Cloneable {
  public Tracciamento() {
    super();
  }

  public void addOpenspcoopAppender(OpenspcoopAppender openspcoopAppender) {
    this.openspcoopAppender.add(openspcoopAppender);
  }

  public OpenspcoopAppender getOpenspcoopAppender(int index) {
    return this.openspcoopAppender.get( index );
  }

  public OpenspcoopAppender removeOpenspcoopAppender(int index) {
    return this.openspcoopAppender.remove( index );
  }

  public List<OpenspcoopAppender> getOpenspcoopAppenderList() {
    return this.openspcoopAppender;
  }

  public void setOpenspcoopAppenderList(List<OpenspcoopAppender> openspcoopAppender) {
    this.openspcoopAppender=openspcoopAppender;
  }

  public int sizeOpenspcoopAppenderList() {
    return this.openspcoopAppender.size();
  }

  public void addOpenspcoopSorgenteDati(OpenspcoopSorgenteDati openspcoopSorgenteDati) {
    this.openspcoopSorgenteDati.add(openspcoopSorgenteDati);
  }

  public OpenspcoopSorgenteDati getOpenspcoopSorgenteDati(int index) {
    return this.openspcoopSorgenteDati.get( index );
  }

  public OpenspcoopSorgenteDati removeOpenspcoopSorgenteDati(int index) {
    return this.openspcoopSorgenteDati.remove( index );
  }

  public List<OpenspcoopSorgenteDati> getOpenspcoopSorgenteDatiList() {
    return this.openspcoopSorgenteDati;
  }

  public void setOpenspcoopSorgenteDatiList(List<OpenspcoopSorgenteDati> openspcoopSorgenteDati) {
    this.openspcoopSorgenteDati=openspcoopSorgenteDati;
  }

  public int sizeOpenspcoopSorgenteDatiList() {
    return this.openspcoopSorgenteDati.size();
  }

  public ConfigurazioneTracciamentoPorta getPortaDelegata() {
    return this.portaDelegata;
  }

  public void setPortaDelegata(ConfigurazioneTracciamentoPorta portaDelegata) {
    this.portaDelegata = portaDelegata;
  }

  public ConfigurazioneTracciamentoPorta getPortaApplicativa() {
    return this.portaApplicativa;
  }

  public void setPortaApplicativa(ConfigurazioneTracciamentoPorta portaApplicativa) {
    this.portaApplicativa = portaApplicativa;
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

  public java.lang.String getEsiti() {
    return this.esiti;
  }

  public void setEsiti(java.lang.String esiti) {
    this.esiti = esiti;
  }

  private static final long serialVersionUID = 1L;



  @XmlElement(name="openspcoop-appender",required=true,nillable=false)
  private List<OpenspcoopAppender> openspcoopAppender = new ArrayList<>();

  /**
   * Use method getOpenspcoopAppenderList
   * @return List&lt;OpenspcoopAppender&gt;
  */
  public List<OpenspcoopAppender> getOpenspcoopAppender() {
  	return this.getOpenspcoopAppenderList();
  }

  /**
   * Use method setOpenspcoopAppenderList
   * @param openspcoopAppender List&lt;OpenspcoopAppender&gt;
  */
  public void setOpenspcoopAppender(List<OpenspcoopAppender> openspcoopAppender) {
  	this.setOpenspcoopAppenderList(openspcoopAppender);
  }

  /**
   * Use method sizeOpenspcoopAppenderList
   * @return lunghezza della lista
  */
  public int sizeOpenspcoopAppender() {
  	return this.sizeOpenspcoopAppenderList();
  }

  @XmlElement(name="openspcoop-sorgente-dati",required=true,nillable=false)
  private List<OpenspcoopSorgenteDati> openspcoopSorgenteDati = new ArrayList<>();

  /**
   * Use method getOpenspcoopSorgenteDatiList
   * @return List&lt;OpenspcoopSorgenteDati&gt;
  */
  public List<OpenspcoopSorgenteDati> getOpenspcoopSorgenteDati() {
  	return this.getOpenspcoopSorgenteDatiList();
  }

  /**
   * Use method setOpenspcoopSorgenteDatiList
   * @param openspcoopSorgenteDati List&lt;OpenspcoopSorgenteDati&gt;
  */
  public void setOpenspcoopSorgenteDati(List<OpenspcoopSorgenteDati> openspcoopSorgenteDati) {
  	this.setOpenspcoopSorgenteDatiList(openspcoopSorgenteDati);
  }

  /**
   * Use method sizeOpenspcoopSorgenteDatiList
   * @return lunghezza della lista
  */
  public int sizeOpenspcoopSorgenteDati() {
  	return this.sizeOpenspcoopSorgenteDatiList();
  }

  @XmlElement(name="porta-delegata",required=false,nillable=false)
  protected ConfigurazioneTracciamentoPorta portaDelegata;

  @XmlElement(name="porta-applicativa",required=false,nillable=false)
  protected ConfigurazioneTracciamentoPorta portaApplicativa;

  @jakarta.xml.bind.annotation.XmlTransient
  protected java.lang.String statoRawEnumValue;

  @XmlAttribute(name="stato",required=false)
  protected StatoFunzionalita stato = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString("abilitato");

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="esiti",required=false)
  protected java.lang.String esiti;

}
