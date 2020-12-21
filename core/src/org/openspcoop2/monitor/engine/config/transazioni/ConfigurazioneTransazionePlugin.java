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
package org.openspcoop2.monitor.engine.config.transazioni;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;


/** <p>Java class for configurazione-transazione-plugin complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="configurazione-transazione-plugin"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="id-configurazione-transazione-plugin" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="enabled" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="plugin" type="{http://www.openspcoop2.org/monitor/engine/config/transazioni}info-plugin" minOccurs="1" maxOccurs="1"/&gt;
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
@XmlType(name = "configurazione-transazione-plugin", 
  propOrder = {
  	"idConfigurazioneTransazionePlugin",
  	"enabled",
  	"plugin"
  }
)

@XmlRootElement(name = "configurazione-transazione-plugin")

public class ConfigurazioneTransazionePlugin extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public ConfigurazioneTransazionePlugin() {
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

  public java.lang.String getIdConfigurazioneTransazionePlugin() {
    return this.idConfigurazioneTransazionePlugin;
  }

  public void setIdConfigurazioneTransazionePlugin(java.lang.String idConfigurazioneTransazionePlugin) {
    this.idConfigurazioneTransazionePlugin = idConfigurazioneTransazionePlugin;
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

  private static final long serialVersionUID = 1L;

  @XmlTransient
  private Long id;



  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="id-configurazione-transazione-plugin",required=true,nillable=false)
  protected java.lang.String idConfigurazioneTransazionePlugin;

  @javax.xml.bind.annotation.XmlSchemaType(name="boolean")
  @XmlElement(name="enabled",required=true,nillable=false)
  protected boolean enabled;

  @XmlElement(name="plugin",required=true,nillable=false)
  protected InfoPlugin plugin;

}
