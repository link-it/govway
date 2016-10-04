/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2016 Link.it srl (http://link.it).
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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
package org.openspcoop2.pdd.monitor;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;


/** <p>Java class for stato-pdd complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="stato-pdd">
 * 		&lt;sequence>
 * 			&lt;element name="num-msg-in-consegna" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="1" maxOccurs="1" default="0"/>
 * 			&lt;element name="tempo-medio-attesa-in-consegna" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="1" maxOccurs="1" default="-1"/>
 * 			&lt;element name="tempo-max-attesa-in-consegna" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="1" maxOccurs="1" default="-1"/>
 * 			&lt;element name="num-msg-in-spedizione" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="1" maxOccurs="1" default="0"/>
 * 			&lt;element name="tempo-medio-attesa-in-spedizione" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="1" maxOccurs="1" default="-1"/>
 * 			&lt;element name="tempo-max-attesa-in-spedizione" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="1" maxOccurs="1" default="-1"/>
 * 			&lt;element name="num-msg-in-processamento" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="1" maxOccurs="1" default="0"/>
 * 			&lt;element name="tempo-medio-attesa-in-processamento" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="1" maxOccurs="1" default="-1"/>
 * 			&lt;element name="tempo-max-attesa-in-processamento" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="1" maxOccurs="1" default="-1"/>
 * 			&lt;element name="tot-messaggi" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="1" maxOccurs="1" default="0"/>
 * 			&lt;element name="tempo-medio-attesa" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="1" maxOccurs="1" default="-1"/>
 * 			&lt;element name="tempo-max-attesa" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="1" maxOccurs="1" default="-1"/>
 * 			&lt;element name="tot-messaggi-duplicati" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="1" maxOccurs="1" default="0"/>
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
@XmlType(name = "stato-pdd", 
  propOrder = {
  	"numMsgInConsegna",
  	"tempoMedioAttesaInConsegna",
  	"tempoMaxAttesaInConsegna",
  	"numMsgInSpedizione",
  	"tempoMedioAttesaInSpedizione",
  	"tempoMaxAttesaInSpedizione",
  	"numMsgInProcessamento",
  	"tempoMedioAttesaInProcessamento",
  	"tempoMaxAttesaInProcessamento",
  	"totMessaggi",
  	"tempoMedioAttesa",
  	"tempoMaxAttesa",
  	"totMessaggiDuplicati"
  }
)

@XmlRootElement(name = "stato-pdd")

public class StatoPdd extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public StatoPdd() {
  }

  public Long getId() {
    if(this.id!=null)
		return this.id;
	else
		return new Long(-1);
  }

  public void setId(Long id) {
    if(id!=null)
		this.id=id;
	else
		this.id=new Long(-1);
  }

  public long getNumMsgInConsegna() {
    return this.numMsgInConsegna;
  }

  public void setNumMsgInConsegna(long numMsgInConsegna) {
    this.numMsgInConsegna = numMsgInConsegna;
  }

  public long getTempoMedioAttesaInConsegna() {
    return this.tempoMedioAttesaInConsegna;
  }

  public void setTempoMedioAttesaInConsegna(long tempoMedioAttesaInConsegna) {
    this.tempoMedioAttesaInConsegna = tempoMedioAttesaInConsegna;
  }

  public long getTempoMaxAttesaInConsegna() {
    return this.tempoMaxAttesaInConsegna;
  }

  public void setTempoMaxAttesaInConsegna(long tempoMaxAttesaInConsegna) {
    this.tempoMaxAttesaInConsegna = tempoMaxAttesaInConsegna;
  }

  public long getNumMsgInSpedizione() {
    return this.numMsgInSpedizione;
  }

  public void setNumMsgInSpedizione(long numMsgInSpedizione) {
    this.numMsgInSpedizione = numMsgInSpedizione;
  }

  public long getTempoMedioAttesaInSpedizione() {
    return this.tempoMedioAttesaInSpedizione;
  }

  public void setTempoMedioAttesaInSpedizione(long tempoMedioAttesaInSpedizione) {
    this.tempoMedioAttesaInSpedizione = tempoMedioAttesaInSpedizione;
  }

  public long getTempoMaxAttesaInSpedizione() {
    return this.tempoMaxAttesaInSpedizione;
  }

  public void setTempoMaxAttesaInSpedizione(long tempoMaxAttesaInSpedizione) {
    this.tempoMaxAttesaInSpedizione = tempoMaxAttesaInSpedizione;
  }

  public long getNumMsgInProcessamento() {
    return this.numMsgInProcessamento;
  }

  public void setNumMsgInProcessamento(long numMsgInProcessamento) {
    this.numMsgInProcessamento = numMsgInProcessamento;
  }

  public long getTempoMedioAttesaInProcessamento() {
    return this.tempoMedioAttesaInProcessamento;
  }

  public void setTempoMedioAttesaInProcessamento(long tempoMedioAttesaInProcessamento) {
    this.tempoMedioAttesaInProcessamento = tempoMedioAttesaInProcessamento;
  }

  public long getTempoMaxAttesaInProcessamento() {
    return this.tempoMaxAttesaInProcessamento;
  }

  public void setTempoMaxAttesaInProcessamento(long tempoMaxAttesaInProcessamento) {
    this.tempoMaxAttesaInProcessamento = tempoMaxAttesaInProcessamento;
  }

  public long getTotMessaggi() {
    return this.totMessaggi;
  }

  public void setTotMessaggi(long totMessaggi) {
    this.totMessaggi = totMessaggi;
  }

  public long getTempoMedioAttesa() {
    return this.tempoMedioAttesa;
  }

  public void setTempoMedioAttesa(long tempoMedioAttesa) {
    this.tempoMedioAttesa = tempoMedioAttesa;
  }

  public long getTempoMaxAttesa() {
    return this.tempoMaxAttesa;
  }

  public void setTempoMaxAttesa(long tempoMaxAttesa) {
    this.tempoMaxAttesa = tempoMaxAttesa;
  }

  public long getTotMessaggiDuplicati() {
    return this.totMessaggiDuplicati;
  }

  public void setTotMessaggiDuplicati(long totMessaggiDuplicati) {
    this.totMessaggiDuplicati = totMessaggiDuplicati;
  }

  public Filtro getFiltro() {
    return this.filtro;
  }

  public void setFiltro(Filtro filtro) {
    this.filtro = filtro;
  }

  private static final long serialVersionUID = 1L;

  @XmlTransient
  private Long id;

  private static org.openspcoop2.pdd.monitor.model.StatoPddModel modelStaticInstance = null;
  private static synchronized void initModelStaticInstance(){
	  if(org.openspcoop2.pdd.monitor.StatoPdd.modelStaticInstance==null){
  			org.openspcoop2.pdd.monitor.StatoPdd.modelStaticInstance = new org.openspcoop2.pdd.monitor.model.StatoPddModel();
	  }
  }
  public static org.openspcoop2.pdd.monitor.model.StatoPddModel model(){
	  if(org.openspcoop2.pdd.monitor.StatoPdd.modelStaticInstance==null){
	  		initModelStaticInstance();
	  }
	  return org.openspcoop2.pdd.monitor.StatoPdd.modelStaticInstance;
  }


  @javax.xml.bind.annotation.XmlSchemaType(name="long")
  @XmlElement(name="num-msg-in-consegna",required=true,nillable=false,defaultValue="0")
  protected long numMsgInConsegna = 0l;

  @javax.xml.bind.annotation.XmlSchemaType(name="long")
  @XmlElement(name="tempo-medio-attesa-in-consegna",required=true,nillable=false,defaultValue="-1")
  protected long tempoMedioAttesaInConsegna = -1l;

  @javax.xml.bind.annotation.XmlSchemaType(name="long")
  @XmlElement(name="tempo-max-attesa-in-consegna",required=true,nillable=false,defaultValue="-1")
  protected long tempoMaxAttesaInConsegna = -1l;

  @javax.xml.bind.annotation.XmlSchemaType(name="long")
  @XmlElement(name="num-msg-in-spedizione",required=true,nillable=false,defaultValue="0")
  protected long numMsgInSpedizione = 0l;

  @javax.xml.bind.annotation.XmlSchemaType(name="long")
  @XmlElement(name="tempo-medio-attesa-in-spedizione",required=true,nillable=false,defaultValue="-1")
  protected long tempoMedioAttesaInSpedizione = -1l;

  @javax.xml.bind.annotation.XmlSchemaType(name="long")
  @XmlElement(name="tempo-max-attesa-in-spedizione",required=true,nillable=false,defaultValue="-1")
  protected long tempoMaxAttesaInSpedizione = -1l;

  @javax.xml.bind.annotation.XmlSchemaType(name="long")
  @XmlElement(name="num-msg-in-processamento",required=true,nillable=false,defaultValue="0")
  protected long numMsgInProcessamento = 0l;

  @javax.xml.bind.annotation.XmlSchemaType(name="long")
  @XmlElement(name="tempo-medio-attesa-in-processamento",required=true,nillable=false,defaultValue="-1")
  protected long tempoMedioAttesaInProcessamento = -1l;

  @javax.xml.bind.annotation.XmlSchemaType(name="long")
  @XmlElement(name="tempo-max-attesa-in-processamento",required=true,nillable=false,defaultValue="-1")
  protected long tempoMaxAttesaInProcessamento = -1l;

  @javax.xml.bind.annotation.XmlSchemaType(name="long")
  @XmlElement(name="tot-messaggi",required=true,nillable=false,defaultValue="0")
  protected long totMessaggi = 0l;

  @javax.xml.bind.annotation.XmlSchemaType(name="long")
  @XmlElement(name="tempo-medio-attesa",required=true,nillable=false,defaultValue="-1")
  protected long tempoMedioAttesa = -1l;

  @javax.xml.bind.annotation.XmlSchemaType(name="long")
  @XmlElement(name="tempo-max-attesa",required=true,nillable=false,defaultValue="-1")
  protected long tempoMaxAttesa = -1l;

  @javax.xml.bind.annotation.XmlSchemaType(name="long")
  @XmlElement(name="tot-messaggi-duplicati",required=true,nillable=false,defaultValue="0")
  protected long totMessaggiDuplicati = 0l;

  @javax.xml.bind.annotation.XmlTransient
  protected Filtro filtro;

}
