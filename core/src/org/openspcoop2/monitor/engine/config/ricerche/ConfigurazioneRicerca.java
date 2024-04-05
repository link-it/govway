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
package org.openspcoop2.monitor.engine.config.ricerche;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import java.io.Serializable;


/** <p>Java class for configurazione-ricerca complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="configurazione-ricerca"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="id-configurazione-ricerca" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="id-configurazione-servizio-azione" type="{http://www.openspcoop2.org/monitor/engine/config/ricerche}id-configurazione-servizio-azione" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="enabled" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="plugin" type="{http://www.openspcoop2.org/monitor/engine/config/ricerche}info-plugin" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="label" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="1" maxOccurs="1"/&gt;
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
@XmlType(name = "configurazione-ricerca", 
  propOrder = {
  	"idConfigurazioneRicerca",
  	"idConfigurazioneServizioAzione",
  	"enabled",
  	"plugin",
  	"label"
  }
)

@XmlRootElement(name = "configurazione-ricerca")

public class ConfigurazioneRicerca extends org.openspcoop2.utils.beans.BaseBeanWithId implements Serializable , Cloneable {
  public ConfigurazioneRicerca() {
    super();
  }

  public java.lang.String getIdConfigurazioneRicerca() {
    return this.idConfigurazioneRicerca;
  }

  public void setIdConfigurazioneRicerca(java.lang.String idConfigurazioneRicerca) {
    this.idConfigurazioneRicerca = idConfigurazioneRicerca;
  }

  public IdConfigurazioneServizioAzione getIdConfigurazioneServizioAzione() {
    return this.idConfigurazioneServizioAzione;
  }

  public void setIdConfigurazioneServizioAzione(IdConfigurazioneServizioAzione idConfigurazioneServizioAzione) {
    this.idConfigurazioneServizioAzione = idConfigurazioneServizioAzione;
  }

  public boolean isEnabled() {
    return this.enabled;
  }

  public boolean getEnabled() {
    return this.enabled;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  public InfoPlugin getPlugin() {
    return this.plugin;
  }

  public void setPlugin(InfoPlugin plugin) {
    this.plugin = plugin;
  }

  public java.lang.String getLabel() {
    return this.label;
  }

  public void setLabel(java.lang.String label) {
    this.label = label;
  }

  private static final long serialVersionUID = 1L;

  private static org.openspcoop2.monitor.engine.config.ricerche.model.ConfigurazioneRicercaModel modelStaticInstance = null;
  private static synchronized void initModelStaticInstance(){
	  if(org.openspcoop2.monitor.engine.config.ricerche.ConfigurazioneRicerca.modelStaticInstance==null){
  			org.openspcoop2.monitor.engine.config.ricerche.ConfigurazioneRicerca.modelStaticInstance = new org.openspcoop2.monitor.engine.config.ricerche.model.ConfigurazioneRicercaModel();
	  }
  }
  public static org.openspcoop2.monitor.engine.config.ricerche.model.ConfigurazioneRicercaModel model(){
	  if(org.openspcoop2.monitor.engine.config.ricerche.ConfigurazioneRicerca.modelStaticInstance==null){
	  		initModelStaticInstance();
	  }
	  return org.openspcoop2.monitor.engine.config.ricerche.ConfigurazioneRicerca.modelStaticInstance;
  }


  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="id-configurazione-ricerca",required=true,nillable=false)
  protected java.lang.String idConfigurazioneRicerca;

  @XmlElement(name="id-configurazione-servizio-azione",required=true,nillable=false)
  protected IdConfigurazioneServizioAzione idConfigurazioneServizioAzione;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="boolean")
  @XmlElement(name="enabled",required=true,nillable=false)
  protected boolean enabled;

  @XmlElement(name="plugin",required=true,nillable=false)
  protected InfoPlugin plugin;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="label",required=true,nillable=false)
  protected java.lang.String label;

}
