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
package org.openspcoop2.core.allarmi;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import org.openspcoop2.core.allarmi.constants.TipoAllarme;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Java class for allarme complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="allarme"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="nome" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="tipo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="tipo-allarme" type="{http://www.openspcoop2.org/core/allarmi}tipo-allarme" minOccurs="1" maxOccurs="1" default="ATTIVO"/&gt;
 * 			&lt;element name="mail" type="{http://www.openspcoop2.org/core/allarmi}allarme-mail" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="script" type="{http://www.openspcoop2.org/core/allarmi}allarme-script" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="stato-precedente" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="stato" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="dettaglio-stato" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="lasttimestamp-create" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="lasttimestamp-update" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="enabled" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="acknowledged" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="tipo-periodo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="periodo" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="filtro" type="{http://www.openspcoop2.org/core/allarmi}allarme-filtro" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="group-by" type="{http://www.openspcoop2.org/core/allarmi}allarme-raggruppamento" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="allarme-parametro" type="{http://www.openspcoop2.org/core/allarmi}allarme-parametro" minOccurs="0" maxOccurs="unbounded"/&gt;
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
@XmlType(name = "allarme", 
  propOrder = {
  	"nome",
  	"tipo",
  	"tipoAllarme",
  	"mail",
  	"script",
  	"statoPrecedente",
  	"stato",
  	"dettaglioStato",
  	"lasttimestampCreate",
  	"lasttimestampUpdate",
  	"enabled",
  	"acknowledged",
  	"tipoPeriodo",
  	"periodo",
  	"filtro",
  	"groupBy",
  	"allarmeParametro"
  }
)

@XmlRootElement(name = "allarme")

public class Allarme extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public Allarme() {
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

  public java.lang.String getNome() {
    return this.nome;
  }

  public void setNome(java.lang.String nome) {
    this.nome = nome;
  }

  public java.lang.String getTipo() {
    return this.tipo;
  }

  public void setTipo(java.lang.String tipo) {
    this.tipo = tipo;
  }

  public void set_value_tipoAllarme(String value) {
    this.tipoAllarme = (TipoAllarme) TipoAllarme.toEnumConstantFromString(value);
  }

  public String get_value_tipoAllarme() {
    if(this.tipoAllarme == null){
    	return null;
    }else{
    	return this.tipoAllarme.toString();
    }
  }

  public org.openspcoop2.core.allarmi.constants.TipoAllarme getTipoAllarme() {
    return this.tipoAllarme;
  }

  public void setTipoAllarme(org.openspcoop2.core.allarmi.constants.TipoAllarme tipoAllarme) {
    this.tipoAllarme = tipoAllarme;
  }

  public AllarmeMail getMail() {
    return this.mail;
  }

  public void setMail(AllarmeMail mail) {
    this.mail = mail;
  }

  public AllarmeScript getScript() {
    return this.script;
  }

  public void setScript(AllarmeScript script) {
    this.script = script;
  }

  public java.lang.Integer getStatoPrecedente() {
    return this.statoPrecedente;
  }

  public void setStatoPrecedente(java.lang.Integer statoPrecedente) {
    this.statoPrecedente = statoPrecedente;
  }

  public java.lang.Integer getStato() {
    return this.stato;
  }

  public void setStato(java.lang.Integer stato) {
    this.stato = stato;
  }

  public java.lang.String getDettaglioStato() {
    return this.dettaglioStato;
  }

  public void setDettaglioStato(java.lang.String dettaglioStato) {
    this.dettaglioStato = dettaglioStato;
  }

  public java.util.Date getLasttimestampCreate() {
    return this.lasttimestampCreate;
  }

  public void setLasttimestampCreate(java.util.Date lasttimestampCreate) {
    this.lasttimestampCreate = lasttimestampCreate;
  }

  public java.util.Date getLasttimestampUpdate() {
    return this.lasttimestampUpdate;
  }

  public void setLasttimestampUpdate(java.util.Date lasttimestampUpdate) {
    this.lasttimestampUpdate = lasttimestampUpdate;
  }

  public java.lang.Integer getEnabled() {
    return this.enabled;
  }

  public void setEnabled(java.lang.Integer enabled) {
    this.enabled = enabled;
  }

  public java.lang.Integer getAcknowledged() {
    return this.acknowledged;
  }

  public void setAcknowledged(java.lang.Integer acknowledged) {
    this.acknowledged = acknowledged;
  }

  public java.lang.String getTipoPeriodo() {
    return this.tipoPeriodo;
  }

  public void setTipoPeriodo(java.lang.String tipoPeriodo) {
    this.tipoPeriodo = tipoPeriodo;
  }

  public java.lang.Integer getPeriodo() {
    return this.periodo;
  }

  public void setPeriodo(java.lang.Integer periodo) {
    this.periodo = periodo;
  }

  public AllarmeFiltro getFiltro() {
    return this.filtro;
  }

  public void setFiltro(AllarmeFiltro filtro) {
    this.filtro = filtro;
  }

  public AllarmeRaggruppamento getGroupBy() {
    return this.groupBy;
  }

  public void setGroupBy(AllarmeRaggruppamento groupBy) {
    this.groupBy = groupBy;
  }

  public void addAllarmeParametro(AllarmeParametro allarmeParametro) {
    this.allarmeParametro.add(allarmeParametro);
  }

  public AllarmeParametro getAllarmeParametro(int index) {
    return this.allarmeParametro.get( index );
  }

  public AllarmeParametro removeAllarmeParametro(int index) {
    return this.allarmeParametro.remove( index );
  }

  public List<AllarmeParametro> getAllarmeParametroList() {
    return this.allarmeParametro;
  }

  public void setAllarmeParametroList(List<AllarmeParametro> allarmeParametro) {
    this.allarmeParametro=allarmeParametro;
  }

  public int sizeAllarmeParametroList() {
    return this.allarmeParametro.size();
  }

  private static final long serialVersionUID = 1L;

  @XmlTransient
  private Long id;

  private static org.openspcoop2.core.allarmi.model.AllarmeModel modelStaticInstance = null;
  private static synchronized void initModelStaticInstance(){
	  if(org.openspcoop2.core.allarmi.Allarme.modelStaticInstance==null){
  			org.openspcoop2.core.allarmi.Allarme.modelStaticInstance = new org.openspcoop2.core.allarmi.model.AllarmeModel();
	  }
  }
  public static org.openspcoop2.core.allarmi.model.AllarmeModel model(){
	  if(org.openspcoop2.core.allarmi.Allarme.modelStaticInstance==null){
	  		initModelStaticInstance();
	  }
	  return org.openspcoop2.core.allarmi.Allarme.modelStaticInstance;
  }


  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="nome",required=true,nillable=false)
  protected java.lang.String nome;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="tipo",required=true,nillable=false)
  protected java.lang.String tipo;

  @javax.xml.bind.annotation.XmlTransient
  protected java.lang.String _value_tipoAllarme;

  @XmlElement(name="tipo-allarme",required=true,nillable=false,defaultValue="ATTIVO")
  protected TipoAllarme tipoAllarme = (TipoAllarme) TipoAllarme.toEnumConstantFromString("ATTIVO");

  @XmlElement(name="mail",required=true,nillable=false)
  protected AllarmeMail mail;

  @XmlElement(name="script",required=true,nillable=false)
  protected AllarmeScript script;

  @javax.xml.bind.annotation.XmlSchemaType(name="integer")
  @XmlElement(name="stato-precedente",required=true,nillable=false)
  protected java.lang.Integer statoPrecedente;

  @javax.xml.bind.annotation.XmlSchemaType(name="integer")
  @XmlElement(name="stato",required=true,nillable=false)
  protected java.lang.Integer stato;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="dettaglio-stato",required=false,nillable=false)
  protected java.lang.String dettaglioStato;

  @javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(org.openspcoop2.utils.jaxb.DateTime2String.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="dateTime")
  @XmlElement(name="lasttimestamp-create",required=true,nillable=false,type=java.lang.String.class)
  protected java.util.Date lasttimestampCreate;

  @javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(org.openspcoop2.utils.jaxb.DateTime2String.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="dateTime")
  @XmlElement(name="lasttimestamp-update",required=false,nillable=false,type=java.lang.String.class)
  protected java.util.Date lasttimestampUpdate;

  @javax.xml.bind.annotation.XmlSchemaType(name="integer")
  @XmlElement(name="enabled",required=true,nillable=false)
  protected java.lang.Integer enabled;

  @javax.xml.bind.annotation.XmlSchemaType(name="integer")
  @XmlElement(name="acknowledged",required=true,nillable=false)
  protected java.lang.Integer acknowledged;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="tipo-periodo",required=false,nillable=false)
  protected java.lang.String tipoPeriodo;

  @javax.xml.bind.annotation.XmlSchemaType(name="integer")
  @XmlElement(name="periodo",required=false,nillable=false)
  protected java.lang.Integer periodo;

  @XmlElement(name="filtro",required=true,nillable=false)
  protected AllarmeFiltro filtro;

  @XmlElement(name="group-by",required=true,nillable=false)
  protected AllarmeRaggruppamento groupBy;

  @XmlElement(name="allarme-parametro",required=true,nillable=false)
  protected List<AllarmeParametro> allarmeParametro = new ArrayList<AllarmeParametro>();

  /**
   * @deprecated Use method getAllarmeParametroList
   * @return List&lt;AllarmeParametro&gt;
  */
  @Deprecated
  public List<AllarmeParametro> getAllarmeParametro() {
  	return this.allarmeParametro;
  }

  /**
   * @deprecated Use method setAllarmeParametroList
   * @param allarmeParametro List&lt;AllarmeParametro&gt;
  */
  @Deprecated
  public void setAllarmeParametro(List<AllarmeParametro> allarmeParametro) {
  	this.allarmeParametro=allarmeParametro;
  }

  /**
   * @deprecated Use method sizeAllarmeParametroList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizeAllarmeParametro() {
  	return this.allarmeParametro.size();
  }

}
