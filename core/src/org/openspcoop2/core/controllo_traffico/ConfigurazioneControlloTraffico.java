/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it).
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


/** <p>Java class for configurazione-controllo-traffico complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="configurazione-controllo-traffico"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="controllo-max-threads-enabled" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="1" maxOccurs="1" default="true"/&gt;
 * 			&lt;element name="controllo-max-threads-warning-only" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="1" maxOccurs="1" default="false"/&gt;
 * 			&lt;element name="controllo-max-threads-soglia" type="{http://www.w3.org/2001/XMLSchema}unsignedLong" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="controllo-max-threads-tipo-errore" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="1" maxOccurs="1" default="fault"/&gt;
 * 			&lt;element name="controllo-max-threads-tipo-errore-includi-descrizione" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="1" maxOccurs="1" default="true"/&gt;
 * 			&lt;element name="controllo-congestione-enabled" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="1" maxOccurs="1" default="false"/&gt;
 * 			&lt;element name="controllo-congestione-threshold" type="{http://www.w3.org/2001/XMLSchema}unsignedInt" minOccurs="0" maxOccurs="1"/&gt;
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
@XmlType(name = "configurazione-controllo-traffico", 
  propOrder = {
  	"controlloMaxThreadsEnabled",
  	"controlloMaxThreadsWarningOnly",
  	"controlloMaxThreadsSoglia",
  	"controlloMaxThreadsTipoErrore",
  	"controlloMaxThreadsTipoErroreIncludiDescrizione",
  	"controlloCongestioneEnabled",
  	"controlloCongestioneThreshold"
  }
)

@XmlRootElement(name = "configurazione-controllo-traffico")

public class ConfigurazioneControlloTraffico extends org.openspcoop2.utils.beans.BaseBeanWithId implements Serializable , Cloneable {
  public ConfigurazioneControlloTraffico() {
    super();
  }

  public boolean isControlloMaxThreadsEnabled() {
    return this.controlloMaxThreadsEnabled;
  }

  public boolean getControlloMaxThreadsEnabled() {
    return this.controlloMaxThreadsEnabled;
  }

  public void setControlloMaxThreadsEnabled(boolean controlloMaxThreadsEnabled) {
    this.controlloMaxThreadsEnabled = controlloMaxThreadsEnabled;
  }

  public boolean isControlloMaxThreadsWarningOnly() {
    return this.controlloMaxThreadsWarningOnly;
  }

  public boolean getControlloMaxThreadsWarningOnly() {
    return this.controlloMaxThreadsWarningOnly;
  }

  public void setControlloMaxThreadsWarningOnly(boolean controlloMaxThreadsWarningOnly) {
    this.controlloMaxThreadsWarningOnly = controlloMaxThreadsWarningOnly;
  }

  public java.lang.Long getControlloMaxThreadsSoglia() {
    return this.controlloMaxThreadsSoglia;
  }

  public void setControlloMaxThreadsSoglia(java.lang.Long controlloMaxThreadsSoglia) {
    this.controlloMaxThreadsSoglia = controlloMaxThreadsSoglia;
  }

  public java.lang.String getControlloMaxThreadsTipoErrore() {
    return this.controlloMaxThreadsTipoErrore;
  }

  public void setControlloMaxThreadsTipoErrore(java.lang.String controlloMaxThreadsTipoErrore) {
    this.controlloMaxThreadsTipoErrore = controlloMaxThreadsTipoErrore;
  }

  public boolean isControlloMaxThreadsTipoErroreIncludiDescrizione() {
    return this.controlloMaxThreadsTipoErroreIncludiDescrizione;
  }

  public boolean getControlloMaxThreadsTipoErroreIncludiDescrizione() {
    return this.controlloMaxThreadsTipoErroreIncludiDescrizione;
  }

  public void setControlloMaxThreadsTipoErroreIncludiDescrizione(boolean controlloMaxThreadsTipoErroreIncludiDescrizione) {
    this.controlloMaxThreadsTipoErroreIncludiDescrizione = controlloMaxThreadsTipoErroreIncludiDescrizione;
  }

  public boolean isControlloCongestioneEnabled() {
    return this.controlloCongestioneEnabled;
  }

  public boolean getControlloCongestioneEnabled() {
    return this.controlloCongestioneEnabled;
  }

  public void setControlloCongestioneEnabled(boolean controlloCongestioneEnabled) {
    this.controlloCongestioneEnabled = controlloCongestioneEnabled;
  }

  public java.lang.Integer getControlloCongestioneThreshold() {
    return this.controlloCongestioneThreshold;
  }

  public void setControlloCongestioneThreshold(java.lang.Integer controlloCongestioneThreshold) {
    this.controlloCongestioneThreshold = controlloCongestioneThreshold;
  }

  private static final long serialVersionUID = 1L;



  @javax.xml.bind.annotation.XmlSchemaType(name="boolean")
  @XmlElement(name="controllo-max-threads-enabled",required=true,nillable=false,defaultValue="true")
  protected boolean controlloMaxThreadsEnabled = true;

  @javax.xml.bind.annotation.XmlSchemaType(name="boolean")
  @XmlElement(name="controllo-max-threads-warning-only",required=true,nillable=false,defaultValue="false")
  protected boolean controlloMaxThreadsWarningOnly = false;

  @javax.xml.bind.annotation.XmlSchemaType(name="unsignedLong")
  @XmlElement(name="controllo-max-threads-soglia",required=true,nillable=false)
  protected java.lang.Long controlloMaxThreadsSoglia;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="controllo-max-threads-tipo-errore",required=true,nillable=false,defaultValue="fault")
  protected java.lang.String controlloMaxThreadsTipoErrore = "fault";

  @javax.xml.bind.annotation.XmlSchemaType(name="boolean")
  @XmlElement(name="controllo-max-threads-tipo-errore-includi-descrizione",required=true,nillable=false,defaultValue="true")
  protected boolean controlloMaxThreadsTipoErroreIncludiDescrizione = true;

  @javax.xml.bind.annotation.XmlSchemaType(name="boolean")
  @XmlElement(name="controllo-congestione-enabled",required=true,nillable=false,defaultValue="false")
  protected boolean controlloCongestioneEnabled = false;

  @javax.xml.bind.annotation.XmlSchemaType(name="unsignedInt")
  @XmlElement(name="controllo-congestione-threshold",required=false,nillable=false)
  protected java.lang.Integer controlloCongestioneThreshold;

}
