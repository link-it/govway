/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it).
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
package org.openspcoop2.pdd.monitor;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.openspcoop2.pdd.monitor.constants.StatoMessaggio;
import java.io.Serializable;


/** <p>Java class for messaggio complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="messaggio"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="dettaglio" type="{http://www.openspcoop2.org/pdd/monitor}dettaglio" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="busta-info" type="{http://www.openspcoop2.org/pdd/monitor}busta" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="id-messaggio" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="ora-attuale" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="ora-registrazione" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="stato" type="{http://www.openspcoop2.org/pdd/monitor}StatoMessaggio" minOccurs="1" maxOccurs="1"/&gt;
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
@XmlType(name = "messaggio", 
  propOrder = {
  	"dettaglio",
  	"bustaInfo",
  	"idMessaggio",
  	"oraAttuale",
  	"oraRegistrazione",
  	"stato"
  }
)

@XmlRootElement(name = "messaggio")

public class Messaggio extends org.openspcoop2.utils.beans.BaseBeanWithId implements Serializable , Cloneable {
  public Messaggio() {
    super();
  }

  public Dettaglio getDettaglio() {
    return this.dettaglio;
  }

  public void setDettaglio(Dettaglio dettaglio) {
    this.dettaglio = dettaglio;
  }

  public Busta getBustaInfo() {
    return this.bustaInfo;
  }

  public void setBustaInfo(Busta bustaInfo) {
    this.bustaInfo = bustaInfo;
  }

  public java.lang.String getIdMessaggio() {
    return this.idMessaggio;
  }

  public void setIdMessaggio(java.lang.String idMessaggio) {
    this.idMessaggio = idMessaggio;
  }

  public java.util.Date getOraAttuale() {
    return this.oraAttuale;
  }

  public void setOraAttuale(java.util.Date oraAttuale) {
    this.oraAttuale = oraAttuale;
  }

  public java.util.Date getOraRegistrazione() {
    return this.oraRegistrazione;
  }

  public void setOraRegistrazione(java.util.Date oraRegistrazione) {
    this.oraRegistrazione = oraRegistrazione;
  }

  public void setStatoRawEnumValue(String value) {
    this.stato = (StatoMessaggio) StatoMessaggio.toEnumConstantFromString(value);
  }

  public String getStatoRawEnumValue() {
    if(this.stato == null){
    	return null;
    }else{
    	return this.stato.toString();
    }
  }

  public org.openspcoop2.pdd.monitor.constants.StatoMessaggio getStato() {
    return this.stato;
  }

  public void setStato(org.openspcoop2.pdd.monitor.constants.StatoMessaggio stato) {
    this.stato = stato;
  }

  public Filtro getFiltro() {
    return this.filtro;
  }

  public void setFiltro(Filtro filtro) {
    this.filtro = filtro;
  }

  private static final long serialVersionUID = 1L;

  private static org.openspcoop2.pdd.monitor.model.MessaggioModel modelStaticInstance = null;
  private static synchronized void initModelStaticInstance(){
	  if(org.openspcoop2.pdd.monitor.Messaggio.modelStaticInstance==null){
  			org.openspcoop2.pdd.monitor.Messaggio.modelStaticInstance = new org.openspcoop2.pdd.monitor.model.MessaggioModel();
	  }
  }
  public static org.openspcoop2.pdd.monitor.model.MessaggioModel model(){
	  if(org.openspcoop2.pdd.monitor.Messaggio.modelStaticInstance==null){
	  		initModelStaticInstance();
	  }
	  return org.openspcoop2.pdd.monitor.Messaggio.modelStaticInstance;
  }


  @XmlElement(name="dettaglio",required=true,nillable=false)
  protected Dettaglio dettaglio;

  @XmlElement(name="busta-info",required=true,nillable=false)
  protected Busta bustaInfo;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="id-messaggio",required=true,nillable=false)
  protected java.lang.String idMessaggio;

  @javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(org.openspcoop2.utils.jaxb.DateTime2String.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="dateTime")
  @XmlElement(name="ora-attuale",required=true,nillable=false,type=java.lang.String.class)
  protected java.util.Date oraAttuale;

  @javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(org.openspcoop2.utils.jaxb.DateTime2String.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="dateTime")
  @XmlElement(name="ora-registrazione",required=true,nillable=false,type=java.lang.String.class)
  protected java.util.Date oraRegistrazione;

  @javax.xml.bind.annotation.XmlTransient
  protected java.lang.String statoRawEnumValue;

  @XmlElement(name="stato",required=true,nillable=false)
  protected StatoMessaggio stato;

  @javax.xml.bind.annotation.XmlTransient
  protected Filtro filtro;

}
