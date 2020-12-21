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


/** <p>Java class for elenco-id-plugin complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="elenco-id-plugin"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="id-plugin" type="{http://www.openspcoop2.org/monitor/engine/config/base}id-plugin" minOccurs="0" maxOccurs="unbounded"/&gt;
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
@XmlType(name = "elenco-id-plugin", 
  propOrder = {
  	"idPlugin"
  }
)

@XmlRootElement(name = "elenco-id-plugin")

public class ElencoIdPlugin extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public ElencoIdPlugin() {
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

  public void addIdPlugin(IdPlugin idPlugin) {
    this.idPlugin.add(idPlugin);
  }

  public IdPlugin getIdPlugin(int index) {
    return this.idPlugin.get( index );
  }

  public IdPlugin removeIdPlugin(int index) {
    return this.idPlugin.remove( index );
  }

  public List<IdPlugin> getIdPluginList() {
    return this.idPlugin;
  }

  public void setIdPluginList(List<IdPlugin> idPlugin) {
    this.idPlugin=idPlugin;
  }

  public int sizeIdPluginList() {
    return this.idPlugin.size();
  }

  private static final long serialVersionUID = 1L;

  @XmlTransient
  private Long id;



  @XmlElement(name="id-plugin",required=true,nillable=false)
  protected List<IdPlugin> idPlugin = new ArrayList<IdPlugin>();

  /**
   * @deprecated Use method getIdPluginList
   * @return List&lt;IdPlugin&gt;
  */
  @Deprecated
  public List<IdPlugin> getIdPlugin() {
  	return this.idPlugin;
  }

  /**
   * @deprecated Use method setIdPluginList
   * @param idPlugin List&lt;IdPlugin&gt;
  */
  @Deprecated
  public void setIdPlugin(List<IdPlugin> idPlugin) {
  	this.idPlugin=idPlugin;
  }

  /**
   * @deprecated Use method sizeIdPluginList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizeIdPlugin() {
  	return this.idPlugin.size();
  }

}
