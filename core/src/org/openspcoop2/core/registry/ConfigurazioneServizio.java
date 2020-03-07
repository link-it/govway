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
package org.openspcoop2.core.registry;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Java class for configurazione-servizio complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="configurazione-servizio"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="connettore" type="{http://www.openspcoop2.org/core/registry}connettore" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="configurazione-azione" type="{http://www.openspcoop2.org/core/registry}configurazione-servizio-azione" minOccurs="0" maxOccurs="unbounded"/&gt;
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
@XmlType(name = "configurazione-servizio", 
  propOrder = {
  	"connettore",
  	"configurazioneAzione"
  }
)

@XmlRootElement(name = "configurazione-servizio")

public class ConfigurazioneServizio extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public ConfigurazioneServizio() {
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

  public Connettore getConnettore() {
    return this.connettore;
  }

  public void setConnettore(Connettore connettore) {
    this.connettore = connettore;
  }

  public void addConfigurazioneAzione(ConfigurazioneServizioAzione configurazioneAzione) {
    this.configurazioneAzione.add(configurazioneAzione);
  }

  public ConfigurazioneServizioAzione getConfigurazioneAzione(int index) {
    return this.configurazioneAzione.get( index );
  }

  public ConfigurazioneServizioAzione removeConfigurazioneAzione(int index) {
    return this.configurazioneAzione.remove( index );
  }

  public List<ConfigurazioneServizioAzione> getConfigurazioneAzioneList() {
    return this.configurazioneAzione;
  }

  public void setConfigurazioneAzioneList(List<ConfigurazioneServizioAzione> configurazioneAzione) {
    this.configurazioneAzione=configurazioneAzione;
  }

  public int sizeConfigurazioneAzioneList() {
    return this.configurazioneAzione.size();
  }

  private static final long serialVersionUID = 1L;

  @XmlTransient
  private Long id;



  @XmlElement(name="connettore",required=false,nillable=false)
  protected Connettore connettore;

  @XmlElement(name="configurazione-azione",required=true,nillable=false)
  protected List<ConfigurazioneServizioAzione> configurazioneAzione = new ArrayList<ConfigurazioneServizioAzione>();

  /**
   * @deprecated Use method getConfigurazioneAzioneList
   * @return List&lt;ConfigurazioneServizioAzione&gt;
  */
  @Deprecated
  public List<ConfigurazioneServizioAzione> getConfigurazioneAzione() {
  	return this.configurazioneAzione;
  }

  /**
   * @deprecated Use method setConfigurazioneAzioneList
   * @param configurazioneAzione List&lt;ConfigurazioneServizioAzione&gt;
  */
  @Deprecated
  public void setConfigurazioneAzione(List<ConfigurazioneServizioAzione> configurazioneAzione) {
  	this.configurazioneAzione=configurazioneAzione;
  }

  /**
   * @deprecated Use method sizeConfigurazioneAzioneList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizeConfigurazioneAzione() {
  	return this.configurazioneAzione.size();
  }

}
