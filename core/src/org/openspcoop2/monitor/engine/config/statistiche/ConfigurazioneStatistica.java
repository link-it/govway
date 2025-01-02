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
package org.openspcoop2.monitor.engine.config.statistiche;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;


/** <p>Java class for configurazione-statistica complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="configurazione-statistica"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="id-configurazione-statistica" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="id-configurazione-servizio-azione" type="{http://www.openspcoop2.org/monitor/engine/config/statistiche}id-configurazione-servizio-azione" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="enabled" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="plugin" type="{http://www.openspcoop2.org/monitor/engine/config/statistiche}info-plugin" minOccurs="1" maxOccurs="1"/&gt;
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
@XmlType(name = "configurazione-statistica", 
  propOrder = {
  	"idConfigurazioneStatistica",
  	"idConfigurazioneServizioAzione",
  	"enabled",
  	"plugin",
  	"label"
  }
)

@XmlRootElement(name = "configurazione-statistica")

public class ConfigurazioneStatistica extends org.openspcoop2.utils.beans.BaseBeanWithId implements Serializable , Cloneable {
  public ConfigurazioneStatistica() {
    super();
  }

  public java.lang.String getIdConfigurazioneStatistica() {
    return this.idConfigurazioneStatistica;
  }

  public void setIdConfigurazioneStatistica(java.lang.String idConfigurazioneStatistica) {
    this.idConfigurazioneStatistica = idConfigurazioneStatistica;
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

  private static org.openspcoop2.monitor.engine.config.statistiche.model.ConfigurazioneStatisticaModel modelStaticInstance = null;
  private static synchronized void initModelStaticInstance(){
	  if(org.openspcoop2.monitor.engine.config.statistiche.ConfigurazioneStatistica.modelStaticInstance==null){
  			org.openspcoop2.monitor.engine.config.statistiche.ConfigurazioneStatistica.modelStaticInstance = new org.openspcoop2.monitor.engine.config.statistiche.model.ConfigurazioneStatisticaModel();
	  }
  }
  public static org.openspcoop2.monitor.engine.config.statistiche.model.ConfigurazioneStatisticaModel model(){
	  if(org.openspcoop2.monitor.engine.config.statistiche.ConfigurazioneStatistica.modelStaticInstance==null){
	  		initModelStaticInstance();
	  }
	  return org.openspcoop2.monitor.engine.config.statistiche.ConfigurazioneStatistica.modelStaticInstance;
  }


  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="id-configurazione-statistica",required=true,nillable=false)
  protected java.lang.String idConfigurazioneStatistica;

  @XmlElement(name="id-configurazione-servizio-azione",required=true,nillable=false)
  protected IdConfigurazioneServizioAzione idConfigurazioneServizioAzione;

  @javax.xml.bind.annotation.XmlSchemaType(name="boolean")
  @XmlElement(name="enabled",required=true,nillable=false)
  protected boolean enabled;

  @XmlElement(name="plugin",required=true,nillable=false)
  protected InfoPlugin plugin;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="label",required=true,nillable=false)
  protected java.lang.String label;

}
