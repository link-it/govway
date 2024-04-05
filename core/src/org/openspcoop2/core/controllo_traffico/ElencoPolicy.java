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
package org.openspcoop2.core.controllo_traffico;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Java class for elenco-policy complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="elenco-policy"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="configurazione-policy" type="{http://www.openspcoop2.org/core/controllo_traffico}configurazione-policy" minOccurs="0" maxOccurs="unbounded"/&gt;
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
@XmlType(name = "elenco-policy", 
  propOrder = {
  	"configurazionePolicy"
  }
)

@XmlRootElement(name = "elenco-policy")

public class ElencoPolicy extends org.openspcoop2.utils.beans.BaseBeanWithId implements Serializable , Cloneable {
  public ElencoPolicy() {
    super();
  }

  public void addConfigurazionePolicy(ConfigurazionePolicy configurazionePolicy) {
    this.configurazionePolicy.add(configurazionePolicy);
  }

  public ConfigurazionePolicy getConfigurazionePolicy(int index) {
    return this.configurazionePolicy.get( index );
  }

  public ConfigurazionePolicy removeConfigurazionePolicy(int index) {
    return this.configurazionePolicy.remove( index );
  }

  public List<ConfigurazionePolicy> getConfigurazionePolicyList() {
    return this.configurazionePolicy;
  }

  public void setConfigurazionePolicyList(List<ConfigurazionePolicy> configurazionePolicy) {
    this.configurazionePolicy=configurazionePolicy;
  }

  public int sizeConfigurazionePolicyList() {
    return this.configurazionePolicy.size();
  }

  private static final long serialVersionUID = 1L;



  @XmlElement(name="configurazione-policy",required=true,nillable=false)
  private List<ConfigurazionePolicy> configurazionePolicy = new ArrayList<>();

  /**
   * Use method getConfigurazionePolicyList
   * @return List&lt;ConfigurazionePolicy&gt;
  */
  public List<ConfigurazionePolicy> getConfigurazionePolicy() {
  	return this.getConfigurazionePolicyList();
  }

  /**
   * Use method setConfigurazionePolicyList
   * @param configurazionePolicy List&lt;ConfigurazionePolicy&gt;
  */
  public void setConfigurazionePolicy(List<ConfigurazionePolicy> configurazionePolicy) {
  	this.setConfigurazionePolicyList(configurazionePolicy);
  }

  /**
   * Use method sizeConfigurazionePolicyList
   * @return lunghezza della lista
  */
  public int sizeConfigurazionePolicy() {
  	return this.sizeConfigurazionePolicyList();
  }

}
