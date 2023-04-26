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
package org.openspcoop2.core.controllo_traffico;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Java class for configurazione-rate-limiting complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="configurazione-rate-limiting"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="tipo-errore" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="1" maxOccurs="1" default="fault"/&gt;
 * 			&lt;element name="tipo-errore-includi-descrizione" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="1" maxOccurs="1" default="true"/&gt;
 * 			&lt;element name="proprieta" type="{http://www.openspcoop2.org/core/controllo_traffico}configurazione-rate-limiting-proprieta" minOccurs="0" maxOccurs="unbounded"/&gt;
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
@XmlType(name = "configurazione-rate-limiting", 
  propOrder = {
  	"tipoErrore",
  	"tipoErroreIncludiDescrizione",
  	"proprieta"
  }
)

@XmlRootElement(name = "configurazione-rate-limiting")

public class ConfigurazioneRateLimiting extends org.openspcoop2.utils.beans.BaseBeanWithId implements Serializable , Cloneable {
  public ConfigurazioneRateLimiting() {
    super();
  }

  public java.lang.String getTipoErrore() {
    return this.tipoErrore;
  }

  public void setTipoErrore(java.lang.String tipoErrore) {
    this.tipoErrore = tipoErrore;
  }

  public boolean isTipoErroreIncludiDescrizione() {
    return this.tipoErroreIncludiDescrizione;
  }

  public boolean getTipoErroreIncludiDescrizione() {
    return this.tipoErroreIncludiDescrizione;
  }

  public void setTipoErroreIncludiDescrizione(boolean tipoErroreIncludiDescrizione) {
    this.tipoErroreIncludiDescrizione = tipoErroreIncludiDescrizione;
  }

  public void addProprieta(ConfigurazioneRateLimitingProprieta proprieta) {
    this.proprieta.add(proprieta);
  }

  public ConfigurazioneRateLimitingProprieta getProprieta(int index) {
    return this.proprieta.get( index );
  }

  public ConfigurazioneRateLimitingProprieta removeProprieta(int index) {
    return this.proprieta.remove( index );
  }

  public List<ConfigurazioneRateLimitingProprieta> getProprietaList() {
    return this.proprieta;
  }

  public void setProprietaList(List<ConfigurazioneRateLimitingProprieta> proprieta) {
    this.proprieta=proprieta;
  }

  public int sizeProprietaList() {
    return this.proprieta.size();
  }

  private static final long serialVersionUID = 1L;



  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="tipo-errore",required=true,nillable=false,defaultValue="fault")
  protected java.lang.String tipoErrore = "fault";

  @javax.xml.bind.annotation.XmlSchemaType(name="boolean")
  @XmlElement(name="tipo-errore-includi-descrizione",required=true,nillable=false,defaultValue="true")
  protected boolean tipoErroreIncludiDescrizione = true;

  @XmlElement(name="proprieta",required=true,nillable=false)
  private List<ConfigurazioneRateLimitingProprieta> proprieta = new ArrayList<>();

  /**
   * Use method getProprietaList
   * @return List&lt;ConfigurazioneRateLimitingProprieta&gt;
  */
  public List<ConfigurazioneRateLimitingProprieta> getProprieta() {
  	return this.getProprietaList();
  }

  /**
   * Use method setProprietaList
   * @param proprieta List&lt;ConfigurazioneRateLimitingProprieta&gt;
  */
  public void setProprieta(List<ConfigurazioneRateLimitingProprieta> proprieta) {
  	this.setProprietaList(proprieta);
  }

  /**
   * Use method sizeProprietaList
   * @return lunghezza della lista
  */
  public int sizeProprieta() {
  	return this.sizeProprietaList();
  }

}
