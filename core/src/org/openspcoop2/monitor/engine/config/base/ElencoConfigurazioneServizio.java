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
package org.openspcoop2.monitor.engine.config.base;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Java class for elenco-configurazione-servizio complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="elenco-configurazione-servizio"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="configurazione-servizio" type="{http://www.openspcoop2.org/monitor/engine/config/base}configurazione-servizio" minOccurs="0" maxOccurs="unbounded"/&gt;
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
@XmlType(name = "elenco-configurazione-servizio", 
  propOrder = {
  	"configurazioneServizio"
  }
)

@XmlRootElement(name = "elenco-configurazione-servizio")

public class ElencoConfigurazioneServizio extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public ElencoConfigurazioneServizio() {
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

  public void addConfigurazioneServizio(ConfigurazioneServizio configurazioneServizio) {
    this.configurazioneServizio.add(configurazioneServizio);
  }

  public ConfigurazioneServizio getConfigurazioneServizio(int index) {
    return this.configurazioneServizio.get( index );
  }

  public ConfigurazioneServizio removeConfigurazioneServizio(int index) {
    return this.configurazioneServizio.remove( index );
  }

  public List<ConfigurazioneServizio> getConfigurazioneServizioList() {
    return this.configurazioneServizio;
  }

  public void setConfigurazioneServizioList(List<ConfigurazioneServizio> configurazioneServizio) {
    this.configurazioneServizio=configurazioneServizio;
  }

  public int sizeConfigurazioneServizioList() {
    return this.configurazioneServizio.size();
  }

  private static final long serialVersionUID = 1L;

  @XmlTransient
  private Long id;



  @XmlElement(name="configurazione-servizio",required=true,nillable=false)
  protected List<ConfigurazioneServizio> configurazioneServizio = new ArrayList<ConfigurazioneServizio>();

  /**
   * @deprecated Use method getConfigurazioneServizioList
   * @return List&lt;ConfigurazioneServizio&gt;
  */
  @Deprecated
  public List<ConfigurazioneServizio> getConfigurazioneServizio() {
  	return this.configurazioneServizio;
  }

  /**
   * @deprecated Use method setConfigurazioneServizioList
   * @param configurazioneServizio List&lt;ConfigurazioneServizio&gt;
  */
  @Deprecated
  public void setConfigurazioneServizio(List<ConfigurazioneServizio> configurazioneServizio) {
  	this.configurazioneServizio=configurazioneServizio;
  }

  /**
   * @deprecated Use method sizeConfigurazioneServizioList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizeConfigurazioneServizio() {
  	return this.configurazioneServizio.size();
  }

}
