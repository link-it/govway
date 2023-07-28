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
package org.openspcoop2.core.plugins;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import java.io.Serializable;


/** <p>Java class for id-configurazione-servizio complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="id-configurazione-servizio"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="accordo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="tipo-soggetto-referente" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="nome-soggetto-referente" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="versione" type="{http://www.w3.org/2001/XMLSchema}unsignedInt" minOccurs="0" maxOccurs="1" default="1"/&gt;
 * 			&lt;element name="servizio" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="1" maxOccurs="1"/&gt;
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
@XmlType(name = "id-configurazione-servizio", 
  propOrder = {
  	"accordo",
  	"tipoSoggettoReferente",
  	"nomeSoggettoReferente",
  	"versione",
  	"servizio"
  }
)

@XmlRootElement(name = "id-configurazione-servizio")

public class IdConfigurazioneServizio extends org.openspcoop2.utils.beans.BaseBeanWithId implements Serializable , Cloneable {
  public IdConfigurazioneServizio() {
    super();
  }

  public java.lang.String getAccordo() {
    return this.accordo;
  }

  public void setAccordo(java.lang.String accordo) {
    this.accordo = accordo;
  }

  public java.lang.String getTipoSoggettoReferente() {
    return this.tipoSoggettoReferente;
  }

  public void setTipoSoggettoReferente(java.lang.String tipoSoggettoReferente) {
    this.tipoSoggettoReferente = tipoSoggettoReferente;
  }

  public java.lang.String getNomeSoggettoReferente() {
    return this.nomeSoggettoReferente;
  }

  public void setNomeSoggettoReferente(java.lang.String nomeSoggettoReferente) {
    this.nomeSoggettoReferente = nomeSoggettoReferente;
  }

  public java.lang.Integer getVersione() {
    return this.versione;
  }

  public void setVersione(java.lang.Integer versione) {
    this.versione = versione;
  }

  public java.lang.String getServizio() {
    return this.servizio;
  }

  public void setServizio(java.lang.String servizio) {
    this.servizio = servizio;
  }

  private static final long serialVersionUID = 1L;



  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="accordo",required=true,nillable=false)
  protected java.lang.String accordo;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="tipo-soggetto-referente",required=false,nillable=false)
  protected java.lang.String tipoSoggettoReferente;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="nome-soggetto-referente",required=false,nillable=false)
  protected java.lang.String nomeSoggettoReferente;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="unsignedInt")
  @XmlElement(name="versione",required=false,nillable=false,defaultValue="1")
  protected java.lang.Integer versione = java.lang.Integer.valueOf("1");

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="servizio",required=true,nillable=false)
  protected java.lang.String servizio;

}
