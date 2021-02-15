/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it).
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


/** <p>Java class for allarme-notifica complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="allarme-notifica"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="data-notifica" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="id-allarme" type="{http://www.openspcoop2.org/core/allarmi}id-allarme" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="old-stato" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="old-dettaglio-stato" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="nuovo-stato" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="nuovo-dettaglio-stato" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="history-entry" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/&gt;
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
@XmlType(name = "allarme-notifica", 
  propOrder = {
  	"dataNotifica",
  	"idAllarme",
  	"oldStato",
  	"oldDettaglioStato",
  	"nuovoStato",
  	"nuovoDettaglioStato",
  	"historyEntry"
  }
)

@XmlRootElement(name = "allarme-notifica")

public class AllarmeNotifica extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public AllarmeNotifica() {
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

  public java.util.Date getDataNotifica() {
    return this.dataNotifica;
  }

  public void setDataNotifica(java.util.Date dataNotifica) {
    this.dataNotifica = dataNotifica;
  }

  public IdAllarme getIdAllarme() {
    return this.idAllarme;
  }

  public void setIdAllarme(IdAllarme idAllarme) {
    this.idAllarme = idAllarme;
  }

  public java.lang.Integer getOldStato() {
    return this.oldStato;
  }

  public void setOldStato(java.lang.Integer oldStato) {
    this.oldStato = oldStato;
  }

  public java.lang.String getOldDettaglioStato() {
    return this.oldDettaglioStato;
  }

  public void setOldDettaglioStato(java.lang.String oldDettaglioStato) {
    this.oldDettaglioStato = oldDettaglioStato;
  }

  public java.lang.Integer getNuovoStato() {
    return this.nuovoStato;
  }

  public void setNuovoStato(java.lang.Integer nuovoStato) {
    this.nuovoStato = nuovoStato;
  }

  public java.lang.String getNuovoDettaglioStato() {
    return this.nuovoDettaglioStato;
  }

  public void setNuovoDettaglioStato(java.lang.String nuovoDettaglioStato) {
    this.nuovoDettaglioStato = nuovoDettaglioStato;
  }

  public java.lang.String getHistoryEntry() {
    return this.historyEntry;
  }

  public void setHistoryEntry(java.lang.String historyEntry) {
    this.historyEntry = historyEntry;
  }

  private static final long serialVersionUID = 1L;

  @XmlTransient
  private Long id;

  private static org.openspcoop2.core.allarmi.model.AllarmeNotificaModel modelStaticInstance = null;
  private static synchronized void initModelStaticInstance(){
	  if(org.openspcoop2.core.allarmi.AllarmeNotifica.modelStaticInstance==null){
  			org.openspcoop2.core.allarmi.AllarmeNotifica.modelStaticInstance = new org.openspcoop2.core.allarmi.model.AllarmeNotificaModel();
	  }
  }
  public static org.openspcoop2.core.allarmi.model.AllarmeNotificaModel model(){
	  if(org.openspcoop2.core.allarmi.AllarmeNotifica.modelStaticInstance==null){
	  		initModelStaticInstance();
	  }
	  return org.openspcoop2.core.allarmi.AllarmeNotifica.modelStaticInstance;
  }


  @javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(org.openspcoop2.utils.jaxb.DateTime2String.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="dateTime")
  @XmlElement(name="data-notifica",required=true,nillable=false,type=java.lang.String.class)
  protected java.util.Date dataNotifica;

  @XmlElement(name="id-allarme",required=true,nillable=false)
  protected IdAllarme idAllarme;

  @javax.xml.bind.annotation.XmlSchemaType(name="integer")
  @XmlElement(name="old-stato",required=true,nillable=false)
  protected java.lang.Integer oldStato;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="old-dettaglio-stato",required=false,nillable=false)
  protected java.lang.String oldDettaglioStato;

  @javax.xml.bind.annotation.XmlSchemaType(name="integer")
  @XmlElement(name="nuovo-stato",required=true,nillable=false)
  protected java.lang.Integer nuovoStato;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="nuovo-dettaglio-stato",required=false,nillable=false)
  protected java.lang.String nuovoDettaglioStato;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="history-entry",required=false,nillable=false)
  protected java.lang.String historyEntry;

}
