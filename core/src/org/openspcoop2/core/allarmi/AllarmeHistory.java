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
import java.io.Serializable;


/** <p>Java class for allarme-history complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="allarme-history"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="id-allarme" type="{http://www.openspcoop2.org/core/allarmi}id-allarme" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="enabled" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="stato" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="dettaglio-stato" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="acknowledged" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="timestamp-update" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="utente" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/&gt;
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
@XmlType(name = "allarme-history", 
  propOrder = {
  	"idAllarme",
  	"enabled",
  	"stato",
  	"dettaglioStato",
  	"acknowledged",
  	"timestampUpdate",
  	"utente"
  }
)

@XmlRootElement(name = "allarme-history")

public class AllarmeHistory extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public AllarmeHistory() {
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

  public IdAllarme getIdAllarme() {
    return this.idAllarme;
  }

  public void setIdAllarme(IdAllarme idAllarme) {
    this.idAllarme = idAllarme;
  }

  public java.lang.Integer getEnabled() {
    return this.enabled;
  }

  public void setEnabled(java.lang.Integer enabled) {
    this.enabled = enabled;
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

  public java.lang.Integer getAcknowledged() {
    return this.acknowledged;
  }

  public void setAcknowledged(java.lang.Integer acknowledged) {
    this.acknowledged = acknowledged;
  }

  public java.util.Date getTimestampUpdate() {
    return this.timestampUpdate;
  }

  public void setTimestampUpdate(java.util.Date timestampUpdate) {
    this.timestampUpdate = timestampUpdate;
  }

  public java.lang.String getUtente() {
    return this.utente;
  }

  public void setUtente(java.lang.String utente) {
    this.utente = utente;
  }

  private static final long serialVersionUID = 1L;

  @XmlTransient
  private Long id;

  private static org.openspcoop2.core.allarmi.model.AllarmeHistoryModel modelStaticInstance = null;
  private static synchronized void initModelStaticInstance(){
	  if(org.openspcoop2.core.allarmi.AllarmeHistory.modelStaticInstance==null){
  			org.openspcoop2.core.allarmi.AllarmeHistory.modelStaticInstance = new org.openspcoop2.core.allarmi.model.AllarmeHistoryModel();
	  }
  }
  public static org.openspcoop2.core.allarmi.model.AllarmeHistoryModel model(){
	  if(org.openspcoop2.core.allarmi.AllarmeHistory.modelStaticInstance==null){
	  		initModelStaticInstance();
	  }
	  return org.openspcoop2.core.allarmi.AllarmeHistory.modelStaticInstance;
  }


  @XmlElement(name="id-allarme",required=true,nillable=false)
  protected IdAllarme idAllarme;

  @javax.xml.bind.annotation.XmlSchemaType(name="integer")
  @XmlElement(name="enabled",required=true,nillable=false)
  protected java.lang.Integer enabled;

  @javax.xml.bind.annotation.XmlSchemaType(name="integer")
  @XmlElement(name="stato",required=true,nillable=false)
  protected java.lang.Integer stato;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="dettaglio-stato",required=false,nillable=false)
  protected java.lang.String dettaglioStato;

  @javax.xml.bind.annotation.XmlSchemaType(name="integer")
  @XmlElement(name="acknowledged",required=true,nillable=false)
  protected java.lang.Integer acknowledged;

  @javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(org.openspcoop2.utils.jaxb.DateTime2String.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="dateTime")
  @XmlElement(name="timestamp-update",required=true,nillable=false,type=java.lang.String.class)
  protected java.util.Date timestampUpdate;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="utente",required=false,nillable=false)
  protected java.lang.String utente;

}
