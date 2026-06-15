/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2026 Link.it srl (https://link.it).
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
package org.openspcoop2.core.config;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Java class for connettore-llm complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="connettore-llm"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="provider" type="{http://www.openspcoop2.org/core/config}connettore-llm-provider-ref" minOccurs="0" maxOccurs="unbounded"/&gt;
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
@XmlType(name = "connettore-llm", 
  propOrder = {
  	"provider"
  }
)

@XmlRootElement(name = "connettore-llm")

public class ConnettoreLlm extends org.openspcoop2.utils.beans.BaseBeanWithId implements Serializable , Cloneable {
  public ConnettoreLlm() {
    super();
  }

  public void addProvider(ConnettoreLlmProviderRef provider) {
    this.provider.add(provider);
  }

  public ConnettoreLlmProviderRef getProvider(int index) {
    return this.provider.get( index );
  }

  public ConnettoreLlmProviderRef removeProvider(int index) {
    return this.provider.remove( index );
  }

  public List<ConnettoreLlmProviderRef> getProviderList() {
    return this.provider;
  }

  public void setProviderList(List<ConnettoreLlmProviderRef> provider) {
    this.provider=provider;
  }

  public int sizeProviderList() {
    return this.provider.size();
  }

  private static final long serialVersionUID = 1L;



  @XmlElement(name="provider",required=true,nillable=false)
  private List<ConnettoreLlmProviderRef> provider = new ArrayList<>();

  /**
   * Use method getProviderList
   * @return List&lt;ConnettoreLlmProviderRef&gt;
  */
  public List<ConnettoreLlmProviderRef> getProvider() {
  	return this.getProviderList();
  }

  /**
   * Use method setProviderList
   * @param provider List&lt;ConnettoreLlmProviderRef&gt;
  */
  public void setProvider(List<ConnettoreLlmProviderRef> provider) {
  	this.setProviderList(provider);
  }

  /**
   * Use method sizeProviderList
   * @return lunghezza della lista
  */
  public int sizeProvider() {
  	return this.sizeProviderList();
  }

}
