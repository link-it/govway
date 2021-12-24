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
package org.openspcoop2.core.eventi;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;


/** <p>Java class for evento complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="evento"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="tipo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="codice" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="severita" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="ora-registrazione" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="descrizione" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="id-transazione" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="id-configurazione" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="configurazione" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="cluster-id" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/&gt;
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
@XmlType(name = "evento", 
  propOrder = {
  	"tipo",
  	"codice",
  	"severita",
  	"oraRegistrazione",
  	"descrizione",
  	"idTransazione",
  	"idConfigurazione",
  	"configurazione",
  	"clusterId"
  }
)

@XmlRootElement(name = "evento")

public class Evento extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public Evento() {
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

  public java.lang.String getTipo() {
    return this.tipo;
  }

  public void setTipo(java.lang.String tipo) {
    this.tipo = tipo;
  }

  public java.lang.String getCodice() {
    return this.codice;
  }

  public void setCodice(java.lang.String codice) {
    this.codice = codice;
  }

  public int getSeverita() {
    return this.severita;
  }

  public void setSeverita(int severita) {
    this.severita = severita;
  }

  public java.util.Date getOraRegistrazione() {
    return this.oraRegistrazione;
  }

  public void setOraRegistrazione(java.util.Date oraRegistrazione) {
    this.oraRegistrazione = oraRegistrazione;
  }

  public java.lang.String getDescrizione() {
    return this.descrizione;
  }

  public void setDescrizione(java.lang.String descrizione) {
    this.descrizione = descrizione;
  }

  public java.lang.String getIdTransazione() {
    return this.idTransazione;
  }

  public void setIdTransazione(java.lang.String idTransazione) {
    this.idTransazione = idTransazione;
  }

  public java.lang.String getIdConfigurazione() {
    return this.idConfigurazione;
  }

  public void setIdConfigurazione(java.lang.String idConfigurazione) {
    this.idConfigurazione = idConfigurazione;
  }

  public java.lang.String getConfigurazione() {
    return this.configurazione;
  }

  public void setConfigurazione(java.lang.String configurazione) {
    this.configurazione = configurazione;
  }

  public java.lang.String getClusterId() {
    return this.clusterId;
  }

  public void setClusterId(java.lang.String clusterId) {
    this.clusterId = clusterId;
  }

  private static final long serialVersionUID = 1L;

  @XmlTransient
  private Long id;

  private static org.openspcoop2.core.eventi.model.EventoModel modelStaticInstance = null;
  private static synchronized void initModelStaticInstance(){
	  if(org.openspcoop2.core.eventi.Evento.modelStaticInstance==null){
  			org.openspcoop2.core.eventi.Evento.modelStaticInstance = new org.openspcoop2.core.eventi.model.EventoModel();
	  }
  }
  public static org.openspcoop2.core.eventi.model.EventoModel model(){
	  if(org.openspcoop2.core.eventi.Evento.modelStaticInstance==null){
	  		initModelStaticInstance();
	  }
	  return org.openspcoop2.core.eventi.Evento.modelStaticInstance;
  }


  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="tipo",required=true,nillable=false)
  protected java.lang.String tipo;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="codice",required=true,nillable=false)
  protected java.lang.String codice;

  @javax.xml.bind.annotation.XmlSchemaType(name="int")
  @XmlElement(name="severita",required=true,nillable=false)
  protected int severita;

  @javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(org.openspcoop2.utils.jaxb.DateTime2String.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="dateTime")
  @XmlElement(name="ora-registrazione",required=true,nillable=false,type=java.lang.String.class)
  protected java.util.Date oraRegistrazione;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="descrizione",required=false,nillable=false)
  protected java.lang.String descrizione;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="id-transazione",required=false,nillable=false)
  protected java.lang.String idTransazione;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="id-configurazione",required=false,nillable=false)
  protected java.lang.String idConfigurazione;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="configurazione",required=false,nillable=false)
  protected java.lang.String configurazione;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="cluster-id",required=false,nillable=false)
  protected java.lang.String clusterId;

}
