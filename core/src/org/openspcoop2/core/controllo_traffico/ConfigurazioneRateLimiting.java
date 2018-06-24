/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it).
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
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;


/** <p>Java class for configurazione-rate-limiting complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="configurazione-rate-limiting">
 * 		&lt;sequence>
 * 			&lt;element name="tipo-errore" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="1" maxOccurs="1" default="fault"/>
 * 			&lt;element name="tipo-errore-includi-descrizione" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="1" maxOccurs="1" default="true"/>
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
@XmlType(name = "configurazione-rate-limiting", 
  propOrder = {
  	"tipoErrore",
  	"tipoErroreIncludiDescrizione"
  }
)

@XmlRootElement(name = "configurazione-rate-limiting")

public class ConfigurazioneRateLimiting extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public ConfigurazioneRateLimiting() {
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

  private static final long serialVersionUID = 1L;

  @XmlTransient
  private Long id;



  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="tipo-errore",required=true,nillable=false,defaultValue="fault")
  protected java.lang.String tipoErrore = "fault";

  @javax.xml.bind.annotation.XmlSchemaType(name="boolean")
  @XmlElement(name="tipo-errore-includi-descrizione",required=true,nillable=false,defaultValue="true")
  protected boolean tipoErroreIncludiDescrizione = true;

}
