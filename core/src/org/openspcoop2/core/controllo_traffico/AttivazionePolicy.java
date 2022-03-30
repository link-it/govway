/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it).
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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;


/** <p>Java class for attivazione-policy complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="attivazione-policy"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="id-active-policy" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="alias" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="update-time" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="posizione" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="continua-valutazione" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="1" maxOccurs="1" default="false"/&gt;
 * 			&lt;element name="id-policy" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="enabled" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="warning-only" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="1" maxOccurs="1" default="false"/&gt;
 * 			&lt;element name="ridefinisci" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="valore" type="{http://www.w3.org/2001/XMLSchema}unsignedLong" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="valore2" type="{http://www.w3.org/2001/XMLSchema}unsignedLong" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="filtro" type="{http://www.openspcoop2.org/core/controllo_traffico}attivazione-policy-filtro" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="group-by" type="{http://www.openspcoop2.org/core/controllo_traffico}attivazione-policy-raggruppamento" minOccurs="1" maxOccurs="1"/&gt;
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
@XmlType(name = "attivazione-policy", 
  propOrder = {
  	"idActivePolicy",
  	"alias",
  	"updateTime",
  	"posizione",
  	"continuaValutazione",
  	"idPolicy",
  	"enabled",
  	"warningOnly",
  	"ridefinisci",
  	"valore",
  	"valore2",
  	"filtro",
  	"groupBy"
  }
)

@XmlRootElement(name = "attivazione-policy")

public class AttivazionePolicy extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public AttivazionePolicy() {
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

  public IdActivePolicy getOldIdActivePolicy() {
    return this.oldIdActivePolicy;
  }

  public void setOldIdActivePolicy(IdActivePolicy oldIdActivePolicy) {
    this.oldIdActivePolicy=oldIdActivePolicy;
  }

  public java.lang.String getIdActivePolicy() {
    return this.idActivePolicy;
  }

  public void setIdActivePolicy(java.lang.String idActivePolicy) {
    this.idActivePolicy = idActivePolicy;
  }

  public java.lang.String getAlias() {
    return this.alias;
  }

  public void setAlias(java.lang.String alias) {
    this.alias = alias;
  }

  public java.util.Date getUpdateTime() {
    return this.updateTime;
  }

  public void setUpdateTime(java.util.Date updateTime) {
    this.updateTime = updateTime;
  }

  public int getPosizione() {
    return this.posizione;
  }

  public void setPosizione(int posizione) {
    this.posizione = posizione;
  }

  public boolean isContinuaValutazione() {
    return this.continuaValutazione;
  }

  public boolean getContinuaValutazione() {
    return this.continuaValutazione;
  }

  public void setContinuaValutazione(boolean continuaValutazione) {
    this.continuaValutazione = continuaValutazione;
  }

  public java.lang.String getIdPolicy() {
    return this.idPolicy;
  }

  public void setIdPolicy(java.lang.String idPolicy) {
    this.idPolicy = idPolicy;
  }

  public boolean isEnabled() {
    return this.enabled;
  }

  public boolean getEnabled() {
    return this.enabled;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  public boolean isWarningOnly() {
    return this.warningOnly;
  }

  public boolean getWarningOnly() {
    return this.warningOnly;
  }

  public void setWarningOnly(boolean warningOnly) {
    this.warningOnly = warningOnly;
  }

  public boolean isRidefinisci() {
    return this.ridefinisci;
  }

  public boolean getRidefinisci() {
    return this.ridefinisci;
  }

  public void setRidefinisci(boolean ridefinisci) {
    this.ridefinisci = ridefinisci;
  }

  public java.lang.Long getValore() {
    return this.valore;
  }

  public void setValore(java.lang.Long valore) {
    this.valore = valore;
  }

  public java.lang.Long getValore2() {
    return this.valore2;
  }

  public void setValore2(java.lang.Long valore2) {
    this.valore2 = valore2;
  }

  public AttivazionePolicyFiltro getFiltro() {
    return this.filtro;
  }

  public void setFiltro(AttivazionePolicyFiltro filtro) {
    this.filtro = filtro;
  }

  public AttivazionePolicyRaggruppamento getGroupBy() {
    return this.groupBy;
  }

  public void setGroupBy(AttivazionePolicyRaggruppamento groupBy) {
    this.groupBy = groupBy;
  }

  private static final long serialVersionUID = 1L;

  @XmlTransient
  private Long id;

  private static org.openspcoop2.core.controllo_traffico.model.AttivazionePolicyModel modelStaticInstance = null;
  private static synchronized void initModelStaticInstance(){
	  if(org.openspcoop2.core.controllo_traffico.AttivazionePolicy.modelStaticInstance==null){
  			org.openspcoop2.core.controllo_traffico.AttivazionePolicy.modelStaticInstance = new org.openspcoop2.core.controllo_traffico.model.AttivazionePolicyModel();
	  }
  }
  public static org.openspcoop2.core.controllo_traffico.model.AttivazionePolicyModel model(){
	  if(org.openspcoop2.core.controllo_traffico.AttivazionePolicy.modelStaticInstance==null){
	  		initModelStaticInstance();
	  }
	  return org.openspcoop2.core.controllo_traffico.AttivazionePolicy.modelStaticInstance;
  }


  @javax.xml.bind.annotation.XmlTransient
  protected IdActivePolicy oldIdActivePolicy;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="id-active-policy",required=true,nillable=false)
  protected java.lang.String idActivePolicy;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="alias",required=false,nillable=false)
  protected java.lang.String alias;

  @javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(org.openspcoop2.utils.jaxb.DateTime2String.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="dateTime")
  @XmlElement(name="update-time",required=true,nillable=false,type=java.lang.String.class)
  protected java.util.Date updateTime;

  @javax.xml.bind.annotation.XmlSchemaType(name="int")
  @XmlElement(name="posizione",required=true,nillable=false)
  protected int posizione;

  @javax.xml.bind.annotation.XmlSchemaType(name="boolean")
  @XmlElement(name="continua-valutazione",required=true,nillable=false,defaultValue="false")
  protected boolean continuaValutazione = false;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="id-policy",required=true,nillable=false)
  protected java.lang.String idPolicy;

  @javax.xml.bind.annotation.XmlSchemaType(name="boolean")
  @XmlElement(name="enabled",required=true,nillable=false)
  protected boolean enabled;

  @javax.xml.bind.annotation.XmlSchemaType(name="boolean")
  @XmlElement(name="warning-only",required=true,nillable=false,defaultValue="false")
  protected boolean warningOnly = false;

  @javax.xml.bind.annotation.XmlSchemaType(name="boolean")
  @XmlElement(name="ridefinisci",required=true,nillable=false)
  protected boolean ridefinisci;

  @javax.xml.bind.annotation.XmlSchemaType(name="unsignedLong")
  @XmlElement(name="valore",required=false,nillable=false)
  protected java.lang.Long valore;

  @javax.xml.bind.annotation.XmlSchemaType(name="unsignedLong")
  @XmlElement(name="valore2",required=false,nillable=false)
  protected java.lang.Long valore2;

  @XmlElement(name="filtro",required=true,nillable=false)
  protected AttivazionePolicyFiltro filtro;

  @XmlElement(name="group-by",required=true,nillable=false)
  protected AttivazionePolicyRaggruppamento groupBy;

}
