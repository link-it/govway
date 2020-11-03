/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it).
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


/** <p>Java class for validazione-contenuti-applicativi-richiesta-applicabilita complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="validazione-contenuti-applicativi-richiesta-applicabilita"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="azione" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="unbounded"/&gt;
 * 			&lt;element name="content-type" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="unbounded"/&gt;
 * 			&lt;element name="match" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/&gt;
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
@XmlType(name = "validazione-contenuti-applicativi-richiesta-applicabilita", 
  propOrder = {
  	"azione",
  	"contentType",
  	"match"
  }
)

@XmlRootElement(name = "validazione-contenuti-applicativi-richiesta-applicabilita")

public class ValidazioneContenutiApplicativiRichiestaApplicabilita extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public ValidazioneContenutiApplicativiRichiestaApplicabilita() {
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

  public java.lang.String getMatch() {
    return this.match;
  }

  public void setMatch(java.lang.String match) {
    this.match = match;
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
  @XmlElement(name="match",required=false,nillable=false)
  protected java.lang.String match;

}
