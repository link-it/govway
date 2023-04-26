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
package org.openspcoop2.monitor.engine.config.transazioni;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Java class for configurazione-transazione complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="configurazione-transazione"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="id-configurazione-servizio-azione" type="{http://www.openspcoop2.org/monitor/engine/config/transazioni}id-configurazione-servizio-azione" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="enabled" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="configurazione-transazione-plugin" type="{http://www.openspcoop2.org/monitor/engine/config/transazioni}configurazione-transazione-plugin" minOccurs="0" maxOccurs="unbounded"/&gt;
 * 			&lt;element name="configurazione-transazione-stato" type="{http://www.openspcoop2.org/monitor/engine/config/transazioni}configurazione-transazione-stato" minOccurs="0" maxOccurs="unbounded"/&gt;
 * 			&lt;element name="configurazione-transazione-risorsa-contenuto" type="{http://www.openspcoop2.org/monitor/engine/config/transazioni}configurazione-transazione-risorsa-contenuto" minOccurs="0" maxOccurs="unbounded"/&gt;
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
@XmlType(name = "configurazione-transazione", 
  propOrder = {
  	"idConfigurazioneServizioAzione",
  	"enabled",
  	"configurazioneTransazionePlugin",
  	"configurazioneTransazioneStato",
  	"configurazioneTransazioneRisorsaContenuto"
  }
)

@XmlRootElement(name = "configurazione-transazione")

public class ConfigurazioneTransazione extends org.openspcoop2.utils.beans.BaseBeanWithId implements Serializable , Cloneable {
  public ConfigurazioneTransazione() {
    super();
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

  public void addConfigurazioneTransazionePlugin(ConfigurazioneTransazionePlugin configurazioneTransazionePlugin) {
    this.configurazioneTransazionePlugin.add(configurazioneTransazionePlugin);
  }

  public ConfigurazioneTransazionePlugin getConfigurazioneTransazionePlugin(int index) {
    return this.configurazioneTransazionePlugin.get( index );
  }

  public ConfigurazioneTransazionePlugin removeConfigurazioneTransazionePlugin(int index) {
    return this.configurazioneTransazionePlugin.remove( index );
  }

  public List<ConfigurazioneTransazionePlugin> getConfigurazioneTransazionePluginList() {
    return this.configurazioneTransazionePlugin;
  }

  public void setConfigurazioneTransazionePluginList(List<ConfigurazioneTransazionePlugin> configurazioneTransazionePlugin) {
    this.configurazioneTransazionePlugin=configurazioneTransazionePlugin;
  }

  public int sizeConfigurazioneTransazionePluginList() {
    return this.configurazioneTransazionePlugin.size();
  }

  public void addConfigurazioneTransazioneStato(ConfigurazioneTransazioneStato configurazioneTransazioneStato) {
    this.configurazioneTransazioneStato.add(configurazioneTransazioneStato);
  }

  public ConfigurazioneTransazioneStato getConfigurazioneTransazioneStato(int index) {
    return this.configurazioneTransazioneStato.get( index );
  }

  public ConfigurazioneTransazioneStato removeConfigurazioneTransazioneStato(int index) {
    return this.configurazioneTransazioneStato.remove( index );
  }

  public List<ConfigurazioneTransazioneStato> getConfigurazioneTransazioneStatoList() {
    return this.configurazioneTransazioneStato;
  }

  public void setConfigurazioneTransazioneStatoList(List<ConfigurazioneTransazioneStato> configurazioneTransazioneStato) {
    this.configurazioneTransazioneStato=configurazioneTransazioneStato;
  }

  public int sizeConfigurazioneTransazioneStatoList() {
    return this.configurazioneTransazioneStato.size();
  }

  public void addConfigurazioneTransazioneRisorsaContenuto(ConfigurazioneTransazioneRisorsaContenuto configurazioneTransazioneRisorsaContenuto) {
    this.configurazioneTransazioneRisorsaContenuto.add(configurazioneTransazioneRisorsaContenuto);
  }

  public ConfigurazioneTransazioneRisorsaContenuto getConfigurazioneTransazioneRisorsaContenuto(int index) {
    return this.configurazioneTransazioneRisorsaContenuto.get( index );
  }

  public ConfigurazioneTransazioneRisorsaContenuto removeConfigurazioneTransazioneRisorsaContenuto(int index) {
    return this.configurazioneTransazioneRisorsaContenuto.remove( index );
  }

  public List<ConfigurazioneTransazioneRisorsaContenuto> getConfigurazioneTransazioneRisorsaContenutoList() {
    return this.configurazioneTransazioneRisorsaContenuto;
  }

  public void setConfigurazioneTransazioneRisorsaContenutoList(List<ConfigurazioneTransazioneRisorsaContenuto> configurazioneTransazioneRisorsaContenuto) {
    this.configurazioneTransazioneRisorsaContenuto=configurazioneTransazioneRisorsaContenuto;
  }

  public int sizeConfigurazioneTransazioneRisorsaContenutoList() {
    return this.configurazioneTransazioneRisorsaContenuto.size();
  }

  private static final long serialVersionUID = 1L;

  private static org.openspcoop2.monitor.engine.config.transazioni.model.ConfigurazioneTransazioneModel modelStaticInstance = null;
  private static synchronized void initModelStaticInstance(){
	  if(org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneTransazione.modelStaticInstance==null){
  			org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneTransazione.modelStaticInstance = new org.openspcoop2.monitor.engine.config.transazioni.model.ConfigurazioneTransazioneModel();
	  }
  }
  public static org.openspcoop2.monitor.engine.config.transazioni.model.ConfigurazioneTransazioneModel model(){
	  if(org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneTransazione.modelStaticInstance==null){
	  		initModelStaticInstance();
	  }
	  return org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneTransazione.modelStaticInstance;
  }


  @XmlElement(name="id-configurazione-servizio-azione",required=true,nillable=false)
  protected IdConfigurazioneServizioAzione idConfigurazioneServizioAzione;

  @javax.xml.bind.annotation.XmlSchemaType(name="boolean")
  @XmlElement(name="enabled",required=true,nillable=false)
  protected boolean enabled;

  @XmlElement(name="configurazione-transazione-plugin",required=true,nillable=false)
  private List<ConfigurazioneTransazionePlugin> configurazioneTransazionePlugin = new ArrayList<>();

  @XmlElement(name="configurazione-transazione-stato",required=true,nillable=false)
  private List<ConfigurazioneTransazioneStato> configurazioneTransazioneStato = new ArrayList<>();

  @XmlElement(name="configurazione-transazione-risorsa-contenuto",required=true,nillable=false)
  private List<ConfigurazioneTransazioneRisorsaContenuto> configurazioneTransazioneRisorsaContenuto = new ArrayList<>();

}
