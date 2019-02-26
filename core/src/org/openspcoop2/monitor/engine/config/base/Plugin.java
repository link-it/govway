/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it).
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
import org.openspcoop2.monitor.engine.config.base.constants.TipoPlugin;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Java class for plugin complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="plugin">
 * 		&lt;sequence>
 * 			&lt;element name="tipo" type="{http://www.openspcoop2.org/monitor/engine/config/base}tipo-plugin" minOccurs="1" maxOccurs="1"/>
 * 			&lt;element name="class-name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="1" maxOccurs="1"/>
 * 			&lt;element name="descrizione" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="label" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="1" maxOccurs="1"/>
 * 			&lt;element name="plugin-servizio-compatibilita" type="{http://www.openspcoop2.org/monitor/engine/config/base}plugin-servizio-compatibilita" minOccurs="0" maxOccurs="unbounded"/>
 * 			&lt;element name="plugin-filtro-compatibilita" type="{http://www.openspcoop2.org/monitor/engine/config/base}plugin-filtro-compatibilita" minOccurs="0" maxOccurs="unbounded"/>
 * 		&lt;/sequence>
 * &lt;/complexType>
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "plugin", 
  propOrder = {
  	"tipo",
  	"className",
  	"descrizione",
  	"label",
  	"pluginServizioCompatibilita",
  	"pluginFiltroCompatibilita"
  }
)

@XmlRootElement(name = "plugin")

public class Plugin extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public Plugin() {
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

  public void set_value_tipo(String value) {
    this.tipo = (TipoPlugin) TipoPlugin.toEnumConstantFromString(value);
  }

  public String get_value_tipo() {
    if(this.tipo == null){
    	return null;
    }else{
    	return this.tipo.toString();
    }
  }

  public org.openspcoop2.monitor.engine.config.base.constants.TipoPlugin getTipo() {
    return this.tipo;
  }

  public void setTipo(org.openspcoop2.monitor.engine.config.base.constants.TipoPlugin tipo) {
    this.tipo = tipo;
  }

  public java.lang.String getClassName() {
    return this.className;
  }

  public void setClassName(java.lang.String className) {
    this.className = className;
  }

  public java.lang.String getDescrizione() {
    return this.descrizione;
  }

  public void setDescrizione(java.lang.String descrizione) {
    this.descrizione = descrizione;
  }

  public java.lang.String getLabel() {
    return this.label;
  }

  public void setLabel(java.lang.String label) {
    this.label = label;
  }

  public void addPluginServizioCompatibilita(PluginServizioCompatibilita pluginServizioCompatibilita) {
    this.pluginServizioCompatibilita.add(pluginServizioCompatibilita);
  }

  public PluginServizioCompatibilita getPluginServizioCompatibilita(int index) {
    return this.pluginServizioCompatibilita.get( index );
  }

  public PluginServizioCompatibilita removePluginServizioCompatibilita(int index) {
    return this.pluginServizioCompatibilita.remove( index );
  }

  public List<PluginServizioCompatibilita> getPluginServizioCompatibilitaList() {
    return this.pluginServizioCompatibilita;
  }

  public void setPluginServizioCompatibilitaList(List<PluginServizioCompatibilita> pluginServizioCompatibilita) {
    this.pluginServizioCompatibilita=pluginServizioCompatibilita;
  }

  public int sizePluginServizioCompatibilitaList() {
    return this.pluginServizioCompatibilita.size();
  }

  public void addPluginFiltroCompatibilita(PluginFiltroCompatibilita pluginFiltroCompatibilita) {
    this.pluginFiltroCompatibilita.add(pluginFiltroCompatibilita);
  }

  public PluginFiltroCompatibilita getPluginFiltroCompatibilita(int index) {
    return this.pluginFiltroCompatibilita.get( index );
  }

  public PluginFiltroCompatibilita removePluginFiltroCompatibilita(int index) {
    return this.pluginFiltroCompatibilita.remove( index );
  }

