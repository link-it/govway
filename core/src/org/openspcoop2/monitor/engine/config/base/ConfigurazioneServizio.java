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


/** <p>Java class for configurazione-servizio complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="configurazione-servizio"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="accordo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="tipo-soggetto-referente" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="nome-soggetto-referente" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="versione" type="{http://www.w3.org/2001/XMLSchema}unsignedInt" minOccurs="1" maxOccurs="1" default="1"/&gt;
 * 			&lt;element name="servizio" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="configurazione-servizio-azione" type="{http://www.openspcoop2.org/monitor/engine/config/base}id-configurazione-servizio-azione" minOccurs="0" maxOccurs="unbounded"/&gt;
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
  	"accordo",
  	"tipoSoggettoReferente",
  	"nomeSoggettoReferente",
  	"versione",
  	"servizio",
  	"configurazioneServizioAzione"
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

  public java.lang.String getAccordo() {
    return this.accordo;
  }

  public void setAccordo(java.lang.String accordo) {
    this.accordo = accordo;
  }

  public java.lang.String getTipoSoggettoReferente() {
    return this.tipoSoggettoReferente;
  }

  public void setTipoSoggettoReferente(java.lang.String tipoSoggettoReferente) {
    this.tipoSoggettoReferente = tipoSoggettoReferente;
  }

  public java.lang.String getNomeSoggettoReferente() {
    return this.nomeSoggettoReferente;
  }

  public void setNomeSoggettoReferente(java.lang.String nomeSoggettoReferente) {
    this.nomeSoggettoReferente = nomeSoggettoReferente;
  }

  public java.lang.Integer getVersione() {
    return this.versione;
  }

  public void setVersione(java.lang.Integer versione) {
    this.versione = versione;
  }

  public java.lang.String getServizio() {
    return this.servizio;
  }

  public void setServizio(java.lang.String servizio) {
    this.servizio = servizio;
  }

  public void addConfigurazioneServizioAzione(IdConfigurazioneServizioAzione configurazioneServizioAzione) {
    this.configurazioneServizioAzione.add(configurazioneServizioAzione);
  }

  public IdConfigurazioneServizioAzione getConfigurazioneServizioAzione(int index) {
    return this.configurazioneServizioAzione.get( index );
  }

  public IdConfigurazioneServizioAzione removeConfigurazioneServizioAzione(int index) {
    return this.configurazioneServizioAzione.remove( index );
  }

  public List<IdConfigurazioneServizioAzione> getConfigurazioneServizioAzioneList() {
    return this.configurazioneServizioAzione;
  }

  public void setConfigurazioneServizioAzioneList(List<IdConfigurazioneServizioAzione> configurazioneServizioAzione) {
    this.configurazioneServizioAzione=configurazioneServizioAzione;
  }

  public int sizeConfigurazioneServizioAzioneList() {
    return this.configurazioneServizioAzione.size();
  }

  private static final long serialVersionUID = 1L;

  @XmlTransient
  private Long id;

  private static org.openspcoop2.monitor.engine.config.base.model.ConfigurazioneServizioModel modelStaticInstance = null;
  private static synchronized void initModelStaticInstance(){
	  if(org.openspcoop2.monitor.engine.config.base.ConfigurazioneServizio.modelStaticInstance==null){
  			org.openspcoop2.monitor.engine.config.base.ConfigurazioneServizio.modelStaticInstance = new org.openspcoop2.monitor.engine.config.base.model.ConfigurazioneServizioModel();
	  }
  }
  public static org.openspcoop2.monitor.engine.config.base.model.ConfigurazioneServizioModel model(){
	  if(org.openspcoop2.monitor.engine.config.base.ConfigurazioneServizio.modelStaticInstance==null){
	  		initModelStaticInstance();
	  }
	  return org.openspcoop2.monitor.engine.config.base.ConfigurazioneServizio.modelStaticInstance;
  }


  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="accordo",required=true,nillable=false)
  protected java.lang.String accordo;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="tipo-soggetto-referente",required=true,nillable=false)
  protected java.lang.String tipoSoggettoReferente;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="nome-soggetto-referente",required=true,nillable=false)
  protected java.lang.String nomeSoggettoReferente;

  @javax.xml.bind.annotation.XmlSchemaType(name="unsignedInt")
  @XmlElement(name="versione",required=true,nillable=false,defaultValue="1")
  protected java.lang.Integer versione = java.lang.Integer.valueOf("1");

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="servizio",required=false,nillable=false)
  protected java.lang.String servizio;

  @XmlElement(name="configurazione-servizio-azione",required=true,nillable=false)
  protected List<IdConfigurazioneServizioAzione> configurazioneServizioAzione = new ArrayList<IdConfigurazioneServizioAzione>();

  /**
   * @deprecated Use method getConfigurazioneServizioAzioneList
   * @return List&lt;IdConfigurazioneServizioAzione&gt;
  */
  @Deprecated
  public List<IdConfigurazioneServizioAzione> getConfigurazioneServizioAzione() {
  	return this.configurazioneServizioAzione;
  }

  /**
   * @deprecated Use method setConfigurazioneServizioAzioneList
   * @param configurazioneServizioAzione List&lt;IdConfigurazioneServizioAzione&gt;
  */
  @Deprecated
  public void setConfigurazioneServizioAzione(List<IdConfigurazioneServizioAzione> configurazioneServizioAzione) {
  	this.configurazioneServizioAzione=configurazioneServizioAzione;
  }

  /**
   * @deprecated Use method sizeConfigurazioneServizioAzioneList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizeConfigurazioneServizioAzione() {
  	return this.configurazioneServizioAzione.size();
  }

}
