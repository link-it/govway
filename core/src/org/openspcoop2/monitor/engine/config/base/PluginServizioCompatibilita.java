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


/** <p>Java class for plugin-servizio-compatibilita complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="plugin-servizio-compatibilita"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="uri-accordo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="servizio" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="plugin-servizio-azione-compatibilita" type="{http://www.openspcoop2.org/monitor/engine/config/base}plugin-servizio-azione-compatibilita" minOccurs="0" maxOccurs="unbounded"/&gt;
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
@XmlType(name = "plugin-servizio-compatibilita", 
  propOrder = {
  	"uriAccordo",
  	"servizio",
  	"pluginServizioAzioneCompatibilita"
  }
)

@XmlRootElement(name = "plugin-servizio-compatibilita")

public class PluginServizioCompatibilita extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public PluginServizioCompatibilita() {
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

  public java.lang.String getUriAccordo() {
    return this.uriAccordo;
  }

  public void setUriAccordo(java.lang.String uriAccordo) {
    this.uriAccordo = uriAccordo;
  }

  public java.lang.String getServizio() {
    return this.servizio;
  }

  public void setServizio(java.lang.String servizio) {
    this.servizio = servizio;
  }

  public void addPluginServizioAzioneCompatibilita(PluginServizioAzioneCompatibilita pluginServizioAzioneCompatibilita) {
    this.pluginServizioAzioneCompatibilita.add(pluginServizioAzioneCompatibilita);
  }

  public PluginServizioAzioneCompatibilita getPluginServizioAzioneCompatibilita(int index) {
    return this.pluginServizioAzioneCompatibilita.get( index );
  }

  public PluginServizioAzioneCompatibilita removePluginServizioAzioneCompatibilita(int index) {
    return this.pluginServizioAzioneCompatibilita.remove( index );
  }

  public List<PluginServizioAzioneCompatibilita> getPluginServizioAzioneCompatibilitaList() {
    return this.pluginServizioAzioneCompatibilita;
  }

  public void setPluginServizioAzioneCompatibilitaList(List<PluginServizioAzioneCompatibilita> pluginServizioAzioneCompatibilita) {
    this.pluginServizioAzioneCompatibilita=pluginServizioAzioneCompatibilita;
  }

  public int sizePluginServizioAzioneCompatibilitaList() {
    return this.pluginServizioAzioneCompatibilita.size();
  }

  private static final long serialVersionUID = 1L;

  @XmlTransient
  private Long id;



  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="uri-accordo",required=false,nillable=false)
  protected java.lang.String uriAccordo;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="servizio",required=false,nillable=false)
  protected java.lang.String servizio;

  @XmlElement(name="plugin-servizio-azione-compatibilita",required=true,nillable=false)
  protected List<PluginServizioAzioneCompatibilita> pluginServizioAzioneCompatibilita = new ArrayList<PluginServizioAzioneCompatibilita>();

  /**
   * @deprecated Use method getPluginServizioAzioneCompatibilitaList
   * @return List&lt;PluginServizioAzioneCompatibilita&gt;
  */
  @Deprecated
  public List<PluginServizioAzioneCompatibilita> getPluginServizioAzioneCompatibilita() {
  	return this.pluginServizioAzioneCompatibilita;
  }

  /**
   * @deprecated Use method setPluginServizioAzioneCompatibilitaList
   * @param pluginServizioAzioneCompatibilita List&lt;PluginServizioAzioneCompatibilita&gt;
  */
  @Deprecated
  public void setPluginServizioAzioneCompatibilita(List<PluginServizioAzioneCompatibilita> pluginServizioAzioneCompatibilita) {
  	this.pluginServizioAzioneCompatibilita=pluginServizioAzioneCompatibilita;
  }

  /**
   * @deprecated Use method sizePluginServizioAzioneCompatibilitaList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizePluginServizioAzioneCompatibilita() {
  	return this.pluginServizioAzioneCompatibilita.size();
  }

}
