/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it).
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


/** <p>Java class for plugin complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="plugin"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="tipo-plugin" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="class-name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="tipo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="descrizione" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="label" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="stato" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0" maxOccurs="1" default="true"/&gt;
 * 			&lt;element name="plugin-servizio-compatibilita" type="{http://www.openspcoop2.org/core/plugins}plugin-servizio-compatibilita" minOccurs="0" maxOccurs="unbounded"/&gt;
 * 			&lt;element name="plugin-proprieta-compatibilita" type="{http://www.openspcoop2.org/core/plugins}plugin-proprieta-compatibilita" minOccurs="0" maxOccurs="unbounded"/&gt;
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
@XmlType(name = "plugin", 
  propOrder = {
  	"tipoPlugin",
  	"className",
  	"tipo",
  	"descrizione",
  	"label",
  	"stato",
  	"pluginServizioCompatibilita",
  	"pluginProprietaCompatibilita"
  }
)

@XmlRootElement(name = "plugin")

public class Plugin extends org.openspcoop2.utils.beans.BaseBeanWithId implements Serializable , Cloneable {
  public Plugin() {
    super();
  }

  public IdPlugin getOldIdPlugin() {
    return this.oldIdPlugin;
  }

  public void setOldIdPlugin(IdPlugin oldIdPlugin) {
    this.oldIdPlugin=oldIdPlugin;
  }

  public java.lang.String getTipoPlugin() {
    return this.tipoPlugin;
  }

  public void setTipoPlugin(java.lang.String tipoPlugin) {
    this.tipoPlugin = tipoPlugin;
  }

  public java.lang.String getClassName() {
    return this.className;
  }

  public void setClassName(java.lang.String className) {
    this.className = className;
  }

  public java.lang.String getTipo() {
    return this.tipo;
  }

  public void setTipo(java.lang.String tipo) {
    this.tipo = tipo;
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

  public boolean isStato() {
    return this.stato;
  }

  public boolean getStato() {
    return this.stato;
  }

  public void setStato(boolean stato) {
    this.stato = stato;
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

  public void addPluginProprietaCompatibilita(PluginProprietaCompatibilita pluginProprietaCompatibilita) {
    this.pluginProprietaCompatibilita.add(pluginProprietaCompatibilita);
  }

  public PluginProprietaCompatibilita getPluginProprietaCompatibilita(int index) {
    return this.pluginProprietaCompatibilita.get( index );
  }

  public PluginProprietaCompatibilita removePluginProprietaCompatibilita(int index) {
    return this.pluginProprietaCompatibilita.remove( index );
  }

  public List<PluginProprietaCompatibilita> getPluginProprietaCompatibilitaList() {
    return this.pluginProprietaCompatibilita;
  }

  public void setPluginProprietaCompatibilitaList(List<PluginProprietaCompatibilita> pluginProprietaCompatibilita) {
    this.pluginProprietaCompatibilita=pluginProprietaCompatibilita;
  }

  public int sizePluginProprietaCompatibilitaList() {
    return this.pluginProprietaCompatibilita.size();
  }

  private static final long serialVersionUID = 1L;

  private static org.openspcoop2.core.plugins.model.PluginModel modelStaticInstance = null;
  private static synchronized void initModelStaticInstance(){
	  if(org.openspcoop2.core.plugins.Plugin.modelStaticInstance==null){
  			org.openspcoop2.core.plugins.Plugin.modelStaticInstance = new org.openspcoop2.core.plugins.model.PluginModel();
	  }
  }
  public static org.openspcoop2.core.plugins.model.PluginModel model(){
	  if(org.openspcoop2.core.plugins.Plugin.modelStaticInstance==null){
	  		initModelStaticInstance();
	  }
	  return org.openspcoop2.core.plugins.Plugin.modelStaticInstance;
  }


  @javax.xml.bind.annotation.XmlTransient
  protected IdPlugin oldIdPlugin;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="tipo-plugin",required=true,nillable=false)
  protected java.lang.String tipoPlugin;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="class-name",required=true,nillable=false)
  protected java.lang.String className;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="tipo",required=true,nillable=false)
  protected java.lang.String tipo;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="descrizione",required=false,nillable=false)
  protected java.lang.String descrizione;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="label",required=true,nillable=false)
  protected java.lang.String label;

  @javax.xml.bind.annotation.XmlSchemaType(name="boolean")
  @XmlElement(name="stato",required=false,nillable=false,defaultValue="true")
  protected boolean stato = true;

  @XmlElement(name="plugin-servizio-compatibilita",required=true,nillable=false)
  private List<PluginServizioCompatibilita> pluginServizioCompatibilita = new ArrayList<>();

  /**
   * Use method getPluginServizioCompatibilitaList
   * @return List&lt;PluginServizioCompatibilita&gt;
  */
  public List<PluginServizioCompatibilita> getPluginServizioCompatibilita() {
  	return this.getPluginServizioCompatibilitaList();
  }

  /**
   * Use method setPluginServizioCompatibilitaList
   * @param pluginServizioCompatibilita List&lt;PluginServizioCompatibilita&gt;
  */
  public void setPluginServizioCompatibilita(List<PluginServizioCompatibilita> pluginServizioCompatibilita) {
  	this.setPluginServizioCompatibilitaList(pluginServizioCompatibilita);
  }

  /**
   * Use method sizePluginServizioCompatibilitaList
   * @return lunghezza della lista
  */
  public int sizePluginServizioCompatibilita() {
  	return this.sizePluginServizioCompatibilitaList();
  }

  @XmlElement(name="plugin-proprieta-compatibilita",required=true,nillable=false)
  private List<PluginProprietaCompatibilita> pluginProprietaCompatibilita = new ArrayList<>();

  /**
   * Use method getPluginProprietaCompatibilitaList
   * @return List&lt;PluginProprietaCompatibilita&gt;
  */
  public List<PluginProprietaCompatibilita> getPluginProprietaCompatibilita() {
  	return this.getPluginProprietaCompatibilitaList();
  }

  /**
   * Use method setPluginProprietaCompatibilitaList
   * @param pluginProprietaCompatibilita List&lt;PluginProprietaCompatibilita&gt;
  */
  public void setPluginProprietaCompatibilita(List<PluginProprietaCompatibilita> pluginProprietaCompatibilita) {
  	this.setPluginProprietaCompatibilitaList(pluginProprietaCompatibilita);
  }

  /**
   * Use method sizePluginProprietaCompatibilitaList
   * @return lunghezza della lista
  */
  public int sizePluginProprietaCompatibilita() {
  	return this.sizePluginProprietaCompatibilitaList();
  }

}
