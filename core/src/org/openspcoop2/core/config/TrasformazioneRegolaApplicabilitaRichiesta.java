/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it).
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
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
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

public class TrasformazioneRegolaApplicabilitaRichiesta extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public TrasformazioneRegolaApplicabilitaRichiesta() {
  }

  public Long getId() {
    if(this.id!=null)
		return this.id;
	else
		return Long.valueOf(-1);
  }

  public void setId(Long id) {
    if(id!=null)
		this.id=id;
	else
		this.id=Long.valueOf(-1);
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

  @XmlTransient
  private Long id;



  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="azione",required=true,nillable=false)
  protected List<java.lang.String> azione = new ArrayList<java.lang.String>();

  /**
   * @deprecated Use method getAzioneList
   * @return List&lt;java.lang.String&gt;
  */
  @Deprecated
  public List<java.lang.String> getAzione() {
  	return this.azione;
  }

  /**
   * @deprecated Use method setAzioneList
   * @param azione List&lt;java.lang.String&gt;
  */
  @Deprecated
  public void setAzione(List<java.lang.String> azione) {
  	this.azione=azione;
  }

  /**
   * @deprecated Use method sizeAzioneList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizeAzione() {
  	return this.azione.size();
  }

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="content-type",required=true,nillable=false)
  protected List<java.lang.String> contentType = new ArrayList<java.lang.String>();

  /**
   * @deprecated Use method getContentTypeList
   * @return List&lt;java.lang.String&gt;
  */
  @Deprecated
  public List<java.lang.String> getContentType() {
  	return this.contentType;
  }

  /**
   * @deprecated Use method setContentTypeList
   * @param contentType List&lt;java.lang.String&gt;
  */
  @Deprecated
  public void setContentType(List<java.lang.String> contentType) {
  	this.contentType=contentType;
  }

  /**
   * @deprecated Use method sizeContentTypeList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizeContentType() {
  	return this.contentType.size();
  }

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="pattern",required=false,nillable=false)
  protected java.lang.String pattern;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="connettore",required=true,nillable=false)
  protected List<java.lang.String> connettore = new ArrayList<java.lang.String>();

  /**
   * @deprecated Use method getConnettoreList
   * @return List&lt;java.lang.String&gt;
  */
  @Deprecated
  public List<java.lang.String> getConnettore() {
  	return this.connettore;
  }

  /**
   * @deprecated Use method setConnettoreList
   * @param connettore List&lt;java.lang.String&gt;
  */
  @Deprecated
  public void setConnettore(List<java.lang.String> connettore) {
  	this.connettore=connettore;
  }

  /**
   * @deprecated Use method sizeConnettoreList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizeConnettore() {
  	return this.connettore.size();
  }

  @XmlElement(name="soggetto",required=true,nillable=false)
  protected List<TrasformazioneRegolaApplicabilitaSoggetto> soggetto = new ArrayList<TrasformazioneRegolaApplicabilitaSoggetto>();

  /**
   * @deprecated Use method getSoggettoList
   * @return List&lt;TrasformazioneRegolaApplicabilitaSoggetto&gt;
  */
  @Deprecated
  public List<TrasformazioneRegolaApplicabilitaSoggetto> getSoggetto() {
  	return this.soggetto;
  }

  /**
   * @deprecated Use method setSoggettoList
   * @param soggetto List&lt;TrasformazioneRegolaApplicabilitaSoggetto&gt;
  */
  @Deprecated
  public void setSoggetto(List<TrasformazioneRegolaApplicabilitaSoggetto> soggetto) {
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

  @XmlElement(name="servizio-applicativo",required=true,nillable=false)
  protected List<TrasformazioneRegolaApplicabilitaServizioApplicativo> servizioApplicativo = new ArrayList<TrasformazioneRegolaApplicabilitaServizioApplicativo>();

  /**
   * @deprecated Use method getServizioApplicativoList
   * @return List&lt;TrasformazioneRegolaApplicabilitaServizioApplicativo&gt;
  */
  @Deprecated
  public List<TrasformazioneRegolaApplicabilitaServizioApplicativo> getServizioApplicativo() {
  	return this.servizioApplicativo;
  }

  /**
   * @deprecated Use method setServizioApplicativoList
   * @param servizioApplicativo List&lt;TrasformazioneRegolaApplicabilitaServizioApplicativo&gt;
  */
  @Deprecated
  public void setServizioApplicativo(List<TrasformazioneRegolaApplicabilitaServizioApplicativo> servizioApplicativo) {
  	this.servizioApplicativo=servizioApplicativo;
  }

  /**
   * @deprecated Use method sizeServizioApplicativoList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizeServizioApplicativo() {
  	return this.servizioApplicativo.size();
  }

}
