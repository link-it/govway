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


/** <p>Java class for trasformazione-regola-applicabilita-richiesta complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="trasformazione-regola-applicabilita-richiesta"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="azione" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="unbounded"/&gt;
 * 			&lt;element name="content-type" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="unbounded"/&gt;
 * 			&lt;element name="pattern" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="connettore" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="unbounded"/&gt;
 * 			&lt;element name="soggetto" type="{http://www.openspcoop2.org/core/config}trasformazione-regola-applicabilita-soggetto" minOccurs="0" maxOccurs="unbounded"/&gt;
 * 			&lt;element name="servizio-applicativo" type="{http://www.openspcoop2.org/core/config}trasformazione-regola-applicabilita-servizio-applicativo" minOccurs="0" maxOccurs="unbounded"/&gt;
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
@XmlType(name = "trasformazione-regola-applicabilita-richiesta", 
  propOrder = {
  	"azione",
  	"contentType",
  	"pattern",
  	"connettore",
  	"soggetto",
  	"servizioApplicativo"
  }
)

@XmlRootElement(name = "trasformazione-regola-applicabilita-richiesta")

public class TrasformazioneRegolaApplicabilitaRichiesta extends org.openspcoop2.utils.beans.BaseBeanWithId implements Serializable , Cloneable {
  public TrasformazioneRegolaApplicabilitaRichiesta() {
    super();
  }

  public void addAzione(java.lang.String azione) {
    this.azione.add(azione);
  }

  public java.lang.String getAzione(int index) {
    return this.azione.get( index );
  }

  public java.lang.String removeAzione(int index) {
    return this.azione.remove( index );
  }

  public List<java.lang.String> getAzioneList() {
    return this.azione;
  }

  public void setAzioneList(List<java.lang.String> azione) {
    this.azione=azione;
  }

  public int sizeAzioneList() {
    return this.azione.size();
  }

  public void addContentType(java.lang.String contentType) {
    this.contentType.add(contentType);
  }

  public java.lang.String getContentType(int index) {
    return this.contentType.get( index );
  }

  public java.lang.String removeContentType(int index) {
    return this.contentType.remove( index );
  }

  public List<java.lang.String> getContentTypeList() {
    return this.contentType;
  }

  public void setContentTypeList(List<java.lang.String> contentType) {
    this.contentType=contentType;
  }

  public int sizeContentTypeList() {
    return this.contentType.size();
  }

  public java.lang.String getPattern() {
    return this.pattern;
  }

  public void setPattern(java.lang.String pattern) {
    this.pattern = pattern;
  }

  public void addConnettore(java.lang.String connettore) {
    this.connettore.add(connettore);
  }

  public java.lang.String getConnettore(int index) {
    return this.connettore.get( index );
  }

  public java.lang.String removeConnettore(int index) {
    return this.connettore.remove( index );
  }

  public List<java.lang.String> getConnettoreList() {
    return this.connettore;
  }

  public void setConnettoreList(List<java.lang.String> connettore) {
    this.connettore=connettore;
  }

  public int sizeConnettoreList() {
    return this.connettore.size();
  }

  public void addSoggetto(TrasformazioneRegolaApplicabilitaSoggetto soggetto) {
    this.soggetto.add(soggetto);
  }

  public TrasformazioneRegolaApplicabilitaSoggetto getSoggetto(int index) {
    return this.soggetto.get( index );
  }

  public TrasformazioneRegolaApplicabilitaSoggetto removeSoggetto(int index) {
    return this.soggetto.remove( index );
  }

  public List<TrasformazioneRegolaApplicabilitaSoggetto> getSoggettoList() {
    return this.soggetto;
  }

  public void setSoggettoList(List<TrasformazioneRegolaApplicabilitaSoggetto> soggetto) {
    this.soggetto=soggetto;
  }

  public int sizeSoggettoList() {
    return this.soggetto.size();
  }

  public void addServizioApplicativo(TrasformazioneRegolaApplicabilitaServizioApplicativo servizioApplicativo) {
    this.servizioApplicativo.add(servizioApplicativo);
  }

  public TrasformazioneRegolaApplicabilitaServizioApplicativo getServizioApplicativo(int index) {
    return this.servizioApplicativo.get( index );
  }

  public TrasformazioneRegolaApplicabilitaServizioApplicativo removeServizioApplicativo(int index) {
    return this.servizioApplicativo.remove( index );
  }

  public List<TrasformazioneRegolaApplicabilitaServizioApplicativo> getServizioApplicativoList() {
    return this.servizioApplicativo;
  }

  public void setServizioApplicativoList(List<TrasformazioneRegolaApplicabilitaServizioApplicativo> servizioApplicativo) {
    this.servizioApplicativo=servizioApplicativo;
  }

  public int sizeServizioApplicativoList() {
    return this.servizioApplicativo.size();
  }

  private static final long serialVersionUID = 1L;



  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="azione",required=true,nillable=false)
  private List<java.lang.String> azione = new ArrayList<>();

  /**
   * Use method getAzioneList
   * @return List&lt;java.lang.String&gt;
  */
  public List<java.lang.String> getAzione() {
  	return this.getAzioneList();
  }

  /**
   * Use method setAzioneList
   * @param azione List&lt;java.lang.String&gt;
  */
  public void setAzione(List<java.lang.String> azione) {
  	this.setAzioneList(azione);
  }

  /**
   * Use method sizeAzioneList
   * @return lunghezza della lista
  */
  public int sizeAzione() {
  	return this.sizeAzioneList();
  }

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="content-type",required=true,nillable=false)
  private List<java.lang.String> contentType = new ArrayList<>();

  /**
   * Use method getContentTypeList
   * @return List&lt;java.lang.String&gt;
  */
  public List<java.lang.String> getContentType() {
  	return this.getContentTypeList();
  }

  /**
   * Use method setContentTypeList
   * @param contentType List&lt;java.lang.String&gt;
  */
  public void setContentType(List<java.lang.String> contentType) {
  	this.setContentTypeList(contentType);
  }

  /**
   * Use method sizeContentTypeList
   * @return lunghezza della lista
  */
  public int sizeContentType() {
  	return this.sizeContentTypeList();
  }

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="pattern",required=false,nillable=false)
  protected java.lang.String pattern;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="connettore",required=true,nillable=false)
  private List<java.lang.String> connettore = new ArrayList<>();

  /**
   * Use method getConnettoreList
   * @return List&lt;java.lang.String&gt;
  */
  public List<java.lang.String> getConnettore() {
  	return this.getConnettoreList();
  }

  /**
   * Use method setConnettoreList
   * @param connettore List&lt;java.lang.String&gt;
  */
  public void setConnettore(List<java.lang.String> connettore) {
  	this.setConnettoreList(connettore);
  }

  /**
   * Use method sizeConnettoreList
   * @return lunghezza della lista
  */
  public int sizeConnettore() {
  	return this.sizeConnettoreList();
  }

  @XmlElement(name="soggetto",required=true,nillable=false)
  private List<TrasformazioneRegolaApplicabilitaSoggetto> soggetto = new ArrayList<>();

  /**
   * Use method getSoggettoList
   * @return List&lt;TrasformazioneRegolaApplicabilitaSoggetto&gt;
  */
  public List<TrasformazioneRegolaApplicabilitaSoggetto> getSoggetto() {
  	return this.getSoggettoList();
  }

  /**
   * Use method setSoggettoList
   * @param soggetto List&lt;TrasformazioneRegolaApplicabilitaSoggetto&gt;
  */
  public void setSoggetto(List<TrasformazioneRegolaApplicabilitaSoggetto> soggetto) {
  	this.setSoggettoList(soggetto);
  }

  /**
   * Use method sizeSoggettoList
   * @return lunghezza della lista
  */
  public int sizeSoggetto() {
  	return this.sizeSoggettoList();
  }

  @XmlElement(name="servizio-applicativo",required=true,nillable=false)
  private List<TrasformazioneRegolaApplicabilitaServizioApplicativo> servizioApplicativo = new ArrayList<>();

  /**
   * Use method getServizioApplicativoList
   * @return List&lt;TrasformazioneRegolaApplicabilitaServizioApplicativo&gt;
  */
  public List<TrasformazioneRegolaApplicabilitaServizioApplicativo> getServizioApplicativo() {
  	return this.getServizioApplicativoList();
  }

  /**
   * Use method setServizioApplicativoList
   * @param servizioApplicativo List&lt;TrasformazioneRegolaApplicabilitaServizioApplicativo&gt;
  */
  public void setServizioApplicativo(List<TrasformazioneRegolaApplicabilitaServizioApplicativo> servizioApplicativo) {
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