  public List<PluginFiltroCompatibilita> getPluginFiltroCompatibilitaList() {
    return this.pluginFiltroCompatibilita;
  }

  public void setPluginFiltroCompatibilitaList(List<PluginFiltroCompatibilita> pluginFiltroCompatibilita) {
    this.pluginFiltroCompatibilita=pluginFiltroCompatibilita;
  }

  public int sizePluginFiltroCompatibilitaList() {
    return this.pluginFiltroCompatibilita.size();
  }

  private static final long serialVersionUID = 1L;

  @XmlTransient
  private Long id;

  private static org.openspcoop2.monitor.engine.config.base.model.PluginModel modelStaticInstance = null;
  private static synchronized void initModelStaticInstance(){
	  if(org.openspcoop2.monitor.engine.config.base.Plugin.modelStaticInstance==null){
  			org.openspcoop2.monitor.engine.config.base.Plugin.modelStaticInstance = new org.openspcoop2.monitor.engine.config.base.model.PluginModel();
	  }
  }
  public static org.openspcoop2.monitor.engine.config.base.model.PluginModel model(){
	  if(org.openspcoop2.monitor.engine.config.base.Plugin.modelStaticInstance==null){
	  		initModelStaticInstance();
	  }
	  return org.openspcoop2.monitor.engine.config.base.Plugin.modelStaticInstance;
  }


  @javax.xml.bind.annotation.XmlTransient
  protected java.lang.String _value_tipo;

  @XmlElement(name="tipo",required=true,nillable=false)
  protected TipoPlugin tipo;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="class-name",required=true,nillable=false)
  protected java.lang.String className;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="descrizione",required=false,nillable=false)
  protected java.lang.String descrizione;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="label",required=true,nillable=false)
  protected java.lang.String label;

  @XmlElement(name="plugin-servizio-compatibilita",required=true,nillable=false)
  protected List<PluginServizioCompatibilita> pluginServizioCompatibilita = new ArrayList<PluginServizioCompatibilita>();

  /**
   * @deprecated Use method getPluginServizioCompatibilitaList
   * @return List<PluginServizioCompatibilita>
  */
  @Deprecated
  public List<PluginServizioCompatibilita> getPluginServizioCompatibilita() {
  	return this.pluginServizioCompatibilita;
  }

  /**
   * @deprecated Use method setPluginServizioCompatibilitaList
   * @param pluginServizioCompatibilita List<PluginServizioCompatibilita>
  */
  @Deprecated
  public void setPluginServizioCompatibilita(List<PluginServizioCompatibilita> pluginServizioCompatibilita) {
  	this.pluginServizioCompatibilita=pluginServizioCompatibilita;
  }

  /**
   * @deprecated Use method sizePluginServizioCompatibilitaList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizePluginServizioCompatibilita() {
  	return this.pluginServizioCompatibilita.size();
  }

  @XmlElement(name="plugin-filtro-compatibilita",required=true,nillable=false)
  protected List<PluginFiltroCompatibilita> pluginFiltroCompatibilita = new ArrayList<PluginFiltroCompatibilita>();

  /**
   * @deprecated Use method getPluginFiltroCompatibilitaList
   * @return List<PluginFiltroCompatibilita>
  */
  @Deprecated
  public List<PluginFiltroCompatibilita> getPluginFiltroCompatibilita() {
  	return this.pluginFiltroCompatibilita;
  }

  /**
   * @deprecated Use method setPluginFiltroCompatibilitaList
   * @param pluginFiltroCompatibilita List<PluginFiltroCompatibilita>
  */
  @Deprecated
  public void setPluginFiltroCompatibilita(List<PluginFiltroCompatibilita> pluginFiltroCompatibilita) {
  	this.pluginFiltroCompatibilita=pluginFiltroCompatibilita;
  }

  /**
   * @deprecated Use method sizePluginFiltroCompatibilitaList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizePluginFiltroCompatibilita() {
  	return this.pluginFiltroCompatibilita.size();
  }

}
