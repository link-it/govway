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
package org.openspcoop2.core.plugins;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
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
 * 			&lt;element name="configurazione-servizio" type="{http://www.openspcoop2.org/core/plugins}configurazione-servizio" minOccurs="0" maxOccurs="unbounded"/&gt;
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

public class ElencoConfigurazioneServizio extends org.openspcoop2.utils.beans.BaseBeanWithId implements Serializable , Cloneable {
  public ElencoConfigurazioneServizio() {
    super();
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



  @XmlElement(name="configurazione-servizio",required=true,nillable=false)
  private List<ConfigurazioneServizio> configurazioneServizio = new ArrayList<>();

  /**
   * Use method getConfigurazioneServizioList
   * @return List&lt;ConfigurazioneServizio&gt;
  */
  public List<ConfigurazioneServizio> getConfigurazioneServizio() {
  	return this.getConfigurazioneServizioList();
  }

  /**
   * Use method setConfigurazioneServizioList
   * @param configurazioneServizio List&lt;ConfigurazioneServizio&gt;
  */
  public void setConfigurazioneServizio(List<ConfigurazioneServizio> configurazioneServizio) {
  	this.setConfigurazioneServizioList(configurazioneServizio);
  }

  /**
   * Use method sizeConfigurazioneServizioList
   * @return lunghezza della lista
  */
  public int sizeConfigurazioneServizio() {
  	return this.sizeConfigurazioneServizioList();
  }

}
